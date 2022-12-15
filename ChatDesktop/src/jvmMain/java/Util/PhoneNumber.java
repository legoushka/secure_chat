package Util;

import java.io.IOException;

public class PhoneNumber {
    public static String formatPhoneNumber(String originalPhoneNumber){
        String formattedPhoneNumber = originalPhoneNumber;
        if (formattedPhoneNumber.length() == 11){
            if (formattedPhoneNumber.charAt(0) == '8')
            {
                formattedPhoneNumber = "7" + formattedPhoneNumber.substring(1);
            }
            return formattedPhoneNumber;
        }
        return formattedPhoneNumber;
    }
}
