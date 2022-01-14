package com.royal.attitude.status.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
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

import java.io.File;
import java.util.ArrayList;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.fragment.app.Fragment;

public class FragmentUploadsImage extends Fragment {

    private Methods methods;
    private ProgressDialog progressDialog, progress;
    private Spinner spinner;
    private AppCompatButton button_browse, button_submit;
    private AppCompatEditText et_tags;
    private ImageView imageView;
    private ArrayList<String> arrayList_cat, arrayList_catid;
    private String cat_id;
    private Uri imageUri = null;

    public FragmentUploadsImage newInstance(String image) {
        FragmentUploadsImage fragment = new FragmentUploadsImage();
        Bundle args = new Bundle();
        if (image != null) {
            args.putString("image", image);
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_uploads, container, false);

        methods = new Methods(getActivity());

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getActivity().getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);

        progress = new ProgressDialog(getActivity());
        progress.setMessage(getActivity().getResources().getString(R.string.loading));
        progress.setCancelable(false);

        button_browse = v.findViewById(R.id.button_upload_wall_browse);
        button_submit = v.findViewById(R.id.button_upload_wall_submit);
        et_tags = v.findViewById(R.id.et_upload_tags);

        imageView = v.findViewById(R.id.iv_upload_wall_submit);

        arrayList_cat = new ArrayList<>();
        arrayList_catid = new ArrayList<>();

        spinner = v.findViewById(R.id.spinner_upload_wallcat);

        if (methods.isConnectingToInternet()) {
            loadAllCat();
        } else {
            Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.err_internet_not_conn), Toast.LENGTH_SHORT).show();
        }

        button_browse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (methods.checkPer()) {
                    pickImage();
                }
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (methods.checkPer()) {
                    pickImage();
                }
            }
        });

        button_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Constants.isLogged) {
                    if (methods.isConnectingToInternet()) {
                        if (imageUri == null) {
                            methods.showToast(getResources().getString(R.string.select_wallpaper));
                        } else {
//                            uploadImageQuote();
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

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                cat_id = arrayList_catid.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

//        if (getArguments().getString("image") != null) {
//            String image = getArguments().getString("image");
//
//            Uri uri = Uri.fromFile(new File(image));
//            imagePath = methods.getPathImage(uri);
//
//            try {
//                bitmap_upload = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
//                imageView.setImageBitmap(bitmap_upload);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }

        return v;
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
                                spinner.setAdapter(adapter);

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

    private void uploadImageQuote() {
        File file = null;
        try {
            if (imageUri != null) {
                file = new File(methods.getTempUploadPath(imageUri));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

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
                            imageView.setImageResource(R.drawable.placeholder_upload);
                            imageUri = null;
                            et_tags.setText("");
                            uploadDialog();
                        } else {
                            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getActivity(), getResources().getString(R.string.err_server), Toast.LENGTH_LONG).show();
                    }
                    progress.dismiss();
                }
            }
        }, methods.getAPIRequest(Constants.METHOD_UPLOAD_IMAGE, 0, "", "", "", cat_id, "", et_tags.getText().toString(), "", "", "", "", "", "", "", Constants.itemUser.getId(), "", file));
        loadUpload.execute();

    }

    private void pickImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        activityResultLaunch.launch(intent);
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

    private final ActivityResultLauncher<Intent> activityResultLaunch = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK) {
                if (result.getData() != null && result.getData().getData() != null) {
                    try {
                        imageUri = result.getData().getData();
                        imageView.setImageURI(imageUri);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    });
}