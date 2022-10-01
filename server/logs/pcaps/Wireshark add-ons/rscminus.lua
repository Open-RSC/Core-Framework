-- RSCMINUS.LUA VERSION 2021-01-08 ALPHA
--
-- WARNING: This is being provided as an alpha
-- WARNING: Some opcodes may not be fully implemented.
-- WARNING: Some opcodes may report incorrect data, or be interpretted wrongly.
--
-- Please report any issues you encounter, but only when dissecting original Jagex RSC235 server data.
-- It is expected that private servers may have incorrect data that confuses this dissector.

rsc235_protocol = Proto("rsc235",  "RSC235 Protocol")

-- turn this on to see the packet dissection order window,
-- and some extra debugging fields on opcodes 191 & 104 (possibly others if this comment isn't updated)
thisDebugging = false

--============================================================================--
--=                                                                          =--
--=    README: install this file to your wireshark plugins folder.           =--
--=                                                                          =--
--=         In linux this is at ~/.config/wireshark/plugins/                 =--
--=         (create the folder if it doesn't exist)                          =--
--=                                                                          =--
--=         In windows it's at C:\Program Files\Wireshark\plugins\           =--
--=                                                                          =--
--=    You should also import the colouring rules from:                      =--
--=    "View -> Coloring Rules -> Import... -> "colouring_rules"             =--
--=                                                                          =--
--============================================================================--
--
-- example queries:
--   rsc235.234.updateType.5.equipment[10] == 0x51
--     shows only player updates where the player is wearing a golden amulet
--
--   !(rsc235.149.onlineStatus == 4 || rsc235.149.onlineStatus == 0) && rsc235.opcode == 149
--     shows only players updates from those on the same world as the recorder
--
--   !((rsc235.packetSource == 0 && (rsc235.opcode == 36 || rsc235.opcode == 48 || rsc235.opcode == 79 || rsc235.opcode == 91 || rsc235.opcode == 99 || rsc235.opcode == 104 || rsc235.opcode == 191 || rsc235.opcode == 211 || rsc235.opcode == 213 || (rsc235.opcode == 234 && !(rsc235.234.updateType == 1 || rsc235.234.updateType == 6)))) || rsc235.opcode == 67)
--     filter out most of the "background noise" of making sure the player stays in the world and the world stays sane
--     alternatively, remove the ! in front to only see "background noise"!
--
--   More examples can be found in the colouring_rules file
--
-- Documentation on wireshark bitwise operators
-- http://bitop.luajit.org/
--
--============================================================================--

--------------------------------------------------------------------------------
-- Custom opcodes
--------------------------------------------------------------------------------
VIRTUAL_OPCODE_CONNECT = 10000
VIRTUAL_OPCODE_SERVER_METADATA = 12345

--------------------------------------------------------------------------------
-- Server opcodes
--------------------------------------------------------------------------------
SERVER_OPCODES = {}
SERVER_OPCODES[4] = "CLOSE_CONNECTION_NOTIFY" -- same as opcode 165, but client will send opcode 31 if clientstream still exists
SERVER_OPCODES[5] = "QUEST_STATUS"
SERVER_OPCODES[6] = "UPDATE_STAKED_ITEMS_OPPONENT"
SERVER_OPCODES[15] = "UPDATE_TRADE_ACCEPTANCE"
SERVER_OPCODES[20] = "SHOW_CONFIRM_TRADE"
SERVER_OPCODES[25] = "FLOOR_SET"
SERVER_OPCODES[30] = "SYNC_DUEL_SETTINGS"
SERVER_OPCODES[33] = "UPDATE_XP"
SERVER_OPCODES[36] = "DISPLAY_TELEPORT_TELEGRAB_BUBBLE"
SERVER_OPCODES[42] = "OPEN_BANK"
SERVER_OPCODES[48] = "SCENERY_HANDLER" -- TODO: Confirm functionality
SERVER_OPCODES[51] = "PRIVACY_SETTINGS"
SERVER_OPCODES[52] = "UPDATE_SYSTEM_UPDATE_TIMER"
SERVER_OPCODES[53] = "SET_INVENTORY"
SERVER_OPCODES[59] = "SHOW_APPEARANCE_CHANGE"
SERVER_OPCODES[79] = "NPC_COORDS" -- TODO: coordinates are not always exactly correct, but tracking should be complete.
SERVER_OPCODES[83] = "DISPLAY_DEATH_SCREEN"
SERVER_OPCODES[84] = "WAKE_UP"
SERVER_OPCODES[87] = "SEND_PM"
SERVER_OPCODES[89] = "SHOW_DIALOGUE_SERVER_MESSAGE_NOT_TOP"
SERVER_OPCODES[90] = "SET_INVENTORY_SLOT" -- FINDOUT: confirm no opcode contains multiple data (most likely not, or else it would have been used on bank withdraw)
SERVER_OPCODES[91] = "BOUNDARY_HANDLER" -- TODO: confirm functionality
SERVER_OPCODES[92] = "INITIATE_TRADE"
SERVER_OPCODES[97] = "UPDATE_ITEMS_TRADED_TO_YOU"
SERVER_OPCODES[99] = "GROUND_ITEM_HANDLER" -- TODO: confirm functionality
SERVER_OPCODES[101] = "SHOW_SHOP"
SERVER_OPCODES[104] = "UPDATE_NPC" -- TODO: confirm functionality
SERVER_OPCODES[109] = "SET_IGNORE"
SERVER_OPCODES[111] = "COMPLETED_TUTORIAL" -- TODO: reconfirm after text changes
SERVER_OPCODES[114] = "SET_FATIGUE"
SERVER_OPCODES[117] = "FALL_ASLEEP"
SERVER_OPCODES[120] = "RECEIVE_PM"
SERVER_OPCODES[123] = "REMOVE_INVENTORY_SLOT"
SERVER_OPCODES[128] = "CONCLUDE_TRADE"
SERVER_OPCODES[131] = "SEND_MESSAGE" -- FINDOUT: figure out why eggsampler guy thinks the second username is "clan". as far as I can tell, it's just a duplicate of the username. the client uses it to check against the names in the ignore list.
SERVER_OPCODES[137] = "EXIT_SHOP"
SERVER_OPCODES[149] = "UPDATE_FRIEND"
SERVER_OPCODES[153] = "SET_EQUIP_STATS"
SERVER_OPCODES[156] = "SET_STATS"
SERVER_OPCODES[159] = "UPDATE_STAT"
SERVER_OPCODES[162] = "UPDATE_TRADE_RECIPIENT_ACCEPTANCE" -- TODO: reconfirm after text changes
SERVER_OPCODES[165] = "CLOSE_CONNECTION"
SERVER_OPCODES[172] = "SHOW_CONFIRM_DUEL" -- TODO: CONFIRM FUNCTIONALITY
SERVER_OPCODES[176] = "SHOW_DIALOGUE_DUEL" -- TODO: CONFIRM FUNCTIONALITY
SERVER_OPCODES[182] = "SHOW_WELCOME"
SERVER_OPCODES[183] = "DENY_LOGOUT"
SERVER_OPCODES[189] = "WRITE_28_BYTES_TO_RANDOM.DAT" -- Happens in zero replays out of 10,000+ but is in client.
SERVER_OPCODES[191] = "PLAYER_COORDS" -- TODO: There may be a bug still in the way players are stored; sometimes there are duplicates.
SERVER_OPCODES[194] = "INCORRECT_SLEEPWORD"
SERVER_OPCODES[203] = "CLOSE_BANK"
SERVER_OPCODES[204] = "PLAY_SOUND"
SERVER_OPCODES[206] = "SET_PRAYERS"
SERVER_OPCODES[210] = "UPDATE_DUEL_ACCEPTANCE" -- TODO: CONFIRM FUNCTIONALITY
SERVER_OPCODES[211] = "REMOVE_WORLD_ENTITY" -- removes whatever is at the X Y coordinate provided. can be boundary, scenery, or ground item. -- TODO: CONFIRM FUNCTIONALITY
SERVER_OPCODES[213] = "NO_OP_WHILE_WAITING_FOR_NEW_APPEARANCE"
SERVER_OPCODES[222] = "SHOW_DIALOGUE_SERVER_MESSAGE_TOP"
SERVER_OPCODES[225] = "CONCLUDE_DUEL_DIALOGUE"
SERVER_OPCODES[234] = "UPDATE_PLAYERS" -- FINDOUT: concretely figure out difference between type 3 and 4
SERVER_OPCODES[237] = "UPDATE_IGNORE_BECAUSE_OF_NAME_CHANGE" -- FINDOUT: search for a replay where field 4/2 are filled
SERVER_OPCODES[240] = "GAME_SETTINGS"
SERVER_OPCODES[244] = "SET_FATIGUE_SLEEPING"
SERVER_OPCODES[245] = "SHOW_DIALOGUE_MENU"
SERVER_OPCODES[249] = "UPDATE_BANK_ITEMS_DISPLAY"
SERVER_OPCODES[252] = "DISABLE_OPTION_MENU"
SERVER_OPCODES[253] = "UPDATE_DUEL_OPPONENT_ACCEPTANCE"
SERVER_OPCODES[10000] = "VIRTUAL_OPCODE_LOGIN_RESPONSE"
SERVER_OPCODES[12345] = "VIRTUAL_OPCODE_SERVER_METADATA"

--------------------------------------------------------------------------------
-- Client opcodes
--------------------------------------------------------------------------------
CLIENT_OPCODES = {}
CLIENT_OPCODES[0] = "CONNECT" -- done
-- CLIENT_OPCODES[3] = "SEND_DEBUG_INFO" -- 204 opcode (and possibly earlier) removed in 233, used to send various debug info to jagex if the client was erroring. unlikely that any information was sent back from server in response.
CLIENT_OPCODES[4] = "CAST_ON_INVENTORY_ITEM" -- TODO: confirm functionality
CLIENT_OPCODES[8] = "SEND_DUEL_SETTINGS" -- TODO: confirm working
CLIENT_OPCODES[14] = "INTERACT_WITH_BOUNDARY" -- done
CLIENT_OPCODES[16] = "WALK_AND_PERFORM_ACTION" -- done
CLIENT_OPCODES[22] = "BANK_WITHDRAW" -- done
CLIENT_OPCODES[23] = "BANK_DEPOSIT" -- done
CLIENT_OPCODES[29] = "SEND_COMBAT_STYLE" -- done
CLIENT_OPCODES[31] = "CLOSE_CONNECTION_REPLY" -- done
-- CLIENT_OPCODES[32] = "SESSION" -- 204 opcode (and possibly earlier) removed in 233, used to be able to detect if Login Server was offline
CLIENT_OPCODES[33] = "SEND_STAKED_ITEMS" -- TODO: confirm functionality
CLIENT_OPCODES[38] = "SEND_COMMAND_STRING" -- done
CLIENT_OPCODES[45] = "SEND_SLEEPWORD" -- done
CLIENT_OPCODES[46] = "OFFER_TRADE_ITEM" -- done
CLIENT_OPCODES[50] = "CAST_NPC" -- done
CLIENT_OPCODES[53] = "USE_ON_GROUND_ITEM" -- TODO: confirm functionality
CLIENT_OPCODES[55] = "SET_TRADE_ACCEPTED_TRUE" -- done because it's only the opcode
CLIENT_OPCODES[59] = "ADMIN_TELEPORT_TO_TILE" -- done; can see admins tping around in final day replays most likely using this opcode
CLIENT_OPCODES[60] = "ENABLE_PRAYER" -- done
CLIENT_OPCODES[64] = "SEND_PRIVACY_SETTINGS" -- TODO: confirm functionality, expand meanings
CLIENT_OPCODES[67] = "HEARTBEAT" -- todo: probably add explanation that this is what keeps the server from considering client dead
CLIENT_OPCODES[77] = "DUEL_CONFIRM_ACCEPT" -- done because it's only the opcode
CLIENT_OPCODES[79] = "INTERACT_WITH_SCENERY_OPTION_2" -- done
CLIENT_OPCODES[84] = "SKIP_TUTORIAL" -- done because it's only the opcode
CLIENT_OPCODES[90] = "ACTIVATE_INVENTORY_ITEM" -- done
CLIENT_OPCODES[91] = "COMBINE_INVENTORY_ITEMS" -- done
CLIENT_OPCODES[99] = "CAST_ON_SCENERY" -- TODO: confirm functionality
CLIENT_OPCODES[102] = "REQUEST_LOGOUT" -- done because it's only the opcode
CLIENT_OPCODES[103] = "SEND_DUEL_REQUEST" -- TODO: confirm functionality
CLIENT_OPCODES[104] = "CONFIRM_ACCEPT_TRADE" -- done because it's only the opcode
CLIENT_OPCODES[111] = "SEND_CLIENT_SETTINGS" -- TODO: confirm functionality
CLIENT_OPCODES[113] = "USE_WITH_PLAYER" -- TODO: confirm functionality
CLIENT_OPCODES[115] = "USE_WITH_SCENERY" -- TODO: confirm functionality
CLIENT_OPCODES[116] = "SELECT_DIALOGUE_OPTION" -- done
CLIENT_OPCODES[127] = "INTERACT_WITH_BOUNDARY_OPTION_2" -- TODO: confirm functionality
CLIENT_OPCODES[132] = "ADD_IGNORE" -- TODO: confirm functionality
CLIENT_OPCODES[135] = "USE_ON_NPC" -- TODO: confirm functionality
CLIENT_OPCODES[136] = "INTERACT_WITH_SCENERY" -- done
CLIENT_OPCODES[137] = "CAST_ON_SELF" -- TODO: confirm functionality
CLIENT_OPCODES[142] = "AGREE_TO_TRADE" -- TODO: confirm functionality
CLIENT_OPCODES[153] = "TALK_TO_NPC" -- TODO: confirm functionality
CLIENT_OPCODES[158] = "CAST_ON_GROUND" -- TODO: confirm functionality
CLIENT_OPCODES[161] = "USE_WITH_BOUNDARY" -- TODO: confirm functionality
-- CLIENT_OPCODES[163]  = "KNOWN_PLAYERS" -- This opcode was in RSC in all revisions between MC40 (early 2001) and RSC204 (2006). It was removed in RSC233 (2011). The client used to send this opcode as a reply for every PLAYER_COORDS packet with the client's known-players array!
CLIENT_OPCODES[165] = "FOLLOW_PLAYER" -- done
CLIENT_OPCODES[166] = "CLOSE_SHOP" -- done because it's only the opcode
CLIENT_OPCODES[167] = "REMOVE_FRIEND" -- TODO: confirm functionality
CLIENT_OPCODES[169] = "EQUIP_ITEM" -- TODO: confirm functionality
CLIENT_OPCODES[170] = "UNEQUIP_ITEM" -- TODO: confirm functionality
CLIENT_OPCODES[171] = "ATTACK_PLAYER"  -- done
CLIENT_OPCODES[176] = "ACCEPT_DUEL"  -- done because it's only the opcode
CLIENT_OPCODES[180] = "CAST_ON_BOUNDARY" -- TODO: confirm functionality
CLIENT_OPCODES[187] = "WALK" -- done
CLIENT_OPCODES[190] = "ATTACK_NPC" -- TODO: confirm functionality
CLIENT_OPCODES[195] = "ADD_FRIEND" -- TODO: confirm functionality
CLIENT_OPCODES[197] = "DECLINE_DUEL" -- done because it's only the opcode
CLIENT_OPCODES[202] = "INTERACT_NPC" -- TODO: confirm functionality
CLIENT_OPCODES[206] = "SEND_REPORT" -- done
CLIENT_OPCODES[212] = "BANK_CLOSE" -- done because it's only the opcode
CLIENT_OPCODES[216] = "SEND_CHAT_MESSAGE" -- done
CLIENT_OPCODES[218] = "SEND_PM" -- done
CLIENT_OPCODES[221] = "SELL_TO_SHOP" -- TODO: confirm functionality
CLIENT_OPCODES[229] = "CAST_PVP" -- done
CLIENT_OPCODES[230] = "ABORT_DIALOGUE" -- done because it's only the opcode
CLIENT_OPCODES[235] = "SEND_APPEARANCE"
CLIENT_OPCODES[236] = "BUY_FROM_SHOP" -- TODO: confirm functionality
CLIENT_OPCODES[241] = "REMOVE_IGNORED" -- TODO: confirm functionality
CLIENT_OPCODES[246] = "DROP_ITEM" -- TODO: confirm functionality
CLIENT_OPCODES[247] = "TAKE_GROUND_ITEM" -- done
CLIENT_OPCODES[249] = "CAST_ON_GROUND_ITEM" -- TODO: confirm functionality
CLIENT_OPCODES[254] = "DISABLE_PRAYER" -- done
CLIENT_OPCODES[10000] = "VIRTUAL_OPCODE_LOGIN_REQUEST"

-- Item ID array
ITEM_NAMES = { "Iron Short Sword", "Iron Kite Shield", "Iron Square Shield", "Wooden Shield", "Medium Iron Helmet", "Large Iron Helmet", "Iron Chain Mail Body", "Iron Plate Mail Body", "Iron Plate Mail Legs", "Coins", "Bronze Arrows", "Iron Axe", "Knife", "Logs", "Leather Armour", "Leather Gloves", "Boots", "Cabbage", "Egg", "Bones", "Bucket", "Milk", "Flour", "Amulet of GhostSpeak", "Silverlight key 1", "Silverlight key 2", "skull", "Iron dagger", "grain", "Book", "Fire-Rune", "Water-Rune", "Air-Rune", "Earth-Rune", "Mind-Rune", "Body-Rune", "Life-Rune", "Death-Rune", "Needle", "Nature-Rune", "Chaos-Rune", "Law-Rune", "Thread", "Holy Symbol of saradomin", "Unblessed Holy Symbol", "Cosmic-Rune", "key", "key", "scroll", "Water", "Silverlight key 3", "Silverlight", "Broken shield", "Broken shield", "Cadavaberries", "message", "Cadava", "potion", "Phoenix Crossbow", "Crossbow", "Certificate", "bronze dagger", "Steel dagger", "Mithril dagger", "Adamantite dagger", "Bronze Short Sword", "Steel Short Sword", "Mithril Short Sword", "Adamantite Short Sword", "Bronze Long Sword", "Iron Long Sword", "Steel Long Sword", "Mithril Long Sword", "Adamantite Long Sword", "Rune long sword", "Bronze 2-handed Sword", "Iron 2-handed Sword", "Steel 2-handed Sword", "Mithril 2-handed Sword", "Adamantite 2-handed Sword", "rune 2-handed Sword", "Bronze Scimitar", "Iron Scimitar", "Steel Scimitar", "Mithril Scimitar", "Adamantite Scimitar", "bronze Axe", "Steel Axe", "Iron battle Axe", "Steel battle Axe", "Mithril battle Axe", "Adamantite battle Axe", "Rune battle Axe", "Bronze Mace", "Steel Mace", "Mithril Mace", "Adamantite Mace", "Rune Mace", "Brass key", "staff", "Staff of Air", "Staff of water", "Staff of earth", "Medium Bronze Helmet", "Medium Steel Helmet", "Medium Mithril Helmet", "Medium Adamantite Helmet", "Large Bronze Helmet", "Large Steel Helmet", "Large Mithril Helmet", "Large Adamantite Helmet", "Large Rune Helmet", "Bronze Chain Mail Body", "Steel Chain Mail Body", "Mithril Chain Mail Body", "Adamantite Chain Mail Body", "Bronze Plate Mail Body", "Steel Plate Mail Body", "Mithril Plate Mail Body", "Adamantite Plate Mail Body", "Steel Plate Mail Legs", "Mithril Plate Mail Legs", "Adamantite Plate Mail Legs", "Bronze Square Shield", "Steel Square Shield", "Mithril Square Shield", "Adamantite Square Shield", "Bronze Kite Shield", "Steel Kite Shield", "Mithril Kite Shield", "Adamantite Kite Shield", "cookedmeat", "raw chicken", "burntmeat", "pot", "flour", "bread dough", "bread", "burntbread", "jug", "water", "wine", "grapes", "shears", "wool", "fur", "cow hide", "leather", "clay", "copper ore", "iron ore", "gold", "mithril ore", "adamantite ore", "coal", "Bronze Pickaxe", "uncut diamond", "uncut ruby", "uncut emerald", "uncut sapphire", "diamond", "ruby", "emerald", "sapphire", "Herb", "tinderbox", "chisel", "hammer", "bronze bar", "iron bar", "steel bar", "gold bar", "mithril bar", "adamantite bar", "Pressure gauge", "Fish Food", "Poison", "Poisoned fish food", "spinach roll", "Bad wine", "Ashes", "Apron", "Cape", "Wizards robe", "wizardshat", "Brass necklace", "skirt", "Longbow", "Shortbow", "Crossbow bolts", "Apron", "Chef's hat", "Beer", "skirt", "skirt", "Black Plate Mail Body", "Staff of fire", "Magic Staff", "wizardshat", "silk", "flier", "tin ore", "Mithril Axe", "Adamantite Axe", "bronze battle Axe", "Bronze Plate Mail Legs", "Ball of wool", "Oil can", "Cape", "Kebab", "Spade", "Closet Key", "rubber tube", "Bronze Plated Skirt", "Iron Plated Skirt", "Black robe", "stake", "Garlic", "Red spiders eggs", "Limpwurt root", "Strength Potion", "Strength Potion", "Strength Potion", "Strength Potion", "Steel Plated skirt", "Mithril Plated skirt", "Adamantite Plated skirt", "Cabbage", "Cape", "Large Black Helmet", "Red Bead", "Yellow Bead", "Black Bead", "White Bead", "Amulet of accuracy", "Redberries", "Rope", "Reddye", "Yellowdye", "Paste", "Onion", "Bronze key", "Soft Clay", "wig", "wig", "Half full wine jug", "Keyprint", "Black Plate Mail Legs", "banana", "pastry dough", "Pie dish", "cooking apple", "pie shell", "Uncooked apple pie", "Uncooked meat pie", "Uncooked redberry pie", "apple pie", "Redberry pie", "meat pie", "burntpie", "Half a meat pie", "Half a Redberry pie", "Half an apple pie", "Portrait", "Faladian Knight's sword", "blurite ore", "Asgarnian Ale", "Wizard's Mind Bomb", "Dwarven Stout", "Eye of newt", "Rat's tail", "Bluedye", "Goblin Armour", "Goblin Armour", "Goblin Armour", "unstrung Longbow", "unstrung shortbow", "Unfired Pie dish", "unfired pot", "arrow shafts", "Woad Leaf", "Orangedye", "Gold ring", "Sapphire ring", "Emerald ring", "Ruby ring", "Diamond ring", "Gold necklace", "Sapphire necklace", "Emerald necklace", "Ruby necklace", "Diamond necklace", "ring mould", "Amulet mould", "Necklace mould", "Gold Amulet", "Sapphire Amulet", "Emerald Amulet", "Ruby Amulet", "Diamond Amulet", "Gold Amulet", "Sapphire Amulet", "Emerald Amulet", "Ruby Amulet", "Diamond Amulet", "superchisel", "Mace of Zamorak", "Bronze Plate Mail top", "Steel Plate Mail top", "Mithril Plate Mail top", "Adamantite Plate Mail top", "Iron Plate Mail top", "Black Plate Mail top", "Sapphire Amulet of magic", "Emerald Amulet of protection", "Ruby Amulet of strength", "Diamond Amulet of power", "Karamja Rum", "Cheese", "Tomato", "Pizza Base", "Burnt Pizza", "Incomplete Pizza", "Uncooked Pizza", "Plain Pizza", "Meat Pizza", "Anchovie Pizza", "Half Meat Pizza", "Half Anchovie Pizza", "Cake", "Burnt Cake", "Chocolate Cake", "Partial Cake", "Partial Chocolate Cake", "Slice of Cake", "Chocolate Slice", "Chocolate Bar", "Cake Tin", "Uncooked cake", "Unfired bowl", "Bowl", "Bowl of water", "Incomplete stew", "Incomplete stew", "Uncooked stew", "Stew", "Burnt Stew", "Potato", "Raw Shrimp", "Shrimp", "Raw Anchovies", "Anchovies", "Burnt fish", "Raw Sardine", "Sardine", "Raw Salmon", "Salmon", "Raw Trout", "Trout", "Burnt fish", "Raw Herring", "Herring", "Raw Pike", "Pike", "Burnt fish", "Raw Tuna", "Tuna", "Burnt fish", "Raw Swordfish", "Swordfish", "Burnt Swordfish", "Raw Lobster", "Lobster", "Burnt Lobster", "Lobster Pot", "Net", "Fishing Rod", "Fly Fishing Rod", "Harpoon", "Fishing Bait", "Feather", "Chest key", "Silver", "silver bar", "Holy Symbol of saradomin", "Holy symbol mould", "Disk of Returning", "Monks robe", "Monks robe", "Red key", "Orange Key", "yellow key", "Blue key", "Magenta key", "black key", "rune dagger", "Rune short sword", "rune Scimitar", "Medium Rune Helmet", "Rune Chain Mail Body", "Rune Plate Mail Body", "Rune Plate Mail Legs", "Rune Square Shield", "Rune Kite Shield", "rune Axe", "Rune skirt", "Rune Plate Mail top", "Runite bar", "runite ore", "Plank", "Tile", "skull", "Big Bones", "Muddy key", "Map", "Map Piece", "Map Piece", "Map Piece", "Nails", "Anti dragon breath Shield", "Maze key", "Pumpkin", "Black dagger", "Black Short Sword", "Black Long Sword", "Black 2-handed Sword", "Black Scimitar", "Black Axe", "Black battle Axe", "Black Mace", "Black Chain Mail Body", "Black Square Shield", "Black Kite Shield", "Black Plated skirt", "Herb", "Herb", "Herb", "Herb", "Herb", "Herb", "Herb", "Herb", "Herb", "Guam leaf", "Marrentill", "Tarromin", "Harralander", "Ranarr Weed", "Irit Leaf", "Avantoe", "Kwuarm", "Cadantine", "Dwarf Weed", "Unfinished potion", "Unfinished potion", "Unfinished potion", "Unfinished potion", "Unfinished potion", "Unfinished potion", "Unfinished potion", "Unfinished potion", "Unfinished potion", "Unfinished potion", "Vial", "Vial", "Unicorn horn", "Blue dragon scale", "Pestle and mortar", "Snape grass", "Medium black Helmet", "White berries", "Ground blue dragon scale", "Ground unicorn horn", "attack Potion", "attack Potion", "attack Potion", "stat restoration Potion", "stat restoration Potion", "stat restoration Potion", "defense Potion", "defense Potion", "defense Potion", "restore prayer Potion", "restore prayer Potion", "restore prayer Potion", "Super attack Potion", "Super attack Potion", "Super attack Potion", "fishing Potion", "fishing Potion", "fishing Potion", "Super strength Potion", "Super strength Potion", "Super strength Potion", "Super defense Potion", "Super defense Potion", "Super defense Potion", "ranging Potion", "ranging Potion", "ranging Potion", "wine of Zamorak", "raw bear meat", "raw rat meat", "raw beef", "enchanted bear meat", "enchanted rat meat", "enchanted beef", "enchanted chicken meat", "Dramen Staff", "Dramen Branch", "Cape", "Cape", "Cape", "Cape", "Greendye", "Purpledye", "Iron ore certificate", "Coal certificate", "Mithril ore certificate", "silver certificate", "Gold certificate", "Dragonstone Amulet", "Dragonstone", "Dragonstone Amulet", "Crystal key", "Half of a key", "Half of a key", "Iron bar certificate", "steel bar certificate", "Mithril bar certificate", "silver bar certificate", "Gold bar certificate", "Lobster certificate", "Raw lobster certificate", "Swordfish certificate", "Raw swordfish certificate", "Diary", "Front door key", "Ball", "magnet", "Grey wolf fur", "uncut dragonstone", "Dragonstone ring", "Dragonstone necklace", "Raw Shark", "Shark", "Burnt Shark", "Big Net", "Casket", "Raw cod", "Cod", "Raw Mackerel", "Mackerel", "Raw Bass", "Bass", "Ice Gloves", "Firebird Feather", "Firebird Feather", "Poisoned Iron dagger", "Poisoned bronze dagger", "Poisoned Steel dagger", "Poisoned Mithril dagger", "Poisoned Rune dagger", "Poisoned Adamantite dagger", "Poisoned Black dagger", "Cure poison Potion", "Cure poison Potion", "Cure poison Potion", "Poison antidote", "Poison antidote", "Poison antidote", "weapon poison", "ID Paper", "Poison Bronze Arrows", "Christmas cracker", "Party Hat", "Party Hat", "Party Hat", "Party Hat", "Party Hat", "Party Hat", "Miscellaneous key", "Bunch of keys", "Whisky", "Candlestick", "Master thief armband", "Blamish snail slime", "Blamish oil", "Oily Fishing Rod", "lava eel", "Raw lava eel", "Poison Crossbow bolts", "Dragon sword", "Dragon axe", "Jail keys", "Dusty Key", "Charged Dragonstone Amulet", "Grog", "Candle", "black Candle", "Candle", "black Candle", "insect repellant", "Bat bones", "wax Bucket", "Excalibur", "Druids robe", "Druids robe", "Eye patch", "Unenchanted Dragonstone Amulet", "Unpowered orb", "Fire orb", "Water orb", "Battlestaff", "Battlestaff of fire", "Battlestaff of water", "Battlestaff of air", "Battlestaff of earth", "Blood-Rune", "Beer glass", "glassblowing pipe", "seaweed", "molten glass", "soda ash", "sand", "air orb", "earth orb", "bass certificate", "Raw bass certificate", "shark certificate", "Raw shark certificate", "Oak Logs", "Willow Logs", "Maple Logs", "Yew Logs", "Magic Logs", "Headless Arrows", "Iron Arrows", "Poison Iron Arrows", "Steel Arrows", "Poison Steel Arrows", "Mithril Arrows", "Poison Mithril Arrows", "Adamantite Arrows", "Poison Adamantite Arrows", "Rune Arrows", "Poison Rune Arrows", "Oak Longbow", "Oak Shortbow", "Willow Longbow", "Willow Shortbow", "Maple Longbow", "Maple Shortbow", "Yew Longbow", "Yew Shortbow", "Magic Longbow", "Magic Shortbow", "unstrung Oak Longbow", "unstrung Oak Shortbow", "unstrung Willow Longbow", "unstrung Willow Shortbow", "unstrung Maple Longbow", "unstrung Maple Shortbow", "unstrung Yew Longbow", "unstrung Yew Shortbow", "unstrung Magic Longbow", "unstrung Magic Shortbow", "barcrawl card", "bronze arrow heads", "iron arrow heads", "steel arrow heads", "mithril arrow heads", "adamantite arrow heads", "rune arrow heads", "flax", "bow string", "Easter egg", "scorpion cage", "scorpion cage", "scorpion cage", "scorpion cage", "Enchanted Battlestaff of fire", "Enchanted Battlestaff of water", "Enchanted Battlestaff of air", "Enchanted Battlestaff of earth", "scorpion cage", "scorpion cage", "scorpion cage", "scorpion cage", "gold", "gold bar", "Ruby ring", "Ruby necklace", "Family crest", "Crest fragment", "Crest fragment", "Crest fragment", "Steel gauntlets", "gauntlets of goldsmithing", "gauntlets of cooking", "gauntlets of chaos", "robe of Zamorak", "robe of Zamorak", "Address Label", "Tribal totem", "tourist guide", "spice", "Uncooked curry", "curry", "Burnt curry", "yew logs certificate", "maple logs certificate", "willow logs certificate", "lockpick", "Red vine worms", "Blanket", "Raw giant carp", "giant Carp", "Fishing competition Pass", "Hemenster fishing trophy", "Pendant of Lucien", "Boots of lightfootedness", "Ice Arrows", "Lever", "Staff of Armadyl", "Pendant of Armadyl", "Large cog", "Large cog", "Large cog", "Large cog", "Rat Poison", "shiny Key", "khazard Helmet", "khazard chainmail", "khali brew", "khazard cell keys", "Poison chalice", "magic whistle", "Cup of tea", "orb of protection", "orbs of protection", "Holy table napkin", "bell", "Gnome Emerald Amulet of protection", "magic golden feather", "Holy grail", "Script of Hazeel", "Pineapple", "Pineapple ring", "Pineapple Pizza", "Half pineapple Pizza", "Magic scroll", "Mark of Hazeel", "bloody axe of zamorak", "carnillean armour", "Carnillean Key", "Cattle prod", "Plagued sheep remains", "Poisoned animal feed", "Protective jacket", "Protective trousers", "Plagued sheep remains", "Plagued sheep remains", "Plagued sheep remains", "dwellberries", "Gasmask", "picture", "Book", "Seaslug", "chocolaty milk", "Hangover cure", "Chocolate dust", "Torch", "Torch", "warrant", "Damp sticks", "Dry sticks", "Broken glass", "oyster pearls", "little key", "Scruffy note", "Glarial's amulet", "Swamp tar", "Uncooked Swamp paste", "Swamp paste", "Oyster pearl bolts", "Glarials pebble", "book on baxtorian", "large key", "Oyster pearl bolt tips", "oyster", "oyster pearls", "oyster", "Soil", "Dragon medium Helmet", "Mithril seed", "An old key", "pigeon cage", "Messenger pigeons", "Bird feed", "Rotten apples", "Doctors gown", "Bronze key", "Distillator", "Glarial's urn", "Glarial's urn", "Priest robe", "Priest gown", "Liquid Honey", "Ethenea", "Sulphuric Broline", "Plague sample", "Touch paper", "Dragon Bones", "Herb", "Snake Weed", "Herb", "Ardrigal", "Herb", "Sito Foil", "Herb", "Volencia Moss", "Herb", "Rogues Purse", "Soul-Rune", "king lathas Amulet", "Bronze Spear", "halloween mask", "Dragon bitter", "Greenmans ale", "halloween mask", "halloween mask", "cocktail glass", "cocktail shaker", "Bone Key", "gnome robe", "gnome robe", "gnome robe", "gnome robe", "gnome robe", "gnomeshat", "gnomeshat", "gnomeshat", "gnomeshat", "gnomeshat", "gnome top", "gnome top", "gnome top", "gnome top", "gnome top", "gnome cocktail guide", "Beads of the dead", "cocktail glass", "cocktail glass", "lemon", "lemon slices", "orange", "orange slices", "Diced orange", "Diced lemon", "Fresh Pineapple", "Pineapple chunks", "lime", "lime chunks", "lime slices", "fruit blast", "odd looking cocktail", "Whisky", "vodka", "gin", "cream", "Drunk dragon", "Equa leaves", "SGG", "Chocolate saturday", "brandy", "blurberry special", "wizard blizzard", "pineapple punch", "gnomebatta dough", "gianne dough", "gnomebowl dough", "gnomecrunchie dough", "gnomebatta", "gnomebowl", "gnomebatta", "gnomecrunchie", "gnomebowl", "Uncut Red Topaz", "Uncut Jade", "Uncut Opal", "Red Topaz", "Jade", "Opal", "Swamp Toad", "Toad legs", "King worm", "Gnome spice", "gianne cook book", "gnomecrunchie", "cheese and tomato batta", "toad batta", "gnome batta", "worm batta", "fruit batta", "Veg batta", "Chocolate bomb", "Vegball", "worm hole", "Tangled toads legs", "Choc crunchies", "Worm crunchies", "Toad crunchies", "Spice crunchies", "Crushed Gemstone", "Blurberry badge", "Gianne badge", "tree gnome translation", "Bark sample", "War ship", "gloughs journal", "invoice", "Ugthanki Kebab", "special curry", "glough's key", "glough's notes", "Pebble", "Pebble", "Pebble", "Pebble", "Daconia rock", "Sinister key", "Herb", "Torstol", "Unfinished potion", "Jangerberries", "fruit blast", "blurberry special", "wizard blizzard", "pineapple punch", "SGG", "Chocolate saturday", "Drunk dragon", "cheese and tomato batta", "toad batta", "gnome batta", "worm batta", "fruit batta", "Veg batta", "Chocolate bomb", "Vegball", "worm hole", "Tangled toads legs", "Choc crunchies", "Worm crunchies", "Toad crunchies", "Spice crunchies", "Stone-Plaque", "Tattered Scroll", "Crumpled Scroll", "Bervirius Tomb Notes", "Zadimus Corpse", "Potion of Zamorak", "Potion of Zamorak", "Potion of Zamorak", "Boots", "Boots", "Boots", "Boots", "Boots", "Santa's hat", "Locating Crystal", "Sword Pommel", "Bone Shard", "Steel Wire", "Bone Beads", "Rashiliya Corpse", "ResetCrystal", "Bronze Wire", "Present", "Gnome Ball", "Papyrus", "A lump of Charcoal", "Arrow", "Lit Arrow", "Rocks", "Paramaya Rest Ticket", "Ship Ticket", "Damp cloth", "Desert Boots", "Orb of light", "Orb of light", "Orb of light", "Orb of light", "Railing", "Randas's journal", "Unicorn horn", "Coat of Arms", "Coat of Arms", "Staff of Iban", "Dwarf brew", "Ibans Ashes", "Cat", "A Doll of Iban", "Old Journal", "Klank's gauntlets", "Iban's shadow", "Iban's conscience", "Amulet of Othainian", "Amulet of Doomion", "Amulet of Holthion", "keep key", "Bronze Throwing Dart", "Prototype Throwing Dart", "Iron Throwing Dart", "Full Water Skin", "Lens mould", "Lens", "Desert Robe", "Desert Shirt", "Metal Key", "Slaves Robe Bottom", "Slaves Robe Top", "Steel Throwing Dart", "Astrology Book", "Unholy Symbol mould", "Unholy Symbol of Zamorak", "Unblessed Unholy Symbol of Zamorak", "Unholy Symbol of Zamorak", "Shantay Desert Pass", "Staff of Iban", "Dwarf cannon base", "Dwarf cannon stand", "Dwarf cannon barrels", "Dwarf cannon furnace", "Fingernails", "Powering crystal1", "Mining Barrel", "Ana in a Barrel", "Stolen gold", "multi cannon ball", "Railing", "Ogre tooth", "Ogre relic", "Skavid map", "dwarf remains", "Key", "Ogre relic part", "Ogre relic part", "Ogre relic part", "Ground bat bones", "Unfinished potion", "Ogre potion", "Magic ogre potion", "Tool kit", "Nulodion's notes", "cannon ammo mould", "Tenti Pineapple", "Bedobin Copy Key", "Technical Plans", "Rock cake", "Bronze dart tips", "Iron dart tips", "Steel dart tips", "Mithril dart tips", "Adamantite dart tips", "Rune dart tips", "Mithril Throwing Dart", "Adamantite Throwing Dart", "Rune Throwing Dart", "Prototype dart tip", "info document", "Instruction manual", "Unfinished potion", "Iron throwing knife", "Bronze throwing knife", "Steel throwing knife", "Mithril throwing knife", "Adamantite throwing knife", "Rune throwing knife", "Black throwing knife", "Water Skin mostly full", "Water Skin mostly empty", "Water Skin mouthful left", "Empty Water Skin", "nightshade", "Shaman robe", "Iron Spear", "Steel Spear", "Mithril Spear", "Adamantite Spear", "Rune Spear", "Cat", "Seasoned Sardine", "Kittens", "Kitten", "Wrought iron key", "Cell Door Key", "A free Shantay Disclaimer", "Doogle leaves", "Raw Ugthanki Meat", "Tasty Ugthanki Kebab", "Cooked Ugthanki Meat", "Uncooked Pitta Bread", "Pitta Bread", "Tomato Mixture", "Onion Mixture", "Onion and Tomato Mixture", "Onion and Tomato and Ugthanki Mix", "Burnt Pitta Bread", "Panning tray", "Panning tray", "Panning tray", "Rock pick", "Specimen brush", "Specimen jar", "Rock Sample", "gold Nuggets", "cat", "Scrumpled piece of paper", "Digsite info", "Poisoned Bronze Throwing Dart", "Poisoned Iron Throwing Dart", "Poisoned Steel Throwing Dart", "Poisoned Mithril Throwing Dart", "Poisoned Adamantite Throwing Dart", "Poisoned Rune Throwing Dart", "Poisoned Bronze throwing knife", "Poisoned Iron throwing knife", "Poisoned Steel throwing knife", "Poisoned Mithril throwing knife", "Poisoned Black throwing knife", "Poisoned Adamantite throwing knife", "Poisoned Rune throwing knife", "Poisoned Bronze Spear", "Poisoned Iron Spear", "Poisoned Steel Spear", "Poisoned Mithril Spear", "Poisoned Adamantite Spear", "Poisoned Rune Spear", "Book of experimental chemistry", "Level 1 Certificate", "Level 2 Certificate", "Level 3 Certificate", "Trowel", "Stamped letter of recommendation", "Unstamped letter of recommendation", "Rock Sample", "Rock Sample", "Cracked rock Sample", "Belt buckle", "Powering crystal2", "Powering crystal3", "Powering crystal4", "Old boot", "Bunny ears", "Damaged armour", "Damaged armour", "Rusty sword", "Ammonium Nitrate", "Nitroglycerin", "Old tooth", "Radimus Scrolls", "chest key", "broken arrow", "buttons", "broken staff", "vase", "ceramic remains", "Broken glass", "Unidentified powder", "Machette", "Scroll", "stone tablet", "Talisman of Zaros", "Explosive compound", "Bull Roarer", "Mixed chemicals", "Ground charcoal", "Mixed chemicals", "Spell scroll", "Yommi tree seed", "Totem Pole", "Dwarf cannon base", "Dwarf cannon stand", "Dwarf cannon barrels", "Dwarf cannon furnace", "Golden Bowl", "Golden Bowl with pure water", "Raw Manta ray", "Manta ray", "Raw Sea turtle", "Sea turtle", "Annas Silver Necklace", "Bobs Silver Teacup", "Carols Silver Bottle", "Davids Silver Book", "Elizabeths Silver Needle", "Franks Silver Pot", "Thread", "Thread", "Thread", "Flypaper", "Murder Scene Pot", "A Silver Dagger", "Murderers fingerprint", "Annas fingerprint", "Bobs fingerprint", "Carols fingerprint", "Davids fingerprint", "Elizabeths fingerprint", "Franks fingerprint", "Zamorak Cape", "Saradomin Cape", "Guthix Cape", "Staff of zamorak", "Staff of guthix", "Staff of Saradomin", "A chunk of crystal", "A lump of crystal", "A hunk of crystal", "A red crystal", "Unidentified fingerprint", "Annas Silver Necklace", "Bobs Silver Teacup", "Carols Silver Bottle", "Davids Silver Book", "Elizabeths Silver Needle", "Franks Silver Pot", "A Silver Dagger", "A glowing red crystal", "Unidentified liquid", "Radimus Scrolls", "Robe", "Armour", "Dagger", "eye patch", "Booking of Binding", "Holy Water Vial", "Enchanted Vial", "Scribbled notes", "Scrawled notes", "Scatched notes", "Shamans Tome", "Edible seaweed", "Rough Sketch of a bowl", "Burnt Manta ray", "Burnt Sea turtle", "Cut reed plant", "Magical Fire Pass", "Snakes Weed Solution", "Ardrigal Solution", "Gujuo Potion", "Germinated Yommi tree seed", "Dark Dagger", "Glowing Dark Dagger", "Holy Force Spell", "Iron Pickaxe", "Steel Pickaxe", "Mithril Pickaxe", "Adamantite Pickaxe", "Rune Pickaxe", "Sleeping Bag", "A blue wizards hat", "Gilded Totem Pole", "Blessed Golden Bowl", "Blessed Golden Bowl with Pure Water", "Raw Oomlie Meat", "Cooked Oomlie meat Parcel", "Dragon Bone Certificate", "Limpwurt Root Certificate", "Prayer Potion Certificate", "Super Attack Potion Certificate", "Super Defense Potion Certificate", "Super Strength Potion Certificate", "Half Dragon Square Shield", "Half Dragon Square Shield", "Dragon Square Shield", "Palm tree leaf", "Raw Oomlie Meat Parcel", "Burnt Oomlie Meat parcel", "Bailing Bucket", "Plank", "Arcenia root", "display tea", "Blessed Golden Bowl with plain water", "Golden Bowl with plain water", "Cape of legends", "Scythe" }
ITEM_NAMES[0] = "Iron Mace"
ITEM_BASE_PRICE = { 91,238,168,20,84,154,210,560,280,1,2,56,6,4,21,6,6,1,4,1,2,6,2,35,1,1,1,35,2,1,4,4,4,4,3,3,1,20,1,7,10,12,1,200,200,15,1,1,5,6,1,50,1,1,1,1,1,1,4,70,1,10,125,325,800,26,325,845,2080,40,140,500,1300,3200,32000,80,280,1000,2600,6400,64000,32,112,400,1040,2560,16,200,182,650,1690,4160,41600,18,225,585,1440,14400,1,15,1500,1500,1500,24,300,780,1920,44,550,1430,3520,35200,60,750,1950,4800,160,2000,5200,12800,1000,2600,6400,48,600,1560,3840,68,850,2210,5440,4,1,1,1,10,1,12,1,1,1,1,1,1,1,10,1,1,1,3,17,150,162,400,45,1,200,100,50,25,2000,1000,500,250,1,1,1,1,8,28,100,300,300,640,1,1,1,1,1,1,2,2,2,15,2,30,2,80,50,3,2,2,2,2,2,3840,1500,200,2,30,1,3,520,1280,52,80,2,3,7,3,3,1,3,80,280,13,8,3,7,7,14,13,13,11,1000,2600,6400,1,32,1056,4,4,4,4,100,3,18,5,5,5,3,1,2,2,2,1,2,1920,2,1,3,1,1,1,1,1,30,12,15,1,10,4,5,3,200,3,2,2,2,3,3,5,40,40,40,60,23,3,1,1,1,5,350,900,1275,2025,3525,450,1050,1425,2175,3675,5,5,5,350,900,1275,2025,3525,350,900,1275,2025,3525,3525,4500,160,2000,5200,12800,560,3840,900,1275,2025,3525,30,4,4,4,1,10,25,40,50,60,25,30,50,1,70,30,50,10,30,10,10,20,2,4,3,4,4,10,20,1,1,5,5,15,15,1,10,10,50,50,20,20,1,15,15,25,25,1,100,100,1,200,200,1,150,150,1,20,5,5,5,5,3,2,1,75,150,300,5,12,40,30,1,1,1,1,1,1,8000,20800,25600,19200,50000,65000,64000,38400,54400,12800,64000,65000,5000,3200,1,1,1,1,1,1,1,1,1,3,20,1,30,240,624,960,1920,768,384,1248,432,1440,1152,1632,1920,1,1,1,1,1,1,1,1,1,3,5,11,20,25,40,48,54,65,70,3,5,11,20,25,40,48,54,65,70,2,2,20,50,4,10,576,10,40,20,12,9,6,88,66,44,120,90,60,152,114,76,180,135,90,200,150,100,220,165,110,264,198,132,288,216,144,1,1,1,1,1,1,1,1,15,15,32,32,32,32,5,5,10,20,30,15,25,17625,10000,17625,1,1,1,10,20,30,15,25,10,10,10,10,1,1,1,3,50,1000,17625,18375,300,300,1,20,50,25,25,17,17,120,120,6,2,2,35,10,125,325,8000,800,240,288,216,144,288,216,144,144,1,2,1,2,2,2,2,2,2,1,2,5,5,2,5,10,15,150,150,3,100000,200000,2,1,17625,3,3,3,3,3,3,1,2,200,40,30,2,17625,100,300,300,7000,15500,15500,15500,15500,25,2,2,2,2,2,2,300,300,10,10,10,10,20,40,80,160,320,1,6,6,24,24,64,64,160,160,800,800,160,100,320,200,640,400,1280,800,2560,1600,80,50,160,100,320,200,640,400,1280,800,10,1,3,12,32,80,400,5,10,10,10,10,10,10,42500,42500,42500,42500,10,10,10,10,150,300,2025,2175,10,10,10,10,6,6,6,6,40,30,10,10,1,230,10,20,1,10,20,30,20,3,5,50,50,10,20,12,6,2,20,15,12,10,10,10,10,1,1,10,10,5,1,20,10,10,1,1,10,1,0,2,1,1,1,1,100,50,1,0,5000,65,1,15,0,0,50,50,0,0,0,4,2,2,1,4,2,2,2,4,4,5,0,0,0,1400,1,2,1,1,1,30,110,1,2,1,56,5,112,200,2,100000,200,1,1,1,1,1,40,1,1,1,1,5,5,0,10,1,1,1,1,1,5,1,5,1,5,1,5,1,5,2500,10,4,15,2,2,15,15,0,2,1,180,180,180,180,180,160,160,160,160,160,180,180,180,180,180,2,35,2,2,2,2,2,2,2,2,1,1,2,1,2,2,2,5,5,5,2,2,2,2,2,5,2,2,2,2,2,2,2,2,2,2,2,2,40,30,20,200,150,100,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,20,20,1,2,2,2,2,2,40,1,1,25,25,1,30,30,30,30,30,30,30,120,120,120,120,120,120,160,150,150,160,85,85,85,85,5,5,5,5,1,25,25,25,200,200,200,200,200,160,100,100,1,200,1,1,100,20,160,10,9,45,10,10,10,5,5,10,20,10,10,10,10,10,1,20,10,10,15,2,2,2,2,1,6,2,2,0,0,0,1,2,70,5,30,10,10,40,40,1,40,40,20,2,200,200,200,200,5,15,200000,200000,200000,200000,0,0,100,100,300,10,10,0,0,0,1,1,0,0,0,20,3,120,120,120,1,5,1,20,500,0,1,3,9,25,65,350,50,130,700,1,2,1,3,6,2,21,54,133,333,37,27,24,18,15,30,40,13,46,119,293,1000,2,10,2,2,1,1,1,2,2,20,5,4,10,3,3,3,3,1,1,1,1,1,1,1,1,1,1,10,63,2,5,20,50,130,700,2,6,21,54,37,133,333,4,13,46,119,293,1000,1,1,1,1,1,1,5,1,1,1,1,0,0,0,1,1,1,1,1,20,2,0,5,1,1,1,1,1,1,0,20,40,5,1,1,2,1,2,20,2,5,200,500,200000,200000,200000,150000,1000,1000,500,500,500,500,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,100,100,100,80000,80000,80000,2000,2000,2000,2000,1,1,1,1,1,1,1,1,2000,2,5,15,40,35,2,1,3,200,20,20,20,1,2,5,500,500,2,1,1,1,1,200,91,91,1,140,500,1300,3200,32000,30,2,20,1000,1000,10,35,10,10,10,10,10,10,500000,110000,500000,5,16,1,10,1,7,10,1000,1000,450,15 }
ITEM_BASE_PRICE[0] = 63
ITEM_WEILDABLE = { true,true,true,true,true,true,true,true,true,false,false,true,false,false,true,true,true,false,false,false,false,false,false,true,false,false,false,true,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true,false,false,false,false,false,false,true,false,false,false,false,false,false,true,true,false,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,false,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true,true,true,true,true,true,true,true,false,true,true,false,true,true,true,true,true,true,false,false,false,true,true,true,true,false,false,true,false,false,false,false,true,true,true,true,false,false,false,false,false,false,false,true,true,true,false,true,true,false,false,false,false,true,false,false,false,false,false,false,false,false,false,false,false,false,true,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true,true,true,true,true,false,false,false,false,false,false,false,false,true,true,true,true,true,false,true,true,true,true,true,true,true,true,true,true,true,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true,false,false,true,true,false,false,false,false,false,false,true,true,true,true,true,true,true,true,true,true,true,true,false,false,false,false,false,false,false,false,false,false,false,false,true,false,false,true,true,true,true,true,true,true,true,true,true,true,true,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true,false,true,true,true,true,false,false,false,false,false,false,false,true,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true,false,false,false,false,false,false,false,false,false,false,false,true,false,false,true,true,true,true,true,true,true,false,false,false,false,false,false,false,false,false,false,true,true,true,true,true,true,false,false,false,false,false,false,false,false,false,false,false,true,true,false,false,true,false,false,false,false,false,false,false,false,true,true,true,true,true,false,false,false,true,true,true,true,true,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true,true,true,true,true,true,true,true,true,true,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true,true,true,true,false,false,false,false,false,false,false,true,false,false,false,false,true,true,true,true,true,true,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true,true,false,false,true,true,false,false,false,false,false,false,true,true,false,false,false,false,false,false,false,false,false,true,false,false,false,false,false,false,false,false,false,true,false,false,false,false,false,true,true,false,false,false,false,true,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true,false,false,false,false,false,false,false,false,false,false,false,false,true,false,false,false,false,false,false,true,false,false,false,false,true,true,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true,true,true,false,false,true,true,false,false,false,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,false,true,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true,true,true,true,true,true,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true,false,false,false,false,false,false,false,false,false,true,false,false,false,false,false,true,false,false,true,true,true,false,true,false,true,false,false,false,true,true,false,true,true,true,false,false,false,true,true,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true,true,true,false,false,false,false,true,true,true,true,true,true,true,false,false,false,false,false,false,true,true,true,true,true,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true,false,false,false,false,false,false,false,false,false,false,true,false,false,false,false,false,false,false,true,true,true,true,true,true,false,false,false,false,false,true,false,false,false,false,false,true,false,false,false,true,false,true,true,false,true,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true,true,false,false,false,false,false,false,false,true,false,false,false,false,false,false,false,false,false,false,false,false,false,true,false,false,false,false,false,false,false,false,false,true,true }
ITEM_WEILDABLE[0] = true
ITEM_STACKABLE = { false,false,false,false,false,false,false,false,false,true,true,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true,true,true,true,true,true,true,true,true,true,true,true,true,false,false,true,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true,true,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true,true,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true,true,true,true,true,false,false,false,false,false,false,true,true,true,true,true,true,true,true,true,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true,false,false,false,false,false,false,false,false,true,true,true,true,false,false,false,false,false,true,true,true,true,true,true,true,true,true,true,true,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true,true,true,true,true,true,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true,true,true,false,true,false,false,false,false,false,false,false,true,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true,true,true,true,false,false,false,true,false,false,false,false,false,true,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true,true,true,false,false,false,false,false,false,false,false,true,false,false,false,false,false,true,false,false,false,false,false,false,false,false,false,false,true,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true,true,true,true,true,true,true,true,true,true,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true,false,false,false,true,true,true,true,true,true,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true,true,true,true,true,true,false,false,false,false,false,false,false,false,false,false,false,false,false,false }
ITEM_STACKABLE[0] = false

-- Scenery Names
SCENERY_NAMES =  { "Tree","Well","Table","Treestump","Ladder","Ladder","Chair","logs","Longtable","Throne","Range","Gravestone","Gravestone","Bed","Bed","bar","Chest","Chest","Altar","Post","Support","barrel","Bench","Portrait","candles","fountain","landscape","Millstones","Counter","Stall","Target","PalmTree","PalmTree","Fern","Cactus","Bullrushes","Flower","Mushroom","Coffin","Coffin","stairs","stairs","stairs","stairs","railing","pillar","Bookcase","Sink","Dummy","anvil","Torch","hopper","chute","cart","sacks","cupboard","Gate","gate","gate","gate","signpost","signpost","doors","doors","signpost","signpost","bookcase","henge","Dolmen","Tree","cupboard","Wheat","sign","sails","sign","sign","Drain","manhole","manhole","pipe","Chest","Chest","barrel","cupboard","cupboard","fountain","signpost","Tree","sign","sign","sign","sign","gate","gate","sign","sign","fire","Rock","Rock","Rock","Rock","Rock","Rock","Rock","Rock","Rock","Rock","Rock","Rock","Rock","Rock","Rock","Rock","Rock","Rock","web","web","furnace","Cook's Range","Machine","Spinning wheel","Lever","Lever","LeverA","LeverB","LeverC","LeverD","LeverE","LeverF","Ladder","signpost","signpost","signpost","Compost Heap","Coffin","Coffin","gate","gate","sign","cupboard","cupboard","doors","torch","Altar","Shield","Grill","Cauldron","Grill","Mine Cart","Buffers","Track","Track","Track","Hole","ship","ship","ship","Emergency escape ladder","sign","sign","ship","ship","ship","Rocks","Rocks","Rocks","Rocks","Rocks","Rocks","Rocks","Rocks","Rocks","hopper","cupboard","cupboard","Rocks","Doric's anvil","pottery oven","potter's wheel","gate","gate","crate","Banana tree","Banana tree","crate","Chest","Chest","Flower","sign","sign","Potato","fish","fish","fish","Rock","Rock","Rocks","Ladder","Ladder","Monks Altar","Ladder","Coffin","Coffin","Smashed table","Fungus","Smashed chair","Broken pillar","Fallen tree","Danger Sign","Rock","Rock","Rocks","Gravestone","bone","bone","carcass","animalskull","Vine","Vine","Vine","Chest","Chest","Ladder","ship","ship","hole","Ladder","Chest","Chest","Chest","Chest","hole","ship","ship","Altar of Guthix","The Cauldron of Thunder","Tree","ship","ship","ship","ship","ship","ship","Ladder","Dramen Tree","hopper","Chest","Chest","Ladder","sign","sign","gate","gate","gate","Door mat","gate","Cauldron","cupboard","cupboard","gate","fish","sign","cupboard","cupboard","Chest","Chest","sign","signpost","Rockslide","Ladder","fish","barrel","table","Fireplace","Egg","Eggs","Stalagmites","Stool","Bench","table","table","fountain of heros","bush","hedge","flower","plant","Giant crystal","sign","sign","crate","crate","ship","ship","beehive","Ladder","Altar","sign","signpost","Archway","Obelisk of water","Obelisk of fire","sand pit","Obelisk of air","Obelisk of earth","gate","Oak Tree","Willow Tree","Maple Tree","Yew Tree","Tree","gate","sign","flax","Large treestump","Rocks","Lever","Lever","Lever","gate","ship","ship","Bakers Stall","Silk Stall","Fur Stall","Silver Stall","Spices Stall","gems Stall","crate","crate","sign","stairs","Chest","Chest","Chest","Chest","Chest","Chest","Chest","Chest","Chest","empty stall","stairs","hopper","signpost","sign","gate","gate","Lever","Lever","pipe","fish","fish","fish","fish","Vine","gate","gate","gate","stairs","broken cart","Lever","clock pole blue","clock pole red","clock pole purple","clock pole black","wallclockface","Lever Bracket","Lever","stairs","stairs","gate","gate","Lever","Lever","Foodtrough","fish","spearwall","hornedskull","Chest","Chest","guardscupboard","guardscupboard","Coal truck","ship","ship","ship","Tree","Ballista","largespear","spirit tree","young spirit Tree","gate","wall","tree","tree","Fern","Fern","Fern","Fern","fly trap","Fern","Fern","plant","plant","plant","stone head","dead Tree","sacks","khazard open Chest","khazard shut Chest","doorframe","Sewer valve","Sewer valve 2","Sewer valve 3","Sewer valve 4","Sewer valve 5","Cave entrance","Log bridge","Log bridge","tree platform","tree platform","gate","tree platform","tree platform","Log bridge","Log bridge","tree platform","tree platform","Tribal brew","Pineapple tree","Pineapple tree","log raft","log raft","Tomb of hazeel","range","Bookcase","Carnillean Chest","Carnillean Chest","crate","Butlers cupboard","Butlers cupboard","gate","gate","Cattle furnace","Ardounge wall","Ardounge wall corner","Dug up soil","Pile of mud","large Sewer pipe","Ardounge wall gateway","cupboard","cupboard","Fishing crane","Rowboat","Damaged Rowboat","barrel","gate","Ladder","Fishing crane","Fishing crane","Waterfall","leaflessTree","leaflessTree","log raft","doors","Well","Tomb of glarial","Waterfall","Waterfall","Bookcase","doors","doors","Stone stand","Stone stand","Stone stand","Stone stand","Stone stand","Stone stand","Glarial's Gravestone","gate","crate","leaflessTree","Statue of glarial","Chalice of eternity","Chalice of eternity","doors","Lever","Lever","log raft remains","Tree"," Range","crate","fish","Watch tower","signpost","Rocks","doors","Rope ladder","cupboard","cupboard","Rope ladder","Cooking pot","Gallow","gate","crate","cupboard","cupboard","gate","cupboard","cupboard","sign","grand tree","gate","gate","Rocks","Rocks","Rocks","Rocks","Rocks","Rocks","Rocks","Rocks","Rocks","Log bridge","Watch tower","Log bridge","climbing rocks","Ledge","Ledge","log","log","Rocks","Rocks","Rocks","Rocks","Rocks","Rocks","Rocks","Rocks","Rocks","Rocks","Rocks","Rocks","Rocks","Rocks","Rocks","Rocks","Rocks","Rocks","Rocks","Rocks","Rocks","PalmTree","Scorched Earth","Rocks","sign","fish","Rocky Walkway","Rocky Walkway","Rocky Walkway","Rocky Walkway","fight Dummy","gate","Jungle Vine","statue","sign","grand tree","grand tree","grand tree","grand tree","grand tree","Hillside Entrance","tree","Log bridge","Tree platform","Tree platform","Metalic Dungeon Gate","Log bridge","Log bridge","Watch tower","Watch tower","Shallow water","Doors","grand tree","Tree Ladder","Tree Ladder","blurberrys cocktail bar","Gem Rocks","Giannes place","ropeswing","net","Frame","Tree","Tree","Tree","cart","fence","beam","Sign","Sign","Sign","Sign","Sign","Sign","Sign","Sign","Sign","Sign","Root","Root","Metal Gate","Metal Gate","A farm cart","Ledge","Ledge","Ladder","cage","glider","cupboard","cupboard","stairs","glider","gate","gate","chaos altar","Gnome stronghold gate","ropeswing","ropeswing","stairs","stairs","Chest","Chest","Pile of rubble","Stone stand","Watch tower","Pile of rubble","Root","Root","Root","Sign","Hammock","Goal","stone tile","Chest","Chest","Watch tower","net","Watch tower","Watch tower","ropeswing","Bumpy Dirt","pipe","net","pipe","log","pipe","pipe","Handholds","Ladder","gate","stronghold spirit Tree","Tree","Tree","Tree","Spiked pit","Spiked pit","Cave","stone pebble","Pile of rubble","Pile of rubble","pipe","pipe","Stone","Stone","ropeswing","log","net","Ledge","Handholds","log","log","Rotten Gallows","Pile of rubble","ropeswing","ropeswing","ocks","Tree","Well stacked rocks","Tomb Dolmen","Handholds","Bridge Blockade","Log Bridge","Handholds","Tree","Tree","Wet rocks","Smashed table","Crude Raft","Daconia rock","statue","Stepping stones","gate","gate","gate","pipe","ropeswing","Stone","Ledge","Vine","Rocks","Wooden Gate","Wooden Gate","Stone bridge","Stone bridge","Stone bridge","Stone bridge","Stone platform","fence","Rocks","Stone bridge","Stone bridge","Gate of Iban","Wooden Door","Tomb Dolmen","Cave entrance","Old bridge","Old bridge","Crumbled rock","stalagmite","stalagmite","Rocks","Ledge","Lever","stalactite","stalactite","stalactite","Rocks","Rocks","Rocks","Rocks","Rocks","Rocks","Rocks","Rocks","Rocks","Rocks","Rocks","Rocks","Rocks","Ledge","Ledge","Ledge","Ledge","Swamp","Swamp","Rocks","Rocks","Rocks","Rocks","Rocks","Rocks","Rocks","Rocks","Rocks","Rocks","Rocks","Pile of mud","Travel Cart","Travel Cart","Rocks","stalactite","Rocks","Rocks","Rocks","sign","Ladder","Grill","Spiked pit","signpost","Ship","Ship","Grill","sacks","Zamorakian Temple","Grill","Grill","Grill","Grill","Grill","Grill","Grill","Rocks","Rocks","Tomb Doors","Swamp","Rocks","Rocks","stalactite","stalactite","Spiked pit","Lever","Cage","Cage","Rocks","Spear trap","Rocks","Rocks","Rocks","Rocks","Rocks","Rocks","Ledge","Furnace","Well","Passage","Passage","Passage","stalagmite","Rocks","Rocks","Rocks","Rocks","Rocks","Rocks","Passage","snap trap","Wooden planks","Passage","Passage","Flames of zamorak","Platform","Rock","Rock","Rock","Rock","wall grill","Ledge","wall grill","Dug up soil","Dug up soil","Pile of mud","stalagmite","Pile of mud","Pile of mud","Pile of mud","Pile of mud","Pile of mud","Spiked pit","Rocks","Rocks","Rocks","Rocks","Rocks","Rocks","Rocks","Rocks","Rocks","Rocks","Rocks","Rocks","Ledge","Ledge","Ledge","Ledge","Ledge","Ledge","Boulder","crate","Door","Platform","Cage remains","Ledge","Passage","Passage","Gate of Zamorak","Rocks","Bridge support","Tomb of Iban","Claws of Iban","barrel","Rock","Rocks","Rocks","Swamp","Chest","Stone bridge","cage","cage","Stone steps","Pile of mud","Stone bridge","Stone bridge","Stone bridge","Stone bridge","Stone bridge","Stone bridge","Stone bridge","Stone bridge","Stone bridge","Stone bridge","Stone bridge","Stone bridge","Stone bridge","Stone bridge","Stone bridge","Stone bridge","Stone bridge","Stone bridge","Stone bridge","Stone bridge","Chest","Chest","Pit of the Damned","Open Door","signpost","Stone Gate","Chest","Zodiac","Chest","Ladder","Stone steps","Rock","Rock","Rock","Telescope","Gate","sacks","Ladder","Chest","Chest","Bookcase","Iron Gate","Ladder","Chest","Chest","Chest","Chest","Rockslide","Altar","column","Grave of Scorpius","Bank Chest","dwarf multicannon","Disturbed sand","Disturbed sand","dwarf multicannon base","dwarf multicannon stand","dwarf multicannon barrels","Cave","Cave","fence","signpost","Rocks","Rocks","Cave entrance","Chest","Chest","Wooden Doors","Pedestal","bush","bush","Standard","Mining Cave","Mining Cave","Rocks","Lift","Mining Barrel","Hole","Hole","Cave","Cave","Cave","Counter","Track","Cave","Mine Cart","Lift Platform","Chest","Chest","Watch tower","Ladder","Cave entrance","Pile of mud","Cave","Ladder","crate","crate","Gate","Gate","bush","bush","bush","bush","multicannon","Rocks","Rocks","Ladder","Cave entrance","Counter","Chest","Chest","Chest","Chest","Bookcase","Captains Chest","Experimental Anvil","Rocks","Rocks","Column","Wall","Wall","Wall","Wall","Lever","Lever","Wall","Ladder","Wall","Gate","Gate","Ladder","shock","Desk","Cave","Mining Cart","Rock of Dalgroth","entrance","Dried Cactus","climbing rocks","Rocks","lightning","Crude Desk","Heavy Metal Gate","Counter","Crude bed","flames","Carved Rock","USE","crate","crate","barrel","Brick","Brick","Brick","Brick","Brick","Brick","Barrier","buried skeleton","Brick","Brick","Specimen tray","winch","crate","crate","Urn","buried skeleton","panning point","Rocks","signpost","signpost","signpost","signpost","signpost","soil","soil","soil","Gate","ship","barrel","Leak","bush","bush","cupboard","sacks","sacks","Leak","cupboard","Wrought Mithril Gates","Legends Hall Doors","Camp bed","barrel","barrel","Chest","Chest","Dense Jungle Tree","Jungle tree stump","signpost","gate","Bookcase","Dense Jungle Tree","Dense Jungle Tree","Spray","Spray","winch","Brick","Rope","Rope","Dense Jungle Palm","Dense Jungle Palm","Trawler net","Trawler net","Brick","Chest","Chest","Trawler catch","Yommi Tree","Grown Yommi Tree","Chopped Yommi Tree","Trimmed Yommi Tree","Totem Pole","Baby Yommi Tree","Fertile earth","Rock Hewn Stairs","Hanging rope","Rocks","Boulder","dwarf multicannon","dwarf multicannon base","dwarf multicannon stand","dwarf multicannon barrels","rock","Rock Hewn Stairs","Rock Hewn Stairs","Rock Hewn Stairs","Compost Heap","beehive","Drain","web","fountain","Sinclair Crest","barrel","barrel","barrel","barrel","barrel","barrel","Flour Barrel","sacks","gate","Dead Yommi Tree","clawspell","Rocks","crate","Cavernous Opening","Ancient Lava Furnace","Spellcharge","Rocks","cupboard","sacks","Rock","Saradomin stone","Guthix stone","Zamorak stone","Magical pool","Wooden Beam","Rope down into darkness","Cave entrance","Cave entrance","Ancient Wooden Doors","Table","Crude bed","Tall Reeds","Goblin foot prints","Dark Metal Gate","Magical pool","Rope Up","Half buried remains","Totem Pole","Totem Pole","Comfy bed","Rotten Yommi Tree","Rotten Yommi Tree","Rotten Yommi Tree","Rotten Totem Pole","Leafy Palm Tree","Grand Viziers Desk","Strange Barrel","ship","ship","ship","digsite bed","Tea stall","Boulder","Boulder","Damaged Earth","Ladder","Ladder" }
SCENERY_NAMES[0] = "Tree"
SCENERY_NAMES[60000] = "Nothing"
BOUNDARY_NAMES = { "Doorframe","Door","Window","Fence","railings","Stained glass window","Highwall","Door","Doorframe","battlement","Doorframe","snowwall","arrowslit","timberwall","timberwindow","blank","highblank","mossybricks","Door","Door","Door","Odd looking wall","Door","web","Door","Door","Door","Door","Door","Door","Door","Door","Door","Window","Door","Door","Door","Door","Door","Door","Crumbled","Cavern","Door","Door","Door","cavern2","Door","Door","Door","Door","Door","Door","Door","Door","Door","Wall","Door","Strange looking wall","Door","Door","Door","memberrailings","Door","Door","Magic Door","Door","Door","Door","Door","Door","Door","Door","Door","Door","Door","Door","Door","Door","Strange Panel","Door","Door","Door","Door","Door","Door","blockblank","unusual looking wall","Door","Door","Door","Door","Door","Door","Door","Door","Door","Door","Door","Door","Door","Fence with loose pannels","Door","Door","Door","Door","Door","Door","Door","Door","Door","rat cage","Door","Door","Door","Door","Door","Door","arrowslit","solidblank","Door","Door","Door","Door","loose panel","Door","plankswindow","Low Fence","Door","Door","Door","Door","Door","Door","Door","Door","Door","Cooking pot","Door","Door","Door","Door","Door","Door","plankstimber","Door","Door","magic portal","magic portal","magic portal","Door","Cavern wall","Door","Door","Door","Door","Door","Door","Door","Door","Door","Door","Door","Low wall","Low wall","Blacksmiths Door","railings","railings","railings","railings","railings","railings","railings","Door","Doorframe","Tent","Jail Door","Jail Door","Window","magic portal","Jail Door","railings","railings","railings","railings","railings","railings","Cave exit","Cave exit","Cave exit","Cave exit","Cave exit","Cave exit","railings","Door","battlement","Tent Door","Door","Tent Door","Low Fence","Sturdy Iron Gate","battlement","Water","Wheat","Jungle","Window","Rut","Crumbled Cavern 1","Crumbled Cavern 2","cavernhole","flamewall","Ruined wall","Ancient Wall","Door" }
BOUNDARY_NAMES[0] = "Wall"

-- NPC data
NPC_NAMES = { "Bob","Sheep","Chicken","Goblin","Hans","cow","cook","Bear","Priest","Urhney","Man","Bartender","Camel","Gypsy","Ghost","Sir Prysin","Traiborn the wizard","Captain Rovin","Rat","Reldo","mugger","Lesser Demon","Giant Spider","Man","Jonny the beard","Baraek","Katrine","Tramp","Rat","Romeo","Juliet","Father Lawrence","Apothecary","spider","Delrith","Veronica","Weaponsmaster","Professor Oddenstein","Curator","skeleton","zombie","king","Giant bat","Bartender","skeleton","skeleton","Rat","Horvik the Armourer","Bear","skeleton","Shopkeeper","zombie","Ghost","Aubury","Shopkeeper","shopkeeper","Darkwizard","lowe","Thessalia","Darkwizard","Giant","Goblin","farmer","Thief","Guard","Black Knight","Hobgoblin","zombie","Zaff","Scorpion","silk trader","Man","Guide","Giant Spider","Peksa","Barbarian","Fred the farmer","Gunthor the Brave","Witch","Ghost","Wizard","Shop Assistant","Shop Assistant","Zeke","Louie Legs","Warrior","Shopkeeper","Shop Assistant","Highwayman","Kebab Seller","Chicken","Ernest","Monk","Dwarf","Banker","Count Draynor","Morgan","Dr Harlow","Deadly Red spider","Guard","Cassie","White Knight","Ranael","Moss Giant","Shopkeeper","Shop Assistant","Witch","Black Knight","Greldo","Sir Amik Varze","Guildmaster","Valaine","Drogo","Imp","Flynn","Wyson the gardener","Wizard Mizgog","Prince Ali","Hassan","Osman","Joe","Leela","Lady Keli","Ned","Aggie","Prince Ali","Jailguard","Redbeard Frank","Wydin","shop assistant","Brian","squire","Head chef","Thurgo","Ice Giant","King Scorpion","Pirate","Sir Vyvin","Monk of Zamorak","Monk of Zamorak","Wayne","Barmaid","Dwarven shopkeeper","Doric","Shopkeeper","Shop Assistant","Guide","Hetty","Betty","Bartender","General wartface","General Bentnoze","Goblin","Goblin","Herquin","Rommik","Grum","Ice warrior","Warrior","Thrander","Border Guard","Border Guard","Customs Officer","Luthas","Zambo","Captain Tobias","Gerrant","Shopkeeper","Shop Assistant","Seaman Lorris","Seaman Thresnor","Tanner","Dommik","Abbot Langley","Thordur","Brother Jered","Rat","Ghost","skeleton","zombie","Lesser Demon","Melzar the mad","Scavvo","Greater Demon","Shopkeeper","Shop Assistant","Oziach","Bear","Black Knight","chaos Dwarf","dwarf","Wormbrain","Klarense","Ned","skeleton","Dragon","Oracle","Duke of Lumbridge","Dark Warrior","Druid","Red Dragon","Blue Dragon","Baby Blue Dragon","Kaqemeex","Sanfew","Suit of armour","Adventurer","Adventurer","Adventurer","Adventurer","Leprechaun","Monk of entrana","Monk of entrana","zombie","Monk of entrana","tree spirit","cow","Irksol","Fairy Lunderwin","Jakut","Doorman","Fairy Shopkeeper","Fairy Shop Assistant","Fairy banker","Giles","Miles","Niles","Gaius","Fairy Ladder attendant","Jatix","Master Crafter","Bandit","Noterazzo","Bandit","Fat Tony","Donny the lad","Black Heather","Speedy Keith","White wolf sentry","Boy","Rat","Nora T Hag","Grey wolf","shapeshifter","shapeshifter","shapeshifter","shapeshifter","White wolf","Pack leader","Harry","Thug","Firebird","Achetties","Ice queen","Grubor","Trobert","Garv","guard","Grip","Alfonse the waiter","Charlie the cook","Guard Dog","Ice spider","Pirate","Jailer","Lord Darquarius","Seth","Banker","Helemos","Chaos Druid","Poison Scorpion","Velrak the explorer","Sir Lancelot","Sir Gawain","King Arthur","Sir Mordred","Renegade knight","Davon","Bartender","Arhein","Morgan le faye","Candlemaker","lady","lady","lady","Beggar","Merlin","Thrantax","Hickton","Black Demon","Black Dragon","Poison Spider","Monk of Zamorak","Hellhound","Animated axe","Black Unicorn","Frincos","Otherworldly being","Owen","Thormac the sorceror","Seer","Kharid Scorpion","Kharid Scorpion","Kharid Scorpion","Barbarian guard","Bartender","man","gem trader","Dimintheis","chef","Hobgoblin","Ogre","Boot the Dwarf","Wizard","Chronozon","Captain Barnaby","Customs Official","Man","farmer","Warrior","Guard","Knight","Paladin","Hero","Baker","silk merchant","Fur trader","silver merchant","spice merchant","gem merchant","Zenesha","Kangai Mau","Wizard Cromperty","RPDT employee","Horacio","Aemad","Kortan","zoo keeper","Make over mage","Bartender","chuck","Rogue","Shadow spider","Fire Giant","Grandpa Jack","Sinister stranger","Bonzo","Forester","Morris","Brother Omad","Thief","Head Thief","Big Dave","Joshua","Mountain Dwarf","Mountain Dwarf","Brother Cedric","Necromancer","zombie","Lucien","The Fire warrior of lesarkus","guardian of Armadyl","guardian of Armadyl","Lucien","winelda","Brother Kojo","Dungeon Rat","Master fisher","Orven","Padik","Shopkeeper","Lady servil","Guard","Guard","Guard","Guard","Jeremy Servil","Justin Servil","fightslave joe","fightslave kelvin","local","Khazard Bartender","General Khazard","Khazard Ogre","Guard","Khazard Scorpion","hengrad","Bouncer","Stankers","Docky","Shopkeeper","Fairy queen","Merlin","Crone","High priest of entrana","elkoy","remsai","bolkoy","local gnome","bolren","Black Knight titan","kalron","brother Galahad","tracker 1","tracker 2","tracker 3","Khazard troop","commander montai","gnome troop","khazard warlord","Sir Percival","Fisher king","maiden","Fisherman","King Percival","unhappy peasant","happy peasant","ceril","butler","carnillean guard","Tribesman","henryeta","philipe","clivet","cult member","Lord hazeel","alomone","Khazard commander","claus","1st plague sheep","2nd plague sheep","3rd plague sheep","4th plague sheep","Farmer brumty","Doctor orbon","Councillor Halgrive","Edmond","Citizen","Citizen","Citizen","Citizen","Citizen","Jethick","Mourner","Mourner","Ted Rehnison","Martha Rehnison","Billy Rehnison","Milli Rehnison","Alrena","Mourner","Clerk","Carla","Bravek","Caroline","Holgart","Holgart","Holgart","kent","bailey","kennith","Platform Fisherman","Platform Fisherman","Platform Fisherman","Elena","jinno","Watto","Recruiter","Head mourner","Almera","hudon","hadley","Rat","Combat instructor","golrie","Guide","King Black Dragon","cooking instructor","fishing instructor","financial advisor","gerald","mining instructor","Elena","Omart","Bank assistant","Jerico","Kilron","Guidor's wife","Quest advisor","chemist","Mourner","Mourner","Wilderness guide","Magic Instructor","Mourner","Community instructor","boatman","skeleton mage","controls guide","nurse sarah","Tailor","Mourner","Guard","Chemist","Chancy","Hops","DeVinci","Guidor","Chancy","Hops","DeVinci","king Lathas","Head wizard","Magic store owner","Wizard Frumscone","target practice zombie","Trufitus","Colonel Radick","Soldier","Bartender","Jungle Spider","Jiminua","Jogre","Guard","Ogre","Guard","Guard","shop keeper","Bartender","Frenita","Ogre chieftan","rometti","Rashiliyia","Blurberry","Heckel funch","Aluft Gianne","Hudo glenfad","Irena","Mosol","Gnome banker","King Narnode Shareen","UndeadOne","Drucas","tourist","King Narnode Shareen","Hazelmere","Glough","Shar","Shantay","charlie","Gnome guard","Gnome pilot","Mehman","Ana","Chaos Druid warrior","Gnome pilot","Shipyard worker","Shipyard worker","Shipyard worker","Shipyard foreman","Shipyard foreman","Gnome guard","Femi","Femi","Anita","Glough","Salarin the twisted","Black Demon","Gnome pilot","Gnome pilot","Gnome pilot","Gnome pilot","Sigbert the Adventurer","Yanille Watchman","Tower guard","Gnome Trainer","Gnome Trainer","Gnome Trainer","Gnome Trainer","Blurberry barman","Gnome waiter","Gnome guard","Gnome child","Earth warrior","Gnome child","Gnome child","Gulluck","Gunnjorn","Zadimus","Brimstail","Gnome child","Gnome local","Gnome local","Moss Giant","Gnome Baller","Goalie","Gnome Baller","Gnome Baller","Gnome Baller","Gnome Baller","Referee","Gnome Baller","Gnome Baller","Gnome Baller","Gnome Baller","Gnome Baller","Gnome Baller","Gnome Baller","Gnome Baller","Gnome Baller","Cheerleader","Cheerleader","Nazastarool Zombie","Nazastarool Skeleton","Nazastarool Ghost","Fernahei","Jungle Banker","Cart Driver","Cart Driver","Obli","Kaleb","Yohnus","Serevel","Yanni","Official","Koftik","Koftik","Koftik","Koftik","Blessed Vermen","Blessed Spider","Paladin","Paladin","slave","slave","slave","slave","slave","slave","slave","Kalrag","Niloof","Kardia the Witch","Souless","Othainian","Doomion","Holthion","Klank","Iban","Koftik","Goblin guard","Observatory Professor","Ugthanki","Observatory assistant","Souless","Dungeon spider","Kamen","Iban disciple","Koftik","Goblin","Chadwell","Professor","San Tojalon","Ghost","Spirit of Scorpius","Scorpion","Dark Mage","Mercenary","Mercenary Captain","Mercenary","Mining Slave","Watchtower wizard","Ogre Shaman","Skavid","Ogre guard","Ogre guard","Ogre guard","Skavid","Skavid","Og","Grew","Toban","Gorad","Ogre guard","Yanille Watchman","Ogre merchant","Ogre trader","Ogre trader","Ogre trader","Mercenary","City Guard","Mercenary","Lawgof","Dwarf","lollk","Skavid","Ogre guard","Nulodion","Dwarf","Al Shabim","Bedabin Nomad","Captain Siad","Bedabin Nomad Guard","Ogre citizen","Rock of ages","Ogre","Skavid","Skavid","Skavid","Draft Mercenary Guard","Mining Cart Driver","kolodion","kolodion","Gertrude","Shilop","Rowdy Guard","Shantay Pass Guard","Rowdy Slave","Shantay Pass Guard","Assistant","Desert Wolf","Workman","Examiner","Student","Student","Guide","Student","Archaeological expert","civillian","civillian","civillian","civillian","Murphy","Murphy","Sir Radimus Erkle","Legends Guild Guard","Escaping Mining Slave","Workman","Murphy","Echned Zekin","Donovan the Handyman","Pierre the Dog Handler","Hobbes the Butler","Louisa The Cook","Mary The Maid","Stanford The Gardener","Guard","Guard Dog","Guard","Man","Anna Sinclair","Bob Sinclair","Carol Sinclair","David Sinclair","Elizabeth Sinclair","Frank Sinclair","kolodion","kolodion","kolodion","kolodion","Irvig Senay","Ranalph Devere","Poison Salesman","Gujuo","Jungle Forester","Ungadulu","Ungadulu","Death Wing","Nezikchened","Dwarf Cannon engineer","Dwarf commander","Viyeldi","Nurmof","Fatigue expert","Karamja Wolf","Jungle Savage","Oomlie Bird","Sidney Smith","Siegfried Erkle","Tea seller","Wilough","Philop","Kanel","chamber guardian","Sir Radimus Erkle","Pit Scorpion","Shadow Warrior","Fionella","Battle mage","Battle mage","Battle mage","Gundai","Lundail" }
NPC_NAMES[0] = "Unicorn"

-- Names of message types are from RSC+
MESSAGE_TYPES = { "Private Incoming", "Private Outgoing", "Quest", "Chat", "Private Log In/Log Out", "Trade Request Received", "Other" }
MESSAGE_TYPES[0] = "None"

-- Spelling & Capitalization is mirrored from Quest List as displayed in client
QUEST_NAMES = { "Cook's assistant", "Demon slayer", "Doric's quest", "The restless ghost", "Goblin diplomacy", "Ernest the chicken", "Imp catcher", "Pirate's treasure", "Prince Ali rescue", "Romeo & Juliet", "Sheep shearer", "Shield of Arrav", "The knight's sword", "Vampire slayer", "Witch's potion", "Dragon slayer", "Witch's house", "Lost city", "Hero's quest", "Druidic ritual", "Merlin's crystal", "Scorpion catcher", "Family crest", "Tribal totem", "Fishing contest", "Monk's friend", "Temple of Ikov", "Clock tower", "The Holy Grail", "Fight Arena", "Tree Gnome Village", "The Hazeel Cult", "Sheep Herder", "Plague City", "Sea Slug", "Waterfall quest", "Biohazard", "Jungle potion", "Grand tree", "Shilo village", "Underground pass", "Observatory quest", "Tourist trap", "Watchtower", "Dwarf Cannon", "Murder Mystery", "Digsite", "Gertrude's Cat", "Legend's Quest" }
QUEST_NAMES[0] = "Black knight's fortress"

SKILL_NAMES = { "Defense", "Strength", "Hits", "Ranged", "Prayer", "Magic", "Cooking", "Woodcutting", "Fletching", "Fishing", "Firemaking", "Crafting", "Smithing", "Mining", "Herblaw", "Agility", "Thieving" }
SKILL_NAMES[0] = "Attack"

PRAYER_NAMES = { "Burst of strength", "Clarity of thought", "Rock skin", "Superhuman strength", "Improved reflexes", "Rapid restore", "Rapid heal", "Protect items", "Steel skin", "Ultimate strength", "Incredible reflexes", "Paralyze monster", "Protect from missiles" }
PRAYER_NAMES[0] = "Thick Skin"

SPELL_NAMES = { "Confuse","Water strike","Enchant lvl-1 amulet","Earth strike","Weaken","Fire strike","Bones to bananas","Wind bolt","Curse","Low level alchemy","Water bolt","Varrock teleport","Enchant lvl-2 amulet","Earth bolt","Lumbridge teleport","Telekinetic grab","Fire bolt","Falador teleport","Crumble undead","Wind blast","Superheat item","Camelot teleport","Water blast","Enchant lvl-3 amulet","Iban blast","Ardougne teleport","Earth blast","High level alchemy","Charge water orb","Enchant lvl-4 amulet","Watchtower teleport","Fire blast","Charge earth orb","Claws of Guthix","Saradomin Strike","Flames of Zamorak","Wind wave","Charge Fire Orb","Water wave","Charge air orb","Vulnerability","Enchant lvl-5","Earth wave","Enfeeble","Fire wave","Stun","Charge" }
SPELL_NAMES[0] = "Wind strike"

ANIMATION_NAMES = {}
ANIMATION_NAMES[0] = "Facing North"
ANIMATION_NAMES[1] = "Facing Northwest"
ANIMATION_NAMES[2] = "Facing West"
ANIMATION_NAMES[3] = "Facing Southwest"
ANIMATION_NAMES[4] = "Facing South"
ANIMATION_NAMES[5] = "Facing Southeast"
ANIMATION_NAMES[6] = "Facing East"
ANIMATION_NAMES[7] = "Facing Northeast"
ANIMATION_NAMES[8] = "In Combat (facing right)"
ANIMATION_NAMES[9] = "In Combat (facing left)"

-- These names were never displayed in game as far as I know, so they're pretty bad.
ANIMATION_SPRITE_NAMES_RAW = { "head1","body1","legs1","fhead1","fbody1","head2","head3","head4","chefshat","apron","apron","boots","fullhelm","fullhelm","fullhelm","fullhelm","fullhelm","fullhelm","fullhelm","fullhelm","chainmail","chainmail","chainmail","chainmail","chainmail","chainmail","chainmail","platemailtop","platemailtop","platemailtop","platemailtop","platemailtop","platemailtop","platemailtop","platemailtop","platemailtop","platemaillegs","platemaillegs","platemaillegs","platemaillegs","platemaillegs","platemaillegs","platemaillegs","platemaillegs","platemaillegs","leatherarmour","leathergloves","sword","sword","sword","sword","sword","sword","sword","fplatemailtop","fplatemailtop","fplatemailtop","fplatemailtop","fplatemailtop","fplatemailtop","fplatemailtop","apron","cape","cape","cape","cape","cape","cape","cape","mediumhelm","mediumhelm","mediumhelm","mediumhelm","mediumhelm","mediumhelm","mediumhelm","wizardsrobe","wizardshat","wizardshat","necklace","necklace","skirt","wizardsrobe","Wizardsrobe","wizardsrobe","Wizardsrobe","skirt","skirt","skirt","skirt","skirt","Skirt","skirt","skirt","skirt","skirt","skirt","squareshield","squareshield","squareshield","squareshield","squareshield","squareshield","squareshield","squareshield","squareshield","crossbow","longbow","battleaxe","battleaxe","battleaxe","battleaxe","battleaxe","battleaxe","battleaxe","mace","mace","mace","mace","mace","mace","mace","staff","rat","demon","spider","spider","camel","cow","sheep","unicorn","bear","chicken","skeleton","skelweap","zombie","zombweap","ghost","bat","goblin","goblin","goblin","gobweap","scorpion","dragon","dragon","dragon","Wolf","Wolf","partyhat","partyhat","partyhat","partyhat","partyhat","partyhat","leathergloves","chicken","fplatemailtop","skirt","Wolf","spider","battleaxe","sword","eyepatch","demon","dragon","spider","Wolf","unicorn","demon","spider","necklace","rat","mediumhelm","chainmail","wizardshat","legs1","gasmask","mediumhelm","spider","spear","halloweenmask","wizardsrobe","skirt","halloweenmask","halloweenmask","skirt","skirt","skirt","skirt","skirt","wizardshat","wizardshat","wizardshat","wizardshat","wizardshat","wizardsrobe","wizardsrobe","wizardsrobe","wizardsrobe","wizardsrobe","Wizardsrobe","skirt","boots","boots","boots","boots","boots","santahat","ibanstaff","souless","boots","legs1","Wizardsrobe","skirt","cape","Wolf","bunnyears","saradominstaff","spear","skirt","wizardsrobe","wolf","chicken","squareshield","cape","boots","wizardsrobe","Scythe" }
ANIMATION_SPRITE_NAMES_RAW[0] = "None"
-- These are names that I came up with based on ANIMATION_SPRITE_NAMES_RAW & by viewing the sprites in-client.
ANIMATION_SPRITE_NAMES = { "Short Hair","Male Body","Coloured Pants","Long Hair","Female Body","Short Hair 2","Long Bearded Head","Bald Head","Chef's Hat","White Apron","Brown Apron","Leather Boots","Large Bronze Helmet","Large Iron Helmet","Large Steel Helmet","Large Mithril Helmet","Large Adamantite Helmet","Large Rune Helmet","Large Black Helmet","Large White Helmet","Bronze Chain Mail Body","Iron Chain Mail Body","Steel Chain Mail Body","Mithril Chain Mail Body","Adamantite Chain Mail Body","Rune Chain Mail Body","Black Chain Mail Body","Bronze Plate Mail Body","Iron Plate Mail Body","Steel Plate Mail Body","Mithril Plate Mail Body","Adamantite Plate Mail Body","Black Plate Mail Body","Rune Plate Mail Body","White Plate Mail Body","Unused Plate Mail Body (Colour between Mithril & Rune)","Bronze Plate Mail Legs","Iron Plate Mail Legs","Steel Plate Mail Legs","Mithril Plate Mail Legs","Adamantite Plate Mail Legs","Rune Plate Mail Legs","Black Plate Mail Legs","White Plate Mail Legs","Unused Plate Mail Legs (Colour between Mithril & Rune)","Leather Armour","Leather Gloves","Bronze Sword","Iron Sword","Steel Sword","Mithril Sword","Adamantite Sword","Rune Sword","Black Sword","Female Bronze Plate Mail top","Female Iron Plate Mail top","Female Steel Plate Mail top","Female Mithril Plate Mail top","Female Adamantite Plate Mail top","Female Rune Plate Mail top","Female Black Plate Mail top","White Apron","Red Cape","Black Cape","Blue Cape","Green Cape","Yellow Cape","Orange Cape","Purple Cape","Medium Bronze Helmet","Medium Iron Helmet","Medium Steel Helmet","Medium Mithril Helmet","Medium Adamantite Helmet","Medium Rune Helmet","Medium Black Helmet","Blue Wizards Robe","Blue Wizardshat","Black Wizardshat","Silver Necklace","Gold Necklace (could be any of them)","Blue Skirt","Black Wizards Robe","Saradomin Monk Robe","Zamorak Monk Robe","Druid Robe","Druid Skirt","Saradomin Monk Skirt","Black Wizards Skirt","Pink Skirt","Zamorak Monk Skirt","Bronze Plated Skirt","Iron Plated Skirt","Steel Plated Skirt","Mithril Plated Skirt","Adamantite Plated Skirt","Rune Plated Skirt","Bronze Square Shield","Iron Square Shield","Steel Square Shield","Mithril Square Shield","Adamantite Square Shield","Rune Square Shield","Black Square Shield","Anti Dragon Breath Shield","Wooden Shield","Crossbow ","Longbow","Bronze Battleaxe","Iron Battleaxe","Steel Battleaxe","Mithril Battleaxe","Adamantite Battleaxe","Rune Battleaxe","Black Battleaxe","Bronze Mace","Iron Mace","Steel Mace","Mithril Mace","Adamantite Mace","Rune Mace","Black Mace","Staff","Rat","Red Demon","Brown Spider","Red Spider","Camel","Cow","Sheep","Unicorn","Bear","Chicken","Skeleton","Skeleton's Scimitar and Shield","Zombie","Zombie's Axe","Ghost","Bat","Goblin","Goblin with Red Armour","Goblin with Green Armour","Goblin's Spear","Scorpion","Elvarg","Red Dragon","Blue Dragon","White Wolf","Grey Wolf","Red Party Hat","Yellow Party Hat","Blue Party Hat","Green Party Hat","Pink Party Hat","White Party Hat","Ice Gloves","Firebird","Unused Plate Mail top (Colour between Mithril & Rune)","Priest Gown","Karamja Wolf","Ice Spider","Dragon Battleaxe","Dragon Sword","Right Eyepatch","Black Demon","Black Dragon","Poison Spider","Hellhound","Black Unicorn","Chronozon","Shadow Spider","Pendant of Lucien","Dungeon Rat","Khazard Helmet","Khazard Chainmail","Zamorak Wizardshat (unused)","Mourner Legs","Gas Mask","Dragon Medium Helmet","Jungle Spider","Spear","Green Halloween Mask","Priest Robe","Priest Gown","Red Halloween Mask","Blue Halloween Mask","Pastel Pink Gnome Skirt","Pastel Green Gnome Skirt","Pastel Blue Gnome Skirt","Pastel Yellow Gnome Skirt","Pastel Cyan Gnome Skirt","Pastel Pink Gnomeshat","Pastel Green Gnomeshat","Pastel Blue Gnomeshat","Pastel Yellow Gnomeshat","Pastel Cyan Gnomeshat","Pastel Pink Gnome Top","Pastel Green Gnome Top","Pastel Blue Gnome Top","Pastel Yellow Gnome Top","Pastel Cyan Gnome Top","Green Robe (unused, perhaps planned for Brimstail)","Green Skirt (unused, perhaps planned for Brimstail)","Pastel Pink Gnome Boots","Pastel Green Gnome Boots","Pastel Blue Gnome Boots","Pastel Yellow Gnome Boots","Pastel Cyan Gnome Boots","Santa's Hat","Staff of Iban","Souless","Desert Boots","White Pants","Slaves Robe Top","Slaves Robe Bottom","Cape of Legends","Desert Wolf","Bunny Ears","Staff of Saradomin","Gujou's Rune Spear","Gujuo's Skirt","Gujuo's Robe top","Karamja Wolf","Oomlie Bird","Dragon Square Shield","Cape of Legends","Shadow Warrior Boots","Shadow Warrior Robe","Scythe" }
ANIMATION_SPRITE_NAMES[0] = "None"

 -- this.anIntArray1465
HAIR_COLOURS = {         0xffa040,          0x805030,      0x604020,     0x303030,           0xff6020,            0xff4000,     0xffffff,  0x00ff00,     0x00ffff }
HAIR_COLOURS_STRING = {  "#FFA040",         "#805030",     "#604020",    "#303030",          "#FF6020",           "#FF4000",    "#FFFFFF", "#00FF00",    "#00FFFF" } 
HAIR_COLOUR_NAMES = {    "Sunshade Orange", "Korma Brown", "Dark Brown", "Night Rider Grey", "Outrageous Orange", "Orange Red", "White",   "Lime Green", "Cyan"}
HAIR_COLOURS[0] =        0xffc030
HAIR_COLOURS_STRING[0] = "#FFC030"
HAIR_COLOUR_NAMES[0] =   "Saffron Yellow"

-- this.anIntArray1548
SKIN_COLOURS = {         0xccb366,      0xb38c40,   0x997326,             0x906020 } 
SKIN_COLOURS_STRING = {  "#CCB366",     "#B38C40",  "#997326",            "#906020" }
SKIN_COLOUR_NAMES = {    "Tacha (Tan)", "Marigold", "Buttered Rum Brown", "Brown" }
SKIN_COLOURS[0] =        0xecded0
SKIN_COLOURS_STRING[0] = "#ECDED0"
SKIN_COLOUR_NAMES[0] =   "Spring Wood (Pale White)"

 -- this.anIntArray1592
CLOTHING_COLOURS =      {    0xff8000,      0xffe000,  0xa0e000,          0x00e000,     0x008000,  0x00a080,                 0x00b0ff,        0x0080ff,      0x0030f0,  0xe000e0,       0x303030,           0x604000,     0x805000,      0xffffff }
CLOTHING_COLOURS_STRING = {  "#FF8000",     "#FFE000", "#A0E000",         "#00E000",    "#008000", "#00A080",                "#00B0FF",       "#0080FF",     "#0030F0", "#E000E0",      "#303030",          "#604000",    "#805000",     "#FFFFFF" }
CLOTHING_COLOUR_NAMES = {    "Dark Orange", "Yellow",  "Inch Worm Green", "Lime Green", "Green",   "Free Speech Aquamarine", "Deep Sky Blue", "Dodger Blue", "Blue",    "Deep Magenta", "Night Rider Grey", "Dark Brown", "Light Brown", "White" }
CLOTHING_COLOURS[0] =        0xff0000
CLOTHING_COLOURS_STRING[0] = "#FF0000"
CLOTHING_COLOUR_NAMES[0] =   "Red"

BUBBLE_TYPE_NAMES = { "Telegrab/Iban's Magic" } -- Both Telegrab & Iban's magic are type 1.
BUBBLE_TYPE_NAMES[0] = "Teleportation"

SPRITE_TYPES = {} -- there is no sprite 0, or sprite greater than 6
SPRITE_TYPES[1] = "Magic Projectile"
SPRITE_TYPES[2] = "Ranged Projectile"
SPRITE_TYPES[3] = "Gnomeball"
SPRITE_TYPES[4] = "Iban Blast" -- never on opcode 234 type 3, only on type 4
SPRITE_TYPES[5] = "Cannonball"
SPRITE_TYPES[6] = "God Spell" -- TODO: observed only claws of guthix / saradomin strike

BOUNDARY_ALIGNMENTS = { "North-South", "Northwest-Southeast", "Southwest-Northeast" }
BOUNDARY_ALIGNMENTS[0] = "East-West"

OPTION_NAMES = { "Player Killer (Unused in RSC235)", "Number of Mouse Buttons", "Sound Enabled" }
OPTION_NAMES[0] = "Camera Mode Auto"

REPORT_REASONS = { "Buying or selling an account", "Encouraging rule-breaking", "Staff impersonation", "Macroing or use of bots", "Scamming", "Exploiting a bug", "Seriously offensive language", "Solicitation", "Disruptive behaviour", "Offensive account name", "Real-life threats", "Asking for or providing contact information", "Breaking real-world laws", "Advertising websites" }
-- there is no reason 0

--============================================================================--

-- this is a little excessive, but we're going to track everything about a player the client does
function rsc_newCharacter()
  rsc_newChar = {}
  rsc_newChar["damageTaken"] = 0
  rsc_newChar["accountName"] = nil
  rsc_newChar["healthMax"] = 0
  rsc_newChar["stepCount"] = nil
  rsc_newChar["colourTop"] = nil
  rsc_newChar["messageTimeout"] = 0
  rsc_newChar["projectileRange"] = 0
  rsc_newChar["movingStep"] = 0
  rsc_newChar["waypointCurrent"] = nil
  rsc_newChar["animationCurrent"] = nil
  rsc_newChar["skullVisible"] = 0
  rsc_newChar["incomingProjectileSprite"] = 0
  rsc_newChar["bubbleTimeout"] = 0
  rsc_newChar["colourBottom"] = nil
  rsc_newChar["displayName"] = nil
  rsc_newChar["attackingPlayerServerIndex"] = 0
  rsc_newChar["serverIndex"] = nil
  rsc_newChar["message"] = nil
  rsc_newChar["combatTimeout"] = 0
  rsc_newChar["currentX"] = nil
  rsc_newChar["colourHair"] = nil
  rsc_newChar["level"] = -1
  rsc_newChar["npcId"] = nil
  rsc_newChar["attackingNpcServerIndex"] = 0
  rsc_newChar["colourSkin"] = nil
  rsc_newChar["animationNext"] = nil
  rsc_newChar["currentY"] = nil
  rsc_newChar["bubbleItem"] = nil
  rsc_newChar["healthCurrent"] = 0
  rsc_newChar["waypointsX"] = {}
  rsc_newChar["waypointsY"] = {}
  rsc_newChar["equippedItem"] = {}
  return rsc_newChar
end

--============================================================================--
-- creates new objects
function dereferencePlayerArrays()
  newThisPlayers = {}
  newThisPlayerServer = {}
  newThisPlayerCount = 0
  for key, value in next, thisPlayers do
    newThisPlayers[key] = value
  end
  for key, value in next, thisPlayerServer do
    newThisPlayerServer[key] = value
  end
  return { newThisPlayers, newThisPlayerCount + thisPlayerCount, newThisPlayerServer }
end

function dereferenceList(list)
  newCache = {}
  for key, value in next, list do
    newCache[key] = value
  end
  return newCache
end

--============================================================================--

-- necessary to keep these values over multiple opcodes for context
thisPlayerCount = 0
thisPlayers = {}
thisKnownPlayerCount = 0
thisKnownPlayers = {}
thisPlayerServer = {}
thisNpcs = {}
thisNpcsServer = {}
thisNpcsCache = {}
thisNpcsCacheCount = 0
thisNpcsCount = 0
thisLocalPlayer = rsc_newCharacter()
for i = 0, 500 do
  thisPlayers[i] = rsc_newCharacter()
  thisKnownPlayers[i] = rsc_newCharacter()
  thisPlayerServer[i] = rsc_newCharacter()
  thisNpcs[i] = rsc_newCharacter()
  thisNpcsServer[i] = rsc_newCharacter()
  thisNpcsCache[i] = rsc_newCharacter()
end
for i=500,4000 do
  thisPlayerServer[i] = rsc_newCharacter()
  thisNpcsServer[i] = rsc_newCharacter()
end
for i=4000,5000 do
  thisNpcsServer[i] = rsc_newCharacter()
end
thisRegionX = nil
thisRegionY = nil
thisLocalRegionX = nil
thisLocalRegionY = nil
thisMagicLoc = 128
thisLocalPlayerServerIndex = -1
thisTradeConfirmItems = nil

--============================================================================--
-- other globals
workingBuffer = nil
thisOptionCache = {} -- not an authentic global, just using it to show what option was selected

-- Wireshark first goes through the entire file frame by frame sequentially, which is good,
-- because it lets us use data from previous opcodes to reference necessary variables in
-- the opcode we are currently trying to intepret, the same way that RSC does.
-- 
-- HOWEVER, Wireshark will then re-dissect frames according to how the user interacts with
-- the graphical interface, i.e. in an unpredictable order, from the middle, "randomly".
-- This screws with the "game state" and displays incorrect data.
--
-- In tshark, it will only go through the file one time, and the user doesn't get a chance
-- to screw up the gamestate by clicking on random frames to be re-examined.
-- 
-- In order to mitigate the problem in Wireshark, the first time the frame is dissected,
-- when the game state is still intact and sequentially interpretted,
-- we fill this array below to cache any values needed in order to correctly
-- display the dissected frame once it is dissected subsequent times
--
-- Without this, all opcodes still display data properly, except for the following:
-- 116c, 36, 48, 79, 91, 92, 99, 123, 176, 191, 211, 234
thisGameStateValuesAtThisTime = {}

thisPlayerInventory = { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 }
thisPlayerInventory[0] = -1

-- pixels for drawing sleepwords in unicode blocks (unfortunately can't draw more than 158 pixels wide and we would need 255px)
blockChars = { "▗","▖","▄","▝","▐","▞","▟","▘","▚","▌","▙","▀","▜","▛","█" }
blockChars[0] = " "

--============================================================================--

--------------------------------------------------------------------------------
-- shared client/server variables
--------------------------------------------------------------------------------
clientPacket = ProtoField.uint8("rsc235.packetSource", "Server/Client")
clientOpcode = ProtoField.uint32("rsc235.opcode", "Opcode")
clientPacketLength = ProtoField.uint16("rsc235.packetLength", "Packet Length")
--------------------------------------------------------------------------------

--------------------------------------------------------------------------------
-- client opcode related variables
--------------------------------------------------------------------------------
clientConnectReconnecting = ProtoField.uint8("rsc235.0c.reconnecting", "Reconnecting")
clientConnectVersion = ProtoField.uint8("rsc235.0c.clientVersion", "Client Version")
clientConnectEncrypted = ProtoField.bytes("rsc235.0c.encrypted", "Encrypted Block")

clientCastOnInventoryInventoryIndex = ProtoField.int16("rsc235.4c.slot", "Inventory Slot")
clientCastOnInventorySpellID = ProtoField.int16("rsc235.4c.spellID", "Spell ID")

clientSendDuelSettingsRetreat = ProtoField.uint8("rsc235.8c.retreatEnabled", "Retreat Enabled")
clientSendDuelSettingsMagic = ProtoField.uint8("rsc235.8c.magicEnabled", "Magic Enabled")
clientSendDuelSettingsPrayer = ProtoField.uint8("rsc235.8c.prayerEnabled", "Prayer Enabled")
clientSendDuelSettingsWeapons = ProtoField.uint8("rsc235.8c.weaponsEnabled", "Weapons Enabled")

clientInteractWithBoundaryXCoord = ProtoField.uint16("rsc235.14c.xCoord", "X Coordinate")
clientInteractWithBoundaryYCoord = ProtoField.uint16("rsc235.14c.yCoord", "Y Coordinate")
clientInteractWithBoundaryAlignment = ProtoField.uint8("rsc235.14c.alignment", "Alignment")

clientWalkAndPerformActionX = ProtoField.int16("rsc235.16c.xCoord", "X Coordinate")
clientWalkAndPerformActionY = ProtoField.int16("rsc235.16c.yCoord", "Y Coordinate")
clientWalkAndPerformActionPathX = ProtoField.int8("rsc235.16c.xCoordPath", "X Coordinate Path")
clientWalkAndPerformActionPathY = ProtoField.int8("rsc235.16c.yCoordPath", "Y Coordinate Path")

clientBankWithdrawItemID = ProtoField.uint16("rsc235.22c.itemID", "Item ID")
clientBankWithdrawAmount = ProtoField.uint32("rsc235.22c.withdrawAmount", "Amount to Withdraw")
clientBankWithdrawMagicNumber = ProtoField.uint32("rsc235.22c.magicNumber", "Magic Number which could not change and whose purpose is unknown", base.HEX)

clientBankDepositItemID = ProtoField.uint16("rsc235.23c.itemID", "Item ID")
clientBankDepositAmount = ProtoField.uint32("rsc235.23c.withdrawAmount", "Amount to Deposit")
clientBankDepositMagicNumber = ProtoField.uint32("rsc235.23c.magicNumber", "Magic Number which could not change and whose purpose is unknown", base.HEX)

clientCombatStyle = ProtoField.uint8("rsc235.29c.combatStyle", "Style")

clientSendStakedItemsCount = ProtoField.uint8("rsc235.33c.itemCount", "Number of Items to be Staked by Player")
clientSendStakedItemsItemID = ProtoField.uint16("rsc235.33c.itemId", "Item ID of Item to be Staked by Player")
clientSendStakedItemsItemAmount = ProtoField.uint32("rsc235.33c.itemIdStackamount", "Amount of Item to be Staked by Player")

clientSendCommandStringCommand = ProtoField.string("rsc235.38c.command", "\"::[command]\"")

clientGuessSleepwordDelay = ProtoField.uint8("rsc235.45c.delay", "Delay")
clientGuessSleepwordGuess = ProtoField.string("rsc235.45c.wordGuess", "Guess at Word")

clientOfferItemItemCount = ProtoField.uint8("rsc235.46c.itemCount", "Number of items offered")
clientOfferItemItemID = ProtoField.uint16("rsc235.46c.itemID", "Item ID")
clientOfferItemItemStack = ProtoField.uint32("rsc235.46c.itemStackCount", "Items in this stack")

clientCastNPCSpellID = ProtoField.uint16("rsc235.50c.spellID", "Spell ID")
clientCastNPCNPCID = ProtoField.uint16("rsc235.50c.npcID", "NPC Server Index")

clientUseOnGroundItemXCoord = ProtoField.uint16("rsc235.53c.xCoord", "X Coordinate")
clientUseOnGroundItemYCoord = ProtoField.uint16("rsc235.53c.yCoord", "Y Coordinate")
clientUseOnGroundItemInventorySlot = ProtoField.uint16("rsc235.53c.slot", "Inventory Slot")
clientUseOnGroundItemGroundItemID = ProtoField.uint16("rsc235.53c.groundItemID", "Ground Item's ID")

clientAdminTeleportXCoord = ProtoField.uint16("rsc235.59c.xCoord", "X Coordinate")
clientAdminTeleportYCoord = ProtoField.uint16("rsc235.59c.yCoord", "Y Coordinate")

clientPrayerOnPrayer = ProtoField.int8("rsc235.60c.prayer", "Prayer")

clientSendPrivacySettingsChat = ProtoField.uint8("rsc235.64c.chat", "Block Chat")
clientSendPrivacySettingsPrivate = ProtoField.uint8("rsc235.64c.private", "Block Private")
clientSendPrivacySettingsTrade = ProtoField.uint8("rsc235.64c.trade", "Block Trade")
clientSendPrivacySettingsDuel = ProtoField.uint8("rsc235.64c.duel", "Block Duel")

clientInteractWithScenery2XCoord = ProtoField.uint16("rsc235.79c.xCoord", "X Coordinate")
clientInteractWithScenery2YCoord = ProtoField.uint16("rsc235.79c.yCoord", "Y Coordinate")

clientActivateInventoryItemSlot = ProtoField.uint16("rsc235.90c.slot", "Inventory Slot Index of item to be activated")
-- TODO: should add field for what the item activated is

clientCombineInventorySlot1 = ProtoField.uint16("rsc235.91c.slot1", "Inventory Slot Index of first item to be combined")
clientCombineInventorySlot2 = ProtoField.uint16("rsc235.91c.slot2", "Inventory Slot Index of second item to be combined")
-- TODO: should add fields for what the items actually are, to be used in searches (if the wireshark dissector is a search tool...)

clientCastOnSceneryXCoord = ProtoField.uint16("rsc235.99c.xCoord", "X Coordinate")
clientCastOnSceneryYCoord = ProtoField.uint16("rsc235.99c.yCoord", "Y Coordinate")
clientCastOnScenerySpellID = ProtoField.uint16("rsc235.99c.spellID", "Spell ID")

clientSendDuelRequest = ProtoField.int16("rsc235.103c.PID", "PID of Player to send Duel Request to")

clientSetting = ProtoField.uint8("rsc235.111c.setting", "Option")
clientSettingValue = ProtoField.uint8("rsc235.111c.value", "Enabled?")

clientUseOnPlayerPID = ProtoField.uint16("rsc235.113c.pid", "PID")
clientUseOnPlayerSlot = ProtoField.uint16("rsc235.113c.slot", "Inventory Slot Index of item to be used on player")

clientUseOnSceneryX = ProtoField.int16("rsc235.115c.xCoord", "X Coordinate")
clientUseOnSceneryY = ProtoField.int16("rsc235.115c.yCoord", "Y Coordinate")
clientUseOnScenerySlot = ProtoField.int16("rsc235.115c.slot", "Inventory Slot Index of item to be used on scenery")

clientOption = ProtoField.uint8("rsc235.116c.clientOption", "Option")

clientInteractWithBoundary2XCoord = ProtoField.uint16("rsc235.127c.xCoord", "X Coordinate")
clientInteractWithBoundary2YCoord = ProtoField.uint16("rsc235.127c.yCoord", "Y Coordinate")
clientInteractWithBoundary2Alignment = ProtoField.uint8("rsc235.127c.alignment", "Alignment")

clientAddIgnoreName = ProtoField.string("rsc235.132c.name", "Name of Person to Ignore")

clientUseOnNPCNPCID = ProtoField.uint16("rsc235.135c.npcID", "NPC Server Index")
clientUseOnNPCSlot = ProtoField.int16("rsc235.135c.slot", "Inventory Slot Index of item to be used on NPC")

clientInteractWithSceneryX = ProtoField.int16("rsc235.136c.xCoord", "X Coordinate")
clientInteractWithSceneryY = ProtoField.int16("rsc235.136c.yCoord", "Y Coordinate")

clientCastOnSelfSpell = ProtoField.int16("rsc235.137c.spell", "Spell to Cast on Self")

clientAgreeToTradePID = ProtoField.int16("rsc235.142c.PID", "PID of Player to Trade")

clientTalkToNPCID = ProtoField.uint16("rsc235.153c.npcID", "NPC Server Index")

clientCastOnGroundXCoord = ProtoField.int16("rsc235.158c.xCoord", "X Coordinate")
clientCastOnGroundYCoord = ProtoField.int16("rsc235.158c.yCoord", "Y Coordinate")
clientCastOnGroundSpell = ProtoField.int16("rsc235.158c.spell", "Spell to Cast")

clientUseWithBoundaryXCoord = ProtoField.int16("rsc235.161c.xCoord", "X Coordinate")
clientUseWithBoundaryYCoord = ProtoField.int16("rsc235.161c.yCoord", "Y Coordinate")
clientUseWithBoundaryAlignment = ProtoField.uint8("rsc235.161c.alignment", "Alignment")
clientUseWithBoundarySlot = ProtoField.int16("rsc235.161c.slot", "Inventory Slot Index of item to be used on boundary")

clientFollowPlayerPID = ProtoField.int16("rsc235.165c.PID", "PID of Player to Follow")

clientRemoveFriendName = ProtoField.string("rsc235.167c.name", "Name of Ex-Friend")

clientEquipItemSlot = ProtoField.int16("rsc235.169c.slot", "Inventory Slot Index of item to be equipped")

clientUnequipItemItem = ProtoField.int16("rsc235.170c.slot", "Inventory Slot Index of item to be unequipped")

clientAttackPlayerTargetPID = ProtoField.int16("rsc235.171c.targetPID", "Target's PID")

clientCastOnBoundaryXCoord = ProtoField.int16("rsc235.180c.xCoord", "X Coordinate")
clientCastOnBoundaryYCoord = ProtoField.int16("rsc235.180c.yCoord", "Y Coordinate")
clientCastOnBoundaryAlignment = ProtoField.uint8("rsc235.180c.alignment", "Alignment")
clientCastOnBoundarySpell = ProtoField.int16("rsc235.180c.spell", "Spell to Cast")

clientWalkX = ProtoField.int16("rsc235.187c.xCoord", "X Coordinate")
clientWalkY = ProtoField.int16("rsc235.187c.yCoord", "Y Coordinate")
clientWalkPathX = ProtoField.int8("rsc235.187c.xCoordPath", "X Coordinate Path")
clientWalkPathY = ProtoField.int8("rsc235.187c.yCoordPath", "Y Coordinate Path")

clientAttackNPCID = ProtoField.uint16("rsc235.190c.npcID", "NPC Server Index")

clientAddFriendName = ProtoField.string("rsc235.195c.name", "Name of New Friend")

clientInteractNPCID = ProtoField.uint16("rsc235.202c.npcID", "NPC Server Index")

clientSendReportName = ProtoField.string("rsc235.206c.name", "Name of Troublemaker")
clientSendReportReason = ProtoField.int8("rsc235.206c.reason", "Reason")
clientSendReportMute = ProtoField.int8("rsc235.206c.mute", "Add to Ignore List?")

clientSendChatMessageLength = ProtoField.int16("rsc235.216c.messageLength", "Desired Message Length")
clientSendChatMessageMessage = ProtoField.string("rsc235.216c.message", "Desired Chat Message (scrambled)")

clientSendPrivateMessageRecipient = ProtoField.string("rsc235.218c.recipient", "Desired Recipient")
clientSendPrivateMessageLength = ProtoField.int16("rsc235.218c.messageLength", "Desired Message Length")
clientSendPrivateMessageMessage = ProtoField.string("rsc235.218c.message", "Desired Message (scrambled)")

clientSellToShopItemId = ProtoField.int16("rsc235.221c.itemID", "Item ID")
clientSellToShopShopAmount = ProtoField.int16("rsc235.221c.shopAmount", "Shop Amount")
clientSellToShopSellAmount = ProtoField.int16("rsc235.221c.sellAmount", "Sell Amount")

clientCastPVPTargetPID = ProtoField.int16("rsc235.229c.targetPID", "Target's PID")
clientCastPVPSpellID = ProtoField.int16("rsc235.229c.spellID", "Spell ID")

clientChangeAppearanceHeadRestrictions = ProtoField.int8("rsc235.235c.headRestrictions", "Head Restrictions")
clientChangeAppearanceHeadType = ProtoField.int8("rsc235.235c.headType", "Head Type")
clientChangeAppearanceBodyType = ProtoField.int8("rsc235.235c.bodyType", "Body Type")
clientChangeAppearanceAlways2 = ProtoField.int8("rsc235.235c.always2", "Always 2, even in very old versions of client") -- I checked old versions, and this value is always 2 even in client versions 40 & 127 (Those versions have extra fields for PKer & Class)
clientChangeAppearanceHairColour = ProtoField.int8("rsc235.235c.hairColour", "Hair Colour")
clientChangeAppearanceTopColour = ProtoField.int8("rsc235.235c.topColour", "Top Colour")
clientChangeAppearenceBottomColour = ProtoField.int8("rsc235.235c.bottomColour", "Bottom Colour")
clientChangeAppearenceSkinColour = ProtoField.int8("rsc235.235c.skinColour", "Skin Colour")

clientBuyFromShopItemId = ProtoField.int16("rsc235.236c.itemID", "Item ID")
clientBuyFromShopShopAmount = ProtoField.int16("rsc235.236c.shopAmount", "Shop Amount")
clientBuyFromShopSellAmount = ProtoField.int16("rsc235.236c.sellAmount", "Sell Amount")

clientExignoredName = ProtoField.string("rsc235.241c.name", "Name of the Ex-Ignored")

clientDropItemSlot = ProtoField.int16("rsc235.246c.slot", "Inventory Slot Index of item to be dropped")

clientTakeGroundItemXCoord = ProtoField.int16("rsc235.247c.xCoord", "X Coordinate")
clientTakeGroundItemYCoord = ProtoField.int16("rsc235.247c.yCoord", "Y Coordinate")
clientTakeGroundItemItemID = ProtoField.int16("rsc235.247c.itemID", "Item ID")

clientCastOnGroundItemXCoord = ProtoField.int16("rsc235.249c.xCoord", "X Coordinate")
clientCastOnGroundItemYCoord = ProtoField.int16("rsc235.249c.yCoord", "Y Coordinate")
clientCastOnGroundItemItemID = ProtoField.int16("rsc235.249c.itemID", "Item ID")
clientCastOnGroundItemSpellID = ProtoField.int16("rsc235.249c.spell", "Spell to Cast")

clientPrayerOffPrayer = ProtoField.int8("rsc235.254c.prayer", "Prayer")
--------------------------------------------------------------------------------

--------------------------------------------------------------------------------
-- server opcode related variables
--------------------------------------------------------------------------------
serverLoginResponse = ProtoField.int8("rsc235.serverLoginResponse", "Login Response")

serverMetadataLoginResponse = ProtoField.int32("rsc235.12345.loginResponse", "Login Response")
serverMetadataClientVersion = ProtoField.int32("rsc235.12345.clientVersion", "Client Version")
serverMetadataAuthenticClient = ProtoField.int32("rsc235.12345.authenticClient", "Authentic Client")
serverMetadataStartTime = ProtoField.int64("rsc235.12345.startTime", "Start Time")
serverMetadataWorldNumber = ProtoField.int32("rsc235.12345.worldNumber", "World Number")
serverMetadataServerName = ProtoField.string("rsc235.12345.serverName", "Server Name")
serverMetadataUsername = ProtoField.string("rsc235.12345.userName", "Username")
serverMetadataPlayerIP = ProtoField.string("rsc235.12345.playerIP", "Player's IP Address")
serverMetadataReservedBytes = ProtoField.bytes("rsc235.12345.reserved", "Reserved Bytes (If any value other than zero, update your rscminus.lua!)")

QUEST_DISSECTOR_ABRV = {"rsc235.5.cooksAssistant", "rsc235.5.demonSlayer", "rsc235.5.doricsQuest", "rsc235.5.theRestlessGhost", "rsc235.5.goblinDiplomacy", "rsc235.5.ernestTheChicken", "rsc235.5.impCatcher", "rsc235.5.piratesTreasure", "rsc235.5.princeAliRescue", "rsc235.5.romeoJuliet", "rsc235.5.sheepShearer", "rsc235.5.shieldOfArrav", "rsc235.5.theKnightsSword", "rsc235.5.vampireSlayer", "rsc235.5.witchsPotion", "rsc235.5.dragonSlayer", "rsc235.5.witchsHouse", "rsc235.5.lostCity", "rsc235.5.herosQuest", "rsc235.5.druidicRitual", "rsc235.5.merlinsCrystal", "rsc235.5.scorpionCatcher", "rsc235.5.familyCrest", "rsc235.5.tribalTotem", "rsc235.5.fishingContest", "rsc235.5.monksFriend", "rsc235.5.templeOfIkov", "rsc235.5.clockTower", "rsc235.5.theHolyGrail", "rsc235.5.fightArena", "rsc235.5.treeGnomeVillage", "rsc235.5.theHazeelCult", "rsc235.5.sheepHerder", "rsc235.5.plagueCity", "rsc235.5.seaSlug", "rsc235.5.waterfallQuest", "rsc235.5.biohazard", "rsc235.5.junglePotion", "rsc235.5.grandTree", "rsc235.5.shiloVillage", "rsc235.5.undergroundPass", "rsc235.5.observatoryQuest", "rsc235.5.touristTrap", "rsc235.5.watchtower", "rsc235.5.dwarfCannon", "rsc235.5.murderMystery", "rsc235.5.digsite", "rsc235.5.gertrudesCat", "rsc235.5.legendsQuest" }
QUEST_DISSECTOR_ABRV[0] = "rsc235.5.blackKnightsFortress"
serverQuests = {}
for i = 0, 49 do
  serverQuests[i] = ProtoField.uint8(QUEST_DISSECTOR_ABRV[i], QUEST_NAMES[i])
end

serverUpdateStakedItemsOpponentNumberOfItemsStaked = ProtoField.uint8("rsc235.6.numberOfItemsStaked", "Number of Items Staked")
serverUpdateStakedItemsOpponentItemId = ProtoField.uint16("rsc235.6.itemID", "Item ID")
serverUpdateStakedItemsOpponentItemStack = ProtoField.int32("rsc235.6.itemStackAmount", "Amount of this Item Staked")

serverUpdateTradeAcceptance = ProtoField.uint8("rsc235.15.tradeAccepted", "Trade Accepted (Local Player Accepts)")

serverConfirmTradeRecipientName = ProtoField.string("rsc235.20.accountName", "Confirm Trade Partner's Name")
serverConfirmTradeRecipientItemCount = ProtoField.uint8("rsc235.20.toRecipientItemCount", "Number of Items Going to Recipient")
serverConfirmTradeRecipientItemID = ProtoField.uint16("rsc235.20.toRecipientItemID", "Item ID")
serverConfirmTradeRecipientItemStack = ProtoField.int32("rsc235.20.toRecipientItemStackCount", "Items in this stack")
serverConfirmTradeItemCount = ProtoField.uint8("rsc235.20.itemCount", "Number of Items Received in Trade")
serverConfirmTradeItemID = ProtoField.uint16("rsc235.20.itemID", "Item ID")
serverConfirmTradeItemStack = ProtoField.int32("rsc235.20.itemStackCount", "Items in this stack")

serverFloorSetLocalPlayerServerIndex = ProtoField.uint16("rsc235.25.localPlayerServerIndex", "Local Player Server Index (PID)")
serverFloorSetPlaneWidth = ProtoField.int16("rsc235.25.planeWidth", "Plane Width")
serverFloorSetPlaneHeight = ProtoField.int16("rsc235.25.planeHeight", "Plane Height")
serverFloorSetPlaneFloor = ProtoField.int16("rsc235.25.floor", "Current Floor")
serverFloorSetPlaneDistanceBetweenFloors = ProtoField.int16("rsc235.25.planeDistanceBetweenFloors", "Distance Between Floors")

serverSyncDuelSettingsRetreat = ProtoField.uint8("rsc235.30.retreatEnabled", "Retreat Enabled")
serverSyncDuelSettingsMagic = ProtoField.uint8("rsc235.30.magicEnabled", "Magic Enabled")
serverSyncDuelSettingsPrayer = ProtoField.uint8("rsc235.30.prayerEnabled", "Prayer Enabled")
serverSyncDuelSettingsWeapons = ProtoField.uint8("rsc235.30.weaponsEnabled", "Weapons Enabled")

serverUpdateXPSkill = ProtoField.uint8("rsc235.33.skill", "Skill")
serverUpdateXPXP = ProtoField.double("rsc235.33.xp", "XP, int/4")

serverDisplayTeleportBubbleType = ProtoField.int8("rsc235.36.type", "Bubble Type")
serverDisplayTeleportBubbleXCoord = ProtoField.uint8("rsc235.36.x", "  X Coordinate")
serverDisplayTeleportBubbleYCoord = ProtoField.uint8("rsc235.36.y", "  Y Coordinate")

serverOpenBankItemCount = ProtoField.uint8("rsc235.42.bankItemCount", "Items in Bank")
serverOpenBankMaximumItemCount = ProtoField.uint8("rsc235.42.maximumBankItems", "How Many Items Can Fit in the Bank")
serverOpenBankItemID = ProtoField.uint16("rsc235.42.itemID", "Item ID")
serverOpenBankItemsInStack = ProtoField.uint32("rsc235.42.itemsInStack", "Items in Stack")

serverObjectHandlerRemoveMode = ProtoField.uint8("rsc235.48.removeMode", "Only Removing Scenery?")
serverObjectHandlerObjectID = ProtoField.uint16("rsc235.48.sceneryId", "Scenery ID")
serverObjectHandlerXCoordinateOffset = ProtoField.int8("rsc235.48.xCoordinate", "X Coordinate of Scenery")
serverObjectHandlerYCoordinateOffset = ProtoField.int8("rsc235.48.yCoordinate", "Y Coordinate of Scenery")

serverBlockChat = ProtoField.uint8("rsc235.51.serverBlockChat", "Block Chat Messages")
serverBlockPrivate = ProtoField.uint8("rsc235.51.serverBlockPrivate", "Block Private Messages")
serverBlockTrade = ProtoField.uint8("rsc235.51.serverBlockTrade", "Block Trade Requests")
serverBlockDuel = ProtoField.uint8("rsc235.51.serverBlockDuel", "Block Duel Requests")

serverSystemUpdateTimer = ProtoField.uint16("rsc235.52.systemUpdateTimer", "System Update Timer")

serverInventoryItemsCount = ProtoField.uint8("rsc235.53.inventoryItemCount","Inventory Item Count")
serverItemId = ProtoField.uint16("rsc235.53.itemID", "Item ID")
serverItemCount = ProtoField.uint32("rsc235.53.itemAmount", "Amount of Item in Stack")
serverItemEquipped = ProtoField.uint8("rsc235.53.equipped","Equipped")

serverNPCCoordsCount = ProtoField.uint8("rsc235.79.npcCount", "NPC Count")
serverNPCCoordsReqUpdate = ProtoField.bool("rsc235.79.requireUpdate", "Update Required")
serverNPCCoordsUpdateType = ProtoField.bool("rsc235.79.updateType", "Stationary?")
serverNPCCoordsAnimationUpdate = ProtoField.uint8("rsc235.79.animationUpdate", "Animation")
serverNPCCoordsServerIndex = ProtoField.uint16("rsc235.79.serverIndex", "Server Index")
serverNPCCoordsXCoordinate = ProtoField.int32("rsc235.79.xCoordinate", "X Coordinate (sent as offset)")
serverNPCCoordsYCoordinate = ProtoField.int32("rsc235.79.yCoordinate", "Y Coordinate (sent as offset)")
serverNPCCoordsAnimation = ProtoField.uint8("rsc235.79.animation", "Animation")
serverNPCCoordsNPCID = ProtoField.uint16("rsc235.79.npcId", "NPC ID")

serverSendPrivateMessageRecipient = ProtoField.string("rsc235.87.recipient", "Recipient")
serverSendPrivateMessageLength = ProtoField.uint16("rsc235.87.messageLength", "Message Length")
serverSendPrivateMessageMessage = ProtoField.string("rsc235.87.message", "Private Message (Scrambled)")

serverShowDialogueServerMessageTopMessage = ProtoField.string("rsc235.89.message", "Message")

serverSetInventorySlotSlot = ProtoField.uint8("rsc235.90.slot", "Slot Number")
serverSetInventorySlotItemID = ProtoField.uint16("rsc235.90.itemID", "Item ID")
serverSetInventorySlotItemEquipped = ProtoField.bool("rsc235.90.equipped", "Item Equipped")
serverSetInventorySlotItemStackAmount = ProtoField.int32("rsc235.90.itemAmount", "Amount of Item in Stack")

serverBoundaryHandlerRemoveMode = ProtoField.uint8("rsc235.91.removeMode", "Only Removing a Boundary?")
serverBoundaryHandlerObjectID = ProtoField.uint16("rsc235.91.boundaryId", "Boundary ID")
serverBoundaryHandlerXCoordinateOffset = ProtoField.int8("rsc235.91.xCoordinate", "X Coordinate of Boundary")
serverBoundaryHandlerYCoordinateOffset = ProtoField.int8("rsc235.91.yCoordinate", "Y Coordinate of Boundary")
serverBoundaryHandlerAlignment = ProtoField.int8("rsc235.91.alignment", "Alignment")

serverInitiateTradePID = ProtoField.uint16("rsc235.92.pid", "PID of User to Trade With")
serverInitiateTradeUsername = ProtoField.string("rsc235.92.username", "Username (from opcode 234 type 5)")

serverUpdateItemsTradedToYouItemCount = ProtoField.int8("rsc235.97.itemCount", "Number of Item Slots Occupied")
serverUpdateItemsTradedToYouItemID = ProtoField.int16("rsc235.97.itemID", "Item")
serverUpdateItemsTradedToYouItemAmount = ProtoField.int32("rsc235.97.itemAmount", "Amount of Item in Stack")

serverGroundItemHandlerRemoveMode = ProtoField.uint8("rsc235.91.removeMode", "Only Removing a Ground Item?")
serverGroundItemHandlerItemID = ProtoField.uint16("rsc235.99.itemID", "Ground Item ID")
serverGroundItemHandlerXCoordinateOffset = ProtoField.int8("rsc235.99.xCoordinate", "X Coordinate of Ground Item")
serverGroundItemHandlerYCoordinateOffset = ProtoField.int8("rsc235.99.yCoordinate", "Y Coordinate of Ground Item")

serverShowShopItemCount = ProtoField.uint8("rsc235.101.itemCount", "Number of Items in Shop")
serverShowShopType = ProtoField.uint8("rsc235.101.shopType", "Shop Type")
serverShowShopSellPriceGenerosity = ProtoField.uint8("rsc235.101.sellPriceGenerosity", "Sell Price Generosity") --  (divides by 100 and multiply by the base item price)
serverShowShopBuyPriceGenerosity = ProtoField.uint8("rsc235.101.buyPriceGenerosity", "Buy Price Generosity") -- (divides by 100 and multiply by the base item price)
serverShowShopStockEffect = ProtoField.uint8("rsc235.101.stockEffectDegree", "How Much Being Over/Under-Stocked Affects Buy/Sell Prices")
serverShowShopItemID = ProtoField.uint16("rsc235.101.itemID", "Item ID")
serverShowShopItemStackAmount = ProtoField.uint16("rsc235.101.amountInStock", "Amount in Stock")
serverShowShopBaseAmountInStock = ProtoField.uint16("rsc235.101.baseAmountInStock", "Base Amount in Stock")

serverUpdateNPCNPCCount = ProtoField.uint8("rsc235.104.npcCount", "NPC Count")
serverUpdateNPCNPCServerIndex = ProtoField.uint16("rsc235.104.npcServerIndex", "NPC Server Index")      
serverUpdateNPCUpdateType = ProtoField.uint8("rsc235.104.updateType", "Update Type")
serverUpdateNPCDamageTaken = ProtoField.uint8("rsc235.104.damageTaken", "Damage Taken (by NPC)")
serverUpdateNPCCurrentHP = ProtoField.uint8("rsc235.104.currentHP", "NPC's HP")
serverUpdateNPCMaxHP = ProtoField.uint8("rsc235.104.maxHP", "NPC's Max HP")
serverUpdateNPCPID = ProtoField.uint8("rsc235.104.pidTalkingTo", "PID of Player NPC is Talking To")
serverUpdateNPCMessageLength = ProtoField.uint8("rsc235.104.messageLength", "Message Length")
serverUpdateNPCMessage = ProtoField.string("rsc235.104.message", "Message (Scrambled)")          

serverSetIgnoreListCount = ProtoField.int8("rsc235.109.ignoreListCount", "Number of People on Ignore List")
serverSetIgnoreListName = ProtoField.string("rsc235.109.name", "Name")
serverSetIgnoreListAccountName = ProtoField.string("rsc235.109.accountName", "Account Name")
serverSetIgnoreListOldName = ProtoField.string("rsc235.109.oldName", "Old Name")
serverSetIgnoreListServers = ProtoField.string("rsc235.109.oldName2", "Old Name (Duplicate)") -- FINDOUT: why did Jagex do this? icegold.pcap has this field filled a few times.

serverJustCompletedTutorial = ProtoField.uint8("rsc235.111.justCompletedTutorial", "Completed Tutorial")

serverSetFatigue = ProtoField.double("rsc235.114.fatigue", "Fatigue")

serverSleepWordImage = ProtoField.bytes("rsc235.117.sleepWordImageData", "Sleep Word Image Data (can be converted with rscminus)") -- TODO, could convert to ascii-art since it's just a black and white image

serverReceivePMSender = ProtoField.string("rsc235.120.sender", "Sender")
serverReceivePMSender2 = ProtoField.string("rsc235.120.sender2", "Sender (Duplicate)")
serverReceivePMModStatus = ProtoField.int8("rsc235.120.modStatus", "Moderator Status")
serverReceivePMMessageID = ProtoField.uint64("rsc235.120.messageID", "Packed Message ID")
serverReceivePMMessageIDWorld = ProtoField.int16("rsc235.120.messageID.world", "World the Message was Sent From")
serverReceivePMMessageIDID = ProtoField.int24("rsc235.120.messageID.ID", "Message ID")
serverReceivePMMessageLength = ProtoField.uint16("rsc235.120.messageLength", "Message Length")
serverReceivePMMessage = ProtoField.string("rsc235.120.message", "Private Message (Scrambled)")

serverRemoveSlot = ProtoField.uint8("rsc235.123.removeInventorySlot", "Item In Inventory to Remove")

serverSendMessageType = ProtoField.uint8("rsc235.131.messageType", "Message Type")
serverSendMessageInfoContained = ProtoField.uint8("rsc235.131.infoContained", "Info Contained")
serverSendMessageMessage = ProtoField.string("rsc235.131.message", "Message")
serverSendMessageSender = ProtoField.string("rsc235.131.sender", "Sender")
serverSendMessageSender2 = ProtoField.string("rsc235.131.sender2", "Sender (Always Duplicate)")
serverSendMessageColour = ProtoField.string("rsc235.131.colour", "Colour")

serverUpdateFriendFriendName = ProtoField.string("rsc235.149.friendName", "Friend Name")
serverUpdateFriendOldFriendName = ProtoField.string("rsc235.149.oldFriendName", "Old Friend Name")
serverUpdateFriendOnlineStatus = ProtoField.uint8("rsc235.149.onlineStatus", "Online Status")
serverUpdateFriendFriendServer = ProtoField.string("rsc235.149.friendServer", "Friend's Server")

serverSetEquipmentStatsArmour = ProtoField.uint8("rsc235.153.armour", "Armour Bonus")
serverSetEquipmentStatsWeaponAim = ProtoField.uint8("rsc235.153.weaponAim", "Weapon Aim")
serverSetEquipmentStatsWeaponPower = ProtoField.uint8("rsc235.153.weaponPower", "Weapon Power")
serverSetEquipmentStatsMagic = ProtoField.uint8("rsc235.153.magic", "Magic Bonus")
serverSetEquipmentStatsPrayer = ProtoField.uint8("rsc235.153.prayer", "Prayer Bonus")

serverSetStatsPlayerCurrentStat = {}
serverSetStatsPlayerBaseStat = {}
serverSetStatsPlayerXP = {}
for i = 0, 17 do
  local lowerCaseSkill = string.lower(SKILL_NAMES[i])
  serverSetStatsPlayerCurrentStat[i] = ProtoField.uint8("rsc235.156.currentStat." .. lowerCaseSkill, "Effective " .. SKILL_NAMES[i] .. " Level")
  serverSetStatsPlayerBaseStat[i] = ProtoField.uint8("rsc235.156.baseStat." .. lowerCaseSkill, "Base " .. SKILL_NAMES[i] .. " Level")
  serverSetStatsPlayerXP[i] = ProtoField.double("rsc235.156.xp." .. lowerCaseSkill, SKILL_NAMES[i] .. " XP")
end
serverSetStatsPlayerQuestPoints = ProtoField.uint8("rsc235.156.questPoints", "Quest Points")

serverUpdateStatSkill = ProtoField.uint8("rsc235.159.skill", "Skill")
serverUpdateStatPlayerCurrentStat = ProtoField.uint8("rsc235.159.currentStat", "Effective Level")
serverUpdateStatPlayerBaseStat = ProtoField.uint8("rsc235.159.baseStat", "Base Level")
serverUpdateStatExperience = ProtoField.double("rsc235.159.xp", "Experience Points")

serverUpdateTradeRecipientAcceptance = ProtoField.uint8("rsc235.162.tradeAccepted", "Trade Accepted")

serverShowConfirmDuelOpponentName = ProtoField.string("rsc235.172.opponentName", "Opponent Name")
serverShowConfirmDuelOpponentItemCount = ProtoField.uint8("rsc235.172.opponentItemCount", "Number of Items Staked by Opponent")
serverShowConfirmDuelOpponentItemID = ProtoField.uint16("rsc235.172.opponentItemId", "Item ID of Item Staked by Opponent")
serverShowConfirmDuelOpponentItemStackAmount = ProtoField.uint32("rsc235.172.opponentItemIdStackamount", "Amount of Item Staked by Opponent")
serverShowConfirmDuelItemCount = ProtoField.uint8("rsc235.172.itemCount", "Number of Items Staked by Player")
serverShowConfirmDuelItemID = ProtoField.uint16("rsc235.172.itemID", "Item ID of Item Staked by Player")
serverShowConfirmDuelItemStackAmount = ProtoField.uint32("rsc235.172.itemIdStackamount", "Amount of Item Staked")
serverShowConfirmDuelRetreat = ProtoField.uint8("rsc235.172.retreat", "Allowed to Retreat")
serverShowConfirmDuelMagic = ProtoField.uint8("rsc235.172.magic", "Magic Restricted")
serverShowConfirmDuelPrayer = ProtoField.uint8("rsc235.172.prayer", "Prayer Restricted")
serverShowConfirmDuelWeapons = ProtoField.uint8("rsc235.172.weapons",  "Weapons Restricted")

serverShowDialogueDuelPID = ProtoField.uint16("rsc235.176.pid", "PID of User to Duel With")
serverShowDialogueDuelUsername = ProtoField.string("rsc235.176.username", "Username (from opcode 234 type 5)")

serverShowWelcomeIP = ProtoField.string("rsc235.182.IPAddress", "Last Logged In IP (almost always replaced by RSC+)")
serverShowWelcomeDays = ProtoField.uint16("rsc235.182.daysAgo", "Days Since Log In")
serverShowWelcomeRecoverySetDays = ProtoField.uint8("rsc235.182.recoverySetDays", "Days Since Recovery Questions Set")
serverShowWelcomeUnreadMessages = ProtoField.uint16("rsc235.182.unreadMessages", "Unread Message Count")

serverPlayerCoordsLocalRegionX = ProtoField.uint16("rsc235.191.thisLocalRegionX", "Local Region X coordinate")
serverPlayerCoordsLocalRegionY = ProtoField.uint16("rsc235.191.thisLocalRegionY", "Local Region Y coordinate")
serverPlayerCoordsAnimation = ProtoField.uint8("rsc235.191.animation", "Animation")
serverPlayerCoordsPlayerCount = ProtoField.uint8("rsc235.191.playerCount", "Player Count")
serverPlayerCoordsAnimationUpdateRequired = ProtoField.bool("rsc235.191.animationUpdateRequired", "Animation Update Required")
-- serverPlayerCoordsUpdateType =  ProtoField.bool("rsc235.191.updateType", "Moving Animation Update, or Stationary Animation Update") TODO
serverPlayerCoordsAnimationUpdate = ProtoField.uint8("rsc235.191.animationUpdate", "Animation Update")
serverPlayerCoordsNewPlayerPID = ProtoField.uint16("rsc235.191.newPlayerServerIndex", "New Player's PID")
serverPlayerCoordsNewPlayerX = ProtoField.uint16("rsc235.191.newPlayerXCoordinate", "New Player's X coordinate")
serverPlayerCoordsNewPlayerY = ProtoField.uint16("rsc235.191.newPlayerYCoordinate", "New Player's Y coordinate")
serverPlayerCoordsNewPlayerAnimation = ProtoField.uint8("rsc235.191.newPlayerAnimation", "New Player's Animation State")

serverSoundName = ProtoField.string("rsc235.204.soundName", "Sound File Name")

serverSetPrayersThickSkin = ProtoField.uint8("rsc235.206.thickSkin", PRAYER_NAMES[0])
serverSetPrayersBurstOfStrength = ProtoField.uint8("rsc235.206.burstOfStrength", PRAYER_NAMES[1])
serverSetPrayersClarityOfThought = ProtoField.uint8("rsc235.206.clarityOfThought", PRAYER_NAMES[2])
serverSetPrayersRockSkin = ProtoField.uint8("rsc235.206.rockSkin", PRAYER_NAMES[3])
serverSetPrayersSuperhumanStrength = ProtoField.uint8("rsc235.206.superhumanStrength", PRAYER_NAMES[4])
serverSetPrayersImprovedReflexes = ProtoField.uint8("rsc235.206.improvedReflexes", PRAYER_NAMES[5])
serverSetPrayersRapidRestore = ProtoField.uint8("rsc235.206.rapidRestore", PRAYER_NAMES[6])
serverSetPrayersRapidHeal = ProtoField.uint8("rsc235.206.rapidHeal", PRAYER_NAMES[7])
serverSetPrayersProtectItems = ProtoField.uint8("rsc235.206.protectItems", PRAYER_NAMES[8])
serverSetPrayersSteelSkin = ProtoField.uint8("rsc235.206.steelSkin", PRAYER_NAMES[9])
serverSetPrayersUltimateStrength = ProtoField.uint8("rsc235.206.ultimateStrength", PRAYER_NAMES[10])
serverSetPrayersIncredibleReflexes = ProtoField.uint8("rsc235.206.incredibleReflexes", PRAYER_NAMES[11])
serverSetPrayersParalyzeMonster = ProtoField.uint8("rsc235.206.paralyzeMonster", PRAYER_NAMES[12])
serverSetPrayersProtectFromMissiles = ProtoField.uint8("rsc235.206.protectFromMissiles", PRAYER_NAMES[13])

serverUpdateAcceptedDuelBool = ProtoField.uint8("rsc235.210.accepted", "Accepted Duel")

serverRemoveWorldEntityXCoordinate = ProtoField.int16("rsc235.211.xCoordinate", "X Coordinate")
serverRemoveWorldEntityYCoordinate = ProtoField.int16("rsc235.211.yCoordinate", "Y Coordinate")

serverShowDialogueServerMessageNotTopMessage = ProtoField.string("rsc235.222.message", "Message")

serverPlayerUpdatePlayerCount = ProtoField.uint8("rsc235.234.playerCount", "Player Count")
serverPlayerUpdatePID = ProtoField.uint16("rsc235.234.pid", "PID")
serverPlayerUpdateUpdateType = ProtoField.uint8("rsc235.234.updateType", "Update Type")
serverPlayerUpdateUpdateType0bubbleItem = ProtoField.uint16("rsc235.234.updateType.0.bubbleItem", "Bubble Item")
serverPlayerUpdateUpdateType1modStatus = ProtoField.int8("rsc235.234.updateType.1.modStatus", "Mod Status")
serverPlayerUpdateUpdateType1chatLength = ProtoField.uint16("rsc235.234.updateType.1.chatLength", "Message Length")
serverPlayerUpdateUpdateType1chatMessage = ProtoField.string("rsc235.234.updateType.1.chatMessage", "Chat Message (Scrambled)")
serverPlayerUpdateUpdateType2damage = ProtoField.uint8("rsc235.234.updateType.2.damage", "Damage Taken")
serverPlayerUpdateUpdateType2currentHp = ProtoField.uint8("rsc235.234.updateType.2.currentHP", "Current HP")
serverPlayerUpdateUpdateType2maxHp = ProtoField.uint8("rsc235.234.updateType.2.maxHP", "Max HP")
serverPlayerUpdateUpdateType3sprite = ProtoField.uint16("rsc235.234.updateType.3.sprite", "Sprite")
serverPlayerUpdateUpdateType3shooterServerIndex = ProtoField.uint16("rsc235.234.updateType.3.shooterServerIndex", "Shooter Server Index")
serverPlayerUpdateUpdateType4sprite = ProtoField.uint16("rsc235.234.updateType.4.sprite", "Sprite")
serverPlayerUpdateUpdateType4shooterServerIndex = ProtoField.uint16("rsc235.234.updateType.4.shooterServerIndex", "Shooter Server Index")
serverPlayerUpdateUpdateType5serverIndex = ProtoField.uint16("rsc235.234.updateType.5.serverIndex", "Server Index (unused)")
serverPlayerUpdateUpdateType5username1 = ProtoField.string("rsc235.234.updateType.5.displayName", "Display Name")
serverPlayerUpdateUpdateType5username2 = ProtoField.string("rsc235.234.updateType.5.accountName", "Account Name")
serverPlayerUpdateUpdateType5equipCount = ProtoField.uint8("rsc235.234.updateType.5.equipCount", "Equipment Count")
serverPlayerUpdateUpdateType5equipment = ProtoField.bytes("rsc235.234.updateType.5.equipment", "Equipment")
serverPlayerUpdateUpdateType5hairColor = ProtoField.uint8("rsc235.234.updateType.5.hairColour", "Hair Colour")
serverPlayerUpdateUpdateType5topColor = ProtoField.uint8("rsc235.234.updateType.5.topColour", "Top Colour")
serverPlayerUpdateUpdateType5bottomColor = ProtoField.uint8("rsc235.234.updateType.5.pantsColour", "Pants Colour")
serverPlayerUpdateUpdateType5skinColor = ProtoField.uint8("rsc235.234.updateType.5.skinColour", "Skin Colour")
serverPlayerUpdateUpdateType5level = ProtoField.uint8("rsc235.234.updateType.5.level", "Combat Level")
serverPlayerUpdateUpdateType5skull = ProtoField.uint8("rsc235.234.updateType.5.skull", "Skull")
serverPlayerUpdateUpdateType6chatLength = ProtoField.uint16("rsc235.234.updateType.6.chatLength", "Message Length")
serverPlayerUpdateUpdateType6chatMessage = ProtoField.string("rsc235.234.updateType.6.chatMessage", "Chat Message (Scrambled)")

serverUpdateIgnoreBecauseOfNameChangeIgnoreListName = ProtoField.string("rsc235.237.name", "Name")
serverUpdateIgnoreBecauseOfNameChangeIgnoreListAccountName = ProtoField.string("rsc235.237.accountName", "Account Name")
serverUpdateIgnoreBecauseOfNameChangeIgnoreListOldName = ProtoField.string("rsc235.237.oldName", "Old Name")
serverUpdateIgnoreBecauseOfNameChangeIgnoreListServers = ProtoField.string("rsc235.237.oldName2", "Old Name (Duplicate)") -- TODO, confirm this is just old name duplicate
serverUpdateIgnoreBecauseOfNameChangeAttemptUpdatingExistingEntry = ProtoField.bool("rsc235.237.attemptUpdatingExistingEntry", "Attempt to Update an Existing Name on the Ignore List") -- always false in 10,000+ replays

serverCameraModeAuto = ProtoField.uint8("rsc235.240.serverCameraModeAuto", "Camera Angle Mode")
serverMouseButtonOne = ProtoField.uint8("rsc235.240.serverMouseButtonOne", "Mouse Buttons")
serverSoundDisabled = ProtoField.uint8("rsc235.240.serverSoundDisabled", "Sound Effects")

serverSetFatigueSleepingFatigueSleeping = ProtoField.uint16("rsc235.244.fatigueSleeping", "Displayed Fatigue While Asleep")

serverShowDialogueMenuCount = ProtoField.uint8("rsc235.245.optionCount", "Number of Options")
serverShowDialogueMenuString = ProtoField.string("rsc235.245.optionText", "Option Text")

serverUpdateBankItemDisplaySlot = ProtoField.uint8("rsc235.249.slot", "Slot Number")
serverUpdateBankItemDisplayItemID = ProtoField.uint16("rsc235.249.itemID", "Item ID")
serverUpdateBankItemDisplayItemCount = ProtoField.uint16("rsc235.249.itemCount", "Amount in New Stack")

serverUpdateDuelOpponentAccepted = ProtoField.uint8("rsc235.253.duelOpponentAccepted", "Opponent Accepted Duel")

--------------------------------------------------------------------------------

rsc235_protocol.fields = {
  -- common
  clientPacket,
  clientOpcode,
  clientPacketLength,

  -- 10000 (rscminus virtual opcode)
  serverLoginResponse,
  
  -- 12345 (server-sided metadata field)
  serverMetadataLoginResponse,
  serverMetadataClientVersion,
  serverMetadataAuthenticClient,
  serverMetadataStartTime,
  serverMetadataWorldNumber,
  serverMetadataServerName,
  serverMetadataUsername,
  serverMetadataPlayerIP,
  serverMetadataReservedBytes,

  -- 5
  serverQuests[0], serverQuests[1], serverQuests[2], serverQuests[3], serverQuests[4], serverQuests[5], serverQuests[6],
  serverQuests[7], serverQuests[8], serverQuests[9], serverQuests[10], serverQuests[11], serverQuests[12], serverQuests[13],
  serverQuests[14], serverQuests[15], serverQuests[16], serverQuests[17], serverQuests[18], serverQuests[19], serverQuests[20],
  serverQuests[21], serverQuests[22], serverQuests[23], serverQuests[24], serverQuests[25], serverQuests[26], serverQuests[27],
  serverQuests[28], serverQuests[29], serverQuests[30], serverQuests[31], serverQuests[32], serverQuests[33], serverQuests[34],
  serverQuests[35], serverQuests[36], serverQuests[37], serverQuests[38], serverQuests[39], serverQuests[40], serverQuests[41],
  serverQuests[42], serverQuests[43], serverQuests[44], serverQuests[45], serverQuests[46], serverQuests[47], serverQuests[48],
  serverQuests[49],

  -- 6
  serverUpdateStakedItemsOpponentNumberOfItemsStaked,
  serverUpdateStakedItemsOpponentItemId,
  serverUpdateStakedItemsOpponentItemStack,

  -- 15
  serverUpdateTradeAcceptance,

  -- 20
  serverConfirmTradeRecipientName,
  serverConfirmTradeRecipientItemCount,
  serverConfirmTradeRecipientItemID,
  serverConfirmTradeRecipientItemStack,
  serverConfirmTradeItemCount,
  serverConfirmTradeItemID,
  serverConfirmTradeItemStack,

  -- 25
  serverFloorSetLocalPlayerServerIndex,
  serverFloorSetPlaneWidth,
  serverFloorSetPlaneHeight,
  serverFloorSetPlaneIndex,
  serverFloorSetPlaneFloor,
  serverFloorSetPlaneDistanceBetweenFloors,

  -- 30
  serverSyncDuelSettingsRetreat,
  serverSyncDuelSettingsMagic,
  serverSyncDuelSettingsPrayer,
  serverSyncDuelSettingsWeapons,

  -- 33
  serverUpdateXPSkill,
  serverUpdateXPXP,

  -- 36
  serverDisplayTeleportBubbleType,
  serverDisplayTeleportBubbleXCoord,
  serverDisplayTeleportBubbleYCoord,

  -- 42
  serverOpenBankItemCount,
  serverOpenBankMaximumItemCount,
  serverOpenBankItemID,
  serverOpenBankItemsInStack,

  -- 48
  serverObjectHandlerRemoveMode,
  serverObjectHandlerObjectID,
  serverObjectHandlerXCoordinateOffset,
  serverObjectHandlerYCoordinateOffset,

  -- 51
  serverBlockChat,
  serverBlockPrivate,
  serverBlockTrade,
  serverBlockDuel,

  -- 52
  serverSystemUpdateTimer,

  -- 53
  serverInventoryItemsCount,
  serverItemId,
  serverItemCount,
  serverItemEquipped,

  -- 79
  serverNPCCoordsCount,
  serverNPCCoordsReqUpdate,
  serverNPCCoordsUpdateType,
  serverNPCCoordsAnimationUpdate,
  serverNPCCoordsServerIndex,
  serverNPCCoordsXCoordinate,
  serverNPCCoordsYCoordinate,
  serverNPCCoordsAnimation,
  serverNPCCoordsNPCID,

  -- 87
  serverSendPrivateMessageRecipient,
  serverSendPrivateMessageLength,
  serverSendPrivateMessageMessage,

  -- 89
  serverShowDialogueServerMessageTopMessage,

  -- 90
  serverSetInventorySlotSlot,
  serverSetInventorySlotItemID,
  serverSetInventorySlotItemEquipped,
  serverSetInventorySlotItemStackAmount,

  -- 91
  serverBoundaryHandlerRemoveMode,
  serverBoundaryHandlerObjectID,
  serverBoundaryHandlerXCoordinateOffset,
  serverBoundaryHandlerYCoordinateOffset,
  serverBoundaryHandlerAlignment,

  -- 92
  serverInitiateTradePID,
  serverInitiateTradeUsername,

  -- 97
  serverUpdateItemsTradedToYouItemCount,
  serverUpdateItemsTradedToYouItemID,
  serverUpdateItemsTradedToYouItemAmount,

  -- 99
  serverGroundItemHandlerRemoveMode,
  serverGroundItemHandlerItemID,
  serverGroundItemHandlerXCoordinateOffset,
  serverGroundItemHandlerYCoordinateOffset,

  -- 101
  serverShowShopItemCount,
  serverShowShopType,
  serverShowShopSellPriceGenerosity,
  serverShowShopBuyPriceGenerosity,
  serverShowShopStockEffect,
  serverShowShopItemID,
  serverShowShopItemStackAmount,
  serverShowShopBaseAmountInStock,

  -- 104
  serverUpdateNPCNPCCount,
  serverUpdateNPCNPCServerIndex,
  serverUpdateNPCUpdateType,
  serverUpdateNPCDamageTaken,
  serverUpdateNPCCurrentHP,
  serverUpdateNPCMaxHP,
  serverUpdateNPCPID,
  serverUpdateNPCMessageLength,
  serverUpdateNPCMessage,

  -- 109
  serverSetIgnoreListCount,
  serverSetIgnoreListName,
  serverSetIgnoreListAccountName,
  serverSetIgnoreListOldName,
  serverSetIgnoreListServers,

  -- 111
  serverJustCompletedTutorial,

  -- 114
  serverSetFatigue,

  -- 117
  serverSleepWordImage,

  -- 120
  serverReceivePMSender,
  serverReceivePMSender2,
  serverReceivePMModStatus,
  serverReceivePMMessageID,
  serverReceivePMMessageIDWorld,
  serverReceivePMMessageIDID,
  serverReceivePMMessageLength,
  serverReceivePMMessage,

  -- 123
  serverRemoveSlot,

  -- 131
  serverSendMessageType,
  serverSendMessageInfoContained,
  serverSendMessageMessage,
  serverSendMessageSender,
  serverSendMessageSender2,
  serverSendMessageColour,

  -- 149
  serverUpdateFriendFriendName,
  serverUpdateFriendOldFriendName,
  serverUpdateFriendOnlineStatus,
  serverUpdateFriendFriendServer,

  -- 153
  serverSetEquipmentStatsArmour,
  serverSetEquipmentStatsWeaponAim,
  serverSetEquipmentStatsWeaponPower,
  serverSetEquipmentStatsMagic,
  serverSetEquipmentStatsPrayer,

  -- 156
  serverSetStatsPlayerCurrentStat[0], serverSetStatsPlayerBaseStat[0], serverSetStatsPlayerXP[0],
  serverSetStatsPlayerCurrentStat[1], serverSetStatsPlayerBaseStat[1], serverSetStatsPlayerXP[1],
  serverSetStatsPlayerCurrentStat[2], serverSetStatsPlayerBaseStat[2], serverSetStatsPlayerXP[2],
  serverSetStatsPlayerCurrentStat[3], serverSetStatsPlayerBaseStat[3], serverSetStatsPlayerXP[3],
  serverSetStatsPlayerCurrentStat[4], serverSetStatsPlayerBaseStat[4], serverSetStatsPlayerXP[4],
  serverSetStatsPlayerCurrentStat[5], serverSetStatsPlayerBaseStat[5], serverSetStatsPlayerXP[5],
  serverSetStatsPlayerCurrentStat[6], serverSetStatsPlayerBaseStat[6], serverSetStatsPlayerXP[6],
  serverSetStatsPlayerCurrentStat[7], serverSetStatsPlayerBaseStat[7], serverSetStatsPlayerXP[7],
  serverSetStatsPlayerCurrentStat[8], serverSetStatsPlayerBaseStat[8], serverSetStatsPlayerXP[8],
  serverSetStatsPlayerCurrentStat[9], serverSetStatsPlayerBaseStat[9], serverSetStatsPlayerXP[9],
  serverSetStatsPlayerCurrentStat[10], serverSetStatsPlayerBaseStat[10], serverSetStatsPlayerXP[10],
  serverSetStatsPlayerCurrentStat[11], serverSetStatsPlayerBaseStat[11], serverSetStatsPlayerXP[11],
  serverSetStatsPlayerCurrentStat[12], serverSetStatsPlayerBaseStat[12], serverSetStatsPlayerXP[12],
  serverSetStatsPlayerCurrentStat[13], serverSetStatsPlayerBaseStat[13], serverSetStatsPlayerXP[13],
  serverSetStatsPlayerCurrentStat[14], serverSetStatsPlayerBaseStat[14], serverSetStatsPlayerXP[14],
  serverSetStatsPlayerCurrentStat[15], serverSetStatsPlayerBaseStat[15], serverSetStatsPlayerXP[15],
  serverSetStatsPlayerCurrentStat[16], serverSetStatsPlayerBaseStat[16], serverSetStatsPlayerXP[16],
  serverSetStatsPlayerCurrentStat[17], serverSetStatsPlayerBaseStat[17], serverSetStatsPlayerXP[17],
  serverSetStatsPlayerQuestPoints,

  -- 159
  serverUpdateStatSkill,
  serverUpdateStatPlayerCurrentStat,
  serverUpdateStatPlayerBaseStat,
  serverUpdateStatExperience,

  -- 162
  serverUpdateTradeRecipientAcceptance,

  -- 172
  serverShowConfirmDuelOpponentName,
  serverShowConfirmDuelOpponentItemCount,
  serverShowConfirmDuelOpponentItemID,
  serverShowConfirmDuelOpponentItemStackAmount,
  serverShowConfirmDuelItemCount,
  serverShowConfirmDuelItemID,
  serverShowConfirmDuelItemStackAmount,
  serverShowConfirmDuelRetreat,
  serverShowConfirmDuelMagic,
  serverShowConfirmDuelPrayer,
  serverShowConfirmDuelWeapons,

  -- 176
  serverShowDialogueDuelPID,
  serverShowDialogueDuelUsername, 

  -- 182
  serverShowWelcomeIP,
  serverShowWelcomeDays,
  serverShowWelcomeRecoverySetDays,
  serverShowWelcomeUnreadMessages,

  -- 191
  serverPlayerCoordsLocalRegionX,
  serverPlayerCoordsLocalRegionY,
  serverPlayerCoordsAnimation,
  serverPlayerCoordsPlayerCount,
  serverPlayerCoordsAnimationUpdateRequired,
  serverPlayerCoordsAnimationUpdate,
  serverPlayerCoordsNewPlayerPID,
  serverPlayerCoordsNewPlayerX,
  serverPlayerCoordsNewPlayerY,
  serverPlayerCoordsNewPlayerAnimation,

  -- 204
  serverSoundName,

  -- 206
  serverSetPrayersThickSkin,
  serverSetPrayersBurstOfStrength,
  serverSetPrayersClarityOfThought,
  serverSetPrayersRockSkin,
  serverSetPrayersSuperhumanStrength,
  serverSetPrayersImprovedReflexes,
  serverSetPrayersRapidRestore,
  serverSetPrayersRapidHeal,
  serverSetPrayersProtectItems,
  serverSetPrayersSteelSkin,
  serverSetPrayersUltimateStrength,
  serverSetPrayersIncredibleReflexes,
  serverSetPrayersParalyzeMonster,
  serverSetPrayersProtectFromMissiles,

  -- 210
  serverUpdateAcceptedDuelBool,

  -- 211
  serverRemoveWorldEntityXCoordinate,
  serverRemoveWorldEntityYCoordinate,

  -- 222
  serverShowDialogueServerMessageNotTopMessage,

  -- 234
  serverPlayerUpdatePlayerCount,
  serverPlayerUpdatePID,
  serverPlayerUpdateUpdateType,
  serverPlayerUpdateUpdateType0bubbleItem,
  serverPlayerUpdateUpdateType1modStatus,
  serverPlayerUpdateUpdateType1chatLength,
  serverPlayerUpdateUpdateType1chatMessage,
  serverPlayerUpdateUpdateType2damage,
  serverPlayerUpdateUpdateType2currentHp,
  serverPlayerUpdateUpdateType2maxHp,
  serverPlayerUpdateUpdateType3sprite,
  serverPlayerUpdateUpdateType3shooterServerIndex,
  serverPlayerUpdateUpdateType4sprite,
  serverPlayerUpdateUpdateType4shooterServerIndex,
  serverPlayerUpdateUpdateType5serverIndex,
  serverPlayerUpdateUpdateType5username1,
  serverPlayerUpdateUpdateType5username2,
  serverPlayerUpdateUpdateType5equipCount,
  serverPlayerUpdateUpdateType5equipment,
  serverPlayerUpdateUpdateType5hairColor,
  serverPlayerUpdateUpdateType5topColor,
  serverPlayerUpdateUpdateType5bottomColor,
  serverPlayerUpdateUpdateType5skinColor,
  serverPlayerUpdateUpdateType5level,
  serverPlayerUpdateUpdateType5skull,
  serverPlayerUpdateUpdateType5unused,
  serverPlayerUpdateUpdateType6chatLength,
  serverPlayerUpdateUpdateType6chatMessage,

  -- 237
  serverUpdateIgnoreBecauseOfNameChangeIgnoreListName,
  serverUpdateIgnoreBecauseOfNameChangeIgnoreListAccountName,
  serverUpdateIgnoreBecauseOfNameChangeIgnoreListOldName,
  serverUpdateIgnoreBecauseOfNameChangeIgnoreListServers,
  serverUpdateIgnoreBecauseOfNameChangeAttemptUpdatingExistingEntry,

  -- 240
  serverCameraModeAuto,
  serverMouseButtonOne,
  serverSoundDisabled,

  -- 244
  serverSetFatigueSleepingFatigueSleeping,

  -- 245
  serverShowDialogueMenuCount,
  serverShowDialogueMenuString,

  -- 249
  serverUpdateBankItemDisplaySlot,
  serverUpdateBankItemDisplayItemID,
  serverUpdateBankItemDisplayItemCount,

  -- 253
  serverUpdateDuelOpponentAccepted,
    
  -- 0c
  clientConnectReconnecting,
  clientConnectVersion,
  clientConnectEncrypted,
  
  -- 4c
  clientCastOnInventoryInventoryIndex,
  clientCastOnInventorySpellID,
  
  -- 8c
  clientSendDuelSettingsRetreat,
  clientSendDuelSettingsMagic,
  clientSendDuelSettingsPrayer,
  clientSendDuelSettingsWeapons,
  
  -- 14c
  clientInteractWithBoundaryXCoord,
  clientInteractWithBoundaryYCoord,
  clientInteractWithBoundaryAlignment,
  
  -- 16c
  clientWalkAndPerformActionX,
  clientWalkAndPerformActionY,
  clientWalkAndPerformActionPathX,
  clientWalkAndPerformActionPathY,
  
  -- 22c
  clientBankWithdrawItemID,
  clientBankWithdrawAmount,
  clientBankWithdrawMagicNumber,

  -- 23c
  clientBankDepositItemID,
  clientBankDepositAmount,
  clientBankDepositMagicNumber,
  
  -- 29c
  clientCombatStyle,
  
  -- 33c
  clientSendStakedItemsCount,
  clientSendStakedItemsItemID,
  clientSendStakedItemsItemAmount,
  
  -- 38c
  clientSendCommandStringCommand,
  
  -- 45c
  clientGuessSleepwordDelay,
  clientGuessSleepwordGuess,
  
  -- 46c
  clientOfferItemItemCount,
  clientOfferItemItemID,
  clientOfferItemItemStack,
  
  -- 50c
  clientCastNPCSpellID,
  clientCastNPCNPCID,
  
  -- 53c
  clientUseOnGroundItemXCoord,
  clientUseOnGroundItemYCoord,
  clientUseOnGroundItemInventorySlot,
  clientUseOnGroundItemGroundItemID,
  
  -- 59c
  clientAdminTeleportXCoord,
  clientAdminTeleportYCoord,

  -- 60c
  clientPrayerOnPrayer,
  
  -- 64c
  clientSendPrivacySettingsChat,
  clientSendPrivacySettingsPrivate,
  clientSendPrivacySettingsTrade,
  clientSendPrivacySettingsDuel,
  
  -- 79c
  clientInteractWithScenery2XCoord,
  clientInteractWithScenery2YCoord,

  -- 90c
  clientActivateInventoryItemSlot,
  
  -- 91c
  clientCombineInventorySlot1,
  clientCombineInventorySlot2,

  -- 99c
  clientCastOnSceneryXCoord,
  clientCastOnSceneryYCoord,
  clientCastOnScenerySpellID,
  
  -- 103c
  clientSendDuelRequest,
  
  -- 111c
  clientSetting,
  clientSettingValue,
  
  -- 113c
  clientUseOnPlayerPID,
  clientUseOnPlayerSlot,
  
  -- 115c
  clientUseOnSceneryX,
  clientUseOnSceneryY,
  clientUseOnScenerySlot,

  -- 116c
  clientOption,
  
  -- 127c
  clientInteractWithBoundary2XCoord,
  clientInteractWithBoundary2YCoord,
  clientInteractWithBoundary2Alignment,
  
  -- 132c
  clientAddIgnoreName,
  
  -- 135c
  clientUseOnNPCNPCID,
  clientUseOnNPCSlot,

  -- 136c
  clientInteractWithSceneryX,
  clientInteractWithSceneryY,

  -- 137c
  clientCastOnSelfSpell,
  
  -- 142c
  clientAgreeToTradePID,
  
  -- 153c
  clientTalkToNPCID,
  
  -- 158c
  clientCastOnGroundXCoord,
  clientCastOnGroundYCoord,
  clientCastOnGroundSpell,
  
  -- 161c
  clientUseWithBoundaryXCoord,
  clientUseWithBoundaryYCoord,
  clientUseWithBoundaryAlignment,
  clientUseWithBoundarySlot,
  
  -- 165c
  clientFollowPlayerPID,
  
  -- 167c
  clientRemoveFriendName,
  
  -- 169c
  clientEquipItemSlot,

  -- 170c
  clientUnequipItemItem,
  
  -- 171c
  clientAttackPlayerTargetPID,
  
  -- 180c
  clientCastOnBoundaryXCoord,
  clientCastOnBoundaryYCoord,
  clientCastOnBoundaryAlignment,
  clientCastOnBoundarySpell,

  -- 187c
  clientWalkX,
  clientWalkY,
  clientWalkPathX,
  clientWalkPathY,
  
  -- 190c
  clientAttackNPCID,

  -- 195c
  clientAddFriendName,
  
  -- 202c
  clientInteractNPCID,
  
  -- 206c
  clientSendReportName,
  clientSendReportReason,
  clientSendReportMute,
  
  -- 216c
  clientSendChatMessageLength,
  clientSendChatMessageMessage,
  
  -- 218c
  clientSendPrivateMessageRecipient,
  clientSendPrivateMessageLength,
  clientSendPrivateMessageMessage,
  
  -- 221c
  clientSellToShopItemId,
  clientSellToShopShopAmount,
  clientSellToShopSellAmount,

  -- 229c
  clientCastPVPTargetPID,
  clientCastPVPSpellID,
  
  -- 235c
  clientChangeAppearanceHeadRestrictions,
  clientChangeAppearanceHeadType,
  clientChangeAppearanceBodyType,
  clientChangeAppearanceAlways2,
  clientChangeAppearanceHairColour,
  clientChangeAppearanceTopColour,
  clientChangeAppearenceBottomColour,
  clientChangeAppearenceSkinColour,
  
  -- 236c
  clientBuyFromShopItemId,
  clientBuyFromShopShopAmount,
  clientBuyFromShopSellAmount,
  
  -- 241c
  clientExignoredName,
  
  -- 246c
  clientDropItemSlot,
  
  -- 247c
  clientTakeGroundItemXCoord,
  clientTakeGroundItemYCoord,
  clientTakeGroundItemItemID,
  
  -- 249c
  clientCastOnGroundItemXCoord,
  clientCastOnGroundItemYCoord,
  clientCastOnGroundItemItemID,
  clientCastOnGroundItemSpellID,
  
  -- 254c
  clientPrayerOffPrayer

}


if gui_enabled() then
  if thisDebugging then
    debugWindow = TextWindow.new("debug log")
  end
end
--============================================================================--

function resolveOpcodeName(clientPacket, opcode)
  if (clientPacket == 1) then
    return "CLIENT_" .. resolveClientOpcodeName(opcode)
  else
    return "SERVER_" .. resolveServerOpcodeName(opcode)
  end
end

function resolveClientOpcodeName(opcode)
  if (CLIENT_OPCODES[opcode] ~= nil) then
    return CLIENT_OPCODES[opcode]
  else
    return "OPCODE_UNKNOWN"
  end
end

function resolveServerOpcodeName(opcode)
  if (SERVER_OPCODES[opcode] ~= nil) then
    return SERVER_OPCODES[opcode]
  else
    return "OPCODE_UNKNOWN"
  end
end
function addOpcodeData(clientPacket, opcode, tree, buffer, pinfoNumber, pinfoVisited)
  if (clientPacket == 1) then
    addOpcodeDataClient(opcode, tree, buffer, pinfoNumber, pinfoVisited)
  else
    addOpcodeDataServer(opcode, tree, buffer, pinfoNumber, pinfoVisited)
  end
end

-- TODO: search for clientStream.newPacket(
function addOpcodeDataClient(opcode, tree, buffer, pinfoNumber, pinfoVisited)
  local packetLengthBuffer = rsc_getPacketLengthBuffer(buffer, 0)
  local packetLength = rsc_readPacketLength(packetLengthBuffer)

  -- Offset buffer by length size
  buffer = buffer(packetLengthBuffer:len())

  local clientOpcodeData = buffer(0, 1)
  local opcodeName = resolveClientOpcodeName(clientOpcodeData:uint())
  tree:add(clientPacketLength, packetLengthBuffer, packetLength)
  local opcodeField = tree:add(clientOpcode, clientOpcodeData)
  opcodeField:append_text(" (" .. opcodeName .. ")")

  local clientOpcodeValue = clientOpcodeData:uint()
  -- 0c -- CLIENT_OPCODE_CONNECT
  if (clientOpcodeValue == 0) then
    -- standalone, doesn't require data from other opcodes
    local reconnectingField = opcodeField:add(clientConnectReconnecting, buffer(1, 1))
    if buffer(1, 1):int() == 1 then
      reconnectingField:append_text(" (yes)")
    else
      reconnectingField:append_text(" (no)")
    end
    opcodeField:add(clientConnectVersion, buffer(2, 4)) -- always 235
    -- "encrypted field" only available intact in one known surviving replay,
    -- but because it's a client function, more could be generated if desired. 
    local encryptedField = opcodeField:add(clientConnectEncrypted, buffer(6, buffer:len() - 6))
    encryptedField:add("Contains XTEA keys, nonces, password, & username.")
    encryptedField:add("Removed for privacy in almost all replays.")
    
  -- 4c -- CLIENT_OPCODE_CAST_ON_INVENTORY_ITEM
  elseif (clientOpcodeValue == 4) then
    -- not standalone (unless you're OK not knowing what was in the slot)
    -- requires inventory state
    if pinfoVisited then
      thisPlayerInventory = dereferenceList(thisGameStateValuesAtThisTime[pinfoNumber][1])
    else
      thisGameStateValuesAtThisTime[pinfoNumber] = { dereferenceList(thisPlayerInventory) }
    end
    local slotField = opcodeField:add(clientCastOnInventoryInventoryIndex, buffer(1, 2))
    slotField:append_text(" (" .. ITEM_NAMES[thisPlayerInventory[bit.band(buffer(1, 2):uint(), 32767)]] .. ")")
    local spellField = opcodeField:add(clientCastOnInventorySpellID, buffer(3, 2))
    spellField:append_text(" (" .. SPELL_NAMES[buffer(3, 2):int()] .. ")")
  
  -- 8c -- CLIENT_OPCODE_SEND_DUEL_SETTINGS
  elseif (clientOpcodeValue == 8) then
    -- standalone, doesn't require data from other opcodes
    local retreatField = opcodeField:add(clientSendDuelSettingsRetreat, buffer(1, 1))
    retreatField:append_text(" (" .. boolToEnglish(buffer(1, 1):int()) .. ")")

    local magicField = opcodeField:add(clientSendDuelSettingsMagic, buffer(2, 1))
    magicField:append_text(" (" .. boolToEnglish(buffer(2, 1):int()) .. ")")

    local prayerField = opcodeField:add(clientSendDuelSettingsPrayer, buffer(3, 1))
    prayerField:append_text(" (" .. boolToEnglish(buffer(3, 1):int()) .. ")")

    local weaponsField = opcodeField:add(clientSendDuelSettingsWeapons, buffer(4, 1))
    weaponsField:append_text(" (" .. boolToEnglish(buffer(4, 1):int()) .. ")")

  -- 14c -- CLIENT_OPCODE_INTERACT_WITH_BOUNDARY
  elseif (clientOpcodeValue == 14) then
    opcodeField:add(clientInteractWithBoundaryXCoord, buffer(1, 2))
    opcodeField:add(clientInteractWithBoundaryYCoord, buffer(3, 2))
    local alignmentField = opcodeField:add(clientInteractWithBoundaryAlignment, buffer(5, 1))
    alignmentField:append_text(" (" .. BOUNDARY_ALIGNMENTS[buffer(5, 1):int()] .. ")")

  -- 16c -- CLIENT_OPCODE_WALK_AND_PERFORM_ACTION
  elseif (clientOpcodeValue == 16) then
    -- standalone, doesn't require data from other opcodes
    opcodeField:add(clientWalkAndPerformActionX, buffer(1, 2))
    opcodeField:add(clientWalkAndPerformActionY, buffer(3, 2))
    if packetLength > 5 then
      pathField = opcodeField:add("Client Calculated Path:")
      pathField:add("Note: May deviate from actual path taken, especially if player interrupts path with new request.")
      for i = 5, packetLength - 1, 2 do
        if i > 5 then
          pointsField:append_text(" then")
        end
        pointXBuffer = buffer(i, 1)
        pointYBuffer = buffer(i + 1, 1)
        pointsField = pathField:add("Walk to the point ")
        
        if pointXBuffer:int() > 0 then
          if pointXBuffer:int() > 1 then
            pointsField:append_text(pointXBuffer:int() .. " tiles West")
          else
            pointsField:append_text("1 tile West")
          end
        elseif pointXBuffer:int() < 0 then
          if pointXBuffer:int() < -1 then
            pointsField:append_text((pointXBuffer:int() * -1) .. " tiles East")
          else
            pointsField:append_text("1 tile East")
          end
        end
        
        if pointYBuffer:int() > 0 then
          if pointXBuffer:int() ~= 0 then
            if pointYBuffer:int() > 1 then
              pointsField:append_text(" and " .. pointYBuffer:int() .. " tiles South")
            else
              pointsField:append_text(" and 1 tile South")
            end
          else
            if pointYBuffer:int() > 1 then
              pointsField:append_text(pointYBuffer:int() .. " tiles South")
            else
              pointsField:append_text("1 tile South")
            end
          end
        elseif pointYBuffer:int() < 0 then
          if pointXBuffer:int() ~= 0 then
            if pointYBuffer:int() < -1 then
              pointsField:append_text(" and " .. (pointYBuffer:int() * -1) .. " tiles North")
            else
              pointsField:append_text(" and 1 tile North")
            end
          else
            if pointYBuffer:int() < -1 then
              pointsField:append_text((pointYBuffer:int() * -1) .. " tiles North")
            else
              pointsField:append_text("1 tile North")
            end
          end
        end
        
        pointsField:add(clientWalkAndPerformActionPathX, pointXBuffer)
        pointsField:add(clientWalkAndPerformActionPathY, pointYBuffer)
      end
    end
    
  -- 22c -- CLIENT_OPCODE_BANK_WITHDRAW
  elseif (clientOpcodeValue == 22) then
    -- standalone, doesn't require data from other opcodes
    local itemIDBuffer = buffer(1, 2)
    local itemIDField = opcodeField:add(clientBankWithdrawItemID, itemIDBuffer)
    itemIDField:append_text(" (" .. ITEM_NAMES[itemIDBuffer:uint()] .. ")")
    opcodeField:add(clientBankWithdrawAmount, buffer(3, 4))
    opcodeField:add(clientBankWithdrawMagicNumber, buffer(7, 4))

  -- 23c -- CLIENT_OPCODE_BANK_DEPOSIT
  elseif (clientOpcodeValue == 23) then
    -- standalone, doesn't require data from other opcodes
    local itemIDBuffer = buffer(1, 2)
    local itemIDField = opcodeField:add(clientBankDepositItemID, itemIDBuffer)
    itemIDField:append_text(" (" .. ITEM_NAMES[itemIDBuffer:uint()] .. ")")
    opcodeField:add(clientBankDepositAmount, buffer(3, 4))
    opcodeField:add(clientBankDepositMagicNumber, buffer(7, 4))

  -- 29c -- CLIENT_OPCODE_SEND_COMBAT_STYLE
  elseif (clientOpcodeValue == 29) then
    -- standalone, doesn't require data from other opcodes
    local combatStyleField = opcodeField:add(clientCombatStyle, buffer(1, 1))
    local combatStyle = buffer(1, 1):uint()
    if combatStyle == 0 then
      combatStyleField:append_text(" (Controlled)")
    elseif combatStyle == 1 then
      combatStyleField:append_text(" (Aggressive)")
    elseif combatStyle == 2 then
      combatStyleField:append_text(" (Accurate)")
    elseif combatStyle == 3 then
      combatStyleField:append_text(" (Defensive)")
    end
    
    if pinfoNumber < 50 then
      local explanationField = tree:add("This opcode existing this early into the login process is a result of RSC+ injecting it.")
      explanationField:add("Authentically, the client cannot send this opcode unless the user is in combat and manually selects a combat style.")
      explanationField:add("One of RSC+'s features was to remember what combat style the user was using last, as the server does not keep track")
      explanationField:add("of that after logging out. RSC+ injecting it here maintained the user's preferred combat style across logins.")
    end
    
  -- 33c -- SERVER_OPCODE_SEND_STAKED_ITEMS
  elseif (clientOpcodeValue == 33) then
    -- standalone, doesn't require data from other opcodes
    local itemCount = buffer(1, 1):int()
    opcodeField:add(clientSendStakedItemsCount, buffer(1, 1))
    local offset = 2
    for i = 0, itemCount - 1 do
      local itemId = buffer(offset, 2):int()
      local itemIdField = opcodeField:add(clientSendStakedItemsItemID, buffer(offset, 2))
      itemIdField:append_text(" (" .. ITEM_NAMES[itemId] .. ")")
      itemIdField:add(clientSendStakedItemsItemAmount, buffer(offset + 2, 4))
      offset = offset + 6
    end
      
  -- 38c -- CLIENT_OPCODE_SEND_COMMAND_STRING
  elseif (clientOpcodeValue == 38) then
    -- standalone, doesn't require data from other opcodes
    local commandBuffer = rsc_getRSCStringBuffer(buffer, 1)
    opcodeField:add(clientSendCommandStringCommand, commandBuffer, rsc_readRSCString(commandBuffer))
    
  -- 45c -- CLIENT_OPCODE_SEND_SLEEP_WORD
  elseif (clientOpcodeValue == 45) then
    local delaySleepWordBuffer = buffer(1, 1)
    opcodeField:add(clientGuessSleepwordDelay, delaySleepWordBuffer)
    
    local wordGuessBuffer = rsc_getRSCStringBuffer(buffer, 2)
    local guessField = opcodeField:add(clientGuessSleepwordGuess, wordGuessBuffer, rsc_readRSCString(wordGuessBuffer))
    -- TODO: can say if it was successful or not by looking ahead
    
  -- 46c -- CLIENT_OPCODE_OFFER_TRADE_ITEM
  elseif (clientOpcodeValue == 46) then
    -- standalone, doesn't require data from other opcodes
    local itemCount = buffer(1, 1):uint()
    local itemCountField = opcodeField:add(clientOfferItemItemCount, buffer(1, 1))
    
    for i = 0, itemCount - 1 do
      local itemOfferedField = itemCountField:add("")
      local itemIDBuffer = buffer(2 + i * 6, 2)
      local itemStackCountBuffer = buffer(4 + i * 6, 4)
      itemOfferedField:append_text(ITEM_NAMES[itemIDBuffer:uint()] .. " (" .. itemStackCountBuffer:uint() .. ")")
      local itemIDField = itemOfferedField:add(clientOfferItemItemID, itemIDBuffer)
      itemIDField:append_text(" (" .. ITEM_NAMES[itemIDBuffer:uint()] .. ")")
      itemOfferedField:add(clientOfferItemItemStack, itemStackCountBuffer)
    end
  
  -- 50c -- CLIENT_OPCODE_CAST_NPC
  elseif (clientOpcodeValue == 50) then
    -- not standalone
    -- need npc index <-> npc id mapping
    -- load / save those values
    if pinfoVisited then
      thisNpcsServer = dereferenceList(thisGameStateValuesAtThisTime[pinfoNumber][1])
    else
      thisGameStateValuesAtThisTime[pinfoNumber] = { dereferenceList(thisNpcsServer) }
    end
    local npcIDField = opcodeField:add(clientCastNPCNPCID, buffer(1, 2))
    npcIDField:append_text(" (" .. NPC_NAMES[thisNpcsServer[buffer(1, 2):uint()]["npcId"]] .. ")")
    local spellIDField = opcodeField:add(clientCastNPCSpellID, buffer(3, 2))
    spellIDField:append_text(" (" .. SPELL_NAMES[buffer(3, 2):int()] .. ")")
    
  -- 53c -- CLIENT_OPCODE_USE_ON_GROUND_ITEM
  elseif (clientOpcodeValue == 53) then
    -- not standalone (unless you're OK not knowing what was in the slot)
    -- requires inventory state
    if pinfoVisited then
      thisPlayerInventory = dereferenceList(thisGameStateValuesAtThisTime[pinfoNumber][1])
    else
      thisGameStateValuesAtThisTime[pinfoNumber] = { dereferenceList(thisPlayerInventory) }
    end
    opcodeField:add(clientUseOnGroundItemXCoord, buffer(1, 2))
    opcodeField:add(clientUseOnGroundItemYCoord, buffer(3, 2))
    local groundItemIdField = opcodeField:add(clientUseOnGroundItemGroundItemID, buffer(5, 2))
    groundItemIdField:append_text(" (" .. ITEM_NAMES[buffer(5, 2):uint()] .. ")")
    local inventorySlotField = opcodeField:add(clientUseOnGroundItemInventorySlot, buffer(7, 2))
    inventorySlotField:append_text(" (" .. ITEM_NAMES[thisPlayerInventory[bit.band(buffer(7, 2):uint(), 32767)]] .. ")")
    
  -- 59c -- CLIENT_OPCODE_ADMIN_TELEPORT_TO_TILE_PROBABLY
  elseif (clientOpcodeValue == 59) then
    -- standalone, doesn't require data from other opcodes
    opcodeField:add(clientAdminTeleportXCoord, buffer(1, 2))
    opcodeField:add(clientAdminTeleportYCoord, buffer(3, 2))  
  
  -- 60c -- CLIENT_OPCODE_ENABLE_PRAYER
  elseif (clientOpcodeValue == 60) then
    -- standalone, doesn't require data from other opcodes
    prayerField = opcodeField:add(clientPrayerOnPrayer, buffer(1, 1))
    prayerField:append_text(" (" .. PRAYER_NAMES[buffer(1, 1):uint()] .. ")")
    
  -- 64c -- CLIENT_OPCODE_SEND_PRIVACY_SETTINGS
  elseif (clientOpcodeValue == 64) then
    -- standalone, doesn't require data from other opcodes
    opcodeField:add(clientSendPrivacySettingsChat, buffer(1, 1))
    opcodeField:add(clientSendPrivacySettingsPrivate, buffer(2, 1))
    opcodeField:add(clientSendPrivacySettingsTrade, buffer(3, 1))
    opcodeField:add(clientSendPrivacySettingsDuel, buffer(4, 1))
  
  -- 79c -- CLIENT_OPCODE_INTERACT_WITH_SCENERY2
  elseif (clientOpcodeValue == 79) then
    -- standalone, doesn't require data from other opcodes
    opcodeField:add(clientInteractWithScenery2XCoord, buffer(1, 2))
    opcodeField:add(clientInteractWithScenery2YCoord, buffer(3, 2))
    
  -- 90c -- CLIENT_OPCODE_ACTIVATE_INVENTORY_ITEM
  elseif (clientOpcodeValue == 90) then
    -- not standalone (unless you're OK not knowing what was in the slot)
    -- requires inventory state
    if pinfoVisited then
      thisPlayerInventory = dereferenceList(thisGameStateValuesAtThisTime[pinfoNumber][1])
    else
      thisGameStateValuesAtThisTime[pinfoNumber] = { dereferenceList(thisPlayerInventory) }
    end
    local slotField = opcodeField:add(clientActivateInventoryItemSlot, buffer(1, 2))
    slotField:append_text(" (" .. ITEM_NAMES[bit.band(thisPlayerInventory[buffer(1, 2):uint()], 32767)] .. ")")
  
  -- 91c -- CLIENT_OPCODE_COMBINE_INVENTORY_ITEMS
  elseif (clientOpcodeValue == 91) then
    -- not standalone (unless you're OK not knowing what was in the slot)
    -- requires inventory state
    if pinfoVisited then
      thisPlayerInventory = dereferenceList(thisGameStateValuesAtThisTime[pinfoNumber][1])
    else
      thisGameStateValuesAtThisTime[pinfoNumber] = { dereferenceList(thisPlayerInventory) }
    end
    
    local slotField = opcodeField:add(clientCombineInventorySlot1, buffer(1, 2))
    slotField:append_text(" (" .. ITEM_NAMES[bit.band(thisPlayerInventory[buffer(1, 2):uint()], 32767)] .. ")")
    local slotField = opcodeField:add(clientCombineInventorySlot2, buffer(3, 2))
    slotField:append_text(" (" .. ITEM_NAMES[bit.band(thisPlayerInventory[buffer(3, 2):uint()], 32767)] .. ")")
    
  -- 99c -- CLIENT_OPCODE_CAST_ON_SCENERY
  elseif (clientOpcodeValue == 99) then
    -- standalone, doesn't require data from other opcodes
    opcodeField:add(clientCastOnSceneryXCoord, buffer(1, 2))
    opcodeField:add(clientCastOnSceneryYCoord, buffer(3, 2))
    local spellField = opcodeField:add(clientCastOnScenerySpellID, buffer(5, 2))
    spellField:append_text(" (" .. SPELL_NAMES[buffer(5, 2):int()] .. ")")
  
  -- 103c -- CLIENT_SEND_DUEL_REQUEST
  elseif (clientOpcodeValue == 103) then
    -- not standalone, would like to know which player has which PID
    if pinfoVisited then
      thisPlayerServer[buffer(1, 2):int()]["displayName"] = thisGameStateValuesAtThisTime[pinfoNumber]
    else
      thisGameStateValuesAtThisTime[pinfoNumber] = thisPlayerServer[buffer(1, 2):int()]["displayName"]
    end
    
    local targetField = opcodeField:add(clientSendDuelRequest, buffer(1, 2))
    targetField:append_text(" (" .. thisPlayerServer[buffer(1, 2):int()]["displayName"] .. ")")
  
  -- 111c -- CLIENT_OPCODE_SEND_CLIENT_SETTINGS
  elseif (clientOpcodeValue == 111) then
    -- standalone, doesn't require data from other opcodes
    local optionField = opcodeField:add(clientSetting, buffer(1, 1))
    optionField:append_text(" (" .. OPTION_NAMES[buffer(1, 1):int()] .. ")")
    opcodeField:add(clientSettingValue, buffer(2, 1))
  
  -- 113c -- CLIENT_OPCODE_USE_WITH_PLAYER
  elseif (clientOpcodeValue == 113) then
    -- not standalone, would like to know which player has which PID & what item they are using
    if pinfoVisited then
      thisPlayerServer[buffer(1, 2):int()]["displayName"] = thisGameStateValuesAtThisTime[pinfoNumber][1]
      thisPlayerInventory = dereferenceList(thisGameStateValuesAtThisTime[pinfoNumber][2])
    else
      thisGameStateValuesAtThisTime[pinfoNumber] = { thisPlayerServer[buffer(1, 2):int()]["displayName"], dereferenceList(thisPlayerInventory) }
    end
    
    local targetField = opcodeField:add(clientUseOnPlayerPID, buffer(1, 2))
    targetField:append_text(" (" .. thisPlayerServer[buffer(1, 2):int()]["displayName"] .. ")")
    local slotField = opcodeField:add(clientUseOnPlayerSlot, buffer(3, 2))
    slotField:append_text(" (" .. ITEM_NAMES[thisPlayerInventory[bit.band(buffer(3, 2):uint(), 32767)]] .. ")")

  -- 115c -- CLIENT_USE_WITH_SCENERY
  elseif (clientOpcodeValue == 115) then
    -- not standalone, would like to know what item they are using
    if pinfoVisited then
      thisPlayerInventory = dereferenceList(thisGameStateValuesAtThisTime[pinfoNumber])
    else
      thisGameStateValuesAtThisTime[pinfoNumber] = dereferenceList(thisPlayerInventory)
    end
    opcodeField:add(clientUseOnSceneryX, buffer(1, 2))
    opcodeField:add(clientUseOnSceneryY, buffer(3, 2))
    local slotField = opcodeField:add(clientUseOnScenerySlot, buffer(5, 2))
    slotField:append_text(" (" .. ITEM_NAMES[thisPlayerInventory[bit.band(buffer(5, 2):uint(), 32767)]] .. ")")

  -- 116c -- CLIENT_OPCODE_SELECT_DIALOGUE_OPTION
  elseif (clientOpcodeValue == 116) then
    -- not standalone
    -- requires thisOptionCache
    if pinfoVisited then
      thisOptionCache = thisGameStateValuesAtThisTime[pinfoNumber]
    else 
      thisGameStateValuesAtThisTime[pinfoNumber] = dereferenceList(thisOptionCache)
    end

    local optionBuffer = buffer(1, 1)
    local optionField = opcodeField:add(clientOption, optionBuffer)
    optionField:append_text(" (" .. thisOptionCache[optionBuffer:uint()] .. ")")
    
  -- 127c -- CLIENT_OPCODE_INTERACT_WITH_BOUNDARY2
  elseif (clientOpcodeValue == 127) then
    -- standalone, doesn't require data from other opcodes
    opcodeField:add(clientInteractWithBoundary2XCoord, buffer(1, 2))
    opcodeField:add(clientInteractWithBoundary2YCoord, buffer(3, 2))
    local alignmentField = opcodeField:add(clientInteractWithBoundary2Alignment, buffer(5, 1))
    alignmentField:append_text(" (" .. BOUNDARY_ALIGNMENTS[buffer(5, 1):int()] .. ")")
    
  -- 132c -- CLIENT_OPCODE_ADD_IGNORE
  elseif (clientOpcodeValue == 132) then
    -- standalone, doesn't require data from other opcodes
    local ignoreNameBuffer = rsc_getRSCStringBuffer(buffer, 1)
    opcodeField:add(clientAddIgnoreName, ignoreNameBuffer, rsc_readRSCString(ignoreNameBuffer))
    
  -- 135c -- CLIENT_OPCODE_USE_ON_NPC
  elseif (clientOpcodeValue == 135) then
    -- not standalone, would like to know which player has which PID & what item they are using
    if pinfoVisited then
      thisNpcsServer = dereferenceList(thisGameStateValuesAtThisTime[pinfoNumber][1])
      thisPlayerInventory = dereferenceList(thisGameStateValuesAtThisTime[pinfoNumber][2])
    else
      thisGameStateValuesAtThisTime[pinfoNumber] = { dereferenceList(thisNpcsServer), dereferenceList(thisPlayerInventory) }
    end
    
    -- TODO: didn't check the order of these yet
    local npcIDField = opcodeField:add(clientUseOnNPCNPCID, buffer(1, 2))
    npcIDField:append_text(" (" .. NPC_NAMES[thisNpcsServer[buffer(1, 2):uint()]["npcId"]] .. ")")
    local slotField = opcodeField:add(clientUseOnNPCSlot, buffer(3, 2))
    slotField:append_text(" (" .. ITEM_NAMES[thisPlayerInventory[bit.band(buffer(3, 2):uint(), 32767)]] .. ")")
    
  -- 136c -- CLIENT_OPCODE_INTERACT_WITH_SCENERY
  elseif (clientOpcodeValue == 136) then
    -- standalone, doesn't require data from other opcodes
    opcodeField:add(clientInteractWithSceneryX, buffer(1, 2))
    opcodeField:add(clientInteractWithSceneryY, buffer(3, 2))
    
  -- 137c -- CLIENT_OPCODE_CAST_ON_SELF
  elseif (clientOpcodeValue == 137) then
    -- standalone, doesn't require data from other opcodes
    local spellField = opcodeField:add(clientCastOnSelfSpell, buffer(1, 2))
    spellField:append_text(" (" .. SPELL_NAMES[buffer(1, 2):int()] .. ")")
  
  -- 142c -- CLIENT_OPCODE_AGREE_TO_TRADE
  elseif (clientOpcodeValue == 142) then
    -- not standalone, would like to know which player has which PID
    if pinfoVisited then
      thisPlayerServer[buffer(1, 2):int()]["displayName"] = thisGameStateValuesAtThisTime[pinfoNumber]
    else
      thisGameStateValuesAtThisTime[pinfoNumber] = thisPlayerServer[buffer(1, 2):int()]["displayName"]
    end

    local targetField = opcodeField:add(clientAgreeToTradePID, buffer(1, 2))
    targetField:append_text(" (" .. thisPlayerServer[buffer(1, 2):int()]["displayName"] .. ")")
    
  -- 153c -- CLIENT_OPCODE_TALK_TO_NPC
  elseif (clientOpcodeValue == 153) then
    -- not standalone, would like to know which NPC player is talking to
    if pinfoVisited then
      thisNpcsServer = dereferenceList(thisGameStateValuesAtThisTime[pinfoNumber])
    else
      thisGameStateValuesAtThisTime[pinfoNumber] = dereferenceList(thisNpcsServer)
    end
    
    local npcIDField = opcodeField:add(clientTalkToNPCID, buffer(1, 2))
    npcIDField:append_text(" (" .. NPC_NAMES[thisNpcsServer[buffer(1, 2):uint()]["npcId"]] .. ")")
    
  -- 158c -- CLIENT_OPCODE_CAST_ON_GROUND
  elseif (clientOpcodeValue == 158) then
    -- standalone, doesn't require data from other opcodes
    opcodeField:add(clientCastOnGroundXCoord, buffer(1, 2))
    opcodeField:add(clientCastOnGroundYCoord, buffer(3, 2))
    local spellField = opcodeField:add(clientCastOnGroundSpell, buffer(5, 2))
    spellField:append_text(" (" .. SPELL_NAMES[buffer(5, 2):int()] .. ")")
  
  -- 161c -- CLIENT_OPCODE_USE_WITH_BOUNDARY  
  elseif (clientOpcodeValue == 161) then
    -- not standalone, would like to know what item the player is using
    if pinfoVisited then
      thisPlayerInventory = dereferenceList(thisGameStateValuesAtThisTime[pinfoNumber])
    else
      thisGameStateValuesAtThisTime[pinfoNumber] = dereferenceList(thisPlayerInventory)
    end
    opcodeField:add(clientUseWithBoundaryXCoord, buffer(1, 2))
    opcodeField:add(clientUseWithBoundaryYCoord, buffer(3, 2))
    opcodeField:add(clientUseWithBoundaryAlignment, buffer(5, 1))
    local slotField = opcodeField:add(clientUseWithBoundarySlot, buffer(6, 2))
    slotField:append_text(" (" .. ITEM_NAMES[thisPlayerInventory[bit.band(buffer(6, 2):uint(), 32767)]] .. ")")

  -- 165c -- CLIENT_OPCODE_FOLLOW_PLAYER
  elseif (clientOpcodeValue == 165) then
    -- not standalone, would like to know which player has which PID
    if pinfoVisited then
      thisPlayerServer[buffer(1, 2):int()]["displayName"] = thisGameStateValuesAtThisTime[pinfoNumber]
    else
      thisGameStateValuesAtThisTime[pinfoNumber] = thisPlayerServer[buffer(1, 2):int()]["displayName"]
    end
    
    local targetField = opcodeField:add(clientFollowPlayerPID, buffer(1, 2))
    targetField:append_text(" (" .. thisPlayerServer[buffer(1, 2):int()]["displayName"] .. ")")
  
  -- 167c -- CLIENT_OPCODE_REMOVE_FRIEND
  elseif (clientOpcodeValue == 167) then
    -- standalone, doesn't require data from other opcodes
    local friendBuffer = rsc_getRSCStringBuffer(buffer, 1)
    opcodeField:add(clientRemoveFriendName, friendBuffer, rsc_readRSCString(friendBuffer))
    
  -- 169c -- CLIENT_OPCODE_EQUIP_ITEM
  elseif (clientOpcodeValue == 169) then
    -- not standalone, would like to know what item the player is equipping
    if pinfoVisited then
      thisPlayerInventory = dereferenceList(thisGameStateValuesAtThisTime[pinfoNumber])
    else
      thisGameStateValuesAtThisTime[pinfoNumber] = dereferenceList(thisPlayerInventory)
    end
    
    local slotField = opcodeField:add(clientEquipItemSlot, buffer(1, 2))
    slotField:append_text(" (" .. ITEM_NAMES[thisPlayerInventory[bit.band(buffer(1, 2):uint(), 32767)]] .. ")")
      
  -- 170c -- CLIENT_OPCODE_UNEQUIP_ITEM
  elseif (clientOpcodeValue == 170) then
    -- not standalone, would like to know what item the player is unequipping
    if pinfoVisited then
      thisPlayerInventory = dereferenceList(thisGameStateValuesAtThisTime[pinfoNumber])
    else
      thisGameStateValuesAtThisTime[pinfoNumber] = dereferenceList(thisPlayerInventory)
    end
    
    local slotField = opcodeField:add(clientUnequipItemItem, buffer(1, 2))
    slotField:append_text(" (" .. ITEM_NAMES[thisPlayerInventory[bit.band(buffer(1, 2):uint(), 32767)]] .. ")")
    
  -- 171c -- CLIENT_OPCODE_ATTACK_PLAYER
  elseif (clientOpcodeValue == 171) then
    -- not standalone, would like to know which player has which PID
    if pinfoVisited then
      thisPlayerServer[buffer(1, 2):int()]["displayName"] = thisGameStateValuesAtThisTime[pinfoNumber]
    else
      thisGameStateValuesAtThisTime[pinfoNumber] = thisPlayerServer[buffer(1, 2):int()]["displayName"]
    end
    
    local targetField = opcodeField:add(clientAttackPlayerTargetPID, buffer(1, 2))
    targetField:append_text(" (" .. thisPlayerServer[buffer(1, 2):int()]["displayName"] .. ")")
    
  -- 180c -- CLIENT_OPCODE_CAST_ON_BOUNDARY
  elseif (clientOpcodeValue == 180) then
    -- standalone, doesn't require data from other opcodes
    opcodeField:add(clientCastOnBoundaryXCoord, buffer(1, 2))
    opcodeField:add(clientCastOnBoundaryYCoord, buffer(3, 2))
    opcodeField:add(clientCastOnBoundaryAlignment, buffer(5, 1))
    local spellField = opcodeField:add(clientCastOnGroundSpell, buffer(6, 2))
    spellField:append_text(" (" .. SPELL_NAMES[buffer(6, 2):int()] .. ")")
  
  -- 187c -- CLIENT_OPCODE_WALK
  elseif (clientOpcodeValue == 187) then
    -- standalone, doesn't require data from other opcodes
    opcodeField:add(clientWalkX, buffer(1, 2))
    opcodeField:add(clientWalkY, buffer(3, 2))
    if packetLength > 5 then
      pathField = opcodeField:add("Client Calculated Path:")
      for i = 5, packetLength - 1, 2 do
        if i > 5 then
          pointsField:append_text(" then")
        end
        pointXBuffer = buffer(i, 1)
        pointYBuffer = buffer(i + 1, 1)
        pointsField = pathField:add("Walk to the point ")
        
        if pointXBuffer:int() > 0 then
          if pointXBuffer:int() > 1 then
            pointsField:append_text(pointXBuffer:int() .. " tiles West")
          else
            pointsField:append_text("1 tile West")
          end
        elseif pointXBuffer:int() < 0 then
          if pointXBuffer:int() < -1 then
            pointsField:append_text((pointXBuffer:int() * -1) .. " tiles East")        
          else
            pointsField:append_text("1 tile East")
          end
        end
        
        if pointYBuffer:int() > 0 then
          if pointXBuffer:int() ~= 0 then
            if pointYBuffer:int() > 1 then
              pointsField:append_text(" and " .. pointYBuffer:int() .. " tiles South")
            else
              pointsField:append_text(" and 1 tile South")
            end
          else
            if pointYBuffer:int() > 1 then
              pointsField:append_text(pointYBuffer:int() .. " tiles South")
            else
              pointsField:append_text("1 tile South")
            end
          end
        elseif pointYBuffer:int() < 0 then
          if pointXBuffer:int() ~= 0 then
            if pointYBuffer:int() < -1 then
              pointsField:append_text(" and " .. (pointYBuffer:int() * -1) .. " tiles North")
            else
              pointsField:append_text(" and 1 tile North")
            end
          else
            if pointYBuffer:int() < -1 then
              pointsField:append_text((pointYBuffer:int() * -1) .. " tiles North")
            else
              pointsField:append_text("1 tile North")
            end
          end
        end
        
        pointsField:add(clientWalkPathX, pointXBuffer)
        pointsField:add(clientWalkPathY, pointYBuffer)
      end
    end
    
  -- 190c -- CLIENT_OPCODE_ATTACK_NPC
  elseif (clientOpcodeValue == 190) then
    -- not standalone, would like to know which NPC player is attacking
    if pinfoVisited then
      thisNpcsServer = dereferenceList(thisGameStateValuesAtThisTime[pinfoNumber])
    else
      thisGameStateValuesAtThisTime[pinfoNumber] = dereferenceList(thisNpcsServer)
    end
    
    local npcIDField = opcodeField:add(clientAttackNPCID, buffer(1, 2))
    npcIDField:append_text(" (" .. NPC_NAMES[thisNpcsServer[buffer(1, 2):uint()]["npcId"]] .. ")")
    
  -- 195c -- CLIENT_OPCODE_ADD_FRIEND
  elseif (clientOpcodeValue == 195) then
    -- standalone, doesn't require data from other opcodes
    local friendBuffer = rsc_getRSCStringBuffer(buffer, 1)
    opcodeField:add(clientAddFriendName, friendBuffer, rsc_readRSCString(friendBuffer))
  
  -- 202c -- CLIENT_OPCODE_INTERACT_NPC
  elseif (clientOpcodeValue == 202) then
    -- not standalone, would like to know which NPC player is interacting with
    if pinfoVisited then
      thisNpcsServer = dereferenceList(thisGameStateValuesAtThisTime[pinfoNumber])
    else
      thisGameStateValuesAtThisTime[pinfoNumber] = dereferenceList(thisNpcsServer)
    end
    
    local npcIDField = opcodeField:add(clientInteractNPCID, buffer(1, 2))
    npcIDField:append_text(" (" .. NPC_NAMES[thisNpcsServer[buffer(1, 2):uint()]["npcId"]] .. ")")
  
  -- 206c -- CLIENT_OPCODE_SEND_REPORT
  elseif (clientOpcodeValue == 206) then
    -- standalone, doesn't require data from other opcodes
    local troubleMakerBuffer = rsc_getRSCStringBuffer(buffer, 1)
    offset = 1 + troubleMakerBuffer:len()
    opcodeField:add(clientSendReportName, troubleMakerBuffer, rsc_readRSCString(troubleMakerBuffer))
    
    local reportReasonField = opcodeField:add(clientSendReportReason, buffer(offset, 1))
    reportReasonField:append_text(" (" .. REPORT_REASONS[buffer(offset, 1):int()] .. ")")
    
    local muteField = opcodeField:add(clientSendReportMute, buffer(offset + 1, 1))
    if buffer(offset + 1, 1):int() == 1 then
      muteField:append_text(" (yes)")
    else
      muteField:append_text(" (no)")
    end

  -- 216c -- CLIENT_OPCODE_SEND_CHAT_MESSAGE
  elseif (clientOpcodeValue == 216) then
    offset = 1

    local method220Cache = method220(buffer, offset)
    local messageLengthBuffer = method220Cache[1]
    local messageLength = method220Cache[2]
    opcodeField:add(clientSendChatMessageLength, messageLengthBuffer, messageLength)
    offset = offset + method220Cache[3]

    local method240Cache = method240(buffer, 0, offset, messageLength)
    local scrambledChatMessageBufferLength = method240Cache[1]
    local chatMessageInts = method240Cache[2]
    opcodeField:add(clientSendChatMessageMessage, buffer(offset, scrambledChatMessageBufferLength), byteArrayToString(chatMessageInts))
    offset = offset + scrambledChatMessageBufferLength
    
  -- 218c -- CLIENT_OPCODE_SEND_PRIVATE_MESSAGE
  elseif (clientOpcodeValue == 218) then
    local recipientNameBuffer = rsc_getRSCStringBuffer(buffer, 1)
    offset = 1 + recipientNameBuffer:len()
    opcodeField:add(clientSendPrivateMessageRecipient, recipientNameBuffer, rsc_readRSCString(recipientNameBuffer))

    local method220Cache = method220(buffer, offset)
    local messageLengthBuffer = method220Cache[1]
    local messageLength = method220Cache[2]
    opcodeField:add(clientSendPrivateMessageLength, messageLengthBuffer, messageLength)
    offset = offset + method220Cache[3]

    local method240Cache = method240(buffer, 0, offset, messageLength)
    local scrambledChatMessageBufferLength = method240Cache[1]
    local chatMessageInts = method240Cache[2]
    opcodeField:add(clientSendPrivateMessageMessage, buffer(offset, scrambledChatMessageBufferLength), byteArrayToString(chatMessageInts))
    offset = offset + scrambledChatMessageBufferLength
    
  -- 221c -- CLIENT_OPCODE_SELL_TO_SHOP
  elseif (clientOpcodeValue == 221) then
    -- standalone, doesn't require data from other opcodes
    local itemField = opcodeField:add(clientSellToShopItemId, buffer(1, 2))
    itemField:append_text(" (" .. ITEM_NAMES[buffer(1, 2):uint()] .. ")")
    opcodeField:add(clientSellToShopShopAmount, buffer(3, 2))
    opcodeField:add(clientSellToShopSellAmount, buffer(5, 2))
     
  -- 229c -- CLIENT_OPCODE_CAST_PVP
  elseif (clientOpcodeValue == 229) then
    -- not standalone, would like to know which player has which PID
    if pinfoVisited then
      thisPlayerServer[buffer(1, 2):int()]["displayName"] = thisGameStateValuesAtThisTime[pinfoNumber]
    else
      thisGameStateValuesAtThisTime[pinfoNumber] = thisPlayerServer[buffer(1, 2):int()]["displayName"]
    end
    
    local targetField = opcodeField:add(clientCastPVPTargetPID, buffer(1, 2))
    targetField:append_text(" (" .. thisPlayerServer[buffer(1, 2):int()]["displayName"] .. ")")
    
    local spellField = opcodeField:add(clientCastPVPSpellID, buffer(3, 2))
    spellField:append_text(" (" .. SPELL_NAMES[buffer(3, 2):int()] .. ")")
    
  -- 235c -- CLIENT_OPCODE_CHANGE_APPEARANCE
  elseif (clientOpcodeValue == 235) then
    -- standalone, doesn't require data from other opcodes
    local headRestrictionsField = opcodeField:add(clientChangeAppearanceHeadRestrictions, buffer(1, 1))
    if (buffer(1, 1):uint() == 1) then
      headRestrictionsField:append_text(" (Beard Allowed)")
    elseif (buffer(1, 1):uint() == 2) then
      headRestrictionsField:append_text(" (Beard Not Allowed)")
    end
    
    local headTypeField = opcodeField:add(clientChangeAppearanceHeadType, buffer(2, 1))
    if (buffer(2, 1):uint() == 0) then
      headTypeField:append_text(" (?)")
    elseif (buffer(2, 1):uint() == 3) then
      headTypeField:append_text(" (?)")
    elseif (buffer(2, 1):uint() == 5) then
      headTypeField:append_text(" (?)")
    elseif (buffer(2, 1):uint() == 6) then
      headTypeField:append_text(" (?)")
    elseif (buffer(2, 1):uint() == 7) then
      headTypeField:append_text(" (Bald normal type)")
    end
    
    local bodyTypeField = opcodeField:add(clientChangeAppearanceBodyType, buffer(3, 1))
    if (buffer(3, 1):uint() == 1) then
      bodyTypeField:append_text(" (Flat Chest)")
    elseif (buffer(3, 1):uint() == 4) then
      bodyTypeField:append_text(" (Busty Chest)")
    end
    
    -- Always 2, even in very old versions of client, including v40, v127, v204, & v233
    opcodeField:add(clientChangeAppearanceAlways2, buffer(4, 1))
    
    local hairColourField = opcodeField:add(clientChangeAppearanceHairColour, buffer(5, 1))
    hairColourField:append_text(" (HTML Colour: " .. HAIR_COLOURS_STRING[buffer(5, 1):uint()] .. ") (English Name: " .. HAIR_COLOUR_NAMES[buffer(5, 1):uint()] .. ")") 
    
    local topColourField = opcodeField:add(clientChangeAppearanceTopColour, buffer(6, 1))
    topColourField:append_text(" (HTML Colour: " .. CLOTHING_COLOURS_STRING[buffer(6, 1):uint()] .. ") (English Name: " .. CLOTHING_COLOUR_NAMES[buffer(6, 1):uint()] .. ")") 
    
    local bottomColourField = opcodeField:add(clientChangeAppearenceBottomColour, buffer(7, 1))
    bottomColourField:append_text(" (HTML Colour: " .. CLOTHING_COLOURS_STRING[buffer(7, 1):uint()] .. ") (English Name: " .. CLOTHING_COLOUR_NAMES[buffer(7, 1):uint()] .. ")") 
    
    local skinColourField = opcodeField:add(clientChangeAppearenceSkinColour, buffer(8, 1))
    skinColourField:append_text(" (HTML Colour: " .. SKIN_COLOURS_STRING[buffer(8, 1):uint()] .. ") (English Name: " .. SKIN_COLOUR_NAMES[buffer(8, 1):uint()] .. ")")
  
  -- 236c -- CLIENT_OPCODE_BUY_FROM_SHOP
  elseif (clientOpcodeValue == 236) then
    -- standalone, doesn't require data from other opcodes
    local itemField = opcodeField:add(clientBuyFromShopItemId, buffer(1, 2))
    itemField:append_text(" (" .. ITEM_NAMES[buffer(1, 2):uint()] .. ")")
    opcodeField:add(clientBuyFromShopShopAmount, buffer(3, 2))
    opcodeField:add(clientBuyFromShopSellAmount, buffer(5, 2))
    
  -- 241c -- CLIENT_OPCODE_REMOVE_IGNORED
  elseif (clientOpcodeValue == 241) then
    -- standalone, doesn't require data from other opcodes
    local exignoredBuffer = rsc_getRSCStringBuffer(buffer, 1)
    opcodeField:add(clientExignoredName, exignoredBuffer, rsc_readRSCString(exignoredBuffer))
    
  -- 246c -- CLIENT_OPCODE_DROP_ITEM
  elseif (clientOpcodeValue == 246) then
    -- not standalone, would like to know what item the player is unequipping
    if pinfoVisited then
      thisPlayerInventory = dereferenceList(thisGameStateValuesAtThisTime[pinfoNumber])
    else
      thisGameStateValuesAtThisTime[pinfoNumber] = dereferenceList(thisPlayerInventory)
    end
    
    local slotField = opcodeField:add(clientDropItemSlot, buffer(1, 2))
    slotField:append_text(" (" .. ITEM_NAMES[thisPlayerInventory[bit.band(buffer(1, 2):uint(), 32767)]] .. ")")
    
  -- 247c -- CLIENT_OPCODE_TAKE_GROUND_ITEM
  elseif (clientOpcodeValue == 247) then
    -- standalone, doesn't require data from other opcodes
    opcodeField:add(clientTakeGroundItemXCoord, buffer(1, 2))
    opcodeField:add(clientTakeGroundItemYCoord, buffer(3, 2))
    local itemField = opcodeField:add(clientTakeGroundItemItemID, buffer(5, 2))
    itemField:append_text(" (" .. ITEM_NAMES[buffer(5, 2):uint()] .. ")")
    
  -- 249c -- CLIENT_OPCODE_CAST_ON_GROUND_ITEM
  elseif (clientOpcodeValue == 249) then
    -- standalone, doesn't require data from other opcodes
    opcodeField:add(clientCastOnGroundItemXCoord, buffer(1, 2))
    opcodeField:add(clientCastOnGroundItemYCoord, buffer(3, 2))
    local itemField = opcodeField:add(clientCastOnGroundItemItemID, buffer(5, 2))
    itemField:append_text(" (" .. ITEM_NAMES[buffer(5, 2):uint()] .. ")")
    local spellField = opcodeField:add(clientCastOnGroundItemSpellID, buffer(7, 2))
    spellField:append_text(" (" .. SPELL_NAMES[buffer(7, 2):int()] .. ")")

  -- 254c -- CLIENT_OPCODE_DISABLE_PRAYER
  elseif (clientOpcodeValue == 254) then
    -- standalone, doesn't require data from other opcodes
    local prayerField = opcodeField:add(clientPrayerOffPrayer, buffer(1, 1))
    prayerField:append_text(" (" .. PRAYER_NAMES[buffer(1, 1):uint()] .. ")")
  end
end

function addOpcodeDataServer(opcode, tree, buffer, pinfoNumber, pinfoVisited)
  if (opcode == VIRTUAL_OPCODE_CONNECT) then
    local loginResponse = buffer(0, 1)
    tree:add(serverLoginResponse, loginResponse)
   elseif (opcode == VIRTUAL_OPCODE_SERVER_METADATA) then
      tree:add(clientPacketLength, buffer(0, 1))
      dataFields = tree:add(clientOpcode, buffer(1, 1), VIRTUAL_OPCODE_SERVER_METADATA)
      local opcodeName = resolveServerOpcodeName(serverOpcode)
      dataFields:append_text(" (VIRTUAL_OPCODE_SERVER_METADATA)")
      dataFields:add(serverMetadataLoginResponse, buffer(2, 4)) -- Login Response
      dataFields:add(serverMetadataClientVersion, buffer(6, 4)) -- Client Version
      dataFields:add(serverMetadataAuthenticClient, buffer(10, 4)) -- Authentic Client
      dataFields:add(serverMetadataStartTime, buffer(14, 8)) -- Start Time (already in header)
      
      offset = 26
      
      -- Server Name
      local recipientNameBuffer = rsc_getRSCStringBuffer(buffer, offset)
      offset = offset + recipientNameBuffer:len()
      dataFields:add(serverMetadataServerName, recipientNameBuffer, rsc_readRSCString(recipientNameBuffer))
      
      dataFields:add(serverMetadataWorldNumber, buffer(22, 4)) -- World Number
      
      -- Username
      local recipientNameBuffer = rsc_getRSCStringBuffer(buffer, offset)
      offset = offset + recipientNameBuffer:len()
      dataFields:add(serverMetadataUsername, recipientNameBuffer, rsc_readRSCString(recipientNameBuffer))
      
      -- Player's IP Address
      local recipientNameBuffer = rsc_getRSCStringBuffer(buffer, offset)
      offset = offset + recipientNameBuffer:len()
      dataFields:add(serverMetadataPlayerIP, recipientNameBuffer, rsc_readRSCString(recipientNameBuffer))
      
      -- Reserved Bytes
      if (buffer:len() - offset > 0) then
        dataFields:add(serverMetadataReservedBytes, buffer(offset, buffer:len() - offset))
      end
    
  else
    local packetLengthBuffer = rsc_getPacketLengthBuffer(buffer, 0)
    local packetLength = rsc_readPacketLength(packetLengthBuffer)

    -- Offset buffer by length size
    buffer = buffer(packetLengthBuffer:len())

    local serverOpcodeBuffer = buffer(packetOffset, 1)
    local serverOpcode = serverOpcodeBuffer:uint()
    local opcodeName = resolveServerOpcodeName(serverOpcode)
    tree:add(clientPacketLength, packetLengthBuffer, packetLength)
    local opcodeField = tree:add(clientOpcode, serverOpcodeBuffer)
    opcodeField:append_text(" (" .. opcodeName .. ")")

    -- 5 -- SERVER_OPCODE_QUEST_STATUS
    if (serverOpcode == 5) then
      -- standalone, doesn't require data from other opcodes
      for i = 0, 49 do
        local questField = opcodeField:add(serverQuests[i], buffer(i + 1, 1), buffer(i + 1, 1):int())
        if (buffer(i + 1, 1):int() > 0) then
          questField:append_text(" (done)")
        else
          questField:append_text(" (not done)")
        end
      end

    -- 6 -- SERVER_OPCODE_UPDATE_STAKED_ITEMS_OPPONENT
    elseif (serverOpcode == 6) then
      -- standalone, doesn't require data from other opcodes
      local itemCount = buffer(1, 1):int()
      opcodeField:add(serverUpdateStakedItemsOpponentNumberOfItemsStaked, buffer(1, 1))

      local offset = 2
      for i = 0, itemCount - 1 do
        local itemId = buffer(offset, 2):int()
        local itemIdField = opcodeField:add(serverUpdateStakedItemsOpponentItemId, buffer(offset, 2))
        itemIdField:append_text(" (" .. ITEM_NAMES[itemId] .. ")")
        itemIdField:add(serverUpdateStakedItemsOpponentItemStack, buffer(offset + 2, 4))
        offset = offset + 6
      end

    -- 15 -- SERVER_OPCODE_UPDATE_TRADE_ACCEPTANCE
    elseif (serverOpcode == 15) then
      -- standalone, doesn't require data from other opcodes
      local tradeAcceptanceField = opcodeField:add(serverUpdateTradeAcceptance, buffer(1, 1))
      tradeAcceptanceField:append_text(" (" .. boolToEnglish(buffer(1, 1):int()) .. ")")
      tradeAcceptanceField:add("Note: actually in all known replays, this field is always equal to zero.")

    -- 20 -- SERVER_OPCODE_CONFIRM_TRADE
    elseif (serverOpcode == 20) then
      -- standalone, doesn't require data from other opcodes
      local recipientNameBuffer = rsc_getRSCStringBuffer(buffer, 1)
      offset = 1 + recipientNameBuffer:len()
      opcodeField:add(serverConfirmTradeRecipientName, recipientNameBuffer, rsc_readRSCString(recipientNameBuffer))
      
      local itemsReceivedCount = buffer(offset, 1)
      local itemsReceivedCountField = opcodeField:add(serverConfirmTradeItemCount, itemsReceivedCount)
      for i = 0, itemsReceivedCount:int() - 1 do
        local itemId = buffer(offset + 1 + i * 6, 2)
        local itemIdField = itemsReceivedCountField:add(serverConfirmTradeItemID, itemId)
        itemIdField:append_text(" (" .. ITEM_NAMES[itemId:int()] .. ")")
        itemIdField:add(serverConfirmTradeItemStack, buffer(offset + 3 + i * 6, 4))
      end 

      offset = offset + itemsReceivedCount:int() * 6 + 1

      local recipientItemCount = buffer(offset, 1)
      local recipientItemCountField = opcodeField:add(serverConfirmTradeRecipientItemCount, recipientItemCount)
      for i = 0, recipientItemCount:int() - 1 do
        local itemId = buffer(offset + 1 + i * 6, 2)
        local itemIdField = recipientItemCountField:add(serverConfirmTradeRecipientItemID, itemId)
        itemIdField:append_text(" (" .. ITEM_NAMES[itemId:int()] .. ")")
        itemIdField:add(serverConfirmTradeRecipientItemStack, buffer(offset + 3 + i * 6, 4))
      end
      

    -- 25 -- SERVER_OPCODE_FLOOR_SET
    elseif (serverOpcode == 25) then
      -- standalone, doesn't require data from other opcodes
      -- it's actually the opcode that sets many of the values we end up needing to cache
      local localPlayerServerIndexBuffer = buffer(1, 2)
      thisLocalPlayerServerIndex = localPlayerServerIndexBuffer:int()
      local planeWidthBuffer = buffer(3, 2)
      thisPlaneWidth = planeWidthBuffer:int()
      local planeHeightBuffer = buffer(5, 2)
      thisPlaneHeight = planeHeightBuffer:int()
      local planeFloorBuffer = buffer(7, 2)
      thisPlaneFloor = planeFloorBuffer:int()
      local planeDistanceBetweenFloorsBuffer = buffer(9, 2)
      thisPlaneDistanceBetweenFloors = planeDistanceBetweenFloorsBuffer:int()

      thisPlaneHeight = thisPlaneHeight - (thisPlaneFloor * thisPlaneDistanceBetweenFloors)

      opcodeField:add(serverFloorSetLocalPlayerServerIndex, localPlayerServerIndexBuffer)
      opcodeField:add(serverFloorSetPlaneWidth, planeWidthBuffer)
      local planeHeightField = opcodeField:add(serverFloorSetPlaneHeight, planeHeightBuffer, thisPlaneHeight)
      planeHeightField:append_text(" (takes into account the Floor & Distance Between Floors)")
      opcodeField:add(serverFloorSetPlaneFloor, planeFloorBuffer)
      opcodeField:add(serverFloorSetPlaneDistanceBetweenFloors, planeDistanceBetweenFloorsBuffer)

    -- 30 -- SERVER_OPCODE_SYNC_DUEL_SETTINGS
    elseif (serverOpcode == 30) then
      -- standalone, doesn't require data from other opcodes
      local retreatField = opcodeField:add(serverSyncDuelSettingsRetreat, buffer(1, 1))
      retreatField:append_text(" (" .. boolToEnglish(buffer(1, 1):int()) .. ")")

      local magicField = opcodeField:add(serverSyncDuelSettingsMagic, buffer(2, 1))
      magicField:append_text(" (" .. boolToEnglish(buffer(2, 1):int()) .. ")")

      local prayerField = opcodeField:add(serverSyncDuelSettingsPrayer, buffer(3, 1))
      prayerField:append_text(" (" .. boolToEnglish(buffer(3, 1):int()) .. ")")

      local weaponsField = opcodeField:add(serverSyncDuelSettingsWeapons, buffer(4, 1))
      weaponsField:append_text(" (" .. boolToEnglish(buffer(4, 1):int()) .. ")")

    -- 33 -- SERVER_OPCODE_UPDATE_XP
    elseif (serverOpcode == 33) then
      -- standalone, doesn't require data from other opcodes
      local skillField = opcodeField:add(serverUpdateXPSkill, buffer(1, 1))
      skillField:append_text(" (" .. SKILL_NAMES[buffer(1, 1):int()] .. ")")
      opcodeField:add(serverUpdateXPXP, buffer(2, 4), buffer(2, 4):int() / 4)

    -- 36 -- SERVER_OPCODE_DISPLAY_TELEPORT_BUBBLE
    elseif (serverOpcode == 36) then
      -- not standalone
      -- requires thisLocalRegionX and thisLocalRegionY
      -- load / save those values
      if pinfoVisited then
        thisLocalRegionX = thisGameStateValuesAtThisTime[pinfoNumber][1]
        thisLocalRegionY = thisGameStateValuesAtThisTime[pinfoNumber][2]
      else 
        thisGameStateValuesAtThisTime[pinfoNumber] = { thisLocalRegionX, thisLocalRegionY }
      end

      -- parse opcode data
      for i = 0, math.floor(buffer:len() / 3) - 1 do
        local bubbleTypeBuffer = buffer(1 + i * 3, 1)
        local bubbleTypeField = opcodeField:add(serverDisplayTeleportBubbleType, bubbleTypeBuffer)

        bubbleTypeField:append_text(" (" .. BUBBLE_TYPE_NAMES[bubbleTypeBuffer:int()] .. ")")

        opcodeField:add(serverDisplayTeleportBubbleXCoord, buffer(2 + i * 3, 1), buffer(2 + i * 3, 1):int() + thisLocalRegionX + 2) -- TODO: investigate why there is + 2
        opcodeField:add(serverDisplayTeleportBubbleYCoord, buffer(3 + i * 3, 1), buffer(3 + i * 3, 1):int() + thisLocalRegionY - 1) -- TODO: investigate why there is - 1
      end

    -- 42 -- SERVER_OPCODE_OPEN_BANK
    elseif (serverOpcode == 42) then
      -- standalone, doesn't require data from other opcodes
      local itemsInBank = buffer(1, 1)
      local itemsMaxInBank = buffer(2, 1)
      local offset = 3
      local itemsInBankField = opcodeField:add(serverOpenBankItemCount, itemsInBank)
      local maxField = opcodeField:add(serverOpenBankMaximumItemCount, itemsMaxInBank)
      maxField:add("Note: This value is always 192, would be 48 on a F2P world if they existed")
      for i = 0, itemsInBank:uint() - 1 do
        local itemIdBuffer = buffer(offset, 2)
        local itemIdField = itemsInBankField:add(serverOpenBankItemID, itemIdBuffer)
        itemIdField:append_text(" (" .. ITEM_NAMES[itemIdBuffer:int()] .. ")")
        offset = offset + 2

        local getIntCache = rsc_getUnsignedInt3(buffer, offset)
        offset = offset + getIntCache[1]:len() -- 2 or 4 long
        itemIdField:add(serverOpenBankItemsInStack, getIntCache[1], getIntCache[2])
      end

    -- 48 -- SERVER_OPCODE_SCENERY_HANDLER
    elseif (serverOpcode == 48) then
      -- not standalone
      -- requires thisLocalRegionX and thisLocalRegionY
      -- load / save those values
      if pinfoVisited then
        thisLocalRegionX = thisGameStateValuesAtThisTime[pinfoNumber][1]
        thisLocalRegionY = thisGameStateValuesAtThisTime[pinfoNumber][2]
      else 
        thisGameStateValuesAtThisTime[pinfoNumber] = { thisLocalRegionX, thisLocalRegionY }
      end
      
      -- TODO: should put "data from previous opcodes" field here

      local offset = 1
      while buffer:len() > offset + 1 do
        local removeModeField = opcodeField:add(serverObjectHandlerRemoveMode, buffer(offset, 1)) -- shares the first byte with objectIDField
        if buffer(offset, 1):uint() == 255 then
          removeModeField:append_text(" (In remove mode)")
          removeModeField:add(serverObjectHandlerXCoordinateOffset, buffer(offset + 1, 1), (buffer(offset + 1, 1):int() / 8) + thisLocalRegionX)
          removeModeField:add(serverObjectHandlerYCoordinateOffset, buffer(offset + 2, 1), (buffer(offset + 2, 1):int() / 8) + thisLocalRegionY)
          offset = offset + 3
        else
          removeModeField:append_text(" (Adding ")
          local objectIDField = removeModeField:add(serverObjectHandlerObjectID, buffer(offset, 2))
          local objectName = SCENERY_NAMES[buffer(offset, 2):uint()]
          removeModeField:append_text(objectName .. " scenery)")
          objectIDField:append_text(" (" .. objectName .. ")")
          removeModeField:add(serverObjectHandlerXCoordinateOffset, buffer(offset + 2, 1), buffer(offset + 2, 1):int() + thisLocalRegionX)
          removeModeField:add(serverObjectHandlerYCoordinateOffset, buffer(offset + 3, 1), buffer(offset + 3, 1):int() + thisLocalRegionY)
          offset = offset + 4
        end
      end

    -- 51 -- SERVER_OPCODE_PRIVACY_SETTINGS
    elseif (serverOpcode == 51) then
      -- standalone, doesn't require data from other opcodes
      local blockChat = buffer(1, 1)
      local blockPrivate = buffer(2, 1)
      local blockTrade = buffer(3, 1)
      local blockDuel = buffer(4, 1)
      local field = opcodeField:add(serverBlockChat, blockChat)
      if (blockChat:uint() == 0) then
        field:append_text(" (Off)")
      else
        field:append_text(" (On)")
      end
      field = opcodeField:add(serverBlockPrivate, blockPrivate)
      if (blockPrivate:uint() == 0) then
        field:append_text(" (Off)")
      else
        field:append_text(" (On)")
      end
      field = opcodeField:add(serverBlockTrade, blockTrade)
      if (blockTrade:uint() == 0) then
        field:append_text(" (Off)")
      else
        field:append_text(" (On)")
      end

      field = opcodeField:add(serverBlockDuel, blockDuel)
      if (blockDuel:uint() == 0) then
        field:append_text(" (Off)")
      else
        field:append_text(" (On)")
      end

    -- 52 -- SERVER_OPCODE_UPDATE_SYSTEM_UPDATE_TIMER
    elseif (serverOpcode == 52) then
      -- standalone, doesn't require data from other opcodes
      local systemUpdateTimerBuffer = buffer(1, 2)
      local systemUpdateTimer = (buffer(1, 2):int() * 32) / 50
      local minutes = math.floor(systemUpdateTimer / 60)
      local seconds = systemUpdateTimer % 60

      local systemUpdateTimerField = opcodeField:add(serverSystemUpdateTimer, systemUpdateTimerBuffer, systemUpdateTimer)
      if seconds < 10 then
        systemUpdateTimerField:append_text(" (" .. minutes .. ":0" .. seconds .. ")")
      else
        systemUpdateTimerField:append_text(" (" .. minutes .. ":" .. seconds .. ")")
      end

    -- 53 -- SERVER_OPCODE_SET_INVENTORY
    elseif (serverOpcode == 53) then
      -- standalone, doesn't require data from other opcodes
      local inventoryItemsCount = buffer(1, 1)
      opcodeField:add(serverInventoryItemsCount, inventoryItemsCount)
      offset = 2
      for index = 0, inventoryItemsCount:uint() - 1, 1 do
        local itemInfo = buffer(offset, 2)
        local equipped = math.floor(itemInfo:uint() / 32768) -- the first bit of itemInfo is for equipped
        local itemId = bit.band(itemInfo:uint(), 32767) -- the rightmost 15 bits are for item ID
        
        thisPlayerInventory[index] = itemInfo:uint()

        local itemIdField = opcodeField:add(serverItemId, itemInfo, itemId)
        itemIdField:append_text(" (" .. ITEM_NAMES[itemId] .. ")")
        if (ITEM_WEILDABLE[itemId] or equipped == 1) then
          local equippedField = itemIdField:add(serverItemEquipped, buffer(offset, 1), equipped)
          equippedField:append_text(" (" .. boolToEnglish(equipped) .. ")")
        end
        offset = offset + 2

        local itemCount
        local itemCountValue
        if (ITEM_STACKABLE[itemId]) then
          local getIntCache = rsc_getUnsignedInt3(buffer, offset)
          offset = offset + getIntCache[1]:len()
          itemIdField:add(serverItemCount, getIntCache[1], getIntCache[2])
        else
          itemCountValue = 1
        end
      end

    -- 79 -- SERVER_OPCODE_NPC_COORDS
    elseif (serverOpcode == 79) then
      -- not standalone
      -- requires thisLocalRegionX and thisLocalRegionY
      -- load / save those values
      if pinfoVisited then
        thisLocalRegionX = thisGameStateValuesAtThisTime[pinfoNumber][1]
        thisLocalRegionY = thisGameStateValuesAtThisTime[pinfoNumber][2]
        thisNpcs = dereferenceList(thisGameStateValuesAtThisTime[pinfoNumber][3])
        thisNpcsServer = dereferenceList(thisGameStateValuesAtThisTime[pinfoNumber][4])
        thisNpcsCache = dereferenceList(thisGameStateValuesAtThisTime[pinfoNumber][5])
        thisNpcsCount = thisGameStateValuesAtThisTime[pinfoNumber][6]
      else
        thisGameStateValuesAtThisTime[pinfoNumber] = { thisLocalRegionX, thisLocalRegionY,
        dereferenceList(thisNpcs), dereferenceList(thisNpcsServer), dereferenceList(thisNpcsCache), thisNpcsCount }
      end

      local initialStateField = opcodeField:add("Necessary Variables From Previous Opcodes")
      initialStateField:add("Local Player's X Coordinate: " .. thisLocalRegionX)
      initialStateField:add("Local Player's Y Coordinate: " .. thisLocalRegionY)
      
      for index = 0, thisNpcsCount - 1 do
        thisNpcsCache = {}
        for key, value in next, thisNpcs do
          if key <= index then
            thisNpcsCache[key] = value
          end
        end
      end

      thisNpcsCount = 0

      local npcCountBuffer = buffer(1, 1)
      local npcCount = npcCountBuffer:uint()
      opcodeField:add(serverNPCCoordsCount, npcCountBuffer)

      local bitOffset = 8
      if npcCount > 0 then
        npcAnimationUpdatesField = opcodeField:add("Animation & Coordinate Updates")
      end
      for index = 0, npcCount - 1 do
        local reqUpdate = rsc_getBitMask(1, bitOffset, buffer)
        local reqUpdateField = npcAnimationUpdatesField:add(serverNPCCoordsReqUpdate, workingBuffer, reqUpdate == 1)
        bitOffset = bitOffset + 1
        
        if (thisNpcsCache[index] == nil) then
            if thisDebugging then
              debugWindow:append("Warning! encountered nil NPC on pinfo.number: ")
              debugWindow:append(pinfo.number)
              debugWindow:append("\n")
            end
          thisNpcsCache[index] = {}
          thisNpcsCache[index]["currentX"] = 0
          thisNpcsCache[index]["currentY"] = 0
          thisNpcsCache[index]["animationNext"] = 0
        end

        if reqUpdate ~= 0 then
          local updateType = rsc_getBitMask(1, bitOffset, buffer)
          local typeField = reqUpdateField:add(serverNPCCoordsUpdateType, workingBuffer, updateType)
          bitOffset = bitOffset + 1
  
          if updateType ~= 0 then -- Animation update
            typeField:append_text(" (Stationary)")
            local animationNextFragment = rsc_getBitMask(2, bitOffset, buffer)
            if animationNextFragment ~= 3 then
              thisNpcsCache[index]["animationNext"] = rsc_getBitMask(4, bitOffset, buffer)
              bitOffset = bitOffset + 4
              local animUpdateField = reqUpdateField:add(serverNPCCoordsAnimationUpdate, workingBuffer, thisNpcsCache[index]["animationNext"])
              animUpdateField:append_text(" (" .. ANIMATION_NAMES[thisNpcsCache[index]["animationNext"]] .. ")")
            else
              reqUpdateField:append_text(" (Remove NPC)")
              bitOffset = bitOffset + 2
              thisNpcsCache[index]["removed"] = true
              goto continue -- lol
            end
          else -- Animation update (while walking, adds to coordinates based on animation direction)
            typeField:append_text(" (Moving)")
            thisNpcsCache[index]["animationNext"] = rsc_getBitMask(3, bitOffset, buffer)
            bitOffset = bitOffset + 3
            
            if (thisNpcsCache[index]["animationNext"] == 1 or thisNpcsCache[index]["animationNext"] == 2 or thisNpcsCache[index]["animationNext"] == 3) then
              thisNpcsCache[index]["currentX"] = thisNpcsCache[index]["currentX"] + 1
            else
              if (thisNpcsCache[index]["animationNext"] == 5 or thisNpcsCache[index]["animationNext"] == 6 or thisNpcsCache[index]["animationNext"] == 7) then
                thisNpcsCache[index]["currentX"] = thisNpcsCache[index]["currentX"] - 1
              end
            end
            if (thisNpcsCache[index]["animationNext"] == 3 or thisNpcsCache[index]["animationNext"] == 4 or thisNpcsCache[index]["animationNext"] == 5) then
              thisNpcsCache[index]["currentY"] = thisNpcsCache[index]["currentY"] + 1
            else
              if (thisNpcsCache[index]["animationNext"] == 0 or thisNpcsCache[index]["animationNext"] == 1 or thisNpcsCache[index]["animationNext"] == 7) then
                thisNpcsCache[index]["currentY"] = thisNpcsCache[index]["currentY"] - 1
              end
            end
            
            local animUpdateField = reqUpdateField:add(serverPlayerCoordsAnimationUpdate, workingBuffer, thisNpcsCache[index]["animationNext"])
            animUpdateField:append_text(" (" .. ANIMATION_NAMES[thisNpcsCache[index]["animationNext"]] .. ")")
          end
        end
        
        thisNpcs[thisNpcsCount] = thisNpcsCache[index]
        thisNpcsCount = thisNpcsCount + 1
        ::continue::
        if (thisNpcsCache[index] ~= nil) then
          if (thisNpcsCache[index]["npcId"] ~= nil) then
            reqUpdateField:prepend_text("(index: " .. thisNpcsCache[index]["serverIndex"] .. " aka "  .. NPC_NAMES[thisNpcsCache[index]["npcId"]] .. "@(" .. thisNpcsCache[index]["currentX"] .. "," ..  thisNpcsCache[index]["currentY"] .. ")) ")
          else 
            reqUpdateField:prepend_text("(index: " .. index .. " (unknown npc aka bug))) ")
          end
        end
      end

      if (packetLength * 8 > bitOffset + 34) then
        newNpcsField = opcodeField:add("New NPC or Coordinate Refresh")
        local newNpcCount = math.floor((packetLength * 8 - bitOffset) /  36)
        newNpcsField:prepend_text(newNpcCount .. " ")
        if newNpcCount ~= 1 then
          newNpcsField:append_text("s")
        end
      end
      while (packetLength * 8 > bitOffset + 34) do
        local serverIndex = rsc_getBitMask(12, bitOffset, buffer)
        local newNpcsServerIndexField = newNpcsField:add(serverNPCCoordsServerIndex, workingBuffer, serverIndex)
        bitOffset = bitOffset + 12

        local areaX = rsc_getBitMask(5, bitOffset, buffer)
        if areaX > 15 then
          areaX = areaX - 32
        end
        local x = thisLocalRegionX + areaX
        newNpcsServerIndexField:add(serverNPCCoordsXCoordinate, workingBuffer, x)
        bitOffset = bitOffset + 5

        local areaY = rsc_getBitMask(5, bitOffset, buffer)
        if areaY > 15 then
          areaY = areaY - 32
        end
        local y = thisLocalRegionY + areaY
        newNpcsServerIndexField:add(serverNPCCoordsYCoordinate, workingBuffer, y)
        bitOffset = bitOffset + 5

        local anim = rsc_getBitMask(4, bitOffset, buffer)
        local newNpcsServerIndexFieldAnimField = newNpcsServerIndexField:add(serverNPCCoordsAnimation, workingBuffer, anim)
        bitOffset = bitOffset + 4
        newNpcsServerIndexFieldAnimField:append_text(" (" .. ANIMATION_NAMES[anim] .. ")")

        local npcId = rsc_getBitMask(10, bitOffset, buffer)
        local newNpcsServerIndexFieldNPCIDField = newNpcsServerIndexField:add(serverNPCCoordsNPCID, workingBuffer, npcId)
        bitOffset = bitOffset + 10
        newNpcsServerIndexFieldNPCIDField:append_text(" (" .. NPC_NAMES[npcId] .. ")")
        
        local found = false
        for findIndex = 0, thisNpcsCount - 1 do
          if (thisNpcsCache[findIndex] ~= nil) then 
            if (serverIndex == thisNpcsCache[findIndex]["serverIndex"]) then
              if (thisNpcsCache[findIndex]["removed"] == false) then
                found = true
              end
            end
          end
        end
        
        thisNpcs[thisNpcsCount] = rsc_createNPC(serverIndex, y, x, animation, npcId)
        
        if (found == false) then
          thisNpcsCount = thisNpcsCount + 1
        end        

      end

    -- 87 -- SERVER_OPCODE_SEND_PM
    elseif (serverOpcode == 87) then
      -- standalone, doesn't require data from other opcodes
      local senderBuffer = rsc_getRSCStringBuffer(buffer, 1)
      offset = senderBuffer:len() + 1
      opcodeField:add(serverSendPrivateMessageRecipient, senderBuffer, rsc_readRSCString(senderBuffer))

      local method220Cache = method220(buffer, offset)
      local messageLengthBuffer = method220Cache[1]
      local messageLength = method220Cache[2]
      opcodeField:add(serverSendPrivateMessageLength, messageLengthBuffer, messageLength)
      offset = offset + method220Cache[3]

      local method240Cache = method240(buffer, 0, offset, messageLength)
      local scrambledChatMessageBufferLength = method240Cache[1]
      local chatMessageInts = method240Cache[2]
      opcodeField:add(serverSendPrivateMessageMessage, buffer(offset, scrambledChatMessageBufferLength), byteArrayToString(chatMessageInts))
      offset = offset + scrambledChatMessageBufferLength

    -- 89 -- SERVER_OPCODE_SHOW_DIALOGUE_SERVER_MESSAGE_TOP
    elseif (serverOpcode == 89) then
      -- standalone, doesn't require data from other opcodes
      local messageBuffer = rsc_getRSCStringBuffer(buffer, 1)
      opcodeField:add(serverShowDialogueServerMessageTopMessage, messageBuffer, rsc_readRSCString(messageBuffer))

    -- 90 -- SERVER_OPCODE_SET_INVENTORY_SLOT
    elseif (serverOpcode == 90) then
      -- standalone, doesn't require data from other opcodes
      local slotBuffer = buffer(1, 1)
      local slotField = opcodeField:add(serverSetInventorySlotSlot, slotBuffer)

      local itemIDAndEquippedBuffer = buffer(2, 2)
      local equipped = math.floor(itemIDAndEquippedBuffer:uint() / 32768) -- the first bit of itemInfo is for equipped
      local itemID = bit.band(itemIDAndEquippedBuffer:uint(), 32767) -- the rightmost 15 bits are for item ID
      slotField:add(serverSetInventorySlotItemEquipped, buffer(2, 1), equipped)
      local itemIDField = slotField:add(serverSetInventorySlotItemID, itemIDAndEquippedBuffer, itemID)
      itemIDField:append_text(" (" .. ITEM_NAMES[itemID] .. ")")
      
      thisPlayerInventory[slotBuffer:uint()] = itemIDAndEquippedBuffer:uint()

      if (ITEM_STACKABLE[itemID]) then
        local getItemStack = rsc_getUnsignedInt3(buffer, 4)
        slotField:add(serverSetInventorySlotItemStackAmount, getItemStack[1], getItemStack[2])
      end

    -- 91 -- SERVER_OPCODE_BOUNDARY_HANDLER
    elseif (serverOpcode == 91) then
      -- not standalone
      -- requires thisLocalRegionX and thisLocalRegionY
      -- load / save those values
      if pinfoVisited then
        thisLocalRegionX = thisGameStateValuesAtThisTime[pinfoNumber][1]
        thisLocalRegionY = thisGameStateValuesAtThisTime[pinfoNumber][2]
      else 
        thisGameStateValuesAtThisTime[pinfoNumber] = { thisLocalRegionX, thisLocalRegionY }
      end

      local offset = 1
      while buffer:len() > offset + 1 do
        local removeModeField = opcodeField:add(serverBoundaryHandlerRemoveMode, buffer(offset, 1)) -- shares the first byte with objectIDField
        if buffer(offset, 1):uint() == 255 then
          removeModeField:append_text(" (In remove mode, removes 8x8 chunks at a time)")
          removeModeField:add(serverBoundaryHandlerXCoordinateOffset, buffer(offset + 1, 1), (buffer(offset + 1, 1):int() / 8) + thisLocalRegionX)
          removeModeField:add(serverBoundaryHandlerYCoordinateOffset, buffer(offset + 2, 1), (buffer(offset + 2, 1):int() / 8) + thisLocalRegionY)
          offset = offset + 3
        else
          removeModeField:append_text(" (Adding ")
          local objectIDField = removeModeField:add(serverBoundaryHandlerObjectID, buffer(offset, 2))
          local objectName = BOUNDARY_NAMES[buffer(offset, 2):uint()]
          removeModeField:append_text(objectName .. " object)")
          objectIDField:append_text(" (" .. objectName .. ")")
          removeModeField:add(serverBoundaryHandlerXCoordinateOffset, buffer(offset + 2, 1), buffer(offset + 2, 1):int() + thisLocalRegionX)
          removeModeField:add(serverBoundaryHandlerYCoordinateOffset, buffer(offset + 3, 1), buffer(offset + 3, 1):int() + thisLocalRegionY)
          local alignmentField = removeModeField:add(serverBoundaryHandlerAlignment, buffer(offset + 4, 1))
          alignmentField:append_text(" (" .. BOUNDARY_ALIGNMENTS[buffer(offset + 4, 1):int()] .. ")")
          offset = offset + 5
        end
      end

    -- 92 -- SERVER_OPCODE_INITIATE_TRADE
    elseif (serverOpcode == 92) then
      -- not standalone, (unless you're okay not knowing the username you're trading with)
      -- load / save player display name
      if pinfoVisited then
        thisPlayerServer[buffer(1, 2):int()]["displayName"] = thisGameStateValuesAtThisTime[pinfoNumber]
      else
        thisGameStateValuesAtThisTime[pinfoNumber] = thisPlayerServer[buffer(1, 2):int()]["displayName"]
      end

      opcodeField:add(serverInitiateTradePID, buffer(1, 2))
      opcodeField:add(serverInitiateTradeUsername, buffer(1, 2), thisPlayerServer[buffer(1, 2):int()]["displayName"])

    -- 97 -- SERVER_OPCODE_UPDATE_ITEMS_TRADED_TO_YOU
    elseif (serverOpcode == 97) then
      -- standalone, doesn't require data from other opcodes
      local itemCount = buffer(1, 1):int()
      opcodeField:add(serverUpdateItemsTradedToYouItemCount, buffer(1, 1))
      local offset = 2
      for i = 0, itemCount - 1 do
        local itemId = buffer(offset, 2):int()
        local itemIdField = opcodeField:add(serverUpdateItemsTradedToYouItemID, buffer(offset, 2))
        itemIdField:append_text(" (" .. ITEM_NAMES[itemId] .. ")")
        itemIdField:add(serverUpdateItemsTradedToYouItemAmount, buffer(offset + 2, 4))
        offset = offset + 6
      end

    -- 99 -- SERVER_OPCODE_GROUND_ITEM_HANDLER
    elseif (serverOpcode == 99) then
      -- not standalone
      -- requires thisLocalRegionX and thisLocalRegionY
      -- load / save those values
      if pinfoVisited then
        thisLocalRegionX = thisGameStateValuesAtThisTime[pinfoNumber][1]
        thisLocalRegionY = thisGameStateValuesAtThisTime[pinfoNumber][2]
      else 
        thisGameStateValuesAtThisTime[pinfoNumber] = { thisLocalRegionX, thisLocalRegionY }
      end
      
      -- TODO: should put "data from previous opcodes" field here
      
      local offset = 1
      while buffer:len() > offset + 1 do
        local removeModeField = opcodeField:add(serverGroundItemHandlerRemoveMode, buffer(offset, 1)) -- shares the first byte with objectIDField
        if buffer(offset, 1):uint() == 255 then
          removeModeField:append_text(" (In remove mode)")
          removeModeField:add(serverGroundItemHandlerXCoordinateOffset, buffer(offset + 1, 1), (buffer(offset + 1, 1):int() / 8) + thisLocalRegionX)
          removeModeField:add(serverGroundItemHandlerYCoordinateOffset, buffer(offset + 2, 1), (buffer(offset + 2, 1):int() / 8) + thisLocalRegionY)
          offset = offset + 3
        else
          
          local objectIDField = removeModeField:add(serverGroundItemHandlerItemID, buffer(offset, 2))
          -- First bit being on means that it is removing a specific item ID from that location
          local removing = math.floor(thisPlayerInventory[buffer(1, 1):uint()] / 32768) 
          local objectName = ITEM_NAMES[buffer(offset, 2):uint() % 32767]
          if (removing == 1) then
            removeModeField:append_text(" (Removing ")
            removeModeField:append_text(objectName .. " from the ground)")          
          else 
            removeModeField:append_text(" (Adding ")
            removeModeField:append_text(objectName .. " to the ground)")
          end
          objectIDField:append_text(" (" .. objectName .. ")")
          removeModeField:add(serverGroundItemHandlerXCoordinateOffset, buffer(offset + 2, 1), buffer(offset + 2, 1):int() + thisLocalRegionX)
          removeModeField:add(serverGroundItemHandlerYCoordinateOffset, buffer(offset + 3, 1), buffer(offset + 3, 1):int() + thisLocalRegionY)
          offset = offset + 4
        end
      end

    -- 101 -- SERVER_OPCODE_SHOW_SHOP
    elseif(serverOpcode == 101) then
      -- standalone, doesn't require data from other opcodes
      local itemCountField = opcodeField:add(serverShowShopItemCount, buffer(1, 1))
      local shopTypeField = opcodeField:add(serverShowShopType, buffer(2, 1))
      if buffer(2, 1):int() == 1 then
        shopTypeField:append_text(" (general store)")
      else
        shopTypeField:append_text(" (specialty store)")
      end

      local sellGenerosityField = opcodeField:add(serverShowShopSellPriceGenerosity, buffer(3, 1))
      local shopSellGenerosity = buffer(3, 1):uint()
      sellGenerosityField:append_text("   (" .. shopSellGenerosity / 100 .. " * base_item_value)")

      local buyGenerosityField = opcodeField:add(serverShowShopBuyPriceGenerosity, buffer(4, 1))
      local shopBuyGenerosity = buffer(4, 1):uint()
      buyGenerosityField:append_text("   (" .. shopBuyGenerosity / 100 .. " * base_item_value)")

      opcodeField:add(serverShowShopStockEffect, buffer(5, 1))
      local stockEffectValue = buffer(5, 1):uint()

      for i = 0, buffer(1, 1):int() - 1 do
        local itemIDBuffer = buffer(6 + i * 6, 2)
        local itemIDField = itemCountField:add(serverShowShopItemID, itemIDBuffer)
        local itemID = itemIDBuffer:uint()
        itemIDField:append_text(" (" .. ITEM_NAMES[itemID] ..")")

        local amountBuffer = buffer(8 + i * 6, 2)
        local amountInStock = amountBuffer:uint() 
        local amountField = itemIDField:add(serverShowShopItemStackAmount, amountBuffer)

        local baseAmountInStockBuffer = buffer(10 + i * 6, 2)
        local baseAmountField = itemIDField:add(serverShowShopBaseAmountInStock, baseAmountInStockBuffer)
        local baseAmountInStock = baseAmountInStockBuffer:uint()

        local sellValue = calculateShopItemPrice(ITEM_BASE_PRICE[itemID], amountInStock, baseAmountInStock, stockEffectValue, shopSellGenerosity)
        local buyValue  = calculateShopItemPrice(ITEM_BASE_PRICE[itemID], amountInStock, baseAmountInStock, stockEffectValue, shopBuyGenerosity)

        local calculatedField = itemIDField:add("Client-Calculated Price Data")
        calculatedField:add("Base Item Value (from cache): " .. ITEM_BASE_PRICE[itemID] .. " gp")
        calculatedField:add("Sell Value (calculated): " .. sellValue .. " gp")
        calculatedField:add("Buy Value (calculated): " .. buyValue .. " gp")
      end

    -- 104 -- SERVER_OPCODE_UPDATE_NPC
    elseif (serverOpcode == 104) then
      -- not standalone
      -- requires thisLocalRegionX and thisLocalRegionY
      -- load / save those values
      if pinfoVisited then
        thisNpcsServer = dereferenceList(thisGameStateValuesAtThisTime[pinfoNumber][1])
        thisPlayerServer = dereferenceList(thisGameStateValuesAtThisTime[pinfoNumber][2])
      else
        thisGameStateValuesAtThisTime[pinfoNumber] = { dereferenceList(thisNpcsServer), dereferenceList(thisPlayerServer) }
      end

      local npcCountBuffer = buffer(1, 2)
      local npcCount = npcCountBuffer:uint()
      local npcCountField = opcodeField:add(serverUpdateNPCNPCCount, npcCountBuffer)
      
      if thisDebugging then
        local debugField = opcodeField:add("DEBUG")
        for key, value in next, thisNpcsServer do
          if thisNpcsServer[key]["serverIndex"] then
            debugField:add("serverIndex: " .. key)
          end
        end
      end

      local offset = 3
      for index = 0, npcCount - 1 do
        local serverIndexBuffer = buffer(offset, 2)
        local serverIndexField = npcCountField:add(serverUpdateNPCNPCServerIndex, serverIndexBuffer)
        offset = offset + 2
        if (thisNpcsServer[serverIndexBuffer:uint()] ~= nil) then 
          serverIndexField:append_text(" (" .. NPC_NAMES[thisNpcsServer[serverIndexBuffer:uint()]["npcId"]].. ")")
        end

        local updateTypeBuffer = buffer(offset, 1)
        local updateTypeField = serverIndexField:add(serverUpdateNPCUpdateType, updateTypeBuffer)
        offset = offset + 1

        if updateTypeBuffer:uint() == 2 then
          updateTypeField:append_text(" (Combat Update)")
          updateTypeField:add(serverUpdateNPCDamageTaken, buffer(offset, 1))
          updateTypeField:add(serverUpdateNPCCurrentHP, buffer(offset + 1, 1))
          updateTypeField:add(serverUpdateNPCMaxHP, buffer(offset + 2, 1))
          offset = offset + 3
        elseif updateTypeBuffer:uint() == 1 then
          updateTypeField:append_text(" (NPC Chat Message)")

          local playerTalkingToPIDBuffer = buffer(offset, 2)
          local playerPIDField = updateTypeField:add(serverUpdateNPCPID, playerTalkingToPIDBuffer)
          playerPIDField:append_text(" (" .. thisPlayerServer[playerTalkingToPIDBuffer:uint()]["displayName"] .. ")")
          offset = offset + 2

          local method220Cache = method220(buffer, offset)
          local messageLengthBuffer = method220Cache[1]
          local messageLength = method220Cache[2]
          updateTypeField:add(serverUpdateNPCMessageLength, messageLengthBuffer, messageLength)
          offset = offset + method220Cache[3]

          local method240Cache = method240(buffer, 0, offset, messageLength)
          local scrambledChatMessageBufferLength = method240Cache[1]
          local chatMessageInts = method240Cache[2]
          updateTypeField:add(serverUpdateNPCMessage, buffer(offset, scrambledChatMessageBufferLength), byteArrayToString(chatMessageInts))
          offset = offset + scrambledChatMessageBufferLength
        end
      end

    -- 109 -- SERVER_OPCODE_SET_IGNORE
     elseif (serverOpcode == 109) then
      -- standalone, doesn't require data from other opcodes
      local ignoreListCount = buffer(1, 1):int()
      opcodeField:add(serverSetIgnoreListCount, buffer(1, 1))

      local offset = 2
      for i = 0, ignoreListCount - 1 do
        local ignoreListNameBuffer = rsc_getRSCStringBuffer(buffer, offset)
        offset = offset + ignoreListNameBuffer:len()
        local nameField = opcodeField:add(serverSetIgnoreListName, ignoreListNameBuffer, rsc_readRSCString(ignoreListNameBuffer))

        local ignoreListAccNameBuffer = rsc_getRSCStringBuffer(buffer, offset)
        offset = offset + ignoreListAccNameBuffer:len()
        nameField:add(serverSetIgnoreListAccountName, ignoreListAccNameBuffer, rsc_readRSCString(ignoreListAccNameBuffer))

        local ignoreListOldNameBuffer = rsc_getRSCStringBuffer(buffer, offset)
        offset = offset + ignoreListOldNameBuffer:len()
        nameField:add(serverSetIgnoreListOldName, ignoreListOldNameBuffer, rsc_readRSCString(ignoreListOldNameBuffer))

        local ignoreListServerBuffer = rsc_getRSCStringBuffer(buffer, offset)
        offset = offset + ignoreListServerBuffer:len()
        nameField:add(serverSetIgnoreListServers, ignoreListServerBuffer, rsc_readRSCString(ignoreListServerBuffer))
      end

    -- 111 -- SERVER_OPCODE_COMPLETED_TUTORIAL
    elseif (serverOpcode == 111) then
      -- standalone, doesn't require data from other opcodes
      local field = opcodeField:add(serverJustCompletedTutorial,buffer(1, 1))
      if buffer(1, 1):int() == 0 then
        field:append_text(" (Completed Tutorial)")
      else
        field:append_text(" (In Tutorial)")
      end

    -- 114 -- SERVER_OPCODE_SET_FATIGUE
    elseif (serverOpcode == 114) then  
      -- standalone, doesn't require data from other opcodes
      local field = opcodeField:add(serverSetFatigue, buffer(1,2), buffer(1,2):int()/7.5)
      field:append_text(" (note that this is client fatigue, server fatigue is more precise)")

    -- 117 -- SERVER_OPCODE_FALL_ASLEEP
    elseif (serverOpcode == 117) then
      -- standalone, doesn't require data from other opcodes
      opcodeField:add(serverSleepWordImage, buffer(1,buffer:len() - 1))

      -- TODO: put the player attempted word here, and say if it was successful or not.

      -- TODO: would be fun to show ascii art representation of this image
      -- However, Wireshark truncates text fields a maximum of 79 unicode characters long
      -- Which means we can only get an effective resolution of 158x40, but sleep words are 255x40.
      -- The image could be drawn sideways, but, with the space between lines too, it seems "not worth it."

      -- local pictureField = opcodeField:add("Picture:")
      -- for y = 0, 19 do
      -- local lineField = opcodeField:add("")
      --   for x = 0, 78 do
      --     lineField:append_text("▚")
      --   end
      -- end

    -- 120 -- SERVER_OPCODE_RECEIVE_PM
    elseif (serverOpcode == 120) then
      -- standalone, doesn't require data from other opcodes
      local offset = 1

      local senderBuffer = rsc_getRSCStringBuffer(buffer, offset)
      opcodeField:add(serverReceivePMSender, senderBuffer, rsc_readRSCString(senderBuffer))
      offset = offset + senderBuffer:len()

      -- Confirmed that in all samples, this sender2 is ALWAYS equal to the first sender.
      -- TODO: should try creating replay with sender2 different and see if it is used as former name.
      local senderBuffer2 = rsc_getRSCStringBuffer(buffer, offset)
      opcodeField:add(serverReceivePMSender2, senderBuffer2, rsc_readRSCString(senderBuffer2))
      offset = offset + senderBuffer2:len()

      local modStatus = buffer(offset, 1)
      local modStatusField = opcodeField:add(serverReceivePMModStatus, modStatus)
      if (modStatus:int() == 0) then
        modStatusField:append_text(" (not a mod)")
      elseif (modStatus:int() == 1) then
        modStatusField:append_text(" (pmod)")
      elseif (modStatus:int() == 2) then
        modStatusField:append_text(" (jmod)")
      else
        modStatusField:append_text(" (unknown)")
      end
      offset = offset + 1

      local messageIDField = opcodeField:add(serverReceivePMMessageID, buffer(offset, 8))
      local explanationField = messageIDField:add("Explanation")
      explanationField:add("If the client receives a Private Message with the same Message ID within 100 PMs")
      explanationField:add("of each other, the client will not display the PM. This happens occasionally, possibly")
      explanationField:add("due to a race condition where multiple login servers attempt to handle the same PM.")
      messageIDField:add(serverReceivePMMessageIDWorld, buffer(offset + 3, 2))
      local messageIDIDField = messageIDField:add(serverReceivePMMessageIDID, buffer(offset + 5, 3))
      local explanationField = messageIDIDField:add("Explanation")
      explanationField:add("This number is the number of PMs sent from that world since the world was last rebooted.")
      -- I know this from talking to RuneLite developer abex & having them hook the same field in OSRS, observing that
      -- it was 505514 before an update, and 7, 8, 9, 10, 11, 12 sequentially after the server rebooted for an update.
      offset = offset + 8

      local method220Cache = method220(buffer, offset)
      local messageLengthBuffer = method220Cache[1]
      local messageLength = method220Cache[2]
      opcodeField:add(serverReceivePMMessageLength, messageLengthBuffer, messageLength)
      offset = offset + method220Cache[3]

      local method240Cache = method240(buffer, 0, offset, messageLength)
      local scrambledChatMessageBufferLength = method240Cache[1]
      local chatMessageInts = method240Cache[2]
      opcodeField:add(serverReceivePMMessage, buffer(offset, scrambledChatMessageBufferLength), byteArrayToString(chatMessageInts))
      offset = offset + scrambledChatMessageBufferLength

    -- 123 -- SERVER_OPCODE_REMOVE_INVENTORY_SLOT
    elseif (serverOpcode == 123) then
      -- not standalone (unless you're OK not knowing what was in the slot)
      -- requires inventory state
      if pinfoVisited then
        thisPlayerInventory = dereferenceList(thisGameStateValuesAtThisTime[pinfoNumber][1])
      else
        thisGameStateValuesAtThisTime[pinfoNumber] = { dereferenceList(thisPlayerInventory) }
      end

      local itemRemovedField = opcodeField:add(serverRemoveSlot, buffer(1, 1))
      
      local equipped = math.floor(thisPlayerInventory[buffer(1, 1):uint()] / 32768) -- the first bit of itemInfo is for equipped
      local itemId = bit.band(thisPlayerInventory[buffer(1, 1):uint()], 32767) -- the rightmost 15 bits are for item ID
      itemRemovedField:append_text(" (" .. ITEM_NAMES[itemId] .. ")")
      -- TODO: possibly say if it was equipped or not
      for i = buffer(1, 1):uint() - 1, 29 do
        thisPlayerInventory[i] = thisPlayerInventory[i + 1]
      end

    -- 131 -- SERVER_OPCODE_SEND_MESSAGE
    elseif (serverOpcode == 131) then
      -- standalone, doesn't require data from other opcodes
      local offset = 1

      local messageType = buffer(offset, 1)
      offset = offset + 1
      local messageTypeField = opcodeField:add(serverSendMessageType, messageType)
      messageTypeField:append_text(" (" .. MESSAGE_TYPES[messageType:int()] .. ")")

      local infoContained = buffer(offset, 1)
      offset = offset + 1
      local infoContainedField = opcodeField:add(serverSendMessageInfoContained, infoContained)

      local message = rsc_getRSCStringBuffer(buffer, offset)
      offset = offset + message:len()
      opcodeField:add(serverSendMessageMessage, message, rsc_readRSCString(message))

      local infoContainedEffect = 0

      if (bit.band(infoContained:int(), 1) ~= 0) then
        local sender = rsc_getRSCStringBuffer(buffer, offset)
        offset = offset + sender:len()
        local sender2 = rsc_getRSCStringBuffer(buffer, offset)
        offset = offset + sender2:len()

        opcodeField:add(serverSendMessageSender, sender, rsc_readRSCString(sender))
        opcodeField:add(serverSendMessageSender2, sender2, rsc_readRSCString(sender2))
        infoContainedEffect = infoContainedEffect + 1
      end

      if (bit.band(infoContained:int(),2) ~= 0) then
        local colour = rsc_getRSCStringBuffer(buffer, offset)
        opcodeField:add(serverSendMessageColour, colour, rsc_readRSCString(colour))
        infoContainedEffect = infoContainedEffect + 2
      end

      if infoContainedEffect == 0 then
        infoContainedField:append_text(" (only the message)")
      elseif infoContainedEffect == 1 then
        infoContainedField:append_text(" (message, sender, sender duplicate)")
      elseif infoContainedEffect == 2 then
        infoContainedField:append_text(" (message, colour)")
      elseif infoContainedEffect == 3 then
        infoContainedField:append_text(" (message, sender, sender duplicate, colour)")
      end      

    -- 149 -- SERVER_OPCODE_UPDATE_FRIEND
    elseif (serverOpcode == 149) then
      -- standalone, doesn't require data from other opcodes
      local friendName = rsc_getRSCStringBuffer(buffer, 1)
      local offset = friendName:len() + 1
      local oldFriendName = rsc_getRSCStringBuffer(buffer, offset)
      offset = offset + oldFriendName:len()
      local onlineStatus = buffer(offset, 1)

      local loggedIn = bit.band(onlineStatus:uint(), 4) ~= 0

      opcodeField:add(serverUpdateFriendFriendName, friendName, rsc_readRSCString(friendName))
      opcodeField:add(serverUpdateFriendOldFriendName, oldFriendName, rsc_readRSCString(oldFriendName))
      onlineStatusField = opcodeField:add(serverUpdateFriendOnlineStatus, onlineStatus)

      if (loggedIn) then
        local friendServer = rsc_getRSCStringBuffer(buffer, offset + 1)
        opcodeField:add(serverUpdateFriendFriendServer, friendServer, rscStringCrop(friendServer))
        if (onlineStatus:uint() == 4) then
          onlineStatusField:append_text(" (online different world)")
        else
          onlineStatusField:append_text(" (online same world)")
        end
      else
        onlineStatusField:append_text(" (offline)")
      end

    -- 153 -- SERVER_OPCODE_SET_EQUIP_STATS
    elseif (serverOpcode == 153) then
      -- standalone, doesn't require data from other opcodes
      opcodeField:add(serverSetEquipmentStatsArmour, buffer(1, 1))
      opcodeField:add(serverSetEquipmentStatsWeaponAim, buffer(2, 1))
      opcodeField:add(serverSetEquipmentStatsWeaponPower, buffer(3, 1))
      opcodeField:add(serverSetEquipmentStatsMagic, buffer(4, 1))
      opcodeField:add(serverSetEquipmentStatsPrayer, buffer(5, 1))

    -- 156 -- SERVER_OPCODE_SET_STATS
    elseif (serverOpcode == 156) then
      -- standalone, doesn't require data from other opcodes
      for i = 0, 17 do
        local skillNameField = opcodeField:add(SKILL_NAMES[i])
        skillNameField:add(serverSetStatsPlayerCurrentStat[i], buffer(i + 1, 1))
        skillNameField:add(serverSetStatsPlayerBaseStat[i], buffer(i + 19, 1))
        local xpBuffer = buffer(i * 4 + 37, 4)
        skillNameField:add(serverSetStatsPlayerXP[i], xpBuffer, xpBuffer:int() / 4)
      end
      opcodeField:add(serverSetStatsPlayerQuestPoints, buffer(109, 1))

    -- 159 -- SERVER_OPCODE_UPDATE_STAT
    elseif (serverOpcode == 159) then
      -- standalone, doesn't require data from other opcodes
      local skillBuffer = buffer(1, 1)
      local skillField = opcodeField:add(serverUpdateStatSkill, skillBuffer)
      skillField:append_text(" (" .. SKILL_NAMES[skillBuffer:int()] .. ")")      
      opcodeField:add(serverUpdateStatPlayerCurrentStat, buffer(2, 1))
      opcodeField:add(serverUpdateStatPlayerBaseStat, buffer(3, 1))
      opcodeField:add(serverUpdateStatExperience, buffer(4, 4), buffer(4, 4):int() / 4)

    -- 162 -- SERVER_OPCODE_UPDATE_TRADE_RECIPIENT_ACCEPTANCE
    elseif (serverOpcode == 162) then
      -- standalone, doesn't require data from other opcodes
      local tradeAcceptanceField = opcodeField:add(serverUpdateTradeRecipientAcceptance, buffer(1, 1))
      if buffer(1, 1):uint() == 1 then
        tradeAcceptanceField:append_text(" (Accepted)")
      else
        tradeAcceptanceField:append_text(" (Not Accepted)")
      end

    -- 172 -- SERVER_OPCODE_SHOW_CONFIRM_DUEL
    elseif (serverOpcode == 172) then
      -- standalone, doesn't require data from other opcodes
      local offset = 1

      local opponentUsernameBuffer = rsc_getRSCStringBuffer(buffer, offset)
      offset = offset + opponentUsernameBuffer:len()
      opcodeField:add(serverShowConfirmDuelOpponentName, opponentUsernameBuffer, rsc_readRSCString(opponentUsernameBuffer))

      local opponentItemCount = buffer(offset, 1):uint()
      local opponentItemCountField = opcodeField:add(serverShowConfirmDuelOpponentItemCount, buffer(offset, 1), opponentItemCount)
      offset = offset + 1
      for index = 0, opponentItemCount - 1 do
        local itemIDField = opponentItemCountField:add(serverShowConfirmDuelOpponentItemID, buffer(offset, 2))
        itemIDField:append_text(" (" .. ITEM_NAMES[buffer(offset ,2):int()] .. ")")
        itemIDField:add(serverShowConfirmDuelOpponentItemStackAmount, buffer(offset + 2, 4))
        offset = offset + 6
      end

      local itemCount = buffer(offset, 1):uint()
      local itemCountField = opcodeField:add(serverShowConfirmDuelItemCount, buffer(offset, 1), itemCount)
      offset = offset + 1
      for index = 0, itemCount - 1 do
        local itemIDField = itemCountField:add(serverShowConfirmDuelItemID, buffer(offset, 2))
        itemIDField:append_text(" (" .. ITEM_NAMES[buffer(offset ,2):int()] .. ")")
        itemIDField:add(serverShowConfirmDuelItemStackAmount, buffer(offset + 2, 4))
        offset = offset + 6
      end

      local retreatField = opcodeField:add(serverShowConfirmDuelRetreat, buffer(offset, 1))
      if buffer(offset, 1):int() == 0 then
        retreatField:append_text(" (No retreat is possible!)")
      else
        retreatField:append_text(" (You can retreat from this duel)")
      end

      local magicField = opcodeField:add(serverShowConfirmDuelMagic, buffer(offset + 1, 1))
      if buffer(offset + 1, 1):int() == 0 then
        magicField:append_text(" (Magic may be used)")
      else
        magicField:append_text(" (Magic cannot be used)")
      end

      local prayerField = opcodeField:add(serverShowConfirmDuelPrayer, buffer(offset + 2, 1))
      if buffer(offset + 2, 1):int() == 0 then
        prayerField:append_text(" (Prayer may be used)")
      else
        prayerField:append_text(" (Prayer cannot be used)")
      end

      local weaponField = opcodeField:add(serverShowConfirmDuelWeapons, buffer(offset + 3, 1))
      if buffer(offset + 3, 1):int() == 0 then
        weaponField:append_text(" (Weapons may be used)")
      else
        weaponField:append_text(" (Weapons cannot be used)")
      end

    -- 176 -- SERVER_OPCODE_SHOW_DIALOGUE_DUEL
    elseif (serverOpcode == 176) then
      -- not standalone, (unless you're okay not knowing the username you're duelling with)
      -- load / save player display name
      if pinfoVisited then
        thisPlayerServer[buffer(1, 2):int()]["displayName"] = thisGameStateValuesAtThisTime[pinfoNumber]
      else
        thisGameStateValuesAtThisTime[pinfoNumber] = thisPlayerServer[buffer(1, 2):int()]["displayName"]
      end

      opcodeField:add(serverShowDialogueDuelPID, buffer(1, 2))
      opcodeField:add(serverShowDialogueDuelUsername, buffer(1, 2), thisPlayerServer[buffer(1, 2):int()]["displayName"])

    -- 182 -- SERVER_OPCODE_SHOW_WELCOME
    elseif (serverOpcode == 182) then
      -- standalone, doesn't require data from other opcodes
      opcodeField:add(serverShowWelcomeIP, buffer(1,4), buffer(1, 1):uint() .. "." .. buffer(2, 1):uint()  .. "." .. buffer(3, 1):uint()  .. "." .. buffer(4, 1):uint())
      local lastLoginField = opcodeField:add(serverShowWelcomeDays, buffer(5, 2))
      if buffer(5, 2):int() == 0 then
        lastLoginField:append_text(" (earlier today)")
      elseif buffer(5, 2):int() == 1 then
        lastLoginField:append_text(" (yesterday)")
      end
      local recoveryDaysField = opcodeField:add(serverShowWelcomeRecoverySetDays, buffer(7, 1))
      if buffer(7, 1):uint() == 200 then
        recoveryDaysField:append_text(" (recovery questions not set)")
      elseif buffer(7, 1):uint() == 201 then
        recoveryDaysField:append_text(" (unspecified days ago)")
      end
      local unreadMessageField = opcodeField:add(serverShowWelcomeUnreadMessages, buffer(8, 2), buffer(8, 2):int() - 1)
      if (buffer(8, 2):int() == 0) then
        unreadMessageField:append_text(" (don't display how many messages there are)")
      else
        unreadMessageField:append_text(" (after subtracting 1 from byte)")
      end

    -- 191 -- SERVER_OPCODE_PLAYER_COORDS
    -- TODO: sometimes there may still be problems with PID not lining up with player name
    elseif (serverOpcode == 191) then
      -- not standalone
      -- requires lots of previously recorded data!
      if pinfoVisited then
        thisPlayers = dereferenceList(thisGameStateValuesAtThisTime[pinfoNumber][1])
        thisPlayerCount = thisGameStateValuesAtThisTime[pinfoNumber][2]
        thisPlayerServer = dereferenceList(thisGameStateValuesAtThisTime[pinfoNumber][3])
      else 
        thisGameStateValuesAtThisTime[pinfoNumber] = dereferencePlayerArrays()
      end

      -- interpret opcode data
      local bitOffset = 0

      thisKnownPlayerCount = thisGameStateValuesAtThisTime[pinfoNumber][2] -- dereferenced thisPlayerCount
      thisKnownPlayers = dereferenceList(thisGameStateValuesAtThisTime[pinfoNumber][1]) -- dereferenced thisPlayers
      thisPlayerCount = 0

      local initialStateField = opcodeField:add("Necessary Variables From Previous Opcodes")
      initialStateField:add("Local Player's PID: " .. thisLocalPlayerServerIndex)
      initialStateField:add("Known Player Count: " .. thisKnownPlayerCount)

      local localPlayerInfoField = opcodeField:add("Local Player Information")

      -- first 3 bytes are shared by localRegion X & Y
      local localPlayerXCoordinate = rsc_getBitMask(11, bitOffset, buffer)
      local localPlayerXField = localPlayerInfoField:add(serverPlayerCoordsLocalRegionX, workingBuffer, localPlayerXCoordinate)
      if not pinfoVisited then
        thisLocalRegionX = localPlayerXCoordinate
      end
      bitOffset = bitOffset + 11

      local localPlayerYCoordinate = rsc_getBitMask(13, bitOffset, buffer)
      local localRegionYField = localPlayerInfoField:add(serverPlayerCoordsLocalRegionY, workingBuffer, localPlayerYCoordinate)
      if not pinfoVisited then
        thisLocalRegionY = localPlayerYCoordinate
      end
      bitOffset = bitOffset + 13

      -- next nibble is animation
      local localPlayerAnimation = rsc_getBitMask(4, bitOffset, buffer)
      local animationField = localPlayerInfoField:add(serverPlayerCoordsAnimation, workingBuffer, localPlayerAnimation)
      animationField:append_text(" (" .. ANIMATION_NAMES[localPlayerAnimation] .. ")")
      bitOffset = bitOffset + 4

      local animationUpdatesField = opcodeField:add("Animation Updates")

      -- this byte is in the middle of the next 2 bytes
      local playerCount = rsc_getBitMask(8, bitOffset, buffer)
      local playerCountField = animationUpdatesField:add(serverPlayerCoordsPlayerCount, workingBuffer, playerCount)
      bitOffset = bitOffset + 8

      -- loadNextRegion() may have this impact on the variables we read in: TODO
      -- regionX = thisLocalRegionX - 24
      -- regionY = thisLocalRegionY - 24

      thisLocalPlayer = rsc_createPlayer(1, thisLocalPlayerServerIndex, localPlayerYCoordinate, localPlayerXCoordinate, localPlayerAnimation)
      thisPlayers[0] = thisLocalPlayer[1]
      -- debugField:add("local player pid: " .. thisPlayers[0]["serverIndex"])

      for index = 0, playerCount - 1 do
        local reqUpdate = rsc_getBitMask(1, bitOffset, buffer)
        local reqUpdateField = animationUpdatesField:add(serverPlayerCoordsAnimationUpdateRequired, workingBuffer, reqUpdate == 1)
        bitOffset = bitOffset + 1

        local relevantPlayerPID = thisKnownPlayers[index + 1]["serverIndex"]

        if reqUpdate ~= 0 then
          local updateType = rsc_getBitMask(1, bitOffset, buffer)
          --bitViewField:add("updateType: " .. updateType) -- TODO: Not really going to expose this at this time because "who cares"
          --TODO: improve the bit-view of these fields, and expose updateType
          bitOffset = bitOffset + 1

          if updateType ~= 0 then -- Animation update
            local animationNextFragment = rsc_getBitMask(2, bitOffset, buffer)
            if animationNextFragment ~= 3 then
              thisKnownPlayers[index + 1]["animationNext"] = rsc_getBitMask(4, bitOffset, buffer)
              bitOffset = bitOffset + 4
              local animUpdateField = reqUpdateField:add(serverPlayerCoordsAnimationUpdate, workingBuffer, thisKnownPlayers[index + 1]["animationNext"])
              animUpdateField:append_text(" (" .. ANIMATION_NAMES[thisKnownPlayers[index + 1]["animationNext"]] .. ")")
            else
              reqUpdateField:append_text(" (remove player)")
              bitOffset = bitOffset + 2
              thisKnownPlayers[index + 1]["removed"] = true
              goto continuePlayer -- lol
            end
          else -- Animation update (while walking)
            thisKnownPlayers[index + 1]["animationNext"] = rsc_getBitMask(3, bitOffset, buffer)
            bitOffset = bitOffset + 3
            local animUpdateField = reqUpdateField:add(serverPlayerCoordsAnimationUpdate, workingBuffer, thisKnownPlayers[index + 1]["animationNext"])
            animUpdateField:append_text(" (" .. ANIMATION_NAMES[thisKnownPlayers[index + 1]["animationNext"]] .. ")")
          end          
        end

        thisPlayers[thisPlayerCount] = thisKnownPlayers[index + 1]
        thisPlayerCount = thisPlayerCount + 1
        ::continuePlayer::
        reqUpdateField:prepend_text("(Username: " .. thisPlayerServer[relevantPlayerPID]["displayName"] .. ") ")
      end

      if (packetLength * 8 > bitOffset + 24) then
        newPlayersField = opcodeField:add("Newly Created Player")
        local newPlayersCount = math.floor((packetLength * 8 - bitOffset) /  25)
        newPlayersField:prepend_text(newPlayersCount .. " ")
        if newPlayersCount ~= 1 then
          newPlayersField:append_text("s")
        end
      end
      while (packetLength * 8 > bitOffset + 24) do
        local serverIndex = rsc_getBitMask(11, bitOffset, buffer)
        local newPlayersPIDField = newPlayersField:add(serverPlayerCoordsNewPlayerPID, workingBuffer, serverIndex)
        bitOffset = bitOffset + 11

        local areaX = rsc_getBitMask(5, bitOffset, buffer)
        if areaX > 15 then
          areaX = areaX - 32
        end
        local x = localPlayerXCoordinate + areaX
        newPlayersPIDField:add(serverPlayerCoordsNewPlayerX, workingBuffer, x)
        bitOffset = bitOffset + 5

        local areaY = rsc_getBitMask(5, bitOffset, buffer)
        if areaY > 15 then
          areaY = areaY - 32
        end
        local y = localPlayerYCoordinate + areaY + thisPlaneFloor * thisPlaneDistanceBetweenFloors
        newPlayersPIDField:add(serverPlayerCoordsNewPlayerY, workingBuffer, y)
        bitOffset = bitOffset + 5

        local anim = rsc_getBitMask(4, bitOffset, buffer)
        local newPlayersPIDFieldAnimField = newPlayersPIDField:add(serverPlayerCoordsNewPlayerAnimation, workingBuffer, anim)
        bitOffset = bitOffset + 4
        newPlayersPIDFieldAnimField:append_text(" (" .. ANIMATION_NAMES[anim] .. ")")

        local potentialNewPlayer = rsc_createPlayer(1, serverIndex, y, x, anim)
        --if potentialNewPlayer[2] == false then -- TODO
          thisPlayers[thisPlayerCount - 1] = potentialNewPlayer[1]
        --end
      end

      -- for i = 0, thisPlayerCount - 1 do -- TODO: remove
      --  debugField:add("pid: " .. thisPlayers[i]["serverIndex"] .. " thisPlayers[".. i .. "][\"displayName\"]:  " .. thisPlayers[i]["displayName"])
      -- end

    -- 204 -- SERVER_OPCODE_PLAY_SOUND
    elseif (serverOpcode == 204) then
      -- standalone, doesn't require data from other opcodes
      local soundBuffer = rsc_getRSCStringBuffer(buffer, 1)
      opcodeField:add(serverSoundName, soundBuffer, rsc_readRSCString(soundBuffer))

    -- 206 -- SERVER_OPCODE_SET_PRAYERS
    elseif (serverOpcode == 206) then
      -- standalone, doesn't require data from other opcodes
      local prayerField = opcodeField:add(serverSetPrayersThickSkin, buffer(1, 1))
      if (buffer(1, 1):int() == 1) then
        prayerField:append_text(" (on)")
      else
        prayerField:append_text(" (off)")
      end
      local prayerField = opcodeField:add(serverSetPrayersBurstOfStrength, buffer(2, 1))
      if (buffer(2, 1):int() == 1) then
        prayerField:append_text(" (on)")
      else
        prayerField:append_text(" (off)")
      end
      local prayerField = opcodeField:add(serverSetPrayersClarityOfThought, buffer(3, 1))
      if (buffer(3, 1):int() == 1) then
        prayerField:append_text(" (on)")
      else
        prayerField:append_text(" (off)")
      end
      local prayerField = opcodeField:add(serverSetPrayersRockSkin, buffer(4, 1))
      if (buffer(4, 1):int() == 1) then
        prayerField:append_text(" (on)")
      else
        prayerField:append_text(" (off)")
      end
      local prayerField = opcodeField:add(serverSetPrayersSuperhumanStrength, buffer(5, 1))
      if (buffer(5, 1):int() == 1) then
        prayerField:append_text(" (on)")
      else
        prayerField:append_text(" (off)")
      end
      local prayerField = opcodeField:add(serverSetPrayersImprovedReflexes, buffer(6, 1))
      if (buffer(6, 1):int() == 1) then
        prayerField:append_text(" (on)")
      else
        prayerField:append_text(" (off)")
      end
      local prayerField = opcodeField:add(serverSetPrayersRapidRestore, buffer(7, 1))
      if (buffer(7, 1):int() == 1) then
        prayerField:append_text(" (on)")
      else
        prayerField:append_text(" (off)")
      end
      local prayerField = opcodeField:add(serverSetPrayersRapidHeal, buffer(8, 1))
      if (buffer(8, 1):int() == 1) then
        prayerField:append_text(" (on)")
      else
        prayerField:append_text(" (off)")
      end
      local prayerField = opcodeField:add(serverSetPrayersProtectItems, buffer(9, 1))
      if (buffer(9, 1):int() == 1) then
        prayerField:append_text(" (on)")
      else
        prayerField:append_text(" (off)")
      end
      local prayerField = opcodeField:add(serverSetPrayersSteelSkin, buffer(10, 1))
      if (buffer(10, 1):int() == 1) then
        prayerField:append_text(" (on)")
      else
        prayerField:append_text(" (off)")
      end
      local prayerField = opcodeField:add(serverSetPrayersUltimateStrength, buffer(11, 1))
      if (buffer(11, 1):int() == 1) then
        prayerField:append_text(" (on)")
      else
        prayerField:append_text(" (off)")
      end
      local prayerField = opcodeField:add(serverSetPrayersIncredibleReflexes, buffer(12, 1))
      if (buffer(12, 1):int() == 1) then
        prayerField:append_text(" (on)")
      else
        prayerField:append_text(" (off)")
      end
      local prayerField = opcodeField:add(serverSetPrayersParalyzeMonster, buffer(13, 1))
      if (buffer(13, 1):int() == 1) then
        prayerField:append_text(" (on)")
      else
        prayerField:append_text(" (off)")
      end
      local prayerField = opcodeField:add(serverSetPrayersProtectFromMissiles, buffer(14, 1))
      if (buffer(14, 1):int() == 1) then
        prayerField:append_text(" (on)")
      else
        prayerField:append_text(" (off)")
      end

    -- 210 -- SERVER_OPCODE_UPDATE_DUEL_ACCEPTANCE
    elseif (serverOpcode == 210) then
      -- standalone, doesn't require data from other opcodes
      local acceptedDuelField = opcodeField:add(serverUpdateAcceptedDuelBool, buffer(1, 1))
      if buffer(1, 1):uint() == 1 then
        acceptedDuelField:append_text(" (Accepted)")
      else
        acceptedDuelField:append_text(" (Not Accepted)")
      end

    -- 211  -- SERVER_OPCODE_REMOVE_WORLD_ENTITY
    elseif (serverOpcode == 211) then
      -- not standalone
      -- requires thisLocalRegionX and thisLocalRegionY
      -- load / save those values
      if pinfoVisited then
        thisLocalRegionX = thisGameStateValuesAtThisTime[pinfoNumber][1]
        thisLocalRegionY = thisGameStateValuesAtThisTime[pinfoNumber][2]
      else 
        thisGameStateValuesAtThisTime[pinfoNumber] = { thisLocalRegionX, thisLocalRegionY }
      end

      -- TODO: possibly say what was removed at the coordinate, but it would be quite resource intensive and for not much benefit.
      for offset = 1, buffer:len() - 1, 4 do
        local xCoord = math.floor((buffer(offset, 2):int() / 8) + thisLocalRegionX)
        local yCoord = math.floor((buffer(offset + 2, 2):int() / 8) + thisLocalRegionY)
        local entityField = opcodeField:add("Remove any non-living thing at coordinate (" .. xCoord .. ", " .. yCoord .. ")")
        entityField:add(serverRemoveWorldEntityXCoordinate, buffer(offset, 2), xCoord)
        entityField:add(serverRemoveWorldEntityYCoordinate, buffer(offset + 2, 2), yCoord)
      end

    -- 222 -- SHOW_DIALOGUE_SERVER_MESSAGE_NOT_TOP
    elseif (serverOpcode == 222) then
      -- standalone, doesn't require data from other opcodes
      local messageBuffer = rsc_getRSCStringBuffer(buffer, 1)
      opcodeField:add(serverShowDialogueServerMessageTopMessage, messageBuffer, rsc_readRSCString(messageBuffer))

    -- 234 -- SERVER_OPCODE_UPDATE_PLAYERS
    elseif (serverOpcode == 234) then
      -- not standalone
      -- technically this could be a standalone opcode, but,
      -- it also depends on itself in order to link the PID with the player's usernames.
      local playerCount = buffer(1,2)

      local offset = 3
      local playerCountField = opcodeField:add(serverPlayerUpdatePlayerCount, playerCount)

      for update = 0, playerCount:uint() - 1, 1 do
        local pid = buffer(offset,2)
        offset = offset + 2
        local updateType = buffer(offset, 1)
        offset = offset + 1

        pidField = playerCountField:add(serverPlayerUpdatePID, pid)
        pid = pid:int()

        updateTypeField = pidField:add(serverPlayerUpdateUpdateType, updateType)
        updateType = updateType:uint()

        -- display bubble over player
        if (updateType == 0) then
          updateTypeField:append_text(" (display bubble)")
          local itemID = buffer(offset, 2)
          bubbleItem = updateTypeField:add(serverPlayerUpdateUpdateType0bubbleItem, itemID)
          bubbleItem:append_text(" (" .. ITEM_NAMES[itemID:uint()] ..")")
          offset = offset + 2

        -- chat message
        elseif (updateType == 1) then
          updateTypeField:append_text(" (chat message)")
          local modStatus = buffer(offset, 1)
          offset = offset + 1

          modStatusField = updateTypeField:add(serverPlayerUpdateUpdateType1modStatus, modStatus)
          if (modStatus:int() == 0) then
            modStatusField:append_text(" (not a mod)")
          elseif (modStatus:int() == 1) then
            modStatusField:append_text(" (pmod)")
          elseif (modStatus:int() == 2) then
            modStatusField:append_text(" (jmod)")
          else
            modStatusField:append_text(" (unknown)")
          end

          local method220Cache = method220(buffer, offset)
          local messageLengthBuffer = method220Cache[1]
          local messageLength = method220Cache[2]
          updateTypeField:add(serverPlayerUpdateUpdateType1chatLength, messageLengthBuffer, messageLength)
          offset = offset + method220Cache[3]

          local method240Cache = method240(buffer, 0, offset, messageLength)
          local scrambledChatMessageBufferLength = method240Cache[1]
          local chatMessageInts = method240Cache[2]
          updateTypeField:add(serverPlayerUpdateUpdateType1chatMessage, buffer(offset, scrambledChatMessageBufferLength), byteArrayToString(chatMessageInts))
          offset = offset + scrambledChatMessageBufferLength

        -- update damage taken
        elseif (updateType == 2) then
          updateTypeField:append_text(" (damage update)")
          local damage = buffer(offset, 1)
          local currentHp = buffer(offset + 1, 1)
          local maxHp = buffer(offset + 2, 1)
          offset = offset + 3

          updateTypeField:add(serverPlayerUpdateUpdateType2damage, damage)
          updateTypeField:add(serverPlayerUpdateUpdateType2currentHp, currentHp)
          updateTypeField:add(serverPlayerUpdateUpdateType2maxHp, maxHp)

        -- show projectile sprite from player towards NPC
        elseif (updateType == 3) then
          updateTypeField:append_text(" (show projectile from player to NPC)")
          local sprite = buffer(offset, 2)
          spriteType = updateTypeField:add(serverPlayerUpdateUpdateType3sprite, sprite)
          offset = offset + 2
          spriteType:append_text(" (" .. SPRITE_TYPES[sprite:int()] .. ")")

          local shooterServerIndex = buffer(offset, 2) -- TODO: interpret who this is
          offset = offset + 2
          updateTypeField:add(serverPlayerUpdateUpdateType3shooterServerIndex, shooterServerIndex)

        -- show projectile sprite between 2 players
        elseif (updateType == 4) then
          updateTypeField:append_text(" (show projectile from player to player)")
          local sprite = buffer(offset, 2)
          local spriteField = updateTypeField:add(serverPlayerUpdateUpdateType4sprite, sprite)
          offset = offset + 2

          spriteField:append_text(" (" .. SPRITE_TYPES[sprite:int()] .. ")")

          local shooterServerIndex = buffer(offset, 2)  -- TODO: interpret who this is
          offset = offset + 2
          bubbleItem = updateTypeField:add(serverPlayerUpdateUpdateType4shooterServerIndex, shooterServerIndex)

        -- equipment change
        elseif (updateType == 5) then
          updateTypeField:append_text(" (equipment change, player name & appearance)")

          local serverIndex = buffer(offset, 2)
          offset = offset + 2
          updateTypeField:add(serverPlayerUpdateUpdateType5serverIndex, serverIndex)

          local username1 = rsc_getRSCStringBuffer(buffer, offset)
          offset = offset + username1:len()
          local username = rsc_readRSCString(username1)
          updateTypeField:add(serverPlayerUpdateUpdateType5username1, username1, username)
          thisPlayerServer[pid]["displayName"] = username

          local username2 = rsc_getRSCStringBuffer(buffer, offset)
          offset = offset + username2:len()
          local accountName = rsc_readRSCString(username2)
          updateTypeField:add(serverPlayerUpdateUpdateType5username2, username2, accountName)
          thisPlayerServer[pid]["accountName"] = accountName

          local equipCount = buffer(offset, 1)
          offset = offset + 1
          updateTypeField:add(serverPlayerUpdateUpdateType5equipCount, equipCount)

          local equipment = buffer(offset, equipCount:uint())
          equipmentField = updateTypeField:add(serverPlayerUpdateUpdateType5equipment, equipment)

          local slot = {}
          slot[0] = "Hair Style: "
          slot[1] = "Shirt Style: "
          slot[2] = "Pants Style: "
          slot[3] = "Shield: "
          slot[4] = "Weapon: "
          slot[5] = "Head: "
          slot[6] = "Body: "
          slot[7] = "Legs: "
          slot[8] = "Glove: "
          slot[9] = "Boots: "
          slot[10] = "Amulet: "
          slot[11] = "Cape: "
          for i = 0, equipCount:uint() - 1, 1 do
            local equipField = equipmentField:add(buffer(offset, 1), slot[i] .. buffer(offset, 1):uint())
            thisPlayerServer[pid]["equippedItem"][i] = buffer(offset, 1):uint()
            equipField:append_text(" (" .. ANIMATION_SPRITE_NAMES[thisPlayerServer[pid]["equippedItem"][i]] .. ")")
            offset = offset + 1
          end

          local hairColourField = updateTypeField:add(serverPlayerUpdateUpdateType5hairColor, buffer(offset, 1))
          thisPlayerServer[pid]["colourHair"] = buffer(offset, 1):int()
          hairColourField:append_text(" (HTML Colour: " .. HAIR_COLOURS_STRING[thisPlayerServer[pid]["colourHair"]] .. ") (English Name: " .. HAIR_COLOUR_NAMES[thisPlayerServer[pid]["colourHair"]] .. ")") 

          local topColourField = updateTypeField:add(serverPlayerUpdateUpdateType5topColor, buffer(offset + 1, 1))
          thisPlayerServer[pid]["colourTop"] = buffer(offset + 1, 1):int()
          topColourField:append_text(" (HTML Colour: " .. CLOTHING_COLOURS_STRING[thisPlayerServer[pid]["colourTop"]] .. ") (English Name: " .. CLOTHING_COLOUR_NAMES[thisPlayerServer[pid]["colourTop"]] .. ")") 

          local bottomColourField = updateTypeField:add(serverPlayerUpdateUpdateType5bottomColor, buffer(offset + 2, 1))
          thisPlayerServer[pid]["colourBottom"] = buffer(offset + 2, 1):int()
          bottomColourField:append_text(" (HTML Colour: " .. CLOTHING_COLOURS_STRING[thisPlayerServer[pid]["colourBottom"]] .. ") (English Name: " .. CLOTHING_COLOUR_NAMES[thisPlayerServer[pid]["colourBottom"]] .. ")") 

          local skinColourField = updateTypeField:add(serverPlayerUpdateUpdateType5skinColor, buffer(offset + 3, 1))
          thisPlayerServer[pid]["colourSkin"] = buffer(offset + 3, 1):int()
          skinColourField:append_text(" (HTML Colour: " .. SKIN_COLOURS_STRING[thisPlayerServer[pid]["colourSkin"]] .. ") (English Name: " .. SKIN_COLOUR_NAMES[thisPlayerServer[pid]["colourSkin"]] .. ")") 

          updateTypeField:add(serverPlayerUpdateUpdateType5level, buffer(offset + 4, 1))
          thisPlayerServer[pid]["level"] = buffer(offset + 4, 1):int()

          local skullField = updateTypeField:add(serverPlayerUpdateUpdateType5skull, buffer(offset + 5, 1))
          thisPlayerServer[pid]["skullVisible"] = buffer(offset + 5, 1):int()
          skullField:append_text(" (" .. boolToEnglish(thisPlayerServer[pid]["skullVisible"]) .. ")")

          offset = offset + 6

        -- quest chat over players' heads
        elseif (updateType == 6) then
          updateTypeField:append_text(" (quest chat from player)")

          local method220Cache = method220(buffer, offset)
          local messageLengthBuffer = method220Cache[1]
          local messageLength = method220Cache[2]
          updateTypeField:add(serverPlayerUpdateUpdateType6chatLength, messageLengthBuffer, messageLength)
          offset = offset + method220Cache[3]

          local method240Cache = method240(buffer, 0, offset, messageLength)
          local scrambledChatMessageBufferLength = method240Cache[1]
          local chatMessageInts = method240Cache[2]
          updateTypeField:add(serverPlayerUpdateUpdateType6chatMessage, buffer(offset, scrambledChatMessageBufferLength), byteArrayToString(chatMessageInts))
          offset = offset + scrambledChatMessageBufferLength

        else
          -- There is not actually another update type
          -- but there was client logic for an "else" that hypothetically
          -- might have been hit if the player was null
          -- (it would have also been hit if there was another update type)
          updateTypeField:append_text(" (unknown)")

          -- TODO: determine if this condition was ever hit in any known replays
        end
        pidField:append_text(" (Username: " .. thisPlayerServer[pid]["displayName"] .. ") ")
      end

    -- 237 -- SERVER_OPCODE_UPDATE_IGNORE_BECAUSE_OF_NAME_CHANGE
    elseif (serverOpcode == 237) then
      -- standalone, doesn't require data from other opcodes
      local offset = 1

      local ignoreListNameBuffer = rsc_getRSCStringBuffer(buffer, offset)
      local ignoreListNameValue = rsc_readRSCString(ignoreListNameBuffer)
      opcodeField:add(serverUpdateIgnoreBecauseOfNameChangeIgnoreListName, ignoreListNameBuffer, ignoreListNameValue)
      offset = offset + ignoreListNameBuffer:len()

      local ignoreListAccountNameBuffer = rsc_getRSCStringBuffer(buffer, offset)
      local ignoreListAccountNameValue = rsc_readRSCString(ignoreListAccountNameBuffer)
      opcodeField:add(serverUpdateIgnoreBecauseOfNameChangeIgnoreListAccountName, ignoreListAccountNameBuffer, ignoreListAccountNameValue)
      offset = offset + ignoreListAccountNameBuffer:len()

      local ignoreListOldNameBuffer = rsc_getRSCStringBuffer(buffer, offset)
      local ignoreListOldNameValue = rsc_readRSCString(ignoreListOldNameBuffer)
      opcodeField:add(serverUpdateIgnoreBecauseOfNameChangeIgnoreListOldName, ignoreListOldNameBuffer, ignoreListOldNameValue)
      offset = offset + ignoreListOldNameBuffer:len()

      local ignoreListServersBuffer = rsc_getRSCStringBuffer(buffer, offset)
      local ignoreListServersValue = rsc_readRSCString(ignoreListServersBuffer)
      opcodeField:add(serverUpdateIgnoreBecauseOfNameChangeIgnoreListServers, ignoreListServersBuffer, ignoreListServersValue)
      offset = offset + ignoreListServersBuffer:len()

      local updateExistingField = opcodeField:add(serverUpdateIgnoreBecauseOfNameChangeAttemptUpdatingExistingEntry, buffer(offset, 1), buffer(offset, 1):int() == 1)
      updateExistingField:append_text(" (currently no known replays that show this value being true)")

    -- 240 -- SERVER_OPCODE_GAME_SETTINGS
    elseif (serverOpcode == 240) then
      -- standalone, doesn't require data from other opcodes
      local cameraModeAuto = buffer(1, 1)
      local mouseModeOne = buffer(2, 1)
      local disableSound = buffer(3, 1)
      local field = opcodeField:add(serverCameraModeAuto, cameraModeAuto)
      if (cameraModeAuto:uint() == 1) then
        field:append_text(" (Auto)")
      else
        field:append_text(" (Manual)")
      end
      field = opcodeField:add(serverMouseButtonOne, mouseModeOne)
      if (mouseModeOne:uint() == 1) then
        field:append_text(" (One)")
      else
        field:append_text(" (Two)")
      end
      field = opcodeField:add(serverSoundDisabled, disableSound)
      if (disableSound:uint() == 1) then
        field:append_text(" (Disabled)")
      else
        field:append_text(" (Enabled)")
      end

    -- 244 -- SERVER_OPCODE_SET_FATIGUE_SLEEPING
    elseif (serverOpcode == 244) then
      -- standalone, doesn't require data from other opcodes
      opcodeField:add(serverSetFatigueSleepingFatigueSleeping, buffer(1, 2), buffer(1, 2):int() / 7.5)

    -- 245 -- SERVER_OPCODE_SHOW_DIALOGUE_MENU
    elseif (serverOpcode == 245) then
      -- standalone, doesn't require data from other opcodes
      local optionCount = buffer(1, 1)
      local menuCountField = opcodeField:add(serverShowDialogueMenuCount, optionCount)

      local offset = 2
      for i = 0, optionCount:uint() - 1, 1 do
        local stringBuffer = rsc_getRSCStringBuffer(buffer, offset)
        local stringValue = rsc_readRSCString(stringBuffer)
        local stringTree = menuCountField:add(serverShowDialogueMenuString, stringBuffer, "")
        stringTree:set_text("Option (" .. i .. "): " .. stringValue)
        if not pinfoVisited then
          thisOptionCache[i] = stringValue
        end
        offset = offset + stringBuffer:len()
      end

    -- 249 -- SERVER_OPCODE_UPDATE_BANK_ITEM_DISPLAY
    elseif (serverOpcode == 249) then
      -- standalone, doesn't require data from other opcodes
      local slotField = opcodeField:add(serverUpdateBankItemDisplaySlot, buffer(1, 1))
      local slot = buffer (1, 1):uint()
      local page = math.floor(slot / 48) + 1
      local row = math.floor((slot % 48) / 8) + 1
      local column = math.floor((slot % 48) % 8) + 1
      slotField:append_text(" (Page " .. page .. ", Row " .. row .. ", Column " .. column .. ")")
      local itemIDField = opcodeField:add(serverUpdateBankItemDisplayItemID, buffer(2, 2))
      itemIDField:append_text(" (" .. ITEM_NAMES[buffer(2, 2):int()] .. ")")
      local count = rsc_getUnsignedInt3(buffer, 4)
      itemIDField:add(serverUpdateBankItemDisplayItemCount, count[1], count[2])

      -- TODO: you'll most likely want to actually implement the bank slot shifting code here in order to track CLIENT_BANK_WITHDRAW & other stuff

    -- 253 -- SERVER_OPCODE_UPDATE_DUEL_OPPONENT_ACCEPTED
    elseif (serverOpcode == 253) then
      -- standalone, doesn't require data from other opcodes
      local duelAcceptedField = opcodeField:add(serverUpdateDuelOpponentAccepted, buffer(1, 1))
      if (buffer(1, 1):int() == 1) then
        duelAcceptedField:append_text(" (Accepted)")
      else
        duelAcceptedField:append_text(" (Not Accepted)")
      end
    end
  end
end

function bytesToString(bytes)
  local length = bytes:len()
  local bytesString = bytes(0, 1):uint()
  for i = 1, length - 1, 1 do
    bytesString = bytesString .. " " .. bytes(i, 1):uint()
  end
  return bytesString
end

function boolToEnglish(bool)
  if bool == 0 then
    return "No"
  else
    return "Yes"
  end
end

function byteArrayToString(bytes)
  local length = #bytes
  local bytesString = string.char(bytes[1])
  for i = 2, length do
    bytesString = bytesString .. string.char(bytes[i])
  end
  return bytesString
end

function rscStringCrop(buffer)
  return buffer(1, buffer:len() - 2):string()
end

function rsc_getPacketLengthBuffer(buffer, offset)
  local length = buffer(offset, 1)
  if (length:uint() >= 160) then
    length = buffer(offset, 2)
  end
  return length
end

function rsc_getRSCStringBuffer(buffer, offset)
  local length = 1
  local value = buffer(offset + 1, 1)
  while (value:uint() ~= 0) do
    length = length + 1
    value = buffer(offset + length, 1)
  end
  length = length + 1
  return buffer(offset, length)
end

function rsc_getUnsignedInt3(buffer, offset)
  if buffer(offset, 1):int() >= 0 then
    return { buffer(offset, 2), buffer(offset, 2):uint() }
  else
    return { buffer(offset, 4), bit.band(buffer(offset, 4):uint(), 2147483647) }
  end
end

function rsc_readPacketLength(buffer)
  local length = buffer(0, 1):uint()
  if (buffer:len() > 1) then
    length = 256 * length - (40960 - buffer(1, 1):uint())
  end
  return length
end

function rsc_readRSCString(buffer)
  local RSCString = ""
  local length = buffer:len()
  for i = 1, length - 2, 1 do
    value = buffer(i, 1):uint()
    if (value == 160) then -- 0xa0 in hex
      RSCString = RSCString .. " "
    else 
      RSCString = RSCString .. string.char(value)
    end
  end
  return RSCString
end

-- Keeping anIntArray486 instead of "2^bitsToGet - 1" because anIntArray486 is faster
anIntArray486 = { 1,3,7,15,31,63,127,255,511,1023,2047,4095,8191,16383,32767,65535,131071,262143,524287,1048575,2097151,4194303,8388607,16777215,33554431,67108863,134217727,268435455,536870911,1073741823,2147483647,-1 }
anIntArray486[0] = 0

-- reads "bitsToGet" bits from the entire buffer, starting at bitOffset
-- reimplemented for lua datatypes where :int() can return an 8 bit int, a 16 bit int, or a 32 bit int (maybe 24 bit int too!)
function rsc_getBitMask(bitsToGet, bitOffset, buffer)
  local byteOffset = math.floor(bitOffset / 8)
  local spannedBytes = math.ceil((bitsToGet + bitOffset % 8) / 8)
  workingBuffer = buffer(byteOffset + 1, spannedBytes)

  bitOffset = bitOffset - byteOffset * 8

  return bit.band(bit.rshift(workingBuffer:int(), spannedBytes * 8 - bitsToGet - bitOffset), anIntArray486[bitsToGet])
end

-- Cheat on not having to reimplement anIntArray209 in lua by precomputing it
-- In case you are uninitiated, this is a dictionary used for decoding chat in opcode 234 update types 1 & 6, for NPC chat, and also for Send/Receive PMs
-- anIntArray209 contains 512 elements.
anIntArray209 = { 215,203,83,158,104,101,93,84,107,103,109,95,94,98,89,86,70,41,32,27,24,23,-1,-2,26,-3,-4,31,30,-5,-6,-7,37,38,36,-8,-9,-10,40,-11,-12,55,48,46,47,-13,-14,-15,52,51,-16,-17,54,-18,-19,63,60,59,-20,-21,62,-22,-23,67,66,-24,-25,69,-26,-27,199,132,80,77,76,-28,-29,79,-30,-31,87,85,-32,-33,-34,-35,-36,197,-37,91,-38,134,-39,-40,-41,97,-42,-43,133,106,-44,117,-45,-46,139,-47,-48,110,-49,-50,114,113,-51,-52,116,-53,-54,135,138,136,129,125,124,-55,-56,130,128,-57,-58,-59,183,-60,-61,-62,-63,-64,148,-65,-66,153,149,145,144,-67,-68,147,-69,-70,-71,152,154,-72,-73,-74,157,171,-75,-76,207,184,174,167,166,165,-77,-78,-79,172,170,-80,-81,-82,178,-83,177,182,-84,-85,187,181,-86,-87,-88,-89,206,221,-90,189,-91,198,254,262,195,196,-92,-93,-94,-95,-96,252,255,250,-97,211,209,-98,-99,212,-100,213,-101,-102,-103,224,-104,232,227,220,226,-105,-106,246,236,-107,243,-108,-109,231,237,235,-110,-111,239,238,-112,-113,-114,-115,-116,241,-117,244,-118,-119,248,-120,249,-121,-122,-123,253,-124,-125,-126,-127,259,258,-128,-129,261,-130,-131,390,327,296,281,274,271,270,-132,-133,273,-134,-135,278,277,-136,-137,280,-138,-139,289,286,285,-140,-141,288,-142,-143,293,292,-144,-145,295,-146,-147,312,305,302,301,-148,-149,304,-150,-151,309,308,-152,-153,311,-154,-155,320,317,316,-156,-157,319,-158,-159,324,323,-160,-161,326,-162,-163,359,344,337,334,333,-164,-165,336,-166,-167,341,340,-168,-169,343,-170,-171,352,349,348,-172,-173,351,-174,-175,356,355,-176,-177,358,-178,-179,375,368,365,364,-180,-181,367,-182,-183,372,371,-184,-185,374,-186,-187,383,380,379,-188,-189,382,-190,-191,387,386,-192,-193,389,-194,-195,454,423,408,401,398,397,-196,-197,400,-198,-199,405,404,-200,-201,407,-202,-203,416,413,412,-204,-205,415,-206,-207,420,419,-208,-209,422,-210,-211,439,432,429,428,-212,-213,431,-214,-215,436,435,-216,-217,438,-218,-219,447,444,443,-220,-221,446,-222,-223,451,450,-224,-225,453,-226,-227,486,471,464,461,460,-228,-229,463,-230,-231,468,467,-232,-233,470,-234,-235,479,476,475,-236,-237,478,-238,-239,483,482,-240,-241,485,-242,-243,499,495,492,491,-244,-245,494,-246,-247,497,-248,502,-249,506,503,-250,-251,505,-252,-253,508,-254,510,-255,-256,0 }

-- BufferBase_Sub3 method220 determines length of message
function method220(buffer, offset)
  local messageLengthTenative = buffer(offset, 1)
  local messageLengthBuffer -- can be either 1 or 2 bytes long
  local messageLength -- how many characters are in the message the player sent (not directly related to the number of bytes)
  if (messageLengthTenative:uint() < 128) then
    messageLengthBuffer = buffer(offset, 1)
    messageLength = messageLengthBuffer:uint()
    return { messageLengthBuffer, messageLength, 1 }
  else
    messageLengthBuffer = buffer(offset, 2)
    messageLength = messageLengthBuffer:uint() - 32768
    return { messageLengthBuffer, messageLength, 2 }
  end
end

-- from Class11.java, takes each byte in the opcode data as an instruction on how to read anIntArray209
function method240(opcodeBuffer, returnStringIndex, currentBufferOffset, chatLength)
  if (chatLength == 0) then
    return { 0, "" }
  else
    local scrambleIndex = 0
    local returnLength = currentBufferOffset
    local returnString = {}

    while (true)
    do
      currentByte = opcodeBuffer(returnLength, 1):int()
      -----------------------------------------------------------------
      if (currentByte >= 0) then
        scrambleIndex = scrambleIndex + 1
      else
        scrambleIndex = anIntArray209[scrambleIndex + 1]
      end

      decodedCharacter = anIntArray209[scrambleIndex + 1]
      if (decodedCharacter < 0) then
        returnString[returnStringIndex + 1] = bit.bnot(decodedCharacter)
        returnStringIndex = returnStringIndex + 1
        scrambleIndex = 0
      end

      -----------------------------------------------------------------
      for bitIndex = 6, 0, -1 do
        if (bit.band(currentByte, 2^bitIndex) == 0) then
          scrambleIndex = scrambleIndex + 1
        else
          scrambleIndex = anIntArray209[scrambleIndex + 1]
        end

        decodedCharacter = anIntArray209[scrambleIndex + 1]
        if (decodedCharacter < 0) then
          returnString[returnStringIndex + 1] = bit.bnot(decodedCharacter)
          returnStringIndex = returnStringIndex + 1
          scrambleIndex = 0
        end
      end
      -----------------------------------------------------------------
      returnLength = returnLength + 1

      if (returnStringIndex >= chatLength) then
        break
      end

      if (returnLength >= opcodeBuffer:len()) then
        break
      end

    end
    return { returnLength - currentBufferOffset, returnString }
  end
end

function calculateShopItemPrice(basePrice, amountInStock, baseAmountInStock, degreeToWhichShopkeeperCaresAboutStock, generosityBonus)
  howMuchICareAboutMyOverstockUnderstockSituation = degreeToWhichShopkeeperCaresAboutStock * (baseAmountInStock - amountInStock)

  -- clamp howMuchICareAboutMyOverstockUnderstockSituation to -100 to 100 range
  if howMuchICareAboutMyOverstockUnderstockSituation < -100 then
    howMuchICareAboutMyOverstockUnderstockSituation = -100
  elseif howMuchICareAboutMyOverstockUnderstockSituation > 100 then
    howMuchICareAboutMyOverstockUnderstockSituation = 100
  end

  -- clamp hectoBasePriceMultiplier to be at least positive 10
  hectoBasePriceMultiplier = generosityBonus + howMuchICareAboutMyOverstockUnderstockSituation
  if (hectoBasePriceMultiplier < 10) then
    hectoBasePriceMultiplier = 10
  end

  return math.floor((basePrice * hectoBasePriceMultiplier) / 100)
end  

function rsc_createPlayer(wipeTradeItems, serverIndex, yPos, xPos, animation)
  if (wipeTradeItems ~= 1) then
    thisTradeConfirmItems = nil
  end

  local alreadyKnownPlayer = false
  for i = 0, thisKnownPlayerCount - 1 do    
    if (thisKnownPlayers[i]["serverIndex"] == serverIndex) then
      if (thisKnownPlayers[i]["removed"] == false) then
        alreadyKnownPlayer = true
        break
      end
    end
  end

  thisPlayerServer[serverIndex]["serverIndex"] = serverIndex
  thisPlayerServer[serverIndex]["waypointCurrent"] = 0
  thisPlayerServer[serverIndex]["movingStep"] = 0
  thisPlayerServer[serverIndex]["stepCount"] = 0
  thisPlayerServer[serverIndex]["waypointsX"][0] = xPos
  thisPlayerServer[serverIndex]["currentX"] = xPos
  thisPlayerServer[serverIndex]["waypointsY"][0] = yPos
  thisPlayerServer[serverIndex]["currentY"] = yPos
  thisPlayerServer[serverIndex]["animationNext"] = animation
  thisPlayerServer[serverIndex]["animationCurrent"] = animation
  thisPlayerServer[serverIndex]["removed"] = false -- this is not part of normal client code, just a helper here in lua

  if ((xPos ~= thisPlayerServer[serverIndex]["waypointsX"][thisPlayerServer[serverIndex]["waypointCurrent"]]) or (yPos ~= thisPlayerServer[serverIndex]["waypointsY"][thisPlayerServer[serverIndex]["waypointCurrent"]])) then
    thisPlayerServer[serverIndex]["waypointCurrent"] = (thisPlayerServer[serverIndex]["waypointCurrent"] + 1) % 10
    thisPlayerServer[serverIndex]["waypointsX"][thisPlayerServer[serverIndex]["waypointCurrent"]] = xPos
    thisPlayerServer[serverIndex]["waypointsY"][thisPlayerServer[serverIndex]["waypointCurrent"]] = ypos
  end

  thisPlayerCount = thisPlayerCount + 1
  return { thisPlayerServer[serverIndex], alreadyKnownPlayer }
end

function rsc_createNPC(serverIndex, yPos, xPos, animation, npcId)
  thisNpcsServer[serverIndex]["serverIndex"] = serverIndex
  thisNpcsServer[serverIndex]["waypointCurrent"] = 0
  thisNpcsServer[serverIndex]["movingStep"] = 0
  thisNpcsServer[serverIndex]["stepCount"] = 0
  thisNpcsServer[serverIndex]["waypointsX"][0] = xPos
  thisNpcsServer[serverIndex]["currentX"] = xPos
  thisNpcsServer[serverIndex]["waypointsY"][0] = yPos
  thisNpcsServer[serverIndex]["currentY"] = yPos
  thisNpcsServer[serverIndex]["npcId"] = npcId
  thisNpcsServer[serverIndex]["animationNext"] = animation
  thisNpcsServer[serverIndex]["animationCurrent"] = animation
  thisNpcsServer[serverIndex]["removed"] = false -- this is not part of normal client code, just a helper here in lua
  return thisNpcsServer[serverIndex]
end

function rsc235_protocol.dissector(buffer, pinfo, tree)
  length = buffer:len()
  if length == 0 then return end

  pinfo.cols.protocol = rsc235_protocol.name

  local clientPacketValue = buffer(0, 1)
  local clientOpcodeValue = buffer(1, 4)
  local opcodeName = resolveOpcodeName(clientPacketValue:uint(), clientOpcodeValue:uint())

  pinfo.cols['info'] = opcodeName

  local header_subtree = tree:add(rsc235_protocol, buffer(0, 5), "rscminus Header")
  local clientPacketField = header_subtree:add(clientPacket, clientPacketValue)
  if (clientPacketValue:uint() == 1) then
    clientPacketField:append_text(" (Client)")
  else
    clientPacketField:append_text(" (Server)")
  end

  local clientOpcodeField = header_subtree:add(clientOpcode, clientOpcodeValue)
  clientOpcodeField:append_text(" (" .. opcodeName .. ")")

  local data_buffer = buffer(5)
  local data_subtree = tree:add(rsc235_protocol, data_buffer, "RSC235 Data")

  if not gui_enabled() then
    addOpcodeData(clientPacketValue:uint(), clientOpcodeValue:uint(), data_subtree, data_buffer, pinfo.number, pinfo.visited)
    return
  end
  
  if thisDebugging then
    debugWindow:append(pinfo.number)
    debugWindow:append(", ")
    debugWindow:append(tostring(pinfo.visited))
    debugWindow:append("\n")
  end
  
  addOpcodeData(clientPacketValue:uint(), clientOpcodeValue:uint(), data_subtree, data_buffer, pinfo.number, pinfo.visited)
end

local ethertype_table = DissectorTable.get("ethertype")
ethertype_table:add(0x0, rsc235_protocol)
