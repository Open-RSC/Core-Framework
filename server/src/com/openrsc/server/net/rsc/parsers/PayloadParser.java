package com.openrsc.server.net.rsc.parsers;

import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.Packet;
import com.openrsc.server.net.rsc.struct.AbstractStruct;

public interface PayloadParser<Opcode> {

	abstract Opcode toOpcodeEnum(Packet packet, Player player);
	abstract AbstractStruct<Opcode> parse(Packet packet, Player player);
}
