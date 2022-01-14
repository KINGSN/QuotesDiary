package com.royal.attitude.status.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.nativead.NativeAd;
import com.royal.attitude.status.R;
import com.royal.attitude.status.SearchQuotes;
import com.royal.attitude.status.adapter.AdapterImageQuotes;
import com.royal.attitude.status.adapter.AdapterTextQuotes;
import com.royal.attitude.status.asyncTask.LoadQuotes;
import com.royal.attitude.status.eventbus.EventLiked;
import com.royal.attitude.status.eventbus.GlobalBus;
import com.royal.attitude.status.interfaces.QuotesListener;
import com.royal.attitude.status.item.ItemQuotes;
import com.royal.attitude.status.utils.Constants;
import com.royal.attitude.status.utils.EndlessRecyclerViewScrollListener;
import com.royal.attitude.status.utils.Methods;
import com.royal.attitude.status.utils.MySingleton;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;
import okhttp3.RequestBody;

public class FragmentLatestQuotes extends Fragment {

    private GridLayoutManager lLayout;
    private LinearLayoutManager linearLayout;
    private RecyclerView recyclerView;
    private AdapterImageQuotes adapterImageQuotes;
    private AdapterTextQuotes adapterTextQuotes;
    private ArrayList<ItemQuotes> arrayList, arrayListTemp;
    private Boolean isOver = false, isScroll = false;
    private Methods methods;
    private int page = 1, position;

    private CircularProgressBar progressBar;
    private LinearLayout ll_empty;
    private TextView tv_empty;
    private AppCompatButton button_empty;

    public FragmentLatestQuotes newInstance(int pos) {
        FragmentLatestQuotes fragment = new FragmentLatestQuotes();
        Bundle args = new Bundle();
        args.putInt("someInt", pos);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_latest, container, false);

        methods = new Methods(getActivity());

        position = getArguments().getInt("someInt", 0);

        arrayList = new ArrayList<>();
        arrayListTemp = new ArrayList<>();

        progressBar = v.findViewById(R.id.pb_latest);
        ll_empty = v.findViewById(R.id.ll_empty);
        tv_empty = v.findViewById(R.id.tv_empty);
        button_empty = v.findViewById(R.id.btn_empty_try);

        recyclerView = v.findViewById(R.id.rv_latest);
        recyclerView.setHasFixedSize(true);

