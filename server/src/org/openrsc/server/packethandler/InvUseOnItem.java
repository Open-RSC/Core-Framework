package org.openrsc.server.packethandler;

import org.openrsc.server.packethandler.PacketHandler;
import org.openrsc.server.net.Packet;
import org.openrsc.server.entityhandling.EntityHandler;
import org.openrsc.server.util.DataConversions;
import org.openrsc.server.util.Formulae;
import org.openrsc.server.event.MiniEvent;
import org.apache.mina.common.IoSession;
import org.openrsc.server.entityhandling.defs.extras.ItemArrowHeadDef;
import org.openrsc.server.entityhandling.defs.extras.ItemDartTipDef;
import org.openrsc.server.entityhandling.defs.extras.ItemGemDef;
import org.openrsc.server.entityhandling.defs.extras.ItemHerbDef;
import org.openrsc.server.entityhandling.defs.extras.ItemHerbSecond;
import org.openrsc.server.entityhandling.defs.extras.ItemLogCutDef;
import org.openrsc.server.model.InvItem;
import org.openrsc.server.model.MenuHandler;
import org.openrsc.server.model.Player;
import org.openrsc.server.model.World;
public class InvUseOnItem implements PacketHandler {
	private final int UNSTRUNG_SHORTBOW = 277;
	private final int UNSTRUNG_LONGBOW = 276;
	private final int UNSTRUNG_OAK_SHORTBOW = 659;
	private final int UNSTRUNG_OAK_LONGBOW = 658;
	private final int UNSTRUNG_WILLOW_SHORTBOW = 661;
	private final int UNSTRUNG_WILLOW_LONGBOW = 660;
	private final int UNSTRUNG_MAPLE_SHORTBOW = 663;
	private final int UNSTRUNG_MAPLE_LONGBOW = 662;
	private final int UNSTRUNG_YEW_SHORTBOW = 665;
	private final int UNSTRUNG_YEW_LONGBOW = 664;
	private final int UNSTRUNG_MAGIC_SHORTBOW = 667;
	private final int UNSTRUNG_MAGIC_LONGBOW = 666;
	
	public static final int[] potionMixing = {222, 223, 224, 475, 476, 478, 479, 481, 482, 484, 485, 487, 488, 490, 491, 493, 494, 496, 497, 499, 500, 964, 965};
	
	public void handlePacket(Packet p, IoSession session) throws Exception {
		Player player = (Player)session.getAttachment();
		if (player != null) {
			if (player.isBusy()) {
				player.resetPath();
				return;
			}
			player.resetAllExceptDMing();
			InvItem item1 = player.getInventory().get(p.readShort());
			InvItem item2 = player.getInventory().get(p.readShort());
			if (item1 == null || item2 == null)
				return;
			ItemHerbSecond secondDef = null;
			if ((secondDef = EntityHandler.getItemHerbSecond(item1.getID(), item2.getID())) != null && doHerbSecond(player, item1, item2, secondDef))
				return;
			else if ((secondDef = EntityHandler.getItemHerbSecond(item2.getID(), item1.getID())) != null && doHerbSecond(player, item2, item1, secondDef))
				return;
			else if (item1.getID() == 381 && attachFeathers(player, item1, item2))
				return;
			else if (item2.getID() == 381 && attachFeathers(player, item2, item1))
				return;
			else if (item1.getID() == 167 && doCutGem(player, item1, item2))
				return;
			else if (item2.getID() == 167 && doCutGem(player, item2, item1))
				return;
			else if (item1.getID() == 464 && doHerblaw(player, item1, item2))
				return;
			else if (item2.getID() == 464 && doHerblaw(player, item2, item1))
				return;
			else if (item1.getID() == 13 && doLogCut(player, item1, item2))
				return;
			else if (item2.getID() == 13 && doLogCut(player, item2, item1))
				return;
			else if (item1.getID() == 676 && doBowString(player, item1, item2))
				return;
			else if (item2.getID() == 676 && doBowString(player, item2, item1))
				return;
			else if (item1.getID() == 637 && doArrowHeads(player, item1, item2))
				return;
			else if (item2.getID() == 637 && doArrowHeads(player, item2, item1))
				return;
			else if (item1.getID() == 207 && useWool(player, item1, item2))
				return;
			else if (item2.getID() == 207 && useWool(player, item2, item1))
				return;
			else if (item1.getID() == 39 && makeLeather(player, item1, item2))
				return;
			else if (item2.getID() == 39 && makeLeather(player, item2, item1))
				return;
			else if (item1.getID() == 468 && doGrind(player, item1, item2))
				return;
			else if (item2.getID() == 468 && doGrind(player, item2, item1))
				return;
			else if ((item1.getID() == 50 || item1.getID() == 141 || item1.getID() == 342) && useWater(player, item1, item2))
				return;
			else if ((item2.getID() == 50 || item2.getID() == 141 || item2.getID() == 342) && useWater(player, item2, item1))
				return;
			else if (item1.getID() == 621 && doGlassBlowing(player, item1, item2))
				return;
			else if (item2.getID() == 621 && doGlassBlowing(player, item2, item1))
				return;
			else if (item1.getID() == 526 && combineKeys(player, item1, item2))
				return;
			else if (item2.getID() == 526 && combineKeys(player, item2, item1))
				return;
			else if (item2.getID() == 526 && combineKeys(player, item2, item1))
				return;
			else if (item1.getID() == 321 && addTomato(player, item1, item2))
				return;
			else if (item2.getID() == 321 && addTomato(player, item2, item1))
				return;
			else if (item1.getID() == 323 && addCheese(player, item1, item2))
				return;
			else if (item2.getID() == 323 && addCheese(player, item2, item1))
				return;
			else if (item1.getID() == 250 && addPastry(player, item1, item2))
				return;
			else if (item2.getID() == 250 && addPastry(player, item2, item1))
				return;
			else if (item1.getID() == 252 && addCookingApple(player, item1, item2))
				return;
			else if (item2.getID() == 252 && addCookingApple(player, item2, item1))
				return;
			else if (item1.getID() == 132 && addCookedmeat(player, item1, item2))
				return;
			else if (item2.getID() == 132 && addCookedmeat(player, item2, item1))
				return; 
			else if (item1.getID() == 236 && addRedberries(player, item1, item2))
				return;
			else if (item2.getID() == 236 && addRedberries(player, item2, item1))
				return;
			else if (item1.getID() == 239 && addYellowDye(player, item1, item2))
				return;
			else if (item2.getID() == 239 && addYellowDye(player, item2, item1))
				return;
			else if (item1.getID() == 238 && addRedDye(player, item1, item2))
				return;
			else if (item2.getID() == 238 && addRedDye(player, item2, item1))
				return;
			else if (item1.getID() == 272 && addBlueDye(player, item1, item2))
				return;
			else if (item2.getID() == 272 && addBlueDye(player, item2, item1))
				return;
			else if (item1.getID() == 282 && addOrangeDye(player, item1, item2))
				return;
			else if (item2.getID() == 282 && addOrangeDye(player, item2, item1))
				return;
			else if (item1.getID() == 515 && addGreenDye(player, item1, item2))
				return;
			else if (item2.getID() == 515 && addGreenDye(player, item2, item1))
				return;
			else if (item1.getID() == 516 && addPurpleDye(player, item1, item2))
				return;
			else if (item2.getID() == 516 && addPurpleDye(player, item2, item1))
				return;
			else if (item1.getID() == 352 && addAnchoviePizza(player, item1, item2)) // Anchovies
				return;				
			else if (item1.getID() == 325 && addAnchoviePizza(player, item2, item1)) // Plain Pizza
				return;
			else if (item1.getID() == 132 && addMeatPizza(player, item1, item2)) // Cooked Meat
				return;
			else if (item1.getID() == 325 && addMeatPizza(player, item2, item1)) // Plain Pizza
				return;
			else if (item1.getID() == 749 && addPineappleRing(player, item1, item2)) // Pineapple Ring
				return;
			else if (item1.getID() == 325 && addPineappleRing(player, item2, item1)) // Plain Pizza
				return;
			else if (item1.getID() == 748 && slicePineapple(player, item1, item2)) // Pineapple
				return;
			else if (item1.getID() == 13 && slicePineapple(player, item2, item1)) // Knife		
				return;
			else if (item1.getID() == 166 && item2.getID() == 600 || item1.getID() == 600 && item2.getID() == 166)
				lightCandle(player);
			else if(item1.getID() == 176 && item2.getID() == 177 || item1.getID() == 177 && item2.getID() == 176)
				poisonFishFood(player);
			else if(fixMap(player, item1.getID(), item2.getID()))
					return;
			else if (item1.getID() == 337 && item2.getID() == 468 && doGrind(player, item1, item2)) // Chocolate Dust
				return;
			else if (item1.getID() == 588 && item2.getID() == 377 || item1.getID() == 377 && item2.getID() == 588)
				makeOilyFishingRod(player);
			else if (item1.getID() == 457 && item2.getID() == 587 || item1.getID() == 587 && item2.getID() == 457)
				makeBlamishOil(player);
			else if (item1.getID() == 22 && item2.getID() ==  772 || item1.getID() == 772 && item2.getID() ==  22)
				makeChocolateMilk(player);
			else if (item1.getID() == 469 && item2.getID() == 770 || item1.getID() == 770 && item2.getID() == 469)
				makeHangoverCure(player);
			else if(item1.getID() == 13 && item2.getID() == 510 || item1.getID() == 510 && item2.getID() == 13)
				cutDramenStaff(player);
			else if ((item1.getID() == 166 && item2.getID() == 14) || (item2.getID() == 166 && item1.getID() == 14) || (item1.getID() == 166 && item2.getID() == 632) || (item2.getID() == 166 && item1.getID() == 632) || (item1.getID() == 166 && item2.getID() == 633) || (item2.getID() == 166 && item1.getID() == 633) || (item1.getID() == 166 && item2.getID() == 634) || (item2.getID() == 166 && item1.getID() == 634) || (item1.getID() == 166 && item2.getID() == 635) || (item2.getID() == 166 && item1.getID() == 635) || (item1.getID() == 166 && item2.getID() == 636) || (item2.getID() == 166 && item1.getID() == 636))
				player.sendMessage("I think you should put the logs down before you light them!");
			else {
				for (int potionToMix : potionMixing) {
					if (item1.getID() == potionToMix && combinePotions(player, item1, item2))
						return;
				}
				player.sendMessage("Nothing interesting happens");
			}
		}
	}

	
	private void cutDramenStaff(Player owner) {
		if(owner.getMaxStat(12) > 30) {
			owner.sendMessage("You carve the branch into a staff");
			owner.getInventory().remove(new InvItem(510));
			owner.getInventory().add(new InvItem(509));
			owner.sendInventory();			
		} else {
			owner.sendMessage("You need to be at least 31 crafting to carve the Dramen Staff");
		}
	}
	
