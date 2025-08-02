package com.tarbar.kisan.Fragments;

import static com.tarbar.kisan.Helper.constant.FETCH_PROFILE;
import android.app.ProgressDialog;
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
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
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
import com.tarbar.kisan.Activities.LoadFragments;
import com.tarbar.kisan.Activities.MainActivity;
import com.tarbar.kisan.Activities.SplashActivity;
import com.tarbar.kisan.Helper.ApiUtils;
import com.tarbar.kisan.Helper.Iconstant;
import com.tarbar.kisan.Helper.SharedPreferenceManager;
import com.tarbar.kisan.Helper.constant;
import com.tarbar.kisan.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Kisan_SevaAnurodh_fragment extends Fragment {

    private CheckBox checkbox1, checkbox2, checkbox3, checkbox4;
    private Button submitButton;
    ProgressDialog dialog;
    private SharedPreferenceManager sharedPrefMgr;
    String userid;
    TextView toolbarTitle;
    ImageView back;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_kisan_seva_anurodh, container, false);
        view.setBackgroundColor(Color.WHITE);

        sharedPrefMgr = new SharedPreferenceManager(getContext());
        sharedPrefMgr.connectDB();
        userid = sharedPrefMgr.getString(Iconstant.userid);
        sharedPrefMgr.closeDB();

        checkbox1 = view.findViewById(R.id.checkbox1);
        checkbox2 = view.findViewById(R.id.checkbox2);
        checkbox3 = view.findViewById(R.id.checkbox3);
        checkbox4 = view.findViewById(R.id.checkbox4);
        submitButton = view.findViewById(R.id.submit);
        back = view.findViewById(R.id.back);

        toolbarTitle = view.findViewById(R.id.title);
        toolbarTitle.setText(R.string.title_kisan_seva_anurodh);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.click_animation);
                v.startAnimation(animation);
                Intent i = new Intent(requireContext(), MainActivity.class);
                i.putExtra("SELECT_PROFILE_FRAGMENT", true);
                startActivity(i);
                requireActivity().finish();
            }
        });

        checkbox1.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                checkbox2.setChecked(false);
                checkbox3.setChecked(false);
                checkbox4.setChecked(false);
            }
        });

        checkbox2.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                checkbox1.setChecked(false);
                checkbox3.setChecked(false);
                checkbox4.setChecked(false);
            }
        });

        checkbox3.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                checkbox1.setChecked(false);
                checkbox2.setChecked(false);
                checkbox4.setChecked(false);
            }
        });

        checkbox4.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                checkbox1.setChecked(false);
                checkbox2.setChecked(false);
                checkbox3.setChecked(false);
            }
        });

        getKisanProfile();

        submitButton.setOnClickListener(v -> {
            Animation animation = AnimationUtils.loadAnimation(requireContext(), R.anim.click_animation);
            v.startAnimation(animation);
            if (checkbox1.isChecked() || checkbox2.isChecked() || checkbox3.isChecked() || checkbox4.isChecked()) {
                StringBuilder selectedOptions = new StringBuilder();

                if (checkbox1.isChecked())
                    selectedOptions.append(getString(R.string.txt_kisan)).append(", ");
                if (checkbox2.isChecked())
                    selectedOptions.append(getString(R.string.txt_business)).append(", ");
                if (checkbox3.isChecked())
                    selectedOptions.append(getString(R.string.txt_doctor)).append(", ");
                if (checkbox4.isChecked())
                    selectedOptions.append(getString(R.string.txt_helper)).append(", ");

                if (selectedOptions.length() > 2) {
                    selectedOptions.setLength(selectedOptions.length() - 2);
                }

//                Toast.makeText(getContext(), selectedOptions.toString(), Toast.LENGTH_SHORT).show();
                UpdateAnimalAPI();
            } else {
                Toast.makeText(getContext(), "कृपया भूमिका चुनें !", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void getKisanProfile() {
        dialog = new ProgressDialog(getActivity());
        dialog.setMessage(getString(R.string.getting_data));
        dialog.setCancelable(false);
        dialog.show();
        String url = FETCH_PROFILE;
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("getKisanProfile_response", "" + response);
                        dialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("status");
                            String message = jsonObject.getString("message");
                            if (status.equalsIgnoreCase("success")) {
                                JSONArray dataArray = jsonObject.getJSONArray("data");
                                if (dataArray.length() > 0) {
                                    JSONObject userData = dataArray.getJSONObject(0);
                                    String UserRole = userData.getString("user_role");

                                    if (UserRole.equals("1")) checkbox1.setChecked(true);
                                    else if (UserRole.equals("2")) checkbox2.setChecked(true);
                                    else if (UserRole.equals("3")) checkbox3.setChecked(true);
                                    else if (UserRole.equals("4")) checkbox4.setChecked(true);

                                } else {
                                    dialog.dismiss();
                                    Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                dialog.dismiss();
                                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            dialog.dismiss();
                            Toast.makeText(getActivity(), R.string.json_parsing_error, Toast.LENGTH_SHORT).show();
                            Log.d("CheckException", "" + e);
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
                params.put("userid", userid);
                return params;
            }
        };
        stringRequest.setRetryPolicy(ApiUtils.DEFAULT_RETRY_POLICY);
        requestQueue.add(stringRequest);
    }

    private void UpdateAnimalAPI() {
        dialog = new ProgressDialog(requireContext());
        dialog.setMessage(getString(R.string.saving_data));
        dialog.setCancelable(false);
        dialog.show();

        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());

        final String userRole;
        if (checkbox1.isChecked()) {
            userRole = "1";
        } else if (checkbox2.isChecked()) {
            userRole = "2";
        } else if (checkbox3.isChecked()) {
            userRole = "3";
        } else if (checkbox4.isChecked()) {
            userRole = "4";
        } else {
            dialog.dismiss();
            Toast.makeText(requireContext(), "कृपया कोई भूमिका चुनें!", Toast.LENGTH_SHORT).show();
            return;
        }

        StringRequest postRequest = new StringRequest(Request.Method.POST, constant.UPDATE_USER_ROLE,
                response -> {
                    dialog.dismiss();
                    Log.d("UpdateAnimalAPI", response);
                    try {
                        JSONObject jsonObject1 = new JSONObject(response);
                        Intent intent = new Intent(requireContext(), Checking.class);
                        intent.putExtra("isVerified", jsonObject1.getString("status").equalsIgnoreCase("success"));
                        intent.putExtra("message", jsonObject1.getString("message"));
                        intent.putExtra("nextActivity", LoadFragments.class);
                        intent.putExtra("Fragment_KisanSevaAnurodh", true);
                        startActivity(intent);
                    } catch (JSONException e) {
                        Log.d("CheckException", e.toString());
                        Toast.makeText(requireContext(), R.string.json_parsing_error, Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    dialog.dismiss();
                    Toast.makeText(requireContext(), R.string.generic_error, Toast.LENGTH_SHORT).show();
                    Log.d("Check", error.toString());
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", userid);
                params.put("is_businessman", userRole);
                return params;
            }
        };
        postRequest.setRetryPolicy(ApiUtils.DEFAULT_RETRY_POLICY);
        requestQueue.add(postRequest);
    }
}