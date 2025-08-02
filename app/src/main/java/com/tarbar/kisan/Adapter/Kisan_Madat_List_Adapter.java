package com.tarbar.kisan.Adapter;

import static com.tarbar.kisan.Helper.constant.PROFILE_IMGAE_PATH;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;
import com.tarbar.kisan.R;
import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import de.hdodenhof.circleimageview.CircleImageView;

public class Kisan_Madat_List_Adapter extends RecyclerView.Adapter<Kisan_Madat_List_Adapter.ViewHolder> {
    private List<Map<String, String>> data;
    private Context context;

    public Kisan_Madat_List_Adapter(List<Map<String, String>> data, Context context) {
        this.data = data;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_kisan_madat_list, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Map<String, String> animalInfo = data.get(position);
        String profilePic = animalInfo.get("profilePic");
        String imageUrl = PROFILE_IMGAE_PATH + profilePic;
        Picasso.get()
                .load(imageUrl)
                .into(holder.profilepicture, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        holder.profilepicture.setVisibility(View.VISIBLE);
                        holder.kisanImg.setVisibility(View.GONE);
                        Bitmap bitmap = ((BitmapDrawable) holder.profilepicture.getDrawable()).getBitmap();
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                        holder.imageData = byteArrayOutputStream.toByteArray();
                        Log.d("ImageDataLength", "Length: " + holder.imageData.length);
                    }
                    @Override
                    public void onError(Exception e) {
                        holder.kisanImg.setVisibility(View.VISIBLE);
                        holder.profilepicture.setVisibility(View.GONE);
                    }
                });

        String name = animalInfo.get("name");
        String fullName = (isValid(name) ? name : "");
        fullName = fullName.trim();

        holder.txtName.setText(fullName);

        if (isValid(animalInfo.get("father_name"))) {
            holder.txtFatherName.setText(context.getString(R.string.title_father_name) + " : " + animalInfo.get("father_name"));
        }

        if (isValid(animalInfo.get("caste"))) {
            holder.txtCaste.setText(context.getString(R.string.title_caste) + " : " + animalInfo.get("caste"));
        }

        if (isValid(animalInfo.get("village"))) {
            holder.txtVillage.setText(context.getString(R.string.title_village) + " : " + animalInfo.get("village"));
        }

        if (isValid(animalInfo.get("tahsil"))) {
            holder.txtTahsil.setText(context.getString(R.string.title_tahsil) + " : " + animalInfo.get("tahsil"));
        }

        if (isValid(animalInfo.get("jilha"))) {
            holder.txtJilha.setText(context.getString(R.string.title_jilha) + " : " + animalInfo.get("jilha"));
        }

        if (isValid(animalInfo.get("state"))) {
            holder.txtState.setText(context.getString(R.string.title_state) + " : " + animalInfo.get("state"));
        }

        if (isValid(animalInfo.get("kisan_rank"))) {
            holder.txtRank.setText(context.getString(R.string.str_rank) + " : " + animalInfo.get("kisan_rank"));
        }

        if (isValid(animalInfo.get("kisanNumber"))) {
            holder.txtMobileNumber.setText(animalInfo.get("kisanNumber"));
        }

        if (isValid(animalInfo.get("kisan_help_money"))) {
            holder.txtAmount.setText(context.getString(R.string.title_rs) + " : " + animalInfo.get("kisan_help_money"));
        }

        if (isValid(animalInfo.get("kisan_help_money_date"))) {
            holder.txtDate.setText(context.getString(R.string.str_date) + " : " + animalInfo.get("kisan_help_money_date"));
        }

        String dateString = animalInfo.get("kisan_help_money_date");
        if (isValid(dateString)) {
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                Date date = inputFormat.parse(dateString);
                if (date != null) {
                    holder.txtDate.setText(context.getString(R.string.str_date) + " : " + outputFormat.format(date));
                }
            } catch (ParseException e) {
                e.printStackTrace();
                holder.txtDate.setText(context.getString(R.string.str_date) + " : " + dateString);
            }
        }
    }

    private boolean isValid(String value) {
        return value != null && !value.trim().equalsIgnoreCase("null") && !value.trim().isEmpty();
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
        TextView txtName,txtFatherName,txtCaste,txtVillage,txtTahsil,txtJilha,
                txtState,txtRank,txtMobileNumber,txtAmount,txtDate;
        CircleImageView profilepicture;
        ImageView kisanImg;
        private byte[] imageData;

        public ViewHolder(View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            txtFatherName = itemView.findViewById(R.id.txtFatherName);
            txtCaste = itemView.findViewById(R.id.txtCaste);
            txtVillage = itemView.findViewById(R.id.txtVillage);
            txtTahsil = itemView.findViewById(R.id.txtTahsil);
            txtJilha = itemView.findViewById(R.id.txtJilha);
            txtState = itemView.findViewById(R.id.txtState);
            txtRank = itemView.findViewById(R.id.txtRank);
            txtMobileNumber = itemView.findViewById(R.id.txtMobileNumber);
            txtAmount = itemView.findViewById(R.id.txtAmount);
            txtDate = itemView.findViewById(R.id.txtDate);
            profilepicture = itemView.findViewById(R.id.profilepicture);
            kisanImg = itemView.findViewById(R.id.kisanImg);
        }
    }
}