	private boolean fixMap(Player owner, int piece1, int piece2) {
		if(piece1 == 416 && (piece2 == 417 || piece2 == 418)) {
			if(owner.getInventory().countId(417) > 0 && owner.getInventory().countId(418) > 0) {
				owner.sendMessage("You put the map pieces together");
				owner.getInventory().remove(new InvItem(416, 1));
				owner.getInventory().remove(new InvItem(417, 1));
				owner.getInventory().remove(new InvItem(418, 1));
				owner.getInventory().add(new InvItem(415, 1));
				owner.sendInventory();
				return true;
			}
		} else if(piece1 == 417 && (piece2 == 416 || piece2 == 418)) {
			if(owner.getInventory().countId(416) > 0 && owner.getInventory().countId(418) > 0) {
				owner.sendMessage("You put the map pieces together");
				owner.getInventory().remove(new InvItem(416, 1));
				owner.getInventory().remove(new InvItem(417, 1));
				owner.getInventory().remove(new InvItem(418, 1));
				owner.getInventory().add(new InvItem(415, 1));
				owner.sendInventory();
				return true;
			}
		} else if(piece1 == 418 && (piece2 == 416 || piece2 == 417)) {
			if(owner.getInventory().countId(416) > 0 && owner.getInventory().countId(417) > 0) {
				owner.sendMessage("You put the map pieces together");
				owner.getInventory().remove(new InvItem(416, 1));
				owner.getInventory().remove(new InvItem(417, 1));
				owner.getInventory().remove(new InvItem(418, 1));
				owner.getInventory().add(new InvItem(415, 1));
				owner.sendInventory();
				return true;
			}
		}
		return false;
	}
	
	private void makeBlamishOil(Player owner)
	{
		if (owner.getMaxStat(15) < 24)
		{
			owner.sendMessage("You need a herblaw level of 24 to make this oil");
			return;
		}
		else
		{
			owner.sendMessage("You mix the snail slime with the harralander potion");
			owner.getInventory().remove(new InvItem(457));
			owner.getInventory().remove(new InvItem(587));
			owner.getInventory().add(588, 1);
			owner.sendInventory();		
		}
	}
	
	private void makeOilyFishingRod(Player owner)
	{
		owner.sendMessage("You cover the fishing rod in oil");
		owner.getInventory().remove(new InvItem(377));
		owner.getInventory().remove(new InvItem(588));
		owner.getInventory().add(589, 1);
		owner.sendInventory();
	}
	
	private void lightCandle(Player owner)
	{
		owner.sendMessage("You light the candle");
		owner.getInventory().remove(new InvItem(600));
		owner.getInventory().add(602, 1);
		owner.sendInventory();
	}
	
	private void makeHangoverCure(Player owner)
	{
		owner.sendMessage("You mix the snape grass with the chocolaty milk");
		owner.getInventory().remove(new InvItem(469));
		owner.getInventory().remove(new InvItem(770));
		owner.getInventory().add(new InvItem(771));
		owner.sendInventory();	
	}
	
	private void makeChocolateMilk(Player owner)
	{
		owner.sendMessage("You mix the chocolate dust with the milk");
		owner.getInventory().remove(new InvItem(772));
		owner.getInventory().remove(new InvItem(22));
		owner.getInventory().add(new InvItem(770));
		owner.sendInventory();	
	}
	
	private void poisonFishFood(Player owner) {
		owner.sendMessage("You poison the fish food");
		owner.getInventory().remove(new InvItem(176));
		owner.getInventory().remove(new InvItem(177));
		owner.getInventory().add(new InvItem(178));
		owner.sendInventory();
	}

	private boolean slicePineapple(Player player, final InvItem Pineapple, final InvItem Knife) {
		if (Pineapple.getID() != 748)
			return false;
		else if (Knife.getID() != 13)
			return false;
		else if (player.getInventory().remove(Pineapple) > -1) {
			player.sendMessage("You slice the Pineapple into a Pineapple Ring");
			player.getInventory().add(new InvItem(749, 1));
			player.sendInventory();
		}
		return true;
	}	
	
