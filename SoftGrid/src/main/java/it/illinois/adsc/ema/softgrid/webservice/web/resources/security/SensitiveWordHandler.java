/* Copyright (C) 2016 Advanced Digital Science Centre

        * This file is part of Soft-Grid.
        * For more information visit https://www.illinois.adsc.com.sg/cybersage/
        *
        * Soft-Grid is free software: you can redistribute it and/or modify
        * it under the terms of the GNU General Public License as published by
        * the Free Software Foundation, either version 3 of the License, or
        * (at your option) any later version.
        *
        * Soft-Grid is distributed in the hope that it will be useful,
        * but WITHOUT ANY WARRANTY; without even the implied warranty of
        * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
        * GNU General Public License for more details.
        *
        * You should have received a copy of the GNU General Public License
        * along with Soft-Grid.  If not, see <http://www.gnu.org/licenses/>.

        * @author Prageeth Mahendra Gunathilaka
*/
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
