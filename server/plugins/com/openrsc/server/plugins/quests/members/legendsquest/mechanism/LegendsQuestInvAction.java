package com.openrsc.server.plugins.quests.members.legendsquest.mechanism;

import com.openrsc.server.Constants;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.entity.update.ChatMessage;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.listeners.action.InvActionListener;
import com.openrsc.server.plugins.listeners.action.InvUseOnItemListener;
import com.openrsc.server.plugins.listeners.executive.InvActionExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnItemExecutiveListener;
import com.openrsc.server.util.rsc.DataConversions;

import static com.openrsc.server.plugins.Functions.*;

public class LegendsQuestInvAction implements InvActionListener, InvActionExecutiveListener, InvUseOnItemListener, InvUseOnItemExecutiveListener {

	public static final int SCRIBBLED_NOTES = 1241;
	public static final int SCRAWLED_NOTES = 1242;
	public static final int SCRATCHED_NOTES = 1243;
	public static final int ROUGH_SKETCH_OF_A_BOWL = 1246;
	public static final int SHAMANS_TOME = 1244;
	public static final int BOOKING_OF_BINDING = 1238;
	public static final int YOMMI_TREE_SEED = 1182;
	public static final int GERMINATED_YOMMI_TREE_SEED = 1254;
	public static final int A_RED_CRYSTAL = 1222;
	public static final int HOLY_FORCE_SPELL = 1257;
	public static final int GILDED_TOTEM_POLE = 1265;

	@Override
	public boolean blockInvAction(Item item, Player p) {
		if(inArray(item.getID(), SCRIBBLED_NOTES, SCRAWLED_NOTES, SCRATCHED_NOTES, ROUGH_SKETCH_OF_A_BOWL, SHAMANS_TOME, BOOKING_OF_BINDING, YOMMI_TREE_SEED, GERMINATED_YOMMI_TREE_SEED, A_RED_CRYSTAL, HOLY_FORCE_SPELL, GILDED_TOTEM_POLE)) {
			return true;
		}
		return false;
	}

