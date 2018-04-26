package uk.ac.kent.fb224.mpchecker;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Tree1 on 19/04/2018.
 */

public class About extends AppCompatActivity{
    private TextView Contact;
    private ImageView Parliament;
    private ImageView TWFY;
    private ImageView Wiki;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.about);
        Contact = findViewById(R.id.AboutContact);
        Parliament = findViewById(R.id.AboutParliament);
        TWFY = findViewById(R.id.AboutTWFY);
        Wiki = findViewById(R.id.AboutWiki);

        Contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "FJBargewell@gmail.com", null));
                startActivity(Intent.createChooser(emailIntent, "Send Mail...."));
            }
        });
        Parliament.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Browser = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.data.parliament.uk/"));
                startActivity(Browser);
            }
        });
        TWFY.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Browser = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.theyworkforyou.com/api/"));
                startActivity(Browser);
            }
        });
        Wiki.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Browser = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.mediawiki.org/wiki/API:Main_page"));
                startActivity(Browser);
            }
        });
    }
}