        if (position == 0) {
            lLayout = new GridLayoutManager(getActivity(), 2);
            lLayout.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return (adapterImageQuotes.getItemViewType(position) >= 1000 || adapterImageQuotes.isHeader(position)) ? lLayout.getSpanCount() : 1;
                }
            });
            recyclerView.setLayoutManager(lLayout);

            recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(lLayout) {
                @Override
                public void onLoadMore(int p, int totalItemsCount) {
                    if (!isOver) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                isScroll = true;
                                loadLatestQuotes();
                            }
                        }, 500);
                    } else {
                        adapterImageQuotes.hideHeader();
                    }
                }
            });
        } else {
            linearLayout = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(linearLayout);

            recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayout) {
                @Override
                public void onLoadMore(int p, int totalItemsCount) {
                    if (!isOver) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                isScroll = true;
                                loadLatestQuotes();
                            }
                        }, 500);
                    } else {
                        adapterTextQuotes.hideHeader();
                    }
                }
            });
        }

        button_empty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadLatestQuotes();
            }
        });

        loadLatestQuotes();

        setHasOptionsMenu(true);
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
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
            intent.putExtra("pos", position);
            startActivity(intent);
            return true;
        }

        @Override
        public boolean onQueryTextChange(String s) {
            return false;
        }
    };

    private void loadLatestQuotes() {
        if (methods.isConnectingToInternet()) {
            RequestBody requestBody;
            if (position == 0) {
                requestBody = methods.getAPIRequest(Constants.METHOD_QUOTES_LATEST_IMAGE, page, "", "", "", "", "", "", "", "", "", "", "", "", "", Constants.itemUser.getId(), "", null);
            } else {
                requestBody = methods.getAPIRequest(Constants.METHOD_QUOTES_LATEST_TEXT, page, "", "", "", "", "", "", "", "", "", "", "", "", "", Constants.itemUser.getId(), "", null);
            }

            LoadQuotes loadQuotes = new LoadQuotes(new QuotesListener() {
                @Override
                public void onStart() {
                    if (arrayList.size() == 0) {
                        recyclerView.setVisibility(View.GONE);
                        ll_empty.setVisibility(View.GONE);
                        progressBar.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onEnd(String success, String verifyStatus, String message, ArrayList<ItemQuotes> arrayListQuotes, int total_records) {
                    if (getActivity() != null) {
                        if (success.equals("1")) {
                            if (!verifyStatus.equals("-1") && !verifyStatus.equals("-2")) {
                                if (arrayListQuotes.size() == 0) {
                                    isOver = true;
                                    try {
                                        if (position == 0) {
                                            adapterImageQuotes.hideHeader();
                                        } else {
                                            adapterTextQuotes.hideHeader();
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    setEmpty(true, getString(R.string.err_no_quotes_found));
                                } else {
                                    arrayListTemp.addAll(arrayListQuotes);
                                    for (int i = 0; i < arrayListQuotes.size(); i++) {
                                        arrayList.add(arrayListQuotes.get(i));

                                        if (Constants.isNativeAd) {
                                            int abc = arrayList.lastIndexOf(null);
                                            if (((arrayList.size() - (abc + 1)) % Constants.nativeAdShow == 0) && (arrayListQuotes.size() - 1 != i || arrayListTemp.size() != total_records)) {
                                                arrayList.add(null);
                                            }
                                        }
                                    }

                                    page = page + 1;
                                    setAdapter();
                                }
                            } else if (verifyStatus.equals("-2")) {
                                methods.getInvalidUserDialog(message);
                            } else {
                                methods.getVerifyDialog(getString(R.string.error_unauth_access), message);
                            }
                        } else {
                            setEmpty(false, getString(R.string.err_server));
                        }
                        progressBar.setVisibility(View.GONE);
                    }
                }
            }, requestBody);
            loadQuotes.execute();
        } else {
            setEmpty(false, getString(R.string.err_internet_not_conn));
            progressBar.setVisibility(View.GONE);
        }
    }

    private void setAdapter() {
        if (!isScroll) {
            if (position == 0) {
                adapterImageQuotes = new AdapterImageQuotes(getActivity(), false, arrayList, arrayListTemp);
                recyclerView.setAdapter(adapterImageQuotes);
            } else {
                adapterTextQuotes = new AdapterTextQuotes(getActivity(), false, arrayList, arrayListTemp);
                recyclerView.setAdapter(adapterTextQuotes);
            }
            loadNativeAds();
        } else {
            if (position == 0) {
                adapterImageQuotes.notifyDataSetChanged();
            } else {
                adapterTextQuotes.notifyDataSetChanged();
            }
        }
        setEmpty(true, getString(R.string.err_no_quotes_found));
    }

    private void loadNativeAds() {
        if (Constants.isNativeAd) {
            AdLoader.Builder builder = new AdLoader.Builder(getActivity(), Constants.nativeAdID);
            AdLoader adLoader = builder.forNativeAd(
                    new NativeAd.OnNativeAdLoadedListener() {
                        @Override
                        public void onNativeAdLoaded(NativeAd nativeAd) {
                            try {
                                if (position == 0) {
                                    adapterImageQuotes.addAds(nativeAd);
                                } else {
                                    adapterTextQuotes.addAds(nativeAd);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).build();

            // Load the Native Express ad.
            adLoader.loadAds(new AdRequest.Builder().build(), 5);
        }
    }

    private void setEmpty(Boolean isSuccess, String errorMsg) {
        if (isSuccess && arrayList.size() > 0) {
            ll_empty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        } else {
            tv_empty.setText(errorMsg);
            ll_empty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }


    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onLikeChange(EventLiked eventLiked) {
        try {
            if (getActivity() != null) {
                if (eventLiked.getType().equals("text") && position == 1) {
                    if (arrayListTemp.get(eventLiked.getPosition()).getId().equals(eventLiked.getId())) {
                        arrayListTemp.clear();
                        arrayListTemp.addAll(MySingleton.getInstance().getQuotesTemp());
                        arrayList.clear();
                        arrayList.addAll(MySingleton.getInstance().getQuotes());
                        adapterTextQuotes.notifyDataSetChanged();
                    }
                    GlobalBus.getBus().removeStickyEvent(eventLiked);
                } else if (eventLiked.getType().equals("image") && position == 0) {
                    if (arrayListTemp.get(eventLiked.getPosition()).getId().equals(eventLiked.getId())) {
                        arrayListTemp.clear();
                        arrayListTemp.addAll(MySingleton.getInstance().getQuotesTemp());
                        arrayList.clear();
                        arrayList.addAll(MySingleton.getInstance().getQuotes());
                        adapterTextQuotes.notifyDataSetChanged();
                    }
                    GlobalBus.getBus().removeStickyEvent(eventLiked);
                }
            } else {
                GlobalBus.getBus().removeStickyEvent(eventLiked);
            }
        } catch (Exception e) {
            GlobalBus.getBus().removeStickyEvent(eventLiked);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        GlobalBus.getBus().register(this);
    }

    @Override
    public void onStop() {
        GlobalBus.getBus().unregister(this);
        super.onStop();
    }

    @Override
    public void onDestroy() {
        if (position == 0 && adapterImageQuotes != null) {
            adapterImageQuotes.destroyNativeAds();
        } else if (position == 1 && adapterTextQuotes != null) {
            adapterTextQuotes.destroyNativeAds();
        }
        super.onDestroy();
    }
}