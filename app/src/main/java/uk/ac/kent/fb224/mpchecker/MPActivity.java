//This is the MP browsing screen
//http://lda.data.parliament.uk/bills/752025.json
//http://lda.data.parliament.uk/bills.json?_view=Bills&_pageSize=50&_page=0
//URL FORMAT FOR PARLIAMENT RESOURCES!!!!: http://api.data.parliament.uk/resources/files/754404.xml

package uk.ac.kent.fb224.mpchecker;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import java.util.List;

public class MPActivity extends AppCompatActivity {
    private RecyclerView MPRecyclerView;
    private LinearLayoutManager layoutManager;
    private MPListAdapter adapter;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    public String Conurl;
    private Button MPFavs;
    private Button AllMps;
    private ImageView MaskImage;
    private ProgressBar MaskSpinner;
    private TextView MaskText;
    private Boolean IsFavOpen = false;
    public int counter = 0;//this is for removing the loading mask when all MPs have been loaded from the JSON
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mp_layout);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        NavigationView navigationView = findViewById(R.id.MPNavView);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                //handle Nav Drawer clicks here with a switch case
                return false;
            }
        });

        MPRecyclerView = (RecyclerView) findViewById(R.id.MPListView);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        MPRecyclerView.setLayoutManager(layoutManager);
        adapter = new MPListAdapter();
        MPRecyclerView.setAdapter(adapter);//set the adapter and layout manager for the recyclerview

        adapter.ConList = NetManager.getInstance(this).conList;
        NetManager.getInstance(this).DetailsConList = NetManager.getInstance(this).conList; // this defaults the list of MPs to be used by the details activity to be the full list of MPs
        NetManager NetMgr = NetManager.getInstance(getApplicationContext());
        RequestQueue requestQueue = NetMgr.requestQueue;//fetch the request queue
        Conurl = "https://www.theyworkforyou.com/api/getConstituencies?key=DvbcgvFHgew2FECNnUCJ7frD&output=js";
        StringRequest request = new StringRequest(Request.Method.GET, Conurl, // this volley request fetches the name of all current UK Constituencies
            new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray JConList = new JSONArray(response);
                    for(int i=0; i < JConList.length(); i++){
                        String conName;
                        JSONObject Consti = JConList.getJSONObject(i);

                       conName = Consti.getString("name");
//                        Log.d("con", conName); todo remove debug
                        String MPURL = "https://www.theyworkforyou.com/api/getMP?constituency="+conName+"&key=DvbcgvFHgew2FECNnUCJ7frD&output=js";
                        GetMP(MPURL, conName);
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


        MPFavs = findViewById(R.id.MPFavs);
        MPFavs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IsFavOpen = true;
                adapter.ConList = NetManager.conFavList;
                NetManager.getInstance(MPActivity.this).DetailsConList = NetManager.getInstance(MPActivity.this).conFavList; //changes the list that the details activity will take MPs from to the Favourite list
                MPFavs.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                AllMps.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                adapter.notifyDataSetChanged();
                Log.d("fav", "button pressed"); // Todo remove debug
            }
        });
        AllMps = findViewById(R.id.MPAllMPs);
        AllMps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IsFavOpen = false;
                adapter.ConList = NetManager.conList;
                NetManager.getInstance(MPActivity.this).DetailsConList = NetManager.getInstance(MPActivity.this).conList; //reverts back to the main list of MPs
                MPFavs.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                AllMps.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                adapter.notifyDataSetChanged();
                Log.d("AllMps", "button pressed"); // Todo remove debug
            }
        });

    }
public void addTextListener(String query){
                query = query.toString().toLowerCase();
                ArrayList<Constituency> list = new ArrayList<Constituency>();
                if(IsFavOpen == false) {
                    list = NetManager.getInstance(MPActivity.this).conList;
                }
                else {
                    list = NetManager.getInstance(MPActivity.this).conFavList;
                }
                final ArrayList<Constituency> FilteredList = new ArrayList<Constituency>();

                for(int i=0; i<list.size(); i++){
                    final String Text = list.get(i).MPName.toLowerCase();
                    if(Text.contains(query)){
                        FilteredList.add(list.get(i));
                    }
                }
                MPRecyclerView.setLayoutManager(new LinearLayoutManager(MPActivity.this));
                adapter = new MPListAdapter();
                MPRecyclerView.setAdapter(adapter);
                adapter.ConList = FilteredList;
                NetManager.getInstance(MPActivity.this).DetailsConList = FilteredList;
                adapter.notifyDataSetChanged();
            }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.main_nav_drawer, menu);
        inflater.inflate(R.menu.menu_main, menu);

        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                addTextListener(newText); // this starts the method to manage searching of the MP List
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if(mToggle.onOptionsItemSelected(item)){
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
                    NetManager.getInstance(MPActivity.this).conList.add(NewCon);
                    counter++;
                    if(counter == 649){
                        MaskText = findViewById(R.id.MPLoadText);
                        MaskImage = findViewById(R.id.MPLoadMask);
                        MaskSpinner = findViewById(R.id.MPLoadMaskSpinner);
                        MaskImage.setVisibility(View.GONE);
                        MaskSpinner.setVisibility(View.GONE);
                        MaskText.setVisibility(View.GONE);
                    }
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
