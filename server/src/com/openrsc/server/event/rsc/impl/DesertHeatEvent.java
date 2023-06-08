package com.openrsc.server.event.rsc.impl;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.event.rsc.DuplicationStrategy;
import com.openrsc.server.event.rsc.GameTickEvent;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.container.CarriedItems;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.util.rsc.DataConversions;

public class DesertHeatEvent extends GameTickEvent {
	final private Mob mob;
	private boolean desertHeatMessaged = false;
	public int desertHeatCounter = Integer.MIN_VALUE;

	public DesertHeatEvent(World world, Mob owner) {
		super(world, owner, 0, "Desert Heat", DuplicationStrategy.ONE_PER_MOB);
		this.mob = owner;
		Player player = (Player) mob;
		if (player.getCache().hasKey("desert_heat_counter")) {
			this.desertHeatCounter = (int)player.getCache().getLong("desert_heat_counter");
			player.getCache().remove("desert_heat_counter");
		}
	}

	@Override
	public void run() {
		doDesertHeat();
	}

	public void doDesertHeat() {
		if (inDesert()) {
			if (this.desertHeatCounter == Integer.MIN_VALUE) {
				this.desertHeatCounter = calculateHeatDelay();
				this.doEvaporate();
			}

			this.desertHeatCounter -= 1;

			if (this.desertHeatCounter <= 0) {
				this.doHeatStroke();
				this.desertHeatCounter = calculateHeatDelay();
			}
		} else if (this.desertHeatCounter != Integer.MIN_VALUE) {
			this.desertHeatCounter = Integer.MIN_VALUE;
			this.desertHeatMessaged = false;
		}
	}

	public boolean inDesert() {
		Player player = (Player) mob;
		Point loc = player.getLocation();
		int x = loc.getX();
		int y = loc.getY();

		//Check if they could be in the desert
		if (loc.inBounds(48, 721, 189, 815)) {

			//Check if they are standing in Shantay pass
			if (loc.inBounds(59, 721, 67, 732))
				return false;

				//Check if they are along the dunes of Al Kharid
			else if (y <= 722 && x <= 82)
				return false;

				//Check if they are in the mining camp
			else if (loc.inBounds(80, 799, 91, 812))
				return false;

				//Check if they are in Bedabin camp
			else if (x >= 160 && y >= 784)
				return false;

			return true;
		}

		return false;
	}

	private int calculateHeatDelay() {
		//Default tick delay of 140
		//On base tick rate (0.64) this is 89.6 seconds
		//On cabbage tick rate (0.43) this is 60.2 seconds

		//Possible tick delay range depending on equipped items: 40-190
		Player player = (Player) mob;
		int tickDelay = 140;

		Item item = player.getEquippedChest();
		if (item != null) {
			if (item.getCatalogId() == ItemId.DESERT_SHIRT.id())
				tickDelay += 20;
			else if (item.getDef(getWorld()).getArmourBonus() > 0)
				tickDelay -= 40;
		}

		item = player.getEquippedLegs();
		if (item != null) {
			if (item.getCatalogId() == ItemId.DESERT_ROBE.id())
				tickDelay += 20;
			else if (item.getDef(getWorld()).getArmourBonus() > 0)
				tickDelay -= 30;
		}

		item = player.getEquippedBoots();
		if (item != null) {
			if (item.getCatalogId() == ItemId.DESERT_BOOTS.id())
				tickDelay += 10;
			else if (item.getDef(getWorld()).getArmourBonus() > 0)
				tickDelay -= 10;
		}

		item = player.getEquippedGloves();
		if (item != null) {
			if (item.getDef(getWorld()).getArmourBonus() > 0)
				tickDelay -= 10;
		}

		item = player.getEquippedHelmet();
		if (item != null) {
			if (item.getDef(getWorld()).getArmourBonus() > 0)
				tickDelay -= 10;
		}

		return tickDelay;
	}

	//The desert heat has ticked. Look for water or cause damage.
	private void doHeatStroke() {
		Player player = (Player) mob;
		CarriedItems ci = player.getCarriedItems();

		if (ci.remove(new Item(ItemId.WATER_SKIN_MOUTHFUL_LEFT.id(), 1)) >= 0)
			ci.getInventory().add(new Item(ItemId.EMPTY_WATER_SKIN.id(), 1));
		else if (ci.remove(new Item(ItemId.WATER_SKIN_MOSTLY_EMPTY.id(), 1)) >= 0)
			ci.getInventory().add(new Item(ItemId.WATER_SKIN_MOUTHFUL_LEFT.id(), 1));
		else if (ci.remove(new Item(ItemId.WATER_SKIN_MOSTLY_FULL.id(), 1)) >= 0)
			ci.getInventory().add(new Item(ItemId.WATER_SKIN_MOSTLY_EMPTY.id(), 1));
		else if (ci.remove(new Item(ItemId.FULL_WATER_SKIN.id(), 1)) >= 0)
			ci.getInventory().add(new Item(ItemId.WATER_SKIN_MOSTLY_FULL.id(), 1));
		else {
			if (!this.desertHeatMessaged) {
				player.message("You start dying of thirst while you're in the desert.");
				this.desertHeatMessaged = true;
			}

			int damage = DataConversions.getRandom().nextInt(10);
			player.damage(damage + 1);
			return;
		}

		this.desertHeatMessaged = false;
	}

	public void doEvaporate() {
		Player player = (Player) mob;
		CarriedItems ci = player.getCarriedItems();
		int jugCount = 0;
		int bowlCount = 0;
		int bucketCount = 0;
		//Jugs
		while (ci.remove(new Item(ItemId.JUG_OF_WATER.id(), 1), false) >= 0) {
			++jugCount;
			ci.getInventory().add(new Item(ItemId.JUG.id(), 1), false);
		}

		//Bowls
		while (ci.remove(new Item(ItemId.BOWL_OF_WATER.id(), 1), false) >= 0) {
			++bowlCount;
			ci.getInventory().add(new Item(ItemId.BOWL.id(), 1), false);
		}

		//Buckets
		while (ci.remove(new Item(ItemId.BUCKET_OF_WATER.id(), 1), false) >= 0) {
			++bucketCount;
			ci.getInventory().add(new Item(ItemId.BUCKET.id(), 1), false);
		}

		if (jugCount > 1)
			player.message("The water in your jugs evaporates in the desert heat.");
		else if (jugCount > 0)
			player.message("The water in your jug evaporates in the desert heat.");

		if (bowlCount > 1)
			player.message("The water in your bowls evaporates in the desert heat.");
		else if (bowlCount > 0)
			player.message("The water in your bowl evaporates in the desert heat.");

		if (bucketCount > 1)
			player.message("The water in your buckets evaporates in the desert heat.");
		else if (bucketCount > 0)
			player.message("The water in your bucket evaporates in the desert heat.");

		if (jugCount > 0 || bowlCount > 0 || bucketCount > 0)
			ActionSender.sendInventory(player);
	}
}
