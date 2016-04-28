package id.co.viva.news.app.services;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by reza on 01/12/14.
 */
public class Validation {

    public Validation(){}

    public boolean isEmailValid(String email) {
        boolean result = false;
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            result = true;
        }
        return result;
    }

    public boolean isLengthValid(String password) {
        boolean result = false;
        password.toLowerCase();
        if(password.length() >= 6) {
            result = true;
        }
        return result;
    }

}
