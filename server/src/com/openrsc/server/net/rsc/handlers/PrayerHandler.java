package com.openrsc.server.net.rsc.handlers;

import com.openrsc.server.constants.IronmanMode;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.external.PrayerDef;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.entity.player.Prayers;
import com.openrsc.server.net.rsc.PayloadProcessor;
import com.openrsc.server.net.rsc.enums.OpcodeIn;
import com.openrsc.server.net.rsc.struct.incoming.PrayerStruct;

import static com.openrsc.server.model.entity.player.Prayers.*;

public class PrayerHandler implements PayloadProcessor<PrayerStruct, OpcodeIn> {

	private void activatePrayer(final Prayers prayers, final int prayerID) {
		if (prayers.isPrayerActivated(prayerID)) return;

		switch (prayerID) {
			case THICK_SKIN:
				deactivatePrayer(prayers, ROCK_SKIN, false);
				deactivatePrayer(prayers, STEEL_SKIN, false);
				break;
			case BURST_OF_STRENGTH:
				deactivatePrayer(prayers, SUPERHUMAN_STRENGTH, false);
				deactivatePrayer(prayers, ULTIMATE_STRENGTH, false);
				break;
			case CLARITY_OF_THOUGHT:
				deactivatePrayer(prayers, IMPROVED_REFLEXES, false);
				deactivatePrayer(prayers, INCREDIBLE_REFLEXES, false);
				break;
			case ROCK_SKIN:
				deactivatePrayer(prayers, THICK_SKIN, false);
				deactivatePrayer(prayers, STEEL_SKIN, false);
				break;
			case SUPERHUMAN_STRENGTH:
				deactivatePrayer(prayers, BURST_OF_STRENGTH, false);
				deactivatePrayer(prayers, ULTIMATE_STRENGTH, false);
				break;
			case IMPROVED_REFLEXES:
				deactivatePrayer(prayers, CLARITY_OF_THOUGHT, false);
				deactivatePrayer(prayers, INCREDIBLE_REFLEXES, false);
				break;
			case STEEL_SKIN:
				deactivatePrayer(prayers, THICK_SKIN, false);
				deactivatePrayer(prayers, ROCK_SKIN, false);
				break;
			case ULTIMATE_STRENGTH:
				deactivatePrayer(prayers, BURST_OF_STRENGTH, false);
				deactivatePrayer(prayers, SUPERHUMAN_STRENGTH, false);
				break;
			case INCREDIBLE_REFLEXES:
				deactivatePrayer(prayers, CLARITY_OF_THOUGHT, false);
				deactivatePrayer(prayers, IMPROVED_REFLEXES, false);
				break;
			case RAPID_RESTORE: // TODO
			case RAPID_HEAL: // TODO
			case PROTECT_ITEMS:
			case PARALYZE_MONSTER:
			case PROTECT_FROM_MISSILES:
			default:
				break;
		}

		prayers.setPrayer(prayerID, true);
	}

	private void deactivatePrayer(final Prayers prayers, final int prayerID, final boolean updatePlayer) {
		if (!prayers.isPrayerActivated(prayerID)) return;
		// TODO RAPID_RESTORE RAPID_HEAL
		prayers.setPrayer(prayerID, false, updatePlayer);
	}

	public void process(final PrayerStruct payload, final Player player) throws Exception {
		final int prayerID = payload.prayerID;

		if (prayerID < THICK_SKIN || prayerID > PROTECT_FROM_MISSILES) {
			player.setSuspiciousPlayer(true,
				String.format("prayerID < %d or prayerID > %d", THICK_SKIN, PROTECT_FROM_MISSILES));
			return;
		}

		if (player.getConfig().LACKS_PRAYERS) {
			player.message("World does not feature prayers!");
			return;
		}

		if (player.getDuel().isDuelActive() && player.getDuel().getDuelSetting(2)) {
			player.message("Prayers cannot be used during this duel!");
			return;
		}

		if (prayerID == PROTECT_ITEMS && player.isIronMan(IronmanMode.Ultimate.id())) {
			player.message("Ultimate Ironmen cannot protect items.");
			return;
		}

		final OpcodeIn opcode = payload.getOpcode();

		if (opcode == OpcodeIn.PRAYER_ACTIVATED) {
			final PrayerDef prayerDef = player.getWorld().getServer().getEntityHandler().getPrayerDef(prayerID);
			assert prayerDef != null;

			if (player.getSkills().getMaxStat(Skill.PRAYER.id()) < prayerDef.getReqLevel()) {
				player.message("Your prayer ability is not high enough to use this prayer");
				return;
			}

			if (player.getSkills().getLevel(Skill.PRAYER.id()) <= 0) {
				player.message("You have run out of prayer points. Return to a church to recharge");
				return;
			}

			activatePrayer(player.getPrayers(), prayerID);
		} else if (opcode == OpcodeIn.PRAYER_DEACTIVATED) {
			deactivatePrayer(player.getPrayers(), prayerID, true);
		}
	}
}
