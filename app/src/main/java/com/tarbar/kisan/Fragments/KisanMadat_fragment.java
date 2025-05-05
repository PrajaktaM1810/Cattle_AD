package com.tarbar.kisan.Fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.tarbar.kisan.Activities.LoadFilterFragments;
import com.tarbar.kisan.Activities.MainActivity;
import com.tarbar.kisan.Adapter.DateRangeAdapter;
import com.tarbar.kisan.Adapter.DoodhLeaderBoard_Adapter;
import com.tarbar.kisan.Adapter.Free_BijdanList_Adapter;
import com.tarbar.kisan.Adapter.Kisan_Madat_List_Adapter;
import com.tarbar.kisan.Helper.ApiUtils;
import com.tarbar.kisan.Helper.Iconstant;
import com.tarbar.kisan.Helper.SharedPreferenceManager;
import com.tarbar.kisan.Helper.constant;
import com.tarbar.kisan.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
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

public class KisanMadat_fragment extends Fragment {
    TextView totalHelpers, id_data_not_found, dateRange,totalAmt;
    RecyclerView recyclerView;
    Kisan_Madat_List_Adapter adapter;
    List<Map<String, String>> data = new ArrayList<>();
    List<Map<String, String>> filteredData = new ArrayList<>();
    List<Map<String, String>> filteredSearchData = new ArrayList<>();
    private LoadFilterFragments loadFilterFragmentsActivityInstance;
    boolean isAllSelected = false, isCowSelected = false, isBuffaloSelected = false, bySearch = false;
    String fromDate, toDate;
    private String selectedLayout = "";
    List<Map<String, String>> activeList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_kisan_madat_option, container, false);
        view.setBackgroundColor(Color.WHITE);

        loadFilterFragmentsActivityInstance = (LoadFilterFragments) getActivity();
        totalHelpers = view.findViewById(R.id.totalHelpers);
        totalAmt = view.findViewById(R.id.totalAmt);
        id_data_not_found = view.findViewById(R.id.id_data_not_found);
        dateRange = view.findViewById(R.id.dateRange);
        recyclerView = view.findViewById(R.id.recyclerView);
