package launcher.Utils;

import java.io.*;

public class CmdRunner implements Runnable {
	String[] cmdArray;
	File workingDirectory = null;
	boolean needsOutput = false;
	public String output = "";

	public CmdRunner(String[] cmdArray, File workingDirectory, boolean needsOutput) {
		this.cmdArray = cmdArray;
		this.workingDirectory = workingDirectory;
		this.needsOutput = needsOutput;
	}

	@Override
	public void run() {
		try {
      // Check if workingDirectory exists
      if (!workingDirectory.exists()) {
        Logger.Error("Working directory does not exist: [" + workingDirectory.getAbsolutePath() + "]. Not launching command.");
        return;
      }

      // Exec the command, get output and error streams
      Process process = Runtime.getRuntime().exec(cmdArray, null, workingDirectory);
      final SequenceInputStream in = new SequenceInputStream(process.getInputStream(), process.getErrorStream());
      final BufferedReader reader = new BufferedReader(new InputStreamReader(in));
      
      // Iterate over the output
      while ((output = reader.readLine()) != null) {
        System.out.println(output);
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
