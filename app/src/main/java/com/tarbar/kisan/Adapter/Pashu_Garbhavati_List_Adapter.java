package com.tarbar.kisan.Adapter;

import static com.tarbar.kisan.Helper.constant.GET_BIJDAN_DATA;
import static com.tarbar.kisan.Helper.constant.GET_DOODH_LIST;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.tarbar.kisan.Activities.LoadFilterFragments;
import com.tarbar.kisan.Activities.LoadFragments;
import com.tarbar.kisan.Helper.ApiUtils;
import com.tarbar.kisan.Helper.Iconstant;
import com.tarbar.kisan.Helper.SharedPreferenceManager;
import com.tarbar.kisan.Helper.constant;
import com.tarbar.kisan.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Pashu_Garbhavati_List_Adapter extends RecyclerView.Adapter<Pashu_Garbhavati_List_Adapter.PashuViewHolder> {
    private List<Map<String, String>> animalData;
    String pashuId;
    ProgressDialog dialog;
    private Context context;
    private List<Map<String, String>> dataList = new ArrayList<>();
    private SharedPreferenceManager sharedPrefMgr;
    String mobileNumber, KisanId, KisanNumber,animalnumber,animaltype,byatcnt,animalBlock,animalOff,milkAdditionDate,pashu_garbhavati_date;
    public Pashu_Garbhavati_List_Adapter(Context context, List<Map<String, String>> animalData) {
        this.context = context;
        this.animalData = animalData;
        sharedPrefMgr = new SharedPreferenceManager(context);
        sharedPrefMgr.connectDB();
        mobileNumber = sharedPrefMgr.getString(Iconstant.mobile);
        Log.d("MobileNumber", "Retrieved mobile number: " + mobileNumber);
        sharedPrefMgr.closeDB();
    }

    @NonNull
    @Override
    public PashuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_pashu_garbhavati_list, parent, false);
        return new PashuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PashuViewHolder holder, int position) {
        Map<String, String> animalInfo = animalData.get(position);
        Log.d("PashuListAdapter", "Animal Info: " + animalInfo.toString());
        pashuId = animalInfo.get("animalId");
        animalnumber = animalInfo.get("animalNo");
//        animalBlock = animalInfo.get("animalBlock");
//        animalOff = animalInfo.get("animalOff");

        String formattedAnimalNo = animalnumber.substring(0, 6) + "\n" + animalnumber.substring(6);
        holder.tvAnimalNo.setText(formattedAnimalNo);
        animaltype = animalInfo.get("type");
        KisanNumber = animalInfo.get("kisanNumber");
        String pashu_garbhavati_date = animalInfo.get("pashu_garbhavati_date");
        if(pashu_garbhavati_date.equals("null") || pashu_garbhavati_date.isEmpty()) {
            holder.tvDate.setText("-");
        } else {
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Date date = inputFormat.parse(pashu_garbhavati_date);
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());
                String formattedDate = outputFormat.format(date);
                holder.tvDate.setText(formattedDate);
            } catch (ParseException e) {
                e.printStackTrace();
                holder.tvDate.setText(pashu_garbhavati_date);
            }
        }
        Log.d("pashu_garbhavati_date", "Value: " + pashu_garbhavati_date);
        Log.d("CheckParams",""+KisanNumber);

        if (!KisanNumber.equals(mobileNumber)) {
            Log.d("CheckKK", "KisanNumber is not equal to mobileNumber");
            holder.tvAddDate.setVisibility(View.GONE);
            holder.tvAddDateShadow.setVisibility(View.VISIBLE);
        } else if (KisanNumber.equals(mobileNumber)) {
            Log.d("CheckKK", "KisanNumber is equal to mobileNumber");
//            if (holder.tvDate.equals("-")) {
//                Log.d("CheckKK", "holder.tvDate is '-'");
//                showAddData(holder.tvAddDate, holder.tvAddDateShadow);
//            } else {
//                Log.d("CheckKK", "holder.tvDate is not '-'");
//                showAddDataShadow(holder.tvAddDate, holder.tvAddDateShadow);
//            }
            showAddData(holder.tvAddDate, holder.tvAddDateShadow);
        }

        if (animaltype != null) {
            if (animaltype.equals(holder.itemView.getContext().getString(R.string.str_cow))) {
                holder.imgType.setImageResource(R.drawable.cow_img);
            } else if (animaltype.equals(holder.itemView.getContext().getString(R.string.str_buffalo))) {
                holder.imgType.setImageResource(R.drawable.buffalo_img);
            }
        }

