package com.openrsc.server.net.rsc.struct.outgoing;

import com.openrsc.server.net.rsc.enums.OpcodeOut;
import com.openrsc.server.net.rsc.struct.AbstractStruct;

public class QuestInfoStruct extends AbstractStruct<OpcodeOut> {

	public int[] questCompleted; //size 50 for latest original client
	// custom below
	public int isUpdate; // if is update is for a specific quest
	public int numberOfQuests;
	public int[] questId;
	public int[] questStage;
	public String[] questName;
}
