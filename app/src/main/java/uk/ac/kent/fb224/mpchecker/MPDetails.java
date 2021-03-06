// Wiki URL: https://en.wikipedia.org/w/api.php?action=query&prop=extracts&exintro&titles=Theresa%20May&format=json
//for election results: pull first page, lose first 7, pull first 157 of second page
// http://lda.data.parliament.uk/electionresults.json?_view=Elections&_pageSize=500&_sort=-election.label&_page=0
package uk.ac.kent.fb224.mpchecker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static android.provider.ContactsContract.CommonDataKinds.Website.URL;

/**
 * Created by Forrest on 02/03/2018.
 */

public class MPDetails extends AppCompatActivity {
    public DatabaseReference mElectionDatabase;
    public String ElectionURL = null;
    public static Election election = null;
    private RecyclerView BillRecyclerView;
    private LinearLayoutManager layoutManager;
    private MPDetailsBillAdapter adapter;
    public ImageView Image;
    public TextView Name;
    public TextView Role;
    public TextView Party;
    public TextView Bio;
    public TextView Con;
    public TextView ElectionResults;
    public TextView WikiLink;
    public TextView Cand1;
    public TextView Party1;
    public TextView Votes1;
    public TextView Cand2;
    public TextView Party2;
    public TextView Votes2;
    public TextView Cand3;
    public TextView Party3;
    public TextView Votes3;
    public TextView Cand4;
    public TextView Party4;
    public TextView Votes4;
    public TextView Cand5;
    public TextView Party5;
    public TextView Votes5;
    public ImageView Block1;
    public ImageView Block2;
    public ImageView Block3;
    public ImageView Block4;
    public ImageView Block5;
    public TextView EResult;
    public ImageView Emask;
    public ProgressBar ESpinner;
    private ImageView billMask;
    private ProgressBar billSpinner;
    public Button Contact;
    private FloatingActionButton FavButton;
    private boolean pagetwo = true;
    private ProgressBar WikiSpinner;
    private int MPPosition;
    private Constituency MP;
    public boolean isXMLDone = false;
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
        String MPName = intent.getStringExtra("MP Name");
        if(MPName == null){
            MPPosition = intent.getIntExtra("MP_POSITION", 0);
            MP = NetManager.getInstance(this).DetailsConList.get(MPPosition);
        }else{
            for(int i=0;i<NetManager.getInstance(this).conList.size();i++){
                Constituency tempmp = NetManager.getInstance(this).conList.get(i);
                if(i==121){}
                else if(MPName.equals(tempmp.MPName)){
                    MP = tempmp;
                    break;
                }
            }
        }


        Image = findViewById(R.id.MPDetailsImage);
        Name = findViewById(R.id.MPDetailsName);
        Role = findViewById(R.id.MPDetailsRole);
        Party = findViewById(R.id.MPDetailsParty);
        Con = findViewById(R.id.MPDetailsCon);
        Bio = findViewById(R.id.MPWikiBio);
        Contact = findViewById(R.id.MPDetailsContactMP);
        WikiSpinner = findViewById(R.id.MPWikiSpinner);
        WikiLink = findViewById(R.id.MPDWikiLink);
        Cand1 = findViewById(R.id.MPDCand1);
        Party1 = findViewById(R.id.MPDParty1);
        Votes1 = findViewById(R.id.MPDVotes1);
        Cand2 = findViewById(R.id.MPDCand2);
        Party2 = findViewById(R.id.MPDParty2);
        Votes2 = findViewById(R.id.MPDVotes2);
        Cand3 = findViewById(R.id.MPDCand3);
        Party3 = findViewById(R.id.MPDParty3);
        Votes3 = findViewById(R.id.MPDVotes3);
        Cand4 = findViewById(R.id.MPDCand4);
        Party4 = findViewById(R.id.MPDParty4);
        Votes4 = findViewById(R.id.MPDVotes4);
        Cand5 = findViewById(R.id.MPDCand5);
        Party5 = findViewById(R.id.MPDParty5);
        Votes5 = findViewById(R.id.MPDVotes5);
        Block1 = findViewById(R.id.MPDPBlock1);
        Block2 = findViewById(R.id.MPDPBlock2);
        Block3 = findViewById(R.id.MPDPBlock3);
        Block4 = findViewById(R.id.MPDPBlock4);
        Block5 = findViewById(R.id.MPDPBlock5);
        EResult = findViewById(R.id.MPDEResult);
        Emask = findViewById(R.id.MPDEMask);
        ESpinner = findViewById(R.id.MPDESpinner);
        FavButton = findViewById(R.id.MPDetailsFav);
        billMask = findViewById(R.id.MPDetailsMask);
        billSpinner = findViewById(R.id.MPDetailsSpinner);
        Name.setText(MP.MPName);
        Con.setText(MP.ConName);
        Role.setText(MP.MPRole);
        Party.setText(MP.Party);
        BillRecyclerView = (RecyclerView) findViewById(R.id.MPDetailsRecycler);
        layoutManager = new LinearLayoutManager(this) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        pagetwo = false;
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        BillRecyclerView.setLayoutManager(layoutManager);

