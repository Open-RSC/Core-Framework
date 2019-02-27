package orsc.graphics.gui;

import orsc.enumerations.MenuItemAction;
import orsc.graphics.two.MudClientGraphics;
import orsc.util.ArrayUtil;
import orsc.util.GenUtil;

public final class Menu {
	public int font;
	private int itemCount;
	private int menuHeight;
	private MenuItem[] menuItems;
	private String menuTitle;
	private int menuWidth;
	private MudClientGraphics surf;

	public Menu(MudClientGraphics var1, int var2) {
		this(var1, var2, (String) null);
	}

	public Menu(MudClientGraphics surf, int font, String title) {
		this.menuHeight = 0;
		this.itemCount = 0;
		this.menuWidth = 0;

		try {
			this.font = font;
			this.menuItems = new MenuItem[10];
			this.surf = surf;
			this.menuTitle = title;

			for (int var4 = 0; var4 < 10; ++var4) {
				this.menuItems[var4] = new MenuItem();
			}

			this.calculateMenuWidth();
		} catch (RuntimeException var5) {
			throw GenUtil.makeThrowable(var5, "wb.<init>(" + (surf != null ? "{...}" : "null") + ',' + font + ','
				+ (title != null ? "{...}" : "null") + ')');
		}
	}

	public final void addCharacterItem(int index, MenuItemAction actionID, String label, String actor) {
		try {
			this.addItem(0, label, 0, 0, actor, index, (String) null, actionID, 0, (String) null, (String) null);

		} catch (RuntimeException var7) {
			throw GenUtil.makeThrowable(var7, "wb.DA(" + index + ',' + actionID + ',' + false + ','
				+ (label != null ? "{...}" : "null") + ',' + (actor != null ? "{...}" : "null") + ')');
		}
	}

	public final void addCharacterItem_WithID(int targetPlayer, String actor, MenuItemAction actionID, String label,
											  int selectedIndex) {
		try {

			this.addItem(selectedIndex, label, 0, 0, actor, targetPlayer, (String) null, actionID, 0, (String) null,
				(String) null);
		} catch (RuntimeException var8) {
			throw GenUtil.makeThrowable(var8, "wb.J(" + targetPlayer + ',' + (actor != null ? "{...}" : "null") + ','
				+ actionID + ',' + (label != null ? "{...}" : "null") + ',' + selectedIndex + ',' + "dummy" + ')');
		}
	}

	private void addItem(int id_or_z, String label, int dir, int var4, String actor, int index_or_x,
						 String dropped2, MenuItemAction actionID, int tile_id, String dropped, String strB) {
		try {
			if (this.menuItems.length == this.itemCount) {
				MenuItem[] src = this.menuItems;
				this.menuItems = new MenuItem[10 + this.itemCount];

				for (int i = 0; this.menuItems.length > i; ++i) {
					if (this.itemCount > i) {
						this.menuItems[i] = src[i];
					} else {
						this.menuItems[i] = new MenuItem();
					}
				}
			}


			this.menuItems[this.itemCount++].set(label, var4, index_or_x, id_or_z, tile_id, dropped2, 100, actionID,
				actor, dropped, dir, strB);
			this.calculateMenuWidth();
		} catch (RuntimeException var15) {
			throw GenUtil.makeThrowable(var15, "wb.N(" + id_or_z + ',' + (label != null ? "{...}" : "null") + ',' + dir
				+ ',' + var4 + ',' + (actor != null ? "{...}" : "null") + ',' + index_or_x + ','
				+ (dropped2 != null ? "{...}" : "null") + ',' + actionID + ',' + tile_id + ',' + "dummy" + ','
				+ (dropped != null ? "{...}" : "null") + ',' + (strB != null ? "{...}" : "null") + ')');
		}
	}

	public final void addItem(MenuItemAction action, String actor, String label) {
		try {

			this.addItem(0, label, 0, 0, actor, 0, (String) null, action, 0, (String) null, (String) null);
		} catch (RuntimeException var6) {
			throw GenUtil.makeThrowable(var6, "wb.V(" + action + ',' + (actor != null ? "{...}" : "null") + ','
				+ (label != null ? "{...}" : "null") + ',' + "dummy" + ')');
		}
	}

	public final void addItem_With2Strings(String label, String actor, String dropped, MenuItemAction actionID,
										   String strB) {
		try {
			this.addItem(0, label, 0, 0, actor, 0, (String) null, actionID, 0, dropped, strB);

		} catch (RuntimeException var8) {
			throw GenUtil.makeThrowable(var8,
				"wb.E(" + (label != null ? "{...}" : "null") + ',' + (actor != null ? "{...}" : "null") + ','
					+ (dropped != null ? "{...}" : "null") + ',' + actionID + ','
					+ (strB != null ? "{...}" : "null") + ',' + "dumb" + ')');
		}
	}

