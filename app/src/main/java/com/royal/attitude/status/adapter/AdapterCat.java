package com.royal.attitude.status.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import androidx.annotation.NonNull;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.royal.attitude.status.item.ItemCat;
import com.royal.attitude.status.R;
import com.royal.attitude.status.utils.Methods;

import java.util.ArrayList;


public class AdapterCat extends RecyclerView.Adapter<AdapterCat.MyViewHolder> {

    private Methods methods;
    private ArrayList<ItemCat> arrayList;
    public Context context;
    private ArrayList<ItemCat> filteredArrayList;
    private NameFilter filter;

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textView_name;
        RoundedImageView imageView;
        View view_cat;

        MyViewHolder(View view) {
            super(view);
            textView_name = view.findViewById(R.id.textView);
            imageView = view.findViewById(R.id.imageView_cat);
            view_cat = view.findViewById(R.id.view_cat);
        }
    }

    public AdapterCat(Context context, ArrayList<ItemCat> arrayList) {
        this.arrayList = arrayList;
        this.context = context;
        filteredArrayList = arrayList;
        methods = new Methods(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_category, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        try {
            Picasso.get()
                    .load(arrayList.get(position).getImageBig())
                    .fit().centerCrop()
                    .placeholder(R.drawable.placeholder)
                    .into(holder.imageView);
            holder.textView_name.setText(arrayList.get(position).getName());

            new LoadColor(holder.view_cat).execute(methods.getImageThumbSize(arrayList.get(position).getImageSmall(),context.getString(R.string.categories)));
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

    public String getID(int pos) {
        return arrayList.get(pos).getId();
    }

    public Filter getFilter() {
        if (filter == null) {
            filter = new NameFilter();
        }
        return filter;
    }

    private class NameFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            constraint = constraint.toString().toLowerCase();
            FilterResults result = new FilterResults();
            if (constraint.toString().length() > 0) {
                ArrayList<ItemCat> filteredItems = new ArrayList<>();

                for (int i = 0, l = filteredArrayList.size(); i < l; i++) {
                    String nameList = filteredArrayList.get(i).getName();
                    if (nameList.toLowerCase().contains(constraint))
                        filteredItems.add(filteredArrayList.get(i));
                }
                result.count = filteredItems.size();
                result.values = filteredItems;
            } else {
                synchronized (this) {
                    result.values = filteredArrayList;
                    result.count = filteredArrayList.size();
                }
            }
            return result;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {

            arrayList = (ArrayList<ItemCat>) results.values;
            notifyDataSetChanged();
        }
    }

    public class LoadColor extends AsyncTask<String, String, String> {

        Bitmap bitmap;
        View view;

        LoadColor(View view) {
            this.view = view;
        }

        @Override
        protected String doInBackground(String... strings) {
            bitmap = methods.getBitmapFromURL(strings[0]);
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                    @Override
                    public void onGenerated(@NonNull Palette palette) {
                        int defaultValue = 0x000000;
                        int vibrant = palette.getVibrantColor(defaultValue);

                        view.setBackground(methods.getGradientDrawable((vibrant & 0x00FFFFFF) | (0x80 << 24), (vibrant & 0x00FFFFFF) | (0x80 << 24)));
                    }
                });
                super.onPostExecute(s);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}