package com.example.amplifyaidemo;

import com.amazonaws.mobile.client.AWSMobileClient;

import java.util.UUID;

public class Util {

    public static String getUUID() {
        return UUID.randomUUID().toString();
    }
    public static String getUserName() {
        return AWSMobileClient.getInstance().getUsername();
    }

    public static String convertCountryCode(int src){
        switch (src){
            case 0 : return "zh";
            case 1 : return "zh-TW";
            case 2 : return "id-ID";
            case 3: return "ja-JP";
            case 4 : return "ko-KR";
            case 5 : return "ta-MY";
            case 6 : return "da-DK";
            case 7 : return "en-US";
            case 8 : return "de-DE";
            case 9 : return "nb-NO";
            case 10: return "sv-SE";//Swedish
            case 11 : return "fr-FR";
            case 12 : return "it-IT";
            case 13 : return "es-ES";
            case 14 : return "pt-PT";
            case 15 : return "cs-CZ";//czech
            case 16 : return "pl-PL";
            case 17 : return "ru-RU";
            case 18 : return "ar-SA";
            case 19 : return "he-IL";
            case 20 : return "hi-IN";
            case 21 : return "fa-IR";
            case 22 : return "fi-FI";
            case 23 : return "tr-TR";
            default: return "";
        }
    }
    public static String convertCountryCodeToTranslate(String src){
        switch (src){
            case  "zh" : return "zh";
            case "zh-TW":  return "zh-TW";
            case "id-ID" : return "id";
            case "ja-JP":  return "ja";
            case "ko-KR" : return "ko";
            case "ta-MY" : return "ta";
            case "da-DK" : return "da";
            case "en-US" : return "en";
            case "de-DE" : return "de";
            case "nb-NO" : return "nb";
            case "sv-SE":  return "sv";//Swedish
            case "fr-FR" : return "fr";
            case "it-IT" : return "it";
            case "es-ES" : return "es";
            case "pt-PT" : return "pt";
            case "cs-CZ" : return "cs";//czech
            case "pl-PL" : return "pl";
            case "ru-RU" : return "ru";
            case "ar-SA" : return "ar";
            case "he-IL" : return "he";
            case "hi-IN" : return "hi";
            case "fa-IR" : return "fa";
            case "fi-FI" : return "fi";
            case "tr-TR" : return "tr";
            default: return "";
        }
    }
}

