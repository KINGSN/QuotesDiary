package com.royal.attitude.status;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.royal.attitude.status.interfaces.AdConsentListener;
import com.royal.attitude.status.utils.AdConsent;
import com.royal.attitude.status.utils.Constants;
import com.royal.attitude.status.utils.Methods;
import com.royal.attitude.status.utils.SharedPref;
import com.google.ads.consent.ConsentInformation;
import com.google.ads.consent.ConsentStatus;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.radiobutton.MaterialRadioButton;
import com.onesignal.OneSignal;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.text.DecimalFormat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.viewpager.widget.ViewPager;

public class SettingActivity extends AppCompatActivity {

    Toolbar toolbar;
    Methods methods;
    SharedPref sharedPref;
    AdConsent adConsent;
    LinearLayout ll_consent, ll_adView, ll_clearcache;
    SwitchCompat switch_consent, switch_noti;
    Boolean isNoti = true;
    View view_moreapp;
    ProgressDialog progressDialog;
    TextView tv_privacy, tv_moreapp, tv_rateapp, tv_cachesize, tv_about, tv_theme;
    String them_mode = "";
    ConstraintLayout ll_theme;
    ImageView iv_theme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        sharedPref = new SharedPref(this);
        methods = new Methods(this);
        methods.setStatusColor(getWindow());
        methods.forceRTLIfSupported(getWindow());

        isNoti = sharedPref.getIsNotification();
        them_mode = methods.getDarkMode();

        toolbar = this.findViewById(R.id.toolbar_setting);
        toolbar.setTitle(getString(R.string.settings));
        this.setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog = new ProgressDialog(SettingActivity.this);
        progressDialog.setMessage(getString(R.string.clearing_cache));

        adConsent = new AdConsent(this, new AdConsentListener() {
            @Override
            public void onConsentUpdate() {
                setConsentSwitch();
            }
        });

        ll_theme = findViewById(R.id.ll_theme);
        ll_consent = findViewById(R.id.ll_consent);
        switch_noti = findViewById(R.id.switch_noti);
        switch_consent = findViewById(R.id.switch_consent);
        tv_theme = findViewById(R.id.tv_theme);
        tv_rateapp = findViewById(R.id.tv_rateapp);
        tv_moreapp = findViewById(R.id.tv_moreapp);
        tv_privacy = findViewById(R.id.tv_privacy);
        tv_cachesize = findViewById(R.id.tv_cachesize);
        tv_about = findViewById(R.id.tv_about);
        ll_adView = findViewById(R.id.ll_adView);
        ll_clearcache = findViewById(R.id.ll_cache);
        iv_theme = findViewById(R.id.iv_theme);
        view_moreapp = findViewById(R.id.view_moreapp);

        methods.showBannerAd(ll_adView);

        if (getString(R.string.play_more_apps).equals("")) {
            view_moreapp.setVisibility(View.GONE);
            tv_moreapp.setVisibility(View.GONE);
        }

        initializeCache();

        if (adConsent.isUserFromEEA()) {
            setConsentSwitch();
        } else {
            ll_consent.setVisibility(View.GONE);
        }

