package com.stormy.ocrlib;

final class Line {

    // the equation is ax + by + c = 0
    private int a, b, c;

    // creates the line that passes between those two points
    boolean init(Point p1, Point p2) {
        if (p1 == p2) {
            a = 1;
            b = 0;
            c = -p1.x;
            return true;

        }
        a = p1.y - p2.y;
        b = p2.x - p1.x;
        c = p1.x * p2.y - p1.y * p2.x;

        return (a != 0) || (b != 0) || (c != 0);
    }

    // returns true if "p" is contained by the line
    boolean isOnLine(Point p) {
        int res = a * p.x + b * p.y + c;

        return (res == 0);
    }

    // returns a line parallel to this line and passes through the point "p"
    Line getParallel(Point p) {
        Line tmp = new Line();

        tmp.a = a;
        tmp.b = b;
        tmp.c = -tmp.a * p.x - tmp.b * p.y;

        return tmp;
    }

    // returns a line that is perpendicular to this line and passes through "p"
    Line getPerpendicular(Point p) {
        Line tmp = new Line();

        tmp.a = -b;
        tmp.b = a;
        tmp.c = -tmp.a * p.x - tmp.b * p.y;

        return tmp;
    }

    // returns 0 if p is on the line, +1 if p is in one semiplane and -1 if it
    // is in the other
    int getSign(Point p) {
        int tmp = a * p.x + b * p.y + c;

        if (tmp < 0)
            return -1;
        if (tmp > 0)
            return 1;
        return 0;
    }

    void translate(int deltaX, int deltaY) {
        c -= deltaX * a + deltaY * b;
    }

    // gets the distance between those two lines (they have to be parallel)
    double getDistance(Line l) {
        double x, y;

        // choose a point on the line
        if (b != 0) {
            x = 0.f;
            y = (double) (-c) / (double) b;
        } else {
            y = 0.f;
            x = (double) (-c) / (double) a;
        }

        return Math.abs((double) a * x + (double) b * y
                + (double) c)
                / Math.sqrt((double) a * (double) a + (double) b * (double) b);
    }

    double getSignedDistance(Point p) {
        return ((double) a * (double) p.x + (double) b * (double) p.y + (double) c)
                / Math.sqrt((double) a * (double) a + (double) b * (double) b);
    }

    double getDistance(Point p) {
        return Math.abs(getSignedDistance(p));
    }

    Point intersect(Line l) {
        double x, y;
        x = ((double) c * (double) l.b - (double) l.c * (double) b)
                / ((double) l.a * (double) b - (double) a * (double) l.b);
        y = ((double) c * (double) l.a - (double) l.c * (double) a)
                / ((double) a * (double) l.b - (double) l.a * (double) b);

        return new Point((int) x, (int) y);
    }

    double angle() {

        if (b == 0)
            return 0.0;

        if (a == 0) {
            if (b > 0)
                return Math.acos(0.0);
            return -Math.acos(0.0);
        }

        return Math.atan(-(double) b / (double) a);
    }
}
