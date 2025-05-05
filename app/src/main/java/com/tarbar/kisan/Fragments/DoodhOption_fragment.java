package com.tarbar.kisan.Fragments;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.tarbar.kisan.Helper.constant.GET_ANIMAL_LIST;

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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.tarbar.kisan.Activities.LoadFilterFragments;
import com.tarbar.kisan.Activities.LoadFragments;
import com.tarbar.kisan.Activities.MainActivity;
import com.tarbar.kisan.Adapter.Doodh_List_Adapter;
import com.tarbar.kisan.Helper.ApiUtils;
import com.tarbar.kisan.Helper.Iconstant;
import com.tarbar.kisan.Helper.SharedPreferenceManager;
import com.tarbar.kisan.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Arrays;
import android.widget.ImageView;

public class DoodhOption_fragment extends Fragment {
    private SharedPreferenceManager sharedPrefMgr;
    ProgressDialog dialog;
    String mobileNumber;
    LinearLayout allLayout, cowLayout, buffaloLayout;
    TextView allLTxt,cowTxt,buffaloTxt;
    TextView txt_all, txt_cow, txt_buffalo, totalAnimal, kisanNumber,shadowKisanNumber, id_data_not_found;
    RecyclerView recyclerView;
    Doodh_List_Adapter adapter;
    List<Map<String, String>> animalData = new ArrayList<>();
    List<Map<String, String>> animalDataSearch = new ArrayList<>();
    List<Map<String, String>> filteredData = new ArrayList<>();
    private MainActivity mainActivityInstance;
    String strkisanNumberFromApi;
    boolean isAllSelected = false, isCowSelected = false, isBuffaloSelected = false, bySearch = false;
    String MobileNumber,PashuNumber;
    private String selectedLayout = "";
    boolean isMobileMatched = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_doodh_option, container, false);
        view.setBackgroundColor(Color.WHITE);
        sharedPrefMgr = new SharedPreferenceManager(getContext());
        sharedPrefMgr.connectDB();
        mobileNumber = sharedPrefMgr.getString(Iconstant.mobile);
        Log.d("MobileNumber", "" + mobileNumber);
        sharedPrefMgr.closeDB();

        mainActivityInstance = (MainActivity) getActivity();
        allLayout = view.findViewById(R.id.allLayout);
        cowLayout = view.findViewById(R.id.cowLayout);
        buffaloLayout = view.findViewById(R.id.buffaloLayout);
        allLTxt = view.findViewById(R.id.allLTxt);
        cowTxt = view.findViewById(R.id.cowTxt);
        buffaloTxt = view.findViewById(R.id.buffaloTxt);

        totalAnimal = view.findViewById(R.id.totalAnimal);
        kisanNumber = view.findViewById(R.id.kisanNumber);
        shadowKisanNumber = view.findViewById(R.id.shadowKisanNumber);
        id_data_not_found = view.findViewById(R.id.id_data_not_found);

        recyclerView = view.findViewById(R.id.recyclerView);
        adapter = new Doodh_List_Adapter(getContext(), filteredData);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        detailsHistory();

        allLTxt.setText(R.string.str_sabhi);
        cowTxt.setText(R.string.str_cow);
        buffaloTxt.setText(R.string.str_buffalo);

        allLayout.setOnClickListener(v -> {
            Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.click_animation);
            v.startAnimation(animation);
            toggleSelection(v, 1);
            if (bySearch == true){
                filterAnimalsBySearch("", PashuNumber, "");
            } else {
                filterAnimalsByTypeAndMobileNumber("", MobileNumber, "");
            }
        });

        cowLayout.setOnClickListener(v -> {
            Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.click_animation);
            v.startAnimation(animation);
            toggleSelection(v, 2);
            if (bySearch == true){
                filterAnimalsBySearch("गाय", PashuNumber, "");
            } else {
                filterAnimalsByTypeAndMobileNumber("गाय", MobileNumber, "");
            }
        });

        buffaloLayout.setOnClickListener(v -> {
            Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.click_animation);
            v.startAnimation(animation);
            toggleSelection(v, 3);
            if (bySearch == true){
                filterAnimalsBySearch("भैंस", PashuNumber, "");
            } else {
                filterAnimalsByTypeAndMobileNumber("भैंस", MobileNumber, "");
            }
        });

        if (mainActivityInstance != null && mainActivityInstance.cardView != null) {
            mainActivityInstance.cardView.setVisibility(VISIBLE);
            mainActivityInstance.button1.setVisibility(VISIBLE);
            mainActivityInstance.iconbutton1.setBackgroundResource(R.drawable.leaderboard_icon);

            mainActivityInstance.swipeRefreshLayout.setOnRefreshListener(() -> {
                bySearch = false;
                changeButtonColor(allLayout);
                detailsHistory();
                resetButtonColor(cowLayout);
                resetButtonColor(buffaloLayout);
                mainActivityInstance.button2.setVisibility(GONE);
            });

            mainActivityInstance.button1.setOnClickListener(v -> {
                Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.click_animation);
                v.startAnimation(animation);
                changeButtonAndTextColor(mainActivityInstance.button1, mainActivityInstance.btn1txt);
                resetOtherButtonColors(mainActivityInstance.button1);
                Intent intent = new Intent(getContext(), LoadFilterFragments.class);
                intent.putExtra("DoodhLeaderboard_fragment", true);
                startActivity(intent);
            });

            mainActivityInstance.button2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.click_animation);
                    v.startAnimation(animation);
                    changeButtonAndTextColor(mainActivityInstance.button2, mainActivityInstance.btn2txt);
                    resetOtherButtonColors(mainActivityInstance.button2);
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

            mainActivityInstance.button3.setOnClickListener(v -> {
                Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.click_animation);
                v.startAnimation(animation);
                changeButtonAndTextColor(mainActivityInstance.button3, mainActivityInstance.btn3txt);
                resetOtherButtonColors(mainActivityInstance.button3);
                searchDialog();
            });
        }
        toggleSelection(allLayout, 1);
        return view;
    }

    public void setBySearch(boolean bySearch) {
        this.bySearch = bySearch;
    }

    public LinearLayout getAllLayout() {
        return allLayout;
    }

    public LinearLayout getBuffaloLayout() {
        return buffaloLayout;
    }

    public LinearLayout getCowLayout() {
        return cowLayout;
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
            resetBackButtonColors(mainActivityInstance.button2);
            resetOtherButtonColors(mainActivityInstance.button2);
            List<String> selectedOptions = new ArrayList<>();
            for (int i = 0; i < checkBoxes.length; i++) {
                if (checkBoxes[i].isChecked()) {
                    selectedOptions.add(optionImageMap.keySet().toArray(new String[0])[i]);
                }
            }

            for (String selectedOption : selectedOptions) {
                if (bySearch) {
                    filterAnimalsBySearch(selectedLayout, PashuNumber, selectedOption);
                } else {
                    filterAnimalsByTypeAndMobileNumber(selectedLayout, getMobileNumber(), selectedOption);
                }
            }
        });

        builder.setNegativeButton(R.string.cancel, (dialog, which) -> {
            dialog.dismiss();
            resetBackButtonColors(mainActivityInstance.button2);
            resetOtherButtonColors(mainActivityInstance.button2);
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

    private String getMobileNumber() {
        return mobileNumber;
    }

    private void toggleSelection(View view, int buttonType) {
        resetButtonColors();
        if (buttonType == 1) {
            isAllSelected = true;
            isCowSelected = false;
            isBuffaloSelected = false;
            selectedLayout = "";
            changeButtonColor(allLayout);
            if (bySearch == true){
                filterAnimalsBySearch("", PashuNumber, "");
            } else {
                filterAnimalsByTypeAndMobileNumber("", MobileNumber, "");
            }
            mainActivityInstance.button2.setVisibility(View.GONE);
        } else if (buttonType == 2) {
            isCowSelected = true;
            isAllSelected = false;
            isBuffaloSelected = false;
            selectedLayout = "गाय";
            changeButtonColor(cowLayout);
            if (bySearch == true){
                filterAnimalsBySearch("गाय", PashuNumber, "");
            } else {
                filterAnimalsByTypeAndMobileNumber("गाय", MobileNumber, "");
            }
            mainActivityInstance.button2.setVisibility(VISIBLE);
        } else if (buttonType == 3) {
            isBuffaloSelected = true;
            isAllSelected = false;
            isCowSelected = false;
            selectedLayout = "भैंस";
            changeButtonColor(buffaloLayout);
            if (bySearch == true){
                filterAnimalsBySearch("भैंस", PashuNumber, "");
            } else {
                filterAnimalsByTypeAndMobileNumber("भैंस", MobileNumber, "");
            }
            mainActivityInstance.button2.setVisibility(VISIBLE);
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

    private void filterAnimalsBySearch(String type, String PashuNumber, String caste) {
        List<Map<String, String>> filteredAnimals;
        if (type != null && !type.isEmpty() && caste != null && !caste.isEmpty()) {
            filteredAnimals = getFilteredAnimalsBySearch(type, PashuNumber, caste);
        } else if (type != null && !type.isEmpty()) {
            filteredAnimals = getFilteredAnimalsBySearch(type, PashuNumber, "");
        } else if (caste != null && !caste.isEmpty()) {
            filteredAnimals = getFilteredAnimalsBySearch("", PashuNumber, caste);
        } else {
            filteredAnimals = getFilteredAnimalsBySearch("", PashuNumber, "");
        }
        if (filteredAnimals.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
//            id_data_not_found.setText(caste+" उपलब्ध नहीं है।");
            id_data_not_found.setVisibility(VISIBLE);
        } else {
            recyclerView.setVisibility(VISIBLE);
            id_data_not_found.setVisibility(View.GONE);
        }
        displayAnimals(filteredAnimals);
    }

    private void filterAnimalsByTypeAndMobileNumber(String type, String mobileNumber, String caste) {
        List<Map<String, String>> filteredAnimals;
        if (type != null && !type.isEmpty() && caste != null && !caste.isEmpty()) {
            filteredAnimals = getFilteredAnimals(type, mobileNumber, caste);
        } else if (type != null && !type.isEmpty()) {
            filteredAnimals = getFilteredAnimals(type, mobileNumber, "");
        } else if (caste != null && !caste.isEmpty()) {
            filteredAnimals = getFilteredAnimals("", mobileNumber, caste);
        } else {
            filteredAnimals = getFilteredAnimals("", mobileNumber, "");
        }
        if (filteredAnimals.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
//            id_data_not_found.setText(caste+" उपलब्ध नहीं है।");
            id_data_not_found.setVisibility(VISIBLE);
        } else {
            recyclerView.setVisibility(VISIBLE);
            id_data_not_found.setVisibility(View.GONE);
        }
        displayAnimals(filteredAnimals);
    }

    private List<Map<String, String>> getFilteredAnimalsBySearch(String type, String pashunumber, String caste) {
        List<Map<String, String>> filteredAnimals = new ArrayList<>();
        for (Map<String, String> animalSearch : animalDataSearch) {
            boolean typeMatch = type.isEmpty() || type.equals(animalSearch.get("type"));
            boolean pashu = pashunumber.isEmpty() || pashunumber.equals(animalSearch.get("animalNo"));
            boolean casteMatch = caste.isEmpty() || caste.equals(animalSearch.get("animalCaste"));
            if (typeMatch && pashu && casteMatch) {
                filteredAnimals.add(animalSearch);
            }
        }

        if (filteredAnimals.isEmpty()) {
            for (Map<String, String> animalSearch : animalData) {
                boolean typeMatch = type.isEmpty() || type.equals(animalSearch.get("type"));
                boolean pashu = pashunumber.isEmpty() || pashunumber.equals(animalSearch.get("animalNo"));
                boolean casteMatch = caste.isEmpty() || caste.equals(animalSearch.get("animalCaste"));
                if (typeMatch && pashu && casteMatch) {
                    filteredAnimals.add(animalSearch);
                }
            }
        }
        return filteredAnimals;
    }

    private List<Map<String, String>> getFilteredAnimals(String type, String mobileNumber, String caste) {
        List<Map<String, String>> filteredAnimals = new ArrayList<>();
        for (Map<String, String> animal : animalData) {
            boolean typeMatch = type.isEmpty() || type.equals(animal.get("type"));
            boolean mobileMatch = mobileNumber.equals(animal.get("kisanNumber"));
            boolean casteMatch = caste.isEmpty() || caste.equals(animal.get("animalCaste"));

            if (typeMatch && mobileMatch && casteMatch) {
                filteredAnimals.add(animal);
            }
        }
        return filteredAnimals;
    }

    private void displayAnimals(List<Map<String, String>> animals) {
        adapter.updateList(animals);
        String total = String.valueOf(animals.size());
        totalAnimal.setText(getString(R.string.total_pashu, total));
    }

    private void changeButtonAndTextColor(LinearLayout selectedButton, TextView text) {
        selectedButton.setBackgroundColor(getResources().getColor(R.color.LogoBlue));
        ((TextView) text).setTextColor(getResources().getColor(R.color.white));
    }

    private void resetBackButtonColors(LinearLayout selectedLayout) {
        if (selectedLayout == mainActivityInstance.button2) {
            mainActivityInstance.button2.setBackgroundColor(getResources().getColor(android.R.color.white));
            ((TextView) mainActivityInstance.btn2txt).setTextColor(getResources().getColor(android.R.color.black));
        }

        if (selectedLayout == mainActivityInstance.button3) {
            mainActivityInstance.button3.setBackgroundColor(getResources().getColor(android.R.color.white));
            ((TextView) mainActivityInstance.btn3txt).setTextColor(getResources().getColor(android.R.color.black));
        }
    }

    private void resetOtherButtonColors(LinearLayout selecteLayout) {
        if (selecteLayout != mainActivityInstance.button1) {
            mainActivityInstance.button1.setBackgroundColor(getResources().getColor(android.R.color.white));
            ((TextView) mainActivityInstance.btn1txt).setTextColor(getResources().getColor(android.R.color.black));
        }
        if (selecteLayout != mainActivityInstance.button2) {
            mainActivityInstance.button2.setBackgroundColor(getResources().getColor(android.R.color.white));
            ((TextView) mainActivityInstance.btn2txt).setTextColor(getResources().getColor(android.R.color.black));
        }
        if (selecteLayout != mainActivityInstance.button3) {
            mainActivityInstance.button3.setBackgroundColor(getResources().getColor(android.R.color.white));
            ((TextView) mainActivityInstance.btn3txt).setTextColor(getResources().getColor(android.R.color.black));
        }
    }

    public void detailsHistory() {
        dialog = new ProgressDialog(getActivity());
        dialog.setMessage(getString(R.string.getting_data));
        dialog.setCancelable(false);
        dialog.show();
        String url = GET_ANIMAL_LIST;
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            mainActivityInstance.swipeRefreshLayout.setRefreshing(false);
                            dialog.dismiss();
                            JSONObject jsonObject = new JSONObject(response);
                            Log.d("API_Response", "Response: " + response);
                            if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                                dialog.dismiss();
                                JSONArray dataArray = jsonObject.getJSONArray("data");
                                animalData.clear();
                                animalDataSearch.clear();
                                int matchCount = 0;
                                isMobileMatched = false;
                                for (int i = 0; i < dataArray.length(); i++) {
                                    JSONObject animalObj = dataArray.getJSONObject(i);
                                    String animalId = animalObj.optString("id", null);
                                    String UserId = animalObj.optString("userid", null);
                                    String animalNo = animalObj.optString("number", null);
                                    String type = animalObj.optString("type", null);
                                    String byaatCount = animalObj.optString("byat_count", null);
                                    String milkCount = animalObj.optString("milk", null);
                                    String animalCaste = animalObj.optString("animal_caste", null);
                                    String kisanNumberFromApi = animalObj.optString("kisan_number", null);
                                    String latest_byat_registration = animalObj.optString("last_byat_addition_date", null);
                                    String latest_doodh_registration = animalObj.optString("last_doodh_addition_date", null);
                                    strkisanNumberFromApi = kisanNumberFromApi;
                                    Log.d("strkisanNumberFromApi_one", "" + strkisanNumberFromApi);

                                    mainActivityInstance.btn1txt.setText(R.string.str_doodh_leaderboard);
                                    mainActivityInstance.btn2txt.setText(R.string.str_filter);
                                    mainActivityInstance.btn3txt.setText(R.string.str_search);

                                    if (mobileNumber.equals(kisanNumberFromApi)) {
                                        isMobileMatched = true;
                                        Map<String, String> animalInfo = new HashMap<>();
                                        animalInfo.put("animalId", animalId);
                                        animalInfo.put("userId", UserId);
                                        animalInfo.put("animalNo", animalNo);
                                        animalInfo.put("type", type);
                                        animalInfo.put("byaatCount", byaatCount);
                                        animalInfo.put("MilkData", milkCount);
                                        animalInfo.put("animalCaste", animalCaste);
                                        animalInfo.put("date", animalCaste);
                                        animalInfo.put("kisanNumber", kisanNumberFromApi);
                                        animalInfo.put("lastbyat", latest_byat_registration);
                                        animalInfo.put("lastMilk", latest_doodh_registration);
                                        animalData.add(animalInfo);
                                        matchCount++;
                                    } else {
                                        isMobileMatched = false;
                                        Map<String, String> animalInfo = new HashMap<>();
                                        animalInfo.put("animalId", animalId);
                                        animalInfo.put("userId", UserId);
                                        animalInfo.put("animalNo", animalNo);
                                        animalInfo.put("type", type);
                                        animalInfo.put("byaatCount", byaatCount);
                                        animalInfo.put("MilkData", milkCount);
                                        animalInfo.put("animalCaste", animalCaste);
                                        animalInfo.put("date", animalCaste);
                                        animalInfo.put("kisanNumber", kisanNumberFromApi);
                                        animalInfo.put("lastbyat", latest_byat_registration);
                                        animalInfo.put("lastMilk", latest_doodh_registration);
                                        animalDataSearch.add(animalInfo);
                                        matchCount++;
                                    }
                                }
                                if (matchCount > 0) {
                                    mainActivityInstance.button1.setVisibility(VISIBLE);
                                    recyclerView.setVisibility(VISIBLE);
                                    id_data_not_found.setVisibility(View.GONE);
                                    shadowKisanNumber.setVisibility(GONE);
                                    kisanNumber.setVisibility(VISIBLE);
                                    MobileNumber = mobileNumber;
                                    kisanNumber.setText(getString(R.string.kisan_number, mobileNumber));
                                    adapter.updateList(animalData);
                                    adapter.notifyDataSetChanged();
                                    displayAnimals(animalData);
                                } else {
                                    recyclerView.setVisibility(GONE);
                                    id_data_not_found.setVisibility(VISIBLE);
                                    shadowKisanNumber.setVisibility(GONE);
                                    kisanNumber.setVisibility(VISIBLE);
                                    MobileNumber = mobileNumber;
                                    kisanNumber.setText(getString(R.string.kisan_number, mobileNumber));
                                }
                            } else {
                                mainActivityInstance.button1.setVisibility(VISIBLE);
                                dialog.dismiss();
                                recyclerView.setVisibility(GONE);
                                id_data_not_found.setVisibility(VISIBLE);
                                kisanNumber.setVisibility(GONE);
                            }

                        } catch (JSONException e) {
                            mainActivityInstance.swipeRefreshLayout.setRefreshing(false);
                            dialog.dismiss();
                            if (e instanceof JSONException) {
                                Toast.makeText(getContext(), R.string.json_parsing_error, Toast.LENGTH_SHORT).show();
                            } else {
                                Log.d("CheckException", "" + e);
                                Toast.makeText(getContext(), R.string.generic_error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mainActivityInstance.swipeRefreshLayout.setRefreshing(false);
                        dialog.dismiss();
                        if (error instanceof TimeoutError) {
                            Toast.makeText(getContext(), R.string.timeout_error, Toast.LENGTH_SHORT).show();
                        } else if (error instanceof NoConnectionError) {
                            Toast.makeText(getContext(), R.string.no_connection_error, Toast.LENGTH_SHORT).show();
                        } else if (error instanceof AuthFailureError) {
                            Toast.makeText(getContext(), R.string.auth_failure_error, Toast.LENGTH_SHORT).show();
                        } else if (error instanceof ServerError) {
                            Log.d("ServerError", "" + error);
                            Toast.makeText(getContext(), R.string.server_error, Toast.LENGTH_SHORT).show();
                        } else if (error instanceof NetworkError) {
                            Toast.makeText(getContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
                        } else if (error instanceof ParseError) {
                            Toast.makeText(getContext(), R.string.parse_error, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), R.string.unknown_error, Toast.LENGTH_SHORT).show();
                        }
                        Log.d("Check", "" + error.getMessage());
                    }
                });
        postRequest.setRetryPolicy(ApiUtils.DEFAULT_RETRY_POLICY);
        requestQueue.add(postRequest);
    }

    private void searchDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.lyt_forget_password, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);

        LinearLayout password_layout = dialogView.findViewById(R.id.password_layout);
        LinearLayout mobile_layout = dialogView.findViewById(R.id.mobileLayout);
        LinearLayout pashu_layout = dialogView.findViewById(R.id.pashuNumber);
        EditText animalNumber = dialogView.findViewById(R.id.AnimalNumber);
        TextView textGetPassword = dialogView.findViewById(R.id.textGetPassword);
        TextView textCancel = dialogView.findViewById(R.id.textCancel);
        TextView textSearch = dialogView.findViewById(R.id.searchTxt);

        pashu_layout.setVisibility(View.VISIBLE);
        textSearch.setVisibility(View.VISIBLE);
        mobile_layout.setVisibility(View.GONE);
        password_layout.setVisibility(View.GONE);
        textCancel.setVisibility(VISIBLE);
        textGetPassword.setVisibility(View.GONE);

        dialogView.findViewById(R.id.textCancel).setOnClickListener(v -> {
            dialog.dismiss();
            resetBackButtonColors(mainActivityInstance.button2);
            resetOtherButtonColors(mainActivityInstance.button2);
        });

        dialogView.findViewById(R.id.searchTxt).setOnClickListener(v -> {
            bySearch = true;
            resetBackButtonColors(mainActivityInstance.button2);
            resetOtherButtonColors(mainActivityInstance.button2);
            changeButtonColor(allLayout);
            resetButtonColor(cowLayout);
            resetButtonColor(buffaloLayout);

            String enteredAnimalNumber = animalNumber.getText().toString();
            Log.d("InthisBlock", "Entered Animal Number: " + enteredAnimalNumber);

            if (enteredAnimalNumber.isEmpty()) {
                animalNumber.setError(getString(R.string.error_add_pashu_number));
            } else {
                List<Map<String, String>> filteredList = new ArrayList<>();
                boolean foundInSearch = false;
                for (Map<String, String> animalInfo : animalDataSearch) {
                    String animalNo = animalInfo.get("animalNo");
                    String Mobile = animalInfo.get("kisanNumber");
                    Log.d("InthatBlock", "Animal Number: " + animalNo);
                    if (animalNo != null && animalNo.equals(enteredAnimalNumber)) {
                        filteredList.add(animalInfo);
                        shadowKisanNumber.setVisibility(VISIBLE);
                        kisanNumber.setVisibility(GONE);
                        kisanNumber.setText(getString(R.string.kisan_number, ""));
                        shadowKisanNumber.setText(getString(R.string.kisan_number, Mobile));
                        MobileNumber = "";
                        PashuNumber = enteredAnimalNumber;
                        mainActivityInstance.button1.setVisibility(GONE);
                        foundInSearch = true;
                        break;
                    }
                }

                // If not found in animalDataSearch, check animalData
                if (!foundInSearch) {
                    for (Map<String, String> animalInfo : animalData) {
                        String animalNo = animalInfo.get("animalNo");
                        String Mobile = animalInfo.get("kisanNumber");
                        Log.d("InthatBlock", "Animal Number: " + animalNo);
                        if (animalNo != null && animalNo.equals(enteredAnimalNumber)) {
                            filteredList.add(animalInfo);
                            shadowKisanNumber.setVisibility(VISIBLE);
                            kisanNumber.setVisibility(GONE);
                            kisanNumber.setText(getString(R.string.kisan_number, ""));
                            shadowKisanNumber.setText(getString(R.string.kisan_number, Mobile));
                            MobileNumber = "";
                            PashuNumber = enteredAnimalNumber;
                            mainActivityInstance.button1.setVisibility(GONE);
                            break;
                        }
                    }
                }

                if (filteredList.isEmpty()) {
                    Toast.makeText(getContext(), R.string.str_animal_not_found, Toast.LENGTH_SHORT).show();
                } else {
                    adapter.updateList(filteredList);
                    adapter.notifyDataSetChanged();
                    displayAnimals(filteredList);
                }
                dialog.dismiss();
                resetBackButtonColors(mainActivityInstance.button3);
                resetOtherButtonColors(mainActivityInstance.button3);
            }
        });
        dialog.show();
    }
}


