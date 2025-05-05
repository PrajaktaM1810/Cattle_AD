package com.tarbar.kisan.Activities;

import static com.tarbar.kisan.Helper.constant.LOGIN;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.tarbar.kisan.Helper.ApiUtils;
import com.tarbar.kisan.Helper.Iconstant;
import com.tarbar.kisan.Helper.SharedPreferenceManager;
import com.tarbar.kisan.R;
import com.tarbar.kisan.Helper.constant;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {
    private SharedPreferenceManager sharedPrefMgr;
    TextView register, ForgetPassword, submit;
    EditText MobileNumber, Password;
    String hash = "";
    ProgressDialog dialog;
    String strMobile, strPassword;
    String whatsappNumber,enquiry;
    LinearLayout needHelp;
    private static final String ALLOWED_CHARACTERS = "0123456789";
    ImageView wpimg;
    TextView needHelpNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        View rootView = findViewById(android.R.id.content);
        rootView.setBackgroundColor(Color.WHITE);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        sharedPrefMgr = new SharedPreferenceManager(getApplicationContext());

        whatsappNumber = constant.tarbar_whatsapp_number;
        enquiry = constant.tarbar_enquiry_number;

        hash = getRandomString(30);
        initViews();
    }

    private static String getRandomString(final int sizeOfRandomString) {
        final Random random = new Random();
        final StringBuilder sb = new StringBuilder(sizeOfRandomString);
        for (int i = 0; i < sizeOfRandomString; ++i)
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        return sb.toString();
    }

    private void initViews() {
        register = findViewById(R.id.register);
        ForgetPassword = findViewById(R.id.ForgetPassword);
        submit = findViewById(R.id.submit);
        MobileNumber = findViewById(R.id.MobileNumber);
        Password = findViewById(R.id.Password);
        wpimg = findViewById(R.id.wpimg);
        needHelpNumber = findViewById(R.id.needHelpNumber);
        needHelp = findViewById(R.id.needHelp);

        register.setOnClickListener(v -> {
            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.click_animation);
            v.startAnimation(animation);
            MobileNumber.setText("");
            Password.setText("");
            Intent newIntent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(newIntent);
            finish();
        });

        ForgetPassword.setOnClickListener(v -> {
            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.click_animation);
            v.startAnimation(animation);
            MobileNumber.setText("");
            Password.setText("");
            showForgotPasswordDialog(true);
        });

        needHelp.setOnClickListener(v -> {
            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.click_animation);
            v.startAnimation(animation);
            MobileNumber.setText("");
            Password.setText("");
            showForgotPasswordDialog(false);
        });

        wpimg.setOnClickListener(v -> {
            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.click_animation);
            v.startAnimation(animation);
            MobileNumber.setText("");
            Password.setText("");
            showForgotPasswordDialog(false);
        });

        needHelpNumber.setOnClickListener(v -> {
            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.click_animation);
            v.startAnimation(animation);
            MobileNumber.setText("");
            Password.setText("");
            showForgotPasswordDialog(false);
        });

        submit.setOnClickListener(v -> {
            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.click_animation);
            v.startAnimation(animation);
            strMobile = MobileNumber.getText().toString();
            strPassword = Password.getText().toString();
            String regex = "^[6789]\\d{9}$";
            if (strMobile.isEmpty() || !strMobile.matches(regex)) {
                MobileNumber.setError(getString(R.string.enter_correct_mobile_number));
            } else if (strPassword.isEmpty()) {
                Password.setError(getString(R.string.enter_password_number));
            } else if (strPassword.length() < 4) {
                Password.setError(getString(R.string.enter_correct_password));
            } else {
                LoginApi();
            }
        });
    }

    private void showForgotPasswordDialog(boolean isForgetPassword) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.lyt_forget_password, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        // dialog.setCancelable(false);

        EditText input = dialogView.findViewById(R.id.forgetPasswordNumber);
        TextView textGetPassword = dialogView.findViewById(R.id.textGetPassword);
        LinearLayout password_layout = dialogView.findViewById(R.id.password_layout);
        password_layout.setVisibility(View.GONE);

        if (isForgetPassword) {
            textGetPassword.setText(R.string.get_password);
        } else {
            textGetPassword.setText(R.string.do_message);
        }

        dialogView.findViewById(R.id.textGetPassword).setOnClickListener(v -> {
            String mobile = input.getText().toString().trim();

            if (mobile.isEmpty()) {
                input.setError(getString(R.string.enter_mobile_number));
            } else if (!Pattern.matches("[6-9][0-9]{9}", mobile)) {
                input.setError(getString(R.string.enter_correct_mobile_number));
            } else {
                String messageToSend;
                if (isForgetPassword) {
                    messageToSend = getString(R.string.hello) + "\n\n" + getString(R.string.sendPassword) + "\nमोबाइल नंबर : " + mobile;
                    WhatsAppMessage(messageToSend,whatsappNumber);
                } else {
                    messageToSend = getString(R.string.hello) + "\nमोबाइल नंबर : " + mobile;
                    WhatsAppMessage(messageToSend,enquiry);
                }
                dialog.dismiss();
            }
        });

        dialogView.findViewById(R.id.textCancel).setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void LoginApi() {
        dialog = new ProgressDialog(LoginActivity.this);
        dialog.setMessage(getString(R.string.Verifying));
        dialog.setCancelable(false);
        dialog.show();
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        final StringRequest postRequest = new StringRequest(Request.Method.POST, LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("LoginResponse", response);
                        dialog.dismiss();
                        try {
                            JSONObject jsonObject1 = new JSONObject(response);
                            String status = jsonObject1.getString("status");
                            String message = jsonObject1.getString("message");
                            if (status.equalsIgnoreCase("success")) {

                                MobileNumber.setText("");
                                Password.setText("");

                                JSONObject user = jsonObject1.getJSONObject("user");
                                String id = user.getString("id");
                                String name = user.getString("name");
                                String mobile = user.getString("mobile");
                                String address = user.getString("address");
                                String state = user.getString("state");
                                String password = user.getString("password");
                                String kisan_number = user.getString("kisan_number");
//                                String isProfileFilled = user.getString("isProfileFilled");

                                sharedPrefMgr.connectDB();
                                sharedPrefMgr.setString(Iconstant.userid, id);
                                sharedPrefMgr.setString(Iconstant.name, name);
                                sharedPrefMgr.setString(Iconstant.mobile, mobile);
                                sharedPrefMgr.setString(Iconstant.address, address);
                                sharedPrefMgr.setString(Iconstant.state, state);
                                sharedPrefMgr.setString(Iconstant.password, password);
                                sharedPrefMgr.setString(Iconstant.kisan_number, kisan_number);
                                sharedPrefMgr.setBoolean("IsLogin", true);
                                sharedPrefMgr.closeDB();

                                Intent intent = new Intent(LoginActivity.this, Checking.class);
                                intent.putExtra("isVerified", true);
                                intent.putExtra("message", message);
                                intent.putExtra("nextActivity", MainActivity.class);
                                startActivity(intent);

//                                if (isProfileFilled.equals("1")) {
//                                    Intent intent = new Intent(LoginActivity.this, Checking.class);
//                                    intent.putExtra("isVerified", true);
//                                    intent.putExtra("message", message);
//                                    intent.putExtra("nextActivity", MainActivity.class);
//                                    startActivity(intent);
//                                } else if (isProfileFilled.equals("0")) {
//                                    Intent intent = new Intent(LoginActivity.this, LoadFragments.class);
//                                    intent.putExtra("fillProfileDetails", true);
//                                    startActivity(intent);
//                                }

                            } else if (status.equalsIgnoreCase("failure")) {
                                showDialog(message, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent i = new Intent(LoginActivity.this, OtpActivity.class);
                                        i.putExtra("mobile", strMobile);
                                        startActivity(i);
                                        finish();
                                    }
                                });
                            } else if (status.equalsIgnoreCase("notActive")) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                builder.setMessage(message);
                                builder.setPositiveButton(R.string.do_message, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        makeAccountActiveDialog(message);
                                    }
                                });
                                builder.setCancelable(false);
                                AlertDialog alert = builder.create();
                                alert.show();
                            } else {
                                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            dialog.dismiss();
                            if (e instanceof JSONException) {
                                Log.d("CheckException",""+e);
                                Toast.makeText(LoginActivity.this, R.string.json_parsing_error, Toast.LENGTH_SHORT).show();
                            } else {
                                Log.d("CheckException",""+e);
                                Toast.makeText(LoginActivity.this, R.string.generic_error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dialog.dismiss();
                        if (error instanceof TimeoutError) {
                            Toast.makeText(LoginActivity.this, R.string.timeout_error, Toast.LENGTH_SHORT).show();
                        } else if (error instanceof NoConnectionError) {
                            Toast.makeText(LoginActivity.this, R.string.no_connection_error, Toast.LENGTH_SHORT).show();
                        } else if (error instanceof AuthFailureError) {
                            Toast.makeText(LoginActivity.this, R.string.auth_failure_error, Toast.LENGTH_SHORT).show();
                        } else if (error instanceof ServerError) {
                            Log.d("ServerError",""+error);
                            Toast.makeText(LoginActivity.this, R.string.server_error, Toast.LENGTH_SHORT).show();
                        } else if (error instanceof NetworkError) {
                            Toast.makeText(LoginActivity.this, R.string.network_error, Toast.LENGTH_SHORT).show();
                        } else if (error instanceof ParseError) {
                            Toast.makeText(LoginActivity.this, R.string.parse_error, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(LoginActivity.this, R.string.unknown_error, Toast.LENGTH_SHORT).show();
                        }
                        Log.d("Check", "" + error.getMessage());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("mobile", strMobile);
                params.put("password", strPassword);
                Log.d("LoginHashmaps",""+strMobile);
                Log.d("LoginHashmaps",""+strPassword);
                return params;
            }
        };
        postRequest.setRetryPolicy(ApiUtils.DEFAULT_RETRY_POLICY);
        requestQueue.add(postRequest);
    }

    private void showDialog(String message, DialogInterface.OnClickListener positiveClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton(R.string.cancel, positiveClickListener)
                .setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void makeAccountActiveDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.lyt_forget_password, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
//        dialog.setCancelable(false);

        EditText forgetPasswordNumber = dialogView.findViewById(R.id.forgetPasswordNumber);
        EditText forgetPassword = dialogView.findViewById(R.id.Password);
        LinearLayout password_layout = dialogView.findViewById(R.id.password_layout);
        TextView textGetPassword = dialogView.findViewById(R.id.textGetPassword);
        textGetPassword.setText(R.string.ok);

        password_layout.setVisibility(View.VISIBLE);

        dialogView.findViewById(R.id.textGetPassword).setOnClickListener(v -> {
            String mobile = forgetPasswordNumber.getText().toString().trim();
            String password = forgetPassword.getText().toString().trim();
            if (password_layout.getVisibility() == View.VISIBLE) {
                if (mobile.isEmpty()) {
                    forgetPasswordNumber.setError(getString(R.string.enter_mobile_number));
                } else if (Pattern.matches("[0-7]{10}", strMobile)) {
                    forgetPasswordNumber.setError(getString(R.string.enter_correct_mobile_number));
                } else if (password.isEmpty()) {
                    forgetPassword.setError(getString(R.string.enter_password_number));
                } else if (password.length() < 4) {
                    forgetPassword.setError(getString(R.string.enter_correct_password));
                } else {
                    String whatsappMessage = getString(R.string.hello) + "\nमेरा अकाउंट ब्लॉक बता रहा है, कृपया उसे एक्टिव करें.." + "\n\nमोबाइल नंबर : " + mobile + "\nपासवर्ड : " + password;
                    sendWhatsAppMessage(whatsappMessage);
                    dialog.dismiss();
                }
            }
        });
        dialogView.findViewById(R.id.textCancel).setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void WhatsAppMessage(String message, String number) {
        PackageManager pm = getPackageManager();
        try {
            pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
            String url = "https://api.whatsapp.com/send?phone=" + number + "&text=" + Uri.encode(message);
            Intent sendIntent = new Intent(Intent.ACTION_VIEW);
            sendIntent.setData(Uri.parse(url));
            startActivity(sendIntent);
        } catch (PackageManager.NameNotFoundException e) {
            Toast.makeText(this, R.string.whatsapp_not_installed, Toast.LENGTH_SHORT).show();
        }
    }

    private void sendWhatsAppMessage(String message) {
        PackageManager pm = getPackageManager();
        try {
            pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
            String url = "https://api.whatsapp.com/send?phone=" + whatsappNumber + "&text=" + Uri.encode(message);
            Intent sendIntent = new Intent(Intent.ACTION_VIEW);
            sendIntent.setData(Uri.parse(url));
            startActivity(sendIntent);
        } catch (PackageManager.NameNotFoundException e) {
            Toast.makeText(this, R.string.whatsapp_not_installed, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MobileNumber.setText("");
        Password.setText("");
        Intent i = new Intent(LoginActivity.this, SplashActivity.class);
        startActivity(i);
        finish();
    }
}
