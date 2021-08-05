package com.openrsc.server.plugins.authentic.itemactions;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.UseInvTrigger;
import com.openrsc.server.util.rsc.MessageType;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class InvUseOnItem implements UseInvTrigger {
	private int[] capes = {
		ItemId.RED_CAPE.id(), ItemId.BLACK_CAPE.id(), ItemId.BLUE_CAPE.id(),
		ItemId.GREEN_CAPE.id(), ItemId.YELLOW_CAPE.id(), ItemId.ORANGE_CAPE.id(),
		ItemId.PURPLE_CAPE.id()
	};
	private int[] dye = {
		ItemId.REDDYE.id(), ItemId.YELLOWDYE.id(), ItemId.BLUEDYE.id(),
		ItemId.ORANGEDYE.id(), ItemId.GREENDYE.id(), ItemId.PURPLEDYE.id()
	};
	private int[] newCapes = {
		ItemId.RED_CAPE.id(), ItemId.YELLOW_CAPE.id(), ItemId.BLUE_CAPE.id(),
		ItemId.ORANGE_CAPE.id(), ItemId.GREEN_CAPE.id(), ItemId.PURPLE_CAPE.id()
	};

	@Override
	public void onUseInv(Player player, Integer invIndex, Item item1, Item item2) {
		if(item1.getItemStatus().getNoted() || item2.getItemStatus().getNoted()) return;

		/*
		 * Dye the wig with yellow dye and get blonde wig for Prince Ali rescue Quest
		 */
		if (compareItemsIds(item1, item2, ItemId.WOOL_WIG.id(), ItemId.YELLOWDYE.id())) {
			if (player.getCarriedItems().remove(new Item(ItemId.YELLOWDYE.id())) > -1
					&& player.getCarriedItems().remove(new Item(ItemId.WOOL_WIG.id())) > -1) {
				player.message("You dye the wig blond");
				player.getCarriedItems().getInventory().add(new Item(ItemId.BLONDE_WIG.id()));

				return;
			}
		}

		else if (compareItemsIds(item1, item2, ItemId.POT_OF_FLOUR.id(), ItemId.SWAMP_TAR.id())) {
			if (player.getCarriedItems().hasCatalogID(ItemId.POT_OF_FLOUR.id(), Optional.of(false)) && player.getCarriedItems().remove(new Item(ItemId.SWAMP_TAR.id())) > -1) {
				player.getCarriedItems().remove(new Item(ItemId.POT_OF_FLOUR.id()));
				player.getCarriedItems().getInventory().add(new Item(ItemId.POT.id()));
				player.message("you mix the flour with the swamp tar");
				player.message("it mixes into a paste");
				player.getCarriedItems().getInventory().add(new Item(ItemId.UNCOOKED_SWAMP_PASTE.id()));

				return;
			}
		}

		else if (compareItemsIds(item1, item2, ItemId.TINDERBOX.id(), ItemId.UNLIT_BLACK_CANDLE.id())) {
			if (player.getCarriedItems().remove(new Item(ItemId.UNLIT_BLACK_CANDLE.id())) > -1) {
				player.message("You light the candle");
				player.getCarriedItems().getInventory().add(new Item(ItemId.LIT_BLACK_CANDLE.id()));

				return;
			}
		}

		else if (compareItemsIds(item1, item2, ItemId.TINDERBOX.id(), ItemId.UNLIT_CANDLE.id())) {
			if (player.getCarriedItems().remove(new Item(ItemId.UNLIT_CANDLE.id())) > -1) {
				player.message("You light the candle");
				player.getCarriedItems().getInventory().add(new Item(ItemId.LIT_CANDLE.id()));

				return;
			}
		}

		else if (compareItemsIds(item1, item2, ItemId.BLAMISH_OIL.id(), ItemId.FISHING_ROD.id())) {
			if (player.getCarriedItems().remove(new Item(ItemId.BLAMISH_OIL.id())) > -1
					&& player.getCarriedItems().remove(new Item(ItemId.FISHING_ROD.id())) > -1) {
				player.message("You rub the oil onto the fishing rod");
				player.getCarriedItems().getInventory().add(new Item(ItemId.OILY_FISHING_ROD.id()));

			}
			return;
		}

		else if (compareItemsIds(item1, item2, ItemId.BROKEN_GLASS.id(), ItemId.DAMP_STICKS.id())) {
			if (player.getCarriedItems().remove(new Item(ItemId.DAMP_STICKS.id())) > -1) {
				player.message("you hold the glass to the sun");
				player.message("above the damp sticks");
				delay(2);
				player.message("the glass acts like a lens");
				player.message("and drys the sticks out");
				player.getCarriedItems().getInventory().add(new Item(ItemId.DRY_STICKS.id()));
				return;
			}
		}

		else if (compareItemsIds(item1, item2, ItemId.MILK.id(), ItemId.CHOCOLATE_DUST.id())) {
			if (player.getCarriedItems().remove(new Item(ItemId.MILK.id())) > -1
					&& player.getCarriedItems().remove(new Item(ItemId.CHOCOLATE_DUST.id())) > -1) {
				player.message("You mix the chocolate into the bucket");
				player.getCarriedItems().getInventory().add(new Item(ItemId.CHOCOLATY_MILK.id()));

			}
			return;
		}

		else if (compareItemsIds(item1, item2, ItemId.CHOCOLATY_MILK.id(), ItemId.SNAPE_GRASS.id())) {
			if (player.getCarriedItems().remove(new Item(ItemId.CHOCOLATY_MILK.id())) > -1
					&& player.getCarriedItems().remove(new Item(ItemId.SNAPE_GRASS.id())) > -1) {
				player.message("You mix the snape grass into the bucket");
				player.getCarriedItems().getInventory().add(new Item(ItemId.HANGOVER_CURE.id()));

			}
			return;
		}

		else if (compareItemsIds(item1, item2, ItemId.RAW_BEEF.id(), ItemId.BOWL_OF_WATER.id())
				|| compareItemsIds(item1, item2, ItemId.RAW_CHICKEN.id(), ItemId.BOWL_OF_WATER.id())
				|| compareItemsIds(item1, item2, ItemId.RAW_RAT_MEAT.id(), ItemId.BOWL_OF_WATER.id())
				|| compareItemsIds(item1, item2, ItemId.RAW_BEAR_MEAT.id(), ItemId.BOWL_OF_WATER.id())) {
			player.playerServerMessage(MessageType.QUEST, "you need to precook the meat");
			return;
		}


		/*
		 * Combine Dyes
		 */
		else if (compareItemsIds(item1, item2, ItemId.REDDYE.id(), ItemId.YELLOWDYE.id())) {
			if (player.getCarriedItems().remove(new Item(ItemId.YELLOWDYE.id())) > -1
					&& player.getCarriedItems().remove(new Item(ItemId.REDDYE.id())) > -1) {
				player.getCarriedItems().getInventory().add(new Item(ItemId.ORANGEDYE.id()));
				player.message("You mix the Dyes");

				return;
			}
		}

		else if (compareItemsIds(item1, item2, ItemId.REDDYE.id(), ItemId.BLUEDYE.id())) {
			if (player.getCarriedItems().remove(new Item(ItemId.BLUEDYE.id())) > -1
					&& player.getCarriedItems().remove(new Item(ItemId.REDDYE.id())) > -1) {
				player.getCarriedItems().getInventory().add(new Item(ItemId.PURPLEDYE.id()));
				player.message("You mix the Dyes");

				return;
			}
		}

		else if (compareItemsIds(item1, item2, ItemId.YELLOWDYE.id(), ItemId.BLUEDYE.id())) {
			if (player.getCarriedItems().remove(new Item(ItemId.BLUEDYE.id())) > -1
					&& player.getCarriedItems().remove(new Item(ItemId.YELLOWDYE.id())) > -1) {
				player.getCarriedItems().getInventory().add(new Item(ItemId.GREENDYE.id()));
				player.message("You mix the Dyes");

				return;
			}
		}

		else if (compareItemsIds(item1, item2, ItemId.GOBLIN_ARMOUR.id(), ItemId.ORANGEDYE.id())) {
			if (player.getCarriedItems().remove(new Item(ItemId.ORANGEDYE.id())) > -1
					&& player.getCarriedItems().remove(new Item(ItemId.GOBLIN_ARMOUR.id())) > -1) {
				player.getCarriedItems().getInventory().add(new Item(ItemId.ORANGE_GOBLIN_ARMOUR.id()));
				player.message("You dye the goblin armor");
			}
		}

		else if (compareItemsIds(item1, item2, ItemId.GOBLIN_ARMOUR.id(), ItemId.BLUEDYE.id())) {
			if (player.getCarriedItems().remove(new Item(ItemId.BLUEDYE.id())) > -1
					&& player.getCarriedItems().remove(new Item(ItemId.GOBLIN_ARMOUR.id())) > -1) {
				player.getCarriedItems().getInventory().add(new Item(ItemId.BLUE_GOBLIN_ARMOUR.id()));
				player.message("You dye the goblin armor");
			}
		}

		else if (compareItemsIds(item1, item2,
				ItemId.RIGHT_HALF_DRAGON_SQUARE_SHIELD.id(), ItemId.LEFT_HALF_DRAGON_SQUARE_SHIELD.id())) {
			// TODO non-kosher message: hinting to use the anvil (if kosher message is found, replace)
			player.message("You need an anvil and a hammer to repair the shield");
		}

		else if (compareItemsIds(item1, item2, ItemId.TOOTH_KEY_HALF.id(), ItemId.LOOP_KEY_HALF.id())) {
			if (player.getCarriedItems().remove(item1) > -1 && player.getCarriedItems().remove(item2) > -1) {
				player.message("You join the two halves of the key together");
				player.getCarriedItems().getInventory().add(new Item(ItemId.CRYSTAL_KEY.id(), 1));
				if (config().CRYSTAL_KEY_GIVES_XP) {
					player.incExp(Skill.CRAFTING.id(), 40, true);
				}
			}
		}

		else if (isMapPiece(item1) && isMapPiece(item2)) {
			int[] pieces = {
				ItemId.MAP_PIECE_1.id(), ItemId.MAP_PIECE_2.id(), ItemId.MAP_PIECE_3.id()
			};
			if (player.getCarriedItems().getInventory().countId(pieces[0]) < 1 || player.getCarriedItems().getInventory().countId(pieces[1]) < 1 ||
				player.getCarriedItems().getInventory().countId(pieces[2]) < 1) {
				player.message("You still need one more piece of map");
			} else {
				player.message("You put all the pieces of map together");
				player.getCarriedItems().remove(new Item(pieces[0]));
				player.getCarriedItems().remove(new Item(pieces[1]));
				player.getCarriedItems().remove(new Item(pieces[2]));
				player.getCarriedItems().getInventory().add(new Item(ItemId.MAP.id(), 1));
			}
		}

		else if (isCrestFragment(item1) && isCrestFragment(item2)) {
			int[] fragments = {
				ItemId.CREST_FRAGMENT_ONE.id(), ItemId.CREST_FRAGMENT_TWO.id(), ItemId.CREST_FRAGMENT_THREE.id()
			};
			if (player.getCarriedItems().getInventory().countId(fragments[0]) < 1 || player.getCarriedItems().getInventory().countId(fragments[1]) < 1 ||
				player.getCarriedItems().getInventory().countId(fragments[2]) < 1) {
				player.message("You still need one more piece of the crest");
			} else {
				player.message("You put all the pieces of the crest together");
				player.getCarriedItems().remove(new Item(fragments[0]));
				player.getCarriedItems().remove(new Item(fragments[1]));
				player.getCarriedItems().remove(new Item(fragments[2]));
				player.getCarriedItems().getInventory().add(new Item(ItemId.FAMILY_CREST.id(), 1));
			}
		}

		for (Integer il : capes) {
			if (il == item1.getCatalogId()) {
				for (int i = 0; i < dye.length; i++) {
					if (dye[i] == item2.getCatalogId()) {
						if (player.getCarriedItems().remove(new Item(item1.getCatalogId())) > -1
								&& player.getCarriedItems().remove(new Item(item2.getCatalogId())) > -1) {
							player.message("You dye the Cape");
							player.getCarriedItems().getInventory().add(new Item(newCapes[i]));
							player.incExp(Skill.CRAFTING.id(), 10, true);
							return;
						}
					}
				}
			}

			else if (il == item2.getCatalogId()) {
				for (int i = 0; i < dye.length; i++) {
					if (dye[i] == item1.getCatalogId()) {
						if (player.getCarriedItems().remove(new Item(item1.getCatalogId())) > -1
								&& player.getCarriedItems().remove(new Item(item2.getCatalogId())) > -1) {
							player.message("You dye the Cape");
							player.getCarriedItems().getInventory().add(new Item(newCapes[i]));
							player.incExp(Skill.CRAFTING.id(), 10, true);
							return;
						}
					}
				}
			}
		}
	}

	private boolean isMapPiece(Item item) {
		return item.getCatalogId() == ItemId.MAP_PIECE_1.id()
			|| item.getCatalogId() == ItemId.MAP_PIECE_2.id()
			|| item.getCatalogId() == ItemId.MAP_PIECE_3.id();
	}

	private boolean isCrestFragment(Item item) {
		return item.getCatalogId() == ItemId.CREST_FRAGMENT_ONE.id()
			|| item.getCatalogId() == ItemId.CREST_FRAGMENT_TWO.id()
			|| item.getCatalogId() == ItemId.CREST_FRAGMENT_THREE.id();
	}

	@Override
	public boolean blockUseInv(Player player, Integer invIndex, Item item1, Item item2) {
		if (compareItemsIds(item1, item2, ItemId.REDDYE.id(), ItemId.YELLOWDYE.id()))
			return true;
		else if (compareItemsIds(item1, item2, ItemId.REDDYE.id(), ItemId.BLUEDYE.id()))
			return true;
		else if (compareItemsIds(item1, item2, ItemId.YELLOWDYE.id(), ItemId.BLUEDYE.id()))
			return true;
		else if (compareItemsIds(item1, item2, ItemId.GOBLIN_ARMOUR.id(), ItemId.ORANGEDYE.id()))
			return true;
		else if (compareItemsIds(item1, item2, ItemId.GOBLIN_ARMOUR.id(), ItemId.BLUEDYE.id()))
			return true;
		else if (compareItemsIds(item1, item2, ItemId.POT_OF_FLOUR.id(), ItemId.SWAMP_TAR.id()))
			return true;
		else if (compareItemsIds(item1, item2, ItemId.TINDERBOX.id(), ItemId.UNLIT_BLACK_CANDLE.id()))
			return true;
		else if (compareItemsIds(item1, item2, ItemId.TINDERBOX.id(), ItemId.UNLIT_CANDLE.id()))
			return true;
		else if (compareItemsIds(item1, item2, ItemId.BLAMISH_OIL.id(), ItemId.FISHING_ROD.id()))
			return true;
		else if (compareItemsIds(item1, item2, ItemId.BROKEN_GLASS.id(), ItemId.DAMP_STICKS.id()))
			return true;
		else if (compareItemsIds(item1, item2, ItemId.MILK.id(), ItemId.CHOCOLATE_DUST.id()))
			return true;
		else if (compareItemsIds(item1, item2, ItemId.CHOCOLATY_MILK.id(), ItemId.SNAPE_GRASS.id()))
			return true;
		else if (compareItemsIds(item1, item2, ItemId.RAW_BEEF.id(), ItemId.BOWL_OF_WATER.id())
				|| compareItemsIds(item1, item2, ItemId.RAW_CHICKEN.id(), ItemId.BOWL_OF_WATER.id())
				|| compareItemsIds(item1, item2, ItemId.RAW_RAT_MEAT.id(), ItemId.BOWL_OF_WATER.id())
				|| compareItemsIds(item1, item2, ItemId.RAW_BEAR_MEAT.id(), ItemId.BOWL_OF_WATER.id()))
			return true;
		/*
		 * prince ali rescue dye wig and yellow die to blond wig
		 */
		else if (compareItemsIds(item1, item2, ItemId.WOOL_WIG.id(), ItemId.YELLOWDYE.id()))
			return true;
		/*
		 * assembles: key halves, map pieces, and crest fragments
		 * cannot be assembled directly: dragon square shield halves, however does hint player
		 */
		else if (compareItemsIds(item1, item2, ItemId.TOOTH_KEY_HALF.id(), ItemId.LOOP_KEY_HALF.id()))
			return true;
		else if (compareItemsIds(item1, item2, ItemId.MAP_PIECE_1.id(), ItemId.MAP_PIECE_2.id())
				|| compareItemsIds(item1, item2, ItemId.MAP_PIECE_1.id(), ItemId.MAP_PIECE_3.id())
				|| compareItemsIds(item1, item2, ItemId.MAP_PIECE_2.id(), ItemId.MAP_PIECE_3.id()))
			return true;
		else if (compareItemsIds(item1, item2, ItemId.CREST_FRAGMENT_ONE.id(), ItemId.CREST_FRAGMENT_TWO.id())
				|| compareItemsIds(item1, item2, ItemId.CREST_FRAGMENT_ONE.id(), ItemId.CREST_FRAGMENT_THREE.id())
				|| compareItemsIds(item1, item2, ItemId.CREST_FRAGMENT_TWO.id(), ItemId.CREST_FRAGMENT_THREE.id()))
			return true;
		else if (compareItemsIds(item1, item2, ItemId.RIGHT_HALF_DRAGON_SQUARE_SHIELD.id(), ItemId.LEFT_HALF_DRAGON_SQUARE_SHIELD.id()))
			return true;

		for (int il : capes) {
			if (il == item1.getCatalogId()) {
				return true;
			}
		}
		return false;
	}
}
