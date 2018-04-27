package uk.ac.kent.fb224.mpchecker;
//guardian api url https://content.guardianapis.com/search?format=json&tag=politics/politics&api-key=e1b65d8f-a5df-49fc-9cf1-9a03e7128a29&page-size=50

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Scene;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

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

public class LandingActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private ViewGroup YourMp;
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
    private ActionBarDrawerToggle toggle;
    private android.support.v7.widget.Toolbar toolbar;
    private Boolean BillsLoaded = false;
    private int BillCounter = 0;
    private int errorCount = 0;
    private boolean error1shown = false;
    private boolean error2shown = false;
    private boolean error3shown = false;
    private Scene LandingScene;
    private Scene MPScene;
    private ViewGroup SceneRoot;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.landing_menu);

        mMPReference = FirebaseDatabase.getInstance().getReference();
        mask = findViewById(R.id.LandingMaskContainer);
        content = findViewById(R.id.LandingContentCont);
        MPName = findViewById(R.id.LandMPName);
        MPRole = findViewById(R.id.LandMPRole);
        MPCon = findViewById(R.id.LandMPCon);
        MPImage = findViewById(R.id.LandMPImage);
        toolbar = findViewById(R.id.toolbar);
        YourMp = findViewById(R.id.YourMPLayout);

        SceneRoot = (ViewGroup) findViewById(R.id.drawer_layout);
        LandingScene = Scene.getSceneForLayout(SceneRoot, R.layout.landing_menu, this);
        MPScene = Scene.getSceneForLayout(SceneRoot, R.layout.mp_layout, this);

        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toggle = new ActionBarDrawerToggle(
                this, drawer, R.string.navigation_bar_open, R.string.navigation_bar_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NewsRView = findViewById(R.id.NewsRView);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        NewsRView.setLayoutManager(layoutManager);
        adapter = new NewsListAdapter();
        NewsRView.setAdapter(adapter);
        adapter.NewsList = NetManager.getInstance(LandingActivity.this).NewsList;

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        for(int i=0;i<50;i++){//initialise the bill lists with null objects, these will be overwritten during loading
            Bill tempbill = null;
            NetManager.getInstance(this).BillList.add(tempbill);
            NetManager.getInstance(this).StaticBillList.add(tempbill);
        }
        if(NetManager.getInstance(LandingActivity.this).isLoaded==false){//if the list of MP's has not been loaded yet
            for(int k=0;k<=649;k++) {
                mMPReference = FirebaseDatabase.getInstance().getReference().child("Raw Data").child("MP").child(Integer.toString(k));
                ValueEventListener MPListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        final Constituency NewCon = dataSnapshot.getValue(Constituency.class);

                        if(NewCon != null && NewCon.Party.equals("Labour/Co-operative")){
                            NewCon.Party = "Labour";
                        }
                        if(NewCon != null && NewCon.MPImageUrl == null){
                            NewCon.MPImageUrl = "placeholder";
                        }
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


    private void GetBills(){
        NetManager NetMgr = NetManager.getInstance(getApplicationContext());
        RequestQueue requestQueue = NetMgr.requestQueue;
        String BillURL = "http://lda.data.parliament.uk/commonsdivisions.json?_view=Commons+Divisions&_pageSize=50&_page=0";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, BillURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject result = response.getJSONObject("result");
                    JSONArray items = result.getJSONArray("items");
                    for(int i=0; i < items.length(); i++){
                        JSONObject vote = items.getJSONObject(i);

                        String VoteURL = vote.getString("_about");
                        String tempURL = VoteURL.substring(36);
                        GetVoteResult(tempURL, i);//this takes the id number from the url provided, and passes it a method to fetch the appropriate information
                    }                             //the iteration number is also passed as this is used to ensure the list is sorted correctly
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                errorCount++;
                GetBills();
                if(errorCount >= 3 && error1shown == false){
                    Toast.makeText(getApplicationContext(),"Error retrieving data from Parliament API, please try again later.", Toast.LENGTH_LONG).show();
                    error1shown = true;
                }
            }
        });
        requestQueue.add(request);
    }
    public void GetVoteResult (final String id, final int index){
        NetManager NetMgr = NetManager.getInstance(getApplicationContext());
        RequestQueue requestQueue = NetMgr.requestQueue;//fetch the request queue
        final String URL = "http://lda.data.parliament.uk/commonsdivisions/id/"+id+".json";
        JsonObjectRequest request2 = new JsonObjectRequest(Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                final Bill NewBill = new Bill();
                NewBill.ID = id;
                try {
                    JSONObject result = response.getJSONObject("result");
                    JSONObject PrimTopic = result.getJSONObject("primaryTopic");
                    JSONArray AbstainArray = PrimTopic.getJSONArray("AbstainCount");
                    int AbstainCount = 0;
                    for (int i = 0; i < AbstainArray.length(); i++) {
                        JSONObject AbCont = AbstainArray.getJSONObject(i);
                        AbstainCount = AbCont.getInt("_value");
                    }
                    JSONArray AyeArray = PrimTopic.getJSONArray("AyesCount");
                    int AyeCount = 0;
                    for (int i = 0; i < AyeArray.length(); i++) {
                        JSONObject AyeCont = AyeArray.getJSONObject(i);
                        AyeCount = AyeCont.getInt("_value");
                    }
                    JSONArray NoeArray = PrimTopic.getJSONArray("Noesvotecount");
                    int NoeCount = 0;
                    for (int i = 0; i < NoeArray.length(); i++) {
                        JSONObject NoeCont = NoeArray.getJSONObject(i);
                        NoeCount = NoeCont.getInt("_value");
                    }
                    NewBill.Abstains = AbstainCount;
                    NewBill.Ayes = AyeCount;
                    NewBill.Noes = NoeCount;
                    NewBill.Name = PrimTopic.getString("title");
                    NewBill.count = BillCounter;
                    JSONObject Dateobj = PrimTopic.getJSONObject("date");
                    NewBill.Date = Dateobj.getString("_value");
                    GetVotes(URL, NewBill);
                    BillCounter++;
                    NetManager.getInstance(LandingActivity.this).BillList.set(index, NewBill);
                    NetManager.getInstance(LandingActivity.this).StaticBillList.set(index, NewBill);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                adapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                GetVoteResult(id, index);
                Log.d("error", "get vote");
            }
        }); requestQueue.add(request2);
    }
    public void GetVotes(final String url, final Bill bill){
        final ArrayList<Vote> VoteList = new ArrayList<Vote>();
        NetManager NetMgr = NetManager.getInstance(getApplicationContext());
        RequestQueue requestQueue = NetMgr.requestQueue;//fetch the request queue
        JsonObjectRequest request3 = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONObject result = null;
                try {
                    result = response.getJSONObject("result");
                    JSONObject main = result.getJSONObject("primaryTopic");
                    JSONArray Votes = main.getJSONArray("vote");
                    for(int i=0; i < Votes.length(); i++){
                        final Vote vote = new Vote();
                        JSONObject Member = Votes.getJSONObject(i);
                        String VoteParty = Member.getString("memberParty");
                        JSONObject Memberobj = Member.getJSONObject("memberPrinted");
                        String MemberName = Memberobj.getString("_value");
                        String VoteCont = Member.getString("type");
                        String VoteResult = VoteCont.substring(38);
                        vote.Name = MemberName;
                        if(VoteParty.equals("Labour (Co-op)")){
                            VoteParty = "Labour";
                        }
                        vote.Party = VoteParty;
                        vote.VoteType = VoteResult;
                        JSONArray about = Member.getJSONArray("member");
                        JSONObject obj = about.getJSONObject(0);
                        String RawConURL = obj.getString("_about");
                        GetVoteCon(RawConURL, vote, bill, i);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error", "vote fetch net error");
                if(error2shown==false){
                    Toast.makeText(getApplicationContext(), "There has been an error retrieving vote data, some information might be incorrect", Toast.LENGTH_LONG).show();}
                error2shown = true;
            }
        });
        requestQueue.add(request3);
    }
    public void GetVoteCon(final String URL, final Vote vote, final Bill bill, final int count){
        final NetManager NetMgr = NetManager.getInstance(getApplicationContext());
        RequestQueue requestQueue = NetMgr.requestQueue;//fetch the request queue
        String id = URL.substring(34);
        final String OutURL = "http://lda.data.parliament.uk/members/"+id+".json";
        JsonObjectRequest request4 = new JsonObjectRequest(Request.Method.GET, OutURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject result = response.getJSONObject("result");
                    JSONObject primTopic = result.getJSONObject("primaryTopic");
                    JSONObject Consti = primTopic.getJSONObject("constituency");
                    JSONObject label = Consti.getJSONObject("label");
                    String Con = label.getString("_value");
                    vote.Con = Con;
                    if (vote.VoteType.equals("AyeVote")){
                        bill.VoteAyeList.add(vote);
                    } else if (vote.VoteType.equals("NoVote")){
                        bill.VoteNoeList.add(vote);
                    }
                    if (vote.Party == "Labour"&& vote.VoteType.equals("AyeVote")){
                        bill.LabourAyeVotes.add(vote);
                    }
                    else if (vote.Party == "Labour"&& vote.VoteType.equals("NoVote")){
                        bill.LabourAyeVotes.add(vote);
                    }
                    else if(vote.Party == "Conservative" && vote.VoteType.equals("AyeVote")){//these lists are here to help keep load times down later on in the app
                        bill.ConservativeAyeVotes.add(vote);
                    }
                    else if(vote.Party == "Conservative" && vote.VoteType.equals("NoVote")){//these lists are here to help keep load times down later on in the app
                        bill.ConservativeNoVotes.add(vote);
                    }
                    if(bill.count>=49&&(count == (bill.Ayes+bill.Noes)-1)){
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(2000);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            mask.setVisibility(View.GONE);
                                            content.setVisibility(View.VISIBLE);
                                        }
                                    });

                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error3shown==false){
                Toast.makeText(getApplicationContext(), "There has been an error retrieving vote data, some MP's votes might be incorrect", Toast.LENGTH_LONG).show();}
                error3shown= true;
            }
        }); requestQueue.add(request4);


    }
    private void startManager(){//this class checks to see if the user has already set their MP
        SharedPreferences sharedPreferences = getSharedPreferences("Main_Pref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Boolean isFirst = sharedPreferences.getBoolean("First_Open", true);
        String FoundMP = sharedPreferences.getString("User_MP", "error");
        if(isFirst==true){//if the app has not been opened on this device before
            Intent intent = new Intent(getApplicationContext(), FindActivityPostcode.class);
            startActivity(intent);
            finish();
        }else{//else retrieve the MP the user set
//            String FoundMP = sharedPreferences.getString("User_MP", "error");
            if(FoundMP.equals("error")){
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
                YourMp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), MPDetails.class);
                        intent.putExtra("MP Name", UserMP.MPName);
                        startActivity(intent);
                    }
                });
                if (UserMP.MPImageUrl != null) {//check to ensure there is an image to prevent crashes if the URL is null
                    NetManager.getInstance(getApplicationContext()).imageLoader.get(UserMP.MPImageUrl, imageListener1);//setup NetManager object, fetch MP image
                }

                GetNews();
                GetBills();
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
    public boolean onOptionsItemSelected(MenuItem item){
        if(toggle.onOptionsItemSelected(item)){
            return true;
        } // if the search button is clicked, excecute the search code
        return super.onOptionsItemSelected(item);
    }
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
                        String tempDate = result.getString("webPublicationDate");

                        //these lines manipulate the date/time taken from the api to produce a more easily readable timestamp
                        tempDate = tempDate.replace("T", " ");
                        tempDate = tempDate.replace("Z", " ");
                        String year = tempDate.substring(0,4);
                        String month = tempDate.substring(5,7);
                        String day = tempDate.substring(8,10);
                        String time = tempDate.substring(11);
                        NewNews.Date = time+" -  "+day+"/"+month+"/"+year;

                        NewNews.Name = result.getString("webTitle");
                        NewNews.Category = result.getString("pillarName");
                        NewNews.URL = result.getString("webUrl");
                        NetManager.getInstance(LandingActivity.this).NewsList.add(NewNews);
                    }
                    adapter.NewsList = NetManager.getInstance(LandingActivity.this).NewsList;
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
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.nav_home){
            Intent intent = new Intent(this, LandingActivity.class);
            startActivity(intent);
        } else if(id == R.id.nav_MPs){
            Intent intent = new Intent(this, MPActivity.class);
            startActivity(intent);
        } else if(id == R.id.nav_Bills){
            Intent intent = new Intent(this, BillActivity.class);
            startActivity(intent);
        } else if(id == R.id.nav_reset){
            SharedPreferences sharedPreferences = getSharedPreferences("Main_Pref", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove("First_Open");
            editor.remove("User_MP");
            editor.apply();
            Intent intent = new Intent(this, LandingActivity.class);
            startActivity(intent);
        }else if(id == R.id.nav_about){
            Intent intent = new Intent(this, About.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}