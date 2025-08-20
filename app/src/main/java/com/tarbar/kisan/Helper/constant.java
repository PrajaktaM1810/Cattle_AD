package com.tarbar.kisan.Helper;

public class constant {

    public static String tarbar_whatsapp_number = "919405075577";
    public static String tarbar_enquiry_number = "917020921516";
    public static String tarbar_doodh_complaint_number = "917375815160";


//    Images Path
    public static final String BULL_IMAGES_PATH = "https://tarbargroup.in/assets/bull_imgs/";

    static String BASE_URL = "https://tarbargroup.in/api/";

    public static final String IMGAE_PATH = "https://tarbargroup.in/assets/images/";

    static String SEND_OTP = BASE_URL+"auth/otp_request";
    static String ANIMAL_REGISTER = BASE_URL+"animals/register_animal";
    static String HISTORY_DETAILS = BASE_URL+"animals";
    public static final String URL_OTP_VERIFICATION = BASE_URL + "ax_verify_otp.php";
    public static final String URL_RESEND_OTP = BASE_URL + "ax_resend_otp.php";
    static String prefs = "codegente";
    static String updateProfile = BASE_URL+"Users/update_profile";
    public static String getAnimalData  = BASE_URL+"api/animals";
    static String getRoleWiseData = BASE_URL+"Users/rolewise_data";
    static String postAnimalChild = BASE_URL+"animals/add_child";
    static String postUpdateAnimal = BASE_URL+"animals/update_animal";
    static String addSales = BASE_URL+"Animals/add_sale";
    static String bullDetails = BASE_URL+"animals/bull_details";
    static String DeleteAnimal = BASE_URL+"animals/delete_animal";
    static String kisanDetails = BASE_URL+"users/helperHistory";
    static String prefix = "https://tarbargroup.in/api/";

//    DetailsFragment
    static String urlVivaranHistory = BASE_URL + "animals";

    /////////////////////////////////////////////////////

//    Settings
    public static String SETTINGS = BASE_URL + "settings/app_version";
//    public static String GET_PROFILE_STATUS = BASE_URL + "auth/ax_getProfileStatus";

//    Login
    public static String LOGIN = BASE_URL + "auth/login";

//    REGISTRATION
    public static String REGISTER = BASE_URL + "auth/register";
    public static String GET_STATES = BASE_URL + "auth/getAddress";

//    VerifyOtp
    public static String VERIFY_OTP = BASE_URL + "auth/verify_otp";
    public static String RESEND_OTP = BASE_URL + "auth/otp_request";

//    Mpin
    static String SET_PIN = BASE_URL + "auth/set_pin";
    static String VERIFY_PIN = BASE_URL + "auth/verify_pin";
    static String RESET_PIN = BASE_URL + "auth/reset_pin";

//    RESET PASSWORD
   public static String RESET_PASSWORD = BASE_URL + "auth/reset_password";

//   HomeFragment
    public static String SLIDER = BASE_URL + "settings/slider";
    public static String SLIDER_IMG_PATH = "https://tarbargroup.in/assets/banner_img/";

//  Profile Fragment
    public static final String PROFILE_IMGAE_PATH =  "https://tarbargroup.in/uploads/users/";
    public static String FETCH_PROFILE = BASE_URL + "users/getUserProfile";
    public static String UPDATE_PROFILE = BASE_URL + "users/updateUserProfile";
    public static String DELETE_PROFILE_PICTURE = BASE_URL + "users/deleteProfilePicture";

//  UserRequest
    public static String GET_USER_ROLES = BASE_URL + "users/getUserRoles";
    public static String UPDATE_USER_ROLE = BASE_URL + "users/updateUserRole";

//   PASHU
    public static String GET_ANIMAL_LIST = BASE_URL + "animals/get_animal_list";
    public static String ADD_ANIMAL = BASE_URL + "animals/register_animal";
    public static String GET_ANIMAL_DATA = BASE_URL + "animals/get_animal_data";
    public static String UPDATE_ANIMAL = BASE_URL + "animals/update_animal_data";
    public static String DELETE_ANIMAL = BASE_URL + "animals/delete_animal";

    public static String GET_BYAT_LIST = BASE_URL + "animals/get_byat_list";
    public static String ADD_BYAT = BASE_URL + "animals/register_byat";
    public static String GET_BYAT_DATA = BASE_URL + "animals/get_byat_data";
    public static String UPDATE_BYAT_DATA = BASE_URL + "animals/update_byat";
    public static String DELETE_BYAT = BASE_URL + "animals/ax_delete_byat";

//  DOODH MODULE
    public static String ADD_ANIMAL_DOODH_DATA = BASE_URL + "animals/ax_add_doodh_data";
    public static String GET_DOODH_LIST = BASE_URL + "animals/ax_get_doodh_list";
    public static String GET_PARTICULAR_DOODH_DATA = BASE_URL + "animals/ax_get_doodh_data";
    public static String UPDATE_PARTICULAR_DOODH_DATA = BASE_URL + "animals/ax_update_doodh_data";
    public static String DELETE_DOODH_DATA = BASE_URL + "animals/ax_delete_doodh";
    public static String DOODH_LEADERBOARD_LIST = BASE_URL + "animals/ax_get_leaderboard_list";

//  BULL MODULE
    public static String GET_BULL_LIST = BASE_URL + "animals/getBullList";

//    PASHU GARBHAVATI MODULE
    public static String GET_BIJDAN_DETAILS = BASE_URL + "animals/get_bijadan_details";
    public static String GET_BIJDAN_DATA = BASE_URL + "animals/get_pashu_garbhavati_list";
    public static String Add_BIJDAN_DATA = BASE_URL + "animals/pashu_garbhavati_data_addition";
    public static String UPDATE_BIJDAN_DATA = BASE_URL + "animals/update_bijadan_details";
    public static String DELETE_GARBHAVATI_DATA = BASE_URL + "animals/delete_garbhavati_data";

//    FREE BIJDAN MODULE
    public static String FREE_BIJDAN_ANIMAL_LIST = BASE_URL + "animals/ax_get_animal_list_by_date_range";


//    PASHU UPDATE
    public static String GET_SELLING_LIST = BASE_URL + "animals/get_selling_list";
    public static String ADD_SELLING_DETAILS = BASE_URL + "animals/addSellingDetails";
    public static String GET_PARTICULAR_SELLING_DETAILS = BASE_URL + "animals/get_particular_selling_details";
    public static String UPDATE_PARTICULAR_SELLING_DETAILS = BASE_URL + "animals/update_particular_selling_details";
    public static String DELETE_PARTICULAR_SELLING_DETAILS = BASE_URL + "animals/delete_particular_selling_details";

    // KISAN MADAT
   public static String GET_KISAN_MADAT_LIST = BASE_URL + "users/get_helpers_list";

//   PASHU VYAPARI
    public static String GET_SELLING_REPORT = BASE_URL + "animals/get_selling_report";
    public static String GET_USER_SELLING_HISTORY = BASE_URL + "animals/get_user_selling_history";

}



