/*
 * BatchLoadDialog.java
 *
 * Created on October 20, 2006, 9:38 PM
 */

package gov.nih.nimh.mass_sieve.gui;

import java.awt.Cursor;
import java.awt.Dialog;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 *
 * @author  slotta
 */
public class BatchLoadDialog extends Dialog {
    private MassSieveFrame msFrame;
    private ExperimentPanel defaultExp;
    private JFileChooser jFileChooserLoad;
    private JOptionPane jOptionPaneAbout;
    private DefaultListModel listModel;
    
    /** Creates new form BatchLoadDialog */
    public BatchLoadDialog(MassSieveFrame parent) {
        super(parent, true);
        initComponents();
        msFrame = parent;
        defaultExp = new ExperimentPanel(msFrame, "Batch Load");
        jFileChooserLoad = new JFileChooser();
        jFileChooserLoad.setDialogTitle("Open Files");
        jFileChooserLoad.setMultiSelectionEnabled(true);
        jFileChooserLoad.setFileFilter(new MSFileFilter());
        jOptionPaneAbout = new JOptionPane();
        listModel = new DefaultListModel();
        jListFiles.setModel(listModel);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        jPanelTop = new javax.swing.JPanel();
        jButtonAddFiles = new javax.swing.JButton();
        jLabelExpName = new javax.swing.JLabel();
        jTextFieldExperimentName = new javax.swing.JTextField();
        jButtonFilter = new javax.swing.JButton();
        jPanelBottom = new javax.swing.JPanel();
        jButtonRemove = new javax.swing.JButton();
        jButtonCancel = new javax.swing.JButton();
        jButtonLoad = new javax.swing.JButton();
        jPanelCenter = new javax.swing.JPanel();
        jScrollPaneList = new javax.swing.JScrollPane();
        jListFiles = new javax.swing.JList();

        setTitle("Batch Load Files");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        jButtonAddFiles.setText("Add Files");
        jButtonAddFiles.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAddFilesActionPerformed(evt);
            }
        });

        jLabelExpName.setText("Experiment Name:");

        jTextFieldExperimentName.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextFieldExperimentName.setToolTipText("Enter Experiment Name");
        jTextFieldExperimentName.setPreferredSize(new java.awt.Dimension(20, 20));

        jButtonFilter.setText("Set Filter");
        jButtonFilter.setPreferredSize(new java.awt.Dimension(90, 25));
        jButtonFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonFilterActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanelTopLayout = new org.jdesktop.layout.GroupLayout(jPanelTop);
        jPanelTop.setLayout(jPanelTopLayout);
        jPanelTopLayout.setHorizontalGroup(
            jPanelTopLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanelTopLayout.createSequentialGroup()
                .addContainerGap()
                .add(jButtonAddFiles)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jButtonFilter, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabelExpName)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jTextFieldExperimentName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 99, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanelTopLayout.setVerticalGroup(
            jPanelTopLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanelTopLayout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jPanelTopLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jButtonAddFiles)
                    .add(jButtonFilter, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabelExpName)
                    .add(jTextFieldExperimentName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        add(jPanelTop, java.awt.BorderLayout.NORTH);

        jButtonRemove.setText("Delete");
        jButtonRemove.setPreferredSize(new java.awt.Dimension(90, 25));
        jButtonRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRemoveActionPerformed(evt);
            }
        });

        jButtonCancel.setText("Cancel");
        jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancelActionPerformed(evt);
            }
        });

        jButtonLoad.setText("Load");
        jButtonLoad.setPreferredSize(new java.awt.Dimension(75, 25));
        jButtonLoad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonLoadActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanelBottomLayout = new org.jdesktop.layout.GroupLayout(jPanelBottom);
        jPanelBottom.setLayout(jPanelBottomLayout);
        jPanelBottomLayout.setHorizontalGroup(
            jPanelBottomLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanelBottomLayout.createSequentialGroup()
                .addContainerGap()
                .add(jButtonRemove, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 177, Short.MAX_VALUE)
                .add(jButtonCancel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jButtonLoad, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanelBottomLayout.setVerticalGroup(
            jPanelBottomLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanelBottomLayout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jPanelBottomLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jButtonRemove, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jButtonLoad, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jButtonCancel))
                .addContainerGap())
        );
        add(jPanelBottom, java.awt.BorderLayout.SOUTH);

        jListFiles.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPaneList.setViewportView(jListFiles);

        org.jdesktop.layout.GroupLayout jPanelCenterLayout = new org.jdesktop.layout.GroupLayout(jPanelCenter);
        jPanelCenter.setLayout(jPanelCenterLayout);
        jPanelCenterLayout.setHorizontalGroup(
            jPanelCenterLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanelCenterLayout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPaneList, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 423, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanelCenterLayout.setVerticalGroup(
            jPanelCenterLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jScrollPaneList, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 269, Short.MAX_VALUE)
        );
        add(jPanelCenter, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    private void jButtonFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonFilterActionPerformed
                defaultExp.showPreferences();
    }//GEN-LAST:event_jButtonFilterActionPerformed
    
    private void jButtonLoadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonLoadActionPerformed
        new Thread(new Runnable() {
            public void run() {
                msFrame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                
                HashMap<String, ArrayList<File>> efList = new HashMap<String, ArrayList<File>>();
                Object[] objs = listModel.toArray();
                for (Object obj:objs) {
                    FileExperimentTuple fet = (FileExperimentTuple)obj;
                    String exp = fet.getExp();
                    if (efList.containsKey(exp)) {
                        efList.get(exp).add(fet.getFile());
                    } else {
                        ArrayList<File> l = new ArrayList<File>();
                        l.add(fet.getFile());
                        efList.put(exp,l);
                    }
                }
                cleanup();
                ArrayList<String> sortExperiments = new ArrayList<String>();
                sortExperiments.addAll(efList.keySet());
                Collections.sort(sortExperiments);
                for (String exp:sortExperiments) {
                    ArrayList<File> files = efList.get(exp);
                    File[] f = new File[1];
                    f = files.toArray(f);
                    msFrame.addExperimentAndFiles(defaultExp, exp,f);
                }
                msFrame.setCursor(null);
            }
        }).start();
        
    }//GEN-LAST:event_jButtonLoadActionPerformed
    
    private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelActionPerformed
        cleanup();
    }//GEN-LAST:event_jButtonCancelActionPerformed
    
    private void jButtonRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRemoveActionPerformed
        Object[] daFiles = jListFiles.getSelectedValuesList().toArray();
        for (Object o:daFiles) {
            listModel.removeElement(o);
        }
    }//GEN-LAST:event_jButtonRemoveActionPerformed
    
    private void jButtonAddFilesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAddFilesActionPerformed
        if (jTextFieldExperimentName.getText().length() > 0) {
            int status = jFileChooserLoad.showOpenDialog(this);
            if (status == JFileChooser.APPROVE_OPTION) {
                File selectedFiles[] = jFileChooserLoad.getSelectedFiles();
                for (File f:selectedFiles) {
                    listModel.addElement(new FileExperimentTuple(f, jTextFieldExperimentName.getText()));
                    //System.out.println(f.getName());
                }
                jListFiles.ensureIndexIsVisible(listModel.getSize()-1);
            }
        } else {
            jOptionPaneAbout.showMessageDialog(this, "Please enter an experiment name first.");
        }
    }//GEN-LAST:event_jButtonAddFilesActionPerformed
    
    /** Closes the dialog */
    private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
        cleanup();
    }//GEN-LAST:event_closeDialog
    
    private void cleanup() {
        setVisible(false);
        listModel.removeAllElements();
        jTextFieldExperimentName.setText(null);
    }
    
    /**
     * @param args the command line arguments
     */
    //public static void main(String args[]) {
    //    java.awt.EventQueue.invokeLater(new Runnable() {
    //        public void run() {
    //            new BatchLoadDialog(new java.awt.Frame(), true).setVisible(true);
    //        }
    //    });
    //}
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonAddFiles;
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JButton jButtonFilter;
    private javax.swing.JButton jButtonLoad;
    private javax.swing.JButton jButtonRemove;
    private javax.swing.JLabel jLabelExpName;
    private javax.swing.JList jListFiles;
    private javax.swing.JPanel jPanelBottom;
    private javax.swing.JPanel jPanelCenter;
    private javax.swing.JPanel jPanelTop;
    private javax.swing.JScrollPane jScrollPaneList;
    private javax.swing.JTextField jTextFieldExperimentName;
    // End of variables declaration//GEN-END:variables
    
}
