/*
 * PrintUtilities.java
 *
 * Created on August 9, 2006, 12:15 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package gov.nih.nimh.mass_sieve.gui;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import javax.swing.RepaintManager;
import prefuse.Display;

/**
 *
 * @author slotta
 */
public class PrintUtilities implements Printable {
    private Component componentToBePrinted;
    
    public static void printComponent(Component c) {
        new PrintUtilities(c).print();
    }
    
    public PrintUtilities(Component componentToBePrinted) {
        this.componentToBePrinted = componentToBePrinted;
    }
    
    public void print() {
        PrinterJob printJob = PrinterJob.getPrinterJob();
        printJob.setPrintable(this);
        if (printJob.printDialog())
            try {
                printJob.print();
            } catch(PrinterException pe) {
                System.out.println("Error printing: " + pe);
            }
    }
    
    public int print(Graphics g, PageFormat pageFormat, int pageIndex) {
        if (pageIndex > 0) {
            return(Printable.NO_SUCH_PAGE);
        } else {
            Graphics2D g2d = (Graphics2D)g;
            g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
            disableDoubleBuffering(componentToBePrinted);
            if (componentToBePrinted instanceof Display) {
                Display d = (Display)componentToBePrinted;
                d.setHighQuality(true);
                //d.printComponents(g2d);
                d.paint(g2d);
                d.setHighQuality(false);
            } else {
            componentToBePrinted.paint(g2d);
            }
            enableDoubleBuffering(componentToBePrinted);
            return(Printable.PAGE_EXISTS);
        }
    }
    
    public static void disableDoubleBuffering(Component c) {
        RepaintManager currentManager = RepaintManager.currentManager(c);
        currentManager.setDoubleBufferingEnabled(false);
    }
    
    public static void enableDoubleBuffering(Component c) {
        RepaintManager currentManager = RepaintManager.currentManager(c);
        currentManager.setDoubleBufferingEnabled(true);
    }
}

