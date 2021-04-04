package com.openrsc.server.net.rsc.handlers;

public class PacketConflictHandler /*implements PayloadProcessor<OpcodeIn>*/ {

	/*public static HashMap<String, PacketHandler> packetHandlers = new HashMap<String, PacketHandler>();
	private static final Logger LOGGER = LogManager.getLogger();

	static {
		bind(OpcodeInn.DUEL_DECLINED.getOpcode(), 1, PlayerDuelHandler.class);
		bind(OpcodeInn.CHANGE_RECOVERY_REQUEST.getOpcode(), 2, SecuritySettingsHandler.class);
		bind(OpcodeInn.GROUND_ITEM_TAKE.getOpcode(), 1, GroundItemTake.class);
		bind(OpcodeInn.CHANGE_DETAILS_REQUEST.getOpcode(), 2, SecuritySettingsHandler.class);
	}

	public void process(AbstractStruct<OpcodeIn> payload, Player player) throws Exception {
		int pID = packet.getID();
		int length = packet.getLength();
		Player affectedPlayer;

		OpcodeInn opcode = OpcodeInn.getFromList(packet.getID(),
			OpcodeInn.DUEL_DECLINED, OpcodeInn.CHANGE_RECOVERY_REQUEST,
			OpcodeInn.GROUND_ITEM_TAKE, OpcodeInn.CHANGE_DETAILS_REQUEST);

		if (opcode == null)
			return;

		switch (opcode) {
			case DUEL_DECLINED:
			case CHANGE_RECOVERY_REQUEST:
				// both are same length, check first if player can decline duel
				affectedPlayer = player.getDuel().getDuelRecipient();
				if (affectedPlayer != null && affectedPlayer != player && player.getDuel().isDuelActive()) {
					// likely to be a duel decline request, pass it along to PlayerDuelHandler to continue doing validations
					get(unique(pID,1)).handlePacket(packet, player);
				} else {
					// redirect to SecuritySettingsHandler
					get(unique(pID,2)).handlePacket(packet, player);
				}
				break;
			case GROUND_ITEM_TAKE:
			case CHANGE_DETAILS_REQUEST:
				if (length > 1) {
					// redirect to GroundItemTake
					get(unique(pID,1)).handlePacket(packet, player);
				} else {
					// redirect to SecuritySettingsHandler
					get(unique(pID,2)).handlePacket(packet, player);
				}
				break;
			default:
				System.out.println("A packet without needing conflict resolution entered");
				break;
		}
	}

	private static void bind(int opcode, int num, Class<?> clazz) {
		Object clazzObject;
		try {
			clazzObject = clazz.getConstructor().newInstance();
			if (clazzObject instanceof PacketHandler) {
				PacketHandler packetHandler = (PacketHandler) clazzObject;
				packetHandlers.put(unique(opcode, num), packetHandler);
			} else {
				throw new Exception("bind(opcode, class) not instance of PacketHandler");
			}
		} catch (Exception e) {
			LOGGER.catching(e);
		}
	}

	public static PacketHandler get(String id) {
		return packetHandlers.get(id);
	}

	static String unique(int opcode, int num) {
		return opcode + "-" + num;
	}*/
}
