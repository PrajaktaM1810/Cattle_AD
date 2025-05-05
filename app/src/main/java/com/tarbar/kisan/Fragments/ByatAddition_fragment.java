package com.tarbar.kisan.Fragments;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.tarbar.kisan.Helper.constant.ADD_BYAT;
import static com.tarbar.kisan.Helper.constant.DELETE_BYAT;
import static com.tarbar.kisan.Helper.constant.GET_BYAT_DATA;
import static com.tarbar.kisan.Helper.constant.GET_BYAT_LIST;
import static com.tarbar.kisan.Helper.constant.UPDATE_BYAT_DATA;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
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
import com.tarbar.kisan.Activities.MainActivity;
import com.tarbar.kisan.Helper.ApiUtils;
import com.tarbar.kisan.Helper.Iconstant;
import com.tarbar.kisan.Helper.SharedPreferenceManager;
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
import java.util.Locale;
import java.util.Map;

public class ByatAddition_fragment extends Fragment {
    SharedPreferenceManager sharedPrefMgr;
    EditText AnimalNumber, AnimalType, AnimalGender, ByatCnt,
            AnimalBirthdate, KisanMobileNumber,
            KisanPassword, bullTarbarNumber, DoctorNumber;
    Spinner byat_spinner;
    ImageView back;
    String MobileNumber, userid, Password;
    ProgressDialog dialog;
    Button submit;
    int arrayId;
    ImageView calenderImg;
    private String byatGender;
    Button DeleteByat,buttonGaonBull;
    String animalId,animalNumber,animalType,byatcnt,byatId,kisanmobilenumber,KisanId,animalBirthDate;
    boolean initialForm,origionalForm;
    LinearLayout PasswordLayout,lytDoctorNumber;
    TextView title_password,title_doctor_number;
    private List<Map<String, String>> byaatList2 = new ArrayList<>();
    String latestbyat;
    private String globalByatCount;
    private String globalByatBirthdate, globalstr;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_byat_additon_form, container, false);
        view.setBackgroundColor(Color.WHITE);
        initViews(view);

        sharedPrefMgr = new SharedPreferenceManager(requireContext());
        sharedPrefMgr.connectDB();
        userid = sharedPrefMgr.getString(Iconstant.userid);
        MobileNumber = sharedPrefMgr.getString(Iconstant.mobile);
        Password = sharedPrefMgr.getString(Iconstant.password);
        sharedPrefMgr.closeDB();

        initialForm = getArguments() != null && getArguments().getBoolean("initialForm", false);
        origionalForm = getArguments() != null && getArguments().getBoolean("origionalForm", false);

        Bundle bundle = getArguments();
        if (bundle != null) {
            KisanId = bundle.getString("KisanId");
            animalId = bundle.getString("animalId");
            animalNumber = bundle.getString("animalNumber");
            animalType = bundle.getString("animalType");
            byatcnt = bundle.getString("byatcnt");
            kisanmobilenumber = bundle.getString("kisanmobilenumber");
            latestbyat = bundle.getString("latest_byat");
            byatId = bundle.getString("byatId");
            animalBirthDate = bundle.getString("animal_birth_date");

            Log.d("ByatAdditionFragment", "KisanId: " + KisanId);
            Log.d("ByatAdditionFragment", "animalId: " + animalId);
            Log.d("ByatAdditionFragment", "animalNumber: " + animalNumber);
            Log.d("ByatAdditionFragment", "animalType: " + animalType);
            Log.d("ByatAdditionFragment", "byatcnt: " + byatcnt);
            Log.d("ByatAdditionFragment", "kisanmobilenumber: " + kisanmobilenumber);
            Log.d("ByatAdditionFragment", "latestbyat: " + latestbyat);
            Log.d("ByatAdditionFragment", "byatId: " + byatId);
            Log.d("ByatAdditionFragment", "animalBirthDate: " + animalBirthDate);

        } else {
            KisanId = "";
            animalId = "";
            animalNumber = "";
            animalType = "";
            byatcnt = "";
            kisanmobilenumber = "";
            latestbyat = "";
            byatId = "";
        }

        if (initialForm) {
//            Update
            getByatProfile(byatId);
        } else if (origionalForm) {
//            Addition
            byatListData(animalId);
            DeleteByat.setVisibility(GONE);
            AnimalNumber.setText(animalNumber);
            AnimalType.setText(animalType);
            KisanMobileNumber.setText(kisanmobilenumber);
        }
        return view;
    }

    private void initViews(View view) {
        AnimalNumber = view.findViewById(R.id.AnimalNumber);
        AnimalType = view.findViewById(R.id.AnimalType);
        AnimalGender = view.findViewById(R.id.AnimalGender);
        ByatCnt =  view.findViewById(R.id.ByatCnt);
        AnimalBirthdate = view.findViewById(R.id.AnimalBirthdate);
        KisanMobileNumber = view.findViewById(R.id.KisanMobileNumber);
        KisanPassword = view.findViewById(R.id.KisanPassword);
        byat_spinner = view.findViewById(R.id.byat_spinner);
        back = view.findViewById(R.id.back);
        submit = view.findViewById(R.id.submit);
        calenderImg = view.findViewById(R.id.calenderImg);
        DeleteByat = view.findViewById(R.id.DeleteByat);
        buttonGaonBull = view.findViewById(R.id.buttonGaonBull);
        bullTarbarNumber = view.findViewById(R.id.bullTarbarNumber);
        DoctorNumber = view.findViewById(R.id.DoctorNumber);
        PasswordLayout = view.findViewById(R.id.PasswordLayout);
        title_password = view.findViewById(R.id.title_password);
        lytDoctorNumber = view.findViewById(R.id.lytDoctorNumber);
        title_doctor_number = view.findViewById(R.id.title_doctor_number);

        AnimalBirthdate.setFocusable(false);
        AnimalBirthdate.setClickable(true);

        if (bullTarbarNumber.equals(getString(R.string.txt_village_bull))) {
            lytDoctorNumber.setVisibility(GONE);
            title_doctor_number.setVisibility(GONE);
        } else {
            lytDoctorNumber.setVisibility(VISIBLE);
            title_doctor_number.setVisibility(VISIBLE);
        }

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.Gender, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        byat_spinner.setAdapter(adapter);

        byat_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                byatGender = parentView.getItemAtPosition(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.click_animation);
                v.startAnimation(animation);

                AnimalNumber.setText("");
                AnimalType.setText("");
                byat_spinner.setSelection(0);
                bullTarbarNumber.setText("");
                DoctorNumber.setText("");
                KisanMobileNumber.setText("");
                KisanPassword.setText("");

                Intent intent = new Intent(requireContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);

                if (getActivity() != null) {
                    getActivity().finish();
                }
            }
        });

        buttonGaonBull.setOnClickListener(v -> {
            if (bullTarbarNumber.getText().toString().isEmpty()) {
                bullTarbarNumber.setText(R.string.txt_village_bull);
                    lytDoctorNumber.setVisibility(GONE);
                    title_doctor_number.setVisibility(GONE);
                    DoctorNumber.setText("");
            } else {
                bullTarbarNumber.setText("");
                lytDoctorNumber.setVisibility(VISIBLE);
                title_doctor_number.setVisibility(VISIBLE);
            }
        });

        calenderImg.setOnClickListener(v -> showDatePickerDialog());

        AnimalBirthdate.setOnClickListener(v -> showDatePickerDialog());

        submit.setOnClickListener(v -> {
            Animation animation = AnimationUtils.loadAnimation(requireContext(), R.anim.click_animation);
            v.startAnimation(animation);

            String regex = "^[789]\\d{9}$";

            String selectedByatGender = byat_spinner.getSelectedItem().toString();
            String strKisanPassword = KisanPassword.getText().toString();
            String byatBirthdate = AnimalBirthdate.getText().toString();
            String isBull = bullTarbarNumber.getText().toString();
            String doctorNumber = DoctorNumber.getText().toString();

            if (selectedByatGender.equals("लिंग चुनें")) {
                TextView errorText = (TextView) byat_spinner.getSelectedView();
                errorText.setError(getString(R.string.hint_choose_gender));
                errorText.setTextColor(Color.BLACK);
                errorText.setText("");
            } else if (byatBirthdate.isEmpty()) {
                AnimalBirthdate.setError(getString(R.string.error_byat_birhtdate));
                AnimalBirthdate.setFocusable(true);
            } else if (isBull.isEmpty()) {
                bullTarbarNumber.setError(getString(R.string.hint_tarbar_number));
                bullTarbarNumber.setFocusable(true);
            } else if (isBull.isEmpty() || (!isBull.equals("गाँव बैल") && (doctorNumber.isEmpty() || doctorNumber.length() < 10 || !doctorNumber.matches(regex)))) {
                // Condition for when bullTarbarNumber is not empty but does not contain "गाँव बैल"
                DoctorNumber.setError(getString(R.string.enter_correct_mobile_number));
                DoctorNumber.requestFocus();
            } else if (!isBull.isEmpty() && isBull.equals("गाँव बैल") && !doctorNumber.isEmpty()) {
                // Doctor number should not be filled if bullTarbarNumber is "गाँव बैल"
                DoctorNumber.setError(getString(R.string.enter_correct_mobile_number));
                DoctorNumber.requestFocus();
            } else if (strKisanPassword.isEmpty()) {
                KisanPassword.setError(getString(R.string.enter_password_number));
            } else if (strKisanPassword.length() < 4) {
                KisanPassword.setError(getString(R.string.enter_correct_password));
            } else if (!strKisanPassword.equals(Password)) {
                Toast.makeText(requireContext(), R.string.error_password_not_matching, Toast.LENGTH_SHORT).show();
            } else {
                if (initialForm) {
                    UpdatePashuAPI(animalId);
                } else if (origionalForm) {
                    AddByatAPI();
                }
            }
        });


        DeleteByat.setOnClickListener(v -> {
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
    }

    private void byatListData(String PashuId) {
        String url = GET_BYAT_LIST;
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("APIResponse", "Response: " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("status");
                            if (status.equals("success")) {
                                JSONArray dataArray = jsonObject.getJSONArray("data");
                                byaatList2.clear();
                                if (dataArray.length() > 0) {
                                    JSONObject lastData = dataArray.getJSONObject(dataArray.length() - 1);
                                    globalByatCount = lastData.getString("pashu_byat_count");
                                    globalByatBirthdate = lastData.getString("byat_birthdate");
                                    Log.d("DebugTag", "globalByatCount: " + globalByatCount);
                                    Log.d("DebugTag", "globalByatBirthdate: " + globalByatBirthdate);
                                    String addition = String.valueOf(Integer.parseInt(globalByatCount) + 1);
                                    ByatCnt.setText("ब्यात " + addition);
                                    globalstr = addition;

                                    for (int i = 0; i < dataArray.length(); i++) {
                                        JSONObject data = dataArray.getJSONObject(i);
                                        Map<String, String> byaatData = new HashMap<>();
                                        byaatData.put("byat_id", data.getString("byat_id"));
                                        byaatData.put("user_id", data.getString("user_id"));
                                        byaatData.put("pashu_id", data.getString("pashu_id"));
                                        byaatData.put("pashu_number", data.getString("pashu_number"));
                                        byaatData.put("pashu_type", data.getString("pashu_type"));
                                        byaatData.put("pashu_byat_count", data.getString("pashu_byat_count"));
                                        byaatData.put("byat_gender", data.getString("byat_gender"));
                                        byaatData.put("byat_birthdate", data.getString("byat_birthdate"));
                                        byaatData.put("is_village_bull", data.getString("is_village_bull"));
                                        byaatData.put("doctor_number", data.getString("doctor_number"));
                                        byaatData.put("kisan_number", data.getString("kisan_number"));
                                        byaatData.put("tarbar_bull_number", data.getString("tarbar_bull_number"));
                                        byaatList2.add(byaatData);
                                    }
                                } else {
                                    ByatCnt.setText("ब्यात 1");
                                    globalstr = "1";
//                                    Toast.makeText(getContext(), R.string.str_data_not_found, Toast.LENGTH_SHORT).show();
                                }
                            } else if (status.equals("error")) {
                                ByatCnt.setText("ब्यात 1");
                                globalstr = "1";
//                                Toast.makeText(getContext(), R.string.str_data_not_found, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Log.d("JSONException", "" + e);
                            Toast.makeText(getContext(), R.string.json_parsing_error, Toast.LENGTH_SHORT).show();
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
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("pashu_id", PashuId);
                return params;
            }
        };
        postRequest.setRetryPolicy(ApiUtils.DEFAULT_RETRY_POLICY);
        requestQueue.add(postRequest);
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
                callApi();
            }
        });

        dialogView.findViewById(R.id.textCancel).setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void callApi() {
        deleteByat(animalId,byatId);
    }

