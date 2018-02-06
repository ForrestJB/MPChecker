package uk.ac.kent.fb224.mpchecker;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Tree1 on 29/01/2018.
 */

public class MPListAdapter extends RecyclerView.Adapter<MPListAdapter.ViewHolder> {
    public ArrayList<Constituency> ConList = new ArrayList<Constituency>();
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView MPThumb;
        ImageView MPPartyColour;
        TextView MPName;
        TextView MPPartyName;
        TextView MPConName;
        TextView MPRole;
        FloatingActionButton FavButton;

        public ViewHolder(View itemView) {
            super(itemView);
            MPThumb = itemView.findViewById(R.id.MPThumb);
            MPPartyColour = itemView.findViewById(R.id.MPPartyColour);
            MPName = itemView.findViewById(R.id.MPName);
            MPPartyName = itemView.findViewById(R.id.MPPartyName);
            MPConName = itemView.findViewById(R.id.MPConName);
            MPRole = itemView.findViewById(R.id.MPRole);
            FavButton = itemView.findViewById(R.id.MPFav);

            FavButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //what to do when fav button is pressed
                }
            });
        }
    }
private Context context;
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.mp_card_layout, parent, false);
        MPListAdapter.ViewHolder vh = new MPListAdapter.ViewHolder(v);
        return vh;
    }

    public void onBindViewHolder(ViewHolder holder, int position){
        Constituency con = ConList.get(position);

        holder.MPName.setText(con.MPName);
        holder.MPPartyName.setText(con.Party);
        holder.MPRole.setText(con.MPRole);
        //todo: images
    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
