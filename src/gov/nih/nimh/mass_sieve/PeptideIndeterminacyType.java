/*
 * PeptideIndeterminacyType.java
 *
 * Created on November 14, 2006, 3:26 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package gov.nih.nimh.mass_sieve;

/**
 *
 * @author slotta
 */
public enum PeptideIndeterminacyType { 
    NONE {
        public String toString() {
            return "";
        }
    },
    
    SOME {
        public String toString() {
            return "Some peptides";
        }
    },
    
    ALL {
        public String toString() {
            return "All peptides";
        }
    }
 };