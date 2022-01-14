package com.royal.attitude.status.fragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.royal.attitude.status.asyncTask.LoadAllCat;
import com.royal.attitude.status.asyncTask.LoadUpload;
import com.royal.attitude.status.interfaces.AllCategoryListener;
import com.royal.attitude.status.interfaces.SuccessListener;
import com.royal.attitude.status.item.ItemCat;
import com.royal.attitude.status.R;
import com.royal.attitude.status.utils.Constants;
import com.royal.attitude.status.utils.Methods;
import com.jaredrummler.android.colorpicker.ColorPickerDialog;
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

public class FragmentUploadsText extends Fragment implements ColorPickerDialogListener {

    private Methods methods;
    private ProgressDialog progress;
    private CardView cv_text;
    private Spinner spinnerCat;
    private AppCompatButton button_submit;
    private EditText editText_quote;
    private AppCompatEditText et_tags;
    private RoundedImageView iv_color_text, iv_color_bg, iv_font;
    private ArrayList<String> arrayList_cat, arrayList_fonts, arrayList_catid;
    private String suc = "", cat_id, textColor = "#000000", bgColor = "#FFFFFF";
    private int selectFontPos = -1;

    public FragmentUploadsText newInstance() {
        return new FragmentUploadsText();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_uploads_text, container, false);

        methods = new Methods(getActivity());

        progress = new ProgressDialog(getActivity());
        progress.setMessage(getActivity().getResources().getString(R.string.loading));
        progress.setCancelable(false);

        cv_text = v.findViewById(R.id.cv_upload_text);
        button_submit = v.findViewById(R.id.button_upload_text_submit);
        editText_quote = v.findViewById(R.id.editText_upload_text);
        et_tags = v.findViewById(R.id.et_upload_tags);
        iv_color_text = v.findViewById(R.id.iv_upload_text_color);
        iv_color_bg = v.findViewById(R.id.iv_upload_bg_color);
        iv_font = v.findViewById(R.id.iv_upload_text_font);

        arrayList_cat = new ArrayList<>();
        arrayList_fonts = new ArrayList<>();
        arrayList_catid = new ArrayList<>();

        spinnerCat = v.findViewById(R.id.spinner_upload_textcat);

