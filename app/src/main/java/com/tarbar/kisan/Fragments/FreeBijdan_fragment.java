package com.tarbar.kisan.Fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
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

public class FreeBijdan_fragment extends Fragment {
    LinearLayout allLayout, cowLayout, buffaloLayout;
    TextView allLTxt, cowTxt, buffaloTxt;
    TextView totalKisan, id_data_not_found, dateRange;
    RecyclerView recyclerView;
    Free_BijdanList_Adapter adapter;
    List<Map<String, String>> data = new ArrayList<>();
    List<Map<String, String>> filteredData = new ArrayList<>();
    List<Map<String, String>> filteredSearchData = new ArrayList<>();
    private LoadFilterFragments loadFilterFragmentsActivityInstance;
    boolean isAllSelected = false, isCowSelected = false, isBuffaloSelected = false, bySearch = false;
    String fromDate, toDate;
    private String selectedLayout = "";
    List<Map<String, String>> activeList;
    TextView freeBijdancnt,BijdanDonecnt;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_free_bijdan, container, false);
        view.setBackgroundColor(Color.WHITE);

        loadFilterFragmentsActivityInstance = (LoadFilterFragments) getActivity();
        allLayout = view.findViewById(R.id.allLayout);
        cowLayout = view.findViewById(R.id.cowLayout);
        buffaloLayout = view.findViewById(R.id.buffaloLayout);
        allLTxt = view.findViewById(R.id.allLTxt);
        cowTxt = view.findViewById(R.id.cowTxt);
        buffaloTxt = view.findViewById(R.id.buffaloTxt);
        totalKisan = view.findViewById(R.id.totalKisan);
        id_data_not_found = view.findViewById(R.id.id_data_not_found);
        dateRange = view.findViewById(R.id.dateRange);
        freeBijdancnt = view.findViewById(R.id.freeBijdancnt);
        BijdanDonecnt = view.findViewById(R.id.BijdanDonecnt);

        recyclerView = view.findViewById(R.id.recyclerView);
