package com.openrsc.android.updater;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.openrsc.client.R;
import com.openrsc.client.android.GameActivity;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import orsc.osConfig;
import orsc.util.GenUtil;

public class CacheUpdater extends Activity {

    private TextProgressBar progressBar;

    private TextView tv1;
    private boolean completed = false;

    List<String> excludedFiles = new ArrayList<>();
    List<String> refuseUpdate = new ArrayList<>();

	final AtomicReference<String> realPath = new AtomicReference<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	// This is so we can do network stuff on the main thread
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

		StrictMode.setThreadPolicy(policy);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.updater);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setTextSize(18);
        progressBar.setIndeterminate(false);
        progressBar.setMax(100);

        Button launchButton = findViewById(R.id.launch_client);
        launchButton.setVisibility(View.GONE);
        launchButton.setOnClickListener(v -> {
            if (completed) {
                Intent mainIntent = new Intent(CacheUpdater.this, GameActivity.class);
                mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(mainIntent);
                finish();
            }
        });
        //Start Game
        tv1 = findViewById(R.id.textView1);
        setStatus("Checking for game-cache updates...");
        Handler handler = new Handler();
        handler.post(() -> new UpdateTask().execute());
    }

    public void setStatus(String s) {
        tv1.setText(s);
    }

    @SuppressLint("StaticFieldLeak")
    class UpdateTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... aurl) {
            excludedFiles.add(osConfig.MD5_TABLENAME);
            refuseUpdate.add("config.txt");

            File cacheHome = getFilesDir();
            if (!cacheHome.exists())
                cacheHome.mkdirs();

            File md5Table = new File(cacheHome, osConfig.MD5_TABLENAME);

            if (md5Table.exists()) {
                md5Table.delete();
            }

            downloadFile(md5Table, getFilesDir().toString() + File.separator);

            md5 localCache = new md5(md5Table.getParentFile(), "");
            md5 remoteCache = new md5(md5Table, "");

            for (md5.Entry entry : remoteCache.entries) {
                if (excludedFiles.contains(entry.getRef().getName()))
                    continue;

                File entryFile = new File(cacheHome, entry.getRef().toString());
                entryFile.getParentFile().mkdirs();

                String localSum = localCache.getRefSum(entryFile);
                if (localSum != null) {
                    if (refuseUpdate.contains(entry.getRef().getName()) ||
                            localSum.equalsIgnoreCase(entry.getSum())) {
                        continue;
                    }
                }

                downloadFile(entryFile, getFilesDir().toString() + File.separator);
            }

            publishProgress("Updating completed...");
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            completed = true;
            showGameSelectionDialog();
        }

        private boolean isIp(final String address) {
			Pattern pattern = Pattern.compile("^(?:[0-9]{1,3}\\.){3}[0-9]{1,3}$");
			Matcher matcher = pattern.matcher(address);
			return matcher.matches();
		}

        private void selectServer(final String serverUrl, final String serverPort) {
			String ip;
			if (isIp(serverUrl)) {
				ip = serverUrl;
			} else { // Attempt to get the IP address of the hostname.
				try {
					InetAddress address = InetAddress.getByName(new URL(serverUrl).getHost());
					ip = address.getHostAddress();
					System.out.println(ip);

				} catch (MalformedURLException | UnknownHostException ex) {
					System.out.println("Error getting IP address from URL");
					ex.printStackTrace();
					return;
				}
			}

			FileOutputStream outputStream;
			try {
				outputStream = new FileOutputStream(realPath.get() + "ip.txt");
				OutputStreamWriter outputWriter = new OutputStreamWriter(outputStream);
				outputWriter.write(ip);
				outputWriter.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				outputStream = new FileOutputStream(realPath.get() + "port.txt");
				OutputStreamWriter outputWriter = new OutputStreamWriter(outputStream);
				outputWriter.write(serverPort);
				outputWriter.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			Intent mainIntent = new Intent(CacheUpdater.this, GameActivity.class);
			mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(mainIntent);
			finish();
		}

        void showGameSelectionDialog() {
            String desiredPath = "/storage/emulated/0/Android/data/user/0/com.openrsc.client/files" + File.separator;

            FileOutputStream fos = null;

            try {
                fos = new FileOutputStream(desiredPath + "test.txt");
                realPath.set(desiredPath);
            } catch (Exception e) {
                try {
                    fos = new FileOutputStream(getFilesDir().getPath() + File.separator + "test.txt");
                    realPath.set(getFilesDir().getPath() + File.separator);
                } catch (Exception e2) {
                    try {
                        fos = new FileOutputStream(Objects.requireNonNull(getExternalFilesDir(null)).getPath() + File.separator + "test.txt");
                        realPath.set(Objects.requireNonNull(getExternalFilesDir(null)).getPath() + File.separator);
                    } catch (Exception ignored) {
                    }
                }
            } finally {
                GenUtil.close(fos);
            }

            System.out.println(" ");
            System.out.println(" ");
            System.out.println("Please select which game you wish to play.");
            System.out.println(" ");
            System.out.println("43594 openrsc / 43595 cabbage / 43235 uranium / 43599 coleslaw");
            System.out.println(" ");

            // setup the alert builder
            AlertDialog.Builder builder = new AlertDialog.Builder(CacheUpdater.this);
            builder.setTitle("Game Selection");

            // add a list
            //String[] games = {"RSC Cabbage", "Open RSC", "RSC Preservation (alpha)", "Open PK (beta)", "Dev Testing", "Local Instance"};
            String[] games = {"Open RSC", "RSC Cabbage", "RSC Uranium", "RSC Coleslaw", "Local Instance"};
            builder.setItems(games, (dialog, which) -> {
                switch (which) {
					case 0: // Open RSC
						selectServer("https://game.openrsc.com", "43602");
						break;
                    case 1: // RSC Cabbage
						selectServer("https://game.openrsc.com", "43595");
						break;
					case 2: // RSC Uranium
						selectServer("https://game.openrsc.com", "43601");
						break;
					case 3: // RSC Coleslaw
						selectServer("https://game.openrsc.com", "43599");
						break;
                    case 4: // Manual
                        LinearLayout layout = new LinearLayout(CacheUpdater.this);

                        // TextView to enter ip
                        final EditText ipBox = new EditText(CacheUpdater.this);
                        ipBox.setHint("192.168.1.100");
                        layout.addView(ipBox);

                        // TextView to enter port
                        final EditText portBox = new EditText(CacheUpdater.this);
                        portBox.setHint("43594");
                        layout.addView(portBox);

                        new AlertDialog.Builder(CacheUpdater.this)
                                .setTitle("Local Instance")
                                .setMessage("Enter details for local instance")
                                .setView(layout)
                                .setPositiveButton("Enter", (dialog1, whichButton) -> {
                                    String ip_local = "192.168.1.100";
                                    String port_local = "43594";

                                    if (!ipBox.getText().toString().trim().equals("")) {
                                        ip_local = ipBox.getText().toString().trim();
                                    }
                                    if (!portBox.getText().toString().trim().equals("")) {
                                        port_local = portBox.getText().toString().trim();
                                    }

                                    selectServer(ip_local, port_local);
                                })
                                .show();
                }
            });

            AlertDialog dialog = builder.create();

            dialog.show();
        }

        @Override
        protected void onProgressUpdate(String... values) {
            if (values.length == 1) {
                tv1.setText(values[0]);
            } else if (values.length == 2) {
                progressBar.setText(values[0] + " - " + Integer.parseInt(values[1]) + "%");
                progressBar.setProgress(Integer.parseInt(values[1]));
            }
        }

        private void downloadFile(File file, String prefix) {
            try {
                String fileURL = file.toString().replace(prefix, osConfig.CACHE_URL).replace(File.separator, "/");
                String description = getDescription(file);
                publishProgress("Downloading " + description, String.valueOf(0));
                HttpURLConnection connection = (HttpURLConnection) new URL(fileURL).openConnection();
                try (BufferedInputStream in = new BufferedInputStream(connection.getInputStream());
                     FileOutputStream fileOutputStream = new FileOutputStream(file)) {
                    int filesize = connection.getContentLength();
                    byte[] dataBuffer = new byte[1024];
                    int bytesRead;
                    int totalRead = 0;
                    while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                        totalRead += bytesRead;
                        fileOutputStream.write(dataBuffer, 0, bytesRead);
                        publishProgress("Downloading " + description, "" + (100 * totalRead / filesize));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                connection.disconnect();
            } catch (Exception a) {
                a.printStackTrace();
            }
        }

        public String getMD5Checksum(String filename) throws Exception {
            InputStream fis = openFileInput(filename);

            byte[] buffer = new byte[1024];
            MessageDigest complete = MessageDigest.getInstance("MD5");
            int numRead;

            do {
                numRead = fis.read(buffer);
                if (numRead > 0) {
                    complete.update(buffer, 0, numRead);
                }
            } while (numRead != -1);

            fis.close();

            byte[] b = complete.digest();
            StringBuilder result = new StringBuilder();

            for (byte aB : b) {
                result.append(Integer.toString((aB & 0xff) + 0x100, 16).substring(1));
            }
            return result.toString();
        }

        boolean verifyFile(String filename, String checksum) {
            return true;

        }
    }

    private final String[] nicename = {"Checksum", "3D models", "Application Icon", "Graphics", "Landscape", "library"};
    private final String[] normalName = {"MD5CHECKSUM", "models.orsc", "RuneScape.png", "Sprites.orsc", "Landscape.orsc", "library.orsc"};

    public String getNiceName(String s) {
        for (int i = 0; i < normalName.length; i++) {
            if (normalName[i].equalsIgnoreCase(s)) {
                return nicename[i];
            }
        }
        return "File";
    }

    private String getDescription(File ref) {
        int index = ref.getName().lastIndexOf('.');
        if (index == -1)
            return "General";
        else {
            String extension = ref.getName().substring(index + 1);
            if (extension.equalsIgnoreCase("ospr"))
                return "Graphics";
            else if (extension.equalsIgnoreCase("wav"))
                return "Audio";
            else if (extension.equalsIgnoreCase("orsc"))
                return "Graphics";
            else if (extension.equalsIgnoreCase("jar"))
                return "Executable";
            else
                return "General";
        }
    }
}
