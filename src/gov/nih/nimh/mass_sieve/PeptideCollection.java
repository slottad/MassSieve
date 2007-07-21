/*
 * PeptideCollection.java
 *
 * Created on March 31, 2006, 2:02 PM
 *
 * @author Douglas Slotta
 */

package gov.nih.nimh.mass_sieve;

import gov.nih.nimh.mass_sieve.gui.ExperimentPanel;
import gov.nih.nimh.mass_sieve.gui.MassSieveFrame;
import gov.nih.nimh.mass_sieve.gui.PeptideHitListPanel;
import gov.nih.nimh.mass_sieve.gui.PeptideListPanel;
import gov.nih.nimh.mass_sieve.gui.PrintUtilities;
import gov.nih.nimh.mass_sieve.gui.ProteinGroupListPanel;
import gov.nih.nimh.mass_sieve.gui.ProteinListPanel;
import gov.nih.nimh.mass_sieve.io.AnalysisProgramType;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import prefuse.Constants;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.RepaintAction;
import prefuse.action.assignment.ColorAction;
import prefuse.action.assignment.DataColorAction;
import prefuse.action.layout.graph.BalloonTreeLayout;
import prefuse.action.layout.graph.ForceDirectedLayout;
import prefuse.action.layout.graph.NodeLinkTreeLayout;
import prefuse.action.layout.graph.RadialTreeLayout;
import prefuse.activity.Activity;
import prefuse.controls.Control;
import prefuse.controls.DragControl;
import prefuse.controls.FocusControl;
import prefuse.controls.HoverActionControl;
import prefuse.controls.NeighborHighlightControl;
import prefuse.controls.PanControl;
import prefuse.controls.WheelZoomControl;
import prefuse.controls.ZoomToFitControl;
import prefuse.data.Graph;
import prefuse.data.Table;
import prefuse.data.expression.parser.ExpressionParser;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.EdgeRenderer;
import prefuse.render.LabelRenderer;
import prefuse.util.ColorLib;
import prefuse.visual.EdgeItem;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualItem;

public class PeptideCollection implements Serializable, Comparable<PeptideCollection> {
    
    private HashMap<String, Peptide> minPeptides;
    private HashMap<String, Protein> minProteins;
    private HashMap<Integer, PeptideCollection> clusters;
    private HashSet<String> experimentSet;
    private ArrayList<PeptideHit> peptideHits;
    private ArrayList<Protein> equivalents, subsets, supersets, subsumables, differentiables, discretes, countables;
    private int cluster_num;
    private Integer countablesCount;
    transient private PeptideHitListPanel peptideHitListPanel;
    transient private PeptideListPanel peptideListPanel;
    transient private ProteinListPanel proteinListPanel;
    transient private ProteinGroupListPanel clusterListPanel, parsimonyListPanel;
    transient private Graph clusterGraph;
    transient private NodeItem highlightedItem;
    
    /** Creates a new instance of PeptideCollection */
    public PeptideCollection() {
        peptideHits = new ArrayList<PeptideHit>();
        minPeptides = new HashMap<String, Peptide>();
        experimentSet = new HashSet<String>();
        cluster_num = -1;
        countablesCount = null;
    }
    
    public void addPeptideHit(PeptideHit p) {
        if (p == null) return;
        peptideHits.add(p);
        experimentSet.add(p.getExperiment());
        String key = p.getSequence();
        if (minPeptides.containsKey(key)) {
            Peptide pg = minPeptides.get(key);
            pg.addPeptideHit(p);
        } else {
            Peptide pg = new Peptide(p);
            minPeptides.put(key, pg);
        }
    }
    
    public void updatePeptideHits() {
        peptideHits = new ArrayList<PeptideHit>();
        for (Peptide p:minPeptides.values()) {
            peptideHits.addAll(p.getPeptideHits());
            experimentSet.addAll(p.getExperimentSet());
        }
    }
    
    public void addPeptideGroup(Peptide pg) {
        String key = pg.getSequence();
        if (minPeptides.containsKey(key)) {
            System.out.print("This PeptideCollection already contains " + key);
            System.out.println(" are you sure this is what you want?");
        } else {
            minPeptides.put(key, pg);
        }
    }
    
    public PeptideCollection getCutoffCollection(double omssa, double mascot, double xtandem) {
        return getCutoffCollection(omssa, mascot, xtandem, false);
    }
    
    public PeptideCollection getCutoffCollection(double omssa, double mascot, double xtandem, boolean useIonIdent) {
        PeptideCollection pc = new PeptideCollection();
        double cutoff = 0.05;
        
        for (PeptideHit p:peptideHits) {
            switch (p.getSourceType()) {
                case MASCOT:  cutoff = mascot;  break;
                case OMSSA:   cutoff = omssa;   break;
                case XTANDEM: cutoff = xtandem; break;
            }
            if (p.getSourceType() == AnalysisProgramType.MASCOT && useIonIdent) {
                if (p.getIonScore() >= p.getIdent()) {
                    pc.addPeptideHit(p);
                }
            } else if (p.getExpect() <= cutoff) {
                pc.addPeptideHit(p);
            }
        }
        
        return pc;
    }
    
