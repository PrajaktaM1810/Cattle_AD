package com.tarbar.kisan.Fragments;

import static android.view.View.GONE;
import static com.tarbar.kisan.Helper.constant.GET_SELLING_LIST;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.tarbar.kisan.Activities.LoadFilterFragments;
import com.tarbar.kisan.Activities.LoadFormFragments;
import com.tarbar.kisan.Activities.MainActivity;
import com.tarbar.kisan.Adapter.Selling_List_Adapter;
import com.tarbar.kisan.Helper.ApiUtils;
import com.tarbar.kisan.Helper.Iconstant;
import com.tarbar.kisan.Helper.SharedPreferenceManager;
import com.tarbar.kisan.R;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.util.Log;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PashuUpdateOption_fragment extends Fragment {
    private SharedPreferenceManager sharedPrefMgr;
    ProgressDialog dialog;
    LinearLayout allLayout, cowLayout, buffaloLayout;
    TextView allLTxt, cowTxt, buffaloTxt;
    TextView id_data_not_found;
    RecyclerView recyclerView;
    Selling_List_Adapter adapter;
    List<Map<String, String>> sellingData = new ArrayList<>();
    List<Map<String, String>> filteredData = new ArrayList<>();
    private LoadFilterFragments loadFilterFragments;
    boolean isAllSelected = false, isCowSelected = false, isBuffaloSelected = false;
    private String selectedLayout = "";
    String userid;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pashu_update_option, container, false);
        view.setBackgroundColor(Color.WHITE);

        sharedPrefMgr = new SharedPreferenceManager(requireContext());
        sharedPrefMgr.connectDB();
        userid = sharedPrefMgr.getString(Iconstant.userid);
        sharedPrefMgr.closeDB();

        loadFilterFragments = (LoadFilterFragments) getActivity();
        allLayout = view.findViewById(R.id.allLayout);
        cowLayout = view.findViewById(R.id.cowLayout);
        buffaloLayout = view.findViewById(R.id.buffaloLayout);
        allLTxt = view.findViewById(R.id.allLTxt);
        cowTxt = view.findViewById(R.id.cowTxt);
        buffaloTxt = view.findViewById(R.id.buffaloTxt);
        id_data_not_found = view.findViewById(R.id.id_data_not_found);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new Selling_List_Adapter(getContext(), filteredData);
        recyclerView.setAdapter(adapter);

        historyList();

        allLTxt.setText(R.string.str_pashu_becha);
        cowTxt.setText(R.string.str_cow);
        buffaloTxt.setText(R.string.str_buffalo);

        allLayout.setOnClickListener(v -> {
            Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.click_animation);
            v.startAnimation(animation);
            toggleSelection(v, 1);
            filterList();
        });

        cowLayout.setOnClickListener(v -> {
            Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.click_animation);
            v.startAnimation(animation);
            toggleSelection(v, 2);
            Intent i = new Intent(requireContext(), LoadFormFragments.class);
            i.putExtra("PashuUpdateAddForm_fragment", true);
            i.putExtra("animalType", "गाय");
            startActivity(i);
        });

        buffaloLayout.setOnClickListener(v -> {
            Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.click_animation);
            v.startAnimation(animation);
            toggleSelection(v, 3);
            Intent i = new Intent(requireContext(), LoadFormFragments.class);
            i.putExtra("PashuUpdateAddForm_fragment", true);
            i.putExtra("animalType", "भैंस");
            startActivity(i);
        });

        if (loadFilterFragments != null) {
            loadFilterFragments.bottomMenu.setVisibility(GONE);
            loadFilterFragments.swipeRefreshLayout.setOnRefreshListener(() -> {
                historyList();
            });
            loadFilterFragments.back.setOnClickListener(v -> {
                Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.click_animation);
                v.startAnimation(animation);
                Intent i = new Intent(requireContext(), MainActivity.class);
                i.putExtra("SELECT_HOME_FRAGMENT", true);
                startActivity(i);
                requireActivity().finish();
            });
        }

        toggleSelection(allLayout, 1);
        return view;
    }

    private void toggleSelection(View view, int buttonType) {
        resetButtonColors();
        if (buttonType == 1) {
            isAllSelected = true;
            isCowSelected = false;
            isBuffaloSelected = false;
            selectedLayout = "";
            changeButtonColor(allLayout);
        } else if (buttonType == 2) {
            isAllSelected = false;
            isCowSelected = true;
            isBuffaloSelected = false;
            selectedLayout = "गाय";
            changeButtonColor(cowLayout);
        } else if (buttonType == 3) {
            isAllSelected = false;
            isCowSelected = false;
            isBuffaloSelected = true;
            selectedLayout = "भैंस";
            changeButtonColor(buffaloLayout);
        }
    }

    private void filterList() {
        filteredData.clear();
        if (isAllSelected) {
            filteredData.addAll(sellingData);
        } else {
            for (Map<String, String> map : sellingData) {
                if (map.get("pashu_type") != null && map.get("pashu_type").equals(selectedLayout)) {
                    filteredData.add(map);
                }
            }
        }

        if (filteredData.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            id_data_not_found.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            id_data_not_found.setVisibility(View.GONE);
        }

        if (adapter == null) {
            adapter = new Selling_List_Adapter(getContext(), filteredData);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.updateList(new ArrayList<>(filteredData));
        }
    }

    private void resetButtonColors() {
        resetButtonColor(allLayout);
        resetButtonColor(cowLayout);
        resetButtonColor(buffaloLayout);
    }

    public void resetButtonColor(LinearLayout layout) {
        layout.setBackgroundColor(getResources().getColor(R.color.white));
        TextView textView = findTextViewInsideLinearLayout(layout);
        if (textView != null) {
            textView.setTextColor(getResources().getColor(R.color.black));
        }
    }

    public void changeButtonColor(LinearLayout layout) {
        layout.setBackgroundColor(getResources().getColor(R.color.orange));
        TextView textView = findTextViewInsideLinearLayout(layout);
        if (textView != null) {
            textView.setTextColor(getResources().getColor(R.color.white));
        }
    }

    private TextView findTextViewInsideLinearLayout(LinearLayout layout) {
        for (int i = 0; i < layout.getChildCount(); i++) {
            View child = layout.getChildAt(i);
            if (child instanceof TextView) {
                return (TextView) child;
            }
        }
        return null;
    }

    public void historyList() {
        dialog = new ProgressDialog(getActivity());
        dialog.setMessage(getString(R.string.getting_data));
        dialog.setCancelable(false);
        dialog.show();

        String url = GET_SELLING_LIST;
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());

        Log.d("HistoryList", "Sending request to: " + url);

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        Log.d("HistoryList", "Response received: " + response);

                        if (loadFilterFragments != null) {
                            loadFilterFragments.swipeRefreshLayout.setRefreshing(false);
                        }
                        dialog.dismiss();

                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");
                        Log.d("HistoryList", "Status: " + status);

                        if (status.equalsIgnoreCase("success")) {
                            JSONArray dataArray = jsonObject.getJSONArray("data");
                            Log.d("HistoryList", "Data array length: " + dataArray.length());

                            sellingData.clear();
                            for (int i = dataArray.length() - 1; i >= 0; i--) {
                                JSONObject animalObj = dataArray.getJSONObject(i);
                                Map<String, String> animalInfo = new HashMap<>();
                                animalInfo.put("id", animalObj.optString("id", ""));
                                animalInfo.put("pashu_type", animalObj.optString("pashu_type", ""));
                                animalInfo.put("seller_number", animalObj.optString("seller_number", ""));
                                animalInfo.put("selling_date", animalObj.optString("selling_date", ""));

                                Log.d("HistoryList", "Parsed item: " + animalInfo.toString());
                                sellingData.add(animalInfo);
                            }
                            Log.d("HistoryList", "Total items added to list: " + sellingData.size());

                            requireActivity().runOnUiThread(() -> {
                                filterList();
                                recyclerView.getAdapter().notifyDataSetChanged();
                            });
                        } else {
                            sellingData.clear();
                            requireActivity().runOnUiThread(this::filterList);
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
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("userid", userid);
                Log.d("HistoryList", "Params: " + params.toString());
                return params;
            }
        };

        postRequest.setRetryPolicy(ApiUtils.DEFAULT_RETRY_POLICY);
        requestQueue.add(postRequest);
    }
}