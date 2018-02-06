//This is the MP list screen, which at this time is also serving as a landing page for the app
package uk.ac.kent.fb224.mpchecker;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private RecyclerView MPRecyclerView;
    private LinearLayoutManager layoutManager;
    private MPListAdapter adapter;
    public String Conurl;
    private Button MenuBills;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        MenuBills = findViewById(R.id.MainBillButton);


        MPRecyclerView = (RecyclerView) findViewById(R.id.MPListView);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        MPRecyclerView.setLayoutManager(layoutManager);
        adapter = new MPListAdapter();
        MPRecyclerView.setAdapter(adapter);//set the adapter and layout manager for the recyclerview
        adapter.ConList = NetManager.getInstance(this).conList;
        NetManager NetMgr = NetManager.getInstance(getApplicationContext());
        RequestQueue requestQueue = NetMgr.requestQueue;//fetch the request queue
        Conurl = "https://www.theyworkforyou.com/api/getConstituencies?key=DvbcgvFHgew2FECNnUCJ7frD&output=js";
        StringRequest request = new StringRequest(Request.Method.GET, Conurl, // this volley request fetches the name of all current UK Constituencies
            new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("yes", "good response");
                try {
                    JSONArray JConList = new JSONArray(response);
                    int d = 0;
                    for(int i=0; i < JConList.length(); i++){
                        String conName;
                        JSONObject Consti = JConList.getJSONObject(i);

                       conName = Consti.getString("name");
                        Log.d("con", conName);
                        String MPURL = "https://www.theyworkforyou.com/api/getMP?constituency="+conName+"&key=DvbcgvFHgew2FECNnUCJ7frD&output=js";
                        GetMP(MPURL, conName);
                        d++;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                adapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("no", "no response");
                //todo: error response
            }
        }
    );
        requestQueue.add(request);

//        MenuBills.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(this, BillActivity.class);
//            }
//        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public void GetMP(String URL, final String conName){
        NetManager NetMgr = NetManager.getInstance(getApplicationContext());
        RequestQueue requestQueue = NetMgr.requestQueue;//fetch the request queue
        StringRequest request2 = new StringRequest(Request.Method.GET, URL,
        new Response.Listener<String>() {
            @Override
            public void onResponse(String response2) {
                final Constituency NewCon = new Constituency();
                try {
                    JSONObject MP = new JSONObject(response2);
                    NewCon.ConName = conName;
                    NewCon.MPName = MP.getString("full_name");
                    NewCon.Party = MP.getString("party");
                    if (MP.has("image")) {
                        String temp = MP.getString("image");
                        NewCon.MPImageUrl = "https://www.theyworkforyou.com" + temp;
                    }
                    NetManager.getInstance(MainActivity.this).conList.add(NewCon);

                    Log.d("MP", NewCon.MPName);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                adapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("MP", "MP Net Error");
            }
        }
    );
        requestQueue.add(request2);
    }

}