    public PeptideCollection getNonIndeterminents() {
        PeptideCollection pc = new PeptideCollection();
        
        for (PeptideHit p:peptideHits) {
            if (!p.isIndeterminate()) {
                pc.addPeptideHit(p);
            }
        }
        return pc;
    }
    
    public void createProteinList() {
        minProteins = new HashMap<String, Protein>();
        for (Peptide pg:minPeptides.values()) {
            String pepName = pg.getSequence();
            for (String protName:pg.getProteins()) {
                if (minProteins.containsKey(protName)) {
                    Protein prot = minProteins.get(protName);
                    prot.addPeptide(pepName);
                } else {
                    Protein prot = new Protein();
                    prot.setName(protName);
                    prot.addPeptide(pepName);
                    prot.setCluster(cluster_num);
                    minProteins.put(protName,prot);
                }
            }
        }
        for (Protein p:minProteins.values()) {
            MassSieveFrame.addProtein(p.getName());
        }
    }
    
    public void updateProteins(HashMap<String, Protein> mainProteins) {
        minProteins = new HashMap<String, Protein>();
        for (Peptide pg:minPeptides.values()) {
            String pepName = pg.getSequence();
            for (String protName:pg.getProteins()) {
                if (minProteins.containsKey(protName)) {
                    Protein prot = minProteins.get(protName);
                    prot.addPeptide(pepName);
                } else {
                    Protein prot = mainProteins.get(protName);
                    minProteins.put(protName,prot);
                }
            }
        }
        for (Peptide pg:minPeptides.values()) {
            pg.updateProteins(minProteins);
        }
        for (Protein p:minProteins.values()) {
            p.updatePeptides(minPeptides);
        }
    }
    
    public void updateClusters() {
        clusters = new HashMap<Integer, PeptideCollection>();
        for (Peptide pg:minPeptides.values()) {
            pg.setCluster(-1);
        }
        int cluster_num = 1;
        for (Peptide pg:minPeptides.values()) {
            if (pg.getCluster() == -1) {
                PeptideCollection newCluster = new PeptideCollection();
                newCluster.setClusterNum(cluster_num);
                pg.setCluster(cluster_num);
                newCluster.addPeptideGroup(pg);
                addLinkedMembers(newCluster, cluster_num, pg);
                newCluster.updateProteins(minProteins);
                newCluster.updateParsimony();
                clusters.put(cluster_num,newCluster);
                cluster_num++;
            }
        }
        int equivGroup = 0;
        HashSet<String> usedProteins = new HashSet<String>();
        ArrayList<Protein> equivOrder = new ArrayList<Protein>();
        equivOrder.addAll(getCountables());
        equivOrder.addAll(getSubsets());
        equivOrder.addAll(getSubsumables());
        for (Protein p:equivOrder) {
            if (!usedProteins.contains(p.getName())) {
                p.setEquivalentGroup(equivGroup);
                usedProteins.add(p.getName());
                for (Protein ps:p.getEquivalent()) {
                    ps.setEquivalentGroup(equivGroup);
                    usedProteins.add(ps.getName());
                }
                equivGroup++;
            }
        }
    }
    
    private void addLinkedMembers(PeptideCollection cluster, Integer clustNum, Peptide pg) {
        for (String proName:pg.getProteins()) {
            Protein pro = minProteins.get(proName);
            pro.setCluster(clustNum);
            for (String pepName:pro.getPeptides()) {
                Peptide subPg = minPeptides.get(pepName);
                if (subPg.getCluster() == -1) {
                    subPg.setCluster(clustNum);
                    cluster.addPeptideGroup(subPg);
                    addLinkedMembers(cluster, clustNum, subPg);
                }
            }
        }
    }
    
    public void updateParsimony() {
        for (Protein prot:minProteins.values()) {
            for (String pepName:prot.getPeptides()) {
                Peptide pep = minPeptides.get(pepName);
                prot.addAssociatedProteins(pep.getProteins());
            }
        }
        for (Protein prot:minProteins.values()) {
            prot.updateParsimony(minProteins);
        }
        for (Protein prot:minProteins.values()) {
            prot.computeParsimonyType();
        }
    }
    
    public DefaultTreeModel getTree(ExperimentPanel expPanel) {
        DefaultMutableTreeNode root=new DefaultMutableTreeNode(expPanel.getName() + " Overview");
        root.add(getPeptideHitsTree(expPanel));
        root.add(getPeptideTree(expPanel));
        root.add(getProteinTree(expPanel));
        root.add(getClusterTree(expPanel));
        root.add(getParsimonyTree(expPanel));
        return new DefaultTreeModel(root);
    }
    
