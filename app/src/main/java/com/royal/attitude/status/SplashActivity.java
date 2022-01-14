package com.royal.attitude.status;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;

import com.royal.attitude.status.asyncTask.LoadAbout;
import com.royal.attitude.status.asyncTask.LoadLogin;
import com.royal.attitude.status.interfaces.AboutListener;
import com.royal.attitude.status.interfaces.LoginListener;
import com.royal.attitude.status.item.ItemUser;
import com.royal.attitude.status.utils.Constants;
import com.royal.attitude.status.utils.DBHelper;
import com.royal.attitude.status.utils.Methods;
import com.royal.attitude.status.utils.SharedPref;
import com.facebook.AccessToken;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    DBHelper dbHelper;
    SharedPref sharedPref;
    Methods methods;
    ProgressBar progressbar_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        dbHelper = new DBHelper(this);
        dbHelper.getAbout();

        try {
            Constants.isFromPush = getIntent().getExtras().getBoolean("ispushnoti", false);
        } catch (Exception e) {
            Constants.isFromPush = false;
        }

        sharedPref = new SharedPref(this);
        methods = new Methods(this);
        methods.setStatusColor(getWindow());
        hideStatusBar();

        progressbar_login = findViewById(R.id.progressbar_login);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (sharedPref.getIsFirst()) {
                    loadAboutData();
                } else {
                    if (!sharedPref.getIsAutoLogin()) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                openMainActivity();
                            }
                        }, 2000);
                    } else {
                        if (methods.isConnectingToInternet()) {
                            if(sharedPref.getLoginType().equals(Constants.LOGIN_TYPE_FB)) {
                                if(AccessToken.getCurrentAccessToken() != null) {
                                    loadLogin(Constants.LOGIN_TYPE_FB, sharedPref.getAuthID());
                                } else {
                                    sharedPref.setIsAutoLogin(false);
                                    openMainActivity();
                                }
                            } else if(sharedPref.getLoginType().equals(Constants.LOGIN_TYPE_GOOGLE)) {
                                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                                if(currentUser != null) {
                                    loadLogin(Constants.LOGIN_TYPE_GOOGLE, sharedPref.getAuthID());
                                } else {
                                    sharedPref.setIsAutoLogin(false);
                                    openMainActivity();
                                }
                            } else {
                                loadLogin(Constants.LOGIN_TYPE_NORMAL, "");
                            }
                        } else {
                            openMainActivity();
                        }
                    }
                }
            }
        }, 500);
    }

    private void loadAboutData() {
        if (methods.isConnectingToInternet()) {

            LoadAbout loadAbout = new LoadAbout(new AboutListener() {
                @Override
                public void onStart() {

                }

                @Override
                public void onEnd(String success, String verifyStatus, String message) {
                    if (success.equals("1")) {
                        if (!verifyStatus.equals("-1") && !verifyStatus.equals("-2")) {
                            String version = "";
                            try {
                                PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                                version = String.valueOf(pInfo.versionCode);
                            } catch (PackageManager.NameNotFoundException e) {
                                e.printStackTrace();
                            }
                            if(Constants.showUpdateDialog && !Constants.appVersion.equals(version)) {
                                methods.showUpdateAlert(Constants.appUpdateMsg, true);
                            } else {
                                dbHelper.addtoAbout();
                                sharedPref.setIsFirst(false);
                                openLoginActivity();
                            }
                        } else if(verifyStatus.equals("-2")) {
                            methods.getInvalidUserDialog(message);
                        } else {
                            errorDialog(getString(R.string.error_unauth_access), message);
                        }
                    } else {
                        errorDialog(getString(R.string.err_server), getString(R.string.err_server));
                    }
                }
            }, methods.getAPIRequest(Constants.METHOD_ABOUT, 0,  "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", null));
            loadAbout.execute();
        } else {
            errorDialog(getString(R.string.err_internet_not_conn), getString(R.string.err_connect_net_try));
        }
    }

    private void loadLogin(String loginType, String authID) {
        LoadLogin loadLogin = new LoadLogin(new LoginListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onEnd(String success, String loginSuccess, String message, String user_id, String user_name) {

                if (success.equals("1")) {
                    if (loginSuccess.equals("1")) {
                        Constants.itemUser = new ItemUser(user_id, user_name, sharedPref.getEmail(), "", "","", loginType);
                        Constants.isLogged = true;
                    }
                    openMainActivity();
                } else {
                    openLoginActivity();
                }
            }
        }, methods.getAPIRequest(Constants.METHOD_LOGIN, 0, "", "", "", "", loginType, "", "", "", "", sharedPref.getEmail(), sharedPref.getPassword(), "", "", authID, "", null));
        loadLogin.execute();
    }

    private void openLoginActivity() {
        Intent intent;
        if (sharedPref.getIsFirst()) {
            sharedPref.setIsFirst(false);
            intent = new Intent(SplashActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("from", "");
        } else {
            intent = new Intent(SplashActivity.this, MainActivity.class);
        }
        startActivity(intent);
        finish();
    }

    private void openMainActivity() {
        Intent intent;
        if (Constants.pushCID.equals("0")) {
            intent = new Intent(SplashActivity.this, MainActivity.class);
        } else {
            intent = new Intent(SplashActivity.this, QuotesByCategory.class);
            intent.putExtra("cid", Constants.pushCID);
            intent.putExtra("cname", Constants.pushCName);
            intent.putExtra("pos", 0);
        }
        startActivity(intent);
        finish();
    }

    private void errorDialog(String title, String message) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(SplashActivity.this, R.style.AlertDialogTheme);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setCancelable(false);

        if (title.equals(getString(R.string.err_internet_not_conn)) || title.equals(getString(R.string.err_server))) {
            alertDialog.setNegativeButton(getString(R.string.try_again), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    loadAboutData();
                }
            });
        }

        alertDialog.setPositiveButton(getString(R.string.exit), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        alertDialog.show();
    }

    void hideStatusBar() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }
}