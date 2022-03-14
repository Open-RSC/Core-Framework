package com.openrsc.server.constants;

import com.openrsc.server.model.entity.EntityType;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpellDamages {

	public enum MagicType {
		GOODEVILMAGIC,
		F2PONLYMAGIC,
		MODERNMAGIC
	}

	private final Map<Spells, ArrayList<Pair<EntityType, Double>>> dividedMagicProjectiles = new HashMap<Spells, ArrayList<Pair<EntityType, Double>>>() {{
		put(Spells.CHILL_BOLT, new ArrayList<Pair<EntityType, Double>>(){{
			add(Pair.of(EntityType.PLAYER, 3.0)); add(Pair.of(EntityType.NPC, 6.0));
		}});
		put(Spells.SHOCK_BOLT, new ArrayList<Pair<EntityType, Double>>(){{
			add(Pair.of(EntityType.PLAYER, 3.5)); add(Pair.of(EntityType.NPC, 7.0));
		}});
		put(Spells.WIND_BOLT_R, new ArrayList<Pair<EntityType, Double>>(){{
			add(Pair.of(EntityType.PLAYER, 4.0)); add(Pair.of(EntityType.NPC, 8.0));
		}});
		put(Spells.ELEMENTAL_BOLT, new ArrayList<Pair<EntityType, Double>>(){{
			add(Pair.of(EntityType.PLAYER, 5.0)); add(Pair.of(EntityType.NPC, 10.0));
		}});
	}};

	private final Map<Spells, ArrayList<Pair<EntityType, Double>>> f2pOnlyMagicProjectiles = new HashMap<Spells, ArrayList<Pair<EntityType, Double>>>() {{
		put(Spells.WIND_STRIKE, new ArrayList<Pair<EntityType, Double>>(){{
			add(Pair.of(EntityType.PLAYER, 1.0)); add(Pair.of(EntityType.NPC, 2.0));
		}});
		put(Spells.WATER_STRIKE, new ArrayList<Pair<EntityType, Double>>(){{
			add(Pair.of(EntityType.PLAYER, 2.0)); add(Pair.of(EntityType.NPC, 3.0));
		}});
		put(Spells.EARTH_STRIKE, new ArrayList<Pair<EntityType, Double>>(){{
			add(Pair.of(EntityType.PLAYER, 3.0)); add(Pair.of(EntityType.NPC, 4.0));
		}});
		put(Spells.FIRE_STRIKE, new ArrayList<Pair<EntityType, Double>>(){{
			add(Pair.of(EntityType.PLAYER, 4.0)); add(Pair.of(EntityType.NPC, 5.0));
		}});
		put(Spells.WIND_BOLT, new ArrayList<Pair<EntityType, Double>>(){{
			add(Pair.of(EntityType.PLAYER, 4.5)); add(Pair.of(EntityType.NPC, 6.0));
		}});
		put(Spells.WATER_BOLT, new ArrayList<Pair<EntityType, Double>>(){{
			add(Pair.of(EntityType.PLAYER, 5.0)); add(Pair.of(EntityType.NPC, 7.0));
		}});
		put(Spells.EARTH_BOLT, new ArrayList<Pair<EntityType, Double>>(){{
			add(Pair.of(EntityType.PLAYER, 5.5)); add(Pair.of(EntityType.NPC, 8.0));
		}});
		put(Spells.FIRE_BOLT, new ArrayList<Pair<EntityType, Double>>(){{
			add(Pair.of(EntityType.PLAYER, 6.0)); add(Pair.of(EntityType.NPC, 9.0));
		}});
		put(Spells.WIND_BLAST, new ArrayList<Pair<EntityType, Double>>(){{
			add(Pair.of(EntityType.PLAYER, 6.5)); add(Pair.of(EntityType.NPC, 10.0));
		}});
		put(Spells.WATER_BLAST, new ArrayList<Pair<EntityType, Double>>(){{
			add(Pair.of(EntityType.PLAYER, 7.0)); add(Pair.of(EntityType.NPC, 11.0));
		}});
		put(Spells.EARTH_BLAST, new ArrayList<Pair<EntityType, Double>>(){{
			add(Pair.of(EntityType.PLAYER, 7.5)); add(Pair.of(EntityType.NPC, 12.0));
		}});
		put(Spells.FIRE_BLAST, new ArrayList<Pair<EntityType, Double>>(){{
			add(Pair.of(EntityType.PLAYER, 8.0)); add(Pair.of(EntityType.NPC, 13.0));
		}});
	}};

	private final Map<Spells, ArrayList<Pair<EntityType, Double>>> modernMagicProjectiles = new HashMap<Spells, ArrayList<Pair<EntityType, Double>>>() {{
		put(Spells.WIND_STRIKE, new ArrayList<Pair<EntityType, Double>>(){{
			add(Pair.of(EntityType.PLAYER, 1.0)); add(Pair.of(EntityType.NPC, 1.0));
		}});
		put(Spells.WATER_STRIKE, new ArrayList<Pair<EntityType, Double>>(){{
			add(Pair.of(EntityType.PLAYER, 2.0)); add(Pair.of(EntityType.NPC, 2.0));
		}});
		put(Spells.EARTH_STRIKE, new ArrayList<Pair<EntityType, Double>>(){{
			add(Pair.of(EntityType.PLAYER, 3.0)); add(Pair.of(EntityType.NPC, 3.0));
		}});
		put(Spells.FIRE_STRIKE, new ArrayList<Pair<EntityType, Double>>(){{
			add(Pair.of(EntityType.PLAYER, 4.0)); add(Pair.of(EntityType.NPC, 4.0));
		}});
		put(Spells.WIND_BOLT, new ArrayList<Pair<EntityType, Double>>(){{
			add(Pair.of(EntityType.PLAYER, 4.5)); add(Pair.of(EntityType.NPC, 4.5));
		}});
		put(Spells.WATER_BOLT, new ArrayList<Pair<EntityType, Double>>(){{
			add(Pair.of(EntityType.PLAYER, 5.0)); add(Pair.of(EntityType.NPC, 5.0));
		}});
		put(Spells.EARTH_BOLT, new ArrayList<Pair<EntityType, Double>>(){{
			add(Pair.of(EntityType.PLAYER, 5.5)); add(Pair.of(EntityType.NPC, 5.5));
		}});
		put(Spells.FIRE_BOLT, new ArrayList<Pair<EntityType, Double>>(){{
			add(Pair.of(EntityType.PLAYER, 6.0)); add(Pair.of(EntityType.NPC, 6.0));
		}});
		put(Spells.WIND_BLAST, new ArrayList<Pair<EntityType, Double>>(){{
			add(Pair.of(EntityType.PLAYER, 6.5)); add(Pair.of(EntityType.NPC, 6.5));
		}});
		put(Spells.WATER_BLAST, new ArrayList<Pair<EntityType, Double>>(){{
			add(Pair.of(EntityType.PLAYER, 7.0)); add(Pair.of(EntityType.NPC, 7.0));
		}});
		put(Spells.EARTH_BLAST, new ArrayList<Pair<EntityType, Double>>(){{
			add(Pair.of(EntityType.PLAYER, 7.5)); add(Pair.of(EntityType.NPC, 7.5));
		}});
		put(Spells.FIRE_BLAST, new ArrayList<Pair<EntityType, Double>>(){{
			add(Pair.of(EntityType.PLAYER, 8.0)); add(Pair.of(EntityType.NPC, 8.0));
		}});
		put(Spells.WIND_WAVE, new ArrayList<Pair<EntityType, Double>>(){{
			add(Pair.of(EntityType.PLAYER, 8.5)); add(Pair.of(EntityType.NPC, 8.5));
		}});
		put(Spells.WATER_WAVE, new ArrayList<Pair<EntityType, Double>>(){{
			add(Pair.of(EntityType.PLAYER, 9.0)); add(Pair.of(EntityType.NPC, 9.0));
		}});
		put(Spells.EARTH_WAVE, new ArrayList<Pair<EntityType, Double>>(){{
			add(Pair.of(EntityType.PLAYER, 9.5)); add(Pair.of(EntityType.NPC, 9.5));
		}});
		put(Spells.FIRE_WAVE, new ArrayList<Pair<EntityType, Double>>(){{
			add(Pair.of(EntityType.PLAYER, 10.0)); add(Pair.of(EntityType.NPC, 10.0));
		}});
	}};

	public double getSpellDamage(Spells spell, EntityType entityType, MagicType magicType) {
		double damage = -1.0;

		switch(magicType) {
			case GOODEVILMAGIC:
				if (dividedMagicProjectiles.containsKey(spell)) {
					damage = getSpellDamage(entityType, dividedMagicProjectiles.get(spell));
				}
				break;
			case F2PONLYMAGIC:
				if (f2pOnlyMagicProjectiles.containsKey(spell)) {
					damage = getSpellDamage(entityType, f2pOnlyMagicProjectiles.get(spell));
				}
				break;
			case MODERNMAGIC:
				if (modernMagicProjectiles.containsKey(spell)) {
					damage = getSpellDamage(entityType, modernMagicProjectiles.get(spell));
				}
				break;
		}

		return damage;
	}

	private Double getSpellDamage(EntityType entityType, List<Pair<EntityType, Double>> fromSpellDamages) {
		double damage = 0;

		for (Pair<EntityType, Double> spellDamage : fromSpellDamages) {
			if (spellDamage.getKey() == entityType) {
				damage = spellDamage.getValue();
				break;
			}
		}

		return damage;
	}
}
