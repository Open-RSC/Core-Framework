package com.openrsc.android.updater;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.widget.TextView;
import android.widget.Toast;

import com.openrsc.client.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;

import orsc.osConfig;

public class ApplicationUpdater extends Activity {

    private TextProgressBar progressBar;
    private TextView tv1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.applicationupdater);

        progressBar = findViewById(R.id.progressBar1);
        progressBar.setTextSize(18);
        progressBar.setIndeterminate(false);
        tv1 = findViewById(R.id.textView1);
        System.out.println(" ");
        System.out.println(" ");
        System.out.println("Connecting to " + osConfig.DL_URL + " with installed Android client version " + osConfig.ANDROID_CLIENT_VERSION + ". Please wait...");
        System.out.println(" ");
        System.out.println(" ");
        new Handler().postDelayed(() -> {
			try {
				new CheckVersionTask().execute().get();
			} catch (Exception e) {
				e.printStackTrace();
				Toast.makeText(ApplicationUpdater.this, "Unable to check for updates", Toast.LENGTH_LONG).show();
			}
		}, 1000);

    }

    public int getVersion() {
        PackageManager manager = getApplicationContext().getPackageManager();
        PackageInfo info;
        try {
            info = manager.getPackageInfo(getApplicationContext().getPackageName(), 0);
            return osConfig.ANDROID_CLIENT_VERSION;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this, "Unable to check application version", Toast.LENGTH_LONG).show();
        }
        return -1;
    }

    @SuppressLint({"SetTextI18n", "SetWorldReadable"})
	public void showUpdateDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ApplicationUpdater.this);
        alertDialogBuilder.setTitle("New version available!");
        alertDialogBuilder
                .setMessage("There is a new app update available,"
                        + " please install the newest version from the website.")
                .setCancelable(false).setPositiveButton("Go to website", (dialog, id) -> {
					try {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://runescapeclassic.dev/download"));
                        startActivity(browserIntent);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}).setNegativeButton("No thanks", (dialog, id) -> {
					Intent mainIntent = new Intent(ApplicationUpdater.this, CacheUpdater.class);
					mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(mainIntent);
					finish();
				});
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @SuppressLint("StaticFieldLeak")
	class CheckVersionTask extends AsyncTask<String, String, String> {

        private boolean shouldUpdate = false;

        @Override
        protected String doInBackground(String... aurl) {
            System.out.println(" ");
            System.out.println(" ");
            System.out.println("Performing background task for app version check.");
            System.out.println(" ");
            System.out.println(" ");
            URL updatePage;
            try {
                updatePage = new URL(osConfig.ANDROID_DOWNLOAD_PATH + "android_version.txt");
				HttpURLConnection conn = (HttpURLConnection)updatePage.openConnection();
				conn.setConnectTimeout(1000);

                System.out.println(" ");
                System.out.println(" ");
                System.out.println("Checking external Android client version at: " + updatePage);
                System.out.println(" ");
                System.out.println(" ");

                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine = in.readLine();
				in.close();

                System.out.println(" ");
                System.out.println(" ");
                System.out.println("Received external Android client version: " + inputLine);
                System.out.println(" ");
                System.out.println(" ");

                if (Integer.parseInt(inputLine) > getVersion()) {
                    shouldUpdate = true;
                    publishProgress("New app version available!");
                }
            } catch (Exception e) {
                e.printStackTrace();
                publishProgress("Unable to check for app update.");
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            tv1.setText(values[0]);
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result) {
            if (shouldUpdate) {
                showUpdateDialog();
            } else {
                Intent mainIntent = new Intent(ApplicationUpdater.this, CacheUpdater.class);
                startActivity(mainIntent);
                finish();
            }
            super.onPostExecute(result);
        }
    }

    @SuppressLint("StaticFieldLeak")
	class DownloadApplication extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... aurl) {

            HttpURLConnection connection;
            try {
                connection = (HttpURLConnection) new URL(osConfig.ANDROID_DOWNLOAD_PATH + "openrsc.apk").openConnection();
                connection.connect();

                int fileLength = connection.getContentLength();
				try (FileOutputStream fos = openFileOutput("openrsc.apk", Context.MODE_PRIVATE)) {
					InputStream in = connection.getInputStream();
					byte[] buffer = new byte[4096];
					long total = 0;
					int len;
					while ((len = in.read(buffer)) > 0) {
						total += len;
						fos.write(buffer, 0, len);
						if (fileLength > 0)
							publishProgress("Downloading update...", "" + (int) ((total * 100) / fileLength));
					}
					fos.flush();
				}
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            progressBar.setText(values[0] + " - " + Integer.parseInt(values[1]) + "%");
            progressBar.setProgress(Integer.parseInt(values[1]));
            progressBar.postInvalidate();
            super.onProgressUpdate(values);
        }
    }
}
