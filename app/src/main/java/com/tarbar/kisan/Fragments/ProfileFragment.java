package com.tarbar.kisan.Fragments;

import static android.view.View.GONE;
import static com.tarbar.kisan.Helper.constant.FETCH_PROFILE;
import static com.tarbar.kisan.Helper.constant.PROFILE_IMGAE_PATH;
import static com.tarbar.kisan.Helper.constant.SLIDER;
import static com.tarbar.kisan.Helper.constant.SLIDER_IMG_PATH;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

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
import com.tarbar.kisan.Activities.LoadFragments;
import com.tarbar.kisan.Activities.LoginActivity;
import com.tarbar.kisan.Activities.MainActivity;
import com.tarbar.kisan.Adapter.PagerAdapter;
import com.tarbar.kisan.Helper.ApiUtils;
import com.tarbar.kisan.Helper.Iconstant;
import com.tarbar.kisan.Helper.NonSwipeableViewPager;
import com.tarbar.kisan.Helper.SharedPreferenceManager;
import com.tarbar.kisan.Helper.constant;
import com.tarbar.kisan.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {
    private ViewPager2 mViewPager1;
    private ViewPager2 mViewPager2;
    private PagerAdapter pagerAdapter1;
    private PagerAdapter pagerAdapter2;
    private SharedPreferenceManager sharedPrefMgr;
    String userid;
    ProgressDialog dialog;
    Context context;
    private Timer timer1;
    private Timer timer2;
    private int currentPage1 = 0;
    private int currentPage2 = 0;
    private final List<String> imageUrls1 = new ArrayList<>();
    private final List<String> imageUrls2 = new ArrayList<>();

    private byte[] imageData;

    CircleImageView profilepicture;
    ImageView kisanImg;
    TextView username,address;
    Button ad_top,ad_bottom;
    TextView editproftxt,roletxt,resettxt,logouttxt;
    RelativeLayout editprofile,user_role,reset_password,logout;
    LinearLayout editprofcrown,editprofcrown1,roleicon1,roleicon2,reseticon1,reseticon2,logoutIcon1,logoutIcon2;
    LinearLayout top_slider,bottom_slider;
    ImageView whatsappimg;
    String enquiry,mobileNumber;
    LinearLayout needHelp;
    TextView needHelpNumber;
    MainActivity mainActivityInstance;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_profile, container, false);
        view.setBackgroundColor(Color.WHITE);
        context = getActivity();
        sharedPrefMgr = new SharedPreferenceManager(context);
        sharedPrefMgr.connectDB();
        userid = sharedPrefMgr.getString(Iconstant.userid);
        mobileNumber = sharedPrefMgr.getString(Iconstant.mobile);
//        enquiry = sharedPrefMgr.getString(Iconstant.enquiry_whatsapp_number);
        sharedPrefMgr.closeDB();

        enquiry = constant.tarbar_enquiry_number;

        initView(view);

        mainActivityInstance = (MainActivity) getActivity();
        mainActivityInstance.swipeRefreshLayout.setRefreshing(false);
        getKisanProfile();
        top_imgs_apicall();
        bottom_imgs_apicall();

        startAutoSlider();

        return view;
    }

    private void initView(View view) {

        profilepicture = view.findViewById(R.id.profilepicture);
        kisanImg = view.findViewById(R.id.kisanImg);

        mViewPager1 = view.findViewById(R.id.slider);
        mViewPager2 = view.findViewById(R.id.slider2);

        editproftxt = view.findViewById(R.id.editproftxt);
        roletxt = view.findViewById(R.id.roletxt);
        resettxt = view.findViewById(R.id.resettxt);
        logouttxt = view.findViewById(R.id.logouttxt);

        editprofile = view.findViewById(R.id.editprofile);
        user_role = view.findViewById(R.id.user_role);
        reset_password = view.findViewById(R.id.reset_password);
        logout = view.findViewById(R.id.logout);

        username = view.findViewById(R.id.username);
        address = view.findViewById(R.id.address);
        ad_top = view.findViewById(R.id.ad_top);
        ad_bottom = view.findViewById(R.id.ad_bottom);

        editprofcrown = view.findViewById(R.id.editprofcrown);
        editprofcrown1 = view.findViewById(R.id.editprofcrown1);
        roleicon1 = view.findViewById(R.id.roleicon1);
        roleicon2 = view.findViewById(R.id.roleicon2);
        reseticon1 = view.findViewById(R.id.reseticon1);
        reseticon2 = view.findViewById(R.id.reseticon2);
        logoutIcon1 = view.findViewById(R.id.logoutIcon1);
        logoutIcon2 = view.findViewById(R.id.logoutIcon2);

        top_slider = view.findViewById(R.id.top_slider);
        bottom_slider = view.findViewById(R.id.bottom_slider);
        whatsappimg = view.findViewById(R.id.whatsappimg);
        needHelpNumber = view.findViewById(R.id.needHelpNumber);
        needHelp = view.findViewById(R.id.needHelp);

        pagerAdapter1 = new PagerAdapter(requireContext(), imageUrls1);
        pagerAdapter2 = new PagerAdapter(requireContext(), imageUrls2);

        mViewPager1.setAdapter(pagerAdapter1);
        mViewPager2.setAdapter(pagerAdapter2);


        whatsappimg.setOnClickListener(v -> {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.click_animation);
            v.startAnimation(animation);
            messageDialog();
        });

        needHelpNumber.setOnClickListener(v -> {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.click_animation);
            v.startAnimation(animation);
            messageDialog();
        });

        needHelp.setOnClickListener(v -> {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.click_animation);
            v.startAnimation(animation);
            messageDialog();
        });

        ad_top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation animation = AnimationUtils.loadAnimation(context, R.anim.click_animation);
                v.startAnimation(animation);
                top_slider.setVisibility(View.VISIBLE);
                bottom_slider.setVisibility(View.GONE);
            }
        });

        ad_bottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation animation = AnimationUtils.loadAnimation(context, R.anim.click_animation);
                v.startAnimation(animation);
                bottom_slider.setVisibility(View.VISIBLE);
                top_slider.setVisibility(View.GONE);
            }
        });

