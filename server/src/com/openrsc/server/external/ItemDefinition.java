package com.openrsc.server.external;

public final class ItemDefinition extends EntityDef {
	/**
	 * The command for an <code>InventoryItem</code>,
	 * or a <code>GroundItem</code> represented by
	 * this <code>ItemDefinition</code>.
	 */
	private String[] command;

	/**
	 * The description for an <code>InventoryItem</code>,
	 * or a <code>GroundItem</code> represented by
	 * this <code>ItemDefinition</code>.
	 */
	private String description;

	/**
	 * The name for an <code>InventoryItem</code>,
	 * or a <code>GroundItem</code> represented by
	 * this <code>ItemDefinition</code>.
	 */
	private String name;

	/**
	 * The female-only verifier-status for an
	 * <code>InventoryItem</code>, or a
	 * <code>GroundItem</code> represented by
	 * this <code>ItemDefinition</code>.
	 */
	private boolean isFemaleOnly;

	/**
	 * The members-only verifier-status for an
	 * <code>InventoryItem</code>, or a
	 * <code>GroundItem</code> represented by
	 * this <code>ItemDefinition</code>.
	 */
	private boolean isMembersOnly;

	/**
	 * The stackability verifier-status for an
	 * <code>InventoryItem</code>, or a
	 * <code>GroundItem</code> represented by
	 * this <code>ItemDefinition</code>.
	 */
	private boolean isStackable;

	/**
	 * The untradability verifier-status for an
	 * <code>InventoryItem</code>, or a
	 * <code>GroundItem</code> represented by
	 * this <code>ItemDefinition</code>.
	 */
	private boolean isUntradable;

	/**
	 * The wearability verifier-status for an
	 * <code>InventoryItem</code>, or a
	 * <code>GroundItem</code> represented by
	 * this <code>ItemDefinition</code>.
	 */
	private boolean isWearable;

	/**
	 * The appearance-ID for an <code>InventoryItem</code>,
	 * or a <code>GroundItem</code> represented by
	 * this <code>ItemDefinition</code>.
	 */
	private int appearanceId;

	/**
	 * The armour-bonus for an <code>InventoryItem</code>,
	 * or a <code>GroundItem</code> represented by
	 * this <code>ItemDefinition</code>.
	 */
	private long armourBonus;

	/**
	 * The default price for an <code>InventoryItem</code>,
	 * or a <code>GroundItem</code> represented by
	 * this <code>ItemDefinition</code>.
	 */
	private int defaultPrice;

	/**
	 * The ID for an <code>InventoryItem</code>,
	 * or a <code>GroundItem</code> represented by
	 * this <code>ItemDefinition</code>.
	 */
	private int id;

	/**
	 * The magic-bonus for an <code>InventoryItem</code>,
	 * or a <code>GroundItem</code> represented by
	 * this <code>ItemDefinition</code>.
	 */
	private int magicBonus;

	/**
	 * The prayer-bonus for an <code>InventoryItem</code>,
	 * or a <code>GroundItem</code> represented by
	 * this <code>ItemDefinition</code>.
	 */
	private int prayerBonus;

	/**
	 * The required level for an <code>InventoryItem</code>,
	 * or a <code>GroundItem</code> represented by
	 * this <code>ItemDefinition</code>.
	 */
	private int requiredLevel;

	/**
	 * The required skill-index for an <code>InventoryItem</code>,
	 * or a <code>GroundItem</code> represented by
	 * this <code>ItemDefinition</code>.
	 */
	private int requiredSkillIndex;

	/**
	 * The weapon aim-bonus for an <code>InventoryItem</code>,
	 * or a <code>GroundItem</code> represented by
	 * this <code>ItemDefinition</code>.
	 */
	private int weaponAimBonus;

	/**
	 * The weapon power-bonus for an <code>InventoryItem</code>,
	 * or a <code>GroundItem</code> represented by
	 * this <code>ItemDefinition</code>.
	 */
	private int weaponPowerBonus;

	/**
	 * The wearable-ID for an <code>InventoryItem</code>,
	 * or a <code>GroundItem</code> represented by
	 * this <code>ItemDefinition</code>.
	 */
	private int wearableId;

	/**
	 * The worn-item-index for an <code>InventoryItem</code>,
	 * or a <code>GroundItem</code> represented by
	 * this <code>ItemDefinition</code>.
	 */
	private int wornItemIndex;

	/**
	 * The noteability verifier-status for an
	 * <code>InventoryItem</code>, or a
	 * <code>GroundItem</code> represented by
	 * this <code>ItemDefinition</code>.
	 */
	private boolean isNoteable;


