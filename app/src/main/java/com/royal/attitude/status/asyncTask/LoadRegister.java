package com.royal.attitude.status.asyncTask;

import android.os.AsyncTask;

import com.royal.attitude.status.interfaces.SocialLoginListener;
import com.royal.attitude.status.utils.Constants;
import com.royal.attitude.status.utils.JSONParser;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.RequestBody;

public class LoadRegister extends AsyncTask<String, String, String> {

    private RequestBody requestBody;
    private SocialLoginListener socialLoginListener;
    private String success = "0", message = "", user_id = "";

    public LoadRegister(SocialLoginListener socialLoginListener, RequestBody requestBody) {
        this.socialLoginListener = socialLoginListener;
        this.requestBody = requestBody;
    }

    @Override
    protected void onPreExecute() {
        socialLoginListener.onStart();
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            String json = JSONParser.okhttpPost(Constants.SERVER_URL, requestBody);
            JSONObject mainJson = new JSONObject(json);
            JSONArray jsonArray = mainJson.getJSONArray(Constants.TAG_ROOT);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject c = jsonArray.getJSONObject(i);

                success = c.getString(Constants.TAG_SUCCESS);
                message = c.getString(Constants.TAG_MSG);
                if(c.has("user_id")) {
                    user_id = c.getString("user_id");
                }
            }
            return "1";
        } catch (Exception e) {
            e.printStackTrace();
            return "0";
        }
    }

    @Override
    protected void onPostExecute(String s) {
        socialLoginListener.onEnd(s, success, message, user_id);
        super.onPostExecute(s);
    }
}