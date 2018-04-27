package uk.ac.kent.fb224.mpchecker;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Forrest on 19/03/2018.
 */

public class BillDetails extends AppCompatActivity {
    private int BillPosition;
    private Bill bill;
    private TextView Title;
    private TextView Date;
    private TextView Desc;
    private TextView Ayes;
    private TextView Noes;
    private ImageView AyeOC;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.bill_details);

        Intent intent = getIntent();
        BillPosition = intent.getIntExtra("Bill_Position", 0);
        bill = NetManager.getInstance(this).BillList.get(BillPosition);
        Title = findViewById(R.id.BDTitle);
        Date = findViewById(R.id.BDDate);
        Desc = findViewById(R.id.BDDesc);
        Ayes = findViewById(R.id.BDAyes);
        Noes = findViewById(R.id.BDNoes);
        AyeOC = findViewById(R.id.BDAyeOCL);


        Title.setText(bill.Name);
        try {
            java.util.Date df = new SimpleDateFormat("yyyy-mm-dd").parse(bill.Date);
            SimpleDateFormat formatter = new SimpleDateFormat("E, MMM dd yyyy");
            String ParsedDate = formatter.format(df);
            Date.setText(ParsedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(bill.Desc != null){
            Desc.setText(bill.Desc);
        }
        Ayes.setText(Integer.toString(bill.Ayes));
        Noes.setText(Integer.toString(bill.Noes));

        AyeOC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), BillVotes.class);
                intent.putExtra("Bill_Position", BillPosition);
                startActivity(intent);
            }
        });
    }
}
