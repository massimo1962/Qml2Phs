/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package it.ingv;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author stefano
 */
public class Options extends javax.swing.JDialog {

    private Properties prop = new Properties();

    /**
     * Creates new form Options
     * @param parent
     * @param modal
     */
    public Options(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        try {
            this.load();
        } catch (InvalidPropertiesFormatException ex) {
            //LOG.log(Level.SEVERE, null, ex);
        } 
        initComponents();
        jTextAreaURIlst.setText(prop.getProperty("URI_WEBSERVICE_DEFAULT"));
        this.jRadioButtonExtended.setSelected(Boolean.parseBoolean(prop.getProperty("phs_extended")));
        this.jRadioButtonStandard.setSelected(!Boolean.parseBoolean(prop.getProperty("phs_extended")));
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jButtonSaveOptions = new javax.swing.JButton();
        jRadioButtonStandard = new javax.swing.JRadioButton();
        jRadioButtonExtended = new javax.swing.JRadioButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextAreaURIlst = new javax.swing.JTextArea();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jButtonSaveOptions.setText("Save");
        jButtonSaveOptions.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSaveOptionsActionPerformed(evt);
            }
        });

        buttonGroup1.add(jRadioButtonStandard);
        jRadioButtonStandard.setText("Phs standard");

        buttonGroup1.add(jRadioButtonExtended);
        jRadioButtonExtended.setText("Phs extended");
        jRadioButtonExtended.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonExtendedActionPerformed(evt);
            }
        });

        jTextAreaURIlst.setColumns(20);
        jTextAreaURIlst.setRows(5);
        jScrollPane1.setViewportView(jTextAreaURIlst);

        jLabel1.setText("Preferences");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(53, 53, 53)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jRadioButtonExtended)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButtonSaveOptions)
                        .addGap(22, 22, 22))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jRadioButtonStandard)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 189, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButtonStandard)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButtonSaveOptions)
                    .addComponent(jRadioButtonExtended))
                .addGap(29, 29, 29))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jRadioButtonExtendedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonExtendedActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jRadioButtonExtendedActionPerformed

    private void jButtonSaveOptionsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSaveOptionsActionPerformed
        try {
            // TODO add your handling code here:
            prop.setProperty("URI_WEBSERVICE_DEFAULT",this.jTextAreaURIlst.getText());
            prop.setProperty("phs_extended", Boolean.toString(jRadioButtonExtended.isSelected()) );
            
            this.store();
        } catch (IOException ex) {
            Logger.getLogger(Options.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButtonSaveOptionsActionPerformed
    
     /*
     * load of property file
     */
    public final void load() throws InvalidPropertiesFormatException{
        FileInputStream fis ;
        try {
            fis = new FileInputStream("qml_to_phs_properties.xml");
            prop.loadFromXML( fis );
        } catch (IOException ex) {
            Logger.getLogger(Options.class.getName()).log(Level.SEVERE, null, ex);
        }
    
    }
    
     /*
     * return true if correct store of property into the file
     */        
    public boolean store() throws FileNotFoundException, IOException{
        FileOutputStream fos;
        fos = new FileOutputStream("qml_to_phs_properties.xml");
        prop.storeToXML(fos, "jT properties file");
        return false;
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton jButtonSaveOptions;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JRadioButton jRadioButtonExtended;
    private javax.swing.JRadioButton jRadioButtonStandard;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextAreaURIlst;
    // End of variables declaration//GEN-END:variables

    public Boolean isPhs_extended() {
        return Boolean.parseBoolean(prop.getProperty("phs_extended"));
    }

    public void setPhs_extended(Boolean phs_extended) {
        prop.setProperty("phs_extended", Boolean.toString(phs_extended));       
    }

    public String getURI_WEBSERVICE_DEFAULT() {
        return prop.getProperty("URI_WEBSERVICE_DEFAULT");
    }

    public void setURI_WEBSERVICE_DEFAULT(String URI_WEBSERVICE_DEFAULT) {
        prop.setProperty("URI_WEBSERVICE_DEFAULT", URI_WEBSERVICE_DEFAULT);       
    }
    
    public void setEventVersion(String EventVersion) {
        prop.setProperty("EventVersion", EventVersion);       
    }
    
    public String getEventVersion() {
        return prop.getProperty("EventVersion");
    }
    
}
