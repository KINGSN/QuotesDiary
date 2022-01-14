package com.royal.attitude.status.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.royal.attitude.status.LoginActivity;
import com.royal.attitude.status.utils.MySingleton;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.royal.attitude.status.adapter.AdapterQuotesHome;
import com.royal.attitude.status.adapter.AdapterTextQuotesHome;
import com.royal.attitude.status.asyncTask.LoadHome;
import com.royal.attitude.status.asyncTask.LoadLike;
import com.royal.attitude.status.interfaces.HomeListener;
import com.royal.attitude.status.interfaces.InterAdListener;
import com.royal.attitude.status.interfaces.SuccessListener;
import com.royal.attitude.status.item.ItemQuotes;
import com.royal.attitude.status.MainActivity;
import com.royal.attitude.status.QuoteDetailsImage;
import com.royal.attitude.status.R;
import com.royal.attitude.status.SearchQuotes;
import com.royal.attitude.status.utils.Constants;
import com.royal.attitude.status.utils.Methods;
import com.royal.attitude.status.utils.RecyclerItemClickListener;

import java.util.ArrayList;
import java.util.Collections;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;

public class FragmentHome extends Fragment {

    private Methods methods;
    private FragmentManager fm;
    private ImageView iv_quote_of_day, iv_qotd_share, iv_qotd_like;
    private ViewPager viewPager;
    private ImagePagerAdapter imagePagerAdapter;
    private LinearLayout ll_home, ll_latest, ll_pop, ll_top, ll_text, ll_featured;
    private RecyclerView rv_latest, rv_popular, rv_top, rv_text;
    private AdapterQuotesHome adapterLatest, adapterPopular, adapterTop;
    private AdapterTextQuotesHome adapterText;
    private TextView tv_no_latest, tv_no_pop, tv_no_top, tv_no_text;
    private ArrayList<ItemQuotes> arrayList_latest, arrayList_popular, arrayList_featured, arrayList_top, arrayList_text, arrayList_quoteOfDay;
    private AppCompatButton button_latest, button_pop, button_top, button_text;
    private LinearLayout ll_adView;
    private CardView cv_qotd;

    private CircularProgressBar progressBar;
    private FrameLayout frameLayout;
    private String errr_msg;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        methods = new Methods(getActivity(), new InterAdListener() {
            @Override
            public void onClick(int position, String type) {

                if (type.equals(getString(R.string.latest))) {
                    MySingleton.getInstance().getQuotes().clear();
                    MySingleton.getInstance().getQuotes().addAll(arrayList_latest);

                    Intent intent = new Intent(getActivity(), QuoteDetailsImage.class);
                    intent.putExtra("pos", position);
                    intent.putExtra("arr", arrayList_latest);
                    startActivity(intent);
                } else if (type.equals(getString(R.string.popular))) {
                    MySingleton.getInstance().getQuotes().clear();
                    MySingleton.getInstance().getQuotes().addAll(arrayList_popular);

                    Intent intent = new Intent(getActivity(), QuoteDetailsImage.class);
                    intent.putExtra("pos", position);
                    intent.putExtra("arr", arrayList_popular);
                    startActivity(intent);
                } else if (type.equals(getString(R.string.topliked))) {
                    MySingleton.getInstance().getQuotes().clear();
                    MySingleton.getInstance().getQuotes().addAll(arrayList_top);

                    Intent intent = new Intent(getActivity(), QuoteDetailsImage.class);
                    intent.putExtra("pos", position);
                    intent.putExtra("arr", arrayList_top);
                    startActivity(intent);
                } else if (type.equals(getString(R.string.share))) {
                    methods.optionImageQuotes("share", arrayList_featured.get(position).getImageBig(), arrayList_featured.get(position).getId());
                } else if (type.equals(getString(R.string.featured))) {
                    MySingleton.getInstance().getQuotes().clear();
                    MySingleton.getInstance().getQuotes().addAll(arrayList_featured);

                    Intent intent = new Intent(getActivity(), QuoteDetailsImage.class);
                    intent.putExtra("pos", position);
                    intent.putExtra("arr", arrayList_featured);
                    startActivity(intent);
                } else if (type.equals(getString(R.string.quote_of_day))) {
                    methods.optionImageQuotes("share", arrayList_quoteOfDay.get(0).getImageBig(), arrayList_quoteOfDay.get(0).getId());
                } else if (type.equals(getString(R.string.liked))) {
                    loadLikeQOTD(arrayList_quoteOfDay.get(0).getId(), 0, arrayList_quoteOfDay.get(0).getLiked());
                }
            }
        });
        fm = getFragmentManager();

