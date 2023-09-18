package com.openrsc.server.plugins.authentic.minigames.kittencare;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Minigames;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.MiniGameInterface;
import com.openrsc.server.plugins.triggers.*;
import com.openrsc.server.util.rsc.DataConversions;

import java.util.ArrayList;
import java.util.List;

import static com.openrsc.server.plugins.Functions.*;

public class KittenToCat implements MiniGameInterface, CatGrowthTrigger, DropObjTrigger,
	OpInvTrigger, UseInvTrigger, UseNpcTrigger {

	protected static final int BASE_FACTOR = 16;

	@Override
	public int getMiniGameId() {
		return Minigames.KITTEN_CARE;
	}

	@Override
	public String getMiniGameName() {
		return "Kitten Care (members)";
	}

	@Override
	public boolean isMembers() {
		return true;
	}

	@Override
	public void handleReward(Player player) {
		//mini-quest complete handled already
	}

	@Override
	public boolean blockDropObj(Player player, Integer invIndex, Item item, Boolean fromInventory) {
		return item.getCatalogId() == ItemId.KITTEN.id();
	}

	@Override
	public void onDropObj(Player player, Integer invIndex, Item item, Boolean fromInventory) {
		if (item.getCatalogId() == ItemId.KITTEN.id()) {
			int totalReleased = 1;
			if (player.getCache().hasKey("kittens_released")) {
				totalReleased += player.getCache().getInt("kittens_released");
			}
			player.getCache().set("kittens_released", totalReleased);
			player.getCarriedItems().remove(new Item(ItemId.KITTEN.id()));
			mes("you drop the kitten");
			delay(2);
			mes("it's upset and runs away");
			delay();
		}

		KittenState state = new KittenState();
		state.saveState(player);
	}

	@Override
	public boolean blockOpInv(Player player, Integer invIndex, Item item, String command) {
		return item.getCatalogId() == ItemId.KITTEN.id()
			|| (item.getCatalogId() == ItemId.CAT.id() && player.getConfig().WANT_EXTENDED_CATS_BEHAVIOR);
	}

	@Override
	public void onOpInv(Player player, Integer invIndex, Item item, String command) {
		if (item.getCatalogId() == ItemId.KITTEN.id()) {
			mes("you softly stroke the kitten");
			delay(3);
			mes("@yel@kitten:..purr..purr..");
			delay(3);
			mes("the kitten appreciates the attention");
			delay();

			reduceKittensLoneliness(player);
		} else if (item.getCatalogId() == ItemId.CAT.id() && config().WANT_EXTENDED_CATS_BEHAVIOR) {
			mes("you softly stroke the cat");
			delay(3);
			mes("@yel@cat:..purr..purr..");
			delay(3);
			mes("it appreciates the attention");
			delay();
		}
	}

	public void entertainCat(Item item, Player player, boolean isGrown) {
		if (item.getCatalogId() == ItemId.BALL_OF_WOOL.id()) {
			if (!isGrown) {
				mes("your kitten plays around with the ball of wool");
				delay(3);
				mes("it seems to love pouncing on it");
				delay(3);

				reduceKittensLoneliness(player);
			} else {
				mes("your cat plays around with the ball of wool");
				delay(3);
				mes("it seems to love pouncing on it");
				delay(3);
			}
		} else if (item.getCatalogId() == ItemId.WOOL.id()) {
			if (!isGrown) {
				mes("your kitten plays around with the wool");
				delay(3);
				mes("it seems to be enjoying itself");
				delay(3);

				reduceKittensLoneliness(player);
			} else {
				mes("your cat plays around with the wool");
				delay(3);
				mes("it seems to be enjoying itself");
				delay(3);
			}
		}
	}

	public void feedCat(Item item, Player player, boolean isGrown) {
		boolean feeded = false;
		switch (ItemId.getById(item.getCatalogId())) {
		case MILK:
			player.getCarriedItems().remove(new Item(item.getCatalogId()));
			player.getCarriedItems().getInventory().add(new Item(ItemId.BUCKET.id()));
			if(!isGrown) {
				mes("you give the kitten the milk");
				delay(3);
				mes("the kitten quickly laps it up then licks his paws");
				delay(3);
			} else {
				mes("you give the cat the milk");
				delay(3);
				mes("the kitten quickly laps it up then licks his paws");
				delay(3);
			}
			feeded = true;
			break;
		case RAW_SHRIMP:
		case RAW_SARDINE:
		case SEASONED_SARDINE:
		case SARDINE:
		case RAW_ANCHOVIES:
		case RAW_TROUT:
		case TROUT:
		case RAW_SALMON:
		case SALMON:
		case RAW_TUNA:
		case TUNA:
			player.getCarriedItems().remove(new Item(item.getCatalogId()));
			if(!isGrown) {
				mes("you give the kitten the " + item.getDef(player.getWorld()).getName(),
						"the kitten quickly eats it up then licks his paws");
			} else {
				mes("you give the cat the " + item.getDef(player.getWorld()).getName(),
						"it quickly eat's them up and licks its paws");
			}
			feeded = true;
			break;
		default:
			break;
		}

		if (feeded && !isGrown)
			reduceKittensHunger(player);
	}

	private void reduceKittensLoneliness(Player player) {
		KittenState state = new KittenState();
		state.loadState(player);
		int loneliness = state.getLoneliness();
		if (loneliness >= BASE_FACTOR) {
			state.setLoneliness(loneliness - BASE_FACTOR);
			state.saveState(player);
		}
	}

	private void reduceKittensHunger(Player player) {
		KittenState state = new KittenState();
		state.loadState(player);
		int hunger = state.getHunger();
		if (hunger >= BASE_FACTOR) {
			state.setHunger(hunger - BASE_FACTOR);
			state.saveState(player);
		}
	}

	@Override
	public boolean blockCatGrowth(Player player) {
		return player.getCarriedItems().hasCatalogID(ItemId.KITTEN.id());
	}

	@Override
	public void onCatGrowth(Player player) {
		if (player.getCarriedItems().hasCatalogID(ItemId.KITTEN.id())) {
			// no events in memory, check in cache
			KittenState state = new KittenState();
			state.loadState(player);
			int kittenHunger = state.getHunger();
			int kittenLoneliness = state.getLoneliness();
			int kittenEvents = state.getEvents();

			int changeHunger = DataConversions.random(4, 6);
			int changeLoneliness = DataConversions.random(4, 6);

			// trigger only if the gauges have passed to the next tenth digit
			boolean tHunger = ((kittenHunger + changeHunger) / BASE_FACTOR) - (kittenHunger / BASE_FACTOR) > 0;
			boolean tLoneliness = ((kittenLoneliness + changeLoneliness) / BASE_FACTOR) - (kittenLoneliness / BASE_FACTOR) > 0;

			kittenHunger += changeHunger;
			kittenLoneliness += changeLoneliness;

			List<String> messages = new ArrayList<String>();
			// hungry and lonely
			if (tHunger && tLoneliness) {
				messages = KittenMessageSolver.messagesCombined(kittenHunger, kittenLoneliness);
				kittenEvents++;
			}
			// just hungry
			else if (tHunger) {
				messages = KittenMessageSolver.messagesHunger(kittenHunger);
				kittenEvents++;
			}
			// just lonely
			else if (tLoneliness) {
				messages = KittenMessageSolver.messagesLoneliness(kittenLoneliness);
				kittenEvents++;
			}

			for (String message : messages) {
				player.message(message);
			}

			// kitten runs off - reset counters
			if (kittenHunger >= 4*BASE_FACTOR || kittenLoneliness >= 4*BASE_FACTOR) {
				int totalReleased = 1;
				if (player.getCache().hasKey("kittens_released")) {
					totalReleased += player.getCache().getInt("kittens_released");
				}
				player.getCache().set("kittens_released", totalReleased);
				player.getCarriedItems().remove(new Item(ItemId.KITTEN.id()));
				kittenEvents = kittenHunger = kittenLoneliness = 0;
			}
			// kitten grows to cat - replace and reset counters
			else if (kittenEvents >= 32) {
				int totalRaised = 1;
				if (player.getCache().hasKey("kittens_raised")) {
					totalRaised += player.getCache().getInt("kittens_raised");
				}

				player.getCarriedItems().remove(new Item(ItemId.KITTEN.id()));
				player.getCarriedItems().getInventory().add(new Item(ItemId.CAT.id()));
				player.getCache().set("kittens_raised", totalRaised);
				kittenEvents = kittenHunger = kittenLoneliness = 0;
				mes("you're kitten has grown into a healthy cat");
				delay(2);
				mes("it can hunt for its self now");
				delay(2);
			}

			state.setEvents(kittenEvents);
			state.setHunger(kittenHunger);
			state.setLoneliness(kittenLoneliness);
			state.saveState(player);
		}
	}

	@Override
	public boolean blockUseInv(Player player, Integer invIndex, Item item1, Item item2) {
		return isFoodOnCat(item1, item2) || isEntertainmentForCat(item1, item2);
	}

	@Override
	public void onUseInv(Player player, Integer invIndex, Item item1, Item item2) {
		if (isFoodOnCat(item1, item2) || isEntertainmentForCat(item1, item2)) {
			boolean isGrownCat = item1.getCatalogId() != ItemId.KITTEN.id() && item2.getCatalogId() != ItemId.KITTEN.id();
			Item item;
			if (isGrownCat) {
				item = item1.getCatalogId() == ItemId.CAT.id() ? item2 : item1;
			} else {
				item = item1.getCatalogId() == ItemId.KITTEN.id() ? item2 : item1;
			}
			if (isEntertainmentForCat(item1, item2)) {
				entertainCat(item, player, isGrownCat);
			} else if (isFoodOnCat(item1, item2)) {
				feedCat(item, player, isGrownCat);
			}
		}
	}

	private boolean isEntertainmentForCat(Item item1, Item item2) {
		return compareItemsIds(item1, item2, ItemId.KITTEN.id(), ItemId.BALL_OF_WOOL.id())
				|| compareItemsIds(item1, item2, ItemId.CAT.id(), ItemId.BALL_OF_WOOL.id())
				|| compareItemsIds(item1, item2, ItemId.KITTEN.id(), ItemId.WOOL.id())
				|| compareItemsIds(item1, item2, ItemId.CAT.id(), ItemId.WOOL.id());
	}

	private boolean isFoodOnCat(Item item1, Item item2) {
		return ((item2.getCatalogId() == ItemId.KITTEN.id() || item2.getCatalogId() == ItemId.CAT.id()) && inArray(item1.getCatalogId(), ItemId.MILK.id(), ItemId.RAW_SHRIMP.id(), ItemId.RAW_SARDINE.id(), ItemId.SEASONED_SARDINE.id(),
				ItemId.SARDINE.id(), ItemId.RAW_ANCHOVIES.id(), ItemId.RAW_TROUT.id(), ItemId.TROUT.id(),
				ItemId.RAW_SALMON.id(), ItemId.SALMON.id(), ItemId.RAW_TUNA.id(), ItemId.TUNA.id())) ||
				((item1.getCatalogId() == ItemId.KITTEN.id() || item1.getCatalogId() == ItemId.CAT.id()) && inArray(item2.getCatalogId(), ItemId.MILK.id(), ItemId.RAW_SHRIMP.id(), ItemId.RAW_SARDINE.id(), ItemId.SEASONED_SARDINE.id(),
				ItemId.SARDINE.id(), ItemId.RAW_ANCHOVIES.id(), ItemId.RAW_TROUT.id(), ItemId.TROUT.id(),
				ItemId.RAW_SALMON.id(), ItemId.SALMON.id(), ItemId.RAW_TUNA.id(), ItemId.TUNA.id()));
	}

	@Override
	public boolean blockUseNpc(Player player, Npc n, Item item) {
		//only small rats
		return (item.getCatalogId() == ItemId.KITTEN.id() || item.getCatalogId() == ItemId.CAT.id()) && n.getID() == NpcId.RAT_WITCHES_POTION.id();
	}

	@Override
	public void onUseNpc(Player player, Npc n, Item item) {
		if (item.getCatalogId() == ItemId.KITTEN.id() && n.getID() == NpcId.RAT_WITCHES_POTION.id()) {
			player.message("it pounces on the rat...");
			if (DataConversions.random(0,9) == 0) {
				n.face(player);
				delay();
				n.remove();
				delay(2);
				//possibly non kosher
				mes("...and quickly gobbles it up");
				delay(3);
				mes("it returns to your satchel licking it's paws");
				delay(3);

				reduceKittensLoneliness(player);
			}
		} else if (item.getCatalogId() == ItemId.CAT.id() && n.getID() == NpcId.RAT_WITCHES_POTION.id()) {
			player.message("the cat pounces on the rat...");
			n.face(player);
			delay();
			n.remove();
			delay(2);
			mes("...and quickly gobbles it up");
			delay(3);
			mes("it returns to your satchel licking it's paws");
			delay(3);
		}
	}
}
