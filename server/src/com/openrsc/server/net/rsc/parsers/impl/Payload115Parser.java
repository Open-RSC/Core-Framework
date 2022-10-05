package com.openrsc.server.net.rsc.parsers.impl;

import com.openrsc.server.constants.Classes;
import com.openrsc.server.constants.Constants;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.Packet;
import com.openrsc.server.net.rsc.Crypto;
import com.openrsc.server.net.rsc.enums.OpcodeIn;
import com.openrsc.server.net.rsc.parsers.PayloadParser;
import com.openrsc.server.net.rsc.struct.AbstractStruct;
import com.openrsc.server.net.rsc.struct.incoming.*;
import com.openrsc.server.util.rsc.DataConversions;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * RSC Protocol-115 Parser of Incoming Packets to respective Protocol Independent Structs
 * **/
public class Payload115Parser implements PayloadParser<OpcodeIn> {
	@Override
	public OpcodeIn toOpcodeEnum(Packet packet, Player player) {
		OpcodeIn opcode = null;

		switch (packet.getID()) {
			case 5:
				opcode = OpcodeIn.HEARTBEAT;
				break;
			case 215:
				opcode = OpcodeIn.WALK_TO_ENTITY;
				break;
			case 255:
				opcode = OpcodeIn.WALK_TO_POINT;
				break;
			case 1:
				opcode = OpcodeIn.CONFIRM_LOGOUT;
				break;
			case 6:
				opcode = OpcodeIn.LOGOUT;
				break;

			case 231:
				opcode = OpcodeIn.COMBAT_STYLE_CHANGED;
				break;
			case 237:
				opcode = OpcodeIn.QUESTION_DIALOG_ANSWER;
				break;
			case 236:
				opcode = OpcodeIn.PLAYER_APPEARANCE_CHANGE;
				break;
			case 29:
				opcode = OpcodeIn.SOCIAL_ADD_IGNORE;
				break;
			case 26:
				opcode = OpcodeIn.SOCIAL_ADD_FRIEND;
				break;
			case 28:
				opcode = OpcodeIn.SOCIAL_SEND_PRIVATE_MESSAGE;
				break;
			case 27:
				opcode = OpcodeIn.SOCIAL_REMOVE_FRIEND;
				break;
			case 30:
				opcode = OpcodeIn.SOCIAL_REMOVE_IGNORE;
				break;
			case 199:
				opcode = OpcodeIn.DUEL_FIRST_ACCEPTED;
				break;
			case 201:
				opcode = OpcodeIn.DUEL_OFFER_ITEM;
				break;
			case 200:
				opcode = OpcodeIn.DUEL_FIRST_SETTINGS_CHANGED;
				break;
			case 203:
				opcode = OpcodeIn.DUEL_DECLINED;
				break;
			case 198:
				opcode = OpcodeIn.DUEL_SECOND_ACCEPTED;
				break;
			case 238:
				opcode = OpcodeIn.INTERACT_WITH_BOUNDARY;
				break;
			case 229:
				opcode = OpcodeIn.INTERACT_WITH_BOUNDARY2;
				break;
			case 252:
				opcode = OpcodeIn.GROUND_ITEM_TAKE;
				break;
			case 223:
				opcode = OpcodeIn.CAST_ON_BOUNDARY;
				break;
			case 239:
				opcode = OpcodeIn.USE_WITH_BOUNDARY;
				break;
			case 245:
				opcode = OpcodeIn.NPC_TALK_TO;
				break;
			case 244:
				opcode = OpcodeIn.NPC_ATTACK;
				break;
			case 225:
				opcode = OpcodeIn.CAST_ON_NPC;
				break;
			case 243:
				opcode = OpcodeIn.NPC_USE_ITEM;
				break;
			case 226:
				opcode = OpcodeIn.PLAYER_CAST_PVP;
				break;
			case 219:
				opcode = OpcodeIn.PLAYER_USE_ITEM;
				break;
			case 228:
				opcode = OpcodeIn.PLAYER_ATTACK;
				break;
			case 204:
				opcode = OpcodeIn.PLAYER_DUEL;
				break;
			case 235:
				opcode = OpcodeIn.PLAYER_INIT_TRADE_REQUEST;
				break;
			case 214:
				opcode = OpcodeIn.PLAYER_FOLLOW;
				break;
			case 224:
				opcode = OpcodeIn.CAST_ON_GROUND_ITEM;
				break;
			case 250:
				opcode = OpcodeIn.GROUND_ITEM_USE_ITEM;
				break;
			case 220:
				opcode = OpcodeIn.CAST_ON_INVENTORY_ITEM;
				break;
			case 240:
				opcode = OpcodeIn.ITEM_USE_ITEM;
				break;
			case 248:
				opcode = OpcodeIn.ITEM_UNEQUIP_FROM_INVENTORY;
				break;
			case 249:
				opcode = OpcodeIn.ITEM_EQUIP_FROM_INVENTORY;
				break;
			case 246:
				opcode = OpcodeIn.ITEM_COMMAND;
				break;
			case 251:
				opcode = OpcodeIn.ITEM_DROP;
				break;
			case 227:
				opcode = OpcodeIn.CAST_ON_SELF;
				break;
			case 221:
				opcode = OpcodeIn.CAST_ON_LAND;
				break;
			case 242:
				opcode = OpcodeIn.OBJECT_COMMAND;
				break;
			case 230:
				opcode = OpcodeIn.OBJECT_COMMAND2;
				break;
			case 222:
				opcode = OpcodeIn.CAST_ON_SCENERY;
				break;
			case 241:
				opcode = OpcodeIn.USE_ITEM_ON_SCENERY;
				break;
			case 218:
				opcode = OpcodeIn.SHOP_CLOSE;
				break;
			case 217:
				opcode = OpcodeIn.SHOP_BUY;
				break;
			case 216:
				opcode = OpcodeIn.SHOP_SELL;
				break;
			case 232:
				opcode = OpcodeIn.PLAYER_ACCEPTED_INIT_TRADE_REQUEST;
				break;
			case 233:
				opcode = OpcodeIn.PLAYER_DECLINED_TRADE;
				break;
			case 234:
				opcode = OpcodeIn.PLAYER_ADDED_ITEMS_TO_TRADE_OFFER;
				break;
			case 202:
				opcode = OpcodeIn.PLAYER_ACCEPTED_TRADE;
				break;
			case 212:
				opcode = OpcodeIn.PRAYER_ACTIVATED;
				break;
			case 211:
				opcode = OpcodeIn.PRAYER_DEACTIVATED;
				break;
			case 213:
				opcode = OpcodeIn.GAME_SETTINGS_CHANGED;
				break;
			case 3:
				opcode = OpcodeIn.CHAT_MESSAGE;
				break;
			case 7:
				opcode = OpcodeIn.COMMAND;
				break;
			case 31:
				opcode = OpcodeIn.PRIVACY_SETTINGS_CHANGED;
				break;
			case 207:
				opcode = OpcodeIn.BANK_CLOSE;
				break;
			case 206:
				opcode = OpcodeIn.BANK_WITHDRAW;
				break;
			case 205:
				opcode = OpcodeIn.BANK_DEPOSIT;
				break;

			case 0:
			case 19: //relogin
				opcode = OpcodeIn.LOGIN;
				break;
			case 2:
				opcode = OpcodeIn.REGISTER_ACCOUNT;
				break;
			case 4:
				opcode = OpcodeIn.FORGOT_PASSWORD;
				break;
			case 8:
				opcode = OpcodeIn.RECOVERY_ATTEMPT;
				break;
			case 25:
				opcode = OpcodeIn.CHANGE_PASS;
				break;
			case 208:
				opcode = OpcodeIn.SET_RECOVERY;
				break;

			case 17:
				opcode = OpcodeIn.SEND_DEBUG_INFO;
				break;
			case 254:
				opcode = OpcodeIn.KNOWN_PLAYERS;
				break;
			default:
				break;
		}

		return opcode;
	}