//        recyclerView.setNestedScrollingEnabled(false);
        adapter = new Kisan_Madat_List_Adapter(data, requireContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date today = new Date();
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);

        fromDate = (currentYear - 1) + "-04-01";
        toDate = currentYear + "-03-31";

        try {
            Date fromDateObj = dateFormat.parse(fromDate);
            Date toDateObj = dateFormat.parse(toDate);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(today);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            today = calendar.getTime();

            calendar.setTime(fromDateObj);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            fromDateObj = calendar.getTime();

            calendar.setTime(toDateObj);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            toDateObj = calendar.getTime();

            if (!today.before(fromDateObj) && !today.after(toDateObj) || today.equals(fromDateObj) || today.equals(toDateObj)) {
                getUsersList(fromDate, toDate);
            } else {
                fromDate = currentYear + "-04-01";
                toDate = (currentYear + 1) + "-03-31";
                getUsersList(fromDate, toDate);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (loadFilterFragmentsActivityInstance != null && loadFilterFragmentsActivityInstance.cardView != null) {
            loadFilterFragmentsActivityInstance.cardView.setVisibility(View.VISIBLE);
            loadFilterFragmentsActivityInstance.button1.setVisibility(View.VISIBLE);
            loadFilterFragmentsActivityInstance.iconbutton1.setBackgroundResource(R.drawable.icon_calender);

            loadFilterFragmentsActivityInstance.btn1txt.setText(R.string.str_year);
            loadFilterFragmentsActivityInstance.btn2txt.setText(R.string.str_filter);
            loadFilterFragmentsActivityInstance.btn3txt.setText(R.string.str_search);

            loadFilterFragmentsActivityInstance.swipeRefreshLayout.setOnRefreshListener(() -> {
                bySearch = false;
                getUsersList(fromDate, toDate);
            });

            loadFilterFragmentsActivityInstance.back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.click_animation);
                    v.startAnimation(animation);
                    Intent i = new Intent(requireContext(), MainActivity.class);
                    i.putExtra("SELECT_HOME_FRAGMENT", true);
                    startActivity(i);
                    requireActivity().finish();
                }
            });

            loadFilterFragmentsActivityInstance.button1.setOnClickListener(v -> {
                Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.click_animation);
                v.startAnimation(animation);
                changeButtonAndTextColor(loadFilterFragmentsActivityInstance.button1, loadFilterFragmentsActivityInstance.btn1txt);
                resetOtherButtonColors(loadFilterFragmentsActivityInstance.button1);
                showDateRangeDialog();
            });

            loadFilterFragmentsActivityInstance.button2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.click_animation);
                    v.startAnimation(animation);
                    changeButtonAndTextColor(loadFilterFragmentsActivityInstance.button2, loadFilterFragmentsActivityInstance.btn2txt);
                    resetOtherButtonColors(loadFilterFragmentsActivityInstance.button2);
                    showCheckBoxPopup();
                }
            });

            loadFilterFragmentsActivityInstance.button3.setOnClickListener(v -> {
                Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.click_animation);
                v.startAnimation(animation);
                changeButtonAndTextColor(loadFilterFragmentsActivityInstance.button3, loadFilterFragmentsActivityInstance.btn3txt);
                resetOtherButtonColors(loadFilterFragmentsActivityInstance.button3);
                searchDialog();
            });
        }
        return view;
    }

    private void showCheckBoxPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.CustomRadioButtonDialogTheme);
        builder.setTitle("चयन करें");
        builder.setCancelable(false);

        String[] options = {
                getString(R.string.filter_more_money),
                getString(R.string.filter_minimum_money),
                getString(R.string.rank)
        };

        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(20, 20, 20, 20);

        CheckBox[] checkBoxes = new CheckBox[options.length];
        for (int i = 0; i < options.length; i++) {
            CheckBox checkBox = new CheckBox(getContext());
            checkBox.setText(options[i]);
            checkBoxes[i] = checkBox;

            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    for (CheckBox other : checkBoxes) {
                        if (other != checkBox) {
                            other.setChecked(false);
                        }
                    }
                }
            });

            layout.addView(checkBox);
        }

        builder.setView(layout);

        builder.setPositiveButton(R.string.searchtxt, (dialog, which) -> {
            for (int i = 0; i < checkBoxes.length; i++) {
                if (checkBoxes[i].isChecked()) {
                    switch (i) {
                        case 0:
                            sortDataByMoney(true);
                            break;
                        case 1:
                            sortDataByMoney(false);
                            break;
                        case 2:
                            sortDataByRank();
                            break;
                    }
                    break;
                }
            }
            resetOtherButtonColors(loadFilterFragmentsActivityInstance.button2);
            resetOtherButtonColors(loadFilterFragmentsActivityInstance.button3);
            resetOtherButtonColors(loadFilterFragmentsActivityInstance.button1);
        });

        builder.setNegativeButton(R.string.cancel, (dialog, which) -> {
            dialog.dismiss();
            resetOtherButtonColors(loadFilterFragmentsActivityInstance.button2);
            resetOtherButtonColors(loadFilterFragmentsActivityInstance.button3);
            resetOtherButtonColors(loadFilterFragmentsActivityInstance.button1);
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void sortDataByMoney(boolean isDescending) {
        Collections.sort(data, (o1, o2) -> {
            int money1 = Integer.parseInt(o1.get("kisan_help_money").isEmpty() ? "0" : o1.get("kisan_help_money"));
            int money2 = Integer.parseInt(o2.get("kisan_help_money").isEmpty() ? "0" : o2.get("kisan_help_money"));

            return isDescending ? Integer.compare(money2, money1) : Integer.compare(money1, money2);
        });

        adapter.updateList(data);
    }

    private void sortDataByRank() {
        Collections.sort(data, (o1, o2) -> {
            int rank1 = Integer.parseInt(o1.get("kisan_rank").isEmpty() ? "0" : o1.get("kisan_rank"));
            int rank2 = Integer.parseInt(o2.get("kisan_rank").isEmpty() ? "0" : o2.get("kisan_rank"));

            return Integer.compare(rank1, rank2);
        });

        adapter.updateList(data);
    }

    private void updateTotalCounts(List<Map<String, String>> list) {
        int TotalMoney = 0;
        for (Map<String, String> item : list) {
            try {
                int totalMoney = Integer.parseInt(item.get("kisan_help_money").isEmpty() ? "0" : item.get("kisan_help_money"));
                TotalMoney += totalMoney;
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        totalAmt.setText(getString(R.string.total_money, String.valueOf(TotalMoney)));
    }

    public void getUsersList(String fromDate, String toDate) {
        ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage(getString(R.string.getting_data));
        dialog.setCancelable(false);
        dialog.show();

        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        String formattedFromDate = fromDate;
        String formattedToDate = toDate;

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

        String url = constant.GET_KISAN_MADAT_LIST + "?from_date=" + fromDate + "&to_date=" + toDate;
        Log.d("Request_URL", "Constructed URL: " + url);
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());

        StringRequest getRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    bySearch = false;
                    data.clear();
                    filteredData.clear();
                    filteredSearchData.clear();
                    loadFilterFragmentsActivityInstance.swipeRefreshLayout.setRefreshing(false);
                    dialog.dismiss();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.optString("status", "");
                        if ("success".equalsIgnoreCase(status)) {
                            recyclerView.setVisibility(View.VISIBLE);
                            id_data_not_found.setVisibility(View.GONE);
                            JSONArray dataArray = jsonObject.getJSONArray("data");
                            List<Map<String, String>> sortedData = new ArrayList<>();
                            for (int i = 0; i < dataArray.length(); i++) {
                                JSONObject userObj = dataArray.getJSONObject(i);
                                Map<String, String> userInfo = new HashMap<>();
                                userInfo.put("name", userObj.optString("name", ""));
                                userInfo.put("father_name", userObj.optString("father_name", ""));
//                                userInfo.put("surname", userObj.optString("surname", ""));
                                userInfo.put("caste", userObj.optString("caste", ""));
                                userInfo.put("kisanNumber", userObj.optString("kisan_number", ""));
                                userInfo.put("state", userObj.optString("state", ""));
                                userInfo.put("profilePic", userObj.optString("profile_pic", ""));
                                userInfo.put("tahsil", userObj.optString("tahsil", ""));
                                userInfo.put("jilha", userObj.optString("jilha", ""));
                                userInfo.put("village", userObj.optString("village", ""));
                                userInfo.put("kisan_rank", userObj.optString("kisan_rank", ""));
                                userInfo.put("kisan_help_money", userObj.optString("kisan_help_money", ""));
                                userInfo.put("kisan_help_money_date", userObj.optString("kisan_help_money_date", ""));
                                sortedData.add(userInfo);
                            }

                            Collections.sort(sortedData, Comparator.comparingInt(o -> Integer.parseInt(o.get("kisan_rank").isEmpty() ? "0" : o.get("kisan_rank"))));
                            data.addAll(sortedData);

                            adapter.updateList(data);
                            totalHelpers.setText(getString(R.string.total_helpers, String.valueOf(dataArray.length())));
                            updateTotalCounts(data);
                        } else {
                            recyclerView.setVisibility(View.GONE);
                            id_data_not_found.setVisibility(View.VISIBLE);
                            totalHelpers.setText(R.string.total_ranks_zero);
                            updateTotalCounts(data);
                        }
                    } catch (JSONException e) {
                        Toast.makeText(getContext(), R.string.json_parsing_error, Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    loadFilterFragmentsActivityInstance.swipeRefreshLayout.setRefreshing(false);
                    dialog.dismiss();
                    handleVolleyError(error);
                });
        getRequest.setRetryPolicy(ApiUtils.DEFAULT_RETRY_POLICY);
        requestQueue.add(getRequest);
    }


    private void handleVolleyError(VolleyError error) {
        Log.e("Error", "Error: " + error.toString());
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

//    Down Buttons

    private void changeButtonAndTextColor(LinearLayout selectedButton, TextView text) {
        selectedButton.setBackgroundColor(getResources().getColor(R.color.LogoBlue));
        ((TextView) text).setTextColor(getResources().getColor(R.color.white));
    }

    private void resetOtherButtonColors(LinearLayout selecteLayout) {
        if (selecteLayout != loadFilterFragmentsActivityInstance.button1) {
            loadFilterFragmentsActivityInstance.button1.setBackgroundColor(getResources().getColor(android.R.color.white));
            ((TextView) loadFilterFragmentsActivityInstance.btn1txt).setTextColor(getResources().getColor(android.R.color.black));
        }
        if (selecteLayout != loadFilterFragmentsActivityInstance.button2) {
            loadFilterFragmentsActivityInstance.button2.setBackgroundColor(getResources().getColor(android.R.color.white));
            ((TextView) loadFilterFragmentsActivityInstance.btn2txt).setTextColor(getResources().getColor(android.R.color.black));
        }
        if (selecteLayout != loadFilterFragmentsActivityInstance.button3) {
            loadFilterFragmentsActivityInstance.button3.setBackgroundColor(getResources().getColor(android.R.color.white));
            ((TextView) loadFilterFragmentsActivityInstance.btn3txt).setTextColor(getResources().getColor(android.R.color.black));
        }
    }

//   Button3 Searching

    private void searchDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.lyt_forget_password, null);
        builder.setView(dialogView);
        AlertDialog dialog2 = builder.create();
        dialog2.setCancelable(false);

        LinearLayout password_layout = dialogView.findViewById(R.id.password_layout);
        LinearLayout mobile_layout = dialogView.findViewById(R.id.mobileLayout);
        LinearLayout pashu_layout = dialogView.findViewById(R.id.pashuNumber);
        EditText animalNumber = dialogView.findViewById(R.id.AnimalNumber);
        TextView textGetPassword = dialogView.findViewById(R.id.textGetPassword);
        TextView textCancel = dialogView.findViewById(R.id.textCancel);
        TextView textSearch = dialogView.findViewById(R.id.searchTxt);

        animalNumber.setHint(getString(R.string.hint_add_kisan_number));

        pashu_layout.setVisibility(View.VISIBLE);
        textSearch.setVisibility(View.VISIBLE);
        mobile_layout.setVisibility(View.GONE);
        password_layout.setVisibility(View.GONE);
        textCancel.setVisibility(View.VISIBLE);
        textGetPassword.setVisibility(View.GONE);

        dialogView.findViewById(R.id.textCancel).setOnClickListener(v -> {
            dialog2.dismiss();
            resetOtherButtonColors(loadFilterFragmentsActivityInstance.button3);
            resetOtherButtonColors(loadFilterFragmentsActivityInstance.button2);
            resetOtherButtonColors(loadFilterFragmentsActivityInstance.button1);
        });

        dialogView.findViewById(R.id.searchTxt).setOnClickListener(v -> {
            bySearch = true;
            String enteredKisanNumber = animalNumber.getText().toString();
            if (enteredKisanNumber.isEmpty()) {
                animalNumber.setError(getString(R.string.hint_add_kisan_number));
            } else {
                filterDataByKisanNumber(enteredKisanNumber);
                dialog2.dismiss();

                resetOtherButtonColors(loadFilterFragmentsActivityInstance.button3);
                resetOtherButtonColors(loadFilterFragmentsActivityInstance.button2);
                resetOtherButtonColors(loadFilterFragmentsActivityInstance.button1);
            }
        });
        dialog2.show();
    }

    private void filterDataByKisanNumber(String enteredKisanNumber) {
        filteredData.clear();
        filteredSearchData.clear();
        resetOtherButtonColors(loadFilterFragmentsActivityInstance.button2);
        resetOtherButtonColors(loadFilterFragmentsActivityInstance.button3);
        for (Map<String, String> item : data) {
            String kisanNumber = item.get("kisanNumber");
            if (kisanNumber != null && kisanNumber.equals(enteredKisanNumber)) {
                filteredSearchData.add(item);
            }
        }
        adapter.updateList(filteredSearchData);
        updateTotalCounts(filteredSearchData);
        totalHelpers.setText(getString(R.string.total_helpers, String.valueOf(filteredSearchData.size())));

        if (filteredSearchData.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            id_data_not_found.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            id_data_not_found.setVisibility(View.GONE);
        }
    }

//   Button1 Filter

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
            resetOtherButtonColors(loadFilterFragmentsActivityInstance.button2);
            resetOtherButtonColors(loadFilterFragmentsActivityInstance.button3);
            resetOtherButtonColors(loadFilterFragmentsActivityInstance.button1);

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
                    getUsersList(formattedStartDate, formattedEndDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            resetOtherButtonColors(loadFilterFragmentsActivityInstance.button2);
            resetOtherButtonColors(loadFilterFragmentsActivityInstance.button3);
            resetOtherButtonColors(loadFilterFragmentsActivityInstance.button1);
            dialog1.dismiss();
        });

        AlertDialog dialog1 = builder.create();
        dialog1.setCancelable(false);
        dialog1.show();

        adapter.setOnItemSelectedListener(selectedDateRange -> {
        });
    }
}