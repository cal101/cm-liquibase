package liquibase.changelog;

import java.util.Comparator;


public class ReversedChangeLogNamesComparator implements Comparator<String> {

    @Override
    public int compare(String a, String b) {
        return b.compareTo(a);
    }
}
