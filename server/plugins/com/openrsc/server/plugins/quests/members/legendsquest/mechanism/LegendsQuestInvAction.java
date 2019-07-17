package com.openrsc.server.plugins.quests.members.legendsquest.mechanism;

import com.openrsc.server.Constants;
import com.openrsc.server.external.ItemId;
import com.openrsc.server.external.NpcId;
import com.openrsc.server.model.Skills.SKILLS;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.entity.update.ChatMessage;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.listeners.action.InvActionListener;
import com.openrsc.server.plugins.listeners.action.InvUseOnItemListener;
import com.openrsc.server.plugins.listeners.executive.InvActionExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnItemExecutiveListener;
import com.openrsc.server.util.rsc.DataConversions;

import static com.openrsc.server.plugins.Functions.addItem;
import static com.openrsc.server.plugins.Functions.getCurrentLevel;
import static com.openrsc.server.plugins.Functions.getNearestNpc;
import static com.openrsc.server.plugins.Functions.hasItem;
import static com.openrsc.server.plugins.Functions.inArray;
import static com.openrsc.server.plugins.Functions.message;
import static com.openrsc.server.plugins.Functions.npcTalk;
import static com.openrsc.server.plugins.Functions.removeItem;
import static com.openrsc.server.plugins.Functions.showMenu;
import static com.openrsc.server.plugins.Functions.sleep;
import static com.openrsc.server.plugins.Functions.spawnNpc;

public class LegendsQuestInvAction implements InvActionListener, InvActionExecutiveListener, InvUseOnItemListener, InvUseOnItemExecutiveListener {

	@Override
	public boolean blockInvAction(Item item, Player p) {
		return inArray(item.getID(),
			ItemId.SCRIBBLED_NOTES.id(), ItemId.SCRAWLED_NOTES.id(), ItemId.SCATCHED_NOTES.id(),
			ItemId.ROUGH_SKETCH_OF_A_BOWL.id(), ItemId.SHAMANS_TOME.id(), ItemId.BOOKING_OF_BINDING.id(),
			ItemId.YOMMI_TREE_SEED.id(), ItemId.GERMINATED_YOMMI_TREE_SEED.id(),
			ItemId.A_RED_CRYSTAL.id(), ItemId.HOLY_FORCE_SPELL.id(), ItemId.GILDED_TOTEM_POLE.id());
	}

