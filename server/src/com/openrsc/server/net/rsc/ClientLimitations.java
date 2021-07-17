package com.openrsc.server.net.rsc;

public class ClientLimitations {
	private final int NO_SUPPORT = 0;
	private final int MOD_JMOD_SUPPORT = 1;
	private final int AUTHENTIC_SOUNDS_ONLY = 37;

	public int maxAnimationId, maxItemId, maxNpcId, maxSceneryId, maxPrayerId, maxSpellId,
		maxSkillId, maxRoofId, maxTextureId, maxTileId, maxBoundaryId, maxTeleBubbleId,
		maxProjectileSprite, maxSkinColor, maxHairColor, maxClothingColor, maxQuestId,
		maxDialogueOptions, maxBankItems;
	public String mapHash;
	int supportsModSprites = NO_SUPPORT;
	int numberOfSounds = NO_SUPPORT;
	public boolean supportsItemBank = false;
	public boolean supportsConfirmTrade = false;
	public boolean supportsIntegerStacks = false;
	public boolean supportsClickMine = false;
	public boolean supportsClickWoodcut = false;
	public boolean supportsClickFish = false;
	public boolean supportsTypedPickaxes = false;
	public boolean supportsSkillUpdate = false;

	ClientLimitations(int clientVersion) {
		setKnownLimitations(clientVersion);
	}

	public void setKnownLimitations(int clientVersion) {
		if (clientVersion >= 38 && clientVersion <= 40) {
			maxAnimationId = 114;
			maxItemId = 306;
			maxNpcId = 157;
			maxSceneryId = 179;
			maxPrayerId = -1;
			if (clientVersion == 38) {
				maxSkillId = 18;
				maxSpellId = 10;
			} else {
				maxSkillId = 15;
				maxSpellId = 8;
			}
			maxRoofId = -1; // not implemented until client 115 on 2001-12-24
			maxTextureId = -1; // not implemented until client 115 on 2001-12-24
			maxTileId = 2;
			maxBoundaryId = 46;
			maxTeleBubbleId = -1; // not implemented until client 119 on 2002-01-24
			maxProjectileSprite = 2;
			maxSkinColor = 4;
			maxHairColor = 9;
			maxClothingColor = 14;
			maxQuestId = -1; // quest list not implemented until 2001-05-28
			maxDialogueOptions = 5;
			maxBankItems = 0; // item bank not implemented until client 72 on 2001-07-26
			mapHash = "14";
		}

		else if (clientVersion >= 177 && clientVersion <= 235) {
			// not sure which client in 2009 added this feature
			// 224 is the only 2009 RSC client I have.
			if (clientVersion >= 224) {
				supportsModSprites = MOD_JMOD_SUPPORT;
			}
			maxAnimationId = 228;
			maxItemId = 1289;
			maxNpcId = 793;
			maxSceneryId = 1188;
			maxPrayerId = 13;
			maxSpellId = 47;
			maxSkillId = 17;
			maxRoofId = 5;
			maxTextureId = 54;
			maxTileId = 24;
			maxBoundaryId = 213;
			maxTeleBubbleId = 1;
			maxProjectileSprite = 6;
			maxSkinColor = 4;
			maxHairColor = 9;
			maxClothingColor = 14;
			maxQuestId = 49;
			mapHash = "63";
			maxDialogueOptions = 5;
			maxBankItems = 48 * 4;
			numberOfSounds = AUTHENTIC_SOUNDS_ONLY;
		}

		if (clientVersion >= 72) {
			supportsItemBank = true;
		}
		if (clientVersion >= 105) {
			supportsIntegerStacks = true;
		}
		if (clientVersion >= 106) {
			supportsConfirmTrade = true;
		}
		if (clientVersion >= 110) {
			supportsSkillUpdate = true;
		}
		if (clientVersion >= 124) {
			supportsClickMine = true;
			supportsClickFish = true;
		}
		if (clientVersion >= 128) {
			supportsClickWoodcut = true;
		}
		if (clientVersion >= 170) {
			supportsTypedPickaxes = true;
		}
	}

	@Override
	public String toString() {
		return String.format("@ora@Client Limitations%%@gre@maxAnimationId: @whi@%d, @gre@maxItemId: @whi@%d, @gre@maxNpcId: @whi@%d, @gre@maxSceneryId: @whi@%d, @gre@maxPrayerId: @whi@%d, @gre@maxSpellId: @whi@%d, @gre@maxSkillId: @whi@%d, @gre@maxRoofId: @whi@%d, @gre@maxTextureId: @whi@%d, @gre@maxTileId: @whi@%d, @gre@maxBoundaryId: @whi@%d, @gre@maxTeleBubbleId: @whi@%d, @gre@maxProjectileSprite: @whi@%d, @gre@maxSkinColor: @whi@%d, @gre@maxHairColor: @whi@%d, @gre@maxClothingColor: @whi@%d, @gre@maxQuestId: @whi@%d, @gre@maxDialogueOptions: @whi@%d, @gre@maxBankItems: @whi@%d, @gre@mapHash: @whi@%s",
			maxAnimationId, maxItemId, maxNpcId, maxSceneryId, maxPrayerId, maxSpellId,
			maxSkillId, maxRoofId, maxTextureId, maxTileId, maxBoundaryId, maxTeleBubbleId,
			maxProjectileSprite, maxSkinColor, maxHairColor, maxClothingColor, maxQuestId,
			maxDialogueOptions, maxBankItems, mapHash);
	}
}
