package uk.ac.kent.fb224.mpchecker;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Tree1 on 01/03/2018.
 */

public class LandingActivity extends AppCompatActivity {
    public Button Bills;
    public Button MPs;
    private DatabaseReference mMPReference;
    public Button News;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.landing_menu);

        Bills = findViewById(R.id.MainBillButton);
        MPs = findViewById(R.id.MainMpButton);
        News = findViewById(R.id.MainNewsButton);

        for(int k=0;k<=649;k++) {//todo this is kinda a janky fix, but is it ok?
            mMPReference = FirebaseDatabase.getInstance().getReference().child("Raw Data").child("MP").child(Integer.toString(k));
            ValueEventListener MPListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final Constituency NewCon = dataSnapshot.getValue(Constituency.class);
                    NetManager.getInstance(LandingActivity.this).conList.add(NewCon);
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            mMPReference.addValueEventListener(MPListener);
        }

        Bills.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), BillActivity.class);
                startActivity(intent);
            }
        });
        MPs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MPActivity.class);
                startActivity(intent);
            }
        });
        News.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo: News onclick after activity is implemented
            }
        });
    }
}

