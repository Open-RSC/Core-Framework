package com.openrsc.server.plugins.itemactions;

import com.openrsc.server.Server;
import com.openrsc.server.event.ShortEvent;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.InvUseOnItemListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnItemExecutiveListener;

public class InvUseOnItem implements InvUseOnItemListener, InvUseOnItemExecutiveListener {
	int[] new_pumpkin_head = { 2098, 2099, 2100, 2097, 2102, 2101 };
	int[] pumpkin_head = { 2097, 2098, 2099, 2100, 2101, 2102 };
	int[] capes = { 183, 209, 229, 511, 512, 513, 514 };
	int[] dye = { 238, 239, 272, 282, 515, 516 };
	int[] newCapes = { 183, 512, 229, 513, 511, 514 };
	
	@Override
	public void onInvUseOnItem(Player player, Item item1, Item item2) {
		/**
		 * Dye the wig with yellow dye and get blonde wig for Prince Ali rescue Quest
		 */
		if(compareItemsIds(item1, item2, 245, 239)) {
			if(player.getInventory().remove(new Item(239)) > -1 && player.getInventory().remove(new Item(245)) > -1) {
				player.message("You dye the wig blond");
				player.getInventory().add(new Item(244));
				
				return;
			}
		}
		else if(compareItemsIds(item1, item2, 136, 783)) {
			if(player.getInventory().remove(new Item(136)) > -1 && player.getInventory().remove(new Item(783)) > -1) {
				player.message("you mix the flour with the swamp tar");
				player.message("it mixes into a paste");
				player.getInventory().add(new Item(784));
				
				return;
			}
		}
		else if(compareItemsIds(item1, item2, 166, 600)) {
			if(player.getInventory().remove(new Item(600)) > -1) {
				player.message("You light the candle");
				player.getInventory().add(new Item(602));
				
				return;
			}
		}
		else if(compareItemsIds(item1, item2, 166, 599)) {
			if(player.getInventory().remove(new Item(599)) > -1) {
				player.message("You light the candle");
				player.getInventory().add(new Item(601));
				
				return;
			}
		}
		else if(compareItemsIds(item1, item2, 588, 377)) {
			if(player.getInventory().remove(new Item(588)) > -1 && player.getInventory().remove(new Item(377)) > -1) {
				player.message("You rub the oil onto the fishing rod");
				player.getInventory().add(new Item(589));
				
			} 
			return;
		}
		else if(compareItemsIds(item1, item2, 778, 776)) {
			if(player.getInventory().remove(new Item(776)) > -1) {
				player.message("you hold the glass to the sun");
				player.message("above the damp sticks");
				Server.getServer().getEventHandler().add(new ShortEvent(player) {
					public void action() {
						owner.message("the glass acts like a lens");
						owner.message("and drys the sticks out");
					}
				});
				player.getInventory().add(new Item(777));
				
				return;
			}
		}
		else if(compareItemsIds(item1, item2, 22, 772)) {
			if(player.getInventory().remove(new Item(22)) > -1 && player.getInventory().remove(new Item(772)) > -1) {
				player.message("You mix the chocolate into the bucket");
				player.getInventory().add(new Item(770));
				
			}
			return;
		}
		else if(compareItemsIds(item1, item2, 770, 469)) {
			if(player.getInventory().remove(new Item(770)) > -1 && player.getInventory().remove(new Item(469)) > -1) {
				player.message("You mix the snape grass into the bucket");
				player.getInventory().add(new Item(771));
				
			}
			return;
		}



		/**
		 * Wine and combine Dyes
		 */

		else if(compareItemsIds(item1, item2, 143, 141)) { // wine
			if (player.getInventory().remove(new Item(143)) > -1 && player.getInventory().remove(new Item(141)) > -1) {
				player.message("You combine the grapes and water to make wine");
				player.getInventory().add(new Item(142));
				
				return;
			}
		}
		else if(compareItemsIds(item1, item2, 238, 239)) {
			if (player.getInventory().remove(new Item(239)) > -1 && player.getInventory().remove(new Item(238)) > -1) {
				player.getInventory().add(new Item(282));
				player.message("You mix the Dyes");
				
				return;
			}
		}
		else if(compareItemsIds(item1, item2, 238, 272)) {
			if (player.getInventory().remove(new Item(272)) > -1 && player.getInventory().remove(new Item(238)) > -1) {
				player.getInventory().add(new Item(516));
				player.message("You mix the Dyes");
				
				return;
			}
		}
		else if(compareItemsIds(item1, item2, 239, 272)) {
			if (player.getInventory().remove(new Item(272)) > -1 && player.getInventory().remove(new Item(239)) > -1) {
				player.getInventory().add(new Item(515));
				player.message("You mix the Dyes");
				
				return;
			}
		}


		for (Integer il : capes) {
			if (il == item1.getID()) {
				for (int i = 0; i < dye.length; i++) {
					if (dye[i] == item2.getID()) {
						if (player.getInventory().remove(new Item(item1.getID())) > -1 && player.getInventory().remove(new Item(item2.getID())) > -1) {
							player.message("You dye the Cape");
							player.getInventory().add(new Item(newCapes[i]));
							player.incExp(12, 10, true);
							return;
						}
					}
				}
			} 
			else if (il == item2.getID()) {
				for (int i = 0; i < dye.length; i++) {
					if (dye[i] == item1.getID()) {
						if (player.getInventory().remove(new Item(item1.getID())) > -1 && player.getInventory().remove(new Item(item2.getID())) > -1) {
							player.message("You dye the Cape");
							player.getInventory().add(new Item(newCapes[i]));
							player.incExp(12, 10, true);
							return;
						}
					}
				}
			}
		}
		for (Integer il : pumpkin_head) {
			if (il == item1.getID()) {
				for (int i = 0; i < dye.length; i++) {
					if (dye[i] == item2.getID()) {
						if (player.getInventory().remove(new Item(item1.getID())) > -1 && player.getInventory().remove(new Item(item2.getID())) > -1) {
							player.message("You dye the Pumpkin head");
							player.getInventory().add(new Item(new_pumpkin_head[i]));
							
							return;
						}
					}
				}
			} 
			else if (il == item2.getID()) {
				for (int i = 0; i < dye.length; i++) {
					if (dye[i] == item1.getID()) {
						if (player.getInventory().remove(new Item(item1.getID())) > -1 && player.getInventory().remove(new Item(item2.getID())) > -1) {
							player.message("You dye the Pumpkin head");
							player.getInventory().add(new Item(new_pumpkin_head[i]));
							
							return;
						}
					}
				}
			}
		}
		if(compareItemsIds(item1, item2, 273, 282)) {
			if (player.getInventory().remove(new Item(282)) > -1 && player.getInventory().remove(new Item(273)) > -1) {
				player.getInventory().add(new Item(274));
				player.message("You dye the goblin armor");
				
				return;
			}
		}
		else if(compareItemsIds(item1, item2, 273, 272)) {
			if (player.getInventory().remove(new Item(272)) > -1 && player.getInventory().remove(new Item(273)) > -1) {
				player.getInventory().add(new Item(275));
				player.message("You dye the goblin armor");
				
				return;
			}
		}
		else if (compareItemsIds(item1, item2, 1276, 1277)) {
			//non-kosher message: hinting to use the anvil (if kosher message is found, replace)
			player.message("You need an anvil and a hammer to repair the shield");
			
			// joining directly is non-kosher mechanic
			//if (player.getInventory().remove(new Item(1276)) > -1 && player.getInventory().remove(new Item(1277)) > -1) {
			//	player.message("You join the two halves of the shield together");
			//	player.getInventory().add(new Item(1278));
			//	
			//	return;
			//}
			
			return;
		}
		else if (compareItemsIds(item1, item2, 526, 527)) {
			if (player.getInventory().remove(item1) > -1 && player.getInventory().remove(item2) > -1) {
				player.message("You join the two halves of the key together");
				player.getInventory().add(new Item(525, 1));
				return;
			}
		}
		
		else if(isMapPiece(item1) && isMapPiece(item2)) {
			int[] pieces = {416, 417, 418};
			if(player.getInventory().countId(pieces[0]) < 1 || player.getInventory().countId(pieces[1]) < 1 ||
					player.getInventory().countId(pieces[2]) < 1) {
				player.message("You still need one more piece of map");
				return;
			}
			else {
				player.message("You put all the pieces of map together");
				player.getInventory().remove(pieces[0], 1);
				player.getInventory().remove(pieces[1], 1);
				player.getInventory().remove(pieces[2], 1);
				player.getInventory().add(new Item(415, 1));
				return;
			}
		}
		
		else if(isCrestFragment(item1) && isCrestFragment(item2)) {
			int[] fragments = {695, 696, 697};
			if(player.getInventory().countId(fragments[0]) < 1 || player.getInventory().countId(fragments[1]) < 1 ||
					player.getInventory().countId(fragments[2]) < 1) {
				player.message("You still need one more piece of the crest");
				return;
			}
			else {
				player.message("You put all the pieces of the crest together");
				player.getInventory().remove(fragments[0], 1);
				player.getInventory().remove(fragments[1], 1);
				player.getInventory().remove(fragments[2], 1);
				player.getInventory().add(new Item(694, 1));
				return;
			}
		}
	}
	