	public final void addTileItem(int x, byte var2, MenuItemAction actID, String label, String actor, int dir, int z) {
		try {

			this.addItem(z, label, dir, 0, actor, x, (String) null, actID, 0, (String) null, (String) null);
		} catch (RuntimeException var9) {
			throw GenUtil.makeThrowable(var9,
				"wb.W(" + x + ',' + 22 + ',' + actID + ',' + (label != null ? "{...}" : "null") + ','
					+ (actor != null ? "{...}" : "null") + ',' + dir + ',' + z + ')');
		}
	}

	public final void addTileItem_WithID(MenuItemAction actID, int z, int dir, int x, int id, String actor,
										 String name) {
		try {

			this.addItem(z, name, dir, 0, actor, x, (String) null, actID, id, (String) null, (String) null);
		} catch (RuntimeException var10) {
			throw GenUtil.makeThrowable(var10, "wb.B(" + actID + ',' + z + ',' + dir + ',' + x + ',' + "dummy" + ','
				+ id + ',' + (actor != null ? "{...}" : "null") + ',' + (name != null ? "{...}" : "null") + ')');
		}
	}

	public final void addUseOnObject(int var1, String label, int var3, int var4, int var5, MenuItemAction actID,
									 int var7, String actor, int var9) {
		try {

			this.addItem(var1, label, var7, var4, actor, var9, (String) null, actID, var5, (String) null,
				(String) null);
		} catch (RuntimeException var11) {
			throw GenUtil.makeThrowable(var11,
				"wb.I(" + var1 + ',' + (label != null ? "{...}" : "null") + ',' + var3 + ',' + var4 + ',' + var5
					+ ',' + actID + ',' + var7 + ',' + (actor != null ? "{...}" : "null") + ',' + var9 + ')');
		}
	}

	private void calculateMenuWidth() {
		try {

			int lineHeight = this.surf.fontHeight(this.font) + 1;
			if (null == this.menuTitle) {
				this.menuHeight = 0;
				this.menuWidth = 0;
			} else {
				this.menuHeight = lineHeight;
				this.menuWidth = 5 + this.surf.stringWidth(this.font, this.menuTitle);
			}

			for (int i = 0; this.itemCount > i; ++i) {
				this.menuHeight += lineHeight;
				int lineWidth = 5
					+ this.surf.stringWidth(this.font, this.menuItems[i].label + " " + this.menuItems[i].actor);
				if (lineWidth > this.menuWidth) {
					this.menuWidth = lineWidth;
				}
			}

		} catch (RuntimeException var5) {
			throw GenUtil.makeThrowable(var5, "wb.EA(" + "dummy" + ')');
		}
	}

	public final int getHeight() {
		try {

			return this.menuHeight;
		} catch (RuntimeException var3) {
			throw GenUtil.makeThrowable(var3, "wb.T(" + "dummy" + ')');
		}
	}

	public final MenuItemAction getItemAction(int item) {
		try {

			return this.menuItems[item].actionID;
		} catch (RuntimeException var4) {
			throw GenUtil.makeThrowable(var4, "wb.M(" + "dummy" + ',' + item + ')');
		}
	}

	public final String getItemActor(int item) {
		try {

			return this.menuItems[item].actor;
		} catch (RuntimeException var4) {
			throw GenUtil.makeThrowable(var4, "wb.O(" + "dummy" + ',' + item + ')');
		}
	}

	public final int getItemCount(int var1) {
		try {

			if (var1 != -27153) {
				this.calculateMenuWidth();
			}

			return this.itemCount;
		} catch (RuntimeException var3) {
			throw GenUtil.makeThrowable(var3, "wb.F(" + var1 + ')');
		}
	}

	public final int getItemDirection(int item) {
		try {

			return this.menuItems[item].dir;
		} catch (RuntimeException var4) {
			throw GenUtil.makeThrowable(var4, "wb.H(" + item + ',' + "dummy" + ')');
		}
	}

	public final int getItemIdOrZ(int var2) {
		try {

			return this.menuItems[var2].id_or_z;
		} catch (RuntimeException var4) {
			throw GenUtil.makeThrowable(var4, "wb.K(" + 97 + ',' + var2 + ')');
		}
	}

	public final int getItemIndexOrX(int item) {
		try {

			return this.menuItems[item].index_or_x;
		} catch (RuntimeException var4) {
			throw GenUtil.makeThrowable(var4, "wb.C(" + true + ',' + item + ')');
		}
	}

	public final String getItemLabel(int var1) {
		try {

			return this.menuItems[var1].label;
		} catch (RuntimeException var4) {
			throw GenUtil.makeThrowable(var4, "wb.A(" + var1 + ',' + "dummy" + ')');
		}
	}

	public final int getItemParam_l(int item) {
		try {

			return this.menuItems[item].m_l;
		} catch (RuntimeException var4) {
			throw GenUtil.makeThrowable(var4, "wb.Q(" + "dummy" + ',' + item + ')');
		}
	}

