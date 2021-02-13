package com.stormy.ocrlib;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

class ImageTransform {

    private final ITHelperFuncs ithelperfuncs = new ITHelperFuncs();

    static int info(Point P0, Point P1, Point P2) {
        return (P1.x - P0.x) * (P2.y - P0.y) - (P2.x - P0.x) * (P1.y - P0.y);
    }

    static Rectangle getMinAreaRectangle(Point p0, Point p1, ArrayList<Point> points) {
        Rectangle res = new Rectangle();
        res.area = -1.f;

        // this function assumes that P0 P1 is a segment from the convex hull
        // !!!

        Line baseLine = new Line();
        Line baseLineParallel;
        Line perpendicular1, perpendicular2;
        baseLine.init(p0, p1);

        // now get the point that is farthest to the line
        double distance = baseLine.getDistance(points.get(0));
        int index = 0;

        int i;
        for (i = 0; i < points.size(); i++) {
            double tmpDistance = baseLine.getDistance(points.get(i));
            if (distance < tmpDistance) {
                distance = tmpDistance;
                index = i;
            }
        }

        // now index is the index of the farthest point from the line P0 P1
        if (distance < 10e-6) { // the distance is 0
            // error
            return res;
        }

        // if distance != 0 then draw a parallel line through the point

        baseLineParallel = baseLine.getParallel(points.get(index));

        // now get a perpendicular on baseline and keep the points with the
        // smallest and the greatest signed distance

        Line perp;

        perp = baseLine.getPerpendicular(points.get(index));

        // scan for the point with the smallest and greatest signed distance

        int indexMin;
        int indexMax;
        double distMin;
        double distMax;

        indexMin = 0;
        indexMax = 0;
        distMin = perp.getSignedDistance(points.get(indexMin));
        distMax = perp.getSignedDistance(points.get(indexMax));

        for (i = 0; i < points.size(); i++) {
            double tmpDist = perp.getSignedDistance(points.get(i));
            if (distMin > tmpDist) {
                distMin = tmpDist;
                indexMin = i;
            }

            if (distMax < tmpDist) {
                distMax = tmpDist;
                indexMax = i;
            }
        }

        // we now have the 2 points

        perpendicular1 = perp.getParallel(points.get(indexMin));
        perpendicular2 = perp.getParallel(points.get(indexMax));

        res.l1 = baseLine;
        res.l2 = baseLineParallel;
        res.p1 = perpendicular1;
        res.p2 = perpendicular2;
        res.area = (distMax - distMin) * distance;

        return res;

    }

    Rectangle getMinAreaRectangle(ArrayList<Point> points) {
        points = getConvexHull(points);

        Rectangle res = new Rectangle();
        res.area = -1.f;

        if (points.size() <= 2) {
            return res;
        }

        int i;
        for (i = 0; i < points.size() - 2; i++) {
            Rectangle tmp = getMinAreaRectangle(points.get(i),
                    points.get(i + 1), points);
            if (tmp.area > 0) {
                if (res.area < 0) {
                    res = tmp;
                } else {
                    if (res.area > tmp.area) {
                        res = tmp;
                    }
                }
            }
        }

        // now consider the last point and the first point
        Rectangle tmp = getMinAreaRectangle(points.get(points.size() - 1),
                points.get(0), points);
        if (tmp.area > 0) {
            if (res.area < 0) {
                res = tmp;
            } else {
                if (res.area > tmp.area) {
                    res = tmp;
                }
            }
        }

        return res;
    }

    ArrayList<Point> getConvexHull(ArrayList<Point> points) {
        /*
         * this function implements the Graham's scan algorithm for finding the
         * convex hull. Here is a link to some explanation:
         * http://en.wikipedia.org/wiki/Graham_scan
         */

        ArrayList<Point> result = new ArrayList<Point>();

        // check to see if we have any points
        if (points.isEmpty())
            return result;

        // first find the lowest, leftmost point
        ithelperfuncs.basePoint = points.get(0);

        for (Point it : points) {
            if (ithelperfuncs.basePoint.y >= it.y) {
                // check for strict inequality
                if (ithelperfuncs.basePoint.y > it.y) {
                    ithelperfuncs.basePoint = it;
                } else {
                    if (ithelperfuncs.basePoint.x > it.x) {
                        ithelperfuncs.basePoint = it;
                    }
                }
            }
        }

        // now remove the basePoint

        for (Point it : points) {
            if (!ithelperfuncs.basePoint.equals(it)) {
                result.add(it);
            }
        }

        // XXX
        // std::make_heap(result.begin(), result.end(), ITHelperFuncs::sortPointFunc);
        // std::sort_heap(result.begin(), result.end(), ITHelperFuncs::sortPointFunc);

        points.clear();

        Point current;

        if (result.isEmpty()) {
            //there is no point left
            //push the basepoint into result and return
            result.add(ithelperfuncs.basePoint);
            return result;
        }

        PriorityQueue<Point> queue = new PriorityQueue<>(result.size(), new Comparator<Point>() {
            @Override
            public int compare(Point o1, Point o2) {
                if (ithelperfuncs.sortPointFunc(o1, o2)) {
                    return -1;
                }
                return 0;
            }
        });
        for (Point p : result) {
            queue.add(p);
        }
        result.clear();
        for (Point p : queue) {
            result.add(p);
        }

        int index = 0;
        current = result.get(index);
        points.add(current);

        do {
            if (!ithelperfuncs.areEqual(current, result.get(index))) {
                current = result.get(index);
                points.add(current);
            }
            ++index;
        } while (index != result.size());

        index = 0;

        result.clear();

        result.add(ithelperfuncs.basePoint);

        result.add(points.get(index));
        ++index;
        // result.add(new Point(points.get(index)));

        while (index != points.size()) {
            if (info(result.get(result.size() - 2),
                    result.get(result.size() - 1), points.get(index)) > 0) {
                // the new point should be included into the hull
                result.add(points.get(index));
                index++;
            } else {
                // pop a point from the stack
                result.remove(result.size() - 1);
            }
        }
        return result;
    }

    class ITHelperFuncs {

        Point basePoint;

        boolean sortPointFunc(Point p1, Point p2) {

            /*
             * Let the basepoint be P0. P1 < P2 if the angle between P0P1 and Ox
             * is smaller than the angle between P0P2 and Ox or if the angles
             * are equal and P1 is farther than P2 from P0. This will let me
             * eliminate quickly the points with the same angle
             */

            int y1, x1, x2, y2;

            x1 = p1.x - basePoint.x;
            y1 = p1.y - basePoint.y;

            x2 = p2.x - basePoint.x;
            y2 = p2.y - basePoint.y;

            if (y1 * x2 < y2 * x1) {
                return true;
            }
            if (y1 * x2 == y2 * x1) {
                return (x1 * x1 + y1 * y1 > x2 * x2 + y2 * y2);
            }

            return false;
        }

        boolean areEqual(Point p1, Point p2) {
            int y1, x1, x2, y2;

            x1 = p1.x - basePoint.x;
            y1 = p1.y - basePoint.y;

            x2 = p2.x - basePoint.x;
            y2 = p2.y - basePoint.y;

            return (y1 * x2 == y2 * x1);
        }
    }
}
