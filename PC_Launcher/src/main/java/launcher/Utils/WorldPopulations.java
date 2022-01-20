package launcher.Utils;

import launcher.Fancy.MainWindow;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.Arrays;

public class WorldPopulations implements Runnable {
	final static int WORLDS_SUPPORTED = 6; // increment when Kale or other servers are implemented. do not decrement.

	public final static int PRESERVATION = 0;
	public final static int CABBAGE = 1;
	public final static int URANIUM = 2;
	public final static int COLESLAW = 3;
	public final static int TWOTHOUSANDONESCAPE = 4;
	public final static int OPENPK = 5;
	public final static int KALE = 6;

	private static long lastPopCheck = 0;

	public static String[] worldOnlineTexts = new String[WORLDS_SUPPORTED];

	public static void updateWorldPopulations() {
		Thread t = new Thread(new WorldPopulations());
		t.start();
	}

	@Override
	public void run() {
		if (System.currentTimeMillis() < lastPopCheck + 1000) {
			MainWindow.get().updateWorldTotalTexts();
			return;
		}

		String[] splitWorldTotals = null;
		lastPopCheck = System.currentTimeMillis();

		URL url;
		URLConnection con;
		InputStream is = null;
		BufferedReader br;

		try {
			url = new URL("https://rsc.vet/onlinelookup");
			con = url.openConnection();
			con.setConnectTimeout(3000);
			con.setReadTimeout(3000);

			con.addRequestProperty("User-Agent", Utils.generateUserAgent());

			is = con.getInputStream();
			br = new BufferedReader(new InputStreamReader(is));

		} catch (UnknownHostException uhe) {
			if (null == WorldPopulations.worldOnlineTexts[WorldPopulations.PRESERVATION] ||
				null == WorldPopulations.worldOnlineTexts[WorldPopulations.URANIUM]) {
				Arrays.fill(WorldPopulations.worldOnlineTexts, "You're offline");
			}

			MainWindow.get().updateWorldTotalTexts();
			return;
		} catch (SocketTimeoutException ste) {
			if (null == WorldPopulations.worldOnlineTexts[WorldPopulations.PRESERVATION] ||
				null == WorldPopulations.worldOnlineTexts[WorldPopulations.URANIUM]) {
				Arrays.fill(WorldPopulations.worldOnlineTexts, "Socket timeout");
			}

			MainWindow.get().updateWorldTotalTexts();
			return;
		} catch (IOException ioe) {
			try {
				if (is != null) is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			if (ioe.toString().contains("Server returned HTTP response code: 521")) {
				Arrays.fill(WorldPopulations.worldOnlineTexts, "Webserver offline");
			} else {
				Arrays.fill(WorldPopulations.worldOnlineTexts, "Webserver offline?");
			}

			MainWindow.get().updateWorldTotalTexts();
			return;
		}

		String line = null;

		try {
			while ((line = br.readLine()) != null) {
				splitWorldTotals = line.split(",");
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

		try {
			br.close();
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}


		if (null == splitWorldTotals) {
			for (int i = 0; i < WORLDS_SUPPORTED; i++) {
				worldOnlineTexts[i] = "";
			}
		} else {
			for (int i = 0; i < WORLDS_SUPPORTED && i < splitWorldTotals.length; i++) {
				boolean isNumber;
				try {
					Integer.parseInt(splitWorldTotals[i]);
					isNumber = true;
				} catch (NumberFormatException nfe) {
					isNumber = false;
				}
				if (isNumber)
					splitWorldTotals[i] += " online";

				worldOnlineTexts[i] = splitWorldTotals[i];
			}
		}

		MainWindow.get().updateWorldTotalTexts();
	}
}