	@Override
	public AbstractStruct<OpcodeIn> parse(Packet packet, Player player) {

		OpcodeIn opcode = toOpcodeEnum(packet, player);
		AbstractStruct<OpcodeIn> result = null;

		switch (opcode) {
			case COMBAT_STYLE_CHANGED:
				CombatStyleStruct c = new CombatStyleStruct();
				c.style = packet.readByte();
				result = c;
				break;

			case PLAYER_APPEARANCE_CHANGE:
				PlayerAppearanceStruct pl = new PlayerAppearanceStruct();
				pl.headRestrictions = packet.readByte();
				pl.headType = packet.readByte();
				pl.bodyType = packet.readByte();
				pl.mustEqual2 = packet.readByte();
				pl.hairColour = packet.readByte();
				pl.topColour = packet.readByte();
				pl.trouserColour = packet.readByte();
				pl.skinColour = packet.readByte();
				Classes characterClass = null;
				int classIndex = packet.readByte();
				switch (classIndex) {
					case 0:
						characterClass = Classes.ADVENTURER;
						break;
					case 1:
						characterClass = Classes.WARRIOR;
						break;
					case 2:
						characterClass = Classes.WIZARD;
						break;
					case 3:
						characterClass = Classes.RANGER;
						break;
					case 4:
						characterClass = Classes.MINER;
						break;
				}
				pl.chosenClass = characterClass;
				result = pl;
				break;

			case QUESTION_DIALOG_ANSWER:
				MenuOptionStruct m = new MenuOptionStruct();
				m.option = packet.readByte();
				result = m;
				break;

			case CHAT_MESSAGE:
				ChatStruct cs = new ChatStruct();
				cs.message = read115RSCString(packet.readBytes(packet.getReadableBytes()));
				result = cs;
				break;
			case COMMAND:
				CommandStruct co = new CommandStruct();
				co.command = packet.readString();
				result = co;
				break;
			case SOCIAL_ADD_FRIEND:
			case SOCIAL_REMOVE_FRIEND:
			case SOCIAL_ADD_IGNORE:
			case SOCIAL_REMOVE_IGNORE:
			case SOCIAL_SEND_PRIVATE_MESSAGE:
				FriendStruct fs = new FriendStruct();
				fs.player = DataConversions.hashToUsername(packet.readLong());
				if (opcode == OpcodeIn.SOCIAL_SEND_PRIVATE_MESSAGE) {
					fs.message = read115RSCString(packet.readBytes(packet.getReadableBytes()));
				}
				result = fs;
				break;

			case BANK_CLOSE:
				BankStruct b = new BankStruct();
				result = b;
				break;
			case BANK_WITHDRAW:
			case BANK_DEPOSIT:
				BankStruct b1 = new BankStruct();
				b1.catalogID = packet.readShort();
				b1.amount = packet.readShort();
				result = b1;
				break;

			case SHOP_CLOSE:
				ShopStruct s = new ShopStruct();
				result = s;
				break;
			case SHOP_BUY:
			case SHOP_SELL:
				ShopStruct s1 = new ShopStruct();
				s1.catalogID = packet.readShort();
				s1.stockAmount = packet.readShort();
				s1.amount = 1;
				result = s1;
				break;

			case ITEM_UNEQUIP_FROM_INVENTORY:
			case ITEM_EQUIP_FROM_INVENTORY:
				EquipStruct e = new EquipStruct();
				e.slotIndex = packet.readShort();
				result = e;
				break;

			case GROUND_ITEM_USE_ITEM:
				ItemOnGroundItemStruct iog = new ItemOnGroundItemStruct();
				iog.groundItemCoord = new Point(packet.readShort(), packet.readShort());
				iog.groundItemId = packet.readShort();
				iog.slotIndex = packet.readShort();
				result = iog;
				break;

			case ITEM_USE_ITEM:
				ItemOnItemStruct ioi = new ItemOnItemStruct();
				ioi.slotIndex1 = packet.readShort();
				ioi.slotIndex2 = packet.readShort();
				result = ioi;
				break;

			case USE_WITH_BOUNDARY:
			case USE_ITEM_ON_SCENERY:
				ItemOnObjectStruct ioo = new ItemOnObjectStruct();
				ioo.coordObject = new Point(packet.readShort(), packet.readShort());
				if (opcode == OpcodeIn.USE_WITH_BOUNDARY) {
					ioo.direction = packet.readByte();
				}
				ioo.slotID = packet.readShort();
				result = ioo;
				break;

			case NPC_USE_ITEM:
			case PLAYER_USE_ITEM:
				ItemOnMobStruct it = new ItemOnMobStruct();
				it.serverIndex = packet.readShort();
				it.slotIndex = packet.readShort();
				result = it;
				break;

			case GROUND_ITEM_TAKE:
				TargetPositionStruct tp = new TargetPositionStruct();
				tp.coordinate = new Point(packet.readShort(), packet.readShort());
				if (opcode == OpcodeIn.GROUND_ITEM_TAKE) {
					tp.itemId = packet.readShort();
				}
				result = tp;
				break;

			case ITEM_COMMAND:
			case ITEM_DROP:
				ItemCommandStruct ic = new ItemCommandStruct();
				ic.index = packet.readShort();
				result = ic;
				break;

			case OBJECT_COMMAND:
			case OBJECT_COMMAND2:
			case INTERACT_WITH_BOUNDARY:
			case INTERACT_WITH_BOUNDARY2:
				TargetObjectStruct to = new TargetObjectStruct();
				to.coordObject = new Point(packet.readShort(), packet.readShort());
				if (opcode == OpcodeIn.INTERACT_WITH_BOUNDARY || opcode == OpcodeIn.INTERACT_WITH_BOUNDARY2) {
					to.direction = packet.readByte();
				}
				result = to;
				break;

			case NPC_ATTACK:
			case NPC_TALK_TO:
			case PLAYER_ATTACK:
			case PLAYER_FOLLOW:
				TargetMobStruct t = new TargetMobStruct();
				t.serverIndex = packet.readShort();
				result = t;
				break;

			case CAST_ON_SELF:
			case PLAYER_CAST_PVP:
			case CAST_ON_NPC:
			case CAST_ON_INVENTORY_ITEM:
			case CAST_ON_BOUNDARY:
			case CAST_ON_SCENERY:
			case CAST_ON_GROUND_ITEM:
			case CAST_ON_LAND:
				SpellStruct sp = new SpellStruct();
				if (opcode == OpcodeIn.PLAYER_CAST_PVP || opcode == OpcodeIn.CAST_ON_NPC
					|| opcode == OpcodeIn.CAST_ON_INVENTORY_ITEM) {
					sp.targetIndex = packet.readShort();
				} else if (opcode == OpcodeIn.CAST_ON_BOUNDARY || opcode == OpcodeIn.CAST_ON_SCENERY
					|| opcode == OpcodeIn.CAST_ON_GROUND_ITEM || opcode == OpcodeIn.CAST_ON_LAND) {
					sp.targetCoord = new Point(packet.readShort(), packet.readShort());
					if (opcode == OpcodeIn.CAST_ON_BOUNDARY) {
						sp.direction = packet.readByte();
					} else if (opcode == OpcodeIn.CAST_ON_GROUND_ITEM) {
						sp.targetIndex = packet.readShort();
					}
				}
				sp.spell = Constants.spellToEnum(packet.readShort());
				result = sp;
				break;

			case PLAYER_DUEL:
			case DUEL_FIRST_SETTINGS_CHANGED:
			case DUEL_FIRST_ACCEPTED:
			case DUEL_DECLINED:
			case DUEL_OFFER_ITEM:
			case DUEL_SECOND_ACCEPTED:
				PlayerDuelStruct pd = new PlayerDuelStruct();
				if (opcode == OpcodeIn.PLAYER_DUEL) {
					pd.targetPlayerID = packet.readShort();
				} else if (opcode == OpcodeIn.DUEL_OFFER_ITEM) {
					pd.duelCount = packet.readByte();
					pd.duelCatalogIDs = new int[pd.duelCount];
					pd.duelAmounts = new int[pd.duelCount];
					pd.duelNoted = new boolean[pd.duelCount];
					for (int slot = 0; slot < pd.duelCount; slot++) {
						pd.duelCatalogIDs[slot] = packet.readShort();
						pd.duelAmounts[slot] = packet.readInt();
						pd.duelNoted[slot] = false;
					}
				} else if (opcode == OpcodeIn.DUEL_FIRST_SETTINGS_CHANGED) {
					pd.disallowRetreat = packet.readByte();
					pd.disallowMagic = packet.readByte();
					pd.disallowPrayer = packet.readByte();
					pd.disallowWeapons = packet.readByte();
				}
				result = pd;
				break;

			case PLAYER_INIT_TRADE_REQUEST:
			case PLAYER_ACCEPTED_INIT_TRADE_REQUEST:
			case PLAYER_ACCEPTED_TRADE:
			case PLAYER_DECLINED_TRADE:
			case PLAYER_ADDED_ITEMS_TO_TRADE_OFFER:
				PlayerTradeStruct pt = new PlayerTradeStruct();
				if (opcode == OpcodeIn.PLAYER_INIT_TRADE_REQUEST) {
					pt.targetPlayerID = packet.readShort();
				} else if (opcode == OpcodeIn.PLAYER_ADDED_ITEMS_TO_TRADE_OFFER) {
					pt.tradeCount = packet.readByte();
					pt.tradeCatalogIDs = new int[pt.tradeCount];
					pt.tradeAmounts = new int[pt.tradeCount];
					pt.tradeNoted = new boolean[pt.tradeCount];
					for (int slot = 0; slot < pt.tradeCount; slot++) {
						pt.tradeCatalogIDs[slot] = packet.readShort();
						pt.tradeAmounts[slot] = packet.readInt();
						pt.tradeNoted[slot] = false;
					}
				}
				result = pt;
				break;

			case PRAYER_ACTIVATED:
			case PRAYER_DEACTIVATED:
				PrayerStruct p = new PrayerStruct();
				p.prayerID = packet.readByte();
				result = p;
				break;

			case GAME_SETTINGS_CHANGED:
				GameSettingStruct gs = new GameSettingStruct();
				int setting = gs.index = packet.readByte();
				int value = gs.value = packet.readByte();
				if (setting == 0) {
					gs.cameraModeAuto = value;
				} else if (setting == 2) {
					gs.mouseButtonOne = value;
				}
				result = gs;
				break;

			case PRIVACY_SETTINGS_CHANGED:
				PrivacySettingsStruct pr = new PrivacySettingsStruct();
				pr.blockChat = packet.readByte();
				pr.blockPrivate = packet.readByte();
				pr.blockTrade = packet.readByte();
				pr.blockDuel = packet.readByte();
				result = pr;
				break;

			case CHANGE_PASS:
			case SET_RECOVERY:
			case SET_DETAILS:
				SecuritySettingsStruct sec = new SecuritySettingsStruct();
				if (opcode == OpcodeIn.CHANGE_PASS) {
					// Get encrypted block
					// old + new password is always 40 characters long, with spaces at the end.
					// each blocks having encrypted 7 chars of password
					int blockLen;
					byte[] decBlock; // current decrypted block
					int session =  player.sessionId;
					int receivedSession;
					boolean errored = false;
					byte[] concatPassData = new byte[42];
					for (int i = 0; i < 6; i++) {
						blockLen = packet.readUnsignedByte();
						decBlock = Crypto.decryptRSA(packet.readBytes(blockLen), 0, blockLen);
						// TODO: there are ignored nonces at the beginning of the decrypted block
						receivedSession = ByteBuffer.wrap(Arrays.copyOfRange(decBlock, 4, 8)).getInt();
						// decrypted packet must be of length 15
						if (session == -1 && decBlock.length == 15) {
							session = receivedSession;
						} else if (session != receivedSession || decBlock.length != 15) {
							errored = true; // decryption error occurred
						}

						if (!errored) {
							System.arraycopy(decBlock, 8, concatPassData, i * 7, 7);
						}
					}

					String oldPassword = "";
					String newPassword = "";
					try {
						oldPassword = new String(Arrays.copyOfRange(concatPassData, 0, 20), "UTF8").trim();
						newPassword = new String(Arrays.copyOfRange(concatPassData, 20, 42), "UTF8").trim();
					} catch (Exception ex1) {
						//LOGGER.info("error parsing passwords in change password block");
						errored = true;
						ex1.printStackTrace();
					}

					if (!errored) {
						sec.passwords = new String[]{ oldPassword, newPassword };
					}
				} else if (opcode == OpcodeIn.SET_RECOVERY) {
					// Get the 5 recovery answers
					int blockLen;
					byte[] decBlock; // current decrypted block
					int session =  player.sessionId;
					int receivedSession;
					boolean errored = false;
					int questLen = 0;
					int answerLen = 0;
					int expBlocks = 0;
					byte[] answerData;
					String questions[] = new String[5];
					String answers[] = new String[5];
					for (int i = 0; i < 5; i++) {
						questLen = packet.readUnsignedByte();
						questions[i] = new String(packet.readBytes(questLen));
						answerLen = packet.readUnsignedByte();
						// Get encrypted block for answers
						expBlocks = (int)Math.ceil(answerLen / 7.0);
						answerData = new byte[expBlocks * 7];
						for (int j = 0; j < expBlocks; j++) {
							blockLen = packet.readUnsignedByte();
							decBlock = Crypto.decryptRSA(packet.readBytes(blockLen), 0, blockLen);
							// TODO: there are ignored nonces at the beginning of the decrypted block
							receivedSession = ByteBuffer.wrap(Arrays.copyOfRange(decBlock, 4, 8)).getInt();
							// decrypted packet must be of length 15
							if (session == -1 && decBlock.length == 15) {
								session = receivedSession;
							} else if (session != receivedSession || decBlock.length != 15) {
								errored = true; // decryption error occurred
							}

							if (!errored) {
								System.arraycopy(decBlock, 8, answerData, j * 7, 7);
							}
						}

						try {
							answers[i] = new String(answerData, "UTF8").trim();
						} catch (Exception ex) {
							//LOGGER.info("error parsing answer " + i + " in change recovery block");
							errored = true;
							ex.printStackTrace();
						}
					}

					if (!errored) {
						sec.questions = questions.clone();
						sec.answers = answers.clone();
					}
				} else if (opcode == OpcodeIn.SET_DETAILS) {
					boolean errored = false;
					int expLen = 0;
					String details[] = new String[4];
					for (int i = 0; i < 4; i++) {
						expLen = packet.readUnsignedByte();
						details[i] = new String(packet.readBytes(expLen));
						if (details[i].length() != expLen) errored = true;
					}

					if (!errored) {
						sec.details = details.clone();
					}
				}
				result = sec;
				break;

			case HEARTBEAT:
			case LOGOUT:
			case CONFIRM_LOGOUT:
				NoPayloadStruct n = new NoPayloadStruct();
				result = n;
				break;

			case WALK_TO_POINT:
			case WALK_TO_ENTITY:
				WalkStruct w = new WalkStruct();
				w.firstStep = new Point(packet.readShort(), packet.readShort());

				int numWaypoints = packet.getReadableBytes() / 2;
				for (int stepCount = 0; stepCount < numWaypoints; stepCount++) {
					w.steps.add(new Point(packet.readByte(), packet.readByte()));
				}
				result = w;
				break;

			case KNOWN_PLAYERS:
				KnownPlayersStruct kp = new KnownPlayersStruct();
				kp.playerCount = packet.readShort();
				kp.playerServerIndex = new int[kp.playerCount];
				kp.playerServerAppearanceId = new int[kp.playerCount];
				for (int i = 0; i < kp.playerCount; i++) {
					kp.playerServerIndex[i] = packet.readShort();
					kp.playerServerAppearanceId[i] = packet.readShort();
				}
				result = kp;
				break;
		}

		if (result != null) {
			result.setOpcode(opcode);
		}

		return result;

	}