        arrayList_latest = new ArrayList<>();
        arrayList_popular = new ArrayList<>();
        arrayList_featured = new ArrayList<>();
        arrayList_top = new ArrayList<>();
        arrayList_text = new ArrayList<>();
        arrayList_quoteOfDay = new ArrayList<>();

        viewPager = v.findViewById(R.id.vp_home);
        viewPager.setClipToPadding(false);
        viewPager.setPadding(100, 0, 100, 0);
        viewPager.setPageMargin(30);

        ll_adView = v.findViewById(R.id.ll_adView);
        methods.showBannerAd(ll_adView);

        cv_qotd = v.findViewById(R.id.cv_qotd);
        ll_home = v.findViewById(R.id.ll_home);
        ll_latest = v.findViewById(R.id.ll_home_latest);
        ll_featured = v.findViewById(R.id.ll_home_featured);
        ll_pop = v.findViewById(R.id.ll_home_pop);
        ll_text = v.findViewById(R.id.ll_home_text);
        ll_top = v.findViewById(R.id.ll_home_top);
        frameLayout = v.findViewById(R.id.fl_empty);
        progressBar = v.findViewById(R.id.pb_home);
        tv_no_latest = v.findViewById(R.id.tv_no_latest);
        tv_no_pop = v.findViewById(R.id.tv_no_pop);
        tv_no_top = v.findViewById(R.id.tv_no_top);
        tv_no_text = v.findViewById(R.id.tv_no_text);
        button_latest = v.findViewById(R.id.button_all_latest);
        button_pop = v.findViewById(R.id.button_all_popular);
        button_top = v.findViewById(R.id.button_all_top);
        button_text = v.findViewById(R.id.button_all_text);
        iv_quote_of_day = v.findViewById(R.id.iv_home_quote_of_day);
        iv_qotd_share = v.findViewById(R.id.iv_home_qotd_share);
        iv_qotd_like = v.findViewById(R.id.iv_home_qotd_like);

        rv_latest = v.findViewById(R.id.rv_home_latest);
        rv_latest.setNestedScrollingEnabled(false);
        LinearLayoutManager llm_latest = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        rv_latest.setLayoutManager(llm_latest);

        rv_popular = v.findViewById(R.id.rv_home_popular);
        rv_popular.setNestedScrollingEnabled(false);
        LinearLayoutManager llm_popular = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        rv_popular.setLayoutManager(llm_popular);

        rv_top = v.findViewById(R.id.rv_home_top);
        rv_top.setNestedScrollingEnabled(false);
        LinearLayoutManager llm_top = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        rv_top.setLayoutManager(llm_top);

        rv_text = v.findViewById(R.id.rv_home_text);
        rv_text.setNestedScrollingEnabled(false);
        LinearLayoutManager llm_text = new LinearLayoutManager(getActivity());
        rv_text.setLayoutManager(llm_text);

        LayoutInflater inflat = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View myView = inflat.inflate(R.layout.layout_err_internet, null);
        frameLayout.addView(myView);
        myView.findViewById(R.id.btn_empty_try).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getHomeData();
            }
        });

        rv_latest.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                methods.showInter(position, getString(R.string.latest));
            }
        }));

        rv_popular.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                methods.showInter(position, getString(R.string.popular));
            }
        }));

        rv_top.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                methods.showInter(position, getString(R.string.topliked));
            }
        }));

        button_latest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentLatest f1 = new FragmentLatest();
                FragmentTransaction ft = fm.beginTransaction();