    private DefaultMutableTreeNode getClusterTree(ExperimentPanel expPanel) {
        DefaultMutableTreeNode root=new DefaultMutableTreeNode(getClusterListPanel(expPanel));
        DefaultMutableTreeNode child, grandchild;
        
        // Old: sort by cluster size
        //ArrayList<PeptideCollection> sortClusters = new ArrayList<PeptideCollection>();
        //sortClusters.addAll(clusters.values());
        //Collections.sort(sortClusters);
        //for (PeptideCollection pc:sortClusters) {
        
        // New: sort by cluster num
        ArrayList<Integer> sortClusterNum = new ArrayList<Integer>();
        sortClusterNum.addAll(clusters.keySet());
        Collections.sort(sortClusterNum);
        for (Integer i:sortClusterNum) {
            PeptideCollection pc = clusters.get(i);
            pc.overrideProteins(this.minProteins);
            child = new DefaultMutableTreeNode(pc);
            root.add(child);
            grandchild = pc.getPeptideTree(expPanel);
            child.add(grandchild);
            grandchild = pc.getProteinTree(expPanel);
            child.add(grandchild);
        }
        return root;
    }
    
    private void overrideProteins(HashMap<String, Protein> overrideProts) {
        for (String pName:minProteins.keySet()) {
            minProteins.put(pName, overrideProts.get(pName));
        }
    }
    
    private DefaultMutableTreeNode getPeptideTree(ExperimentPanel expPanel) {
        DefaultMutableTreeNode root=new DefaultMutableTreeNode(getPeptideListPanel(expPanel));
        DefaultMutableTreeNode child;
        ArrayList<Peptide> sortPeptides = new ArrayList<Peptide>();
        sortPeptides.addAll(minPeptides.values());
        Collections.sort(sortPeptides);
        for (Peptide pg:sortPeptides) {
            //child = pg.getTree();
            child = new DefaultMutableTreeNode(pg);
            root.add(child);
        }
        return root;
    }
    
    private DefaultMutableTreeNode getPeptideHitsTree(ExperimentPanel expPanel) {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(getPeptideHitListPanel(expPanel));
        return root;
    }
    
