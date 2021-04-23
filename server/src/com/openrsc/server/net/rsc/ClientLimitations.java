package com.openrsc.server.net.rsc;

public class ClientLimitations {
	private final int NO_SUPPORT = 0;
	private final int MOD_JMOD_SUPPORT = 1;
	private final int AUTHENTIC_SOUNDS_ONLY = 37;

	int maxAnimationId, maxItemId, maxNpcId, maxSceneryId, maxPrayerId, maxSpellId,
		maxSkillId, maxRoofId, maxTextureId, maxTileId, maxBoundaryId, maxTeleBubbleId,
		maxProjectileSprite, maxSkinColor, maxHairColor, maxClothingColor, maxQuestId,
		maxDialogueOptions, maxBankItems;
	String mapHash;
	int supportsModSprites = NO_SUPPORT;
	int numberOfSounds = NO_SUPPORT;

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

		// fallback for old custom client (pre May 2021)
		// TODO: can probably remove this code in a few months if desired to break very out of date clients
		else if (clientVersion == 8) {
			// not necessarily what the client is supporting on the server's config at this time
			// but what the client was capable of accepting with proper config in May 2021
			maxAnimationId = 482;
			maxItemId = 1486;
			maxNpcId = 815;
			maxSceneryId = 1294;
			maxPrayerId = 13;
			maxSpellId = 47;
			maxSkillId = 19;
			maxRoofId = 5;
			maxTextureId = 54;
			maxTileId = 24;
			maxBoundaryId = 213;
			maxTeleBubbleId = 1;
			maxProjectileSprite = 6;
			maxSkinColor = 4;
			maxHairColor = 9;
			maxClothingColor = 14;
			maxQuestId = 99;
			numberOfSounds = 37;
			supportsModSprites = 4;
			maxDialogueOptions = 20;
			maxBankItems = Integer.MAX_VALUE;
			mapHash = "0dd0a1e47767f7f64b7931688131512f";
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
