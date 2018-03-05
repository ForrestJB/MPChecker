// Wiki URL: https://en.wikipedia.org/w/api.php?action=query&prop=extracts&exintro&titles=Theresa%20May&format=json
package uk.ac.kent.fb224.mpchecker;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

/**
 * Created by Forrest on 02/03/2018.
 */

public class MPDetails extends AppCompatActivity {
    public ImageLoader.ImageContainer GetBit;
    public ImageView Image;
    public TextView Name;
    public TextView Role;
    public TextView Party;
    public TextView Bio;
    public TextView Con;
    public TextView ElectionResults;
    public Button Contact;
    private int MPPosition;
    private Constituency MP;
    private ImageLoader.ImageListener imageListener1 = new ImageLoader.ImageListener() {
        @Override
        public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {//
            if (response.getBitmap() !=null){
                Image.setImageBitmap(response.getBitmap());
            }
        }

        @Override
        public void onErrorResponse(VolleyError error) {
            Toast.makeText(getApplicationContext(), "Network error, please check your internet connection", Toast.LENGTH_LONG).show();
        }
    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.mp_details_page);

        Intent intent = getIntent();
        MPPosition = intent.getIntExtra("MP_POSITION", 0);
        MP = NetManager.getInstance(this).DetailsConList.get(MPPosition);

        Image = findViewById(R.id.MPDetailsImage);
        Name = findViewById(R.id.MPDetailsName);
        Role = findViewById(R.id.MPDetailsRole);
        Party = findViewById(R.id.MPDetailsParty);
        Con = findViewById(R.id.MPDetailsCon);
        Bio = findViewById(R.id.MPWikiBio);
        ElectionResults = findViewById(R.id.MPElectionResults);
        Contact = findViewById(R.id.MPDetailsContactMP);

        Name.setText(MP.MPName);
        Con.setText(MP.ConName);
        Role.setText(MP.MPRole);
        Party.setText(MP.Party);
        final NetManager NetMgr = NetManager.getInstance(getApplicationContext());
        if (MP.MPImageUrl != null) {//check to ensure there is an image to prevent crashes if the URL is null
            NetMgr.imageLoader.get(MP.MPImageUrl, imageListener1);//setup NetManager object, fetch MP image
        }
    }
}

