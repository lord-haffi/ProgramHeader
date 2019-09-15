/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package program;

/**
 *
 * @author LeonWin
 */
public class IllegalStringSyntaxException extends IllegalArgumentException{
    public IllegalStringSyntaxException(String string, String addMessage){
        super(buildMessage(string, -1, null, addMessage));
    }
    public IllegalStringSyntaxException(String string, int pointer, String addMessage){
        super(buildMessage(string, pointer, null, addMessage));
    }
    public IllegalStringSyntaxException(String string, String substring, String addMessage){
        super(buildMessage(string, -1, substring, addMessage));
    }
    public IllegalStringSyntaxException(String string, int pointer, String substring, String addMessage){
        super(buildMessage(string, pointer, substring, addMessage));
    }
    
    private static String buildMessage(String string, int pointer, String substring, String addMessage) {
        String message = "Illegal syntax in '"+string+"'";
        if (pointer >= 0)
            message += " at index "+pointer;
        if (substring != null)
            message += " ('"+substring+"')";
        if (addMessage != null)
            message += ": "+addMessage;
        return message;
    }
}
