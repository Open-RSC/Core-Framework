package com.stormy.ocrlib.svm;

import java.io.Serializable;

class svm_problem
        implements Serializable {

    private static final long serialVersionUID = 8812735673571736194L;
    int l;
    double[] y;
    svm_node[][] x;
}