	private boolean isMapPiece(Item item) {
		return item.getID() == 416 || item.getID() == 417 || item.getID() == 418;
	}
	
	private boolean isCrestFragment(Item item) {
		return item.getID() == 695 || item.getID() == 696 || item.getID() == 697;
	}

	@Override
	public boolean blockInvUseOnItem(Player player, Item item1, Item item2) {
		if(compareItemsIds(item1, item2, 238, 239))
			return true;
		else if(compareItemsIds(item1, item2, 238, 272))
			return true;
		else if(compareItemsIds(item1, item2, 239, 272))
			return true;
		else if(compareItemsIds(item1, item2, 273, 282))
			return true;
		else if(compareItemsIds(item1, item2, 273, 272))
			return true;
		else if(compareItemsIds(item1, item2, 136, 783))
			return true;
		else if(compareItemsIds(item1, item2, 166, 600))
			return true;
		else if(compareItemsIds(item1, item2, 166, 599))
			return true;
		else if(compareItemsIds(item1, item2, 588, 377))
			return true;
		else if(compareItemsIds(item1, item2, 778, 776))
			return true;
		else if(compareItemsIds(item1, item2, 22, 772))
			return true;
		else if(compareItemsIds(item1, item2, 770, 469))
			return true;
		/**
		 * prince ali rescue dye wig and yellow die to blond wig
		 */
		else if(compareItemsIds(item1, item2, 245, 239))
			return true;
		/**
		 * assembles: key halves, map pieces, and crest fragments
		 * cannot be assembled directly: dragon square shield halves, however does hint player
		 **/
		else if(compareItemsIds(item1, item2, 526, 527))
			return true;
		else if(compareItemsIds(item1, item2, 416, 417) || compareItemsIds(item1, item2, 416, 418) || compareItemsIds(item1, item2, 417, 418))
			return true;
		else if(compareItemsIds(item1, item2, 695, 696) || compareItemsIds(item1, item2, 695, 697) || compareItemsIds(item1, item2, 696, 697))
			return true;
		else if(compareItemsIds(item1, item2, 1276, 1277))
			return true;
		else if(compareItemsIds(item1, item2, 143, 141))
			return true;

		for(int il : capes) {
			if(il == item1.getID()) {
				return true;
			}
		}
		for(int il : pumpkin_head) {
			if(il == item1.getID()) {
				return true;
			} else if(il == item2.getID()) {
				return true;
			}
		}
		return false;
	}
	
	public boolean compareItemsIds(Item item1, Item item2, int idA, int idB) {
		return item1.getID() == idA && item2.getID() == idB || item1.getID() == idB && item2.getID() == idA;
	}
}
