package rsc.graphics.three;

import rsc.util.GenUtil;

public class ModelFileManager {
	public static String[] modelFile = new String[5000];
	public static int modelFiles = 0;

	public static final int getModelFileIndex(String model) {
		try {
			if (!model.equalsIgnoreCase("na")) {
				for (int var2 = 0; var2 < modelFiles; ++var2) {
					if (modelFile[var2].equalsIgnoreCase(model)) {
						return var2;
					}
				}

				modelFile[modelFiles++] = model;
				return modelFiles - 1;
			} else {
				return 0;
			}
		} catch (RuntimeException var3) {
			throw GenUtil.makeThrowable(var3, "ca.CA(" + "dummy" + ',' + (model != null ? "{...}" : "null") + ')');
		}
	}

}