	private boolean addAnchoviePizza(Player player, final InvItem Anchovies, final InvItem PlainPizza) {
		if (Anchovies.getID() != 352)
			return false;
		else if (PlainPizza.getID() != 325)
			return false;
		else if (player.getInventory().remove(Anchovies) > -1 && player.getInventory().remove(PlainPizza) > -1) {
			player.sendMessage("You add the Anchovies to the Plain Pizza");
			player.getInventory().add(new InvItem(327, 1));
			player.sendInventory();
		}
		return true;
	}
	
	private boolean addMeatPizza(Player player, final InvItem CookedMeat, final InvItem PlainPizza) {
		if (CookedMeat.getID() != 132)
			return false;
		else if (PlainPizza.getID() != 325)
			return false;
		else if (player.getInventory().remove(CookedMeat) > -1 && player.getInventory().remove(PlainPizza) > -1) {
			player.sendMessage("You add the Cooked Meat to the Plain Pizza");
			player.getInventory().add(new InvItem(326, 1));
			player.sendInventory();
		}
		return true;
	}

	private boolean addPineappleRing(Player player, final InvItem PineappleRing, final InvItem PlainPizza) {
		if (PineappleRing.getID() != 749)
			return false;
		else if (PlainPizza.getID() != 325)
			return false;
		else if (player.getInventory().remove(PineappleRing) > -1 && player.getInventory().remove(PlainPizza) > -1) {
			player.sendMessage("You add the Pineapple Ring to the Plain Pizza");
			player.getInventory().add(new InvItem(750, 1));
			player.sendInventory();
		}
		return true;
	}	
	
