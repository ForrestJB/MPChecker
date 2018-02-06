package uk.ac.kent.fb224.mpchecker;

import android.content.Context;
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
    public NetManager(Context context){
        this.context = context;

        requestQueue = Volley.newRequestQueue(context.getApplicationContext());
    }

    public static NetManager getInstance(Context context){
        if (instance == null){
            instance =  new NetManager(context.getApplicationContext());
        }
        return instance;
    }

}
