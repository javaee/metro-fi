/*
 * DateComparator.java
 *
 * Created on April 13, 2005, 12:16 PM
 */

package com.sun.japex.report;

import java.io.File;
import java.util.Comparator;
import java.util.Date;


public class DateComparator implements Comparator {
    public static final int ORDER_DESC = 1;
    public static final int ORDER_ASC = 2;
    private int order;    
    /** Creates a new instance of DateComparator */
    public DateComparator() {
        order = ORDER_DESC;
    }
    public DateComparator(int order) {
        this.order = order;
    }
    public int compare(Object o1, Object o2) {
        if ( (o1 instanceof File) && (o2 instanceof File) )
        {
            long lm1 = ((File)o1).lastModified();
            long lm2 = ((File)o2).lastModified();

            if ( lm1 < lm2 ) {
                return (order == ORDER_DESC) ? -1 : 1;
            } else if ( lm1 > lm2 ) {
                return (order == ORDER_DESC) ? 1 : -1;
            } else {
                return 0;
            }
        } else if ( (o1 instanceof Comparable) && (o2 instanceof Comparable) ) {
            return ((Comparable)o1).compareTo( ((Comparable)o2) );
        }
        else
        {
            return -1;
        }        
    }
    public boolean equals(Object obj) {
        if ( obj instanceof DateComparator ) {
            return true;
        } else {
            return false;
        }
    }
}