	private boolean combinePotions(Player player, final InvItem firstPotion, final InvItem secondPotion) {
		boolean success = false;
		switch (firstPotion.getID()) {
			case 223:
				switch (secondPotion.getID()) {
					case 224: //mix 3 dose str pot from 2 dose + 1 dose
						if (player.getInventory().remove(firstPotion) > -1 && player.getInventory().remove(secondPotion) > -1) {
							player.sendMessage("You combine the potions.");
							player.getInventory().add(new InvItem(222, 1));
							player.getInventory().add(new InvItem(465, 1));
							player.sendInventory();
							success = true;
							break;
						}
				}
				break;
			case 224:
				switch (secondPotion.getID()) {
					case 223: //mix 3 dose from 1 dose + 2 dose
						if (player.getInventory().remove(firstPotion) > -1 && player.getInventory().remove(secondPotion) > -1) {
							player.sendMessage("You combine the potions.");
							player.getInventory().add(new InvItem(222, 1));
							player.getInventory().add(new InvItem(465, 1));
							player.sendInventory();
							success = true;
							break;	
						}
					case 224: // mix 2 dose from 1+1
						if (player.getInventory().remove(firstPotion) > -1 && player.getInventory().remove(secondPotion) > -1) {
							player.sendMessage("You combine the potions.");
							player.getInventory().add(new InvItem(223, 1));
							player.getInventory().add(new InvItem(465, 1));
							player.sendInventory();
							success = true;
							break;	
						}
				}
				break;				
			case 475:
				switch (secondPotion.getID()) {
					case 476: //mix 3 dose from 2 dose + 1 dose
						if (player.getInventory().remove(firstPotion) > -1 && player.getInventory().remove(secondPotion) > -1) {
							player.sendMessage("You combine the potions.");
							player.getInventory().add(new InvItem(474, 1));
							player.getInventory().add(new InvItem(465, 1));
							player.sendInventory();
						}	
						success = true;
						break;						
				}	
				break;				
			case 476:
				switch (secondPotion.getID()) {
					case 475: //mix 3 dose from 1 dose + 2 dose
						if (player.getInventory().remove(firstPotion) > -1 && player.getInventory().remove(secondPotion) > -1) {
							player.sendMessage("You combine the potions.");
							player.getInventory().add(new InvItem(474, 1));
							player.getInventory().add(new InvItem(465, 1));
							player.sendInventory();
						}	
						success = true;
						break;						
					case 476: //mix 2 dose from 1 dose + 1 dose
						if (player.getInventory().remove(firstPotion) > -1 && player.getInventory().remove(secondPotion) > -1) {
							player.sendMessage("You combine the potions.");
							player.getInventory().add(new InvItem(475, 1));
							player.getInventory().add(new InvItem(465, 1));
							player.sendInventory();
						}	
						success = true;
						break;						
				}
				break;
			case 478:
				switch (secondPotion.getID()) {
					case 479: //mix 3 dose from 2 dose + 1 dose
						if (player.getInventory().remove(firstPotion) > -1 && player.getInventory().remove(secondPotion) > -1) {
							player.sendMessage("You combine the potions.");
							player.getInventory().add(new InvItem(477, 1));
							player.getInventory().add(new InvItem(465, 1));
							player.sendInventory();
						}	
						success = true;
						break;						
				}
				break;				
			case 479:
				switch (secondPotion.getID()) {
					case 478: //mix 3 dose from 1 dose + 2 dose
						if (player.getInventory().remove(firstPotion) > -1 && player.getInventory().remove(secondPotion) > -1) {
							player.sendMessage("You combine the potions.");
							player.getInventory().add(new InvItem(477, 1));
							player.getInventory().add(new InvItem(465, 1));
							player.sendInventory();
						}	
						success = true;
						break;						
					case 479: //mix 2 dose from 1 + 1
						if (player.getInventory().remove(firstPotion) > -1 && player.getInventory().remove(secondPotion) > -1) {
							player.sendMessage("You combine the potions.");
							player.getInventory().add(new InvItem(478, 1));
							player.getInventory().add(new InvItem(465, 1));
							player.sendInventory();
						}	
						success = true;
						break;						
				}
				break;
			case 481:
				switch (secondPotion.getID()) {
					case 482: // mix 3 from 2+1
						if (player.getInventory().remove(firstPotion) > -1 && player.getInventory().remove(secondPotion) > -1) {
							player.sendMessage("You combine the potions.");
							player.getInventory().add(new InvItem(480, 1));
							player.getInventory().add(new InvItem(465, 1));
							player.sendInventory();
						}	
						success = true;
						break;						
				}
				break;
			case 482:
				switch (secondPotion.getID()) {
					case 481: //mix 3 from 1+2
						if (player.getInventory().remove(firstPotion) > -1 && player.getInventory().remove(secondPotion) > -1) {
							player.sendMessage("You combine the potions.");
							player.getInventory().add(new InvItem(480, 1));
							player.getInventory().add(new InvItem(465, 1));
							player.sendInventory();
						}	
						success = true;
						break;						
					case 482: //mix 2 from 1+1
						if (player.getInventory().remove(firstPotion) > -1 && player.getInventory().remove(secondPotion) > -1) {
							player.sendMessage("You combine the potions.");
							player.getInventory().add(new InvItem(481, 1));
							player.getInventory().add(new InvItem(465, 1));
							player.sendInventory();
						}	
						success = true;
						break;						
				}
				break;
			case 484:
				switch (secondPotion.getID()) {
					case 485: //3 from 2+1
						if (player.getInventory().remove(firstPotion) > -1 && player.getInventory().remove(secondPotion) > -1) {
							player.sendMessage("You combine the potions.");
							player.getInventory().add(new InvItem(483, 1));
							player.getInventory().add(new InvItem(465, 1));
							player.sendInventory();
						}	
						success = true;
						break;						
				}
				break;
			case 485:
					switch (secondPotion.getID()) {
						case 484: //3 from 1+2
							if (player.getInventory().remove(firstPotion) > -1 && player.getInventory().remove(secondPotion) > -1) {
								player.sendMessage("You combine the potions.");
								player.getInventory().add(new InvItem(483, 1));
								player.getInventory().add(new InvItem(465, 1));
								player.sendInventory();
							}	
							success = true;
							break;						
						case 485: //2 from 1+1
							if (player.getInventory().remove(firstPotion) > -1 && player.getInventory().remove(secondPotion) > -1) {
								player.sendMessage("You combine the potions.");
								player.getInventory().add(new InvItem(484, 1));
								player.getInventory().add(new InvItem(465, 1));
								player.sendInventory();
							}	
							success = true;
							break;						
					}
				break;				
			case 487:
				switch (secondPotion.getID()) {
					case 488: //3 from 1+2
						if (player.getInventory().remove(firstPotion) > -1 && player.getInventory().remove(secondPotion) > -1) {
							player.sendMessage("You combine the potions.");
							player.getInventory().add(new InvItem(486, 1));
							player.getInventory().add(new InvItem(465, 1));
							player.sendInventory();
						}	
						success = true;
						break;						
				}
				break;				
			case 488:
				switch (secondPotion.getID()) {
					case 487: //3 from 2+1
						if (player.getInventory().remove(firstPotion) > -1 && player.getInventory().remove(secondPotion) > -1) {
							player.sendMessage("You combine the potions.");
							player.getInventory().add(new InvItem(486, 1));
							player.getInventory().add(new InvItem(465, 1));
							player.sendInventory();
						}	
						success = true;
						break;						
					case 488: //2 from 1+1
						if (player.getInventory().remove(firstPotion) > -1 && player.getInventory().remove(secondPotion) > -1) {
							player.sendMessage("You combine the potions.");
							player.getInventory().add(new InvItem(487, 1));
							player.getInventory().add(new InvItem(465, 1));
							player.sendInventory();
						}	
						success = true;
						break;						
				}
				break;				
			case 490:
				switch (secondPotion.getID()) {
					case 491: //3 from 2+1
						if (player.getInventory().remove(firstPotion) > -1 && player.getInventory().remove(secondPotion) > -1) {
							player.sendMessage("You combine the potions.");
							player.getInventory().add(new InvItem(489, 1));
							player.getInventory().add(new InvItem(465, 1));
							player.sendInventory();
						}	
						success = true;
						break;						
				}
				break;				
			case 491:
				switch (secondPotion.getID()) {
					case 490: //3 from 1+2
						if (player.getInventory().remove(firstPotion) > -1 && player.getInventory().remove(secondPotion) > -1) {
							player.sendMessage("You combine the potions.");
							player.getInventory().add(new InvItem(489, 1));
							player.getInventory().add(new InvItem(465, 1));
							player.sendInventory();
						}	
						success = true;
						break;						
					case 491: //2 from 1+1
						if (player.getInventory().remove(firstPotion) > -1 && player.getInventory().remove(secondPotion) > -1) {
							player.sendMessage("You combine the potions.");
							player.getInventory().add(new InvItem(490, 1));
							player.getInventory().add(new InvItem(465, 1));
							player.sendInventory();
						}	
						success = true;
						break;						
				}
				break;				
			case 493:
				switch (secondPotion.getID()) {
					case 494: //3 from 2+1
						if (player.getInventory().remove(firstPotion) > -1 && player.getInventory().remove(secondPotion) > -1) {
							player.sendMessage("You combine the potions.");
							player.getInventory().add(new InvItem(492, 1));
							player.getInventory().add(new InvItem(465, 1));
							player.sendInventory();
						}	
						success = true;
						break;						
				}
				break;				
			case 494:
				switch (secondPotion.getID()) {
					case 493: //3 from 1+2
						if (player.getInventory().remove(firstPotion) > -1 && player.getInventory().remove(secondPotion) > -1) {
							player.sendMessage("You combine the potions.");
							player.getInventory().add(new InvItem(492, 1));
							player.getInventory().add(new InvItem(465, 1));
							player.sendInventory();
						}	
						success = true;
						break;						
					case 494: //2 from 1+1
						if(player.getInventory().remove(firstPotion) > -1 && player.getInventory().remove(secondPotion) > -1) {
							player.sendMessage("You combine the potions.");
							player.getInventory().add(new InvItem(493, 1));
							player.getInventory().add(new InvItem(465, 1));
							player.sendInventory();
						}	
						success = true;
						break;						
				}
				break;				
			case 496:
				switch (secondPotion.getID()) {
					case 497: //3 from 2+1
						if (player.getInventory().remove(firstPotion) > -1 && player.getInventory().remove(secondPotion) > -1) {
							player.sendMessage("You combine the potions.");
							player.getInventory().add(new InvItem(495, 1));
							player.getInventory().add(new InvItem(465, 1));
							player.sendInventory();
						}	
						success = true;
						break;						
				}
				break;				
			case 497:
				switch (secondPotion.getID()) {
					case 496: // 3 from 1+2
						if (player.getInventory().remove(firstPotion) > -1 && player.getInventory().remove(secondPotion) > -1) {
							player.sendMessage("You combine the potions.");
							player.getInventory().add(new InvItem(495, 1));
							player.getInventory().add(new InvItem(465, 1));
							player.sendInventory();
						}	
						success = true;
						break;						
					case 497: //2 from 1+1
						if (player.getInventory().remove(firstPotion) > -1 && player.getInventory().remove(secondPotion) > -1) {
							player.sendMessage("You combine the potions.");
							player.getInventory().add(new InvItem(496, 1));
							player.getInventory().add(new InvItem(465, 1));
							player.sendInventory();
						}	
						success = true;
						break;						
				}
				break;				
			case 499:
				switch (secondPotion.getID()) {
					case 500: //3 from 2+1
						if (player.getInventory().remove(firstPotion) > -1 && player.getInventory().remove(secondPotion) > -1) {
							player.sendMessage("You combine the potions.");
							player.getInventory().add(new InvItem(498, 1));
							player.getInventory().add(new InvItem(465, 1));
							player.sendInventory();
						}	
						success = true;
						break;						
				}
				break;				
			case 500:
				switch (secondPotion.getID()) {
					case 499: // 3 from 1+2
						if (player.getInventory().remove(firstPotion) > -1 && player.getInventory().remove(secondPotion) > -1) {
							player.sendMessage("You combine the potions.");
							player.getInventory().add(new InvItem(498, 1));
							player.getInventory().add(new InvItem(465, 1));
							player.sendInventory();
						}	
						success = true;
						break;						
					case 500: //2 from 1+1
						if (player.getInventory().remove(firstPotion) > -1 && player.getInventory().remove(secondPotion) > -1) {
							player.sendMessage("You combine the potions.");
							player.getInventory().add(new InvItem(499, 1));
							player.getInventory().add(new InvItem(465, 1));
							player.sendInventory();
						}	
						success = true;
						break;						
				}
				break;	
				/*		963: Potion of Zamorak
						964: Potion of Zamorak
						965: Potion of Zamorak*/
			case 964:
				switch (secondPotion.getID()) {
					case 965: //2 from 1+1
						if (player.getInventory().remove(firstPotion) > -1 && player.getInventory().remove(secondPotion) > -1) {
							player.sendMessage("You combine the potions.");
							player.getInventory().add(new InvItem(963, 1));
							player.getInventory().add(new InvItem(465, 1));
							player.sendInventory();
						}
						success = true;
						break;		
				}
				break;
			case 965:
				switch (secondPotion.getID()) {
					case 964: //2 from 1+1
						if (player.getInventory().remove(firstPotion) > -1 && player.getInventory().remove(secondPotion) > -1) {
							player.sendMessage("You combine the potions.");
							player.getInventory().add(new InvItem(963, 1));
							player.getInventory().add(new InvItem(465, 1));
							player.sendInventory();
						}	
						success = true;
						break;	
					case 965: //2 from 1+1
						if (player.getInventory().remove(firstPotion) > -1 && player.getInventory().remove(secondPotion) > -1) {
							player.sendMessage("You combine the potions.");
							player.getInventory().add(new InvItem(964, 1));
							player.getInventory().add(new InvItem(465, 1));
							player.sendInventory();
						}	
						success = true;
						break;	
				}
		}
		return success;
	}
	
