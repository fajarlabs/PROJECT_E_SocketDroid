package app.com.iotdroid.module.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.com.iotdroid.R;
import app.com.iotdroid.config.OptionKey;
import app.com.iotdroid.config.URLFormat;
import app.com.iotdroid.library.AlertUtility;
import app.com.iotdroid.library.volley.VolleyMultipartRequestUtility;
import app.com.iotdroid.module.main.sqlite.dao.SwitchLabelDAO;
import app.com.iotdroid.module.main.sqlite.model.SwitchLabelModel;
import app.com.iotdroid.module.setting.LoginSettingActivity;
import app.com.iotdroid.module.setting.SettingActivity;
import app.com.iotdroid.module.setting.sqlite.dao.SettingDAO;
import app.com.iotdroid.module.setting.sqlite.model.SettingModel;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    /* Tag */
    private final String TAG = MainActivity.class.getSimpleName();

    /* Component */
    private ListView listViewSwitch;
    private ProgressDialog progressDialog;
    private AlertUtility alertUtility;
    private List<ListViewDataModel> list;
    private DataModel dataModelChannel,dataModelSensor;
    private SettingDAO settingDAO;
    private SwitchLabelDAO switchLabelDAO;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RequestQueue requestQueue;

    /* Thread for update feed */
    private static Thread threadUpdate = null;
    private static Boolean loop = false;

    /* 7 seconds */
    private int threadTime = 7000;

    /*
      Settingan arguments API ?switch={data}&notif={data}
      Data ini static
     */
    public static final String field_switch = "switch";
    public static final String field_notif = "notif";

    /* Notif flag */
    private static Boolean alert_flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /* Request Queue */
        requestQueue = Volley.newRequestQueue(MainActivity.this);

        /* Setting DAO */
        settingDAO = new SettingDAO(this);
        switchLabelDAO = new SwitchLabelDAO(this);

        /* Alert init */
        alertUtility = new AlertUtility(this);

        /* Data list */
        list = new ArrayList<>();

        /* Progress dialog */
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        /* ListView */
        listViewSwitch = (ListView) findViewById(R.id.listViewSwitch);
        listViewSwitch.setDivider(null);
        listViewSwitch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {}
        });

        listViewSwitch.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                LayoutInflater li = LayoutInflater.from(MainActivity.this);
                View promptsView = li.inflate(R.layout.prompt, null);
                final EditText editTextPromptTitle = (EditText) promptsView.findViewById(R.id.editTextPrompTitle);
                final EditText editTextPromptDescription = (EditText) promptsView.findViewById(R.id.editTextPromptDescription);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);
                // set dialog message
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        /* Update element
                                           Ada kekurangan disini belum disimpan kedalam SQlite
                                           Lanjutan disini */
                                        Map<String, String> search = new HashMap<>();
                                        search.put(SwitchLabelDAO.KEYSTACK,String.valueOf(position));
                                        ArrayList<SwitchLabelModel> arrSwitchLabel = switchLabelDAO.selectData(search);

                                        /* Jika data switch lebih dari 0 maka update data */
                                        if(arrSwitchLabel.size() > 0)
                                            switchLabelDAO.updateData(new SwitchLabelModel(null, position, editTextPromptTitle.getText().toString(), editTextPromptDescription.getText().toString()));
                                        else
                                            switchLabelDAO.insertData(new SwitchLabelModel(null,position,editTextPromptTitle.getText().toString(),editTextPromptDescription.getText().toString()));

                                        ListViewDataModel listViewDataModel = list.get(position);
                                        listViewDataModel.setTitle(editTextPromptTitle.getText().toString());
                                        listViewDataModel.setDescription(editTextPromptDescription.getText().toString());
                                        final ListViewAdapter adapter = new ListViewAdapter(MainActivity.this, list);
                                        listViewSwitch.setAdapter(adapter);
                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                /* create alert dialog */
                AlertDialog alertDialog = alertDialogBuilder.create();

                /* show it */
                alertDialog.show();

                return true;
            }
        });

        /* Swipe refresh layout set */
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);

        /* Add listener swipeRefreshLayout */
        swipeRefreshLayout.setOnRefreshListener(this);

        /* Load first data listview */
        refreshSwitch();

        /* Jika threadUpdate adalah null maka buat thread */
        if(threadUpdate == null) {
            threadUpdate = new Thread(new Runnable() {
                @Override
                public void run() {
                    while(!loop) {

                        try {
                            refreshSwitch();
                        }catch (Exception e) {
                            e.printStackTrace();
                        }

                        try {
                            Thread.sleep(threadTime);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

            /* Thread start */
            threadUpdate.start();
        }

    }

    /**
     * Search token from table setting
     * @param keyTok
     * @return
     */
    private String searchToken(String keyTok) {
        Map<String,String> search = new HashMap<>();
        search.put(SettingDAO.KEYID, keyTok);
        ArrayList<SettingModel> listData = settingDAO.selectData(search);
        String result = null;
        if(listData.size() > 0) {
            for(SettingModel settingModel : listData) {
                if(settingModel.getOptionName().equals(keyTok))
                    result = settingModel.getOptionValue();
            }
        }
        return result;
    }

    /**
     * Refresh switch
     */
    private void refreshSwitch() {
        /*
        Ambil settingan terakhir dari channel
         */
        String tokenChannel = searchToken(OptionKey.KEY_TOK_CHANNEL);

        /* field last feed */
        String URLLastFeedSwitch = URLFormat.lastFeedFajarlabs(tokenChannel, MainActivity.field_switch);

        /* field notif */
        String URLLastFeedNotif = URLFormat.lastFeedFajarlabs(tokenChannel, MainActivity.field_notif);

        /* Synchronize */
        syncAgnosthingData(URLLastFeedSwitch,URLLastFeedNotif);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent i = new Intent(getApplicationContext(), LoginSettingActivity.class);
            startActivity(i);
            return true;
        }

        if(id == R.id.other_settings) {
            progressDialog.show();
            switchLabelDAO.deleteData(null);
            refreshSwitch();
            return true;
        }

        if(id == R.id.quit) {
            this.finishAffinity();
        }

        if(id == R.id.action_refresh) {
            progressDialog.show();
            refreshSwitch();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        refreshSwitch();
    }

    /* Adapater for listView */
    private class ListViewAdapter extends ArrayAdapter<ListViewDataModel> {

        private Activity context;
        private List<ListViewDataModel> listData;

        public ListViewAdapter(Activity context,List<ListViewDataModel> listData) {
            super(context, R.layout.listview_item_detail,listData);

            /* Set data in constructor */
            this.context = context;
            this.listData = listData;
        }

        @Override
        public View getView(final int position, View view, ViewGroup parent) {
            LayoutInflater inflater = context.getLayoutInflater();
            View rowView= inflater.inflate(R.layout.listview_item_detail, null, true);

            /* Set UI Title */
            TextView textViewTitle = (TextView) rowView.findViewById(R.id.textViewTitle);
            textViewTitle.setText(listData.get(position).getTitle());
            TextView textViewDescription = (TextView) rowView.findViewById(R.id.textViewDescription);
            textViewDescription.setText(listData.get(position).getDescription());
            LinearLayout linearLayoutList = (LinearLayout) rowView.findViewById(R.id.LinearLayoutList);

            /*
            Periksa channel jika statusnya On maka lampu berwarna putih dan jika statusnya Off maka lampu berwarna hitam
            */
            final ImageButton imageButton = (ImageButton) rowView.findViewById(R.id.imageButtonLamp);
            Boolean switch_status = listData.get(position).getCheckedSensor() == null ? false : listData.get(position).getCheckedSensor() ;
            if(switch_status)
                imageButton.setImageResource(R.drawable.lightbulb_on);
            else
                imageButton.setImageResource(R.drawable.lightbulb_off);

            /* Periksa posisi channel beri warna merah jika kondisi off dan beri warna hijau untuk kondisi on*/
            if(listData.get(position).getCheckedChannel())
                linearLayoutList.setBackgroundResource(R.drawable.rounded_red);
            else
                linearLayoutList.setBackgroundResource(R.drawable.rounded_green);


            /* Set label */
            Map<String, String> search = new HashMap<>();
            search.put(SwitchLabelDAO.KEYSTACK,String.valueOf(position));
            ArrayList<SwitchLabelModel> arrSwitchLabel = switchLabelDAO.selectData(search);
            if(arrSwitchLabel.size() > 0) {
                for(SwitchLabelModel switchLabelModel : arrSwitchLabel) {
                    textViewTitle.setText(switchLabelModel.getTitle());
                    textViewDescription.setText(switchLabelModel.getDescription());
                }
            }

            return rowView;
        }
    }

    /* Update query agnosthing */
    private void updateQueryAgnosthing(String data) {
        progressDialog.show();

        /* Update channel */
        String channel = searchToken(OptionKey.KEY_TOK_CHANNEL);
        String urlUpdateChannel = URLFormat.updateFeedFajarlabs(channel,MainActivity.field_switch+"="+data);

        /*Log.e(TAG,"Update Channel "+urlUpdateChannel);*/

        VolleyMultipartRequestUtility reqUpdateChannel = new VolleyMultipartRequestUtility(Request.Method.GET, urlUpdateChannel,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse networkResponse) {
                        String s = new String(networkResponse.data);
                        refreshSwitch();
                        alert_flag = false;
                        if(progressDialog.isShowing()) progressDialog.dismiss();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        /* Hentikan icon loading refresh */
                        if(alert_flag == false) {
                            alertUtility.alertYes("Maaf, tidak ada respon dari server !", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // do something bro..
                                    dialog.cancel();
                                }
                            });
                            alert_flag = true;
                        }

                        /* Jika kondisinya tampil maka sembunyikan */
                        if(progressDialog.isShowing())
                            progressDialog.dismiss();
                    }
                }){

            /* Lakukan sesuatu disini */

        };

        /* Socket timeout after 30 seconds */
        int socketTimeout = 30000;

        /* Using policy */
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        reqUpdateChannel.setRetryPolicy(policy);

        /* Update channel */
        requestQueue.add(reqUpdateChannel);
    }

    /**
     * Cari posisi data dan update data terbaru
     * @param position
     * @param str_change
     * @return
     */
    private String findDataPosition(int position,String str_change) {
        String[] array = dataModelChannel.getValue().split("\\-", -1);
        // data
        String data = array[position];
        // Change
        array[position] = str_change;

        String strQuery = "";
        int length = array.length - 1;
        for(int i = 0; i < array.length; i++) {
            String separator = "";
            if(length != i ) {
                separator = "-";
            }
            strQuery += array[i]+separator;
        }

        dataModelChannel.setValue(strQuery);

        return strQuery;
    }

    /**
     * Channel request
     * @param url
     */
    private void channelRequest(final Map<String,String> url) {
        VolleyMultipartRequestUtility requestSwitch = new VolleyMultipartRequestUtility(Request.Method.GET, url.get("last_feed_channel"),
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse networkResponse) {
                        String s = new String(networkResponse.data);
                        try {
                            Log.d(TAG, s);
                            /* Decode data json */
                            JSONObject jsonObject = new JSONObject(s);
                            dataModelChannel = new DataModel(jsonObject.getString("value"),jsonObject.getInt("code"));
                            String[] array = dataModelChannel.getValue().split("\\-", -1);

                            int i = 1;
                            /* Bersihkan stack data */
                            list.clear();
                            for(String str : array) {
                                String concat = "";
                                Boolean status = false;

                                if(str.equals("1")) {
                                    concat += "Turun";
                                    status = true;
                                }

                                if(str.equals("0")) {
                                    concat += "Naik";
                                    status = false;
                                }

                                /* Add to array list */
                                list.add(new ListViewDataModel("Saklar "+i, concat,status,null));

                                i++;
                            }

                            alert_flag = false;

                            /* Update informasi sensor */
                            sensorRequest(url.get("last_feed_sensor"),list);

                            /* Hilangkan icon loading refresh */
                            swipeRefreshLayout.setRefreshing(false);
                        } catch (JSONException e) {

                            /* Hentikan icon loading refresh */
                            swipeRefreshLayout.setRefreshing(false);

                            /* Alert using flag */
                            if(alert_flag == false) {
                                alertUtility.alertYes("Maaf, ada kesalahan data !", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                                alert_flag = true;
                            }
                            /*Log.e(TAG, e.toString());*/
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        /* Hentikan icon loading refresh */
                        swipeRefreshLayout.setRefreshing(false);
                        alertUtility.alertYes("Maaf, tidak ada respon dari server !", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        if(progressDialog.isShowing()) progressDialog.dismiss();
                        /* Log.e(TAG, volleyError.toString()); */
                    }
                }){
            /* Do anything here */
        };

        /* Thread after 30 seconds */
        int socketTimeout = 30000;

        /* Policy */
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        requestSwitch.setRetryPolicy(policy);

        requestQueue.add(requestSwitch);
    }

    /**
     * Request sensor
     * @param url
     * @param list
     */
    private void sensorRequest(String url,final List<ListViewDataModel> list) {
        VolleyMultipartRequestUtility requestSensor = new VolleyMultipartRequestUtility(Request.Method.GET, url,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse networkResponse) {
                        String s = new String(networkResponse.data);
                        if(progressDialog.isShowing()) progressDialog.dismiss();
                        try {
                            Log.e(TAG, s);

                            /* Decode JSON */
                            JSONObject jsonObject = new JSONObject(s);
                            dataModelSensor = new DataModel(jsonObject.getString("value"),jsonObject.getInt("code"));
                            String[] array = dataModelSensor.getValue().split("\\-", -1);

                            /* Komparasi, periksa apakah jumlah channel sama dengan jumlah sensor ?
                               Jika sama update light-bulb */
                            if(array.length == list.size()) {
                                /* Update On Item pada saklarnya */
                                int i = 0;
                                for(String str : array) {
                                    Boolean checked = str.equals("0") ? false : true ;
                                    list.get(i).setCheckedSensor(checked);
                                    i++;
                                }
                                final ListViewAdapter adapter = new ListViewAdapter(MainActivity.this, list);
                                listViewSwitch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        /*Boolean currentPos = list.get(position).getCheckedChannel();*/
                                        String conChannel = "";
                                        int size = list.size() - 1;

                                        /* Komparasi antrian tumpukan */
                                        int i = 0;
                                        for(ListViewDataModel channel : list) {
                                            String cond = channel.getCheckedChannel() == true ? "1" : "0";
                                            /* Flip value */
                                            if(i == position) {
                                                if(cond.equals("1"))
                                                    cond = "0";
                                                else
                                                    cond = "1";
                                            }

                                            String coma = size != i ? "-" : "";
                                            conChannel+= cond+coma;

                                            i++;
                                        }

                                        /* Update channel disini */
                                        updateQueryAgnosthing(conChannel);
                                        /*Log.e(TAG,"concat => "+conChannel);*/
                                    }
                                });
                                listViewSwitch.setAdapter(adapter);
                            }

                            if(progressDialog.isShowing())
                                progressDialog.dismiss();
                        } catch (Exception e) {}
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        alertUtility.alertYes("Maaf, sensor tidak terdeteksi. Silahkan segarkan kembali !", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        Log.e(TAG, volleyError.toString());
                    }
                }){
            /* Do anything */
        };
        /* 30 seconds - change to what you want */
        int socketTimeout = 30000;

        /* Policy */
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        requestSensor.setRetryPolicy(policy);

        requestQueue.add(requestSensor);
    }

    /**
     * Sinkronisasi data dari server ke handheld
     * @param URLLastFeedChannel
     * @param  URLLastFeedSensor
     */
    private void syncAgnosthingData(String URLLastFeedChannel,String URLLastFeedSensor) {
        /* Masukan data url kedalam array list */
        Map<String,String> url = new HashMap<>();
        url.put("last_feed_channel",URLLastFeedChannel);
        url.put("last_feed_sensor",URLLastFeedSensor);

        /* Channel request */
        channelRequest(url);
    }

    /* Data Model Internet */
    private class DataModel {
        private String value;
        private int code;

        public DataModel(String value, int code) {
            this.value = value;
            this.code = code;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }
    }

    /* Data Model ListView */
    private class ListViewDataModel {
        private String title;
        private String description;
        private Boolean checkedChannel;
        private Boolean checkedSensor;

        public ListViewDataModel(String title, String description, Boolean checkedChannel, Boolean checkedSensor) {
            this.title = title;
            this.description = description;
            this.checkedChannel = checkedChannel;
            this.checkedSensor = checkedSensor;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Boolean getCheckedChannel() {
            return checkedChannel;
        }

        public void setCheckedChannel(Boolean checkedChannel) {
            this.checkedChannel = checkedChannel;
        }

        public Boolean getCheckedSensor() {
            return checkedSensor;
        }

        public void setCheckedSensor(Boolean checkedSensor) {
            this.checkedSensor = checkedSensor;
        }
    }
}
