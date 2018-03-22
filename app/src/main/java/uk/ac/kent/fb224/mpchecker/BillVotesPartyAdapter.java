package uk.ac.kent.fb224.mpchecker;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;

/**
 * Created by Tree1 on 21/03/2018.
 */

public class BillVotesPartyAdapter  extends RecyclerView.Adapter<BillVotesPartyAdapter.ViewHolder>{
    private Bill bill;
    private Context context;
    public ArrayList<String> PartyList;
    public ArrayList<Vote> VoteList;
    private ImageView PartyColour;
    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView PartyName;

        private TextView VoteCount;
        private View.OnClickListener onClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo onclick for partys
            }
        };
        public ViewHolder(View itemView) {
            super(itemView);
            PartyName = itemView.findViewById(R.id.BVPPartyName);
            PartyColour = itemView.findViewById(R.id.BVPPartyBlock);
            VoteCount = itemView.findViewById(R.id.BVPVoteCount);
            this.itemView.setOnClickListener(onClick);
        }
    }
    @Override
    public BillVotesPartyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.bill_votes_party_card, parent, false);
        BillVotesPartyAdapter.ViewHolder vh = new BillVotesPartyAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(BillVotesPartyAdapter.ViewHolder holder, int position) {
        String Party = PartyList.get(position);
        int VoteCount = 0;
        holder.PartyName.setText(Party);
        for(int i=0;i<VoteList.size();i++){
            String ThisParty = VoteList.get(i).Party;
            if (ThisParty.equals(Party)) {
                VoteCount++;
            }
        }
        holder.VoteCount.setText(Integer.toString(VoteCount));

        if(Party.equals("Conservative")) {
            PartyColour.setBackgroundColor(Color.rgb(0,135, 220));
        }
        else if (Party.equals("Labour")){
            PartyColour.setBackgroundColor(Color.rgb(146,0,13));
        }
        else if (Party.equals("Labour (Co-op)")){
            PartyColour.setBackgroundColor(Color.rgb(146,0,14));
        }
        else if (Party.equals("Liberal Democrat")){
            PartyColour.setBackgroundColor(Color.rgb(253,187,48));
        }
        else if(Party.equals("Scottish National Party")){
            PartyColour.setBackgroundColor(Color.rgb(255,249,93));
        }
        else if (Party.equals("Plaid Cymru")){
            PartyColour.setBackgroundColor(Color.rgb(63,132,40));
        }
        else if (Party.equals("Green Party")) {
            PartyColour.setBackgroundColor(Color.rgb(0,116,95));
        }
        else if (Party.equals("Democratic Unionist Party")){
            PartyColour.setBackgroundColor(Color.rgb(212,106,76));
        }
        else if (Party.equals("Sinn Féin")){
            PartyColour.setBackgroundColor(Color.rgb(0,136,0));
        }
        else {
            PartyColour.setBackgroundColor(Color.rgb(0,0,0));
        }
    }

    @Override
    public int getItemCount() {
        return PartyList.size();
    }
}
