package com.royal.attitude.status.asyncTask;

import android.os.AsyncTask;

import com.royal.attitude.status.interfaces.CategoryListener;
import com.royal.attitude.status.item.ItemCat;
import com.royal.attitude.status.utils.Constants;
import com.royal.attitude.status.utils.JSONParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.RequestBody;

public class LoadCat extends AsyncTask<String,String,String> {

    private RequestBody requestBody;
    private CategoryListener categoryListener;
    private ArrayList<ItemCat> arrayList_image_cat, arrayList_text_cat;
    private String message = "", verifyStatus = "0";

    public LoadCat(CategoryListener categoryListener, RequestBody requestBody) {
        this.categoryListener = categoryListener;
        this.requestBody = requestBody;
        arrayList_image_cat = new ArrayList<>();
        arrayList_text_cat = new ArrayList<>();
    }

    @Override
    protected void onPreExecute() {
        categoryListener.onStart();
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... strings) {
        String json = JSONParser.okhttpPost(Constants.SERVER_URL, requestBody);
        JSONObject jOb = null;
        try {
            jOb = new JSONObject(json);

                JSONObject jsonObj = jOb.getJSONObject(Constants.TAG_ROOT);

                JSONArray jsonArray_image = jsonObj.getJSONArray("image_quotes_cat");
                for (int i = 0; i < jsonArray_image.length(); i++) {
                    JSONObject c = jsonArray_image.getJSONObject(i);

                    String id = c.getString(Constants.TAG_CID);
                    String name = c.getString(Constants.TAG_CAT_NAME);
                    String image = c.getString(Constants.TAG_CAT_IMAGE).replace(" ", "%20");
                    String image_thumb = c.getString(Constants.TAG_CAT_IMAGE_THUMB).replace(" ", "%20");

                    ItemCat itemCat = new ItemCat(id, name, image, image_thumb);
                    arrayList_image_cat.add(itemCat);
                }

                JSONArray jsonArray_text = jsonObj.getJSONArray("text_quotes_cat");

                for (int i = 0; i < jsonArray_text.length(); i++) {
                    JSONObject c = jsonArray_text.getJSONObject(i);

                    String id = c.getString(Constants.TAG_CID);
                    String name = c.getString(Constants.TAG_CAT_NAME);
                    String image = c.getString(Constants.TAG_CAT_IMAGE).replace(" ", "%20");
                    String image_thumb = c.getString(Constants.TAG_CAT_IMAGE_THUMB).replace(" ", "%20");

                    ItemCat itemCat = new ItemCat(id, name, image, image_thumb);
                    arrayList_text_cat.add(itemCat);
                }
            return "1";
        } catch (JSONException e) {
            JSONArray jsonArray = null;
            try {
                jsonArray = jOb.getJSONArray(Constants.TAG_ROOT);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject c = jsonArray.getJSONObject(i);
                    if(c.has(Constants.TAG_SUCCESS)) {
                        verifyStatus = c.getString(Constants.TAG_SUCCESS);
                        message = c.getString(Constants.TAG_MSG);
                    }
                }
                return "1";
            } catch (Exception e1) {
                e1.printStackTrace();
                return "0";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "0";
        }
    }

    @Override
    protected void onPostExecute(String s) {
        categoryListener.onEnd(s, verifyStatus, message, arrayList_image_cat, arrayList_text_cat);
        super.onPostExecute(s);
    }
}