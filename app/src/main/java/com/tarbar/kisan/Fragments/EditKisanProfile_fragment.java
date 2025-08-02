package com.tarbar.kisan.Fragments;

import static com.tarbar.kisan.Helper.constant.DELETE_PROFILE_PICTURE;
import static com.tarbar.kisan.Helper.constant.FETCH_PROFILE;
import static com.tarbar.kisan.Helper.constant.GET_STATES;
import static com.tarbar.kisan.Helper.constant.PROFILE_IMGAE_PATH;
import static com.tarbar.kisan.Helper.constant.UPDATE_PROFILE;

import com.tarbar.kisan.Activities.LoginActivity;
import com.tarbar.kisan.Helper.ApiUtils;
import com.tarbar.kisan.Helper.CommonMethods;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
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
import com.tarbar.kisan.Activities.Checking;
import com.tarbar.kisan.Activities.MainActivity;
import com.tarbar.kisan.Activities.RegisterActivity;
import com.tarbar.kisan.Activities.SplashActivity;
import com.tarbar.kisan.Helper.CommonMethods;
import com.tarbar.kisan.Helper.Iconstant;
import com.tarbar.kisan.Helper.KeyboardUtils;
import com.tarbar.kisan.Helper.SharedPreferenceManager;
import com.tarbar.kisan.Helper.VolleyMultipartRequest;
import com.tarbar.kisan.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditKisanProfile_fragment extends Fragment {
    private SharedPreferenceManager sharedPrefMgr;
    String userid;
    private static final int GALLERY_REQUEST_CODE = 123;
    private static final int CAMERA_PERMISSION_REQUEST = 15;
    ProgressDialog dialog;
    private byte[] imageData;
    private Bitmap bitmap;
    EditText name, fathername, surname, MobileNumber, caste,state;
    AutoCompleteTextView jilha,tahsil,village;
    Button submit;
    TextView toolbarTitle;
    ImageView back;
    CircleImageView profilepicture;
    ImageView kisanImg,choosepicture,choosepicture2;
    String enquiry;
    String strName, strCaste, strFatherName,strSurName,strJilha,strTahsil,strVillage;
    TextView txtMobileNumber,txtKisanName,txtFatherName,txtKisanCaste,txtState,txtKisanJilha,txtKisanTahsil,txtKisanVillage;
    LinearLayout llmobilenumber,llkisanname,llfathername,llcaste,llstate;
    ConstraintLayout lljilha,lltahsil,llvillage;
    boolean initialForm,origionalForm;
    String jilhaId,tahsilId,villageId;
    private ProgressBar progressBar;
    private CommonMethods commonMethods;
    private List<HashMap<String, String>> dataList = new ArrayList<>();
    private ArrayList<String> states = new ArrayList<>();
    private ArrayList<String> districts = new ArrayList<>();
    private ArrayList<String> tahsils = new ArrayList<>();
    private ArrayList<String> villages = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_kisan_account, container, false);
        view.setBackgroundColor(Color.WHITE);
        sharedPrefMgr = new SharedPreferenceManager(getContext());
        sharedPrefMgr.connectDB();
        userid = sharedPrefMgr.getString(Iconstant.userid);
        enquiry = sharedPrefMgr.getString(Iconstant.enquiry_whatsapp_number);
        sharedPrefMgr.closeDB();

        commonMethods = new CommonMethods();

        initialForm = getArguments() != null && getArguments().getBoolean("initialForm", false);
        origionalForm = getArguments() != null && getArguments().getBoolean("origionalForm", false);

        name = view.findViewById(R.id.name);
        surname = view.findViewById(R.id.surname);
        MobileNumber = view.findViewById(R.id.MobileNumber);
        caste = view.findViewById(R.id.caste);
        submit = view.findViewById(R.id.submit);
        back = view.findViewById(R.id.back);
        profilepicture = view.findViewById(R.id.profilepicture);
        kisanImg = view.findViewById(R.id.kisanImg);
        choosepicture = view.findViewById(R.id.choosepicture);
        choosepicture2 = view.findViewById(R.id.choosepicture2);
        toolbarTitle = view.findViewById(R.id.title);
        toolbarTitle.setText(R.string.title_profile);

        fathername = view.findViewById(R.id.fathername);
        jilha = view.findViewById(R.id.jilha);
        tahsil = view.findViewById(R.id.tahsil);
        village = view.findViewById(R.id.village);
        state = view.findViewById(R.id.state);
        jilha.setTextSize(14);
        tahsil.setTextSize(14);
        village.setTextSize(14);
