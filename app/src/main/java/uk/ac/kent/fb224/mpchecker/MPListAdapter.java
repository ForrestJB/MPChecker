package uk.ac.kent.fb224.mpchecker;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
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
 * Created by Tree1 on 29/01/2018.
 */

public class MPListAdapter extends RecyclerView.Adapter<MPListAdapter.ViewHolder> {
    ImageView MPPartyColour;
    private Context context;
    @Override
    public void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(hasStableIds);
    }

    public ArrayList<Constituency> ConList = new ArrayList<Constituency>();
    public class ViewHolder extends RecyclerView.ViewHolder {
        NetworkImageView MPThumb;
        TextView MPName;
        TextView MPPartyName;
        TextView MPConName;
        TextView MPRole;
        FloatingActionButton FavButton;

        private View.OnClickListener onClick = new View.OnClickListener() {
            @Override
            public void onClick(View view) {//onclick to open MP profile page when a card is clicked
                int position = ViewHolder.this.getLayoutPosition();
                Intent intent = new Intent(view.getContext(), MPDetails.class);
                intent.putExtra("MP_POSITION", position);
                context.startActivity(intent);
            }
        };
        public ViewHolder(View itemView) {
            super(itemView);
            MPThumb = itemView.findViewById(R.id.BVMPThumb);
            MPPartyColour = itemView.findViewById(R.id.BVMPPartyColour);
            MPName = itemView.findViewById(R.id.BVMPName);
            MPPartyName = itemView.findViewById(R.id.BVMPPartyName);
            MPConName = itemView.findViewById(R.id.MPConName);
            MPRole = itemView.findViewById(R.id.MPRole);
            FavButton = itemView.findViewById(R.id.MPFav);
            this.itemView.setOnClickListener(onClick);
            FavButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) { // handle what to do when the Favourite button is pressed
                    Constituency con = ConList.get(getAdapterPosition());
                    if (con.isFav == false) {
                        FavButton.setColorFilter(Color.rgb(218, 165, 32));
                        Log.d("con pulled", con.MPName);//todo: remove debug
                        con.isFav = true;
                        NetManager.conFavList.add(con);
                        notifyDataSetChanged();

                    }
                    else{
                        FavButton.setColorFilter(null);
                        con.isFav = false;
                        NetManager.conFavList.remove(con);
                        notifyDataSetChanged();
                    }
                }
            });
        }
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View v = LayoutInflater.from(context).inflate(R.layout.mp_card_layout, parent, false);
        MPListAdapter.ViewHolder vh = new MPListAdapter.ViewHolder(v);
        return vh;
    }

    public void onBindViewHolder(ViewHolder holder, int position){
        Constituency con = ConList.get(position);
        holder.setIsRecyclable(false);
        holder.MPName.setText(con.MPName);
        holder.MPPartyName.setText(con.Party);
        if(con.Party.equals("Conservative")) {
            MPPartyColour.setBackgroundColor(Color.rgb(0,135, 220));
        }
        else if (con.Party.equals("Labour")){
            MPPartyColour.setBackgroundColor(Color.rgb(146,0,13));
        }
        else if (con.Party.equals("Labour/Co-operative")){
            MPPartyColour.setBackgroundColor(Color.rgb(146,0,14));
        }
        else if (con.Party.equals("Liberal Democrat")){
            MPPartyColour.setBackgroundColor(Color.rgb(253,187,48));
        }
        else if(con.Party.equals("Scottish National Party")){
            MPPartyColour.setBackgroundColor(Color.rgb(255,249,93));
        }
        else if (con.Party.equals("Plaid Cymru")){
            MPPartyColour.setBackgroundColor(Color.rgb(63,132,40));
        }
        else if (con.Party.equals("Green")) {
            MPPartyColour.setBackgroundColor(Color.rgb(0,116,95));
        }
        else if (con.Party.equals("DUP")){
            MPPartyColour.setBackgroundColor(Color.rgb(212,106,76));
        }
        else if (con.Party.equals("Sinn Féin")){
            MPPartyColour.setBackgroundColor(Color.rgb(0,136,0));
        }
        else if (con.Party.equals("Speaker")){
            MPPartyColour.setBackgroundColor(Color.rgb(0,0,0));
        }
        holder.MPRole.setText(con.MPRole);
        holder.MPThumb.setImageUrl(con.MPImageUrl, NetManager.getInstance(context).imageLoader);
        if (con.isFav == true){
            holder.FavButton.setColorFilter(Color.rgb(218, 165, 32));
        }
        else{ holder.FavButton.setColorFilter(null);}
    }

    @Override
    public int getItemCount() {
        return ConList.size();
    }
}
