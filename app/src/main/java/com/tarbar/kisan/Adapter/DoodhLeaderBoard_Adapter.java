package com.tarbar.kisan.Adapter;

import static com.tarbar.kisan.Helper.constant.GET_DOODH_LIST;
import static com.tarbar.kisan.Helper.constant.PROFILE_IMGAE_PATH;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
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
import com.squareup.picasso.Picasso;
import com.tarbar.kisan.Activities.LoadFragments;
import com.tarbar.kisan.Helper.Iconstant;
import com.tarbar.kisan.Helper.SharedPreferenceManager;
import com.tarbar.kisan.Helper.constant;
import com.tarbar.kisan.R;

import java.io.ByteArrayOutputStream;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class DoodhLeaderBoard_Adapter extends RecyclerView.Adapter<DoodhLeaderBoard_Adapter.ViewHolder> {
    private List<Map<String, String>> data;
    private Context context;

    public DoodhLeaderBoard_Adapter(List<Map<String, String>> doodhData, Context context) {
        this.data = doodhData;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_doodh_leaderboard, parent, false);
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

        if (isValid(animalInfo.get("rank"))) {
            holder.txtRank.setText(context.getString(R.string.str_rank) + " : " + animalInfo.get("rank"));
        }

        if (isValid(animalInfo.get("number"))) {
            holder.txtAnimalNumber.setText(context.getString(R.string.title_pashu_number) + " : " + animalInfo.get("number"));
        }

        if (isValid(animalInfo.get("pashuDoodh"))) {
            holder.txtDoodhKilo.setText(context.getString(R.string.str_doodh) + " : " + animalInfo.get("pashuDoodh") + " " + context.getString(R.string.str_kilo));
        }

        if (isValid(animalInfo.get("type"))) {
            holder.txtAnimalType.setText(context.getString(R.string.txt_pashu) + " : " + animalInfo.get("type"));
        }

        if (isValid(animalInfo.get("byatCount"))) {
            holder.txtByatCount.setText(context.getString(R.string.str_byat) + " : " + animalInfo.get("byatCount"));
        }

        if (isValid(animalInfo.get("pashuPita"))) {
            holder.txtPitaNumber.setText(context.getString(R.string.str_pita_number) + " : " + animalInfo.get("pashuPita"));
        }

        String dateString = animalInfo.get("date");
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

        if (isValid(animalInfo.get("name")) || isValid(animalInfo.get("father_name"))) {
            String fullName = "";

            if (isValid(animalInfo.get("name"))) {
                fullName += animalInfo.get("name");
            }

            if (isValid(animalInfo.get("father_name"))) {
                if (!fullName.isEmpty()) fullName += " ";
                fullName += animalInfo.get("father_name");
            }

//            if (isValid(animalInfo.get("surname"))) {
//                if (!fullName.isEmpty()) fullName += " ";
//                fullName += animalInfo.get("surname");
//            }
            holder.txtName.setText(fullName);
        }

        if (isValid(animalInfo.get("kisanNumber"))) {
            holder.txtNumber.setText(animalInfo.get("kisanNumber"));
        }
        if (isValid(animalInfo.get("state"))) {
            holder.txtAddress.setText(context.getString(R.string.title_state) + " : " + animalInfo.get("state"));
        }
        if (isValid(animalInfo.get("tahsil"))) {
            holder.txtTahsil.setText(context.getString(R.string.title_tahsil) + " : " + animalInfo.get("tahsil"));
        }
        if (isValid(animalInfo.get("jilha"))) {
            holder.txtJilha.setText(context.getString(R.string.title_jilha) + " : " + animalInfo.get("jilha"));
        }
        if (isValid(animalInfo.get("village"))) {
            holder.txtVillage.setText(context.getString(R.string.title_village) + " : " + animalInfo.get("village"));
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

    private boolean isValid(String value) {
        return value != null && !value.trim().equalsIgnoreCase("null") && !value.trim().isEmpty();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtRank, txtDoodhKilo, txtAnimalType, txtByatCount, txtPitaNumber, txtDate, txtAnimalNumber;
        TextView txtName, txtNumber, txtAddress, txtTahsil, txtJilha, txtVillage;
        CircleImageView profilepicture;
        ImageView kisanImg;
        private byte[] imageData;

        public ViewHolder(View itemView) {
            super(itemView);
            txtRank = itemView.findViewById(R.id.txtRank);
            txtAnimalNumber = itemView.findViewById(R.id.txtAnimalNumber);
            txtDoodhKilo = itemView.findViewById(R.id.txtDoodhKilo);
            txtAnimalType = itemView.findViewById(R.id.txtAnimalType);
            txtByatCount = itemView.findViewById(R.id.txtByatCount);
            txtPitaNumber = itemView.findViewById(R.id.txtPitaNumber);
            txtDate = itemView.findViewById(R.id.txtDate);

            txtName = itemView.findViewById(R.id.txtName);
            txtNumber = itemView.findViewById(R.id.txtNumber);
            txtAddress = itemView.findViewById(R.id.txtAddress);
            txtTahsil = itemView.findViewById(R.id.txtTahsil);
            txtJilha = itemView.findViewById(R.id.txtJilha);
            txtVillage = itemView.findViewById(R.id.txtVillage);

            profilepicture = itemView.findViewById(R.id.profilepicture);
            kisanImg = itemView.findViewById(R.id.kisanImg);
        }
    }
}
