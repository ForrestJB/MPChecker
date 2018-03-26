package uk.ac.kent.fb224.mpchecker;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Forrest on 07/03/2018.
 */

public class MPDetailsBillAdapter extends RecyclerView.Adapter<MPDetailsBillAdapter.ViewHolder> {
    private Context context;
    public ArrayList<Bill> BillList = new ArrayList<Bill>();
    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView Title;
        public TextView Date;
        public TextView Ayes;
        public TextView Noes;
        public TextView MPVote;
        private View.OnClickListener onClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = MPDetailsBillAdapter.ViewHolder.this.getLayoutPosition();
                Intent intent = new Intent(v.getContext(), BillDetails.class);
                intent.putExtra("Bill_Position", position);
                context.startActivity(intent);
            }
        };
        public ViewHolder(View itemView) {
            super(itemView);
            Title = itemView.findViewById(R.id.MPDBillName);
            Date = itemView.findViewById(R.id.MPDBillDate);
            Ayes = itemView.findViewById(R.id.MPDBillAyes);
            Noes = itemView.findViewById(R.id.MPDBillNoes);
            MPVote = itemView.findViewById(R.id.MPDVote);
            this.itemView.setOnClickListener(onClick);
        }
    }
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        context = parent.getContext();
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.mp_details_bill_card_layout, parent, false);
        MPDetailsBillAdapter.ViewHolder vh = new ViewHolder(v);
        return vh;
    }


    public void onBindViewHolder(MPDetailsBillAdapter.ViewHolder holder, int position) {
        Bill bill = BillList.get(position);
        holder.Title.setText(bill.Name);
        holder.Ayes.setText("Ayes: "+bill.Ayes);
        holder.Noes.setText("Noes: "+bill.Noes);
        try {
            Date df = new SimpleDateFormat("yyyy-mm-dd").parse(bill.Date);
            SimpleDateFormat formatter = new SimpleDateFormat("E, MMM dd yyyy");
            String ParsedDate = formatter.format(df);
            holder.Date.setText(ParsedDate);
            if(bill.MPVote != null){
                if(bill.MPVote.equals("AyeVote")){
                    holder.MPVote.setText("Aye");
                }
                else if(bill.MPVote.equals("NoVote")){
                    holder.MPVote.setText("No");
                }
            }
            else{ holder.MPVote.setText("Abstain");}
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return BillList.size();
    }
}

