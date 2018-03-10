package uk.ac.kent.fb224.mpchecker;

import android.os.AsyncTask;
import android.os.DropBoxManager;
import android.util.Log;
import android.util.Xml;
import android.widget.Switch;

import com.android.volley.Cache;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Forrest on 08/03/2018.
 */

public class ElectionXMLParser {
    public DatabaseReference mElectionDatabase;
    public static final String ns = null;
    private ArrayList<Election> ElectionList = new ArrayList<Election>();

    public Election Parse(InputStream in) throws XmlPullParserException, IOException, ParseException {
        XmlPullParser parser = Xml.newPullParser();
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
        parser.setInput(in, null);
        parser.nextTag();
        return readFeed(parser);
    }
    private Election readFeed(XmlPullParser parser) throws XmlPullParserException, IOException, ParseException {
        Election election1 = new Election();

        parser.require(XmlPullParser.START_TAG, ns, "Results");
        while(parser.next() != XmlPullParser.END_TAG){
            if(parser.getEventType() != XmlPullParser.START_TAG){
                continue;
            }
            String name = parser.getName();
            if(name.equals("Results")){
                election1 = (readEntry(parser));
            }
        }
        return election1;

    }
    private Election readEntry(XmlPullParser parser) throws XmlPullParserException, IOException, ParseException {
        parser.require(XmlPullParser.START_TAG, ns, "Results");
        Election election = new Election();
        String order = null;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("Result")) {
                election.Result = parser.getText();
            } else if (name.equals("Candidates")) {
                parser.require(XmlPullParser.START_TAG, ns, "Candidate");
                while (parser.next() != XmlPullParser.END_TAG) {
                    if (parser.getEventType() != XmlPullParser.START_TAG) {
                        continue;
                    }
                    if (parser.getName().equals("order")) {
                        order = parser.getText();
                    }
                    switch (order) {
                        case "1":
                            if (parser.getName().equals("Name")) {
                                election.CandidateOne = parser.getText();
                            } else if (parser.getName().equals("Party")) {
                                election.CandidateOneParty = parser.getText();
                            } else if (parser.getName().equals("Votes")) {
                                election.CandidateOneVotes = parser.getText();
                            }
                        case "2":
                            if (parser.getName().equals("Name")) {
                                election.CandidateTwo = parser.getText();
                            } else if (parser.getName().equals("Party")) {
                                election.CandidateTwoParty = parser.getText();
                            } else if (parser.getName().equals("Votes")) {
                                election.CandidateTwoVotes = parser.getText();
                            }
                        case "3":
                            if (parser.getName().equals("Name")) {
                                election.CandidateThree = parser.getText();
                            } else if (parser.getName().equals("Party")) {
                                election.CandidateThreeParty = parser.getText();
                            } else if (parser.getName().equals("Votes")) {
                                election.CandidateThreeVotes = parser.getText();
                            }
                        case "4":
                            if (parser.getName().equals("Name")) {
                                election.CandidateFour = parser.getText();
                            } else if (parser.getName().equals("Party")) {
                                election.CandidateFourParty = parser.getText();
                            } else if (parser.getName().equals("Votes")) {
                                election.CandidateFourVotes = parser.getText();
                            }
                        case "5":
                            if (parser.getName().equals("Name")) {
                                election.CandidateFive = parser.getText();
                            } else if (parser.getName().equals("Party")) {
                                election.CandidateFiveParty = parser.getText();
                            } else if (parser.getName().equals("Votes")) {
                                election.CandidateFiveVotes = parser.getText();
                            }
                        default:
                    }

                    parser.next();
                }
            }
        }
        return election;
    }

    public ArrayList GetElections(){return ElectionList;}
}
