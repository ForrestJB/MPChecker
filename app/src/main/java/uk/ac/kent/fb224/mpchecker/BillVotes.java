package uk.ac.kent.fb224.mpchecker;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Tree1 on 20/03/2018.
 */

public class BillVotes extends AppCompatActivity {
    private int BillPosition;
    private Bill bill;
    private RecyclerView PartyAyeRView;
    private RecyclerView PartyNoeRView;
    private BillVotesNameAdapter NAadapter;
    private BillVotesNameAdapter NNadapter;
    private BillVotesPartyAdapter PAadapter;
    private BillVotesPartyAdapter PNadapter;
    private LinearLayoutManager layoutManager1;
    private LinearLayoutManager layoutManager2;
    private ArrayList<String> PAyeList = new ArrayList<>();
    private ArrayList<String> PNoeList = new ArrayList<>();
    private ArrayList<Constituency> AllAyeList = new ArrayList<>();
    private ArrayList<Constituency> AllNoeList = new ArrayList<>();
    private TextView Title;
    private TextView Ayes;
    private TextView Noes;
    private TextView Date;
    private TextView Total;
    private Button PartyButton;
    private Button NameButton;
    private boolean isNameSelected = false;
    private ViewGroup Mask;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.bill_votes);

        Intent intent = getIntent();
        BillPosition = intent.getIntExtra("Bill_Position", 0);
        bill = NetManager.getInstance(this).BillList.get(BillPosition);

        PartyAyeRView = findViewById(R.id.BVPAyesRView);
        PartyNoeRView = findViewById(R.id.BVPNoesRView);
        Ayes = findViewById(R.id.BVAyes);
        Noes = findViewById(R.id.BVNoes);
        Total = findViewById(R.id.BVTotalVotes);
        Title = findViewById(R.id.BVTitle);
        PartyButton = findViewById(R.id.BVPartyButton);
        NameButton = findViewById(R.id.BVNameButton);
        Date = findViewById(R.id.BVDate);
        Mask = findViewById(R.id.MaskCon);

        Ayes.append(" "+Integer.toString(bill.Ayes));
        Noes.append(" "+Integer.toString(bill.Noes));
        int TotalVotes = bill.Ayes + bill.Noes;
        Total.append(" "+Integer.toString(TotalVotes));
        Title.setText(bill.Name);
        try {
            DateFormat raw = new SimpleDateFormat("yyyy-MM-dd");
            DateFormat output = new SimpleDateFormat("E, MMM dd yyyy");
            java.util.Date newDate = null;
            newDate = raw.parse(bill.Date);
            String ParsedDate = output.format(newDate);
            Date.setText(ParsedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        PAadapter = new BillVotesPartyAdapter();
        PNadapter = new BillVotesPartyAdapter();
        NAadapter = new BillVotesNameAdapter();
        NNadapter = new BillVotesNameAdapter();

        layoutManager1 = new LinearLayoutManager(this){//these are here as we want to use the smooth scrolling provided by the NestedScrollView instead of the RecyclerView
          @Override
            public boolean canScrollVertically(){
              return false;
          }
        };
        layoutManager2 = new LinearLayoutManager(this){
            @Override
            public boolean canScrollVertically(){
                return false;
            }
        };
        layoutManager1.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager2.setOrientation(LinearLayoutManager.VERTICAL);
        PartyAyeRView.setLayoutManager(layoutManager1);
        PartyNoeRView.setLayoutManager(layoutManager2);
        PNadapter = new BillVotesPartyAdapter();
        PAadapter = new BillVotesPartyAdapter();
        PartyAyeRView.setAdapter(PAadapter);
        PartyNoeRView.setAdapter(PNadapter);

        for(int i=0; i<bill.VoteAyeList.size();i++){
            final Vote vote;
            boolean add = false;
            boolean PartyFound = false;
            vote = bill.VoteAyeList.get(i);
            if(PAyeList.size() == 0){
                add = true;
            }else {
                for (int j = 0; j < PAyeList.size(); j++) {
                    String compare = PAyeList.get(j);
                    if (compare.equals(vote.Party)) {
                        PartyFound = true;
                    }
                    if (j+1== PAyeList.size() && PartyFound == false) {
                        add = true;
                    }
                }
            }
            if(add==true){
                PAyeList.add(vote.Party);
            }
        }
        for(int i=0; i<bill.VoteNoeList.size();i++){
            final Vote vote;
            boolean add = false;
            boolean PartyFound = false;
            vote = bill.VoteNoeList.get(i);
            if(PNoeList.size() == 0){
                add = true;
            }else {
                for (int j = 0; j < PNoeList.size(); j++) {
                    String compare = PNoeList.get(j);
                    if (compare.equals(vote.Party)) {
                        PartyFound = true;
                    }
                    if (j+1== PNoeList.size() && PartyFound == false) {
                        add = true;
                    }
                }
            }
            if(add==true){
                PNoeList.add(vote.Party);
            }
        }

        PAadapter.PartyList = PAyeList;
        PNadapter.PartyList = PNoeList;
        PAadapter.BillPosition = BillPosition;
        PNadapter.BillPosition = BillPosition;
        PAadapter.VoteList = bill.VoteAyeList;
        PNadapter.VoteList = bill.VoteNoeList;
        PAadapter.notifyDataSetChanged();
        PNadapter.notifyDataSetChanged();
        NNadapter.MPList = bill.VoteNoeList;
        NAadapter.MPList = bill.VoteAyeList;

        PartyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNameSelected==false){
                } else{
                    PartyButton.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                    NameButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    PartyAyeRView.setAdapter(PAadapter);
                    PartyNoeRView.setAdapter(PNadapter);
                    PAadapter.notifyDataSetChanged();
                    PNadapter.notifyDataSetChanged();
                    isNameSelected=false;
                }
            }
        });
        NameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNameSelected== true){
                }else{
                    NameButton.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                    PartyButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    Mask.setVisibility(View.VISIBLE);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(50);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    PartyAyeRView.setAdapter(NAadapter);
                                    PartyNoeRView.setAdapter(NNadapter);
                                    NAadapter.notifyDataSetChanged();
                                    NNadapter.notifyDataSetChanged();
                                    isNameSelected=true;
                                    Mask.setVisibility(View.GONE);
                                }
                            });
                        }
                    }).start();
                }
            }
        });
    }
}
