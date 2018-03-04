package uk.ac.kent.fb224.mpchecker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Tree1 on 31/01/2018.
 */

public class BillActivity extends AppCompatActivity {
    private RecyclerView BillRecyclerView;
    private RecyclerView MPRecyclerView;
    private LinearLayoutManager layoutManager;
    private BillListAdapter adapter;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    public String FetchURL;
    public String BillURL;
    private Button MenuBills;
    private Button MPFavs;
    private Button AllMps;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bill_layout);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true); todo: wtf is wrong with this?
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        BillRecyclerView = (RecyclerView) findViewById(R.id.BillListView);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        BillRecyclerView.setLayoutManager(layoutManager);
        adapter = new BillListAdapter();
        BillRecyclerView.setAdapter(adapter);
        adapter.BillList = NetManager.getInstance(this).BillList;
        NetManager NetMgr = NetManager.getInstance(getApplicationContext());
        RequestQueue requestQueue = NetMgr.requestQueue;
        BillURL = "http://lda.data.parliament.uk/commonsdivisions.json?_view=Commons+Divisions&_pageSize=30&_page=0";

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
                        GetVoteResult(tempURL);//this takes the id number from the url provided, and passes it a method to fetch the appropriate information
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Network error, please check your internet connection", Toast.LENGTH_LONG).show();//if there is a reponse error, notify the user
            }
        });
        requestQueue.add(request);
    }
    public void GetVoteResult (String id){
        NetManager NetMgr = NetManager.getInstance(getApplicationContext());
        RequestQueue requestQueue = NetMgr.requestQueue;//fetch the request queue
        String URL = "http://lda.data.parliament.uk/commonsdivisions/id/"+id+".json";
        JsonObjectRequest request2 = new JsonObjectRequest(Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                final Bill NewBill = new Bill();
                try {
                    JSONObject result = response.getJSONObject("result");
                    JSONObject PrimTopic = result.getJSONObject("primaryTopic");
                    JSONArray AbstainArray = PrimTopic.getJSONArray("AbstainCount");
                    int AbstainCount = 0;
                    for (int i = 0; i < AbstainArray.length(); i++) {
                        JSONObject AbCont = AbstainArray.getJSONObject(i);
                        AbstainCount = AbCont.getInt("_value");
                        Log.d("abstains", Integer.toString(AbstainCount));
                    }
                    JSONArray AyeArray = PrimTopic.getJSONArray("AyesCount");
                    int AyeCount = 0;
                    for (int i = 0; i < AyeArray.length(); i++) {
                        JSONObject AyeCont = AyeArray.getJSONObject(i);
                        AyeCount = AyeCont.getInt("_value");
                        Log.d("Ayes", Integer.toString(AyeCount));
                    }
                    JSONArray NoeArray = PrimTopic.getJSONArray("Noesvotecount");
                    int NoeCount = 0;
                    for (int i = 0; i < NoeArray.length(); i++) {
                        JSONObject NoeCont = NoeArray.getJSONObject(i);
                        NoeCount = NoeCont.getInt("_value");
                        Log.d("Noes", Integer.toString(NoeCount));
                    }
                    NewBill.Abstains = AbstainCount;
                    NewBill.Ayes = AyeCount;
                    NewBill.Noes = NoeCount;
                    NewBill.Name = PrimTopic.getString("title");
                    JSONObject Dateobj = PrimTopic.getJSONObject("date");
                    NewBill.Date = Dateobj.getString("_value");
                    Log.d("title", NewBill.Name);
                    JSONArray Votes = PrimTopic.getJSONArray("vote");
//                    for(int i=0; i < Votes.length(); i++){
//                        JSONObject Member = Votes.getJSONObject(i);
//                        String VoteParty = Member.getString("memberParty");
//                        Log.d("party", VoteParty);
//                        JSONObject Memberobj = Member.getJSONObject("memberPrinted");
//                        String MemberName = Memberobj.getString("_value");
//                        Log.d("MP name", MemberName);
//                        String VoteCont = Member.getString("type");
//                        String vote = VoteCont.substring(38);
//                        Log.d("vote", vote);
//                        //todo itemised votes
                    NetManager.getInstance(BillActivity.this).BillList.add(NewBill);
//                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                adapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Network error, please check your internet connection", Toast.LENGTH_LONG).show();//if there is a reponse error, notify the user

            }
        });
        requestQueue.add(request2);
    }
}
