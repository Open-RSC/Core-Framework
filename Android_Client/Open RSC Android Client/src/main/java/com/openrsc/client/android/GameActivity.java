package com.openrsc.client.android;

import static android.graphics.Bitmap.Config.ARGB_8888;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.BatteryManager;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.openrsc.android.render.InputImpl;
import com.openrsc.android.render.RSCBitmapSurfaceView;
import com.openrsc.client.R;
import com.openrsc.client.model.Sprite;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Objects;

import orsc.Config;
import orsc.PacketHandler;
import orsc.mudclient;
import orsc.multiclient.ClientPort;
import orsc.osConfig;

public class GameActivity extends Activity implements ClientPort {

    private InputImpl inputImpl;
    private mudclient mudclient;
    private RSCBitmapSurfaceView gameView;

	private boolean loadedReceivers = false;
	private int batteryLevel;
	private int batteryScale;
	private boolean batteryCharging;
	protected volatile boolean wifiAvailable = false;

	protected volatile boolean cellularAvailable = false;
	private HashSet<Network> cellularNetworks = new HashSet<>();
	private HashSet<Network> wifiNetworks = new HashSet<>();
	private boolean checkedNetworks = true;

    private boolean hadSideMenu;

	final BroadcastReceiver batteryReceiver = new BatteryReceiver();

	final ConnectivityManager.NetworkCallback networkManager = new NetworkManager();

	class BatteryReceiver extends BroadcastReceiver {
		BatteryReceiver() {
		}

		public void onReceive(Context context, Intent intent) {
			batteryLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
			batteryScale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
			int intExtra = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
			batteryCharging = intExtra == BatteryManager.BATTERY_STATUS_CHARGING || intExtra == BatteryManager.BATTERY_STATUS_FULL;
		}
	}

	class NetworkManager extends ConnectivityManager.NetworkCallback {
		NetworkManager() {
		}

		public void onCapabilitiesChanged(Network network, NetworkCapabilities networkCapabilities) {
			networkChange(network, networkCapabilities);
		}

		public void onLost(Network network) {
			networkChange(network, (NetworkCapabilities) null);
		}
	}

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gameView = new RSCBitmapSurfaceView(this) {

			@Override
            public void setTitle(String title) {

            }

            @Override
            public void setIconImage(String serverName) {

            }
        };
        setMudclient(new mudclient(this));
        setContentView(gameView);

        mudclient.packetHandler = new PacketHandler(mudclient);

        if (mudclient.threadState >= 0) mudclient.threadState = 0;

        osConfig.F_ANDROID_BUILD = true;

        mudclient.startMainThread();

        setInputImpl(new InputImpl(mudclient, gameView));

		//Utils.context = getApplicationContext();

