package com.openrsc.server.plugins.authentic.misc;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.OpLocTrigger;

import static com.openrsc.server.plugins.Functions.*;

public class PickFromTree implements OpLocTrigger {

	private final int BANANA_TREE_ID = 183;
	private final int EMPTY_BANANA_TREE_ID = 184;

	private final int PINEAPPLE_TREE_ID = 430;
	private final int EMPTY_PINEAPPLE_TREE_ID = 431;

	@Override
	public boolean blockOpLoc(Player player, GameObject obj, String command) {
		return obj.getID() == BANANA_TREE_ID
			|| obj.getID() == EMPTY_BANANA_TREE_ID
			|| obj.getID() == PINEAPPLE_TREE_ID
			|| obj.getID() == EMPTY_PINEAPPLE_TREE_ID;
	}

	@Override
	public void onOpLoc(Player player, GameObject obj, String command) {
		if (obj.getID() == BANANA_TREE_ID || obj.getID() == PINEAPPLE_TREE_ID) {
			int repeat = 1;
			if (config().BATCH_PROGRESSION) {
				repeat = getTotalPicks(obj.getID());
			}

			startbatch(repeat);
			batchTreePick(player, obj);
		}

		if (obj.getID() == EMPTY_BANANA_TREE_ID || obj.getID() == EMPTY_PINEAPPLE_TREE_ID) {
			player.message("there are no " + getFruitName(obj.getID()) + "s left on the tree");
		}
	}

	private void batchTreePick(Player player, GameObject tree) {
		int fruitCount = 1;
		if (player.getCache().hasKey(getFruitName(tree.getID()) + "_pick"))
			fruitCount = player.getCache().getInt(getFruitName(tree.getID()) + "_pick") + 1;

		player.getCache().set(getFruitName(tree.getID()) + "_pick", fruitCount);
		give(player, getFruitId(tree.getID()), 1);

		if (isTreeEmpty(tree.getID(), fruitCount)) {

			if (tree.getID() == BANANA_TREE_ID) player.message("you pick the last banana");

			changeloc(tree, config().GAME_TICK * 750, getEmptyTreeId(tree.getID())); // 8 minutes respawn time.
			player.getCache().remove(getFruitName(tree.getID()) + "_pick");
			return;
		} else {
			player.message("you pick a " + getFruitName(tree.getID()));
		}

		delay();

		updatebatch();
		if (!ifinterrupted() && !isbatchcomplete()) {
			batchTreePick(player, tree);
		}
	}

	private int getTotalPicks(int treeId) {
		if (treeId == BANANA_TREE_ID) return 5;
		if (treeId == PINEAPPLE_TREE_ID) return 4;
		return -1;
	}

	private boolean isTreeEmpty(int treeId, int fruitCount) {
		if ((treeId == BANANA_TREE_ID && fruitCount >= getTotalPicks(treeId))
			|| (treeId == PINEAPPLE_TREE_ID && fruitCount >= getTotalPicks(treeId))) {
			return true;
		}
		return false;
	}

	private String getFruitName(int treeId) {
		if (treeId == BANANA_TREE_ID || treeId == EMPTY_BANANA_TREE_ID)
			return "banana";
		else if (treeId == PINEAPPLE_TREE_ID || treeId == EMPTY_PINEAPPLE_TREE_ID)
			return "pineapple";
		return "";
	}

	private int getFruitId(int treeId) {
		if (treeId == BANANA_TREE_ID)
			return ItemId.BANANA.id();
		else if (treeId == PINEAPPLE_TREE_ID)
			return ItemId.FRESH_PINEAPPLE.id();
		return -1;
	}

	private int getEmptyTreeId(int treeId) {
		if (treeId == BANANA_TREE_ID) return EMPTY_BANANA_TREE_ID;
		if (treeId == PINEAPPLE_TREE_ID) return EMPTY_PINEAPPLE_TREE_ID;
		return -1;
	}
}
