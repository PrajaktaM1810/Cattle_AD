package com.tarbar.kisan.Activities;

import static android.view.View.GONE;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.tarbar.kisan.Fragments.DoodhOption_fragment;
import com.tarbar.kisan.Fragments.HomeFragment;
import com.tarbar.kisan.Fragments.PashuOption_fragment;
import com.tarbar.kisan.Fragments.ProfileFragment;
import com.tarbar.kisan.Helper.Iconstant;
import com.tarbar.kisan.Helper.SharedPreferenceManager;
import com.tarbar.kisan.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private SharedPreferenceManager sharedPrefMgr;
    String userid, mobile, address, name;
    BottomNavigationView bottomNavigationView;
    HomeFragment HomeFragment_ax;
    PashuOption_fragment PashuOption;
    DoodhOption_fragment DoodhOption;
    ProfileFragment ProfileFragment;

    public LinearLayout button1, button2, button3;
    public TextView btn1txt,btn2txt,btn3txt;
    public ImageView iconbutton1;

    public CardView cardView;

    public SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        View rootView = findViewById(android.R.id.content);
        rootView.setBackgroundColor(Color.WHITE);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        sharedPrefMgr = new SharedPreferenceManager(MainActivity.this);
        sharedPrefMgr.connectDB();
        userid = sharedPrefMgr.getString(Iconstant.userid);
        sharedPrefMgr.closeDB();

        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);

        iconbutton1 = findViewById(R.id.iconbutton1);

        btn1txt = findViewById(R.id.btn1txt);
        btn2txt = findViewById(R.id.btn2txt);
        btn3txt = findViewById(R.id.btn3txt);

        cardView = findViewById(R.id.cardView);

        mobile = sharedPrefMgr.getString("mobile");
        address = sharedPrefMgr.getString("address");
        name = sharedPrefMgr.getString("name");
        Log.d("MainActivity", "Retrieved_mobile_number: " + mobile);

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
//        swipeRefreshLayout.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(false);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setItemIconTintList(null);

        HomeFragment_ax = new HomeFragment();
        PashuOption = new PashuOption_fragment();
        DoodhOption = new DoodhOption_fragment();
        ProfileFragment = new ProfileFragment();

        if (getIntent().getBooleanExtra("SELECT_HOME_FRAGMENT", false)) {
            bottomNavigationView.setSelectedItemId(R.id.action_home);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.mainContainer, HomeFragment_ax)
                    .commit();
        } else if (getIntent().getBooleanExtra("SELECT_DETAILS_FRAGMENT", false)) {
            bottomNavigationView.setSelectedItemId(R.id.action_details);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.mainContainer, PashuOption)
                    .commit();
        } else if (getIntent().getBooleanExtra("SELECT_MILK_FRAGMENT", false)) {
            bottomNavigationView.setSelectedItemId(R.id.action_milk);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.mainContainer, DoodhOption)
                    .commit();
        } else if (getIntent().getBooleanExtra("SELECT_PROFILE_FRAGMENT", false)) {
            bottomNavigationView.setSelectedItemId(R.id.action_profile);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.mainContainer, ProfileFragment)
                    .commit();
        } else {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.mainContainer, HomeFragment_ax)
                    .commit();
        }

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.action_home) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.mainContainer, HomeFragment_ax)
                    .commit();
            return true;
        } else if (menuItem.getItemId() == R.id.action_details) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.mainContainer, PashuOption)
                    .commit();
            resetOtherButtonColors();
            return true;
        } else if (menuItem.getItemId() == R.id.action_milk) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.mainContainer, DoodhOption)
                    .commit();
            resetOtherButtonColors();
            return true;
        } else if (menuItem.getItemId() == R.id.action_profile) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.mainContainer, ProfileFragment)
                    .commit();
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().findFragmentById(R.id.mainContainer) instanceof PashuOption_fragment) {
            swipeRefreshLayout.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setRefreshing(true);
            swipeRefreshLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(false);
                    PashuOption_fragment fragment = (PashuOption_fragment) getSupportFragmentManager()
                            .findFragmentById(R.id.mainContainer);
                    if (fragment != null) {
                        fragment.setBySearch(false);
                        fragment.detailsHistory();
                        fragment.changeButtonColor(fragment.getAllLayout());
                        fragment.resetButtonColor(fragment.getCowLayout());
                        fragment.resetButtonColor(fragment.getBuffaloLayout());
                        button2.setVisibility(GONE);
                    }
                }
            }, 2000);
        } else if (getSupportFragmentManager().findFragmentById(R.id.mainContainer) instanceof DoodhOption_fragment) {
            swipeRefreshLayout.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setRefreshing(true);
            swipeRefreshLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(false);
                    DoodhOption_fragment fragment = (DoodhOption_fragment) getSupportFragmentManager()
                            .findFragmentById(R.id.mainContainer);
                    if (fragment != null) {
                        fragment.setBySearch(false);
                        fragment.detailsHistory();
                        fragment.changeButtonColor(fragment.getAllLayout());
                        fragment.resetButtonColor(fragment.getCowLayout());
                        fragment.resetButtonColor(fragment.getBuffaloLayout());
                        button2.setVisibility(GONE);
                    }
                }
            }, 2000);
        } else {
            super.onBackPressed();
        }
    }

    private void resetOtherButtonColors() {
        button1.setBackgroundColor(getResources().getColor(android.R.color.white));
        ((TextView) btn1txt).setTextColor(getResources().getColor(android.R.color.black));
        button2.setBackgroundColor(getResources().getColor(android.R.color.white));
        ((TextView) btn2txt).setTextColor(getResources().getColor(android.R.color.black));
        button3.setBackgroundColor(getResources().getColor(android.R.color.white));
        ((TextView) btn3txt).setTextColor(getResources().getColor(android.R.color.black));
    }
}