		// Hide the bars and stuff
		updateHideUi();
		setReceivers();
    }

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unsetReceivers();
	}

	public void networkChange(Network network, NetworkCapabilities networkCapabilities) {
		boolean canDataConnect = false;
		if (this.checkedNetworks) {
			this.checkedNetworks = false;
			this.cellularNetworks.clear();
			this.wifiNetworks.clear();
		}
		if (networkCapabilities != null) {
			(networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ? this.cellularNetworks : this.wifiNetworks).add(network);
		} else {
			this.cellularNetworks.remove(network);
			this.wifiNetworks.remove(network);
		}
		this.wifiAvailable = !this.wifiNetworks.isEmpty();
		if (!this.wifiAvailable && !this.cellularNetworks.isEmpty()) {
			canDataConnect = true;
		}
		this.cellularAvailable = canDataConnect;
	}

	private void checkNetwork() {
		this.cellularNetworks.clear();
		this.wifiNetworks.clear();
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		Network activeNetwork = connectivityManager.getActiveNetwork();
		NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork);
		if (networkCapabilities != null && networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) {
			networkChange(activeNetwork, networkCapabilities);
		}
		this.checkedNetworks = true;
	}

	private void setReceivers() {
		if (!this.loadedReceivers) {
			this.loadedReceivers = true;
			checkNetwork();
			((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE)).registerNetworkCallback(new NetworkRequest.Builder().addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET).build(), networkManager);
			registerReceiver(batteryReceiver, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
		}
	}

	private void unsetReceivers() {
		this.loadedReceivers = false;
		((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE)).unregisterNetworkCallback(networkManager);
		unregisterReceiver(batteryReceiver);
	}

    @Override
	public void onResume() {
    	super.onResume();
    	updateHideUi();
	}

	private void updateHideUi() {
		final View decorView = getWindow().getDecorView();
		decorView.setOnSystemUiVisibilityChangeListener (new View.OnSystemUiVisibilityChangeListener() {
			@Override
			public void onSystemUiVisibilityChange(int visibility) {
				if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
					decorView.setSystemUiVisibility(
						View.SYSTEM_UI_FLAG_LAYOUT_STABLE
							| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
							| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
							| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
							| View.SYSTEM_UI_FLAG_FULLSCREEN
							| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
				}
			}
		});
	}

    @Override
    public boolean drawLoading(int i) {
        if (gameView == null) return false;
        return gameView.drawLoading(i);
    }

    @Override
    public void showLoadingProgress(int percentage, String status) {
        if (gameView != null) {
        	gameView.showLoadingProgress(percentage, status);
        	gameView.postInvalidate();
        }
    }

    @Override
    public void initListeners() {
        if (gameView != null) gameView.initListeners();
    }

    @Override
    public void crashed() {
        if (gameView != null) gameView.crashed();
    }

    @Override
    public void drawLoadingError() {
        if (gameView != null) gameView.drawLoadingError();
    }

    @Override
    public void drawOutOfMemoryError() {
        if (gameView != null) gameView.drawOutOfMemoryError();
    }

    @Override
    public boolean isDisplayable() {
        if (gameView != null) return gameView.isDisplayable();
        return false;
    }

    @Override
    public void drawTextBox(String line2, byte var2, String line1) {
        if (gameView != null) gameView.drawTextBox(line2, var2, line1);
    }

    @Override
    public void initGraphics() {
        if (gameView != null) gameView.initGraphics();
    }

    @Override
    public void draw() {
        if (gameView != null) gameView.draw();
    }

    @Override
    public void close() {
        if (gameView != null) gameView.close();
    }

    @Override
    public String getCacheLocation() {
        if (gameView != null) return gameView.getCacheLocation();
        return null;
    }

    @Override
    public void resized() {
        if (gameView != null) gameView.resized();
    }

    @Override
    public Sprite getSpriteFromByteArray(ByteArrayInputStream byteArrayInputStream) {
        return gameView.getSpriteFromByteArray(byteArrayInputStream);
    }

    @Override
    public void playSound(byte[] soundData, int offset, int dataLength) {
        if (gameView != null) gameView.playSound(soundData, offset, dataLength);
    }

    @Override
    public void stopSoundPlayer() {
        if (gameView != null) gameView.stopSoundPlayer();
    }

    public mudclient getMudclient() {
        return mudclient;
    }

    public void setMudclient(mudclient mudclient) {
        this.mudclient = mudclient;
    }

    public InputImpl getInputImpl() {
        return inputImpl;
    }

    public void setInputImpl(InputImpl inputImpl) {
        this.inputImpl = inputImpl;
    }

    public void drawKeyboard() {
        InputMethodManager imm = (InputMethodManager) this
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        Objects.requireNonNull(imm).toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        if (imm.isAcceptingText()) osConfig.F_SHOWING_KEYBOARD = true;
        if (Config.S_SIDE_MENU_TOGGLE) {
        	hadSideMenu = mudclient.getOptionSideMenu();
			mudclient.setOptionSideMenu(false);
		}
    }

    public void closeKeyboard() {
        ((InputMethodManager) Objects.requireNonNull(getSystemService(Activity.INPUT_METHOD_SERVICE)))
                .toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
        osConfig.F_SHOWING_KEYBOARD = false;
		if (Config.S_SIDE_MENU_TOGGLE) {
			mudclient.setOptionSideMenu(hadSideMenu);
		}
    }

	private double getBatteryPercentage() {
		if (batteryLevel == -1 || batteryScale == -1) {
			return 50.0;
		}

		return ((double)batteryLevel / (double)batteryScale) * 100.0;
	}

	public boolean getBatteryCharging() {
		return batteryCharging;
	}

	public int getBatteryPercent() {
		return (int)getBatteryPercentage();
	}

	public Sprite getBattery(int level) {
		if (gameView != null) {
			if (batteryCharging) {
				return gameView.getBattery(8);
			} else {
				double batteryPercent = getBatteryPercentage();
				if (batteryPercent > 92.5) return gameView.getBattery(7);
				else if (batteryPercent > 77.5) return gameView.getBattery(6);
				else if (batteryPercent > 62.5) return gameView.getBattery(5);
				else if (batteryPercent > 47.5) return gameView.getBattery(4);
				else if (batteryPercent > 32.5) return gameView.getBattery(3);
				else if (batteryPercent > 17.5) return gameView.getBattery(2);
				else if (batteryPercent > 10) return gameView.getBattery(1);
				else return gameView.getBattery(0);
			}
		}
		return null;
	}

	public Sprite getConnectivity(int level) {
		if (gameView != null) {
			if (!wifiAvailable && !cellularAvailable) return gameView.getConnectivity(0);
			else if (wifiAvailable) return gameView.getConnectivity(2);
			else return gameView.getConnectivity(1);
		}
		return null;
	}

	public String getConnectivityText() {
		if (!wifiAvailable && !cellularAvailable) return "NONE";
		else if (wifiAvailable) return "WIFI";
		else return "CELL";
	}

    @Override
    public void setTitle(String title) {

    }

    @Override
    public void setIconImage(String serverName) {

    }
}
