package com.wadsack;

import com.wadsack.android.sheepshead.scorer.Player;

/**
 * A bunch of common utility functions that should be extension methods when Java 7 comes around
 *
 * Author: Jeremy Wadsack
 */
public class Util {

    public static String join(String separator, String[] strings) {
        if (strings == null) {
            return null;
        }
        if (strings.length == 0) {
            return "";
        }
        StringBuilder builder = new StringBuilder(strings[0]);
        for (int i = 1, length = strings.length; i < length; i++) {
            builder.append(separator);
            builder.append(strings[i]);
        }
        return builder.toString();
    }

    public static int arrayIndexOf(Player[] list, Player item) {
        if (item == null) {
            return -1;
        }
        for (int i = 0; i < list.length; i++) {
            if (list[i].equals(item)) {
                return i;
            }
        }
        return -1;
    }
}
