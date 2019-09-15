/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package program;

import java.util.logging.LogRecord;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author leon
 */
public class ParamBundleFormatter extends BundleFormatter {
    public ParamBundleFormatter (boolean translate) {
        super(translate);
    }
    
    @Override
    public String format (LogRecord lr) {
        Object[] params = lr.getParameters();
        Matcher matcher = Pattern.compile("\\{(\\d+)\\}").matcher(super.format(lr));
        StringBuffer ret = new StringBuffer();
        
        while (matcher.find()) {
            try {
                int paramInd = Integer.parseInt(matcher.group(1));
                matcher.appendReplacement(ret, paramInd < params.length ? params[paramInd].toString() : "{"+paramInd+"}");
            } catch (NumberFormatException e) { //This should never happen
                matcher.appendReplacement(ret, "{NumberFormatException with:"+matcher.group(1)+"}");
            }
        }
        matcher.appendTail(ret);
        
        return ret.toString();
    }
}
