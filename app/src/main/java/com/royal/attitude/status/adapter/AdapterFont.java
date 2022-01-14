package com.royal.attitude.status.adapter;

import android.content.Context;
import android.graphics.Typeface;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.royal.attitude.status.R;

import java.util.ArrayList;


public class AdapterFont extends RecyclerView.Adapter<AdapterFont.MyViewHolder> {

    private ArrayList<String> arrayList;
    public Context context;

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        MyViewHolder(View view) {
            super(view);
            textView = view.findViewById(R.id.tv_bottom_font);
        }
    }

    public AdapterFont(Context context, ArrayList<String> arrayList) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_bottom_font, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.textView.setText(arrayList.get(position).replace(".otf","").replace(".ttf",""));
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/" + arrayList.get(position));
        holder.textView.setTypeface(typeface);

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