package com.openrsc.server.net.rsc.generators;

import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.Packet;
import com.openrsc.server.net.PacketBuilder;
import com.openrsc.server.net.rsc.struct.AbstractStruct;

public interface PayloadGenerator<Opcode> {

	abstract PacketBuilder fromOpcodeEnum(Opcode opcode, Player player);
	abstract Packet generate(AbstractStruct<Opcode> payload, Player player);
}
