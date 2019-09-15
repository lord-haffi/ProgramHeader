/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package program;

import java.io.File;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.LogRecord;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.logging.ConsoleHandler;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 *
 * @author LeonWin
 */
public class Program {
    private static final Logger log = Logger.getLogger(Program.class.getName());
    public static final String[][] CODES = {
        {"userdir", System.getProperty("user.dir")},
        {"userhome", System.getProperty("user.home")},
        {"appdata", System.getProperty("user.home")+System.getProperty("file.separator")+"AppData"+System.getProperty("file.separator")+"Local"}
    };
    
    private static String name = "Not defined", version = "Not defined", workDir = null;
    //private static boolean interruptProgram = false;
    private static final State state = new State();
    private static Locale locale = Locale.getDefault();
    private static ResourceBundle metadata = null;
    private static Charset defaultCharset = StandardCharsets.UTF_8;
    private static SettingMap settings = null;
    
    private static class SettingMap extends HashMap <String, String> {
        public final File file;
        public SettingMap (File file) {
            this.file = file;
        }
        @Override
        public synchronized String get (Object key) {
            String ret = super.get(key);
            if (ret == null)
                log.logp(Level.WARNING, "program.Program.SettingMap", "get", "%SENOTFOUND%", key);
            return ret;
        }
        @Override
        public synchronized String put (String key, String obj) {
            log.logp(Level.FINE, "program.Program.SettingMap", "put", "Added/changed setting: {0} -> {1}", new Object[]{key, obj});
            return super.put(key, obj);
        }
        /**
         * Saves the current storaged settings to the settings-file or prints an error-message if it fails.
         */
        public synchronized void saveSettings () {
            String data_str = "";
            for (Map.Entry<String, String> entry : entrySet())
                data_str += entry.getKey()+":"+entry.getValue()+"\r\n";
            
            try {
                Streamer.writeData(data_str.getBytes(defaultCharset), file, true, false);
                log.logp(Level.CONFIG, "program.Program.SettingMap", "saveSettings", "Saved settings");
            } catch (Streamer.StreamingException e) {
                log.logp(Level.SEVERE, "program.Program.SettingMap", "saveSettings", "%ERRSAVE%", e);
            }
        }
    }
    
    public static String getTitle () {
        return name+" v"+version;
    }
    public static String getName () {
        return name;
    }
    public static String getVersion () {
        return version;
    }
    public static ResourceBundle getMetadata () {
        return metadata;
    }
    public static Locale getLocale () {
        return locale;
    }
    public static Charset getDefaultCharset () {
        return defaultCharset;
    }
    @SuppressWarnings("unchecked")
    public static <T> T getState (String key, Class<T> type) {
        try {
            return ((Attribute<T>)state.get(key)).getValue();
        } catch (ClassCastException | NullPointerException e) {
            log(log, Level.SEVERE, "program.Program", "getState", "%ERRATT%", e, key, type.getName());
            return null;
        }
    }
    @SuppressWarnings("unchecked")
    public static <T> void setState (String key, T newVal) {
        try {
            ((Attribute<T>)state.get(key)).setValue(newVal);
        } catch (ClassCastException | NullPointerException e) {
            log(log, Level.SEVERE, "program.Program", "setState", "%ERRATT%", e, key, newVal.getClass().getName());
        }
    }
    public static String getSetting (String key) {
        return settings != null ? settings.get(key) : null;
    }
    public static void setSetting (String key, String newVal) {
        if (settings != null)
            settings.put(key, newVal);
    }
    public static boolean interrupted () {
        return getState("interrupted", Boolean.class);
    }
    public static void interrupt () {
        setState("interrupted", true);
    }
    