	private boolean addYellowDye(Player player, final InvItem Dye, final InvItem Cape) {
		int newID;
		switch (Cape.getID()) {
			case 183: // Red Cape
				newID = 512;
				break;
			case 209: // Black Cape
				newID = 512;
				break;
			case 229: // Blue Cape
				newID = 512;
				break;
			case 511: // Green Cape
				newID = 512;
				break;
			case 513: // Orange Cape
				newID = 512;
				break;
			case 514: // Purple Cape
				newID = 512;
				break;
			case 272: // Blue Dye
				newID = 515;
				break;
			case 238: // Red Dye
				newID = 282;
				break;
			case 245: //Wig
				newID = 244;
				break;
			default:
				return false;
		}
		if (player.getInventory().remove(Cape) > -1 && player.getInventory().remove(Dye) > -1) {
			player.sendMessage("You add the dye to the " + Cape.getDef().getName());
			player.getInventory().add(new InvItem(newID, 1));
			player.sendInventory();
		}
		return true;
	}

	private boolean addRedDye(Player player, final InvItem Dye, final InvItem Cape) {
		int newID;
		switch (Cape.getID()) {
			case 512: // Yellow Cape
				newID = 183;
				break;
			case 209: // Black Cape
				newID = 183;
				break;
			case 229: // Blue Cape
				newID = 183;
				break;
			case 511: // Green Cape
				newID = 183;
				break;
			case 513: // Orange Cape
				newID = 183;
				break;
			case 514: // Purple Cape
				newID = 183;
				break;
			case 272: // Blue Dye
				newID = 516;
				break;
			default:
				return false;
		}
		if (player.getInventory().remove(Cape) > -1 && player.getInventory().remove(Dye) > -1) {
			player.sendMessage("You add the Dye to the " + Cape.getDef().getName());
			player.getInventory().add(new InvItem(newID, 1));
			player.sendInventory();
		}
		return true;
	}

	private boolean addBlueDye(Player player, final InvItem Dye, final InvItem Cape) {
		int newID;
		switch (Cape.getID()) {
			case 183: // Red Cape
				newID = 229;
				break;
			case 209: // Black Cape
				newID = 229;
				break;
			case 512: // Yellow Cape
				newID = 229;
				break;
			case 511: // Green Cape
				newID = 229;
				break;
			case 513: // Orange Cape
				newID = 229;
				break;
			case 514: // Purple Cape
				newID = 229;
				break;
			case 273: // Goblin Armour
				newID = 275;
				break;
			default:
				return false;
		}
		if (player.getInventory().remove(Cape) > -1 && player.getInventory().remove(Dye) > -1) {
			player.sendMessage("You add the Dye to the " + Cape.getDef().getName());
			player.getInventory().add(new InvItem(newID, 1));
			player.sendInventory();
		}
		return true;
	}

	private boolean addOrangeDye(Player player, final InvItem Dye, final InvItem Cape) {
		int newID;
		switch (Cape.getID()) {
			case 183: // Red Cape
				newID = 513;
				break;
			case 209: // Black Cape
				newID = 513;
				break;
			case 229: // Blue Cape
				newID = 513;
				break;
			case 511: // Green Cape
				newID = 513;
				break;
			case 512: // Yellow Cape
				newID = 513;
				break;
			case 514: // Purple Cape
				newID = 513;
				break;
			case 273: // Goblin Armour
				newID = 274;
				break;
			default:
				return false;
		}
		if (player.getInventory().remove(Cape) > -1 && player.getInventory().remove(Dye) > -1) {
			player.sendMessage("You add the Dye to the " + Cape.getDef().getName());
			player.getInventory().add(new InvItem(newID, 1));
			player.sendInventory();
		}
		return true;
	}

	private boolean addGreenDye(Player player, final InvItem Dye, final InvItem Cape) {
		int newID;
		switch (Cape.getID()) {
			case 183: // Red Cape
				newID = 511;
				break;
			case 209: // Black Cape
				newID = 511;
				break;
			case 229: // Blue Cape
				newID = 511;
				break;
			case 512: // Yellow Cape
				newID = 511;
				break;
			case 513: // Orange Cape
				newID = 511;
				break;
			case 514: // Purple Cape
				newID = 511;
				break;
			default:
				return false;
		}
		if (player.getInventory().remove(Cape) > -1 && player.getInventory().remove(Dye) > -1) {
			player.sendMessage("You add the dye to the Cape");
			player.getInventory().add(new InvItem(newID, 1));
			player.sendInventory();
		}
		return true;
	}

	private boolean addPurpleDye(Player player, final InvItem Dye, final InvItem Cape) {
		int newID;
		switch (Cape.getID()) {
			case 183: // Red Cape
				newID = 514;
				break;
			case 209: // Black Cape
				newID = 514;
				break;
			case 229: // Blue Cape
				newID = 514;
				break;
			case 511: // Green Cape
				newID = 514;
				break;
			case 513: // Orange Cape
				newID = 514;
				break;
			case 512: // Yellow Cape
				newID = 514;
				break;
			default:
				return false;
		}
		if (player.getInventory().remove(Cape) > -1 && player.getInventory().remove(Dye) > -1) {
			player.sendMessage("You add the dye to the " + Cape.getDef().getName());
			player.getInventory().add(new InvItem(newID, 1));
			player.sendInventory();
		}
		return true;
	}

	private boolean addCookingApple(Player player, final InvItem CookingApple, final InvItem Shell) {
		if (CookingApple.getID() != 252)
			return false;
		else if (Shell.getID() != 253)
			return false;
		else if (player.getInventory().remove(CookingApple) > -1 && player.getInventory().remove(Shell) > -1) {
			player.sendMessage("You add the cooking apple to the pie shell.");
			player.getInventory().add(new InvItem(254, 1));
			player.sendInventory();
		}
		return true;
	}

	private boolean addCookedmeat(Player player, final InvItem Cookedmeat, final InvItem Shell) {
		if (Cookedmeat.getID() != 132)
			return false;
		else if (Shell.getID() != 253)
			return false;
		else if (player.getInventory().remove(Cookedmeat) > -1 && player.getInventory().remove(Shell) > -1) {
			player.sendMessage("You add the cooked meat to the pie shell.");
			player.getInventory().add(new InvItem(255, 1));
			player.sendInventory();
		}
		return true;
	}

	private boolean addRedberries(Player player, final InvItem Redberries, final InvItem Shell) {
		if (Redberries.getID() != 236)
			return false;
		else if (Shell.getID() != 253)
			return false;
		else if (player.getInventory().remove(Redberries) > -1 && player.getInventory().remove(Shell) > -1) {
			player.sendMessage("You add the redberries to the pie shell.");
			player.getInventory().add(new InvItem(256, 1));
			player.sendInventory();
		}
		return true;
	}

