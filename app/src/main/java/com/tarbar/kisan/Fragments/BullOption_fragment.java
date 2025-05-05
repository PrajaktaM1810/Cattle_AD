package com.tarbar.kisan.Fragments;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.tarbar.kisan.Helper.constant.GET_ANIMAL_LIST;
import static com.tarbar.kisan.Helper.constant.GET_BULL_LIST;

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
import com.tarbar.kisan.Activities.Checking;
import com.tarbar.kisan.Activities.LoadFilterFragments;
import com.tarbar.kisan.Activities.LoadFragments;
import com.tarbar.kisan.Activities.MainActivity;
import com.tarbar.kisan.Adapter.Bull_List_Adapter;
import com.tarbar.kisan.Adapter.Pashu_List_Adapter;
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

public class BullOption_fragment extends Fragment {
    private SharedPreferenceManager sharedPrefMgr;
    ProgressDialog dialog;
    String mobileNumber;
    LinearLayout allLayout, cowLayout, buffaloLayout;
    TextView allLTxt,cowTxt,buffaloTxt;
    TextView txt_all, txt_cow, txt_buffalo, totalAnimal, id_data_not_found;
    RecyclerView recyclerView;
    Bull_List_Adapter adapter;
    List<Map<String, String>> bullData = new ArrayList<>();
    List<Map<String, String>> bullDataSearch = new ArrayList<>();
    List<Map<String, String>> filteredData = new ArrayList<>();
    private LoadFilterFragments loadFilterFragments;
    boolean isAllSelected = false, isCowSelected = false, isBuffaloSelected = false, bySearch = false;
    private String selectedLayout = "";
    String PashuNumber;;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bull_option, container, false);
        view.setBackgroundColor(Color.WHITE);

        loadFilterFragments = (LoadFilterFragments) getActivity();
        allLayout = view.findViewById(R.id.allLayout);
        cowLayout = view.findViewById(R.id.cowLayout);
        buffaloLayout = view.findViewById(R.id.buffaloLayout);
        allLTxt = view.findViewById(R.id.allLTxt);
        cowTxt = view.findViewById(R.id.cowTxt);
        buffaloTxt = view.findViewById(R.id.buffaloTxt);

        totalAnimal = view.findViewById(R.id.totalAnimal);
        id_data_not_found = view.findViewById(R.id.id_data_not_found);

        recyclerView = view.findViewById(R.id.recyclerView);
        adapter = new Bull_List_Adapter(getContext(), filteredData);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        bullList();

        allLTxt.setText(R.string.str_sabhi);
        cowTxt.setText(R.string.str_sand);
        buffaloTxt.setText(R.string.str_bhaisa);

        allLayout.setOnClickListener(v -> {
            Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.click_animation);
            v.startAnimation(animation);
            toggleSelection(v, 1);
            if (bySearch == true){
                filterAnimalsBySearch("", PashuNumber,"");
            } else {
                filterAnimalsByTypeAndMobileNumber("", "");
            }
        });

        cowLayout.setOnClickListener(v -> {
            Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.click_animation);
            v.startAnimation(animation);
            toggleSelection(v, 2);
            if (bySearch == true){
                filterAnimalsBySearch("सांड",PashuNumber, "");
            } else {
                filterAnimalsByTypeAndMobileNumber("सांड", "");
            }
        });

        buffaloLayout.setOnClickListener(v -> {
            Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.click_animation);
            v.startAnimation(animation);
            toggleSelection(v, 3);
            if (bySearch == true) {
                filterAnimalsBySearch("भैसा", PashuNumber,"");
            } else {
                filterAnimalsByTypeAndMobileNumber("भैसा", "");
            }
        });

        if (loadFilterFragments != null && loadFilterFragments.cardView != null) {
            loadFilterFragments.cardView.setVisibility(VISIBLE);
            loadFilterFragments.button1.setVisibility(GONE);

            loadFilterFragments.btn2txt.setText(R.string.str_filter);
            loadFilterFragments.btn3txt.setText(R.string.str_search);

            loadFilterFragments.swipeRefreshLayout.setOnRefreshListener(() -> {
                bySearch = false;
                bullList();
                changeButtonColor(allLayout);
                resetButtonColor(cowLayout);
                resetButtonColor(buffaloLayout);
                loadFilterFragments.button2.setVisibility(GONE);
            });

            loadFilterFragments.back.setOnClickListener(new View.OnClickListener() {
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

            loadFilterFragments.button2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.click_animation);
                    v.startAnimation(animation);
                    changeButtonAndTextColor(loadFilterFragments.button2, loadFilterFragments.btn2txt);
                    resetOtherButtonColors(loadFilterFragments.button2);
                    String selectedLayout = getSelectedLayout();
                    Map<String, Integer> optionImageMap = new LinkedHashMap<>();
                    switch (selectedLayout) {
                        case "सांड":
                            optionImageMap.put("गिर सांड", R.drawable.gir_cow);
                            optionImageMap.put("साहीवाल सांड", R.drawable.sahival_cow);
                            break;
                        case "भैसा":
                            optionImageMap.put("मुर्रा भैसा", R.drawable.murrah_buffalo_image);
                            break;
                    }
                    showRadioButtonPopup(optionImageMap, selectedLayout);
                }
            });

            loadFilterFragments.button3.setOnClickListener(v -> {
                Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.click_animation);
                v.startAnimation(animation);
                changeButtonAndTextColor(loadFilterFragments.button3, loadFilterFragments.btn3txt);
                resetOtherButtonColors(loadFilterFragments.button3);
                searchDialog();
            });
        }
        toggleSelection(allLayout, 1);
        return view;
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
            resetBackButtonColors(loadFilterFragments.button2);
            resetOtherButtonColors(loadFilterFragments.button2);
            List<String> selectedOptions = new ArrayList<>();
            for (int i = 0; i < checkBoxes.length; i++) {
                if (checkBoxes[i].isChecked()) {
                    selectedOptions.add(optionImageMap.keySet().toArray(new String[0])[i]);
                }
            }

            for (String selectedOption : selectedOptions) {
                if (bySearch) {
                    filterAnimalsBySearch(selectedLayout, PashuNumber,selectedOption);
                } else {
                    filterAnimalsByTypeAndMobileNumber(selectedLayout, selectedOption);
                }
            }
        });

        builder.setNegativeButton(R.string.cancel, (dialog, which) -> {
            dialog.dismiss();
            resetBackButtonColors(loadFilterFragments.button2);
            resetOtherButtonColors(loadFilterFragments.button2);
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
                filterAnimalsByTypeAndMobileNumber("", "");
            }
            loadFilterFragments.button2.setVisibility(View.GONE);
        } else if (buttonType == 2) {
            isCowSelected = true;
            isAllSelected = false;
            isBuffaloSelected = false;
            selectedLayout = "सांड";
            changeButtonColor(cowLayout);
            if (bySearch == true){
                filterAnimalsBySearch("सांड",PashuNumber, "");
            } else {
                filterAnimalsByTypeAndMobileNumber("सांड", "");
            }
            loadFilterFragments.button2.setVisibility(VISIBLE);
        } else if (buttonType == 3) {
            isBuffaloSelected = true;
            isAllSelected = false;
            isCowSelected = false;
            selectedLayout = "भैसा";
            changeButtonColor(buffaloLayout);
            if (bySearch == true){
                filterAnimalsBySearch("भैसा",PashuNumber, "");
            } else {
                filterAnimalsByTypeAndMobileNumber("भैसा", "");
            }
            loadFilterFragments.button2.setVisibility(VISIBLE);
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

    private void filterAnimalsBySearch(String type, String PashuNumber,String caste) {
        List<Map<String, String>> filteredAnimals;
        if (type != null && !type.isEmpty() && caste != null && !caste.isEmpty()) {
            filteredAnimals = getFilteredAnimalsBySearch(type,PashuNumber, caste);
        } else if (type != null && !type.isEmpty()) {
            filteredAnimals = getFilteredAnimalsBySearch(type,PashuNumber, "");
        } else if (caste != null && !caste.isEmpty()) {
            filteredAnimals = getFilteredAnimalsBySearch("",PashuNumber, caste);
        } else {
            filteredAnimals = getFilteredAnimalsBySearch("",PashuNumber, "");
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

    private void filterAnimalsByTypeAndMobileNumber(String type, String caste) {
        List<Map<String, String>> filteredAnimals;
        if (type != null && !type.isEmpty() && caste != null && !caste.isEmpty()) {
            filteredAnimals = getFilteredAnimals(type, caste);
        } else if (type != null && !type.isEmpty()) {
            filteredAnimals = getFilteredAnimals(type, "");
        } else if (caste != null && !caste.isEmpty()) {
            filteredAnimals = getFilteredAnimals("", caste);
        } else {
            filteredAnimals = getFilteredAnimals("", "");
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
        for (Map<String, String> animalSearch : bullDataSearch) {
            boolean typeMatch = (type == null || type.isEmpty()) || type.equals(animalSearch.get("bullType"));
            boolean pashu = (pashunumber == null || pashunumber.isEmpty()) || pashunumber.equals(animalSearch.get("animalNo"));
            boolean casteMatch = (caste == null || caste.isEmpty()) || caste.equals(animalSearch.get("bullCaste"));
            if (typeMatch && pashu && casteMatch) {
                filteredAnimals.add(animalSearch);
            }
        }
        if (filteredAnimals.isEmpty()) {
            for (Map<String, String> animalSearch : bullData) {
                boolean typeMatch = (type == null || type.isEmpty()) || type.equals(animalSearch.get("bullType"));
                boolean pashu = (pashunumber == null || pashunumber.isEmpty()) || pashunumber.equals(animalSearch.get("animalNo"));
                boolean casteMatch = (caste == null || caste.isEmpty()) || caste.equals(animalSearch.get("bullCaste"));
                if (typeMatch && pashu && casteMatch) {
                    filteredAnimals.add(animalSearch);
                }
            }
        }
        return filteredAnimals;
    }

    private List<Map<String, String>> getFilteredAnimals(String type,String caste) {
        List<Map<String, String>> filteredAnimals = new ArrayList<>();
        for (Map<String, String> animal : bullData) {
            boolean typeMatch = type.isEmpty() || type.equals(animal.get("bullType"));
            boolean casteMatch = caste.isEmpty() || caste.equals(animal.get("bullCaste"));
            if (typeMatch && casteMatch) {
                filteredAnimals.add(animal);
            }
        }
        return filteredAnimals;
    }

    private void displayAnimals(List<Map<String, String>> animals) {
        adapter.updateList(animals);
        String total = String.valueOf(animals.size());
        totalAnimal.setText(getString(R.string.total_bull, total));
    }

    private void changeButtonAndTextColor(LinearLayout selectedButton, TextView text) {
        selectedButton.setBackgroundColor(getResources().getColor(R.color.LogoBlue));
        ((TextView) text).setTextColor(getResources().getColor(R.color.white));
    }

    private void resetBackButtonColors(LinearLayout selectedLayout) {
        if (selectedLayout == loadFilterFragments.button2) {
            loadFilterFragments.button2.setBackgroundColor(getResources().getColor(android.R.color.white));
            ((TextView) loadFilterFragments.btn2txt).setTextColor(getResources().getColor(android.R.color.black));
        }

        if (selectedLayout == loadFilterFragments.button3) {
            loadFilterFragments.button3.setBackgroundColor(getResources().getColor(android.R.color.white));
            ((TextView) loadFilterFragments.btn3txt).setTextColor(getResources().getColor(android.R.color.black));
        }
    }

    private void resetOtherButtonColors(LinearLayout selecteLayout) {
        if (selecteLayout != loadFilterFragments.button2) {
            loadFilterFragments.button2.setBackgroundColor(getResources().getColor(android.R.color.white));
            ((TextView) loadFilterFragments.btn2txt).setTextColor(getResources().getColor(android.R.color.black));
        }
        if (selecteLayout != loadFilterFragments.button3) {
            loadFilterFragments.button3.setBackgroundColor(getResources().getColor(android.R.color.white));
            ((TextView) loadFilterFragments.btn3txt).setTextColor(getResources().getColor(android.R.color.black));
        }
    }

    public void bullList() {
        dialog = new ProgressDialog(getActivity());
        dialog.setMessage(getString(R.string.getting_data));
        dialog.setCancelable(false);
        dialog.show();

        String url = GET_BULL_LIST;
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());

        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        loadFilterFragments.swipeRefreshLayout.setRefreshing(false);
                        dialog.dismiss();

                        JSONObject jsonObject = new JSONObject(response);
                        Log.d("API_Response", "Response: " + response);

                        if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                            JSONArray dataArray = jsonObject.getJSONArray("data");
                            bullData.clear();
                            bullDataSearch.clear();

                            for (int i = 0; i < dataArray.length(); i++) {
                                JSONObject animalObj = dataArray.getJSONObject(i);

                                Map<String, String> animalInfo = new HashMap<>();
                                animalInfo.put("animalId", animalObj.optString("id", ""));
                                animalInfo.put("animalNo", animalObj.optString("number", ""));
                                animalInfo.put("dob", animalObj.optString("dob", ""));
                                animalInfo.put("bullPic", animalObj.optString("bull_pic", ""));
                                animalInfo.put("pidhiTwo", animalObj.optString("pidhi_two_number", ""));
                                animalInfo.put("pidhiThree", animalObj.optString("pidhi_three_number", ""));
                                animalInfo.put("pidhiFour", animalObj.optString("pidhi_four_number", ""));
                                animalInfo.put("pidhiFive", animalObj.optString("pidhi_five_number", ""));
                                animalInfo.put("pidhiSix", animalObj.optString("pidhi_six_number", ""));
                                animalInfo.put("bullType", animalObj.optString("bull_type", ""));
                                animalInfo.put("bullCaste", animalObj.optString("bull_caste", ""));
                                animalInfo.put("bullMotherNumber", animalObj.optString("bull_mother_number", ""));
                                animalInfo.put("mother1DayMilkQty", animalObj.optString("mother_1_day_milk_qty", ""));
                                animalInfo.put("mother305DaysMilkQty", animalObj.optString("mother_305_days_milk_qty", ""));

                                bullData.add(animalInfo);
                                bullDataSearch.add(animalInfo);
                            }

                            displayAnimals(bullData);
                            adapter.updateList(bullData);
                            adapter.notifyDataSetChanged();

                            if (!bullData.isEmpty()) {
                                recyclerView.setVisibility(View.VISIBLE);
                                id_data_not_found.setVisibility(View.GONE);
                            } else {
                                recyclerView.setVisibility(View.GONE);
                                id_data_not_found.setVisibility(View.VISIBLE);
                            }
                        } else {
                            recyclerView.setVisibility(View.GONE);
                            id_data_not_found.setVisibility(View.VISIBLE);
                        }
                    } catch (JSONException e) {
                        dialog.dismiss();
                        Log.e("JSON_ERROR", "Error: ", e);
                        Toast.makeText(getContext(), R.string.json_parsing_error, Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    dialog.dismiss();
                    loadFilterFragments.swipeRefreshLayout.setRefreshing(false);
                    Log.e("VolleyError", "Error: " + error.getMessage());
                    Toast.makeText(getContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
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
        LinearLayout bullNumberLyt = dialogView.findViewById(R.id.bullNumberLyt);
        EditText animalNumber = dialogView.findViewById(R.id.AnimalNumber);
        EditText bullNumber = dialogView.findViewById(R.id.bullNumber);
        TextView textGetPassword = dialogView.findViewById(R.id.textGetPassword);
        TextView textCancel = dialogView.findViewById(R.id.textCancel);
        TextView textSearch = dialogView.findViewById(R.id.searchTxt);

        bullNumberLyt.setVisibility(VISIBLE);
        pashu_layout.setVisibility(View.GONE);
        textSearch.setVisibility(View.VISIBLE);
        mobile_layout.setVisibility(View.GONE);
        password_layout.setVisibility(View.GONE);
        textCancel.setVisibility(VISIBLE);
        textGetPassword.setVisibility(View.GONE);

        dialogView.findViewById(R.id.textCancel).setOnClickListener(v -> {
            dialog.dismiss();
            resetBackButtonColors(loadFilterFragments.button2);
            resetOtherButtonColors(loadFilterFragments.button2);
        });

        dialogView.findViewById(R.id.searchTxt).setOnClickListener(v -> {
            bySearch = true;
            resetBackButtonColors(loadFilterFragments.button2);
            resetOtherButtonColors(loadFilterFragments.button2);
            changeButtonColor(allLayout);
            resetButtonColor(cowLayout);
            resetButtonColor(buffaloLayout);

            String enteredAnimalNumber = bullNumber.getText().toString();
            Log.d("InthisBlock", "Entered Bull Number: " + enteredAnimalNumber);

            if (enteredAnimalNumber.isEmpty()) {
                bullNumber.setError(getString(R.string.title_add_bull_number));
            } else {
                List<Map<String, String>> filteredList = new ArrayList<>();
                boolean foundInSearch = false;
                for (Map<String, String> animalInfo : bullDataSearch) {
                    String animalNo = animalInfo.get("animalNo");
                    Log.d("InthatBlock", "Bull Number: " + animalNo);
                    if (animalNo != null && animalNo.equals(enteredAnimalNumber)) {
                        filteredList.add(animalInfo);
                        PashuNumber = enteredAnimalNumber;
                        foundInSearch = true;
                        break;
                    }
                }

                // If not found in animalDataSearch, check animalData
                if (!foundInSearch) {
                    for (Map<String, String> animalInfo : bullData) {
                        String animalNo = animalInfo.get("animalNo");
                        Log.d("InthatBlock", "Animal Number: " + animalNo);
                        if (animalNo != null && animalNo.equals(enteredAnimalNumber)) {
                            filteredList.add(animalInfo);
                            PashuNumber = enteredAnimalNumber;
                            break;
                        }
                    }
                }

                if (filteredList.isEmpty()) {
                    Toast.makeText(getContext(), R.string.str_bull_not_found, Toast.LENGTH_SHORT).show();
                } else {
                    adapter.updateList(filteredList);
                    adapter.notifyDataSetChanged();
                    displayAnimals(filteredList);
                }
                dialog.dismiss();
                resetBackButtonColors(loadFilterFragments.button3);
                resetOtherButtonColors(loadFilterFragments.button3);
                loadFilterFragments.button2.setVisibility(GONE);
            }
        });
        dialog.show();
    }
}