//        state.setText("Maharashtra");
        txtMobileNumber = view.findViewById(R.id.txtMobileNumber);
        txtKisanName = view.findViewById(R.id.txtKisanName);
        txtFatherName = view.findViewById(R.id.txtFatherName);
        txtKisanCaste = view.findViewById(R.id.txtKisanCaste);
        txtState = view.findViewById(R.id.txtState);
        txtKisanJilha = view.findViewById(R.id.txtKisanJilha);
        txtKisanTahsil = view.findViewById(R.id.txtKisanTahsil);
        txtKisanVillage = view.findViewById(R.id.txtKisanVillage);

        llmobilenumber = view.findViewById(R.id.llmobilenumber);
        llkisanname = view.findViewById(R.id.llkisanname);
        llfathername = view.findViewById(R.id.llfathername);
        llcaste = view.findViewById(R.id.llcaste);
        llstate = view.findViewById(R.id.llstate);

        KeyboardUtils.setHindiKeyboard(name);
        KeyboardUtils.setHindiKeyboard(fathername);
        KeyboardUtils.setHindiKeyboard(caste);
        KeyboardUtils.setHindiKeyboard(caste);

        if (initialForm) {
            txtMobileNumber.setVisibility(View.GONE);
            txtKisanName.setVisibility(View.GONE);
            txtKisanCaste.setVisibility(View.GONE);
            txtState.setVisibility(View.GONE);
            llmobilenumber.setVisibility(View.GONE);
            llkisanname.setVisibility(View.GONE);
            llcaste.setVisibility(View.GONE);
            llstate.setVisibility(View.GONE);
        } else if (origionalForm) {
            txtMobileNumber.setVisibility(View.VISIBLE);
            txtKisanName.setVisibility(View.VISIBLE);
            txtKisanCaste.setVisibility(View.VISIBLE);
            txtState.setVisibility(View.VISIBLE);
            llmobilenumber.setVisibility(View.VISIBLE);
            llkisanname.setVisibility(View.VISIBLE);
            llcaste.setVisibility(View.VISIBLE);
            llstate.setVisibility(View.VISIBLE);
        }

        fetchStates();
        getKisanProfile();

        jilha.setOnClickListener(v -> {
            String selectedState = state.getText().toString();
            if (selectedState.isEmpty()) {
                Toast.makeText(getContext(), "Please select a state first", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean stateFound = false;
            for (HashMap<String, String> item : dataList) {
                if (item.get("state").equals(selectedState)) {
                    stateFound = true;
                    break;
                }
            }

            if (!stateFound) {
                Toast.makeText(getContext(), "Selected state not found", Toast.LENGTH_SHORT).show();
                return;
            }

            districts.clear();
            for (HashMap<String, String> item : dataList) {
                if (item.get("state").equals(selectedState) && !districts.contains(item.get("district"))) {
                    districts.add(item.get("district"));
                }
            }

            ArrayAdapter<String> districtAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, districts);
            jilha.setAdapter(districtAdapter);
            jilha.showDropDown();
        });

        jilha.setOnItemClickListener((parent, v, position, id) -> {
            String selectedDistrict = districts.get(position);
            jilha.setText(selectedDistrict);
            showTahsils();
        });

        tahsil.setOnClickListener(v -> {
            String selectedDistrict = jilha.getText().toString();
            if (selectedDistrict.isEmpty()) {
                Toast.makeText(getContext(), "Please select a district first", Toast.LENGTH_SHORT).show();
                return;
            }

            tahsils.clear();
            for (HashMap<String, String> item : dataList) {
                if (item.get("district").equals(selectedDistrict) && !tahsils.contains(item.get("tehshil"))) {
                    tahsils.add(item.get("tehshil"));
                }
            }

            ArrayAdapter<String> tahsilAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, tahsils);
            tahsil.setAdapter(tahsilAdapter);
            tahsil.showDropDown();
        });

        tahsil.setOnItemClickListener((parent, v, position, id) -> {
            String selectedTahsil = tahsils.get(position);
            tahsil.setText(selectedTahsil);
            showVillages();
        });

        village.setOnClickListener(v -> {
            String selectedTahsil = tahsil.getText().toString();
            if (selectedTahsil.isEmpty()) {
                Toast.makeText(getContext(), "Please select a tahsil first", Toast.LENGTH_SHORT).show();
                return;
            }

            villages.clear();
            for (HashMap<String, String> item : dataList) {
                if (item.get("tehshil").equals(selectedTahsil) && !villages.contains(item.get("village"))) {
                    villages.add(item.get("village"));
                }
            }

            ArrayAdapter<String> villageAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, villages);
            village.setAdapter(villageAdapter);
            village.showDropDown();
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.click_animation);
                view.startAnimation(animation);
                strName = name.getText().toString();
                strFatherName = fathername.getText().toString();
