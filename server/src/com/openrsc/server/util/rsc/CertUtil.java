package com.openrsc.server.util.rsc;

import com.openrsc.server.constants.ItemId;

public class CertUtil {

	public static boolean isCert(int catalogID) {
		int[] certIds = {
			/* Ores **/
			ItemId.IRON_ORE_CERTIFICATE.id(), ItemId.COAL_CERTIFICATE.id(), ItemId.MITHRIL_ORE_CERTIFICATE.id(), ItemId.SILVER_CERTIFICATE.id(), ItemId.GOLD_CERTIFICATE.id(),
			/* Bars **/
			ItemId.IRON_BAR_CERTIFICATE.id(), ItemId.STEEL_BAR_CERTIFICATE.id(), ItemId.MITHRIL_BAR_CERTIFICATE.id(), ItemId.SILVER_BAR_CERTIFICATE.id(), ItemId.GOLD_BAR_CERTIFICATE.id(),
			/* Fish **/
			ItemId.LOBSTER_CERTIFICATE.id(), ItemId.RAW_LOBSTER_CERTIFICATE.id(), ItemId.SWORDFISH_CERTIFICATE.id(), ItemId.RAW_SWORDFISH_CERTIFICATE.id(), ItemId.BASS_CERTIFICATE.id(), ItemId.RAW_BASS_CERTIFICATE.id(), ItemId.SHARK_CERTIFICATE.id(), ItemId.RAW_SHARK_CERTIFICATE.id(),
			/* Logs **/
			ItemId.YEW_LOGS_CERTIFICATE.id(), ItemId.MAPLE_LOGS_CERTIFICATE.id(), ItemId.WILLOW_LOGS_CERTIFICATE.id(),
			/* Misc **/
			ItemId.DRAGON_BONE_CERTIFICATE.id(), ItemId.LIMPWURT_ROOT_CERTIFICATE.id(), ItemId.PRAYER_POTION_CERTIFICATE.id(), ItemId.SUPER_ATTACK_POTION_CERTIFICATE.id(), ItemId.SUPER_DEFENSE_POTION_CERTIFICATE.id(), ItemId.SUPER_STRENGTH_POTION_CERTIFICATE.id()
		};

		return DataConversions.inArray(certIds, catalogID);
	}
}
