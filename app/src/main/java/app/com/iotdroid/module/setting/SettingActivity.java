package app.com.iotdroid.module.setting;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import app.com.iotdroid.R;
import app.com.iotdroid.config.URLFormat;
import app.com.iotdroid.module.setting.sqlite.dao.SettingDAO;
import app.com.iotdroid.config.OptionKey;
import app.com.iotdroid.module.setting.sqlite.model.SettingModel;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editTextTokenChannel;
    private Button btnSimpan;

    private SettingDAO settingDAO;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option);
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
        getSupportActionBar().setTitle("Setting");

        // Progress Dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);

        // Setting DAO
        settingDAO = new SettingDAO(this);

        // Token Channel
        editTextTokenChannel = (EditText) findViewById(R.id.editTextTokenChannel);

        // Button exec
        btnSimpan = (Button) findViewById(R.id.btnSimpan);
        btnSimpan.setOnClickListener(this);

        progressDialog.show();
        new SearchTokenAsyncTask().execute();
    }

    /**
     * Search token
     * @param keyTok
     * @return
     */
    private String searchToken(String keyTok) {
        // Ambil token sensor
        Map<String,String> searchChannel = new HashMap<>();
        searchChannel.put(SettingDAO.KEYID, keyTok);
        ArrayList<SettingModel> listDataChannel = settingDAO.selectData(searchChannel);
        String result = null;
        if(listDataChannel.size() > 0) {
            for(SettingModel settingModel : listDataChannel)
                result = settingModel.getOptionValue();
        }
        return result;
    }

    /**
     * Update token channel
     */
    private void updateToken(String keyTok,String value) {
        Map<String,String> search = new HashMap<>();
        search.put(SettingDAO.KEYID, keyTok);
        ArrayList<SettingModel> listData = settingDAO.selectData(search);
        if(listData.size() > 0)
            settingDAO.updateData(new SettingModel(keyTok,value));
        else
            settingDAO.insertData(new SettingModel(keyTok,value));
    }

    @Override
    public void onClick(View v) {
        if(v == btnSimpan) {
            progressDialog.show();
            ArrayList<String> data = new ArrayList<>();
            data.add(editTextTokenChannel.getText().toString());
            new UpdateTokenAsyncTask().execute(data);
        }
    }

    /**
     * Class for UpdateTokenAsyncTask
     */
    private class UpdateTokenAsyncTask extends  AsyncTask<ArrayList<String>,Void,ArrayList<String>> {

        @Override
        protected ArrayList<String> doInBackground(ArrayList<String>... params) {
            // Update channel
            updateToken(OptionKey.KEY_TOK_CHANNEL, params[0].get(0));
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<String> result) {
            if(progressDialog.isShowing()) progressDialog.dismiss();
        }
    }

    /**
     * Private class for searchTokenAsyncTask
     */
    private class SearchTokenAsyncTask extends AsyncTask<String,Void,String> {
        String tokenChannel,channelId,tokenSensor,sensorId;
        @Override
        protected String doInBackground(String... params) {
            // Search token
            tokenChannel = searchToken(OptionKey.KEY_TOK_CHANNEL);
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            // Update UI
            if(progressDialog.isShowing()) progressDialog.dismiss();
            // Update channel
            editTextTokenChannel.setText(tokenChannel);
        }
    }
}
