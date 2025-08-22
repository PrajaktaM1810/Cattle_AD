package com.tarbar.kisan.Adapter;

import static com.tarbar.kisan.Helper.constant.GET_GARBHAVATI_LIST;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.tarbar.kisan.Activities.LoadFilterFragments;
import com.tarbar.kisan.Helper.ApiUtils;
import com.tarbar.kisan.Helper.Iconstant;
import com.tarbar.kisan.Helper.SharedPreferenceManager;
import com.tarbar.kisan.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Pashu_Garbhavati_List_Adapter extends RecyclerView.Adapter<Pashu_Garbhavati_List_Adapter.PashuViewHolder> {

    // Data and Context
    private List<Map<String, String>> animalData;
    private Context context;
    private List<Map<String, String>> dataList = new ArrayList<>();

    // Helpers and Managers
    private SharedPreferenceManager sharedPrefMgr;
    private ProgressDialog dialog;

    // State Variables
    private String mobileNumber, pashuId, animalnumber, animaltype, KisanNumber;
    private String animalBlock, animalOff, milkAdditionDate, pashu_garbhavati_date;

    public Pashu_Garbhavati_List_Adapter(Context context, List<Map<String, String>> animalData) {
        this.context = context;
        this.animalData = animalData;
        initializeSharedPreferences();
    }

    private void initializeSharedPreferences() {
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

        initializeAnimalData(animalInfo);
        setupAnimalNumber(holder, animalInfo);
        setupAnimalType(holder);
        setupDateDisplay(holder, animalInfo);
        setupAddDateVisibility(holder);
        setupClickListeners(holder, animalInfo);
    }

    private void initializeAnimalData(Map<String, String> animalInfo) {
        pashuId = animalInfo.get("animalId");
        animalnumber = animalInfo.get("animalNo");
        animaltype = animalInfo.get("type");
        KisanNumber = animalInfo.get("kisanNumber");
        pashu_garbhavati_date = animalInfo.get("pashu_garbhavati_date");
        Log.d("CheckParams", "" + KisanNumber);
    }

    private void setupAnimalNumber(PashuViewHolder holder, Map<String, String> animalInfo) {
        String formattedAnimalNo = animalnumber.substring(0, 6) + "\n" + animalnumber.substring(6);
        holder.tvAnimalNo.setText(formattedAnimalNo);
    }

    private void setupAnimalType(PashuViewHolder holder) {
        if (animaltype != null) {
            if (animaltype.equals(holder.itemView.getContext().getString(R.string.str_cow))) {
                holder.imgType.setImageResource(R.drawable.cow_img);
            } else if (animaltype.equals(holder.itemView.getContext().getString(R.string.str_buffalo))) {
                holder.imgType.setImageResource(R.drawable.buffalo_img);
            }
        }
    }

    private void setupDateDisplay(PashuViewHolder holder, Map<String, String> animalInfo) {
        if (pashu_garbhavati_date.equals("null") || pashu_garbhavati_date.isEmpty()) {
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
    }

    private void setupAddDateVisibility(PashuViewHolder holder) {
        if (!KisanNumber.equals(mobileNumber)) {
            Log.d("CheckKK", "KisanNumber is not equal to mobileNumber");
            holder.tvAddDate.setVisibility(View.GONE);
            holder.tvAddDateShadow.setVisibility(View.VISIBLE);
        } else if (KisanNumber.equals(mobileNumber)) {
            Log.d("CheckKK", "KisanNumber is equal to mobileNumber");
            showAddData(holder.tvAddDate, holder.tvAddDateShadow);
        }
    }

    private void setupClickListeners(PashuViewHolder holder, Map<String, String> animalInfo) {
        holder.tvAddDate.setOnClickListener(v -> handleAddDateClick(v, animalInfo));
        holder.dateLayout.setOnClickListener(v -> handleDateLayoutClick(v, animalInfo));
        holder.tvDate.setOnClickListener(v -> handleDateLayoutClick(v, animalInfo));
        holder.ivIcon.setOnClickListener(v -> handleDateLayoutClick(v, animalInfo));
    }

    private void handleAddDateClick(View v, Map<String, String> animalInfo) {
        Animation animation = AnimationUtils.loadAnimation(v.getContext(), R.anim.click_animation);
        v.startAnimation(animation);

        if (animalBlock != null && animalBlock.equals("1")) {
            showBlockedAnimalDialog(v.getContext());
        } else {
            navigateToGarbhavatiForm(animalInfo, v.getContext());
        }
    }

    private void handleDateLayoutClick(View v, Map<String, String> animalInfo) {
        Animation animation = AnimationUtils.loadAnimation(v.getContext(), R.anim.click_animation);
        v.startAnimation(animation);

        if ("-".equals(animalInfo.get("pashu_garbhavati_date"))) {
            handleNoDateCase(v.getContext(), animalInfo);
        } else {
            String animalId = animalInfo.get("animalId");
            String animalNumber = animalInfo.get("animalNo");
            String animalType = animalInfo.get("type");
            dateListData(animalId, animalNumber, animalType);
        }
    }

    private void handleNoDateCase(Context context, Map<String, String> animalInfo) {
        if (mobileNumber.equals(animalInfo.get("kisanNumber"))) {
            if (pashu_garbhavati_date != null && !pashu_garbhavati_date.isEmpty() && !pashu_garbhavati_date.equals("null")) {
                showDataNotFoundDialog(context);
            } else {
                showAddDataDialog(context);
            }
        } else {
            showDataNotFoundDialog(context);
        }
    }

    private void showBlockedAnimalDialog(Context context) {
        new AlertDialog.Builder(context)
                .setMessage(R.string.msg_animal_blocked)
                .setPositiveButton(R.string.ok, (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void showDataNotFoundDialog(Context context) {
        new AlertDialog.Builder(context)
                .setMessage(R.string.str_data_not_found)
                .setPositiveButton(R.string.ok, (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void showAddDataDialog(Context context) {
        new AlertDialog.Builder(context)
                .setMessage(R.string.msg_add_data)
                .setPositiveButton(R.string.ok, (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void navigateToGarbhavatiForm(Map<String, String> animalInfo, Context context) {
        String UserId = animalInfo.get("userId");
        String KisanMobileNumber = animalInfo.get("kisanNumber");
        String animalId = animalInfo.get("animalId");
        String animalNumber = animalInfo.get("animalNo");
        String animalType = animalInfo.get("type");

        Log.d("AnimalInfo", "animalId: " + animalId);
        Log.d("AnimalInfo", "animalnumber: " + animalNumber);
        Log.d("AnimalInfo", "animaltype: " + animalType);

        Intent intent = new Intent(context, LoadFilterFragments.class);
        intent.putExtra("GarbhavatiForm_fragment", true);
        intent.putExtra("userId", UserId);
        intent.putExtra("kisanmobileNumber", KisanMobileNumber);
        intent.putExtra("animalId", animalId);
        intent.putExtra("animalNumber", animalNumber);
        intent.putExtra("animalType", animalType);
        context.startActivity(intent);
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
        showProgressDialog();

        String url = GET_GARBHAVATI_LIST;
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                response -> handleDateListResponse(response, PashuNumber, PashuType),
                this::handleVolleyError) {
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

    private void showProgressDialog() {
        dialog = new ProgressDialog(context);
        dialog.setMessage(context.getString(R.string.getting_data));
        dialog.setCancelable(false);
        dialog.show();
    }

    private void handleDateListResponse(String response, String PashuNumber, String PashuType) {
        try {
            dialog.dismiss();
            JSONObject jsonObject = new JSONObject(response);
            String status = jsonObject.getString("status");

            if (status.equals("success")) {
                processSuccessResponse(jsonObject, PashuNumber, PashuType);
            } else if (status.equals("error")) {
                String message = jsonObject.getString("message");
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            handleJsonException(e);
        }
    }

    private void processSuccessResponse(JSONObject jsonObject, String PashuNumber, String PashuType) throws JSONException {
        JSONArray dataArray = jsonObject.getJSONArray("data");
        dataList.clear();

        for (int i = 0; i < dataArray.length(); i++) {
            JSONObject data = dataArray.getJSONObject(i);
            Map<String, String> bijdanData = createBijdanDataMap(data);
            dataList.add(bijdanData);
        }

        String totalCountLast301Days = jsonObject.getString("total_count_last_301_days");
        showRecyclerViewDialog(context, PashuNumber, PashuType, totalCountLast301Days);
    }

    private Map<String, String> createBijdanDataMap(JSONObject data) throws JSONException {
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
        return bijdanData;
    }

    private void handleJsonException(JSONException e) {
        dialog.dismiss();
        Log.d("JSONException", "" + e);
        Toast.makeText(context, R.string.json_parsing_error, Toast.LENGTH_SHORT).show();
    }

    private void handleVolleyError(VolleyError error) {
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

    public void updateList(List<Map<String, String>> newAnimalData) {
        if (animalData != null) {
            animalData.clear();
            animalData.addAll(newAnimalData);
        } else {
            animalData = new ArrayList<>(newAnimalData);
        }
        notifyDataSetChanged();
    }

    private void showRecyclerViewDialog(Context context, String animalNumber, String PashuType, String totalCountLast301Days) {
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
        ImageView imgType, tvAddDate, tvAddDateShadow, ivIcon;
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
            setupBijdanData(holder, item);
            setupClickListeners(holder, item);
        }

        private void setupBijdanData(ShowDataViewHolder holder, Map<String, String> item) {
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
        }

        private void setupClickListeners(ShowDataViewHolder holder, Map<String, String> item) {
            String userNumber = "7058143404";

            holder.img_checkDetails.setOnClickListener(v ->
                    navigateToGarbhavatiFormUpdate(item, userNumber, holder.itemView.getContext()));

            holder.lyt_checkDetails.setOnClickListener(v -> {
                Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.click_animation);
                v.startAnimation(animation);
                navigateToGarbhavatiFormUpdate(item, userNumber, holder.itemView.getContext());
            });
        }

        private void navigateToGarbhavatiFormUpdate(Map<String, String> item, String userNumber, Context context) {
            Intent intent = new Intent(context, LoadFilterFragments.class);
            intent.putExtra("GarbhavatiForm_fragment_update", true);
            intent.putExtra("bijdanId", item.get("id"));
            intent.putExtra("kisanmobileNumber", userNumber);
            context.startActivity(intent);
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        public class ShowDataViewHolder extends RecyclerView.ViewHolder {
            TextView txtBullNumber, date;
            ImageView img_checkDetails;
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