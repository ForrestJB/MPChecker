package uk.ac.kent.fb224.mpchecker;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * Created by Tree1 on 29/01/2018.
 */

public class Constituency {
    public String ConName;
    public String MPImageUrl;
    public String Party;
    public String MPName;
    public String MPRole;
    public int pos;
    public String email;
    public String id;
    public boolean isFav = false;

    public static Comparator<Constituency> ConNameSort = new Comparator<Constituency>() {
        @Override
        public int compare(Constituency c1, Constituency c2) {
            String ConName1 = c1.ConName.toUpperCase();
            String ConName2 = c2.ConName.toUpperCase();

            return ConName1.compareTo(ConName2);
        }
    };
}
