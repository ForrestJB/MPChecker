package uk.ac.kent.fb224.mpchecker;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by Tree1 on 24/03/2018.
 */

public class BillPartyVotes extends AppCompatActivity {
    private String Party;
    private Bill bill;
    private int BillPosition;
    private RecyclerView PartyAyeRView;
    private RecyclerView PartyNoeRView;
    private BillVotesNameAdapter NAadapter;
    private BillVotesNameAdapter NNadapter;
    private LinearLayoutManager layoutManager1;
    private LinearLayoutManager layoutManager2;
    private TextView Title;
    private TextView VoteB;
    private TextView Ayes;
    private TextView Noes;
    private TextView Total;
    private ArrayList<Vote> AyeVotes = new ArrayList<>();
    private ArrayList<Vote> NoeVotes = new ArrayList<>();
    private ArrayList<Constituency> AyeCons = new ArrayList<>();
    private ArrayList<Constituency> NoeCons = new ArrayList<>();
    private ViewGroup Mask;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.bill_votes_party);

        Intent intent = getIntent();
        Party = intent.getStringExtra("Party");
        BillPosition = intent.getIntExtra("BillPosition", 0);
        bill = NetManager.getInstance(this).BillList.get(BillPosition);

        PartyAyeRView = findViewById(R.id.BVPAyesRView);
        PartyNoeRView = findViewById(R.id.BVPNoesRView);
        Ayes = findViewById(R.id.BVAyes);
        Noes = findViewById(R.id.BVNoes);
        Total = findViewById(R.id.BVTotalVotes);
        Title = findViewById(R.id.BVTitle);
        VoteB = findViewById(R.id.BVVoteBreakdown);
        Mask = findViewById(R.id.MaskCon);

        NAadapter = new BillVotesNameAdapter();
        NNadapter = new BillVotesNameAdapter();

        layoutManager1 = new LinearLayoutManager(this) {//these are here as we want to use the smooth scrolling provided by the NestedScrollView instead of the RecyclerView scrolling
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        layoutManager2 = new LinearLayoutManager(this) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        layoutManager1.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager2.setOrientation(LinearLayoutManager.VERTICAL);
        PartyAyeRView.setLayoutManager(layoutManager1);
        PartyNoeRView.setLayoutManager(layoutManager2);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Thread(new Runnable() {
            @Override
            public void run() {


                NetManager NetMgr = NetManager.getInstance(getApplicationContext());
                if(Party == "Labour"){
                    AyeVotes = bill.LabourAyeVotes;
                    NoeVotes = bill.LabourNoVotes;
                } else if (Party == "Conservative") {
                    AyeVotes = bill.ConservativeAyeVotes;
                    NoeVotes = bill.ConservativeNoVotes;
                }
                else {
                    for (int i = 0; i < bill.VoteAyeList.size(); i++) {//get all of the yes votes from MPs of this party
                        String compare = bill.VoteAyeList.get(i).Party;
                        if (compare.equals(Party)) {
                            AyeVotes.add(bill.VoteAyeList.get(i));

                        }
                    }
                    for (int i = 0; i < bill.VoteNoeList.size(); i++) {//get all of the no votes from MPs of this party
                        String compare = bill.VoteNoeList.get(i).Party;
                        if (compare.equals(Party)) {
                            NoeVotes.add(bill.VoteNoeList.get(i));
                        }
                    }
                }
                try {
                    Thread.sleep(50);//this is here to hold this thread to allow the oncreate to return and move onto the next thread before the recyclerview causes the ui thread to hang
                                            //further comment: this seems like the completely wrong kind of way to fix this but it works
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                NAadapter.MPList = AyeVotes;
                NAadapter.isNoes = false;
                NNadapter.MPList = NoeVotes;
                NNadapter.isNoes = true;
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        PartyAyeRView.setAdapter(NAadapter);
                        PartyNoeRView.setAdapter(NNadapter);

                        Title.setText(bill.Name);
                        Total.append(" " + (AyeVotes.size() + NoeVotes.size()));
                        Ayes.append(" " + AyeVotes.size());
                        Noes.append(" " + NoeVotes.size());
                        VoteB.setText(Party + " Vote Breakdown:");
                        NAadapter.notifyDataSetChanged();
                        NNadapter.notifyDataSetChanged();
                        Mask.setVisibility(View.GONE);

                    }

                });
            }
        }).start();




    }
}



