package com.openrsc.server.constants;

import java.util.HashMap;

public class Retreats {

	public final HashMap<Integer, Integer> npcData;

	private final Constants constants;

	public Retreats(Constants constants) {
		this.constants = constants;
		npcData = new HashMap<Integer, Integer>() {{
			put(NpcId.SOULESS_UNDEAD.id(), 15);
			put(NpcId.ROWDY_GUARD.id(), 15);
			put(NpcId.SHANTAY_PASS_GUARD_MOVING.id(), 15); //observed 13 but thought to be 15
			put(NpcId.UGTHANKI.id(), 15);
			put(NpcId.CHAOS_DWARF.id(), 10);
			put(NpcId.UNICORN.id(), 8);
			put(NpcId.BLACK_UNICORN.id(), 8);
			put(NpcId.ROGUE.id(), 8);
			put(NpcId.FORESTER.id(), 8); //observed 5 but believed to be 8 since is guardian similar to rogue
			put(NpcId.GOBLIN_OBSERVATORY.id(), 5);
			put(NpcId.BEAR_LVL24.id(), 5);
			put(NpcId.BEAR_LVL26.id(), 5);
			put(NpcId.THIEF_GENERIC.id(), 5);
			put(NpcId.THIEF_BLANKET.id(), 5);
			put(NpcId.HEAD_THIEF.id(), 5);
			put(NpcId.MONK.id(), 5);
			put(NpcId.IMP.id(), 4);
			put(NpcId.GIANT_BAT.id(), 4);
			put(NpcId.DEATH_WING.id(), 4);
			put(NpcId.CHICKEN.id(), 2);
			put(NpcId.FIREBIRD.id(), 2);
			put(NpcId.OOMLIE_BIRD.id(), 2);
			put(NpcId.SCORPION.id(), 2);
			put(NpcId.POISON_SCORPION.id(), 2);
			put(NpcId.KING_SCORPION.id(), 2);
			put(NpcId.JUNGLE_SPIDER.id(), 2); //might be a typo on authentic retreat, kept as is
			put(NpcId.HOBGOBLIN_LVL32.id(), 1);
			put(NpcId.ROWDY_SLAVE.id(), 1);
			put(NpcId.HIGHWAYMAN.id(), 1);
			put(NpcId.MUGGER.id(), 1);
			put(NpcId.RAT_WITCHES_POTION.id(), 1);
			put(NpcId.RAT_LVL8.id(), 1);
			put(NpcId.RAT_LVL13.id(), 1);
			put(NpcId.RAT_WMAZEKEY.id(), 1);
			put(NpcId.DUNGEON_RAT.id(), 1);
			put(NpcId.BLESSED_VERMEN.id(), 1);
			put(NpcId.ZOMBIE_LVL19.id(), 1);
			put(NpcId.ZOMBIE_LVL24_GEN.id(), 1);
			put(NpcId.ZOMBIE_INVOKED.id(), 1);
			put(NpcId.ZOMBIE_LVL32.id(), 1);
			put(NpcId.ZOMBIE_ENTRANA.id(), 1);
			put(NpcId.ZOMBIE_WMAZEKEY.id(), 1);
			put(NpcId.SPIDER.id(), 1);
			put(NpcId.GIANT_SPIDER_LVL8.id(), 1);
			put(NpcId.GIANT_SPIDER_LVL31.id(), 1);
			put(NpcId.DEADLY_RED_SPIDER.id(), 1);
			put(NpcId.SHADOW_SPIDER.id(), 1);
			put(NpcId.POISON_SPIDER.id(), 1);
			put(NpcId.ICE_SPIDER.id(), 1);
			put(NpcId.DONNY_THE_LAD.id(), 1);
			put(NpcId.SPEEDY_KEITH.id(), 1);
			put(NpcId.BLACK_HEATHER.id(), 1);
			put(NpcId.WYSON_THE_GARDENER.id(), 1);
			put(NpcId.STRAVEN.id(), 1);
			put(NpcId.JONNY_THE_BEARD.id(), 1);
			put(NpcId.KALRAG.id(), 1);
		}};
	}
	
}
