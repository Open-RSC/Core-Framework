package orsc.util;

import java.util.ArrayList;

public class BankUtil {

	public static boolean isCert(int itemID) {
		int[] certIds = {
			/* Ores **/
			517, 518, 519, 520, 521,
			/* Bars **/
			528, 529, 530, 531, 532,
			/* Fish **/
			533, 534, 535, 536, 628, 629, 630, 631,
			/* Logs **/
			711, 712, 713,
			/* Misc **/
			1270, 1271, 1272, 1273, 1274, 1275
		};
		ArrayList<Integer> certArr = new ArrayList<Integer>();
		for (int id : certIds) {
			certArr.add(id);
		}
		return certArr.contains(itemID);
	}

	public static int uncertedID(int itemID) {

		if (itemID == 517) {
			return 151;
		} else if (itemID == 518) {
			return 155;
		} else if (itemID == 519) {
			return 153;
		} else if (itemID == 520) {
			return 383;
		} else if (itemID == 521) {
			return 152;
		} else if (itemID == 528) {
			return 170;
		} else if (itemID == 529) {
			return 171;
		} else if (itemID == 530) {
			return 173;
		} else if (itemID == 531) {
			return 384;
		} else if (itemID == 532) {
			return 172;
		} else if (itemID == 533) {
			return 373;
		} else if (itemID == 534) {
			return 372;
		} else if (itemID == 535) {
			return 370;
		} else if (itemID == 536) {
			return 369;
		} else if (itemID == 628) {
			return 555;
		} else if (itemID == 629) {
			return 554;
		} else if (itemID == 630) {
			return 546;
		} else if (itemID == 631) {
			return 545;
		} else if (itemID == 711) {
			return 635;
		} else if (itemID == 712) {
			return 634;
		} else if (itemID == 713) {
			return 633;
		} else if (itemID == 1270) {
			return 814;
		} else if (itemID == 1271) {
			return 220;
		} else if (itemID == 1272) {
			return 483;
		} else if (itemID == 1273) {
			return 486;
		} else if (itemID == 1274) {
			return 495;
		} else if (itemID == 1275) {
			return 492;
		} else {
			return itemID;
		}
	}
}
