package com.royal.attitude.status;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
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

public class QuotesByCategory extends AppCompatActivity {

    Toolbar toolbar;
    Methods methods;
    GridLayoutManager lLayout;
    RecyclerView recyclerView;
    AdapterTextQuotes adapterTextQuotes;
    AdapterImageQuotes adapterImageQuotes;
    String cat_id, cat_name;
    ArrayList<ItemQuotes> arrayList, arrayListTemp;
    Boolean isOver = false, isScroll = false;
    int position, page = 1;
    LinearLayoutManager linearLayout;

    CircularProgressBar progressBar;
    LinearLayout ll_empty;
    TextView tv_empty;
    AppCompatButton button_empty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quotes_by_cat);

        position = getIntent().getExtras().getInt("pos");
        cat_id = getIntent().getExtras().getString("cid");
        cat_name = getIntent().getExtras().getString("cname");
        if (cat_name == null) {
            cat_name = "";
        }

        toolbar = findViewById(R.id.tb_quotes_by_cat);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(cat_name);

        methods = new Methods(this);
        methods.forceRTLIfSupported(getWindow());
        methods.setStatusColor(getWindow());

        arrayList = new ArrayList<>();
        arrayListTemp = new ArrayList<>();

        progressBar = findViewById(R.id.pb_quote_by_cat);
        ll_empty = findViewById(R.id.ll_empty);
        tv_empty = findViewById(R.id.tv_empty);
        button_empty = findViewById(R.id.btn_empty_try);

        recyclerView = findViewById(R.id.rv_quote_by_cat);
        recyclerView.setHasFixedSize(true);

        LinearLayout ll_adView = findViewById(R.id.ll_adView);
        methods.showBannerAd(ll_adView);

        if (position == 0) {
            lLayout = new GridLayoutManager(this, 2);
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
                                loadQuotesByCat();
                            }
                        }, 500);
                    } else {
                        adapterTextQuotes.hideHeader();
                    }
                }
            });
        } else {
            linearLayout = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(linearLayout);

            recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayout) {
                @Override
                public void onLoadMore(int p, int totalItemsCount) {
                    if (!isOver) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                isScroll = true;
                                loadQuotesByCat();
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
                loadQuotesByCat();
            }
        });

        loadQuotesByCat();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);
        menu.findItem(R.id.menu_filter).setVisible(false);
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
            Intent intent = new Intent(QuotesByCategory.this, SearchQuotes.class);
            intent.putExtra("search", s);
            intent.putExtra("pos", position);
            startActivity(intent);
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
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadNativeAds() {
        if (Constants.isNativeAd) {
            AdLoader.Builder builder = new AdLoader.Builder(QuotesByCategory.this, Constants.nativeAdID);
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

    private void loadQuotesByCat() {
        if (methods.isConnectingToInternet()) {
            RequestBody requestBody;
            if (position == 0) {
                requestBody = methods.getAPIRequest(Constants.METHOD_QUOTES_CAT_IMAGE, page, "", "", "", cat_id, "", "", "", "", "", "", "", "", "", Constants.itemUser.getId(), "", null);
            } else {
                requestBody = methods.getAPIRequest(Constants.METHOD_QUOTES_CAT_TEXT, page, "", "", "", cat_id, "", "", "", "", "", "", "", "", "", Constants.itemUser.getId(), "", null);
            }

            LoadQuotes loadQuotes = new LoadQuotes(new QuotesListener() {
                @Override
                public void onStart() {
                    if (arrayList.size() == 0) {
                        arrayList.clear();
                        recyclerView.setVisibility(View.GONE);
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
                adapterImageQuotes = new AdapterImageQuotes(QuotesByCategory.this, false, arrayList, arrayListTemp);

                recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
                recyclerView.setAdapter(adapterImageQuotes);
            } else {
                adapterTextQuotes = new AdapterTextQuotes(QuotesByCategory.this, false, arrayList, arrayListTemp);

                recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
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

    @Override
    public void onDestroy() {
        if (position == 0 && adapterImageQuotes != null) {
            adapterImageQuotes.destroyNativeAds();
        } else if (position == 1 && adapterTextQuotes != null) {
            adapterTextQuotes.destroyNativeAds();
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (Constants.isFromPush) {
            Constants.isFromPush = false;
            Constants.pushCID = "0";
            Intent intent = new Intent(QuotesByCategory.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            super.onBackPressed();
        }
    }
}