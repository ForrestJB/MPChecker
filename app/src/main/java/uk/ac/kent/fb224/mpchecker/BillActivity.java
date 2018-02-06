package uk.ac.kent.fb224.mpchecker;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

/**
 * Created by Tree1 on 31/01/2018.
 */

public class BillActivity extends AppCompatActivity {
    private RecyclerView BillRecyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bill_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        BillRecyclerView = (RecyclerView) findViewById(R.id.BillListView);
    }
}