    private DefaultMutableTreeNode getProteinTree(ExperimentPanel expPanel) {
        ProteinListPanel plp = getProteinListPanel(expPanel);
        DefaultMutableTreeNode root=new DefaultMutableTreeNode(plp);
        DefaultMutableTreeNode child;
        ArrayList<Protein> sortProteins = new ArrayList<Protein>();
        sortProteins.addAll(minProteins.values());
        Collections.sort(sortProteins);
        for (Protein prot:sortProteins) {
            //child = prot.getTree(expPanel);
            child = new DefaultMutableTreeNode(prot);
            root.add(child);
        }
        return root;
    }
    
    
    private DefaultMutableTreeNode getParsimonyTree(ExperimentPanel expPanel) {
        DefaultMutableTreeNode root=new DefaultMutableTreeNode(getParsimonyListPanel(expPanel));
        DefaultMutableTreeNode child, grandchild;
        
        ProteinListPanel psPanel;
        // Discrete
        psPanel = new ProteinListPanel(expPanel);
        psPanel.addProteinList(getDiscretes(), experimentSet);
        psPanel.setName("Discrete (" + getDiscretes().size() + ")");
        child = new DefaultMutableTreeNode(psPanel);
        //for (Protein p:getDiscretes()) { child.add(p.getTree(expPanel)); }
        for (Protein p:getDiscretes()) { child.add(new DefaultMutableTreeNode(p)); }
        root.add(child);
        
        // Differentiable
        psPanel = new ProteinListPanel(expPanel);
        psPanel.addProteinList(getDifferentiables(), experimentSet);
        psPanel.setName("Differentiable (" + getDifferentiables().size() + ")");
        child = new DefaultMutableTreeNode(psPanel);
        //for (Protein p:getDifferentiables()) { child.add(p.getTree(expPanel)); }
        for (Protein p:getDifferentiables()) { child.add(new DefaultMutableTreeNode(p)); }
        root.add(child);
        
        // Superset
        psPanel = new ProteinListPanel(expPanel);
        child = new DefaultMutableTreeNode(psPanel);
        HashSet<String> usedProteins = new HashSet<String>();
        ArrayList<Protein> superset_subsets = new ArrayList<Protein>();
        for (Protein p:getSupersets()) {
            if (!usedProteins.contains(p.getName())) {
                if (p.getEquivalent().size() > 0) {
                    grandchild = new DefaultMutableTreeNode(p.getName() + " Group (" + (p.getEquivalent().size()+1) + ")");
                    //grandchild.add(p.getTree(expPanel));
                    grandchild.add(new DefaultMutableTreeNode(p));
                    usedProteins.add(p.getName());
                    superset_subsets.add(p);
                    for (Protein ps:p.getEquivalent()) {
                        //grandchild.add(ps.getTree(expPanel));
                        grandchild.add(new DefaultMutableTreeNode(ps));
                        usedProteins.add(ps.getName());
                    }
                    child.add(grandchild);
                } else {
                    //child.add(p.getTree(expPanel));
                    child.add(new DefaultMutableTreeNode(p));
                    usedProteins.add(p.getName());
                    superset_subsets.add(p);
                }
            }
        }
        psPanel.addProteinList(superset_subsets, experimentSet);
        psPanel.setName("Superset (" + superset_subsets.size() + ")");
        root.add(child);
        
        // Subsumable
        psPanel = new ProteinListPanel(expPanel);
        child = new DefaultMutableTreeNode(psPanel);
        usedProteins = new HashSet<String>();
        ArrayList<Protein> subsumable_subsets = new ArrayList<Protein>();
        for (Protein p:getSubsumables()) {
            if (!usedProteins.contains(p.getName())) {
                if (p.getEquivalent().size() > 0) {
                    grandchild = new DefaultMutableTreeNode(p.getName() + " Group (" + (p.getEquivalent().size()+1) + ")");
                    //grandchild.add(p.getTree(expPanel));
                    grandchild.add(new DefaultMutableTreeNode(p));
                    usedProteins.add(p.getName());
                    subsumable_subsets.add(p);
                    for (Protein ps:p.getEquivalent()) {
                        //grandchild.add(ps.getTree(expPanel));
                        grandchild.add(new DefaultMutableTreeNode(ps));
                        usedProteins.add(ps.getName());
                    }
                    child.add(grandchild);
                } else {
                    //child.add(p.getTree(expPanel));
                    child.add(new DefaultMutableTreeNode(p));
                    usedProteins.add(p.getName());
                    subsumable_subsets.add(p);
                }
            }
        }
        psPanel.addProteinList(subsumable_subsets, experimentSet);
        psPanel.setName("Subsumable (" + subsumable_subsets.size() + ")");
        root.add(child);
        
        // Subset
        psPanel = new ProteinListPanel(expPanel);
        child = new DefaultMutableTreeNode(psPanel);
        usedProteins = new HashSet<String>();
        ArrayList<Protein> subset_subsets = new ArrayList<Protein>();
        for (Protein p:getSubsets()) {
            if (!usedProteins.contains(p.getName())) {
                if (p.getEquivalent().size() > 0) {
                    grandchild = new DefaultMutableTreeNode(p.getName() + " Group (" + (p.getEquivalent().size()+1) + ")");
                    //grandchild.add(p.getTree(expPanel));
                    grandchild.add(new DefaultMutableTreeNode(p));
                    usedProteins.add(p.getName());
                    subset_subsets.add(p);
                    for (Protein ps:p.getEquivalent()) {
                        //grandchild.add(ps.getTree(expPanel));
                        grandchild.add(new DefaultMutableTreeNode(ps));
                        usedProteins.add(ps.getName());
                    }
                    child.add(grandchild);
                } else {
                    //child.add(p.getTree(expPanel));
                    child.add(new DefaultMutableTreeNode(p));
                    usedProteins.add(p.getName());
                    subset_subsets.add(p);
                }
            }
        }
        psPanel.addProteinList(subset_subsets, experimentSet);
        psPanel.setName("Subset (" + subset_subsets.size() + ")");
        root.add(child);
        
        // Equivalent
        psPanel = new ProteinListPanel(expPanel);
        child = new DefaultMutableTreeNode(psPanel);
        usedProteins = new HashSet<String>();
        ArrayList<Protein> equivalent_subsets = new ArrayList<Protein>();
        for (Protein p:getEquivalents()) {
            if (!usedProteins.contains(p.getName())) {
                if (p.getEquivalent().size() > 0) {
                    grandchild = new DefaultMutableTreeNode(p.getName() + " Group (" + (p.getEquivalent().size()+1) + ")");
                    //grandchild.add(p.getTree(expPanel));
                    grandchild.add(new DefaultMutableTreeNode(p));
                    usedProteins.add(p.getName());
                    equivalent_subsets.add(p);
                    for (Protein ps:p.getEquivalent()) {
                        //grandchild.add(ps.getTree(expPanel));
                        grandchild.add(new DefaultMutableTreeNode(ps));
                        usedProteins.add(ps.getName());
                    }
                    child.add(grandchild);
                } else {
                    //child.add(p.getTree(expPanel));
                    child.add(new DefaultMutableTreeNode(p));
                    usedProteins.add(p.getName());
                    equivalent_subsets.add(p);
                }
            }
        }
        psPanel.addProteinList(equivalent_subsets, experimentSet);
        psPanel.setName("Equivalent (" + equivalent_subsets.size() + ")");
        root.add(child);
        
        return root;
    }
    
