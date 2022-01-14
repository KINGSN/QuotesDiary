package com.royal.attitude.status.utils;

import android.net.Uri;

import com.royal.attitude.status.item.ItemUser;
import com.royal.attitude.status.BuildConfig;
import com.royal.attitude.status.item.ItemAbout;

public class Constants {

    public static final String SERVER_URL = BuildConfig.SERVER_URL + "api.php";

    public static final String METHOD_ABOUT = "get_app_details";
    public static final String METHOD_HOME = "get_home";
    public static final String METHOD_CAT = "get_cat_list";
    public static final String METHOD_CAT_TYPE = "get_category_users";

    public static final String METHOD_QUOTES_LATEST_IMAGE = "image_quotes_latest";
    public static final String METHOD_QUOTES_LATEST_TEXT = "text_quotes_latest";
    public static final String METHOD_QUOTES_CAT_IMAGE = "get_image_quotes_cat_id";
    public static final String METHOD_QUOTES_CAT_TEXT = "get_text_quotes_cat_id";
    public static final String METHOD_QUOTES_TOP_LIKE_IMAGE = "image_quotes_top_likes";
    public static final String METHOD_QUOTES_TOP_LIKE_TEXT = "text_quotes_top_likes";
    public static final String METHOD_QUOTES_POPULAR_IMAGE = "image_quotes_popular";
    public static final String METHOD_QUOTES_POPULAR_TEXT = "text_quotes_popular";
    public static final String METHOD_QUOTES_FAVOURITE = "get_favourite_list";
    public static final String METHOD_SEARCH_IMAGE = "search_image_quotes";
    public static final String METHOD_SEARCH_TEXT = "search_text_quotes";
    public static final String METHOD_REPORT = "quotes_report";

    public static final String METHOD_LIKE_IMAGE = "get_image_like";
    public static final String METHOD_LIKE_TEXT = "get_text_like";
    public static final String METHOD_FAV_QUOTE = "quotes_favourite";

    public static final String METHOD_UPLOAD_IMAGE = "upload_image_quote";
    public static final String METHOD_UPLOAD_TEXT = "upload_text_quote";

    public static final String METHOD_SINGLE_IMAGE = "get_image_single_quotes_id";
    public static final String METHOD_SINGLE_TEXT = "get_text_single_quotes_id";

    public static final String METHOD_DOWNLOAD_COUNT = "get_quotes_download";

    public static final String METHOD_LOGIN = "user_login";
    public static final String METHOD_REGISTER = "user_register";
    public static final String METHOD_PROFILE = "user_profile";
    public static final String METHOD_PROFILE_UPDATE = "user_profile_update";
    public static final String METHOD_FORGOT_PASS = "forgot_pass";
    public static final String METHOD_USER_QUOTES_TEXT = "get_text_quotes";
    public static final String METHOD_USER_QUOTES_IMAGES = "get_image_quotes";
    public static final String METHOD_USER_QUOTES_DELETE = "delete_quote";

    public static final String TAG_ROOT = "QUOTES_DIARY";
    public static final String TAG_MSG = "msg";
    public static final String TAG_SUCCESS = "success";

    public static final String TAG_USER_ID = "user_id";
    public static final String TAG_NAME = "name";
    public static final String TAG_EMAIL = "email";
    public static final String TAG_PHONE = "phone";
    public static final String TAG_PROFILE_IMAGE = "user_profile";

    public static final String TAG_CID = "cid";
    public static final String TAG_CAT_NAME = "category_name";
    public static final String TAG_CAT_IMAGE = "category_image";
    public static final String TAG_CAT_IMAGE_THUMB = "category_image_thumb";

    public static final String TAG_FEATURED_QUOTES = "featured_image_quotes";
    public static final String TAG_LATEST_IMAGE_QUOTES = "latest_image_quotes";
    public static final String TAG_POPULAR_QUOTES = "popular_image_quotes";
    public static final String TAG_TOP_LIKED_QUOTES = "top_liked_image_quotes";
    public static final String TAG_QUOTE_OF_THE_DAY = "today_quote";
    public static final String TAG_LATEST_TEXT_QUOTES = "latest_text_quotes";

    public static final String TAG_QUOTES_ID = "id";
    public static final String TAG_QUOTES_CAT_ID = "cat_id";
    public static final String TAG_QUOTES_CAT_NAME = "category_name";
    public static final String TAG_QUOTES_IMAGE_BIG = "quote_image_b";
    public static final String TAG_QUOTES_IMAGE_SMALL = "quote_image_s";
    public static final String TAG_QUOTES_TEXT = "quote";
    public static final String TAG_QUOTES_TOTAL_LIKES = "quotes_likes";
    public static final String TAG_QUOTES_TOTAL_VIEWS = "total_views";
    public static final String TAG_QUOTES_TOTAL_DOWNLOADS = "total_download";
    public static final String TAG_QUOTES_LIKED = "already_like";
    public static final String TAG_QUOTES_BG = "quote_bg";
    public static final String TAG_QUOTES_FONT = "quote_font";
    public static final String TAG_QUOTES_FONT_COLOR = "quote_font_color";
    public static final String TAG_QUOTES_FAV = "already_favourite";
    public static final String TAG_QUOTES_APPROVED = "quote_status";

    public static final String DARK_MODE_ON = "on";
    public static final String DARK_MODE_OFF = "off";
    public static final String DARK_MODE_SYSTEM = "system";

    public static final String LOGIN_TYPE_NORMAL = "normal";
    public static final String LOGIN_TYPE_GOOGLE = "google";
    public static final String LOGIN_TYPE_FB = "facebook";

    private static String TAG_HOST_URL = BuildConfig.SERVER_URL;

    public static final String URL_ABOUT_US_LOGO = TAG_HOST_URL + "images/";

    public static Boolean isUpdate = false, isLogged = false, isFromPush = false, isBannerAd = true, isInterstitialAd = true,
            isNativeAd = true, showUpdateDialog = true, appUpdateCancel = true;
    public static String publisherAdID = "", interstitialAdID = "", nativeAdID = "", bannerAdID = "", bannerAdType = "admob", interstitialAdType = "admob", natveAdType = "admob";
    public static int nativeAdShow = 8, interstitialAdShow = 5;

    public static ItemAbout itemAbout;
    public static ItemUser itemUser = new ItemUser("","", "", "","","",Constants.LOGIN_TYPE_NORMAL);
    public static Uri uri_setwall;
    public static String pushCID = "0", pushCName = "", packageName = "", appVersion = "", appUpdateMsg = "", appUpdateURL = "";

    public static int adCount = 0;
}