	private boolean addPastry(Player player, final InvItem Pastry, final InvItem Dish) {
		if (Pastry.getID() != 250)
			return false;
		else if (Dish.getID() != 251)
			return false;
		else if (player.getInventory().remove(Pastry) > -1 && player.getInventory().remove(Dish) > -1) {
			player.sendMessage("You put the pastry dough inside the pie dish.");
			player.getInventory().add(new InvItem(253, 1));
			player.sendInventory();
		}
		return true;
	}

	private boolean addTomato(Player player, final InvItem Base, final InvItem Tomato) {
		if (Tomato.getID() != 320)
			return false;
		else if (Base.getID() != 321)
			return false;
		else if(player.getInventory().remove(Base) > -1 && player.getInventory().remove(Tomato) > -1) {
			player.sendMessage("You add the Tomato to the Pizza Base");
			player.getInventory().add(new InvItem(323, 1));
			player.sendInventory();
		}
		return true;
	}

	private boolean addCheese(Player player, final InvItem UnfinishedPizza, final InvItem Cheese) {
		if (Cheese.getID() != 319)
			return false;
		else if (UnfinishedPizza.getID() != 323)
			return false;
		else if (player.getInventory().remove(UnfinishedPizza) > -1 && player.getInventory().remove(Cheese) > -1) {
			player.sendMessage("You add the Cheese to the Incomplete Pizza");
			player.getInventory().add(new InvItem(324, 1));
			player.sendInventory();
		}
		return true;
	}
	
	private boolean combineKeys(Player player, final InvItem firstHalf, final InvItem secondHalf) {
		if (secondHalf.getID() != 527)
			return false;
		if (player.getInventory().remove(firstHalf) > -1 && player.getInventory().remove(secondHalf) > -1) {
			player.sendMessage("You combine the key halves to make a crystal key.");
			player.getInventory().add(new InvItem(525, 1));
			player.sendInventory();
		}
		return true;
	}
	
	private boolean doGlassBlowing(Player player, final InvItem pipe, final InvItem glass) {
		if (glass.getID() != 623)
			return false;
	
		World.getDelayedEventHandler().add(new MiniEvent(player) {
			public void action() {
				String[] options = new String[]{"Beer Glass", "Vial", "Orb"};
				owner.setMenuHandler(new MenuHandler(options) {
					public void handleReply(final int option, final String reply) {
						InvItem result;
						int reqLvl, exp;
						switch (option) {
							case 0:
								result = new InvItem(620, 1);
								reqLvl = 1;
								exp = 18;
								break;
							case 1:
								result = new InvItem(465, 1);
								reqLvl = 33;
								exp = 35;
								break;
							case 2:
								result = new InvItem(611, 1);
								reqLvl = 46;
								exp = 53;
								break;
							default:
								return;
						}
						if (owner.getCurStat(12) < reqLvl) {
							owner.sendMessage("You need a crafting level of " + reqLvl + " to make a " + result.getDef().getName() + ".");
      						return;
						}
						if (owner.getInventory().remove(glass) > -1) {
							owner.sendMessage("You make a " + result.getDef().getName());
							owner.getInventory().add(result);
							owner.increaseXP(12, exp, 1);
							owner.sendStat(12);
							owner.sendInventory();
						}
					}
				});
				owner.sendMenu(options);
			}
		});
		return true;
	}
	
	private boolean useWater(Player player, final InvItem water, final InvItem item) {
		final int jugID = Formulae.getEmptyJug(water.getID());
		if (jugID == -1) // This shouldn't happen
			return false;
			
		switch (item.getID()) {
			case 149: // Clay
				if (player.getInventory().remove(water) > -1 && player.getInventory().remove(item) > -1) {
					player.sendMessage("You soften the clay.");
					player.getInventory().add(new InvItem(jugID, 1));
					player.getInventory().add(new InvItem(243, 1));
					player.sendInventory();
				}
				break;
			case 23:
			case 136:
				World.getDelayedEventHandler().add(new MiniEvent(player) {
					public void action() {
						String[] options = new String[]{"Pizza Base", "Pastry Dough", "Bread Dough"};
						owner.setMenuHandler(new MenuHandler(options) {
							public void handleReply(final int option, final String reply) {
								InvItem result;
								switch (option) {
									case 0: // Pizza Base
										result = new InvItem(321, 1);
										break;
									case 1: // Pastry Dough
										result = new InvItem(250, 1);
										break;
									case 2: // Bread Dough
										result = new InvItem(137, 1);
									break;
									default:
										return;
								}
								if (owner.getInventory().remove(water) > -1 && owner.getInventory().remove(item) > -1) {
									owner.getInventory().add(result);
									owner.getInventory().add(new InvItem(jugID, 1));
									owner.sendMessage("You make a " + result.getDef().getName());
									owner.sendInventory();
								}
							}
						});
						owner.sendMenu(options);
					}
				});
				break;				
			default:
				return false;
		}
		return true;
	}
	
	private boolean doGrind(Player player, final InvItem mortar, final InvItem item) {
		int newID;
		switch (item.getID()) {
			case 337:
				newID = 772;
			break;
			case 466: // Unicorn Horn
				newID = 473;
			break;
			case 467: // Blue dragon scale
				newID = 472;
				break;
			default:
				return false;
		}
		if (player.getInventory().remove(item) > -1) {
			player.sendMessage("You grind up the " + item.getDef().getName());
			player.getInventory().add(new InvItem(newID, 1));
			player.sendInventory();
		}
		return true;
	}
	
	private boolean doHerbSecond(Player player, final InvItem second, final InvItem unfinished, final ItemHerbSecond def) {
		if (unfinished.getID() != def.getUnfinishedID())
			return false;
		if (player.getCurStat(15) < def.getReqLevel()) {
			player.sendMessage("You need a herblaw level of " + def.getReqLevel() + " to mix those");
			return true;
		}
		if (player.getInventory().remove(second) > -1 && player.getInventory().remove(unfinished) > -1) {
			player.sendMessage("You mix the " + second.getDef().getName() + " with the " + unfinished.getDef().getName());
			player.getInventory().add(new InvItem(def.getPotionID(), 1));
			player.increaseXP(15, def.getExp(), 1);
			player.sendStat(15);
			player.sendInventory();
		}
		return true;
	}
	
	private boolean makeLeather(Player player, final InvItem needle, final InvItem leather) {
		if (leather.getID() != 148)
			return false;
		if (player.getInventory().countId(43) < 1) {
			player.sendMessage("You need some thread to make anything out of leather");
			return true;
		}
		if (DataConversions.random(0, 5) == 0)
			player.getInventory().remove(43, 1);
			
		World.getDelayedEventHandler().add(new MiniEvent(player) {
			public void action() {
				String[] options = new String[]{"Armour", "Gloves", "Boots", "Cancel"};
				owner.setMenuHandler(new MenuHandler(options) {
					public void handleReply(final int option, final String reply) {
						InvItem result;
						int reqLvl, exp;
						switch (option) {
							case 0:
								result = new InvItem(15, 1);
								reqLvl = 14;
								exp = 25;
								break;
							case 1:
								result = new InvItem(16, 1);
								reqLvl = 1;
								exp = 14;
								break;
							case 2:
								result = new InvItem(17, 1);
								reqLvl = 7;
								exp = 17;
								break;
							default:
								return;
						}
						if (owner.getCurStat(12) < reqLvl) {
							owner.sendMessage("You need a crafting level of " + reqLvl + " to make " + result.getDef().getName() + ".");
      						return;
						}
						if (owner.getInventory().remove(leather) > -1) {
							owner.sendMessage("You make some " + result.getDef().getName());
							owner.getInventory().add(result);
							owner.increaseXP(12, exp, 1);
							owner.sendStat(12);
							owner.sendInventory();
						}
					}
				});
				owner.sendMenu(options);
			}
		});
		return true;
	}
	
