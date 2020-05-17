package com.openrsc.server.plugins.quests.members.legendsquest.mechanism;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.constants.Skills;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.entity.update.ChatMessage;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.triggers.OpInvTrigger;
import com.openrsc.server.plugins.triggers.UseInvTrigger;
import com.openrsc.server.util.rsc.DataConversions;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class LegendsQuestInvAction implements OpInvTrigger, UseInvTrigger {

	@Override
	public boolean blockOpInv(Player player, Integer invIndex, Item item, String command) {
		return inArray(item.getCatalogId(),
			ItemId.SCRIBBLED_NOTES.id(), ItemId.SCRAWLED_NOTES.id(), ItemId.SCATCHED_NOTES.id(),
			ItemId.ROUGH_SKETCH_OF_A_BOWL.id(), ItemId.SHAMANS_TOME.id(), ItemId.BOOKING_OF_BINDING.id(),
			ItemId.YOMMI_TREE_SEED.id(), ItemId.GERMINATED_YOMMI_TREE_SEED.id(),
			ItemId.A_RED_CRYSTAL.id(), ItemId.HOLY_FORCE_SPELL.id(), ItemId.GILDED_TOTEM_POLE.id());
	}

	@Override
	public void onOpInv(Player player, Integer invIndex, Item item, String command) {
		if (item.getCatalogId() == ItemId.GILDED_TOTEM_POLE.id()) {
			mes(player.getWorld().getServer().getConfig().GAME_TICK * 2, "This totem pole is utterly awe inspiring.",
				"Perhaps you should show it to Radimus Erkle...");
		}
		else if (item.getCatalogId() == ItemId.HOLY_FORCE_SPELL.id()) {
			Npc n = ifnearvisnpc(player, NpcId.ECHNED_ZEKIN.id(), 5);
			if (n != null && player.getQuestStage(Quests.LEGENDS_QUEST) == 7) {
				mes("You thrust the Holy Force spell in front of the spirit.");
				mes(n, player.getWorld().getServer().getConfig().GAME_TICK * 2, "A bright, holy light streams out from the paper spell.");
				if (player.getCache().hasKey("already_cast_holy_spell")) {
					npcsay(player, n, "Argghhhhh...not again....!");
				} else {
					npcsay(player, n, "Argghhhhh...noooooo!");
					player.getCache().store("already_cast_holy_spell", true);
				}
				mes(n, player.getWorld().getServer().getConfig().GAME_TICK * 2, "The spirit lets out an unearthly, blood curdling scream...");
				int formerNpcX = n.getX();
				int formerNpcY = n.getY();
				if (n != null)
					n.remove();
				Npc second_nezikchened = addnpc(NpcId.NEZIKCHENED.id(), formerNpcX, formerNpcY, 60000 * 15, player);
				if (second_nezikchened != null) {
					mes(second_nezikchened, player.getWorld().getServer().getConfig().GAME_TICK, "The spell seems to weaken the Demon.");
					second_nezikchened.getSkills().setLevel(Skills.DEFENSE, n.getSkills().getLevel(Skills.DEFENSE) - 5);
					if (player.getCache().hasKey("ran_from_2nd_nezi")) {
						second_nezikchened.getUpdateFlags().setChatMessage(new ChatMessage(second_nezikchened, "So you have returned and I am prepared for you now!", player));
					} else {
						second_nezikchened.getUpdateFlags().setChatMessage(new ChatMessage(second_nezikchened, "Now I am revealed to you Vacu, so shall ye perish.", player));
					}
					second_nezikchened.startCombat(player);
					int newPray = (int) Math.ceil((double) player.getSkills().getLevel(Skills.PRAYER) / 2);
					if (player.getSkills().getLevel(Skills.PRAYER) - newPray < 30) {
						mes(player.getWorld().getServer().getConfig().GAME_TICK * 2, "A sense of fear comes over you ",
							"You feel a sense of loss...");
					} else {
						mes(player.getWorld().getServer().getConfig().GAME_TICK * 2, "An intense sense of fear comes over you ",
							"You feel a great sense of loss...");
					}
					player.getSkills().setLevel(Skills.PRAYER, newPray);
					if (player.getCache().hasKey("ran_from_2nd_nezi")) {
						delay(7000);
						mes(player.getWorld().getServer().getConfig().GAME_TICK * 2, "The Demon takes out a dark dagger and throws it at you...");
						if (DataConversions.random(0, 1) == 1) {
							mes(player.getWorld().getServer().getConfig().GAME_TICK * 2, "The dagger hits you with an agonising blow...");
							player.damage(14);
						} else {
							mes(player.getWorld().getServer().getConfig().GAME_TICK, "But you neatly manage to dodge the attack.");
						}
					}
				}

			} else {
				mes(player.getWorld().getServer().getConfig().GAME_TICK, "There is no suitable candidate to cast this spell on.");
			}
		}
		else if (item.getCatalogId() == ItemId.A_RED_CRYSTAL.id() && !player.getWorld().getServer().getConfig().WANT_PETS) {
			mes(player.getWorld().getServer().getConfig().GAME_TICK * 2, "As the crystal touches your hands a voice inside of your head says..",
				"@gre@Voice in head: Bring life to the dragons eye.");
		}
		else if (item.getCatalogId() == ItemId.GERMINATED_YOMMI_TREE_SEED.id()) {
			mes(player.getWorld().getServer().getConfig().GAME_TICK * 2, "These seeds have been germinated in pure water...");
			player.message("They can be planted in fertile soil now...");
		}
		else if (item.getCatalogId() == ItemId.YOMMI_TREE_SEED.id()) {
			player.message("These seeds need to be germinated in pure water...");
		}
		else if (item.getCatalogId() == ItemId.BOOKING_OF_BINDING.id()) {
			player.message("You read the Book of Binding...");
			int page = multi(player,
				"Arcana..",
				"Instructo...",
				"Defeati...",
				"Enchanto...");
			if (page == 0) {
				mes(player.getWorld().getServer().getConfig().GAME_TICK * 2, "You read the section entitled Arcana...");
				ActionSender.sendBox(player, "Arcana...% % Use holy water to determine possesion, slight changes in appearance may be percieved when doused.% % Legendary Silverlight will help to defeat any demon by weakening it.% % Be wary of any demon, it may have special forms of attack. % % Use an Octagram shape to confine unearthly creatures of the underworld - the perfect geometry confuses them.", true);
			} else if (page == 1) {
				mes(player.getWorld().getServer().getConfig().GAME_TICK * 2, "You read the section entitled Instructo...");
				ActionSender.sendBox(player, "Instructo...% % To make Holy water enchant small vials to contain the magic water.% % See later chapters for enchantment. Place sacred water into vial and equip as any other missile.", true);
			} else if (page == 2) {
				mes(player.getWorld().getServer().getConfig().GAME_TICK * 2, "You read the section entitled Defeati...");
				ActionSender.sendBox(player, "Defeati...% %. Hold the book of binding open to the possesed letting the goodlight fall on them completely. Be prepared for as soon as the beast is released it will strike and strike hard.", true);
			} else if (page == 3) {
				mes(player.getWorld().getServer().getConfig().GAME_TICK * 2, "You read the section entitled Enchanto...",
					"This looks like an enchantment, it requires some magic and prayer to cast.");
				player.message("Would you like to try and cast this enchantment?");
				int opt = multi(player,
					"Yes, I'll try.",
					"No, I don't think I'll bother.");
				//authentic, didn't matter option chosen
				if (opt == 0 || opt == 1)
				{
					if (getCurrentLevel(player, Skills.PRAYER) < 10) {
						player.message("You need at least ten prayer points to cast this spell.");
						return;
					}
					if (getCurrentLevel(player, Skills.MAGIC) < 10) {
						player.message("You need at least ten magic points to cast this spell.");
						return;
					}
					if (player.getCarriedItems().hasCatalogID(ItemId.EMPTY_VIAL.id(), Optional.of(false))) {
						mes("The spell is cast perfectly..",
								"You enchant one of the empty vials.");
						player.getCarriedItems().remove(new Item(ItemId.EMPTY_VIAL.id()));
						player.getCarriedItems().getInventory().add(new Item(ItemId.ENCHANTED_VIAL.id()));
					} else {
						player.message("This spell looks as if it needs some other components.");
					}
				}
			}
		}
		else if (inArray(item.getCatalogId(), ItemId.SCRIBBLED_NOTES.id(), ItemId.SCRAWLED_NOTES.id(), ItemId.SCATCHED_NOTES.id())) {
			player.message("You try your best to decode the writing, this is what you make out.");
			if (item.getCatalogId() == ItemId.SCRIBBLED_NOTES.id()) {
				ActionSender.sendBox(player, "Daily notes of Ungadulu...% % Day 1...% % I have prepared the incantations and will invoke the spirits of my ancestors and pay them hommage. Though I feel a strange presence in these caves, it is with the heart of the lion that I fight my fears and mark the magical pentagram. % % Day 2... % % What have I done? My spirit is overthrown by a feeling of fear and evil, I am not myself these days and feel helpless and weak. % % From my teachings...                                                               ", true);
			} else if (item.getCatalogId() == ItemId.SCRAWLED_NOTES.id()) {
				ActionSender.sendBox(player, "I fear that the spirit of an ancient one resides within me and uses me...I am too weak to cast the curse myself and fight the beast within.% % Day 3....% %...my last hope is that someone will read this and aid me...I am undone and I fear....", true);
			} else if (item.getCatalogId() == ItemId.SCATCHED_NOTES.id()) {
				ActionSender.sendBox(player, "Day 4 ...% % These days come so fleetingly, I have no idea how long I have been here now...% % Day 5... % %A wizened charm will release me, but never magic that would would harm...", true);
			}
		}
		else if (item.getCatalogId() == ItemId.ROUGH_SKETCH_OF_A_BOWL.id()) {
			mes(player.getWorld().getServer().getConfig().GAME_TICK * 2, "You look at the rough sketch that Gujuo gave you.");
			player.message("It looks like a picture of a bowl...");
		}
		else if (item.getCatalogId() == ItemId.SHAMANS_TOME.id()) {
			mes(player.getWorld().getServer().getConfig().GAME_TICK * 2, "You read the ancient shamans tome.");
			mes(player.getWorld().getServer().getConfig().GAME_TICK * 6, "It is written in a strange sort of language but you manage a rough translation.");
			ActionSender.sendBox(player, "% % ...scattered are my hopes that I will ever be released from this flaming Octagram, it is the only thing which will contain this beast within.% %Although it's grip over me is weakened with magic, it is hopeless to know if a saviour would guess this. % % I am doomed...", true);
		}
	}

	@Override
	public boolean blockUseInv(Player player, Integer invIndex, Item item1, Item item2) {
		return compareItemsIds(item1, item2, ItemId.YOMMI_TREE_SEED.id(), ItemId.BLESSED_GOLDEN_BOWL_WITH_PURE_WATER.id());
	}

	@Override
	public void onUseInv(Player player, Integer invIndex, Item item1, Item item2) {
		if (compareItemsIds(item1, item2, ItemId.YOMMI_TREE_SEED.id(), ItemId.BLESSED_GOLDEN_BOWL_WITH_PURE_WATER.id())) {
			for (int i = 0; i < player.getCarriedItems().getInventory().countId(ItemId.YOMMI_TREE_SEED.id()); i++) {
				player.getCarriedItems().remove(new Item(ItemId.YOMMI_TREE_SEED.id()));
				give(player, ItemId.GERMINATED_YOMMI_TREE_SEED.id(), 1);
			}
			player.message("You place the seeds in the pure sacred water...");
			player.getCarriedItems().remove(new Item(ItemId.BLESSED_GOLDEN_BOWL_WITH_PURE_WATER.id()));
			player.getCarriedItems().getInventory().add(new Item(ItemId.BLESSED_GOLDEN_BOWL.id()));
			mes(player.getWorld().getServer().getConfig().GAME_TICK * 2, "The pure water in the golden bowl has run out...");
			player.message("You start to see little shoots growing on the seeds.");
			if (player.getQuestStage(Quests.LEGENDS_QUEST) == 4) {
				player.setQuestStage(Quests.LEGENDS_QUEST, 5);
			}
		}
	}

}
