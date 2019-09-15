/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package program;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * This class contains some static methods, which could facilitate the interaction with the file-system.<br>
 * The most methods are throwing a {@link Streamer.StreamingException StreamingException} if any error occurs.
 * @author Leon Haffmans
 * @version 06. 01. 2018
 */
public class Streamer {
    /**
     * Reads the data from the specified file and returns them as a byte-array.
     * @param file The file to read.
     * @return The data of the 'file' as a byte-arrray.
     * @throws Streamer.StreamingException If the file could not be read.
     */
    public static byte[] readData(File file) throws StreamingException{
        if(file != null && !file.isDirectory() && file.exists()){
            byte[] inhalt = null;
            FileInputStream input = null;
            try{
                input = new FileInputStream(file);
                inhalt = new byte[(int)file.length()];
                input.read(inhalt);
            }catch(IOException e){
                if(input != null) try{input.close();}catch(IOException e1){}
                throw new StreamingException(e.getMessage());
            }finally{
                if(input != null) try{input.close();}catch(IOException e1){}
                return inhalt;
            }
        }else if(file == null)
            throw new StreamingException("The specified file is 'null'.");
        else if(file.isDirectory())
            throw new StreamingException("The specified file is a directory.");
        else// (!file.exists())
            throw new StreamingException("The file does not exist.");
    }

    /**
     * Reads the data from the specified file and saves them to the puffer-array.<br>
     * Throws a {@link Streamer.StreamingException StreamingException} if the file could not be read or if the memory capacity of the puffer-array is too little.
     * @param file The file to read.
     * @param puffer The puffer-array in which the data will be saved.
     * @return The number of bytes which are saved in 'puffer'.
     * @throws Streamer.StreamingException If the file could not be read or if the memory capacity of the puffer-array is too little.
     */
    public static int readData(File file, byte[] puffer) throws StreamingException{
        if(file != null && !file.isDirectory() && file.exists() && puffer != null){
            int erg = -1;
            FileInputStream input = null;
            try{
                input = new FileInputStream(file);
                if(puffer.length >= (int)file.length())
                    erg = input.read(puffer);
                else
                    throw new StreamingException("The puffer-array has too little memory capacity.");
            }catch(IOException e){
                if(input != null) try{input.close();}catch(IOException e1){}
                throw new StreamingException(e.getMessage());
            }finally{
                if(input != null) try{input.close();}catch(IOException e1){}
                return erg;
            }
        }else if(file == null)
            throw new StreamingException("The specified file is 'null'.");
        else if(file.isDirectory())
            throw new StreamingException("The specified file is a directory.");
        else if(!file.exists())
            throw new StreamingException("The file does not exist.");
        else// (puffer == null)
            throw new StreamingException("The specified puffer-array is 'null'.");
    }

    /**
     * Writes data to the specified file-output.
     * @param daten The data.
     * @param fileoutput The file to write.
     * @param change Indicates if the method can overwrite already existing data.
     * @throws Streamer.StreamingException If the data could not be saved to the file.
     */
    public static void writeData(byte[] daten, File fileoutput, boolean change) throws StreamingException{
        if(daten != null)
            writeData(daten, daten.length, fileoutput, change, false);
        else
            throw new StreamingException("No data to write ('null').");
    }

    /**
     * Writes data to the specified file-output.
     * @param daten The data.
     * @param fileoutput The file to write.
     * @param change Indicates if the method can overwrite already existing data.
     * @param append Indicates if the data can be appended to existing file-data.
     * @throws Streamer.StreamingException If the data could not be saved to the file.
     */
    public static void writeData(byte[] daten, File fileoutput, boolean change, boolean append) throws StreamingException{
        if(daten != null)
            writeData(daten, daten.length, fileoutput, change, append);
        else
            throw new StreamingException("No data to write ('null').");
    }

    /**
     * Writes data to the specified file-output.
     * @param daten The data.
     * @param datenLen The number of bytes which will be saved to the file.
     * @param fileoutput The file to write.
     * @param change Indicates if the method can overwrite already existing data.
     * @param append Indicates if the data can be appended to existing file-data.
     * @throws Streamer.StreamingException If the data could not be saved to the file.
     */
    public static void writeData(byte[] daten, int datenLen, File fileoutput, boolean change, boolean append) throws StreamingException{
        if(fileoutput != null && !fileoutput.isDirectory() && daten != null){
            if(change || !change && !fileoutput.exists()){
                FileOutputStream output = null;
                try{
                    output = new FileOutputStream(fileoutput, append);
                    output.write(daten, 0, datenLen);
                    output.flush();
                }catch(IOException e){
                    if(output != null) try{output.close();}catch(IOException e1){}
                    throw new StreamingException(e.getMessage());
                }finally{
                    if(output != null) try{output.close();}catch(IOException e1){}
                }
            }else{
                throw new StreamingException("File already exists.");
            }
        }else if(fileoutput == null)
            throw new StreamingException("The specified file is 'null'.");
        else if(fileoutput.isDirectory())
            throw new StreamingException("The specified file is a directory.");
        else if(daten == null)
            throw new StreamingException("The specified data are 'null'.");
    }