	private boolean useWool(Player player, final InvItem woolBall, final InvItem item) {
		int newID;
		switch (item.getID()) {
			case 44: // Holy Symbol of Saradomin
				newID = 45;
				break;
			case 1027: // Unholy Symbol of Zamorak
				newID = 1028;
				break;
			case 296: // Gold Amulet
				newID = 301;
				break;
			case 297: // Sapphire Amulet
				newID = 302;
				break;
			case 298: // Emerald Amulet
				newID = 303;
				break;
			case 299: // Ruby Amulet
				newID = 304;
				break;
			case 300: // Diamond Amulet
				newID = 305;
				break;
			case 524: // Dragonstone Amulet
				newID = 610;
				break;
			default:
				return false;
		}
		final int newId = newID;
		World.getDelayedEventHandler().add(new MiniEvent(player) {
			public void action() {
				if (owner.getInventory().remove(woolBall) > -1 && owner.getInventory().remove(item) > -1) {
					owner.sendMessage("You string the amulet");
					owner.getInventory().add(new InvItem(newId, 1));
					owner.sendInventory();
				}
			}
		});
		return true;
	}
	
	private boolean attachFeathers(Player player, final InvItem feathers, final InvItem item) {
		long amount = 10;
		
      	if (feathers.getAmount() < amount)
      		amount = feathers.getAmount();
      	if (item.getAmount() < amount)
      		amount = item.getAmount();
      	InvItem newItem;
      	long exp;
      	ItemDartTipDef tipDef = null;
      	if (item.getID() == 280) {
      		newItem = new InvItem(637, amount);
      		exp = amount;
      	} else if ((tipDef = EntityHandler.getItemDartTipDef(item.getID())) != null) {
      		newItem = new InvItem(tipDef.getDartID(), amount);
      		exp = (int)(tipDef.getExp() * (double)amount);
      	} else
			return false;
      	final long amt = amount;
      	final long xp = exp;
      	final InvItem newItm = newItem;
		World.getDelayedEventHandler().add(new MiniEvent(player) {
			public void action() {
				if (owner.getInventory().remove(feathers.getID(), amt) > -1 && owner.getInventory().remove(item.getID(), amt) > -1) {
					owner.sendMessage("You attach the feathers to the " + item.getDef().getName());
					owner.getInventory().add(newItm);
					owner.increaseXP(9, xp, 1);
					owner.sendStat(9);
					owner.sendInventory();
				}
			}
		});
		return true;
	}
	
	private boolean doCutGem(Player player, final InvItem chisel, final InvItem gem) {
		final ItemGemDef gemDef = EntityHandler.getItemGemDef(gem.getID());
		if (gemDef == null)
      		return false;
		if (player.getCurStat(12) < gemDef.getReqLevel()) {
			player.sendMessage("You need a crafting level of " + gemDef.getReqLevel() + " to cut this gem");
      		return true;
		}
		World.getDelayedEventHandler().add(new MiniEvent(player) {
			public void action() {
				if (owner.getInventory().remove(gem) > -1) {
					InvItem cutGem = new InvItem(gemDef.getGemID(), 1);
					owner.sendMessage("You cut the " + cutGem.getDef().getName());
					owner.sendSound("chisel", false);
					owner.getInventory().add(cutGem);
					owner.increaseXP(12, gemDef.getExp(), 1);
					owner.sendStat(12);
					owner.sendInventory();
				}
			}
		});
		return true;
	}
	
	private boolean doArrowHeads(Player player, final InvItem headlessArrows, final InvItem arrowHeads) {
		final ItemArrowHeadDef headDef = EntityHandler.getItemArrowHeadDef(arrowHeads.getID());
		if (headDef == null)
      		return false;
		
		if (player.getCurStat(9) < headDef.getReqLevel()) {
			player.sendMessage("You need a fletching level of " + headDef.getReqLevel() + " to attach those.");
      		return true;
		}
		long amount = 10;
		if (headlessArrows.getAmount() < amount)
			amount = headlessArrows.getAmount();
		if (arrowHeads.getAmount() < amount)
			amount = arrowHeads.getAmount();
		final long amt = amount;
		World.getDelayedEventHandler().add(new MiniEvent(player) {
			public void action() {
				if (owner.getInventory().remove(headlessArrows.getID(), amt) > -1 && owner.getInventory().remove(arrowHeads.getID(), amt) > -1) {
					owner.sendMessage("You attach the heads to the arrows");
					owner.getInventory().add(new InvItem(headDef.getArrowID(), amt));
					owner.increaseXP(9, (int)(headDef.getExp() * (double)amt), 1);
					owner.sendStat(9);
					owner.sendInventory();
				}
			}
		});
		return true;
	}
	
