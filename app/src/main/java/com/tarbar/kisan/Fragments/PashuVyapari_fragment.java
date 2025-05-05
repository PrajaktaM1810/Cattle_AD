package com.tarbar.kisan.Fragments;

import static android.view.View.GONE;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.tarbar.kisan.Activities.LoadFilterFragments;
import com.tarbar.kisan.Activities.MainActivity;
import com.tarbar.kisan.Adapter.DateRangeAdapter;
import com.tarbar.kisan.Adapter.PashuVyapari_Adapter;
import com.tarbar.kisan.Helper.ApiUtils;
import com.tarbar.kisan.Helper.Iconstant;
import com.tarbar.kisan.Helper.SharedPreferenceManager;
import com.tarbar.kisan.Helper.constant;
import com.tarbar.kisan.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
public class PashuVyapari_fragment extends Fragment {
    private SharedPreferenceManager sharedPrefMgr;
    ProgressDialog dialog;
    String mobileNumber;
    TextView txt_all, txt_cow, txt_buffalo, totalVyapari, id_data_not_found,dateRange;
    RecyclerView recyclerView;
    PashuVyapari_Adapter adapter;
    List<Map<String, String>> data = new ArrayList<>();
    List<Map<String, String>> filteredData = new ArrayList<>();
    List<Map<String, String>> filteredSearchData = new ArrayList<>();
    List<Map<String, String>> activeList;
    String strkisanNumberFromApi;
    boolean isAllSelected = false, isCowSelected = false, isBuffaloSelected = false, bySearch = false;
    String MobileNumber, PashuNumber,type;
    private String selectedLayout = "";
    boolean isMobileMatched = false;
    String fromDate, toDate;
    AppCompatEditText searchEditText;
    ImageView cancelIcon, filterIcon;
    boolean showHighest = false;
    boolean showLowest = false;
    private LoadFilterFragments loadFilterFragments;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pashu_vyapari, container, false);
        view.setBackgroundColor(Color.WHITE);
        sharedPrefMgr = new SharedPreferenceManager(getContext());
        sharedPrefMgr.connectDB();
        mobileNumber = sharedPrefMgr.getString(Iconstant.mobile);
        sharedPrefMgr.closeDB();
        totalVyapari = view.findViewById(R.id.totalVyapari);
        id_data_not_found = view.findViewById(R.id.id_data_not_found);
        dateRange = view.findViewById(R.id.dateRange);
        searchEditText = view.findViewById(R.id.searchEditText);
        cancelIcon = view.findViewById(R.id.cancelIcon);
        filterIcon = view.findViewById(R.id.filterIcon);

        loadFilterFragments = (LoadFilterFragments) getActivity();

        cancelIcon.setOnClickListener(v -> {
            searchEditText.setText("");
            cancelIcon.setVisibility(View.GONE);
            filterData("");
        });

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                cancelIcon.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
                filterData(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        filterIcon.setOnClickListener(v -> showFilterPopup());

        recyclerView = view.findViewById(R.id.recyclerView);
        adapter = new PashuVyapari_Adapter(data, requireContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        getSellingList(null, null);

        if (loadFilterFragments != null) {

            loadFilterFragments.button3.setVisibility(GONE);

            loadFilterFragments.bottomMenu.setVisibility(GONE);
            loadFilterFragments.swipeRefreshLayout.setOnRefreshListener(() -> {
                getSellingList(null, null);
            });

            loadFilterFragments.back.setOnClickListener(v -> {
                Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.click_animation);
                v.startAnimation(animation);
                Intent i = new Intent(requireContext(), MainActivity.class);
                i.putExtra("SELECT_HOME_FRAGMENT", true);
                startActivity(i);
                requireActivity().finish();
            });

            loadFilterFragments.button1.setOnClickListener(v -> {
                Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.click_animation);
                v.startAnimation(animation);
                changeButtonAndTextColor(loadFilterFragments.button1, loadFilterFragments.btn1txt);
                resetOtherButtonColors(loadFilterFragments.button1);
                showDateRangeDialog();
            });

            loadFilterFragments.button2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.click_animation);
                    v.startAnimation(animation);
                    changeButtonAndTextColor(loadFilterFragments.button2, loadFilterFragments.btn2txt);
                    resetOtherButtonColors(loadFilterFragments.button2);

                }
            });
        }
        return view;
    }

    private void changeButtonAndTextColor(LinearLayout selectedButton, TextView text) {
        selectedButton.setBackgroundColor(getResources().getColor(R.color.LogoBlue));
        ((TextView) text).setTextColor(getResources().getColor(R.color.white));
    }

    private void resetOtherButtonColors(LinearLayout selecteLayout) {
        if (selecteLayout != loadFilterFragments.button1) {
            loadFilterFragments.button1.setBackgroundColor(getResources().getColor(android.R.color.white));
            ((TextView) loadFilterFragments.btn1txt).setTextColor(getResources().getColor(android.R.color.black));
        }
        if (selecteLayout != loadFilterFragments.button2) {
            loadFilterFragments.button2.setBackgroundColor(getResources().getColor(android.R.color.white));
            ((TextView) loadFilterFragments.btn2txt).setTextColor(getResources().getColor(android.R.color.black));
        }
    }

    private void filterData(String query) {
        filteredSearchData.clear();
        if (query.isEmpty()) {
            adapter.updateList(data);
            totalVyapari.setText(getString(R.string.total_vyapari, String.valueOf(data.size())));
            id_data_not_found.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            return;
        }

        for (Map<String, String> item : data) {
            if (containsQuery(item, query)) {
                filteredSearchData.add(item);
            }
        }

        adapter.updateList(filteredSearchData);
        totalVyapari.setText(getString(R.string.total_vyapari, String.valueOf(filteredSearchData.size())));

        if (filteredSearchData.isEmpty()) {
            id_data_not_found.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            id_data_not_found.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private boolean containsQuery(Map<String, String> item, String query) {
        query = query.toLowerCase();
        return item.get("village").toLowerCase().contains(query) ||
                item.get("tahsil").toLowerCase().contains(query) ||
                item.get("jilha").toLowerCase().contains(query) ||
                item.get("state").toLowerCase().contains(query) ||
                item.get("kisanNumber").toLowerCase().contains(query);
    }

    private void showFilterPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.filter_popup, null);
        builder.setView(dialogView);
        TextView searchBtn = dialogView.findViewById(R.id.searchBtn);
        CheckBox highestCheck = dialogView.findViewById(R.id.highestCheck);
        CheckBox lowestCheck = dialogView.findViewById(R.id.lowestCheck);
        AlertDialog dialog = builder.create();
        dialog.show();

        highestCheck.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                lowestCheck.setChecked(false);
            }
        });

        lowestCheck.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                highestCheck.setChecked(false);
            }
        });

        searchBtn.setOnClickListener(v -> {
            if (highestCheck.isChecked()) {
                showHighest = true;
                showLowest = false;
                filterHighestSales();
            } else if (lowestCheck.isChecked()) {
                showHighest = false;
                showLowest = true;
                filterLowestSales();
            }
            dialog.dismiss();
        });
    }

    private void filterHighestSales() {
        if (data.isEmpty()) return;
        List<Map<String, String>> filtered = new ArrayList<>();
        int maxSales = 0;
        for (Map<String, String> item : data) {
            try {
                int sales = Integer.parseInt(item.get("total_sales"));
                if (sales > maxSales) maxSales = sales;
            } catch (NumberFormatException e) {}
        }
        for (Map<String, String> item : data) {
            try {
                int sales = Integer.parseInt(item.get("total_sales"));
                if (sales == maxSales) filtered.add(item);
            } catch (NumberFormatException e) {}
        }
        adapter.updateList(filtered);
        totalVyapari.setText(getString(R.string.total_vyapari, String.valueOf(filtered.size())));
    }

    private void filterLowestSales() {
        if (data.isEmpty()) return;
        List<Map<String, String>> filtered = new ArrayList<>();
        int minSales = Integer.MAX_VALUE;
        for (Map<String, String> item : data) {
            try {
                int sales = Integer.parseInt(item.get("total_sales"));
                if (sales > 0 && sales < minSales) minSales = sales;
            } catch (NumberFormatException e) {}
        }
        if (minSales == Integer.MAX_VALUE) {
            adapter.updateList(new ArrayList<>());
            return;
        }
        for (Map<String, String> item : data) {
            try {
                int sales = Integer.parseInt(item.get("total_sales"));
                if (sales == minSales) filtered.add(item);
            } catch (NumberFormatException e) {}
        }
        adapter.updateList(filtered);
        totalVyapari.setText(getString(R.string.total_vyapari, String.valueOf(filtered.size())));
    }

    public void getSellingList(String fromDate, String toDate) {
        ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage(getString(R.string.getting_data));
        dialog.setCancelable(false);
        dialog.show();
        String formattedFromDate = "";
        String formattedToDate = "";
        boolean hasDates = fromDate != null && !fromDate.isEmpty() && toDate != null && !toDate.isEmpty();
        if (hasDates) {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            try {
                Date from = inputFormat.parse(fromDate);
                Date to = inputFormat.parse(toDate);
                if (from != null && to != null) {
                    formattedFromDate = outputFormat.format(from);
                    formattedToDate = outputFormat.format(to);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            dateRange.setText(formattedFromDate + " - " + formattedToDate);
        } else {
            dateRange.setText("");
        }
        String url = constant.GET_SELLING_REPORT;
        if (fromDate == null || fromDate.isEmpty() || toDate == null || toDate.isEmpty()) {
            url += "?from_date=&to_date=";
        } else {
            url += "?from_date=" + fromDate + "&to_date=" + toDate;
        }
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        StringRequest getRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    bySearch = false;
                    data.clear();
                    filteredData.clear();
                    filteredSearchData.clear();
                    dialog.dismiss();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.optString("status", "");
                        if ("success".equalsIgnoreCase(status)) {
                            recyclerView.setVisibility(View.VISIBLE);
                            id_data_not_found.setVisibility(View.GONE);
                            JSONArray dataArray = jsonObject.getJSONArray("data");
                            for (int i = 0; i < dataArray.length(); i++) {
                                JSONObject obj = dataArray.getJSONObject(i);
                                Map<String, String> info = new HashMap<>();
                                info.put("rank", String.valueOf(i + 1));
                                info.put("userId", obj.optString("userid", ""));
                                info.put("name", obj.optString("name", ""));
                                info.put("father_name", obj.optString("father_name", ""));
                                info.put("surname", obj.optString("surname", ""));
                                info.put("kisanNumber", obj.optString("kisan_number", ""));
                                info.put("caste", obj.optString("caste", ""));
                                info.put("state", obj.optString("state", ""));
                                info.put("jilha", obj.optString("jilha", ""));
                                info.put("tahsil", obj.optString("tahsil", ""));
                                info.put("village", obj.optString("village", ""));
                                info.put("total_sales", obj.optString("total_sales", ""));
                                data.add(info);
                            }
                            adapter.updateList(data);
                            totalVyapari.setText(getString(R.string.total_vyapari, String.valueOf(dataArray.length())));
                        } else {
                            recyclerView.setVisibility(View.GONE);
                            id_data_not_found.setVisibility(View.VISIBLE);
                            totalVyapari.setText(R.string.total_ranks_zero);
                        }
                    } catch (JSONException e) {
                        Toast.makeText(getContext(), R.string.json_parsing_error, Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    dialog.dismiss();
                    handleVolleyError(error);
                });
        getRequest.setRetryPolicy(ApiUtils.DEFAULT_RETRY_POLICY);
        requestQueue.add(getRequest);
    }

    private void handleVolleyError(VolleyError error) {
        if (error.networkResponse != null) {
            Log.e("ErrorResponse", "Status Code: " + error.networkResponse.statusCode);
            Log.e("ErrorResponse", "Response Data: " + new String(error.networkResponse.data));
        }
        String errorMessage = getString(R.string.unknown_error);
        if (error instanceof TimeoutError) {
            errorMessage = getString(R.string.timeout_error);
        } else if (error instanceof NoConnectionError) {
            errorMessage = getString(R.string.no_connection_error);
        } else if (error instanceof AuthFailureError) {
            errorMessage = getString(R.string.auth_failure_error);
        } else if (error instanceof ServerError) {
            errorMessage = getString(R.string.server_error);
        } else if (error instanceof NetworkError) {
            errorMessage = getString(R.string.network_error);
        } else if (error instanceof ParseError) {
            errorMessage = getString(R.string.parse_error);
        }
        Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
    }

    private void showDateRangeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.layout_dialog_date_range, null);
        RecyclerView dateRangeRecyclerView = dialogView.findViewById(R.id.dateRangeRecyclerView);
        List<String> dateRanges = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int previousYear = currentYear - 1;

        dateRanges.add("01/04/" + currentYear + " - 31/03/" + (currentYear + 1));
        dateRanges.add("01/04/" + previousYear + " - 31/03/" + currentYear);

        DateRangeAdapter adapter = new DateRangeAdapter(dateRanges);
        dateRangeRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        dateRangeRecyclerView.setAdapter(adapter);
        builder.setTitle(R.string.hint_choose_year);
        builder.setView(dialogView);

        builder.setNegativeButton(R.string.cancel, (dialog1, which) -> {
            dialog1.dismiss();
            resetOtherButtonColors(loadFilterFragments.button2);
            resetOtherButtonColors(loadFilterFragments.button3);
            resetOtherButtonColors(loadFilterFragments.button1);

        });

        builder.setPositiveButton(R.string.searchtxt, (dialog1, which) -> {
            bySearch = true;
            String selectedDateRange = adapter.getSelectedDateRange();
            if (selectedDateRange != null) {
                String[] dates = selectedDateRange.split(" - ");
                String startDate = dates[0].trim();
                String endDate = dates[1].trim();
                SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
                SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                try {
                    String formattedStartDate = outputFormat.format(inputFormat.parse(startDate));
                    String formattedEndDate = outputFormat.format(inputFormat.parse(endDate));
                    getSellingList(formattedStartDate, formattedEndDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            resetOtherButtonColors(loadFilterFragments.button2);
            resetOtherButtonColors(loadFilterFragments.button3);
            resetOtherButtonColors(loadFilterFragments.button1);
            dialog1.dismiss();
        });

        AlertDialog dialog1 = builder.create();
        dialog1.setCancelable(false);
        dialog1.show();

        adapter.setOnItemSelectedListener(selectedDateRange -> {
        });
    }
}