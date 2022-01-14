package com.royal.attitude.status;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import androidx.annotation.Dimension;
import androidx.annotation.NonNull;

import com.royal.attitude.status.item.ItemQuoteMaker;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.royal.attitude.status.adapter.AdapterColorsText;
import com.royal.attitude.status.adapter.AdapterFont;
import com.royal.attitude.status.utils.CustomEditText;
import com.royal.attitude.status.interfaces.KeyboardHeightObserver;
import com.royal.attitude.status.utils.KeyboardHeightProvider;
import com.royal.attitude.status.utils.Methods;
import com.royal.attitude.status.utils.MyBounceInterpolator;
import com.royal.attitude.status.utils.OnDragTouchListener;
import com.royal.attitude.status.utils.RecyclerItemClickListener;
import com.makeramen.roundedimageview.RoundedImageView;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.util.ArrayList;

import abak.tr.com.boxedverticalseekbar.BoxedVertical;

public class MakeQuotesActivity extends AppCompatActivity implements KeyboardHeightObserver {

    Methods methods;
    ArrayList<ItemQuoteMaker> arrayListText;
    ImageView iv_bg_quotemaker;
//    TextView tv_quote;
    RelativeLayout rl_quotemaker;
    ArrayList<Integer> arrayListColor;
    Boolean isBold = false;
    Bitmap bitmap_bg;
    ProgressDialog progressDialog;
//    OnDragTouchListener onDragTouchListener;
    InputMethodManager inputMethodManager;

    LinearLayout ll_edit;
    ImageView iv_quote, iv_close, iv_bold;

    RelativeLayout rl_text, rl_text_cutout, ll_make;
    RecyclerView rv_text_colors, rv_bg_colors;
    AdapterColorsText adapterColorsText, adapterColors_bg;
    RoundedImageView iv_bg_change;
    ImageView iv_ratio, iv_bgimage;
    CustomEditText et_text_quote;
    ImageView iv_text_align, iv_save;
    TextView tv_text_done;
    ViewPager vp_text_font;
    FontPagerAdapter fontPagerAdapter;
    ArrayList<String> arrayList_font;
//    int seekProgress = 15;
    BottomSheetDialog dialog_font;
    Typeface typeface;
    final static int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 123;
    int bg_color, editTextGravity = 1, textGravit = 1, TAG_GRAVITY_CENTER = 1, TAG_GRAVITY_START = 2, TAG_GRAVITY_END = 3;
    Boolean isTextBol = false, isBoldChanged = false, isFullRatio = true;
    int selectedTextPos = 0;

    int padding, screenWidth, radius, bottomMargin;
    Animation anim_right, anim_left;
    private KeyboardHeightProvider keyboardHeightProvider;

    BoxedVertical boxed_vertical;

    RoundedImageView iv_text_delete;
    int statusBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_quotes);

        keyboardHeightProvider = new KeyboardHeightProvider(this);
        View view = findViewById(R.id.rl_main);
        view.post(new Runnable() {
            public void run() {
                keyboardHeightProvider.start();
            }
        });

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ll_make.setVisibility(View.GONE);
            }
        });

        methods = new Methods(this);
        methods.forceRTLIfSupported(getWindow());
        methods.setStatusColor(getWindow());

        screenWidth = methods.getScreenWidth();
        padding = dp(8);
        radius = dp(5);
        bottomMargin = dp(30);

        inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        arrayListText = new ArrayList<>();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.saving));

        arrayListColor = new ArrayList<>();
        arrayList_font = new ArrayList<>();

        listAssetFontFiles();
        int[] rainbow = getResources().getIntArray(R.array.colorlist);
        for (int aRainbow : rainbow) {
            arrayListColor.add(aRainbow);
        }

        bg_color = getResources().getColor(R.color.color1);

        anim_right = AnimationUtils.loadAnimation(MakeQuotesActivity.this, R.anim.text_size_right);
        anim_right.setFillAfter(true);

        anim_left = AnimationUtils.loadAnimation(MakeQuotesActivity.this, R.anim.text_size_left);
        anim_left.setFillAfter(true);

        rl_quotemaker = findViewById(R.id.rl_quotemaker);
