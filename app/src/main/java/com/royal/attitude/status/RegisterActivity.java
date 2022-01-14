package com.royal.attitude.status;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.royal.attitude.status.asyncTask.LoadRegister;
import com.royal.attitude.status.interfaces.SocialLoginListener;
import com.royal.attitude.status.utils.Constants;
import com.royal.attitude.status.utils.Methods;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.File;

public class RegisterActivity extends AppCompatActivity {

    Methods methods;
    RoundedImageView iv_regis_propic;
    EditText editText_name, editText_email, editText_pass, editText_cpass, editText_phone;
    TextView textView_signin;
    Button button_register;
    ProgressDialog progressDialog;
    Uri imageUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        methods = new Methods(this);
        methods.setStatusColor(getWindow());
        methods.forceRTLIfSupported(getWindow());

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.registering));
        progressDialog.setCancelable(false);

        iv_regis_propic = findViewById(R.id.iv_regis_propic);
        textView_signin = findViewById(R.id.tv_regis_signin);
        button_register = findViewById(R.id.button_register);
        editText_name = findViewById(R.id.et_regis_name);
        editText_email = findViewById(R.id.et_regis_email);
        editText_pass = findViewById(R.id.et_regis_password);
        editText_cpass = findViewById(R.id.et_regis_cpassword);
        editText_phone = findViewById(R.id.et_regis_phone);

        TextView tv_welcome = findViewById(R.id.tv);

        tv_welcome.setTypeface(tv_welcome.getTypeface(), Typeface.BOLD);
        textView_signin.setTypeface(textView_signin.getTypeface(), Typeface.BOLD);
        button_register.setTypeface(button_register.getTypeface(), Typeface.BOLD);

        iv_regis_propic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (methods.checkPer()) {
                    pickImage();
                }
            }
        });

        button_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validate()) {
                    if (methods.isConnectingToInternet()) {
                        loadRegister();
                    } else {
                        Toast.makeText(RegisterActivity.this, getResources().getString(R.string.err_internet_not_conn), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        textView_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private Boolean validate() {
        if (editText_name.getText().toString().trim().isEmpty()) {
            editText_name.setError(getResources().getString(R.string.enter_name));
            editText_name.requestFocus();
            return false;
        } else if (editText_email.getText().toString().trim().isEmpty()) {
            editText_email.setError(getResources().getString(R.string.enter_email));
            editText_email.requestFocus();
            return false;
        } else if (!isEmailValid(editText_email.getText().toString())) {
            editText_email.setError(getString(R.string.error_invalid_email));
            editText_email.requestFocus();
            return false;
        } else if (editText_pass.getText().toString().isEmpty()) {
            editText_pass.setError(getResources().getString(R.string.enter_password));
            editText_pass.requestFocus();
            return false;
        } else if (editText_pass.getText().toString().endsWith(" ")) {
            editText_pass.setError(getResources().getString(R.string.pass_end_space));
            editText_pass.requestFocus();
            return false;
        } else if (editText_cpass.getText().toString().isEmpty()) {
            editText_cpass.setError(getResources().getString(R.string.enter_cpassword));
            editText_cpass.requestFocus();
            return false;
        } else if (!editText_pass.getText().toString().equals(editText_cpass.getText().toString())) {
            editText_cpass.setError(getResources().getString(R.string.pass_nomatch));
            editText_cpass.requestFocus();
            return false;
        } else if (editText_phone.getText().toString().trim().isEmpty()) {
            editText_phone.setError(getResources().getString(R.string.enter_phone));
            editText_phone.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@") && !email.contains(" ");
    }

    private void loadRegister() {
        if (methods.isConnectingToInternet()) {
            File file = null;
            try {
                if (imageUri != null) {
                    file = new File(methods.getTempUploadPath(imageUri));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            LoadRegister loadRegister = new LoadRegister(new SocialLoginListener() {
                @Override
                public void onStart() {
                    progressDialog.show();
                }

                @Override
                public void onEnd(String success, String registerSuccess, String message, String user_id) {
                    progressDialog.dismiss();
                    if (success.equals("1")) {
                        switch (registerSuccess) {
                            case "1":
                                Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.putExtra("from", "");
                                startActivity(intent);
                                finish();
                                break;
                            case "-1":
                                methods.getVerifyDialog(getString(R.string.error_unauth_access), message);
                                break;
                            default:
                                    if (message.contains("already") || message.contains("Invalid email format")) {
                                        editText_email.setError(getResources().getString(R.string.email_already_regis));
                                        editText_email.requestFocus();
                                    } else {
                                        methods.showToast(message);
                                    }
                                break;
                        }
                    } else {
                        Toast.makeText(RegisterActivity.this, getString(R.string.err_server), Toast.LENGTH_SHORT).show();
                    }
                }
            }, methods.getAPIRequest(Constants.METHOD_REGISTER, 0,"", "", "", "", "normal", "", "", "", "", editText_email.getText().toString(), editText_pass.getText().toString(), editText_name.getText().toString(), editText_phone.getText().toString(), "", "",file));
            loadRegister.execute();
        } else {
            methods.showToast(getString(R.string.err_internet_not_conn));
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
                        iv_regis_propic.setImageURI(imageUri);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    });
}