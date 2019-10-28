# Server Source

## General Layout
The server contains two packages, src (core) and plugins. Both packages
must be compiled in order to have a fully functional server. Plugins are
responsible for managing the specific game logic (ex: calculating experience, 
handling skill processes, determining object/npc/item interactions), while
the core code is used for more general components (ex: walking, executing
events, managing logins, parsing incoming packets).

## Packets
Packets are the direct connection between the server and client. 

### Incoming Packet Handling
Incoming packets are handled through the `PacketHandler.java` interface. When packets
are received by the server (core), this is the distribution chain:
1. Packets are read by `RSCConnectionHandler.java` and added to a player's packet queue.
2. The game loop in `Server.java` executes `processIncomingPackets` on the `Player.java` class,
for each player online in the game.
3. `processIncomingPackets` uses the packet ID to distribute to the proper implementation of
the `PacketHandler.java` class.
4. The `PacketHandler.java` class implementation chosen should only perform basic logic such as:
  a. Parsing the packet input,
  b. and distributing the input to the appropriate plugin or other code point.

### Outgoing Packet Handling
Outgoing packets are handled in `ActionSender.java`.

TODO

## Plugins
Plugins are the primary source of content in the game. Where the server core
manages the npc, object, and ground item placements, as well as basic mechanics
such as movements, plugins handle conversations and interactions
that influence your character's skills, quests, and acquisition of goods.

### Plugin Handling
Plugins are handled in `PluginHandler.java`. They are responsible for handling game-specific logic
related to interactions with objects, items, and npcs.

TODO

## Events
Events are scheduled function executions. There are two types of events:
1) Server Events
2) Game Tick Events

### Server Events
Server events are used for delayed execution that may not fall directly within the
game's tick cycle. This is reserved for things such as displaying chat options,
dropping items, spawning game objects and ground items, and following other players.

### Game Tick Events
Game tick events are executed on a specific tick cycle, set in the config. This
cycle is usually 600ms (Runescape's original tick timing), but has potential
to vary in custom servers. Game tick events handle most of the game's skill-based
interactions, as well as a lot of other components.

### Player Data
DB Field -> Used for permanent storage
Cache -> Used for storing non-permanent information across sessions
Attribute -> Used for storing single-session information
