package com.royal.attitude.status.asyncTask;

import android.os.AsyncTask;

import com.royal.attitude.status.interfaces.AllCategoryListener;
import com.royal.attitude.status.item.ItemCat;
import com.royal.attitude.status.utils.Constants;
import com.royal.attitude.status.utils.JSONParser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.RequestBody;

public class LoadAllCat extends AsyncTask<String,String,String> {

    private RequestBody requestBody;
    private AllCategoryListener allCategoryListener;
    private ArrayList<ItemCat> arrayList;
    private String verifyStatus = "1", message = "0";

    public LoadAllCat(AllCategoryListener allCategoryListener, RequestBody requestBody) {
        this.allCategoryListener = allCategoryListener;
        this.requestBody = requestBody;
        arrayList = new ArrayList<>();
    }

    @Override
    protected void onPreExecute() {
        allCategoryListener.onStart();
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... strings) {
        String json = JSONParser.okhttpPost(Constants.SERVER_URL, requestBody);

        try {
            JSONObject jOb = new JSONObject(json);
            JSONArray jsonArray = jOb.getJSONArray(Constants.TAG_ROOT);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject c = jsonArray.getJSONObject(i);

                if(!c.has(Constants.TAG_SUCCESS)) {

                    String id = c.getString(Constants.TAG_CID);
                    String name = c.getString(Constants.TAG_CAT_NAME);
                    String image = c.getString(Constants.TAG_CAT_IMAGE).replace(" ", "%20");
                    String image_thumb = c.getString(Constants.TAG_CAT_IMAGE_THUMB).replace(" ", "%20");

                    ItemCat itemCat = new ItemCat(id, name, image, image_thumb);
                    arrayList.add(itemCat);
                } else {
                    verifyStatus = c.getString(Constants.TAG_SUCCESS);
                    message = c.getString(Constants.TAG_MSG);
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
        allCategoryListener.onEnd(s, verifyStatus, message, arrayList);
        super.onPostExecute(s);
    }
}