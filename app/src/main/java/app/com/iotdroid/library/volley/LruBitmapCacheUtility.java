package app.com.iotdroid.library.volley;

import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.toolbox.ImageLoader;

/**
 * Created by spasi on 27/01/2016.
 */
public class LruBitmapCacheUtility extends LruCache<String, Bitmap> implements ImageLoader.ImageCache{
    public static int getDefaultLruCacheSize() {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 20240);
        final int cacheSize = maxMemory / 64;

        return cacheSize;
    }

    // Ukuran LruCache default
    public LruBitmapCacheUtility() {
        this(getDefaultLruCacheSize());
    }

    // Ukuran LruCache settingan sendiri
    public LruBitmapCacheUtility(int sizeInKiloBytes) {
        super(sizeInKiloBytes);
    }

    @Override
    protected int sizeOf(String key, Bitmap value) {
        return value.getRowBytes() * value.getHeight() / 1024;
    }

    @Override
    public Bitmap getBitmap(String url) {
        return get(url);
    }

    @Override
    public void putBitmap(String url, Bitmap bitmap) {
        put(url, bitmap);
    }
}