        switch_noti.setChecked(isNoti);
        switch_noti.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                OneSignal.setSubscription(isChecked);
                sharedPref.setIsNotification(isChecked);
            }
        });

        if (methods.isDarkMode()) {
            iv_theme.setImageResource(R.mipmap.mode_dark);
        } else {
            iv_theme.setImageResource(R.mipmap.mode_icon);
        }

        String mode = methods.getDarkMode();
        switch (mode) {
            case Constants.DARK_MODE_SYSTEM:
                tv_theme.setText(getString(R.string.system_default));
                break;
            case Constants.DARK_MODE_OFF:
                tv_theme.setText(getString(R.string.light));
                break;
            case Constants.DARK_MODE_ON:
                tv_theme.setText(getString(R.string.dark));
                break;
        }

        switch_consent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    ConsentInformation.getInstance(SettingActivity.this).setConsentStatus(ConsentStatus.PERSONALIZED);
                } else {
                    ConsentInformation.getInstance(SettingActivity.this).setConsentStatus(ConsentStatus.NON_PERSONALIZED);
                }
            }
        });

        tv_rateapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String appName = getPackageName();//your application package name i.e play store application url
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("market://details?id="
                                    + appName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id="
                                    + appName)));
                }
            }
        });

        tv_privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPrivacyDialog();
            }
        });

        tv_moreapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.play_more_apps))));
            }
        });

        ll_consent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adConsent.requestConsent();
            }
        });

        ll_theme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openThemeDialog();
            }
        });

        ll_clearcache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AsyncTask<String, String, String>() {
                    @Override
                    protected void onPreExecute() {
                        progressDialog.show();
                        super.onPreExecute();
                    }

                    @Override
                    protected String doInBackground(String... strings) {
                        FileUtils.deleteQuietly(getCacheDir());
                        FileUtils.deleteQuietly(getExternalCacheDir());
                        return null;
                    }

                    @Override
                    protected void onPostExecute(String s) {
                        progressDialog.dismiss();
                        Toast.makeText(SettingActivity.this, getString(R.string.cache_cleared), Toast.LENGTH_SHORT).show();
                        tv_cachesize.setText("0 MB");
                        super.onPostExecute(s);
                    }
                }.execute();
            }
        });

        tv_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, AcitivityAbout.class);
                startActivity(intent);
            }
        });

        changeThemeColor();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            onBackPressed();
        } else {
            return super.onOptionsItemSelected(menuItem);
        }
        return true;
    }

    private void setConsentSwitch() {
        switch_consent.setChecked(ConsentInformation.getInstance(this).getConsentStatus() == ConsentStatus.PERSONALIZED);
    }

    private void openThemeDialog() {
        final Dialog dialog = new Dialog(SettingActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_theme);
        if (getString(R.string.isRTL).equals("true")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                dialog.getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
        }
        dialog.getWindow().setLayout(ViewPager.LayoutParams.MATCH_PARENT, ViewPager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        RadioGroup radioGroup = dialog.findViewById(R.id.radioGroup_them);
        MaterialButton btn_ok = dialog.findViewById(R.id.textView_ok_them);
        MaterialButton btn_cancel = dialog.findViewById(R.id.textView_cancel_them);

        String mode = methods.getDarkMode();
        assert mode != null;
        switch (mode) {
            case Constants.DARK_MODE_SYSTEM:
                radioGroup.check(radioGroup.getChildAt(0).getId());
                break;
            case Constants.DARK_MODE_OFF:
                radioGroup.check(radioGroup.getChildAt(1).getId());
                break;
            case Constants.DARK_MODE_ON:
                radioGroup.check(radioGroup.getChildAt(2).getId());
                break;
        }

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                MaterialRadioButton rb = group.findViewById(checkedId);
                if (null != rb && checkedId > -1) {
                    switch (checkedId) {
                        case R.id.radioButton_system_them:
                            them_mode = Constants.DARK_MODE_SYSTEM;
                            break;
                        case R.id.radioButton_light_them:
                            them_mode = Constants.DARK_MODE_OFF;
                            break;
                        case R.id.radioButton_dark_them:
                            them_mode = Constants.DARK_MODE_ON;
                            break;
                        default:
                            break;
                    }
                }
            }
        });

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPref.setDarkMode(them_mode);
                switch (them_mode) {
                    case Constants.DARK_MODE_SYSTEM:
                        tv_theme.setText(getResources().getString(R.string.system_default));
                        break;
                    case Constants.DARK_MODE_OFF:
                        tv_theme.setText(getResources().getString(R.string.light));
                        break;
                    case Constants.DARK_MODE_ON:
                        tv_theme.setText(getResources().getString(R.string.dark));
                        break;
                    default:
                        break;
                }
                dialog.dismiss();

                String mode = sharedPref.getDarkMode();
                switch (mode) {
                    case Constants.DARK_MODE_SYSTEM:
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                        break;
                    case Constants.DARK_MODE_OFF:
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                        break;
                    case Constants.DARK_MODE_ON:
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                        break;
                }

            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void openPrivacyDialog() {
        Dialog dialog;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialog = new Dialog(SettingActivity.this, android.R.style.Theme_Material_Light_Dialog_Alert);
        } else {
            dialog = new Dialog(SettingActivity.this);
        }

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_privacy);

        WebView webview = dialog.findViewById(R.id.webview);
        webview.getSettings().setJavaScriptEnabled(true);
        String mimeType = "text/html;charset=UTF-8";
        String encoding = "utf-8";

        if (Constants.itemAbout != null) {
            String text = "";
            if (methods.isDarkMode()) {
                text = "<html><head>"
                        + "<style> body{color:#fff !important;text-align:left}"
                        + "</style></head>"
                        + "<body>"
                        + Constants.itemAbout.getPrivacy()
                        + "</body></html>";
            } else {
                text = "<html><head>"
                        + "<style> body{color:#000 !important;text-align:left}"
                        + "</style></head>"
                        + "<body>"
                        + Constants.itemAbout.getPrivacy()
                        + "</body></html>";
            }

            webview.setBackgroundColor(Color.TRANSPARENT);
            webview.loadDataWithBaseURL("blarg://ignored", text, mimeType, encoding, "");
        }

        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    private void changeThemeColor() {

        int[][] states = new int[][]{
                new int[]{-android.R.attr.state_checked},
                new int[]{android.R.attr.state_checked},
        };

        int[] thumbColors = new int[]{
                ContextCompat.getColor(SettingActivity.this, R.color.switch_thumb_disable),
                ContextCompat.getColor(SettingActivity.this, R.color.primary),
        };

        int[] trackColors = new int[]{
                ContextCompat.getColor(SettingActivity.this, R.color.switch_track_disable),
                ContextCompat.getColor(SettingActivity.this, R.color.switch_track),
        };
        DrawableCompat.setTintList(DrawableCompat.wrap(switch_noti.getThumbDrawable()), new ColorStateList(states, thumbColors));
        DrawableCompat.setTintList(DrawableCompat.wrap(switch_noti.getTrackDrawable()), new ColorStateList(states, trackColors));
        DrawableCompat.setTintList(DrawableCompat.wrap(switch_consent.getThumbDrawable()), new ColorStateList(states, thumbColors));
        DrawableCompat.setTintList(DrawableCompat.wrap(switch_consent.getTrackDrawable()), new ColorStateList(states, trackColors));
    }

    private void initializeCache() {
        long size = 0;
        size += getDirSize(this.getCacheDir());
        size += getDirSize(this.getExternalCacheDir());
        tv_cachesize.setText(readableFileSize(size));
    }

    public long getDirSize(File dir) {
        long size = 0;
        for (File file : dir.listFiles()) {
            if (file != null && file.isDirectory()) {
                size += getDirSize(file);
            } else if (file != null && file.isFile()) {
                size += file.length();
            }
        }
        return size;
    }

    public static String readableFileSize(long size) {
        if (size <= 0) return "0 Bytes";
        final String[] units = new String[]{"Bytes", "kB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }
}