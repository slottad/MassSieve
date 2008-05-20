/*
 * SequencePanel.java
 *
 * Created on August 8, 2006, 10:06 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package gov.nih.nimh.mass_sieve.gui;

import gov.nih.nimh.mass_sieve.*;
import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import org.biojava.bio.gui.glyph.TurnGlyph;
import org.biojava.bio.gui.sequence.FeatureBlockSequenceRenderer;
import org.biojava.bio.gui.sequence.FeatureSource;
import org.biojava.bio.gui.sequence.GlyphFeatureRenderer;
import org.biojava.bio.gui.sequence.MultiLineRenderer;
import org.biojava.bio.gui.sequence.OffsetRulerRenderer;
import org.biojava.bio.gui.sequence.PeptideDigestRenderer;
import org.biojava.bio.gui.sequence.SequencePanelWrapper;
import org.biojava.bio.gui.sequence.SequenceRenderer;
import org.biojava.bio.gui.sequence.SymbolSequenceRenderer;
import org.biojava.bio.gui.sequence.tracklayout.SimpleTrackLayout;
import org.biojava.bio.proteomics.Digest;
import org.biojava.bio.proteomics.ProteaseManager;
import org.biojava.bio.seq.FeatureFilter;
import org.biojava.bio.seq.FeatureHolder;
import org.biojava.bio.seq.Sequence;
import org.biojava.bio.seq.impl.ViewSequence;
import org.biojava.utils.ChangeVetoException;

/**
 *
 * @author slotta
 */
public class SequencePanel extends JPanel {
    private Sequence seqObj;
    private PeptideDigestRenderer digestRenderer;
    private SequencePanelWrapper seqPanel;
    
    /** Creates a new instance of SequencePanel */
    public SequencePanel(Protein pro, boolean useDigest, String proteaseName, int size) {
        int tracksize = size/15;
        JToolBar jToolBar = new JToolBar();
        jToolBar.setFloatable(false);
        setLayout(new BorderLayout());
        jToolBar.add(new JLabel(pro.getName()));
        jToolBar.addSeparator();
        jToolBar.add(new JLabel("[" + pro.getParsimonyType() + "]"));
        jToolBar.addSeparator();
        jToolBar.add(new JLabel("Len: " + pro.getLength()));
        jToolBar.addSeparator();
        jToolBar.add(new JLabel("Cov: " + pro.getCoverageNum() + "(" + pro.getCoveragePercent() + "%)"));
        jToolBar.addSeparator();
        jToolBar.add(new JLabel("Mass: " + pro.getMass()));
        jToolBar.addSeparator();
        jToolBar.add(new JLabel("pI: " + pro.getIsoelectricPoint()));
        jToolBar.addSeparator();
        jToolBar.add(new JLabel("UniqPeps: " + pro.getNumUniquePeptides()));
        jToolBar.addSeparator();
        jToolBar.add(new JLabel("PepHits: " + pro.getNumPeptideHits()));
        jToolBar.addSeparator();
        jToolBar.add(new JLabel("Cluster:" + pro.getCluster()));
        JPanel infoPanel = new JPanel(new BorderLayout());
        
        infoPanel.add(jToolBar, BorderLayout.NORTH);
        infoPanel.add(new JLabel("Desc: " + pro.getDescription()), BorderLayout.SOUTH);
        add(infoPanel, BorderLayout.NORTH);
        
        if (pro.getSeqObj() == null) {
            add(BorderLayout.CENTER, new JButton("No Sequence Data Available"));
            return;
        }
        seqObj = pro.getSeqObj();
        
        seqPanel = new SequencePanelWrapper();
        MultiLineRenderer multi = new MultiLineRenderer();
        OffsetRulerRenderer offsetRenderer = new OffsetRulerRenderer();
        seqPanel.setSequence(seqObj);
        seqPanel.setRenderer(multi);
        try {
            multi.addRenderer( createPeptideHitRenderer() );
            multi.addRenderer(new SymbolSequenceRenderer());
            multi.addRenderer(offsetRenderer);
            multi.addRenderer( createPeptideDigestRenderer() );
        } catch (ChangeVetoException ex) {
            ex.printStackTrace();
        }
        seqPanel.setTrackLayout(new SimpleTrackLayout(seqObj,tracksize));
        if (useDigest) {
            try{
                ViewSequence view = new ViewSequence(seqObj);
                Digest digest = new Digest();
                digest.setSequence( view );
                digest.setProtease( ProteaseManager.getProteaseByName(proteaseName) );
                digest.setMaxMissedCleavages(0);
                digest.addDigestFeatures();
                seqPanel.setSequence(view);
                digestRenderer.sortPeptidesIntoLanes();
            } catch(org.biojava.bio.BioException ex){
                JOptionPane.showMessageDialog(this,"There was an error digesting the protein","Demo", JOptionPane.ERROR_MESSAGE);
            }
        }
        
        //add(seqPanel);
        JScrollPane jScrollPane = new JScrollPane(seqPanel);
        add(jScrollPane, BorderLayout.CENTER);
        setMinimumSize(new Dimension(10,10));
    }
    
    protected SequenceRenderer createPeptideHitRenderer() throws ChangeVetoException{
        GlyphFeatureRenderer gfr = new GlyphFeatureRenderer();
        gfr.addFilterAndGlyph(new FeatureFilter.ByType("peptide hit"),
                new TurnGlyph(java.awt.Color.GREEN.darker(), new java.awt.BasicStroke(3F))
                );
        FeatureBlockSequenceRenderer block = new FeatureBlockSequenceRenderer();
        block.setFeatureRenderer(gfr);
        return block;
    }
    
    protected SequenceRenderer createPeptideDigestRenderer() throws ChangeVetoException{
        digestRenderer = new PeptideDigestRenderer( new FeatureSource(){
            public FeatureHolder getFeatureHolder(){
                return seqPanel.getSequence();
            }
        });
        digestRenderer.setFilter( new FeatureFilter.ByType( Digest.PEPTIDE_FEATURE_TYPE ) );
        return digestRenderer;
    }
}
