package orsc.graphics.three;

import orsc.util.GenUtil;

class ModelFileManager {
	private static String[] modelFile = new String[5000];
	private static int modelFiles = 0;

	static void getModelFileIndex(String model) {
		try {
			if (!model.equalsIgnoreCase("na")) {
				for (int var2 = 0; var2 < modelFiles; ++var2) {
					if (modelFile[var2].equalsIgnoreCase(model)) {
						return;
					}
				}

				modelFile[modelFiles++] = model;
			}
		} catch (RuntimeException var3) {
			throw GenUtil.makeThrowable(var3, "ca.CA(" + "dummy" + ',' + (model != null ? "{...}" : "null") + ')');
		}
	}

}