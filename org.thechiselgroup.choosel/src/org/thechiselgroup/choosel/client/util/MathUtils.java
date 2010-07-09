package org.thechiselgroup.choosel.client.util;

public final class MathUtils {

    private MathUtils() {

    }

    public static int restrictToInterval(int minimum, int maximum, int value) {
        return Math.max(minimum, Math.min(maximum, value));
    }

}