//        recyclerView.setNestedScrollingEnabled(false);
        adapter = new Free_BijdanList_Adapter(data, requireContext());
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
                getbBijdanList(fromDate, toDate);
            } else {
                fromDate = currentYear + "-04-01";
                toDate = (currentYear + 1) + "-03-31";
                getbBijdanList(fromDate, toDate);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        allLTxt.setText(R.string.str_sabhi);
        cowTxt.setText(R.string.str_cow);
        buffaloTxt.setText(R.string.str_buffalo);

        allLayout.setOnClickListener(v -> {
            Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.click_animation);
            v.startAnimation(animation);
            toggleSelection(v, 1);
        });

        cowLayout.setOnClickListener(v -> {
            Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.click_animation);
            v.startAnimation(animation);
            toggleSelection(v, 2);
        });

        buffaloLayout.setOnClickListener(v -> {
            Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.click_animation);
            v.startAnimation(animation);
            toggleSelection(v, 3);
        });

        if (loadFilterFragmentsActivityInstance != null && loadFilterFragmentsActivityInstance.cardView != null) {
            loadFilterFragmentsActivityInstance.cardView.setVisibility(View.VISIBLE);
            loadFilterFragmentsActivityInstance.button1.setVisibility(View.VISIBLE);
            loadFilterFragmentsActivityInstance.iconbutton1.setBackgroundResource(R.drawable.icon_calender);

            loadFilterFragmentsActivityInstance.btn1txt.setText(R.string.str_year);
            loadFilterFragmentsActivityInstance.btn2txt.setText(R.string.str_filter);
            loadFilterFragmentsActivityInstance.btn3txt.setText(R.string.str_search);

            loadFilterFragmentsActivityInstance.swipeRefreshLayout.setOnRefreshListener(() -> {
                bySearch = false;
                getbBijdanList(fromDate, toDate);
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
                    String selectedLayout = getSelectedLayout();
                    Map<String, Integer> optionImageMap = new LinkedHashMap<>();
                    switch (selectedLayout) {
                        case "गाय":
                            optionImageMap.put("गिर गाय", R.drawable.gir_cow);
                            optionImageMap.put("साहीवाल गाय", R.drawable.sahival_cow);
                            optionImageMap.put("कांकरेज गाय", R.drawable.kankrej_cow);
                            break;
                        case "भैंस":
                            optionImageMap.put("मुर्रा भैंस", R.drawable.murrah_buffalo_image);
                            optionImageMap.put("मेहसानी भैंस", R.drawable.mehasani_buffalo_image);
                            break;
                    }
                    showRadioButtonPopup(optionImageMap, selectedLayout);
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
        toggleSelection(allLayout, 1);
        return view;
    }

//    Upper Buttons

    private void toggleSelection(View view, int buttonType) {
        resetButtonColors();

        if (bySearch) {
            activeList = filteredSearchData;
        } else {
            activeList = data;
        }

        if (buttonType == 1) {
            isAllSelected = true;
            isCowSelected = false;
            isBuffaloSelected = false;
            selectedLayout = "";
            changeButtonColor(allLayout);
            applyFilter(activeList, "");

            if (loadFilterFragmentsActivityInstance.button2 != null) {
                loadFilterFragmentsActivityInstance.button2.setVisibility(View.GONE);
            }
        } else {
            if (loadFilterFragmentsActivityInstance.button2 != null) {
                loadFilterFragmentsActivityInstance.button2.setVisibility(View.VISIBLE);
            }
            if (buttonType == 2) {
                isCowSelected = true;
                isAllSelected = false;
                isBuffaloSelected = false;
                selectedLayout = "गाय";
                changeButtonColor(cowLayout);
                applyFilter(activeList, "गाय");
            } else if (buttonType == 3) {
                isBuffaloSelected = true;
                isAllSelected = false;
                isCowSelected = false;
                selectedLayout = "भैंस";
                changeButtonColor(buffaloLayout);
                applyFilter(activeList, "भैंस");
            }
        }
    }

    private void applyFilter(List<Map<String, String>> sourceList, String typeFilter) {
        filteredData.clear();
        for (Map<String, String> item : sourceList) {
            String type = item.get("type");
            if (typeFilter.isEmpty() || (type != null && type.equalsIgnoreCase(typeFilter))) {
                filteredData.add(item);
            }
        }

        if (filteredData.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            id_data_not_found.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            id_data_not_found.setVisibility(View.GONE);
        }
        updateTotalCounts(filteredData);
        adapter.updateList(filteredData);
        totalKisan.setText(getString(R.string.total_kisan, String.valueOf(filteredData.size())));
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

    private void showRadioButtonPopup(Map<String, Integer> optionImageMap, String selectedLayout) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.CustomRadioButtonDialogTheme);
        builder.setTitle(R.string.hint_choose_caste);
        builder.setCancelable(false);
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);

        final CheckBox[] checkBoxes = new CheckBox[optionImageMap.size()];
        int index = 0;

        for (Map.Entry<String, Integer> entry : optionImageMap.entrySet()) {
            String option = entry.getKey();
            int imageResId = entry.getValue();

            LinearLayout horizontalLayout = new LinearLayout(getContext());
            horizontalLayout.setOrientation(LinearLayout.HORIZONTAL);

            CheckBox checkBox = new CheckBox(getContext());
            checkBox.setText("");
            checkBox.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

            TextView textView = new TextView(getContext());
            textView.setText(option);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            textView.setTextColor(Color.BLACK);

            LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1.0f
            );
            textView.setLayoutParams(textParams);

            ImageView imageView = new ImageView(getContext());
            LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(dpToPx(40), dpToPx(40));
            imageParams.setMargins(dpToPx(5), 0, dpToPx(5), 0);
            imageView.setLayoutParams(imageParams);
            imageView.setImageResource(imageResId);

            horizontalLayout.addView(checkBox);
            horizontalLayout.addView(textView);
            horizontalLayout.addView(imageView);

            int margin = 10;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(dpToPx(margin), dpToPx(margin), dpToPx(margin), dpToPx(margin));
            horizontalLayout.setLayoutParams(params);

            checkBoxes[index] = checkBox;
            layout.addView(horizontalLayout);
            horizontalLayout.setOnClickListener(v -> {
                checkBox.setChecked(!checkBox.isChecked());

                if (checkBox.isChecked()) {
                    for (int j = 0; j < checkBoxes.length; j++) {
                        if (checkBoxes[j] != checkBox) {
                            checkBoxes[j].setChecked(false);
                        }
                    }
                }
            });

            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    for (int j = 0; j < checkBoxes.length; j++) {
                        if (checkBoxes[j] != buttonView) {
                            checkBoxes[j].setChecked(false);
                        }
                    }
                }
            });
            index++;
        }

        builder.setView(layout);

        builder.setPositiveButton(R.string.searchtxt, (dialog, which) -> {
            resetOtherButtonColors(loadFilterFragmentsActivityInstance.button2);
            resetOtherButtonColors(loadFilterFragmentsActivityInstance.button1);
            resetOtherButtonColors(loadFilterFragmentsActivityInstance.button3);
            List<String> selectedOptions = new ArrayList<>();
            for (int i = 0; i < checkBoxes.length; i++) {
                if (checkBoxes[i].isChecked()) {
                    selectedOptions.add(optionImageMap.keySet().toArray(new String[0])[i]);
                }
            }
            if (!selectedOptions.isEmpty()) {
                String selectedCaste = selectedOptions.get(0);
                filterDataByAnimalCaste(selectedCaste);
            }
        });

        builder.setNegativeButton(R.string.cancel, (dialog, which) -> {
            dialog.dismiss();
            resetOtherButtonColors(loadFilterFragmentsActivityInstance.button2);
            resetOtherButtonColors(loadFilterFragmentsActivityInstance.button1);
            resetOtherButtonColors(loadFilterFragmentsActivityInstance.button3);
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return (int) (dp * density + 0.5f);
    }

    private String getSelectedLayout() {
        return selectedLayout;
    }

    private void filterDataByAnimalCaste(String selectedCaste) {
        List<Map<String, String>> filteredList = new ArrayList<>();

        for (Map<String, String> item : data) {
            String animalCaste = item.get("animal_caste");
            if (animalCaste != null && animalCaste.equalsIgnoreCase(selectedCaste)) {
                filteredList.add(item);
            }
        }

        adapter.updateList(filteredList);
        updateTotalCounts(filteredList);
        totalKisan.setText(getString(R.string.total_kisan, String.valueOf(filteredList.size())));

        if (filteredList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            id_data_not_found.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            id_data_not_found.setVisibility(View.GONE);
        }
    }

    private void updateTotalCounts(List<Map<String, String>> list) {
        int totalFreeBijdan = 0;
        int totalDoneBijdan = 0;

        for (Map<String, String> item : list) {
            try {
                int freeCount = Integer.parseInt(item.get("freeBijdanCnt").isEmpty() ? "0" : item.get("freeBijdanCnt"));
                int doneCount = Integer.parseInt(item.get("totalDoneBijdanCnt").isEmpty() ? "0" : item.get("totalDoneBijdanCnt"));
                totalFreeBijdan += freeCount;
                totalDoneBijdan += doneCount;
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        freeBijdancnt.setText(getString(R.string.title_free_bijdan_cnt, String.valueOf(totalFreeBijdan)));
        BijdanDonecnt.setText(getString(R.string.title_bijdan_done_cnt, String.valueOf(totalDoneBijdan)));
    }

    public void getbBijdanList(String fromDate, String toDate) {
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

        String url = constant.GET_ANIMAL_LIST + "?from_date=" + fromDate + "&to_date=" + toDate;
        Log.d("Request_URL", "Constructed URL: " + url);
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());

        StringRequest getRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    bySearch = false;
                    data.clear();
                    filteredData.clear();
                    filteredSearchData.clear();
                    toggleSelection(allLayout, 1);
                    loadFilterFragmentsActivityInstance.swipeRefreshLayout.setRefreshing(false);
                    dialog.dismiss();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.optString("status", "");
                        if ("success".equalsIgnoreCase(status)) {
                            recyclerView.setVisibility(View.VISIBLE);
                            id_data_not_found.setVisibility(View.GONE);
                            JSONArray dataArray = jsonObject.getJSONArray("data");
                            for (int i = 0; i < dataArray.length(); i++) {
                                JSONObject animalObj = dataArray.getJSONObject(i);

                                Map<String, String> animalInfo = new HashMap<>();
                                animalInfo.put("type", animalObj.optString("type", ""));
                                animalInfo.put("animal_caste", animalObj.optString("animal_caste", ""));
                                animalInfo.put("kisanNumber", animalObj.optString("kisan_number", ""));
                                animalInfo.put("isOff", animalObj.optString("isoff", ""));
                                animalInfo.put("freeBijdanCnt", animalObj.optString("free_bijdan_cnt", ""));
                                animalInfo.put("totalDoneBijdanCnt", animalObj.optString("total_done_bijdan_cnt", ""));
                                data.add(animalInfo);
                            }
                            adapter.updateList(data);
                            totalKisan.setText(getString(R.string.total_kisan, String.valueOf(dataArray.length())));
                            updateTotalCounts(data);
                        } else {
                            recyclerView.setVisibility(View.GONE);
                            id_data_not_found.setVisibility(View.VISIBLE);
                            totalKisan.setText(R.string.total_ranks_zero);
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
                toggleSelection(allLayout, 1);
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
        totalKisan.setText(getString(R.string.total_ranks, String.valueOf(filteredSearchData.size())));
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
                    getbBijdanList(formattedStartDate, formattedEndDate);
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