package com.tarbar.kisan.Fragments;

import static android.view.View.GONE;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.tarbar.kisan.Activities.MainActivity;
import com.tarbar.kisan.R;

public class CompanyProfile_fragment extends Fragment {
    ImageView companyImg,TarbarLogo;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_company_profile, container, false);
        view.setBackgroundColor(Color.WHITE);
        initViews(view);

        return view;
    }

    private void initViews(View view) {
        companyImg = view.findViewById(R.id.companyImg);
        companyImg.setVisibility(GONE);

        TarbarLogo = view.findViewById(R.id.TarbarLogo);

        TarbarLogo.setOnClickListener(v -> {
            Animation animation = AnimationUtils.loadAnimation(requireContext(), R.anim.click_animation);
            v.startAnimation(animation);
            Intent i = new Intent(requireContext(), MainActivity.class);
            i.putExtra("SELECT_HOME_FRAGMENT", true);
            startActivity(i);
            requireActivity().finish();
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        requireActivity().getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent i = new Intent(requireContext(), MainActivity.class);
                i.putExtra("SELECT_HOME_FRAGMENT", true);
                startActivity(i);
                requireActivity().finish();
            }
        });
    }
}
