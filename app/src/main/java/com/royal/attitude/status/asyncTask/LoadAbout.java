package com.royal.attitude.status.asyncTask;

import android.os.AsyncTask;

import com.royal.attitude.status.interfaces.AboutListener;
import com.royal.attitude.status.item.ItemAbout;
import com.royal.attitude.status.utils.Constants;
import com.royal.attitude.status.utils.JSONParser;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.RequestBody;

public class LoadAbout extends AsyncTask<String, String, String> {

    private RequestBody requestBody;
    private AboutListener aboutListener;
    private String message = "", verifyStatus = "0";

    public LoadAbout(AboutListener aboutListener, RequestBody requestBody) {
        this.aboutListener = aboutListener;
        this.requestBody = requestBody;
    }

    @Override
    protected void onPreExecute() {
        aboutListener.onStart();
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... strings) {
        String json = JSONParser.okhttpPost(Constants.SERVER_URL, requestBody);
        try {
            JSONObject jsonObject = new JSONObject(json);
            if (jsonObject.has(Constants.TAG_ROOT)) {
                JSONArray jsonArray = jsonObject.getJSONArray(Constants.TAG_ROOT);

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject c = jsonArray.getJSONObject(i);

                    if(!c.has(Constants.TAG_SUCCESS)) {
                        String appname = c.getString("app_name");
                        String applogo = c.getString("app_logo");
                        String desc = c.getString("app_description");
                        String appversion = c.getString("app_version");
                        String appauthor = c.getString("app_author");
                        String appcontact = c.getString("app_contact");
                        String email = c.getString("app_email");
                        String website = c.getString("app_website");
                        String privacy = c.getString("app_privacy_policy");
                        String developedby = c.getString("app_developed_by");

                        Constants.publisherAdID = c.getString("publisher_id");

                        Constants.isBannerAd = Boolean.parseBoolean(c.getString("banner_ad"));
                        Constants.isInterstitialAd = Boolean.parseBoolean(c.getString("interstital_ad"));
                        Constants.isNativeAd = Boolean.parseBoolean(c.getString("native_ad"));

                        Constants.bannerAdType = c.getString("banner_ad_type");
                        Constants.interstitialAdType = c.getString("interstital_ad_type");
                        Constants.natveAdType = c.getString("native_ad_type");

                        Constants.bannerAdID = c.getString("banner_ad_id");
                        Constants.interstitialAdID = c.getString("interstital_ad_id");
                        Constants.nativeAdID = c.getString("native_ad_id");

                        Constants.interstitialAdShow = Integer.parseInt(c.getString("interstital_ad_click"));
                        Constants.nativeAdShow = Integer.parseInt(c.getString("native_position"));

                        Constants.packageName = c.getString("package_name");

                        Constants.showUpdateDialog = c.getBoolean("app_update_status");
                        Constants.appVersion = c.getString("app_new_version");
                        Constants.appUpdateMsg = c.getString("app_update_desc");
                        Constants.appUpdateURL = c.getString("app_redirect_url");
                        Constants.appUpdateCancel = c.getBoolean("cancel_update_status");

                        Constants.itemAbout = new ItemAbout(appname, applogo, desc, appversion, appauthor, appcontact, email, website, privacy, developedby);
                    } else {
                        verifyStatus = c.getString(Constants.TAG_SUCCESS);
                        message = c.getString(Constants.TAG_MSG);
                    }
                }
            }
            return "1";
        } catch (Exception ee) {
            ee.printStackTrace();
            return "0";
        }
    }

    @Override
    protected void onPostExecute(String s) {
        aboutListener.onEnd(s, verifyStatus, message);
        super.onPostExecute(s);
    }
}