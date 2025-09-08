package com.university.user.util;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class MatcherString {

    public static Boolean findValue(String regex, String originalValue, String findValue){
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(originalValue);
        return matcher.find() && (matcher.group(1).contains(findValue));
    }
}