	@Override
	public void onInvAction(Item item, Player p) {
		if (item.getID() == ItemId.GILDED_TOTEM_POLE.id()) {
			message(p, 1300, "This totem pole is utterly awe inspiring.",
				"Perhaps you should show it to Radimus Erkle...");
		}
		else if (item.getID() == ItemId.HOLY_FORCE_SPELL.id()) {
			Npc n = getNearestNpc(p, NpcId.ECHNED_ZEKIN.id(), 5);
			if (n != null && p.getQuestStage(Constants.Quests.LEGENDS_QUEST) == 7) {
				message(p, "You thrust the Holy Force spell in front of the spirit.");
				message(p, n, 1300, "A bright, holy light streams out from the paper spell.");
				if (p.getCache().hasKey("already_cast_holy_spell")) {
					npcTalk(p, n, "Argghhhhh...not again....!");
				} else {
					npcTalk(p, n, "Argghhhhh...noooooo!");
					p.getCache().store("already_cast_holy_spell", true);
				}
				message(p, n, 1300, "The spirit lets out an unearthly, blood curdling scream...");
				int formerNpcX = n.getX();
				int formerNpcY = n.getY();
				if (n != null)
					n.remove();
				Npc second_nezikchened = spawnNpc(NpcId.NEZIKCHENED.id(), formerNpcX, formerNpcY, 60000 * 15, p);
				if (second_nezikchened != null) {
					message(p, second_nezikchened, 600, "The spell seems to weaken the Demon.");
					second_nezikchened.getSkills().setLevel(SKILLS.DEFENSE.id(), n.getSkills().getLevel(SKILLS.DEFENSE.id()) - 5);
					if (p.getCache().hasKey("ran_from_2nd_nezi")) {
						second_nezikchened.getUpdateFlags().setChatMessage(new ChatMessage(second_nezikchened, "So you have returned and I am prepared for you now!", p));
					} else {
						second_nezikchened.getUpdateFlags().setChatMessage(new ChatMessage(second_nezikchened, "Now I am revealed to you Vacu, so shall ye perish.", p));
					}
					second_nezikchened.startCombat(p);
					int newPray = (int) Math.ceil((double) p.getSkills().getLevel(SKILLS.PRAYER.id()) / 2);
					if (p.getSkills().getLevel(SKILLS.PRAYER.id()) - newPray < 30) {
						message(p, 1300, "A sense of fear comes over you ",
							"You feel a sense of loss...");
					} else {
						message(p, 1300, "An intense sense of fear comes over you ",
							"You feel a great sense of loss...");
					}
					p.getSkills().setLevel(SKILLS.PRAYER.id(), newPray);
					if (p.getCache().hasKey("ran_from_2nd_nezi")) {
						sleep(7000);
						message(p, 1300, "The Demon takes out a dark dagger and throws it at you...");
						if (DataConversions.random(0, 1) == 1) {
							message(p, 1300, "The dagger hits you with an agonising blow...");
							p.damage(14);
						} else {
							message(p, 600, "But you neatly manage to dodge the attack.");
						}
					}
				}

			} else {
				message(p, 600, "There is no suitable candidate to cast this spell on.");
			}
		}
		else if (item.getID() == ItemId.A_RED_CRYSTAL.id() && !Constants.GameServer.WANT_PETS) {
			message(p, 1300, "As the crystal touches your hands a voice inside of your head says..",
				"@gre@Voice in head: Bring life to the dragons eye.");
		}
		else if (item.getID() == ItemId.GERMINATED_YOMMI_TREE_SEED.id()) {
			message(p, 1300, "These seeds have been germinated in pure water...");
			p.message("They can be planted in fertile soil now...");
		}
		else if (item.getID() == ItemId.YOMMI_TREE_SEED.id()) {
			p.message("These seeds need to be germinated in pure water...");
		}
		else if (item.getID() == ItemId.BOOKING_OF_BINDING.id()) {
			p.message("You read the Book of Binding...");
			int page = showMenu(p,
				"Arcana..",
				"Instructo...",
				"Defeati...",
				"Enchanto...");
			if (page == 0) {
				message(p, 1300, "You read the section entitled Arcana...");
				ActionSender.sendBox(p, "Arcana...% % Use holy water to determine possesion, slight changes in appearance may be percieved when doused.% % Legendary Silverlight will help to defeat any demon by weakening it.% % Be wary of any demon, it may have special forms of attack. % % Use an Octagram shape to confine unearthly creatures of the underworld - the perfect geometry confuses them.", true);
			} else if (page == 1) {
				message(p, 1300, "You read the section entitled Instructo...");
				ActionSender.sendBox(p, "Instructo...% % To make Holy water enchant small vials to contain the magic water.% % See later chapters for enchantment. Place sacred water into vial and equip as any other missile.", true);
			} else if (page == 2) {
				message(p, 1300, "You read the section entitled Defeati...");
				ActionSender.sendBox(p, "Defeati...% %. Hold the book of binding open to the possesed letting the goodlight fall on them completely. Be prepared for as soon as the beast is released it will strike and strike hard.", true);
			} else if (page == 3) {
				message(p, 1300, "You read the section entitled Enchanto...",
					"This looks like an enchantment, it requires some magic and prayer to cast.");
				p.message("Would you like to try and cast this enchantment?");
				int opt = showMenu(p,
					"Yes, I'll try.",
					"No, I don't think I'll bother.");
				//authentic, didn't matter option chosen
				if (opt == 0 || opt == 1) 
				{
					if (getCurrentLevel(p, SKILLS.PRAYER.id()) < 10) {
						p.message("You need at least ten prayer points to cast this spell.");
						return;
					}
					if (getCurrentLevel(p, SKILLS.MAGIC.id()) < 10) {
						p.message("You need at least ten magic points to cast this spell.");
						return;
					}
					if (hasItem(p, ItemId.EMPTY_VIAL.id())) {
						message(p, "The spell is cast perfectly..",
								"You enchant one of the empty vials.");
						p.getInventory().replace(ItemId.EMPTY_VIAL.id(), ItemId.ENCHANTED_VIAL.id());
					} else {
						p.message("This spell looks as if it needs some other components.");
					}
				}
			}
		}
		else if (inArray(item.getID(), ItemId.SCRIBBLED_NOTES.id(), ItemId.SCRAWLED_NOTES.id(), ItemId.SCATCHED_NOTES.id())) {
			p.message("You try your best to decode the writing, this is what you make out.");
			if (item.getID() == ItemId.SCRIBBLED_NOTES.id()) {
				ActionSender.sendBox(p, "Daily notes of Ungadulu...% % Day 1...% % I have prepared the incantations and will invoke the spirits of my ancestors and pay them hommage. Though I feel a strange presence in these caves, it is with the heart of the lion that I fight my fears and mark the magical pentagram. % % Day 2... % % What have I done? My spirit is overthrown by a feeling of fear and evil, I am not myself these days and feel helpless and weak. % % From my teachings...                                                               ", true);
			} else if (item.getID() == ItemId.SCRAWLED_NOTES.id()) {
				ActionSender.sendBox(p, "I fear that the spirit of an ancient one resides within me and uses me...I am too weak to cast the curse myself and fight the beast within.% % Day 3....% %...my last hope is that someone will read this and aid me...I am undone and I fear....", true);
			} else if (item.getID() == ItemId.SCATCHED_NOTES.id()) {
				ActionSender.sendBox(p, "Day 4 ...% % These days come so fleetingly, I have no idea how long I have been here now...% % Day 5... % %A wizened charm will release me, but never magic that would would harm...", true);
			}
		}
		else if (item.getID() == ItemId.ROUGH_SKETCH_OF_A_BOWL.id()) {
			message(p, 1300, "You look at the rough sketch that Gujuo gave you.");
			p.message("It looks like a picture of a bowl...");
		}
		else if (item.getID() == ItemId.SHAMANS_TOME.id()) {
			message(p, 1300, "You read the ancient shamans tome.");
			message(p, 3800, "It is written in a strange sort of language but you manage a rough translation.");
			ActionSender.sendBox(p, "% % ...scattered are my hopes that I will ever be released from this flaming Octagram, it is the only thing which will contain this beast within.% %Although it's grip over me is weakened with magic, it is hopeless to know if a saviour would guess this. % % I am doomed...", true);
		}
	}

