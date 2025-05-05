package com.tarbar.kisan.Activities;

import static android.view.View.GONE;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.tarbar.kisan.Fragments.BullOption_fragment;
import com.tarbar.kisan.Fragments.DoodhLeaderboard_fragment;
import com.tarbar.kisan.Fragments.DoodhOption_fragment;
import com.tarbar.kisan.Fragments.FreeBijdan_fragment;
import com.tarbar.kisan.Fragments.GarbhavatiForm_fragment;
import com.tarbar.kisan.Fragments.KisanMadat_fragment;
import com.tarbar.kisan.Fragments.PashuGarbhavati_fragment;
import com.tarbar.kisan.Fragments.PashuOption_fragment;
import com.tarbar.kisan.Fragments.PashuUpdateForm_fragment;
import com.tarbar.kisan.Fragments.PashuUpdateOption_fragment;
import com.tarbar.kisan.R;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
        transaction.commit();
    }

//    @Override
//    public void onBackPressed() {
//        if (getSupportFragmentManager().findFragmentById(R.id.mainContainer) instanceof PashuOption_fragment) {
//            swipeRefreshLayout.setVisibility(View.VISIBLE);
//            swipeRefreshLayout.setRefreshing(true);
//            swipeRefreshLayout.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    swipeRefreshLayout.setRefreshing(false);
//                    PashuOption_fragment fragment = (PashuOption_fragment) getSupportFragmentManager()
//                            .findFragmentById(R.id.mainContainer);
//                    if (fragment != null) {
//                        fragment.setBySearch(false);
//                        fragment.detailsHistory();
//                        fragment.changeButtonColor(fragment.getAllLayout());
//                        fragment.resetButtonColor(fragment.getCowLayout());
//                        fragment.resetButtonColor(fragment.getBuffaloLayout());
//                    }
//                }
//            }, 2000);
//        } else if (getSupportFragmentManager().findFragmentById(R.id.mainContainer) instanceof DoodhOption_fragment) {
//            swipeRefreshLayout.setVisibility(View.VISIBLE);
//            swipeRefreshLayout.setRefreshing(true);
//            swipeRefreshLayout.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    swipeRefreshLayout.setRefreshing(false);
//                    DoodhOption_fragment fragment = (DoodhOption_fragment) getSupportFragmentManager()
//                            .findFragmentById(R.id.mainContainer);
//                    if (fragment != null) {
//                        fragment.setBySearch(false);
//                        fragment.detailsHistory();
//                        fragment.changeButtonColor(fragment.getAllLayout());
//                        fragment.resetButtonColor(fragment.getCowLayout());
//                        fragment.resetButtonColor(fragment.getBuffaloLayout());
//                    }
//                }
//            }, 2000);
//        } else {
//            Intent i = new Intent(LoadFormFragments.this, MainActivity.class);
//            i.putExtra("SELECT_HOME_FRAGMENT", true);
//            startActivity(i);
//            finish();
//            super.onBackPressed();
//        }
//    }

}
