package com.royal.attitude.status;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.royal.attitude.status.asyncTask.LoadProfile;
import com.royal.attitude.status.fragments.FragmentUserQuotes;
import com.royal.attitude.status.interfaces.SuccessListener;
import com.royal.attitude.status.utils.Constants;
import com.royal.attitude.status.utils.Methods;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.tabs.TabLayout;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class ProfileActivity extends AppCompatActivity {

    Methods methods;
    Toolbar toolbar;
    ViewPager viewPager;
    ViewPagerAdapter adapter;
    RoundedImageView iv_prof;
    TextView textView_email, textView_mobile, textView_notlog;
    ProgressDialog progressDialog;
    AppBarLayout appbar;
    CollapsingToolbarLayout collapsing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        toolbar = findViewById(R.id.toolbar_pro);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        methods = new Methods(this);
        methods.forceRTLIfSupported(getWindow());
        methods.setStatusColor(getWindow());

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);

        appbar = findViewById(R.id.appbar);
        collapsing = findViewById(R.id.collapsing);
        viewPager = findViewById(R.id.viewpager_profile);
        iv_prof = findViewById(R.id.iv_prof);
        textView_email = findViewById(R.id.tv_prof_email);
        textView_mobile = findViewById(R.id.tv_prof_mobile);
        textView_notlog = findViewById(R.id.textView_notlog);

        LinearLayout ll_adView = findViewById(R.id.ll_adView);
        methods.showBannerAd(ll_adView);

        collapsing.setTitle(Constants.itemUser.getName());

        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = findViewById(R.id.tabs_gifs);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));

        if (Constants.itemUser != null && !Constants.itemUser.getId().equals("")) {
            if (methods.isConnectingToInternet()) {
                loadUserProfile();
            } else {
                setEmpty(true, getString(R.string.err_internet_not_conn));
            }
        } else {
            setEmpty(true, getString(R.string.not_log));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_profile, menu);

        if (Constants.itemUser != null && !Constants.itemUser.getId().equals("")) {
            menu.findItem(R.id.item_profile_edit).setVisible(true);
        } else {
            menu.findItem(R.id.item_profile_edit).setVisible(false);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.item_profile_edit:
                if (Constants.itemUser != null && !Constants.itemUser.getId().equals("")) {
                    Intent intent = new Intent(ProfileActivity.this, ProfileEditActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(ProfileActivity.this, getResources().getString(R.string.not_log), Toast.LENGTH_SHORT).show();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadUserProfile() {
        LoadProfile loadProfile = new LoadProfile(new SuccessListener() {
            @Override
            public void onStart() {
                progressDialog.show();
            }

            @Override
            public void onEnd(String success, String registerSuccess, String message) {
                progressDialog.dismiss();
                if (success.equals("1")) {
                    switch (registerSuccess) {
                        case "1":
                            setVariables();
                            break;
                        case "-2":
                            methods.getInvalidUserDialog(message);
                            break;
                        case "-1":
                            methods.getVerifyDialog(getString(R.string.error_unauth_access), message);
                            break;
                        default:
                            setEmpty(false, message);
                            break;
                    }
                } else {
                    Toast.makeText(ProfileActivity.this, getString(R.string.err_server), Toast.LENGTH_SHORT).show();
                }
            }
        }, methods.getAPIRequest(Constants.METHOD_PROFILE, 0, "", "", "", "", "", "", "", "", "", "", "", "", "", Constants.itemUser.getId(), "", null));
        loadProfile.execute();
    }

    class ViewPagerAdapter extends FragmentStatePagerAdapter {

        ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return new FragmentUserQuotes().newInstance(position);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 1) {
                return getResources().getString(R.string.text);
            }
            return getResources().getString(R.string.image);
        }
    }

    public void setVariables() {
        textView_mobile.setText(Constants.itemUser.getMobile());
        textView_email.setText(Constants.itemUser.getEmail());

        textView_notlog.setVisibility(View.GONE);

        collapsing.setTitle(Constants.itemUser.getName());

        if(!Constants.itemUser.getImage().equals("")) {
            Picasso.get().load(Constants.itemUser.getImage()).placeholder(R.drawable.placeholder_prof).into(iv_prof);
        }
    }

    public void setEmpty(Boolean flag, String message) {
        if (flag) {
            textView_notlog.setText(message);
            textView_notlog.setVisibility(View.VISIBLE);
        } else {
            textView_notlog.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        if (Constants.isUpdate) {
            Constants.isUpdate = false;
            setVariables();
        }
        super.onResume();
    }
}
