package orsc.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;

public class Utils {
	public static Context context;

	public static void openWebpage(final String url) {
		final Handler handlerTimer = new Handler(Looper.getMainLooper());
		handlerTimer.postDelayed(new Runnable() {
			public void run() {
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
				alertDialogBuilder
					.setMessage("You are opening a link to an external site. If you remain inactive in\n"
						+ "game for too long, you will be logged out.")
					.setCancelable(false).setPositiveButton("OK", (dialog, id) -> {
						try {
							Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
							context.startActivity(browserIntent);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}).setNegativeButton("Cancel", (dialog, id) -> {
					});
				AlertDialog alertDialog = alertDialogBuilder.create();
				alertDialog.show();
			}
		}, 100);

		/*try {
			Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
			context.startActivity(browserIntent);
		} catch (Exception e) {
			e.printStackTrace();
		}*/
	}
}
