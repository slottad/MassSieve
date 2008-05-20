/*
 * ProteinHit.java
 *
 * Created on October 19, 2006, 11:15 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package gov.nih.nimh.mass_sieve;

import java.io.Serializable;

/**
 *
 * @author slotta
 */
public class ProteinHit implements Serializable {

    private int start;
    private int end;
    private String name;

    /** Creates a new instance of ProteinHit */
    public ProteinHit() {
        start = -1;
        end = -1;
    }

    public ProteinHit(String name, int start, int end) {
        this.name = name.intern();
        this.start = start;
        this.end = end;
    }

    public int compareTo(ProteinHit p) {
        int res = name.compareToIgnoreCase(p.getName());
        if (res != 0) {
            return res;
        }
        return start - p.getStart();
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof ProteinHit) {
            ProteinHit p = (ProteinHit) obj;
            if (p.getName().equals(name)) {
                if (p.getStart() == start) {
                    if (p.getEnd() == end) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void updateLocation(String pepSeq, String proSeq) {
        int loc = proSeq.indexOf(pepSeq);
        if (loc >= 0) {
            start = loc + 1;
            end = loc + pepSeq.length();
        }
    }

    public int hashCode() {
        return name.hashCode() + start;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
