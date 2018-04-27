package uk.ac.kent.fb224.mpchecker;

import java.util.ArrayList;

/**
 * Created by Tree1 on 23/02/2018.
 */

public class Bill {
    String ID;
    String Desc;
    String Date;
    String Name;
    String Result;
    String MPVote;
    int count;
    int Ayes;
    int Noes;
    int Abstains;
   ArrayList<Vote> VoteAyeList = new ArrayList<Vote>();
   ArrayList<Vote> VoteNoeList = new ArrayList<Vote>();
   ArrayList<Vote> LabourAyeVotes = new ArrayList<>();
    ArrayList<Vote> LabourNoVotes = new ArrayList<>();
   ArrayList<Vote> ConservativeAyeVotes = new ArrayList<>();
    ArrayList<Vote> ConservativeNoVotes = new ArrayList<>();
}
