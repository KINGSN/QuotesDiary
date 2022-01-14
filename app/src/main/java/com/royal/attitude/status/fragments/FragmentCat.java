package com.royal.attitude.status.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.royal.attitude.status.asyncTask.LoadCat;
import com.royal.attitude.status.interfaces.CategoryListener;
import com.royal.attitude.status.item.ItemCat;
import com.royal.attitude.status.R;
import com.royal.attitude.status.utils.Constants;
import com.royal.attitude.status.utils.Methods;

import java.util.ArrayList;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;

public class FragmentCat extends Fragment {

    private Methods methods;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    private TextView tv_image, tv_text;
    private ArrayList<ItemCat> arrayList_image_cat, arrayList_text_cat;
    private CircularProgressBar progressBar;
    private String errr_msg;

    private LinearLayout ll_empty;
    private TextView tv_empty;
    private AppCompatButton button_empty;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_category, container, false);

        methods = new Methods(getActivity());

        arrayList_image_cat = new ArrayList<>();
        arrayList_text_cat = new ArrayList<>();

        viewPager = rootView.findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(3);
        tv_image = rootView.findViewById(R.id.tv_tab_image);
        tv_text = rootView.findViewById(R.id.tv_tab_text);
        progressBar = rootView.findViewById(R.id.pb_cat);
        ll_empty = rootView.findViewById(R.id.ll_empty);
        tv_empty = rootView.findViewById(R.id.tv_empty);
        button_empty = rootView.findViewById(R.id.btn_empty_try);

        button_empty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadCategories();
            }
        });

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

        loadCategories();

        return rootView;
    }

    public class ViewPagerAdapter extends FragmentPagerAdapter {

        ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return FragmentCatByType.newInstance(position, arrayList_image_cat);
                case 1:
                    return FragmentCatByType.newInstance(position, arrayList_text_cat);
                default:
                    return FragmentCatByType.newInstance(position, arrayList_image_cat);
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

    private void loadCategories() {
        if (methods.isConnectingToInternet()) {

            LoadCat loadCat = new LoadCat(new CategoryListener() {
                @Override
                public void onStart() {
                    ll_empty.setVisibility(View.GONE);
                    viewPager.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                }

                @Override
                public void onEnd(String success, String verifyStatus, String message, ArrayList<ItemCat> arrayListImageCat, ArrayList<ItemCat> arrayListTextCat) {
                    if (getActivity() != null) {
                        if (success.equals("1")) {
                            if (!verifyStatus.equals("-1")) {
                                errr_msg = getString(R.string.err_no_cat_found);
                                arrayList_image_cat.addAll(arrayListImageCat);
                                arrayList_text_cat.addAll(arrayListTextCat);
                                progressBar.setVisibility(View.INVISIBLE);
                                viewPager.setAdapter(adapter);

                                setEmpty(true);
                            } else {
                                methods.getVerifyDialog(getString(R.string.error_unauth_access), message);
                            }
                        } else {
                            errr_msg = getString(R.string.err_server);
                            setEmpty(false);
                            methods.showToast(getString(R.string.err_server));
                        }
                        progressBar.setVisibility(View.GONE);
                    }
                }
            }, methods.getAPIRequest(Constants.METHOD_CAT, 0, "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", null));
            loadCat.execute();
        }
    }

    private void setTabsBG(int tabs) {
        if(getActivity() != null) {
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

    private void setEmpty(Boolean isSuccess) {

        if (isSuccess) {
            ll_empty.setVisibility(View.GONE);
            viewPager.setVisibility(View.VISIBLE);
        } else {
            tv_empty.setText(errr_msg);
            ll_empty.setVisibility(View.VISIBLE);
            viewPager.setVisibility(View.GONE);
        }
    }
}