	public final String getItemStringB(int item) {
		try {

			return this.menuItems[item].strB;
		} catch (RuntimeException var4) {
			throw GenUtil.makeThrowable(var4, "wb.CA(" + item + ',' + "dummy" + ')');
		}
	}

	public final int getItemTileID(int item) {
		try {

			return this.menuItems[item].tile_id;
		} catch (RuntimeException var4) {
			throw GenUtil.makeThrowable(var4, "wb.L(" + item + ',' + "dummy" + ')');
		}
	}

	public final int getWidth() {
		try {

			return this.menuWidth;
		} catch (RuntimeException var3) {
			throw GenUtil.makeThrowable(var3, "wb.BA(" + "dummy" + ')');
		}
	}

	public final int handleClick(int mouseX, int menuX, int menuY, int mouseY) {
		try {

			return this.process(mouseY, mouseX, menuY, menuX, -3, false);
		} catch (RuntimeException var7) {
			throw GenUtil.makeThrowable(var7,
				"wb.D(" + mouseX + ',' + menuX + ',' + menuY + ',' + -40 + ',' + mouseY + ')');
		}
	}

	private int process(int mouseY, int mouseX, int menuY, int menuX, int var5, boolean draw) {
		try {

			if (this.menuWidth != 0 && this.menuHeight != 0) {
				if (draw) {
					this.surf.drawBoxAlpha(menuX, menuY, this.menuWidth, this.menuHeight, 13684944, 160);
				}

				int lineHeight = 1 + this.surf.fontHeight(this.font);
				int lineY = lineHeight + menuY - 3;
				int clickedLine = -1;
				if (null != this.menuTitle) {
					if (menuX < mouseX && mouseY > lineY + (3 - lineHeight) && lineY + 3 > mouseY
						&& mouseX < menuX + this.menuWidth) {
						if (!draw) {
							return -2;
						}

						clickedLine = -2;
					}

					if (draw) {
						this.surf.drawString(this.menuTitle, 2 + menuX, lineY, 0xFFFF, this.font);
					}

					lineY += lineHeight;
				}

				if (var5 >= -1) {
					this.menuTitle = (String) null;
				}

				for (int i = 0; i < this.itemCount; ++i) {
					int lineColor = 16777215;
					if (menuX < mouseX && mouseY > 3 + lineY - lineHeight && mouseY < 3 + lineY
						&& menuX + this.menuWidth > mouseX) {
						lineColor = 16776960;
						if (!draw) {
							return i;
						}

						clickedLine = i;
					}

					if (draw) {
						this.surf.drawString(this.menuItems[i].label + " " + this.menuItems[i].actor, menuX + 2, lineY,
							lineColor, this.font);
					}

					lineY += lineHeight;
				}

				return clickedLine;
			} else {
				return -1;
			}
		} catch (RuntimeException var12) {
			throw GenUtil.makeThrowable(var12,
				"wb.R(" + mouseY + ',' + mouseX + ',' + menuY + ',' + menuX + ',' + var5 + ',' + draw + ')');
		}
	}

	public final void recalculateSize(int var1) {
		try {

			this.itemCount = var1;
			this.calculateMenuWidth();
		} catch (RuntimeException var3) {
			throw GenUtil.makeThrowable(var3, "wb.P(" + var1 + ')');
		}
	}

	public final void removeItem(int item) {
		try {

			if (item >= 0 && this.itemCount > item) {
				MenuItem removed = this.menuItems[item];

				for (int i = item; i < this.itemCount - 1; ++i) {
					this.menuItems[i] = this.menuItems[1 + i];
				}

				this.menuItems[--this.itemCount] = removed;
				this.calculateMenuWidth();
			}
		} catch (RuntimeException var5) {
			throw GenUtil.makeThrowable(var5, "wb.G(" + "dummy" + ',' + item + ')');
		}
	}

	public final int render(int menuY, int menuX, int mouseY, byte var4, int mouseX) {
		try {

			return this.process(mouseY, mouseX, menuY, menuX, -66, true);
		} catch (RuntimeException var7) {
			throw GenUtil.makeThrowable(var7,
				"wb.U(" + menuY + ',' + menuX + ',' + mouseY + ',' + -12 + ',' + mouseX + ')');
		}
	}

	public final void sort() {
		try {

			if (this.itemCount != 0) {
				int[] priority = new int[this.itemCount];
				MenuItem[] src = new MenuItem[this.itemCount];

				int i;
				for (i = 0; this.itemCount > i; ++i) {
					MenuItem tmp = this.menuItems[i];
					priority[i] = tmp.actionID.priority();
					src[i] = tmp;
				}

				ArrayUtil.quickSort(src, priority);
				i = 0;

				while (i < this.itemCount) {
					this.menuItems[i] = (MenuItem) src[i];
					++i;
				}

			}
		} catch (RuntimeException var6) {
			throw GenUtil.makeThrowable(var6, "wb.AA(" + "dummy" + ')');
		}
	}
}
