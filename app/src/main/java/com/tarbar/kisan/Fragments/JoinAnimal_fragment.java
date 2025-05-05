package com.tarbar.kisan.Fragments;

import static com.tarbar.kisan.Helper.constant.ADD_ANIMAL;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.tarbar.kisan.Activities.Checking;
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

public class JoinAnimal_fragment extends Fragment {
    SharedPreferenceManager sharedPrefMgr;
    EditText AnimalNumber, AnimalType, AnimalGender, ByatNumber, AnimalBirthdate, KisanMobileNumber, KisanPassword;
    Spinner AnimalJati;
    ImageView cowImg, buffaloImg, back;
    String MobileNumber, userid,Password;
    ProgressDialog dialog;
    Button submit;
    int arrayId;
    ImageView calenderImg;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_animal, container, false);
        view.setBackgroundColor(Color.WHITE);
        initViews(view);
        sharedPrefMgr = new SharedPreferenceManager(requireContext());
        sharedPrefMgr.connectDB();
        userid = sharedPrefMgr.getString(Iconstant.userid);
        MobileNumber = sharedPrefMgr.getString(Iconstant.mobile);
        Password = sharedPrefMgr.getString(Iconstant.password);
        sharedPrefMgr.closeDB();

        KisanMobileNumber.setText(MobileNumber);
        AnimalType.setText("गाय");
        AnimalGender.setText("महिला");
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

        AnimalBirthdate.setFocusable(false);
        AnimalBirthdate.setClickable(true);

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
                AddAnimalAPI();
            }
        });
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

    private void AddAnimalAPI() {
        dialog = new ProgressDialog(requireContext());
        dialog.setMessage(getString(R.string.saving_data));
        dialog.setCancelable(false);
        dialog.show();
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        final StringRequest postRequest = new StringRequest(Request.Method.POST, ADD_ANIMAL,
                response -> {
                    dialog.dismiss();
                    Log.d("AddAnimalAPI", "" + response);
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
                            Log.d("CheckException", "" + e);
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
                        Log.d("ServerError", "" + error.getMessage());
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
                params.put("userid", userid);
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
                    Log.d("Request_Params", "animal_dob: " + formattedDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                params.put("byat_count", ByatNumber.getText().toString());
                params.put("kisan_number", KisanMobileNumber.getText().toString());

                Log.d("Request_Params", "userid: " + userid);
                Log.d("Request_Params", "number: " + AnimalNumber.getText().toString());
                Log.d("Request_Params", "type: " + AnimalType.getText().toString());
                Log.d("Request_Params", "animal_caste: " + casteOptions);
                Log.d("Request_Params", "animal_gender: " + AnimalGender.getText().toString());
                Log.d("Request_Params", "animal_dob: " + AnimalBirthdate.getText().toString());
                Log.d("Request_Params", "byat_count: " + ByatNumber.getText().toString());
                Log.d("Request_Params", "kisan_number: " + KisanMobileNumber.getText().toString());

                return params;
            }
        };
        postRequest.setRetryPolicy(ApiUtils.DEFAULT_RETRY_POLICY);
        requestQueue.add(postRequest);
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