    public Display getGraphDisplay(GraphLayoutType glType, final ExperimentPanel expPanel, String highlightName) {
        final Display display = new Display();
        int X = expPanel.getDetailWidth();
        int Y = expPanel.getDetailHeight();
        display.setSize(X, Y); // set display size
        //display.setHighQuality(true);
        //display.setPreferredSize(new Dimension(600,600));
        display.addControlListener(new DragControl()); // drag items around
        display.addControlListener(new PanControl());  // pan with background left-drag
        display.addControlListener(new WheelZoomControl()); // zoom with vertical right-drag
        display.addControlListener(new ZoomToFitControl(Visualization.ALL_ITEMS, 50, 500, Control.MIDDLE_MOUSE_BUTTON));
        display.addControlListener(new NeighborHighlightControl());
        
        Visualization vis = new Visualization();
        if (clusterGraph == null) { clusterGraph = this.toGraph(); }
        vis.add("graph", clusterGraph);
        LabelRenderer r = new LabelRenderer("name");
        r.setHorizontalAlignment(Constants.CENTER);
        //r.setRoundedCorner(8, 8); // round the corners
        
        DefaultRendererFactory rf = new DefaultRendererFactory(r);
        rf.setDefaultEdgeRenderer(new EdgeRenderer(Constants.EDGE_TYPE_CURVE));
        vis.setRendererFactory(rf);
        int[] palette = new int[] {ColorLib.rgb(255,180,180), ColorLib.rgb(190,190,255)};
        DataColorAction fill = new DataColorAction("graph.nodes", "type", Constants.NOMINAL, VisualItem.FILLCOLOR, palette);
        fill.add(VisualItem.FIXED, ColorLib.rgb(255,100,100));
        fill.add(VisualItem.HIGHLIGHT, ColorLib.rgb(255,200,125));
        int[] text_palette = new int[] {ColorLib.gray(0), ColorLib.gray(255)};
        DataColorAction text = new DataColorAction("graph.nodes", "indeterminate", Constants.NUMERICAL, VisualItem.TEXTCOLOR, text_palette);
        //ColorAction text = new ColorAction("graph.nodes", VisualItem.TEXTCOLOR, ColorLib.gray(0));
        ColorAction edges = new ColorAction("graph.edges", VisualItem.STROKECOLOR, ColorLib.gray(200));
        
        ActionList color = new ActionList(Activity.INFINITY);
        color.add(text);
        color.add(fill);
        color.add(edges);
        color.add(new RepaintAction());
        
        if (highlightName != null) {
            String pred  = "name='" + highlightName + "'";
            Iterator iter = vis.items("graph.nodes", ExpressionParser.predicate(pred));
            while ( iter.hasNext() ) {
                NodeItem ni = (NodeItem)iter.next();
                highlightedItem = ni;
                ni.setFixed(true);
                Iterator iterEdge = ni.edges();
                while ( iterEdge.hasNext() ) {
                    EdgeItem eItem = (EdgeItem)iterEdge.next();
                    NodeItem nItem = eItem.getAdjacentItem(ni);
                    if (eItem.isVisible()) {
                        eItem.setHighlighted(true);
                        nItem.setHighlighted(true);
                    }
                }
            }
        }
        
        ActionList layout = new ActionList();
        switch(glType) {
            case BALLOON_TREE:
                layout.add(new BalloonTreeLayout("graph"));
                break;
            case FORCE_DIRECTED:
                layout = new ActionList(Activity.INFINITY);
                layout.add(new ForceDirectedLayout("graph"));
                break;
            case NODE_LINK_TREE:
                layout.add(new NodeLinkTreeLayout("graph"));
                break;
            case RADIAL_TREE:
                layout.add(new RadialTreeLayout("graph"));
                break;
        }
        //layout.add(fill);
        layout.add(new RepaintAction());
        
        vis.putAction("color", color);
        vis.putAction("layout", layout);
        display.setVisualization(vis);
        vis.run("color");
        vis.run("layout");
        //Rectangle2D bounds = vis.getBounds(Visualization.ALL_ITEMS);
        //GraphicsLib.expand(bounds, 50 + (int)(1/display.getScale()));
        //DisplayLib.fitViewToBounds(display,bounds,1);
        //vis.runAfter("layout",1000);
        
        Control shutoffHighlight = new HoverActionControl("color") {
            //public void itemEntered(VisualItem item, MouseEvent evt) {
            //}
            public void itemExited(VisualItem item, MouseEvent evt) {
                if (highlightedItem != null) {
                    highlightedItem.setFixed(false);
                    Iterator iterEdge = highlightedItem.edges();
                    while ( iterEdge.hasNext() ) {
                        EdgeItem eItem = (EdgeItem)iterEdge.next();
                        NodeItem nItem = eItem.getAdjacentItem(highlightedItem);
                        if (eItem.isVisible()) {
                            eItem.setHighlighted(false);
                            nItem.setHighlighted(false);
                        }
                    }
                    highlightedItem = null;
                }
            }
        };
        display.addControlListener(shutoffHighlight);
        
        Control selectItem = new FocusControl() {
            public void itemClicked(VisualItem item, MouseEvent evt) {
                if (item.isInGroup("graph.nodes")) {
                    if (item.getString("type").equals("peptide")) {
                        Peptide pep = minPeptides.get(item.getString("name"));
                        expPanel.showPeptide(pep, true);
                    }
                    if (item.getString("type").equals("protein")) {
                        Protein pro = minProteins.get(item.getString("name"));
                        expPanel.showProtein(pro, true);
                    }
                    String pred  = "name='" + item.getString("name") + "'";
                    Iterator iter = item.getVisualization().items("graph.nodes", ExpressionParser.predicate(pred));
                    while ( iter.hasNext() ) {
                        NodeItem ni = (NodeItem)iter.next();
                        highlightedItem = ni;
                        ni.setFixed(true);
                        Iterator iterEdge = ni.edges();
                        while ( iterEdge.hasNext() ) {
                            EdgeItem eItem = (EdgeItem)iterEdge.next();
                            NodeItem nItem = eItem.getAdjacentItem(ni);
                            if (eItem.isVisible()) {
                                eItem.setHighlighted(true);
                                nItem.setHighlighted(true);
                            }
                        }
                    }
                    
                }
            }
        };
        display.addControlListener(selectItem);
        
        final JPopupMenu menu = new JPopupMenu();
        
        // Create and add a menu item
        JMenuItem printItem = new JMenuItem("Print This");
        printItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PrintUtilities.printComponent(display);
            }
        });
        menu.add(printItem);
        
        // Set the component to show the popup menu
        display.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent evt) {
                if (evt.isPopupTrigger()) {
                    menu.show(evt.getComponent(), evt.getX(), evt.getY());
                }
            }
            public void mouseReleased(MouseEvent evt) {
                if (evt.isPopupTrigger()) {
                    menu.show(evt.getComponent(), evt.getX(), evt.getY());
                }
            }
        });
        display.pan(X/2.0, Y/2.0);
        return display;
    }
    
    public Graph toGraph() {
        Table edgeTable = new Table();
        Table nodeTable = new Table();
        HashMap<String, Integer> unique = new HashMap<String, Integer>();
        
        edgeTable.addColumn("Node1", int.class);
        edgeTable.addColumn("Node2", int.class);
        nodeTable.addColumn("key", int.class);
        nodeTable.addColumn("name", String.class);
        nodeTable.addColumn("type", String.class);
        nodeTable.addColumn("indeterminate", int.class);
        
        int idx = 0;
        for (Protein prot:minProteins.values()) {
            int row = nodeTable.addRow();
            unique.put(prot.getName(), idx);
            nodeTable.setInt(row, "key", idx++);
            nodeTable.setString(row, "name", prot.getName());
            nodeTable.setString(row, "type", "protein");
            nodeTable.setInt(row, "indeterminate", 0);
        }
        
        for (Peptide pep:minPeptides.values()) {
            int row = nodeTable.addRow();
            unique.put(pep.getSequence(), idx);
            nodeTable.setInt(row, "key", idx++);
            nodeTable.setString(row, "name", pep.getSequence());
            nodeTable.setString(row, "type", "peptide");
            if (pep.getIndeterminateType() == PeptideIndeterminacyType.NONE) {
                nodeTable.setInt(row, "indeterminate", 0);
            } else {
                nodeTable.setInt(row, "indeterminate", 1);
            }
            
        }
        
        for (Protein prot:minProteins.values()) {
            int id1 = unique.get(prot.getName());
            for (String pep:prot.getPeptides()) {
                int id2 = unique.get(pep);
                int row = edgeTable.addRow();
                edgeTable.setInt(row,"Node1", id1);
                edgeTable.setInt(row,"Node2", id2);
            }
        }
        Graph g = new Graph(nodeTable, edgeTable, false, "key", "Node1", "Node2");
        //System.err.println(g.getEdgeCount());
        return g;
    }
    
    public void print() {
        for (Peptide pg:minPeptides.values()) {
            System.out.print(pg.getSequence() + "(" + pg.getPeptideHits().size() + ") ");
            if (pg.containsMascot()) System.out.print("Mascot(" + pg.getMascot().size() + ") ");
            if (pg.containsOmssa()) System.out.print("OMSSA(" + pg.getOmssa().size() + ") ");
            if (pg.containsXTandem()) System.out.print("XTandem(" + pg.getXTandem().size() + ") ");
            System.out.print("\t");
            for (String pro:pg.getProteins()) {
                System.out.print(pro + " ");
            }
            System.out.println();
        }
    }
    
    public PeptideCollection filterByPeptidePerProtein(int numPeps) {
        HashSet<String> proDiscard = new HashSet<String>();
        for (String pName:minProteins.keySet()) {
            Protein p = minProteins.get(pName);
            if (p.getNumUniquePeptides() < numPeps) {
                proDiscard.add(pName);
            }
        }
        PeptideCollection new_pc = new PeptideCollection();
        for (PeptideHit ph:peptideHits) {
            new_pc.addPeptideHit(ph.maskProtein(proDiscard));
        }
        new_pc.createProteinList();
        return new_pc;
    }
    
    public PeptideCollection filterByProteinCoverage(int minCoverage) {
        HashSet<String> proDiscard = new HashSet<String>();
        for (String pName:minProteins.keySet()) {
            Protein p = minProteins.get(pName);
            if (p.getCoveragePercent() < minCoverage) {
                proDiscard.add(pName);
            }
        }
        PeptideCollection new_pc = new PeptideCollection();
        for (PeptideHit ph:peptideHits) {
            new_pc.addPeptideHit(ph.maskProtein(proDiscard));
        }
        new_pc.createProteinList();
        return new_pc;
    }
    
    public PeptideCollection getPeptidesByHits(int numHits) {
        PeptideCollection new_pc = new PeptideCollection();
        for (Peptide pg:minPeptides.values()) {
            if (pg.getNumPeptideHits() >= numHits) {
                new_pc.minPeptides.put(pg.getSequence(),pg);
            }
        }
        new_pc.updatePeptideHits();
        return new_pc;
    }
    
    public PeptideCollection getOmssa() {
        PeptideCollection new_pc = new PeptideCollection();
        for (Peptide pg:minPeptides.values()) {
            if (pg.containsOmssa()) {
                new_pc.minPeptides.put(pg.getSequence(),pg);
            }
        }
        return new_pc;
    }
    
    public PeptideCollection getMascot() {
        PeptideCollection new_pc = new PeptideCollection();
        for (Peptide pg:minPeptides.values()) {
            if (pg.containsMascot()) {
                new_pc.minPeptides.put(pg.getSequence(),pg);
            }
        }
        return new_pc;
    }
    
    public PeptideCollection getXTandem() {
        PeptideCollection new_pc = new PeptideCollection();
        for (Peptide pg:minPeptides.values()) {
            if (pg.containsXTandem()) {
                new_pc.minPeptides.put(pg.getSequence(),pg);
            }
        }
        return new_pc;
    }
    
    public PeptideCollection getSequest() {
        PeptideCollection new_pc = new PeptideCollection();
        for (Peptide pg:minPeptides.values()) {
            if (pg.containsSequest()) {
                new_pc.minPeptides.put(pg.getSequence(),pg);
            }
        }
        return new_pc;
    }
    
    public PeptideCollection Union(PeptideCollection pc) {
        PeptideCollection new_pc = new PeptideCollection();
        new_pc.minPeptides.putAll(pc.minPeptides);
        for (String key:minPeptides.keySet()) {
            if (!new_pc.minPeptides.containsKey(key)) {
                new_pc.minPeptides.put(key,minPeptides.get(key));
            }
        }
        return new_pc;
    }
    
    public PeptideCollection Intersection(PeptideCollection pc) {
        PeptideCollection new_pc = new PeptideCollection();
        for (String key:minPeptides.keySet()) {
            if (pc.minPeptides.containsKey(key)) {
                new_pc.minPeptides.put(key,minPeptides.get(key));
            }
        }
        return new_pc;
    }
    
    public PeptideCollection Difference(PeptideCollection pc) {
        PeptideCollection new_pc = new PeptideCollection();
        for (String key:minPeptides.keySet()) {
            if (!pc.minPeptides.containsKey(key)) {
                new_pc.minPeptides.put(key,minPeptides.get(key));
            }
        }
        return new_pc;
    }
    
    public void setClusterNum(int i) {
        cluster_num = i;
    }
    
    public int getClusterNum() {
        return cluster_num;
    }
    
    public PeptideCollection getCluster(int i) {
        return clusters.get(i);
    }
    
    public int getNumElements() {
        return minPeptides.size() + minProteins.size();
    }
    
    public int compareTo(PeptideCollection pc) {
        int x = getNumElements();
        int y = pc.getNumElements();
        if (x > y) return -1;
        if (x < y) return 1;
        return 0;
    }
    
    public String toString() {
        return "Cluster " + cluster_num + " (" + getNumElements() + ')';
        //return "Cluster " + cluster_num;
    }
    
    public HashMap<String, Peptide> getMinPeptides() {
        return minPeptides;
    }
    
    public void setMinPeptides(HashMap<String, Peptide> minPeptides) {
        this.minPeptides = minPeptides;
    }
    
    public HashMap<String, Protein> getMinProteins() {
        return minProteins;
    }
    
    public void setMinProteins(HashMap<String, Protein> minProteins) {
        this.minProteins = minProteins;
    }
    
    public ArrayList<PeptideHit> getPeptideHits() {
        return peptideHits;
    }
    
    public HashSet<String> getExperimentSet() {
        return experimentSet;
    }
    
    private void categorizeProteins() {
        equivalents = new ArrayList<Protein>();
        subsets = new ArrayList<Protein>();
        supersets = new ArrayList<Protein>();
        subsumables = new ArrayList<Protein>();
        differentiables = new ArrayList<Protein>();
        discretes = new ArrayList<Protein>();
        countables = new ArrayList<Protein>();
        
        ArrayList<Protein> sortProteins = new ArrayList<Protein>();
        sortProteins.addAll(minProteins.values());
        Collections.sort(sortProteins);
        for (Protein p:sortProteins) {
            switch (p.getParsimonyType()) {
                case EQUIVALENT: equivalents.add(p); break;
                case SUBSET: subsets.add(p); break;
                case SUPERSET: supersets.add(p); break;
                case SUBSUMABLE: subsumables.add(p); break;
                case DIFFERENTIABLE: differentiables.add(p); break;
                case DISCRETE: discretes.add(p); break;
            }
        }
        
        countables.addAll(discretes);
        countables.addAll(differentiables);
        countables.addAll(supersets);
        countables.addAll(equivalents);
    }
    
    public ArrayList<Protein> getEquivalents() {
        if (equivalents == null) categorizeProteins();
        return equivalents;
    }
    
    public ArrayList<Protein> getSubsets() {
        if (subsets == null) categorizeProteins();
        return subsets;
    }
    
    public ArrayList<Protein> getSupersets() {
        if (supersets == null) categorizeProteins();
        return supersets;
    }
    
    public ArrayList<Protein> getSubsumables() {
        if (subsumables == null) categorizeProteins();
        return subsumables;
    }
    
    public ArrayList<Protein> getDifferentiables() {
        if (differentiables == null) categorizeProteins();
        return differentiables;
    }
    
    public ArrayList<Protein> getDiscretes() {
        if (discretes == null) categorizeProteins();
        return discretes;
    }
    
    public ArrayList<Protein> getCountables() {
        if (countables == null) categorizeProteins();
        return countables;
    }
    
    public Integer getCountablesCount() {
        if (countablesCount == null) {
            HashSet<Integer> uniqueEquivs = new HashSet<Integer>();
            for (Protein p:getCountables()) {
                uniqueEquivs.add(p.getEquivalentGroup());
            }
            countablesCount = uniqueEquivs.size();
        }
        return countablesCount;
    }
    
    public PeptideHitListPanel getPeptideHitListPanel(ExperimentPanel expPanel) {
        if (peptideHitListPanel == null) {
            peptideHitListPanel = new PeptideHitListPanel(expPanel);
            peptideHitListPanel.addProteinPeptideHitList(peptideHits);
            peptideHitListPanel.setName("Peptide Hits (" + peptideHits.size() + ")");
        }
        return peptideHitListPanel;
    }
    
    public PeptideListPanel getPeptideListPanel(ExperimentPanel expPanel) {
        if (peptideListPanel == null) {
            peptideListPanel = new PeptideListPanel(expPanel);
            peptideListPanel.addPeptideList(new ArrayList<Peptide>(minPeptides.values()), experimentSet);
            peptideListPanel.setName("Peptides (" + minPeptides.size() + ")");
        }
        return peptideListPanel;
    }
    
    public ProteinListPanel getProteinListPanel(ExperimentPanel expPanel) {
        if (proteinListPanel == null) {
            proteinListPanel = new ProteinListPanel(expPanel);
            proteinListPanel.addProteinList(new ArrayList<Protein>(minProteins.values()), experimentSet);
            proteinListPanel.setName("Proteins (" + minProteins.size() + ")");
        }
        return proteinListPanel;
    }
    
    public ProteinGroupListPanel getClusterListPanel(ExperimentPanel expPanel) {
        if (clusterListPanel == null) {
            clusterListPanel = new ProteinGroupListPanel(expPanel);
            clusterListPanel.addProteinList(new ArrayList<Protein>(minProteins.values()), experimentSet, true);
            clusterListPanel.setName("Clusters (" + clusters.size() + ")");
        }
        return clusterListPanel;
    }
    
    public ProteinGroupListPanel getParsimonyListPanel(ExperimentPanel expPanel) {
        if (parsimonyListPanel == null) {
            parsimonyListPanel = new ProteinGroupListPanel(expPanel);
            parsimonyListPanel.addProteinList(getCountables(), experimentSet, false);
            parsimonyListPanel.setName("Parsimony (" + getCountablesCount() + ")");
        }
        return parsimonyListPanel;
    }
}
