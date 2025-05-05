package com.tarbar.kisan.Fragments;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.tarbar.kisan.Helper.constant.ADD_ANIMAL_DOODH_DATA;
import static com.tarbar.kisan.Helper.constant.DELETE_DOODH_DATA;
import static com.tarbar.kisan.Helper.constant.FETCH_PROFILE;
import static com.tarbar.kisan.Helper.constant.GET_PARTICULAR_DOODH_DATA;
import static com.tarbar.kisan.Helper.constant.PROFILE_IMGAE_PATH;
import static com.tarbar.kisan.Helper.constant.UPDATE_PARTICULAR_DOODH_DATA;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
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
import com.squareup.picasso.Picasso;
import com.tarbar.kisan.Activities.Checking;
import com.tarbar.kisan.Activities.MainActivity;
import com.tarbar.kisan.Helper.ApiUtils;
import com.tarbar.kisan.Helper.Iconstant;
import com.tarbar.kisan.Helper.SharedPreferenceManager;
import com.tarbar.kisan.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class DoodhAddition_fragment extends Fragment {
    SharedPreferenceManager sharedPrefMgr;
    EditText AnimalDoodh,AnimalNumber, AnimalType, AnimalGender, ByatCnt,PashuFatherNumber,
            DoodhAdditionDate, KisanPassword,mobileNumber,
            KisanName,KisanState,KisanJilha,KisanTahsil,KisanVillage,fathername,caste;
    Button buttonGaonBull;
    ImageView back;
    String userid, Password;
    ProgressDialog dialog;
    Button submit;
    int arrayId;
    ImageView calenderImg,kisanImg;
    private String byatGender;
    Button DeleteDoodhData;
    String animalId,animalNumber,animalType,byatcnt,doodhId,KisanId;
    boolean initialForm,origionalForm;
    LinearLayout PasswordLayout;
    TextView title_password;
    String latestdoodh;
    CircleImageView profilepicture;
    private byte[] imageData;
    private List<Map<String, String>> byaatList2 = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_doodh_additon_form, container, false);
        view.setBackgroundColor(Color.WHITE);
        initViews(view);

        sharedPrefMgr = new SharedPreferenceManager(requireContext());
        sharedPrefMgr.connectDB();
        userid = sharedPrefMgr.getString(Iconstant.userid);
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
            doodhId = bundle.getString("doodhId");
            latestdoodh = bundle.getString("lastMilk");

            Log.d("ByatAdditionFragment", "KisanId: " + KisanId);
            Log.d("ByatAdditionFragment", "animalId: " + animalId);
            Log.d("ByatAdditionFragment", "animalNumber: " + animalNumber);
            Log.d("ByatAdditionFragment", "animalType: " + animalType);
            Log.d("ByatAdditionFragment", "byatcnt: " + byatcnt);
            Log.d("ByatAdditionFragment", "latestdoodh: " + latestdoodh);
            Log.d("ByatAdditionFragment", "doodhId: " + doodhId);
        } else {
            KisanId = "";
            animalId = "";
            animalNumber = "";
            animalType = "";
            byatcnt = "";
            latestdoodh = "";
            doodhId = "";
        }
        getKisanProfile(KisanId);
        if (initialForm) {
            getDoodhData(doodhId,animalId);
        } else if (origionalForm) {
            DoodhAdditionDate.setFocusable(false);
            DoodhAdditionDate.setClickable(true);
            buttonGaonBull.setVisibility(VISIBLE);
            DeleteDoodhData.setVisibility(GONE);
            AnimalNumber.setText(animalNumber);
            AnimalType.setText(animalType);
            ByatCnt.setText(byatcnt);
        }
        return view;
    }

    private void initViews(View view) {
        AnimalDoodh = view.findViewById(R.id.AnimalDoodh);
        PashuFatherNumber = view.findViewById(R.id.PashuFatherNumber);
        AnimalNumber = view.findViewById(R.id.AnimalNumber);
        AnimalType = view.findViewById(R.id.AnimalType);
        AnimalGender = view.findViewById(R.id.AnimalGender);
        ByatCnt =  view.findViewById(R.id.ByatCnt);
        KisanPassword = view.findViewById(R.id.KisanPassword);
        back = view.findViewById(R.id.back);
        submit = view.findViewById(R.id.submit);
        calenderImg = view.findViewById(R.id.calenderImg);
        DeleteDoodhData = view.findViewById(R.id.DeleteDoodhData);
        PasswordLayout = view.findViewById(R.id.PasswordLayout);
        title_password = view.findViewById(R.id.title_password);
        mobileNumber = view.findViewById(R.id.mobileNumber);
        KisanName = view.findViewById(R.id.KisanName);
        KisanState = view.findViewById(R.id.KisanState);
        KisanJilha = view.findViewById(R.id.KisanJilha);
        KisanTahsil = view.findViewById(R.id.KisanTahsil);
        KisanVillage = view.findViewById(R.id.KisanVillage);
        profilepicture = view.findViewById(R.id.profilepicture);
        kisanImg = view.findViewById(R.id.kisanImg);
        DoodhAdditionDate = view.findViewById(R.id.DoodhAdditionDate);
        buttonGaonBull = view.findViewById(R.id.buttonGaonBull);
        fathername = view.findViewById(R.id.fathername);
        caste = view.findViewById(R.id.caste);

        buttonGaonBull.setOnClickListener(v -> {
            if (PashuFatherNumber.getText().toString().isEmpty()) {
                PashuFatherNumber.setText(R.string.txt_village_bull);
            } else {
                PashuFatherNumber.setText("");
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.click_animation);
                v.startAnimation(animation);
                AnimalDoodh.setText("");
                AnimalNumber.setText("");
                AnimalType.setText("");
                ByatCnt.setText("");
                PashuFatherNumber.setText("");
                DoodhAdditionDate.setText("");
                KisanPassword.setText("");

                KisanName.setText("");
                fathername.setText("");
                caste.setText("");
                mobileNumber.setText("");
                KisanState.setText("");
                KisanJilha.setText("");
                KisanTahsil.setText("");
                KisanVillage.setText("");

                Intent intent = new Intent(requireContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                if (getActivity() != null) {
                    getActivity().finish();
                }
            }
        });

        calenderImg.setOnClickListener(v -> showDatePickerDialog());

        DoodhAdditionDate.setOnClickListener(v -> showDatePickerDialog());

        submit.setOnClickListener(v -> {
            Animation animation = AnimationUtils.loadAnimation(requireContext(), R.anim.click_animation);
            v.startAnimation(animation);

            String strAnimalDoodh = AnimalDoodh.getText().toString();
            String strFatherNumber = PashuFatherNumber.getText().toString();
            String strDate = DoodhAdditionDate.getText().toString();
            String strKisanPassword = KisanPassword.getText().toString();

            // Regular Expression for validation
            String validAnimalDoodhPattern = "^[0-9]{1,2}(\\.[0-9]{1,3})?$";

            if (strAnimalDoodh.isEmpty()) {
                AnimalDoodh.setError(getString(R.string.error_add_doodh));
                AnimalDoodh.setFocusable(true);
            } else if (strAnimalDoodh.length() > 6 || !strAnimalDoodh.matches(validAnimalDoodhPattern)) {
                AnimalDoodh.setError(getString(R.string.error_invalid_doodh_format));
                AnimalDoodh.setFocusable(true);
            } else if (strFatherNumber.isEmpty()) {
                Log.d("Validation", "Father number is empty.");
                PashuFatherNumber.setError(getString(R.string.error_add_pita_number));
                PashuFatherNumber.setFocusable(true);
            } else if (!strFatherNumber.equals("गाँव बैल") && (strFatherNumber.length() != 12 || !strFatherNumber.matches("\\d+"))) {
                Log.d("Validation", "Father number is invalid, must be 12 digits or 'गाँव बैल'.");
                PashuFatherNumber.setError(getString(R.string.error_12_digit_pita_number));
                PashuFatherNumber.setFocusable(true);
            }  else if (strDate.isEmpty()) {
                DoodhAdditionDate.setError(getString(R.string.error_enter_date));
                DoodhAdditionDate.setFocusable(true);
            } else if (strKisanPassword.isEmpty()) {
                KisanPassword.setError(getString(R.string.enter_password_number));
            } else if (strKisanPassword.length() < 4) {
                KisanPassword.setError(getString(R.string.enter_correct_password));
            } else if (!strKisanPassword.equals(Password)) {
                Toast.makeText(requireContext(), R.string.error_password_not_matching, Toast.LENGTH_SHORT).show();
            } else {
                if (initialForm) {
                    UpdateDoodhDataAPI(animalId);
                } else if (origionalForm) {
                    AddDoodhDataAPI();
                }
            }
        });

        DeleteDoodhData.setOnClickListener(v -> {
            Animation animation = AnimationUtils.loadAnimation(requireContext(), R.anim.click_animation);
            v.startAnimation(animation);
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(getString(R.string.confirm_delete_doodh_data));
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
        deleteDoodhData(animalId,doodhId);
    }

//    private void showDatePickerDialog() {
//        final Calendar calendar = Calendar.getInstance();
//        if (latestdoodh == null) {
//            int year = calendar.get(Calendar.YEAR);
//            int month = calendar.get(Calendar.MONTH);
//            int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
//
//            DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
//                    (view, year1, monthOfYear, dayOfMonth1) -> {
//                        String selectedDate = String.format("%02d/%02d/%d", dayOfMonth1, monthOfYear + 1, year1);
//                        DoodhAdditionDate.setText(selectedDate);
//                    }, year, month, dayOfMonth);
//            datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
//            datePickerDialog.show();
//        } else {
//            try {
//                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
//                Date latestDate = sdf.parse(latestdoodh);
//
//                Calendar minCalendar = Calendar.getInstance();
//                minCalendar.setTime(latestDate);
//                minCalendar.add(Calendar.DAY_OF_YEAR, 301);
//
//                int year = calendar.get(Calendar.YEAR);
//                int month = calendar.get(Calendar.MONTH);
//                int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
//
//                DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
//                        (view, year1, monthOfYear, dayOfMonth1) -> {
//                            String selectedDate = String.format("%02d/%02d/%d", dayOfMonth1, monthOfYear + 1, year1);
//                            DoodhAdditionDate.setText(selectedDate);
//                        }, year, month, dayOfMonth);
//
//                datePickerDialog.getDatePicker().setMinDate(minCalendar.getTimeInMillis());
//                datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
//                datePickerDialog.show();
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//        }
//    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                (view, year1, monthOfYear, dayOfMonth1) -> {
                    String selectedDate = String.format("%02d/%02d/%d", dayOfMonth1, monthOfYear + 1, year1);
                    DoodhAdditionDate.setText(selectedDate);
                }, year, month, dayOfMonth);
        datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
        datePickerDialog.show();
    }

    //    Add Doodh Data
    private void AddDoodhDataAPI() {
        dialog = new ProgressDialog(requireContext());
        dialog.setMessage(getString(R.string.saving_data));
        dialog.setCancelable(false);
        dialog.show();
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        final StringRequest postRequest = new StringRequest(Request.Method.POST, ADD_ANIMAL_DOODH_DATA,
                response -> {
                    dialog.dismiss();
                    Log.d("AddDoodhDataAPI", "" + response);
                    try {
                        JSONObject jsonObject1 = new JSONObject(response);
                        String Status = jsonObject1.getString("status");
                        String Message = jsonObject1.getString("message");
                        if (Status.equalsIgnoreCase("success")) {
                            Intent intent = new Intent(requireContext(), Checking.class);
                            intent.putExtra("isVerified", true);
                            intent.putExtra("message", Message);
                            intent.putExtra("nextActivity", MainActivity.class);
                            intent.putExtra("SELECT_MILK_FRAGMENT", true);
                            startActivity(intent);
                            AnimalDoodh.setText("");
                            AnimalNumber.setText("");
                            AnimalType.setText("");
                            ByatCnt.setText("");
                            PashuFatherNumber.setText("");
                            DoodhAdditionDate.setText("");
                            KisanPassword.setText("");
                            KisanName.setText("");
                            fathername.setText("");
                            caste.setText("");
                            mobileNumber.setText("");
                            KisanState.setText("");
                            KisanJilha.setText("");
                            KisanTahsil.setText("");
                            KisanVillage.setText("");

                        } else {
                            Intent intent = new Intent(requireContext(), Checking.class);
                            intent.putExtra("isVerified", false);
                            intent.putExtra("message", Message);
                            intent.putExtra("nextActivity", MainActivity.class);
                            intent.putExtra("SELECT_MILK_FRAGMENT", true);
                            startActivity(intent);
                            AnimalDoodh.setText("");
                            AnimalNumber.setText("");
                            AnimalType.setText("");
                            ByatCnt.setText("");
                            PashuFatherNumber.setText("");
                            DoodhAdditionDate.setText("");
                            KisanPassword.setText("");
                            KisanName.setText("");
                            fathername.setText("");
                            caste.setText("");
                            mobileNumber.setText("");
                            KisanState.setText("");
                            KisanJilha.setText("");
                            KisanTahsil.setText("");
                            KisanVillage.setText("");
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
                params.put("userid", KisanId);
                params.put("pashu_id", animalId);
                params.put("pashu_doodh", AnimalDoodh.getText().toString());
                params.put("pashu_pita", PashuFatherNumber.getText().toString());
                String additionBirthdate = DoodhAdditionDate.getText().toString();
                SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy");
                SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    Date date = inputFormat.parse(additionBirthdate);
                    String formattedDate = outputFormat.format(date);
                    params.put("date", formattedDate);
                    Log.d("DoodhParams", "date: " + formattedDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                    Log.e("DoodhParams", "Invalid date format: " + additionBirthdate);
                }
                Log.d("DoodhParams", "userid: " + KisanId);
                Log.d("DoodhParams", "pashu_id: " + animalId);
                Log.d("DoodhParams", "pashu_doodh: " + AnimalDoodh.getText().toString());
                Log.d("DoodhParams", "pashu_pita: " + PashuFatherNumber.getText().toString());
                return params;
            }
        };
        postRequest.setRetryPolicy(ApiUtils.DEFAULT_RETRY_POLICY);
        requestQueue.add(postRequest);
    }

//  2)

//  Get doodh data of particular doodh record.
    private void getDoodhData(String DoodhId, String AnimalId) {
    dialog = new ProgressDialog(getActivity());
    dialog.setMessage(getString(R.string.getting_data));
    dialog.setCancelable(false);
    dialog.show();
    String url = GET_PARTICULAR_DOODH_DATA;
    RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
    StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    dialog.dismiss();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");
                        if ("success".equals(status)) {
                            JSONObject data = jsonObject.getJSONObject("data");
                            String userId = data.getString("userid");
                            String pashuDoodh = data.getString("pashu_doodh");
                            String pashuPitaNumber = data.getString("pashu_pita");
                            String animalNumber = data.getString("number");
                            String animalType = data.getString("type");
                            String byatcnt = data.getString("byat_count");

                            if (userid.equals(userId)){
                                submit.setVisibility(VISIBLE);
                                DeleteDoodhData.setVisibility(VISIBLE);
                                title_password.setVisibility(VISIBLE);
                                PasswordLayout.setVisibility(VISIBLE);
                                buttonGaonBull.setVisibility(VISIBLE);

                                calenderImg.setClickable(true);
                                calenderImg.setEnabled(true);

                                DoodhAdditionDate.setClickable(true);
                                DoodhAdditionDate.setFocusable(true);
                                AnimalDoodh.setClickable(true);
                                AnimalDoodh.setFocusable(true);
                                PashuFatherNumber.setClickable(true);
                                PashuFatherNumber.setFocusable(true);
                                DoodhAdditionDate.setClickable(true);
                                DoodhAdditionDate.setFocusable(true);
                                KisanPassword.setClickable(true);
                                KisanPassword.setFocusable(true);

                                String originalDate = data.getString("date");
                                try {
                                    SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd");
                                    Date date = originalFormat.parse(originalDate);
                                    SimpleDateFormat desiredFormat = new SimpleDateFormat("dd/MM/yyyy");
                                    String formattedDate = desiredFormat.format(date);
                                    DoodhAdditionDate.setText(formattedDate);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                    DoodhAdditionDate.setText("");
                                }
                                AnimalDoodh.setText(pashuDoodh);
                                PashuFatherNumber.setText(pashuPitaNumber);
                                AnimalNumber.setText(animalNumber);
                                AnimalType.setText(animalType);
                                ByatCnt.setText(byatcnt);
                            } else {
                                submit.setVisibility(GONE);
                                DeleteDoodhData.setVisibility(GONE);
                                title_password.setVisibility(GONE);
                                PasswordLayout.setVisibility(GONE);
                                buttonGaonBull.setVisibility(GONE);
                                calenderImg.setClickable(false);
                                calenderImg.setEnabled(false);

                                DoodhAdditionDate.setClickable(false);
                                DoodhAdditionDate.setFocusable(false);
                                DoodhAdditionDate.setFocusableInTouchMode(false);
                                DoodhAdditionDate.setLongClickable(false);
                                AnimalDoodh.setClickable(false);
                                AnimalDoodh.setFocusable(false);
                                PashuFatherNumber.setClickable(false);
                                PashuFatherNumber.setFocusable(false);
                                DoodhAdditionDate.setClickable(false);
                                DoodhAdditionDate.setFocusable(false);

                                String originalDate = data.getString("date");
                                try {
                                    SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd");
                                    Date date = originalFormat.parse(originalDate);
                                    SimpleDateFormat desiredFormat = new SimpleDateFormat("dd/MM/yyyy");
                                    String formattedDate = desiredFormat.format(date);
                                    DoodhAdditionDate.setText(formattedDate);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                    DoodhAdditionDate.setText("");
                                }
                                AnimalDoodh.setText(pashuDoodh);
                                PashuFatherNumber.setText(pashuPitaNumber);
                                AnimalNumber.setText(animalNumber);
                                AnimalType.setText(animalType);
                                ByatCnt.setText(byatcnt);
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
                    Toast.makeText(getActivity(), R.string.generic_error, Toast.LENGTH_SHORT).show();
                    Log.d("Check", "" + error.getMessage());
                }
            }) {
        @Override
        protected Map<String, String> getParams() {
            Map<String, String> params = new HashMap<>();
            params.put("id", DoodhId);
            params.put("animalId", AnimalId);
            Log.d("DoodhId", "" + DoodhId);
            Log.d("animalId", "" + AnimalId);
            return params;
        }
    };
    stringRequest.setRetryPolicy(ApiUtils.DEFAULT_RETRY_POLICY);
    requestQueue.add(stringRequest);
}

    private void UpdateDoodhDataAPI(String PashuId) {
        dialog = new ProgressDialog(requireContext());
        dialog.setMessage(getString(R.string.saving_data));
        dialog.setCancelable(false);
        dialog.show();
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        final StringRequest postRequest = new StringRequest(Request.Method.POST, UPDATE_PARTICULAR_DOODH_DATA,
                response -> {
                    dialog.dismiss();
                    Log.d("UpdateDoodhDataAPI", "" + response);
                    try {
                        JSONObject jsonObject1 = new JSONObject(response);
                        String Status = jsonObject1.getString("status");
                        String Message = jsonObject1.getString("message");
                        if (Status.equalsIgnoreCase("success")) {
                            Intent intent = new Intent(requireContext(), Checking.class);
                            intent.putExtra("isVerified", true);
                            intent.putExtra("message", Message);
                            intent.putExtra("nextActivity", MainActivity.class);
                            intent.putExtra("SELECT_MILK_FRAGMENT", true);
                            startActivity(intent);

                            AnimalDoodh.setText("");
                            AnimalNumber.setText("");
                            AnimalType.setText("");
                            ByatCnt.setText("");
                            PashuFatherNumber.setText("");
                            DoodhAdditionDate.setText("");
                            KisanPassword.setText("");
                            KisanName.setText("");
                            fathername.setText("");
                            caste.setText("");
                            mobileNumber.setText("");
                            KisanState.setText("");
                            KisanJilha.setText("");
                            KisanTahsil.setText("");
                            KisanVillage.setText("");
                        } else {
                            Intent intent = new Intent(requireContext(), Checking.class);
                            intent.putExtra("isVerified", false);
                            intent.putExtra("message", Message);
                            intent.putExtra("nextActivity", MainActivity.class);
                            intent.putExtra("SELECT_MILK_FRAGMENT", true);
                            startActivity(intent);

                            AnimalDoodh.setText("");
                            AnimalNumber.setText("");
                            AnimalType.setText("");
                            ByatCnt.setText("");
                            PashuFatherNumber.setText("");
                            DoodhAdditionDate.setText("");
                            KisanPassword.setText("");
                            KisanName.setText("");
                            fathername.setText("");
                            caste.setText("");
                            mobileNumber.setText("");
                            KisanState.setText("");
                            KisanJilha.setText("");
                            KisanTahsil.setText("");
                            KisanVillage.setText("");
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
                params.put("id", doodhId);
                params.put("pashu_id", PashuId);
                params.put("pashu_doodh", AnimalDoodh.getText().toString());
                params.put("pashu_pita", PashuFatherNumber.getText().toString());

                String additionDate = DoodhAdditionDate.getText().toString();
                SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy");
                SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    Date date = inputFormat.parse(additionDate);
                    String formattedDate = outputFormat.format(date);
                    params.put("date", formattedDate);
                    Log.d("DoodhParams", "date: " + formattedDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                    Log.e("DoodhParams", "Invalid date format: " + additionDate);
                }

                Log.d("DoodhParams", "doodh_id: " + doodhId);
                Log.d("DoodhParams", "pashu_id: " + PashuId);
                Log.d("DoodhParams", "pashu_doodh: " + AnimalDoodh.getText().toString());
                Log.d("DoodhParams", "pashu_pita: " + PashuFatherNumber.getText().toString());
                return params;
            }
        };
        postRequest.setRetryPolicy(ApiUtils.DEFAULT_RETRY_POLICY);
        requestQueue.add(postRequest);
    }

    private void deleteDoodhData(String PashuId, String DoodhId) {
        dialog = new ProgressDialog(requireContext());
        dialog.setMessage(getString(R.string.deleting));
        dialog.setCancelable(false);
        dialog.show();
        String url = DELETE_DOODH_DATA;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        try {
                            Log.d("DeleteAnimalResponse", "" + response);
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("status");
                            String Message = jsonObject.getString("message");
                            if (status.equals("success")) {
                                Intent intent = new Intent(requireContext(), Checking.class);
                                intent.putExtra("isVerified", true);
                                intent.putExtra("message", Message);
                                intent.putExtra("nextActivity", MainActivity.class);
                                intent.putExtra("SELECT_MILK_FRAGMENT", true);
                                startActivity(intent);
                                AnimalDoodh.setText("");
                                AnimalNumber.setText("");
                                AnimalType.setText("");
                                ByatCnt.setText("");
                                PashuFatherNumber.setText("");
                                DoodhAdditionDate.setText("");
                                KisanPassword.setText("");
                                KisanName.setText("");
                                fathername.setText("");
                                caste.setText("");
                                mobileNumber.setText("");
                                KisanState.setText("");
                                KisanJilha.setText("");
                                KisanTahsil.setText("");
                                KisanVillage.setText("");
                            } else {
                                Intent intent = new Intent(requireContext(), Checking.class);
                                intent.putExtra("isVerified", false);
                                intent.putExtra("message", Message);
                                intent.putExtra("nextActivity", MainActivity.class);
                                intent.putExtra("SELECT_MILK_FRAGMENT", true);
                                startActivity(intent);
                                AnimalDoodh.setText("");
                                AnimalNumber.setText("");
                                AnimalType.setText("");
                                ByatCnt.setText("");
                                PashuFatherNumber.setText("");
                                DoodhAdditionDate.setText("");
                                KisanPassword.setText("");
                                KisanName.setText("");
                                fathername.setText("");
                                caste.setText("");
                                mobileNumber.setText("");
                                KisanState.setText("");
                                KisanJilha.setText("");
                                KisanTahsil.setText("");
                                KisanVillage.setText("");
                            }
                        } catch (JSONException e) {
                            dialog.dismiss();
                            if (e instanceof JSONException) {
                                Log.d("JSONException_ServerError",""+e);
                                Toast.makeText(requireContext(), R.string.json_parsing_error, Toast.LENGTH_SHORT).show();
                            } else {
                                Log.d("CheckException",""+e);
                                Toast.makeText(requireContext(), R.string.generic_error, Toast.LENGTH_SHORT).show();
                            }
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
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("pashu_id", PashuId);
                params.put("doodh_id", DoodhId);
                Log.d("DoodhParams", "pashu_id: " + PashuId);
                Log.d("DoodhParams", "doodh_id: " + DoodhId);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        stringRequest.setRetryPolicy(ApiUtils.DEFAULT_RETRY_POLICY);
        requestQueue.add(stringRequest);
    }

//    3)
//    Kisan Profile

    private void getKisanProfile(String KisanId) {
        String url = FETCH_PROFILE;
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.optString("status");
                        String message = jsonObject.optString("message");

                        if ("success".equalsIgnoreCase(status)) {
                            JSONArray dataArray = jsonObject.optJSONArray("data");
                            if (dataArray != null && dataArray.length() > 0) {
                                JSONObject userData = dataArray.optJSONObject(0);

                                String kisanName = userData.optString("name", "");
                                String kisanMobile = userData.optString("mobile", "");
                                String kisanJilha = userData.optString("jilha", "");
                                String kisanTahsil = userData.optString("tahsil", "");
                                String kisanVillage = userData.optString("village", "");
                                String kisanState = userData.optString("state", "");
                                String KisanFatherName = userData.getString("father_name");
                                String KisanCaste = userData.getString("caste");
                                String profilePic = userData.optString("profile_pic", "");

                                KisanName.setText("null".equalsIgnoreCase(kisanName) ? "" : kisanName);
                                mobileNumber.setText("null".equalsIgnoreCase(kisanMobile) ? "" : kisanMobile);
                                KisanJilha.setText("null".equalsIgnoreCase(kisanJilha) ? "" : kisanJilha);
                                KisanTahsil.setText("null".equalsIgnoreCase(kisanTahsil) ? "" : kisanTahsil);
                                KisanVillage.setText("null".equalsIgnoreCase(kisanVillage) ? "" : kisanVillage);
                                KisanState.setText("null".equalsIgnoreCase(kisanState) ? "" : kisanState);
                                fathername.setText("null".equalsIgnoreCase(KisanFatherName) ? "" : KisanFatherName);
                                caste.setText("null".equalsIgnoreCase(KisanCaste) ? "" : KisanCaste);

                                String imageUrl = PROFILE_IMGAE_PATH + profilePic;
                                Picasso.get()
                                        .load(imageUrl)
                                        .into(profilepicture, new com.squareup.picasso.Callback() {
                                            @Override
                                            public void onSuccess() {
                                                profilepicture.setVisibility(View.VISIBLE);
                                                kisanImg.setVisibility(View.GONE);

                                                Bitmap bitmap = ((BitmapDrawable) profilepicture.getDrawable()).getBitmap();
                                                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                                bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                                                imageData = byteArrayOutputStream.toByteArray();
                                                Log.d("ImageDataLength", "Length: " + imageData.length);
                                            }

                                            @Override
                                            public void onError(Exception e) {
                                                kisanImg.setVisibility(View.VISIBLE);
                                                profilepicture.setVisibility(View.GONE);
                                            }
                                        });
                            } else {
                                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(getActivity(), R.string.json_parsing_error, Toast.LENGTH_SHORT).show();
                        Log.d("CheckException", "JSON Exception: " + e.getMessage());
                    }
                },
                error -> {
                    Toast.makeText(getActivity(), R.string.generic_error, Toast.LENGTH_SHORT).show();
                    Log.d("Check", "Volley Error: " + error.getMessage());
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("userid", KisanId);
                Log.d("CheckUserID", "UserID: " + KisanId);
                return params;
            }
        };
        stringRequest.setRetryPolicy(ApiUtils.DEFAULT_RETRY_POLICY);
        requestQueue.add(stringRequest);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        requireActivity().getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                AnimalDoodh.setText("");
                AnimalNumber.setText("");
                AnimalType.setText("");
                ByatCnt.setText("");
                PashuFatherNumber.setText("");
                DoodhAdditionDate.setText("");
                KisanPassword.setText("");
                KisanName.setText("");
                fathername.setText("");
                caste.setText("");
                mobileNumber.setText("");
                KisanState.setText("");
                KisanJilha.setText("");
                KisanTahsil.setText("");
                KisanVillage.setText("");

                Intent intent = new Intent(requireContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });
    }
}
