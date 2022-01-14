package com.royal.attitude.status.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.royal.attitude.status.item.ItemQuotes;
import com.royal.attitude.status.R;
import com.royal.attitude.status.utils.Methods;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class AdapterQuotesHome extends RecyclerView.Adapter<AdapterQuotesHome.MyViewHolder> {

    private ArrayList<ItemQuotes> arrayList;
    public Context context;
    private Methods methods;

    class MyViewHolder extends RecyclerView.ViewHolder {
        RoundedImageView iv_quotes;
        TextView tv_likes, tv_views, tv_downloads;

        MyViewHolder(View view) {
            super(view);
            iv_quotes = view.findViewById(R.id.iv_quotes_home);
            tv_likes = view.findViewById(R.id.tv_home_like);
            tv_views = view.findViewById(R.id.tv_home_views);
            tv_downloads = view.findViewById(R.id.tv_home_download);
        }
    }

    public AdapterQuotesHome(Context context, ArrayList<ItemQuotes> arrayList) {
        this.arrayList = arrayList;
        this.context = context;
        methods = new Methods(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_qutoes_home, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.tv_likes.setText(methods.format(Double.parseDouble(arrayList.get(holder.getAdapterPosition()).getLikes())));
        holder.tv_views.setText(methods.format(Double.parseDouble(arrayList.get(holder.getAdapterPosition()).getViews())));
        holder.tv_downloads.setText(methods.format(Double.parseDouble(arrayList.get(holder.getAdapterPosition()).getDownloads())));
        Picasso.get()
                .load(methods.getImageThumbSize(arrayList.get(position).getImageSmall(), context.getString(R.string.home)))
                .fit().centerCrop()
                .placeholder(R.drawable.placeholder)
                .into(holder.iv_quotes);
    }

    @Override
    public long getItemId(int id) {
        return id;
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }
}