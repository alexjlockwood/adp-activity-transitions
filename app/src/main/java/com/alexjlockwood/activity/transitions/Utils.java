package com.alexjlockwood.activity.transitions;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.view.View;

import java.util.Iterator;
import java.util.Set;

public final class Utils {

    public static final String[] RADIOHEAD_ALBUM_URLS = {
            "http://i.imgur.com/TaFnVAP.jpg",
            "http://i.imgur.com/RMCqfPu.jpg",
            "http://i.imgur.com/SBXnqun.jpg",
            "http://i.imgur.com/wcMIc6s.jpg",
            "http://i.imgur.com/HvKBJfQ.jpg",
            "http://i.imgur.com/st41fg5.jpg",
            "http://i.imgur.com/301YTSo.jpg",
            "http://i.imgur.com/qSlh1nl.jpg",
    };

    public static final String[] RADIOHEAD_ALBUM_NAMES = {
            "Pablo Honey", "The Bends", "OK Computer", "Kid A",
            "Amnesiac", "Hail to the Thief", "In Rainbows", "The King of Limbs",
    };

    public static final String[] RADIOHEAD_BACKGROUND_URLS = {
            "http://i.imgur.com/tWL3mUP.jpg",
            "http://i.imgur.com/Yt898e7.jpg",
            "http://i.imgur.com/AhCOaqV.jpg",
            "http://i.imgur.com/EpzQzq0.jpg",
            "http://i.imgur.com/wdI7zrx.jpg",
            "http://i.imgur.com/Hxe0H5l.jpg",
            "http://i.imgur.com/dFTiB6W.jpg",
            "http://i.imgur.com/E9Zeuff.jpg",
    };

    /**
     * Returns true if {@param view} is contained within {@param container}'s bounds.
     */
    public static boolean isViewInBounds(@NonNull View container, @NonNull View view) {
        Rect containerBounds = new Rect();
        container.getHitRect(containerBounds);
        return view.getLocalVisibleRect(containerBounds);
    }

    /**
     * Returns a string representation of {@param set}. Used only for debugging purposes.
     */
    @NonNull
    public static String setToString(@NonNull Set<String> set) {
        Iterator<String> i = set.iterator();
        if (!i.hasNext()) {
            return "[]";
        }
        StringBuilder sb = new StringBuilder().append('[');
        while (true) {
            sb.append(i.next());
            if (!i.hasNext()) {
                return sb.append(']').toString();
            }
            sb.append(", ");
        }
    }

    private Utils() {
        // This utility class should not be instantiated.
    }
}