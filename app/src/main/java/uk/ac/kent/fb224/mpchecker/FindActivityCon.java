package uk.ac.kent.fb224.mpchecker;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Forrest on 30/04/2018.
 */

public class FindActivityCon extends AppCompatActivity{
    private RecyclerView ConView;
    private LinearLayoutManager layoutManager;
    private ConListAdapter adapter;
    private ArrayList<Constituency> tempList = new ArrayList<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.find_layout_con);
        ConView = findViewById(R.id.ConRView);
        NetManager NetMgr = NetManager.getInstance(this);
        tempList = NetMgr.conList;
        if(NetMgr.conSortedDone == false) {
            for (int i = 121; i <= tempList.size(); i++) {
                Constituency tempCon = tempList.get(649);
                tempList.add(i, tempCon);
                tempList.remove(650);
            }
            tempList.remove(649);
            NetManager.getInstance(this).conSortedDone = true;
        }

        Collections.sort(tempList, Constituency.ConNameSort);

        NetManager.getInstance(this).SortedConList = tempList;
        ConView = (RecyclerView) findViewById(R.id.ConRView);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        ConView.setLayoutManager(layoutManager);
        adapter = new ConListAdapter();
        adapter.ConList = NetManager.getInstance(this).SortedConList;
        ConView.setAdapter(adapter);//set the adapter and layout manager for the recyclerview
        adapter.notifyDataSetChanged();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
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
    public void addTextListener(String query){
        query = query.toString().toLowerCase();
        ArrayList<Constituency> list = new ArrayList<Constituency>();
        list = NetManager.getInstance(FindActivityCon.this).SortedConList;

        final ArrayList<Constituency> FilteredList = new ArrayList<Constituency>();

        for(int i=0; i<list.size(); i++){
            if(i != 121) {
                final String Text = list.get(i).ConName.toLowerCase();
                Log.d("iterations", Integer.toString(i));
                if (Text.contains(query)) {
                    FilteredList.add(list.get(i));
                }
            }
        }
        ConView.setLayoutManager(new LinearLayoutManager(FindActivityCon.this));
        adapter.ConList = FilteredList;
//        NetManager.getInstance(MPActivity.this).DetailsConList = FilteredList;
        adapter.notifyDataSetChanged();
    }
}
