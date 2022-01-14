package com.royal.attitude.status.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.royal.attitude.status.R;

import java.util.ArrayList;


public class AdapterColorsText extends RecyclerView.Adapter<AdapterColorsText.MyViewHolder> {

    private ArrayList<Integer> arrayList;
    public Context context;
    private int selectedPos = 0;

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView_color;

        MyViewHolder(View view) {
            super(view);
            imageView_color = view.findViewById(R.id.iv_edit_text_color);
        }
    }

    public AdapterColorsText(Context context, ArrayList<Integer> arrayList) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_color_text, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        try {
            holder.imageView_color.setColorFilter(arrayList.get(position));

//            if(selectedPos == position) {
//                holder.imageView_tick.setVisibility(View.VISIBLE);
//            } else {
//                holder.imageView_tick.setVisibility(View.INVISIBLE);
//            }
        } catch (Exception e) {
            e.printStackTrace();
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

    public void setSelectedColor (int selectedPos) {
        if (this.selectedPos >= 0) {
            notifyItemChanged(this.selectedPos);
        }
        this.selectedPos = selectedPos;
        notifyItemChanged(selectedPos);
    }
}