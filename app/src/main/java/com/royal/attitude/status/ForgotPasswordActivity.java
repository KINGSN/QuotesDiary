package com.royal.attitude.status;

import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.royal.attitude.status.asyncTask.LoadForgotPass;
import com.royal.attitude.status.interfaces.SuccessListener;
import com.royal.attitude.status.utils.Constants;
import com.royal.attitude.status.utils.Methods;

public class ForgotPasswordActivity extends AppCompatActivity {

    Toolbar toolbar;
    Methods methods;
    Button button_send;
    EditText editText_email;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpass);

        toolbar = findViewById(R.id.toolbar_forgostpass);

        methods = new Methods(this);
        methods.forceRTLIfSupported(getWindow());
        methods.setStatusColor(getWindow());

        toolbar.setTitle(getString(R.string.forgot_pass));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog = new ProgressDialog(ForgotPasswordActivity.this);
        progressDialog.setMessage(getString(R.string.loading));

        TextView tv = findViewById(R.id.tv);
        TextView tv1 = findViewById(R.id.tv1);
        button_send = findViewById(R.id.button_forgot_send);
        editText_email = findViewById(R.id.et_forgot_email);

        tv.setTypeface(tv.getTypeface(), Typeface.BOLD);
        tv1.setTypeface(tv1.getTypeface(), Typeface.BOLD);
        button_send.setTypeface(button_send.getTypeface(), Typeface.BOLD);

        button_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (methods.isConnectingToInternet()) {
                    if (!editText_email.getText().toString().trim().isEmpty()) {
                        loadForgotPass(editText_email.getText().toString());
                    } else {
                        Toast.makeText(ForgotPasswordActivity.this, getString(R.string.enter_email), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ForgotPasswordActivity.this, getString(R.string.err_internet_not_conn), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;

            default:
                return super.onOptionsItemSelected(menuItem);
        }
        return true;
    }

    private void loadForgotPass(String email) {
        LoadForgotPass loadForgotPass = new LoadForgotPass(new SuccessListener() {
            @Override
            public void onStart() {
                progressDialog.show();
            }

            @Override
            public void onEnd(String success, String registerSuccess, String message) {
                progressDialog.dismiss();
                if (success.equals("1")) {
                    methods.showToast(message);
                } else {
                    Toast.makeText(ForgotPasswordActivity.this, getString(R.string.err_server), Toast.LENGTH_SHORT).show();
                }
            }
        }, methods.getAPIRequest(Constants.METHOD_FORGOT_PASS, 0, "", "", "", "", "", "", "", "", "", email, "", "", "", "", "",null));
        loadForgotPass.execute();
    }
}