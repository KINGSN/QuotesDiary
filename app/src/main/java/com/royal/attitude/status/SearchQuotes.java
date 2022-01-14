package com.royal.attitude.status;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.royal.attitude.status.adapter.AdapterImageQuotes;
import com.royal.attitude.status.adapter.AdapterTextQuotes;
import com.royal.attitude.status.asyncTask.LoadQuotes;
import com.royal.attitude.status.interfaces.QuotesListener;
import com.royal.attitude.status.item.ItemQuotes;
import com.royal.attitude.status.utils.Constants;
import com.royal.attitude.status.utils.EndlessRecyclerViewScrollListener;
import com.royal.attitude.status.utils.Methods;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.nativead.NativeAd;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import fr.castorflex.android.circularprogressbar.CircularProgressBar;
import okhttp3.RequestBody;

public class SearchQuotes extends AppCompatActivity {

    Toolbar toolbar;
    Methods methods;
    GridLayoutManager gridLayoutManager;
    RecyclerView rv_image, rv_text;
    AdapterTextQuotes adapterTextQuotes;
    AdapterImageQuotes adapterImageQuotes;
    String search;
    ArrayList<ItemQuotes> arrayList, arrayListTemp;
    Boolean isOver = false, isScroll = false;
    int position = 0, page = 1;
    LinearLayoutManager linearLayout;

    CircularProgressBar progressBar;
    LinearLayout ll_empty;
    TextView tv_empty;
    AppCompatButton button_empty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        search = getIntent().getStringExtra("search");
        position = getIntent().getIntExtra("pos", 0);

        toolbar = findViewById(R.id.tb_search);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(search);

        methods = new Methods(this);
        methods.forceRTLIfSupported(getWindow());
        methods.setStatusColor(getWindow());

        arrayList = new ArrayList<>();
        arrayListTemp = new ArrayList<>();

        progressBar = findViewById(R.id.pb_search);
        ll_empty = findViewById(R.id.ll_empty);
        tv_empty = findViewById(R.id.tv_empty);
        button_empty = findViewById(R.id.btn_empty_try);

        rv_image = findViewById(R.id.rv_search_image);
        rv_image.setHasFixedSize(true);

        rv_text = findViewById(R.id.rv_search_text);
        rv_text.setHasFixedSize(true);

        LinearLayout ll_adView = findViewById(R.id.ll_adView);
        methods.showBannerAd(ll_adView);

        gridLayoutManager = new GridLayoutManager(this, 2);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return (adapterImageQuotes.getItemViewType(position) >= 1000 || adapterImageQuotes.isHeader(position)) ? gridLayoutManager.getSpanCount() : 1;
            }
        });
        rv_image.setLayoutManager(gridLayoutManager);

        rv_image.addOnScrollListener(new EndlessRecyclerViewScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int p, int totalItemsCount) {
                if (!isOver) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isScroll = true;
                            loadQuotes();
                        }
                    }, 500);
                } else {
                    adapterImageQuotes.hideHeader();
                }
            }
        });

        linearLayout = new LinearLayoutManager(this);
        rv_text.setLayoutManager(linearLayout);

        rv_text.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayout) {
            @Override
            public void onLoadMore(int p, int totalItemsCount) {
                if (!isOver) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isScroll = true;
                            loadQuotes();
                        }
                    }, 500);
                } else {
                    adapterTextQuotes.hideHeader();
                }
            }
        });


        button_empty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadQuotes();
            }
        });

        loadQuotes();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);
        menu.findItem(R.id.menu_filter).setVisible(true);
        MenuItem item = menu.findItem(R.id.search);
        MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);

        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setOnQueryTextListener(queryTextListener);
        return super.onCreateOptionsMenu(menu);
    }

    SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String s) {
            search = s;
            isScroll = false;
            isOver = false;
            page = 1;
            loadQuotes();
            return false;
        }

        @Override
        public boolean onQueryTextChange(String s) {
            return true;
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.menu_filter:
                openFilterDialog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadQuotes() {
        if (methods.isConnectingToInternet()) {
            RequestBody requestBody;
            if (position == 0) {
                requestBody = methods.getAPIRequest(Constants.METHOD_SEARCH_IMAGE, page, "", search, "", "", "", "", "", "", "", "", "", "", "", Constants.itemUser.getId(), "", null);
            } else {
                requestBody = methods.getAPIRequest(Constants.METHOD_SEARCH_TEXT, page, "", search, "", "", "", "", "", "", "", "", "", "", "", Constants.itemUser.getId(), "", null);
            }

            LoadQuotes loadQuotes = new LoadQuotes(new QuotesListener() {
                @Override
                public void onStart() {
                    if (!isScroll) {
                        arrayList.clear();
                        arrayListTemp.clear();
                        rv_text.setVisibility(View.GONE);
                        rv_image.setVisibility(View.GONE);
                        ll_empty.setVisibility(View.GONE);
                        progressBar.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onEnd(String success, String verifyStatus, String message, ArrayList<ItemQuotes> arrayListQuotes, int total_records) {
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
                adapterImageQuotes = new AdapterImageQuotes(SearchQuotes.this, false, arrayList, arrayListTemp);
                rv_image.setAdapter(adapterImageQuotes);
            } else {
                adapterTextQuotes = new AdapterTextQuotes(SearchQuotes.this, false, arrayList, arrayListTemp);
                rv_text.setAdapter(adapterTextQuotes);
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
            AdLoader.Builder builder = new AdLoader.Builder(SearchQuotes.this, Constants.nativeAdID);
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
            if (position == 0) {
                rv_image.setVisibility(View.VISIBLE);
            } else {
                rv_text.setVisibility(View.VISIBLE);
            }
        } else {
            tv_empty.setText(errorMsg);
            ll_empty.setVisibility(View.VISIBLE);
            rv_image.setVisibility(View.GONE);
            rv_text.setVisibility(View.GONE);
        }
    }

    private void openFilterDialog() {
        final Dialog dialog_rate = new Dialog(SearchQuotes.this);
        dialog_rate.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_rate.setContentView(R.layout.layout_quote_chooser);

        ImageView iv_close = dialog_rate.findViewById(R.id.iv_rate_close);
        final RadioButton rb_image = dialog_rate.findViewById(R.id.rb_image);
        final RadioButton rb_text = dialog_rate.findViewById(R.id.rb_text);

        AppCompatButton button_select = dialog_rate.findViewById(R.id.button_select_type);

        if (position == 0) {
            rb_image.setChecked(true);
        } else if (position == 1) {
            rb_text.setChecked(true);
        }

        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_rate.dismiss();
            }
        });

        button_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rb_image.isChecked()) {
                    position = 0;
                } else if (rb_text.isChecked()) {
                    position = 1;
                }

                arrayList.clear();
                isScroll = false;
                isOver = false;
                page = 1;
                loadQuotes();
                dialog_rate.dismiss();
            }
        });

        dialog_rate.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog_rate.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog_rate.show();
        Window window = dialog_rate.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }
}