        final NetManager NetMgr = NetManager.getInstance(getApplicationContext());
        if (MP.MPImageUrl != null) {//check to ensure there is an image to prevent crashes if the URL is null
            NetMgr.imageLoader.get(MP.MPImageUrl, imageListener1);//setup NetManager object, fetch MP image
        }else{NetMgr.imageLoader.get("https://upload.wikimedia.org/wikipedia/commons/7/7c/Profile_avatar_placeholder_large.png", imageListener1);}
        RequestQueue requestQueue = NetMgr.requestQueue;//fetch the request queue
        String WikiURL = "https://en.wikipedia.org/w/api.php?action=query&prop=extracts&exintro&titles=" + MP.MPName + "&format=json";
        WikiURL = WikiURL.replaceAll(" ", "%20");
        final String tempWikiUrl = "https://en.wikipedia.org/wiki/" + MP.MPName;
        WikiLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent Browser = new Intent(Intent.ACTION_VIEW, Uri.parse(tempWikiUrl));
                startActivity(Browser);
            }
        });
        Log.d("email", MP.email);
        Contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", MP.email, null));
                startActivity(Intent.createChooser(emailIntent, "Send Mail...."));
            }
        });
        if(MP.isFav==true){
            FavButton.setColorFilter(Color.rgb(218, 165, 32));
        }
        FavButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MP.isFav == false) {
                    FavButton.setColorFilter(Color.rgb(218, 165, 32));
                    MP.isFav = true;
                    SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("Main_Pref", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    int key = sharedPref.getInt("MPNumber", 0);
                    editor.putString("MP"+MP.id, MP.ConName);
                    editor.putInt("MPNumber", key+1);
                    editor.apply();
                    NetManager.conFavList.add(MP);

                }
                else{
                    FavButton.setColorFilter(null);
                    SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("Main_Pref", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    int key = sharedPref.getInt("MPNumber", 0);
                    editor.remove("MP"+MP.id);
                    editor.putInt("MPNumber", key-1);
                    editor.apply();
                    MP.isFav = false;
                    NetManager.conFavList.remove(MP);
                }
            }
        });
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
                                Log.d("bio", Extract); //todo log
                                String RawExtract = stripHtml(Extract);
                                int ExtractLength = RawExtract.length();

                                if (ExtractLength > 250) {
                                    String ShortRawExtract = RawExtract.substring(0, 250);
                                    Bio.setText(ShortRawExtract);
                                } else {
                                    Bio.setText(RawExtract);
                                }
                                Bio.append("....");
                                WikiSpinner.setVisibility(View.GONE);
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
        for(int i=0; i<=649;i++){
            mElectionDatabase = FirebaseDatabase.getInstance().getReference().child("Raw Data").child("Elections").child(Integer.toString(i));
            ValueEventListener ElectionListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Election ESearcher = dataSnapshot.getValue(Election.class);
                    if (ESearcher.Con.equals(MP.ConName)) {
                        String ElectionURLT = ESearcher.URL;
                        ElectionURL = ElectionURLT + ".XML";
                        loadPage();
                    }
                }
                    @Override
                    public void onCancelled (DatabaseError databaseError){

                    }
                }

                ;mElectionDatabase.addValueEventListener(ElectionListener);
            };
        doBills();
        //again, this is the code that was used to pull the election results from the api adn store them onto my database
        //although not used at the minute, it is left incase I need to update the database in the future

