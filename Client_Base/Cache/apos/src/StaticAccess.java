import com.aposbot._default.IStaticAccess;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URL;

public final class StaticAccess
        implements IStaticAccess {

    static final String[] SKILL_NAMES = {
            "Attack", "Defense", "Strength", "Hits", "Ranged", "Prayer", "Magic",
            "Cooking", "Woodcut", "Fletching", "Fishing", "Firemaking", "Crafting",
            "Smithing", "Mining", "Herblaw", "Agility", "Thieving"
    };

    static final String[] SPELL_NAMES = {
            "Wind strike", "Confuse", "Water strike", "Enchant lvl-1 amulet",
            "Earth strike", "Weaken", "Fire strike", "Bones to bananas",
            "Wind bolt", "Curse", "Low level alchemy", "Water bolt",
            "Varrock teleport", "Enchant lvl-2 amulet", "Earth bolt",
            "Lumbridge teleport", "Telekinetic grab", "Fire bolt",
            "Falador teleport", "Crumble undead", "Wind blast", "Superheat item",
            "Camelot teleport", "Water blast", "Enchant lvl-3 amulet",
            "Iban blast", "Ardougne teleport", "Earth blast", "High level alchemy",
            "Charge Water Orb", "Enchant lvl-4 amulet", "Watchtower teleport",
            "Fire blast", "Claws of Guthix", "Saradomin strike",
            "Flames of Zamorak", "Charge earth Orb", "Wind wave",
            "Charge Fire Orb", "Water wave", "Charge air Orb", "Vulnerability",
            "Enchant lvl-5 amulet", "Earth wave", "Enfeeble", "Fire wave", "Stun",
            "Charge"
    };

    static final int[] CACHE_SUMS = {
            0x229aa476,
            0x1c9fa8c3,
            0x2fdddb3c,
            0x5181c9f5,
            0xaaca2b0d,
            0x6a1d6b00,
            0xe997514b,
            0x3fc5d9e3,
            0xb03e2a0c,
            0xe0e19e2c,
            0xa95e7195,
            0x7d5437c5
    };

    private static final StaticAccess instance = new StaticAccess();

    static void setStrings() {
        ac.x[165] += " (Guam Leaf)";
        ac.x[435] += " (Marrentill)";
        ac.x[436] += " (Tarromin)";
        ac.x[437] += " (Harralander)";
        ac.x[438] += " (Ranarr Weed)";
        ac.x[439] += " (Irit Leaf)";
        ac.x[440] += " (Avantoe)";
        ac.x[441] += " (Kwuarm)";
        ac.x[442] += " (Cadantine)";
        ac.x[443] += " (Dwarf Weed)";
        ac.x[933] += " (Torstol)";
        ac.x[221] += " (4)";
        ac.x[222] += " (3)";
        ac.x[223] += " (2)";
        ac.x[224] += " (1)";
        for (int i = 0; i < 9; ++i) {
            ac.x[480 + (i * 3)] += " (3)";
            ac.x[480 + (i * 3) + 1] += " (2)";
            ac.x[480 + (i * 3) + 2] += " (1)";
        }
        for (int i = 0; i < 2; ++i) {
            ac.x[566 + (i * 3)] += " (3)";
            ac.x[566 + (i * 3) + 1] += " (2)";
            ac.x[566 + (i * 3) + 2] += " (1)";
        }
        ac.x[963] += " (3)";
        ac.x[964] += " (2)";
        ac.x[965] += " (1)";
    }

    public static final StaticAccess get() {
        return instance;
    }

    static final boolean loadFont(e game, String name, String replacement, int index) {
        // Modification of client's qa.a
        // added "replacement" argument
        boolean flag = false;
        name = name.toLowerCase();
        boolean flag1 = false;
        if (name.startsWith("helvetica")) {
            name = name.substring(9);
        }
        if (name.startsWith("h")) {
            name = name.substring(1);
        }
        if (name.startsWith("f")) {
            name = name.substring(1);
            flag = true;
        }
        if (name.startsWith("d")) {
            name = name.substring(1);
            flag1 = true;
        }
        if (name.endsWith(".jf")) {
            name = name.substring(0, -3 + name.length());
        }
        int k1 = 0;
        if (name.endsWith("b")) {
            k1 = 1;
            name = name.substring(0, name.length() - 1);
        }
        if (name.endsWith("p")) {
            name = name.substring(0, -1 + name.length());
        }
        int size = Integer.parseInt(name);
        Font font = new Font(replacement, k1, size);
        FontMetrics fontmetrics = game.getFontMetrics(font);
        final String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!\"\243$%^&*()-_=+[{]};:'@#~,<.>/?\\| ";
        b.c = 855;
        for (int i2 = 0; -96 < ~i2; i2++) {
            if (!s.a(index, font, i2, -95, game, characters.charAt(i2),
                    fontmetrics, flag1)) {
                return false;
            }
        }

        m.b[index] = new byte[b.c];
        for (int j2 = 0; ~j2 > ~b.c; j2++) {
            m.b[index][j2] = qb.k[j2];
        }

        if (1 == k1 && fb.k[index]) {
            fb.k[index] = false;
            if (!loadFont(game, (new StringBuilder()).append("f").append(size)
                    .append("p").toString(), replacement, index)) {
                return false;
            }
        }
        if (flag && !fb.k[index]) {
            fb.k[index] = false;
			return loadFont(game, (new StringBuilder()).append("d").append(size)
					.append("p").toString(), replacement, index);
        }
        return true;
    }

    // modification of ib.a
    public static byte[] load(String friendly_name, int arg0, int index) throws IOException {
        ib.b++;
        if (null != la.g[index]) {
            return la.g[index];
        }
        nb.q = arg0;
        o.l = friendly_name;
        if (m.e != null) {
            byte[] data = m.e.a(9395, index);
            if (null != data && mb.a(data, data.length, 0) == tb.l[index]) {
                la.g[index] = k.a(128, true, data);
                return la.g[index];
            }
        }

        if (tb.l[index] == 0) {
            tb.l[index] = CACHE_SUMS[index];
        }

        String filename = (new StringBuilder()).append(ib.z[5])
                .append(index).append("_")
                .append(Long.toHexString(tb.l[index])).toString();
        byte[] data = null;
        String path = "." + File.separator + "Content" + File.separator + filename;
        File file = new File(path);
        boolean file_exists = file.exists();
        URL url = null;
        if (!file_exists) {
            url = new URL(ib.c, filename);
        }

        for (int j1 = 0; ~j1 > -4; j1++) {

            if (!file_exists) {
                System.out.println("[" + friendly_name + "]: loading from URL: " + url);
                data = da.a(url, true, true);
                if (data != null) {
                    FileOutputStream out = new FileOutputStream(file);
                    try {
                        out.write(data);
                    } finally {
                        out.close();
                    }
                }
            } else {
                System.out.println("[" + friendly_name + "]: loading from file: " + path);
                RandomAccessFile f = new RandomAccessFile(file, "r");
                data = new byte[(int) f.length()];
                try {
                    f.readFully(data);
                } finally {
                    f.close();
                }
            }

            if (~mb.a(data, data.length, 0) != ~tb.l[index]) {
                continue;
            }
            if (null != m.e) {
                m.e.a(index, data.length, -97, data);
            }
            la.g[index] = k.a(128, true, data);
            return la.g[index];
        }

        if (null != data) {
            StringBuilder stringbuilder = new StringBuilder(
                    (new StringBuilder()).append(ib.z[4]).append(index)
                            .append(ib.z[3]).append(tb.l[index]).toString());
            stringbuilder.append((new StringBuilder()).append(ib.z[2])
                    .append(data.length).toString());
            for (int k1 = 0; ~data.length < ~k1 && -6 < ~k1; k1++) {
                stringbuilder.append((new StringBuilder()).append(" ")
                        .append(data[k1]).toString());
            }

            throw new IOException(stringbuilder.toString());
        } else {
            throw new IOException((new StringBuilder()).append(ib.z[4])
                    .append(index).append(ib.z[3]).append(tb.l[index])
                    .toString());
        }
    }

    @Override
    public String getNpcName(int id) {
        return e.Mb[id];
    }

    @Override
    public String getNpcDesc(int id) {
        return ba.ac[id];
    }

    @Override
    public int getNpcLevel(int id) {
        return ((eb.b[id] + (la.a[id] + jb.k[id])) - -fb.d[id]) / 4;
    }

    @Override
    public String getItemName(int id) {
        return ac.x[id];
    }

    @Override
    public String getItemDesc(int id) {
        return ga.b[id];
    }

    @Override
    public String getItemCommand(int id) {
        return lb.ac[id];
    }

    @Override
    public int getItemBasePrice(int id) {
        return kb.b[id];
    }

    @Override
    public boolean isItemStackable(int id) {
        return fa.e[id] != 1;
    }

    @Override
    public boolean isItemTradable(int id) {
        return kb.c[id] != 1;
    }

    @Override
    public String getObjectName(int id) {
        return l.a[id];
    }

    @Override
    public String getObjectDesc(int id) {
        return la.f[id];
    }

    @Override
    public String getBoundName(int id) {
        return ta.r[id];
    }

    @Override
    public String getBoundDesc(int id) {
        return ub.b[id];
    }

    @Override
    public int getSpellReqLevel(int id) {
        return pa.f[id];
    }

    @Override
    public int getSpellType(int i) {
        return qb.e[i];
    }

    @Override
    public int getReagentCount(int id) {
        return o.p[id];
    }

    @Override
    public int getReagentId(int spell, int i) {
        return oa.d[spell][i];
    }

    @Override
    public int getReagentAmount(int spell, int i) {
        return da.J[spell][i];
    }

    @Override
    public int getFriendCount() {
        return n.g;
    }

    @Override
    public String getFriendName(int i) {
        return ua.h[i].replace((char) 160, ' ');
    }

    @Override
    public int getIgnoredCount() {
        return db.g;
    }

    @Override
    public String getIgnoredName(int i) {
        return ia.a[i].replace((char) 160, ' ');
    }

    @Override
    public int getPrayerCount() {
        return t.g;
    }

    @Override
    public int getPrayerLevel(int i) {
        return ca.B[i];
    }

    @Override
    public String getPrayerName(int i) {
        return t.h[i];
    }

    @Override
    public String[] getSpellNames() {
        return SPELL_NAMES;
    }

    @Override
    public String[] getSkillNames() {
        return SKILL_NAMES;
    }
}