//        Add Data form

        holder.tvAddDate.setOnClickListener(v -> {
            Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.click_animation);
            v.startAnimation(animation);
            if (animalBlock != null && animalBlock.equals("1")) {
                new AlertDialog.Builder(holder.itemView.getContext())
                        .setMessage(R.string.msg_animal_blocked)
                        .setPositiveButton(R.string.ok, (dialog, which) -> dialog.dismiss())
                        .show();
            } else {
                String UserId = animalInfo.get("userId");
                String KisanMobileNumber = animalInfo.get("kisanNumber");
                animalnumber = animalInfo.get("animalNo");
                String animalId = animalInfo.get("animalId");
                animalnumber = animalInfo.get("animalNo");
                animaltype = animalInfo.get("type");

                Log.d("AnimalInfo", "animalId: " + animalId);
                Log.d("AnimalInfo", "animalnumber: " + animalnumber);
                Log.d("AnimalInfo", "animaltype: " + animaltype);

                Intent intent = new Intent(holder.itemView.getContext(), LoadFilterFragments.class);
                intent.putExtra("GarbhavatiForm_fragment", true);
                intent.putExtra("userId", UserId);
                intent.putExtra("kisanmobileNumber", KisanMobileNumber);
                intent.putExtra("animalId", animalId);
                intent.putExtra("animalNumber", animalnumber);
                intent.putExtra("animalType", animaltype);
                holder.itemView.getContext().startActivity(intent);
            }
        });

        holder.dateLayout.setOnClickListener(v -> handleClick(v, animalInfo));
        holder.tvDate.setOnClickListener(v -> handleClick(v, animalInfo));
        holder.ivIcon.setOnClickListener(v -> handleClick(v, animalInfo));
    }

    private void handleClick(View v, Map<String, String> animalInfo) {
        Animation animation = AnimationUtils.loadAnimation(v.getContext(), R.anim.click_animation);
        v.startAnimation(animation);
//        if (animalBlock != null && animalBlock.equals("1")) {
//            new AlertDialog.Builder(holder.itemView.getContext())
//                    .setMessage(R.string.msg_animal_blocked)
//                    .setPositiveButton(R.string.ok, (dialog, which) -> dialog.dismiss())
//                    .show();
//        } else {
        if ("-".equals(animalInfo.get("pashu_garbhavati_date"))) {
            if (mobileNumber.equals(animalInfo.get("kisanNumber"))) {
                if (pashu_garbhavati_date != null && !pashu_garbhavati_date.isEmpty() && !pashu_garbhavati_date.equals("null")) {
                    new AlertDialog.Builder(v.getContext())
                            .setMessage(R.string.str_data_not_found)
                            .setPositiveButton(R.string.ok, (dialog, which) -> dialog.dismiss())
                            .show();
                } else {
                    new AlertDialog.Builder(v.getContext())
                            .setMessage(R.string.msg_add_data)
                            .setPositiveButton(R.string.ok, (dialog, which) -> dialog.dismiss())
                            .show();
                }
            } else {
                new AlertDialog.Builder(v.getContext())
                        .setMessage(R.string.str_data_not_found)
                        .setPositiveButton(R.string.ok, (dialog, which) -> dialog.dismiss())
                        .show();
            }
        } else {
            String animalId = animalInfo.get("animalId");
            String animalNumber = animalInfo.get("animalNo");
            String animalType = animalInfo.get("type");
            dateListData(animalId,animalNumber,animalType);
        }
//        }
    }

    @Override
    public int getItemCount() {
        return animalData.size();
    }

    private void showAddData(View tvAddByaat, View tvAddByaatShadow) {
        Log.d("showAddData", "Setting AddByaat visible and AddByaatShadow gone.");
        tvAddByaat.setVisibility(View.VISIBLE);
        tvAddByaatShadow.setVisibility(View.GONE);
    }

    private void showAddDataShadow(View tvAddByaat, View tvAddByaatShadow) {
        Log.d("showAddDataShadow", "Setting AddByaatShadow visible and AddByaat gone.");
        tvAddByaat.setVisibility(View.GONE);
        tvAddByaatShadow.setVisibility(View.VISIBLE);
    }

    private void dateListData(String PashuId, String PashuNumber, String PashuType) {
        dialog = new ProgressDialog(context);
        dialog.setMessage(context.getString(R.string.getting_data));
        dialog.setCancelable(false);
        dialog.show();
        String url = GET_BIJDAN_DATA;
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            dialog.dismiss();
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("status");
                            if (status.equals("success")) {
                                JSONArray dataArray = jsonObject.getJSONArray("data");
                                dataList.clear();
                                for (int i = 0; i < dataArray.length(); i++) {
                                    JSONObject data = dataArray.getJSONObject(i);
                                    Map<String, String> bijdanData = new HashMap<>();
                                    bijdanData.put("id", data.getString("id"));
                                    bijdanData.put("userid", data.getString("userid"));
                                    bijdanData.put("pashu_id", data.getString("pashu_id"));
                                    bijdanData.put("bull_number", data.getString("bull_number"));
                                    bijdanData.put("doctor_number", data.getString("doctor_number"));
                                    bijdanData.put("bijdan_date", data.getString("bijdan_date"));
                                    bijdanData.put("future_birth_date", data.getString("future_birth_date"));
                                    bijdanData.put("created_at", data.getString("created_at"));
                                    bijdanData.put("updated_at", data.getString("updated_at"));
                                    dataList.add(bijdanData);
                                }
                                String totalCountLast301Days = jsonObject.getString("total_count_last_301_days");
                                showRecyclerViewDialog(context, PashuNumber, PashuType, totalCountLast301Days);
                            } else if (status.equals("error")) {
                                Toast.makeText(context, R.string.str_data_not_found, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            dialog.dismiss();
                            if (e instanceof JSONException) {
                                Log.d("JSONException", "" + e);
                                Toast.makeText(context, R.string.json_parsing_error, Toast.LENGTH_SHORT).show();
                            } else {
                                Log.d("CheckException", "" + e);
                                Toast.makeText(context, R.string.generic_error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dialog.dismiss();
                        if (error instanceof TimeoutError) {
                            Toast.makeText(context, R.string.timeout_error, Toast.LENGTH_SHORT).show();
                        } else if (error instanceof NoConnectionError) {
                            Toast.makeText(context, R.string.no_connection_error, Toast.LENGTH_SHORT).show();
                        } else if (error instanceof AuthFailureError) {
                            Toast.makeText(context, R.string.auth_failure_error, Toast.LENGTH_SHORT).show();
                        } else if (error instanceof ServerError) {
                            Log.d("ServerError", "" + error);
                            Toast.makeText(context, R.string.server_error, Toast.LENGTH_SHORT).show();
                        } else if (error instanceof NetworkError) {
                            Toast.makeText(context, R.string.network_error, Toast.LENGTH_SHORT).show();
                        } else if (error instanceof ParseError) {
                            Toast.makeText(context, R.string.parse_error, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, R.string.unknown_error, Toast.LENGTH_SHORT).show();
                        }
                        Log.d("Check", "" + error.getMessage());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("pashu_id", PashuId);
                return params;
            }
        };
        postRequest.setRetryPolicy(ApiUtils.DEFAULT_RETRY_POLICY);
        requestQueue.add(postRequest);
    }


    public void updateList(List<Map<String, String>> newAnimalData) {
        if (animalData != null) {
            animalData.clear();
            animalData.addAll(newAnimalData);
        } else {
            animalData = new ArrayList<>(newAnimalData);
        }
        notifyDataSetChanged();
    }

    private void showRecyclerViewDialog(Context context, String animalNumber,String PashuType,String totalCountLast301Days) {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.layout_pashu_bijdan_date, null);
        TextView PashuNumber = dialogView.findViewById(R.id.PashuNumber);
        TextView pashuType = dialogView.findViewById(R.id.PashuType);
        TextView Pashu300DaysBijdan = dialogView.findViewById(R.id.Pashu300DaysBijdan);
        PashuNumber.setText(context.getString(R.string.title_pashu_number) + " : " + animalNumber);
        pashuType.setText(context.getString(R.string.title_pashu_prakar) + " : " + PashuType);
        Pashu300DaysBijdan.setText(context.getString(R.string.title_300_days_total_bijdan) + " : " + totalCountLast301Days);
        Collections.reverse(dataList);
        RecyclerView recyclerView = dialogView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        ShowData_Adapter adapter = new ShowData_Adapter(dataList);
        recyclerView.setAdapter(adapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);
        builder.setPositiveButton(R.string.cancel, null);
        builder.show();
    }

    public static class PashuViewHolder extends RecyclerView.ViewHolder {
        TextView tvAnimalNo, tvDate;
        ImageView imgType, tvAddDate, tvAddDateShadow,ivIcon;
        LinearLayout lnrAddDoodh, dateLayout;

        public PashuViewHolder(View itemView) {
            super(itemView);
            tvAnimalNo = itemView.findViewById(R.id.tvAnimalNo);
            imgType = itemView.findViewById(R.id.tvType);
            tvAddDate = itemView.findViewById(R.id.tvAddDate);
            tvDate = itemView.findViewById(R.id.tvDate);
            ivIcon = itemView.findViewById(R.id.ivIcon);
            lnrAddDoodh = itemView.findViewById(R.id.lnrAddDoodh);
            dateLayout = itemView.findViewById(R.id.dateLayout);
            tvAddDateShadow = itemView.findViewById(R.id.tvAddDateShadow);
        }
    }

    public class ShowData_Adapter extends RecyclerView.Adapter<ShowData_Adapter.ShowDataViewHolder> {
        private List<Map<String, String>> items;

        public ShowData_Adapter(List<Map<String, String>> items) {
            this.items = items;
        }

        @NonNull
        @Override
        public ShowDataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_show_bijdan_data, parent, false);
            return new ShowDataViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ShowDataViewHolder holder, int position) {
            Map<String, String> item = items.get(position);

            String bijdanBullNumber = item.get("bull_number");
            String bijdanDate = item.get("bijdan_date");
            if (bijdanBullNumber == null || bijdanBullNumber.isEmpty() || bijdanBullNumber.equals("null")) {
                bijdanBullNumber = "-";
            }
            if (bijdanDate == null || bijdanDate.isEmpty() || bijdanDate.equals("null")) {
                bijdanDate = "-";
            }
            holder.txtBullNumber.setText(bijdanBullNumber);
            holder.date.setText(bijdanDate);

            String userNumber = "7058143404";

            holder.img_checkDetails.setOnClickListener(v -> {
                Intent intent = new Intent(holder.itemView.getContext(), LoadFilterFragments.class);
                intent.putExtra("GarbhavatiForm_fragment_update", true);
                intent.putExtra("bijdanId", item.get("id"));
                intent.putExtra("kisanmobileNumber", userNumber);
//                intent.putExtra("animalId", item.get("pashu_id"));
//                intent.putExtra("animalNumber",animalnumber);
//                intent.putExtra("animalType",animaltype);
//                intent.putExtra("byatcnt",byatcnt);
//                intent.putExtra("doodhId", item.get("id"));
//                intent.putExtra("lastMilk", milkAdditionDate);
                holder.itemView.getContext().startActivity(intent);
            });

            holder.lyt_checkDetails.setOnClickListener(v -> {
                Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.click_animation);
                v.startAnimation(animation);
                Intent intent = new Intent(holder.itemView.getContext(), LoadFilterFragments.class);
                intent.putExtra("GarbhavatiForm_fragment_update", true);
                intent.putExtra("bijdanId", item.get("id"));
                intent.putExtra("kisanmobileNumber", userNumber);
//                intent.putExtra("animalId", item.get("pashu_id"));
//                intent.putExtra("animalNumber",animalnumber);
//                intent.putExtra("animalType",animaltype);
//                intent.putExtra("byatcnt",byatcnt);
//                intent.putExtra("doodhId", item.get("id"));
//                intent.putExtra("lastMilk", milkAdditionDate);
                holder.itemView.getContext().startActivity(intent);
            });
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        public class ShowDataViewHolder extends RecyclerView.ViewHolder {
            TextView txtBullNumber, date;
            ImageView img_checkDetails,img_whatsapp;
            LinearLayout lyt_checkDetails;

            public ShowDataViewHolder(View itemView) {
                super(itemView);
                txtBullNumber = itemView.findViewById(R.id.txtBullNumber);
                date = itemView.findViewById(R.id.date);
                img_checkDetails = itemView.findViewById(R.id.img_checkDetails);
                lyt_checkDetails = itemView.findViewById(R.id.lyt_checkDetails);
            }
        }
    }
}

