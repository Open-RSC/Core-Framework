package com.openrsc.server.net.rsc;

import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.struct.AbstractStruct;

public interface PayloadProcessor<Struct extends AbstractStruct<Opcode>, Opcode> {

	public void process(Struct payload, Player player) throws Exception;
}