	private String read115RSCString(byte[] data) {
		// TODO: refactor
		// good words - place and read from file
		String[] ygb = new String[]{"i","you","to","me","a","for","the","what","have","ok","it","do","no","lol","need","its","can","is","and","want","selling","how","get","iron","got","go","my","i'm","im","that","your","you're","youre","yourself","here","buy","don't","dont","i'll","ill","hey","in","free","on","buying","runes","of","will","where","much","press","hi","give","yes","are","not","one","armor","trade","all","this","make","am","some","just","know","ore","if","kill","bars","stuff","he","or","sell","gp","now","plz","money","help","more","who","so","oh","there","any","gold","sale","coal","level","up","axe","adam","him","yeah","wait","bronze","anyone","come","follow","good","with","only","we","but","then","please","lvl","steel","mine","did","like","be","take","sorry","lets","why","cool","sword","back","them","was","shield","at","can't","cant","smithing","thanks","thx","armour","too","well","see","meat","out","food","arrows","an","man","legs","right","bar","black","each","rune","they","fight","helmet","pay","bye","stop","going","guy","quest","run","off","hehe","np","long","from","mith","were","yo","does","someone","thanx","key","new","sure","bead","way","pie","smith","tin","many","nope","when","hold","attack","anything","ha","weapon","plate","body","hello","better","about","other","clan","something","kite","newbie","red","pies","let","look","pk","think","haha","nice","copper","use","magic","time","hat","fire","mining","tell","yep","has","really","wine","away","law","could","amulet","cut","nature","helm","guys","died","name","over","bow","wow","would","nothing","huh","had","talk","mind","won't","wont","pick","find","first","show","friend","dude","join","blue","wants","as","again","killed","by","yup","down","air","his","didn't","didnt","done","mithril","'em","say","said","bank","spare","thing","goblin","chaos","people","beads","gems","silverlight","brb","sec","clay","battle","hammer","stats","full","sapphire","fine","mace","than","leave","keep","kind","yet","try","weapons","though","put","nah","later","shoot","accept","else","top","still","sup","shop","water","heh","okay","chest","cosmic","place","cya","shut","never","hit","these","ruby","cooked","bolts","should","bones","her","worth","staff","white","those","range","i've","ive","emerald","lost","thank","gotta","blurite","k","crossbow","door","doing","chefs","thnx","dagger","store","uncut","god","varrock","left","she","stay","offer","half","cheap","us","hmm","smelt","almost","apple","move","which","nm","enough","already","gave","ready","best","same","cook","ty","drop","dark","another","hahaha","type","chain","yellow","even","friends","room","made","short","two","getting","little","dunno","ores","been","big","following","very","c'mon","after","death","work","anybody","coins","hurry","weak","large","yah","deal","sry","pickaxe","lot","oops","maybe","hard","kool","rock","thought","ice","items","things","king","ah","extra","wot","close","fighting","lots","dead","cooking","wanted","meet","ask","yay","last","open","sir","sweet","around","trying","log","cause","trading","girl","square","um","cabbage","necklace","wool","list","into","guess","welcome","easy","price","beer","wizard","wrong","min","needs","oil","poor","next","diamond","soon","sold","high","furnace","earth","mines","took","start","none","archer","town","fun","coming","strong","ranged","ever","phoenix","eat","both","leg","ain't","aint","swords","moi","player","must","told","started","cost","space","arrav","talking","knight","before","play","pot","nuggets","because","killer","might","wood","while","flour","server","guild","found","isn't","isnt","ppl","crafting","milk","chef","lumbridge","funny","change","piece","light","boy","day","low","falador","everything","beat","cash","team","ghost","newbies","care","ahh","train","killing","doesn't","doesnt","ones","watch","remember","raw","village","call","logged","knights","uh","umm","anyway","non","skull","fast","bought","silver","alright","bring","person","our","cross","hehehe","own","turn","prayer","wish","hmmm","making","levels","rich","everyone","jk","looking","attacked","lead","green","daggers","hp","used","bread","skirt","every","times","logs","today","saw","points","check","potion","great","gone","also","per","night","guard","sum","far","once","till","strength","i'd","id","lord","went","brass","always","add","giving","giants","takes","redberry","hahahaha","few","rocks","ohh","chicken","kinda","borrow","win","bit","gem","dwarf","pretty","crossbows","berries","wizards","eggs","hits","lose","castle","goblins","most","hole","bob","haven't","havent","whatever","side","needed","miner","amulets","says","called","egg","axes","seen","rat","burnt","may","war","outside","wear","nugget","boots","health","medium","power","berry","handed","eye","quick","shot","bear","anymore","bro","rats","shields","lucky","dish","arrow","lvls","spell","slow","myself","through","hay","heal","err","kills","sapphires","either","spaces","rest","ago","you'll","youll","chisel","near","garlic","ring","ned","master","gate","looks","dye","being","quests","three","higher","somebody","guards","bucket","robe","tried","forgot","together","advanced","actually","map","idea","their","part","item","waiting","smelting","mold","stronger","hear","bows","holy","problem","character","happy","south","less","aren't","arent","save","behind","jug","least","box","newt","walk","makes","hope","helmets","gives","fish","playing","peace","set","giant","barbarian","whoa","wearing","point","finish","luck","wasn't","wasnt","nevermind","warrior","dragon","grab","general","evil","ladder","orange","listen","without","shears","second","stake","buddy","bet","hah","anvil","under","heard","gets","worry","true","forget","color","hurt","face","scared","men","ground","char","inside","spot","shadow","leader","imp","wouldn't","wouldnt","taking","hiya","gtg","enchant","dropped","leather","suit","grr","enchanted","self","probably","sheep","running","faster","rare","plus","scimitar","since","keys","wind","archers","diamonds","except","carry","north","minute","days","training","delrith","ahead","unless","duh","until","matter","emeralds","super","kidding","speak","somewhere","raise","secret","vampire","drink","vault","defense","argh","minutes","tinderbox","stand","burn","member","goes","cooks","comes","chickens","came","arch","happened","finally","couldn't","couldnt","shall","heheh","chance","gloves","maces","push","knows","tower","witch","grain","able","tree","spider","tinder","pots","paid","mins","floor","fair","defence","road","certificate","using","teleport","such","interested","helping","others","myth","late","accuracy","yesterday","poison","barb","small","rid","wheat","promise","lend","pking","paying","adamantite","junk","busy","book","string","sigh","doh","whole","scorps","hatchet","asked","coals","awesome","monks","dishes","aha","shooting","lady","west","mould","broke","arm","anyways","meant","different","saying","ruins","ouch","means","dwarven","attacking","outta","scorpions","working","they're","theyre","demon","message","sometimes","common","area","upstairs","spells","dough","knew","grrr","craft","rule","city","works","week","that's","let's","he's","she's","it's","who's","what's","where's","how's","why's","falador's","varrock's","want's","here's","champ's","cook's","chief's","man's","mine's","thats","lets","hes","shes","its","whos","whats","wheres","hows","whys","faladors","varrocks","wants","heres","champs","cooks","chiefs","mans","mines","tail","leaving","rules","girls","gauge","front","smither","pker","wooden","helped","aww","asking","sun","juliet","fountain","prince","stairs","ranger","inventory","cow","draynor","church","wonder","romeo","sad","read","costs","word","skill","damage","woman","wassup","mix","willing","transfer","leggings","knife","hours","fighter","switch","skeleton","scorpion","fur","inn","stick","expensive","east","saving","end","tube","sells","port","drunk","stuck","normal","bored","understand","protect","moon","bury","mage","champs","monk","forever","bananas","tomorrow","distract","special","lawrence","answer","warriors","scorp","male","having","question","pieces","tough","imps","happens","cans","dungeon","chase","ooh","annoying","kicked","female","equipment","couple","cave","slayer","hour","instead","helms","farmer","lower","leaves","dig","mate","pull","send","kings","complete","highest","aw","reason","moss","words","cheers","boring","safe","peeps","arg","waste","hair","changed","twice","tired","practice","wolf","usually","cutting","wall","stone","round","onion","spiders","met","joking","finished","anvils","tons","smart","jolly","dose","apron","course","won","runs","letters","bunch","melt","final","sit","boar","besides","wines","goto","cobweb","names","npk","boo","tonight","past","party","moment","form","building","pressure","standing","pit","nobody","bah","sounds","root","favor","bridge","necklaces","'cos","beg","quite","meeting","library","kharid","joke","ic","herald","button","happen","against","pile","likes","corner","ton","storage","return","sewers","bone","badly","miss","mess","longbow","lalala","excuse","closed","rope","keeps","impure","die","dies","serious","pirates","kites","cheaper","woah","skills","reward","molds","closet","charge","become","adams","total","test","sewer","den","bk","onions","neither","meats","learn","everybody","woad","supposed","nearly","mined","trees","lala","ook","mark","kinds","chop","uniform","swap","metal","count","afford","wiz","smithed","rings","picked","moo","line","ashes","cares","woohoo","women","telling","rubies","locked","hats","dwarves","colors","cloud","anywhere","thief","skeletons","plenty","nearest","farm","equip","dying","cast","added","traded","gained","clans","steak","row","brown","pks","earlier","coin","ace","teach","spent","longsword","curse","sleep","players","members","exactly","dwarfs","depends","spy","named","lying","group","above","starting","appear","rofl","picture","pm","kept","easier","doors","block","monsters","manor","field","worked","whoops","whoever","fan","cadava","along","shouldn't","shouldnt","shortbow","bolt","blah","staffs","plates","no-one","clue","chat","zombies","scroll","rawmeat","ate","ruin","limp","laugh","fill","helps","empty","business","trip","star","squire","scary","report","rather","throw","saved","potions","number","mugger","gotten","file","wheel","cadavaberries","blood","weaken","status","sort","wondering","played","hunter","trouble","pink","mossy","okie","fought","dangerous","champions","break","zero","weeks","shirt","collect","split","loose","cough","combat","truce","summer","powerful","middle","zombie","loss","enjoy","archery","thinking","prove","nvm","catch","attacks","stat","snake","sarim","oic","forge","build","boys","whew","cold","thinks","prices","missed","thankyou","seriously","nights","heals","fires","figure","longer","wig","taken","private","talked","table","clothes","ally","write","stock","sky","seeya","redberries","monster","holding","hide","cows","cannot","advance","you've","youve","main","limpwurt","yawn","stores","sound","roll","partner","messed","info","ghosts","ernest","erm","choose","broken","soul","pour","mountain","exp","case","ali","rimmington","plan","increase","entrance","crowded","banana","healing","eyes","boss","refine","places","phew","mode","hitpoints","supply","lever","dieing","ash","afraid","seem","pastry","hero","etc","dropping","doric","between","queen","often","museum","jump","grim","mill","grave","given","prayers","non-pk","losing","forest","champ","turned","step","healed","grapes","chainmail","cabbages","anytime","typing","type","types","spawn","mistake","guide","careful","tells","post","offering","month","fit","enter","chill","walking","profit","mega","confused","bks","alive","suppose","redwine","bother","beside","useless","snow","search","outfit","lum","easily","earn","dudes","recharge","ignore","honest","bucks","straight","spinning","regular","grow","upper","moving","deposit","bid","bears","across","trades","tip","morning","evening","msg","hose","fell","fall","dare","completed","accepted","spend","priest","loan","disconnected","agree","sign","pirate","mission","miners","leela","ghostspeak","fly","flower","watching","thin","magical","gunthor","fresh","flame","cooper","beers","seems","runite","muggers","hunt","hire","cookedmeat","colour","calling","bag","awhile","armors","request","rain","paladin","hobgoblins","games","beard","army","tommorow","seconds","mighty","laughing","fred","apples","wild","whose","ways","temple","song","skulls","secs","sail","robes","kit","interesting","closest","apothecary","yum","worthless","weren't","werent","unicorn","stove","spade","gosh","battleaxe","ferment","yummy","wilderness","stout","shops","match","burned","smelter","pray","nose","mostly","amount","weaker","smithy","platemail","fights","windmill","purple","pkers","perfect","pans","months","gift","fault","cheese","below","raises","hungry","heya","heaps","drain","closer","choice","barely","aye","smelted","peoples","glad","crafter","stopped","prospect","pair","joining","hitting","examine","enemy","duck","smiths","sister","possible","market","looked","levers","golden","yarn","winning","uncooked","silent","spears","parts","lock","btw","board","bishop","bake","truth","strongest","lumb","cry","alchemy","stew","howdy","fail","everywhere","downstairs","doubt","club","barbarians","arms","window","palace","moulds","fishing","dart","clear","bigger","begging","adds","woodcutting","wing","telekinetic","supplies","pure","major","important","goodbye","fortress","dusty","asgarnia","worse","spots","sob","reldo","random","quickly","buys","worst","value","tsk","they'll","theyll","target","blackarm","mister","joined","indeed","dinner","ammo","agent","wise","valuable","tramp","swamp","starts","path","mass","manhole","stars","spike","sometime","sense","roar","revenge","rescue","offline","listening","impossible","handle","fence","fellow","element","dear","caves","swing","swift","storm","simple","sets","plated","option","notice","enchanting","eating","donate","chars","venom","showed","provide","prefer","oven","order","language","ladies","hammers","characters","yell","wasted","solid","sheesh","sake","loud","kebabs","hunting","herblaw","class","switched","skirts","pub","noticed","messages","lone","knock","keeping","improve","exit","decent","basement","trader","takers","sugar","staying","sand","regenerate","pack","fairy","failed","allies","wasting","walked","showing","roots","quiet","privacy","ooops","odd","incantation","hundred","honor","hideout","hail","further","foot","explain","experience","desert","corn","ale","yuck","ultra","third","taste","tall","quicker","offered","neat","missing","invisible","hugs","grey","faced","duel","difference","cover","unfermented","street","sneak","proud","fix","due","boom","pked","picks","mystic","materials","maker","machine","lest","land","figured","farther","cupboard","collateral","thou","thee","that'll","thatll","prize","present","ninja","misty","million","lowest","horse","hardly","cloak","brought","tag","sent","royal","ranging","putting","partners","jumped","irons","ingredients","headed","gypsy","grill","exact","crush","brothers","blocked","whenever","skin","respawn","finding","certain","center","bark","average","writing","useful","turns","style","sink","shear","scimitars","passed","onto","manage","leaders","jail","including","highway","froze","enemies","drops","deep","comment","chasing","brain","beast","base","bald","armours","angry","seller","santa","rebel","honey","holder","himself","hasn't","hasnt","fletching","eyeball","equal","direction","clean","checking","changing","wins","user","shows","select","seeing","scare","reach","older","normally","librarian","herbs","heading","desperate","darkwizards","chiefs","band","act","thy","strange","size","single","shots","paper","ours","loot","filled","extras","elite","compared","beep","basic","threw","spelled","park","music","hut","danger","dance","bounty","winner","wink","timber","survive","sunday","silk","shame","sailor","rainbow","pickaxes","opens","lovely","longs","intruder","hatchets","fact","exchange","especially","during","brave","born","attention","superheat","letting","human","gulp","funky","byebye","wake","thousand","tele","pole","perhaps","original","liked","increases","imcando","favour","dream","destroy","zone","yikes","weakest","trap","thurgo","speaking","restore","race","picking","owned","otherwise","jewels","jewelry","information","furnaces","drank","crying","confuse","vote","stash","plays","pipe","payment","opening","offers","mrs","morgan","messing","material","misunderstand","understood","misunderstood","misheard","magician","lately","heat","gardener","fear","eek","command","changes","alliance","totally","puts","horvik","hobs","hardest","freebies","force","cowboy","correct","collecting","champion","burning","bond","beginner","beautiful","action","task","mistakes","story","slowly","we'll","well","sheriff","osman","newts","moved","loading","laughs","hint","forth","forsale","firemaking","dip","alrighty","whistle","letter","hopefully","generous","fin","eventually","dyes","deadly","collection","coffin","carrying","camel","cake","bloke","adamantine","accidently","woods","waldo","turtle","tiny","tackle","squares","pottery","maze","loaded","lighting","kebab","goods","gather","fired","extreme","epic","early","doubled","climb","brings","appreciate","allowed","allot","zoom","weaklings","warm","visit","unfired","term","smithers","rogue","recently","raising","raised","proof","promised","obtain","mates","matches","leveled","history","herd","hall","guest","goldbar","fermented","expect","dungeons","create","animal","aggie","wanting","ultimate","trapped","spelling","sitting","'scuse","retreat","respect","repay","pressed","pow","mainly","leads","kitchen","gotcha","fund","followed","favorite","cloths","caught","burying","blink","bean","bartender","art","worthy","warlock","uses","spar","smiles","sharp","restless","rangers","questions","precious","options","offense","katrine","interest","hop","hiding","grant","glass","fruit","fireball","eternal","compost","combo","children","chased","challenge","bidder","barrow","amazing","alike","adventure","walls","underground","solve","several","rate","hobgoblin","helper","harlow","graveyard","gates","frost","foe","deeper","darkwizard","cage","beef","baker","whow","wars","turning","tries","talks","sos","shovel","shopping","practicing","pizza","numbers","nowhere","location","instant","icon","hush","hers","happening","guessing","gray","freeze","duke","clock","chestplate","chains","calm","buds","beggar","basically","attend","appears","yard","wk","weakness","trips","treasure","track","towards","somehow","silence","rusty","rocky","reading","pardon","oilcan","noon","mason","lizard","itself","flashy","farmers","discount","deserve","controlled","confusing","comic","cell","brand","boot","bonus","bathroom","asleep","arrgh","angle","whisper","trusted","snipper","shoes","sales","sage","rumble","river","rice","repeat","remove","recover","reasonable","rank","pocket","paste","obviously","nicely","news","journey","hiring","grand","feed","decide","created","colours","buyers","bottle","boat","blacksmith","belong","beauty","assistant","accurate","women's","womens","witches","weakened","travel","teleported","support","stands","sleeping","skate","shout","shake","scrolls","restock","quality","princess","plain","opened","object","monday","meaning","mansion","lesson","learned","knives","focus","fighters","eats","ear","drinking","dizzy","broadsword","bothering","blonde","blame","banker","bakery","armourer","admit","adding","wield","view","vest","thrander","swamps","spun","someday","soldier","servant","raining","paint","omen","limproot","leveling","known","knowing","however","hill","heavy","frank","entire","energy","enchantment","drinks","divide","defend","customer","combination","closing","carlem","buddies","breath","begin","battling","bats","alter","would've","wouldve","worries","spawns","rpg","profitable","products","outlaw","mixed","method","melting","icy","hitpoint","henge","glory","future","fully","eastern","decided","darkness","crossbones","creature","contest","claim","certificates","casting","carrier","bush","buckets","booty","bargain","altar","wash","uninteresting","switches","runner","risk","realize","proper","mountains","merci","likely","fishy","fails","everyday","edge","earned","dragons","docks","coolest","continue","arching","waited","spring","spares","shade","score","restart","regen","purpose","popular","nicer","keeper","jewel","held","heap","grin","goal","foods","figures","fiend","explore","eep","coast","clap","chief","chests","blocks","battles","bash","balance","backwards","animals","alley","wrath","wonderful","usual","unknown","twist","teamed","tanned","sisters","shorts","reporting","regain","print","ppls","posted","poisoned","owns","mention","lunch","lousy","hoping","highwayman","gladiator","fortune","fort","flier","fixed","feast","falls","failing","elf","disguise","dawn","curious","crystal","chances","bothered","armored","acting","accepting","within","various","unlike","unicorns","sunshine","spud","sorts","slay","sight","sighs","shrugs","shortcut","shiny","shearer","safer","sack","route","rooms","reply","regenerates","natures","leading","lair","jokes","jobs","glove","garden","frog","flying","fisherman","fare","excellent","dokie","dock","customers","checked","bottles","bold","bin","bags","assassin","asks","artist","aggressive","advice","adventurer","tunnel","teleporting","suggest","smash","sides","shortsword","recruit","recon","planning","pity","oldest","mirth","march","lease","learning","laws","kingdom","invincible","injured","inv","hopper","ham","graph","gambler","fried","fork","follows","folks","fisher","fields","fastest","excited","dressed","directions","difficult","defeat","crunch","creatures","clothing","closes","circle","cheapest","central","cart","captain","camp","buyer","breakfast","blocking","beans","agreed","bankaccount","worried","windows","weekend","upon","unlock","triple","transferring","themselves","swordsmen","summon","sudden","stayed","slice","singing","qp","penguin","packed","overall","opposite","nearby","mercy","manifest","mails","ladders","hidden","heaven","haunted","gamer","gaining","friendly","friar","females","fancy","eater","direct","chaps","burns","buildings","bringing","brains","blond","blank","beginning","bearded","backpack","avoid","armed","accidentally","wondered","wherever","undead","typo","treat","trained","tools","teams","tale","succeed","stonehenge","spinner","spinach","skip","ship","runestones","returns","regenerating","refill","rarely","note","needing","muhaha","lines","laters","june","joy","ivy","involved","hooray","friday","flat","fishfood","fairly","emergency","defensive","cure","crew","court","chew","chatting","butter","bodies","although","actual","wrote","withdraw","wigs","wave","walks","waits","unenchanted","toast","teaching","stored","stopping","sly","slide","shower","should've","shouldve","shopkeeper","sending","seek","scream","saves","rod","project","powers","patient","ocean","mound","mills","masked","lumby","judge","island","improves","hazy","greetings","giggles","furs","flips","flip","fermenting","famous","falling","easiest","cries","counts","considering","combine","coffee","batch","barbs","allow","warned","vampires","valley","update","unlucky","thieves","that'd","thatd","teeth","teaming","tastes","tails","steps","stays","stair","spoke","specific","smiley","smile","singer","saturday","rush","rolls","require","replace","rent","remind","regret","reckon","pretend","pressing","prepare","pointy","pointless","multiple","loyal","lonely","impressed","hurray","honestly","halt","groups","generals","freezing","facing","example","enabled","elsewhere","effort","draw","donations","disappeared","cuts","cup","could've","couldve","congo","clever","cleric","chair","cabin","buried","brick","boost","bits","banks","available","assistance","armory","aloud","vine","victory","unwanted","trail","toward","tied","thousands","tens","surprised","surprise","stops","staring","northwest","southwest","smaller","slider","rum","rot","risky","restores","respond","respawns","requires","remembered","potter","portrait","passing","oiling","obvious","northeast","nonplayer","newspaper","minion","minds","mile","magics","located","limited","limit","lethal","knots","july","impact","horses","hired","herb","growing","grabbed","gnarly","gains","freedom","forgive","fate","fallen","exist","ease","distracted","crushed","counter","coppers","copperore","convert","christmas","cars","cane","camels","butcher","books","blacks","beware","bend","beggars","awake","attitude","afterwards","voice","tour","tempting","temp","surely","suite","study","stated","squires","sport","spelt","slim","sidekick","shrug","shortbows","shining","shadows","searching","samurai","ruined","rounds","richer","requests","redberrypie","rarest","produce","prepared","pictures","person","peksa","patience","objects","nightmare","mud","minus","minerals","merry","meal","matters","mages","lords","loop","loaf","liquid","lift","lent","knees","intelligence","insist","holds","harbor","handy","guessed","grown","greatest","grabs","grabbing","goodness","glow","firing","filling","feasting","familiar","everlasting","ditch","deposited","demand","cyan","curses","crowd","costume","conversation","contains","consider","completely","colored","circles","ciao","beyond","baked","automatically","arena","answers","amounts","altogether","wreck","worker","wizardshat","warn","warlord","wannabe","vend","trough","tome","teatime","survivor","stranger","stouts","sports","sow","soot","sooner","songs","snore","smilie","slightly","sire","signed","sheet","shack","settle","scout","scientist","runeite","rolling","rise","record","reappear","rating","pairs","nest","nerves","mens","lure","loyalty","literally","harm","handing","genius","fourth","fever","faces","experienced","exceed","employee","distance","disappear","diplomacy","digging","deserved","definitely","current","crimson","crashed","core","condition","complicated","complain","chocolate","chisels","cheer","cheapo","charges","catching","burntmeat","blazing","bids","barren","barrel","auction","attacker","asap","advancing","ability","woman's","womans","wished","waves","wartface","wandering","waist","vulture","valuables","vacation","tunes","tear","tasty","swapping","summit","strikes","stood","sting","sticks","stack","spoken","sole","snap","smelts","slurp","shy","shed","shearing","shape","seagull","sarcastic","sandwich","safety","rotfl","rookie","role","resist","rents","removed","relax","refuse","refined","randomly","prizes","praying","potato","popped","pearl","peaceful","nun","noo","nooo","noooo","mugged","miles","midnight","metals","mask","lowers","lowered","lighter","jumping","jester","jest","include","imprint","impressive","humph","horrible","hooked","hid","healthy","haven","handsome","halves","flow","elemental","earnest","doses","divided","disconnect","digger","demons","deliver","darling","darkside","cutter","counting","cooker","chrome","chopping","charity","breaking","brake","brag","border","bodyguard","billion","barn","baraek","awful","autumn","aside","armorer","areas","answering","answered","alligator","al-kharid","younger","yippy","worm","whining","weather","weaponry","villa","vanilla","truly","trek","transform","tours","tougher","torch","topic","theirs","targets","tables","sweetness","supreme","supper","suggestion","stared","stakes","stacks","squad","spending","speedy","smirks","slower","slayers","sings","services","sept","science","scar","savings","sample","sadly","sabotage","reported","repair","reminds","regeneration","recognize","raid","puzzle","pushing","pushed","powered","polite","pitiful","pace","owed","opponent","o'clock","nag","mystery","modified","moans","maps","luckily","legend","instructions","increased","impatient","iced","hurricane","hilarious","hides","helpless","hearing","halve","guardian","grows","grinder","gifts","ghoul","gabindo","frames","forward","fooling","finishing","explains","eve","debt","deathless","curator","crime","crafted","cotton","controls","constantly","constant","commando","charging","channel","celebrate","brow","breaks","boxes","blinking","blanket","bath","bashing","appeared","affect","absolutely","woodcutters","woodcutter","watches","watched","wander","visual","vision","victor","vast","urban","upset","tribe","transformed","torn","throws","thirsty","tailoring","symbol","swim","surrounded","steam","stealers","staves","starter","speaks","slept","sleepy","skilled","setting","separate","sellers","screaming","scaring","scale","savage","sakes","ridiculous","response","recruiting","rally","rake","prysin","pros","professor","practically","possibly","pits","percent","peas","pale","ordinary","oars","npcs","newer","nearer","mob","mirror","mermaid","melted","marketplace","magicians","loses","listened","lake","kilt","justice","jungle","irritating","innocent","hundreds","humor","halfway","grief","gnome","forced","fletch","extremely","endless","ended","empires","drunken","doomed","destroyed","deserves","crumble","cruise","crossing","courtyard","compass","coat","click","clicks","chasm","chap","capital","cadavaberry","buttons","bulk","borrowing","blushes","beards","assure","anyhow","anger","afternoon","yawns","winter","what'll","whatll","oclock","wears","vip","victim","updated","typos","troll","trivia","timing","they've","theyve","they'd","theyd","terms","tap","swings","swag","sunny","suffer","streets","standard","speaker","soup","sorcerer","society","smooth","slicer","skillful","signs","shortly","shorter","seal","ruler","required","related","ranges","quester","purchase","pulled","potters","posts","position","population","poles","pleased","platinum","planned","pints","picky","patrol","operate","obstacle","obsessed","nod","nifty","monastery","mixing","mint","messes","meaty","managed","longest","lights","knot","keyprint","jackpot","ignoring","ideas","hunted","humble","hollow","hobgobs","hints","hermit","helpful","guarding","growl","graves","grape","goodnight","gasp","freebie","fetch","fakes","explosives","exploring","expert","enchants","directly","deed","crosses","cracks","cracked","coughs","cookers","compliment","competition","commander","combined","collector","claimed","chunk","cellar","cavern","catches","cards","bidding","became","assume","arrived","arrive","appearing","ancient","ambush","amazed","adamant","workers","wisely","when's","whens","unfortunately","underneath","today's","todays","territory","taught","surrender","sty","stubborn","stoop","stocks","spirit","southeast","souls","someplace","smarter","slipped","skele","skeles","sits","session","section","secrets","runesword","riddle","renegades","reasons","realised","pulling","profits","peasant","pattern","package","ourselves","officially","offended","odds","nicest","natural","moves","mizgog","missile","mentioned","medal","mature","matching","massive","lizardman","limits","laid","invented","holiday","highly","hedge","headquarters","hadn't","hadnt","grind","gremlin","grammar","grains","glasses","gladly","ginger","generation","funniest","frosty","follower","flames","farewell","faints","exciting","equals","effect","drill","dresser","disc","curved","creepy","counted","connected","complex","complaining","colorful","chips","chip","chant","certainly","cauldron","carts","carefully","bright","breathe","bragging","boing","blessed","becoming","battleaxes","basics","background","avatar","attire","apologize","yelled","xmas","worlds","wishes","whistles","whichever","whether","weakens","wand","updates","unit","tuesday","troubles","tricky","transferred","thursday","wednesday","throwing","throne","tests","testing","swamped","subtle","stump","stoves","stocking","sticker","station","stall","stabbed","spilt","sphere","speller","specially","spawning","spared","sounded","soldiers","smashed","simply","signing","shaking","sequence","senior","selves","selection","screenshot","scrap","scored","scavengers","scares","runestone","rotate","roof","romeo's","romeos","riches","reveal","retrieve","resources","remaining","recharging","recharged","ratio","rates","rampage","quote","pushes","public","pry","prospecting","propose","properly","process","priests","pricey","prey","plants","planet","pendant","payments","paradise","paladins","packs","ought","ork","ogre","non-pks","nap","mystical","missions","meetings","meanwhile","marching","manners","losses","laughed","kneed","intense","includes","inches","improved","immature","gunthor's","gunthors","guesses","grounds","grins","greeting","gown","gathering","gardening","forgotten","fold","flies","fixing","finest","fiery","ferments","estimate","equipped","enhancing","earning","dyed","dosh","dial","descent","depending","delicious","defeated","decision","december","darker","curiosity","crafters","covered","contain","confusion","compete","compare","cleared","choices","childish","charged","carries","brew","brawl","boats","blend","becomes","banked","baking","bacon","baa","auctioning","attached","ashamed","arguing","argue","aprons","annoyed","ambushed","aboard","fire-rune","water-rune","air-rune","earth-rune","mind-rune","body-rune","life-rune","death-rune","needle","nature-rune","chaos-rune","law-rune","thread","saradomin","unblessed","cosmic-rune","2-handed","burntbread","bad","chef's","chefs","reddye","yellowdye","shell","burntpie","faladian","knight's","knights","asgarnian","wizard's","wizards","mindbomb","rat's","rats","bluedye","unstrung","leaf","orangedye","zamorak","protection","karamja","tomato","incomplete","anchovie","partial","bowl","shrimp","anchovies","sardine","salmon","trout","herring","pike","tuna","swordfish","lobster","harpoon","bait","feather","returning","magenta","plank","tile","muddy","nails","anti","pumpkin","guam","marrentill","tarromin","harralander","ranarr","irit","avantoe","kwuarm","cadantine","unfinished","vial","pestle","mortar","snape","restoration","dramen","branch","hans","urhney","traiborn","rovin","lesser","lessers","greaters","jonny","veronica","weaponsmaster","oddenstein","bat","aubury","lowe","thessalia","zaff","zeke","louie","dr","mr","cassie","ranael","greldo","amik","varze","guildmaster","valaine","drogo","flynn","wyson","hassan","joe","keli","jailguard","redbeard","wydin","brian","vyvin","wayne","barmaid","hetty","betty","bentnoze","herquin","rommik","grum","customs","officer","luthas","zambo","tobias","gerrant","seaman","lorris","thresnor","tanner","dommik","abbot","langley","thordur","jered","melzar","mad","scavvo","greater","oziach","wormbrain","klarense","oracle","druid","baby","kaqemeex","sanfew","leprechaun","entrana","irksol","lunderwin","jakut","doorman","treestump","longtable","gravestone","bench","candles","landscape","millstones","palmtree","fern","cactus","bullrushes","mushroom","railing","pillar","bookcase","chute","sacks","signpost","dolmen","sails","cobweb","spiderweb","doric's","dorics","potter's","potters","crate","fungus","carcass","guthix","thunder","doorframe","railings","battlement","arrowslit","crumbled","strike","blast","burst","clarity","superhuman","reflexes","rapid","incredible","paralyze","missiles","volcano","volcanos","crandor","pier","al","edgeville","prison","gaol","zanaris","isle","lobbies","swordies","lobby","swordy","lava","tomb","crypt","plateau","overgrown","plantation","planks","nail","tiles","lobsters","paralyzes","crates","fungi","bookcases","pillars","railings","mushrooms","signposts","cacti","ferns","benches","gravestones","pumpkins","halloween","feathers","pikes","herrings","sardines","anchovie","bowls","shrimps","trouts","tomatoes","fire-runes","water-runes","air-runes","earth-runes","mind-runes","body-runes","life-runes","death-runes","nature-runes","chaos-runes","law-runes","cosmic-runes","rs","runescape","game","fantasy","world","escape","scape","escaped","cape","capes","caped","mail","belief","believe","imagine","version",":-)",":^)",":)",":-p",":^p",":p","-)","^)",")","-p","^p","p",":-(",":(","-(","(","stupid","silly","daft","crazy","idiot","fool","dumb","thick","moron","nasty","horrid","mean","meanie","unfair","liar","trick","scam","scammer","scamming","stole","stolen","nicked","smell","smelly","stinking","stinker","stink","foul","hate","naff","dislike","rubbish","garbage","trash","terrible","pathetic","miserable","unhappy","sea","multiplayer","control","rewards","advantage","adventuring","spooky","unarmed","requirement","mithral","adamite","pizzas","symbols","saphire","saph","saphs","adamnite","system","clan","count","country","hola","mom","truck","violet","needle","addy","saffire","ammy","cobra","where'd","whered","he'll","nipped","dedicated","someone's","spoon","gang","doughnuts","speed","lag","laggy","technically","capacity","brother","harpoons","bluerose13x","puffin","puffffin","nicodeamus","michelle","pan","war","premium","wolves","strangers","brazil","vancouver","perchance","wicked","panther","she'll","ladykilljoy","arsenes","pennywise","addiction","someones","pony","kalika","clicking","kicking","spencer","unlocked","nightsword","arrowslits","hellhound","hellhounds","ponies","upwards","trenger","druidic","hence","predict","prediction","achetties","clicked","spanish","shapeshifter","shifter","compound","retired","update","everyones","buman","album","puzzles","gain","messenger","analog","van","blessing","convinced","banner","glassblowing","baconer","tytn","query","queries","argument","online","worldpay","tourist","cooky","cert","certs","u","ur","konger","ammies","butch","married","wally","equalizer","ranked","stamping","assasin","assasins","appearance","effects","makeover","typer","bass","doombringer","effective","wildy","wazzup","germany","knocked","shore","titan","pine","pineapple","competition","thomas"};
		char[] bhb = new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', ' ', '!', '?', '.', ',', ':', ';', '(', ')', '-', '&', '*', '\\', '\''};

