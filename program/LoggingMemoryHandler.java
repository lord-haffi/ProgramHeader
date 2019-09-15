/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package program;

import java.io.IOException;
import java.util.ResourceBundle;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.XMLFormatter;
import javax.swing.JOptionPane;

/**
 * This memory
 * @author LeonWin
 */
public class LoggingMemoryHandler extends Handler {
    private static final Level errorLevel = Level.SEVERE;
    
    private int head=0, count=0;
    private LogRecord[] buffer;
    
    public LoggingMemoryHandler(int size){
        buffer = new LogRecord[size];
        setFormatter(new BundleFormatter(false));
    }
    
    @Override
    public void publish(LogRecord lr) {
        //System.out.println(lr.getLevel().getName()+": "+lr.getMessage());
        if(isLoggable(lr)){
            buffer[head] = lr;
            head = (head+1)%buffer.length;
            if(count < buffer.length)
                count++;
            if(lr.getLevel().intValue() >= errorLevel.intValue())
                error();
        }
    }
    public void error(){
        Program.interrupt();
        setLevel(Level.OFF);
        int code = ErrorReport.showErrorReport(buffer, head-1, count);
        ResourceBundle bundle = ResourceBundle.getBundle("program/lan", Program.getLocale());
        if(code != ErrorReport.CANCEL){
            String target = ErrorReport.getSelectedFile();
            try {
                FileHandler targetH = new FileHandler(target);
                targetH.setFormatter(new XMLFormatter());
                for(int i=0; i<count; i++){
                    int ind = head-1-i;
                    if(ind < 0) ind += buffer.length;
                    buffer[ind].setMessage(getFormatter().format(buffer[ind]));
                    targetH.publish(buffer[ind]);
                }
                targetH.close();
            } catch (IllegalArgumentException | IOException | SecurityException ex) {
                JOptionPane.showMessageDialog(null, bundle.getString("ERRF").replaceFirst("{0}", target), Program.getTitle()+" - "+bundle.getString("ERR"), JOptionPane.ERROR_MESSAGE);
            }
            if(code == ErrorReport.SAVESEND){
                // TODO
                System.out.println("Send report");
            }
        }
        close();
        JOptionPane.showMessageDialog(null, bundle.getString("PLSRES"), Program.getTitle(), JOptionPane.WARNING_MESSAGE);
        System.exit(0);
    }

    @Override
    public void flush() {
        
    }

    @Override
    public void close() throws SecurityException {
        buffer = null;
        head = count = 0;
    }
    
}
