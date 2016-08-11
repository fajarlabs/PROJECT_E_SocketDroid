package app.com.iotdroid.library.volley.example;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by masfajar on 5/20/2016.
 * Contoh selengkapnya di http://www.itsalif.info/content/android-volley-tutorial-http-get-post-put
 */


public class VolleyExample {

    private Context context;

    /**
     * Constructor
     * @param context
     */
    public VolleyExample(Context context) {
        // Init context
        this.context = context;
    }

    // Contoh untuk request data melalui metode GET
    public void usingGet() {

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(context);
        String url ="http://agnosthings.com/88bcc76e-117c-11e6-8001-005056805279/field/last/feed/255/switch";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    // Display the first 500 characters of the response string.
                    // Your code on response
                }
            }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Error Code
                error.printStackTrace();
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    // Contoh melakukan request data dengan metode POST
    public void usingPost() {

        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "http://api.someservice.com/post/comment";

        StringRequest stringRequest = new StringRequest(Request.Method.POST,url,
                new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Your code, after complete
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Your code, if error
            }
        }){
            @Override
            protected Map<String,String> getParams(){

                // Add parameter here
                Map<String,String> params = new HashMap<String, String>();
                params.put("parameter_1","value_1");
                params.put("parameter_2","value_2");

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                // Header
                Map<String,String> params = new HashMap<String, String>();
                // Type header
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }
        };

        // Add to queue
        queue.add(stringRequest);
    }
}
