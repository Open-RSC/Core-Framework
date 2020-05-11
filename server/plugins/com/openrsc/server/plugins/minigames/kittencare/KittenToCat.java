package com.openrsc.server.plugins.minigames.kittencare;

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
			player.getCarriedItems().remove(new Item(ItemId.KITTEN.id()));
			mes(player, player.getWorld().getServer().getConfig().GAME_TICK * 2, "you drop the kitten");
			mes(player, 0, "it's upset and runs away");
		}

		KittenState state = new KittenState();
		state.saveState(player);
	}

	@Override
	public boolean blockOpInv(Player player, Integer invIndex, Item item, String command) {
		return item.getCatalogId() == ItemId.KITTEN.id()
			|| (item.getCatalogId() == ItemId.CAT.id() && player.getWorld().getServer().getConfig().WANT_EXTENDED_CATS_BEHAVIOR);
	}

	@Override
	public void onOpInv(Player player, Integer invIndex, Item item, String command) {
		if (item.getCatalogId() == ItemId.KITTEN.id()) {
			mes(player, "you softly stroke the kitten",
				"@yel@kitten:..purr..purr..");
			mes(player, player.getWorld().getServer().getConfig().GAME_TICK, "the kitten appreciates the attention");

			reduceKittensLoneliness(player);
		} else if (item.getCatalogId() == ItemId.CAT.id() && player.getWorld().getServer().getConfig().WANT_EXTENDED_CATS_BEHAVIOR) {
			mes(player, "you softly stroke the cat",
				"@yel@cat:..purr..purr..");
			mes(player, player.getWorld().getServer().getConfig().GAME_TICK, "it appreciates the attention");
		}
	}

	public void entertainCat(Item item, Player player, boolean isGrown) {
		if (item.getCatalogId() == ItemId.BALL_OF_WOOL.id()) {
			if (!isGrown) {
				mes(player, "your kitten plays around with the ball of wool",
						"it seems to love pouncing on it");

				reduceKittensLoneliness(player);
			} else {
				mes(player, "your cat plays around with the wool",
						"it seems to be enjoying itself");
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
				mes(player, "you give the kitten the milk",
						"the kitten quickly laps it up then licks his paws");
			} else {
				mes(player, "you give the cat the milk",
						"the kitten quickly laps it up then licks his paws");
			}
			feeded = true;
			break;
		case RAW_SHRIMP:
		case RAW_SARDINE:
		case SEASONED_SARDINE:
		case SARDINE:
		case RAW_HERRING:
		case RAW_ANCHOVIES:
		case RAW_TROUT:
		case TROUT:
		case RAW_SALMON:
		case SALMON:
		case RAW_TUNA:
		case TUNA:
			player.getCarriedItems().remove(new Item(item.getCatalogId()));
			if(!isGrown) {
				mes(player, "you give the kitten the " + item.getDef(player.getWorld()).getName(),
						"the kitten quickly eats it up then licks his paws");
			} else {
				mes(player, "you give the cat the " + item.getDef(player.getWorld()).getName(),
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
				player.getCarriedItems().remove(new Item(ItemId.KITTEN.id()));
				kittenEvents = kittenHunger = kittenLoneliness = 0;
			}
			// kitten grows to cat - replace and reset counters
			else if (kittenEvents >= 32) {
				player.getCarriedItems().remove(new Item(ItemId.KITTEN.id()));
				player.getCarriedItems().getInventory().add(new Item(ItemId.CAT.id()));
				kittenEvents = kittenHunger = kittenLoneliness = 0;
				mes(player, player.getWorld().getServer().getConfig().GAME_TICK * 2, "you're kitten has grown into a healthy cat",
						"it can hunt for its self now");
			}

			state.setEvents(kittenEvents);
			state.setHunger(kittenHunger);
			state.setLoneliness(kittenLoneliness);
			state.saveState(player);
		}
	}

	@Override
	public boolean blockUseInv(Player player, Integer invIndex, Item item1, Item item2) {
		return isFoodOnCat(item1, item2) || isBallWoolOnCat(item1, item2);
	}

	@Override
	public void onUseInv(Player player, Integer invIndex, Item item1, Item item2) {
		if (isFoodOnCat(item1, item2) || isBallWoolOnCat(item1, item2)) {
			boolean isGrownCat = item1.getCatalogId() != ItemId.KITTEN.id() && item2.getCatalogId() != ItemId.KITTEN.id();
			Item item;
			if (isGrownCat) {
				item = item1.getCatalogId() == ItemId.CAT.id() ? item2 : item1;
			} else {
				item = item1.getCatalogId() == ItemId.KITTEN.id() ? item2 : item1;
			}
			if (isBallWoolOnCat(item1, item2)) {
				entertainCat(item, player, isGrownCat);
			} else if (isFoodOnCat(item1, item2)) {
				feedCat(item, player, isGrownCat);
			}
		}
	}

	private boolean isBallWoolOnCat(Item item1, Item item2) {
		return compareItemsIds(item1, item2, ItemId.KITTEN.id(), ItemId.BALL_OF_WOOL.id())
				|| compareItemsIds(item1, item2, ItemId.CAT.id(), ItemId.BALL_OF_WOOL.id());
	}

	private boolean isFoodOnCat(Item item1, Item item2) {
		return ((item2.getCatalogId() == ItemId.KITTEN.id() || item2.getCatalogId() == ItemId.CAT.id()) && inArray(item1.getCatalogId(), ItemId.MILK.id(), ItemId.RAW_SHRIMP.id(), ItemId.RAW_SARDINE.id(), ItemId.SEASONED_SARDINE.id(),
				ItemId.SARDINE.id(), ItemId.RAW_HERRING.id(), ItemId.RAW_ANCHOVIES.id(), ItemId.RAW_TROUT.id(),
				ItemId.TROUT.id(), ItemId.RAW_SALMON.id(), ItemId.RAW_TUNA.id(), ItemId.TUNA.id())) ||
				((item1.getCatalogId() == ItemId.KITTEN.id() || item1.getCatalogId() == ItemId.CAT.id()) && inArray(item2.getCatalogId(), ItemId.MILK.id(), ItemId.RAW_SHRIMP.id(), ItemId.RAW_SARDINE.id(), ItemId.SEASONED_SARDINE.id(),
				ItemId.SARDINE.id(), ItemId.RAW_HERRING.id(), ItemId.RAW_ANCHOVIES.id(), ItemId.RAW_TROUT.id(),
				ItemId.TROUT.id(), ItemId.RAW_SALMON.id(), ItemId.SALMON.id(), ItemId.RAW_TUNA.id(), ItemId.TUNA.id()));
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
				delay(player.getWorld().getServer().getConfig().GAME_TICK);
				n.remove();
				delay(player.getWorld().getServer().getConfig().GAME_TICK * 2);
				//possibly non kosher
				mes(player, player.getWorld().getServer().getConfig().GAME_TICK * 3, "...and quickly gobbles it up",
						"it returns to your satchel licking it's paws");

				reduceKittensLoneliness(player);
			}
		} else if (item.getCatalogId() == ItemId.CAT.id() && n.getID() == NpcId.RAT_WITCHES_POTION.id()) {
			player.message("the cat pounces on the rat...");
			n.face(player);
			delay(player.getWorld().getServer().getConfig().GAME_TICK);
			n.remove();
			delay(player.getWorld().getServer().getConfig().GAME_TICK * 2);
			mes(player, player.getWorld().getServer().getConfig().GAME_TICK * 3, "...and quickly gobbles it up",
					"it returns to your satchel licking it's paws");
		}
	}
}
