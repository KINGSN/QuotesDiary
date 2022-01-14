package com.royal.attitude.status;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.royal.attitude.status.asyncTask.LoadProfileEdit;
import com.royal.attitude.status.interfaces.ProfileEditListener;
import com.royal.attitude.status.R;
import com.royal.attitude.status.utils.Constants;
import com.royal.attitude.status.utils.Methods;
import com.royal.attitude.status.utils.SharedPref;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.io.File;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

public class ProfileEditActivity extends AppCompatActivity {

    Toolbar toolbar;
    Methods methods;
    SharedPref sharedPref;
    LinearLayout ll_pass, ll_cpass;
    RoundedImageView iv_profedit;
    EditText editText_name, editText_email, editText_phone, editText_pass, editText_cpass;
    ProgressDialog progressDialog;
    Uri imageUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);

        sharedPref = new SharedPref(this);
        methods = new Methods(this);
        methods.forceRTLIfSupported(getWindow());
        methods.setStatusColor(getWindow());

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);

        toolbar = findViewById(R.id.toolbar_proedit);
        toolbar.setTitle(getResources().getString(R.string.profile_edit));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        AppCompatButton button_update = findViewById(R.id.button_prof_update);
        ll_pass = findViewById(R.id.ll_profedit_pass);
        ll_cpass = findViewById(R.id.ll_profedit_cpass);
        iv_profedit = findViewById(R.id.iv_profedit);
        editText_name = findViewById(R.id.editText_profedit_name);
        editText_email = findViewById(R.id.editText_profedit_email);
        editText_phone = findViewById(R.id.editText_profedit_phone);
        editText_pass = findViewById(R.id.editText_profedit_password);
        editText_cpass = findViewById(R.id.editText_profedit_cpassword);

        if (Constants.itemUser.getLoginType().equals(Constants.LOGIN_TYPE_FB) || Constants.itemUser.getLoginType().equals(Constants.LOGIN_TYPE_GOOGLE)) {
            ll_pass.setVisibility(View.GONE);
            ll_cpass.setVisibility(View.GONE);
            editText_email.setEnabled(false);
        }

        LinearLayout ll_adView = findViewById(R.id.ll_adView);
        methods.showBannerAd(ll_adView);

        iv_profedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (methods.checkPer()) {
                    pickImage();
                }
            }
        });

        button_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validate()) {
                    loadUpdateProfile();
                }
            }
        });

        setProfileVar();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private Boolean validate() {
        editText_name.setError(null);
        editText_email.setError(null);
        editText_cpass.setError(null);
        if (editText_name.getText().toString().trim().isEmpty()) {
            Toast.makeText(ProfileEditActivity.this, getResources().getString(R.string.cannot_empty), Toast.LENGTH_SHORT).show();
            editText_name.setError(getString(R.string.cannot_empty));
            editText_name.requestFocus();
            return false;
        } else if (editText_email.getText().toString().trim().isEmpty()) {
            Toast.makeText(ProfileEditActivity.this, getResources().getString(R.string.email_empty), Toast.LENGTH_SHORT).show();
            editText_email.setError(getString(R.string.cannot_empty));
            editText_email.requestFocus();
            return false;
        } else if (editText_pass.getText().toString().endsWith(" ")) {
            Toast.makeText(ProfileEditActivity.this, getResources().getString(R.string.pass_end_space), Toast.LENGTH_SHORT).show();
            editText_pass.setError(getString(R.string.pass_end_space));
            editText_pass.requestFocus();
            return false;
        } else if (!editText_pass.getText().toString().trim().equals(editText_cpass.getText().toString().trim())) {
            Toast.makeText(ProfileEditActivity.this, getResources().getString(R.string.pass_nomatch), Toast.LENGTH_SHORT).show();
            editText_cpass.setError(getString(R.string.pass_nomatch));
            editText_cpass.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    private void updateArray(String profileImage) {
        Constants.itemUser.setName(editText_name.getText().toString());
        Constants.itemUser.setEmail(editText_email.getText().toString());
        Constants.itemUser.setMobile(editText_phone.getText().toString());
        Constants.itemUser.setImage(profileImage);

        if (!editText_pass.getText().toString().equals("")) {
            sharedPref.setRemeber(false);
        }
    }

    private void loadUpdateProfile() {
        if (methods.isConnectingToInternet()) {
            File file = null;
            try {
                if (imageUri != null) {
                    file = new File(methods.getTempUploadPath(imageUri));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            LoadProfileEdit loadProfileEdit = new LoadProfileEdit(new ProfileEditListener() {
                @Override
                public void onStart() {
                    progressDialog.show();
                }

                @Override
                public void onEnd(String profileImage, String success, String registerSuccess, String message) {
                    progressDialog.dismiss();
                    if (success.equals("1")) {
                        if (registerSuccess.equals("1")) {
                            updateArray(profileImage);
                            Constants.isUpdate = true;
                            finish();
                            methods.showToast(message);
                        } else {
                            if (message.contains("Email address already used")) {
                                editText_email.setError(getString(R.string.email_already_regis));
                                editText_email.requestFocus();
                            } else {
                                methods.showToast(message);
                            }
                        }
                    } else {
                        Toast.makeText(ProfileEditActivity.this, getString(R.string.err_server), Toast.LENGTH_SHORT).show();
                    }
                }
            }, methods.getAPIRequest(Constants.METHOD_PROFILE_UPDATE, 0, "", "", "", "", "", "", "", "", "", editText_email.getText().toString(), editText_pass.getText().toString(), editText_name.getText().toString(), editText_phone.getText().toString(), Constants.itemUser.getId(), "", file));
            loadProfileEdit.execute();
        } else {
            Toast.makeText(ProfileEditActivity.this, getResources().getString(R.string.err_internet_not_conn), Toast.LENGTH_SHORT).show();
        }
    }

    public void setProfileVar() {
        editText_name.setText(Constants.itemUser.getName());
        editText_phone.setText(Constants.itemUser.getMobile());
        editText_email.setText(Constants.itemUser.getEmail());

        if (!Constants.itemUser.getImage().equals("")) {
            Picasso.get().load(Constants.itemUser.getImage()).placeholder(R.drawable.placeholder_prof).into(iv_profedit);
        }
    }

    private void pickImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        activityResultLaunch.launch(intent);
    }

    private final ActivityResultLauncher<Intent> activityResultLaunch = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK) {
                if (result.getData() != null && result.getData().getData() != null) {
                    try {
                        imageUri = result.getData().getData();
                        iv_profedit.setImageURI(imageUri);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    });
}
