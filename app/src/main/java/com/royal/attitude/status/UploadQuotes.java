package com.royal.attitude.status;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.royal.attitude.status.fragments.FragmentUploadsImage;
import com.royal.attitude.status.fragments.FragmentUploadsText;
import com.royal.attitude.status.utils.Methods;

public class UploadQuotes extends AppCompatActivity {

    Methods methods;
    Toolbar toolbar;
    ViewPager viewPager;
    ViewPagerAdapter adapter;
    TextView tv_image, tv_text;
    private String image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_quotes);

        toolbar = findViewById(R.id.toolbar_upload);
        toolbar.setTitle(getResources().getString(R.string.upload));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        methods = new Methods(this);
        methods.forceRTLIfSupported(getWindow());
        methods.setStatusColor(getWindow());

        viewPager = findViewById(R.id.viewpager_uploads);
        tv_image = findViewById(R.id.tv_tab_image);
        tv_text = findViewById(R.id.tv_tab_text);

        adapter = new ViewPagerAdapter(getSupportFragmentManager());

        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setTabsBG(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        tv_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTabsBG(0);
                viewPager.setCurrentItem(0);
            }
        });

        tv_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTabsBG(1);
                viewPager.setCurrentItem(1);
            }
        });

        try {
             image = getIntent().getStringExtra("image");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    class ViewPagerAdapter extends FragmentStatePagerAdapter {

        ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new FragmentUploadsImage().newInstance(image);
                case 1:
                    return new FragmentUploadsText().newInstance();
                default:
                    return new FragmentUploadsText().newInstance();
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getResources().getString(R.string.image);
                case 1:
                    return getResources().getString(R.string.text);
                default:
                    return getResources().getString(R.string.image);
            }
        }
    }

    private void setTabsBG(int tabs) {
        if (tabs == 0) {
            tv_image.setTextColor(ContextCompat.getColor(UploadQuotes.this, R.color.gradient_color_1));
            tv_text.setTextColor(ContextCompat.getColor(UploadQuotes.this, R.color.white));

            tv_image.setBackgroundResource(R.drawable.bg_tab_cat_selected);
            tv_text.setBackgroundResource(R.drawable.bg_tab_cat_unselected);
        } else {
            tv_text.setTextColor(ContextCompat.getColor(UploadQuotes.this, R.color.gradient_color_1));
            tv_image.setTextColor(ContextCompat.getColor(UploadQuotes.this, R.color.white));

            tv_text.setBackgroundResource(R.drawable.bg_tab_cat_selected);
            tv_image.setBackgroundResource(R.drawable.bg_tab_cat_unselected);
        }
    }
}