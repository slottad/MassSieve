/*
 * PreferencesPanel.java
 *
 * Created on July 31, 2006, 3:50 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package gov.nih.nimh.mass_sieve.gui;

import com.jhlabs.awt.*;
import gov.nih.nimh.mass_sieve.*;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.util.Iterator;
import java.util.TreeSet;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import org.biojava.bio.proteomics.ProteaseManager;

/**
 *
 * @author slotta
 */
public class PreferencesDialog extends JDialog {
    private int width = 450;
    private int height = 350;
    private JCheckBox useDigestBox;
    private JCheckBox useMultiColumnSortBox;
    private JComboBox proteaseCombo;
    private JComboBox digest;
    private ButtonGroup layoutGroup;
    private JRadioButton ballonButton;
    private JRadioButton forceButton;
    private JRadioButton nodeLinkButton;
    private JRadioButton radialButton;
    private JButton okButton, cancelButton;
    private boolean useIonIdent, useDigest, useMultiColumnSort;
    private JLabel mcWarning;
    private MassSieveFrame msFrame;
    
    /** Creates a new instance of PreferencesDialog */
    public PreferencesDialog(MassSieveFrame frm) {
        msFrame = frm;
        setBounds(50,50,width, height);
        setTitle("Options");
        setAlwaysOnTop(true);
        setLayout(new BorderLayout());
        JPanel centerPanel = new JPanel(new ParagraphLayout());
        useDigestBox = new JCheckBox("Show Digest");
        useDigestBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                useDigestBoxStateChanged(evt);
            }
        });
        proteaseCombo = constructProteaseCombo();
        proteaseCombo.setEnabled(false);
        ballonButton = new JRadioButton("Ballon");
        forceButton = new JRadioButton("Force Directed");
        nodeLinkButton = new JRadioButton("Node Link", true);
        radialButton = new JRadioButton("Radial");
        layoutGroup = new ButtonGroup();
        layoutGroup.add(ballonButton);
        layoutGroup.add(forceButton);
        layoutGroup.add(nodeLinkButton);
        layoutGroup.add(radialButton);
        useMultiColumnSortBox = new JCheckBox("Enable multicolumn sorting.");
        useMultiColumnSortBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                useMultiColumnSortBoxStateChanged(evt);
            }
        });
        mcWarning = new JLabel("<html>NB: This can be confusing.<p>" +
                "Also, this will not apply to current<br>" +
                "table, only new or redisplayed ones.</html>");

        okButton = new JButton("OK");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });
        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });
        centerPanel.add(new JLabel("Digest:"), ParagraphLayout.NEW_PARAGRAPH);
        centerPanel.add(proteaseCombo);
        centerPanel.add(useDigestBox);
        centerPanel.add(new JLabel("Graph Layout Type:"), ParagraphLayout.NEW_PARAGRAPH);
        centerPanel.add(nodeLinkButton);
        centerPanel.add(radialButton, ParagraphLayout.NEW_LINE);
        centerPanel.add(forceButton, ParagraphLayout.NEW_LINE);
        centerPanel.add(ballonButton, ParagraphLayout.NEW_LINE);
        centerPanel.add(new JLabel("Table Options:"), ParagraphLayout.NEW_PARAGRAPH);
        centerPanel.add(useMultiColumnSortBox);
        centerPanel.add(mcWarning, ParagraphLayout.NEW_LINE);
        mcWarning.setVisible(false);
        
        add(centerPanel,BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        buttonPanel.add(Box.createHorizontalGlue());
        buttonPanel.add(cancelButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonPanel.add(okButton);
        add(buttonPanel,BorderLayout.SOUTH);
    }
    
    private JComboBox constructProteaseCombo() {
        JComboBox pCombo = new JComboBox( new DefaultComboBoxModel() );
        Object selected = pCombo.getSelectedItem();
        ((DefaultComboBoxModel)pCombo.getModel()).removeAllElements();
        int idx = -1;
        int i = 0;
        for(Iterator it = new TreeSet( ProteaseManager.getNames() ).iterator(); it.hasNext(); ){
            String protease = (String)it.next();
            if( protease.equals(selected))
                idx = i;
            i++;
            pCombo.addItem(protease);
        }
        pCombo.setSelectedItem("Trypsin");
        return pCombo;
    }
    
    private void useDigestBoxStateChanged(java.awt.event.ItemEvent evt) {
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            useDigest = true;
            proteaseCombo.setEnabled(true);
        } else {
            useDigest = false;
            proteaseCombo.setEnabled(false);
        }
    }
    
    private void useMultiColumnSortBoxStateChanged(java.awt.event.ItemEvent evt) {
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            useMultiColumnSort = true;
            mcWarning.setVisible(true);
        } else {
            useMultiColumnSort = false;
            mcWarning.setVisible(false);
        }
    }
    
    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {
        msFrame.setUseDigest(useDigest);
        msFrame.setDigestName(proteaseCombo.getSelectedItem().toString());
        if (ballonButton.isSelected()) msFrame.setGraphLayout(GraphLayoutType.BALLOON_TREE);
        if (forceButton.isSelected()) msFrame.setGraphLayout(GraphLayoutType.FORCE_DIRECTED);
        if (nodeLinkButton.isSelected()) msFrame.setGraphLayout(GraphLayoutType.NODE_LINK_TREE);
        if (radialButton.isSelected()) msFrame.setGraphLayout(GraphLayoutType.RADIAL_TREE);
        msFrame.setUseMultiColumnSort(useMultiColumnSort);
        setVisible(false);
    }
    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {
        setVisible(false);
    }
    public void setUseDigestBox(boolean b) {
        if (b) {
            useDigest = true;
            proteaseCombo.setEnabled(true);
            useDigestBox.setSelected(true);
        } else {
            useDigest = false;
            proteaseCombo.setEnabled(false);
            useDigestBox.setSelected(false);
        }
    }
    public void setProteaseCombo(String s) {
        proteaseCombo.setSelectedItem(s);
    }
    public void setGraphLayout(GraphLayoutType glt) {
        switch(glt) {
            case BALLOON_TREE:
                ballonButton.setSelected(true);
                break;
            case FORCE_DIRECTED:
                forceButton.setSelected(true);
                break;
            case NODE_LINK_TREE:
                nodeLinkButton.setSelected(true);
                break;
            case RADIAL_TREE:
                radialButton.setSelected(true);
                break;
        }
    }
}