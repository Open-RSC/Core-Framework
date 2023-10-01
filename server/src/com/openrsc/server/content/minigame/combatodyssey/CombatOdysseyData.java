package com.openrsc.server.content.minigame.combatodyssey;

import com.openrsc.server.model.world.World;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class CombatOdysseyData {
	/**
	 * The asynchronous logger.
	 */
	private static final Logger LOGGER = LogManager.getLogger();

	ArrayList<Tier> tiers;
	World world;

	public CombatOdysseyData(World world) {
		this.world = world;
		this.tiers = new ArrayList<Tier>();
	}

	public void load() {
		try {
			String filename = getWorld().getServer().getConfig().CONFIG_DIR + "/defs/extras/CombatOdyssey.json";
			JSONObject object = new JSONObject(new String(Files.readAllBytes(Paths.get(filename))));
			JSONArray tiers = object.getJSONArray(JSONObject.getNames(object)[0]);

			// Loop through each tier
			for (int i = 0; i < tiers.length(); ++i) {
				JSONObject tier = tiers.getJSONObject(i);

				int tierId = tier.getInt("tierId");

				int tierMasterId = tier.getInt("tierMasterId");

				JSONArray rewards = tier.getJSONArray("rewards");
				ArrayList<Pair<Integer, Integer>> rewardList = new ArrayList<Pair<Integer, Integer>>();
				// Loop through each reward for this tier
				for (int j = 0; j < rewards.length(); ++j) {
					JSONObject reward = rewards.getJSONObject(j);
					int itemId = reward.getInt("itemId");
					int amount = reward.getInt("amount");
					Pair<Integer, Integer> rewardPair = new ImmutablePair<Integer, Integer>(itemId, amount);
					rewardList.add(rewardPair);
				}

				// Create the Tier object
				Tier coTier = new Tier(tierId, tierMasterId, rewardList);

				JSONArray tasks = tier.getJSONArray("tasks");
				// Loop through each task within the tier
				int taskId = 0;
				for (int j = 0; j < tasks.length(); ++j) {
					JSONObject task = tasks.getJSONObject(j);
					String description = task.getString("description");
					JSONArray npcIds = task.getJSONArray("npcIds");

					// Get the list of NpcIds for this task
					ArrayList<Integer> npcIdList = new ArrayList<Integer>();
					for (int k = 0; k < npcIds.length(); ++k) {
						npcIdList.add(npcIds.getInt(k));
					}

					// Now convert the ArrayList<Integer> into an int[] (I don't want an Integer[])
					int[] npcIdArray = new int[npcIdList.size()];
					for (int k = 0; k < npcIdList.size(); ++k) {
						npcIdArray[k] = npcIdList.get(k);
					}

					int kills = task.getInt("kills");
					JSONArray monsterInfoDialog = task.getJSONArray("monsterInfoDialog");

					// Get the list of dialog for this task
					ArrayList<String> monsterDialogList = new ArrayList<String>();
					for (int k = 0; k < monsterInfoDialog.length(); ++k) {
						monsterDialogList.add(monsterInfoDialog.getString(k));
					}

					Task coTask = new Task(taskId++, description, npcIdArray, kills, monsterDialogList.toArray(new String[0]));
					coTier.addTask(coTask);
				}

				getTiers().add(coTier);
			}

			LOGGER.info("Loaded " + getTiers().size() + " Odyssey tiers from " + filename);
		} catch (Exception e) {
			LOGGER.error(e);
		}
	}

	public ArrayList<Tier> getTiers() {
		return tiers;
	}

	public Tier getTier(int tier) {
		try {
			return tiers.get(tier);
		} catch (IndexOutOfBoundsException ex) {
			return null;
		}
	}

	private World getWorld() {
		return world;
	}
}
