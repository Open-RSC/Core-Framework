import com.aposbot.Constants;
import com.aposbot._default.IAutoLogin;
import com.aposbot._default.IClient;

import java.util.Locale;

public final class AutoLogin
        implements IAutoLogin {

    private static final AutoLogin instance = new AutoLogin();
    private IClient c;
    private boolean enabled;
    private String username;
    private String password;
    private boolean loaded;
    private volatile boolean banned;
    private long next_attempt;

    private AutoLogin() {
    }

    static void init(IClient client) {
        instance.c = client;
    }

    public static boolean isAutoLogin() {
        return instance.isEnabled();
    }

    public static void setAutoLogin(boolean b) {
        instance.setEnabled(b);
    }

    public static void setCredentials(String username, String password) {
        instance.setAccount(username, password);
    }

    public static void onLoginResponse(String arg0, String arg1) {
        String message = arg0 + " " + arg1;
        System.out.println(message);
        message = message.toLowerCase(Locale.ENGLISH);
        if (message.contains("updated")) {
            System.out.println("Looks like APOS is out of date. Wait patiently for an update to be released on the forums.");
            instance.next_attempt = Long.MAX_VALUE;
        } else if (message.contains("member") ||
                message.contains("stolen") ||
                message.contains("locked") ||
                message.contains("banned") ||
                message.contains("customer support") ||
                message.contains("new players") ||
                message.contains("cannot access") ||
                message.contains("disabled")) {
            System.out.println("Autologin has been infinitely delayed due to an apparent fatal problem. If you've managed to solve that problem, re-enable it.");
            instance.next_attempt = Long.MAX_VALUE;
        } else {
            instance.next_attempt = System.currentTimeMillis() + 4000L + Constants.RANDOM.nextInt(8000);
        }
    }

    public static void loginTick() {
        instance.onLoginTick();
    }

    public static void checkForLog() {
        loginTick();
    }

    public static void welcomeBoxTick() {
        instance.onWelcomeBoxTick();
    }

    static AutoLogin get() {
        return instance;
    }

    @Override
    public void onLoginTick() {
        if (banned) return;

        if (System.currentTimeMillis() < next_attempt) {
            return;
        }

        if (!loaded) {
            String font = ClientInit.getBotLoader().getFont();
            if (font != null) {
                System.out.println("Setting font to " + font);
                c.setFont(font);
            }
            StaticAccess.setStrings();
            loaded = true;
        }

        if (enabled && username != null) {
            final int len = password.length();
            final StringBuilder b = new StringBuilder(len);
            for (int i = 0; i < len; i++) {
                b.append('*');
            }
            System.out.println("Logging in: " + username + " " + b.toString());
            c.login(username, password);
        }
    }

    @Override
    public void onWelcomeBoxTick() {
        c.onLoggedIn();
        if (enabled) {
            System.out.println("Closing welcome box.");
            c.closeWelcomeBox();
        }
        PaintListener.resetXp();
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean b) {
        enabled = b;
        if (b) {
            next_attempt = 0;
        }
    }

    @Override
    public void setBanned(boolean b) {
        banned = b;
    }

    @Override
    public void setAccount(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
