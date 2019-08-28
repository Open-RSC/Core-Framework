package com.openrsc.interfaces.misc;

import com.openrsc.interfaces.InputListener;
import com.openrsc.interfaces.NComponent;
import com.openrsc.interfaces.NCustomComponent;
import com.openrsc.interfaces.misc.party.Party;
import com.openrsc.client.entityhandling.EntityHandler.GUIPARTS;
import com.openrsc.client.entityhandling.EntityHandler;
import orsc.graphics.gui.SocialLists;
import orsc.mudclient;
import orsc.util.GenUtil;

public class PartyGUI {
	public static mudclient mc;
	public NComponent partyGuiComponent;

	public PartyGUI(final mudclient graphics) {
		partyGuiComponent = new NComponent(graphics);
		partyGuiComponent.setSize(130, 37);
		//partyGuiComponent.setBackground(0xFFFFFF, 0xFFFFFF, 128);
		partyGuiComponent.setLocation((graphics.getGameWidth() - 175) / 20, graphics.getGameHeight() - 310);

		NCustomComponent partyGuiItself = new NCustomComponent(graphics) {
			@Override
			public void render() {
				float partyGuiWidth = 120;
				int i2 = 75;
				int index;
				int var12;
				if (graphics.party.inParty()) {
					for (index = 0; index < SocialLists.partyListCount; ++index) {
						if (SocialLists.partyListCount == 1) {
							partyGuiComponent.setSize(130, 37);
						} else if (SocialLists.partyListCount == 2) {
							partyGuiComponent.setSize(130, 57);
						} else if (SocialLists.partyListCount == 3) {
							partyGuiComponent.setSize(130, 77);
						} else if (SocialLists.partyListCount == 4) {
							partyGuiComponent.setSize(130, 97);
						} else if (SocialLists.partyListCount == 5) {
							partyGuiComponent.setSize(130, 117);
						}
						String partyIsh = graphics.party.username[index];
						var12 = 100;
						int var777;
						var777 = GenUtil.buildColor(220, 220, 220);
						for (int var13 = graphics.party.username[index].length(); graphics.getSurface().stringWidth(1, partyIsh) > 120; partyIsh = graphics.party.username[index].substring(0, var13 - var12) + "...") {
							++var12;
						}
						if (SocialLists.partyListCount == 1) {
							if (graphics.party.skull[0] > 0) {
								graphics.getSurface().drawSprite(graphics.spriteSelect(EntityHandler.GUIparts.get(GUIPARTS.SKULL.id())), getX() + 58, getY() - 6, 14, 14, 5924);
							}
							if (graphics.party.inCombat[0] > 0) {
								graphics.getSurface().drawSprite(graphics.spriteSelect(EntityHandler.GUIparts.get(GUIPARTS.EQUIPSLOT_SWORD.id())), getX() + 45, getY() - 6, 14, 14, 5924);
							}
							if (graphics.party.pMemD[0] > 0) {
								graphics.party.pMemDTimeout[0] = 500;
							}
							if (graphics.party.pMemDTimeout[0] > 0) {
								graphics.getSurface().drawSprite(graphics.spriteSelect(EntityHandler.GUIparts.get(GUIPARTS.DAMAGETAKEN.id())), getX() + 84, getY() - 6, 14, 14, 5924);
							}
							graphics.getSurface().drawString("@yel@" + graphics.party.username[0] + "@whi@-" + graphics.party.cbLvl[0], getX() - 20, getY() + 6, 0xffffff, 0);
							if (graphics.party.partyRank[0] == 1) {
								graphics.getSurface().drawSprite(graphics.spriteSelect(EntityHandler.crowns.get(1)), getX() + 71, getY() - 6, 14, 14, 5924);
							}
							int hpMissing = 0;
							double prog1 = 0;
							double prog2 = 0;
							hpMissing = graphics.party.maxHp[0] - graphics.party.curHp[0];
							prog1 = ((double) hpMissing / graphics.party.maxHp[0]);
							prog2 = (prog1 * 100);
							int prog3 = (int) Math.round(prog2);
							graphics.getSurface().drawBox(getX() - 20, getY() + 8, 100, 4, 0xFF0000);
							if (graphics.party.pMemDTimeout[0] < 1) {
								graphics.getSurface().drawBox(getX() - 20, getY() + 8, 100 - prog3, 4, 0x00FF00);
							}
						} else if (SocialLists.partyListCount == 2) {
							if (graphics.party.partyRank[0] == 1) {
								graphics.getSurface().drawSprite(graphics.spriteSelect(EntityHandler.crowns.get(1)), getX() + 71, getY() - 6, 14, 14, 5924);
							}
							if (graphics.party.partyRank[1] == 1) {
								graphics.getSurface().drawSprite(graphics.spriteSelect(EntityHandler.crowns.get(1)), getX() + 71, getY() + 14, 14, 14, 5924);
							}
							if (graphics.party.skull[0] > 0) {
								graphics.getSurface().drawSprite(graphics.spriteSelect(EntityHandler.GUIparts.get(GUIPARTS.SKULL.id())), getX() + 58, getY() - 6, 14, 14, 5924);
							}
							if (graphics.party.skull[1] > 0) {
								graphics.getSurface().drawSprite(graphics.spriteSelect(EntityHandler.GUIparts.get(GUIPARTS.SKULL.id())), getX() + 58, getY() + 13, 14, 14, 5924);
							}
							if (graphics.party.pMemD[0] > 0) {
								graphics.party.pMemDTimeout[0] = 500;
							}
							if (graphics.party.pMemD[1] > 0) {
								graphics.party.pMemDTimeout[1] = 500;
							}
							if (graphics.party.pMemDTimeout[0] > 0) {
								graphics.getSurface().drawSprite(graphics.spriteSelect(EntityHandler.GUIparts.get(GUIPARTS.DAMAGETAKEN.id())), getX() + 84, getY() - 6, 14, 14, 5924);
							}
							if (graphics.party.pMemDTimeout[1] > 0) {
								graphics.getSurface().drawSprite(graphics.spriteSelect(EntityHandler.GUIparts.get(GUIPARTS.DAMAGETAKEN.id())),  getX() + 84, getY() + 14, 14, 14, 5924);
							}
							if (graphics.party.inCombat[0] > 0) {
								graphics.getSurface().drawSprite(graphics.spriteSelect(EntityHandler.GUIparts.get(GUIPARTS.EQUIPSLOT_SWORD.id())), getX() + 45, getY() - 6, 14, 14, 5924);
							}
							if (graphics.party.inCombat[1] > 0) {
								graphics.getSurface().drawSprite(graphics.spriteSelect(EntityHandler.GUIparts.get(GUIPARTS.EQUIPSLOT_SWORD.id())), getX() + 45, getY() + 14, 14, 14, 5924);
							}
							graphics.getSurface().drawString("@yel@" + graphics.party.username[0] + "@whi@-" + graphics.party.cbLvl[0], getX() - 20, getY() + 6, 0xffffff, 0);
							int hpMissing = 0;
							double prog1 = 0;
							double prog2 = 0;
							hpMissing = graphics.party.maxHp[0] - graphics.party.curHp[0];
							prog1 = ((double) hpMissing / graphics.party.maxHp[0]);
							prog2 = (prog1 * 100);
							int prog3 = (int) Math.round(prog2); // 3
							graphics.getSurface().drawBox(getX() - 20, getY() + 8, 100, 4, 0xFF0000);
							if (graphics.party.pMemDTimeout[0] < 1) {
								graphics.getSurface().drawBox(getX() - 20, getY() + 8, 100 - prog3, 4, 0x00FF00);
							}
							graphics.getSurface().drawString("@yel@" + graphics.party.username[1] + "@whi@-" + graphics.party.cbLvl[1], getX() - 20, getY() + 26, 0xffffff, 0);
							int hpMissing1 = 0;
							double prog11 = 0;
							double prog22 = 0;
							hpMissing1 = graphics.party.maxHp[1] - graphics.party.curHp[1];
							prog11 = ((double) hpMissing1 / graphics.party.maxHp[1]);
							prog22 = (prog11 * 100);
							int prog33 = (int) Math.round(prog22); // 3
							graphics.getSurface().drawBox(getX() - 20, getY() + 28, 100, 4, 0xFF0000);
							if (graphics.party.pMemDTimeout[1] < 1) {
								graphics.getSurface().drawBox(getX() - 20, getY() + 28, 100 - prog33, 4, 0x00FF00);
							}
						} else if(SocialLists.partyListCount == 3) {
							if (graphics.party.pMemD[0] > 0) {
								graphics.party.pMemDTimeout[0] = 500;
							}
							if (graphics.party.pMemD[1] > 0) {
								graphics.party.pMemDTimeout[1] = 500;
							}
							if (graphics.party.pMemD[2] > 0) {
								graphics.party.pMemDTimeout[2] = 500;
							}
							if (graphics.party.pMemDTimeout[0] > 0) {
								graphics.getSurface().drawSprite(graphics.spriteSelect(EntityHandler.GUIparts.get(GUIPARTS.DAMAGETAKEN.id())), getX() + 84, getY() - 6, 14, 14, 5924);
							}
							if (graphics.party.pMemDTimeout[1] > 0) {
								graphics.getSurface().drawSprite(graphics.spriteSelect(EntityHandler.GUIparts.get(GUIPARTS.DAMAGETAKEN.id())), getX() + 84, getY() + 14, 14, 14, 5924);
							}
							if (graphics.party.pMemDTimeout[2] > 0) {
								graphics.getSurface().drawSprite(graphics.spriteSelect(EntityHandler.GUIparts.get(GUIPARTS.DAMAGETAKEN.id())), getX() + 84, getY() + 34, 14, 14, 5924);
							}
							int hpMissing1 = 0;
							double prog111 = 0;
							double prog222 = 0;
							hpMissing1 = graphics.party.maxHp[0] - graphics.party.curHp[0];
							prog111 = ((double) hpMissing1 / graphics.party.maxHp[0]);
							prog222 = (prog111 * 100);
							int prog333 = (int) Math.round(prog222); // 3
							graphics.getSurface().drawBox(getX() - 20, getY() + 8, 100, 4, 0xFF0000);
							if (graphics.party.pMemDTimeout[0] < 1) {
								graphics.getSurface().drawBox(getX() - 20, getY() + 8, 100 - prog333, 4, 0x00FF00);
							}
							int hpMissing111 = 0;
							double prog1111 = 0;
							double prog2222 = 0;
							hpMissing111 = graphics.party.maxHp[1] - graphics.party.curHp[1];
							prog1111 = ((double) hpMissing111 / graphics.party.maxHp[1]);
							prog2222 = (prog1111 * 100);
							int prog3333 = (int) Math.round(prog2222);
							graphics.getSurface().drawBox(getX() - 20, getY() + 28, 100, 4, 0xFF0000);
							if (graphics.party.pMemDTimeout[1] < 1) {
								graphics.getSurface().drawBox(getX() - 20, getY() + 28, 100 - prog3333, 4, 0x00FF00);
							}
							int hpMissing1111 = 0;
							double prog11111 = 0;
							double prog22222 = 0;
							hpMissing1111 = graphics.party.maxHp[2] - graphics.party.curHp[2];
							prog11111 = ((double) hpMissing1111 / graphics.party.maxHp[2]);
							prog22222 = (prog11111 * 100);
							int prog33333 = (int) Math.round(prog22222);
							graphics.getSurface().drawBox(getX() - 20, getY() + 48, 100, 4, 0xFF0000);
							if (graphics.party.pMemDTimeout[2] < 1) {
								graphics.getSurface().drawBox(getX() - 20, getY() + 48, 100 - prog33333, 4, 0x00FF00);
							}
							if (graphics.party.partyRank[0] == 1) {
								graphics.getSurface().drawSprite(graphics.spriteSelect(EntityHandler.crowns.get(1)), getX() + 71, getY() - 6, 14, 14, 5924);
							}
							if (graphics.party.partyRank[1] == 1) {
								graphics.getSurface().drawSprite(graphics.spriteSelect(EntityHandler.crowns.get(1)), getX() + 71, getY() + 14, 14, 14, 5924);
							}
							if (graphics.party.partyRank[2] == 1) {
								graphics.getSurface().drawSprite(graphics.spriteSelect(EntityHandler.crowns.get(1)), getX() + 71, getY() + 34, 14, 14, 5924);
							}
							if (graphics.party.skull[0] > 0) {
								graphics.getSurface().drawSprite(graphics.spriteSelect(EntityHandler.GUIparts.get(GUIPARTS.SKULL.id())), getX() + 58, getY() - 6, 14, 14, 5924);
							}
							if (graphics.party.skull[1] > 0) {
								graphics.getSurface().drawSprite(graphics.spriteSelect(EntityHandler.GUIparts.get(GUIPARTS.SKULL.id())), getX() + 58, getY() + 13, 14, 14, 5924);
							}
							if (graphics.party.skull[2] > 0) {
								graphics.getSurface().drawSprite(graphics.spriteSelect(EntityHandler.GUIparts.get(GUIPARTS.SKULL.id())), getX() + 58, getY() + 32, 14, 14, 5924);
							}
							if (graphics.party.inCombat[0] > 0) {
								graphics.getSurface().drawSprite(graphics.spriteSelect(EntityHandler.GUIparts.get(GUIPARTS.EQUIPSLOT_SWORD.id())), getX() + 45, getY() - 6, 14, 14, 5924);
							}
							if (graphics.party.inCombat[1] > 0) {
								graphics.getSurface().drawSprite(graphics.spriteSelect(EntityHandler.GUIparts.get(GUIPARTS.EQUIPSLOT_SWORD.id())), getX() + 45, getY() + 14, 14, 14, 5924);
							}
							if (graphics.party.inCombat[2] > 0) {
								graphics.getSurface().drawSprite(graphics.spriteSelect(EntityHandler.GUIparts.get(GUIPARTS.EQUIPSLOT_SWORD.id())), getX() + 45, getY() + 34, 14, 14, 5924);
							}
							graphics.getSurface().drawString("@yel@" + graphics.party.username[0] + "@whi@-" + graphics.party.cbLvl[0], getX() - 20, getY() + 6, 0xffffff, 0);
							graphics.getSurface().drawString("@yel@" + graphics.party.username[1] + "@whi@-" + graphics.party.cbLvl[1], getX() - 20, getY() + 26, 0xffffff, 0);
							graphics.getSurface().drawString("@yel@" + graphics.party.username[2] + "@whi@-" + graphics.party.cbLvl[2], getX() - 20, getY() + 46, 0xffffff, 0);
						} else if (SocialLists.partyListCount == 4) {
							if (graphics.party.pMemD[0] > 0) {
								graphics.party.pMemDTimeout[0] = 500;
							}
							if (graphics.party.pMemD[1] > 0) {
								graphics.party.pMemDTimeout[1] = 500;
							}
							if (graphics.party.pMemD[2] > 0) {
								graphics.party.pMemDTimeout[2] = 500;
							}
							if (graphics.party.pMemD[3] > 0) {
								graphics.party.pMemDTimeout[3] = 500;
							}
							if (graphics.party.pMemDTimeout[0] > 0) {
								graphics.getSurface().drawSprite(graphics.spriteSelect(EntityHandler.GUIparts.get(GUIPARTS.DAMAGETAKEN.id())), getX() + 84, getY() - 6, 14, 14, 5924);
							}
							if (graphics.party.pMemDTimeout[1] > 0) {
								graphics.getSurface().drawSprite(graphics.spriteSelect(EntityHandler.GUIparts.get(GUIPARTS.DAMAGETAKEN.id())), getX() + 84, getY() + 14, 14, 14, 5924);
							}
							if (graphics.party.pMemDTimeout[2] > 0) {
								graphics.getSurface().drawSprite(graphics.spriteSelect(EntityHandler.GUIparts.get(GUIPARTS.DAMAGETAKEN.id())), getX() + 84, getY() + 34, 14, 14, 5924);
							}
							if (graphics.party.pMemDTimeout[3] > 0) {
								graphics.getSurface().drawSprite(graphics.spriteSelect(EntityHandler.GUIparts.get(GUIPARTS.DAMAGETAKEN.id())), getX() + 84, getY() + 54, 14, 14, 5924);
							}
							int hpMissing1212 = 0;
							double prog111111 = 0;
							double prog222222 = 0;
							hpMissing1212 = graphics.party.maxHp[0] - graphics.party.curHp[0];
							prog111111 = ((double) hpMissing1212 / graphics.party.maxHp[0]);
							prog222222 = (prog111111 * 100);
							int prog333333 = (int) Math.round(prog222222); // 3
							graphics.getSurface().drawBox(getX() - 20, getY() + 8, 100, 4, 0xFF0000);
							if (graphics.party.pMemDTimeout[0] < 1) {
								graphics.getSurface().drawBox(getX() - 20, getY() + 8, 100 - prog333333, 4, 0x00FF00);
							}
							int hpMissing111222 = 0;
							double prog1111222 = 0;
							double prog2222333 = 0;
							hpMissing111222 = graphics.party.maxHp[1] - graphics.party.curHp[1];
							prog1111222 = ((double) hpMissing111222 / graphics.party.maxHp[1]);
							prog2222333 = (prog1111222 * 100);
							int prog33332222 = (int) Math.round(prog2222333);
							graphics.getSurface().drawBox(getX() - 20, getY() + 28, 100, 4, 0xFF0000);
							if (graphics.party.pMemDTimeout[1] < 1) {
								graphics.getSurface().drawBox(getX() - 20, getY() + 28, 100 - prog33332222, 4, 0x00FF00);
							}
							int hpMissing11116767 = 0;
							double prog111117878 = 0;
							double prog222228888 = 0;
							hpMissing11116767 = graphics.party.maxHp[2] - graphics.party.curHp[2];
							prog111117878 = ((double) hpMissing11116767 / graphics.party.maxHp[2]);
							prog222228888 = (prog111117878 * 100);
							int prog333331111 = (int) Math.round(prog222228888);
							graphics.getSurface().drawBox(getX() - 20, getY() + 48, 100, 4, 0xFF0000);
							if (graphics.party.pMemDTimeout[2] < 1) {
								graphics.getSurface().drawBox(getX() - 20, getY() + 48, 100 - prog333331111, 4, 0x00FF00);
							}
							int hphphphphphp = 0;
							double pgpg = 0;
							double pgpg2 = 0;
							hphphphphphp = graphics.party.maxHp[3] - graphics.party.curHp[3];
							pgpg = ((double) hphphphphphp / graphics.party.maxHp[3]);
							pgpg2 = (pgpg * 100);
							int pgpg3 = (int) Math.round(pgpg2);
							graphics.getSurface().drawBox(getX() - 20, getY() + 68, 100, 4, 0xFF0000);
							if (graphics.party.pMemDTimeout[3] < 1) {
								graphics.getSurface().drawBox(getX() - 20, getY() + 68, 100 - pgpg3, 4, 0x00FF00);
							}
							if (graphics.party.partyRank[0] == 1) {
								graphics.getSurface().drawSprite(graphics.spriteSelect(EntityHandler.crowns.get(1)), getX() + 71, getY() - 6, 14, 14, 5924);
							}
							if (graphics.party.partyRank[1] == 1) {
								graphics.getSurface().drawSprite(graphics.spriteSelect(EntityHandler.crowns.get(1)), getX() + 71, getY() + 14, 14, 14, 5924);
							}
							if (graphics.party.partyRank[2] == 1) {
								graphics.getSurface().drawSprite(graphics.spriteSelect(EntityHandler.crowns.get(1)), getX() + 71, getY() + 34, 14, 14, 5924);
							}
							if (graphics.party.partyRank[3] == 1) {
								graphics.getSurface().drawSprite(graphics.spriteSelect(EntityHandler.crowns.get(1)), getX() + 71, getY() + 54, 14, 14, 5924);
							}
							if (graphics.party.skull[0] > 0) {
								graphics.getSurface().drawSprite(graphics.spriteSelect(EntityHandler.GUIparts.get(GUIPARTS.SKULL.id())), getX() + 58, getY() - 6, 14, 14, 5924);
							}
							if (graphics.party.skull[1] > 0) {
								graphics.getSurface().drawSprite(graphics.spriteSelect(EntityHandler.GUIparts.get(GUIPARTS.SKULL.id())), getX() + 58, getY() + 13, 14, 14, 5924);
							}
							if (graphics.party.skull[2] > 0) {
								graphics.getSurface().drawSprite(graphics.spriteSelect(EntityHandler.GUIparts.get(GUIPARTS.SKULL.id())), getX() + 58, getY() + 32, 14, 14, 5924);
							}
							if (graphics.party.skull[3] > 0) {
								graphics.getSurface().drawSprite(graphics.spriteSelect(EntityHandler.GUIparts.get(GUIPARTS.SKULL.id())), getX() + 58, getY() + 51, 14, 14, 5924);
							}
							if (graphics.party.inCombat[0] > 0) {
								graphics.getSurface().drawSprite(graphics.spriteSelect(EntityHandler.GUIparts.get(GUIPARTS.EQUIPSLOT_SWORD.id())), getX() + 45, getY() - 6, 14, 14, 5924);
							}
							if (graphics.party.inCombat[1] > 0) {
								graphics.getSurface().drawSprite(graphics.spriteSelect(EntityHandler.GUIparts.get(GUIPARTS.EQUIPSLOT_SWORD.id())), getX() + 45, getY() + 14, 14, 14, 5924);
							}
							if (graphics.party.inCombat[2] > 0) {
								graphics.getSurface().drawSprite(graphics.spriteSelect(EntityHandler.GUIparts.get(GUIPARTS.EQUIPSLOT_SWORD.id())), getX() + 45, getY() + 34, 14, 14, 5924);
							}
							if (graphics.party.inCombat[3] > 0) {
								graphics.getSurface().drawSprite(graphics.spriteSelect(EntityHandler.GUIparts.get(GUIPARTS.EQUIPSLOT_SWORD.id())), getX() + 45, getY() + 54, 14, 14, 5924);
							}
							graphics.getSurface().drawString("@yel@" + graphics.party.username[0] + "@whi@-" + graphics.party.cbLvl[0], getX() - 20, getY() + 6, 0xffffff, 0);
							graphics.getSurface().drawString("@yel@" + graphics.party.username[1] + "@whi@-" + graphics.party.cbLvl[1], getX() - 20, getY() + 26, 0xffffff, 0);
							graphics.getSurface().drawString("@yel@" + graphics.party.username[2] + "@whi@-" + graphics.party.cbLvl[2], getX() - 20, getY() + 46, 0xffffff, 0);
							graphics.getSurface().drawString("@yel@" + graphics.party.username[3] + "@whi@-" + graphics.party.cbLvl[3], getX() - 20, getY() + 66, 0xffffff, 0);
						} else if (SocialLists.partyListCount == 5) {
							if (graphics.party.pMemD[0] > 0) {
								graphics.party.pMemDTimeout[0] = 500;
							}
							if (graphics.party.pMemD[1] > 0) {
								graphics.party.pMemDTimeout[1] = 500;
							}
							if (graphics.party.pMemD[2] > 0) {
								graphics.party.pMemDTimeout[2] = 500;
							}
							if (graphics.party.pMemD[3] > 0) {
								graphics.party.pMemDTimeout[3] = 500;
							}
							if (graphics.party.pMemD[4] > 0) {
								graphics.party.pMemDTimeout[4] = 500;
							}
							if (graphics.party.pMemDTimeout[0] > 0) {
								graphics.getSurface().drawSprite(graphics.spriteSelect(EntityHandler.GUIparts.get(GUIPARTS.DAMAGETAKEN.id())), getX() + 84, getY() - 6, 14, 14, 5924);
							}
							if (graphics.party.pMemDTimeout[1] > 0) {
								graphics.getSurface().drawSprite(graphics.spriteSelect(EntityHandler.GUIparts.get(GUIPARTS.DAMAGETAKEN.id())), getX() + 84, getY() + 14, 14, 14, 5924);
							}
							if (graphics.party.pMemDTimeout[2] > 0) {
								graphics.getSurface().drawSprite(graphics.spriteSelect(EntityHandler.GUIparts.get(GUIPARTS.DAMAGETAKEN.id())), getX() + 84, getY() + 34, 14, 14, 5924);
							}
							if (graphics.party.pMemDTimeout[3] > 0) {
								graphics.getSurface().drawSprite(graphics.spriteSelect(EntityHandler.GUIparts.get(GUIPARTS.DAMAGETAKEN.id())), getX() + 84, getY() + 54, 14, 14, 5924);
							}
							if (graphics.party.pMemDTimeout[4] > 0) {
								graphics.getSurface().drawSprite(graphics.spriteSelect(EntityHandler.GUIparts.get(GUIPARTS.DAMAGETAKEN.id())), getX() + 84, getY() + 74, 14, 14, 5924);
							}
							int hphphp = 0;
							double p1p1p1 = 0;
							double p2p2p2 = 0;
							hphphp = graphics.party.maxHp[0] - graphics.party.curHp[0];
							p1p1p1 = ((double) hphphp / graphics.party.maxHp[0]);
							p2p2p2 = (p1p1p1 * 100);
							int p3p3p3 = (int) Math.round(p2p2p2); // 3
							graphics.getSurface().drawBox(getX() - 20, getY() + 8, 100, 4, 0xFF0000);
							if (graphics.party.pMemDTimeout[0] < 1) {
								graphics.getSurface().drawBox(getX() - 20, getY() + 8, 100 - p3p3p3, 4, 0x00FF00);
							}
							int hphphphp = 0;
							double p1p1p1p1 = 0;
							double p2p2p2p2 = 0;
							hphphphp = graphics.party.maxHp[1] - graphics.party.curHp[1];
							p1p1p1p1 = ((double) hphphphp / graphics.party.maxHp[1]);
							p2p2p2p2 = (p1p1p1p1 * 100);
							int p3p3p3p3 = (int) Math.round(p2p2p2p2);
							graphics.getSurface().drawBox(getX() - 20, getY() + 28, 100, 4, 0xFF0000);
							if (graphics.party.pMemDTimeout[1] < 1) {
								graphics.getSurface().drawBox(getX() - 20, getY() + 28, 100 - p3p3p3p3, 4, 0x00FF00);
							}
							int hphp = 0;
							double p1p1 = 0;
							double p2p2 = 0;
							hphp = graphics.party.maxHp[2] - graphics.party.curHp[2];
							p1p1 = ((double) hphp / graphics.party.maxHp[2]);
							p2p2 = (p1p1 * 100);
							int p3p3 = (int) Math.round(p2p2);
							graphics.getSurface().drawBox(getX() - 20, getY() + 48, 100, 4, 0xFF0000);
							if (graphics.party.pMemDTimeout[2] < 1) {
								graphics.getSurface().drawBox(getX() - 20, getY() + 48, 100 - p3p3, 4, 0x00FF00);
							}
							int hphphphphphphp = 0;
							double gpgp = 0;
							double gpgp2 = 0;
							hphphphphphphp = graphics.party.maxHp[3] - graphics.party.curHp[3];
							gpgp = ((double) hphphphphphphp / graphics.party.maxHp[3]);
							gpgp2 = (gpgp * 100);
							int gppg = (int) Math.round(gpgp2);
							graphics.getSurface().drawBox(getX() - 20, getY() + 68, 100, 4, 0xFF0000);
							if (graphics.party.pMemDTimeout[3] < 1) {
								graphics.getSurface().drawBox(getX() - 20, getY() + 68, 100 - gppg, 4, 0x00FF00);
							}
							int hphphphphphp1 = 0;
							double pgpggpgp = 0;
							double pgpg22 = 0;
							hphphphphphp1 = graphics.party.maxHp[4] - graphics.party.curHp[4];
							pgpggpgp = ((double) hphphphphphp1 / graphics.party.maxHp[4]);
							pgpg22 = (pgpggpgp * 100);
							int pgpg33 = (int) Math.round(pgpg22);
							graphics.getSurface().drawBox(getX() - 20, getY() + 88, 100, 4, 0xFF0000);
							if (graphics.party.pMemDTimeout[4] < 1) {
								graphics.getSurface().drawBox(getX() - 20, getY() + 88, 100 - pgpg33, 4, 0x00FF00);
							}
							if (graphics.party.partyRank[0] == 1) {
								graphics.getSurface().drawSprite(graphics.spriteSelect(EntityHandler.crowns.get(1)), getX() + 71, getY() - 6, 14, 14, 5924);
							}
							if (graphics.party.partyRank[1] == 1) {
								graphics.getSurface().drawSprite(graphics.spriteSelect(EntityHandler.crowns.get(1)), getX() + 71, getY() + 14, 14, 14, 5924);
							}
							if (graphics.party.partyRank[2] == 1) {
								graphics.getSurface().drawSprite(graphics.spriteSelect(EntityHandler.crowns.get(1)), getX() + 71, getY() + 34, 14, 14, 5924);
							}
							if (graphics.party.partyRank[3] == 1) {
								graphics.getSurface().drawSprite(graphics.spriteSelect(EntityHandler.crowns.get(1)), getX() + 71, getY() + 54, 14, 14, 5924);
							}
							if (graphics.party.partyRank[4] == 1) {
								graphics.getSurface().drawSprite(graphics.spriteSelect(EntityHandler.crowns.get(1)), getX() + 71, getY() + 74, 14, 14, 5924);
							}
							if (graphics.party.skull[0] > 0) {
								graphics.getSurface().drawSprite(graphics.spriteSelect(EntityHandler.GUIparts.get(GUIPARTS.SKULL.id())), getX() + 58, getY() - 6, 14, 14, 5924);
							}
							if (graphics.party.skull[1] > 0) {
								graphics.getSurface().drawSprite(graphics.spriteSelect(EntityHandler.GUIparts.get(GUIPARTS.SKULL.id())), getX() + 58, getY() + 13, 14, 14, 5924);
							}
							if (graphics.party.skull[2] > 0) {
								graphics.getSurface().drawSprite(graphics.spriteSelect(EntityHandler.GUIparts.get(GUIPARTS.SKULL.id())), getX() + 58, getY() + 32, 14, 14, 5924);
							}
							if (graphics.party.skull[3] > 0) {
								graphics.getSurface().drawSprite(graphics.spriteSelect(EntityHandler.GUIparts.get(GUIPARTS.SKULL.id())), getX() + 58, getY() + 51, 14, 14, 5924);
							}
							if (graphics.party.skull[4] > 0) {
								graphics.getSurface().drawSprite(graphics.spriteSelect(EntityHandler.GUIparts.get(GUIPARTS.SKULL.id())), getX() + 58, getY() + 70, 14, 14, 5924);
							}
							if (graphics.party.inCombat[0] > 0) {
								graphics.getSurface().drawSprite(graphics.spriteSelect(EntityHandler.GUIparts.get(GUIPARTS.EQUIPSLOT_SWORD.id())), getX() + 45, getY() - 6, 14, 14, 5924);
							}
							if (graphics.party.inCombat[1] > 0) {
								graphics.getSurface().drawSprite(graphics.spriteSelect(EntityHandler.GUIparts.get(GUIPARTS.EQUIPSLOT_SWORD.id())), getX() + 45, getY() + 14, 14, 14, 5924);
							}
							if (graphics.party.inCombat[2] > 0) {
								graphics.getSurface().drawSprite(graphics.spriteSelect(EntityHandler.GUIparts.get(GUIPARTS.EQUIPSLOT_SWORD.id())), getX() + 45, getY() + 34, 14, 14, 5924);
							}
							if (graphics.party.inCombat[3] > 0) {
								graphics.getSurface().drawSprite(graphics.spriteSelect(EntityHandler.GUIparts.get(GUIPARTS.EQUIPSLOT_SWORD.id())), getX() + 45, getY() + 54, 14, 14, 5924);
							}
							if (graphics.party.inCombat[4] > 0) {
								graphics.getSurface().drawSprite(graphics.spriteSelect(EntityHandler.GUIparts.get(GUIPARTS.EQUIPSLOT_SWORD.id())), getX() + 45, getY() + 74, 14, 14, 5924);
							}
							graphics.getSurface().drawString("@yel@" + graphics.party.username[0] + "@whi@-" + graphics.party.cbLvl[0], getX() - 20, getY() + 6, 0xffffff, 0);
							graphics.getSurface().drawString("@yel@" + graphics.party.username[1] + "@whi@-" + graphics.party.cbLvl[1], getX() - 20, getY() + 26, 0xffffff, 0);
							graphics.getSurface().drawString("@yel@" + graphics.party.username[2] + "@whi@-" + graphics.party.cbLvl[2], getX() - 20, getY() + 46, 0xffffff, 0);
							graphics.getSurface().drawString("@yel@" + graphics.party.username[3] + "@whi@-" + graphics.party.cbLvl[3], getX() - 20, getY() + 66, 0xffffff, 0);
							graphics.getSurface().drawString("@yel@" + graphics.party.username[4] + "@whi@-" + graphics.party.cbLvl[4], getX() - 20, getY() + 86, 0xffffff, 0);
						}
					}
				}
			}
		};
		partyGuiItself.setLocation(25, 20);

		final NComponent headerComponent = new NComponent(graphics);
		headerComponent.setSize(142, 15);
		//headerComponent.setBackground(0, 0, 156);
		headerComponent.setLocation(0, 0);
		headerComponent.setFontColor(0xFFFFFF, 0xFFFFFF);
		headerComponent.setTextCentered(true);
		//headerComponent.setText("Party");
		headerComponent.setInputListener(new InputListener() {
			@Override
			public boolean onMouseDown(int clickX, int clickY, int mButtonDown, int mButtonClick) {

				if (mButtonDown == 2 && partyGuiComponent.isVisible()) {
					int newX = clickX - (headerComponent.getWidth() / 2);
					int newY = clickY - 5;

					int totalCoverageX = newX + partyGuiComponent.getWidth();
					int totalCoverageY = newY + partyGuiComponent.getHeight();

					if (totalCoverageX > graphics.getGameWidth()) {
						newX -= totalCoverageX - graphics.getGameWidth();
					}
					if (totalCoverageY > graphics.getGameHeight()) {
						newY -= totalCoverageY - graphics.getGameHeight();
					}
					if (newX < 0)
						newX = 0;
					if (newX < 0)
						newX = 0;
					partyGuiComponent.setLocation(newX, newY);
					return true;
				}
				return false;
			}
		});

		NComponent menuButton = new NComponent(graphics);
		menuButton.setTextCentered(true);
		menuButton.setText("Party");
		menuButton.setBorderColors(0xFFFFFF, 0x454545);
		menuButton.setBackground(0x454545, 0xFFFFFF, 128);
		menuButton.setFontColor(0xFFFFFF, 0xFF00000);
		menuButton.setTextSize(0);
		menuButton.setLocation(20, 0);
		menuButton.setSize(75, 15);
		menuButton.setInputListener(new InputListener() {
			@Override
			public boolean onMouseDown(int clickX, int clickY, int mButtonDown, int mButtonClick) {
				if (mButtonClick == 1) {
					graphics.party.showPartySetupInterface(graphics.party.inParty());
					graphics.showUiTab = 0;
					return true;
				}
				return false;
			}
		});
		partyGuiComponent.addComponent(menuButton);
		partyGuiComponent.addComponent(headerComponent);
		partyGuiComponent.addComponent(partyGuiItself);
		partyGuiComponent.setVisible(false);

	}

	public void show() {
		partyGuiComponent.setVisible(true);
	}

	public void hide() {
		partyGuiComponent.setVisible(false);
	}

	public void resetPartyGui() {
		partyGuiComponent.setVisible(false);
	}

	public NComponent getComponent() {
		return partyGuiComponent;
	}
}
