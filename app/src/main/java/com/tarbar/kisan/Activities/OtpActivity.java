package com.tarbar.kisan.Activities;

import static com.tarbar.kisan.Helper.constant.RESEND_OTP;
import static com.tarbar.kisan.Helper.constant.VERIFY_OTP;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.ParseError;
import com.tarbar.kisan.Helper.ApiUtils;
import com.tarbar.kisan.Helper.Iconstant;
import com.tarbar.kisan.Helper.SharedPreferenceManager;
import com.tarbar.kisan.R;

import java.util.Random;


import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class OtpActivity extends AppCompatActivity {
    private EditText otp1;
    private EditText otp2;
    private EditText otp3;
    private EditText otp4;
    ImageView back;
    String otp = "1234";

    TextView resendOtp,mobileNumber;
    Button verify;
    ProgressDialog dialog;
    LinearLayout llVerification;
    private SharedPreferenceManager sharedPrefMgr;
    Context context;
    LinearLayout textlayout;
    String mobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        View rootView = findViewById(android.R.id.content);
        rootView.setBackgroundColor(Color.WHITE);
        context = this;

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        sharedPrefMgr = new SharedPreferenceManager(this);

//        mobile = getIntent().getStringExtra("mobile");
//        Log.d("CheckMobile",""+mobile);

        initViews();
        Random random = new Random();
        otp = String.format("%06d", random.nextInt(1000000));
        otp1.addTextChangedListener(new GenericTextWatcher(otp1));
        otp2.addTextChangedListener(new GenericTextWatcher(otp2));
        otp3.addTextChangedListener(new GenericTextWatcher(otp3));
        otp4.addTextChangedListener(new GenericTextWatcher(otp4));

        otp1.setShowSoftInputOnFocus(false);
        otp2.setShowSoftInputOnFocus(false);
        otp3.setShowSoftInputOnFocus(false);
        otp4.setShowSoftInputOnFocus(false);

        enterStaticOtpWithAnimation();

        verify.setOnClickListener(view -> {
            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.click_animation);
            view.startAnimation(animation);
            if (otp == null) return;
            if (getOtp().isEmpty() || getOtp().length() != 4) {
                Toast.makeText(this, R.string.enter_otp, Toast.LENGTH_SHORT).show();
            } else {
                verifyOTP();
            }
        });

        resendOtp.setOnClickListener(view -> {
            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.click_animation);
            view.startAnimation(animation);
            resendOTP();
        });

        back.setOnClickListener(view -> {
            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.click_animation);
            view.startAnimation(animation);
            otp1.setText("");
            otp2.setText("");
            otp3.setText("");
            otp4.setText("");
            Intent back = new Intent(OtpActivity.this, RegisterActivity.class);
            startActivity(back);
        });
    }

    private void enterStaticOtpWithAnimation() {
        otp1.postDelayed(() -> otp1.setText("1"), 1000);
        otp2.postDelayed(() -> otp2.setText("2"), 1500);
        otp3.postDelayed(() -> otp3.setText("3"), 2000);
        otp4.postDelayed(() -> {
            otp4.setText("4");
            navigateToNextScreen();
        }, 2500);
    }

    private void navigateToNextScreen() {
        Intent intent = new Intent(OtpActivity.this, Checking.class);
        intent.putExtra("isVerified", true);
        intent.putExtra("message", getString(R.string.successfully_registered));
        intent.putExtra("nextActivity", LoadFragments.class);
        intent.putExtra("fillProfileDetails", true);
        startActivity(intent);
    }

    private void initViews() {
        otp1 = findViewById(R.id.otp1);
        otp2 = findViewById(R.id.otp2);
        otp3 = findViewById(R.id.otp3);
        otp4 = findViewById(R.id.otp4);

        resendOtp = findViewById(R.id.resendOtp);
        textlayout = findViewById(R.id.textlayout);
        llVerification = findViewById(R.id.llVerification);
        verify = findViewById(R.id.verify);
        back = findViewById(R.id.back);
        mobileNumber = findViewById(R.id.mobileNumber);
        mobileNumber.setText(mobile);
    }

    public String getOtp() {
        return otp1.getText().toString() + otp2.getText().toString() + otp3.getText().toString() + otp4.getText().toString();
    }

    public class GenericTextWatcher implements TextWatcher {
        private View view;

        private GenericTextWatcher(View view) {
            this.view = view;
        }

        @Override
        public void afterTextChanged(Editable editable) {
            String text = editable.toString();
            EditText editText = (EditText) view;
            editText.setSelection(editText.getText().length());

            int id = view.getId();

            if (id == R.id.otp1) {
                if (text.length() == 1) {
                    otp1.setBackground(getResources().getDrawable(R.drawable.otp_white_circle));
                    otp2.requestFocus();
                } else if (text.length() == 0) {
                    otp1.setBackground(getResources().getDrawable(R.drawable.otp_slight_black_circle));
                }
            } else if (id == R.id.otp2) {
                if (text.length() == 1) {
                    otp2.setBackground(getResources().getDrawable(R.drawable.otp_white_circle));
                    otp3.requestFocus();
                } else if (text.length() == 0) {
                    otp2.setBackground(getResources().getDrawable(R.drawable.otp_slight_black_circle));
                    otp1.requestFocus();
                }
            } else if (id == R.id.otp3) {
                if (text.length() == 1) {
                    otp3.setBackground(getResources().getDrawable(R.drawable.otp_white_circle));
                    otp4.requestFocus();
                } else if (text.length() == 0) {
                    otp3.setBackground(getResources().getDrawable(R.drawable.otp_slight_black_circle));
                    otp2.requestFocus();
                }
            } else if (id == R.id.otp4) {
                if (text.length() == 1) {
                    otp4.setBackground(getResources().getDrawable(R.drawable.otp_white_circle));
                } else if (text.length() == 0) {
                    otp4.setBackground(getResources().getDrawable(R.drawable.otp_slight_black_circle));
                    otp3.requestFocus();
                }
            }
        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
        }

        @Override
        public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            String text = arg0.toString();
            int viewId = view.getId();

            if (viewId == R.id.otp1) {
                if (text.length() == 1) {
                    otp1.setBackground(getResources().getDrawable(R.drawable.otp_white_circle));
                    otp2.requestFocus();
                } else if (text.length() == 0) {
                    otp1.setBackground(getResources().getDrawable(R.drawable.otp_slight_black_circle));
                }
            } else if (viewId == R.id.otp2) {
                if (text.length() == 1) {
                    otp2.setBackground(getResources().getDrawable(R.drawable.otp_white_circle));
                    otp3.requestFocus();
                } else if (text.length() == 0) {
                    otp2.setBackground(getResources().getDrawable(R.drawable.otp_slight_black_circle));
                    otp1.requestFocus();
                }
            } else if (viewId == R.id.otp3) {
                if (text.length() == 1) {
                    otp3.setBackground(getResources().getDrawable(R.drawable.otp_white_circle));
                    otp4.requestFocus();
                } else if (text.length() == 0) {
                    otp3.setBackground(getResources().getDrawable(R.drawable.otp_slight_black_circle));
                    otp2.requestFocus();
                }
            } else if (viewId == R.id.otp4) {
                if (text.length() == 1) {
                    otp4.setBackground(getResources().getDrawable(R.drawable.otp_white_circle));
                } else if (text.length() == 0) {
                    otp4.setBackground(getResources().getDrawable(R.drawable.otp_slight_black_circle));
                    otp3.requestFocus();
                }
            }
        }
    }

    private void verifyOTP() {
        dialog = new ProgressDialog(OtpActivity.this);
        dialog.setMessage(getString(R.string.Verifying));
        dialog.setCancelable(false);
        dialog.show();
        String url = VERIFY_OTP;
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    dialog.dismiss();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");
                        if (status.equalsIgnoreCase("success")) {
                            JSONObject userData = jsonObject.getJSONObject("user_data");

                            String id = userData.getString("id");
                            String name = userData.getString("name");
                            String Mobile = userData.getString("mobile");
                            String password = userData.getString("password");
                            String address = userData.getString("address");
                            String state = userData.getString("state");
                            String kisanNumber = userData.getString("kisan_number");

                            sharedPrefMgr.connectDB();
                            sharedPrefMgr.setString(Iconstant.userid, id);
                            sharedPrefMgr.setString(Iconstant.name, name);
                            sharedPrefMgr.setString(Iconstant.mobile, Mobile);
                            sharedPrefMgr.setString(Iconstant.address, address);
                            sharedPrefMgr.setString(Iconstant.state, state);
                            sharedPrefMgr.setString(Iconstant.password, password);
                            sharedPrefMgr.setString(Iconstant.kisan_number, kisanNumber);
                            sharedPrefMgr.closeDB();

                            otp1.setText("");
                            otp2.setText("");
                            otp3.setText("");
                            otp4.setText("");
                            Intent intent = new Intent(OtpActivity.this, Checking.class);
                            intent.putExtra("isVerified", true);
                            intent.putExtra("message", getString(R.string.successfully_registered));
                            intent.putExtra("nextActivity", MainActivity.class);
                            startActivity(intent);
                        } else if (status.equalsIgnoreCase("error")) {
                            Toast.makeText(OtpActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        dialog.dismiss();
                        if (e instanceof JSONException) {
                            Toast.makeText(OtpActivity.this, R.string.json_parsing_error, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(OtpActivity.this, R.string.generic_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                error -> {
                    dialog.dismiss();
                    if (error instanceof TimeoutError) {
                        Toast.makeText(OtpActivity.this, R.string.timeout_error, Toast.LENGTH_SHORT).show();
                    } else if (error instanceof NoConnectionError) {
                        Toast.makeText(OtpActivity.this, R.string.no_connection_error, Toast.LENGTH_SHORT).show();
                    } else if (error instanceof AuthFailureError) {
                        Toast.makeText(OtpActivity.this, R.string.auth_failure_error, Toast.LENGTH_SHORT).show();
                    } else if (error instanceof ServerError) {
                        Toast.makeText(OtpActivity.this, R.string.server_error, Toast.LENGTH_SHORT).show();
                    } else if (error instanceof NetworkError) {
                        Toast.makeText(OtpActivity.this, R.string.network_error, Toast.LENGTH_SHORT).show();
                    } else if (error instanceof ParseError) {
                        Toast.makeText(OtpActivity.this, R.string.parse_error, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(OtpActivity.this, R.string.unknown_error, Toast.LENGTH_SHORT).show();
                    }
                    Log.d("Check", "Error: " + error.getMessage());
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("mobile", mobile);
                params.put("otp", getOtp());
                return params;
            }
        };
        requestQueue.add(postRequest);
    }

    private void resendOTP() {
        dialog = new ProgressDialog(OtpActivity.this);
        dialog.setMessage(getString(R.string.sending_otp));
        dialog.setCancelable(false);
        dialog.show();
        String url = RESEND_OTP;
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    dialog.dismiss();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        Log.d("ResendOtpResponse",""+response);
                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");
                        if (status.equalsIgnoreCase("success")) {
                            Toast.makeText(OtpActivity.this, message, Toast.LENGTH_SHORT).show();
                        } else if (status.equalsIgnoreCase("error")) {
                            Toast.makeText(OtpActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        dialog.dismiss();
                        Toast.makeText(OtpActivity.this, R.string.json_parsing_error, Toast.LENGTH_SHORT).show();
                        Log.e("resendOTP", "JSON Parsing Error: " + e.getMessage());
                    }
                },
                error -> {
                    dialog.dismiss();
                    if (error instanceof TimeoutError) {
                        Toast.makeText(OtpActivity.this, R.string.timeout_error, Toast.LENGTH_SHORT).show();
                    } else if (error instanceof NoConnectionError) {
                        Toast.makeText(OtpActivity.this, R.string.no_connection_error, Toast.LENGTH_SHORT).show();
                    } else if (error instanceof AuthFailureError) {
                        Toast.makeText(OtpActivity.this, R.string.auth_failure_error, Toast.LENGTH_SHORT).show();
                    } else if (error instanceof ServerError) {
                        Toast.makeText(OtpActivity.this, R.string.server_error, Toast.LENGTH_SHORT).show();
                    } else if (error instanceof NetworkError) {
                        Toast.makeText(OtpActivity.this, R.string.network_error, Toast.LENGTH_SHORT).show();
                    } else if (error instanceof ParseError) {
                        Toast.makeText(OtpActivity.this, R.string.parse_error, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(OtpActivity.this, R.string.unknown_error, Toast.LENGTH_SHORT).show();
                    }
                    Log.e("resendOTP", "Error: " + error.getMessage());
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("mobile", mobile);
                return params;
            }
        };
        postRequest.setRetryPolicy(ApiUtils.DEFAULT_RETRY_POLICY);
        requestQueue.add(postRequest);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        otp1.setText("");
        otp2.setText("");
        otp3.setText("");
        otp4.setText("");
        Intent back = new Intent(OtpActivity.this, RegisterActivity.class);
        startActivity(back);
    }

}