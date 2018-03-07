// Wiki URL: https://en.wikipedia.org/w/api.php?action=query&prop=extracts&exintro&titles=Theresa%20May&format=json
package uk.ac.kent.fb224.mpchecker;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import static android.provider.ContactsContract.CommonDataKinds.Website.URL;

/**
 * Created by Forrest on 02/03/2018.
 */

public class MPDetails extends AppCompatActivity {
    private RecyclerView BillRecyclerView;
    private RecyclerView MPRecyclerView;
    private LinearLayoutManager layoutManager;
    private MPDetailsBillAdapter adapter;
    public ImageLoader.ImageContainer GetBit;
    public ImageView Image;
    public TextView Name;
    public TextView Role;
    public TextView Party;
    public TextView Bio;
    public TextView Con;
    public TextView ElectionResults;
    public Button Contact;
    private ProgressBar WikiSpinner;
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
        WikiSpinner = findViewById(R.id.MPWikiSpinner);

        Name.setText(MP.MPName);
        Con.setText(MP.ConName);
        Role.setText(MP.MPRole);
        Party.setText(MP.Party);
        BillRecyclerView = (RecyclerView) findViewById(R.id.MPDetailsRecycler);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        BillRecyclerView.setLayoutManager(layoutManager);
        adapter = new MPDetailsBillAdapter();
        adapter.BillList = NetManager.getInstance(this).MPDetailsBillList;
        BillRecyclerView.setAdapter(adapter);
        final NetManager NetMgr = NetManager.getInstance(getApplicationContext());
        if (MP.MPImageUrl != null) {//check to ensure there is an image to prevent crashes if the URL is null
            NetMgr.imageLoader.get(MP.MPImageUrl, imageListener1);//setup NetManager object, fetch MP image
        }
        RequestQueue requestQueue = NetMgr.requestQueue;//fetch the request queue
        String WikiURL = "https://en.wikipedia.org/w/api.php?action=query&prop=extracts&exintro&titles=" + MP.MPName + "&format=json";
        WikiURL = WikiURL.replaceAll(" ", "%20");
        Log.d("URL", WikiURL);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, WikiURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject query = response.getJSONObject("query");
                            JSONObject pages = query.getJSONObject("pages");
                            Iterator iterator = pages.keys();
                            while (iterator.hasNext()) {
                                String key = (String) iterator.next();
                                JSONObject page = pages.getJSONObject(key);
                                String Extract = page.getString("extract");
                                Log.d("bio", Extract);
                                String RawExtract = stripHtml(Extract);
                                WikiSpinner.setVisibility(View.GONE);
                                Bio.setText(RawExtract);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Wiki Error", "WIki Error"); //Todo proper error response
            }
        });
        requestQueue.add(request);
        getBills();
    }

    public String stripHtml(String html) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            return Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY).toString();
        } else {
            return Html.fromHtml(html).toString();
        }
    }

    public void getBills() {
        String BillID = null;
        NetManager NetMgr = NetManager.getInstance(getApplicationContext());
        RequestQueue requestQueue = NetMgr.requestQueue;
        String BillURL = "http://lda.data.parliament.uk/commonsdivisions.json?_view=Commons+Divisions&_pageSize=30&_page=0";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, BillURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject result = response.getJSONObject("result");
                    JSONArray items = result.getJSONArray("items");
                    for (int i = 0; i < items.length(); i++) {
                        JSONObject vote = items.getJSONObject(i);

                        String VoteURL = vote.getString("_about");
                        String BillID = VoteURL.substring(36);
                        getVotes(BillID);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Network error, please check your internet connection", Toast.LENGTH_LONG).show();//if there is a reponse error, notify the user
            }
        });
        requestQueue.add(request);
    }
        public void getVotes(String BillID){
            NetManager NetMgr = NetManager.getInstance(getApplicationContext());
            RequestQueue requestQueue = NetMgr.requestQueue;
            String BillURL = "http://lda.data.parliament.uk/commonsdivisions/id/"+BillID+".json";
            final String finalBillID = BillID;
            JsonObjectRequest request2 = new JsonObjectRequest(Request.Method.GET, BillURL, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    final Bill NewBill = new Bill();
                    NewBill.ID = finalBillID;
                    try {
                        JSONObject result = response.getJSONObject("result");
                        JSONObject PrimTopic = result.getJSONObject("primaryTopic");
                        JSONArray AbstainArray = PrimTopic.getJSONArray("AbstainCount");
                        int AbstainCount = 0;
                        for (int i = 0; i < AbstainArray.length(); i++) {
                            JSONObject AbCont = AbstainArray.getJSONObject(i);
                            AbstainCount = AbCont.getInt("_value");
                            Log.d("abstains", Integer.toString(AbstainCount));
                        }
                        JSONArray AyeArray = PrimTopic.getJSONArray("AyesCount");
                        int AyeCount = 0;
                        for (int i = 0; i < AyeArray.length(); i++) {
                            JSONObject AyeCont = AyeArray.getJSONObject(i);
                            AyeCount = AyeCont.getInt("_value");
                            Log.d("Ayes", Integer.toString(AyeCount));
                        }
                        JSONArray NoeArray = PrimTopic.getJSONArray("Noesvotecount");
                        int NoeCount = 0;
                        for (int i = 0; i < NoeArray.length(); i++) {
                            JSONObject NoeCont = NoeArray.getJSONObject(i);
                            NoeCount = NoeCont.getInt("_value");
                            Log.d("Noes", Integer.toString(NoeCount));
                        }
                        NewBill.Abstains = AbstainCount;
                        NewBill.Ayes = AyeCount;
                        NewBill.Noes = NoeCount;
                        NewBill.Name = PrimTopic.getString("title");
                        JSONObject Dateobj = PrimTopic.getJSONObject("date");
                        NewBill.Date = Dateobj.getString("_value");
                        Log.d("title", NewBill.Name);
                        NetManager.getInstance(MPDetails.this).MPDetailsBillList.add(NewBill);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    adapter.notifyDataSetChanged();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), "Network error, please check your internet connection", Toast.LENGTH_LONG).show();//if there is a reponse error, notify the user

                }
            });
            requestQueue.add(request2);
        }


}