//        Text

        editproftxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeDigit("0");
                Intent intent = new Intent(getActivity(), LoadFragments.class);
                intent.putExtra("Fragment_EditKisanProfile", true);
                startActivity(intent);
            }
        });

        roletxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeDigit("1");
                Intent intent = new Intent(getActivity(), LoadFragments.class);
                intent.putExtra("Fragment_KisanSevaAnurodh", true);
                startActivity(intent);
            }
        });

        resettxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeDigit("2");
                Intent intent = new Intent(getActivity(), LoadFragments.class);
                intent.putExtra("Fragment_ResetPassword", true);
                startActivity(intent);
            }
        });

        logouttxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeDigit("3");
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(getString(R.string.confirm_logout));
                builder.setNegativeButton(getString(R.string.no), null);
                builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        sharedPrefMgr.clear();
                        SharedPreferences preferences = getActivity().getSharedPreferences("Cattle", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.clear();
                        editor.commit();
                        getActivity().finish();
                        Intent intent = new Intent(getActivity(),LoginActivity.class);
                        startActivity(intent);
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
                alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
                alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
            }
        });

//        Layouts
        editprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeDigit("0");
                Intent intent = new Intent(getActivity(), LoadFragments.class);
                intent.putExtra("Fragment_EditKisanProfile", true);
                startActivity(intent);
            }
        });

        user_role.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeDigit("1");
                Intent intent = new Intent(getActivity(), LoadFragments.class);
                intent.putExtra("Fragment_KisanSevaAnurodh", true);
                startActivity(intent);
            }
        });

        reset_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeDigit("2");
                Intent intent = new Intent(getActivity(), LoadFragments.class);
                intent.putExtra("Fragment_ResetPassword", true);
                startActivity(intent);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeDigit("3");
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(getString(R.string.confirm_logout));
                builder.setNegativeButton(getString(R.string.no), null);
                builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        sharedPrefMgr.clear();
                        SharedPreferences preferences = getActivity().getSharedPreferences("Cattle", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.clear();
                        editor.commit();
                        getActivity().finish();
                        Intent intent = new Intent(getActivity(),LoginActivity.class);
                        startActivity(intent);
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
                alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
                alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
            }
        });
    }

    private void messageDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.lyt_forget_password, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        // dialog.setCancelable(false);

        EditText input = dialogView.findViewById(R.id.forgetPasswordNumber);
        TextView textGetPassword = dialogView.findViewById(R.id.textGetPassword);
        LinearLayout password_layout = dialogView.findViewById(R.id.password_layout);
        password_layout.setVisibility(View.GONE);
        textGetPassword.setText(R.string.do_message);

        dialogView.findViewById(R.id.textGetPassword).setOnClickListener(v -> {
            String mobile = input.getText().toString().trim();

            if (mobile.isEmpty()) {
                input.setError(getString(R.string.enter_mobile_number));
            } else if (!Pattern.matches("[6-9][0-9]{9}", mobile)) {
                input.setError(getString(R.string.enter_correct_mobile_number));
            } else if (!mobile.equals(mobileNumber)) {
                Toast.makeText(context, getString(R.string.enter_login_mobile_number), Toast.LENGTH_SHORT).show();
            } else {
                    String messageToSend = getString(R.string.hello) + "\nमोबाइल नंबर : " + mobile;
                    WhatsAppMessage(messageToSend,enquiry);
                    dialog.dismiss();
            }
        });
        dialogView.findViewById(R.id.textCancel).setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void WhatsAppMessage(String message, String number) {
        PackageManager pm = getActivity().getPackageManager();
        try {
            pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
            String url = "https://api.whatsapp.com/send?phone=" + number + "&text=" + Uri.encode(message);
            Intent sendIntent = new Intent(Intent.ACTION_VIEW);
            sendIntent.setData(Uri.parse(url));
            startActivity(sendIntent);
        } catch (PackageManager.NameNotFoundException e) {
            Toast.makeText(getActivity(), R.string.whatsapp_not_installed, Toast.LENGTH_SHORT).show(); // Use getActivity() for Toast
        }
    }

    public void changeDigit(String id) {
        switch (id) {
            case "0":
                unselectOthers();
                editprofcrown.setVisibility(View.GONE);
                editprofcrown1.setVisibility(View.VISIBLE);
                editproftxt.setTextColor(getResources().getColor(R.color.IconBlue));
                break;
            case "1":
                unselectOthers();
                roleicon1.setVisibility(View.GONE);
                roleicon2.setVisibility(View.VISIBLE);
                roletxt.setTextColor(getResources().getColor(R.color.IconBlue));
                break;
            case "2":
                unselectOthers();
                reseticon1.setVisibility(View.GONE);
                reseticon2.setVisibility(View.VISIBLE);
                resettxt.setTextColor(getResources().getColor(R.color.IconBlue));
                break;
            case "3":
                unselectOthers();
                logoutIcon1.setVisibility(View.GONE);
                logoutIcon2.setVisibility(View.VISIBLE);
                logouttxt.setTextColor(getResources().getColor(R.color.IconBlue));
                break;
        }
    }

    private void unselectOthers() {
        editprofcrown.setVisibility(View.VISIBLE);
        editprofcrown1.setVisibility(View.GONE);
        editproftxt.setTextColor(getResources().getColor(R.color.black));

        roleicon1.setVisibility(View.VISIBLE);
        roleicon2.setVisibility(View.GONE);
        roletxt.setTextColor(getResources().getColor(R.color.black));

        reseticon1.setVisibility(View.VISIBLE);
        reseticon2.setVisibility(View.GONE);
        resettxt.setTextColor(getResources().getColor(R.color.black));

        logoutIcon1.setVisibility(View.VISIBLE);
        logoutIcon2.setVisibility(View.GONE);
        logouttxt.setTextColor(getResources().getColor(R.color.black));
    }

    private void getKisanProfile() {
        dialog = new ProgressDialog(context);
        dialog.setMessage(getString(R.string.getting_data));
        dialog.setCancelable(false);
        dialog.show();
        String url = FETCH_PROFILE;
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("status");
                            String message = jsonObject.getString("message");
                            if (status.equalsIgnoreCase("success")) {
                                JSONArray dataArray = jsonObject.getJSONArray("data");
                                if (dataArray.length() > 0) {
                                    JSONObject userData = dataArray.getJSONObject(0);
                                    String KisanName = userData.getString("name");
                                    String Kisanfathername = userData.getString("father_name");
//                                    String surname = userData.getString("surname");
                                    String KisanAddress = userData.getString("state");
                                    String profilePic = userData.getString("profile_pic");

                                    if (isValid(KisanName) || isValid(Kisanfathername)) {
                                        String fullName = "";

                                        if (isValid(KisanName)) {
                                            fullName += KisanName;
                                        }

                                        if (isValid(Kisanfathername)) {
                                            if (!fullName.isEmpty()) fullName += " ";
                                            fullName += Kisanfathername;
                                        }

//                                        if (isValid(surname)) {
//                                            if (!fullName.isEmpty()) fullName += " ";
//                                            fullName += surname;
//                                        }
                                        username.setText(fullName);
                                    }
                                    address.setText(KisanAddress);
                                    address.setText(KisanAddress);

                                    String imageUrl = PROFILE_IMGAE_PATH + profilePic;
                                    Picasso.get()
                                            .load(imageUrl)
                                            .into(profilepicture, new com.squareup.picasso.Callback() {
                                                @Override
                                                public void onSuccess() {
                                                    dialog.dismiss();
                                                    profilepicture.setVisibility(View.VISIBLE);
                                                    kisanImg.setVisibility(View.GONE);

                                                    Bitmap bitmap = ((BitmapDrawable) profilepicture.getDrawable()).getBitmap();
                                                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                                                    imageData = byteArrayOutputStream.toByteArray();
                                                    Log.d("ImageDataLength", "Length: " + imageData.length);
                                                }
                                                @Override
                                                public void onError(Exception e) {
                                                    dialog.dismiss();
                                                    kisanImg.setVisibility(View.VISIBLE);
                                                    profilepicture.setVisibility(View.GONE);
                                                }
                                            });
                                } else {
                                    dialog.dismiss();
                                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                dialog.dismiss();
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            dialog.dismiss();
                            if (e instanceof JSONException) {
                                Toast.makeText(context, R.string.json_parsing_error, Toast.LENGTH_SHORT).show();
                            } else {
                                Log.d("CheckException",""+e);
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
                params.put("userid", userid);
                return params;
            }
        };
        stringRequest.setRetryPolicy(ApiUtils.DEFAULT_RETRY_POLICY);
        requestQueue.add(stringRequest);
    }

    private boolean isValid(String value) {
        return value != null && !value.trim().equalsIgnoreCase("null") && !value.trim().isEmpty();
    }

    //    Slider

    private void top_imgs_apicall() {
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        StringRequest postRequest = new StringRequest(Request.Method.POST, SLIDER,
                response -> handleApiResponse(response, imageUrls1, pagerAdapter1, SLIDER_IMG_PATH),
                error -> {
                    Log.d("Error", " " + error);
                    Toast.makeText(requireContext(), "Error fetching top images", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("type", "top_slider");
                return params;
            }
        };
        postRequest.setRetryPolicy(ApiUtils.DEFAULT_RETRY_POLICY);
        requestQueue.add(postRequest);
    }

    private void bottom_imgs_apicall() {
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        StringRequest postRequest = new StringRequest(Request.Method.POST, SLIDER,
                response -> handleApiResponse(response, imageUrls2, pagerAdapter2, SLIDER_IMG_PATH),
                error -> {
                    Log.d("Error", " " + error);
                    Toast.makeText(requireContext(), "Error fetching bottom images", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("type", "bottom_slider");
                return params;
            }
        };
        postRequest.setRetryPolicy(ApiUtils.DEFAULT_RETRY_POLICY);
        requestQueue.add(postRequest);
    }

    private void handleApiResponse(String response, List<String> imageUrls, PagerAdapter adapter, String imgPath) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                JSONArray sliders = jsonObject.getJSONArray("data");
                for (int i = 0; i < sliders.length(); i++) {
                    JSONObject slider = sliders.getJSONObject(i);
                    String imageUrl = imgPath + slider.getString("img");
                    if (!imageUrls.contains(imageUrl)) {
                        imageUrls.add(imageUrl);
                    }
                }
                adapter.notifyDataSetChanged();
            } else {
                String message = jsonObject.getString("message");
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "Error parsing data", Toast.LENGTH_SHORT).show();
        }
    }

    private void startAutoSlider() {
        disableTouchSliding(mViewPager1);
        disableTouchSliding(mViewPager2);
        scheduleSlider(mViewPager1, imageUrls1, timer1, this::updateCurrentPage1);
        scheduleSlider(mViewPager2, imageUrls2, timer2, this::updateCurrentPage2);
    }

    private void disableTouchSliding(ViewPager2 viewPager) {
        viewPager.setUserInputEnabled(false);
        viewPager.setOnTouchListener((v, event) -> true);
    }

    private void updateCurrentPage1() {
        currentPage1++;
        if (currentPage1 >= imageUrls1.size()) {
            currentPage1 = 0;
            mViewPager1.setCurrentItem(currentPage1, false);
        } else {
            mViewPager1.setCurrentItem(currentPage1, true);
        }
    }

    private void updateCurrentPage2() {
        currentPage2++;
        if (currentPage2 >= imageUrls2.size()) {
            currentPage2 = 0;
            mViewPager2.setCurrentItem(currentPage2, false);
        } else {
            mViewPager2.setCurrentItem(currentPage2, true);
        }
    }

    private void scheduleSlider(ViewPager2 viewPager, List<String> imageUrls, Timer timer, Runnable pageUpdater) {
        final Handler handler = new Handler();
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (!imageUrls.isEmpty()) {
                    handler.post(pageUpdater);
                }
            }
        }, 7000, 7000);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (timer1 != null) timer1.cancel();
        if (timer2 != null) timer2.cancel();
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity != null && mainActivity.cardView != null) {
            mainActivity.cardView.setVisibility(GONE);
        }
    }
}
