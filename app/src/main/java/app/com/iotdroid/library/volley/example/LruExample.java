package app.com.iotdroid.library.volley.example;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import app.com.iotdroid.library.volley.AppControllerUtility;

/**
 * Created by masfajar on 5/23/2016.
 */
public class LruExample extends AppCompatActivity {

    // Load image loader
    private ImageLoader imageLoader = AppControllerUtility.getInstance().getImageLoader();
    private NetworkImageView imageViewProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Image from url path
        /*
        imageViewProfile = (NetworkImageView) findViewById(R.id.imageViewProfile);
        imageLoader.get("http://megaicons.net/static/img/icons_sizes/27/89/64/metroui-folder-os-os-android-icon.png", ImageLoader.getImageListener(imageViewProfile, R.mipmap.ic_launcher, android.R.drawable.ic_dialog_alert));
        imageViewProfile.setImageUrl("http://megaicons.net/static/img/icons_sizes/27/89/64/metroui-folder-os-os-android-icon.png", imageLoader);
        */
    }
}
