package uk.ac.kent.fb224.mpchecker;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;

/**
 * Created by Tree1 on 20/03/2018.
 */

public class BillVotes extends AppCompatActivity {
    private int BillPosition;
    private Bill bill;
    private String VotesSelected;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.bill_votes);

        Intent intent = getIntent();
        BillPosition = intent.getIntExtra("Bill_Position", 0);
        bill = NetManager.getInstance(this).BillList.get(BillPosition);

        VotesSelected = intent.getStringExtra("Votes_Selected");


    }
}
