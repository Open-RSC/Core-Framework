package orsc.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class Utils {
	public static Context context;

	public static void openWebpage(final String url) {
		try {
			Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
			context.startActivity(browserIntent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