//   private void showDateRangeDialog() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
//        LayoutInflater inflater = getLayoutInflater();
//        View dialogView = inflater.inflate(R.layout.layout_dialog_date_range, null);
//
//        RecyclerView dateRangeRecyclerView = dialogView.findViewById(R.id.dateRangeRecyclerView);
//
//        List<String> dateRanges = new ArrayList<>();
//
//        Calendar calendar = Calendar.getInstance();
//        int currentYear = calendar.get(Calendar.YEAR);
//
//        dateRanges.add(String.valueOf(R.string.str_april_2024_to_march_2025));
//
//        for (int year = 2025; year <= currentYear; year++) {
//            dateRanges.add("1 " + getString(R.string.str_april) + " " + year + " " + getString(R.string.str_se) + " 31 " + getString(R.string.str_march) + " " + (year + 1));
//        }
//
//        DateRangeAdapter adapter = new DateRangeAdapter(dateRanges);
//        dateRangeRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
//        dateRangeRecyclerView.setAdapter(adapter);
//
//        adapter.setOnItemSelectedListener(selectedDateRange -> {
//            String[] dates = selectedDateRange.split(" " +String.valueOf(R.string.str_se)+ " ");
//            String startDate = dates[0].trim();
//            String endDate = dates[1].trim();
//            String fromDate = convertToDateFormat(startDate);
//            String toDate = convertToDateFormat(endDate);
//            getLeaderboardList(fromDate, toDate);
//            dialog.dismiss();
//        });
//        builder.setView(dialogView);
//        AlertDialog dialog = builder.create();
//        dialog.show();
//    }
//
//    private String convertToDateFormat(String date) {
//        try {
//            SimpleDateFormat sdf = new SimpleDateFormat("d MMM yyyy", Locale.ENGLISH);
//            Date parsedDate = sdf.parse(date);
//            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
//            return outputFormat.format(parsedDate);
//        } catch (ParseException e) {
//            e.printStackTrace();
//            return "";
//        }
//    }