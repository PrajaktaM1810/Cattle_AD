package com.tarbar.kisan.Adapter;

import static com.tarbar.kisan.Helper.constant.GET_BYAT_LIST;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
import com.tarbar.kisan.Activities.Checking;
import com.tarbar.kisan.Activities.LoadFragments;
import com.tarbar.kisan.Activities.LoginActivity;
import com.tarbar.kisan.Activities.MainActivity;
import com.tarbar.kisan.Activities.OtpActivity;
import com.tarbar.kisan.Helper.ApiUtils;
import com.tarbar.kisan.Helper.Iconstant;
import com.tarbar.kisan.Helper.SharedPreferenceManager;
import com.tarbar.kisan.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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

public class Pashu_List_Adapter extends RecyclerView.Adapter<Pashu_List_Adapter.PashuViewHolder> {
    private List<Map<String, String>> animalData;
    String pashuId;
    ProgressDialog dialog;
    private Context context;
    private List<Map<String, String>> byaatList = new ArrayList<>();
    private SharedPreferenceManager sharedPrefMgr;
    String mobileNumber, KisanNumber,animalnumber,animaltype,byatcnt,animalBlock,animalOff;
    public Pashu_List_Adapter(Context context, List<Map<String, String>> animalData) {
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_pashu_list, parent, false);
        return new PashuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PashuViewHolder holder, int position) {
        Map<String, String> animalInfo = animalData.get(position);
        Log.d("PashuListAdapter", "Animal Info: " + animalInfo.toString());
        pashuId = animalInfo.get("animalId");
        animalnumber = animalInfo.get("animalNo");
        animaltype = animalInfo.get("type");
        byatcnt = animalInfo.get("byaatCount");
        animalBlock = animalInfo.get("animalBlock");
        animalOff = animalInfo.get("animalOff");

        String formattedAnimalNo = animalnumber.substring(0, 6) + "\n" + animalnumber.substring(6);
        holder.tvAnimalNo.setText(formattedAnimalNo);
        String byatcount = animalInfo.get("byaatCount");
        if (byatcount.equals("NULL") || byatcount.equals("Null") || byatcount.equals("null")  || byatcount.isEmpty()) {
            holder.tvByaat.setText("0");
        } else {
            holder.tvByaat.setText(animalInfo.get("byaatCount"));
        }
        String animalType = animalInfo.get("type");
        KisanNumber = animalInfo.get("kisanNumber");
        String latest_byat_registration = animalInfo.get("latest_byat");
        Log.d("LatestByatRegistration", "Value: " + latest_byat_registration);

        Log.d("CheckParams",""+latest_byat_registration);
        Log.d("CheckParams",""+KisanNumber);

        if(!KisanNumber.equals(mobileNumber)){
            holder.tvAddByaat.setVisibility(View.GONE);
            holder.tvAddByaatShadow.setVisibility(View.VISIBLE);
        } else if(KisanNumber.equals(mobileNumber)) {
            if(byatcnt.equals("0")) {
                showAddByaat(holder.tvAddByaat, holder.tvAddByaatShadow);
            } else {
                handleDateLogic(latest_byat_registration, holder.tvAddByaat, holder.tvAddByaatShadow, KisanNumber);
            }
        }

        if (animalType != null) {
            if (animalType.equals(holder.itemView.getContext().getString(R.string.str_cow))) {
                holder.imgType.setImageResource(R.drawable.cow_img);
            } else if (animalType.equals(holder.itemView.getContext().getString(R.string.str_buffalo))) {
                holder.imgType.setImageResource(R.drawable.buffalo_img);
            }
        }

        holder.img_seeDetails.setOnClickListener(v -> {
            Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.click_animation);
            v.startAnimation(animation);
            if (animalBlock != null && animalBlock.equals("1")) {
                new AlertDialog.Builder(holder.itemView.getContext())
                        .setMessage(R.string.msg_animal_blocked)
                        .setPositiveButton(R.string.ok, (dialog, which) -> dialog.dismiss())
                        .show();
            } else {
                String animalId = animalInfo.get("animalId");
                String animalNumber = animalInfo.get("animalNo");
                Intent intent = new Intent(holder.itemView.getContext(), LoadFragments.class);
                intent.putExtra("Fragment_EditAnimalProfile", true);
                intent.putExtra("animalId", animalId);
                intent.putExtra("animalNo", animalNumber);
                intent.putExtra("latest_byat", animalNumber);
                holder.itemView.getContext().startActivity(intent);
            }
        });

