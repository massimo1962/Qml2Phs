/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.ingv;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 *
 * @author massimofares
 */
//Classe per gestire il log all'interno della textarea 
class LogHandler extends Handler {

    private javax.swing.JFrame mainFrame;
    public LogHandler handler = null;
    
    public LogHandler(Level level, javax.swing.JFrame mainFrame) {
        super();
        //leggo dalle opzioni il livello di log
        //setLevel(level.parse(Main.G.Options.getLogLevel())); //TODO
        this.mainFrame = mainFrame;
        int LOG_SIZE=10000;
        int LOG_ROTATION_COUNT=3;
        Handler handler=null;
        try {
            handler = new FileHandler( System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") +  "qml_to_phs.log", LOG_SIZE, LOG_ROTATION_COUNT);
        } catch (IOException | SecurityException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }

        //Il logger prende tutti i livelli
        Logger.global.setLevel(Level.ALL);
        //Il file handler parsa e salva solo da SEVERE in su
        handler.setLevel(Level.INFO);
        Logger.global.addHandler(handler);

        
    
        
        
    
    
    
    }
    public synchronized void publish(LogRecord record) {
      String message = null;
      if (!isLoggable(record))
        return;
      message = getFormatter().format(record);
//      showInfo(message);
    }
    public void close() {
    }
    public void flush() {
    }

    private void showInfo(String data, javax.swing.JFrame mainFrame) {
//      mainFrame.jTextAreaLog.append(data);
    }

}
    
    
       
    
    
    
    
