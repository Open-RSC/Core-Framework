import com.aposbot.BotLoader;
import com.aposbot.EntryFrame;
import com.aposbot._default.ISleepListener;
import com.stormy.ocrlib.DictSearch;
import com.stormy.ocrlib.OCR;
import com.stormy.ocrlib.SimpleImageIO;

import java.io.*;

public final class SleepListener
        implements ISleepListener {

    private static final SleepListener instance = new SleepListener();
    private static final int OCR_NUM3 = 0;
    private static final int OCR_JOKER = 1;
    private static final int OCR_EXTERNAL = 2;
    private static final int OCR_MANUAL = 3;
    private static final String hc_bmp = "." + File.separator + "HC.BMP";
    private static final String slword_txt = "." + File.separator + "slword.txt";
    private static final String dict_txt = "." + File.separator + "lib" + File.separator + "Dictionary.txt";
    private static final String model_txt = "." + File.separator + "lib" + File.separator + "Model.txt";
    private OCR stormy;
    private File hc;
    private File slword;
    private long mod;
    private String sleepWord;
    private int ocrType;

    private SleepListener() {
    }

    public static void newWord(byte[] data) {
        instance.onNewWord(data);
    }

    private static String readLine(File file) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            return reader.readLine().trim();
        } catch (final Throwable t) {
            System.out.println("Error reading slword.txt: " + t.toString());
        } finally {
            try {
                reader.close();
            } catch (final Throwable t) {
            }
        }
        return null;
    }

    private static byte[] convertImage(byte[] data) {
        int var1 = 1;
        byte var2 = 0;
        final byte[] var4 = new byte[10200];
        int var3;
        int var5;
        int var6;
        for (var3 = 0; var3 < 255; var2 = (byte) (255 - var2)) {
            var5 = data[var1++] & 255;
            for (var6 = 0; var6 < var5; ++var6) {
                var4[var3++] = var2;
            }
        }
        for (var5 = 1; var5 < 40; ++var5) {
            var6 = 0;
            while (var6 < 255) {
                final int var7 = data[var1++] & 255;
                for (int var8 = 0; var8 < var7; ++var8) {
                    var4[var3] = var4[var3 - 255];
                    ++var3;
                    ++var6;
                }
                if (var6 < 255) {
                    var4[var3] = (byte) (255 - var4[var3 - 255]);
                    ++var3;
                    ++var6;
                }
            }
        }
        return var4;
    }

    private static void saveBitmap(OutputStream out, byte[] data) throws IOException {
        out.write(66);
        out.write(77);
        short var3 = 1342;
        out.write(var3 & 255);
        out.write(var3 >> 8 & 255);
        out.write(0);
        out.write(0);
        out.write(0);
        out.write(0);
        out.write(0);
        out.write(0);
        byte var10 = 62;
        out.write(var10 & 255);
        out.write(var10 >> 8 & 255);
        out.write(0);
        out.write(0);
        var10 = 40;
        out.write(var10 & 255);
        out.write(var10 >> 8 & 255);
        out.write(0);
        out.write(0);
        var3 = 256;
        out.write(var3 & 255);
        out.write(var3 >> 8 & 255);
        out.write(0);
        out.write(0);
        var10 = 40;
        out.write(var10 & 255);
        out.write(var10 >> 8 & 255);
        out.write(0);
        out.write(0);
        var10 = 1;
        out.write(var10 & 255);
        out.write(var10 >> 8 & 255);
        var10 = 1;
        out.write(var10 & 255);
        out.write(var10 >> 8 & 255);
        var10 = 0;
        out.write(var10 & 255);
        out.write(var10 >> 8 & 255);
        out.write(0);
        out.write(0);
        var10 = 0;
        out.write(var10 & 255);
        out.write(var10 >> 8 & 255);
        out.write(0);
        out.write(0);
        var10 = 0;
        out.write(var10 & 255);
        out.write(var10 >> 8 & 255);
        out.write(0);
        out.write(0);
        var10 = 0;
        out.write(var10 & 255);
        out.write(var10 >> 8 & 255);
        out.write(0);
        out.write(0);
        var10 = 0;
        out.write(var10 & 255);
        out.write(var10 >> 8 & 255);
        out.write(0);
        out.write(0);
        var10 = 0;
        out.write(var10 & 255);
        out.write(var10 >> 8 & 255);
        out.write(0);
        out.write(0);
        out.write(0);
        out.write(0);
        out.write(0);
        out.write(0);
        out.write(255);
        out.write(255);
        out.write(255);
        out.write(0);
        int var4 = 9945;
        for (int var5 = 0; var5 < 40; ++var5) {
            for (int var6 = 0; var6 < 32; ++var6) {
                byte var7 = 0;
                for (int var8 = 0; var8 < 8; ++var8) {
                    var7 = (byte) (2 * var7);
                    if (var6 != 31 || var8 != 7) {
                        if (data[var4] != 0) {
                            ++var7;
                        }
                        ++var4;
                    }
                }
                out.write(var7);
            }
            var4 -= 510;
        }
    }

    static final SleepListener get() {
        return instance;
    }

    @Override
    public void setSolver(BotLoader bl, String type) {
        if (type.equals(EntryFrame.LABEL_NUM3)) {
            BufferedReader mr = null;
            BufferedReader dr = null;
            try {
                mr = new BufferedReader(new FileReader(model_txt));
                dr = new BufferedReader(new FileReader(dict_txt));
                stormy = new OCR(new DictSearch(dr), mr);
                ocrType = OCR_NUM3;
            } catch (final Throwable t) {
                t.printStackTrace();
                ocrType = OCR_MANUAL;
            } finally {
                try {
                    if (mr != null) {
                        mr.close();
                    }
                } catch (final IOException ex) {
                }
                try {
                    if (dr != null) {
                        dr.close();
                    }
                } catch (final IOException ex) {
                }
            }
        } else if (type.equals(EntryFrame.LABEL_EXTERNAL)) {
            hc = new File(hc_bmp);
            slword = new File(slword_txt);
            ocrType = OCR_EXTERNAL;
        } else if (type.equals(EntryFrame.LABEL_JOKER)) {
            hc = new File(hc_bmp);
            final Joker joker = Joker.get();
            if (joker.loadNativeLibrary()) {
                joker.setFilePaths(model_txt, dict_txt);
                ocrType = OCR_JOKER;
            } else {
                ocrType = OCR_MANUAL;
            }
        } else {
            ocrType = OCR_MANUAL;
        }
        if (ocrType != bl.getDefaultOCR()) {
            bl.setDefaultOCR(ocrType);
            bl.storeProperties(null);
        }
    }

    @Override
    public void onNewWord(byte[] data) {
        if (ScriptListener.get().isScriptRunning() && ocrType != OCR_MANUAL) {
            if (ocrType == OCR_NUM3) {
                final ByteArrayOutputStream out = new ByteArrayOutputStream(4096);
                try {
                    saveBitmap(out, convertImage(data));
                    sleepWord = stormy.guess(SimpleImageIO.readBMP(out.toByteArray()), true);
                } catch (final IOException ex) {
                    ex.printStackTrace();
                    sleepWord = null;
                }
            } else {
                if (ocrType != OCR_JOKER) {
                    mod = slword.lastModified();
                }
                FileOutputStream out = null;
                try {
                    out = new FileOutputStream(hc);
                    saveBitmap(out, convertImage(data));
                    if (ocrType == OCR_JOKER) {
                        sleepWord = Joker.get().getGuess();
                    }
                } catch (final IOException ex) {
                    ex.printStackTrace();
                } finally {
                    try {
                        if (out != null) {
                            out.close();
                        }
                    } catch (final IOException ex) {
                    }
                }
            }
            ScriptListener.get().onNewSleepWord();
        }
    }

    @Override
    public String getGuess() {
        if (ocrType == OCR_EXTERNAL) {
            if (mod < slword.lastModified()) {
                sleepWord = readLine(slword);
                mod = slword.lastModified();
                return sleepWord;
            }
            return null;
        }
        return sleepWord;
    }
}
