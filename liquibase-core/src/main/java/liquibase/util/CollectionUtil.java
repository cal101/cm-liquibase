package liquibase.util;

import java.util.*;

public class CollectionUtil {

    public static <T> Set<Set<T>> powerSet(Collection<T> originalSet) {
        Set<Set<T>> sets = new HashSet<>();
        if (originalSet.isEmpty()) {
            sets.add(new HashSet<T>());
            return sets;
        }
        List<T> list = new ArrayList<>(originalSet);
        T head = list.get(0);
        Collection<T> rest = list.subList(1, list.size());
        for (Set<T> set : powerSet(rest)) {
            Set<T> newSet = new HashSet<>();
            newSet.add(head);
            newSet.addAll(set);
            sets.add(newSet);
            sets.add(set);
        }
        return sets;
    }

    public static <T> List<Map<String, T>> permutations(Map<String, List<T>> parameterValues) {
        List<Map<String, T>> list = new ArrayList<>();
        if ((parameterValues == null) || parameterValues.isEmpty()) {
            return list;
        }

        permute(new HashMap<String, T>(), new ArrayList<String>(parameterValues.keySet()), parameterValues, list);

        return list;


    }

    private static <T> void permute(Map<String, T> basePermutation, List<String> remainingKeys, Map<String, List<T>> parameterValues, List<Map<String, T>> returnList) {

        String thisKey = remainingKeys.get(0);
        remainingKeys = remainingKeys.subList(1, remainingKeys.size());
        for (T value : parameterValues.get(thisKey)) {
            Map<String, T> permutation = new HashMap<>(basePermutation);

            permutation.put(thisKey, value);

            if (remainingKeys.isEmpty()) {
                returnList.add(permutation);
            } else {
                permute(permutation, remainingKeys, parameterValues, returnList);
            }
        }
    }

    /**
     * Returns passed currentValue if it is not null and creates a new ArrayList if it is null.
     * <br><br>
     * Example: values = createIfNull(values)
     */
    public static <T> List<T> createIfNull(List<T> currentValue) {
        if (currentValue == null) {
            return new ArrayList<>();
        } else {
            return currentValue;
        }
    }

    /**
     * Returns a new empty array if the passed array is null.
     */
    public static <T> T[] createIfNull(T[] arguments) {
        if (arguments == null) {
            return (T[]) new Object[0];
        } else {
            return arguments;
        }
    }

    /**
     * Returns a new empty set if the passed array is null.
     */
    public static <T> Set<T> createIfNull(Set<T> currentValue) {
        if (currentValue == null) {
            return new HashSet<>();
        } else {
            return currentValue;
        }
    }


}
