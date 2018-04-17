package uk.ac.kent.fb224.mpchecker;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
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

public class BillPartyVotes extends AppCompatActivity{
    private String Party;
    private Bill bill;
    private DatabaseReference mDatabase;
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
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.bill_votes_party);

        Intent intent = getIntent();
        Party = intent.getStringExtra("Party");
        BillPosition = intent.getIntExtra("BillPosition", 0);
        bill = NetManager.getInstance(this).BillList.get(BillPosition);
        mDatabase = FirebaseDatabase.getInstance().getReference(); // get a Reference for the current database

        PartyAyeRView = findViewById(R.id.BVPAyesRView);
        PartyNoeRView = findViewById(R.id.BVPNoesRView);
        Ayes = findViewById(R.id.BVAyes);
        Noes = findViewById(R.id.BVNoes);
        Total = findViewById(R.id.BVTotalVotes);
        Title = findViewById(R.id.BVTitle);
        VoteB = findViewById(R.id.BVVoteBreakdown);

        NAadapter = new BillVotesNameAdapter();
        NNadapter = new BillVotesNameAdapter();

        layoutManager1 = new LinearLayoutManager(this);
        layoutManager2 = new LinearLayoutManager(this);
        layoutManager1.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager2.setOrientation(LinearLayoutManager.VERTICAL);
        PartyAyeRView.setLayoutManager(layoutManager1);
        PartyNoeRView.setLayoutManager(layoutManager2);

        PartyAyeRView.setAdapter(NAadapter);
        PartyNoeRView.setAdapter(NNadapter);

        for(int i=0; i<bill.VoteAyeList.size();i++){//get all of the yes votes from MPs of this party
            String compare = bill.VoteAyeList.get(i).Party;
            if(compare.equals(Party)){
                AyeVotes.add(bill.VoteAyeList.get(i));
                Log.d("iterations", Integer.toString(i));
            }
        }
        for(int i=0; i<bill.VoteNoeList.size();i++){//get all of the no votes from MPs of this party
            String compare = bill.VoteNoeList.get(i).Party;
            if(compare.equals(Party)){
                NoeVotes.add(bill.VoteNoeList.get(i));
            }
        }

        NetManager NetMgr = NetManager.getInstance(getApplicationContext());
        NetManager.getInstance(this).DetailsConList = NetManager.getInstance(this).conList;
        for(int i=0;i<NetMgr.conList.size();i++){//set the index of each MP in the main list as a variable of that mp (this is important for use in the adapter)
            if(i==121){}else {
                NetMgr.conList.get(i).pos = i;
            }
        }
        for(int i=0;i<AyeVotes.size();i++){//get the full details for all of the yes voting MP's found earlier
            Vote tempVote = AyeVotes.get(i);
            for(int j=0;j<NetMgr.conList.size();j++){
                if(j==121){
                }else{
                Constituency tempCon = NetMgr.conList.get(j);
                if(tempVote.Con.equals(tempCon.ConName)){
                    AyeCons.add(tempCon);
                    break;
                }
            }}
        }
        for(int i=0;i<NoeVotes.size();i++){//get the full details for all of the no voting MP's found earlier
            Vote tempVote = NoeVotes.get(i);
            for(int j=0;j<NetMgr.conList.size();j++){
                if(j==121){

                }else{
                Constituency tempCon = NetMgr.conList.get(j);
                if(tempVote.Con.equals(tempCon.ConName)){
                    NoeCons.add(tempCon);
                    break;
                }
            }}
        }
        NAadapter.MPList = AyeCons;
        NAadapter.isNoes = false;
        NNadapter.MPList = NoeCons;
        NNadapter.isNoes = true;

        Title.setText(bill.Name);
        Total.append(" "+(AyeCons.size()+NoeCons.size()));
        Ayes.append(" "+AyeCons.size());
        Noes.append(" "+NoeCons.size());
        VoteB.setText(Party+" Vote Breakdown:");
        NAadapter.notifyDataSetChanged();
        NNadapter.notifyDataSetChanged();
    }
}
