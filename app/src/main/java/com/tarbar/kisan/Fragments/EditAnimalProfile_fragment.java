package com.tarbar.kisan.Fragments;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.tarbar.kisan.Helper.constant.DELETE_ANIMAL;
import static com.tarbar.kisan.Helper.constant.GET_ANIMAL_DATA;
import static com.tarbar.kisan.Helper.constant.UPDATE_ANIMAL;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.tarbar.kisan.Activities.Checking;
import com.tarbar.kisan.Activities.LoginActivity;
import com.tarbar.kisan.Activities.MainActivity;
import com.tarbar.kisan.Helper.ApiUtils;
import com.tarbar.kisan.Helper.Iconstant;
import com.tarbar.kisan.Helper.SharedPreferenceManager;
import com.tarbar.kisan.Helper.constant;
import com.tarbar.kisan.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditAnimalProfile_fragment extends Fragment {
    SharedPreferenceManager sharedPrefMgr;
    EditText AnimalNumber, AnimalType, AnimalGender, ByatNumber, AnimalBirthdate, KisanMobileNumber, KisanPassword;
    EditText KisanName,KisanFathername,KisanCaste,KisanState,KisanJilha,KisanTahsil,KisanVillage;
    EditText MotherNumber, FatherNumber;
    Spinner AnimalJati;
    ImageView cowImg, buffaloImg, back;
    String MobileNumber,userid,Password,AnimalId,kisanNumber;
    EditText Pidhi2Number,Pidhi3Number,Pidhi4Number,Pidhi5Number,Pidhi6Number;
    ProgressDialog dialog;
    Button submit;
    int arrayId;
    ImageView calenderImg;
    Button DeleteAnimal;
    LinearLayout PasswordLayout;
    TextView passwordtxt;
    String formattedDate;
    String number,Pidhi2number,Pidhi3number,Pidhi4number,Pidhi5number,Pidhi6number;
    private List<JSONObject> bullList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_animal_profile, container, false);
        view.setBackgroundColor(Color.WHITE);
        initViews(view);

        AnimalId = getActivity().getIntent().getStringExtra("animalId");

        sharedPrefMgr = new SharedPreferenceManager(requireContext());
        sharedPrefMgr.connectDB();
        userid = sharedPrefMgr.getString(Iconstant.userid);
        MobileNumber = sharedPrefMgr.getString(Iconstant.mobile);
        Password = sharedPrefMgr.getString(Iconstant.password);
        sharedPrefMgr.closeDB();

        getBullNumbers();

        if (AnimalId != null) {
            getAnimalDetails(AnimalId);
        } else {
            Log.d("ViewAnimalProfile_AnimalId", ""+AnimalId);
        }

