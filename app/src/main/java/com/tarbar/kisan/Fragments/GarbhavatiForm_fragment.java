package com.tarbar.kisan.Fragments;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.tarbar.kisan.Helper.constant.ADD_BYAT;
import static com.tarbar.kisan.Helper.constant.Add_BIJDAN_DATA;
import static com.tarbar.kisan.Helper.constant.DELETE_BYAT;
import static com.tarbar.kisan.Helper.constant.GET_BIJDAN_DATA;
import static com.tarbar.kisan.Helper.constant.GET_BIJDAN_DETAILS;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.tarbar.kisan.Activities.LoadFilterFragments;
import com.tarbar.kisan.Activities.MainActivity;
import com.tarbar.kisan.Helper.ApiUtils;
import com.tarbar.kisan.Helper.Iconstant;
import com.tarbar.kisan.Helper.SharedPreferenceManager;
import com.tarbar.kisan.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class GarbhavatiForm_fragment extends Fragment {
    SharedPreferenceManager sharedPrefMgr;
    EditText KisanMobileNumber, AnimalNumber, AnimalType,
            AnimalBijdanDate, ChildExpectedBirthdate,
            KisanPassword, bullNumber, DoctorNumber;
    ImageView back;
    String MobileNumber, userid, Password;
    ProgressDialog dialog;
    Button submit;
    ImageView calenderImg;
    Button Delete,buttonGaonBull;
    String userId,Kisanmobile_Number,bijdanId,animalId,animalNumber,animalType;
    boolean EditForm,AddForm;
    LinearLayout PasswordLayout,lytDoctorNumber;
    TextView title_password,title_doctor_number;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_garbhavati_form, container, false);
        view.setBackgroundColor(Color.WHITE);
        initViews(view);

        sharedPrefMgr = new SharedPreferenceManager(requireContext());
        sharedPrefMgr.connectDB();
        userid = sharedPrefMgr.getString(Iconstant.userid);
        MobileNumber = sharedPrefMgr.getString(Iconstant.mobile);
        Password = sharedPrefMgr.getString(Iconstant.password);
        sharedPrefMgr.closeDB();

        EditForm = getArguments() != null && getArguments().getBoolean("initialForm", false);
        AddForm = getArguments() != null && getArguments().getBoolean("origionalForm", false);

        Bundle bundle = getArguments();
        if (bundle != null) {
            userId = bundle.getString("userId");
            Kisanmobile_Number = bundle.getString("kisanmobileNumber");
            animalId = bundle.getString("animalId");
            animalNumber = bundle.getString("animalNumber");
            animalType = bundle.getString("animalType");
            bijdanId = bundle.getString("bijdanId");

            Log.d("BijdanFormFragment", "userId: " + userId);
            Log.d("BijdanFormFragment", "kisanmobileNumber: " + Kisanmobile_Number);
            Log.d("BijdanFormFragment", "bijdanId: " + bijdanId);
            Log.d("BijdanFormFragment", "animalId: " + animalId);
            Log.d("BijdanFormFragment", "animalNumber: " + animalNumber);
            Log.d("BijdanFormFragment", "animalType: " + animalType);

        } else {
            userId = "";
            Kisanmobile_Number = "";
            bijdanId = "";
            animalId = "";
            animalNumber = "";
            animalType = "";
        }

        if (EditForm) {
//            Update
            getBijdanData(bijdanId);
        } else if (AddForm) {
//            Addition
            Delete.setVisibility(GONE);
            AnimalNumber.setText(animalNumber);
            AnimalType.setText(animalType);
            KisanMobileNumber.setText(Kisanmobile_Number);
        }
        return view;
    }

    private void initViews(View view) {
        AnimalNumber = view.findViewById(R.id.AnimalNumber);
        AnimalType = view.findViewById(R.id.AnimalType);
        AnimalBijdanDate = view.findViewById(R.id.AnimalBijdanDate);
        KisanMobileNumber = view.findViewById(R.id.KisanMobileNumber);
        KisanPassword = view.findViewById(R.id.KisanPassword);
        back = view.findViewById(R.id.back);
        submit = view.findViewById(R.id.submit);
        calenderImg = view.findViewById(R.id.calenderImg);
        Delete = view.findViewById(R.id.Delete);
        buttonGaonBull = view.findViewById(R.id.buttonGaonBull);
        bullNumber = view.findViewById(R.id.bullNumber);
        DoctorNumber = view.findViewById(R.id.DoctorNumber);
        ChildExpectedBirthdate = view.findViewById(R.id.ChildExpectedBirthdate);
        PasswordLayout = view.findViewById(R.id.PasswordLayout);
        title_password = view.findViewById(R.id.title_password);
        lytDoctorNumber = view.findViewById(R.id.lytDoctorNumber);
        title_doctor_number = view.findViewById(R.id.title_doctor_number);

        AnimalBijdanDate.setFocusable(false);
        AnimalBijdanDate.setClickable(true);

        if (bullNumber.equals(getString(R.string.txt_village_bull))) {
            lytDoctorNumber.setVisibility(GONE);
            title_doctor_number.setVisibility(GONE);
        } else {
            lytDoctorNumber.setVisibility(VISIBLE);
            title_doctor_number.setVisibility(VISIBLE);
        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.click_animation);
                v.startAnimation(animation);

                AnimalNumber.setText("");
                AnimalType.setText("");
                bullNumber.setText("");
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
            if (bullNumber.getText().toString().isEmpty()) {
                bullNumber.setText(R.string.txt_village_bull);
                lytDoctorNumber.setVisibility(GONE);
                title_doctor_number.setVisibility(GONE);
                DoctorNumber.setText("");
            } else {
                bullNumber.setText("");
                lytDoctorNumber.setVisibility(VISIBLE);
                title_doctor_number.setVisibility(VISIBLE);
            }
        });

        calenderImg.setOnClickListener(v -> showDatePickerDialog());

        AnimalBijdanDate.setOnClickListener(v -> showDatePickerDialog());

        submit.setOnClickListener(v -> {
            Animation animation = AnimationUtils.loadAnimation(requireContext(), R.anim.click_animation);
            v.startAnimation(animation);

            String regex = "^[6789]\\d{9}$";

            String strKisanPassword = KisanPassword.getText().toString();
            String byatBirthdate = AnimalBijdanDate.getText().toString();
            String isBull = bullNumber.getText().toString();
            String doctorNumber = DoctorNumber.getText().toString();

            if (byatBirthdate.isEmpty()) {
                AnimalBijdanDate.setError(getString(R.string.error_byat_birhtdate));
                AnimalBijdanDate.setFocusable(true);
            } else if (isBull.isEmpty()) {
                bullNumber.setError(getString(R.string.hint_tarbar_number));
                bullNumber.setFocusable(true);
            } else if (isBull.isEmpty() || (!isBull.equals("गाँव बैल") && (doctorNumber.isEmpty() || doctorNumber.length() < 10 || !doctorNumber.matches(regex)))) {
                DoctorNumber.setError(getString(R.string.enter_correct_mobile_number));
                DoctorNumber.requestFocus();
            } else if (!isBull.isEmpty() && isBull.equals("गाँव बैल") && !doctorNumber.isEmpty()) {
                DoctorNumber.setError(getString(R.string.enter_correct_mobile_number));
                DoctorNumber.requestFocus();
            } else if (strKisanPassword.isEmpty()) {
                KisanPassword.setError(getString(R.string.enter_password_number));
            } else if (strKisanPassword.length() < 4) {
                KisanPassword.setError(getString(R.string.enter_correct_password));
            } else if (!strKisanPassword.equals(Password)) {
                Toast.makeText(requireContext(), R.string.error_password_not_matching, Toast.LENGTH_SHORT).show();
            } else {
                if (EditForm) {
//                    UpdatePashuAPI(animalId);
                } else if (AddForm) {
                    AddBijdanDataAPI();
                }
            }
        });

        Delete.setOnClickListener(v -> {
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

//    private void byatListData(String PashuId) {
//        String url = GET_BYAT_LIST;
//        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
//        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        Log.d("APIResponse", "Response: " + response);
//                        try {
//                            JSONObject jsonObject = new JSONObject(response);
//                            String status = jsonObject.getString("status");
//                            if (status.equals("success")) {
//                                JSONArray dataArray = jsonObject.getJSONArray("data");
//                                byaatList2.clear();
//                                if (dataArray.length() > 0) {
//                                    JSONObject lastData = dataArray.getJSONObject(dataArray.length() - 1);
//                                    globalByatCount = lastData.getString("pashu_byat_count");
//                                    globalByatBirthdate = lastData.getString("byat_birthdate");
//                                    Log.d("DebugTag", "globalByatCount: " + globalByatCount);
//                                    Log.d("DebugTag", "globalByatBirthdate: " + globalByatBirthdate);
//                                    String addition = String.valueOf(Integer.parseInt(globalByatCount) + 1);
//                                    ByatCnt.setText("ब्यात " + addition);
//                                    globalstr = addition;
//
//                                    for (int i = 0; i < dataArray.length(); i++) {
//                                        JSONObject data = dataArray.getJSONObject(i);
//                                        Map<String, String> byaatData = new HashMap<>();
//                                        byaatData.put("byat_id", data.getString("byat_id"));
//                                        byaatData.put("user_id", data.getString("user_id"));
//                                        byaatData.put("pashu_id", data.getString("pashu_id"));
//                                        byaatData.put("pashu_number", data.getString("pashu_number"));
//                                        byaatData.put("pashu_type", data.getString("pashu_type"));
//                                        byaatData.put("pashu_byat_count", data.getString("pashu_byat_count"));
//                                        byaatData.put("byat_gender", data.getString("byat_gender"));
//                                        byaatData.put("byat_birthdate", data.getString("byat_birthdate"));
//                                        byaatData.put("is_village_bull", data.getString("is_village_bull"));
//                                        byaatData.put("doctor_number", data.getString("doctor_number"));
//                                        byaatData.put("kisan_number", data.getString("kisan_number"));
//                                        byaatData.put("tarbar_bull_number", data.getString("tarbar_bull_number"));
//                                        byaatList2.add(byaatData);
//                                    }
//                                } else {
//                                    ByatCnt.setText("ब्यात 1");
//                                    globalstr = "1";
////                                    Toast.makeText(getContext(), R.string.str_data_not_found, Toast.LENGTH_SHORT).show();
//                                }
//                            } else if (status.equals("error")) {
//                                ByatCnt.setText("ब्यात 1");
//                                globalstr = "1";
////                                Toast.makeText(getContext(), R.string.str_data_not_found, Toast.LENGTH_SHORT).show();
//                            }
//                        } catch (JSONException e) {
//                            Log.d("JSONException", "" + e);
//                            Toast.makeText(getContext(), R.string.json_parsing_error, Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        if (error instanceof TimeoutError) {
//                            Toast.makeText(getContext(), R.string.timeout_error, Toast.LENGTH_SHORT).show();
//                        } else if (error instanceof NoConnectionError) {
//                            Toast.makeText(getContext(), R.string.no_connection_error, Toast.LENGTH_SHORT).show();
//                        } else if (error instanceof AuthFailureError) {
//                            Toast.makeText(getContext(), R.string.auth_failure_error, Toast.LENGTH_SHORT).show();
//                        } else if (error instanceof ServerError) {
//                            Log.d("ServerError", "" + error);
//                            Toast.makeText(getContext(), R.string.server_error, Toast.LENGTH_SHORT).show();
//                        } else if (error instanceof NetworkError) {
//                            Toast.makeText(getContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
//                        } else if (error instanceof ParseError) {
//                            Toast.makeText(getContext(), R.string.parse_error, Toast.LENGTH_SHORT).show();
//                        } else {
//                            Toast.makeText(getContext(), R.string.unknown_error, Toast.LENGTH_SHORT).show();
//                        }
//                        Log.d("Check", "" + error.getMessage());
//                    }
//                }) {
//            @Override
//            protected Map<String, String> getParams() {
//                Map<String, String> params = new HashMap<>();
//                params.put("pashu_id", PashuId);
//                return params;
//            }
//        };
//        postRequest.setRetryPolicy(ApiUtils.DEFAULT_RETRY_POLICY);
//        requestQueue.add(postRequest);
//    }

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
//        deleteData(animalId);
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                (view, year1, monthOfYear, dayOfMonth1) -> {
                    String selectedDate = String.format("%02d/%02d/%d", dayOfMonth1, monthOfYear + 1, year1);
                    AnimalBijdanDate.setText(selectedDate);

                    // Calculate and set expected birth date based on animal type
                    calculateExpectedBirthDate(selectedDate);
                }, year, month, dayOfMonth);
        datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
        datePickerDialog.show();
    }

    private void calculateExpectedBirthDate(String selectedDate) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(sdf.parse(selectedDate));

            String animalTypeStr = AnimalType.getText().toString();
            if (animalTypeStr.equals("भैंस")) {
                calendar.add(Calendar.DAY_OF_YEAR, 300);
            } else if (animalTypeStr.equals("गाय")) {
                calendar.add(Calendar.DAY_OF_YEAR, 270);
            }
            String expectedDate = sdf.format(calendar.getTime());
            ChildExpectedBirthdate.setText(expectedDate);
        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "Error calculating expected birth date", Toast.LENGTH_SHORT).show();
        }
    }

    private void getBijdanData(String bijdanId) {
        dialog = new ProgressDialog(getActivity());
        dialog.setMessage(getString(R.string.getting_data));
        dialog.setCancelable(false);
        dialog.show();
        String url = GET_BIJDAN_DETAILS;
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response_getBijdanData",""+response);
                        dialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("status");
                            String message = jsonObject.getString("message");
                            if ("success".equals(status)) {
                                JSONObject data = jsonObject.getJSONObject("data");
                                String tarbarBullNumber = data.getString("bull_number");
                                String doctorNumber = data.getString("doctor_number");

                                String bijdanDate = data.getString("bijdan_date");
                                String futureBirthDate = data.getString("future_birth_date");

                                String animalNumber = data.getString("number");
                                String type = data.getString("type");
                                String kisanNumber = data.getString("kisan_number");


                                Log.d("TypeNumber", type);

                                if (tarbarBullNumber.equals("गाँव बैल")) {
                                    lytDoctorNumber.setVisibility(GONE);
                                    title_doctor_number.setVisibility(GONE);
                                    bullNumber.setText(tarbarBullNumber);
                                } else {
                                    lytDoctorNumber.setVisibility(VISIBLE);
                                    title_doctor_number.setVisibility(VISIBLE);
                                    bullNumber.setText(tarbarBullNumber);
                                }

                                Log.d("KISAN_NUMBER",""+Kisanmobile_Number);

                                if (MobileNumber.equals(Kisanmobile_Number)){
                                    submit.setVisibility(VISIBLE);
                                    Delete.setVisibility(VISIBLE);
                                    title_password.setVisibility(VISIBLE);
                                    PasswordLayout.setVisibility(VISIBLE);
                                    buttonGaonBull.setVisibility(VISIBLE);

                                    calenderImg.setClickable(true);
                                    calenderImg.setEnabled(true);

                                    AnimalBijdanDate.setClickable(true);
                                    AnimalBijdanDate.setFocusable(true);

                                    bullNumber.setClickable(true);
                                    bullNumber.setFocusable(true);

                                    DoctorNumber.setClickable(true);
                                    DoctorNumber.setFocusable(true);

                                    KisanPassword.setClickable(true);
                                    KisanPassword.setFocusable(true);

                                    AnimalBijdanDate.setText(bijdanDate);
                                    ChildExpectedBirthdate.setText(futureBirthDate);

                                    AnimalNumber.setText(animalNumber == null || animalNumber.equalsIgnoreCase("Null") ? "" : animalNumber);
                                    AnimalType.setText(type == null || type.equalsIgnoreCase("Null") ? "" : type);
                                    KisanMobileNumber.setText(kisanNumber == null || kisanNumber.equalsIgnoreCase("Null") ? "" : kisanNumber);

                                    DoctorNumber.setText(doctorNumber == null || doctorNumber.equalsIgnoreCase("Null") ? "" : doctorNumber);

                                } else {
                                    submit.setVisibility(GONE);
                                    Delete.setVisibility(GONE);
                                    title_password.setVisibility(GONE);
                                    PasswordLayout.setVisibility(GONE);
                                    buttonGaonBull.setVisibility(GONE);

                                    calenderImg.setClickable(false);
                                    calenderImg.setEnabled(false);

                                    AnimalBijdanDate.setClickable(false);
                                    AnimalBijdanDate.setFocusable(false);
                                    AnimalBijdanDate.setEnabled(false);

                                    bullNumber.setClickable(false);
                                    bullNumber.setFocusable(false);

                                    DoctorNumber.setClickable(false);
                                    DoctorNumber.setFocusable(false);

                                    KisanPassword.setClickable(false);
                                    KisanPassword.setFocusable(false);

                                    AnimalBijdanDate.setText(bijdanDate);
                                    ChildExpectedBirthdate.setText(futureBirthDate);

                                    AnimalNumber.setText(animalNumber == null || animalNumber.equalsIgnoreCase("Null") ? "" : animalNumber);
                                    AnimalType.setText(type == null || type.equalsIgnoreCase("Null") ? "" : type);
                                    KisanMobileNumber.setText(kisanNumber == null || kisanNumber.equalsIgnoreCase("Null") ? "" : kisanNumber);
                                    DoctorNumber.setText(doctorNumber == null || doctorNumber.equalsIgnoreCase("Null") ? "" : doctorNumber);
                                }

                            } else {
                                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            dialog.dismiss();
                            if (e instanceof JSONException) {
                                Log.d("CheckException",""+e);
                                Toast.makeText(requireContext(), R.string.json_parsing_error, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(requireContext(), R.string.generic_error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
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
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", bijdanId);
                Log.d("id",""+bijdanId);
                return params;
            }
        };
        stringRequest.setRetryPolicy(ApiUtils.DEFAULT_RETRY_POLICY);
        requestQueue.add(stringRequest);
    }

    private void AddBijdanDataAPI() {
        dialog = new ProgressDialog(requireContext());
        dialog.setMessage(getString(R.string.saving_data));
        dialog.setCancelable(false);
        dialog.show();
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        final StringRequest postRequest = new StringRequest(Request.Method.POST, Add_BIJDAN_DATA,
                response -> {
                    dialog.dismiss();
                    Log.d("AddBijdanDataAPI", "" + response);
                    try {
                        JSONObject jsonObject1 = new JSONObject(response);
                        String Status = jsonObject1.getString("status");
                        String Message = jsonObject1.getString("message");

                        if (Status.equalsIgnoreCase("success")) {

                            AnimalNumber.setText("");
                            AnimalType.setText("");
                            bullNumber.setText("");
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
                String BullNo = bullNumber.getText().toString();
                String doctorNumber = DoctorNumber.getText().toString();
                params.put("userid", userid);
                params.put("pashu_id", animalId);
                params.put("bull_number",BullNo);
                params.put("doctor_number",doctorNumber);

                String BijdanDate = AnimalBijdanDate.getText().toString();
                SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy");
                SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    Date date = inputFormat.parse(BijdanDate);
                    String formattedDate = outputFormat.format(date);
                    params.put("bijdan_date",BijdanDate);
                    Log.d("Params", "byat_birthdate: " + formattedDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                    Log.e("Params", "Invalid date format: " + BijdanDate);
                }

                String FutureDate = ChildExpectedBirthdate.getText().toString();
                SimpleDateFormat inputFormat1 = new SimpleDateFormat("dd/MM/yyyy");
                SimpleDateFormat outputFormat1 = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    Date date = inputFormat1.parse(FutureDate);
                    String formattedDate1 = outputFormat1.format(date);
                    params.put("future_birth_date",FutureDate);
                    Log.d("Params", "byat_birthdate: " + formattedDate1);
                } catch (ParseException e) {
                    e.printStackTrace();
                    Log.e("Params", "Invalid date format: " + FutureDate);
                }
                return params;
            }
        };
        postRequest.setRetryPolicy(ApiUtils.DEFAULT_RETRY_POLICY);
        requestQueue.add(postRequest);
    }

//    private void UpdatePashuAPI(String PashuId) {
//        dialog = new ProgressDialog(requireContext());
//        dialog.setMessage(getString(R.string.saving_data));
//        dialog.setCancelable(false);
//        dialog.show();
//        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
//        final StringRequest postRequest = new StringRequest(Request.Method.POST, UPDATE_BYAT_DATA,
//                response -> {
//                    dialog.dismiss();
//                    Log.d("UpdateByatAPI", "" + response);
//                    try {
//                        JSONObject jsonObject1 = new JSONObject(response);
//                        String Status = jsonObject1.getString("status");
//                        String Message = jsonObject1.getString("message");
//                        if (Status.equalsIgnoreCase("success")) {
//                            Intent intent = new Intent(requireContext(), Checking.class);
//                            intent.putExtra("isVerified", true);
//                            intent.putExtra("message", Message);
//                            intent.putExtra("nextActivity", MainActivity.class);
//                            intent.putExtra("SELECT_DETAILS_FRAGMENT", true);
//                            startActivity(intent);
//                        } else {
//                            Intent intent = new Intent(requireContext(), Checking.class);
//                            intent.putExtra("isVerified", false);
//                            intent.putExtra("message", Message);
//                            intent.putExtra("nextActivity", MainActivity.class);
//                            intent.putExtra("SELECT_DETAILS_FRAGMENT", true);
//                            startActivity(intent);
//                        }
//                    } catch (JSONException e) {
//                        dialog.dismiss();
//                        if (e instanceof JSONException) {
//                            Toast.makeText(requireContext(), R.string.json_parsing_error, Toast.LENGTH_SHORT).show();
//                        } else {
//                            Log.d("CheckException",""+e);
//                            Toast.makeText(requireContext(), R.string.generic_error, Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                },
//                error -> {
//                    dialog.dismiss();
//                    if (error instanceof TimeoutError) {
//                        Toast.makeText(requireContext(), R.string.timeout_error, Toast.LENGTH_SHORT).show();
//                    } else if (error instanceof NoConnectionError) {
//                        Toast.makeText(requireContext(), R.string.no_connection_error, Toast.LENGTH_SHORT).show();
//                    } else if (error instanceof AuthFailureError) {
//                        Toast.makeText(requireContext(), R.string.auth_failure_error, Toast.LENGTH_SHORT).show();
//                    } else if (error instanceof ServerError) {
//                        Log.d("ServerError",""+error);
//                        Toast.makeText(requireContext(), R.string.server_error, Toast.LENGTH_SHORT).show();
//                    } else if (error instanceof NetworkError) {
//                        Toast.makeText(requireContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
//                    } else if (error instanceof ParseError) {
//                        Toast.makeText(requireContext(), R.string.parse_error, Toast.LENGTH_SHORT).show();
//                    } else {
//                        Toast.makeText(requireContext(), R.string.unknown_error, Toast.LENGTH_SHORT).show();
//                    }
//                    Log.d("Check", "" + error.getMessage());
//                }
//        ) {
//            @Override
//            protected Map<String, String> getParams() {
//                Map<String, String> params = new HashMap<>();
//                String updatedByatCount = ByatCnt.getText().toString();
//                String BullNo = bullNumber.getText().toString();
//
//                params.put("byat_id", byatId);
//                params.put("user_id", KisanId);
//                params.put("pashu_id", PashuId);
//                params.put("pashu_byat_count", updatedByatCount);
//                params.put("byat_gender", byatGender);
//
//                String byatBirthdate = AnimalBijdanDate.getText().toString();
//                SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy");
//                SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
//                try {
//                    Date date = inputFormat.parse(byatBirthdate);
//                    String formattedDate = outputFormat.format(date);
//                    params.put("byat_birthdate", formattedDate);
//                    Log.d("Params", "byat_birthdate: " + formattedDate);
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                    Log.e("Params", "Invalid date format: " + byatBirthdate);
//                }
//                if(BullNo.equals("गाँव बैल")){
//                    params.put("is_village_bull", bullNumber.getText().toString());
//                } else if (!BullNo.isEmpty() && !BullNo.equals("गाँव बैल")){
//                    params.put("is_village_bull", "");
//                }
//                params.put("doctor_number", DoctorNumber.getText().toString());
//                if (!BullNo.isEmpty() && !BullNo.equals("गाँव बैल")) {
//                    params.put("tarbar_bull_number", bullNumber.getText().toString());
//                } else {
//                    params.put("tarbar_bull_number", "");
//                }
//                Log.d("ByatParams", "byat_id: " + byatId);
//                Log.d("ByatParams", "user_id: " + KisanId);
//                Log.d("ByatParams", "pashu_id: " + PashuId);
//                Log.d("ByatParams", "pashu_byat_cnt: " + updatedByatCount);
//                Log.d("ByatParams", "byat_gender: " + byatGender);
//                if(BullNo.equals("गाँव बैल")){
//                    Log.d("ByatParams", "is_village_bull: " + bullNumber.getText().toString());
//                } else if (!BullNo.isEmpty() && !BullNo.equals("गाँव बैल")){
//                    Log.d("ByatParams", "is_village_bull: " + "");
//                }
//                Log.d("ByatParams", "doctor_number: " + DoctorNumber.getText().toString());
//                if (!BullNo.isEmpty() && !BullNo.equals("गाँव बैल")) {
//                    Log.d("ByatParams", "tarbar_bull_number: " + bullNumber.getText().toString());
//                } else {
//                    Log.d("ByatParams", "tarbar_bull_number: " + "");
//                }
//                return params;
//            }
//        };
//        postRequest.setRetryPolicy(ApiUtils.DEFAULT_RETRY_POLICY);
//        requestQueue.add(postRequest);
//    }

//    private void deleteData(String PashuId,String dataId) {
//        dialog = new ProgressDialog(requireContext());
//        dialog.setMessage(getString(R.string.deleting));
//        dialog.setCancelable(false);
//        dialog.show();
//        String url = DELETE_BYAT;
//        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        dialog.dismiss();
//                        try {
//                            Log.d("DeleteAnimalResponse",""+response);
//                            JSONObject jsonObject = new JSONObject(response);
//                            String status = jsonObject.getString("status");
//                            String Message = jsonObject.getString("message");
//                            if (status.equals("success")) {
//                                Intent intent = new Intent(requireContext(), Checking.class);
//                                intent.putExtra("isVerified", true);
//                                intent.putExtra("message", Message);
//                                intent.putExtra("nextActivity", MainActivity.class);
//                                intent.putExtra("SELECT_DETAILS_FRAGMENT", true);
//                                startActivity(intent);
//                            } else {
//                                Intent intent = new Intent(requireContext(), Checking.class);
//                                intent.putExtra("isVerified", false);
//                                intent.putExtra("message", Message);
//                                intent.putExtra("nextActivity", MainActivity.class);
//                                intent.putExtra("SELECT_DETAILS_FRAGMENT", true);
//                                startActivity(intent);
//                            }
//                        } catch (JSONException e) {
//                            dialog.dismiss();
//                            e.printStackTrace();
//                        }
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        dialog.dismiss();
//                        Toast.makeText(requireContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                }) {
//            @Override
//            protected Map<String, String> getParams() {
//                Map<String, String> params = new HashMap<>();
//                params.put("pashu_id", PashuId);
//                params.put("id", dataId);
//                return params;
//            }
//        };
//        stringRequest.setRetryPolicy(ApiUtils.DEFAULT_RETRY_POLICY);
//        requestQueue.add(stringRequest);
//    }

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