//        tv_quote = findViewById(R.id.tv);
        et_text_quote = findViewById(R.id.et_edit_text_quote);

//        onDragTouchListener = new OnDragTouchListener(tv_quote);
//        tv_quote.setOnTouchListener(onDragTouchListener);
        typeface = Typeface.createFromAsset(getAssets(), "fonts/" + arrayList_font.get(0));
        et_text_quote.setTypeface(typeface);

        iv_bg_quotemaker = findViewById(R.id.iv_bg_quotemaker);

        vp_text_font = findViewById(R.id.vp_text_font);
        boxed_vertical = findViewById(R.id.boxed_vertical);
        iv_bg_change = findViewById(R.id.iv_quotemaer_bg);
        iv_bgimage = findViewById(R.id.iv_quotemaer_bgimage);
        iv_ratio = findViewById(R.id.iv_quotemaer_ratio);
        ll_make = findViewById(R.id.ll_make);
        ll_edit = findViewById(R.id.ll_edit);
        iv_close = findViewById(R.id.iv_edit_close);
        iv_quote = findViewById(R.id.iv_edit_quote);
        iv_bold = findViewById(R.id.iv_edit_text_bold);

        iv_bold.setColorFilter(Color.GRAY);
        boxed_vertical.startAnimation(anim_left);

        rl_text = findViewById(R.id.rl_text);
        rl_text_cutout = findViewById(R.id.rl_text_cutout);
        rv_text_colors = findViewById(R.id.rv_edit_text_color);
        tv_text_done = findViewById(R.id.iv_edit_text_done);
        iv_text_align = findViewById(R.id.iv_edit_text_align);
        iv_save = findViewById(R.id.iv_edit_save);

        iv_text_delete = findViewById(R.id.iv_demo);


        statusBar = (int) Math.ceil(25 * getResources().getDisplayMetrics().density);

        rl_text.setVisibility(View.GONE);

        fontPagerAdapter = new FontPagerAdapter();
        vp_text_font.setAdapter(fontPagerAdapter);

        rv_text_colors.setLayoutManager(new LinearLayoutManager(MakeQuotesActivity.this, LinearLayoutManager.HORIZONTAL, false));
        adapterColorsText = new AdapterColorsText(MakeQuotesActivity.this, arrayListColor);
        rv_text_colors.setAdapter(adapterColorsText);

        adapterColors_bg = new AdapterColorsText(MakeQuotesActivity.this, arrayListColor);
        rv_bg_colors = findViewById(R.id.rv_bg_color);
        rv_bg_colors.setLayoutManager(new LinearLayoutManager(MakeQuotesActivity.this, LinearLayoutManager.HORIZONTAL, false));
        rv_bg_colors.setAdapter(adapterColors_bg);

        et_text_quote.setShadowLayer(padding, 0f, 0f, 0);
        et_text_quote.setPadding(padding, padding, padding, padding);

        iv_bold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                didTapButton(v);
                isBold = !isBold;
                setBoldButtonStatus();
            }
        });

        iv_ratio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                didTapButton(view);

                RelativeLayout.LayoutParams params;
                if (isFullRatio) {
                    iv_ratio.setImageResource(R.drawable.ic_ratio_square);
                    params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, screenWidth);
                    params.addRule(RelativeLayout.CENTER_IN_PARENT);
                } else {
                    iv_ratio.setImageResource(R.drawable.ic_ratio_full);
                    params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    params.addRule(RelativeLayout.BELOW, R.id.statusBarView);
                }
                rl_quotemaker.setLayoutParams(params);


