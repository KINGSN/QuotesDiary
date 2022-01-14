package com.royal.attitude.status.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.royal.attitude.status.interfaces.DeleteListener;
import com.royal.attitude.status.interfaces.InterAdListener;
import com.royal.attitude.status.MyCollectionDetail;
import com.royal.attitude.status.R;
import com.royal.attitude.status.utils.Methods;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class AdapterMyCollection extends RecyclerView.Adapter<AdapterMyCollection.MyViewHolder> {

    private ArrayList<Uri> arrayList;
    public Context context;
    public Methods methods;
    DeleteListener deleteListener;

    class MyViewHolder extends RecyclerView.ViewHolder {
        RoundedImageView imageView;
        ImageView iv_delete, iv_share;

        MyViewHolder(View view) {
            super(view);
            iv_delete = view.findViewById(R.id.iv_my_delete);
            iv_share = view.findViewById(R.id.iv_my_share);
            imageView = view.findViewById(R.id.iv_my);
        }
    }


    public AdapterMyCollection(Context context, ArrayList<Uri> arrayList, DeleteListener deleteListener) {
        this.arrayList = arrayList;
        this.context = context;
        this.deleteListener = deleteListener;
        methods = new Methods(context, interAdListener);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_quote_my, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                methods.showInter(holder.getAbsoluteAdapterPosition(), "list");
            }
        });

        Picasso.get()
                .load(arrayList.get(position))
                .fit().centerCrop()
                .placeholder(R.drawable.placeholder)
                .into(holder.imageView);

        holder.iv_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                methods.showInter(holder.getAbsoluteAdapterPosition(), "share");
            }
        });

        holder.iv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteListener.onDelete(holder.getAbsoluteAdapterPosition());
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public void remove(int position) {
        arrayList.remove(position);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                notifyItemRemoved(position);
            }
        }, 500);
    }

    InterAdListener interAdListener = new InterAdListener() {
        @Override
        public void onClick(int position, String type) {
            switch (type) {
                case "share":
                    methods.shareFileImageUri(arrayList.get(position), "");
                    break;
                case "list":
                    Intent intent = new Intent(context, MyCollectionDetail.class);
                    intent.putExtra("pos", position);
                    intent.putExtra("array", arrayList);
                    context.startActivity(intent);
                    break;
            }
        }
    };
}