//                strSurName = surname.getText().toString();
                strCaste = caste.getText().toString();
                String selectedJilha = jilha.getText().toString();
                String selectedTahsil = tahsil.getText().toString();
                String selectedVillage = village.getText().toString();

                if (strName.isEmpty()) {
                    name.setError(getString(R.string.hint_enter_name));
                    name.setFocusable(true);
                } else if (strFatherName.isEmpty()) {
                    fathername.setError(getString(R.string.hint_enter_father_name));
                    fathername.setFocusable(true);
//                } else if (strSurName.isEmpty()) {
//                    surname.setError(getString(R.string.hint_enter_surname));
//                    surname.setFocusable(true);
                } else if (strCaste.isEmpty()) {
                    caste.setError(getString(R.string.hint_caste));
                    caste.setFocusable(true);
                } else if (selectedJilha.equals(getString(R.string.hint_jilha))) {
                    Toast.makeText(getContext(), getString(R.string.hint_jilha), Toast.LENGTH_SHORT).show();
                } else if (selectedTahsil.equals(getString(R.string.hint_tahsil))) {
                    Toast.makeText(getContext(), getString(R.string.hint_tahsil), Toast.LENGTH_SHORT).show();
                } else if (selectedVillage.equals(getString(R.string.hint_village))) {
                    Toast.makeText(getContext(), getString(R.string.hint_village), Toast.LENGTH_SHORT).show();
                } else {
                     UpdateUserProfile(imageData);
                }
            }
        });

        choosepicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.click_animation);
                v.startAnimation(animation);
                 allOptions();
            }
        });

        choosepicture2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.click_animation);
                v.startAnimation(animation);
                 selectedOptions();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.click_animation);
                v.startAnimation(animation);
                if (initialForm) {
                    Intent i = new Intent(requireContext(), SplashActivity.class);
                    startActivity(i);
                    requireActivity().finish();
                } else if (origionalForm) {
                    Intent i = new Intent(requireContext(), MainActivity.class);
                    i.putExtra("SELECT_PROFILE_FRAGMENT", true);
                    startActivity(i);
                    requireActivity().finish();
                } else {
                    requireActivity().getOnBackPressedDispatcher().onBackPressed();
                }
            }
        });
        return view;
    }

