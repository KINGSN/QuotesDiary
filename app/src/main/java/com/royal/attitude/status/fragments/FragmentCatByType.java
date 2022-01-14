package com.royal.attitude.status.fragments;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.view.MenuItemCompat;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.royal.attitude.status.adapter.AdapterCat;
import com.royal.attitude.status.interfaces.InterAdListener;
import com.royal.attitude.status.item.ItemCat;
import com.royal.attitude.status.QuotesByCategory;
import com.royal.attitude.status.R;
import com.royal.attitude.status.utils.Methods;
import com.royal.attitude.status.utils.RecyclerItemClickListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class FragmentCatByType extends Fragment {

    private RecyclerView recyclerView;
    private AdapterCat adapterCat;
    private SearchView searchView;
    private Methods methods;
    private ArrayList<ItemCat> arrayList_cat;
    private int position;

    private LinearLayout ll_empty;
    private TextView tv_empty;
    private AppCompatButton button_empty;

    public static FragmentCatByType newInstance(int pos, ArrayList<ItemCat> arrayListCat) {
        FragmentCatByType fragment = new FragmentCatByType();
        Bundle args = new Bundle();
        args.putInt("someInt", pos);
        args.putSerializable("array", arrayListCat);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_cat_by_type, container, false);

        position = getArguments().getInt("someInt", 0);
        arrayList_cat = (ArrayList<ItemCat>) getArguments().getSerializable("array");

        methods = new Methods(getActivity(), new InterAdListener() {
            @Override
            public void onClick(int pos, String type) {
                Intent intent = new Intent(getActivity(), QuotesByCategory.class);
                int id = Integer.parseInt(adapterCat.getID(pos));
                int reali = getPosition(id);
                intent.putExtra("cid", arrayList_cat.get(reali).getId());
                intent.putExtra("cname", arrayList_cat.get(reali).getName());
                intent.putExtra("pos", position);
                startActivity(intent);
            }
        });

        recyclerView = v.findViewById(R.id.rv_category);
        adapterCat = new AdapterCat(getActivity(), arrayList_cat);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
        adapterCat.setHasStableIds(true);

        ll_empty = v.findViewById(R.id.ll_empty);
        tv_empty = v.findViewById(R.id.tv_empty);
        button_empty = v.findViewById(R.id.btn_empty_try);

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int pos) {
                methods.showInter(pos, "");
            }
        }));

        getArray();

        setHasOptionsMenu(true);
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_search, menu);
        MenuItem item = menu.findItem(R.id.search);
        MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
        searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setOnQueryTextListener(queryTextListener);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }

    SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String s) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String s) {
            if (!searchView.isIconified()) {
                adapterCat.getFilter().filter(s);
                adapterCat.notifyDataSetChanged();
            }
            return true;
        }
    };

    public int getPosition(int id) {
        int count = 0;
        for (int i = 0; i < arrayList_cat.size(); i++) {
            if (id == Integer.parseInt(arrayList_cat.get(i).getId())) {
                count = i;
                break;
            }
        }
        return count;
    }

    private void getArray() {
        Collections.sort(arrayList_cat, new Comparator<ItemCat>() {
            @Override
            public int compare(ItemCat categoryItem, ItemCat t1) {
                return categoryItem.getName().compareToIgnoreCase(t1.getName());
            }
        });
        recyclerView.setAdapter(adapterCat);
        setEmpty();
    }

    private void setEmpty() {
        if (arrayList_cat.size() == 0) {
            button_empty.setVisibility(View.GONE);
            tv_empty.setText(getString(R.string.err_no_cat_found));
            ll_empty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            ll_empty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }
}