package com.stormy.ocrlib;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class DictSearch {

    private final List<String> words;

    public DictSearch(List<String> words) {
        this.words = words;
    }

    public DictSearch(BufferedReader r) throws IOException {
        words = new ArrayList<String>();
        String line;
        while ((line = r.readLine()) != null) {
            words.add(line);
        }
    }

    String getMatch(int len, double[][] strProb) {
        final int listSize = words.size();
        int result = -1;
        double best = -1;
        double cur;
        for (int i = 0; i < listSize; ++i) {
            final String str = words.get(i);
            if (str.length() != len) {
                continue;
            }
            cur = 0;
            for (int j = 0; j < len; ++j) {
                cur += Math.pow(strProb[j][str.charAt(j) - 'a'], 2);
            }
            if (cur > best) {
                best = cur;
                result = i;
            }
        }
        if (result == -1) {
            return "unknown";
        }
        return words.get(result);
    }
}
