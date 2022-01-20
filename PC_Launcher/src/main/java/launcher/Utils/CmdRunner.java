package launcher.Utils;

import java.io.File;

public class CmdRunner implements Runnable {
	String[] cmdArray;
	File workingDirectory = null;
	boolean needsOutput = false;
	public String output = "";

	public CmdRunner(String[] cmdArray) {
		this.cmdArray = cmdArray;
	}

	public CmdRunner(String[] cmdArray, boolean needsOutput) {
		this.cmdArray = cmdArray;
		this.needsOutput = needsOutput;
	}

	public CmdRunner(String[] cmdArray, File workingDirectory) {
		this.cmdArray = cmdArray;
		this.workingDirectory = workingDirectory;
	}

	@Override
	public void run() {
		try {
			java.util.Scanner s =
				new java.util.Scanner(Runtime.getRuntime().exec(this.cmdArray, null, this.workingDirectory).getInputStream())
					.useDelimiter("\\A");
			if (s.hasNext()) {
				output = s.next();
				if (needsOutput) {
					Utils.lastCommandOutput = output;
					Utils.outputCommandRunning = false;
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
