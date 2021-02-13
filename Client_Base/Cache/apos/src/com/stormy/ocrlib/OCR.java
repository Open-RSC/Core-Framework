package com.stormy.ocrlib;

import com.stormy.ocrlib.svm.svm;
import com.stormy.ocrlib.svm.svm_model;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

public final class OCR {

    private static final int MAX_WORD_LEN = 30;
    private static final Segmentor s = new Segmentor();
    private final DictSearch dictSearch;
    svm_model model;
    double[][] strProb;
    double[] charProb;
    int[] labels;

    public OCR(DictSearch dictSearch, BufferedReader modelReader)
            throws OCRException, IOException {
        this.dictSearch = dictSearch;
        model = svm.svm_load_model(modelReader);
        if (model == null) {
            throw new OCRException();
        }
        final int n = model.nr_class;
        strProb = new double[MAX_WORD_LEN][n];
        labels = new int[n];
        charProb = new double[n];
    }

    public String guess(SimpleImage image, boolean dict) {
        return guess_old(image, dict);
    }

    public String guess_old(SimpleImage image, boolean dict) {
        final List<Letter> split = Letter.splitLetters(image);
        final int len = split.size();
        if (dict) {
            for (int i = 0; i < len; ++i) {
                final Letter letter = split.get(i);
                letter.center(1);
                letter.scale(15, 15);
                letter.guess(this, i);
            }
            return dictSearch.getMatch(len, strProb);
        } else {
            char[] ch = new char[len];
            for (int i = 0; i < len; ++i) {
                final Letter letter = split.get(i);
                letter.center(1);
                letter.scale(15, 15);
                ch[i] = letter.getBestGuess(this, i);
            }
            return new String(ch);
        }
    }

    public String guess_new(SimpleImage image, boolean dict) {
        s.process(image);
        final List<Letter> split = Letter.splitLetters(image);
        final int len = split.size();
        if (dict) {
            for (int i = 0; i < len; ++i) {
                final Letter letter = new Letter(s.getLetter(i, true));
                letter.center(1);
                letter.scale(15, 15);
                letter.guess(this, i);
            }
            return dictSearch.getMatch(len, strProb);
        } else {
            char[] ch = new char[len];
            for (int i = 0; i < len; ++i) {
                final Letter letter = new Letter(s.getLetter(i, true));
                letter.center(1);
                letter.scale(15, 15);
                ch[i] = letter.getBestGuess(this, i);
            }
            return new String(ch);
        }
    }
}
