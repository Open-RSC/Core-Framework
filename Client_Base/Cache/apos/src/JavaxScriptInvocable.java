import javax.script.Invocable;
import javax.script.ScriptException;
import java.math.BigDecimal;
import java.math.BigInteger;

public final class JavaxScriptInvocable extends Script {

    private final Invocable inv;
    private final String name;

    public JavaxScriptInvocable(Extension ex, Invocable inv, String name) {
        super(ex);
        this.inv = inv;
        this.name = name;
    }

    private static int looseToInt(Object object) {
        // needlessly excessive?
        Class<?> c = object.getClass();
        if (Integer.class.equals(c)) {
            return (Integer) object;
        } else if (Long.class.equals(c)) {
            return ((Long) object).intValue();
        } else if (Short.class.equals(c)) {
            return ((Short) object).intValue();
        } else if (Byte.class.equals(c)) {
            return ((Byte) object).intValue();
        } else if (Float.class.equals(c)) {
            return ((Float) object).intValue();
        } else if (Double.class.equals(c)) {
            return ((Double) object).intValue();
        } else if (String.class.equals(c)) {
            return Integer.parseInt((String) object);
        } else if (BigInteger.class.equals(c)) {
            return ((BigInteger) object).intValue();
        } else if (BigDecimal.class.equals(c)) {
            return ((BigDecimal) object).intValue();
        } else {
            return 0;
        }
    }

    @Override
    public void init(String params) {
        try {
            inv.invokeFunction("init", params);
        } catch (NoSuchMethodException ignored) {
        } catch (ScriptException ex) {
            System.out.println(ScriptListener.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    @Override
    public int main() {
        try {
            return looseToInt(inv.invokeFunction("main", this));
        } catch (NoSuchMethodException ignored) {
        } catch (ScriptException ex) {
            System.out.println(ScriptListener.ERROR_MESSAGE);
            ex.printStackTrace();
        }
        return 1000;
    }

    @Override
    public void paint() {
        try {
            inv.invokeFunction("paint", this);
        } catch (NoSuchMethodException ignored) {
        } catch (ScriptException ex) {
            System.out.println(ScriptListener.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    @Override
    public void onServerMessage(String str) {
        try {
            inv.invokeFunction("onServerMessage", str);
        } catch (NoSuchMethodException ignored) {
        } catch (ScriptException ex) {
            System.out.println(ScriptListener.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    @Override
    public void onTradeRequest(String name) {
        try {
            inv.invokeFunction("onTradeRequest", name);
        } catch (NoSuchMethodException ignored) {
        } catch (ScriptException ex) {
            System.out.println(ScriptListener.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    @Override
    public void onChatMessage(String msg, String name, boolean mod,
                              boolean admin) {

        try {
            inv.invokeFunction("onChatMessage", msg, name, mod, admin);
        } catch (NoSuchMethodException ignored) {
        } catch (ScriptException ex) {
            System.out.println(ScriptListener.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    @Override
    public void onPrivateMessage(String msg, String name, boolean mod,
                                 boolean admin) {

        try {
            inv.invokeFunction("onPrivateMessage", msg, name, mod, admin);
        } catch (NoSuchMethodException ignored) {
        } catch (ScriptException ex) {
            System.out.println(ScriptListener.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    @Override
    public void onKeyPress(int keycode) {
        try {
            inv.invokeFunction("onKeyPress", keycode);
        } catch (NoSuchMethodException ignored) {
        } catch (ScriptException ex) {
            System.out.println(ScriptListener.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return name;
    }
}