//                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

                ft.hide(fm.getFragments().get(fm.getBackStackEntryCount()));
                ft.add(R.id.frame_layout, f1, getString(R.string.latest));
                ft.addToBackStack(getString(R.string.latest));
                ft.commit();
                ((MainActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.latest));
            }
        });

        button_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentLatest f1 = new FragmentLatest();
                Bundle bundle = new Bundle();
                bundle.putString("from", "text");
                f1.setArguments(bundle);
                FragmentTransaction ft = fm.beginTransaction();
//                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

                ft.hide(fm.getFragments().get(fm.getBackStackEntryCount()));
                ft.add(R.id.frame_layout, f1, getString(R.string.latest));
                ft.addToBackStack(getString(R.string.latest));
                ft.commit();
                ((MainActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.latest));
            }
        });

        button_top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTopLiked f1 = new FragmentTopLiked();
                FragmentTransaction ft = fm.beginTransaction();
//                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

                ft.hide(fm.getFragments().get(fm.getBackStackEntryCount()));
                ft.add(R.id.frame_layout, f1, getString(R.string.topliked));
                ft.addToBackStack(getString(R.string.topliked));
                ft.commit();
                ((MainActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.topliked));
            }
        });

        button_pop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentPopular f1 = new FragmentPopular();
                FragmentTransaction ft = fm.beginTransaction();
