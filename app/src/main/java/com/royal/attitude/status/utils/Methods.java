package com.royal.attitude.status.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.InterstitialAdListener;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdListener;
import com.royal.attitude.status.asyncTask.LoadQuotes;
import com.royal.attitude.status.interfaces.InterAdListener;
import com.royal.attitude.status.interfaces.QuotesListener;
import com.royal.attitude.status.item.ItemQuotes;
import com.royal.attitude.status.item.ItemUser;
import com.royal.attitude.status.BuildConfig;
import com.royal.attitude.status.LoginActivity;
import com.royal.attitude.status.MainActivity;
import com.royal.attitude.status.R;
import com.royal.attitude.status.SetWallpaperActivity;
import com.facebook.login.LoginManager;
import com.google.ads.consent.ConsentInformation;
import com.google.ads.consent.ConsentStatus;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.yakivmospan.scytale.Crypto;
import com.yakivmospan.scytale.Options;
import com.yakivmospan.scytale.Store;

import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.SecretKey;
import javax.net.ssl.HttpsURLConnection;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class Methods {

    private Context context;
    private InterAdListener interAdListener;
    private SecretKey key;
    public static com.facebook.ads.InterstitialAd interstitialAd;
    public static String fbads="";
    com.facebook.ads.AdView adViewfb;
    public String TAG="KINGSN";
   public static NativeAd fbnativeAd;
    public static List<NativeAd> nativeAdsfb = new ArrayList<>();
    public static String fbnativeLoaded="false";


    public Methods(Context context) {
        this.context = context;

        Store store = new Store(context);
        if (!store.hasKey(BuildConfig.ENC_KEY)) {
            key = store.generateSymmetricKey(BuildConfig.ENC_KEY, null);
        } else {
            key = store.getSymmetricKey(BuildConfig.ENC_KEY, null);
        }
    }

    // constructor
    public Methods(Context context, InterAdListener interAdListener) {
        this.context = context;
        this.interAdListener = interAdListener;

        Store store = new Store(context);
        if (!store.hasKey(BuildConfig.ENC_KEY)) {
            key = store.generateSymmetricKey(BuildConfig.ENC_KEY, null);
        } else {
            key = store.getSymmetricKey(BuildConfig.ENC_KEY, null);
        }
    }

    public void showToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public boolean isDarkMode() {
        int currentNightMode = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        switch (currentNightMode) {
            case Configuration.UI_MODE_NIGHT_NO:
                // Night mode is not active, we're using the light theme
                return false;
            case Configuration.UI_MODE_NIGHT_YES:
                // Night mode is active, we're using dark theme
                return true;
            default:
                return false;
        }
    }

    public String getDarkMode() {
        SharedPref sharedPref = new SharedPref(context);
        return sharedPref.getDarkMode();
    }

    public boolean isConnectingToInternet() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public int getScreenWidth() {
        int columnWidth;
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        final Point point = new Point();

        point.x = display.getWidth();
        point.y = display.getHeight();

        columnWidth = point.x;
        return columnWidth;
    }

    public int getScreenHeight() {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        final Point point = new Point();
        point.y = display.getHeight();

        return point.y;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void forceRTLIfSupported(Window window) {
        if (context.getResources().getString(R.string.isRTL).equals("true")) {
            window.getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }
    }

    public void setStatusColor(Window window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    public void clickLogin() {
        if (Constants.isLogged) {
            logout((Activity) context);
            Toast.makeText(context, context.getResources().getString(R.string.logout_success), Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(context, LoginActivity.class);
            intent.putExtra("from", "app");
            context.startActivity(intent);
        }
    }

    private void logout(Activity activity) {
        SharedPref sharedPref = new SharedPref(context);
        sharedPref.setIsAutoLogin(false);
        Constants.isLogged = false;
        if (Constants.itemUser.getLoginType().equals(Constants.LOGIN_TYPE_FB)) {
            LoginManager.getInstance().logOut();
        }
        Constants.itemUser = new ItemUser("", "", "", "", "", "", Constants.LOGIN_TYPE_NORMAL);
        sharedPref.setLoginDetails(Constants.itemUser, false, "", "");
        Intent intent1 = new Intent(context, LoginActivity.class);
        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent1.putExtra("from", " ");
        context.startActivity(intent1);
        activity.finish();
    }

    public String getPathImage(Uri uri) {
        try {
            String filePath = "";
            String wholeID = DocumentsContract.getDocumentId(uri);

            // Split at colon, use second item in the array
            String id = wholeID.split(":")[1];

            String[] column = {MediaStore.Images.Media.DATA};

            // where id is equal to
            String sel = MediaStore.Images.Media._ID + "=?";

            Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    column, sel, new String[]{id}, null);

            int columnIndex = cursor.getColumnIndex(column[0]);

            if (cursor.moveToFirst()) {
                filePath = cursor.getString(columnIndex);
            }
            cursor.close();
            return filePath;
        } catch (Exception e) {
            e.printStackTrace();
            if (uri == null) {
                return null;
            }
            // try to retrieve the image from the media store first
            // this will only work for images selected from gallery
            String[] projection = {MediaStore.Images.Media.DATA};
            Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
            if (cursor != null) {
                int column_index = cursor
                        .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                String returnn = cursor.getString(column_index);
                cursor.close();
                return returnn;
            }
            // this is our fallback here
            return uri.getPath();
        }
    }

    private void showPersonalizedAds(LinearLayout linearLayout) {
        AdView adView = new AdView(context);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.setAdUnitId(Constants.bannerAdID);
        adView.setAdSize(AdSize.BANNER);
        linearLayout.addView(adView);
        adView.loadAd(adRequest);
    }

    private void showNonPersonalizedAds(LinearLayout linearLayout) {
        Bundle extras = new Bundle();
        extras.putString("npa", "1");
        AdView adView = new AdView(context);
        AdRequest adRequest = new AdRequest.Builder()
                .addNetworkExtrasBundle(AdMobAdapter.class, extras)
                .build();
        adView.setAdUnitId(Constants.bannerAdID);
        adView.setAdSize(AdSize.BANNER);
        linearLayout.addView(adView);
        adView.loadAd(adRequest);
    }

    public void showBannerAd(LinearLayout linearLayout) {
        if (isConnectingToInternet() && Constants.isBannerAd) {
           /* if (ConsentInformation.getInstance(context).getConsentStatus() == ConsentStatus.NON_PERSONALIZED) {
                showNonPersonalizedAds(linearLayout);
            } else {
                showPersonalizedAds(linearLayout);
            }*/

            showFbBanner(linearLayout);
        }
    }

    public void showInter(final int pos, final String type) {
        if (Constants.isInterstitialAd) {
            Constants.adCount = Constants.adCount + 1;
            if (Constants.adCount % Constants.interstitialAdShow == 0) {
                final AdManagerInter adManagerAdmobInter = new AdManagerInter(context);
                if (adManagerAdmobInter.getAd() != null) {
                    adManagerAdmobInter.getAd().setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            AdManagerInter.setAd(null);
                            adManagerAdmobInter.createAd();
                            interAdListener.onClick(pos, type);
                            super.onAdDismissedFullScreenContent();
                        }

                        @Override
                        public void onAdFailedToShowFullScreenContent(@NonNull @NotNull com.google.android.gms.ads.AdError adError) {
                            AdManagerInter.setAd(null);
                            adManagerAdmobInter.createAd();
                            interAdListener.onClick(pos, type);
                            super.onAdFailedToShowFullScreenContent(adError);
                        }
                    });
                    adManagerAdmobInter.getAd().show((Activity) context);
                } else {
                    AdManagerInter.setAd(null);
                    adManagerAdmobInter.createAd();
                    interAdListener.onClick(pos, type);
                }
            } else {
                interAdListener.onClick(pos, type);
            }

        } else {
            interAdListener.onClick(pos, type);
        }
    }

    public Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            InputStream input;
            if (src.contains("https://")) {
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                input = connection.getInputStream();
            } else {
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                input = connection.getInputStream();
            }

            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public GradientDrawable getGradientDrawable(int first, int second) {
        GradientDrawable gd = new GradientDrawable();
        gd.setColors(new int[]{first, second});
        gd.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        gd.setOrientation(GradientDrawable.Orientation.BOTTOM_TOP);
        gd.setCornerRadius(12);
        gd.mutate();
        return gd;
    }

    public void shareFileImage(String path) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        Uri phototUri = Uri.parse("file://" + path);
        shareIntent.setDataAndType(phototUri, "image/*");
        shareIntent.putExtra(Intent.EXTRA_STREAM, phototUri);
        context.startActivity(Intent.createChooser(shareIntent, context.getString(R.string.share_via)));
    }

    public void shareFileImageUri(Uri path, String name) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);

        shareIntent.setDataAndType(path, "image/*");
        shareIntent.putExtra(Intent.EXTRA_STREAM, path);
