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
import android.widget.CheckBox;
import android.widget.EditText;
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
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.tarbar.kisan.Activities.LoadFilterFragments;
import com.tarbar.kisan.Activities.MainActivity;
import com.tarbar.kisan.Adapter.DateRangeAdapter;
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
import androidx.appcompat.widget.AppCompatEditText;
import android.widget.ImageView;

public class KisanMadat_fragment extends Fragment {
    TextView totalHelpers, id_data_not_found, dateRange, totalAmt;
    RecyclerView recyclerView;
    Kisan_Madat_List_Adapter adapter;
    List<Map<String, String>> data = new ArrayList<>();
    List<Map<String, String>> filteredData = new ArrayList<>();
    List<Map<String, String>> filteredSearchData = new ArrayList<>();
    private LoadFilterFragments loadFilterFragmentsActivityInstance;
    boolean bySearch = false;
    String fromDate, toDate;
    private SharedPreferenceManager sharedPrefMgr;
    String kisanId, Password;
    String statusStr = "";
    String messageStr = "";
    String isHelperStr = "";
    String isBusinessman = "";
    String isDoctor = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_kisan_madat_option, container, false);
        view.setBackgroundColor(Color.WHITE);

        sharedPrefMgr = new SharedPreferenceManager(getContext());
        sharedPrefMgr.connectDB();
        kisanId = sharedPrefMgr.getString(Iconstant.userid);
        Password = sharedPrefMgr.getString(Iconstant.password);
        sharedPrefMgr.closeDB();

        AppCompatEditText searchEditText = view.findViewById(R.id.searchEditText);
        ImageView cancelIcon = view.findViewById(R.id.cancelIcon);

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    cancelIcon.setVisibility(View.VISIBLE);
                    filterData(s.toString());
                } else {
                    cancelIcon.setVisibility(View.GONE);
                    adapter.updateList(data);
                    updateTotalCounts(data);
                    totalHelpers.setText(getString(R.string.total_helpers, String.valueOf(data.size())));
                    if (data.isEmpty()) {
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
        });

        loadFilterFragmentsActivityInstance = (LoadFilterFragments) getActivity();
        totalHelpers = view.findViewById(R.id.totalHelpers);
        totalAmt = view.findViewById(R.id.totalAmt);
        id_data_not_found = view.findViewById(R.id.id_data_not_found);
        dateRange = view.findViewById(R.id.dateRange);
        recyclerView = view.findViewById(R.id.recyclerView);
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
                fromDate = (currentYear - 1) + "-04-01";
                toDate = currentYear + "-03-31";
                getUsersList(fromDate, toDate);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (loadFilterFragmentsActivityInstance != null && loadFilterFragmentsActivityInstance.cardView != null) {
            loadFilterFragmentsActivityInstance.cardView.setVisibility(View.VISIBLE);
            loadFilterFragmentsActivityInstance.button1.setVisibility(View.VISIBLE);
            loadFilterFragmentsActivityInstance.button2.setVisibility(View.VISIBLE);
            loadFilterFragmentsActivityInstance.iconbutton1.setBackgroundResource(R.drawable.icon_calender);

            loadFilterFragmentsActivityInstance.btn1txt.setText(R.string.str_year);
            loadFilterFragmentsActivityInstance.btn2txt.setText(R.string.str_filter);

            loadFilterFragmentsActivityInstance.swipeRefreshLayout.setOnRefreshListener(() -> {
                bySearch = false;
                getUsersList(fromDate, toDate);
            });

            loadFilterFragmentsActivityInstance.back.setOnClickListener(v -> {
                Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.click_animation);
                v.startAnimation(animation);
                Intent i = new Intent(requireContext(), MainActivity.class);
                i.putExtra("SELECT_HOME_FRAGMENT", true);
                startActivity(i);
                requireActivity().finish();
            });

            loadFilterFragmentsActivityInstance.button1.setOnClickListener(v -> {
                Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.click_animation);
                v.startAnimation(animation);
                changeButtonAndTextColor(loadFilterFragmentsActivityInstance.button1, loadFilterFragmentsActivityInstance.btn1txt);
                resetOtherButtonColors(loadFilterFragmentsActivityInstance.button1);
                showDateRangeDialog();
            });

            loadFilterFragmentsActivityInstance.button2.setOnClickListener(v -> {
                Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.click_animation);
                v.startAnimation(animation);
                changeButtonAndTextColor(loadFilterFragmentsActivityInstance.button2, loadFilterFragmentsActivityInstance.btn2txt);
                resetOtherButtonColors(loadFilterFragmentsActivityInstance.button2);
                showCheckBoxPopup();
            });

            loadFilterFragmentsActivityInstance.button3.setOnClickListener(v -> {
                Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.click_animation);
                v.startAnimation(animation);
                changeButtonAndTextColor(loadFilterFragmentsActivityInstance.button3, loadFilterFragmentsActivityInstance.btn3txt);
                resetOtherButtonColors(loadFilterFragmentsActivityInstance.button3);
                joinAsHelperMessageDialog();
            });

            loadFilterFragmentsActivityInstance.button4.setOnClickListener(v -> {
                Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.click_animation);
                v.startAnimation(animation);
                changeButtonAndTextColor(loadFilterFragmentsActivityInstance.button4, loadFilterFragmentsActivityInstance.btn4txt);
                resetOtherButtonColors(loadFilterFragmentsActivityInstance.button4);

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(getString(R.string.confirm_delete_helper_role));

                builder.setNegativeButton(getString(R.string.no), (dialog, which) -> {
                    resetOtherButtonColors(loadFilterFragmentsActivityInstance.button4);
                    resetOtherButtonColors(loadFilterFragmentsActivityInstance.button2);
                    resetOtherButtonColors(loadFilterFragmentsActivityInstance.button3);
                    resetOtherButtonColors(loadFilterFragmentsActivityInstance.button1);
                });

                builder.setPositiveButton(getString(R.string.yes), (dialog, which) -> {
                    resetOtherButtonColors(loadFilterFragmentsActivityInstance.button4);
                    resetOtherButtonColors(loadFilterFragmentsActivityInstance.button2);
                    resetOtherButtonColors(loadFilterFragmentsActivityInstance.button3);
                    resetOtherButtonColors(loadFilterFragmentsActivityInstance.button1);
                    dialog.dismiss();
                    messageDialog();
                });

                AlertDialog alert = builder.create();
                alert.setOnShowListener(dialogInterface -> {
                    alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
                    alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
                });
                alert.show();
            });
        }
        getHelperStatus(kisanId);
        return view;
    }

    private void joinAsHelperMessageDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.lyt_forget_password, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        EditText passwordEditText = dialogView.findViewById(R.id.Password);
        TextView textGetPassword = dialogView.findViewById(R.id.textGetPassword);
        LinearLayout passwordLayout = dialogView.findViewById(R.id.password_layout);
        LinearLayout mobileLayout = dialogView.findViewById(R.id.mobileLayout);
        LinearLayout pashuNumber = dialogView.findViewById(R.id.pashuNumber);
        passwordLayout.setVisibility(View.VISIBLE);
        mobileLayout.setVisibility(View.GONE);
        pashuNumber.setVisibility(View.GONE);

        textGetPassword.setText(R.string.str_join_as_helper);

        dialogView.findViewById(R.id.textGetPassword).setOnClickListener(v -> {
            String pass = passwordEditText.getText().toString();

            if (pass.isEmpty()) {
                passwordEditText.setError(getString(R.string.enter_password_number));
            } else if (pass.length() < 4) {
                passwordEditText.setError(getString(R.string.enter_correct_password));
            } else if (!pass.equals(Password)) {
                Toast.makeText(requireContext(), R.string.error_password_not_matching, Toast.LENGTH_SHORT).show();
            } else {
                dialog.dismiss();
                updateUserBusinessStatus(kisanId, "1");
            }
            resetOtherButtonColors(loadFilterFragmentsActivityInstance.button4);
            resetOtherButtonColors(loadFilterFragmentsActivityInstance.button2);
            resetOtherButtonColors(loadFilterFragmentsActivityInstance.button3);
            resetOtherButtonColors(loadFilterFragmentsActivityInstance.button1);
        });

        dialogView.findViewById(R.id.textCancel).setOnClickListener(v -> {
            dialog.dismiss();
            resetOtherButtonColors(loadFilterFragmentsActivityInstance.button4);
            resetOtherButtonColors(loadFilterFragmentsActivityInstance.button2);
            resetOtherButtonColors(loadFilterFragmentsActivityInstance.button3);
            resetOtherButtonColors(loadFilterFragmentsActivityInstance.button1);
        });
        dialog.show();
    }

    private void messageDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.lyt_forget_password, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        EditText passwordEditText = dialogView.findViewById(R.id.Password);
        TextView textGetPassword = dialogView.findViewById(R.id.textGetPassword);
        LinearLayout passwordLayout = dialogView.findViewById(R.id.password_layout);
        LinearLayout mobileLayout = dialogView.findViewById(R.id.mobileLayout);
        LinearLayout pashuNumber = dialogView.findViewById(R.id.pashuNumber);
        passwordLayout.setVisibility(View.VISIBLE);
        mobileLayout.setVisibility(View.GONE);
        pashuNumber.setVisibility(View.GONE);

        textGetPassword.setText(R.string.delete_animal_profile);

        dialogView.findViewById(R.id.textGetPassword).setOnClickListener(v -> {
            String pass = passwordEditText.getText().toString();

            if (pass.isEmpty()) {
                passwordEditText.setError(getString(R.string.enter_password_number));
            } else if (pass.length() < 4) {
                passwordEditText.setError(getString(R.string.enter_correct_password));
            } else if (!pass.equals(Password)) {
                Toast.makeText(requireContext(), R.string.error_password_not_matching, Toast.LENGTH_SHORT).show();
            } else {
                dialog.dismiss();
                updateUserBusinessStatus(kisanId, "0");
            }
        });

        dialogView.findViewById(R.id.textCancel).setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void getHelperStatus(String userId) {
        if (userId == null || userId.isEmpty()) {
            return;
        }
        StringRequest getRequest = new StringRequest(Request.Method.POST, constant.GET_USER_ROLES,
                response -> {
                    Log.d("Checkresponse", "" + response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        statusStr = jsonObject.optString("status", "");
                        if ("success".equalsIgnoreCase(statusStr)) {
                            JSONObject dataObject = jsonObject.optJSONObject("data");
                            if (dataObject != null) {
                                isHelperStr = dataObject.optString("is_helper");
                                if (isHelperStr.equals("0")) {
                                    Log.d("HelperStatus", "Status already set. Skipping...");
                                    loadFilterFragmentsActivityInstance.button3.setVisibility(View.VISIBLE);
                                    loadFilterFragmentsActivityInstance.iconbutton3.getLayoutParams().height = 80;
                                    loadFilterFragmentsActivityInstance.iconbutton3.getLayoutParams().width = 80;
                                    loadFilterFragmentsActivityInstance.iconbutton3.setImageResource(R.drawable.kisan_madat);
                                    loadFilterFragmentsActivityInstance.btn3txt.setText(R.string.str_join_as_helper);
                                    loadFilterFragmentsActivityInstance.button4.setVisibility(GONE);
                                } else if (isHelperStr.equals("1")) {
                                    loadFilterFragmentsActivityInstance.button4.setVisibility(View.VISIBLE);
                                    loadFilterFragmentsActivityInstance.iconbutton4.setImageResource(R.drawable.cancel_iocn);
                                    loadFilterFragmentsActivityInstance.btn4txt.setText(R.string.cancel_role);
                                    loadFilterFragmentsActivityInstance.button3.setVisibility(View.GONE);
                                }
                                Log.d("BusinessStatus", "User is helper: " + isHelperStr);
                            } else {
                                Log.e("BusinessStatus", "Data object missing");
                            }
                        } else {
                            messageStr = jsonObject.optString("message", "Failed to get business status");
                            Log.e("BusinessStatus", messageStr);
                        }
                    } catch (JSONException e) {
                        Log.e("BusinessStatus", "JSON parsing error: " + e.getMessage());
                        Toast.makeText(getContext(), R.string.json_parsing_error, Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("BusinessStatus", "Error fetching business status: " + error.getMessage());
                    Toast.makeText(getContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", userId);
                return params;
            }
        };
        getRequest.setRetryPolicy(ApiUtils.DEFAULT_RETRY_POLICY);
        Volley.newRequestQueue(requireContext()).add(getRequest);
    }

    private void updateUserBusinessStatus(String userId, String statusValue) {
        ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage(getString(R.string.saving_data));
        dialog.setCancelable(false);
        dialog.show();
        if (userId == null || userId.isEmpty()) {
            return;
        }
        String url = constant.UPDATE_USER_ROLE;
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    dialog.dismiss();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.optString("status", "");

                        if ("success".equalsIgnoreCase(status)) {
                            getHelperStatus(kisanId);
                            String message = jsonObject.optString("message", "भूमिका सफलतापूर्वक अपडेट कर दी गई");
                            Log.d("UpdateStatus", message);
                            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                        } else {
                            String message = jsonObject.optString("message", "भूमिका को अपडेट करने में विफल");
                            Log.e("UpdateStatus", message);
                            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Log.e("UpdateStatus", "JSON parsing error: " + e.getMessage());
                        Toast.makeText(getContext(), R.string.json_parsing_error, Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    dialog.dismiss();
                    Log.e("UpdateStatus", "Error updating business status: " + error.getMessage());
                    Toast.makeText(getContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", userId);
                params.put("is_helper", statusValue);
                return params;
            }
        };
        postRequest.setRetryPolicy(ApiUtils.DEFAULT_RETRY_POLICY);
        requestQueue.add(postRequest);
    }

    private void showCheckBoxPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.CustomRadioButtonDialogTheme);
        builder.setTitle("चयन करें");
        builder.setCancelable(false);

        String[] options = {
                getString(R.string.filter_more_money),
                getString(R.string.filter_minimum_money),
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
                    }
                    break;
                }
            }
            resetOtherButtonColors(loadFilterFragmentsActivityInstance.button4);
            resetOtherButtonColors(loadFilterFragmentsActivityInstance.button2);
            resetOtherButtonColors(loadFilterFragmentsActivityInstance.button3);
            resetOtherButtonColors(loadFilterFragmentsActivityInstance.button1);
        });

        builder.setNegativeButton(R.string.cancel, (dialog, which) -> {
            dialog.dismiss();
            resetOtherButtonColors(loadFilterFragmentsActivityInstance.button4);
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
            Log.d("GetUsersList", "Date parse error: " + e.getMessage());
            e.printStackTrace();
        }

        dateRange.setText(formattedFromDate + " - " + formattedToDate);
        Log.d("GetUsersList", "Request params -> from_date: " + fromDate + ", to_date: " + toDate);
        String url = constant.GET_KISAN_MADAT_LIST + "?from_date=" + fromDate + "&to_date=" + toDate;
        Log.d("GetUsersList", "URL: " + url);
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());

        StringRequest getRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    Log.d("GetUsersList", "Response: " + response);
                    bySearch = false;
                    data.clear();
                    filteredData.clear();
                    filteredSearchData.clear();
                    loadFilterFragmentsActivityInstance.swipeRefreshLayout.setRefreshing(false);
                    dialog.dismiss();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.optString("status", "");
                        Log.d("GetUsersList", "Status: " + status);
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
                        Log.d("GetUsersList", "JSON parse error: " + e.getMessage());
                        Toast.makeText(getContext(), R.string.json_parsing_error, Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.d("GetUsersList", "Volley error: " + error.toString());
                    loadFilterFragmentsActivityInstance.swipeRefreshLayout.setRefreshing(false);
                    dialog.dismiss();
                    handleVolleyError(error);
                });
        getRequest.setRetryPolicy(ApiUtils.DEFAULT_RETRY_POLICY);
        requestQueue.add(getRequest);
    }

    private void handleVolleyError(VolleyError error) {
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
        if (selecteLayout != loadFilterFragmentsActivityInstance.button4) {
            loadFilterFragmentsActivityInstance.button4.setBackgroundColor(getResources().getColor(android.R.color.white));
            ((TextView) loadFilterFragmentsActivityInstance.btn4txt).setTextColor(getResources().getColor(android.R.color.black));
        }
    }

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
        EditText kisanNumber = dialogView.findViewById(R.id.forgetPasswordNumber);
        TextView textGetPassword = dialogView.findViewById(R.id.textGetPassword);
        TextView textCancel = dialogView.findViewById(R.id.textCancel);
        TextView textSearch = dialogView.findViewById(R.id.searchTxt);

        kisanNumber.setHint(getString(R.string.hint_add_kisan_number));

        pashu_layout.setVisibility(View.GONE);
        textSearch.setVisibility(View.VISIBLE);
        mobile_layout.setVisibility(View.VISIBLE);
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
            String enteredKisanNumber = kisanNumber.getText().toString();
            if (enteredKisanNumber.isEmpty()) {
                kisanNumber.setError(getString(R.string.hint_add_kisan_number));
            } else {
                filterData(enteredKisanNumber);
                dialog2.dismiss();
                resetOtherButtonColors(loadFilterFragmentsActivityInstance.button3);
                resetOtherButtonColors(loadFilterFragmentsActivityInstance.button2);
                resetOtherButtonColors(loadFilterFragmentsActivityInstance.button1);
            }
        });
        dialog2.show();
    }

    private void filterData(String searchText) {
        filteredSearchData.clear();
        searchText = searchText.toLowerCase(Locale.getDefault());

        for (Map<String, String> item : data) {
            String name = item.get("name") != null ? item.get("name").toLowerCase(Locale.getDefault()) : "";
            String fatherName = item.get("father_name") != null ? item.get("father_name").toLowerCase(Locale.getDefault()) : "";
            String caste = item.get("caste") != null ? item.get("caste").toLowerCase(Locale.getDefault()) : "";
            String kisanNumber = item.get("kisanNumber") != null ? item.get("kisanNumber").toLowerCase(Locale.getDefault()) : "";
            String state = item.get("state") != null ? item.get("state").toLowerCase(Locale.getDefault()) : "";
            String tahsil = item.get("tahsil") != null ? item.get("tahsil").toLowerCase(Locale.getDefault()) : "";
            String jilha = item.get("jilha") != null ? item.get("jilha").toLowerCase(Locale.getDefault()) : "";
            String village = item.get("village") != null ? item.get("village").toLowerCase(Locale.getDefault()) : "";
            String kisanRank = item.get("kisan_rank") != null ? item.get("kisan_rank").toLowerCase(Locale.getDefault()) : "";

            if (name.contains(searchText) ||
                    fatherName.contains(searchText) ||
                    caste.contains(searchText) ||
                    kisanNumber.contains(searchText) ||
                    state.contains(searchText) ||
                    tahsil.contains(searchText) ||
                    jilha.contains(searchText) ||
                    village.contains(searchText) ||
                    kisanRank.contains(searchText)) {
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

        adapter.setOnItemSelectedListener(selectedDateRange -> {});
    }
}