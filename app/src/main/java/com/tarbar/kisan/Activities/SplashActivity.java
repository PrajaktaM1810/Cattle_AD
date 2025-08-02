package com.tarbar.kisan.Activities;

import static com.tarbar.kisan.Helper.constant.SETTINGS;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.NoConnectionError;
import com.tarbar.kisan.Helper.ApiUtils;
import com.tarbar.kisan.Helper.Iconstant;
import com.tarbar.kisan.Helper.SharedPreferenceManager;
import com.tarbar.kisan.R;

import android.app.ProgressDialog;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SplashActivity extends AppCompatActivity {

    private SharedPreferenceManager sharedPrefMgr;
    private static final int SPLASH_DELAY = 1500;
    ProgressDialog dialog;
    String appVersion, maintenanceStatus, maintenanceMsg, apkFiles, mobileNumber,enqiry_wp_number;
    String Version = "1.0.0";
    String userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        View rootView = findViewById(android.R.id.content);
        rootView.setBackgroundColor(Color.WHITE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        sharedPrefMgr = new SharedPreferenceManager(SplashActivity.this);
        sharedPrefMgr.connectDB();
        userid = sharedPrefMgr.getString(Iconstant.userid);
        sharedPrefMgr.closeDB();

        settings();
    }

    private void navigateBasedOnRegistration() {
        boolean isLoggedIn = sharedPrefMgr.getBoolean("IsLogin");
        if (isLoggedIn) {
//            getProfileStatus();
            Intent i = new Intent(SplashActivity.this, MainActivity.class);
            i.putExtra("SELECT_HOME_FRAGMENT", true);
            startActivity(i);
            finish();
        } else {
            Intent i = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(i);
            finish();
        }
    }

    private void settings() {
        String url = SETTINGS;
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                response -> {
            Log.d("settings_response","" +response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");

                        appVersion = jsonObject.getJSONObject("data").getString("app_version");
                        maintenanceStatus = jsonObject.getJSONObject("data").getString("maintance_status");
                        maintenanceMsg = jsonObject.getJSONObject("data").getString("maintance_msg");
                        apkFiles = jsonObject.getJSONObject("data").getString("apk_files");
//                        mobileNumber = jsonObject.getJSONObject("data").getString("mob_number");
//                        enqiry_wp_number = jsonObject.getJSONObject("data").getString("enquiry_wp_number");

//                        appVersion = "1.0.0";
//                        maintenanceStatus = "0";
//
//                        sharedPrefMgr.connectDB();
//                        sharedPrefMgr.setString(Iconstant.tarbar_whatsapp_number, "919405075577");
//                        sharedPrefMgr.setString(Iconstant.enquiry_whatsapp_number, "917020921516");
//                        Log.d("whatsappNumber", "" + mobileNumber);
//                        sharedPrefMgr.closeDB();

                        if (status.equalsIgnoreCase("success")) {
                            if (Version.equals(appVersion)) {
                                if (maintenanceStatus.equals("1")) {
                                    setContentView(R.layout.layout_maintainance);
                                    TextView messageText = findViewById(R.id.message_text);
                                    messageText.setText(maintenanceMsg);
                                } else {
                                    new Handler().postDelayed(this::navigateBasedOnRegistration, SPLASH_DELAY);
                                }
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this);
                                builder.setMessage(R.string.download_version);
                                builder.setCancelable(false);
                                builder.setPositiveButton(R.string.download_new_version, (dialog, which) -> {
                                    String marketLink = apkFiles;
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(marketLink));
                                    try {
                                        if (getPackageManager().resolveActivity(intent, 0) != null) {
                                            startActivity(intent);
                                        } else {
                                            Log.e("check", "No activity found to handle the intent");
                                        }
                                    } catch (ActivityNotFoundException e) {
                                        Log.e("check", "ActivityNotFoundException: " + e.getMessage());
                                    }
                                });
                                AlertDialog alertDialog = builder.create();
                                alertDialog.show();
                            }
                        } else if (status.equalsIgnoreCase("error")) {
                            Toast.makeText(SplashActivity.this, maintenanceMsg, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(SplashActivity.this, R.string.json_parsing_error, Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.d("CheckERROR",""+error);
                    if (error instanceof TimeoutError) {
                        Toast.makeText(SplashActivity.this, R.string.timeout_error, Toast.LENGTH_SHORT).show();
                    } else if (error instanceof NoConnectionError) {
                        Toast.makeText(SplashActivity.this, R.string.no_connection_error, Toast.LENGTH_SHORT).show();
                        Log.d("CheckERROR",""+error);
                    } else if (error instanceof AuthFailureError) {
                        Toast.makeText(SplashActivity.this, R.string.auth_failure_error, Toast.LENGTH_SHORT).show();
                    } else if (error instanceof ServerError) {
                        Toast.makeText(SplashActivity.this, R.string.server_error, Toast.LENGTH_SHORT).show();
                    } else if (error instanceof NetworkError) {
                        Toast.makeText(SplashActivity.this, R.string.network_error, Toast.LENGTH_SHORT).show();
                    } else if (error instanceof ParseError) {
                        Toast.makeText(SplashActivity.this, R.string.parse_error, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(SplashActivity.this, R.string.unknown_error, Toast.LENGTH_SHORT).show();
                    }
                    Log.d("Check", "" + error.getMessage());
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return null;
            }
        };
        postRequest.setRetryPolicy(ApiUtils.DEFAULT_RETRY_POLICY);
        requestQueue.add(postRequest);
    }

//    private void getProfileStatus() {
//        dialog = new ProgressDialog(SplashActivity.this);
//        dialog.setMessage(getString(R.string.getting_data));
//        dialog.setCancelable(false);
//        dialog.show();
//        String url = GET_PROFILE_STATUS;
//        RequestQueue requestQueue = Volley.newRequestQueue(SplashActivity.this);
//        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        dialog.dismiss();
//                        try {
//                            JSONObject jsonObject = new JSONObject(response);
//                            String status = jsonObject.getString("status");
//                            if (status.equals("success")) {
//                                String isProfileFilled = jsonObject.getString("isProfileFilled");
//                                if (isProfileFilled.equals("1")) {
//                                    Intent i = new Intent(SplashActivity.this, MainActivity.class);
//                                    i.putExtra("SELECT_HOME_FRAGMENT", true);
//                                    startActivity(i);
//                                    finish();
//                                } else if (isProfileFilled.equals("0")) {
//                                    Intent intent = new Intent(SplashActivity.this, LoadFragments.class);
//                                    intent.putExtra("fillProfileDetails", true);
//                                    startActivity(intent);
//                                    finish();
//                                }
//                            } else {
//                                String message = jsonObject.getString("message");
//                                Toast.makeText(SplashActivity.this, message, Toast.LENGTH_SHORT).show();
//                            }
//                        } catch (JSONException e) {
//                            Toast.makeText(SplashActivity.this, R.string.json_parsing_error, Toast.LENGTH_SHORT).show();
//                            Log.d("CheckException", "" + e);
//                        }
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        dialog.dismiss();
//                        Toast.makeText(SplashActivity.this, R.string.generic_error, Toast.LENGTH_SHORT).show();
//                        Log.d("Check", "" + error.getMessage());
//                    }
//                }) {
//            @Override
//            protected Map<String, String> getParams() {
//                Map<String, String> params = new HashMap<>();
//                params.put("user_id", userid);
//                return params;
//            }
//        };
//        stringRequest.setRetryPolicy(ApiUtils.DEFAULT_RETRY_POLICY);
//        requestQueue.add(stringRequest);
//    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
        System.exit(0);
    }
}
