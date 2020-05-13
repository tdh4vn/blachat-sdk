package com.blameo.chatsdk.utils;

import java.util.List;

public class BlaChatTextUtils {
    public static boolean containsInList(String a1, List<String> a2) {
        for (String a: a2) {
            if (a1.equals(a)) return true;
        }
        return false;
    }
}
