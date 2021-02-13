package com.stormy.ocrlib;

import java.util.Comparator;

public final class LetterComparator implements Comparator<Region> {

    @Override
    public int compare(Region r1, Region r2) {
        if (r1.OffsetX() < r2.OffsetX()) {
            return -1;
        } else if (r1.OffsetX() > r2.OffsetX()) {
            return 1;
        }
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof LetterComparator;
    }
}