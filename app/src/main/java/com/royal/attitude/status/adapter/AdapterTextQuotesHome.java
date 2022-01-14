package com.royal.attitude.status.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.royal.attitude.status.asyncTask.LoadLike;
import com.royal.attitude.status.interfaces.InterAdListener;
import com.royal.attitude.status.interfaces.SuccessListener;
import com.royal.attitude.status.item.ItemQuotes;
import com.royal.attitude.status.LoginActivity;
import com.royal.attitude.status.QuoteDetailsText;
import com.royal.attitude.status.R;
import com.royal.attitude.status.utils.Constants;
import com.royal.attitude.status.utils.Methods;
import com.royal.attitude.status.utils.MySingleton;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class AdapterTextQuotesHome extends RecyclerView.Adapter<AdapterTextQuotesHome.MyViewHolder> {

    private ArrayList<ItemQuotes> arrayList;
    public Context context;
    private Methods methods;

    class MyViewHolder extends RecyclerView.ViewHolder {
        LinearLayout ll_share, ll_like;
        RelativeLayout ll;
        TextView textView_quote, textView_likes, textView_views;
        ImageView iv_like, iv_views;
        Typeface font;

        MyViewHolder(View view) {
            super(view);
            ll = view.findViewById(R.id.ll_text);
            ll_share = view.findViewById(R.id.ll_text_share);
            ll_like = view.findViewById(R.id.ll_text_like);
            iv_like = view.findViewById(R.id.iv_like_text);
            iv_views = view.findViewById(R.id.iv_views_text);
            textView_quote = view.findViewById(R.id.tv_quote);
            textView_likes = view.findViewById(R.id.tv_like_text);
            textView_views = view.findViewById(R.id.tv_views_text);
        }
    }

    public AdapterTextQuotesHome(Context context, ArrayList<ItemQuotes> arrayList) {
        this.arrayList = arrayList;
        this.context = context;
        methods = new Methods(context, interAdListener);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_quotes_text, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        holder.iv_views.setColorFilter(R.color.black);
        holder.ll.setBackgroundColor(Color.parseColor(arrayList.get(position).getBgColor()));

        if (!arrayList.get(position).getFontColor().trim().equals("")) {
            holder.textView_quote.setTextColor(Color.parseColor(arrayList.get(position).getFontColor()));
        }

        if (!arrayList.get(position).getFont().trim().equals("")) {
            try {
                holder.font = Typeface.createFromAsset(context.getAssets(), "fonts/quotes/" + arrayList.get(position).getFont());
            } catch (Exception e) {
                holder.font = Typeface.createFromAsset(context.getAssets(), "fonts/quotes/Roboto.ttf");
            }
            holder.textView_quote.setTypeface(holder.font);
        }

        holder.textView_likes.setText(methods.format(Double.parseDouble(arrayList.get(position).getLikes())));
        holder.textView_views.setText(methods.format(Double.parseDouble(arrayList.get(position).getViews())));
        holder.textView_quote.setText(arrayList.get(position).getQuote());

        if (arrayList.get(holder.getAdapterPosition()).getLiked()) {
            holder.iv_like.setImageResource(R.mipmap.ic_like_grey_hover);
        } else {
            holder.iv_like.setImageResource(R.mipmap.ic_like_grey);
        }

        holder.ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                methods.showInter(holder.getAdapterPosition(), "list");
            }
        });

        holder.ll_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                methods.showInter(holder.getAdapterPosition(), "share");
            }
        });

        holder.ll_like.setOnClickListener(new View.OnClickListener() {
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
        return arrayList.size();
    }

    InterAdListener interAdListener = new InterAdListener() {
        @Override
        public void onClick(int position, String type) {
            switch (type) {
                case "share":
                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, context.getResources().getString(R.string.share_via));
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, arrayList.get(position).getQuote() + "\n\n" + context.getResources().getString(R.string.app_name) + " - https://play.google.com/store/apps/details?id=" + context.getPackageName());
                    context.startActivity(Intent.createChooser(sharingIntent, context.getResources().getString(R.string.share_via)));
                    break;
                case "list":
                    MySingleton.getInstance().getQuotes().clear();
                    MySingleton.getInstance().getQuotes().addAll(arrayList);

                    Intent intent = new Intent(context, QuoteDetailsText.class);
                    intent.putExtra("pos", position);
                    intent.putExtra("arr", arrayList);
                    context.startActivity(intent);
                    break;
            }
        }
    };
}