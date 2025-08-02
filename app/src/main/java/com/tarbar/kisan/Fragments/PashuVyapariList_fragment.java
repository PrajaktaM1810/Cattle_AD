package com.tarbar.kisan.Fragments;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
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
import com.tarbar.kisan.Adapter.PashuVyapariList_Adapter;
import com.tarbar.kisan.Helper.ApiUtils;
import com.tarbar.kisan.Helper.Iconstant;
import com.tarbar.kisan.Helper.SharedPreferenceManager;
import com.tarbar.kisan.Helper.constant;
import com.tarbar.kisan.R;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.util.Log;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.Editable;

public class PashuVyapariList_fragment extends Fragment {
    private SharedPreferenceManager sharedPrefMgr;
    ProgressDialog dialog;
    String mobileNumber;
    LinearLayout allLayout, cowLayout, buffaloLayout;
    TextView allLTxt, cowTxt, buffaloTxt;
    TextView totalRecords, id_data_not_found,kisanNumber;
    RecyclerView recyclerView;
    PashuVyapariList_Adapter adapter;
    List<Map<String, String>> data = new ArrayList<>();
    List<Map<String, String>> filteredData = new ArrayList<>();
    List<Map<String, String>> filteredSearchData = new ArrayList<>();
    List<Map<String, String>> activeList;
    private LoadFilterFragments loadFilterFragmentsActivityInstance;
    boolean isAllSelected = false, isCowSelected = false, isBuffaloSelected = false, bySearch = false;
    private String selectedLayout = "";
    String userId,KisanmobileNumber;
    AppCompatEditText searchEditText;
    ImageView cancelIcon;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pashuvyapari_list, container, false);
        view.setBackgroundColor(Color.WHITE);
        sharedPrefMgr = new SharedPreferenceManager(getContext());
        sharedPrefMgr.connectDB();
        mobileNumber = sharedPrefMgr.getString(Iconstant.mobile);
        Log.d("MobileNumber", "" + mobileNumber);
        sharedPrefMgr.closeDB();
        loadFilterFragmentsActivityInstance = (LoadFilterFragments) getActivity();
        allLayout = view.findViewById(R.id.allLayout);
        cowLayout = view.findViewById(R.id.cowLayout);
        buffaloLayout = view.findViewById(R.id.buffaloLayout);
        allLTxt = view.findViewById(R.id.allLTxt);
        cowTxt = view.findViewById(R.id.cowTxt);
        buffaloTxt = view.findViewById(R.id.buffaloTxt);
        totalRecords = view.findViewById(R.id.totalRecords);
        id_data_not_found = view.findViewById(R.id.id_data_not_found);
        kisanNumber = view.findViewById(R.id.kisanNumber);
        searchEditText = view.findViewById(R.id.searchEditText);
        cancelIcon = view.findViewById(R.id.cancelIcon);

        InputFilter filter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    if (!Character.isDigit(source.charAt(i))) {
                        return "";
                    }
                }
                return null;
            }
        };
        searchEditText.setFilters(new InputFilter[]{filter});

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    cancelIcon.setVisibility(View.VISIBLE);
                } else {
                    cancelIcon.setVisibility(View.GONE);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
                String searchText = s.toString().trim();
                if (!searchText.isEmpty()) {
                    bySearch = true;
                    filteredSearchData.clear();
                    for (Map<String, String> item : data) {
                        String sellerNumber = item.get("sellerNumber");
                        if (sellerNumber != null && sellerNumber.contains(searchText)) {
                            filteredSearchData.add(item);
                        }
                    }
                    if (isAllSelected) {
                        applyFilter(filteredSearchData, "");
                    } else if (isCowSelected) {
                        applyFilter(filteredSearchData, "गाय");
                    } else if (isBuffaloSelected) {
                        applyFilter(filteredSearchData, "भैंस");
                    }
                } else {
                    bySearch = false;
                    if (isAllSelected) {
                        applyFilter(data, "");
                    } else if (isCowSelected) {
                        applyFilter(data, "गाय");
                    } else if (isBuffaloSelected) {
                        applyFilter(data, "भैंस");
                    }
                }
            }
        });

        cancelIcon.setOnClickListener(v -> {
            searchEditText.setText("");
            cancelIcon.setVisibility(View.GONE);
        });

        Bundle bundle = getArguments();
        if (bundle != null) {
            userId = bundle.getString("userId");
            KisanmobileNumber = bundle.getString("kisanmobileNumber");
            Log.d("PashuVyapariListFragment", "userId: " + userId);
            Log.d("PashuVyapariListFragment", "kisanmobileNumber: " + KisanmobileNumber);
            kisanNumber.setText(KisanmobileNumber);
            kisanNumber.setText(getString(R.string.str_vyapari_number, KisanmobileNumber));
        } else {
            userId = "";
            KisanmobileNumber = "";
        }

        recyclerView = view.findViewById(R.id.recyclerView);
        adapter = new PashuVyapariList_Adapter(data, requireContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        getUserSellingHistory(userId);

        allLTxt.setText(R.string.str_sabhi);
        cowTxt.setText(R.string.str_cow);
        buffaloTxt.setText(R.string.str_buffalo);

        allLayout.setOnClickListener(v -> {
            Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.click_animation);
            v.startAnimation(animation);
            toggleSelection(v, 1);
        });

        cowLayout.setOnClickListener(v -> {
            Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.click_animation);
            v.startAnimation(animation);
            toggleSelection(v, 2);
        });

        buffaloLayout.setOnClickListener(v -> {
            Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.click_animation);
            v.startAnimation(animation);
            toggleSelection(v, 3);
        });

        if (loadFilterFragmentsActivityInstance != null && loadFilterFragmentsActivityInstance.cardView != null) {
            loadFilterFragmentsActivityInstance.iconbutton1.setBackgroundResource(R.drawable.icon_calender);
            loadFilterFragmentsActivityInstance.swipeRefreshLayout.setOnRefreshListener(() -> {
                bySearch = false;
                getUserSellingHistory(userId);
            });

            loadFilterFragmentsActivityInstance.back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.click_animation);
                    v.startAnimation(animation);
                    requireActivity().getOnBackPressedDispatcher().onBackPressed();
                }
            });
        }
        toggleSelection(allLayout, 1);
        return view;
    }

    private void toggleSelection(View view, int buttonType) {
        resetButtonColors();
        List<Map<String, String>> activeList;

        if (bySearch) {
            activeList = filteredSearchData;
        } else {
            activeList = data;
        }

        if (buttonType == 1) {
            isAllSelected = true;
            isCowSelected = false;
            isBuffaloSelected = false;
            selectedLayout = "";
            changeButtonColor(allLayout);
            applyFilter(activeList, "");
        } else if (buttonType == 2) {
            isCowSelected = true;
            isAllSelected = false;
            isBuffaloSelected = false;
            selectedLayout = "गाय";
            changeButtonColor(cowLayout);
            applyFilter(activeList, "गाय");
        } else if (buttonType == 3) {
            isBuffaloSelected = true;
            isAllSelected = false;
            isCowSelected = false;
            selectedLayout = "भैंस";
            changeButtonColor(buffaloLayout);
            applyFilter(activeList, "भैंस");
        }
    }

    private void applyFilter(List<Map<String, String>> sourceList, String typeFilter) {
        filteredData.clear();
        for (Map<String, String> item : sourceList) {
            String type = item.get("pashuType");
            if (typeFilter.isEmpty() || (type != null && type.equalsIgnoreCase(typeFilter))) {
                filteredData.add(item);
            }
        }
        if (filteredData.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            id_data_not_found.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            id_data_not_found.setVisibility(View.GONE);
        }
        adapter.updateList(filteredData);
        totalRecords.setText(getString(R.string.total_ranks, String.valueOf(filteredData.size())));
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

    public void getUserSellingHistory(String userId) {
        ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage(getString(R.string.getting_data));
        dialog.setCancelable(false);
        dialog.show();

        String url = constant.GET_USER_SELLING_HISTORY + "?userid=" + userId;
        Log.d("Request_URL", "Constructed URL: " + url);
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());

        StringRequest getRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    bySearch = false;
                    data.clear();
                    filteredData.clear();
                    filteredSearchData.clear();
                    toggleSelection(allLayout, 1);
                    loadFilterFragmentsActivityInstance.swipeRefreshLayout.setRefreshing(false);
                    Log.d("Response", "Response: " + response);
                    dialog.dismiss();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.optString("status", "");

                        if ("success".equalsIgnoreCase(status)) {
                            recyclerView.setVisibility(View.VISIBLE);
                            id_data_not_found.setVisibility(View.GONE);
                            JSONArray dataArray = jsonObject.getJSONArray("data");
                            for (int i = 0; i < dataArray.length(); i++) {
                                JSONObject animalObj = dataArray.getJSONObject(i);

                                Map<String, String> animalInfo = new HashMap<>();
                                animalInfo.put("rank", String.valueOf(i + 1));
                                animalInfo.put("userId", animalObj.optString("userid", ""));
                                animalInfo.put("pashuId", animalObj.optString("id", ""));
                                animalInfo.put("pashuType", animalObj.optString("pashu_type", ""));
                                animalInfo.put("sellerNumber", animalObj.optString("seller_number", ""));
                                animalInfo.put("sellingDate", animalObj.optString("selling_date", ""));
                                animalInfo.put("createdAt", animalObj.optString("created_date", ""));
                                animalInfo.put("updatedAt", animalObj.optString("updated_at", ""));
                                data.add(animalInfo);
                            }
                            adapter.updateList(data);
                            totalRecords.setText(getString(R.string.total_ranks, String.valueOf(dataArray.length())));
                        } else {
                            recyclerView.setVisibility(View.GONE);
                            id_data_not_found.setVisibility(View.VISIBLE);
                            totalRecords.setText(R.string.total_ranks_zero);
                        }
                    } catch (JSONException e) {
                        Toast.makeText(getContext(), R.string.json_parsing_error, Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    loadFilterFragmentsActivityInstance.swipeRefreshLayout.setRefreshing(false);
                    dialog.dismiss();
                    handleVolleyError(error);
                });
        getRequest.setRetryPolicy(ApiUtils.DEFAULT_RETRY_POLICY);
        requestQueue.add(getRequest);
    }

    private void handleVolleyError(VolleyError error) {
        if (error.networkResponse != null) {
            Log.e("ErrorResponse", "Status Code: " + error.networkResponse.statusCode);
            Log.e("ErrorResponse", "Response Data: " + new String(error.networkResponse.data));
        }
        String errorMessage = getString(R.string.unknown_error);
        if (error instanceof TimeoutError) {
            errorMessage = getString(R.string.timeout_error);
        } else if (error instanceof NoConnectionError) {
            errorMessage = getString(R.string.no_connection_error);
        } else if (error instanceof AuthFailureError) {
            errorMessage = getString(R.string.auth_failure_error);
        } else if (error instanceof ServerError) {
            errorMessage = getString(R.string.server_error);
        } else if (error instanceof NetworkError) {
            errorMessage = getString(R.string.network_error);
        } else if (error instanceof ParseError) {
            errorMessage = getString(R.string.parse_error);
        }
        Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
    }
}