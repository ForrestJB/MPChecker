package uk.ac.kent.fb224.mpchecker;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
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

public class BillVotesNameAdapter extends RecyclerView.Adapter<BillVotesNameAdapter.ViewHolder>{
    public ArrayList<Constituency> MPList = new ArrayList<>();
    private Context context;

    public class ViewHolder extends RecyclerView.ViewHolder {
        private NetworkImageView MPThumb;
        private ImageView PartyColour;
        private TextView MPName;
        private TextView PartyName;
        private TextView ConName;
        public ViewHolder(View itemView) {
            super(itemView);
            PartyColour = itemView.findViewById(R.id.BVMPPartyColour);
            MPName = itemView.findViewById(R.id.BVMPName);
            PartyName = itemView.findViewById(R.id.BVMPPartyName);
            ConName = itemView.findViewById(R.id.BVMPCon);
        }
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.bill_votes_mp_card, parent, false);
        BillVotesNameAdapter.ViewHolder vh = new BillVotesNameAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Constituency con = MPList.get(position);
        holder.MPName.setText(con.MPName);
        holder.PartyName.setText(con.Party);
        holder.ConName.setText(con.ConName);
        String Party = con.Party;
        if(Party.equals("Conservative")) {
            holder.PartyColour.setBackgroundColor(Color.rgb(0,135, 220));
        }
        else if (Party.equals("Labour")){
            holder.PartyColour.setBackgroundColor(Color.rgb(146,0,13));
        }
        else if (Party.equals("Labour (Co-op)")){
            holder.PartyColour.setBackgroundColor(Color.rgb(146,0,14));
        }
        else if (Party.equals("Liberal Democrat")){
            holder.PartyColour.setBackgroundColor(Color.rgb(253,187,48));
        }
        else if(Party.equals("Scottish National Party")){
            holder.PartyColour.setBackgroundColor(Color.rgb(255,249,93));
        }
        else if (Party.equals("Plaid Cymru")){
            holder.PartyColour.setBackgroundColor(Color.rgb(63,132,40));
        }
        else if (Party.equals("Green Party")) {
            holder.PartyColour.setBackgroundColor(Color.rgb(0,116,95));
        }
        else if (Party.equals("Democratic Unionist Party")){
            holder.PartyColour.setBackgroundColor(Color.rgb(212,106,76));
        }
        else if (Party.equals("Sinn Féin")){
            holder.PartyColour.setBackgroundColor(Color.rgb(0,136,0));
        }
        else {
            holder.PartyColour.setBackgroundColor(Color.rgb(0,0,0));
        }
    }

    @Override
    public int getItemCount() {
        return MPList.size();
    }


}
