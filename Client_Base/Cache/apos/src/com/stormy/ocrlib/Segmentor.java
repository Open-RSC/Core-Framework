package com.stormy.ocrlib;

import java.util.ArrayList;
import java.util.Collections;

final class Segmentor {

    private static final JoiningCriterionT smallRegionsCrowdingCriterion = new SmallRegionsCrowdingCriterion();
    private static final JoiningCriterionT regionOverlapCriterion = new RegionOverlapCriterion();
    private static final LetterComparator letterComparator = new LetterComparator();
    private final ArrayList<Rectangle> rectangles = new ArrayList<>();
    private ArrayList<Region> letters = new ArrayList<>();
    private final ImageTransform imt = new ImageTransform();

    Rectangle getRectangle(int imageNo) {
        Rectangle tmp = new Rectangle();
        tmp.area = -1.f;

        if (imageNo < 0)
            return tmp;
        if (imageNo >= letters.size())
            return tmp;

        Rectangle r = new Rectangle(rectangles.get(imageNo));
        if (r.area < 0)
            return r;
        int deltaX, deltaY;
        deltaX = letters.get(imageNo).OffsetX();
        deltaY = letters.get(imageNo).OffsetY();
        r.l1.translate(deltaX, deltaY);
        r.l2.translate(deltaX, deltaY);
        r.p1.translate(deltaX, deltaY);
        r.p2.translate(deltaX, deltaY);

        return r;
    }

    void process(SimpleImage im) {
        letters.clear();
        rectangles.clear();

        SimpleImage tmpIm = new SimpleImage(im);

        int i, j;

        for (i = 0; i < im.Height(); i++) {
            for (j = 0; j < im.Width(); j++) {
                if (tmpIm.getPixel(j, i) == 255) {
                    // find the region
                    Region r = tmpIm.extract(j, i, true);

                    // remove it from the image
                    tmpIm.remove(r);

                    // push the region on the vector
                    letters.add(r);

                    // now find the rectangle
                    rectangles.add(getRegionRectangle(r));
                }
            }
        }

        joinRegions(smallRegionsCrowdingCriterion);
        joinRegions(regionOverlapCriterion);

        Collections.sort(letters, letterComparator);
        System.out.println(letters.size());

        buildRectangles();
    }

    void joinRegions(JoiningCriterionT joinCritFunc) {
        ArrayList<Integer> rids = new ArrayList<>(letters.size());

        int i;
        for (i = 0; i < letters.size(); i++) {
            rids.add(i);
        }

        int j;
        for (i = 0; i < letters.size(); i++) {
            for (j = i + 1; j < letters.size(); j++) {
                if (joinCritFunc.joinCritFunc(letters.get(i), letters.get(j))) {
                    rids.set(j, rids.get(j) < rids.get(i) ?
                            rids.get(j) : rids.get(i));
                    rids.set(i, j);
                }
            }
        }

        collapse_ids(rids);
    }

    void collapse_ids(ArrayList<Integer> regionId) {
        if (regionId.size() != letters.size())
            return;

        @SuppressWarnings("unchecked")
        ArrayList<Region>[] regList = new ArrayList[letters.size()];
        for (int i = 0; i < regList.length; ++i) {
            regList[i] = new ArrayList<>();
        }

        int i;
        for (i = 0; i < letters.size(); i++) {
            regList[i].clear();
        }

        // now fill regList

        for (i = 0; i < regionId.size(); i++) {
            regList[regionId.get(i)].add(letters.get(i));
        }

        // now collapse the regions
        ArrayList<Region> tmpLetters = new ArrayList<Region>();

        for (i = 0; i < regionId.size(); i++) {
            if (regList[i].size() > 0) {
                tmpLetters.add(collapse_regions(regList[i]));
            }
        }

        letters = tmpLetters;
    }

    Rectangle getRegionRectangle(Region r) {
        ArrayList<Point> input = new ArrayList<>();

        int i, j;
        for (i = 0; i < r.Height(); i++) {
            for (j = 0; j < r.Width(); j++) {
                if (r.getPixel(j, i) == 255) {
                    input.add(new Point(j, i));
                }
            }
        }

        return imt.getMinAreaRectangle(input);
    }

    void buildRectangles() {
        rectangles.clear();
        int i;
        for (i = 0; i < letters.size(); i++) {
            rectangles.add(getRegionRectangle(letters.get(i)));
        }
    }

    Region collapse_regions(ArrayList<Region> regs) {
        if (regs.size() == 0)
            return null;
        // adds more regions together
        int minX, maxX, minY, maxY;

        minX = regs.get(0).OffsetX();
        minY = regs.get(0).OffsetY();

        maxX = minX + regs.get(0).Width();
        maxY = minY + regs.get(0).Height();

        int i;
        for (i = 0; i < regs.size(); i++) {
            if (regs.get(i).OffsetX() < minX)
                minX = regs.get(i).OffsetX();
            if (regs.get(i).OffsetY() < minY)
                minY = regs.get(i).OffsetY();

            if (regs.get(i).OffsetX() + regs.get(i).Width() > maxX)
                maxX = regs.get(i).OffsetX() + regs.get(i).Width();
            if (regs.get(i).OffsetY() + regs.get(i).Height() > maxY)
                maxY = regs.get(i).OffsetY() + regs.get(i).Height();
        }

        // now minX, maxX, minY, maxY define a rectangle that includes all the
        // regions

        // add all the regions to the new region
        Region tmpReg = new Region(maxX - minX + 1, maxY - minY + 1, minX, minY);

        int j, k;

        for (k = 0; k < regs.size(); k++) {
            for (i = 0; i < regs.get(k).Height(); i++) {
                for (j = 0; j < regs.get(k).Width(); j++) {
                    if (regs.get(k).getPixel(j, i) != 0) {
                        tmpReg.setPixel(j + regs.get(k).OffsetX() - minX, i
                                + regs.get(k).OffsetY() - minY, regs.get(k)
                                .getPixel(j, i));
                    }
                }
            }
        }

        return tmpReg;
    }

    SimpleImage getLetter(int imageNo, boolean rotated) {
        rotated = true;

        SimpleImage tmp = new SimpleImage(1, 1);

        if (imageNo < 0)
            return tmp;
        if (imageNo >= letters.size())
            return tmp;

        tmp = letters.get(imageNo);

        // now rotate the image if necessary
        if (rotated) {
            Rectangle r = getRectangle(imageNo);

            if (r.area > 0) {
                double angle;

                if (Math.abs(r.l1.angle()) < Math.abs(r.p1.angle())) {
                    angle = r.l1.angle();
                } else {
                    angle = r.p1.angle();
                }

                // tmp.scale(tmp.Width() * 2, tmp.Height() * 2);
                tmp.rotate(-angle);
                // tmp.scale(tmp.Width() / 2, tmp.Height() / 2);
            }
        }

        // tmp.center(1.f);

        // tmp.scale(10, 10);

        return tmp;

    }

    int getNrOfLetters() {
        return letters.size();
    }
}
