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
import com.tarbar.kisan.Fragments.PashuUpdateOption_fragment;
import com.tarbar.kisan.Fragments.PashuVyapari_fragment;
import com.tarbar.kisan.R;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LoadFilterFragments extends AppCompatActivity {
    FrameLayout mainContainer;

    public LinearLayout button1, button2, button3;
    public TextView btn1txt,btn2txt,btn3txt,title;
    public ImageView iconbutton1,iconbutton2,iconbutton3;
    public ImageView back;

    public CardView cardView;

    public SwipeRefreshLayout swipeRefreshLayout;

    public LinearLayout toolbarll,bottomMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loadfilter_fragments);

        View rootView = findViewById(android.R.id.content);
        rootView.setBackgroundColor(Color.WHITE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        InitView();

        String userId = getIntent().getStringExtra("userId");
        String KisanmobileNumber = getIntent().getStringExtra("kisanmobileNumber");
        String animalId = getIntent().getStringExtra("animalId");
        String animalno = getIntent().getStringExtra("animalNumber");
        String animalType = getIntent().getStringExtra("animalType");

        String BijdanId = getIntent().getStringExtra("bijdanId");

        Log.d("LoadFilterFragments", "kisanmobileNumber: " + KisanmobileNumber);
        Log.d("LoadFilterFragments", "animalId: " + animalId);
        Log.d("LoadFilterFragments", "animalNumber: " + animalno);
        Log.d("LoadFilterFragments", "animalType: " + animalType);

        boolean DoodhLeaderboardScreen = getIntent().getBooleanExtra("DoodhLeaderboard_fragment", false);
        boolean BullOptionScreen = getIntent().getBooleanExtra("BullOption_fragment", false);
        boolean PashuGarbhavatiTbl = getIntent().getBooleanExtra("PashuGarbhavati_fragment", false);
        boolean GarbhavatiFormScreen = getIntent().getBooleanExtra("GarbhavatiForm_fragment", false);
        boolean GarbhavatiFormScreen_update = getIntent().getBooleanExtra("GarbhavatiForm_fragment_update", false);
        boolean FreeBijdanListScreen = getIntent().getBooleanExtra("FreeBijdan_fragment", false);
        boolean KisanMadatScreen = getIntent().getBooleanExtra("KisanMadat_fragment", false);
        boolean PashuUpdateScreen = getIntent().getBooleanExtra("PashuUpdate_fragment", false);
        boolean PashuVyapariScreen = getIntent().getBooleanExtra("PashuVyapari_fragment", false);

      if (DoodhLeaderboardScreen) {
          title.setText(R.string.str_doodh_leaderboard);
          DoodhLeaderboard_fragment doodhLeaderboard_fragment = new DoodhLeaderboard_fragment();
          replaceFragment(doodhLeaderboard_fragment);
          resetOtherButtonColors();
      } else if (BullOptionScreen) {
          title.setText(R.string.title_bull);
          BullOption_fragment bullOption_fragment = new BullOption_fragment();
          replaceFragment(bullOption_fragment);
          resetOtherButtonColors();
      } else if (FreeBijdanListScreen) {
          title.setText(R.string.title_free_bijdan);
          FreeBijdan_fragment freeBijdan_fragment = new FreeBijdan_fragment();
          replaceFragment(freeBijdan_fragment);
          resetOtherButtonColors();
      } else if (KisanMadatScreen) {
          title.setText(R.string.kisan_madat);
          KisanMadat_fragment kisanMadat_fragment = new KisanMadat_fragment();
          replaceFragment(kisanMadat_fragment);
          resetOtherButtonColors();
      } else if (PashuGarbhavatiTbl) {
          title.setText(R.string.title_garbhavati);
          PashuGarbhavati_fragment pashuGarbhavati_fragment = new PashuGarbhavati_fragment();
          replaceFragment(pashuGarbhavati_fragment);
          resetOtherButtonColors();
      } else if (GarbhavatiFormScreen) {
          GarbhavatiForm_fragment garbhavatiForm_fragment = new GarbhavatiForm_fragment();
          toolbarll.setVisibility(GONE);
          bottomMenu.setVisibility(GONE);
          Bundle bundle = new Bundle();
          bundle.putBoolean("origionalForm", true);
          bundle.putString("userId", userId);
          bundle.putString("kisanmobileNumber", KisanmobileNumber);
          bundle.putString("animalId", animalId);
          bundle.putString("animalNumber", animalno);
          bundle.putString("animalType", animalType);
          garbhavatiForm_fragment.setArguments(bundle);
          replaceFragment(garbhavatiForm_fragment);
          swipeRefreshLayout.setRefreshing(false);
          resetOtherButtonColors();
      } else if (GarbhavatiFormScreen_update) {
          GarbhavatiForm_fragment garbhavatiForm_fragment = new GarbhavatiForm_fragment();
          toolbarll.setVisibility(GONE);
          bottomMenu.setVisibility(GONE);
          Bundle bundle = new Bundle();
          bundle.putBoolean("initialForm", true);
          bundle.putString("bijdanId", BijdanId);
          bundle.putString("kisanmobileNumber", KisanmobileNumber);
          bundle.putString("animalId", animalId);
          bundle.putString("animalNumber", animalno);
          bundle.putString("animalType", animalType);
          garbhavatiForm_fragment.setArguments(bundle);
          replaceFragment(garbhavatiForm_fragment);
          swipeRefreshLayout.setRefreshing(false);
          resetOtherButtonColors();
      } else if (PashuUpdateScreen) {
          title.setText(R.string.str_pashu_update);
          bottomMenu.setVisibility(GONE);
          PashuUpdateOption_fragment pashuUpdateOption_fragment = new PashuUpdateOption_fragment();
          replaceFragment(pashuUpdateOption_fragment);
          resetOtherButtonColors();
      }  else if (PashuVyapariScreen) {
          title.setText(R.string.str_pashu_vyapari);
          PashuVyapari_fragment pashuVyapari_fragment = new PashuVyapari_fragment();
          replaceFragment(pashuVyapari_fragment);
          resetOtherButtonColors();
      }
    }

    public void InitView() {
        mainContainer = findViewById(R.id.mainContainer);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);

        title = findViewById(R.id.title);
        back = findViewById(R.id.back);

        toolbarll = findViewById(R.id.toolbarll);
        bottomMenu = findViewById(R.id.bottomMenu);

        iconbutton1 = findViewById(R.id.iconbutton1);

        btn1txt = findViewById(R.id.btn1txt);
        btn2txt = findViewById(R.id.btn2txt);
        btn3txt = findViewById(R.id.btn3txt);

        cardView = findViewById(R.id.cardView);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setRefreshing(false);
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.mainContainer, fragment);
        transaction.commit();

        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(false);
        }
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
                    }
                }
            }, 2000);
        } else {
            Intent i = new Intent(LoadFilterFragments.this, MainActivity.class);
            i.putExtra("SELECT_HOME_FRAGMENT", true);
            startActivity(i);
            finish();
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
