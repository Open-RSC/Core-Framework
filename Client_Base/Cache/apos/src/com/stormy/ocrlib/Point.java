package com.stormy.ocrlib;

final class Point {

    final int x, y;

    Point() {
        x = 0;
        y = 0;
    }

    Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + x;
        result = prime * result + y;
        return result;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Point)) return false;
        Point p = (Point) object;
        if (p == this) return true;
        return p.x == x && p.y == y;
    }

    @Override
    public String toString() {
        return String.format("Point [x=%s, y=%s]", x, y);
    }
}
