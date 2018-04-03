package uk.ac.kent.fb224.mpchecker;
//guardian api url https://content.guardianapis.com/search?format=json&tag=politics/politics&api-key=e1b65d8f-a5df-49fc-9cf1-9a03e7128a29&page-size=50

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Tree1 on 01/03/2018.
 */

public class LandingActivity extends AppCompatActivity {
    private TextView MPName;
    private TextView MPRole;
    private TextView MPCon;
    private ConstraintLayout mask;
    private ConstraintLayout content;
    private DatabaseReference mMPReference;
    private Constituency UserMP;
    private ArrayList<News> NewsList = new ArrayList<>();
    private NewsListAdapter adapter;
    private LinearLayoutManager layoutManager;
    private RecyclerView NewsRView;
    private ImageView MPImage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.landing_menu);

        mask = findViewById(R.id.LandingMaskContainer);
        content = findViewById(R.id.LandingContentCont);
        MPName = findViewById(R.id.LandMPName);
        MPRole = findViewById(R.id.LandMPRole);
        MPCon = findViewById(R.id.LandMPCon);
        MPImage = findViewById(R.id.LandMPImage);

        if(NetManager.getInstance(LandingActivity.this).isLoaded==false){//if the list of MP's has not been loaded yet
            for(int k=0;k<=649;k++) {
                mMPReference = FirebaseDatabase.getInstance().getReference().child("Raw Data").child("MP").child(Integer.toString(k));
                ValueEventListener MPListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final Constituency NewCon = dataSnapshot.getValue(Constituency.class);
                        NetManager.getInstance(LandingActivity.this).conList.add(NewCon);
                        if (NetManager.getInstance(LandingActivity.this).conList.size()==649){//this serves as a listener, to remove the loading page once the MP List has been loaded
                            NetManager.getInstance(LandingActivity.this).isLoaded = true;
                            startManager();
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                };
                mMPReference.addValueEventListener(MPListener);
            }
        }else{startManager();}


    }
    private void startManager(){//this class checks to see if the user has already set their MP
        SharedPreferences sharedPreferences = getSharedPreferences("Main_Pref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Boolean isFirst = sharedPreferences.getBoolean("First_Open", true);
        String FoundMP = sharedPreferences.getString("User_MP", "error");
        Log.d("found mp", FoundMP);
        if(isFirst==true){//if the app has not been opened on this device before
            Intent intent = new Intent(getApplicationContext(), FindActivityPostcode.class);
            startActivity(intent);
            finish();
        }else{//else retrieve the MP the user set
//            String FoundMP = sharedPreferences.getString("User_MP", "error");
            if(FoundMP.equals("error")){
                Toast.makeText(getApplicationContext(), "Something hs gone very, very wrong here....", Toast.LENGTH_LONG).show();
            }else {
                for(int i=0;i<NetManager.getInstance(getApplicationContext()).conList.size();i++){
                    if(i==121){}else {
                        Constituency con = NetManager.getInstance(getApplicationContext()).conList.get(i);
                        if (con.MPName.equals(FoundMP)) {
                            UserMP = con;
                        }
                    }
                }
                MPName.setText(UserMP.MPName);
                MPRole.setText(UserMP.MPRole);
                MPCon.setText(UserMP.ConName);
                if (UserMP.MPImageUrl != null) {//check to ensure there is an image to prevent crashes if the URL is null
                    NetManager.getInstance(getApplicationContext()).imageLoader.get(UserMP.MPImageUrl, imageListener1);//setup NetManager object, fetch MP image
                }
                NewsRView = findViewById(R.id.NewsRView);
                layoutManager = new LinearLayoutManager(this);
                layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                NewsRView.setLayoutManager(layoutManager);
                adapter = new NewsListAdapter();
                NewsRView.setAdapter(adapter);
                GetNews();
            }
        }
    }
    private ImageLoader.ImageListener imageListener1 = new ImageLoader.ImageListener() {
        @Override
        public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {//
            if (response.getBitmap() !=null){
                MPImage.setImageBitmap(response.getBitmap());
            }
        }

        @Override
        public void onErrorResponse(VolleyError error) {
            Toast.makeText(getApplicationContext(), "Network error, please check your internet connection", Toast.LENGTH_LONG).show();
        }
    };
    private void GetNews(){
        Log.d("error", "called GetNews");
        String URL = "https://content.guardianapis.com/search?format=json&tag=politics/politics&api-key=e1b65d8f-a5df-49fc-9cf1-9a03e7128a29&page-size=30";
        NetManager NetMgr = NetManager.getInstance(getApplicationContext());
        RequestQueue requestQueue = NetMgr.requestQueue;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject res = response.getJSONObject("response");
                    JSONArray results = res.getJSONArray("results");
                    for(int i=0;i<results.length();i++){
                        final News NewNews = new News();
                        JSONObject result = results.getJSONObject(i);
                        NewNews.Date = result.getString("webPublicationDate");
                        NewNews.Name = result.getString("webTitle");
                        NewNews.Category = result.getString("pillarName");
                        NewsList.add(NewNews);
                    }
                    adapter.NewsList = NewsList;
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("error", "news json error");
            }
        }); requestQueue.add(request);
        mask.setVisibility(View.GONE);
        content.setVisibility(View.VISIBLE);
    }
}