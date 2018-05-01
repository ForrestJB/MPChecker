package uk.ac.kent.fb224.mpchecker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Forrest on 30/04/2018.
 */

public class ConListAdapter extends RecyclerView.Adapter<ConListAdapter.ViewHolder>{
    private Context context;

    public ArrayList<Constituency> ConList = new ArrayList<>();
    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView MPName;
        private TextView ConName;
        private TextView Party;
        private ImageView PartyColour;

        private View.OnClickListener OnClick = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPref = context.getSharedPreferences("Main_Pref", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();

                int position = ConListAdapter.ViewHolder.this.getLayoutPosition();
                Constituency tempCon = ConList.get(position);
                editor.putString("User_MP", tempCon.MPName);
                editor.putBoolean("First_Open", false);
                editor.apply();
                Log.d("Your MP", tempCon.MPName);
                Intent intent = new Intent(view.getContext(), LandingActivity.class);
                context.startActivity(intent);
            }
        };
        public ViewHolder(View itemView) {
            super(itemView);
            MPName = itemView.findViewById(R.id.ConSearchMPName);
            ConName = itemView.findViewById(R.id.ConSearchConName);
            Party = itemView.findViewById(R.id.ConSearchParty);
            PartyColour = itemView.findViewById(R.id.ConSearchColor);
            this.itemView.setOnClickListener(OnClick);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        context = parent.getContext();
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.con_card_layout, parent, false);
        ConListAdapter.ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
    Constituency con = ConList.get(position);
    holder.ConName.setText(con.ConName);
    holder.MPName.setText(con.MPName);
    holder.Party.setText(con.Party);
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
        return ConList.size();

    }
}