//                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

                ft.hide(fm.getFragments().get(fm.getBackStackEntryCount()));
                ft.add(R.id.frame_layout, f1, getString(R.string.topliked));
                ft.addToBackStack(getString(R.string.topliked));
                ft.commit();
                ((MainActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.popular));
            }
        });

        iv_quote_of_day.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MySingleton.getInstance().getQuotes().clear();
                MySingleton.getInstance().getQuotes().addAll(arrayList_quoteOfDay);

                Intent intent = new Intent(getActivity(), QuoteDetailsImage.class);
                intent.putExtra("pos", 0);
                intent.putExtra("arr", arrayList_quoteOfDay);
                startActivity(intent);
            }
        });

        iv_qotd_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                methods.showInter(0, getString(R.string.liked));
            }
        });

        iv_qotd_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                methods.showInter(0, getString(R.string.quote_of_day));
            }
        });

        getHomeData();

        setHasOptionsMenu(true);
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search, menu);
        MenuItem item = menu.findItem(R.id.search);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItem.SHOW_AS_ACTION_IF_ROOM);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setOnQueryTextListener(queryTextListener);
        super.onCreateOptionsMenu(menu, inflater);
    }

    SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String s) {
            Intent intent = new Intent(getActivity(), SearchQuotes.class);
            intent.putExtra("search", s);
            intent.putExtra("pos", 0);
            startActivity(intent);
            return true;
        }

        @Override
        public boolean onQueryTextChange(String s) {
            return false;
        }
    };

    private void loadLikeQOTD(String qid, final int posi, Boolean isLiked) {
        if (methods.isConnectingToInternet()) {
            String like = "0";
            if (!isLiked) {
                like = "1";
            }

            LoadLike loadLike = new LoadLike(new SuccessListener() {
                @Override
                public void onStart() {

                }

                @Override
                public void onEnd(String success, String registerSuccess, String message) {
                    if (success.equals("1")) {
                        String no = "";
                        if (registerSuccess.equals("1")) {
                            no = String.valueOf(Integer.parseInt(arrayList_quoteOfDay.get(posi).getLikes()) + 1);
                            arrayList_quoteOfDay.get(posi).setLiked(true);
                            iv_qotd_like.setImageResource(R.drawable.ic_like_hover);
                        } else {
                            no = String.valueOf(Integer.parseInt(arrayList_quoteOfDay.get(posi).getLikes()) - 1);
                            arrayList_quoteOfDay.get(posi).setLiked(false);
                            iv_qotd_like.setImageResource(R.drawable.ic_like);
                        }

                        arrayList_quoteOfDay.get(posi).setLikes(no);

                        methods.showToast(message);
                    }
                }
            }, methods.getAPIRequest(Constants.METHOD_LIKE_IMAGE, 0, qid, "", like, "", "", "", "", "", "", "", "", "", "", Constants.itemUser.getId(), "", null));
            loadLike.execute();
        } else {
            methods.showToast(getString(R.string.err_internet_not_conn));
        }
    }

    private void getHomeData() {
        if (methods.isConnectingToInternet()) {

            LoadHome loadHome = new LoadHome(new HomeListener() {
                @Override
                public void onStart() {
                    frameLayout.setVisibility(View.GONE);
                    ll_home.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                }

                @Override
                public void onEnd(String success, String message, ArrayList<ItemQuotes> arrayListFeatured, ArrayList<ItemQuotes> arrayListLatest, ArrayList<ItemQuotes> arrayListPopular, ArrayList<ItemQuotes> arrayListTop, ArrayList<ItemQuotes> arrayListQuoteOfDay, ArrayList<ItemQuotes> arrayListText) {
                    if (getActivity() != null) {
                        if (success.equals("1")) {
                            try {
                                arrayList_featured.addAll(arrayListFeatured);
                                arrayList_latest.addAll(arrayListLatest);
                                arrayList_popular.addAll(arrayListPopular);
                                arrayList_top.addAll(arrayListTop);
                                arrayList_text.addAll(arrayListText);
                                arrayList_quoteOfDay.addAll(arrayListQuoteOfDay);

                                Collections.shuffle(arrayList_latest);

                                tv_no_latest.setText(arrayList_latest.size() + " " + getString(R.string.quotes));
                                tv_no_pop.setText(arrayList_popular.size() + " " + getString(R.string.quotes));
                                tv_no_top.setText(arrayList_top.size() + " " + getString(R.string.quotes));
                                tv_no_text.setText(arrayList_text.size() + " " + getString(R.string.quotes));

                                adapterLatest = new AdapterQuotesHome(getActivity(), arrayList_latest);
                                rv_latest.setAdapter(adapterLatest);

                                adapterPopular = new AdapterQuotesHome(getActivity(), arrayList_popular);
                                rv_popular.setAdapter(adapterPopular);

                                adapterTop = new AdapterQuotesHome(getActivity(), arrayList_top);
                                rv_top.setAdapter(adapterTop);

                                adapterText = new AdapterTextQuotesHome(getActivity(), arrayList_text);
                                rv_text.setAdapter(adapterText);

                                if(arrayList_quoteOfDay.size() > 0) {
                                    Picasso.get().load(arrayList_quoteOfDay.get(0).getImageBig()).into(iv_quote_of_day);
                                    if (arrayList_quoteOfDay.get(0).getLiked()) {
                                        iv_qotd_like.setImageResource(R.drawable.ic_like_hover);
                                    } else {
                                        iv_qotd_like.setImageResource(R.drawable.ic_like);
                                    }
                                } else {
                                    cv_qotd.setVisibility(View.GONE);
                                }

                                imagePagerAdapter = new ImagePagerAdapter();
                                viewPager.setAdapter(imagePagerAdapter);
                                if (imagePagerAdapter.getCount() > 2) {
                                    viewPager.setCurrentItem(1);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else if(success.equals("-2")) {
                            methods.getInvalidUserDialog(message);
                        }

                        loadEmpty();

                        ll_home.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                        errr_msg = getString(R.string.err_server);
                    }
                }
            }, methods.getAPIRequest(Constants.METHOD_HOME, 0, "", "", "", "", "", "", "", "", "", "", "", "", "", Constants.itemUser.getId(), "", null));
            loadHome.execute();
        } else {
            errr_msg = getString(R.string.err_internet_not_conn);
            frameLayout.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }
    }

    private void loadEmpty() {
        if (arrayList_latest.size() == 0) {
            ll_latest.setVisibility(View.GONE);
        } else {
            ll_latest.setVisibility(View.VISIBLE);
        }

        if (arrayList_popular.size() == 0) {
            ll_pop.setVisibility(View.GONE);
        } else {
            ll_pop.setVisibility(View.VISIBLE);
        }

        if (arrayList_top.size() == 0) {
            ll_top.setVisibility(View.GONE);
        } else {
            ll_top.setVisibility(View.VISIBLE);
        }

        if (arrayList_text.size() == 0) {
            ll_text.setVisibility(View.GONE);
        } else {
            ll_text.setVisibility(View.VISIBLE);
        }

        if (arrayList_featured.size() == 0) {
            ll_featured.setVisibility(View.GONE);
        } else {
            ll_featured.setVisibility(View.VISIBLE);
        }
    }

    private class ImagePagerAdapter extends PagerAdapter {

        private LayoutInflater inflater;

        private ImagePagerAdapter() {
            inflater = getLayoutInflater();
        }

        @Override
        public int getCount() {
            return arrayList_featured.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view.equals(object);
        }

        @Override
        public int getItemPosition(@NonNull final Object object) {
            return POSITION_NONE;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, final int position) {

            View imageLayout = inflater.inflate(R.layout.layout_viewpager_home, container, false);
            RoundedImageView imageView = imageLayout.findViewById(R.id.iv_vp_home);
            LinearLayout ll_like = imageLayout.findViewById(R.id.ll_vp_home_like);
            LinearLayout ll_share = imageLayout.findViewById(R.id.ll_vp_home_share);
            final ImageView iv_like = imageLayout.findViewById(R.id.iv_vp_home_like);
            TextView tv_likes = imageLayout.findViewById(R.id.tv_home_vp_like);

            tv_likes.setText(methods.format(Double.parseDouble(arrayList_featured.get(position).getLikes())));

            Picasso.get()
                    .load(arrayList_featured.get(position).getImageBig())
                    .placeholder(R.drawable.placeholder)
                    .into(imageView);

            if (arrayList_featured.get(position).getLiked()) {
                iv_like.setImageResource(R.mipmap.ic_like_grey_hover);
            } else {
                iv_like.setImageResource(R.mipmap.ic_like_grey);
            }

            ll_share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    methods.showInter(position, getString(R.string.share));
                }
            });

            ll_like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(Constants.isLogged) {
                        loadLike(arrayList_featured.get(position).getId(), position, arrayList_featured.get(position).getLiked());
                    } else {
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        intent.putExtra("from", "app");
                        startActivity(intent);
                    }
                }
            });

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    methods.showInter(position, getString(R.string.featured));
                }
            });

            container.addView(imageLayout);
            return imageLayout;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }
    }

    private void loadLike(String qid, final int posi, Boolean isLiked) {
        if(methods.isConnectingToInternet()) {
            String like = "0";
            if (!isLiked) {
                like = "1";
            }

            LoadLike loadLike = new LoadLike(new SuccessListener() {
                @Override
                public void onStart() {

                }

                @Override
                public void onEnd(String success, String registerSuccess, String message) {
                    if (getActivity() != null) {
                        if (success.equals("1")) {
                            String no = "";
                            if (registerSuccess.equals("1")) {
                                no = String.valueOf(Integer.parseInt(arrayList_featured.get(posi).getLikes()) + 1);
                                arrayList_featured.get(posi).setLiked(true);
                            } else {
                                no = String.valueOf(Integer.parseInt(arrayList_featured.get(posi).getLikes()) - 1);
                                arrayList_featured.get(posi).setLiked(false);
                            }

                            arrayList_featured.get(posi).setLikes(no);
                            imagePagerAdapter.notifyDataSetChanged();

                            methods.showToast(message);
                        }
                    }
                }
            }, methods.getAPIRequest(Constants.METHOD_LIKE_IMAGE, 0, qid, "", like, "", "", "", "", "", "", "", "", "", "", Constants.itemUser.getId(), "", null));
            loadLike.execute();
        } else {
            methods.showToast(getString(R.string.err_internet_not_conn));
        }
    }
}