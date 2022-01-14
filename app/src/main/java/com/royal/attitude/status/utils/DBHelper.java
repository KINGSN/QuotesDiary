package com.royal.attitude.status.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.royal.attitude.status.item.ItemAbout;

public class DBHelper extends SQLiteOpenHelper {

    private Methods methods;
    private static String DB_NAME = "quotes.db";
    private SQLiteDatabase db;

    // Table Name
    private static final String TABLE_QUOTES = "quotes";
    private static final String TABLE_ABOUT = "about";

    // Table columns_quotes
    private static final String TAG_ID = "id";
    private static final String TAG_CAT_ID = "cid";
    private static final String TAG_CAT_NAME = "cname";
    private static final String TAG_IMAGE_BIG = "image";
    private static final String TAG_IMAGE_SMALL = "thumb";
    private static final String TAG_TEXT_QUOTE = "text_quote";
    private static final String TAG_LIKES = "likes";
    private static final String TAG_TYPE = "type";
    private static final String TAG_ISLIKED = "isLiked";
    private static final String TAG_VIEWS = "downloads";
    private static final String TAG_DOWNLOADS = "views";

    private static final String TAG_ABOUT_NAME = "name";
    private static final String TAG_ABOUT_LOGO = "logo";
    private static final String TAG_ABOUT_VERSION = "version";
    private static final String TAG_ABOUT_AUTHOR = "author";
    private static final String TAG_ABOUT_CONTACT = "contact";
    private static final String TAG_ABOUT_EMAIL = "email";
    private static final String TAG_ABOUT_WEBSITE = "website";
    private static final String TAG_ABOUT_DESC = "desc";
    private static final String TAG_ABOUT_DEVELOPED = "developed";
    private static final String TAG_ABOUT_PRIVACY = "privacy";
    private static final String TAG_ABOUT_PUB_ID = "ad_pub";
    private static final String TAG_ABOUT_BANNER_ID = "ad_banner";
    private static final String TAG_ABOUT_INTER_ID = "ad_inter";
    private static final String TAG_ABOUT_IS_BANNER = "isbanner";
    private static final String TAG_ABOUT_IS_INTER = "isinter";
    private static final String TAG_ABOUT_CLICK = "click";

    // Creating table query
    private static final String CREATE_TABLE_QUOTES = "create table " + TABLE_QUOTES + "(" + TAG_ID
            + " TEXT PRIMARY KEY, " + TAG_CAT_ID + " TEXT, " + TAG_CAT_NAME + " TEXT, " + TAG_IMAGE_BIG + " TEXT" +
            ", " + TAG_IMAGE_SMALL + " TEXT, " + TAG_TEXT_QUOTE + " TEXT, " + TAG_LIKES + " TEXT, " + TAG_TYPE + " TEXT" +
            ", " + TAG_ISLIKED + " TEXT, " + TAG_VIEWS + " TEXT, " + TAG_DOWNLOADS + " TEXT);";

    // Creating table query
    private static final String CREATE_TABLE_ABOUT = "create table " + TABLE_ABOUT + "(" + TAG_ABOUT_NAME
            + " TEXT, " + TAG_ABOUT_LOGO + " TEXT, " + TAG_ABOUT_VERSION + " TEXT, " + TAG_ABOUT_AUTHOR + " TEXT" +
            ", " + TAG_ABOUT_CONTACT + " TEXT, " + TAG_ABOUT_EMAIL + " TEXT, " + TAG_ABOUT_WEBSITE + " TEXT, " + TAG_ABOUT_DESC + " TEXT" +
            ", " + TAG_ABOUT_DEVELOPED + " TEXT, " + TAG_ABOUT_PRIVACY + " TEXT, " + TAG_ABOUT_PUB_ID + " TEXT, " + TAG_ABOUT_BANNER_ID + " TEXT" +
            ", " + TAG_ABOUT_INTER_ID + " TEXT, " + TAG_ABOUT_IS_BANNER + " TEXT, " + TAG_ABOUT_IS_INTER + " TEXT, " + TAG_ABOUT_CLICK + " TEXT);";

    private String[] columns_about = new String[]{TAG_ABOUT_NAME, TAG_ABOUT_LOGO, TAG_ABOUT_VERSION, TAG_ABOUT_AUTHOR,
            TAG_ABOUT_CONTACT, TAG_ABOUT_EMAIL, TAG_ABOUT_WEBSITE, TAG_ABOUT_DESC, TAG_ABOUT_DEVELOPED, TAG_ABOUT_PRIVACY,
            TAG_ABOUT_PUB_ID, TAG_ABOUT_BANNER_ID, TAG_ABOUT_INTER_ID, TAG_ABOUT_IS_BANNER, TAG_ABOUT_IS_INTER, TAG_ABOUT_CLICK};

