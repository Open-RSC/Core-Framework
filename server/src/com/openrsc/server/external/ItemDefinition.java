package com.openrsc.server.external;

/**
 * @author ephemeral
 */
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
	private int armourBonus;

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

	private int originalItemID;

	private int noteID;


	/**
	 * Creates a new default instance of this <code>ItemDefinition</code>.
	 *
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
	public ItemDefinition(String name, String description, String[] command, boolean isFemaleOnly, boolean isMembersOnly,
						  boolean isStackable, boolean isUntradable, boolean isWearable, int appearanceID, int wearableID,
						  int wearSlot, int requiredLevel, int requiredSkillID, int armourBonus, int weaponAimBonus,
						  int weaponPowerBonus, int magicBonus, int prayerBonus, int basePrice, int noted, int original) {
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
		this.noteID = noted;
		this.originalItemID = original;
	}


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

	/**
	 * Returns the current appearance-ID
	 * for this <code>ItemDefinition</code>.
	 *
	 * @return Returns the appearance-ID.
	 */
	public final int getAppearanceId() {
		return appearanceId;
	}

	/**
	 * Returns the current armour-bonus
	 * for this <code>ItemDefinition</code>.
	 *
	 * @return Returns the armour-bonus.
	 */
	public final int getArmourBonus() {
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

	/**
	 * Returns the current required skill-index
	 * for this <code>ItemDefinition</code>.
	 *
	 * @return Returns the required skill-index.
	 */
	public final int getRequiredSkillIndex() {
		return requiredSkillIndex;
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

	/**
	 * Returns the current weapon power-bonus
	 * for this <code>ItemDefinition</code>.
	 *
	 * @return Returns the weapon power-bonus.
	 */
	public final int getWeaponPowerBonus() {
		return weaponPowerBonus;
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

	/**
	 * Returns the current worn-item-index
	 * for this <code>ItemDefinition</code>.
	 *
	 * @return Returns the worn-item-index.
	 */
	public final int getWieldPosition() {
		return wornItemIndex;
	}


	public int getOriginalItemID() {
		return originalItemID;
	}

	public int getNoteID() {
		return noteID;
	}

	public void nullCommand() { this.command = null; }
}
