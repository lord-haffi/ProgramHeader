/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package program;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author leon
 */
public class State extends HashMap <String, Attribute<?>> {
    private static Logger log = Logger.getLogger(State.class.getName());
    @Override
    public Attribute get (Object key) {
        Attribute ret = super.get(key);
        if (ret == null)
            log.log(Level.WARNING, "%ATTNOTFOUND%", key);
        return ret;
    }
    @Override
    public Attribute put (String key, Attribute obj) {
        log.log(Level.FINE, "Added attribute: {0} -> {1}", new Object[]{key, obj});
        return super.put(key, obj);
    }
    public Attribute put (Attribute att) {
        return put(att.getKey(), att);
    }
}