        if (methods.isConnectingToInternet()) {
            loadAllCat();
        } else {
            Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.err_internet_not_conn), Toast.LENGTH_SHORT).show();
        }

        button_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Constants.isLogged) {
                    if (methods.isConnectingToInternet()) {
                        if (editText_quote.getText().toString().trim().isEmpty()) {
                            Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.please_enter_quote), Toast.LENGTH_SHORT).show();
                        } else {
//                            loadUploadText();
                            Toast.makeText(getActivity(), "Upload is disabled in demo app", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getActivity(), getResources().getString(R.string.err_internet_not_conn), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    methods.clickLogin();
                }
            }
        });

        spinnerCat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    cat_id = arrayList_catid.get(i);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        iv_color_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorPickerDialog pickerDialog = ColorPickerDialog.newBuilder()
                        .setDialogId(1)
                        .setColor(Color.parseColor(textColor))
                        .setDialogType(ColorPickerDialog.TYPE_PRESETS)
                        .setDialogTitle(R.string.app_name)
                        .setAllowCustom(true)
                        .setPresets(getResources().getIntArray(R.array.colorlist))
                        .setShowAlphaSlider(false)
                        .create();

                pickerDialog.setColorPickerDialogListener(FragmentUploadsText.this);
                pickerDialog.show(Objects.requireNonNull(getFragmentManager()), "color");
            }
        });

        iv_color_bg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ColorPickerDialog pickerDialog = ColorPickerDialog.newBuilder()
                        .setDialogId(2)
                        .setColor(Color.parseColor(bgColor))
                        .setDialogType(ColorPickerDialog.TYPE_PRESETS)
                        .setDialogTitle(R.string.app_name)
                        .setAllowCustom(true)
                        .setPresets(getResources().getIntArray(R.array.colorlist))
                        .setShowAlphaSlider(false)
                        .create();

                pickerDialog.setColorPickerDialogListener(FragmentUploadsText.this);
                pickerDialog.show(Objects.requireNonNull(getFragmentManager()), "color");
            }
        });

        iv_font.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTextFont();
            }
        });


        listAssetFiles("fonts/quotes");
        setTextFont();

        return v;
    }

    private void setTextFont() {

        if(selectFontPos < arrayList_fonts.size()-1) {
            selectFontPos = selectFontPos + 1;
        } else {
            selectFontPos = 0;
        }

        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/quotes/" + arrayList_fonts.get(selectFontPos));
        editText_quote.setTypeface(typeface);
    }

    private boolean listAssetFiles(String path) {

        String [] list;
        try {
            list = getActivity().getAssets().list(path);
            if (list.length > 0) {
                // This is a folder
                for (String file : list) {
                    if (!listAssetFiles(path + "/" + file))
                        return false;
                    else {
                        arrayList_fonts.add(file);
                    }
                }
            }
        } catch (IOException e) {
            return false;
        }

        return true;
    }

    @Override public void onColorSelected(int dialogId, int color) {
        switch (dialogId) {
            case 1:
                textColor = String.format("#%06X", (0xFFFFFF & color));
                editText_quote.setTextColor(color);
                break;
            case 2:
                bgColor = String.format("#%06X", (0xFFFFFF & color));
                cv_text.setCardBackgroundColor(color);
                break;
        }
    }

    @Override
    public void onDialogDismissed(int dialogId) {

    }

    private void loadAllCat() {

        LoadAllCat loadAllCat = new LoadAllCat(new AllCategoryListener() {
            @Override
            public void onStart() {
                progress.show();
            }

            @Override
            public void onEnd(String success, String verifyStatus, String message, ArrayList<ItemCat> arrayListCat) {
                if (getActivity() != null) {
                    progress.dismiss();
                    if (success.equals("1")) {
                        switch (verifyStatus) {
                            case "1":
                                for (int i = 0; i < arrayListCat.size(); i++) {
                                    arrayList_cat.add(arrayListCat.get(i).getName());
                                    arrayList_catid.add(arrayListCat.get(i).getId());
                                }

                                ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.layout_spinner, arrayList_cat);
                                spinnerCat.setAdapter(adapter);

                                if (arrayList_catid.size() > 0) {
                                    cat_id = arrayList_catid.get(0);
                                }
                                break;
                            case "-2":
                                methods.getInvalidUserDialog(message);
                                break;
                            case "-1":
                                methods.getVerifyDialog(getString(R.string.error_unauth_access), message);
                                break;
                        }
                    }
                }
            }
        }, methods.getAPIRequest(Constants.METHOD_CAT_TYPE, 0, "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", null));
        loadAllCat.execute();
    }

    private void uploadDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity(), R.style.AlertDialogTheme);
        alertDialog.setTitle(getString(R.string.upload_success));
        alertDialog.setMessage(getString(R.string.upload_success_message));
        alertDialog.setNegativeButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alertDialog.show();
    }

    private void loadUploadText() {

        LoadUpload loadUpload = new LoadUpload(new SuccessListener() {
            @Override
            public void onStart() {
                progress.show();
            }

            @Override
            public void onEnd(String success, String registerSuccess, String message) {
                if (getActivity() != null) {
                    if (success.equals("1")) {
                        if (registerSuccess.equals("1")) {
                            editText_quote.setText("");
                            et_tags.setText("");
                            uploadDialog();
                        } else if(registerSuccess.equals("-2")) {
                            methods.getInvalidUserDialog(message);
                        } else {
                            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getActivity(), getResources().getString(R.string.err_server), Toast.LENGTH_LONG).show();
                    }
                    progress.dismiss();
                }
            }
        }, methods.getAPIRequest(Constants.METHOD_UPLOAD_TEXT, 0, "", editText_quote.getText().toString(), "", cat_id, "", et_tags.getText().toString(), textColor.replace("#",""), arrayList_fonts.get(selectFontPos), bgColor.replace("#",""), "", "", "", "", Constants.itemUser.getId(), "", null));
        loadUpload.execute();
    }
}