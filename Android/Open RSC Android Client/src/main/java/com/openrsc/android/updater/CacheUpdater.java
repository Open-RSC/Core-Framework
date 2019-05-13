package com.openrsc.android.updater;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.openrsc.client.R;
import com.openrsc.client.android.GameActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.util.Map.Entry;
import java.util.Properties;

import orsc.Config;

public class CacheUpdater extends Activity {

    private TextProgressBar progressBar;

    private TextView tv1;
    private boolean completed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

        private Properties oldChecksum;
        private Properties newChecksum;

        @Override
        protected String doInBackground(String... aurl) {
            oldChecksum = new Properties();
            newChecksum = new Properties();
            try {
                downloadFile("MD5CHECKSUM");
                File f = new File(getFilesDir().getPath() + File.separator + "MD5CHECKSUM.old");
                if (!f.exists()) {
                    f.createNewFile();
                }
                FileInputStream fi = openFileInput("MD5CHECKSUM.old");
                FileInputStream f2 = openFileInput("MD5CHECKSUM");
                oldChecksum.load(fi);
                newChecksum.load(f2);
                fi.close();
                f2.close();
            } catch (Exception e) {
                System.out.println(" ");
                System.out.println(" ");
                System.out.println("Unable to load checksums.");
                System.out.println(" ");
                System.out.println(" ");
                System.exit(1);
            }
            try {
                /* Update cache file */
                for (Entry<Object, Object> e : oldChecksum.entrySet()) {
                    for (Entry<Object, Object> e1 : newChecksum.entrySet()) {
                        if (e1.getKey().equals(e.getKey()) && !e1.getValue().equals(e.getValue())) {
                            deleteFile((String) e.getKey());
                            downloadFile((String) e.getKey());
                            publishProgress("Updating " + e.getKey() + "...\n");
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println(" ");
                System.out.println(" ");
                System.out.println("Unable to update cache files.");
                System.out.println(" ");
                System.out.println(" ");
                System.exit(1);
            }

            try {
                publishProgress("Downloading game cache files");
                /* Download new added files */
                for (Object o : newChecksum.keySet()) {
                    if (!oldChecksum.keySet().contains(o)) {
                        downloadFile((String) o);
                    }
                }
            } catch (Exception e) {
                System.out.println(" ");
                System.out.println(" ");
                System.out.println("Unable to download newly added files.");
                System.out.println(" ");
                System.out.println(" ");
                System.exit(1);
            }

            try {
                for (Entry<Object, Object> entrySet : newChecksum.entrySet()) {
                    String filename = (String) entrySet.getKey();
                    String hash = (String) entrySet.getValue();
                    boolean verified = false;

                    while (!verified) {
                        verified = verifyFile(filename, hash);
                        if (!verified) {
                            publishProgress("Re-downloading " + filename);
                            deleteFile(filename);
                            downloadFile(filename);
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println(" ");
                System.out.println(" ");
                System.out.println("Unable to verify data files");
                System.out.println(" ");
                System.out.println(" ");
                System.exit(1);
            }

            File old = new File(getFilesDir(), "MD5CHECKSUM.old");
            File new1 = new File(getFilesDir(), "MD5CHECKSUM");
            old.delete();
            new1.renameTo(old);
            publishProgress("Updating completed...");
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            completed = true;
            showGameSelectionDialog();
        }

        void showGameSelectionDialog() {

            System.out.println(" ");
            System.out.println(" ");
            System.out.println("Please select which game you wish to play.");
            System.out.println(" ");
            System.out.println(" ");

            // 43594 openrsc / 43595 cabbage / 43596 preservation / 43597 openpk / 43598 wk / 43599 dev

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CacheUpdater.this);
            alertDialogBuilder.setTitle("Game Selection");
            alertDialogBuilder
                    .setMessage("Please select which game you wish to play.")
                    .setCancelable(false).setPositiveButton("Open RSC", (dialog, id) -> {
                String ip = "game.openrsc.com";
                String port = "43594";
                FileOutputStream fileout;
                try {
                    fileout = new FileOutputStream("/data/user/0/com.openrsc.client/files" + File.separator + "ip.txt");
                    OutputStreamWriter outputWriter = new OutputStreamWriter(fileout);
                    outputWriter.write(ip);
                    outputWriter.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    fileout = new FileOutputStream("/data/user/0/com.openrsc.client/files" + File.separator + "port.txt");
                    OutputStreamWriter outputWriter = new OutputStreamWriter(fileout);
                    outputWriter.write(port);
                    outputWriter.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Intent mainIntent = new Intent(CacheUpdater.this, GameActivity.class);
                mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(mainIntent);
                finish();
            }).setNeutralButton("RSC Cabbage", (dialog, id) -> {
                String ip = "cabbage.openrsc.com";
                String port = "43595";
                FileOutputStream fileout;
                try {
                    fileout = new FileOutputStream("/data/user/0/com.openrsc.client/files" + File.separator + "ip.txt");
                    OutputStreamWriter outputWriter = new OutputStreamWriter(fileout);
                    outputWriter.write(ip);
                    outputWriter.close();
                } catch (Exception ignored) {
                }
                try {
                    fileout = new FileOutputStream("/data/user/0/com.openrsc.client/files" + File.separator + "port.txt");
                    OutputStreamWriter outputWriter = new OutputStreamWriter(fileout);
                    outputWriter.write(port);
                    outputWriter.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Intent mainIntent = new Intent(CacheUpdater.this, GameActivity.class);
                mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(mainIntent);
                finish();
           /* }).setNegativeButton("RSC Preservation", (dialog, id) -> {
                String ip = "rscp.openrsc.com";
                String port = "43596";
                FileOutputStream fileout;
                try {
                    fileout = new FileOutputStream("/data/user/0/com.openrsc.client/files" + File.separator + "ip.txt");
                    OutputStreamWriter outputWriter = new OutputStreamWriter(fileout);
                    outputWriter.write(ip);
                    outputWriter.close();
                } catch (Exception ignored) {
                }
                try {
                    fileout = new FileOutputStream("/data/user/0/com.openrsc.client/files" + File.separator + "port.txt");
                    OutputStreamWriter outputWriter = new OutputStreamWriter(fileout);
                    outputWriter.write(port);
                    outputWriter.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Intent mainIntent = new Intent(CacheUpdater.this, GameActivity.class);
                mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(mainIntent);
                finish();*/
            }).setNegativeButton("Dev Test", (dialog, id) -> {
                String ip = "dev.openrsc.com";
                String port = "43599";
                FileOutputStream fileout;
                try {
                    fileout = new FileOutputStream("/data/user/0/com.openrsc.client/files" + File.separator + "ip.txt");
                    OutputStreamWriter outputWriter = new OutputStreamWriter(fileout);
                    outputWriter.write(ip);
                    outputWriter.close();
                } catch (Exception ignored) {
                }
                try {
                    fileout = new FileOutputStream("/data/user/0/com.openrsc.client/files" + File.separator + "port.txt");
                    OutputStreamWriter outputWriter = new OutputStreamWriter(fileout);
                    outputWriter.write(port);
                    outputWriter.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Intent mainIntent = new Intent(CacheUpdater.this, GameActivity.class);
                mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(mainIntent);
                finish();
            });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
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

        void downloadFile(String filename) {
            Log.d("Updater", "Downloading file: " + filename + " - " + getNiceName(filename));
            HttpURLConnection connection = null;
            try {
                connection = (HttpURLConnection) new URL(Config.CACHE_URL + filename).openConnection();
                connection.connect();
                publishProgress("Downloading " + getNiceName(filename));
                int fileLength = connection.getContentLength();
                try (FileOutputStream fos = openFileOutput(filename, Context.MODE_PRIVATE)) {
                    InputStream in = connection.getInputStream();
                    byte[] buffer = new byte[1024];
                    int total = 0;
                    int len = 0;
                    while ((len = in.read(buffer)) > 0) {
                        total += len;
                        if (fileLength > 0) {
                            int progress = (total * 100) / fileLength;
                            publishProgress("Downloading " + filename, "" + progress);
                        }
                        fos.write(buffer, 0, len);
                    }
                    fos.flush();
                }
            } catch (Exception e) {
                e.printStackTrace();
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

    private final String nicename[] = {"Checksum", "3D models", "Application Icon", "Graphics", "Landscape", "library"};
    private final String normalName[] = {"MD5CHECKSUM", "models.orsc", "RuneScape.png", "Sprites.orsc", "Landscape.orsc", "library.orsc"};

    public String getNiceName(String s) {
        for (int i = 0; i < normalName.length; i++) {
            if (normalName[i].equalsIgnoreCase(s)) {
                return nicename[i];
            }
        }
        return "File";
    }

}
