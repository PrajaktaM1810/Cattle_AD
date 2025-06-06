package com.tarbar.kisan.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.tarbar.kisan.Activities.LoadFormFragments;
import com.tarbar.kisan.R;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Selling_List_Adapter extends RecyclerView.Adapter<Selling_List_Adapter.PashuViewHolder> {
    private List<Map<String, String>> bullData;
    private final Context context;

    public Selling_List_Adapter(Context context, List<Map<String, String>> bullData) {
        this.context = context;
        this.bullData = new ArrayList<>(bullData);
    }

    @NonNull
    @Override
    public PashuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_selling_list, parent, false);
        return new PashuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PashuViewHolder holder, int position) {
        Map<String, String> animalInfo = bullData.get(position);

        String sellingId = animalInfo.get("id");
        String sellerNumber = animalInfo.get("seller_number");
        String sellingDate = animalInfo.get("selling_date");
        String pashuType = animalInfo.get("pashu_type");

        holder.SellerMobileNumber.setText(getValueOrDash(sellerNumber));

        if (sellingDate == null || sellingDate.trim().isEmpty() || sellingDate.equals("0000-00-00")) {
            holder.SellingDate.setText("-");
        } else {
            holder.SellingDate.setText(sellingDate);
        }

        if (pashuType != null) {
            if (pashuType.contains("गाय")) {
                holder.tvType.setImageResource(R.drawable.cow_img);
            } else if (pashuType.contains("भैंस")) {
                holder.tvType.setImageResource(R.drawable.buffalo_img);
            }
        }

        holder.SellerMobileNumber.setOnClickListener(v -> {
            Intent i = new Intent(context, LoadFormFragments.class);
            i.putExtra("PashuUpdateEditForm_fragment", true);
            i.putExtra("selling_id", sellingId);
            context.startActivity(i);
        });

        holder.ivIcon.setOnClickListener(v -> {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.click_animation);
            v.startAnimation(animation);
            Intent i = new Intent(context, LoadFormFragments.class);
            i.putExtra("PashuUpdateEditForm_fragment", true);
            i.putExtra("selling_id", sellingId);
            context.startActivity(i);
        });
    }

    private String getValueOrDash(String value) {
        return (value == null || value.trim().isEmpty() || value.equalsIgnoreCase("null")) ? "-" : value;
    }

    @Override
    public int getItemCount() {
        return bullData.size();
    }

    public void updateList(List<Map<String, String>> newBullData) {
        this.bullData = new ArrayList<>(newBullData);
        notifyDataSetChanged();
    }

    public static class PashuViewHolder extends RecyclerView.ViewHolder {
        TextView SellerMobileNumber;
        TextView SellingDate;
        ImageView tvType, ivIcon;

        public PashuViewHolder(View itemView) {
            super(itemView);
            SellerMobileNumber = itemView.findViewById(R.id.SellerMobileNumber);
            SellingDate = itemView.findViewById(R.id.SellingDate);
            tvType = itemView.findViewById(R.id.tvType);
            ivIcon = itemView.findViewById(R.id.ivIcon);
        }
    }
}