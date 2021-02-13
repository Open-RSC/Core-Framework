import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Choice;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Locale;

import com.aposbot.Constants;
import com.aposbot.StandardCloseHandler;

public final class S_Stake extends Script {

	private static final int GNOME_AMULET = 744;
	private static final int R2H = 81;
	private static final int CHAIN = 400;
	private static final int DAXE = 594;
	private static final int DSWORD = 593;
	private static final int RUBY_AMULET = 316;

	private static final int[] prayers = {
		9, 10, 11
	};
	private static final int[] dstone_amulets = {
		597, 522
	};
	private static final int[] weapons = {
		DAXE, DSWORD, R2H, 1217, 1216, 1218
	};
	private static final int[] shields = {
		1278, 404
	};
	private static final int[] helms =  {
		795, 112
	};
	private static final int[] body =  {
		407, 401, CHAIN
	};
	private static final int[] legs =  {
		402, 406
	};
	private static final int[] capes = {
		1288, 1215, 1214, 1213
	};
	private static final int[] amulets = {
		dstone_amulets[0], dstone_amulets[1], GNOME_AMULET, RUBY_AMULET
	};
	private static final int[] gauntlets = {
		698, 699, 700, 701, 1006
	};

	private boolean equip;
	private boolean pray;
	private boolean bare;
	private boolean prayers_off;
	
	private boolean listen_input;
	private StringBuilder input;
	
	private int hue;
	private int ticks;
	private boolean hue_dir;

	private boolean cast;

	private int set_mode = -1;
	
	private static final int AS_NONE = 0;
	private static final int AS_ATTACK = 1;
	private static final int AS_DEFENCE = 2;
	private int as_type = AS_NONE;

	private static final int SE_AMULET = 0;
	private static final int SE_PLATE = 1;
	private static final int SE_KITE = 2;
	private int se_type = SE_AMULET;

	private boolean switch_mode = true;
	private boolean switch_pray;

	private final Choice spell_choice = new Choice();
	private final Choice player_choice = new Choice();
	private final Frame frame = new Frame();

	private int wear_id = -1;
	private boolean power;

	public static void main(String[] argv) {
		new S_Stake(null).show_ac_frame();
	}

