import com.aposbot._default.IScript;
import com.aposbot._default.IScriptListener;

public final class ScriptListener
        implements IScriptListener {

    static final String ERROR_MESSAGE = "Error processing script. Send this output to the script's author:";
    private static final ScriptListener instance = new ScriptListener();
    private Extension ex;
    private long next;
    private boolean running;
    private IScript script;
    private String lastWord;
    private boolean newWord;
    private volatile boolean banned;

    private ScriptListener() {
    }

    static void init(Extension ex) {
        instance.ex = ex;
    }

    public static boolean isRunning() {
        return instance.isScriptRunning();
    }

    public static void setRunning(boolean b) {
        instance.setScriptRunning(b);
    }

    public static void runScript() {
        instance.onGameTick();
    }

    public static void message(boolean flag, String s1, int i1, String s2, int j1, int k1, String s3,
                               String s4) {

        instance.onGameMessage(flag, s1, i1, s2, j1, k1, s3, s4);
    }

    static final IScriptListener get() {
        return instance;
    }

    @Override
    public void onGameTick() {
        if (banned) {
            return;
        }
        if (running) {
            if (script.isSleeping()) {
                if (newWord && (script.getFatigue() == 0 || script.isTricking())) {
                    final String word = SleepListener.get().getGuess();
                    if (word != null && !word.equals(lastWord)) {
                        System.out.println("Sending guessed word: " + word);
                        ex.sendCAPTCHA(word);
                        lastWord = word;
                        newWord = false;
                    }
                }
                return;
            }
            if (System.currentTimeMillis() >= next) {
                try {
                    next = (script.main() + System.currentTimeMillis());
                } catch (final Throwable t) {
                    System.out.println(ERROR_MESSAGE);
                    t.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onPaintTick() {
        if (running) {
            try {
                script.paint();
            } catch (final Throwable t) {
                System.out.println(ERROR_MESSAGE);
                t.printStackTrace();
            }
        }
    }

    @Override
    public void onGameMessage(boolean flag, String s1, int i1, String s2, int j1, int k1, String s3,
                              String s4) {
        if (running) {
            if (s1 != null) {
                s1 = s1.replace((char) 160, ' ');
            }
            try {
                if (j1 == 1) {
                    script.onPrivateMessage(s2, s1, k1 == 1, k1 >= 2);
                } else if (j1 == 0 || j1 == 3) {
                    script.onServerMessage(s2);
                } else if (j1 == 4) {
                    script.onChatMessage(s2, s1, k1 == 1, k1 >= 2);
                } else if (j1 == 6) {
                    script.onTradeRequest(s1);
                }
            } catch (final Throwable t) {
                System.out.println(ERROR_MESSAGE);
                t.printStackTrace();
            }
        }
    }

    @Override
    public void onKeyPress(int i) {
        if (running) {
            script.onKeyPress(i);
        }
    }

    @Override
    public void onNewSleepWord() {
        newWord = true;
    }

    @Override
    public boolean isScriptRunning() {
        return running;
    }

    @Override
    public void setScriptRunning(boolean b) {
        running = b;
    }

    @Override
    public void setIScript(IScript script) {
        this.script = script;
    }

    @Override
    public String getScriptName() {
        if (script == null) {
            return "null";
        }
        return script.toString();
    }

    @Override
    public boolean hasScript() {
        return script != null;
    }

    @Override
    public void setBanned(boolean b) {
        banned = b;
    }
}