    public static void init (ResourceBundle metadata, Attribute[] atts, boolean logOnConsole) {
        initLogging(logOnConsole);
        initProgramState(atts);
        initMetadata(metadata);
        initSettings();
//        interpretArgs(args);
        log.logp(Level.CONFIG, "program.Program", "init", "Initiation finished: init-program of class 'Program'");
    }
    public static void initLogging (boolean logOnConsole) {
        Logger rootLog = Logger.getLogger("");
        rootLog.removeHandler(rootLog.getHandlers()[0]);
        
        Handler h = new LoggingMemoryHandler(64);
        h.setLevel(Level.ALL);
        rootLog.addHandler(h);
        if (logOnConsole) {
            ConsoleHandler ch = new ConsoleHandler();
            ch.setLevel(Level.ALL);
            ch.setFormatter(new ParamBundleFormatter(true));
            rootLog.addHandler(ch);
        }
        
        log.setLevel(Level.ALL);
        log.setResourceBundle(ResourceBundle.getBundle("program/lan"));
        
        log.logp(Level.CONFIG, "program.Program", "initLogging", "Initiation finished: logging");
    }
    public static void initProgramState (Attribute[] atts) {
        state.put(new Attribute<Boolean>("interrupted", false));
        if (atts != null)
            for (Attribute att : atts)
                state.put(att);
        log.logp(Level.CONFIG, "program.Program", "initProgramState", "Initiation finished: program-state");
    }
    public static void initMetadata (ResourceBundle metadata) {
        if (metadata == null) {
            log.logp(Level.SEVERE, "program.Program", "initMetadata", "Illegal Argument for metadata: null");
            return;
        }
        log.logp(Level.FINER, "program.Program", "initMetadata", "Interpreting metadata ResourceBundle: {0}", metadata.getBaseBundleName());
        Program.metadata = metadata;
        name = metadata.getString("programName");
        version = metadata.getString("programVersion");
        log.logp(Level.CONFIG, "program.Program", "initMetadata", "Loaded program name ({0}) and version ({1})", new Object[]{name, version});
        try {
            defaultCharset = Charset.forName(metadata.getString("defaultCharset"));
            log.logp(Level.CONFIG, "program.Program", "initMetadata", "Loaded charset {0}", defaultCharset.name());
        } catch (IllegalArgumentException ex) {
            log.logp(Level.SEVERE, "program.Program", "initMetadata", "Could not load charset", ex);
        }
        try {
            workDir = interpret(metadata.getString("workDir"));
            log.logp(Level.CONFIG, "program.Program", "initMetadata", "Loaded working directory {0}", workDir);
        } catch (IllegalArgumentException ex) {
            log.logp(Level.SEVERE, "program.Program", "initMetadata", "Could not load working directory", ex);
        }
        log.logp(Level.CONFIG, "program.Program", "initMetadata", "Initiation finished: metadata");
    }
    public static void initSettings() {
        settings = new SettingMap(new File(workDir+"settings"));
        byte[] dat = null;
        try {
            dat = Streamer.readData(settings.file);
        } catch (Streamer.StreamingException e) {
            log.logp(Level.SEVERE, "program.Program", "initSettings", "Could not read settings file", e);
        }
        log.logp(Level.FINER, "program.Program", "initSettings", "Readed settings-file ({0})", settings.file.getAbsolutePath());
        String[] list = new String(dat, defaultCharset).split("\r\n");
        for (String setting : list) {
            if (!setting.trim().equals("")) {
                int ind = setting.indexOf(":");
                if (ind == -1)
                    log.logp(Level.SEVERE, "program.Program", "initSettings", "Unallowed syntax in settings-file",
                            new IllegalStringSyntaxException(setting, "missing ':'"));
                settings.put(setting.substring(0, ind), setting.substring(ind+1));
            }
        }
        log.logp(Level.FINER, "program.Program", "initSettings", "Added settings to map");
        
        try {
            String lan = settings.get("lan");
            if (!lan.equalsIgnoreCase("default")) {
                locale = new Locale(lan);
                Locale.setDefault(locale);
            }
        } catch (NullPointerException e) {
            log.logp(Level.SEVERE, "program.Program", "initSettings", "Missing language-setting in settings-file");
        }
        log.logp(Level.CONFIG, "program.Program", "initSettings", "Loaded settings");
    }
//    public static void interpretArgs(String[] args) {
//        
//    }
    
    /*public static String interpret(String line) throws IllegalArgumentException{
        log.entering("program.Program", "interpret", line);
        if (line == null)
            throw new IllegalArgumentException("Couldn't find the meta-info 'workDir'.");
        //START Interpret line
        String interpretedLine = "";
        String toReplace = null, replacement = null;
        for (int i=0; i<line.length(); i++) {
            char cur = line.charAt(i);
            if (toReplace == null && cur == '%')
                toReplace = "";
            else if (toReplace != null && cur == '%') {
                for (String[] code : codes) {
                    if (toReplace.startsWith(code[0])) {
                        String ext = toReplace.substring(code[0].length());
                        replacement = code[1];
                        File curFile = new File(replacement);
                        while (ext.length() > 0) {
                            if (ext.startsWith(".parent")) {
                                curFile = curFile.getParentFile();
                                ext = ext.substring(".parent".length());
                            }else
                                throw new IllegalStringSyntaxException(line, toReplace);
                        }
                        replacement = curFile.getAbsolutePath();
                    }
                }
                if (replacement == null)
                    throw new IllegalStringSyntaxException(line, toReplace);
                interpretedLine+=replacement;
                toReplace = replacement = null;
            }else if (toReplace != null)
                toReplace+=cur;
            else if (cur == '/')
                interpretedLine += File.pathSeparator;
            else
                interpretedLine += cur;
        }
        //END Interpret line
        return interpretedLine;
    }*/
    public static String interpret (String line) throws IllegalArgumentException {
        log.entering("program.Program", "interpret", line);
        if (line == null)
            throw new IllegalArgumentException("Couldn't find the meta-info 'workDir'.");
        //START Interpret line
        Matcher matcher = Pattern.compile("(/)|(\\\\)|%([^%]*)%").matcher(line);
        StringBuffer interpretedLine = new StringBuffer();
        
        while (matcher.find()) {
            if (matcher.group(1) != null || matcher.group(2) != null)
                matcher.appendReplacement(interpretedLine, File.separator);
            else {
                String[] parts = matcher.group(3).split("\\.");
                String path = null;
                for (String[] code : CODES)
                    if (parts[0].equals(code[0]))
                        path = code[1];
                if (path == null)
                    throw new IllegalStringSyntaxException(line, parts[0], "Unknown Code");
                File curFile = new File(path);
                for (int i = 1; i < parts.length; i++) {
                    switch (parts[i]) {
                        case "parent":
                            curFile = curFile.getParentFile();
                            break;
                        default:
                            throw new IllegalStringSyntaxException(line, parts[i], "Unknown attribute");
                    }
                }
                matcher.appendReplacement(interpretedLine, curFile.getAbsolutePath());
            }
        }
        matcher.appendTail(interpretedLine);
        //END Interpret line
        log.exiting("program.Program", "interpret", interpretedLine.toString());
        return interpretedLine.toString();
    }
    public static void log (Logger logger, Level level, String className, String methodName, String message, Throwable th, Object... params) {
        try {
            LogRecord rec = new LogRecord(level, message);
            rec.setResourceBundle(logger.getResourceBundle());
            rec.setThrown(th);
            rec.setParameters(params);
            rec.setSourceClassName(className);
            rec.setSourceMethodName(methodName);
            logger.log(rec);
        } catch (NullPointerException e) {
            log.log(Level.SEVERE, "%ERRLOGARG%", e);
        }
    }
}