//        String ElectionURL = "http://lda.data.parliament.uk/electionresults.json?_view=Elections&_pageSize=500&_sort=-election.label&_page=0";
//        String ElectionURL2 = "http://lda.data.parliament.uk/electionresults.json?_view=Elections&_pageSize=500&_sort=-election.label&_page=1";
//        JsonObjectRequest ERequest = new JsonObjectRequest(Request.Method.GET, ElectionURL2, null, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                try {
//                    JSONObject result = response.getJSONObject("result");
//                    JSONArray items = result.getJSONArray("items");
//                    int count = 492; //todo remove this
//                    for (int i = 0; i <= 157; i++) {
//                        JSONObject Election = items.getJSONObject(i);
//                        JSONObject Constitency = Election.getJSONObject("constituency");
//                        JSONObject label = Constitency.getJSONObject("label");todo redundant, remove?
//                        String Value = label.getString("_value");
//                        String RawURL = Election.getString("_about");
//                        String id = RawURL.substring(36);
//                        String XMLURL = "http://api.data.parliament.uk/resources/files/"+id;
//                        mDatabase = FirebaseDatabase.getInstance().getReference();
//                        mDatabase.child("Raw Data").child("Elections").child(Integer.toString(count)).child("Con").setValue(Value);
//                        mDatabase.child("Raw Data").child("Elections").child(Integer.toString(count)).child("URL").setValue(XMLURL);
//                        count++;
//                    }
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//
//            }
//        }); requestQueue.add(ERequest);

    }
    public void doBills(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                adapter = new MPDetailsBillAdapter();
                NetManager.getInstance(MPDetails.this).BillList = NetManager.getInstance(MPDetails.this).StaticBillList; // reset the billist so we can set new values for MPVote, which keeps track of how this mp voted for each bill
                for(int i=0;i<NetManager.getInstance(MPDetails.this).BillList.size();i++){
                    Bill tempBill = NetManager.getInstance(MPDetails.this).BillList.get(i);
                    Log.d("bill number", Integer.toString(i));
                    for(int j=0;j<tempBill.VoteAyeList.size();j++){
                        Vote tempVote = tempBill.VoteAyeList.get(j);
                        if(MP.ConName.equals(tempVote.Con)){
                            NetManager.getInstance(MPDetails.this).BillList.get(i).MPVote = "AyeVote";
                            break;
                        }
                    }
                    for(int x=0;x<tempBill.VoteNoeList.size();x++){
                        Vote tempVote = tempBill.VoteNoeList.get(x);
                        if(MP.ConName.equals(tempVote.Con)){
                            NetManager.getInstance(MPDetails.this).BillList.get(i).MPVote = "NoVote";
                            break;
                        }
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.BillList = NetManager.getInstance(MPDetails.this).BillList;
                        BillRecyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        billMask.setVisibility(View.GONE);
                        billSpinner.setVisibility(View.GONE);
                    }
                });

            }
        }).start();

    }
    public Election loadElection(String ElectionURL) throws ParseException, XmlPullParserException, IOException {
        InputStream stream;
        ElectionXMLParser electionXMLParser = new ElectionXMLParser();
        Election electiona = null;
        try {
            Log.d("URL", ElectionURL);
            stream = DownloadURL(ElectionURL);
            electiona = electionXMLParser.Parse(stream);
            Log.d("success!!", electiona.CandidateThree);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        return electiona;
    }
    public void applyElection(Election election){
        Cand1.setText(election.CandidateOne);
        Cand2.setText(election.CandidateTwo);
        Cand3.setText(election.CandidateThree);
        Cand4.setText(election.CandidateFour);
        Cand5.setText(election.CandidateFive);
        Party1.setText(election.CandidateOneParty);
        if(election.CandidateOneParty.equals("Con")) {
            Block1.setBackgroundColor(Color.rgb(0,135, 220));
        }
        else if (election.CandidateOneParty.equals("Lab")){
            Block1.setBackgroundColor(Color.rgb(146,0,13));
        }
        else if (election.CandidateOneParty.equals("LD")){
            Block1.setBackgroundColor(Color.rgb(253,187,48));
        }
        else if(election.CandidateOneParty.equals("SNP")){
            Block1.setBackgroundColor(Color.rgb(255,249,93));
        }
        else if (election.CandidateOneParty.equals("Plaid Cymru")){
            Block1.setBackgroundColor(Color.rgb(63,132,40));
        }
        else if (election.CandidateOneParty.equals("Green")) {
            Block1.setBackgroundColor(Color.rgb(0,116,95));
        }
        else if (election.CandidateOneParty.equals("DUP")){
            Block1.setBackgroundColor(Color.rgb(212,106,76));
        }
        else if (election.CandidateOneParty.equals("SF")){
            Block1.setBackgroundColor(Color.rgb(0,136,0));
        }
        else if (election.CandidateOneParty.equals("Speaker")){
            Block1.setBackgroundColor(Color.rgb(0,0,0));
        }
        else if (election.CandidateOneParty.equals("UKIP")){
            Block1.setBackgroundColor(Color.rgb(112,20,122));
        }
        else {Block1.setBackgroundColor(Color.rgb(0,0,0));}
        Party2.setText(election.CandidateTwoParty);
        if(election.CandidateTwoParty.equals("Con")) {
            Block2.setBackgroundColor(Color.rgb(0,135, 220));
        }
        else if (election.CandidateTwoParty.equals("Lab")){
            Block2.setBackgroundColor(Color.rgb(146,0,13));
        }
        else if (election.CandidateTwoParty.equals("LD")){
            Block2.setBackgroundColor(Color.rgb(253,187,48));
        }
        else if(election.CandidateTwoParty.equals("SNP")){
            Block2.setBackgroundColor(Color.rgb(255,249,93));
        }
        else if (election.CandidateTwoParty.equals("Plaid Cymru")){
            Block2.setBackgroundColor(Color.rgb(63,132,40));
        }
        else if (election.CandidateTwoParty.equals("Green")) {
            Block2.setBackgroundColor(Color.rgb(0,116,95));
        }
        else if (election.CandidateTwoParty.equals("DUP")){
            Block2.setBackgroundColor(Color.rgb(212,106,76));
        }
        else if (election.CandidateTwoParty.equals("SF")){
            Block2.setBackgroundColor(Color.rgb(0,136,0));
        }
        else if (election.CandidateTwoParty.equals("Speaker")){
            Block2.setBackgroundColor(Color.rgb(0,0,0));
        }
        else if (election.CandidateOneParty.equals("UKIP")){
            Block2.setBackgroundColor(Color.rgb(112,20,122));
        }
        else {Block2.setBackgroundColor(Color.rgb(0,0,0));}
        Party3.setText(election.CandidateThreeParty);
        if(election.CandidateThreeParty.equals("Con")) {
            Block3.setBackgroundColor(Color.rgb(0,135, 220));
        }
        else if (election.CandidateThreeParty.equals("Lab")){
            Block3.setBackgroundColor(Color.rgb(146,0,13));
        }
        else if (election.CandidateThreeParty.equals("LD")){
            Block3.setBackgroundColor(Color.rgb(253,187,48));
        }
        else if(election.CandidateThreeParty.equals("SNP")){
            Block3.setBackgroundColor(Color.rgb(255,249,93));
        }
        else if (election.CandidateThreeParty.equals("Plaid Cymru")){
            Block3.setBackgroundColor(Color.rgb(63,132,40));
        }
        else if (election.CandidateThreeParty.equals("Green")) {
            Block3.setBackgroundColor(Color.rgb(0,116,95));
        }
        else if (election.CandidateThreeParty.equals("DUP")){
            Block3.setBackgroundColor(Color.rgb(212,106,76));
        }
        else if (election.CandidateThreeParty.equals("SF")){
            Block3.setBackgroundColor(Color.rgb(0,136,0));
        }
        else if (election.CandidateThreeParty.equals("Speaker")){
            Block3.setBackgroundColor(Color.rgb(0,0,0));
        }
        else if (election.CandidateOneParty.equals("UKIP")){
            Block3.setBackgroundColor(Color.rgb(112,20,122));
        }
        else {Block3.setBackgroundColor(Color.rgb(0,0,0));}
        Party4.setText(election.CandidateFourParty);
        if(election.CandidateFourParty != null){
        if(election.CandidateFourParty.equals("Con")) {
            Block4.setBackgroundColor(Color.rgb(0,135, 220));
        }
        else if (election.CandidateFourParty.equals("Lab")){
            Block4.setBackgroundColor(Color.rgb(146,0,13));
        }
        else if (election.CandidateFourParty.equals("LD")){
            Block4.setBackgroundColor(Color.rgb(253,187,48));
        }
        else if(election.CandidateFourParty.equals("SNP")){
            Block4.setBackgroundColor(Color.rgb(255,249,93));
        }
        else if (election.CandidateFourParty.equals("Plaid Cymru")){
            Block4.setBackgroundColor(Color.rgb(63,132,40));
        }
        else if (election.CandidateFourParty.equals("Green")) {
            Block4.setBackgroundColor(Color.rgb(0,116,95));
        }
        else if (election.CandidateFourParty.equals("DUP")){
            Block4.setBackgroundColor(Color.rgb(212,106,76));
        }
        else if (election.CandidateFourParty.equals("SF")){
            Block4.setBackgroundColor(Color.rgb(0,136,0));
        }
        else if (election.CandidateFourParty.equals("Speaker")){
            Block4.setBackgroundColor(Color.rgb(0,0,0));
        }
        else {Block4.setBackgroundColor(Color.rgb(0,0,0));}}
        if(election.CandidateFiveParty != null){
        Party5.setText(election.CandidateFiveParty);
        if(election.CandidateFiveParty.equals("Con")) {
            Block5.setBackgroundColor(Color.rgb(0,135, 220));
        }
        else if (election.CandidateFiveParty.equals("Lab")){
            Block5.setBackgroundColor(Color.rgb(146,0,13));
        }
        else if (election.CandidateFiveParty.equals("LD")){
            Block5.setBackgroundColor(Color.rgb(253,187,48));
        }
        else if(election.CandidateFiveParty.equals("SNP")){
            Block5.setBackgroundColor(Color.rgb(255,249,93));
        }
        else if (election.CandidateFiveParty.equals("Plaid Cymru")){
            Block5.setBackgroundColor(Color.rgb(63,132,40));
        }
        else if (election.CandidateFiveParty.equals("Green")) {
            Block5.setBackgroundColor(Color.rgb(0,116,95));
        }
        else if (election.CandidateFiveParty.equals("DUP")){
            Block5.setBackgroundColor(Color.rgb(212,106,76));
        }
        else if (election.CandidateFiveParty.equals("SF")){
            Block5.setBackgroundColor(Color.rgb(0,136,0));
        }
        else if (election.CandidateFiveParty.equals("Speaker")){
            Block5.setBackgroundColor(Color.rgb(0,0,0));
        }
        else if (election.CandidateOneParty.equals("UKIP")){
            Block5.setBackgroundColor(Color.rgb(112,20,122));
        }
        else {Block5.setBackgroundColor(Color.rgb(0,0,0));}}
        Votes1.setText(election.CandidateOneVotes);
        Votes2.setText(election.CandidateTwoVotes);
        Votes3.setText(election.CandidateThreeVotes);
        Votes4.setText(election.CandidateFourVotes);
        Votes5.setText(election.CandidateFiveVotes);
        EResult.setText(election.Result);
        Emask.setVisibility(View.GONE);
        ESpinner.setVisibility(View.GONE);
    }
    private class DownloadXMLTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {
            try {
                election = loadElection(ElectionURL);
                return null;
            }  catch (XmlPullParserException e) {
                return null;
            } catch (IOException e) {
                return null;
            } catch (ParseException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            applyElection(election);
        }
    }
    private void loadPage() {
            new DownloadXMLTask().execute(ElectionURL);
    }
    private InputStream DownloadURL(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(15000 /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        // Starts the query
        conn.connect();
        InputStream stream = conn.getInputStream();
        return stream;
    }
    public String stripHtml(String html) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            return Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY).toString();
        } else {
            return Html.fromHtml(html).toString();
        }
    }
}

