package com.tarbar.kisan.Fragments;

import static android.view.View.GONE;
import static com.tarbar.kisan.Helper.constant.ADD_SELLING_DETAILS;
import static com.tarbar.kisan.Helper.constant.DELETE_PARTICULAR_SELLING_DETAILS;
import static com.tarbar.kisan.Helper.constant.GET_PARTICULAR_SELLING_DETAILS;
import static com.tarbar.kisan.Helper.constant.UPDATE_PARTICULAR_SELLING_DETAILS;
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
import com.tarbar.kisan.Activities.LoadFilterFragments;
import com.tarbar.kisan.Activities.LoadFormFragments;
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
import java.util.Locale;

public class PashuUpdateForm_fragment extends Fragment {
    SharedPreferenceManager sharedPrefMgr;
    EditText AnimalType, SellerMobileNumber, SellingDate, KisanMobileNumber, KisanState, KisanJilha, KisanTahsil, KisanVillage, KisanPassword;
    String MobileNumber, userid, Password;
    ProgressDialog dialog;
    Button submit;
    ImageView calenderImg;
    Button Delete;
    String Animal_Type, sellinId;
    boolean initialForm, origionalForm;
    LinearLayout PasswordLayout;
    TextView title_password;
    ImageView cowImg, buffaloImg;
    TextView txtState, txtJilha, txtTahsil, txtVillage;
    LinearLayout lljilha, llvillage, lltahsil, llstate;
    private LoadFormFragments loadFormFragments;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pashu_update_form, container, false);
        view.setBackgroundColor(Color.WHITE);
        initViews(view);

        sharedPrefMgr = new SharedPreferenceManager(requireContext());
        sharedPrefMgr.connectDB();
        userid = sharedPrefMgr.getString(Iconstant.userid);
        MobileNumber = sharedPrefMgr.getString(Iconstant.mobile);
        Password = sharedPrefMgr.getString(Iconstant.password);
        sharedPrefMgr.closeDB();

        origionalForm = getArguments() != null && getArguments().getBoolean("addForm", false);
        initialForm = getArguments() != null && getArguments().getBoolean("editForm", false);

        Bundle bundle = getArguments();
        if (bundle != null) {
            Animal_Type = bundle.getString("animalType");
            sellinId = bundle.getString("selling_id");
        } else {
            Animal_Type = "";
            sellinId = "";
        }

        loadFormFragments = (LoadFormFragments) getActivity();
        if (loadFormFragments != null) {
            loadFormFragments.back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.click_animation);
                    v.startAnimation(animation);
                    AnimalType.setText("");
                    SellerMobileNumber.setText("");
                    SellingDate.setText("");
                    KisanMobileNumber.setText("");
                    KisanState.setText("");
                    KisanJilha.setText("");
                    KisanTahsil.setText("");
                    KisanVillage.setText("");
                    KisanPassword.setText("");
                    Intent intent = new Intent(requireContext(), LoadFilterFragments.class);
                    intent.putExtra("PashuUpdate_fragment", true);
                    startActivity(intent);
                    requireActivity().finish();
                }
            });
        }

        if (initialForm) {
            Delete.setVisibility(View.VISIBLE);
            SellingDate.setFocusable(false);
            SellingDate.setClickable(true);
            calenderImg.setClickable(false);
            calenderImg.setEnabled(false);
            getSellingDetails(sellinId);
        } else if (origionalForm) {
            cowImg.setOnClickListener(null);
            buffaloImg.setOnClickListener(null);
            Delete.setVisibility(GONE);
            txtState.setVisibility(GONE);
            txtJilha.setVisibility(GONE);
            txtTahsil.setVisibility(GONE);
            txtVillage.setVisibility(GONE);
            llstate.setVisibility(GONE);
            lljilha.setVisibility(GONE);
            llvillage.setVisibility(GONE);
            lltahsil.setVisibility(GONE);
            AnimalType.setText(Animal_Type);
            KisanMobileNumber.setText(MobileNumber);
        }
        return view;
    }

    private void initViews(View view) {
        AnimalType = view.findViewById(R.id.AnimalType);
        SellerMobileNumber = view.findViewById(R.id.SellerMobileNumber);
        SellingDate = view.findViewById(R.id.SellingDate);
        KisanMobileNumber = view.findViewById(R.id.KisanMobileNumber);
        KisanState = view.findViewById(R.id.KisanState);
        KisanJilha = view.findViewById(R.id.KisanJilha);
        KisanTahsil = view.findViewById(R.id.KisanTahsil);
        KisanVillage = view.findViewById(R.id.KisanVillage);
        KisanPassword = view.findViewById(R.id.KisanPassword);
        submit = view.findViewById(R.id.submit);
        calenderImg = view.findViewById(R.id.calenderImg);
        Delete = view.findViewById(R.id.Delete);
        PasswordLayout = view.findViewById(R.id.PasswordLayout);
        title_password = view.findViewById(R.id.title_password);
        cowImg = view.findViewById(R.id.cowImg);
        buffaloImg = view.findViewById(R.id.buffaloImg);
        txtState = view.findViewById(R.id.txtState);
        txtJilha = view.findViewById(R.id.txtJilha);
        txtTahsil = view.findViewById(R.id.txtTahsil);
        txtVillage = view.findViewById(R.id.txtVillage);
        lljilha = view.findViewById(R.id.lljilha);
        llvillage = view.findViewById(R.id.llvillage);
        lltahsil = view.findViewById(R.id.lltahsil);
        llstate = view.findViewById(R.id.llstate);

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

        calenderImg.setOnClickListener(v -> showDatePickerDialog());
        SellingDate.setOnClickListener(v -> showDatePickerDialog());

        submit.setOnClickListener(v -> {
            Animation animation = AnimationUtils.loadAnimation(requireContext(), R.anim.click_animation);
            v.startAnimation(animation);
            String regex = "^[6789]\\d{9}$";
            String strSellerMobile = SellerMobileNumber.getText().toString();
            String strSellingDate = SellingDate.getText().toString();
            String strKisanPassword = KisanPassword.getText().toString();
            String strKisanMobile = KisanMobileNumber.getText().toString();

            if (strSellerMobile.isEmpty()) {
                SellerMobileNumber.setError(getString(R.string.error_enter_seller_mobile));
            } else if (!strSellerMobile.matches(regex)) {
                SellerMobileNumber.setError(getString(R.string.error_invalid_seller_mobile));
            } else if (strSellerMobile.equals(strKisanMobile)) {
                SellerMobileNumber.setError(getString(R.string.error_valid_seller_number));
            } else if (strSellingDate.isEmpty()) {
                SellingDate.setError(getString(R.string.error_byat_birhtdate));
                SellingDate.setFocusable(true);
            } else if (strKisanPassword.isEmpty()) {
                KisanPassword.setError(getString(R.string.enter_password_number));
            } else if (strKisanPassword.length() < 4) {
                KisanPassword.setError(getString(R.string.enter_correct_password));
            } else if (!strKisanPassword.equals(Password)) {
                Toast.makeText(requireContext(), R.string.error_password_not_matching, Toast.LENGTH_SHORT).show();
            } else {
                if (initialForm) {
                    UpdateSellingDetails(sellinId);
                } else if (origionalForm) {
                    AddSellingDetails();
                }
            }
        });

        Delete.setOnClickListener(v -> {
            Animation animation = AnimationUtils.loadAnimation(requireContext(), R.anim.click_animation);
            v.startAnimation(animation);
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(getString(R.string.confirm_delete_selling_details));
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
        deleteData(sellinId);
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                (view, year1, monthOfYear, dayOfMonth1) -> {
                    String selectedDate = String.format("%02d/%02d/%d", dayOfMonth1, monthOfYear + 1, year1);
                    SellingDate.setText(selectedDate);
                }, year, month, dayOfMonth);
        datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
        datePickerDialog.show();
    }

    private void AddSellingDetails() {
        dialog = new ProgressDialog(requireContext());
        dialog.setMessage(getString(R.string.saving_data));
        dialog.setCancelable(false);
        dialog.show();
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        final StringRequest postRequest = new StringRequest(Request.Method.POST, ADD_SELLING_DETAILS,
                response -> {
                    dialog.dismiss();
                    Log.d("AddSellingDetailsAPI", "" + response);
                    try {
                        JSONObject jsonObject1 = new JSONObject(response);
                        String Status = jsonObject1.getString("status");
                        String Message = jsonObject1.getString("message");

                        if (Status.equalsIgnoreCase("success")) {
                            AnimalType.setText("");
                            SellerMobileNumber.setText("");
                            SellingDate.setText("");
                            KisanMobileNumber.setText("");
                            KisanState.setText("");
                            KisanJilha.setText("");
                            KisanTahsil.setText("");
                            KisanVillage.setText("");
                            KisanPassword.setText("");
                            Intent intent = new Intent(requireContext(), Checking.class);
                            intent.putExtra("isVerified", true);
                            intent.putExtra("message", Message);
                            intent.putExtra("nextActivity", LoadFilterFragments.class);
                            intent.putExtra("PashuUpdate_fragment", true);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(requireContext(), Checking.class);
                            intent.putExtra("isVerified", false);
                            intent.putExtra("message", Message);
                            intent.putExtra("nextActivity", LoadFilterFragments.class);
                            intent.putExtra("PashuUpdate_fragment", true);
                            startActivity(intent);
                        }
                    } catch (JSONException e) {
                        dialog.dismiss();
                        if (e instanceof JSONException) {
                            Toast.makeText(getContext(), R.string.json_parsing_error, Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d("CheckException",""+e);
                            Toast.makeText(getContext(), R.string.generic_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                error -> {
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
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                String pashuType = AnimalType.getText().toString();
                String sellerNumber = SellerMobileNumber.getText().toString();
                String sellingDateInput = SellingDate.getText().toString();
                String formattedSellingDate = sellingDateInput;
                try {
                    SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    Date date = inputFormat.parse(sellingDateInput);
                    formattedSellingDate = outputFormat.format(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                params.put("userid", userid);
                params.put("pashu_type", pashuType);
                params.put("seller_number", sellerNumber);
                params.put("selling_date", formattedSellingDate);
                Log.d("UpdateParams", "userid: " + userid);
                Log.d("UpdateParams", "pashu_type: " + pashuType);
                Log.d("UpdateParams", "seller_number: " + sellerNumber);
                Log.d("UpdateParams", "selling_date: " + formattedSellingDate);
                return params;
            }
        };
        postRequest.setRetryPolicy(ApiUtils.DEFAULT_RETRY_POLICY);
        requestQueue.add(postRequest);
    }

    private void getSellingDetails(String sellingId) {
        ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage(getString(R.string.getting_data));
        dialog.setCancelable(false);
        dialog.show();
        String url = GET_PARTICULAR_SELLING_DETAILS;
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    dialog.dismiss();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");
                        if ("success".equals(status)) {
                            JSONObject data = jsonObject.getJSONObject("data");
                            String pashuType = data.getString("pashu_type");
                            String sellerNumber = data.getString("seller_number");
                            String sellingDate = data.getString("selling_date");
                            String kisanNumber = data.getString("kisan_number");
                            String jilha = data.getString("jilha");
                            String tahsil = data.getString("tahsil");
                            String village = data.getString("village");
                            String state = data.getString("state");
                            AnimalType.setText(isValid(pashuType) ? pashuType : "");
                            SellerMobileNumber.setText(isValid(sellerNumber) ? sellerNumber : "");
                            SellingDate.setText(isValid(sellingDate) ? sellingDate : "");
                            KisanMobileNumber.setText(isValid(kisanNumber) ? kisanNumber : "");
                            KisanState.setText(isValid(state) ? state : "");
                            KisanJilha.setText(isValid(jilha) ? jilha : "");
                            KisanTahsil.setText(isValid(tahsil) ? tahsil : "");
                            KisanVillage.setText(isValid(village) ? village : "");
                        } else {
                            String message = jsonObject.getString("message");
                            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        dialog.dismiss();
                        if (e instanceof JSONException) {
                            Toast.makeText(getContext(), R.string.json_parsing_error, Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d("CheckException",""+e);
                            Toast.makeText(getContext(), R.string.generic_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                error -> {
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
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", sellingId);
                return params;
            }
        };
        stringRequest.setRetryPolicy(ApiUtils.DEFAULT_RETRY_POLICY);
        requestQueue.add(stringRequest);
    }

    private boolean isValid(String value) {
        return value != null && !value.isEmpty() && !value.equalsIgnoreCase("null");
    }

    private void UpdateSellingDetails(String SellinId) {
        dialog = new ProgressDialog(requireContext());
        dialog.setMessage(getString(R.string.saving_data));
        dialog.setCancelable(false);
        dialog.show();
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        final StringRequest postRequest = new StringRequest(Request.Method.POST, UPDATE_PARTICULAR_SELLING_DETAILS,
                response -> {
                    dialog.dismiss();
                    Log.d("UpdateSellingDetailsAPI", "" + response);
                    try {
                        JSONObject jsonObject1 = new JSONObject(response);
                        String Status = jsonObject1.getString("status");
                        String Message = jsonObject1.getString("message");
                        if (Status.equalsIgnoreCase("success")) {
                            Intent intent = new Intent(requireContext(), Checking.class);
                            intent.putExtra("isVerified", true);
                            intent.putExtra("message", Message);
                            intent.putExtra("nextActivity", LoadFilterFragments.class);
                            intent.putExtra("PashuUpdate_fragment", true);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(requireContext(), Checking.class);
                            intent.putExtra("isVerified", false);
                            intent.putExtra("message", Message);
                            intent.putExtra("nextActivity", LoadFilterFragments.class);
                            intent.putExtra("PashuUpdate_fragment", true);
                            startActivity(intent);
                        }
                    } catch (JSONException e) {
                        dialog.dismiss();
                        if (e instanceof JSONException) {
                            Toast.makeText(getContext(), R.string.json_parsing_error, Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d("CheckException",""+e);
                            Toast.makeText(getContext(), R.string.generic_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                error -> {
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
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                String pashuType = AnimalType.getText().toString();
                String sellerNumber = SellerMobileNumber.getText().toString();
                params.put("id", SellinId);
                params.put("pashu_type", pashuType);
                params.put("seller_number", sellerNumber);
                Log.d("UpdateParams", "id: " + SellinId);
                Log.d("UpdateParams", "pashu_type: " + pashuType);
                Log.d("UpdateParams", "seller_number: " + sellerNumber);
                return params;
            }
        };
        postRequest.setRetryPolicy(ApiUtils.DEFAULT_RETRY_POLICY);
        requestQueue.add(postRequest);
    }

    private void deleteData(String SellinId) {
        dialog = new ProgressDialog(requireContext());
        dialog.setMessage(getString(R.string.deleting));
        dialog.setCancelable(false);
        dialog.show();
        String url = DELETE_PARTICULAR_SELLING_DETAILS;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
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
                            intent.putExtra("nextActivity", LoadFilterFragments.class);
                            intent.putExtra("PashuUpdate_fragment", true);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(requireContext(), Checking.class);
                            intent.putExtra("isVerified", false);
                            intent.putExtra("message", Message);
                            intent.putExtra("nextActivity", LoadFilterFragments.class);
                            intent.putExtra("PashuUpdate_fragment", true);
                            startActivity(intent);
                        }
                    } catch (JSONException e) {
                        dialog.dismiss();
                        if (e instanceof JSONException) {
                            Toast.makeText(getContext(), R.string.json_parsing_error, Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d("CheckException",""+e);
                            Toast.makeText(getContext(), R.string.generic_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                error -> {
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
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", SellinId);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        stringRequest.setRetryPolicy(ApiUtils.DEFAULT_RETRY_POLICY);
        requestQueue.add(stringRequest);
    }

}