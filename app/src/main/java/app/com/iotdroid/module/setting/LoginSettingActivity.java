package app.com.iotdroid.module.setting;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import app.com.iotdroid.R;
import app.com.iotdroid.library.AlertUtility;

public class LoginSettingActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnAccessSetting;
    private EditText editTextLoginSetting;
    private AlertUtility alertUtility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_setting);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        getSupportActionBar().setTitle("Access Setting");

        // Alert
        alertUtility = new AlertUtility(this);

        btnAccessSetting = (Button) findViewById(R.id.btnAccessSetting);
        btnAccessSetting.setOnClickListener(this);

        editTextLoginSetting = (EditText) findViewById(R.id.editTextLoginSetting);
    }

    @Override
    public void onClick(View v) {
        if(v == btnAccessSetting) {
            String password = editTextLoginSetting.getText().toString();
            if(password.equals("12345")) {
                editTextLoginSetting.setText("");
                Intent i = new Intent(getApplicationContext(), SettingActivity.class);
                startActivity(i);
            } else {
                alertUtility.alertYes("Login failed!", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // do something bro..
                        dialog.cancel();
                    }
                });
            }

        }
    }
}
