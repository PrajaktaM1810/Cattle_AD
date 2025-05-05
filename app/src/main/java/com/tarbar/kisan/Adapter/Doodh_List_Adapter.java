package com.tarbar.kisan.Adapter;

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

public class Doodh_List_Adapter extends RecyclerView.Adapter<Doodh_List_Adapter.PashuViewHolder> {
    private List<Map<String, String>> animalData;
    String pashuId;
    ProgressDialog dialog;
    private Context context;
    private List<Map<String, String>> doodhList = new ArrayList<>();
    private SharedPreferenceManager sharedPrefMgr;
    String mobileNumber, KisanNumber,animalnumber,animaltype,byatcnt,animalBlock,animalOff,milkAdditionDate, milkData,latest_byat_registration;
    public Doodh_List_Adapter(Context context, List<Map<String, String>> animalData) {
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_doodh_list, parent, false);
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
        milkData = animalInfo.get("MilkData");
        if (milkData.equals("NULL") || milkData.equals("Null") || milkData.equals("null")  || milkData.isEmpty()) {
            holder.tvDoodh.setText("0");
        } else {
            holder.tvDoodh.setText(animalInfo.get("MilkData"));
        }
        animaltype = animalInfo.get("type");
        KisanNumber = animalInfo.get("kisanNumber");
        latest_byat_registration = animalInfo.get("lastbyat");
        String latest_milk_registration = animalInfo.get("lastMilk");
        Log.d("LatestByatRegistration", "Value: " + latest_byat_registration);
        Log.d("LatestMilkRegistration", "Value: " + latest_milk_registration);
        Log.d("CheckParams",""+latest_milk_registration);
        Log.d("CheckParams",""+KisanNumber);

        if(!KisanNumber.equals(mobileNumber)){
            holder.tvAddDoodh.setVisibility(View.GONE);
            holder.tvAddDoodhShadow.setVisibility(View.VISIBLE);
        } else if(KisanNumber.equals(mobileNumber)) {
            if(milkData.equals("0.000")) {
//                showAddByaat(holder.tvAddDoodh, holder.tvAddDoodhShadow);
                handleDateLogic(latest_byat_registration,latest_milk_registration, holder.tvAddDoodh, holder.tvAddDoodhShadow, KisanNumber);
            } else {
                handleDateLogic(latest_byat_registration,latest_milk_registration, holder.tvAddDoodh, holder.tvAddDoodhShadow, KisanNumber);
            }
        }

        if (animaltype != null) {
            if (animaltype.equals(holder.itemView.getContext().getString(R.string.str_cow))) {
                holder.imgType.setImageResource(R.drawable.cow_img);
            } else if (animaltype.equals(holder.itemView.getContext().getString(R.string.str_buffalo))) {
                holder.imgType.setImageResource(R.drawable.buffalo_img);
            }
        }

//        Add Byat form

