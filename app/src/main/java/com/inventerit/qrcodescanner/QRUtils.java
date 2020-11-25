package com.inventerit.qrcodescanner;


public abstract class QRUtils {

    public static boolean emailValidation(String emailAddress){
        if(android.util.Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches())
            return true;
        else
            return false;
    }

}
