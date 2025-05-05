package com.tarbar.kisan.Activities;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.tarbar.kisan.Fragments.ByatAddition_fragment;
import com.tarbar.kisan.Fragments.CommingSoon_fragment;
import com.tarbar.kisan.Fragments.CompanyProfile_fragment;
import com.tarbar.kisan.Fragments.DoodhAddition_fragment;
import com.tarbar.kisan.Fragments.EditAnimalProfile_fragment;
import com.tarbar.kisan.Fragments.EditKisanProfile_fragment;
import com.tarbar.kisan.Fragments.JoinAnimal_fragment;

import com.tarbar.kisan.Fragments.Kisan_SevaAnurodh_fragment;
import com.tarbar.kisan.Fragments.ResetPassword_fragment;
import com.tarbar.kisan.R;

import android.util.Log;
import android.widget.TextView;

public class LoadFragments extends AppCompatActivity {
    FrameLayout mainContainer;
    TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_fragments);

        View rootView = findViewById(android.R.id.content);
        rootView.setBackgroundColor(Color.WHITE);

        InitView();

        String KisanId = getIntent().getStringExtra("KisanId");
        String animalId = getIntent().getStringExtra("animalId");
        String animalno = getIntent().getStringExtra("animalNumber");
        String animalType = getIntent().getStringExtra("animalType");
        String byatcnt = getIntent().getStringExtra("byatcnt");
        String kisanMobileNumber = getIntent().getStringExtra("kisanmobilenumber");
        String latestbyat = getIntent().getStringExtra("latest_byat");
        String lastMilk = getIntent().getStringExtra("lastMilk");
        String byatId = getIntent().getStringExtra("byatId");
        String doodhId = getIntent().getStringExtra("doodhId");
        String animalBirthDate = getIntent().getStringExtra("animal_birth_date");

        Log.d("LoadFragments", "KisanId: " + KisanId);
        Log.d("LoadFragments", "animalId: " + animalId);
        Log.d("LoadFragments", "animalNumber: " + animalno);
        Log.d("LoadFragments", "animalType: " + animalType);
        Log.d("LoadFragments", "byatcnt: " + byatcnt);
        Log.d("LoadFragments", "kisanmobilenumber: " + kisanMobileNumber);
        Log.d("LoadFragments", "latest_byat: " + latestbyat);
        Log.d("LoadFragments", "latest_doodh: " + lastMilk);
        Log.d("LoadFragments", "byatId: " + byatId);
        Log.d("LoadFragments", "doodhId: " + doodhId);
        Log.d("LoadFragments", "animalBirthDate: " + animalBirthDate);

        boolean FillProfileDetailsString = getIntent().getBooleanExtra("fillProfileDetails", false);
        boolean ComminSoonString = getIntent().getBooleanExtra("Fragment_CommingSoon", false);
        boolean ComapnyProfileString = getIntent().getBooleanExtra("Fragment_CompanyProfile", false);
        boolean firstString = getIntent().getBooleanExtra("Fragment_JoinAnimal", false);
        boolean secondString = getIntent().getBooleanExtra("Fragment_EditAnimalProfile", false);
        boolean EditKisanProfileString = getIntent().getBooleanExtra("Fragment_EditKisanProfile", false);
        boolean KisanSevaAnurodhString = getIntent().getBooleanExtra("Fragment_KisanSevaAnurodh", false);
        boolean ResetPasswordString = getIntent().getBooleanExtra("Fragment_ResetPassword", false);
        boolean ByatAdditionFormString = getIntent().getBooleanExtra("Fragment_ByatAddition", false);
        boolean ByatProfileString = getIntent().getBooleanExtra("Fragment_ByatProfile", false);

        boolean DoodhAdditionString = getIntent().getBooleanExtra("Fragment_AddDoodh", false);
        boolean DoodhProfileString = getIntent().getBooleanExtra("Fragment_DoodhProfile", false);

        if (firstString) {
            replaceFragment(new JoinAnimal_fragment());
        } else if (secondString) {
            EditAnimalProfile_fragment editAnimalProfileFragment = new EditAnimalProfile_fragment();
            Bundle bundle = new Bundle();
            bundle.putString("animalId", animalId);
            editAnimalProfileFragment.setArguments(bundle);
            replaceFragment(editAnimalProfileFragment);
        } else if (ComapnyProfileString) {
            replaceFragment(new CompanyProfile_fragment());
        } else if (ComminSoonString) {
            replaceFragment(new CommingSoon_fragment());
        } else if (EditKisanProfileString) {
            EditKisanProfile_fragment editKisanProfileFragment = new EditKisanProfile_fragment();
            Bundle bundle = new Bundle();
            bundle.putBoolean("origionalForm", true);
            editKisanProfileFragment.setArguments(bundle);
            replaceFragment(editKisanProfileFragment);
        } else if (KisanSevaAnurodhString) {
            Kisan_SevaAnurodh_fragment kisanSevaAnurodhFragment = new Kisan_SevaAnurodh_fragment();
            replaceFragment(kisanSevaAnurodhFragment);
        } else if (ResetPasswordString) {
            replaceFragment(new ResetPassword_fragment());
        } else if (FillProfileDetailsString) {
            EditKisanProfile_fragment editKisanProfileFragment = new EditKisanProfile_fragment();
            Bundle bundle = new Bundle();
            bundle.putBoolean("initialForm", true);
            editKisanProfileFragment.setArguments(bundle);
            replaceFragment(editKisanProfileFragment);
        } else if (ByatAdditionFormString) {
            ByatAddition_fragment byatAdditionFragment = new ByatAddition_fragment();
            Bundle bundle = new Bundle();
            bundle.putBoolean("origionalForm", true);
            bundle.putString("KisanId", KisanId);
            bundle.putString("animalId", animalId);
            bundle.putString("animalNumber", animalno);
            bundle.putString("animalType", animalType);
            bundle.putString("byatcnt", byatcnt);
            bundle.putString("latest_byat", latestbyat);
            bundle.putString("kisanmobilenumber", kisanMobileNumber);
            bundle.putString("animal_birth_date", animalBirthDate);
            byatAdditionFragment.setArguments(bundle);
            replaceFragment(byatAdditionFragment);
        } else if (ByatProfileString) {
            ByatAddition_fragment byatAdditionFragment = new ByatAddition_fragment();
            Bundle bundle = new Bundle();
            bundle.putBoolean("initialForm", true);
            bundle.putString("KisanId", KisanId);
            bundle.putString("animalId", animalId);
            bundle.putString("animalNumber", animalno);
            bundle.putString("animalType", animalType);
            bundle.putString("kisanmobilenumber", kisanMobileNumber);
            bundle.putString("latest_byat", latestbyat);
            bundle.putString("byatId", byatId);
            byatAdditionFragment.setArguments(bundle);
            replaceFragment(byatAdditionFragment);
        }

        else if (DoodhAdditionString) {
            DoodhAddition_fragment byatAdditionFragment = new DoodhAddition_fragment();
            Bundle bundle = new Bundle();
//            DoodhAddition
            bundle.putBoolean("origionalForm", true);
            bundle.putString("KisanId", KisanId);
            bundle.putString("animalId", animalId);
            bundle.putString("animalNumber", animalno);
            bundle.putString("animalType", animalType);
            bundle.putString("byatcnt", byatcnt);
            bundle.putString("lastMilk", lastMilk);
            byatAdditionFragment.setArguments(bundle);
            replaceFragment(byatAdditionFragment);
        } else if (DoodhProfileString) {
            DoodhAddition_fragment byatAdditionFragment = new DoodhAddition_fragment();
            Bundle bundle = new Bundle();
            bundle.putBoolean("initialForm", true);
            bundle.putString("KisanId", KisanId);
            bundle.putString("animalId", animalId);
            bundle.putString("animalNumber", animalno);
            bundle.putString("animalType", animalType);
            bundle.putString("byatcnt", byatcnt);
            bundle.putString("lastMilk", lastMilk);
            bundle.putString("doodhId", doodhId);
            byatAdditionFragment.setArguments(bundle);
            replaceFragment(byatAdditionFragment);
        }
    }

    public void InitView() {
        mainContainer = findViewById(R.id.mainContainer);
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.mainContainer, fragment);
        transaction.commit();
    }
}
