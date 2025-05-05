package com.tarbar.kisan.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tarbar.kisan.R;

import java.util.List;

public class DateRangeAdapter extends RecyclerView.Adapter<DateRangeAdapter.DateRangeViewHolder> {

    private final List<String> dateRanges;
    private OnItemSelectedListener onItemSelectedListener;
    private int selectedPosition = -1;

    public DateRangeAdapter(List<String> dateRanges) {
        this.dateRanges = dateRanges;
    }

    public void setOnItemSelectedListener(OnItemSelectedListener listener) {
        this.onItemSelectedListener = listener;
    }

    @NonNull
    @Override
    public DateRangeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_date_range, parent, false);
        return new DateRangeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DateRangeViewHolder holder, int position) {
        String dateRange = dateRanges.get(position);
        holder.dateRangeTextView.setText(dateRange);
        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setChecked(position == selectedPosition);

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                int oldPosition = selectedPosition;
                selectedPosition = position;

                if (oldPosition != -1) {
                    notifyItemChanged(oldPosition);
                }
                notifyItemChanged(selectedPosition);

                if (onItemSelectedListener != null) {
                    onItemSelectedListener.onItemSelected(dateRange);
                }
            } else if (selectedPosition == position) {
                selectedPosition = -1;
                notifyItemChanged(position);
            }
        });

        holder.itemView.setOnClickListener(v -> {
            if (selectedPosition != position) {
                int oldPosition = selectedPosition;
                selectedPosition = position;

                if (oldPosition != -1) {
                    notifyItemChanged(oldPosition);
                }
                notifyItemChanged(selectedPosition);

                if (onItemSelectedListener != null) {
                    onItemSelectedListener.onItemSelected(dateRange);
                }
            } else {
                int oldPosition = selectedPosition;
                selectedPosition = -1;

                if (oldPosition != -1) {
                    notifyItemChanged(oldPosition);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return dateRanges.size();
    }

    public String getSelectedDateRange() {
        if (selectedPosition != -1) {
            return dateRanges.get(selectedPosition);
        }
        return null;
    }

    public interface OnItemSelectedListener {
        void onItemSelected(String selectedDateRange);
    }

    static class DateRangeViewHolder extends RecyclerView.ViewHolder {

        TextView dateRangeTextView;
        CheckBox checkBox;

        public DateRangeViewHolder(@NonNull View itemView) {
            super(itemView);
            dateRangeTextView = itemView.findViewById(R.id.dateRangeTextView);
            checkBox = itemView.findViewById(R.id.checkBox);
        }
    }
}
