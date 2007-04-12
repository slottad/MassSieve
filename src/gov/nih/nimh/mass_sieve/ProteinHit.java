/*
 * ProteinHit.java
 *
 * Created on October 19, 2006, 11:15 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package gov.nih.nimh.mass_sieve;

/**
 *
 * @author slotta
 */
public class ProteinHit {
    private int start;
    private int end;
    private String name;
    
    /** Creates a new instance of ProteinHit */
    public ProteinHit() {
    }
    
    public ProteinHit(String name, int start, int end) {
        this.name = new String(name).intern();
        this.start = start;
        this.end = end;
    }
        
    public int compareTo(ProteinHit p) {
        int res = name.compareToIgnoreCase(p.getName());
        if (res != 0) return res;
        return start - p.getStart();
    }
    
    public boolean equals(Object obj) {
        ProteinHit p = (ProteinHit)obj;
        if (p.getName().equals(name)) {
            if (p.getStart() == start) {
                if (p.getEnd() == end) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public int hashCode() {
        return name.hashCode()+start;
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
