package com.royal.attitude.status.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.TooltipCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.royal.attitude.status.LoginActivity;
import com.royal.attitude.status.QuoteDetailsText;
import com.royal.attitude.status.R;
import com.royal.attitude.status.asyncTask.LoadLike;
import com.royal.attitude.status.interfaces.InterAdListener;
import com.royal.attitude.status.interfaces.SuccessListener;
import com.royal.attitude.status.item.ItemQuotes;
import com.royal.attitude.status.utils.Constants;
import com.royal.attitude.status.utils.Methods;
import com.royal.attitude.status.utils.MySingleton;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;

public class AdapterTextQuotes extends RecyclerView.Adapter {

    private Methods methods;
    private ArrayList<ItemQuotes> arrayList, arrayListTemp;
    public Context context;
    private Boolean isUserData;

    private final int VIEW_PROG = -1;

    private Boolean isAdLoaded = false;
    private List<NativeAd> mNativeAdsAdmob = new ArrayList<>();

    public AdapterTextQuotes(Context context, Boolean isUserData, ArrayList<ItemQuotes> arrayList, ArrayList<ItemQuotes> arrayListTemp) {
        this.arrayList = arrayList;
        this.arrayListTemp = arrayListTemp;
        this.context = context;
        this.isUserData = isUserData;
        methods = new Methods(context, interAdListener);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        LinearLayout ll_share, ll_like;
        RelativeLayout ll;
        TextView textView_quote, textView_likes, textView_views;
        ImageView iv_like, iv_views, iv_status;
        Typeface font;

        MyViewHolder(View view) {
            super(view);
            ll = view.findViewById(R.id.ll_text);
            ll_share = view.findViewById(R.id.ll_text_share);
            ll_like = view.findViewById(R.id.ll_text_like);
            iv_like = view.findViewById(R.id.iv_like_text);
            iv_views = view.findViewById(R.id.iv_views_text);
            iv_status = view.findViewById(R.id.iv_img_status);
            textView_quote = view.findViewById(R.id.tv_quote);
            textView_likes = view.findViewById(R.id.tv_like_text);
            textView_views = view.findViewById(R.id.tv_views_text);

            TooltipCompat.setTooltipText(iv_status, context.getString(R.string.approved));
        }
    }

    private static class ADViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout rl_native_ad;

