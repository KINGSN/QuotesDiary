package com.royal.attitude.status.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.royal.attitude.status.interfaces.InterAdListener;
import com.royal.attitude.status.item.ItemQuotes;
import com.royal.attitude.status.QuoteDetailsImage;
import com.royal.attitude.status.R;
import com.royal.attitude.status.utils.Methods;
import com.royal.attitude.status.utils.MySingleton;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.TooltipCompat;
import androidx.recyclerview.widget.RecyclerView;
import fr.castorflex.android.circularprogressbar.CircularProgressBar;


public class AdapterImageQuotes extends RecyclerView.Adapter {

    private Methods methods;
    private ArrayList<ItemQuotes> arrayList;
    private ArrayList<ItemQuotes> arrayListTemp;
    public Context context;
    private Boolean isUserData = false;

    private final int VIEW_PROG = -1;

    private Boolean isAdLoaded = false;
    private List<NativeAd> mNativeAdsAdmob = new ArrayList<>();

    public AdapterImageQuotes(Context context, Boolean isUserData, ArrayList<ItemQuotes> arrayList, ArrayList<ItemQuotes> arrayListTemp) {
        this.arrayList = arrayList;
        this.arrayListTemp = arrayListTemp;
        this.context = context;
        this.isUserData = isUserData;
        methods = new Methods(context, interAdListener);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        RoundedImageView imageView;
        ImageView iv_status;
        TextView tv_likes, tv_downloads, tv_views;
        ProgressBar progressBar;

        MyViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.iv_by_cat);
            iv_status = view.findViewById(R.id.iv_img_status);
            tv_likes = view.findViewById(R.id.tv_img_likes);
            tv_views = view.findViewById(R.id.tv_img_views);
            tv_downloads = view.findViewById(R.id.tv_img_downloads);
            progressBar = view.findViewById(R.id.pb_image_by_cat);

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
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_quotes_image, parent, false);
            return new MyViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MyViewHolder) {
            ((MyViewHolder) holder).tv_likes.setText(methods.format(Double.parseDouble(arrayList.get(position).getLikes())));
            ((MyViewHolder) holder).tv_downloads.setText(methods.format(Double.parseDouble(arrayList.get(position).getDownloads())));
            ((MyViewHolder) holder).tv_views.setText(methods.format(Double.parseDouble(arrayList.get(position).getViews())));

            if (isUserData && arrayList.get(position).getIsApproved()) {
                ((MyViewHolder) holder).iv_status.setVisibility(View.VISIBLE);
            } else {
                ((MyViewHolder) holder).iv_status.setVisibility(View.GONE);
            }

            ((MyViewHolder) holder).imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    methods.showInter(holder.getAdapterPosition(), "");
                }
            });
            ((MyViewHolder) holder).progressBar.setVisibility(View.VISIBLE);

            Picasso.get()
                    .load(methods.getImageThumbSize(arrayList.get(position).getImageSmall(), context.getString(R.string.latest)))
                    .fit().centerCrop()
                    .placeholder(R.drawable.placeholder)
                    .into(((MyViewHolder) holder).imageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            ((MyViewHolder) holder).progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError(Exception e) {
                            ((MyViewHolder) holder).progressBar.setVisibility(View.GONE);
                        }
                    });
        } else if (holder instanceof ADViewHolder) {
            if (isAdLoaded) {
                if (((ADViewHolder) holder).rl_native_ad.getChildCount() == 0) {
                    if (mNativeAdsAdmob.size() >= 1) {

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

    public boolean isHeader(int position) {
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

    public void addAds(NativeAd nativeAd) {
        mNativeAdsAdmob.add(nativeAd);
        isAdLoaded = true;
    }

    private void populateUnifiedNativeAdView(NativeAd nativeAd, NativeAdView adView) {

        MediaView mediaView = adView.findViewById(R.id.ad_media);
        adView.setMediaView(mediaView);

        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_icon));
        adView.setPriceView(adView.findViewById(R.id.ad_price));
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
        adView.setStoreView(adView.findViewById(R.id.ad_store));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));

        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());

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

    private InterAdListener interAdListener = new InterAdListener() {
        @Override
        public void onClick(int position, String type) {
            int real_pos = getRealPos(position);

            MySingleton.getInstance().getQuotes().clear();
            MySingleton.getInstance().getQuotes().addAll(arrayList);

            Intent intent = new Intent(context, QuoteDetailsImage.class);
            intent.putExtra("pos", real_pos);
            intent.putExtra("arr", arrayListTemp);
            intent.putExtra("user", isUserData);
            context.startActivity(intent);
        }
    };
}