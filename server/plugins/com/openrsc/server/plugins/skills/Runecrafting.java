package com.openrsc.server.plugins.skills;

import com.openrsc.server.Constants;
import com.openrsc.server.event.custom.BatchEvent;
import com.openrsc.server.external.EntityHandler;
import com.openrsc.server.external.ItemId;
import com.openrsc.server.external.ObjectRunecraftingDef;
import com.openrsc.server.model.Skills.SKILLS;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.InvUseOnObjectListener;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnObjectExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;

import static com.openrsc.server.plugins.Functions.*;
public class Runecrafting implements ObjectActionListener, ObjectActionExecutiveListener, InvUseOnObjectExecutiveListener,InvUseOnObjectListener {


	@Override
	public boolean blockObjectAction(GameObject obj, String command, Player player) {
		final ObjectRunecraftingDef def = EntityHandler.getObjectRunecraftingDef(obj.getID());
		if (def == null)
			return false;

		switch (ItemId.getById(def.getRuneId()))
		{
			case AIR_RUNE:
			case MIND_RUNE:
			case WATER_RUNE:
			case EARTH_RUNE:
			case FIRE_RUNE:
			case BODY_RUNE:
			case COSMIC_RUNE:
			case CHAOS_RUNE:
			case NATURE_RUNE:
			case LAW_RUNE:
			case DEATH_RUNE:
			case BLOOD_RUNE:
				return true;
			default:
				return false;
		}
	}

	@Override
	public void onObjectAction(GameObject obj, String command, Player player) {

		final ObjectRunecraftingDef def = EntityHandler.getObjectRunecraftingDef(obj.getID());

		if (def == null) {
			return;
		}

		if (player.getQuestStage(Constants.Quests.RUNE_MYSTERIES) != -1)
		{
			player.message("You need to complete Rune Mysteries first. How did you get here?");
			return;
		}

		if (!hasItem(player,ItemId.RUNE_ESSENCE.id())){
			player.message("You have no rune essence to bind.");
			return;
		}
		else {
			if (player.getSkills().getLevel(SKILLS.RUNECRAFT.id()) < def.getRequiredLvl()) {
				player.message("You require more skill to use this altar.");
				return;
			}
			player.message("You bind the temple's power into " + def.getRuneName() + " runes.");
		}
			player.setBatchEvent(new BatchEvent(player, 100, 1030, false) {
			public void action() {
				if (!hasItem(player, ItemId.RUNE_ESSENCE.id())) {
					interrupt();
					return;
				}
				removeItem(player, ItemId.RUNE_ESSENCE.id(), 1);
				addItem(player, def.getRuneId(), getRuneMultiplier(player,def.getRuneId()));
				player.incExp(SKILLS.RUNECRAFT.id(), def.getExp(), true);
			}
		});

	}

	@Override
	public boolean blockInvUseOnObject(GameObject obj, Item item, Player player) {

		if (item.getID() == ItemId.AIR_TALISMAN.id() && obj.getID() == 1190)
			return true;
		else if (item.getID() == ItemId.MIND_TALISMAN.id() && obj.getID() == 1192)
			return true;
		else if (item.getID() == ItemId.WATER_TALISMAN.id() && obj.getID() == 1194)
			return true;
		else if (item.getID() == ItemId.EARTH_TALISMAN.id() && obj.getID() == 1196)
			return true;
		else if (item.getID() == ItemId.FIRE_TALISMAN.id() && obj.getID() == 1198)
			return true;
		else if (item.getID() == ItemId.BODY_TALISMAN.id() && obj.getID() == 1200)
			return true;
		else if (item.getID() == ItemId.COSMIC_TALISMAN.id() && obj.getID() == 1202)
			return true;
		else if (item.getID() == ItemId.CHAOS_TALISMAN.id() && obj.getID() == 1204)
			return true;
		else if (item.getID() == ItemId.NATURE_TALISMAN.id() && obj.getID() == 1206)
			return true;
		else if (item.getID() == ItemId.LAW_TALISMAN.id() && obj.getID() == 1208)
			return true;
		else if (item.getID() == ItemId.DEATH_TALISMAN.id() && obj.getID() == 1210)
			return true;
		else if (item.getID() == ItemId.BLOOD_TALISMAN.id() && obj.getID() == 1212)
			return true;
		return false;
	}

	@Override
	public void onInvUseOnObject(GameObject obj, Item item, Player p) {

		p.message("You feel a powerful force take hold of you...");
		sleep(500);

		switch(ItemId.getById(item.getID()))
		{
			case AIR_TALISMAN:
				p.teleport(985,19,false);
				break;
			case MIND_TALISMAN:
				p.teleport(934,14,false);
				break;
			case WATER_TALISMAN:
				p.teleport(986,63,false);
				break;
			case EARTH_TALISMAN:
				p.teleport(934,70,false);
				break;
			case FIRE_TALISMAN:
				p.teleport(887,26,false);
				break;
			case BODY_TALISMAN:
				p.teleport(893,71,false);
				break;
			case COSMIC_TALISMAN:
				p.teleport(839,26,false);
				break;
			case CHAOS_TALISMAN:
				p.teleport(934,14,false);
				break;
			case NATURE_TALISMAN:
				p.teleport(787,29,false);
				break;
			case LAW_TALISMAN:
				p.teleport(790,69,false);
				break;
			case DEATH_TALISMAN:
				p.teleport(934,14,false);
				break;
			case BLOOD_TALISMAN:
				p.teleport(743,22,false);
				break;
		}
	}

	public int getRuneMultiplier(Player p, int runeId) {
		int retVal;

		switch(ItemId.getById(runeId)) {
			case AIR_RUNE:
				retVal =  (int)Math.floor(getCurrentLevel(p, SKILLS.RUNECRAFT.id())/11.0)+1;
				if (retVal > 10)
					retVal = 10;
				break;
			case MIND_RUNE:
				retVal = (int)Math.floor(getCurrentLevel(p, SKILLS.RUNECRAFT.id())/14.0)+1;
				if (retVal > 8)
					retVal = 8;
				break;
			case WATER_RUNE:
				retVal = (int)Math.floor(getCurrentLevel(p, SKILLS.RUNECRAFT.id())/19.0)+1;
				if (retVal > 6)
					retVal = 6;
				break;
			case EARTH_RUNE:
				retVal = (int)Math.floor(getCurrentLevel(p, SKILLS.RUNECRAFT.id())/26.0)+1;
				if (retVal > 4)
					retVal = 4;
				break;
			case FIRE_RUNE:
				retVal = (int)Math.floor(getCurrentLevel(p, SKILLS.RUNECRAFT.id())/35.0)+1;
				if (retVal > 3)
					retVal = 3;
				break;
			case BODY_RUNE:
				retVal = (int)Math.floor(getCurrentLevel(p, SKILLS.RUNECRAFT.id())/46.0)+1;
				if (retVal > 2)
					retVal = 2;
				break;
			default:
				retVal = 1;
		}

		return retVal;
	}
}
