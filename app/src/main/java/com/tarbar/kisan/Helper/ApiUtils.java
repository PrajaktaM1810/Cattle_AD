package com.tarbar.kisan.Helper;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RetryPolicy;

public class ApiUtils {

    private ApiUtils() {
    }
    public static final String BASE_URL = constant.BASE_URL;
    public static final int SOCKET_TIMEOUT = 600000;
    public static final RetryPolicy DEFAULT_RETRY_POLICY =
            new DefaultRetryPolicy(SOCKET_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

}