package com.royal.attitude.status.fragments;

import android.app.PendingIntent;
import android.app.RecoverableSecurityException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.royal.attitude.status.adapter.AdapterMyCollection;
import com.royal.attitude.status.interfaces.DeleteListener;
import com.royal.attitude.status.R;
import com.royal.attitude.status.utils.Methods;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static android.app.Activity.RESULT_OK;

public class FragmentMyCollQuotes extends Fragment {

    Methods methods;
    GridLayoutManager lLayout;
    RecyclerView recyclerView;
    AdapterMyCollection adapterMyCollection;
    ArrayList<Uri> arrayList;
    ArrayList<String> arrayListDisplayName;
    TextView tv_empty;
    int position, deletePos = 0;
    ;
    int DELETE_REQUEST_URI_R = 11, DELETE_REQUEST_URI_Q = 12;

    public FragmentMyCollQuotes newInstance(int pos) {
        FragmentMyCollQuotes fragment = new FragmentMyCollQuotes();
        Bundle args = new Bundle();
        args.putInt("someInt", pos);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_my_coll, container, false);

        arrayList = new ArrayList<>();
        arrayListDisplayName = new ArrayList<>();

        position = getArguments().getInt("someInt", 0);
        methods = new Methods(getActivity());

        tv_empty = rootView.findViewById(R.id.tv_mycoll_empty);
        lLayout = new GridLayoutManager(getActivity(), 2);

        recyclerView = rootView.findViewById(R.id.recyclerView_myCollection);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(lLayout);

        new LoadQuote().execute();

        setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
    }

    public class LoadQuote extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            getImages();
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if (adapterMyCollection == null) {
                adapterMyCollection = new AdapterMyCollection(getActivity(), arrayList, new DeleteListener() {
                    @Override
                    public void onDelete(int pos) {
                        showDeleteDialog(pos);
                    }
                });
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView.setAdapter(adapterMyCollection);
                    }
                }, 500);

            } else {
                adapterMyCollection.notifyDataSetChanged();
            }
            setEmpty();
            super.onPostExecute(s);
        }
    }

    public void getImages() {
        arrayList.clear();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            String selection;
            String[] selectionArgs;

            if (position == 0) {
                selection = MediaStore.Files.FileColumns.RELATIVE_PATH + " like? ";
                selectionArgs = new String[]{"%" + getString(R.string.app_name) + "/" + getString(R.string.my_quotes) + "%"};
            } else {
                selection = MediaStore.Images.Media.BUCKET_DISPLAY_NAME + " like? ";
                selectionArgs = new String[]{getString(R.string.app_name)};
            }

            Cursor cursor = getActivity().getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    new String[]{MediaStore.Images.Media._ID, MediaStore.Images.Media.DISPLAY_NAME},
                    selection,
                    selectionArgs,
                    null);

            if (cursor != null && cursor.moveToFirst()) {
                for (int i = 0; i < cursor.getCount(); i++) {
                    String id = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media._ID));

                    arrayListDisplayName.add(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)));

                    arrayList.add(Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id));

                    cursor.moveToNext();
                }

                cursor.close();
            }
        } else {
            File root;
            if (position == 0) {
                root = new File(Environment.getExternalStorageDirectory() + File.separator + Environment.DIRECTORY_PICTURES + File.separator + getResources().getString(R.string.app_name) + File.separator + getString(R.string.my_quotes));
            } else {
                root = new File(Environment.getExternalStorageDirectory() + File.separator + Environment.DIRECTORY_PICTURES + File.separator + getResources().getString(R.string.app_name));
            }
            if (root.exists()) {
                String[] okFileExtensions = new String[]{"jpg", "jpeg", "png"};
                Collection<File> images = FileUtils.listFiles(root, okFileExtensions, true);
                for (File image : images) {
                    arrayList.add(getImageContentUri(image));
                }
            }
        }
    }

    public Uri getImageContentUri(File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = getActivity().getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID},
                MediaStore.Images.Media.DATA + "=? ",
                new String[]{filePath}, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            cursor.close();
            return Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + id);
        }
        return null;
    }

    public void setEmpty() {
        if (arrayList.size() > 0) {
            recyclerView.setVisibility(View.VISIBLE);
            tv_empty.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.GONE);
            tv_empty.setVisibility(View.VISIBLE);
        }
    }

    private void showDeleteDialog(int pos) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity(), R.style.AlertDialogTheme);
        alertDialog.setTitle(getString(R.string.delete));
        alertDialog.setMessage(getString(R.string.sure_delete));
        alertDialog.setPositiveButton(getString(R.string.delete), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                try {
                    int delete = getActivity().getContentResolver().delete(arrayList.get(pos), null, null);
                    if (delete == 1) {
                        adapterMyCollection.remove(pos);
                        Toast.makeText(getActivity(), getResources().getString(R.string.image_deleted), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), getResources().getString(R.string.error_delete), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception exception) {
                    deletePos = pos;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && exception instanceof RecoverableSecurityException) {
                        try {
                            ArrayList<Uri> arrayListUri = new ArrayList<>();
                            arrayListUri.add(arrayList.get(pos));
                            PendingIntent editPendingIntent = MediaStore.createDeleteRequest(getActivity().getContentResolver(), arrayListUri);
                            startIntentSenderForResult(editPendingIntent.getIntentSender(), DELETE_REQUEST_URI_R, null, 0, 0, 0, null);
                        } catch (IntentSender.SendIntentException e) {
                            e.printStackTrace();
                        }
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && exception instanceof RecoverableSecurityException) {
                        try {
                            startIntentSenderForResult(((RecoverableSecurityException) exception).getUserAction().getActionIntent().getIntentSender(), DELETE_REQUEST_URI_Q, null, 0, 0, 0, null);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == DELETE_REQUEST_URI_R) {
            if (resultCode == RESULT_OK) {
                adapterMyCollection.remove(deletePos);
                Toast.makeText(getActivity(), getResources().getString(R.string.image_deleted), Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == DELETE_REQUEST_URI_Q) {
            if (resultCode == RESULT_OK) {
                int delete = getActivity().getContentResolver().delete(arrayList.get(deletePos), null, null);
                if (delete == 1) {
                    adapterMyCollection.remove(deletePos);
                    Toast.makeText(getActivity(), getResources().getString(R.string.image_deleted), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.error_delete), Toast.LENGTH_SHORT).show();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onResume() {
        if (getActivity() != null) {
            new LoadQuote().execute();
        }
        super.onResume();
    }
}