package com.tarbar.kisan.Fragments;

import static android.view.View.GONE;
import static com.tarbar.kisan.Helper.constant.SLIDER;
import static com.tarbar.kisan.Helper.constant.SLIDER_IMG_PATH;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.tarbar.kisan.Activities.LoadFilterFragments;
import com.tarbar.kisan.Activities.LoadFragments;
import com.tarbar.kisan.Activities.MainActivity;
import com.tarbar.kisan.Adapter.PagerAdapter;
import com.tarbar.kisan.Helper.ApiUtils;
import com.tarbar.kisan.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class HomeFragment extends Fragment {
    private ViewPager2 mViewPager1;
    private ViewPager2 mViewPager2;
    private Timer timer1;
    private Timer timer2;
    private int currentPage1 = 0;
    private int currentPage2 = 0;
    private final List<String> imageUrls1 = new ArrayList<>();
    private final List<String> imageUrls2 = new ArrayList<>();
    private PagerAdapter pagerAdapter1;
    private PagerAdapter pagerAdapter2;
    ImageView companyImg;
    MainActivity mainActivityInstance;

    private LinearLayout freeBijdan, pashuDr, pashuBusiness, farmerHelp, bullDetails, pashuBijdan, pashuUpdate, mandi;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.home_fragment, container, false);
        root.setBackgroundColor(Color.WHITE);
        mViewPager1 = root.findViewById(R.id.slider);
        mViewPager2 = root.findViewById(R.id.slider2);
        companyImg = root.findViewById(R.id.companyImg);
        mainActivityInstance = (MainActivity) getActivity();
        mainActivityInstance.swipeRefreshLayout.setRefreshing(false);

        pagerAdapter1 = new PagerAdapter(requireContext(), imageUrls1);
        pagerAdapter2 = new PagerAdapter(requireContext(), imageUrls2);

        mViewPager1.setAdapter(pagerAdapter1);
        mViewPager2.setAdapter(pagerAdapter2);

        freeBijdan = root.findViewById(R.id.freeBijdan);
        pashuDr = root.findViewById(R.id.pashuDr);
        pashuBusiness = root.findViewById(R.id.pashuBusiness);
        farmerHelp = root.findViewById(R.id.farmerHelp);
        bullDetails = root.findViewById(R.id.bullDetails);
        pashuBijdan = root.findViewById(R.id.pashuBijdan);
        pashuUpdate = root.findViewById(R.id.pashuUpdate);
        mandi = root.findViewById(R.id.mandi);

        companyImg.setOnClickListener(v -> {
            Animation animation = AnimationUtils.loadAnimation(requireContext(), R.anim.click_animation);
            v.startAnimation(animation);
            Intent i = new Intent(requireContext(), LoadFragments.class);
            i.putExtra("Fragment_CompanyProfile", true);
            startActivity(i);
            requireActivity().finish();
        });

        freeBijdan.setOnClickListener(v -> {
            Animation animation = AnimationUtils.loadAnimation(requireContext(), R.anim.click_animation);
            v.startAnimation(animation);
            Intent i = new Intent(requireContext(), LoadFilterFragments.class);
            i.putExtra("FreeBijdan_fragment", true);
            startActivity(i);
            requireActivity().finish();
        });

        pashuDr.setOnClickListener(v -> {
            Animation animation = AnimationUtils.loadAnimation(requireContext(), R.anim.click_animation);
            v.startAnimation(animation);
            Intent i = new Intent(requireContext(), LoadFragments.class);
            i.putExtra("Fragment_CommingSoon", true);
            startActivity(i);
            requireActivity().finish();
        });

        pashuBusiness.setOnClickListener(v -> {
            Animation animation = AnimationUtils.loadAnimation(requireContext(), R.anim.click_animation);
            v.startAnimation(animation);
            Intent i = new Intent(requireContext(), LoadFilterFragments.class);
            i.putExtra("PashuVyapari_fragment", true);
            startActivity(i);
            requireActivity().finish();
        });

        farmerHelp.setOnClickListener(v -> {
            Animation animation = AnimationUtils.loadAnimation(requireContext(), R.anim.click_animation);
            v.startAnimation(animation);
            Intent i = new Intent(requireContext(), LoadFilterFragments.class);
            i.putExtra("KisanMadat_fragment", true);
            startActivity(i);
            requireActivity().finish();
        });

        bullDetails.setOnClickListener(v -> {
            Animation animation = AnimationUtils.loadAnimation(requireContext(), R.anim.click_animation);
            v.startAnimation(animation);
            Intent i = new Intent(requireContext(), LoadFilterFragments.class);
            i.putExtra("BullOption_fragment", true);
            startActivity(i);
            requireActivity().finish();
        });

        pashuBijdan.setOnClickListener(v -> {
            Animation animation = AnimationUtils.loadAnimation(requireContext(), R.anim.click_animation);
            v.startAnimation(animation);
            Intent i = new Intent(requireContext(), LoadFilterFragments.class);
            i.putExtra("PashuGarbhavati_fragment", true);
            startActivity(i);
            requireActivity().finish();
        });

        pashuUpdate.setOnClickListener(v -> {
            Animation animation = AnimationUtils.loadAnimation(requireContext(), R.anim.click_animation);
            v.startAnimation(animation);
            Intent i = new Intent(requireContext(), LoadFilterFragments.class);
            i.putExtra("PashuUpdate_fragment", true);
            startActivity(i);
            requireActivity().finish();
        });

        mandi.setOnClickListener(v -> {
            Animation animation = AnimationUtils.loadAnimation(requireContext(), R.anim.click_animation);
            v.startAnimation(animation);
            Intent i = new Intent(requireContext(), LoadFragments.class);
            i.putExtra("Fragment_CommingSoon", true);
            startActivity(i);
            requireActivity().finish();
        });

        top_imgs_apicall();
        bottom_imgs_apicall();

        startAutoSlider();

        return root;
    }

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

    private void updateCurrentPage1() {
        currentPage1++;
        if (currentPage1 >= imageUrls1.size()) {
            currentPage1 = 0;
            mViewPager1.setCurrentItem(currentPage1, false);
        } else {
            mViewPager1.setCurrentItem(currentPage1, true);
        }
    }

    private void disableTouchSliding(ViewPager2 viewPager) {
        viewPager.setUserInputEnabled(false);
        viewPager.setOnTouchListener((v, event) -> true);
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
