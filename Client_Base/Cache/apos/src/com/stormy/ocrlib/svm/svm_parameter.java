package com.stormy.ocrlib.svm;

class svm_parameter
        implements Cloneable, java.io.Serializable {

    /* svm_type */
    static final int C_SVC = 0;
    static final int NU_SVC = 1;
    static final int ONE_CLASS = 2;
    static final int EPSILON_SVR = 3;
    static final int NU_SVR = 4;
    /* kernel_type */
    static final int LINEAR = 0;
    static final int POLY = 1;
    static final int RBF = 2;
    static final int SIGMOID = 3;
    static final int PRECOMPUTED = 4;
    private static final long serialVersionUID = -2339420518589702987L;
    int svm_type;
    int kernel_type;
    int degree; // for poly
    double gamma; // for poly/rbf/sigmoid
    double coef0; // for poly/sigmoid

    // these are for training only
    double cache_size; // in MB
    double eps; // stopping criteria
    double C; // for C_SVC, EPSILON_SVR and NU_SVR
    int nr_weight; // for C_SVC
    int[] weight_label; // for C_SVC
    double[] weight; // for C_SVC
    double nu; // for NU_SVC, ONE_CLASS, and NU_SVR
    double p; // for EPSILON_SVR
    int shrinking; // use the shrinking heuristics
    int probability; // do probability estimates

    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (final CloneNotSupportedException e) {
            return null;
        }
    }
}
