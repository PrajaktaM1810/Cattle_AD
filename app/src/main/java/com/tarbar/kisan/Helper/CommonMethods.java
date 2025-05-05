package com.tarbar.kisan.Helper;


import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.NetworkError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.tarbar.kisan.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CommonMethods {
    public interface OnStateResponseListener {
        void onSuccess(ArrayList<HashMap<String, String>> stateList);
        void onError(String errorMessage);
    }

    public void getState(Context context, String url, OnStateResponseListener listener) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        String status = jsonResponse.optString("status");
                        if ("success".equalsIgnoreCase(status)) {
                            JSONArray dataArray = jsonResponse.optJSONArray("data");
                            ArrayList<HashMap<String, String>> stateList = new ArrayList<>();
                            if (dataArray != null) {
                                for (int i = 0; i < dataArray.length(); i++) {
                                    JSONObject stateObject = dataArray.getJSONObject(i);
                                    HashMap<String, String> stateData = new HashMap<>();
                                    stateData.put("id", stateObject.optString("id"));
                                    stateData.put("state", stateObject.optString("state"));
                                    stateData.put("district", stateObject.optString("district"));
                                    stateData.put("tehshil", stateObject.optString("tehshil"));
                                    stateData.put("village", stateObject.optString("village"));
                                    stateList.add(stateData);
                                }
                            }
                            listener.onSuccess(stateList);
                        } else {
                            listener.onError(jsonResponse.optString("message", "Unknown error occurred"));
                        }
                    } catch (JSONException e) {
                        if (e instanceof JSONException) {
                            Log.d("CheckError",""+e);
                            Toast.makeText(context, R.string.json_parsing_error, Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d("CheckException",""+e);
                            Toast.makeText(context, R.string.generic_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                error -> {
                    String errorMessage;
                    if (error instanceof TimeoutError) {
                        errorMessage = context.getString(R.string.timeout_error);
                    } else if (error instanceof NoConnectionError) {
                        errorMessage = context.getString(R.string.no_connection_error);
                    } else if (error instanceof AuthFailureError) {
                        errorMessage = context.getString(R.string.auth_failure_error);
                    } else if (error instanceof ServerError) {
                        errorMessage = context.getString(R.string.server_error);
                    } else if (error instanceof NetworkError) {
                        errorMessage = context.getString(R.string.network_error);
                    } else if (error instanceof ParseError) {
                        errorMessage = context.getString(R.string.parse_error);
                    } else {
                        errorMessage = context.getString(R.string.unknown_error);
                    }
                    listener.onError(errorMessage);
                    Log.e("VolleyError", error.toString());
                });
        requestQueue.add(stringRequest);
    }

}