	public S_Stake(Extension ex) {
		super(ex);
		for (String s : SPELL) {
			spell_choice.add(s);
		}
		spell_choice.select(33);
		Panel c_panel = new Panel(new GridLayout(2, 2));
		c_panel.add(new Label("Spell:"));
		c_panel.add(spell_choice);
		c_panel.add(new Label("Player:"));
		c_panel.add(player_choice);
		Panel b_panel = new Panel();
		Button ok = new Button("OK");
		ok.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				frame.setVisible(false);
			}
		});
		b_panel.add(ok);
		frame.addWindowListener(
			new StandardCloseHandler(frame, StandardCloseHandler.HIDE));
		frame.setIconImages(Constants.ICONS);
		frame.setTitle(getClass().getSimpleName());
		frame.add(c_panel, BorderLayout.CENTER);
		frame.add(b_panel, BorderLayout.SOUTH);
		frame.pack();
		frame.setResizable(false);
	}

	private void show_ac_frame() {
		try {
			player_choice.removeAll();
			int count = countPlayers();
			for (int i = 0; i < count; ++i) {
				player_choice.add(getPlayerName(i));
			}
		} catch (Throwable t) {
		}
		frame.setLocationRelativeTo(null);
		frame.toFront();
		frame.requestFocus();
		frame.setVisible(true);
	}

	private boolean equip(int[] ids, int count) {
		for (int i = 0; i < count; ++i) {
			int id = getInventoryId(i);
			if (inArray(ids, id) && isItemEquipped(i)) {
				return false;
			}
		}
		for (int i : ids) {
			for (int j = 0; j < count; ++j) {
				if (getInventoryId(j) == i) {
					wearItem(j);
					return true;
				}
			}
		}
		return false;
	}

	private boolean id_equipped(int id, int count) {
		for (int i = 0; i < count; ++i) {
			int j = getInventoryId(i);
			if (id == j) return isItemEquipped(i);
		}
		return false;
	}

	@Override
	public int main() {
		disableKeys();
		if (equip) {
			int count = getInventoryCount();
			if (equip(weapons, count)) return random(50, 100);
			if (equip(body, count)) return random(50, 100);
			if (equip(legs, count)) return random(50, 100);
			if (!id_equipped(R2H, count)) {
				if (equip(shields, count)) return random(50, 100);
			}
			if (equip(helms, count)) return random(50, 100);
			if (equip(amulets, count)) return random(50, 100);
			if (id_equipped(CHAIN, count)) {
				if (equip(gauntlets, count)) return random(50, 100);
			}
			if (equip(capes, count)) return random(50, 100);
			equip = false;
		}
		if (bare) {
			int count = getInventoryCount();
			for (int i = 0; i < count; ++i) {
				if (isItemEquipped(i)) {
					removeItem(i);
					return random(50, 100);
				}
			}
			bare = false;
		}
		if (prayers_off) {
			for (int id : prayers) {
				if (isPrayerEnabled(id)) {
					disablePrayer(id);
					return random(50, 100);
				}
			}
			prayers_off = false;
		}
		if (pray) {
			for (int id : prayers) {
				if (!isPrayerEnabled(id)) {
					enablePrayer(id);
					return random(50, 100);
				}
			}
			pray = false;
		}
		if (set_mode != -1) {
			setFightMode(set_mode);
			set_mode = -1;
			return random(50, 100);
		}
		if (wear_id != -1) {
			int index = getInventoryIndex(wear_id);
			if (index != -1 && !isItemEquipped(index)) {
				wearItem(index);
				return random(50, 100);
			}
			wear_id = -1;
		}
		if (as_type == AS_ATTACK) {
			int mode = power ? 1 : 2;
			if (switch_mode && getFightMode() != mode) {
				setFightMode(mode);
				return random(10, 20);
			}
			if (switch_pray) {
				if (isPrayerEnabled(9)) {
					disablePrayer(9);
					return random(10, 20);
				}
				if (!isPrayerEnabled(10)) {
					enablePrayer(10);
					return random(10, 20);
				}
				if (!isPrayerEnabled(11)) {
					enablePrayer(11);
					return random(10, 20);
				}
			}
			switch (se_type) {
			case SE_AMULET:
				if (power) {
					int index = getInventoryIndex(RUBY_AMULET);
					if (index != -1 && !isItemEquipped(index)) {
						wearItem(index);
						return random(50, 100);
					}
				} else {
					if (equip(dstone_amulets, getInventoryCount())) {
						return random(50, 100);
					}
				}
				break;
			case SE_PLATE:
				if (equip(gauntlets, getInventoryCount())) {
					return random(50, 100);
				}
				break;
			case SE_KITE:
				int index = getInventoryIndex(R2H);
				if (index != -1 && !isItemEquipped(index)) {
					wearItem(index);
					return random(50, 100);
				}
				break;
			}
			as_type = AS_NONE;
			return random(10, 20);
		} else if (as_type == AS_DEFENCE) {
			if (switch_mode && getFightMode() != 3) {
				setFightMode(3);
				return random(10, 20);
			}
			if (switch_pray) {
				if (isPrayerEnabled(10)) {
					disablePrayer(10);
					return random(10, 20);
				}
				if (isPrayerEnabled(11)) {
					disablePrayer(11);
					return random(10, 20);
				}
				if (!isPrayerEnabled(9)) {
					enablePrayer(9);
					return random(10, 20);
				}
			}
			switch (se_type) {
			case SE_AMULET:
				int index = getInventoryIndex(GNOME_AMULET);
				if (index != -1 && !isItemEquipped(index)) {
					wearItem(index);
					return random(50, 100);
				}
				break;
			case SE_PLATE:
				if (equip(body, getInventoryCount())) {
					return random(50, 100);
				}
				break;
			case SE_KITE:
				if (equip(shields, getInventoryCount())) {
					return random(50, 100);
				}
				break;
			}
			as_type = AS_NONE;
			return random(10, 20);
		}
		if (cast) {
			int[] player = getPlayerByName(player_choice.getSelectedItem());
			if (player[0] != -1) {
				magePlayer(player[0], spell_choice.getSelectedIndex());
				return random(600, 800);
			}
			System.out.println("ERROR: Player not found!");
			return random(1000, 2000);
		}
		return 0;
	}
	
	private void process_input() {
		listen_input = false;
		try {
			String[] input = this.input.toString().trim().toLowerCase(Locale.ENGLISH).split(" ");
			if (input[0].equals("prayoff")) {
				prayers_off = true;
			} else if (input[0].equals("bare")) {
				bare = true;
			} else if (input[0].equals("mode")) {
				switch (input[1].charAt(0)) {
				case '0':
				case 'c':
					set_mode = 0;
					break;
				case '1':
				case 's':
					set_mode = 1;
					break;
				case '2':
				case 'a':
					set_mode = 2;
					break;
				case '3':
				case 'd':
					set_mode = 3;
					break;
				}
			} else if (input[0].equals("switchpray")) {
				switch_pray = input[1].charAt(0) != '0';
				System.out.println(switch_mode ? "Switching prayers" : "Not switching prayers");
			}
		} catch (Throwable t) {
		}
	}
	
	@Override
	public void onKeyPress(int keycode) {
		if (listen_input) {
			if (keycode == KeyEvent.VK_BACK_SPACE) {
				if (input.length() == 0) {
					listen_input = false;
				} else {
					input.deleteCharAt(input.length() - 1);
				}
			} else if (keycode == KeyEvent.VK_ENTER) {
				process_input();
			} else {
				if (Character.isUpperCase(keycode)) {
					input.append((char) (keycode + ('a' - 'A')));
				} else if (keycode == ' ' || Character.isDigit(keycode)) {
					input.append((char) keycode);
				}
			}
			return;
		}

		switch (keycode) {
			case KeyEvent.VK_X:
				show_ac_frame();
				break;
			case KeyEvent.VK_C:
				cast = !cast;
				break;

			case KeyEvent.VK_E:
				equip = true;
				break;
			case KeyEvent.VK_P:
				pray = true;
				break;

			case KeyEvent.VK_R:
				se_type = SE_AMULET;
				as_type = AS_ATTACK;
				break;
			case KeyEvent.VK_T:
				se_type = SE_AMULET;
				as_type = AS_DEFENCE;
				break;

			case KeyEvent.VK_F:
				se_type = SE_PLATE;
				as_type = AS_ATTACK;
				break;
			case KeyEvent.VK_G:
				se_type = SE_PLATE;
				as_type = AS_DEFENCE;
				break;

			case KeyEvent.VK_V:
				se_type = SE_KITE;
				as_type = AS_ATTACK;
				break;
			case KeyEvent.VK_B:
				se_type = SE_KITE;
				as_type = AS_DEFENCE;
				break;

			case KeyEvent.VK_A:
				if (wear_id == -1) {
					wear_id = DAXE;
				}
				break;
			case KeyEvent.VK_S:
				if (wear_id == -1) {
					wear_id = DSWORD;
				}
				break;

			case KeyEvent.VK_SPACE:
				power = !power;
				System.out.println("Preferring " + (power ? "power" : "accuracy"));
				break;

			case KeyEvent.VK_M:
				switch_mode = !switch_mode;
				System.out.println(switch_mode ? "Switching modes" : "Not switching modes");
				break;

			case KeyEvent.VK_SLASH:
				listen_input = true;
				input = new StringBuilder();
				break;
		}
	}
	
	private String get_stat_string(int skill) {
		int cur = getCurrentLevel(skill);
		int level = getLevel(skill);
		String col;
		if (getCurrentLevel(skill) > getLevel(skill)) {
			col = "@gre@";
		} else if (getCurrentLevel(skill) == getLevel(skill)) {
			col = "@ora@";
		} else {
			col = "@red@";
		}
		return col + cur + "  @whi@/  " + level;
	}
	
	@Override
	public void paint() {
		++ticks;
		if ((ticks % 2) == 0) {
			if (hue_dir) ++hue;
			else --hue;
			if (hue < 0) {
				hue = 0;
				hue_dir = true;
			} else if (hue > 135) {
				hue = 135;
				hue_dir = false;
			}
		}

		int rgb = java.awt.Color.HSBtoRGB(hue / 255.0f, 0.9f, 0.9f);
		int x = 395;
		int y = 320;
		
		drawString("@mag@MAG:   " + get_stat_string(6), x, y, 1, 0);
		y -= 15;
		drawString("@whi@STR:   " + get_stat_string(2), x, y, 1, 0);
		y -= 15;
		drawString("@ora@DEF:   " + get_stat_string(1), x, y, 1, 0);
		y -= 15;
		drawString("@cya@ATK:   " + get_stat_string(0), x, y, 1, 0);
		y -= 15;

		int pcount = 0;
		for (int i = 0; i < 13; ++i) {
			if (isPrayerEnabled(i)) {
				++pcount;
			}
		}
		
		if (pcount == 0) {
			drawString("Prayers enabled: @red@" + pcount, x, y, 1, 0xFFFFFF);
		} else {
			drawString("Prayers enabled: @gre@" + pcount, x, y, 1, 0xFFFFFF);
		}
		y -= 15;
		
		if (!inCombat()) {
			drawString("Style: " + FIGHTMODES[getFightMode()], x, y, 2, 0xFFFFFF);
			y -= 15;
		}
		
		if (equip) {
			drawString("Equipping...", x, y, 1, rgb);
			y -= 15;
		}
		
		if (bare) {
			drawString("Going bare...", x, y, 1, rgb);
			y -= 15;
		}
		
		if (cast) {
			drawString("Autocasting", x, y, 1, rgb);
			y -= 15;
		}
		
		if (listen_input) {
			drawString("Command@gre@:@whi@ " + input + ((ticks % 40) < 20 ? "" : "*"), 315, 50, 1, 0xFFFFFF);
		}
	}
}