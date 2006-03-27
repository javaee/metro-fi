package com.sun.xml.analysis.frequency;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * A Set that manages how many occurances of a value occurs in the set.
 *
 * @author Paul.Sandoz@Sun.Com
 */
public class FrequencySet<T> extends HashMap<T, Integer> {

    /**
     * Add a value to the set.
     *
     * @param value the value to put in the set.
     */
    public void add(T value) {
        _add(value, 1);
    }
    
    /**
     * Add a value to the set with 0 occurences.
     *
     * @param value the value to put in the set.
     */
    public void add0(T value) {
        _add(value, 0);
    }
    
    private void _add(T value, int v) {
        Integer f = get(value);
        f = (f == null) ? v : f + 1;        
        put(value, f);
    }
    
    private class FrequencyComparator implements Comparator {
        public int compare(Object o1, Object o2) {
            Map.Entry<T, Integer> e1 = (Map.Entry<T, Integer>)o1;
            Map.Entry<T, Integer> e2 = (Map.Entry<T, Integer>)o2;
            int diff = e2.getValue() - e1.getValue();
            if (diff == 0) {
                if (e1.getKey().equals(e2.getKey())) {
                    return 0;
                } else {
                    return -1;
                }
            } else {
                return diff;
            }
        }
    };
    
    /**
     * Create an ordered list of values in the order of decreasing frequency
     * of occurence.
     *
     * @return the list of values in the order of decreasing frequency
     *         of occurence.
     */
    public List<T> createFrequencyBasedList() {
        Set<Map.Entry<T, Integer>> s = new TreeSet(new FrequencyComparator());
        s.addAll(entrySet());

        for (Map.Entry<T, Integer> e : entrySet()) {
            System.out.println(e.getKey() + " " + e.getValue());
        }
        
        List<T> l = new ArrayList<T>();
        for (Map.Entry<T, Integer> e : s) {
            l.add(e.getKey());
        }
        
        return l;
    }    
}
