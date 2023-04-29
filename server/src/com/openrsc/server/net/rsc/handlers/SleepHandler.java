package com.openrsc.server.net.rsc.handlers;

import com.openrsc.server.database.impl.mysql.queries.logging.GenericLog;
import com.openrsc.server.event.SingleEvent;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.net.rsc.PayloadProcessor;
import com.openrsc.server.net.rsc.enums.OpcodeIn;
import com.openrsc.server.net.rsc.struct.incoming.SleepStruct;
import com.openrsc.server.util.rsc.CaptchaGenerator;
import com.openrsc.server.util.rsc.PrerenderedSleepword;

public final class SleepHandler implements PayloadProcessor<SleepStruct, OpcodeIn> {

	public void process(SleepStruct payload, Player player) throws Exception {
		if (!player.isSleeping()) {
			return;
		}

		String sleepWord;
		if (player.isUsing233CompatibleClient()) {
			int sleepDelay = payload.sleepDelay; // TODO: use this somehow
		}
		sleepWord = payload.sleepWord.trim();
		if (sleepWord.equalsIgnoreCase("-null-")) {
			player.incrementSleepTries();

			player.getWorld().getServer()
				.getGameEventHandler()
				.add(new SingleEvent(player.getWorld(), player, player
					.getIncorrectSleepTimes() * 1000, "Guess Sleep Word") {
					@Override
					public void action() {
						ActionSender.sendEnterSleep(getOwner());
					}
				});
		} else {
			String correctWord;
			boolean knowCorrectWord = true;
			PrerenderedSleepword curSleepword = null;
			if (null != player.queuedSleepword) {
				curSleepword = player.queuedSleepword;
			} else {
				if (CaptchaGenerator.usingPrerenderedSleepwords) {
					curSleepword = CaptchaGenerator.prerenderedSleepwords.get(player.getPrerenderedSleepwordIndex());
				} else {
					// server failed to load prerendered sleepwords.
					// word is "asleep", but we won't check that.
					knowCorrectWord = false;
				}
			}

			if (knowCorrectWord && (CaptchaGenerator.usingPrerenderedSleepwords || (null != player.queuedSleepword && CaptchaGenerator.usingPrerenderedSleepwordsSpecial))) {
			    knowCorrectWord = curSleepword.knowTheCorrectWord;
			    if (knowCorrectWord) {
			        correctWord = curSleepword.correctWord;
                } else {
                    correctWord = "-null-";
                    player.getWorld().getServer().getGameLogger().addQuery(new GenericLog(player.getWorld(), player.getUsername() + " guessed !_" + sleepWord + "_! for filename:: " + CaptchaGenerator.prerenderedSleepwords.get(player.getPrerenderedSleepwordIndex()).filename));
                }
            } else {
			    correctWord = player.getSleepword();
            }
			if (sleepWord.equalsIgnoreCase(correctWord) || !knowCorrectWord) {
				if (null != player.queuedSleepword) {
					try {
						player.queuedSleepwordSender.message("@whi@" + player.getUsername() + " correctly guessed @cya@" + sleepWord + "@whi@ for sleepword @cya@" + curSleepword.filename);
					} catch (Exception ex) {} // moderator likely logged out
					player.queuedSleepword = null;
				}
				ActionSender.sendWakeUp(player, true, false);
				player.resetSleepTries();
				// Advance the fatigue expert part of tutorial island
				if (player.getCache().hasKey("tutorial") && player.getCache().getInt("tutorial") == 85)
					player.getCache().set("tutorial", 86);

				//Handle exp toggle for servers without fatigue
				if (!player.getConfig().WANT_FATIGUE) {
					handleExpToggle(player);
				}
			} else {
				if (null != player.queuedSleepword) {
					try {
						player.queuedSleepwordSender.message("@whi@" + player.getUsername() + " incorrectly guessed @cya@" + sleepWord + "@whi@ for sleepword @cya@" + curSleepword.filename);
					} catch (Exception ex) {} // moderator likely logged out
					if (player.getIncorrectSleepTimes() > 2) {
						player.queuedSleepword = null;
						ActionSender.sendWakeUp(player, true, false);
						player.resetSleepTries();
						return;
					}
				}
				ActionSender.sendIncorrectSleepword(player);
				player.incrementSleepTries();
				if (player.getIncorrectSleepTimes() > 5) {
					if (player.getConfig().WARN_EXCESSIVE_CAPTCHA_FAILURE) {
						player.getWorld().sendModAnnouncement(player.getUsername() + " has failed sleeping captcha " + player.getIncorrectSleepTimes() + " times!");
					}
					player.getWorld().getServer().getGameLogger().addQuery(new GenericLog(player.getWorld(), player.getUsername() + " has failed sleeping captcha " + player.getIncorrectSleepTimes() + " times!"));
				}

				player.getWorld().getServer().getGameEventHandler()
					.add(new SingleEvent(player.getWorld(), player, player
						.getIncorrectSleepTimes() * 1000, "Guess Sleep Word") {
						@Override
						public void action() {
							ActionSender.sendEnterSleep(getOwner());
						}
					});
			}
		}
	}

	private void handleExpToggle(Player player) {
		player.toggleFreezeXp();
		ActionSender.sendExperienceToggle(player);
		if (player.isExperienceFrozen())
			player.message("You have @red@DISABLED@whi@ " + (player.getConfig().WANT_OPENPK_POINTS ? "points" : "experience") + " gain!");
		else
			player.message("You have @gre@ENABLED@whi@ " + (player.getConfig().WANT_OPENPK_POINTS ? "points" : "experience") + " gain!");
	}
}
