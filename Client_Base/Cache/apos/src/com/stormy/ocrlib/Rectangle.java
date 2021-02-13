package com.stormy.ocrlib;

final class Rectangle {

    Line l1, l2, p1, p2;
    double area;

    Rectangle(Rectangle r) {
        l1 = r.l1;
        l2 = r.l2;
        p1 = r.p1;
        p2 = r.p2;
        area = r.area;
    }

    Rectangle() {
    }
}
