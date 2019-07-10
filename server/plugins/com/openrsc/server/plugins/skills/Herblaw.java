package com.openrsc.server.plugins.skills;

import com.openrsc.server.Constants;
import com.openrsc.server.Constants.Quests;
import com.openrsc.server.event.custom.BatchEvent;
import com.openrsc.server.external.EntityHandler;
import com.openrsc.server.external.ItemHerbDef;
import com.openrsc.server.external.ItemHerbSecond;
import com.openrsc.server.external.ItemId;
import com.openrsc.server.external.ItemUnIdentHerbDef;
import com.openrsc.server.model.Skills;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.InvActionListener;
import com.openrsc.server.plugins.listeners.action.InvUseOnItemListener;
import com.openrsc.server.plugins.listeners.executive.InvActionExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnItemExecutiveListener;
import com.openrsc.server.util.rsc.Formulae;
import com.openrsc.server.util.rsc.MessageType;

import java.util.concurrent.atomic.AtomicReference;

import static com.openrsc.server.plugins.Functions.*;

public class Herblaw implements InvActionListener, InvUseOnItemListener,
	InvActionExecutiveListener, InvUseOnItemExecutiveListener {

	@Override
	public void onInvAction(final Item item, Player player) {
		if (item.getDef().getCommand().equalsIgnoreCase("Identify")) {
			handleHerbIdentify(item, player);
		}
	}

	public boolean blockInvAction(final Item i, Player p) {
		return i.getDef().getCommand().equalsIgnoreCase("Identify");
	}

	private boolean handleHerbIdentify(final Item item, Player player) {
		if (!Constants.GameServer.MEMBER_WORLD) {
			player.sendMemberErrorMessage();
			return false;
		}
		ItemUnIdentHerbDef herb = item.getUnIdentHerbDef();
		if (herb == null) {
			return false;
		}
		if (player.getSkills().getLevel(Skills.HERBLAW) < herb.getLevelRequired()) {
			player.message("You cannot identify this herb");
			player.message("you need a higher herblaw level");
			return false;
		}
		if (player.getQuestStage(Constants.Quests.DRUIDIC_RITUAL) != -1) {
			player.message("You need to complete Druidic ritual quest first");
			return false;
		}
		player.setBatchEvent(new BatchEvent(player, 600, "Herblaw Identify Herb",
			Formulae.getRepeatTimes(player, Skills.HERBLAW), false) {
			public void action() {
				if (!owner.getInventory().hasItemId(item.getID())) {
					interrupt();
					return;
				}
				if (owner.inCombat()) {
					interrupt();
					return;
				}
				ItemUnIdentHerbDef herb = item.getUnIdentHerbDef();
				Item newItem = new Item(herb.getNewId());
				owner.getInventory().remove(item);
				owner.getInventory().add(newItem);
				owner.message("This herb is " + newItem.getDef().getName());
				owner.incExp(Skills.HERBLAW, herb.getExp(), true);
				owner.setBusy(false);
			}
		});
		return true;
	}

	@Override
	public void onInvUseOnItem(Player player, Item item, Item usedWith) {
		ItemHerbSecond secondDef = null;
		if ((secondDef = EntityHandler.getItemHerbSecond(item.getID(), usedWith
			.getID())) != null) {
			doHerbSecond(player, item, usedWith, secondDef, false);
		} else if ((secondDef = EntityHandler.getItemHerbSecond(usedWith
			.getID(), item.getID())) != null) {
			doHerbSecond(player, usedWith, item, secondDef, true);
		} else if (item.getID() == ItemId.PESTLE_AND_MORTAR.id()) {
			doGrind(player, item, usedWith);
		} else if (usedWith.getID() == ItemId.PESTLE_AND_MORTAR.id()) {
			doGrind(player, usedWith, item);
		} else if (item.getID() == ItemId.VIAL.id()) {
			doHerblaw(player, item, usedWith);
		} else if (usedWith.getID() == ItemId.VIAL.id()) {
			doHerblaw(player, usedWith, item);
		} else if (item.getID() == ItemId.UNFINISHED_OGRE_POTION.id() && usedWith.getID() == ItemId.GROUND_BAT_BONES.id()) {
			makeLiquid(player, usedWith, item, true);
		} else if (item.getID() == ItemId.GROUND_BAT_BONES.id() && usedWith.getID() == ItemId.UNFINISHED_OGRE_POTION.id()) {
			makeLiquid(player, item, usedWith, false);
		} else if (item.getID() == ItemId.UNFINISHED_POTION.id() && (usedWith.getID() == ItemId.GROUND_BAT_BONES.id() || usedWith.getID() == ItemId.GUAM_LEAF.id())) {
			makeLiquid(player, item, usedWith, false);
		} else if (usedWith.getID() == ItemId.UNFINISHED_POTION.id() && (item.getID() == ItemId.GROUND_BAT_BONES.id() || item.getID() == ItemId.GUAM_LEAF.id())) {
			makeLiquid(player, usedWith, item, true);
		} else if (usedWith.getID() == ItemId.NITROGLYCERIN.id() && item.getID() == ItemId.AMMONIUM_NITRATE.id()
				|| usedWith.getID() == ItemId.AMMONIUM_NITRATE.id() && item.getID() == ItemId.NITROGLYCERIN.id()) {
			if (player.getSkills().getLevel(Skills.HERBLAW) < 10) {
				player.message("You need to have a herblaw level of 10 or over to mix this liquid");
				return;
			}
			if (player.getQuestStage(Constants.Quests.DRUIDIC_RITUAL) != -1) {
				player.message("You need to complete Druidic ritual quest first");
				return;
			}
			player.incExp(Skills.HERBLAW, 20, true);
			player.playerServerMessage(MessageType.QUEST, "You mix the nitrate powder into the liquid");
			player.message("It has produced a foul mixture");
			showBubble(player, new Item(ItemId.AMMONIUM_NITRATE.id()));
			player.getInventory().remove(ItemId.AMMONIUM_NITRATE.id(), 1);
			player.getInventory().replace(ItemId.NITROGLYCERIN.id(), ItemId.MIXED_CHEMICALS_1.id());
		} else if (usedWith.getID() == ItemId.GROUND_CHARCOAL.id() && item.getID() == ItemId.MIXED_CHEMICALS_1.id()
				|| usedWith.getID() == ItemId.MIXED_CHEMICALS_1.id() && item.getID() == ItemId.GROUND_CHARCOAL.id()) {
			if (player.getSkills().getLevel(Skills.HERBLAW) < 10) {
				player.message("You need to have a herblaw level of 10 or over to mix this liquid");
				return;
			}
			if (player.getQuestStage(Constants.Quests.DRUIDIC_RITUAL) != -1) {
				player.message("You need to complete Druidic ritual quest first");
				return;
			}
			player.incExp(Skills.HERBLAW, 25, true);
			player.playerServerMessage(MessageType.QUEST, "You mix the charcoal into the liquid");
			player.message("It has produced an even fouler mixture");
			showBubble(player, new Item(ItemId.GROUND_CHARCOAL.id()));
			player.getInventory().remove(ItemId.GROUND_CHARCOAL.id(), 1);
			player.getInventory().replace(ItemId.MIXED_CHEMICALS_1.id(), ItemId.MIXED_CHEMICALS_2.id());
		} else if (usedWith.getID() == ItemId.ARCENIA_ROOT.id() && item.getID() == ItemId.MIXED_CHEMICALS_2.id()
				|| usedWith.getID() == ItemId.MIXED_CHEMICALS_2.id() && item.getID() == ItemId.ARCENIA_ROOT.id()) {
			if (player.getSkills().getLevel(Skills.HERBLAW) < 10) {
				player.message("You need to have a herblaw level of 10 or over to mix this liquid");
				return;
			}
			if (player.getQuestStage(Constants.Quests.DRUIDIC_RITUAL) != -1) {
				player.message("You need to complete Druidic ritual quest first");
				return;
			}
			player.incExp(Skills.HERBLAW, 30, true);
			player.message("You mix the root into the mixture");
			player.message("You produce a potentially explosive compound...");
			showBubble(player, new Item(ItemId.ARCENIA_ROOT.id()));
			player.getInventory().remove(ItemId.ARCENIA_ROOT.id(), 1);
			player.getInventory().replace(ItemId.MIXED_CHEMICALS_2.id(), ItemId.EXPLOSIVE_COMPOUND.id());
			playerTalk(player, null, "Excellent this looks just right");
		} else if (usedWith.getID() == ItemId.UNFINISHED_HARRALANDER_POTION.id() && item.getID() == ItemId.BLAMISH_SNAIL_SLIME.id()
				|| usedWith.getID() == ItemId.BLAMISH_SNAIL_SLIME.id() && item.getID() == ItemId.UNFINISHED_HARRALANDER_POTION.id()) {
			if (player.getSkills().getLevel(Skills.HERBLAW) < 25) {
				player.message("You need a level of 25 herblaw to mix this potion");
				return;
			}
			if (player.getQuestStage(Constants.Quests.DRUIDIC_RITUAL) != -1) {
				player.message("You need to complete Druidic ritual quest first");
				return;
			}
			player.incExp(Skills.HERBLAW, 320, true);
			player.message("You mix the slime into your potion");
			player.getInventory().remove(ItemId.UNFINISHED_HARRALANDER_POTION.id(), 1);
			player.getInventory().replace(ItemId.BLAMISH_SNAIL_SLIME.id(), ItemId.BLAMISH_OIL.id());
		} else if (usedWith.getID() == ItemId.SNAKES_WEED_SOLUTION.id() && item.getID() == ItemId.ARDRIGAL.id()
				|| usedWith.getID() == ItemId.ARDRIGAL.id() && item.getID() == ItemId.SNAKES_WEED_SOLUTION.id()) {
			if (player.getSkills().getLevel(Skills.HERBLAW) < 45) {
				player.message("You need to have a herblaw level of 45 or over to mix this potion");
				return;
			}
			if (player.getQuestStage(Constants.Quests.DRUIDIC_RITUAL) != -1) {
				player.message("You need to complete Druidic ritual quest first");
				return;
			}
			//player needs to have learned secret from gujuo
			if (player.getQuestStage(Quests.LEGENDS_QUEST) >= 0 && player.getQuestStage(Quests.LEGENDS_QUEST) < 7) {
				player.message("You're not quite sure what effect this will have.");
				player.message("You decide against experimenting.");
				return;
			}
			player.message("You add the Ardrigal to the Snakesweed Solution.");
			player.message("The mixture seems to bubble slightly with a strange effervescence...");
			player.getInventory().remove(ItemId.ARDRIGAL.id(), 1);
			player.getInventory().replace(ItemId.SNAKES_WEED_SOLUTION.id(), ItemId.GUJUO_POTION.id());
		} else if (usedWith.getID() == ItemId.ARDRIGAL_SOLUTION.id() && item.getID() == ItemId.SNAKE_WEED.id()
				|| usedWith.getID() == ItemId.SNAKE_WEED.id() && item.getID() == ItemId.ARDRIGAL_SOLUTION.id()) {
			if (player.getSkills().getLevel(Skills.HERBLAW) < 45) {
				player.message("You need to have a herblaw level of 45 or over to mix this potion");
				return;
			}
			if (player.getQuestStage(Constants.Quests.DRUIDIC_RITUAL) != -1) {
				player.message("You need to complete Druidic ritual quest first");
				return;
			}
			//player needs to have learned secret from gujuo
			if (player.getQuestStage(Quests.LEGENDS_QUEST) >= 0 && player.getQuestStage(Quests.LEGENDS_QUEST) < 7) {
				player.message("You're not quite sure what effect this will have.");
				player.message("You decide against experimenting.");
				return;
			}
			player.message("You add the Snake Weed to the Ardrigal solution.");
			player.message("The mixture seems to bubble slightly with a strange effervescence...");
			player.getInventory().remove(ItemId.SNAKE_WEED.id(), 1);
			player.getInventory().replace(ItemId.ARDRIGAL_SOLUTION.id(), ItemId.GUJUO_POTION.id());
		}
	}

	public boolean blockInvUseOnItem(Player p, Item item, Item usedWith) {
		if ((EntityHandler.getItemHerbSecond(item.getID(), usedWith.getID())) != null
			|| (EntityHandler.getItemHerbSecond(usedWith.getID(), item
			.getID())) != null) {
			return true;
		} else if (item.getID() == ItemId.PESTLE_AND_MORTAR.id() || usedWith.getID() == ItemId.PESTLE_AND_MORTAR.id()) {
			return true;
		} else if (item.getID() == ItemId.VIAL.id() || usedWith.getID() == ItemId.VIAL.id()) {
			return true;
		} else if (item.getID() == ItemId.UNFINISHED_OGRE_POTION.id() && usedWith.getID() == ItemId.GROUND_BAT_BONES.id()
			|| item.getID() == ItemId.GROUND_BAT_BONES.id() && usedWith.getID() == ItemId.UNFINISHED_OGRE_POTION.id()) {
			return true;
		} else if (item.getID() == ItemId.UNFINISHED_POTION.id() && (usedWith.getID() == ItemId.GROUND_BAT_BONES.id() || usedWith.getID() == ItemId.GUAM_LEAF.id())
			|| usedWith.getID() == ItemId.UNFINISHED_POTION.id() && (item.getID() == ItemId.GROUND_BAT_BONES.id() || item.getID() == ItemId.GUAM_LEAF.id())) {
			return true;
		} else if (usedWith.getID() == ItemId.NITROGLYCERIN.id() && item.getID() == ItemId.AMMONIUM_NITRATE.id()
				|| usedWith.getID() == ItemId.AMMONIUM_NITRATE.id() && item.getID() == ItemId.NITROGLYCERIN.id()) {
			return true;
		} else if (usedWith.getID() == ItemId.GROUND_CHARCOAL.id() && item.getID() == ItemId.MIXED_CHEMICALS_1.id()
				|| usedWith.getID() == ItemId.MIXED_CHEMICALS_1.id() && item.getID() == ItemId.GROUND_CHARCOAL.id()) {
			return true;
		} else if (usedWith.getID() == ItemId.ARCENIA_ROOT.id() && item.getID() == ItemId.MIXED_CHEMICALS_2.id()
				|| usedWith.getID() == ItemId.MIXED_CHEMICALS_2.id() && item.getID() == ItemId.ARCENIA_ROOT.id()) {
			return true;
		} else if (usedWith.getID() == ItemId.UNFINISHED_HARRALANDER_POTION.id() && item.getID() == ItemId.BLAMISH_SNAIL_SLIME.id()
				|| usedWith.getID() == ItemId.BLAMISH_SNAIL_SLIME.id() && item.getID() == ItemId.UNFINISHED_HARRALANDER_POTION.id()) {
			return true;
		} else if (usedWith.getID() == ItemId.SNAKES_WEED_SOLUTION.id() && item.getID() == ItemId.ARDRIGAL.id()
				|| usedWith.getID() == ItemId.ARDRIGAL.id() && item.getID() == ItemId.SNAKES_WEED_SOLUTION.id()) {
			return true;
		} else if (usedWith.getID() == ItemId.ARDRIGAL_SOLUTION.id() && item.getID() == ItemId.SNAKE_WEED.id()
				|| usedWith.getID() == ItemId.SNAKE_WEED.id() && item.getID() == ItemId.ARDRIGAL_SOLUTION.id()) {
			return true;
		}
		return false;
	}

	private boolean doHerblaw(Player player, final Item vial,
							  final Item herb) {
		if (!Constants.GameServer.MEMBER_WORLD) {
			player.sendMemberErrorMessage();
			return false;
		}
		if (vial.getID() == ItemId.VIAL.id() && herb.getID() == ItemId.GROUND_BAT_BONES.id()) {
			player.message("You mix the ground bones into the water");
			player.message("Fizz!!!");
			playerTalk(player, null, "Oh dear, the mixture has evaporated!",
				"It's useless...");
			player.getInventory().remove(vial.getID(), 1);
			player.getInventory().remove(herb.getID(), 1);
			player.getInventory().add(new Item(ItemId.EMPTY_VIAL.id(), 1));
			return false;
		}
		if (vial.getID() == ItemId.VIAL.id() && herb.getID() == ItemId.JANGERBERRIES.id()) {
			player.message("You mix the berries into the water");
			player.getInventory().remove(vial.getID(), 1);
			player.getInventory().remove(herb.getID(), 1);
			player.getInventory().add(new Item(ItemId.UNFINISHED_POTION.id(), 1));
			return false;
		}
		if (vial.getID() == ItemId.VIAL.id() && herb.getID() == ItemId.ARDRIGAL.id()) {
			player.message("You put the ardrigal herb into the watervial.");
			player.message("You make a solution of Ardrigal.");
			player.getInventory().remove(vial.getID(), 1);
			player.getInventory().remove(herb.getID(), 1);
			player.getInventory().add(new Item(ItemId.ARDRIGAL_SOLUTION.id(), 1));
			return false;
		}
		if (vial.getID() == ItemId.VIAL.id() && herb.getID() == ItemId.SNAKE_WEED.id()) {
			player.message("You put the Snake Weed herb into the watervial.");
			player.message("You make a solution of Snake Weed.");
			player.getInventory().remove(vial.getID(), 1);
			player.getInventory().remove(herb.getID(), 1);
			player.getInventory().add(new Item(ItemId.SNAKES_WEED_SOLUTION.id(), 1));
			return false;
		}
		final ItemHerbDef herbDef = EntityHandler.getItemHerbDef(herb.getID());
		if (herbDef == null) {
			return false;
		}
		player.setBatchEvent(new BatchEvent(player, 1200, "Herblaw Make Potion",
			Formulae.getRepeatTimes(player, Skills.HERBLAW), false) {
			@Override
			public void action() {
				if (owner.getSkills().getLevel(Skills.HERBLAW) < herbDef.getReqLevel()) {
					owner.message("you need level " + herbDef.getReqLevel()
						+ " herblaw to make this potion");
					interrupt();
					return;
				}
				if (player.getQuestStage(Constants.Quests.DRUIDIC_RITUAL) != -1) {
					player.message("You need to complete Druidic ritual quest first");
					return;
				}
				if (owner.getInventory().hasItemId(vial.getID())
					&& owner.getInventory().hasItemId(herb.getID())) {
					owner.getInventory().remove(vial.getID(), 1);
					owner.getInventory().remove(herb.getID(), 1);
					owner.playSound("mix");
					owner.message("You put the " + herb.getDef().getName()
						+ " into the vial of water");
					owner.getInventory().add(
						new Item(herbDef.getPotionId(), 1));
				} else {
					interrupt();
				}
			}
		});
		return true;
	}

	private boolean doHerbSecond(Player player, final Item second,
								 final Item unfinished, final ItemHerbSecond def, final boolean isSwapped) {
		if (!Constants.GameServer.MEMBER_WORLD) {
			player.sendMemberErrorMessage();
			return false;
		}
		if (unfinished.getID() != def.getUnfinishedID()) {
			return false;
		}
		final AtomicReference<Item> bubbleItem = new AtomicReference<Item>();
		bubbleItem.set(null);
		//constraint shaman potion
		if (second.getID() == ItemId.JANGERBERRIES.id() && unfinished.getID() == ItemId.UNFINISHED_GUAM_POTION.id() &&
			(player.getQuestStage(Quests.WATCHTOWER) >= 0 && player.getQuestStage(Quests.WATCHTOWER) < 6)) {
			playerTalk(player, null, "Hmmm...perhaps I shouldn't try and mix these items together",
				"It might have unpredictable results...");
			return false;
		} else if (second.getID() == ItemId.JANGERBERRIES.id() && unfinished.getID() == ItemId.UNFINISHED_GUAM_POTION.id()) {
			if (!isSwapped) {
				bubbleItem.set(unfinished);
			} else {
				bubbleItem.set(second);
			}
		}
		player.setBatchEvent(new BatchEvent(player, 1200, "Herblaw Make Potion",
			Formulae.getRepeatTimes(player, Skills.HERBLAW), false) {
			public void action() {
				if (owner.getSkills().getLevel(Skills.HERBLAW) < def.getReqLevel()) {
					owner.message("You need a herblaw level of "
						+ def.getReqLevel() + " to make this potion");
					interrupt();
					return;
				}
				if (player.getQuestStage(Constants.Quests.DRUIDIC_RITUAL) != -1) {
					player.message("You need to complete Druidic ritual quest first");
					return;
				}
				if (owner.getInventory().hasItemId(second.getID())
					&& owner.getInventory().hasItemId(unfinished.getID())) {
					if (bubbleItem.get() != null) {
						showBubble(owner, bubbleItem.get());
					}
					owner.playSound("mix");
					owner.message("You mix the " + second.getDef().getName()
						+ " into your potion");
					owner.getInventory().remove(second.getID(), 1);
					owner.getInventory().remove(unfinished.getID(), 1);
					owner.getInventory().add(new Item(def.getPotionID(), 1));
					owner.incExp(Skills.HERBLAW, def.getExp(), true);
				} else
					interrupt();
			}
		});
		return false;
	}

	private boolean makeLiquid(Player p, final Item ingredient,
							   final Item unfinishedPot, final boolean isSwapped) {
		if (!Constants.GameServer.MEMBER_WORLD) {
			p.sendMemberErrorMessage();
			return false;
		}
		if (unfinishedPot.getID() == ItemId.UNFINISHED_POTION.id() && (ingredient.getID() == ItemId.GROUND_BAT_BONES.id() || ingredient.getID() == ItemId.GUAM_LEAF.id())
			|| ingredient.getID() == ItemId.UNFINISHED_POTION.id() && (unfinishedPot.getID() == ItemId.GROUND_BAT_BONES.id() || unfinishedPot.getID() == ItemId.GUAM_LEAF.id())) {
			p.message("You mix the liquid with the " + ingredient.getDef().getName().toLowerCase());
			p.message("Bang!!!");
			displayTeleportBubble(p, p.getX(), p.getY(), true);
			p.damage(8);
			playerTalk(p, null, "Ow!");
			p.message("You mixed this ingredients incorrectly and the mixture exploded!");
			p.getInventory().remove(unfinishedPot.getID(), 1);
			p.getInventory().remove(ingredient.getID(), 1);
			p.getInventory().add(new Item(ItemId.EMPTY_VIAL.id(), 1));
			return false;
		}
		if (unfinishedPot.getID() == ItemId.UNFINISHED_OGRE_POTION.id() && ingredient.getID() == ItemId.GROUND_BAT_BONES.id()
			|| unfinishedPot.getID() == ItemId.GROUND_BAT_BONES.id() && ingredient.getID() == ItemId.UNFINISHED_OGRE_POTION.id()) {
			if (p.getSkills().getLevel(Skills.HERBLAW) < 14) {
				p.message("You need to have a herblaw level of 14 or over to mix this liquid");
				return false;
			}
			if (p.getQuestStage(Constants.Quests.DRUIDIC_RITUAL) != -1) {
				p.message("You need to complete Druidic ritual quest first");
				return false;
			}
			if (p.getQuestStage(Quests.WATCHTOWER) >= 0 && p.getQuestStage(Quests.WATCHTOWER) < 6) {
				playerTalk(p, null, "Hmmm...perhaps I shouldn't try and mix these items together",
					"It might have unpredictable results...");
				return false;
			} else if (p.getInventory().hasItemId(ingredient.getID())
				&& p.getInventory().hasItemId(unfinishedPot.getID())) {
				if (!isSwapped) {
					showBubble(p, unfinishedPot);
				} else {
					showBubble(p, ingredient);
				}
				p.message("You mix the " + ingredient.getDef().getName().toLowerCase() + " into the liquid");
				p.message("You produce a strong potion");
				p.getInventory().remove(ingredient.getID(), 1);
				p.getInventory().remove(unfinishedPot.getID(), 1);
				p.getInventory().add(new Item(ItemId.OGRE_POTION.id(), 1));
				//the other half has been done already
				p.incExp(Skills.HERBLAW, 100, true);
			}
		}
		return false;
	}

	private boolean doGrind(Player player, final Item mortar,
							final Item item) {
		if (!Constants.GameServer.MEMBER_WORLD) {
			player.sendMemberErrorMessage();
			return false;
		}
		int newID;
		switch (ItemId.getById(item.getID())) {
			case UNICORN_HORN:
				newID = ItemId.GROUND_UNICORN_HORN.id();
				break;
			case BLUE_DRAGON_SCALE:
				newID = ItemId.GROUND_BLUE_DRAGON_SCALE.id();
				break;
			/**
			 * Quest items.
			 */
			case BAT_BONES:
				newID = ItemId.GROUND_BAT_BONES.id();
				break;
			case A_LUMP_OF_CHARCOAL:
				newID = ItemId.GROUND_CHARCOAL.id();
				player.message("You grind the charcoal to a powder");
				break;
			case CHOCOLATE_BAR:
				newID = ItemId.CHOCOLATE_DUST.id();
				break;
			/**
			 * End of Herblaw Quest Items.
			 */
			default:
				return false;
		}
		player.setBatchEvent(new BatchEvent(player, 600, "Herblaw Grind", player.getInventory().countId(item.getID()), false) {
			@Override
			public void action() {
				if (player.getInventory().remove(item) > -1) {
					if (item.getID() != ItemId.A_LUMP_OF_CHARCOAL.id()) {
						player.message("You grind the " + item.getDef().getName()
							+ " to dust");
					}
					showBubble(player, new Item(ItemId.PESTLE_AND_MORTAR.id()));
					player.getInventory().add(new Item(newID, 1));

				}
			}
			});
		return true;
	}
}