	@Override
	public boolean blockInvUseOnItem(Player p, Item item1, Item item2) {
		return Functions.compareItemsIds(item1, item2, ItemId.YOMMI_TREE_SEED.id(), ItemId.BLESSED_GOLDEN_BOWL_WITH_PURE_WATER.id());
	}

	@Override
	public void onInvUseOnItem(Player p, Item item1, Item item2) {
		if (Functions.compareItemsIds(item1, item2, ItemId.YOMMI_TREE_SEED.id(), ItemId.BLESSED_GOLDEN_BOWL_WITH_PURE_WATER.id())) {
			for (int i = 0; i < p.getInventory().countId(ItemId.YOMMI_TREE_SEED.id()); i++) {
				removeItem(p, ItemId.YOMMI_TREE_SEED.id(), 1);
				addItem(p, ItemId.GERMINATED_YOMMI_TREE_SEED.id(), 1);
			}
			p.message("You place the seeds in the pure sacred water...");
			p.getInventory().replace(ItemId.BLESSED_GOLDEN_BOWL_WITH_PURE_WATER.id(), ItemId.BLESSED_GOLDEN_BOWL.id());
			message(p, 1300, "The pure water in the golden bowl has run out...");
			p.message("You start to see little shoots growing on the seeds.");
			if (p.getQuestStage(Constants.Quests.LEGENDS_QUEST) == 4) {
				p.setQuestStage(Constants.Quests.LEGENDS_QUEST, 5);
			}
		}
	}

}