        private ADViewHolder(View view) {
            super(view);
            rl_native_ad = view.findViewById(R.id.rl_native_ad);
        }
    }

    private static class ProgressViewHolder extends RecyclerView.ViewHolder {
        private static CircularProgressBar progressBar;

        private ProgressViewHolder(View v) {
            super(v);
            progressBar = v.findViewById(R.id.pb);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_PROG) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_progressbar, parent, false);
            return new ProgressViewHolder(v);
        } else if (viewType >= 1000) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_ads, parent, false);
            return new ADViewHolder(itemView);
        } else {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_quotes_text, parent, false);
            return new MyViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MyViewHolder) {
            ((MyViewHolder) holder).ll.setBackgroundColor(Color.parseColor(arrayList.get(position).getBgColor()));
            ((MyViewHolder) holder).iv_views.setColorFilter(R.color.black);

            if (isUserData && arrayList.get(position).getIsApproved()) {
                ((MyViewHolder) holder).iv_status.setVisibility(View.VISIBLE);
            } else {
                ((MyViewHolder) holder).iv_status.setVisibility(View.GONE);
            }


            if (!arrayList.get(position).getFontColor().trim().equals("")) {
                ((MyViewHolder) holder).textView_quote.setTextColor(Color.parseColor(arrayList.get(position).getFontColor()));
            }

            if (!arrayList.get(position).getFont().trim().equals("")) {
                try {
                    ((MyViewHolder) holder).font = Typeface.createFromAsset(context.getAssets(), "fonts/quotes/" + arrayList.get(position).getFont());
                } catch (Exception e) {
                    ((MyViewHolder) holder).font = Typeface.createFromAsset(context.getAssets(), "fonts/quotes/Roboto.ttf");
                }

                ((MyViewHolder) holder).textView_quote.setTypeface(((MyViewHolder) holder).font);
            }

            ((MyViewHolder) holder).textView_likes.setText(methods.format(Double.parseDouble(arrayList.get(position).getLikes())));
            ((MyViewHolder) holder).textView_views.setText(methods.format(Double.parseDouble(arrayList.get(position).getViews())));
            ((MyViewHolder) holder).textView_quote.setText(arrayList.get(position).getQuote());

            if (arrayList.get(holder.getAdapterPosition()).getLiked()) {
                ((MyViewHolder) holder).iv_like.setImageResource(R.mipmap.ic_like_grey_hover);
            } else {
                ((MyViewHolder) holder).iv_like.setImageResource(R.mipmap.ic_like_grey);
            }

            ((MyViewHolder) holder).ll.setOnClickListener(v -> methods.showInter(holder.getAdapterPosition(), "list"));

            ((MyViewHolder) holder).ll_share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    methods.showInter(holder.getAdapterPosition(), "share");
                }
            });

            ((MyViewHolder) holder).ll_like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Constants.isLogged) {
                        loadLike(arrayList.get(holder.getAdapterPosition()).getId(), holder.getAdapterPosition());
                    } else {
                        Intent intent = new Intent(context, LoginActivity.class);
                        intent.putExtra("from", "app");
                        context.startActivity(intent);
                    }
                }
            });
        } else if (holder instanceof ADViewHolder) {
            if (isAdLoaded) {
                if (((ADViewHolder) holder).rl_native_ad.getChildCount() == 0) {
                    if (mNativeAdsAdmob.size() >= 5) {

                        int i = new Random().nextInt(mNativeAdsAdmob.size() - 1);

                        NativeAdView adView = (NativeAdView) ((Activity) context).getLayoutInflater().inflate(R.layout.layout_native_ad_admob, null);
                        populateUnifiedNativeAdView(mNativeAdsAdmob.get(i), adView);
                        ((ADViewHolder) holder).rl_native_ad.removeAllViews();
                        ((ADViewHolder) holder).rl_native_ad.addView(adView);

                        ((ADViewHolder) holder).rl_native_ad.setVisibility(View.VISIBLE);
                    }
                }
            }
        } else {
            if (getItemCount() == 1) {
                ProgressViewHolder.progressBar.setVisibility(View.GONE);
            }
        }
    }

    private void loadLike(String qid, final int pos) {
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
                        if (registerSuccess.equals("1")) {
                            no = String.valueOf(Integer.parseInt(arrayList.get(pos).getLikes()) + 1);
                            arrayList.get(pos).setLiked(true);
                        } else {
                            no = String.valueOf(Integer.parseInt(arrayList.get(pos).getLikes()) - 1);
                            arrayList.get(pos).setLiked(false);
                        }

                        arrayList.get(pos).setLikes(no);
//                        dbHelper.updateViews(arrayList.get(pos));
                        notifyItemChanged(pos);

                        methods.showToast(message);
                    }
                }
            }, methods.getAPIRequest(Constants.METHOD_LIKE_TEXT, 0, qid, "", like, "", "", "", "", "", "", "", "", "", "", Constants.itemUser.getId(), "", null));
            loadLike.execute();
        } else {
            methods.showToast(context.getString(R.string.err_internet_not_conn));
        }
    }

    @Override
    public long getItemId(int id) {
        return id;
    }

    @Override
    public int getItemCount() {
        return arrayList.size() + 1;
    }

    public void hideHeader() {
        ProgressViewHolder.progressBar.setVisibility(View.GONE);
    }

    private boolean isHeader(int position) {
        return position == arrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (isHeader(position)) {
            return VIEW_PROG;
        } else if (arrayList.get(position) == null) {
            return 1000 + position;
        } else {
            return position;
        }
    }

    public void addAds(NativeAd unifiedNativeAd) {
        mNativeAdsAdmob.add(unifiedNativeAd);
        isAdLoaded = true;
    }

    private void populateUnifiedNativeAdView(NativeAd nativeAd, NativeAdView adView) {
        // Set the media view. Media content will be automatically populated in the media view once
        // adView.setNativeAd() is called.
        MediaView mediaView = adView.findViewById(R.id.ad_media);
        adView.setMediaView(mediaView);

        // Set other ad assets.
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_icon));
        adView.setPriceView(adView.findViewById(R.id.ad_price));
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
        adView.setStoreView(adView.findViewById(R.id.ad_store));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));

        // The headline is guaranteed to be in every UnifiedNativeAd.
        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.getBody() == null) {
            adView.getBodyView().setVisibility(View.INVISIBLE);
        } else {
            adView.getBodyView().setVisibility(View.VISIBLE);
            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        }

        if (nativeAd.getCallToAction() == null) {
            adView.getCallToActionView().setVisibility(View.INVISIBLE);
        } else {
            adView.getCallToActionView().setVisibility(View.VISIBLE);
            ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }

        if (nativeAd.getIcon() == null) {
            adView.getIconView().setVisibility(View.GONE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(
                    nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getPrice() == null) {
            adView.getPriceView().setVisibility(View.INVISIBLE);
        } else {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
        }

        if (nativeAd.getStore() == null) {
            adView.getStoreView().setVisibility(View.INVISIBLE);
        } else {
            adView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
        }

        if (nativeAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView())
                    .setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getAdvertiser() == null) {
            adView.getAdvertiserView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }

        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad. The SDK will populate the adView's MediaView
        // with the media content from this native ad.
        adView.setNativeAd(nativeAd);
    }

    public void destroyNativeAds() {
        try {
            for (int i = 0; i < mNativeAdsAdmob.size(); i++) {
                mNativeAdsAdmob.get(i).destroy();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getRealPos(int pos) {
//        return arrayListTemp.indexOf(arrayList.get(pos));

        for (int i = 0; i < arrayListTemp.size(); i++) {
            if (arrayListTemp.get(i).getId().equals(arrayList.get(pos).getId())) {
                return i;
            }
        }
        return 0;
    }

    InterAdListener interAdListener = new InterAdListener() {
        @Override
        public void onClick(int position, String type) {
            int real_pos = getRealPos(position);

            switch (type) {
                case "share":
                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, context.getResources().getString(R.string.share_via));
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, arrayListTemp.get(real_pos).getQuote() + "\n\n" + context.getResources().getString(R.string.app_name) + " - https://play.google.com/store/apps/details?id=" + context.getPackageName());
                    context.startActivity(Intent.createChooser(sharingIntent, context.getResources().getString(R.string.share_via)));
                    break;
                case "list":
                    MySingleton.getInstance().getQuotes().clear();
                    MySingleton.getInstance().getQuotes().addAll(arrayList);

                    Intent intent = new Intent(context, QuoteDetailsText.class);
                    intent.putExtra("pos", real_pos);
                    intent.putExtra("arr", arrayListTemp);
                    intent.putExtra("user", isUserData);
                    context.startActivity(intent);
                    break;
            }
        }
    };
}