	@Override
	public void onInvAction(Item item, Player p) {
		if(item.getID() == GILDED_TOTEM_POLE) {
			message(p, 1300, "This totem pole is utterly awe inspiring.",
					"Perhaps you should show it to Radimus Erkle...");
		}
		if(item.getID() == HOLY_FORCE_SPELL) {
			Npc n = getNearestNpc(p, 740, 5);
			if(n != null && p.getQuestStage(Constants.Quests.LEGENDS_QUEST) == 7) {
				message(p, "You thrust the Holy Force spell in front of the spirit.");
				message(p, n, 1300, "A bright, holy light streams out from the paper spell.");
				if(p.getCache().hasKey("already_cast_holy_spell")) {
					npcTalk(p, n, "Argghhhhh...not again....!");
				} else {
					npcTalk(p, n, "Argghhhhh...noooooo!");
					p.getCache().store("already_cast_holy_spell", true);
				}
				message(p, n, 1300, "The Demon lets out an unearthly, blood curdling scream...");
				int formerNpcX = n.getX();
				int formerNpcY = n.getY();
				if(n != null)
					n.remove();
				Npc second_nezikchened = spawnNpc(769, formerNpcX, formerNpcY, 60000 * 15,  p);
				if(second_nezikchened != null) {
					message(p, second_nezikchened, 600, "The spell seems to weaken the Demon.");
					second_nezikchened.getSkills().setLevel(DEFENCE, n.getSkills().getLevel(DEFENCE) - 5);
					if(p.getCache().hasKey("ran_from_2nd_nezi")) {
						second_nezikchened.getUpdateFlags().setChatMessage(new ChatMessage(second_nezikchened, "So you have returned and I am prepared for you now!", p));
					} else {
						second_nezikchened.getUpdateFlags().setChatMessage(new ChatMessage(second_nezikchened, "Now I am revealed to you Vacu, so shall ye perish.", p));
					}
					second_nezikchened.startCombat(p);
					int newPray = (int) Math.ceil((double) p.getSkills().getLevel(PRAYER) / 2);
					if(p.getSkills().getLevel(PRAYER) - newPray < 30) {
						message(p, 1300, "A sense of fear comes over you ",
								"You feel a sense of loss...");
					}
					else {
						message(p, 1300, "An intense sense of fear comes over you ",
								"You feel a great sense of loss...");
					}
					p.getSkills().setLevel(PRAYER, newPray);
					if(p.getCache().hasKey("ran_from_2nd_nezi")) {
						sleep(7000);
						message(p, 1300, "The Demon takes out a dark dagger and throws it at you...");
						if(DataConversions.random(0, 1) == 1) {
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
		if(item.getID() == A_RED_CRYSTAL) {
			message(p, 1300, "As the crystal touches your hands a voice inside of your head says..",
					"@gre@Voice in head: Bring life to the dragons eye.");
		}
		if(item.getID() == GERMINATED_YOMMI_TREE_SEED) {
			message(p, 1300, "These seeds have been germinated in pure water...");
			p.message("They can be planted in fertile soil now...");
		}
		if(item.getID() == YOMMI_TREE_SEED) {
			p.message("These seeds need to be germinated in pure water...");
		}
		if(item.getID() == BOOKING_OF_BINDING) {
			p.message("You read the Book of Binding...");
			int page = showMenu(p,
					"Arcana..",
					"Instructo...",
					"Defeati...",
					"Enchanto...");
			if(page == 0) {
				message(p, 1300, "You read the section entitled Arcana...");
				ActionSender.sendBox(p, "Arcana...% % Use holy water to determine possesion, slight changes in appearance may be percieved when doused.% % Legendary Silverlight will help to defeat any demon by weakening it.% % Be wary of any demon, it may have special forms of attack. % % Use an Octagram shape to confine unearthly creatures of the underworld - the perfect geometry confuses them.", true);
			} else if(page == 1) {
				message(p, 1300, "You read the section entitled Instructo...");
				ActionSender.sendBox(p, "Instructo...% % To make Holy water enchant small vials to contain the magic water.% % See later chapters for enchantment. Place sacred water into vial and equip as any other missile.", true);
			} else if(page == 2) {
				message(p, 1300, "You read the section entitled Defeati...");
				ActionSender.sendBox(p, "Defeati...% %. Hold the book of binding open to the possesed letting the goodlight fall on them completely. Be prepared for as soon as the beast is released it will strike and strike hard.", true);
			} else if(page == 3) {
				message(p, 1300, "You read the section entitled Enchanto...",
						"This looks like an enchantment, it requires some magic and prayer to cast.");
				p.message("Would you like to try and cast this enchantment?");
				int opt = showMenu(p,
						"Yes, I'll try.",
						"No, I don't think I'll bother.");
				if(opt == 0) {
					if(hasItem(p, 465)) {
						// TODO
					} else {
						p.message("This spell looks as if it needs some other components.");
					}
				} else if(opt == 1) {
					p.message("This spell looks as if it needs some other components.");
				}
			}
		}
		if(inArray(item.getID(), SCRIBBLED_NOTES, SCRAWLED_NOTES, SCRATCHED_NOTES)) {
			p.message("You try your best to decode the writing, this is what you make out.");
			if(item.getID() == SCRIBBLED_NOTES) {
				ActionSender.sendBox(p, "Daily notes of Ungadulu...% % Day 1...% % I have prepared the incantations and will invoke the spirits of my ancestors and pay them hommage. Though I feel a strange presence in these caves, it is with the heart of the lion that I fight my fears and mark the magical pentagram. % % Day 2... % % What have I done? My spirit is overthrown by a feeling of fear and evil, I am not myself these days and feel helpless and weak. % % From my teachings...                                                               ", true);
			} else if(item.getID() == SCRAWLED_NOTES) {
				ActionSender.sendBox(p, "I fear that the spirit of an ancient one resides within me and uses me...I am too weak to cast the curse myself and fight the beast within.% % Day 3....% %...my last hope is that someone will read this and aid me...I am undone and I fear....", true);
			} else if(item.getID() == SCRATCHED_NOTES) {
				ActionSender.sendBox(p, "Day 4 ...% % These days come so fleetingly, I have no idea how long I have been here now...% % Day 5... % %A wizened charm will release me, but never magic that would would harm...", true);
			}
		}
		if(item.getID() == ROUGH_SKETCH_OF_A_BOWL) {
			message(p, 1300, "You look at the rough sketch that Gujuo gave you.");
			p.message("It looks like a picture of a bowl...");
		}
		if(item.getID() == SHAMANS_TOME) {
			message(p, 1300, "You read the ancient shamans tome.");
			message(p, 3800, "It is written in a strange sort of language but you manage a rough translation.");
			ActionSender.sendBox(p, "% % ...scattered are my hopes that I will ever be released from this flaming Octagram, it is the only thing which will contain this beast within.% %Although it's grip over me is weakened with magic, it is hopeless to know if a saviour would guess this. % % I am doomed...", true);
		}
	}

	@Override
	public boolean blockInvUseOnItem(Player p, Item item1, Item item2) {
		if(item1.getID() == YOMMI_TREE_SEED && item2.getID() == 1267 || item1.getID() == 1267 && item2.getID() == YOMMI_TREE_SEED) {
			return true;
		}
		return false;
	}

	@Override
	public void onInvUseOnItem(Player p, Item item1, Item item2) {
		if(item1.getID() == YOMMI_TREE_SEED && item2.getID() == 1267 || item1.getID() == 1267 && item2.getID() == YOMMI_TREE_SEED) {
			for(int i = 0; i < p.getInventory().countId(YOMMI_TREE_SEED); i++) {
				removeItem(p, YOMMI_TREE_SEED, 1);
				addItem(p, GERMINATED_YOMMI_TREE_SEED, 1);
			}
			p.message("You place the seeds in the pure sacred water...");
			p.getInventory().replace(1267, 1266);
			message(p, 1300, "The pure water in the golden bowl has run out...");
			p.message("You start to see little shoots growing on the seeds.");
			if(p.getQuestStage(Constants.Quests.LEGENDS_QUEST) == 4) {
				p.setQuestStage(Constants.Quests.LEGENDS_QUEST, 5);
			}
		}
	}
}
