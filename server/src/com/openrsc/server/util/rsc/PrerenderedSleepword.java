package com.openrsc.server.util.rsc;

import java.util.ArrayList;
import java.util.List;

public class PrerenderedSleepword {
    public String filename;
    public String correctWord;
    public byte[] pngData;
    public byte[] rleData;
    public boolean knowTheCorrectWord;

    PrerenderedSleepword(String _filename, String _correctWord, boolean _knowTheCorrectWord, byte[] _pngData, byte[] _rleData) {
        this.filename = _filename;
        this.correctWord = _correctWord;
        this.knowTheCorrectWord = _knowTheCorrectWord;
        this.pngData = _pngData;
        this.rleData = _rleData;
    }
}
