package com.openrsc.server.util.rsc;

import java.util.HashMap;
import java.util.Map;

/**
 * Mapping class from modern to "retro" (May 2001) appearance id
 * */
public class AppearanceRetroConverter {
	private static final Map<Integer, Integer> map = new HashMap<Integer, Integer>() {{
		put(117, 89); // ironmace
		put(49, 43); // ironsword
		put(99, 75); // ironsquareshield
		put(106, 79); // woodensquareshield
		put(71, 53); // ironmediumhelm
		put(14, 14); // ironfullhelm
		put(22, 22); // ironchainmail
		put(29, 27); // ironplatemailtop
		put(38, 34); // ironplatemaillegs
		put(110, 83); // ironbattleaxe
		put(46, 40); // leatherarmour
		put(47, 41); // leathergloves
		put(12, 18); // boots
		put(80, 60); // necklace
		put(107, 80); // crossbow
		put(48, 42); // bronzesword
		put(50, 44); // steelsword
		put(51, 45); // mithrilsword
		put(52, 46); // adamantitesword
		put(53, 47); // runesword
		put(109, 82); // bronzebattleaxe
		put(111, 84); // steelbattleaxe
		put(112, 85); // mithrilbattleaxe
		put(113, 86); // adamantitebattleaxe
		put(114, 87); // runebattleaxe
		put(116, 88); // bronzemace
		put(118, 90); // steelmace
		put(119, 91); // mithrilmace
		put(120, 92); // adamantitemace
		put(121, 93); // runemace
		put(123, 94); // staff
		put(70, 52); // bronzemediumhelm
		put(72, 54); // steelmediumhelm
		put(73, 55); // mithrilmediumhelm
		put(74, 56); // adamantitemediumhelm
		put(13, 13); // bronzefullhelm
		put(15, 15); // steelfullhelm
		put(16, 16); // mithrilfullhelm
		put(17, 17); // adamantitefullhelm
		put(18, 18); // runefullhelm
		put(21, 21); // bronzechainmail
		put(23, 23); // steelchainmail
		put(24, 24); // mithrilchainmail
		put(25, 25); // adamantitechainmail
		put(28, 26); // bronzeplatemailtop
		put(30, 28); // steelplatemailtop
		put(31, 29); // mithrilplatemailtop
		put(32, 30); // adamantiteplatemailtop
		put(39, 35); // steelplatemaillegs
		put(40, 36); // mithrilplatemaillegs
		put(41, 37); // adamantiteplatemaillegs
		put(98, 74); // bronzesquareshield
		put(100, 76); // steelsquareshield
		put(101, 77); // mithrilsquareshield
		put(102, 78); // adamantitesquareshield
		put(10, 10); // whiteapron
		put(63, 49); // redcape
		put(77, 57); // wizardsrobe
		put(78, 58); // wizardshat
		put(82, 61); // blueskirt
		put(108, 81); // longbow
		put(11, 11); // brownapron
		put(9, 9); // chefshat
		put(90, 67); // pinkskirt
		put(89, 66); // blackskirt
		put(33, 31); // blackplatemailtop
		put(79, 59); // blackwizardshat
		put(37, 33); // bronzeplatemaillegs
		put(64, 50); // blackcape
		put(92, 69); // bronzeskirt
		put(93, 70); // ironskirt
		put(83, 62); // blackrobe
		put(94, 71); // steelskirt
		put(95, 72); // mithrilskirt
		put(96, 73); // adamantiteskirt
		put(65, 51); // bluecape
		put(19, 19); // blackfullhelm
		put(43, 38); // blackplatemaillegs
	}};

	public static Integer convert(int modernId) {
		return map.getOrDefault(modernId, modernId);
	}
}
