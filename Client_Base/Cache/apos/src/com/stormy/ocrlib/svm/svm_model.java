//
// svm_model
//
package com.stormy.ocrlib.svm;

public class svm_model
        implements java.io.Serializable {

    private static final long serialVersionUID = 6790261614477255960L;
    public int nr_class; // number of classes, = 2 in regression/one class svm
    svm_parameter param; // parameter
    int l; // total #SV
    svm_node[][] SV; // SVs (SV[l])
    double[][] sv_coef; // coefficients for SVs in decision functions
    // (sv_coef[k-1][l])
    double[] rho; // constants in decision functions (rho[k*(k-1)/2])
    double[] probA; // pariwise probability information
    double[] probB;
    int[] sv_indices; // sv_indices[0,...,nSV-1] are values in
    // [1,...,num_traning_data] to indicate SVs in the
    // training set

    // for classification only

    int[] label; // label of each class (label[k])
    int[] nSV; // number of SVs for each class (nSV[k])
    // nSV[0] + nSV[1] + ... + nSV[k-1] = l
}
