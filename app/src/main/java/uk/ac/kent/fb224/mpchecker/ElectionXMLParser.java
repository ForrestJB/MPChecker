
package uk.ac.kent.fb224.mpchecker;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;

/**
 * Created by Forrest on 08/03/2018.
 *
 * Ok so this class is here to parse the XML file that contains the details of the most recent published election for the chosen MP/Constituency
 * its pretty unoptimised and ugly, but it does work for this specific xml format that Parliament is using
 * todo full comments for this piece of shit and be done with it
 */

public class ElectionXMLParser {
    public static final String ns = null;

    public Election Parse(InputStream in) throws XmlPullParserException, IOException, ParseException {
        Election output = new Election();
        XmlPullParser parser = Xml.newPullParser();
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
        parser.setInput(in, null);
        parser.nextTag();
        return readFeed(parser);
    }

    private Election readFeed(XmlPullParser parser) throws XmlPullParserException, IOException, ParseException {
        Election election = new Election();
        String Result = " ";
        parser.require(XmlPullParser.START_TAG, ns, "Results");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("Candidates")) {
                election = readEntry(parser);
            } else if (name.equals("Result")) {
                parser.next();
                Result = parser.getText();
                parser.next();parser.next();
            } else {
                skip(parser);
            }
        }
        election.Result = Result;
        return election;

    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
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

    private Election readEntry(XmlPullParser parser) throws XmlPullParserException, IOException, ParseException {;
        Election election1 = new Election();
        String order = null;
        String name = parser.getName();
        if (name.equals("Candidates")) {
            election1 = readCandidate(parser);
        }

        return election1;
    }

    public Election readCandidate(XmlPullParser parser) throws IOException, XmlPullParserException {
        Election election1 = new Election();
        String name = parser.getName();
        String order = null;
        parser.require(XmlPullParser.START_TAG, ns, "Candidates");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            if (parser.getName().equals("Candidate")) {
                order = parser.getAttributeValue(null, "Order");
                parser.nextTag();
                if (order.equals("1")) {
                    boolean done = false;
                    while (done == false) {
                        if (parser.getName().equals("Name")) {
                            if (parser.getName() != null && parser.getName().equalsIgnoreCase("Name")) {
                                parser.next();
                                if(election1.CandidateOne == null) {
                                    election1.CandidateOne = parser.getText();
                                }
                                parser.next();
                            }
                        } else if (parser.getName().equals("Party")) {
                            if (parser.getName() != null && parser.getName().equalsIgnoreCase("Party")) {
                                parser.next();
                                if(election1.CandidateOneParty == null) {
                                    election1.CandidateOneParty = parser.getText();
                                }
                                parser.next();
                            }
                        } else if (parser.getName().equals("Votes")) {
                            if (parser.getName() != null && parser.getName().equalsIgnoreCase("Votes")) {
                                parser.next();
                                if (election1.CandidateOneVotes == null) {
                                    election1.CandidateOneVotes = parser.getText();
                                }
                                parser.next();
                            }
                                } else if (parser.getName().equals("Constituency")) {
                                    done = true;
                            while(!parser.getName().equals("Candidate")){
                                parser.next();
                                while(parser.getName() == null){
                                    parser.next();
                                }
                            }
                                }
                         else {
                           parser.next();
                           while(parser.getEventType() != XmlPullParser.START_TAG){
                               parser.next();
                           }
                        }
                    }
                } else if (order.equals("2")) {
                    boolean done = false;
                    while (done == false) {
                        if (parser.getName().equals("Name")) {
                            if (parser.getName() != null && parser.getName().equalsIgnoreCase("Name")) {
                                parser.next();
                                if(election1.CandidateTwo == null) {
                                    election1.CandidateTwo = parser.getText();
                                }
                            }
                            parser.next();
                        } else if (parser.getName().equals("Party")) {
                            if (parser.getName() != null && parser.getName().equalsIgnoreCase("Party")) {
                                parser.next();
                                if(election1.CandidateTwoParty == null) {
                                    election1.CandidateTwoParty = parser.getText();
                                }
                                parser.next();
                            }
                        } else if (parser.getName().equals("Votes")) {
                            if (parser.getName() != null && parser.getName().equalsIgnoreCase("Votes")) {
                                parser.next();
                                if(election1.CandidateTwoVotes == null) {
                                    election1.CandidateTwoVotes = parser.getText();
                                }
                                parser.next();
                            }

                        }
                            else if (parser.getName().equals("Constituency")) {
                                done = true;
                            while(!parser.getName().equals("Candidate")){
                                parser.next();
                                while(parser.getName() == null){
                                    parser.next();
                                }
                            }
                            }
                         else {
                            parser.next();
                            while(parser.getEventType() != XmlPullParser.START_TAG){
                                parser.next();
                            }
                        }
                    }
                } else if (order.equals("3")) {
                    boolean done = false;
                    while (done == false) {
                        if (parser.getName().equals("Name")) {
                            if (parser.getName() != null && parser.getName().equalsIgnoreCase("Name")) {
                                parser.next();
                                if(election1.CandidateThree == null) {
                                    election1.CandidateThree = parser.getText();
                                }
                                parser.next();
                            }
                        } else if (parser.getName().equals("Party")) {
                            if (parser.getName() != null && parser.getName().equalsIgnoreCase("Party")) {
                                parser.next();
                                if( election1.CandidateThreeParty == null) {
                                    election1.CandidateThreeParty = parser.getText();
                                }
                                parser.next();
                            }
                        } else if (parser.getName().equals("Votes")) {
                            if (parser.getName() != null && parser.getName().equalsIgnoreCase("Votes")) {
                                parser.next();
                                if(election1.CandidateThreeVotes == null) {
                                    election1.CandidateThreeVotes = parser.getText();
                                }
                                parser.next();
                            }
                            } else if (parser.getName().equals("Constituency")) {
                                done = true;
                            while(!parser.getName().equals("Candidate")){
                                parser.next();
                                while(parser.getName() == null){
                                    parser.next();
                                }
                            }
                            }
                         else {
                            parser.next();
                            while(parser.getEventType() != XmlPullParser.START_TAG){
                                parser.next();
                            }
                        }
                    }
                } else if (order.equals("4")) {
                    boolean done = false;
                    while (done == false) {
                        if (parser.getName().equals("Name")) {
                            if (parser.getName() != null && parser.getName().equalsIgnoreCase("Name")) {
                                parser.next();
                                if(election1.CandidateFour == null) {
                                    election1.CandidateFour = parser.getText();
                                }
                                parser.next();
                            }
                        } else if (parser.getName().equals("Party")) {
                            if (parser.getName() != null && parser.getName().equalsIgnoreCase("Party")) {
                                parser.next();
                                if(election1.CandidateFourParty == null) {
                                    election1.CandidateFourParty = parser.getText();
                                }
                                parser.next();
                            }
                        } else if (parser.getName().equals("Votes")) {
                            if (parser.getName() != null && parser.getName().equalsIgnoreCase("Votes")) {
                                parser.next();
                                if(election1.CandidateFourVotes == null) {
                                    election1.CandidateFourVotes = parser.getText();
                                }
                                parser.next();
                            }
                            } else if (parser.getName().equals("Constituency")) {
                                done = true;
                            while(!parser.getName().equals("Candidate")){
                                parser.next();
                                while(parser.getName() == null){
                                    parser.next();
                                }
                            }
                            }
                         else {
                            parser.next();
                            while(parser.getEventType() != XmlPullParser.START_TAG){
                                parser.next();
                            }
                        }
                    }
                } else if (order.equals("5")) {
                    boolean done = false;
                    while (done == false) {
                        if (parser.getName().equals("Name")) {
                            if (parser.getName() != null && parser.getName().equalsIgnoreCase("Name")) {
                                parser.next();
                                if(election1.CandidateFive == null) {
                                    election1.CandidateFive = parser.getText();
                                }
                                parser.next();
                            }
                        } else if (parser.getName().equals("Party")) {
                            if (parser.getName() != null && parser.getName().equalsIgnoreCase("Party")) {
                                parser.next();
                                if(election1.CandidateFiveParty == null) {
                                    election1.CandidateFiveParty = parser.getText();
                                }
                                parser.next();
                            }
                        } else if (parser.getName().equals("Votes")) {
                            if (parser.getName() != null && parser.getName().equalsIgnoreCase("Votes")) {
                                parser.next();
                                if(election1.CandidateFiveVotes == null) {
                                    election1.CandidateFiveVotes = parser.getText();
                                }
                                parser.next();
                            }
                            } else if (parser.getName().equals("Constituency")) {
                                done = true;
                            while(!parser.getName().equals("Candidate")){
                                parser.next();
                                while(parser.getName() == null){
                                    parser.next();
                                }
                            }
                            }
                         else {
                            parser.next();
                            while(parser.getEventType() != XmlPullParser.START_TAG){
                                parser.next();
                            }
                        }
                    }

                }
            }
        }
        return election1;
    }
}