//    Profile Image

    private void allOptions() {
        final CharSequence[] options = {
                getString(R.string.gallary_option),
                getString(R.string.camera_option),
                getString(R.string.remove_img_option)
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getString(R.string.choose_img));
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("गैलरी")) {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    startActivityForResult(intent, GALLERY_REQUEST_CODE);
                } else if (options[item].equals("कैमरा")) {
                    if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)
                            == PackageManager.PERMISSION_GRANTED) {
                        openCamera();
                    } else {
                        ActivityCompat.requestPermissions(requireActivity(),
                                new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST);
                    }
                } else if (options[item].equals("हटाएं")) {
                    deletePic();
                }
            }
        });
        builder.show();
    }

    private void selectedOptions() {
        final CharSequence[] options = {
                getString(R.string.gallary_option),
                getString(R.string.camera_option)
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getString(R.string.choose_img));
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("गैलरी")) {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    startActivityForResult(intent, GALLERY_REQUEST_CODE);
                } else if (options[item].equals("कैमरा")) {
                    if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)
                            == PackageManager.PERMISSION_GRANTED) {
                        openCamera();
                    } else {
                        ActivityCompat.requestPermissions(requireActivity(),
                                new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST);
                    }
                }
            }
        });
        builder.show();
    }

    private void setImageToImageView(Bitmap bitmap) {
        profilepicture.setVisibility(View.VISIBLE);
        profilepicture.setImageBitmap(bitmap);
        profilepicture.invalidate();
    }

    private byte[] getFileDataFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            startActivityForResult(cameraIntent, CAMERA_PERMISSION_REQUEST);
        } else {
            Toast.makeText(requireContext(), getString(R.string.camera_not_available), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == Activity.RESULT_OK && data != null) {
            if (requestCode == GALLERY_REQUEST_CODE) {
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null) {
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), selectedImageUri);
                        setImageToImageView(bitmap);
                        imageData = getFileDataFromBitmap(bitmap);
                        Log.d("ImageSelection", "Gallery image selected & converted successfully. Size: " + imageData.length);
                    } catch (IOException e) {
                        Log.e("ImageSelection", "Error processing gallery image", e);
                    }
                }
            } else if (requestCode == CAMERA_PERMISSION_REQUEST) {
                Bundle extras = data.getExtras();
                if (extras != null) {
                    bitmap = (Bitmap) extras.get("data");
                    setImageToImageView(bitmap);
                    imageData = getFileDataFromBitmap(bitmap);
                    Log.d("ImageSelection", "Camera image selected & converted successfully. Size: " + imageData.length);
                }
            }
        } else {
            Toast.makeText(requireContext(), getString(R.string.cant_select_img), Toast.LENGTH_SHORT).show();

        }
    }

    private void deletePic() {
        dialog = new ProgressDialog(requireContext());
        dialog.setMessage("Removing Profile Picture");
        dialog.setCancelable(false);
        dialog.show();

        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, DELETE_PROFILE_PICTURE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            dialog.dismiss();
                            Log.d("Checkk1","");
                            JSONObject jsonObject1 = new JSONObject(response);
                            String code = jsonObject1.getString("code");
                            if (code.equals("1")) {
                                Log.d("Checkk","");
                                dialog.dismiss();
                                Intent i = new Intent(getContext(), EditKisanProfile_fragment.class);
                                startActivity(i);
                            }
                        } catch (JSONException e) {
                            dialog.dismiss();
                            if (e instanceof JSONException) {
                                Toast.makeText(getContext(), R.string.json_parsing_error, Toast.LENGTH_SHORT).show();
                            } else {
                                Log.d("CheckException",""+e);
                                Toast.makeText(getContext(), R.string.generic_error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dialog.dismiss();
                        if (error instanceof TimeoutError) {
                            Toast.makeText(getContext(), R.string.timeout_error, Toast.LENGTH_SHORT).show();
                        } else if (error instanceof NoConnectionError) {
                            Toast.makeText(getContext(), R.string.no_connection_error, Toast.LENGTH_SHORT).show();
                        } else if (error instanceof AuthFailureError) {
                            Toast.makeText(getContext(), R.string.auth_failure_error, Toast.LENGTH_SHORT).show();
                        } else if (error instanceof ServerError) {
                            Log.d("ServerError",""+error);
                            Toast.makeText(getContext(), R.string.server_error, Toast.LENGTH_SHORT).show();
                        } else if (error instanceof NetworkError) {
                            Toast.makeText(getContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
                        } else if (error instanceof ParseError) {
                            Toast.makeText(getContext(), R.string.parse_error, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), R.string.unknown_error, Toast.LENGTH_SHORT).show();
                        }
                        Log.d("Check", "" + error.getMessage());
                    }
                }
        ) {
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

    private void fetchStates() {
        String apiUrl = GET_STATES;
        commonMethods.getState(requireContext(), apiUrl, new CommonMethods.OnStateResponseListener() {
            @Override
            public void onSuccess(ArrayList<HashMap<String, String>> stateList) {
                dataList = stateList;

                for (HashMap<String, String> item : dataList) {
                    if (!states.contains(item.get("state"))) {
                        states.add(item.get("state"));
                    }
                }
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(requireContext(), "Failed to fetch states: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDistricts() {
        String selectedState = state.getText().toString();
        if (selectedState.isEmpty()) {
            Toast.makeText(getContext(), "Please select a state first", Toast.LENGTH_SHORT).show();
            return;
        }

        districts.clear();
        for (HashMap<String, String> item : dataList) {
            if (item.get("state").equals(selectedState) && !districts.contains(item.get("district"))) {
                districts.add(item.get("district"));
            }
        }

        ArrayAdapter<String> districtAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, districts);
        jilha.setAdapter(districtAdapter);
        jilha.showDropDown();
    }

    private void showTahsils() {
        String selectedDistrict = jilha.getText().toString();
        if (selectedDistrict.isEmpty()) {
            Toast.makeText(getContext(), "Please select a district first", Toast.LENGTH_SHORT).show();
            return;
        }

        tahsils.clear();
        for (HashMap<String, String> item : dataList) {
            if (item.get("district").equals(selectedDistrict) && !tahsils.contains(item.get("tehshil"))) {
                tahsils.add(item.get("tehshil"));
            }
        }

        ArrayAdapter<String> tahsilAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, tahsils);
        tahsil.setAdapter(tahsilAdapter);
        tahsil.showDropDown();
    }

    private void showVillages() {
        String selectedTahsil = tahsil.getText().toString();
        if (selectedTahsil.isEmpty()) {
            Toast.makeText(getContext(), "Please select a tahsil first", Toast.LENGTH_SHORT).show();
            return;
        }

        villages.clear();
        for (HashMap<String, String> item : dataList) {
            if (item.get("tehshil").equals(selectedTahsil) && !villages.contains(item.get("village"))) {
                villages.add(item.get("village"));
            }
        }

        ArrayAdapter<String> villageAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, villages);
        village.setAdapter(villageAdapter);
        village.showDropDown();
    }

    private void getKisanProfile() {
        dialog = new ProgressDialog(getActivity());
        dialog.setMessage(getString(R.string.getting_data));
        dialog.setCancelable(false);
        dialog.show();
        String url = FETCH_PROFILE;
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("getKisanProfile_response", "" + response);
                        dialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("status");
                            String message = jsonObject.getString("message");
                            if (status.equalsIgnoreCase("success")) {
                                JSONArray dataArray = jsonObject.getJSONArray("data");
                                if (dataArray.length() > 0) {
                                    JSONObject userData = dataArray.getJSONObject(0);
                                    String KisanName = userData.getString("name");
                                    String KisanFatherName = userData.getString("father_name");
//                                    String SurnameName = userData.getString("surname");
                                    String KisanMobile = userData.getString("mobile");
                                    String KisanCaste = userData.getString("caste");
                                    String KisanJilha = userData.getString("jilha");
                                    String KisanTahsil = userData.getString("tahsil");
                                    String KisanVillage = userData.getString("village");
                                    String KisanState = userData.getString("state");
                                    String profilePic = userData.getString("profile_pic");

                                    name.setText(KisanName == null || KisanName.equalsIgnoreCase("Null") ? "" : KisanName);
                                    fathername.setText(KisanFatherName == null || KisanFatherName.equalsIgnoreCase("Null") ? "" : KisanFatherName);
//                                    surname.setText(SurnameName == null || SurnameName.equalsIgnoreCase("Null") ? "" : SurnameName);
                                    MobileNumber.setText(KisanMobile == null || KisanMobile.equalsIgnoreCase("Null") ? "" : KisanMobile);
                                    caste.setText(KisanCaste == null || KisanCaste.equalsIgnoreCase("Null") ? "" : KisanCaste);
                                    state.setText(KisanState == null || KisanState.equalsIgnoreCase("Null") ? "" : KisanState);

                                    setAutoCompleteTextViewSelection(jilha, KisanJilha);
                                    setAutoCompleteTextViewSelection(tahsil, KisanTahsil);
                                    setAutoCompleteTextViewSelection(village, KisanVillage);

                                    String imageUrl = PROFILE_IMGAE_PATH + profilePic;
                                    Picasso.get()
                                            .load(imageUrl)
                                            .into(profilepicture, new com.squareup.picasso.Callback() {
                                                @Override
                                                public void onSuccess() {
                                                    dialog.dismiss();
                                                    profilepicture.setVisibility(View.VISIBLE);
                                                    choosepicture.setVisibility(View.VISIBLE);
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
                                                    choosepicture2.setVisibility(View.VISIBLE);
                                                    profilepicture.setVisibility(View.GONE);
                                                }
                                            });
                                } else {
                                    dialog.dismiss();
                                    Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                dialog.dismiss();
                                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            dialog.dismiss();
                            if (e instanceof JSONException) {
                                Toast.makeText(getContext(), R.string.json_parsing_error, Toast.LENGTH_SHORT).show();
                            } else {
                                Log.d("CheckException",""+e);
                                Toast.makeText(getContext(), R.string.generic_error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dialog.dismiss();
                        if (error instanceof TimeoutError) {
                            Toast.makeText(getContext(), R.string.timeout_error, Toast.LENGTH_SHORT).show();
                        } else if (error instanceof NoConnectionError) {
                            Toast.makeText(getContext(), R.string.no_connection_error, Toast.LENGTH_SHORT).show();
                        } else if (error instanceof AuthFailureError) {
                            Toast.makeText(getContext(), R.string.auth_failure_error, Toast.LENGTH_SHORT).show();
                        } else if (error instanceof ServerError) {
                            Log.d("ServerError",""+error);
                            Toast.makeText(getContext(), R.string.server_error, Toast.LENGTH_SHORT).show();
                        } else if (error instanceof NetworkError) {
                            Toast.makeText(getContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
                        } else if (error instanceof ParseError) {
                            Toast.makeText(getContext(), R.string.parse_error, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), R.string.unknown_error, Toast.LENGTH_SHORT).show();
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

//    private void UpdateUserProfile(final byte[] imageData) {
//        dialog = new ProgressDialog(getContext());
//        dialog.setMessage(getString(R.string.saving_data));
//        dialog.setCancelable(false);
//        dialog.show();
//
//        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, UPDATE_PROFILE,
//                new Response.Listener<NetworkResponse>() {
//                    @Override
//                    public void onResponse(NetworkResponse response) {
//                        dialog.dismiss();
//                        try {
//                            JSONObject jsonObject = new JSONObject(new String(response.data, "UTF-8"));
//                            String status = jsonObject.getString("status");
//                            String message = jsonObject.getString("message");
//
//                            Intent intent = new Intent(requireContext(), Checking.class);
//                            intent.putExtra("isVerified", true);
//                            intent.putExtra("message", message);
//                            intent.putExtra("nextActivity", MainActivity.class);
//                            intent.putExtra("SELECT_PROFILE_FRAGMENT", true);
//                            startActivity(intent);
//
//                        } catch (Exception e) {
//                            dialog.dismiss();
//                            Log.e("JSONException", e.getMessage(), e);
//                            Toast.makeText(getContext(), R.string.json_parsing_error, Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        dialog.dismiss();
//                        if (error instanceof TimeoutError) {
//                            Toast.makeText(getContext(), R.string.timeout_error, Toast.LENGTH_SHORT).show();
//                        } else if (error instanceof NoConnectionError) {
//                            Toast.makeText(getContext(), R.string.no_connection_error, Toast.LENGTH_SHORT).show();
//                        } else if (error instanceof AuthFailureError) {
//                            Toast.makeText(getContext(), R.string.auth_failure_error, Toast.LENGTH_SHORT).show();
//                        } else if (error instanceof ServerError) {
//                            Log.d("ServerError",""+error);
//                            Toast.makeText(getContext(), R.string.server_error, Toast.LENGTH_SHORT).show();
//                        } else if (error instanceof NetworkError) {
//                            Toast.makeText(getContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
//                        } else if (error instanceof ParseError) {
//                            Toast.makeText(getContext(), R.string.parse_error, Toast.LENGTH_SHORT).show();
//                        } else {
//                            Toast.makeText(getContext(), R.string.unknown_error, Toast.LENGTH_SHORT).show();
//                        }
//                        Log.d("Checkkjhljh", "" + error.getMessage());
//                    }
//                }) {
//
//            @Override
//            public String getBodyContentType() {
//                return "multipart/form-data; charset=UTF-8";
//            }
//
//            @Override
//            protected String getParamsEncoding() {
//                return "UTF-8";
//            }
//
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> params = new HashMap<>();
//                params.put("userid", userid);
//                params.put("name", name.getText().toString().trim());
//                params.put("father_name", fathername.getText().toString().trim());
//                params.put("state", "KJLL");
//                params.put("jilha", "reg");
//                params.put("tahsil", "rdggh");
//                params.put("village", "rghyeye");
//                params.put("caste", caste.getText().toString().trim());
//                params.put("mobile", MobileNumber.getText().toString().trim());
//
//                Log.d("ProfileUpdate", "Params: " + params.toString());
//                return params;
//            }
//
//            @Override
//            protected Map<String, DataPart> getByteData() {
//                Map<String, DataPart> params = new HashMap<>();
//                long imagename = System.currentTimeMillis();
//
//                if (imageData != null && imageData.length > 0) {
//                    params.put("profile_pic", new DataPart(imagename + ".png", imageData));
//                    Log.d("API_Image_Upload", "Image uploaded. Size: " + imageData.length);
//                } else {
//                    Log.e("API_Image_Upload", "No image uploaded.");
//                }
//
//                return params;
//            }
//        };
//        volleyMultipartRequest.setRetryPolicy(ApiUtils.DEFAULT_RETRY_POLICY);
//        Volley.newRequestQueue(getContext()).add(volleyMultipartRequest);
//    }


    private void UpdateUserProfile(final byte[] imageData) {
        dialog = new ProgressDialog(getContext());
        dialog.setMessage(getString(R.string.saving_data));
        dialog.setCancelable(false);
        dialog.show();

        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, UPDATE_PROFILE,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        Log.e("JSONException", "" + response);
                        dialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(new String(response.data, "UTF-8"));
                            String status = jsonObject.getString("status");
                            String message = jsonObject.getString("message");

                            Intent intent = new Intent(requireContext(), Checking.class);
                            intent.putExtra("isVerified", true);
                            intent.putExtra("message", message);
                            intent.putExtra("nextActivity", MainActivity.class);
                            intent.putExtra("SELECT_PROFILE_FRAGMENT", true);
                            startActivity(intent);
                            requireActivity().finish();

                        } catch (UnsupportedEncodingException e) {
                            dialog.dismiss();
                            Log.e("EncodingException", e.getMessage(), e);
                            Toast.makeText(getContext(), R.string.encoding_error, Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            dialog.dismiss();
                            Log.e("JSONException", e.getMessage(), e);
                            Toast.makeText(getContext(), R.string.json_parsing_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dialog.dismiss();
                        Log.e("Volley Error", "" + error);
                        String errorMessage = error.getMessage() == null ? getString(R.string.unknown_error) : error.getMessage();
                        Toast.makeText(getContext(), "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("userid", userid);
                params.put("name", name.getText().toString().trim());
                params.put("fathername", fathername.getText().toString().trim());
                params.put("state", state.getText().toString().trim());
                params.put("jilha", jilha.getText().toString().trim());
                params.put("tahsil", tahsil.getText().toString().trim());
                params.put("village", village.getText().toString().trim());
                params.put("caste", caste.getText().toString().trim());
                params.put("mobile", MobileNumber.getText().toString().trim());
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();
                if (imageData != null && imageData.length > 0) {
                    params.put("profile_pic", new DataPart(imagename + ".png", imageData));
                }
                return params;
            }
        };

        volleyMultipartRequest.setRetryPolicy(ApiUtils.DEFAULT_RETRY_POLICY);
        Volley.newRequestQueue(getContext()).add(volleyMultipartRequest);
    }


    private void setAutoCompleteTextViewSelection(AutoCompleteTextView autoCompleteTextView, String selectedValue) {
        if (selectedValue != null && !selectedValue.equalsIgnoreCase("Null") && !selectedValue.isEmpty()) {
            ArrayAdapter<String> adapter = (ArrayAdapter<String>) autoCompleteTextView.getAdapter();
            if (adapter != null) {
                int position = adapter.getPosition(selectedValue);
                if (position >= 0) {
                    autoCompleteTextView.setText(adapter.getItem(position), false);
                }
            } else {
                Log.d("AutoComplete", "Adapter is null for AutoCompleteTextView.");
            }
        } else {
            autoCompleteTextView.setText("");
        }
    }
}
