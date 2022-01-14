package com.royal.attitude.status.asyncTask;

import android.os.AsyncTask;

import com.royal.attitude.status.interfaces.HomeListener;
import com.royal.attitude.status.item.ItemQuotes;
import com.royal.attitude.status.utils.Constants;
import com.royal.attitude.status.utils.JSONParser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.RequestBody;

public class LoadHome extends AsyncTask<String, String, String> {

    private RequestBody requestBody;
    private HomeListener homeListener;
    private ArrayList<ItemQuotes> arrayListFeatured, arrayListLatest, arrayListPopular, arrayListTop, arrayListText, arrayListQuoteOfDay;
    private String message = "";

    public LoadHome(HomeListener homeListener, RequestBody requestBody) {
        this.homeListener = homeListener;
        this.requestBody = requestBody;
        arrayListFeatured = new ArrayList<>();
        arrayListPopular = new ArrayList<>();
        arrayListLatest = new ArrayList<>();
        arrayListTop = new ArrayList<>();
        arrayListText = new ArrayList<>();
        arrayListQuoteOfDay = new ArrayList<>();
    }

    @Override
    protected void onPreExecute() {
        homeListener.onStart();
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... strings) {
        String json = JSONParser.okhttpPost(Constants.SERVER_URL, requestBody);
        try {
            JSONObject jOb = new JSONObject(json);

            if(jOb.get(Constants.TAG_ROOT)instanceof JSONObject) {
                JSONObject jsonObj = jOb.getJSONObject(Constants.TAG_ROOT);

                JSONArray jsonArray_featured = jsonObj.getJSONArray(Constants.TAG_FEATURED_QUOTES);
                for (int i = 0; i < jsonArray_featured.length(); i++) {
                    JSONObject objJson = jsonArray_featured.getJSONObject(i);

                    String id = objJson.getString(Constants.TAG_QUOTES_ID);
                    String cid = objJson.getString(Constants.TAG_QUOTES_CAT_ID);
                    String img = objJson.getString(Constants.TAG_QUOTES_IMAGE_BIG).replace(" ", "%20");
                    String img_thumb = objJson.getString(Constants.TAG_QUOTES_IMAGE_SMALL).replace(" ", "%20");
                    Boolean isliked = Boolean.parseBoolean(objJson.getString(Constants.TAG_QUOTES_LIKED));
                    Boolean isFav = Boolean.parseBoolean(objJson.getString(Constants.TAG_QUOTES_FAV));
                    String totalviews = objJson.getString(Constants.TAG_QUOTES_TOTAL_VIEWS);
                    String totallikes = objJson.getString(Constants.TAG_QUOTES_TOTAL_LIKES);
                    String totaldownload = objJson.getString(Constants.TAG_QUOTES_TOTAL_DOWNLOADS);

                    ItemQuotes itemQuotes = new ItemQuotes(id, cid, "", img, img_thumb, "", totallikes, isliked, isFav, totalviews, totaldownload);
                    arrayListFeatured.add(itemQuotes);
                }

                JSONArray jsonArray_lat_image = jsonObj.getJSONArray(Constants.TAG_LATEST_IMAGE_QUOTES);
                for (int i = 0; i < jsonArray_lat_image.length(); i++) {
                    JSONObject objJson = jsonArray_lat_image.getJSONObject(i);

                    String id = objJson.getString(Constants.TAG_QUOTES_ID);
                    String cid = objJson.getString(Constants.TAG_QUOTES_CAT_ID);
                    String img = objJson.getString(Constants.TAG_QUOTES_IMAGE_BIG).replace(" ", "%20");
                    String img_thumb = objJson.getString(Constants.TAG_QUOTES_IMAGE_SMALL).replace(" ", "%20");
                    Boolean isliked = Boolean.parseBoolean(objJson.getString(Constants.TAG_QUOTES_LIKED));
                    Boolean isFav = Boolean.parseBoolean(objJson.getString(Constants.TAG_QUOTES_FAV));
                    String totalviews = objJson.getString(Constants.TAG_QUOTES_TOTAL_VIEWS);
                    String totallikes = objJson.getString(Constants.TAG_QUOTES_TOTAL_LIKES);
                    String totaldownload = objJson.getString(Constants.TAG_QUOTES_TOTAL_DOWNLOADS);

                    ItemQuotes itemQuotes = new ItemQuotes(id, cid, "", img, img_thumb, "", totallikes, isliked, isFav, totalviews, totaldownload);
                    arrayListLatest.add(itemQuotes);
                }

                JSONArray jsonArray_pop = jsonObj.getJSONArray(Constants.TAG_POPULAR_QUOTES);
                for (int i = 0; i < jsonArray_pop.length(); i++) {
                    JSONObject objJson = jsonArray_pop.getJSONObject(i);

                    String id = objJson.getString(Constants.TAG_QUOTES_ID);
                    String cid = objJson.getString(Constants.TAG_QUOTES_CAT_ID);
                    String img = objJson.getString(Constants.TAG_QUOTES_IMAGE_BIG).replace(" ", "%20");
                    String img_thumb = objJson.getString(Constants.TAG_QUOTES_IMAGE_SMALL).replace(" ", "%20");
                    Boolean isliked = Boolean.parseBoolean(objJson.getString(Constants.TAG_QUOTES_LIKED));
                    Boolean isFav = Boolean.parseBoolean(objJson.getString(Constants.TAG_QUOTES_FAV));
                    String totalviews = objJson.getString(Constants.TAG_QUOTES_TOTAL_VIEWS);
                    String totallikes = objJson.getString(Constants.TAG_QUOTES_TOTAL_LIKES);
                    String totaldownload = objJson.getString(Constants.TAG_QUOTES_TOTAL_DOWNLOADS);

                    ItemQuotes itemQuotes = new ItemQuotes(id, cid, "", img, img_thumb, "", totallikes, isliked, isFav, totalviews, totaldownload);
                    arrayListPopular.add(itemQuotes);
                }

                JSONArray jsonArray_top = jsonObj.getJSONArray(Constants.TAG_TOP_LIKED_QUOTES);
                for (int i = 0; i < jsonArray_top.length(); i++) {
                    JSONObject objJson = jsonArray_top.getJSONObject(i);

                    String id = objJson.getString(Constants.TAG_QUOTES_ID);
                    String cid = objJson.getString(Constants.TAG_QUOTES_CAT_ID);
                    String img = objJson.getString(Constants.TAG_QUOTES_IMAGE_BIG).replace(" ", "%20");
                    String img_thumb = objJson.getString(Constants.TAG_QUOTES_IMAGE_SMALL).replace(" ", "%20");
                    Boolean isliked = Boolean.parseBoolean(objJson.getString(Constants.TAG_QUOTES_LIKED));
                    Boolean isFav = Boolean.parseBoolean(objJson.getString(Constants.TAG_QUOTES_FAV));
                    String totalviews = objJson.getString(Constants.TAG_QUOTES_TOTAL_VIEWS);
                    String totallikes = objJson.getString(Constants.TAG_QUOTES_TOTAL_LIKES);
                    String totaldownload = objJson.getString(Constants.TAG_QUOTES_TOTAL_DOWNLOADS);

                    ItemQuotes itemQuotes = new ItemQuotes(id, cid, "", img, img_thumb, "", totallikes, isliked, isFav, totalviews, totaldownload);
                    arrayListTop.add(itemQuotes);
                }

                JSONArray jsonArray_day = jsonObj.getJSONArray(Constants.TAG_QUOTE_OF_THE_DAY);
                for (int i = 0; i < jsonArray_day.length(); i++) {
                    JSONObject objJson = jsonArray_day.getJSONObject(i);

                    String id = objJson.getString(Constants.TAG_QUOTES_ID);
                    String cid = objJson.getString(Constants.TAG_QUOTES_CAT_ID);
                    String img = objJson.getString(Constants.TAG_QUOTES_IMAGE_BIG).replace(" ", "%20");
                    String img_thumb = objJson.getString(Constants.TAG_QUOTES_IMAGE_SMALL).replace(" ", "%20");
                    Boolean isliked = Boolean.parseBoolean(objJson.getString(Constants.TAG_QUOTES_LIKED));
                    Boolean isFav = Boolean.parseBoolean(objJson.getString(Constants.TAG_QUOTES_FAV));
                    String totalviews = objJson.getString(Constants.TAG_QUOTES_TOTAL_VIEWS);
                    String totallikes = objJson.getString(Constants.TAG_QUOTES_TOTAL_LIKES);
                    String totaldownload = objJson.getString(Constants.TAG_QUOTES_TOTAL_DOWNLOADS);

                    ItemQuotes itemQuotes = new ItemQuotes(id, cid, "", img, img_thumb, "", totallikes, isliked, isFav, totalviews, totaldownload);
                    arrayListQuoteOfDay.add(itemQuotes);
                }

                JSONArray jsonArray_text = jsonObj.getJSONArray(Constants.TAG_LATEST_TEXT_QUOTES);
                for (int i = 0; i < jsonArray_text.length(); i++) {
                    JSONObject objJson = jsonArray_text.getJSONObject(i);

                    String id = objJson.getString(Constants.TAG_QUOTES_ID);
                    String cid = objJson.getString(Constants.TAG_QUOTES_CAT_ID);
                    String bg = objJson.getString(Constants.TAG_QUOTES_BG);
                    String font = objJson.getString(Constants.TAG_QUOTES_FONT);
                    String font_color = objJson.getString(Constants.TAG_QUOTES_FONT_COLOR);
                    String quote = objJson.getString(Constants.TAG_QUOTES_TEXT);
                    Boolean isliked = Boolean.parseBoolean(objJson.getString(Constants.TAG_QUOTES_LIKED));
                    Boolean isFav = Boolean.parseBoolean(objJson.getString(Constants.TAG_QUOTES_FAV));
                    String totalviews = objJson.getString(Constants.TAG_QUOTES_TOTAL_VIEWS);
                    String totallikes = objJson.getString(Constants.TAG_QUOTES_TOTAL_LIKES);
                    String totaldownload = objJson.getString(Constants.TAG_QUOTES_TOTAL_DOWNLOADS);

                    ItemQuotes itemQuotes = new ItemQuotes(id, cid, "", "", "", quote, totallikes, isliked, isFav, totalviews, totaldownload, bg, font, font_color);
                    arrayListText.add(itemQuotes);
                }
                return "1";
            } else {
                JSONArray jsonArray = jOb.getJSONArray(Constants.TAG_ROOT);
                message = jsonArray.getJSONObject(0).getString(Constants.TAG_MSG);
                return jsonArray.getJSONObject(0).getString(Constants.TAG_SUCCESS);
            }
        } catch (Exception ee) {
            ee.printStackTrace();
            return "0";
        }
    }

    @Override
    protected void onPostExecute(String s) {
        homeListener.onEnd(s, message, arrayListFeatured, arrayListLatest, arrayListPopular, arrayListTop, arrayListQuoteOfDay, arrayListText);
        super.onPostExecute(s);
    }
}