        holder.tvAddDoodh.setOnClickListener(v -> {
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
//                String kisanNo = animalInfo.get("kisanNumber");
//                String Last_Date = animalInfo.get("latest_byat");
//                String animal_birth_date = animalInfo.get("date");
                animalnumber = animalInfo.get("animalNo");
                animaltype = animalInfo.get("type");
                byatcnt = animalInfo.get("byaatCount");
                milkAdditionDate = animalInfo.get("lastMilk");

                Log.d("MilkInfo", "KisanId: " + KisanId);
                Log.d("MilkInfo", "animalId: " + animalId);
                Log.d("MilkInfo", "animalnumber: " + animalnumber);
                Log.d("MilkInfo", "animaltype: " + animaltype);
                Log.d("MilkInfo", "byatcount: " + byatcnt);
                Log.d("MilkInfo", "milkAdditionDate: " + milkAdditionDate);

                Intent intent = new Intent(holder.itemView.getContext(), LoadFragments.class);
                intent.putExtra("Fragment_AddDoodh", true);
                intent.putExtra("KisanId", KisanId);
                intent.putExtra("animalId", animalId);
                intent.putExtra("animalNumber", animalnumber);
                intent.putExtra("animalType", animaltype);
                intent.putExtra("byatcnt", byatcnt);
                intent.putExtra("lastMilk", milkAdditionDate);
                holder.itemView.getContext().startActivity(intent);
            }
        });

        holder.doodhLayout.setOnClickListener(v -> handleDoodhClick(v, animalInfo));
        holder.tvDoodh.setOnClickListener(v -> handleDoodhClick(v, animalInfo));
        holder.ivIcon.setOnClickListener(v -> handleDoodhClick(v, animalInfo));
    }

    private void handleDoodhClick(View v, Map<String, String> animalInfo) {
        Animation animation = AnimationUtils.loadAnimation(v.getContext(), R.anim.click_animation);
        v.startAnimation(animation);
//        if (animalBlock != null && animalBlock.equals("1")) {
//            new AlertDialog.Builder(holder.itemView.getContext())
//                    .setMessage(R.string.msg_animal_blocked)
//                    .setPositiveButton(R.string.ok, (dialog, which) -> dialog.dismiss())
//                    .show();
//        } else {
            if ("0.000".equals(animalInfo.get("MilkData"))) {
                    if (mobileNumber.equals(animalInfo.get("kisanNumber"))) {
                        if (latest_byat_registration != null && !latest_byat_registration.isEmpty() && !latest_byat_registration.equals("null")) {
                            new AlertDialog.Builder(v.getContext())
                                    .setMessage(R.string.str_data_not_found)
                                    .setPositiveButton(R.string.ok, (dialog, which) -> dialog.dismiss())
                                    .show();
                        } else {
                            new AlertDialog.Builder(v.getContext())
                                    .setMessage(R.string.msg_add_byat)
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
                doodhListData(animalId,animalNumber);
            }
//        }
    }

    @Override
    public int getItemCount() {
        return animalData.size();
    }

    private void handleDateLogic(String bytaAdditionDate, String dateString, View tvAddByaat, View tvAddByaatShadow, String kisannumber) {
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

//    private void handleDateLogic(String bytaAdditionDate, String dateString, View tvAddByaat, View tvAddByaatShadow, String kisannumber) {
//        Log.d("handleDateLogic", "Received dateString: " + dateString + ", bytaAdditionDate: '" + bytaAdditionDate + "'");
//
//        if (bytaAdditionDate != null && !bytaAdditionDate.isEmpty() && !bytaAdditionDate.equals("null")) {
//            Date bytaAdditionDateParsed = parseDate(bytaAdditionDate);
//            if (bytaAdditionDateParsed != null) {
//                Calendar bytaAdditionCalendar = Calendar.getInstance();
//                bytaAdditionCalendar.setTime(bytaAdditionDateParsed);
//                bytaAdditionCalendar.set(Calendar.HOUR_OF_DAY, 0);
//                bytaAdditionCalendar.set(Calendar.MINUTE, 0);
//                bytaAdditionCalendar.set(Calendar.SECOND, 0);
//                bytaAdditionCalendar.set(Calendar.MILLISECOND, 0);
//
//                Calendar bytaAdditionPlus301 = (Calendar) bytaAdditionCalendar.clone();
//                bytaAdditionPlus301.add(Calendar.DAY_OF_YEAR, 301);
//
//                Calendar today = Calendar.getInstance();
//                today.set(Calendar.HOUR_OF_DAY, 0);
//                today.set(Calendar.MINUTE, 0);
//                today.set(Calendar.SECOND, 0);
//                today.set(Calendar.MILLISECOND, 0);
//
//                Log.d("handleDateLogic", "bytaAdditionDate: " + bytaAdditionCalendar.getTime());
//                Log.d("handleDateLogic", "bytaAdditionDate + 301 days: " + bytaAdditionPlus301.getTime());
//                Log.d("handleDateLogic", "Today's date: " + today.getTime());
//
//                if (today.getTime().after(bytaAdditionCalendar.getTime()) && today.getTime().before(bytaAdditionPlus301.getTime())) {
//                    // Process dateString logic only if today is within the range
//                    if (dateString == null) {
//                        Log.d("handleDateLogic", "dateString is null. Showing AddByaat.");
//                        showAddByaat(tvAddByaat, tvAddByaatShadow);
//                        return;
//                    }
//
//                    Date DateString = parseDate(dateString);
//                    if (DateString == null) {
//                        Log.d("handleDateLogic", "BirthDate parsing returned null. Showing AddByaat.");
//                        showAddByaat(tvAddByaat, tvAddByaatShadow);
//                        return;
//                    }
//
//                    Log.d("handleDateLogic", "Parsed DateString: " + DateString);
//
//                    Calendar birthCalendar = Calendar.getInstance();
//                    birthCalendar.setTime(DateString);
//                    birthCalendar.set(Calendar.HOUR_OF_DAY, 0);
//                    birthCalendar.set(Calendar.MINUTE, 0);
//                    birthCalendar.set(Calendar.SECOND, 0);
//                    birthCalendar.set(Calendar.MILLISECOND, 0);
//
//                    if (birthCalendar.getTime().equals(today.getTime())) {
//                        Log.d("handleDateLogic", "BirthDate is equal to today. Showing AddByaatShadow.");
//                        showAddByaatShadow(tvAddByaat, tvAddByaatShadow);
//                        return;
//                    }
//
//                    Calendar DateStringPlus301Days = (Calendar) birthCalendar.clone();
//                    DateStringPlus301Days.add(Calendar.DAY_OF_YEAR, 301);
//                    Log.d("handleDateLogic", "BirthDate + 301 days: " + DateStringPlus301Days.getTime());
//
//                    Log.d("KisanNumber", "" + kisannumber);
//
//                    if (kisannumber.equals(mobileNumber) && today.getTime().after(DateStringPlus301Days.getTime())) {
//                        Log.d("handleDateLogic", "Today's date is after BirthDate + 301 days. Showing AddByaat.");
//                        showAddByaat(tvAddByaat, tvAddByaatShadow);
//                    } else {
//                        Log.d("handleDateLogic", "Today's date is before BirthDate + 301 days. Showing AddByaatShadow.");
//                        showAddByaatShadow(tvAddByaat, tvAddByaatShadow);
//                    }
//                } else {
//                    Log.d("handleDateLogic", "Today's date is outside the range of bytaAdditionDate and +301 days. No action.");
//                    showAddByaatShadow(tvAddByaat, tvAddByaatShadow);
//                }
//            }
//        } else {
//            Log.d("handleDateLogic", "bytaAdditionDate is null or empty. Showing AddByaat directly.");
//            showAddByaatShadow(tvAddByaat, tvAddByaatShadow);
//        }
//    }

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

    private void doodhListData(String PashuId, String PashuNumber) {
        dialog = new ProgressDialog(context);
        dialog.setMessage(context.getString(R.string.getting_data));
        dialog.setCancelable(false);
        dialog.show();
        String url = GET_DOODH_LIST;
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
                                doodhList.clear();
                                for (int i = 0; i < dataArray.length(); i++) {
                                    JSONObject data = dataArray.getJSONObject(i);
                                    Map<String, String> doodhData = new HashMap<>();
                                    doodhData.put("id", data.getString("id"));
                                    doodhData.put("userid", data.getString("userid"));
                                    doodhData.put("pashu_id", data.getString("pashu_id"));
                                    doodhData.put("pashu_doodh", data.getString("pashu_doodh"));
                                    doodhData.put("pashu_pita", data.getString("pashu_pita"));
                                    doodhData.put("date", data.getString("date"));
                                    doodhData.put("created_at", data.getString("created_at"));
                                    doodhList.add(doodhData);
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
        View dialogView = LayoutInflater.from(context).inflate(R.layout.layout_show_doodh_data, null);
        TextView PashuNumber = dialogView.findViewById(R.id.PashuNumber);
        PashuNumber.setText(context.getString(R.string.title_pashu_number) + " : " + animalNumber);
        Collections.reverse(doodhList);
        RecyclerView recyclerView = dialogView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        ShowData_Adapter adapter = new ShowData_Adapter(doodhList);
        recyclerView.setAdapter(adapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);
        builder.setPositiveButton(R.string.cancel, null);
        builder.show();
    }

    public static class PashuViewHolder extends RecyclerView.ViewHolder {
        TextView tvAnimalNo, tvDoodh;
        ImageView imgType, tvAddDoodh, tvAddDoodhShadow,ivIcon;
        LinearLayout lnrAddDoodh, doodhLayout;

        public PashuViewHolder(View itemView) {
            super(itemView);
            tvAnimalNo = itemView.findViewById(R.id.tvAnimalNo);
            imgType = itemView.findViewById(R.id.tvType);
            tvAddDoodh = itemView.findViewById(R.id.tvAddDoodh);
            tvDoodh = itemView.findViewById(R.id.tvDoodh);
            ivIcon = itemView.findViewById(R.id.ivIcon);
            lnrAddDoodh = itemView.findViewById(R.id.lnrAddDoodh);
            doodhLayout = itemView.findViewById(R.id.doodhLayout);
            tvAddDoodhShadow = itemView.findViewById(R.id.tvAddDoodhShadow);
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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_show_doodh_data, parent, false);
            return new ShowDataViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ShowDataViewHolder holder, int position) {
            Map<String, String> item = items.get(position);

            String DoodhKilo = item.get("pashu_doodh");
//            double kilo = Double.parseDouble(DoodhKilo);
//            String intDoodhKilo = String.valueOf((int) kilo);

            holder.txtdoodhcnt.setText(DoodhKilo + " किलो");
            String originalDate = item.get("date");

            final String[] formattedDate = {""};
            try {
                SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date date = originalFormat.parse(originalDate);
                SimpleDateFormat desiredFormat = new SimpleDateFormat("dd/MM/yyyy");
                formattedDate[0] = desiredFormat.format(date);
                holder.date.setText(formattedDate[0]);
            } catch (ParseException e) {
                e.printStackTrace();
                holder.date.setText("");
            }

            if(!KisanNumber.equals(mobileNumber)) {
                if (position == 0) {
                    holder.img_whatsapp.setVisibility(View.VISIBLE);
                } else {
                    holder.img_whatsapp.setVisibility(View.GONE);
                }
            } else {
                holder.img_whatsapp.setVisibility(View.GONE);
            }

            holder.img_whatsapp.setOnClickListener(v -> {
                Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.click_animation);
                v.startAnimation(animation);
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(context.getString(R.string.confirm_delete_doodh))
                        .setNegativeButton(context.getString(R.string.no), null)
                        .setPositiveButton(context.getString(R.string.yes), (dialog, which) -> {
                            dialog.dismiss();
                            messageDialog(DoodhKilo, formattedDate[0], animalnumber);
                        });
                AlertDialog alert = builder.create();
                alert.show();
                alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
                alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
            });

        /////////////////////////////////
            holder.img_checkDetails.setOnClickListener(v -> {
                Intent intent = new Intent(holder.itemView.getContext(), LoadFragments.class);
                intent.putExtra("Fragment_DoodhProfile", true);
                intent.putExtra("KisanId", item.get("userid"));
                intent.putExtra("animalId", item.get("pashu_id"));
                intent.putExtra("animalNumber",animalnumber);
                intent.putExtra("animalType",animaltype);
                intent.putExtra("byatcnt",byatcnt);
                intent.putExtra("doodhId", item.get("id"));
                intent.putExtra("lastMilk", milkAdditionDate);
                holder.itemView.getContext().startActivity(intent);
            });

            holder.lyt_checkDetails.setOnClickListener(v -> {
                Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.click_animation);
                v.startAnimation(animation);
                Intent intent = new Intent(holder.itemView.getContext(), LoadFragments.class);
                intent.putExtra("Fragment_DoodhProfile", true);
                intent.putExtra("KisanId", item.get("userid"));
                intent.putExtra("animalId", item.get("pashu_id"));
                intent.putExtra("animalNumber",animalnumber);
                intent.putExtra("animalType",animaltype);
                intent.putExtra("byatcnt",byatcnt);
                intent.putExtra("doodhId", item.get("id"));
                intent.putExtra("lastMilk", milkAdditionDate);
                holder.itemView.getContext().startActivity(intent);
            });
        }

        private void messageDialog(String doodhKilo, String Date, String animalnumber) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            LayoutInflater inflater = LayoutInflater.from(context);
            View dialogView = inflater.inflate(R.layout.lyt_forget_password, null);
            builder.setView(dialogView);
            AlertDialog dialog = builder.create();

            EditText passwordEditText = dialogView.findViewById(R.id.Password);
            TextView textGetPassword = dialogView.findViewById(R.id.textGetPassword);
            LinearLayout passwordLayout = dialogView.findViewById(R.id.password_layout);
            LinearLayout mobileLayout = dialogView.findViewById(R.id.mobileLayout);
            LinearLayout pashuNumber = dialogView.findViewById(R.id.pashuNumber);

            passwordLayout.setVisibility(View.VISIBLE);
            mobileLayout.setVisibility(View.GONE);
            pashuNumber.setVisibility(View.GONE);

            textGetPassword.setText(R.string.ok);

            dialogView.findViewById(R.id.textGetPassword).setOnClickListener(v -> {
                String pass = passwordEditText.getText().toString();

                if (pass.isEmpty()) {
                    passwordEditText.setError(context.getString(R.string.enter_password_number));
                } else if (pass.length() < 4) {
                    passwordEditText.setError(context.getString(R.string.enter_correct_password));
                } else if (!pass.equals(pass)) {
                    Toast.makeText(context, R.string.error_password_not_matching, Toast.LENGTH_SHORT).show();
                } else {
                    dialog.dismiss();
                    String phoneNumber = constant.tarbar_enquiry_number;
                    String message = context.getString(R.string.title_pashu_number) + " : " + animalnumber + "\n" +
                            "किलो : " + doodhKilo + " Kg\n" +
                            "तारीख : " + Date + "\n" +
                            "यह दूध की जानकारी गलत है | \n\n" +
                            " ~ रिपोर्ट की : " + mobileNumber;
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse("https://wa.me/" + phoneNumber + "?text=" + Uri.encode(message)));
                        intent.putExtra(Intent.EXTRA_REFERRER, Uri.parse("android-app://com.whatsapp"));
                        context.startActivity(intent);
                        dialog.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(context, "Error opening WhatsApp", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            dialogView.findViewById(R.id.textCancel).setOnClickListener(v -> dialog.dismiss());
            dialog.show();
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        public class ShowDataViewHolder extends RecyclerView.ViewHolder {
            TextView txtdoodhcnt, date;
            ImageView img_checkDetails,img_whatsapp;
            LinearLayout lyt_checkDetails;

            public ShowDataViewHolder(View itemView) {
                super(itemView);
                txtdoodhcnt = itemView.findViewById(R.id.txtdoodhcnt);
                date = itemView.findViewById(R.id.date);
                img_checkDetails = itemView.findViewById(R.id.img_checkDetails);
                img_whatsapp = itemView.findViewById(R.id.img_whatsapp);
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