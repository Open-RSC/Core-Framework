package com.stormy.ocrlib;

final class RegionOverlapCriterion implements JoiningCriterionT {

    private static double overlap(Region r1, Region r2) {
        int oX;

        int r1minX = r1.OffsetX();
        int r1maxX = r1.OffsetX() + r1.Width();

        int r2minX = r2.OffsetX();
        int r2maxX = r2.OffsetX() + r2.Width();

        if (r1maxX < r2minX)
            return 0;
        if (r1minX > r2maxX)
            return 0;

        if (r1minX >= r2minX) {
            if (r1maxX <= r2maxX) {
                oX = r1maxX - r1minX + 1;
            } else {
                oX = r2maxX - r1minX + 1;
            }
        } else {
            if (r1maxX <= r2maxX) {
                oX = r1maxX - r2minX + 1;
            } else {
                oX = r2maxX - r2minX + 1;
            }
        }

        return (double) oX / (r2maxX - r2minX + 1);
    }

    @Override
    public boolean joinCritFunc(Region r1, Region r2) {
        double overlapFactor = 0.3;
        return ((overlap(r1, r2) >= overlapFactor) || (overlap(r2, r1) >= overlapFactor));
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof RegionOverlapCriterion;
    }
}