package com.royal.attitude.status;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.royal.attitude.status.asyncTask.LoadFav;
import com.royal.attitude.status.asyncTask.LoadLike;
import com.royal.attitude.status.asyncTask.LoadQuotes;
import com.royal.attitude.status.asyncTask.LoadReport;
import com.royal.attitude.status.asyncTask.LoadStatus;
import com.royal.attitude.status.eventbus.EventDelete;
import com.royal.attitude.status.eventbus.EventLiked;
import com.royal.attitude.status.eventbus.GlobalBus;
import com.royal.attitude.status.interfaces.InterAdListener;
import com.royal.attitude.status.interfaces.QuotesListener;
import com.royal.attitude.status.interfaces.SuccessListener;
import com.royal.attitude.status.item.ItemQuotes;
import com.royal.attitude.status.utils.Constants;
import com.royal.attitude.status.utils.Methods;
import com.royal.attitude.status.utils.MySingleton;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class QuoteDetailsText extends AppCompatActivity {

    Toolbar toolbar;
    Methods methods;
    int pos;
    MenuItem menuItem_fav;
    ArrayList<ItemQuotes> arrayList;
    CustomPagerAdapter customPagerAdapter;
    ViewPager viewPager;
    LinearLayout ll_adView;
    FloatingActionButton button_share, button_copy_text, button_likeDislike, button_report, button_delete;
    Boolean isSuccess = false, isUser = false;
    BottomSheetDialog dialog_report;
    ProgressDialog progressDialog;
    final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_details);

        toolbar = findViewById(R.id.toolbar_textDetails);
        toolbar.setTitle(getResources().getString(R.string.quotes));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        methods = new Methods(this, new InterAdListener() {
            @Override
            public void onClick(int position, String type) {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getResources().getString(R.string.share_via));
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, arrayList.get(position).getQuote() + "\n\n" + getResources().getString(R.string.app_name) + " - https://play.google.com/store/apps/details?id=" + getPackageName());
                startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.share_via)));
            }
        });
        methods.forceRTLIfSupported(getWindow());
        methods.setStatusColor(getWindow());

        pos = getIntent().getExtras().getInt("pos");
        isUser = getIntent().getExtras().getBoolean("user", false);
        arrayList = (ArrayList<ItemQuotes>) getIntent().getSerializableExtra("arr");

        progressDialog = new ProgressDialog(QuoteDetailsText.this);
        progressDialog.setMessage(getString(R.string.loading));

        ll_adView = findViewById(R.id.ll_adView);
        button_share = findViewById(R.id.button_share_text);
        button_copy_text = findViewById(R.id.button_copy_text);
        button_likeDislike = findViewById(R.id.button_likeDislike_text);
        button_report = findViewById(R.id.button_report_text);

        methods.showBannerAd(ll_adView);

        setLikeDislike(arrayList.get(pos).getLikes());
        loadViewed(pos);

        viewPager = findViewById(R.id.view_pager_text);
        customPagerAdapter = new CustomPagerAdapter(QuoteDetailsText.this, arrayList);

        viewPager.setAdapter(customPagerAdapter);
        viewPager.setCurrentItem(pos, true);
        viewPager.getAdapter().notifyDataSetChanged();
        viewPager.setOffscreenPageLimit(0);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                pos = position;
                isSuccess = false;
                loadViewed(position);
                changeFav(arrayList.get(position).getFav());
                setLikeDislike(arrayList.get(pos).getLikes());
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                Log.e("scroll", "PageScrollStateChanged");
            }
        });

        if (isUser) {
            button_delete = findViewById(R.id.button_delete);
            button_delete.setVisibility(View.VISIBLE);
            button_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDeleteDialog();
                }
            });
        }

        button_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                methods.showInter(pos, "");
            }
        });

        button_copy_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("quote", arrayList.get(pos).getQuote());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(QuoteDetailsText.this, arrayList.get(pos).getQuote(), Toast.LENGTH_SHORT).show();
            }
        });

        button_likeDislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (methods.isConnectingToInternet()) {
                    if (Constants.isLogged) {
                        loadLike(arrayList.get(pos).getId(), pos);
                    } else {
                        Intent intent = new Intent(QuoteDetailsText.this, LoginActivity.class);
                        intent.putExtra("from", "app");
                        startActivity(intent);
                    }
                } else {
                    Toast.makeText(QuoteDetailsText.this, getResources().getString(R.string.err_internet_not_conn), Toast.LENGTH_SHORT).show();
                }
            }
        });

        button_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showReportDialog();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_imagedetails, menu);
        menuItem_fav = menu.findItem(R.id.item_fav);
        changeFav(arrayList.get(pos).getFav());
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.item_fav:
                loadFav(arrayList.get(pos).getId(), pos, arrayList.get(pos).getFav());
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadFav(String qid, final int posi, Boolean isFav) {
        if (methods.isConnectingToInternet()) {
            LoadFav loadFav = new LoadFav(new SuccessListener() {
                @Override
                public void onStart() {

                }

                @Override
                public void onEnd(String success, String favSuccess, String message) {
                    if (success.equals("1")) {
                        if (favSuccess.equals("1")) {
                            arrayList.get(posi).setFav(true);
                        } else {
                            arrayList.get(posi).setFav(false);
                        }

                        if (viewPager.getCurrentItem() == posi) {
                            changeFav(arrayList.get(posi).getFav());
                        }

                        methods.showToast(message);
                    }
                }
            }, methods.getAPIRequest(Constants.METHOD_FAV_QUOTE, 0, qid, "", "", "", "text", "", "", "", "", "", "", "", "", Constants.itemUser.getId(), "", null));
            loadFav.execute();
        } else {
            methods.showToast(getString(R.string.err_internet_not_conn));
        }
    }

    private void changeFav(Boolean flag) {
        if (menuItem_fav != null) {
            if (flag) {
                menuItem_fav.setIcon(R.mipmap.ic_fav_hover);
            } else {
                menuItem_fav.setIcon(R.mipmap.ic_fav);
            }
        }
    }

    public void setLikeDislike(String number) {
        button_likeDislike.setLabelText("" + number);
        if (arrayList.get(pos).getLiked()) {
            button_likeDislike.setImageResource(R.mipmap.ic_like_hover);
        } else {
            button_likeDislike.setImageResource(R.mipmap.ic_like);
        }
    }

    private void loadViewed(final int pos) {
        if (methods.isConnectingToInternet()) {
            LoadQuotes loadQuotes = new LoadQuotes(new QuotesListener() {
                @Override
                public void onStart() {

                }

                @Override
                public void onEnd(String success, String verifyStatus, String message, ArrayList<ItemQuotes> arrayListQuotes, int total_records) {
                    if (success.equals("1")) {
                        if (!verifyStatus.equals("-1") && !verifyStatus.equals("-2")) {
                            arrayList.get(pos).setViews(String.valueOf(Integer.parseInt(arrayList.get(pos).getViews()) + 1));
                        } else if (verifyStatus.equals("-2")) {
                            methods.getInvalidUserDialog(message);
                        } else {
                            methods.getVerifyDialog(getString(R.string.error_unauth_access), message);
                        }
                    }
                }
            }, methods.getAPIRequest(Constants.METHOD_SINGLE_TEXT, 0, arrayList.get(pos).getId(), "", "", "", "", "", "", "", "", "", "", "", "", Constants.itemUser.getId(), "", null));
            loadQuotes.execute();
        }
    }

    private void loadLike(String qid, final int posi) {

        if (methods.isConnectingToInternet()) {
            String like = "0";
            if (!arrayList.get(pos).getLiked()) {
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
                        int postemp = getFullArrayPos(arrayList.get(posi));
                        if (registerSuccess.equals("1")) {
                            no = String.valueOf(Integer.parseInt(arrayList.get(posi).getLikes()) + 1);
                            arrayList.get(posi).setLiked(true);
                            MySingleton.getInstance().getQuotes().get(postemp).setLiked(true);
                        } else {
                            no = String.valueOf(Integer.parseInt(arrayList.get(posi).getLikes()) - 1);
                            arrayList.get(posi).setLiked(false);
                            MySingleton.getInstance().getQuotes().get(postemp).setLiked(false);
                        }

                        arrayList.get(posi).setLikes(no);
                        MySingleton.getInstance().getQuotes().get(postemp).setLikes(no);

                        MySingleton.getInstance().getQuotesTemp().clear();
                        MySingleton.getInstance().getQuotesTemp().addAll(arrayList);
                        GlobalBus.getBus().postSticky(new EventLiked(arrayList.get(posi).getLiked(), posi, no, arrayList.get(posi).getId(), "text"));

                        if (viewPager.getCurrentItem() == posi) {
                            setLikeDislike(no);
                        }

                        methods.showToast(message);
                    }
                }
            }, methods.getAPIRequest(Constants.METHOD_LIKE_TEXT, 0, qid, "", like, "", "", "", "", "", "", "", "", "", "", Constants.itemUser.getId(), "", null));
            loadLike.execute();
        } else {
            methods.showToast(getString(R.string.err_internet_not_conn));
        }
    }

    private int getFullArrayPos(ItemQuotes itemQuotes) {
        for (int i = 0; i < MySingleton.getInstance().getQuotes().size(); i++) {
            if (MySingleton.getInstance().getQuotes().get(i) != null && MySingleton.getInstance().getQuotes().get(i).getId().equals(itemQuotes.getId())) {
                return i;
            }
        }
        return 0;
    }

    class CustomPagerAdapter extends PagerAdapter {

        Context mContext;
        LayoutInflater mLayoutInflater;
        ArrayList<ItemQuotes> arrayList;

        CustomPagerAdapter(Context context, ArrayList<ItemQuotes> arrayList) {
            this.arrayList = arrayList;
            mContext = context;
            mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return arrayList.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, final int position) {
            View itemView = mLayoutInflater.inflate(R.layout.layout_viewpager_text, container, false);
            CardView cv_bg = itemView.findViewById(R.id.cv_quote_detail_text);
            TextView textView = itemView.findViewById(R.id.textView_quote);
            textView.setTag(position);

            if (!arrayList.get(position).getFont().trim().equals("")) {
                Typeface font;
                try {
                    font = Typeface.createFromAsset(getAssets(), "fonts/quotes/" + arrayList.get(position).getFont());
                } catch (Exception e) {
                    font = Typeface.createFromAsset(getAssets(), "fonts/quotes/Roboto.ttf");
                }

                textView.setTypeface(font);
            }

            if (!arrayList.get(position).getFontColor().trim().equals("")) {
                textView.setTextColor(Color.parseColor(arrayList.get(position).getFontColor()));
            }

            textView.setText(arrayList.get(position).getQuote());

            cv_bg.setCardBackgroundColor(Color.parseColor(arrayList.get(position).getBgColor()));

            container.addView(itemView, 0);
            return itemView;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }
    }

    private void showDeleteDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(QuoteDetailsText.this, R.style.AlertDialogTheme);
        alertDialog.setTitle(getString(R.string.delete));
        alertDialog.setMessage(getString(R.string.sure_delete));
        alertDialog.setPositiveButton(getString(R.string.delete), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                loadDelete(viewPager.getCurrentItem());
            }
        });
        alertDialog.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        alertDialog.show();
    }

    private void showReportDialog() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        View view = inflater.inflate(R.layout.layout_report, null);

        dialog_report = new BottomSheetDialog(QuoteDetailsText.this);
        dialog_report.setContentView(view);
        dialog_report.getWindow().findViewById(R.id.design_bottom_sheet).setBackgroundResource(android.R.color.transparent);
        dialog_report.show();

        final EditText editText_report;
        Button button_submit;

        button_submit = dialog_report.findViewById(R.id.button_report_submit);
        editText_report = dialog_report.findViewById(R.id.et_report);
        TextView tv_report = dialog_report.findViewById(R.id.tv_report);
        tv_report.setText(getString(R.string.report_quote_));

        button_submit.setBackground(methods.getRoundDrawable(getResources().getColor(R.color.colorPrimary)));

        button_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText_report.getText().toString().trim().isEmpty()) {
                    Toast.makeText(QuoteDetailsText.this, getString(R.string.enter_report), Toast.LENGTH_SHORT).show();
                } else {
                    if (Constants.isLogged) {
//                        loadReportSubmit(editText_report.getText().toString());
                        Toast.makeText(QuoteDetailsText.this, "Reporting is disabled in demo app", Toast.LENGTH_SHORT).show();
                    } else {
                        methods.clickLogin();
                    }
                }
            }
        });
    }

    public void loadDelete(int pos) {
        if (methods.isConnectingToInternet()) {
            LoadStatus loadStatus = new LoadStatus(new SuccessListener() {
                @Override
                public void onStart() {
                    progressDialog.show();
                }

                @Override
                public void onEnd(String success, String registerSuccess, String message) {
                    progressDialog.dismiss();
                    if (success.equals("1")) {
                        if (registerSuccess.equals("1")) {

                            int postemp = getFullArrayPos(arrayList.get(pos));
                            MySingleton.getInstance().getQuotes().remove(postemp);

                            ItemQuotes itemQuotes = arrayList.get(pos);
                            arrayList.remove(pos);
                            viewPager.setAdapter(customPagerAdapter);
                            if (pos >= arrayList.size()) {
                                viewPager.setCurrentItem(pos - 1);
                            } else {
                                viewPager.setCurrentItem(pos);
                            }

                            MySingleton.getInstance().getQuotesTemp().clear();
                            MySingleton.getInstance().getQuotesTemp().addAll(arrayList);
                            GlobalBus.getBus().postSticky(new EventDelete(itemQuotes, pos, "text"));
                        }
                        Toast.makeText(QuoteDetailsText.this, message, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(QuoteDetailsText.this, getString(R.string.err_server), Toast.LENGTH_SHORT).show();
                    }
                }
            }, methods.getAPIRequest(Constants.METHOD_USER_QUOTES_DELETE, 0, arrayList.get(pos).getId(), "", "", "", "text", "", "", "", "", "", "", "", "", Constants.itemUser.getId(), "", null));
            loadStatus.execute();
        } else {
            Toast.makeText(this, getString(R.string.err_internet_not_conn), Toast.LENGTH_SHORT).show();
        }
    }

    public void loadReportSubmit(String report) {
        if (methods.isConnectingToInternet()) {
            LoadReport loadReport = new LoadReport(new SuccessListener() {
                @Override
                public void onStart() {
                    progressDialog.show();
                }

                @Override
                public void onEnd(String success, String registerSuccess, String message) {
                    progressDialog.dismiss();
                    if (success.equals("1")) {
                        if (registerSuccess.equals("1")) {
                            dialog_report.dismiss();
                            Toast.makeText(QuoteDetailsText.this, message, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(QuoteDetailsText.this, getString(R.string.err_server), Toast.LENGTH_SHORT).show();
                    }
                }
            }, methods.getAPIRequest(Constants.METHOD_REPORT, 0, arrayList.get(pos).getId(), "", "", "", "text", "", "", "", "", "", "", "", "", Constants.itemUser.getId(), report, null));
            loadReport.execute();
        } else {
            Toast.makeText(this, getString(R.string.err_internet_not_conn), Toast.LENGTH_SHORT).show();
        }
    }
}