package com.stormy.ocrlib;

import com.stormy.ocrlib.svm.svm;
import com.stormy.ocrlib.svm.svm_node;

import java.util.ArrayList;
import java.util.List;

final class Letter extends Region {

    private Letter(int width, int height) {
        super(width, height);
    }

    Letter(SimpleImage letter) {
        super(letter);
    }

    static List<Letter> splitLetters(SimpleImage image) {
        final ArrayList<Letter> list = new ArrayList<Letter>();
        final int h = image.Height();
        int start = -1;
        for (int x = 0; x < image.Width(); ++x) {
            if (rowContainsColour(image, x)) {
                if (start == -1) {
                    start = x;
                }
            } else {
                if (start == -1) {
                    continue;
                }
                final int w = x - start;
                if (w > 5) {
                    final Letter letter = new Letter(w, h);
                    letter.copy(image, x, 0, w, h, 0, 0);
                    list.add(letter);
                    start = -1;
                }
            }
        }
        return list;
    }

    private static boolean rowContainsColour(SimpleImage image, int x) {
        for (int y = 0; y < image.Height(); ++y) {
            if (image.getPixel(x, y) != 0) {
                return true;
            }
        }
        return false;
    }

    void guess(OCR ocr, int idxInStr) {
        final svm_node[] svmNode = toSvmNode();

        final int[] labels = ocr.labels;

        final double[] charProb = ocr.charProb;

        svm.svm_get_labels(ocr.model, labels);
        svm.svm_predict_probability(ocr.model, svmNode, charProb);

        final double[] probs = ocr.strProb[idxInStr];

        for (int i = 0; i < probs.length; i++) {
            probs[i] = 0;
        }
        for (int i = 0; i < charProb.length; i++) {
            probs[/* 'a' + */labels[i]] = charProb[i];
        }
    }

    char getBestGuess(OCR ocr, int idxInStr) {
        guess(ocr, idxInStr);
        final double[] charProb = ocr.charProb;
        int index = 0;
        for (int i = 0; i < charProb.length; i++) {
            if (charProb[i] > charProb[index]) {
                index = i;
            }
        }
        return (char) (ocr.labels[index] + 'a');
    }

    private svm_node[] toSvmNode() {
        int pixelCount = 0;
        final int width = Width();
        final int height = Height();
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                if (getPixel(x, y) != 0) {
                    pixelCount++;
                }
            }
        }
        final svm_node[] output = new svm_node[pixelCount + 1];
        for (int i = 0; i < output.length; ++i) {
            output[i] = new svm_node();
        }
        int pos = 0;
        double max = -1.;
        double min = 256.;
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                if (getPixel(x, y) != 0) {
                    output[pos].index = y * width + x + 1;
                    output[pos].value = getPixel(x, y) / 255.f;
                    if (max < output[pos].value) {
                        max = output[pos].value;
                    }
                    if (min > output[pos].value) {
                        min = output[pos].value;
                    }
                    pos++;
                }
            }
        }
        output[pos].index = -1;
        return output;
    }
}
