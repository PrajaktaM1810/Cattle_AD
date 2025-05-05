package com.tarbar.kisan.Fragments;

import static com.tarbar.kisan.Helper.constant.GET_BYAT_LIST;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.tarbar.kisan.Helper.ApiUtils;
import com.tarbar.kisan.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Byat_List_Fragment extends Fragment {

    RecyclerView recyclerView;
    TextView PahuNumber;
    Byat_List_Adapter adapter;
    List<Map<String, String>> byatList;
    String pashuId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_byat_list, container, false);
        view.setBackgroundColor(Color.WHITE);
        recyclerView = view.findViewById(R.id.recyclerView);
        PahuNumber = view.findViewById(R.id.PahuNumber);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        byatList = new ArrayList<>();
        adapter = new Byat_List_Adapter(byatList);
        recyclerView.setAdapter(adapter);

        Bundle arguments = getArguments();
        if (arguments != null) {
            pashuId = arguments.getString("pashu_id", "1");
            fetchByatList(pashuId);
        }

        fetchByatList(pashuId);

        return view;
    }

    private void fetchByatList(String pashuId) {
        String url = GET_BYAT_LIST;
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");
                        if (status.equals("success")) {
                            JSONArray dataArray = jsonObject.getJSONArray("data");
                            byatList.clear();
                            for (int i = 0; i < dataArray.length(); i++) {
                                JSONObject dataObject = dataArray.getJSONObject(i);
                                Map<String, String> byatDetails = new HashMap<>();
                                byatDetails.put("byat_id", dataObject.getString("byat_id"));
                                byatDetails.put("user_id", dataObject.getString("user_id"));
                                byatDetails.put("pashu_id", dataObject.getString("pashu_id"));
                                byatDetails.put("pashu_number", dataObject.getString("pashu_number"));
                                byatDetails.put("pashu_type", dataObject.getString("pashu_type"));
                                byatDetails.put("pashu_byat_count", dataObject.getString("pashu_byat_count"));
                                byatDetails.put("byat_number", dataObject.getString("byat_number"));
                                byatDetails.put("byat_gender", dataObject.getString("byat_gender"));
                                byatDetails.put("byat_birthdate", dataObject.getString("byat_birthdate"));
                                byatDetails.put("is_village_bull", dataObject.getString("is_village_bull"));
                                byatDetails.put("doctor_number", dataObject.getString("doctor_number"));
                                byatDetails.put("kisan_number", dataObject.getString("kisan_number"));
                                byatDetails.put("tarbar_bull_number", dataObject.getString("tarbar_bull_number"));

                                byatList.add(byatDetails);
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            String message = jsonObject.getString("message");
                            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Error parsing data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(getContext(), "Error fetching data", Toast.LENGTH_SHORT).show()) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("pashu_id", pashuId);
                Log.d("RequestParameter_Pashuid", "" + pashuId);
                return params;
            }
        };
        stringRequest.setRetryPolicy(ApiUtils.DEFAULT_RETRY_POLICY);
        requestQueue.add(stringRequest);
    }

    public class Byat_List_Adapter extends RecyclerView.Adapter<Byat_List_Adapter.ByatViewHolder> {

        List<Map<String, String>> byatItems;

        public Byat_List_Adapter(List<Map<String, String>> byatItems) {
            this.byatItems = byatItems;
        }

        @NonNull
        @Override
        public ByatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_byat_list, parent, false);
            return new ByatViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ByatViewHolder holder, int position) {
            Map<String, String> byatItem = byatItems.get(position);
            holder.tvByatCount.setText(byatItem.get("pashu_byat_count"));
            holder.tvByaatBirthdate.setText(byatItem.get("byat_birthdate"));
        }

        @Override
        public int getItemCount() {
            return byatItems.size();
        }

        public class ByatViewHolder extends RecyclerView.ViewHolder {
            TextView tvByatCount, tvByaatBirthdate;
            ImageView img_seeDetails;
            LinearLayout seeDetails;

            public ByatViewHolder(View itemView) {
                super(itemView);
                tvByatCount = itemView.findViewById(R.id.tvByatCount);
                tvByaatBirthdate = itemView.findViewById(R.id.tvByaatBirthdate);
                img_seeDetails = itemView.findViewById(R.id.img_seeDetails);
                seeDetails = itemView.findViewById(R.id.seeDetails);
            }
        }
    }
}
