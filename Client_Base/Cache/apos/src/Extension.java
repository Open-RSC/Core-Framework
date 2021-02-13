import com.aposbot.BotFrame;
import com.aposbot._default.*;

import javax.script.Invocable;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.KeyEvent;

public class Extension extends client
        implements IClient {

    private static final long serialVersionUID = 1L;
    private final BotFrame frame;
    private boolean disableKeys;
    private IClientInit init;

    Extension(BotFrame frame) {
        this.frame = frame;
    }

    public IClientInit getParentInit() {
        return this.init;
    }

    public void setParentInit(IClientInit init) {
        this.init = init;
    }

    @Override
    public void onLoggedIn() {
        frame.enableRenderingToggle();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(Wd, Oi + 12);
    }

    private void paste() {
        try {
            Clipboard c = Toolkit.getDefaultToolkit()
                    .getSystemClipboard();
            Transferable t = c.getContents(null);
            DataFlavor f = DataFlavor.stringFlavor;
            if (!t.isDataFlavorSupported(f)) {
                return;
            }
            char[] str = ((String) t.getTransferData(f))
                    .toCharArray();
            for (int i = 0; i < str.length; ++i) {
                if (str[i] >= ' ' && str[i] <= '~') {
                    typeChar(str[i], str[i]);
                }
            }
        } catch (Throwable t) {
            System.out.println("Couldn't paste: " + t);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        final int keyCode = e.getKeyCode();
        if (disableKeys) {
            ScriptListener.get().onKeyPress(keyCode);
            return;
        }
        if ((e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) != 0) {
            if (e.getKeyCode() == KeyEvent.VK_V) {
                paste();
                return;
            }
        }
        switch (keyCode) {
            case KeyEvent.VK_ESCAPE:
                setCameraNSOffset(0);
                setCameraEWOffset(0);
                setCameraHeight(750);
                fogginess = 0;
                break;
            case KeyEvent.VK_PAGE_UP:
                setCameraNSOffset(getCameraNSOffset() - 10);
                break;
            case KeyEvent.VK_PAGE_DOWN:
                setCameraNSOffset(getCameraNSOffset() + 10);
                break;
            case KeyEvent.VK_HOME:
                setCameraEWOffset(getCameraEWOffset() + 10);
                break;
            case KeyEvent.VK_END:
                setCameraEWOffset(getCameraEWOffset() - 10);
                break;
            case KeyEvent.VK_UP:
                final int h = getCameraHeight();
                if (h > 300) {
                    setCameraHeight(h - 10);
                    fogginess -= 30;
                }
                break;
            case KeyEvent.VK_DOWN:
                setCameraHeight(getCameraHeight() + 10);
                fogginess += 30;
                break;
            case KeyEvent.VK_F12:
            case KeyEvent.VK_PRINTSCREEN:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        takeScreenshot(String.valueOf(System.currentTimeMillis()));
                    }
                }, "ScreenshotThread").start();
                break;
            default:
                ScriptListener.get().onKeyPress(keyCode);
                super.keyPressed(e);
                break;
        }
    }

    @Override
    public boolean isRendering() {
        return !disable_gfx;
    }

    @Override
    public void setRendering(boolean b) {
        disable_gfx = !b;
    }

    @Override
    public void setKeysDisabled(boolean b) {
        disableKeys = b;
    }

    @Override
    public void stopScript() {
        frame.stopScript();
    }

    @Override
    public void takeScreenshot(String fileName) {
        frame.takeScreenshot(fileName);
    }

    @Override
    public int getServer() {
        return Integer.parseInt(frame.getCodeBase().substring(14, 15));
    }

    @Override
    public void setServer(int id) {
        frame.updateWorld(id);
    }

    @Override
    public int getCameraNSOffset() {
        return oc;
    }

    @Override
    public void setCameraNSOffset(int i) {
        oc = i;
    }

    @Override
    public int getCameraEWOffset() {
        return Be;
    }

    @Override
    public void setCameraEWOffset(int i) {
        Be = i;
    }

    @Override
    public int getCameraHeight() {
        return ac;
    }

    @Override
    public void setCameraHeight(int i) {
        ac = i;
    }

    @Override
    public void setActionInd(int i) {
        xh = i;
    }

    @Override
    public void closeWelcomeBox() {
        Oh = false;
    }

    @Override
    public int getLocalX() {
        return Lf;
    }

    @Override
    public int getLocalY() {
        return sh;
    }

    @Override
    public int getCombatTimer() {
        return ai;
    }

    @Override
    public void setLogoutTimer(int i) {
        bj = i;
    }

    @Override
    public int getAreaX() {
        return Qg;
    }

    @Override
    public int getAreaY() {
        return zg;
    }

    @Override
    public int getBaseLevel(int skill) {
        return cg[skill];
    }

    @Override
    public int getCurrentLevel(int skill) {
        return oh[skill];
    }

    @Override
    public double getExperience(int skill) {
        return Ak[skill] / 4.0;
    }

    @Override
    public int getCombatStyle() {
        return Fg;
    }

    @Override
    public void setCombatStyle(int i) {
        Fg = i;
    }

    @Override
    public String[] getDialogOptions() {
        return ah;
    }

    @Override
    public int getDialogOptionCount() {
        return Id;
    }

    @Override
    public boolean isDialogVisible() {
        return Ph;
    }

    @Override
    public void setDialogVisible(boolean b) {
        Ph = false;
    }

    @Override
    public boolean isSleeping() {
        return Qk;
    }

    @Override
    public int getInventorySize() {
        return lc;
    }

    @Override
    public int getInventoryId(int i) {
        return vf[i];
    }

    @Override
    public int getInventoryStack(int i) {
        return xe[i];
    }

    @Override
    public boolean isBankVisible() {
        return Fe;
    }

    @Override
    public void setBankVisible(boolean b) {
        Fe = b;
    }

    @Override
    public int getBankSize() {
        return vj;
    }

    @Override
    public int getBankId(int i) {
        return ae[i];
    }

    @Override
    public int getBankStack(int i) {
        return di[i];
    }

    @Override
    public int getGroundItemCount() {
        return Ah;
    }

    @Override
    public int getGroundItemLocalX(int i) {
        return Zf[i];
    }

    @Override
    public int getGroundItemLocalY(int i) {
        return Ni[i];
    }

    @Override
    public int getGroundItemId(int i) {
        return Gj[i];
    }

    @Override
    public int getObjectCount() {
        return eh;
    }

    @Override
    public int getObjectLocalX(int i) {
        return Se[i];
    }

    @Override
    public int getObjectLocalY(int i) {
        return ye[i];
    }

    @Override
    public int getObjectId(int i) {
        return vc[i];
    }

    @Override
    public int getObjectDir(int i) {
        return bg[i];
    }

    @Override
    public int getBoundCount() {
        return hf;
    }

    @Override
    public int getBoundLocalX(int i) {
        return Jd[i];
    }

    @Override
    public int getBoundLocalY(int i) {
        return yk[i];
    }

    @Override
    public int getBoundDir(int i) {
        return Hj[i];
    }

    @Override
    public int getBoundId(int i) {
        return Ng[i];
    }

    @Override
    public void typeChar(char key_char, int key_code) {
        // TODO: keep shift down
        boolean upper = Character.isUpperCase(key_char);
        int m = 0;
        if (upper) {
            super.keyPressed(new KeyEvent(this,
                    KeyEvent.KEY_PRESSED, System.currentTimeMillis(),
                    0, KeyEvent.VK_SHIFT, KeyEvent.CHAR_UNDEFINED));
            m |= KeyEvent.VK_SHIFT;
        }
        super.keyPressed(new KeyEvent(this, KeyEvent.KEY_PRESSED,
                System.currentTimeMillis(), m, key_code, key_char));
        super.keyTyped(new KeyEvent(this, KeyEvent.KEY_TYPED,
                System.currentTimeMillis(), m,
                KeyEvent.VK_UNDEFINED, key_char));
        super.keyReleased(new KeyEvent(this, KeyEvent.KEY_RELEASED,
                System.currentTimeMillis(), m, key_code, key_char));
        if (upper) {
            super.keyReleased(new KeyEvent(this,
                    KeyEvent.KEY_RELEASED, System.currentTimeMillis(),
                    0, KeyEvent.VK_SHIFT, KeyEvent.CHAR_UNDEFINED));
        }
    }

    @Override
    public boolean isShopVisible() {
        return uk;
    }

    @Override
    public void setShopVisible(boolean b) {
        uk = b;
    }

    @Override
    public int getShopSize() {
        return Rj.length;
    }

    @Override
    public int getShopId(int i) {
        return Rj[i];
    }

    @Override
    public int getShopStack(int i) {
        return Jf[i];
    }

    @Override
    public boolean isEquipped(int slot) {
        return Aj[slot] == 1;
    }

    @Override
    public boolean isPrayerEnabled(int id) {
        return bk[id];
    }

    @Override
    public void setPrayerEnabled(int i, boolean flag) {
        bk[i] = flag;
    }

    @Override
    public boolean isInTradeOffer() {
        return Hk;
    }

    @Override
    public void setInTradeOffer(boolean b) {
        Hk = b;
    }

    @Override
    public boolean isInTradeConfirm() {
        return Xj;
    }

    @Override
    public void setInTradeConfirm(boolean b) {
        Xj = b;
    }

    @Override
    public boolean hasLocalAcceptedTrade() {
        return Mi;
    }

    @Override
    public boolean hasLocalConfirmedTrade() {
        return Vi;
    }

    @Override
    public boolean hasRemoteAcceptedTrade() {
        return md;
    }

    @Override
    public int getLocalTradeItemCount() {
        return mf;
    }

    @Override
    public int getLocalTradeItemId(int i) {
        return Qf[i];
    }

    @Override
    public int getLocalTradeItemStack(int i) {
        return jj[i];
    }

    @Override
    public int getRemoteTradeItemCount() {
        return Lk;
    }

    @Override
    public int getRemoteTradeItemId(int i) {
        return zj[i];
    }

    @Override
    public int getRemoteTradeItemStack(int i) {
        return Dd[i];
    }

    private ba getScreen() {
        return li;
    }

    @Override
    public int[] getPixels() {
        return getScreen().rb;
    }

    @Override
    public Image getImage() {
        return getScreen().Gb;
    }

    @Override
    public boolean isLoggedIn() {
        return getScreen() != null && qg == 1;
    }

    @Override
    public int[][] getAdjacency() {
        return Hh.bb;
    }

    @Override
    public void drawString(String str, int x, int y, int font, int colour) {
        /* use xb so our text has shadows */
        boolean orig = getScreen().xb;
        getScreen().xb = true;
        getScreen().a(str, x, y, colour, false, font);
        getScreen().xb = orig;
    }

    @Override
    public void displayMessage(String str) {
        a(false, ((String) (null)), 0, str, 7, 0, ((String) (null)), ((String) (null)));
    }

    @Override
    public void offerItemTrade(int slot, int amount) {
        a(amount, (byte) 9, slot);
    }

    @Override
    public void login(String username, String password) {
        Vh = 2;
        a(-12, password, username, false);
    }

    @Override
    public void walkDirectly(int destLX, int destLY, boolean action) {
        a(destLY, destLX, sh, Lf, action, 8);
    }

    @Override
    public void walkAround(int destLX, int destLY) {
        a((byte) 10, sh, destLY, destLX, true, Lf);
    }

    @Override
    public void walkToBound(int destLX, int destLY, int dir) {
        a(false, destLX, destLY, dir);
    }

    @Override
    public void walkToObject(int destLX, int destLY, int dir, int id) {
        b(5126, id, destLX, destLY, dir);
    }

    @Override
    public void createPacket(int opcode) {
        Jh.b(opcode, 0);
    }

    @Override
    public void put1(int val) {
        Jh.f.c(val, 115);
    }

    @Override
    public void put2(int val) {
        Jh.f.e(393, val);
    }

    @Override
    public void put4(int val) {
        Jh.f.b(-422797528, val);
    }

    @Override
    public void finishPacket() {
        Jh.b(21294);
    }

    @Override
    public void sendCAPTCHA(String str) {
        Jh.b(45, 0);
        if (Yk) {
            Jh.f.c(1, -75);
        } else {
            Jh.f.c(0, -100);
            Yk = true;
        }
        Jh.f.a(str, 116);
        Jh.b(21294);
        e = "";
        Zj = il[436];
        Cb = "";
    }

    @Override
    public boolean isSkipLines() {
        return U;
    }

    @Override
    public void setSkipLines(boolean flag) {
        U = flag;
    }

    @Override
    public void sendPrivateMessage(String msg, String name) {
        name = name.replace(' ', (char) 160);
        x = "";
        Bj = 0;
        Ob = "";
        Qd = name;
        a((byte) -76, name, msg);
    }

    @Override
    public void addFriend(String str) {
        str = str.replace(' ', (char) 160);
        Bj = 0;
        e = "";
        Cb = "";
        b(114, str);
    }

    @Override
    public void addIgnore(String str) {
        str = str.replace(' ', (char) 160);
        e = "";
        Bj = 0;
        Cb = "";
        a(str, (byte) 5);
    }

    @Override
    public void removeIgnore(String str) {
        str = str.replace(' ', (char) 160);
        //a((byte)-15, ia.a[j3]);
        a((byte) -15, str);
    }

    @Override
    public void removeFriend(String str) {
        str = str.replace(' ', (char) 160);
        //b(ua.h[i3], (byte)69);
        b(str, (byte) 69);
    }

    @Override
    public int getQuestCount() {
        return Te.length;
    }

    @Override
    public String getQuestName(int i) {
        return Te[i];
    }

    @Override
    public boolean isQuestComplete(int i) {
        return fi[i];
    }

    @Override
    public double getFatigue() {
        return (vg * 100.0) / 750.0;
    }

    @Override
    public double getSleepingFatigue() {
        return (100.0 * pg) / 750.0;
    }

    @Override
    public int getPlayerCount() {
        return Yc;
    }

    @Override
    public Object getPlayer() {
        return wi;
    }

    @Override
    public Object getPlayer(int index) {
        return rg[index];
    }

    @Override
    public String getPlayerName(Object mob) {
        return ((ta) mob).c.replace((char) 160, ' ');
    }

    @Override
    public int getPlayerCombatLevel(Object mob) {
        return ((ta) mob).s;
    }

    @Override
    public int getNpcCount() {
        return de;
    }

    @Override
    public Object getNpc(int index) {
        return Tb[index];
    }

    @Override
    public int getNpcId(Object mob) {
        return ((ta) mob).t;
    }

    @Override
    public boolean isMobWalking(Object mob) {
        return ((ta) mob).e != (((ta) mob).o + 1) % 10;
    }

    @Override
    public boolean isMobTalking(Object mob) {
        return ((ta) mob).I > 0;
    }

    @Override
    public boolean isHeadIconVisible(Object mob) {
        return ((ta) mob).E > 0;
    }

    @Override
    public boolean isHpBarVisible(Object mob) {
        return ((ta) mob).d > 0;
    }

    @Override
    public int getMobBaseHitpoints(Object mob) {
        return ((ta) mob).G;
    }

    @Override
    public int getMobServerIndex(Object mob) {
        return ((ta) mob).b;
    }

    private int getMobLocDivide() {
        return Ug;
    }

    @Override
    public int getMobLocalX(Object mob) {
        return (((ta) mob).i - 64) / getMobLocDivide();
    }

    @Override
    public int getMobLocalY(Object mob) {
        return (((ta) mob).K - 64) / getMobLocDivide();
    }

    @Override
    public int getMobDirection(Object mob) {
        return ((ta) mob).y;
    }

    @Override
    public boolean isMobInCombat(Object mob) {
        final int dir = getMobDirection(mob);
        return dir == 8 || dir == 9;
    }

    @Override
    public IStaticAccess getStatic() {
        return StaticAccess.get();
    }

    @Override
    public IScriptListener getScriptListener() {
        return ScriptListener.get();
    }

    @Override
    public IAutoLogin getAutoLogin() {
        return AutoLogin.get();
    }

    @Override
    public void setAutoLogin(boolean b) {
        frame.setAutoLogin(b);
    }

    @Override
    public IPaintListener getPaintListener() {
        return PaintListener.get();
    }

    @Override
    public IJokerFOCR getJoker() {
        return Joker.get();
    }

    @Override
    public IScript createInvocableScript(Invocable inv, String name) {
        return new JavaxScriptInvocable(this, inv, name);
    }

    @Override
    public void setFont(String name) {
        StaticAccess.loadFont(this, "h11p", name, 0);
        StaticAccess.loadFont(this, "h12b", name, 1);
        StaticAccess.loadFont(this, "h12p", name, 2);
        StaticAccess.loadFont(this, "h13b", name, 3);
        StaticAccess.loadFont(this, "h14b", name, 4);
        StaticAccess.loadFont(this, "h16b", name, 5);
        StaticAccess.loadFont(this, "h20b", name, 6);
        StaticAccess.loadFont(this, "h24b", name, 7);
    }

    @Override
    public final byte[] a(String s1, int i1, int j1, int k1) {
        // found in applet class
        try {
            J++;
            return StaticAccess.load(s1, i1, j1);
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return null;
    }

    @Override
    public void resizeGame(int width, int height) {
        Wd = width;
        Oi = height - 12;

        Rh = width;
        Hf = height;
        Eb = (-this.Wd + this.Rh) / 2;
        K = 0;

        // set raster
        li.rb = new int[width * height];
        li.lb = li.u = width;
        li.Rb = li.k = height;
        li.fb.setDimensions(width, height);

		/*
		li.Gb = this.createImage(li);
		for (int i = 0; i < 3; ++i) {
			li.b(true);
			this.prepareImage(li.Gb, this);
		}

		System.out.println(li.Gb.getWidth(null) + " " + li.Gb.getHeight(null));
		*/

        // set scene
        Ek.pb = li.rb;
        Ek.A = width;
        Ek.wb = height;
        Ek.a(this.Oi / 2, true, this.Wd, this.Wd / 2, this.Oi / 2, this.qd, this.Wd / 2);

        // make interfaces
        O(56);

        Mc = new qa(this.li, 5);
        Ud = Mc.a(this.li.u - 199, 196, 90,
                true, 106, 500, 24 + 36, 1);

        zk = new qa(this.li, 5);
        Hi = zk.a(this.li.u - 199, 196, 126,
                true, 106, 500, 36 + 40, 1);

        fe = new qa(this.li, 5);
        lk = fe.a(this.li.u - 199, 196, 251,
                true, 106, 500, 24 + 36, 1);

        if ((width - 73) < 1000) {
            client.il[262] = String.format(
                    "~%d~@whi@Remove         WWWWWWWWWW", width - 73);
        } else {
            client.il[262] = "";
        }

        Xb = getGraphics();
        //repaint();
    }

    @Override
    public int getGameWidth() {
        return Wd;
    }

    @Override
    public int getGameHeight() {
        return Oi + 12;
    }

    @Override
    public String getServerAddress() {
        return Dh;
    }

    @Override
    public void setServerAddress(String serverAddress) {
        Dh = serverAddress;
    }
}
