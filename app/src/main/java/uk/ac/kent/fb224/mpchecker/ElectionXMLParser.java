package uk.ac.kent.fb224.mpchecker;

import android.os.DropBoxManager;
import android.util.Xml;
import android.widget.Switch;

import com.android.volley.Cache;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Forrest on 08/03/2018.
 */

public class ElectionXMLParser {
    public static final String ns = null;
    private ArrayList<Election> ElectionList = new ArrayList<Election>();
    public List parse(InputStream in) throws XmlPullParserException, IOException, ParseException {
        XmlPullParser parser = Xml.newPullParser();
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
        parser.setInput(in, null);
        parser.nextTag();
        return readFeed(parser);
    }
    private List readFeed(XmlPullParser parser) throws XmlPullParserException, IOException, ParseException {
        List entries = new ArrayList();

        parser.require(XmlPullParser.START_TAG, ns, "Results");
        while(parser.next() != XmlPullParser.END_TAG){
            if(parser.getEventType() != XmlPullParser.START_TAG){
                continue;
            }
            String name = parser.getName();
            if(name.equals("Results")){
                entries.add(readEntry(parser));
            }
        }
        return entries;

    }
    private Election readEntry(XmlPullParser parser) throws XmlPullParserException, IOException, ParseException{
        parser.require(XmlPullParser.START_TAG, ns, "Results");
        final Election election = new Election();
        String order = null;
        while(parser.next() != XmlPullParser.END_TAG){
            if(parser.getEventType() != XmlPullParser.START_TAG){
                continue;
            }
            String name = parser.getName();
            if (name.equals("Result")){
                 election.Result = parser.getText();
            } else if (name.equals("Candidates")){
                parser.require(XmlPullParser.START_TAG,ns, "Candidate");
                while(parser.next() != XmlPullParser.END_TAG){
                    if(parser.getEventType() != XmlPullParser.START_TAG){
                        continue;
                    }
                    if (parser.getName().equals("order")) {
                        order = parser.getText();
                    }
                            switch(order){
                                case "1":
                                    if(parser.getName().equals("Name")){election.CandidateOne = parser.getText();}
                                    else if (parser.getName().equals("Party")){election.CandidateOneParty = parser.getText();}
                                    else if (parser.getName().equals("Votes")){election.CandidateOneVotes = parser.getText();}
                                    break;
                                case "2":
                                    if(parser.getName().equals("Name")){election.CandidateTwo = parser.getText();}
                                    else if (parser.getName().equals("Party")){election.CandidateTwoParty = parser.getText();}
                                    else if (parser.getName().equals("Votes")){election.CandidateTwoVotes = parser.getText();}
                                    break;
                                case"3":
                                    if(parser.getName().equals("Name")){election.CandidateThree = parser.getText();}
                                    else if (parser.getName().equals("Party")){election.CandidateThreeParty = parser.getText();}
                                    else if (parser.getName().equals("Votes")){election.CandidateThreeVotes = parser.getText();}
                                    break;
                                case"4":
                                    if(parser.getName().equals("Name")){election.CandidateFour = parser.getText();}
                                    else if (parser.getName().equals("Party")){election.CandidateFourParty = parser.getText();}
                                    else if (parser.getName().equals("Votes")){election.CandidateFourVotes = parser.getText();}
                                    break;
                                case"5":
                                    if(parser.getName().equals("Name")){election.CandidateFive = parser.getText();}
                                    else if (parser.getName().equals("Party")){election.CandidateFiveParty = parser.getText();}
                                    else if (parser.getName().equals("Votes")){election.CandidateFiveVotes = parser.getText();}
                                    break;
                                default:
                                    break;
                            }
                    parser.next();
                }
            }
        }
        return election;
    }
    public ArrayList GetElections(){return ElectionList;}
}
