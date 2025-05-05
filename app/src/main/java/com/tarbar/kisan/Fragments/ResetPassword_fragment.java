package com.tarbar.kisan.Fragments;

import static com.tarbar.kisan.Helper.constant.RESET_PASSWORD;

import android.app.ProgressDialog;
import android.content.Context;
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
import com.tarbar.kisan.Activities.MainActivity;
import com.tarbar.kisan.Helper.ApiUtils;
import com.tarbar.kisan.Helper.Iconstant;
import com.tarbar.kisan.Helper.SharedPreferenceManager;
import com.tarbar.kisan.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ResetPassword_fragment extends Fragment {
    ImageView back;
    EditText oldPassword, newPassword, confirmPasword;
    ProgressDialog dialog;
    private SharedPreferenceManager sharedPrefMgr;
    String mobile;
    Button submit;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reset_password, container, false);
        view.setBackgroundColor(Color.WHITE);
        sharedPrefMgr = new SharedPreferenceManager(requireContext());
        sharedPrefMgr.connectDB();
        mobile = sharedPrefMgr.getString(Iconstant.mobile);
        Log.d("mobile", "" + mobile);
        sharedPrefMgr.closeDB();

        submit = view.findViewById(R.id.submit);
        back = view.findViewById(R.id.back);
        oldPassword = view.findViewById(R.id.oldPassword);
        newPassword = view.findViewById(R.id.newPassword);
        confirmPasword = view.findViewById(R.id.confirmPasword);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation animation = AnimationUtils.loadAnimation(requireContext(), R.anim.click_animation);
                v.startAnimation(animation);
                Intent i = new Intent(requireContext(), MainActivity.class);
                i.putExtra("SELECT_PROFILE_FRAGMENT", true);
                startActivity(i);
                requireActivity().finish();
            }
        });

        submit.setOnClickListener(v -> {
            Animation animation = AnimationUtils.loadAnimation(requireContext(), R.anim.click_animation);
            v.startAnimation(animation);
            String strOldPassword = oldPassword.getText().toString();
            String strNewPassword = newPassword.getText().toString();
            String strConfirmPassword = confirmPasword.getText().toString();

            if (strOldPassword.isEmpty()) {
                oldPassword.setError(getString(R.string.enter_password_number));
            } else if (strOldPassword.length() < 4) {
                oldPassword.setError(getString(R.string.enter_correct_password));
            } else if (strNewPassword.isEmpty()) {
                newPassword.setError(getString(R.string.enter_password_number));
            } else if (strNewPassword.length() < 4) {
                newPassword.setError(getString(R.string.enter_correct_password));
            } else if (strConfirmPassword.isEmpty()) {
                confirmPasword.setError(getString(R.string.enter_password_number));
            } else if (strConfirmPassword.length() < 4) {
                confirmPasword.setError(getString(R.string.enter_correct_password));
            } else if (!strNewPassword.equals(strConfirmPassword)) {
                confirmPasword.setError(getString(R.string.confirm_new_password));
            } else {
                ResetPasswordApi();
            }
        });

        return view;
    }

    private void ResetPasswordApi() {
        dialog = new ProgressDialog(requireContext());
        dialog.setMessage(getString(R.string.reseting_password));
        dialog.setCancelable(false);
        dialog.show();
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        final StringRequest postRequest = new StringRequest(Request.Method.POST, RESET_PASSWORD,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("ResetPasswordResponse", response);
                        dialog.dismiss();
                        try {
                            JSONObject jsonObject1 = new JSONObject(response);
                            String status = jsonObject1.getString("status");
                            String message = jsonObject1.getString("message");
                            if (status.equalsIgnoreCase("success")) {
                                Intent i = new Intent(requireContext(), MainActivity.class);
                                i.putExtra("SELECT_PROFILE_FRAGMENT", true);
                                startActivity(i);
                                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            dialog.dismiss();
                            if (e instanceof JSONException) {
                                Toast.makeText(requireContext(), R.string.json_parsing_error, Toast.LENGTH_SHORT).show();
                            } else {
                                Log.d("CheckException", "" + e);
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
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("mobile", mobile);
                params.put("old_password", oldPassword.getText().toString());
                params.put("new_password", newPassword.getText().toString());
                Log.d("ResetPasswordParams", params.toString());
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
                Intent i = new Intent(requireContext(), MainActivity.class);
                i.putExtra("SELECT_PROFILE_FRAGMENT", true);
                startActivity(i);
                requireActivity().finish();
            }
        });
    }

}
