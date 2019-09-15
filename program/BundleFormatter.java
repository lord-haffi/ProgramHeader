/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package program;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 *
 * @author LeonWin
 */
public class BundleFormatter extends Formatter{
    //public final static ProgramBundleFormatter BASIC = new ProgramBundleFormatter(false), TRANS = new ProgramBundleFormatter(true);
    
    protected final boolean translate;
    public BundleFormatter (boolean translate){
        this.translate = translate;
    }
    
    @Override
    public String format (LogRecord lr) {
        if (lr.getResourceBundle() == null)
            return lr.getMessage();
        ResourceBundle bundle = translate ? ResourceBundle.getBundle(lr.getResourceBundleName(), Program.getLocale()) : lr.getResourceBundle();
        
        Matcher matcher = Pattern.compile("%([^%]*)%").matcher(lr.getMessage());
        StringBuffer ret = new StringBuffer();
        while (matcher.find()) {
            String key = matcher.group(1), value = key.equals("") ? "%" : bundle.getString(key);
            matcher.appendReplacement(ret, value != null ? value : "%"+key+"%");
        }
        matcher.appendTail(ret);
        
        return ret.toString();
    }
}