    private String[] columns_quotes = new String[]{TAG_ID, TAG_CAT_ID, TAG_CAT_NAME, TAG_IMAGE_BIG, TAG_IMAGE_SMALL, TAG_TEXT_QUOTE,
            TAG_LIKES, TAG_TYPE, TAG_ISLIKED, TAG_VIEWS, TAG_DOWNLOADS};

    public DBHelper(Context context) {
        super(context, DB_NAME, null, 1);
        db = getWritableDatabase();
        methods = new Methods(context);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(CREATE_TABLE_QUOTES);
            db.execSQL(CREATE_TABLE_ABOUT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addtoAbout() {
        db.delete(TABLE_ABOUT, null, null);

        ContentValues contentValues = new ContentValues();
        contentValues.put(TAG_ABOUT_NAME, Constants.itemAbout.getAppName());
        contentValues.put(TAG_ABOUT_LOGO, Constants.itemAbout.getAppLogo());
        contentValues.put(TAG_ABOUT_VERSION, Constants.itemAbout.getAppVersion());
        contentValues.put(TAG_ABOUT_AUTHOR, Constants.itemAbout.getAuthor());
        contentValues.put(TAG_ABOUT_CONTACT, Constants.itemAbout.getContact());
        contentValues.put(TAG_ABOUT_EMAIL, Constants.itemAbout.getEmail());
        contentValues.put(TAG_ABOUT_WEBSITE, Constants.itemAbout.getWebsite());
        contentValues.put(TAG_ABOUT_DESC, Constants.itemAbout.getAppDesc());
        contentValues.put(TAG_ABOUT_DEVELOPED, Constants.itemAbout.getDevelopedby());
        contentValues.put(TAG_ABOUT_PRIVACY, Constants.itemAbout.getPrivacy());
        contentValues.put(TAG_ABOUT_PUB_ID, Constants.publisherAdID);
        contentValues.put(TAG_ABOUT_BANNER_ID, Constants.bannerAdID);
        contentValues.put(TAG_ABOUT_INTER_ID, Constants.interstitialAdID);
        contentValues.put(TAG_ABOUT_IS_BANNER, Constants.isBannerAd.toString());
        contentValues.put(TAG_ABOUT_IS_INTER, Constants.isInterstitialAd.toString());
        contentValues.put(TAG_ABOUT_CLICK, Constants.interstitialAdShow);

        db.insert(TABLE_ABOUT, null, contentValues);
    }

    public Boolean getAbout() {
        try {
            Cursor c = db.query(TABLE_ABOUT, columns_about, null, null, null, null, null);
            if (c != null && c.moveToFirst()) {
                for (int i = 0; i < c.getCount(); i++) {
                    String appname = c.getString(c.getColumnIndex(TAG_ABOUT_NAME));
                    String applogo = c.getString(c.getColumnIndex(TAG_ABOUT_LOGO));
                    String desc = c.getString(c.getColumnIndex(TAG_ABOUT_DESC));
                    String appversion = c.getString(c.getColumnIndex(TAG_ABOUT_VERSION));
                    String appauthor = c.getString(c.getColumnIndex(TAG_ABOUT_AUTHOR));
                    String appcontact = c.getString(c.getColumnIndex(TAG_ABOUT_CONTACT));
                    String email = c.getString(c.getColumnIndex(TAG_ABOUT_EMAIL));
                    String website = c.getString(c.getColumnIndex(TAG_ABOUT_WEBSITE));
                    String privacy = c.getString(c.getColumnIndex(TAG_ABOUT_PRIVACY));
                    String developedby = c.getString(c.getColumnIndex(TAG_ABOUT_DEVELOPED));

                    Constants.bannerAdID = c.getString(c.getColumnIndex(TAG_ABOUT_BANNER_ID));
                    Constants.interstitialAdID = c.getString(c.getColumnIndex(TAG_ABOUT_INTER_ID));
                    Constants.isBannerAd = Boolean.parseBoolean(c.getString(c.getColumnIndex(TAG_ABOUT_IS_BANNER)));
                    Constants.isInterstitialAd = Boolean.parseBoolean(c.getString(c.getColumnIndex(TAG_ABOUT_IS_INTER)));
                    Constants.publisherAdID = c.getString(c.getColumnIndex(TAG_ABOUT_PUB_ID));
                    Constants.interstitialAdShow = Integer.parseInt(c.getString(c.getColumnIndex(TAG_ABOUT_CLICK)));

                    Constants.itemAbout = new ItemAbout(appname, applogo, desc, appversion, appauthor, appcontact, email, website, privacy, developedby);
                }
                c.close();
                return true;
            } else if (c != null) {
                c.close();
                return false;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUOTES);
        onCreate(db);
    }
}