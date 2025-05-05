package com.tarbar.kisan.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.tarbar.kisan.R;

import java.util.List;
import java.util.Map;

public class PashuVyapariList_Adapter extends RecyclerView.Adapter<PashuVyapariList_Adapter.ViewHolder> {
    private List<Map<String, String>> data;
    private Context context;

    public PashuVyapariList_Adapter(List<Map<String, String>> data, Context context) {
        this.data = data;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_pashuvyapari_list, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Map<String, String> animalInfo = data.get(position);

        holder.tvKisanMobileNumber.setText(getValidValue(animalInfo.get("sellerNumber")));
        holder.tvDate.setText(getValidValue(animalInfo.get("sellingDate")));

        String animalType = getValidValue(animalInfo.get("pashuType"));
            if (animalType.equals(holder.itemView.getContext().getString(R.string.str_cow))) {
                holder.tvType.setImageResource(R.drawable.cow_img);
            } else if (animalType.equals(holder.itemView.getContext().getString(R.string.str_buffalo))) {
                holder.tvType.setImageResource(R.drawable.buffalo_img);
            }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void updateList(List<Map<String, String>> newData) {
        this.data = newData;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate,tvKisanMobileNumber;
        ImageView tvType;

        public ViewHolder(View itemView) {
            super(itemView);
            tvKisanMobileNumber = itemView.findViewById(R.id.tvKisanMobileNumber);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvType = itemView.findViewById(R.id.tvType);
        }
    }

    private String getValidValue(String value) {
        return (value == null || value.trim().isEmpty()) ? "-" : value;
    }
}
