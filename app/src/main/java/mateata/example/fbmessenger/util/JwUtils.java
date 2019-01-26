package mateata.example.fbmessenger.util;

public class JwUtils {

    public static boolean isNumber(String str){
        boolean result = false; 
                  
        try{
        	Double.parseDouble(str);
            result = true ;
        }catch(Exception e){}
                  
        return result ;
    }


}


















