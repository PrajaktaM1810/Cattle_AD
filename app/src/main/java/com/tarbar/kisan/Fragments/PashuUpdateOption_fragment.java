package com.tarbar.kisan.Fragments;

import static android.view.View.GONE;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.constraintlayout.widget.ConstraintLayout;
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
import com.tarbar.kisan.Activities.LoadFormFragments;
import com.tarbar.kisan.Activities.MainActivity;
import com.tarbar.kisan.Adapter.DateRangeAdapter;
import com.tarbar.kisan.Adapter.Selling_List_Adapter;
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
import java.util.List;
import java.util.Locale;
import java.util.Map;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PashuUpdateOption_fragment extends Fragment {
    private SharedPreferenceManager sharedPrefMgr;
    ProgressDialog dialog;
    LinearLayout allLayout, cowLayout, buffaloLayout;
    TextView allLTxt, cowTxt, buffaloTxt;
    TextView id_data_not_found;
    RecyclerView recyclerView;
    Selling_List_Adapter adapter;
    List<Map<String, String>> sellingData = new ArrayList<>();
    List<Map<String, String>> filteredData = new ArrayList<>();
    private LoadFilterFragments loadFilterFragments;
    boolean isAllSelected = false, isCowSelected = false, isBuffaloSelected = false;
    private String selectedLayout = "";
    String userid;
    TextView dateRange;
    ConstraintLayout dateFilter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pashu_update_option, container, false);
        view.setBackgroundColor(Color.WHITE);

        sharedPrefMgr = new SharedPreferenceManager(requireContext());
        sharedPrefMgr.connectDB();
        userid = sharedPrefMgr.getString(Iconstant.userid);
        sharedPrefMgr.closeDB();

        loadFilterFragments = (LoadFilterFragments) getActivity();
        allLayout = view.findViewById(R.id.allLayout);
        cowLayout = view.findViewById(R.id.cowLayout);
        buffaloLayout = view.findViewById(R.id.buffaloLayout);
        allLTxt = view.findViewById(R.id.allLTxt);
        cowTxt = view.findViewById(R.id.cowTxt);
        buffaloTxt = view.findViewById(R.id.buffaloTxt);
        id_data_not_found = view.findViewById(R.id.id_data_not_found);
        dateRange = view.findViewById(R.id.dateRange);
        dateFilter = view.findViewById(R.id.dateFilter);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new Selling_List_Adapter(getContext(), filteredData);
        recyclerView.setAdapter(adapter);

        historyList(null,null);

        allLTxt.setText(R.string.str_pashu_becha);
        cowTxt.setText(R.string.str_cow);
        buffaloTxt.setText(R.string.str_buffalo);

        allLayout.setOnClickListener(v -> {
            Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.click_animation);
            v.startAnimation(animation);
            toggleSelection(v, 1);
            filterList();
        });

        cowLayout.setOnClickListener(v -> {
            Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.click_animation);
            v.startAnimation(animation);
            toggleSelection(v, 2);
            Intent i = new Intent(requireContext(), LoadFormFragments.class);
            i.putExtra("PashuUpdateAddForm_fragment", true);
            i.putExtra("animalType", "गाय");
            startActivity(i);
        });

        buffaloLayout.setOnClickListener(v -> {
            Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.click_animation);
            v.startAnimation(animation);
            toggleSelection(v, 3);
            Intent i = new Intent(requireContext(), LoadFormFragments.class);
            i.putExtra("PashuUpdateAddForm_fragment", true);
            i.putExtra("animalType", "भैंस");
            startActivity(i);
        });

        if (loadFilterFragments != null) {
            loadFilterFragments.bottomMenu.setVisibility(View.VISIBLE);
            loadFilterFragments.button3.setVisibility(GONE);
            loadFilterFragments.button2.setVisibility(GONE);
            loadFilterFragments.button4.setVisibility(GONE);

            loadFilterFragments.btn1txt.setText(R.string.str_year);

            loadFilterFragments.iconbutton1.setBackgroundResource(R.drawable.icon_calender);

            loadFilterFragments.swipeRefreshLayout.setOnRefreshListener(() -> {
                historyList(null,null);
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

            loadFilterFragments.button3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.click_animation);
                    v.startAnimation(animation);
                    changeButtonAndTextColor(loadFilterFragments.button3, loadFilterFragments.btn3txt);
                    resetOtherButtonColors(loadFilterFragments.button3);
                }
            });
        }
        toggleSelection(allLayout, 1);

        AppCompatEditText searchEditText = view.findViewById(R.id.searchEditText);
        ImageView cancelIcon = view.findViewById(R.id.cancelIcon);

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    cancelIcon.setVisibility(View.VISIBLE);
                    filterDataByMobileNumber(s.toString());
                } else {
                    cancelIcon.setVisibility(View.GONE);
                    adapter.updateList(filteredData);
                    if (filteredData.isEmpty()) {
                        recyclerView.setVisibility(View.GONE);
                        id_data_not_found.setVisibility(View.VISIBLE);
                    } else {
                        recyclerView.setVisibility(View.VISIBLE);
                        id_data_not_found.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        cancelIcon.setOnClickListener(v -> {
            searchEditText.setText("");
            cancelIcon.setVisibility(View.GONE);
            adapter.updateList(filteredData);
            if (filteredData.isEmpty()) {
                recyclerView.setVisibility(View.GONE);
                id_data_not_found.setVisibility(View.VISIBLE);
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                id_data_not_found.setVisibility(View.GONE);
            }
        });

        return view;
    }

    private void filterDataByMobileNumber(String enteredNumber) {
        List<Map<String, String>> filteredSearchData = new ArrayList<>();

        for (Map<String, String> item : sellingData) {
            String sellerNumber = item.get("seller_number");
            if (sellerNumber != null && sellerNumber.contains(enteredNumber)) {
                filteredSearchData.add(item);
            }
        }

        adapter.updateList(filteredSearchData);

        if (filteredSearchData.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            id_data_not_found.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            id_data_not_found.setVisibility(View.GONE);
        }
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

    private void toggleSelection(View view, int buttonType) {
        resetButtonColors();
        if (buttonType == 1) {
            isAllSelected = true;
            isCowSelected = false;
            isBuffaloSelected = false;
            selectedLayout = "";
            changeButtonColor(allLayout);
        } else if (buttonType == 2) {
            isAllSelected = false;
            isCowSelected = true;
            isBuffaloSelected = false;
            selectedLayout = "गाय";
            changeButtonColor(cowLayout);
        } else if (buttonType == 3) {
            isAllSelected = false;
            isCowSelected = false;
            isBuffaloSelected = true;
            selectedLayout = "भैंस";
            changeButtonColor(buffaloLayout);
        }
    }

    private void filterList() {
        filteredData.clear();
        if (isAllSelected) {
            filteredData.addAll(sellingData);
        } else {
            for (Map<String, String> map : sellingData) {
                if (map.get("pashu_type") != null && map.get("pashu_type").equals(selectedLayout)) {
                    filteredData.add(map);
                }
            }
        }

        if (filteredData.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            id_data_not_found.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            id_data_not_found.setVisibility(View.GONE);
        }

        if (adapter == null) {
            adapter = new Selling_List_Adapter(getContext(), filteredData);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.updateList(new ArrayList<>(filteredData));
        }
    }

    private void resetButtonColors() {
        resetButtonColor(allLayout);
        resetButtonColor(cowLayout);
        resetButtonColor(buffaloLayout);
    }

    public void resetButtonColor(LinearLayout layout) {
        layout.setBackgroundColor(getResources().getColor(R.color.white));
        TextView textView = findTextViewInsideLinearLayout(layout);
        if (textView != null) {
            textView.setTextColor(getResources().getColor(R.color.black));
        }
    }

    public void changeButtonColor(LinearLayout layout) {
        layout.setBackgroundColor(getResources().getColor(R.color.orange));
        TextView textView = findTextViewInsideLinearLayout(layout);
        if (textView != null) {
            textView.setTextColor(getResources().getColor(R.color.white));
        }
    }

    private TextView findTextViewInsideLinearLayout(LinearLayout layout) {
        for (int i = 0; i < layout.getChildCount(); i++) {
            View child = layout.getChildAt(i);
            if (child instanceof TextView) {
                return (TextView) child;
            }
        }
        return null;
    }

//    public void historyList(String fromDate, String toDate) {
//        dialog = new ProgressDialog(getActivity());
//        dialog.setMessage(getString(R.string.getting_data));
//        dialog.setCancelable(false);
//        dialog.show();
//
//        String url = GET_SELLING_LIST;
//        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
//
//        Log.d("HistoryList", "Sending request to: " + url);
//        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
//                response -> {
//                    try {
//                        Log.d("HistoryList", "Response received: " + response);
//
//                        if (loadFilterFragments != null) {
//                            loadFilterFragments.swipeRefreshLayout.setRefreshing(false);
//                        }
//                        dialog.dismiss();
//
//                        JSONObject jsonObject = new JSONObject(response);
//                        String status = jsonObject.getString("status");
//                        Log.d("HistoryList", "Status: " + status);
//
//                        if (status.equalsIgnoreCase("success")) {
//                            if (jsonObject.has("data")) {
//                                JSONArray dataArray = jsonObject.getJSONArray("data");
//                                Log.d("HistoryList", "Data array length: " + dataArray.length());
//
//                                sellingData.clear();
//                                for (int i = dataArray.length() - 1; i >= 0; i--) {
//                                    JSONObject animalObj = dataArray.getJSONObject(i);
//                                    Map<String, String> Info = new HashMap<>();
//                                    Info.put("id", animalObj.optString("id", ""));
//                                    Info.put("pashu_type", animalObj.optString("pashu_type", ""));
//                                    Info.put("seller_number", animalObj.optString("seller_number", ""));
//                                    Info.put("selling_date", animalObj.optString("selling_date", ""));
//
//                                    Log.d("HistoryList", "Parsed item: " + Info.toString());
//                                    sellingData.add(Info);
//                                }
//                                Log.d("HistoryList", "Total items added to list: " + sellingData.size());
//
//                                requireActivity().runOnUiThread(() -> {
//                                    filterList();
//                                    if (recyclerView != null && recyclerView.getAdapter() != null) {
//                                        recyclerView.getAdapter().notifyDataSetChanged();
//                                    }
//                                });
//                            }
//                        } else {
//                            sellingData.clear();
//                            requireActivity().runOnUiThread(this::filterList);
//                        }
//                    } catch (JSONException e) {
//                        dialog.dismiss();
//                        if (e instanceof JSONException) {
//                            Toast.makeText(getContext(), R.string.json_parsing_error, Toast.LENGTH_SHORT).show();
//                        } else {
//                            Log.d("CheckException",""+e);
//                            Toast.makeText(getContext(), R.string.generic_error, Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                    dateRange.setText(formattedFromDate + " - " + formattedToDate);
//                },
//                error -> {
//                    dialog.dismiss();
//                    if (error instanceof TimeoutError) {
//                        Toast.makeText(getContext(), R.string.timeout_error, Toast.LENGTH_SHORT).show();
//                    } else if (error instanceof NoConnectionError) {
//                        Toast.makeText(getContext(), R.string.no_connection_error, Toast.LENGTH_SHORT).show();
//                    } else if (error instanceof AuthFailureError) {
//                        Toast.makeText(getContext(), R.string.auth_failure_error, Toast.LENGTH_SHORT).show();
//                    } else if (error instanceof ServerError) {
//                        Log.d("ServerError",""+error);
//                        Toast.makeText(getContext(), R.string.server_error, Toast.LENGTH_SHORT).show();
//                    } else if (error instanceof NetworkError) {
//                        Toast.makeText(getContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
//                    } else if (error instanceof ParseError) {
//                        Toast.makeText(getContext(), R.string.parse_error, Toast.LENGTH_SHORT).show();
//                    } else {
//                        Toast.makeText(getContext(), R.string.unknown_error, Toast.LENGTH_SHORT).show();
//                    }
//                    Log.d("Check", "" + error.getMessage());
//                }) {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> params = new HashMap<>();
//                params.put("userid", userid);
//                if (fromDate != null && !fromDate.isEmpty() && toDate != null && !toDate.isEmpty()) {
//                    params.put("from_date", fromDate);
//                    params.put("to_date", toDate);
//                }
//                Log.d("HistoryList", "Params: " + params.toString());
//                return params;
//            }
//        };
//        postRequest.setRetryPolicy(ApiUtils.DEFAULT_RETRY_POLICY);
//        requestQueue.add(postRequest);
//    }

    public void historyList(String fromDate, String toDate) {
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
            dateFilter.setVisibility(View.VISIBLE);
            dateRange.setText(formattedFromDate + " - " + formattedToDate);
        } else {
            dateFilter.setVisibility(View.GONE);
            dateRange.setText("");
        }

        String url = constant.GET_SELLING_LIST;
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());

        StringRequest getRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    if (loadFilterFragments != null) {
                        loadFilterFragments.swipeRefreshLayout.setRefreshing(false);
                    }
                    dialog.dismiss();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");

                        if (status.equalsIgnoreCase("success")) {
                            sellingData.clear();
                            if (jsonObject.has("data")) {
                                JSONArray dataArray = jsonObject.getJSONArray("data");
                                for (int i = dataArray.length() - 1; i >= 0; i--) {
                                    JSONObject animalObj = dataArray.getJSONObject(i);
                                    Map<String, String> Info = new HashMap<>();
                                    Info.put("id", animalObj.optString("id", ""));
                                    Info.put("pashu_type", animalObj.optString("pashu_type", ""));
                                    Info.put("seller_number", animalObj.optString("seller_number", ""));
                                    Info.put("selling_date", animalObj.optString("selling_date", ""));
                                    sellingData.add(Info);
                                }
                            }
                            requireActivity().runOnUiThread(() -> {
                                filterList();
                                if (recyclerView != null && recyclerView.getAdapter() != null) {
                                    recyclerView.getAdapter().notifyDataSetChanged();
                                }
                            });
                        } else {
                            sellingData.clear();
                            requireActivity().runOnUiThread(this::filterList);
                        }
                    } catch (JSONException e) {
                        dialog.dismiss();
                        if (loadFilterFragments != null && loadFilterFragments.swipeRefreshLayout.isRefreshing()) {
                            loadFilterFragments.swipeRefreshLayout.setRefreshing(false);
                        }
                        Toast.makeText(getContext(), R.string.json_parsing_error, Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    if (loadFilterFragments != null && loadFilterFragments.swipeRefreshLayout.isRefreshing()) {
                        loadFilterFragments.swipeRefreshLayout.setRefreshing(false);
                    }
                    dialog.dismiss();
                    if (error instanceof TimeoutError) {
                        Toast.makeText(getContext(), R.string.timeout_error, Toast.LENGTH_SHORT).show();
                    } else if (error instanceof NoConnectionError) {
                        Toast.makeText(getContext(), R.string.no_connection_error, Toast.LENGTH_SHORT).show();
                    } else if (error instanceof AuthFailureError) {
                        Toast.makeText(getContext(), R.string.auth_failure_error, Toast.LENGTH_SHORT).show();
                    } else if (error instanceof ServerError) {
                        Toast.makeText(getContext(), R.string.server_error, Toast.LENGTH_SHORT).show();
                    } else if (error instanceof NetworkError) {
                        Toast.makeText(getContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
                    } else if (error instanceof ParseError) {
                        Toast.makeText(getContext(), R.string.parse_error, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), R.string.unknown_error, Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("userid", userid);
                params.put("from_date", fromDate != null ? fromDate : "");
                params.put("to_date", toDate != null ? toDate : "");
                return params;
            }
        };
        getRequest.setRetryPolicy(ApiUtils.DEFAULT_RETRY_POLICY);
        requestQueue.add(getRequest);
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
                    historyList(formattedStartDate, formattedEndDate);
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