//        Add Byat form

        holder.tvAddByaat.setOnClickListener(v -> {
            Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.click_animation);
            v.startAnimation(animation);
            if (animalBlock != null && animalBlock.equals("1")) {
                new AlertDialog.Builder(holder.itemView.getContext())
                        .setMessage(R.string.msg_animal_blocked)
                        .setPositiveButton(R.string.ok, (dialog, which) -> dialog.dismiss())
                        .show();
            } else {
                String KisanId = animalInfo.get("userId");
                String animalId = animalInfo.get("animalId");
                String kisanNo = animalInfo.get("kisanNumber");
                String Last_Date = animalInfo.get("latest_byat");
                String animal_birth_date = animalInfo.get("date");
                Log.d("ByatInfo", "KisanId: " + KisanId);
                Log.d("ByatInfo", "animalId: " + animalId);
                Log.d("ByatInfo", "animalnumber: " + animalnumber);
                Log.d("ByatInfo", "animaltype: " + animaltype);
                Log.d("ByatInfo", "byatcnt: " + byatcnt);
                Log.d("ByatInfo", "kisanNo: " + kisanNo);
                Log.d("ByatInfo", "Last_Date: " + Last_Date);
                Log.d("ByatInfo", "animal_birth_date: " + animal_birth_date);

                Intent intent = new Intent(holder.itemView.getContext(), LoadFragments.class);
                intent.putExtra("Fragment_ByatAddition", true);
                intent.putExtra("KisanId", KisanId);
                intent.putExtra("animalId", animalId);
                intent.putExtra("animalNumber", animalnumber);
                intent.putExtra("animalType", animaltype);
                intent.putExtra("byatcnt", byatcnt);
                intent.putExtra("kisanmobilenumber", kisanNo);
                intent.putExtra("latest_byat", Last_Date);
                intent.putExtra("animal_birth_date", animal_birth_date);
                holder.itemView.getContext().startActivity(intent);
            }
        });

        View.OnClickListener byaatClickListener = v -> {
            Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.click_animation);
            v.startAnimation(animation);
            if (animalBlock != null && animalBlock.equals("1")) {
                new AlertDialog.Builder(holder.itemView.getContext())
                        .setMessage(R.string.msg_animal_blocked)
                        .setPositiveButton(R.string.ok, (dialog, which) -> dialog.dismiss())
                        .show();
            } else {
                String childcnt = animalInfo.get("byaatCount");
                if (childcnt.equals("0")) {
                    new AlertDialog.Builder(holder.itemView.getContext())
                            .setMessage(R.string.str_data_not_found)
                            .setPositiveButton(R.string.ok, (dialog, which) -> dialog.dismiss())
                            .show();
                } else {
                    String animalId = animalInfo.get("animalId");
                    String animalNumber = animalInfo.get("animalNo");
                    byatListData(animalId,animalNumber);
                }
            }
        };
        holder.byatLayout.setOnClickListener(byaatClickListener);
        holder.tvByaat.setOnClickListener(byaatClickListener);
        holder.ivIcon.setOnClickListener(byaatClickListener);
    }

    @Override
    public int getItemCount() {
        return animalData.size();
    }

    private void handleDateLogic(String dateString, View tvAddByaat, View tvAddByaatShadow, String kisannumber) {
        Log.d("handleDateLogic", "Received dateString: " + dateString);

        if (dateString == null) {
            Log.d("handleDateLogic", "dateString is null. Showing AddByaat.");
            showAddByaat(tvAddByaat, tvAddByaatShadow);
            return;
        }

        Date birthDate = parseDate(dateString);
        if (birthDate == null) {
            Log.d("handleDateLogic", "BirthDate parsing returned null. Showing AddByaat.");
            showAddByaat(tvAddByaat, tvAddByaatShadow);
            return;
        }

        Log.d("handleDateLogic", "Parsed birthDate: " + birthDate);

        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        Log.d("handleDateLogic", "Today's date (time normalized): " + today.getTime());

        Calendar birthCalendar = Calendar.getInstance();
        birthCalendar.setTime(birthDate);
        birthCalendar.set(Calendar.HOUR_OF_DAY, 0);
        birthCalendar.set(Calendar.MINUTE, 0);
        birthCalendar.set(Calendar.SECOND, 0);
        birthCalendar.set(Calendar.MILLISECOND, 0);

        if (birthCalendar.getTime().equals(today.getTime())) {
            Log.d("handleDateLogic", "BirthDate is equal to today. Showing AddByaatShadow.");
            showAddByaatShadow(tvAddByaat, tvAddByaatShadow);
            return;
        }

        Calendar birthDatePlus301Days = (Calendar) birthCalendar.clone();
        birthDatePlus301Days.add(Calendar.DAY_OF_YEAR, 301);
        Log.d("handleDateLogic", "BirthDate + 301 days: " + birthDatePlus301Days.getTime());

        Log.d("KisanNumber",""+KisanNumber);

        if (KisanNumber.equals(mobileNumber) && today.getTime().after(birthDatePlus301Days.getTime())) {
                Log.d("handleDateLogic", "Today's date is after BirthDate + 301 days. Showing AddByaat.");
                showAddByaat(tvAddByaat, tvAddByaatShadow);
        } else {
                Log.d("handleDateLogic", "Today's date is before BirthDate + 301 days. Showing AddByaatShadow.");
                showAddByaatShadow(tvAddByaat, tvAddByaatShadow);
        }
    }

    private Date parseDate(String dateString) {
        try {
            Log.d("parseDate", "Attempting to parse date: " + dateString);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            return dateFormat.parse(dateString);
        } catch (ParseException e) {
            Log.e("parseDate", "Date parsing failed for dateString: " + dateString, e);
            return null;
        }
    }

    private void showAddByaat(View tvAddByaat, View tvAddByaatShadow) {
        Log.d("showAddByaat", "Setting AddByaat visible and AddByaatShadow gone.");
        tvAddByaat.setVisibility(View.VISIBLE);
        tvAddByaatShadow.setVisibility(View.GONE);
    }

    private void showAddByaatShadow(View tvAddByaat, View tvAddByaatShadow) {
        Log.d("showAddByaatShadow", "Setting AddByaatShadow visible and AddByaat gone.");
        tvAddByaat.setVisibility(View.GONE);
        tvAddByaatShadow.setVisibility(View.VISIBLE);
    }

    private void byatListData(String PashuId, String PashuNumber) {
        dialog = new ProgressDialog(context);
        dialog.setMessage(context.getString(R.string.getting_data));
        dialog.setCancelable(false);
        dialog.show();
        String url = GET_BYAT_LIST;
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
                                byaatList.clear();
                                for (int i = 0; i < dataArray.length(); i++) {
                                    JSONObject data = dataArray.getJSONObject(i);
                                    Map<String, String> byaatData = new HashMap<>();
                                    byaatData.put("byat_id", data.getString("byat_id"));
                                    byaatData.put("user_id", data.getString("user_id"));
                                    byaatData.put("pashu_id", data.getString("pashu_id"));
                                    byaatData.put("pashu_byat_count", data.getString("pashu_byat_count"));
                                    byaatData.put("byat_gender", data.getString("byat_gender"));
                                    byaatData.put("byat_birthdate", data.getString("byat_birthdate"));
                                    byaatData.put("is_village_bull", data.getString("is_village_bull"));
                                    byaatData.put("doctor_number", data.getString("doctor_number"));
                                    byaatData.put("tarbar_bull_number", data.getString("tarbar_bull_number"));
                                    byaatData.put("kisan_number", data.getString("kisan_number"));
                                    byaatList.add(byaatData);
                                }
                                showRecyclerViewDialog(context,PashuNumber);
                            } else if (status.equals("error")) {
                                Toast.makeText(context, R.string.str_data_not_found, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            dialog.dismiss();
                            if (e instanceof JSONException) {
                                Log.d("JSONException", "" +e);
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

    private void showRecyclerViewDialog(Context context, String animalNumber) {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.layout_show_data, null);
        TextView PashuNumber = dialogView.findViewById(R.id.PashuNumber);
        PashuNumber.setText(context.getString(R.string.title_pashu_number) + " : " + animalNumber);
        Collections.reverse(byaatList);
        RecyclerView recyclerView = dialogView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        ShowData_Adapter adapter = new ShowData_Adapter(byaatList);
        recyclerView.setAdapter(adapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);
        builder.setPositiveButton(R.string.cancel, null);
        builder.show();
    }

    public static class PashuViewHolder extends RecyclerView.ViewHolder {
        TextView tvAnimalNo, tvByaat;
        ImageView imgType, tvAddByaat, img_seeDetails, tvAddByaatShadow,ivIcon;
        LinearLayout lnrAddByaat, seeDetails, byatLayout;

        public PashuViewHolder(View itemView) {
            super(itemView);
            tvAnimalNo = itemView.findViewById(R.id.tvAnimalNo);
            imgType = itemView.findViewById(R.id.tvType);
            tvAddByaat = itemView.findViewById(R.id.tvAddByaat);
            ivIcon = itemView.findViewById(R.id.ivIcon);
            tvByaat = itemView.findViewById(R.id.tvByaat);
            lnrAddByaat = itemView.findViewById(R.id.lnrAddByaat);
            img_seeDetails = itemView.findViewById(R.id.img_seeDetails);
            seeDetails = itemView.findViewById(R.id.seeDetails);
            byatLayout = itemView.findViewById(R.id.byatLayout);
            tvAddByaatShadow = itemView.findViewById(R.id.tvAddByaatShadow);
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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_show_data, parent, false);
            return new ShowDataViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ShowDataViewHolder holder, int position) {
            Map<String, String> item = items.get(position);

            holder.txtbyatcnt.setText("ब्यात " + item.get("pashu_byat_count"));
            String originalDate = item.get("byat_birthdate");
            try {
                SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date date = originalFormat.parse(originalDate);
                SimpleDateFormat desiredFormat = new SimpleDateFormat("dd/MM/yyyy");
                String formattedDate = desiredFormat.format(date);
                holder.date.setText(formattedDate);

            } catch (ParseException e) {
                e.printStackTrace();
                holder.date.setText("");
            }

            holder.img_checkDetails.setOnClickListener(v -> {
                Intent intent = new Intent(holder.itemView.getContext(), LoadFragments.class);
                intent.putExtra("Fragment_ByatProfile", true);
                intent.putExtra("KisanId", item.get("user_id"));
                intent.putExtra("animalId", item.get("pashu_id"));
                intent.putExtra("kisanmobilenumber", item.get("kisan_number"));
                intent.putExtra("byatId", item.get("byat_id"));
                holder.itemView.getContext().startActivity(intent);
            });


            holder.img_checkDetails.setOnClickListener(v -> {
                Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.click_animation);
                v.startAnimation(animation);

                Intent intent = new Intent(holder.itemView.getContext(), LoadFragments.class);
                intent.putExtra("Fragment_ByatProfile", true);
                intent.putExtra("KisanId", item.get("user_id"));
                intent.putExtra("animalId", item.get("pashu_id"));
//                intent.putExtra("byatcnt", byatcnt);
                intent.putExtra("kisanmobilenumber", item.get("kisan_number"));
                intent.putExtra("byatId", item.get("byat_id"));
                holder.itemView.getContext().startActivity(intent);
            });

        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        public class ShowDataViewHolder extends RecyclerView.ViewHolder {
            TextView txtbyatcnt, date;
            ImageView img_checkDetails;
            LinearLayout lyt_checkDetails;

            public ShowDataViewHolder(View itemView) {
                super(itemView);
                txtbyatcnt = itemView.findViewById(R.id.txtbyatcnt);
                date = itemView.findViewById(R.id.date);
                img_checkDetails = itemView.findViewById(R.id.img_checkDetails);
                lyt_checkDetails = itemView.findViewById(R.id.lyt_checkDetails);
            }
        }
    }
}