	/**
	 * Creates a new default instance of this <code>ItemDefinition</code>.
	 *
	 * @param isNoteable
	 * @param basePrice
	 * @param prayerBonus
	 * @param magicBonus
	 * @param weaponPowerBonus
	 * @param weaponAimBonus
	 * @param armourBonus
	 * @param requiredSkillID
	 * @param requiredLevel
	 * @param wearSlot
	 * @param wearableID
	 * @param appearanceID
	 * @param isWearable
	 * @param isUntradable
	 * @param isStackable
	 * @param isMembersOnly
	 * @param isFemaleOnly
	 * @param description
	 * @param name
	 * @param command
	 */
	public ItemDefinition(int id, String name, String description, String[] command, boolean isFemaleOnly, boolean isMembersOnly,
						  boolean isStackable, boolean isUntradable, boolean isWearable, int appearanceID, int wearableID,
						  int wearSlot, int requiredLevel, int requiredSkillID, long armourBonus, int weaponAimBonus,
						  int weaponPowerBonus, int magicBonus, int prayerBonus, int basePrice, boolean isNoteable) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.command = command;
		this.isFemaleOnly = isFemaleOnly;
		this.isMembersOnly = isMembersOnly;
		this.isStackable = isStackable;
		this.isUntradable = isUntradable;
		this.isWearable = isWearable;
		this.appearanceId = appearanceID;
		this.wearableId = wearableID;
		this.wornItemIndex = wearSlot;
		this.requiredLevel = requiredLevel;
		this.requiredSkillIndex = requiredSkillID;
		this.armourBonus = armourBonus;
		this.weaponAimBonus = weaponAimBonus;
		this.weaponPowerBonus = weaponPowerBonus;
		this.magicBonus = magicBonus;
		this.prayerBonus = prayerBonus;
		this.defaultPrice = basePrice;
		this.isNoteable = isNoteable;
	}

	public ItemDefinition(ItemDefinitionBuilder builder) {
		this(builder.id, builder.name, builder.description, builder.command, builder.isFemaleOnly, builder.isMembersOnly,
			builder.isStackable, builder.isUntradable, builder.isWearable, builder.appearanceId, builder.wearableId,
			builder.wornItemIndex, builder.requiredLevel, builder.requiredSkillIndex, builder.armourBonus, builder.weaponAimBonus,
			builder.weaponPowerBonus, builder.magicBonus, builder.prayerBonus, builder.defaultPrice, builder.isNoteable);
	}

	public ItemDefinition() { }


	/**
	 * Returns the current command for this
	 * <code>ItemDefinition</code>.
	 *
	 * @return Returns the command.
	 */
	public final String[] getCommand() {
		return command;
	}

	/**
	 * Returns the current description for this
	 * <code>ItemDefinition</code>.
	 *
	 * @return Returns the description.
	 */
	public final String getDescription() {
		return description;
	}

	/**
	 * Returns the current name for this
	 * <code>ItemDefinition</code>.
	 *
	 * @return Returns the name.
	 */
	public final String getName() {
		return name;
	}

	/**
	 * Returns the current female-only
	 * verifier-status for this
	 * <code>ItemDefinition</code>.
	 *
	 * @return Returns the female-only
	 * verifier-status.
	 */
	public final boolean isFemaleOnly() {
		return isFemaleOnly;
	}

	/**
	 * Returns the current members-only
	 * verifier-status for this
	 * <code>ItemDefinition</code>.
	 *
	 * @return Returns the members-only
	 * verifier-status.
	 */
	public final boolean isMembersOnly() {
		return isMembersOnly;
	}

	/**
	 * Returns the current stackability
	 * verifier-status for this
	 * <code>ItemDefinition</code>.
	 *
	 * @return Returns the stackability
	 * verifier-status.
	 */
	public final boolean isStackable() {
		return isStackable;
	}

	/**
	 * Returns the current untradability
	 * verifier-status for this
	 * <code>ItemDefinition</code>.
	 *
	 * @return Returns the untradability
	 * verifier-status.
	 */
	public final boolean isUntradable() {
		return isUntradable;
	}

	/**
	 * Returns the current wearability
	 * verifier-status for this
	 * <code>ItemDefinition</code>.
	 *
	 * @return Returns the wearability
	 * verifier-status.
	 */
	public final boolean isWieldable() {
		return isWearable;
	}
	public void setWieldable(boolean wieldable) {
		this.isWearable = wieldable;
	}

	/**
	 * Returns the current appearance-ID
	 * for this <code>ItemDefinition</code>.
	 *
	 * @return Returns the appearance-ID.
	 */
	public final int getAppearanceId() {
		return appearanceId;
	}
	public void setAppearanceId(int appearanceId) {
		this.appearanceId = appearanceId;
	}

	/**
	 * Returns the current armour-bonus
	 * for this <code>ItemDefinition</code>.
	 *
	 * @return Returns the armour-bonus.
	 */
	public final long getArmourBonus() {
		return armourBonus;
	}

	/**
	 * Returns the current default price
	 * for this <code>ItemDefinition</code>.
	 *
	 * @return Returns the default price.
	 */
	public final int getDefaultPrice() {
		return defaultPrice;
	}

	/**
	 * Returns the current ID
	 * for this <code>ItemDefinition</code>.
	 *
	 * @return Returns the ID.
	 */
	public final int getId() {
		return id;
	}

	/**
	 * Returns the current magic-bonus
	 * for this <code>ItemDefinition</code>.
	 *
	 * @return Returns the magic-bonus.
	 */
	public final int getMagicBonus() {
		return magicBonus;
	}

	/**
	 * Returns the current prayer-bonus
	 * for this <code>ItemDefinition</code>.
	 *
	 * @return Returns the prayer-bonus.
	 */
	public final int getPrayerBonus() {
		return prayerBonus;
	}

	/**
	 * Returns the current required level
	 * for this <code>ItemDefinition</code>.
	 *
	 * @return Returns the required level.
	 */
	public final int getRequiredLevel() {
		return requiredLevel;
	}
	public void setRequiredLevel(int requiredLevel) {
		this.requiredLevel = requiredLevel;
	}

	/**
	 * Returns the current required skill-index
	 * for this <code>ItemDefinition</code>.
	 *
	 * @return Returns the required skill-index.
	 */
	public final int getRequiredSkillIndex() {
		return requiredSkillIndex;
	}
	public void setRequiredSkillIndex(int index) {
		this.requiredSkillIndex = index;
	}

	/**
	 * Returns the current weapon aim-bonus
	 * for this <code>ItemDefinition</code>.
	 *
	 * @return Returns the weapon aim-bonus.
	 */
	public final int getWeaponAimBonus() {
		return weaponAimBonus;
	}
	public void setWeaponAimBonus(int bonus) {
		this.weaponAimBonus = bonus;
	}

	/**
	 * Returns the current weapon power-bonus
	 * for this <code>ItemDefinition</code>.
	 *
	 * @return Returns the weapon power-bonus.
	 */
	public final int getWeaponPowerBonus() {
		return weaponPowerBonus;
	}
	public void setWeaponPowerBonus(int bonus) {
		this.weaponPowerBonus = bonus;
	}

	/**
	 * Returns a virtual melee-bonus
	 * for this <code>ItemDefinition</code>.
	 *
	 * @return Returns the melee-bonus.
	 */
	public final long getMeleeBonus() {
		return armourBonus + weaponAimBonus + weaponAimBonus;
	}

	/**
	 * Returns the current wearable-ID
	 * for this <code>ItemDefinition</code>.
	 *
	 * @return Returns the wearable-ID.
	 */
	public final int getWearableId() {
		return wearableId;
	}
	public void setWearableId(int wearableId) {
		this.wearableId = wearableId;
	}

	/**
	 * Returns the current worn-item-index
	 * for this <code>ItemDefinition</code>.
	 *
	 * @return Returns the worn-item-index.
	 */
	public final int getWieldPosition() {
		return wornItemIndex;
	}
	public void setWieldPosition(int wieldPosition) {
		this.wornItemIndex = wieldPosition;
	}

	/**
	 * Computed function if the item is
	 * candidate of being note
	 *
	 * @return Returns the noteability
	 * status.
	 */
	public final boolean isNoteable() { return !isStackable && (!isUntradable || isNoteable); }


	@Deprecated
	public int getOriginalItemID() {
		return id;
	}

	@Deprecated
	public int getNoteID() {
		return id;
	}

	public void nullCommand() { this.command = null; }

	public static class ItemDefinitionBuilder
	{
		private String[] command;
		private String description;
		private String name;
		private boolean isFemaleOnly;
		private boolean isMembersOnly;
		private boolean isStackable;
		private boolean isUntradable;
		private boolean isWearable;
		private int appearanceId;
		private long armourBonus;
		private int defaultPrice;
		private int id;
		private int magicBonus;
		private int prayerBonus;
		private int requiredLevel;
		private int requiredSkillIndex;
		private int weaponAimBonus;
		private int weaponPowerBonus;
		private int wearableId;
		private int wornItemIndex;
		private boolean isNoteable;

		public ItemDefinitionBuilder(int id, String name) {
			this.id = id;
			this.name = name;
		}

		public ItemDefinitionBuilder description(String description) {
			this.description = description;
			return this;
		}

		public ItemDefinitionBuilder command(String[] command) {
			this.command = command;
			return this;
		}

		public ItemDefinitionBuilder isStackable(boolean isStackable) {
			this.isStackable = isStackable;
			return this;
		}

		public ItemDefinitionBuilder defaultPrice(int defaultPrice) {
			this.defaultPrice = defaultPrice;
			return this;
		}

		public ItemDefinitionBuilder armourBonus(long armourBonus) {
			this.armourBonus = armourBonus;
			return this;
		}

		public ItemDefinitionBuilder weaponAimBonus(int weaponAimBonus) {
			this.weaponAimBonus = weaponAimBonus;
			return this;
		}

		public ItemDefinitionBuilder weaponPowerBonus(int weaponPowerBonus) {
			this.weaponPowerBonus = weaponPowerBonus;
			return this;
		}

		public ItemDefinitionBuilder magicBonus(int magicBonus) {
			this.magicBonus = magicBonus;
			return this;
		}

		public ItemDefinitionBuilder prayerBonus(int prayerBonus) {
			this.prayerBonus = prayerBonus;
			return this;
		}

		public ItemDefinition build() {
			ItemDefinition definition =  new ItemDefinition(this);
			return definition;
		}
	}
}
