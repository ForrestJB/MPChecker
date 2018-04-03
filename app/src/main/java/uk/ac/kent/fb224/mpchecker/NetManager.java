package uk.ac.kent.fb224.mpchecker;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;

/**
 * Created by Tree1 on 30/01/2018.
 */

public class NetManager {
    private Context context;
    private static NetManager instance;
    public RequestQueue requestQueue;
    static ArrayList<Constituency> conList = new ArrayList<Constituency>();
    static ArrayList<Constituency> conFavList = new ArrayList<Constituency>();
    static ArrayList<Constituency> DetailsConList = new ArrayList<Constituency>();
    static ArrayList<Bill> BillList = new ArrayList<Bill>();
    static ArrayList<Bill> StaticBillList = new ArrayList<Bill>();
    static ArrayList<Bill> MPDetailsBillList = new ArrayList<Bill>();
    public Boolean isLoaded = false;
    public ImageLoader imageLoader;

    private ImageLoader.ImageCache imageCache = new ImageLoader.ImageCache() {
        private final LruCache<String, Bitmap> cache = new LruCache<String, Bitmap>(20);
        @Override
        public Bitmap getBitmap(String url) {
            return cache.get(url);
        }

        @Override
        public void putBitmap(String url, Bitmap bitmap) {
            cache.put(url, bitmap);
        }
    };
    public NetManager(Context context){
        this.context = context;

        requestQueue = Volley.newRequestQueue(context.getApplicationContext());

        imageLoader = new ImageLoader(requestQueue, imageCache);
    }

    public static NetManager getInstance(Context context){
        if (instance == null){
            instance =  new NetManager(context.getApplicationContext());
        }
        return instance;
    }

}
