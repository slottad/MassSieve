/*
 * ProteinInfo.java
 *
 * Created on July 26, 2007, 11:08 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package gov.nih.nimh.mass_sieve;

import java.io.Serializable;
import org.biojava.bio.BioException;
import org.biojava.bio.seq.ProteinTools;
import org.biojavax.bio.seq.RichSequence;

/**
 *
 * @author slotta
 */
public class ProteinInfo implements Serializable {
    private int length;
    private String name;
    private String description;
    private String sequence;
    private double mass;
    
    /** Creates a new instance of ProteinInfo */
    public ProteinInfo() {
        length = 0;
        description = "";
        sequence = "";
        name = "";
        mass = -1;
    }
    
    public ProteinInfo(RichSequence rs) {
        name = rs.getName();
        description = rs.getDescription();
        sequence = rs.seqString();
        length = rs.length();
    }
    
    public ProteinInfo(String s) {
        name = s;
        length = 0;
        description = "";
        sequence = "";
    }
    
    public int getLength() {
        return length;
    }
    
    public void setLength(int length) {
        this.length = length;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getSequence() {
        return sequence;
    }
    
    public void setSequence(String sequence) {
        this.sequence = sequence;
        length = sequence.length();
    }
    
    public RichSequence getRichSequence() {
        if (sequence.length() <= 0) return null;
        try {
            RichSequence rs = RichSequence.Tools.createRichSequence(name, sequence, ProteinTools.getAlphabet());
            rs.setDescription(description);
            return rs;
        } catch (BioException ex) {
            return null;
        }
    }
    
    public void updateFromRichSequence(RichSequence rs) {
        if (rs.getName().equals(name)) {
            description = rs.getDescription();
            sequence = rs.seqString();
            length = rs.length();
        } else {
            System.err.println("Trying to update " + name + " with data from " + rs.getName());
        }
    }
    
    public void update(ProteinInfo pInfo) {
        if (pInfo.getName().equals(name)) {
            if (description.length() < pInfo.getDescription().length()) description = pInfo.getDescription();
            if (sequence.length() < pInfo.getSequence().length()) sequence = pInfo.getSequence();
            if (length < pInfo.getLength()) length = pInfo.getLength();
        } else {
            System.err.println("Trying to update " + name + " with data from " + pInfo.getName());
        }
    }

    public double getMass() {
        return mass;
    }

    public void setMass(double mass) {
        this.mass = mass;
    }
}
