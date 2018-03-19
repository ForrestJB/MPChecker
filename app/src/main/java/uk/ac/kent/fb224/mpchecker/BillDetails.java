package uk.ac.kent.fb224.mpchecker;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Forrest on 19/03/2018.
 */

public class BillDetails extends AppCompatActivity {
    private int BillPosition;
    private Bill bill;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.bill_details);

        Intent intent = getIntent();
        BillPosition = intent.getIntExtra("Bill_Position", 0);
        bill = NetManager.getInstance(this).BillList.get(BillPosition);
    }
}
