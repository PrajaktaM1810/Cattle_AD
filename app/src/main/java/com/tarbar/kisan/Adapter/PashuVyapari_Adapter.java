package com.tarbar.kisan.Adapter;

import static com.tarbar.kisan.Helper.constant.GET_DOODH_LIST;
import static com.tarbar.kisan.Helper.constant.PROFILE_IMGAE_PATH;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
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

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
public class PashuVyapari_Adapter extends RecyclerView.Adapter<PashuVyapari_Adapter.ViewHolder> {
    private List<Map<String, String>> data;
    private Context context;

    public PashuVyapari_Adapter(List<Map<String, String>> doodhData, Context context) {
        this.data = doodhData;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_pashu_vyapari, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Map<String, String> animalInfo = data.get(position);

        holder.txtRank.setText(context.getString(R.string.str_rank) + " : " + getValidText(animalInfo.get("rank")));
        holder.txtVillage.setText(context.getString(R.string.title_village) + " : " + getValidText(animalInfo.get("village")));
        holder.txtTahsil.setText(context.getString(R.string.title_tahsil) + " : " + getValidText(animalInfo.get("tahsil")));
        holder.txtJilha.setText(context.getString(R.string.title_jilha) + " : " + getValidText(animalInfo.get("jilha")));
        holder.txtState.setText(context.getString(R.string.title_state) + " : " + getValidText(animalInfo.get("state")));
        holder.txtNumber.setText(getValidText(animalInfo.get("kisanNumber")));

        String fullName = getValidText(animalInfo.get("name"));
        holder.txtName.setText(fullName);

        holder.txtFatherName.setText(context.getString(R.string.title_father_name) + " : " + getValidText(animalInfo.get("father_name")));
        holder.txtCaste.setText(context.getString(R.string.title_caste) + " : " + getValidText(animalInfo.get("caste")));
        holder.txtSellingTotal.setText(context.getString(R.string.str_total_selling) + " : " + getValidText(animalInfo.get("total_sales")));

        String mobileNumber = getValidText(animalInfo.get("kisanNumber"));

        holder.imgCall.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + mobileNumber));
            context.startActivity(intent);
        });

        holder.imgWhatsapp.setOnClickListener(v -> {
            String number = mobileNumber;
            number = number.replace("+", "").replace(" ", "");

            String url = "https://api.whatsapp.com/send?phone=" + number;

            if (isAnyWhatsAppInstalled(context)) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));

                if (intent.resolveActivity(context.getPackageManager()) != null) {
                    context.startActivity(intent);
                }
            } else {
                Toast.makeText(context, context.getString(R.string.whatsapp_not_installed), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isAnyWhatsAppInstalled(Context context) {
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> packages = pm.getInstalledPackages(0);

        for (PackageInfo pkg : packages) {
            if (pkg.packageName.startsWith("com.whatsapp")) {
                return true;
            }
        }
        return false;
    }

    private String getValidText(String value) {
        return isValid(value) ? value : "-";
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
        TextView txtRank,txtNumber, txtState, txtTahsil, txtJilha, txtVillage;
        TextView txtName, txtFatherName, txtCaste, txtSellingTotal;
        ImageView imgCall, imgWhatsapp, imgArrow;
        public ViewHolder(View itemView) {
            super(itemView);
            txtRank = itemView.findViewById(R.id.txtRank);
            txtState = itemView.findViewById(R.id.txtState);
            txtTahsil = itemView.findViewById(R.id.txtTahsil);
            txtJilha = itemView.findViewById(R.id.txtJilha);
            txtVillage = itemView.findViewById(R.id.txtVillage);
            txtFatherName = itemView.findViewById(R.id.txtFatherName);
            txtCaste = itemView.findViewById(R.id.txtCaste);
            txtName = itemView.findViewById(R.id.txtName);
            txtNumber = itemView.findViewById(R.id.txtNumber);
            txtSellingTotal = itemView.findViewById(R.id.txtSellingTotal);

            imgCall = itemView.findViewById(R.id.imgCall);
            imgWhatsapp = itemView.findViewById(R.id.imgWhatsapp);
            imgArrow = itemView.findViewById(R.id.imgArrow);
        }
    }
}