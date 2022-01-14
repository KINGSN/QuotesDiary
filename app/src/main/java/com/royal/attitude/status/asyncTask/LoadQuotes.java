package com.royal.attitude.status.asyncTask;

import android.os.AsyncTask;

import com.royal.attitude.status.interfaces.QuotesListener;
import com.royal.attitude.status.item.ItemQuotes;
import com.royal.attitude.status.utils.Constants;
import com.royal.attitude.status.utils.JSONParser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.RequestBody;

public class LoadQuotes extends AsyncTask<String, String, String> {

    private RequestBody requestBody;
    private QuotesListener quotesListener;
    private ArrayList<ItemQuotes> arrayList;
    private String verifyStatus = "0", message = "0";
    private int total_records = -1;

    public LoadQuotes(QuotesListener quotesListener, RequestBody requestBody) {
        this.quotesListener = quotesListener;
        this.requestBody = requestBody;
        arrayList = new ArrayList<>();
    }

    @Override
    protected void onPreExecute() {
        quotesListener.onStart();
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... strings) {
        String json = JSONParser.okhttpPost(Constants.SERVER_URL, requestBody);
        try {
            JSONObject jOb = new JSONObject(json);
            JSONArray jsonArray = jOb.getJSONArray(Constants.TAG_ROOT);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject objJson = jsonArray.getJSONObject(i);

                if(!objJson.has(Constants.TAG_SUCCESS)) {

                    if(objJson.has("num")) {
                        total_records = objJson.getInt("num");
                    }

                    String id = objJson.getString(Constants.TAG_QUOTES_ID);
                    String cid = objJson.getString(Constants.TAG_QUOTES_CAT_ID);
                    String cat_name = "", img = "", img_thumb = "", quote = "", bg = "", font = "", font_color = "";
                    if (objJson.has(Constants.TAG_QUOTES_CAT_NAME)) {
                        cat_name = objJson.getString(Constants.TAG_QUOTES_CAT_NAME);
                    }
                    if (objJson.has(Constants.TAG_QUOTES_IMAGE_BIG)) {
                        img = objJson.getString(Constants.TAG_QUOTES_IMAGE_BIG).replace(" ", "%20");
                    }
                    if (objJson.has(Constants.TAG_QUOTES_IMAGE_SMALL)) {
                        img_thumb = objJson.getString(Constants.TAG_QUOTES_IMAGE_SMALL).replace(" ", "%20");
                    }
                    if (objJson.has(Constants.TAG_QUOTES_TEXT)) {
                        quote = objJson.getString(Constants.TAG_QUOTES_TEXT);
                        bg = objJson.getString(Constants.TAG_QUOTES_BG);
                        font = objJson.getString(Constants.TAG_QUOTES_FONT);
                        font_color = objJson.getString(Constants.TAG_QUOTES_FONT_COLOR);
                    }

                    Boolean isliked = Boolean.parseBoolean(objJson.getString(Constants.TAG_QUOTES_LIKED));
                    Boolean isFav = Boolean.parseBoolean(objJson.getString(Constants.TAG_QUOTES_FAV));
                    String totalviews = objJson.getString(Constants.TAG_QUOTES_TOTAL_VIEWS);
                    String totallikes = objJson.getString(Constants.TAG_QUOTES_TOTAL_LIKES);
                    String totaldownload = objJson.getString(Constants.TAG_QUOTES_TOTAL_DOWNLOADS);

                    ItemQuotes itemQuotes = new ItemQuotes(id, cid, cat_name, img, img_thumb, quote, totallikes, isliked, isFav, totalviews, totaldownload, bg, font, font_color);

                    if(objJson.has(Constants.TAG_QUOTES_APPROVED)) {
                        itemQuotes.setIsApproved(objJson.getBoolean(Constants.TAG_QUOTES_APPROVED));
                    }
                    arrayList.add(itemQuotes);
                } else {
                    verifyStatus = objJson.getString(Constants.TAG_SUCCESS);
                    message = objJson.getString(Constants.TAG_MSG);
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
        quotesListener.onEnd(s, verifyStatus, message, arrayList, total_records);
        super.onPostExecute(s);
    }
}