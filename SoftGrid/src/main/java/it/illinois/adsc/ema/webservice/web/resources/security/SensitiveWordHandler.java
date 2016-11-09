//package it.ilinois.adsc.ema.webservice.web.resources.security;
//
//import java.io.*;
//import java.util.*;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
///**
// * This class filter out the sensitive string patterns
// * Created by prageeth.g on 1/2/2016.
// */
//public class SensitiveWordHandler {
//    // this hash map contains the various patterns of sensitive data such as credit card or email
//    private static List<Pattern> stringPatternList = null;
//
//    public SensitiveWordHandler() {
//        if (stringPatternList == null) {
//            init();
//        }
//    }
//
//    private void init() {
//        stringPatternList = new ArrayList<>();
//        // load the sensitive string patterns with their masking dummuy string from a properties file
//        // as a sample expressions, credit card regular expression is added
//        File sensitivePropertyFile = new File("sensitive_expression.properties");
//        if (sensitivePropertyFile.exists()) {
//            try {
//                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(sensitivePropertyFile)));
//                String patternString;
//                while((patternString = bufferedReader.readLine()) != null)
//                {
//                    stringPatternList.add(Pattern.compile(patternString));
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    /**
//     * This Method filter out any sensitive string patterns by replacing them with a
//     * dummy string as defined in the stringPatternList hash map
//     * @param str String to be filtered
//     * @return Filtered String
//     */
////    public String encordAndFilterString(String str) {
////        for (Pattern pattern : stringPatternList) {
////            Matcher matcher = pattern.matcher(str);
////            str = matcher.replaceAll(" ");
////        }
////        return str;
////    }
//}