//    private void showDatePickerDialog() {
////        if (initialForm) {
////            Log.d("InThisBLoack","");
////            final Calendar calendar = Calendar.getInstance();
////            if (globalByatBirthdate == null) {
////                int year = calendar.get(Calendar.YEAR);
////                int month = calendar.get(Calendar.MONTH);
////                int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
////
////                DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
////                        (view, year1, monthOfYear, dayOfMonth1) -> {
////                            String selectedDate = String.format("%02d/%02d/%d", dayOfMonth1, monthOfYear + 1, year1);
////                            AnimalBirthdate.setText(selectedDate);
////                        }, year, month, dayOfMonth);
////                datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
////                datePickerDialog.show();
////            } else {
////                try {
////                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
////                    Date latestDate = sdf.parse(globalByatBirthdate);
////
////                    Calendar minCalendar = Calendar.getInstance();
////                    minCalendar.setTime(latestDate);
////                    minCalendar.add(Calendar.DAY_OF_YEAR, 301);
////
////                    int year = calendar.get(Calendar.YEAR);
////                    int month = calendar.get(Calendar.MONTH);
////                    int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
////
////                    DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
////                            (view, year1, monthOfYear, dayOfMonth1) -> {
////                                String selectedDate = String.format("%02d/%02d/%d", dayOfMonth1, monthOfYear + 1, year1);
////                                AnimalBirthdate.setText(selectedDate);
////                            }, year, month, dayOfMonth);
////
////                    datePickerDialog.getDatePicker().setMinDate(minCalendar.getTimeInMillis());
////                    datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
////                    datePickerDialog.show();
////                } catch (ParseException e) {
////                    e.printStackTrace();
////                }
////            }
////        } else if(origionalForm){
////            Log.d("InThatBLoack","");
//            final Calendar calendar = Calendar.getInstance();
//            if (latestbyat == null) {
//                int year = calendar.get(Calendar.YEAR);
//                int month = calendar.get(Calendar.MONTH);
//                int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
//
//                DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
//                        (view, year1, monthOfYear, dayOfMonth1) -> {
//                            String selectedDate = String.format("%02d/%02d/%d", dayOfMonth1, monthOfYear + 1, year1);
//                            AnimalBirthdate.setText(selectedDate);
//                        }, year, month, dayOfMonth);
//                datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
//                datePickerDialog.show();
//            } else {
//                try {
//                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
//                    Date latestDate = sdf.parse(latestbyat);
//
//                    Calendar minCalendar = Calendar.getInstance();
//                    minCalendar.setTime(latestDate);
//                    minCalendar.add(Calendar.DAY_OF_YEAR, 301);
//
//                    int year = calendar.get(Calendar.YEAR);
//                    int month = calendar.get(Calendar.MONTH);
//                    int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
//
//                    DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
//                            (view, year1, monthOfYear, dayOfMonth1) -> {
//                                String selectedDate = String.format("%02d/%02d/%d", dayOfMonth1, monthOfYear + 1, year1);
//                                AnimalBirthdate.setText(selectedDate);
//                            }, year, month, dayOfMonth);
//
//                    datePickerDialog.getDatePicker().setMinDate(minCalendar.getTimeInMillis());
//                    datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
//                    datePickerDialog.show();
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
//            }
//
////        }
//    }

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

    private void getByatProfile(String ByatId) {
        dialog = new ProgressDialog(getActivity());
        dialog.setMessage(getString(R.string.getting_data));
        dialog.setCancelable(false);
        dialog.show();
        String url = GET_BYAT_DATA;
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response_getByatProfile",""+response);
                        dialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("status");
                            String message = jsonObject.getString("message");
                            if ("success".equals(status)) {
                                JSONObject data = jsonObject.getJSONObject("data");
                                String byatId = data.getString("byat_id");
                                String userId = data.getString("user_id");
                                String pashuId = data.getString("pashu_id");
                                String pashuByatCount = data.getString("pashu_byat_count");
                                String byatGender = data.getString("byat_gender");
                                String isVillageBull = data.optString("is_village_bull", "");
                                String tarbarBullNumber = data.getString("tarbar_bull_number");
                                String doctorNumber = data.getString("doctor_number");
                                String animalNumber = data.getString("number");
                                String type = data.getString("type");
                                String kisanNumber = data.getString("kisan_number");

                                Log.d("TypeNumber", type);

                                if (tarbarBullNumber.equals("गाँव बैल")) {
                                    lytDoctorNumber.setVisibility(GONE);
                                    title_doctor_number.setVisibility(GONE);
                                } else {
                                    lytDoctorNumber.setVisibility(VISIBLE);
                                    title_doctor_number.setVisibility(VISIBLE);
                                }

                                Log.d("KISAN_NUMBER",""+kisanmobilenumber);

                                if (MobileNumber.equals(kisanmobilenumber)){
                                    submit.setVisibility(VISIBLE);
                                    DeleteByat.setVisibility(VISIBLE);
                                    title_password.setVisibility(VISIBLE);
                                    PasswordLayout.setVisibility(VISIBLE);
                                    buttonGaonBull.setVisibility(VISIBLE);

                                    byat_spinner.setClickable(true);
                                    byat_spinner.setFocusable(true);

                                    calenderImg.setClickable(true);
                                    calenderImg.setEnabled(true);

                                    AnimalBirthdate.setClickable(true);
                                    AnimalBirthdate.setFocusable(true);

                                    bullTarbarNumber.setClickable(true);
                                    bullTarbarNumber.setFocusable(true);

                                    DoctorNumber.setClickable(true);
                                    DoctorNumber.setFocusable(true);

                                    KisanPassword.setClickable(true);
                                    KisanPassword.setFocusable(true);

                                    if (byatGender == null || byatGender.isEmpty()) {
                                        byat_spinner.setSelection(0);
                                    } else {
                                        ArrayAdapter<String> adapter = (ArrayAdapter<String>) byat_spinner.getAdapter();
                                        int position = adapter.getPosition(byatGender);
                                        if (position >= 0) {
                                            byat_spinner.setSelection(position);
                                        } else {
                                            byat_spinner.setSelection(0);
                                        }
                                    }
                                    
                                    String originalDate = data.
                                            getString("byat_birthdate");
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

                                    AnimalNumber.setText(animalNumber == null || animalNumber.equalsIgnoreCase("Null") ? "" : animalNumber);
                                    AnimalType.setText(type == null || type.equalsIgnoreCase("Null") ? "" : type);
                                    ByatCnt.setText("ब्यात " + pashuByatCount);
                                    KisanMobileNumber.setText(kisanNumber == null || kisanNumber.equalsIgnoreCase("Null") ? "" : kisanNumber);

                                    if (!isVillageBull.isEmpty()) {
                                        bullTarbarNumber.setText(isVillageBull);
                                    } else if (tarbarBullNumber != null && !tarbarBullNumber.equalsIgnoreCase("Null")) {
                                        bullTarbarNumber.setText(tarbarBullNumber);
                                    }

                                    DoctorNumber.setText(doctorNumber == null || doctorNumber.equalsIgnoreCase("Null") ? "" : doctorNumber);

                                    Log.d("ByatData", "Byat ID: " + byatId);


                                } else {
                                    submit.setVisibility(GONE);
                                    DeleteByat.setVisibility(GONE);
                                    title_password.setVisibility(GONE);
                                    PasswordLayout.setVisibility(GONE);
                                    buttonGaonBull.setVisibility(GONE);

                                    byat_spinner.setClickable(false);
                                    byat_spinner.setFocusable(false);

                                    calenderImg.setClickable(false);
                                    calenderImg.setEnabled(false);

                                    AnimalBirthdate.setClickable(false);
                                    AnimalBirthdate.setFocusable(false);
                                    AnimalBirthdate.setEnabled(false);

                                    bullTarbarNumber.setClickable(false);
                                    bullTarbarNumber.setFocusable(false);

                                    DoctorNumber.setClickable(false);
                                    DoctorNumber.setFocusable(false);

                                    KisanPassword.setClickable(false);
                                    KisanPassword.setFocusable(false);

                                    String originalDate = data.getString("byat_birthdate");
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

                                    AnimalNumber.setText(animalNumber == null || animalNumber.equalsIgnoreCase("Null") ? "" : animalNumber);
                                    AnimalType.setText(type == null || type.equalsIgnoreCase("Null") ? "" : type);
                                    ByatCnt.setText("ब्यात " + pashuByatCount);
                                    KisanMobileNumber.setText(kisanNumber == null || kisanNumber.equalsIgnoreCase("Null") ? "" : kisanNumber);

                                    if (byatGender == null || byatGender.isEmpty()) {
                                        byat_spinner.setSelection(0);
                                    } else {
                                        ArrayAdapter<String> adapter = (ArrayAdapter<String>) byat_spinner.getAdapter();
                                        int position = adapter.getPosition(byatGender);
                                        if (position >= 0) {
                                            byat_spinner.setSelection(position);
                                        } else {
                                            byat_spinner.setSelection(0);
                                        }
                                    }

                                    if (byat_spinner.getSelectedView() != null) {
                                        ((TextView) byat_spinner.getSelectedView()).setTextColor(Color.BLACK); // Set text color to black
                                    }

                                    byat_spinner.setEnabled(false);
                                    byat_spinner.setClickable(false);


                                    if (!isVillageBull.isEmpty()) {
                                        bullTarbarNumber.setText(isVillageBull);
                                    } else if (tarbarBullNumber != null && !tarbarBullNumber.equalsIgnoreCase("Null")) {
                                        bullTarbarNumber.setText(tarbarBullNumber);
                                    }

                                    DoctorNumber.setText(doctorNumber == null || doctorNumber.equalsIgnoreCase("Null") ? "" : doctorNumber);

                                    Log.d("ByatData", "Byat ID: " + byatId);
                                }

                            } else {
                                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), R.string.generic_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dialog.dismiss();
                        Toast.makeText(getActivity(), R.string.generic_error, Toast.LENGTH_SHORT).show();
                        Log.d("Check", "" + error.getMessage());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("byat_id", ByatId);
                Log.d("ByatID",""+ByatId);
                return params;
            }
        };
        stringRequest.setRetryPolicy(ApiUtils.DEFAULT_RETRY_POLICY);
        requestQueue.add(stringRequest);
    }

    private void AddByatAPI() {
        dialog = new ProgressDialog(requireContext());
        dialog.setMessage(getString(R.string.saving_data));
        dialog.setCancelable(false);
        dialog.show();
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        final StringRequest postRequest = new StringRequest(Request.Method.POST, ADD_BYAT,
                response -> {
                    dialog.dismiss();
                    Log.d("AddByatAPI", "" + response);
                    try {
                        JSONObject jsonObject1 = new JSONObject(response);
                        String Status = jsonObject1.getString("status");
                        String Message = jsonObject1.getString("message");

                        if (Status.equalsIgnoreCase("success")) {

                            AnimalNumber.setText("");
                            AnimalType.setText("");
                            byat_spinner.setSelection(0);
                            bullTarbarNumber.setText("");
                            DoctorNumber.setText("");
                            KisanMobileNumber.setText("");
                            KisanPassword.setText("");

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
                    dialog.dismiss();
                    if (error instanceof TimeoutError) {
                        Toast.makeText(requireContext(), R.string.timeout_error, Toast.LENGTH_SHORT).show();
                    } else if (error instanceof NoConnectionError) {
                        Toast.makeText(requireContext(), R.string.no_connection_error, Toast.LENGTH_SHORT).show();
                    } else if (error instanceof NetworkError) {
                        Toast.makeText(requireContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
                    } else if (error instanceof ServerError) {
                        Toast.makeText(requireContext(), R.string.server_error, Toast.LENGTH_SHORT).show();
                    } else if (error instanceof ParseError) {
                        Toast.makeText(requireContext(), R.string.parse_error, Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                String BullNo = bullTarbarNumber.getText().toString();
                params.put("user_id", KisanId);
                params.put("pashu_id", animalId);
                params.put("pashu_byat_count",globalstr);
                params.put("byat_gender", byatGender);

                String byatBirthdate = AnimalBirthdate.getText().toString();
                SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy");
                SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    Date date = inputFormat.parse(byatBirthdate);
                    String formattedDate = outputFormat.format(date);
                    params.put("byat_birthdate", formattedDate);
                    Log.d("Params", "byat_birthdate: " + formattedDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                    Log.e("Params", "Invalid date format: " + byatBirthdate);
                }

                if(BullNo.equals("गाँव बैल")){
                    params.put("is_village_bull", bullTarbarNumber.getText().toString());
                } else if (!BullNo.isEmpty() && !BullNo.equals("गाँव बैल")){
                    params.put("is_village_bull", "");
                }

                params.put("doctor_number", DoctorNumber.getText().toString());
                if (!BullNo.isEmpty() && !BullNo.equals("गाँव बैल")) {
                    params.put("tarbar_bull_number", bullTarbarNumber.getText().toString());
                } else {
                    params.put("tarbar_bull_number", "");
                }

                Log.d("Params", "user_id: " + KisanId);
                Log.d("Params", "pashu_id: " + animalId);
                Log.d("Paramssss", "pashu_byat_cnt: " +  globalstr);
                Log.d("Params", "byat_gender: " + byatGender);

                if(BullNo.equals("गाँव बैल")){
                    Log.d("Params", "is_village_bull: " + bullTarbarNumber.getText().toString());
                } else if (!BullNo.isEmpty() && !BullNo.equals("गाँव बैल")){
                    Log.d("Params", "is_village_bull: " + "");
                }

                Log.d("Params", "doctor_number: " + DoctorNumber.getText().toString());

                if (!BullNo.isEmpty() && !BullNo.equals("गाँव बैल")) {
                    Log.d("Params", "tarbar_bull_number: " + bullTarbarNumber.getText().toString());
                } else {
                    Log.d("Params", "tarbar_bull_number: " + "");
                }
                return params;
            }
        };
        postRequest.setRetryPolicy(ApiUtils.DEFAULT_RETRY_POLICY);
        requestQueue.add(postRequest);
    }

    private void UpdatePashuAPI(String PashuId) {
        dialog = new ProgressDialog(requireContext());
        dialog.setMessage(getString(R.string.saving_data));
        dialog.setCancelable(false);
        dialog.show();
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        final StringRequest postRequest = new StringRequest(Request.Method.POST, UPDATE_BYAT_DATA,
                response -> {
                    dialog.dismiss();
                    Log.d("UpdateByatAPI", "" + response);
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
                    dialog.dismiss();
                    if (error instanceof TimeoutError) {
                        Toast.makeText(requireContext(), R.string.timeout_error, Toast.LENGTH_SHORT).show();
                    } else if (error instanceof NoConnectionError) {
                        Toast.makeText(requireContext(), R.string.no_connection_error, Toast.LENGTH_SHORT).show();
                    } else if (error instanceof AuthFailureError) {
                        Toast.makeText(requireContext(), R.string.auth_failure_error, Toast.LENGTH_SHORT).show();
                    } else if (error instanceof ServerError) {
                        Log.d("ServerError",""+error);
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
                String updatedByatCount = ByatCnt.getText().toString();
                String BullNo = bullTarbarNumber.getText().toString();

                params.put("byat_id", byatId);
                params.put("user_id", KisanId);
                params.put("pashu_id", PashuId);
                params.put("pashu_byat_count", updatedByatCount);
                params.put("byat_gender", byatGender);

                String byatBirthdate = AnimalBirthdate.getText().toString();
                SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy");
                SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    Date date = inputFormat.parse(byatBirthdate);
                    String formattedDate = outputFormat.format(date);
                    params.put("byat_birthdate", formattedDate);
                    Log.d("Params", "byat_birthdate: " + formattedDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                    Log.e("Params", "Invalid date format: " + byatBirthdate);
                }
                if(BullNo.equals("गाँव बैल")){
                    params.put("is_village_bull", bullTarbarNumber.getText().toString());
                } else if (!BullNo.isEmpty() && !BullNo.equals("गाँव बैल")){
                    params.put("is_village_bull", "");
                }
                params.put("doctor_number", DoctorNumber.getText().toString());
                if (!BullNo.isEmpty() && !BullNo.equals("गाँव बैल")) {
                    params.put("tarbar_bull_number", bullTarbarNumber.getText().toString());
                } else {
                    params.put("tarbar_bull_number", "");
                }
                Log.d("ByatParams", "byat_id: " + byatId);
                Log.d("ByatParams", "user_id: " + KisanId);
                Log.d("ByatParams", "pashu_id: " + PashuId);
                Log.d("ByatParams", "pashu_byat_cnt: " + updatedByatCount);
                Log.d("ByatParams", "byat_gender: " + byatGender);
                if(BullNo.equals("गाँव बैल")){
                    Log.d("ByatParams", "is_village_bull: " + bullTarbarNumber.getText().toString());
                } else if (!BullNo.isEmpty() && !BullNo.equals("गाँव बैल")){
                    Log.d("ByatParams", "is_village_bull: " + "");
                }
                Log.d("ByatParams", "doctor_number: " + DoctorNumber.getText().toString());
                if (!BullNo.isEmpty() && !BullNo.equals("गाँव बैल")) {
                    Log.d("ByatParams", "tarbar_bull_number: " + bullTarbarNumber.getText().toString());
                } else {
                    Log.d("ByatParams", "tarbar_bull_number: " + "");
                }
                return params;
            }
        };
        postRequest.setRetryPolicy(ApiUtils.DEFAULT_RETRY_POLICY);
        requestQueue.add(postRequest);
    }

    private void deleteByat(String PashuId,String ByatId) {
        dialog = new ProgressDialog(requireContext());
        dialog.setMessage(getString(R.string.deleting));
        dialog.setCancelable(false);
        dialog.show();
        String url = DELETE_BYAT;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        try {
                            Log.d("DeleteAnimalResponse",""+response);
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("status");
                            String Message = jsonObject.getString("message");
                            if (status.equals("success")) {
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
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dialog.dismiss();
                        Toast.makeText(requireContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("pashu_id", PashuId);
                params.put("byat_id", ByatId);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        stringRequest.setRetryPolicy(ApiUtils.DEFAULT_RETRY_POLICY);
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
