package orsc;

import com.openrsc.client.entityhandling.EntityHandler;
import com.openrsc.client.entityhandling.EntityHandler.GUIPARTS;
import com.openrsc.client.entityhandling.EntityHandler.PROJECTILE_TYPES;
import com.openrsc.client.entityhandling.defs.ItemDef;
import com.openrsc.client.entityhandling.defs.NPCDef;
import com.openrsc.client.entityhandling.defs.SpellDef;
import com.openrsc.client.entityhandling.defs.SpriteDef;
import com.openrsc.client.entityhandling.defs.extras.AnimationDef;
import com.openrsc.client.model.Sprite;
import com.openrsc.data.DataFileDecrypter;
import com.openrsc.data.DataOperations;
import com.openrsc.interfaces.NComponent;
import com.openrsc.interfaces.NCustomComponent;

import com.openrsc.interfaces.misc.*;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

//import javax.sound.sampled.AudioSystem;
//import javax.sound.sampled.Clip;

import com.openrsc.interfaces.misc.clan.Clan;
import orsc.buffers.RSBufferUtils;

import orsc.enumerations.*;
import orsc.graphics.gui.*;
import orsc.graphics.three.CollisionFlag;
import orsc.graphics.three.RSModel;
import orsc.graphics.three.Scene;
import orsc.graphics.three.World;
import orsc.graphics.two.Fonts;
import orsc.graphics.two.GraphicsController;
import orsc.graphics.two.MudClientGraphics;
import orsc.multiclient.ClientPort;
import orsc.net.Network_Socket;
import orsc.util.FastMath;
import orsc.util.GenUtil;
import orsc.util.StringUtil;

import static orsc.Config.*;
import static orsc.multiclient.ClientPort.saveHideIp;


public final class mudclient implements Runnable {

    public static final int spriteMedia = 2000;
    public static final int spriteUtil = 2100;
    public static final int spriteItem = 2150;
    static final int spriteLogo = 3150;
    public static final int spriteProjectile = 3160;
    public static final int spriteTexture = 3225;
    private static int FPS = 0;
    public static KillAnnouncerQueue killQueue = new KillAnnouncerQueue();
    static byte[][] s_kb = new byte[250][];
    static int[] s_wb;
    private static ArrayList<String> messages = new ArrayList<String>();
    private static int currentChat = 0;
    private static ClientPort clientPort;
    public final int[] bankItemOnTab = new int[500];
    private final int[] mouseClickX = new int[8192];
    private final int[] mouseClickY = new int[8192];
    private final int[][] animDirLayer_To_CharLayer = new int[][]{{11, 2, 9, 7, 1, 6, 10, 0, 5, 8, 3, 4},
            {11, 2, 9, 7, 1, 6, 10, 0, 5, 8, 3, 4}, {11, 3, 2, 9, 7, 1, 6, 10, 0, 5, 8, 4},
            {3, 4, 2, 9, 7, 1, 6, 10, 8, 11, 0, 5}, {3, 4, 2, 9, 7, 1, 6, 10, 8, 11, 0, 5},
            {4, 3, 2, 9, 7, 1, 6, 10, 8, 11, 0, 5}, {11, 4, 2, 9, 7, 1, 6, 10, 0, 5, 8, 3},
            {11, 2, 9, 7, 1, 6, 10, 0, 5, 8, 4, 3}};
    private final int[] animFrameToSprite_CombatA = new int[]{0, 1, 2, 1, 0, 0, 0, 0};
    private final int[] animFrameToSprite_CombatB = new int[]{0, 0, 0, 0, 0, 1, 2, 1};
    private final int[] bankItemID = new int[500];
    private final int[] bankItemSize = new int[500];
    private final boolean cameraAutoAngleDebug = false;
    private final int[] characterBubbleScale = new int[150];
    private final int[] characterBubbleX = new int[150];
    private final int[] characterBubbleY = new int[150];
    private final int[] characterDialogHalfWidth = new int[150];
    private final int[] characterDialogHeight = new int[150];
    private final String[] characterDialogString = new String[150];
    private final int[] characterDialogX = new int[150];
    private final int[] characterDialogY = new int[150];
    private final int[] characterHealthX = new int[150];
    private final int[] characterHealthY = new int[150];
    private final int[] duelItemCounts = new int[8];
    private final int[] duelOfferItemID = new int[8];
    private final int[] duelOfferItemSize = new int[8];
    private final int[] duelOpponentItemCount = new int[8];
    private final int[] duelOpponentItemCounts = new int[8];
    private final int[] duelOpponentItemId = new int[8];
    private final int[] duelOpponentItems = new int[8];
    private final String[] equipmentStatNames = new String[]{"Armour", "WeaponAim", "WeaponPower", "Magic",
            "Prayer"};
    private final boolean[] gameObjectInstance_Arg1 = new boolean[5000];
    private final int[] gameObjectInstanceDir = new int[5000];
    private final int[] gameObjectInstanceID = new int[5000];
    private final RSModel[] gameObjectInstanceModel = new RSModel[5000];
    private final int[] gameObjectInstanceX = new int[5000];
    private final int[] groundItemID = new int[5000];
    private final int[] groundItemX = new int[5000];
    private final int[] groundItemZ = new int[5000];
    private final int[] inventoryItemEquipped = new int[35];
    private final int[] inventoryItemID = new int[35];
    private final int[] inventoryItemSize = new int[35];
    private final ORSCharacter[] knownPlayers = new ORSCharacter[500];
    private final String[] optionsMenuText = new String[20];
    private final int[] groundItemHeight = new int[5000];
    private final int character2Colour = 2;
    private final RSModel[] modelCache = new RSModel[1000];
    private final int[] newBankItems = new int[500];
    private final int[] newBankItemsCount = new int[500];
    private final ORSCharacter[] npcs = new ORSCharacter[500];
    private final ORSCharacter[] npcsCache = new ORSCharacter[500];
    private final ORSCharacter[] npcsServer = new ORSCharacter[5000];
    private final int[] pathX = new int[8000];
    private final int[] pathZ = new int[8000];
    private final int[] playerClothingColors = new int[]{0xFF0000, 16744448, 16769024, 10543104, '\ue000', '\u8000',
            '\ua080', '\ub0ff', '\u80ff', 12528, 14680288, 3158064, 6307840, 8409088, 0xFFFFFF};
    private final int[] playerHairColors = new int[]{16760880, 16752704, 8409136, 6307872, 3158064, 16736288,
            16728064, 0xFFFFFF, '\uff00', '\uffff'};
    private final ORSCharacter[] players = new ORSCharacter[500];
    private final ORSCharacter[] playerServer = new ORSCharacter[4000];
	private static ArrayList<String> skillNameLongArray = new ArrayList<>();
	private static ArrayList<String> skillNamesArray = new ArrayList<>();
	private static String[] skillNameLong;
	private static String[] skillNames;
	public static int skillCount;
	private final int[] playerSkinColors = new int[]{15523536, 13415270, 11766848, 10056486, 9461792};
    private int[] playerStatBase;
	private int[] playerExperience;
    private final int[] playerStatEquipment = new int[5];
    private final boolean[] prayerOn = new boolean[50];
    private final int projectileMaxRange = 40;
    private final int[] shopItemCount = new int[256];
    private final int[] shopItemID = new int[256];
    private final int[] shopItemPrice = new int[256];
    private final int[] teleportBubbleTime = new int[50];
    private final int[] teleportBubbleX = new int[50];
    private final int[] teleportBubbleZ = new int[50];
    private final int tileSize = 128;
    private final int[] tradeConfirmItems = new int[14];
    private final int[] tradeConfirmItemsCount1 = new int[14];
    private final int[] tradeItemID = new int[14];
    private final int[] tradeItemSize = new int[14];
    private final int[] tradeRecipientConfirmItemCount = new int[14];
    private final int[] tradeRecipientConfirmItems = new int[14];
    private final int[] tradeRecipientItem = new int[14];
    private final int[] tradeRecipientItemCount = new int[14];
    private final boolean[] wallObjectInstance_Arg1 = new boolean[500];
    private final int[] wallObjectInstanceDir = new int[500];
    private final int[] wallObjectInstanceID = new int[500];
    private final RSModel[] wallObjectInstanceModel = new RSModel[500];
    private final int[] wallObjectInstanceX = new int[500];
    private final int[] wallObjectInstanceZ = new int[500];
    public Thread clientBaseThread = null;
    public int threadState = 0;
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
    public boolean enterPressed = false;
    public int lastMouseAction = 0;
    public int lastMouseButtonDown = 0;
    public PacketHandler packetHandler;
    public int selectedSkill = -1;
    public boolean m_N = false;
    public int mouseX = 0;
    public int mouseY = 0;
    public int mouseLastProcessedX = 0;
    public int mouseLastProcessedY = 0;
    public int screenOffsetX;
    public int screenOffsetY;
    public boolean shiftPressed = false;
    //public int groupID = 100;
    public boolean rendering;
    public int bankItemsMax = 50;
    public int bankPage = 0;
    public int bankSelectedItemSlot = -1;
    public int cameraRotation = 128;
    private GameMode currentViewMode = GameMode.LOGIN;
    public InputXAction inputX_Action = InputXAction.ACT_0;
    public Menu menuCommon;
    public int menuX = 0;
    public int menuY = 0;
    public int mouseButtonClick = 0;
    public int mouseButtonItemCountIncrement = 0;
    private int mouseClickCount = 0;
    private int mouseClickXStep = 0;
    private HashMap<String, File> soundCache = new HashMap<String, File>();
    public boolean authenticSettings = !(
            isAndroid() ||
                    S_WANT_CLANS || S_WANT_KILL_FEED
                    || S_FOG_TOGGLE || S_GROUND_ITEM_TOGGLE
                    || S_AUTO_MESSAGE_SWITCH_TOGGLE || S_BATCH_PROGRESSION
                    || S_SIDE_MENU_TOGGLE || S_INVENTORY_COUNT_TOGGLE
                    || S_MENU_COMBAT_STYLE_TOGGLE
                    || S_FIGHTMODE_SELECTOR_TOGGLE || S_SHOW_ROOF_TOGGLE
                    || S_EXPERIENCE_COUNTER_TOGGLE || S_WANT_GLOBAL_CHAT
                    || S_EXPERIENCE_DROPS_TOGGLE || S_ITEMS_ON_DEATH_MENU);
    public long totalXpGainedStartTime = 0;
    public String[] achievementNames = new String[500];
    public String[] achievementTitles = new String[500];
    public String[] achievementDescs = new String[500];
    public int[] achievementProgress = new int[500];
    public int showUiTab = 0;
    public boolean topMouseMenuVisible = false;
    public int resizeWidth;
    public int resizeHeight;
    public Clan clan;
    public boolean PAUSED;
    public boolean gotInitialConfigs = false;
    public ArrayList<String> skillGuideChosenTabs;
    public String clanKickPlayer;
    private long lastFPSUpdate = 0;
    private int currentFPS = 0;
    private long[] m_F = new long[10];
    String m_p = null;
    private double xpPerHour = 0;
    private boolean hasGameCrashed = false;
    private int gameState = 1;
    private int m_b = 0;
    private int totalAchievements = 0;
    private int sleepModifier = 20;
    private int[] animFrameToSprite_Walk = new int[]{0, 1, 2, 1};
    private int appearanceHeadGender = 1;
    private int appearanceHeadType = 0;
    private int autoLoginTimeout = 0;
    private int cameraAngle = 1;
    private int cameraPositionX = 0;
    private int cameraPositionZ = 0;
    public boolean cameraAllowPitchModification = false;
    public int cameraPitch = 912;
    private int cameraAutoMoveX = 0;
    private int cameraAutoMoveZ = 0;
    public int cameraZoom = 750;
    public int minCameraZoom = 500;
    private int characterBubbleCount = 0;
    private int[] characterBubbleID = new int[150];
    private int characterDialogCount = 0;
    private int[] characterHealthBar = new int[150];
    private int characterHealthCount = 0;
    private String chatMessageTarget;
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
    private int deathScreenTimeout = 0;
    private boolean duelConfirmed = false;
    private String duelConfirmOpponentName = "";
    private int duelDoX_Slot;
    private int dropInventorySlot = -1;
    private int[] duelItems = new int[8];
    private int duelItemsCount = 0;
    private boolean duelOfferAccepted = false;
    private int duelOfferItemCount = 0;
    private boolean duelOffsetOpponentAccepted = false;
    private int duelOffsetOpponentItemCount = 0;
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
    private boolean errorLoadingData = false;
    private boolean errorLoadingMemory = false;
    private int[] experienceArray = new int[S_PLAYER_LEVEL_LIMIT];
    private int fatigueSleeping = 0;
    public boolean doCameraZoom = false;
    private int gameHeight = 334;
    private int gameObjectInstanceCount = 0;
    private int[] gameObjectInstanceZ = new int[5000];
    private int gameWidth = 512;
    private int groundItemCount = 0;
    private boolean inputX_Focused = true;
    private int inputX_Height = 0;
    private String[] inputX_Lines = null;
    private boolean inputX_OK = false;
    private int inputX_Width = 0;
    private boolean insideTutorial = false;
    private boolean insideBlackHole = false;
    private int inventoryItemCount;
    private boolean isSleeping = false;
    private int knownPlayerCount = 0;
    private int lastHeightOffset = -1;
    private int lastObjectAnimationNumberFireLightningSpell = -1;
    private int lastObjectAnimationNumberTorch = -1;
    private int lastObjectAnimatonNumberClaw = -1;
    private boolean loadingArea = false;
    public ORSCharacter localPlayer = new ORSCharacter();
    private int logoutTimeout = 0;
    private int m_Ai;
    private int m_be;
    private int m_Ce = 0;
    private int m_Cg;
    private int m_cl = 30;
    private int m_dk = 1;
    private int m_ed;
    private int cameraAutoMoveAmountX = 2;
    private int m_Eg;
    private int m_ek;
    private int m_Ge;
    private int m_hh = 0;
    private boolean runningAsApplet = true;
    private boolean allowDebugCommands = !runningAsApplet || true;
    private int optionsMenuCount = 0;
    private String m_ig = "";
    private int questPoints = 0;
    private int m_Ji = 0;
    private int settingTab = 0;
    private int loginButtonExistingUser;
    private int m_Kj;
    private int m_ld = 2;
    private int characterBottomColour = 14;
    private int m_Mj;
    private int m_nj = -1;
    private int m_Of;
    private int cameraAutoMoveFrameCount = 0;
    private int m_Oj = 0;
    private int cameraAutoMoveAmountZ = 2;
    private int m_qd = 9;
    private int m_rc = 0;
    private int m_Re;
    private int m_rf;
    private boolean reportAbuse_isMute = false;
    private int m_Wc = 0;
    private int m_Wg = 8;
    private long lastWrite;
    private int m_wk = -1;
    private int m_Xc;
    private int loginScreenNumber = 0;
    private int m_Xi;
    private int lostPasswordButtonIdx;
    private int rememberButtonIdx;
    private int hideIpButtonIdx;
    private int m_Zb = 0;
    private int localPlayerServerIndex = -1;
    private int m_Ze;
    private Menu menuDuel;
    private boolean menuDuel_Visible = false;
    private int menuDuelX;
    private int menuDuelY;
    private Menu menuTrade;
    private boolean menuTrade_Visible = false;
    private int menuTradeX;
    private int menuTradeY;
    private boolean menuVisible = false;
    private int messageTabActivity_Chat = 0;
    private int messageTabActivity_Game = 0;
    private int messageTabActivity_Private = 0;
    private int messageTabActivity_Clan = 0;
    private int messageTabActivity_Quest = 0;
    public MessageTab messageTabSelected = MessageTab.ALL;
    private int midRegionBaseX;
    private int midRegionBaseZ;
    private int minimapRandom_1 = 0;
    private int minimapRandom_2 = 0;
    private int mouseButtonDownTime = 0;
    private int mouseWalkX = 0;
    private int mouseWalkY = 0;
    private int newBankItemCount = 0;
    private int npcCacheCount = 0;
    private int npcCount = 0;
    private int objectAnimationCount = 0;
    private int objectAnimationNumberClaw = 0;
    private int objectAnimationNumberFireLightningSpell = 0;
    private int objectAnimationNumberTorch = 0;
    private boolean optionCameraModeAuto = true;
    private boolean optionMouseButtonOne = false;
    private boolean optionSoundDisabled = true;
    private boolean clanInviteBlockSetting = false;
    private Panel panelAppearance;
    private Panel panelLogin;
    private Panel panelLoginWelcome;
    private Panel panelSetRecoveryQuestion;
    private Panel panelRecovery;
    private Panel panelContact;
    private Panel panelMagic;
    private int panelMessageChat;
    private int panelMessageEntry;
    private int panelMessagePrivate;
    private int panelMessageQuest;
    private int panelMessageClan;
    private Panel panelMessageTabs;
    private Panel panelPlayerInfo;
    private Panel panelQuestInfo;
    private Panel panelPlayerTaskInfo;
    private Panel panelSettings;
    private Panel panelSocial;
    private Panel panelClan;
    private SocialPopupMode panelSocialPopup_Mode = SocialPopupMode.NONE;
    private int panelSocialTab = 0;
    private PasswordChangeMode panelPasswordChange_Mode = PasswordChangeMode.NONE;
    private String oldPassword;
    private String newPassword;
    private String password = "";
    private int playerCount = 0;
    private int playerLocalX;
    private int playerLocalZ;
    private int[] playerStatCurrent;
    private String[] messagesArray = new String[5];
    private long[] playerStatXpGained;
    private long[] xpGainedStartTime;
    private long playerXpGainedTotal = 0;
    private String[] questNames = new String[100];
    private int[] questStages = new int[100];
    private int reportAbuse_AbuseType = 0;
    private String reportAbuse_Name = "";
    private int reportAbuse_State = 0;
    private int requestedPlane = -1;
    private Scene scene;
    private int selectedItemInventoryIndex = -1;
    private int selectedSpell = -1;
    private String serverMessage = "";
    private boolean serverMessageBoxTop = false;
    private boolean developerMenu = false;
    private int devMenuNpcID;
    private boolean modMenu = false;
    private Integer settingsHideIP = 0;
    private int settingsBlockChat = 0;
    private int settingsBlockDuel = 0;
    private int settingsBlockPrivate = 0;
    private int settingsBlockTrade = 0;
    private int shopBuyPriceMod = 0;
    private int shopPriceMultiplier = 0;
    private int shopSelectedItemIndex = -1;
    private int shopSelectedItemType = -2;
    private int shopSellPriceMod = 0;
    private boolean showAppearanceChange = false;
    private boolean showSetRecoveryQuestion = false;
    private boolean showSetContactDetails = false;
    private boolean showDialogBank = false;
    private boolean showDialogDuel = false;
    private boolean showDialogDuelConfirm = false;
    private boolean showDialogMessage = false;
    private boolean showDialogServerMessage = false;
    private boolean showDialogShop = false;
    private boolean showDialogTrade = false;
    private boolean showDialogTradeConfirm = false;
    private boolean optionsMenuShow = false;
    private int showUiWildWarn = 0;
    private int recentSkill = -1;
    private String sleepingStatusText = null;
    private boolean sleepWordDelay = true;
    private int sleepWordDelayTimer = 0;
    private int spriteCount = 0;
    private int statFatigue = 0;
    private int statKills2 = 0;
    private int petFatigue = 0;
    private MudClientGraphics surface;
    private int systemUpdate = 0;
    private int elixirTimer = 0;
    private boolean inWild = false;
    private int teleportBubbleCount = 0;
    private int[] teleportBubbleType = new int[50];
    private boolean tradeAccepted = false;
    private boolean tradeConfirmAccepted = false;
    private int tradeConfirmItemsCount = 0;
    private int tradeDoX_Slot;
    private int tradeItemCount = 0;
    private boolean tradeRecipientAccepted = false;
    private int tradeRecipientConfirmItemsCount = 0;
    private String tradeRecipientConfirmName;
    private int tradeRecipientItemsCount = 0;
    private String tradeRecipientName = "";
    private int uiTabPlayerInfoSubTab = 0;
    private String username = "";
    private int wallObjectInstanceCount = 0;
    private int welcomeLastLoggedInDays = 0;
    private String welcomeLastLoggedInHost = null;
    private String welcomeLastLoggedInIp;
    private int welcomeTipOfDay;
    private int welcomeRecoverySetDays = 0;
    private boolean welcomeScreenShown = false;
    //private int welcomeUnreadMessages = 0;
    private World world;
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
    //flag consumed by bank interface to sync custom options
    //gets unset when player logins again after welcome screen
    private boolean initLoginCleared;
    private int xpPerHourCount = 0;
    private CustomBankInterface bank;
    private int settingsBlockGlobal;
    private int lastSelectedSpell = -1;
    private int flag = 0;
    private Timer tiktok = new Timer();
    private NComponent mainComponent;
    public OnlineListInterface onlineList;
    private NCustomComponent experienceOverlay;
    private ProgressBarInterface batchProgressBar;
    private BankPinInterface bankPinInterface;
    private FishingTrawlerInterface fishingTrawlerInterface;
    public AchievementGUI achievementInterface;
    public IronManInterface ironmanInterface;
    public AuctionHouse auctionHouse;
    public SkillGuideInterface skillGuideInterface;
    public QuestGuideInterface questGuideInterface;
    public ExperienceConfigInterface experienceConfigInterface;
    public DoSkillInterface doSkillInterface;
    public LostOnDeathInterface lostOnDeathInterface;
    public TerritorySignupInterface territorySignupInterface;
    private String skillGuideChosen;
    private String questGuideChosen;
    private int questGuideProgress;
    private String questGuideStartWho;
    private String questGuideStartWhere;
	private String[] questGuideStartWhos = {"Sir Amik Varze", "the Cook", "the Gypsy", "Doric", "the Priest", "the Bertender", "Veronica", "Wizard Mizgog", "Redbeard Frank", "Chancellor Hassan", "Romeo", "Fred the Farmer", "Reldo", "the Squire", "Morgan", "Hetty", "the Guildmaster", "a boy", "the Adventurers", "Achetties", "Kaqemeex", "King Arthur", "Thormac", "Dimintheis", "Kangai Mau", "a mountain dwarf", "Brother Omad", "Lucien", "Brother Kojo", "King Arthur", "Lady Servil", "Bolren", "Ceril Carnillean", "Councillor Halgrive", "Edmond", "Caroline", "Almera", "Elena", "Trufitus", "King Narnode Shareen", "Mosol Rei", "King Lathas", "Observatory Professor", "Irena", "Watchtower Wizard", "Captain Lawgof", "a Gaurd", "an Examiner", "Gertrude", "Sir Radimus Erkle","Duke Horacio"};
	private String[] questGuideStartWheres = {"on the first floor of the White Knight's Castle in Falador", "on the first floor of Lumbridge Castle", "in Varrock Square", "north of Falador", "in the Lumbridge church", "inside the Rusty Anchor bar in Port Sarim", "outside of Draynor Manor", "on the top floor of the Wizard's Tower", "in Port Sarim", "inside Al-Kharid palace", "in Varrock Square", "north of Lumbridge", "in the Varrock Palace Library", "on the White Knight Castle grounds in Falador", "in Draynor Village", "in Rimmington", "inside the Champion's Guild", "in Taverly", "in the Lumbridge swamp", "outside of the Heroes' Guild north of Taverly", "at the Druid's Stone Circle in Taverly", "in Camelot", "on the top floor fo the Sorcerer's Tower south of Seer's Village", "in eastern Varrock", "in The Shrimp and Parrot pub in Brimhaven", "on either side of the White Wolf Mountain passage", "in the Monastery south of East Ardougne", "in the Flying Horse Inn on the western end of East Ardougne", "inside the Clock Tower south of East Ardougne", "in Camelot", "west of Port Khazard", "in Tree Gnome Village", "south of the Ardougne Castle", "outside of the East Ardougne church", "north of the Ardougne Castle", "east of Ardougne", "northeast of Baxtorian Falls", "north of the Ardougne Castle", "north-east of Tai Bwo Wannai", "in the Grand Tree", "outside of Shilo Village in southern Karamja", "on the ground floor of Ardougne Castle", "in the Observatory reception room west of the Tree Gnome Village", "outside the Shantay Pass in the Kharidian desert", "at the top of the Watchtower north of Yanille", "far north-east of Seer's Village", "in the Sinclair Mansion north of Camelot", "in the Exam Centre south of the Digsite", "at her house west of Varrock", "inside the Legend's Guild","inside of Lumbridge Castle"};
	private String[] questGuideRequirement;
    private String[] questGuideReward;
	private String[][] questGuideRequirements = {{"12 quest points"}, {"None"}, {"Ability to defeat a level 30 demon"}, {"None"}, {"None"}, {"None"}, {"None"}, {"None"}, {"None"}, {"None"}, {"None"}, {"None"}, {"A friend to assist"}, {"10 Mining", "A friend to assist"}, {"Ability to defeat a level 43 vampire"}, {"None"}, {"32 Quest Points", "33 Magic", "The ability to defeat a level 110 dragon"}, {"Ability to defeat a level 54 shapeshifter"}, {"31 Crafting", "36 Woodcutting", "Ability to defeat a level 95 monster"}, {"Completed Shield of Arrav, Dragon Slayer, Merlin's Crystal, and Lost City", "56 Quest Points", "53 Cooking", "53 Fishing", "25 Herblaw", "50 Mining"}, {"None"}, {"Ability to defeat a level 58 knight", "A friend to assist"}, {"Completed the Barbarian Bar Crawl", "31 Prayer"}, {"40 Mining", "40 Smithing", "40 Crafting", "59 Magic"}, {"21 Thieving"}, {"10 Fishing"}, {"None"}, {"42 Thieving", "35 Ranged", "Ability to defeat a level 63 monster with ranged"}, {"None"}, {"Completed Merlin's Crystal", "20 Attack", "Ability to defeat a level 146 Black Knight Titan"}, {"Ability to defeat a level 122 monster"}, {"None"}, {"None"}, {"None"}, {"None"}, {"30 Firemaking"}, {"None"}, {"Completed Plague City"}, {"Completed Druidic Ritual", "3 Herblaw"}, {"25 Agility", "Ability to defeat a level 184 monster"}, {"Completed Jungle Potion", "32 Agility", "20 Crafting", "4 Smithing", "Ability to defeat a level 83 monsters"}, {"Completed Biohazard", "25 Ranged"}, {"10 Crafting"}, {"10 Fletching", "10 Smithing", "Ability to defeat a level 47 enemy"}, {"40 Mining", "30 Agility", "15 Thieving", "14 Herblaw", "14 Magic", "Ability to defeat a level 68 ogre"}, {"None"}, {"None"}, {"Completed Druidic Ritual", "25 Thieving", "10 Agility", "10 Herblaw"}, {"None"}, {"108 Quest Points", "50 Agility", "50 Crafting", "45 Herblaw", "56 Magic", "52 Mining", "42 Prayer", "50 Smithing", "50 Strength", "50 Thieving", "50 Woodcutting", "Ability to defeat a level 172 demon"},{"None"}};
	private String[][] questGuideRewards;
    private String skillToDo;
    private long time;
    private long m_timer;
    private ArrayList<XPNotification> xpNotifications = new ArrayList<XPNotification>();
    private int amountToZoom = 0;
    private Panel panelLoginOptions;

    int instructContactDetails;
    int controlContactName;
    int controlContactZipCode;
    int controlContactCountry;
    int controlContactEmail;
    int finishSubmitContact;
    int controlRecoveryInstruction;
    int[] controlSetQuestion = new int[5];
    int[] controlSetAnswer = new int[5];
    int[] controlCustomQuestion = new int[5];
    int[] controlCustomAnswer = new int[5];
    int finishSetRecovery;
    int qaIndex = -1;
    boolean attemptedRecovery = false;
    String[] jfb = new String[5];
    int recoveryQAindices[] = {0, 1, 2, 3, 4};
    int instructPassRecovery1;
    int instructPassRecovery2;
    int controlPreviousPassword;
    int controlNewPassword;
    int controlConfirmation;
    int passwordRecoverSubmit;
    int passwordRecoverCancel;
    int controlPassQuestion[] = new int[5];
    int controlPassAnswer[] = new int[5];
    String questions[] = {"Where were you born?",
            "What was your first teacher's name?",
            "What is your father's middle name?",
            "Who was your first best friend?",
            "What is your favourite vacation spot?",
            "What is your mother's middle name?",
            "What was your first pet's name?",
            "What was the name of your first school?",
            "What is your mother's maiden name?",
            "Who was your first boyfriend/girlfriend?",
            "What was the first computer game you purchased?",
            "Who is your favourite actor/actress?",
            "Who is your favourite author?",
            "Who is your favourite musician?",
            "Who is your favourite cartoon character?",
            "What is your favourite book?",
            "What is your favourite food?",
            "What is your favourite movie?"};

    /**
     * Newest RSC cache: SAME VALUES.
     * <p>
     * mudclient.spriteMedia = 2000; mudclient.spriteUtil =
     * mudclient.spriteMedia + 100; 2100 mudclient.spriteItem = 50 +
     * mudclient.spriteUtil; 2150 mudclient.spriteLogo = 1000 +
     * mudclient.spriteItem; 3150 mudclient.spriteProjectile = 10 +
     * mudclient.spriteLogo; 3160 mudclient.spriteTexture = 50 +
     * mudclient.spriteProjectile; 3210
     */

    public mudclient(ClientPort handler) {
        clientPort = handler;
        F_CACHE_DIR = clientPort.getCacheLocation();
        initConfig();
    }

    private static boolean isValidEmailAddress(String email) {
        boolean stricterFilter = true;
        String stricterFilterString = "[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}";
        String laxString = ".+@.+\\.[A-Za-z]{2}[A-Za-z]*";
        String emailRegex = stricterFilter ? stricterFilterString : laxString;
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(emailRegex);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }

    public static String formatStackAmount(int length) {
        if (length < 100000) {
            return String.valueOf(length);
        }
        if (length < 10000000) {
            return "@whi@" + length / 1000 + "K";
        }
        return "@gre@" + length / 1000000 + "M";
    }

    private void zeroMF() {
        try {
            for (int i = 0; i < 10; ++i) {
                this.m_F[i] = 0L;
            }


        } catch (RuntimeException var3) {
            throw GenUtil.makeThrowable(var3, "e.PE(" + "dummy" + ')');
        }
    }

    private void setFPS(int var1, byte var2) {
        try {

            this.sleepModifier = 1000 / var1;
            if (var2 <= 104) {
                this.screenOffsetX = 113;
            }

        } catch (RuntimeException var4) {
            throw GenUtil.makeThrowable(var4, "e.IE(" + var1 + ',' + var2 + ')');
        }
    }

    private void errorGameCrash() {
        try {

            if (!this.hasGameCrashed) {
                this.hasGameCrashed = true;
                System.out.println("error_game_crash");
            }
        } catch (RuntimeException var6) {
            var6.printStackTrace();
            throw GenUtil.makeThrowable(var6, "e.KE(" + "{...}" + ',' + "dummy" + ')');
        }
    }

    private void closeProgram() {
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

    private boolean loadLogo() {
        try {

            byte[] Archive = unpackData("library.orsc", "Library", 0);
            if (Archive == null) {
                System.out.println("Library is empty");
            }
            Fonts.addFont(DataOperations.loadData("h11p.jf", 0, Archive));
            Fonts.addFont(DataOperations.loadData("h12b.jf", 0, Archive));
            Fonts.addFont(DataOperations.loadData("h12p.jf", 0, Archive));
            Fonts.addFont(DataOperations.loadData("h13b.jf", 0, Archive));
            Fonts.addFont(DataOperations.loadData("h14b.jf", 0, Archive));
            Fonts.addFont(DataOperations.loadData("h16b.jf", 0, Archive));
            Fonts.addFont(DataOperations.loadData("h20b.jf", 0, Archive));
            Fonts.addFont(DataOperations.loadData("h24b.jf", 0, Archive));
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
                    this.gameState = 2;
                }

                while (this.gameState == 2) {
                    if (this.gotInitialConfigs) {
                        this.gameState = 0;
                        run2();
                    }
                }

            } catch (Exception var10) {
                var10.printStackTrace();
                this.errorGameCrash();
            }

        } catch (RuntimeException var11) {
            throw GenUtil.makeThrowable(var11, "e.run()");
        }
    }

    private void run2() {
        try {
            try {
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
                        int m_Q = 10;
                        if (var5 < m_Q) {
                            var5 = m_Q;
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
                        int m_S = 1000;
                        if (var9 > m_S) {
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
                        if (this.currentViewMode == GameMode.LOGIN) {
                            this.createLoginPanels(3845);
                            //this.renderLoginScreenViewports(-116);
                        }
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
                this.errorGameCrash();
            }

        } catch (RuntimeException var11) {
            throw GenUtil.makeThrowable(var11, "e.run()");
        }
    }

    public void startMainThread() {
        this.clientBaseThread = new Thread(this);
        this.clientBaseThread.start();
        if (!isAndroid()) {
            this.clientBaseThread.setPriority(1);
        }
        gameState = 1;
    }

    public World getWorld() {
        return world;
    }

    public final void addFriend(String player) {
        try {

            if (200 <= SocialLists.friendListCount) {
                this.showMessage(false, null, "Friend list is full", MessageType.GAME, 0, null
                );
            } else {
                String var3 = StringUtil.displayNameToKey(player);
                if (null != var3) {
                    int var4;
                    for (var4 = 0; var4 < SocialLists.friendListCount; ++var4) {
                        if (var3.equals(StringUtil.displayNameToKey(SocialLists.friendList[var4]))) {
                            this.showMessage(false, null, player + " is already on your friend list.",
                                    MessageType.GAME, 0, null);
                            return;
                        }

                        if (SocialLists.friendListOld[var4] != null
                                && var3.equals(StringUtil.displayNameToKey(SocialLists.friendListOld[var4]))) {
                            this.showMessage(false, null, player + " is already on your friend list.",
                                    MessageType.GAME, 0, null);
                            return;
                        }
                    }

                    for (var4 = 0; var4 < SocialLists.ignoreListCount; ++var4) {
                        if (var3.equals(StringUtil.displayNameToKey(SocialLists.ignoreListArg0[var4]))) {
                            this.showMessage(false, null,
                                    "Please remove " + player + " from your ignore list first.", MessageType.GAME, 0,
                                    null);
                            return;
                        }

                        if (null != SocialLists.ignoreListArg1[var4]
                                && var3.equals(StringUtil.displayNameToKey(SocialLists.ignoreListArg1[var4]))) {
                            this.showMessage(false, null,
                                    "Please remove " + player + " from your ignore list first.", MessageType.GAME, 0,
                                    null);
                            return;
                        }
                    }

                    if (!var3.equals(StringUtil.displayNameToKey(this.localPlayer.accountName))) {
                        this.packetHandler.getClientStream().newPacket(195);
                        this.packetHandler.getClientStream().writeBuffer1.putString(player);
                        this.packetHandler.getClientStream().finishPacket();
                    } else {
                        this.showMessage(false, null, "You can\'t add yourself to your own friend list.",
                                MessageType.GAME, 0, null);
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
                this.showMessage(false, null, "Ignore list full", MessageType.GAME, 0, null
                );
            } else {
                String var3 = StringUtil.displayNameToKey(player);
                if (var3 != null) {
                    int var4;
                    for (var4 = 0; var4 < SocialLists.ignoreListCount; ++var4) {
                        if (var3.equals(StringUtil.displayNameToKey(SocialLists.ignoreListArg0[var4]))) {
                            this.showMessage(false, null, player + " is already on your ignore list",
                                    MessageType.GAME, 0, null);
                            return;
                        }

                        if (SocialLists.ignoreListArg1[var4] != null
                                && var3.equals(StringUtil.displayNameToKey(SocialLists.ignoreListArg1[var4]))) {
                            this.showMessage(false, null, player + " is already on your ignore list",
                                    MessageType.GAME, 0, null);
                            return;
                        }
                    }

                    for (var4 = 0; var4 < SocialLists.friendListCount; ++var4) {
                        if (var3.equals(StringUtil.displayNameToKey(SocialLists.friendList[var4]))) {
                            this.showMessage(false, null,
                                    "Please remove " + player + " from your friends list first", MessageType.GAME, 0,
                                    null);
                            return;
                        }

                        if (SocialLists.friendListOld[var4] != null
                                && var3.equals(StringUtil.displayNameToKey(SocialLists.friendListOld[var4]))) {
                            this.showMessage(false, null,
                                    "Please remove " + player + " from your friends list first", MessageType.GAME, 0,
                                    null);
                            return;
                        }
                    }

                    if (!var3.equals(StringUtil.displayNameToKey(this.localPlayer.accountName))) {
                        this.packetHandler.getClientStream().newPacket(132);
                        this.packetHandler.getClientStream().writeBuffer1.putString(player);
                        this.packetHandler.getClientStream().finishPacket();
                    } else {
                        this.showMessage(false, null, "You can\'t add yourself to your ignore list",
                                MessageType.GAME, 0, null);
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

    private void addPlayerToMenu(int index) {
        try {

            ORSCharacter player = this.players[index];
            String name = player.getStaffName();
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

            if (player.isInvulnerable) {
                level = "@bla@";
            }

            level = " @whi@" + level + "(level-" + player.level + ")";
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
                } else {
                    this.menuCommon.addCharacterItem(player.serverIndex, MenuItemAction.PLAYER_DUEL, "Duel with",
                            "@whi@" + name + level);
                }


                this.menuCommon.addCharacterItem(player.serverIndex, MenuItemAction.PLAYER_TRADE, "Trade with",
                        "@whi@" + name + level);
                this.menuCommon.addCharacterItem(player.serverIndex, MenuItemAction.PLAYER_FOLLOW, "Follow",
                        "@whi@" + name + level);
                this.menuCommon.addItem_With2Strings("Report abuse", "@whi@" + name + level, player.getStaffName(),
                        MenuItemAction.REPORT_ABUSE, player.accountName);
                if (modMenu) {
                    this.menuCommon.addItem_With2Strings("Summon", "@whi@" + name, player.displayName,
                            MenuItemAction.MOD_SUMMON_PLAYER, player.accountName);
                    this.menuCommon.addItem_With2Strings("Return", "@whi@" + name, player.displayName,
                            MenuItemAction.MOD_RETURN_PLAYER, player.accountName);
                    this.menuCommon.addItem_With2Strings("Goto", "@whi@" + name, player.displayName,
                            MenuItemAction.MOD_GOTO_PLAYER, player.accountName);
                    this.menuCommon.addItem_With2Strings("Jail", "@whi@" + name, player.displayName,
                            MenuItemAction.MOD_PUT_PLAYER_JAIL, player.accountName);
                    this.menuCommon.addItem_With2Strings("Release", "@whi@" + name, player.displayName,
                            MenuItemAction.MOD_RELEASE_PLAYER_JAIL, player.accountName);
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

    private void autoRotateCamera(byte var1) {
        try {

            if ((this.cameraAngle & 1) != 1 || !this.cameraColliding(this.cameraAngle)) {
                if ((1 & this.cameraAngle) == 0 && this.cameraColliding(this.cameraAngle)) {
                    if (!this.cameraColliding(1 + this.cameraAngle & 7)) {
                        if (this.cameraColliding((7 & 7 + this.cameraAngle))) {
                            this.cameraAngle = 7 & 7 + this.cameraAngle;
                        }
                    } else {
                        this.cameraAngle = 7 & 1 + this.cameraAngle;
                    }

                } else {
                    int[] var2 = new int[]{1, -1, 2, -2, 3, -3, 4};
                    int var3 = 0;
                    if (var1 > 7) {
                        while (var3 < 7) {
                            if (this.cameraColliding((7 & 8 + this.cameraAngle + var2[var3]))) {
                                this.cameraAngle = 7 & this.cameraAngle + var2[var3] + 8;
                                break;
                            }

                            ++var3;
                        }

                        if ((1 & this.cameraAngle) == 0 && this.cameraColliding(this.cameraAngle)) {
                            if (this.cameraColliding((7 & 1 + this.cameraAngle))) {
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

    private boolean cameraColliding(int angle) {
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

    public final void cantLogout(byte var1) {
        try {
            if (var1 >= -19) {
                this.drawMenu();
            }

            this.logoutTimeout = 0;

            this.showMessage(false, null, "Sorry, you can\'t logout at the moment", MessageType.GAME, 0,
                    null, "@cya@");
        } catch (RuntimeException var3) {
            throw GenUtil.makeThrowable(var3, "client.CB(" + var1 + ')');
        }
    }

    private void checkConnection() {
        try {

            long var2 = GenUtil.currentTimeMillis();
            if (this.packetHandler.getClientStream().hasFinishedPackets()) {
                this.lastWrite = var2;
            }

            if (-5001L > ~(var2 + -this.lastWrite)) {
                this.lastWrite = var2;
                this.packetHandler.getClientStream().newPacket(67);
                this.packetHandler.getClientStream().finishPacket();
            }

            try {
                this.packetHandler.getClientStream().flush(0, true);
            } catch (IOException var5) {
                this.lostConnection(123);
                return;
            }
            int len = this.packetHandler.getClientStream().readIncomingPacket(packetHandler.getPacketsIncoming());
            if (len > 0)
                this.packetHandler.handlePacket(packetHandler.getPacketsIncoming().getUnsignedByte(), len);
        } catch (RuntimeException var6) {
            throw GenUtil.makeThrowable(var6, "client.SB(" + "dummy" + ')');
        }
    }

    private void clearInputString80(byte var1) {
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

    public final void closeConnection(boolean sendPacket) {
        try {

            if (sendPacket && null != this.packetHandler.getClientStream()) {
                try {
                    this.packetHandler.getClientStream().newPacket(31);
                    this.packetHandler.getClientStream().finishPacketAndFlush();
                } catch (IOException var4) {
                }
            }

            this.setUsername("");
            this.password = "";
            this.jumpToLogin();
        } catch (RuntimeException var5) {
            throw GenUtil.makeThrowable(var5, "client.RB(" + sendPacket + ',' + "dummy" + ')');
        }
    }

    private void createRecoveryQuestionPanel() {
        this.panelSetRecoveryQuestion = new Panel(this.getSurface(), 100);
        int i1 = 8;
        this.controlRecoveryInstruction = this.panelSetRecoveryQuestion.addCenteredText(256, i1, "@yel@Please provide 5 security questions in case you lose your password", 1, true);
        i1 += 22;
        this.panelSetRecoveryQuestion.addCenteredText(256, i1, "If you ever lose your password, you will need these to prove you own your account.", 1, true);
        i1 += 13;
        this.panelSetRecoveryQuestion.addCenteredText(256, i1, "Your answers are encrypted and are ONLY used for password recovery purposes.", 1, true);
        i1 += 22;
        this.panelSetRecoveryQuestion.addCenteredText(256, i1, "@ora@IMPORTANT:@whi@ To recover your password you must give the EXACT same answers you", 1, true);
        i1 += 13;
        this.panelSetRecoveryQuestion.addCenteredText(256, i1, "give here. If you think you might forget an answer, or someone else could guess the", 1, true);
        i1 += 13;
        this.panelSetRecoveryQuestion.addCenteredText(256, i1, "answer, then press the 'different question' button to get a better question.", 1, true);
        i1 += 35;
        for (int j1 = 0; j1 < 5; j1++) {
            this.panelSetRecoveryQuestion.addButtonBackground(170, i1, 310, 30);
            this.jfb[j1] = "~:" + this.recoveryQAindices[j1];
            this.controlSetQuestion[j1] = this.panelSetRecoveryQuestion.addCenteredText(170, i1 - 7, (j1 + 1) + ": "
                    + this.questions[this.recoveryQAindices[j1]], 1, true);
            this.controlSetAnswer[j1] = this.panelSetRecoveryQuestion.addCenteredTextEntry(170, i1 + 7, 310, 80, 30, 1, false, true);
            this.panelSetRecoveryQuestion.addButtonBackground(370, i1, 80, 30);
            this.panelSetRecoveryQuestion.addCenteredText(370, i1 - 7, "Different", 1, true);
            this.panelSetRecoveryQuestion.addCenteredText(370, i1 + 7, "Question", 1, true);
            this.controlCustomQuestion[j1] = this.panelSetRecoveryQuestion.addButton(370, i1, 80, 30);
            this.panelSetRecoveryQuestion.addButtonBackground(455, i1, 80, 30);
            this.panelSetRecoveryQuestion.addCenteredText(455, i1 - 7, "Enter own", 1, true);
            this.panelSetRecoveryQuestion.addCenteredText(455, i1 + 7, "Question", 1, true);
            this.controlCustomAnswer[j1] = this.panelSetRecoveryQuestion.addButton(455, i1, 80, 30);
            i1 += 35;
        }

        this.panelSetRecoveryQuestion.setFocus(this.controlSetAnswer[0]);
        i1 += 10;
        this.panelSetRecoveryQuestion.addButtonBackground(256, i1, 250, 30);
        this.panelSetRecoveryQuestion.addCenteredText(256, i1, "Click here when finished", 4, true);
        this.finishSetRecovery = this.panelSetRecoveryQuestion.addButton(256, i1, 250, 30);
    }

    public void method_181() {
        if (this.qaIndex != -1) {
            if (this.chatMessageInputCommit.length() > 0) {
                this.jfb[this.qaIndex] = this.chatMessageInputCommit;
                this.panelSetRecoveryQuestion.setText(this.controlSetQuestion[this.qaIndex], this.qaIndex + 1 + ": " + this.jfb[this.qaIndex]);
                this.panelSetRecoveryQuestion.setText(this.controlSetAnswer[this.qaIndex], "");
                this.qaIndex = -1;
            }

        } else {
            this.panelSetRecoveryQuestion.handleMouse(this.getMouseX(), this.getMouseY(), this.getMouseButtonDown(), this.getLastMouseDown());

            int var3;
            for (int var1 = 0; var1 < 5; ++var1) {
                if (this.panelSetRecoveryQuestion.isClicked(this.controlCustomQuestion[var1])) {
                    boolean var2 = false;

                    while (!var2) {
                        this.recoveryQAindices[var1] = (this.recoveryQAindices[var1] + 1) % this.questions.length;
                        var2 = true;

                        for (var3 = 0; var3 < 5; ++var3) {
                            if (var3 != var1 && this.recoveryQAindices[var3] == this.recoveryQAindices[var1]) {
                                var2 = false;
                            }
                        }
                    }

                    this.jfb[var1] = "~:" + this.recoveryQAindices[var1];
                    this.panelSetRecoveryQuestion.setText(this.controlSetQuestion[var1], var1 + 1 + ": " + this.questions[this.recoveryQAindices[var1]]);
                    this.panelSetRecoveryQuestion.setText(this.controlSetAnswer[var1], "");
                }
            }

            for (int var8 = 0; var8 < 5; ++var8) {
                if (this.panelSetRecoveryQuestion.isClicked(this.controlCustomAnswer[var8])) {
                    this.qaIndex = var8;
                    this.chatMessageInput = "";
                    this.chatMessageInputCommit = "";
                }
            }

            if (this.panelSetRecoveryQuestion.isClicked(this.finishSetRecovery)) {
                var3 = 0;

                while (true) {
                    if (var3 >= 5) {
                        int var6;
                        for (int var9 = 0; var9 < 5; ++var9) {
                            String var5 = this.panelSetRecoveryQuestion.getControlText(this.controlSetAnswer[var9]);

                            for (var6 = 0; var6 < var9; ++var6) {
                                String var7 = this.panelSetRecoveryQuestion.getControlText(this.controlSetAnswer[var6]);
                                if (var5.equalsIgnoreCase(var7)) {
                                    this.panelSetRecoveryQuestion.setText(this.controlRecoveryInstruction, "@yel@Each question must have a different answer");
                                    return;
                                }
                            }
                        }

                        //todo: check opcode
                        this.packetHandler.getClientStream().newPacket(208);

                        for (int var10 = 0; var10 < 5; ++var10) {
                            String question = this.jfb[var10];
                            if (question == null || question.length() == 0) {
                                question = String.valueOf(var10 + 1);
                            }

                            if (question.length() > 50) {
                                question = question.substring(0, 50);
                            }

                            String answer = this.panelSetRecoveryQuestion.getControlText(this.controlSetAnswer[var10]);

                            if (answer.length() > 100) {
                                answer = answer.substring(0, 100);
                            }

                            this.packetHandler.getClientStream().writeBuffer1.putByte(question.length());
                            this.packetHandler.getClientStream().writeBuffer1.putString(question);
                            this.packetHandler.getClientStream().writeBuffer1.putByte(answer.length());
                            this.packetHandler.getClientStream().writeBuffer1.putString(answer);
                            //todo put encrypted??
                            //conn.enc_cred_put(DataUtil.method_13(this.panelSetRecoveryQuestion.getControlText(this.controlSetAnswer[var10])),
                            //		super.sess_id, this.rsa_exponent, this.rsa_modulus);
                        }

                        this.packetHandler.getClientStream().finishPacket();

                        for (var6 = 0; var6 < 5; ++var6) {
                            this.recoveryQAindices[var6] = var6;
                            this.jfb[var6] = "~:" + this.recoveryQAindices[var6];
                            this.panelSetRecoveryQuestion.setText(this.controlSetAnswer[var6], "");
                            this.panelSetRecoveryQuestion.setText(this.controlSetQuestion[var6], var6 + 1 + ": " + this.questions[this.recoveryQAindices[var6]]);
                        }

                        this.panelSetRecoveryQuestion.setFocus(this.controlSetAnswer[0]);

                        this.getSurface().blackScreen(true);
                        this.showSetRecoveryQuestion = false;
                        break;
                    }

                    String ans = this.panelSetRecoveryQuestion.getControlText(this.controlSetAnswer[var3]);
                    if (ans == null || ans.length() < 3) {
                        this.panelSetRecoveryQuestion.setText(this.controlRecoveryInstruction, "@yel@Please provide a longer answer to question: " + (var3 + 1));
                        return;
                    }

                    ++var3;
                }
            }

        }
    }

    public void method_182() {
        this.getSurface().interlace = false;
        this.getSurface().blackScreen(true);
        this.panelSetRecoveryQuestion.drawPanel();
        if (this.qaIndex != -1) {
            int y = 150;
            this.getSurface().drawBox(26, y, 460, 60, 0);
            this.getSurface().drawBoxBorder(26, y, 460, 60, 0xFFFFFF);
            y += 22;
            this.getSurface().drawColoredStringCentered(256, "Please enter your question", 0xFFFFFF, 0, 4, y);
            y += 25;
            this.getSurface().drawColoredStringCentered(256, this.chatMessageInput + "*", 0xFFFFFF, 0, 4, y);
        }

        this.getSurface().drawSprite(spriteSelect(EntityHandler.GUIparts.get(GUIPARTS.BLUEBAR.id())), 0, this.gameHeight);
        // this.getSurface().draw(this.graphics, this.screenOffsetX,
        // 256, this.screenOffsetY);
        clientPort.draw();
    }

    private void createPasswordRecoveryPanel() {
        this.panelRecovery = new Panel(this.getSurface(), 100);
        int i1 = 10;
        this.instructPassRecovery1 = this.panelRecovery.addCenteredText(256, i1, "@yel@To prove this is your account please provide the answers to", 1, true);
        i1 += 15;
        this.instructPassRecovery2 = this.panelRecovery.addCenteredText(256, i1, "@yel@your security questions. You will then be able to reset your password", 1, true);
        i1 += 35;
        for (int j1 = 0; j1 < 5; j1++) {
            this.panelRecovery.addButtonBackground(256, i1, 410, 30);
            this.controlPassQuestion[j1] = this.panelRecovery.addCenteredText(256, i1 - 7, (j1 + 1) + ": question?", 1, true);
            this.controlPassAnswer[j1] = this.panelRecovery.addCenteredTextEntry(256, i1 + 7, 310, 80, 30, 1, true, true);
            i1 += 35;
        }

        this.panelRecovery.setFocus(this.controlPassAnswer[0]);
        this.panelRecovery.addButtonBackground(256, i1, 410, 30);
        this.panelRecovery.addCenteredText(256, i1 - 7, "If you know it, enter a previous password used on this account", 1, true);
        this.controlPreviousPassword = this.panelRecovery.addCenteredTextEntry(256, i1 + 7, 310, 80, 30, 1, true, true);
        i1 += 35;
        this.panelRecovery.addButtonBackground(151, i1, 200, 30);
        this.panelRecovery.addCenteredText(151, i1 - 7, "Choose a NEW password", 1, true);
        this.controlNewPassword = this.panelRecovery.addCenteredTextEntry(146, i1 + 7, 200, 80, 30, 1, true, true);
        this.panelRecovery.addButtonBackground(361, i1, 200, 30);
        this.panelRecovery.addCenteredText(361, i1 - 7, "Confirm new password", 1, true);
        this.controlConfirmation = this.panelRecovery.addCenteredTextEntry(366, i1 + 7, 200, 80, 30, 1, true, true);
        i1 += 35;
        this.panelRecovery.addButtonBackground(201, i1, 100, 30);
        this.panelRecovery.addCenteredText(201, i1, "Submit", 4, true);
        this.passwordRecoverSubmit = this.panelRecovery.addButton(201, i1, 100, 30);
        this.panelRecovery.addButtonBackground(311, i1, 100, 30);
        this.panelRecovery.addCenteredText(311, i1, "Cancel", 4, true);
        this.passwordRecoverCancel = this.panelRecovery.addButton(311, i1, 100, 30);
    }

    public void setShowRecoveryDialogue(boolean show) {
        this.showSetRecoveryQuestion = show;

        for (int i = 0; i < 5; ++i) {
            this.recoveryQAindices[i] = i;
            this.jfb[i] = "~:" + this.recoveryQAindices[i];
            this.panelSetRecoveryQuestion.setText(this.controlSetAnswer[i], "");
            this.panelSetRecoveryQuestion.setText(this.controlSetQuestion[i], i + 1 + ": " + this.questions[this.recoveryQAindices[i]]);
        }
    }

    public void setShowContactDialogue(boolean show) {
        this.showSetContactDetails = show;

        this.panelContact.setText(this.controlContactName, "");
        this.panelContact.setText(this.controlContactZipCode, "");
        this.panelContact.setText(this.controlContactCountry, "");
        this.panelContact.setText(this.controlContactEmail, "");
    }

    public void createContactDetailsPanel() {
        this.panelContact = new Panel(this.getSurface(), 100);
        int i = 256;
        int j = 400;
        int k = 25;
        this.instructContactDetails = this.panelContact.addCenteredText(256, k, "@yel@Please supply your contact details", 5, true);
        k += 30;
        this.panelContact.addCenteredText(256, k, "We need this information to provide an efficient customer support service ", 1, true);
        k += 15;
        this.panelContact.addCenteredText(256, k, "and also to work out where to locate future RuneScape servers.", 1, true);
        k += 25;
        this.panelContact.addCenteredText(256, k, "We know some people are concerned about entering their email address on", 1, true);
        k += 15;
        this.panelContact.addCenteredText(255, k, "websites, and for this reason we take our users privacy very seriously.", 1, true);
        k += 15;
        this.panelContact.addCenteredText(256, k, "For our full policy please click the relevant link below this game window", 1, true);
        k += 40;
        this.panelContact.addButtonBackground(i, k, j, 30);
        this.panelContact.addCenteredText(i, k - 7, "Full name", 1, true);
        this.controlContactName = this.panelContact.addCenteredTextEntry(i, k + 7, j, 80, 30, 1, false, true);
        k += 35;
        this.panelContact.addButtonBackground(i, k, j, 30);
        this.panelContact.addCenteredText(i, k - 7, "Postcode/Zipcode", 1, true);
        this.controlContactZipCode = this.panelContact.addCenteredTextEntry(i, k + 7, j, 80, 30, 1, false, true);
        k += 35;
        this.panelContact.addButtonBackground(i, k, j, 30);
        this.panelContact.addCenteredText(i, k - 7, "Country", 1, true);
        this.controlContactCountry = this.panelContact.addCenteredTextEntry(i, k + 7, j, 80, 30, 1, false, true);
        k += 35;
        this.panelContact.addButtonBackground(i, k, j, 30);
        this.panelContact.addCenteredText(i, k - 7, "Email address", 1, true);
        this.controlContactEmail = this.panelContact.addCenteredTextEntry(i, k + 7, j, 80, 30, 1, false, true);
        k += 35;
        this.panelContact.addButtonBackground(i, k, 100, 30);
        this.panelContact.addCenteredText(i, k, "Submit", 4, true);
        this.finishSubmitContact = this.panelContact.addButton(i, k, 100, 30);
        this.panelContact.setFocus(this.controlContactName);
    }

    public void L() {
        this.getSurface().interlace = false;
        this.getSurface().blackScreen(true);
        this.panelContact.drawPanel();
        this.getSurface().drawSprite(spriteSelect(EntityHandler.GUIparts.get(GUIPARTS.BLUEBAR.id())), 0, this.gameHeight);
        clientPort.draw();
    }

    public void M() {
        this.panelContact.handleMouse(this.mouseX, this.mouseY, this.currentMouseButtonDown,
                this.lastMouseButtonDown);
        if (this.panelContact.isClicked(this.controlContactName)) {
            this.panelContact.setFocus(this.controlContactZipCode);
        }
        if (this.panelContact.isClicked(this.controlContactZipCode)) {
            this.panelContact.setFocus(this.controlContactCountry);
        }
        if (this.panelContact.isClicked(this.controlContactCountry)) {
            this.panelContact.setFocus(this.controlContactEmail);
        }
        if (this.panelContact.isClicked(this.controlContactEmail)) {
            this.panelContact.setFocus(this.controlContactName);
        }
        if (this.panelContact.isClicked(this.finishSubmitContact)) {
            String name = this.panelContact.getControlText(this.controlContactName);
            String zipCode = this.panelContact.getControlText(this.controlContactZipCode);
            String country = this.panelContact.getControlText(this.controlContactCountry);
            String email = this.panelContact.getControlText(this.controlContactEmail);
            //to get from parameters later!
            boolean emptyFields = ((name == null) || (name.trim().length() == 0))
                    && ((zipCode == null) || (zipCode.trim().length() == 0))
                    && ((country == null) || (country.trim().length() == 0))
                    && ((email == null) || (email.trim().length() == 0));
            boolean shouldCancel = emptyFields && isLenientContactDetails();
            if (shouldCancel) {
                this.panelContact.setFocus(this.controlContactName);
                this.getSurface().blackScreen(true);
                this.showSetContactDetails = false;
                return;
            } else if (!isLenientContactDetails() && ((name == null) || (name.trim().length() == 0) || (zipCode == null) || (zipCode.trim().length() == 0) || (country == null) || (country.trim().length() == 0) || (email == null) || (email.trim().length() == 0))) {
                this.panelContact.setText(this.instructContactDetails, "@yel@Please fill in all the requested details");
                return;
            } else if (email != null && email.trim().length() > 0 && !isValidEmailAddress(email)) {
                this.panelContact.setText(this.instructContactDetails, "@yel@Please use a valid email address");
                return;
            }

            this.packetHandler.getClientStream().newPacket(253);
            this.packetHandler.getClientStream().writeBuffer1.putByte(name.length());
            this.packetHandler.getClientStream().writeBuffer1.putString(name);
            this.packetHandler.getClientStream().writeBuffer1.putByte(zipCode.length());
            this.packetHandler.getClientStream().writeBuffer1.putString(zipCode);
            this.packetHandler.getClientStream().writeBuffer1.putByte(country.length());
            this.packetHandler.getClientStream().writeBuffer1.putString(country);
            this.packetHandler.getClientStream().writeBuffer1.putByte(email.length());
            this.packetHandler.getClientStream().writeBuffer1.putString(email);
            this.packetHandler.getClientStream().finishPacket();

            this.panelContact.setFocus(this.controlContactName);

            this.getSurface().blackScreen(true);
            this.showSetContactDetails = false;
        }
    }

    private void createAppearancePanel(int var1) {
        try {
            this.panelAppearance = new Panel(this.getSurface(), 100);

            this.panelAppearance.addCenteredText(256, 10, "Please design Your Character", 4, true);
            short var2 = 140;
            byte var3 = 34;
            int var6 = var2 + 116;
            int yFromTopDistance = var3 - 10;
            this.panelAppearance.addCenteredText(var6 - 55, yFromTopDistance + 110, "Front", 3, true);
            this.panelAppearance.addCenteredText(var6, yFromTopDistance + 110, "Side", 3, true);
            this.panelAppearance.addCenteredText(var6 + 55, 110 + yFromTopDistance, "Back", 3, true);
            yFromTopDistance += 145;
            byte var4 = 54;
            this.panelAppearance.addDecoratedBox((var6 - var4), yFromTopDistance, 53, 41);
            this.panelAppearance.addCenteredText(var6 - var4, yFromTopDistance - 8, "Head", 1, true);
            this.panelAppearance.addCenteredText(var6 - var4, yFromTopDistance + 8, "Type", 1, true);
            this.panelAppearance.addSprite(var6 - var4 - 40, yFromTopDistance, spriteSelect(EntityHandler.GUIparts.get(GUIPARTS.LEFTARROW.id())));
            this.controlButtonAppearanceHeadMinus = this.panelAppearance.addButton(-40 - var4 + var6, yFromTopDistance, 20, 20);
            this.panelAppearance.addSprite(var6 - var4 + 40, yFromTopDistance, spriteSelect(EntityHandler.GUIparts.get(GUIPARTS.RIGHTARROW.id())));
            this.controlButtonAppearanceHeadPlus = this.panelAppearance.addButton(var6 + (40 - var4), yFromTopDistance, 20, 20);
            this.panelAppearance.addDecoratedBox((var6 + var4), yFromTopDistance, 53, 41);
            this.panelAppearance.addCenteredText(var6 + var4, yFromTopDistance - 8, "Hair", 1, true);
            this.panelAppearance.addCenteredText(var4 + var6, 8 + yFromTopDistance, "Color", 1, true);
            this.panelAppearance.addSprite(var4 + (var6 - 40), yFromTopDistance, spriteSelect(EntityHandler.GUIparts.get(GUIPARTS.LEFTARROW.id())));
            this.m_Kj = this.panelAppearance.addButton(var6 + var4 - 40, yFromTopDistance, 20, 20);
            this.panelAppearance.addSprite(40 + var4 + var6, yFromTopDistance, spriteSelect(EntityHandler.GUIparts.get(GUIPARTS.RIGHTARROW.id())));
            this.m_ed = this.panelAppearance.addButton(40 + var4 + var6, yFromTopDistance, 20, 20);
            yFromTopDistance += 50;
            this.panelAppearance.addDecoratedBox((var6 - var4), yFromTopDistance, 53, 41);
            this.panelAppearance.addCenteredText(var6 - var4, yFromTopDistance, "Gender", 1, true);
            this.panelAppearance.addSprite(var6 - var4 - 40, yFromTopDistance, spriteSelect(EntityHandler.GUIparts.get(GUIPARTS.LEFTARROW.id())));
            this.m_Ge = this.panelAppearance.addButton(var6 - 40 - var4, yFromTopDistance, 20, 20);
            this.panelAppearance.addSprite(40 - var4 + var6, yFromTopDistance, spriteSelect(EntityHandler.GUIparts.get(GUIPARTS.RIGHTARROW.id())));
            this.m_Of = this.panelAppearance.addButton(40 + (var6 - var4), yFromTopDistance, 20, 20);
            this.panelAppearance.addDecoratedBox((var4 + var6), yFromTopDistance, 53, 41);
            this.panelAppearance.addCenteredText(var4 + var6, yFromTopDistance - 8, "Top", 1, true);
            this.panelAppearance.addCenteredText(var4 + var6, 8 + yFromTopDistance, "Color", 1, true);
            this.panelAppearance.addSprite(var6 + (var4 - 40), yFromTopDistance, spriteSelect(EntityHandler.GUIparts.get(GUIPARTS.LEFTARROW.id())));
            this.m_Xc = this.panelAppearance.addButton(var4 + (var6 - 40), yFromTopDistance, 20, 20);
            this.panelAppearance.addSprite(40 + var4 + var6, yFromTopDistance, spriteSelect(EntityHandler.GUIparts.get(GUIPARTS.RIGHTARROW.id())));
            this.m_ek = this.panelAppearance.addButton(var6 - (-var4 - 40), yFromTopDistance, 20, 20);
            yFromTopDistance += 50;
            if (var1 != -24595) {
                this.renderLoginScreenViewports(-127);
            }

            this.panelAppearance.addDecoratedBox((var6 - var4), yFromTopDistance, 53, 41);
            this.panelAppearance.addCenteredText(var6 - var4, yFromTopDistance - 8, "Skin", 1, true);
            this.panelAppearance.addCenteredText(var6 - var4, yFromTopDistance + 8, "Color", 1, true);
            this.panelAppearance.addSprite(var6 - 40 - var4, yFromTopDistance, spriteSelect(EntityHandler.GUIparts.get(GUIPARTS.LEFTARROW.id())));
            this.m_Ze = this.panelAppearance.addButton(var6 - var4 - 40, yFromTopDistance, 20, 20);
            this.panelAppearance.addSprite(var6 - var4 + 40, yFromTopDistance, spriteSelect(EntityHandler.GUIparts.get(GUIPARTS.RIGHTARROW.id())));
            this.m_Mj = this.panelAppearance.addButton(var6 + (40 - var4), yFromTopDistance, 20, 20);
            this.panelAppearance.addDecoratedBox((var4 + var6), yFromTopDistance, 53, 41);
            this.panelAppearance.addCenteredText(var4 + var6, yFromTopDistance - 8, "Bottom", 1, true);
            this.panelAppearance.addCenteredText(var4 + var6, yFromTopDistance + 8, "Color", 1, true);
            this.panelAppearance.addSprite(var4 - 40 + var6, yFromTopDistance, spriteSelect(EntityHandler.GUIparts.get(GUIPARTS.LEFTARROW.id())));
            this.m_Re = this.panelAppearance.addButton(var6 - (40 - var4), yFromTopDistance, 20, 20);
            this.panelAppearance.addSprite(var6 + var4 + 40, yFromTopDistance, spriteSelect(EntityHandler.GUIparts.get(GUIPARTS.RIGHTARROW.id())));
            this.m_Ai = this.panelAppearance.addButton(40 + var4 + var6, yFromTopDistance, 20, 20);
            yFromTopDistance += 82;
            yFromTopDistance -= 35;
            this.panelAppearance.addButtonBackground(var6, yFromTopDistance, 200, 30);
            this.panelAppearance.addCenteredText(var6, yFromTopDistance, "Accept", 4, false);
            this.m_Eg = this.panelAppearance.addButton(var6, yFromTopDistance, 200, 30);
        } catch (RuntimeException var5) {
            throw GenUtil.makeThrowable(var5, "client.J(" + var1 + ')');
        }
    }

    private void createLoginPanels(int var1) {
        try {

            this.panelLoginWelcome = new Panel(this.getSurface(), 50);
            byte yOffsetWelcome = 40;
            int yOffsetLogin = 0;
            if (isAndroid()) {
                yOffsetWelcome = -125;
                yOffsetLogin = -200;
            }

            if (!wantMembers()) { // Free version
                this.panelLoginWelcome.addCenteredText(halfGameWidth(), halfGameHeight() + 23 + yOffsetWelcome, "Welcome to " + getServerNameWelcome(), 4, true);
                this.panelLoginWelcome.addCenteredText(halfGameWidth(), halfGameHeight() + 38 + yOffsetWelcome, getWelcomeText(), 4, true);

                panelLoginWelcome.addButtonBackground(halfGameWidth() - 100, halfGameHeight() + 73 + yOffsetWelcome, 120, 35);
                panelLoginWelcome.addButtonBackground(halfGameWidth() + 100, halfGameHeight() + 73 + yOffsetWelcome, 120, 35);

                panelLoginWelcome.addCenteredText(halfGameWidth() - 100, halfGameHeight() + 73 + yOffsetWelcome, "New User", 5, false);
                panelLoginWelcome.addCenteredText(halfGameWidth() + 100, halfGameHeight() + 73 + yOffsetWelcome, "Existing User", 5, false);

                loginButtonNewUser = panelLoginWelcome.addButton(halfGameWidth() - 100, halfGameHeight() + 73 + yOffsetWelcome, 120, 35);
                loginButtonExistingUser = panelLoginWelcome.addButton(halfGameWidth() + 100, halfGameHeight() + 73 + yOffsetWelcome, 120, 35);
            } else { // Members version
                this.panelLoginWelcome.addCenteredText(halfGameWidth(), halfGameHeight() + 33 + yOffsetWelcome, "Welcome to " + getServerNameWelcome(), 4, true);
                this.panelLoginWelcome.addCenteredText(halfGameWidth(), halfGameHeight() + 48 + yOffsetWelcome, getWelcomeText(), 4, true);

                panelLoginWelcome.addButtonBackground(halfGameWidth() - 100, halfGameHeight() + 83 + yOffsetWelcome, 120, 35);
                panelLoginWelcome.addButtonBackground(halfGameWidth() + 100, halfGameHeight() + 83 + yOffsetWelcome, 120, 35);

                panelLoginWelcome.addCenteredText(halfGameWidth() - 100, halfGameHeight() + 83 + yOffsetWelcome, "New User", 5, false);
                panelLoginWelcome.addCenteredText(halfGameWidth() + 100, halfGameHeight() + 83 + yOffsetWelcome, "Existing User", 5, false);

                loginButtonNewUser = panelLoginWelcome.addButton(halfGameWidth() - 100, halfGameHeight() + 83 + yOffsetWelcome, 120, 35);
                loginButtonExistingUser = panelLoginWelcome.addButton(halfGameWidth() + 100, halfGameHeight() + 83 + yOffsetWelcome, 120, 35);
            }

            this.panelLogin = new Panel(this.getSurface(), 50);
            short androidHeightOffset = isAndroid() ? (short) 30 : 230;
            this.controlLoginStatus1 = this.panelLogin.addCenteredText(halfGameWidth(), halfGameHeight() + 35 + yOffsetLogin, "", 4, true);
            this.controlLoginStatus2 = this.panelLogin.addCenteredText(halfGameWidth(), halfGameHeight() + 55 + yOffsetLogin,
                    "Please enter your username and password", 4, true);
            int var6 = androidHeightOffset + 28;
            this.panelLogin.addButtonBackground(halfGameWidth() - 116, halfGameHeight() + 91 + yOffsetLogin, 200, 40);
            this.panelLogin.addCenteredText(halfGameWidth() - 116, halfGameHeight() + 81 + yOffsetLogin, "Username:", 4, false);
            this.controlLoginUser = this.panelLogin.addCenteredTextEntry(halfGameWidth() - 116, halfGameHeight() + 98 + yOffsetLogin, 200, 320, 40, 4, false, false);

            if (var1 != 3845) {
                this.drawNPC(51, 106, -15, -96, 26, 108, 22, -63);
            }

            this.panelLogin.addButtonBackground(halfGameWidth() - 46, halfGameHeight() + 138 + yOffsetLogin, 200, 40);
            this.panelLogin.addCenteredText(halfGameWidth() - 46, halfGameHeight() + 128 + yOffsetLogin, "Password:", 4, false);
            this.controlLoginPass = this.panelLogin.addCenteredTextEntry(halfGameWidth() - 46, halfGameHeight() + 146 + yOffsetLogin, 200, 20, 40, 4, true, false);

            if (Remember()) {
                String cred = ClientPort.loadCredentials();
                if (cred.length() > 0) {
                    String[] split = cred.split(",");
                    if (split.length == 2) {
                        String user = split[0];
                        String pass = split[1];
                        this.panelLogin.setText(this.controlLoginUser, user);
                        this.panelLogin.setText(this.controlLoginPass, pass);
                    }
                }
            }

            this.panelLogin.addButtonBackground(halfGameWidth() + 154, halfGameHeight() + 83 + yOffsetLogin, 120, 25);
            this.panelLogin.addCenteredText(halfGameWidth() + 154, halfGameHeight() + 83 + yOffsetLogin, "Ok", 4, false);
            this.m_be = this.panelLogin.addButton(halfGameWidth() + 154, halfGameHeight() + 83 + yOffsetLogin, 120, 25);
            this.panelLogin.addButtonBackground(halfGameWidth() + 154, halfGameHeight() + 113 + yOffsetLogin, 120, 25);
            this.panelLogin.addCenteredText(halfGameWidth() + 154, halfGameHeight() + 113 + yOffsetLogin, "Cancel", 4, false);
            this.m_Xi = this.panelLogin.addButton(halfGameWidth() + 154, halfGameHeight() + 113 + yOffsetLogin, 120, 25);
            this.panelLogin.setFocus(this.controlLoginUser);
            this.panelLogin.addButtonBackground(halfGameWidth() + 154, halfGameHeight() + 143 + yOffsetLogin, 120, 25);
            this.panelLogin.addCenteredText(halfGameWidth() + 154, halfGameHeight() + 143 + yOffsetLogin, "Forgot password", 4, false);
            this.lostPasswordButtonIdx = this.panelLogin.addButton(halfGameWidth() + 154, halfGameHeight() + 143 + yOffsetLogin, 120, 25);

            //int offRememb = -1;
            //int offHide = -1;
			/*int width = 120;
			if (S_WANT_HIDE_IP && (Remember())) {
				offRememb = 124;
				offHide = 186;
				width = 60;
			} else if (S_WANT_HIDE_IP) {
				offHide = 154;
			} else if (Remember()) {
				offRememb = 154;
			}*/

            if (S_WANT_HIDE_IP) {
                this.settingsHideIP = ClientPort.loadHideIp();
                String text = (this.settingsHideIP != 1) ? "Hide IP" : "Show IP";
                this.panelLogin.addButtonBackground(halfGameWidth() + 24, halfGameHeight() + 91 + yOffsetLogin, 60, 40);
                this.panelLogin.addCenteredText(halfGameWidth() + 24, halfGameHeight() + 91 + yOffsetLogin, text, 3, false);
                this.hideIpButtonIdx = this.panelLogin.addButton(halfGameWidth() + 24, halfGameHeight() + 91 + yOffsetLogin, 60, 40);
            }
            if (Remember()) {
                this.panelLogin.addButtonBackground(halfGameWidth() - 186, halfGameHeight() + 138 + yOffsetLogin, 60, 40);
                this.panelLogin.addCenteredText(halfGameWidth() - 186, halfGameHeight() + 138 + yOffsetLogin, "Save", 3, false);
                this.rememberButtonIdx = this.panelLogin.addButton(halfGameWidth() - 186, halfGameHeight() + 138 + yOffsetLogin, 60, 40);
            }

            /* Registration setup */

            menuNewUser = new Panel(getSurface(), 50);
            if (isAndroid()) {
                menuNewUser.addCenteredText(halfGameWidth() - 6, halfGameHeight() - 149, "@whi@To open keyboard press the back button", 5, false);
            }
            menuNewUser.addCenteredText(halfGameWidth() - 6, halfGameHeight() - 127, "@whi@Enter a username between 2 and 12 characters long", 1, false);
            menuNewUser.addCenteredText(halfGameWidth() - 6, halfGameHeight() - 116, "@red@(Only regular letters, numbers and spaces are allowed)", 0, false);
            menuNewUser.addButtonBackground(halfGameWidth() - 6, halfGameHeight() - 90, 420, 34);
            menuNewUser.addCenteredText(halfGameWidth() - 6, halfGameHeight() - 99, "Choose a Username (This is the name other users will see)", 4,
                    false);
            menuNewUserUsername = menuNewUser.addCenteredTextEntry(halfGameWidth() - 6, halfGameHeight() - 82, 200, 12, 40, 4, false, false);

            if (!wantEmail()) { // moves the password box down a bit for a clean look
                menuNewUser.addCenteredText(halfGameWidth() - 6, halfGameHeight() - 38, "@whi@Password must be at least between 4 and 64 characters long", 1, false);
                menuNewUser.addCenteredText(halfGameWidth() - 6, halfGameHeight() - 27, "@red@(DO NOT use the same password that you use elsewhere. Regular letters and numbers only)", 0, false);

                menuNewUser.addButtonBackground(halfGameWidth() - 6, halfGameHeight() - 1, 420, 34);
                menuNewUser.addCenteredText(halfGameWidth() - 6, halfGameHeight() - 6, "Choose a Password (You will require this to login)", 4, false);
                menuNewUserPassword = menuNewUser.addCenteredTextEntry(halfGameWidth(), halfGameHeight() + 7, 200, 64, 40, 4, true, false);
            } else { // leaves space for the email box below
                menuNewUser.addCenteredText(halfGameWidth() - 6, halfGameHeight() - 64, "@whi@Password must be at least between 4 and 64 characters long", 1, false);
                menuNewUser.addCenteredText(halfGameWidth() - 6, halfGameHeight() - 53, "@red@(DO NOT use the same password that you use elsewhere. Regular letters and numbers only)", 0, false);

                menuNewUser.addButtonBackground(halfGameWidth() - 6, halfGameHeight() - 28, 420, 34);
                menuNewUser.addCenteredText(halfGameWidth() - 6, halfGameHeight() - 37, "Choose a Password (You will require this to login)", 4, false);
                menuNewUserPassword = menuNewUser.addCenteredTextEntry(halfGameWidth(), halfGameHeight() - 20, 200, 64, 40, 4, true, false);
            }

            if (wantEmail()) {
                menuNewUser.addCenteredText(halfGameWidth() - 6, halfGameHeight() - 2, "@whi@It's recommended to use a valid email address", 1, false);
                menuNewUser.addButtonBackground(halfGameWidth() - 6, halfGameHeight() + 26, 420, 34);
                menuNewUser.addCenteredText(halfGameWidth() - 6, halfGameHeight() + 17, "E-mail address", 4, false);
                menuNewUserEmail = menuNewUser.addCenteredTextEntry(halfGameWidth(), halfGameHeight() + 34, 200, 40, 40, 4, false, false);
            }

            // menuNewUser.addButtonBackground(250, i + 22, 420, 44);

            menuNewUser.addButtonBackground(halfGameWidth() - 81, halfGameHeight() + 66, 270, 34);
            menuNewUserStatus = menuNewUser.addCenteredText(halfGameWidth() - 81, halfGameHeight() + 57,
                    "To create an account please enter", 4, true);
            menuNewUserStatus2 = menuNewUser.addCenteredText(halfGameWidth() - 81, halfGameHeight() + 74,
                    "all the requested details", 4, true);

            menuNewUser.addButtonBackground(halfGameWidth() + 94, halfGameHeight() + 66, 70, 34);
            menuNewUser.addCenteredText(halfGameWidth() + 94, halfGameHeight() + 66, "Submit", 5, false);
            menuNewUserSubmit = menuNewUser.addButton(halfGameWidth() + 79, halfGameHeight() + 66, 100, 34);

            menuNewUser.addButtonBackground(halfGameWidth() + 169, halfGameHeight() + 66, 70, 34);
            menuNewUser.addCenteredText(halfGameWidth() + 169, halfGameHeight() + 66, "Cancel", 5, false);
            menuNewUserCancel = menuNewUser.addButton(halfGameWidth() + 169, halfGameHeight() + 66, 100, 34);

        } catch (RuntimeException var4) {
            throw GenUtil.makeThrowable(var4, "client.B(" + var1 + ')');
        }
    }

    private void createMessageTabPanel(int var1) {
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

    public final ORSCharacter createNpc(int sprite, int type, int x, int y, int serverIndex) {
        try {
            if (null == this.npcsServer[serverIndex]) {
                this.npcsServer[serverIndex] = new ORSCharacter();
                this.npcsServer[serverIndex].serverIndex = serverIndex;
            }


            ORSCharacter character = this.npcsServer[serverIndex];
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
                waypointIdx = character.waypointIndexCurrent;
                if (x != character.waypointsX[waypointIdx] || y != character.waypointsZ[waypointIdx]) {
                    character.waypointIndexCurrent = waypointIdx = (1 + waypointIdx) % 10;
                    character.waypointsX[waypointIdx] = x;
                    character.waypointsZ[waypointIdx] = y;
                }
            } else {
                character.serverIndex = serverIndex;
                character.waypointIndexCurrent = 0;
                character.waypointIndexNext = 0;
                character.waypointsX[0] = character.currentX = x;
                character.direction = ORSCharacterDirection.lookup(sprite);
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

    private void createPacket64(int v1, int v2, int v3, int v4) {
        try {
            this.packetHandler.getClientStream().newPacket(64);

            this.packetHandler.getClientStream().writeBuffer1.putByte(v1);
            this.packetHandler.getClientStream().writeBuffer1.putByte(v2);
            this.packetHandler.getClientStream().writeBuffer1.putByte(v3);
            this.packetHandler.getClientStream().writeBuffer1.putByte(v4);
            this.packetHandler.getClientStream().finishPacket();
        } catch (RuntimeException var7) {
            throw GenUtil.makeThrowable(var7, "client.L(" + v3 + ',' + v2 + ',' + v1 + ',' + "dummy" + ',' + v4 + ')');
        }
    }

    public int getLocalPlayerServerIndex() {
        return this.localPlayerServerIndex;
    }

    public void setLocalPlayerServerIndex(int i) {
        this.localPlayerServerIndex = i;
    }

    public int getM_rc() {
        return this.m_rc;
    }

    public void setM_rc(int i) {
        this.m_rc = i;
    }

    public final ORSCharacter createPlayer(int zPosition, int serverIndex, int xPosition, int var4, ORSCharacterDirection direction) {
        try {
            if (null == this.playerServer[serverIndex]) {
                this.playerServer[serverIndex] = new ORSCharacter();
                this.playerServer[serverIndex].serverIndex = serverIndex;
            }


            ORSCharacter c = this.playerServer[serverIndex];
            boolean existingCharFound = false;

            int i;
            for (i = 0; i < this.knownPlayerCount; ++i) {
                if (this.knownPlayers[i].serverIndex == serverIndex) {
                    existingCharFound = true;
                    break;
                }
            }

            if (existingCharFound) {
                c.animationNext = direction.rsDir;
                i = c.waypointIndexCurrent;
                if (xPosition != c.waypointsX[i] || c.waypointsZ[i] != zPosition) {
                    c.waypointIndexCurrent = i = (1 + i) % 10;
                    c.waypointsX[i] = xPosition;
                    c.waypointsZ[i] = zPosition;
                }
            } else {
                c.serverIndex = serverIndex;
                c.waypointsX[0] = c.currentX = xPosition;
                c.waypointIndexCurrent = 0;
                c.waypointIndexNext = 0;
                c.stepFrame = 0;
                c.direction = direction;
                c.animationNext = direction.rsDir;
                c.waypointsZ[0] = c.currentZ = zPosition;
            }

            this.players[this.playerCount++] = c;
            return c;
        } catch (RuntimeException var9) {
            throw GenUtil.makeThrowable(var9,
                    "client.P(" + zPosition + ',' + serverIndex + ',' + xPosition + ',' + var4 + ',' + direction + ')');
        }
    }

    private void createTopMouseMenu(int var1) {
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
                    this.menuCommon.removeItem((var3 - 1));
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
                        this.getSurface().drawString(var9, 6, 14, 0xFFFF00, 1);
                    }
                }

                var3 = this.menuCommon.getItemCount(-27153);
                if (var3 > 0) {
                    var4 = -1;

                    for (var5 = 0; var5 < var3; ++var5) {
                        String var6 = this.menuCommon.getItemActor(var5);
                        if (null != var6 && var6.length() > 0) {
                            var4 = var5;
                            break;
                        }
                    }

                    String var11 = null;
                    if ((this.selectedItemInventoryIndex >= 0 || this.selectedSpell >= 0) && var3 == 1) {
                        var11 = "Choose a target";
                    } else if ((this.selectedItemInventoryIndex >= 0 || this.selectedSpell >= 0) && var3 > 1) {
                        var11 = "@whi@" + this.menuCommon.getItemLabel(0) + " "
                                + this.menuCommon.getItemActor(0);
                    } else if (var4 != -1) {
                        var11 = this.menuCommon.getItemActor(var4) + ": @whi@"
                                + this.menuCommon.getItemLabel(0);
                    }

                    if (var3 == 2 && null != var11) {
                        var11 = var11 + "@whi@ / 1 more option";
                    }

                    if (var3 > 2 && var11 != null) {
                        var11 = var11 + "@whi@ / " + (var3 - 1) + " more options";
                    }

                    if (null != var11) {
                        this.getSurface().drawString(var11, 6, 14, 0xFFFF00, 1);
                    }

                    if (!this.optionMouseButtonOne && this.mouseButtonClick == 1
                            || this.optionMouseButtonOne && this.mouseButtonClick == 1 && var3 == 1) {
                        if (this.controlPressed && this.shiftPressed && this.menuVisible) {
                            this.packetHandler.getClientStream().newPacket(59);
                            this.packetHandler.getClientStream().writeBuffer1.putShort(this.m_rf);
                            this.packetHandler.getClientStream().writeBuffer1.putShort(this.m_Cg);
                            this.packetHandler.getClientStream().finishPacket();
                        } else {
                            this.handleMenuItemClicked(false, 0);
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

    public final RSModel createWallObjectModel(int x, int y, int type, int dir, int index) {
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
        int[] indices = new int[]{v1, v2, v3, v4};
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
                    this.setFPS(1, (byte) 106);
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
                            this.drawGame(13);
                        }
                    } catch (OutOfMemoryError var4) {
                        var4.printStackTrace();
                        this.errorLoadingMemory = true;
                    }

                }
            } else {
                clientPort.drawLoadingError();
                this.setFPS(1, (byte) 106);
            }
        } catch (RuntimeException var5) {
            throw GenUtil.makeThrowable(var5, "client.JD()");
        }
    }

    private void drawAppearancePanelCharacterSprites(int var1) {
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
                    spriteSelect(EntityHandler.getAnimationDef(this.character2Colour), 0), y, 102, (byte) 105, 64);

            // body
            this.getSurface().drawSpriteClipping(spriteSelect(EntityHandler.getAnimationDef(m_dk), 0), var5 - 32 - 55, y, 64,
                    102, this.getPlayerClothingColors()[this.m_Wg], this.getPlayerSkinColors()[this.m_hh], false, 0, 1);

            this.getSurface().drawSpriteClipping(spriteSelect(EntityHandler.getAnimationDef(appearanceHeadType), 0),
                    var5 - 32 - 55, y, 64, 102, this.getPlayerHairColors()[this.m_ld],
                    this.getPlayerSkinColors()[this.m_hh], false, 0, var1 + 13760);

            this.getSurface().spriteClip3(var5 - 32, this.getPlayerClothingColors()[this.characterBottomColour],
                    spriteSelect(EntityHandler.getAnimationDef(character2Colour), 6), y, 102, (byte) 105, 64);

            this.getSurface().drawSpriteClipping(spriteSelect(EntityHandler.getAnimationDef(this.m_dk), 6), var5 - 32, y,
                    64, 102, this.getPlayerClothingColors()[this.m_Wg], this.getPlayerSkinColors()[this.m_hh], false, 0,
                    1);

            this.getSurface().drawSpriteClipping(spriteSelect(EntityHandler.getAnimationDef(this.appearanceHeadType), 6),
                    var5 - 32, y, 64, 102, this.getPlayerHairColors()[this.m_ld], this.getPlayerSkinColors()[this.m_hh],
                    false, 0, 1);

            this.getSurface().spriteClip3(var5 + 55 - 32, this.getPlayerClothingColors()[this.characterBottomColour],
                    spriteSelect(EntityHandler.getAnimationDef(this.character2Colour), 12), y, 102, (byte) 110, 64);

            this.getSurface().drawSpriteClipping(spriteSelect(EntityHandler.getAnimationDef(this.m_dk), 12),
                    55 + (var5 - 32), y, 64, 102, this.getPlayerClothingColors()[this.m_Wg],
                    this.getPlayerSkinColors()[this.m_hh], false, 0, var1 + 13760);

            this.getSurface().drawSpriteClipping(
                    spriteSelect(EntityHandler.getAnimationDef(this.appearanceHeadType), 12), var5 + 55 - 32, y, 64, 102,
                    this.getPlayerHairColors()[this.m_ld], this.getPlayerSkinColors()[this.m_hh], false, 0, 1);
            this.getSurface().drawSprite(spriteSelect(EntityHandler.GUIparts.get(GUIPARTS.BLUEBAR.id())), 0, this.getGameHeight());
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

    public Sprite spriteSelect(ItemDef item) {
        return getSurface().spriteSelect(item);
    }

    public Sprite spriteSelect(AnimationDef animation, int offset) {
        return getSurface().spriteSelect(animation, offset);
    }

    public Sprite spriteSelect(SpriteDef sprite) {
        return getSurface().spriteSelect(sprite);
    }

    private void drawCharacterOverlay() {
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
                this.getSurface().drawWrappedCenteredString(this.characterDialogString[i], x, y, 300, 1, 0xFFFF00,
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
                this.getSurface().spriteClipping(spriteSelect(EntityHandler.GUIparts.get(GUIPARTS.CLIPPING.id())), (byte) -122, offsetY, centerX - var7 / 2,
                        var7, centerY, 85);
                int width = scale * 36 / 100;
                int height = scale * 24 / 100;
                this.getSurface().drawSpriteClipping(spriteSelect(EntityHandler.getItemDef(id)),
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

    private void drawChatMessageTabs(int var1) {
        try {
            this.getSurface().drawSpriteClipping(spriteSelect(EntityHandler.GUIparts.get(GUIPARTS.BLUEBAR.id())), 0, getGameHeight(), getGameWidth(), 10, 0, 0, false, 0, 1);
            if (S_WANT_CLANS) {
                this.getSurface().drawSprite(spriteSelect(EntityHandler.GUIparts.get(GUIPARTS.CHATTABS.id())), halfGameWidth() - 256,
                        this.getGameHeight() - 4);
            } else {
                this.getSurface().drawSprite(spriteSelect(EntityHandler.GUIparts.get(GUIPARTS.CHATTABSCLAN.id())), halfGameWidth() - 256,
                        this.getGameHeight() - 4);
            }

            if (var1 == 5) {
                int color = GenUtil.buildColor(255, 255, 255);
                if (this.messageTabSelected == MessageTab.ALL) {
                    color = GenUtil.buildColor(255, 200, 50);
                }
                if (this.messageTabActivity_Game % 30 > 15) {
                    color = GenUtil.buildColor(255, 50, 50);
                }
                this.getSurface().drawColoredStringCentered(halfGameWidth() - 200, "All messages", color, 0, 0,
                        6 + this.getGameHeight());

                color = GenUtil.buildColor(255, 255, 255);
                if (this.messageTabSelected == MessageTab.CHAT) {
                    color = GenUtil.buildColor(255, 200, 50);
                }
                if (this.messageTabActivity_Chat % 30 > 15) {
                    color = GenUtil.buildColor(255, 50, 50);
                }
                this.getSurface().drawColoredStringCentered(halfGameWidth() - 100, "Chat history", color, 0, 0,
                        this.getGameHeight() + 6);

                color = GenUtil.buildColor(255, 255, 255);
                if (this.messageTabSelected == MessageTab.QUEST) {
                    color = GenUtil.buildColor(255, 200, 50);
                }
                if (this.messageTabActivity_Quest % 30 > 15) {
                    color = GenUtil.buildColor(255, 50, 50);
                }
                this.getSurface().drawColoredStringCentered(halfGameWidth(), "Quest history", color, 0, 0,
                        6 + this.getGameHeight());

                color = GenUtil.buildColor(255, 255, 255);
                if (this.messageTabSelected == MessageTab.PRIVATE) {
                    color = GenUtil.buildColor(255, 200, 50);
                }
                if (this.messageTabActivity_Private % 30 > 15) {
                    color = GenUtil.buildColor(255, 50, 50);
                }
                this.getSurface().drawColoredStringCentered(halfGameWidth() + 100, "Private history", color, 0, 0,
                        this.getGameHeight() + 6);
                if (S_WANT_CLANS) {
                    color = GenUtil.buildColor(255, 255, 255);
                    if (this.messageTabSelected == MessageTab.CLAN) {
                        color = GenUtil.buildColor(255, 200, 50);
                    }
                    if (this.messageTabActivity_Clan % 30 > 15) {
                        color = GenUtil.buildColor(255, 50, 50);
                    }
                    this.getSurface().drawColoredStringCentered(halfGameWidth() + 200, "Clan history", color, 0, 0, 6 + this.getGameHeight());
                } else {
                    color = GenUtil.buildColor(255, 255, 255);
                    this.getSurface().drawColoredStringCentered(halfGameWidth() + 200, "Report Abuse", color, 0, 0, 6 + this.getGameHeight());
                }
            }
        } catch (RuntimeException var3) {
            throw GenUtil.makeThrowable(var3, "client.QB(" + var1 + ')');
        }
    }

    private void drawDialogCombatStyle() {
        try {

            byte sx = 7;
            byte sy = 15;
            short width;
            if (isAndroid()) {
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
                        this.packetHandler.getClientStream().newPacket(29);
                        this.packetHandler.getClientStream().writeBuffer1.putByte(this.combatStyle);
                        this.packetHandler.getClientStream().finishPacket();
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

            this.getSurface().drawColoredStringCentered(width / 2 + sx, (isAndroid() ? "C" : "Select c") + "ombat style", 0xFFFFFF, 0, 3, 16 + sy);
            this.getSurface().drawColoredStringCentered(width / 2 + sx, "Controlled (+1 " + (isAndroid() ? "all" : "of each") + ")", 0, 0, 3, sy + 36);
            this.getSurface().drawColoredStringCentered(width / 2 + sx, "Aggressive (+3 " + (isAndroid() ? "str" : "strength") + ")", 0, 0, 3, 56 + sy);
            this.getSurface().drawColoredStringCentered(width / 2 + sx, "Accurate   (+3 " + (isAndroid() ? "att" : "attack") + ")", 0, 0, 3, sy + 76);
            this.getSurface().drawColoredStringCentered(width / 2 + sx, "Defensive  (+3 " + (isAndroid() ? "def" : "defense") + ")", 0, 0, 3, sy + 96);
        } catch (RuntimeException var7) {
            throw GenUtil.makeThrowable(var7, "client.TB(" + "dummy" + ')');
        }
    }

    private void drawDialogDuel() {
        try {

            int clickedIndex = -1;
            if (this.mouseButtonClick != 0 && this.menuDuel_Visible) {
                clickedIndex = this.menuDuel.handleClick(this.mouseX, this.menuDuelX, this.menuDuelY, this.mouseY);
            }

            if (clickedIndex >= 0) {
                this.mouseButtonClick = 0;
                this.menuDuel_Visible = false;
                MenuItemAction act = this.menuDuel.getItemAction(clickedIndex);
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
                                this.duelStakeItem(-1, slot);
                            }
                        }

                        if (mouseX_Local > 8 && mouseY_Local > 30 && mouseX_Local < 205 && mouseY_Local < 129) {
                            int slot = (mouseX_Local - 9) / 49 + (mouseY_Local - 31) / 34 * 4;
                            if (slot >= 0 && slot < this.duelOfferItemCount) {
                                this.duelRemoveItem(slot, -1);
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
                            this.packetHandler.getClientStream().newPacket(8);
                            this.packetHandler.getClientStream().writeBuffer1.putByte(!this.duelSettingsRetreat ? 0 : 1);
                            this.packetHandler.getClientStream().writeBuffer1.putByte(this.duelSettingsMagic ? 1 : 0);
                            this.packetHandler.getClientStream().writeBuffer1.putByte(!this.duelSettingsPrayer ? 0 : 1);
                            this.packetHandler.getClientStream().writeBuffer1.putByte(!this.duelSettingsWeapons ? 0 : 1);
                            this.packetHandler.getClientStream().finishPacket();
                            this.duelOffsetOpponentAccepted = false;
                            this.duelOfferAccepted = false;
                        }

                        if (mouseX_Local >= 217 && mouseY_Local >= 238 && mouseX_Local <= 286 && mouseY_Local <= 259) {
                            this.duelOfferAccepted = true;
                            this.packetHandler.getClientStream().newPacket(176);
                            this.packetHandler.getClientStream().finishPacket();
                        }

                        if (mouseX_Local >= 394 && mouseY_Local >= 238 && mouseX_Local < 463 && mouseY_Local < 259) {
                            this.showDialogDuel = false;
                            this.packetHandler.getClientStream().newPacket(197);
                            this.packetHandler.getClientStream().finishPacket();
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
                    this.packetHandler.getClientStream().newPacket(197);
                    this.packetHandler.getClientStream().finishPacket();
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
                        0xFFFFFF, 1);
                this.getSurface().drawString("Your Stake", xr + 9, 27 + yr, 0xFFFFFF, 4);
                this.getSurface().drawString("Opponent\'s Stake", 9 + xr, 120 + yr, 0xFFFFFF, 4);
                this.getSurface().drawString("Duel Options", xr + 9, yr + 212, 0xFFFFFF, 4);
                this.getSurface().drawString("Your Inventory", xr + 216, yr + 27, 0xFFFFFF, 4);
                this.getSurface().drawString("No retreating", 1 + 8 + xr, 215 + yr + 16, 0xFFFF00, 3);
                this.getSurface().drawString("No magic", 1 + 8 + xr, 250 + yr, 0xFFFF00, 3);
                this.getSurface().drawString("No prayer", 8 + xr + 102, yr + 231, 0xFFFF00, 3);
                this.getSurface().drawString("No weapons", 102 + 8 + xr, 35 + yr + 215, 0xFFFF00, 3);

                this.getSurface().drawBoxBorder(xr + 93, 11, 215 + yr + 6, 11, 0xFFFF00);
                if (this.duelSettingsRetreat) {
                    this.getSurface().drawBox(xr + 95, 8 + 215 + yr, 7, 7, 0xFFFF00);
                }

                this.getSurface().drawBoxBorder(93 + xr, 11, 25 + yr + 215, 11, 0xFFFF00);
                if (this.duelSettingsMagic) {
                    this.getSurface().drawBox(xr + 95, 215 + yr + 27, 7, 7, 0xFFFF00);
                }

                this.getSurface().drawBoxBorder(191 + xr, 11, 6 + 215 + yr, 11, 0xFFFF00);
                if (this.duelSettingsPrayer) {
                    this.getSurface().drawBox(xr + 193, 8 + yr + 215, 7, 7, 0xFFFF00);
                }
                this.getSurface().drawBoxBorder(xr + 191, 11, yr + 215 + 25, 11, 0xFFFF00);
                if (this.duelSettingsWeapons) {
                    this.getSurface().drawBox(193 + xr, 215 + yr + 27, 7, 7, 0xFFFF00);
                }

                if (!this.duelOfferAccepted) {
                    this.getSurface().drawSprite(spriteSelect(EntityHandler.GUIparts.get(GUIPARTS.ACCEPTBUTTON.id())), 217 + xr, yr + 238);
                }

                this.getSurface().drawSprite(spriteSelect(EntityHandler.GUIparts.get(GUIPARTS.DECLINEBUTTON.id())), xr + 394, yr + 238);
                if (this.duelOffsetOpponentAccepted) {
                    this.getSurface().drawColoredStringCentered(xr + 341, "Other player", 0xFFFFFF, 0, 1, 246 + yr);
                    this.getSurface().drawColoredStringCentered(341 + xr, "has accepted", 0xFFFFFF, 0, 1, 256 + yr);
                }

                if (this.duelOfferAccepted) {
                    this.getSurface().drawColoredStringCentered(35 + 217 + xr, "Waiting for", 0xFFFFFF, 0, 1, yr + 246);
                    this.getSurface().drawColoredStringCentered(252 + xr, "other player", 0xFFFFFF, 0, 1, 256 + yr);
                }

                for (int itm = 0; this.inventoryItemCount > itm; ++itm) {
                    int xI = 217 + xr + (itm % 5) * 49;
                    int yI = yr + 31 + (itm / 5) * 34;
                    this.getSurface().drawSpriteClipping(
                            spriteSelect(EntityHandler.getItemDef(this.inventoryItemID[itm])), xI,
                            yI, 48, 32, EntityHandler.getItemDef(this.inventoryItemID[itm]).getPictureMask(), 0, false,
                            0, 1);

                    ItemDef def = EntityHandler.getItemDef(this.inventoryItemID[itm]);
                    if (def.getNotedFormOf() >= 0) {
                        ItemDef originalDef = EntityHandler.getItemDef(def.getNotedFormOf());
                        getSurface().drawSpriteClipping(spriteSelect(originalDef), xI + 7, yI + 4,
                                33, 23, originalDef.getPictureMask(), 0, false, 0, 1);
                    }
                    if (EntityHandler.getItemDef(this.inventoryItemID[itm]).isStackable()) {
                        this.getSurface().drawString("" + this.inventoryItemSize[itm], xI + 1,
                                10 + yI, 0xFFFF00, 1);
                    }
                }

                for (int itmOffer = 0; this.duelOfferItemCount > itmOffer; ++itmOffer) {
                    int xI = xr + 9 + itmOffer % 4 * 49;
                    int yI = yr + 31 + itmOffer / 4 * 34;
                    this.getSurface().drawSpriteClipping(
                            spriteSelect(EntityHandler.getItemDef(this.duelOfferItemID[itmOffer])),
                            xI, yI, 48, 32, EntityHandler.getItemDef(this.duelOfferItemID[itmOffer]).getPictureMask(),
                            0, false, 0, 1);

                    ItemDef def = EntityHandler.getItemDef(this.duelOfferItemID[itmOffer]);
                    if (def.getNotedFormOf() >= 0) {
                        ItemDef originalDef = EntityHandler.getItemDef(def.getNotedFormOf());
                        getSurface().drawSpriteClipping(spriteSelect(originalDef), xI + 7, yI + 4,
                                33, 23, originalDef.getPictureMask(), 0, false, 0, 1);
                    }

                    if (EntityHandler.getItemDef(this.duelOfferItemID[itmOffer]).isStackable()) {
                        this.getSurface().drawString("" + this.duelOfferItemSize[itmOffer], 1 + xI, 10 + yI, 0xFFFF00,
                                1);
                    }

                    if (xI < this.mouseX && this.mouseX < 48 + xI && yI < this.mouseY && 32 + yI > this.mouseY) {
                        this.getSurface().drawString(
                                EntityHandler.getItemDef(this.duelOfferItemID[itmOffer]).getName() + ": @whi@"
                                        + EntityHandler.getItemDef(this.duelOfferItemID[itmOffer]).getDescription(),
                                8 + xr, yr + 273, 0xFFFF00, 1);
                    }
                }

                for (int itmOffer = 0; itmOffer < this.duelOffsetOpponentItemCount; ++itmOffer) {
                    int xI = itmOffer % 4 * 49 + 9 + xr;
                    int yI = itmOffer / 4 * 34 + 124 + yr;
                    this.getSurface().drawSpriteClipping(
                            spriteSelect(EntityHandler.getItemDef(this.duelOpponentItemId[itmOffer])),
                            xI, yI, 48, 32,
                            EntityHandler.getItemDef(this.duelOpponentItemId[itmOffer]).getPictureMask(), 0, false, 0,
                            1);

                    ItemDef def = EntityHandler.getItemDef(this.duelOpponentItemId[itmOffer]);
                    if (def.getNotedFormOf() >= 0) {
                        ItemDef originalDef = EntityHandler.getItemDef(def.getNotedFormOf());
                        getSurface().drawSpriteClipping(spriteSelect(originalDef), xI + 7, yI + 4,
                                33, 23, originalDef.getPictureMask(), 0, false, 0, 1);
                    }

                    if (EntityHandler.getItemDef(this.duelOpponentItemId[itmOffer]).isStackable()) {
                        this.getSurface().drawString("" + this.duelOpponentItemCount[itmOffer], 1 + xI, 10 + yI,
                                0xFFFF00, 1);
                    }

                    if (this.mouseX > xI && 48 + xI > this.mouseX && yI < this.mouseY && this.mouseY < yI + 32) {
                        this.getSurface().drawString(
                                EntityHandler.getItemDef(this.duelOpponentItemId[itmOffer]).getName() + ": @whi@"
                                        + EntityHandler.getItemDef(this.duelOpponentItemId[itmOffer]).getDescription(),
                                xr + 8, 273 + yr, 0xFFFF00, 1);
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

    private void drawDialogDuelConfirm() {
        try {

            byte xr = 22;
            byte yr = 36;
            this.getSurface().drawBox(xr, yr, 468, 16, 192);
            int color = 10000536;
            this.getSurface().drawBoxAlpha(xr, yr + 16, 468, 246, color, 160);
            this.getSurface().drawColoredStringCentered(xr + 234,
                    "Please confirm your duel with @yel@" + this.duelOpponentName, 0xFFFFFF, 0, 1, yr + 12);
            this.getSurface().drawColoredStringCentered(xr + 117, "Your stake:", 0xFFFF00, 0, 1, yr + 30);

            for (int var5 = 0; this.duelItemsCount > var5; ++var5) {
                String var6 = EntityHandler.getItemDef(this.duelItems[var5]).getName();
                if (EntityHandler.getItemDef(this.duelItems[var5]).isStackable()) {
                    var6 = var6 + " x " + StringUtil.formatItemCount(this.duelItemCounts[var5]);
                }

                this.getSurface().drawColoredStringCentered(xr + 117, var6, 0xFFFFFF, 0, 1, 42 + yr + var5 * 12);
            }

            if (this.duelItemsCount == 0) {
                this.getSurface().drawColoredStringCentered(xr + 117, "Nothing!", 0xFFFFFF, 0, 1, 42 + yr);
            }

            this.getSurface().drawColoredStringCentered(351 + xr, "Your opponent\'s stake:", 0xFFFF00, 0, 1, 30 + yr);

            for (int var5 = 0; var5 < this.duelOpponentItemsCount; ++var5) {
                String var6 = EntityHandler.getItemDef(this.duelOpponentItems[var5]).getName();
                if (EntityHandler.getItemDef(this.duelOpponentItems[var5]).isStackable()) {
                    var6 = var6 + " x " + StringUtil.formatItemCount(this.duelOpponentItemCounts[var5]);
                }

                this.getSurface().drawColoredStringCentered(xr + 351, var6, 0xFFFFFF, 0, 1, var5 * 12 + 42 + yr);
            }

            if (this.duelOpponentItemsCount == 0) {
                this.getSurface().drawColoredStringCentered(351 + xr, "Nothing!", 0xFFFFFF, 0, 1, 42 + yr);
            }

            if (this.duelOptionRetreat == 0) {
                this.getSurface().drawColoredStringCentered(xr + 234, "You can retreat from this duel", '\uff00', 0, 1,
                        yr + 180);
            } else {
                this.getSurface().drawColoredStringCentered(234 + xr, "No retreat is possible!", 0xFF0000, 0, 1,
                        180 + yr);
            }

            if (this.duelOptionMagic == 0) {
                this.getSurface().drawColoredStringCentered(234 + xr, "Magic may be used", '\uff00', 0, 1, yr + 192);
            } else {
                this.getSurface().drawColoredStringCentered(xr + 234, "Magic cannot be used", 0xFF0000, 0, 1, 192 + yr);
            }

            if (this.duelOptionPrayer == 0) {
                this.getSurface().drawColoredStringCentered(xr + 234, "Prayer may be used", '\uff00', 0, 1, 204 + yr);
            } else {
                this.getSurface().drawColoredStringCentered(xr + 234, "Prayer cannot be used", 0xFF0000, 0, 1,
                        yr + 204);
            }

            if (this.duelOptionWeapons != 0) {
                this.getSurface().drawColoredStringCentered(xr + 234, "Weapons cannot be used", 0xFF0000, 0, 1,
                        216 + yr);
            } else {
                this.getSurface().drawColoredStringCentered(xr + 234, "Weapons may be used", '\uff00', 0, 1, yr + 216);
            }

            this.getSurface().drawColoredStringCentered(xr + 234, "If you are sure click \'Accept\' to begin the duel",
                    0xFFFFFF, 0, 1, yr + 230);
            if (!this.duelConfirmed) {
                this.getSurface().drawSprite(spriteSelect(EntityHandler.GUIparts.get(GUIPARTS.ACCEPTBUTTON.id())), 83 + xr, 238 + yr);
                this.getSurface().drawSprite(spriteSelect(EntityHandler.GUIparts.get(GUIPARTS.DECLINEBUTTON.id())), xr - 35 + 352, yr + 238);
            } else {
                this.getSurface().drawColoredStringCentered(xr + 234, "Waiting for other player...", 0xFFFF00, 0, 1,
                        yr + 250);
            }

            if (this.mouseButtonClick == 1) {
                if (xr > this.mouseX || this.mouseY < yr || xr + 468 < this.mouseX || this.mouseY > 262 + yr) {
                    this.showDialogDuelConfirm = false;
                    this.packetHandler.getClientStream().newPacket(230);
                    this.packetHandler.getClientStream().finishPacket();
                }

                if (118 + xr - 35 <= this.mouseX && this.mouseX <= xr + 118 + 70 && yr + 238 <= this.mouseY
                        && 238 + yr + 21 >= this.mouseY) {
                    this.duelConfirmed = true;
                    this.packetHandler.getClientStream().newPacket(77);
                    this.packetHandler.getClientStream().finishPacket();
                }

                if (352 + (xr - 35) <= this.mouseX && 353 + xr + 70 >= this.mouseX && yr + 238 <= this.mouseY
                        && 259 + yr >= this.mouseY) {
                    this.showDialogDuelConfirm = false;
                    this.packetHandler.getClientStream().newPacket(197);
                    this.packetHandler.getClientStream().finishPacket();
                }

                this.mouseButtonClick = 0;
            }
        } catch (RuntimeException var7) {
            throw GenUtil.makeThrowable(var7, "client.VC(" + "dummy" + ')');
        }
    }

    private void drawDialogLogout() {
        try {

            this.getSurface().drawBox((getGameWidth() - 260) / 2, (getGameHeight() - 60) / 2, 260, 60, 0);
            this.getSurface().drawBoxBorder((getGameWidth() - 260) / 2, 260, (getGameHeight() - 60) / 2, 60, 0xFFFFFF);
            this.getSurface().drawColoredStringCentered((getGameWidth() - 256) / 2 + 256 / 2, "Logging out...", 0xFFFFFF, 0, 5, (getGameHeight() - 60) / 2 + 36);
        } catch (RuntimeException var3) {
            throw GenUtil.makeThrowable(var3, "client.SD(" + "dummy" + ')');
        }
    }

    private void drawDialogOptionsMenu(int var1) {
        try {


            if (isAndroid()) {
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
                            this.packetHandler.getClientStream().newPacket(116);
                            this.packetHandler.getClientStream().writeBuffer1.putByte(i);
                            this.packetHandler.getClientStream().finishPacket();
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
                            (S_WANT_KEYBOARD_SHORTCUTS > 1 ? "(" + (j + 1) + ")" : "") + optionsMenuText[j],
                            startX + 10, startY + j * spread, k, 6);
                }
            } else {
                int var2;
                if (this.mouseButtonClick == 0) {
                    // Draw
                    var2 = 0;
                    while (this.optionsMenuCount > var2) {
                        int var3 = '\uffff';
                        if (this.mouseX < this.getSurface().stringWidth(1, this.optionsMenuText[var2]) + 9
                                && this.mouseY > 2 + var2 * 12 && this.mouseY < 2 + var2 * 12 + 12) {
                            var3 = 0xFF0000;
                        }

                        this.getSurface().drawString(
                                (S_WANT_KEYBOARD_SHORTCUTS > 1 ? "(" + (var2 + 1) + ") " : "") + this.optionsMenuText[var2],
                                6, var2 * 12 + 12, var3, 1);
                        ++var2;
                    }
                } else {
                    // Click
                    for (var2 = 0; var2 < this.optionsMenuCount; ++var2) {
                        if (this.getSurface().stringWidth(1, this.optionsMenuText[var2]) + 9 > this.mouseX
                                && 2 + var2 * 12 < this.mouseY && 2 + 12 + var2 * 12 > this.mouseY) {
                            this.packetHandler.getClientStream().newPacket(116);
                            this.packetHandler.getClientStream().writeBuffer1.putByte(var2);
                            this.packetHandler.getClientStream().finishPacket();
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

    private void drawDialogServerMessage(byte var1) {
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
            this.getSurface().drawBoxBorder(xr, var2, yr, var3, 0xFFFFFF);
            this.getSurface().drawWrappedCenteredString(this.serverMessage, xr + 256 - 56, yr + 17, var2 - 40, 1,
                    0xFFFFFF, true);
            int var4 = (getGameHeight() + (var3) - 23) / 2;

            int var5 = 0xFFFFFF;
            if (var4 - 12 < this.mouseY && var4 >= this.mouseY && this.mouseX > xr + 50 && this.mouseX < xr + 350) {
                var5 = 0xFF0000;

            }

            this.getSurface().drawColoredStringCentered(xr + 256 - 56, "Click here to close window", var5, 0, 1, var4);

            if (this.mouseButtonClick == 1) {
                if (var5 == 0xFF0000) {
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

    private void drawDialogShop() {
        try {

            if (this.mouseButtonClick != 0 && this.inputX_Action == InputXAction.ACT_0) {
                this.mouseButtonClick = 0;
                int mlx = this.mouseX - (getGameWidth() - 408) / 2;
                int mly = this.mouseY - (getGameHeight() - 246) / 2;
                if (mlx < 0 || mly < 12 || mlx >= 408 || mly >= 246) {
                    this.packetHandler.getClientStream().newPacket(166);
                    this.packetHandler.getClientStream().finishPacket();
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
                                this.packetHandler.getClientStream().newPacket(236);
                                this.packetHandler.getClientStream().writeBuffer1
                                        .putShort(this.shopItemID[this.shopSelectedItemIndex]);
                                this.packetHandler.getClientStream().writeBuffer1.putShort(count);
                                this.packetHandler.getClientStream().writeBuffer1.putShort(btnCount);
                                this.packetHandler.getClientStream().finishPacket();
                            }
                        }

                        int invCount = this.getInventoryCount(id);
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
                                this.packetHandler.getClientStream().newPacket(221);
                                this.packetHandler.getClientStream().writeBuffer1
                                        .putShort(this.shopItemID[this.shopSelectedItemIndex]);
                                this.packetHandler.getClientStream().writeBuffer1.putShort(count);
                                this.packetHandler.getClientStream().writeBuffer1.putShort(btnCount);
                                this.packetHandler.getClientStream().finishPacket();
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
            this.getSurface().drawString("Buying and selling items", xr + 1, yr + 10, 0xFFFFFF, 1);

            int color2 = 0xFFFFFF;
            if (this.mouseX > 320 + xr && yr <= this.mouseY && this.mouseX < xr + 408 && this.mouseY < yr + 12) {
                color2 = 0xFF0000;
            }
            this.getSurface().b(xr + 406, "Close window", yr + 10, color2, -92, 1);

            this.getSurface().drawString("Shops stock in green", 2 + xr, 24 + yr, '\uff00', 1);
            this.getSurface().drawString("Number you own in blue", xr + 135, yr + 24, '\uffff', 1);
            this.getSurface().drawString("Your money: " + this.getInventoryCount(10) + "gp",
                    280 + xr, 24 + yr, 0xFFFF00, 1);
            {
                int slot = 0;
                for (int row = 0; row < 5; ++row) {
                    for (int column = 0; column < 8; ++column) {
                        int sx = column * 49 + 7 + xr;
                        int sy = yr + 28 + row * 34;
                        if (this.shopSelectedItemIndex == slot) {
                            this.getSurface().drawBoxAlpha(sx, sy, 49, 34, 0xFF0000, 160);
                        } else {
                            this.getSurface().drawBoxAlpha(sx, sy, 49, 34, 13684944, 160);
                        }

                        this.getSurface().drawBoxBorder(sx, 50, sy, 35, 0);
                        if (this.shopItemID[slot] != -1) {
                            this.getSurface().drawSpriteClipping(
                                    spriteSelect(EntityHandler.getItemDef(this.shopItemID[slot])),
                                    sx, sy, 48, 32, EntityHandler.getItemDef(this.shopItemID[slot]).getPictureMask(), 0,
                                    false, 0, 1);

                            ItemDef def = EntityHandler.getItemDef(this.shopItemID[slot]);
                            if (def.getNotedFormOf() >= 0) {
                                ItemDef originalDef = EntityHandler.getItemDef(def.getNotedFormOf());
                                getSurface().drawSpriteClipping(spriteSelect(originalDef), sx + 7,
                                        sy + 4, 33, 23, originalDef.getPictureMask(), 0, false, 0, 1);
                            }

                            this.getSurface().drawString("" + this.shopItemCount[slot], 1 + sx, 10 + sy, '\uff00', 1);
                            this.getSurface().b(47 + sx, "" + this.getInventoryCount(this.shopItemID[slot]),
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
                                "This item is not currently available to buy", 0xFFFF00, 0, 3, 214 + yr);
                    } else {
                        int cost = GenUtil.computeItemCost(EntityHandler.getItemDef(id).getBasePrice(),
                                this.shopItemPrice[this.shopSelectedItemIndex], this.shopBuyPriceMod, -30910, true, 1,
                                count, this.shopPriceMultiplier);
                        this.getSurface().drawString(
                                EntityHandler.getItemDef(id).getName() + ": buy for " + cost + "gp each", 2 + xr,
                                yr + 214, 0xFFFF00, 1);
                        boolean mouseInRow = 204 + yr <= this.mouseY && yr + 215 >= this.mouseY;
                        this.getSurface().drawString("Buy:", xr + 285, 214 + yr, 0xFFFFFF, 3);

                        color2 = 0xFFFFFF;
                        if (mouseInRow && this.mouseX > 318 + xr && this.mouseX < xr + 330) {
                            color2 = 0xFF0000;
                        }
                        this.getSurface().drawString("1", xr + 320, 214 + yr, color2, 3);

                        if (count >= 5) {
                            color2 = 0xFFFFFF;
                            if (mouseInRow && this.mouseX > 333 + xr && this.mouseX < 345 + xr) {
                                color2 = 0xFF0000;
                            }
                            this.getSurface().drawString("5", 335 + xr, 214 + yr, color2, 3);
                        }

                        if (count >= 10) {
                            color2 = 0xFFFFFF;
                            if (mouseInRow && 348 + xr < this.mouseX && this.mouseX < xr + 365) {
                                color2 = 0xFF0000;
                            }
                            this.getSurface().drawString("10", 350 + xr, 214 + yr, color2, 3);
                        }

                        if (count >= 50) {
                            color2 = 0xFFFFFF;
                            if (mouseInRow && this.mouseX > 368 + xr && 385 + xr > this.mouseX) {
                                color2 = 0xFF0000;
                            }
                            this.getSurface().drawString("50", xr + 370, 214 + yr, color2, 3);
                        }

                        color2 = 0xFFFFFF;
                        if (mouseInRow && this.mouseX > xr + 388 && this.mouseX < 400 + xr) {
                            color2 = 0xFF0000;
                        }
                        this.getSurface().drawString("X", 390 + xr, 214 + yr, color2, 3);
                    }

                    int invCount = this.getInventoryCount(id);
                    if (invCount <= 0) {
                        this.getSurface().drawColoredStringCentered(xr + 204,
                                "You do not have any of this item to sell", 0xFFFF00, 0, 3, 239 + yr);
                    } else {

                        int sellCost = GenUtil.computeItemCost(EntityHandler.getItemDef(id).getBasePrice(),
                                this.shopItemPrice[this.shopSelectedItemIndex], this.shopSellPriceMod, -30910, false, 1,
                                count, this.shopPriceMultiplier);
                        this.getSurface().drawString(
                                EntityHandler.getItemDef(id).getName() + ": sell for " + sellCost + "gp each", 2 + xr,
                                yr + 239, 0xFFFF00, 1);
                        boolean mouseInRow = this.mouseY >= yr + 229 && yr + 240 >= this.mouseY;

                        color2 = 0xFFFFFF;
                        this.getSurface().drawString("Sell:", xr + 285, yr + 239, 0xFFFFFF, 3);
                        if (mouseInRow && xr + 318 < this.mouseX && this.mouseX < xr + 330) {
                            color2 = 0xFF0000;
                        }
                        this.getSurface().drawString("1", xr + 320, 239 + yr, color2, 3);

                        if (invCount >= 5) {
                            color2 = 0xFFFFFF;
                            if (mouseInRow && xr + 333 < this.mouseX && this.mouseX < xr + 345) {
                                color2 = 0xFF0000;
                            }
                            this.getSurface().drawString("5", 335 + xr, 239 + yr, color2, 3);
                        }

                        if (invCount >= 10) {
                            color2 = 0xFFFFFF;
                            if (mouseInRow && 348 + xr < this.mouseX && 365 + xr > this.mouseX) {
                                color2 = 0xFF0000;
                            }
                            this.getSurface().drawString("10", xr + 350, 239 + yr, color2, 3);
                        }

                        if (invCount >= 50) {
                            color2 = 0xFFFFFF;
                            if (mouseInRow && this.mouseX > xr + 368 && 385 + xr > this.mouseX) {
                                color2 = 0xFF0000;
                            }
                            this.getSurface().drawString("50", xr + 370, 239 + yr, color2, 3);
                        }

                        color2 = 0xFFFFFF;
                        if (mouseInRow && this.mouseX > 388 + xr && xr + 400 > this.mouseX) {
                            color2 = 0xFF0000;
                        }
                        this.getSurface().drawString("X", xr + 390, yr + 239, color2, 3);
                    }
                }
            } else {
                this.getSurface().drawColoredStringCentered(204 + xr, "Select an object to buy or sell", 0xFFFF00, 0, 3,
                        214 + yr);
            }

        } catch (RuntimeException var14) {
            throw GenUtil.makeThrowable(var14, "client.HA(" + "dummy" + ')');
        }
    }

    private void drawDialogTrade() {
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
                                    this.tradeOffer(-1, slot);
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
                                this.packetHandler.getClientStream().newPacket(55);
                                this.packetHandler.getClientStream().finishPacket();
                            }

                            if (mouseLX >= 394 && mouseLY >= 238 && mouseLX < 463 && mouseLY < 259) {
                                this.showDialogTrade = false;
                                this.packetHandler.getClientStream().newPacket(230);
                                this.packetHandler.getClientStream().finishPacket();
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
                        this.packetHandler.getClientStream().newPacket(230);
                        this.packetHandler.getClientStream().finishPacket();
                    }
                }
            } else {
                this.menuTrade_Visible = false;
                this.mouseButtonClick = 0;
                MenuItemAction act = this.menuTrade.getItemAction(clickOp);
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
                    int modCount = this.menuTrade.getItemIdOrZ(clickOp);
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

                this.getSurface().drawString("Trading with: " + this.tradeRecipientName, xr + 1, 10 + yr, 0xFFFFFF, 1);
                this.getSurface().drawString("Your Offer", xr + 9, yr + 27, 0xFFFFFF, 4);
                this.getSurface().drawString("Opponent\'s Offer", xr + 9, yr + 152, 0xFFFFFF, 4);
                this.getSurface().drawString("Your Inventory", xr + 216, yr + 27, 0xFFFFFF, 4);
                if (!this.tradeAccepted) {
                    this.getSurface().drawSprite(spriteSelect(EntityHandler.GUIparts.get(GUIPARTS.ACCEPTBUTTON.id())), xr + 217, yr + 238);
                }

                this.getSurface().drawSprite(spriteSelect(EntityHandler.GUIparts.get(GUIPARTS.DECLINEBUTTON.id())), xr + 394, yr + 238);
                if (this.tradeRecipientAccepted) {
                    this.getSurface().drawColoredStringCentered(xr + 341, "Other player", 0xFFFFFF, 0, 1, 246 + yr);
                    this.getSurface().drawColoredStringCentered(xr + 341, "has accepted", 0xFFFFFF, 0, 1, 256 + yr);
                }

                if (this.tradeAccepted) {
                    this.getSurface().drawColoredStringCentered(xr + 217 + 35, "Waiting for", 0xFFFFFF, 0, 1, yr + 246);
                    this.getSurface().drawColoredStringCentered(xr + 252, "other player", 0xFFFFFF, 0, 1, 256 + yr);
                }

                for (int slot = 0; slot < this.inventoryItemCount; ++slot) {
                    int sX = xr + 217 + slot % 5 * 49;
                    int sY = 31 + yr + slot / 5 * 34;
                    this.getSurface().drawSpriteClipping(
                            spriteSelect(EntityHandler.getItemDef(this.inventoryItemID[slot])), sX,
                            sY, 48, 32, EntityHandler.getItemDef(this.inventoryItemID[slot]).getPictureMask(), 0, false,
                            0, 1);

                    ItemDef def = EntityHandler.getItemDef(this.inventoryItemID[slot]);
                    if (def.getNotedFormOf() >= 0) {
                        ItemDef originalDef = EntityHandler.getItemDef(def.getNotedFormOf());
                        getSurface().drawSpriteClipping(spriteSelect(originalDef), sX + 7, sY + 4,
                                33, 23, originalDef.getPictureMask(), 0, false, 0, 1);
                    }

                    if (EntityHandler.getItemDef(this.inventoryItemID[slot]).isStackable()) {
                        this.getSurface().drawString("" + this.inventoryItemSize[slot], 1 + sX,
                                10 + sY, 0xFFFF00, 1);
                    }
                }

                for (int slot = 0; this.tradeItemCount > slot; ++slot) {
                    int sx = slot % 4 * 49 + 9 + xr;
                    int sy = slot / 4 * 34 + yr + 31;
                    this.getSurface().drawSpriteClipping(
                            spriteSelect(EntityHandler.getItemDef(this.tradeItemID[slot])), sx, sy,
                            48, 32, EntityHandler.getItemDef(this.tradeItemID[slot]).getPictureMask(), 0, false, 0, 1);
                    if (EntityHandler.getItemDef(this.tradeItemID[slot]).isStackable()) {
                        this.getSurface().drawString("" + this.tradeItemSize[slot], sx + 1, 10 + sy,
                                0xFFFF00, 1);
                    }

                    ItemDef def = EntityHandler.getItemDef(this.tradeItemID[slot]);
                    if (def.getNotedFormOf() >= 0) {
                        ItemDef originalDef = EntityHandler.getItemDef(def.getNotedFormOf());
                        getSurface().drawSpriteClipping(spriteSelect(originalDef), sx + 7, sy + 4,
                                33, 23, originalDef.getPictureMask(), 0, false, 0, 1);
                    }

                    if (sx < this.mouseX && 48 + sx > this.mouseX && sy < this.mouseY && this.mouseY < sy + 32) {
                        this.getSurface().drawString(
                                EntityHandler.getItemDef(this.tradeItemID[slot]).getName() + ": @whi@"
                                        + EntityHandler.getItemDef(this.tradeItemID[slot]).getDescription(),
                                8 + xr, 273 + yr, 0xFFFF00, 1);
                    }
                }

                for (int slot = 0; this.tradeRecipientItemsCount > slot; ++slot) {
                    int sx = xr + 9 + slot % 4 * 49;
                    int sy = yr + 156 + slot / 4 * 34;
                    this.getSurface().drawSpriteClipping(
                            spriteSelect(EntityHandler.getItemDef(this.tradeRecipientItem[slot])),
                            sx, sy, 48, 32, EntityHandler.getItemDef(this.tradeRecipientItem[slot]).getPictureMask(), 0,
                            false, 0, 1);

                    ItemDef def = EntityHandler.getItemDef(this.tradeRecipientItem[slot]);
                    if (def.getNotedFormOf() >= 0) {
                        ItemDef originalDef = EntityHandler.getItemDef(def.getNotedFormOf());
                        getSurface().drawSpriteClipping(spriteSelect(originalDef), sx + 7, sy + 4,
                                33, 23, originalDef.getPictureMask(), 0, false, 0, 1);
                    }

                    if (EntityHandler.getItemDef(this.tradeRecipientItem[slot]).isStackable()) {
                        this.getSurface().drawString("" + this.tradeRecipientItemCount[slot], sx + 1, 10 + sy, 0xFFFF00,
                                1);
                    }

                    if (sx < this.mouseX && this.mouseX < sx + 48 && this.mouseY > sy && sy + 32 > this.mouseY) {
                        this.getSurface().drawString(
                                EntityHandler.getItemDef(this.tradeRecipientItem[slot]).getName() + ": @whi@"
                                        + EntityHandler.getItemDef(this.tradeRecipientItem[slot]).getDescription(),
                                xr + 8, yr + 273, 0xFFFF00, 1);
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

    private void drawDialogWelcome(int var1) {
        try {

            //int var2 = 65;
            int var2 = 135;
				/*if (this.welcomeUnreadMessages > 0) {
					var2 += 30;
				}*/

            if (!this.welcomeLastLoggedInIp.equalsIgnoreCase("0.0.0.0")) {
                var2 += 45;
            }
            int welcomeWindowX = (getGameWidth() - 400) / 2;
            int welcomeWindowY = (getGameHeight() - var2) / 2;

            this.getSurface().drawBox(welcomeWindowX, welcomeWindowY, 400, var2, 0);
            int var3 = welcomeWindowY;
            this.getSurface().drawBoxBorder(welcomeWindowX, 400, welcomeWindowY, var2, 0xFFFFFF);
            var3 += 20;
            this.getSurface().drawColoredStringCentered(welcomeWindowX + 256 - 56, "Welcome to " + getServerName() + " " + this.localPlayer.accountName,
                    0xFFFF00, 0, 4, var3);
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
                this.getSurface().drawColoredStringCentered(welcomeWindowX + 256 - 56, "You last logged in " + var4, 0xFFFFFF, 0, 1, var3);
                var3 += 15;
                if (this.welcomeLastLoggedInHost == null) {
                    this.welcomeLastLoggedInHost = getHostnameFromIP();
                }

                if (this.settingsHideIP != null && this.settingsHideIP != 1) {
                    this.getSurface().drawColoredStringCentered(welcomeWindowX + 256 - 56, "from: " + this.welcomeLastLoggedInHost, 0xFFFFFF,
                            var1 ^ -4853, 1, var3);
                }
                var3 += 15;
                var3 += 15;
            }

            int var5 = 0xFFFFFF;
            if (this.welcomeRecoverySetDays > 0) {
                if (this.welcomeRecoverySetDays == 14) {
                    var4 = "Earlier today";
                } else if (this.welcomeRecoverySetDays == 13) {
                    var4 = "Yesterday";
                } else {
                    var4 = 14 - this.welcomeRecoverySetDays + " days ago";
                }

                this.getSurface().drawColoredStringCentered(welcomeWindowX + 256 - 56, var4 + " you requested new recovery questions", 0xFF8000, 0, 1, var3);
                var3 += 15;
                this.getSurface().drawColoredStringCentered(welcomeWindowX + 256 - 56, "If you do not remember making this request then", 0xFF8000, 0, 1, var3);
                var3 += 15;
                this.getSurface().drawColoredStringCentered(welcomeWindowX + 256 - 56, "cancel it and change your password immediately!", 0xFF8000, 0, 1, var3);
                var3 += 15;
                var3 += 15;
                var5 = 0xFFFFFF;
                if (this.mouseY > var3 - 12 && this.mouseY <= var3 && this.mouseX > 106
                        && this.mouseX < 406) {
                    var5 = 0xFF0000;
                }

                this.getSurface().drawColoredStringCentered(welcomeWindowX + 256 - 56, "No that wasn't me - Cancel the request!", var5, 0, 1, var3);
                if (var5 == 0xFF0000 && this.mouseButtonClick == 1) {
                    this.packetHandler.getClientStream().newPacket(196);
                    this.packetHandler.getClientStream().finishPacket();
                    this.showDialogMessage = false;
                }

                var3 += 15;
                var5 = 0xFFFFFF;
                if (this.mouseY > var3 - 12 && this.mouseY <= var3 && this.mouseX > 106
                        && this.mouseX < 406) {
                    var5 = 0xFF0000;
                }

                this.getSurface().drawColoredStringCentered(welcomeWindowX + 256 - 56, "That's ok, activate the new questions in " + this.welcomeRecoverySetDays + " days time", var5, 0, 1, var3);
                if (var5 == 0xFF0000 && this.mouseButtonClick == 1) {
                    this.showDialogMessage = false;
                }
            } else {
                var3 += 7;
                this.getSurface().drawColoredStringCentered(welcomeWindowX + 256 - 56, "Security tip of the day", 0xFF0000, 0, 1, var3);
                var3 += 15;
                if (this.welcomeTipOfDay == 0) {
                    this.getSurface().drawColoredStringCentered(welcomeWindowX + 256 - 56, "Don't tell ANYONE your password or recovery questions!", 0xFFFFFF, 0, 1, var3);
                    var3 += 15;
                    this.getSurface().drawColoredStringCentered(welcomeWindowX + 256 - 56, "Not even people claiming to be Jagex staff.", 0xFFFFFF, 0, 1, var3);
                    var3 += 15;
                }

                if (this.welcomeTipOfDay == 1) {
                    this.getSurface().drawColoredStringCentered(welcomeWindowX + 256 - 56, "Never enter your password or recovery questions into ANY", 0xFFFFFF, 0, 1, var3);
                    var3 += 15;
                    this.getSurface().drawColoredStringCentered(welcomeWindowX + 256 - 56, "website other than this one - Not even if it looks similar.", 0xFFFFFF, 0, 1, var3);
                    var3 += 15;
                }

                if (this.welcomeTipOfDay == 2) {
                    this.getSurface().drawColoredStringCentered(welcomeWindowX + 256 - 56, "Don't use RuneScape cheats, helpers, or automaters.", 0xFFFFFF, 0, 1, var3);
                    var3 += 15;
                    this.getSurface().drawColoredStringCentered(welcomeWindowX + 256 - 56, "These programs WILL steal your password.", 0xFFFFFF, 0, 1, var3);
                    var3 += 15;
                }

                if (this.welcomeTipOfDay == 3) {
                    this.getSurface().drawColoredStringCentered(welcomeWindowX + 256 - 56, "Watch out for fake emails, and fake staff. Real staff", 0xFFFFFF, 0, 1, var3);
                    var3 += 15;
                    this.getSurface().drawColoredStringCentered(welcomeWindowX + 256 - 56, "will NEVER ask you for your password or recovery questions!", 0xFFFFFF, 0, 1, var3);
                    var3 += 15;
                }

                if (this.welcomeTipOfDay == 4) {
                    this.getSurface().drawColoredStringCentered(welcomeWindowX + 256 - 56, "Use a password your friends won't guess. Do NOT use your name!", 0xFFFFFF, 0, 1, var3);
                    var3 += 15;
                    this.getSurface().drawColoredStringCentered(welcomeWindowX + 256 - 56, "Choose a unique password which you haven't used anywhere else", 0xFFFFFF, 0, 1, var3);
                    var3 += 15;
                }

                if (this.welcomeTipOfDay == 5) {
                    this.getSurface().drawColoredStringCentered(welcomeWindowX + 256 - 56, "If possible only play runescape from your own computer", 0xFFFFFF, 0, 1, var3);
                    var3 += 15;
                    this.getSurface().drawColoredStringCentered(welcomeWindowX + 256 - 56, "Other machines could have been tampered with to steal your pass", 0xFFFFFF, 0, 1, var3);
                    var3 += 15;
                }

                var3 += 22;

                if (isAndroid()) {

                    this.getSurface().drawBoxAlpha(150, var3 - 20, (207), var3 - (var3 - 12) + 20, 3158064, 160);
                    this.getSurface().drawBoxBorder(150, (207), var3 - 20, var3 - (var3 - 12) + 20, 4210752);
                    if (this.mouseY >= var3 - 20 && this.mouseY <= (var3 - 20) + (var3 - (var3 - 12) + 20) && this.mouseX >= 150 && this.mouseX < 150 + 207) {
                        var5 = 0xFF0000;
                    }

                } else {

                    if (this.mouseY > var3 - 12 && this.mouseY <= var3 && this.mouseX > welcomeWindowX + 106 - 56 && this.mouseX < welcomeWindowX + 406 - 56) {
                        var5 = 0xFF0000;
                    }
                }

                this.getSurface().drawColoredStringCentered(welcomeWindowX + 256 - 56, "Click here to close window", var5, var1 ^ var1, 1, var3);
                if (this.mouseButtonClick == 1) {
                    if (var5 == 0xFF0000) {
                        this.showDialogMessage = false;
                    }

                    // allows closing the window by clicking anywhere in the upper half of the screen outside of the window
                    if ((this.mouseX < welcomeWindowX + 86 - 56 || this.mouseX > welcomeWindowX + 426 - 56)
                            && (this.mouseY > 167 - welcomeWindowY || welcomeWindowY + 167 > this.mouseY)) {
                        this.showDialogMessage = false;
                    }

                    // allows closing the window by clicking anywhere in the lower half of the screen outside of the window
                    if ((this.mouseX < welcomeWindowX + 86 - 56 || this.mouseX > welcomeWindowX + 426 - 56)
                            && (this.mouseY < 167 - welcomeWindowY || welcomeWindowY + 167 < this.mouseY)) {
                        this.showDialogMessage = false;
                    }
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

    private void drawDialogWildWarn(int var1) {
        try {

            this.getSurface().drawBox(halfGameWidth() - 170, halfGameHeight() - 90, 340, 180, 0);
            if (var1 <= 90) {
                this.loadGameConfig(true);
            }
            this.getSurface().drawBoxBorder(halfGameWidth() - 170, 340, halfGameHeight() - 90, 180, 0xFFFFFF);
            this.getSurface().drawColoredStringCentered(halfGameWidth(), "Warning! Proceed with caution", 0xFF0000, 0, 4, halfGameHeight() - 70);
            this.getSurface().drawColoredStringCentered(halfGameWidth(), "If you go much further north you will enter the", 0xFFFFFF, 0, 1, halfGameHeight() - 44);
            this.getSurface().drawColoredStringCentered(halfGameWidth(), "wilderness. This a very dangerous area where", 0xFFFFFF, 0, 1, halfGameHeight() - 31);
            this.getSurface().drawColoredStringCentered(halfGameWidth(), "other players can attack you!", 0xFFFFFF, 0, 1, halfGameHeight() - 18);
            this.getSurface().drawColoredStringCentered(halfGameWidth(), "The further north you go the more dangerous it", 0xFFFFFF, 0, 1, halfGameHeight() + 4);
            this.getSurface().drawColoredStringCentered(halfGameWidth(), "becomes, but the more treasure you will find.", 0xFFFFFF, 0, 1, halfGameHeight() + 17);
            this.getSurface().drawColoredStringCentered(halfGameWidth(), "In the wilderness an indicator at the bottom-right", 0xFFFFFF, 0, 1, halfGameHeight() + 39);
            this.getSurface().drawColoredStringCentered(halfGameWidth(), "of the screen will show the current level of danger", 0xFFFFFF, 0, 1, halfGameHeight() + 52);
            int var3 = 0xFFFFFF;
            if (this.mouseY > halfGameHeight() + 62 && this.mouseY <= halfGameHeight() + 74 && this.mouseX > halfGameWidth() - 75 && this.mouseX < halfGameWidth() + 75) {
                var3 = 0xFF0000;
            }
            this.getSurface().drawColoredStringCentered(halfGameWidth(), "Click here to close window", var3, 0, 1, halfGameHeight() + 74);
            if (this.mouseButtonClick != 0) {
                if (halfGameHeight() + 62 < this.mouseY && halfGameHeight() + 74 >= this.mouseY && this.mouseX > halfGameWidth() - 75 && this.mouseX < halfGameWidth() + 75) {
                    this.showUiWildWarn = 2;
                }
                this.mouseButtonClick = 0;
                if (this.mouseX < halfGameWidth() - 170 || this.mouseX > halfGameWidth() + 170 || this.mouseY < halfGameHeight() - 90 || this.mouseY > halfGameHeight() + 90) {
                    this.showUiWildWarn = 2;
                }
            }

            this.mouseButtonClick = 0;
            if (this.mouseX < halfGameWidth() - 170 || this.mouseX > halfGameWidth() + 170 || this.mouseY < halfGameHeight() - 90 || this.mouseY > halfGameHeight() + 90) {
                this.showUiWildWarn = 2;
            }

        } catch (RuntimeException var4) {
            throw GenUtil.makeThrowable(var4, "client.FA(" + var1 + ')');
        }
    }

    private void drawDialogueChangePassword() {
        if (this.mouseButtonClick != 0) {
            this.mouseButtonClick = 0;
            if (this.mouseX < 106 || this.mouseY < 150 || this.mouseX > 406 || this.mouseY > 210) {
                this.panelPasswordChange_Mode = PasswordChangeMode.NONE;
                return;
            }
        }

        int y = (getGameHeight() - 60) / 2;
        this.getSurface().drawBox((getGameWidth() - 300) / 2, (getGameHeight() - 60) / 2, 300, 60, 0);
        this.getSurface().drawBoxBorder((getGameWidth() - 300) / 2, 300, (getGameHeight() - 60) / 2, 60, 0xFFFFFF);
        y += 22;
        String var2;
        int var3;
        if (this.panelPasswordChange_Mode == PasswordChangeMode.OLD_PASSWORD) {
            this.getSurface().drawColoredStringCentered(halfGameWidth(), "Please enter your current password", 0xFFFFFF, 0, 4, y);
            y += 25;
            var2 = "*";

            for (var3 = 0; var3 < this.inputTextCurrent.length(); ++var3) {
                var2 = "X" + var2;
            }

            this.getSurface().drawColoredStringCentered(halfGameWidth(), var2, 0xFFFFFF, 0, 4, y);
            if (this.inputTextFinal.length() > 0) {
                this.oldPassword = this.inputTextFinal;
                this.inputTextCurrent = "";
                this.inputTextFinal = "";
                this.panelPasswordChange_Mode = PasswordChangeMode.NEW_PASSWORD;
                return;
            }
        } else if (this.panelPasswordChange_Mode == PasswordChangeMode.NEW_PASSWORD) {
            this.getSurface().drawColoredStringCentered(halfGameWidth(), "Please enter your new password", 0xFFFFFF, 0, 4, y);
            y += 25;
            var2 = "*";

            for (var3 = 0; var3 < this.inputTextCurrent.length(); ++var3) {
                var2 = "X" + var2;
            }

            this.getSurface().drawColoredStringCentered(halfGameWidth(), var2, 0xFFFFFF, 0, 4, y);
            if (this.inputTextFinal.length() > 0) {
                this.newPassword = this.inputTextFinal;
                this.inputTextCurrent = "";
                this.inputTextFinal = "";
                this.panelPasswordChange_Mode = PasswordChangeMode.CONFIRM_PASSWORD;
                if (this.newPassword.length() < 5) {
                    this.panelPasswordChange_Mode = PasswordChangeMode.NEED_LONGER_PASSWORD;
                    return;
                }
                if (this.newPassword.trim().equalsIgnoreCase(this.username.trim())) {
                    this.panelPasswordChange_Mode = PasswordChangeMode.PASSWORD_NOT_EQ_USER;
                    return;
                }
                return;
            }
        } else if (this.panelPasswordChange_Mode == PasswordChangeMode.CONFIRM_PASSWORD) {
            this.getSurface().drawColoredStringCentered(halfGameWidth(), "Enter password again to confirm", 0xFFFFFF, 0, 4, y);
            y += 25;
            var2 = "*";

            for (var3 = 0; var3 < this.inputTextCurrent.length(); ++var3) {
                var2 = "X" + var2;
            }

            this.getSurface().drawColoredStringCentered(halfGameWidth(), var2, 0xFFFFFF, 0, 4, y);
            if (this.inputTextFinal.length() > 0) {
                if (this.inputTextFinal.equalsIgnoreCase(this.newPassword)) {
                    this.panelPasswordChange_Mode = PasswordChangeMode.PASSWORD_REQ_SENT;
                    this.sendPassChange(this.oldPassword, this.newPassword);
                    return;
                }

                this.panelPasswordChange_Mode = PasswordChangeMode.PASSWORD_MISMATCH;
                return;
            }
        } else {
            if (this.panelPasswordChange_Mode == PasswordChangeMode.PASSWORD_MISMATCH) {
                this.getSurface().drawColoredStringCentered(halfGameWidth(), "Passwords do not match!", 0xFFFFFF, 0, 4, y);
                y += 25;
                this.getSurface().drawColoredStringCentered(halfGameWidth(), "Press any key to close", 0xFFFFFF, 0, 4, y);
                return;
            }

            if (this.panelPasswordChange_Mode == PasswordChangeMode.PASSWORD_REQ_SENT) {
                this.getSurface().drawColoredStringCentered(halfGameWidth(), "Ok, your request has been sent", 0xFFFFFF, 0, 4, y);
                y += 25;
                this.getSurface().drawColoredStringCentered(halfGameWidth(), "Press any key to close", 0xFFFFFF, 0, 4, y);
                return;
            }

            if (this.panelPasswordChange_Mode == PasswordChangeMode.NEED_LONGER_PASSWORD) {
                this.getSurface().drawColoredStringCentered(halfGameWidth(), "Password must be at", 0xFFFFFF, 0, 4, y);
                y += 25;
                this.getSurface().drawColoredStringCentered(halfGameWidth(), "least 5 letters long", 0xFFFFFF, 0, 4, y);
            }

            if (this.panelPasswordChange_Mode == PasswordChangeMode.PASSWORD_NOT_EQ_USER) {
                this.getSurface().drawColoredStringCentered(halfGameWidth(), "Your password must not be", 0xFFFFFF, 0, 4, y);
                y += 25;
                this.getSurface().drawColoredStringCentered(halfGameWidth(), "the same as your username", 0xFFFFFF, 0, 4, y);
            }
        }

    }

    private void sendPassChange(String oldPass, String newPass) {
        this.packetHandler.getClientStream().newPacket(25);
        oldPass = DataOperations.addCharacters(oldPass, 20);
        newPass = DataOperations.addCharacters(newPass, 20);
        this.packetHandler.getClientStream().writeBuffer1.putString(oldPass);
        this.packetHandler.getClientStream().writeBuffer1.putString(newPass);
        this.packetHandler.getClientStream().finishPacket();
    }

    private void drawGame(int var1) {
        try {

            if (isAndroid()) {
                this.menuCommon.font = C_MENU_SIZE;
            }

            if (var1 == 13) {
                if (this.deathScreenTimeout != 0) {
                    this.getSurface().fade2black(16316665);
                    this.getSurface().drawColoredStringCentered(this.halfGameWidth(), "Oh dear! You are dead...",
                            0xFF0000, 0, 7, this.halfGameHeight());
                    this.drawChatMessageTabs(var1 - 8);
                    // this.getSurface().draw(this.graphics, this.screenOffsetX,
                    // 256, this.screenOffsetY);
                    clientPort.draw();
                } else if (this.showAppearanceChange) {
                    this.drawAppearancePanelCharacterSprites(-13759);
                } else if (this.showSetRecoveryQuestion) {
                    this.method_182();
                } else if (this.showSetContactDetails) {
                    this.L();
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
                    this.getSurface().drawBox(this.halfGameWidth() - 100, 160 - (isAndroid() ? 80 : 0), 200, 40, 0);
                    if (isAndroid()) {
                        this.getSurface().drawColoredStringCentered(this.halfGameWidth(),
                                "You are sleeping - Fatigue: " + this.fatigueSleeping + "%", 0xFFFF00, var1 - 13, 7, 31);
                    } else {
                        this.getSurface().drawColoredStringCentered(this.halfGameWidth(), "You are sleeping", 0xFFFF00,
                                var1 - 13, 7, 50);
                        this.getSurface().drawColoredStringCentered(this.halfGameWidth(),
                                "Fatigue: " + this.fatigueSleeping + "%", 0xFFFF00, var1 - 13, 7, 90);
                    }
                    this.getSurface().drawColoredStringCentered(this.halfGameWidth(),
                            "When you want to wake up just use your", 0xFFFFFF, 0, 5, 140 - (isAndroid() ? 80 : 0));
                    this.getSurface().drawColoredStringCentered(this.halfGameWidth(),
                            "keyboard to type the word in the box below", 0xFFFFFF, var1 ^ 13, 5, 160 - (isAndroid() ? 80 : 0));
                    this.getSurface().drawColoredStringCentered(this.halfGameWidth(), this.inputTextCurrent + "*",
                            '\uffff', var1 - 13, 5, 180 - (isAndroid() ? 80 : 0));
                    if (null != this.sleepingStatusText) {
                        this.getSurface().drawColoredStringCentered(this.halfGameWidth(), this.sleepingStatusText,
                                0xFF0000, 0, 5, 260 - (isAndroid() ? 110 : 0));
                    } else {
                        this.getSurface().drawSprite(getSurface().spriteVerts[2], this.halfGameWidth() - 127, 230 - (isAndroid() ? 110 : 0));
                    }

                    this.getSurface().drawBoxBorder(this.halfGameWidth() - 128, 257, 229 - (isAndroid() ? 110 : 0), 42, 0xFFFFFF);
                    this.drawChatMessageTabs(5);
                    this.getSurface().drawColoredStringCentered(this.halfGameWidth(), "If you can\'t read the word",
                            0xFFFFFF, var1 - 13, 1, 290 - (isAndroid() ? 110 : 0));
                    this.getSurface().drawColoredStringCentered(this.halfGameWidth(),
                            "@yel@click here@whi@ to get a different one", 0xFFFFFF, var1 ^ 13, 1, 305 - (isAndroid() ? 110 : 0));
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


                        if (!this.doCameraZoom) {
                            amountToZoom -= 50;
                            this.doCameraZoom = true;
                        }

                        // Sets camera zoom distance based on last saved value in the player cache
                        cameraZoom = minCameraZoom + (Config.C_LAST_ZOOM * 2);

                        if ((this.lastHeightOffset == 0
                                && (world.collisionFlags[this.localPlayer.currentX / 128][this.localPlayer.currentZ
                                / 128] & 0x80) == 0 && !C_HIDE_ROOFS)) {

                            this.scene.addModel(this.world.modelRoofGrid[this.lastHeightOffset][centerX]);
                            if (this.lastHeightOffset == 0) {
                                this.scene.addModel(this.world.modelWallGrid[1][centerX]);
                                this.scene.addModel(this.world.modelRoofGrid[1][centerX]);
                                this.scene.addModel(this.world.modelWallGrid[2][centerX]);
                                this.scene.addModel(this.world.modelRoofGrid[2][centerX]);
                            }

                            if (this.doCameraZoom) {
                                amountToZoom += 50;
                                this.doCameraZoom = false;
                            }
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

                    this.scene.reduceSprites((byte) 67, this.spriteCount);
                    this.spriteCount = 0;

                    for (centerX = 0; this.playerCount > centerX; ++centerX) {
                        ORSCharacter var3 = this.players[centerX];
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

                            if (var3.direction == ORSCharacterDirection.COMBAT_A) {
                                this.scene.setCombatXOffset(var1 + 24, var7, -30);
                            }

                            if (var3.direction == ORSCharacterDirection.COMBAT_B) {
                                this.scene.setCombatXOffset(var1 ^ 45, var7, 30);
                            }
                        }
                    }

                    for (centerX = 0; centerX < this.playerCount; ++centerX) {
                        ORSCharacter var3 = this.players[centerX];
                        if (var3.projectileRange > 0) {
                            ORSCharacter var16 = null;
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
                                this.scene.drawSprite(var3.incomingProjectileSprite.id + spriteProjectile, var13,
                                        0, var11, var12, 32, 32, (byte) 109);
                                ++this.spriteCount;
                            }
                        }
                    }

                    for (centerX = 0; centerX < this.npcCount; ++centerX) {
                        ORSCharacter var3 = this.npcs[centerX];
                        if (var3.projectileRange > 0) {
                            ORSCharacter var16 = null;
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
                                this.scene.drawSprite(var3.incomingProjectileSprite.id + spriteProjectile, var13,
                                        0, var11, var12, 32, 32, (byte) 109);
                                ++this.spriteCount;
                            }
                        }
                    }

                    for (centerX = 0; this.npcCount > centerX; ++centerX) {
                        ORSCharacter var3 = this.npcs[centerX];
                        int var4 = var3.currentX;
                        int var5 = var3.currentZ;
                        int var6 = -this.world.getElevation(var4, var5);
                        int var7 = this.scene.drawSprite(20000 + centerX, var5, centerX + 30000, var4, var6,
                                EntityHandler.getNpcDef(var3.npcId).getCamera1(),
                                EntityHandler.getNpcDef(var3.npcId).getCamera2(), (byte) 109);
                        ++this.spriteCount;
                        if (var3.direction == ORSCharacterDirection.COMBAT_A) {
                            this.scene.setCombatXOffset(86, var7, -30);
                        }

                        if (var3.direction == ORSCharacterDirection.COMBAT_B) {
                            this.scene.setCombatXOffset(var1 ^ 99, var7, 30);
                        }
                    }

                    int centerZ;
                    if (C_SHOW_GROUND_ITEMS != 1) {

                        for (centerX = 0; centerX < this.groundItemCount; ++centerX) {
                            if (C_SHOW_GROUND_ITEMS == 3
                                    && (this.groundItemID[centerX] == 20 || this.groundItemID[centerX] == 814 || this.groundItemID[centerX] == 413 || this.groundItemID[centerX] == 604))
                                continue;
                            else if (C_SHOW_GROUND_ITEMS == 2 && (this.groundItemID[centerX] != 20 && this.groundItemID[centerX] != 814 && this.groundItemID[centerX] != 413 && this.groundItemID[centerX] != 604)) {
                                continue;
                            }
                            centerZ = this.groundItemX[centerX] * this.tileSize + 64;
                            int var4 = this.tileSize * this.groundItemZ[centerX] + 64;
                            this.scene.drawSprite('\u9c40' + this.groundItemID[centerX], var4, centerX + 20000, centerZ,
                                    -this.world.getElevation(centerZ, var4) - this.groundItemHeight[centerX], 96, 64, (byte) 109);
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
                        this.scene.setFrustum(-50, centerZ, 0, -50, centerX, -10);
                    }

                    this.characterBubbleCount = 0;
                    this.characterHealthCount = 0;
                    this.characterDialogCount = 0;
                    if (this.cameraAutoAngleDebug) {
                        if (this.optionCameraModeAuto && !this.doCameraZoom) {
                            centerX = this.cameraAngle;
                            this.autoRotateCamera((byte) 22);
                            if (centerX != this.cameraAngle) {
                                this.cameraPositionZ = this.localPlayer.currentZ;
                                this.cameraPositionX = this.localPlayer.currentX;
                            }
                        }

                        this.cameraRotation = this.cameraAngle * 32;
                        this.scene.fogLandscapeDistance = 3000;
                        this.scene.fogEntityDistance = 3000;
                        this.scene.fogZFalloff = 1;
                        this.scene.fogSmoothingStartDistance = 2800;

                        centerX = this.cameraPositionX + this.cameraAutoMoveX;
                        centerZ = this.cameraPositionZ + this.cameraAutoMoveZ;
                        this.scene.setCamera(centerX, -this.world.getElevation(centerX, centerZ), centerZ, cameraPitch,
                                this.cameraRotation * 4, 0, 2000);

                        int zoomMultiplier = 0;
                        if (Config.S_ZOOM_VIEW_TOGGLE)
                            zoomMultiplier = Config.C_ZOOM == 0 ? 0 : Config.C_ZOOM == 1 ? +200 : Config.C_ZOOM == 2 ? +400 : -200;

                        this.scene.setCamera(centerX, -this.world.getElevation(centerX, centerZ), centerZ, cameraPitch,
                                this.cameraRotation * 4, 0, (this.cameraZoom + zoomMultiplier) * 2);

                    } else {
                        if (this.optionCameraModeAuto && !this.doCameraZoom) {
                            this.autoRotateCamera((byte) 94);
                        }
                        if (C_HIDE_FOG) {
                            if (!this.interlace) {
                                this.scene.fogZFalloff = 1;
                                this.scene.fogLandscapeDistance = gameWidth * 2 + cameraZoom * 2 - 124;
                                this.scene.fogEntityDistance = gameWidth * 2 + cameraZoom * 2 - 124;
                                this.scene.fogSmoothingStartDistance = gameWidth * 2 + cameraZoom * 2 - 224;
                            } else {
                                this.scene.fogZFalloff = 1;
                                this.scene.fogLandscapeDistance = gameWidth * 2 + cameraZoom * 2 - 324;
                                this.scene.fogEntityDistance = gameWidth * 2 + cameraZoom * 2 - 324;
                                this.scene.fogSmoothingStartDistance = gameWidth * 2 + cameraZoom * 2 - 424;
                            }
                        } else {
                            this.scene.fogZFalloff = 1;
                            this.scene.fogLandscapeDistance = cameraZoom * 6;
                            this.scene.fogEntityDistance = cameraZoom * 6;
                            this.scene.fogSmoothingStartDistance = cameraZoom * 6;
                        }

                        centerX = this.cameraPositionX + this.cameraAutoMoveX;
                        centerZ = this.cameraPositionZ + this.cameraAutoMoveZ;

                        this.scene.setCamera(centerX, -this.world.getElevation(centerX, centerZ), centerZ, cameraPitch,
                                this.cameraRotation * 4, 0, this.cameraZoom * 2);
                    }

                    this.scene.endScene(-113);
                    this.drawCharacterOverlay();

                    if (this.mouseClickXStep > 0) {
                        switch ((int) ((24 - this.mouseClickXStep) / 6)) {
                            case 0:
                                this.getSurface().drawSprite(spriteSelect(EntityHandler.GUIparts.get(GUIPARTS.YELLOWX1.id())),
                                        this.mouseWalkX - 8, this.mouseWalkY - 8);
                                break;
                            case 1:
                                this.getSurface().drawSprite(spriteSelect(EntityHandler.GUIparts.get(GUIPARTS.YELLOWX2.id())),
                                        this.mouseWalkX - 8, this.mouseWalkY - 8);
                                break;
                            case 2:
                                this.getSurface().drawSprite(spriteSelect(EntityHandler.GUIparts.get(GUIPARTS.YELLOWX3.id())),
                                        this.mouseWalkX - 8, this.mouseWalkY - 8);
                                break;
                            case 3:
                                this.getSurface().drawSprite(spriteSelect(EntityHandler.GUIparts.get(GUIPARTS.YELLOWX4.id())),
                                        this.mouseWalkX - 8, this.mouseWalkY - 8);
                                break;
                        }
                    } else if (this.mouseClickXStep < 0) {
                        switch ((int) ((this.mouseClickXStep + 24) / 6)) {
                            case 0:
                                this.getSurface().drawSprite(spriteSelect(EntityHandler.GUIparts.get(GUIPARTS.REDX1.id())),
                                        this.mouseWalkX - 8, this.mouseWalkY - 8);
                                break;
                            case 1:
                                this.getSurface().drawSprite(spriteSelect(EntityHandler.GUIparts.get(GUIPARTS.REDX2.id())),
                                        this.mouseWalkX - 8, this.mouseWalkY - 8);
                                break;
                            case 2:
                                this.getSurface().drawSprite(spriteSelect(EntityHandler.GUIparts.get(GUIPARTS.REDX3.id())),
                                        this.mouseWalkX - 8, this.mouseWalkY - 8);
                                break;
                            case 3:
                                this.getSurface().drawSprite(spriteSelect(EntityHandler.GUIparts.get(GUIPARTS.REDX4.id())),
                                        this.mouseWalkX - 8, this.mouseWalkY - 8);
                                break;
                        }
                        //this.getSurface().drawSprite(spriteSelect(18 + mudclient.spriteMedia + (this.mouseClickXStep + 24) / 6),
                        //	this.mouseWalkX - 8, this.mouseWalkY - 8);
                    }

                    if (this.systemUpdate != 0) {
                        centerX = this.systemUpdate / 50;
                        centerZ = centerX / 60;
                        centerX %= 60;
                        if (centerX < 10) {
                            this.getSurface().drawColoredStringCentered(256,
                                    "Automatic server restart in: " + centerZ + ":0" + centerX, 0xFFFF00, 0, 1,
                                    this.getGameHeight() - 7);
                        } else {
                            this.getSurface().drawColoredStringCentered(256,
                                    "Automatic server restart in: " + centerZ + ":" + centerX, 0xFFFF00, 0, 1,
                                    this.getGameHeight() - 7);
                        }
                    }
                    if (S_WANT_EXPERIENCE_ELIXIRS && this.elixirTimer != 0) {
                        centerX = this.elixirTimer / 50;
                        centerZ = centerX / 60;
                        centerX %= 60;
                        if (inWild) {
                            if (centerX < 10) {
                                this.getSurface().drawColoredStringCentered(this.getGameWidth() - 53,
                                        "EXP Elixir: " + centerZ + ":0" + centerX, 0x9139e7, 0, 0,
                                        this.getGameHeight() - 62);
                            } else {
                                this.getSurface().drawColoredStringCentered(this.getGameWidth() - 53,
                                        "EXP Elixir: " + centerZ + ":" + centerX, 0x9139e7, 0, 0,
                                        this.getGameHeight() - 62);
                            }
                        } else if (!inWild) {
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
                    if (C_KILL_FEED) {
                        killQueue.clean();
                        int Offset = 0;
                        for (KillAnnouncer notify : killQueue.Kill) {
                            int picture_width = 20;
                            int width_killed = 507 - this.getSurface().stringWidth(1, notify.killedString);
                            int width_icon = 507 - this.getSurface().stringWidth(1, notify.killedString) - picture_width - 5;
                            int width_killer = 507 - this.getSurface().stringWidth(1, notify.killedString) - picture_width - 8 - this.getSurface().stringWidth(1, notify.killerString);

                            this.getSurface().drawString(notify.killerString, width_killer, 50 + Offset, 0xffffff, 1);
                            switch (notify.killPicture) {
                                case -1:
                                    getSurface().drawSpriteClipping(spriteSelect(EntityHandler.projectiles.get(PROJECTILE_TYPES.RANGED.id())), width_icon, 36 + Offset, picture_width,
                                            18, 0, 0, false, 0, 1);
                                    break;
                                case -2:
                                    getSurface().drawSpriteClipping(spriteSelect(EntityHandler.projectiles.get(PROJECTILE_TYPES.MAGIC.id())), width_icon, 36 + Offset, picture_width,
                                            18, 0, 0, false, 0, 1);
                                    break;
                                default:
                                    getSurface().drawSpriteClipping(spriteSelect(EntityHandler.getItemDef(notify.killPicture)), width_icon, 36 + Offset, picture_width,
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
                            this.getSurface().drawSprite(spriteSelect(EntityHandler.GUIparts.get(GUIPARTS.SKULL.id())), this.getGameWidth() - 59, this.getGameHeight() - 56);
                            this.getSurface().drawColoredStringCentered(this.getGameWidth() - 47, "Wilderness", 0xFFFF00, 0, 1,
                                    this.getGameHeight() - 20);
                            this.getSurface().drawColoredStringCentered(this.getGameWidth() - 47, "Level: " + centerZ, 0xFFFF00, 0, 1,
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
                    if (S_SIDE_MENU_TOGGLE && C_SIDE_MENU_OVERLAY) {
                        int i = 130;
                        if (localPlayer.isDev()) {
                            this.getSurface().drawString("Tile: @gre@(@whi@" + (playerLocalX + midRegionBaseX)
                                    + "@gre@,@whi@" + (playerLocalZ + midRegionBaseZ) + "@gre@)", 7, i, 0xffffff, 1);
                            i += 14;
                        }
                        //this.getSurface().drawString("FPS: @gre@(@whi@" + FPS + "@gre@)", 7, i, 0xffffff, 1);
                        //i += 14;
                        this.getSurface().drawString(
                                "Hits: " + this.playerStatCurrent[3] + "@gre@/@whi@" + this.playerStatBase[3], 7, i, 0xffffff, 1);
                        i += 14;
                        this.getSurface().drawString(
                                "Prayer: " + this.playerStatCurrent[5] + "@gre@/@whi@" + this.playerStatBase[5], 7, i, 0xffffff, 1);
                        i += 14;
                        this.getSurface().drawString(
                                "Kills: " + this.statKills2 + "@whi@", 7, i, 0xffffff, 1);
                        if (Config.S_WANT_FATIGUE) {
                            i += 14;
                            this.getSurface().drawString(
                                    "Fatigue: " + this.statFatigue + "%", 7, i, 0xffffff, 1);
                        }
                        if (Config.DEBUG) {
                            i += 14;
                            this.getSurface().drawString("Camera Zoom: " + cameraZoom, 7, i, 0xffffff, 1);
                            i += 14;
                            this.getSurface().drawString("Camera Pitch: " + cameraPitch, 7, i, 0xffffff, 1);
                            i += 14;
                            this.getSurface().drawString("Camera Rotation: " + cameraRotation, 7, i, 0xffffff, 1);
                            int cameraX = this.cameraPositionX + this.cameraAutoMoveX;
                            int cameraZ = this.cameraPositionZ + this.cameraAutoMoveZ;
                            int cameraY = -this.world.getElevation(cameraX, cameraZ) - 170;
                            i += 14;
                            this.getSurface().drawString("Frame: " + cameraAutoMoveFrameCount, 7, i, 0xffffffff, 1);
                            i += 14;
                            this.getSurface().drawString("Camera X: " + cameraX + " (" + this.cameraPositionX + " + " + this.cameraAutoMoveX + ") [" + this.localPlayer.currentX + "] [" + cameraAutoMoveAmountX + "]", 7, i, 0xffffff, 1);
                            i += 14;
                            this.getSurface().drawString("Camera Z: " + cameraZ + " (" + this.cameraPositionZ + " + " + this.cameraAutoMoveZ + ") [" + this.localPlayer.currentZ + "] [" + cameraAutoMoveAmountZ + "]", 7, i, 0xffffff, 1);
                            i += 14;
                            this.getSurface().drawString("Camera Y: " + cameraY, 7, i, 0xffffff, 1);
                        }
                    }

                    if (S_EXPERIENCE_COUNTER_TOGGLE && C_EXPERIENCE_COUNTER == 2) {
                        this.drawExperienceCounter(recentSkill);
                    }

                    if (isAndroid() && Config.S_WANT_PLAYER_COMMANDS) { // on screen buttons for various player chat commands
                        if (F_SHOWING_KEYBOARD) {
							C_SIDE_MENU_OVERLAY = false;
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
                            if (S_WANT_CLANS) {
                                uiX += uiWidth + 15;
                                this.getSurface().drawBoxAlpha(uiX, uiY, uiWidth, uiHeight, 0x659CDE, 160);
                                this.getSurface().drawBoxBorder(uiX, uiWidth, uiY, uiHeight, 0);
                                this.getSurface().drawString("@whi@Clan", uiX + 12, uiY + 20, 0xffffff, 1);
                                if (this.mouseButtonClick != 0) {
                                    if (this.mouseX >= uiX && this.mouseX <= uiX + uiWidth && this.mouseY >= uiY && this.mouseY <= uiY + uiHeight) {
                                        this.mouseButtonClick = 0;
                                        this.panelMessageTabs.setText(this.panelMessageEntry, "::clan ");
                                    }
                                }
                            }
                            uiX += uiWidth + 15;
                            this.getSurface().drawBoxAlpha(uiX, uiY, uiWidth, uiHeight, 0xff0000, 160);
                            this.getSurface().drawBoxBorder(uiX, uiWidth, uiY, uiHeight, 0);
                            this.getSurface().drawString("@whi@Online", uiX + 12, uiY + 20, 0xffffff, 1);
                            if (this.mouseButtonClick != 0) {
                                if (this.mouseX >= uiX && this.mouseX <= uiX + uiWidth && this.mouseY >= uiY && this.mouseY <= uiY + uiHeight) {
                                    this.mouseButtonClick = 0;
                                    this.panelMessageTabs.setText(this.panelMessageEntry, "::onlinelist");
                                }
                            }
							/*uiX += uiWidth + 15;
							this.getSurface().drawBoxAlpha(uiX, uiY, uiWidth, uiHeight, 0xff0000, 160);
							this.getSurface().drawBoxBorder(uiX, uiWidth, uiY, uiHeight, 0);
							this.getSurface().drawString("@whi@Info", uiX + 20, uiY + 20, 0xffffff, 1);
							if (this.mouseButtonClick != 0) {
								if (this.mouseX >= uiX && this.mouseX <= uiX + uiWidth && this.mouseY >= uiY && this.mouseY <= uiY + uiHeight) {
									this.mouseButtonClick = 0;
									this.panelMessageTabs.setText(this.panelMessageEntry, "::info");
								}
							}
							uiX += uiWidth + 15;
							this.getSurface().drawBoxAlpha(uiX, uiY, uiWidth, uiHeight, 0xff0000, 160);
							this.getSurface().drawBoxBorder(uiX, uiWidth, uiY, uiHeight, 0);
							this.getSurface().drawString("@whi@Commands", uiX + 20, uiY + 20, 0xffffff, 1);
							if (this.mouseButtonClick != 0) {
								if (this.mouseX >= uiX && this.mouseX <= uiX + uiWidth && this.mouseY >= uiY && this.mouseY <= uiY + uiHeight) {
									this.mouseButtonClick = 0;
									this.panelMessageTabs.setText(this.panelMessageEntry, "::commands");
								}
							}*/
                        }
                    }

                    if (isAndroid()) {
                        if (F_SHOWING_KEYBOARD) {
                            panelMessageTabs.reposition(panelMessageEntry, 7, 130 + 10, getGameWidth() - 14, 14);
                        } else {
                            panelMessageTabs.reposition(panelMessageEntry, 7, getGameHeight() - 10, getGameWidth() - 14, 14);
                        }
                    }

                    if (this.messageTabSelected == MessageTab.ALL) {
                        for (centerX = 0; centerX < messagesArray.length; ++centerX) {
                            if (MessageHistory.messageHistoryTimeout[centerX] > 0) {
                                String var17 = MessageHistory.messageHistoryColor[centerX]
                                        + StringUtil.formatMessage(MessageHistory.messageHistoryMessage[centerX],
                                        MessageHistory.messageHistorySender[centerX],
                                        MessageHistory.messageHistoryType[centerX], MessageHistory.messageHistoryColor[centerX]);
                                double boost = this.getGameHeight();
                                if (isAndroid() && F_SHOWING_KEYBOARD)
                                    boost = (boost / 2.5) + 8;
                                this.getSurface().drawColoredString(7, (int) boost - centerX * 12 - 18, var17,
                                        1, 0xFFFF00, MessageHistory.messageHistoryCrownID[centerX]);
                            }
                        }
                    }


                    boolean LAST_FRAME_SHOWING_KEYBOARD = F_SHOWING_KEYBOARD;
                    this.panelMessageTabs.hide(this.panelMessageChat);
                    this.panelMessageTabs.hide(this.panelMessageQuest);
                    this.panelMessageTabs.hide(this.panelMessagePrivate);
                    this.panelMessageTabs.hide(this.panelMessageClan);
                    if (this.messageTabSelected == MessageTab.CHAT) {
                        this.panelMessageTabs.show(this.panelMessageChat);
                    } else if (this.messageTabSelected == MessageTab.QUEST) {
                        this.panelMessageTabs.show(this.panelMessageQuest);
                    } else if (this.messageTabSelected == MessageTab.PRIVATE) {
                        this.panelMessageTabs.show(this.panelMessagePrivate);
                    } else if (this.messageTabSelected == MessageTab.CLAN) {
                        this.panelMessageTabs.show(this.panelMessageClan);
                    }

                    MiscFunctions.textListEntryHeightMod = 2;
                    this.panelMessageTabs.drawPanel();
                    MiscFunctions.textListEntryHeightMod = 0;
                    this.getSurface().a(spriteSelect(EntityHandler.GUIparts.get(GUIPARTS.MENUBAR.id())), 0, this.getSurface().width2 - 200, 128, 3);
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

    private void drawInputX() {
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
                this.getSurface().drawBoxBorder(xr, this.inputX_Width, yr, this.inputX_Height, 0xFFFFFF);
                int lineHeightBase = this.getSurface().fontHeight(1);
                int inputLineHeight = this.getSurface().fontHeight(4);
                int lineHeight = lineHeightBase + 2;

                for (int i = 0; i < this.inputX_Lines.length; ++i) {
                    this.getSurface().drawColoredStringCentered(200 + xr, this.inputX_Lines[i], 0xFFFF00, 0, 1,
                            lineHeight * i + 5 + yr + lineHeightBase);
                }

                if (this.inputX_Focused) {
                    this.getSurface().drawColoredStringCentered(200 + xr, this.inputTextCurrent + "*", 0xFFFFFF, 0, 4,
                            yr + 5 + lineHeight * this.inputX_Lines.length + 3 + inputLineHeight);
                }

                int okLineY = lineHeightBase + 8 + yr + inputLineHeight + 2 + this.inputX_Lines.length * lineHeight;

                int color = 0xFFFFFF;
                if (this.mouseX > xr + 200 - 26 && this.mouseX < xr + 200 - 8 && okLineY - lineHeightBase < this.mouseY
                        && this.mouseY < okLineY) {
                    color = 0xFFFF00;
                    if (this.mouseButtonClick != 0) {
                        this.inputX_OK = true;
                        this.mouseButtonClick = 0;
                        this.inputTextFinal = this.inputTextCurrent;
                    }
                }
                this.getSurface().drawString("OK", 200 + xr - 26, okLineY, color, 1);

                color = 0xFFFFFF;
                if (this.mouseX > xr + 208 && this.mouseX < xr + 208 + 40 && this.mouseY > okLineY - lineHeightBase
                        && this.mouseY < okLineY) {
                    color = 0xFFFF00;
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
                            str = str.substring(str.length() - 10);
                        }
                        int var4 = Integer.MAX_VALUE;
                        long intOverflowCheck = Long.parseLong(str);
                        if (intOverflowCheck < Integer.MAX_VALUE) {
                            var4 = Integer.parseInt(str);
                        }
                        this.tradeOffer(var4, this.tradeDoX_Slot);
                    } catch (NumberFormatException var16) {
                        System.out.println("Trade offer X number format exception: " + var16);
                    }
                } else if (this.inputX_Action == InputXAction.TRADE_REMOVE) {
                    try {
                        if (str.length() > 10) {
                            str = str.substring(str.length() - 10);
                        }
                        int var4 = Integer.MAX_VALUE;
                        long intOverflowCheck = Long.parseLong(str);
                        if (intOverflowCheck < Integer.MAX_VALUE) {
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
                                str = str.substring(str.length() - 10);
                            }
                            int var4 = Integer.MAX_VALUE;
                            long intOverflowCheck = Long.parseLong(str);
                            if (intOverflowCheck < Integer.MAX_VALUE) {
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
                                str = str.substring(str.length() - 10);
                            }
                            int var4 = Integer.MAX_VALUE;
                            long intOverflowCheck = Long.parseLong(str);
                            if (intOverflowCheck < Integer.MAX_VALUE) {
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
                                str = str.substring(str.length() - 10);
                            }
                            int var4 = Integer.MAX_VALUE;
                            long intOverflowCheck = Long.parseLong(str);
                            if (intOverflowCheck < Integer.MAX_VALUE) {
                                var4 = Integer.parseInt(str);
                            }
                            this.packetHandler.getClientStream().newPacket(236);
                            this.packetHandler.getClientStream().writeBuffer1.putShort(id);
                            this.packetHandler.getClientStream().writeBuffer1
                                    .putShort(this.shopItemCount[this.shopSelectedItemIndex]);
                            this.packetHandler.getClientStream().writeBuffer1.putShort(var4);
                            this.packetHandler.getClientStream().finishPacket();
                        }
                    } catch (NumberFormatException var10) {
                        System.out.println("Shop buy X number format exception: " + var10);
                    }
                } else if (this.inputX_Action == InputXAction.SHOP_SELL) {
                    try {
                        int id = this.shopItemID[this.shopSelectedItemIndex];
                        if (id != -1) {
                            if (str.length() > 10) {
                                str = str.substring(str.length() - 10);
                            }
                            int var4 = Integer.MAX_VALUE;
                            long intOverflowCheck = Long.parseLong(str);
                            if (intOverflowCheck < Integer.MAX_VALUE) {
                                var4 = Integer.parseInt(str);
                            }
                            this.packetHandler.getClientStream().newPacket(221);
                            this.packetHandler.getClientStream().writeBuffer1.putShort(this.shopItemID[this.shopSelectedItemIndex]);
                            this.packetHandler.getClientStream().writeBuffer1
                                    .putShort(this.shopItemCount[this.shopSelectedItemIndex]);
                            this.packetHandler.getClientStream().writeBuffer1.putShort(var4);
                            this.packetHandler.getClientStream().finishPacket();
                        }
                    } catch (NumberFormatException var13) {
                        System.out.println("Shop sell X number format exception: " + var13);
                    }
                } else if (this.inputX_Action == InputXAction.DUEL_STAKE) {
                    try {
                        if (str.length() > 10) {
                            str = str.substring(str.length() - 10);
                        }
                        int var4 = Integer.MAX_VALUE;
                        long intOverflowCheck = Long.parseLong(str);
                        if (intOverflowCheck < Integer.MAX_VALUE) {
                            var4 = Integer.parseInt(str);
                        }
                        this.duelStakeItem(var4, this.duelDoX_Slot);
                    } catch (NumberFormatException var12) {
                        System.out.println("Stake add X number format exception: " + var12);
                    }
                } else if (this.inputX_Action == InputXAction.DUEL_REMOVE) {
                    try {
                        if (str.length() > 10) {
                            str = str.substring(str.length() - 10);
                        }
                        int var4 = Integer.MAX_VALUE;
                        long intOverflowCheck = Long.parseLong(str);
                        if (intOverflowCheck < Integer.MAX_VALUE) {
                            var4 = Integer.parseInt(str);
                        }
                        this.duelRemoveItem(this.duelDoX_Slot, var4);
                    } catch (NumberFormatException var11) {
                        System.out.println("Stake remove X number format exception: " + var11);
                    }
                } else if (this.inputX_Action == InputXAction.SKIP_TUTORIAL) {
                    this.packetHandler.getClientStream().newPacket(84);
                    this.packetHandler.getClientStream().finishPacket();
                } else if (this.inputX_Action == InputXAction.EXIT_BLACK_HOLE) {
                    this.packetHandler.getClientStream().newPacket(86);
                    this.packetHandler.getClientStream().finishPacket();
                } else if (this.inputX_Action == InputXAction.DROP_X) {
                    try {
                        if (str.length() > 10) {
                            str = str.substring(str.length() - 10);
                        }
                        int var4 = Integer.MAX_VALUE;
                        long intOverflowCheck = Long.parseLong(str);
                        if (intOverflowCheck < Integer.MAX_VALUE) {
                            var4 = Integer.parseInt(str);
                        }
                        this.packetHandler.getClientStream().newPacket(246);
                        this.packetHandler.getClientStream().writeBuffer1.putShort(dropInventorySlot);
                        this.packetHandler.getClientStream().writeBuffer1.putInt(var4);
                        this.packetHandler.getClientStream().finishPacket();
                        dropInventorySlot = -1;
                    } catch (NumberFormatException var13) {
                        System.out.println("Drop X number format exception: " + var13);
                    }
                } else if (this.inputX_Action == InputXAction.INVITE_CLAN_PLAYER) {
                    this.packetHandler.getClientStream().newPacket(199);
                    this.packetHandler.getClientStream().writeBuffer1.putByte(11);
                    this.packetHandler.getClientStream().writeBuffer1.putByte(2);
                    this.packetHandler.getClientStream().writeBuffer1.putString(str);
                    this.packetHandler.getClientStream().finishPacket();
                } else if (this.inputX_Action == InputXAction.KICK_CLAN_PLAYER) {
                    this.packetHandler.getClientStream().newPacket(199);
                    this.packetHandler.getClientStream().writeBuffer1.putByte(11);
                    this.packetHandler.getClientStream().writeBuffer1.putByte(5);
                    this.packetHandler.getClientStream().writeBuffer1.putString(clanKickPlayer);
                    this.packetHandler.getClientStream().finishPacket();
                } else if (this.inputX_Action == InputXAction.CLAN_DELEGATE_LEADERSHIP) {
                    this.packetHandler.getClientStream().newPacket(199);
                    this.packetHandler.getClientStream().writeBuffer1.putByte(11);
                    this.packetHandler.getClientStream().writeBuffer1.putByte(6);
                    this.packetHandler.getClientStream().writeBuffer1.putString(clanKickPlayer);
                    this.packetHandler.getClientStream().writeBuffer1.putByte(1);
                    this.packetHandler.getClientStream().finishPacket();
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
            Sprite sprite = spriteSelect(EntityHandler.getItemDef(id));
            int mask = EntityHandler.getItemDef(id).getPictureMask();
            this.getSurface().drawSpriteClipping(sprite, x, y, width, height, mask, 0, false, 0, 1);

            ItemDef def = EntityHandler.getItemDef(id);
            if (def.getNotedFormOf() >= 0) {
                ItemDef originalDef = EntityHandler.getItemDef(def.getNotedFormOf());
                getSurface().drawSpriteClipping(spriteSelect(originalDef), x + 7, y + 4, width / 2 + 5,
                        height / 2 + 4, originalDef.getPictureMask(), 0, false, 0, 1);
            }

        } catch (RuntimeException var10) {
            throw GenUtil.makeThrowable(var10, "client.CA(" + height + ',' + var2 + ',' + x + ',' + id + ',' + width
                    + ',' + "dummy" + ',' + y + ')');
        }
    }

    private void drawLogin() {
        try {
            this.getSurface().interlace = false;
            this.welcomeScreenShown = false;
            this.getSurface().blackScreen(true);
            if (this.loginScreenNumber == 0 || this.loginScreenNumber == 2 || this.loginScreenNumber == 3) {
                int var2 = this.frameCounter * 2 % 3072;
                if (var2 < 1024) {
                    this.getSurface().drawSprite(getSurface().spriteVerts[0], 0, isAndroid() ? 140 : 10);
                    if (var2 > 768) {
                        this.getSurface().a(getSurface().spriteVerts[1], 0, 0, var2 - 768, isAndroid() ? 140 : 10);
                    }
                } else if (var2 < 2048) {
                    this.getSurface().drawSprite(getSurface().spriteVerts[1], 0, isAndroid() ? 140 : 10);
                    if (var2 > 1792) {
                        this.getSurface().a(getSurface().spriteVerts[2], 0, 0, var2 - 1792, isAndroid() ? 140 : 10); // Logo sprite
                    }
                } else {
                    this.getSurface().drawSprite(getSurface().spriteVerts[2], 0, isAndroid() ? 140 : 10); // Logo sprite
                    if (var2 > 2816) {
                        this.getSurface().a(getSurface().spriteVerts[0], 0, 0, var2 - 2816, isAndroid() ? 140 : 10);
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
                    this.getSurface().drawBoxAlpha(0, halfGameHeight() + 18, this.getGameWidth(), 30, 0, 100);
                }

                this.panelLogin.drawPanel();
            }
            if (this.loginScreenNumber == 3) {
                panelLoginOptions.drawPanel();
            }
            if (this.loginScreenNumber == 4) {
                this.panelRecovery.drawPanel();
            }

            this.getSurface().drawSpriteClipping(spriteSelect(EntityHandler.GUIparts.get(GUIPARTS.BLUEBAR.id())), 0, getGameHeight(), getGameWidth(), 10, 0, 0, false, 0, 1);
            // this.getSurface().draw(this.graphics, this.screenOffsetX, 256,
            // this.screenOffsetY);
            clientPort.draw();

        } catch (RuntimeException var3) {
            throw GenUtil.makeThrowable(var3, "client.R(" + 2540 + ')');
        }
    }

    private void drawMenu() {
        try {

            if (this.mouseButtonClick == 0) {
                int width = this.menuCommon.getWidth();
                int height = this.menuCommon.getHeight();

                if (this.menuX - 10 <= this.mouseX && this.menuY - 10 <= this.mouseY
                        && width + this.menuX + 10 >= this.mouseX && this.mouseY <= 10 + this.menuY + height) {
                    this.menuCommon.render(this.menuY, this.menuX, this.mouseY, (byte) -12, this.mouseX);
                } else {
                    this.topMouseMenuVisible = false;
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

    private boolean drawMenuMessage(String otherAccountName, int var2, String otherDisplayName) {
        try {
            if (inWild) {
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

    private void drawMinimapEntity(int val, int x, byte var3, int y) {
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

            ORSCharacter npc = this.npcs[npcIndex];
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
            if (npc.direction == ORSCharacterDirection.COMBAT_A) {
                var12 = false;
                var13 = 5;
                x -= overlayMovement * def.getCombatSprite() / 100;
                var11 = 2;
                var14 = var13 * 3 + this.animFrameToSprite_CombatA[this.frameCounter / (def.getCombatModel() - 1) % 8];
            } else if (npc.direction == ORSCharacterDirection.COMBAT_B) {
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
                        //int sprite = variant + animationDef.getNumber();
                        Sprite sprite = spriteSelect(animationDef, variant);
                        int something1 = sprite.getSomething1();
                        int something2 = sprite.getSomething2();
                        int something3 = this.spriteSelect(EntityHandler.getAnimationDef(animID), 0).getSomething1();
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

            if (npc.direction == ORSCharacterDirection.COMBAT_A || npc.direction == ORSCharacterDirection.COMBAT_B
                    || npc.combatTimeout != 0) {
                if (npc.combatTimeout > 0) {
                    var15 = x;
                    if (npc.direction == ORSCharacterDirection.COMBAT_B) {
                        var15 = x + overlayMovement * 20 / 100;
                    } else if (npc.direction == ORSCharacterDirection.COMBAT_A) {
                        var15 = x - overlayMovement * 20 / 100;
                    }

                    var16 = npc.healthCurrent * 30 / npc.healthMax;
                    this.characterHealthX[this.characterHealthCount] = width1 / 2 + var15;
                    this.characterHealthY[this.characterHealthCount] = y;
                    this.characterHealthBar[this.characterHealthCount++] = var16;
                }

                if (npc.combatTimeout > 150) {
                    var15 = x;
                    if (npc.direction == ORSCharacterDirection.COMBAT_B) {
                        var15 = x + overlayMovement * 10 / 100;
                    } else if (npc.direction == ORSCharacterDirection.COMBAT_A) {
                        var15 = x - overlayMovement * 10 / 100;
                    }

                    this.getSurface().drawSprite(spriteSelect(EntityHandler.GUIparts.get(GUIPARTS.DAMAGETAKEN.id())), var15 - (12 - width1 / 2),
                            y + height / 2 - 12);
                    this.getSurface().drawColoredStringCentered(width1 / 2 - 1 + var15, "" + npc.damageTaken, 0xFFFFFF,
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

            ORSCharacter player = this.players[index];
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
                if (player.direction == ORSCharacterDirection.COMBAT_B) {
                    wantedAnimDir = 2;
                    actualAnimDir = 5;
                    x += overlayMovement * 5 / 100;
                    mirrorX = true;
                    spriteOffset = this.animFrameToSprite_CombatB[this.frameCounter / 6 % 8] + actualAnimDir * 3;
                } else if (player.direction == ORSCharacterDirection.COMBAT_A) {
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
                            //int sprite = EntityHandler.getAnimationDef(animID).getNumber() + mySpriteOffset;
                            Sprite sprite = spriteSelect(EntityHandler.getAnimationDef(animID), mySpriteOffset);
                            int something1 = sprite.getSomething1();
                            int something2 = sprite.getSomething2();
                            int something3 = this.spriteSelect(EntityHandler.getAnimationDef(animID), 0).getSomething1();
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
                                int colourTransform = 0xFFFFFFFF;

                                if (player.isInvisible)
                                    colourTransform &= 0x80FFFFFF;

                                if (player.isInvulnerable)
                                    colourTransform &= 0xFF202020;

                                this.getSurface().drawSpriteClipping(sprite, xOffset + x, y + yOffset, spriteWidth,
                                        height, colorMask1, colorMask2, mirrorX, topPixelSkew, 1, colourTransform);

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

                if (S_SHOW_FLOATING_NAMETAGS) {
                    if (C_NAME_CLAN_TAG_OVERLAY && this.showUiTab == 0) {
                        if (player.displayName != null)
                            this.getSurface().drawShadowText(player.getStaffName(), (width - this.getSurface().stringWidth(0, player.getStaffName())) / 2 + x + 1, y - 14, 0xffff00, 0, false);
                    }
                    if (C_NAME_CLAN_TAG_OVERLAY && this.showUiTab == 0) {
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

                if (player.direction == ORSCharacterDirection.COMBAT_A
                        || player.direction == ORSCharacterDirection.COMBAT_B || player.combatTimeout != 0) {
                    if (player.combatTimeout > 0) {
                        int var14 = x;
                        if (player.direction == ORSCharacterDirection.COMBAT_B) {
                            var14 = x + overlayMovement * 20 / 100;
                        } else if (player.direction == ORSCharacterDirection.COMBAT_A) {
                            var14 = x - overlayMovement * 20 / 100;
                        }

                        int healthStep = player.healthCurrent * 30 / player.healthMax;
                        this.characterHealthX[this.characterHealthCount] = width / 2 + var14;
                        this.characterHealthY[this.characterHealthCount] = y;
                        this.characterHealthBar[this.characterHealthCount++] = healthStep;
                    }

                    if (player.combatTimeout > 150) {
                        int var14 = x;
                        if (player.direction == ORSCharacterDirection.COMBAT_B) {
                            var14 = x + overlayMovement * 10 / 100;
                        } else if (player.direction == ORSCharacterDirection.COMBAT_A) {
                            var14 = x - overlayMovement * 10 / 100;
                        }

                        this.getSurface().drawSprite(spriteSelect(EntityHandler.GUIparts.get(GUIPARTS.DAMAGEGIVEN.id())), width / 2 + var14 - 12,
                                height / 2 + (y - 12));
                        this.getSurface().drawColoredStringCentered(width / 2 + (var14 - 1), "" + player.damageTaken,
                                0xFFFFFF, 0, 3, height / 2 + y + 5);
                    }
                }

                if (player.skullVisible == 1 && player.bubbleTimeout == 0) {
                    int skullX = topPixelSkew + x + width / 2;
                    if (player.direction == ORSCharacterDirection.COMBAT_A) {
                        skullX -= overlayMovement * 20 / 100;
                    } else if (player.direction == ORSCharacterDirection.COMBAT_B) {
                        skullX += overlayMovement * 20 / 100;
                    }

                    int destWidth = overlayMovement * 16 / 100;
                    int destHeight = overlayMovement * 16 / 100;
                    this.getSurface().drawSprite(spriteSelect(EntityHandler.GUIparts.get(GUIPARTS.SKULL.id())), skullX - destWidth / 2,
                            y - destHeight / 2 - overlayMovement * 10 / 100, destWidth, destHeight, 5924);
                } else if (player.skullVisible == 2 && player.bubbleTimeout == 0) {
                    int skullX = topPixelSkew + x + width / 2;
                    if (player.direction == ORSCharacterDirection.COMBAT_A) {
                        skullX -= overlayMovement * 20 / 100;
                    } else if (player.direction == ORSCharacterDirection.COMBAT_B) {
                        skullX += overlayMovement * 20 / 100;
                    }

                    int destWidth = overlayMovement * 16 / 100;
                    int destHeight = overlayMovement * 16 / 100;
                    getSurface().drawSpriteClipping(spriteSelect(EntityHandler.GUIparts.get(GUIPARTS.SKULL.id())), skullX - destWidth / 2,
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

    private void drawPopupReport(boolean var1) {
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
                this.getSurface().drawBoxBorder(var7, var5, var8, var6, 0xFFFFFF);
                this.getSurface().drawColoredStringCentered(256, "Enter the name of the player you wish to report:",
                        0xFFFF00, 0, 1, 5 + var8 + var3);
                int var9 = var3 + 2;
                this.getSurface().drawColoredStringCentered(256, this.inputTextCurrent + "*", 0xFFFFFF, 0, 4,
                        var4 + var8 + 5 + var9 + 3);
                int var10 = var3 + var4 + 8 + var8 + var9 + 2;
                int var11 = 0xFFFFFF;
                if (var2 > 0) {
                    String var12 = this.reportAbuse_isMute ? "[X]" : "[ ]";
                    if (var2 > 1) {
                        var12 = var12 + " Mute player";
                    } else {
                        var12 = var12 + " Suggest mute";
                    }

                    int var13 = this.getSurface().stringWidth(1, var12);
                    if (this.mouseX > 256 - var13 / 2 && this.mouseX < var13 / 2 + 256 && var10 - var3 < this.mouseY
                            && var10 > this.mouseY) {
                        if (this.mouseButtonClick != 0) {
                            this.reportAbuse_isMute = !this.reportAbuse_isMute;
                            this.mouseButtonClick = 0;
                        }

                        var11 = 0xFFFF00;
                    }

                    this.getSurface().drawColoredStringCentered(256, var12, var11, 0, 1, var10);
                    var10 += 10 + var3;
                }

                var11 = 0xFFFFFF;
                if (this.mouseX > 210 && this.mouseX < 228 && var10 - var3 < this.mouseY && var10 > this.mouseY) {
                    if (this.mouseButtonClick != 0) {
                        this.inputTextFinal = this.inputTextCurrent;
                        this.mouseButtonClick = 0;
                    }

                    var11 = 0xFFFF00;
                }

                this.getSurface().drawString("OK", 210, var10, var11, 1);
                var11 = 0xFFFFFF;
                if (this.mouseX > 264 && this.mouseX < 304 && this.mouseY > var10 - var3 && var10 > this.mouseY) {
                    var11 = 0xFFFF00;
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

    private void drawPopupSocial() {
        try {

            int x = 106;
            int y = 145;

            if (isAndroid())
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
                this.getSurface().drawBoxBorder(106, 300, y, 70, 0xFFFFFF);
                y += 20;
                this.getSurface().drawColoredStringCentered(256, "Enter name to add to friends list", 0xFFFFFF, 0, 4,
                        y);
                y += 20;
                this.getSurface().drawColoredStringCentered(256, this.inputTextCurrent + "*", 0xFFFFFF, 0, 4, y);
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
                this.getSurface().drawBoxBorder(6, 500, y, 70, 0xFFFFFF);
                y += 20;
                this.getSurface().drawColoredStringCentered(256, "Enter message to send to " + this.chatMessageTarget,
                        0xFFFFFF, 0, 4, y);
                y += 20;
                this.getSurface().drawColoredStringCentered(256, this.chatMessageInput + "*", 0xFFFFFF, 0, 4, y);
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
                this.getSurface().drawBoxBorder(106, 300, y, 70, 0xFFFFFF);
                y += 20;
                this.getSurface().drawColoredStringCentered(256, "Enter name to add to ignore list", 0xFFFFFF, 0, 4, y);
                y += 20;
                this.getSurface().drawColoredStringCentered(256, this.inputTextCurrent + "*", 0xFFFFFF, 0, 4, y);
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

            int color = 0xFFFFFF;
            if (this.mouseX > x + 130 && this.mouseX < x + 170 && this.mouseY > y + 10 && this.mouseY < y + 28) {
                color = 0xFFFF00;
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
                color = 0xFF0000 + time * 1280;
                this.getSurface().drawCircle(x + width / 2, y + height / 2, time + 10, color, alpha, -1057205208);
            }

        } catch (RuntimeException var11) {
            throw GenUtil.makeThrowable(var11, "client.LB(" + topPixelSkew + ',' + x + ',' + y + ',' + height + ',' + id
                    + ',' + width + ',' + var7 + ')');
        }
    }

    private void drawTextBox(String line2, byte var2, String line1) {
        try {

            if (var2 == -64) {
                clientPort.drawTextBox(line2, var2, line1);
            }
        } catch (RuntimeException var8) {
            throw GenUtil.makeThrowable(var8, "client.MD(" + (line2 != null ? "{...}" : "null") + ',' + var2 + ','
                    + (line1 != null ? "{...}" : "null") + ')');
        }
    }

    private void drawTradeConfirmDialog(int var1) {
        try {

            byte var2 = 22;
            byte var3 = 36;
            this.getSurface().drawBox(var2, var3, 468, 16, 192);
            int var4 = 10000536;
            this.getSurface().drawBoxAlpha(var2, var3 + 16, 468, 246, var4, 160);
            this.getSurface().drawColoredStringCentered(234 + var2,
                    "Please confirm your trade with @yel@" + this.tradeRecipientConfirmName, 0xFFFFFF, 0, 1, var3 + 12);
            this.getSurface().drawColoredStringCentered(var2 + 117, "You are about to give:", 0xFFFF00, 0, 1,
                    30 + var3);

            int var5;
            String var6;
            for (var5 = 0; this.tradeConfirmItemsCount > var5; ++var5) {
                var6 = EntityHandler.getItemDef(this.tradeConfirmItems[var5]).getName();
                if (EntityHandler.getItemDef(this.tradeConfirmItems[var5]).isStackable()) {
                    var6 = var6 + " x " + StringUtil.formatItemCount(this.tradeConfirmItemsCount1[var5]);
                }

                this.getSurface().drawColoredStringCentered(var2 + 117, var6, 0xFFFFFF, 0, 1, var5 * 12 + 42 + var3);
            }

            if (this.tradeConfirmItemsCount == 0) {
                this.getSurface().drawColoredStringCentered(var2 + 117, "Nothing!", 0xFFFFFF, 0, 1, 42 + var3);
            }

            this.getSurface().drawColoredStringCentered(351 + var2, "In return you will receive:", 0xFFFF00, 0, 1,
                    30 + var3);

            for (var5 = 0; var5 < this.tradeRecipientConfirmItemsCount; ++var5) {
                var6 = EntityHandler.getItemDef(this.tradeRecipientConfirmItems[var5]).getName();
                if (EntityHandler.getItemDef(this.tradeRecipientConfirmItems[var5]).isStackable()) {
                    var6 = var6 + " x " + StringUtil.formatItemCount(this.tradeRecipientConfirmItemCount[var5]);
                }

                this.getSurface().drawColoredStringCentered(351 + var2, var6, 0xFFFFFF, 0, 1, 42 + var3 + var5 * 12);
            }

            if (this.tradeRecipientConfirmItemsCount == 0) {
                this.getSurface().drawColoredStringCentered(351 + var2, "Nothing!", 0xFFFFFF, 0, 1, var3 + 42);
            }

            this.getSurface().drawColoredStringCentered(var2 + 234, "Are you sure you want to do this?", '\uffff', 0, 4,
                    200 + var3);
            this.getSurface().drawColoredStringCentered(var2 + 234,
                    "There is NO WAY to reverse a trade if you change your mind.", 0xFFFFFF, 0, 1, var3 + 215);
            this.getSurface().drawColoredStringCentered(234 + var2, "Remember that not all players are trustworthy",
                    0xFFFFFF, 0, 1, var3 + 230);
            if (this.tradeConfirmAccepted) {
                this.getSurface().drawColoredStringCentered(234 + var2, "Waiting for other player...", 0xFFFF00, 0, 1,
                        250 + var3);
            } else {
                this.getSurface().drawSprite(spriteSelect(EntityHandler.GUIparts.get(GUIPARTS.ACCEPTBUTTON.id())), var2 - 35 + 118, 238 + var3);
                this.getSurface().drawSprite(spriteSelect(EntityHandler.GUIparts.get(GUIPARTS.DECLINEBUTTON.id())), var2 + 352 - 35, var3 + 238);
            }

            if (this.mouseButtonClick == 1) {
                if (this.mouseX < var2 || this.mouseY < var3 || this.mouseX > 468 + var2 || this.mouseY > var3 + 262) {
                    this.showDialogTradeConfirm = false;
                    this.packetHandler.getClientStream().newPacket(230);
                    this.packetHandler.getClientStream().finishPacket();
                }

                if (this.mouseX >= var2 + 118 - 35 && this.mouseX <= var2 + 118 + 70 && this.mouseY >= var3 + 238
                        && this.mouseY <= 238 + var3 + 21) {
                    this.tradeConfirmAccepted = true;
                    this.packetHandler.getClientStream().newPacket(104);
                    this.packetHandler.getClientStream().finishPacket();
                }

                if (352 + var2 - 35 <= this.mouseX && this.mouseX <= var2 + 423 && this.mouseY >= var3 + 238
                        && this.mouseY <= 238 + var3 + 21) {
                    this.showDialogTradeConfirm = false;
                    this.packetHandler.getClientStream().newPacket(230);
                    this.packetHandler.getClientStream().finishPacket();
                }

                this.mouseButtonClick = 0;
            }

        } catch (RuntimeException var7) {
            throw GenUtil.makeThrowable(var7, "client.WD(" + var1 + ')');
        }
    }

    private void drawExperienceCounter(int skill) {
        if (!S_EXPERIENCE_COUNTER_TOGGLE) return;
        if (selectedSkill >= 0) {
            skill = selectedSkill;
        }
        int textColor = C_EXPERIENCE_COUNTER_COLOR == 0 ? 0xFFFFFF :
                C_EXPERIENCE_COUNTER_COLOR == 1 ? 0xFFFF00 :
                        C_EXPERIENCE_COUNTER_COLOR == 2 ? 0xFF0000 :
                                C_EXPERIENCE_COUNTER_COLOR == 3 ? 0x0000FF : 0x00FF00;
        int totalXp = 0;
        long timePassed = 0;
        if (C_EXPERIENCE_COUNTER_MODE == 1 || skill < 0) {
            for (int i = 0; i < skillCount; i++) {
                totalXp += this.playerExperience[i];
            }

            int stringWid = getSurface().stringWidth(3, "Total: " + totalXp);
            int x = halfGameWidth() - (stringWid / 2) - 10;
            int width = stringWid + 6;
            this.getSurface().drawBoxAlpha(x, 0, width, 20, 0x989898, 90);
            //this.getSurface().drawBoxBorder(x, width, 0, 20, 0x000000);

            if (textColor == 0xFFFFFF) {
                getSurface().drawShadowText("Total: " + totalXp, halfGameWidth() - (stringWid / 2) - 4, 15, textColor, 2, false);
            } else {
                getSurface().drawString("Total: " + totalXp, halfGameWidth() - (stringWid / 2) - 4, 15, textColor, 2);
            }

            if (isAndroid() && this.mouseButtonClick == 1 && mouseX >= x && mouseX <= x + width && mouseY >= 0 && mouseY <= 20) {
                if (doubleClick()) {
                    experienceConfigInterface.setVisible(true);
                    setMouseClick(0);
                }
            } else if (!isAndroid() && this.mouseButtonClick == 1 && mouseX >= x && mouseX <= x + width && mouseY >= 0 && mouseY <= 20) {
                experienceConfigInterface.setVisible(true);
                setMouseClick(0);
            }

            if (C_EXPERIENCE_CONFIG_SUBMENU && mouseX >= x && mouseX <= x + width && mouseY >= 0 && mouseY <= 20) {
                // Checks for non-positive gains
                if (this.playerXpGainedTotal < 0) {
                    this.playerXpGainedTotal = 0;
                }

                // Used to make sure it isn't changing so quickly
                xpPerHourCount++;
                if (xpPerHourCount % 100 == 0) {
                    timePassed = System.currentTimeMillis() - this.totalXpGainedStartTime;
                    xpPerHour = this.playerXpGainedTotal / (((double) timePassed) / 3600000);
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
        } else {
            int stringWid = getSurface().stringWidth(3, skillNames[skill] + ": " + playerStatBase[skill] + ": " + playerExperience[skill]);
            int x = (getGameWidth() / 2) - (stringWid / 2) - 10;
            int width = stringWid + 6;
            this.getSurface().drawBoxAlpha(x, 0, width, 20, 0x989898, 90);
            //this.getSurface().drawBoxBorder(x, width, 0, 20, 0x000000);

            int tilLvl = 0, baseTilLvl = 0, progressWidth = 0;
            double progress = 0;
            if (playerStatBase[skill] != S_PLAYER_LEVEL_LIMIT) {
                tilLvl = this.experienceArray[playerStatBase[skill] - 1] - this.playerExperience[skill];
                baseTilLvl = this.experienceArray[playerStatBase[skill]] - this.experienceArray[playerStatBase[skill] - 1];
                progress = ((double) tilLvl) / ((double) baseTilLvl) / 0.9;
                progressWidth = (int) (progress * width);

                this.getSurface().drawBox(x, 19, width - progressWidth, 2, 0x00FF00);
                this.getSurface().drawBox(x + width - progressWidth, 19, progressWidth, 2, 0xFF0000);
            }

            if (textColor == 0xFFFFFF) {
                getSurface().drawShadowText(skillNames[skill] + ": " + playerStatBase[skill] + ": " + playerExperience[skill], (getGameWidth() / 2) - (stringWid / 2) - 4, 15, textColor, 2, false);
            } else {
                getSurface().drawString(skillNames[skill] + ": " + playerStatBase[skill] + ": " + playerExperience[skill], (getGameWidth() / 2) - (stringWid / 2) - 4, 15, textColor, 2);
            }

            if (isAndroid() && this.mouseButtonClick == 1 && mouseX >= x && mouseX <= x + width && mouseY >= 0 && mouseY <= 20) {
                if (doubleClick()) {
                    experienceConfigInterface.setVisible(true);
                    setMouseClick(0);
                }
            } else if (!isAndroid() && this.mouseButtonClick == 1 && mouseX >= x && mouseX <= x + width && mouseY >= 0 && mouseY <= 20) {
                experienceConfigInterface.setVisible(true);
                setMouseClick(0);
            }

            if (C_EXPERIENCE_CONFIG_SUBMENU && mouseX >= x && mouseX <= x + width && mouseY >= 0 && mouseY <= 20) {
                // Checks for non-positive gains
                if (this.playerStatXpGained[skill] < 0) {
                    this.playerStatXpGained[skill] = 0;
                }

                // Used to make sure it isn't changing too quickly
                xpPerHourCount++;
                if (xpPerHourCount % 100 == 0) {
                    timePassed = System.currentTimeMillis() - this.xpGainedStartTime[skill];
                    xpPerHour = this.playerStatXpGained[skill] / (((double) timePassed) / 3600000);
                }
                this.getSurface().drawBoxAlpha(x, 20, width, 61, 0x989898, 90);
                //this.getSurface().drawBoxBorder(x, width, 19, 61, 0x000000);

                if (textColor == 0xFFFFFF) {
                    if (playerStatBase[skill] == S_PLAYER_LEVEL_LIMIT) {
                        this.getSurface().drawShadowText("Gained: " + this.playerStatXpGained[skill], x + 3, 63, textColor, 2, false);
                        this.getSurface().drawShadowText("Xp/hr:     " + (int) xpPerHour, x + 3, 78, textColor, 2, false);
                    } else {
                        this.getSurface().drawShadowText("Next lvl: " + this.experienceArray[playerStatBase[skill] - 1], x + 3, 33, textColor, 2, false);
                        this.getSurface().drawShadowText("Til lvl:     " + tilLvl, x + 3, 48, textColor, 2, false);
                        this.getSurface().drawShadowText("Gained: " + this.playerStatXpGained[skill], x + 3, 63, textColor, 2, false);
                        this.getSurface().drawShadowText("Xp/hr:     " + (int) xpPerHour, x + 3, 78, textColor, 2, false);
                    }
                } else {
                    if (playerStatBase[skill] == S_PLAYER_LEVEL_LIMIT) {
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

    private void drawExperienceConfig() {
        if (!S_EXPERIENCE_COUNTER_TOGGLE) return;
        experienceConfigInterface.onRender(this.getSurface());
    }

    private void drawUi(int var1) {
        try {

            boolean var2 = false;

            try {
                mainComponent.renderComponent();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (lastSelectedSpell != -1 && isAndroid()) {
                int boxWidth = 75;
                int boxHeight = 50;
                int x = getGameWidth() - boxWidth - 15;
                int y = halfGameHeight() - boxHeight;

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
                this.setInitLoginCleared(false);
            } else if (this.showDialogServerMessage) {
                this.drawDialogServerMessage((byte) -115);
            } else if (this.showUiWildWarn != 1) {
                if (this.isShowDialogBank() && this.combatTimeout == 0) {
                    this.drawDialogBank();
                } else if (auctionHouse.isVisible() && combatTimeout == 0) {
                    auctionHouse.onRender(getSurface());
                } else if (ironmanInterface.isVisible() && combatTimeout == 0) {
                    ironmanInterface.onRender(getSurface());
                } else if (achievementInterface.isVisible() && combatTimeout == 0) {
                    achievementInterface.onRender(getSurface());
                } else if (clan.getClanInterface().isVisible()) {
                    clan.getClanInterface().onRender(getSurface());
                } else if (this.showDialogShop && this.combatTimeout == 0) {
                    this.drawDialogShop();
                } else if (S_WANT_SKILL_MENUS && skillGuideInterface.isVisible()) {
                    this.drawSkillGuide();
                } else if (S_WANT_QUEST_MENUS && questGuideInterface.isVisible()) {
                    this.drawQuestGuide();
                } else if (experienceConfigInterface.isVisible()) {
                    this.drawExperienceConfig();
                } else if (doSkillInterface.isVisible() && this.combatTimeout == 0) {
                    this.drawDoSkill();
                } else if (S_ITEMS_ON_DEATH_MENU && lostOnDeathInterface.isVisible()) {
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
                    } else if (this.panelPasswordChange_Mode != PasswordChangeMode.NONE) {
                        this.drawDialogueChangePassword();
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
						this.getSurface().drawBoxBorder(106, 300, var3, 50, 0xFFFFFF);
						this.getSurface().drawBoxAlpha(106, var3, 300, 50, 0, 128);
						var3 += 20;
						this.getSurface().drawColoredStringCentered(256, "Enter chat message", 0xFFFFFF, 0, 4, var3);
						var3 += 20;
						this.getSurface().drawColoredStringCentered(256, var11 + "*", 0xFFFFFF, 0, 4, var3);
					}*/
                if (this.optionsMenuShow) {
                    this.drawDialogOptionsMenu(-312);
                }

                if (((this.localPlayer.direction == ORSCharacterDirection.COMBAT_A
                        || this.localPlayer.direction == ORSCharacterDirection.COMBAT_B) || C_FIGHT_MENU == 2) && C_FIGHT_MENU != 0) {
                    this.drawDialogCombatStyle();
                }

                this.handleTabUIClick();
                boolean mustDrawMenu = !this.optionsMenuShow && !this.topMouseMenuVisible;
                if (mustDrawMenu) {
                    this.menuCommon.recalculateSize(0);
                }

                if (this.showUiTab == 0 && mustDrawMenu) {
                    this.drawUiTab0(var1 ^ 2);
                }

                if (this.showUiTab == 1) {
                    this.drawUiTab1(-15252, mustDrawMenu);
                }

                if (S_INVENTORY_COUNT_TOGGLE && C_INV_COUNT) {
                    this.getSurface().drawShadowText(this.inventoryItemCount + "/30", this.getGameWidth() - 19, 17, (0x00FFFFFF << (int) Math.floor((this.inventoryItemCount / 15)) * 8) & 0x00FFFFFF, 1, true);
                }

                if (this.showUiTab == 2) {
                    this.drawUiTabMinimap(mustDrawMenu, (byte) 125);
                }

                if (this.showUiTab == 3) {
                    this.drawUiTabPlayerInfo(mustDrawMenu, var1 ^ 0);
                }

                if (this.showUiTab == 4) {
                    this.drawUiTabMagic(mustDrawMenu, (byte) -74);
                }

                if (this.showUiTab == 5) {
                    this.drawUiTab5(mustDrawMenu, false);
                }

                if (this.showUiTab == 6) {
                    this.drawUiTabOptions(15, mustDrawMenu);
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

    private void drawDialogBank() {
        bank.onRender();
    }

    // game screen right click menu definitions */
    private void drawUiTab0(int var1) {
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
                        String msg = StringUtil.formatMessage(
                                MessageHistory.messageHistoryMessage[i], MessageHistory.messageHistorySender[i],
                                MessageHistory.messageHistoryType[i], MessageHistory.messageHistoryColor[i]);
                        if (this.mouseX > 7 && this.mouseX < this.getSurface().stringWidth(1, msg) + 7
                                && this.mouseY > -(i * 12) - 30 + this.getGameHeight()
                                && this.getGameHeight() - i * 12 - 18 > this.mouseY
                                && (this.mouseButtonClick == 2
                                || this.optionMouseButtonOne && this.mouseButtonClick == 1)
                                && this.drawMenuMessage(MessageHistory.messageHistoryClan[i], 127,
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
            int var3 = this.scene.b(0);
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
                        // wall object right click menu
                        if (var8 != null && var8.key >= 10000) {
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
                                                            + (localPlayer.isDev() ? " @or1@(" + id + ":"
                                                            + (wallObjectInstanceX[var9] + this.midRegionBaseX)
                                                            + ","
                                                            + (wallObjectInstanceZ[var9] + this.midRegionBaseZ)
                                                            + ","
                                                            + wallObjectInstanceDir[var9]
                                                            + ")" : ""));
                                }

                                this.wallObjectInstance_Arg1[var9] = true;
                            }
                        }
                        // Game Object Right Click Menu
                        else if (null != var8 && var8.key >= 0) {
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
                                                                + (localPlayer.isDev()
                                                                ? " @or1@(" + id + ":"
                                                                + (gameObjectInstanceX[var9]
                                                                + this.midRegionBaseX)
                                                                + ","
                                                                + (gameObjectInstanceZ[var9]
                                                                + this.midRegionBaseZ)
                                                                + ","
                                                                + gameObjectInstanceDir[var9]
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
                            // ground item right click menu
                            if (id == 2) {
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
                                    if (!isAndroid()) {
                                        this.menuCommon
                                                .addCharacterItem(this.groundItemID[var9],
                                                        MenuItemAction.GROUND_ITEM_EXAMINE,
                                                        "Examine", "@lre@"
                                                                + EntityHandler.getItemDef(this.groundItemID[var9])
                                                                .getName()
                                                                + (localPlayer.isDev()
                                                                ? " @or1@(" + groundItemID[var9] + ":"
                                                                + (groundItemX[var9] + midRegionBaseX) + ","
                                                                + (groundItemZ[var9] + midRegionBaseZ) + ","
                                                                + wallObjectInstanceDir[var9] + ")"
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
                                int levelDifference = 0;
                                int var13 = this.npcs[var9].npcId;
                                if (EntityHandler.getNpcDef(var13).isAttackable()) {
                                    int npcLevel = (EntityHandler.getNpcDef(var13).getStr()
                                            + EntityHandler.getNpcDef(var13).getAtt()
                                            + EntityHandler.getNpcDef(var13).getDef()
                                            + EntityHandler.getNpcDef(var13).getHits()) / 4;
                                    int playerLevel = (this.playerStatBase[3] + this.playerStatBase[2]
                                            + this.playerStatBase[1] + this.playerStatBase[0] + 27) / 4;
                                    var11 = "@yel@";
                                    levelDifference = playerLevel - npcLevel;
                                    if (levelDifference < 0) {
                                        var11 = "@or1@";
                                    }

                                    if (levelDifference < -3) {
                                        var11 = "@or2@";
                                    }

                                    if (levelDifference < -6) {
                                        var11 = "@or3@";
                                    }

                                    if (levelDifference < -9) {
                                        var11 = "@red@";
                                    }

                                    if (levelDifference > 0) {
                                        var11 = "@gr1@";
                                    }

                                    if (levelDifference > 3) {
                                        var11 = "@gr2@";
                                    }

                                    if (levelDifference > 6) {
                                        var11 = "@gr3@";
                                    }

                                    if (levelDifference > 9) {
                                        var11 = "@gre@";
                                    }

                                    var11 = " " + var11 + "(level-" + npcLevel + ")";
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
                                                levelDifference >= 0 ? MenuItemAction.NPC_ATTACK1 : MenuItemAction.NPC_ATTACK2,
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
                                                    + (localPlayer.isDev() ? " @or1@(" + this.npcs[var9].npcId + ")" : ""));
                                } else {
                                    this.menuCommon.addCharacterItem_WithID(this.npcs[var9].serverIndex,
                                            "@yel@" + EntityHandler.getNpcDef(this.npcs[var9].npcId).getName(),
                                            MenuItemAction.NPC_USE_ITEM, "Use " + this.m_ig + " with",
                                            this.selectedItemInventoryIndex);
                                }
                            }
                        } else {
                            this.addPlayerToMenu(var9);
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
                    //System.out.println("Build Walk Here Right Click: var2=" + var2 + ", this.world.faceTileX[var2]= " + this.world.faceTileX[var2] + ", this.world.faceTileZ[var2]=" + this.world.faceTileZ[var2]);

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

    // inventory right click menu definitions
    private void drawUiTab1(int var1, boolean var2) {
        try {
            if (var1 != -15252) {
                this.packetHandler.handlePacket2(-79, -83);
            }

            int var3 = this.getSurface().width2 - 248;
            this.getSurface().drawSprite(spriteSelect(EntityHandler.GUIparts.get(GUIPARTS.BAGTAB.id())), var3, 3);

            int var4;
            int var5;
            int id;
            for (var4 = 0; this.m_cl > var4; ++var4) {
                var5 = var3 + var4 % 5 * 49;
                id = var4 / 5 * 34 + 36;
                if (this.inventoryItemCount > var4 && this.inventoryItemEquipped[var4] == 1) {
                    this.getSurface().drawBoxAlpha(var5, id, 49, 34, 0xFF0000, 128);
                } else {
                    this.getSurface().drawBoxAlpha(var5, id, 49, 34, GenUtil.buildColor(181, 181, 181), 128);
                }

                if (var4 < this.inventoryItemCount) {
                    this.getSurface().drawSpriteClipping(
                            spriteSelect(EntityHandler.getItemDef(this.inventoryItemID[var4])),
                            var5, id, 48, 32, EntityHandler.getItemDef(this.inventoryItemID[var4]).getPictureMask(), 0,
                            false, 0, var1 ^ -15251);

                    ItemDef def = EntityHandler.getItemDef(this.inventoryItemID[var4]);
                    if (def.getNotedFormOf() >= 0) {
                        ItemDef originalDef = EntityHandler.getItemDef(def.getNotedFormOf());
                        getSurface().drawSpriteClipping(spriteSelect(originalDef), var5 + 7,
                                id + 4, 33, 23, originalDef.getPictureMask(), 0, false, 0, 1);
                    }
                    if (EntityHandler.getItemDef(this.inventoryItemID[var4]).isStackable()) {
                        this.getSurface().drawString("" + this.inventoryItemSize[var4], 1 + var5,
                                id + 10, 0xFFFF00, 1);
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
                                String equipCommand;
                                if ((24 & EntityHandler.getItemDef(id).wearableID) == 0) {
                                    equipCommand = "Wear";
                                } else {
                                    equipCommand = "Wield";
                                }

                                this.menuCommon.addCharacterItem(var5, MenuItemAction.ITEM_EQUIP, equipCommand,
                                        "@lre@" + EntityHandler.getItemDef(id).getName());
                            }

                            if (!EntityHandler.getItemDef(id).getCommand().equals("")
                                    && EntityHandler.getItemDef(id).getNotedFormOf() == -1) {
                                this.menuCommon.addCharacterItem(var5, MenuItemAction.ITEM_COMMAND,
                                        EntityHandler.getItemDef(id).getCommand(),
                                        "@lre@" + EntityHandler.getItemDef(id).getName());
                            }

                            if (S_WANT_DROP_X && EntityHandler.getItemDef(id).getCommand().equals("Bury")
                                    && EntityHandler.getItemDef(id).getNotedFormOf() == -1) {
                                this.menuCommon.addCharacterItem(var5, MenuItemAction.ITEM_COMMAND_ALL,
                                        //EntityHandler.getItemDef(id).getCommand(), -- generic label.
                                        "Bury All",
                                        "@lre@" + EntityHandler.getItemDef(id).getName());
                            }

                            this.menuCommon.addCharacterItem(var5, MenuItemAction.ITEM_USE, "Use",
                                    "@lre@" + EntityHandler.getItemDef(id).getName());
                            this.menuCommon.addCharacterItem(var5, MenuItemAction.ITEM_DROP, "Drop",
                                    "@lre@" + EntityHandler.getItemDef(id).getName());
                            if (S_WANT_DROP_X) {
                                this.menuCommon.addCharacterItem(var5, MenuItemAction.ITEM_DROP_X, "Drop X",
                                        "@lre@" + EntityHandler.getItemDef(id).getName());
                                this.menuCommon.addCharacterItem(var5, MenuItemAction.ITEM_DROP_ALL, "Drop All",
                                        "@lre@" + EntityHandler.getItemDef(id).getName());
                            }
                            this.menuCommon.addCharacterItem(id, MenuItemAction.ITEM_EXAMINE, "Examine",
                                    "@lre@" + EntityHandler.getItemDef(id).getName()
                                            + (localPlayer.isDev() ? " @or1@(" + id + ")" : ""));
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

    // social tab
    private void drawUiTab5(boolean var1, boolean var2) {
        try {
            int var3 = this.getSurface().width2 - 199;
            byte var4 = 36;
            this.getSurface().drawSprite(spriteSelect(EntityHandler.GUIparts.get(GUIPARTS.MENUSOCIAL.id())), var3 - 49, 3);
            short var5 = 196;
            short var6 = 182;
            int maxWidth = getGameWidth() - 23;
            int minWidth = getGameWidth() - 83;
            if (var2) {
                this.cameraAutoMoveX = -88;
            }

            // if clans are enabled
            if (S_WANT_CLANS) {
                int clanTab;
                int colorB;
                int colorA = colorB = clanTab = GenUtil.buildColor(160, 160, 160);
                if (this.panelSocialTab == 1) {
                    clanTab = GenUtil.buildColor(220, 220, 220);
                    if (clan.inClan()) {
                        this.getSurface().drawBoxAlpha(var3, 24 + var4, var5, 49, GenUtil.buildColor(220, 220, 220), 192);
                        this.getSurface().drawLineHoriz(var3, var4 + 72, var5, 0);
                        this.getSurface().drawBoxAlpha(var3, var4 + var6 - 16 + 34, var5, 49, GenUtil.buildColor(220, 220, 220), 192);
                    } else {
                        this.getSurface().drawBoxAlpha(var3, var4 + var6 - 30, var5, 49, GenUtil.buildColor(220, 220, 220), 192);
                    }
                } else if (this.panelSocialTab == 0) {
                    colorA = GenUtil.buildColor(220, 220, 220);
                } else {
                    colorB = GenUtil.buildColor(220, 220, 220);
                }

                this.getSurface().drawBoxAlpha(var3, var4, 65, 24, colorA, 128);
                this.getSurface().drawBoxAlpha(var3 + var5 / 2 - 32, var4, 65, 24, clanTab, 128);
                this.getSurface().drawBoxAlpha(var3 + var5 / 2 + 33, var4, 65, 24, colorB, 128);
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
            } else { // clans disabled
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
                this.getSurface().drawLineVert(var3 + var5 / 2, var4, 0, 24);
                this.getSurface().drawLineHoriz(var3, var4 + var6 - 16, var5, 0);
                this.getSurface().drawColoredStringCentered(var3 + var5 / 4, "Friends", 0, 0, 4, var4 + 16);
                this.getSurface().drawColoredStringCentered(var3 + var5 / 4 + var5 / 2, "Ignore", 0, 0, 4, var4 + 16);
                this.panelSocial.clearList(this.controlSocialPanel);
            }

            int index;
            String colorKey;
            int var12;

            // friend tab
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
                            null, null);
                }
                this.panelSocial.drawPanel();
            }

            // ignore tab
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
                            0, null, null);
                }

                this.panelSocial.drawPanel();
            }

            // clan tab
            if (this.panelSocialTab == 1) {
                int listX = var3 + 3;
                int listY = 75;

                int buttonColorA = 0x0A2B56, buttonColorB = 0x0A2B56;

                if (clan.inClan()) {
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
                        this.panelClan.setListEntry(this.controlClanPanel, index, (clan.clanRank[index] == 0 ? "     " : "") + colorKey + clanIsh + "          ", (clan.clanRank[index] == 1 ? 3 : clan.clanRank[index] == 2 ? 4 : 0),
                                null, null);
                    }
                    if (this.mouseX > var3 + 20 && this.mouseX < var3 + 94 && this.mouseY > var6 + (var4 + 26)
                            && this.mouseY < var6 + var4 + 60) {
                        buttonColorA = 0x263751;
                        if (getMouseClick() == 1) {
                            this.showUiTab = 0;
                            String[] inputXMessage = new String[]{"Are you sure you want to leave the clan?"};
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
                index = this.panelSocial.getControlSelectedListIndex(this.controlSocialPanel);
                if (index >= 0 && this.mouseX < maxWidth) {
                    if (this.mouseX > minWidth) {
                        this.m_wk = -(index + 2);
                    } else {
                        this.m_wk = index;
                    }
                }

                this.getSurface().drawColoredStringCentered(var5 / 2 + var3, "Click a name to send a message", 0xFFFFFF,
                        0, 1, 35 + var4);
                if (var3 < this.mouseX && this.mouseX < var3 + var5 && this.mouseY > var6 + (var4 - 16)
                        && this.mouseY < var6 + var4) {
                    var17 = 0xFFFF00;
                } else {
                    var17 = 0xFFFFFF;
                }

                this.getSurface().drawColoredStringCentered(var5 / 2 + var3, "Click here to add a friend", var17, 0, 1,
                        var6 + var4 - 3);
            }

            if (this.panelSocialTab == 2) {
                index = this.panelSocial.getControlSelectedListIndex(this.controlSocialPanel);
                if (index >= 0 && this.mouseX < maxWidth) {
                    if (this.mouseX <= minWidth) {
                        this.m_nj = index;
                    } else {
                        this.m_nj = -(index + 2);
                    }
                }

                this.getSurface().drawColoredStringCentered(var3 + var5 / 2, "Blocking messages from:", 0xFFFFFF, 0, 1,
                        35 + var4);
                if (this.mouseX > var3 && var3 + var5 > this.mouseX && var6 + var4 - 16 < this.mouseY
                        && var6 + var4 > this.mouseY) {
                    var17 = 0xFFFF00;
                } else {
                    var17 = 0xFFFFFF;
                }

                this.getSurface().drawColoredStringCentered(var5 / 2 + var3, "Click here to add a name", var17, 0, 1,
                        var4 + (var6 - 3));
            }

            if (var1) {
                int var15 = this.mouseY - 36;
                var3 = 199 + this.mouseX - this.getSurface().width2;
                // handle friends and ignores tab
                if (var3 >= 0 && var15 >= 0 && var3 < 196 && var15 < 26) {
                    this.panelSocial.handleMouse(var3 - 199 + this.getSurface().width2, var15 + 36,
                            this.currentMouseButtonDown, this.lastMouseButtonDown);
                    if (var15 <= 24 && this.mouseButtonClick == 1) {
                        if (S_WANT_CLANS) {
                            if (var3 < 65 && (this.panelSocialTab == 2 || this.panelSocialTab == 1)) {
                                this.panelSocialTab = 0; // Show Friends Tab (Clicked)
                                this.panelSocial.resetList(this.controlSocialPanel);
                            } else if (var3 > 132 && var3 < 196 && (this.panelSocialTab == 1 || this.panelSocialTab == 0)) {
                                this.panelSocialTab = 2; // Show Ignore Tab (Clicked)
                                this.panelSocial.resetList(this.controlSocialPanel);
                            }
                        } else {
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
                // handle clan tab
                if (S_WANT_CLANS) {
                    if (var3 >= 65 && var15 >= 0 && var3 < 132 && var15 < 26) {
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

                // interactions within the panels
                if (var3 >= 0 && var15 >= 0 && var3 < 196 && var15 < 225 && (this.panelSocialTab == 0 || this.panelSocialTab == 2)) {
                    this.panelSocial.handleMouse(var3 - 199 + this.getSurface().width2, var15 + 36,
                            this.currentMouseButtonDown, this.lastMouseButtonDown);
                    if (this.mouseButtonClick >= 1 && this.panelSocialTab == 0) {
                        index = this.panelSocial.getControlSelectedListIndex(this.controlSocialPanel);
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
                        index = this.panelSocial.getControlSelectedListIndex(this.controlSocialPanel);
                        if (index >= 0 && this.mouseX < maxWidth && this.mouseX > minWidth) {
                            this.removeIgnore(SocialLists.ignoreList[index]); // Remove Ignore
                        }
                    }

                    // add friend
                    if (var15 > 166 && this.mouseButtonClick == 1 && this.panelSocialTab == 0) {
                        this.inputTextFinal = "";
                        this.inputTextCurrent = "";
                        this.panelSocialPopup_Mode = SocialPopupMode.ADD_FRIEND;
                    }

                    // add ignore
                    if (var15 > 166 && this.mouseButtonClick == 1 && this.panelSocialTab == 2) {
                        this.panelSocialPopup_Mode = SocialPopupMode.ADD_IGNORE;
                        this.inputTextCurrent = "";
                        this.inputTextFinal = "";
                    }

                    this.mouseButtonClick = 0;
                }

                // clan interactions
                else if (var3 >= 0 && var15 >= 0 && var3 < 196 && var15 < 295 && this.panelSocialTab == 1 && S_WANT_CLANS) {
                    this.panelClan.handleMouse(var3 - 199 + this.getSurface().width2, var15 + 36,
                            this.currentMouseButtonDown, this.lastMouseButtonDown);
                    if (this.mouseButtonClick >= 1 && this.panelSocialTab == 1) {
                        index = this.panelClan.getControlSelectedListIndex(this.controlClanPanel);
                        if (index >= 0 && this.mouseX < maxWidth) {
                            if (mouseButtonClick == 2) {
                                if (StringUtil.displayNameToKey(clan.username[index]).equals(StringUtil.displayNameToKey(this.localPlayer.accountName))) {
                                    return;
                                }
                                if (clan.isClanLeader() || clan.isAllowed(0)) { // Kick From Clan
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
                            }
                        }
                    }
                }
            }
        } catch (RuntimeException npcLevel) {
            throw GenUtil.makeThrowable(npcLevel, "client.VD(" + var1 + ',' + var2 + ')');
        }
    }

    // spells menu
    private void drawUiTabMagic(boolean var1, byte var2) {
        try {
            int var3 = this.getSurface().width2 - 199;
            byte var4 = 36;
            this.getSurface().drawSprite(spriteSelect(EntityHandler.GUIparts.get(GUIPARTS.MENUSPELLS.id())), var3 - 49, 3);
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
                                        0, null, null);
                    }

                    this.panelMagic.drawPanel();
                    var10 = this.panelMagic.getControlSelectedListIndex(this.controlMagicPanel);
                    if (var10 != -1) {
                        this.getSurface().drawString(
                                "Level " + EntityHandler.getSpellDef(var10).getReqLevel() + ": "
                                        + EntityHandler.getSpellDef(var10).getName(),
                                2 + var3, var4 + 124, 0xFFFF00, 1);
                        this.getSurface().drawString(EntityHandler.getSpellDef(var10).getDescription(), 2 + var3,
                                136 + var4, 0xFFFFFF, 0);
                        var18 = 0;
                        for (Entry<Integer, Integer> e : EntityHandler.getSpellDef(var10).getRunesRequired()) {
                            var12 = e.getKey();
                            this.getSurface().drawSprite(
                                    spriteSelect(EntityHandler.getItemDef(var12)),
                                    2 + var3 + var18 * 44, var4 + 150);
                            var13 = this.getInventoryCount(var12);
                            int var14 = e.getValue();
                            String var15 = "@red@";
                            if (this.hasRunes(var12, var14)) {
                                var15 = "@gre@";
                            }
                            this.getSurface().drawString(var15 + var13 + "/" + var14, 2 + var3 + var18 * 44, var4 + 150,
                                    0xFFFFFF, 1);
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
                                        0, null, null);
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
                                        0xFFFF00, 0, 1, var4 + 130);
                        this.getSurface().drawColoredStringCentered(var3 + var5 / 2,
                                EntityHandler.getPrayerDef(var10).getDescription(), 0xFFFFFF, 0, 0, 145 + var4);
                        this.getSurface().drawColoredStringCentered(var3 + var5 / 2,
                                "Drain rate: " + EntityHandler.getPrayerDef(var10).getDrainRate(), 0, 0, 1, 160 + var4);
                    }
                    // this.getSurface().drawColoredStringCentered(var3 + var5 / 2,
                    //		"Prayer points: " + this.playerStatCurrent[5] + "/" + this.playerStatBase[5], 0, 0, 1, 175 + var4);
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
                                    this.showMessage(false, null,
                                            "Your magic ability is not high enough for this spell", MessageType.GAME, 0,
                                            null);
                                } else {
                                    int k3 = 0;
                                    for (Entry<Integer, Integer> e : EntityHandler.getSpellDef(var9)
                                            .getRunesRequired()) {
                                        if (!hasRunes(e.getKey(), e.getValue())) {
                                            this.showMessage(false, null,
                                                    "You don\'t have all the reagents you need for this spell",
                                                    MessageType.GAME, 0, null);
                                            k3 = -1;
                                            break;
                                        }
                                        k3++;
                                    }
                                    if (k3 == EntityHandler.getSpellDef(var9).getRuneCount()) {
                                        this.selectedSpell = var9;
                                        lastSelectedSpell = var9;
                                        this.selectedItemInventoryIndex = -1;
                                        //if (EntityHandler.getSpellDef(var9).getSpellType() == 3 && var9 != 16) {
                                        //	showUiTab = 1;
                                        //}
                                    }
                                }
                            }
                        }

                        if (this.mouseButtonClick == 1 && this.m_Ji == 1) {
                            var9 = this.panelMagic.getControlSelectedListIndex(this.controlMagicPanel);
                            if (var9 != -1) {
                                var10 = this.playerStatBase[5];
                                if (var10 < EntityHandler.getPrayerDef(var9).getReqLevel()) {
                                    this.showMessage(false, null,
                                            "Your prayer ability is not high enough for this prayer", MessageType.GAME,
                                            0, null);
                                } else if (this.playerStatCurrent[5] == 0) {
                                    this.showMessage(false, null,
                                            "You have run out of prayer points. Return to a church to recharge",
                                            MessageType.GAME, 0, null);
                                } else if (!this.prayerOn[var9]) {
                                    this.packetHandler.getClientStream().newPacket(60);
                                    this.packetHandler.getClientStream().writeBuffer1.putByte(var9);
                                    this.packetHandler.getClientStream().finishPacket();
                                    this.prayerOn[var9] = true;
                                    this.playSoundFile("prayeron");
                                } else {
                                    this.packetHandler.getClientStream().newPacket(254);
                                    this.packetHandler.getClientStream().writeBuffer1.putByte(var9);
                                    this.packetHandler.getClientStream().finishPacket();
                                    this.prayerOn[var9] = false;
                                    this.playSoundFile("prayeroff");
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

    // mini map menu
    private void drawUiTabMinimap(boolean var1, byte var2) {
        try {
            int var3 = this.getSurface().width2 - 199;
            short var4 = 156;
            short var5 = 152;
            this.getSurface().drawSprite(spriteSelect(EntityHandler.GUIparts.get(GUIPARTS.MINIMAPTAB.id())), var3 - 49, 3);
            var3 += 40;
            this.getSurface().drawBox(var3, 36, var4, var5, 0);
            this.getSurface().setClip(var3, var4 + var3, 36 + var5, 36);
            if (var2 <= 119) {
                this.characterHealthBar = null;
            }

            int var6 = 192 + this.minimapRandom_2;
            int var7 = 255 & this.cameraRotation + this.minimapRandom_1;
            int mX = var6 * (this.localPlayer.currentX - 6040) * 3 / 2048;
            int mZ = var6 * (this.localPlayer.currentZ - 6040) * 3 / 2048;
            int var10 = FastMath.trigTable_1024[1023 & 1024 - var7 * 4];
            int var11 = FastMath.trigTable_1024[(1023 & 1024 - var7 * 4) + 1024];
            int var12 = mX * var11 + var10 * mZ >> 18;
            mZ = mZ * var11 - mX * var10 >> 18;
            this.getSurface().drawMinimapSprite(this.getSurface().minimapSprite, 36 - (-(var5 / 2) - mZ),
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
                this.drawMinimapEntity(0xFF0000, var3 - (-(var4 / 2) - var12), (byte) -53, var5 / 2 + 36 - mZ);
            }

            ORSCharacter var14;
            for (var13 = 0; this.npcCount > var13; ++var13) {
                var14 = this.npcs[var13];
                mZ = var6 * (var14.currentZ - this.localPlayer.currentZ) * 3 / 2048;
                mX = (var14.currentX - this.localPlayer.currentX) * var6 * 3 / 2048;
                var12 = mZ * var10 + mX * var11 >> 18;
                mZ = var11 * mZ - mX * var10 >> 18;
                this.drawMinimapEntity(0xFFFF00, var4 / 2 + var3 + var12, (byte) -93, var5 / 2 - mZ + 36);
            }

            for (var13 = 0; this.playerCount > var13; ++var13) {
                var14 = this.players[var13];
                mX = (var14.currentX - this.localPlayer.currentX) * var6 * 3 / 2048;
                mZ = var6 * (var14.currentZ - this.localPlayer.currentZ) * 3 / 2048;
                var12 = mX * var11 + var10 * mZ >> 18;
                mZ = var11 * mZ - mX * var10 >> 18;
                int var15 = 0xFFFFFF;
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

            this.getSurface().drawCircle(var3 + var4 / 2, var5 / 2 + 36, 2, 0xFFFFFF, 255, -1057205208);
            this.getSurface().drawMinimapSprite(spriteSelect(EntityHandler.GUIparts.get(GUIPARTS.COMPASS.id())), 55, var3 + 19, 842218000, 128,
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
                        this.walkToActionSource(this.playerLocalX, this.playerLocalZ, mX / 128, (mZ / 128),
                                false);
                    }
                    this.mouseButtonClick = 0;
                }

            }
        } catch (RuntimeException var18) {
            throw GenUtil.makeThrowable(var18, "client.HD(" + var1 + ',' + var2 + ')');
        }
    }

    // wrench settings menu
    private void drawUiTabOptions(int var1, boolean mustTrackMouse) {
        try {
            int var3 = this.getSurface().width2 - 199;
            this.getSurface().drawSprite(spriteSelect(EntityHandler.GUIparts.get(GUIPARTS.SETTINGSTAB.id())), var3 - 49, 3);
            byte var4 = 36 + 25;
            short var5 = 196;

            int chosenColor = GenUtil.buildColor(220, 220, 220);
            int unchosenColor = GenUtil.buildColor(160, 160, 160);

            // draw menu boxes
            // android settings box & tabs
            if (isAndroid()) {
                this.drawAndroidSettingsBox(var3, var4, var5, unchosenColor, chosenColor);

                // desktop settings box & tabs
            } else {

                // authentic settings GUI
                if (this.authenticSettings) {
                    var4 = 36;
                    this.getSurface().drawBoxAlpha(var3, 36, var5, 65, GenUtil.buildColor(181, 181, 181), 160);
                    this.getSurface().drawBoxAlpha(var3, 101, var5, 65, GenUtil.buildColor(201, 201, 201), 160);
                    this.getSurface().drawBoxAlpha(var3, 166, var5, 95, GenUtil.buildColor(181, 181, 181), 160);
                    this.getSurface().drawBoxAlpha(var3, 261, var5, (this.insideTutorial || this.insideBlackHole) ? 55 : 40, GenUtil.buildColor(201, 201, 201), 160);
                }

                // custom settings GUI
                else {
                    this.drawCustomSettingsBox(var3, var4, var5, chosenColor, unchosenColor);
                }
            }

            int var6 = 3 + var3;
            int var7 = var4 + 15;

            // adds options to settings tabs
            if (this.authenticSettings)
                this.drawAuthenticSettingsOptions(var3, var4, var5, var6, var7, chosenColor, unchosenColor);
            else {
                // social settings definitions
                if (this.settingTab == 0) {
                    this.drawSocialSettingsOptions(var3, var5, var6, var7);
                }

                // game settings definitions
                if (this.settingTab == 1) {
                    this.drawGeneralSettingsOptions(var3, var5, var6, var7);
                }

                // android settings definitions
                if (this.settingTab == 2) {
                    this.drawAndroidSettingsOptions(var3, var5, var6, var7);
                }
            }

            // mouse tracking for option buttons
            if (mustTrackMouse) {
                var3 = 199 - this.getSurface().width2 + this.mouseX; // relative X
                int var13 = this.mouseY - 36; // relative Y
                // within panel
                if (var3 >= 0 && var13 >= 0 && var3 < 196 && var13 < 295) {
                    // tab switching
                    if (!this.authenticSettings) {
                        this.panelSettings.handleMouse(this.getMouseX(), this.getMouseY(), this.getMouseButtonDown(), this.getLastMouseDown());
                        if (isAndroid() && var13 <= 24 && this.mouseButtonClick == 1) {
                            if (var3 < 66 && (this.settingTab == 1 || this.settingTab == 2)) {
                                this.settingTab = 0; // Social Settings Tab
                                this.panelSettings.resetList(this.controlSettingPanel);
                            } else if (var3 >= 66 && var3 <= 131
                                    && (this.settingTab == 0 || this.settingTab == 2)) {
                                this.settingTab = 1; // General Settings Tab
                                this.panelSettings.resetList(this.controlSettingPanel);
                            } else if (var3 > 131 && (this.settingTab == 0 || this.settingTab == 1)) {
                                this.settingTab = 2; // Android Settings Tab
                                this.panelSettings.resetList(this.controlSettingPanel);
                            }
                        } else if (!isAndroid()) {
                            if (var13 <= 24 && this.mouseButtonClick == 1) {
                                if (var3 < 98 && this.settingTab == 1) {
                                    this.settingTab = 0; // Social Settings Tab
                                } else if (var3 >= 98 && this.settingTab == 0) {
                                    this.settingTab = 1; // General Settings Tab
                                }
                                this.panelSettings.resetList(this.controlSettingPanel);
                            }
                        }
                    }

                    int var9 = this.getSurface().width2 - 199;
                    var6 = var9 + 3;
                    byte var10 = 36;
                    var5 = 184;

                    if (!this.authenticSettings) {
                        var7 = 30 + var10;

                        /* general tab option clicks */
                        if (this.settingTab == 1) {
                            this.handleGeneralSettingsClicks(var5, var6, var7);
                        }

                        /* social tab option clicks */
                        if (this.settingTab == 0) {
                            this.handleSocialSettingsClicks(var5, var6, var7);
                        }

                        /* android tab option clicks */
                        if (this.settingTab == 2) {
                            this.handleAndroidSettingsClicks(var5, var6, var7);
                        }
                    } else {
                        var7 = var10 + 15;
                        this.handleAuthenticSettingsClicks(var5, var6, var7);
                    }

                    this.mouseButtonClick = 0;
                }
            }
        } catch (RuntimeException var12) {
            throw GenUtil.makeThrowable(var12, "client.BC(" + var1 + ',' + mustTrackMouse + ')');
        }
    }

    // custom settings menu with android tab
    private void drawAndroidSettingsBox(int var3, byte var4, short var5, int unchosenColor, int chosenColor) {
        if (this.settingTab == 0) {
            this.getSurface().drawBoxAlpha(var3, 36, var5, 25, GenUtil.buildColor(181, 181, 181), 160);
            this.getSurface().drawBoxAlpha(var3, 61, var5, 105, GenUtil.buildColor(201, 201, 201), 160);
            this.getSurface().drawBoxAlpha(var3, 166, var5, 95, GenUtil.buildColor(181, 181, 181), 160);
            this.getSurface().drawBoxAlpha(var3, 261, var5, (this.insideTutorial || this.insideBlackHole) ? 55 : 40, GenUtil.buildColor(201, 201, 201), 160);
        } else if (this.settingTab == 1) {
            this.getSurface().drawBoxAlpha(var3, 36, var5, 25, GenUtil.buildColor(181, 181, 181), 160);
            this.getSurface().drawBoxAlpha(var3, 61, var5, 105, GenUtil.buildColor(201, 201, 201), 160);
            this.getSurface().drawBoxAlpha(var3, 166, var5, 95, GenUtil.buildColor(181, 181, 181), 160);
            this.getSurface().drawBoxAlpha(var3, 261, var5, (this.insideTutorial || this.insideBlackHole) ? 55 : 40, GenUtil.buildColor(201, 201, 201), 160);
        } else if (this.settingTab == 2) {
            this.getSurface().drawBoxAlpha(var3, 36, var5, 25, GenUtil.buildColor(181, 181, 181), 160);
            this.getSurface().drawBoxAlpha(var3, 61, var5, 105, GenUtil.buildColor(201, 201, 201), 160);
            this.getSurface().drawBoxAlpha(var3, 166, var5, 95, GenUtil.buildColor(181, 181, 181), 160);
            this.getSurface().drawBoxAlpha(var3, 261, var5, (this.insideTutorial || this.insideBlackHole) ? 55 : 40, GenUtil.buildColor(201, 201, 201), 160);
        }

        this.getSurface().drawLineHoriz(var3, 24 + var4 - 25, var5, 0);
        this.getSurface().drawLineVert(var3 + var5 / 3, 0 + var4 - 25, 0, 24);
        this.getSurface().drawLineVert(var3 + 2 * (var5 / 3) + 1, 0 + var4 - 25, 0, 24);

        this.getSurface().drawColoredStringCentered(var5 / 4 + var3 - 16, "Social", 0, 0, 4, 16 + var4 - 25);
        this.getSurface().drawColoredStringCentered(var3 + var5 / 4 + var5 / 3 - 16, "General", 0, 0, 4, 16 + var4 - 25);
        this.getSurface().drawColoredStringCentered(var3 + var5 / 4 + 2 * var5 / 3 - 15, "Android", 0, 0, 4, 16 + var4 - 25);
    }

    // custom settings menu
    private void drawCustomSettingsBox(int var3, byte var4, short var5, int chosenColor, int unchosenColor) {
        if (this.settingTab == 0) {
            this.getSurface().drawBoxAlpha(var3, var4 - 25, var5 / 2, 24, chosenColor, 128);
            this.getSurface().drawBoxAlpha(var5 / 2 + var3, var4 - 25, var5 / 2, 24, unchosenColor, 128);
        } else if (this.settingTab == 1) {
            this.getSurface().drawBoxAlpha(var3, var4 - 25, var5 / 2, 24, unchosenColor, 128);
            this.getSurface().drawBoxAlpha(var5 / 2 + var3, var4 - 25, var5 / 2, 24, chosenColor, 128);
        }

        this.getSurface().drawLineHoriz(var3, 24 + var4 - 25, var5, 0);
        this.getSurface().drawLineVert(var3 + var5 / 2, 0 + var4 - 25, 0, 24);

        this.getSurface().drawColoredStringCentered(var5 / 4 + var3, "Social", 0, 0, 4, 16 + var4 - 25);
        this.getSurface().drawColoredStringCentered(var3 + var5 / 4 + var5 / 2, "General", 0, 0, 4, 16 + var4 - 25);

        this.getSurface().drawBoxAlpha(var3, 36 + 25, var5, 200, GenUtil.buildColor(181, 181, 181), 160);
        this.getSurface().drawBoxAlpha(var3, 261, var5, 40, GenUtil.buildColor(201, 201, 201), 160);
    }

    // custom social settings tab
    private void drawSocialSettingsOptions(int baseX, short boxWidth, int x, int y) {
        // security settings text
        y += 5;
        this.getSurface().drawString("Security settings", 3 + baseX, y, 0, 1);

        // change password
        y += 15;
        int securityColor = 0xFFFFFF;
        if (this.mouseX > x && this.mouseX < x + boxWidth && this.mouseY > y - 12
                && this.mouseY < y + 4) {
            securityColor = 0xFFFF00;
        }
        this.getSurface().drawString("Change password", 3 + baseX, y, securityColor, 1);

        // change recovery questions
        y += 15;
        securityColor = 0xFFFFFF;
        if (this.mouseX > x && this.mouseX < x + boxWidth && this.mouseY > y - 12
                && this.mouseY < y + 4) {
            securityColor = 0xFFFF00;
        }
        this.getSurface().drawString("Change recovery questions", 3 + baseX, y, securityColor, 1);

        // change contact details
        y += 15;
        securityColor = 0xFFFFFF;
        if (this.mouseX > x && this.mouseX < x + boxWidth && this.mouseY > y - 12 && this.mouseY < y + 4) {
            securityColor = 0xFFFF00;
        }
        this.getSurface().drawString("Change contact details", 3 + baseX, y, securityColor, 1);

        // privacy settings text
        y += 20;
        this.getSurface().drawString("Privacy settings", 3 + baseX, y, 0, 1);

        // block chat
        y += 15;
        if (this.settingsBlockChat != 0) {
            this.getSurface().drawString("Block chat messages: @gre@<on>", 3 + baseX, y, 0xFFFFFF, 1);
        } else {
            this.getSurface().drawString("Block chat messages: @red@<off>", 3 + baseX, y, 0xFFFFFF, 1);
        }

        // block private
        y += 15;
        if (this.settingsBlockPrivate == 0) {
            this.getSurface().drawString("Block private messages: @red@<off>", 3 + baseX, y, 0xFFFFFF, 1);
        } else {
            this.getSurface().drawString("Block private messages: @gre@<on>", baseX + 3, y, 0xFFFFFF, 1);
        }

        // if global chat enabled, block global
        y += 15;
        if (S_WANT_GLOBAL_CHAT) {
            if (this.settingsBlockGlobal == 1) {
                this.getSurface().drawString("Block global messages: @red@None", 3 + baseX, y, 0xFFFFFF, 1);
            } else if (this.settingsBlockGlobal == 2) {
                this.getSurface().drawString("Block global messages: @gre@All", baseX + 3, y, 0xFFFFFF, 1);
            } else if (this.settingsBlockGlobal == 3) {
                this.getSurface().drawString("Block global messages: @or1@Pking", baseX + 3, y, 0xFFFFFF, 1);
            } else if (this.settingsBlockGlobal == 4) {
                this.getSurface().drawString("Block global messages: @gr1@General", baseX + 3, y, 0xFFFFFF, 1);
            }
            y += 15;
        }

        // block trade
        if (this.settingsBlockTrade != 0) {
            this.getSurface().drawString("Block trade requests: @gre@<on>", baseX + 3, y, 0xFFFFFF, 1);
        } else {
            this.getSurface().drawString("Block trade requests: @red@<off>", 3 + baseX, y, 0xFFFFFF, 1);
        }

        // block duel
        y += 15;
        if (this.settingsBlockDuel != 0) {
            this.getSurface().drawString("Block duel requests: @gre@<on>", baseX + 3, y, 0xFFFFFF, 1);
        } else {
            this.getSurface().drawString("Block duel requests: @red@<off>", 3 + baseX, y, 0xFFFFFF, 1);
        }

        //Display Online List
        if (!this.insideTutorial) {
            y += 25;
            int textColor = 0xFFFFFF;
            if (this.mouseX > x && this.mouseX < x + boxWidth && this.mouseY > y - 12
                    && this.mouseY < y + 4) {
                textColor = 0xFFFF00;
            }
            this.getSurface().drawString("Display online list", (baseX + 3), y, textColor, 1);
        }

        // skip tutorial or exit the black hole menu option
        int logoutColor;
        if (this.insideTutorial) {
            y = 256;
            logoutColor = 0xFFFFFF;
            if (x < this.mouseX && this.mouseX < x + boxWidth && y - 12 < this.mouseY
                    && this.mouseY < 4 + y) {
                logoutColor = 0xFFFF00;
            }
            this.getSurface().drawString("Skip the tutorial", x, y, logoutColor, 1);
        } else if (this.insideBlackHole) {
            y = 256;
            logoutColor = 0xFFFFFF;
            if (x < this.mouseX && this.mouseX < x + boxWidth && y - 12 < this.mouseY
                    && this.mouseY < 4 + y) {
                logoutColor = 0xFFFF00;
            }
            this.getSurface().drawString("Exit the black hole", x, y, logoutColor, 1);
        }

        // logout text
        y = 275;
        this.getSurface().drawString("Always logout when you finish", x, y, 0, 1);
        logoutColor = 0xFFFFFF;

        // logout menu option
        y += 15;
        if (x < this.mouseX && x + boxWidth > this.mouseX && y - 12 < this.mouseY && this.mouseY < 4 + y) {
            logoutColor = 0xFFFF00;
        }
        this.getSurface().drawString("Click here to logout", baseX + 3, y, logoutColor, 1);
    }

    // custom general menu tab
    private void drawGeneralSettingsOptions(int baseX, short boxWidth, int x, int y) {
        this.panelSettings.clearList(this.controlSettingPanel);
        int index = 0;
        this.getSurface().drawString("Game options", 3 + baseX, y, 0, 1);

        // camera angle mode - byte index 1
        if (this.optionCameraModeAuto) {
            this.panelSettings.setListEntry(this.controlSettingPanel, index++,
                    "@whi@Camera angle mode - @gre@Auto", 0, null, null);
        } else {
            this.panelSettings.setListEntry(this.controlSettingPanel, index++,
                    "@whi@Camera angle mode - @red@Manual", 0, null, null);
        }

        // mouse button(s) - byte index 2
        if (this.optionMouseButtonOne) {
            this.panelSettings.setListEntry(this.controlSettingPanel, index++,
                    "@whi@Mouse buttons - @red@One", 1, null, null);
        } else {
            this.panelSettings.setListEntry(this.controlSettingPanel, index++,
                    "@whi@Mouse buttons - @gre@Two", 1, null, null);
        }

        // sound effects
        if (this.optionSoundDisabled) {
            this.panelSettings.setListEntry(this.controlSettingPanel, index++,
                    "@whi@Sound effects - @red@off", 2, null, null);
        } else {
            this.panelSettings.setListEntry(this.controlSettingPanel, index++,
                    "@whi@Sound effects - @gre@on", 2, null, null);
        }

        // batch progress bar
        if (S_BATCH_PROGRESSION) {
            if (!C_BATCH_PROGRESS_BAR) {
                this.panelSettings.setListEntry(this.controlSettingPanel, index++,
                        "@whi@Batch Progress Bar - @red@Off", 24, null, null);
            } else {
                this.panelSettings.setListEntry(this.controlSettingPanel, index++,
                        "@whi@Batch Progress Bar - @gre@On", 24, null, null);
            }
        }

        // experience drops
        if (S_EXPERIENCE_DROPS_TOGGLE) {
            if (!C_EXPERIENCE_DROPS) {
                this.panelSettings.setListEntry(this.controlSettingPanel, index++,
                        "@whi@Experience Drops - @red@Off", 25, null, null);
            } else {
                this.panelSettings.setListEntry(this.controlSettingPanel, index++,
                        "@whi@Experience Drops - @gre@On", 25, null, null);
            }
        }

        // show roof
        if (S_SHOW_ROOF_TOGGLE) {
            if (!C_HIDE_ROOFS) {
                this.panelSettings.setListEntry(this.controlSettingPanel, index++,
                        "@whi@Hide Roofs - @red@Off", 26, null, null);
            } else {
                this.panelSettings.setListEntry(this.controlSettingPanel, index++,
                        "@whi@Hide Roofs - @gre@On", 26, null, null);
            }
        }

        // fog toggle
        if (S_FOG_TOGGLE) {
            if (!C_HIDE_FOG) {
                this.panelSettings.setListEntry(this.controlSettingPanel, index++,
                        "@whi@Fog - @red@Off", 27, null, null);
            } else {
                this.panelSettings.setListEntry(this.controlSettingPanel, index++,
                        "@whi@Fog - @gre@On", 27, null, null);
            }
        }

        // ground items
        if (S_GROUND_ITEM_TOGGLE) {
            this.panelSettings.setListEntry(this.controlSettingPanel, index++,
                    "@whi@Ground Items - " + (C_SHOW_GROUND_ITEMS == 0 ? "@gre@Show ALL"
                            : C_SHOW_GROUND_ITEMS == 1 ? "@red@Hide ALL"
                            : C_SHOW_GROUND_ITEMS == 2 ? "@gr1@Only Bones" : "@ora@No Bones"), 8, null, null);
        }

        // auto message switch
        if (S_AUTO_MESSAGE_SWITCH_TOGGLE) {
            if (!C_MESSAGE_TAB_SWITCH) {
                this.panelSettings.setListEntry(this.controlSettingPanel, index++,
                        "@whi@Auto Message Switch - @red@Off", 9, null, null);
            } else {
                this.panelSettings.setListEntry(this.controlSettingPanel, index++,
                        "@whi@Auto Message Switch - @gre@On", 9, null, null);
            }
        }

        // side menu
        if (S_SIDE_MENU_TOGGLE) {
            if (!C_SIDE_MENU_OVERLAY) {
                this.panelSettings.setListEntry(this.controlSettingPanel, index++,
                        "@whi@Side Menu - @red@Off", 10, null, null);
            } else {
                this.panelSettings.setListEntry(this.controlSettingPanel, index++,
                        "@whi@Side Menu - @gre@On", 10, null, null);
            }
        }

        // kill feed
        if (S_WANT_KILL_FEED) {
            if (!C_KILL_FEED) {
                this.panelSettings.setListEntry(this.controlSettingPanel, index++,
                        "@whi@Kill Feed - @red@Off", 11, null, null);
            } else {
                this.panelSettings.setListEntry(this.controlSettingPanel, index++,
                        "@whi@Kill Feed - @gre@On", 11, null, null);
            }
        }

        // combat style
        if (S_MENU_COMBAT_STYLE_TOGGLE)
            this.panelSettings.setListEntry(this.controlSettingPanel, index++, "@whi@Combat Style - " + (this.combatStyle == 0 ? "@yel@Controlled" : this.combatStyle == 1 ? "@red@Aggressive" : this.combatStyle == 2 ? "@ora@Accurate" : "@gre@Defensive"), 12, null, null);

        // fight mode selector
        if (S_FIGHTMODE_SELECTOR_TOGGLE)
            this.panelSettings.setListEntry(this.controlSettingPanel, index++,
                    "@whi@Fightmode Selector - " + (C_FIGHT_MENU == 0 ? "@red@Never"
                            : C_FIGHT_MENU == 1 ? "@yel@In Combat" : "@gre@Always"), 13, null, null);

        // experience counter
        if (S_EXPERIENCE_COUNTER_TOGGLE)
            this.panelSettings.setListEntry(this.controlSettingPanel, index++,
                    "@whi@Experience Counter - " + (C_EXPERIENCE_COUNTER == 0 ? "@red@Never"
                            : C_EXPERIENCE_COUNTER == 1 ? "@yel@Recent" : "@gre@Always"), 14, null, null);

        // inventory count
        if (S_INVENTORY_COUNT_TOGGLE) {
            if (!C_INV_COUNT) {
                this.panelSettings.setListEntry(this.controlSettingPanel, index++,
                        "@whi@Inventory Count - @red@Off", 15, null, null);
            } else {
                this.panelSettings.setListEntry(this.controlSettingPanel, index++,
                        "@whi@Inventory Count - @gre@On", 15, null, null);
            }
        }

        // if clans are enabled
        if (S_WANT_CLANS) {
            // if floating name tags are enabled
            if (S_SHOW_FLOATING_NAMETAGS) {
                // name and clan tag overlay
                if (!C_NAME_CLAN_TAG_OVERLAY) {
                    this.panelSettings.setListEntry(this.controlSettingPanel, index++,
                            "@whi@Name and Clan Tag - @red@Off", 16, null, null);
                } else {
                    this.panelSettings.setListEntry(this.controlSettingPanel, index++,
                            "@whi@Name and Clan Tag - @gre@On", 16, null, null);
                }
            }

            // clan invite blocking
            if (!this.clanInviteBlockSetting) {
                this.panelSettings.setListEntry(this.controlSettingPanel, index++,
                        "@whi@Clan Invitation - @red@Block", 17, null, null);
            } else {
                this.panelSettings.setListEntry(this.controlSettingPanel, index++,
                        "@whi@Clan Invitation - @gre@Receive", 17, null, null);
            }

            // report abuse
            this.panelSettings.setListEntry(this.controlSettingPanel, index,
                    "@whi@Report Abuse", 18, null, null);
        }

        // items on death menu option OR logout text if not enabled
        y = 275;
        if (S_ITEMS_ON_DEATH_MENU) {
            int onDeathColor = 0xFFFFFF;
            if (x < this.mouseX && x + boxWidth > this.mouseX && y - 12 < this.mouseY && this.mouseY < 4 + y) {
                onDeathColor = 0xFFFF00;
            }
            this.getSurface().drawString("Items on death", baseX + 3, y, onDeathColor, 1);
        } else
            this.getSurface().drawString("Always logout when you finish", x, y, 0, 1);

        // logout menu option
        y += 15;
        int logoutColor = 0xFFFFFF;
        if (x < this.mouseX && x + boxWidth > this.mouseX && y - 12 < this.mouseY && this.mouseY < 4 + y) {
            logoutColor = 0xFFFF00;
        }
        this.getSurface().drawString("Click here to logout", baseX + 3, y, logoutColor, 1);
        this.panelSettings.drawPanel();

    }

    // android menu tab
    private void drawAndroidSettingsOptions(int baseX, short boxWidth, int x, int y) {
        this.panelSettings.clearList(this.controlSettingPanel);
        int index = 0;
        this.getSurface().drawString("Android options", 3 + baseX, y, 0, 1);

        // Color changing long press timer option
        // -> red
        if (C_LONG_PRESS_TIMER < 3) { // 1-2
            this.panelSettings.setListEntry(this.controlSettingPanel, index++,
                    "@whi@Hold-time for Menu - @red@" + (C_LONG_PRESS_TIMER), 0, null, null);
        }
        // -> light red
        if (C_LONG_PRESS_TIMER > 2 && C_LONG_PRESS_TIMER < 5) { // 3-4
            this.panelSettings.setListEntry(this.controlSettingPanel, index++,
                    "@whi@Hold-time for Menu - @lre@" + (C_LONG_PRESS_TIMER), 0, null, null);
        }
        // -> green
        if (C_LONG_PRESS_TIMER > 4 && C_LONG_PRESS_TIMER < 9) { // 5-8
            this.panelSettings.setListEntry(this.controlSettingPanel, index++,
                    "@whi@Hold-time for Menu - @gre@" + (C_LONG_PRESS_TIMER), 0, null, null);
        }
        // -> light red
        if (C_LONG_PRESS_TIMER > 8 && C_LONG_PRESS_TIMER < 11) { // 9-10
            this.panelSettings.setListEntry(this.controlSettingPanel, index++,
                    "@whi@Hold-time for Menu - @lre@" + (C_LONG_PRESS_TIMER), 0, null, null);
        }
        // -> red
        if (C_LONG_PRESS_TIMER > 10) { // 11
            this.panelSettings.setListEntry(this.controlSettingPanel, index++,
                    "@whi@Hold-time for Menu - @red@" + (C_LONG_PRESS_TIMER), 0, null, null);
        }

        // Color changing font size toggle
        // -> light red
        if (C_MENU_SIZE < 3) { // 1-2
            this.panelSettings.setListEntry(this.controlSettingPanel, index++,
                    "@whi@Font Size - @lre@" + (C_MENU_SIZE), 1, null, null);
        }
        // -> green
        if (C_MENU_SIZE > 2 && C_MENU_SIZE < 5) { // 3-4
            this.panelSettings.setListEntry(this.controlSettingPanel, index++,
                    "@whi@Font Size - @gre@" + (C_MENU_SIZE), 1, null, null);
        }
        // -> light red
        if (C_MENU_SIZE > 4 && C_MENU_SIZE < 7) { // 5-6
            this.panelSettings.setListEntry(this.controlSettingPanel, index++,
                    "@whi@Font Size - @lre@" + (C_MENU_SIZE), 1, null, null);
        }

        // menu size
        if (C_MENU_SIZE == 7) { // 7
            this.panelSettings.setListEntry(this.controlSettingPanel, index++,
                    "@whi@Font Size - @red@" + (C_MENU_SIZE), 1, null, null);
        }

        // hold and choose
        if (!C_HOLD_AND_CHOOSE) {
            this.panelSettings.setListEntry(this.controlSettingPanel, index++,
                    "@whi@Hold and Choose - @red@Off", 2, null, null);
        } else {
            this.panelSettings.setListEntry(this.controlSettingPanel, index++,
                    "@whi@Hold and Choose - @gre@On", 2, null, null);
        }

        // swipe to scroll
        if (!C_SWIPE_TO_SCROLL) {
            this.panelSettings.setListEntry(this.controlSettingPanel, index++,
                    "@whi@Swipe to Scroll - @red@Off", 3, null, null);
        } else {
            this.panelSettings.setListEntry(this.controlSettingPanel, index++,
                    "@whi@Swipe to Scroll - @gre@On", 3, null, null);
        }

        // swipe to zoom
        if (!C_SWIPE_TO_ZOOM) {
            this.panelSettings.setListEntry(this.controlSettingPanel, index++,
                    "@whi@Swipe to Zoom - @red@Off", 4, null, null);
        } else {
            this.panelSettings.setListEntry(this.controlSettingPanel, index++,
                    "@whi@Swipe to Zoom - @gre@On", 4, null, null);
        }

        // swipe to rotate
        if (!C_SWIPE_TO_ROTATE) {
            this.panelSettings.setListEntry(this.controlSettingPanel, index++,
                    "@whi@Swipe to Rotate - @red@Off", 5, null, null);
        } else {
            this.panelSettings.setListEntry(this.controlSettingPanel, index++,
                    "@whi@Swipe to Rotate - @gre@On", 5, null, null);
        }

        // volume to rotate
        if (!C_VOLUME_TO_ROTATE) {
            this.panelSettings.setListEntry(this.controlSettingPanel, index++,
                    "@whi@Volume buttons to Rotate - @red@Off", 6, null, null);
        } else {
            this.panelSettings.setListEntry(this.controlSettingPanel, index++,
                    "@whi@Volume buttons to Rotate - @gre@On", 6, null, null);
        }

        // logout text
        y += 199;
        this.getSurface().drawString("Always logout when you finish", x, y, 0, 1);
        int logoutColor = 0xFFFFFF;

        // logout menu option
        y += 15;
        if (x < this.mouseX && x + boxWidth > this.mouseX && y - 12 < this.mouseY && this.mouseY < 4 + y) {
            logoutColor = 0xFFFF00;
        }
        this.getSurface().drawString("Click here to logout", baseX + 3, y, logoutColor, 1);
        this.panelSettings.drawPanel();
    }

    // custom general menu tab
    private void handleGeneralSettingsClicks(short var5, int var6, int yFromTopDistance) {
        int settingIndex;
        int checkPosition = this.panelSettings.getControlSelectedListIndex(this.controlSettingPanel);
        if (checkPosition >= 0)
            settingIndex = this.panelSettings.getControlSelectedListInt(this.controlSettingPanel, checkPosition);
        else
            settingIndex = checkPosition;

        // camera mode - byte index 0
        if (settingIndex == 0 && this.mouseButtonClick == 1) {
            this.optionCameraModeAuto = !this.optionCameraModeAuto;
            this.packetHandler.getClientStream().newPacket(111);
            this.packetHandler.getClientStream().writeBuffer1.putByte(0);
            this.packetHandler.getClientStream().writeBuffer1.putByte(this.optionCameraModeAuto ? 1 : 0);
            this.packetHandler.getClientStream().finishPacket();
        }

        // one or two mouse button(s) - byte index 1
        if (settingIndex == 1 && this.mouseButtonClick == 1) {
            this.optionMouseButtonOne = !this.optionMouseButtonOne;
            this.packetHandler.getClientStream().newPacket(111);
            this.packetHandler.getClientStream().writeBuffer1.putByte(1);
            this.packetHandler.getClientStream().writeBuffer1.putByte(this.optionMouseButtonOne ? 1 : 0);
            this.packetHandler.getClientStream().finishPacket();
        }

        // sound on/off - byte index 2
        if (settingIndex == 2 && this.mouseButtonClick == 1) {
            this.optionSoundDisabled = !this.optionSoundDisabled;
            this.packetHandler.getClientStream().newPacket(111);
            this.packetHandler.getClientStream().writeBuffer1.putByte(2);
            this.packetHandler.getClientStream().writeBuffer1.putByte(this.optionSoundDisabled ? 1 : 0);
            this.packetHandler.getClientStream().finishPacket();
        }

        // hide roofs toggle - byte index 5
        if (settingIndex == 26 && this.mouseButtonClick == 1 && S_SHOW_ROOF_TOGGLE) {
            C_HIDE_ROOFS = !C_HIDE_ROOFS;
            this.packetHandler.getClientStream().newPacket(111);
            this.packetHandler.getClientStream().writeBuffer1.putByte(26);
            boolean optionHideRoofs = C_HIDE_ROOFS;
            this.packetHandler.getClientStream().writeBuffer1.putByte(optionHideRoofs ? 1 : 0);
            this.packetHandler.getClientStream().finishPacket();
        }

        // fog toggle - byte index 6
        if (settingIndex == 27 && this.mouseButtonClick == 1 && S_FOG_TOGGLE) {
            C_HIDE_FOG = !C_HIDE_FOG;
            this.packetHandler.getClientStream().newPacket(111);
            this.packetHandler.getClientStream().writeBuffer1.putByte(27);
            boolean optionHideFog = C_HIDE_FOG;
            this.packetHandler.getClientStream().writeBuffer1.putByte(optionHideFog ? 1 : 0);
            this.packetHandler.getClientStream().finishPacket();
        }

        // ground items toggle - byte index 28
        if (settingIndex == 8 && this.mouseButtonClick == 1 && S_GROUND_ITEM_TOGGLE) {
            C_SHOW_GROUND_ITEMS++;
            if (C_SHOW_GROUND_ITEMS == 4)
                C_SHOW_GROUND_ITEMS = 0;
            this.packetHandler.getClientStream().newPacket(111);
            this.packetHandler.getClientStream().writeBuffer1.putByte(28);
            this.packetHandler.getClientStream().writeBuffer1.putByte(C_SHOW_GROUND_ITEMS);
            this.packetHandler.getClientStream().finishPacket();
        }

        // auto message tab switch - byte index 29
        if (settingIndex == 9 && this.mouseButtonClick == 1 && S_AUTO_MESSAGE_SWITCH_TOGGLE) {
            C_MESSAGE_TAB_SWITCH = !C_MESSAGE_TAB_SWITCH;
            this.packetHandler.getClientStream().newPacket(111);
            this.packetHandler.getClientStream().writeBuffer1.putByte(29);
            boolean optionHideFog = C_MESSAGE_TAB_SWITCH;
            this.packetHandler.getClientStream().writeBuffer1.putByte(optionHideFog ? 1 : 0);
            this.packetHandler.getClientStream().finishPacket();
        }

        // side menu - byte index 30
        if (settingIndex == 10 && this.mouseButtonClick == 1 && S_SIDE_MENU_TOGGLE) {
            C_SIDE_MENU_OVERLAY = !C_SIDE_MENU_OVERLAY;
            this.packetHandler.getClientStream().newPacket(111);
            this.packetHandler.getClientStream().writeBuffer1.putByte(30);
            boolean optionHideFog = C_SIDE_MENU_OVERLAY;
            this.packetHandler.getClientStream().writeBuffer1.putByte(optionHideFog ? 1 : 0);
            this.packetHandler.getClientStream().finishPacket();
        }

        // kill feed - byte index 31
        if (settingIndex == 11 && this.mouseButtonClick == 1 && S_WANT_KILL_FEED) {
            C_KILL_FEED = !C_KILL_FEED;
            this.packetHandler.getClientStream().newPacket(111);
            this.packetHandler.getClientStream().writeBuffer1.putByte(31);
            boolean optionHideFog = C_KILL_FEED;
            this.packetHandler.getClientStream().writeBuffer1.putByte(optionHideFog ? 1 : 0);
            this.packetHandler.getClientStream().finishPacket();
        }

        // combat style
        if (settingIndex == 12 && this.mouseButtonClick == 1 && S_MENU_COMBAT_STYLE_TOGGLE) {
            this.combatStyle++;
            if (this.combatStyle == 4)
                this.combatStyle = 0;
            this.packetHandler.getClientStream().newPacket(29);
            this.packetHandler.getClientStream().writeBuffer1.putByte(this.combatStyle);
            this.packetHandler.getClientStream().finishPacket();
        }

        // fight mode selector - byte index 32
        if (settingIndex == 13 && this.mouseButtonClick == 1 && S_FIGHTMODE_SELECTOR_TOGGLE) {
            C_FIGHT_MENU++;
            if (C_FIGHT_MENU == 3)
                C_FIGHT_MENU = 0;
            this.packetHandler.getClientStream().newPacket(111);
            this.packetHandler.getClientStream().writeBuffer1.putByte(32);
            this.packetHandler.getClientStream().writeBuffer1.putByte(C_FIGHT_MENU);
            this.packetHandler.getClientStream().finishPacket();
        }

        // experience counter - byte index 33
        if (settingIndex == 14 && this.mouseButtonClick == 1 && S_EXPERIENCE_COUNTER_TOGGLE) {
            C_EXPERIENCE_COUNTER++;
            if (C_EXPERIENCE_COUNTER == 3)
                C_EXPERIENCE_COUNTER = 0;
            this.packetHandler.getClientStream().newPacket(111);
            this.packetHandler.getClientStream().writeBuffer1.putByte(33);
            this.packetHandler.getClientStream().writeBuffer1.putByte(C_EXPERIENCE_COUNTER);
            this.packetHandler.getClientStream().finishPacket();
        }

        // inventory count - byte index 34
        if (settingIndex == 15 && this.mouseButtonClick == 1 && S_INVENTORY_COUNT_TOGGLE) {
            C_INV_COUNT = !C_INV_COUNT;
            this.packetHandler.getClientStream().newPacket(111);
            this.packetHandler.getClientStream().writeBuffer1.putByte(34);
            boolean optionHideFog = C_INV_COUNT;
            this.packetHandler.getClientStream().writeBuffer1.putByte(optionHideFog ? 1 : 0);
            this.packetHandler.getClientStream().finishPacket();
        }

        // if clans are enabled
        if (S_WANT_CLANS) {
            // floating clan name tag overlay - byte index 35
            if (S_SHOW_FLOATING_NAMETAGS) {
                if (settingIndex == 16 && this.mouseButtonClick == 1) {
                    C_NAME_CLAN_TAG_OVERLAY = !C_NAME_CLAN_TAG_OVERLAY;
                    this.packetHandler.getClientStream().newPacket(111);
                    this.packetHandler.getClientStream().writeBuffer1.putByte(35);
                    boolean optionHideFog = C_NAME_CLAN_TAG_OVERLAY;
                    this.packetHandler.getClientStream().writeBuffer1.putByte(optionHideFog ? 1 : 0);
                    this.packetHandler.getClientStream().finishPacket();
                }
            }

            // clan invite blocking - byte index 11
            if (settingIndex == 17 && this.mouseButtonClick == 1) {
                this.clanInviteBlockSetting = !this.clanInviteBlockSetting;
                this.packetHandler.getClientStream().newPacket(111);
                this.packetHandler.getClientStream().writeBuffer1.putByte(11);
                this.packetHandler.getClientStream().writeBuffer1.putByte(this.clanInviteBlockSetting ? 0 : 1);
                this.packetHandler.getClientStream().finishPacket();
            }

            // report abuse
            if (settingIndex == 18 && this.mouseButtonClick == 1) {
                this.inputTextFinal = "";
                this.inputTextCurrent = "";
                this.reportAbuse_State = 1;
            }

            // batch progress bar - byte index 24
            if (S_BATCH_PROGRESSION) {
                if (settingIndex == 24 && this.mouseButtonClick == 1) {
                    C_BATCH_PROGRESS_BAR = !C_BATCH_PROGRESS_BAR;
                    this.packetHandler.getClientStream().newPacket(111);
                    this.packetHandler.getClientStream().writeBuffer1.putByte(24);
                    boolean optionBatchProgressBar = C_BATCH_PROGRESS_BAR;
                    this.packetHandler.getClientStream().writeBuffer1.putByte(optionBatchProgressBar ? 1 : 0);
                    this.packetHandler.getClientStream().finishPacket();
                }
            }

            // experience drops - byte index 25
            if (settingIndex == 25 && this.mouseButtonClick == 1 && S_EXPERIENCE_DROPS_TOGGLE) {
                C_EXPERIENCE_DROPS = !C_EXPERIENCE_DROPS;
                this.packetHandler.getClientStream().newPacket(111);
                this.packetHandler.getClientStream().writeBuffer1.putByte(25);
                boolean optionExperienceDrops = C_EXPERIENCE_DROPS;
                this.packetHandler.getClientStream().writeBuffer1.putByte(optionExperienceDrops ? 1 : 0);
                this.packetHandler.getClientStream().finishPacket();
            }
        }

        // items on death
        yFromTopDistance = 275;
        if (S_ITEMS_ON_DEATH_MENU) {
            if (this.mouseX > var6 && var5 + var6 > this.mouseX && this.mouseY > yFromTopDistance - 12
                    && this.mouseY < yFromTopDistance + 4 && this.mouseButtonClick == 1) {
                this.showUiTab = 0;
                lostOnDeathInterface.setVisible(true);
            }
        }

        // logout menu option
        yFromTopDistance = 290;
        if (this.mouseX > var6 && var5 + var6 > this.mouseX && this.mouseY > yFromTopDistance - 12
                && this.mouseY < yFromTopDistance + 4 && this.mouseButtonClick == 1) {
            this.sendLogout(0);
        }
    }

    // custom social menu tab
    private void handleSocialSettingsClicks(short var5, int var6, int yFromTopDistance) {
        boolean var11 = false;

        // change password
        yFromTopDistance += 2 * 15 + 5;
        if (this.mouseX > var6 && this.mouseX < var5 + var6 && this.mouseY > yFromTopDistance - 12 && this.mouseY < yFromTopDistance + 4
                && mouseButtonClick == 1) {
            this.panelPasswordChange_Mode = PasswordChangeMode.OLD_PASSWORD;
            this.inputTextCurrent = "";
            this.inputTextFinal = "";
        }

        // change recovery questions
        yFromTopDistance += 15;
        if (this.mouseX > var6 && this.mouseX < var5 + var6 && this.mouseY > yFromTopDistance - 12 && this.mouseY < yFromTopDistance + 4
                && mouseButtonClick == 1) {
            this.packetHandler.getClientStream().newPacket(200);
            this.packetHandler.getClientStream().finishPacket();
        }

        // change contact details
        yFromTopDistance += 15;
        if (this.mouseX > var6 && this.mouseX < var5 + var6 && this.mouseY > yFromTopDistance - 12 && this.mouseY < yFromTopDistance + 4
                && mouseButtonClick == 1) {
            this.packetHandler.getClientStream().newPacket(201);
            this.packetHandler.getClientStream().finishPacket();
        }

        // block chat toggle
        yFromTopDistance += 24;
        if (this.mouseX > var6 && this.mouseX < var5 + var6 && this.mouseY > yFromTopDistance - 12
                && 4 + yFromTopDistance > this.mouseY && this.mouseButtonClick == 1) {
            this.settingsBlockChat = 1 - this.settingsBlockChat;
            var11 = true;
        }

        // block private toggle
        yFromTopDistance += 15;
        if (this.mouseX > var6 && var5 + var6 > this.mouseX && this.mouseY > yFromTopDistance - 12
                && yFromTopDistance + 4 > this.mouseY && this.mouseButtonClick == 1) {
            this.settingsBlockPrivate = 1 - this.settingsBlockPrivate;
            var11 = true;
        }

        // block global chat toggle
        yFromTopDistance += 15;
        if (S_WANT_GLOBAL_CHAT) {
            if (this.mouseX > var6 && var5 + var6 > this.mouseX && this.mouseY > yFromTopDistance - 12
                    && yFromTopDistance + 4 > this.mouseY && this.mouseButtonClick == 1) {
                if (this.settingsBlockGlobal >= 4) {
                    this.settingsBlockGlobal = 0;
                }
                this.settingsBlockGlobal = this.settingsBlockGlobal + 1;
                this.packetHandler.getClientStream().newPacket(111);
                this.packetHandler.getClientStream().writeBuffer1.putByte(9);
                this.packetHandler.getClientStream().writeBuffer1.putByte(this.settingsBlockGlobal);
                this.packetHandler.getClientStream().finishPacket();
            }
            yFromTopDistance += 15;
        }

        // block trade toggle
        if (this.mouseX > var6 && this.mouseX < var6 + var5 && yFromTopDistance - 12 < this.mouseY
                && this.mouseY < 4 + yFromTopDistance && this.mouseButtonClick == 1) {
            this.settingsBlockTrade = 1 - this.settingsBlockTrade;
            var11 = true;
        }

        // block duel toggle
        yFromTopDistance += 15;
        if (this.mouseX > var6 && this.mouseX < var6 + var5
                && yFromTopDistance - 12 < this.mouseY && this.mouseY < yFromTopDistance + 4 && this.mouseButtonClick == 1) {
            var11 = true;
            this.settingsBlockDuel = 1 - this.settingsBlockDuel;
        }

        // block chat toggle
        if (var11) {
            this.createPacket64(this.settingsBlockChat, this.settingsBlockPrivate,
                    this.settingsBlockTrade, this.settingsBlockDuel);
        }

        //Handle online list click
        yFromTopDistance += 25;
        if (this.mouseX > var6 && this.mouseX < var6 + var5
                && yFromTopDistance - 12 < this.mouseY && this.mouseY < yFromTopDistance + 4 && this.mouseButtonClick == 1) {
            this.sendCommandString("onlinelist");
        }

        // skip tutorial button or exit blackhole button
        if (this.insideTutorial) {
            yFromTopDistance = 255;
            if (this.mouseX > var6 && var5 + var6 > this.mouseX && yFromTopDistance - 12 < this.mouseY
                    && this.mouseY < yFromTopDistance + 4 && this.mouseButtonClick == 1) {
                this.showItemModX(InputXPrompt.promptSkipTutorial, InputXAction.SKIP_TUTORIAL, false);
                this.showUiTab = 0;
            }
        } else if (this.insideBlackHole) {
            yFromTopDistance = 255;
            if (this.mouseX > var6 && var5 + var6 > this.mouseX && yFromTopDistance - 12 < this.mouseY
                    && this.mouseY < yFromTopDistance + 4 && this.mouseButtonClick == 1) {
                this.showItemModX(InputXPrompt.promptExitBlackHole, InputXAction.EXIT_BLACK_HOLE, false);
                this.showUiTab = 0;
            }
        }

        // logout menu option
        yFromTopDistance = 290;
        if (this.mouseX > var6 && var5 + var6 > this.mouseX && this.mouseY > yFromTopDistance - 12
                && this.mouseY < yFromTopDistance + 4 && this.mouseButtonClick == 1) {
            this.sendLogout(0);
        }
    }

    // android menu tab
    private void handleAndroidSettingsClicks(short var5, int var6, int yFromTopDistance) {

        // hold to right click delay control
        if (this.panelSettings.getControlSelectedListIndex(this.controlSettingPanel) == 0 && this.mouseButtonClick == 1) {
            C_LONG_PRESS_TIMER++;
            if (C_LONG_PRESS_TIMER == 13)
                C_LONG_PRESS_TIMER = 1;
            this.packetHandler.getClientStream().newPacket(111);
            this.packetHandler.getClientStream().writeBuffer1.putByte(19);
            this.packetHandler.getClientStream().writeBuffer1.putByte(C_LONG_PRESS_TIMER);
            this.packetHandler.getClientStream().finishPacket();
        }

        // font size control
        if (this.panelSettings.getControlSelectedListIndex(this.controlSettingPanel) == 1 && this.mouseButtonClick == 1) {
            C_MENU_SIZE++;
            if (C_MENU_SIZE == 8)
                C_MENU_SIZE = 1;
            if (isAndroid()) {
                this.menuCommon.font = C_MENU_SIZE;
            }
            this.packetHandler.getClientStream().newPacket(111);
            this.packetHandler.getClientStream().writeBuffer1.putByte(20);
            this.packetHandler.getClientStream().writeBuffer1.putByte(C_MENU_SIZE);
            this.packetHandler.getClientStream().finishPacket();
        }

        // hold to right click toggle
        if (this.panelSettings.getControlSelectedListIndex(this.controlSettingPanel) == 2 && this.mouseButtonClick == 1) {
            C_HOLD_AND_CHOOSE = !C_HOLD_AND_CHOOSE;
            this.packetHandler.getClientStream().newPacket(111);
            this.packetHandler.getClientStream().writeBuffer1.putByte(21);
            this.packetHandler.getClientStream().writeBuffer1.putByte(C_HOLD_AND_CHOOSE ? 1 : 0);
            this.packetHandler.getClientStream().finishPacket();
        }

        // swipe scroll control
        if (this.panelSettings.getControlSelectedListIndex(this.controlSettingPanel) == 3 && this.mouseButtonClick == 1) {
            C_SWIPE_TO_SCROLL = !C_SWIPE_TO_SCROLL;
            this.packetHandler.getClientStream().newPacket(111);
            this.packetHandler.getClientStream().writeBuffer1.putByte(18);
            this.packetHandler.getClientStream().writeBuffer1.putByte(C_SWIPE_TO_SCROLL ? 1 : 0);
            this.packetHandler.getClientStream().finishPacket();
        }

        // swipe camera zoom control
        if (this.panelSettings.getControlSelectedListIndex(this.controlSettingPanel) == 4 && this.mouseButtonClick == 1) {
            C_SWIPE_TO_ZOOM = !C_SWIPE_TO_ZOOM;
            this.packetHandler.getClientStream().newPacket(111);
            this.packetHandler.getClientStream().writeBuffer1.putByte(22);
            this.packetHandler.getClientStream().writeBuffer1.putByte(C_SWIPE_TO_ZOOM ? 1 : 0);
            this.packetHandler.getClientStream().finishPacket();
        }

        // swipe camera rotation control
        if (this.panelSettings.getControlSelectedListIndex(this.controlSettingPanel) == 5 && this.mouseButtonClick == 1) {
            C_SWIPE_TO_ROTATE = !C_SWIPE_TO_ROTATE;
            this.packetHandler.getClientStream().newPacket(111);
            this.packetHandler.getClientStream().writeBuffer1.putByte(17);
            this.packetHandler.getClientStream().writeBuffer1.putByte(C_SWIPE_TO_ROTATE ? 1 : 0);
            this.packetHandler.getClientStream().finishPacket();
        }

        // volume button camera rotation control
        if (this.panelSettings.getControlSelectedListIndex(this.controlSettingPanel) == 6 && this.mouseButtonClick == 1) {
            C_VOLUME_TO_ROTATE = !C_VOLUME_TO_ROTATE;
            this.packetHandler.getClientStream().newPacket(111);
            this.packetHandler.getClientStream().writeBuffer1.putByte(16);
            this.packetHandler.getClientStream().writeBuffer1.putByte(C_VOLUME_TO_ROTATE ? 1 : 0);
            this.packetHandler.getClientStream().finishPacket();
        }

        // logout menu option
        yFromTopDistance += 223;
        if (this.mouseX > var6 && var5 + var6 > this.mouseX && this.mouseY > yFromTopDistance - 12
                && this.mouseY < yFromTopDistance + 4 && this.mouseButtonClick == 1) {
            this.sendLogout(0);
        }
    }

    // authentic settings / social tab
    private void drawAuthenticSettingsOptions(int baseX, byte var4, short boxWidth, int x, int y, int chosenColor, int unchosenColor) {
        this.getSurface().drawString("Game options - click to toggle", 3 + baseX, y, 0, 1);

        // camera angle mode - byte index 0
        y += 15;
        if (this.optionCameraModeAuto) {
            this.getSurface().drawString("@whi@Camera angle mode - @gre@Auto", 3 + baseX, y, 0, 1);
        } else {
            this.getSurface().drawString("@whi@Camera angle mode - @red@Manual", 3 + baseX, y, 0, 1);
        }

        // mouse buttons - byte index 1
        y += 15;
        if (this.optionMouseButtonOne) {
            this.getSurface().drawString("@whi@Mouse buttons - @red@One", 3 + baseX, y, 0, 1);
        } else {
            this.getSurface().drawString("@whi@Mouse buttons - @gre@Two", 3 + baseX, y, 0, 1);
        }

        // sound effects - byte index 2
        y += 15;
        if (this.optionSoundDisabled) {
            this.getSurface().drawString("@whi@Sound effects - @red@off", 3 + baseX, y, 0, 1);
        } else {
            this.getSurface().drawString("@whi@Sound effects - @gre@on", 3 + baseX, y, 0, 1);
        }

        // security settings section
        y += 15;
        y += 5;
        this.getSurface().drawString("Security settings", 3 + baseX, y, 0, 1);

        // change password
        y += 15;
        int securityColor = 0xFFFFFF;
        if (this.mouseX > x && this.mouseX < x + boxWidth && this.mouseY > y - 12
                && this.mouseY < y + 4) {
            securityColor = 0xFFFF00;
        }
        this.getSurface().drawString("Change password", 3 + baseX, y, securityColor, 1);

        // change recovery questions
        y += 15;
        securityColor = 0xFFFFFF;
        if (this.mouseX > x && this.mouseX < x + boxWidth && this.mouseY > y - 12
                && this.mouseY < y + 4) {
            securityColor = 0xFFFF00;
        }
        this.getSurface().drawString("Change recovery questions", 3 + baseX, y, securityColor, 1);

        // change contact details
        y += 15;
        securityColor = 0xFFFFFF;
        if (this.mouseX > x && this.mouseX < x + boxWidth && this.mouseY > y - 12 && this.mouseY < y + 4) {
            securityColor = 0xFFFF00;
        }
        this.getSurface().drawString("Change contact details", 3 + baseX, y, securityColor, 1);

		/*
		// this section is left for reference only
		y += 20;
		this.getSurface().drawString("To change you contact details,", 3 + baseX, y, 0xFFFFFF, 0);
		y += 15;
		this.getSurface().drawString("password, recovery questions, etc..", 3 + baseX, y, 0xFFFFFF, 0);
		y += 15;
		this.getSurface().drawString("please contact the administrators of", 3 + baseX, y, 0xFFFFFF, 0);
		y += 15;
		this.getSurface().drawString("the server you are playing.", 3 + baseX, y, 0xFFFFFF, 0);
		*/

        // privacy setting text
        y += 20;
        this.getSurface().drawString("Privacy settings. Will be applied to", 3 + baseX, y, 0, 1);
        y += 15;
        this.getSurface().drawString("all people not on your friends list", 3 + baseX, y, 0, 1);

        // block chat toggle
        y += 15;
        if (this.settingsBlockChat != 0) {
            this.getSurface().drawString("Block chat messages: @gre@<on>", 3 + baseX, y, 0xFFFFFF, 1);
        } else {
            this.getSurface().drawString("Block chat messages: @red@<off>", 3 + baseX, y, 0xFFFFFF, 1);
        }

        // block private toggle
        y += 15;
        if (this.settingsBlockPrivate == 0) {
            this.getSurface().drawString("Block private messages: @red@<off>", 3 + baseX, y, 0xFFFFFF, 1);
        } else {
            this.getSurface().drawString("Block private messages: @gre@<on>", baseX + 3, y, 0xFFFFFF, 1);
        }

        // block trade toggle
        y += 15;
        if (this.settingsBlockTrade != 0) {
            this.getSurface().drawString("Block trade requests: @gre@<on>", baseX + 3, y, 0xFFFFFF, 1);
        } else {
            this.getSurface().drawString("Block trade requests: @red@<off>", 3 + baseX, y, 0xFFFFFF, 1);
        }

        // block duel toggle
        y += 15;
        if (this.settingsBlockDuel != 0) {
            this.getSurface().drawString("Block duel requests: @gre@<on>", baseX + 3, y, 0xFFFFFF, 1);
        } else {
            this.getSurface().drawString("Block duel requests: @red@<off>", 3 + baseX, y, 0xFFFFFF, 1);
        }

        // skip tutorial button or exit blackhole button
        int logoutColor = 0xFFFFFF;
        if (this.insideTutorial) {
            y += 20;
            if (x < this.mouseX && this.mouseX < x + boxWidth && y - 12 < this.mouseY
                    && this.mouseY < 4 + y) {
                logoutColor = 0xFFFF00;
            }
            this.getSurface().drawString("Skip the tutorial", x, y, logoutColor, 1);
        } else if (this.insideBlackHole) {
            y += 20;
            if (x < this.mouseX && this.mouseX < x + boxWidth && y - 12 < this.mouseY
                    && this.mouseY < 4 + y) {
                logoutColor = 0xFFFF00;
            }
            this.getSurface().drawString("Exit the black hole", x, y, logoutColor, 1);
        }

        // logout section text
        y += 20;
        this.getSurface().drawString("Always logout when you finish", x, y, 0, 1);

        // logout menu option
        y += 15;
        logoutColor = 0xFFFFFF;
        if (x < this.mouseX && x + boxWidth > this.mouseX && y - 12 < this.mouseY && this.mouseY < 4 + y) {
            logoutColor = 0xFFFF00;
        }
        this.getSurface().drawString("Click here to logout", baseX + 3, y, logoutColor, 1);
    }

    // authentic settings/social tab version
    private void handleAuthenticSettingsClicks(short var5, int var6, int yFromTopDistance) {

        // camera mode - byte index 0
        yFromTopDistance += 15;
        if (this.mouseX > var6 && this.mouseX < var5 + var6 && this.mouseY > yFromTopDistance - 12
                && 4 + yFromTopDistance > this.mouseY && this.mouseButtonClick == 1) {
            this.optionCameraModeAuto = !this.optionCameraModeAuto;
            this.packetHandler.getClientStream().newPacket(111);
            this.packetHandler.getClientStream().writeBuffer1.putByte(0);
            this.packetHandler.getClientStream().writeBuffer1.putByte(this.optionCameraModeAuto ? 1 : 0);
            this.packetHandler.getClientStream().finishPacket();
        }

        // one or two mouse button(s) - byte index 1
        yFromTopDistance += 15;
        if (this.mouseX > var6 && this.mouseX < var5 + var6 && this.mouseY > yFromTopDistance - 12
                && 4 + yFromTopDistance > this.mouseY && this.mouseButtonClick == 1) {
            this.optionMouseButtonOne = !this.optionMouseButtonOne;
            this.packetHandler.getClientStream().newPacket(111);
            this.packetHandler.getClientStream().writeBuffer1.putByte(1);
            this.packetHandler.getClientStream().writeBuffer1.putByte(this.optionMouseButtonOne ? 1 : 0);
            this.packetHandler.getClientStream().finishPacket();

        }

        // sound on/off - byte index 2
        yFromTopDistance += 15;
        if (this.mouseX > var6 && this.mouseX < var5 + var6 && this.mouseY > yFromTopDistance - 12
                && 4 + yFromTopDistance > this.mouseY && this.mouseButtonClick == 1) {
            this.optionSoundDisabled = !this.optionSoundDisabled;
            this.packetHandler.getClientStream().newPacket(111);
            this.packetHandler.getClientStream().writeBuffer1.putByte(2);
            this.packetHandler.getClientStream().writeBuffer1.putByte(this.optionSoundDisabled ? 1 : 0);
            this.packetHandler.getClientStream().finishPacket();
        }

        // change password
        yFromTopDistance += 2 * 15 + 5;
        if (this.mouseX > var6 && this.mouseX < var5 + var6 && this.mouseY > yFromTopDistance - 12 && this.mouseY < yFromTopDistance + 4
                && mouseButtonClick == 1) {
            this.panelPasswordChange_Mode = PasswordChangeMode.OLD_PASSWORD;
            this.inputTextCurrent = "";
            this.inputTextFinal = "";
        }

        // change recovery questions
        yFromTopDistance += 15;
        if (this.mouseX > var6 && this.mouseX < var5 + var6 && this.mouseY > yFromTopDistance - 12 && this.mouseY < yFromTopDistance + 4
                && mouseButtonClick == 1) {
            this.packetHandler.getClientStream().newPacket(200);
            this.packetHandler.getClientStream().finishPacket();
        }

        // change contact details
        yFromTopDistance += 15;
        if (this.mouseX > var6 && this.mouseX < var5 + var6 && this.mouseY > yFromTopDistance - 12 && this.mouseY < yFromTopDistance + 4
                && mouseButtonClick == 1) {
            this.packetHandler.getClientStream().newPacket(201);
            this.packetHandler.getClientStream().finishPacket();
        }

        // divider
        yFromTopDistance += 3 * 15 + 5;
        boolean var11 = false;

        // block chat toggle
        if (this.mouseX > var6 && this.mouseX < var5 + var6 && this.mouseY > yFromTopDistance - 12
                && 4 + yFromTopDistance > this.mouseY && this.mouseButtonClick == 1) {
            this.settingsBlockChat = 1 - this.settingsBlockChat;
            var11 = true;
        }

        // plock private toggle
        yFromTopDistance += 15;
        if (this.mouseX > var6 && var5 + var6 > this.mouseX && this.mouseY > yFromTopDistance - 12
                && yFromTopDistance + 4 > this.mouseY && this.mouseButtonClick == 1) {
            this.settingsBlockPrivate = 1 - this.settingsBlockPrivate;
            var11 = true;
        }

        // block trade toggle
        yFromTopDistance += 15;
        if (this.mouseX > var6 && this.mouseX < var6 + var5 && yFromTopDistance - 12 < this.mouseY
                && this.mouseY < 4 + yFromTopDistance && this.mouseButtonClick == 1) {
            this.settingsBlockTrade = 1 - this.settingsBlockTrade;
            var11 = true;
        }

        // block duel toggle
        yFromTopDistance += 15;
        if (this.mouseX > var6 && this.mouseX < var6 + var5
                && yFromTopDistance - 12 < this.mouseY && this.mouseY < yFromTopDistance + 4 && this.mouseButtonClick == 1) {
            var11 = true;
            this.settingsBlockDuel = 1 - this.settingsBlockDuel;
        }

        // adjusts the menu slightly to accommodate the skip link below
        yFromTopDistance += 20;
        if (var11) {
            this.createPacket64(this.settingsBlockChat, this.settingsBlockPrivate,
                    this.settingsBlockTrade, this.settingsBlockDuel);
        }

        // skip tutorial button or exit blackhole button
        if (this.insideTutorial) { // tutorial menu option
            if (this.mouseX > var6 && var5 + var6 > this.mouseX && yFromTopDistance - 12 < this.mouseY
                    && this.mouseY < yFromTopDistance + 4 && this.mouseButtonClick == 1) {
                this.showItemModX(InputXPrompt.promptSkipTutorial, InputXAction.SKIP_TUTORIAL, false);
                this.showUiTab = 0;
            }
            yFromTopDistance += 20;
        } else if (this.insideBlackHole) { // blackhole menu option
            if (this.mouseX > var6 && var5 + var6 > this.mouseX && yFromTopDistance - 12 < this.mouseY
                    && this.mouseY < yFromTopDistance + 4 && this.mouseButtonClick == 1) {
                this.showItemModX(InputXPrompt.promptExitBlackHole, InputXAction.EXIT_BLACK_HOLE, false);
                this.showUiTab = 0;
            }
            yFromTopDistance += 20;
        }

        // logout menu option
        yFromTopDistance += 15;
        if (this.mouseX > var6 && var5 + var6 > this.mouseX && this.mouseY > yFromTopDistance - 12
                && this.mouseY < yFromTopDistance + 4 && this.mouseButtonClick == 1) {
            this.sendLogout(0);
        }
    }

    private boolean doubleClick() {
        if (tiktok == null) {
            tiktok = new Timer();
        }
        tiktok.schedule(new TimerTask() {
            public void run() {
                flag += 1;
            }
        }, 50, 400);
        if (flag > 1) {
            if (tiktok != null) {
                tiktok.cancel();
                tiktok = null;
            }
            flag = 0;
            return false;
        } else if (flag == 1) {
            if (tiktok != null) {
                tiktok.cancel();
                tiktok = null;
            }
            flag = 0;
            return true;
        } else {
            return false;
        }
    }

    // bar chart icon tab menu
    private void drawUiTabPlayerInfo(boolean var1, int var2) {
        try {

            int x = this.surface.width2 - 199;
            byte y = 36;
            this.getSurface().drawSprite(spriteSelect(EntityHandler.GUIparts.get(GUIPARTS.SKILLSTAB.id())), x - 49, 3);
            short width = 196;
            short height;
            if (S_WANT_EXP_INFO)
                height = 275;
            else
                height = 262;
            int var8;
            int yFromTopDistance = var8 = GenUtil.buildColor(160, 160, 160);
            if (this.uiTabPlayerInfoSubTab != 0) {
                var8 = GenUtil.buildColor(220, 220, 220);
            } else {
                yFromTopDistance = GenUtil.buildColor(220, 220, 220);
            }

            this.surface.drawBoxAlpha(x, y, width / 2, 24, yFromTopDistance, 128);
            this.surface.drawBoxAlpha(x + width / 2, y, width / 2, 24, var8, 128);
            this.surface.drawBoxAlpha(x, 24 + y, width, height - 12, GenUtil.buildColor(220, 220, 220), 128);
            this.surface.drawLineHoriz(x, y + 24, width, 0);
            this.surface.drawLineVert(x + width / 2, y, 0, 24);
            this.surface.drawColoredStringCentered(x + width / 4, "Stats", 0, 0, 4, y + 16);
            this.surface.drawColoredStringCentered(x + width / 4 + width / 2, "Quests", 0, 0, 4, y + 16);
            int heightMargin;

            // stats menu tab
			if (this.uiTabPlayerInfoSubTab == 0)
			{
				heightMargin = 72;
				int yOffset = heightMargin + 13;
				int xOffset = x + 5;
				int textColour = 0xFFFFFF, textColourHovered = 0xFF0000, textColourHeading = 0xFFFF00;
				int currentlyHoveredSkill = -1;
				int currSkill = 0, totalXp = 0, i =0;
				int leftColLength = (int)Math.floor(skillCount/2);
				int rightColLength = skillCount - leftColLength;

				this.getSurface().drawString("Skills", xOffset, heightMargin, textColourHeading, 3);

				//Determine if the mouse is hovering over a skill
				if (this.mouseX >= xOffset && this.mouseX <= x + 196){
					if (this.mouseY >= heightMargin-13 && this.mouseY <= (heightMargin + 13*leftColLength) - 2) {
						int xthing = this.mouseX > (xOffset + width/2 - 10) ? 1 : 0;
						int ything = (int)Math.floor((double)(this.mouseY-heightMargin+13)/13);
						currentlyHoveredSkill = (leftColLength + 1)*xthing + ything - 1;
						if (!(currentlyHoveredSkill >= 0 && currentlyHoveredSkill <= this.getSkillNames().length-1)) {
							currentlyHoveredSkill = -1;
						} else {
							if (isAndroid() && this.mouseButtonClick == 1 && this.uiTabPlayerInfoSubTab == 0) {
								if (doubleClick() && S_WANT_SKILL_MENUS) {
									setSkillGuideChosen(this.skillNameLong[currentlyHoveredSkill]);
									skillGuideInterface.setVisible(true);
									this.showUiTab = 0;
								}
							} else if (!isAndroid() && this.mouseButtonClick == 1 && this.uiTabPlayerInfoSubTab == 0 && S_WANT_SKILL_MENUS) {
								setSkillGuideChosen(this.skillNameLong[currentlyHoveredSkill]);
								skillGuideInterface.setVisible(true);
								this.showUiTab = 0;
							}
						}
					}
				}

				for (currSkill=0; currSkill < skillCount; currSkill++){

					this.getSurface().drawString(this.getSkillNames()[currSkill] + ":@yel@" + this.playerStatCurrent[i]
						+ "/" + this.playerStatBase[i], xOffset, yOffset, currentlyHoveredSkill == i ? textColourHovered : textColour, 1);

					yOffset += 13;
					i++;

					if (i == leftColLength) {
						xOffset += width/2 - 10;
						yOffset = heightMargin;
					}
				}

				if (leftColLength != rightColLength) {
					xOffset = x + 5;
				}

				this.getSurface().drawString("Quest Points:@yel@" + this.questPoints, xOffset, yOffset, textColour, 1);

				if (Config.S_WANT_FATIGUE) {
					if (xOffset == x + 5) {
						xOffset += width/2 - 10;
					} else {
						yOffset += 13;
						xOffset = x + 5;
					}
					this.getSurface().drawString("Fatigue: @yel@" + this.statFatigue + "%", xOffset, yOffset,
						textColour, 1);
				}

				yOffset += 20;
				this.getSurface().drawString("Equipment Status", 5 + x, yOffset, textColourHeading, 3);
				yOffset += 13;

				//Draw the equipment bonuses
				for (currSkill = 0; currSkill < 3; ++currSkill) {
					this.getSurface().drawString(this.equipmentStatNames[currSkill] + ":@yel@" + this.playerStatEquipment[currSkill],
						5 + x, yOffset, textColour, 1);
					if (2 > currSkill) {
						this.getSurface().drawString(
							this.equipmentStatNames[currSkill + 3] + ":@yel@" + this.playerStatEquipment[3 + currSkill],
							width / 2 + x + 25, yOffset, 0xFFFFFF, 1);
					}
					yOffset += 13;
				}
				yOffset -= 2;
				this.getSurface().drawLineHoriz(x, yOffset-7, width, 0);

				heightMargin = yOffset + 8;
				if (currentlyHoveredSkill == -1) {
					this.getSurface().drawString("Overall levels", x + 5, heightMargin, textColourHeading, 1);
					heightMargin += 12;
					int currSkillTotal = 0;
					totalXp = 0;

					for (currSkill = 0; currSkill < skillCount; ++currSkill) {
						totalXp += this.playerExperience[currSkill];
						currSkillTotal += this.playerStatBase[currSkill];
					}

					if (S_WANT_EXP_INFO) {
						this.getSurface().drawString("Total xp: " + totalXp, 5 + x, heightMargin, textColour, 1);
						heightMargin += 12;
					}
					this.getSurface().drawString("Skill total: " + currSkillTotal, 5 + x, heightMargin, textColour, 1);
					heightMargin += 12;
					this.getSurface().drawString("Combat level: " + this.localPlayer.level, 5 + x, heightMargin, textColour, 1);
					heightMargin += 12;
					//if there is a skill hovered over
				} else {
					this.getSurface().drawString(this.skillNameLong[currentlyHoveredSkill] + " skill", 5 + x, heightMargin, textColourHeading, 1);
					heightMargin += 12;
					int nextLevelExp = this.experienceArray[0];

					for (int currLevel = 0; currLevel < S_PLAYER_LEVEL_LIMIT - 1; ++currLevel) {
						if (this.experienceArray[currLevel] <= this.playerExperience[currentlyHoveredSkill]) {
							nextLevelExp = this.experienceArray[currLevel + 1];
						}
					}

					this.getSurface().drawString("Total xp: " + this.playerExperience[currentlyHoveredSkill], 5 + x, heightMargin, textColour,
						1);
					heightMargin += 12;
					this.getSurface().drawString("Next level at: " + nextLevelExp, 5 + x, heightMargin, textColour, 1);
					if (S_WANT_EXP_INFO) {
						heightMargin += 12;
						this.getSurface().drawString("Xp to next level: " + (nextLevelExp - this.playerExperience[currentlyHoveredSkill]), 5 + x, heightMargin, textColour, 1);
					}
				}
			}


			// quests menu tab
            if (this.uiTabPlayerInfoSubTab == 1) {
                this.panelQuestInfo.clearList(this.controlQuestInfoPanel);
                int index = 0, questNum = 0;
                this.panelQuestInfo.setListEntry(this.controlQuestInfoPanel, index++,
                        "@whi@Quest-list (green=completed)", 0, null, null);
                for (questNum = 0; questNum < questNames.length; ++questNum) {
                    if (this.questNames[questNum] != null) {
                        this.panelQuestInfo.setListEntry(this.controlQuestInfoPanel, index++,
                                (questStages[questNum] < 0 ? "@gre@" :
                                        questStages[questNum] > 0 && Config.S_WANT_QUEST_STARTED_INDICATOR ? "@yel@" : "@red@")
                                        + this.questNames[questNum],
                                0, null, null);
                    }
                }

                int position = this.panelQuestInfo.getControlSelectedListIndex(this.controlQuestInfoPanel) - 1;
                if (S_WANT_QUEST_MENUS && this.mouseButtonClick == 1 && position >= 0
                        && this.getMouseX() > x && this.getMouseY() > y + 36
                        && this.getMouseX() < x + this.getSurface().stringWidth(1, this.questNames[position])
                        && this.getMouseY() < height + 44) {
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
                int mouseYOffset = this.mouseY - 36;
                x = -this.getSurface().width2 - (-199 - this.mouseX);
                if (x >= 0 && mouseYOffset >= 0 && x < width && mouseYOffset < height) {
                    if (this.uiTabPlayerInfoSubTab == 1) {
                        this.panelQuestInfo.handleMouse(x + this.getSurface().width2 - 199, 36 + mouseYOffset,
                                this.currentMouseButtonDown, this.lastMouseButtonDown);
                    }
                    if (mouseYOffset <= 24 && this.mouseButtonClick == 1) {
                        if (x >= 98) {
                            if (x > 98) {
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

    private void duelRemoveItem(int itemIndex, int removeCount) {
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

            this.packetHandler.getClientStream().newPacket(33);
            this.packetHandler.getClientStream().writeBuffer1.putByte(this.duelOfferItemCount);

            for (int i = 0; this.duelOfferItemCount > i; ++i) {
                this.packetHandler.getClientStream().writeBuffer1.putShort(this.duelOfferItemID[i]);
                this.packetHandler.getClientStream().writeBuffer1.putInt(this.duelOfferItemSize[i]);
            }

            this.packetHandler.getClientStream().finishPacket();
            this.duelOfferAccepted = false;
            this.duelOffsetOpponentAccepted = false;
        } catch (RuntimeException var9) {
            throw GenUtil.makeThrowable(var9, "client.IC(" + itemIndex + ',' + removeCount + ',' + "dummy" + ')');
        }
    }

    private void duelStakeItem(int andStakeCount, int andStakeInvIndex) {
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

            if (EntityHandler.getItemDef(andStakeInvID).quest && !localPlayer.isAdmin()) {
                andStakeSuccess = true;
                this.showMessage(false, null, "This object cannot be added to a duel offer", MessageType.GAME,
                        0, null);
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
                this.packetHandler.getClientStream().newPacket(33);
                this.packetHandler.getClientStream().writeBuffer1.putByte(this.duelOfferItemCount);

                for (int i = 0; this.duelOfferItemCount > i; ++i) {
                    this.packetHandler.getClientStream().writeBuffer1.putShort(this.duelOfferItemID[i]);
                    this.packetHandler.getClientStream().writeBuffer1.putInt(this.duelOfferItemSize[i]);
                }

                this.packetHandler.getClientStream().finishPacket();
                this.duelOffsetOpponentAccepted = false;
                this.duelOfferAccepted = false;
            }

        } catch (RuntimeException var9) {
            throw GenUtil.makeThrowable(var9,
                    "client.C(" + "dummy" + ',' + andStakeCount + ',' + andStakeInvIndex + ')');
        }
    }

    private void fetchContainerSize() {
        try {

            //getSurface().resize(this.gameWidth, this.gameHeight + 12);
            //this.getSurface().setClip(0, this.getGameWidth(), this.getGameHeight() + 12, 0);
            //this.scene.setMidpoints(this.halfGameHeight(), true, this.getGameWidth(), this.halfGameWidth(),
            //	this.halfGameHeight(), this.m_qd, this.halfGameWidth());


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
        this.scene.setMidpoints(this.halfGameHeight(), true, this.getGameWidth(), this.halfGameWidth(),
                this.halfGameHeight(), this.m_qd, this.halfGameWidth());

        clientPort.resized();
        int var3 = this.getSurface().width2 - 199;
        byte var12 = 36;
        panelMagic.reposition(controlMagicPanel, var3, 24 + var12, 196, 90);
        panelSocial.reposition(controlSocialPanel, var3, var12 + 40, 196, 126);
        panelClan.reposition(controlClanPanel, var3, var12 + 72, 196, 128);
        panelPlayerInfo.reposition(controlPlayerInfoPanel, var3, 24 + var12, 196, 251);
        panelQuestInfo.reposition(controlQuestInfoPanel, var3, 24 + var12, 196, 251);
        panelPlayerTaskInfo.reposition(controlPlayerTaskInfoPanel, var3, 24 + var12 + 27, 196, 224);
        if (!authenticSettings)
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

    private ORSCharacter getServerNPC(int serverIndex) {
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

    private ORSCharacter getServerPlayer(int serverIndex) {
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

    private void handleAppearancePanelControls(int var1) {
        try {
            this.panelAppearance.handleMouse(this.mouseX, this.mouseY, this.currentMouseButtonDown,
                    this.lastMouseButtonDown);

            if (this.panelAppearance.isClicked(this.controlButtonAppearanceHeadMinus)) {
                do {
                    do {
                        this.appearanceHeadType = (EntityHandler.animationCount() + (this.appearanceHeadType - 1))
                                % EntityHandler.animationCount();
                    }
                    while ((3 & EntityHandler.getAnimationDef(this.appearanceHeadType).getGenderModel()) != 1);
                } while ((EntityHandler.getAnimationDef(this.appearanceHeadType).getGenderModel()
                        & this.appearanceHeadGender * 4) == 0);
            }

            if (this.panelAppearance.isClicked(this.controlButtonAppearanceHeadPlus)) {
                do {
                    do {
                        this.appearanceHeadType = (1 + this.appearanceHeadType) % EntityHandler.animationCount();
                    }
                    while (1 != (3 & EntityHandler.getAnimationDef(this.appearanceHeadType).getGenderModel()));
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
                this.packetHandler.getClientStream().newPacket(235);
                this.packetHandler.getClientStream().writeBuffer1.putByte(this.appearanceHeadGender);
                this.packetHandler.getClientStream().writeBuffer1.putByte(this.appearanceHeadType);
                this.packetHandler.getClientStream().writeBuffer1.putByte(this.m_dk);
                this.packetHandler.getClientStream().writeBuffer1.putByte(this.character2Colour);
                this.packetHandler.getClientStream().writeBuffer1.putByte(this.m_ld);
                this.packetHandler.getClientStream().writeBuffer1.putByte(this.m_Wg);
                this.packetHandler.getClientStream().writeBuffer1.putByte(this.characterBottomColour);
                this.packetHandler.getClientStream().writeBuffer1.putByte(this.m_hh);
                this.packetHandler.getClientStream().finishPacket();
                this.getSurface().blackScreen(true);
                this.showAppearanceChange = false;
            }

        } catch (RuntimeException var3) {
            throw GenUtil.makeThrowable(var3, "client.M(" + var1 + ')');
        }
    }

    private void handleGameInput() {
        try {

            if (this.systemUpdate > 1) {
                --this.systemUpdate;
            }
            if (S_WANT_EXPERIENCE_ELIXIRS && this.elixirTimer > 1) {
                --this.elixirTimer;
                if (this.elixirTimer <= 1) {
                    this.elixirTimer = 0;
                }
            }
            this.checkConnection();
            if (this.logoutTimeout > 0) {
                --this.logoutTimeout;
            }

            if (this.localPlayer.direction == ORSCharacterDirection.COMBAT_A
                    || this.localPlayer.direction == ORSCharacterDirection.COMBAT_B) {
                this.combatTimeout = 500;
            }

            if (this.combatTimeout > 0) {
                --this.combatTimeout;
            }

            if (this.showAppearanceChange) {
                this.handleAppearancePanelControls(86);
            } else if (this.showSetRecoveryQuestion) {
                this.method_181();
            } else if (this.showSetContactDetails) {
                this.M();
            } else {
                int updateIndex;
                ORSCharacter updateEntity;
                int waypointIndexCurrent;
                int waypointIndexNext;
                int stepsToMove;
                int amountToMove;
                for (updateIndex = 0; updateIndex < this.playerCount; ++updateIndex) {
                    updateEntity = this.players[updateIndex];
                    waypointIndexCurrent = (1 + updateEntity.waypointIndexCurrent) % 10;
                    if (updateEntity.waypointIndexNext == waypointIndexCurrent) {
                        updateEntity.direction = ORSCharacterDirection.lookup(updateEntity.animationNext);
                    } else {
                        ORSCharacterDirection characterDirection = null;
                        waypointIndexNext = updateEntity.waypointIndexNext;
                        if (waypointIndexNext < waypointIndexCurrent) {
                            stepsToMove = waypointIndexCurrent - waypointIndexNext;
                        } else {
                            stepsToMove = 10 + waypointIndexCurrent - waypointIndexNext;
                        }

                        amountToMove = 4;
                        if (stepsToMove > 2) {
                            amountToMove = stepsToMove * amountToMove - amountToMove;
                        }

                        if (updateEntity.waypointsX[waypointIndexNext] - updateEntity.currentX <= this.tileSize * 3
                                && updateEntity.waypointsZ[waypointIndexNext] - updateEntity.currentZ <= this.tileSize * 3
                                && updateEntity.waypointsX[waypointIndexNext] - updateEntity.currentX >= -this.tileSize * 3
                                && updateEntity.waypointsZ[waypointIndexNext] - updateEntity.currentZ >= -this.tileSize * 3 && stepsToMove <= 8 * S_MAX_WALKING_SPEED) {
                            if (updateEntity.waypointsX[waypointIndexNext] > updateEntity.currentX) {
                                characterDirection = ORSCharacterDirection.WEST;
                                updateEntity.currentX += amountToMove;
                                ++updateEntity.stepFrame;
                            } else if (updateEntity.waypointsX[waypointIndexNext] < updateEntity.currentX) {
                                ++updateEntity.stepFrame;
                                characterDirection = ORSCharacterDirection.EAST;
                                updateEntity.currentX -= amountToMove;
                            }

                            if (updateEntity.currentX - updateEntity.waypointsX[waypointIndexNext] < amountToMove
                                    && updateEntity.currentX - updateEntity.waypointsX[waypointIndexNext] > -amountToMove) {
                                updateEntity.currentX = updateEntity.waypointsX[waypointIndexNext];
                            }

                            if (updateEntity.waypointsZ[waypointIndexNext] > updateEntity.currentZ) {
                                if (characterDirection != null) {
                                    if (characterDirection == ORSCharacterDirection.WEST) {
                                        characterDirection = ORSCharacterDirection.SOUTH_WEST;
                                    } else {
                                        characterDirection = ORSCharacterDirection.SOUTH_EAST;
                                    }
                                } else {
                                    characterDirection = ORSCharacterDirection.SOUTH;
                                }

                                updateEntity.currentZ += amountToMove;
                                ++updateEntity.stepFrame;
                            } else if (updateEntity.waypointsZ[waypointIndexNext] < updateEntity.currentZ) {
                                ++updateEntity.stepFrame;
                                updateEntity.currentZ -= amountToMove;
                                if (characterDirection != null) {
                                    if (characterDirection == ORSCharacterDirection.WEST) {
                                        characterDirection = ORSCharacterDirection.NORTH_WEST;
                                    } else {
                                        characterDirection = ORSCharacterDirection.NORTH_EAST;
                                    }
                                } else {
                                    characterDirection = ORSCharacterDirection.NORTH;
                                }
                            }

                            if (updateEntity.currentZ - updateEntity.waypointsZ[waypointIndexNext] < amountToMove
                                    && updateEntity.currentZ - updateEntity.waypointsZ[waypointIndexNext] > -amountToMove) {
                                updateEntity.currentZ = updateEntity.waypointsZ[waypointIndexNext];
                            }
                        } else {
                            updateEntity.currentX = updateEntity.waypointsX[waypointIndexNext];
                            updateEntity.currentZ = updateEntity.waypointsZ[waypointIndexNext];
                        }

                        if (characterDirection != null) {
                            updateEntity.direction = characterDirection;
                        }

                        if (updateEntity.waypointsX[waypointIndexNext] == updateEntity.currentX && updateEntity.waypointsZ[waypointIndexNext] == updateEntity.currentZ) {
                            updateEntity.waypointIndexNext = (1 + waypointIndexNext) % 10;
                        }
                    }

                    if (updateEntity.bubbleTimeout > 0) {
                        --updateEntity.bubbleTimeout;
                    }

                    if (updateEntity.combatTimeout > 0) {
                        --updateEntity.combatTimeout;
                    }

                    if (updateEntity.messageTimeout > 0) {
                        --updateEntity.messageTimeout;
                    }

                    if (this.deathScreenTimeout > 0) {
                        --this.deathScreenTimeout;
                        if (this.deathScreenTimeout == 0) {
                            this.showMessage(false, null,
                                    "You have been granted another life. Be more careful this time!", MessageType.GAME,
                                    0, null);
                        }

                        if (this.deathScreenTimeout == 0) {
                            this.showMessage(false, null,
                                    "You retain your skills. Your objects land where you died", MessageType.GAME, 0,
                                    null);
                        }
                    }
                }

                for (updateIndex = 0; updateIndex < this.npcCount; ++updateIndex) {
                    updateEntity = this.npcs[updateIndex];
                    waypointIndexCurrent = (updateEntity.waypointIndexCurrent + 1) % 10;
                    if (waypointIndexCurrent == updateEntity.waypointIndexNext) {
                        if (updateEntity.npcId == 43) {
                            ++updateEntity.stepFrame;
                        }

                        updateEntity.direction = ORSCharacterDirection.lookup(updateEntity.animationNext);
                    } else {
                        ORSCharacterDirection characterDirection = null;
                        waypointIndexNext = updateEntity.waypointIndexNext;
                        if (waypointIndexCurrent <= waypointIndexNext) {
                            stepsToMove = waypointIndexCurrent + (10 - waypointIndexNext);
                        } else {
                            stepsToMove = waypointIndexCurrent - waypointIndexNext;
                        }

                        amountToMove = 4;
                        if (stepsToMove > 2) {
                            amountToMove = (stepsToMove - 1) * amountToMove;
                        }

                        if (this.tileSize * 3 >= updateEntity.waypointsX[waypointIndexNext] - updateEntity.currentX
                                && updateEntity.waypointsZ[waypointIndexNext] - updateEntity.currentZ <= this.tileSize * 3
                                && updateEntity.waypointsX[waypointIndexNext] - updateEntity.currentX >= -this.tileSize * 3
                                && updateEntity.waypointsZ[waypointIndexNext] - updateEntity.currentZ >= -this.tileSize * 3 && stepsToMove <= 8) {
                            if (updateEntity.waypointsX[waypointIndexNext] > updateEntity.currentX) {
                                ++updateEntity.stepFrame;
                                updateEntity.currentX += amountToMove;
                                characterDirection = ORSCharacterDirection.WEST;
                            } else if (updateEntity.currentX > updateEntity.waypointsX[waypointIndexNext]) {
                                characterDirection = ORSCharacterDirection.EAST;
                                ++updateEntity.stepFrame;
                                updateEntity.currentX -= amountToMove;
                            }

                            if (amountToMove > updateEntity.currentX - updateEntity.waypointsX[waypointIndexNext]
                                    && -amountToMove < updateEntity.currentX - updateEntity.waypointsX[waypointIndexNext]) {
                                updateEntity.currentX = updateEntity.waypointsX[waypointIndexNext];
                            }

                            if (updateEntity.waypointsZ[waypointIndexNext] <= updateEntity.currentZ) {
                                if (updateEntity.currentZ > updateEntity.waypointsZ[waypointIndexNext]) {
                                    if (characterDirection == null) {
                                        characterDirection = ORSCharacterDirection.NORTH;
                                    } else if (characterDirection != ORSCharacterDirection.WEST) {
                                        characterDirection = ORSCharacterDirection.NORTH_EAST;
                                    } else {
                                        characterDirection = ORSCharacterDirection.NORTH_WEST;
                                    }

                                    updateEntity.currentZ -= amountToMove;
                                    ++updateEntity.stepFrame;
                                }
                            } else {
                                ++updateEntity.stepFrame;
                                if (characterDirection == null) {
                                    characterDirection = ORSCharacterDirection.SOUTH;
                                } else if (characterDirection != ORSCharacterDirection.WEST) {
                                    characterDirection = ORSCharacterDirection.SOUTH_EAST;
                                } else {
                                    characterDirection = ORSCharacterDirection.SOUTH_WEST;
                                }

                                updateEntity.currentZ += amountToMove;
                            }

                            if (updateEntity.currentZ - updateEntity.waypointsZ[waypointIndexNext] < amountToMove
                                    && -amountToMove < updateEntity.currentZ - updateEntity.waypointsZ[waypointIndexNext]) {
                                updateEntity.currentZ = updateEntity.waypointsZ[waypointIndexNext];
                            }
                        } else {
                            updateEntity.currentX = updateEntity.waypointsX[waypointIndexNext];
                            updateEntity.currentZ = updateEntity.waypointsZ[waypointIndexNext];
                        }

                        if (characterDirection != null) {
                            updateEntity.direction = characterDirection;
                        }

                        if (updateEntity.currentX == updateEntity.waypointsX[waypointIndexNext] && updateEntity.waypointsZ[waypointIndexNext] == updateEntity.currentZ) {
                            updateEntity.waypointIndexNext = (1 + waypointIndexNext) % 10;
                        }
                    }

                    if (updateEntity.combatTimeout > 0) {
                        --updateEntity.combatTimeout;
                    }

                    if (updateEntity.bubbleTimeout > 0) {
                        --updateEntity.bubbleTimeout;
                    }

                    if (updateEntity.messageTimeout > 0) {
                        --updateEntity.messageTimeout;
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

                for (updateIndex = 0; updateIndex < this.playerCount; ++updateIndex) {
                    updateEntity = this.players[updateIndex];
                    if (updateEntity.projectileRange > 0) {
                        --updateEntity.projectileRange;
                    }
                }

                for (updateIndex = 0; updateIndex < this.npcCount; ++updateIndex) {
                    updateEntity = this.npcs[updateIndex];
                    if (updateEntity.projectileRange > 0) {
                        --updateEntity.projectileRange;
                    }
                }

                if (this.sleepWordDelayTimer > 20) {
                    this.sleepWordDelayTimer = 0;
                    this.sleepWordDelay = false;
                }

                int var10;
                if (!this.cameraAutoAngleDebug) {
                    if (this.cameraPositionX - this.localPlayer.currentX < -500
                            || this.cameraPositionX - this.localPlayer.currentX > 500
                            || this.cameraPositionZ - this.localPlayer.currentZ < -500
                            || this.cameraPositionZ - this.localPlayer.currentZ > 500) {
                        this.cameraPositionX = this.localPlayer.currentX;
                        this.cameraPositionZ = this.localPlayer.currentZ;
                    }

                    if (this.optionCameraModeAuto) {
                        updateIndex = this.cameraAngle * 32;
                        var10 = updateIndex - this.cameraRotation;
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

                    if (this.localPlayer.currentZ != this.cameraPositionZ) {
                        this.cameraPositionZ += (this.localPlayer.currentZ - this.cameraPositionZ)
                                / ((this.cameraZoom - 500) / 15 + 16);
                    }

                    if (this.cameraPositionX != this.localPlayer.currentX) {
                        this.cameraPositionX += (this.localPlayer.currentX - this.cameraPositionX)
                                / ((this.cameraZoom - 500) / 15 + 16);
                    }
                } else if (this.cameraPositionX - this.localPlayer.currentX < -500
                        || this.cameraPositionX - this.localPlayer.currentX > 500
                        || this.cameraPositionZ - this.localPlayer.currentZ < -500
                        || this.cameraPositionZ - this.localPlayer.currentZ > 500) {
                    this.cameraPositionX = this.localPlayer.currentX;
                    this.cameraPositionZ = this.localPlayer.currentZ;
                }

                if (!this.isSleeping) {
                    if (mouseY > (getGameHeight() - 4)) { // Chat Tab Selection
                        if (mouseX > 15 + (halfGameWidth() - 256) && mouseX < 96 + (halfGameWidth() - 256)
                                && lastMouseButtonDown == 1)
                            this.messageTabSelected = MessageTab.ALL;
                        if (mouseX > 110 + (halfGameWidth() - 256) && mouseX < 194 + (halfGameWidth() - 256)
                                && lastMouseButtonDown == 1) {
                            this.messageTabSelected = MessageTab.CHAT;
                            this.panelMessageTabs.controlScrollAmount[this.panelMessageChat] = 999999;
                        }
                        if (mouseX > 215 + (halfGameWidth() - 256) && mouseX < 295 + (halfGameWidth() - 256)
                                && lastMouseButtonDown == 1) {
                            this.messageTabSelected = MessageTab.QUEST;
                            this.panelMessageTabs.controlScrollAmount[this.panelMessageQuest] = 999999;
                        }
                        if (mouseX > 315 + (halfGameWidth() - 256) && mouseX < 395 + (halfGameWidth() - 256)
                                && lastMouseButtonDown == 1) {
                            this.messageTabSelected = MessageTab.PRIVATE;
                            this.panelMessageTabs.controlScrollAmount[this.panelMessagePrivate] = 999999;
                        }
                        if (mouseX > 417 + (halfGameWidth() - 256) && mouseX < 497 + (halfGameWidth() - 256)
                                && lastMouseButtonDown == 1) {
                            if (S_WANT_CLANS) {
                                this.messageTabSelected = MessageTab.CLAN;
                                this.panelMessageTabs.controlScrollAmount[this.panelMessageClan] = 999999;
                            } else {
                                this.inputTextFinal = "";
                                this.inputTextCurrent = "";
                                this.reportAbuse_State = 1;
                            }
                        }

                        this.currentMouseButtonDown = 0;
                        this.lastMouseButtonDown = 0;
                    }

                    this.panelMessageTabs.handleMouse(this.mouseX, this.mouseY,
                            this.currentMouseButtonDown, this.lastMouseButtonDown);
                    auctionHouse.myAuctions.handleMouse(this.mouseX, this.mouseY,
                            this.currentMouseButtonDown, this.lastMouseButtonDown);
                    auctionHouse.auctionMenu.handleMouse(this.mouseX, this.mouseY,
                            this.currentMouseButtonDown, this.lastMouseButtonDown);
                    clan.getClanInterface().clanSetupPanel.handleMouse(this.mouseX, this.mouseY,
                            this.currentMouseButtonDown, this.lastMouseButtonDown);
                    bank.bank.handleMouse(this.mouseX, this.mouseY,
                            this.currentMouseButtonDown, this.lastMouseButtonDown);
                    if (this.messageTabSelected != MessageTab.ALL && this.mouseX >= 494
                            && this.mouseY >= this.getGameHeight() - 66) {
                        this.lastMouseButtonDown = 0;
                    }

                    if (this.panelMessageTabs.isClicked(this.panelMessageEntry)) {
                        String var11 = this.panelMessageTabs.getControlText(this.panelMessageEntry);
                        this.panelMessageTabs.setText(this.panelMessageEntry, "");
                        if (var11.startsWith("::")) {
                            if (var11.equalsIgnoreCase("::dev") && localPlayer.isDev()) {
                                developerMenu = true;
                            } else if (var11.equalsIgnoreCase("::mod") && localPlayer.isMod()) {
                                modMenu = true;
                            } else if (var11.startsWith("::n ") && localPlayer.isDev()) {
                                devMenuNpcID = Integer.parseInt(var11.split(" ")[1]);
                            } else if (var11.equalsIgnoreCase("::overlay") && S_SIDE_MENU_TOGGLE) {
                                C_SIDE_MENU_OVERLAY = !C_SIDE_MENU_OVERLAY;
                            } else {
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

                    for (updateIndex = 0; updateIndex < messagesArray.length; ++updateIndex) {
                        if (MessageHistory.messageHistoryTimeout[updateIndex] > 0) {
                            --MessageHistory.messageHistoryTimeout[updateIndex];
                        }
                    }

                    if (this.deathScreenTimeout != 0) {
                        this.lastMouseButtonDown = 0;
                    }

                    if (!this.showDialogTrade && !this.showDialogDuel && !isShowDialogBank()) {
                        this.mouseButtonDownTime = 0;
                        this.mouseButtonItemCountIncrement = 0;
                    } else {
                        if (this.currentMouseButtonDown == 0) {
                            this.mouseButtonDownTime = 0;
                            this.mouseButtonItemCountIncrement = 0;
                        } else {
                            ++this.mouseButtonDownTime;
                        }
                        if (!isAndroid()) {
                            if (this.mouseButtonDownTime > 500)
                                this.mouseButtonItemCountIncrement += 100000;
                            else if (this.mouseButtonDownTime > 350)
                                this.mouseButtonItemCountIncrement += 10000;
                            else if (this.mouseButtonDownTime > 250)
                                this.mouseButtonItemCountIncrement += 1000;
                            else if (this.mouseButtonDownTime > 150)
                                this.mouseButtonItemCountIncrement += 100;
                            else if (this.mouseButtonDownTime > 100)
                                this.mouseButtonItemCountIncrement += 10;
                            else if (this.mouseButtonDownTime > 50)
                                this.mouseButtonItemCountIncrement++;
                            else if (this.mouseButtonDownTime > 20 && (this.mouseButtonDownTime & 5) == 0)
                                ++this.mouseButtonItemCountIncrement;
                        }
                    }

                    if (this.lastMouseButtonDown == 1) {
                        this.mouseButtonClick = 1;
                    } else if (this.lastMouseButtonDown == 2) {
                        this.mouseButtonClick = 2;
                    }
                    if (mainComponent.checkMouseInput(getMouseX(), getMouseY(), getMouseButtonDown(),
                            getMouseClick()) && !this.isShowDialogBank()) {
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
                                if (!this.doCameraZoom) {
                                    if ((1 & this.cameraAngle) == 0) {
                                        this.cameraAngle = 7 & 1 + this.cameraAngle;
                                    }

                                    for (updateIndex = 0; updateIndex < 8 && !this.cameraColliding(this.cameraAngle); ++updateIndex) {
                                        this.cameraAngle = 1 + this.cameraAngle & 7;
                                    }
                                }
                            }

                            if (this.keyRight) {
                                this.keyRight = false;
                                this.cameraAngle = 7 + this.cameraAngle & 7;
                                if (!this.doCameraZoom) {
                                    if ((1 & this.cameraAngle) == 0) {
                                        this.cameraAngle = this.cameraAngle + 7 & 7;
                                    }

                                    for (updateIndex = 0; updateIndex < 8 && !this.cameraColliding(this.cameraAngle); ++updateIndex) {
                                        this.cameraAngle = this.cameraAngle + 7 & 7;
                                    }
                                }
                            }
                        }
                    } else if (this.keyLeft) {
                        this.cameraRotation = 255 & this.cameraRotation + 2;
                    } else if (this.keyRight) {
                        this.cameraRotation = 255 & this.cameraRotation - 2;
                    } else if (this.keyDown) {
                        if (S_ZOOM_VIEW_TOGGLE || getLocalPlayer().isStaff()) {
                            // Don't want to go over 255
                            if (C_LAST_ZOOM < 254) {
                                C_LAST_ZOOM += 2;
                                // We probably want to send this on the client tick rather than each time a button is pressed
                                saveZoomDistance();
                            }
                        } else {
                            if (this.cameraAllowPitchModification) {
                                this.cameraPitch = (this.cameraPitch + 4) & 1023;

                                // Limit on the half circled where everything is right side up
                                if (this.cameraPitch > 256 && this.cameraPitch <= 512)
                                    this.cameraPitch = 256;

                                if (this.cameraPitch < 768 && this.cameraPitch > 512)
                                    this.cameraPitch = 768;
                            }
                        }
                    } else if (this.keyUp) {
                        if (S_ZOOM_VIEW_TOGGLE || getLocalPlayer().isStaff()) {
                            // Don't want to go under 0
                            if (C_LAST_ZOOM > 1) {
                                C_LAST_ZOOM -= 2;
                                // We probably want to send this on the client tick rather than each time a button is pressed
                                saveZoomDistance();
                            }
                        } else {
                            if (this.cameraAllowPitchModification) {
                                this.cameraPitch = (this.cameraPitch + 1024 - 4) & 1023;
                            }
                        }
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

                    if (amountToZoom > 0) {
                        minCameraZoom += 4;
                        amountToZoom -= 1;
                    }
                    if (amountToZoom < 0) {
                        minCameraZoom -= 4;
                        amountToZoom += 1;
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

                    for (updateIndex = 0; updateIndex < this.gameObjectInstanceCount; ++updateIndex) {
                        var10 = this.gameObjectInstanceX[updateIndex];
                        waypointIndexCurrent = this.gameObjectInstanceZ[updateIndex];
                        if (var10 >= 0 && waypointIndexCurrent >= 0 && var10 < 96 && waypointIndexCurrent < 96) {
                        	if (this.gameObjectInstanceID[updateIndex] == 74)
								this.gameObjectInstanceModel[updateIndex].addRotation(1, 0, 0);
                        	else if (EntityHandler.getObjectDef(this.gameObjectInstanceID[updateIndex]).getObjectModel() == "portal")
								this.gameObjectInstanceModel[updateIndex].addRotation(0, 1, 0);
                        }
					}

                    for (updateIndex = 0; updateIndex < this.teleportBubbleCount; ++updateIndex) {
                        ++this.teleportBubbleTime[updateIndex];
                        if (this.teleportBubbleTime[updateIndex] > 50) {
                            --this.teleportBubbleCount;

                            for (var10 = updateIndex; var10 < this.teleportBubbleCount; ++var10) {
                                this.teleportBubbleX[var10] = this.teleportBubbleX[var10 + 1];
                                this.teleportBubbleZ[var10] = this.teleportBubbleZ[1 + var10];
                                this.teleportBubbleTime[var10] = this.teleportBubbleTime[1 + var10];
                                this.teleportBubbleType[var10] = this.teleportBubbleType[1 + var10];
                            }
                        }
                    }

                } else {
                    if (this.inputTextFinal.length() > 0) {
                        this.packetHandler.getClientStream().newPacket(45);
                        if (this.sleepWordDelay) {
                            this.packetHandler.getClientStream().writeBuffer1.putByte(1);
                        } else {
                            this.packetHandler.getClientStream().writeBuffer1.putByte(0);
                            this.sleepWordDelay = true;
                        }

                        this.packetHandler.getClientStream().writeBuffer1.putNullThenString(this.inputTextFinal, 116);
                        this.packetHandler.getClientStream().finishPacket();
                        this.inputTextCurrent = "";
                        this.sleepingStatusText = "Please wait...";
                        this.inputTextFinal = "";
                    }

                    if (this.lastMouseButtonDown == 1 && this.mouseY > 275 - (isAndroid() ? 110 : 0) && this.mouseY < 310 - (isAndroid() ? 110 : 0) && this.mouseX > 56
                            && this.mouseX < 456) {
                        this.packetHandler.getClientStream().newPacket(45);
                        if (!this.sleepWordDelay) {
                            this.packetHandler.getClientStream().writeBuffer1.putByte(0);
                            this.sleepWordDelay = true;
                        } else {
                            this.packetHandler.getClientStream().writeBuffer1.putByte(1);
                        }

                        this.packetHandler.getClientStream().writeBuffer1.putNullThenString("-null-", -74);
                        this.packetHandler.getClientStream().finishPacket();
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

    public void saveZoomDistance() {
        // Saves last zoom distance
        this.packetHandler.getClientStream().newPacket(111);
        this.packetHandler.getClientStream().writeBuffer1.putByte(23);
        this.packetHandler.getClientStream().writeBuffer1.putByte(Config.C_LAST_ZOOM);
        this.packetHandler.getClientStream().finishPacket();
        //System.out.println(cameraZoom);
    }

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
                if (this.loginScreenNumber == 4 && null != this.panelRecovery) {
                    this.panelRecovery.keyPress(key);
                }
            }

            if (var1 > 105) {
                if (this.currentViewMode == GameMode.GAME) {
                    if (this.showAppearanceChange) {
                        this.panelAppearance.keyPress(key);
                        return;
                    }
                    if (this.showSetRecoveryQuestion) {
                        if (this.qaIndex == -1) {
                            this.panelSetRecoveryQuestion.keyPress(key);
                        }
                        return;
                    }
                    if (this.showSetContactDetails) {
                        this.panelContact.keyPress(key);
                        return;
                    }

                    if (this.panelPasswordChange_Mode == PasswordChangeMode.PASSWORD_MISMATCH
                            || this.panelPasswordChange_Mode == PasswordChangeMode.PASSWORD_REQ_SENT
                            || this.panelPasswordChange_Mode == PasswordChangeMode.NEED_LONGER_PASSWORD
                            || this.panelPasswordChange_Mode == PasswordChangeMode.PASSWORD_NOT_EQ_USER) {
                        this.panelPasswordChange_Mode = PasswordChangeMode.NONE;
                    }
                    if (auctionHouse.isVisible() && (auctionHouse.auctionMenu.focusOn(auctionHouse.auctionSearchHandle)
                            || auctionHouse.myAuctions.focusOn(auctionHouse.textField_priceEach)
                            || auctionHouse.auctionMenu.focusOn(auctionHouse.textField_buyAmount)
                            || auctionHouse.myAuctions.focusOn(auctionHouse.textField_amount)
                            || auctionHouse.myAuctions.focusOn(auctionHouse.textField_price))) {
                        auctionHouse.keyDown(key);
                        return;
                    }
                    if (S_WANT_CUSTOM_BANKS && bank.bank.focusOn(bank.bankSearch)) {
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

                    if (optionsMenuShow && S_WANT_KEYBOARD_SHORTCUTS > 0) {
                        try {
                            int option = Integer.parseInt("" + (char) key) - 1;
                            if (option >= 0 && option < optionsMenuCount) {
                                this.packetHandler.getClientStream().newPacket(116);
                                this.packetHandler.getClientStream().writeBuffer1.putByte(option);
                                this.packetHandler.getClientStream().finishPacket();
                                mouseButtonClick = 0;
                                optionsMenuShow = false;
                                return;
                            }
                        } catch (Exception e) {

                        }
                    }
                    if ((key == '\n' || key == '\r') && (this.inputX_Action == InputXAction.BANK_DEPOSIT || this.inputX_Action == InputXAction.BANK_WITHDRAW)) {
                        this.inputX_OK = true;
                    }

                    if (this.panelSocialPopup_Mode == SocialPopupMode.NONE && this.panelPasswordChange_Mode == PasswordChangeMode.NONE
                            && this.reportAbuse_State == 0 && !this.isSleeping && this.inputX_Action == InputXAction.ACT_0) {
                        this.panelMessageTabs.keyPress(key);
                    }
                }

            }
        } catch (RuntimeException var4) {
            throw GenUtil.makeThrowable(var4, "client.AD(" + var1 + ',' + key + ')');
        }
    }

    private void handleLoginScreenInput(int var1) {
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
                        if (wantEmail()) {
                            if (menuNewUser.getControlText(menuNewUserUsername) != null
                                    && menuNewUser.getControlText(menuNewUserUsername).length() == 0
                                    || menuNewUser.getControlText(menuNewUserPassword) != null
                                    && menuNewUser.getControlText(menuNewUserPassword).length() == 0
                                    || menuNewUser.getControlText(menuNewUserEmail) != null
                                    && menuNewUser.getControlText(menuNewUserEmail).length() == 0) {
                                menuNewUser.setText(menuNewUserStatus, "Please fill in all requested");
                                menuNewUser.setText(menuNewUserStatus2, "information to continue!");
                                return;
                            }
                        } else {
                            if (menuNewUser.getControlText(menuNewUserUsername) != null
                                    && menuNewUser.getControlText(menuNewUserUsername).length() == 0
                                    || menuNewUser.getControlText(menuNewUserPassword) != null
                                    && menuNewUser.getControlText(menuNewUserPassword).length() == 0) {
                                menuNewUser.setText(menuNewUserStatus, "Please fill in all requested");
                                menuNewUser.setText(menuNewUserStatus2, "information to continue!");
                                return;
                            }
                        }

                        menuNewUser.setText(menuNewUserStatus, "Please wait...");
                        menuNewUser.setText(menuNewUserStatus2, "Creating new account");

                        String username = menuNewUser.getControlText(menuNewUserUsername);
                        String password = menuNewUser.getControlText(menuNewUserPassword);
                        String email = menuNewUser.getControlText(menuNewUserEmail);

                        sendRegister(username, password, email);
                    }
                } else if (this.loginScreenNumber == 2) {
                    this.panelLogin.handleMouse(this.mouseX, this.mouseY, this.currentMouseButtonDown,
                            this.lastMouseButtonDown);
                    if (this.panelLogin.isClicked(this.m_Xi)) {
                        this.loginScreenNumber = 0;
                    }
                    if (isAndroid() || Remember()) {
                        if (this.panelLogin.isClicked(this.rememberButtonIdx)) {

                            boolean temp = ClientPort.saveCredentials(this.panelLogin.getControlText(this.controlLoginUser) + "," + this.panelLogin.getControlText(this.controlLoginPass));

                            if (temp)
                                this.panelLogin.setText(this.controlLoginStatus2, "@gre@Credentials Saved");
                        }
                    }

                    if (S_WANT_HIDE_IP) {
                        if (this.panelLogin.isClicked(this.hideIpButtonIdx)) {
                            this.settingsHideIP = 1 - this.settingsHideIP;
                            String text = (this.settingsHideIP != 1) ? "Hide IP" : "Show IP";
                            this.panelLogin.setText(this.hideIpButtonIdx - 1, text);

                            boolean temp = saveHideIp(this.settingsHideIP);

                            String msg = (this.settingsHideIP != 1) ? "@red@Your IP will be shown after login"
                                    : "@gre@Your IP will be hidden after login";
                            if (temp)
                                this.panelLogin.setText(this.controlLoginStatus2, msg);
                        }
                    }


                    if (this.panelLogin.isClicked(this.controlLoginUser)) {
                        this.panelLogin.setFocus(this.controlLoginPass);
                    }

                    if (this.panelLogin.isClicked(this.controlLoginPass) || this.panelLogin.isClicked(this.m_be) || this.enterPressed) {

                        this.enterPressed = false;

                        this.setUsername(this.panelLogin.getControlText(this.controlLoginUser));
                        this.password = this.panelLogin.getControlText(this.controlLoginPass);
                        this.autoLoginTimeout = 2;
                        this.login(-12, this.password, this.getUsername(), false);
                    }

                    if (this.panelLogin.isClicked(this.lostPasswordButtonIdx)) {
                        this.username = this.panelLogin.getControlText(this.controlLoginUser);
                        this.username = DataOperations.addCharacters(this.username, 20);
                        if (this.username.trim().length() == 0) {
                            showLoginScreenStatus("You must enter your username to recover your password", "");
                            return;
                        }

                        showLoginScreenStatus("Please wait...", "Connecting to server");

                        try {
//							if (is_applet()) {
//								conn = new Connection(host, this, server_port);
//							} else {
//								conn = new Connection(host, (Applet) null, server_port);
//							}

                            if ((Config.SERVER_IP != null)) {
                                String ip = Config.SERVER_IP; // allows override if manually set in Config code
                                int port = Config.SERVER_PORT; // allows override if manually set in Config code
                                this.packetHandler.setClientStream(new Network_Socket(this.packetHandler.openSocket(port, ip), this.packetHandler));
                                this.packetHandler.getClientStream().m_d = MiscFunctions.maxReadTries;
                            } else {
                                String ip = Config.getServerIp(); // loads cached server IP addressed
                                int port = Config.getServerPort(); // loads cached port
                                this.packetHandler.setClientStream(new Network_Socket(this.packetHandler.openSocket(port, ip), this.packetHandler));
                                this.packetHandler.getClientStream().m_d = MiscFunctions.maxReadTries;
                            }

                            this.packetHandler.getClientStream().newPacket(5);
                            this.packetHandler.getClientStream().writeBuffer1.putString(getUsername());
                            this.packetHandler.getClientStream().finishPacketAndFlush();
                            int var11 = this.packetHandler.getClientStream().read();

                            System.out.println("Getting response: " + var11);
                            if (var11 == 0) {
                                showLoginScreenStatus("Sorry, the recovery questions for this user have not been set", "");
                                return;
                            }

                            int premadeQuestionNum;
                            for (int i = 0; i < 5; ++i) {
                                int len = this.packetHandler.getClientStream().read();
                                byte[] buff = new byte[5000];
                                this.packetHandler.getClientStream().read(buff, 0, len);
                                String question = new String(buff, 0, len - 1);
                                if (question.startsWith("~:")) {
                                    question = question.substring(2);
                                    premadeQuestionNum = 0;

                                    try {
                                        premadeQuestionNum = Integer.parseInt(question);
                                    } catch (Exception e) {
                                    }

                                    question = this.questions[premadeQuestionNum];
                                }

                                this.panelRecovery.setText(this.controlPassQuestion[i], question);
                            }

                            if (this.attemptedRecovery) {
                                showLoginScreenStatus("Sorry, you have already attempted 1 recovery, try again later", "");
                                return;
                            }

                            this.loginScreenNumber = 4;
                            this.panelRecovery.setText(this.instructPassRecovery1,
                                    "@yel@To prove this is your account please provide the answers to");
                            this.panelRecovery.setText(this.instructPassRecovery2,
                                    "@yel@your security questions. You will then be able to reset your password");

                            for (int i = 0; i < 5; ++i) {
                                this.panelRecovery.setText(this.controlPassAnswer[i], "");
                            }

                            this.panelRecovery.setText(this.controlPreviousPassword, "");
                            this.panelRecovery.setText(this.controlNewPassword, "");
                            this.panelRecovery.setText(this.controlConfirmation, "");
                            return;
                        } catch (Exception e) {
                            showLoginScreenStatus("Sorry! Unable to connect to server.", "Check your internet settings");
                            return;
                        }
                    }
                } else if (this.loginScreenNumber == 4) {
                    this.panelRecovery.handleMouse(this.mouseX, this.mouseY, this.currentMouseButtonDown,
                            this.lastMouseButtonDown);
                    if (this.panelRecovery.isClicked(this.passwordRecoverSubmit)) {
                        String newPass = this.panelRecovery.getControlText(this.controlNewPassword);
                        String confPass = this.panelRecovery.getControlText(this.controlConfirmation);
                        if (!newPass.equalsIgnoreCase(confPass)) {
                            this.showLoginScreenStatus("@yel@The two new passwords entered are not the same as each other!", "");
                            return;
                        }

                        if (newPass.length() < 5) {
                            this.showLoginScreenStatus("@yel@Your new password must be at least 5 letters long", "");
                            return;
                        }

                        this.showLoginScreenStatus("Please wait...", "Connecting to server");

                        try {
							/*if (this.is_applet()) {
								super.conn = new Connection(super.host, this, super.server_port);
							} else {
								super.conn = new Connection(super.host, (Applet) null, super.server_port);
							}*/

                            if ((Config.SERVER_IP != null)) {
                                String ip = Config.SERVER_IP; // allows override if manually set in Config code
                                int port = Config.SERVER_PORT; // allows override if manually set in Config code
                                this.packetHandler.setClientStream(new Network_Socket(this.packetHandler.openSocket(port, ip), this.packetHandler));
                                this.packetHandler.getClientStream().m_d = MiscFunctions.maxReadTries;
                            } else {
                                String ip = Config.getServerIp(); // loads cached server IP addressed
                                int port = Config.getServerPort(); // loads cached port
                                this.packetHandler.setClientStream(new Network_Socket(this.packetHandler.openSocket(port, ip), this.packetHandler));
                                this.packetHandler.getClientStream().m_d = MiscFunctions.maxReadTries;
                            }

                            String pPass = DataOperations.addCharacters(this.panelRecovery.getControlText(this.controlPreviousPassword), 20);
                            String nPass = DataOperations.addCharacters(this.panelRecovery.getControlText(this.controlNewPassword), 20);
                            this.packetHandler.getClientStream().newPacket(7);
                            this.packetHandler.getClientStream().writeBuffer1.putString(getUsername());
                            this.packetHandler.getClientStream().writeBuffer1.putString(pPass);
                            this.packetHandler.getClientStream().writeBuffer1.putString(nPass);
                            this.packetHandler.getClientStream().writeBuffer1.putLong(getUID());
                            String answer;
                            for (int i = 0; i < 5; ++i) {
                                answer = this.panelRecovery.getControlText(this.controlPassAnswer[i]);
                                if (answer.length() > 100) {
                                    answer = answer.substring(0, 100);
                                }
                                this.packetHandler.getClientStream().writeBuffer1.putString(answer);
                            }

                            //conn.int_put(this.seed());
                            //conn.enc_cred_put(var13 + var5, var3, this.rsa_exponent, this.rsa_modulus);

                            //for (var6 = 0; var6 < 5; ++var6) {
                            //	conn.enc_cred_put(DataUtil.method_13(this.recovery_ui.get_text(this.jlb[var6])), var3,
                            //			this.rsa_exponent, this.rsa_modulus);
                            //}

                            this.packetHandler.getClientStream().finishPacketAndFlush();
                            int var7 = this.packetHandler.getClientStream().read();
                            System.out.println("Recover response: " + var7);
                            if (var7 == 0) {
                                this.loginScreenNumber = 2;
                                this.showLoginScreenStatus("Sorry, recovery failed. You may try again in 1 hour", "");
                                this.attemptedRecovery = true;
                                return;
                            }

                            if (var7 == 1) {
                                this.loginScreenNumber = 2;
                                this.showLoginScreenStatus("Your pass has been reset. You may now use the new pass to login", "");
                                return;
                            }

                            this.loginScreenNumber = 2;
                            this.showLoginScreenStatus("Recovery failed! Attempts exceeded?", "");
                            return;
                        } catch (Exception e) {
                            this.showLoginScreenStatus("Sorry! Unable to connect to server.", "Check your internet settings");
                        }
                    }

                    if (this.panelRecovery.isClicked(this.passwordRecoverCancel)) {
                        this.loginScreenNumber = 0;
                    }
                }
            } else {
                this.panelLoginWelcome.handleMouse(this.mouseX, this.mouseY, this.currentMouseButtonDown,
                        this.lastMouseButtonDown);
                if (this.panelLoginWelcome.isClicked(loginButtonExistingUser)) {
                    if (isAndroid())
                        clientPort.drawKeyboard(); // launches the Android soft keyboard
                    this.loginScreenNumber = 2;
                    this.panelLogin.setText(this.controlLoginStatus1, "");
                    this.panelLogin.setText(this.controlLoginStatus2, "Please enter your username and password");
                    this.panelLogin.setFocus(this.controlLoginUser);
                } else if (panelLoginWelcome.isClicked(loginButtonNewUser)) {
                    loginScreenNumber = 1;
                    this.menuNewUser.setText(this.menuNewUserStatus, "Please fill in all fields");
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
            }
            this.showLoginScreenStatus("Sorry! The server is currently full.", "Please try again later");
        }

        this.m_Zb = 1500;
        username = user;
        user = DataOperations.addCharacters(user, 20);
        password = pass;
        pass = DataOperations.addCharacters(pass, 20);

        if (user.trim().length() == 0) {
            showLoginScreenStatus("Please fill in all requested", "information to continue!");
            return;
        }
        if (user.trim().length() < 2) {
            showLoginScreenStatus("Username must be at least 2", "characters long!");
            return;
        }
        if (user.trim().length() > 12) {
            showLoginScreenStatus("Username is too long, please use", "username with length of 2-12");
            return;
        }
        if (pass.trim().length() < 4) {
            showLoginScreenStatus("Password must be at least 4", "characters long!");
            return;
        }
        if (pass.trim().length() > 16) {
            showLoginScreenStatus("Password is too long, please use", "password with length of 4-16");
            return;
        }
        if (!isValidEmailAddress(email)) {
            showLoginScreenStatus("Invalid email address", "please use a valid email address");
        }
        try {
            if ((Config.SERVER_IP != null)) {
                String ip = Config.SERVER_IP; // allows override if manually set in Config code
                int port = Config.SERVER_PORT; // allows override if manually set in Config code
                this.packetHandler.setClientStream(new Network_Socket(this.packetHandler.openSocket(port, ip), this.packetHandler));
                this.packetHandler.getClientStream().m_d = MiscFunctions.maxReadTries;
            } else {
                String ip = Config.getServerIp(); // loads cached server IP addressed
                int port = Config.getServerPort(); // loads cached port
                this.packetHandler.setClientStream(new Network_Socket(this.packetHandler.openSocket(port, ip), this.packetHandler));
                this.packetHandler.getClientStream().m_d = MiscFunctions.maxReadTries;
            }
            this.packetHandler.getClientStream().newPacket(78);
            this.packetHandler.getClientStream().writeBuffer1.putString(user);
            this.packetHandler.getClientStream().writeBuffer1.putString(pass);
            if (wantEmail()) {
                this.packetHandler.getClientStream().writeBuffer1.putString(email);
            }
            this.packetHandler.getClientStream().finishPacketAndFlush();

            int registerResponse = this.packetHandler.getClientStream().read();

            System.out.println("Registration response:" + registerResponse);
            if (registerResponse == 0) {
                panelLogin.setText(controlLoginUser, username.replaceAll("[^=,\\da-zA-Z\\s]|(?<!,)\\s", " ").trim());
                panelLogin.setText(controlLoginPass, password);

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
            if (registerResponse == 6) {
                showLoginScreenStatus("Invalid e-mail address", "please use a valid email address");
                return;
            }
            if (registerResponse == 7) {
                showLoginScreenStatus("Username must be 2-12", "characters long!");
                return;
            }
            showLoginScreenStatus("Error unable to login.", "Unrecognised response code");
        } catch (Exception e) {
            this.showLoginScreenStatus("Sorry! Unable to connect.", "Check internet settings");
            e.printStackTrace();
        }

    }

    private void handleMenuItemClicked(boolean var1, int item) {
        try {

            MenuItemAction var3 = this.menuCommon.getItemAction(item);
            int indexOrX = this.menuCommon.getItemIndexOrX(item);
            int idOrZ = this.menuCommon.getItemIdOrZ(item);
            int dir = this.menuCommon.getItemDirection(item);
            int tileID = this.menuCommon.getItemTileID(item);
            int var8 = this.menuCommon.getItemParam_l(item);
            String var9 = this.menuCommon.getItemStringB(item);

            ORSCharacter character = null;
            int cTileX;
            int cTileZ;

            switch (var3) {
                case GROUND_ITEM_CAST_SPELL: {
                    this.walkToGroundItem(this.playerLocalX, this.playerLocalZ, indexOrX, idOrZ, true);
                    this.packetHandler.getClientStream().newPacket(249);
                    this.packetHandler.getClientStream().writeBuffer1.putShort(tileID); // spell
                    this.packetHandler.getClientStream().writeBuffer1.putShort(indexOrX + this.midRegionBaseX);
                    this.packetHandler.getClientStream().writeBuffer1.putShort(idOrZ + this.midRegionBaseZ);
                    this.packetHandler.getClientStream().writeBuffer1.putShort(dir);
                    this.packetHandler.getClientStream().finishPacket();
                    this.selectedSpell = -1;
                    break;
                }
                case GROUND_ITEM_USE_ITEM: {
                    this.walkToGroundItem(this.playerLocalX, this.playerLocalZ, indexOrX, idOrZ, true);
                    this.packetHandler.getClientStream().newPacket(53);
                    this.packetHandler.getClientStream().writeBuffer1.putShort(this.midRegionBaseX + indexOrX);
                    this.packetHandler.getClientStream().writeBuffer1.putShort(this.midRegionBaseZ + idOrZ);
                    this.packetHandler.getClientStream().writeBuffer1.putShort(dir);
                    this.packetHandler.getClientStream().writeBuffer1.putShort(tileID); // inventory
                    // item

                    this.packetHandler.getClientStream().finishPacket();
                    this.selectedItemInventoryIndex = -1;
                    break;
                }
                case GROUND_ITEM_TAKE: {
                    this.walkToGroundItem(this.playerLocalX, this.playerLocalZ, indexOrX, idOrZ, true);
                    this.packetHandler.getClientStream().newPacket(247);
                    this.packetHandler.getClientStream().writeBuffer1.putShort(indexOrX + this.midRegionBaseX);
                    this.packetHandler.getClientStream().writeBuffer1.putShort(this.midRegionBaseZ + idOrZ);
                    this.packetHandler.getClientStream().writeBuffer1.putShort(dir);
                    this.packetHandler.getClientStream().finishPacket();
                    break;
                }
                case GROUND_ITEM_EXAMINE: {
                    this.showMessage(false, null, EntityHandler.getItemDef(indexOrX).getDescription(),
                            MessageType.GAME, 0, null);
                    break;
                }
                case ITEM_EXAMINE: {
                    //if (EntityHandler.getItemDef(indexOrX).stackable) {
                    //	this.showMessage(false, (String) null,
                    //			StringUtil.formatItemCount(getInventoryCount(indexOrX)) + (getInventoryCount(indexOrX) < 1000 ? "x" : "") + " - "
                    //					+ EntityHandler.getItemDef(indexOrX).getDescription(),
                    //					MessageType.GAME, 0, (String) null, (String) null);
                    //} else {
                    this.showMessage(false, null, EntityHandler.getItemDef(indexOrX).getDescription(),
                            MessageType.GAME, 0, null);
                    //}
                    break;
                }
                case WALL_CAST_SPELL: {
                    this.walkToWall(indexOrX, idOrZ, dir);
                    this.packetHandler.getClientStream().newPacket(180);
                    this.packetHandler.getClientStream().writeBuffer1.putShort(this.midRegionBaseX + indexOrX);
                    this.packetHandler.getClientStream().writeBuffer1.putShort(this.midRegionBaseZ + idOrZ);
                    this.packetHandler.getClientStream().writeBuffer1.putByte(dir);
                    this.packetHandler.getClientStream().writeBuffer1.putShort(tileID);

                    this.packetHandler.getClientStream().finishPacket();
                    this.selectedSpell = -1;
                    break;
                }
                case WALL_USE_ITEM: {
                    this.walkToWall(indexOrX, idOrZ, dir);
                    this.packetHandler.getClientStream().newPacket(161);
                    this.packetHandler.getClientStream().writeBuffer1.putShort(indexOrX + this.midRegionBaseX);
                    this.packetHandler.getClientStream().writeBuffer1.putShort(idOrZ + this.midRegionBaseZ);
                    this.packetHandler.getClientStream().writeBuffer1.putByte(dir);
                    this.packetHandler.getClientStream().writeBuffer1.putShort(tileID);
                    this.packetHandler.getClientStream().finishPacket();
                    this.selectedItemInventoryIndex = -1;
                    break;
                }
                case WALL_COMMAND1: {
                    this.walkToWall(indexOrX, idOrZ, dir);
                    this.packetHandler.getClientStream().newPacket(14);
                    this.packetHandler.getClientStream().writeBuffer1.putShort(indexOrX + this.midRegionBaseX);
                    this.packetHandler.getClientStream().writeBuffer1.putShort(idOrZ + this.midRegionBaseZ);
                    this.packetHandler.getClientStream().writeBuffer1.putByte(dir);
                    this.packetHandler.getClientStream().finishPacket();
                    break;
                }
                case WALL_COMMAND2: {
                    this.walkToWall(indexOrX, idOrZ, dir);
                    this.packetHandler.getClientStream().newPacket(127);
                    this.packetHandler.getClientStream().writeBuffer1.putShort(this.midRegionBaseX + indexOrX);
                    this.packetHandler.getClientStream().writeBuffer1.putShort(idOrZ + this.midRegionBaseZ);
                    this.packetHandler.getClientStream().writeBuffer1.putByte(dir);
                    this.packetHandler.getClientStream().finishPacket();
                    break;
                }
                case WALL_EXAMINE: {
                    this.showMessage(false, null, EntityHandler.getDoorDef(indexOrX).getDescription(),
                            MessageType.GAME, 0, null);
                    break;
                }
                case OBJECT_CAST_SPELL: {
                    this.walkToObject(indexOrX, idOrZ, dir, 5126, tileID);
                    this.packetHandler.getClientStream().newPacket(99);
                    this.packetHandler.getClientStream().writeBuffer1.putShort(var8);
                    this.packetHandler.getClientStream().writeBuffer1.putShort(indexOrX + this.midRegionBaseX);
                    this.packetHandler.getClientStream().writeBuffer1.putShort(this.midRegionBaseZ + idOrZ);

                    this.packetHandler.getClientStream().finishPacket();
                    this.selectedSpell = -1;
                    break;
                }
                case OBJECT_USE_ITEM: {
                    this.walkToObject(indexOrX, idOrZ, dir, 5126, tileID);
                    this.packetHandler.getClientStream().newPacket(115);
                    this.packetHandler.getClientStream().writeBuffer1.putShort(indexOrX + this.midRegionBaseX);
                    this.packetHandler.getClientStream().writeBuffer1.putShort(idOrZ + this.midRegionBaseZ);
                    this.packetHandler.getClientStream().writeBuffer1.putShort(var8);
                    this.packetHandler.getClientStream().finishPacket();
                    this.selectedItemInventoryIndex = -1;
                    break;
                }
                case OBJECT_COMMAND1: {
                    this.walkToObject(indexOrX, idOrZ, dir, 5126, tileID);
                    this.packetHandler.getClientStream().newPacket(136);
                    this.packetHandler.getClientStream().writeBuffer1.putShort(this.midRegionBaseX + indexOrX);
                    this.packetHandler.getClientStream().writeBuffer1.putShort(idOrZ + this.midRegionBaseZ);
                    this.packetHandler.getClientStream().finishPacket();
                    break;
                }
                case OBJECT_COMMAND2: {
                    this.walkToObject(indexOrX, idOrZ, dir, 5126, tileID);
                    this.packetHandler.getClientStream().newPacket(79);
                    this.packetHandler.getClientStream().writeBuffer1.putShort(this.midRegionBaseX + indexOrX);
                    this.packetHandler.getClientStream().writeBuffer1.putShort(this.midRegionBaseZ + idOrZ);
                    this.packetHandler.getClientStream().finishPacket();
                    break;
                }
                case OBJECT_EXAMINE: {
                    this.showMessage(false, null, EntityHandler.getObjectDef(indexOrX).getDescription(),
                            MessageType.GAME, 0, null);
                    break;
                }
                case ITEM_CAST_SPELL: {
                    this.packetHandler.getClientStream().newPacket(4);
                    this.packetHandler.getClientStream().writeBuffer1.putShort(idOrZ);
                    this.packetHandler.getClientStream().writeBuffer1.putShort(indexOrX);
                    this.packetHandler.getClientStream().finishPacket();
                    if (selectedSpell != 16) {
                        showUiTab = 4;
                    }
                    this.selectedSpell = -1;
                    break;
                }
                case ITEM_USE_ITEM: {
                    this.packetHandler.getClientStream().newPacket(91);
                    this.packetHandler.getClientStream().writeBuffer1.putShort(indexOrX);
                    this.packetHandler.getClientStream().writeBuffer1.putShort(idOrZ);
                    this.packetHandler.getClientStream().finishPacket();
                    this.selectedItemInventoryIndex = -1;
                    break;
                }
                case ITEM_REMOVE_EQUIPPED: {
                    this.packetHandler.getClientStream().newPacket(170);
                    this.packetHandler.getClientStream().writeBuffer1.putShort(indexOrX);
                    this.packetHandler.getClientStream().finishPacket();
                    break;
                }
                case ITEM_EQUIP: {
                    this.packetHandler.getClientStream().newPacket(169);
                    this.packetHandler.getClientStream().writeBuffer1.putShort(indexOrX);
                    this.packetHandler.getClientStream().finishPacket();
                    break;
                }
                case ITEM_COMMAND: {
                    this.packetHandler.getClientStream().newPacket(90);
                    this.packetHandler.getClientStream().writeBuffer1.putShort(indexOrX);
                    this.packetHandler.getClientStream().finishPacket();
                    break;
                }
                case ITEM_COMMAND_ALL: {
                    int commandQuantity = getInventoryCount(inventoryItemID[indexOrX]);
                    this.packetHandler.getClientStream().newPacket(90);
                    this.packetHandler.getClientStream().writeBuffer1.putShort(indexOrX);
                    this.packetHandler.getClientStream().writeBuffer1.putInt(commandQuantity);
                    this.packetHandler.getClientStream().finishPacket();
                    break;
                }
                case ITEM_USE: {
                    this.selectedItemInventoryIndex = indexOrX;
                    if (!isAndroid())
                        this.showUiTab = 0;
                    this.m_ig = EntityHandler.getItemDef(this.inventoryItemID[this.selectedItemInventoryIndex]).getName();
                    break;
                }
                case ITEM_DROP: {
                    this.packetHandler.getClientStream().newPacket(246);
                    this.packetHandler.getClientStream().writeBuffer1.putShort(indexOrX);
                    int amount = 1;
                    if (EntityHandler.getItemDef(inventoryItemID[indexOrX]).isStackable())
                        amount = getInventoryCount(this.inventoryItemID[indexOrX]);
                    this.packetHandler.getClientStream().writeBuffer1.putInt(amount);
                    this.packetHandler.getClientStream().finishPacket();
                    if (!isAndroid())
                        this.showUiTab = 0;
                    this.selectedItemInventoryIndex = -1;
                    this.showMessage(false, null,
                            "Dropping " + EntityHandler.getItemDef(this.inventoryItemID[indexOrX]).getName(),
                            MessageType.INVENTORY, 0, null);
                    break;
                }
                case ITEM_DROP_X: {
                    dropInventorySlot = indexOrX;
                    this.showItemModX(InputXPrompt.dropX, InputXAction.DROP_X, true);
                    break;
                }
                case ITEM_DROP_ALL: {
                    dropInventorySlot = indexOrX;
                    int dropQuantity = getInventoryCount(inventoryItemID[dropInventorySlot]);
                    this.packetHandler.getClientStream().newPacket(246);
                    this.packetHandler.getClientStream().writeBuffer1.putShort(dropInventorySlot);
                    this.packetHandler.getClientStream().writeBuffer1.putInt(dropQuantity);
                    this.packetHandler.getClientStream().finishPacket();
                    if (!isAndroid())
                        this.showUiTab = 0;
                    this.selectedItemInventoryIndex = this.dropInventorySlot = -1;
                    if (dropQuantity == 1)
                        this.showMessage(false, null,
                                "Dropping " + EntityHandler.getItemDef(this.inventoryItemID[indexOrX]).getName(),
                                MessageType.INVENTORY, 0, null);
                    break;
                }
                case NPC_CAST_SPELL: {
                    character = this.getServerNPC(indexOrX);
                    if (character == null) {
                        return;
                    }
                    cTileX = (character.currentX - 64) / this.tileSize;
                    cTileZ = (character.currentZ - 64) / this.tileSize;
                    this.walkToActionSource(this.playerLocalX, this.playerLocalZ, cTileX, cTileZ, true);
                    this.packetHandler.getClientStream().newPacket(50);
                    this.packetHandler.getClientStream().writeBuffer1.putShort(idOrZ);
                    this.packetHandler.getClientStream().writeBuffer1.putShort(indexOrX);

                    this.packetHandler.getClientStream().finishPacket();
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
                    this.walkToActionSource(this.playerLocalX, this.playerLocalZ, cTileX, cTileZ, true);
                    this.packetHandler.getClientStream().newPacket(135);
                    this.packetHandler.getClientStream().writeBuffer1.putShort(indexOrX);
                    this.packetHandler.getClientStream().writeBuffer1.putShort(idOrZ);
                    this.packetHandler.getClientStream().finishPacket();
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
                    this.walkToActionSource(this.playerLocalX, this.playerLocalZ, cTileX, cTileZ, true);
                    this.packetHandler.getClientStream().newPacket(153);
                    this.packetHandler.getClientStream().writeBuffer1.putShort(indexOrX);
                    this.packetHandler.getClientStream().finishPacket();
                    break;
                }
                case NPC_COMMAND1: {
                    character = this.getServerNPC(indexOrX);
                    if (character == null) {
                        return;
                    }
                    cTileX = (character.currentX - 64) / this.tileSize;
                    cTileZ = (character.currentZ - 64) / this.tileSize;
                    this.walkToActionSource(this.playerLocalX, this.playerLocalZ, cTileX, cTileZ, true);
                    this.packetHandler.getClientStream().newPacket(202);
                    this.packetHandler.getClientStream().writeBuffer1.putShort(indexOrX);
                    this.packetHandler.getClientStream().finishPacket();
                    break;
                }
                case NPC_COMMAND2: {
                    character = this.getServerNPC(indexOrX);
                    if (character == null) {
                        return;
                    }
                    cTileX = (character.currentX - 64) / this.tileSize;
                    cTileZ = (character.currentZ - 64) / this.tileSize;
                    this.walkToActionSource(this.playerLocalX, this.playerLocalZ, cTileX, cTileZ, true);
                    this.packetHandler.getClientStream().newPacket(203);
                    this.packetHandler.getClientStream().writeBuffer1.putShort(indexOrX);
                    this.packetHandler.getClientStream().finishPacket();
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
                    this.walkToActionSource(this.playerLocalX, this.playerLocalZ, cTileX, cTileZ, true);
                    this.packetHandler.getClientStream().newPacket(190);
                    this.packetHandler.getClientStream().writeBuffer1.putShort(indexOrX);
                    this.packetHandler.getClientStream().finishPacket();
                    break;
                }
                case NPC_EXAMINE: {
                    this.showMessage(false, null, EntityHandler.getNpcDef(indexOrX).getDescription(),
                            MessageType.GAME, 0, null);
                    break;
                }
                case PLAYER_CAST_SPELL: {
                    character = this.getServerPlayer(indexOrX);
                    if (character == null) {
                        return;
                    }
                    cTileX = (character.currentX - 64) / this.tileSize;
                    cTileZ = (character.currentZ - 64) / this.tileSize;
                    this.walkToActionSource(this.playerLocalX, this.playerLocalZ, cTileX, cTileZ, true);
                    this.packetHandler.getClientStream().newPacket(229);
                    this.packetHandler.getClientStream().writeBuffer1.putShort(idOrZ);
                    this.packetHandler.getClientStream().writeBuffer1.putShort(indexOrX);

                    this.packetHandler.getClientStream().finishPacket();
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
                    this.walkToActionSource(this.playerLocalX, this.playerLocalZ, cTileX, cTileZ, true);
                    this.packetHandler.getClientStream().newPacket(113);
                    this.packetHandler.getClientStream().writeBuffer1.putShort(indexOrX);
                    this.packetHandler.getClientStream().writeBuffer1.putShort(idOrZ);
                    this.packetHandler.getClientStream().finishPacket();
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
                    this.walkToActionSource(this.playerLocalX, this.playerLocalZ, cTileX, cTileZ, true);
                    this.packetHandler.getClientStream().newPacket(171);
                    this.packetHandler.getClientStream().writeBuffer1.putShort(indexOrX);
                    this.packetHandler.getClientStream().finishPacket();
                    break;
                }
                case PLAYER_DUEL: {
                    this.packetHandler.getClientStream().newPacket(103);
                    this.packetHandler.getClientStream().writeBuffer1.putShort(indexOrX);
                    this.packetHandler.getClientStream().finishPacket();
                    break;
                }
                case PLAYER_TRADE: {
                    this.packetHandler.getClientStream().newPacket(142);
                    this.packetHandler.getClientStream().writeBuffer1.putShort(indexOrX);
                    this.packetHandler.getClientStream().finishPacket();
                    break;
                }
                case PLAYER_FOLLOW: {
                    this.packetHandler.getClientStream().newPacket(165);
                    this.packetHandler.getClientStream().writeBuffer1.putShort(indexOrX);
                    this.packetHandler.getClientStream().finishPacket();
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
                    this.walkToActionSource(this.playerLocalX, this.playerLocalZ, indexOrX, idOrZ, true);
                    this.packetHandler.getClientStream().newPacket(158);
                    this.packetHandler.getClientStream().writeBuffer1.putShort(dir);
                    this.packetHandler.getClientStream().writeBuffer1.putShort(indexOrX + this.midRegionBaseX);
                    this.packetHandler.getClientStream().writeBuffer1.putShort(this.midRegionBaseZ + idOrZ);

                    this.packetHandler.getClientStream().finishPacket();
                    this.selectedSpell = -1;
                    break;
                }
                case LANDSCAPE_WALK_HERE: {
                    //System.out.println("LANDSCAPE_WALK_HERE: playerLocalX=" + this.playerLocalX + ", playerLocalZ= " + this.playerLocalZ + ", indexOrX=" + indexOrX + ", idOrZ=" + idOrZ);
                    this.walkToActionSource(this.playerLocalX, this.playerLocalZ, indexOrX, idOrZ, false);
                    if (this.mouseClickXStep == -24) {
                        this.mouseClickXStep = 24;
                    }
                    break;
                }
                case SELF_CAST_SPELL: {
                    this.packetHandler.getClientStream().newPacket(137);
                    this.packetHandler.getClientStream().writeBuffer1.putShort(indexOrX);
                    this.packetHandler.getClientStream().finishPacket();
                    this.selectedSpell = -1;
                    break;
                }
                case CANCEL: {
                    this.selectedSpell = -1;
                    this.selectedItemInventoryIndex = -1;
                    break;
                }
                case DEV_ADD_NPC: {
                    sendCommandString("cnpc " + devMenuNpcID + " 1 " + (indexOrX + midRegionBaseX) + " "
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
                    sendCommandString("robject " + (indexOrX + midRegionBaseX) + " "
                            + (idOrZ + midRegionBaseZ) + "");
                    break;
                }
                case DEV_ROTATE_OBJECT: {
                    sendCommandString("rotateobject " + (indexOrX + midRegionBaseX) + " "
                            + (idOrZ + midRegionBaseZ) + "");
                    break;
                }
                case MOD_SUMMON_PLAYER: {
                    String playerName = var9;
                    playerName = playerName.replaceAll(" ", "_");
                    sendCommandString("summon " + playerName);
                    break;
                }
                case MOD_RETURN_PLAYER: {
                    String playerName = var9;
                    playerName = playerName.replaceAll(" ", "_");
                    sendCommandString("return " + playerName);
                    break;
                }
                case MOD_RELEASE_PLAYER_JAIL: {
                    String playerName = var9;
                    playerName = playerName.replaceAll(" ", "_");
                    sendCommandString("release " + playerName);
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
                    sendCommandString("jail " + playerName);
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
                    String[] kickMessage = new String[]{"Are you sure you want to kick " + playerName + " from clan?"};
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

    private void handleReportAbuseClick() {
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

            int yFromTopDistance = 156;
            int color;
            if (var2) {
                var2 = false;

                for (color = 0; color < 6; ++color) {
                    int var5 = color == 0 ? 30 : 18;
                    if (this.mouseY > yFromTopDistance - 12 && this.mouseY < var5 + yFromTopDistance - 12) {
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

                    yFromTopDistance += 2 + var5;
                }
            }

            if (!var2) {
                this.reportAbuse_AbuseType = 0;
            }

            if (this.mouseButtonClick != 0 && this.reportAbuse_AbuseType != 0) {
                this.packetHandler.getClientStream().newPacket(206);
                this.packetHandler.getClientStream().writeBuffer1.putString(this.reportAbuse_Name);
                this.packetHandler.getClientStream().writeBuffer1.putByte(this.reportAbuse_AbuseType);
                this.packetHandler.getClientStream().writeBuffer1.putByte(this.reportAbuse_isMute ? 1 : 0);
                this.packetHandler.getClientStream().finishPacket();
                this.reportAbuse_State = 0;
                this.inputTextFinal = "";
                this.inputTextCurrent = "";
                this.mouseButtonClick = 0;
            } else {
                yFromTopDistance += 15;
                if (this.mouseButtonClick != 0) {
                    this.mouseButtonClick = 0;
                    if (this.mouseX < 31 || this.mouseY < 35 || this.mouseX > 481 || this.mouseY > 310) {
                        this.reportAbuse_State = 0;
                        return;
                    }

                    if (this.mouseX > 66 && this.mouseX < 446 && this.mouseY >= yFromTopDistance - 15 && this.mouseY < yFromTopDistance + 5) {
                        this.reportAbuse_State = 0;
                        return;
                    }
                }

                this.getSurface().drawBox(31, 35, 450, 275, 0);
                this.getSurface().drawBoxBorder(31, 450, 35, 275, 0xFFFFFF);
                byte var7 = 50;
                this.getSurface().drawColoredStringCentered(256,
                        "This form is for reporting players who are breaking our rules", 0xFFFFFF, 0, 1, var7);
                yFromTopDistance = var7 + 15;
                this.getSurface().drawColoredStringCentered(256,
                        "Using it sends a snapshot of the last 60 seconds of activity to us", 0xFFFFFF, 0, 1, yFromTopDistance);
                yFromTopDistance += 15;
                this.getSurface().drawColoredStringCentered(256, "If you misuse this form, you will be banned.",
                        16744448, 0, 1, yFromTopDistance);
                yFromTopDistance += 15;
                yFromTopDistance += 10;
                this.getSurface().drawColoredStringCentered(256,
                        "Click on the most suitable option from the Rules of " + SERVER_NAME + ".", 0xFFFF00, 0, 1, yFromTopDistance);
                yFromTopDistance += 15;
                this.getSurface().drawColoredStringCentered(256,
                        "This will send a report to our Player Support team for investigation.", 0xFFFF00, 0, 1, yFromTopDistance);
                yFromTopDistance += 18;
                this.getSurface().drawColoredStringCentered(106, "Honour", 0xFF0000, 0, 4, yFromTopDistance);
                this.getSurface().drawColoredStringCentered(256, "Respect", 0xFF0000, 0, 4, yFromTopDistance);
                this.getSurface().drawColoredStringCentered(406, "Security", 0xFF0000, 0, 4, yFromTopDistance);
                yFromTopDistance += 18;
                if (this.reportAbuse_AbuseType == 1) {
                    this.getSurface().drawBox(36, yFromTopDistance - 12, 140, 30, 3158064);
                }

                this.getSurface().drawBoxBorder(36, 140, yFromTopDistance - 12, 30, 4210752);
                if (this.reportAbuse_AbuseType == 7) {
                    this.getSurface().drawBox(186, yFromTopDistance - 12, 140, 30, 3158064);
                }

                this.getSurface().drawBoxBorder(186, 140, yFromTopDistance - 12, 30, 4210752);
                if (this.reportAbuse_AbuseType == 12) {
                    this.getSurface().drawBox(336, yFromTopDistance - 12, 140, 30, 3158064);
                }

                this.getSurface().drawBoxBorder(336, 140, yFromTopDistance - 12, 30, 4210752);
                if (this.reportAbuse_AbuseType == 1) {
                    color = 16744448;
                } else {
                    color = 0xFFFFFF;
                }

                this.getSurface().drawColoredStringCentered(106, "Buying or", color, 0, 0, yFromTopDistance);
                if (this.reportAbuse_AbuseType == 7) {
                    color = 16744448;
                } else {
                    color = 0xFFFFFF;
                }

                this.getSurface().drawColoredStringCentered(256, "Seriously offensive", color, 0, 0, yFromTopDistance);
                if (this.reportAbuse_AbuseType != 12) {
                    color = 0xFFFFFF;
                } else {
                    color = 16744448;
                }

                this.getSurface().drawColoredStringCentered(406, "Asking for or providing", color, 0, 0, yFromTopDistance);
                yFromTopDistance += 12;
                if (this.reportAbuse_AbuseType != 1) {
                    color = 0xFFFFFF;
                } else {
                    color = 16744448;
                }

                this.getSurface().drawColoredStringCentered(106, "selling an account", color, 0, 0, yFromTopDistance);
                if (this.reportAbuse_AbuseType != 7) {
                    color = 0xFFFFFF;
                } else {
                    color = 16744448;
                }

                this.getSurface().drawColoredStringCentered(256, "language", color, 0, 0, yFromTopDistance);
                if (this.reportAbuse_AbuseType != 12) {
                    color = 0xFFFFFF;
                } else {
                    color = 16744448;
                }

                this.getSurface().drawColoredStringCentered(406, "contact information", color, 0, 0, yFromTopDistance);
                yFromTopDistance += 20;
                if (this.reportAbuse_AbuseType == 2) {
                    this.getSurface().drawBox(36, yFromTopDistance - 12, 140, 18, 3158064);
                }

                this.getSurface().drawBoxBorder(36, 140, yFromTopDistance - 12, 18, 4210752);
                if (this.reportAbuse_AbuseType == 8) {
                    this.getSurface().drawBox(186, yFromTopDistance - 12, 140, 18, 3158064);
                }

                this.getSurface().drawBoxBorder(186, 140, yFromTopDistance - 12, 18, 4210752);
                if (this.reportAbuse_AbuseType == 13) {
                    this.getSurface().drawBox(336, yFromTopDistance - 12, 140, 18, 3158064);
                }

                this.getSurface().drawBoxBorder(336, 140, yFromTopDistance - 12, 18, 4210752);
                if (this.reportAbuse_AbuseType == 2) {
                    color = 16744448;
                } else {
                    color = 0xFFFFFF;
                }

                this.getSurface().drawColoredStringCentered(106, "Encouraging rule-breaking", color, 0, 0, yFromTopDistance);
                if (this.reportAbuse_AbuseType == 8) {
                    color = 16744448;
                } else {
                    color = 0xFFFFFF;
                }

                this.getSurface().drawColoredStringCentered(256, "Solicitation", color, 0, 0, yFromTopDistance);
                if (this.reportAbuse_AbuseType != 13) {
                    color = 0xFFFFFF;
                } else {
                    color = 16744448;
                }

                this.getSurface().drawColoredStringCentered(406, "Breaking real-world laws", color, 0, 0, yFromTopDistance);
                yFromTopDistance += 20;
                if (this.reportAbuse_AbuseType == 3) {
                    this.getSurface().drawBox(36, yFromTopDistance - 12, 140, 18, 3158064);
                }

                this.getSurface().drawBoxBorder(36, 140, yFromTopDistance - 12, 18, 4210752);
                if (this.reportAbuse_AbuseType == 9) {
                    this.getSurface().drawBox(186, yFromTopDistance - 12, 140, 18, 3158064);
                }

                this.getSurface().drawBoxBorder(186, 140, yFromTopDistance - 12, 18, 4210752);
                if (this.reportAbuse_AbuseType == 14) {
                    this.getSurface().drawBox(336, yFromTopDistance - 12, 140, 18, 3158064);
                }

                this.getSurface().drawBoxBorder(336, 140, yFromTopDistance - 12, 18, 4210752);
                if (this.reportAbuse_AbuseType == 3) {
                    color = 16744448;
                } else {
                    color = 0xFFFFFF;
                }

                this.getSurface().drawColoredStringCentered(106, "Staff impersonation", color, 0, 0, yFromTopDistance);
                if (this.reportAbuse_AbuseType != 9) {
                    color = 0xFFFFFF;
                } else {
                    color = 16744448;
                }

                this.getSurface().drawColoredStringCentered(256, "Disruptive behaviour", color, 0, 0, yFromTopDistance);
                if (this.reportAbuse_AbuseType != 14) {
                    color = 0xFFFFFF;
                } else {
                    color = 16744448;
                }

                this.getSurface().drawColoredStringCentered(406, "Advertising websites", color, 0, 0, yFromTopDistance);
                yFromTopDistance += 20;
                if (this.reportAbuse_AbuseType == 4) {
                    this.getSurface().drawBox(36, yFromTopDistance - 12, 140, 18, 3158064);
                }

                this.getSurface().drawBoxBorder(36, 140, yFromTopDistance - 12, 18, 4210752);
                if (this.reportAbuse_AbuseType == 10) {
                    this.getSurface().drawBox(186, yFromTopDistance - 12, 140, 18, 3158064);
                }

                this.getSurface().drawBoxBorder(186, 140, yFromTopDistance - 12, 18, 4210752);
                if (this.reportAbuse_AbuseType != 4) {
                    color = 0xFFFFFF;
                } else {
                    color = 16744448;
                }

                this.getSurface().drawColoredStringCentered(106, "Macroing or use of bots", color, 0, 0, yFromTopDistance);
                if (this.reportAbuse_AbuseType == 10) {
                    color = 16744448;
                } else {
                    color = 0xFFFFFF;
                }

                this.getSurface().drawColoredStringCentered(256, "Offensive account name", color, 0, 0, yFromTopDistance);
                yFromTopDistance += 20;
                if (this.reportAbuse_AbuseType == 5) {
                    this.getSurface().drawBox(36, yFromTopDistance - 12, 140, 18, 3158064);
                }

                this.getSurface().drawBoxBorder(36, 140, yFromTopDistance - 12, 18, 4210752);
                if (this.reportAbuse_AbuseType == 11) {
                    this.getSurface().drawBox(186, yFromTopDistance - 12, 140, 18, 3158064);
                }

                this.getSurface().drawBoxBorder(186, 140, yFromTopDistance - 12, 18, 4210752);
                if (this.reportAbuse_AbuseType == 5) {
                    color = 16744448;
                } else {
                    color = 0xFFFFFF;
                }

                this.getSurface().drawColoredStringCentered(106, "Scamming", color, 0, 0, yFromTopDistance);
                if (this.reportAbuse_AbuseType != 11) {
                    color = 0xFFFFFF;
                } else {
                    color = 16744448;
                }

                this.getSurface().drawColoredStringCentered(256, "Real-life threats", color, 0, 0, yFromTopDistance);
                yFromTopDistance += 20;
                if (this.reportAbuse_AbuseType == 6) {
                    this.getSurface().drawBox(36, yFromTopDistance - 12, 140, 18, 3158064);
                }

                this.getSurface().drawBoxBorder(36, 140, yFromTopDistance - 12, 18, 4210752);
                if (this.reportAbuse_AbuseType != 6) {
                    color = 0xFFFFFF;
                } else {
                    color = 16744448;
                }

                this.getSurface().drawColoredStringCentered(106, "Exploiting a bug", color, 0, 0, yFromTopDistance);
                yFromTopDistance += 18;
                yFromTopDistance += 15;
                color = 0xFFFFFF;
                if (this.mouseX > 196 && this.mouseX < 316 && this.mouseY > yFromTopDistance - 15 && this.mouseY < 5 + yFromTopDistance) {
                    color = 0xFFFF00;
                }

                this.getSurface().drawColoredStringCentered(256, "Click here to cancel", color, 0, 1, yFromTopDistance);
            }
        } catch (RuntimeException var6) {
            throw GenUtil.makeThrowable(var6, "client.ID(" + "dummy" + ')');
        }
    }

    private void handleTabUIClick() {
        try {
            if (this.showUiTab == 0 && this.mouseX >= this.getSurface().width2 - 35 && this.mouseY >= 3
                    && this.mouseX < this.getSurface().width2 - 3 && this.mouseY < 35) {
                this.showUiTab = 1;
            }

            if (this.showUiTab == 0 && this.mouseX >= this.getSurface().width2 - 35 - 33 && this.mouseY >= 3
                    && this.getSurface().width2 - 3 - 33 > this.mouseX && this.mouseY < 35) {
                this.showUiTab = 2;
                this.minimapRandom_1 = (int) (13.0D * Math.random()) - 6; // random rotation of the minimap as anti-bot?
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
                this.minimapRandom_2 = (int) (23.0D * Math.random()) - 11; // random rotation of the minimap as anti-bot?
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

    private boolean hasRunes(int rune, int count) {
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
            return this.getInventoryCount(rune) >= count;
        } catch (RuntimeException var5) {
            throw GenUtil.makeThrowable(var5, "client.OA(" + "dummy" + ',' + count + ',' + rune + ')');
        }
    }

    private boolean isEquipped(int id) {
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

    private void jumpToLogin() {
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
        File uID = new File(F_CACHE_DIR + File.separator + "uid.dat");
        try {
            PrintWriter printWriter;
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
            //e.printStackTrace(); // Localhost often causes an error to be printed, not important to see
        }
        return 0L;
    }

    /*private String getMacAddress() {
        try {
            InetAddress a = InetAddress.getLocalHost();
            NetworkInterface n = NetworkInterface.getByInetAddress(a);
            byte[] m = n.getHardwareAddress();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < m.length; i++) {
                sb.append(String.format("%02X%s", m[i], (i < m.length - 1) ? "-" : ""));
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "failed";
    }*/

    private void loadSprites() {
        clientPort.showLoadingProgress(30, "people and monsters");
        //Load all sprites from the disk
        try {
            getSurface().fillSpriteTree();
        } catch (Exception e) {
            System.out.println(e);
        }

        //Load & apply sprite packs
        File configFile = new File(clientPort.getCacheLocation());
        if (configFile.isDirectory()) {
            configFile = new File(clientPort.getCacheLocation() + File.separator + "config.txt");
            if (configFile.exists()) {
                ArrayList<String> activePacks = new ArrayList<>();
                try {
                    BufferedReader br = new BufferedReader(new FileReader(configFile));
                    String line;
                    while ((line = br.readLine()) != null) {
                        String[] packageName = line.split(":");
                        if (Integer.parseInt(packageName[1]) == 1)
                            activePacks.add(packageName[0]);
                    }
                    br.close();

                    for (String filename : activePacks) {
                        ZipFile spritePack = new ZipFile(clientPort.getCacheLocation() + File.separator + filename + ".pack");
                        Enumeration<? extends ZipEntry> entries = spritePack.entries();
                        //Loop through each spritesheet in the sprite pack

                        while (entries.hasMoreElements()) {
                            List<Sprite> spriteGroup;
                            ZipEntry entry = entries.nextElement();
                            spriteGroup = GraphicsController.unpackSpriteData(spritePack, entry);
                            List<Sprite> defaultSprites = getSurface().spriteTree.get(entry.getName());
                            for (Sprite sprite : spriteGroup) {
                                for (int i = 0; i < defaultSprites.size(); i++) {
                                    if (sprite.getID() == defaultSprites.get(i).getID()) {
                                        defaultSprites.set(i, sprite);
                                        break;
                                    }
                                }
                            }
                            getSurface().spriteTree.replace(entry.getName(), defaultSprites);
                        }

                    }
                } catch (IOException a) {
                    a.printStackTrace();
                }
            }

        }

        //Create a hashmap of the animation names to their index in the sprite tree
        getSurface().mapAnimations();
    }

    private void loadEntitiesAuthentic() {
        clientPort.showLoadingProgress(30, "people and monsters");

        int animationNumber = 0;
        label0:
        for (int animationIndex = 0; animationIndex < EntityHandler.animationCount(); animationIndex++) {
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

    private void loadGameConfig(boolean var1) {
        try {
            clientPort.showLoadingProgress(1, "Loading Configuration");
            EntityHandler.load(MEMBER_WORLD);
        } catch (RuntimeException var3) {
            throw GenUtil.makeThrowable(var3, "client.CE(" + var1 + ')');
        }
    }

    private void loadMediaAuthentic() {
        clientPort.showLoadingProgress(20, "2d graphics");
        loadSprite(spriteMedia, "media", 1);
        loadSprite(spriteMedia + 1, "media", 6);
        loadSprite(spriteMedia + 9, "media", 1);
        loadSprite(spriteMedia + 10, "media", 1);
        //getSurface().spriteVerts[2] = getSurface().sprites[spriteMedia + 10];
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
        loadSprite(spriteMedia + 30, "media", 1);
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

    private void loadModels() {
        byte[] models = unpackData("models.orsc", "3d models", 60);

        String[] modelNames = {"torcha2", "torcha3", "torcha4", "skulltorcha2", "skulltorcha3", "skulltorcha4",
                "firea2", "firea3", "fireplacea2", "fireplacea3", "firespell2", "firespell3", "lightning2",
                "lightning3", "clawspell2", "clawspell3", "clawspell4", "clawspell5", "spellcharge2", "spellcharge3"};
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

    public final boolean loadNextRegion(int wantZ, int wantX, boolean var3) {
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
                    this.getSurface().drawColoredStringCentered(256, "Loading... Please wait", 0xFFFFFF, 0, 1, 192);
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
                            this.getWorld().registerObjectDir(xTile, zTile, dir);
                            int xSize;
                            int zSize;
                            if (dir == 0 || dir == 4) {
                                xSize = EntityHandler.getObjectDef(objectID).getWidth();
                                zSize = EntityHandler.getObjectDef(objectID).getHeight();
                            } else {
                                xSize = EntityHandler.getObjectDef(objectID).getHeight();
                                zSize = EntityHandler.getObjectDef(objectID).getWidth();
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
                            this.getWorld().registerObjectDir(xTile, zTile, dir);
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
                        ORSCharacter var23 = this.players[i];
                        var23.currentX -= this.tileSize * baseDX;
                        var23.currentZ -= baseDZ * this.tileSize;

                        for (int j = 0; j <= var23.waypointIndexCurrent; ++j) {
                            var23.waypointsX[j] -= this.tileSize * baseDX;
                            var23.waypointsZ[j] -= baseDZ * this.tileSize;
                        }
                    }

                    for (int i = 0; i < this.npcCount; ++i) {
                        ORSCharacter var23 = this.npcs[i];
                        var23.currentZ -= this.tileSize * baseDZ;
                        var23.currentX -= this.tileSize * baseDX;

                        for (int j = 0; var23.waypointIndexCurrent >= j; ++j) {
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

    private void loadSounds() {
        try {
            File folder = new File(F_CACHE_DIR + System.getProperty("file.separator"));
            File[] listOfFiles = folder.listFiles();

            for (int i = 0; i < listOfFiles.length; i++)
                if (listOfFiles[i].isFile() && listOfFiles[i].getName().endsWith(".wav")) {
                    soundCache.put(listOfFiles[i].getName().toLowerCase(), listOfFiles[i]);
                }

            byte[] soundData = unpackData("sounds.mem", "Sound effects", 90);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    private void loadTextures() {
        clientPort.showLoadingProgress(50, "Textures");
        this.scene.setFrustum(0, 11, 7, EntityHandler.textureCount());
        for (int i = 0; i < EntityHandler.textureCount(); i++) {
            Sprite sprite = getSurface().spriteTree.get("textures").get(i);
            int length = sprite.getWidth() * sprite.getHeight();
            int[] pixels = sprite.getPixels();
            int[] ai1 = new int[32768];
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

    private void loadTexturesAuthentic() {
        clientPort.showLoadingProgress(50, "Textures");
        this.scene.setFrustum(0, 11, 7, EntityHandler.textureCount());
        for (int i = 0; i < EntityHandler.textureCount(); i++) {
            loadSprite(spriteTexture + i, "texture", 1);
            Sprite sprite = getSurface().sprites[spriteTexture + i];
            int length = sprite.getWidth() * sprite.getHeight();
            int[] pixels = sprite.getPixels();
            int[] ai1 = new int[32768];
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

    private void loadSprite(int id, String packageName, int amount) {
        for (int i = id; i < id + amount; i++) {
            if (!getSurface().loadSprite(i, packageName)) {
                errorLoadingData = true;
                return;
            }
        }
    }

    private void login(int var1, String pass, String user, boolean reconnecting) {
        try {

            this.m_Zb = 0;
            if (this.m_Zb > 0) {
                this.showLoginScreenStatus("Please wait...", "Connecting to server");

                try {
                    GenUtil.sleepShadow(2000L);
                } catch (Exception var14) {
                }

                this.showLoginScreenStatus("Sorry! The server is currently full.", "Please try again later");
            } else {
                while (this.autoLoginTimeout > 0) {
                    try {
                        this.setUsername(user);
                        this.password = pass;
                        //MiscFunctions.netbase_a(20, (byte) -5, pass);
                        if (this.getUsername().trim().length() == 0) {
                            this.showLoginScreenStatus("You must enter both a username",
                                    "and a password - Please try again");
                            return;
                        }

                        if (!reconnecting) {
                            this.showLoginScreenStatus("Please wait...", "Connecting to server");
                            if (isAndroid())
                                clientPort.closeKeyboard(); // close the keyboard if still open
                        } else {
                            this.drawTextBox("Attempting to re-establish", (byte) -64,
                                    "Connection lost! Please wait...");
                        }

                        if ((Config.SERVER_IP != null)) {
                            String ip = Config.SERVER_IP; // allows override if manually set in Config code
                            int port = Config.SERVER_PORT; // allows override if manually set in Config code
                            this.packetHandler.setClientStream(new Network_Socket(this.packetHandler.openSocket(port, ip), this.packetHandler));
                            this.packetHandler.getClientStream().m_d = MiscFunctions.maxReadTries;
                        } else {
                            String ip = Config.getServerIp(); // loads cached server IP addressed
                            int port = Config.getServerPort(); // loads cached port
                            this.packetHandler.setClientStream(new Network_Socket(this.packetHandler.openSocket(port, ip), this.packetHandler));
                            this.packetHandler.getClientStream().m_d = MiscFunctions.maxReadTries;
                        }

                        Math.random();
                        Math.random();
                        Math.random();
                        Math.random();

                        this.packetHandler.getClientStream().newPacket(0);
                        if (reconnecting) {
                            this.packetHandler.getClientStream().writeBuffer1.putByte(1);
                        } else {
                            this.packetHandler.getClientStream().writeBuffer1.putByte(0);
                        }
                        this.packetHandler.getClientStream().writeBuffer1.putInt(CLIENT_VERSION);
                        this.packetHandler.getClientStream().writeBuffer1.putString(getUsername());
                        // TODO: This strips special chars to underscore. We may want to in the future allow special chars.
                        this.packetHandler.getClientStream().writeBuffer1.putString(DataOperations.addCharacters(password, 20));

                        this.packetHandler.getClientStream().writeBuffer1.putLong(getUID());
                        //this.packetHandler.getClientStream().writeBuffer1.putString(getMacAddress());
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
                        this.packetHandler.getClientStream().finishPacketAndFlush();
                        // this.clientStream.seedIsaac(isaacSeed);
                        int loginResponse = this.packetHandler.getClientStream().read();

                        System.out.println("login response:" + loginResponse);
                        if ((loginResponse & 0x40) != 0) {
                            this.autoLoginTimeout = 0;
                            this.m_Ce = loginResponse & 0x3;
                            this.m_Oj = (loginResponse >> 2) & 0xf;
                            this.resetGame((byte) -123);
                        } else {
                            if (loginResponse == 1) {
                                this.autoLoginTimeout = 0;
                            } else {
                                if (!reconnecting) {
                                    if (loginResponse == -1) {
                                        this.showLoginScreenStatus("Error unable to login.", "Server timed out");
                                    } else if (loginResponse == 3) {
                                        this.showLoginScreenStatus("Invalid username or password.",
                                                "Try again, or create a new account");
                                    } else if (loginResponse != 4) {
                                        if (loginResponse == 5) {
                                            this.showLoginScreenStatus("The client has been updated.", "Please reload this page");
                                        } else if (loginResponse == 6) {
                                            this.showLoginScreenStatus("You may only use 1 character at once.",
                                                    "Your ip-address is already in use");
                                        } else if (loginResponse == 7) {
                                            this.showLoginScreenStatus("Login attempts exceeded!", "Please try again in 5 minutes");
                                        } else if (loginResponse == 8) {
                                            this.showLoginScreenStatus("Error unable to login.", "Server rejected session");
                                        } else if (loginResponse == 9) {
                                            this.showLoginScreenStatus("Error unable to login.",
                                                    "Under 13 accounts cannot access RuneScape Classic");
                                        } else if (loginResponse == 10) {
                                            this.showLoginScreenStatus("That username is already in use.",
                                                    "Wait 60 seconds then retry");
                                        } else if (loginResponse == 11) {
                                            this.showLoginScreenStatus("Account temporarily disabled.",
                                                    "Check your message inbox for details");
                                        } else if (loginResponse == 12) {
                                            this.showLoginScreenStatus("Account permanently disabled.",
                                                    "Check your message inbox for details");
                                        } else if (loginResponse == 14) {
                                            this.showLoginScreenStatus("Sorry! This world is currently full.",
                                                    "Please try a different world");
                                            this.m_Zb = 1500;
                                        } else if (loginResponse == 15) {
                                            this.showLoginScreenStatus("You need a members account", "to login to this world");
                                        } else if (loginResponse != 16) {
                                            if (loginResponse != 17) {
                                                if (loginResponse != 18) {
                                                    if (loginResponse == 20) {
                                                        this.showLoginScreenStatus("Error - loginserver mismatch",
                                                                "Please try a different world");
                                                    } else if (loginResponse == 21) {
                                                        this.showLoginScreenStatus("That is not a veteran RS-Classic account.",
                                                                "Please try a non-veterans world.");
                                                    } else if (loginResponse != 22) {
                                                        if (loginResponse == 23) {
                                                            this.showLoginScreenStatus("You need to set your display name.",
                                                                    "Please go to the Account Management page to do this.");
                                                        } else if (loginResponse != 24) {
                                                            if (loginResponse != 25) {
                                                                this.showLoginScreenStatus("Error unable to login.",
                                                                        "Unrecognised response code");
                                                            } else {
                                                                this.showLoginScreenStatus("None of your characters can log in.",
                                                                        "Contact customer support");
                                                            }
                                                        } else {
                                                            this.showLoginScreenStatus("This world does not accept new players.",
                                                                    "Please see the launch page for help");
                                                        }
                                                    } else {
                                                        this.showLoginScreenStatus("Password suspected stolen.",
                                                                "Press 'change your password' on front page.");
                                                    }
                                                } else {
                                                    this.showLoginScreenStatus("Account suspected stolen.",
                                                            "Press 'recover a locked account' on front page.");
                                                }
                                            } else {
                                                this.showLoginScreenStatus("Error - failed to decode profile.",
                                                        "Contact customer support");
                                            }
                                        } else {
                                            this.showLoginScreenStatus("Error - no reply from loginserver.", "Please try again");
                                        }
                                    } else {
                                        this.showLoginScreenStatus("That username is already logged in.",
                                                "Wait 60 seconds then retry");
                                    }
                                } else {
                                    this.setUsername("");
                                    this.jumpToLogin();
                                }
                            }
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
                                // GenUtil.reportError(var15, "Error
                                // while connecting");
                                this.showLoginScreenStatus("Sorry! Unable to connect.",
                                        "Check internet settings or try another world");
                            }
                        } else {
                            try {
                                GenUtil.sleepShadow(5000L);
                            } catch (Exception var12) {
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

    private void lostConnection(int var1) {
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

    public final void playSoundFile(String key) {
        try {
            if (!optionSoundDisabled) {
                File sound = soundCache.get(key + ".wav");
                if (sound == null)
                    return;
                try {
                    // PC sound code:
                    //Clip clip = AudioSystem.getClip();
                    //clip.open(AudioSystem.getAudioInputStream(sound));
                    //clip.start();

                    // Android sound code:
                    //int dataLength = DataOperations.getDataFileLength(key + ".pcm", soundData);
                    //int offset = DataOperations.getDataFileOffset(key + ".pcm", soundData);
                    //clientPort.playSound(soundData, offset, dataLength);

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

        } catch (RuntimeException var6) {
            throw GenUtil.makeThrowable(var6, "client.SC(" + "dummy" + ',' + (key != null ? "{...}" : "null") + ')');
        }
    }

    private void putStringPair(String str1, String str2) {
        try {
            this.packetHandler.getClientStream().newPacket(218);
            this.packetHandler.getClientStream().writeBuffer1.putString(str1);
            RSBufferUtils.putEncryptedString(this.packetHandler.getClientStream().writeBuffer1, str2);
            this.packetHandler.getClientStream().finishPacket();
        } catch (RuntimeException var5) {
            throw GenUtil.makeThrowable(var5, "client.KB(" + "dummy" + ',' + (str1 != null ? "{...}" : "null") + ','
                    + (str2 != null ? "{...}" : "null") + ')');
        }
    }

    private void removeFriend(String var1, byte var2) {
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

                        this.packetHandler.getClientStream().newPacket(167);
                        this.packetHandler.getClientStream().writeBuffer1.putNullThenString(var1, 110);
                        this.packetHandler.getClientStream().finishPacket();
                        break;
                    }
                }

            }
        } catch (RuntimeException var6) {
            throw GenUtil.makeThrowable(var6, "client.O(" + (var1 != null ? "{...}" : "null") + ',' + var2 + ')');
        }
    }

    private void removeIgnore(String name) {
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

                        this.packetHandler.getClientStream().newPacket(241);
                        this.packetHandler.getClientStream().writeBuffer1.putNullThenString(name, -78);
                        this.packetHandler.getClientStream().finishPacket();
                        break;
                    }
                }

            }
        } catch (RuntimeException var6) {
            throw GenUtil.makeThrowable(var6, "client.E(" + "dummy" + ',' + (name != null ? "{...}" : "null") + ')');
        }
    }

    private boolean worldComponentsLoaded = false;

    private void loadWorldComponents() {
        byte sector_h = 0; // sector h
        byte sector_x = 50; // sector x
        byte sector_y = 50; // sector y    (h0x50y50 - Lumbridge sector) (h0x50y39 - deep wilderness sector)
        this.world.loadSections(sector_x * 48 + 23, (sector_y * 48 + 23), sector_h);
        this.world.addLoginScreenModels(this.modelCache);
        this.worldComponentsLoaded = true;
    }

    private void renderLoginScreenViewports(int var1) {
        try {

            if (!this.worldComponentsLoaded) {
                this.loadWorldComponents();
            }

            // First view
            short slide_x = 9728;
            short zoom_distance = 1100;
            short slide_y = 6400;
            this.scene.fogLandscapeDistance = 10000;
            this.scene.fogEntityDistance = 10000;
            short rotation = 888;
            this.scene.fogZFalloff = 1;
            this.scene.fogSmoothingStartDistance = 10000;
            this.scene.setCamera(slide_x, -this.world.getElevation(slide_x, slide_y), slide_y, 912, rotation, 0, zoom_distance * 2);
            this.scene.endScene(-124);
            if (var1 >= -48) {
                this.localPlayer = null;
            }

            this.getSurface().fade2black(16316665);
            this.getSurface().fade2black(16316665);
            this.getSurface().drawBox(0, 0, getGameWidth(), 6, 0);

            int var9;
            for (var9 = 6; var9 >= 1; --var9) {
                this.getSurface().a(8, var9, var9, 0, 16740352, getGameWidth(), 0);
            }

            this.getSurface().drawBox(0, halfGameHeight() + 27, getGameWidth(), 20, 0);

            for (var9 = 6; var9 >= 1; --var9) {
                this.getSurface().a(8, var9, halfGameHeight() + 27 - var9, 0, 16740352, getGameWidth(), 0);
            }

            if (DISPLAY_LOGO_SPRITE)
                this.getSurface().drawSprite(spriteSelect(EntityHandler.GUIparts.get(GUIPARTS.MAINLOGO.id())), 15, 15);
            //this.getSurface().drawColoredStringCentered(250, "Open RSC", 0xFFFFFF, 0, 7, 110); // width, title, color, crown sprite, font size, height
            this.getSurface().storeSpriteVert(0, 0, 0, getGameWidth(), halfGameHeight() + 33);

            // Second view
            slide_y = 9216;
            rotation = 888;
            zoom_distance = 1100;
            slide_x = 9216;
            this.scene.fogLandscapeDistance = 10000;
            this.scene.fogZFalloff = 1;
            this.scene.fogSmoothingStartDistance = 10000;
            this.scene.fogEntityDistance = 10000;
            this.scene.setCamera(slide_x, -this.world.getElevation(slide_x, slide_y), slide_y, 912, rotation, 0, zoom_distance * 2);
            this.scene.endScene(-114);
            this.getSurface().fade2black(16316665);
            this.getSurface().fade2black(16316665);
            this.getSurface().drawBox(0, 0, getGameWidth(), 6, 0);

            for (var9 = 6; var9 >= 1; --var9) {
                this.getSurface().a(8, var9, var9, 0, 16740352, getGameWidth(), 0);
            }

            this.getSurface().drawBox(0, halfGameHeight() + 27, getGameWidth(), 20, 0);

            for (var9 = 6; var9 >= 1; --var9) {
                this.getSurface().a(8, var9, halfGameHeight() + 27 - var9, 0, 16740352, getGameWidth(), 0);
            }

            if (DISPLAY_LOGO_SPRITE)
                this.getSurface().drawSprite(spriteSelect(EntityHandler.GUIparts.get(GUIPARTS.MAINLOGO.id())), 15, 15);
            //this.getSurface().drawColoredStringCentered(250, "Open RSC", 0xFFFFFF, 0, 7, 110); // width, title, color, crown sprite, font size, height
            this.getSurface().storeSpriteVert(1, 0, 0, getGameWidth(), halfGameHeight() + 33);

            // Third view
            zoom_distance = 500;
            rotation = 376;
            slide_x = 11136;
            slide_y = 10368;

            for (var9 = 0; var9 < 64; ++var9) {
                this.scene.removeModel(this.world.modelRoofGrid[0][var9]);
                this.scene.removeModel(this.world.modelWallGrid[1][var9]);
                this.scene.removeModel(this.world.modelRoofGrid[1][var9]);
                this.scene.removeModel(this.world.modelWallGrid[2][var9]);
                this.scene.removeModel(this.world.modelRoofGrid[2][var9]);
            }

            this.scene.fogLandscapeDistance = 10000;
            this.scene.fogEntityDistance = 10000;
            this.scene.fogZFalloff = 1;
            this.scene.fogSmoothingStartDistance = 10000;
            this.scene.setCamera(slide_x, -this.world.getElevation(slide_x, slide_y), slide_y, 912, rotation, 0, zoom_distance * 2);
            this.scene.endScene(-111);

            this.getSurface().fade2black(16316665);
            this.getSurface().fade2black(16316665);
            this.getSurface().drawBox(0, 0, getGameWidth(), 6, 0);

            for (var9 = 6; var9 >= 1; --var9) {
                this.getSurface().a(8, var9, var9, 0, 16740352, getGameWidth(), 0);
            }

            this.getSurface().drawBox(0, halfGameHeight() + 27, getGameWidth(), 20, 0);

            for (var9 = 6; var9 >= 1; --var9) {
                this.getSurface().a(8, var9, halfGameHeight() + 27, 0, 16740352, getGameWidth(), 0);
            }

            if (DISPLAY_LOGO_SPRITE)
                this.getSurface().drawSprite(spriteSelect(EntityHandler.GUIparts.get(GUIPARTS.MAINLOGO.id())), 15, 15);
            //this.getSurface().drawColoredStringCentered(250, "Open RSC", 0xFFFFFF, 0, 7, 110); // width, title, color, crown sprite, font size, height
            this.getSurface().storeSpriteVert(2, 0, 0, getGameWidth(), halfGameHeight() + 33);
        } catch (RuntimeException var10) {
            throw GenUtil.makeThrowable(var10, "client.HC(" + var1 + ')');
        }

    }

    private void resetGame(int var1) {
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
            if (S_EXPERIENCE_DROPS_TOGGLE)
                experienceOverlay.setVisible(true);
            this.getSurface().blackScreen(true);
            // this.getSurface().draw(this.graphics, this.screenOffsetX, 256,
            // this.screenOffsetY);
            clientPort.draw();
            int i;
            for (i = 0; i < this.gameObjectInstanceCount; ++i) {
                this.scene.removeModel(this.gameObjectInstanceModel[i]);
                this.world.removeGameObject_CollisonFlags(this.gameObjectInstanceID[i],
                        this.gameObjectInstanceX[i], this.gameObjectInstanceZ[i]);
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
            this.achievementInterface.setVisible(false);
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

    private void resetLoginScreenVariables(byte var1) {
        try {
            this.npcCount = 0;
            this.playerCount = 0;
            this.loginScreenNumber = 0;
            if (var1 != -88) {
                this.teleportBubbleType = null;
            }

            this.currentViewMode = GameMode.LOGIN;


        } catch (RuntimeException var3) {
            throw GenUtil.makeThrowable(var3, "client.UA(" + var1 + ')');
        }
    }

    // This seems to be having trouble. The decryption function is known to work
    // and the encryption function is the mirror of it. Thus the problem is
    // confusing.
    private void sendChatMessage(String var1) {
        try {
            this.packetHandler.getClientStream().newPacket(216);
            RSBufferUtils.putEncryptedString(this.packetHandler.getClientStream().writeBuffer1, var1);
            this.packetHandler.getClientStream().finishPacket();
        } catch (RuntimeException var4) {
            throw GenUtil.makeThrowable(var4, "client.AA(" + (var1 != null ? "{...}" : "null") + ')');
        }
    }

    public final void sendCommandString(String var1) {
        try {
            this.packetHandler.getClientStream().newPacket(38);
            this.packetHandler.getClientStream().writeBuffer1.putString(var1);
            this.packetHandler.getClientStream().finishPacket();
        } catch (RuntimeException var4) {
            throw GenUtil.makeThrowable(var4, "client.UC(" + (var1 != null ? "{...}" : "null") + ',' + "dummy" + ')');
        }
    }

    private void sendLogout(int var1) {
        try {
            if (this.currentViewMode != GameMode.LOGIN) {
                if (this.combatTimeout <= 450) {
                    if (var1 < this.combatTimeout) {
                        this.showMessage(false, null, "You can\'t logout for 10 seconds after combat",
                                MessageType.GAME, 0, null, "@cya@");
                    } else {
                        this.packetHandler.getClientStream().newPacket(102);
                        this.packetHandler.getClientStream().finishPacket();
                        this.logoutTimeout = 1000;
                        // this.createLoginPanels(3845);
                    }
                } else {
                    this.showMessage(false, null, "You can\'t logout during combat!", MessageType.GAME, 0,
                            null, "@cya@");
                }
            }
        } catch (RuntimeException var3) {
            throw GenUtil.makeThrowable(var3, "client.T(" + var1 + ')');
        }
    }

    private boolean sendWalkToGroundItem(int startX, int startZ, int x1, int x2, int z1, int z2, boolean var4,
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
                    this.packetHandler.getClientStream().newPacket(187);
                } else {
                    this.packetHandler.getClientStream().newPacket(16);
                }

                --var10;
                this.packetHandler.getClientStream().writeBuffer1.putShort(this.midRegionBaseX + startX);
                this.packetHandler.getClientStream().writeBuffer1.putShort(this.midRegionBaseZ + startZ);
                if (var9 && var10 == -1 && (this.midRegionBaseX + startX) % 5 == 0) {
                    var10 = 0;
                }

                for (int var11 = var10; var11 >= 0 && var10 - 25 < var11; --var11) {
                    this.packetHandler.getClientStream().writeBuffer1.putByte(this.pathX[var11] - startX);
                    this.packetHandler.getClientStream().writeBuffer1.putByte(this.pathZ[var11] - startZ);
                }

                this.packetHandler.getClientStream().finishPacket();
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

    private void showItemModX(InputXAction action, String[] lines, boolean var4, String defaultText) {
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

    private void showLoginScreenStatus(String a, String b) {
        try {

            if (this.loginScreenNumber == 4) {
                this.panelRecovery.setText(this.instructPassRecovery1, a);
                this.panelRecovery.setText(this.instructPassRecovery2, b);
            }
            if (this.loginScreenNumber == 2) {
                if (b != null && b.length() >= 1) {
                    this.panelLogin.setText(this.controlLoginStatus1, a);
                    this.panelLogin.setText(this.controlLoginStatus2, b);
                } else {
                    this.panelLogin.setText(this.controlLoginStatus1, "");
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

    public final void showMessage(boolean crownEnabled, String sender, String message, MessageType type, int crownID, String formerName) {
        showMessage(crownEnabled, sender, message, type, crownID, formerName, null);
    }

    public final void showMessage(boolean crownEnabled, String sender, String message, MessageType type, int crownID,
                                  String formerName, String colourOverride) {
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

            String colour = null != colourOverride ? colourOverride : type.color;

            if (!crownEnabled)
                crownID = 0;

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

                if (C_MESSAGE_TAB_SWITCH) {
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
            MessageHistory.messageHistoryColor[0] = colour;
            String msg = colour + StringUtil.formatMessage(message, sender, type, colour);

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
                    this.panelMessageTabs.addToList(msg, false, 0, null, null,
                            this.panelMessageQuest);
                } else {
                    this.panelMessageTabs.addToList(msg, true, 0, null, null, this.panelMessageQuest);
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
                    + (formerName != null ? "{...}" : "null") + ',' + ')');
        }
    }

    public final void sortOnlineFriendsList() {
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

    private void startGame(byte var1) {
        try {
            this.fetchContainerSize();
            if (!clientPort.drawLoading(2)) {
                this.errorLoadingData = true;
            } else {
                RSBufferUtils.setStringEncryptor(RSBufferUtils.encryption);
                this.setExperienceArray();
                MiscFunctions.maxReadTries = 1000;
                // We must fetch the server config before continuing.
                if ((Config.SERVER_IP != null)) {
                    String ip = Config.SERVER_IP; // allows override if manually set in Config code
                    int port = Config.SERVER_PORT; // allows override if manually set in Config code
                    System.out.println(" ");
                    System.out.println(" ");
                    System.out.println("Fetching server configs from " + ip + ":" + port);
                    System.out.println(" ");
                    System.out.println(" ");
                } else {
                    String ip = ClientPort.loadIP(); // loads based on Cache/ip.txt
                    int port = ClientPort.loadPort(); // loads based on Cache/port.txt
                    System.out.println(" ");
                    System.out.println(" ");
                    System.out.println("Fetching server configs from " + ip + ":" + port);
                    System.out.println(" ");
                    System.out.println(" ");
                }
                this.getServerConfig();
            }
        } catch (RuntimeException var9) {
            throw GenUtil.makeThrowable(var9, "client.KC(" + var1 + ')');
        }
    }

    public void setExperienceArray(int[] a) {
        this.experienceArray = a;
    }

    public final void hideBatchProgressBar() {
        batchProgressBar.hide();
    }

    public final void showBatchProgressBar() {
        batchProgressBar.show();
    }

    public final void updateBatchProgressBar(int repeat) {
        batchProgressBar.updateProgress(repeat);
    }

    public final void resetBatchProgressBar() {
        batchProgressBar.resetProgressBar();
    }

    public final void initializeBatchProgressVariables(int repeatFor, int delay) {
        batchProgressBar.initVariables(repeatFor, delay);
    }

    public void showBankPinInterface() {
        bankPinInterface.show();
    }

    public void hideBankPinInterface() {
        bankPinInterface.hide();
    }

    public IronManInterface getIronmanInterface() {
        return ironmanInterface;
    }

    public AuctionHouse getAuctionHouse() {
        return auctionHouse;
    }

    public OnlineListInterface getOnlineList() {
        return onlineList;
    }

    public FishingTrawlerInterface getFishingTrawlerInterface() {
        return fishingTrawlerInterface;
    }

    public void setBlockChat(int status) {
        this.settingsBlockChat = status;
    }

    public void setBlockPrivate(int status) {
        this.settingsBlockPrivate = status;
    }

    public void setBlockTrade(int status) {
        this.settingsBlockTrade = status;
    }

    public void setBlockDuel(int status) {
        this.settingsBlockDuel = status;
    }

    public int getPlayerCount() {
        return this.playerCount;
    }

    public void setPlayerCount(int i) {
        this.playerCount = i;
    }

    public ORSCharacter getKnownPlayer(int i) {
        return this.knownPlayers[i];
    }

    public int getKnownPlayerCount() {
        return this.knownPlayerCount;
    }

    public void setKnownPlayerCount(int i) {
        this.knownPlayerCount = i;
    }

    public void setKnownPlayer(int i, ORSCharacter p) {
        this.knownPlayers[i] = p;
    }

    public void setPlayer(int i, ORSCharacter p) {
        this.players[i] = p;
    }

    public ORSCharacter getPlayer(int i) {
        return this.players[i];
    }

    public int getLocalPlayerX() {
        return this.playerLocalX;
    }

    public void setLocalPlayerX(int i) {
        this.playerLocalX = i;
    }

    public int getLocalPlayerZ() {
        return this.playerLocalZ;
    }

    public void setLocalPlayerZ(int i) {
        this.playerLocalZ = i;
    }

    public int getMidRegionBaseX() {
        return this.midRegionBaseX;
    }

    public int getMidRegionBaseZ() {
        return this.midRegionBaseZ;
    }

    public int getTileSize() {
        return this.tileSize;
    }

    public int getGameObjectInstanceCount() {
        return gameObjectInstanceCount;
    }

    public void setGameObjectInstanceCount(int i) {
        this.gameObjectInstanceCount = i;
    }

    public void setGameObjectInstanceX(int i, int n) {
        this.gameObjectInstanceX[i] = n;
    }

    public int getGameObjectInstanceX(int i) {
        return this.gameObjectInstanceX[i];
    }

    public void setGameObjectInstanceZ(int i, int n) {
        this.gameObjectInstanceZ[i] = n;
    }

    public int getGameObjectInstanceZ(int i) {
        return this.gameObjectInstanceZ[i];
    }

    public void setGameObjectInstanceModel(int i, RSModel m) {
        this.gameObjectInstanceModel[i] = m;
        this.gameObjectInstanceModel[i].key = i;
    }

    public RSModel getGameObjectInstanceModel(int i) {
        return this.gameObjectInstanceModel[i];
    }

    public void setGameObjectInstanceID(int i, int n) {
        this.gameObjectInstanceID[i] = n;
    }

    public int getGameObjectInstanceID(int i) {
        return this.gameObjectInstanceID[i];
    }

    public void setGameObjectInstanceDir(int i, int n) {
        this.gameObjectInstanceDir[i] = n;
    }

    public int getGameObjectInstanceDir(int i) {
        return this.gameObjectInstanceDir[i];
    }

    public int getWallObjectInstanceCount() {
        return this.wallObjectInstanceCount;
    }

    public void setWallObjectInstanceCount(int i) {
        this.wallObjectInstanceCount = i;
    }

    public void setWallObjectInstanceX(int i, int n) {
        this.wallObjectInstanceX[i] = n;
    }

    public int getWallObjectInstanceX(int i) {
        return this.wallObjectInstanceX[i];
    }

    public void setWallObjectInstanceZ(int i, int n) {
        this.wallObjectInstanceZ[i] = n;
    }

    public int getWallObjectInstanceZ(int i) {
        return this.wallObjectInstanceZ[i];
    }

    public void setWallObjectInstanceModel(int i, RSModel n) {
        this.wallObjectInstanceModel[i] = n;
        this.wallObjectInstanceModel[i].key = i + 10000;
    }

    public RSModel getWallObjectInstanceModel(int i) {
        return this.wallObjectInstanceModel[i];
    }

    public void setWallObjectInstanceDir(int i, int n) {
        this.wallObjectInstanceDir[i] = n;
    }

    public int getWallObjectInstanceDir(int i) {
        return this.wallObjectInstanceDir[i];
    }

    public void setWallObjectInstanceID(int i, int n) {
        this.wallObjectInstanceID[i] = n;
    }

    public int getWallObjectInstanceID(int i) {
        return this.wallObjectInstanceID[i];
    }

    public Scene getScene() {
        return this.scene;
    }

    public RSModel getModelCacheItem(int i) {
        return this.modelCache[i];
    }

    public void setInventoryItemID(int i, int n) {
        this.inventoryItemID[i] = n;
    }

    public int getInventoryItemID(int i) {
        return this.inventoryItemID[i];
    }

    public void setInventoryItemEquipped(int i, int n) {
        this.inventoryItemEquipped[i] = n;
    }

    public void setInventoryItemSize(int i, int n) {
        this.inventoryItemSize[i] = n;
    }

    public int getInventoryItemSize(int i) {
        return this.inventoryItemSize[i];
    }

    public int getNpcCount() {
        return this.npcCount;
    }

    public void setNpcCount(int i) {
        this.npcCount = i;
    }

    public int getNpcCacheCount() {
        return this.npcCacheCount;
    }

    public void setNpcCacheCount(int i) {
        this.npcCacheCount = i;
    }

    public void setNpcFromCache(int i, ORSCharacter n) {
        this.npcsCache[i] = n;
    }

    public ORSCharacter getNpcFromCache(int i) {
        return this.npcsCache[i];
    }

    public void setNpc(int i, ORSCharacter n) {
        this.npcs[i] = n;
    }

    public ORSCharacter getNpc(int i) {
        return this.npcs[i];
    }

    public ORSCharacter getNpcFromServer(int i) {
        return this.npcsServer[i];
    }

    public void setOptionsMenuShow(boolean show) {
        this.optionsMenuShow = show;
    }

    public void setOptionsMenuCount(int i) {
        this.optionsMenuCount = i;
    }

    public void setOptionsMenuText(int i, String t) {
        this.optionsMenuText[i] = t;
    }

    public void setLoadingArea(boolean loading) {
        this.loadingArea = loading;
    }

    public void setWorldOffsetX(int i) {
        this.worldOffsetX = i;
    }

    public int getWorldOffsetZ() {
        return this.worldOffsetZ;
    }

    public void setWorldOffsetZ(int i) {
        this.worldOffsetZ = i;
    }

    public int getRequestedPlane() {
        return this.requestedPlane;
    }

    public void setRequestedPlane(int i) {
        this.requestedPlane = i;
    }

    public void set() {
    }

    public void setPlayerStatCurrent(int stat, int level) {
        this.playerStatCurrent[stat] = level;
    }

    public void setPlayerStatBase(int stat, int level) {
        this.playerStatBase[stat] = level;
    }

    public int getPlayerStatBase(int stat) {
        return this.playerStatBase[stat];
    }

    public void setPlayerExperience(int stat, int experience) {
        this.playerExperience[stat] = experience;
    }

    public int getPlayerExperience(int stat) {
        return this.playerExperience[stat];
    }

    public void setQuestPoints(int p) {
        this.questPoints = p;
    }

    public void setDeathScreenTimeout(int i) {
        this.deathScreenTimeout = i;
    }

    public int getGroundItemCount() {
        return this.groundItemCount;
    }

    public void setGroundItemCount(int i) {
        this.groundItemCount = i;
    }

    public void setGroundItemX(int i, int n) {
        this.groundItemX[i] = n;
    }

    public int getGroundItemX(int i) {
        return this.groundItemX[i];
    }

    public void setGroundItemZ(int i, int n) {
        this.groundItemZ[i] = n;
    }

    public int getGroundItemZ(int i) {
        return this.groundItemZ[i];
    }

    public void setGroundItemID(int i, int n) {
        this.groundItemID[i] = n;
    }

    public int getGroundItemID(int i) {
        return this.groundItemID[i];
    }

    public void setGroundItemHeight(int i, int n) {
        this.groundItemHeight[i] = n;
    }

    public int getGroundItemHeight(int i) {
        return this.groundItemHeight[i];
    }

    public ORSCharacter getPlayerFromServer(int i) {
        return this.playerServer[i];
    }

    public void setTradeRecipientName(String n) {
        this.tradeRecipientName = n;
    }

    public void setShowDialogTrade(boolean show) {
        this.showDialogTrade = show;
    }

    public int getTradeRecipientItemsCount() {
        return this.tradeRecipientItemsCount;
    }

    public void setTradeRecipientItemsCount(int i) {
        this.tradeRecipientItemsCount = i;
    }

    public void setTradeRecipientItem(int i, int n) {
        this.tradeRecipientItem[i] = n;
    }

    public void setTradeRecipientItemCount(int i, int n) {
        this.tradeRecipientItemCount[i] = n;
    }

    public int getTradeItemCount() {
        return this.tradeItemCount;
    }

    public void setTradeItemCount(int i) {
        this.tradeItemCount = i;
    }

		/*public void setGroupID(int groupID) {
			this.groupID = groupID;
		}*/

    public void setTradeItemID(int i, int n) {
        this.tradeItemID[i] = n;
    }

    public void setTradeItemSize(int i, int n) {
        this.tradeItemSize[i] = n;
    }

    public void setTradeAccepted(boolean accepted) {
        this.tradeAccepted = accepted;
    }

    public void setTradeRecipientAccepted(boolean accepted) {
        this.tradeRecipientAccepted = accepted;
    }

    public void setShowDialogTradeConfirm(boolean confirm) {
        this.showDialogTradeConfirm = confirm;
    }

    public void setTradeConfirmAccepted(boolean confirm) {
        this.tradeConfirmAccepted = confirm;
    }

    public void setTradeRecipientConfirmName(String n) {
        this.tradeRecipientConfirmName = n;
    }

    public int getTradeRecipientConfirmItemsCount() {
        return this.tradeRecipientConfirmItemsCount;
    }

    public void setTradeRecipientConfirmItemsCount(int n) {
        this.tradeRecipientConfirmItemsCount = n;
    }

    public void setTradeRecipientConfirmItems(int i, int n) {
        this.tradeRecipientConfirmItems[i] = n;
    }

    public void setTradeRecipientConfirmItemCount(int i, int n) {
        this.tradeRecipientConfirmItemCount[i] = n;
    }

    public int getTradeConfirmItemsCount() {
        return this.tradeConfirmItemsCount;
    }

    public void setTradeConfirmItemsCount(int i) {
        this.tradeConfirmItemsCount = i;
    }

    public void setTradeConfirmItems(int i, int n) {
        this.tradeConfirmItems[i] = n;
    }

    public void setTradeConfirmItemsCount1(int i, int n) {
        this.tradeConfirmItemsCount1[i] = n;
    }

    public void setShowAppearanceChange(boolean show) {
        this.showAppearanceChange = show;
    }

    public void setShowDialogShop(boolean show) {
        this.showDialogShop = show;
    }

    public void setOptionCameraModeAuto(boolean auto) {
        this.optionCameraModeAuto = auto;
    }

    public void setOptionMouseButtonOne(boolean button) {
        this.optionMouseButtonOne = button;
    }

    public void setOptionSoundDisabled(boolean disabled) {
        this.optionSoundDisabled = disabled;
    }

    public void setCombatStyle(int style) {
        this.combatStyle = style;
    }

    public void setSettingsBlockGlobal(int block) {
        this.settingsBlockGlobal = block;
    }

    public void setClanInviteBlockSetting(boolean block) {
        this.clanInviteBlockSetting = block;
    }

    public boolean checkPrayerOn(int i) {
        return this.prayerOn[i];
    }

    public void togglePrayer(int i, boolean toggle) {
        this.prayerOn[i] = toggle;
    }

    public void setQuestName(int i, String s) {
        this.questNames[i] = s;
    }

    public void setQuestStage(int i, int n) {
        this.questStages[i] = n;
    }

    public CustomBankInterface getBank() {
        return this.bank;
    }

    public int getNewBankItemCount() {
        return this.newBankItemCount;
    }

    public void setNewBankItemCount(int i) {
        this.newBankItemCount = i;
    }

    public void setBankItemsMax(int i) {
        this.bankItemsMax = i;
    }

    public void setShowDialogDuel(boolean show) {
        this.showDialogDuel = show;
    }

    public void setShowDialogDuelConfirm(boolean show) {
        this.showDialogDuelConfirm = show;
    }

    public int getDuelOffsetOpponentItemCount() {
        return this.duelOffsetOpponentItemCount;
    }

    public void setDuelOffsetOpponentItemCount(int i) {
        this.duelOffsetOpponentItemCount = i;
    }

    public void setDuelOpponentItemId(int i, int n) {
        this.duelOpponentItemId[i] = n;
    }

    public void setDuelOpponentItemCount(int i, int n) {
        this.duelOpponentItemCount[i] = n;
    }

    public void setDuelOfferAccepted(boolean accepted) {
        this.duelOfferAccepted = accepted;
    }

    public void setDuelOffsetOpponentAccepted(boolean accepted) {
        this.duelOffsetOpponentAccepted = accepted;
    }

    public void setDuelSettingsRetreat(boolean retreat) {
        this.duelSettingsRetreat = retreat;
    }

    public void setDuelOptionRetreat(int retreat) {
        this.duelOptionRetreat = retreat;
    }

    public void setDuelSettingsMagic(boolean magic) {
        this.duelSettingsMagic = magic;
    }

    public void setDuelOptionMagic(int magic) {
        this.duelOptionMagic = magic;
    }

    public void setDuelSettingsPrayer(boolean prayer) {
        this.duelSettingsPrayer = prayer;
    }

    public void setDuelOptionPrayer(int prayer) {
        this.duelOptionPrayer = prayer;
    }

    public void setDuelSettingsWeapons(boolean weapons) {
        this.duelSettingsWeapons = weapons;
    }

    public void setDuelOptionWeapons(int weapons) {
        this.duelOptionWeapons = weapons;
    }

    public void setDuelConfirmed(boolean confirmed) {
        this.duelConfirmed = confirmed;
    }

    public void setDuelOpponentName(String n) {
        this.duelOpponentName = n;
    }

    public int getDuelOpponentItemsCount() {
        return this.duelOpponentItemsCount;
    }

    public void setDuelOpponentItemsCount(int n) {
        this.duelOpponentItemsCount = n;
    }

    public void setDuelOpponentItems(int i, int n) {
        this.duelOpponentItems[i] = n;
    }

    public void setDuelOpponentItemCounts(int i, int n) {
        this.duelOpponentItemCounts[i] = n;
    }

    public int getDuelItemsCount() {
        return this.duelItemsCount;
    }

    public void setDuelItemsCount(int i) {
        this.duelItemsCount = i;
    }

    public void setDuelItems(int i, int n) {
        this.duelItems[i] = n;
    }

    public void setDuelItemCounts(int i, int n) {
        this.duelItemCounts[i] = n;
    }

    public void setDuelOfferItemCount(int i) {
        this.duelOfferItemCount = i;
    }

    public void setDuelConfirmOpponentName(String s) {
        this.duelConfirmOpponentName = s;
    }

    public void setRecentSkill(int stat) {
        this.recentSkill = stat;
    }

    public void setPlayerStatXpGained(int stat, long exp) {
        this.playerStatXpGained[stat] = exp;
    }

    public long getPlayerStatXpGained(int stat) {
        return this.playerStatXpGained[stat];
    }

    public void setXpGainedStartTime(int i, long n) {
        this.xpGainedStartTime[i] = n;
    }

    public long getXpGainedStartTime(int stat) {
        return this.xpGainedStartTime[stat];
    }

    public long getPlayerXpGainedTotal() {
        return this.playerXpGainedTotal;
    }

    public void setPlayerXpGainedTotal(long exp) {
        this.playerXpGainedTotal = exp;
    }

    public ArrayList getXpNotifications() {
        return xpNotifications;
    }

    public boolean getInitLoginCleared() {
        return this.initLoginCleared;
    }

    public void setInitLoginCleared(boolean cleared) {
        this.initLoginCleared = cleared;
    }

    public boolean getWelcomeScreenShown() {
        return this.welcomeScreenShown;
    }

    public void setWelcomeScreenShown(boolean show) {
        this.welcomeScreenShown = show;
    }

    public void setWelcomeLastLoggedInIp(String i) {
        this.welcomeLastLoggedInIp = i;
    }

    public void setWelcomeLastLoggedInDays(int i) {
        this.welcomeLastLoggedInDays = i;
    }

    public void setWelcomeRecoverySetDays(int i) {
        this.welcomeRecoverySetDays = i;
    }

    public void setWelcomeTipOfDay(int i) {
        this.welcomeTipOfDay = i;
    }

    public void setShowDialogMessage(boolean show) {
        this.showDialogMessage = show;
    }

    public void setWelcomeLastLoggedInHost(String i) {
        this.welcomeLastLoggedInHost = i;
    }

    public void setServerMessage(String i) {
        this.serverMessage = i;
    }

    public void setShowDialogServerMessage(boolean show) {
        this.showDialogServerMessage = show;
    }

    public void setServerMessageBoxTop(boolean boxTop) {
        this.serverMessageBoxTop = boxTop;
    }

    public boolean getIsSleeping() {
        return this.isSleeping;
    }

    public void setIsSleeping(boolean sleeping) {
        this.isSleeping = sleeping;
    }

    public void setFatigueSleeping(int fatigue) {
        this.fatigueSleeping = fatigue;
    }

    public int getStatFatigue() {
        return this.statFatigue;
    }

    public int getStatKills2() {
        return this.statKills2;
    }

    public int getPetFatigue() {
        return this.petFatigue;
    }

    public void setStatFatigue(int fatigue) {
        if (DEBUG)
            System.out.println("Fatigue: " + fatigue);
        this.statFatigue = fatigue;
    }

    public void setStatKills2(int kills2) {
        if (DEBUG)
            System.out.println("Kills2: " + kills2);
        this.statKills2 = kills2;
    }

    public void setPetFatigue(int petFatigue) {
        if (DEBUG)
            System.out.println("PetFatigue: " + petFatigue);
        this.petFatigue = petFatigue;
    }

    public void setInputTextCurrent(String s) {
        this.inputTextCurrent = s;
    }

    public void setInputTextFinal(String s) {
        this.inputTextFinal = s;
    }

    public Sprite makeSleepSprite(ByteArrayInputStream x) {
        return clientPort.getSpriteFromByteArray(x);
    }

    public void setSleepingStatusText(String s) {
        this.sleepingStatusText = s;
    }

    public void setSystemUpdate(int i) {
        this.systemUpdate = i;
    }

    public void setElixirTimer(int i) {
        this.elixirTimer = i;
    }

    public int getTeleportBubbleCount() {
        return this.teleportBubbleCount;
    }

    public void setTeleportBubbleCount(int count) {
        this.teleportBubbleCount = count;
    }

    public void setTeleportBubbleType(int i, int n) {
        this.teleportBubbleType[i] = n;
    }

    public void setTeleportBubbleTime(int i, int n) {
        this.teleportBubbleTime[i] = n;
    }

    public void setTeleportBubbleX(int i, int x) {
        this.teleportBubbleX[i] = x;
    }

    public void setTeleportBubbleZ(int i, int z) {
        this.teleportBubbleZ[i] = z;
    }

    public void setShopSellPriceMod(int i) {
        this.shopSellPriceMod = i;
    }

    public void setShopBuyPriceMod(int i) {
        this.shopBuyPriceMod = i;
    }

    public void setShopPriceMultiplier(int i) {
        this.shopPriceMultiplier = i;
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

    public void setShopItemID(int i, int n) {
        this.shopItemID[i] = n;
    }

    public int getShopItemID(int i) {
        return this.shopItemID[i];
    }

    public void setShopItemCount(int i, int n) {
        this.shopItemCount[i] = n;
    }

    public void setShopItemPrice(int i, int n) {
        this.shopItemPrice[i] = n;
    }

    public int getShopSelectedItemIndex() {
        return this.shopSelectedItemIndex;
    }

    public void setShopSelectedItemIndex(int i) {
        this.shopSelectedItemIndex = i;
    }

    public int getShopSelectedItemType() {
        return this.shopSelectedItemType;
    }

    public void setShopSelectedItemType(int i) {
        this.shopSelectedItemType = i;
    }

    public void setPlayerStatEquipment(int i, int n) {
        this.playerStatEquipment[i] = n;
    }

    public int getProjectileMaxRange() {
        return this.projectileMaxRange;
    }

    public void setInsideTutorial(boolean i) {
        this.insideTutorial = i;
    }

    public void setInsideBlackHole(boolean i) {
        this.insideBlackHole = i;
    }

    void setExperienceArray() {
        int experience = 0;
        for (int i = 0; i < S_PLAYER_LEVEL_LIMIT; ++i) {
            int experienceFactor = 1 + i;
            int experienceIncrease = (int) (300D * Math.pow(2.0D, experienceFactor / 7D) + experienceFactor);
            experience += experienceIncrease;
            this.experienceArray[i] = (experience & 0xffffffc) / 4;
        }
    }

    private void getServerConfig() {
        try {
            if ((Config.SERVER_IP != null)) {
                String ip = Config.SERVER_IP; // allows override if manually set in Config code
                int port = Config.SERVER_PORT; // allows override if manually set in Config code
                this.packetHandler.setClientStream(new Network_Socket(this.packetHandler.openSocket(port, ip), this.packetHandler));
            } else {
                String ip = ClientPort.loadIP(); // loads based on Cache/ip.txt
                int port = ClientPort.loadPort(); // loads based on Cache/port.txt
                this.packetHandler.setClientStream(new Network_Socket(this.packetHandler.openSocket(port, ip), this.packetHandler));
            }
            this.packetHandler.getClientStream().newPacket(19);
            this.packetHandler.getClientStream().finishPacketAndFlush();
            this.packetHandler.getClientStream().getUnsignedByte();
            int len = this.packetHandler.getClientStream().getUnsignedByte();
            this.packetHandler.handlePacket(this.packetHandler.getClientStream().getUnsignedByte(), len);
        } catch (IOException var9) {
            throw GenUtil.makeThrowable(var9, "client.KC()");
        }
    }

    final void continueStartGame(byte var1) {
        System.out.println("Got server configs!");
        if (Config.DEBUG) {
            System.out.println("Debug server configs received:");
            System.out.println(F_CACHE_DIR + " 0");
            System.out.println(SERVER_NAME + " 1");
            System.out.println(SERVER_NAME_WELCOME + " 2");
            System.out.println(S_PLAYER_LEVEL_LIMIT + " 3");
            System.out.println(S_SPAWN_AUCTION_NPCS + " 4");
            System.out.println(S_SPAWN_IRON_MAN_NPCS + " 5");
            System.out.println(S_SHOW_FLOATING_NAMETAGS + " 6");
            System.out.println(S_WANT_CLANS + " 7");
            System.out.println(S_WANT_KILL_FEED + " 8");
            System.out.println(S_FOG_TOGGLE + " 9");
            System.out.println(S_GROUND_ITEM_TOGGLE + " 10");
            System.out.println(S_AUTO_MESSAGE_SWITCH_TOGGLE + " 11");
            System.out.println(S_BATCH_PROGRESSION + " 12");
            System.out.println(S_SIDE_MENU_TOGGLE + " 13");
            System.out.println(S_INVENTORY_COUNT_TOGGLE + " 14");
            System.out.println(S_ZOOM_VIEW_TOGGLE + " 15");
            System.out.println(S_MENU_COMBAT_STYLE_TOGGLE + " 16");
            System.out.println(S_FIGHTMODE_SELECTOR_TOGGLE + " 17");
            System.out.println(S_EXPERIENCE_COUNTER_TOGGLE + " 18");
            System.out.println(S_EXPERIENCE_DROPS_TOGGLE + " 19");
            System.out.println(S_ITEMS_ON_DEATH_MENU + " 20");
            System.out.println(S_SHOW_ROOF_TOGGLE + " 21");
            System.out.println(S_WANT_HIDE_IP + " 22");
            System.out.println(S_WANT_REMEMBER + " 23");
            System.out.println(S_WANT_GLOBAL_CHAT + " 24");
            System.out.println(S_WANT_SKILL_MENUS + " 25");
            System.out.println(S_WANT_QUEST_MENUS + " 26");
            System.out.println(S_WANT_EXPERIENCE_ELIXIRS + " 27");
            System.out.println(S_WANT_KEYBOARD_SHORTCUTS + " 28");
            System.out.println(S_WANT_CUSTOM_BANKS + " 29");
            System.out.println(S_WANT_BANK_PINS + " 30");
            System.out.println(S_WANT_BANK_NOTES + " 31");
            System.out.println(S_WANT_CERT_DEPOSIT + " 32");
            System.out.println(S_CUSTOM_FIREMAKING + " 33");
            System.out.println(S_WANT_DROP_X + " 34");
            System.out.println(S_WANT_EXP_INFO + " 35");
            System.out.println(S_WANT_WOODCUTTING_GUILD + " 36");
            System.out.println(S_WANT_DECANTING + " 37");
            System.out.println(S_WANT_CERTS_TO_BANK + " 38");
            System.out.println(S_WANT_CUSTOM_RANK_DISPLAY + " 39");
            System.out.println(S_RIGHT_CLICK_BANK + " 40");
            System.out.println(S_WANT_FIXED_OVERHEAD_CHAT + " 41");
            System.out.println(WELCOME_TEXT + " 42");
            System.out.println(MEMBER_WORLD + " 43");
            System.out.println(DISPLAY_LOGO_SPRITE + " 44");
            System.out.println(Config.C_LOGO_SPRITE_ID + " 45");
            System.out.println(Config.C_FPS + " 46");
            System.out.println(Config.C_WANT_EMAIL + " 47");
            System.out.println(Config.S_WANT_REGISTRATION_LIMIT + " 48");
            System.out.println(Config.S_ALLOW_RESIZE + " 49");
            System.out.println(Config.S_LENIENT_CONTACT_DETAILS + " 50");
            System.out.println(Config.S_WANT_FATIGUE + " 51");
			System.out.println(Config.S_WANT_RUNECRAFTING + " 60");
			System.out.println(S_WANT_CUSTOM_LANDSCAPE + " 61");
		}
        try {
            this.loadGameConfig(false);
            if (!this.errorLoadingData) {

                this.setFPS(getFPS(), (byte) 107); // Client FPS
                this.setSurface(new MudClientGraphics(this.getGameWidth(), this.getGameHeight() + 12, 4501));

                clientPort.initGraphics();
                getSurface().setIconsStart(0, 3284);
                this.getSurface().mudClientRef = this;
                this.getSurface().setClip(0, this.getGameWidth(), this.getGameHeight() + 12, 0);

				this.loadSkills();
				this.skillCount = skillNameLongArray.size();
				skillNameLong = skillNameLongArray.toArray(new String[skillCount]);
				skillNames = skillNamesArray.toArray(new String[skillCount]);
				skillNameLongArray.clear();
				skillNamesArray.clear();
				this.playerStatBase = new int[skillCount];
				this.playerStatCurrent = new int[skillCount];
				this.playerExperience = new int[skillCount];
				this.playerStatXpGained = new long[skillCount];
				this.xpGainedStartTime = new long[skillCount];

				bank = new CustomBankInterface(this);
                auctionHouse = new AuctionHouse(this);
                skillGuideInterface = new SkillGuideInterface(this);
                questGuideInterface = new QuestGuideInterface(this);
                experienceConfigInterface = new ExperienceConfigInterface(this);
                doSkillInterface = new DoSkillInterface(this);
                if (S_ITEMS_ON_DEATH_MENU)
                    lostOnDeathInterface = new LostOnDeathInterface(this);
                territorySignupInterface = new TerritorySignupInterface(this);

                mainComponent = new NComponent(this);
                mainComponent.setSize(getGameWidth(), getGameHeight());

                bankPinInterface = new BankPinInterface(this);
                mainComponent.addComponent(bankPinInterface);

                ironmanInterface = new IronManInterface(this);

                fishingTrawlerInterface = new FishingTrawlerInterface(this);
                mainComponent.addComponent(fishingTrawlerInterface);

                if (S_BATCH_PROGRESSION) {
                    batchProgressBar = new ProgressBarInterface(this);
                    mainComponent.addComponent(batchProgressBar.getComponent());
                }

                onlineList = new OnlineListInterface(this);
                mainComponent.addComponent(onlineList);

                achievementInterface = new AchievementGUI(this);
                clan = new Clan(this);

                if (S_EXPERIENCE_DROPS_TOGGLE) {
                    experienceOverlay = new NCustomComponent(this) {
                        @Override
                        public void render() {
                            if (C_EXPERIENCE_DROPS) {
                                time = System.currentTimeMillis();
                                for (Iterator<XPNotification> iterator = xpNotifications.iterator(); iterator.hasNext(); ) {
                                    XPNotification xpdrop = iterator.next();
                                    if (!xpdrop.isActive) {
                                        if (C_EXPERIENCE_COUNTER > 0) {
                                            if (time > m_timer && xpdrop.y > 20) {
                                                m_timer = time + 250;
                                                xpdrop.isActive = true;
                                            } else {
                                                return;
                                            }
                                        } else {
                                            if (time > m_timer && xpdrop.y > 0) {
                                                m_timer = time + 250;
                                                xpdrop.isActive = true;
                                            } else {
                                                return;
                                            }
                                        }
                                    }

                                    if (C_EXPERIENCE_COUNTER == 1) {
                                        drawExperienceCounter(xpdrop.skill);
                                    }

                                    int textColor = C_EXPERIENCE_COUNTER_COLOR == 0 ? 0xFFFFFF :
                                            C_EXPERIENCE_COUNTER_COLOR == 1 ? 0xFFFF00 :
                                                    C_EXPERIENCE_COUNTER_COLOR == 2 ? 0xFF0000 :
                                                            C_EXPERIENCE_COUNTER_COLOR == 3 ? 0x0000FF : 0x00FF00;

                                    if (!xpdrop.levelUp) {
                                        if (textColor == 0xFFFFFF) {
                                            graphics().drawShadowText("+" + xpdrop.amount + " " + getSkillNames()[xpdrop.skill] + " exp", xpdrop.x,
                                                    xpdrop.y, textColor, 2, false);
                                        } else {
                                            graphics().drawString("+" + xpdrop.amount + " " + getSkillNames()[xpdrop.skill] + " exp", xpdrop.x,
                                                    xpdrop.y, textColor, 2);
                                        }
                                    } else {
                                        if (textColor == 0xFFFFFF) {
                                            graphics().drawShadowText("+1 " + getSkillNames()[xpdrop.skill] + " level", xpdrop.x,
                                                    xpdrop.y, textColor, 2, false);
                                        } else {
                                            graphics().drawString("+1 " + getSkillNames()[xpdrop.skill] + " level", xpdrop.x,
                                                    xpdrop.y, textColor, 2);
                                        }
                                    }

                                    double dropSpeed = C_EXPERIENCE_DROP_SPEED == 0 ? 0.000000000001 :
                                            C_EXPERIENCE_DROP_SPEED == 1 ? 0.00005 : 1;
                                    xpdrop.y -= dropSpeed;

                                    if (C_EXPERIENCE_COUNTER > 0 && xpdrop.y <= 30) {
                                        xpdrop.isActive = false;
                                    } else if (xpdrop.y <= 0) {
                                        xpdrop.isActive = false;
                                    }

                                    if (C_EXPERIENCE_COUNTER > 0 && (xpdrop.y <= 30 || xpdrop.y > getGameHeight() - 30)) {
                                        iterator.remove();
                                    } else if (xpdrop.y <= 0 || xpdrop.y > getGameHeight()) {
                                        iterator.remove();
                                    }
                                }
                            }
                        }
                    };
                    mainComponent.addComponent(experienceOverlay);
                }

                this.menuCommon = new Menu(this.getSurface(), isAndroid() ? C_MENU_SIZE : 1, "Choose option");

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
                this.controlQuestInfoPanel = this.panelQuestInfo.addScrollingList(var3, 24 + var12, 196, 251, 500,
                        1, true);
                this.panelPlayerTaskInfo = new Panel(this.getSurface(), 5);
                this.controlPlayerTaskInfoPanel = this.panelPlayerTaskInfo.addScrollingList(var3, 24 + var12 + 27, 196, 224, 500,
                        7, true);

                if (!authenticSettings) {
                    this.panelSettings = new Panel(this.getSurface(), 5);
                    this.controlSettingPanel = this.panelSettings.addScrollingList3(var3 + 1, 24 + var12 + 16, 195, 184, 500, 1, true, 1, 2);
                }

                if (!Config.S_WANT_CUSTOM_SPRITES) {
                    this.loadMediaAuthentic();
                    if (!this.errorLoadingData) {
                        this.loadEntitiesAuthentic();
                        if (!this.errorLoadingData) {
                            this.scene = new Scene(this.getSurface(), 25000, 50000, 1000);
                            this.scene.setMidpoints(this.halfGameHeight(), true, this.getGameWidth(),
                                    this.halfGameWidth(), this.halfGameHeight(), this.m_qd,
                                    this.halfGameWidth());
                            this.scene.fogLandscapeDistance = 2400;
                            this.scene.fogEntityDistance = 2400;
                            this.scene.fogSmoothingStartDistance = 2300;
                            this.scene.fogZFalloff = 1;
                            this.scene.setDiffuseDir(-50, -10, true, -50);
                            this.world = new World(this.scene, this.getSurface());
                            this.world.baseMediaSprite = mudclient.spriteMedia;
                            this.loadTexturesAuthentic();
                            if (!this.errorLoadingData) {
                                this.loadModels();
                                if (!this.errorLoadingData) {
                                    this.loadSounds();
                                    if (!this.errorLoadingData) {
                                        clientPort.showLoadingProgress(100, "Starting game...");
                                        this.createMessageTabPanel(56);
                                        this.createLoginPanels(3845);
                                        this.createAppearancePanel(var1 ^ 24649);
                                        this.createRecoveryQuestionPanel();
                                        this.createPasswordRecoveryPanel();
                                        this.createContactDetailsPanel();
                                        this.resetLoginScreenVariables((byte) -88);
                                        this.renderLoginScreenViewports(-116);
                                    }
                                }
                            }
                        }
                    }
                } else {
                    if (!this.errorLoadingData) {
                        this.loadSprites();
                        if (!this.errorLoadingData) {
                            this.scene = new Scene(this.getSurface(), 25000, 50000, 1000);
                            this.scene.setMidpoints(this.halfGameHeight(), true, this.getGameWidth(),
                                    this.halfGameWidth(), this.halfGameHeight(), this.m_qd,
                                    this.halfGameWidth());
                            this.scene.fogLandscapeDistance = 2400;
                            this.scene.fogEntityDistance = 2400;
                            this.scene.fogSmoothingStartDistance = 2300;
                            this.scene.fogZFalloff = 1;
                            this.scene.setDiffuseDir(-50, -10, true, -50);
                            this.world = new World(this.scene, this.getSurface());
                            this.world.baseMediaSprite = mudclient.spriteMedia;
                            this.loadTextures();
                            if (!this.errorLoadingData) {
                                this.loadModels();
                                if (!this.errorLoadingData) {
                                    this.loadSounds();
                                    if (!this.errorLoadingData) {
                                        clientPort.showLoadingProgress(100, "Starting game...");
                                        this.createMessageTabPanel(56);
                                        this.createLoginPanels(3845);
                                        this.createAppearancePanel(var1 ^ 24649);
                                        this.createRecoveryQuestionPanel();
                                        this.createPasswordRecoveryPanel();
                                        this.createContactDetailsPanel();
                                        this.resetLoginScreenVariables((byte) -88);
                                        this.renderLoginScreenViewports(-116);
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

    private void tradeOffer(int count, int slot) {
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

            int invAvailable = this.getInventoryCount(id);
            if (invAvailable <= offered) {
                offerSuccess = true;
            }

            if (EntityHandler.getItemDef(id).quest && !localPlayer.isAdmin()) {
                offerSuccess = true;
                this.showMessage(false, null, "This object cannot be traded with other players",
                        MessageType.GAME, 0, null);
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
                this.packetHandler.getClientStream().newPacket(46);
                this.packetHandler.getClientStream().writeBuffer1.putByte(this.tradeItemCount);

                for (int i = 0; this.tradeItemCount > i; ++i) {
                    this.packetHandler.getClientStream().writeBuffer1.putShort(this.tradeItemID[i]);
                    this.packetHandler.getClientStream().writeBuffer1.putInt(this.tradeItemSize[i]);
                }

                this.packetHandler.getClientStream().finishPacket();
                this.tradeRecipientAccepted = false;
                this.tradeAccepted = false;
            }

        } catch (RuntimeException var9) {
            throw GenUtil.makeThrowable(var9, "client.PA(" + count + ',' + "dummy" + ',' + slot + ')');
        }
    }

    private void tradeRemove(int var1, byte var2, int var3) {
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

            this.packetHandler.getClientStream().newPacket(46);
            if (var2 > 120) {
                this.packetHandler.getClientStream().writeBuffer1.putByte(this.tradeItemCount);

                for (var6 = 0; var6 < this.tradeItemCount; ++var6) {
                    this.packetHandler.getClientStream().writeBuffer1.putShort(this.tradeItemID[var6]);
                    this.packetHandler.getClientStream().writeBuffer1.putInt(this.tradeItemSize[var6]);
                }

                this.packetHandler.getClientStream().finishPacket();
                this.tradeAccepted = false;
                this.tradeRecipientAccepted = false;
            }
        } catch (RuntimeException var9) {
            throw GenUtil.makeThrowable(var9, "client.A(" + var1 + ',' + var2 + ',' + var3 + ')');
        }
    }

    final void update() {
        try {

            boolean errorLoadingCoadebase = false;
            if (!errorLoadingCoadebase) {
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
                            ++this.cameraAutoMoveFrameCount;
                            if (this.cameraAutoMoveFrameCount > 500) {
                                this.cameraAutoMoveFrameCount = 0;
                                int var2 = (int) (4.0D * Math.random());
                                if ((2 & var2) == 2) {
                                    this.cameraAutoMoveZ += this.cameraAutoMoveAmountZ;
                                }

                                if ((var2 & 1) == 1) {
                                    this.cameraAutoMoveX += this.cameraAutoMoveAmountX;
                                }
                            }

                            if (this.cameraAutoMoveX < -50) {
                                this.cameraAutoMoveAmountX = 2;
                            }

                            if (this.cameraAutoMoveZ < -50) {
                                this.cameraAutoMoveAmountZ = 2;
                            }

                            if (this.cameraAutoMoveX > 50) {
                                this.cameraAutoMoveAmountX = -2;
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

                            if (this.cameraAutoMoveZ > 50) {
                                this.cameraAutoMoveAmountZ = -2;
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

    private void updateObjectAnimation(byte var1, int instanceNumber, String modelFileName) {
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

    private void walkToActionSource(int startX, int startZ, int destX, int destZ, boolean walkToEntity) {
        try {

            this.walkToArea(startX, startZ, destX, destZ, destX, destZ, false, walkToEntity);
        } catch (RuntimeException var8) {
            throw GenUtil.makeThrowable(var8, "client.BE(" + destZ + ',' + destX + ',' + startZ + ',' + startX + ','
                    + walkToEntity + ',' + "dummy" + ')');
        }
    }

    private void walkToArea(int startX, int startZ, int x1, int z1, int x2, int z2, boolean reachBorder,
                            boolean walkToEntity) {
        try {

            int count = this.world.findPath(this.pathX, this.pathZ, startX, startZ, x1, x2, z1, z2, reachBorder);
            if (count == -1) {
                if (!walkToEntity) {
                    return;
                }

                count = 1;
                this.pathX[0] = x1;
                this.pathZ[0] = z1;
            }

            --count;
            startZ = this.pathZ[count];
            startX = this.pathX[count];
            --count;
            if (!walkToEntity) {
                this.packetHandler.getClientStream().newPacket(187);
            } else {
                this.packetHandler.getClientStream().newPacket(16);
            }

            this.packetHandler.getClientStream().writeBuffer1.putShort(this.midRegionBaseX + startX);
            this.packetHandler.getClientStream().writeBuffer1.putShort(this.midRegionBaseZ + startZ);

            //System.out.println("walkToArea, startX: " + (this.midRegionBaseX + startX) + " (" + this.midRegionBaseX + " + " + startX + ")");
            //System.out.println("walkToArea, startZ: " + (this.midRegionBaseZ + startZ) + " (" + this.midRegionBaseZ + " + " + startZ + ")");

            if (walkToEntity && count == -1 && (startX + this.midRegionBaseX) % 5 == 0) {
                count = 0;
            }

            for (int i = count; i >= 0 && i > count - 25; --i) {
                this.packetHandler.getClientStream().writeBuffer1.putByte(this.pathX[i] - startX);
                this.packetHandler.getClientStream().writeBuffer1.putByte(this.pathZ[i] - startZ);

                //System.out.println("walkToArea, count: " + count + ", pathX[" + i + "]: " + (this.pathX[i] - startX) + " (" + this.pathX[i] + " - " + startX + ")");
                //System.out.println("walkToArea, count: " + count + ", pathZ[" + i + "]: " + (this.pathZ[i] - startZ) + " (" + this.pathZ[i] + " - " + startZ + ")");
            }

            this.packetHandler.getClientStream().finishPacket();
            this.mouseWalkY = this.mouseY;
            this.mouseWalkX = this.mouseX;
            this.mouseClickXStep = -24;
        } catch (RuntimeException var13) {
            throw GenUtil.makeThrowable(var13, "client.DD(" + x1 + ',' + walkToEntity + ',' + startX + ',' + z1 + ',' + startZ
                    + ',' + x2 + ',' + reachBorder + ',' + z2 + ',' + "dummy" + ')');
        }
    }

    private void walkToGroundItem(int startX, int startZ, int destX, int destZ, boolean var5) {
        try {

            if (!this.sendWalkToGroundItem(startX, startZ, destX, destX, destZ, destZ, false, var5)) {
                this.walkToArea(startX, startZ, destX, destZ, destX, destZ, true, var5);
            }
        } catch (RuntimeException var8) {
            throw GenUtil.makeThrowable(var8, "client.WC(" + "dummy" + ',' + startZ + ',' + destZ + ',' + destX + ','
                    + var5 + ',' + startX + ')');
        }
    }

    private void walkToObject(int destX, int destZ, int dir, int var1, int objectID) {
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

    private void walkToWall(int x, int z, int dir) {
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

    private int halfGameWidth() {
        return gameWidth / 2;
    }

    public int getGameHeight() {
        return gameHeight;
    }

    private int halfGameHeight() {
        return gameHeight / 2;
    }

    public void setGameHeight(int gameHeight) {
        this.gameHeight = gameHeight;
    }

    private int[][] getAnimDirLayer_To_CharLayer() {
        return animDirLayer_To_CharLayer;
    }

    public int getPlayerStatCurrent(int skill) {
        return playerStatCurrent[skill];
    }

    private int[] getPlayerHairColors() {
        return playerHairColors;
    }

    private int[] getPlayerClothingColors() {
        return playerClothingColors;
    }

    private int[] getPlayerSkinColors() {
        return playerSkinColors;
    }

    public ORSCharacter getLocalPlayer() {
        return localPlayer;
    }

    public void setLocalPlayer(ORSCharacter p) {
        this.localPlayer = p;
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

    public void setMouseClick(int i) {
        this.mouseButtonClick = i;
    }

    public int getMouseButtonDown() {
        return currentMouseButtonDown;
    }

    public void setMouseButtonDown(int i) {
        this.currentMouseButtonDown = i;
    }

    public int getMouseButtonDownTime() {
        return mouseButtonDownTime;
    }

    public int getBankItemCount() {
        int bankItemCount = 0;
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

    public void setInventoryItemCount(int i) {
        this.inventoryItemCount = i;
    }

    public int[] getInventoryItemEquipped() {
        return inventoryItemEquipped;
    }

    public int getInventoryItemEquippedID(int i) {
        return inventoryItemEquipped[i];
    }

    public int[] getInventoryItems() {
        return inventoryItemID;
    }

    public ArrayList<Integer> getInventoryItemsArray() {
        ArrayList<Integer> toReturn = new ArrayList<>();
        for (int i : getInventoryItems())
            toReturn.add(i);
        return toReturn;
    }

    public int[] getInventoryItemsCount() {
        return inventoryItemSize;
    }

    public ArrayList<Integer> getInventoryItemsCountArray() {
        ArrayList<Integer> toReturn = new ArrayList<>();
        for (int i : getInventoryItems())
            toReturn.add(i);
        return toReturn;
    }

    public int getLastMouseDown() {
        return lastMouseButtonDown;
    }

    private String[] getSkillNames() {
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

    private void drawSkillGuide() {
        skillGuideInterface.onRender(this.getSurface());
    }

    public String getSkillGuideChosen() {
        return skillGuideChosen;
    }

    private void setSkillGuideChosen(String skillGuideChosen) {
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
		} else if (skillGuideChosen.equalsIgnoreCase("Runecrafting")) {
			skillGuideChosenTabs.add("Singles");
			skillGuideChosenTabs.add("Multiples");
		}
	}


    private void drawQuestGuide() {
        questGuideInterface.onRender(this.getSurface());
    }

    public String getQuestGuideChosen() {
        return questGuideChosen;
    }

    private void setQuestGuideChosen(String questGuideChosen) {
        this.questGuideChosen = questGuideChosen;
    }

    public int getQuestGuideProgress() {
        return questGuideProgress;
    }

    private void setQuestGuideProgress(int questGuideProgress) {
        this.questGuideProgress = questGuideProgress;
    }

    public String getQuestGuideStartWho() {
        return questGuideStartWho;
    }

    private void setQuestGuideStartWho(int chosen) {
        this.questGuideStartWho = questGuideStartWhos[chosen];
    }

    public String getQuestGuideStartWhere() {
        return questGuideStartWhere;
    }

    private void setQuestGuideStartWhere(int chosen) {
        this.questGuideStartWhere = questGuideStartWheres[chosen];
    }

    public String[] getQuestGuideRequirement() {
        return questGuideRequirement;
    }

    private void setQuestGuideRequirement(int chosen) {
        this.questGuideRequirement = questGuideRequirements[chosen];
    }

    public String[] getQuestGuideReward() {
        return questGuideReward;
    }

    private void setQuestGuideReward(int chosen) {
        this.questGuideReward = questGuideRewards[chosen];
    }

    private void drawDoSkill() {
        this.doSkillInterface.onRender();
    }

	private void loadSkills() {
		addSkill("Attack");
		addSkill("Defense");
		addSkill("Strength");
		addSkill("Hits");
		addSkill("Ranged");
		addSkill("Prayer");
		addSkill("Magic");
		addSkill("Cooking");
		addSkill("Woodcutting", "Woodcut");
		addSkill("Fletching");
		addSkill("Fishing");
		addSkill("Firemaking");
		addSkill("Crafting");
		addSkill("Smithing");
		addSkill("Mining");
		addSkill("Herblaw");
		addSkill("Agility");
		addSkill("Thieving");

		if (S_WANT_RUNECRAFTING)
			addSkill("Runecrafting", "Runecraft");
	}

	private void addSkill(String skillname) { addSkill(skillname, skillname); }

	private void addSkill(String longName, String shortName) {
		skillNameLongArray.add(longName);
		skillNamesArray.add(shortName);
	}

	public String getSkillToDo() {
        return skillToDo;
    }

    public void setSkillToDo(String skillToDo) {
        this.skillToDo = skillToDo;
    }

    private void drawLostOnDeath() {
        if (!S_ITEMS_ON_DEATH_MENU) return;
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
            if (uiTabPlayerInfoSubTab == 1) {
                panelQuestInfo.scrollMethodList(controlQuestInfoPanel, x);
            }
        } else if (showUiTab == 6) { // Settings wrench menu
            panelSettings.scrollMethodCustomList(controlSettingPanel, x, 1);
        } else if (showUiTab == 4) // Magic and prayer book list.
            panelMagic.scrollMethodList(controlMagicPanel, x);
        else if (showUiTab == 5) { // Friend list and ignore list.
            if (this.panelSocialTab == 2 || this.panelSocialTab == 0) {
                panelSocial.scrollMethodList(controlSocialPanel, x);
            } else if (this.panelSocialTab == 1) {
                panelClan.scrollMethodList(controlClanPanel, x);
            }
        } else if (skillGuideInterface.isVisible() && uiTabPlayerInfoSubTab == 0) {
            skillGuideInterface.skillGuide.scrollMethodList(skillGuideInterface.skillGuideScroll, x);
        } else if (questGuideInterface.isVisible() && uiTabPlayerInfoSubTab == 1) {
            questGuideInterface.questGuide.scrollMethodList(questGuideInterface.questGuideScroll, x);
        } else if (onlineList.isVisible()) {
            onlineList.panel.scrollMethodList(onlineList.scroll, x);
        } else if (isShowDialogBank() && this.bankPage == 0)
            bank.bank.scrollMethodList(bank.bankScroll, x);
        else if (auctionHouse.isVisible()) {
            if (auctionHouse.activeInterface == 0) {
                auctionHouse.auctionMenu.scrollMethodList(auctionHouse.auctionScrollHandle, x);
            } else {
                auctionHouse.myAuctions.scrollMethodList(auctionHouse.myAuctionScrollHandle, x);
            }
        } else if (clan.getClanInterface().isVisible()) {
            if (clan.getClanInterface().clanActivePanel == 3) {
                clan.getClanInterface().clanSetupPanel.scrollMethodList(clan.getClanInterface().clanSearchScroll, x);
            } else {
                clan.getClanInterface().clanSetupPanel.scrollMethodList(clan.getClanInterface().clanGUIScroll, x);
            }
        } else if (experienceConfigInterface.isVisible() && experienceConfigInterface.selectSkillMenu) {
            experienceConfigInterface.experienceConfig.scrollMethodList(experienceConfigInterface.experienceConfigScroll, x);
        } else if (messageTabSelected == MessageTab.QUEST && !this.controlPressed)
            panelMessageTabs.scrollMethodList(panelMessageQuest, x);
        else if (this.messageTabSelected == MessageTab.CHAT && !this.controlPressed)
            panelMessageTabs.scrollMethodList(panelMessageChat, x);
        else if (this.messageTabSelected == MessageTab.PRIVATE && !this.controlPressed)
            panelMessageTabs.scrollMethodList(panelMessagePrivate, x);
        else if (this.messageTabSelected == MessageTab.CLAN && !this.controlPressed)
            panelMessageTabs.scrollMethodList(panelMessageClan, x);
    }

    private boolean isShowDialogBank() {
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

    public void addXpNotification(int skill, int receivedXp, boolean b) {
        XPNotification n = new XPNotification(skill, receivedXp, false);
        this.xpNotifications.add(n);
    }

    private void kickClanPlayer(String player) {
        this.clanKickPlayer = player;
    }

    public void setVolumeToRotate(boolean b) {
        Config.C_VOLUME_TO_ROTATE = b;
    }

    public void setSwipeToRotate(boolean b) {
        Config.C_SWIPE_TO_ROTATE = b;
    }

    public void setSwipeToScroll
            (boolean b) {
        Config.C_SWIPE_TO_SCROLL = b;
    }

    public void setSwipeToZoom(boolean b) {
        Config.C_SWIPE_TO_ZOOM = b;
    }

    public void setLongPressDelay(int i) {
        Config.C_LONG_PRESS_TIMER = i;
    }

    public void setLastZoom(int i) {
        Config.C_LAST_ZOOM = i;
    }

    public void setGroundItemsToggle(int i) {
        Config.C_SHOW_GROUND_ITEMS = i;
    }

    public void setFightModeSelectorToggle(int i) {
        C_FIGHT_MENU = i;
    }

    public void setExperienceCounterToggle(int i) {
        C_EXPERIENCE_COUNTER = i;
    }

    public void setFontSize(int i) {
        Config.C_MENU_SIZE = i;
    }

    public void setHoldAndChoose(boolean b) {
        Config.C_HOLD_AND_CHOOSE = b;
    }

    public void setOptionBatchProgressBar(boolean b) {
        Config.C_BATCH_PROGRESS_BAR = b;
    }

    public void setOptionExperienceDrops(boolean b) {
        Config.C_EXPERIENCE_DROPS = b;
    }

    public void setOptionHideRoofs(boolean b) {
        Config.C_HIDE_ROOFS = b;
    }

    public void setOptionHideFog(boolean b) {
        Config.C_HIDE_FOG = b;
    }

    public void setOptionAutoMessageSwitch(boolean b) {
        Config.C_MESSAGE_TAB_SWITCH = b;
    }

    public void setOptionHideSideMenu(boolean b) {
        Config.C_SIDE_MENU_OVERLAY = b;
    }

    public void setOptionHideKillFeed(boolean b) {
        Config.C_KILL_FEED = b;
    }

    public void setHideInventoryCount(boolean b) {
        Config.C_INV_COUNT = b;
    }

    public void setHideNameTag(boolean b) {
        Config.C_NAME_CLAN_TAG_OVERLAY = b;
    }

	public void updateQuestRewards() {
		questGuideRewards = new String[][]{{"3 Quest Points", "2500 coins"}, {"1 Quest Point", "Lvl*50 + 250 Cooking experience", "Access to the Cook's range"}, {"3 Quest Points", "Silverlight"}, {"1 Quest Point", "Lvl*75 + 175 Mining experience", "Ability to use Doric's anvils", "180 coins"}, {"1 Quest Point", "Lvl*62.5 + 500 Prayer experience", "Amulet of Ghostspeak"}, {"5 Quest Points", "Lvl*15 + 125 Crafting experience", "1 Gold bar"}, {"4 Quest Points", "300 coins"}, {"1 Quest Point", "Lvl*100 + 375 Magic experience", "An amulet of accuracy"}, {"2 Quest Points", "450 coins", "A gold ring", "An emerald"}, {"3 Quest points", "Free passage through the Al-Kharid tollgate", "700 coins"}, {"5 Quest Points"}, {"1 Quest Point", "Lvl*25 + 125 Crafting experience", "180 coins"}, {"1 Quest Point", "600 coins"}, {"1 Quest Point", "Lvl*375 + 350 Smithing experience"}, {"3 Quest Points", "Lvl*150 + 325 Attack experience"}, {"1 Quest Point", "Lvl*50 + 225 Magic experience"}, {"2 Quest Points", "Lvl*300 + 650 Defense experience", "Lvl*300 + 650 Strength experience", "The ability to wear a Rune plate mail body"}, {"4 Quest Points", "Lvl*150 + 325 Hits experience"}, {"3 Quest Points", "Ability to enter the city of Zanaris", "Ability to wield a Dragon sword"}, {"1 Quest Point", "Lvl*50 + 75 experience in the following skills: Attack, Defense, Hits, Strength, Cooking, Fishing, Mining, Smithing, Ranged, Firemaking, Woodcutting, and Herblaw", "Access to the Heroes' Guild", "Ability to wield the Dragon axe"}, {"4 Quest Points", "250 Herblaw experience", "Ability to use the Herblaw skill"}, {"6 Quest Points", "Excalibur"}, {"1 Quest Point", "Lvl*125 + 375 Strength experience", "Thormac will enchant your battlestaves for 40000 coins"}, {"1 Quest Point", "A pair of Steel gauntlets"}, {"1 Quest Point", "Lvl*75 + 200 Thieving experience", "5 swordfish"}, {"1 Quest Point", this.playerStatBase[10] < 24 ? "(Lvl - 10)*75 + 975 Fishing experience" : "(Lvl - 24)*75 + 2225 Fishing experience", "Access to the underground tunnel beneath White Wolf Mountain"}, {"1 Quest Point", "(Lvl + 1)*125 Woodcutting experience", "8 Law-Runes"}, {"1 Quest Point", "Lvl*250 + 500 experience in Ranged and Fletching"}, {"1 Quest Point", "500 coins"}, {"2 Quest Points", "(Lvl + 1)*300 Defense experience", "(Lvl + 1)*250 Prayer experience"}, {"2 Quest Points", "Lvl*200 + 175 experience in Attack and Thieving", "1000 coins"}, {"2 Quest Points", "Lvl*225 + 200 Attack experience", "A Gnome amulet of protection", "Ability to use Spirit Trees"}, {"1 Quest Point", "Lvl*50 + 500 Thieving experience", "2000 coins"}, {"4 Quest Points", "3100 coins"}, {"1 Quest Point", "Lvl*75 + 175 Mining experience", "A magic scroll granting the ability to cast Ardougne teleport"}, {"1 Quest Point", "Lvl*200 + 175 Fishing experience", "1 Oyster pearls"}, {"1 Quest Point", "Lvl*225 + 250 experience in Attack and Strength", "40 Mithril seeds", "2 Diamonds", "2 Gold bars"}, {"3 Quest Points", "Lvl*50 + 500 Thieving experience", "Ability to use King Lathas' Combat Training Camp", "Ability to travel freely between eastern and western Ardougne gate"}, {"1 Quest Point", "Lvl*125 + 400 Herblaw experience"}, {"5 Quest Points", "Lvl*300 + 400 experience in Agility and Attack", "Lvl*50 + 150 Magic experience", "Access to the Grand Tree mines", "Ability to use the Spirit Tree at the Grand Tree", "Ability to use the Gnome Gliders"}, {"2 Quest Points", "(Lvl + 1)*125 Crafting experience", "Access to Shilo Village"}, {"5 Quest Points", "Lvl*50 + 500 experience in Agility and Attack", "A Staff of Iban", "15 Death-Runes", "30 Fire-Runes"}, {"2 Quest Points", "Lvl*100 + 250 Crafting experience", "Another reward based on your constellation"}, {"2 Quest Points", "(Lvl + 1)*150 experience twice in a choice of Agility, Fletching, Thieving, Smithing", "Ability to make throwing darts", "Access to the Desert Mining Camp"}, {"4 Quest Points", "(Lvl + 1)*250 Magic experience", "A spell scroll granting the ability to cast the Watchtower teleport", "5000 coins"}, {"1 Quest Point", "Lvl*50 + 250 Crafting experience", "Ability to buy a dwarf cannon", "Ability to make cannon balls"}, {"3 Quest Points", "Lvl*37.5 + 187.5 Crafting experience", "2000 coins"}, {"2 Quest Points", "(Lvl + 1)*300 Mining experience", "(Lvl + 1)*125 Herblaw experience", "2 Gold bars"}, {"1 Quest Point", "Lvl*45 + 175 Cooking experience", "A Kitten", "A Chocolate cake and stew"}, {"4 Quest Points", "(Lvl + 1)*150 experience in 4 of these skills of your choice: Attack, Strength, Defense, Hits, Prayer, Magic, Woodcutting, Crafting, Smithing, Herblaw, Agility, and Thieving", "Access to the Legend's Guild", "Ability to wear the Dragon Square Shield and Cape of Legends", "Ability to make Oomlie meat parcels and Blessed golden bowls"},{"1 Quest Point","1 air talisman", "The ability to mine rune essence","The ability to enter mysterious ruins with the proper talisman"}};
	}

	class XPNotification {
        protected int x, y;
        protected int amount = 0;
        protected int skill = 0;
        boolean levelUp;
        boolean isActive;

        XPNotification(int skill, int amount, boolean levelUp) {
            this.skill = skill;
            this.amount = amount;
            this.levelUp = levelUp;
            x = halfGameWidth() - 50;
            y = (int) ((float) getGameHeight() / 4.0f + 40);
            isActive = false;
        }
    }
}
