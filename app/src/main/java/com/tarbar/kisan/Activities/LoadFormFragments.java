package com.tarbar.kisan.Activities;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.tarbar.kisan.Fragments.PashuUpdateForm_fragment;
import com.tarbar.kisan.R;

import android.widget.ImageView;
import android.widget.LinearLayout;

public class LoadFormFragments extends AppCompatActivity {

    FrameLayout mainContainer;
    public ImageView back;
    public LinearLayout toolbarll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loadform_fragments);

        View rootView = findViewById(android.R.id.content);
        rootView.setBackgroundColor(Color.WHITE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        InitView();

        String sellingId = getIntent().getStringExtra("selling_id");
        String AnimalType = getIntent().getStringExtra("animalType");

        boolean PashuUpdateAdd_Form = getIntent().getBooleanExtra("PashuUpdateAddForm_fragment", false);
        boolean PashuUpdateEdit_Form = getIntent().getBooleanExtra("PashuUpdateEditForm_fragment", false);

        if (PashuUpdateAdd_Form) {
            PashuUpdateForm_fragment pashuUpdateForm_fragment = new PashuUpdateForm_fragment();
            Bundle bundle = new Bundle();
            bundle.putBoolean("addForm", true);
            bundle.putString("animalType", AnimalType);
            pashuUpdateForm_fragment.setArguments(bundle);
            replaceFragment(pashuUpdateForm_fragment);
        } else if (PashuUpdateEdit_Form) {
            PashuUpdateForm_fragment pashuUpdateForm_fragment = new PashuUpdateForm_fragment();
            Bundle bundle = new Bundle();
            bundle.putBoolean("editForm", true);
            bundle.putString("selling_id", sellingId);
            pashuUpdateForm_fragment.setArguments(bundle);
            replaceFragment(pashuUpdateForm_fragment);
        }
    }

    public void InitView() {
        mainContainer = findViewById(R.id.mainContainer);
        back = findViewById(R.id.back);
        toolbarll = findViewById(R.id.toolbarll);
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.mainContainer, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

}
