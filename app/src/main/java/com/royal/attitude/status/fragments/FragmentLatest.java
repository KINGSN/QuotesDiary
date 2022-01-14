package com.royal.attitude.status.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.royal.attitude.status.R;
import com.royal.attitude.status.utils.Methods;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;

public class FragmentLatest extends Fragment {

    Methods methods;
    ViewPager viewPager;
    ViewPagerAdapter adapter;
    TextView tv_image, tv_text;
    CircularProgressBar progressBar;

    LinearLayout ll_empty;
    String from = "";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_category, container, false);

        methods = new Methods(getActivity());

        try {
            assert getArguments() != null;
            from = getArguments().getString("from");
        } catch (Exception e) {
            e.printStackTrace();
        }

        viewPager = rootView.findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(3);
        tv_image = rootView.findViewById(R.id.tv_tab_image);
        tv_text = rootView.findViewById(R.id.tv_tab_text);
        progressBar = rootView.findViewById(R.id.pb_cat);
        ll_empty = rootView.findViewById(R.id.ll_empty);

        ll_empty.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        viewPager.setVisibility(View.VISIBLE);

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

        adapter = new ViewPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(adapter);

        if(from!= null && from.equals("text")) {
            setTabsBG(1);
            viewPager.setCurrentItem(1);
        }

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

        return rootView;
    }

    public class ViewPagerAdapter extends FragmentPagerAdapter {

        ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return new FragmentLatestQuotes().newInstance(position);
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

    private void setTabsBG(int tabs) {
        if (tabs == 0) {
            tv_image.setTextColor(ContextCompat.getColor(getActivity(), R.color.gradient_color_1));
            tv_text.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));

            tv_image.setBackgroundResource(R.drawable.bg_tab_cat_selected);
            tv_text.setBackgroundResource(R.drawable.bg_tab_cat_unselected);
        } else {
            tv_text.setTextColor(ContextCompat.getColor(getActivity(), R.color.gradient_color_1));
            tv_image.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));

            tv_text.setBackgroundResource(R.drawable.bg_tab_cat_selected);
            tv_image.setBackgroundResource(R.drawable.bg_tab_cat_unselected);
        }
    }
}