    /**
     * Deletes the specified directory.
     * @param dir The directory.
     * @throws Streamer.StreamingException If the directory could not be deleted.
     */
    public static void deleteDir(File dir) throws StreamingException{
        if(dir != null && dir.exists() && dir.isDirectory()){
            File[] list = dir.listFiles();
            for (File file : list) {
                if (file.isDirectory())
                    deleteDir(file);
                else if(!file.delete())
                    throw new StreamingException("File '"+file.getAbsolutePath()+"' could not be deleted.");
            }
            if(!dir.delete())
                throw new StreamingException("Directory '"+dir.getAbsolutePath()+"' could not be deleted.");
        }else if(dir == null)
            throw new StreamingException("The specified directory is 'null'.");
        else if(!dir.exists())
            throw new StreamingException("The directory does not exist.");
        else// (!file.isDirectory())
            throw new StreamingException("The given directory is not a directory.");
    }
  
    //***********************************more methods*******************************************
    
    /**
     * Adds the defined text to the file-name before its extension.<br>
     * The specified file will not be changed.
     * @param text The text to add.
     * @param file The path to the file.
     * @return The new filepath.
     */
    public static String addToFileName(String text, String file){
        if(file != null){
            String puf = "";
            for(int i=file.length()-1; i>=0; i--){
                char cur = file.charAt(i);
                if(cur == '.'){
                    return file.substring(0, i)+text+"."+puf;
                }else if(cur == File.pathSeparatorChar){
                    return file.substring(0, i+1)+puf+text;
                }else
                    puf = cur+puf;
            }
            //Shouldn't happen if the path is legit.
            return null;
        }else
            return null;
    }

    /**
     * Changes the extension of a specified file with the defined string.<br>
     * If this string is empty, the extension will be removed.<br>
     * The specified file will not be changed.
     * @param end The new extension.
     * @param file The path to the file.
     * @return The new filepath or 'null' if 'end' or 'file' is 'null'.
     */
    public static String setEnding(String end, String file){
        if(file != null && end != null){
            if(!end.equals(""))
                return file.substring(0, file.lastIndexOf(".")+1)+end;
            else
                return file.substring(0, file.lastIndexOf("."));
        }else
            return null;
    }

    /**
     * Changes the filename without changing its extension (if the file has one).
     * @param newName The new name for the file.
     * @param file The filepath.
     * @return The new filepath or 'null' if the filepath or the new name is 'null' or if the new name is an empty string.
     */
    public static String setName(String newName, String file){
        if(file != null && newName != null && !newName.equals("")){
            String ext = "";
            int point = -1;
            for(int i=file.length()-1; i>=0; i--){
                char cur = file.charAt(i);
                if(cur == '.'){
                    point = i;
                }else if(cur == File.pathSeparatorChar){
                    return point == -1 ? file.substring(0, i+1)+newName : file.substring(0, i+1)+newName+"."+ext;
                }else if(point == -1)
                    ext = cur+ext;
            }
            //Shouldn't happen if the path is legit.
            return null;
        }else
            return null;
    }

    /**
     * Returns the extension of a file.
     * @param file The file.
     * @return The extension of the file or <code>null</code> if the file is <code>null</code> or has no extension.
     */
    public static String getEnd(String file)
    {
        if(file == null) return null;
        String ext = "";
        for(int i=file.length()-1; i>=0; i--){
            char cur = file.charAt(i);
            if(cur == '.')
                return ext;
            else if(cur == File.pathSeparatorChar)
                return null;
            else
                ext = cur+ext;
        }
        //Shouldn't happen if 'file' is a legit path
        return null;
    }
    /**
     * This Exception will be thrown by most methods of {@link Streamer Streamer} if any error occurs.
     * 
     * @author Leon Haffmans
     * @version 25. 09. 2016
     */
    public static class StreamingException extends IOException{
        public StreamingException(String message){
            super(message);
        }
    }
}
