package com.royal.attitude.status.asyncTask;

import android.os.AsyncTask;

import com.royal.attitude.status.interfaces.ProfileEditListener;
import com.royal.attitude.status.utils.Constants;
import com.royal.attitude.status.utils.JSONParser;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.RequestBody;

public class LoadProfileEdit extends AsyncTask<String, String, String> {

    private RequestBody requestBody;
    private ProfileEditListener profileEditListener;
    private String success = "0", message = "", profileImage = "";

    public LoadProfileEdit(ProfileEditListener profileEditListener, RequestBody requestBody) {
        this.profileEditListener = profileEditListener;
        this.requestBody = requestBody;
    }

    @Override
    protected void onPreExecute() {
        profileEditListener.onStart();
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

                if(c.has(Constants.TAG_PROFILE_IMAGE)) {
                    profileImage = c.getString(Constants.TAG_PROFILE_IMAGE);
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
        profileEditListener.onEnd(profileImage, s, success, message);
        super.onPostExecute(s);
    }
}