package com.stormy.ocrlib;

final class SmallRegionsCrowdingCriterion implements JoiningCriterionT {

    @Override
    public boolean joinCritFunc(Region r1, Region r2) {
        if ((r1.Width() >= 8) || (r1.Height() >= 8) || (r2.Width() >= 8)
                || (r2.Height() >= 8)) {
            // they are not small regions
            return false;
        }

        int x1, x2, y1, y2;

        x1 = r1.OffsetX() + r1.Width() / 2;
        x2 = r2.OffsetX() + r2.Width() / 2;

        y1 = r1.OffsetY() + r1.Height() / 2;
        y2 = r2.OffsetY() + r2.Height() / 2;

        return Math.abs(x2 - x1) + Math.abs(y1 - y2) < 15;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof SmallRegionsCrowdingCriterion;
    }
}
