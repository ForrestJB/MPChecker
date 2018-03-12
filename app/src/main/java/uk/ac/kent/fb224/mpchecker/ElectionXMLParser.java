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
    private Election election;
    private int order = 1;

    public Election Parse(InputStream in) throws XmlPullParserException, IOException, ParseException {
        Log.d("XML", "called parse");
        XmlPullParser parser = Xml.newPullParser();
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
        parser.setInput(in, null);
        parser.nextTag();
        return readFeed(parser);
    }

    private Election readFeed(XmlPullParser parser) throws XmlPullParserException, IOException, ParseException {
        Log.d("XML", "called readFeed");
        Election election = new Election();

        parser.require(XmlPullParser.START_TAG, ns, "Results");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            Log.d("XML", name);
            if (name.equals("Candidates")) {
                election = readEntry(parser);
            } else if (name.equals("Result")) {
                election.Result = parser.getText();
                skip(parser);
            } else {
                skip(parser);
            }
        }
        return election;

    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }

    private Election readEntry(XmlPullParser parser) throws XmlPullParserException, IOException, ParseException {
        Log.d("XML", "called readEntry");
        Election election1 = new Election();
        String order = null;
        String name = parser.getName();
        if (name.equals("Candidates")) {
            Log.d("XML", "got candidates");
            election1 = readCandidate(parser);
        }

        return election1;
    }

    public Election readCandidate(XmlPullParser parser) throws IOException, XmlPullParserException {
        Log.d("XML", "called readCandidate");
        Election election1 = new Election();
        String name = parser.getName();
        if(name.equals("Candidates")) {
            parser.nextTag();
        }
        parser.require(XmlPullParser.START_TAG, ns, "Candidate");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;}
                if(parser.getName().equals("Candidate")){
                parser.nextTag();
                }
                switch (order) {
                    case 1:
                        if (parser.getName().equals("Name")) {
                            election1.CandidateOne = parser.getText();
                        } else if (parser.getName().equals("Party")) {
                            election1.CandidateOneParty = parser.getText();
                        } else if (parser.getName().equals("Votes")) {
                            election1.CandidateOneVotes = parser.getText();
                            order++;
                        }
                    case 2:
                        if (parser.getName().equals("Name")) {
                            election1.CandidateTwo = parser.getText();
                            Log.d("output", election1.CandidateTwo);
                        } else if (parser.getName().equals("Party")) {
                            election1.CandidateTwoParty = parser.getText();
                        } else if (parser.getName().equals("Votes")) {
                            election1.CandidateTwoVotes = parser.getText();
                            order++;
                        }
                    case 3:
                        if (parser.getName().equals("Name")) {
                            election1.CandidateThree = parser.getText();
                        } else if (parser.getName().equals("Party")) {
                            election1.CandidateThreeParty = parser.getText();
                        } else if (parser.getName().equals("Votes")) {
                            election1.CandidateThreeVotes = parser.getText();
                            order++;
                        }
                    case 4:
                        if (parser.getName().equals("Name")) {
                            election1.CandidateFour = parser.getText();
                        } else if (parser.getName().equals("Party")) {
                            election1.CandidateFourParty = parser.getText();
                        } else if (parser.getName().equals("Votes")) {
                            election1.CandidateFourVotes = parser.getText();
                            order++;
                        }
                    case 5:
                        if (parser.getName().equals("Name")) {
                            election1.CandidateFive = parser.getText();
                        } else if (parser.getName().equals("Party")) {
                            election1.CandidateFiveParty = parser.getText();
                        } else if (parser.getName().equals("Votes")) {
                            election1.CandidateFiveVotes = parser.getText();
                            order++;
                        }
                    default:
                }
            }
            parser.nextTag();


        return election1;
    }
}
