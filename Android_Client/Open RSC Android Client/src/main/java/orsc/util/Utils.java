package orsc.util;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

public class Utils {
	public static void openWebpage(final String url) {
		try {
			Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
			startActivity(browserIntent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
