package com.royal.attitude.status;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.royal.attitude.status.asyncTask.LoadAbout;
import com.royal.attitude.status.fragments.FragmentCat;
import com.royal.attitude.status.fragments.FragmentFav;
import com.royal.attitude.status.fragments.FragmentHome;
import com.royal.attitude.status.fragments.FragmentLatest;
import com.royal.attitude.status.fragments.FragmentMyColl;
import com.royal.attitude.status.fragments.FragmentTopLiked;
import com.royal.attitude.status.interfaces.AboutListener;
import com.royal.attitude.status.interfaces.AdConsentListener;
import com.royal.attitude.status.utils.AdConsent;
import com.royal.attitude.status.utils.AdManagerInter;
import com.royal.attitude.status.utils.Constants;
import com.royal.attitude.status.utils.DBHelper;
import com.royal.attitude.status.utils.Methods;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    Methods methods;
    View view;
    FragmentManager fm;
    NavigationView navigationView;
    DrawerLayout drawer;
    ProgressDialog pbar;
    private LinearLayout ll_ad;
    AdConsent adConsent;
    DBHelper dbHelper;
    MenuItem menu_login, menu_profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        try {
//            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
//            for (Signature signature : info.signatures) {
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
//            }
//        } catch (PackageManager.NameNotFoundException e) {
//
//        } catch (NoSuchAlgorithmException e) {
//
//        }

        dbHelper = new DBHelper(this);
        methods = new Methods(this);
        methods.setStatusColor(getWindow());
        methods.forceRTLIfSupported(getWindow());

        pbar = new ProgressDialog(this);
        pbar.setMessage(getResources().getString(R.string.loading));
        pbar.setCancelable(false);

        fm = getSupportFragmentManager();

        ll_ad = findViewById(R.id.ll_adView);
        adConsent = new AdConsent(this, new AdConsentListener() {
            @Override
            public void onConsentUpdate() {
                methods.showBannerAd(ll_ad);
            }
        });

      //  methods.showFacdebookAd(MainActivity.this);

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.setDrawerIndicatorEnabled(false);

        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.openDrawer(GravityCompat.START);
            }
        });
        toggle.setHomeAsUpIndicator(R.mipmap.ic_nav);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        Menu menu = navigationView.getMenu();
        menu_login = menu.findItem(R.id.nav_login);
        menu_profile = menu.findItem(R.id.nav_profile);

        changeLoginName();

        try {
            if (methods.isConnectingToInternet()) {
                loadAbout();
            } else {
                dbHelper.getAbout();
                adConsent.checkForConsent();
                Toast.makeText(MainActivity.this, getResources().getString(R.string.err_internet_not_conn), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        FragmentHome f1 = new FragmentHome();
        loadFrag(f1, getString(R.string.home));
        navigationView.setCheckedItem(R.id.nav_home);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            FragmentHome f1 = new FragmentHome();
            loadFrag(f1, getString(R.string.home));

        } else if (id == R.id.nav_category) {
            FragmentCat fcat = new FragmentCat();
            loadFrag(fcat, getString(R.string.categories));

        } else if (id == R.id.nav_latest) {
            FragmentLatest f1 = new FragmentLatest();
            loadFrag(f1, getString(R.string.latest));

        } else if (id == R.id.nav_topLiked) {
            FragmentTopLiked fragmentTopLiked = new FragmentTopLiked();
            loadFrag(fragmentTopLiked, getString(R.string.topliked));

        } else if (id == R.id.nav_fav) {
            FragmentFav frag = new FragmentFav();
            loadFrag(frag, getString(R.string.favourites));

        } else if (id == R.id.nav_myCollection) {
            if (methods.checkPer()) {
                FragmentMyColl fmy = new FragmentMyColl();
                loadFrag(fmy, getString(R.string.my_collection));
            }
        } else if (id == R.id.nav_uploads) {
            Intent intent = new Intent(MainActivity.this, UploadQuotes.class);
            startActivity(intent);

        } else if (id == R.id.nav_profile) {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_login) {
            methods.clickLogin();
        } else if (id == R.id.nav_settings) {
            Intent intent = new Intent(MainActivity.this, SettingActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_quotemaker) {
            Intent intent = new Intent(MainActivity.this, MakeQuotesActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void setTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    public void loadFrag(Fragment f1, String name) {
        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }

        FragmentTransaction ft = fm.beginTransaction();
//        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        if (!name.equals(getString(R.string.home))) {
            ft.hide(fm.getFragments().get(fm.getBackStackEntryCount()));
            ft.add(R.id.frame_layout, f1, name);
            ft.addToBackStack(name);
        } else {
            ft.replace(R.id.frame_layout, f1, name);
        }
        ft.commitAllowingStateLoss();

        getSupportActionBar().setTitle(name);
    }

    private void changeLoginName() {
        if (menu_login != null) {
            if (Constants.isLogged) {
                menu_profile.setVisible(true);
                menu_login.setTitle(getResources().getString(R.string.logout));
                menu_login.setIcon(getResources().getDrawable(R.mipmap.logout));
            } else {
                menu_profile.setVisible(false);
                menu_login.setTitle(getResources().getString(R.string.login));
                menu_login.setIcon(getResources().getDrawable(R.mipmap.login));
            }
        }
    }

    private void exitDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this, R.style.AlertDialogTheme);

        alert.setTitle(getString(R.string.exit));
        alert.setMessage(getString(R.string.sure_exit));
        alert.setPositiveButton(getString(R.string.exit), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        alert.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        alert.show();
    }

    private void loadAbout() {
        LoadAbout loadAbout = new LoadAbout(new AboutListener() {
            @Override
            public void onStart() {
                pbar.show();
            }

            @Override
            public void onEnd(String success, String verifyStatus, String message) {
                pbar.dismiss();
                if (!verifyStatus.equals("-1") && !verifyStatus.equals("-2")) {
                    String version = "";
                    try {
                        PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                        version = String.valueOf(pInfo.versionCode);
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                    if (Constants.showUpdateDialog && !Constants.appVersion.equals(version)) {
                        methods.showUpdateAlert(Constants.appUpdateMsg, false);
                    } else {
                        adConsent.checkForConsent();
                        dbHelper.addtoAbout();
                    }

                    AdManagerInter adManagerAdmobInter = new AdManagerInter(MainActivity.this);
                    adManagerAdmobInter.createAd();
                } else if (verifyStatus.equals("-2")) {
                    methods.getInvalidUserDialog(message);
                } else {
                    methods.getVerifyDialog(getString(R.string.error_unauth_access), message);
                }
            }

        }, methods.getAPIRequest(Constants.METHOD_ABOUT, 0, "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", null));
        loadAbout.execute();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (fm.getBackStackEntryCount() != 0) {
            String title = fm.getFragments().get(fm.getBackStackEntryCount() - 1).getTag();
            if (title.equals(getString(R.string.home))) {
                title = getString(R.string.home);
                navigationView.setCheckedItem(R.id.nav_home);
            }
            getSupportActionBar().setTitle(title);
            super.onBackPressed();
        } else {
            exitDialog();
        }
    }

    @Override
    protected void onResume() {
        changeLoginName();
        super.onResume();
    }
}