//        if (!name.equals("")) {
//            shareIntent.putExtra(Intent.EXTRA_TEXT, name + "\n\n" + context.getString(R.string.share_image_message) + "\n\n " + context.getString(R.string.download_from_playstore) + "http://play.google.com/store/apps/details?id=" + context.getPackageName());
//        } else {
//            shareIntent.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.share_image_message) + "\n\n " + context.getString(R.string.download_from_playstore) + "http://play.google.com/store/apps/details?id=" + context.getPackageName());
//        }
        context.startActivity(Intent.createChooser(shareIntent, context.getString(R.string.share_via)));
    }

    public String format(Number number) {
        char[] suffix = {' ', 'k', 'M', 'B', 'T', 'P', 'E'};
        long numValue = number.longValue();
        int value = (int) Math.floor(Math.log10(numValue));
        int base = value / 3;
        if (value >= 3 && base < suffix.length) {
            return new DecimalFormat("#0.0").format(numValue / Math.pow(10, base * 3)) + suffix[base];
        } else {
            return new DecimalFormat("#,##0").format(numValue);
        }
    }

    public String getImageThumbSize(String imagePath, String type) {
        if (type.equals(context.getString(R.string.home))) {
            imagePath = imagePath.replace("&size=300x300", "&size=400x300");
        } else if (type.equals(context.getString(R.string.categories))) {
            imagePath = imagePath.replace("&size=300x300", "&size=400x200");
        } else if (type.equals(context.getString(R.string.latest))) {
            imagePath = imagePath.replace("&size=300x300", "&size=320x270");
        }
        return imagePath;
    }

    public Boolean checkPer() {
        if (android.os.Build.VERSION.SDK_INT >= 29) {
            if ((ContextCompat.checkSelfPermission(context, READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                ((Activity) context).requestPermissions(new String[]{READ_EXTERNAL_STORAGE}, 22);
                return false;
            } else {
                return true;
            }
        } else {
            if ((ContextCompat.checkSelfPermission(context, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    ((Activity) context).requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE}, 22);
                    return false;
                }
            }
            return true;
        }
    }

    public void optionImageQuotes(String option, String url, String id) {
        switch (option) {
            case "setwall":
                new SaveTask("set", id).execute(url);
                break;
            case "share":
                new SaveTask("share", id).execute(url);
                break;
            case "save":
                new SaveTask("save", id).execute(url);
                break;
            default:
                break;
        }
    }

    private void loadDownload(String id) {
        if (isConnectingToInternet()) {
            LoadQuotes loadQuotes = new LoadQuotes(new QuotesListener() {
                @Override
                public void onStart() {

                }

                @Override
                public void onEnd(String success, String verifyStatus, String message, ArrayList<ItemQuotes> arrayListQuotes, int total_records) {

                }
            }, getAPIRequest(Constants.METHOD_DOWNLOAD_COUNT, 0, id, "", "", "", "", "", "", "", "", "", "", "", "", "", "", null));
            loadQuotes.execute();
        }
    }

    public class SaveTask extends AsyncTask<String, String, String> {
        private ProgressDialog pDialog;
        String option, qoute_id, filePath;
        File file;

        SaveTask(String option, String id) {
            this.option = option;
            this.qoute_id = id;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(context, android.app.AlertDialog.THEME_HOLO_LIGHT);
            if (option.equals("save")) {
                pDialog.setMessage(context.getResources().getString(R.string.downloading));
            } else {
                pDialog.setMessage(context.getResources().getString(R.string.please_wait));
            }
            pDialog.setIndeterminate(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            String name = FilenameUtils.getName(strings[0]);
            try {
                if (option.equalsIgnoreCase("save")) {
                    filePath = Environment.getExternalStorageDirectory() + File.separator + Environment.DIRECTORY_PICTURES + File.separator + context.getString(R.string.app_name) + File.separator + name;
                } else {
                    filePath = context.getExternalCacheDir().getAbsoluteFile().getAbsolutePath() + File.separator + name;
                }
                file = new File(filePath);
                if (!file.exists()) {
                    URL url = new URL(strings[0]);

                    InputStream inputStream;

                    if (strings[0].contains("https://")) {
                        HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                        urlConnection.setRequestProperty("Accept", "*/*");
                        urlConnection.setRequestMethod("GET");
                        urlConnection.connect();
                        inputStream = urlConnection.getInputStream();
                    } else {
                        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                        urlConnection.setRequestProperty("Accept", "*/*");
                        urlConnection.setRequestMethod("GET");
                        urlConnection.connect();
                        inputStream = urlConnection.getInputStream();
                    }

                    if (option.equalsIgnoreCase("save")) {
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        boolean isSaved = saveImage(bitmap, name, option);

                        if (isSaved) {
                            return "1";
                        } else {
                            return "2";
                        }
                    } else {
                        if (file.createNewFile()) {
                            file.createNewFile();
                        }

                        FileOutputStream fileOutput = new FileOutputStream(file);

                        byte[] buffer = new byte[1024];
                        int bufferLength = 0;
                        while ((bufferLength = inputStream.read(buffer)) > 0) {
                            fileOutput.write(buffer, 0, bufferLength);
                        }
                        fileOutput.close();
                        return "1";
                    }
                } else {
                    return "2";
                }
            } catch (IOException e) {
                return "0";
            }
        }

        @Override
        protected void onPostExecute(String args) {
            if (args.equals("1") || args.equals("2")) {
                switch (option) {
                    case "save":
                        if (args.equals("2")) {
                            Toast.makeText(context, context.getResources().getString(R.string.quote_already_saved), Toast.LENGTH_SHORT).show();
                        } else {
                            loadDownload(qoute_id);
                            Toast.makeText(context, context.getResources().getString(R.string.quote_saved), Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case "set":
                        Constants.uri_setwall = FileProvider.getUriForFile(context, context.getPackageName().concat(".fileprovider"), file);
                        Intent intent = new Intent(context, SetWallpaperActivity.class);
                        context.startActivity(intent);
                        break;
                    default:
                        Uri contentUri = FileProvider.getUriForFile(context, context.getPackageName().concat(".fileprovider"), file);

                        Intent share = new Intent(Intent.ACTION_SEND);
                        share.setDataAndType(contentUri, context.getContentResolver().getType(contentUri));
                        share.putExtra(Intent.EXTRA_STREAM, contentUri);
                        share.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.get_more_wall) + "\n" + context.getString(R.string.app_name) + " - " + "https://play.google.com/store/apps/details?id=" + context.getPackageName());
                        context.startActivity(Intent.createChooser(share, context.getResources().getString(R.string.share_quote)));
                        pDialog.dismiss();
                        break;
                }
            } else {
                Toast.makeText(context, context.getResources().getString(R.string.please_try_again), Toast.LENGTH_SHORT).show();
            }
            pDialog.dismiss();
        }
    }

    public boolean saveImage(Bitmap bitmap, String fileName, String type) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && (type.equalsIgnoreCase("save") || type.equals("quotemaker"))) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");

            if (type.equalsIgnoreCase("save")) {
                values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + File.separator + context.getString(R.string.app_name));
            } else {
                values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + File.separator + context.getString(R.string.app_name) + File.separator + context.getString(R.string.my_quotes));
            }
            values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000);
            values.put(MediaStore.Images.Media.TITLE, fileName);
            values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
            values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
            values.put(MediaStore.Images.Media.IS_PENDING, true);

            Uri uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            if (uri != null) {
                try {
                    OutputStream outputStream = context.getContentResolver().openOutputStream(uri);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                    outputStream.close();

                    values.put(MediaStore.Images.Media.IS_PENDING, false);
                    context.getContentResolver().update(uri, values, null, null);
                } catch (Exception e) {
                    return false;
                }
                return true;
            } else {
                return false;
            }
        } else {
            File directory;

            if (type.equals("save")) {
                directory = new File(Environment.getExternalStorageDirectory() + File.separator + Environment.DIRECTORY_PICTURES + File.separator + context.getString(R.string.app_name));
            } else if (type.equals("quotemaker")) {
                directory = new File(Environment.getExternalStorageDirectory() + File.separator + Environment.DIRECTORY_PICTURES + File.separator + context.getString(R.string.app_name) + File.separator + context.getString(R.string.my_quotes));
            } else {
                directory = new File(context.getExternalCacheDir().getAbsoluteFile().getAbsolutePath() + File.separator);
            }

            if (!directory.exists()) {
                directory.mkdirs();
            }
            File file = new File(directory, fileName);

            try {
                OutputStream outputStream = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                outputStream.close();

                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
                return true;
            } catch (Exception e) {
                return false;
            }
        }
    }

    public GradientDrawable getRoundDrawable(int color) {
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(color);
        gd.mutate();
        gd.setCornerRadius(10);
        return gd;
    }

    public String getTempUploadPath(Uri uri) {
        File root = context.getExternalCacheDir().getAbsoluteFile();
        try {
            String filePath = root.getPath() + File.separator + System.currentTimeMillis() + ".jpg";

            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            Bitmap bm = BitmapFactory.decodeStream(inputStream);

            if (saveBitMap(root, bm, filePath)) {
                return filePath;
            } else {
                return "";
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    private boolean saveBitMap(File root, Bitmap Final_bitmap, String filePath) {
        if (!root.exists()) {
            boolean isDirectoryCreated = root.mkdirs();
            if (!isDirectoryCreated)
                Log.i("SANJAY ", "Can't create directory to save the image");
            return false;
        }
        String filename = filePath;
        File pictureFile = new File(filename);
        try {
            pictureFile.createNewFile();
            FileOutputStream oStream = new FileOutputStream(pictureFile);
            Final_bitmap.compress(Bitmap.CompressFormat.PNG, 18, oStream);
            oStream.flush();
            oStream.close();

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void getVerifyDialog(String title, String message) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context, R.style.AlertDialogTheme);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setCancelable(false);

        alertDialog.setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                finish();
            }
        });
        alertDialog.show();
    }

    public void getInvalidUserDialog(String message) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context, R.style.AlertDialogTheme);
        alertDialog.setTitle(context.getString(R.string.invalid_user));
        alertDialog.setMessage(message);
        alertDialog.setCancelable(false);

        alertDialog.setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                logout((Activity) context);
            }
        });
        alertDialog.show();
    }

    public void showUpdateAlert(String message, boolean isSplash) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context, R.style.AlertDialogTheme);
        alertDialog.setTitle(context.getString(R.string.update));
        alertDialog.setMessage(message);
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton(context.getString(R.string.update), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String url = Constants.appUpdateURL;
                if (url.equals("")) {
                    url = "http://play.google.com/store/apps/details?id=" + context.getPackageName();
                }
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                context.startActivity(i);

                ((Activity) context).finish();
            }
        });
        if (Constants.appUpdateCancel) {
            alertDialog.setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (isSplash) {
                        Intent intent = new Intent(context, MainActivity.class);
                        context.startActivity(intent);
                        ((Activity) context).finish();
                    }
                }
            });
        } else {
            alertDialog.setNegativeButton(context.getString(R.string.exit), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ((Activity) context).finish();
                }
            });
        }
        alertDialog.show();
    }

    String encrypt(String value) {
        try {
            Crypto crypto = new Crypto(Options.TRANSFORMATION_SYMMETRIC);
            return crypto.encrypt(value, key);
        } catch (Exception e) {
            Crypto crypto = new Crypto(Options.TRANSFORMATION_SYMMETRIC);
            return crypto.encrypt("null", key);
        }
    }

    String decrypt(String value) {
        try {
            Crypto crypto = new Crypto(Options.TRANSFORMATION_SYMMETRIC);
            return crypto.decrypt(value, key);
        } catch (Exception e) {
            e.printStackTrace();
            return "null";
        }
    }

    public RequestBody getAPIRequest(String method, int page, String quotesID, String searchText, String like, String catID, String type, String tags, String colorText, String font, String colorBG, String email, String password, String name, String phone, String userID, String reportMessage, File file) {
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        jsObj.addProperty("method_name", method);
        jsObj.addProperty("package_name", context.getPackageName());

        switch (method) {
            case Constants.METHOD_HOME:
                jsObj.addProperty("user_id", userID);
                break;
            case Constants.METHOD_SEARCH_IMAGE:
                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("search_image_quotes", searchText);
                jsObj.addProperty("page", page);
                break;
            case Constants.METHOD_SEARCH_TEXT:
                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("search_text_quotes", searchText);
                jsObj.addProperty("page", page);
                break;
            case Constants.METHOD_QUOTES_CAT_IMAGE:
                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("image_quotes_cat_id", catID);
                jsObj.addProperty("page", page);
                break;
            case Constants.METHOD_QUOTES_CAT_TEXT:
                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("text_quotes_cat_id", catID);
                jsObj.addProperty("page", page);
                break;
            case Constants.METHOD_SINGLE_IMAGE:
                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("image_single_quotes_id", quotesID);
                break;
            case Constants.METHOD_SINGLE_TEXT:
                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("text_single_quotes_id", quotesID);
                break;
            case Constants.METHOD_DOWNLOAD_COUNT:
                jsObj.addProperty("quotes_download_id", quotesID);
                break;
            case Constants.METHOD_QUOTES_LATEST_IMAGE:
                jsObj.addProperty("page", page);
                jsObj.addProperty("user_id", userID);
                break;
            case Constants.METHOD_QUOTES_LATEST_TEXT:
                jsObj.addProperty("page", page);
                jsObj.addProperty("user_id", userID);
                break;
            case Constants.METHOD_QUOTES_TOP_LIKE_IMAGE:
                jsObj.addProperty("page", page);
                jsObj.addProperty("user_id", userID);
                break;
            case Constants.METHOD_QUOTES_TOP_LIKE_TEXT:
                jsObj.addProperty("page", page);
                jsObj.addProperty("user_id", userID);
                break;
            case Constants.METHOD_QUOTES_POPULAR_IMAGE:
                jsObj.addProperty("page", page);
                jsObj.addProperty("user_id", userID);
                break;
            case Constants.METHOD_QUOTES_POPULAR_TEXT:
                jsObj.addProperty("page", page);
                jsObj.addProperty("user_id", userID);
                break;
            case Constants.METHOD_QUOTES_FAVOURITE:
                jsObj.addProperty("type", type);
                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("page", page);
                break;
            case Constants.METHOD_FAV_QUOTE:
                jsObj.addProperty("type", type);
                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("post_id", quotesID);
                break;
            case Constants.METHOD_USER_QUOTES_TEXT:
                jsObj.addProperty("page", page);
                jsObj.addProperty("user_id", userID);
                break;
            case Constants.METHOD_USER_QUOTES_IMAGES:
                jsObj.addProperty("page", page);
                jsObj.addProperty("user_id", userID);
                break;
            case Constants.METHOD_USER_QUOTES_DELETE:
                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("quote_id", quotesID);
                jsObj.addProperty("type", type);
                break;
            case Constants.METHOD_LIKE_IMAGE:
                jsObj.addProperty("like", like);
                jsObj.addProperty("quote_id", quotesID);
                jsObj.addProperty("user_id", userID);
                break;
            case Constants.METHOD_LIKE_TEXT:
                jsObj.addProperty("like", like);
                jsObj.addProperty("quote_id", quotesID);
                jsObj.addProperty("user_id", userID);
                break;
            case Constants.METHOD_PROFILE:
                jsObj.addProperty("id", userID);
                break;
            case Constants.METHOD_PROFILE_UPDATE:
                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("name", name);
                jsObj.addProperty("email", email);
                jsObj.addProperty("password", password);
                jsObj.addProperty("phone", phone);
                break;
            case Constants.METHOD_LOGIN:
                jsObj.addProperty("email", email);
                jsObj.addProperty("password", password);
                jsObj.addProperty("auth_id", userID);
                jsObj.addProperty("type", type);
                break;
            case Constants.METHOD_REGISTER:
                jsObj.addProperty("name", name);
                jsObj.addProperty("email", email);
                jsObj.addProperty("password", password);
                jsObj.addProperty("phone", phone);
                jsObj.addProperty("type", type);
                jsObj.addProperty("auth_id", userID);
                break;
            case Constants.METHOD_FORGOT_PASS:
                jsObj.addProperty("user_email", email);
                break;
            case Constants.METHOD_UPLOAD_IMAGE:
                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("cat_id", catID);
                jsObj.addProperty("quote_tags", tags);
                break;
            case Constants.METHOD_UPLOAD_TEXT:
                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("cat_id", catID);
                jsObj.addProperty("quote", searchText);
                jsObj.addProperty("quote_tags", tags);
                jsObj.addProperty("quote_font", font);
                jsObj.addProperty("font_color", colorText);
                jsObj.addProperty("bg_color", colorBG);
                break;
            case Constants.METHOD_REPORT:
                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("post_id", quotesID);
                jsObj.addProperty("type", type);
                jsObj.addProperty("report", reportMessage);
                break;
        }

        if (file != null) {
            final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/*");

            if (method.equals(Constants.METHOD_REGISTER) || method.equals(Constants.METHOD_PROFILE_UPDATE)) {
                return new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("user_profile", file.getName(), RequestBody.create(MEDIA_TYPE_PNG, file))
                        .addFormDataPart("data", API.toBase64(jsObj.toString()))
                        .build();
            } else {
                return new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("quote_image", file.getName(), RequestBody.create(MEDIA_TYPE_PNG, file))
                        .addFormDataPart("data", API.toBase64(jsObj.toString()))
                        .build();
            }
        } else {
            return new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("data", API.toBase64(jsObj.toString()))
                    .build();
        }
    }


    public void showFacdebookAd(Context activity1)
    {
        AudienceNetworkAds.initialize(activity1);
        if (!fbads.equals("")) {
            interstitialAd.show();
        } else {
            Log.d("TAG", "The interstitial ad wasn't ready yet.");

            //  AudienceNetworkAds.initialize(activity1);
            interstitialAd = new com.facebook.ads.InterstitialAd(activity1, "4684674478253721_4684709608250208");


            // Create listeners for the Interstitial Ad
            InterstitialAdListener interstitialAdListener = new InterstitialAdListener() {
                @Override
                public void onInterstitialDisplayed(Ad ad) {
                    // Interstitial ad displayed callback
                    //Log.e(TAG, "Interstitial ad displayed.");
                    fbads="";
                     interstitialAd.loadAd();

                }

                @Override
                public void onInterstitialDismissed(Ad ad) {
                    // Interstitial dismissed callback
                    //Log.e(TAG, "Interstitial ad dismissed.");
                    fbads="";
                    interstitialAd.loadAd();



                }

                @Override
                public void onError(Ad ad, AdError adError) {
                    // Ad error callback
                    Log.e("KINGSN", "FBInterstitial ad failed to load: " + adError.getErrorMessage());

                    fbads="";
                    interstitialAd.loadAd();
                }

                @Override
                public void onAdLoaded(Ad ad) {
                    // Interstitial ad is loaded and ready to be displayed
                    Log.d("KINGSN", "FBInterstitial ad is loaded and ready to be displayed!");
                    // Show the ad
                    //interstitialAd.show();
                    fbads="loaded";
                }

                @Override
                public void onAdClicked(Ad ad) {
                    // Ad clicked callback
                    //Log.d(TAG, "Interstitial ad clicked!");
                }

                @Override
                public void onLoggingImpression(Ad ad) {
                    // Ad impression logged callback
                    //Log.d(TAG, "Interstitial ad impression logged!");
                }
            };

            // For auto play video ads, it's recommended to load the ad
            // at least 30 seconds before it is shown
            interstitialAd.loadAd(
                    interstitialAd.buildLoadAdConfig()
                            .withAdListener(interstitialAdListener)
                            .build());
        }
    }

    public void showFbBanner( LinearLayout frameLayout)
    {
       // Log.d("KINGSN", "showFbBanner2: "+Constant.settings.getFb_banner_ad_id());

        adViewfb = new com.facebook.ads.AdView(frameLayout.getContext(), "4684674478253721_4684708694916966", com.facebook.ads.AdSize.BANNER_HEIGHT_50);
        // Find the Ad Container
        LinearLayout adContainer = (frameLayout);

        // Add the ad view to your activity layout
        adContainer.addView(adViewfb);

        adViewfb.loadAd();
        // advieww.setVisibility(View.VISIBLE);

    }

    public void loadNativeAd() {
        // Instantiate a NativeAd object.
        // NOTE: the placement ID will eventually identify this as your App, you can ignore it for
        // now, while you are testing and replace it later when you have signed up.
        // While you are using this temporary code you will only get test ads and if you release
        // your code like this to the Google Play your users will not receive ads (you will get a no fill error).
        fbnativeAd = new NativeAd(context, "4684674478253721_4684710314916804");

        NativeAdListener nativeAdListener = new NativeAdListener() {
            @Override
            public void onMediaDownloaded(Ad ad) {
                // Native ad finished downloading all assets
                Log.e(TAG, "Native ad finished downloading all assets.");
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                // Native ad failed to load
                Log.e(TAG, "Native ad failed to load: " + adError.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Native ad is loaded and ready to be displayed
                Log.d(TAG, "Native ad is loaded and ready to be displayed!");
//                context.adapter.addAds(nativeAd);
                 fbnativeLoaded="true";
//                adapter.addAds(nativeAd);
              //  adapterImageQuotes.addAds(nativeAd);
            }

            @Override
            public void onAdClicked(Ad ad) {
                // Native ad clicked
                Log.d(TAG, "Native ad clicked!");
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Native ad impression
                Log.d(TAG, "Native ad impression logged!");
            }
        };

        // Request an ad
        fbnativeAd.loadAd(
                fbnativeAd.buildLoadAdConfig()
                        .withAdListener(nativeAdListener)
                        .build());
    }
}