package com.royal.attitude.status.asyncTask;

import android.os.AsyncTask;

import com.royal.attitude.status.interfaces.SuccessListener;
import com.royal.attitude.status.item.ItemUser;
import com.royal.attitude.status.utils.Constants;
import com.royal.attitude.status.utils.JSONParser;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.RequestBody;

public class LoadProfile extends AsyncTask<String, String, String> {

    private RequestBody requestBody;
    private SuccessListener successListener;
    private String success = "0", message = "";

    public LoadProfile(SuccessListener successListener, RequestBody requestBody) {
        this.successListener = successListener;
        this.requestBody = requestBody;
    }

    @Override
    protected void onPreExecute() {
        successListener.onStart();
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
                String user_id = c.getString(Constants.TAG_USER_ID);
                String user_name = c.getString(Constants.TAG_NAME);
                String email = c.getString(Constants.TAG_EMAIL);
                String phone = c.getString(Constants.TAG_PHONE);
                String image = c.getString(Constants.TAG_PROFILE_IMAGE);

                String loginType= Constants.itemUser.getLoginType();
                String authID = Constants.itemUser.getLoginType();
                Constants.itemUser = new ItemUser(user_id, user_name, email, phone, image, authID, loginType);
            }
            return "1";
        } catch (Exception e) {
            e.printStackTrace();
            return "0";
        }
    }

    @Override
    protected void onPostExecute(String s) {
        successListener.onEnd(s, success, message);
        super.onPostExecute(s);
    }
}