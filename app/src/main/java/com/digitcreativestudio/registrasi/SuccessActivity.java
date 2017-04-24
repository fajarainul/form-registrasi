package com.digitcreativestudio.registrasi;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class SuccessActivity extends AppCompatActivity {
    public static final String KEY_REGISTRASI = "registration";
    public static final String KEY_NAME = "name";
    public static final String KEY_LICENSE = "license";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);

        Bundle args = getIntent().getExtras();

        ((TextView) findViewById(R.id.success_no_registration))
                .setText(args.getString(KEY_REGISTRASI));
        ((TextView) findViewById(R.id.success_name))
                .setText(args.getString(KEY_NAME));
        ((TextView) findViewById(R.id.success_license_name))
                .setText(args.getString(KEY_LICENSE));
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, WebActivity.class));
        finish();
    }
}
