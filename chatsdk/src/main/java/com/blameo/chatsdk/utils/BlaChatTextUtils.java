package com.blameo.chatsdk.utils;

import java.util.List;

public class BlaChatTextUtils {
    public static boolean containsInList(String a1, List<String> a2) {
        for (String a: a2) {
            if (a1.equals(a)) return true;
        }
        return false;
    }

    public static String convertToTextSearch(String str) {
        str = str.toLowerCase();
        str = str.replaceAll("(à|á|ạ|ả|ã|â|ầ|ấ|ậ|ẩ|ẫ|ă|ằ|ắ|ặ|ẳ|ẵ)", "a");
        str = str.replaceAll("(è|é|ẹ|ẻ|ẽ|ê|ề|ế|ệ|ể|ễ)", "e");
        str = str.replaceAll("(ì|í|ị|ỉ|ĩ)", "i");
        str = str.replaceAll("(ò|ó|ọ|ỏ|õ|ô|ồ|ố|ộ|ổ|ỗ|ơ|ờ|ớ|ợ|ở|ỡ)", "o");
        str = str.replaceAll("(ù|ú|ụ|ủ|ũ|ư|ừ|ứ|ự|ử|ữ)", "u");
        str = str.replaceAll("(ỳ|ý|ỵ|ỷ|ỹ)", "y");
        str = str.replaceAll("(đ)", "d");
        // Some system encode vietnamese combining accent as individual utf-8 characters
        str = str.replace("(\u0300|\u0301|\u0303|\u0309|\u0323)", ""); // Huyền sắc hỏi ngã nặng
        str = str.replace("(\u02C6|\u0306|\u031B)", ""); // Â, Ê, Ă, Ơ, Ư
        return str;
    }
}