	private boolean doBowString(Player player, final InvItem bowString, final InvItem bow) {
		boolean ret = true;
		switch (bow.getID()) {
			case UNSTRUNG_SHORTBOW:
				if (player.getCurStat(9) < 5)
					player.sendMessage("You need a fletching level of 5 to do that.");
				else {
					World.getDelayedEventHandler().add(new MiniEvent(player) {
						public void action() {
							if (owner.getInventory().remove(bowString) > -1 && owner.getInventory().remove(bow) > -1) {
								owner.sendMessage("You add the bow string to the bow");
								owner.getInventory().add(new InvItem(189, 1));
								owner.increaseXP(9, 5, 1);
								owner.sendStat(9);
								owner.sendInventory();
							}
						}
					});
				}
				break;
			case UNSTRUNG_LONGBOW:
				if (player.getCurStat(9) < 10)
					player.sendMessage("You need a fletching level of 10 to do that.");
				else {
					World.getDelayedEventHandler().add(new MiniEvent(player) {
						public void action() {
							if (owner.getInventory().remove(bowString) > -1 && owner.getInventory().remove(bow) > -1) {
								owner.sendMessage("You add the bow string to the bow");
								owner.getInventory().add(new InvItem(188, 1));
								owner.increaseXP(9, 10, 1);
								owner.sendStat(9);
								owner.sendInventory();
							}
						}
					});
				}
				break;
			case UNSTRUNG_OAK_SHORTBOW:
				if (player.getCurStat(9) < 20)
					player.sendMessage("You need a fletching level of 20 to do that.");
				else {
					World.getDelayedEventHandler().add(new MiniEvent(player) {
						public void action() {
							if (owner.getInventory().remove(bowString) > -1 && owner.getInventory().remove(bow) > -1) {
								owner.sendMessage("You add the bow string to the bow");
								owner.getInventory().add(new InvItem(649, 1));
								owner.increaseXP(9, 17, 1);
								owner.sendStat(9);
								owner.sendInventory();
							}
						}
					});
				}
				break;
			case UNSTRUNG_OAK_LONGBOW:
				if (player.getCurStat(9) < 25)
					player.sendMessage("You need a fletching level of 25 to do that.");
				else {
					World.getDelayedEventHandler().add(new MiniEvent(player) {
						public void action() {
							if (owner.getInventory().remove(bowString) > -1 && owner.getInventory().remove(bow) > -1) {
								owner.sendMessage("You add the bow string to the bow");
								owner.getInventory().add(new InvItem(648, 1));
								owner.increaseXP(9, 25, 1);
								owner.sendStat(9);
								owner.sendInventory();
							}
						}
					});
				}
				break;
			case UNSTRUNG_WILLOW_SHORTBOW:
				if (player.getCurStat(9) < 35)
					player.sendMessage("You need a fletching level of 35 to do that.");
				else {
					World.getDelayedEventHandler().add(new MiniEvent(player) {
						public void action() {
							if (owner.getInventory().remove(bowString) > -1 && owner.getInventory().remove(bow) > -1) {
								owner.sendMessage("You add the bow string to the bow");
								owner.getInventory().add(new InvItem(651, 1));
								owner.increaseXP(9, 33, 1);
								owner.sendStat(9);
								owner.sendInventory();
							}
						}
					});
				}
				break;
			case UNSTRUNG_WILLOW_LONGBOW:
				if (player.getCurStat(9) < 40)
					player.sendMessage("You need a fletching level of 40 to do that.");
				else {
					World.getDelayedEventHandler().add(new MiniEvent(player) {
						public void action() {
							if (owner.getInventory().remove(bowString) > -1 && owner.getInventory().remove(bow) > -1) {
								owner.sendMessage("You add the bow string to the bow");
								owner.getInventory().add(new InvItem(650, 1));
								owner.increaseXP(9, 42, 1);
								owner.sendStat(9);
								owner.sendInventory();
							}
						}
					});
				}
				break;
			case UNSTRUNG_MAPLE_SHORTBOW:
				if (player.getCurStat(9) < 50)
					player.sendMessage("You need a fletching level of 50 to do that.");
				else {
					World.getDelayedEventHandler().add(new MiniEvent(player) {
						public void action() {
							if (owner.getInventory().remove(bowString) > -1 && owner.getInventory().remove(bow) > -1) {
								owner.sendMessage("You add the bow string to the bow");
								owner.getInventory().add(new InvItem(653, 1));
								owner.increaseXP(9, 50, 1);
								owner.sendStat(9);
								owner.sendInventory();
							}
						}
					});
				}
				break;
			case UNSTRUNG_MAPLE_LONGBOW:
				if (player.getCurStat(9) < 55)
					player.sendMessage("You need a fletching level of 55 to do that.");
				else {
					World.getDelayedEventHandler().add(new MiniEvent(player) {
						public void action() {
							if (owner.getInventory().remove(bowString) > -1 && owner.getInventory().remove(bow) > -1) {
								owner.sendMessage("You add the bow string to the bow");
								owner.getInventory().add(new InvItem(652, 1));
								owner.increaseXP(9, 58, 1);
								owner.sendStat(9);
								owner.sendInventory();
							}
						}
					});
				}
				break;
			case UNSTRUNG_YEW_SHORTBOW:
				if (player.getCurStat(9) < 65)
					player.sendMessage("You need a fletching level of 65 to do that.");
				else {
					World.getDelayedEventHandler().add(new MiniEvent(player) {
						public void action() {
							if (owner.getInventory().remove(bowString) > -1 && owner.getInventory().remove(bow) > -1) {
								owner.sendMessage("You add the bow string to the bow");
								owner.getInventory().add(new InvItem(655, 1));
								owner.increaseXP(9, 67, 1);
								owner.sendStat(9);
								owner.sendInventory();
							}
						}
					});
				}
				break;
			case UNSTRUNG_YEW_LONGBOW:
				if (player.getCurStat(9) < 70)
					player.sendMessage("You need a fletching level of 70 to do that.");
				else {
					World.getDelayedEventHandler().add(new MiniEvent(player) {
						public void action() {
							if (owner.getInventory().remove(bowString) > -1 && owner.getInventory().remove(bow) > -1) {
								owner.sendMessage("You add the bow string to the bow");
								owner.getInventory().add(new InvItem(654, 1));
								owner.increaseXP(9, 75, 1);
								owner.sendStat(9);
								owner.sendInventory();
							}
						}
					});
				}
				break;
			case UNSTRUNG_MAGIC_SHORTBOW:
				if (player.getCurStat(9) < 80)
					player.sendMessage("You need a fletching level of 80 to do that.");
				else {
					World.getDelayedEventHandler().add(new MiniEvent(player) {
						public void action() {
							if (owner.getInventory().remove(bowString) > -1 && owner.getInventory().remove(bow) > -1) {
								owner.sendMessage("You add the bow string to the bow");
								owner.getInventory().add(new InvItem(657, 1));
								owner.increaseXP(9, 83, 1);
								owner.sendStat(9);
								owner.sendInventory();
							}
						}
					});
				}
				break;
			case UNSTRUNG_MAGIC_LONGBOW:
				if (player.getCurStat(9) < 85)
					player.sendMessage("You need a fletching level of 85 to do that.");
				else {
					World.getDelayedEventHandler().add(new MiniEvent(player) {
						public void action() {
							if (owner.getInventory().remove(bowString) > -1 && owner.getInventory().remove(bow) > -1) {
								owner.sendMessage("You add the bow string to the bow");
								owner.getInventory().add(new InvItem(656, 1));
								owner.increaseXP(9, 92, 1);
								owner.sendStat(9);
								owner.sendInventory();
							}
						}
					});
				}
				break;
			default:
				ret = false;
		}
		return ret;
	}
	
	private boolean doLogCut(Player player, final InvItem knife, final InvItem log) {
		final ItemLogCutDef cutDef = EntityHandler.getItemLogCutDef(log.getID());
		if (cutDef == null)
      		return false;
		
		World.getDelayedEventHandler().add(new MiniEvent(player) {
			public void action() {
				String[] options = new String[]{"Arrow shafts", "Shortbow", "Longbow", "Cancel"};
				owner.setMenuHandler(new MenuHandler(options) {
					public void handleReply(final int option, final String reply) {
						InvItem result;
						int reqLvl, exp;
						switch (option) {
							case 0:
								result = new InvItem(280, cutDef.getShaftAmount());
								reqLvl = cutDef.getShaftLvl();
								exp = cutDef.getShaftExp();
								break;
							case 1:
								result = new InvItem(cutDef.getShortbowID(), 1);
								reqLvl = cutDef.getShortbowLvl();
								exp = cutDef.getShortbowExp();
								break;
							case 2:
								result = new InvItem(cutDef.getLongbowID(), 1);
								reqLvl = cutDef.getLongbowLvl();
								exp = cutDef.getLongbowExp();
								break;
							default:
								return;
						}
						if (owner.getCurStat(9) < reqLvl) {
							owner.sendMessage("You need a fletching level of " + reqLvl + " to cut that.");
      						return;
						}
						if (owner.getInventory().remove(log) > -1) {
							owner.sendMessage("You make a " + result.getDef().getName());
							owner.getInventory().add(result);
							owner.increaseXP(9, exp, 1);
							owner.sendStat(9);
							owner.sendInventory();
						}
					}
				});
				owner.sendMenu(options);
			}
		});
		return true;
	}
	
	private boolean doHerblaw(Player player, final InvItem vial, final InvItem herb) {
		final ItemHerbDef herbDef = EntityHandler.getItemHerbDef(herb.getID());
		if (herbDef == null)
			return false;
		
		if (player.getCurStat(15) < herbDef.getReqLevel()) {
			player.sendMessage("You need a herblaw level of " + herbDef.getReqLevel() + " to mix those.");
			return true;
		}
		World.getDelayedEventHandler().add(new MiniEvent(player) {
			public void action() {
				if (owner.getInventory().remove(vial) > -1 && owner.getInventory().remove(herb) > -1) {
					owner.sendMessage("You add the " + herb.getDef().getName() + " to the water");
					owner.getInventory().add(new InvItem(herbDef.getPotionId(), 1));
					owner.increaseXP(15, herbDef.getExp(), 1);
					owner.sendStat(15);
					owner.sendInventory();
				}
			}
		});
		return true;
	}
}