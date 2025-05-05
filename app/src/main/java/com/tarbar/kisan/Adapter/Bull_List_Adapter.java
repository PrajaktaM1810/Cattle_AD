package com.tarbar.kisan.Adapter;

import static com.tarbar.kisan.Helper.constant.BULL_IMAGES_PATH;

import android.content.Context;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.tarbar.kisan.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.util.Log;

public class Bull_List_Adapter extends RecyclerView.Adapter<Bull_List_Adapter.PashuViewHolder> {
    private List<Map<String, String>> bullData;
    private Context context;
    public Bull_List_Adapter(Context context, List<Map<String, String>> bullData) {
        this.context = context;
        this.bullData = bullData;
    }

    @NonNull
    @Override
    public PashuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_bull_list, parent, false);
        return new PashuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PashuViewHolder holder, int position) {
        Map<String, String> animalInfo = bullData.get(position);
        Log.d("PashuListAdapter", "Animal Info: " + animalInfo.toString());

        String animalId = animalInfo.get("id");
        String bullTypeValue = animalInfo.get("bullType");

        String bullPicUrl = animalInfo.get("bullPic");
        if (bullPicUrl != null && !bullPicUrl.trim().isEmpty()) {
            bullPicUrl = BULL_IMAGES_PATH + bullPicUrl;
            Glide.with(context)
                    .load(bullPicUrl)
                    .fitCenter()
                    .into(holder.bullImage);
        } else {
//            holder.bullImage.setVisibility(View.GONE);
        }

        holder.bullNumber.setText(context.getString(R.string.title_bull_number) + " : " + getValueOrDash(animalInfo.get("animalNo")));
        holder.bullBirthDate.setText(context.getString(R.string.title_bull_dob) + " : " + getFormattedDate(animalInfo.get("dob")));
        holder.bullMotherNumber.setText(context.getString(R.string.title_mother_number) + " : " + getValueOrDash(animalInfo.get("bullMotherNumber")));
        holder.motherPerDayMilk.setText(context.getString(R.string.title_mother_per_day_milk) + " : " + getValueOrDash(animalInfo.get("mother1DayMilkQty")) + " किलो");
        holder.mother305daysMilk.setText(context.getString(R.string.title_mother_305_days_milk) + " : " + getValueOrDash(animalInfo.get("mother305DaysMilkQty")) + " किलो");
        String bullCasteValue = animalInfo.get("bullCaste");
        if (bullCasteValue == null || bullCasteValue.trim().isEmpty()) {
            holder.bullCaste.setVisibility(View.GONE);
        } else {
            holder.bullCaste.setText(bullCasteValue);
            holder.bullCaste.setVisibility(View.VISIBLE);
        }
        holder.secondPidhiNumber.setText(context.getString(R.string.title_pidhi2_number) + " : " + getValueOrDash(animalInfo.get("pidhiTwo")));
        holder.thirdPidhiNumber.setText(context.getString(R.string.title_pidhi3_number) + " : " + getValueOrDash(animalInfo.get("pidhiThree")));
        holder.forthPidhiNumber.setText(context.getString(R.string.title_pidhi4_number) + " : " + getValueOrDash(animalInfo.get("pidhiFour")));
        holder.fifthPidhiNumber.setText(context.getString(R.string.title_pidhi5_number) + " : " + getValueOrDash(animalInfo.get("pidhiFive")));
        holder.sixthPidhiNumber.setText(context.getString(R.string.title_pidhi6_number) + " : " + getValueOrDash(animalInfo.get("pidhiSix")));
    }

    private String getFormattedDate(String date) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date parsedDate = inputFormat.parse(date);
            return outputFormat.format(parsedDate);
        } catch (Exception e) {
            e.printStackTrace();
            return "-";
        }
    }


    private String getValueOrDash(String value) {
        return (value == null || value.trim().isEmpty() || value.equalsIgnoreCase("null")) ? "-" : value;
    }

    @Override
    public int getItemCount() {
        return bullData.size();
    }

    public void updateList(List<Map<String, String>> newbullData) {
        if (bullData != null) {
            bullData.clear();
            bullData.addAll(newbullData);
        } else {
            bullData = new ArrayList<>(newbullData);
        }
        notifyDataSetChanged();
    }

    public static class PashuViewHolder extends RecyclerView.ViewHolder {
        ImageView bullImage;
        TextView bullNumber;
        TextView bullBirthDate;
        TextView bullCaste;
        TextView bullMotherNumber;
        TextView motherPerDayMilk;
        TextView mother305daysMilk;
        TextView secondPidhiNumber;
        TextView thirdPidhiNumber;
        TextView forthPidhiNumber;
        TextView fifthPidhiNumber;
        TextView sixthPidhiNumber;

        public PashuViewHolder(View itemView) {
            super(itemView);
            bullCaste = itemView.findViewById(R.id.bullCaste);
            bullImage = itemView.findViewById(R.id.bullImage);
            bullNumber = itemView.findViewById(R.id.bullNumber);
            bullBirthDate = itemView.findViewById(R.id.bullBirthDate);
            bullMotherNumber = itemView.findViewById(R.id.bullMotherNumber);
            motherPerDayMilk = itemView.findViewById(R.id.motherPerDayMilk);
            mother305daysMilk = itemView.findViewById(R.id.mother305daysMilk);
            secondPidhiNumber = itemView.findViewById(R.id.secondPidhiNumber);
            thirdPidhiNumber = itemView.findViewById(R.id.thirdPidhiNumber);
            forthPidhiNumber = itemView.findViewById(R.id.forthPidhiNumber);
            fifthPidhiNumber = itemView.findViewById(R.id.fifthPidhiNumber);
            sixthPidhiNumber = itemView.findViewById(R.id.sixthPidhiNumber);
        }
    }

}