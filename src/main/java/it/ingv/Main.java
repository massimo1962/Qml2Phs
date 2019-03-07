/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.ingv;

import static it.ingv.MainFrame.helpMe;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author massimo.fares@ingv.it
 * 
 */
public class Main  {
    
    private static final Logger LOG = Logger.global;
    public static Level level = null;
    
        
    /*
    *
    *   @param args the command line arguments
    *
    */
    public static void main(final String[] args) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        
        /*
        *
        *   Master switch between GUI & CommandLine
        *
        */
        
        if (args.length > 0) {      /* Go to command line process */
                    
            if (args[0].equalsIgnoreCase("-h") || args[0].equalsIgnoreCase("--help")) helpMe("help");
            if (args[0].equalsIgnoreCase("-v") || args[0].equalsIgnoreCase("--version")) helpMe("version");
            if (args[0].equalsIgnoreCase("-c") || args[0].equalsIgnoreCase("--config")) helpMe("config");
            
            MainLine.loadFromCommandLine(args);
            //;
        }
        else {                      /* Go to GUI process */
            try {
                for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        javax.swing.UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
                java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            }


            /* Create and display the form */
            java.awt.EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    
                    // ** swing Frame main
                    javax.swing.JFrame frameMain =  new MainFrame();
                                       
                    frameMain.setVisible(true);
                    System.out.println("Parser main:  " + (new Date()));
                    
                }
            });
        }
    }
    
    public static void myPrint (String textToPrint){
        System.out.println("inside main:  " + textToPrint);
    }
    
    
    
    
    
    
}