//                RelativeLayout.LayoutParams paramsChild = new RelativeLayout.LayoutParams(iv_text_delete.getWidth(), iv_text_delete.getHeight());
//                paramsChild.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//                iv_text_delete.setLayoutParams(paramsChild);

                isFullRatio = !isFullRatio;

                for (int i = 0; i < arrayListText.size(); i++) {
                    arrayListText.get(i).getTextView().setOnTouchListener(new OnDragTouchListener(arrayListText.get(i).getTextView(), iv_text_delete, statusBar, bottomMargin, arrayListText.get(i).getOnDragTouchListener()));
                }
            }
        });

        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        iv_quote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                setLayouts(true);
                addTextView("New Text");
            }
        });

        iv_bg_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                didTapButton(v);

                if (ll_make.getVisibility() == View.GONE) {
                    ll_make.setVisibility(View.VISIBLE);
                } else {
                    ll_make.setVisibility(View.GONE);
                }
            }
        });

        iv_bgimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                didTapButton(v);

                if (isFullRatio) {
                    CropImage.activity()
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .start(MakeQuotesActivity.this);
                } else {
                    CropImage.activity()
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setAspectRatio(1, 1)
                            .start(MakeQuotesActivity.this);
                }
            }
        });

        boxed_vertical.setOnBoxedPointsChangeListener(new BoxedVertical.OnValuesChangeListener() {
            @Override
            public void onPointsChanged(BoxedVertical boxedPoints, final int value) {
                System.out.println(value);
                et_text_quote.setTextSize(Dimension.SP, value);
            }

            @Override
            public void onStartTrackingTouch(BoxedVertical boxedPoints) {
                boxed_vertical.startAnimation(anim_right);
            }

            @Override
            public void onStopTrackingTouch(BoxedVertical boxedPoints) {
                boxed_vertical.startAnimation(anim_left);
            }
        });

        vp_text_font.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/" + arrayList_font.get(i));
                et_text_quote.setTypeface(typeface);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        rv_bg_colors.addOnItemTouchListener(new RecyclerItemClickListener(MakeQuotesActivity.this, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                iv_bg_quotemaker.setImageDrawable(null);
                iv_bg_quotemaker.setBackgroundColor(arrayListColor.get(position));

                iv_bg_change.setColorFilter(arrayListColor.get(position));
            }
        }));

        et_text_quote.setKeyImeChangeListener(new CustomEditText.KeyImeChange() {
            @Override
            public void onKeyIme(int keyCode, KeyEvent event) {
                if (KeyEvent.KEYCODE_BACK == event.getKeyCode()) {
                    setLayouts(false);
                    et_text_quote.clearFocus();
                    et_text_quote.setText(arrayListText.get(selectedTextPos).getTextView().getText());
                    et_text_quote.setTextColor(arrayListText.get(selectedTextPos).getTextView().getTextColors());

                    if (isBoldChanged) {
                        if (arrayListText.get(selectedTextPos).getIsTextBold()) {
                            iv_bold.setColorFilter(null);
                            et_text_quote.setTypeface(arrayListText.get(selectedTextPos).getTextView().getTypeface(), Typeface.BOLD);
                        } else {
                            iv_bold.setColorFilter(Color.GRAY);
                            et_text_quote.setTypeface(arrayListText.get(selectedTextPos).getTextView().getTypeface(), Typeface.NORMAL);
                        }
                    }
                    isBoldChanged = false;
                    boxed_vertical.setValue(arrayListText.get(selectedTextPos).getSeekProgress());
                    et_text_quote.setTextSize(Dimension.SP, arrayListText.get(selectedTextPos).getSeekProgress());

                    setEditTextGravity();
                }
            }
        });

        rv_text_colors.addOnItemTouchListener(new RecyclerItemClickListener(MakeQuotesActivity.this, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                et_text_quote.setTextColor(arrayListColor.get(position));
            }
        }));

        tv_text_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLayouts(false);
                inputMethodManager.hideSoftInputFromWindow(et_text_quote.getWindowToken(), 0);
                arrayListText.get(selectedTextPos).getTextView().setText(et_text_quote.getText());
                arrayListText.get(selectedTextPos).getTextView().setTextColor(et_text_quote.getTextColors());
                arrayListText.get(selectedTextPos).setFontPos(vp_text_font.getCurrentItem());

                if (isBold) {
                    arrayListText.get(selectedTextPos).getTextView().setTypeface(et_text_quote.getTypeface(), Typeface.BOLD);
                } else {
                    arrayListText.get(selectedTextPos).getTextView().setTypeface(et_text_quote.getTypeface(), Typeface.NORMAL);
                }
                isBoldChanged = false;
                arrayListText.get(selectedTextPos).setIsTextBold(isBold);
                arrayListText.get(selectedTextPos).setSeekProgress(boxed_vertical.getValue());

                arrayListText.get(selectedTextPos).getTextView().setTextSize(Dimension.SP, boxed_vertical.getValue());
                arrayListText.get(selectedTextPos).getTextView().setOnTouchListener(new OnDragTouchListener(arrayListText.get(selectedTextPos).getTextView(), iv_text_delete, statusBar, bottomMargin, arrayListText.get(selectedTextPos).getOnDragTouchListener()));

                setChangeTextViewGravity();
            }
        });

        iv_text_align.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                didTapButton(v);
                setChangeEditTextGravity();
            }
        });

        iv_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                didTapButton(v);
                if (methods.checkPer()) {
                    new LoadSaveImage().execute();
                }
            }
        });


        addTextView(getString(R.string.write_quote_here));
    }

    private void addTextView(String text) {
        TextView tv_new = new TextView(MakeQuotesActivity.this);
        tv_new.setTag(String.valueOf(arrayListText.size()));
        tv_new.setTypeface(typeface);
        tv_new.setTextSize(Dimension.SP, 25);
        tv_new.setText(text);
        tv_new.setTextColor(arrayListColor.get(7));
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(5,5,5,5);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        tv_new.setLayoutParams(params);

        tv_new.setShadowLayer(padding, 0f, 0f, 0);
        tv_new.setPadding(padding, padding, padding, padding);

        rl_quotemaker.addView(tv_new);

        OnDragTouchListener.OnDragActionListener onDragActionListener = new OnDragTouchListener.OnDragActionListener() {
            @Override
            public void onDragStart(View view) {
                tv_new.bringToFront();
                selectedTextPos = Integer.parseInt(tv_new.getTag().toString());

                ll_edit.setVisibility(View.GONE);
                iv_text_delete.setVisibility(View.VISIBLE);
            }

            @Override
            public void onDragEnd(View view, Boolean delete) {
                ll_edit.setVisibility(View.VISIBLE);
                iv_text_delete.setVisibility(View.GONE);

                if(delete) {
                    try {
                        arrayListText.get(selectedTextPos).getTextView().setOnTouchListener(null);

                        rl_quotemaker.removeView(arrayListText.get(selectedTextPos).getTextView());
                        arrayListText.remove(selectedTextPos);

                        if(selectedTextPos < arrayListText.size()) {
                            for (int i = selectedTextPos; i < arrayListText.size(); i++) {
                                arrayListText.get(i).getTextView().setTag(String.valueOf(i));
                            }
                        } else {
                            if(selectedTextPos != 0) {
                                selectedTextPos = selectedTextPos - 1;
                            }
                        }

                        iv_text_delete.animate().scaleX(1f).setDuration(200).setInterpolator(new OvershootInterpolator()).start();
                        iv_text_delete.animate().scaleY(1f).setDuration(200).setInterpolator(new OvershootInterpolator()).start();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        tv_new.setOnTouchListener(new OnDragTouchListener(tv_new, iv_text_delete, statusBar, bottomMargin, onDragActionListener));

        tv_new.bringToFront();

        tv_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedTextPos = Integer.parseInt(tv_new.getTag().toString());
                tv_new.bringToFront();
                setLayouts(true);
            }
        });

        selectedTextPos = arrayListText.size();
        arrayListText.add(new ItemQuoteMaker(tv_new, onDragActionListener));
    }

    private int dp(int value) {
        return (int) (getResources().getDisplayMetrics().density * value + 0.5f);
    }

    private void setChangeTextViewGravity() {

        arrayListText.get(selectedTextPos).setTextGravity(editTextGravity);
        if (arrayListText.get(selectedTextPos).getTextGravity() == TAG_GRAVITY_START) {
            arrayListText.get(selectedTextPos).getTextView().setGravity(Gravity.START);
        } else if (arrayListText.get(selectedTextPos).getTextGravity() == TAG_GRAVITY_CENTER) {
            arrayListText.get(selectedTextPos).getTextView().setGravity(Gravity.CENTER);
        } else if (arrayListText.get(selectedTextPos).getTextGravity() == TAG_GRAVITY_END) {
            arrayListText.get(selectedTextPos).getTextView().setGravity(Gravity.END);
        }
    }

    private void setEditTextGravity() {
        editTextGravity = arrayListText.get(selectedTextPos).getTextGravity();
        if (editTextGravity == TAG_GRAVITY_START) {
            et_text_quote.setGravity(Gravity.START | Gravity.CENTER);
            iv_text_align.setImageDrawable(ContextCompat.getDrawable(MakeQuotesActivity.this, R.drawable.ic_align_start));
        } else if (editTextGravity == TAG_GRAVITY_CENTER) {
            et_text_quote.setGravity(Gravity.CENTER);
            iv_text_align.setImageDrawable(ContextCompat.getDrawable(MakeQuotesActivity.this, R.drawable.ic_align_center));
        } else if (editTextGravity == TAG_GRAVITY_END) {
            et_text_quote.setGravity(Gravity.END | Gravity.CENTER);
            iv_text_align.setImageDrawable(ContextCompat.getDrawable(MakeQuotesActivity.this, R.drawable.ic_align_end));
        }
    }

    private void setChangeEditTextGravity() {
        if (editTextGravity == TAG_GRAVITY_START) {
            et_text_quote.setGravity(Gravity.CENTER);
            editTextGravity = TAG_GRAVITY_CENTER;
            iv_text_align.setImageDrawable(ContextCompat.getDrawable(MakeQuotesActivity.this, R.drawable.ic_align_center));
        } else if (editTextGravity == TAG_GRAVITY_CENTER) {
            et_text_quote.setGravity(Gravity.END | Gravity.CENTER);
            editTextGravity = TAG_GRAVITY_END;
            iv_text_align.setImageDrawable(ContextCompat.getDrawable(MakeQuotesActivity.this, R.drawable.ic_align_end));
        } else if (editTextGravity == TAG_GRAVITY_END) {
            et_text_quote.setGravity(Gravity.START | Gravity.CENTER);
            editTextGravity = TAG_GRAVITY_START;
            iv_text_align.setImageDrawable(ContextCompat.getDrawable(MakeQuotesActivity.this, R.drawable.ic_align_start));
        } else {
            et_text_quote.setGravity(Gravity.CENTER);
            editTextGravity = TAG_GRAVITY_CENTER;
            iv_text_align.setImageResource(R.drawable.ic_align_center);
        }
    }

    private void setBoldButtonStatus() {
        if (!isBold) {
            iv_bold.setColorFilter(Color.GRAY);
            Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/" + arrayList_font.get(vp_text_font.getCurrentItem()));
            et_text_quote.setTypeface(typeface, Typeface.NORMAL);
        } else {
            iv_bold.setColorFilter(null);
            et_text_quote.setTypeface(et_text_quote.getTypeface(), Typeface.BOLD);
        }
        isBoldChanged = true;
    }

    private Bitmap getBitmapFromView(View view) {
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null) {
            bgDrawable.draw(canvas);
        } else {
            canvas.drawColor(Color.WHITE);
        }
        view.draw(canvas);
        return returnedBitmap;
    }

    class LoadSaveImage extends AsyncTask<String, Boolean, Boolean> {
        @Override
        protected void onPreExecute() {
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            try {
                String filename = System.currentTimeMillis() + ".jpeg";
                Bitmap bitmap = getBitmapFromView(rl_quotemaker);
                methods.saveImage(bitmap, filename, "quotemaker");
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean s) {
            if (s) {
                progressDialog.dismiss();
                Toast.makeText(MakeQuotesActivity.this, getString(R.string.quote_saved), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MakeQuotesActivity.this, getString(R.string.err_saving_quote), Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(s);
        }
    }

    private void listAssetFontFiles() {
        String[] list;
        try {
            list = getAssets().list("fonts");
            if (list != null && list.length > 0) {
                // This is a folder
                for (String file : list) {
                    if (file.endsWith(".ttf") || file.endsWith("otf")) {
                        arrayList_font.add(file);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class FontPagerAdapter extends PagerAdapter {

        private LayoutInflater inflater;

        private FontPagerAdapter() {
            inflater = getLayoutInflater();
        }

        @Override
        public int getCount() {
            return arrayList_font.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view.equals(object);
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {

            View fontLayout = inflater.inflate(R.layout.layout_font, container, false);
            assert fontLayout != null;
            TextView tv_font = fontLayout.findViewById(R.id.tv_text_font);

            tv_font.setText(arrayList_font.get(position).replace(".otf", "").replace(".ttf", ""));
            Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/" + arrayList_font.get(position));
            tv_font.setTypeface(typeface);

            tv_font.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    inputMethodManager.hideSoftInputFromWindow(et_text_quote.getWindowToken(), 0);
                    showFontDialog();
                }
            });

            fontLayout.setTag("myview" + position);
            container.addView(fontLayout, 0);
            return fontLayout;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }
    }

    private void showFontDialog() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        View view = inflater.inflate(R.layout.layout_bottomsheet_font, null);

        dialog_font = new BottomSheetDialog(MakeQuotesActivity.this);
        dialog_font.setContentView(view);
        dialog_font.getWindow().findViewById(R.id.design_bottom_sheet).setBackgroundResource(android.R.color.transparent);
        dialog_font.show();

        final RecyclerView rv_font = dialog_font.findViewById(R.id.rv_bottom_sheet_font);
        rv_font.setLayoutManager(new LinearLayoutManager(MakeQuotesActivity.this));

        AdapterFont adapterFont = new AdapterFont(MakeQuotesActivity.this, arrayList_font);
        rv_font.setAdapter(adapterFont);

        rv_font.addOnItemTouchListener(new RecyclerItemClickListener(MakeQuotesActivity.this, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/" + arrayList_font.get(position));
                et_text_quote.setTypeface(typeface);
                dialog_font.dismiss();
                vp_text_font.setCurrentItem(position);
            }
        }));

        dialog_font.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        et_text_quote.requestFocus();
                        inputMethodManager.showSoftInput(et_text_quote, 0);
                    }
                }, 200);
            }
        });
    }

    public void didTapButton(View view) {
        final Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.click_anim);
        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
        myAnim.setInterpolator(interpolator);

        view.startAnimation(myAnim);
    }

    private void setLayouts(Boolean isTextEdit) {
        if (isTextEdit) {
            ll_edit.setVisibility(View.GONE);
            rl_text.setVisibility(View.VISIBLE);
            ll_make.setVisibility(View.GONE);

            et_text_quote.setText(arrayListText.get(selectedTextPos).getTextView().getText());
            et_text_quote.setTextColor(arrayListText.get(selectedTextPos).getTextView().getCurrentTextColor());
            et_text_quote.setTextSize(Dimension.SP, arrayListText.get(selectedTextPos).getSeekProgress());
            isBold = arrayListText.get(selectedTextPos).getIsTextBold();
            boxed_vertical.setValue(arrayListText.get(selectedTextPos).getSeekProgress());

            if (fontPagerAdapter != null && arrayListText.get(selectedTextPos).getFontPos() != -1) {
                vp_text_font.setCurrentItem(arrayListText.get(selectedTextPos).getFontPos());
            }

            setEditTextGravity();
            setBoldButtonStatus();

            et_text_quote.requestFocus();
            inputMethodManager.showSoftInput(et_text_quote, 0);
        } else {
            ll_edit.setVisibility(View.VISIBLE);
            rl_text.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        if (rl_text.getVisibility() == View.VISIBLE) {
            setLayouts(false);
            inputMethodManager.hideSoftInputFromWindow(et_text_quote.getWindowToken(), 0);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                try {
                    bitmap_bg = MediaStore.Images.Media.getBitmap(getContentResolver(), resultUri);
                    iv_bg_quotemaker.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    iv_bg_quotemaker.setImageBitmap(bitmap_bg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPause() {
        super.onPause();
        keyboardHeightProvider.setKeyboardHeightObserver(null);
    }

    @Override
    public void onResume() {
        super.onResume();
        keyboardHeightProvider.setKeyboardHeightObserver(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        keyboardHeightProvider.close();
    }

    @Override
    public void onKeyboardHeightChanged(int height, int orientation) {
        if (height > 0) {
            View statusBarView = findViewById(R.id.statusBarView);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) rl_text_cutout.getLayoutParams();
            params.height = methods.getScreenHeight() - (height + statusBarView.getHeight());
            rl_text_cutout.setLayoutParams(params);
        }
    }
}