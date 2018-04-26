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
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Tree1 on 31/01/2018.
 */

public class BillActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
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
    private int Counter = 0;
    private TextView MaskText;
    private ImageView MaskImage;
    private ProgressBar MaskSpinner;
    private ActionBarDrawerToggle toggle;
    private android.support.v7.widget.Toolbar toolbar;
    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bill_layout);

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

        BillRecyclerView = (RecyclerView) findViewById(R.id.BillListView);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        BillRecyclerView.setLayoutManager(layoutManager);
        adapter = new BillListAdapter();
        BillRecyclerView.setAdapter(adapter);
        adapter.BillList = NetManager.getInstance(this).BillList;


    }

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
        switch (item.getItemId()) {
            case android.R.id.home:
                drawer.openDrawer(GravityCompat.START);  // OPEN DRAWER
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void addTextListener(String query){
        query = query.toString().toLowerCase();
        ArrayList<Bill> list = new ArrayList<Bill>();
            list = NetManager.getInstance(BillActivity.this).StaticBillList;
        final ArrayList<Bill> FilteredList = new ArrayList<Bill>();

        for(int i=0; i<list.size(); i++){
            final String Text = list.get(i).Name.toLowerCase();
            if(Text.contains(query)){
                FilteredList.add(list.get(i));
            }
        }
        BillRecyclerView.setLayoutManager(new LinearLayoutManager(BillActivity.this));
        adapter = new BillListAdapter();
        BillRecyclerView.setAdapter(adapter);
        adapter.BillList = FilteredList;
        NetManager.getInstance(BillActivity.this).BillList = FilteredList;
        adapter.notifyDataSetChanged();
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