//        KisanMobileNumber.setText(MobileNumber);
        return view;
    }

    private void initViews(View view) {
        AnimalNumber = view.findViewById(R.id.AnimalNumber);
        AnimalType = view.findViewById(R.id.AnimalType);
        AnimalGender = view.findViewById(R.id.AnimalGender);
        ByatNumber = view.findViewById(R.id.ByatNumber);
        AnimalBirthdate = view.findViewById(R.id.AnimalBirthdate);
        KisanMobileNumber = view.findViewById(R.id.KisanMobileNumber);
        KisanPassword = view.findViewById(R.id.KisanPassword);
        AnimalJati = view.findViewById(R.id.AnimalJati);
        buffaloImg = view.findViewById(R.id.buffaloImg);
        cowImg = view.findViewById(R.id.cowImg);
        back = view.findViewById(R.id.back);
        submit = view.findViewById(R.id.submit);
        calenderImg = view.findViewById(R.id.calenderImg);
        DeleteAnimal = view.findViewById(R.id.DeleteAnimal);
        passwordtxt = view.findViewById(R.id.passwordtxt);
        PasswordLayout = view.findViewById(R.id.PasswordLayout);

        KisanName = view.findViewById(R.id.KisanName);
        KisanFathername = view.findViewById(R.id.KisanFathername);
        KisanCaste = view.findViewById(R.id.KisanCaste);
        KisanState = view.findViewById(R.id.KisanState);
        KisanJilha = view.findViewById(R.id.KisanJilha);
        KisanTahsil = view.findViewById(R.id.KisanTahsil);
        KisanVillage = view.findViewById(R.id.KisanVillage);
        MotherNumber = view.findViewById(R.id.MotherNumber);
        FatherNumber = view.findViewById(R.id.FatherNumber);

        Pidhi2Number = view.findViewById(R.id.Pidhi2Number);
        Pidhi3Number = view.findViewById(R.id.Pidhi3Number);
        Pidhi4Number = view.findViewById(R.id.Pidhi4Number);
        Pidhi5Number = view.findViewById(R.id.Pidhi5Number);
        Pidhi6Number = view.findViewById(R.id.Pidhi6Number);

        AnimalBirthdate.setFocusable(false);
        AnimalBirthdate.setClickable(true);

        DeleteAnimal.setOnClickListener(v -> {
            Animation animation = AnimationUtils.loadAnimation(requireContext(), R.anim.click_animation);
            v.startAnimation(animation);
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(getString(R.string.confirm_delete_byat));
            builder.setNegativeButton(getString(R.string.no), null);
            builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface arg0, int arg1) {
                    arg0.dismiss();
                    messageDialog();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
            alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
            alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
        });

        cowImg.setOnClickListener(v -> {
            Animation animation = AnimationUtils.loadAnimation(requireContext(), R.anim.click_animation);
            v.startAnimation(animation);
            AnimalType.setText("गाय");
        });

        buffaloImg.setOnClickListener(v -> {
            Animation animation = AnimationUtils.loadAnimation(requireContext(), R.anim.click_animation);
            v.startAnimation(animation);
            AnimalType.setText("भैंस");
        });

        AnimalType.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateAnimalJatiSpinner(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        back.setOnClickListener(v -> {
            Animation animation = AnimationUtils.loadAnimation(requireContext(), R.anim.click_animation);
            v.startAnimation(animation);
            Intent intent = new Intent(requireContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);

            if (getActivity() != null) {
                getActivity().finish();
            }
        });

        calenderImg.setOnClickListener(v -> showDatePickerDialog());

        AnimalBirthdate.setOnClickListener(v -> showDatePickerDialog());

        submit.setOnClickListener(v -> {
            Animation animation = AnimationUtils.loadAnimation(requireContext(), R.anim.click_animation);
            v.startAnimation(animation);

            String strAnimalNumber = AnimalNumber.getText().toString();
            String selectedJati = AnimalJati.getSelectedItem().toString();
            String strAnimalByat = ByatNumber.getText().toString();
            String strAnimalBirthday = AnimalBirthdate.getText().toString();
            String strKisanPassword = KisanPassword.getText().toString();

            if (strAnimalNumber.isEmpty()) {
                AnimalNumber.setError(getString(R.string.error_add_pashu_number));
                AnimalNumber.setFocusable(true);
            } else if (strAnimalNumber.length() < 12) {
                AnimalNumber.setError(getString(R.string.error_number_too_short));
                AnimalNumber.setFocusable(true);
            } else if (selectedJati.equals("जाति चुनें")) {
                TextView errorText = (TextView) AnimalJati.getSelectedView();
                errorText.setError(getString(R.string.hint_choose_caste));
                errorText.setTextColor(Color.BLACK);
                errorText.setText("");
            } else if (strAnimalByat.isEmpty()) {
                ByatNumber.setError(getString(R.string.error_enter_byat_number));
                ByatNumber.setFocusable(true);
            } else if (strAnimalByat.isEmpty()) {
                ByatNumber.setError(getString(R.string.error_byat_cnt));
                ByatNumber.setFocusable(true);
            } else if (strAnimalBirthday.isEmpty()) {
                AnimalBirthdate.setError(getString(R.string.error_pashu_birhtdate));
                AnimalBirthdate.setFocusable(true);
            } else if (strKisanPassword.isEmpty()) {
                KisanPassword.setError(getString(R.string.enter_password_number));
            } else if (strKisanPassword.length() < 4) {
                KisanPassword.setError(getString(R.string.enter_correct_password));
            } else if (!strKisanPassword.equals(Password)) {
                Toast.makeText(requireContext(), R.string.error_password_not_matching, Toast.LENGTH_SHORT).show();
            } else {
                UpdateAnimalAPI();
            }
        });
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
                deleteAnimal(AnimalId);
            }
        });
        dialogView.findViewById(R.id.textCancel).setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void updateAnimalJatiSpinner(String animalType) {
        if (animalType.equals("गाय")) {
            arrayId = R.array.CowTypes;
        } else if (animalType.equals("भैंस")) {
            arrayId = R.array.BuffaloTypes;
        }

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(), arrayId, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        AnimalJati.setAdapter(adapter);
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                (view, year1, monthOfYear, dayOfMonth1) -> {
                    String selectedDate = String.format("%02d/%02d/%d", dayOfMonth1, monthOfYear + 1, year1);
                    AnimalBirthdate.setText(selectedDate);
                }, year, month, dayOfMonth);
        datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
        datePickerDialog.show();
    }

    public void getBullNumbers() {
        String url = constant.GET_BULL_LIST;
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());

        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("getBullNumbers_response", response);
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            if (jsonResponse.getString("status").equals("success")) {
                                JSONArray dataArray = jsonResponse.getJSONArray("data");

                                for (int i = 0; i < dataArray.length(); i++) {
                                    JSONObject bull = dataArray.getJSONObject(i);
                                    number = bull.getString("number");

                                    Pidhi2number = bull.optString("pidhi_two_number", "");
                                    Pidhi3number = bull.optString("pidhi_three_number", "");
                                    Pidhi4number = bull.optString("pidhi_four_number", "");
                                    Pidhi5number = bull.optString("pidhi_five_number", "");
                                    Pidhi6number = bull.optString("pidhi_six_number", "");
                                }

                                FatherNumber.addTextChangedListener(new TextWatcher() {
                                    @Override
                                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                                    @Override
                                    public void onTextChanged(CharSequence s, int start, int before, int count) {}

                                    @Override
                                    public void afterTextChanged(Editable s) {
                                        String enteredNumber = s.toString().trim();
                                        if (enteredNumber.length() >= 8) {
                                            checkMatchingNumber(enteredNumber, dataArray);
                                        } else if (enteredNumber.length() == 7) {
                                            clearPidhiFields();
                                        } else if (enteredNumber.isEmpty()) {
                                            clearPidhiFields();
                                        }
                                    }
                                });
                            }    else {
                                Toast.makeText(getContext(), jsonResponse.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            if (e instanceof JSONException) {
                                Log.d("CheckError",""+e);
                                Toast.makeText(getContext(), R.string.json_parsing_error, Toast.LENGTH_SHORT).show();
                            } else {
                                Log.d("CheckException",""+e);
                                Toast.makeText(getContext(), R.string.generic_error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
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

    private void checkMatchingNumber(String enteredNumber, JSONArray dataArray) {
        String last8Digits = enteredNumber.substring(enteredNumber.length() - 8);
        boolean isMatchFound = false;

        for (int i = 0; i < dataArray.length(); i++) {
            try {
                JSONObject bull = dataArray.getJSONObject(i);
                String apiNumber = bull.getString("number");
                String apiLast8Digits = apiNumber.length() >= 8 ? apiNumber.substring(apiNumber.length() - 8) : "";

                if (last8Digits.equals(apiLast8Digits)) {
                    isMatchFound = true;
                    String pidhi2 = bull.optString("pidhi_two_number", "");
                    String pidhi3 = bull.optString("pidhi_three_number", "");
                    String pidhi4 = bull.optString("pidhi_four_number", "");
                    String pidhi5 = bull.optString("pidhi_five_number", "");
                    String pidhi6 = bull.optString("pidhi_six_number", "");

                    Pidhi2Number.setText(pidhi2 == null || pidhi2.isEmpty() ? "" : pidhi2);
                    Pidhi3Number.setText(pidhi3 == null || pidhi3.isEmpty() ? "" : pidhi3);
                    Pidhi4Number.setText(pidhi4 == null || pidhi4.isEmpty() ? "" : pidhi4);
                    Pidhi5Number.setText(pidhi5 == null || pidhi5.isEmpty() ? "" : pidhi5);
                    Pidhi6Number.setText(pidhi6 == null || pidhi6.isEmpty() ? "" : pidhi6);

                    break;
                }
            } catch (JSONException e) {
                if (e instanceof JSONException) {
                    Log.d("CheckError",""+e);
                    Toast.makeText(getContext(), R.string.json_parsing_error, Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("CheckException",""+e);
                    Toast.makeText(getContext(), R.string.generic_error, Toast.LENGTH_SHORT).show();
                }
            }
        }

        if (!isMatchFound) {
            Toast.makeText(getContext(), "इस नंबर से कोई बुल उपलब्ध नहीं", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearPidhiFields() {
        Pidhi2Number.setText("");
        Pidhi3Number.setText("");
        Pidhi4Number.setText("");
        Pidhi5Number.setText("");
        Pidhi6Number.setText("");
    }

    private void getAnimalDetails(String animalId) {
        dialog = new ProgressDialog(getContext());
        dialog.setMessage(getString(R.string.getting_data));
        dialog.setCancelable(false);
        dialog.show();
        String url = GET_ANIMAL_DATA;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        try {
                            Log.d("GetAnimalProfileResponse", "" + response);
                            JSONObject jsonObject = new JSONObject(response);

                            String status = jsonObject.getString("status");
                            String message = jsonObject.getString("message");

                            if ("success".equals(status)) {
                                JSONObject data = jsonObject.getJSONObject("data");

                                String number = data.getString("number");
                                String type = data.getString("type");
                                String byat = data.getString("byat_count");
                                String originalDate = data.getString("animal_dob");
                                try {
                                    SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd");
                                    Date date = originalFormat.parse(originalDate);
                                    SimpleDateFormat desiredFormat = new SimpleDateFormat("dd/MM/yyyy");
                                    String formattedDate = desiredFormat.format(date);
                                    AnimalBirthdate.setText(formattedDate);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                    AnimalBirthdate.setText("");
                                }
                                String animalGender = data.getString("animal_gender");
                                String animalCaste = data.getString("animal_caste");
                                Log.d("AnimalCastEE",""+animalCaste);
                                String fatherNumber = data.getString("father_number");
                                String motherNumber = data.getString("mother_number");
                                kisanNumber = data.getString("kisan_number");
                                String name = data.getString("name");
                                String fatherName = data.getString("father_name");
                                String caste = data.getString("caste");
                                String state = data.getString("state");
                                String jilha = data.getString("jilha");
                                String tahsil = data.getString("tahsil");
                                String village = data.getString("village");

                                String pidhi_two_number = data.optString("pidhi_two_number", "");
                                String pidhi_three_number = data.optString("pidhi_three_number", "");
                                String pidhi_four_number = data.optString("pidhi_four_number", "");
                                String pidhi_five_number = data.optString("pidhi_five_number", "");
                                String pidhi_six_number = data.optString("pidhi_six_number", "");

                                if (MobileNumber.equals(kisanNumber)){
                                    passwordtxt.setVisibility(VISIBLE);
                                    PasswordLayout.setVisibility(VISIBLE);
                                    DeleteAnimal.setVisibility(VISIBLE);
                                    submit.setVisibility(VISIBLE);
                                    AnimalNumber.setFocusable(true);
                                    AnimalNumber.setClickable(true);
                                    AnimalType.setFocusable(true);
                                    AnimalType.setClickable(true);
                                    AnimalGender.setFocusable(true);
                                    AnimalGender.setClickable(true);
                                    ByatNumber.setFocusable(true);
                                    ByatNumber.setClickable(true);
                                    AnimalBirthdate.setFocusable(true);
                                    AnimalBirthdate.setClickable(true);
                                    KisanMobileNumber.setFocusable(true);
                                    KisanMobileNumber.setClickable(true);
                                    KisanPassword.setFocusable(true);
                                    KisanPassword.setClickable(true);
                                    KisanName.setFocusable(true);
                                    KisanName.setClickable(true);
                                    KisanFathername.setFocusable(true);
                                    KisanFathername.setClickable(true);
                                    KisanCaste.setFocusable(true);
                                    KisanCaste.setClickable(true);
                                    KisanJilha.setFocusable(true);
                                    KisanJilha.setClickable(true);
                                    KisanTahsil.setFocusable(true);
                                    KisanTahsil.setClickable(true);
                                    KisanVillage.setFocusable(true);
                                    KisanVillage.setClickable(true);
                                    MotherNumber.setFocusable(true);
                                    MotherNumber.setClickable(true);
                                    FatherNumber.setFocusable(true);
                                    FatherNumber.setClickable(true);
                                    cowImg.setClickable(true);
                                    cowImg.setEnabled(true);
                                    buffaloImg.setClickable(true);
                                    buffaloImg.setEnabled(true);
                                    calenderImg.setClickable(true);
                                    calenderImg.setEnabled(true);
                                    AnimalJati.setEnabled(true);
                                    AnimalJati.setClickable(true);
                                } else {
                                    passwordtxt.setVisibility(GONE);
                                    PasswordLayout.setVisibility(GONE);
                                    DeleteAnimal.setVisibility(GONE);
                                    submit.setVisibility(GONE);
                                    AnimalNumber.setFocusable(false);
                                    AnimalNumber.setClickable(false);
                                    AnimalType.setFocusable(false);
                                    AnimalType.setClickable(false);
                                    AnimalGender.setFocusable(false);
                                    AnimalGender.setClickable(false);
                                    ByatNumber.setFocusable(false);
                                    ByatNumber.setClickable(false);
                                    AnimalBirthdate.setFocusable(false);
                                    AnimalBirthdate.setClickable(false);
                                    KisanMobileNumber.setFocusable(false);
                                    KisanMobileNumber.setClickable(false);
                                    KisanPassword.setFocusable(false);
                                    KisanPassword.setClickable(false);
                                    KisanName.setFocusable(false);
                                    KisanName.setClickable(false);
                                    KisanFathername.setFocusable(false);
                                    KisanFathername.setClickable(false);
                                    KisanCaste.setFocusable(false);
                                    KisanCaste.setClickable(false);
                                    KisanJilha.setFocusable(false);
                                    KisanJilha.setClickable(false);
                                    KisanTahsil.setFocusable(false);
                                    KisanTahsil.setClickable(false);
                                    KisanVillage.setFocusable(false);
                                    KisanVillage.setClickable(false);
                                    MotherNumber.setFocusable(false);
                                    MotherNumber.setClickable(false);
                                    FatherNumber.setFocusable(false);
                                    FatherNumber.setClickable(false);
                                    cowImg.setClickable(false);
                                    cowImg.setEnabled(false);
                                    buffaloImg.setClickable(false);
                                    buffaloImg.setEnabled(false);
                                    calenderImg.setClickable(false);
                                    calenderImg.setEnabled(false);
                                    AnimalJati.setEnabled(false);
                                    AnimalJati.setClickable(false);
                                }

                                if (isNullOrEmpty(number)) {
                                    AnimalNumber.setText("");
                                } else {
                                    AnimalNumber.setText(number);
                                }

                                if (isNullOrEmpty(type)) {
                                    AnimalType.setText("");
                                } else {
                                    AnimalType.setText(type);
                                }

                                if (animalCaste == null || animalCaste.isEmpty()) {
                                    AnimalJati.setSelection(0);
                                } else {
                                    ArrayAdapter<String> adapter = (ArrayAdapter<String>) AnimalJati.getAdapter();
                                    int position = adapter.getPosition(animalCaste);
                                    if (position >= 0) {
                                        AnimalJati.setSelection(position);
                                    } else {
                                        AnimalJati.setSelection(0);
                                    }
                                }

                                if (isNullOrEmpty(animalGender)) {
                                    AnimalGender.setText("");
                                } else {
                                    AnimalGender.setText(animalGender);
                                }

                                if (isNullOrEmpty(byat)) {
                                    ByatNumber.setText("");
                                } else {
                                    ByatNumber.setText(byat);
                                }

                                if (isNullOrEmpty(kisanNumber)) {
                                    KisanMobileNumber.setText("");
                                } else {
                                    KisanMobileNumber.setText(kisanNumber);
                                }

                                if (isNullOrEmpty(motherNumber)) {
                                    MotherNumber.setText("");
                                } else {
                                    MotherNumber.setText(motherNumber);
                                }

                                if (isNullOrEmpty(fatherNumber)) {
                                    FatherNumber.setText("");
                                } else {
                                    FatherNumber.setText(fatherNumber);
                                }

                                if (isNullOrEmpty(name)) {
                                    KisanName.setText("");
                                } else {
                                    KisanName.setText(name);
                                }

                                if (isNullOrEmpty(fatherName)) {
                                    KisanFathername.setText("");
                                } else {
                                    KisanFathername.setText(fatherName);
                                }

                                if (isNullOrEmpty(caste)) {
                                    KisanCaste.setText("");
                                } else {
                                    KisanCaste.setText(caste);
                                }

                                if (isNullOrEmpty(state)) {
                                    KisanState.setText("");
                                } else {
                                    KisanState.setText(state);
                                }

                                if (isNullOrEmpty(jilha)) {
                                    KisanJilha.setText("");
                                } else {
                                    KisanJilha.setText(jilha);
                                }

                                if (isNullOrEmpty(tahsil)) {
                                    KisanTahsil.setText("");
                                } else {
                                    KisanTahsil.setText(tahsil);
                                }

                                if (isNullOrEmpty(village)) {
                                    KisanVillage.setText("");
                                } else {
                                    KisanVillage.setText(village);
                                }

                                if (isNullOrEmpty(pidhi_two_number)) {
                                    Pidhi2Number.setText("");
                                } else {
                                    Pidhi2Number.setText(pidhi_two_number);
                                }

                                if (isNullOrEmpty(pidhi_three_number)) {
                                    Pidhi3Number.setText("");
                                } else {
                                    Pidhi3Number.setText(pidhi_three_number);
                                }

                                if (isNullOrEmpty(pidhi_four_number)) {
                                    Pidhi4Number.setText("");
                                } else {
                                    Pidhi4Number.setText(pidhi_four_number);
                                }

                                if (isNullOrEmpty(pidhi_five_number)) {
                                    Pidhi5Number.setText("");
                                } else {
                                    Pidhi5Number.setText(pidhi_five_number);
                                }

                                if (isNullOrEmpty(pidhi_six_number)) {
                                    Pidhi6Number.setText("");
                                } else {
                                    Pidhi6Number.setText(pidhi_six_number);
                                }
                            } else {
                                Toast.makeText(requireContext(), "Error: " + message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            dialog.dismiss();
                            if (e instanceof JSONException) {
                                Log.d("CheckError",""+e);
                                Toast.makeText(getContext(), R.string.json_parsing_error, Toast.LENGTH_SHORT).show();
                            } else {
                                Log.d("CheckException",""+e);
                                Toast.makeText(getContext(), R.string.generic_error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dialog.dismiss();
                        if (error instanceof TimeoutError) {
                            Toast.makeText(getContext(), R.string.timeout_error, Toast.LENGTH_SHORT).show();
                        } else if (error instanceof NoConnectionError) {
                            Toast.makeText(getContext(), R.string.no_connection_error, Toast.LENGTH_SHORT).show();
                        } else if (error instanceof AuthFailureError) {
                            Toast.makeText(getContext(), R.string.auth_failure_error, Toast.LENGTH_SHORT).show();
                        } else if (error instanceof ServerError) {
                            Log.d("ServerError",""+error);
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
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("animalId", animalId);
                Log.d("AnimalDetailsParams", "animalId: " + animalId);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        requestQueue.add(stringRequest);
    }

    public boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty() || str.equalsIgnoreCase("null");
    }

    private void UpdateAnimalAPI() {
        dialog = new ProgressDialog(requireContext());
        dialog.setMessage(getString(R.string.getting_data));
        dialog.setCancelable(false);
        dialog.show();
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        final StringRequest postRequest = new StringRequest(Request.Method.POST, UPDATE_ANIMAL,
                response -> {
                    dialog.dismiss();
                    Log.d("UpdateAnimalAPI", "" + response);
                    try {
                        JSONObject jsonObject1 = new JSONObject(response);
                        String Status = jsonObject1.getString("status");
                        String Message = jsonObject1.getString("message");
                        if (Status.equalsIgnoreCase("success")) {
                            Intent intent = new Intent(requireContext(), Checking.class);
                            intent.putExtra("isVerified", true);
                            intent.putExtra("message", Message);
                            intent.putExtra("nextActivity", MainActivity.class);
                            intent.putExtra("SELECT_DETAILS_FRAGMENT", true);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(requireContext(), Checking.class);
                            intent.putExtra("isVerified", false);
                            intent.putExtra("message", Message);
                            intent.putExtra("nextActivity", MainActivity.class);
                            intent.putExtra("SELECT_DETAILS_FRAGMENT", true);
                            startActivity(intent);
                        }
                    } catch (JSONException e) {
                        dialog.dismiss();
                        if (e instanceof JSONException) {
                            Toast.makeText(requireContext(), R.string.json_parsing_error, Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d("CheckException",""+e);
                            Toast.makeText(requireContext(), R.string.generic_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                error -> {
                    Log.d("ServerError",""+error);
                    dialog.dismiss();
                    if (error instanceof TimeoutError) {
                        Toast.makeText(requireContext(), R.string.timeout_error, Toast.LENGTH_SHORT).show();
                    } else if (error instanceof NoConnectionError) {
                        Toast.makeText(requireContext(), R.string.no_connection_error, Toast.LENGTH_SHORT).show();
                    } else if (error instanceof AuthFailureError) {
                        Toast.makeText(requireContext(), R.string.auth_failure_error, Toast.LENGTH_SHORT).show();
                    } else if (error instanceof ServerError) {
                        Toast.makeText(requireContext(), R.string.server_error, Toast.LENGTH_SHORT).show();
                    } else if (error instanceof NetworkError) {
                        Toast.makeText(requireContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
                    } else if (error instanceof ParseError) {
                        Toast.makeText(requireContext(), R.string.parse_error, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(requireContext(), R.string.unknown_error, Toast.LENGTH_SHORT).show();
                    }
                    Log.d("Check", "" + error.getMessage());
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                String casteOptions = AnimalJati.getSelectedItem().toString();
                Log.d("API_Params", "id: " + AnimalId);
//                Log.d("API_Params", "userid: " + userid);
                Log.d("API_Params", "number: " + AnimalNumber.getText().toString());
                Log.d("API_Params", "type: " + AnimalType.getText().toString());
                Log.d("API_Params", "animal_caste: " + casteOptions);
                Log.d("API_Params", "animal_gender: " + AnimalGender.getText().toString());
//                Log.d("API_Params", "animal_dob: " + AnimalBirthdate.getText().toString());
                Log.d("API_Params", "byat_count: " + ByatNumber.getText().toString());
                Log.d("API_Params", "kisan_number: " + KisanMobileNumber.getText().toString());
                Log.d("API_Params", "mother_number: " + MotherNumber.getText().toString());
                Log.d("API_Params", "father_number: " + FatherNumber.getText().toString());
//                Log.d("API_Params", "pidhi_three_number: " + Pidhi3Number.getText().toString());
//                Log.d("API_Params", "pidhi_four_number: " + Pidhi4Number.getText().toString());
//                Log.d("API_Params", "pidhi_five_number: " + Pidhi5Number.getText().toString());
//                Log.d("API_Params", "pidhi_six_number: " + Pidhi6Number.getText().toString());
//                Log.d("API_Params", "pidhi_seven_number: " + Pidhi7Number.getText().toString());
//                Log.d("API_Params", "father_tarbar_number: " + PitaTarbarNumber.getText().toString());
//                Log.d("API_Params", "pidhi_three_tarbar_number: " + Pidhi3TarbarNumber.getText().toString());
//                Log.d("API_Params", "pidhi_four_tarbar_number: " + Pidhi4TarbarNumber.getText().toString());
//                Log.d("API_Params", "pidhi_five_tarbar_number: " + Pidhi5TarbarNumber.getText().toString());
//                Log.d("API_Params", "pidhi_six_tarbar_number: " + Pidhi6TarbarNumber.getText().toString());
//                Log.d("API_Params", "pidhi_seven_tarbar_number: " + Pidhi7TarbarNumber.getText().toString());


                        params.put("id", AnimalId);
//                params.put("userid", userid);
                params.put("number", AnimalNumber.getText().toString());
                params.put("type", AnimalType.getText().toString());
                params.put("animal_caste", casteOptions);
                params.put("animal_gender", AnimalGender.getText().toString());
//                params.put("animal_dob", AnimalBirthdate.getText().toString());

                String byatBirthdate = AnimalBirthdate.getText().toString();
                SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy");
                SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");

                try {
                    Date date = inputFormat.parse(byatBirthdate);
                    String formattedDate = outputFormat.format(date);
                    params.put("animal_dob", formattedDate);
                    Log.d("API_Params", "animal_dob: " + formattedDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                    Log.e("Date Parsing", "Invalid date format: " + byatBirthdate);
                }

                params.put("byat_count", ByatNumber.getText().toString());
                params.put("kisan_number", KisanMobileNumber.getText().toString());
                params.put("mother_number", MotherNumber.getText().toString());
                params.put("father_number", FatherNumber.getText().toString());
//                params.put("pidhi_three_number", Pidhi3Number.getText().toString());
//                params.put("pidhi_four_number", Pidhi4Number.getText().toString());
//                params.put("pidhi_five_number", Pidhi5Number.getText().toString());
//                params.put("pidhi_six_number", Pidhi6Number.getText().toString());
//                params.put("pidhi_seven_number", Pidhi7Number.getText().toString());
//                params.put("father_tarbar_number", PitaTarbarNumber.getText().toString());
//                params.put("pidhi_three_tarbar_number", Pidhi3TarbarNumber.getText().toString());
//                params.put("pidhi_four_tarbar_number", Pidhi4TarbarNumber.getText().toString());
//                params.put("pidhi_five_tarbar_number", Pidhi5TarbarNumber.getText().toString());
//                params.put("pidhi_six_tarbar_number", Pidhi6TarbarNumber.getText().toString());
//                params.put("pidhi_seven_tarbar_number", Pidhi7TarbarNumber.getText().toString());

                return params;
            }
        };
        postRequest.setRetryPolicy(ApiUtils.DEFAULT_RETRY_POLICY);
        requestQueue.add(postRequest);
    }

    private void deleteAnimal(String animalId) {
        String url = DELETE_ANIMAL;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.d("DeleteAnimalResponse",""+response);
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("status");
                            String message = jsonObject.getString("message");
                            if (status.equals("true")) {
                                Intent intent = new Intent(requireContext(), Checking.class);
                                intent.putExtra("isVerified", true);
                                intent.putExtra("message", message);
                                intent.putExtra("nextActivity", MainActivity.class);
                                intent.putExtra("SELECT_DETAILS_FRAGMENT", true);
                                startActivity(intent);
                            } else {
                                Intent intent = new Intent(requireContext(), Checking.class);
                                intent.putExtra("isVerified", false);
                                intent.putExtra("message", message);
                                intent.putExtra("nextActivity", MainActivity.class);
                                intent.putExtra("SELECT_DETAILS_FRAGMENT", true);
                                startActivity(intent);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(requireContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("animal_id", animalId);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        requestQueue.add(stringRequest);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        requireActivity().getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(requireContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });
    }
}
