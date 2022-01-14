package com.royal.attitude.status;

import android.app.PendingIntent;
import android.app.RecoverableSecurityException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.squareup.picasso.Picasso;
import com.royal.attitude.status.utils.Constants;
import com.royal.attitude.status.utils.ExtendedViewPager;
import com.royal.attitude.status.utils.Methods;
import com.royal.attitude.status.utils.TouchImageView;

import java.util.ArrayList;

public class MyCollectionDetail extends AppCompatActivity {

    CustomPagerAdapter customPagerAdapter;
    ExtendedViewPager viewPager;
    Toolbar toolbar;
    Methods methods;
    ArrayList<Uri> arrayList;
    int pos;
    FloatingActionButton button_share_image, button_set_wallpaper, button_delete, button_upload;
    int DELETE_REQUEST_URI_R = 11, DELETE_REQUEST_URI_Q = 12;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mycoll_detail);
        toolbar = findViewById(R.id.toolbar_mycoll_details);
        toolbar.setTitle(getResources().getString(R.string.quotes));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        methods = new Methods(this);
        methods.forceRTLIfSupported(getWindow());
        methods.setStatusColor(getWindow());

        pos = getIntent().getExtras().getInt("pos");
        arrayList = new ArrayList<>();
        arrayList = getIntent().getExtras().getParcelableArrayList("array");

        button_set_wallpaper = findViewById(R.id.button_mycoll_setas);
        button_share_image = findViewById(R.id.button_mycoll_share);
        button_delete = findViewById(R.id.button_mycoll_delete);
        button_upload = findViewById(R.id.button_mycoll_upload);

        viewPager = findViewById(R.id.vp_mycoll);
        customPagerAdapter = new CustomPagerAdapter(MyCollectionDetail.this, arrayList);

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
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        button_share_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                methods.shareFileImageUri(arrayList.get(pos), "");
            }
        });

        button_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyCollectionDetail.this, UploadQuotes.class);
                intent.putExtra("image", arrayList.get(viewPager.getCurrentItem()));
                startActivity(intent);
            }
        });

        button_set_wallpaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Constants.uri_setwall = arrayList.get(pos);
                Intent intent = new Intent(MyCollectionDetail.this, SetWallpaperActivity.class);
                startActivity(intent);
            }
        });

        button_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeleteDialog();

//                AlertDialog.Builder ai = new AlertDialog.Builder(MyCollectionDetail.this, R.style.AlertDialogTheme)
//                        .setMessage(getResources().getString(R.string.sure_delete))
//                        .setPositiveButton(getResources().getString(R.string.delete), new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                boolean delete = FileUtils.deleteQuietly(new File(arrayList.get(pos)));
//                                if (delete) {
//                                    customPagerAdapter.removeItem(pos);
//                                    Toast.makeText(MyCollectionDetail.this, getResources().getString(R.string.image_deleted), Toast.LENGTH_SHORT).show();
//                                } else {
//                                    Toast.makeText(MyCollectionDetail.this, getResources().getString(R.string.error_delete), Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        })
//                        .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                            }
//                        });
//                ai.show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDeleteDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MyCollectionDetail.this, R.style.AlertDialogTheme);
        alertDialog.setTitle(getString(R.string.delete));
        alertDialog.setMessage(getString(R.string.sure_delete));
        alertDialog.setPositiveButton(getString(R.string.delete), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                try {
                    int delete = getContentResolver().delete(arrayList.get(pos), null, null);
                    if (delete == 1) {
                        customPagerAdapter.removeItem(pos);
                        Toast.makeText(MyCollectionDetail.this, getResources().getString(R.string.image_deleted), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MyCollectionDetail.this, getResources().getString(R.string.error_delete), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception exception) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && exception instanceof RecoverableSecurityException) {
                        try {
                            ArrayList<Uri> arrayListUri = new ArrayList<>();
                            arrayListUri.add(arrayList.get(pos));
                            PendingIntent editPendingIntent = MediaStore.createDeleteRequest(getContentResolver(), arrayListUri);
                            startIntentSenderForResult(editPendingIntent.getIntentSender(), DELETE_REQUEST_URI_R, null, 0, 0, 0);
                        } catch (IntentSender.SendIntentException e) {
                            e.printStackTrace();
                        }
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && exception instanceof RecoverableSecurityException) {
                        try {
                            startIntentSenderForResult(((RecoverableSecurityException) exception).getUserAction().getActionIntent().getIntentSender(), DELETE_REQUEST_URI_Q, null, 0, 0, 0);
                        } catch (IntentSender.SendIntentException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        alertDialog.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        alertDialog.show();
    }

    class CustomPagerAdapter extends PagerAdapter {

        Context mContext;
        LayoutInflater mLayoutInflater;
        ArrayList<Uri> arrayList;

        CustomPagerAdapter(Context context, ArrayList<Uri> arrayList) {
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
            View itemView = mLayoutInflater.inflate(R.layout.layout_viewpager, container, false);
            final TouchImageView img = new TouchImageView(container.getContext());
            img.setTag(position);
            Picasso.get()
                    .load(arrayList.get(position))
                    .placeholder(R.drawable.placeholder)
                    .into(img);
            container.addView(img, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

            container.addView(itemView);
            return img;
        }

        void removeItem(int position) {

            if(arrayList.size() == 1) {
                finish();
            }
            arrayList.remove(position);
            notifyDataSetChanged();
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return PagerAdapter.POSITION_NONE;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == DELETE_REQUEST_URI_R) {
            if(resultCode == RESULT_OK) {
                customPagerAdapter.removeItem(pos);
                Toast.makeText(MyCollectionDetail.this, getResources().getString(R.string.image_deleted), Toast.LENGTH_SHORT).show();
            }
        } else if(requestCode == DELETE_REQUEST_URI_Q) {
            if(resultCode == RESULT_OK) {
                int delete = getContentResolver().delete(arrayList.get(pos), null, null);
                if (delete == 1) {
                    customPagerAdapter.removeItem(pos);
                    Toast.makeText(MyCollectionDetail.this, getResources().getString(R.string.image_deleted), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MyCollectionDetail.this, getResources().getString(R.string.error_delete), Toast.LENGTH_SHORT).show();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