		try {
			String var4 = "";
			String var5 = "";

			int var7;
			for(int var6 = 0; var6 < data.length; ++var6) {
				var7 = data[var6] & 255;
				if(var7 < 50) {
					var4 = var4 + bhb[var7];
				} else if(var7 < 70) {
					++var6;
					var4 = var4 + ygb[(var7 - 50) * 256 + (data[var6] & 255)] + " ";
				} else if(var7 < 90) {
					++var6;
					var4 = var4 + ygb[(var7 - 70) * 256 + (data[var6] & 255)];
				} else if(var7 < 255) {
					var4 = var4 + ygb[var7 - 90] + " ";
				} else {
					++var6;
					var7 = data[var6] & 255;
					if(var7 == 0) {
						var5 = "@red@";
					}

					if(var7 == 1) {
						var5 = "@gre@";
					}

					if(var7 == 2) {
						var5 = "@blu@";
					}

					if(var7 == 3) {
						var5 = "@cya@";
					}

					if(var7 == 4) {
						var5 = "@ran@";
					}

					if(var7 == 5) {
						var5 = "@whi@";
					}

					if(var7 == 6) {
						var5 = "@bla@";
					}

					if(var7 == 7) {
						var5 = "@ora@";
					}

					if(var7 == 8) {
						var5 = "@yel@";
					}

					if(var7 == 9) {
						var5 = "@mag@";
					}
				}
			}

			if(true) {
				for(var7 = 0; var7 < 2; ++var7) {
					String var8 = var4;
					var4 = hn(var4);
					if(var4.equals(var8)) {
						break;
					}
				}
			}

			if(var4.length() > 80) {
				var4 = var4.substring(0, 80);
			}

			var4 = var4.toLowerCase();
			String var12 = var5;
			boolean var13 = true;

			for(int var9 = 0; var9 < var4.length(); ++var9) {
				char var10 = var4.charAt(var9);
				if(var10 >= 97 && var10 <= 122 && var13) {
					var13 = false;
					var10 = (char)(var10 + 65 - 97);
				}

				if(var10 == 46 || var10 == 33 || var10 == 63) {
					var13 = true;
				}

				var12 = var12 + var10;
			}

			return var12;
		} catch (Exception var11) {
			var11.printStackTrace();
			return "eep!";
		}
	}

	private static String hn(String var0) {
		// TODO: refactor
		char[] ahb = new char[1000];
		// badwords - place and read from file
		String[] wgb = new String[]{"phuck","fuck","fuk","fux","fuq","faq","foc","fok","fook","fek","fack","foek","feck","fcuk","fukc","fck","fuick","fhuck","phuk","shit","chit","schit","shjt","shat","shet","siht","shti","sht","crap","bitch","bjtch","13itch","bich","biatch","biotch","bastard","damn","damm","spastic","retard","niga","nigr","niger","chink","wop","coon","hitler","nazi","sex","cyber","shag","hump","bugger","bugga","queer","kweer","gay","puf","fag","fagot","lesbian","lesbo","lesy","lesi","homo","hetro","bisex","penis","bellend","genital","dik","dick","wang","shlong","cock","cok","prick","pric","willy","boner","erection","bals","bollock","testicle","scrotum","nuts","clit","slit","cunt","vagina","vadge","fanny","twat","pusy","pusi","pussy","puss","breast","tit","tits","boob","niple","arse","anus","rectum","anal","butt","asshole","assh01e","urinate","piss","urine","turd","faeces","excrement","excrete","fart","cack","sperm","cum","spunk","smeg","semen","ejaculat","rape","rapist","stalk","wank","masturbate","masterbating","pimp","prostitut","perv","pervert","pedo","pedophile","paedo","paedophile","whore","hore","slaper","slag","slut","suck","lick","blojob","felat","cuniling","naked","undress","nude","condom","dildo","vibrator","bondage","spank","horny","throb","tampon","bloodrag","panty","porn","pasword","pass","pword","hack","cheat","exploit","duplicate","macro","mackro","automine","upgrad","dupe","duping","cjb","crud","pillock","sphosting","Sieag","heil","sieg","heil","fak","rshakz","facking","screw","freecfm","rsmods","dumbass","vze","hooker","nigga","modrune","ahole","negro","ljck","djck","tjts","hornie","musterbate","geocities","ass"};
		int vgb = 200;

		try {
			int var1 = var0.length();
			var0.toLowerCase().getChars(0, var1, ahb, 0);

			for(int var2 = 0; var2 < var1; ++var2) {
				char var3 = ahb[var2];

				for(int var4 = 0; var4 < vgb; ++var4) {
					String var5 = wgb[var4];
					char var6 = var5.charAt(0);
					if(dn(var6, var3, 0)) {
						int var7 = 1;
						int var8 = var5.length();
						char var9 = var5.charAt(1);
						int var10 = 0;
						if(var8 >= 6) {
							var10 = 1;
						}

						for(int var11 = var2 + 1; var11 < var1; ++var11) {
							char var12 = ahb[var11];
							if(dn(var9, var12, var8)) {
								++var7;
								if(var7 >= var8) {
									boolean var13 = false;

									for(int var14 = var2; var14 <= var11; ++var14) {
										if(var0.charAt(var14) >= 65 && var0.charAt(var14) <= 90) {
											var13 = true;
											break;
										}
									}

									if(!var13) {
										break;
									}

									String var15 = "";

									for(int var16 = 0; var16 < var0.length(); ++var16) {
										char var17 = var0.charAt(var16);
										if(var16 < var2 || var16 > var11 || var17 == 32 || var17 >= 97 && var17 <= 122) {
											var15 = var15 + var17;
										} else {
											var15 = var15 + "*";
										}
									}

									var0 = var15;
									break;
								}

								var6 = var9;
								var9 = var5.charAt(var7);
							} else if(!qn(var6, var12, var8)) {
								--var10;
								if(var10 < 0) {
									break;
								}
							}
						}
					}
				}
			}

			return var0;
		} catch (Exception var18) {
			return "wibble!";
		}
	}

	private static boolean dn(char paramChar1, char paramChar2, int paramInt)
	{
		if (paramChar1 == paramChar2) {
			return true;
		}
		if ((paramChar1 == 'i') && ((paramChar2 == 'y') || (paramChar2 == '1') || (paramChar2 == '!') || (paramChar2 == ':') || (paramChar2 == ';'))) {
			return true;
		}
		if ((paramChar1 == 's') && ((paramChar2 == '5') || (paramChar2 == 'z'))) {
			return true;
		}
		if ((paramChar1 == 'e') && (paramChar2 == '3')) {
			return true;
		}
		if ((paramChar1 == 'a') && (paramChar2 == '4')) {
			return true;
		}
		if ((paramChar1 == 'o') && ((paramChar2 == '0') || (paramChar2 == '*'))) {
			return true;
		}
		if ((paramChar1 == 'u') && (paramChar2 == 'v')) {
			return true;
		}
		if ((paramChar1 == 'c') && ((paramChar2 == '(') || (paramChar2 == 'k'))) {
			return true;
		}
		if ((paramChar1 == 'k') && ((paramChar2 == '(') || (paramChar2 == 'c'))) {
			return true;
		}
		if ((paramChar1 == 'w') && (paramChar2 == 'v')) {
			return true;
		}
		return (paramInt >= 4) && (paramChar1 == 'i') && (paramChar2 == 'l');
	}

	private static boolean qn(char paramChar1, char paramChar2, int paramInt)
	{
		if (paramChar1 == paramChar2) {
			return true;
		}
		if ((paramChar2 < 'a') || ((paramChar2 > 'u') && (paramChar2 != 'y'))) {
			return true;
		}
		if ((paramChar1 == 'i') && (paramChar2 == 'y')) {
			return true;
		}
		if ((paramChar1 == 'c') && (paramChar2 == 'k')) {
			return true;
		}
		if ((paramChar1 == 'k') && (paramChar2 == 'c')) {
			return true;
		}
		return (paramInt >= 5) && ((paramChar1 == 'a') || (paramChar1 == 'e') || (paramChar1 == 'i') || (paramChar1 == 'o') || (paramChar1 == 'u') || (paramChar1 == 'y')) && ((paramChar2 == 'a') || (paramChar2 == 'e') || (paramChar2 == 'i') || (paramChar2 == 'o') || (paramChar2 == 'u') || (paramChar2 == 'y'));
	}


	// a basic check is done on authentic opcodes against their possible lengths
	public static boolean isPossiblyValid(int opcode, int length, int protocolVer) {
		// no ISAAC in this version, don't need this
		return true;
	}
}
