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
	public void handleReward(Player p) {
		//mini-quest complete handled already
	}

	@Override
	public boolean blockDropObj(Player p, Item i, Boolean fromInventory) {
		return i.getCatalogId() == ItemId.KITTEN.id();
	}

	@Override
	public void onDropObj(Player p, Item i, Boolean fromInventory) {
		if (i.getCatalogId() == ItemId.KITTEN.id()) {
			removeItem(p, ItemId.KITTEN.id(), 1);
			message(p, 1200, "you drop the kitten");
			message(p, 0, "it's upset and runs away");
		}

		KittenState state = new KittenState();
		state.saveState(p);
	}

	@Override
	public boolean blockOpInv(Item item, Player p, String command) {
		return item.getCatalogId() == ItemId.KITTEN.id();
	}

	@Override
	public void onOpInv(Item item, Player p, String command) {
		if (item.getCatalogId() == ItemId.KITTEN.id()) {
			message(p, "you softly stroke the kitten",
				"@yel@kitten:..purr..purr..");
			message(p, 600, "the kitten appreciates the attention");

			reduceKittensLoneliness(p);
		}
	}

	public void entertainCat(Item item, Player p, boolean isGrown) {
		if (item.getCatalogId() == ItemId.BALL_OF_WOOL.id()) {
			if (!isGrown) {
				message(p, "your kitten plays around with the ball of wool",
						"it seems to love pouncing on it");

				reduceKittensLoneliness(p);
			} else {
				message(p, "your cat plays around with the wool",
						"it seems to be enjoying itself");
			}
		}
	}

	public void feedCat(Item item, Player p, boolean isGrown) {
		boolean feeded = false;
		switch (ItemId.getById(item.getCatalogId())) {
		case MILK:
			p.getCarriedItems().getInventory().replace(item.getCatalogId(), ItemId.BUCKET.id());
			if(!isGrown) {
				message(p, "you give the kitten the milk",
						"the kitten quickly laps it up then licks his paws");
			} else {
				message(p, "you give the cat the milk",
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
		case RAW_TUNA:
		case TUNA:
			removeItem(p, item.getCatalogId(), 1);
			if(!isGrown) {
				message(p, "you give the kitten the " + item.getDef(p.getWorld()).getName(),
						"the kitten quickly eats it up then licks his paws");
			} else {
				message(p, "you give the cat the " + item.getDef(p.getWorld()).getName(),
						"it quickly eat's them up and licks its paws");
			}
			feeded = true;
			break;
		default:
			break;
		}

		if (feeded && !isGrown)
			reduceKittensHunger(p);
	}

	private void reduceKittensLoneliness(Player p) {
		KittenState state = new KittenState();
		state.loadState(p);
		int loneliness = state.getLoneliness();
		if (loneliness >= BASE_FACTOR) {
			state.setLoneliness(loneliness - BASE_FACTOR);
			state.saveState(p);
		}
	}

	private void reduceKittensHunger(Player p) {
		KittenState state = new KittenState();
		state.loadState(p);
		int hunger = state.getHunger();
		if (hunger >= BASE_FACTOR) {
			state.setHunger(hunger - BASE_FACTOR);
			state.saveState(p);
		}
	}

	@Override
	public boolean blockCatGrowth(Player p) {
		return p.getCarriedItems().hasCatalogID(ItemId.KITTEN.id());
	}

	@Override
	public void onCatGrowth(Player p) {
		if (p.getCarriedItems().hasCatalogID(ItemId.KITTEN.id())) {
			// no events in memory, check in cache
			KittenState state = new KittenState();
			state.loadState(p);
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
				p.message(message);
			}

			// kitten runs off - reset counters
			if (kittenHunger >= 4*BASE_FACTOR || kittenLoneliness >= 4*BASE_FACTOR) {
				p.getCarriedItems().remove(ItemId.KITTEN.id(), 1);
				kittenEvents = kittenHunger = kittenLoneliness = 0;
			}
			// kitten grows to cat - replace and reset counters
			else if (kittenEvents >= 32) {
				p.getCarriedItems().getInventory().replace(ItemId.KITTEN.id(), ItemId.CAT.id());
				kittenEvents = kittenHunger = kittenLoneliness = 0;
				message(p, 1200, "you're kitten has grown into a healthy cat",
						"it can hunt for its self now");
			}

			state.setEvents(kittenEvents);
			state.setHunger(kittenHunger);
			state.setLoneliness(kittenLoneliness);
			state.saveState(p);
		}
	}

	@Override
	public boolean blockUseInv(Player p, Item item1, Item item2) {
		return isFoodOnCat(item1, item2) || isBallWoolOnCat(item1, item2);
	}

	@Override
	public void onUseInv(Player p, Item item1, Item item2) {
		if (isFoodOnCat(item1, item2) || isBallWoolOnCat(item1, item2)) {
			boolean isGrownCat = item1.getCatalogId() != ItemId.KITTEN.id() && item2.getCatalogId() != ItemId.KITTEN.id();
			Item item;
			if (isGrownCat) {
				item = item1.getCatalogId() == ItemId.CAT.id() ? item2 : item1;
			} else {
				item = item1.getCatalogId() == ItemId.KITTEN.id() ? item2 : item1;
			}
			if (isBallWoolOnCat(item1, item2)) {
				entertainCat(item, p, isGrownCat);
			} else if (isFoodOnCat(item1, item2)) {
				feedCat(item, p, isGrownCat);
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
				ItemId.TROUT.id(), ItemId.RAW_SALMON.id(), ItemId.RAW_TUNA.id(), ItemId.TUNA.id()));
	}

	@Override
	public boolean blockUseNpc(Player p, Npc n, Item item) {
		//only small rats
		return (item.getCatalogId() == ItemId.KITTEN.id() || item.getCatalogId() == ItemId.CAT.id()) && n.getID() == NpcId.RAT_WITCHES_POTION.id();
	}

	@Override
	public void onUseNpc(Player p, Npc n, Item item) {
		if (item.getCatalogId() == ItemId.KITTEN.id() && n.getID() == NpcId.RAT_WITCHES_POTION.id()) {
			p.message("it pounces on the rat...");
			if (DataConversions.random(0,9) == 0) {
				n.face(p);
				sleep(600);
				n.remove();
				p.setBusyTimer(1200);
				sleep(1200);
				//possibly non kosher
				message(p, 1800, "...and quickly gobbles it up",
						"it returns to your satchel licking it's paws");

				reduceKittensLoneliness(p);
			}
		} else if (item.getCatalogId() == ItemId.CAT.id() && n.getID() == NpcId.RAT_WITCHES_POTION.id()) {
			p.message("the cat pounces on the rat...");
			n.face(p);
			sleep(600);
			n.remove();
			p.setBusyTimer(1200);
			sleep(1200);
			message(p, 1800, "...and quickly gobbles it up",
					"it returns to your satchel licking it's paws");
		}
	}
}
