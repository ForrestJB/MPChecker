//This is the MP browsing screen
//http://lda.data.parliament.uk/bills/752025.json
//http://lda.data.parliament.uk/bills.json?_view=Bills&_pageSize=50&_page=0
//URL FORMAT FOR PARLIAMENT RESOURCES!!!!: http://api.data.parliament.uk/resources/files/754404.xml

package uk.ac.kent.fb224.mpchecker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MPActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private DatabaseReference mDatabase;
    private DatabaseReference mMPReference;
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
    public Election election;
    private ActionBarDrawerToggle toggle;
    private android.support.v7.widget.Toolbar toolbar;
    private DrawerLayout drawer;
    int j = 0;
    public int counter = 0;//this is for removing the loading mask when all MPs have been loaded from the JSON
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mp_layout);

        mDatabase = FirebaseDatabase.getInstance().getReference(); // get a Reference for the current database
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toggle = new ActionBarDrawerToggle(
                this, drawer, R.string.navigation_bar_open, R.string.navigation_bar_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        MPRecyclerView = (RecyclerView) findViewById(R.id.MPListView);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        MPRecyclerView.setLayoutManager(layoutManager);
        adapter = new MPListAdapter();
        MPRecyclerView.setAdapter(adapter);//set the adapter and layout manager for the recyclerview
        getFavs();


        adapter.ConList = NetManager.getInstance(this).conList;
        NetManager.getInstance(this).DetailsConList = NetManager.getInstance(this).conList; // this defaults the list of MPs to be used by the details activity to be the full list of MPs
        NetManager NetMgr = NetManager.getInstance(getApplicationContext());
        RequestQueue requestQueue = NetMgr.requestQueue;//fetch the request queue
        Conurl = "https://www.theyworkforyou.com/api/getConstituencies?key=DvbcgvFHgew2FECNnUCJ7frD&output=js";

//        StringRequest request = new StringRequest(Request.Method.GET, Conurl, // this volley request fetches the name of all current UK Constituencies todo remove this or keep?
//            new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                try {
//                    JSONArray JConList = new JSONArray(response);
//                    for(int i=0; i < JConList.length(); i++){
//                        String conName;
//                        JSONObject Consti = JConList.getJSONObject(i);
//
//                       conName = Consti.getString("name");
////                        Log.d("con", conName); todo remove debug
//                        String MPURL = "https://www.theyworkforyou.com/api/getMP?constituency="+conName+"&key=DvbcgvFHgew2FECNnUCJ7frD&output=js";
////                        GetMP(MPURL, conName);
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                adapter.notifyDataSetChanged();
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.d("no", "no response");
//                //todo: error response
//            }
//        }
//    );
//        requestQueue.add(request);


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

        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getFavs();
        adapter.notifyDataSetChanged();
    }
    public void getFavs(){
        SharedPreferences sharedPref = getSharedPreferences("Main_Pref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        int key = sharedPref.getInt("MPNumber", 0);
        NetManager.getInstance(this).conFavList.clear();//clear the list of favourites before repopulating in order to prevent duplicates
            for (int i = 0; i < NetManager.getInstance(this).conList.size(); i++) {
                if(i==121){}
                else{
                    Constituency tempCon = NetManager.getInstance(this).conList.get(i);
                    String test = sharedPref.getString("MP"+tempCon.id, "error");
                    if(!test.equals("error")){
                        NetManager.getInstance(this).conList.get(i).isFav = true;
                        NetManager.getInstance(this).conFavList.add(tempCon);
                    }
                }
            }
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
public void addTextListener(String query){
                query = query.toString().toLowerCase();
                ArrayList<Constituency> list = new ArrayList<Constituency>();
                list = NetManager.getInstance(MPActivity.this).conList;
                if(IsFavOpen == true) {
                    list = NetManager.getInstance(MPActivity.this).conFavList;
                }

                final ArrayList<Constituency> FilteredList = new ArrayList<Constituency>();

                for(int i=0; i<list.size(); i++){
                    if(i != 121) {
                        final String Text = list.get(i).MPName.toLowerCase();
                        Log.d("iterations", Integer.toString(i));
                        if (Text.contains(query)) {
                            FilteredList.add(list.get(i));
                        }
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
        inflater.inflate(R.menu.main_nav_drawer, menu);
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
        switch (item.getItemId()) {
            case android.R.id.home:
                drawer.openDrawer(GravityCompat.START);  // OPEN DRAWER
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //this is the code to load current MPs from the API, although not currently used
    //it is left here if I ever need to update my database with a large number of new MPs

//    public void GetMP(String URL, final String conName){
//        NetManager NetMgr = NetManager.getInstance(getApplicationContext());
//        RequestQueue requestQueue = NetMgr.requestQueue;//fetch the request queue
//        StringRequest request2 = new StringRequest(Request.Method.GET, URL,
//        new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response2) {
//                final Constituency NewCon = new Constituency();
//                try {
//                    JSONObject MP = new JSONObject(response2);
//                    NewCon.ConName = conName;
//                    NewCon.MPName = MP.getString("full_name");
//                    NewCon.Party = MP.getString("party");
//                    if(MP.has("position")){
//                        NewCon.MPRole = MP.getString("position");
//                        mDatabase.child("Raw Data").child("MP").child(Integer.toString(counter)).child("MPRole").setValue(NewCon.MPRole);
//                    }
//                    if (MP.has("image")) {
//                        String temp = MP.getString("image");
//                        NewCon.MPImageUrl = "https://www.theyworkforyou.com" + temp;
//                        mDatabase.child("Raw Data").child("MP").child(Integer.toString(counter)).child("MPImageUrl").setValue(NewCon.MPImageUrl);
//                    }
//                    NetManager.getInstance(MPActivity.this).conList.add(NewCon);
//                    mDatabase.child("Raw Data").child("MP").child(Integer.toString(counter)).child("ConName").setValue(conName);
//                    mDatabase.child("Raw Data").child("MP").child(Integer.toString(counter)).child("MPName").setValue(NewCon.MPName);
//                    mDatabase.child("Raw Data").child("MP").child(Integer.toString(counter)).child("Party").setValue(NewCon.Party);
//
//                    counter++;
//                    if(counter == 649){
//
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                adapter.notifyDataSetChanged();
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.d("MP", "MP Net Error");
//            }
//        }
//    );
//        requestQueue.add(request2);
//    }
}
