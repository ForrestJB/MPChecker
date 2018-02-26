package uk.ac.kent.fb224.mpchecker;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by Tree1 on 23/02/2018.
 */

public class BillListAdapter extends RecyclerView.Adapter {
    public ArrayList<Bill> BillList = new ArrayList<Bill>();
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
