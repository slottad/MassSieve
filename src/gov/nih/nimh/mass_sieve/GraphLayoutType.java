/*
 * GraphLayoutType.java
 *
 * Created on August 8, 2006, 5:46 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package gov.nih.nimh.mass_sieve;

/**
 * This enumerates the possible algorithms for graph layouts for the peptide-protein graphs.
 * @author Douglas J. Slotta
 */
public enum GraphLayoutType { 
    /**
     * Use the Ballon tree algorithm
     */
    BALLOON_TREE, 
    /**
     * Use the force directed algorithm
     */
    FORCE_DIRECTED, 
    /**
     * Use the node link algorithm
     */
    NODE_LINK_TREE, 
    /**
     * Use the radial tree algorithm
     */
    RADIAL_TREE
}
