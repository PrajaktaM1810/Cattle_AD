package com.tarbar.kisan.Activities;

import static com.tarbar.kisan.Helper.constant.GET_STATES;
import static com.tarbar.kisan.Helper.constant.REGISTER;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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
import com.tarbar.kisan.Helper.ApiUtils;
import com.tarbar.kisan.Helper.CommonMethods;
import com.tarbar.kisan.Helper.KeyboardUtils;
import com.tarbar.kisan.Helper.SharedPreferenceManager;
import com.tarbar.kisan.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private SharedPreferenceManager sharedPrefMgr;
    private ProgressDialog dialog;
    private EditText name, MobileNumber, password, caste;
    private Spinner stateSpinner;
    private Button submit;
    private String stateId, mobile;
    TextView login;
    ImageView back;
    String ContactNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        View rootView = findViewById(android.R.id.content);
        rootView.setBackgroundColor(Color.WHITE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        sharedPrefMgr = new SharedPreferenceManager(this);
        name = findViewById(R.id.name);
        MobileNumber = findViewById(R.id.MobileNumber);
        password = findViewById(R.id.password);
        caste = findViewById(R.id.caste);
        stateSpinner = findViewById(R.id.state_spinner);
        submit = findViewById(R.id.submit);
        login = findViewById(R.id.login);
        back = findViewById(R.id.back);

        KeyboardUtils.setHindiKeyboard(name);
        KeyboardUtils.setHindiKeyboard(caste);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("RegisterActivity", "Back button clicked");
                name.setText("");
                MobileNumber.setText("");
                caste.setText("");
                stateSpinner.setSelection(0);
                password.setText("");
                Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        });

        login.setOnClickListener(v -> {
            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.click_animation);
            v.startAnimation(animation);
            name.setText("");
            MobileNumber.setText("");
            caste.setText("");
            stateSpinner.setSelection(0);
            password.setText("");
            Intent newIntent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(newIntent);
            finish();
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.click_animation);
                v.startAnimation(animation);

                String strname = name.getText().toString();
                String strcaste = caste.getText().toString();
                String selectedState = stateSpinner.getSelectedItem().toString();
                String strPassword = password.getText().toString();

                ContactNumber = MobileNumber.getText().toString();
                String regex = "^[6789]\\d{9}$";

                if (strname.isEmpty()) {
                    name.setError(getString(R.string.hint_enter_name));
                    name.setFocusable(true);
                } else if (ContactNumber.isEmpty() || !ContactNumber.matches(regex)) {
                    MobileNumber.setError(getString(R.string.enter_correct_mobile_number));
                    MobileNumber.setFocusable(true);
                } else if (strcaste.isEmpty()) {
                    caste.setError(getString(R.string.hint_caste));
                    caste.setFocusable(true);
                } else if (selectedState.equals("राज्य चुनें")) {
                    TextView errorText = (TextView) stateSpinner.getSelectedView();
                    errorText.setError(getString(R.string.hint_state));
                    errorText.setTextColor(Color.BLACK);
                    errorText.setText("");
                } else if (strPassword.isEmpty()) {
                    password.setError(getString(R.string.enter_password_number));
                } else if (strPassword.length() < 4) {
                    password.setError(getString(R.string.enter_correct_password));
                } else {
                    registerUser();
                }
            }
        });
        loadStates();
    }

    private void loadStates() {
        String url = GET_STATES;
        CommonMethods commonMethods = new CommonMethods();
        commonMethods.getState(this, url, new CommonMethods.OnStateResponseListener() {
            @Override
            public void onSuccess(ArrayList<HashMap<String, String>> stateList) {
                Log.d("API_STATE_RESPONSE", "Raw stateList: " + stateList.toString());

                ArrayList<String> stateNames = new ArrayList<>();
                HashSet<String> uniqueStates = new HashSet<>();

                stateNames.add(getString(R.string.hint_state));

                for (HashMap<String, String> state : stateList) {
                    Log.d("STATE_ITEM", "State Entry: " + state.toString());

                    String stateName = state.get("state");
                    if (!uniqueStates.contains(stateName)) {
                        uniqueStates.add(stateName);
                        stateNames.add(stateName);
                    }
                }

                Log.d("STATE_NAMES", "Final stateNames list: " + stateNames.toString());

                ArrayAdapter<String> adapter = new ArrayAdapter<>(RegisterActivity.this,
                        android.R.layout.simple_spinner_item, stateNames);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                stateSpinner.setAdapter(adapter);

                stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                        if (position > 0) {
                            stateId = stateList.get(position - 1).get("id");
                            Log.d("STATE_SELECTED", "Selected State ID: " + stateId);
                        } else {
                            stateId = null;
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parentView) {
                        stateId = null;
                    }
                });
            }

            @Override
            public void onError(String errorMessage) {
                Log.e("API_STATE_ERROR", "Error fetching states: " + errorMessage);
                Toast.makeText(RegisterActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void registerUser() {
        dialog = new ProgressDialog(RegisterActivity.this);
        dialog.setMessage(getString(R.string.Saving));
        dialog.setCancelable(false);
        dialog.show();
        String url = REGISTER;
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    dialog.dismiss();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");
                        if (status.equalsIgnoreCase("success")) {
                            name.setText("");
                            MobileNumber.setText("");
                            caste.setText("");
                            stateSpinner.setSelection(0);
                            password.setText("");
                            Intent intent = new Intent(RegisterActivity.this, OtpActivity.class);
                            intent.putExtra("mobile", ContactNumber);
                            startActivity(intent);
                            finish();
                        }
                        else if (status.equalsIgnoreCase("error")) {
                            Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    }
                    catch (JSONException e) {
                        dialog.dismiss();
                        if (e instanceof JSONException) {
                            Toast.makeText(RegisterActivity.this, R.string.json_parsing_error, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(RegisterActivity.this, R.string.generic_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                error -> {
                    dialog.dismiss();
                    if (error instanceof TimeoutError) {
                        Toast.makeText(RegisterActivity.this, R.string.timeout_error, Toast.LENGTH_SHORT).show();
                    } else if (error instanceof NoConnectionError) {
                        Toast.makeText(RegisterActivity.this, R.string.no_connection_error, Toast.LENGTH_SHORT).show();
                    } else if (error instanceof AuthFailureError) {
                        Toast.makeText(RegisterActivity.this, R.string.auth_failure_error, Toast.LENGTH_SHORT).show();
                    } else if (error instanceof ServerError) {
                        Toast.makeText(RegisterActivity.this, R.string.server_error, Toast.LENGTH_SHORT).show();
                    } else if (error instanceof NetworkError) {
                        Toast.makeText(RegisterActivity.this, R.string.network_error, Toast.LENGTH_SHORT).show();
                    } else if (error instanceof ParseError) {
                        Toast.makeText(RegisterActivity.this, R.string.parse_error, Toast.LENGTH_SHORT).show();
                    } else {
                        Log.d("RegistrationException", "" + error.toString());
                        Toast.makeText(RegisterActivity.this, R.string.unknown_error, Toast.LENGTH_SHORT).show();
                    }
                    Log.d("Check", "Error: " + error.getMessage());
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("name", name.getText().toString().trim());
                params.put("password", password.getText().toString().trim());
                params.put("caste", caste.getText().toString().trim());
                params.put("state", stateSpinner.getSelectedItem().toString().trim());
                params.put("mobile", MobileNumber.getText().toString().trim());
                Log.d("RegisterParams", "name: " + name.getText().toString().trim());
                Log.d("RegisterParams", "password: " + password.getText().toString().trim());
                Log.d("RegisterParams", "caste: " + caste.getText().toString().trim());
                Log.d("RegisterParams", "state: " + stateSpinner.getSelectedItem().toString().trim());
                Log.d("RegisterParams", "mobile: " + MobileNumber.getText().toString().trim());
                return params;
            }

        };
        postRequest.setRetryPolicy(ApiUtils.DEFAULT_RETRY_POLICY);
        requestQueue.add(postRequest);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        name.setText("");
        MobileNumber.setText("");
        caste.setText("");
        stateSpinner.setSelection(0);
        password.setText("");
        Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(i);
        finish();
    }

}
