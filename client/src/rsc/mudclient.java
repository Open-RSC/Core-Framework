package rsc;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import com.openrsc.client.data.DataFileDecrypter;
import com.openrsc.client.data.DataOperations;
import com.openrsc.client.entityhandling.EntityHandler;
import com.openrsc.client.entityhandling.defs.ItemDef;
import com.openrsc.client.entityhandling.defs.NPCDef;
import com.openrsc.client.entityhandling.defs.SpellDef;
import com.openrsc.client.entityhandling.defs.extras.AnimationDef;
import com.openrsc.client.model.Sprite;
import com.openrsc.interfaces.NComponent;
import com.openrsc.interfaces.misc.AuctionHouse;
import com.openrsc.interfaces.misc.BankInterface;
import com.openrsc.interfaces.misc.BankPinInterface;
import com.openrsc.interfaces.misc.FishingTrawlerInterface;
import com.openrsc.interfaces.misc.IronManInterface;
import com.openrsc.interfaces.misc.OnlineListInterface;
import com.openrsc.interfaces.misc.ProgressBarInterface;
import com.openrsc.interfaces.misc.QuestGuideInterface;
import com.openrsc.interfaces.misc.SkillGuideInterface;
import com.openrsc.interfaces.misc.ExperienceConfigInterface;
import com.openrsc.interfaces.misc.DoSkillInterface;
import com.openrsc.interfaces.misc.LostOnDeathInterface;
import com.openrsc.interfaces.misc.TerritorySignupInterface;
import com.openrsc.interfaces.misc.clan.Clan;
import com.openrsc.interfaces.NCustomComponent;
import rsc.buffers.RSBufferUtils;
import rsc.buffers.RSBuffer_Bits;
import rsc.enumerations.GameMode;
import rsc.enumerations.InputXAction;
import rsc.enumerations.MenuItemAction;
import rsc.enumerations.MessageTab;
import rsc.enumerations.MessageType;
import rsc.enumerations.RSCharacterDirection;
import rsc.enumerations.SocialPopupMode;
import rsc.graphics.gui.InputXPrompt;
import rsc.graphics.gui.KillAnnouncer;
import rsc.graphics.gui.KillAnnouncerQueue;
import rsc.graphics.gui.Menu;
import rsc.graphics.gui.MessageHistory;
import rsc.graphics.gui.Panel;
import rsc.graphics.gui.SocialLists;
import rsc.graphics.three.CollisionFlag;
import rsc.graphics.three.RSModel;
import rsc.graphics.three.Scene;
import rsc.graphics.three.World;
import rsc.graphics.two.Fonts;
import rsc.graphics.two.MudClientGraphics;
import rsc.multiclient.ClientPort;
import rsc.net.Network_Socket;
import rsc.util.FastMath;
import rsc.util.GenUtil;
import rsc.util.StringUtil;

public final class mudclient implements Runnable {
	long lastFPSUpdate = 0;
	int currentFPS = 0;
	public static int FPS = 0;

	static byte[][] s_kb = new byte[250][];
	static int[] s_wb;

	public Thread clientBaseThread = null;
	private final int m_S = 1000;
	public int threadState = 0;
	private boolean hasGameCrashed = false;
	private int gameState = 1;
	int m_Q = 10;
	long[] m_F = new long[10];
	private int m_b = 0;

	public String chatMessageInput = "";
	public String chatMessageInputCommit = "";
	public boolean controlPressed = false;
	public int currentMouseButtonDown = 0;
	public String inputTextCurrent = "";
	public String inputTextFinal = "";
	public boolean interlace = false;
	public boolean keyLeft = false;
	public boolean keyRight = false;
	public boolean keyUp = false;
	public boolean keyDown = false;
	public boolean pageDown = false;
	public boolean pageUp = false;
	public int lastMouseAction = 0;
	public int lastMouseButtonDown = 0;
	private static ArrayList<String> messages = new ArrayList<String>();
	private static int currentChat = 0;
	private PrintWriter printWriter;

	public int selectedSkill = -1;
	private int totalAchievements = 0;
	private int sleepModifier = 20;
	public boolean m_N = false;
	String m_p = null;
	public int mouseX = 0;
	public int mouseY = 0;
	public int screenOffsetX;
	public int screenOffsetY;
	public boolean shiftPressed = false;
	public boolean adminRights;
	public boolean rendering;
	private ClientPort clientPort;
	public static KillAnnouncerQueue killQueue = new KillAnnouncerQueue();

	public mudclient(ClientPort handler) {
		this.clientPort = handler;
		Config.F_CACHE_DIR = clientPort.getCacheLocation();
		Config.initConfig();
	}

	final void zeroMF() {
		try {
			for (int i = 0; i < 10; ++i) {
				this.m_F[i] = 0L;
			}


		} catch (RuntimeException var3) {
			throw GenUtil.makeThrowable(var3, "e.PE(" + "dummy" + ')');
		}
	}

	private final void setFPS(int var1, byte var2) {
		try {

			this.sleepModifier = 1000 / var1;
			if (var2 <= 104) {
				this.screenOffsetX = 113;
			}

		} catch (RuntimeException var4) {
			throw GenUtil.makeThrowable(var4, "e.IE(" + var1 + ',' + var2 + ')');
		}
	}

	private final void errorGameCrash(String desc) {
		try {

			if (!this.hasGameCrashed) {
				this.hasGameCrashed = true;
				System.out.println("error_game_crash");
			}
		} catch (RuntimeException var6) {
			var6.printStackTrace();
			throw GenUtil.makeThrowable(var6, "e.KE(" + ("crash" != null ? "{...}" : "null") + ',' + "dummy" + ')');
		}
	}

	private final void closeProgram() {
		try {
			new Throwable().printStackTrace();

			this.threadState = -2;
			System.out.println("Closing program");
			this.closeConnection();
			GenUtil.sleepShadow(1000L);
			clientPort.close();
			// if (ClientBase.containerFrame != null) {
			// System.exit(0);
			// }

		} catch (RuntimeException var3) {
			throw GenUtil.makeThrowable(var3, "e.RE(" + 100 + ')');
		}
	}

	private final boolean loadLogo() {
		try {

			byte[] jagexArchive = unpackData("jagex.jag", "Jagex library", 0);
			if (jagexArchive == null) {
				System.out.println("jagarch is null");
			}
			Fonts.addFont(DataOperations.loadData("h11p.jf", 0, jagexArchive));
			Fonts.addFont(DataOperations.loadData("h12b.jf", 0, jagexArchive));
			Fonts.addFont(DataOperations.loadData("h12p.jf", 0, jagexArchive));
			Fonts.addFont(DataOperations.loadData("h13b.jf", 0, jagexArchive));
			Fonts.addFont(DataOperations.loadData("h14b.jf", 0, jagexArchive));
			Fonts.addFont(DataOperations.loadData("h16b.jf", 0, jagexArchive));
			Fonts.addFont(DataOperations.loadData("h20b.jf", 0, jagexArchive));
			Fonts.addFont(DataOperations.loadData("h24b.jf", 0, jagexArchive));
			return true;
		} catch (Exception var4) {
			var4.printStackTrace();
		}
		return false;
	}

	public byte[] unpackData(String filename, String fileTitle, int startPercentage) {
		int decmp_len = 0;
		int cmp_len = 0;
		byte[] data = null;
		try {
			clientPort.showLoadingProgress(startPercentage, "Loading " + fileTitle + " - 0%");
			java.io.InputStream inputstream = DataOperations.streamFromPath(clientPort.getCacheLocation() + filename);
			DataInputStream datainputstream = new DataInputStream(inputstream);
			byte[] headers = new byte[6];
			datainputstream.readFully(headers, 0, 6);
			decmp_len = ((headers[0] & 0xFF) << 16) + ((headers[1] & 0xFF) << 8) + (headers[2] & 0xFF);
			cmp_len = ((headers[3] & 0xFF) << 16) + ((headers[4] & 0xFF) << 8) + (headers[5] & 0xFF);
			clientPort.showLoadingProgress(startPercentage, "Loading " + fileTitle + " - 5%");
			int l = 0;
			data = new byte[cmp_len];
			while (l < cmp_len) {
				int i1 = cmp_len - l;
				if (i1 > 1000)
					i1 = 1000;
				datainputstream.readFully(data, l, i1);
				l += i1;
				clientPort.showLoadingProgress(startPercentage,
						"Loading " + fileTitle + " - " + (5 + l * 95 / cmp_len) + "%");
			}
			datainputstream.close();
		} catch (IOException _ex) {
			_ex.printStackTrace();
		}
		clientPort.showLoadingProgress(startPercentage, "Unpacking " + fileTitle);
		if (cmp_len != decmp_len) {
			byte[] buffer = new byte[decmp_len];
			DataFileDecrypter.unpackData(buffer, decmp_len, data, cmp_len, 0);
			return buffer;
		}
		return data;
	}

	@Override
	public final void run() {
		try {


			try {
				if (this.gameState == 1) {
					for (this.gameState = 2; !clientPort.isDisplayable() && this.threadState >= 0; GenUtil
							.sleepShadow((long) this.sleepModifier)) {
						if (this.threadState > 0) {
							--this.threadState;
							if (this.threadState == 0) {
								this.closeProgram();
								this.clientBaseThread = null;
								return;
							}
						}
					}
					if (this.threadState < 0) {
						if (this.threadState == -1) {
							this.closeProgram();
						}

						this.clientBaseThread = null;
						return;
					}

					if (!this.loadLogo()) {

						if (this.threadState != -2) {
							this.closeProgram();
						}

						this.clientBaseThread = null;
						return;
					}

					this.startGame((byte) -92);
					this.gameState = 0;
				}
				//
				// ClientBase.containerFrame.addMouseListener(this);
				// ClientBase.containerFrame.addMouseMotionListener(this);
				// ClientBase.containerFrame.addKeyListener(this);
				// ClientBase.containerFrame.addWindowListener(new
				// WindowAdapter() {
				// @Override
				// public void windowClosing(WindowEvent e) {
				// threadState = -1;
				//
				// GenUtil.sleepShadow(5000L);
				// if (threadState == -1) {
				// System.out.println("5 seconds expired, forcing kill");
				// closeProgram();
				// if (clientBaseThread != null) {
				// try {
				// clientBaseThread.join();
				// } catch (InterruptedException e1) {
				// e1.printStackTrace();
				// }
				// clientBaseThread = null;
				// }
				// }
				// }
				// });
				// ClientBase.containerFrame.addComponentListener(this);

				int var3 = 0;
				int var4 = 256;
				int var5 = 1;
				int var6 = 0;

				int var7;
				for (var7 = 0; var7 < 10; ++var7) {
					this.m_F[var7] = GenUtil.currentTimeMillis();
				}

				long var1 = GenUtil.currentTimeMillis();

				while (this.threadState >= 0) {
					if (this.threadState > 0) {
						--this.threadState;
						if (this.threadState == 0) {
							this.closeProgram();
							this.clientBaseThread = null;
							return;
						}
					}

					var7 = var4;
					var4 = 300;
					int var8 = var5;
					var5 = 1;
					var1 = GenUtil.currentTimeMillis();
					if (~this.m_F[var3] == -1L) {
						var5 = var8;
						var4 = var7;
					} else if (~var1 < ~this.m_F[var3]) {
						var4 = (int) ((long) (this.sleepModifier * 2560) / (var1 + -this.m_F[var3]));
					}

					if (var4 < 25) {
						var4 = 25;
					}

					if (var4 > 256) {
						var4 = 256;
						var5 = (int) (-((-this.m_F[var3] + var1) / 10L) + (long) this.sleepModifier);
						if (var5 < this.m_Q) {
							var5 = this.m_Q;
						}
					}
					GenUtil.sleepShadow((long) var5);
					this.m_F[var3] = var1;
					int var9;
					if (var5 > 1) {
						for (var9 = 0; var9 < 10; ++var9) {
							if (-1L != ~this.m_F[var9]) {
								this.m_F[var9] += (long) var5;
							}
						}
					}

					var3 = (1 + var3) % 10;
					var9 = 0;

					while (var6 < 256) {
						this.update();
						var6 += var4;
						++var9;
						if (var9 > this.m_S) {
							var6 = 0;
							this.m_b += 6;
							if (this.m_b > 25) {
								this.m_b = 0;
								this.interlace = true;
							}
							break;
						}
					}

					--this.m_b;
					var6 &= 255;
					if (reposition()) {
						continue;
					}
					this.draw();
					currentFPS++;
					long time = System.currentTimeMillis();
					if (time - lastFPSUpdate >= 1000) {
						lastFPSUpdate = time;
						mudclient.FPS = currentFPS;
						currentFPS = 0;

					}

				}

				if (this.threadState == -1) {
					this.closeProgram();
				}

				this.clientBaseThread = null;
			} catch (Exception var10) {
				var10.printStackTrace();
				this.errorGameCrash("crash");
			}

		} catch (RuntimeException var11) {
			throw GenUtil.makeThrowable(var11, "e.run()");
		}
	}

	public void startMainThread() {
		this.clientBaseThread = new Thread(this);
		this.clientBaseThread.start();
		if(!Config.isAndroid()) {
			this.clientBaseThread.setPriority(1);
		}
		gameState = 1;
	}

	public void startThread(int andStart, Runnable proc) {
		try {

			Thread var3 = new Thread(proc);
			if (andStart == 1) {
				var3.setDaemon(true);
				var3.start();
			}
		} catch (RuntimeException var4) {
			throw GenUtil.makeThrowable(var4, "e.S(" + andStart + ',' + (proc != null ? "{...}" : "null") + ')');
		}
	}

	static final int RSC_VERSION = 19;

	public static int rscRunningVersion;
	/**
	 * Newest RSC cache: SAME VALUES.
	 *
	 * mudclient.spriteMedia = 2000; mudclient.spriteUtil =
	 * mudclient.spriteMedia + 100; 2100 mudclient.spriteItem = 50 +
	 * mudclient.spriteUtil; 2150 mudclient.spriteLogo = 1000 +
	 * mudclient.spriteItem; 3150 mudclient.spriteProjectile = 10 +
	 * mudclient.spriteLogo; 3160 mudclient.spriteTexture = 50 +
	 * mudclient.spriteProjectile; 3210
	 */
	public static final int spriteMedia = 2000;
	public static final int spriteUtil = 2100;
	public static final int spriteItem = 2150;
	static final int spriteLogo = 3150;
	static final int spriteProjectile = 3160;
	static final int spriteTexture = 3225;
	//
	// public static final void main(String[] var0) {
	// // mudclient cli = new mudclient();
	// // cli.runningAsApplet = false;
	// // cli.allowDebugCommands = true;
	// // cli.serverTypeMembers = true;
	// // cli.serverTypeVeterans = true;
	// // cli.startApplicationAsFrame(false, "RuneScape Classic", "classic", 0,
	// // mudclient.RSC_VERSION, cli.getGameWidth(),
	// // cli.getGameHeight() + 12);
	// // cli.m_Q = 10;7
	// }

	private final int[][] animDirLayer_To_CharLayer = new int[][] { { 11, 2, 9, 7, 1, 6, 10, 0, 5, 8, 3, 4 },
		{ 11, 2, 9, 7, 1, 6, 10, 0, 5, 8, 3, 4 }, { 11, 3, 2, 9, 7, 1, 6, 10, 0, 5, 8, 4 },
		{ 3, 4, 2, 9, 7, 1, 6, 10, 8, 11, 0, 5 }, { 3, 4, 2, 9, 7, 1, 6, 10, 8, 11, 0, 5 },
		{ 4, 3, 2, 9, 7, 1, 6, 10, 8, 11, 0, 5 }, { 11, 4, 2, 9, 7, 1, 6, 10, 0, 5, 8, 3 },
		{ 11, 2, 9, 7, 1, 6, 10, 0, 5, 8, 4, 3 } };
		private final int[] animFrameToSprite_CombatA = new int[] { 0, 1, 2, 1, 0, 0, 0, 0 };
		private final int[] animFrameToSprite_CombatB = new int[] { 0, 0, 0, 0, 0, 1, 2, 1 };
		private int[] animFrameToSprite_Walk = new int[] { 0, 1, 2, 1 };
		private int appearanceHeadGender = 1;
		private int appearanceHeadType = 0;
		private int autoLoginTimeout = 0;
		public int bankItemCount = 0;
		private final int[] bankItemID = new int[500];
		private final int[] bankItemSize = new int[500];
		public final int[] bankItemOnTab = new int[500];
		public int bankItemsMax = 50;
		public int bankPage = 0;
		public int bankSelectedItemSlot = -1;
		private int cameraAngle = 1;
		private final boolean cameraAutoAngleDebug = false;
		private int cameraAutoRotatePlayerX = 0;
		private int cameraAutoRotatePlayerZ = 0;
		public int cameraRotation = 128;
		private boolean cameraAllowPitchModification = false;
		private int cameraPitch = 912;
		private int cameraRotationX = 0;
		private int cameraRotationZ = 0;
		private int cameraZoom = 550;
		private int characterBubbleCount = 0;
		private int[] characterBubbleID = new int[150];
		private final int[] characterBubbleScale = new int[150];
		private final int[] characterBubbleX = new int[150];
		private final int[] characterBubbleY = new int[150];
		private int characterDialogCount = 0;
		private final int[] characterDialogHalfWidth = new int[150];
		private final int[] characterDialogHeight = new int[150];
		private final String[] characterDialogString = new String[150];
		private final int[] characterDialogX = new int[150];
		private final int[] characterDialogY = new int[150];
		private int[] characterHealthBar = new int[150];
		private int characterHealthCount = 0;
		private final int[] characterHealthX = new int[150];
		private final int[] characterHealthY = new int[150];
		private String chatMessageTarget;
		private Network_Socket clientStream;
		private int frameCounter = 0;
		private int combatStyle = 0;
		private int combatTimeout = 0;
		private int controlButtonAppearanceHeadMinus;
		private int controlButtonAppearanceHeadPlus;
		private int controlLoginPass;
		private int controlLoginStatus1;
		private int controlLoginStatus2;
		private int controlLoginUser;
		private int controlMagicPanel;
		private int controlPlayerInfoPanel;
		private int controlQuestInfoPanel;
		private int controlSettingPanel;
		private int controlPlayerTaskInfoPanel;
		private int controlSocialPanel;
		private int controlClanPanel;
		private int currentRegionMaxX;
		private int currentRegionMaxZ;
		private int currentRegionMinX;
		private int currentRegionMinZ;
		public GameMode currentViewMode = GameMode.LOGIN;
		private int deathScreenTimeout = 0;
		private boolean duelConfirmed = false;
		private String duelConfirmOpponentName = "";
		private int duelDoX_Slot;
		private int dropInventorySlot = -1;
		private final int[] duelItemCounts = new int[8];
		private int[] duelItems = new int[8];
		private int duelItemsCount = 0;
		private boolean duelOfferAccepted = false;
		private int duelOfferItemCount = 0;
		private final int[] duelOfferItemID = new int[8];
		private final int[] duelOfferItemSize = new int[8];
		private boolean duelOffsetOpponentAccepted = false;
		private int duelOffsetOpponentItemCount = 0;
		private final int[] duelOpponentItemCount = new int[8];
		private final int[] duelOpponentItemCounts = new int[8];
		private final int[] duelOpponentItemId = new int[8];
		private final int[] duelOpponentItems = new int[8];
		private int duelOpponentItemsCount = 0;
		private String duelOpponentName;
		private int duelOptionMagic;
		private int duelOptionPrayer;
		private int duelOptionRetreat;
		private int duelOptionWeapons;
		private boolean duelSettingsMagic = false;
		private boolean duelSettingsPrayer = false;
		private boolean duelSettingsRetreat = false;
		private boolean duelSettingsWeapons = false;
		private final String[] equipmentStatNames = new String[] { "Armour", "WeaponAim", "WeaponPower", "Magic",
		"Prayer" };
		private boolean errorLoadingCoadebase = false;
		private boolean errorLoadingData = false;
		private boolean errorLoadingMemory = false;
		private final int[] experienceArray = new int[99];
		private int fatigueSleeping = 0;
		private boolean fogOfWar = false;
		private int gameHeight = 334;
		private final boolean[] gameObjectInstance_Arg1 = new boolean[5000];
		private int gameObjectInstanceCount = 0;
		private final int[] gameObjectInstanceDir = new int[5000];
		private final int[] gameObjectInstanceID = new int[5000];
		private final RSModel[] gameObjectInstanceModel = new RSModel[5000];
		private final int[] gameObjectInstanceX = new int[5000];
		private int[] gameObjectInstanceZ = new int[5000];
		private int gameWidth = 512;
		private int groundItemCount = 0;
		private final int[] groundItemID = new int[5000];
		private final int[] groundItemX = new int[5000];
		private final int[] groundItemZ = new int[5000];
		public InputXAction inputX_Action = InputXAction.ACT_0;
		private boolean inputX_Focused = true;
		private int inputX_Height = 0;
		private String[] inputX_Lines = null;
		private boolean inputX_OK = false;
		private int inputX_Width = 0;
		private boolean insideTutorial = false;
		private int inventoryItemCount;
		private final int[] inventoryItemEquipped = new int[35];
		private final int[] inventoryItemID = new int[35];
		private final int[] inventoryItemSize = new int[35];
		private boolean isSleeping = false;
		private int knownPlayerCount = 0;
		private final RSCharacter[] knownPlayers = new RSCharacter[500];
		private int lastHeightOffset = -1;
		private int lastObjectAnimationNumberFireLightningSpell = -1;
		private int lastObjectAnimationNumberTorch = -1;
		private int lastObjectAnimatonNumberClaw = -1;
		private boolean loadingArea = false;
		private RSCharacter localPlayer = new RSCharacter();
		private int logoutTimeout = 0;
		private final String[] optionsMenuText = new String[20];
		private int m_Ai;
		private int m_be;
		private int m_Ce = 0;
		private int m_Cg;
		private int m_cl = 30;
		private int m_dk = 1;
		private int m_ed;
		private int m_eg = 2;
		private int m_Eg;
		private int m_ek;
		private int m_Ge;
		private int m_hh = 0;
		private boolean runningAsApplet = true;
		private boolean allowDebugCommands = !runningAsApplet || true;
		private int optionsMenuCount = 0;
		private String m_ig = "";
		private int m_ii = 0;
		private int m_Ji = 0;
		private int settingTab = 0;
		private int m_Jj;
		private int m_Kj;
		private int m_ld = 2;
		private final int[] m_Le = new int[5000];
		private int characterBottomColour = 14;
		private int m_Mj;
		private int m_nj = -1;
		private int m_Of;
		private int m_oj = 0;
		private int m_Oj = 0;
		private int m_Ok = 2;
		private int m_qd = 9;
		private int m_rc = 0;
		private int m_Re;
		private int m_rf;
		private boolean m_ue = false;
		private int m_Wc = 0;
		private final int character2Colour = 2;
		private int m_Wg = 8;
		private long lastWrite;
		private int m_wk = -1;
		private int m_Xc;
		private int loginScreenNumber = 0;
		private int m_Xi;
		private int rememberButtonIdx;
		private int m_Zb = 0;
		private int m_Zc = -1;
		private int m_Ze;
		public Menu menuCommon;
		private Menu menuDuel;
		private boolean menuDuel_Visible = false;
		private int menuDuelX;
		private int menuDuelY;
		private Menu menuTrade;
		private boolean menuTrade_Visible = false;
		private int menuTradeX;
		private int menuTradeY;
		private boolean menuVisible = false;
		public int menuX = 0;
		public int menuY = 0;
		private int messageTabActivity_Chat = 0;
		private int messageTabActivity_Game = 0;
		private int messageTabActivity_Private = 0;
		private int messageTabActivity_Clan = 0;
		private int messageTabActivity_Quest = 0;
		private MessageTab messageTabSelected = MessageTab.ALL;
		private int midRegionBaseX;
		private int midRegionBaseZ;
		private int minimapRandom_1 = 0;
		private int minimapRandom_2 = 0;
		private final RSModel[] modelCache = new RSModel[1000];
		public int mouseButtonClick = 0;
		private int mouseButtonDownTime = 0;
		private int mouseButtonItemCountIncrement = 0;
		public int mouseClickCount = 0;
		public final int[] mouseClickX = new int[8192];
		public int mouseClickXStep = 0;
		public final int[] mouseClickY = new int[8192];
		private int mouseWalkX = 0;
		private int mouseWalkY = 0;
		private int newBankItemCount = 0;
		private final int[] newBankItems = new int[500];
		private final int[] newBankItemsCount = new int[500];
		private int npcCacheCount = 0;
		private int npcCount = 0;
		private final RSCharacter[] npcs = new RSCharacter[500];
		private final RSCharacter[] npcsCache = new RSCharacter[500];
		private final RSCharacter[] npcsServer = new RSCharacter[5000];
		private int objectAnimationCount = 0;
		private int objectAnimationNumberClaw = 0;
		private int objectAnimationNumberFireLightningSpell = 0;
		private int objectAnimationNumberTorch = 0;
		private boolean optionCameraModeAuto = true;
		private boolean optionMouseButtonOne = false;
		private boolean optionSoundDisabled = true;
		private boolean clanInviteBlockSetting = false;
		private final RSBuffer_Bits packetsIncoming = new RSBuffer_Bits(30000);
		private Panel panelAppearance;
		private Panel panelLogin;
		private Panel panelLoginWelcome;
		private Panel panelMagic;
		private int panelMessageChat;
		private int panelMessageEntry;
		private int panelMessagePrivate;
		private int panelMessageQuest;
		private int panelMessageClan;
		private Panel panelMessageTabs;
		private Panel panelPlayerInfo;
		private Panel panelQuestInfo;
		//private Panel panelPlayerTaskInfo;
		private Panel panelSettings;
		private Panel panelSocial;
		private Panel panelClan;
		private SocialPopupMode panelSocialPopup_Mode = SocialPopupMode.NONE;
		private int panelSocialTab = 0;
		private String password = "";
		private final int[] pathX = new int[8000];
		private final int[] pathZ = new int[8000];
		private final int[] playerClothingColors = new int[] { 16711680, 16744448, 16769024, 10543104, '\ue000', '\u8000',
				'\ua080', '\ub0ff', '\u80ff', 12528, 14680288, 3158064, 6307840, 8409088, 16777215 };
		private int playerCount = 0;
		private final int[] playerExperience = new int[18];
		private final int[] playerHairColors = new int[] { 16760880, 16752704, 8409136, 6307872, 3158064, 16736288,
				16728064, 16777215, '\uff00', '\uffff' };
		private int playerLocalX;
		private int playerLocalZ;
		private final RSCharacter[] players = new RSCharacter[500];
		private final RSCharacter[] playerServer = new RSCharacter[4000];
		private final int[] playerSkinColors = new int[] { 15523536, 13415270, 11766848, 10056486, 9461792 };
		private final int[] playerStatBase = new int[18];
		private int[] playerStatCurrent = new int[18];
		private String[] messagesArray = new String[5];
		private final int[] playerStatEquipment = new int[5];
		private int[] playerStatXpGained = new int[18];
		private long[] xpGainedStartTime = new long[18];
		private int playerXpGainedTotal = 0;
		public long totalXpGainedStartTime = 0;
		private final boolean[] prayerOn = new boolean[50];
		private final int projectileMaxRange = 40;
		private String[] questNames = new String[100];
		public String[] achievementNames = new String[500];
		public String[] achievementTitles = new String[500];
		public String[] achievementDescs = new String[500];
		private int[] questStages = new int[100];
		public int[] achievementProgress = new int[500];
		private int reportAbuse_AbuseType = 0;
		private String reportAbuse_Name = "";
		private int reportAbuse_State = 0;
		private int requestedPlane = -1;
		private Scene scene;
		private int selectedItemInventoryIndex = -1;
		private int selectedSpell = -1;
		private String serverMessage = "";
		private boolean serverMessageBoxTop = false;
		public boolean serverTypeMembers = Config.MEMBERS_FEATURES;
		private boolean developerMenu = false;
		private int devMenuNpcID;
		private boolean modMenu = false;
		private int settingsBlockChat = 0;
		private int settingsBlockDuel = 0;
		private int settingsBlockPrivate = 0;
		private int settingsBlockTrade = 0;
		private int shopBuyPriceMod = 0;
		private final int[] shopItemCount = new int[256];
		private final int[] shopItemID = new int[256];
		private final int[] shopItemPrice = new int[256];
		private int shopPriceMultiplier = 0;
		private int shopSelectedItemIndex = -1;
		private int shopSelectedItemType = -2;
		private int shopSellPriceMod = 0;
		private boolean showAppearanceChange = false;
		private boolean showDialogBank = false;
		private boolean showDialogDuel = false;
		private boolean showDialogDuelConfirm = false;
		private boolean showDialogMessage = false;
		private boolean showDialogServerMessage = false;
		private boolean showDialogShop = false;
		private boolean showDialogTrade = false;
		private boolean showDialogTradeConfirm = false;
		private boolean optionsMenuShow = false;
		public int showUiTab = 0;
		private int showUiWildWarn = 0;
		private final String[] skillNameLong = new String[] { "Attack", "Defense", "Strength", "Hits", "Ranged", "Prayer",
				"Magic", "Cooking", "Woodcutting", "Fletching", "Fishing", "Firemaking", "Crafting", "Smithing", "Mining",
				"Herblaw", "Agility", "Thieving" };
		private final String[] skillNames = new String[] { "Attack", "Defense", "Strength", "Hits", "Ranged", "Prayer",
				"Magic", "Cooking", "Woodcut", "Fletching", "Fishing", "Firemaking", "Crafting", "Smithing", "Mining",
				"Herblaw", "Agility", "Thieving" };
		private int recentSkill = -1;
		private String sleepingStatusText = null;
		private boolean sleepWordDelay = true;
		private int sleepWordDelayTimer = 0;
		private byte[] soundData = null;
		private int spriteCount = 0;
		private int statFatigue = 0;
		private MudClientGraphics surface;
		private int systemUpdate = 0;
		private int elixirTimer = 0;
		private boolean inWild = false;
		private int teleportBubbleCount = 0;
		private final int[] teleportBubbleTime = new int[50];
		private int[] teleportBubbleType = new int[50];
		private final int[] teleportBubbleX = new int[50];
		private final int[] teleportBubbleZ = new int[50];
		private final int tileSize = 128;
		public boolean topMouseMenuVisible = false;
		private boolean tradeAccepted = false;
		private boolean tradeConfirmAccepted = false;
		private final int[] tradeConfirmItems = new int[14];
		private int tradeConfirmItemsCount = 0;
		private final int[] tradeConfirmItemsCount1 = new int[14];
		private int tradeDoX_Slot;
		private int tradeItemCount = 0;
		private final int[] tradeItemID = new int[14];
		private final int[] tradeItemSize = new int[14];
		private boolean tradeRecipientAccepted = false;
		private final int[] tradeRecipientConfirmItemCount = new int[14];
		private final int[] tradeRecipientConfirmItems = new int[14];
		private int tradeRecipientConfirmItemsCount = 0;
		private String tradeRecipientConfirmName;
		private final int[] tradeRecipientItem = new int[14];
		private final int[] tradeRecipientItemCount = new int[14];
		private int tradeRecipientItemsCount = 0;
		private String tradeRecipientName = "";
		private int uiTabPlayerInfoSubTab = 0;
		private String username = "";
		private final boolean[] wallObjectInstance_Arg1 = new boolean[500];
		private int wallObjectInstanceCount = 0;
		private final int[] wallObjectInstanceDir = new int[500];
		private final int[] wallObjectInstanceID = new int[500];
		private final RSModel[] wallObjectInstanceModel = new RSModel[500];
		private final int[] wallObjectInstanceX = new int[500];
		private final int[] wallObjectInstanceZ = new int[500];
		private int welcomeLastLoggedInDays = 0;
		private String welcomeLastLoggedInHost = null;
		private String welcomeLastLoggedInIp;

		private int welcomeRecoverySetDays = 0;

		private boolean welcomeScreenShown = false;

		private int welcomeUnreadMessages = 0;
		private int welcomeSubscriptionDaysLeft = 0;
		private int welcomePremiumDaysLeft = 0;
		private World world;

		public World getWorld() {
			return world;
		}

		private int worldOffsetX = 0;
		private int worldOffsetZ = 0;
		private int prayerMenuIndex = 0;
		private int magicMenuIndex = 0;

		private Panel menuNewUser;

		private int menuNewUserUsername;
		private int menuNewUserPassword;
		private int menuNewUserEmail;
		private int menuNewUserStatus;
		private int menuNewUserStatus2;
		private int menuNewUserSubmit;
		private int menuNewUserCancel;

		private int loginButtonNewUser;

		public final void addFriend(String player) {
			try {

				if (200 <= SocialLists.friendListCount) {
					this.showMessage(false, (String) null, "Friend list is full", MessageType.GAME, 0, (String) null,
							(String) null);
				} else {
					String var3 = StringUtil.displayNameToKey(player);
					if (null != var3) {
						int var4;
						for (var4 = 0; var4 < SocialLists.friendListCount; ++var4) {
							if (var3.equals(StringUtil.displayNameToKey(SocialLists.friendList[var4]))) {
								this.showMessage(false, (String) null, player + " is already on your friend list.",
										MessageType.GAME, 0, (String) null, (String) null);
								return;
							}

							if (SocialLists.friendListOld[var4] != null
									&& var3.equals(StringUtil.displayNameToKey(SocialLists.friendListOld[var4]))) {
								this.showMessage(false, (String) null, player + " is already on your friend list.",
										MessageType.GAME, 0, (String) null, (String) null);
								return;
							}
						}

						for (var4 = 0; var4 < SocialLists.ignoreListCount; ++var4) {
							if (var3.equals(StringUtil.displayNameToKey(SocialLists.ignoreListArg0[var4]))) {
								this.showMessage(false, (String) null,
										"Please remove " + player + " from your ignore list first.", MessageType.GAME, 0,
										(String) null, (String) null);
								return;
							}

							if (null != SocialLists.ignoreListArg1[var4]
									&& var3.equals(StringUtil.displayNameToKey(SocialLists.ignoreListArg1[var4]))) {
								this.showMessage(false, (String) null,
										"Please remove " + player + " from your ignore list first.", MessageType.GAME, 0,
										(String) null, (String) null);
								return;
							}
						}

						if (!var3.equals(StringUtil.displayNameToKey(this.localPlayer.accountName))) {
							this.getClientStream().newPacket(195);
							this.getClientStream().writeBuffer1.putString(player);
							this.getClientStream().finishPacket();
						} else {
							this.showMessage(false, (String) null, "You can\'t add yourself to your own friend list.",
									MessageType.GAME, 0, (String) null, (String) null);
						}
					}
				}
			} catch (RuntimeException var5) {
				throw GenUtil.makeThrowable(var5, "client.PC(" + "dummy" + ',' + (player != null ? "{...}" : "null") + ')');
			}
		}

		public final void addIgnore(String player) {
			try {

				if (SocialLists.ignoreListCount >= 100) {
					this.showMessage(false, (String) null, "Ignore list full", MessageType.GAME, 0, (String) null,
							(String) null);
				} else {
					String var3 = StringUtil.displayNameToKey(player);
					if (var3 != null) {
						int var4;
						for (var4 = 0; var4 < SocialLists.ignoreListCount; ++var4) {
							if (var3.equals(StringUtil.displayNameToKey(SocialLists.ignoreListArg0[var4]))) {
								this.showMessage(false, (String) null, player + " is already on your ignore list",
										MessageType.GAME, 0, (String) null, (String) null);
								return;
							}

							if (SocialLists.ignoreListArg1[var4] != null
									&& var3.equals(StringUtil.displayNameToKey(SocialLists.ignoreListArg1[var4]))) {
								this.showMessage(false, (String) null, player + " is already on your ignore list",
										MessageType.GAME, 0, (String) null, (String) null);
								return;
							}
						}

						for (var4 = 0; var4 < SocialLists.friendListCount; ++var4) {
							if (var3.equals(StringUtil.displayNameToKey(SocialLists.friendList[var4]))) {
								this.showMessage(false, (String) null,
										"Please remove " + player + " from your friends list first", MessageType.GAME, 0,
										(String) null, (String) null);
								return;
							}

							if (SocialLists.friendListOld[var4] != null
									&& var3.equals(StringUtil.displayNameToKey(SocialLists.friendListOld[var4]))) {
								this.showMessage(false, (String) null,
										"Please remove " + player + " from your friends list first", MessageType.GAME, 0,
										(String) null, (String) null);
								return;
							}
						}

						if (!var3.equals(StringUtil.displayNameToKey(this.localPlayer.accountName))) {
							this.getClientStream().newPacket(132);
							this.getClientStream().writeBuffer1.putString(player);
							this.getClientStream().finishPacket();
						} else {
							this.showMessage(false, (String) null, "You can\'t add yourself to your ignore list",
									MessageType.GAME, 0, (String) null, (String) null);
						}
					}
				}
			} catch (RuntimeException var5) {
				throw GenUtil.makeThrowable(var5, "client.MC(" + (player != null ? "{...}" : "null") + ',' + "dummy" + ')');
			}
		}

		public final void addMouseClick(int button, int x, int y) {
			try {

				this.mouseClickX[this.mouseClickCount] = x;
				this.mouseClickY[this.mouseClickCount] = y;
				this.mouseClickCount = this.mouseClickCount + 1 & 8191;
				for (int i = 10; i < 4000; ++i) {
					int pID = 8191 & this.mouseClickCount - i;
					if (this.mouseClickX[pID] == x && y == this.mouseClickY[pID]) {
						boolean hasInteracted = false;

						for (int j = 1; j < i; ++j) {
							int sID = this.mouseClickCount - j & 8191;
							int psID = 8191 & pID - j;
							if (x != this.mouseClickX[psID] || y != this.mouseClickY[psID]) {
								hasInteracted = true;
							}

							if (this.mouseClickX[sID] != this.mouseClickX[psID]
									|| this.mouseClickY[sID] != this.mouseClickY[psID]) {
								break;
							}

							if (j == i - 1 && hasInteracted && this.combatTimeout == 0 && this.logoutTimeout == 0) {
								this.sendLogout(0);
								return;
							}
						}
					}
				}

			} catch (RuntimeException var11) {
				throw GenUtil.makeThrowable(var11, "client.Q(" + x + ',' + "dummy" + ',' + button + ',' + y + ')');
			}
		}

		private final void addPlayerToMenu(int index) {
			try {

				RSCharacter player = this.players[index];
				String name = player.displayName;
				int var5 = 2203 - this.midRegionBaseZ - this.playerLocalZ - this.worldOffsetZ;
				if (this.midRegionBaseX + this.playerLocalX + this.worldOffsetX >= 2640) {
					var5 = -50;
				}

				String level = "";
				int levelDelta = 0;
				if (this.localPlayer.level > 0 && player.level > 0) {
					levelDelta = this.localPlayer.level - player.level;
				}

				if (levelDelta < 0) {
					level = "@or1@";
				}

				if (levelDelta < -3) {
					level = "@or2@";
				}

				if (levelDelta < -6) {
					level = "@or3@";
				}

				if (levelDelta < -9) {
					level = "@red@";
				}

				if (levelDelta > 0) {
					level = "@gr1@";
				}

				if (levelDelta > 3) {
					level = "@gr2@";
				}

				if (levelDelta > 6) {
					level = "@gr3@";
				}

				if (levelDelta > 9) {
					level = "@gre@";
				}

				level = " " + level + "(level-" + player.level + ")";
				if (this.selectedSpell >= 0) {
					if (EntityHandler.getSpellDef(selectedSpell).getSpellType() == 1
							|| EntityHandler.getSpellDef(selectedSpell).getSpellType() == 2) {
						this.menuCommon.addCharacterItem_WithID(player.serverIndex, "@whi@" + name + level,
								MenuItemAction.PLAYER_CAST_SPELL,
								"Cast " + EntityHandler.getSpellDef(selectedSpell).getName() + " on", this.selectedSpell);
					}
				} else if (this.selectedItemInventoryIndex >= 0) {
					this.menuCommon.addCharacterItem_WithID(player.serverIndex, "@whi@" + name + level,
							MenuItemAction.PLAYER_USE_ITEM, "Use " + this.m_ig + " with", this.selectedItemInventoryIndex);
				} else {

					if (var5 > 0
							&& (player.currentZ - 64) / this.tileSize - (-this.worldOffsetZ - this.midRegionBaseZ) < 2203) {
						this.menuCommon.addCharacterItem(player.serverIndex, levelDelta >= 0 && levelDelta < 5
								? MenuItemAction.PLAYER_ATTACK_SIMILAR : MenuItemAction.PLAYER_ATTACK_DIVERGENT, "Attack",
										"@whi@" + name + level);
					}
					else {
						this.menuCommon.addCharacterItem(player.serverIndex, MenuItemAction.PLAYER_DUEL, "Duel with",
								"@whi@" + name + level);
					}


					this.menuCommon.addCharacterItem(player.serverIndex, MenuItemAction.PLAYER_TRADE, "Trade with",
							"@whi@" + name + level);
					this.menuCommon.addCharacterItem(player.serverIndex, MenuItemAction.PLAYER_FOLLOW, "Follow",
							"@whi@" + name + level);
					this.menuCommon.addItem_With2Strings("Report abuse", "@whi@" + name + level, player.displayName,
							MenuItemAction.REPORT_ABUSE, player.accountName);
					if (modMenu) {
						this.menuCommon.addItem_With2Strings("Summon", "@whi@" + name, player.displayName,
								MenuItemAction.MOD_SUMMON_PLAYER, player.accountName);
						this.menuCommon.addItem_With2Strings("Goto", "@whi@" + name, player.displayName,
								MenuItemAction.MOD_GOTO_PLAYER, player.accountName);
						this.menuCommon.addItem_With2Strings("Jail", "@whi@" + name, player.displayName,
								MenuItemAction.MOD_PUT_PLAYER_JAIL, player.accountName);
						this.menuCommon.addItem_With2Strings("Check", "@whi@" + name, player.displayName,
								MenuItemAction.MOD_CHECK_PLAYER, player.accountName);
						this.menuCommon.addItem_With2Strings("Kick", "@whi@" + name, player.displayName,
								MenuItemAction.MOD_KICK_PLAYER, player.accountName);
					}
				}

			} catch (RuntimeException var8) {
				throw GenUtil.makeThrowable(var8, "client.N(" + index + ',' + "dummy" + ')');
			}
		}

		private final void autoRotateCamera(byte var1) {
			try {

				if ((this.cameraAngle & 1) != 1 || !this.cameraColliding((int) this.cameraAngle)) {
					if ((1 & this.cameraAngle) == 0 && this.cameraColliding((int) this.cameraAngle)) {
						if (!this.cameraColliding(1 + this.cameraAngle & 7)) {
							if (this.cameraColliding((int) (7 & 7 + this.cameraAngle))) {
								this.cameraAngle = 7 & 7 + this.cameraAngle;
							}
						} else {
							this.cameraAngle = 7 & 1 + this.cameraAngle;
						}

					} else {
						int[] var2 = new int[] { 1, -1, 2, -2, 3, -3, 4 };
						int var3 = 0;
						if (var1 > 7) {
							while (var3 < 7) {
								if (this.cameraColliding((int) (7 & 8 + this.cameraAngle + var2[var3]))) {
									this.cameraAngle = 7 & this.cameraAngle + var2[var3] + 8;
									break;
								}

								++var3;
							}

							if ((1 & this.cameraAngle) == 0 && this.cameraColliding((int) this.cameraAngle)) {
								if (this.cameraColliding((int) (7 & 1 + this.cameraAngle))) {
									this.cameraAngle = 1 + this.cameraAngle & 7;
								} else if (this.cameraColliding(7 + this.cameraAngle & 7)) {
									this.cameraAngle = 7 & 7 + this.cameraAngle;
								}

							}
						}
					}
				}
			} catch (RuntimeException var4) {
				throw GenUtil.makeThrowable(var4, "client.AE(" + var1 + ')');
			}
		}

		private final boolean cameraColliding(int angle) {
			try {

				int tileX = this.localPlayer.currentX / 128;
				int tileZ = this.localPlayer.currentZ / 128;
				int tries = 2;

				while (tries >= 1) {
					if (angle != 1 || (CollisionFlag.OBJECT
							& this.world.collisionFlags[tileX][tileZ - tries]) != CollisionFlag.OBJECT
							&& (CollisionFlag.OBJECT
									& this.world.collisionFlags[tileX - tries][tileZ]) != CollisionFlag.OBJECT
									&& (this.world.collisionFlags[tileX - tries][tileZ - tries]
											& CollisionFlag.OBJECT) != CollisionFlag.OBJECT) {
						if (angle == 3 && ((CollisionFlag.OBJECT
								& this.world.collisionFlags[tileX][tries + tileZ]) == CollisionFlag.OBJECT
								|| (this.world.collisionFlags[tileX - tries][tileZ]
										& CollisionFlag.OBJECT) == CollisionFlag.OBJECT
										|| (CollisionFlag.OBJECT & this.world.collisionFlags[tileX - tries][tries
										                                                                    + tileZ]) == CollisionFlag.OBJECT)) {
							return false;
						}
						if (angle != 5 || (this.world.collisionFlags[tileX][tileZ + tries]
								& CollisionFlag.OBJECT) != CollisionFlag.OBJECT
								&& (this.world.collisionFlags[tries + tileX][tileZ]
										& CollisionFlag.OBJECT) != CollisionFlag.OBJECT
										&& (this.world.collisionFlags[tileX + tries][tileZ + tries]
												& CollisionFlag.OBJECT) != CollisionFlag.OBJECT) {
							if (angle == 7 && ((this.world.collisionFlags[tileX][tileZ - tries]
									& CollisionFlag.OBJECT) == CollisionFlag.OBJECT
									|| (CollisionFlag.OBJECT
											& this.world.collisionFlags[tries + tileX][tileZ]) == CollisionFlag.OBJECT
											|| (CollisionFlag.OBJECT & this.world.collisionFlags[tries + tileX][tileZ
											                                                                    - tries]) == CollisionFlag.OBJECT)) {
								return false;
							}
							if (angle == 0 && (this.world.collisionFlags[tileX][tileZ - tries]
									& CollisionFlag.OBJECT) == CollisionFlag.OBJECT) {
								return false;
							}
							if (angle == 2 && (this.world.collisionFlags[tileX - tries][tileZ]
									& CollisionFlag.OBJECT) == CollisionFlag.OBJECT) {
								return false;
							}
							if (angle == 4 && (CollisionFlag.OBJECT
									& this.world.collisionFlags[tileX][tries + tileZ]) == CollisionFlag.OBJECT) {
								return false;
							}
							if (angle == 6 && (this.world.collisionFlags[tileX + tries][tileZ]
									& CollisionFlag.OBJECT) == CollisionFlag.OBJECT) {
								return false;
							}
							--tries;
							continue;
						}
						return false;
					}
					return false;
				}
				return true;
			} catch (RuntimeException var6) {
				throw GenUtil.makeThrowable(var6, "client.NC(" + "dummy" + ',' + angle + ')');
			}
		}

		private final void cantLogout(byte var1) {
			try {
				if (var1 >= -19) {
					this.drawMenu();
				}

				this.logoutTimeout = 0;

				this.showMessage(false, (String) null, "Sorry, you can\'t logout at the moment", MessageType.GAME, 0,
						(String) null, "@cya@");
			} catch (RuntimeException var3) {
				throw GenUtil.makeThrowable(var3, "client.CB(" + var1 + ')');
			}
		}

		private final void checkConnection() {
			try {

				long var2 = GenUtil.currentTimeMillis();
				if (this.getClientStream().hasFinishedPackets()) {
					this.lastWrite = var2;
				}

				if (-5001L > ~(var2 + -this.lastWrite)) {
					this.lastWrite = var2;
					this.getClientStream().newPacket(67);
					this.getClientStream().finishPacket();
				}

				try {
					this.getClientStream().flush(0, true);
				} catch (IOException var5) {
					this.lostConnection(123);
					return;
				}
				int len = this.getClientStream().readIncomingPacket(this.packetsIncoming);
				if (len > 0) {
					this.handlePacket1(this.packetsIncoming.getUnsignedByte(), len);
				}
			} catch (RuntimeException var6) {
				throw GenUtil.makeThrowable(var6, "client.SB(" + "dummy" + ')');
			}
		}

		private final void clearInputString80(byte var1) {
			try {

				this.chatMessageInput = "";
				this.chatMessageInputCommit = "";
			} catch (RuntimeException var3) {
				throw GenUtil.makeThrowable(var3, "client.NA(" + var1 + ')');
			}
		}

		final void closeConnection() {
			try {

				this.closeConnection(true);
				clientPort.stopSoundPlayer();
			} catch (RuntimeException var3) {
				throw GenUtil.makeThrowable(var3, "client.SA(" + "dummy" + ')');
			}
		}

		private final void closeConnection(boolean sendPacket) {
			try {

				if (sendPacket && null != this.getClientStream()) {
					try {
						this.getClientStream().newPacket(31);
						this.getClientStream().finishPacketAndFlush();
					} catch (IOException var4) {
						;
					}
				}

				this.setUsername("");
				this.password = "";
				this.jumpToLogin();
			} catch (RuntimeException var5) {
				throw GenUtil.makeThrowable(var5, "client.RB(" + sendPacket + ',' + "dummy" + ')');
			}
		}

		private final void createAppearancePanel(int var1) {
			try {
				this.panelAppearance = new Panel(this.getSurface(), 100);

				this.panelAppearance.addCenteredText(256, 10, "Please design Your Character", 4, true);
				short var2 = 140;
				byte var3 = 34;
				int var6 = var2 + 116;
				int var7 = var3 - 10;
				this.panelAppearance.addCenteredText(var6 - 55, var7 + 110, "Front", 3, true);
				this.panelAppearance.addCenteredText(var6, var7 + 110, "Side", 3, true);
				this.panelAppearance.addCenteredText(var6 + 55, 110 + var7, "Back", 3, true);
				var7 += 145;
				byte var4 = 54;
				this.panelAppearance.addDecoratedBox((int) (var6 - var4), var7, 53, 41);
				this.panelAppearance.addCenteredText(var6 - var4, var7 - 8, "Head", 1, true);
				this.panelAppearance.addCenteredText(var6 - var4, var7 + 8, "Type", 1, true);
				this.panelAppearance.addSprite(var6 - var4 - 40, var7, mudclient.spriteUtil + 7);
				this.controlButtonAppearanceHeadMinus = this.panelAppearance.addButton(-40 - var4 + var6, var7, 20, 20);
				this.panelAppearance.addSprite(var6 - var4 + 40, var7, 6 + mudclient.spriteUtil);
				this.controlButtonAppearanceHeadPlus = this.panelAppearance.addButton(var6 + (40 - var4), var7, 20, 20);
				this.panelAppearance.addDecoratedBox((int) (var6 + var4), var7, 53, 41);
				this.panelAppearance.addCenteredText(var6 + var4, var7 - 8, "Hair", 1, true);
				this.panelAppearance.addCenteredText(var4 + var6, 8 + var7, "Color", 1, true);
				this.panelAppearance.addSprite(var4 + (var6 - 40), var7, 7 + mudclient.spriteUtil);
				this.m_Kj = this.panelAppearance.addButton(var6 + var4 - 40, var7, 20, 20);
				this.panelAppearance.addSprite(40 + var4 + var6, var7, 6 + mudclient.spriteUtil);
				this.m_ed = this.panelAppearance.addButton(40 + var4 + var6, var7, 20, 20);
				var7 += 50;
				this.panelAppearance.addDecoratedBox((int) (var6 - var4), var7, 53, 41);
				this.panelAppearance.addCenteredText(var6 - var4, var7, "Gender", 1, true);
				this.panelAppearance.addSprite(var6 - var4 - 40, var7, mudclient.spriteUtil + 7);
				this.m_Ge = this.panelAppearance.addButton(var6 - 40 - var4, var7, 20, 20);
				this.panelAppearance.addSprite(40 - var4 + var6, var7, mudclient.spriteUtil + 6);
				this.m_Of = this.panelAppearance.addButton(40 + (var6 - var4), var7, 20, 20);
				this.panelAppearance.addDecoratedBox((int) (var4 + var6), var7, 53, 41);
				this.panelAppearance.addCenteredText(var4 + var6, var7 - 8, "Top", 1, true);
				this.panelAppearance.addCenteredText(var4 + var6, 8 + var7, "Color", 1, true);
				this.panelAppearance.addSprite(var6 + (var4 - 40), var7, 7 + mudclient.spriteUtil);
				this.m_Xc = this.panelAppearance.addButton(var4 + (var6 - 40), var7, 20, 20);
				this.panelAppearance.addSprite(40 + var4 + var6, var7, 6 + mudclient.spriteUtil);
				this.m_ek = this.panelAppearance.addButton(var6 - (-var4 - 40), var7, 20, 20);
				var7 += 50;
				if (var1 != -24595) {
					this.renderLoginScreenViewports(-127);
				}

				this.panelAppearance.addDecoratedBox((int) (var6 - var4), var7, 53, 41);
				this.panelAppearance.addCenteredText(var6 - var4, var7 - 8, "Skin", 1, true);
				this.panelAppearance.addCenteredText(var6 - var4, var7 + 8, "Color", 1, true);
				this.panelAppearance.addSprite(var6 - 40 - var4, var7, 7 + mudclient.spriteUtil);
				this.m_Ze = this.panelAppearance.addButton(var6 - var4 - 40, var7, 20, 20);
				this.panelAppearance.addSprite(var6 - var4 + 40, var7, mudclient.spriteUtil + 6);
				this.m_Mj = this.panelAppearance.addButton(var6 + (40 - var4), var7, 20, 20);
				this.panelAppearance.addDecoratedBox((int) (var4 + var6), var7, 53, 41);
				this.panelAppearance.addCenteredText(var4 + var6, var7 - 8, "Bottom", 1, true);
				this.panelAppearance.addCenteredText(var4 + var6, var7 + 8, "Color", 1, true);
				this.panelAppearance.addSprite(var4 - 40 + var6, var7, mudclient.spriteUtil + 7);
				this.m_Re = this.panelAppearance.addButton(var6 - (40 - var4), var7, 20, 20);
				this.panelAppearance.addSprite(var6 + var4 + 40, var7, 6 + mudclient.spriteUtil);
				this.m_Ai = this.panelAppearance.addButton(40 + var4 + var6, var7, 20, 20);
				var7 += 82;
				var7 -= 35;
				this.panelAppearance.addButtonBackground(var6, var7, 200, 30);
				this.panelAppearance.addCenteredText(var6, var7, "Accept", 4, false);
				this.m_Eg = this.panelAppearance.addButton(var6, var7, 200, 30);
			} catch (RuntimeException var5) {
				throw GenUtil.makeThrowable(var5, "client.J(" + var1 + ')');
			}
		}

		private final void createLoginPanels(int var1) {
			try {

				this.panelLoginWelcome = new Panel(this.getSurface(), 50);
				byte var2 = 40;
				if(Config.isAndroid())
					var2 = -125;
				this.panelLoginWelcome.addCenteredText(256, 190 + var2, "Welcome to Open RSC", 4, true);
				String var3 = null;
				var3 = "You need to create an account on openrsc.com to use this server";
				if (null != var3) {
					this.panelLoginWelcome.addCenteredText(256, 205 + var2, var3, 4, true);
				}
				//
				// this.panelLoginWelcome.addButtonBackground(256, var2 + 250, 200,
				// 35);
				// this.panelLoginWelcome.addCenteredText(256, var2 + 250, "Click
				// here to login", 5, false);
				// this.m_Jj = this.panelLoginWelcome.addButton(256, 250 + var2,
				// 200, 35);
				//
				//
				panelLoginWelcome.addButtonBackground(156, 240 + var2, 120, 35);
				panelLoginWelcome.addButtonBackground(356, 240 + var2, 120, 35);

				panelLoginWelcome.addCenteredText(156, 240 + var2, "New User", 5, false);
				panelLoginWelcome.addCenteredText(356, 240 + var2, "Existing User", 5, false);

				loginButtonNewUser = panelLoginWelcome.addButton(156, 240 + var2, 120, 35);
				this.m_Jj = panelLoginWelcome.addButton(356, 240 + var2, 120, 35);

				this.panelLogin = new Panel(this.getSurface(), 50);
				short var5 = Config.isAndroid() ? (short) 30 : 230;
				this.controlLoginStatus1 = this.panelLogin.addCenteredText(256, var5 - 30, "", 4, true);
				this.controlLoginStatus2 = this.panelLogin.addCenteredText(256, var5 - 10,
						"Please enter your username and password", 4, true);
				int var6 = var5 + 28;
				this.panelLogin.addButtonBackground(140, var6, 200, 40);
				this.panelLogin.addCenteredText(140, var6 - 10, "Username:", 4, false);
				if (var1 != 3845) {
					this.drawNPC(51, 106, -15, -96, 26, 108, 22, -63);
				}

				this.controlLoginUser = this.panelLogin.addCenteredTextEntry(140, 10 + var6, 200, 320, 40, 4, false, false);

				var6 += 47;
				this.panelLogin.addButtonBackground(190, var6, 200, 40);
				this.panelLogin.addCenteredText(190, var6 - 10, "Password:", 4, false);
				this.controlLoginPass = this.panelLogin.addCenteredTextEntry(190, 10 + var6, 200, 20, 40, 4, true, false);
				if(Config.isAndroid()) {
					String cred = clientPort.loadCredentials();
					if(cred != null) {
						if(cred.length() > 0) {
							String[] split = cred.split(",");
							if(split.length == 2) {
								String user = split[0];
								String pass = split[1];
								this.panelLogin.setText(this.controlLoginUser, user);
								this.panelLogin.setText(this.controlLoginPass, pass);
							}
						}
					}
				}

				var6 -= 55;
				this.panelLogin.addButtonBackground(410, var6, 120, 25);
				this.panelLogin.addCenteredText(410, var6, "Ok", 4, false);
				this.m_be = this.panelLogin.addButton(410, var6, 120, 25);
				var6 += 30;
				this.panelLogin.addButtonBackground(410, var6, 120, 25);
				this.panelLogin.addCenteredText(410, var6, "Cancel", 4, false);
				this.m_Xi = this.panelLogin.addButton(410, var6, 120, 25);
				this.panelLogin.setFocus(this.controlLoginUser);
				var6 += 30;

				if(Config.isAndroid()) {
					this.panelLogin.addButtonBackground(410, var6, 120, 25);
					this.panelLogin.addCenteredText(410, var6, "Remember", 4, false);
					this.rememberButtonIdx = this.panelLogin.addButton(410, var6, 120, 25);
					var6 += 30;
				}

				/* Registration setup */

				int i = 60;
				byte byte0 = 110;
				menuNewUser = new Panel(getSurface(), 50);
				if(Config.isAndroid()) {
					menuNewUser.addCenteredText(250, i - 42, "@whi@To open keyboard press the back button", 5 ,false);
				}
				menuNewUser.addCenteredText(250, i - 42 + 22, "@whi@Enter a username between 2 and 12 characters long", 1, false);
				menuNewUser.addCenteredText(250, i - 42 + 33, "@red@(Only regular letters, numbers and spaces are allowed)", 0, false);
				menuNewUser.addButtonBackground(250, i + 17, 420, 34);
				menuNewUser.addCenteredText(250, i + 8, "Choose a Username (This is the name other users will see)", 4,
						false);
				menuNewUserUsername = menuNewUser.addCenteredTextEntry(256, i + 25, 200, 12, 40, 4, false, false);
				i += 62;
				menuNewUser.addCenteredText(250, i - 19, "@whi@Password must be at least between 4 and 16 characters long", 1, false);
				menuNewUser.addCenteredText(250, i - 19 + 11, "@red@(DO NOT use the same password that you use elsewhere. Regular letters and numbers only)", 0, false);

				menuNewUser.addButtonBackground(250, i + 17, 420, 34);
				menuNewUser.addCenteredText(250, i + 8, "Choose a Password (You will require this to login)", 4, false);
				menuNewUserPassword = menuNewUser.addCenteredTextEntry(256, i + 25, 200, 12, 40, 4, true, false);
				i += 54;

				menuNewUser.addCenteredText(250, i - 11, "@whi@It's recommended to use a valid email adress", 1, false);
				menuNewUser.addButtonBackground(250, i + 17, 420, 34);
				menuNewUser.addCenteredText(250, i + 8, "E-mail address", 4, false);
				menuNewUserEmail = menuNewUser.addCenteredTextEntry(256, i + 25, 200, 40, 40, 4, false, false);
				i += 40;

				// menuNewUser.addButtonBackground(250, i + 22, 420, 44);

				menuNewUser.addButtonBackground(((250 - byte0) + 50) - 15, i + 17, 270, 34);
				menuNewUserStatus = menuNewUser.addCenteredText(((250 - byte0) + 50) - 15, i + 8,
						"To create an account please enter", 4, true);
				menuNewUserStatus2 = menuNewUser.addCenteredText(((250 - byte0) + 50) - 15, i + 25,
						"all the requested details", 4, true);

				menuNewUser.addButtonBackground(350, i + 17, 70, 34);
				menuNewUser.addCenteredText(350, i + 17, "Submit", 5, false);
				menuNewUserSubmit = menuNewUser.addButton(335, i + 17, 100, 34);

				menuNewUser.addButtonBackground(425, i + 17, 70, 34);
				menuNewUser.addCenteredText(425, i + 17, "Cancel", 5, false);
				menuNewUserCancel = menuNewUser.addButton(425, i + 17, 100, 34);

			} catch (RuntimeException var4) {
				throw GenUtil.makeThrowable(var4, "client.B(" + var1 + ')');
			}
		}

		private final void createMessageTabPanel(int var1) {
			try {

				this.panelMessageTabs = new Panel(this.getSurface(), 10);
				this.panelMessageChat = this.panelMessageTabs.addScrollingList2(5, 269, 502, var1, 20, 1, true);
				this.panelMessageEntry = this.panelMessageTabs.addLeftTextEntry(7, 324, 498, 14, 1, 80, false, true);
				this.panelMessageQuest = this.panelMessageTabs.addScrollingList2(5, 269, 502, 56, 20, 1, true);
				this.panelMessagePrivate = this.panelMessageTabs.addScrollingList2(5, 269, 502, 56, 20, 1, true);
				this.panelMessageClan = this.panelMessageTabs.addScrollingList2(5, 269, 502, 56, 20, 1, true);
				this.panelMessageTabs.setFocus(this.panelMessageEntry);
			} catch (RuntimeException var3) {
				throw GenUtil.makeThrowable(var3, "client.JC(" + var1 + ')');
			}
		}

		private final RSCharacter createNpc(int sprite, int type, int x, int y, int serverIndex) {
			try {
				if (null == this.npcsServer[serverIndex]) {
					this.npcsServer[serverIndex] = new RSCharacter();
					this.npcsServer[serverIndex].serverIndex = serverIndex;
				}


				RSCharacter character = this.npcsServer[serverIndex];
				boolean var8 = false;

				int waypointIdx;
				for (waypointIdx = 0; waypointIdx < this.npcCacheCount; ++waypointIdx) {
					if (this.npcsCache[waypointIdx].serverIndex == serverIndex) {
						var8 = true;
						break;
					}
				}

				if (var8) {
					character.animationNext = sprite;
					character.npcId = type;
					waypointIdx = character.waypointCurrent;
					if (x != character.waypointsX[waypointIdx] || y != character.waypointsZ[waypointIdx]) {
						character.waypointCurrent = waypointIdx = (1 + waypointIdx) % 10;
						character.waypointsX[waypointIdx] = x;
						character.waypointsZ[waypointIdx] = y;
					}
				} else {
					character.serverIndex = serverIndex;
					character.waypointCurrent = 0;
					character.movingStep = 0;
					character.waypointsX[0] = character.currentX = x;
					character.direction = RSCharacterDirection.lookup(sprite);
					character.animationNext = character.direction.rsDir;
					character.stepFrame = 0;
					character.npcId = type;
					character.waypointsZ[0] = character.currentZ = y;
				}

				this.npcs[this.npcCount++] = character;
				return character;
			} catch (RuntimeException var10) {
				throw GenUtil.makeThrowable(var10,
						"client.U(" + sprite + ',' + type + ',' + x + ',' + y + ',' + serverIndex + ')');
			}
		}

		private final void createPacket64(int v1, int v2, int v3, int v4) {
			try {
				this.getClientStream().newPacket(64);

				this.getClientStream().writeBuffer1.putByte(v1);
				this.getClientStream().writeBuffer1.putByte(v2);
				this.getClientStream().writeBuffer1.putByte(v3);
				this.getClientStream().writeBuffer1.putByte(v4);
				this.getClientStream().finishPacket();
			} catch (RuntimeException var7) {
				throw GenUtil.makeThrowable(var7, "client.L(" + v3 + ',' + v2 + ',' + v1 + ',' + "dummy" + ',' + v4 + ')');
			}
		}

		private final RSCharacter createPlayer(int var1, int serverIndex, int var3, int var4, RSCharacterDirection var5) {
			try {
				if (null == this.playerServer[serverIndex]) {
					this.playerServer[serverIndex] = new RSCharacter();
					this.playerServer[serverIndex].serverIndex = serverIndex;
				}


				RSCharacter c = this.playerServer[serverIndex];
				boolean var7 = false;

				int var8;
				for (var8 = 0; var8 < this.knownPlayerCount; ++var8) {
					if (this.knownPlayers[var8].serverIndex == serverIndex) {
						var7 = true;
						break;
					}
				}

				if (var7) {
					c.animationNext = var5.rsDir;
					var8 = c.waypointCurrent;
					if (var3 != c.waypointsX[var8] || c.waypointsZ[var8] != var1) {
						c.waypointCurrent = var8 = (1 + var8) % 10;
						c.waypointsX[var8] = var3;
						c.waypointsZ[var8] = var1;
					}
				} else {
					c.serverIndex = serverIndex;
					c.waypointsX[0] = c.currentX = var3;
					c.waypointCurrent = 0;
					c.movingStep = 0;
					c.stepFrame = 0;
					c.direction = var5;
					c.animationNext = var5.rsDir;
					c.waypointsZ[0] = c.currentZ = var1;
				}

				this.players[this.playerCount++] = c;
				return c;
			} catch (RuntimeException var9) {
				throw GenUtil.makeThrowable(var9,
						"client.P(" + var1 + ',' + serverIndex + ',' + var3 + ',' + var4 + ',' + var5 + ')');
			}
		}

		private final void createTopMouseMenu(int var1) {
			try {

				if (this.selectedSpell >= 0 || this.selectedItemInventoryIndex >= 0) {
					this.menuCommon.addItem(MenuItemAction.CANCEL, "", "Cancel");
				}

				this.menuCommon.sort();
				int var2 = this.menuCommon.getItemCount(-27153);
				if (var1 < -120) {
					int var3;
					// Limit menu items to 20
					for (var3 = var2; var3 > 20; --var3) {
						this.menuCommon.removeItem((int) (var3 - 1));
					}

					int var4;
					int var5;
					if (this.showUiTab == 5) {
						String var9 = null;
						if (this.panelSocialTab == 0 && this.m_wk != -1) {
							if (this.m_wk >= 0) {
								String var10 = "";
								var5 = this.m_wk;
								if ((4 & SocialLists.friendListArg[var5]) == 0) {
									var9 = SocialLists.friendList[var5];
									var10 = " is offline";
								} else {
									var9 = "Click to message " + SocialLists.friendList[var5];
									if (SocialLists.friendListArgS[var5] != null) {
										var10 = " on " + SocialLists.friendListArgS[var5];
									}
								}

								if (SocialLists.friendListOld[var5] != null
										&& SocialLists.friendListOld[var5].length() > 0) {
									var9 = var9 + " (formerly " + SocialLists.friendListOld[var5] + ")" + var10;
								} else {
									var9 = var9 + var10;
								}
							} else {
								var4 = -(2 + this.m_wk);
								var9 = "Click to remove " + SocialLists.friendList[var4];
								if (null != SocialLists.friendListOld[var4]
										&& SocialLists.friendListOld[var4].length() > 0) {
									var9 = var9 + " (formerly " + SocialLists.friendListOld[var4] + ")";
								}
							}
						}

						if (this.panelSocialTab == 2 && this.m_nj != -1) {
							if (this.m_nj >= 0) {
								var4 = this.m_nj;
								var9 = "Ignoring " + SocialLists.ignoreListArg0[var4];
								if (SocialLists.ignoreListArg1[var4] != null
										&& SocialLists.ignoreListArg1[var4].length() > 0) {
									var9 = var9 + " (formerly " + SocialLists.ignoreListArg1[var4] + ")";
								}
							} else {
								var4 = -(2 + this.m_nj);
								var9 = "Click to remove " + SocialLists.ignoreListArg0[var4];
								if (SocialLists.ignoreListArg1[var4] != null
										&& SocialLists.ignoreListArg1[var4].length() > 0) {
									var9 = var9 + " (formerly " + SocialLists.ignoreListArg1[var4] + ")";
								}
							}
						}

						if (var9 != null) {
							this.getSurface().drawString(var9, 6, 14, 16776960, 1);
						}
					}

					var3 = this.menuCommon.getItemCount(-27153);
					if (var3 > 0) {
						var4 = -1;

						for (var5 = 0; var5 < var3; ++var5) {
							String var6 = this.menuCommon.getItemActor((int) var5);
							if (null != var6 && var6.length() > 0) {
								var4 = var5;
								break;
							}
						}

						String var11 = null;
						if ((this.selectedItemInventoryIndex >= 0 || this.selectedSpell >= 0) && var3 == 1) {
							var11 = "Choose a target";
						} else if ((this.selectedItemInventoryIndex >= 0 || this.selectedSpell >= 0) && var3 > 1) {
							var11 = "@whi@" + this.menuCommon.getItemLabel((int) 0) + " "
									+ this.menuCommon.getItemActor((int) 0);
						} else if (var4 != -1) {
							var11 = this.menuCommon.getItemActor((int) var4) + ": @whi@"
									+ this.menuCommon.getItemLabel((int) 0);
						}

						if (var3 == 2 && null != var11) {
							var11 = var11 + "@whi@ / 1 more option";
						}

						if (var3 > 2 && var11 != null) {
							var11 = var11 + "@whi@ / " + (var3 - 1) + " more options";
						}

						if (null != var11) {
							this.getSurface().drawString(var11, 6, 14, 16776960, 1);
						}

						if (!this.optionMouseButtonOne && this.mouseButtonClick == 1
								|| this.optionMouseButtonOne && this.mouseButtonClick == 1 && var3 == 1) {
							if (this.controlPressed && this.shiftPressed && this.menuVisible) {
								this.getClientStream().newPacket(59);
								this.getClientStream().writeBuffer1.putShort(this.m_rf);
								this.getClientStream().writeBuffer1.putShort(this.m_Cg);
								this.getClientStream().finishPacket();
							} else {
								this.handleMenuItemClicked(false, (int) 0);
							}

							this.mouseButtonClick = 0;
						} else if (!this.optionMouseButtonOne && this.mouseButtonClick == 2
								|| this.optionMouseButtonOne && this.mouseButtonClick == 1) {
							int menuWidth = this.menuCommon.getWidth();
							int var7 = this.menuCommon.getHeight();
							this.menuX = this.mouseX - menuWidth / 2;
							this.topMouseMenuVisible = true;
							this.menuY = this.mouseY - 7;
							if (this.menuX < 0) {
								this.menuX = 0;
							}

							if (this.menuY < 0) {
								this.menuY = 0;
							}

							this.mouseButtonClick = 0;
							if (this.menuY + var7 > getGameHeight() - 19) {
								this.menuY = getGameHeight() - 19 - var7;
							}

							if (menuWidth + this.menuX > getGameWidth() - 2) {
								this.menuX = getGameWidth() - 2 - menuWidth;
							}
						}
					}

				}
			} catch (RuntimeException var8) {
				throw GenUtil.makeThrowable(var8, "client.H(" + var1 + ')');
			}
		}

		private final RSModel createWallObjectModel(int x, int y, int type, int dir, int index) {
			// this.createWallObjectModel(true, y, id, x, direction,
			// this.wallObjectInstanceCount);
			int x1 = x;
			int x2 = x;
			int y2 = y;
			int texFront = EntityHandler.getDoorDef(type).getModelVar2();
			int texBack = EntityHandler.getDoorDef(type).getModelVar3();
			int height = EntityHandler.getDoorDef(type).getWallObjectHeight();
			RSModel model = new RSModel(4, 1);
			if (dir == 1) {
				y2 = 1 + y;
			}

			if (dir == 0) {
				x2 = x + 1;
			}

			if (dir == 2) {
				y2 = 1 + y;
				x1 = 1 + x;
			}

			x1 *= this.tileSize;
			if (dir == 3) {
				y2 = 1 + y;
				x2 = x + 1;
			}

			int z1 = y * this.tileSize;
			x2 *= this.tileSize;
			y2 *= this.tileSize;
			int v1 = model.insertVertex(x1, -this.world.getElevation(x1, z1), z1);
			int v2 = model.insertVertex(x1, -this.world.getElevation(x1, z1) - height, z1);

			int v3 = model.insertVertex(x2, -height - this.world.getElevation(x2, y2), y2);
			int v4 = model.insertVertex(x2, -this.world.getElevation(x2, y2), y2);
			int[] indices = new int[] { v1, v2, v3, v4 };
			model.insertFace(4, indices, texFront, texBack, false);
			model.setDiffuseLightAndColor(-50, -10, -50, 60, 24, false, -95);
			if (x >= 0 && y >= 0 && x < 96 && y < 96) {
				this.scene.addModel(model);
			}

			model.key = index + 10000;
			return model;
		}

		final void draw() {
			try {

				if (this.rendering) {
					this.fetchContainerSize();
					this.rendering = false;
				}

				if (!this.errorLoadingData) {
					if (this.errorLoadingMemory) {
						clientPort.drawOutOfMemoryError();
						this.setFPS((int) 1, (byte) 106);
					} else {
						try {
							// if (var1) {
							// this.errorLoadingCoadebase = false;
							// }

							if (null == this.getSurface()) {
								return;
							}

							if (this.currentViewMode == GameMode.LOGIN) {
								this.getSurface().loggedIn = false;
								this.drawLogin();
							}

							if (this.currentViewMode == GameMode.GAME) {
								this.getSurface().loggedIn = true;
								this.drawGame((int) 13);
							}
						} catch (OutOfMemoryError var4) {
							var4.printStackTrace();
							this.errorLoadingMemory = true;
						}

					}
				} else {
					clientPort.drawLoadingError();
					this.setFPS((int) 1, (byte) 106);
				}
			} catch (RuntimeException var5) {
				throw GenUtil.makeThrowable(var5, "client.JD()");
			}
		}

		private final void drawAppearancePanelCharacterSprites(int var1) {
			try {

				this.getSurface().interlace = false;
				this.getSurface().blackScreen(true);
				this.panelAppearance.drawPanel();
				short var2 = 140;
				int var5 = var2 + 116;
				byte var3 = 50;
				int y = var3 - 25;

				// pants
				this.getSurface().spriteClip3(var5 - 87, this.getPlayerClothingColors()[this.characterBottomColour],
						EntityHandler.getAnimationDef(this.character2Colour).getNumber(), y, 102, (byte) 105, 64);

				// body
				this.getSurface().drawSpriteClipping(EntityHandler.getAnimationDef(m_dk).getNumber(), var5 - 32 - 55, y, 64,
						102, this.getPlayerClothingColors()[this.m_Wg], this.getPlayerSkinColors()[this.m_hh], false, 0, 1);

				this.getSurface().drawSpriteClipping(EntityHandler.getAnimationDef(appearanceHeadType).getNumber(),
						var5 - 32 - 55, y, 64, 102, this.getPlayerHairColors()[this.m_ld],
						this.getPlayerSkinColors()[this.m_hh], false, 0, var1 + 13760);

				this.getSurface().spriteClip3(var5 - 32, this.getPlayerClothingColors()[this.characterBottomColour],
						6 + EntityHandler.getAnimationDef(character2Colour).getNumber(), y, 102, (byte) 105, 64);

				this.getSurface().drawSpriteClipping(EntityHandler.getAnimationDef(this.m_dk).getNumber() + 6, var5 - 32, y,
						64, 102, this.getPlayerClothingColors()[this.m_Wg], this.getPlayerSkinColors()[this.m_hh], false, 0,
						1);

				this.getSurface().drawSpriteClipping(6 + EntityHandler.getAnimationDef(this.appearanceHeadType).getNumber(),
						var5 - 32, y, 64, 102, this.getPlayerHairColors()[this.m_ld], this.getPlayerSkinColors()[this.m_hh],
						false, 0, 1);

				this.getSurface().spriteClip3(var5 + 55 - 32, this.getPlayerClothingColors()[this.characterBottomColour],
						12 + EntityHandler.getAnimationDef(this.character2Colour).getNumber(), y, 102, (byte) 110, 64);

				this.getSurface().drawSpriteClipping(EntityHandler.getAnimationDef(this.m_dk).getNumber() + 12,
						55 + (var5 - 32), y, 64, 102, this.getPlayerClothingColors()[this.m_Wg],
						this.getPlayerSkinColors()[this.m_hh], false, 0, var1 + 13760);

				this.getSurface().drawSpriteClipping(
						EntityHandler.getAnimationDef(this.appearanceHeadType).getNumber() + 12, var5 + 55 - 32, y, 64, 102,
						this.getPlayerHairColors()[this.m_ld], this.getPlayerSkinColors()[this.m_hh], false, 0, 1);
				this.getSurface().drawSprite(22 + mudclient.spriteMedia, 0, this.getGameHeight());
				// this.getSurface().draw(this.graphics, this.screenOffsetX, 256,
				// this.screenOffsetY);
				clientPort.draw();
				if (var1 != -13759) {
					this.drawCharacterOverlay();
				}

			} catch (RuntimeException var4) {
				throw GenUtil.makeThrowable(var4, "client.GD(" + var1 + ')');
			}
		}

		private final void drawCharacterOverlay() {
			try {
				for (int i = 0; this.characterDialogCount > i; ++i) {
					int minLineHeight = this.getSurface().fontHeight(1);
					int x = this.characterDialogX[i];
					int y = this.characterDialogY[i];
					int halfWidth = this.characterDialogHalfWidth[i];
					int height = this.characterDialogHeight[i];
					boolean collided = true;

					while (collided) {
						collided = false;
						for (int j = 0; j < i; ++j) {
							if (this.characterDialogY[j] - minLineHeight < y + height
									&& y - minLineHeight < this.characterDialogY[j] + this.characterDialogHeight[j]
											&& this.characterDialogX[j] + this.characterDialogHalfWidth[j] > x - halfWidth
											&& halfWidth + x > this.characterDialogX[j] - this.characterDialogHalfWidth[j]
													&& this.characterDialogY[j] - height - minLineHeight < y) {
								y = this.characterDialogY[j] - (minLineHeight + height);
								collided = true;
							}
						}
					}

					this.characterDialogY[i] = y;
					this.getSurface().drawWrappedCenteredString(this.characterDialogString[i], x, y, 300, 1, 16776960,
							false);
				}


				for (int i = 0; this.characterBubbleCount > i; ++i) {
					int centerX = this.characterBubbleX[i];
					int bubbleY = this.characterBubbleY[i];
					int scale = this.characterBubbleScale[i];
					int id = this.characterBubbleID[i];
					int var7 = scale * 39 / 100;
					int offsetY = scale * 27 / 100;
					int centerY = bubbleY - offsetY;
					this.getSurface().spriteClipping(mudclient.spriteMedia + 9, (byte) -122, offsetY, centerX - var7 / 2,
							var7, centerY, 85);
					int width = scale * 36 / 100;
					int height = scale * 24 / 100;
					this.getSurface().drawSpriteClipping(EntityHandler.getItemDef(id).getSprite() + mudclient.spriteItem,
							centerX - width / 2, centerY - (height / 2) + offsetY / 2, width, height,
							EntityHandler.getItemDef(id).getPictureMask(), 0, false, 0, 1);
				}

				for (int i = 0; i < this.characterHealthCount; ++i) {
					int x = this.characterHealthX[i];
					int y = this.characterHealthY[i];
					int percent = this.characterHealthBar[i];
					this.getSurface().drawBoxAlpha(x - 15, y - 3, percent, 5, 0x00FF00, 192);
					this.getSurface().drawBoxAlpha(percent - 15 + x, y - 3, 30 - percent, 5, 0xFF0000, 192);
				}

			} catch (RuntimeException var12) {
				throw GenUtil.makeThrowable(var12, "client.VA(" + "dummy" + ')');
			}
		}

		private final void drawChatMessageTabs(int var1) {
			try {


				this.getSurface().drawSpriteClipping(spriteMedia + 22, 0, getGameHeight(), getGameWidth(), 10, 0, 0, false,
						0, 1);
				this.getSurface().drawSprite(mudclient.spriteMedia + 23, (getGameWidth() / 2) - 256,
						this.getGameHeight() - 4);

				if (var1 == 5) {
					int color = GenUtil.buildColor(200, 200, 255);
					if (this.messageTabSelected == MessageTab.ALL) {
						color = GenUtil.buildColor(255, 200, 50);
					}
					if (this.messageTabActivity_Game % 30 > 15) {
						color = GenUtil.buildColor(255, 50, 50);
					}
					this.getSurface().drawColoredStringCentered((getGameWidth() / 2) - 200, "All messages", color, 0, 0,
							6 + this.getGameHeight());

					color = GenUtil.buildColor(200, 200, 255);
					if (this.messageTabSelected == MessageTab.CHAT) {
						color = GenUtil.buildColor(255, 200, 50);
					}
					if (this.messageTabActivity_Chat % 30 > 15) {
						color = GenUtil.buildColor(255, 50, 50);
					}
					this.getSurface().drawColoredStringCentered((getGameWidth() / 2) - 100, "Chat history", color, 0, 0,
							this.getGameHeight() + 6);

					color = GenUtil.buildColor(200, 200, 255);
					if (this.messageTabSelected == MessageTab.QUEST) {
						color = GenUtil.buildColor(255, 200, 50);
					}
					if (this.messageTabActivity_Quest % 30 > 15) {
						color = GenUtil.buildColor(255, 50, 50);
					}
					this.getSurface().drawColoredStringCentered((getGameWidth() / 2), "Quest history", color, 0, 0,
							6 + this.getGameHeight());

					color = GenUtil.buildColor(200, 200, 255);
					if (this.messageTabSelected == MessageTab.PRIVATE) {
						color = GenUtil.buildColor(255, 200, 50);
					}
					if (this.messageTabActivity_Private % 30 > 15) {
						color = GenUtil.buildColor(255, 50, 50);
					}
					this.getSurface().drawColoredStringCentered((getGameWidth() / 2) + 100, "Private history", color, 0, 0,
							this.getGameHeight() + 6);
					if (Config.WANT_CLANS) {
						color = GenUtil.buildColor(200, 200, 255);
						if (this.messageTabSelected == MessageTab.CLAN) {
							color = GenUtil.buildColor(255, 200, 50);
						}
						if (this.messageTabActivity_Clan % 30 > 15) {
							color = GenUtil.buildColor(255, 50, 50);
						}
						this.getSurface().drawColoredStringCentered((getGameWidth() / 2) + 200, "Clan history", color, 0, 0,
								6 + this.getGameHeight());
					}
				}
			} catch (RuntimeException var3) {
				throw GenUtil.makeThrowable(var3, "client.QB(" + var1 + ')');
			}
		}

		private final void drawDialogCombatStyle() {
			try {

				byte sx = 7;
				byte sy = 15;
				short width;
				if(Config.isAndroid()) {
					width = 140;
				} else {
					width = 175;
				}
				if (this.mouseButtonClick != 0) {
					for (int row = 1; row < 5; ++row) {
						if (row > 0 && sx < this.mouseX && this.mouseX < width + sx && this.mouseY > row * 20 + sy
								&& row * 20 + sy + 20 > this.mouseY) {
							this.mouseButtonClick = 0;
							this.combatStyle = row - 1;
							this.getClientStream().newPacket(29);
							this.getClientStream().writeBuffer1.putByte(this.combatStyle);
							this.getClientStream().finishPacket();
							break;
						}
					}
				}

				for (int row = 0; row < 5; ++row) {
					if (1 + this.combatStyle == row) {
						this.getSurface().drawBoxAlpha(sx, sy + row * 20, width, 20, GenUtil.buildColor(255, 0, 0), 128);
					} else {
						this.getSurface().drawBoxAlpha(sx, sy + row * 20, width, 20, GenUtil.buildColor(190, 190, 190),
								128);
					}

					this.getSurface().drawLineHoriz(sx, sy + row * 20, width, 0);
					this.getSurface().drawLineHoriz(sx, 20 + sy + row * 20, width, 0);
				}

				this.getSurface().drawColoredStringCentered(width / 2 + sx, (Config.isAndroid() ? "C" : "Select c") + "ombat style", 16777215, 0, 3, 16 + sy);
				this.getSurface().drawColoredStringCentered(width / 2 + sx, "Controlled (+1 " + (Config.isAndroid() ? "all" : "of each") + ")", 0, 0, 3, sy + 36);
				this.getSurface().drawColoredStringCentered(width / 2 + sx, "Aggressive (+3 " + (Config.isAndroid() ? "str" : "strength") + ")", 0, 0, 3, 56 + sy);
				this.getSurface().drawColoredStringCentered(width / 2 + sx, "Accurate   (+3 " + (Config.isAndroid() ? "att" : "attack") + ")", 0, 0, 3, sy + 76);
				this.getSurface().drawColoredStringCentered(width / 2 + sx, "Defensive  (+3 " + (Config.isAndroid() ? "def" : "defense") + ")", 0, 0, 3, sy + 96);
			} catch (RuntimeException var7) {
				throw GenUtil.makeThrowable(var7, "client.TB(" + "dummy" + ')');
			}
		}

		private final void drawDialogDuel() {
			try {

				int clickedIndex = -1;
				if (this.mouseButtonClick != 0 && this.menuDuel_Visible) {
					clickedIndex = this.menuDuel.handleClick(this.mouseX, this.menuDuelX, this.menuDuelY, this.mouseY);
				}

				if (clickedIndex >= 0) {
					this.mouseButtonClick = 0;
					this.menuDuel_Visible = false;
					MenuItemAction act = this.menuDuel.getItemAction((int) clickedIndex);
					int itemID = this.menuDuel.getItemIndexOrX(clickedIndex);
					int firstItemIndex = -1;
					int itemCount = 0;
					if (act != MenuItemAction.DUEL_STAKE) {
						for (int duelIdx = 0; this.duelOfferItemCount > duelIdx; ++duelIdx) {
							if (this.duelOfferItemID[duelIdx] == itemID) {
								if (firstItemIndex < 0) {
									firstItemIndex = duelIdx;
								}

								if (EntityHandler.getItemDef(itemID).isStackable()) {
									itemCount = this.duelOfferItemSize[duelIdx];
									break;
								}

								++itemCount;
							}
						}
					} else {
						for (int invIdx = 0; this.inventoryItemCount > invIdx; ++invIdx) {
							if (itemID == this.inventoryItemID[invIdx]) {
								if (firstItemIndex < 0) {
									firstItemIndex = invIdx;
								}

								if (EntityHandler.getItemDef(itemID).isStackable()) {
									itemCount = this.inventoryItemSize[invIdx];
									break;
								}

								++itemCount;
							}
						}
					}

					if (firstItemIndex >= 0) {
						int doCount = this.menuDuel.getItemIdOrZ(clickedIndex);
						if (doCount != -2) {
							if (doCount == -1) {
								doCount = itemCount;
							}

							if (act == MenuItemAction.DUEL_STAKE) {
								this.duelStakeItem(doCount, firstItemIndex);
							} else {
								this.duelRemoveItem(firstItemIndex, doCount);
							}
						} else {
							this.duelDoX_Slot = firstItemIndex;
							if (act == MenuItemAction.DUEL_STAKE) {
								this.showItemModX(InputXPrompt.duelStakeX, InputXAction.DUEL_STAKE, true);
							} else {
								this.showItemModX(InputXPrompt.duelRemoveX, InputXAction.DUEL_REMOVE, true);
							}
						}
					}
				} else if (this.inputX_Action == InputXAction.ACT_0) {
					if (this.mouseButtonClick == 1 && this.mouseButtonItemCountIncrement == 0) {
						this.mouseButtonItemCountIncrement = 1;
					}

					int mouseX_Local = this.mouseX - 22;
					int mouseY_Local = this.mouseY - 36;
					if (mouseX_Local >= 0 && mouseY_Local >= 0 && mouseX_Local < 468 && mouseY_Local < 262) {
						if (this.mouseButtonItemCountIncrement > 0) {
							if (mouseX_Local > 216 && mouseY_Local > 30 && mouseX_Local < 462 && mouseY_Local < 235) {
								int slot = (mouseX_Local - 217) / 49 + (mouseY_Local - 31) / 34 * 5;
								if (slot >= 0 && this.inventoryItemCount > slot) {
									this.duelStakeItem((int) -1, slot);
								}
							}

							if (mouseX_Local > 8 && mouseY_Local > 30 && mouseX_Local < 205 && mouseY_Local < 129) {
								int slot = (mouseX_Local - 9) / 49 + (mouseY_Local - 31) / 34 * 4;
								if (slot >= 0 && slot < this.duelOfferItemCount) {
									this.duelRemoveItem((int) slot, (int) -1);
								}
							}

							boolean settingsChanged = false;
							if (mouseX_Local >= 93 && mouseY_Local >= 221 && mouseX_Local <= 104 && mouseY_Local <= 232) {
								settingsChanged = true;
								this.duelSettingsRetreat = !this.duelSettingsRetreat;
							}

							if (mouseX_Local >= 93 && mouseY_Local >= 240 && mouseX_Local <= 104 && mouseY_Local <= 251) {
								this.duelSettingsMagic = !this.duelSettingsMagic;
								settingsChanged = true;
							}

							if (mouseX_Local >= 191 && mouseY_Local >= 221 && mouseX_Local <= 202 && mouseY_Local <= 232) {
								this.duelSettingsPrayer = !this.duelSettingsPrayer;
								settingsChanged = true;
							}

							if (mouseX_Local >= 191 && mouseY_Local >= 240 && mouseX_Local <= 202 && mouseY_Local <= 251) {
								settingsChanged = true;
								this.duelSettingsWeapons = !this.duelSettingsWeapons;
							}

							if (settingsChanged) {
								this.getClientStream().newPacket(8);
								this.getClientStream().writeBuffer1.putByte(!this.duelSettingsRetreat ? 0 : 1);
								this.getClientStream().writeBuffer1.putByte(this.duelSettingsMagic ? 1 : 0);
								this.getClientStream().writeBuffer1.putByte(!this.duelSettingsPrayer ? 0 : 1);
								this.getClientStream().writeBuffer1.putByte(!this.duelSettingsWeapons ? 0 : 1);
								this.getClientStream().finishPacket();
								this.duelOffsetOpponentAccepted = false;
								this.duelOfferAccepted = false;
							}

							if (mouseX_Local >= 217 && mouseY_Local >= 238 && mouseX_Local <= 286 && mouseY_Local <= 259) {
								this.duelOfferAccepted = true;
								this.getClientStream().newPacket(176);
								this.getClientStream().finishPacket();
							}

							if (mouseX_Local >= 394 && mouseY_Local >= 238 && mouseX_Local < 463 && mouseY_Local < 259) {
								this.showDialogDuel = false;
								this.getClientStream().newPacket(197);
								this.getClientStream().finishPacket();
							}

							this.mouseButtonItemCountIncrement = 0;
							this.mouseButtonClick = 0;
						}

						if (this.mouseButtonClick == 2) {
							if (mouseX_Local > 216 && mouseY_Local > 30 && mouseX_Local < 462 && mouseY_Local < 235) {
								int w = this.menuCommon.getWidth();
								int h = this.menuCommon.getHeight();
								this.menuX = this.mouseX - w / 2;
								this.menuY = this.mouseY - 7;
								this.topMouseMenuVisible = true;
								if (this.menuY < 0) {
									this.menuY = 0;
								}

								if (this.menuX < 0) {
									this.menuX = 0;
								}

								if (w + this.menuX > 510) {
									this.menuX = 510 - w;
								}

								if (h + this.menuY > 315) {
									this.menuY = 315 - h;
								}

								int invIndex = (mouseX_Local - 217) / 49 + (mouseY_Local - 31) / 34 * 5;
								if (invIndex >= 0 && this.inventoryItemCount > invIndex) {
									int itemID = this.inventoryItemID[invIndex];
									this.menuDuel_Visible = true;
									this.menuDuel.recalculateSize(0);
									this.menuDuel.addCharacterItem_WithID(itemID,
											"@lre@" + EntityHandler.getItemDef(itemID).getName(), MenuItemAction.DUEL_STAKE,
											"Stake 1", 1);
									this.menuDuel.addCharacterItem_WithID(itemID,
											"@lre@" + EntityHandler.getItemDef(itemID).getName(), MenuItemAction.DUEL_STAKE,
											"Stake 5", 5);
									this.menuDuel.addCharacterItem_WithID(itemID,
											"@lre@" + EntityHandler.getItemDef(itemID).getName(), MenuItemAction.DUEL_STAKE,
											"Stake 10", 10);
									this.menuDuel.addCharacterItem_WithID(itemID,
											"@lre@" + EntityHandler.getItemDef(itemID).getName(), MenuItemAction.DUEL_STAKE,
											"Stake All", -1);
									this.menuDuel.addCharacterItem_WithID(itemID,
											"@lre@" + EntityHandler.getItemDef(itemID).getName(), MenuItemAction.DUEL_STAKE,
											"Stake X", -2);
									int width = this.menuDuel.getWidth();
									int height = this.menuDuel.getHeight();
									this.menuDuelY = this.mouseY - 7;
									this.menuDuelX = this.mouseX - width / 2;
									if (this.menuDuelX < 0) {
										this.menuDuelX = 0;
									}

									if (this.menuDuelY < 0) {
										this.menuDuelY = 0;
									}

									if (this.menuDuelX + width > 510) {
										this.menuDuelX = 510 - width;
									}

									if (this.menuDuelY + height > 315) {
										this.menuDuelY = 315 - height;
									}
								}
							}

							if (mouseX_Local > 8 && mouseY_Local > 30 && mouseX_Local < 205 && mouseY_Local < 133) {
								int slot = (mouseX_Local - 9) / 49 + (mouseY_Local - 31) / 34 * 4;
								if (slot >= 0 && this.duelOfferItemCount > slot) {
									int id = this.duelOfferItemID[slot];
									this.menuDuel_Visible = true;
									this.menuDuel.recalculateSize(0);
									this.menuDuel.addCharacterItem_WithID(id,
											"@lre@" + EntityHandler.getItemDef(id).getName(), MenuItemAction.DUEL_REMOVE,
											"Remove 1", 1);
									this.menuDuel.addCharacterItem_WithID(id,
											"@lre@" + EntityHandler.getItemDef(id).getName(), MenuItemAction.DUEL_REMOVE,
											"Remove 5", 5);
									this.menuDuel.addCharacterItem_WithID(id,
											"@lre@" + EntityHandler.getItemDef(id).getName(), MenuItemAction.DUEL_REMOVE,
											"Remove 10", 10);
									this.menuDuel.addCharacterItem_WithID(id,
											"@lre@" + EntityHandler.getItemDef(id).getName(), MenuItemAction.DUEL_REMOVE,
											"Remove All", -1);
									this.menuDuel.addCharacterItem_WithID(id,
											"@lre@" + EntityHandler.getItemDef(id).getName(), MenuItemAction.DUEL_REMOVE,
											"Remove X", -2);
									int w = this.menuDuel.getWidth();
									int h = this.menuDuel.getHeight();
									this.menuDuelY = this.mouseY - 7;
									this.menuDuelX = this.mouseX - w / 2;
									if (this.menuDuelX < 0) {
										this.menuDuelX = 0;
									}

									if (this.menuDuelY < 0) {
										this.menuDuelY = 0;
									}

									if (this.menuDuelX + w > 510) {
										this.menuDuelX = 510 - w;
									}

									if (h + this.menuDuelY > 315) {
										this.menuDuelY = 315 - h;
									}
								}
							}

							this.mouseButtonClick = 0;
						}

						if (this.menuDuel_Visible) {
							int w = this.menuDuel.getWidth();
							int h = this.menuDuel.getHeight();
							if (this.menuDuelX - 10 > this.mouseX || this.mouseY < this.menuDuelY - 10
									|| this.mouseX > this.menuDuelX + w + 10 || this.mouseY > 10 + h + this.menuDuelY) {
								this.menuDuel_Visible = false;
							}
						}
					} else if (this.mouseButtonClick != 0) {
						this.showDialogDuel = false;
						this.getClientStream().newPacket(197);
						this.getClientStream().finishPacket();
					}
				}

				if (this.showDialogDuel) {
					byte xr = 22;
					byte yr = 36;
					this.getSurface().drawBox(xr, yr, 468, 12, 13175581);
					int colorA = 10000536;
					this.getSurface().drawBoxAlpha(xr, 12 + yr, 468, 18, colorA, 160);
					this.getSurface().drawBoxAlpha(xr, 30 + yr, 8, 248, colorA, 160);
					this.getSurface().drawBoxAlpha(xr + 205, 30 + yr, 11, 248, colorA, 160);
					this.getSurface().drawBoxAlpha(xr + 462, 30 + yr, 6, 248, colorA, 160);
					this.getSurface().drawBoxAlpha(xr + 8, yr + 99, 197, 24, colorA, 160);
					this.getSurface().drawBoxAlpha(8 + xr, 192 + yr, 197, 23, colorA, 160);
					this.getSurface().drawBoxAlpha(xr + 8, yr + 258, 197, 20, colorA, 160);
					this.getSurface().drawBoxAlpha(xr + 216, yr + 235, 246, 43, colorA, 160);
					int colorB = 13684944;
					this.getSurface().drawBoxAlpha(8 + xr, yr + 30, 197, 69, colorB, 160);
					this.getSurface().drawBoxAlpha(xr + 8, 123 + yr, 197, 69, colorB, 160);
					this.getSurface().drawBoxAlpha(8 + xr, yr + 215, 197, 43, colorB, 160);
					this.getSurface().drawBoxAlpha(216 + xr, yr + 30, 246, 205, colorB, 160);

					for (int i = 0; i < 3; ++i) {
						this.getSurface().drawLineHoriz(xr + 8, yr + 30 + i * 34, 197, 0);
					}

					for (int i = 0; i < 3; ++i) {
						this.getSurface().drawLineHoriz(8 + xr, i * 34 + yr + 123, 197, 0);
					}

					for (int i = 0; i < 7; ++i) {
						this.getSurface().drawLineHoriz(216 + xr, i * 34 + yr + 30, 246, 0);
					}

					for (int i = 0; i < 6; ++i) {
						if (i < 5) {
							this.getSurface().drawLineVert(i * 49 + 8 + xr, yr + 30, 0, 69);
							this.getSurface().drawLineVert(i * 49 + xr + 8, yr + 123, 0, 69);
						}

						this.getSurface().drawLineVert(i * 49 + xr + 216, yr + 30, 0, 205);
					}

					this.getSurface().drawLineHoriz(xr + 8, 215 + yr, 197, 0);
					this.getSurface().drawLineHoriz(xr + 8, yr + 257, 197, 0);
					this.getSurface().drawLineVert(8 + xr, yr + 215, 0, 43);
					this.getSurface().drawLineVert(xr + 204, yr + 215, 0, 43);
					this.getSurface().drawString("Preparing to duel with: " + this.duelConfirmOpponentName, 1 + xr, yr + 10,
							16777215, 1);
					this.getSurface().drawString("Your Stake", xr + 9, 27 + yr, 16777215, 4);
					this.getSurface().drawString("Opponent\'s Stake", 9 + xr, 120 + yr, 16777215, 4);
					this.getSurface().drawString("Duel Options", xr + 9, yr + 212, 16777215, 4);
					this.getSurface().drawString("Your Inventory", xr + 216, yr + 27, 16777215, 4);
					this.getSurface().drawString("No retreating", 1 + 8 + xr, 215 + yr + 16, 16776960, 3);
					this.getSurface().drawString("No magic", 1 + 8 + xr, 250 + yr, 16776960, 3);
					this.getSurface().drawString("No prayer", 8 + xr + 102, yr + 231, 16776960, 3);
					this.getSurface().drawString("No weapons", 102 + 8 + xr, 35 + yr + 215, 16776960, 3);

					this.getSurface().drawBoxBorder(xr + 93, 11, 215 + yr + 6, 11, 16776960);
					if (this.duelSettingsRetreat) {
						this.getSurface().drawBox(xr + 95, 8 + 215 + yr, 7, 7, 16776960);
					}

					this.getSurface().drawBoxBorder(93 + xr, 11, 25 + yr + 215, 11, 16776960);
					if (this.duelSettingsMagic) {
						this.getSurface().drawBox(xr + 95, 215 + yr + 27, 7, 7, 16776960);
					}

					this.getSurface().drawBoxBorder(191 + xr, 11, 6 + 215 + yr, 11, 16776960);
					if (this.duelSettingsPrayer) {
						this.getSurface().drawBox(xr + 193, 8 + yr + 215, 7, 7, 16776960);
					}
					this.getSurface().drawBoxBorder(xr + 191, 11, yr + 215 + 25, 11, 16776960);
					if (this.duelSettingsWeapons) {
						this.getSurface().drawBox(193 + xr, 215 + yr + 27, 7, 7, 16776960);
					}

					if (!this.duelOfferAccepted) {
						this.getSurface().drawSprite(25 + mudclient.spriteMedia, 217 + xr, yr + 238);
					}

					this.getSurface().drawSprite(26 + mudclient.spriteMedia, xr + 394, yr + 238);
					if (this.duelOffsetOpponentAccepted) {
						this.getSurface().drawColoredStringCentered(xr + 341, "Other player", 16777215, 0, 1, 246 + yr);
						this.getSurface().drawColoredStringCentered(341 + xr, "has accepted", 16777215, 0, 1, 256 + yr);
					}

					if (this.duelOfferAccepted) {
						this.getSurface().drawColoredStringCentered(35 + 217 + xr, "Waiting for", 16777215, 0, 1, yr + 246);
						this.getSurface().drawColoredStringCentered(252 + xr, "other player", 16777215, 0, 1, 256 + yr);
					}

					for (int itm = 0; this.inventoryItemCount > itm; ++itm) {
						int xI = 217 + xr + (itm % 5) * 49;
						int yI = yr + 31 + (itm / 5) * 34;
						this.getSurface().drawSpriteClipping(
								mudclient.spriteItem + EntityHandler.getItemDef(this.inventoryItemID[itm]).getSprite(), xI,
								yI, 48, 32, EntityHandler.getItemDef(this.inventoryItemID[itm]).getPictureMask(), 0, false,
								0, 1);

						ItemDef def = EntityHandler.getItemDef(this.inventoryItemID[itm]);
						if (def.getNotedFormOf() >= 0) {
							ItemDef originalDef = EntityHandler.getItemDef(def.getNotedFormOf());
							getSurface().drawSpriteClipping(mudclient.spriteItem + originalDef.getSprite(), xI + 7, yI + 4,
									33, 23, originalDef.getPictureMask(), 0, false, 0, 1);
						}
						if (EntityHandler.getItemDef(this.inventoryItemID[itm]).isStackable()) {
							this.getSurface().drawString("" + formatStackAmount(this.inventoryItemSize[itm]), xI + 1,
									10 + yI, 16776960, 1);
						}
					}

					for (int itmOffer = 0; this.duelOfferItemCount > itmOffer; ++itmOffer) {
						int xI = xr + 9 + itmOffer % 4 * 49;
						int yI = yr + 31 + itmOffer / 4 * 34;
						this.getSurface().drawSpriteClipping(
								mudclient.spriteItem + EntityHandler.getItemDef(this.duelOfferItemID[itmOffer]).getSprite(),
								xI, yI, 48, 32, EntityHandler.getItemDef(this.duelOfferItemID[itmOffer]).getPictureMask(),
								0, false, 0, 1);

						ItemDef def = EntityHandler.getItemDef(this.duelOfferItemID[itmOffer]);
						if (def.getNotedFormOf() >= 0) {
							ItemDef originalDef = EntityHandler.getItemDef(def.getNotedFormOf());
							getSurface().drawSpriteClipping(mudclient.spriteItem + originalDef.getSprite(), xI + 7, yI + 4,
									33, 23, originalDef.getPictureMask(), 0, false, 0, 1);
						}

						if (EntityHandler.getItemDef(this.duelOfferItemID[itmOffer]).isStackable()) {
							this.getSurface().drawString("" + this.duelOfferItemSize[itmOffer], 1 + xI, 10 + yI, 16776960,
									1);
						}

						if (xI < this.mouseX && this.mouseX < 48 + xI && yI < this.mouseY && 32 + yI > this.mouseY) {
							this.getSurface().drawString(
									EntityHandler.getItemDef(this.duelOfferItemID[itmOffer]).getName() + ": @whi@"
											+ EntityHandler.getItemDef(this.duelOfferItemID[itmOffer]).getDescription(),
											8 + xr, yr + 273, 16776960, 1);
						}
					}

					for (int itmOffer = 0; itmOffer < this.duelOffsetOpponentItemCount; ++itmOffer) {
						int xI = itmOffer % 4 * 49 + 9 + xr;
						int yI = itmOffer / 4 * 34 + 124 + yr;
						this.getSurface().drawSpriteClipping(
								EntityHandler.getItemDef(this.duelOpponentItemId[itmOffer]).getSprite()
								+ mudclient.spriteItem,
								xI, yI, 48, 32,
								EntityHandler.getItemDef(this.duelOpponentItemId[itmOffer]).getPictureMask(), 0, false, 0,
								1);

						ItemDef def = EntityHandler.getItemDef(this.duelOpponentItemId[itmOffer]);
						if (def.getNotedFormOf() >= 0) {
							ItemDef originalDef = EntityHandler.getItemDef(def.getNotedFormOf());
							getSurface().drawSpriteClipping(mudclient.spriteItem + originalDef.getSprite(), xI + 7, yI + 4,
									33, 23, originalDef.getPictureMask(), 0, false, 0, 1);
						}

						if (EntityHandler.getItemDef(this.duelOpponentItemId[itmOffer]).isStackable()) {
							this.getSurface().drawString("" + this.duelOpponentItemCount[itmOffer], 1 + xI, 10 + yI,
									16776960, 1);
						}

						if (this.mouseX > xI && 48 + xI > this.mouseX && yI < this.mouseY && this.mouseY < yI + 32) {
							this.getSurface().drawString(
									EntityHandler.getItemDef(this.duelOpponentItemId[itmOffer]).getName() + ": @whi@"
											+ EntityHandler.getItemDef(this.duelOpponentItemId[itmOffer]).getDescription(),
											xr + 8, 273 + yr, 16776960, 1);
						}
					}

					if (this.menuDuel_Visible) {
						this.menuDuel.render(this.menuDuelY, this.menuDuelX, this.mouseY, (byte) -12, this.mouseX);
					}

				}
			} catch (RuntimeException var11) {
				throw GenUtil.makeThrowable(var11, "client.TD(" + "dummy" + ')');
			}
		}

		private final void drawDialogDuelConfirm() {
			try {

				byte xr = 22;
				byte yr = 36;
				this.getSurface().drawBox(xr, yr, 468, 16, 192);
				int color = 10000536;
				this.getSurface().drawBoxAlpha(xr, yr + 16, 468, 246, color, 160);
				this.getSurface().drawColoredStringCentered(xr + 234,
						"Please confirm your duel with @yel@" + this.duelOpponentName, 16777215, 0, 1, yr + 12);
				this.getSurface().drawColoredStringCentered(xr + 117, "Your stake:", 16776960, 0, 1, yr + 30);

				for (int var5 = 0; this.duelItemsCount > var5; ++var5) {
					String var6 = EntityHandler.getItemDef(this.duelItems[var5]).getName();
					if (EntityHandler.getItemDef(this.duelItems[var5]).isStackable()) {
						var6 = var6 + " x " + StringUtil.formatItemCount(this.duelItemCounts[var5]);
					}

					this.getSurface().drawColoredStringCentered(xr + 117, var6, 16777215, 0, 1, 42 + yr + var5 * 12);
				}

				if (this.duelItemsCount == 0) {
					this.getSurface().drawColoredStringCentered(xr + 117, "Nothing!", 16777215, 0, 1, 42 + yr);
				}

				this.getSurface().drawColoredStringCentered(351 + xr, "Your opponent\'s stake:", 16776960, 0, 1, 30 + yr);

				for (int var5 = 0; var5 < this.duelOpponentItemsCount; ++var5) {
					String var6 = EntityHandler.getItemDef(this.duelOpponentItems[var5]).getName();
					if (EntityHandler.getItemDef(this.duelOpponentItems[var5]).isStackable()) {
						var6 = var6 + " x " + StringUtil.formatItemCount(this.duelOpponentItemCounts[var5]);
					}

					this.getSurface().drawColoredStringCentered(xr + 351, var6, 16777215, 0, 1, var5 * 12 + 42 + yr);
				}

				if (this.duelOpponentItemsCount == 0) {
					this.getSurface().drawColoredStringCentered(351 + xr, "Nothing!", 16777215, 0, 1, 42 + yr);
				}

				if (this.duelOptionRetreat == 0) {
					this.getSurface().drawColoredStringCentered(xr + 234, "You can retreat from this duel", '\uff00', 0, 1,
							yr + 180);
				} else {
					this.getSurface().drawColoredStringCentered(234 + xr, "No retreat is possible!", 16711680, 0, 1,
							180 + yr);
				}

				if (this.duelOptionMagic == 0) {
					this.getSurface().drawColoredStringCentered(234 + xr, "Magic may be used", '\uff00', 0, 1, yr + 192);
				} else {
					this.getSurface().drawColoredStringCentered(xr + 234, "Magic cannot be used", 16711680, 0, 1, 192 + yr);
				}

				if (this.duelOptionPrayer == 0) {
					this.getSurface().drawColoredStringCentered(xr + 234, "Prayer may be used", '\uff00', 0, 1, 204 + yr);
				} else {
					this.getSurface().drawColoredStringCentered(xr + 234, "Prayer cannot be used", 16711680, 0, 1,
							yr + 204);
				}

				if (this.duelOptionWeapons != 0) {
					this.getSurface().drawColoredStringCentered(xr + 234, "Weapons cannot be used", 16711680, 0, 1,
							216 + yr);
				} else {
					this.getSurface().drawColoredStringCentered(xr + 234, "Weapons may be used", '\uff00', 0, 1, yr + 216);
				}

				this.getSurface().drawColoredStringCentered(xr + 234, "If you are sure click \'Accept\' to begin the duel",
						16777215, 0, 1, yr + 230);
				if (!this.duelConfirmed) {
					this.getSurface().drawSprite(mudclient.spriteMedia + 25, 83 + xr, 238 + yr);
					this.getSurface().drawSprite(26 + mudclient.spriteMedia, xr - 35 + 352, yr + 238);
				} else {
					this.getSurface().drawColoredStringCentered(xr + 234, "Waiting for other player...", 16776960, 0, 1,
							yr + 250);
				}

				if (this.mouseButtonClick == 1) {
					if (xr > this.mouseX || this.mouseY < yr || xr + 468 < this.mouseX || this.mouseY > 262 + yr) {
						this.showDialogDuelConfirm = false;
						this.getClientStream().newPacket(230);
						this.getClientStream().finishPacket();
					}

					if (118 + xr - 35 <= this.mouseX && this.mouseX <= xr + 118 + 70 && yr + 238 <= this.mouseY
							&& 238 + yr + 21 >= this.mouseY) {
						this.duelConfirmed = true;
						this.getClientStream().newPacket(77);
						this.getClientStream().finishPacket();
					}

					if (352 + (xr - 35) <= this.mouseX && 353 + xr + 70 >= this.mouseX && yr + 238 <= this.mouseY
							&& 259 + yr >= this.mouseY) {
						this.showDialogDuelConfirm = false;
						this.getClientStream().newPacket(197);
						this.getClientStream().finishPacket();
					}

					this.mouseButtonClick = 0;
				}
			} catch (RuntimeException var7) {
				throw GenUtil.makeThrowable(var7, "client.VC(" + "dummy" + ')');
			}
		}

		private final void drawDialogLogout() {
			try {

				this.getSurface().drawBox((getGameWidth() - 260) / 2, (getGameHeight() - 60) / 2, 260, 60, 0);
				this.getSurface().drawBoxBorder((getGameWidth() - 260) / 2, 260, (getGameHeight() - 60) / 2, 60, 16777215);
				this.getSurface().drawColoredStringCentered((getGameWidth() - 256) / 2 + 256 / 2, "Logging out...", 16777215, 0, 5, (getGameHeight() - 60) / 2 + 36);
			} catch (RuntimeException var3) {
				throw GenUtil.makeThrowable(var3, "client.SD(" + "dummy" + ')');
			}
		}

		private final void drawDialogOptionsMenu(int var1) {
			try {


				if (Config.isAndroid()) {
					int startY = 25;
					int startX = 5;
					int spread = 20;
					int highest = 0;
					int boxEndY = 0;
					for (int j = 0; j < optionsMenuCount; j++) {
						int textWidth = getSurface().stringWidth(6, optionsMenuText[j]) + 15;
						if (highest < textWidth) {
							highest = textWidth;
						}
						if (boxEndY < startY + j * spread + 20) {
							boxEndY = startY + j * spread + 20;
						}
					}
					if (mouseButtonClick != 0) {
						for (int i = 0; i < optionsMenuCount; i++) {
							if (mouseX > startX && mouseX < startX + highest && mouseY > startY + i * spread - 15
									&& mouseY < startY + i * spread) {
								this.getClientStream().newPacket(116);
								this.getClientStream().writeBuffer1.putByte(i);
								this.getClientStream().finishPacket();
								break;
							}
						}

						mouseButtonClick = 0;
						optionsMenuShow = false;
						return;
					}
					for (int j = 0; j < optionsMenuCount; j++) {
						int k = 65535;
						if (mouseX > startX && mouseX < startX + highest && mouseY > startY + j * spread - 15
								&& mouseY < startY + j * spread)
							k = 0xff0000;

						this.getSurface().drawString(
								(optionsMenuKeyboardInput ? "(" + (j + 1) + ")" : "") + optionsMenuText[j], startX + 10,
								startY + j * spread, k, 6);
					}
				} else {
					int var2;
					if (this.mouseButtonClick == 0) {
						var2 = 0;
						while (this.optionsMenuCount > var2) {
							int var3 = '\uffff';
							if (this.mouseX < this.getSurface().stringWidth(1, this.optionsMenuText[var2])
									&& this.mouseY > var2 * 12 && this.mouseY < var2 * 12 + 12) {
								var3 = 16711680;
							}

							this.getSurface().drawString(
									(optionsMenuKeyboardInput ? "(" + (var2 + 1) + ") " : "") + this.optionsMenuText[var2],
									6, var2 * 12 + 12, var3, 1);
							++var2;
						}
					} else {
						for (var2 = 0; var2 < this.optionsMenuCount; ++var2) {
							if (this.getSurface().stringWidth(1, this.optionsMenuText[var2]) > this.mouseX
									&& var2 * 12 < this.mouseY && 12 + var2 * 12 > this.mouseY) {
								this.getClientStream().newPacket(116);
								this.getClientStream().writeBuffer1.putByte(var2);
								this.getClientStream().finishPacket();
								break;
							}
						}

						this.optionsMenuShow = false;
						this.mouseButtonClick = 0;
					}
				}
			} catch (RuntimeException var4) {
				throw GenUtil.makeThrowable(var4, "client.FB(" + var1 + ')');
			}
		}

		private final void drawDialogServerMessage(byte var1) {
			try {

				short var2 = 400;
				if (var1 != -115) {
					this.m_qd = 64;
				}

				short var3 = 100;
				if (this.serverMessageBoxTop) {
					// boolean var7 = true;
					var3 = 300;
				}
				int xr = (getGameWidth() - var2) / 2;
				int yr = (getGameHeight() - var3) / 2;

				this.getSurface().drawBox(xr, yr, var2, var3, 0);
				this.getSurface().drawBoxBorder(xr, var2, yr, var3, 16777215);
				this.getSurface().drawWrappedCenteredString(this.serverMessage, xr + 256 - 56, yr + 17, var2 - 40, 1,
						16777215, true);
				int var4 = (getGameHeight() + (var3) - 23) / 2;

				int var5 = 16777215;
				if (var4 - 12 < this.mouseY && var4 >= this.mouseY && this.mouseX > xr + 50 && this.mouseX < xr + 350) {
					var5 = 16711680;

				}

				this.getSurface().drawColoredStringCentered(xr + 256 - 56, "Click here to close window", var5, 0, 1, var4);

				if (this.mouseButtonClick == 1) {
					if (var5 == 16711680) {
						this.showDialogServerMessage = false;

					}

					if (var4 - 12 < this.mouseY && var4 >= this.mouseY && this.mouseX > xr + 50 && this.mouseX < xr + 350) {
						this.showDialogServerMessage = false;
					}
				}

				this.mouseButtonClick = 0;
			} catch (RuntimeException var6) {
				throw GenUtil.makeThrowable(var6, "client.V(" + var1 + ')');
			}
		}

		private final void drawDialogShop() {
			try {

				if (this.mouseButtonClick != 0 && this.inputX_Action == InputXAction.ACT_0) {
					this.mouseButtonClick = 0;
					int mlx = this.mouseX - (getGameWidth() - 408) / 2;
					int mly = this.mouseY - (getGameHeight() - 246) / 2;
					if (mlx < 0 || mly < 12 || mlx >= 408 || mly >= 246) {
						this.getClientStream().newPacket(166);
						this.getClientStream().finishPacket();
						this.showDialogShop = false;
						return;
					}

					{
						int slot = 0;
						for (int row = 0; row < 5; ++row) {
							for (int column = 0; column < 8; ++column) {
								int sx = column * 49 + 7;
								int sy = row * 34 + 28;
								if (mlx > sx && 49 + sx > mlx && mly > sy && sy + 34 > mly && this.shopItemID[slot] != -1) {
									this.shopSelectedItemIndex = slot;
									this.shopSelectedItemType = this.shopItemID[slot];
								}
								++slot;
							}
						}
					}

					if (this.shopSelectedItemIndex >= 0) {
						int id = this.shopItemID[this.shopSelectedItemIndex];
						if (id != -1) {
							int count = this.shopItemCount[this.shopSelectedItemIndex];
							if (count > 0 && mly >= 204 && mly <= 215) {
								byte btnCount = 0;
								if (mlx > 318 && mlx < 330) {
									btnCount = 1;
								}

								if (mlx > 333 && mlx < 345) {
									btnCount = 5;
								}

								if (mlx > 348 && mlx < 365) {
									btnCount = 10;
								}

								if (mlx > 368 && mlx < 385) {
									btnCount = 50;
								}

								if (mlx > 388 && mlx < 400) {
									this.showItemModX(InputXPrompt.shopBuyX, InputXAction.SHOP_BUY, true);
								}

								if (btnCount > 0) {
									this.getClientStream().newPacket(236);
									this.getClientStream().writeBuffer1
									.putShort(this.shopItemID[this.shopSelectedItemIndex]);
									this.getClientStream().writeBuffer1.putShort(count);
									this.getClientStream().writeBuffer1.putShort(btnCount);
									this.getClientStream().finishPacket();
								}
							}

							int invCount = this.getInventoryCount((int) id);
							if (invCount > 0 && mly >= 229 && mly <= 240) {
								byte btnCount = 0;
								if (mlx > 318 && mlx < 330) {
									btnCount = 1;
								}

								if (mlx > 333 && mlx < 345) {
									btnCount = 5;
								}

								if (mlx > 348 && mlx < 365) {
									btnCount = 10;
								}

								if (mlx > 388 && mlx < 400) {
									this.showItemModX(InputXPrompt.shopSellX, InputXAction.SHOP_SELL, true);
								}

								if (mlx > 368 && mlx < 385) {
									btnCount = 50;
								}

								if (btnCount > 0) {
									this.getClientStream().newPacket(221);
									this.getClientStream().writeBuffer1
									.putShort(this.shopItemID[this.shopSelectedItemIndex]);
									this.getClientStream().writeBuffer1.putShort(count);
									this.getClientStream().writeBuffer1.putShort(btnCount);
									this.getClientStream().finishPacket();
								}
							}
						}
					}
				}
				int xr = (getGameWidth() - 408) / 2;
				int yr = (getGameHeight() - 246) / 2;

				this.getSurface().drawBox(xr, yr, 408, 12, 192);
				int color = 10000536;
				this.getSurface().drawBoxAlpha(xr, 12 + yr, 408, 17, color, 160);
				this.getSurface().drawBoxAlpha(xr, yr + 29, 8, 170, color, 160);
				this.getSurface().drawBoxAlpha(xr + 399, 29 + yr, 9, 170, color, 160);
				this.getSurface().drawBoxAlpha(xr, 199 + yr, 408, 47, color, 160);
				this.getSurface().drawString("Buying and selling items", xr + 1, yr + 10, 16777215, 1);

				int color2 = 16777215;
				if (this.mouseX > 320 + xr && yr <= this.mouseY && this.mouseX < xr + 408 && this.mouseY < yr + 12) {
					color2 = 16711680;
				}
				this.getSurface().b(xr + 406, "Close window", yr + 10, color2, -92, 1);

				this.getSurface().drawString("Shops stock in green", 2 + xr, 24 + yr, '\uff00', 1);
				this.getSurface().drawString("Number you own in blue", xr + 135, yr + 24, '\uffff', 1);
				this.getSurface().drawString("Your money: " + formatStackAmount(this.getInventoryCount((int) 10)) + "gp",
						280 + xr, 24 + yr, 16776960, 1);
				{
					int slot = 0;
					for (int row = 0; row < 5; ++row) {
						for (int column = 0; column < 8; ++column) {
							int sx = column * 49 + 7 + xr;
							int sy = yr + 28 + row * 34;
							if (this.shopSelectedItemIndex == slot) {
								this.getSurface().drawBoxAlpha(sx, sy, 49, 34, 16711680, 160);
							} else {
								this.getSurface().drawBoxAlpha(sx, sy, 49, 34, 13684944, 160);
							}

							this.getSurface().drawBoxBorder(sx, 50, sy, 35, 0);
							if (this.shopItemID[slot] != -1) {
								this.getSurface().drawSpriteClipping(
										EntityHandler.getItemDef(this.shopItemID[slot]).getSprite() + mudclient.spriteItem,
										sx, sy, 48, 32, EntityHandler.getItemDef(this.shopItemID[slot]).getPictureMask(), 0,
										false, 0, 1);

								ItemDef def = EntityHandler.getItemDef(this.shopItemID[slot]);
								if (def.getNotedFormOf() >= 0) {
									ItemDef originalDef = EntityHandler.getItemDef(def.getNotedFormOf());
									getSurface().drawSpriteClipping(mudclient.spriteItem + originalDef.getSprite(), sx + 7,
											sy + 4, 33, 23, originalDef.getPictureMask(), 0, false, 0, 1);
								}

								this.getSurface().drawString("" + this.shopItemCount[slot], 1 + sx, 10 + sy, '\uff00', 1);
								this.getSurface().b(47 + sx, "" + this.getInventoryCount((int) this.shopItemID[slot]),
										10 + sy, '\uffff', -80, 1);
							}

							++slot;
						}
					}
				}

				this.getSurface().drawLineHoriz(5 + xr, yr + 222, 398, 0);
				if (this.shopSelectedItemIndex != -1) {
					int id = this.shopItemID[this.shopSelectedItemIndex];
					if (id != -1) {
						int count = this.shopItemCount[this.shopSelectedItemIndex];
						if (count <= 0) {
							this.getSurface().drawColoredStringCentered(204 + xr,
									"This item is not currently available to buy", 16776960, 0, 3, 214 + yr);
						} else {
							int cost = GenUtil.computeItemCost(EntityHandler.getItemDef(id).getBasePrice(),
									this.shopItemPrice[this.shopSelectedItemIndex], this.shopBuyPriceMod, -30910, true, 1,
									count, this.shopPriceMultiplier);
							this.getSurface().drawString(
									EntityHandler.getItemDef(id).getName() + ": buy for " + cost + "gp each", 2 + xr,
									yr + 214, 16776960, 1);
							boolean mouseInRow = 204 + yr <= this.mouseY && yr + 215 >= this.mouseY;
							this.getSurface().drawString("Buy:", xr + 285, 214 + yr, 16777215, 3);

							color2 = 16777215;
							if (mouseInRow && this.mouseX > 318 + xr && this.mouseX < xr + 330) {
								color2 = 16711680;
							}
							this.getSurface().drawString("1", xr + 320, 214 + yr, color2, 3);

							color2 = 16777215;
							if (mouseInRow && this.mouseX > 333 + xr && this.mouseX < 345 + xr) {
								color2 = 16711680;
							}
							this.getSurface().drawString("5", 335 + xr, 214 + yr, color2, 3);

							color2 = 16777215;
							if (mouseInRow && 348 + xr < this.mouseX && this.mouseX < xr + 365) {
								color2 = 16711680;
							}
							this.getSurface().drawString("10", 350 + xr, 214 + yr, color2, 3);

							color2 = 16777215;
							if (mouseInRow && this.mouseX > 368 + xr && 385 + xr > this.mouseX) {
								color2 = 16711680;
							}
							this.getSurface().drawString("50", xr + 370, 214 + yr, color2, 3);

							color2 = 16777215;
							if (mouseInRow && this.mouseX > xr + 388 && this.mouseX < 400 + xr) {
								color2 = 16711680;
							}
							this.getSurface().drawString("X", 390 + xr, 214 + yr, color2, 3);
						}

						int invCount = this.getInventoryCount((int) id);
						if (invCount <= 0) {
							this.getSurface().drawColoredStringCentered(xr + 204,
									"You do not have any of this item to sell", 16776960, 0, 3, 239 + yr);
						} else {

							int sellCost = GenUtil.computeItemCost(EntityHandler.getItemDef(id).getBasePrice(),
									this.shopItemPrice[this.shopSelectedItemIndex], this.shopSellPriceMod, -30910, false, 1,
									count, this.shopPriceMultiplier);
							if (EntityHandler.getItemDef(id).getNotedFormOf() >= 0) {

								int originalItemID = EntityHandler.getItemDef(id).getNotedFormOf();
								int originalItemPrice = 0;
								int originalItemCount = 0;
								for (int i = 0; i < shopItemID.length; i++) {
									if (shopItemPrice[i] == originalItemID) {
										originalItemPrice = shopItemPrice[i];
										break;
									}
								}

								for (int i = 0; i < shopItemID.length; i++) {
									if (shopItemID[i] == originalItemID) {
										originalItemCount = shopItemCount[i];
										break;
									}
								}
								sellCost = GenUtil.computeItemCost(EntityHandler.getItemDef(originalItemID).getBasePrice(),
										originalItemPrice, this.shopSellPriceMod, -30910, false, 1, originalItemCount,
										this.shopPriceMultiplier);
							}
							this.getSurface().drawString(
									EntityHandler.getItemDef(id).getName() + ": sell for " + sellCost + "gp each", 2 + xr,
									yr + 239, 16776960, 1);
							boolean mouseInRow = this.mouseY >= yr + 229 && yr + 240 >= this.mouseY;

							color2 = 16777215;
							this.getSurface().drawString("Sell:", xr + 285, yr + 239, 16777215, 3);
							if (mouseInRow && xr + 318 < this.mouseX && this.mouseX < xr + 330) {
								color2 = 16711680;
							}
							this.getSurface().drawString("1", xr + 320, 239 + yr, color2, 3);

							color2 = 16777215;
							if (mouseInRow && xr + 333 < this.mouseX && this.mouseX < xr + 345) {
								color2 = 16711680;
							}
							this.getSurface().drawString("5", 335 + xr, 239 + yr, color2, 3);

							color2 = 16777215;
							if (mouseInRow && 348 + xr < this.mouseX && 365 + xr > this.mouseX) {
								color2 = 16711680;
							}
							this.getSurface().drawString("10", xr + 350, 239 + yr, color2, 3);

							color2 = 16777215;
							if (mouseInRow && this.mouseX > xr + 368 && 385 + xr > this.mouseX) {
								color2 = 16711680;
							}
							this.getSurface().drawString("50", xr + 370, 239 + yr, color2, 3);

							color2 = 16777215;
							if (mouseInRow && this.mouseX > 388 + xr && xr + 400 > this.mouseX) {
								color2 = 16711680;
							}
							this.getSurface().drawString("X", xr + 390, yr + 239, color2, 3);
						}
					}
				} else {
					this.getSurface().drawColoredStringCentered(204 + xr, "Select an object to buy or sell", 16776960, 0, 3,
							214 + yr);
				}

			} catch (RuntimeException var14) {
				throw GenUtil.makeThrowable(var14, "client.HA(" + "dummy" + ')');
			}
		}

		private final void drawDialogTrade() {
			try {

				int clickOp = -1;
				if (this.mouseButtonClick != 0 && this.menuTrade_Visible) {
					clickOp = this.menuTrade.handleClick(this.mouseX, this.menuTradeX, this.menuTradeY, this.mouseY);
				}

				if (clickOp < 0) {
					if (this.inputX_Action == InputXAction.ACT_0) {
						if (this.mouseButtonClick == 1 && this.mouseButtonItemCountIncrement == 0) {
							this.mouseButtonItemCountIncrement = 1;
						}

						int mouseLX = this.mouseX - 22;
						int mouseLY = this.mouseY - 36;
						if (mouseLX >= 0 && mouseLY >= 0 && mouseLX < 468 && mouseLY < 262) {
							if (this.mouseButtonItemCountIncrement > 0) {
								if (mouseLX > 216 && mouseLY > 30 && mouseLX < 462 && mouseLY < 235) {
									int slot = (mouseLY - 31) / 34 * 5 + (mouseLX - 217) / 49;
									if (slot >= 0 && slot < this.inventoryItemCount) {
										this.tradeOffer((int) -1, (int) slot);
									}
								}

								if (mouseLX > 8 && mouseLY > 30 && mouseLX < 205 && mouseLY < 133) {
									int slot = (mouseLY - 31) / 34 * 4 + (mouseLX - 9) / 49;
									if (slot >= 0 && this.tradeItemCount > slot) {
										this.tradeRemove(-1, (byte) 125, slot);
									}
								}

								if (mouseLX >= 217 && mouseLY >= 238 && mouseLX <= 286 && mouseLY <= 259) {
									this.tradeAccepted = true;
									this.getClientStream().newPacket(55);
									this.getClientStream().finishPacket();
								}

								if (mouseLX >= 394 && mouseLY >= 238 && mouseLX < 463 && mouseLY < 259) {
									this.showDialogTrade = false;
									this.getClientStream().newPacket(230);
									this.getClientStream().finishPacket();
								}

								this.mouseButtonItemCountIncrement = 0;
								this.mouseButtonClick = 0;
							}

							if (this.mouseButtonClick == 2) {
								if (mouseLX > 216 && mouseLY > 30 && mouseLX < 462 && mouseLY < 235) {
									int w = this.menuCommon.getWidth();
									int h = this.menuCommon.getHeight();
									this.menuY = this.mouseY - 7;
									this.menuX = this.mouseX - w / 2;
									this.topMouseMenuVisible = true;
									if (this.menuY < 0) {
										this.menuY = 0;
									}

									if (this.menuX < 0) {
										this.menuX = 0;
									}

									if (this.menuX + w > 510) {
										this.menuX = 510 - w;
									}

									if (h + this.menuY > 315) {
										this.menuY = 315 - h;
									}

									int slot = (mouseLY - 31) / 34 * 5 + (mouseLX - 217) / 49;
									if (slot >= 0 && this.inventoryItemCount > slot) {
										int id = this.inventoryItemID[slot];
										this.menuTrade_Visible = true;
										this.menuTrade.recalculateSize(0);
										this.menuTrade.addCharacterItem_WithID(id,
												"@lre@" + EntityHandler.getItemDef(id).getName(),
												MenuItemAction.TRADE_OFFER, "Offer 1", 1);
										this.menuTrade.addCharacterItem_WithID(id,
												"@lre@" + EntityHandler.getItemDef(id).getName(),
												MenuItemAction.TRADE_OFFER, "Offer 5", 5);
										this.menuTrade.addCharacterItem_WithID(id,
												"@lre@" + EntityHandler.getItemDef(id).getName(),
												MenuItemAction.TRADE_OFFER, "Offer 10", 10);
										this.menuTrade.addCharacterItem_WithID(id,
												"@lre@" + EntityHandler.getItemDef(id).getName(),
												MenuItemAction.TRADE_OFFER, "Offer All", -1);
										this.menuTrade.addCharacterItem_WithID(id,
												"@lre@" + EntityHandler.getItemDef(id).getName(),
												MenuItemAction.TRADE_OFFER, "Offer X", -2);
										int wNew = this.menuTrade.getWidth();
										int hNew = this.menuTrade.getHeight();
										this.menuTradeX = this.mouseX - wNew / 2;
										this.menuTradeY = this.mouseY - 7;
										if (this.menuTradeX < 0) {
											this.menuTradeX = 0;
										}

										if (this.menuTradeY < 0) {
											this.menuTradeY = 0;
										}

										if (hNew + this.menuTradeY > 315) {
											this.menuTradeY = 315 - hNew;
										}

										if (this.menuTradeX + wNew > 510) {
											this.menuTradeX = 510 - wNew;
										}
									}
								}

								if (mouseLX > 8 && mouseLY > 30 && mouseLX < 205 && mouseLY < 133) {
									int slot = (mouseLX - 9) / 49 + (mouseLY - 31) / 34 * 4;
									if (slot >= 0 && slot < this.tradeItemCount) {
										int id = this.tradeItemID[slot];
										this.menuTrade_Visible = true;
										this.menuTrade.recalculateSize(0);
										this.menuTrade.addCharacterItem_WithID(id,
												"@lre@" + EntityHandler.getItemDef(id).getName(),
												MenuItemAction.TRADE_REMOVE, "Remove 1", 1);
										this.menuTrade.addCharacterItem_WithID(id,
												"@lre@" + EntityHandler.getItemDef(id).getName(),
												MenuItemAction.TRADE_REMOVE, "Remove 5", 5);
										this.menuTrade.addCharacterItem_WithID(id,
												"@lre@" + EntityHandler.getItemDef(id).getName(),
												MenuItemAction.TRADE_REMOVE, "Remove 10", 10);
										this.menuTrade.addCharacterItem_WithID(id,
												"@lre@" + EntityHandler.getItemDef(id).getName(),
												MenuItemAction.TRADE_REMOVE, "Remove All", -1);
										this.menuTrade.addCharacterItem_WithID(id,
												"@lre@" + EntityHandler.getItemDef(id).getName(),
												MenuItemAction.TRADE_REMOVE, "Remove X", -2);
										int wNew = this.menuTrade.getWidth();
										int hNew = this.menuTrade.getHeight();
										this.menuTradeX = this.mouseX - wNew / 2;
										this.menuTradeY = this.mouseY - 7;
										if (this.menuTradeX < 0) {
											this.menuTradeX = 0;
										}

										if (this.menuTradeY < 0) {
											this.menuTradeY = 0;
										}

										if (hNew + this.menuTradeY > 315) {
											this.menuTradeY = 315 - hNew;
										}

										if (wNew + this.menuTradeX > 510) {
											this.menuTradeX = 510 - wNew;
										}
									}
								}

								this.mouseButtonClick = 0;
							}

							if (this.menuTrade_Visible) {
								int w = this.menuTrade.getWidth();
								int h = this.menuTrade.getHeight();
								if (this.mouseX < this.menuTradeX - 10 || this.menuTradeY - 10 > this.mouseY
										|| this.mouseX > this.menuTradeX - (-w - 10)
										|| this.menuTradeY - (-h - 10) < this.mouseY) {
									this.menuTrade_Visible = false;
								}
							}
						} else if (this.mouseButtonClick != 0) {
							this.showDialogTrade = false;
							this.getClientStream().newPacket(230);
							this.getClientStream().finishPacket();
						}
					}
				} else {
					this.menuTrade_Visible = false;
					this.mouseButtonClick = 0;
					MenuItemAction act = this.menuTrade.getItemAction((int) clickOp);
					int itemID = this.menuTrade.getItemIndexOrX(clickOp);
					int firstSlot = -1;
					int itemCount = 0;
					if (act == MenuItemAction.TRADE_OFFER) {
						for (int invIdx = 0; invIdx < this.inventoryItemCount; ++invIdx) {
							if (this.inventoryItemID[invIdx] == itemID) {
								if (firstSlot < 0) {
									firstSlot = invIdx;
								}

								if (EntityHandler.getItemDef(itemID).isStackable()) {
									itemCount = this.inventoryItemSize[invIdx];
									break;
								}

								++itemCount;
							}
						}
					} else {
						for (int tradeIdx = 0; tradeIdx < this.tradeItemCount; ++tradeIdx) {
							if (itemID == this.tradeItemID[tradeIdx]) {
								if (firstSlot < 0) {
									firstSlot = tradeIdx;
								}

								if (EntityHandler.getItemDef(itemID).isStackable()) {
									itemCount = this.tradeItemSize[tradeIdx];
									break;
								}

								++itemCount;
							}
						}
					}

					if (firstSlot >= 0) {
						int modCount = this.menuTrade.getItemIdOrZ((int) clickOp);
						if (modCount == -2) {
							this.tradeDoX_Slot = firstSlot;
							if (act == MenuItemAction.TRADE_OFFER) {
								this.showItemModX(InputXPrompt.tradeOfferX, InputXAction.TRADE_OFFER, true);
							} else {
								this.showItemModX(InputXPrompt.tradeRemoveX, InputXAction.TRADE_REMOVE, true);
							}
						} else {
							if (modCount == -1) {
								modCount = itemCount;
							}

							if (act != MenuItemAction.TRADE_OFFER) {
								this.tradeRemove(modCount, (byte) 124, firstSlot);
							} else {
								this.tradeOffer(modCount, firstSlot);
							}
						}
					}
				}

				if (this.showDialogTrade) {
					byte xr = 22;
					byte yr = 36;
					this.getSurface().drawBox(xr, yr, 468, 12, 192);
					int color = 10000536;
					this.getSurface().drawBoxAlpha(xr, yr + 12, 468, 18, color, 160);
					this.getSurface().drawBoxAlpha(xr, yr + 30, 8, 248, color, 160);

					this.getSurface().drawBoxAlpha(xr + 205, yr + 30, 11, 248, color, 160);
					this.getSurface().drawBoxAlpha(xr + 462, 30 + yr, 6, 248, color, 160);
					this.getSurface().drawBoxAlpha(xr + 8, yr + 133, 197, 22, color, 160);
					this.getSurface().drawBoxAlpha(xr + 8, yr + 258, 197, 20, color, 160);
					this.getSurface().drawBoxAlpha(xr + 216, yr + 235, 246, 43, color, 160);
					int id = 13684944;
					this.getSurface().drawBoxAlpha(xr + 8, yr + 30, 197, 103, id, 160);
					this.getSurface().drawBoxAlpha(8 + xr, yr + 155, 197, 103, id, 160);
					this.getSurface().drawBoxAlpha(216 + xr, 30 + yr, 246, 205, id, 160);

					for (int var7 = 0; var7 < 4; ++var7) {
						this.getSurface().drawLineHoriz(8 + xr, 30 + yr + var7 * 34, 197, 0);
					}

					for (int var7 = 0; var7 < 4; ++var7) {
						this.getSurface().drawLineHoriz(xr + 8, var7 * 34 + 155 + yr, 197, 0);
					}

					for (int var7 = 0; var7 < 7; ++var7) {
						this.getSurface().drawLineHoriz(216 + xr, yr + 30 + var7 * 34, 246, 0);
					}

					for (int var7 = 0; var7 < 6; ++var7) {
						if (~var7 > -6) {
							this.getSurface().drawLineVert(xr + 8 + var7 * 49, 30 + yr, 0, 103);
						}

						if (var7 < 5) {
							this.getSurface().drawLineVert(var7 * 49 + 8 + xr, 155 + yr, 0, 103);
						}

						this.getSurface().drawLineVert(216 + xr + var7 * 49, yr + 30, 0, 205);
					}

					this.getSurface().drawString("Trading with: " + this.tradeRecipientName, xr + 1, 10 + yr, 16777215, 1);
					this.getSurface().drawString("Your Offer", xr + 9, yr + 27, 16777215, 4);
					this.getSurface().drawString("Opponent\'s Offer", xr + 9, yr + 152, 16777215, 4);
					this.getSurface().drawString("Your Inventory", xr + 216, yr + 27, 16777215, 4);
					if (!this.tradeAccepted) {
						this.getSurface().drawSprite(mudclient.spriteMedia + 25, xr + 217, yr + 238);
					}

					this.getSurface().drawSprite(mudclient.spriteMedia + 26, xr + 394, yr + 238);
					if (this.tradeRecipientAccepted) {
						this.getSurface().drawColoredStringCentered(xr + 341, "Other player", 16777215, 0, 1, 246 + yr);
						this.getSurface().drawColoredStringCentered(xr + 341, "has accepted", 16777215, 0, 1, 256 + yr);
					}

					if (this.tradeAccepted) {
						this.getSurface().drawColoredStringCentered(xr + 217 + 35, "Waiting for", 16777215, 0, 1, yr + 246);
						this.getSurface().drawColoredStringCentered(xr + 252, "other player", 16777215, 0, 1, 256 + yr);
					}

					for (int slot = 0; slot < this.inventoryItemCount; ++slot) {
						int sX = xr + 217 + slot % 5 * 49;
						int sY = 31 + yr + slot / 5 * 34;
						this.getSurface().drawSpriteClipping(
								mudclient.spriteItem + EntityHandler.getItemDef(this.inventoryItemID[slot]).getSprite(), sX,
								sY, 48, 32, EntityHandler.getItemDef(this.inventoryItemID[slot]).getPictureMask(), 0, false,
								0, 1);

						ItemDef def = EntityHandler.getItemDef(this.inventoryItemID[slot]);
						if (def.getNotedFormOf() >= 0) {
							ItemDef originalDef = EntityHandler.getItemDef(def.getNotedFormOf());
							getSurface().drawSpriteClipping(mudclient.spriteItem + originalDef.getSprite(), sX + 7, sY + 4,
									33, 23, originalDef.getPictureMask(), 0, false, 0, 1);
						}

						if (EntityHandler.getItemDef(this.inventoryItemID[slot]).isStackable()) {
							this.getSurface().drawString("" + formatStackAmount(this.inventoryItemSize[slot]), 1 + sX,
									10 + sY, 16776960, 1);
						}
					}

					for (int slot = 0; this.tradeItemCount > slot; ++slot) {
						int sx = slot % 4 * 49 + 9 + xr;
						int sy = slot / 4 * 34 + yr + 31;
						this.getSurface().drawSpriteClipping(
								EntityHandler.getItemDef(this.tradeItemID[slot]).getSprite() + mudclient.spriteItem, sx, sy,
								48, 32, EntityHandler.getItemDef(this.tradeItemID[slot]).getPictureMask(), 0, false, 0, 1);
						if (EntityHandler.getItemDef(this.tradeItemID[slot]).isStackable()) {
							this.getSurface().drawString("" + formatStackAmount(this.tradeItemSize[slot]), sx + 1, 10 + sy,
									16776960, 1);
						}

						ItemDef def = EntityHandler.getItemDef(this.tradeItemID[slot]);
						if (def.getNotedFormOf() >= 0) {
							ItemDef originalDef = EntityHandler.getItemDef(def.getNotedFormOf());
							getSurface().drawSpriteClipping(mudclient.spriteItem + originalDef.getSprite(), sx + 7, sy + 4,
									33, 23, originalDef.getPictureMask(), 0, false, 0, 1);
						}

						if (sx < this.mouseX && 48 + sx > this.mouseX && sy < this.mouseY && this.mouseY < sy + 32) {
							this.getSurface().drawString(
									EntityHandler.getItemDef(this.tradeItemID[slot]).getName() + ": @whi@"
											+ EntityHandler.getItemDef(this.tradeItemID[slot]).getDescription(),
											8 + xr, 273 + yr, 16776960, 1);
						}
					}

					for (int slot = 0; this.tradeRecipientItemsCount > slot; ++slot) {
						int sx = xr + 9 + slot % 4 * 49;
						int sy = yr + 156 + slot / 4 * 34;
						this.getSurface().drawSpriteClipping(
								EntityHandler.getItemDef(this.tradeRecipientItem[slot]).getSprite() + mudclient.spriteItem,
								sx, sy, 48, 32, EntityHandler.getItemDef(this.tradeRecipientItem[slot]).getPictureMask(), 0,
								false, 0, 1);

						ItemDef def = EntityHandler.getItemDef(this.tradeRecipientItem[slot]);
						if (def.getNotedFormOf() >= 0) {
							ItemDef originalDef = EntityHandler.getItemDef(def.getNotedFormOf());
							getSurface().drawSpriteClipping(mudclient.spriteItem + originalDef.getSprite(), sx + 7, sy + 4,
									33, 23, originalDef.getPictureMask(), 0, false, 0, 1);
						}

						if (EntityHandler.getItemDef(this.tradeRecipientItem[slot]).isStackable()) {
							this.getSurface().drawString("" + this.tradeRecipientItemCount[slot], sx + 1, 10 + sy, 16776960,
									1);
						}

						if (sx < this.mouseX && this.mouseX < sx + 48 && this.mouseY > sy && sy + 32 > this.mouseY) {
							this.getSurface().drawString(
									EntityHandler.getItemDef(this.tradeRecipientItem[slot]).getName() + ": @whi@"
											+ EntityHandler.getItemDef(this.tradeRecipientItem[slot]).getDescription(),
											xr + 8, yr + 273, 16776960, 1);
						}
					}

					if (this.menuTrade_Visible) {
						this.menuTrade.render(this.menuTradeY, this.menuTradeX, this.mouseY, (byte) -12, this.mouseX);
					}

				}
			} catch (RuntimeException var11) {
				throw GenUtil.makeThrowable(var11, "client.G(" + "dummy" + ')');
			}
		}

		private final void drawDialogWelcome(int var1) {
			try {

				int var2 = 65;
				if (this.welcomeSubscriptionDaysLeft > 0 || this.welcomePremiumDaysLeft > 0) {
					var2 += 30;
				}

				if (this.welcomeUnreadMessages > 0) {
					var2 += 30;
				}

				if (!this.welcomeLastLoggedInIp.equalsIgnoreCase("0.0.0.0")) {
					var2 += 45;
				}
				int xr = (getGameWidth() - 400) / 2;
				int yr = (getGameHeight() - var2) / 2;

				this.getSurface().drawBox(xr, yr, 400, var2, 0);
				int var3 = yr;
				this.getSurface().drawBoxBorder(xr, 400, yr, var2, 16777215);
				var3 += 20;
				this.getSurface().drawColoredStringCentered(xr + 256 - 56, "Welcome to RuneScape " + this.localPlayer.accountName,
						16776960, 0, 4, var3);
				var3 += 30;
				String var4;
				if (this.welcomeLastLoggedInDays == 0) {
					var4 = "earlier today";
				} else if (this.welcomeLastLoggedInDays != 1) {
					var4 = this.welcomeLastLoggedInDays + " days ago";
				} else {
					var4 = "yesterday";
				}

				if (!this.welcomeLastLoggedInIp.equalsIgnoreCase("0.0.0.0")) {
					this.getSurface().drawColoredStringCentered(xr + 256 - 56, "You last logged in " + var4, 16777215, 0, 1, var3);
					var3 += 15;
					if (this.welcomeLastLoggedInHost == null) {
						this.welcomeLastLoggedInHost = getHostnameFromIP();
					}

					this.getSurface().drawColoredStringCentered(xr + 256 - 56, "from: " + this.welcomeLastLoggedInHost, 16777215,
							var1 ^ -4853, 1, var3);
					var3 += 15;
					var3 += 15;
				}

				if (this.welcomeUnreadMessages > 0) {
					if (this.welcomeUnreadMessages == 1) {
						this.getSurface().drawColoredStringCentered(xr + 256 - 56,
								"You have @yel@0@whi@ unread messages in your message-centre", 16777215, 0, 1, var3);
					} else {
						this.getSurface()
						.drawColoredStringCentered(xr + 256 - 56,
								"You have @gre@" + (this.welcomeUnreadMessages - 1)
								+ " unread messages @whi@in your message-centre",
								16777215, var1 + 4853, 1, var3);
					}

					var3 += 15;
					var3 += 15;
				}

				if (this.welcomeSubscriptionDaysLeft > 0 || this.welcomePremiumDaysLeft > 0) {
					if(this.welcomeSubscriptionDaysLeft > 0) {
						this.getSurface().drawColoredStringCentered((this.welcomePremiumDaysLeft > 0 ? xr + 156 - 56 : xr + 256 - 56),
								"Standard subscription: @yel@" + welcomeSubscriptionDaysLeft + " @whi@days", 0xFFFFFF,
								var1 ^ -4853, 1, var3);
					}
					if(this.welcomePremiumDaysLeft > 0) {
						this.getSurface().drawColoredStringCentered((this.welcomeSubscriptionDaysLeft > 0 ? xr + 356 - 56 : xr + 256 - 56),
								"Premium subscription: @cya@" + welcomePremiumDaysLeft + " @whi@days", 0xFFFFFF,
								var1 ^ -4853, 1, var3);
					}
					var3 += 15;
					var3 += 15;
				}

				int var5 = 16777215;
				if(Config.isAndroid()) {

					this.getSurface().drawBoxAlpha(150, var3 - 20, (207), var3 - (var3 - 12) + 20, 3158064, 160);
					this.getSurface().drawBoxBorder(150, (207), var3 - 20, var3 - (var3 - 12) + 20,4210752);
					if (this.mouseY >= var3 - 20 && this.mouseY <= (var3 - 20) + (var3 - (var3 - 12) + 20) && this.mouseX >= 150 && this.mouseX < 150+207) {
						var5 = 16711680;
					}

				} else {

					if (this.mouseY > var3 - 12 && this.mouseY <= var3 && this.mouseX > xr + 106 - 56 && this.mouseX < xr + 406 - 56) {
						var5 = 16711680;
					}
				}

				this.getSurface().drawColoredStringCentered(xr + 256 - 56, "Click here to close window", var5, var1 ^ var1, 1, var3);
				if (this.mouseButtonClick == 1) {
					if (var5 == 16711680) {
						this.showDialogMessage = false;
					}

					if ((this.mouseX < xr + 86 - 56 || this.mouseX > xr + 426 - 56)
							&& (this.mouseY < 167 - yr || yr + 167 < this.mouseY)) {
						this.showDialogMessage = false;
					}
				}

				this.mouseButtonClick = 0;
			} catch (RuntimeException var6) {
				throw GenUtil.makeThrowable(var6, "client.JB(" + var1 + ')');
			}
		}

		private String getHostnameFromIP() {

			return welcomeLastLoggedInIp;
		}

		private final void drawDialogWildWarn(int var1) {
			try {

				this.getSurface().drawBox(86, 77, 340, 180, 0);
				byte var2 = 97;
				if (var1 <= 90) {
					this.loadGameConfig(true);
				}

				this.getSurface().drawBoxBorder(86, 340, 77, 180, 16777215);
				this.getSurface().drawColoredStringCentered(256, "Warning! Proceed with caution", 16711680, 0, 4, var2);
				int var5 = var2 + 26;
				this.getSurface().drawColoredStringCentered(256, "If you go much further north you will enter the",
						16777215, 0, 1, var5);
				var5 += 13;
				this.getSurface().drawColoredStringCentered(256, "wilderness. This a very dangerous area where", 16777215,
						0, 1, var5);
				var5 += 13;
				this.getSurface().drawColoredStringCentered(256, "other players can attack you!", 16777215, 0, 1, var5);
				var5 += 22;
				this.getSurface().drawColoredStringCentered(256, "The further north you go the more dangerous it", 16777215,
						0, 1, var5);
				var5 += 13;
				this.getSurface().drawColoredStringCentered(256, "becomes, but the more treasure you will find.", 16777215,
						0, 1, var5);
				var5 += 22;
				this.getSurface().drawColoredStringCentered(256, "In the wilderness an indicator at the bottom-right",
						16777215, 0, 1, var5);
				var5 += 13;
				this.getSurface().drawColoredStringCentered(256, "of the screen will show the current level of danger",
						16777215, 0, 1, var5);
				var5 += 22;
				int var3 = 16777215;
				if (this.mouseY > var5 - 12 && this.mouseY <= var5 && this.mouseX > 181 && this.mouseX < 331) {
					var3 = 16711680;
				}

				this.getSurface().drawColoredStringCentered(256, "Click here to close window", var3, 0, 1, var5);
				if (this.mouseButtonClick != 0) {
					if (var5 - 12 < this.mouseY && var5 >= this.mouseY && this.mouseX > 181 && this.mouseX < 331) {
						this.showUiWildWarn = 2;
					}

					this.mouseButtonClick = 0;
					if (this.mouseX < 86 || this.mouseX > 426 || this.mouseY < 77 || this.mouseY > 257) {
						this.showUiWildWarn = 2;
					}
				}

			} catch (RuntimeException var4) {
				throw GenUtil.makeThrowable(var4, "client.FA(" + var1 + ')');
			}
		}

		private final void drawGame(int var1) {
			try {

				if (var1 == 13) {
					if (this.deathScreenTimeout != 0) {
						this.getSurface().fade2black(16316665);
						this.getSurface().drawColoredStringCentered(this.getGameWidth() / 2, "Oh dear! You are dead...",
								16711680, 0, 7, this.getGameHeight() / 2);
						this.drawChatMessageTabs(var1 - 8);
						// this.getSurface().draw(this.graphics, this.screenOffsetX,
						// 256, this.screenOffsetY);
						clientPort.draw();
					} else if (this.showAppearanceChange) {
						this.drawAppearancePanelCharacterSprites(-13759);
					} else if (this.isSleeping) {
						this.getSurface().fade2black(16316665);
						if (Math.random() < 0.15D) {
							this.getSurface().drawColoredStringCentered((int) (Math.random() * 80.0D), "ZZZ",
									(int) (1.6777215E7D * Math.random()), 0, 5, (int) (334.0D * Math.random()));
						}

						if (0.15D > Math.random()) {
							this.getSurface().drawColoredStringCentered(getGameWidth() - (int) (80.0D * Math.random()),
									"ZZZ", (int) (Math.random() * 1.6777215E7D), var1 ^ 13, 5,
									(int) (334.0D * Math.random()));
						}
						//"*"
						this.getSurface().drawBox(this.getGameWidth() / 2 - 100, 160 - (Config.isAndroid() ? 80 : 0), 200, 40, 0);
						if(Config.isAndroid()) {
							this.getSurface().drawColoredStringCentered(this.getGameWidth() / 2,
									"You are sleeping - Fatigue: " + this.fatigueSleeping * 100 / 750 + "%", 16776960, var1 - 13, 7, 31);
						} else {
							this.getSurface().drawColoredStringCentered(this.getGameWidth() / 2, "You are sleeping", 16776960,
									var1 - 13, 7, 50);
							this.getSurface().drawColoredStringCentered(this.getGameWidth() / 2,
									"Fatigue: " + this.fatigueSleeping * 100 / 750 + "%", 16776960, var1 - 13, 7, 90);
						}
						this.getSurface().drawColoredStringCentered(this.getGameWidth() / 2,
								"When you want to wake up just use your", 16777215, 0, 5, 140 - (Config.isAndroid() ? 80 : 0));
						this.getSurface().drawColoredStringCentered(this.getGameWidth() / 2,
								"keyboard to type the word in the box below", 16777215, var1 ^ 13, 5, 160 - (Config.isAndroid() ? 80 : 0));
						this.getSurface().drawColoredStringCentered(this.getGameWidth() / 2, this.inputTextCurrent + "*",
								'\uffff', var1 - 13, 5, 180 - (Config.isAndroid() ? 80 : 0));
						if (null != this.sleepingStatusText) {
							this.getSurface().drawColoredStringCentered(this.getGameWidth() / 2, this.sleepingStatusText,
									16711680, 0, 5, 260 - (Config.isAndroid() ? 110 : 0));
						} else {
							this.getSurface().drawSprite(mudclient.spriteLogo + 2, this.getGameWidth() / 2 - 127, 230 - (Config.isAndroid() ? 110 : 0));
						}

						this.getSurface().drawBoxBorder(this.getGameWidth() / 2 - 128, 257, 229 - (Config.isAndroid() ? 110 : 0), 42, 16777215);
						this.drawChatMessageTabs(5);
						this.getSurface().drawColoredStringCentered(this.getGameWidth() / 2, "If you can\'t read the word",
								16777215, var1 - 13, 1, 290 - (Config.isAndroid() ? 110 : 0));
						this.getSurface().drawColoredStringCentered(this.getGameWidth() / 2,
								"@yel@click here@whi@ to get a different one", 16777215, var1 ^ 13, 1, 305 - (Config.isAndroid() ? 110 : 0));
						// this.getSurface().draw(this.graphics, this.screenOffsetX,
						// 256, this.screenOffsetY);
						clientPort.draw();
					} else if (this.world.playerAlive) {

						int centerX;
						for (centerX = 0; centerX < 64; ++centerX) {
							this.scene.removeModel(this.world.modelRoofGrid[this.lastHeightOffset][centerX]);
							if (this.lastHeightOffset == 0) {
								this.scene.removeModel(this.world.modelWallGrid[1][centerX]);
								this.scene.removeModel(this.world.modelRoofGrid[1][centerX]);
								this.scene.removeModel(this.world.modelWallGrid[2][centerX]);
								this.scene.removeModel(this.world.modelRoofGrid[2][centerX]);
							}

							this.fogOfWar = false;
							if (this.lastHeightOffset == 0
									&& (world.collisionFlags[this.localPlayer.currentX / 128][this.localPlayer.currentZ
									                                                          / 128] & 0x80) == 0
									                                                          && Config.SHOW_ROOF) {

								this.scene.addModel(this.world.modelRoofGrid[this.lastHeightOffset][centerX]);
								if (this.lastHeightOffset == 0 && Config.SHOW_ROOF) {
									this.scene.addModel(this.world.modelWallGrid[1][centerX]);
									this.scene.addModel(this.world.modelRoofGrid[1][centerX]);
									this.scene.addModel(this.world.modelWallGrid[2][centerX]);
									this.scene.addModel(this.world.modelRoofGrid[2][centerX]);
								}

								this.fogOfWar = false;
							}
						}

						if (this.objectAnimationNumberFireLightningSpell != this.lastObjectAnimationNumberFireLightningSpell) {
							this.lastObjectAnimationNumberFireLightningSpell = this.objectAnimationNumberFireLightningSpell;

							for (centerX = 0; centerX < this.gameObjectInstanceCount; ++centerX) {
								if (this.gameObjectInstanceID[centerX] == 97) {
									this.updateObjectAnimation((byte) 48, centerX,
											"firea" + (this.objectAnimationNumberFireLightningSpell + 1));
								}

								if (this.gameObjectInstanceID[centerX] == 274) {
									this.updateObjectAnimation((byte) 58, centerX,
											"fireplacea" + (this.objectAnimationNumberFireLightningSpell + 1));
								}

								if (this.gameObjectInstanceID[centerX] == 1031) {
									this.updateObjectAnimation((byte) 103, centerX,
											"lightning" + (1 + this.objectAnimationNumberFireLightningSpell));
								}

								if (this.gameObjectInstanceID[centerX] == 1036) {
									this.updateObjectAnimation((byte) 89, centerX,
											"firespell" + (this.objectAnimationNumberFireLightningSpell + 1));
								}

								if (this.gameObjectInstanceID[centerX] == 1147) {
									this.updateObjectAnimation((byte) 18, centerX,
											"spellcharge" + (1 + this.objectAnimationNumberFireLightningSpell));
								}
							}
						}

						if (this.lastObjectAnimationNumberTorch != this.objectAnimationNumberTorch) {
							this.lastObjectAnimationNumberTorch = this.objectAnimationNumberTorch;

							for (centerX = 0; this.gameObjectInstanceCount > centerX; ++centerX) {
								if (this.gameObjectInstanceID[centerX] == 51) {
									this.updateObjectAnimation((byte) 23, centerX,
											"torcha" + (1 + this.objectAnimationNumberTorch));
								}

								if (this.gameObjectInstanceID[centerX] == 143) {
									this.updateObjectAnimation((byte) 100, centerX,
											"skulltorcha" + (1 + this.objectAnimationNumberTorch));
								}
							}
						}

						if (this.objectAnimationNumberClaw != this.lastObjectAnimatonNumberClaw) {
							this.lastObjectAnimatonNumberClaw = this.objectAnimationNumberClaw;

							for (centerX = 0; this.gameObjectInstanceCount > centerX; ++centerX) {
								if (this.gameObjectInstanceID[centerX] == 1142) {
									this.updateObjectAnimation((byte) 89, centerX,
											"clawspell" + (1 + this.objectAnimationNumberClaw));
								}
							}
						}

						this.scene.reduceSprites((byte) 67, (int) this.spriteCount);
						this.spriteCount = 0;

						for (centerX = 0; this.playerCount > centerX; ++centerX) {
							RSCharacter var3 = this.players[centerX];
							if (var3.colourBottom != 255) {
								int var4 = var3.currentX;
								int var5 = var3.currentZ;
								int var6 = -this.world.getElevation(var4, var5);
								int var7 = this.scene.drawSprite(centerX + 5000, var5, centerX + 10000, var4, var6, 145,
										220, (byte) 109);
								++this.spriteCount;
								if (this.localPlayer == var3) {
									this.scene.setFaceSpriteLocalPlayer('\u8000', var7);
								}

								if (var3.direction == RSCharacterDirection.COMBAT_A) {
									this.scene.setCombatXOffset(var1 + 24, var7, -30);
								}

								if (var3.direction == RSCharacterDirection.COMBAT_B) {
									this.scene.setCombatXOffset(var1 ^ 45, var7, 30);
								}
							}
						}

						for (centerX = 0; centerX < this.playerCount; ++centerX) {
							RSCharacter var3 = this.players[centerX];
							if (var3.projectileRange > 0) {
								RSCharacter var16 = null;
								if (var3.attackingNpcServerIndex == -1) {
									if (var3.attackingPlayerServerIndex != -1) {
										var16 = this.playerServer[var3.attackingPlayerServerIndex];
									}
								} else {
									var16 = this.npcsServer[var3.attackingNpcServerIndex];
								}

								if (null != var16) {
									int var5 = var3.currentX;
									int var6 = var3.currentZ;
									int var7 = -this.world.getElevation(var5, var6) - 110;
									int var8 = var16.currentX;
									int var9 = var16.currentZ;
									int var10 = -this.world.getElevation(var8, var9)
											- EntityHandler.getNpcDef(var3.npcId).getCamera2() / 2;
									int var11 = (var8 * (this.projectileMaxRange - var3.projectileRange)
											+ var5 * var3.projectileRange) / this.projectileMaxRange;
									int var12 = (var7 * var3.projectileRange
											+ var10 * (this.projectileMaxRange - var3.projectileRange))
											/ this.projectileMaxRange;
									int var13 = ((this.projectileMaxRange - var3.projectileRange) * var9
											+ var6 * var3.projectileRange) / this.projectileMaxRange;
									this.scene.drawSprite(var3.incomingProjectileSprite + mudclient.spriteProjectile, var13,
											0, var11, var12, 32, 32, (byte) 109);
									++this.spriteCount;
								}
							}
						}

						for (centerX = 0; this.npcCount > centerX; ++centerX) {
							RSCharacter var3 = this.npcs[centerX];
							int var4 = var3.currentX;
							int var5 = var3.currentZ;
							int var6 = -this.world.getElevation(var4, var5);
							int var7 = this.scene.drawSprite(20000 + centerX, var5, centerX + 30000, var4, var6,
									EntityHandler.getNpcDef(var3.npcId).getCamera1(),
									EntityHandler.getNpcDef(var3.npcId).getCamera2(), (byte) 109);
							++this.spriteCount;
							if (var3.direction == RSCharacterDirection.COMBAT_A) {
								this.scene.setCombatXOffset(86, var7, -30);
							}

							if (var3.direction == RSCharacterDirection.COMBAT_B) {
								this.scene.setCombatXOffset(var1 ^ 99, var7, 30);
							}
						}

						int centerZ;
						if (Config.SHOW_GROUND_ITEMS != 1) {

							for (centerX = 0; centerX < this.groundItemCount; ++centerX) {
								if(Config.SHOW_GROUND_ITEMS == 3
										&& (this.groundItemID[centerX] == 20 || this.groundItemID[centerX] == 814 || this.groundItemID[centerX] == 413 || this.groundItemID[centerX] == 604))
									continue;
								else if(Config.SHOW_GROUND_ITEMS == 2 && (this.groundItemID[centerX] != 20 && this.groundItemID[centerX] != 814 && this.groundItemID[centerX] != 413 && this.groundItemID[centerX] != 604)) {
									continue;
								}
								centerZ = this.groundItemX[centerX] * this.tileSize + 64;
								int var4 = this.tileSize * this.groundItemZ[centerX] + 64;
								this.scene.drawSprite('\u9c40' + this.groundItemID[centerX], var4, centerX + 20000, centerZ,
										-this.world.getElevation(centerZ, var4) - this.m_Le[centerX], 96, 64, (byte) 109);
								++this.spriteCount;
							}
						}
						for (centerX = 0; this.teleportBubbleCount > centerX; ++centerX) {
							centerZ = 64 + this.tileSize * this.teleportBubbleX[centerX];
							int var4 = this.teleportBubbleZ[centerX] * this.tileSize + 64;
							int var5 = this.teleportBubbleType[centerX];
							if (var5 == 0) {
								this.scene.drawSprite('\uc350' + centerX, var4, centerX + '\uc350', centerZ,
										-this.world.getElevation(centerZ, var4), 128, 256, (byte) 109);
								++this.spriteCount;
							}

							if (var5 == 1) {
								this.scene.drawSprite(centerX + '\uc350', var4, centerX + '\uc350', centerZ,
										-this.world.getElevation(centerZ, var4), 128, 64, (byte) 109);
								++this.spriteCount;
							}
						}

						this.getSurface().interlace = false;
						this.getSurface().blackScreen(true);
						this.getSurface().interlace = this.interlace;
						if (this.lastHeightOffset == 3) {
							centerX = 40 + (int) (3.0D * Math.random());
							centerZ = (int) (7.0D * Math.random()) + 40;
							this.scene.a(-50, centerZ, 0, -50, centerX, -10);
						}

						this.characterBubbleCount = 0;
						this.characterHealthCount = 0;
						this.characterDialogCount = 0;
						if (this.cameraAutoAngleDebug) {
							if (this.optionCameraModeAuto && !this.fogOfWar) {
								centerX = this.cameraAngle;
								this.autoRotateCamera((byte) 22);
								if (centerX != this.cameraAngle) {
									this.cameraAutoRotatePlayerZ = this.localPlayer.currentZ;
									this.cameraAutoRotatePlayerX = this.localPlayer.currentX;
								}
							}

							this.cameraRotation = this.cameraAngle * 32;
							if (fogOfWar) {
								this.scene.fogLandscapeDistance = 3000;
								this.scene.fogEntityDistance = 3000;
								this.scene.fogZFalloff = 1;
								this.scene.fogSmoothingStartDistance = 2800;
							} else {
								this.scene.fogZFalloff = 1;
								this.scene.fogLandscapeDistance = 10000;
								this.scene.fogEntityDistance = 10000;
								this.scene.fogSmoothingStartDistance = 10000;

							}
							centerX = this.cameraAutoRotatePlayerX + this.cameraRotationX;
							centerZ = this.cameraAutoRotatePlayerZ + this.cameraRotationZ;
							this.scene.setCamera(centerX, -this.world.getElevation(centerX, centerZ), centerZ, cameraPitch,
									this.cameraRotation * 4, (int) 0, 2000);
						} else {
							if (this.optionCameraModeAuto && !this.fogOfWar) {
								this.autoRotateCamera((byte) 94);
							}
							if (Config.SHOW_FOG) {
								if (!this.interlace) {
									this.scene.fogZFalloff = 1;
									this.scene.fogLandscapeDistance = 2400;
									this.scene.fogEntityDistance = 2400;
									this.scene.fogSmoothingStartDistance = 2300;
								} else {
									this.scene.fogZFalloff = 1;
									this.scene.fogLandscapeDistance = 2200;
									this.scene.fogEntityDistance = 2200;
									this.scene.fogSmoothingStartDistance = 2100;
								}
							} else {
								this.scene.fogZFalloff = 1;
								this.scene.fogLandscapeDistance = 10000;
								this.scene.fogEntityDistance = 10000;
								this.scene.fogSmoothingStartDistance = 10000;
							}

							centerX = this.cameraAutoRotatePlayerX + this.cameraRotationX;
							centerZ = this.cameraAutoRotatePlayerZ + this.cameraRotationZ;
							float zoomMultiplier = Config.ZOOM == 0 ? 0 : Config.ZOOM == 1 ? +200 : Config.ZOOM == 2 ? +400 : -200;
							this.scene.setCamera(centerX, -this.world.getElevation(centerX, centerZ), centerZ, cameraPitch,
									this.cameraRotation * 4, (int) 0, (int) (this.cameraZoom + zoomMultiplier) * 2);
						}

						this.scene.endScene(-113);
						this.drawCharacterOverlay();

						if (this.mouseClickXStep > 0) {
							this.getSurface().drawSprite(14 + mudclient.spriteMedia + (24 - this.mouseClickXStep) / 6,
									this.mouseWalkX - 8, this.mouseWalkY - 8);
						} else if (this.mouseClickXStep < 0) {
							this.getSurface().drawSprite(18 + mudclient.spriteMedia + (this.mouseClickXStep + 24) / 6,
									this.mouseWalkX - 8, this.mouseWalkY - 8);
						}

						if (this.systemUpdate != 0) {
							centerX = this.systemUpdate / 50;
							centerZ = centerX / 60;
							centerX %= 60;
							if (centerX < 10) {
								this.getSurface().drawColoredStringCentered(256,
										"System update in: " + centerZ + ":0" + centerX, 16776960, 0, 1,
										this.getGameHeight() - 7);
							} else {
								this.getSurface().drawColoredStringCentered(256,
										"System update in: " + centerZ + ":" + centerX, 16776960, 0, 1,
										this.getGameHeight() - 7);
							}
						}
						if (this.elixirTimer != 0) {
							centerX = this.elixirTimer / 50;
							centerZ = centerX / 60;
							centerX %= 60;
							if(inWild) {
								if (centerX < 10) {
									this.getSurface().drawColoredStringCentered(this.getGameWidth() - 53,
											"EXP Elixir: " + centerZ + ":0" + centerX, 0x9139e7, 0, 0,
											this.getGameHeight() - 62);
								} else {
									this.getSurface().drawColoredStringCentered(this.getGameWidth() - 53,
											"EXP Elixir: " + centerZ + ":" + centerX, 0x9139e7, 0, 0,
											this.getGameHeight() - 62);
								}
							} else if(!inWild) {
								if (centerX < 10) {
									this.getSurface().drawColoredStringCentered(this.getGameWidth() - 53,
											"EXP Elixir: " + centerZ + ":0" + centerX, 0x9139e7, 0, 1,
											this.getGameHeight() - 7);
								} else {
									this.getSurface().drawColoredStringCentered(this.getGameWidth() - 53,
											"EXP Elixir: " + centerZ + ":" + centerX, 0x9139e7, 0, 1,
											this.getGameHeight() - 7);
								}
							}
						}
						if(Config.KILL_FEED) {
							killQueue.clean();
							int Offset = 0;
							for(KillAnnouncer notify : killQueue.Kill) {
								int picture_width = 20;
								int width_killed = 507 - this.getSurface().stringWidth(1, notify.killedString);
								int width_icon = 507 - this.getSurface().stringWidth(1, notify.killedString) - picture_width - 5;
								int width_killer = 507 - this.getSurface().stringWidth(1, notify.killedString) - picture_width - 8 - this.getSurface().stringWidth(1, notify.killerString);

								this.getSurface().drawString(notify.killerString, width_killer, 50 + Offset, 0xffffff, 1);
								switch(notify.killPicture) {
								case -1:
									getSurface().drawSpriteClipping(mudclient.spriteProjectile + 1, width_icon, 36 + Offset, picture_width,
											18, 0, 0, false, 0, 1);
									break;
								case -2:
									getSurface().drawSpriteClipping(mudclient.spriteProjectile + 2, width_icon, 36 + Offset, picture_width,
											18, 0, 0, false, 0, 1);
									break;
								default:
									getSurface().drawSpriteClipping(mudclient.spriteItem + EntityHandler.getItemDef(notify.killPicture).getSprite(), width_icon, 36 + Offset, picture_width,
											18, EntityHandler.getItemDef(notify.killPicture).getPictureMask(), 0, false, 0, 1);
									break;
								}
								this.getSurface().drawString(notify.killedString, width_killed, 50 + Offset, 0xffffff, 1);
								Offset += 16;
							}
						}
						if (!this.loadingArea) {
							centerX = -this.playerLocalZ - this.worldOffsetZ - (this.midRegionBaseZ - 2203);
							if (this.worldOffsetX + this.playerLocalX + this.midRegionBaseX >= 2640) {
								centerX = -50;
							}

							if (centerX > 0) {
								inWild = true;
								centerZ = centerX / 6 + 1;
								this.getSurface().drawSprite(13 + mudclient.spriteMedia, this.getGameWidth() - 59, this.getGameHeight() - 56);
								this.getSurface().drawColoredStringCentered(this.getGameWidth() - 47, "Wilderness", 16776960, 0, 1,
										this.getGameHeight() - 20);
								this.getSurface().drawColoredStringCentered(this.getGameWidth() - 47, "Level: " + centerZ, 16776960, 0, 1,
										this.getGameHeight() - 7);
								if (this.showUiWildWarn == 0) {
									this.showUiWildWarn = 2;
								}
							} else {
								inWild = false;
							}

							if (this.showUiWildWarn == 0 && centerX > -10 && centerX <= 0) {
								this.showUiWildWarn = 1;
							}
						}
						if (Config.SIDE_MENU_OVERLAY) {
							int i = 130;
							if (adminRights) {
								this.getSurface().drawString("Tile: @gre@(@whi@" + (playerLocalX + midRegionBaseX)
										+ "@gre@,@whi@" + (playerLocalZ + midRegionBaseZ) + "@gre@)", 7, i, 0xffffff, 1);
								i += 14;
							}
							this.getSurface().drawString("FPS: @gre@(@whi@" + FPS + "@gre@)", 7, i, 0xffffff, 1);
							i += 14;
							this.getSurface().drawString(
									"Hits: " + this.playerStatCurrent[3] + "@gre@/@whi@" + this.playerStatBase[3], 7, i, 0xffffff, 1);
							i += 14;
							this.getSurface().drawString(
									"Prayer: " + this.playerStatCurrent[5] + "@gre@/@whi@" + this.playerStatBase[5], 7, i, 0xffffff, 1);
							i += 14;
							this.getSurface().drawString(
									"Fatigue: " + (double) (this.statFatigue * 1000 / 750) / 10.0 + "%", 7, i, 0xffffff, 1);
						}

						if (Config.EXPERIENCE_COUNTER == 2) {
							this.drawExperienceCounter(recentSkill);
						}

						if(Config.isAndroid()) {
							if(Config.F_SHOWING_KEYBOARD) {
								int uiX = 5;
								int uiY = 5;
								int uiWidth = 60;
								int uiHeight = 30;

								this.getSurface().drawBoxAlpha(uiX, uiY, uiWidth, uiHeight, 0x659CDE, 160);
								this.getSurface().drawBoxBorder(uiX, uiWidth, uiY, uiHeight, 0);
								this.getSurface().drawString("@whi@Global", uiX + 12, uiY + 20, 0xffffff, 1);
								if (this.mouseButtonClick != 0) {
									if (this.mouseX >= uiX && this.mouseX <= uiX + uiWidth && this.mouseY >= uiY && this.mouseY <= uiY + uiHeight) {
										this.mouseButtonClick = 0;
										this.panelMessageTabs.setText(this.panelMessageEntry, "::g ");
									}
								}
								uiX += uiWidth + 15;

								this.getSurface().drawBoxAlpha(uiX, uiY, uiWidth, uiHeight, 0x659CDE, 160);
								this.getSurface().drawBoxBorder(uiX, uiWidth, uiY, uiHeight, 0);
								this.getSurface().drawString("@whi@PK", uiX + 25, uiY + 20, 0xffffff, 1);
								if (this.mouseButtonClick != 0) {
									if (this.mouseX >= uiX && this.mouseX <= uiX + uiWidth && this.mouseY >= uiY && this.mouseY <= uiY + uiHeight) {
										this.mouseButtonClick = 0;
										this.panelMessageTabs.setText(this.panelMessageEntry, "::p ");
									}
								}
								uiX += uiWidth + 15;

								this.getSurface().drawBoxAlpha(uiX, uiY, uiWidth, uiHeight, 0xff0000, 160);
								this.getSurface().drawBoxBorder(uiX, uiWidth, uiY, uiHeight, 0);
								this.getSurface().drawString("@whi@Online", uiX + 12, uiY + 20, 0xffffff, 1);
								if (this.mouseButtonClick != 0) {
									if (this.mouseX >= uiX && this.mouseX <= uiX + uiWidth && this.mouseY >= uiY && this.mouseY <= uiY + uiHeight) {
										this.mouseButtonClick = 0;
										this.panelMessageTabs.setText(this.panelMessageEntry, "::online");
									}
								}
								uiX += uiWidth + 15;
								this.getSurface().drawBoxAlpha(uiX, uiY, uiWidth, uiHeight, 0xff0000, 160);
								this.getSurface().drawBoxBorder(uiX, uiWidth, uiY, uiHeight, 0);
								this.getSurface().drawString("@whi@Info", uiX + 20, uiY + 20, 0xffffff, 1);
								if (this.mouseButtonClick != 0) {
									if (this.mouseX >= uiX && this.mouseX <= uiX + uiWidth && this.mouseY >= uiY && this.mouseY <= uiY + uiHeight) {
										this.mouseButtonClick = 0;
										this.panelMessageTabs.setText(this.panelMessageEntry, "::info");
									}
								}


							}
						}

						if(Config.isAndroid()) {
							if(Config.F_SHOWING_KEYBOARD) {
								panelMessageTabs.reposition(panelMessageEntry, 7, 130+10, getGameWidth() - 14, 14);
							} else {
								panelMessageTabs.reposition(panelMessageEntry, 7, getGameHeight() - 10, getGameWidth() - 14, 14);
							}
						}

						if (this.messageTabSelected == MessageTab.ALL) {
							for (centerX = 0; centerX < messagesArray.length; ++centerX) {
								if (MessageHistory.messageHistoryTimeout[centerX] > 0) {
									String var17 = MessageHistory.messageHistoryColor[centerX]
											+ StringUtil.formatMessage(MessageHistory.messageHistoryMessage[centerX],
													MessageHistory.messageHistorySender[centerX], true,
													MessageHistory.messageHistoryType[centerX]);
									double boost = this.getGameHeight();
									if(Config.isAndroid() && Config.F_SHOWING_KEYBOARD)
										boost = (boost / 2.5) + 8;
									this.getSurface().drawColoredString(7, (int)boost - centerX * 12 - 18, var17,
											1, 16776960, MessageHistory.messageHistoryCrownID[centerX]);
								}
							}
						}






						LAST_FRAME_SHOWING_KEYBOARD = Config.F_SHOWING_KEYBOARD;
						this.panelMessageTabs.hide(this.panelMessageChat);
						this.panelMessageTabs.hide(this.panelMessageQuest);
						this.panelMessageTabs.hide(this.panelMessagePrivate);
						this.panelMessageTabs.hide(this.panelMessageClan);
						if (this.messageTabSelected == MessageTab.CHAT) {
							this.panelMessageTabs.show((int) this.panelMessageChat);
						} else if (this.messageTabSelected == MessageTab.QUEST) {
							this.panelMessageTabs.show((int) this.panelMessageQuest);
						} else if (this.messageTabSelected == MessageTab.PRIVATE) {
							this.panelMessageTabs.show((int) this.panelMessagePrivate);
						} else if (this.messageTabSelected == MessageTab.CLAN) {
							this.panelMessageTabs.show((int) this.panelMessageClan);
						}

						MiscFunctions.textListEntryHeightMod = 2;
						this.panelMessageTabs.drawPanel();
						MiscFunctions.textListEntryHeightMod = 0;
						this.getSurface().a(mudclient.spriteMedia, 0, this.getSurface().width2 - 200, 128, 3);
						this.drawUi(0);
						this.getSurface().loggedIn = false;
						this.drawChatMessageTabs(var1 - 8);
						// this.getSurface().draw(this.graphics, this.screenOffsetX,
						// 256, this.screenOffsetY);
						clientPort.draw();
					}
				}
			} catch (RuntimeException var14) {
				throw GenUtil.makeThrowable(var14, "client.AB(" + var1 + ')');
			}
		}

		public boolean LAST_FRAME_SHOWING_KEYBOARD = false;

		private final void drawInputX() {
			try {

				if (this.inputTextFinal.length() <= 0 && !this.inputX_OK) {
					if (this.inputX_Action.requiresNumeric()) {
						String str = "";

						for (int i = 0; this.inputTextCurrent.length() > i; ++i) {
							char var19 = this.inputTextCurrent.charAt(i);
							if (Character.isDigit(var19)) {
								str = str + var19;
							}
						}

						this.inputTextCurrent = str;
					}

					//int xr = (getGameWidth() - var2) / 2;
					//int yr = (getGameHeight() - var3) / 2;
					int xr = (getGameWidth() - this.inputX_Width) / 2;
					int yr = (getGameHeight() - this.inputX_Height) / 2;
					this.getSurface().drawBox(xr, yr, this.inputX_Width, this.inputX_Height, 0);
					this.getSurface().drawBoxBorder(xr, this.inputX_Width, yr, this.inputX_Height, 16777215);
					int lineHeightBase = this.getSurface().fontHeight(1);
					int inputLineHeight = this.getSurface().fontHeight(4);
					int lineHeight = lineHeightBase + 2;

					for (int i = 0; i < this.inputX_Lines.length; ++i) {
						this.getSurface().drawColoredStringCentered(200 + xr, this.inputX_Lines[i], 16776960, 0, 1,
								lineHeight * i + 5 + yr + lineHeightBase);
					}

					if (this.inputX_Focused) {
						this.getSurface().drawColoredStringCentered(200 + xr, this.inputTextCurrent + "*", 16777215, 0, 4,
								yr + 5 + lineHeight * this.inputX_Lines.length + 3 + inputLineHeight);
					}

					int okLineY = lineHeightBase + 8 + yr + inputLineHeight + 2 + this.inputX_Lines.length * lineHeight;

					int color = 16777215;
					if (this.mouseX > xr + 200 - 26 && this.mouseX < xr + 200 - 8 && okLineY - lineHeightBase < this.mouseY
							&& this.mouseY < okLineY) {
						color = 16776960;
						if (this.mouseButtonClick != 0) {
							this.inputX_OK = true;
							this.mouseButtonClick = 0;
							this.inputTextFinal = this.inputTextCurrent;
						}
					}
					this.getSurface().drawString("OK", 200 + xr - 26, okLineY, color, 1);

					color = 16777215;
					if (this.mouseX > xr + 208 && this.mouseX < xr + 208 + 40 && this.mouseY > okLineY - lineHeightBase
							&& this.mouseY < okLineY) {
						color = 16776960;
						if (this.mouseButtonClick != 0) {
							this.mouseButtonClick = 0;
							this.inputX_Action = InputXAction.ACT_0;
						}
					}
					this.getSurface().drawString("Cancel", 200 + xr + 8, okLineY, color, 1);

					if (this.mouseButtonClick == 1 && (this.mouseX < xr || this.inputX_Width + xr < this.mouseX
							|| this.mouseY < yr || this.mouseY > this.inputX_Height + yr)) {
						this.inputX_Action = InputXAction.ACT_0;
						this.mouseButtonClick = 0;
					}
				} else {
					String str = this.inputTextFinal.trim();
					this.inputTextCurrent = "";
					this.inputTextFinal = "";
					if (this.inputX_Action == InputXAction.TRADE_OFFER) {
						try {
							if (str.length() > 10) {
								str = str.substring(str.length() - 10, str.length());
							}
							int var4 = Integer.MAX_VALUE;
							long intOverflowCheck = Long.parseLong(str);
							if(intOverflowCheck < Integer.MAX_VALUE) {
								var4 = Integer.parseInt(str);
							}
							this.tradeOffer(var4, (int) this.tradeDoX_Slot);
						} catch (NumberFormatException var16) {
							System.out.println("Trade offer X number format exception: " + var16);
						}
					} else if (this.inputX_Action == InputXAction.TRADE_REMOVE) {
						try {
							if (str.length() > 10) {
								str = str.substring(str.length() - 10, str.length());
							}
							int var4 = Integer.MAX_VALUE;
							long intOverflowCheck = Long.parseLong(str);
							if(intOverflowCheck < Integer.MAX_VALUE) {
								var4 = Integer.parseInt(str);
							}
							this.tradeRemove(var4, (byte) 124, this.tradeDoX_Slot);
						} catch (NumberFormatException var15) {
							System.out.println("Trade remove X number format exception: " + var15);
						}
					} else if (this.inputX_Action == InputXAction.BANK_WITHDRAW) {
						try {
							if (this.bank.selectedBankSlot >= 0) {
								if (str.length() > 10) {
									str = str.substring(str.length() - 10, str.length());
								}
								int var4 = Integer.MAX_VALUE;
								long intOverflowCheck = Long.parseLong(str);
								if(intOverflowCheck < Integer.MAX_VALUE) {
									var4 = Integer.parseInt(str);
								}
								this.bank.lastXAmount = var4;
								bank.sendWithdraw(var4);
							}
						} catch (NumberFormatException var9) {
							System.out.println("Withdraw X number format exception: " + var9);
						}
					} else if (this.inputX_Action == InputXAction.BANK_DEPOSIT) {
						try {
							if (this.bank.selectedInventorySlot >= 0) {
								if (str.length() > 10) {
									str = str.substring(str.length() - 10, str.length());
								}
								int var4 = Integer.MAX_VALUE;
								long intOverflowCheck = Long.parseLong(str);
								if(intOverflowCheck < Integer.MAX_VALUE) {
									var4 = Integer.parseInt(str);
								}
								bank.sendDeposit(var4);
							}
						} catch (NumberFormatException var9) {
							System.out.println("Deposit X number format exception: " + var9);
						}
					} else if (this.inputX_Action == InputXAction.SHOP_BUY) {
						try {
							int id = this.shopItemID[this.shopSelectedItemIndex];
							if (id != -1) {
								if (str.length() > 10) {
									str = str.substring(str.length() - 10, str.length());
								}
								int var4 = Integer.MAX_VALUE;
								long intOverflowCheck = Long.parseLong(str);
								if(intOverflowCheck < Integer.MAX_VALUE) {
									var4 = Integer.parseInt(str);
								}
								this.getClientStream().newPacket(236);
								this.getClientStream().writeBuffer1.putShort(id);
								this.getClientStream().writeBuffer1
								.putShort(this.shopItemCount[this.shopSelectedItemIndex]);
								this.getClientStream().writeBuffer1.putShort(var4);
								this.getClientStream().finishPacket();
							}
						} catch (NumberFormatException var10) {
							System.out.println("Shop buy X number format exception: " + var10);
						}
					} else if (this.inputX_Action == InputXAction.SHOP_SELL) {
						try {
							int id = this.shopItemID[this.shopSelectedItemIndex];
							if (id != -1) {
								if (str.length() > 10) {
									str = str.substring(str.length() - 10, str.length());
								}
								int var4 = Integer.MAX_VALUE;
								long intOverflowCheck = Long.parseLong(str);
								if(intOverflowCheck < Integer.MAX_VALUE) {
									var4 = Integer.parseInt(str);
								}
								this.getClientStream().newPacket(221);
								this.getClientStream().writeBuffer1.putShort(this.shopItemID[this.shopSelectedItemIndex]);
								this.getClientStream().writeBuffer1
								.putShort(this.shopItemCount[this.shopSelectedItemIndex]);
								this.getClientStream().writeBuffer1.putShort(var4);
								this.getClientStream().finishPacket();
							}
						} catch (NumberFormatException var13) {
							System.out.println("Shop sell X number format exception: " + var13);
						}
					} else if (this.inputX_Action == InputXAction.DUEL_STAKE) {
						try {
							if (str.length() > 10) {
								str = str.substring(str.length() - 10, str.length());
							}
							int var4 = Integer.MAX_VALUE;
							long intOverflowCheck = Long.parseLong(str);
							if(intOverflowCheck < Integer.MAX_VALUE) {
								var4 = Integer.parseInt(str);
							}
							this.duelStakeItem(var4, this.duelDoX_Slot);
						} catch (NumberFormatException var12) {
							System.out.println("Stake add X number format exception: " + var12);
						}
					} else if (this.inputX_Action == InputXAction.DUEL_REMOVE) {
						try {
							if (str.length() > 10) {
								str = str.substring(str.length() - 10, str.length());
							}
							int var4 = Integer.MAX_VALUE;
							long intOverflowCheck = Long.parseLong(str);
							if(intOverflowCheck < Integer.MAX_VALUE) {
								var4 = Integer.parseInt(str);
							}
							this.duelRemoveItem(this.duelDoX_Slot, var4);
						} catch (NumberFormatException var11) {
							System.out.println("Stake remove X number format exception: " + var11);
						}
					} else if (this.inputX_Action == InputXAction.SKIP_TUTORIAL) {
						this.getClientStream().newPacket(84);
						this.getClientStream().finishPacket();
					} else if (this.inputX_Action == InputXAction.DROP_X) {
						try {
							if (str.length() > 10) {
								str = str.substring(str.length() - 10, str.length());
							}
							int var4 = Integer.MAX_VALUE;
							long intOverflowCheck = Long.parseLong(str);
							if(intOverflowCheck < Integer.MAX_VALUE) {
								var4 = Integer.parseInt(str);
							}
							this.getClientStream().newPacket(246);
							this.getClientStream().writeBuffer1.putShort(dropInventorySlot);
							this.getClientStream().writeBuffer1.putInt(var4);
							this.getClientStream().finishPacket();
							dropInventorySlot = -1;
						} catch (NumberFormatException var13) {
							System.out.println("Drop X number format exception: " + var13);
						}
					} else if (this.inputX_Action == InputXAction.INVITE_CLAN_PLAYER) {
						this.getClientStream().newPacket(199);
						this.getClientStream().writeBuffer1.putByte(11);
						this.getClientStream().writeBuffer1.putByte(2);
						this.getClientStream().writeBuffer1.putString(str);
						this.getClientStream().finishPacket();
					}  else if (this.inputX_Action == InputXAction.KICK_CLAN_PLAYER) {
						this.getClientStream().newPacket(199);
						this.getClientStream().writeBuffer1.putByte(11);
						this.getClientStream().writeBuffer1.putByte(5);
						this.getClientStream().writeBuffer1.putString(clanKickPlayer);
						this.getClientStream().finishPacket();
					} else if (this.inputX_Action == InputXAction.CLAN_DELEGATE_LEADERSHIP) {
						this.getClientStream().newPacket(199);
						this.getClientStream().writeBuffer1.putByte(11);
						this.getClientStream().writeBuffer1.putByte(6);
						this.getClientStream().writeBuffer1.putString(clanKickPlayer);
						this.getClientStream().writeBuffer1.putByte(1);
						this.getClientStream().finishPacket();
					} else if (this.inputX_Action == InputXAction.CLAN_LEAVE) {
						clan.getClanInterface().sendClanLeave();
					}
					this.inputX_Action = InputXAction.ACT_0;
				}
			} catch (RuntimeException var17) {
				throw GenUtil.makeThrowable(var17, "client.PB(" + "dummy" + ')');
			}
		}

		public final void drawItemAt(int id, int x, int y, int width, int height, int var2) {
			try {


				int sprite = EntityHandler.getItemDef(id).getSprite() + mudclient.spriteItem;
				int mask = EntityHandler.getItemDef(id).getPictureMask();
				this.getSurface().drawSpriteClipping(sprite, x, y, width, height, mask, 0, false, 0, 1);

				ItemDef def = EntityHandler.getItemDef(id);
				if (def.getNotedFormOf() >= 0) {
					ItemDef originalDef = EntityHandler.getItemDef(def.getNotedFormOf());
					getSurface().drawSpriteClipping(mudclient.spriteItem + originalDef.getSprite(), x + 7, y + 4, width / 2 + 5,
							height / 2 + 4, originalDef.getPictureMask(), 0, false, 0, 1);
				}

			} catch (RuntimeException var10) {
				throw GenUtil.makeThrowable(var10, "client.CA(" + height + ',' + var2 + ',' + x + ',' + id + ',' + width
						+ ',' + "dummy" + ',' + y + ')');
			}
		}

		private final void drawLogin() {
			try {
				this.getSurface().interlace = false;
				this.welcomeScreenShown = false;

				this.getSurface().blackScreen(true);

				if (this.loginScreenNumber == 0 || this.loginScreenNumber == 2 || this.loginScreenNumber == 3) {
					int var2 = this.frameCounter * 2 % 3072;
					if (var2 < 1024) {
						this.getSurface().drawSprite(mudclient.spriteLogo, 0, Config.isAndroid() ? 140 : 10);
						if (var2 > 768) {
							this.getSurface().a(1 + mudclient.spriteLogo, 0, 0, var2 - 768, Config.isAndroid() ? 140 : 10);
						}
					} else if (var2 < 2048) {
						this.getSurface().drawSprite(1 + mudclient.spriteLogo, 0, Config.isAndroid() ? 140 : 10);
						if (var2 > 1792) {
							this.getSurface().a(mudclient.spriteMedia + 10, 0, 0, var2 - 1792, Config.isAndroid() ? 140 : 10);
						}
					} else {
						this.getSurface().drawSprite(mudclient.spriteMedia + 10, 0, Config.isAndroid() ? 140 : 10);
						if (var2 > 2816) {
							this.getSurface().a(mudclient.spriteLogo, 0, 0, var2 - 2816, Config.isAndroid() ? 140 : 10);
						}
					}
				}

				if (this.loginScreenNumber == 0) {
					this.panelLoginWelcome.drawPanel();
				}
				if (this.loginScreenNumber == 1) {
					menuNewUser.drawPanel();
				}
				if (this.loginScreenNumber == 2) {
					String var4 = this.panelLogin.getControlText(this.controlLoginStatus1);
					if (null != var4 && var4.length() > 0) {
						this.getSurface().drawBoxAlpha(0, 185, this.getGameWidth(), 30, 0, 100);
					}

					this.panelLogin.drawPanel();
				}

				this.getSurface().drawSprite(mudclient.spriteMedia + 22, 0, this.gameHeight);
				// this.getSurface().draw(this.graphics, this.screenOffsetX, 256,
				// this.screenOffsetY);
				clientPort.draw();
			} catch (RuntimeException var3) {
				throw GenUtil.makeThrowable(var3, "client.R(" + 2540 + ')');
			}
		}

		public final void drawMenu() {
			try {

				if (this.mouseButtonClick == 0) {
					int width = this.menuCommon.getWidth();
					int height = this.menuCommon.getHeight();
					boolean renderAnyway = false;
					// Maybe my brain is fried from all the alcohol or the work-out
					// I had today but this is the best I can do right now.
					// This should be one line thing!!!!
					// IF android build and hold and choose = dont close when mouse
					// exits, else close it.
					if (Config.isAndroid() && Config.HOLD_AND_CHOOSE) {
						renderAnyway = true;
					}
					if (renderAnyway) {
						this.menuCommon.render(this.menuY, this.menuX, this.mouseY, (byte) -12, this.mouseX);
					} else {
						if (this.menuX - 10 <= this.mouseX && this.menuY - 10 <= this.mouseY
								&& width + this.menuX + 10 >= this.mouseX && this.mouseY <= 10 + this.menuY + height) {
							this.menuCommon.render(this.menuY, this.menuX, this.mouseY, (byte) -12, this.mouseX);
						} else {
							this.topMouseMenuVisible = false;
						}
					}

				} else {
					int var2 = this.menuCommon.handleClick(this.mouseX, this.menuX, this.menuY, this.mouseY);
					if (var2 >= 0) {
						this.handleMenuItemClicked(false, var2);
					}

					this.topMouseMenuVisible = false;
					this.mouseButtonClick = 0;
				}
			} catch (RuntimeException var4) {
				throw GenUtil.makeThrowable(var4, "client.DB(" + "dummy" + ')');
			}
		}

		private final boolean drawMenuMessage(String otherAccountName, int var2, String otherDisplayName) {
			try {
				if(inWild) {
					return false;
				}
				String otherKey = StringUtil.displayNameToKey(otherAccountName);
				if (null == otherKey) {
					return false;
				} else if (var2 <= 126) {
					return true;
				} else if (otherKey.equals(StringUtil.displayNameToKey(this.localPlayer.accountName))) {
					return false;
				} else {
					boolean isOnFriendList = false;
					boolean isOnline = false;

					for (int i = 0; i < SocialLists.friendListCount; ++i) {
						if (otherKey.equals(StringUtil.displayNameToKey(SocialLists.friendList[i]))) {
							isOnFriendList = true;
							if ((4 & SocialLists.friendListArg[i]) != 0) {
								isOnline = true;
							}
							break;
						}
					}

					if (isOnFriendList) {
						if (isOnline) {
							this.menuCommon.addItem_With2Strings("Message", "@whi@" + otherDisplayName, otherDisplayName,
									MenuItemAction.CHAT_MESSAGE, otherAccountName);
						}
					} else {
						boolean onIgnoreList = false;

						for (int i = 0; i < SocialLists.ignoreListCount; ++i) {
							if (otherKey.equals(StringUtil.displayNameToKey(SocialLists.ignoreList[i]))) {
								onIgnoreList = true;
								break;
							}
						}

						if (!onIgnoreList) {
							this.menuCommon.addItem_With2Strings("Add friend", "@whi@" + otherDisplayName, otherDisplayName,
									MenuItemAction.CHAT_ADD_FRIEND, otherAccountName);
							this.menuCommon.addItem_With2Strings("Add ignore", "@whi@" + otherDisplayName, otherDisplayName,
									MenuItemAction.CHAT_ADD_IGNORE, otherAccountName);
						}
					}

					this.menuCommon.addItem_With2Strings("Report abuse", "@whi@" + otherDisplayName, otherDisplayName,
							MenuItemAction.REPORT_ABUSE, otherAccountName);
					return true;
				}
			} catch (RuntimeException var9) {
				throw GenUtil.makeThrowable(var9, "client.QA(" + (otherAccountName != null ? "{...}" : "null") + ',' + var2
						+ ',' + (otherDisplayName != null ? "{...}" : "null") + ')');
			}
		}

		private final void drawMinimapEntity(int val, int x, byte var3, int y) {
			try {
				this.getSurface().setPixel(x, y, val);

				this.getSurface().setPixel(x - 1, y, val);
				if (var3 <= -32) {
					this.getSurface().setPixel(1 + x, y, val);
					this.getSurface().setPixel(x, y - 1, val);
					this.getSurface().setPixel(x, y + 1, val);
				}
			} catch (RuntimeException var6) {
				throw GenUtil.makeThrowable(var6, "client.D(" + val + ',' + x + ',' + var3 + ',' + y + ')');
			}
		}

		public final void drawNPC(int npcIndex, int x, int y, int width1, int height, int topPixelSkew, int var3,
				int overlayMovement) {
			try {

				RSCharacter npc = this.npcs[npcIndex];
				NPCDef def = EntityHandler.getNpcDef(npc.npcId);
				int var11 = 7 & npc.direction.rsDir + (this.cameraRotation + 16) / 32;
				boolean var12 = false;
				int var13 = var11;
				if (var11 != 5) {
					if (var11 != 6) {
						if (var11 == 7) {
							var12 = true;
							var13 = 1;
						}
					} else {
						var12 = true;
						var13 = 2;
					}
				} else {
					var12 = true;
					var13 = 3;
				}

				int var14 = this.animFrameToSprite_Walk[npc.stepFrame / def.getWalkModel() % 4] + var13 * 3;
				if (npc.direction == RSCharacterDirection.COMBAT_A) {
					var12 = false;
					var13 = 5;
					x -= overlayMovement * def.getCombatSprite() / 100;
					var11 = 2;
					var14 = var13 * 3 + this.animFrameToSprite_CombatA[this.frameCounter / (def.getCombatModel() - 1) % 8];
				} else if (npc.direction == RSCharacterDirection.COMBAT_B) {
					var13 = 5;
					var11 = 2;
					var12 = true;
					x += def.getCombatSprite() * overlayMovement / 100;
					var14 = this.animFrameToSprite_CombatB[this.frameCounter / def.getCombatModel() % 8] + var13 * 3;
				}

				int var15;
				int var16;
				for (var15 = 0; var15 < 12; ++var15) {
					var16 = this.getAnimDirLayer_To_CharLayer()[var11][var15];
					int animID = def.getSprite(var16);
					AnimationDef animationDef = EntityHandler.getAnimationDef(animID);
					if (animID >= 0) {
						byte spriteOffsetX = 0;
						byte spriteOffsetY = 0;
						int variant = var14;
						if (var12 && var13 >= 1 && var13 <= 3 && animationDef.hasF()) {
							variant = var14 + 15;
						}
						if (var13 != 5 || animationDef.hasA()) {
							int sprite = variant + animationDef.getNumber();

							int something1 = this.getSurface().sprites[sprite].getSomething1();
							int something2 = this.getSurface().sprites[sprite].getSomething2();
							int something3 = this.getSurface().sprites[EntityHandler.getAnimationDef(animID).getNumber()]
									.getSomething1();
							if (something1 != 0 && something2 != 0 && something3 != 0) {
								int xOffset = (spriteOffsetX * width1) / something1;
								int yOffset = (spriteOffsetY * height) / something2;
								int spriteWidth = (something1 * width1) / something3;
								xOffset -= (spriteWidth - width1) / 2;
								int colorVariant = animationDef.getCharColour();// CacheValues.animationCharacterColour[animID];
								int baseColor = 0;
								if (colorVariant == 1) {
									baseColor = def.getSkinColour();// CacheValues.npcColourSkin[npc.npcId];
									colorVariant = def.getHairColour();// CacheValues.npcColourHair[npc.npcId];
								} else if (colorVariant != 2) {
									if (colorVariant == 3) {
										baseColor = def.getSkinColour();// CacheValues.npcColourSkin[npc.npcId];
										colorVariant = def.getBottomColour();// CacheValues.npcColourBottom[npc.npcId];
									}
								} else {
									colorVariant = def.getTopColour();// CacheValues.npcColourTop[npc.npcId];
									baseColor = def.getSkinColour();// CacheValues.npcColourSkin[npc.npcId];
								}

								this.getSurface().drawSpriteClipping(sprite, xOffset + x, yOffset + y, spriteWidth, height,
										colorVariant, baseColor, var12, topPixelSkew, 1);
							}
						}
					}
				}

				if (npc.messageTimeout > 0) {
					this.characterDialogHalfWidth[this.characterDialogCount] = this.getSurface().stringWidth(1, npc.message)
							/ 2;
					if (this.characterDialogHalfWidth[this.characterDialogCount] > 150) {
						this.characterDialogHalfWidth[this.characterDialogCount] = 150;
					}

					this.characterDialogHeight[this.characterDialogCount] = this.getSurface().stringWidth(1, npc.message)
							/ 300 * this.getSurface().fontHeight(1);
					this.characterDialogX[this.characterDialogCount] = width1 / 2 + x;
					this.characterDialogY[this.characterDialogCount] = y;
					this.characterDialogString[this.characterDialogCount++] = npc.message;
				}

				if (npc.direction == RSCharacterDirection.COMBAT_A || npc.direction == RSCharacterDirection.COMBAT_B
						|| npc.combatTimeout != 0) {
					if (npc.combatTimeout > 0) {
						var15 = x;
						if (npc.direction == RSCharacterDirection.COMBAT_B) {
							var15 = x + overlayMovement * 20 / 100;
						} else if (npc.direction == RSCharacterDirection.COMBAT_A) {
							var15 = x - overlayMovement * 20 / 100;
						}

						var16 = npc.healthCurrent * 30 / npc.healthMax;
						this.characterHealthX[this.characterHealthCount] = width1 / 2 + var15;
						this.characterHealthY[this.characterHealthCount] = y;
						this.characterHealthBar[this.characterHealthCount++] = var16;
					}

					if (npc.combatTimeout > 150) {
						var15 = x;
						if (npc.direction == RSCharacterDirection.COMBAT_B) {
							var15 = x + overlayMovement * 10 / 100;
						} else if (npc.direction == RSCharacterDirection.COMBAT_A) {
							var15 = x - overlayMovement * 10 / 100;
						}

						this.getSurface().drawSprite(mudclient.spriteMedia + 12, var15 - (12 - width1 / 2),
								y + height / 2 - 12);
						this.getSurface().drawColoredStringCentered(width1 / 2 - 1 + var15, "" + npc.damageTaken, 16777215,
								0, 3, 5 + y + height / 2);
					}

				}
			} catch (RuntimeException var28) {
				throw GenUtil.makeThrowable(var28, "client.EC(" + y + ',' + topPixelSkew + ',' + var3 + ',' + height + ','
						+ overlayMovement + ',' + npcIndex + ',' + width1 + ',' + x + ')');
			}
		}

		public final void drawPlayer(int index, int x, int y, int width, int height, int topPixelSkew, int var3,
				int overlayMovement) {
			try {

				if (var3 != 20) {
					this.resetLoginScreenVariables((byte) -115);
				}

				RSCharacter player = this.players[index];
				if (player.colourBottom != 255) {
					int wantedAnimDir = (player.direction.rsDir + ((this.cameraRotation + 16) / 32)) & 7;
					boolean mirrorX = false;
					int actualAnimDir = wantedAnimDir;
					if (wantedAnimDir == 5) {
						mirrorX = true;
						actualAnimDir = 3;
					} else if (wantedAnimDir == 7) {
						actualAnimDir = 1;
						mirrorX = true;
					} else if (wantedAnimDir == 6) {
						actualAnimDir = 2;
						mirrorX = true;
					}

					int spriteOffset = this.animFrameToSprite_Walk[player.stepFrame / 6 % 4] + actualAnimDir * 3;
					if (player.direction == RSCharacterDirection.COMBAT_B) {
						wantedAnimDir = 2;
						actualAnimDir = 5;
						x += overlayMovement * 5 / 100;
						mirrorX = true;
						spriteOffset = this.animFrameToSprite_CombatB[this.frameCounter / 6 % 8] + actualAnimDir * 3;
					} else if (player.direction == RSCharacterDirection.COMBAT_A) {
						x -= overlayMovement * 5 / 100;
						actualAnimDir = 5;
						mirrorX = false;
						wantedAnimDir = 2;
						spriteOffset = this.animFrameToSprite_CombatA[this.frameCounter / 5 % 8] + actualAnimDir * 3;
					}

					for (int lay = 0; lay < 12; ++lay) {
						int mappedLayer = this.getAnimDirLayer_To_CharLayer()[wantedAnimDir][lay];
						int animID = player.layerAnimation[mappedLayer] - 1;
						if (animID >= 0) {
							byte spriteOffsetX = 0;
							byte spriteOffsetY = 0;
							int mySpriteOffset = spriteOffset;
							if (mirrorX && actualAnimDir >= 1 && actualAnimDir <= 3) {
								if (EntityHandler.getAnimationDef(animID).hasF()) {
									mySpriteOffset = spriteOffset + 15;
								} else if (mappedLayer == 4 && actualAnimDir == 1) {
									mySpriteOffset = actualAnimDir * 3
											+ this.animFrameToSprite_Walk[(player.stepFrame / 6 + 2) % 4];
									spriteOffsetY = -3;
									spriteOffsetX = -22;
								} else if (mappedLayer == 4 && actualAnimDir == 2) {
									spriteOffsetX = 0;
									spriteOffsetY = -8;
									mySpriteOffset = this.animFrameToSprite_Walk[(player.stepFrame / 6 + 2) % 4]
											+ actualAnimDir * 3;
								} else if (mappedLayer == 4 && actualAnimDir == 3) {
									spriteOffsetY = -5;
									mySpriteOffset = actualAnimDir * 3
											+ this.animFrameToSprite_Walk[(2 + player.stepFrame / 6) % 4];
									spriteOffsetX = 26;
								} else if (mappedLayer == 3 && actualAnimDir == 1) {
									mySpriteOffset = actualAnimDir * 3
											+ this.animFrameToSprite_Walk[(2 + player.stepFrame / 6) % 4];
									spriteOffsetX = 22;
									spriteOffsetY = 3;
								} else if (mappedLayer == 3 && actualAnimDir == 2) {
									spriteOffsetY = 8;
									mySpriteOffset = actualAnimDir * 3
											+ this.animFrameToSprite_Walk[(player.stepFrame / 6 + 2) % 4];
									spriteOffsetX = 0;
								} else if (mappedLayer == 3 && actualAnimDir == 3) {
									spriteOffsetX = -26;
									mySpriteOffset = this.animFrameToSprite_Walk[(2 + player.stepFrame / 6) % 4]
											+ actualAnimDir * 3;
									spriteOffsetY = 5;
								}
							}

							if (actualAnimDir != 5 || EntityHandler.getAnimationDef(animID).hasA()) {
								int sprite = EntityHandler.getAnimationDef(animID).getNumber() + mySpriteOffset;
								int something1 = this.getSurface().sprites[sprite].getSomething1();
								int something2 = this.getSurface().sprites[sprite].getSomething2();
								int something3 = this.getSurface().sprites[EntityHandler.getAnimationDef(animID)
								                                           .getNumber()].getSomething1();
								if (something1 != 0 && something2 != 0 && something3 != 0) {
									int xOffset = (spriteOffsetX * width) / something1;
									int yOffset = (spriteOffsetY * height) / something2;
									int spriteWidth = (something1 * width) / something3;
									xOffset -= (spriteWidth - width) / 2;
									int colorMask1 = EntityHandler.getAnimationDef(animID).getCharColour();
									if (colorMask1 == 1) {
										colorMask1 = this.getPlayerHairColors()[player.colourHair];
									} else if (colorMask1 == 2) {
										colorMask1 = this.getPlayerClothingColors()[player.colourTop];
									} else if (colorMask1 == 3) {
										colorMask1 = this.getPlayerClothingColors()[player.colourBottom];
									}

									int colorMask2 = this.getPlayerSkinColors()[player.colourSkin];
									this.getSurface().drawSpriteClipping(sprite, xOffset + x, y + yOffset, spriteWidth,
											height, colorMask1, colorMask2, mirrorX, topPixelSkew, 1);

									/*if(sprite != 1948 && sprite != 1947 && sprite != 1949) {
										this.getSurface().drawSpriteClipping(sprite, xOffset + x, y + yOffset, spriteWidth,
												height, colorMask1, colorMask2, mirrorX, topPixelSkew, 1);
									}
									if(sprite == 1948 || sprite == 1947 || sprite == 1949) {
										this.getSurface().drawSpriteClipping(sprite, xOffset + x, y + yOffset, spriteWidth,
												height, colorMask1, colorMask2, mirrorX, topPixelSkew, 1);
									}*/
								}
							}

							//System.out.println("Animation dir: " + (EntityHandler.getAnimationDef(animID).getNumber() + spriteOffset));
						}
					}


					if (player.messageTimeout > 0) {
						this.characterDialogHalfWidth[this.characterDialogCount] = this.getSurface().stringWidth(1,
								player.message) / 2;
						if (this.characterDialogHalfWidth[this.characterDialogCount] > 150) {
							this.characterDialogHalfWidth[this.characterDialogCount] = 150;
						}

						this.characterDialogHeight[this.characterDialogCount] = this.getSurface().stringWidth(1,
								player.message) / 300 * this.getSurface().fontHeight(1);
						this.characterDialogX[this.characterDialogCount] = width / 2 + x;
						this.characterDialogY[this.characterDialogCount] = y;
						this.characterDialogString[this.characterDialogCount++] = player.message;
					}

					if (Config.SHOW_FLOATING_NAMETAGS) {
						if (Config.NAME_CLAN_TAG_OVERLAY && this.showUiTab == 0) {
							if (player.displayName != null)
								this.getSurface().drawShadowText(player.displayName, (width - this.getSurface().stringWidth(0, player.displayName)) / 2 + x + 1, y - 14, 0xffff00, 0, false);
						}
						if (Config.NAME_CLAN_TAG_OVERLAY && this.showUiTab == 0) {
							if (player.clanTag != null)
								this.getSurface().drawColoredString((width - this.getSurface().stringWidth(0, "< " + player.clanTag + " >")) / 2 + x + 1, y - 5, "< " + player.clanTag + " >", 0, 0x7CADDA, 0);
						}
					}


					if (player.bubbleTimeout > 0) {
						this.characterBubbleX[this.characterBubbleCount] = x + width / 2;
						this.characterBubbleY[this.characterBubbleCount] = y;
						this.characterBubbleScale[this.characterBubbleCount] = overlayMovement;
						this.characterBubbleID[this.characterBubbleCount++] = player.bubbleItem;
					}

					if (player.direction == RSCharacterDirection.COMBAT_A
							|| player.direction == RSCharacterDirection.COMBAT_B || player.combatTimeout != 0) {
						if (player.combatTimeout > 0) {
							int var14 = x;
							if (player.direction == RSCharacterDirection.COMBAT_B) {
								var14 = x + overlayMovement * 20 / 100;
							} else if (player.direction == RSCharacterDirection.COMBAT_A) {
								var14 = x - overlayMovement * 20 / 100;
							}

							int healthStep = player.healthCurrent * 30 / player.healthMax;
							this.characterHealthX[this.characterHealthCount] = width / 2 + var14;
							this.characterHealthY[this.characterHealthCount] = y;
							this.characterHealthBar[this.characterHealthCount++] = healthStep;
						}

						if (player.combatTimeout > 150) {
							int var14 = x;
							if (player.direction == RSCharacterDirection.COMBAT_B) {
								var14 = x + overlayMovement * 10 / 100;
							} else if (player.direction == RSCharacterDirection.COMBAT_A) {
								var14 = x - overlayMovement * 10 / 100;
							}

							this.getSurface().drawSprite(mudclient.spriteMedia + 11, width / 2 + var14 - 12,
									height / 2 + (y - 12));
							this.getSurface().drawColoredStringCentered(width / 2 + (var14 - 1), "" + player.damageTaken,
									16777215, 0, 3, height / 2 + y + 5);
						}
					}

					if (player.skullVisible == 1 && player.bubbleTimeout == 0) {
						int skullX = topPixelSkew + x + width / 2;
						if (player.direction == RSCharacterDirection.COMBAT_A) {
							skullX -= overlayMovement * 20 / 100;
						} else if (player.direction == RSCharacterDirection.COMBAT_B) {
							skullX += overlayMovement * 20 / 100;
						}

						int destWidth = overlayMovement * 16 / 100;
						int destHeight = overlayMovement * 16 / 100;
						this.getSurface().drawSprite(13 + mudclient.spriteMedia, skullX - destWidth / 2,
								y - destHeight / 2 - overlayMovement * 10 / 100, destWidth, destHeight, 5924);
					} else if (player.skullVisible == 2 && player.bubbleTimeout == 0) {
						int skullX = topPixelSkew + x + width / 2;
						if (player.direction == RSCharacterDirection.COMBAT_A) {
							skullX -= overlayMovement * 20 / 100;
						} else if (player.direction == RSCharacterDirection.COMBAT_B) {
							skullX += overlayMovement * 20 / 100;
						}

						int destWidth = overlayMovement * 16 / 100;
						int destHeight = overlayMovement * 16 / 100;
						getSurface().drawSpriteClipping(13 + mudclient.spriteMedia, skullX - destWidth / 2,
								y - destHeight / 2 - overlayMovement * 10 / 100, destWidth, destHeight, 0xFF0000, 0, false,
								0, 0);

						//
						// this.getSurface().drawSprite(13 + mudclient.spriteMedia,
						// skullX - destWidth / 2,
						// y - destHeight / 2 - overlayMovement * 10 / 100,
						// destWidth, destHeight, 5924);
					}

				}
			} catch (RuntimeException var27) {
				var27.printStackTrace();
				throw GenUtil.makeThrowable(var27, "client.OB(" + topPixelSkew + ',' + width + ',' + var3 + ','
						+ overlayMovement + ',' + x + ',' + y + ',' + height + ',' + index + ')');
			}

		}

		private final void drawPopupReport(boolean var1) {
			try {

				if (this.inputTextFinal.length() > 0) {
					this.reportAbuse_Name = this.inputTextFinal.trim();
					this.reportAbuse_AbuseType = 0;
					this.reportAbuse_State = 2;
				} else {
					byte var2 = 0;
					if (this.m_Ce < 2 && this.m_Oj < 7) {
						if (this.m_Oj >= 5) {
							var2 = 1;
						}
					} else {
						var2 = 2;
					}

					int var3 = this.getSurface().fontHeight(1);
					int var4 = this.getSurface().fontHeight(4);
					short var5 = 400;
					int var6 = (var2 > 0 ? 5 + var3 : 0) + 70;
					int var7 = 256 - var5 / 2;
					int var8 = 180 - var6 / 2;
					this.getSurface().drawBox(var7, var8, var5, var6, 0);
					this.getSurface().drawBoxBorder(var7, var5, var8, var6, 16777215);
					this.getSurface().drawColoredStringCentered(256, "Enter the name of the player you wish to report:",
							16776960, 0, 1, 5 + var8 + var3);
					int var9 = var3 + 2;
					this.getSurface().drawColoredStringCentered(256, this.inputTextCurrent + "*", 16777215, 0, 4,
							var4 + var8 + 5 + var9 + 3);
					int var10 = var3 + var4 + 8 + var8 + var9 + 2;
					int var11 = 16777215;
					if (var2 > 0) {
						String var12 = this.m_ue ? "[X]" : "[ ]";
						if (var2 > 1) {
							var12 = var12 + " Mute player";
						} else {
							var12 = var12 + " Suggest mute";
						}

						int var13 = this.getSurface().stringWidth(1, var12);
						if (this.mouseX > 256 - var13 / 2 && this.mouseX < var13 / 2 + 256 && var10 - var3 < this.mouseY
								&& var10 > this.mouseY) {
							if (this.mouseButtonClick != 0) {
								this.m_ue = !this.m_ue;
								this.mouseButtonClick = 0;
							}

							var11 = 16776960;
						}

						this.getSurface().drawColoredStringCentered(256, var12, var11, 0, 1, var10);
						var10 += 10 + var3;
					}

					var11 = 16777215;
					if (this.mouseX > 210 && this.mouseX < 228 && var10 - var3 < this.mouseY && var10 > this.mouseY) {
						if (this.mouseButtonClick != 0) {
							this.inputTextFinal = this.inputTextCurrent;
							this.mouseButtonClick = 0;
						}

						var11 = 16776960;
					}

					this.getSurface().drawString("OK", 210, var10, var11, 1);
					var11 = 16777215;
					if (this.mouseX > 264 && this.mouseX < 304 && this.mouseY > var10 - var3 && var10 > this.mouseY) {
						var11 = 16776960;
						if (this.mouseButtonClick != 0) {
							this.mouseButtonClick = 0;
							this.reportAbuse_State = 0;
						}
					}

					this.getSurface().drawString("Cancel", 264, var10, var11, 1);
					if (this.mouseButtonClick == 1 && (this.mouseX < var7 || this.mouseX > var7 + var5 || var8 > this.mouseY
							|| var8 + var6 < this.mouseY)) {
						this.reportAbuse_State = 0;
						this.mouseButtonClick = 0;
					}

				}
			} catch (RuntimeException var14) {
				throw GenUtil.makeThrowable(var14, "client.F(" + var1 + ')');
			}
		}

		private final void drawPopupSocial() {
			try {

				int x = 106;
				int y = 145;

				if (Config.isAndroid())
					y = 75;

				if (this.mouseButtonClick != 0) {
					this.mouseButtonClick = 0;
					if (this.panelSocialPopup_Mode == SocialPopupMode.ADD_FRIEND
							&& (this.mouseX < x || this.mouseY < y || this.mouseX > x + 300 || this.mouseY > y + 70)) {
						this.panelSocialPopup_Mode = SocialPopupMode.NONE;
						return;
					}

					if (this.panelSocialPopup_Mode == SocialPopupMode.MESSAGE_FRIEND
							&& (this.mouseX < 6 || this.mouseY < y || this.mouseX > 506 || this.mouseY > +70)) {
						this.panelSocialPopup_Mode = SocialPopupMode.NONE;
						return;
					}

					if (this.panelSocialPopup_Mode == SocialPopupMode.ADD_IGNORE
							&& (this.mouseX < x || this.mouseY < y || this.mouseX > 406 || this.mouseY > +70)) {
						this.panelSocialPopup_Mode = SocialPopupMode.NONE;
						return;
					}

					if (this.mouseX > x + 130 && this.mouseX < x + 270 && this.mouseY > y + 48 && this.mouseY < y + 68) {
						this.panelSocialPopup_Mode = SocialPopupMode.NONE;
						return;
					}
				}
				if (this.panelSocialPopup_Mode == SocialPopupMode.ADD_FRIEND) {
					this.getSurface().drawBox(106, y, 300, 70, 0);
					this.getSurface().drawBoxBorder(106, 300, y, 70, 16777215);
					y += 20;
					this.getSurface().drawColoredStringCentered(256, "Enter name to add to friends list", 16777215, 0, 4,
							y);
					y += 20;
					this.getSurface().drawColoredStringCentered(256, this.inputTextCurrent + "*", 16777215, 0, 4, y);
					String localKey = StringUtil.displayNameToKey(this.localPlayer.accountName);
					if (null != localKey && this.inputTextFinal.length() > 0) {
						String friend = this.inputTextFinal.trim();
						this.panelSocialPopup_Mode = SocialPopupMode.NONE;
						this.inputTextCurrent = "";
						this.inputTextFinal = "";
						if (friend.length() > 0 && !localKey.equals(StringUtil.displayNameToKey(friend))) {
							this.addFriend(friend);
						}
					}
				}
				if (this.panelSocialPopup_Mode == SocialPopupMode.MESSAGE_FRIEND) {
					this.getSurface().drawBox(6, y, 500, 70, 0);
					this.getSurface().drawBoxBorder(6, 500, y, 70, 16777215);
					y += 20;
					this.getSurface().drawColoredStringCentered(256, "Enter message to send to " + this.chatMessageTarget,
							16777215, 0, 4, y);
					y += 20;
					this.getSurface().drawColoredStringCentered(256, this.chatMessageInput + "*", 16777215, 0, 4, y);
					if (this.chatMessageInputCommit.length() > 0) {
						String var3 = this.chatMessageInputCommit;
						this.chatMessageInput = "";
						this.panelSocialPopup_Mode = SocialPopupMode.NONE;
						this.chatMessageInputCommit = "";
						this.putStringPair(this.chatMessageTarget, var3);
					}
				}

				if (this.panelSocialPopup_Mode == SocialPopupMode.ADD_IGNORE) {
					this.getSurface().drawBox(106, y, 300, 70, 0);
					this.getSurface().drawBoxBorder(106, 300, y, 70, 16777215);
					y += 20;
					this.getSurface().drawColoredStringCentered(256, "Enter name to add to ignore list", 16777215, 0, 4, y);
					y += 20;
					this.getSurface().drawColoredStringCentered(256, this.inputTextCurrent + "*", 16777215, 0, 4, y);
					String localKey = StringUtil.displayNameToKey(this.localPlayer.accountName);
					if (localKey != null && this.inputTextFinal.length() > 0) {
						String ignore = this.inputTextFinal.trim();
						this.inputTextCurrent = "";
						this.panelSocialPopup_Mode = SocialPopupMode.NONE;
						this.inputTextFinal = "";
						if (ignore.length() > 0 && !localKey.equals(StringUtil.displayNameToKey(ignore))) {
							this.addIgnore(ignore);
						}
					}
				}

				int color = 16777215;
				if (this.mouseX > x + 130 && this.mouseX < x + 170 && this.mouseY > y + 10 && this.mouseY < y + 28) {
					color = 16776960;
				}
				this.getSurface().drawColoredStringCentered(256, "Cancel", color, 0, 1, y + 23);
			} catch (RuntimeException var5) {
				throw GenUtil.makeThrowable(var5, "client.GB(" + "dummy" + ')');
			}
		}

		public final void drawTeleportBubble(int id, int x, int y, int width, int height, int topPixelSkew, int var7) {
			try {

				if (var7 != 2) {
					this.welcomeScreenShown = true;
				}

				int type = this.teleportBubbleType[id];
				int time = this.teleportBubbleTime[id];
				int color;
				int alpha = 255 - time * 5;
				if (type == 0) {
					color = 255 + time * 1280;
					this.getSurface().drawCircle(x + width / 2, height / 2 + y, 20 + time * 2, color, alpha, -1057205208);
				}

				if (type == 1) {
					color = 16711680 + time * 1280;
					this.getSurface().drawCircle(x + width / 2, y + height / 2, time + 10, color, alpha, -1057205208);
				}

			} catch (RuntimeException var11) {
				throw GenUtil.makeThrowable(var11, "client.LB(" + topPixelSkew + ',' + x + ',' + y + ',' + height + ',' + id
						+ ',' + width + ',' + var7 + ')');
			}
		}

		private final void drawTextBox(String line2, byte var2, String line1) {
			try {

				if (var2 == -64) {
					clientPort.drawTextBox(line2, var2, line1);
				}
			} catch (RuntimeException var8) {
				throw GenUtil.makeThrowable(var8, "client.MD(" + (line2 != null ? "{...}" : "null") + ',' + var2 + ','
						+ (line1 != null ? "{...}" : "null") + ')');
			}
		}

		private final void drawTradeConfirmDialog(int var1) {
			try {

				byte var2 = 22;
				byte var3 = 36;
				this.getSurface().drawBox(var2, var3, 468, 16, 192);
				int var4 = 10000536;
				this.getSurface().drawBoxAlpha(var2, var3 + 16, 468, 246, var4, 160);
				this.getSurface().drawColoredStringCentered(234 + var2,
						"Please confirm your trade with @yel@" + this.tradeRecipientConfirmName, 16777215, 0, 1, var3 + 12);
				this.getSurface().drawColoredStringCentered(var2 + 117, "You are about to give:", 16776960, 0, 1,
						30 + var3);

				int var5;
				String var6;
				for (var5 = 0; this.tradeConfirmItemsCount > var5; ++var5) {
					var6 = EntityHandler.getItemDef(this.tradeConfirmItems[var5]).getName();
					if (EntityHandler.getItemDef(this.tradeConfirmItems[var5]).isStackable()) {
						var6 = var6 + " x " + StringUtil.formatItemCount(this.tradeConfirmItemsCount1[var5]);
					}

					this.getSurface().drawColoredStringCentered(var2 + 117, var6, 16777215, 0, 1, var5 * 12 + 42 + var3);
				}

				if (this.tradeConfirmItemsCount == 0) {
					this.getSurface().drawColoredStringCentered(var2 + 117, "Nothing!", 16777215, 0, 1, 42 + var3);
				}

				this.getSurface().drawColoredStringCentered(351 + var2, "In return you will receive:", 16776960, 0, 1,
						30 + var3);

				for (var5 = 0; var5 < this.tradeRecipientConfirmItemsCount; ++var5) {
					var6 = EntityHandler.getItemDef(this.tradeRecipientConfirmItems[var5]).getName();
					if (EntityHandler.getItemDef(this.tradeRecipientConfirmItems[var5]).isStackable()) {
						var6 = var6 + " x " + StringUtil.formatItemCount(this.tradeRecipientConfirmItemCount[var5]);
					}

					this.getSurface().drawColoredStringCentered(351 + var2, var6, 16777215, 0, 1, 42 + var3 + var5 * 12);
				}

				if (this.tradeRecipientConfirmItemsCount == 0) {
					this.getSurface().drawColoredStringCentered(351 + var2, "Nothing!", 16777215, 0, 1, var3 + 42);
				}

				this.getSurface().drawColoredStringCentered(var2 + 234, "Are you sure you want to do this?", '\uffff', 0, 4,
						200 + var3);
				this.getSurface().drawColoredStringCentered(var2 + 234,
						"There is NO WAY to reverse a trade if you change your mind.", 16777215, 0, 1, var3 + 215);
				this.getSurface().drawColoredStringCentered(234 + var2, "Remember that not all players are trustworthy",
						16777215, 0, 1, var3 + 230);
				if (this.tradeConfirmAccepted) {
					this.getSurface().drawColoredStringCentered(234 + var2, "Waiting for other player...", 16776960, 0, 1,
							250 + var3);
				} else {
					this.getSurface().drawSprite(mudclient.spriteMedia + 25, var2 - 35 + 118, 238 + var3);
					this.getSurface().drawSprite(26 + mudclient.spriteMedia, var2 + 352 - 35, var3 + 238);
				}

				if (this.mouseButtonClick == 1) {
					if (this.mouseX < var2 || this.mouseY < var3 || this.mouseX > 468 + var2 || this.mouseY > var3 + 262) {
						this.showDialogTradeConfirm = false;
						this.getClientStream().newPacket(230);
						this.getClientStream().finishPacket();
					}

					if (this.mouseX >= var2 + 118 - 35 && this.mouseX <= var2 + 118 + 70 && this.mouseY >= var3 + 238
							&& this.mouseY <= 238 + var3 + 21) {
						this.tradeConfirmAccepted = true;
						this.getClientStream().newPacket(104);
						this.getClientStream().finishPacket();
					}

					if (352 + var2 - 35 <= this.mouseX && this.mouseX <= var2 + 423 && this.mouseY >= var3 + 238
							&& this.mouseY <= 238 + var3 + 21) {
						this.showDialogTradeConfirm = false;
						this.getClientStream().newPacket(230);
						this.getClientStream().finishPacket();
					}

					this.mouseButtonClick = 0;
				}

			} catch (RuntimeException var7) {
				throw GenUtil.makeThrowable(var7, "client.WD(" + var1 + ')');
			}
		}

		private int xpPerHourCount = 0; long timePassed = 0; double xpPerHour = 0;
		private final void drawExperienceCounter(int skill) {
			if (selectedSkill >= 0) {
				skill = selectedSkill;
			}
			int textColor = Config.EXPERIENCE_COUNTER_COLOR == 0 ? 0xFFFFFF :
				Config.EXPERIENCE_COUNTER_COLOR == 1 ? 0xFFFF00 :
					Config.EXPERIENCE_COUNTER_COLOR == 2 ? 0xFF0000 :
						Config.EXPERIENCE_COUNTER_COLOR == 3 ? 0x0000FF : 0x00FF00;
			int totalXp = 0;
			if (Config.EXPERIENCE_COUNTER_MODE == 1 || skill < 0) {
				for (int i = 0; i < 18; i++) {
					totalXp += this.playerExperience[i];
				}

				int stringWid = getSurface().stringWidth(3, "Total: " + totalXp);
				int x = (getGameWidth()/2) - (stringWid/2) - 10;
				int width = stringWid + 6;
				this.getSurface().drawBoxAlpha(x, 0, width, 20, 0x989898, 90);
				//this.getSurface().drawBoxBorder(x, width, 0, 20, 0x000000);

				if (textColor == 0xFFFFFF) {
					getSurface().drawShadowText("Total: " + totalXp, (getGameWidth()/2) - (stringWid/2) - 4, 15, textColor, 2, false);
				} else {
					getSurface().drawString("Total: " + totalXp, (getGameWidth()/2) - (stringWid/2) - 4, 15, textColor, 2);
				}

				if (Config.isAndroid() && this.mouseButtonClick == 1 && mouseX >= x && mouseX <= x + width && mouseY >= 0 && mouseY <= 20) {
					if (doubleClick()) {
						experienceConfigInterface.setVisible(true);
						setMouseClick(0);
					}
				}
				else if (!Config.isAndroid() && this.mouseButtonClick == 1 && mouseX >= x && mouseX <= x + width && mouseY >= 0 && mouseY <= 20) {
					experienceConfigInterface.setVisible(true);
					setMouseClick(0);
				}

				if (Config.EXPERIENCE_CONFIG_SUBMENU && mouseX >= x && mouseX <= x + width && mouseY >= 0 && mouseY <= 20) {
					// Checks for non-positive gains
					if (this.playerXpGainedTotal < 0) {
						this.playerXpGainedTotal = 0;
					}

					// Used to make sure it isn't changing so quickly
					xpPerHourCount++;
					if (xpPerHourCount % 100 == 0) {
						timePassed = System.currentTimeMillis() - this.totalXpGainedStartTime;
						xpPerHour = this.playerXpGainedTotal/(((double) timePassed)/3600000);
					}
					this.getSurface().drawBoxAlpha(x, 19, width, 31, 0x989898, 90);
					//this.getSurface().drawBoxBorder(x, width, 19, 31, 0x000000);

					if (textColor == 0xFFFFFF) {
						this.getSurface().drawShadowText("Gained: " + this.playerXpGainedTotal, x + 3, 31, textColor, 2, false);
						this.getSurface().drawShadowText("Xp/hr:     " + (int) xpPerHour, x + 3, 46, textColor, 2, false);
					} else {
						this.getSurface().drawString("Gained: " + this.playerXpGainedTotal, x + 3, 31, textColor, 2);
						this.getSurface().drawString("Xp/hr:     " + (int) xpPerHour, x + 3, 46, textColor, 2);
					}
				}
			}

			else {
				int stringWid = getSurface().stringWidth(3, skillNames[skill] + ": " + playerStatBase[skill] + ": " + playerExperience[skill]);
				int x = (getGameWidth()/2) - (stringWid/2) - 10;
				int width = stringWid + 6;
				this.getSurface().drawBoxAlpha(x, 0, width, 20, 0x989898, 90);
				//this.getSurface().drawBoxBorder(x, width, 0, 20, 0x000000);

				int tilLvl = 0, baseTilLvl = 0, progressWidth = 0; double progress = 0;
				if (playerStatBase[skill] != 99) {
					tilLvl = this.experienceArray[playerStatBase[skill] - 1] - this.playerExperience[skill];
					baseTilLvl = this.experienceArray[playerStatBase[skill]] - this.experienceArray[playerStatBase[skill] - 1];
					progress = ((double) tilLvl) / ((double) baseTilLvl) / 0.9;
					progressWidth = (int) (progress*width);

					this.getSurface().drawBox(x, 19, width - progressWidth, 2, 0x00FF00);
					this.getSurface().drawBox(x + width - progressWidth, 19, progressWidth, 2, 0xFF0000);
				}

				if (textColor == 0xFFFFFF) {
					getSurface().drawShadowText(skillNames[skill] + ": " + playerStatBase[skill] + ": " + playerExperience[skill], (getGameWidth()/2) - (stringWid/2) - 4, 15, textColor, 2, false);
				} else {
					getSurface().drawString(skillNames[skill] + ": " + playerStatBase[skill] + ": " + playerExperience[skill], (getGameWidth()/2) - (stringWid/2) - 4, 15, textColor, 2);
				}

				if (Config.isAndroid() && this.mouseButtonClick == 1 && mouseX >= x && mouseX <= x + width && mouseY >= 0 && mouseY <= 20) {
					if (doubleClick()) {
						experienceConfigInterface.setVisible(true);
						setMouseClick(0);
					}
				}
				else if (!Config.isAndroid() && this.mouseButtonClick == 1 && mouseX >= x && mouseX <= x + width && mouseY >= 0 && mouseY <= 20) {
					experienceConfigInterface.setVisible(true);
					setMouseClick(0);
				}

				if (Config.EXPERIENCE_CONFIG_SUBMENU && mouseX >= x && mouseX <= x + width && mouseY >= 0 && mouseY <= 20) {
					// Checks for non-positive gains
					if (this.playerStatXpGained[skill] < 0) {
						this.playerStatXpGained[skill] = 0;
					}

					// Used to make sure it isn't changing too quickly
					xpPerHourCount++;
					if (xpPerHourCount % 100 == 0) {
						timePassed = System.currentTimeMillis() - this.xpGainedStartTime[skill];
						xpPerHour = this.playerStatXpGained[skill]/(((double) timePassed)/3600000);
					}
					this.getSurface().drawBoxAlpha(x, 20, width, 61, 0x989898, 90);
					//this.getSurface().drawBoxBorder(x, width, 19, 61, 0x000000);

					if (textColor == 0xFFFFFF) {
						if (playerStatBase[skill] == 99) {
							this.getSurface().drawShadowText("Gained: " + this.playerStatXpGained[skill], x + 3, 63, textColor, 2, false);
							this.getSurface().drawShadowText("Xp/hr:     " + (int) xpPerHour, x + 3, 78, textColor, 2, false);
						} else {
							this.getSurface().drawShadowText("Next lvl: " + this.experienceArray[playerStatBase[skill] - 1], x + 3, 33, textColor, 2, false);
							this.getSurface().drawShadowText("Til lvl:     " + tilLvl, x + 3, 48, textColor, 2, false);
							this.getSurface().drawShadowText("Gained: " + this.playerStatXpGained[skill], x + 3, 63, textColor, 2, false);
							this.getSurface().drawShadowText("Xp/hr:     " + (int) xpPerHour, x + 3, 78, textColor, 2, false);
						}
					} else {
						if (playerStatBase[skill] == 99) {
							this.getSurface().drawString("Gained: " + this.playerStatXpGained[skill], x + 3, 63, textColor, 2);
							this.getSurface().drawString("Xp/hr:     " + (int) xpPerHour, x + 3, 78, textColor, 2);
						} else {
							this.getSurface().drawString("Next lvl: " + this.experienceArray[playerStatBase[skill] - 1], x + 3, 33, textColor, 2);
							this.getSurface().drawString("Til lvl:     " + tilLvl, x + 3, 48, textColor, 2);
							this.getSurface().drawString("Gained: " + this.playerStatXpGained[skill], x + 3, 63, textColor, 2);
							this.getSurface().drawString("Xp/hr:     " + (int) xpPerHour, x + 3, 78, textColor, 2);
						}
					}
				}
			}
		}

		private final void drawExperienceConfig() {
			experienceConfigInterface.onRender(this.getSurface());
		}

		private final void drawUi(int var1) {
			try {

				boolean var2 = false;

				try {
					mainComponent.renderComponent();
				} catch (Exception e) {
					e.printStackTrace();
				}

				if (lastSelectedSpell != -1 && Config.isAndroid()) {
					int boxWidth = 75;
					int boxHeight = 50;
					int x = getGameWidth() - boxWidth - 15;
					int y = (getGameHeight() / 2) - boxHeight;

					getSurface().drawBoxAlpha(x, y, boxWidth, boxHeight, 0x989898, 128);
					getSurface().drawBoxBorder(x, boxWidth, y, boxHeight, 0);

					SpellDef spellDef = EntityHandler.getSpellDef(lastSelectedSpell);
					if (spellDef != null) {
						getSurface().drawBoxAlpha(x, y, boxWidth, 16, 0x6b8e23, 128);
						getSurface().drawBoxBorder(x, boxWidth, y, 16, 0);

						getSurface().drawBoxAlpha(x, y + 49, boxWidth, 20, GenUtil.buildColor(255, 0, 0), 128);
						getSurface().drawBoxBorder(x, boxWidth, y + 49, 20, 0);

						getSurface().drawColoredStringCentered(x + (boxWidth / 2), "@whi@Remove", 0, 0, 1, y + 63);

						String[] spellName = spellDef.getName().split(" ");
						String color = "@yel@";
						for (Entry<?, ?> e : EntityHandler.getSpellDef(lastSelectedSpell).getRunesRequired()) {
							if (hasRunes((Integer) e.getKey(), (Integer) e.getValue())) {
								continue;
							}
							color = "@whi@";
							break;
						}
						int textHeightOffset = 25;
						getSurface().drawColoredStringCentered(x + (boxWidth / 2), "@whi@" + "Tap to Cast", 0, 0, 1,
								y + 12);

						for (String s : spellName) {
							getSurface().drawColoredStringCentered(x + (boxWidth / 2), color + s, 0, 0, 1,
									y + textHeightOffset + 1);
							textHeightOffset += 10;
						}

						if (mouseX > x && mouseX < x + boxWidth && mouseY > y && mouseY < y + boxHeight
								&& mouseButtonClick > 0 && this.showUiTab == 0) {
							selectedSpell = lastSelectedSpell;
							mouseButtonClick = 0;
						}

						if (mouseX > x && mouseX < x + boxWidth && mouseY > y + 49 && mouseY < y + 69
								&& mouseButtonClick > 0 && this.showUiTab == 0) {
							selectedSpell = -1;
							lastSelectedSpell = -1;
							mouseButtonClick = 0;
						}
					}
				}

				if (var1 != this.logoutTimeout) {
					this.drawDialogLogout();
				} else if (this.showDialogMessage) {
					this.drawDialogWelcome(var1 - 4853);
				} else if (this.showDialogServerMessage) {
					this.drawDialogServerMessage((byte) -115);
				} else if (this.showUiWildWarn != 1) {
					if (this.isShowDialogBank() && this.combatTimeout == 0) {
						this.drawDialogBank();
					} else if (auctionHouse.isVisible() && combatTimeout == 0) {
						auctionHouse.onRender(getSurface());
					} else if (ironmanInterface.isVisible() && combatTimeout == 0) {
						ironmanInterface.onRender(getSurface());
					} /*else if (achievementInterface.isVisible() && combatTimeout == 0) {
						achievementInterface.onRender(getSurface());
					}*/ else if (clan.getClanInterface().isVisible()) {
						clan.getClanInterface().onRender(getSurface());
					} else if (this.showDialogShop && this.combatTimeout == 0) {
						this.drawDialogShop();
					} else if (skillGuideInterface.isVisible()) {
						this.drawSkillGuide();
					} else if (questGuideInterface.isVisible()) {
						this.drawQuestGuide();
					} else if (experienceConfigInterface.isVisible()) {
						this.drawExperienceConfig();
					} else if (doSkillInterface.isVisible() && this.combatTimeout == 0) {
						this.drawDoSkill();
					} else if (lostOnDeathInterface.isVisible()) {
						this.drawLostOnDeath();
					} else if (territorySignupInterface.isVisible()) {
						this.drawTerritorySignup();
					} else if (!this.showDialogTradeConfirm) {
						if (this.showDialogTrade) {
							this.drawDialogTrade();
						} else if (this.showDialogDuelConfirm) {
							this.drawDialogDuelConfirm();
						} else if (this.showDialogDuel) {
							this.drawDialogDuel();
						} else if (this.reportAbuse_State != 1) {
							if (this.reportAbuse_State == 2) {
								this.handleReportAbuseClick();
							} else if (this.panelSocialPopup_Mode == SocialPopupMode.NONE) {
								var2 = true;

							} else {
								this.drawPopupSocial();
							}
						} else {
							this.drawPopupReport(false);
						}
					} else {
						this.drawTradeConfirmDialog(-54);
					}
				} else {
					this.drawDialogWildWarn(120);
				}

				if (this.inputX_Action != InputXAction.ACT_0) {
					this.drawInputX();
				}
				if (var2) {
					/*if (ANDROID_BUILD && SHOWING_KEYBOARD) {
						String var11 = this.panelMessageTabs.getControlText(this.panelMessageEntry);
						int var3 = 45;
						this.getSurface().drawBoxBorder(106, 300, var3, 50, 16777215);
						this.getSurface().drawBoxAlpha(106, var3, 300, 50, 0, 128);
						var3 += 20;
						this.getSurface().drawColoredStringCentered(256, "Enter chat message", 16777215, 0, 4, var3);
						var3 += 20;
						this.getSurface().drawColoredStringCentered(256, var11 + "*", 16777215, 0, 4, var3);
					}*/
					if (this.optionsMenuShow) {
						this.drawDialogOptionsMenu(-312);
					}

					if (((this.localPlayer.direction == RSCharacterDirection.COMBAT_A
							|| this.localPlayer.direction == RSCharacterDirection.COMBAT_B) || Config.FIGHT_MENU == 2) && Config.FIGHT_MENU != 0) {
						this.drawDialogCombatStyle();
					}

					this.handleTabUIClick();
					boolean var3 = !this.optionsMenuShow && !this.topMouseMenuVisible;
					if (var3) {
						this.menuCommon.recalculateSize(0);
					}

					if (this.showUiTab == 0 && var3) {
						this.drawUiTab0(var1 ^ 2);
					}

					if (this.showUiTab == 1) {
						this.drawUiTab1(-15252, var3);
					}

					if (Config.INV_COUNT) {
						this.getSurface().drawShadowText(this.inventoryItemCount + "/30", this.getGameWidth() - 19, 17, 0xFFFFFF, 1, true);
					}

					if (this.showUiTab == 2) {
						this.drawUiTabMinimap(var3, (byte) 125);
					}

					if (this.showUiTab == 3) {
						this.drawUiTabPlayerInfo(var3, var1 ^ 0);
					}

					if (this.showUiTab == 4) {
						this.drawUiTabMagic(var3, (byte) -74);
					}

					if (this.showUiTab == 5) {
						this.drawUiTab5(var3, false);
					}

					if (this.showUiTab == 6) {
						this.drawUiTabOptions(15, var3);
					}

					if (!this.topMouseMenuVisible && !this.optionsMenuShow) {
						this.createTopMouseMenu(-128);
					}

					if (this.topMouseMenuVisible && !this.optionsMenuShow) {
						this.drawMenu();
					}
				}

				this.mouseButtonClick = 0;
			} catch (RuntimeException var4) {
				throw GenUtil.makeThrowable(var4, "client.DC(" + var1 + ')');
			}
		}

		private BankInterface bank;

		private int settingsBlockGlobal;
		private int lastSelectedSpell = -1;

		public int resizeWidth;
		public int resizeHeight;

		private void drawDialogBank() {
			bank.onRender();
		}

		/* Game Screen Right Click Menu Definitions */
		private final void drawUiTab0(int var1) {
			try {
				if (this.messageTabSelected == MessageTab.CHAT && this.panelMessageTabs.isClicked(this.panelMessageChat)
						|| this.messageTabSelected == MessageTab.QUEST
						&& this.panelMessageTabs.isClicked(this.panelMessageQuest)
						|| this.messageTabSelected == MessageTab.PRIVATE
						&& this.panelMessageTabs.isClicked(this.panelMessagePrivate)
						|| this.messageTabSelected == MessageTab.CLAN
						&& this.panelMessageTabs.isClicked(this.panelMessageClan)) {
					int control =
							(
									(this.messageTabSelected == MessageTab.CHAT ? this.panelMessageChat
											:
												this.messageTabSelected == MessageTab.QUEST ? this.panelMessageQuest
														:
															(this.messageTabSelected == MessageTab.CLAN ? this.panelMessageClan
																	: this.panelMessagePrivate)
											));
					int index = this.panelMessageTabs.getControlClickedListIndex(control);
					if (index >> 16 == 2 || this.optionMouseButtonOne && index >> 16 == 1) {
						int var4 = '\uffff' & index;
						String var5 = this.panelMessageTabs.getControlListString2(control, var4);
						String var6 = this.panelMessageTabs.getControlListString3(control, var4);
						if (this.drawMenuMessage(var5, var1 ^ 125, var6)) {
							return;
						}
					}
				}

				if (this.messageTabSelected == MessageTab.ALL) {
					for (int i = 0; i < messagesArray.length; ++i) {
						if (MessageHistory.messageHistoryTimeout[i] > 0
								&& (MessageHistory.messageHistoryType[i] == MessageType.CHAT
								|| MessageHistory.messageHistoryType[i] == MessageType.PRIVATE_RECIEVE
								|| MessageHistory.messageHistoryType[i] == MessageType.FRIEND_STATUS
								|| MessageHistory.messageHistoryType[i] == MessageType.TRADE
								|| MessageHistory.messageHistoryType[i] == MessageType.GLOBAL_CHAT
								|| MessageHistory.messageHistoryType[i] == MessageType.CLAN_CHAT)) {
							String msg = MessageHistory.messageHistoryColor[i] + StringUtil.formatMessage(
									MessageHistory.messageHistoryMessage[i], MessageHistory.messageHistorySender[i], true,
									MessageHistory.messageHistoryType[i]);
							if (this.mouseX > 7 && this.mouseX < this.getSurface().stringWidth(1, msg) + 7
									&& this.mouseY > -(i * 12) - 30 + this.getGameHeight()
									&& this.getGameHeight() - i * 12 - 18 > this.mouseY
									&& (this.mouseButtonClick == 2
									|| this.optionMouseButtonOne && this.mouseButtonClick == 1)
									&& this.drawMenuMessage(MessageHistory.messageHistoryClan[i], (int) 127,
											MessageHistory.messageHistorySender[i])) {
								return;
							}
						}
					}
				}

				this.menuVisible = false;

				for (int var3 = 0; this.gameObjectInstanceCount > var3; ++var3) {
					this.gameObjectInstance_Arg1[var3] = false;
				}

				for (int var3 = 0; this.wallObjectInstanceCount > var3; ++var3) {
					this.wallObjectInstance_Arg1[var3] = false;
				}

				int var2 = -1;
				int var3 = this.scene.b((int) 0);
				RSModel[] var18 = this.scene.b((byte) 124);
				int[] var19 = this.scene.getQB((byte) 104);
				if (var1 != 2) {
					this.mouseClickCount = -82;
				}

				for (int var20 = 0; var20 < var3 && this.menuCommon.getItemCount(var1 ^ -27155) <= 200; ++var20) {
					int var7 = var19[var20];
					RSModel var8 = var18[var20];
					if (var8.facePickIndex[var7] <= 0xFFFF
							|| var8.facePickIndex[var7] >= 200000 && var8.facePickIndex[var7] <= 300000) {
						int var9;
						int id;
						if (this.scene.m_T != var8) {
							if (var8 != null && var8.key >= 10000) { // Wall Object Right Click Menu
								var9 = var8.key - 10000;
								id = this.wallObjectInstanceID[var9];
								if (!this.wallObjectInstance_Arg1[var9]) {
									if (this.selectedSpell >= 0) {
										if (EntityHandler.getSpellDef(selectedSpell).getSpellType() == 4) {
											this.menuCommon.addTileItem_WithID(MenuItemAction.WALL_CAST_SPELL,
													this.wallObjectInstanceZ[var9], this.wallObjectInstanceDir[var9],
													this.wallObjectInstanceX[var9], this.selectedSpell,
													"@cya@" + EntityHandler.getDoorDef(id).getName(),
													"Cast " + EntityHandler.getSpellDef(selectedSpell).getName() + " on");
										}
									} else if (this.selectedItemInventoryIndex >= 0) {
										this.menuCommon.addTileItem_WithID(MenuItemAction.WALL_USE_ITEM,
												this.wallObjectInstanceZ[var9], this.wallObjectInstanceDir[var9],
												this.wallObjectInstanceX[var9], this.selectedItemInventoryIndex,
												"@cya@" + EntityHandler.getDoorDef(id).getName(),
												"Use " + this.m_ig + " with");
									} else {
										if (!EntityHandler.getDoorDef(id).getCommand1().equalsIgnoreCase("WalkTo")) {
											this.menuCommon.addTileItem(this.wallObjectInstanceX[var9], (byte) 22,
													MenuItemAction.WALL_COMMAND1,
													EntityHandler.getDoorDef(id).getCommand1(),
													"@cya@" + EntityHandler.getDoorDef(id).getName(),
													this.wallObjectInstanceDir[var9], this.wallObjectInstanceZ[var9]);
										}

										if (!EntityHandler.getDoorDef(id).getCommand2().equalsIgnoreCase("Examine")) {
											this.menuCommon.addTileItem(this.wallObjectInstanceX[var9], (byte) 22,
													MenuItemAction.WALL_COMMAND2,
													EntityHandler.getDoorDef(id).getCommand2(),
													"@cya@" + EntityHandler.getDoorDef(id).getName(),
													this.wallObjectInstanceDir[var9], this.wallObjectInstanceZ[var9]);
										}
										this.menuCommon
										.addCharacterItem(id, MenuItemAction.WALL_EXAMINE,
												"Examine", "@cya@"
														+ EntityHandler.getDoorDef(id)
														.getName()
														+ (adminRights ? " @or1@(" + id + ":"
																+ (wallObjectInstanceX[var9] + this.midRegionBaseX)
																+ ","
																+ (wallObjectInstanceZ[var9] + this.midRegionBaseZ)
																+ ")" : ""));
									}

									this.wallObjectInstance_Arg1[var9] = true;
								}
							} else if (null != var8 && var8.key >= 0) { // Game Object Right Click Menu
								var9 = var8.key;
								id = this.gameObjectInstanceID[var9];
								if (!this.gameObjectInstance_Arg1[var9]) {
									if (this.selectedSpell < 0) {
										if (this.selectedItemInventoryIndex >= 0) {
											this.menuCommon.addUseOnObject(this.gameObjectInstanceZ[var9],
													"Use " + this.m_ig + " with", -104, this.selectedItemInventoryIndex,
													this.gameObjectInstanceID[var9], MenuItemAction.OBJECT_USE_ITEM,
													this.gameObjectInstanceDir[var9],
													"@cya@" + EntityHandler.getObjectDef(id).getName(),
													this.gameObjectInstanceX[var9]);
										} else {
											if (!EntityHandler.getObjectDef(id).getCommand1().equalsIgnoreCase("WalkTo")) {
												this.menuCommon.addTileItem_WithID(MenuItemAction.OBJECT_COMMAND1,
														this.gameObjectInstanceZ[var9], this.gameObjectInstanceDir[var9],
														this.gameObjectInstanceX[var9], this.gameObjectInstanceID[var9],
														"@cya@" + EntityHandler.getObjectDef(id).getName(),
														EntityHandler.getObjectDef(id).getCommand1());
											}

											if (!EntityHandler.getObjectDef(id).getCommand2().equalsIgnoreCase("Examine")) {
												this.menuCommon.addTileItem_WithID(MenuItemAction.OBJECT_COMMAND2,
														this.gameObjectInstanceZ[var9], this.gameObjectInstanceDir[var9],
														this.gameObjectInstanceX[var9], this.gameObjectInstanceID[var9],
														"@cya@" + EntityHandler.getObjectDef(id).getName(),
														EntityHandler.getObjectDef(id).getCommand2());
											}
											if (developerMenu) {
												this.menuCommon.addTileItem_WithID(MenuItemAction.DEV_ROTATE_OBJECT,
														this.gameObjectInstanceZ[var9], this.gameObjectInstanceDir[var9],
														this.gameObjectInstanceX[var9], this.gameObjectInstanceID[var9],
														"@gr2@Rotate Object",
														"@cya@" + EntityHandler.getObjectDef(id).getName());
												this.menuCommon.addTileItem_WithID(MenuItemAction.DEV_REMOVE_OBJECT,
														this.gameObjectInstanceZ[var9], this.gameObjectInstanceDir[var9],
														this.gameObjectInstanceX[var9], this.gameObjectInstanceID[var9],
														"@gr2@Remove Object",
														"@cya@" + EntityHandler.getObjectDef(id).getName());
											}

											this.menuCommon
											.addCharacterItem(id, MenuItemAction.OBJECT_EXAMINE, "Examine",
													"@cya@" + EntityHandler.getObjectDef(id)
													.getName()
													+ (adminRights
															? " @or1@(" + id + ":"
															+ (gameObjectInstanceX[var9]
																	+ this.midRegionBaseX)
															+ ","
															+ (gameObjectInstanceZ[var9]
																	+ this.midRegionBaseZ)
															+ ")"
															: ""));
										}
									} else if (EntityHandler.getSpellDef(selectedSpell).getSpellType() == 5) {
										this.menuCommon.addUseOnObject(this.gameObjectInstanceZ[var9],
												"Cast " + EntityHandler.getSpellDef(selectedSpell).getName() + " on",
												var1 + 65, this.selectedSpell, this.gameObjectInstanceID[var9],
												MenuItemAction.OBJECT_CAST_SPELL, this.gameObjectInstanceDir[var9],
												"@cya@" + EntityHandler.getObjectDef(id).getName(),
												this.gameObjectInstanceX[var9]);
									}

									this.gameObjectInstance_Arg1[var9] = true;
								}
							} else {
								if (var7 >= 0) {
									var7 = var8.facePickIndex[var7] - 200000;
								}

								if (var7 >= 0) {
									var2 = var7;
								}
							}
						} else {
							var9 = var8.facePickIndex[var7] % 10000;
							//if(this.groundItemID[var9] == 20 && !SHOW_BONES)
							//continue;
							id = var8.facePickIndex[var7] / 10000;
							if (id != 1) {
								if (id == 2) { // Ground Item Right Click Menu
									if (this.selectedSpell >= 0) {
										if (EntityHandler.getSpellDef(selectedSpell).getSpellType() == 3) {
											this.menuCommon.addTileItem_WithID(MenuItemAction.GROUND_ITEM_CAST_SPELL,
													this.groundItemZ[var9], this.groundItemID[var9], this.groundItemX[var9],
													this.selectedSpell,
													"@lre@" + EntityHandler.getItemDef(this.groundItemID[var9]).getName(),
													"Cast " + EntityHandler.getSpellDef(selectedSpell).getName() + " on");
										}
									} else if (this.selectedItemInventoryIndex < 0) {
										this.menuCommon.addTileItem(this.groundItemX[var9], (byte) 22,
												MenuItemAction.GROUND_ITEM_TAKE, "Take",
												"@lre@" + EntityHandler.getItemDef(this.groundItemID[var9]).getName(),
												this.groundItemID[var9], this.groundItemZ[var9]);
										if(!Config.isAndroid()) {
											this.menuCommon
											.addCharacterItem(this.groundItemID[var9],
													MenuItemAction.GROUND_ITEM_EXAMINE,
													"Examine", "@lre@"
															+ EntityHandler.getItemDef(this.groundItemID[var9])
															.getName()
															+ (adminRights
																	? " @or1@(" + groundItemID[var9] + ":"
																	+ (groundItemX[var9] + midRegionBaseX) + ","
																	+ (groundItemZ[var9] + midRegionBaseZ) + ")"
																	: ""));
										}
									} else {
										this.menuCommon.addTileItem_WithID(MenuItemAction.GROUND_ITEM_USE_ITEM,
												this.groundItemZ[var9], this.groundItemID[var9], this.groundItemX[var9],
												this.selectedItemInventoryIndex,
												"@lre@" + EntityHandler.getItemDef(this.groundItemID[var9]).getName(),
												"Use " + this.m_ig + " with");
									}
								} else if (id == 3) { // NPC Right Click Menu
									String var11 = "";
									int var12 = -1;
									int var13 = this.npcs[var9].npcId;
									if (EntityHandler.getNpcDef(var13).isAttackable()) {
										int var14 = (EntityHandler.getNpcDef(var13).getStr()
												+ EntityHandler.getNpcDef(var13).getAtt()
												+ EntityHandler.getNpcDef(var13).getDef()
												+ EntityHandler.getNpcDef(var13).getHits()) / 4;
										int var15 = (this.playerStatBase[3] + this.playerStatBase[2]
												+ this.playerStatBase[1] + this.playerStatBase[0] + 27) / 4;
										var11 = "@yel@";
										var12 = var15 - var14;
										if (var12 < 0) {
											var11 = "@or1@";
										}

										if (var12 < -3) {
											var11 = "@or2@";
										}

										if (var12 < -6) {
											var11 = "@or3@";
										}

										if (var12 < -9) {
											var11 = "@red@";
										}

										if (var12 > 0) {
											var11 = "@gr1@";
										}

										if (var12 > 3) {
											var11 = "@gr2@";
										}

										if (var12 > 6) {
											var11 = "@gr3@";
										}

										if (var12 > 9) {
											var11 = "@gre@";
										}

										var11 = " " + var11 + "(level-" + var14 + ")";
									}

									if (this.selectedSpell >= 0) {
										if (EntityHandler.getSpellDef(selectedSpell).getSpellType() == 2) {
											this.menuCommon.addCharacterItem_WithID(this.npcs[var9].serverIndex,
													"@yel@" + EntityHandler.getNpcDef(this.npcs[var9].npcId).getName(),
													MenuItemAction.NPC_CAST_SPELL,
													"Cast " + EntityHandler.getSpellDef(selectedSpell).getName() + " on",
													this.selectedSpell);
										}
									} else if (this.selectedItemInventoryIndex < 0) {
										if (EntityHandler.getNpcDef(var13).isAttackable()) {
											this.menuCommon.addCharacterItem(this.npcs[var9].serverIndex,
													var12 >= 0 ? MenuItemAction.NPC_ATTACK1 : MenuItemAction.NPC_ATTACK2,
															"Attack",
															"@yel@" + EntityHandler.getNpcDef(this.npcs[var9].npcId).getName()
															+ var11);
										}
										if (developerMenu) {
											this.menuCommon.addCharacterItem(this.npcs[var9].serverIndex,
													MenuItemAction.DEV_REMOVE_NPC, "@gr2@Remove NPC",
													"@yel@" + EntityHandler.getNpcDef(this.npcs[var9].npcId).getName());
										}
										this.menuCommon.addCharacterItem(this.npcs[var9].serverIndex,
												MenuItemAction.NPC_TALK_TO, "Talk-to",
												"@yel@" + EntityHandler.getNpcDef(this.npcs[var9].npcId).getName());

										if (!EntityHandler.getNpcDef(var13).getCommand1().equals("")) {
											this.menuCommon.addCharacterItem(this.npcs[var9].serverIndex,
													MenuItemAction.NPC_COMMAND1, EntityHandler.getNpcDef(var13).getCommand1(),
													"@yel@" + EntityHandler.getNpcDef(this.npcs[var9].npcId).getName());
										}
										if (EntityHandler.getNpcDef(var13).getCommand2() != null) {
											this.menuCommon.addCharacterItem(this.npcs[var9].serverIndex,
													MenuItemAction.NPC_COMMAND2, EntityHandler.getNpcDef(var13).getCommand2(),
													"@yel@" + EntityHandler.getNpcDef(this.npcs[var9].npcId).getName());
										}

										this.menuCommon.addCharacterItem(this.npcs[var9].npcId, MenuItemAction.NPC_EXAMINE,
												"Examine",
												"@yel@" + EntityHandler.getNpcDef(this.npcs[var9].npcId).getName()
												+ (adminRights ? " @or1@(" + this.npcs[var9].npcId + ")" : ""));
									} else {
										this.menuCommon.addCharacterItem_WithID(this.npcs[var9].serverIndex,
												"@yel@" + EntityHandler.getNpcDef(this.npcs[var9].npcId).getName(),
												MenuItemAction.NPC_USE_ITEM, "Use " + this.m_ig + " with",
												this.selectedItemInventoryIndex);
									}
								}
							} else {
								this.addPlayerToMenu((int) var9);
							}
						}
					}
				}

				if (this.selectedSpell >= 0 && EntityHandler.getSpellDef(selectedSpell).getSpellType() <= 1) {
					this.menuCommon.addCharacterItem(this.selectedSpell, MenuItemAction.SELF_CAST_SPELL,
							"Cast " + EntityHandler.getSpellDef(selectedSpell).getName() + " on self", "");
				}

				if (~var2 != 0) {
					this.menuVisible = true;
					this.m_rf = this.midRegionBaseX + this.world.faceTileX[var2];
					this.m_Cg = this.midRegionBaseZ + this.world.faceTileZ[var2];
					if (this.selectedSpell >= 0) {
						if (EntityHandler.getSpellDef(selectedSpell).getSpellType() == 6) {

							this.menuCommon.addTileItem(this.world.faceTileX[var2], (byte) 22,
									MenuItemAction.LANDSCAPE_CAST_SPELL,
									"Cast " + EntityHandler.getSpellDef(selectedSpell).getName() + " on ground", "",
									this.selectedSpell, this.world.faceTileZ[var2]);
						}
					} else if (this.selectedItemInventoryIndex < 0) {
						this.menuCommon.addCharacterItem_WithID(this.world.faceTileX[var2], "",
								MenuItemAction.LANDSCAPE_WALK_HERE, "Walk here", this.world.faceTileZ[var2]);
						if (modMenu) {
							this.menuCommon.addCharacterItem_WithID(this.world.faceTileX[var2], "",
									MenuItemAction.MOD_TELEPORT, "Teleport here", this.world.faceTileZ[var2]);
						}
						if (developerMenu) {
							this.menuCommon.addCharacterItem_WithID(this.world.faceTileX[var2], "",
									MenuItemAction.DEV_ADD_NPC, "@gr2@Add NPC @whi@(@or1@" + devMenuNpcID + "@whi@)",
									this.world.faceTileZ[var2]);
							this.menuCommon.addCharacterItem_WithID(this.world.faceTileX[var2], "",
									MenuItemAction.DEV_ADD_OBJECT, "@gr2@Add Object @whi@(@or1@" + devMenuNpcID + "@whi@)",
									this.world.faceTileZ[var2]);
						}
					}
				}

			} catch (RuntimeException var16) {
				throw GenUtil.makeThrowable(var16, "client.TA(" + var1 + ')');
			}
		}

		/* Inventory Right Click Menu Definitions */
		private final void drawUiTab1(int var1, boolean var2) {
			try {
				if (var1 != -15252) {
					this.handlePacket2(-79, -83);
				}

				int var3 = this.getSurface().width2 - 248;
				this.getSurface().drawSprite(mudclient.spriteMedia + 1, var3, 3);

				int var4;
				int var5;
				int id;
				for (var4 = 0; this.m_cl > var4; ++var4) {
					var5 = var3 + var4 % 5 * 49;
					id = var4 / 5 * 34 + 36;
					if (this.inventoryItemCount > var4 && this.inventoryItemEquipped[var4] == 1) {
						this.getSurface().drawBoxAlpha(var5, id, 49, 34, 16711680, 128);
					} else {
						this.getSurface().drawBoxAlpha(var5, id, 49, 34, GenUtil.buildColor(181, 181, 181), 128);
					}

					if (var4 < this.inventoryItemCount) {
						this.getSurface().drawSpriteClipping(
								mudclient.spriteItem + EntityHandler.getItemDef(this.inventoryItemID[var4]).getSprite(),
								var5, id, 48, 32, EntityHandler.getItemDef(this.inventoryItemID[var4]).getPictureMask(), 0,
								false, 0, var1 ^ -15251);

						ItemDef def = EntityHandler.getItemDef(this.inventoryItemID[var4]);
						if (def.getNotedFormOf() >= 0) {
							ItemDef originalDef = EntityHandler.getItemDef(def.getNotedFormOf());
							getSurface().drawSpriteClipping(mudclient.spriteItem + originalDef.getSprite(), var5 + 7,
									id + 4, 33, 23, originalDef.getPictureMask(), 0, false, 0, 1);
						}
						if (EntityHandler.getItemDef(this.inventoryItemID[var4]).isStackable()) {
							this.getSurface().drawString("" + formatStackAmount(this.inventoryItemSize[var4]), 1 + var5,
									id + 10, 16776960, 1);
						}
					}
				}

				for (var4 = 1; var4 <= 4; ++var4) {
					this.getSurface().drawLineVert(var3 + var4 * 49, 36, 0, this.m_cl / 5 * 34);
				}

				for (var4 = 1; this.m_cl / 5 - 1 >= var4; ++var4) {
					this.getSurface().drawLineHoriz(var3, 36 + var4 * 34, 245, 0);
				}

				if (var2) {
					var3 = 248 + (this.mouseX - this.getSurface().width2);
					var4 = this.mouseY - 36;
					if (var3 >= 0 && var4 >= 0 && var3 < 248 && this.m_cl / 5 * 34 > var4) {
						var5 = var4 / 34 * 5 + var3 / 49;
						if (this.inventoryItemCount > var5) {
							id = this.inventoryItemID[var5];
							if (this.selectedSpell >= 0) {
								if (EntityHandler.getSpellDef(selectedSpell).getSpellType() == 3) {
									this.menuCommon.addCharacterItem_WithID(var5,
											"@lre@" + EntityHandler.getItemDef(id).getName(),
											MenuItemAction.ITEM_CAST_SPELL,
											"Cast " + EntityHandler.getSpellDef(selectedSpell).getName() + " on",
											this.selectedSpell);
								}
							} else if (this.selectedItemInventoryIndex < 0) {
								if (this.inventoryItemEquipped[var5] == 1) {
									this.menuCommon.addCharacterItem(var5, MenuItemAction.ITEM_REMOVE_EQUIPPED, "Remove",
											"@lre@" + EntityHandler.getItemDef(id).getName());
								} else if (EntityHandler.getItemDef(id).wearableID != 0) {
									String var7;
									if ((24 & EntityHandler.getItemDef(id).wearableID) == 0)
									{
										var7 = "Wear";
									} else {
										var7 = "Wield";
									}

									this.menuCommon.addCharacterItem(var5, MenuItemAction.ITEM_EQUIP, var7,
											"@lre@" + EntityHandler.getItemDef(id).getName());
								}

								if (!EntityHandler.getItemDef(id).getCommand().equals("")
										&& EntityHandler.getItemDef(id).getNotedFormOf() == -1) {
									this.menuCommon.addCharacterItem(var5, MenuItemAction.ITEM_COMMAND,
											EntityHandler.getItemDef(id).getCommand(),
											"@lre@" + EntityHandler.getItemDef(id).getName());
								}

								this.menuCommon.addCharacterItem(var5, MenuItemAction.ITEM_USE, "Use",
										"@lre@" + EntityHandler.getItemDef(id).getName());
								this.menuCommon.addCharacterItem(var5, MenuItemAction.ITEM_DROP, "Drop",
										"@lre@" + EntityHandler.getItemDef(id).getName());
								this.menuCommon.addCharacterItem(var5, MenuItemAction.ITEM_DROP_X, "Drop X",
										"@lre@" + EntityHandler.getItemDef(id).getName());
								this.menuCommon.addCharacterItem(id, MenuItemAction.ITEM_EXAMINE, "Examine",
										"@lre@" + EntityHandler.getItemDef(id).getName()
										+ (adminRights ? " @or1@(" + id + ")" : ""));
							} else {
								this.menuCommon.addCharacterItem_WithID(var5,
										"@lre@" + EntityHandler.getItemDef(id).getName(), MenuItemAction.ITEM_USE_ITEM,
										"Use " + this.m_ig + " with", this.selectedItemInventoryIndex);
							}
						}
					}

				}
			} catch (RuntimeException var8) {
				throw GenUtil.makeThrowable(var8, "client.EB(" + var1 + ',' + var2 + ')');
			}
		}

		/* Social Tab */
		private final void drawUiTab5(boolean var1, boolean var2) {
			try {
				int var3 = this.getSurface().width2 - 199;
				byte var4 = 36;
				this.getSurface().drawSprite(mudclient.spriteMedia + 5, var3 - 49, 3);
				short var5 = 196;
				short var6 = 182;
				int maxWidth = getGameWidth() - 23;
				int minWidth = getGameWidth() - 83;
				if (var2) {
					this.cameraRotationX = -88;
				}

				if (Config.WANT_CLANS) {
					int clanTab;
					int colorB;
					int colorA = colorB = clanTab = GenUtil.buildColor(160, 160, 160);
					if (this.panelSocialTab == 1) {
						clanTab = GenUtil.buildColor(220, 220, 220);
						if(clan.inClan()) {
							this.getSurface().drawBoxAlpha(var3, 24 + var4, var5, 49, GenUtil.buildColor(220, 220, 220), 192);
							this.getSurface().drawLineHoriz(var3, var4 + 72, var5, 0);
							this.getSurface().drawBoxAlpha(var3, var4 + var6 - 16 + 34, var5, 49, GenUtil.buildColor(220, 220, 220), 192);
						} else {
							this.getSurface().drawBoxAlpha(var3, var4 + var6 - 30, var5, 49, GenUtil.buildColor(220, 220, 220), 192);
						}
					} else if(this.panelSocialTab == 0){
						colorA = GenUtil.buildColor(220, 220, 220);
					} else {
						colorB = GenUtil.buildColor(220, 220, 220);
					}

					this.getSurface().drawBoxAlpha(var3, var4, 65, 24, colorA, 128);
					this.getSurface().drawBoxAlpha(var3 + var5 / 2 - 32, var4, 65, 24, clanTab, 128);
					this.getSurface().drawBoxAlpha(var3 + var5 / 2 + 33, var4, 65, 24, colorB , 128);
					this.getSurface().drawBoxAlpha(var3, (this.panelSocialTab == 1 && clan.inClan() ? 49 : 0) + 24 + var4, var5, (this.panelSocialTab == 1 ? 127 : var6 - 24), GenUtil.buildColor(220, 220, 220), 128);
					this.getSurface().drawLineHoriz(var3, var4 + 24, var5, 0);
					this.getSurface().drawLineVert(var5 / 2 + var3 - 33, 0 + var4, 0, 24);
					this.getSurface().drawLineVert(var5 / 2 + var3 + 33, 0 + var4, 0, 24);
					this.getSurface().drawLineHoriz(var3, var4 + var6 - 16 + (this.panelSocialTab == 1 ? clan.inClan() ? 34 : -15 : 0), var5, 0);
					this.getSurface().drawColoredStringCentered(var3 + var5 / 4 - 16, "Friends", 0, 0, 4, 16 + var4);
					this.getSurface().drawColoredStringCentered(var5 / 4 + var3 + var5 / 2 - 33 - 16, "Clan", 0, 0, 4, var4 + 16);
					this.getSurface().drawColoredStringCentered(var5 / 4 + var3 + var5 / 2 + 16, "Ignore", 0, 0, 4, var4 + 16);
					this.panelSocial.clearList(this.controlSocialPanel);
					this.panelClan.clearList(this.controlClanPanel);
				}
				else { // Clans Disabled
					int l;
					int k = l = GenUtil.buildColor(160, 160, 160);
					if (this.panelSocialTab == 0)
						k = GenUtil.buildColor(220, 220, 220);
					else
						l = GenUtil.buildColor(220, 220, 220);
					this.getSurface().drawBoxAlpha(var3, var4, var5 / 2, 24, k, 128);
					this.getSurface().drawBoxAlpha(var3 + var5 / 2, var4, var5 / 2, 24, l, 128);
					this.getSurface().drawBoxAlpha(var3, var4 + 24, var5, var6 - 24, GenUtil.buildColor(220, 220, 220), 128);
					this.getSurface().drawLineHoriz(var3, var4 + 24, var5, 0);
					this.getSurface().drawLineVert(var3 + var5 / 2, var4, 24, 0);
					this.getSurface().drawLineHoriz(var3, var4 + var6 - 16, var5, 0);
					this.getSurface().drawColoredStringCentered(var3 + var5 / 4, "Friends", 0, 0, 4, var4 + 16);
					this.getSurface().drawColoredStringCentered(var3 + var5 / 4 + var5 / 2, "Ignore", 0, 0, 4, var4 + 16);
					this.panelSocial.clearList(this.controlSocialPanel);
				}

				int index;
				String colorKey;
				int var12;

				// FRIEND TAB
				if (this.panelSocialTab == 0) {
					for (index = 0; index < SocialLists.friendListCount; ++index) {
						if ((SocialLists.friendListArg[index] & 2) == 0) {
							if ((SocialLists.friendListArg[index] & 4) == 0) {
								colorKey = "@red@";
							} else {
								colorKey = "@yel@";
							}
						} else {
							colorKey = "@gre@";
						}

						String var11 = SocialLists.friendList[index];
						var12 = 0;

						for (int var13 = SocialLists.friendList[index].length(); this.getSurface().stringWidth(1,
								var11) > 120; var11 = SocialLists.friendList[index].substring(0, var13 - var12) + "...") {
							++var12;
						}

						this.panelSocial.setListEntry(this.controlSocialPanel, index,
								colorKey + var11 + "~" + (getGameWidth() - 73) + "~" + "@whi@Remove         WWWWWWWWWW", 0,
								(String) null, (String) null);
					}
					this.panelSocial.drawPanel();
				}

				// IGNORE TAB
				if (this.panelSocialTab == 2) {
					for (index = 0; index < SocialLists.ignoreListCount; ++index) {
						colorKey = SocialLists.ignoreListArg0[index];
						int var16 = 0;

						for (var12 = SocialLists.ignoreListArg0[index].length(); this.getSurface().stringWidth(1,
								colorKey) > 120; colorKey = SocialLists.ignoreListArg0[index].substring(0, var12 - var16)
								+ "...") {
							++var16;
						}

						this.panelSocial.setListEntry(this.controlSocialPanel, index,
								"@yel@" + colorKey + "~" + (getGameWidth() - 73) + "~" + "@whi@Remove         WWWWWWWWWW",
								0, (String) null, (String) null);
					}

					this.panelSocial.drawPanel();
				}

				// CLAN TAB
				if (this.panelSocialTab == 1) {
					int listX = var3 + 3;
					int listY = 75;

					int buttonColorA = 0x0A2B56, buttonColorB = 0x0A2B56;

					if(clan.inClan()) {
						this.getSurface().drawString("Clan: @cla@" + clan.getClanName(), listX, listY, 0xFFFFFF, 1);
						listY += 14;
						this.getSurface().drawString("Tag: @cla@< " + clan.getClanTag() + " >", listX, listY, 0xFFFFFF, 1);
						listY += 14;
						this.getSurface().drawString("Leader: @yel@" + clan.getClanLeaderUsername(), listX, listY, 0xFFFFFF, 1);
						for (index = 0; index < SocialLists.clanListCount; ++index) {

							if ((clan.onlineClanMember[index]) == 0) {
								colorKey = "@whi@";
							} else {
								colorKey = "@gre@";
							}

							String clanIsh = clan.username[index];
							var12 = 0;

							for (int var13 = clan.username[index].length(); this.getSurface().stringWidth(1,
									clanIsh) > 120; clanIsh = clan.username[index].substring(0, var13 - var12) + "...") {
								++var12;
							}
							this.panelClan.setListEntry(this.controlClanPanel, index, (clan.clanRank[index] == 0 ? "     ": "") + colorKey + clanIsh + "          ", (clan.clanRank[index] == 1 ? 3 : clan.clanRank[index] == 2 ? 4 : 0),
									(String) null, (String) null);
						}
						if (this.mouseX > var3 + 20 && this.mouseX < var3 + 94 && this.mouseY > var6 + (var4 + 26)
								&& this.mouseY < var6 + var4 + 60) {
							buttonColorA = 0x263751;
							if (getMouseClick() == 1) {
								this.showUiTab = 0;
								String[] inputXMessage = new String[] { "Are you sure you want to leave the clan?" };
								this.showItemModX(inputXMessage, InputXAction.CLAN_LEAVE, false);
								setMouseClick(0);
							}
						}
						this.getSurface().drawBoxAlpha(listX + 17, listY + 141, 74, 34, buttonColorA, 192);
						this.getSurface().drawBoxBorder(listX + 17, 74, listY + 141, 34, 0xBFA086);
						this.getSurface().drawString("Leave Clan", listX + 17 + (74 / 2 - this.getSurface().stringWidth(0, "Leave Clan") / 2), listY + 141 + 34 / 2 + 4, 0xffffff, 0);


						if (this.mouseX > var3 + 88 + 13 && this.mouseX < var3 + 88 + 88 && this.mouseY > var6 + (var4 + 26)
								&& this.mouseY < var6 + var4 + 60) {
							buttonColorB = 0x263751;
							if (getMouseClick() == 1) {
								clan.showClanSetupInterface(clan.inClan());
								this.showUiTab = 0;
								setMouseClick(0);
							}
						}
						this.getSurface().drawBoxAlpha(listX + 17 + 82, listY + 141, 74, 34, buttonColorB, 192);
						this.getSurface().drawBoxBorder(listX + 17 + 82, 74, listY + 141, 34, 0xBFA086);
						this.getSurface().drawString("Clan Setup", listX + 14 + 85 + (74 / 2 - this.getSurface().stringWidth(0, "Clan Setup") / 2), listY + 141 + 34 / 2 + 4, 0xffffff, 0);

					} else {
						this.getSurface().drawString("You are not currently in a Clan", listX + 10, listY, 0xFFFFFF, 1);
						listY += 28;
						this.getSurface().drawWrappedCenteredString("Click on Clan Setup to create your own clan.% %If you are looking to join an existing Clan, click on Clan Search.", listX + 94, listY, 196 - 12, 1, 0xF38F30, true);
						if (this.mouseX > var3 + 20 && this.mouseX < var3 + 94 && this.mouseY > var6 + (var4 - 23)
								&& this.mouseY < var6 + var4 + 12) {
							buttonColorA = 0x263751;
							if (getMouseClick() == 1) {
								clan.showClanSetupInterface(clan.inClan());
								clan.getClanInterface().clanActivePanel = 3;
								clan.getClanInterface().resetAll();
								clan.getClanInterface().sendClanSearch();
								this.showUiTab = 0;
								setMouseClick(0);
							}
						}
						this.getSurface().drawBoxAlpha(listX + 17, listY + 93, 74, 34, buttonColorA, 192);
						this.getSurface().drawBoxBorder(listX + 17, 74, listY + 93, 34, 0xBFA086);
						this.getSurface().drawString("Clan Search", listX + 17 + (74 / 2 - this.getSurface().stringWidth(0, "Clan Search") / 2), listY + 93 + 34 / 2 + 4, 0xffffff, 0);

						if (this.mouseX > var3 + 88 + 13 && this.mouseX < var3 + 88 + 88 && this.mouseY > var6 + (var4 - 23)
								&& this.mouseY < var6 + var4 + 12) {
							buttonColorB = 0x263751;
							if (getMouseClick() == 1) {
								clan.showClanSetupInterface(clan.inClan());
								this.showUiTab = 0;
								setMouseClick(0);
							}
						}
						this.getSurface().drawBoxAlpha(listX + 17 + 82, listY + 93, 74, 34, buttonColorB, 192);
						this.getSurface().drawBoxBorder(listX + 17 + 82, 74, listY + 93, 34, 0xBFA086);
						this.getSurface().drawString("Clan Setup", listX + 14 + 85 + (74 / 2 - this.getSurface().stringWidth(0, "Clan Setup") / 2), listY + 93 + 34 / 2 + 4, 0xffffff, 0);
					}

					this.panelClan.drawPanel();
				}

				this.m_nj = -1;
				this.m_wk = -1;
				int var17;
				if (this.panelSocialTab == 0) {
					index = this.panelSocial.getControlSelectedListIndex((int) this.controlSocialPanel);
					if (index >= 0 && this.mouseX < maxWidth) {
						if (this.mouseX > minWidth) {
							this.m_wk = -(index + 2);
						} else {
							this.m_wk = index;
						}
					}

					this.getSurface().drawColoredStringCentered(var5 / 2 + var3, "Click a name to send a message", 16777215,
							0, 1, 35 + var4);
					if (var3 < this.mouseX && this.mouseX < var3 + var5 && this.mouseY > var6 + (var4 - 16)
							&& this.mouseY < var6 + var4) {
						var17 = 16776960;
					} else {
						var17 = 16777215;
					}

					this.getSurface().drawColoredStringCentered(var5 / 2 + var3, "Click here to add a friend", var17, 0, 1,
							var6 + var4 - 3);
				}

				if (this.panelSocialTab == 2) {
					index = this.panelSocial.getControlSelectedListIndex((int) this.controlSocialPanel);
					if (index >= 0 && this.mouseX < maxWidth) {
						if (this.mouseX <= minWidth) {
							this.m_nj = index;
						} else {
							this.m_nj = -(index + 2);
						}
					}

					this.getSurface().drawColoredStringCentered(var3 + var5 / 2, "Blocking messages from:", 16777215, 0, 1,
							35 + var4);
					if (this.mouseX > var3 && var3 + var5 > this.mouseX && var6 + var4 - 16 < this.mouseY
							&& var6 + var4 > this.mouseY) {
						var17 = 16776960;
					} else {
						var17 = 16777215;
					}

					this.getSurface().drawColoredStringCentered(var5 / 2 + var3, "Click here to add a name", var17, 0, 1,
							var4 + (var6 - 3));
				}

				if (var1) {
					int var15 = this.mouseY - 36;
					var3 = 199 + this.mouseX - this.getSurface().width2;
					// HANDLE FRIEND TAB AND IGNORE TAB
					if (var3 >= 0 && var15 >= 0 && var3 < 196 && var15 < 26) {
						this.panelSocial.handleMouse(var3 - 199 + this.getSurface().width2, var15 + 36,
								this.currentMouseButtonDown, this.lastMouseButtonDown);
						if (var15 <= 24 && this.mouseButtonClick == 1) {
							if (Config.WANT_CLANS) {
								if (var3 < 65 && (this.panelSocialTab == 2 || this.panelSocialTab == 1)) {
									this.panelSocialTab = 0; // Show Friends Tab (Clicked)
									this.panelSocial.resetList(this.controlSocialPanel);
								} else if (var3 > 132 && var3 < 196 && (this.panelSocialTab == 1 || this.panelSocialTab == 0)) {
									this.panelSocialTab = 2; // Show Ignore Tab (Clicked)
									this.panelSocial.resetList(this.controlSocialPanel);
								}
							}
							else {
                if (var3 < 98 && (this.panelSocialTab == 2 || this.panelSocialTab == 1)) {
                  this.panelSocialTab = 0; // Show Friends Tab (Clicked)
                  this.panelSocial.resetList(this.controlSocialPanel);
                } else if (var3 > 98 && (this.panelSocialTab == 1 || this.panelSocialTab == 0)) {
                  this.panelSocialTab = 2; // Show Ignore Tab (Clicked)
                  this.panelSocial.resetList(this.controlSocialPanel);
                }
							}
						}
					}
					// HANDLE CLAN TAB
					if (Config.WANT_CLANS) {
						if(var3 >= 65 && var15 >= 0 && var3 < 132 && var15 < 26) {
							this.panelClan.handleMouse(var3 - 199 + this.getSurface().width2, var15 + 36,
									this.currentMouseButtonDown, this.lastMouseButtonDown);
							if (var15 <= 24 && this.mouseButtonClick == 1) {
								if (var3 > 65 && var3 < 132 && (this.panelSocialTab == 2 || this.panelSocialTab == 0)) {
									this.panelSocialTab = 1; // Show Clan Tab (Clicked)
									this.panelClan.resetList(this.controlClanPanel);
								}
							}
						}
					}

					// Interactions within the panels
					if(var3 >= 0 && var15 >= 0 && var3 < 196 && var15 < 225 && (this.panelSocialTab == 0 || this.panelSocialTab == 2)) {
						this.panelSocial.handleMouse(var3 - 199 + this.getSurface().width2, var15 + 36,
								this.currentMouseButtonDown, this.lastMouseButtonDown);
						if (this.mouseButtonClick >= 1 && this.panelSocialTab == 0) {
							index = this.panelSocial.getControlSelectedListIndex((int) this.controlSocialPanel);
							if (index >= 0 && this.mouseX < maxWidth) {
								if (this.mouseX > minWidth) { // Remove Friend
									this.removeFriend(SocialLists.friendList[index], (byte) 69);
								} else if ((SocialLists.friendListArg[index] & 4) != 0) { // Message Friend
									this.panelSocialPopup_Mode = SocialPopupMode.MESSAGE_FRIEND;
									this.chatMessageTarget = SocialLists.friendList[index];
									this.chatMessageInputCommit = "";
									this.chatMessageInput = "";
								}
							}
						}

						if (this.mouseButtonClick == 1 && this.panelSocialTab == 2) {
							index = this.panelSocial.getControlSelectedListIndex((int) this.controlSocialPanel);
							if (index >= 0 && this.mouseX < maxWidth && this.mouseX > minWidth) {
								this.removeIgnore(SocialLists.ignoreList[index]); // Remove Ignore
							}
						}

						// Add Friend
						if (var15 > 166 && this.mouseButtonClick == 1 && this.panelSocialTab == 0) {
							this.inputTextFinal = "";
							this.inputTextCurrent = "";
							this.panelSocialPopup_Mode = SocialPopupMode.ADD_FRIEND;
						}

						// Add Ignore
						if (var15 > 166 && this.mouseButtonClick == 1 && this.panelSocialTab == 2) {
							this.panelSocialPopup_Mode = SocialPopupMode.ADD_IGNORE;
							this.inputTextCurrent = "";
							this.inputTextFinal = "";
						}

						this.mouseButtonClick = 0;
					}

					// Clan Interactions
					else if (var3 >= 0 && var15 >= 0 && var3 < 196 && var15 < 295 && this.panelSocialTab == 1 && Config.WANT_CLANS) {
						this.panelClan.handleMouse(var3 - 199 + this.getSurface().width2, var15 + 36,
								this.currentMouseButtonDown, this.lastMouseButtonDown);
						if (this.mouseButtonClick >= 1 && this.panelSocialTab == 1) {
							index = this.panelClan.getControlSelectedListIndex((int) this.controlClanPanel);
							if (index >= 0 && this.mouseX < maxWidth) {
								if (mouseButtonClick == 2) {
									if (StringUtil.displayNameToKey(clan.username[index]).equals(StringUtil.displayNameToKey(this.localPlayer.accountName))) {
										return;
									}
									if(clan.isClanLeader() || clan.isAllowed(0)) { // Kick From Clan
										this.menuCommon.addItem_With2Strings("Kick user",
												"@whi@" + clan.username[index], clan.username[index],
												MenuItemAction.CLAN_MENU_KICK, clan.username[index]);
									}
									boolean isOnFriendList = false;
									boolean isOnline = false;

									for (int i = 0; i < SocialLists.friendListCount; ++i) {
										if (StringUtil.displayNameToKey(clan.username[index]).equals(StringUtil.displayNameToKey(SocialLists.friendList[i]))) {
											isOnFriendList = true;
											if ((4 & SocialLists.friendListArg[i]) != 0) {
												isOnline = true;
											}
											break;
										}
									}
									if (isOnFriendList) {
										if (isOnline) {
											this.menuCommon.addItem_With2Strings("Message", "@whi@" + clan.username[index], clan.username[index],
													MenuItemAction.CHAT_MESSAGE, clan.username[index]);
										}
									} else {
										this.menuCommon.addItem_With2Strings("Add friend", "@whi@" + clan.username[index], clan.username[index],
												MenuItemAction.CHAT_ADD_FRIEND, clan.username[index]);

									}
									return;
								}
							}
						}
					}
				}
			} catch (RuntimeException var14) {
				throw GenUtil.makeThrowable(var14, "client.VD(" + var1 + ',' + var2 + ')');
			}
		}

		private final void drawUiTabMagic(boolean var1, byte var2) {
			try {
				int var3 = this.getSurface().width2 - 199;
				byte var4 = 36;
				this.getSurface().drawSprite(mudclient.spriteMedia + 4, var3 - 49, 3);
				short var5 = 196;
				short var6 = 182;
				int var8;
				int var7 = var8 = GenUtil.buildColor(160, 160, 160);
				if (this.m_Ji != 0) {
					var8 = GenUtil.buildColor(220, 220, 220);
				} else {
					var7 = GenUtil.buildColor(220, 220, 220);
				}

				this.getSurface().drawBoxAlpha(var3, var4, var5 / 2, 24, var7, 128);
				this.getSurface().drawBoxAlpha(var5 / 2 + var3, var4, var5 / 2, 24, var8, 128);
				this.getSurface().drawBoxAlpha(var3, var4 + 24, var5, 90, GenUtil.buildColor(220, 220, 220), 128);
				this.getSurface().drawBoxAlpha(var3, 114 + var4, var5, var6 - 24 - 90, GenUtil.buildColor(160, 160, 160),
						128);
				this.getSurface().drawLineHoriz(var3, 24 + var4, var5, 0);
				this.getSurface().drawLineVert(var3 + var5 / 2, 0 + var4, 0, 24);
				this.getSurface().drawLineHoriz(var3, var4 + 113, var5, 0);
				if (var2 == -74) {
					this.getSurface().drawColoredStringCentered(var5 / 4 + var3, "Magic", 0, var2 + 74, 4, 16 + var4);
					this.getSurface().drawColoredStringCentered(var3 + var5 / 4 + var5 / 2, "Prayers", 0, 0, 4, 16 + var4);
					int var9;
					int var10;
					String var11;
					int var12;
					int var18;
					if (this.m_Ji == 0) {
						this.panelMagic.clearList(this.controlMagicPanel);
						var9 = 0;

						int var13;
						for (var10 = 0; var10 < EntityHandler.spellCount(); ++var10) {
							var11 = "@yel@";

							for (Entry<?, ?> e : EntityHandler.getSpellDef(var10).getRunesRequired()) {
								var13 = (Integer) e.getKey();
								if (!this.hasRunes(var13, (Integer) e.getValue())) {
									var11 = "@whi@";
									break;
								}
							}

							var12 = this.playerStatCurrent[6];
							if (EntityHandler.getSpellDef(var10).getReqLevel() > var12) {
								var11 = "@bla@";
							}

							this.panelMagic
							.setListEntry(this.controlMagicPanel, var9++,
									var11 + "Level " + EntityHandler.getSpellDef(var10).getReqLevel() + ": "
											+ EntityHandler.getSpellDef(var10).getName(),
											0, (String) null, (String) null);
						}

						this.panelMagic.drawPanel();
						var10 = this.panelMagic.getControlSelectedListIndex((int) this.controlMagicPanel);
						if (var10 != -1) {
							this.getSurface().drawString(
									"Level " + EntityHandler.getSpellDef(var10).getReqLevel() + ": "
											+ EntityHandler.getSpellDef(var10).getName(),
											2 + var3, var4 + 124, 16776960, 1);
							this.getSurface().drawString(EntityHandler.getSpellDef(var10).getDescription(), 2 + var3,
									136 + var4, 16777215, 0);
							var18 = 0;
							for (Entry<Integer, Integer> e : EntityHandler.getSpellDef(var10).getRunesRequired()) {
								var12 = e.getKey();
								this.getSurface().drawSprite(
										EntityHandler.getItemDef(var12).getSprite() + mudclient.spriteItem,
										2 + var3 + var18 * 44, var4 + 150);
								var13 = this.getInventoryCount((int) var12);
								int var14 = e.getValue();
								String var15 = "@red@";
								if (this.hasRunes(var12, var14)) {
									var15 = "@gre@";
								}
								this.getSurface().drawString(var15 + var13 + "/" + var14, 2 + var3 + var18 * 44, var4 + 150,
										16777215, 1);
								var18++;
							}
						} else {
							this.getSurface().drawString("Point at a spell for a description", var3 + 2, var4 + 124, 0, 1);
						}
					}

					if (this.m_Ji == 1) {
						this.panelMagic.clearList(this.controlMagicPanel);
						var9 = 0;

						for (var10 = 0; var10 < EntityHandler.prayerCount(); ++var10) {
							var11 = "@whi@";
							if (EntityHandler.getPrayerDef(var10).getReqLevel() > this.playerStatBase[5]) {
								var11 = "@bla@";
							}

							if (this.prayerOn[var10]) {
								var11 = "@gre@";
							}

							this.panelMagic
							.setListEntry(this.controlMagicPanel, var9++,
									var11 + "Level " + EntityHandler.getPrayerDef(var10).getReqLevel() + ": "
											+ EntityHandler.getPrayerDef(var10).getName(),
											0, (String) null, (String) null);
						}

						this.panelMagic.drawPanel();
						var10 = this.panelMagic.getControlSelectedListIndex(this.controlMagicPanel);
						if (var10 == -1) {
							this.getSurface().drawString("Point at a prayer for a description", var3 + 2, var4 + 124, 0, 1);
						} else {
							this.getSurface()
							.drawColoredStringCentered(var3 + var5 / 2,
									"Level " + EntityHandler.getPrayerDef(var10).getReqLevel() + ": "
											+ EntityHandler.getPrayerDef(var10).getName(),
											16776960, 0, 1, var4 + 130);
							this.getSurface().drawColoredStringCentered(var3 + var5 / 2,
									EntityHandler.getPrayerDef(var10).getDescription(), 16777215, 0, 0, 145 + var4);
							this.getSurface().drawColoredStringCentered(var3 + var5 / 2,
									"Drain rate: " + EntityHandler.getPrayerDef(var10).getDrainRate(), 0, 0, 1, 160 + var4);
						}
						this.getSurface().drawColoredStringCentered(var3 + var5 / 2,
								"Prayer points: " + this.playerStatCurrent[5] + "/" + this.playerStatBase[5], 0, 0, 1, 175 + var4);
					}

					if (var1) {
						var3 = 199 - this.getSurface().width2 + this.mouseX;
						int var17 = this.mouseY - 36;
						if (var3 >= 0 && var17 >= 0 && var3 < 196 && var17 < 182) {
							this.panelMagic.handleMouse(var3 + (this.getSurface().width2 - 199), var17 + 36,
									this.currentMouseButtonDown, this.lastMouseButtonDown);
							if (var17 <= 24 && this.mouseButtonClick == 1) {
								if (var3 < 98 && this.m_Ji == 1) {
									this.m_Ji = 0;
									prayerMenuIndex = this.panelMagic.getScrollPosition(this.controlMagicPanel);
									this.panelMagic.resetListToIndex(this.controlMagicPanel, magicMenuIndex);
								} else if (var3 > 98 && this.m_Ji == 0) {
									this.m_Ji = 1;
									magicMenuIndex = this.panelMagic.getScrollPosition(this.controlMagicPanel);
									this.panelMagic.resetListToIndex(this.controlMagicPanel, prayerMenuIndex);
								}
							}

							if (this.mouseButtonClick == 1 && this.m_Ji == 0) {
								var9 = this.panelMagic.getControlSelectedListIndex(this.controlMagicPanel);
								if (var9 != -1) {
									var10 = this.playerStatCurrent[6];
									if (var10 < EntityHandler.getSpellDef(var9).getReqLevel()) {
										this.showMessage(false, (String) null,
												"Your magic ability is not high enough for this spell", MessageType.GAME, 0,
												(String) null, (String) null);
									} else {
										int k3 = 0;
										for (Entry<Integer, Integer> e : EntityHandler.getSpellDef(var9)
												.getRunesRequired()) {
											if (!hasRunes(e.getKey(), e.getValue())) {
												this.showMessage(false, (String) null,
														"You don\'t have all the reagents you need for this spell",
														MessageType.GAME, 0, (String) null, (String) null);
												k3 = -1;
												break;
											}
											k3++;
										}
										if (k3 == EntityHandler.getSpellDef(var9).getRuneCount()) {
											this.selectedSpell = var9;
											lastSelectedSpell = var9;
											this.selectedItemInventoryIndex = -1;
											if (EntityHandler.getSpellDef(var9).getSpellType() == 3 && var9 != 16) {
												showUiTab = 1;
											}
										}
									}
								}
							}

							if (this.mouseButtonClick == 1 && this.m_Ji == 1) {
								var9 = this.panelMagic.getControlSelectedListIndex((int) this.controlMagicPanel);
								if (var9 != -1) {
									var10 = this.playerStatBase[5];
									if (var10 < EntityHandler.getPrayerDef(var9).getReqLevel()) {
										this.showMessage(false, (String) null,
												"Your prayer ability is not high enough for this prayer", MessageType.GAME,
												0, (String) null, (String) null);
									} else if (this.playerStatCurrent[5] == 0) {
										this.showMessage(false, (String) null,
												"You have run out of prayer points. Return to a church to recharge",
												MessageType.GAME, 0, (String) null, (String) null);
									} else if (!this.prayerOn[var9]) {
										this.getClientStream().newPacket(60);
										this.getClientStream().writeBuffer1.putByte(var9);
										this.getClientStream().finishPacket();
										this.prayerOn[var9] = true;
										this.playSoundFile((String) "prayeron");
									} else {
										this.getClientStream().newPacket(254);
										this.getClientStream().writeBuffer1.putByte(var9);
										this.getClientStream().finishPacket();
										this.prayerOn[var9] = false;
										this.playSoundFile((String) "prayeroff");
									}
								}
							}

							this.mouseButtonClick = 0;
						}

					}
				}
			} catch (RuntimeException var16) {
				throw GenUtil.makeThrowable(var16, "client.GA(" + var1 + ',' + var2 + ')');
			}
		}

		private final void drawUiTabMinimap(boolean var1, byte var2) {
			try {
				int var3 = this.getSurface().width2 - 199;
				short var4 = 156;
				short var5 = 152;
				this.getSurface().drawSprite(2 + mudclient.spriteMedia, var3 - 49, 3);
				var3 += 40;
				this.getSurface().drawBox(var3, 36, var4, var5, 0);
				this.getSurface().setClip(var3, var4 + var3, 36 + var5, 36);
				if (var2 <= 119) {
					this.characterHealthBar = (int[]) null;
				}

				int var6 = 192 + this.minimapRandom_2;
				int var7 = 255 & this.cameraRotation + this.minimapRandom_1;
				int mX = var6 * (this.localPlayer.currentX - 6040) * 3 / 2048;
				int mZ = var6 * (this.localPlayer.currentZ - 6040) * 3 / 2048;
				int var10 = FastMath.trigTable_1024[1023 & 1024 - var7 * 4];
				int var11 = FastMath.trigTable_1024[(1023 & 1024 - var7 * 4) + 1024];
				int var12 = mX * var11 + var10 * mZ >> 18;
				mZ = mZ * var11 - mX * var10 >> 18;
									this.getSurface().drawMinimapSprite(mudclient.spriteMedia - 1, 36 - (-(var5 / 2) - mZ),
											var4 / 2 + var3 - var12, 842218000, var6, 255 & 64 + var7);

									int var13;
									for (var13 = 0; var13 < this.gameObjectInstanceCount; ++var13) {
										mZ = var6 * (64 + (this.tileSize * this.gameObjectInstanceZ[var13] - this.localPlayer.currentZ)) * 3
												/ 2048;
										mX = (this.tileSize * this.gameObjectInstanceX[var13] - (this.localPlayer.currentX - 64)) * var6 * 3
												/ 2048;
										var12 = var11 * mX + mZ * var10 >> 18;
									mZ = var11 * mZ - var10 * mX >> 18;
						this.drawMinimapEntity('\uffff', var12 + var3 + var4 / 2, (byte) -61, 36 - mZ + var5 / 2);
									}

									for (var13 = 0; var13 < this.groundItemCount; ++var13) {
										mX = var6 * (64 + this.groundItemX[var13] * this.tileSize - this.localPlayer.currentX) * 3 / 2048;
										mZ = var6 * 3 * (64 + this.tileSize * this.groundItemZ[var13] - this.localPlayer.currentZ) / 2048;
										var12 = var11 * mX + var10 * mZ >> 18;
									mZ = var11 * mZ - mX * var10 >> 18;
									this.drawMinimapEntity(16711680, var3 - (-(var4 / 2) - var12), (byte) -53, var5 / 2 + 36 - mZ);
									}

									RSCharacter var14;
									for (var13 = 0; this.npcCount > var13; ++var13) {
										var14 = this.npcs[var13];
										mZ = var6 * (var14.currentZ - this.localPlayer.currentZ) * 3 / 2048;
										mX = (var14.currentX - this.localPlayer.currentX) * var6 * 3 / 2048;
										var12 = mZ * var10 + mX * var11 >> 18;
										mZ = var11 * mZ - mX * var10 >> 18;
										this.drawMinimapEntity(16776960, var4 / 2 + var3 + var12, (byte) -93, var5 / 2 - mZ + 36);
									}

									for (var13 = 0; this.playerCount > var13; ++var13) {
										var14 = this.players[var13];
										mX = (var14.currentX - this.localPlayer.currentX) * var6 * 3 / 2048;
										mZ = var6 * (var14.currentZ - this.localPlayer.currentZ) * 3 / 2048;
										var12 = mX * var11 + var10 * mZ >> 18;
										mZ = var11 * mZ - mX * var10 >> 18;
										int var15 = 16777215;
										String var16 = StringUtil.displayNameToKey(var14.accountName);
										if (null != var16) {
											for (int var17 = 0; var17 < SocialLists.friendListCount; ++var17) {
												if (var16.equals(StringUtil.displayNameToKey(SocialLists.friendList[var17]))
														&& (SocialLists.friendListArg[var17] & 2) != 0) {
													var15 = '\uff00';
													break;
												}
											}
											for (int var17 = 0; var17 < SocialLists.clanListCount; ++var17) {
												if (var16.equals(StringUtil.displayNameToKey(clan.username[var17]))
														&& (clan.onlineClanMember[var17]) == 1) {
													var15 = 0xFF00FF;
													surface.drawCircle(var12 + var3 + var4 / 2, 36 - mZ + var5 / 2, 2, var15, 255, 0);
												}
											}
										}

										this.drawMinimapEntity(var15, var12 + var3 + var4 / 2, (byte) -67, 36 - mZ + var5 / 2);
									}

									this.getSurface().drawCircle(var3 + var4 / 2, var5 / 2 + 36, 2, 16777215, 255, -1057205208);
									this.getSurface().drawMinimapSprite(mudclient.spriteMedia + 24, 55, var3 + 19, 842218000, 128,
											255 & this.cameraRotation + 128);
									this.getSurface().setClip(0, this.getGameWidth(), this.getGameHeight() + 12, 0);
									if (var1) {
										var3 = 199 - this.getSurface().width2 + this.mouseX;
										var13 = this.mouseY - 36;
										if (var3 >= 40 && var13 >= 0 && var3 < 196 && var13 < 152) {
											var5 = 152;
											var3 = this.getSurface().width2 - 199;
											var4 = 156;
											var6 = 192 + this.minimapRandom_2;
											var7 = 255 & this.cameraRotation + this.minimapRandom_1;
											var3 += 40;
											mZ = (this.mouseY - var5 / 2 - 36) * 16384 / (var6 * 3);
											mX = (this.mouseX + (-(var4 / 2) - var3)) * 16384 / (var6 * 3);
											var10 = FastMath.trigTable_1024[1024 - var7 * 4 & 1023];
											var11 = FastMath.trigTable_1024[1024 + (1023 & 1024 - var7 * 4)];
											var12 = mZ * var10 + var11 * mX >> 15;
										mZ = var11 * mZ - var10 * mX >> 15;
										mX = var12 + this.localPlayer.currentX;
										mZ = this.localPlayer.currentZ - mZ;
										if (this.mouseButtonClick == 1) {
											this.walkToActionSource(this.playerLocalX, this.playerLocalZ, mX / 128, (int) (mZ / 128),
													false);
										}
										this.mouseButtonClick = 0;
										}

									}
			} catch (RuntimeException var18) {
				throw GenUtil.makeThrowable(var18, "client.HD(" + var1 + ',' + var2 + ')');
			}
		}

		private final void drawUiTabOptions(int var1, boolean var2) {
			try {
				int var3 = this.getSurface().width2 - 199;
				this.getSurface().drawSprite(mudclient.spriteMedia + 6, var3 - 49, 3);
				byte var4 = 36 + 25;
				short var5 = 196;

				int chosenColor = GenUtil.buildColor(220, 220, 220);
				int unchosenColor = GenUtil.buildColor(160, 160, 160);

				if (Config.isAndroid()) {
					if (this.settingTab == 0) {
						this.getSurface().drawBoxAlpha(var3, var4 - 25, var5/3, 24, chosenColor, 128);
						this.getSurface().drawBoxAlpha(var5/3 + var3, var4 - 25, (var5/3) + 1, 24, unchosenColor, 128);
						this.getSurface().drawBoxAlpha(2*(var5/3) + var3 + 1, var4 - 25, var5/3, 24, unchosenColor, 128);
					} else if (this.settingTab == 1) {
						this.getSurface().drawBoxAlpha(var3, var4 - 25, var5/3, 24, unchosenColor, 128);
						this.getSurface().drawBoxAlpha(var5/3 + var3, var4 - 25, (var5/3) + 1, 24, chosenColor, 128);
						this.getSurface().drawBoxAlpha(2*(var5/3) + var3 + 1, var4 - 25, var5/3, 24, unchosenColor, 128);
					} else if (this.settingTab == 2) {
						this.getSurface().drawBoxAlpha(var3, var4 - 25, var5/3, 24, unchosenColor, 128);
						this.getSurface().drawBoxAlpha(var5/3 + var3, var4 - 25, (var5/3) + 1, 24, unchosenColor, 128);
						this.getSurface().drawBoxAlpha(2*(var5/3) + var3 + 1, var4 - 25, var5/3, 24, chosenColor, 128);
					}

					this.getSurface().drawLineHoriz(var3, 24 + var4 - 25, var5, 0);
					this.getSurface().drawLineVert(var3 + var5/3, 0 + var4 - 25, 0, 24);
					this.getSurface().drawLineVert(var3 + 2*(var5/3) + 1, 0 + var4 - 25, 0, 24);

					this.getSurface().drawColoredStringCentered(var5/4 + var3 - 16, "Social", 0, 0, 4, 16 + var4 - 25);
					this.getSurface().drawColoredStringCentered(var3 + var5/4 + var5/3 - 16, "General", 0, 0, 4, 16 + var4 - 25);
					this.getSurface().drawColoredStringCentered(var3 + var5/4 + 2* var5/3 - 15, "Android", 0, 0, 4, 16 + var4 - 25);

				} else {
					if (this.settingTab == 0) {
						this.getSurface().drawBoxAlpha(var3, var4 - 25, var5/2, 24, chosenColor, 128);
						this.getSurface().drawBoxAlpha(var5/2 + var3, var4 - 25, var5/2, 24, unchosenColor, 128);
					} else if (this.settingTab == 1) {
						this.getSurface().drawBoxAlpha(var3, var4 - 25, var5/2, 24, unchosenColor, 128);
						this.getSurface().drawBoxAlpha(var5/2 + var3, var4 - 25, var5/2, 24, chosenColor, 128);
					}

					this.getSurface().drawLineHoriz(var3, 24 + var4 - 25, var5, 0);
					this.getSurface().drawLineVert(var3 + var5/2, 0 + var4 - 25, 0, 24);

					this.getSurface().drawColoredStringCentered(var5/4 + var3, "Social", 0, 0, 4, 16 + var4 - 25);
					this.getSurface().drawColoredStringCentered(var3 + var5/4 + var5/2, "General", 0, 0, 4, 16 + var4 - 25);
				}

				this.getSurface().drawBoxAlpha(var3, 36 + 25, var5, 200, GenUtil.buildColor(181, 181, 181), 160);
				this.getSurface().drawBoxAlpha(var3, 261, var5, 40, GenUtil.buildColor(201, 201, 201), 160);

				int var6 = 3 + var3;
				int var7 = var4 + 15;
				if (this.settingTab == 0) {
					this.getSurface().drawString("Privacy settings", 3 + var3, var7, 0, 1);
					var7 += 15;
					//this.getSurface().drawString("all people not on your friends list", var3 + 3, var7, 0, 1);
					//var7 += 15;
					if (this.settingsBlockChat != 0) {
						this.getSurface().drawString("Block chat messages: @gre@On", 3 + var3, var7, 16777215, 1);
					} else {
						this.getSurface().drawString("Block chat messages: @red@Off", 3 + var3, var7, 16777215, 1);
					}

					var7 += 15;
					if (this.settingsBlockPrivate == 0) {
						this.getSurface().drawString("Block private messages: @red@Off", 3 + var3, var7, 16777215, 1);
					} else {
						this.getSurface().drawString("Block private messages: @gre@On", var3 + 3, var7, 16777215, 1);
					}

					var7 += 15;
					if (this.settingsBlockGlobal == 1) {
						this.getSurface().drawString("Block global messages: @red@None", 3 + var3, var7, 16777215, 1);
					} else if(this.settingsBlockGlobal == 2) {
						this.getSurface().drawString("Block global messages: @gre@All", var3 + 3, var7, 16777215, 1);
					} else if(this.settingsBlockGlobal == 3) {
						this.getSurface().drawString("Block global messages: @or1@Pking", var3 + 3, var7, 16777215, 1);
					} else if(this.settingsBlockGlobal == 4) {
						this.getSurface().drawString("Block global messages: @gr1@General", var3 + 3, var7, 16777215, 1);
					}

					var7 += 15;
					if (this.settingsBlockTrade != 0) {
						this.getSurface().drawString("Block trade requests: @gre@On", var3 + 3, var7, 16777215, 1);
					} else {
						this.getSurface().drawString("Block trade requests: @red@Off", 3 + var3, var7, 16777215, 1);
					}

					var7 += 15;
					if (this.settingsBlockDuel != 0) {
						this.getSurface().drawString("Block duel requests: @gre@On", var3 + 3, var7, 16777215, 1);
					} else {
						this.getSurface().drawString("Block duel requests: @red@Off", 3 + var3, var7, 16777215, 1);
					}

					if(Config.WANT_CLANS) {
						var7 += 20;
						this.getSurface().drawString("Clan settings", var3 + 3, var7, 0, 1);
					}
					if(Config.SHOW_FLOATING_NAMETAGS) {
						var7 += 15;
						if (!Config.NAME_CLAN_TAG_OVERLAY) {
							this.getSurface().drawString("Name and Clan Tag - @red@Off", var6, var7, 16777215, 1);
						} else {
							this.getSurface().drawString("Name and Clan Tag - @gre@On", var6, var7, 16777215, 1);
						}
					}

					if(Config.WANT_CLANS) {
						var7 += 15;
						if (!this.clanInviteBlockSetting) {
							this.getSurface().drawString("Clan Invitation - @gre@Receive", var6, var7, 16777215, 1);
						} else {
							this.getSurface().drawString("Clan Invitation - @red@Block", var6, var7, 16777215, 1);
						}
					}

					int var8;
					if (this.insideTutorial) {
						var7 += 20;
						this.getSurface().drawString("Skip Tutorial Island", 3 + var3, var7, 0, 1);
						var7 += 15;
						var8 = 16777215;
						if (var6 < this.mouseX && this.mouseX < var6 + var5 && var7 - 12 < this.mouseY
								&& this.mouseY < 4 + var7) {
							var8 = 16776960;
						}
						this.getSurface().drawString("Skip the tutorial", var6, var7, var8, 1);
						var7 += 35;
					} else {
						var7 += 70;
					}

					this.getSurface().drawString("Always logout when you finish", var6, var7, 0, 1);
					var8 = 16777215;
					var7 += 15;
					if (var6 < this.mouseX && var6 + var5 > this.mouseX && var7 - 12 < this.mouseY && this.mouseY < 4 + var7) {
						var8 = 16776960;
					}

					this.getSurface().drawString("Click here to logout", var3 + 3, var7, var8, 1);
				}

				/* Settings Definitions */
				if (this.settingTab == 1) {
					this.panelSettings.clearList(this.controlSettingPanel);
					int index = 0;
					this.getSurface().drawString("Game options", 3 + var3, var7, 0, 1);

					// Camera angle mode
					if (this.optionCameraModeAuto) {
						this.panelSettings.setListEntry(this.controlSettingPanel, index++,
								"@whi@Camera angle mode - @gre@Auto", 0, (String) null, (String) null);
					} else {
						this.panelSettings.setListEntry(this.controlSettingPanel, index++,
								"@whi@Camera angle mode - @red@Manual", 0, (String) null, (String) null);
					}

					// Mouse Buttons
					if (this.optionMouseButtonOne) {
						this.panelSettings.setListEntry(this.controlSettingPanel, index++,
								"@whi@Mouse Buttons - @red@One", 1, (String) null, (String) null);
					} else {
						this.panelSettings.setListEntry(this.controlSettingPanel, index++,
								"@whi@Mouse Buttons - @gre@Two", 1, (String) null, (String) null);
					}

					// Sound Effects
					if (this.optionSoundDisabled) {
						this.panelSettings.setListEntry(this.controlSettingPanel, index++,
								"@whi@Sound Effects - @red@Off", 2, (String) null, (String) null);
					} else {
						this.panelSettings.setListEntry(this.controlSettingPanel, index++,
								"@whi@Sound Effects - @gre@On", 2, (String) null, (String) null);
					}

					// Batch Progress Bar
					if (!Config.BATCH_PROGRESS_BAR) {
						this.panelSettings.setListEntry(this.controlSettingPanel, index++,
								"@whi@Batch Progress Bar - @red@Off", 3, (String) null, (String) null);
					} else {
						this.panelSettings.setListEntry(this.controlSettingPanel, index++,
								"@whi@Batch Progress Bar - @gre@On", 3, (String) null, (String) null);
					}

					// Experience Drops
					if (!Config.EXPERIENCE_DROPS) {
						this.panelSettings.setListEntry(this.controlSettingPanel, index++,
								"@whi@Experience Drops - @red@Off", 4, (String) null, (String) null);
					} else {
						this.panelSettings.setListEntry(this.controlSettingPanel, index++,
								"@whi@Experience Drops - @gre@On", 4, (String) null, (String) null);
					}

					// Zoom View
					this.panelSettings.setListEntry(this.controlSettingPanel, index++,
							"@whi@Zoom View - " + (Config.ZOOM == 0 ? "@yel@Normal" : Config.ZOOM == 1 ? "@ora@Far" : Config.ZOOM == 2 ? "@red@Super" : "@gre@Near"), 5, (String) null, (String) null);

					// Fog
					if (Config.FOG_TOGGLE) {
						if (!Config.SHOW_FOG) {
							this.panelSettings.setListEntry(this.controlSettingPanel, index++,
									"@whi@Fog - @red@Off", 6, (String) null, (String) null);
						} else {
							this.panelSettings.setListEntry(this.controlSettingPanel, index++,
									"@whi@Fog - @gre@On", 6, (String) null, (String) null);
						}
					}

					// Show Roof
					if (!Config.SHOW_ROOF) {
						this.panelSettings.setListEntry(this.controlSettingPanel, index++,
								"@whi@Show Roof - @red@Off", 7, (String) null, (String) null);
					} else {
						this.panelSettings.setListEntry(this.controlSettingPanel, index++,
								"@whi@Show Roof - @gre@On", 7, (String) null, (String) null);
					}

					// Ground Items
					if (Config.GROUND_ITEM_TOGGLE) {
						this.panelSettings.setListEntry(this.controlSettingPanel, index++,
								"@whi@Ground Items - " + (Config.SHOW_GROUND_ITEMS == 0 ? "@gre@Show ALL"
										: Config.SHOW_GROUND_ITEMS == 1 ? "@red@Hide ALL"
												: Config.SHOW_GROUND_ITEMS == 2 ? "@gr1@Only Bones" : "@ora@No Bones"), 8, (String) null, (String) null);
					}

					// Auto Message Switch
					if (Config.AUTO_MESSAGE_SWITCH_TOGGLE) {
						if (!Config.MESSAGE_TAB_SWITCH) {
							this.panelSettings.setListEntry(this.controlSettingPanel, index++,
									"@whi@Auto Message Switch - @red@Off", 9, (String) null, (String) null);
						} else {
							this.panelSettings.setListEntry(this.controlSettingPanel, index++,
									"@whi@Auto Message Switch - @gre@On", 9, (String) null, (String) null);
						}
					}

					// Side Menu
					if (!Config.SIDE_MENU_OVERLAY) {
						this.panelSettings.setListEntry(this.controlSettingPanel, index++,
								"@whi@Side Menu - @red@Off", 10, (String) null, (String) null);
					} else {
						this.panelSettings.setListEntry(this.controlSettingPanel, index++,
								"@whi@Side Menu - @gre@On", 10, (String) null, (String) null);
					}

					// Kill Feed
					if (Config.WANT_KILL_FEED) {
						if (!Config.KILL_FEED) {
							this.panelSettings.setListEntry(this.controlSettingPanel, index++,
									"@whi@Kill Feed - @red@Off", 11, (String) null, (String) null);
						} else {
							this.panelSettings.setListEntry(this.controlSettingPanel, index++,
									"@whi@Kill Feed - @gre@On", 11, (String) null, (String) null);
						}
					}

					// Combat Style
					this.panelSettings.setListEntry(this.controlSettingPanel, index++, "@whi@Combat Style - " + (this.combatStyle == 0 ? "@yel@Controlled" : this.combatStyle == 1 ? "@red@Aggressive" : this.combatStyle == 2 ? "@ora@Accurate" : "@gre@Defensive"), 12, (String) null, (String) null);

					// Fightmode Selector
					this.panelSettings.setListEntry(this.controlSettingPanel, index++,
							"@whi@Fightmode Selector - " + (Config.FIGHT_MENU == 0 ? "@red@Never"
									: Config.FIGHT_MENU == 1 ? "@yel@In Combat" : "@gre@Always"), 13, (String) null, (String) null);

					// Experience Counter
					this.panelSettings.setListEntry(this.controlSettingPanel, index++,
							"@whi@Experience Counter - " + (Config.EXPERIENCE_COUNTER == 0 ? "@red@Never"
									: Config.EXPERIENCE_COUNTER == 1 ? "@yel@Recent" : "@gre@Always"), 14, (String) null, (String) null);

					// Inventory Count
					if (!Config.INV_COUNT) {
						this.panelSettings.setListEntry(this.controlSettingPanel, index++,
								"@whi@Inventory Count - @red@Off", 15, (String) null, (String) null);
					} else {
						this.panelSettings.setListEntry(this.controlSettingPanel, index++,
								"@whi@Inventory Count - @gre@On", 15, (String) null, (String) null);
					}

					var7 += 195;
					int onDeathColor = 16777215;

					if (var6 < this.mouseX && var6 + var5 > this.mouseX && var7 - 12 < this.mouseY && this.mouseY < 4 + var7) {
						onDeathColor = 16776960;
					}
					this.getSurface().drawString("Items on death", var3 + 3, var7, onDeathColor, 1);

					// var7 += 195;
					// this.getSurface().drawString("Always logout when you finish", var6, var7, 0, 1);

					int var8 = 16777215;
					var7 += 15;
					if (var6 < this.mouseX && var6 + var5 > this.mouseX && var7 - 12 < this.mouseY && this.mouseY < 4 + var7) {
						var8 = 16776960;
					}
					this.getSurface().drawString("Click here to logout", var3 + 3, var7, var8, 1);

					this.panelSettings.drawPanel();
				}

				if (this.settingTab == 2) {
					this.panelSettings.clearList(this.controlSettingPanel);
					int index = 0;
					this.getSurface().drawString("Android options", 3 + var3, var7, 0, 1);
					this.panelSettings.setListEntry(this.controlSettingPanel, index++,
							"@whi@Hold-time for Menu - @gre@" + Config.LONG_PRESS_TIMER + "ms", 0, (String) null, (String) null);

					this.panelSettings.setListEntry(this.controlSettingPanel, index++,
							"@whi@Menu Size - @lre@Font " + (Config.MENU_SIZE), 1, (String) null, (String) null);

					if (!Config.HOLD_AND_CHOOSE) {
						this.panelSettings.setListEntry(this.controlSettingPanel, index++,
								"@whi@Hold and Choose - @red@Off", 2, (String) null, (String) null);
					} else {
						this.panelSettings.setListEntry(this.controlSettingPanel, index++,
								"@whi@Hold and Choose - @gre@On", 2, (String) null, (String) null);
					}

					if (!Config.SWIPE_TO_SCROLL) {
						this.panelSettings.setListEntry(this.controlSettingPanel, index++,
								"@whi@Swipe to Scroll - @red@Off", 3, (String) null, (String) null);
					} else {
						this.panelSettings.setListEntry(this.controlSettingPanel, index++,
								"@whi@Swipe to Scroll - @gre@On", 3, (String) null, (String) null);
					}

					if (!Config.SWIPE_TO_ROTATE) {
						this.panelSettings.setListEntry(this.controlSettingPanel, index++,
								"@whi@Swipe to Rotate - @red@Off", 4, (String) null, (String) null);
					} else {
						this.panelSettings.setListEntry(this.controlSettingPanel, index++,
								"@whi@Swipe to Rotate - @gre@On", 4, (String) null, (String) null);
					}

					var7 += 195;
					this.getSurface().drawString("Always logout when you finish", var6, var7, 0, 1);
					int var8 = 16777215;
					var7 +=15;
					if (var6 < this.mouseX && var6 + var5 > this.mouseX && var7 - 12 < this.mouseY && this.mouseY < 4 + var7) {
						var8 = 16776960;
					}
					this.getSurface().drawString("Click here to logout", var3 + 3, var7, var8, 1);

					this.panelSettings.drawPanel();
				}

				if (var2) {
					var3 = 199 - this.getSurface().width2 + this.mouseX;
					int var13 = this.mouseY - 36;
					if (var3 >= 0 && var13 >= 0 && var3 < 196 && var13 < 295) {
						this.panelSettings.handleMouse(this.getMouseX(), this.getMouseY(), this.getMouseButtonDown(), this.getLastMouseDown());

						if (Config.isAndroid() && var13 <= 24 && this.mouseButtonClick == 1) {
							if (var3 < 66 && (this.settingTab == 1 || this.settingTab == 2)) {
								this.settingTab = 0;
								this.panelSettings.resetList(this.controlSettingPanel);
							} else if (var3 >= 66 && var3 <= 131
									&& (this.settingTab == 0 || this.settingTab == 2)) {
								this.settingTab = 1;
								this.panelSettings.resetList(this.controlSettingPanel);
							} else if (var3 > 131 && (this.settingTab == 0 || this.settingTab == 1)) {
								this.settingTab = 2;
								this.panelSettings.resetList(this.controlSettingPanel);
							}
						}
						else if (!Config.isAndroid() && var13 <= 24 && this.mouseButtonClick == 1) {
							if (var13 <= 24 && this.mouseButtonClick == 1) {
								if (var3 < 98 && this.settingTab == 1) {
									this.settingTab = 0; // Social Settings Panel
									this.panelSettings.resetList(this.controlSettingPanel);
								} else if (var3 >= 98 && this.settingTab == 0) {
									this.settingTab = 1; // General Settings Panel
									this.panelSettings.resetList(this.controlSettingPanel);
								}
							}
						}

						int var9 = this.getSurface().width2 - 199;
						var6 = var9 + 3;
						byte var10 = 36;
						var5 = 184;
						var7 = 30 + var10 + 25;
						if (this.settingTab == 1) {
							int checkPosition = this.panelSettings.getControlSelectedListIndex(this.controlSettingPanel);
							if (checkPosition < 0) return;
							int settingIndex = this.panelSettings.getControlSelectedListInt(this.controlSettingPanel, checkPosition);

							if (settingIndex == 0 && this.mouseButtonClick == 1) {
								this.optionCameraModeAuto = !this.optionCameraModeAuto;
								this.getClientStream().newPacket(111);
								this.getClientStream().writeBuffer1.putByte(0);
								this.getClientStream().writeBuffer1.putByte(this.optionCameraModeAuto ? 1 : 0);
								this.getClientStream().finishPacket();
							}

							if (settingIndex == 1 && this.mouseButtonClick == 1) {
								this.optionMouseButtonOne = !this.optionMouseButtonOne;
								this.getClientStream().newPacket(111);
								this.getClientStream().writeBuffer1.putByte(1);
								this.getClientStream().writeBuffer1.putByte(this.optionMouseButtonOne ? 1 : 0);
								this.getClientStream().finishPacket();

							}

							if (settingIndex == 2 && this.mouseButtonClick == 1) {
								this.optionSoundDisabled = !this.optionSoundDisabled;
								this.getClientStream().newPacket(111);
								this.getClientStream().writeBuffer1.putByte(2);
								this.getClientStream().writeBuffer1.putByte(this.optionSoundDisabled ? 1 : 0);
								this.getClientStream().finishPacket();
							}

							// Batch Progress Bar
							if (settingIndex == 3 && this.mouseButtonClick == 1) {
								Config.BATCH_PROGRESS_BAR = !Config.BATCH_PROGRESS_BAR;
								Config.saveConfiguration(false);
							}

							// Experience Drops
							if (settingIndex == 4 && this.mouseButtonClick == 1) {
								Config.EXPERIENCE_DROPS = !Config.EXPERIENCE_DROPS;
								Config.saveConfiguration(false);
							}

							// Zoom View
							if (settingIndex == 5 && this.mouseButtonClick == 1) {
								this.cameraZoom = 750;
								Config.ZOOM++;
								if (Config.ZOOM == 4)
									Config.ZOOM = 0;
								Config.saveConfiguration(false);
							}

							// Fog
							if (settingIndex == 6 && this.mouseButtonClick == 1 && Config.FOG_TOGGLE) {
								Config.SHOW_FOG = !Config.SHOW_FOG;
								Config.saveConfiguration(false);
							}

							// Show Roof
							if (settingIndex == 7 && this.mouseButtonClick == 1) {
								Config.SHOW_ROOF = !Config.SHOW_ROOF;
								Config.saveConfiguration(false);
							}

							// Show Ground Items
							if (settingIndex == 8 && this.mouseButtonClick == 1 && Config.GROUND_ITEM_TOGGLE) {
								Config.SHOW_GROUND_ITEMS++;
								if (Config.SHOW_GROUND_ITEMS == 4)
									Config.SHOW_GROUND_ITEMS = 0;
								Config.saveConfiguration(false);
							}

							// Auto Message Tab Switch
							if (settingIndex == 9 && this.mouseButtonClick == 1 && Config.AUTO_MESSAGE_SWITCH_TOGGLE) {
								Config.MESSAGE_TAB_SWITCH = !Config.MESSAGE_TAB_SWITCH;
								Config.saveConfiguration(false);
							}

							// Side Menu
							if (settingIndex == 10 && this.mouseButtonClick == 1) {
								Config.SIDE_MENU_OVERLAY = !Config.SIDE_MENU_OVERLAY;
								Config.saveConfiguration(false);
							}

							// Kill Feed
							if (settingIndex == 11 && this.mouseButtonClick == 1 && Config.WANT_KILL_FEED) {
								Config.KILL_FEED = !Config.KILL_FEED;
								Config.saveConfiguration(false);
							}

							// Combat Style
							if (settingIndex == 12 && this.mouseButtonClick == 1) {
								this.combatStyle++;
								if (this.combatStyle == 4) {
									this.combatStyle = 0;
								}
								this.getClientStream().newPacket(29);
								this.getClientStream().writeBuffer1.putByte(this.combatStyle);
								this.getClientStream().finishPacket();
							}

							// Fight Mode
							if (settingIndex == 13 && this.mouseButtonClick == 1) {
								Config.FIGHT_MENU++;
								if (Config.FIGHT_MENU == 3)
									Config.FIGHT_MENU = 0;
								Config.saveConfiguration(false);
							}

							// Experience Counter
							if (settingIndex == 14 && this.mouseButtonClick == 1) {
								Config.EXPERIENCE_COUNTER++;
								if (Config.EXPERIENCE_COUNTER == 3)
									Config.EXPERIENCE_COUNTER = 0;
								Config.saveConfiguration(false);
							}

							// Inventory Count
							if (settingIndex == 15 && this.mouseButtonClick == 1) {
								Config.INV_COUNT = !Config.INV_COUNT;
								Config.saveConfiguration(false);
							}

							var7 += 15 * this.panelSettings.getControlCount(this.controlSettingPanel);

							// Items On Death
							if (this.mouseX > var6 && var5 + var6 > this.mouseX && this.mouseY > var7 - 12
									&& this.mouseY < var7 + 4 && this.mouseButtonClick == 1) {
								this.showUiTab = 0;
								lostOnDeathInterface.setVisible(true);
							}

							var7 += 15;

							if (this.mouseX > var6 && var5 + var6 > this.mouseX && this.mouseY > var7 - 12
									&& this.mouseY < var7 + 4 && this.mouseButtonClick == 1) {
								this.sendLogout(0);
							}
						}

						if (this.settingTab == 0) {
							var7 += 0;
							boolean var11 = false;
							if (this.mouseX > var6 && this.mouseX < var5 + var6 && this.mouseY > var7 - 12
									&& 4 + var7 > this.mouseY && this.mouseButtonClick == 1) {
								this.settingsBlockChat = 1 - this.settingsBlockChat;
								var11 = true;
							}

							var7 += 15;
							if (this.mouseX > var6 && var5 + var6 > this.mouseX && this.mouseY > var7 - 12
									&& var7 + 4 > this.mouseY && this.mouseButtonClick == 1) {
								this.settingsBlockPrivate = 1 - this.settingsBlockPrivate;
								var11 = true;
							}

							var7 += 15;
							if (this.mouseX > var6 && var5 + var6 > this.mouseX && this.mouseY > var7 - 12
									&& var7 + 4 > this.mouseY && this.mouseButtonClick == 1) {
								if(this.settingsBlockGlobal >= 4) {
									this.settingsBlockGlobal = 0;
								}
								this.settingsBlockGlobal = this.settingsBlockGlobal + 1;
								this.getClientStream().newPacket(111);
								this.getClientStream().writeBuffer1.putByte(9);
								this.getClientStream().writeBuffer1.putByte(this.settingsBlockGlobal);
								this.getClientStream().finishPacket();
							}

							var7 += 15;
							if (this.mouseX > var6 && this.mouseX < var6 + var5 && var7 - 12 < this.mouseY
									&& this.mouseY < 4 + var7 && this.mouseButtonClick == 1) {
								this.settingsBlockTrade = 1 - this.settingsBlockTrade;
								var11 = true;
							}

							var7 += 15;
							if (this.mouseX > var6 && this.mouseX < var6 + var5
									&& var7 - 12 < this.mouseY && this.mouseY < var7 + 4 && this.mouseButtonClick == 1) {
								var11 = true;
								this.settingsBlockDuel = 1 - this.settingsBlockDuel;
							}

							var7 += 15;
							if (var11) {
								this.createPacket64(this.settingsBlockChat, this.settingsBlockPrivate,
										this.settingsBlockTrade, this.settingsBlockDuel);
							}

							var7 += 20;
							if (this.mouseX > var6 && var6 + var5 > this.mouseX && var7 - 12 < this.mouseY
									&& 4 + var7 > this.mouseY && this.mouseButtonClick == 1) {
								Config.NAME_CLAN_TAG_OVERLAY = !Config.NAME_CLAN_TAG_OVERLAY;
								Config.saveConfiguration(false);
							}
							var7 += 15;
							if (this.mouseX > var6 && var6 + var5 > this.mouseX && var7 - 12 < this.mouseY
									&& 4 + var7 > this.mouseY && this.mouseButtonClick == 1) {
								this.clanInviteBlockSetting = !this.clanInviteBlockSetting;
								this.getClientStream().newPacket(111);
								this.getClientStream().writeBuffer1.putByte(11);
								this.getClientStream().writeBuffer1.putByte(this.clanInviteBlockSetting ? 1 : 0);
								this.getClientStream().finishPacket();
							}

							if (this.insideTutorial) {
								var7 += 35;
								if (this.mouseX > var6 && var5 + var6 > this.mouseX && var7 - 12 < this.mouseY
										&& this.mouseY < var7 + 4 && this.mouseButtonClick == 1) {
									this.showItemModX(InputXPrompt.promptSkipTutorial, InputXAction.SKIP_TUTORIAL, false);
									this.showUiTab = 0;
								}
								var7 -= 35;
							}

							var7 += 85;
							if (this.mouseX > var6 && var5 + var6 > this.mouseX && this.mouseY > var7 - 12
									&& this.mouseY < var7 + 4 && this.mouseButtonClick == 1) {
								this.sendLogout(0);
							}
						}

						if(this.settingTab == 2) {
							int checkPosition = this.panelSettings.getControlSelectedListIndex(this.controlSettingPanel);
							int settingIndex = checkPosition;

							if (settingIndex == 0 && this.mouseButtonClick == 1) {
								Config.F_LONG_PRESS_CALC = Config.LONG_PRESS_TIMER / 50;
								if (++Config.F_LONG_PRESS_CALC >= 13) {
									Config.F_LONG_PRESS_CALC = 1;
								}
								Config.LONG_PRESS_TIMER = Config.F_LONG_PRESS_CALC * 50;
								Config.saveConfiguration(false);
							}

							if (settingIndex == 1 && this.mouseButtonClick == 1) {
								Config.MENU_SIZE++;
								if (Config.MENU_SIZE == 8)
									Config.MENU_SIZE = 1;
								Config.saveConfiguration(false);
								if(Config.isAndroid()) {
									this.menuCommon.font = Config.MENU_SIZE;
								}
							}

							if (settingIndex == 2 && this.mouseButtonClick == 1) {
								Config.HOLD_AND_CHOOSE = !Config.HOLD_AND_CHOOSE;
								Config.saveConfiguration(false);
							}

							if (settingIndex == 3 && this.mouseButtonClick == 1) {
								Config.SWIPE_TO_SCROLL = !Config.SWIPE_TO_SCROLL;
								Config.saveConfiguration(false);
							}

							if (settingIndex == 4 && this.mouseButtonClick == 1) {
								Config.SWIPE_TO_ROTATE = !Config.SWIPE_TO_ROTATE;
								Config.saveConfiguration(false);
							}

							var7 += 195;
							if (this.mouseX > var6 && var5 + var6 > this.mouseX && this.mouseY > var7 - 12
									&& this.mouseY < var7 + 4 && this.mouseButtonClick == 1) {
								this.sendLogout(0);
							}
						}

						this.mouseButtonClick = 0;
					}
				}
			} catch (RuntimeException var12) {
				throw GenUtil.makeThrowable(var12, "client.BC(" + var1 + ',' + var2 + ')');
			}
		}

		private int flag = 0;
		private Timer tiktok =  new Timer();
		private boolean doubleClick() {
			if (tiktok == null) {
				tiktok =  new Timer();
			}
	        tiktok.schedule(new TimerTask() {
	            public void run() {
	                flag += 1;
	            }
	        },50,400);
	        if (flag > 1) {
	        	if(tiktok != null) {
	                tiktok.cancel();
	                tiktok = null;
	            }
	        	flag = 0;
	        	return false;
	        }
	        else if (flag == 1) {
	        	if(tiktok != null) {
	                tiktok.cancel();
	                tiktok = null;
	            }
	        	flag = 0;
	        	return true;
	        } else {
	    		return false;
	    	}
		}

		private final void drawUiTabPlayerInfo(boolean var1, int var2) {
			try {

				int var3 = this.surface.width2 - 199;
				byte var4 = 36;
				this.getSurface().drawSprite(mudclient.spriteMedia + 3, var3 - 49, 3);
				short var5 = 196;
				short var6 = 275;
				int var8;
				int var7 = var8 = GenUtil.buildColor(160, 160, 160);
				if (this.uiTabPlayerInfoSubTab != 0) {
					var8 = GenUtil.buildColor(220, 220, 220);
				} else {
					var7 = GenUtil.buildColor(220, 220, 220);
				}

				this.surface.drawBoxAlpha(var3, var4, var5 / 2, 24, var7, 128);
				this.surface.drawBoxAlpha(var3 + var5 / 2, var4, var5 / 2, 24, var8, 128);
				this.surface.drawBoxAlpha(var3, 24 + var4, var5, var6 - 12, GenUtil.buildColor(220, 220, 220), 128);
				this.surface.drawLineHoriz(var3, var4 + 24, var5, 0);
				this.surface.drawLineVert(var3 + var5 / 2, 0 + var4, 0, 24);
				this.surface.drawColoredStringCentered(var5 / 4 + var3, "Stats", 0, 0, 4, var4 + 16);
				this.surface.drawColoredStringCentered(var3 + var5 / 4 + var5 / 2, "Quests", 0, 0, 4, var4 + 16);
				int var15;

				if (this.uiTabPlayerInfoSubTab == 0) {
					byte var9 = 72;
					this.getSurface().drawString("Skills", var3 + 5, var9, 16776960, 3);
					int var10 = -1;
					var15 = var9 + 13;
					int var11 = 0, var12 = 0, totalXp = 0;
					for (var11 = 0; var11 < 9; var11++) {
						var12 = 16777215;
						if (this.mouseX > 3 + var3 && var15 - 11 <= this.mouseY && 2 + var15 > this.mouseY
								&& 90 + var3 > this.mouseX) {
							var12 = 16711680;
							var10 = var11;
							if (Config.isAndroid() && this.mouseButtonClick == 1 && this.uiTabPlayerInfoSubTab == 0) {
								if (doubleClick()) {
									setSkillGuideChosen(this.skillNameLong[var10]);
									skillGuideInterface.setVisible(true);
									this.showUiTab = 0;
								}
							}
							else if (!Config.isAndroid() && this.mouseButtonClick == 1 && this.uiTabPlayerInfoSubTab == 0) {
								setSkillGuideChosen(this.skillNameLong[var10]);
								skillGuideInterface.setVisible(true);
								this.showUiTab = 0;
							}
						}

						this.getSurface().drawString(this.getSkillNames()[var11] + ":@yel@" + this.playerStatCurrent[var11]
								+ "/" + this.playerStatBase[var11], var3 + 5, var15, var12, 1);
						var12 = 16777215;
						if (this.mouseX >= var3 + 90 && this.mouseY >= var15 - 11 - 13 && var15 - 13 + 2 > this.mouseY
								&& this.mouseX < var3 + 196) {
							var12 = 16711680;
							var10 = 9 + var11;
							if (Config.isAndroid() && this.mouseButtonClick == 1 && this.uiTabPlayerInfoSubTab == 0) {
								if (doubleClick()) {
									setSkillGuideChosen(this.skillNameLong[var10]);
									skillGuideInterface.setVisible(true);
									this.showUiTab = 0;
								}
							}
							else if (!Config.isAndroid() && this.mouseButtonClick == 1 && this.uiTabPlayerInfoSubTab == 0) {
								setSkillGuideChosen(this.skillNameLong[var10]);
								skillGuideInterface.setVisible(true);
								this.showUiTab = 0;
							}
						}

						this.getSurface().drawString(this.skillNames[9 + var11] + ":@yel@" + this.playerStatCurrent[9 + var11]
								+ "/" + this.playerStatBase[9 + var11], var5 / 2 - 5 + var3, var15 - 13, var12, 1);
						var15 += 13;
					}

					this.getSurface().drawString("Quest Points:@yel@" + this.m_ii, var3 - 5 + var5 / 2, var15 - 13, 16777215, 1);
					var15 += 12;
					this.getSurface().drawString("Fatigue: @yel@" + this.statFatigue * 100 / 750 + "%", 5 + var3, var15 - 13,
							16777215, 1);
					var15 += 8;
					this.getSurface().drawString("Equipment Status", 5 + var3, var15, 16776960, 3);
					var15 += 12;

					for (var11 = 0; var11 < 3; ++var11) {
						this.getSurface().drawString(this.equipmentStatNames[var11] + ":@yel@" + this.playerStatEquipment[var11],
								5 + var3, var15, 16777215, 1);
						if (2 > var11) {
							this.getSurface().drawString(
									this.equipmentStatNames[var11 + 3] + ":@yel@" + this.playerStatEquipment[3 + var11],
									var5 / 2 + var3 + 25, var15, 16777215, 1);
						}

						var15 += 13;
					}

					var15 += 6;
					this.getSurface().drawLineHoriz(var3, var15 - 15, var5, 0);
					if (var10 == -1) {
						this.getSurface().drawString("Overall levels", var3 + 5, var15, 16776960, 1);
						var15 += 12;
						var11 = 0; totalXp = 0;

						for (var12 = 0; var12 < 18; ++var12) {
							totalXp += this.playerExperience[var12];
							var11 += this.playerStatBase[var12];
						}

						this.getSurface().drawString("Total xp: " + totalXp, 5 + var3, var15, 16777215, 1);
						var15 += 12;
						this.getSurface().drawString("Skill total: " + var11, 5 + var3, var15, 16777215, 1);
						var15 += 12;
						this.getSurface().drawString("Combat level: " + this.localPlayer.level, 5 + var3, var15, 16777215, 1);
						var15 += 12;

					} else {
						this.getSurface().drawString(this.skillNameLong[var10] + " skill", 5 + var3, var15, 16776960, 1);
						var15 += 12;
						var11 = this.experienceArray[0];

						for (var12 = 0; var12 < 98; ++var12) {
							if (this.experienceArray[var12] <= this.playerExperience[var10]) {
								var11 = this.experienceArray[var12 + 1];
							}
						}

						this.getSurface().drawString("Total xp: " + this.playerExperience[var10], 5 + var3, var15, 16777215,
								1);
						var15 += 12;
						this.getSurface().drawString("Next level at: " + var11, 5 + var3, var15, 16777215, 1);
						var15 += 12;
						this.getSurface().drawString("Xp to next level: " + (var11 - this.playerExperience[var10]), 5 + var3, var15, 16777215, 1);
					}
				}

				if (this.uiTabPlayerInfoSubTab == 1) {
					this.panelQuestInfo.clearList(this.controlQuestInfoPanel);
					int index = 0;
					this.panelQuestInfo.setListEntry(this.controlQuestInfoPanel, index++,
							"@whi@Quest-list (green=completed)", 0, (String) null, (String) null);
					for (var15 = 0; var15 < 50; ++var15) {
						if (this.questNames[var15] != null) {
							this.panelQuestInfo.setListEntry(this.controlQuestInfoPanel, index++,
									(questStages[var15] == 0 ? "@red@" : questStages[var15] > 0 ? "@yel@" : "@gre@")
									+ this.questNames[var15],
									0, (String) null, (String) null);
						}
					}

					int position = this.panelQuestInfo.getControlSelectedListIndex(this.controlQuestInfoPanel) - 1;
					if (this.mouseButtonClick == 1 && position >= 0
							&& this.getMouseX() > var3 && this.getMouseY() > var4 + 36
							&& this.getMouseX() < var3 + this.getSurface().stringWidth(1, this.questNames[position])
							&& this.getMouseY() < var6 + 44) {
						setQuestGuideChosen(this.questNames[position]);
						setQuestGuideProgress(this.questStages[position]);
						setQuestGuideStartWho(position);
						setQuestGuideStartWhere(position);
						setQuestGuideRequirement(position);
						setQuestGuideReward(position);
						questGuideInterface.setVisible(true);
						this.showUiTab = 0;
						setMouseClick(0);
					}

					this.panelQuestInfo.drawPanel();
				}

				if (var1) {
					int var14 = this.mouseY - 36;
					var3 = -this.getSurface().width2 - (-199 - this.mouseX);
					if (var3 >= 0 && var14 >= 0 && var3 < var5 && var14 < var6) {
						if (this.uiTabPlayerInfoSubTab == 1) {
							this.panelQuestInfo.handleMouse(var3 + this.getSurface().width2 - 199, 36 + var14,
									this.currentMouseButtonDown, this.lastMouseButtonDown);
						}
						if (var14 <= 24 && this.mouseButtonClick == 1) {
							if (var3 >= 98) {
								if (var3 > 98) {
									this.uiTabPlayerInfoSubTab = 1;
								}
							} else {
								this.uiTabPlayerInfoSubTab = 0;
							}
						}
					}

				}
			} catch (RuntimeException var13) {
				throw GenUtil.makeThrowable(var13, "client.QC(" + var1 + ',' + var2 + ')');
			}
		}

		private final void duelRemoveItem(int itemIndex, int removeCount) {
			try {

				int itemID = this.duelOfferItemID[itemIndex];
				int count = removeCount >= 0 ? removeCount : this.mouseButtonItemCountIncrement;
				if (EntityHandler.getItemDef(itemID).isStackable()) {
					this.duelOfferItemSize[itemIndex] -= count;
					if (this.duelOfferItemSize[itemIndex] <= 0) {
						--this.duelOfferItemCount;

						for (int i = itemIndex; i < this.duelOfferItemCount; ++i) {
							this.duelOfferItemID[i] = this.duelOfferItemID[i + 1];
							this.duelOfferItemSize[i] = this.duelOfferItemSize[i + 1];
						}
					}
				} else {
					int removed = 0;

					for (int j = 0; j < this.duelOfferItemCount && removed < count; ++j) {
						if (this.duelOfferItemID[j] == itemID) {
							--this.duelOfferItemCount;
							++removed;

							for (int i = j; this.duelOfferItemCount > i; ++i) {
								this.duelOfferItemID[i] = this.duelOfferItemID[1 + i];
								this.duelOfferItemSize[i] = this.duelOfferItemSize[1 + i];
							}

							--j;
						}
					}
				}

				this.getClientStream().newPacket(33);
				this.getClientStream().writeBuffer1.putByte(this.duelOfferItemCount);

				for (int i = 0; this.duelOfferItemCount > i; ++i) {
					this.getClientStream().writeBuffer1.putShort(this.duelOfferItemID[i]);
					this.getClientStream().writeBuffer1.putInt(this.duelOfferItemSize[i]);
				}

				this.getClientStream().finishPacket();
				this.duelOfferAccepted = false;
				this.duelOffsetOpponentAccepted = false;
			} catch (RuntimeException var9) {
				throw GenUtil.makeThrowable(var9, "client.IC(" + itemIndex + ',' + removeCount + ',' + "dummy" + ')');
			}
		}

		private final void duelStakeItem(int andStakeCount, int andStakeInvIndex) {
			try {

				boolean andStakeSuccess = false;
				int hitStakeStacks = 0;
				int andStakeInvID = this.inventoryItemID[andStakeInvIndex];

				for (int duelIdx = 0; duelIdx < this.duelOfferItemCount; ++duelIdx) {
					if (this.duelOfferItemID[duelIdx] == andStakeInvID) {
						if (EntityHandler.getItemDef(andStakeInvID).isStackable()) {
							if (andStakeCount < 0) {
								for (int invIdx = 0; invIdx < this.mouseButtonItemCountIncrement; ++invIdx) {
									if (this.duelOfferItemSize[duelIdx] < this.inventoryItemSize[andStakeInvIndex]) {
										++this.duelOfferItemSize[duelIdx];
									}

									andStakeSuccess = true;
								}
							} else {
								this.duelOfferItemSize[duelIdx] += andStakeCount;
								if (this.inventoryItemSize[andStakeInvIndex] < this.duelOfferItemSize[duelIdx]) {
									this.duelOfferItemSize[duelIdx] = this.inventoryItemSize[andStakeInvIndex];
								}

								andStakeSuccess = true;
							}
						} else {
							++hitStakeStacks;
						}
					}
				}

				int invCount = this.getInventoryCount(andStakeInvID);
				if (hitStakeStacks >= invCount) {
					andStakeSuccess = true;
				}

				if (EntityHandler.getItemDef(andStakeInvID).quest) {
					andStakeSuccess = true;
					this.showMessage(false, (String) null, "This object cannot be added to a duel offer", MessageType.GAME,
							0, (String) null, (String) null);
				}

				if (!andStakeSuccess) {
					if (andStakeCount < 0) {
						if (this.duelOfferItemCount < 8) {
							this.duelOfferItemID[this.duelOfferItemCount] = andStakeInvID;
							this.duelOfferItemSize[this.duelOfferItemCount] = 1;
							++this.duelOfferItemCount;
							andStakeSuccess = true;
						}
					} else {
						for (int var8 = 0; andStakeCount > var8 && this.duelOfferItemCount < 8
								&& hitStakeStacks < invCount; ++var8) {
							this.duelOfferItemID[this.duelOfferItemCount] = andStakeInvID;
							this.duelOfferItemSize[this.duelOfferItemCount] = 1;
							++hitStakeStacks;
							++this.duelOfferItemCount;
							andStakeSuccess = true;
							if (var8 == 0 && EntityHandler.getItemDef(andStakeInvID).isStackable()) {
								this.duelOfferItemSize[this.duelOfferItemCount
								                       - 1] = this.inventoryItemSize[andStakeInvIndex] < andStakeCount
								                       ? this.inventoryItemSize[andStakeInvIndex] : andStakeCount;
								                       break;
							}
						}
					}
				}

				if (andStakeSuccess) {
					this.getClientStream().newPacket(33);
					this.getClientStream().writeBuffer1.putByte(this.duelOfferItemCount);

					for (int i = 0; this.duelOfferItemCount > i; ++i) {
						this.getClientStream().writeBuffer1.putShort(this.duelOfferItemID[i]);
						this.getClientStream().writeBuffer1.putInt(this.duelOfferItemSize[i]);
					}

					this.getClientStream().finishPacket();
					this.duelOffsetOpponentAccepted = false;
					this.duelOfferAccepted = false;
				}

			} catch (RuntimeException var9) {
				throw GenUtil.makeThrowable(var9,
						"client.C(" + "dummy" + ',' + andStakeCount + ',' + andStakeInvIndex + ')');
			}
		}

		private final void fetchContainerSize() {
			try {

				// Container c;
				// if (this.runningAsApplet) {
				// if (null != ClientBase.containerApplet) {
				// c = ClientBase.containerApplet;
				// } else {
				// c = this;
				// }
				// } else {
				// c = ClientBase.containerFrame;
				//
				// }
				// this.containerWidth = c.getSize().width;
				// this.containerHeight = c.getSize().height;
				//
				// this.screenOffsetY = 0;
				// this.screenOffsetX = (this.containerWidth - this.getGameWidth())
				// / 2;
				// this.drawPadding((byte) 49);
			} catch (RuntimeException var3) {
				throw GenUtil.makeThrowable(var3, "client.PD(" + "dummy" + ')');
			}
		}

		boolean reposition() {

			if (resizeWidth <= 0 && resizeHeight <= 0) {
				return false;
			}
			gameWidth = resizeWidth;
			gameHeight = resizeHeight - 12;

			resizeWidth = resizeHeight = -1;

			getSurface().resize(this.gameWidth, this.gameHeight + 12);
			this.getSurface().setClip(0, this.getGameWidth(), this.getGameHeight() + 12, 0);
			this.scene.setMidpoints(this.getGameHeight() / 2, true, this.getGameWidth(), this.getGameWidth() / 2,
					this.getGameHeight() / 2, this.m_qd, this.getGameWidth() / 2);

			clientPort.resized();
			int var3 = this.getSurface().width2 - 199;
			byte var12 = 36;
			panelMagic.reposition(controlMagicPanel, var3, 24 + var12, 196, 90);
			panelSocial.reposition(controlSocialPanel, var3, var12 + 40, 196, 126);
			panelClan.reposition(controlClanPanel, var3, var12 + 72, 196, 128);
			panelPlayerInfo.reposition(controlPlayerInfoPanel, var3, 24 + var12, 196, 263); // 251
			panelQuestInfo.reposition(controlQuestInfoPanel, var3, 24 + var12, 196, 263); // 251 -remove??
			//panelPlayerTaskInfo.reposition(controlPlayerTaskInfoPanel, var3, 24 + var12 + 27, 196, 224);
			panelSettings.reposition(controlSettingPanel, var3 + 1, 24 + var12 + 16, 195, 184);

			panelMessageTabs.reposition(panelMessageChat, 5, getGameHeight() - 65, getGameWidth() - 10, 56);
			panelMessageTabs.reposition(panelMessageEntry, 7, getGameHeight() - 10, getGameWidth() - 14, 14);
			panelMessageTabs.reposition(panelMessageQuest, 5, getGameHeight() - 65, getGameWidth() - 10, 56);
			panelMessageTabs.reposition(panelMessagePrivate, 5, getGameHeight() - 65, getGameWidth() - 10, 56);
			panelMessageTabs.reposition(panelMessageClan, 5, getGameHeight() - 65, getGameWidth() - 10, 56);
			return true;
		}

		public final int getInventoryCount(int id) {
			try {

				int count = 0;

				for (int index = 0; this.inventoryItemCount > index; ++index) {
					if (this.inventoryItemID[index] == id) {
						if (EntityHandler.getItemDef(inventoryItemID[index]).isStackable()) {
							count += this.inventoryItemSize[index];
						} else {
							++count;
						}
					}
				}

				return count;
			} catch (RuntimeException var5) {
				throw GenUtil.makeThrowable(var5, "client.HB(" + "dummy" + ',' + id + ')');
			}
		}

		private final RSCharacter getServerNPC(int serverIndex) {
			try {


				for (int var3 = 0; var3 < this.npcCount; ++var3) {
					if (serverIndex == this.npcs[var3].serverIndex) {
						return this.npcs[var3];
					}
				}

				return null;
			} catch (RuntimeException var4) {
				throw GenUtil.makeThrowable(var4, "client.AC(" + serverIndex + ',' + -123 + ')');
			}
		}

		private final RSCharacter getServerPlayer(int serverIndex) {
			try {


				for (int i = 0; this.playerCount > i; ++i) {
					if (serverIndex == this.players[i].serverIndex) {
						return this.players[i];
					}
				}

				return null;
			} catch (RuntimeException var4) {
				throw GenUtil.makeThrowable(var4, "client.K(" + serverIndex + ',' + 220 + ')');
			}
		}

		private final void handleAppearancePanelControls(int var1) {
			try {
				this.panelAppearance.handleMouse(this.mouseX, this.mouseY, this.currentMouseButtonDown,
						this.lastMouseButtonDown);

				if (this.panelAppearance.isClicked(this.controlButtonAppearanceHeadMinus)) {
					do {
						do {
							this.appearanceHeadType = (EntityHandler.animationCount() + (this.appearanceHeadType - 1))
									% EntityHandler.animationCount();
						} while ((3 & EntityHandler.getAnimationDef(this.appearanceHeadType).getGenderModel()) != 1);
					} while ((EntityHandler.getAnimationDef(this.appearanceHeadType).getGenderModel()
							& this.appearanceHeadGender * 4) == 0);
				}

				if (this.panelAppearance.isClicked(this.controlButtonAppearanceHeadPlus)) {
					do {
						do {
							this.appearanceHeadType = (1 + this.appearanceHeadType) % EntityHandler.animationCount();
						} while (1 != (3 & EntityHandler.getAnimationDef(this.appearanceHeadType).getGenderModel()));
					} while ((EntityHandler.getAnimationDef(this.appearanceHeadType).getGenderModel()
							& this.appearanceHeadGender * 4) == 0);
				}

				if (this.panelAppearance.isClicked(this.m_Kj)) {
					this.m_ld = (this.getPlayerHairColors().length + (this.m_ld - 1)) % this.getPlayerHairColors().length;
				}

				if (this.panelAppearance.isClicked(this.m_ed)) {
					this.m_ld = (1 + this.m_ld) % this.getPlayerHairColors().length;
				}

				if (this.panelAppearance.isClicked(this.m_Ge) || this.panelAppearance.isClicked(this.m_Of)) {
					for (this.appearanceHeadGender = 3 - this.appearanceHeadGender; (3
							& EntityHandler.getAnimationDef(this.appearanceHeadType).getGenderModel()) != 1
							|| (EntityHandler.getAnimationDef(this.appearanceHeadType).getGenderModel()
									& this.appearanceHeadGender * 4) == 0; this.appearanceHeadType = (1
											+ this.appearanceHeadType) % EntityHandler.animationCount()) {
						;
					}

					while ((3 & EntityHandler.getAnimationDef(this.m_dk).getGenderModel()) != 2
							|| (this.appearanceHeadGender * 4
									& EntityHandler.getAnimationDef(this.m_dk).getGenderModel()) == 0) {
						this.m_dk = (this.m_dk + 1) % EntityHandler.animationCount();
					}
				}

				// if (var1 < 68) {
				// this.getHostnameIp(113, -28);
				// }

				if (this.panelAppearance.isClicked(this.m_Xc)) {
					this.m_Wg = (this.m_Wg - 1 + this.getPlayerClothingColors().length)
							% this.getPlayerClothingColors().length;
				}

				if (this.panelAppearance.isClicked(this.m_ek)) {
					this.m_Wg = (this.m_Wg + 1) % this.getPlayerClothingColors().length;
				}

				if (this.panelAppearance.isClicked(this.m_Ze)) {
					this.m_hh = (this.getPlayerSkinColors().length + (this.m_hh - 1)) % this.getPlayerSkinColors().length;
				}

				if (this.panelAppearance.isClicked(this.m_Mj)) {
					this.m_hh = (1 + this.m_hh) % this.getPlayerSkinColors().length;
				}

				if (this.panelAppearance.isClicked(this.m_Re)) {
					this.characterBottomColour = (this.getPlayerClothingColors().length + this.characterBottomColour - 1)
							% this.getPlayerClothingColors().length;
				}

				if (this.panelAppearance.isClicked(this.m_Ai)) {
					this.characterBottomColour = (1 + this.characterBottomColour) % this.getPlayerClothingColors().length;
				}

				if (this.panelAppearance.isClicked(this.m_Eg)) {
					this.getClientStream().newPacket(235);
					this.getClientStream().writeBuffer1.putByte(this.appearanceHeadGender);
					this.getClientStream().writeBuffer1.putByte(this.appearanceHeadType);
					this.getClientStream().writeBuffer1.putByte(this.m_dk);
					this.getClientStream().writeBuffer1.putByte(this.character2Colour);
					this.getClientStream().writeBuffer1.putByte(this.m_ld);
					this.getClientStream().writeBuffer1.putByte(this.m_Wg);
					this.getClientStream().writeBuffer1.putByte(this.characterBottomColour);
					this.getClientStream().writeBuffer1.putByte(this.m_hh);
					this.getClientStream().finishPacket();
					this.getSurface().blackScreen(true);
					this.showAppearanceChange = false;
				}

			} catch (RuntimeException var3) {
				throw GenUtil.makeThrowable(var3, "client.M(" + var1 + ')');
			}
		}

		private final void handleGameInput() {
			try {

				if (this.systemUpdate > 1) {
					--this.systemUpdate;
				}
				if (this.elixirTimer > 1) {
					--this.elixirTimer;
					if(this.elixirTimer <= 1) {
						this.elixirTimer = 0;
					}
				}
				this.checkConnection();
				if (this.logoutTimeout > 0) {
					--this.logoutTimeout;
				}

				if (this.localPlayer.direction == RSCharacterDirection.COMBAT_A
						|| this.localPlayer.direction == RSCharacterDirection.COMBAT_B) {
					this.combatTimeout = 500;
				}

				if (this.combatTimeout > 0) {
					--this.combatTimeout;
				}

				if (this.showAppearanceChange) {
					this.handleAppearancePanelControls(86);
				} else {
					int var2;
					RSCharacter var3;
					int var4;
					int var6;
					int var7;
					int var8;
					for (var2 = 0; var2 < this.playerCount; ++var2) {
						var3 = this.players[var2];
						var4 = (1 + var3.waypointCurrent) % 10;
						if (var3.movingStep == var4) {
							var3.direction = RSCharacterDirection.lookup(var3.animationNext);
						} else {
							RSCharacterDirection var5 = null;
							var6 = var3.movingStep;
							if (var6 < var4) {
								var7 = var4 - var6;
							} else {
								var7 = 10 + var4 - var6;
							}

							var8 = 4;
							if (var7 > 2) {
								var8 = var7 * 4 - 4;
							}

							if (var3.waypointsX[var6] - var3.currentX <= this.tileSize * 3
									&& var3.waypointsZ[var6] - var3.currentZ <= this.tileSize * 3
									&& var3.waypointsX[var6] - var3.currentX >= -this.tileSize * 3
									&& var3.waypointsZ[var6] - var3.currentZ >= -this.tileSize * 3 && var7 <= 8) {
								if (var3.waypointsX[var6] > var3.currentX) {
									var5 = RSCharacterDirection.WEST;
									var3.currentX += var8;
									++var3.stepFrame;
								} else if (var3.waypointsX[var6] < var3.currentX) {
									++var3.stepFrame;
									var5 = RSCharacterDirection.EAST;
									var3.currentX -= var8;
								}

								if (var3.currentX - var3.waypointsX[var6] < var8
										&& var3.currentX - var3.waypointsX[var6] > -var8) {
									var3.currentX = var3.waypointsX[var6];
								}

								if (var3.waypointsZ[var6] > var3.currentZ) {
									if (var5 != null) {
										if (var5 == RSCharacterDirection.WEST) {
											var5 = RSCharacterDirection.SOUTH_WEST;
										} else {
											var5 = RSCharacterDirection.SOUTH_EAST;
										}
									} else {
										var5 = RSCharacterDirection.SOUTH;
									}

									var3.currentZ += var8;
									++var3.stepFrame;
								} else if (var3.waypointsZ[var6] < var3.currentZ) {
									++var3.stepFrame;
									var3.currentZ -= var8;
									if (var5 != null) {
										if (var5 == RSCharacterDirection.WEST) {
											var5 = RSCharacterDirection.NORTH_WEST;
										} else {
											var5 = RSCharacterDirection.NORTH_EAST;
										}
									} else {
										var5 = RSCharacterDirection.NORTH;
									}
								}

								if (var3.currentZ - var3.waypointsZ[var6] < var8
										&& var3.currentZ - var3.waypointsZ[var6] > -var8) {
									var3.currentZ = var3.waypointsZ[var6];
								}
							} else {
								var3.currentX = var3.waypointsX[var6];
								var3.currentZ = var3.waypointsZ[var6];
							}

							if (var5 != null) {
								var3.direction = var5;
							}

							if (var3.waypointsX[var6] == var3.currentX && var3.waypointsZ[var6] == var3.currentZ) {
								var3.movingStep = (1 + var6) % 10;
							}
						}

						if (var3.bubbleTimeout > 0) {
							--var3.bubbleTimeout;
						}

						if (var3.combatTimeout > 0) {
							--var3.combatTimeout;
						}

						if (var3.messageTimeout > 0) {
							--var3.messageTimeout;
						}

						if (this.deathScreenTimeout > 0) {
							--this.deathScreenTimeout;
							if (this.deathScreenTimeout == 0) {
								this.showMessage(false, (String) null,
										"You have been granted another life. Be more careful this time!", MessageType.GAME,
										0, (String) null, (String) null);
							}

							if (this.deathScreenTimeout == 0) {
								this.showMessage(false, (String) null,
										"You retain your skills. Your objects land where you died", MessageType.GAME, 0,
										(String) null, (String) null);
							}
						}
					}

					for (var2 = 0; var2 < this.npcCount; ++var2) {
						var3 = this.npcs[var2];
						var4 = (var3.waypointCurrent + 1) % 10;
						if (var4 == var3.movingStep) {
							if (var3.npcId == 43) {
								++var3.stepFrame;
							}

							var3.direction = RSCharacterDirection.lookup(var3.animationNext);
						} else {
							RSCharacterDirection var5 = null;
							var6 = var3.movingStep;
							if (var4 <= var6) {
								var7 = var4 + (10 - var6);
							} else {
								var7 = var4 - var6;
							}

							var8 = 4;
							if (var7 > 2) {
								var8 = (var7 - 1) * 4;
							}

							if (this.tileSize * 3 >= var3.waypointsX[var6] - var3.currentX
									&& var3.waypointsZ[var6] - var3.currentZ <= this.tileSize * 3
									&& var3.waypointsX[var6] - var3.currentX >= -this.tileSize * 3
									&& var3.waypointsZ[var6] - var3.currentZ >= -this.tileSize * 3 && var7 <= 8) {
								if (var3.waypointsX[var6] > var3.currentX) {
									++var3.stepFrame;
									var3.currentX += var8;
									var5 = RSCharacterDirection.WEST;
								} else if (var3.currentX > var3.waypointsX[var6]) {
									var5 = RSCharacterDirection.EAST;
									++var3.stepFrame;
									var3.currentX -= var8;
								}

								if (var8 > var3.currentX - var3.waypointsX[var6]
										&& -var8 < var3.currentX - var3.waypointsX[var6]) {
									var3.currentX = var3.waypointsX[var6];
								}

								if (var3.waypointsZ[var6] <= var3.currentZ) {
									if (var3.currentZ > var3.waypointsZ[var6]) {
										if (var5 == null) {
											var5 = RSCharacterDirection.NORTH;
										} else if (var5 != RSCharacterDirection.WEST) {
											var5 = RSCharacterDirection.NORTH_EAST;
										} else {
											var5 = RSCharacterDirection.NORTH_WEST;
										}

										var3.currentZ -= var8;
										++var3.stepFrame;
									}
								} else {
									++var3.stepFrame;
									if (var5 == null) {
										var5 = RSCharacterDirection.SOUTH;
									} else if (var5 != RSCharacterDirection.WEST) {
										var5 = RSCharacterDirection.SOUTH_EAST;
									} else {
										var5 = RSCharacterDirection.SOUTH_WEST;
									}

									var3.currentZ += var8;
								}

								if (var3.currentZ - var3.waypointsZ[var6] < var8
										&& -var8 < var3.currentZ - var3.waypointsZ[var6]) {
									var3.currentZ = var3.waypointsZ[var6];
								}
							} else {
								var3.currentX = var3.waypointsX[var6];
								var3.currentZ = var3.waypointsZ[var6];
							}

							if (var5 != null) {
								var3.direction = var5;
							}

							if (var3.currentX == var3.waypointsX[var6] && var3.waypointsZ[var6] == var3.currentZ) {
								var3.movingStep = (1 + var6) % 10;
							}
						}

						if (var3.combatTimeout > 0) {
							--var3.combatTimeout;
						}

						if (var3.bubbleTimeout > 0) {
							--var3.bubbleTimeout;
						}

						if (var3.messageTimeout > 0) {
							--var3.messageTimeout;
						}
					}

					if (this.showUiTab != 2) {
						if (MiscFunctions.cachingFile_s_g > 0) {
							++this.sleepWordDelayTimer;
						}

						if (MiscFunctions.netsock_s_M > 0) {
							this.sleepWordDelayTimer = 0;
						}

						MiscFunctions.cachingFile_s_g = 0;
						MiscFunctions.netsock_s_M = 0;
					}

					for (var2 = 0; var2 < this.playerCount; ++var2) {
						var3 = this.players[var2];
						if (var3.projectileRange > 0) {
							--var3.projectileRange;
						}
					}

					if (this.sleepWordDelayTimer > 20) {
						this.sleepWordDelayTimer = 0;
						this.sleepWordDelay = false;
					}

					int var10;
					if (!this.cameraAutoAngleDebug) {
						if (this.cameraAutoRotatePlayerX - this.localPlayer.currentX < -500
								|| this.cameraAutoRotatePlayerX - this.localPlayer.currentX > 500
								|| this.cameraAutoRotatePlayerZ - this.localPlayer.currentZ < -500
								|| this.cameraAutoRotatePlayerZ - this.localPlayer.currentZ > 500) {
							this.cameraAutoRotatePlayerX = this.localPlayer.currentX;
							this.cameraAutoRotatePlayerZ = this.localPlayer.currentZ;
						}

						if (this.optionCameraModeAuto) {
							var2 = this.cameraAngle * 32;
							var10 = var2 - this.cameraRotation;
							byte var12 = 1;
							if (var10 != 0) {
								++this.m_Wc;
								if (var10 <= 128) {
									if (var10 <= 0) {
										if (var10 >= -128) {
											if (var10 < 0) {
												var12 = -1;
												var10 = -var10;
											}
										} else {
											var10 += 256;
											var12 = 1;
										}
									} else {
										var12 = 1;
									}
								} else {
									var12 = -1;
									var10 = 256 - var10;
								}

								this.cameraRotation += (var10 * this.m_Wc + 255) / 256 * var12;
								this.cameraRotation &= 255;
							} else {
								this.m_Wc = 0;
							}
						}

						if (this.localPlayer.currentZ != this.cameraAutoRotatePlayerZ) {
							this.cameraAutoRotatePlayerZ += (this.localPlayer.currentZ - this.cameraAutoRotatePlayerZ)
									/ ((this.cameraZoom - 500) / 15 + 16);
						}

						if (this.cameraAutoRotatePlayerX != this.localPlayer.currentX) {
							this.cameraAutoRotatePlayerX += (this.localPlayer.currentX - this.cameraAutoRotatePlayerX)
									/ ((this.cameraZoom - 500) / 15 + 16);
						}
					} else if (this.cameraAutoRotatePlayerX - this.localPlayer.currentX < -500
							|| this.cameraAutoRotatePlayerX - this.localPlayer.currentX > 500
							|| this.cameraAutoRotatePlayerZ - this.localPlayer.currentZ < -500
							|| this.cameraAutoRotatePlayerZ - this.localPlayer.currentZ > 500) {
						this.cameraAutoRotatePlayerX = this.localPlayer.currentX;
						this.cameraAutoRotatePlayerZ = this.localPlayer.currentZ;
					}

					if (!this.isSleeping) {
						if (mouseY > (getGameHeight() - 4)) { // Chat Tab Selection
							if (mouseX > 15 + ((getGameWidth() / 2) - 256) && mouseX < 96 + ((getGameWidth() / 2) - 256)
									&& lastMouseButtonDown == 1)
								this.messageTabSelected = MessageTab.ALL;
							if (mouseX > 110 + ((getGameWidth() / 2) - 256) && mouseX < 194 + ((getGameWidth() / 2) - 256)
									&& lastMouseButtonDown == 1) {
								this.messageTabSelected = MessageTab.CHAT;
								this.panelMessageTabs.controlScrollAmount[this.panelMessageChat] = 999999;
							}
							if (mouseX > 215 + ((getGameWidth() / 2) - 256) && mouseX < 295 + ((getGameWidth() / 2) - 256)
									&& lastMouseButtonDown == 1) {
								this.messageTabSelected = MessageTab.QUEST;
								this.panelMessageTabs.controlScrollAmount[this.panelMessageQuest] = 999999;
							}
							if (mouseX > 315 + ((getGameWidth() / 2) - 256) && mouseX < 395 + ((getGameWidth() / 2) - 256)
									&& lastMouseButtonDown == 1) {
								this.messageTabSelected = MessageTab.PRIVATE;
								this.panelMessageTabs.controlScrollAmount[this.panelMessagePrivate] = 999999;
							}
							if (Config.WANT_CLANS) {
								if (mouseX > 417 + ((getGameWidth() / 2) - 256) && mouseX < 497 + ((getGameWidth() / 2) - 256)
										&& lastMouseButtonDown == 1) {
									this.messageTabSelected = MessageTab.CLAN;
									this.panelMessageTabs.controlScrollAmount[this.panelMessageClan] = 999999;
								}
							}

							this.currentMouseButtonDown = 0;
							this.lastMouseButtonDown = 0;
						}

						// if (this.mouseY > this.getGameHeight() - 4) {
						// if (this.mouseX > 15 && this.mouseX < 96 &&
						// this.lastMouseButtonDown == 1) {
						// this.messageTabSelected = MessageTab.ALL;
						// }
						//
						// if (this.mouseX > 110 && this.mouseX < 194 &&
						// this.lastMouseButtonDown == 1) {
						// this.messageTabSelected = MessageTab.CHAT;
						// this.panelMessageTabs.controlScrollAmount[this.panelMessageChat]
						// = 999999;
						// }
						//
						// if (this.mouseX > 215 && this.mouseX < 295 &&
						// this.lastMouseButtonDown == 1) {
						// this.messageTabSelected = MessageTab.QUEST;
						// this.panelMessageTabs.controlScrollAmount[this.panelMessageQuest]
						// = 999999;
						// }
						//
						// if (this.mouseX > 315 && this.mouseX < 395 &&
						// this.lastMouseButtonDown == 1) {
						// this.messageTabSelected = MessageTab.PRIVATE;
						// this.panelMessageTabs.controlScrollAmount[this.panelMessagePrivate]
						// = 999999;
						// }
						//
						// if (this.mouseX > 417 && this.mouseX < 497 &&
						// this.lastMouseButtonDown == 1) {
						// this.inputTextFinal = "";
						// this.reportAbuse_State = 1;
						// this.inputTextCurrent = "";
						// }
						//
						// this.currentMouseButtonDown = 0;
						// this.lastMouseButtonDown = 0;
						// }

						this.panelMessageTabs.handleMouse(this.mouseX, this.mouseY, this.currentMouseButtonDown,
								this.lastMouseButtonDown);
						auctionHouse.myAuctions.handleMouse(this.mouseX, this.mouseY, this.currentMouseButtonDown,
								this.lastMouseButtonDown);
						auctionHouse.auctionMenu.handleMouse(this.mouseX, this.mouseY, this.currentMouseButtonDown,
								this.lastMouseButtonDown);
						clan.getClanInterface().clanSetupPanel.handleMouse(this.mouseX, this.mouseY, this.currentMouseButtonDown,
								this.lastMouseButtonDown);
						bank.bank.handleMouse(this.mouseX, this.mouseY, this.currentMouseButtonDown,
								this.lastMouseButtonDown);
						if (this.messageTabSelected != MessageTab.ALL && this.mouseX >= 494
								&& this.mouseY >= this.getGameHeight() - 66) {
							this.lastMouseButtonDown = 0;
						}

						if (this.panelMessageTabs.isClicked(this.panelMessageEntry)) {
							String var11 = this.panelMessageTabs.getControlText(this.panelMessageEntry);
							this.panelMessageTabs.setText(this.panelMessageEntry, "");
							if (var11.startsWith("::")) {
								if (var11.equalsIgnoreCase("::dev") && adminRights) {
									developerMenu = true;
								} else if (var11.equalsIgnoreCase("::mod") && adminRights) {
									modMenu = true; 
								} else if (var11.startsWith("::n ") && adminRights) {
									devMenuNpcID = Integer.parseInt(var11.split(" ")[1]);
								} else if (var11.equalsIgnoreCase("::overlay")) {
									Config.SIDE_MENU_OVERLAY = Config.SIDE_MENU_OVERLAY;
								}
								else {
									this.sendCommandString(var11.substring(2));
									String putQueue = var11.substring(2);
									if (messages.size() == 0
											|| !messages.get(messages.size() - 1).equalsIgnoreCase("::" + putQueue)) {
										messages.add("::" + putQueue);
										currentChat = messages.size();
									} else if (messages.get(messages.size() - 1).equalsIgnoreCase("::" + putQueue)) {
										currentChat = messages.size();
									}
								}
							} else {
								this.sendChatMessage(var11);
								if (messages.size() == 0 || !messages.get(messages.size() - 1).equalsIgnoreCase(var11)) {
									messages.add(var11);
									currentChat = messages.size();
								} else if (messages.get(messages.size() - 1).equalsIgnoreCase(var11)) {
									currentChat = messages.size();
								}
							}
						}

						for (var2 = 0; var2 < messagesArray.length; ++var2) {
							if (MessageHistory.messageHistoryTimeout[var2] > 0) {
								--MessageHistory.messageHistoryTimeout[var2];
							}
						}

						if (this.deathScreenTimeout != 0) {
							this.lastMouseButtonDown = 0;
						}

						if (!this.showDialogTrade && !this.showDialogDuel && !isShowDialogBank() ) {
							this.mouseButtonDownTime = 0;
							this.mouseButtonItemCountIncrement = 0;
						} else {
							if (-1 == ~this.currentMouseButtonDown) {
								this.mouseButtonDownTime = 0;
							} else {
								++this.mouseButtonDownTime;
							}
							if (!Config.isAndroid()) {
								if (this.mouseButtonDownTime <= 600) {
									if (this.mouseButtonDownTime > 450) {
										this.mouseButtonItemCountIncrement += 500;
									} else if (this.mouseButtonDownTime > 300) {
										this.mouseButtonItemCountIncrement += 50;
									} else if (this.mouseButtonDownTime <= 150) {
										if (this.mouseButtonDownTime <= 50) {
											if (this.mouseButtonDownTime > 20 && (this.mouseButtonDownTime & 5) == 0) {
												++this.mouseButtonItemCountIncrement;
											}
										} else {
											++this.mouseButtonItemCountIncrement;
										}
									} else {
										this.mouseButtonItemCountIncrement += 5;
									}
								} else {
									this.mouseButtonItemCountIncrement += 5000;
								}
							}
						}

						if (this.lastMouseButtonDown == 1) {
							this.mouseButtonClick = 1;
						} else if (this.lastMouseButtonDown == 2) {
							this.mouseButtonClick = 2;
						}
						if (mainComponent.checkMouseInput(getMouseX(), getMouseY(), getMouseButtonDown(),
								getMouseClick())) {
							this.currentMouseButtonDown = 0;
							this.mouseButtonClick = 0;
							this.lastMouseButtonDown = 0;
						}
						this.scene.setMouseLoc(0, this.mouseX, this.mouseY);
						this.lastMouseButtonDown = 0;
						if (this.optionCameraModeAuto) {
							if (this.m_Wc == 0 || this.cameraAutoAngleDebug) {
								if (this.keyLeft) {
									this.keyLeft = false;
									this.cameraAngle = this.cameraAngle + 1 & 7;
									if (!this.fogOfWar) {
										if ((1 & this.cameraAngle) == 0) {
											this.cameraAngle = 7 & 1 + this.cameraAngle;
										}

										for (var2 = 0; var2 < 8 && !this.cameraColliding(this.cameraAngle); ++var2) {
											this.cameraAngle = 1 + this.cameraAngle & 7;
										}
									}
								}

								if (this.keyRight) {
									this.keyRight = false;
									this.cameraAngle = 7 + this.cameraAngle & 7;
									if (!this.fogOfWar) {
										if ((1 & this.cameraAngle) == 0) {
											this.cameraAngle = this.cameraAngle + 7 & 7;
										}

										for (var2 = 0; var2 < 8 && !this.cameraColliding(this.cameraAngle); ++var2) {
											this.cameraAngle = this.cameraAngle + 7 & 7;
										}
									}
								}
							}
						} else if (this.keyLeft) {
							this.cameraRotation = 255 & this.cameraRotation + 2;
						} else if (this.keyRight) {
							this.cameraRotation = 255 & this.cameraRotation - 2;
						} else if (this.keyDown && this.cameraAllowPitchModification) {
							this.cameraPitch = (this.cameraPitch + 4) & 1023;
						} else if (this.keyUp && this.cameraAllowPitchModification) {
							this.cameraPitch = (this.cameraPitch + 1024 - 4) & 1023;
						} else if (this.pageDown) {
							currentChat++;
							if (currentChat >= messages.size()) {
								currentChat = messages.size() - 1;
								this.pageDown = false;
								return;
							}
							panelMessageTabs.setText(panelMessageEntry, messages.get(currentChat));
							this.pageDown = false;
						} else if (this.pageUp) {
							currentChat--;
							if (currentChat < 0) {
								currentChat = 0;
								this.pageUp = false;
								return;
							}
							panelMessageTabs.setText(panelMessageEntry, messages.get(currentChat));
							this.pageUp = false;
						}

						if (this.mouseClickXStep > 0) {
							--this.mouseClickXStep;
						} else if (this.mouseClickXStep < 0) {
							++this.mouseClickXStep;
						}

						if (this.fogOfWar && this.cameraZoom > 550) {
							this.cameraZoom -= 4;
						} else if (!this.fogOfWar && this.cameraZoom < 750) {
							this.cameraZoom += 4;
						}

						this.scene.d(25013, 17);
						++this.objectAnimationCount;
						if (this.objectAnimationCount > 5) {
							this.objectAnimationCount = 0;
							this.objectAnimationNumberTorch = (1 + this.objectAnimationNumberTorch) % 4;
							this.objectAnimationNumberFireLightningSpell = (1
									+ this.objectAnimationNumberFireLightningSpell) % 3;
							this.objectAnimationNumberClaw = (1 + this.objectAnimationNumberClaw) % 5;
						}

						for (var2 = 0; var2 < this.gameObjectInstanceCount; ++var2) {
							var10 = this.gameObjectInstanceX[var2];
							var4 = this.gameObjectInstanceZ[var2];
							if (var10 >= 0 && var4 >= 0 && var10 < 96 && var4 < 96
									&& this.gameObjectInstanceID[var2] == 74) {
								this.gameObjectInstanceModel[var2].addRotation(1, 0, 0);
							}
						}

						for (var2 = 0; var2 < this.teleportBubbleCount; ++var2) {
							++this.teleportBubbleTime[var2];
							if (this.teleportBubbleTime[var2] > 50) {
								--this.teleportBubbleCount;

								for (var10 = var2; var10 < this.teleportBubbleCount; ++var10) {
									this.teleportBubbleX[var10] = this.teleportBubbleX[var10 + 1];
									this.teleportBubbleZ[var10] = this.teleportBubbleZ[1 + var10];
									this.teleportBubbleTime[var10] = this.teleportBubbleTime[1 + var10];
									this.teleportBubbleType[var10] = this.teleportBubbleType[1 + var10];
								}
							}
						}

					} else {
						if (this.inputTextFinal.length() > 0) {
							this.getClientStream().newPacket(45);
							if (this.sleepWordDelay) {
								this.getClientStream().writeBuffer1.putByte(1);
							} else {
								this.getClientStream().writeBuffer1.putByte(0);
								this.sleepWordDelay = true;
							}

							this.getClientStream().writeBuffer1.putNullThenString(this.inputTextFinal, 116);
							this.getClientStream().finishPacket();
							this.inputTextCurrent = "";
							this.sleepingStatusText = "Please wait...";
							this.inputTextFinal = "";
						}

						if (this.lastMouseButtonDown == 1 && this.mouseY > 275 - (Config.isAndroid() ? 110 : 0) && this.mouseY < 310 - (Config.isAndroid() ? 110 : 0) && this.mouseX > 56
								&& this.mouseX < 456) {
							this.getClientStream().newPacket(45);
							if (!this.sleepWordDelay) {
								this.getClientStream().writeBuffer1.putByte(0);
								this.sleepWordDelay = true;
							} else {
								this.getClientStream().writeBuffer1.putByte(1);
							}

							this.getClientStream().writeBuffer1.putNullThenString("-null-", -74);
							this.getClientStream().finishPacket();
							this.sleepingStatusText = "Please wait...";
							this.inputTextFinal = "";
							this.inputTextCurrent = "";
						}

						this.lastMouseButtonDown = 0;
					}
				}

			} catch (RuntimeException var9) {
				throw GenUtil.makeThrowable(var9, "client.RD(" + "dummy" + ')');
			}
		}

		private boolean optionsMenuKeyboardInput = true;

		public final void handleKeyPress(byte var1, int key) {
			try {

				if (this.currentViewMode == GameMode.LOGIN) {
					if (this.loginScreenNumber == 0 && this.panelLoginWelcome != null) {
						this.panelLoginWelcome.keyPress(key);
					}
					if (this.loginScreenNumber == 1 && this.menuNewUser != null) {
						this.menuNewUser.keyPress(key);
					}
					if (this.loginScreenNumber == 2 && null != this.panelLogin) {
						this.panelLogin.keyPress(key);
					}
				}

				if (var1 > 105) {
					if (this.currentViewMode == GameMode.GAME) {
						if (this.showAppearanceChange) {
							this.panelAppearance.keyPress(key);
							return;
						}
						if (auctionHouse.isVisible() && (auctionHouse.auctionMenu.focusOn(auctionHouse.auctionSearchHandle)
								|| auctionHouse.myAuctions.focusOn(auctionHouse.textField_priceEach)
								|| auctionHouse.auctionMenu.focusOn(auctionHouse.textField_buyAmount)
								|| auctionHouse.myAuctions.focusOn(auctionHouse.textField_amount)
								|| auctionHouse.myAuctions.focusOn(auctionHouse.textField_price))) {
							auctionHouse.keyDown(key);
							return;
						}
						if (bank.bank.focusOn(bank.bankSearch)) {
							bank.keyDown(key);
							return;
						}
						if (clan.getClanInterface().isVisible() && (clan.getClanInterface().clanSetupPanel.focusOn(clan.getClanInterface().clanName_field)
								|| clan.getClanInterface().clanSetupPanel.focusOn(clan.getClanInterface().clanTag_field)
								|| clan.getClanInterface().clanSetupPanel.focusOn(clan.getClanInterface().clanSearch_field))) {
							clan.getClanInterface().keyDown(key);
							return;
						}
						if (mainComponent.checkKeyPress(key)) {
							return;
						}

						if (optionsMenuShow && optionsMenuKeyboardInput) {
							try {
								int option = Integer.parseInt("" + (char) key) - 1;
								if (option >= 0 && option < optionsMenuCount) {
									this.getClientStream().newPacket(116);
									this.getClientStream().writeBuffer1.putByte(option);
									this.getClientStream().finishPacket();
									mouseButtonClick = 0;
									optionsMenuShow = false;
									return;
								}
							} catch (Exception e) {

							}
						}

						if (this.panelSocialPopup_Mode == SocialPopupMode.NONE && this.reportAbuse_State == 0
								&& !this.isSleeping && this.inputX_Action == InputXAction.ACT_0) {
							this.panelMessageTabs.keyPress(key);
						}
					}

				}
			} catch (RuntimeException var4) {
				throw GenUtil.makeThrowable(var4, "client.AD(" + var1 + ',' + key + ')');
			}
		}

		private final void handleLoginScreenInput(int var1) {
			try {
				if (var1 != 2) {
					this.optionsMenuShow = true;
				}

				if (this.m_Zb > 0) {
					--this.m_Zb;
				}


				if (this.loginScreenNumber != 0) {
					if (loginScreenNumber == 1) {
						menuNewUser.handleMouse(this.mouseX, this.mouseY, this.currentMouseButtonDown,
								this.lastMouseButtonDown);
						if (menuNewUser.isClicked(menuNewUserUsername))
							menuNewUser.setFocus(menuNewUserPassword);
						if (menuNewUser.isClicked(menuNewUserPassword))
							menuNewUser.setFocus(menuNewUserEmail);
						if (menuNewUser.isClicked(menuNewUserEmail))
							menuNewUser.setFocus(menuNewUserSubmit);
						if (menuNewUser.isClicked(menuNewUserCancel))
							loginScreenNumber = 0;
						if (menuNewUser.isClicked(menuNewUserSubmit)) {
							if (menuNewUser.getControlText(menuNewUserUsername) != null
									&& menuNewUser.getControlText(menuNewUserUsername).length() == 0
									|| menuNewUser.getControlText(menuNewUserPassword) != null
									&& menuNewUser.getControlText(menuNewUserPassword).length() == 0
									|| menuNewUser.getControlText(menuNewUserEmail) != null
									&& menuNewUser.getControlText(menuNewUserEmail).length() == 0) {
								menuNewUser.setText(menuNewUserStatus, "Please fill in ALL requested");
								menuNewUser.setText(menuNewUserStatus2, "information to continue!");
								return;
							}
							menuNewUser.setText(menuNewUserStatus, "Please wait...");
							menuNewUser.setText(menuNewUserStatus2, "Creating new account");

							String username = menuNewUser.getControlText(menuNewUserUsername);
							String password = menuNewUser.getControlText(menuNewUserPassword);
							String email = menuNewUser.getControlText(menuNewUserEmail);
							sendRegister(username, password, email);
							return;
						}
					} else if (this.loginScreenNumber == 2) {
						this.panelLogin.handleMouse(this.mouseX, this.mouseY, this.currentMouseButtonDown,
								this.lastMouseButtonDown);
						if (this.panelLogin.isClicked(this.m_Xi)) {
							this.loginScreenNumber = 0;
						}
						if(Config.isAndroid()) {
							if (this.panelLogin.isClicked(this.rememberButtonIdx)) {

								boolean temp = clientPort.saveCredentials(this.panelLogin.getControlText(this.controlLoginUser) + "," + this.panelLogin.getControlText(this.controlLoginPass));
								if(temp)
									this.panelLogin.setText(this.controlLoginStatus2, "@gre@Credentials Saved");
							}
						}


						if (this.panelLogin.isClicked(this.controlLoginUser)) {
							this.panelLogin.setFocus(this.controlLoginPass);
						}

						if (this.panelLogin.isClicked(this.controlLoginPass) || this.panelLogin.isClicked(this.m_be)) {

							this.setUsername(this.panelLogin.getControlText(this.controlLoginUser));
							this.password = this.panelLogin.getControlText(this.controlLoginPass);
							this.autoLoginTimeout = 2;
							this.login(-12, this.password, this.getUsername(), false);
						}
					}
				} else {
					this.panelLoginWelcome.handleMouse(this.mouseX, this.mouseY, this.currentMouseButtonDown,
							this.lastMouseButtonDown);
					if (this.panelLoginWelcome.isClicked(this.m_Jj)) {
						this.loginScreenNumber = 2;
						this.panelLogin.setText(this.controlLoginStatus1, "");
						this.panelLogin.setText(this.controlLoginStatus2, "Please enter your username and password");
						this.panelLogin.setFocus(this.controlLoginUser);
					} else if (panelLoginWelcome.isClicked(loginButtonNewUser)) {
						loginScreenNumber = 1;
						this.menuNewUser.setText(this.menuNewUserStatus, "Please fill in ALL fields");
						this.menuNewUser.setText(this.menuNewUserStatus2, "and click submit.");
						menuNewUser.setFocus(menuNewUserUsername);
					}
				}

			} catch (RuntimeException var3) {
				throw GenUtil.makeThrowable(var3, "client.BA(" + var1 + ')');
			}
		}

		private void sendRegister(String user, String pass, String email) {
			if (this.m_Zb > 0) {
				this.showLoginScreenStatus("Please wait...", "Communicating with server");
				try {
					GenUtil.sleepShadow(2000L);
				} catch (Exception var14) {
					;
				}
				this.showLoginScreenStatus("Sorry! The server is currently full.", "Please try again later");
			}

			this.m_Zb = 1500;
			username = user;
			user = DataOperations.addCharacters(user, 20);
			password = pass;
			pass = DataOperations.addCharacters(pass, 20);

			if (user.trim().length() == 0) {
				showLoginScreenStatus("Please fill in ALL requested", "information to continue!");
				return;
			}
			if (user.trim().length() < 2) {
				showLoginScreenStatus("Username must be atleast 2", "characters long!");
				return;
			}
			if (user.trim().length() > 12) {
				showLoginScreenStatus("Username is too long, please use", "username with length of 2-12");
				return;
			}
			if (pass.trim().length() < 4) {
				showLoginScreenStatus("Password must be atleast 4", "characters long!");
				return;
			}
			if (pass.trim().length() > 16) {
				showLoginScreenStatus("Password is too long, please use", "password with length of 4-16");
				return;
			}
			if (!isValidEmailAddress(email)) {
				showLoginScreenStatus("Invalid email address", "please use a valid email adress");
				return;
			}
			try {
				this.setClientStream(new Network_Socket(this.openSocket(Config.SERVER_PORT, Config.SERVER_IP), this));
				this.getClientStream().m_d = MiscFunctions.maxReadTries;

				getClientStream().newPacket(78);
				getClientStream().writeBuffer1.putString(user);
				getClientStream().writeBuffer1.putString(pass);
				getClientStream().writeBuffer1.putString(email);
				getClientStream().finishPacketAndFlush();

				int registerResponse = getClientStream().read();

				System.out.println("Registration response:" + registerResponse);
				if (registerResponse == 0) {
					panelLogin.setText(controlLoginUser, username.replaceAll("[^=,\\da-zA-Z\\s]|(?<!,)\\s", " ").trim());
					panelLogin.setText(controlLoginPass, password);
					// menuNewUser.setText
					// menuNewUser.setText(menuNewUserUsername, "");
					// menuNewUser.setText(menuNewUserPassword, "");
					// menuNewUser.setText(menuNewUserEmail, "");
					// login(-12, user, pass, false);
					showLoginScreenStatus("Account created", "you can now login with your user");
					return;
				}
				if (registerResponse == -1) {
					showLoginScreenStatus("Server timed out", "try again");
					return;
				}
				if (registerResponse == 2) {
					showLoginScreenStatus("Username already taken", "choose a different one");
					return;
				}
				if (registerResponse == 3) {
					showLoginScreenStatus("E-mail address already in use", "use another E-mail");
					return;
				}
				if (registerResponse == 4) {
					showLoginScreenStatus("Registration disabled", "try registering from website.");
					return;
				}
				if (registerResponse == 5) {
					showLoginScreenStatus("You have registered recently", "to prevent flooding, wait an hour.");
					return;
				}
				if (registerResponse == 5) {
					showLoginScreenStatus("Error while registering", "please try again.");
					return;
				}
				if (registerResponse == 6) {
					showLoginScreenStatus("Invalid e-mail address", "please use a valid email adress");
					return;
				}
				if (registerResponse == 7) {
					showLoginScreenStatus("Username must be 2-12", "characters long!");
					return;
				}
				if (registerResponse == 6) {
					showLoginScreenStatus("Password is too long, please use", "password with length of 4-16");
					return;
				}
				showLoginScreenStatus("Error unable to login.", "Unrecognised response code");
			} catch (Exception e) {
				this.showLoginScreenStatus("Sorry! Unable to connect.", "Check internet settings");
				e.printStackTrace();
			}

		}

		public static boolean isValidEmailAddress(String email) {
			boolean stricterFilter = true;
			String stricterFilterString = "[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}";
			String laxString = ".+@.+\\.[A-Za-z]{2}[A-Za-z]*";
			String emailRegex = stricterFilter ? stricterFilterString : laxString;
			java.util.regex.Pattern p = java.util.regex.Pattern.compile(emailRegex);
			java.util.regex.Matcher m = p.matcher(email);
			return m.matches();
		}

		private final void handleMenuItemClicked(boolean var1, int item) {
			try {

				MenuItemAction var3 = this.menuCommon.getItemAction((int) item);
				int indexOrX = this.menuCommon.getItemIndexOrX(item);
				int idOrZ = this.menuCommon.getItemIdOrZ((int) item);
				int dir = this.menuCommon.getItemDirection((int) item);
				int tileID = this.menuCommon.getItemTileID(item);
				int var8 = this.menuCommon.getItemParam_l(item);
				String var9 = this.menuCommon.getItemStringB(item);

				RSCharacter character = null;
				int cTileX;
				int cTileZ;

				switch (var3) {
				case GROUND_ITEM_CAST_SPELL: {
					this.walkToGroundItem(this.playerLocalX, this.playerLocalZ, indexOrX, idOrZ, true);
					this.getClientStream().newPacket(249);
					this.getClientStream().writeBuffer1.putShort(tileID); // spell
					this.getClientStream().writeBuffer1.putShort(indexOrX + this.midRegionBaseX);
					this.getClientStream().writeBuffer1.putShort(idOrZ + this.midRegionBaseZ);
					this.getClientStream().writeBuffer1.putShort(dir);
					this.getClientStream().finishPacket();
					this.selectedSpell = -1;
					break;
				}
				case GROUND_ITEM_USE_ITEM: {
					this.walkToGroundItem(this.playerLocalX, this.playerLocalZ, indexOrX, idOrZ, true);
					this.getClientStream().newPacket(53);
					this.getClientStream().writeBuffer1.putShort(this.midRegionBaseX + indexOrX);
					this.getClientStream().writeBuffer1.putShort(this.midRegionBaseZ + idOrZ);
					this.getClientStream().writeBuffer1.putShort(dir);
					this.getClientStream().writeBuffer1.putShort(tileID); // inventory
					// item

					this.getClientStream().finishPacket();
					this.selectedItemInventoryIndex = -1;
					break;
				}
				case GROUND_ITEM_TAKE: {
					this.walkToGroundItem(this.playerLocalX, this.playerLocalZ, indexOrX, idOrZ, true);
					this.getClientStream().newPacket(247);
					this.getClientStream().writeBuffer1.putShort(indexOrX + this.midRegionBaseX);
					this.getClientStream().writeBuffer1.putShort(this.midRegionBaseZ + idOrZ);
					this.getClientStream().writeBuffer1.putShort(dir);
					this.getClientStream().finishPacket();
					break;
				}
				case GROUND_ITEM_EXAMINE: {
					this.showMessage(false, (String) null, EntityHandler.getItemDef(indexOrX).getDescription(),
							MessageType.GAME, 0, (String) null, (String) null);
					break;
				}
				case ITEM_EXAMINE: {
					if (EntityHandler.getItemDef(indexOrX).stackable) {
						this.showMessage(false, (String) null,
								StringUtil.formatItemCount(getInventoryCount(indexOrX)) + (getInventoryCount(indexOrX) < 1000 ? "x" : "") + " - "
										+ EntityHandler.getItemDef(indexOrX).getDescription(),
										MessageType.GAME, 0, (String) null, (String) null);
					} else {
						this.showMessage(false, (String) null, EntityHandler.getItemDef(indexOrX).getDescription(),
								MessageType.GAME, 0, (String) null, (String) null);
					}
					break;
				}
				case WALL_CAST_SPELL: {
					this.walkToWall(indexOrX, idOrZ, dir);
					this.getClientStream().newPacket(180);
					this.getClientStream().writeBuffer1.putShort(this.midRegionBaseX + indexOrX);
					this.getClientStream().writeBuffer1.putShort(this.midRegionBaseZ + idOrZ);
					this.getClientStream().writeBuffer1.putByte(dir);
					this.getClientStream().writeBuffer1.putShort(tileID);

					this.getClientStream().finishPacket();
					this.selectedSpell = -1;
					break;
				}
				case WALL_USE_ITEM: {
					this.walkToWall(indexOrX, idOrZ, dir);
					this.getClientStream().newPacket(161);
					this.getClientStream().writeBuffer1.putShort(indexOrX + this.midRegionBaseX);
					this.getClientStream().writeBuffer1.putShort(idOrZ + this.midRegionBaseZ);
					this.getClientStream().writeBuffer1.putByte(dir);
					this.getClientStream().writeBuffer1.putShort(tileID);
					this.getClientStream().finishPacket();
					this.selectedItemInventoryIndex = -1;
					break;
				}
				case WALL_COMMAND1: {
					this.walkToWall(indexOrX, idOrZ, dir);
					this.getClientStream().newPacket(14);
					this.getClientStream().writeBuffer1.putShort(indexOrX + this.midRegionBaseX);
					this.getClientStream().writeBuffer1.putShort(idOrZ + this.midRegionBaseZ);
					this.getClientStream().writeBuffer1.putByte(dir);
					this.getClientStream().finishPacket();
					break;
				}
				case WALL_COMMAND2: {
					this.walkToWall(indexOrX, idOrZ, dir);
					this.getClientStream().newPacket(127);
					this.getClientStream().writeBuffer1.putShort(this.midRegionBaseX + indexOrX);
					this.getClientStream().writeBuffer1.putShort(idOrZ + this.midRegionBaseZ);
					this.getClientStream().writeBuffer1.putByte(dir);
					this.getClientStream().finishPacket();
					break;
				}
				case WALL_EXAMINE: {
					this.showMessage(false, (String) null, EntityHandler.getDoorDef(indexOrX).getDescription(),
							MessageType.GAME, 0, (String) null, (String) null);
					break;
				}
				case OBJECT_CAST_SPELL: {
					this.walkToObject(indexOrX, idOrZ, dir, 5126, tileID);
					this.getClientStream().newPacket(99);
					this.getClientStream().writeBuffer1.putShort(var8);
					this.getClientStream().writeBuffer1.putShort(indexOrX + this.midRegionBaseX);
					this.getClientStream().writeBuffer1.putShort(this.midRegionBaseZ + idOrZ);

					this.getClientStream().finishPacket();
					this.selectedSpell = -1;
					break;
				}
				case OBJECT_USE_ITEM: {
					this.walkToObject(indexOrX, idOrZ, dir, 5126, tileID);
					this.getClientStream().newPacket(115);
					this.getClientStream().writeBuffer1.putShort(indexOrX + this.midRegionBaseX);
					this.getClientStream().writeBuffer1.putShort(idOrZ + this.midRegionBaseZ);
					this.getClientStream().writeBuffer1.putShort(var8);
					this.getClientStream().finishPacket();
					this.selectedItemInventoryIndex = -1;
					break;
				}
				case OBJECT_COMMAND1: {
					this.walkToObject(indexOrX, idOrZ, dir, 5126, tileID);
					this.getClientStream().newPacket(136);
					this.getClientStream().writeBuffer1.putShort(this.midRegionBaseX + indexOrX);
					this.getClientStream().writeBuffer1.putShort(idOrZ + this.midRegionBaseZ);
					this.getClientStream().finishPacket();
					break;
				}
				case OBJECT_COMMAND2: {
					this.walkToObject(indexOrX, idOrZ, dir, 5126, tileID);
					this.getClientStream().newPacket(79);
					this.getClientStream().writeBuffer1.putShort(this.midRegionBaseX + indexOrX);
					this.getClientStream().writeBuffer1.putShort(this.midRegionBaseZ + idOrZ);
					this.getClientStream().finishPacket();
					break;
				}
				case OBJECT_EXAMINE: {
					this.showMessage(false, (String) null, EntityHandler.getObjectDef(indexOrX).getDescription(),
							MessageType.GAME, 0, (String) null, (String) null);
					break;
				}
				case ITEM_CAST_SPELL: {
					this.getClientStream().newPacket(4);
					this.getClientStream().writeBuffer1.putShort(idOrZ);
					this.getClientStream().writeBuffer1.putShort(indexOrX);
					this.getClientStream().finishPacket();
					if (selectedSpell != 16) {
						showUiTab = 4;
					}
					this.selectedSpell = -1;
					break;
				}
				case ITEM_USE_ITEM: {
					this.getClientStream().newPacket(91);
					this.getClientStream().writeBuffer1.putShort(indexOrX);
					this.getClientStream().writeBuffer1.putShort(idOrZ);
					this.getClientStream().finishPacket();
					this.selectedItemInventoryIndex = -1;
					break;
				}
				case ITEM_REMOVE_EQUIPPED: {
					this.getClientStream().newPacket(170);
					this.getClientStream().writeBuffer1.putShort(indexOrX);
					this.getClientStream().finishPacket();
					break;
				}
				case ITEM_EQUIP: {
					this.getClientStream().newPacket(169);
					this.getClientStream().writeBuffer1.putShort(indexOrX);
					this.getClientStream().finishPacket();
					break;
				}
				case ITEM_COMMAND: {
					this.getClientStream().newPacket(90);
					this.getClientStream().writeBuffer1.putShort(indexOrX);
					this.getClientStream().finishPacket();
					break;
				}
				case ITEM_USE: {
					this.selectedItemInventoryIndex = indexOrX;
					if (!Config.isAndroid())
						this.showUiTab = 0;
					this.m_ig = EntityHandler.getItemDef(this.inventoryItemID[this.selectedItemInventoryIndex]).getName();
					break;
				}
				case ITEM_DROP: {
					this.getClientStream().newPacket(246);
					this.getClientStream().writeBuffer1.putShort(indexOrX);
					int amount = 1;
					if (EntityHandler.getItemDef(inventoryItemID[indexOrX]).isStackable())
						amount = getInventoryCount(this.inventoryItemID[indexOrX]);
					this.getClientStream().writeBuffer1.putInt(amount);
					this.getClientStream().finishPacket();
					if (!Config.isAndroid())
						this.showUiTab = 0;
					this.selectedItemInventoryIndex = -1;
					this.showMessage(false, (String) null,
							"Dropping " + EntityHandler.getItemDef(this.inventoryItemID[indexOrX]).getName(),
							MessageType.INVENTORY, 0, (String) null, (String) null);
					break;
				}
				case ITEM_DROP_X: {
					dropInventorySlot = indexOrX;
					this.showItemModX(InputXPrompt.dropX, InputXAction.DROP_X, true);
					break;
				}
				case NPC_CAST_SPELL: {
					character = this.getServerNPC(indexOrX);
					if (character == null) {
						return;
					}
					cTileX = (character.currentX - 64) / this.tileSize;
					cTileZ = (character.currentZ - 64) / this.tileSize;
					this.walkToActionSource(this.playerLocalX, this.playerLocalZ, cTileX, (int) cTileZ, true);
					this.getClientStream().newPacket(50);
					this.getClientStream().writeBuffer1.putShort(idOrZ);
					this.getClientStream().writeBuffer1.putShort(indexOrX);

					this.getClientStream().finishPacket();
					this.selectedSpell = -1;
					break;
				}
				case NPC_USE_ITEM: {
					character = this.getServerNPC(indexOrX);
					if (character == null) {
						return;
					}
					cTileX = (character.currentX - 64) / this.tileSize;
					cTileZ = (character.currentZ - 64) / this.tileSize;
					this.walkToActionSource(this.playerLocalX, this.playerLocalZ, cTileX, (int) cTileZ, true);
					this.getClientStream().newPacket(135);
					this.getClientStream().writeBuffer1.putShort(indexOrX);
					this.getClientStream().writeBuffer1.putShort(idOrZ);
					this.getClientStream().finishPacket();
					this.selectedItemInventoryIndex = -1;
					break;
				}
				case NPC_TALK_TO: {
					character = this.getServerNPC(indexOrX);
					if (character == null) {
						return;
					}
					cTileX = (character.currentX - 64) / this.tileSize;
					cTileZ = (character.currentZ - 64) / this.tileSize;
					this.walkToActionSource(this.playerLocalX, this.playerLocalZ, cTileX, (int) cTileZ, true);
					this.getClientStream().newPacket(153);
					this.getClientStream().writeBuffer1.putShort(indexOrX);
					this.getClientStream().finishPacket();
					break;
				}
				case NPC_COMMAND1: {
					character = this.getServerNPC(indexOrX);
					if (character == null) {
						return;
					}
					cTileX = (character.currentX - 64) / this.tileSize;
					cTileZ = (character.currentZ - 64) / this.tileSize;
					this.walkToActionSource(this.playerLocalX, this.playerLocalZ, cTileX, (int) cTileZ, true);
					this.getClientStream().newPacket(202);
					this.getClientStream().writeBuffer1.putShort(indexOrX);
					this.getClientStream().finishPacket();
					break;
				}
				case NPC_COMMAND2: {
					character = this.getServerNPC(indexOrX);
					if (character == null) {
						return;
					}
					cTileX = (character.currentX - 64) / this.tileSize;
					cTileZ = (character.currentZ - 64) / this.tileSize;
					this.walkToActionSource(this.playerLocalX, this.playerLocalZ, cTileX, (int) cTileZ, true);
					this.getClientStream().newPacket(203);
					this.getClientStream().writeBuffer1.putShort(indexOrX);
					this.getClientStream().finishPacket();
					break;
				}
				case NPC_ATTACK2:
				case NPC_ATTACK1: {
					character = this.getServerNPC(indexOrX);
					if (character == null) {
						return;
					}
					cTileX = (character.currentX - 64) / this.tileSize;
					cTileZ = (character.currentZ - 64) / this.tileSize;
					this.walkToActionSource(this.playerLocalX, this.playerLocalZ, cTileX, (int) cTileZ, true);
					this.getClientStream().newPacket(190);
					this.getClientStream().writeBuffer1.putShort(indexOrX);
					this.getClientStream().finishPacket();
					break;
				}
				case NPC_EXAMINE: {
					this.showMessage(false, (String) null, EntityHandler.getNpcDef(indexOrX).getDescription(),
							MessageType.GAME, 0, (String) null, (String) null);
					break;
				}
				case PLAYER_CAST_SPELL: {
					character = this.getServerPlayer(indexOrX);
					if (character == null) {
						return;
					}
					cTileX = (character.currentX - 64) / this.tileSize;
					cTileZ = (character.currentZ - 64) / this.tileSize;
					this.walkToActionSource(this.playerLocalX, this.playerLocalZ, cTileX, (int) cTileZ, true);
					this.getClientStream().newPacket(229);
					this.getClientStream().writeBuffer1.putShort(idOrZ);
					this.getClientStream().writeBuffer1.putShort(indexOrX);

					this.getClientStream().finishPacket();
					this.selectedSpell = -1;
					break;
				}
				case PLAYER_USE_ITEM: {
					character = this.getServerPlayer(indexOrX);
					if (character == null) {
						return;
					}
					cTileX = (character.currentX - 64) / this.tileSize;
					cTileZ = (character.currentZ - 64) / this.tileSize;
					this.walkToActionSource(this.playerLocalX, this.playerLocalZ, cTileX, (int) cTileZ, true);
					this.getClientStream().newPacket(113);
					this.getClientStream().writeBuffer1.putShort(indexOrX);
					this.getClientStream().writeBuffer1.putShort(idOrZ);
					this.getClientStream().finishPacket();
					this.selectedItemInventoryIndex = -1;
					break;
				}
				case PLAYER_ATTACK_DIVERGENT:
				case PLAYER_ATTACK_SIMILAR: {
					character = this.getServerPlayer(indexOrX);
					if (character == null) {
						return;
					}
					cTileX = (character.currentX - 64) / this.tileSize;
					cTileZ = (character.currentZ - 64) / this.tileSize;
					this.walkToActionSource(this.playerLocalX, this.playerLocalZ, cTileX, (int) cTileZ, true);
					this.getClientStream().newPacket(171);
					this.getClientStream().writeBuffer1.putShort(indexOrX);
					this.getClientStream().finishPacket();
					break;
				}
				case PLAYER_DUEL: {
					this.getClientStream().newPacket(103);
					this.getClientStream().writeBuffer1.putShort(indexOrX);
					this.getClientStream().finishPacket();
					break;
				}
				case PLAYER_TRADE: {
					this.getClientStream().newPacket(142);
					this.getClientStream().writeBuffer1.putShort(indexOrX);
					this.getClientStream().finishPacket();
					break;
				}
				case PLAYER_FOLLOW: {
					this.getClientStream().newPacket(165);
					this.getClientStream().writeBuffer1.putShort(indexOrX);
					this.getClientStream().finishPacket();
					break;
				}
				case REPORT_ABUSE: {
					this.inputTextFinal = "";
					this.reportAbuse_State = 1;
					this.inputTextCurrent = var9;
					break;
				}


				case CHAT_ADD_FRIEND: {
					this.addFriend(var9);
					break;
				}
				case CHAT_ADD_IGNORE: {
					this.addIgnore(var9);
					break;
				}
				case CHAT_MESSAGE: {
					this.chatMessageTarget = var9;
					this.chatMessageInput = "";
					this.panelSocialPopup_Mode = SocialPopupMode.MESSAGE_FRIEND;
					this.chatMessageInputCommit = "";
					break;
				}
				case LANDSCAPE_CAST_SPELL: {
					this.walkToActionSource(this.playerLocalX, this.playerLocalZ, indexOrX, (int) idOrZ, true);
					this.getClientStream().newPacket(158);
					this.getClientStream().writeBuffer1.putShort(dir);
					this.getClientStream().writeBuffer1.putShort(indexOrX + this.midRegionBaseX);
					this.getClientStream().writeBuffer1.putShort(this.midRegionBaseZ + idOrZ);

					this.getClientStream().finishPacket();
					this.selectedSpell = -1;
					break;
				}
				case LANDSCAPE_WALK_HERE: {
					this.walkToActionSource(this.playerLocalX, this.playerLocalZ, indexOrX, (int) idOrZ, false);
					if (this.mouseClickXStep == -24) {
						this.mouseClickXStep = 24;
					}
					break;
				}
				case SELF_CAST_SPELL: {
					this.getClientStream().newPacket(137);
					this.getClientStream().writeBuffer1.putShort(indexOrX);
					this.getClientStream().finishPacket();
					this.selectedSpell = -1;
					break;
				}
				case CANCEL: {
					this.selectedSpell = -1;
					this.selectedItemInventoryIndex = -1;
					break;
				}
				case DEV_ADD_NPC: {
					sendCommandString("cnpc " + devMenuNpcID + " " + (indexOrX + midRegionBaseX) + " "
							+ (idOrZ + midRegionBaseZ) + "");
					break;
				}
				case DEV_REMOVE_NPC: {
					sendCommandString("rpc " + indexOrX + "");
					break;
				}
				case DEV_ADD_OBJECT: {
					sendCommandString("aobject " + devMenuNpcID + " " + (indexOrX + midRegionBaseX) + " "
							+ (idOrZ + midRegionBaseZ) + "");
					break;
				}
				case DEV_REMOVE_OBJECT: {
					sendCommandString("robject " + indexOrX + " " + (indexOrX + midRegionBaseX) + " "
							+ (idOrZ + midRegionBaseZ) + "");
					break;
				}
				case DEV_ROTATE_OBJECT: {
					sendCommandString("rotateobject " + indexOrX + " " + (indexOrX + midRegionBaseX) + " "
							+ (idOrZ + midRegionBaseZ) + "");
					break;
				}
				case MOD_SUMMON_PLAYER: {
					String playerName = var9;
					playerName = playerName.replaceAll(" ", "_");
					sendCommandString("summon " + playerName);
					break;
				}
				case MOD_GOTO_PLAYER: {
					String playerName = var9;
					playerName = playerName.replaceAll(" ", "_");
					sendCommandString("goto " + playerName);
					break;
				}
				case MOD_PUT_PLAYER_JAIL: {
					String playerName = var9;
					playerName = playerName.replaceAll(" ", "_");
					sendCommandString("put " + playerName);
					break;
				}
				case MOD_KICK_PLAYER: {
					String playerName = var9;
					playerName = playerName.replaceAll(" ", "_");
					sendCommandString("kick " + playerName);
					break;
				}
				case MOD_CHECK_PLAYER: {
					String playerName = var9;
					playerName = playerName.replaceAll(" ", "_");
					sendCommandString("check " + playerName);
					break;
				}
				case MOD_TELEPORT: {
					int clickX = indexOrX + midRegionBaseX;
					int clickY = idOrZ + midRegionBaseZ;
					sendCommandString("teleport " + (clickX) + " " + (clickY));
					break;
				}
				case CLAN_MENU_KICK: {
					String playerName = var9;
					playerName = playerName.replaceAll(" ", "_");
					kickClanPlayer(playerName);
					String[] kickMessage = new String[] { "Are you sure you want to kick " + playerName + " from clan?" };
					this.showItemModX(kickMessage, InputXAction.KICK_CLAN_PLAYER, false);
					this.showUiTab = 0;
					break;
				}
				default:
					System.err.println("Bad menu option: " + var3);
					break;
				}
			} catch (RuntimeException var13) {
				throw GenUtil.makeThrowable(var13, "client.KA(" + var1 + ',' + item + ')');
			}
		}

		private final void handlePacket1(int opcode, int length) {
			try {//System.out.println("opcode1: " + opcode);
				
				if (opcode == 88) { // Create NPC
					int id = packetsIncoming.getShort();

					String name = packetsIncoming.readString();
					String description = packetsIncoming.readString();

					String optionCommand = "";
					int commandLength = packetsIncoming.getByte() & 0xff;
					if (commandLength > 0) {
						optionCommand = packetsIncoming.readString();
					}

					int attack = packetsIncoming.getByte() & 0xff;
					int strength = packetsIncoming.getByte() & 0xff;
					int defense = packetsIncoming.getByte() & 0xff;
					int hits = packetsIncoming.getByte() & 0xff;
					boolean attackable = packetsIncoming.getByte() == 1;

					int spriteCount = packetsIncoming.getByte() & 0xff;
					int[] sprites = new int[spriteCount];
					for (int i35 = 0; i35 < spriteCount; i35++) {
						sprites[i35] = packetsIncoming.get32();
					}
					for (int l37 = spriteCount; l37 < 12; l37++)
						sprites[l37] = 0;

					int hairColour = packetsIncoming.get32();
					int topColour = packetsIncoming.get32();
					int bottomColour = packetsIncoming.get32();
					int skinColour = packetsIncoming.get32();

					int camera1 = packetsIncoming.getShort();
					int camera2 = packetsIncoming.getShort();

					int walkModel = packetsIncoming.getByte() & 0xff;
					int combatModel = packetsIncoming.getByte() & 0xff;
					int combatSprite = packetsIncoming.getByte() & 0xff;
					NPCDef newNpc = new NPCDef(name, description, optionCommand, attack, strength, hits, defense,
							attackable, sprites, hairColour, topColour, bottomColour, skinColour, camera1, camera2,
							walkModel, combatModel, combatSprite, id);
					EntityHandler.npcs.set(id, newNpc);
					return;
				}
				else if (opcode == 134) {
					int interfaceID = packetsIncoming.getByte();
					switch (interfaceID) {
					case 0: // Progress Bar Updates
						if (!Config.BATCH_PROGRESS_BAR) {
							batchProgressBar.hide();
							return;
						}
						/* Progress bar */
						int actionType = packetsIncoming.getByte() & 0xff;
						if (actionType == 1) {
							int delay = packetsIncoming.getShort();
							int repeatFor = packetsIncoming.getByte() & 0xff;
							batchProgressBar.initVariables(repeatFor, delay);
							batchProgressBar.show();
						}
						if (actionType == 2) {
							batchProgressBar.resetProgressBar();
						}
						if (actionType == 3) {
							int repeat = packetsIncoming.getByte() & 0xff;
							batchProgressBar.updateProgress(repeat);
						}
						break;
					case 1: // Bank Pin
						int action = packetsIncoming.getByte();
						if (action == 0) {
							bankPinInterface.show();
						} else if (action == 1) {
							bankPinInterface.hide();
						}
						break;
					case 2: // Ironman Check
						int iAction = packetsIncoming.getByte();
						if (iAction == 0) {
							ironmanInterface.setIronManMode(packetsIncoming.getByte());
							ironmanInterface.setIronManRestriction(packetsIncoming.getByte());
						} else if (iAction == 1) {
							ironmanInterface.setVisible(true);
						} else if (iAction == 2) {
							ironmanInterface.setVisible(false);
						}
						break;

					case 3: // Auction House
						int packetType = packetsIncoming.getByte() & 0xff;
						if (packetType == 0) { // start receiving items
							auctionHouse.resetAuctionItems();
						} else if (packetType == 1) { // read items
							int auctionItemCount = packetsIncoming.getShort();
							for (int i = 0; i < auctionItemCount; i++) {
								int auctionID = packetsIncoming.get32();
								int itemID = packetsIncoming.get32();
								int amount = packetsIncoming.get32();
								int price = packetsIncoming.get32();
								String seller = "";
								boolean isMyItem = packetsIncoming.getByte() == 1;
								if (isMyItem) {
									seller = localPlayer.displayName;
								} else {
									seller = packetsIncoming.readString();
								}
								int hoursLeft = packetsIncoming.getByte();
								auctionHouse.addAuction(auctionID, itemID, amount, price, seller, hoursLeft);
							}
							auctionHouse.setVisible(true);
						}
						break;
					case 5: // Online List
						onlineList.reset();
						int onlinePlayerCount = packetsIncoming.getShort();
						for (int i = 0; i < onlinePlayerCount; i++) {
							onlineList.addOnlineUser(packetsIncoming.readString(), packetsIncoming.getByte());
						}
						onlineList.setVisible(true);
						break;
					case 6: // Fishing Trawler
						action = packetsIncoming.getByte();
						switch (action) {
						case 0:
							fishingTrawlerInterface.show();
							break;
						case 1:
							fishingTrawlerInterface.setVariables(packetsIncoming.getShort(), packetsIncoming.getShort(),
									packetsIncoming.getByte(), packetsIncoming.getByte() == 1);
							break;
						case 2:
							fishingTrawlerInterface.hide();
							break;
						}
						break;
					case 7: // Clans
						actionType = packetsIncoming.getByte();
						switch (actionType) {
						case 0: // send clan
							clan.setClanName(packetsIncoming.readString());
							clan.setClanTag(packetsIncoming.readString());
							clan.setClanLeaderUsername(packetsIncoming.readString());
							boolean isLeader = packetsIncoming.getByte() == 1;
							clan.setClanLeader(isLeader);
							SocialLists.clanListCount = packetsIncoming.getByte();
							for (int id = 0; id < SocialLists.clanListCount; id++) {
								clan.username[id] = packetsIncoming.readString();
								clan.clanRank[id] = packetsIncoming.getByte();
								clan.onlineClanMember[id] = packetsIncoming.getByte();
							}
							clan.putClan(true);
							break;
						case 1:// leave clan
							clan.putClan(false);
							clan.update();
							// UPDATE GUI?
							break;
						case 2:// SENT INVITATION
							clan.getClanInterface().initializeInvite(packetsIncoming.readString(), packetsIncoming.readString());
							break;
						case 3: // SETTINGs
							clan.setClanSetting(0, packetsIncoming.getByte());
							clan.setClanSetting(1, packetsIncoming.getByte());
							clan.setClanSetting(2, packetsIncoming.getByte());
							clan.allowed[0] = packetsIncoming.getByte() == 1;
							clan.allowed[1] = packetsIncoming.getByte() == 1;
							break;
						case 4: // clan search visuallity
							clan.getClanInterface().resetClans();
							int clanCount = packetsIncoming.getShort();
							for (int i = 0; i < clanCount; i++) {
								int clanID = packetsIncoming.getShort();
								String clanName = packetsIncoming.readString();
								String clanTag = packetsIncoming.readString();
								int members = packetsIncoming.getByte();
								int canJoin = packetsIncoming.getByte();
								int clanPoints = packetsIncoming.get32();
								int clanRank = packetsIncoming.getShort();
								clan.getClanInterface().addClan(clanID, clanName, clanTag, members, canJoin, clanPoints, clanRank);
							}
							break;
						}
						break;
					}
					return;
				}
				/*if(opcode == 50) { // Achievements
					int achievementStatus = this.packetsIncoming.getByte();
					if (achievementStatus == 1) {
						totalAchievements = this.packetsIncoming.getShort();
						for (int i = 0; i < totalAchievements; i++) {
							int achievementID = this.packetsIncoming.get32();
							byte progress = this.packetsIncoming.getByte();

							String achievementName = this.packetsIncoming.readString();
							String achievementTitle = this.packetsIncoming.readString();
							String achievementDesc = this.packetsIncoming.readString();

							achievementNames[achievementID] = achievementName;
							achievementTitles[achievementID] = achievementTitle;
							achievementDescs[achievementID] = achievementDesc;
							achievementProgress[achievementID] = progress;
						}
					} else if (achievementStatus == 2) {
						int achievementID = this.packetsIncoming.get32();
						byte progress = this.packetsIncoming.getByte();

						achievementProgress[achievementID] = progress;
					}
					return;
				}*/
				else if(opcode == 135) { // Kill Announcements
					if (!Config.WANT_KILL_FEED) return;
					String killed = this.packetsIncoming.readString();
					String killer = this.packetsIncoming.readString();
					int killType = this.packetsIncoming.get32();
					killQueue.addKill(new KillAnnouncer(killer, killed, killType));
					return;
				}
				// opcode = this.clientStream.decodeIncomingOpcode(opcode);
				else if (opcode == 131) { // Send Message
					int crown = this.packetsIncoming.getUnsignedByte();
					MessageType type = MessageType.lookup(this.packetsIncoming.getUnsignedByte());
					int var5 = this.packetsIncoming.getUnsignedByte();
					String message = this.packetsIncoming.readString();
					String sender = null;
					String clan = null;
					String color = null;
					if ((var5 & 1) != 0) {
						sender = this.packetsIncoming.readString();
					}

					if ((1 & var5) != 0) {
						clan = this.packetsIncoming.readString();
					}

					if ((var5 & 2) != 0) {
						color = this.packetsIncoming.readString();
					}

					this.showMessage(false, sender, message, type, crown, clan, color);

				} else if (opcode == 4) { // ???
					this.closeConnection(true);

				} else if (opcode == 183) { // ???
					this.cantLogout((byte) -65);

				} else if (opcode == 165) { // ???
					this.closeConnection(false);

				} else if (opcode == 149) { // Log In/Out Message
					String currentName = this.packetsIncoming.readString();
					String formerName = this.packetsIncoming.readString();
					int arg = this.packetsIncoming.getUnsignedByte();
					boolean rename = (arg & 1) != 0;
					boolean online = (4 & arg) != 0;
					String var9 = null;
					if (online) {
						var9 = this.packetsIncoming.readString();
					}
					for (int i = 0; i < SocialLists.friendListCount; ++i) {
						if (!rename) {
							if (SocialLists.friendList[i].equals(currentName)) {
								if (SocialLists.friendListArgS[i] == null && online) {
									this.showMessage(false, (String) null, currentName + " has logged in",
											MessageType.FRIEND_STATUS, 0, (String) null, (String) null);
								}

								if (null != SocialLists.friendListArgS[i] && !online) {
									this.showMessage(false, (String) null, currentName + " has logged out",
											MessageType.FRIEND_STATUS, 0, (String) null, (String) null);
								}

								SocialLists.friendListOld[i] = formerName;
								SocialLists.friendListArgS[i] = var9;
								SocialLists.friendListArg[i] = arg;
								this.sortOnlineFriendsList();
								return;
							}
						} else if (SocialLists.friendList[i].equals(formerName)) {
							if (SocialLists.friendListArgS[i] == null && online) {
								this.showMessage(false, (String) null, currentName + " has logged in",
										MessageType.FRIEND_STATUS, 0, (String) null, (String) null);
							}

							if (SocialLists.friendListArgS[i] != null && !online) {
								this.showMessage(false, (String) null, currentName + " has logged out",
										MessageType.FRIEND_STATUS, 0, (String) null, (String) null);
							}

							SocialLists.friendList[i] = currentName;
							SocialLists.friendListOld[i] = formerName;
							SocialLists.friendListArgS[i] = var9;
							SocialLists.friendListArg[i] = arg;
							this.sortOnlineFriendsList();
							return;
						}
					}

					if (rename) {
						System.out.println("Error: friend display name change packet received, but old name \'" + formerName
								+ "\' is not on friend list");
						return;
					}

					SocialLists.friendList[SocialLists.friendListCount] = currentName;
					SocialLists.friendListOld[SocialLists.friendListCount] = formerName;
					SocialLists.friendListArgS[SocialLists.friendListCount] = var9;
					SocialLists.friendListArg[SocialLists.friendListCount] = arg;
					++SocialLists.friendListCount;
					this.sortOnlineFriendsList();

				} else if (opcode == 237) { // Ignore List Rename
					String arg0 = this.packetsIncoming.readString();
					String replace = this.packetsIncoming.readString();
					if (replace.length() == 0) {
						replace = arg0;
					}

					String arg1 = this.packetsIncoming.readString();
					String find = this.packetsIncoming.readString();
					if (find.length() == 0) {
						find = arg0;
					}

					boolean rename = this.packetsIncoming.getUnsignedByte() == 1;

					for (int j = 0; j < SocialLists.ignoreListCount; ++j) {
						if (rename) {
							if (SocialLists.ignoreList[j].equals(find)) {
								SocialLists.ignoreListArg0[j] = arg0;
								SocialLists.ignoreList[j] = replace;
								SocialLists.ignoreListArg1[j] = arg1;
								SocialLists.ignoreListOld[j] = find;
								return;
							}
						} else if (SocialLists.ignoreList[j].equals(replace)) {
							return;
						}
					}

					if (rename) {
						System.out.println("Error: ignore display name change packet received, but old name \'" + find
								+ "\' is not on ignore list");
						return;
					}

					SocialLists.ignoreListArg0[SocialLists.ignoreListCount] = arg0;
					SocialLists.ignoreList[SocialLists.ignoreListCount] = replace;
					SocialLists.ignoreListArg1[SocialLists.ignoreListCount] = arg1;
					SocialLists.ignoreListOld[SocialLists.ignoreListCount] = find;
					++SocialLists.ignoreListCount;

				} else if (opcode == 109) { // Ignore List ___ ?
					SocialLists.ignoreListCount = this.packetsIncoming.getUnsignedByte();

					for (int var4 = 0; var4 < SocialLists.ignoreListCount; ++var4) {
						SocialLists.ignoreListArg0[var4] = this.packetsIncoming.readString();
						SocialLists.ignoreList[var4] = this.packetsIncoming.readString();
						SocialLists.ignoreListArg1[var4] = this.packetsIncoming.readString();
						SocialLists.ignoreListOld[var4] = this.packetsIncoming.readString();
					}
				} else if (opcode == 158) { // Chat Blocking Settings
					this.settingsBlockChat = this.packetsIncoming.getUnsignedByte();
					this.settingsBlockPrivate = this.packetsIncoming.getUnsignedByte();
					this.settingsBlockTrade = this.packetsIncoming.getUnsignedByte();
					this.settingsBlockDuel = this.packetsIncoming.getUnsignedByte();

				} else if (opcode == 120) { // Recieve Private Message
					String sender = this.packetsIncoming.readString();
					String formerName = this.packetsIncoming.readString();
					int icon = this.packetsIncoming.getUnsignedByte();
					// long var17 = this.packetsIncoming.getLong(0);
					String message = RSBufferUtils.getEncryptedString(this.packetsIncoming);

					// for (int var10 = 0; var10 < 100; ++var10) {
					// if (~var17 == ~this.m_Zd[var10]) {
					// return;
					// }
					// }
					//
					// this.m_Zd[this.m_Ag] = var17;
					// this.m_Ag = (this.m_Ag + 1) % 100;
					this.showMessage(icon == 2, sender, message, MessageType.PRIVATE_RECIEVE, icon, formerName,
							(String) null);

				} else if (opcode == 87) { // Send Private Message
					String var13 = this.packetsIncoming.readString();
					String var14 = RSBufferUtils.getEncryptedString(this.packetsIncoming);
					this.showMessage(false, var13, var14, MessageType.PRIVATE_SEND, 0, var13, (String) null);
				}
			  /*
				 * else if (opcode == 189) { // ???
				 * this.packetsIncoming.curPointerPosition += 28; // if
				 * (this.packetsIncoming.isCRCValid()) { //
				 * GenUtil.seedRandomFile((RSBuffer) this.packetsIncoming, //
				 * this.packetsIncoming.curPointerPosition - 28); // } }
				 */

				else if (opcode == 19) { // Server Configs
					System.out.println("Got Configs!");
					Properties props = new Properties();
					String serverName = this.packetsIncoming.readString();
					props.setProperty("SERVER_NAME", serverName);
					int spawnAuctionNpcs = this.packetsIncoming.getUnsignedByte();
					props.setProperty("SPAWN_AUCTION_NPCS", spawnAuctionNpcs == 1 ? "true" : "false");
					int spawnIronManNpcs = this.packetsIncoming.getUnsignedByte();
					props.setProperty("SPAWN_IRON_MAN_NPCS", spawnIronManNpcs == 1 ? "true" : "false");
					int spawnSubscriptionNpcs = this.packetsIncoming.getUnsignedByte();
					props.setProperty("SPAWN_SUBSCRIPTION_NPCS", spawnSubscriptionNpcs == 1 ? "true" : "false");
					int showFloatingNametags = this.packetsIncoming.getUnsignedByte();
					props.setProperty("SHOW_FLOATING_NAMETAGS", showFloatingNametags == 1 ? "true" : "false");
					int wantClans = this.packetsIncoming.getUnsignedByte();
					props.setProperty("WANT_CLANS", wantClans == 1 ? "true" : "false");
					int wantKillFeed = this.packetsIncoming.getUnsignedByte();
					props.setProperty("WANT_KILL_FEED", wantKillFeed == 1 ? "true" : "false");
					int fogToggle = this.packetsIncoming.getUnsignedByte();
					props.setProperty("FOG_TOGGLE", fogToggle == 1 ? "true" : "false");
					int groundItemToggle = this.packetsIncoming.getUnsignedByte();
					props.setProperty("GROUND_ITEM_TOGGLE", groundItemToggle == 1 ? "true" : "false");
					int autoMessageSwitchToggle = this.packetsIncoming.getUnsignedByte();
					props.setProperty("AUTO_MESSAGE_SWITCH_TOGGLE", autoMessageSwitchToggle == 1 ? "true" : "false");

					Config.updateServerConfiguration(props);
				}
				else {
					 this.handlePacket2(opcode, length);
				 }
			} catch (RuntimeException var11) {
				throw GenUtil.makeThrowable(var11, "client.LD(" + "dummy" + ',' + length + ',' + opcode + ')');
			}
		}

		private final void handlePacket2(int opcode, int length) {
			try {//System.out.println("opcode1: " + opcode);
				label2111: {

				if (opcode == 191) { // Show Other Players
					this.knownPlayerCount = this.playerCount;

					for (int i = 0; this.knownPlayerCount > i; ++i) {
						this.knownPlayers[i] = this.players[i];
					}

					this.packetsIncoming.startBitAccess();
					this.playerLocalX = this.packetsIncoming.getBitMask(11);
					this.playerLocalZ = this.packetsIncoming.getBitMask(13);
					int direction = this.packetsIncoming.getBitMask(4);
					boolean var24 = this.loadNextRegion(this.playerLocalZ, this.playerLocalX, false);
					this.playerLocalX -= this.midRegionBaseX;
					this.playerLocalZ -= this.midRegionBaseZ;
					int var6 = 64 + this.playerLocalX * this.tileSize;
					int var7 = this.playerLocalZ * this.tileSize + 64;
					this.playerCount = 0;
					if (var24) {
						this.localPlayer.movingStep = 0;
						this.localPlayer.waypointCurrent = 0;
						this.localPlayer.currentX = this.localPlayer.waypointsX[0] = var6;
						this.localPlayer.currentZ = this.localPlayer.waypointsZ[0] = var7;
					}

					this.localPlayer = this.createPlayer(var7, this.m_Zc, var6, 1,
							RSCharacterDirection.lookup(direction));
					int dir = this.packetsIncoming.getBitMask(8);

					for (int var9 = 0; dir > var9; ++var9) {
						RSCharacter var34 = this.knownPlayers[var9 + 1];
						int needsUpdate = this.packetsIncoming.getBitMask(1);
						if (needsUpdate != 0) {
							int updateType = this.packetsIncoming.getBitMask(1);
							if (updateType != 0) {
								int needsNextSprite = this.packetsIncoming.getBitMask(2);
								if (needsNextSprite == 3) {
									continue;
								}
								var34.animationNext = this.packetsIncoming.getBitMask(2) + (needsNextSprite << 2);
							} else {
								int modelIndex = this.packetsIncoming.getBitMask(3);
								int var33 = var34.waypointCurrent;
								int var15 = var34.waypointsX[var33];
								int var16 = var34.waypointsZ[var33];
								if (modelIndex == 2 || modelIndex == 1 || modelIndex == 3) {
									var15 += this.tileSize;
								}

								if (modelIndex == 6 || modelIndex == 5 || modelIndex == 7) {
									var15 -= this.tileSize;
								}

								if (modelIndex == 4 || modelIndex == 3 || modelIndex == 5) {
									var16 += this.tileSize;
								}
								var34.animationNext = modelIndex;
								if (modelIndex == 0 || modelIndex == 1 || modelIndex == 7) {
									var16 -= this.tileSize;
								}
								var34.waypointCurrent = var33 = (1 + var33) % 10;
								var34.waypointsX[var33] = var15;
								var34.waypointsZ[var33] = var16;
							}
						}

						this.players[this.playerCount++] = var34;
					}

					while (length * 8 > this.packetsIncoming.getBitHead() - -24) {
						int var9 = this.packetsIncoming.getBitMask(11);
						int var10 = this.packetsIncoming.getBitMask(5);
						if (var10 > 15) {
							var10 -= 32;
						}

						int var11 = this.packetsIncoming.getBitMask(5);
						if (var11 > 15) {
							var11 -= 32;
						}

						direction = this.packetsIncoming.getBitMask(4);
						var7 = 64 + (var11 + this.playerLocalZ) * this.tileSize;
						var6 = (this.playerLocalX + var10) * this.tileSize + 64;
						this.createPlayer(var7, var9, var6, 1, RSCharacterDirection.lookup(direction));
					}

					this.packetsIncoming.endBitAccess();
					return;
				}

				if (opcode != 99) { // Not ???
					if (opcode == 48) { // Show Game Objects
						while (length > this.packetsIncoming.packetEnd) {
							if (this.packetsIncoming.getUnsignedByte() != 255) {
								--this.packetsIncoming.packetEnd;
								int id = this.packetsIncoming.getShort();
								int xTile = this.playerLocalX + this.packetsIncoming.getByte();
								int zTile = this.playerLocalZ + this.packetsIncoming.getByte();
								int dir = packetsIncoming.getByte();
								int count = 0;

								for (int i = 0; i < this.gameObjectInstanceCount; ++i) {
									if (this.gameObjectInstanceX[i] == xTile && zTile == this.gameObjectInstanceZ[i]) {
										this.scene.removeModel(this.gameObjectInstanceModel[i]);
										this.world.removeGameObject_CollisonFlags(this.gameObjectInstanceID[i],
												this.gameObjectInstanceX[i], this.gameObjectInstanceZ[i]);
									} else {
										if (count != i) {
											this.gameObjectInstanceModel[count] = this.gameObjectInstanceModel[i];
											this.gameObjectInstanceModel[count].key = count;
											this.gameObjectInstanceX[count] = this.gameObjectInstanceX[i];
											this.gameObjectInstanceZ[count] = this.gameObjectInstanceZ[i];
											this.gameObjectInstanceID[count] = this.gameObjectInstanceID[i];
											this.gameObjectInstanceDir[count] = this.gameObjectInstanceDir[i];
										}

										++count;
									}
								}

								this.gameObjectInstanceCount = count;

								world.registerObjectDir(xTile, zTile, dir);
								if (id != 60000) {
									int xSize, zSize;
									if (dir != 0 && dir != 4) {
										xSize = EntityHandler.getObjectDef(id).getHeight();// EntityHandler.getObjectDef(id).getHeight();
										zSize = EntityHandler.getObjectDef(id).getWidth();// EntityHandler.getObjectDef(id).getWidth();
									} else {
										zSize = EntityHandler.getObjectDef(id).getHeight();// EntityHandler.getObjectDef(id).getHeight();
										xSize = EntityHandler.getObjectDef(id).getWidth();// EntityHandler.getObjectDef(id).getWidth();
									}

									int xWorld = (xTile * 2 + xSize) * this.tileSize / 2;
									int zWorld = (zTile * 2 + zSize) * this.tileSize / 2;
									int modelIndex = EntityHandler.getObjectDef(id).modelID;// CacheValues.gameObjectModelIndex[id];
									RSModel m = this.modelCache[modelIndex].clone();
									this.scene.addModel(m);
									m.key = this.gameObjectInstanceCount;
									m.addRotation(0, dir * 32, 0);
									m.translate2(xWorld, -this.world.getElevation(xWorld, zWorld), zWorld);
									m.setDiffuseLightAndColor(-50, -10, -50, 48, 48, true, 117);
									this.world.addGameObject_UpdateCollisionMap(xTile, zTile, id, false);
									if (id == 74) {
										m.translate2(0, -480, 0);
									}

									this.gameObjectInstanceX[this.gameObjectInstanceCount] = xTile;
									this.gameObjectInstanceZ[this.gameObjectInstanceCount] = zTile;
									this.gameObjectInstanceID[this.gameObjectInstanceCount] = id;
									this.gameObjectInstanceDir[this.gameObjectInstanceCount] = dir;
									this.gameObjectInstanceModel[this.gameObjectInstanceCount++] = m;
								}
							} else {
								int id = 0;
								int xTile = this.playerLocalX + this.packetsIncoming.getByte() >> 3;
								int zTile = this.playerLocalZ + this.packetsIncoming.getByte() >> 3;

							for (int localIndex = 0; this.gameObjectInstanceCount > localIndex; ++localIndex) {
								int dxTile = (this.gameObjectInstanceX[localIndex] >> 3) - xTile;
								int dzTile = (this.gameObjectInstanceZ[localIndex] >> 3) - zTile;
								if (dxTile == 0 && dzTile == 0) {
									this.scene.removeModel(this.gameObjectInstanceModel[localIndex]);
									this.world.removeGameObject_CollisonFlags(this.gameObjectInstanceID[localIndex],
											this.gameObjectInstanceX[localIndex],
											this.gameObjectInstanceZ[localIndex]);
								} else {
									if (localIndex != id) {
										this.gameObjectInstanceModel[id] = this.gameObjectInstanceModel[localIndex];
										this.gameObjectInstanceModel[id].key = id;
										this.gameObjectInstanceX[id] = this.gameObjectInstanceX[localIndex];
										this.gameObjectInstanceZ[id] = this.gameObjectInstanceZ[localIndex];
										this.gameObjectInstanceID[id] = this.gameObjectInstanceID[localIndex];
										this.gameObjectInstanceDir[id] = this.gameObjectInstanceDir[localIndex];
									}
									++id;
								}
							}
							this.gameObjectInstanceCount = id;
							}
						}
						return;
					}

					if (opcode != 111) { // Not ???
						if (opcode == 53) { // Inventory items 
							// this.inventoryItemCount =
							// this.packetsIncoming.getUnsignedByte();
							// for (int i = 0; i < this.inventoryItemCount; ++i)
							// {
							// int var19 = this.packetsIncoming.getShort();
							// this.inventoryItemID[i] =
							// FastMath.bitwiseAnd(var19, 0x7FFF);
							// this.inventoryItemEquipped[i] = var19 >> 15;// /
							// // 0x8000;
							// if (EntityHandler.getItemDef(0x7FFF &
							// var19).isStackable()) {
							// this.inventoryItemSize[i] =
							// this.packetsIncoming.getSmart16_32();
							// } else {
							// this.inventoryItemSize[i] = 1;
							// }
							// }
							this.inventoryItemCount = this.packetsIncoming.getUnsignedByte();
							for (int i = 0; i < this.inventoryItemCount; ++i) {
								int itemID = this.packetsIncoming.getShort();
								this.inventoryItemID[i] = itemID;
								this.inventoryItemEquipped[i] = packetsIncoming.getByte();
								if (EntityHandler.getItemDef(itemID).isStackable()) {
									this.inventoryItemSize[i] = this.packetsIncoming.get32();
								} else {
									this.inventoryItemSize[i] = 1;
								}
							}
							return;
						}

						if (opcode != 234) { // Not ???
							if (opcode == 91) { // Show Walls
								while (true) {
									while (length > this.packetsIncoming.packetEnd) {
										if (this.packetsIncoming.getUnsignedByte() == 255) {
											int var4 = 0;
											int var19 = this.playerLocalX + this.packetsIncoming.getByte() >> 3;
											int var6 = this.playerLocalZ + this.packetsIncoming.getByte() >> 3;

											for (int var7 = 0; this.wallObjectInstanceCount > var7; ++var7) {
												int dir = (this.wallObjectInstanceX[var7] >> 3) - var19;
												int var9 = (this.wallObjectInstanceZ[var7] >> 3) - var6;
												if (dir == 0 && var9 == 0) {
													this.scene.removeModel(this.wallObjectInstanceModel[var7]);
													this.world.removeWallObject_CollisionFlags(true,
															this.wallObjectInstanceDir[var7],
															this.wallObjectInstanceZ[var7],
															this.wallObjectInstanceX[var7],
															this.wallObjectInstanceID[var7]);
												} else {
													if (var4 != var7) {
														this.wallObjectInstanceModel[var4] = this.wallObjectInstanceModel[var7];
														this.wallObjectInstanceModel[var4].key = var4 + 10000;
														this.wallObjectInstanceX[var4] = this.wallObjectInstanceX[var7];
														this.wallObjectInstanceZ[var4] = this.wallObjectInstanceZ[var7];
														this.wallObjectInstanceDir[var4] = this.wallObjectInstanceDir[var7];
														this.wallObjectInstanceID[var4] = this.wallObjectInstanceID[var7];
													}

													++var4;
												}
											}

											this.wallObjectInstanceCount = var4;
										} else {
											--this.packetsIncoming.packetEnd;
											int id = this.packetsIncoming.getShort();
											int x = this.playerLocalX + this.packetsIncoming.getByte();
											int y = this.playerLocalZ + this.packetsIncoming.getByte();
											byte direction = this.packetsIncoming.getByte();
											int localIndex = 0;

											for (int var9 = 0; var9 < this.wallObjectInstanceCount; ++var9) {
												if (this.wallObjectInstanceX[var9] == x
														&& this.wallObjectInstanceZ[var9] == y
														&& direction == this.wallObjectInstanceDir[var9]) {
													this.scene.removeModel(this.wallObjectInstanceModel[var9]);
													this.world.removeWallObject_CollisionFlags(true,
															this.wallObjectInstanceDir[var9],
															this.wallObjectInstanceZ[var9],
															this.wallObjectInstanceX[var9],
															this.wallObjectInstanceID[var9]);
												} else {
													if (var9 != localIndex) {
														this.wallObjectInstanceModel[localIndex] = this.wallObjectInstanceModel[var9];
														this.wallObjectInstanceModel[localIndex].key = localIndex
																+ 10000;
														this.wallObjectInstanceX[localIndex] = this.wallObjectInstanceX[var9];
														this.wallObjectInstanceZ[localIndex] = this.wallObjectInstanceZ[var9];
														this.wallObjectInstanceDir[localIndex] = this.wallObjectInstanceDir[var9];
														this.wallObjectInstanceID[localIndex] = this.wallObjectInstanceID[var9];
													}

													++localIndex;
												}
											}

											this.wallObjectInstanceCount = localIndex;
											if (id != 60000) {
												this.world.applyWallToCollisionFlags(id, x, y, direction);
												RSModel model = this.createWallObjectModel(x, y, id, direction,
														this.wallObjectInstanceCount);
												this.wallObjectInstanceModel[this.wallObjectInstanceCount] = model;
												this.wallObjectInstanceX[this.wallObjectInstanceCount] = x;
												this.wallObjectInstanceZ[this.wallObjectInstanceCount] = y;
												this.wallObjectInstanceID[this.wallObjectInstanceCount] = id;
												this.wallObjectInstanceDir[this.wallObjectInstanceCount++] = direction;
											}
										}
									}

									return;
								}
							}

							if (opcode == 79) { // Show NPCs
								this.npcCacheCount = this.npcCount;
								this.npcCount = 0;

								for (int i = 0; i < this.npcCacheCount; ++i) {
									this.npcsCache[i] = this.npcs[i];
								}

								this.packetsIncoming.startBitAccess();
								int count = this.packetsIncoming.getBitMask(8);

								int var10, var9, var11, dir, var19;
								for (var19 = 0; count > var19; ++var19) {
									RSCharacter var28 = this.npcsCache[var19];
									int var7 = this.packetsIncoming.getBitMask(1);
									if (var7 != 0) {
										dir = this.packetsIncoming.getBitMask(1);
										if (dir != 0) {
											int nextSpriteOffset = packetsIncoming.getBitMask(2);
											if (nextSpriteOffset == 3) {
												continue;
											}
											var28.animationNext = (nextSpriteOffset << 2)
													+ this.packetsIncoming.getBitMask(2);
										} else {
											var9 = this.packetsIncoming.getBitMask(3);
											var10 = var28.waypointCurrent;
											var11 = var28.waypointsX[var10];
											if (var9 == 2 || var9 == 1 || var9 == 3) {
												var11 += this.tileSize;
											}

											int var31 = var28.waypointsZ[var10];
											if (var9 == 6 || var9 == 5 || var9 == 7) {
												var11 -= this.tileSize;
											}

											if (var9 == 4 || var9 == 3 || var9 == 5) {
												var31 += this.tileSize;
											}

											if (var9 == 0 || var9 == 1 || var9 == 7) {
												var31 -= this.tileSize;
											}

											var28.waypointCurrent = var10 = (var10 - -1) % 10;
											var28.animationNext = var9;
											var28.waypointsX[var10] = var11;
											var28.waypointsZ[var10] = var31;
										}
									}

									this.npcs[this.npcCount++] = var28;
								}

								while (this.packetsIncoming.getBitHead() - -34 < length * 8) {
									var19 = this.packetsIncoming.getBitMask(12);
									int var6 = this.packetsIncoming.getBitMask(5);
									if (var6 > 15) {
										var6 -= 32;
									}

									int var7 = this.packetsIncoming.getBitMask(5);
									if (var7 > 15) {
										var7 -= 32;
									}

									dir = this.packetsIncoming.getBitMask(4);
									var9 = 64 + (var6 + this.playerLocalX) * this.tileSize;
									var10 = (var7 + this.playerLocalZ) * this.tileSize - -64;
									var11 = this.packetsIncoming.getBitMask(10);
									this.createNpc(dir, var11, var9, var10, var19);
								}

								this.packetsIncoming.endBitAccess();
								return;
							}

							if (opcode == 104) { // ???
								int var4 = this.packetsIncoming.getShort();

								for (int var19 = 0; var4 > var19; ++var19) {
									int var6 = this.packetsIncoming.getShort();
									RSCharacter var22 = this.npcsServer[var6];
									int updateType = this.packetsIncoming.getUnsignedByte();
									if (updateType == 1) {
										int var9 = this.packetsIncoming.getShort();
										if (var22 != null) {
											String message = packetsIncoming.readString();
											var22.messageTimeout = 150;
											var22.message = message;
											if (this.localPlayer.serverIndex == var9) {
												this.showMessage(false, (String) null,
														EntityHandler.getNpcDef(var22.npcId).getName() + ": "
																+ var22.message,
																MessageType.QUEST, 0, (String) null, "@yel@");
											}
										}
									} else if (updateType == 2) {
										int var9 = this.packetsIncoming.getUnsignedByte();
										int var10 = this.packetsIncoming.getUnsignedByte();
										int var11 = this.packetsIncoming.getUnsignedByte();

										if (null != var22) {
											var22.damageTaken = var9;
											var22.healthMax = var11;
											var22.combatTimeout = 200;
											var22.healthCurrent = var10;
										}
									}
								}
								return;
							}

							if (opcode == 245) { // Show Options Menu
								this.optionsMenuShow = true;
								int count = this.packetsIncoming.getUnsignedByte();
								this.optionsMenuCount = count;
								for (int i = 0; count > i; ++i) {
									this.optionsMenuText[i] = this.packetsIncoming.readString();
								}
								return;
							}

							if (opcode != 252) { // Not ???
								if (opcode == 25) { // Load Area
									this.loadingArea = true;
									this.m_Zc = this.packetsIncoming.getShort();
									this.worldOffsetX = this.packetsIncoming.getShort();
									this.worldOffsetZ = this.packetsIncoming.getShort();
									this.requestedPlane = this.packetsIncoming.getShort();
									this.m_rc = this.packetsIncoming.getShort();
									this.worldOffsetZ -= this.requestedPlane * this.m_rc;
									return;
								}

								if (opcode == 156) { // Load Stats and Experience
									for (int var4 = 0; var4 < 18; ++var4) {
										this.playerStatCurrent[var4] = this.packetsIncoming.getUnsignedByte();
									}

									for (int var4 = 0; var4 < 18; ++var4) {
										this.playerStatBase[var4] = this.packetsIncoming.getUnsignedByte();
									}

									for (int var4 = 0; var4 < 18; ++var4) {
										this.playerExperience[var4] = this.packetsIncoming.get32();
									}

									this.m_ii = this.packetsIncoming.getUnsignedByte();
									return;
								}

								if (opcode != 153) { // Not ???
									if (opcode == 83) { // Set Death Screen Timeout
										this.deathScreenTimeout = 250;
										return;
									}

									if (opcode == 211) { // Generate Object/Ground Items/Walls Counts
										int packets = (length - 1) / 4;
										for (int i = 0; packets > i; ++i) {
											int x = this.playerLocalX + this.packetsIncoming.get16_V2() >> 3;
											int z = this.playerLocalZ + this.packetsIncoming.get16_V2() >> 3;

											int count = 0;
											for (int j = 0; j < this.groundItemCount; ++j) {
												int var10 = (this.groundItemX[j] >> 3) - x;
												int var11 = (this.groundItemZ[j] >> 3) - z;
												if (var10 != 0 || var11 != 0) {
													if (count != j) {
														this.groundItemX[count] = this.groundItemX[j];
														this.groundItemZ[count] = this.groundItemZ[j];
														this.groundItemID[count] = this.groundItemID[j];
														this.m_Le[count] = this.m_Le[j];
													}

													++count;
												}
											}

											this.groundItemCount = count;
											count = 0;

											for (int j = 0; j < this.gameObjectInstanceCount; ++j) {
												int var10 = (this.gameObjectInstanceX[j] >> 3) - x;
												int var11 = (this.gameObjectInstanceZ[j] >> 3) - z;
												if (var10 == 0 && var11 == 0) {
													this.scene.removeModel(this.gameObjectInstanceModel[j]);
													this.world.removeGameObject_CollisonFlags(
														this.gameObjectInstanceID[j],
														(int) this.gameObjectInstanceX[j],
														(int) this.gameObjectInstanceZ[j]);
												} else {
													if (j != count) {
														this.gameObjectInstanceModel[count] = this.gameObjectInstanceModel[j];
														this.gameObjectInstanceModel[count].key = count;
														this.gameObjectInstanceX[count] = this.gameObjectInstanceX[j];
														this.gameObjectInstanceZ[count] = this.gameObjectInstanceZ[j];
														this.gameObjectInstanceID[count] = this.gameObjectInstanceID[j];
														this.gameObjectInstanceDir[count] = this.gameObjectInstanceDir[j];
													}

													++count;
												}
											}

											this.gameObjectInstanceCount = count;
											count = 0;

											for (int var9 = 0; this.wallObjectInstanceCount > var9; ++var9) {
												int var10 = (this.wallObjectInstanceX[var9] >> 3) - x;
												int var11 = (this.wallObjectInstanceZ[var9] >> 3) - z;
												if (var10 == 0 && var11 == 0) {
													this.scene.removeModel(this.wallObjectInstanceModel[var9]);
													this.world.removeWallObject_CollisionFlags(true,
														this.wallObjectInstanceDir[var9],
														this.wallObjectInstanceZ[var9],
														this.wallObjectInstanceX[var9],
														this.wallObjectInstanceID[var9]);
												} else {
													if (var9 != count) {
														this.wallObjectInstanceModel[count] = this.wallObjectInstanceModel[var9];
														this.wallObjectInstanceModel[count].key = count + 10000;
														this.wallObjectInstanceX[count] = this.wallObjectInstanceX[var9];
														this.wallObjectInstanceZ[count] = this.wallObjectInstanceZ[var9];
														this.wallObjectInstanceDir[count] = this.wallObjectInstanceDir[var9];
														this.wallObjectInstanceID[count] = this.wallObjectInstanceID[var9];
													}

													++count;
												}
											}

											this.wallObjectInstanceCount = count;
										}

										return;
									}

									if (opcode == 59) { // Appearance Change Menu
										this.showAppearanceChange = true;
										return;
									}

									if (opcode == 92) { // Trade Request
										int serverIndex = this.packetsIncoming.getShort();
										if (this.playerServer[serverIndex] != null) {
											this.tradeRecipientName = this.playerServer[serverIndex].displayName;
										}

										this.showDialogTrade = true;
										this.tradeRecipientItemsCount = 0;
										this.tradeItemCount = 0;
										this.tradeAccepted = false;
										this.tradeRecipientAccepted = false;
										return;
									}

									if (opcode == 128) { // Trade Confirm Menu
										this.showDialogTradeConfirm = false;
										this.showDialogTrade = false;
										return;
									}

									if (opcode != 97) { // Not ???
										if (opcode == 162) { // Trade Accept or Decline (Other)
											int accepted = this.packetsIncoming.getUnsignedByte();
											if (accepted != 1) {
												this.tradeRecipientAccepted = false;
											} else {
												this.tradeRecipientAccepted = true;
											}

											return;
										}

										if (opcode != 101) { // Not ???
											if (opcode == 137) { // Close Shop Dialog
												this.showDialogShop = false;
												return;
											}

											if (opcode == 15) { // Trade Accept or Decline (Self)
												byte accepted = this.packetsIncoming.getByte();
												if (accepted != 1) {
													this.tradeAccepted = false;
												} else {
													this.tradeAccepted = true;
												}

												return;
											}

											if (opcode == 240) { // Server Settings
												this.adminRights = this.packetsIncoming.getUnsignedByte() == 1;
												this.optionCameraModeAuto = this.packetsIncoming.getUnsignedByte() == 1;
												this.optionMouseButtonOne = this.packetsIncoming.getUnsignedByte() == 1;
												this.optionSoundDisabled = this.packetsIncoming.getUnsignedByte() == 1;
												this.combatStyle = this.packetsIncoming.getUnsignedByte();
												this.settingsBlockGlobal = packetsIncoming.getUnsignedByte();
												this.clanInviteBlockSetting = this.packetsIncoming.getUnsignedByte() == 1;
												return;
											}

											if (opcode == 206) { // Prayer Toggle
												for (int i = 0; length - 1 > i; ++i) {
													boolean enabled = this.packetsIncoming.getByte() == 1;
													if (!this.prayerOn[i] && enabled) {
														this.playSoundFile((String) "prayeron");
													}
													if (this.prayerOn[i] && !enabled) {
														this.playSoundFile((String) "prayeroff");
													}

													this.prayerOn[i] = enabled;
												}

												return;
											}

											if (opcode == 5) { // Quest Stage Update
												int updateQuestType = this.packetsIncoming.getByte();
												if (updateQuestType == 0) {
													int questCount = this.packetsIncoming.getByte();
													for (int i = 0; i < questCount; i++) {
														int questId = this.packetsIncoming.get32();
														int questStage = this.packetsIncoming.get32();

														String questName = this.packetsIncoming.readString();

														questNames[questId] = questName;
														questStages[questId] = questStage;
													}
												} else if (updateQuestType == 1) {
													int questID = this.packetsIncoming.get32();
													int stage = this.packetsIncoming.get32();

													questStages[questID] = stage;
												}
												return;
											}

											if (opcode == 42) { // Show Bank
												this.setShowDialogBank(true);
												this.newBankItemCount = this.packetsIncoming.getShort();
												this.bankItemsMax = this.packetsIncoming.getShort();
												bank.resetBank();
												for (int var4 = 0; var4 < this.newBankItemCount; ++var4) {
													//this.newBankItems[var4] = this.packetsIncoming.getShort();
													//this.newBankItemsCount[var4] = this.packetsIncoming.get32();
													bank.addBank(var4, this.packetsIncoming.getShort(), this.packetsIncoming.get32());
												}

												//this.updateBankItems(108);
												return;
											}

											if (opcode != 203) { // Not ???
												if (opcode == 33) { // Update Experience
													int skill = this.packetsIncoming.getUnsignedByte();
													this.playerExperience[skill] = this.packetsIncoming.get32();
													return;
												}

												if (opcode != 176) { // Not ???
													if (opcode == 225) { // Show Duel Dialog
														this.showDialogDuel = false;
														this.showDialogDuelConfirm = false;
														return;
													}

													if (opcode == 20) { // Confirm Trade
														this.showDialogTrade = false;
														this.showDialogTradeConfirm = true;
														this.tradeConfirmAccepted = false;
														this.tradeRecipientConfirmName = this.packetsIncoming
																.readString();
														this.tradeRecipientConfirmItemsCount = this.packetsIncoming
																.getUnsignedByte();

														for (int var4 = 0; this.tradeRecipientConfirmItemsCount > var4; ++var4) {
															this.tradeRecipientConfirmItems[var4] = this.packetsIncoming
																	.getShort();
															this.tradeRecipientConfirmItemCount[var4] = this.packetsIncoming
																	.get32();
														}

														this.tradeConfirmItemsCount = this.packetsIncoming
																.getUnsignedByte();

														for (int var4 = 0; var4 < this.tradeConfirmItemsCount; ++var4) {
															this.tradeConfirmItems[var4] = this.packetsIncoming
																	.getShort();
															this.tradeConfirmItemsCount1[var4] = this.packetsIncoming
																	.get32();
														}

														return;
													}

													if (opcode == 6) { // Show Duel Items
														this.duelOffsetOpponentItemCount = this.packetsIncoming
																.getUnsignedByte();

														for (int var4 = 0; this.duelOffsetOpponentItemCount > var4; ++var4) {
															this.duelOpponentItemId[var4] = this.packetsIncoming
																	.getShort();
															this.duelOpponentItemCount[var4] = this.packetsIncoming
																	.get32();
														}

														this.duelOfferAccepted = false;
														this.duelOffsetOpponentAccepted = false;
														return;
													}

													if (opcode == 30) { // Toggle Duel Setting
														if (this.packetsIncoming.getUnsignedByte() == 1) {
															this.duelSettingsRetreat = true;
														} else {
															this.duelSettingsRetreat = false;
														}

														if (this.packetsIncoming.getUnsignedByte() != 1) {
															this.duelSettingsMagic = false;
														} else {
															this.duelSettingsMagic = true;
														}

														if (this.packetsIncoming.getUnsignedByte() != 1) {
															this.duelSettingsPrayer = false;
														} else {
															this.duelSettingsPrayer = true;
														}

														if (this.packetsIncoming.getUnsignedByte() != 1) {
															this.duelSettingsWeapons = false;
														} else {
															this.duelSettingsWeapons = true;
														}

														this.duelOfferAccepted = false;
														this.duelOffsetOpponentAccepted = false;
														return;
													}

													if (opcode == 249) { // Update Bank
														int slot = this.packetsIncoming.getUnsignedByte();
														int item = this.packetsIncoming.getShort();
														int itemCount = this.packetsIncoming.get32();

														/*if (itemCount == 0) {
															--this.newBankItemCount;

															for (int var7 = slot; this.newBankItemCount > var7; ++var7) {
																this.newBankItems[var7] = this.newBankItems[1 + var7];
																this.newBankItemsCount[var7] = this.newBankItemsCount[var7
																	                                                      + 1];
															}
														} else {
															this.newBankItems[slot] = item;
															this.newBankItemsCount[slot] = itemCount;
															if (this.newBankItemCount <= slot) {
																this.newBankItemCount = slot + 1;
															}
														}*/

														//this.updateBankItems(-103);
														bank.updateBank(slot, item, itemCount);
														return;
													}

													if (opcode == 90) { // Update Inventory
														int slot = this.packetsIncoming.getUnsignedByte();
														int itemID = this.packetsIncoming.getShort();
														int stackSize = 1;
														if (EntityHandler.getItemDef(itemID & 32767).isStackable()) {
															stackSize = this.packetsIncoming.get32();
														}
														this.inventoryItemID[slot] = FastMath.bitwiseAnd(itemID, 32767);
														this.inventoryItemEquipped[slot] = itemID / '\u8000';
														this.inventoryItemSize[slot] = stackSize;
														if (slot >= this.inventoryItemCount) {
															this.inventoryItemCount = 1 + slot;
														}

														return;
													}

													if (opcode != 123) { // Not ???
														if (opcode == 159) { // Show Experience Notification
															int skill = this.packetsIncoming.getUnsignedByte();
															recentSkill = skill;
															int oldXp = playerExperience[skill];
															int oldLvl = playerStatBase[skill];
															this.playerStatCurrent[skill] = this.packetsIncoming.getUnsignedByte();
															this.playerStatBase[skill] = this.packetsIncoming.getUnsignedByte();
															this.playerExperience[skill] = this.packetsIncoming.get32();

															int receivedXp = playerExperience[skill] - oldXp;
															receivedXp = receivedXp < 0 ? 0 : receivedXp;
															this.playerStatXpGained[skill] += receivedXp;
															if (this.xpGainedStartTime[skill] == 0) {
																this.xpGainedStartTime[skill] = System.currentTimeMillis();
															}
															this.playerXpGainedTotal += receivedXp;
															if (this.totalXpGainedStartTime == 0) {
																this.totalXpGainedStartTime = System.currentTimeMillis();
															}

															if(Config.EXPERIENCE_DROPS) {
																if (receivedXp > 0) {
																	xpNotifications.add(new XPNotification(skill, receivedXp, false));
																}
																if (oldLvl < this.playerStatBase[skill]) {
																	xpNotifications.add(new XPNotification(skill, 1, true));
																}
															}
															return;
														}

														if (opcode != 253) { // Not ???
															if (opcode == 210) { // Duel Accept / Decline
																byte accepted = this.packetsIncoming.getByte();
																if (accepted != 1) {
																	this.duelOfferAccepted = false;
																} else {
																	this.duelOfferAccepted = true;
																}

																return;
															}

															if (opcode == 172) { // Show Duel Confirm Dialog
																this.duelConfirmed = false;
																this.showDialogDuelConfirm = true;
																this.showDialogDuel = false;
																this.duelOpponentName = this.packetsIncoming
																		.readString();
																this.duelOpponentItemsCount = this.packetsIncoming
																		.getUnsignedByte();
																for (int var4 = 0; var4 < this.duelOpponentItemsCount; ++var4) {
																	this.duelOpponentItems[var4] = this.packetsIncoming
																			.getShort();
																	this.duelOpponentItemCounts[var4] = this.packetsIncoming
																			.get32();
																}

																this.duelItemsCount = this.packetsIncoming
																		.getUnsignedByte();

																for (int var4 = 0; this.duelItemsCount > var4; ++var4) {
																	this.duelItems[var4] = this.packetsIncoming
																			.getShort();
																	this.duelItemCounts[var4] = this.packetsIncoming
																			.get32();
																}

																this.duelOptionRetreat = this.packetsIncoming
																		.getUnsignedByte();
																this.duelOptionMagic = this.packetsIncoming
																		.getUnsignedByte();
																this.duelOptionPrayer = this.packetsIncoming
																		.getUnsignedByte();
																this.duelOptionWeapons = this.packetsIncoming
																		.getUnsignedByte();
																return;
															}

															if (opcode == 204) { // Play Sound
																String filename = this.packetsIncoming.readString();
																this.playSoundFile((String) filename);
																return;
															}

															if (opcode != 36) { // Not ???
																if (opcode == 182) { // Show Log In Dialog
																	if (!this.welcomeScreenShown) {
																		this.welcomeLastLoggedInIp = this.packetsIncoming
																				.readString();
																		this.welcomeLastLoggedInDays = this.packetsIncoming
																				.getShort();
																		this.welcomeSubscriptionDaysLeft = this.packetsIncoming
																				.getShort();
																		this.welcomePremiumDaysLeft = this.packetsIncoming
																				.getShort();
																		this.welcomeUnreadMessages = this.packetsIncoming
																				.getShort();
																		this.showDialogMessage = true;
																		this.welcomeLastLoggedInHost = null;
																		this.welcomeScreenShown = true;
																	}

																	return;
																}

																if (opcode != 89) { // Not ???
																	if (opcode == 222) { // Show Server Message Dialog
																		this.serverMessage = this.packetsIncoming
																				.readString();
																		this.showDialogServerMessage = true;
																		this.serverMessageBoxTop = true;
																		return;
																	}

																	if (opcode != 114) { // Not ???
																		if (opcode == 117) { // Show Sleep Screen
																			if (!this.isSleeping) {
																				this.fatigueSleeping = this.statFatigue;
																			}

																			this.inputTextCurrent = "";
																			this.isSleeping = true;
																			this.inputTextFinal = "";
																			Sprite sprite = clientPort
																					.getSpriteFromByteArray(
																							new ByteArrayInputStream(
																									packetsIncoming.dataBuffer,
																									1, length));

																			this.surface.createCaptchaSprite(
																					mudclient.spriteLogo + 2, sprite);

																			this.sleepingStatusText = null;
																			return;
																		}

																		if (opcode != 244) { // Not ???
																			if (opcode == 84) { // Not Sleeping
																				this.isSleeping = false;
																				return;
																			}

																			if (opcode == 194) { // Wrong Sleep Word
																				this.sleepingStatusText = "Incorrect - Please wait...";
																				return;
																			}

																			if (opcode == 52) { // System Update Timer
																				this.systemUpdate = this.packetsIncoming
																						.getShort() * 32;
																				return;
																			}

																			if (opcode == 54) { // Elixir Timer
																				this.elixirTimer = this.packetsIncoming
																						.getShort() * 32;
																				return;
																			}

																			if (opcode != 213) { // Random Break Clause..
																				break label2111;
																			}

																			return;
																		}

																		this.fatigueSleeping = this.packetsIncoming
																				.getShort();
																		return;
																	}

																	this.statFatigue = this.packetsIncoming.getShort();
																	return;
																}

																this.serverMessage = this.packetsIncoming.readString();
																this.showDialogServerMessage = true;
																this.serverMessageBoxTop = false;
																return;
															}

															if (this.teleportBubbleCount < 50) {
																int type = this.packetsIncoming.getUnsignedByte();
																int x = this.packetsIncoming.getByte()
																		+ this.playerLocalX;
																int z = this.packetsIncoming.getByte()
																		+ this.playerLocalZ;
																this.teleportBubbleType[this.teleportBubbleCount] = type;
																this.teleportBubbleTime[this.teleportBubbleCount] = 0;
																this.teleportBubbleX[this.teleportBubbleCount] = x;
																this.teleportBubbleZ[this.teleportBubbleCount] = z;
																++this.teleportBubbleCount;
															}

															return;
														}

														byte accepted = this.packetsIncoming.getByte();
														if (accepted != 1) {
															this.duelOffsetOpponentAccepted = false;
														} else {
															this.duelOffsetOpponentAccepted = true;
														}

														return;
													}

													int slot = this.packetsIncoming.getUnsignedByte();
													--this.inventoryItemCount;

													for (int index = slot; this.inventoryItemCount > index; ++index) {
														this.inventoryItemID[index] = this.inventoryItemID[index + 1];
														this.inventoryItemSize[index] = this.inventoryItemSize[index
														                                                       + 1];
														this.inventoryItemEquipped[index] = this.inventoryItemEquipped[index
														                                                               + 1];
													}

													return;
												}

												int var4 = this.packetsIncoming.getShort();
												if (null != this.playerServer[var4]) {
													this.duelConfirmOpponentName = this.playerServer[var4].displayName;
												}

												this.duelOfferAccepted = false;
												this.duelSettingsPrayer = false;
												this.duelOffsetOpponentAccepted = false;
												this.duelSettingsWeapons = false;
												this.duelSettingsRetreat = false;
												this.showDialogDuel = true;
												this.duelSettingsMagic = false;
												this.duelOffsetOpponentItemCount = 0;
												this.duelOfferItemCount = 0;
												return;
											}

											this.setShowDialogBank(false);
											return;
										}

										this.showDialogShop = true;
										int shopItemCount = this.packetsIncoming.getUnsignedByte();
										byte shopType = this.packetsIncoming.getByte();
										this.shopSellPriceMod = this.packetsIncoming.getUnsignedByte();
										this.shopBuyPriceMod = this.packetsIncoming.getUnsignedByte();
										this.shopPriceMultiplier = this.packetsIncoming.getUnsignedByte();

										for (int i = 0; i < 40; ++i) {
											this.shopItemID[i] = -1;
										}

										for (int i = 0; shopItemCount > i; ++i) {
											this.shopItemID[i] = this.packetsIncoming.getShort();
											this.shopItemCount[i] = this.packetsIncoming.getShort();
											this.shopItemPrice[i] = this.packetsIncoming.getShort();
										}

										if (shopType == 1) {
											int var6 = 39;

											for (int inventoryIndex = 0; inventoryIndex < this.inventoryItemCount
													&& shopItemCount <= var6; ++inventoryIndex) {
												boolean var25 = false;

												for (int var9 = 0; var9 < 40; ++var9) {
													if (this.inventoryItemID[inventoryIndex] == this.shopItemID[var9]) {
														var25 = true;
														break;
													}
												}

												if (this.inventoryItemID[inventoryIndex] == 10) {
													var25 = true;
												}

												if (!var25) {
													this.shopItemID[var6] = FastMath.bitwiseAnd(32767,
															this.inventoryItemID[inventoryIndex]);
													this.shopItemCount[var6] = 0;
													this.shopItemPrice[var6] = 0;
													--var6;
												}
											}
										}

										if (this.shopSelectedItemIndex >= 0 && 40 > this.shopSelectedItemIndex
												&& this.shopSelectedItemType != this.shopItemID[this.shopSelectedItemIndex]) {
											this.shopSelectedItemIndex = -1;
											this.shopSelectedItemType = -2;
										}

										return;
									}

									this.tradeRecipientItemsCount = this.packetsIncoming.getUnsignedByte();

									for (int var4 = 0; var4 < this.tradeRecipientItemsCount; ++var4) {
										this.tradeRecipientItem[var4] = this.packetsIncoming.getShort();
										this.tradeRecipientItemCount[var4] = this.packetsIncoming.get32();
									}

									this.tradeRecipientAccepted = false;
									this.tradeAccepted = false;
									return;
								}

								for (int eq = 0; eq < 5; ++eq) {
									this.playerStatEquipment[eq] = this.packetsIncoming.getUnsignedByte();
								}

								return;
							}

							this.optionsMenuShow = false;
							return;
						}

						int playerCount = this.packetsIncoming.getShort();

						for (int pp = 0; playerCount > pp; ++pp) {
							int playerServerIndex = this.packetsIncoming.getShort();
							RSCharacter player = this.playerServer[playerServerIndex];
							byte updateType = this.packetsIncoming.getByte();
							if (updateType == 0) {
								int itemType = this.packetsIncoming.getShort();
								if (null != player) {
									player.bubbleTimeout = 150;
									player.bubbleItem = itemType;
								}
							} else if (updateType == 1) {
								if (null != player) {
									int crownID = this.packetsIncoming.getUnsignedByte();
									String message = packetsIncoming.readString();
									boolean var29 = false;
									String displayName = StringUtil.displayNameToKey(player.accountName);
									if (null != displayName) {
										for (int modelIndex = 0; modelIndex < SocialLists.ignoreListCount; ++modelIndex) {
											if (displayName.equals(
													StringUtil.displayNameToKey(SocialLists.ignoreList[modelIndex]))) {
												var29 = true;
												break;
											}
										}
									}

									if (!var29) {
										player.messageTimeout = 150;
										player.message = message;
										this.showMessage(crownID == 2, (player.clanTag != null ? "@whi@[@cla@" + player.clanTag + "@whi@]@yel@ " + player.displayName : player.displayName), player.message,
												MessageType.CHAT, crownID, player.accountName, (String) null);
									}
								}
							} else if (updateType == 2) {
								int damage = this.packetsIncoming.getUnsignedByte();
								int curhp = this.packetsIncoming.getUnsignedByte();
								int maxhp = this.packetsIncoming.getUnsignedByte();
								if (player != null) {
									player.healthMax = maxhp;
									player.healthCurrent = curhp;
									player.damageTaken = damage;
									if (this.localPlayer == player) {
										this.playerStatCurrent[3] = curhp;
										this.playerStatBase[3] = maxhp;
										this.showDialogServerMessage = false;
										this.showDialogMessage = false;
									}
									player.combatTimeout = 200;
								}
							} else if (updateType == 3) {
								int sprite = this.packetsIncoming.getShort();
								int shooterServerIndex = this.packetsIncoming.getShort();
								if (null != player) {
									player.attackingNpcServerIndex = shooterServerIndex;
									player.projectileRange = this.projectileMaxRange;
									player.attackingPlayerServerIndex = -1;
									player.incomingProjectileSprite = sprite;
								}
							} else if (updateType == 4) {
								int sprite = this.packetsIncoming.getShort();
								int shooterServerIndex = this.packetsIncoming.getShort();
								if (player != null) {
									player.projectileRange = this.projectileMaxRange;
									player.attackingNpcServerIndex = -1;
									player.attackingPlayerServerIndex = shooterServerIndex;
									player.incomingProjectileSprite = sprite;
								}
							}
							if (updateType == 6 && player != null) {
								String message = packetsIncoming.readString();

								player.message = message;
								player.messageTimeout = 150;
								if (this.localPlayer == player) {
									this.showMessage(false, (player.clanTag != null ? "@whi@[@cla@" + player.clanTag + "@whi@]@whi@ " + player.displayName : player.displayName), player.message, MessageType.QUEST, 0,
											player.accountName, (String) null);
								}
							} else if (updateType == 5) {
								if (player == null) {
									this.packetsIncoming.getShort();
									this.packetsIncoming.readString();
									this.packetsIncoming.readString();
									int vtmp = this.packetsIncoming.getUnsignedByte();
									this.packetsIncoming.packetEnd += 6 + vtmp;
								} else {
									this.packetsIncoming.getShort();
									player.displayName = this.packetsIncoming.readString();
									player.accountName = this.packetsIncoming.readString();
									int itemCount = this.packetsIncoming.getUnsignedByte();

									for (int i = 0; i < itemCount; ++i) {
										player.layerAnimation[i] = this.packetsIncoming.getShort();
									}

									for (int i = itemCount; i < 12; ++i) {
										player.layerAnimation[i] = 0;
									}

									player.colourHair = this.packetsIncoming.getUnsignedByte();
									player.colourTop = this.packetsIncoming.getUnsignedByte();
									player.colourBottom = this.packetsIncoming.getUnsignedByte();
									player.colourSkin = this.packetsIncoming.getUnsignedByte();
									player.level = this.packetsIncoming.getUnsignedByte();
									player.skullVisible = this.packetsIncoming.getUnsignedByte();
									if (packetsIncoming.getByte() == 1) {
										player.clanTag = this.packetsIncoming.readString();
									} else {
										player.clanTag = null;
									}
								}
							}
						}

						return;
					}

					this.insideTutorial = this.packetsIncoming.getUnsignedByte() != 0;
					return;
				}

				while (true) {
					while (true) {
						while (length > this.packetsIncoming.packetEnd) {
							if (this.packetsIncoming.getUnsignedByte() != 255) {
								--this.packetsIncoming.packetEnd;
								int groundItemID = this.packetsIncoming.getShort();
								int var19 = this.playerLocalX + this.packetsIncoming.getByte();
								int var6 = this.playerLocalZ + this.packetsIncoming.getByte();
								if ((groundItemID & 32768) != 0) {
									groundItemID &= 32767;
									int var7 = 0;

									for (int dir = 0; dir < this.groundItemCount; ++dir) {
										if (this.groundItemX[dir] == var19 && this.groundItemZ[dir] == var6
												&& this.groundItemID[dir] == groundItemID) {
											groundItemID = -123;
										} else {
											if (var7 != dir) {
												this.groundItemX[var7] = this.groundItemX[dir];
												this.groundItemZ[var7] = this.groundItemZ[dir];
												this.groundItemID[var7] = this.groundItemID[dir];
												this.m_Le[var7] = this.m_Le[dir];
											}

											++var7;
										}
									}

									this.groundItemCount = var7;

								} else {
									this.groundItemX[this.groundItemCount] = var19;
									this.groundItemZ[this.groundItemCount] = var6;
									this.groundItemID[this.groundItemCount] = groundItemID;
									this.m_Le[this.groundItemCount] = 0;

									for (int var7 = 0; this.gameObjectInstanceCount > var7; ++var7) {
										if (this.gameObjectInstanceX[var7] == var19
												&& this.gameObjectInstanceZ[var7] == var6) {
											this.m_Le[this.groundItemCount] = EntityHandler
													.getObjectDef(this.gameObjectInstanceID[var7]).getGroundItemVar();
											break;
										}
									}

									++this.groundItemCount;

								}
							} else {
								int var4 = 0;
								int offsetX = this.playerLocalX + this.packetsIncoming.getByte() >> 3;
								int offsetY = this.playerLocalZ + this.packetsIncoming.getByte() >> 3;

								for (int index = 0; this.groundItemCount > index; ++index) {
									int tileX = (this.groundItemX[index] >> 3) - offsetX;
									int tileY = (this.groundItemZ[index] >> 3) - offsetY;
									if (tileX != 0 || tileY != 0) {
										if (var4 != index) {
											this.groundItemX[var4] = this.groundItemX[index];
											this.groundItemZ[var4] = this.groundItemZ[index];
											this.groundItemID[var4] = this.groundItemID[index];
											this.m_Le[var4] = this.m_Le[index];
										}
										++var4;
									}
								}

								this.groundItemCount = var4;

							}
						}

						return;
					}
				}
			}
		} catch (

					RuntimeException var17)

			{
				String var5 = "T2 - " + opcode + " - " + length + " rx:" + this.playerLocalX + " ry:" + this.playerLocalZ
						+ " num3l:" + this.gameObjectInstanceCount + " - ";

				for (int var6 = 0; length > var6 && var6 < 50; ++var6) {
					var5 = var5 + this.packetsIncoming.dataBuffer[var6] + ",";
				}
				var17.printStackTrace();
				this.closeConnection(true);
				return;
			}
			this.closeConnection(true);

		}

		private final void handleReportAbuseClick() {
			try {
				this.reportAbuse_AbuseType = 0;

				boolean var2 = true;
				if (this.mouseX >= 36 && this.mouseX < 176) {
					this.reportAbuse_AbuseType = 1;
				} else if (this.mouseX >= 186 && this.mouseX < 326) {
					this.reportAbuse_AbuseType = 7;
				} else if (this.mouseX >= 336 && this.mouseX < 476) {
					this.reportAbuse_AbuseType = 12;
				} else {
					var2 = false;
				}

				int var3 = 156;
				int color;
				if (var2) {
					var2 = false;

					for (color = 0; color < 6; ++color) {
						int var5 = color == 0 ? 30 : 18;
						if (this.mouseY > var3 - 12 && this.mouseY < var5 + var3 - 12) {
							if (this.reportAbuse_AbuseType == 1) {
								var2 = true;
								this.reportAbuse_AbuseType += color;
								break;
							}

							if (this.reportAbuse_AbuseType == 7) {
								if (color < 5) {
									var2 = true;
									this.reportAbuse_AbuseType += color;
								}
								break;
							}

							if (this.reportAbuse_AbuseType == 12) {
								if (color < 3) {
									var2 = true;
									this.reportAbuse_AbuseType += color;
								}
								break;
							}
						}

						var3 += 2 + var5;
					}
				}

				if (!var2) {
					this.reportAbuse_AbuseType = 0;
				}

				if (this.mouseButtonClick != 0 && this.reportAbuse_AbuseType != 0) {
					this.getClientStream().newPacket(206);
					this.getClientStream().writeBuffer1.putString(this.reportAbuse_Name);
					this.getClientStream().writeBuffer1.putByte(this.reportAbuse_AbuseType);
					this.getClientStream().writeBuffer1.putByte(this.m_ue ? 1 : 0);
					this.getClientStream().finishPacket();
					this.reportAbuse_State = 0;
					this.inputTextFinal = "";
					this.inputTextCurrent = "";
					this.mouseButtonClick = 0;
				} else {
					var3 += 15;
					if (this.mouseButtonClick != 0) {
						this.mouseButtonClick = 0;
						if (this.mouseX < 31 || this.mouseY < 35 || this.mouseX > 481 || this.mouseY > 310) {
							this.reportAbuse_State = 0;
							return;
						}

						if (this.mouseX > 66 && this.mouseX < 446 && this.mouseY >= var3 - 15 && this.mouseY < var3 + 5) {
							this.reportAbuse_State = 0;
							return;
						}
					}

					this.getSurface().drawBox(31, 35, 450, 275, 0);
					this.getSurface().drawBoxBorder(31, 450, 35, 275, 16777215);
					byte var7 = 50;
					this.getSurface().drawColoredStringCentered(256,
							"This form is for reporting players who are breaking our rules", 16777215, 0, 1, var7);
					var3 = var7 + 15;
					this.getSurface().drawColoredStringCentered(256,
							"Using it sends a snapshot of the last 60 seconds of activity to us", 16777215, 0, 1, var3);
					var3 += 15;
					this.getSurface().drawColoredStringCentered(256, "If you misuse this form, you will be banned.",
							16744448, 0, 1, var3);
					var3 += 15;
					var3 += 10;
					this.getSurface().drawColoredStringCentered(256,
							"Click on the most suitable option from the Rules of RuneScape.", 16776960, 0, 1, var3);
					var3 += 15;
					this.getSurface().drawColoredStringCentered(256,
							"This will send a report to our Player Support team for investigation.", 16776960, 0, 1, var3);
					var3 += 18;
					this.getSurface().drawColoredStringCentered(106, "Honour", 16711680, 0, 4, var3);
					this.getSurface().drawColoredStringCentered(256, "Respect", 16711680, 0, 4, var3);
					this.getSurface().drawColoredStringCentered(406, "Security", 16711680, 0, 4, var3);
					var3 += 18;
					if (this.reportAbuse_AbuseType == 1) {
						this.getSurface().drawBox(36, var3 - 12, 140, 30, 3158064);
					}

					this.getSurface().drawBoxBorder(36, 140, var3 - 12, 30, 4210752);
					if (this.reportAbuse_AbuseType == 7) {
						this.getSurface().drawBox(186, var3 - 12, 140, 30, 3158064);
					}

					this.getSurface().drawBoxBorder(186, 140, var3 - 12, 30, 4210752);
					if (this.reportAbuse_AbuseType == 12) {
						this.getSurface().drawBox(336, var3 - 12, 140, 30, 3158064);
					}

					this.getSurface().drawBoxBorder(336, 140, var3 - 12, 30, 4210752);
					if (this.reportAbuse_AbuseType == 1) {
						color = 16744448;
					} else {
						color = 16777215;
					}

					this.getSurface().drawColoredStringCentered(106, "Buying or", color, 0, 0, var3);
					if (this.reportAbuse_AbuseType == 7) {
						color = 16744448;
					} else {
						color = 16777215;
					}

					this.getSurface().drawColoredStringCentered(256, "Seriously offensive", color, 0, 0, var3);
					if (this.reportAbuse_AbuseType != 12) {
						color = 16777215;
					} else {
						color = 16744448;
					}

					this.getSurface().drawColoredStringCentered(406, "Asking for or providing", color, 0, 0, var3);
					var3 += 12;
					if (this.reportAbuse_AbuseType != 1) {
						color = 16777215;
					} else {
						color = 16744448;
					}

					this.getSurface().drawColoredStringCentered(106, "selling an account", color, 0, 0, var3);
					if (this.reportAbuse_AbuseType != 7) {
						color = 16777215;
					} else {
						color = 16744448;
					}

					this.getSurface().drawColoredStringCentered(256, "language", color, 0, 0, var3);
					if (this.reportAbuse_AbuseType != 12) {
						color = 16777215;
					} else {
						color = 16744448;
					}

					this.getSurface().drawColoredStringCentered(406, "contact information", color, 0, 0, var3);
					var3 += 20;
					if (this.reportAbuse_AbuseType == 2) {
						this.getSurface().drawBox(36, var3 - 12, 140, 18, 3158064);
					}

					this.getSurface().drawBoxBorder(36, 140, var3 - 12, 18, 4210752);
					if (this.reportAbuse_AbuseType == 8) {
						this.getSurface().drawBox(186, var3 - 12, 140, 18, 3158064);
					}

					this.getSurface().drawBoxBorder(186, 140, var3 - 12, 18, 4210752);
					if (this.reportAbuse_AbuseType == 13) {
						this.getSurface().drawBox(336, var3 - 12, 140, 18, 3158064);
					}

					this.getSurface().drawBoxBorder(336, 140, var3 - 12, 18, 4210752);
					if (this.reportAbuse_AbuseType == 2) {
						color = 16744448;
					} else {
						color = 16777215;
					}

					this.getSurface().drawColoredStringCentered(106, "Encouraging rule-breaking", color, 0, 0, var3);
					if (this.reportAbuse_AbuseType == 8) {
						color = 16744448;
					} else {
						color = 16777215;
					}

					this.getSurface().drawColoredStringCentered(256, "Solicitation", color, 0, 0, var3);
					if (this.reportAbuse_AbuseType != 13) {
						color = 16777215;
					} else {
						color = 16744448;
					}

					this.getSurface().drawColoredStringCentered(406, "Breaking real-world laws", color, 0, 0, var3);
					var3 += 20;
					if (this.reportAbuse_AbuseType == 3) {
						this.getSurface().drawBox(36, var3 - 12, 140, 18, 3158064);
					}

					this.getSurface().drawBoxBorder(36, 140, var3 - 12, 18, 4210752);
					if (this.reportAbuse_AbuseType == 9) {
						this.getSurface().drawBox(186, var3 - 12, 140, 18, 3158064);
					}

					this.getSurface().drawBoxBorder(186, 140, var3 - 12, 18, 4210752);
					if (this.reportAbuse_AbuseType == 14) {
						this.getSurface().drawBox(336, var3 - 12, 140, 18, 3158064);
					}

					this.getSurface().drawBoxBorder(336, 140, var3 - 12, 18, 4210752);
					if (this.reportAbuse_AbuseType == 3) {
						color = 16744448;
					} else {
						color = 16777215;
					}

					this.getSurface().drawColoredStringCentered(106, "Staff impersonation", color, 0, 0, var3);
					if (this.reportAbuse_AbuseType != 9) {
						color = 16777215;
					} else {
						color = 16744448;
					}

					this.getSurface().drawColoredStringCentered(256, "Disruptive behaviour", color, 0, 0, var3);
					if (this.reportAbuse_AbuseType != 14) {
						color = 16777215;
					} else {
						color = 16744448;
					}

					this.getSurface().drawColoredStringCentered(406, "Advertising websites", color, 0, 0, var3);
					var3 += 20;
					if (this.reportAbuse_AbuseType == 4) {
						this.getSurface().drawBox(36, var3 - 12, 140, 18, 3158064);
					}

					this.getSurface().drawBoxBorder(36, 140, var3 - 12, 18, 4210752);
					if (this.reportAbuse_AbuseType == 10) {
						this.getSurface().drawBox(186, var3 - 12, 140, 18, 3158064);
					}

					this.getSurface().drawBoxBorder(186, 140, var3 - 12, 18, 4210752);
					if (this.reportAbuse_AbuseType != 4) {
						color = 16777215;
					} else {
						color = 16744448;
					}

					this.getSurface().drawColoredStringCentered(106, "Macroing or use of bots", color, 0, 0, var3);
					if (this.reportAbuse_AbuseType == 10) {
						color = 16744448;
					} else {
						color = 16777215;
					}

					this.getSurface().drawColoredStringCentered(256, "Offensive account name", color, 0, 0, var3);
					var3 += 20;
					if (this.reportAbuse_AbuseType == 5) {
						this.getSurface().drawBox(36, var3 - 12, 140, 18, 3158064);
					}

					this.getSurface().drawBoxBorder(36, 140, var3 - 12, 18, 4210752);
					if (this.reportAbuse_AbuseType == 11) {
						this.getSurface().drawBox(186, var3 - 12, 140, 18, 3158064);
					}

					this.getSurface().drawBoxBorder(186, 140, var3 - 12, 18, 4210752);
					if (this.reportAbuse_AbuseType == 5) {
						color = 16744448;
					} else {
						color = 16777215;
					}

					this.getSurface().drawColoredStringCentered(106, "Scamming", color, 0, 0, var3);
					if (this.reportAbuse_AbuseType != 11) {
						color = 16777215;
					} else {
						color = 16744448;
					}

					this.getSurface().drawColoredStringCentered(256, "Real-life threats", color, 0, 0, var3);
					var3 += 20;
					if (this.reportAbuse_AbuseType == 6) {
						this.getSurface().drawBox(36, var3 - 12, 140, 18, 3158064);
					}

					this.getSurface().drawBoxBorder(36, 140, var3 - 12, 18, 4210752);
					if (this.reportAbuse_AbuseType != 6) {
						color = 16777215;
					} else {
						color = 16744448;
					}

					this.getSurface().drawColoredStringCentered(106, "Exploiting a bug", color, 0, 0, var3);
					var3 += 18;
					var3 += 15;
					color = 16777215;
					if (this.mouseX > 196 && this.mouseX < 316 && this.mouseY > var3 - 15 && this.mouseY < 5 + var3) {
						color = 16776960;
					}

					this.getSurface().drawColoredStringCentered(256, "Click here to cancel", color, 0, 1, var3);
				}
			} catch (RuntimeException var6) {
				throw GenUtil.makeThrowable(var6, "client.ID(" + "dummy" + ')');
			}
		}

		private final void handleTabUIClick() {
			try {
				if (this.showUiTab == 0 && this.mouseX >= this.getSurface().width2 - 35 && this.mouseY >= 3
						&& this.mouseX < this.getSurface().width2 - 3 && this.mouseY < 35) {
					this.showUiTab = 1;
				}

				if (this.showUiTab == 0 && this.mouseX >= this.getSurface().width2 - 35 - 33 && this.mouseY >= 3
						&& this.getSurface().width2 - 3 - 33 > this.mouseX && this.mouseY < 35) {
					this.showUiTab = 2;
					this.minimapRandom_1 = (int) (13.0D * Math.random()) - 6;
					this.minimapRandom_2 = (int) (Math.random() * 23.0D) - 11;
				}

				if (this.showUiTab == 0 && this.getSurface().width2 - 101 <= this.mouseX && this.mouseY >= 3
						&& this.mouseX < this.getSurface().width2 - 3 - 66 && this.mouseY < 35) {
					this.showUiTab = 3;
				}

				if (this.showUiTab == 0 && this.mouseX >= this.getSurface().width2 - 99 - 35 && this.mouseY >= 3
						&& this.getSurface().width2 - 3 - 99 > this.mouseX && this.mouseY < 35) {
					this.showUiTab = 4;
				}

				if (this.showUiTab == 0 && this.mouseX >= this.getSurface().width2 - 35 - 132 && this.mouseY >= 3
						&& this.mouseX < this.getSurface().width2 - 135 && this.mouseY < 35) {
					this.showUiTab = 5;
				}

				if (this.showUiTab == 0 && this.getSurface().width2 - 35 - 165 <= this.mouseX && this.mouseY >= 3
						&& this.mouseX < this.getSurface().width2 - 165 - 3 && this.mouseY < 35) {
					this.showUiTab = 6;
				}

				if (this.showUiTab != 0 && this.getSurface().width2 - 35 <= this.mouseX && this.mouseY >= 3
						&& this.getSurface().width2 - 3 > this.mouseX && this.mouseY < 26) {
					this.showUiTab = 1;
				}

				if (this.showUiTab != 0 && this.showUiTab != 2 && this.getSurface().width2 - 68 <= this.mouseX
						&& this.mouseY >= 3 && this.getSurface().width2 - 33 - 3 > this.mouseX && this.mouseY < 26) {
					this.showUiTab = 2;
					this.minimapRandom_2 = (int) (23.0D * Math.random()) - 11;
					this.minimapRandom_1 = (int) (13.0D * Math.random()) - 6;
				}

				if (this.showUiTab != 0 && this.mouseX >= this.getSurface().width2 - 66 - 35 && this.mouseY >= 3
						&& this.getSurface().width2 - 3 - 66 > this.mouseX && this.mouseY < 26) {
					this.showUiTab = 3;
				}

				if (this.showUiTab != 0 && this.getSurface().width2 - 35 - 99 <= this.mouseX && this.mouseY >= 3
						&& this.getSurface().width2 - 102 > this.mouseX && this.mouseY < 26) {
					this.showUiTab = 4;
				}

				if (this.showUiTab != 0 && this.getSurface().width2 - 167 <= this.mouseX && this.mouseY >= 3
						&& this.getSurface().width2 - 132 - 3 > this.mouseX && this.mouseY < 26) {
					this.showUiTab = 5;
				}

				if (this.showUiTab != 0 && this.getSurface().width2 - 35 - 165 <= this.mouseX && this.mouseY >= 3
						&& this.mouseX < this.getSurface().width2 - 168 && this.mouseY < 26) {
					this.showUiTab = 6;
				}

				if (this.showUiTab == 1
						&& (this.mouseX < this.getSurface().width2 - 248 || 36 + this.m_cl / 5 * 34 < this.mouseY)) {
					this.showUiTab = 0;
				}

				if (this.showUiTab == 3 && (this.getSurface().width2 - 199 > this.mouseX || this.mouseY > 324)) {
					this.showUiTab = 0;
				}

				if ((this.showUiTab == 2 || this.showUiTab == 4 || this.showUiTab == 5)
						&& (this.getSurface().width2 - 199 > this.mouseX || this.mouseY > (this.panelSocialTab == 1 ? 307 : 240))) {
					this.showUiTab = 0;
				}

				if (this.showUiTab == 6 && (this.getSurface().width2 - 199 > this.mouseX || this.mouseY > 325)) {
					this.showUiTab = 0;
				}

			} catch (RuntimeException var3) {
				throw GenUtil.makeThrowable(var3, "client.OC(" + "dummy" + ')');
			}
		}

		private final boolean hasRunes(int rune, int count) {
			try {

				if (rune == 31) {// fire
					if (this.isEquipped(197) || this.isEquipped(615) || this.isEquipped(682))
						return true;
				} else if (rune == 32) { // water
					if (this.isEquipped(102) || this.isEquipped(616) || this.isEquipped(683))
						return true;
				} else if (rune == 33) { // air
					if (this.isEquipped(101) || this.isEquipped(617) || this.isEquipped(684))
						return true;
				} else if (rune == 34) { // earth
					if (this.isEquipped(103) || this.isEquipped(618) || this.isEquipped(685))
						return true;
				}
				return this.getInventoryCount((int) rune) >= count;
			} catch (RuntimeException var5) {
				throw GenUtil.makeThrowable(var5, "client.OA(" + "dummy" + ',' + count + ',' + rune + ')');
			}
		}

		private final boolean isEquipped(int id) {
			try {


				for (int i = 0; this.inventoryItemCount > i; ++i) {
					if (id == this.inventoryItemID[i] && this.inventoryItemEquipped[i] == 1) {
						return true;
					}
				}
				return false;
			} catch (RuntimeException var4) {
				throw GenUtil.makeThrowable(var4, "client.NB(" + id + ',' + "dummy" + ')');
			}
		}

		private final void jumpToLogin() {
			try {
				this.logoutTimeout = 0;
				this.loginScreenNumber = 0;
				this.currentViewMode = GameMode.LOGIN;

				this.systemUpdate = 0;
				this.elixirTimer = 0;
			} catch (RuntimeException var3) {
				throw GenUtil.makeThrowable(var3, "client.FC(" + "dummy" + ')');
			}
		}

		private long getUID() {
			File uID = new File(Config.F_CACHE_DIR + File.separator + "uid.dat");
			try {
				if (!uID.exists()) {
					printWriter = new PrintWriter(new FileOutputStream(uID), true);
					long uuID = new SecureRandom().nextLong();
					printWriter.println(uuID);
					printWriter.flush();
					uID.setReadOnly();
					return uuID;
				} else {
					if (uID.canWrite()) {
						printWriter = new PrintWriter(new FileOutputStream(uID), true);
					}
					BufferedReader buffer = new BufferedReader(new FileReader(uID));
					long theUID = Long.parseLong(buffer.readLine());
					buffer.close();
					return theUID;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return 0L;
		}

		public String getMacAddress() {
			try {
				InetAddress a = InetAddress.getLocalHost();
				NetworkInterface n = NetworkInterface.getByInetAddress(a);
				byte[] m = n.getHardwareAddress();
				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < m.length; i++) {
					sb.append(String.format("%02X%s", m[i], (i <m.length - 1) ? "-" : ""));
				}
				return sb.toString();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return "failed";
		}

		private final void loadEntities(boolean var1) {
			clientPort.showLoadingProgress(30, "people and monsters");
			int animationNumber = 0;
			label0: for (int animationIndex = 0; animationIndex < EntityHandler.animationCount(); animationIndex++) {
				String s = EntityHandler.getAnimationDef(animationIndex).getName();
				for (int nextAnimationIndex = 0; nextAnimationIndex < animationIndex; nextAnimationIndex++) {
					if (!EntityHandler.getAnimationDef(nextAnimationIndex).getName().equalsIgnoreCase(s)) {
						continue;
					}
					EntityHandler.getAnimationDef(animationIndex).number = EntityHandler.getAnimationDef(nextAnimationIndex)
							.getNumber();
					continue label0;
				}

				loadSprite(animationNumber, "entity", 15);
				if (EntityHandler.getAnimationDef(animationIndex).hasA()) {
					loadSprite(animationNumber + 15, "entity", 3);
				}

				if (EntityHandler.getAnimationDef(animationIndex).hasF()) {
					loadSprite(animationNumber + 18, "entity", 9);
				}
				EntityHandler.getAnimationDef(animationIndex).number = animationNumber;
				animationNumber += 27;
				if (animationNumber == 1998) {
					animationNumber = 3300;
				}
			}
		}

		private final void loadGameConfig(boolean var1) {
			try {
				clientPort.showLoadingProgress(1, "Loading Configuration");
				EntityHandler.load(this.serverTypeMembers);
			} catch (RuntimeException var3) {
				throw GenUtil.makeThrowable(var3, "client.CE(" + var1 + ')');
			}
		}

		private final void loadMedia(byte var1) {
			clientPort.showLoadingProgress(20, "2d graphics");
			loadSprite(spriteMedia, "media", 1);
			loadSprite(spriteMedia + 1, "media", 6);
			loadSprite(spriteMedia + 9, "media", 1);
			loadSprite(spriteMedia + 10, "media", 1);
			loadSprite(spriteMedia + 11, "media", 3);
			loadSprite(spriteMedia + 14, "media", 8);
			loadSprite(spriteMedia + 22, "media", 1);
			loadSprite(spriteMedia + 23, "media", 1);

			loadSprite(spriteMedia + 24, "media", 1);

			// int[] pixels = surface.sprites[spriteMedia + 24].getPixels();
			// for (int pixel = 0; pixel < pixels.length; ++pixel) {
			// if (pixels[pixel] == 0x000000) {
			// pixels[pixel] = 16711935;
			// }
			// }

			loadSprite(spriteMedia + 25, "media", 2);
			loadSprite(spriteMedia + 27, "media", 2);
			loadSprite(spriteUtil, "media", 2);
			loadSprite(spriteUtil + 2, "media", 4);
			loadSprite(spriteUtil + 6, "media", 2);
			loadSprite(spriteProjectile, "media", 7);
			loadSprite(3284, "media", 11);
			// loadSprite(spriteLogo, "media", 1);

			int i = EntityHandler.invPictureCount();
			for (int j = 1; i > 0; ++j) {
				int amount = i;
				if (amount > 30) {
					amount = 30;
				}
				i -= 30;
				loadSprite(spriteItem + (j - 1) * 30, "media.object", amount);
			}
		}

		private final void loadModels(boolean var1) {
			byte[] models = unpackData("models.jag", "3d models", 60);

			String[] modelNames = { "torcha2", "torcha3", "torcha4", "skulltorcha2", "skulltorcha3", "skulltorcha4",
					"firea2", "firea3", "fireplacea2", "fireplacea3", "firespell2", "firespell3", "lightning2",
					"lightning3", "clawspell2", "clawspell3", "clawspell4", "clawspell5", "spellcharge2", "spellcharge3" };
			for (String name : modelNames) {
				EntityHandler.storeModel(name);
			}
			if (models == null) {
				errorLoadingData = true;
				return;
			}
			for (int j = 0; j < EntityHandler.getModelCount(); j++) {
				int k = DataOperations.getDataFileOffset(EntityHandler.getModelName(j) + ".ob3", models);
				if (k == 0) {
					modelCache[j] = new RSModel(1, 1);
				} else {
					modelCache[j] = new RSModel(models, k, true);
				}
				modelCache[j].m_cb = EntityHandler.getModelName(j).equals("giantcrystal");
			}
		}

		private final boolean loadNextRegion(int wantZ, int wantX, boolean var3) {
			try {

				if (this.deathScreenTimeout != 0) {
					this.world.playerAlive = false;
					return false;
				} else {
					this.loadingArea = var3;
					wantZ += this.worldOffsetZ;
					wantX += this.worldOffsetX;
					if (this.lastHeightOffset == this.requestedPlane && this.currentRegionMinX < wantX
							&& this.currentRegionMaxX > wantX && this.currentRegionMinZ < wantZ
							&& wantZ < this.currentRegionMaxZ) {
						this.world.playerAlive = true;
						return false;
					} else {
						this.getSurface().drawColoredStringCentered(256, "Loading... Please wait", 16777215, 0, 1, 192);
						this.drawChatMessageTabs(5);
						// this.getSurface().draw(this.graphics, this.screenOffsetX,
						// 256, this.screenOffsetY);
						clientPort.draw();
						int oldBaseX = this.midRegionBaseX;
						int oldBaseZ = this.midRegionBaseZ;
						int midRegionX = (wantX + 24) / 48;
						this.midRegionBaseX = midRegionX * 48 - 48;
						int midRegionZ = (24 + wantZ) / 48;
						this.lastHeightOffset = this.requestedPlane;
						this.currentRegionMaxZ = midRegionZ * 48 + 32;
						this.currentRegionMaxX = midRegionX * 48 + 32;
						this.currentRegionMinZ = midRegionZ * 48 - 32;
						this.midRegionBaseZ = midRegionZ * 48 - 48;
						this.currentRegionMinX = midRegionX * 48 - 32;
						this.world.loadSections(wantX, wantZ, this.lastHeightOffset);
						this.midRegionBaseZ -= this.worldOffsetZ;
						this.midRegionBaseX -= this.worldOffsetX;
						int baseDX = this.midRegionBaseX - oldBaseX;
						int baseDZ = this.midRegionBaseZ - oldBaseZ;

						for (int i = 0; this.gameObjectInstanceCount > i; ++i) {
							this.gameObjectInstanceX[i] -= baseDX;
							this.gameObjectInstanceZ[i] -= baseDZ;
							int xTile = this.gameObjectInstanceX[i];
							int zTile = this.gameObjectInstanceZ[i];
							int objectID = this.gameObjectInstanceID[i];
							RSModel model = this.gameObjectInstanceModel[i];

							try {
								int dir = this.gameObjectInstanceDir[i];
								int xSize;
								int zSize;
								if (dir != 0 && dir != 4) {
									zSize = EntityHandler.getObjectDef(objectID).getWidth();
									xSize = EntityHandler.getObjectDef(objectID).getHeight();
								} else {
									zSize = EntityHandler.getObjectDef(objectID).getHeight();
									xSize = EntityHandler.getObjectDef(objectID).getWidth();
								}

								int x = (2 * xTile + xSize) * this.tileSize / 2;
								int z = this.tileSize * (2 * zTile + zSize) / 2;
								if (xTile >= 0 && zTile >= 0 && xTile < 96 && zTile < 96) {
									this.scene.addModel(model);
									model.setTranslate(x, -this.world.getElevation(x, z), z);
									this.world.addGameObject_UpdateCollisionMap(xTile, zTile, objectID, var3);
									if (objectID == 74) {
										model.translate2(0, -480, 0);
									}
								}
							} catch (RuntimeException var21) {
								System.out.println("Loc Error: " + var21.getMessage());
								System.out.println("i:" + i + " obj:" + model);
								var21.printStackTrace();
							}
						}

						for (int i = 0; this.wallObjectInstanceCount > i; ++i) {
							this.wallObjectInstanceX[i] -= baseDX;
							this.wallObjectInstanceZ[i] -= baseDZ;
							int xTile = this.wallObjectInstanceX[i];
							int zTile = this.wallObjectInstanceZ[i];
							int id = this.wallObjectInstanceID[i];
							int dir = this.wallObjectInstanceDir[i];

							try {
								this.world.applyWallToCollisionFlags(id, xTile, zTile, dir);
								RSModel var25 = this.createWallObjectModel(xTile, zTile, id, dir, i);
								this.wallObjectInstanceModel[i] = var25;
							} catch (RuntimeException var20) {
								System.out.println("Bound Error: " + var20.getMessage());
								var20.printStackTrace();
							}
						}

						for (int i = 0; this.groundItemCount > i; ++i) {
							this.groundItemX[i] -= baseDX;
							this.groundItemZ[i] -= baseDZ;
						}

						for (int i = 0; i < this.playerCount; ++i) {
							RSCharacter var23 = this.players[i];
							var23.currentX -= this.tileSize * baseDX;
							var23.currentZ -= baseDZ * this.tileSize;

							for (int j = 0; j <= var23.waypointCurrent; ++j) {
								var23.waypointsX[j] -= this.tileSize * baseDX;
								var23.waypointsZ[j] -= baseDZ * this.tileSize;
							}
						}

						for (int i = 0; i < this.npcCount; ++i) {
							RSCharacter var23 = this.npcs[i];
							var23.currentZ -= this.tileSize * baseDZ;
							var23.currentX -= this.tileSize * baseDX;

							for (int j = 0; var23.waypointCurrent >= j; ++j) {
								var23.waypointsX[j] -= this.tileSize * baseDX;
								var23.waypointsZ[j] -= baseDZ * this.tileSize;
							}
						}

						this.world.playerAlive = true;
						return true;
					}
				}
			} catch (RuntimeException var22) {
				throw GenUtil.makeThrowable(var22, "client.MB(" + wantZ + ',' + wantX + ',' + var3 + ')');
			}
		}

		private final void loadSounds() {
			try {
				//soundData = unpackData("sounds.mem", "Sound effects", 90);
				return;
			} catch (Throwable throwable) {
				System.out.println("Unable to init sounds:" + throwable);
			}
		}

		private final void loadTextures(byte var1) {
			clientPort.showLoadingProgress(50, "Textures");
			this.scene.a(0, 11, 7, EntityHandler.textureCount());
			for (int i = 0; i < EntityHandler.textureCount(); i++) {
				loadSprite(spriteTexture + i, "texture", 1);
				Sprite sprite = getSurface().sprites[spriteTexture + i];
				int length = sprite.getWidth() * sprite.getHeight();
				int[] pixels = sprite.getPixels();
				int ai1[] = new int[32768];
				for (int k = 0; k < length; k++) {
					ai1[((pixels[k] & 0xf80000) >> 9) + ((pixels[k] & 0xf800) >> 6) + ((pixels[k] & 0xf8) >> 3)]++;
				}

				for (int pixel = 0; pixel < pixels.length; ++pixel) {
					if (pixels[pixel] == 0x000000) {
						pixels[pixel] = 16711935;
					}
				}

				int[] dictionary = new int[256];
				dictionary[0] = 0xff00ff;
				int[] temp = new int[256];
				for (int i1 = 0; i1 < ai1.length; i1++) {
					int j1 = ai1[i1];
					if (j1 > temp[255]) {
						for (int k1 = 1; k1 < 256; k1++) {
							if (j1 <= temp[k1]) {
								continue;
							}
							for (int i2 = 255; i2 > k1; i2--) {
								dictionary[i2] = dictionary[i2 - 1];
								temp[i2] = temp[i2 - 1];
							}
							dictionary[k1] = ((i1 & 0x7c00) << 9) + ((i1 & 0x3e0) << 6) + ((i1 & 0x1f) << 3) + 0x40404;
							temp[k1] = j1;
							break;
						}
					}
					ai1[i1] = -1;
				}
				byte[] indices = new byte[length];
				for (int l1 = 0; l1 < length; l1++) {
					int j2 = pixels[l1];
					int k2 = ((j2 & 0xf80000) >> 9) + ((j2 & 0xf800) >> 6) + ((j2 & 0xf8) >> 3);
					int l2 = ai1[k2];
					if (l2 == -1) {
						int i3 = 0x3b9ac9ff;
						int j3 = j2 >> 16 & 0xff;
							int k3 = j2 >> 8 & 0xff;
				int l3 = j2 & 0xff;
				for (int i4 = 0; i4 < 256; i4++) {
					int j4 = dictionary[i4];
					int k4 = j4 >> 16 & 0xff;
				int l4 = j4 >> 8 & 0xff;
				int i5 = j4 & 0xff;
				int j5 = (j3 - k4) * (j3 - k4) + (k3 - l4) * (k3 - l4) + (l3 - i5) * (l3 - i5);
				if (j5 < i3) {
					i3 = j5;
					l2 = i4;
				}
				}

				ai1[k2] = l2;
					}
					indices[l1] = (byte) l2;
				}
				this.scene.loadTexture(i, dictionary, sprite.getSomething1() / 64 - 1, indices);
			}
		}

		private final void loadSprite(int id, String packageName, int amount) {
			for (int i = id; i < id + amount; i++) {
				if (!getSurface().loadSprite(i, packageName)) {
					errorLoadingData = true;
					return;
				}
			}
		}

		private final void login(int var1, String pass, String user, boolean reconnecting) {
			try {

				this.m_Zb = 0;
				if (this.m_Zb > 0) {
					this.showLoginScreenStatus("Please wait...", "Connecting to server");

					try {
						GenUtil.sleepShadow(2000L);
					} catch (Exception var14) {
						;
					}

					this.showLoginScreenStatus("Sorry! The server is currently full.", "Please try again later");
				} else {
					while (this.autoLoginTimeout > 0) {
						try {
							this.setUsername(user);
                            this.password = DataOperations.addCharacters(pass, 20); // TODO: This strips special chars to udnerscore. We may want to in the future allow special chars.
							//this.password = pass;
							//MiscFunctions.netbase_a(20, (byte) -5, pass);
							if (this.getUsername().trim().length() == 0) {
								this.showLoginScreenStatus("You must enter both a username",
										"and a password - Please try again");
								return;
							}

							if (!reconnecting) {
								this.showLoginScreenStatus("Please wait...", "Connecting to server");
							} else {
								this.drawTextBox("Attempting to re-establish", (byte) -64,
										"Connection lost! Please wait...");
							}

							this.setClientStream(
									new Network_Socket(this.openSocket(Config.SERVER_PORT, Config.SERVER_IP), this));
							this.getClientStream().m_d = MiscFunctions.maxReadTries;

							Math.random();
							Math.random();
							Math.random();
							Math.random();

							this.getClientStream().newPacket(0);
							if (reconnecting) {
								this.getClientStream().writeBuffer1.putByte(1);
							} else {
								this.getClientStream().writeBuffer1.putByte(0);
							}
							this.getClientStream().writeBuffer1.putInt(Config.CLIENT_VERSION);
							this.getClientStream().writeBuffer1.putString(getUsername());
							this.getClientStream().writeBuffer1.putString(password);

							this.getClientStream().writeBuffer1.putLong(getUID());
							this.getClientStream().writeBuffer1.putString(getMacAddress());
							/*
							 * RSBuffer rsaBuffer = new RSBuffer(500);
							 * rsaBuffer.putByte(10);
							 * rsaBuffer.putInt(isaacSeed[0]);
							 * rsaBuffer.putInt(isaacSeed[1]);
							 * rsaBuffer.putInt(isaacSeed[2]);
							 * rsaBuffer.putInt(isaacSeed[3]);
							 * rsaBuffer.encodeWithRSA(MiscFunctions.RSA_EXPONENT,
							 * MiscFunctions.RSA_MODULUS);
							 *
							 * this.clientStream.writeBuffer1.writeBytes(rsaBuffer.
							 * dataBuffer, 0, rsaBuffer.curPointerPosition);
							 */

							/*
							 * this.clientStream.writeBuffer1.putShort(0); int
							 * oldSize =
							 * this.clientStream.writeBuffer1.curPointerPosition;
							 *
							 * this.clientStream.writeBuffer1.a(oldSize, isaacSeed,
							 * this.clientStream.writeBuffer1.curPointerPosition);
							 *
							 * this.clientStream.writeBuffer1.put16_Offset(this.
							 * clientStream.writeBuffer1.curPointerPosition -
							 * oldSize);
							 */
							this.getClientStream().finishPacketAndFlush();
							// this.clientStream.seedIsaac(isaacSeed);
							int var11 = this.getClientStream().read();

							System.out.println("login response:" + var11);
							if (var11 == 0) {
								this.m_Ce = var11 & 3;
								this.autoLoginTimeout = 0;
								this.m_Oj = (var11 & 63) >> 2;
								this.resetGame((int) -109);
								return;
							}

							if (var11 == 1) {
								this.autoLoginTimeout = 0;
								this.setEGTo77();
								return;
							}

							if (!reconnecting) {
								if (var11 != -1) {
									if (var11 != 3) {
										if (var11 == 4) {
											this.showLoginScreenStatus("That username is already logged in.",
													"Wait 60 seconds then retry");
										} else if (var11 != 5) {
											if (var11 != 6) {
												if (var11 != 7) {
													if (var11 != 8) {
														if (var11 != 9) {
															if (var11 != 10) {
																if (var11 != 11) {
																	if (var11 != 12) {
																		if (var11 == 14) {
																			this.showLoginScreenStatus(
																					"Sorry! This world is currently full.",
																					"Please try a different world");
																			this.m_Zb = 1500;
																		} else if (var11 != 15) {
																			if (var11 == 16) {
																				this.showLoginScreenStatus(
																						"Error - no reply from loginserver.",
																						"Please try again");
																			} else if (var11 != 17) {
																				if (var11 == 18) {
																					this.showLoginScreenStatus(
																							"Account suspected stolen.",
																							"Press \'recover a locked account\' on front page.");
																				} else if (var11 != 20) {
																					if (var11 != 21) {
																						if (var11 == 22) {
																							this.showLoginScreenStatus(
																									"Password suspected stolen.",
																									"Press \'change your password\' on front page.");
																						} else if (var11 != 23) {
																							if (var11 == 24) {
																								this.showLoginScreenStatus(
																										"This world does not accept new players.",
																										"Please see the launch page for help");
																							} else if (var11 != 25) {
																								this.showLoginScreenStatus(
																										"Error unable to login.",
																										"Unrecognised response code");
																							} else {
																								this.showLoginScreenStatus(
																										"None of your characters can log in.",
																										"Contact customer support");
																							}
																						} else {
																							this.showLoginScreenStatus(
																									"You need to set your display name.",
																									"Please go to the Account Management page to do this.");
																						}
																					} else {
																						this.showLoginScreenStatus(
																								"That is not a veteran RS-Classic account.",
																								"Please try a non-veterans world.");
																					}
																				} else {
																					this.showLoginScreenStatus(
																							"Error - loginserver mismatch",
																							"Please try a different world");
																				}
																			} else {
																				this.showLoginScreenStatus(
																						"Error - failed to decode profile.",
																						"Contact customer support");
																			}
																		} else {
																			this.showLoginScreenStatus(
																					"You need a members account",
																					"to login to this world");
																		}
																	} else {
																		this.showLoginScreenStatus(
																				"Account permanently disabled.",
																				"Check your message inbox for details");
																	}
																} else {
																	this.showLoginScreenStatus(
																			"Account temporarily disabled.",
																			"Check your message inbox for details");
																}
															} else {
																this.showLoginScreenStatus(
																		"That username is already in use.",
																		"Wait 60 seconds then retry");
															}
														} else {
															this.showLoginScreenStatus("Error unable to login.",
																	"Under 13 accounts cannot access RuneScape Classic");
														}
													} else {
														this.showLoginScreenStatus("Error unable to login.",
																"Server rejected session");
													}
												} else {
													this.showLoginScreenStatus("Login attempts exceeded!",
															"Please try again in 5 minutes");
												}
											} else {
												this.showLoginScreenStatus("You may only use 1 character at once.",
														"Your ip-address is already in use");
											}
										} else {
											this.showLoginScreenStatus("The client has been updated.",
													"Please reload this page");
										}
									} else {
										this.showLoginScreenStatus("Invalid username or password.",
												"Try again, or create a new account");
									}
								} else {
									this.showLoginScreenStatus("Error unable to login.", "Server timed out");
								}
							} else {
								this.setUsername("");
								this.jumpToLogin();
							}

							return;
						} catch (Exception var15) {
							var15.printStackTrace();
							if (this.autoLoginTimeout <= 0) {
								if (reconnecting) {
									this.password = "";
									this.setUsername("");
									this.jumpToLogin();
								} else {
									// GenUtil.reportErrorToJagex(var15, "Error
									// while connecting");
									this.showLoginScreenStatus("Sorry! Unable to connect.",
											"Check internet settings or try another world");
								}
							} else {
								try {
									GenUtil.sleepShadow(5000L);
								} catch (Exception var12) {
									;
								}

								--this.autoLoginTimeout;
							}
						}
					}

					if (var1 != -12) {
						this.drawInputX();
					}

				}
			} catch (RuntimeException var16) {
				throw GenUtil.makeThrowable(var16, "client.IB(" + var1 + ',' + (pass != null ? "{...}" : "null") + ','
						+ (user != null ? "{...}" : "null") + ',' + reconnecting + ')');
			}
		}

		private final void lostConnection(int var1) {
			try {

				this.systemUpdate = 0;
				this.elixirTimer = 0;
				if (var1 <= 59) {
					this.drawDialogOptionsMenu(-85);
				}

				if (this.logoutTimeout != 0) {
					this.jumpToLogin();
				} else {
					System.out.println("Lost connection");
					this.autoLoginTimeout = 10;
					this.login(-12, this.password, this.getUsername(), true);
				}
			} catch (RuntimeException var3) {
				throw GenUtil.makeThrowable(var3, "client.CC(" + var1 + ')');
			}
		}

		private final Socket openSocket(int port, String host) throws IOException {
			Socket var4 = new Socket(InetAddress.getByName(host), port);
			//var4.setSendBufferSize(25000);
			//var4.setReceiveBufferSize(25000);
			var4.setSoTimeout(30000);
			var4.setTcpNoDelay(true);
			return var4;
		}

		private final void playSoundFile(String key) {
			try {
				if (!optionSoundDisabled) {
					int dataLength = DataOperations.getDataFileLength(key + ".pcm", soundData);
					int offset = DataOperations.getDataFileOffset(key + ".pcm", soundData);

					clientPort.playSound(soundData, offset, dataLength);
					//
					// soundPlayer.writeStream(soundData,
					// DataOperations.getDataFileOffset(key + ".pcm", soundData),
					// DataOperations.getDataFileLength(key + ".pcm", soundData));
				}

			} catch (RuntimeException var6) {
				throw GenUtil.makeThrowable(var6, "client.SC(" + "dummy" + ',' + (key != null ? "{...}" : "null") + ')');
			}
		}

		private final void putStringPair(String str1, String str2) {
			try {
				this.getClientStream().newPacket(218);

				this.getClientStream().writeBuffer1.putString(str1);
				RSBufferUtils.putEncryptedString(this.getClientStream().writeBuffer1, str2);
				this.getClientStream().finishPacket();
			} catch (RuntimeException var5) {
				throw GenUtil.makeThrowable(var5, "client.KB(" + "dummy" + ',' + (str1 != null ? "{...}" : "null") + ','
						+ (str2 != null ? "{...}" : "null") + ')');
			}
		}

		private final void removeFriend(String var1, byte var2) {
			try {


				String var3 = StringUtil.displayNameToKey(var1);
				if (var3 != null) {
					for (int var4 = 0; var4 < SocialLists.friendListCount; ++var4) {
						if (var3.equals(StringUtil.displayNameToKey(SocialLists.friendList[var4]))) {
							--SocialLists.friendListCount;

							for (int var5 = var4; var5 < SocialLists.friendListCount; ++var5) {
								SocialLists.friendList[var5] = SocialLists.friendList[1 + var5];
								SocialLists.friendListOld[var5] = SocialLists.friendListOld[1 + var5];
								SocialLists.friendListArgS[var5] = SocialLists.friendListArgS[1 + var5];
								SocialLists.friendListArg[var5] = SocialLists.friendListArg[var5 + 1];
							}

							this.getClientStream().newPacket(167);
							this.getClientStream().writeBuffer1.putNullThenString(var1, 110);
							this.getClientStream().finishPacket();
							break;
						}
					}

				}
			} catch (RuntimeException var6) {
				throw GenUtil.makeThrowable(var6, "client.O(" + (var1 != null ? "{...}" : "null") + ',' + var2 + ')');
			}
		}

		private final void removeIgnore(String name) {
			try {

				String findKey = StringUtil.displayNameToKey(name);
				if (findKey != null) {
					for (int j = 0; j < SocialLists.ignoreListCount; ++j) {
						if (findKey.equals(StringUtil.displayNameToKey(SocialLists.ignoreList[j]))) {
							--SocialLists.ignoreListCount;

							for (int i = j; i < SocialLists.ignoreListCount; ++i) {
								SocialLists.ignoreListArg0[i] = SocialLists.ignoreListArg0[i + 1];
								SocialLists.ignoreList[i] = SocialLists.ignoreList[1 + i];
								SocialLists.ignoreListArg1[i] = SocialLists.ignoreListArg1[i + 1];
								SocialLists.ignoreListOld[i] = SocialLists.ignoreListOld[i];
							}

							this.getClientStream().newPacket(241);
							this.getClientStream().writeBuffer1.putNullThenString(name, -78);
							this.getClientStream().finishPacket();
							break;
						}
					}

				}
			} catch (RuntimeException var6) {
				throw GenUtil.makeThrowable(var6, "client.E(" + "dummy" + ',' + (name != null ? "{...}" : "null") + ')');
			}
		}

		private final void renderLoginScreenViewports(int var1) {
			try {

				byte var2 = 0;
				byte var3 = 50;
				byte var4 = 50;
				this.world.loadSections(var3 * 48 + 23, (int) (var4 * 48 + 23), var2);
				this.world.addLoginScreenModels(this.modelCache);
				short var5 = 9728;
				short var7 = 1100;
				short var6 = 6400;
				this.scene.fogLandscapeDistance = 4100;
				this.scene.fogEntityDistance = 4100;
				short var8 = 888;
				this.scene.fogZFalloff = 1;
				this.scene.fogSmoothingStartDistance = 4000;
				this.scene.setCamera(var5, -this.world.getElevation(var5, var6), var6, 912, var8, (int) 0, var7 * 2);
				this.scene.endScene(-124);
				if (var1 >= -48) {
					this.localPlayer = (RSCharacter) null;
				}

				this.getSurface().fade2black(16316665);
				this.getSurface().fade2black(16316665);
				this.getSurface().drawBox(0, 0, getGameWidth(), 6, 0);

				int var9;
				for (var9 = 6; var9 >= 1; --var9) {
					this.getSurface().a(8, var9, var9, 0, 16740352, getGameWidth(), 0);
				}

				this.getSurface().drawBox(0, 194, getGameWidth(), 20, 0);

				for (var9 = 6; var9 >= 1; --var9) {
					this.getSurface().a(8, var9, 194 - var9, 0, 16740352, getGameWidth(), 0);
				}

				this.getSurface().drawSprite(mudclient.spriteMedia + 10, 15, 15);
				this.getSurface().storeSpriteVert(spriteLogo, 0, 0, getGameWidth(), 200);
				var6 = 9216;
				var8 = 888;
				var7 = 1100;
				var5 = 9216;
				this.scene.fogLandscapeDistance = 4100;
				this.scene.fogZFalloff = 1;
				this.scene.fogSmoothingStartDistance = 4000;
				this.scene.fogEntityDistance = 4100;
				this.scene.setCamera(var5, -this.world.getElevation(var5, var6), var6, 912, var8, (int) 0, var7 * 2);
				this.scene.endScene(-114);
				this.getSurface().fade2black(16316665);
				this.getSurface().fade2black(16316665);
				this.getSurface().drawBox(0, 0, getGameWidth(), 6, 0);

				for (var9 = 6; var9 >= 1; --var9) {
					this.getSurface().a(8, var9, var9, 0, 16740352, getGameWidth(), 0);
				}

				this.getSurface().drawBox(0, 194, getGameWidth(), 20, 0);

				for (var9 = 6; var9 >= 1; --var9) {
					this.getSurface().a(8, var9, 194 - var9, 0, 16740352, getGameWidth(), 0);
				}

				this.getSurface().drawSprite(10 + mudclient.spriteMedia, 15, 15);
				this.getSurface().storeSpriteVert(spriteLogo + 1, 0, 0, getGameWidth(), 200);
				var7 = 500;
				var8 = 376;
				var5 = 11136;
				var6 = 10368;

				for (var9 = 0; var9 < 64; ++var9) {
					this.scene.removeModel(this.world.modelRoofGrid[0][var9]);
					this.scene.removeModel(this.world.modelWallGrid[1][var9]);
					this.scene.removeModel(this.world.modelRoofGrid[1][var9]);
					this.scene.removeModel(this.world.modelWallGrid[2][var9]);
					this.scene.removeModel(this.world.modelRoofGrid[2][var9]);
				}

				this.scene.fogLandscapeDistance = 4100;
				this.scene.fogSmoothingStartDistance = 4000;
				this.scene.fogZFalloff = 1;
				this.scene.fogEntityDistance = 4100;
				this.scene.setCamera(var5, -this.world.getElevation(var5, var6), var6, 912, var8, (int) 0, var7 * 2);
				this.scene.endScene(-111);
				this.getSurface().fade2black(16316665);
				this.getSurface().fade2black(16316665);
				this.getSurface().drawBox(0, 0, getGameWidth(), 6, 0);

				for (var9 = 6; var9 >= 1; --var9) {
					this.getSurface().a(8, var9, var9, 0, 16740352, getGameWidth(), 0);
				}

				this.getSurface().drawBox(0, 194, getGameWidth(), 20, 0);

				for (var9 = 6; var9 >= 1; --var9) {
					this.getSurface().a(8, var9, 194, 0, 16740352, getGameWidth(), 0);
				}

				this.getSurface().drawSprite(mudclient.spriteMedia + 10, 15, 15);
				this.getSurface().storeSpriteVert(spriteMedia + 10, 0, 0, getGameWidth(), 200);
			} catch (RuntimeException var10) {
				throw GenUtil.makeThrowable(var10, "client.HC(" + var1 + ')');
			}
		}

		private final void resetGame(int var1) {
			try {
				this.systemUpdate = 0;
				this.elixirTimer = 0;
				this.loginScreenNumber = 0;
				this.logoutTimeout = 0;

				this.currentViewMode = GameMode.GAME;
				this.clearInputString80((byte) -49);

				for (NComponent n : mainComponent.subComponents())
					n.setVisible(false);

				clan.putClan(false);
				experienceOverlay.setVisible(true);
				this.getSurface().blackScreen(true);
				// this.getSurface().draw(this.graphics, this.screenOffsetX, 256,
				// this.screenOffsetY);
				clientPort.draw();
				int i;
				for (i = 0; i < this.gameObjectInstanceCount; ++i) {
					this.scene.removeModel(this.gameObjectInstanceModel[i]);
					this.world.removeGameObject_CollisonFlags(this.gameObjectInstanceID[i],
							(int) this.gameObjectInstanceX[i], (int) this.gameObjectInstanceZ[i]);
				}

				for (i = 0; i < this.wallObjectInstanceCount; ++i) {
					this.scene.removeModel(this.wallObjectInstanceModel[i]);
					this.world.removeWallObject_CollisionFlags(true, this.wallObjectInstanceDir[i],
							this.wallObjectInstanceZ[i], this.wallObjectInstanceX[i], this.wallObjectInstanceID[i]);
				}

				this.groundItemCount = 0;
				this.gameObjectInstanceCount = 0;
				this.wallObjectInstanceCount = 0;
				this.playerCount = 0;

				for (i = 0; i < 4000; ++i) {
					this.playerServer[i] = null;
				}

				for (i = 0; i < 500; ++i) {
					this.players[i] = null;
				}

				this.npcCount = 0;

				for (i = 0; i < 5000; ++i) {
					this.npcsServer[i] = null;
				}

				for (i = 0; i < 500; ++i) {
					this.npcs[i] = null;
				}

				for (i = 0; i < 50; ++i) {
					this.prayerOn[i] = false;
				}

				this.showDialogShop = false;
				this.currentMouseButtonDown = 0;
				this.lastMouseButtonDown = 0;
				this.mouseButtonClick = 0;
				this.isSleeping = false;
				// i = 58 / ((var1 + 46) / 51);
				this.setShowDialogBank(false);
				this.auctionHouse.setVisible(false);
				//this.achievementInterface.setVisible(false);
				clan.getClanInterface().setVisible(false);
				SocialLists.friendListCount = 0;
				SocialLists.clanListCount = 0;
				this.reportAbuse_State = 0;

				for (int j = 0; j < messagesArray.length; ++j) {
					MessageHistory.messageHistoryMessage[j] = null;
					MessageHistory.messageHistoryTimeout[j] = 0;
					MessageHistory.messageHistorySender[j] = null;
					MessageHistory.messageHistoryCrownID[j] = 0;
					MessageHistory.messageHistoryClan[j] = null;
					MessageHistory.messageHistoryColor[j] = null;
					MessageHistory.messageHistoryType[j] = MessageType.GAME;
				}

			} catch (RuntimeException var4) {
				throw GenUtil.makeThrowable(var4, "client.UD(" + var1 + ')');
			}
		}

		private final void resetLoginScreenVariables(byte var1) {
			try {
				this.npcCount = 0;
				this.playerCount = 0;
				this.loginScreenNumber = 0;
				if (var1 != -88) {
					this.teleportBubbleType = (int[]) null;
				}

				this.currentViewMode = GameMode.LOGIN;


			} catch (RuntimeException var3) {
				throw GenUtil.makeThrowable(var3, "client.UA(" + var1 + ')');
			}
		}

		// This seems to be having trouble. The decryption function is known to work
		// and the encryption function is the mirror of it. Thus the problem is
		// confusing.
		private final void sendChatMessage(String var1) {
			try {
				this.getClientStream().newPacket(216);
				RSBufferUtils.putEncryptedString(this.getClientStream().writeBuffer1, var1);
				this.getClientStream().finishPacket();
			} catch (RuntimeException var4) {
				throw GenUtil.makeThrowable(var4, "client.AA(" + (var1 != null ? "{...}" : "null") + ')');
			}
		}

		public final void sendCommandString(String var1) {
			try {
				this.getClientStream().newPacket(38);

				this.getClientStream().writeBuffer1.putString(var1);
				this.getClientStream().finishPacket();
			} catch (RuntimeException var4) {
				throw GenUtil.makeThrowable(var4, "client.UC(" + (var1 != null ? "{...}" : "null") + ',' + "dummy" + ')');
			}
		}

		private final void sendLogout(int var1) {
			try {

				if (this.currentViewMode != GameMode.LOGIN) {
					if (this.combatTimeout <= 450) {
						if (var1 < this.combatTimeout) {
							this.showMessage(false, (String) null, "You can\'t logout for 10 seconds after combat",
									MessageType.GAME, 0, (String) null, "@cya@");
						} else {
							this.getClientStream().newPacket(102);
							this.getClientStream().finishPacket();
							this.logoutTimeout = 1000;
						}
					} else {
						this.showMessage(false, (String) null, "You can\'t logout during combat!", MessageType.GAME, 0,
								(String) null, "@cya@");
					}
				}
			} catch (RuntimeException var3) {
				throw GenUtil.makeThrowable(var3, "client.T(" + var1 + ')');
			}
		}

		private final boolean sendWalkToGroundItem(int startX, int startZ, int x1, int x2, int z1, int z2, boolean var4,
				boolean var9) {
			try {

				int var10 = this.world.findPath(this.pathX, this.pathZ, startX, startZ, x1, x2, z1, z2, var4);
				if (var10 == -1) {
					return false;
				} else {
					--var10;
					startX = this.pathX[var10];
					startZ = this.pathZ[var10];
					if (!var9) {
						this.getClientStream().newPacket(187);
					} else {
						this.getClientStream().newPacket(16);
					}

					--var10;
					this.getClientStream().writeBuffer1.putShort(this.midRegionBaseX + startX);
					this.getClientStream().writeBuffer1.putShort(this.midRegionBaseZ + startZ);
					if (var9 && var10 == -1 && (this.midRegionBaseX + startX) % 5 == 0) {
						var10 = 0;
					}

					for (int var11 = var10; var11 >= 0 && var10 - 25 < var11; --var11) {
						this.getClientStream().writeBuffer1.putByte(this.pathX[var11] - startX);
						this.getClientStream().writeBuffer1.putByte(this.pathZ[var11] - startZ);
					}

					this.getClientStream().finishPacket();
					this.mouseWalkX = this.mouseX;
					this.mouseWalkY = this.mouseY;
					this.mouseClickXStep = -24;
					return true;
				}
			} catch (RuntimeException var12) {
				throw GenUtil.makeThrowable(var12, "client.WA(" + startZ + ',' + startX + ',' + "dummy" + ',' + var4 + ','
						+ x1 + ',' + x2 + ',' + z1 + ',' + z2 + ',' + var9 + ')');
			}
		}

		private final void setEGTo77() {
			try {

				this.m_eg = 77;

			} catch (RuntimeException var3) {
				throw GenUtil.makeThrowable(var3, "client.EA(" + "dummy" + ')');
			}
		}

		private final void showItemModX(InputXAction action, String[] lines, boolean var4, String defaultText) {
			try {
				this.inputX_Lines = lines;
				this.inputX_Width = 400;


				for (int i = 0; lines.length > i; ++i) {
					int width = this.getSurface().stringWidth(1, lines[i]) + 10;
					if (this.inputX_Width < width) {
						this.inputX_Width = width;
					}
				}

				this.inputX_Height = 15 + (this.getSurface().fontHeight(1) + 2) * (1 + lines.length)
						+ this.getSurface().fontHeight(4);
				this.inputX_Action = action;
				this.inputTextCurrent = defaultText;
				this.inputX_OK = false;
				this.inputTextFinal = "";
				this.inputX_Focused = var4;
			} catch (RuntimeException var8) {
				throw GenUtil.makeThrowable(var8,
						"client.JA(" + "dummy" + ',' + action + ',' + (lines != null ? "{...}" : "null") + ',' + var4 + ','
						+ (defaultText != null ? "{...}" : "null") + ')');
			}
		}

		public final void showItemModX(String[] lines, InputXAction var3, boolean var4) {
			try {
				this.showItemModX(var3, lines, var4, "");

			} catch (RuntimeException var6) {
				throw GenUtil.makeThrowable(var6,
						"client.RA(" + (lines != null ? "{...}" : "null") + ',' + "dummy" + ',' + var3 + ',' + var4 + ')');
			}
		}

		private final void showLoginScreenStatus(String a, String b) {
			try {

				if (this.loginScreenNumber == 2) {
					if (b != null && b.length() >= 1) {
						this.panelLogin.setText(this.controlLoginStatus1, a);
						this.panelLogin.setText(this.controlLoginStatus2, b);
					} else {
						this.panelLogin.setText(this.controlLoginStatus2, a);
					}
				}
				if (loginScreenNumber == 1) {
					if (b != null && b.length() >= 1) {
						menuNewUser.setText(menuNewUserStatus, a);
						menuNewUser.setText(menuNewUserStatus2, b);
					} else {
						menuNewUser.setText(menuNewUserStatus, a);
					}
				}

				this.drawLogin();
				this.zeroMF();
			} catch (RuntimeException var5) {
				throw GenUtil.makeThrowable(var5, "client.VB(" + "dummy" + ',' + (a != null ? "{...}" : "null") + ','
						+ (b != null ? "{...}" : "null") + ')');
			}
		}

		public final void showMessage(boolean crownEnabled, String sender, String message, MessageType type, int crownID,
				String formerName, String colorOverride) {
			try {

				if ((type == MessageType.PRIVATE_RECIEVE || type == MessageType.CHAT || type == MessageType.TRADE
						|| type == MessageType.GLOBAL_CHAT || type == MessageType.CLAN_CHAT) && formerName != null && !crownEnabled) {
					String clanKey = StringUtil.displayNameToKey(formerName);
					if (null == clanKey) {
						return;
					}

					for (int i = 0; i < SocialLists.ignoreListCount; ++i) {
						if (clanKey.equals(StringUtil.displayNameToKey(SocialLists.ignoreList[i]))) {
							return;
						}
					}
				}

				String color = type.color;
				if (this.messageTabSelected != MessageTab.ALL) {
					if ((type == MessageType.FRIEND_STATUS || type == MessageType.PRIVATE_RECIEVE
							|| type == MessageType.PRIVATE_SEND) && this.messageTabSelected != MessageTab.PRIVATE) {
						this.messageTabActivity_Private = 200;
					}

					if (type == MessageType.CHAT && this.messageTabSelected != MessageTab.CHAT) {
						this.messageTabActivity_Chat = 100;
					}

					if (type == MessageType.QUEST && this.messageTabSelected != MessageTab.QUEST
							|| type == MessageType.GLOBAL_CHAT && this.messageTabSelected != MessageTab.QUEST) {
						this.messageTabActivity_Quest = 200;
					}
					if (type == MessageType.CLAN_CHAT && this.messageTabSelected != MessageTab.CLAN) {
						this.messageTabActivity_Clan = 200;
					}

					if (type == MessageType.GAME || type == MessageType.INVENTORY) {
						this.messageTabActivity_Game = 200;
					}

					if (Config.MESSAGE_TAB_SWITCH) {
						if (type == MessageType.GAME && this.messageTabSelected != MessageTab.ALL) {
							this.messageTabSelected = MessageTab.ALL;
						}

						if ((type == MessageType.FRIEND_STATUS || type == MessageType.PRIVATE_RECIEVE
								|| type == MessageType.PRIVATE_SEND) && this.messageTabSelected != MessageTab.PRIVATE
								&& this.messageTabSelected != MessageTab.ALL) {
							this.messageTabSelected = MessageTab.ALL;
						}
					}
				}

				if (null != colorOverride) {
					color = colorOverride;
				}

				for (int i = 99; i > 0; --i) {
					MessageHistory.messageHistoryType[i] = MessageHistory.messageHistoryType[i - 1];
					MessageHistory.messageHistoryTimeout[i] = MessageHistory.messageHistoryTimeout[i - 1];
					MessageHistory.messageHistoryCrownID[i] = MessageHistory.messageHistoryCrownID[i - 1];
					MessageHistory.messageHistorySender[i] = MessageHistory.messageHistorySender[i - 1];
					MessageHistory.messageHistoryClan[i] = MessageHistory.messageHistoryClan[i - 1];
					MessageHistory.messageHistoryMessage[i] = MessageHistory.messageHistoryMessage[i - 1];
					MessageHistory.messageHistoryColor[i] = MessageHistory.messageHistoryColor[i - 1];
				}
				MessageHistory.messageHistoryType[0] = type;
				MessageHistory.messageHistoryTimeout[0] = 300;
				MessageHistory.messageHistorySender[0] = sender;
				MessageHistory.messageHistoryCrownID[0] = crownID;
				MessageHistory.messageHistoryClan[0] = formerName;
				MessageHistory.messageHistoryMessage[0] = message;
				MessageHistory.messageHistoryColor[0] = color;
				String msg = color + StringUtil.formatMessage(message, sender, true, type);

				if (type == MessageType.CHAT) {
					if (this.panelMessageTabs.controlListCurrentSize[this.panelMessageChat]
							- 4 != this.panelMessageTabs.controlScrollAmount[this.panelMessageChat]) {
						this.panelMessageTabs.addToList(msg, false, crownID, sender, formerName, this.panelMessageChat);
					} else {
						this.panelMessageTabs.addToList(msg, true, crownID, sender, formerName, this.panelMessageChat);
					}
				}

				if (type == MessageType.QUEST) {
					if (this.panelMessageTabs.controlScrollAmount[this.panelMessageQuest] != this.panelMessageTabs.controlListCurrentSize[this.panelMessageQuest]
							- 4) {
						this.panelMessageTabs.addToList(msg, false, 0, (String) null, (String) null,
								this.panelMessageQuest);
					} else {
						this.panelMessageTabs.addToList(msg, true, 0, (String) null, (String) null, this.panelMessageQuest);
					}
				}
				if (type == MessageType.GLOBAL_CHAT) {
					if (this.panelMessageTabs.controlScrollAmount[this.panelMessageQuest] != this.panelMessageTabs.controlListCurrentSize[this.panelMessageQuest]
							- 4) {
						this.panelMessageTabs.addToList(msg, false, crownID, sender, formerName, this.panelMessageQuest);
					} else {
						this.panelMessageTabs.addToList(msg, true, crownID, sender, formerName, this.panelMessageQuest);
					}
				}
				if (type == MessageType.CLAN_CHAT) {
					if (this.panelMessageTabs.controlScrollAmount[this.panelMessageClan] != this.panelMessageTabs.controlListCurrentSize[this.panelMessageClan]
							- 4) {
						this.panelMessageTabs.addToList(msg, false, crownID, sender, formerName, this.panelMessageClan);
					} else {
						this.panelMessageTabs.addToList(msg, true, crownID, sender, formerName, this.panelMessageClan);
					}
				}

				if (type == MessageType.PRIVATE_RECIEVE || type == MessageType.PRIVATE_SEND) {
					int crown = crownID;
					if (type != MessageType.PRIVATE_RECIEVE) {
						crown = 0;
					}

					if (this.panelMessageTabs.controlListCurrentSize[this.panelMessagePrivate]
							- 4 != this.panelMessageTabs.controlScrollAmount[this.panelMessagePrivate]) {
						this.panelMessageTabs.addToList(msg, false, crown, sender, formerName, this.panelMessagePrivate);
					} else {
						this.panelMessageTabs.addToList(msg, true, crown, sender, formerName, this.panelMessagePrivate);
					}
				}

			} catch (RuntimeException var12) {
				throw GenUtil.makeThrowable(var12, "client.BD(" + crownEnabled + ',' + (sender != null ? "{...}" : "null")
						+ ',' + "dummy" + ',' + (message != null ? "{...}" : "null") + ',' + type + ',' + crownID + ','
						+ (formerName != null ? "{...}" : "null") + ',' + (colorOverride != null ? "{...}" : "null") + ')');
			}
		}

		private final void sortOnlineFriendsList() {
			try {

				boolean loopModified = true;

				while (loopModified) {
					loopModified = false;
					for (int i = 0; i < SocialLists.friendListCount - 1; ++i) {
						// If we aren't online and the next is cycle down.
						if ((SocialLists.friendListArg[i] & 2) == 0 && (SocialLists.friendListArg[i + 1] & 2) != 0
								|| (4 & SocialLists.friendListArg[i]) == 0 && (SocialLists.friendListArg[1 + i] & 4) != 0) {
							String tmpS = SocialLists.friendListArgS[i];
							SocialLists.friendListArgS[i] = SocialLists.friendListArgS[i + 1];
							SocialLists.friendListArgS[i + 1] = tmpS;

							tmpS = SocialLists.friendList[i];
							SocialLists.friendList[i] = SocialLists.friendList[1 + i];
							SocialLists.friendList[i + 1] = tmpS;

							tmpS = SocialLists.friendListOld[i];
							SocialLists.friendListOld[i] = SocialLists.friendListOld[i + 1];
							SocialLists.friendListOld[i + 1] = tmpS;

							int tmpInt = SocialLists.friendListArg[i];
							SocialLists.friendListArg[i] = SocialLists.friendListArg[i + 1];
							SocialLists.friendListArg[i + 1] = tmpInt;

							loopModified = true;
						}
					}
				}

			} catch (RuntimeException var7) {
				throw GenUtil.makeThrowable(var7, "client.RC(" + "dummy" + ')');
			}
		}

		private ArrayList<XPNotification> xpNotifications = new ArrayList<XPNotification>();
		private NComponent mainComponent;
		private OnlineListInterface onlineList;
		private NCustomComponent experienceOverlay;
		private ProgressBarInterface batchProgressBar;
		private BankPinInterface bankPinInterface;
		private FishingTrawlerInterface fishingTrawlerInterface;
		//private AchievementGUI achievementInterface;
		private IronManInterface ironmanInterface;
		private AuctionHouse auctionHouse;
		private SkillGuideInterface skillGuideInterface;
		private QuestGuideInterface questGuideInterface;
		private ExperienceConfigInterface experienceConfigInterface;
		private DoSkillInterface doSkillInterface;
		private LostOnDeathInterface lostOnDeathInterface;
		private TerritorySignupInterface territorySignupInterface;
		public Clan clan;

		public boolean PAUSED;

		final void startGame(byte var1) {
			try {
				this.fetchContainerSize();
				if (!clientPort.drawLoading(2)) {
					this.errorLoadingData = true;
				} else {
					RSBufferUtils.setStringEncryptor(RSBufferUtils.encryption);
					int var10 = 0;

					for (int var3 = 0; var3 < 99; ++var3) {
						int var4 = 1 + var3;
						int var5 = (int) (300.0D * Math.pow(2.0D, (double) var4 / 7.0D) + (double) var4);
						var10 += var5;
						this.experienceArray[var3] = FastMath.bitwiseAnd(var10, 268435452) / 4;
					}

					MiscFunctions.maxReadTries = 1000;
					this.loadGameConfig(false);
					if (!this.errorLoadingData) {
						// mudclient.spriteMedia = 2000;
						// mudclient.spriteUtil = mudclient.spriteMedia + 100;
						// mudclient.spriteItem = 50 + mudclient.spriteUtil;
						// mudclient.spriteLogo = 1000 + mudclient.spriteItem;
						// mudclient.spriteProjectile = 10 + mudclient.spriteLogo;
						// mudclient.spriteTexture = 50 +
						// mudclient.spriteProjectile;

						// this.graphics = this.getGraphics();
						this.setFPS((int) 50, (byte) 107);
						this.setSurface(new MudClientGraphics(this.getGameWidth(), this.getGameHeight() + 12, 4501));

						clientPort.initGraphics();
						getSurface().setIconsStart(0, 3284);
						this.getSurface().mudClientRef = this;
						this.getSurface().setClip(0, this.getGameWidth(), this.getGameHeight() + 12, 0);

						bank = new BankInterface(this);
						auctionHouse = new AuctionHouse(this);
						skillGuideInterface = new SkillGuideInterface(this);
						questGuideInterface = new QuestGuideInterface(this);
						experienceConfigInterface = new ExperienceConfigInterface(this);
						doSkillInterface = new DoSkillInterface(this);
						lostOnDeathInterface = new LostOnDeathInterface(this);
						territorySignupInterface = new TerritorySignupInterface(this);

						mainComponent = new NComponent(this);
						mainComponent.setSize(getGameWidth(), getGameHeight());

						bankPinInterface = new BankPinInterface(this);
						mainComponent.addComponent(bankPinInterface);

						ironmanInterface = new IronManInterface(this);

						fishingTrawlerInterface = new FishingTrawlerInterface(this);
						mainComponent.addComponent(fishingTrawlerInterface);

						batchProgressBar = new ProgressBarInterface(this);
						mainComponent.addComponent(batchProgressBar.getComponent());

						onlineList = new OnlineListInterface(this);
						mainComponent.addComponent(onlineList);

						//achievementInterface = new AchievementGUI(this);
						clan = new Clan(this);

						experienceOverlay = new NCustomComponent(this) {
							@Override
							public void render() {
								if (Config.EXPERIENCE_DROPS) {
									long new_time = System.currentTimeMillis();
									time = new_time;
									for (Iterator<XPNotification> iterator = xpNotifications.iterator(); iterator.hasNext();) {
										XPNotification xpdrop = iterator.next();
										if (!xpdrop.isActive) {
											if (Config.EXPERIENCE_COUNTER > 0) {
												if(time > m_timer && xpdrop.y > 20) {
													m_timer = time + 250;
													xpdrop.isActive = true;
												} else {
													return;
												}
											} else {
												if(time > m_timer && xpdrop.y > 0) {
													m_timer = time + 250;
													xpdrop.isActive = true;
												} else {
													return;
												}
											}
										}

										if (Config.EXPERIENCE_COUNTER == 1) {
											drawExperienceCounter(xpdrop.skill);
										}

										int textColor = Config.EXPERIENCE_COUNTER_COLOR == 0 ? 0xFFFFFF :
											Config.EXPERIENCE_COUNTER_COLOR == 1 ? 0xFFFF00 :
												Config.EXPERIENCE_COUNTER_COLOR == 2 ? 0xFF0000 :
													Config.EXPERIENCE_COUNTER_COLOR == 3 ? 0x0000FF : 0x00FF00;

										if (!xpdrop.levelUp) {
											if (textColor == 0xFFFFFF) {
												graphics().drawShadowText("+" + xpdrop.amount + " " + getSkillNames()[xpdrop.skill] + " exp", xpdrop.x,
														(int) xpdrop.y, textColor, 2, false);
											} else {
												graphics().drawString("+" + xpdrop.amount + " " + getSkillNames()[xpdrop.skill] + " exp", xpdrop.x,
													(int) xpdrop.y, textColor, 2);
											}
										} else {
											if (textColor == 0xFFFFFF) {
												graphics().drawShadowText("+1 " + getSkillNames()[xpdrop.skill] + " level", xpdrop.x,
													(int) xpdrop.y, textColor, 2, false);
											} else {
												graphics().drawString("+1 " + getSkillNames()[xpdrop.skill] + " level", xpdrop.x,
														(int) xpdrop.y, textColor, 2);
											}
										}

										double dropSpeed = Config.EXPERIENCE_DROP_SPEED == 0 ? 0.000000000001 :
											Config.EXPERIENCE_DROP_SPEED == 1 ? 0.00005 : 1;
										xpdrop.y -= dropSpeed;

										if (Config.EXPERIENCE_COUNTER > 0 && xpdrop.y <= 30) {
											xpdrop.isActive = false;
										}
										else if (xpdrop.y <= 0) {
											xpdrop.isActive = false;
										}


										if (Config.EXPERIENCE_COUNTER > 0 && (xpdrop.y <= 30 || xpdrop.y > getGameHeight() - 30)) {
											iterator.remove();
										}
										else if (xpdrop.y <= 0 || xpdrop.y > getGameHeight()) {
											iterator.remove();
										}
									}
								}
							}
						};
						mainComponent.addComponent(experienceOverlay);

						this.menuCommon = new Menu(this.getSurface(), Config.isAndroid() ? Config.MENU_SIZE : 1, "Choose option");

						this.menuTrade = new Menu(this.getSurface(), 1);
						this.menuDuel = new Menu(this.getSurface(), 1);
						MiscFunctions.drawBackgroundArrow = false;
						this.panelMagic = new Panel(this.getSurface(), 5);
						int var3 = this.getSurface().width2 - 199;
						byte var12 = 36;
						this.controlMagicPanel = this.panelMagic.addScrollingList(var3, 24 + var12, 196, 90, 500, 1, true);
						this.panelSocial = new Panel(this.getSurface(), 5);
						this.controlSocialPanel = this.panelSocial.addScrollingList(var3, var12 + 40, 196, 126, 500, 1,
								true);
						this.panelClan = new Panel(this.getSurface(), 5);
						this.controlClanPanel = this.panelClan.addScrollingList(var3, var12 + 72, 196, 128, 500, 1,
								true);
						this.panelPlayerInfo = new Panel(this.getSurface(), 5);
						this.controlPlayerInfoPanel = this.panelPlayerInfo.addScrollingList(var3, 24 + var12, 196, 263, 500,
								1, true);
						this.panelQuestInfo = new Panel(this.getSurface(), 5);
						this.controlQuestInfoPanel = this.panelQuestInfo.addScrollingList(var3, 24 + var12, 196, 263, 500,
								1, true);
						/*this.panelPlayerTaskInfo = new Panel(this.getSurface(), 5);
						this.controlPlayerTaskInfoPanel = this.panelPlayerTaskInfo.addScrollingList(var3, 24 + var12 + 27, 196, 224, 500,
								7, true);*/
						this.panelSettings = new Panel(this.getSurface(), 5);
						this.controlSettingPanel = this.panelSettings.addScrollingList3(var3 + 1, 24 + var12 + 16, 195, 184, 500,
								1, true, 1, 2);

						this.loadMedia((byte) -49);
						if (!this.errorLoadingData) {
							this.loadEntities(true);
							if (!this.errorLoadingData) {
								this.scene = new Scene(this.getSurface(), 25000, 25000, 1000);
								this.scene.setMidpoints(this.getGameHeight() / 2, true, this.getGameWidth(),
										this.getGameWidth() / 2, this.getGameHeight() / 2, this.m_qd,
										this.getGameWidth() / 2);
								this.scene.fogLandscapeDistance = 2400;
								this.scene.fogEntityDistance = 2400;
								this.scene.fogSmoothingStartDistance = 2300;
								this.scene.fogZFalloff = 1;
								this.scene.setDiffuseDir(-50, -10, true, -50);
								this.world = new World(this.scene, this.getSurface());
								this.world.baseMediaSprite = mudclient.spriteMedia;
								this.loadTextures((byte) 91);
								if (!this.errorLoadingData) {
									this.loadModels(true);
									if (!this.errorLoadingData) {
										if (!this.errorLoadingData) {
											//this.loadSounds();
											if (!this.errorLoadingData) {
												clientPort.showLoadingProgress(100, "Starting game...");
												this.createMessageTabPanel(56);
												this.createLoginPanels((int) 3845);
												this.createAppearancePanel(var1 ^ 24649);
												this.resetLoginScreenVariables((byte) -88);
												this.renderLoginScreenViewports(-116);
											}
										}
									}
								}
							}
						}
					}
				}
			} catch (RuntimeException var9) {
				throw GenUtil.makeThrowable(var9, "client.KC(" + var1 + ')');
			}
		}

		private final void tradeOffer(int count, int slot) {
			try {

				boolean offerSuccess = false;
				int offered = 0;
				int id = this.inventoryItemID[slot];

				for (int tSlot = 0; this.tradeItemCount > tSlot; ++tSlot) {
					if (id == this.tradeItemID[tSlot]) {
						if (EntityHandler.getItemDef(id).isStackable()) {
							if (count >= 0) {
								this.tradeItemSize[tSlot] += count;
								if (this.tradeItemSize[tSlot] > this.inventoryItemSize[slot]) {
									this.tradeItemSize[tSlot] = this.inventoryItemSize[slot];
								}

								offerSuccess = true;
							} else {
								for (int j = 0; j < this.mouseButtonItemCountIncrement; ++j) {
									offerSuccess = true;
									if (this.inventoryItemSize[slot] > this.tradeItemSize[tSlot]) {
										++this.tradeItemSize[tSlot];
									}
								}
							}
						} else {
							++offered;
						}
					}
				}

				int invAvailable = this.getInventoryCount((int) id);
				if (invAvailable <= offered) {
					offerSuccess = true;
				}

				if (EntityHandler.getItemDef(id).quest && !adminRights) {
					offerSuccess = true;
					this.showMessage(false, (String) null, "This object cannot be traded with other players",
							MessageType.GAME, 0, (String) null, (String) null);
				}

				if (!offerSuccess) {
					if (count < 0) {
						if (this.tradeItemCount < 12) {
							this.tradeItemID[this.tradeItemCount] = id;
							this.tradeItemSize[this.tradeItemCount] = 1;
							offerSuccess = true;
							++this.tradeItemCount;
						}
					} else {
						for (int l = 0; l < count && this.tradeItemCount < 12 && invAvailable > offered; ++l) {
							this.tradeItemID[this.tradeItemCount] = id;
							this.tradeItemSize[this.tradeItemCount] = 1;
							offerSuccess = true;
							++offered;
							++this.tradeItemCount;
							if (l == 0 && EntityHandler.getItemDef(id).isStackable()) {
								this.tradeItemSize[this.tradeItemCount - 1] = count <= this.inventoryItemSize[slot] ? count
										: this.inventoryItemSize[slot];
								break;
							}
						}
					}
				}

				if (offerSuccess) {
					this.getClientStream().newPacket(46);
					this.getClientStream().writeBuffer1.putByte(this.tradeItemCount);

					for (int i = 0; this.tradeItemCount > i; ++i) {
						this.getClientStream().writeBuffer1.putShort(this.tradeItemID[i]);
						this.getClientStream().writeBuffer1.putInt(this.tradeItemSize[i]);
					}

					this.getClientStream().finishPacket();
					this.tradeRecipientAccepted = false;
					this.tradeAccepted = false;
				}

			} catch (RuntimeException var9) {
				throw GenUtil.makeThrowable(var9, "client.PA(" + count + ',' + "dummy" + ',' + slot + ')');
			}
		}

		private final void tradeRemove(int var1, byte var2, int var3) {
			try {

				int var4 = this.tradeItemID[var3];
				int var5 = var1 < 0 ? this.mouseButtonItemCountIncrement : var1;
				int var6;
				if (!EntityHandler.getItemDef(var4).isStackable()) {
					var6 = 0;
					for (int var7 = 0; var7 < this.tradeItemCount && var6 < var5; ++var7) {
						if (var4 == this.tradeItemID[var7]) {
							++var6;
							--this.tradeItemCount;

							for (int var8 = var7; this.tradeItemCount > var8; ++var8) {
								this.tradeItemID[var8] = this.tradeItemID[var8 + 1];
								this.tradeItemSize[var8] = this.tradeItemSize[var8 + 1];
							}

							--var7;
						}
					}
				} else {
					this.tradeItemSize[var3] -= var5;
					if (this.tradeItemSize[var3] <= 0) {
						--this.tradeItemCount;

						for (var6 = var3; var6 < this.tradeItemCount; ++var6) {
							this.tradeItemID[var6] = this.tradeItemID[1 + var6];
							this.tradeItemSize[var6] = this.tradeItemSize[var6 + 1];
						}
					}
				}

				this.getClientStream().newPacket(46);
				if (var2 > 120) {
					this.getClientStream().writeBuffer1.putByte(this.tradeItemCount);

					for (var6 = 0; var6 < this.tradeItemCount; ++var6) {
						this.getClientStream().writeBuffer1.putShort(this.tradeItemID[var6]);
						this.getClientStream().writeBuffer1.putInt(this.tradeItemSize[var6]);
					}

					this.getClientStream().finishPacket();
					this.tradeAccepted = false;
					this.tradeRecipientAccepted = false;
				}
			} catch (RuntimeException var9) {
				throw GenUtil.makeThrowable(var9, "client.A(" + var1 + ',' + var2 + ',' + var3 + ')');
			}
		}

		final void update() {
			try {

				if (!this.errorLoadingCoadebase) {
					if (!this.errorLoadingMemory) {
						if (!this.errorLoadingData) {

							try {
								++this.frameCounter;
								if (this.currentViewMode == GameMode.LOGIN) {
									this.lastMouseAction = 0;
									this.handleLoginScreenInput(2);
								}

								if (this.currentViewMode == GameMode.GAME) {
									++this.lastMouseAction;
									this.handleGameInput();
								}

								this.lastMouseButtonDown = 0;
								++this.m_oj;
								if (this.m_oj > 500) {
									this.m_oj = 0;
									int var2 = (int) (4.0D * Math.random());
									if ((2 & var2) == 2) {
										this.cameraRotationZ += this.m_Ok;
									}

									if ((var2 & 1) == 1) {
										this.cameraRotationX += this.m_eg;
									}
								}

								if (this.cameraRotationX < -50) {
									this.m_eg = 2;
								}

								if (this.cameraRotationZ < -50) {
									this.m_Ok = 2;
								}

								if (this.cameraRotationX > 50) {
									this.m_eg = -2;
								}

								if (this.messageTabActivity_Private > 0) {
									--this.messageTabActivity_Private;
								}
								if (this.messageTabActivity_Clan > 0) {
									--this.messageTabActivity_Clan;
								}

								if (this.messageTabActivity_Quest > 0) {
									--this.messageTabActivity_Quest;
								}

								if (this.messageTabActivity_Game > 0) {
									--this.messageTabActivity_Game;
								}

								if (this.messageTabActivity_Chat > 0) {
									--this.messageTabActivity_Chat;
								}

								if (this.cameraRotationZ > 50) {
									this.m_Ok = -2;
								}
							} catch (OutOfMemoryError var3) {
								var3.printStackTrace();
								this.errorLoadingMemory = true;
							}

						}
					}
				}
			} catch (RuntimeException var4) {
				throw GenUtil.makeThrowable(var4, "client.MA(" + "dummy" + ')');
			}
		}

//		private final void updateBankItems(int var1) {
//			try {
//
//				this.bankItemCount = this.newBankItemCount;
//
//				int var2;
//				for (var2 = 0; var2 < this.newBankItemCount; ++var2) {
//					this.bankItemID[var2] = this.newBankItems[var2];
//					this.bankItemSize[var2] = this.newBankItemsCount[var2];
//				}
//
//			} catch (RuntimeException var6) {
//				throw GenUtil.makeThrowable(var6, "client.WB(" + var1 + ')');
//			}
//		}

		private final void updateObjectAnimation(byte var1, int instanceNumber, String modelFileName) {
			try {

				int tileX = this.gameObjectInstanceX[instanceNumber];
				int tileZ = this.gameObjectInstanceZ[instanceNumber];
				int pixX = tileX - this.localPlayer.currentX / 128;
				int pixZ = tileZ - this.localPlayer.currentZ / 128;
				byte var8 = 7;
				if (var1 > 2) {
					if (tileX >= 0 && tileZ >= 0 && tileX < 96 && tileZ < 96 && pixX > -var8 && pixX < var8 && pixZ > -var8
							&& pixZ < var8) {
						this.scene.removeModel(this.gameObjectInstanceModel[instanceNumber]);
						int modelFileIndex = EntityHandler.storeModel(modelFileName);
						RSModel model = this.modelCache[modelFileIndex].clone();
						this.scene.addModel(model);
						model.setDiffuseLightAndColor(-50, -10, -50, 48, 48, true, -74);
						model.copyRot256AndTranslateFrom(this.gameObjectInstanceModel[instanceNumber], 6029);
						model.key = instanceNumber;
						this.gameObjectInstanceModel[instanceNumber] = model;
					}

				}
			} catch (RuntimeException var11) {
				throw GenUtil.makeThrowable(var11, "client.FD(" + var1 + ',' + instanceNumber + ','
						+ (modelFileName != null ? "{...}" : "null") + ')');
			}
		}

		private final void walkToActionSource(int startX, int startZ, int destX, int destZ, boolean var5) {
			try {

				this.walkToArea(startX, startZ, destX, destZ, destX, destZ, false, var5);
			} catch (RuntimeException var8) {
				throw GenUtil.makeThrowable(var8, "client.BE(" + destZ + ',' + destX + ',' + startZ + ',' + startX + ','
						+ var5 + ',' + "dummy" + ')');
			}
		}

		private final boolean walkToArea(int startX, int startZ, int x1, int z1, int x2, int z2, boolean reachBorder,
				boolean var2) {
			try {

				int count = this.world.findPath(this.pathX, this.pathZ, startX, startZ, x1, x2, z1, z2, reachBorder);
				if (count == -1) {
					if (!var2) {
						return false;
					}

					count = 1;
					this.pathX[0] = x1;
					this.pathZ[0] = z1;
				}

				--count;
				startZ = this.pathZ[count];
				startX = this.pathX[count];
				--count;
				if (!var2) {
					this.getClientStream().newPacket(187);
				} else {
					this.getClientStream().newPacket(16);
				}

				this.getClientStream().writeBuffer1.putShort(this.midRegionBaseX + startX);
				this.getClientStream().writeBuffer1.putShort(this.midRegionBaseZ + startZ);
				if (var2 && count == -1 && (startX + this.midRegionBaseX) % 5 == 0) {
					count = 0;
				}

				for (int i = count; i >= 0 && i > count - 25; --i) {
					this.getClientStream().writeBuffer1.putByte(this.pathX[i] - startX);
					this.getClientStream().writeBuffer1.putByte(this.pathZ[i] - startZ);
				}

				this.getClientStream().finishPacket();
				this.mouseWalkY = this.mouseY;
				this.mouseWalkX = this.mouseX;
				this.mouseClickXStep = -24;
				return true;
			} catch (RuntimeException var13) {
				throw GenUtil.makeThrowable(var13, "client.DD(" + x1 + ',' + var2 + ',' + startX + ',' + z1 + ',' + startZ
						+ ',' + x2 + ',' + reachBorder + ',' + z2 + ',' + "dummy" + ')');
			}
		}

		private final void walkToGroundItem(int startX, int startZ, int destX, int destZ, boolean var5) {
			try {

				if (!this.sendWalkToGroundItem(startX, startZ, destX, destX, destZ, destZ, false, var5)) {
					this.walkToArea(startX, startZ, destX, destZ, destX, destZ, true, var5);
				}
			} catch (RuntimeException var8) {
				throw GenUtil.makeThrowable(var8, "client.WC(" + "dummy" + ',' + startZ + ',' + destZ + ',' + destX + ','
						+ var5 + ',' + startX + ')');
			}
		}

		private final void walkToObject(int destX, int destZ, int dir, int var1, int objectID) {
			try {

				int worldWidth;
				int worldHeight;
				if (dir != 0 && dir != 4) {
					worldWidth = EntityHandler.getObjectDef(objectID).getHeight();
					worldHeight = EntityHandler.getObjectDef(objectID).getWidth();
				} else {
					worldHeight = EntityHandler.getObjectDef(objectID).getHeight();
					worldWidth = EntityHandler.getObjectDef(objectID).getWidth();
				}

				if (EntityHandler.getObjectDef(objectID).getType() != 2
						&& EntityHandler.getObjectDef(objectID).getType() != 3) {
					this.walkToArea(this.playerLocalX, this.playerLocalZ, destX, destZ, worldWidth - 1 + destX,
							destZ + worldHeight - 1, true, true);
				} else {
					if (dir == 0) {
						++worldWidth;
						--destX;
					}

					if (dir == 2) {
						++worldHeight;
					}

					if (dir == 6) {
						--destZ;
						++worldHeight;
					}

					if (dir == 4) {
						++worldWidth;
					}

					this.walkToArea(this.playerLocalX, this.playerLocalZ, destX, destZ, worldWidth + (destX - 1),
							worldHeight + destZ - 1, false, true);
				}

			} catch (RuntimeException var8) {
				throw GenUtil.makeThrowable(var8,
						"client.OD(" + 5126 + ',' + objectID + ',' + destX + ',' + destZ + ',' + dir + ')');
			}
		}

		private final void walkToWall(int x, int z, int dir) {
			try {

				if (dir == 0) {
					this.walkToArea(this.playerLocalX, this.playerLocalZ, x, z - 1, x, z, false, true);
				} else if (dir != 1) {
					this.walkToArea(this.playerLocalX, this.playerLocalZ, x, z, x, z, true, true);
				} else {
					this.walkToArea(this.playerLocalX, this.playerLocalZ, x - 1, z, x, z, false, true);
				}
			} catch (RuntimeException var6) {
				throw GenUtil.makeThrowable(var6, "client.QD(" + "dummy" + ',' + x + ',' + z + ',' + dir + ')');
			}
		}

		public MudClientGraphics getSurface() {
			return surface;
		}

		public void setSurface(MudClientGraphics surface) {
			this.surface = surface;
		}

		public int getGameWidth() {
			return gameWidth;
		}

		public int getGameHeight() {
			return gameHeight;
		}

		public void setGameHeight(int gameHeight) {
			this.gameHeight = gameHeight;
		}

		public int[][] getAnimDirLayer_To_CharLayer() {
			return animDirLayer_To_CharLayer;
		}

		public int getPlayerStatCurrent(int skill) {
			return playerStatCurrent[skill];
		}

		public int[] getPlayerHairColors() {
			return playerHairColors;
		}

		public int[] getPlayerClothingColors() {
			return playerClothingColors;
		}

		public int[] getPlayerSkinColors() {
			return playerSkinColors;
		}

		public RSCharacter getLocalPlayer() {
			return localPlayer;
		}

		public int getMouseX() {
			return mouseX;
		}

		public int getMouseY() {
			return mouseY;
		}

		public int getMouseClick() {
			return mouseButtonClick;
		}

		public int getMouseButtonDown() {
			return currentMouseButtonDown;
		}

		public int getMouseButtonDownTime() {
			return mouseButtonDownTime;
		}

		public int getBankItemCount() {
			return bankItemCount;
		}

		public int[] getBankItems() {
			return bankItemID;
		}

		public int[] getBankItemsCount() {
			return bankItemSize;
		}

		public int getInventoryItemCount() {
			return inventoryItemCount;
		}

		public int[] getInventoryItemEquipped() {
			return inventoryItemEquipped;
		}

		public int[] getInventoryItems() {
			return inventoryItemID;
		}

		public int[] getInventoryItemsCount() {
			return inventoryItemSize;
		}

		public static final String formatStackAmount(int length) {
			if (length < 100000) {
				return String.valueOf(length);
			}
			if (length < 10000000) {
				return "@whi@" + String.valueOf(length / 1000) + "K";
			}
			return "@gre@" + String.valueOf(length / 1000000) + "M";
		}

		public void setMouseClick(int i) {
			this.mouseButtonClick = i;
		}

		public void setMouseButtonDown(int i) {
			this.currentMouseButtonDown = i;
		}

		public int getLastMouseDown() {
			return lastMouseButtonDown;
		}

		public Network_Socket getClientStream() {
			return clientStream;
		}

		public void setClientStream(Network_Socket clientStream) {
			this.clientStream = clientStream;
		}

		public String[] getSkillNames() {
			return skillNames;
		}

		public String[] getSkillNamesLong() {
			return skillNameLong;
		}

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public int getGameState() {
			return gameState;
		}

		private String skillGuideChosen;
		public ArrayList<String> skillGuideChosenTabs;

		private void drawSkillGuide() {
			skillGuideInterface.onRender(this.getSurface());
		}

		public String getSkillGuideChosen() {
			return skillGuideChosen;
		}

		public void setSkillGuideChosen(String skillGuideChosen) {
			this.skillGuideChosen = skillGuideChosen;
			skillGuideChosenTabs = new ArrayList<String>();
			if (skillGuideChosen.equalsIgnoreCase("Attack")) {
				skillGuideChosenTabs.add("Weapons");
			} else if (skillGuideChosen.equalsIgnoreCase("Defense")) {
				skillGuideChosenTabs.add("Armour");
			} else if (skillGuideChosen.equalsIgnoreCase("Hits")) {
				skillGuideChosenTabs.add("Food");
			} else if (skillGuideChosen.equalsIgnoreCase("Ranged")) {
				skillGuideChosenTabs.add("Bows");
				skillGuideChosenTabs.add("Crossbows");
				skillGuideChosenTabs.add("Thrown");
			} else if (skillGuideChosen.equalsIgnoreCase("Prayer")) {
				skillGuideChosenTabs.add("Prayers");
			} else if (skillGuideChosen.equalsIgnoreCase("Magic")) {
				skillGuideChosenTabs.add("Spells");
				skillGuideChosenTabs.add("Armour");
				skillGuideChosenTabs.add("Weapons");
			} else if (skillGuideChosen.equalsIgnoreCase("Cooking")) {
				skillGuideChosenTabs.add("Meats");
				skillGuideChosenTabs.add("Pizzas");
				skillGuideChosenTabs.add("Pies");
				skillGuideChosenTabs.add("Stews");
				skillGuideChosenTabs.add("Bread");
				skillGuideChosenTabs.add("Cake");
				skillGuideChosenTabs.add("Gnome");
				skillGuideChosenTabs.add("Other");
			} else if (skillGuideChosen.equalsIgnoreCase("Woodcutting")) {
				skillGuideChosenTabs.add("Trees");
				skillGuideChosenTabs.add("Axes");
			} else if (skillGuideChosen.equalsIgnoreCase("Fletching")) {
				skillGuideChosenTabs.add("Ammo");
				skillGuideChosenTabs.add("Bows");
				skillGuideChosenTabs.add("Darts");
			} else if (skillGuideChosen.equalsIgnoreCase("Fishing")) {
				skillGuideChosenTabs.add("Catches");
			} else if (skillGuideChosen.equalsIgnoreCase("Firemaking")) {
				skillGuideChosenTabs.add("Burning");
			} else if (skillGuideChosen.equalsIgnoreCase("Crafting")) {
				skillGuideChosenTabs.add("Leather");
				skillGuideChosenTabs.add("Pottery");
				skillGuideChosenTabs.add("Gems");
				skillGuideChosenTabs.add("Jewelry");
				skillGuideChosenTabs.add("Spinning");
				skillGuideChosenTabs.add("Glass");
				skillGuideChosenTabs.add("Battlestaves");
			} else if (skillGuideChosen.equalsIgnoreCase("Smithing")) {
				skillGuideChosenTabs.add("Smelting");
				skillGuideChosenTabs.add("Bronze");
				skillGuideChosenTabs.add("Iron");
				skillGuideChosenTabs.add("Steel");
				skillGuideChosenTabs.add("Mithril");
				skillGuideChosenTabs.add("Adamantite");
				skillGuideChosenTabs.add("Runite");
				skillGuideChosenTabs.add("Other");
			} else if (skillGuideChosen.equalsIgnoreCase("Mining")) {
				skillGuideChosenTabs.add("Ores");
				skillGuideChosenTabs.add("Pickaxes");
			} else if (skillGuideChosen.equalsIgnoreCase("Herblaw")) {
				skillGuideChosenTabs.add("Herbs");
				skillGuideChosenTabs.add("Potions");
			} else if (skillGuideChosen.equalsIgnoreCase("Agility")) {
				skillGuideChosenTabs.add("Courses");
				skillGuideChosenTabs.add("Shortcuts");
			} else if (skillGuideChosen.equalsIgnoreCase("Thieving")) {
				skillGuideChosenTabs.add("Pickpocket");
				skillGuideChosenTabs.add("Stalls");
				skillGuideChosenTabs.add("Chests");
				skillGuideChosenTabs.add("Doors");
			}
		}

		private String questGuideChosen;
		private int questGuideProgress;
		private String questGuideStartWho;
		private String questGuideStartWhere;
		private String[] questGuideStartWhos = {"Sir Amik Varze", "the Cook", "the Gypsy", "Doric", "the Priest", "the Bertender", "Veronica", "Wizard Mizgog", "Redbeard Frank", "Chancellor Hassan", "Romeo", "Fred the Farmer", "Reldo", "the Squire", "Morgan", "Hetty", "the Guildmaster", "a boy", "the Adventurers", "Achetties", "Kaqemeex", "King Arthur", "Thormac", "Dimintheis", "Kangai Mau", "a mountain dwarf", "Brother Omad", "Lucien", "Brother Kojo", "King Arthur", "Lady Servil", "Bolren", "Ceril Carnillean", "Councillor Halgrive", "Edmond", "Caroline", "Almera", "Elena", "Trufitus", "King Narnode Shareen", "Mosol Rei", "King Lathas", "Observatory Professor", "Irena", "Watchtower Wizard", "Captain Lawgof", "a Gaurd", "an Examiner", "Gertrude", "Sir Radimus Erkle"};
		private String[] questGuideStartWheres = {"on the first floor of the White Knight's Castle in Falador", "on the first floor of Lumbridge Castle", "in Varrock Square", "north of Falador", "in the Lumbridge church", "inside the Rusty Anchor bar in Port Sarim", "outside of Draynor Manor", "on the top floor of the Wizard's Tower", "in Port Sarim", "inside Al-Kharid palace", "in Varrock Square", "north of Lumbridge", "in the Varrock Palace Library", "on the White Knight Castle grounds in Falador", "in Draynor Village", "in Rimmington", "inside the Champion's Guild", "in Taverly", "in the Lumbridge swamp", "outside of the Heroes' Guild north of Taverly", "at the Druid's Stone Circle in Taverly", "in Camelot", "on the top floor fo the Sorcerer's Tower south of Seer's Village", "in eastern Varrock", "in The Shrimp and Parrot pub in Brimhaven", "on either side of the White Wolf Mountain passage", "in the Monastery south of East Ardougne", "in the Flying Horse Inn on the western end of East Ardougne", "inside the Clock Tower south of East Ardougne", "in Camelot", "west of Port Khazard", "in Tree Gnome Village", "south of the Ardougne Castle", "outside of the East Ardougne church", "north of the Ardougne Castle", "east of Ardougne", "northeast of Baxtorian Falls", "north of the Ardougne Castle", "north-east of Tai Bwo Wannai", "in the Grand Tree", "outside of Shilo Village in southern Karamja", "on the ground floor of Ardougne Castle", "in the Observatory reception room west of the Tree Gnome Village", "outside the Shantay Pass in the Kharidian desert", "at the top of the Watchtower north of Yanille", "far north-east of Seer's Village", "in the Sinclair Mansion north of Camelot", "in the Exam Centre south of the Digsite", "at her house west of Varrock", "inside the Legend's Guild"};
		private String[] questGuideRequirement;
		private String[] questGuideReward;
		private String[][] questGuideRequirements = { {"12 quest points"}, {"None"}, {"Ability to defeat a level 30 demon"}, {"None"}, {"None"}, {"None"}, {"None"}, {"None"}, {"None"}, {"None"}, {"None"}, {"None"}, {"A friend to assist"}, {"10 Mining", "A friend to assist"}, {"Ability to defeat a level 43 vampire"}, {"None"}, {"32 Quest Points", "33 Magic", "The ability to defeat a level 110 dragon"}, {"Ability to defeat a level 54 shapeshifter"}, {"31 Crafting", "36 Woodcutting", "Ability to defeat a level 95 monster"}, {"Completed Shield of Arrav, Dragon Slayer, Merlin's Crystal, and Lost City", "56 Quest Points", "53 Cooking", "53 Fishing", "25 Herblaw", "50 Mining"}, {"None"}, {"Ability to defeat a level 58 knight", "A friend to assist"}, {"Completed the Barbarian Bar Crawl", "31 Prayer"}, {"40 Mining", "40 Smithing", "40 Crafting", "59 Magic"}, {"21 Thieving"}, {"10 Fishing"}, {"None"}, {"42 Thieving", "35 Ranged", "Ability to defeat a level 63 monster with ranged"}, {"None"}, {"Completed Merlin's Crystal", "20 Attack", "Ability to defeat a level 146 Black Knight Titan"}, {"Ability to defeat a level 122 monster"}, {"None"}, {"None"}, {"None"}, {"None"}, {"30 Firemaking"}, {"None"}, {"Completed Plague City"}, {"Completed Druidic Ritual", "3 Herblaw"}, {"25 Agility", "Ability to defeat a level 184 monster"}, {"Completed Jungle Potion", "32 Agility", "20 Crafting", "4 Smithing", "Ability to defeat a level 83 monsters"}, {"Completed Biohazard", "25 Ranged"}, {"10 Crafting"}, {"10 Fletching", "10 Smithing", "Ability to defeat a level 47 enemy"}, {"40 Mining", "30 Agility", "15 Thieving", "14 Herblaw", "14 Magic", "Ability to defeat a level 68 ogre"}, {"None"}, {"None"}, {"Completed Druidic Ritual", "25 Thieving", "10 Agility", "10 Herblaw"}, {"None"}, {"108 Quest Points", "50 Agility", "50 Crafting", "45 Herblaw", "56 Magic", "52 Mining", "42 Prayer", "50 Smithing", "50 Strength", "50 Thieving", "50 Woodcutting", "Ability to defeat a level 172 demon"} };
		private String[][] questGuideRewards = { {"3 Quest Points", "2500 coins"}, {"1 Quest Point", "300 Cooking experience", "Access to the Cook's range"}, {"3 Quest Points", "Silverlight"}, {"1 Quest Point", "(Lvl + 1)*75 + 100 Mining experience", "Ability to use Doric's anvils", "180 coins", }, {"1 Quest Point", "1125 Prayer experience", "Amulet of Ghostspeak"}, {"5 Quest Points", "Lvl*30 + 20 Crafting experience", "1 Gold bar"}, {"4 Quest Points", "300 coins"}, {"1 Quest Point", "875 Magic experience", "An amulet of accuracy"}, {"2 Quest Points", "450 coins", "A gold ring", "An emerald"}, {"3 Quest points", "Free passage through the Al-Kharid tollgate", "700 coins"}, {"5 Quest Points"}, {"1 Quest Point", "180 Crafting experience", "180 coins"}, {"1 Quest Point", "600 coins"}, {"1 Quest Point", "Lvl*375 + 350 Smithing experience"}, {"3 Quest Points", "4825 Attack experience"}, {"1 Quest Point", "Lvl*50 + 225 Magic experience"}, {"2 Quest Points", "Lvl*300 + 1000 Defense experience", "Lvl*300 + 1000 Strength experience", "The ability to wear a Rune plate mail body"},  {"4 Quest Points", "Lvl*125 + 200 Hits experience"}, {"3 Quest Points", "Ability to enter the city of Zanaris", "Ability to wield a Dragon sword"}, {"1 Quest Point", "Lvl*50 + 75 experience in the following skills: Attack, Defense, Hits, Strength, Cooking, Fishing, Mining, Smithing, Ranged, Firemaking, Woodcutting, and Herblaw", "Access to the Heroes' Guild", "Ability to wield the Dragon axe"}, {"4 Quest Points", "250 Herblaw experience", "Ability to use the Herblaw skill"}, {"6 Quest Points", "Excalibur"}, {"1 Quest Point", "6625 Strength experience", "Thormac will enchant your battlestaves for 40000 coins"}, {"1 Quest Point", "A pair of Steel gauntlets"}, {"1 Quest Point", "Lvl*75 + 200 Thieving experience", "5 swordfish"}, {"1 Quest Point", this.playerStatBase[10] < 24 ? "(Lvl - 10)*75 + 975 Fishing experience" : "(Lvl - 24)*75 + 2225 Fishing experience", "Access to the underground tunnel beneath White Wolf Mountain"}, {"1 Quest Point", "(Lvl + 1)*125 Woodcutting experience", "8 Law-Runes"}, {"1 Quest Point", "Lvl*250 + 500 experience in Ranged and Fletching"}, {"1 Quest Point", "500 coins"}, {"2 Quest Points", "(Lvl + 1)*300 Defense experience", "(Lvl + 1)*250 Prayer experience"}, {"2 Quest Points", "Lvl*200 + 175 experience in Attack and Thieving", "1000 coins"}, {"2 Quest Points", "Lvl*75 + 6950 Attack experience", "A Gnome amulet of protection", "Ability to use Spirit Trees"}, {"1 Quest Point", "Lvl*57 + 205 Thieving experience", "2000 coins"}, {"4 Quest Points", "3100 coins"}, {"1 Quest Point", "Lvl*75 + 175 Mining experience", "A magic scroll granting the ability to cast Ardougne teleport"}, {"1 Quest Point", "Lvl*200 + 175 Fishing experience", "1 Oyster pearls"}, {"1 Quest Point", "Lvl*225 + 250 experience in Attack and Strength", "40 Mithril seeds", "2 Diamonds", "2 Gold bars"}, {"3 Quest Points", "Lvl*50 + 500 Thieving experience", "Ability to use King Lathas' Combat Training Camp", "Ability to travel freely between eastern and western Ardougne gate"}, {"1 Quest Point", "Lvl*125 + 400 Herblaw experience"}, {"5 Quest Points", "Lvl*300 + 400 experience in Agility and Attack", "Lvl*50 + 150 Magic experience", "Access to the Grand Tree mines", "Ability to use the Spirit Trees", "Ability to use the Gnome Gliders"}, {"2 Quest Points", "(Lvl + 1)*125 Crafting experience", "Access to Shilo Village"}, {"5 Quest Points", "Lvl*50 + 500 Agility Experience", "875 Attack experience", "A Staff of Iban", "15 Death-Runes", "30 Fire-Runes"}, {"2 Quest Points", "Lvl*100 + 250 Crafting experience", "Another reward based on your constellation"}, {"2 Quest Points", "(Lvl + 1)*150 experience twice in a choice of Agility, Fletching, Thieving, Smithing", "Ability to make throwing darts", "Access to the Desert Mining Camp"}, {"4 Quest Points", "15000 Magic experience", "Ability to use the Watchtower teleport spell", "5000 coins"}, {"3 Quest Points", "Lvl*38 + 162 Crafting experience", "2000 coins"}, {"1 Quest Points", "Lvl*50 + 250 Crafting experience", "Ability to buy a dwarf cannon", "Ability to make cannon balls"}, {"2 Quest Points", "(Lvl+1)*300 Mining experience", "(Lvl+1)*125 Herblaw experience", "2 Gold bars"}, {"1 Quest Point", "Lvl*45 + 175 Cooking experience", "A Kitten", "A Chocolate cake and stew"}, {"4 Quest Points", "7650 experience in 4 of these skills of your choice: Attack, Strength, Defense, Hits, Prayer, Magic, Woodcutting, Crafting, Smithing, Herblaw, Agility, and thieving", "Access to the Legend's Guild", "Ability to wear the Dragon Square Shield and Cape of Legends", "Ability to make Oomlie meat parcels and Blessed golden bowls"} };

		private void drawQuestGuide() {
			questGuideInterface.onRender(this.getSurface());
		}

		public String getQuestGuideChosen() {
			return questGuideChosen;
		}

		public void setQuestGuideChosen(String questGuideChosen) {
			this.questGuideChosen = questGuideChosen;
		}

		public int getQuestGuideProgress() {
			return questGuideProgress;
		}

		public void setQuestGuideProgress(int questGuideProgress) {
			this.questGuideProgress = questGuideProgress;
		}

		public String getQuestGuideStartWho() {
			return questGuideStartWho;
		}

		public void setQuestGuideStartWho(int chosen) {
			this.questGuideStartWho = questGuideStartWhos[chosen];
		}

		public String getQuestGuideStartWhere() {
			return questGuideStartWhere;
		}

		public void setQuestGuideStartWhere(int chosen) {
			this.questGuideStartWhere = questGuideStartWheres[chosen];
		}

		public String[] getQuestGuideRequirement() {
			return questGuideRequirement;
		}

		public void setQuestGuideRequirement(int chosen) {
			this.questGuideRequirement = questGuideRequirements[chosen];
		}

		public String[] getQuestGuideReward() {
			return questGuideReward;
		}

		public void setQuestGuideReward(int chosen) {
			this.questGuideReward = questGuideRewards[chosen];
		}

		private String skillToDo;
		private void drawDoSkill() {
			this.doSkillInterface.onRender();
		}

		public String getSkillToDo() {
			return skillToDo;
		}

		public void setSkillToDo(String skillToDo) {
			this.skillToDo = skillToDo;
		}

		private void drawLostOnDeath() {
			lostOnDeathInterface.onRender();
		}

		private void drawTerritorySignup() {
			territorySignupInterface.onRender();
		}

		public void runScroll(int x) {
			if (x > 1)
				x += x;
			else if (x < -1)
				x -= (-x);
			if (showUiTab == 3) { // Quest list.
				if(uiTabPlayerInfoSubTab == 1) {
					panelQuestInfo.scrollMethodList(controlQuestInfoPanel, x);
				}
				/*else if(uiTabPlayerInfoSubTab == 2) {
					panelPlayerTaskInfo.scrollMethodList(controlPlayerTaskInfoPanel, x);
				}*/
			}
			else if (showUiTab == 6) { // Settings wrench menu
				panelSettings.scrollMethodCustomList(controlSettingPanel, x, 1);
			}
			else if (showUiTab == 4) // Magic and prayer book list.
				panelMagic.scrollMethodList(controlMagicPanel, x);
			else if (showUiTab == 5) { // Friendlist and ignore list.
				if(this.panelSocialTab == 2 || this.panelSocialTab == 0) {
					panelSocial.scrollMethodList(controlSocialPanel, x);
				} else if(this.panelSocialTab == 1) {
					panelClan.scrollMethodList(controlClanPanel, x);
				}
			}
			else if (skillGuideInterface.isVisible() && uiTabPlayerInfoSubTab == 0) {
				skillGuideInterface.skillGuide.scrollMethodList(skillGuideInterface.skillGuideScroll, x);
			}
			else if (questGuideInterface.isVisible() && uiTabPlayerInfoSubTab == 1) {
				questGuideInterface.questGuide.scrollMethodList(questGuideInterface.questGuideScroll, x);
			}
			else if (onlineList.isVisible()) {
				onlineList.panel.scrollMethodList(onlineList.scroll, x);
			}
			else if (isShowDialogBank() && this.bankPage == 0)
				bank.bank.scrollMethodList(bank.bankScroll, x);
			else if (auctionHouse.isVisible()) {
				if (auctionHouse.activeInterface == 0) {
					auctionHouse.auctionMenu.scrollMethodList(auctionHouse.auctionScrollHandle, x);
				} else {
					auctionHouse.myAuctions.scrollMethodList(auctionHouse.myAuctionScrollHandle, x);
				}
			}
			else if(clan.getClanInterface().isVisible()) {
				if (clan.getClanInterface().clanActivePanel == 3) {
					clan.getClanInterface().clanSetupPanel.scrollMethodList(clan.getClanInterface().clanSearchScroll, x);
				} else {
					clan.getClanInterface().clanSetupPanel.scrollMethodList(clan.getClanInterface().clanGUIScroll, x);
				}
			}
			else if (experienceConfigInterface.isVisible() && experienceConfigInterface.selectSkillMenu) {
				experienceConfigInterface.experienceConfig.scrollMethodList(experienceConfigInterface.experienceConfigScroll, x);
			}
			else if (messageTabSelected == MessageTab.QUEST && !this.controlPressed)
				panelMessageTabs.scrollMethodList(panelMessageQuest, x);
			else if (this.messageTabSelected == MessageTab.CHAT && !this.controlPressed)
				panelMessageTabs.scrollMethodList(panelMessageChat, x);
			else if (this.messageTabSelected == MessageTab.PRIVATE && !this.controlPressed)
				panelMessageTabs.scrollMethodList(panelMessagePrivate, x);
			else if (this.messageTabSelected == MessageTab.CLAN && !this.controlPressed)
				panelMessageTabs.scrollMethodList(panelMessageClan, x);
			else if (cameraZoom >= 550 && this.controlPressed) {
				if(cameraZoom > 2200) {
					cameraZoom = 2200;
					return;
				}
				cameraZoom += 25 * x;
			}
		}

		public boolean isShowDialogBank() {
			return showDialogBank;
		}

		public void setShowDialogBank(boolean showDialogBank) {
			this.showDialogBank = showDialogBank;
		}

		public String ellipsize(String input, int maxLength) {
			if (input == null || input.length() <= maxLength) {
				return input;
			}
			return input.substring(0, maxLength) + "...";
		}

		private long time;
		private long m_timer;
		class XPNotification {
			private int x, y;
			private int amount = 0;
			private int skill = 0;
			private boolean levelUp;
			private boolean isActive;

			public XPNotification(int skill, int amount, boolean levelUp) {
				this.skill = skill;
				this.amount = amount;
				this.levelUp = levelUp;
				x = (int) ((getGameWidth() / 2) - 50);
				y = (int) ((float) getGameHeight() / 4.0f + 40);
				isActive = false;
			}
		}
		public String clanKickPlayer;
		public void kickClanPlayer(String player) {
			this.clanKickPlayer = player;
		}
}
