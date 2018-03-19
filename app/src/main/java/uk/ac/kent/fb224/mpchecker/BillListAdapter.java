package uk.ac.kent.fb224.mpchecker;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Tree1 on 23/02/2018.
 */

public class BillListAdapter extends RecyclerView.Adapter<BillListAdapter.ViewHolder> {
    private Context context;
    public ArrayList<Bill> BillList = new ArrayList<Bill>();

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView Title;
        public TextView Date;
        public TextView Ayes;
        public TextView Noes;

        private View.OnClickListener onClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = BillListAdapter.ViewHolder.this.getLayoutPosition();
                Intent intent = new Intent(v.getContext(), BillDetails.class);
                intent.putExtra("Bill_Position", position);
                context.startActivity(intent);
            }
        };
        public ViewHolder(View itemView) {
            super(itemView);
            Title = itemView.findViewById(R.id.BillName);
            Date = itemView.findViewById(R.id.BillDate);
            Ayes = itemView.findViewById(R.id.BillAyes);
            Noes = itemView.findViewById(R.id.BillNoes);
            this.itemView.setOnClickListener(onClick);
        }
    }
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.bill_card_layout, parent, false);
        BillListAdapter.ViewHolder vh = new BillListAdapter.ViewHolder(v);
        return vh;
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        Bill bill = BillList.get(position);
        holder.Title.setText(bill.Name);
        holder.Ayes.setText("Ayes: "+bill.Ayes);
        holder.Noes.setText("Noes: "+bill.Noes);
        try {
            Date df = new SimpleDateFormat("yyyy-mm-dd").parse(bill.Date);
            SimpleDateFormat formatter = new SimpleDateFormat("E, MMM dd yyyy");
            String ParsedDate = formatter.format(df);
            holder.Date.setText(ParsedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }



    @Override
    public int getItemCount() {
        return BillList.size();
    }
}
