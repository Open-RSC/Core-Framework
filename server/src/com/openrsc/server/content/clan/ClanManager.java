package com.openrsc.server.content.clan;

import com.openrsc.server.database.GameDatabaseException;
import com.openrsc.server.database.struct.ClanDef;
import com.openrsc.server.database.struct.ClanMember;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Comparator;


public class ClanManager {

	public World getWorld() {
		return world;
	}

	private static class ClanRankComparator implements Comparator<Clan> {
		public int compare(Clan o1, Clan o2) {
			if (o1.getClanPoints() == o2.getClanPoints()) {
				return o1.getClanName().compareTo(o2.getClanName());
			}
			return o1.getClanPoints() > o2.getClanPoints() ? -1 : 1;
		}
	}

	public final static ClanRankComparator CLAN_COMPERATOR = new ClanRankComparator();
	/**
	 * The asynchronous logger.
	 */
	private static final Logger LOGGER = LogManager.getLogger();
	public static ArrayList<Clan> clans = new ArrayList<>();

	private final World world;

	public ClanManager (World world) {
		this.world = world;
	}

	public void createClan(Clan clan) {
		clans.add(clan);
		databaseCreateClan(clan);
	}

	public void deleteClan(Clan clan) {
		databaseDeleteClan(clan);
		clans.remove(clan);
	}

	public void initialize() {
		LOGGER.info("Loading Clans...");
		loadClans();
		LOGGER.info("Loaded " + clans.size() + " clans");
	}

	public Clan getClan(String exist) {
		for (Clan t : clans) {
			if (t.getClanName().equalsIgnoreCase(exist))
				return t;
			else if (t.getClanTag().equalsIgnoreCase(exist))
				return t;
		}
		return null;
	}

	public void checkAndAttachToClan(Player player) {
		for (Clan p : clans) {
			ClanPlayer clanMember = p.getPlayer(player.getUsername());
			if (clanMember != null) {
				clanMember.setPlayerReference(player);
				player.setClan(p);
				p.updateClanGUI();
				p.updateClanSettings();
				break;
			}
		}
	}

	public void checkAndUnattachFromClan(Player player) {
		for (Clan p : clans) {
			ClanPlayer cp = p.getPlayer(player.getUsername());
			if (cp != null) {
				cp.setPlayerReference(null);
				p.updateClanGUI();
				break;
			}
		}
	}

	public void saveClans() {
		for (Clan t : clans) {
			saveClanChanges(t);
		}
	}

	public void saveClanChanges(Clan clan) {
		updateClan(clan);

		deleteClanPlayers(clan);
		saveClanPlayers(clan);

		//saveBank(team);
	}

	private void loadClans() {
		try {
			ClanDef[] clansDefs = getWorld().getServer().getDatabase().getClans();
			for (ClanDef clanDef : clansDefs) {
				Clan clan = new Clan(getWorld());
				clan.setClanID(clanDef.id);
				clan.setClanName(clanDef.name);
				clan.setClanTag(clanDef.tag);
				clan.setKickSetting(clanDef.kick_setting);
				clan.setInviteSetting(clanDef.invite_setting);
				clan.setAllowSearchJoin(clanDef.allow_search_join);
				clan.setClanPoints(clanDef.clan_points);

				ArrayList<ClanPlayer> clanMembers = new ArrayList<ClanPlayer>();

				ClanMember[] dbClanMembers = getWorld().getServer().getDatabase().getClanMembers(clan.getClanID());
				for (ClanMember clanMember : dbClanMembers) {
					ClanPlayer member = new ClanPlayer(clanMember.username);
					int rankID = clanMember.rank;
					member.setRank(ClanRank.getRankFor(rankID));
					member.setKills(clanMember.kills);
					member.setDeaths(clanMember.deaths);
					clanMembers.add(member);
					if (ClanRank.getRankFor(rankID) == ClanRank.LEADER) {
						clan.setLeader(member);
					}
				}

				clan.setPlayers(clanMembers);

				clans.add(clan);
			}
		} catch (GameDatabaseException ex) {
			LOGGER.error("Unable to load clans.");
			LOGGER.catching(ex);
		}
	}

	private void databaseCreateClan(Clan clan) {
		try {
			clan.setClanID(getWorld().getServer().getDatabase()
				.newClan(clan.getClanName(), clan.getClanTag(), clan.getLeader().getUsername()));

			final ArrayList<ClanMember> clanMembers = new ArrayList<>();

			for (ClanPlayer member : clan.getPlayers()) {
				ClanMember clanMember = new ClanMember();
				clanMember.username = member.getUsername();
				clanMember.rank = member.getRank().getRankIndex();
				clanMember.kills = 0;
				clanMember.deaths = 0;
				clanMembers.add(clanMember);
			}

			getWorld().getServer().getDatabase().saveClanMembers(clan.getClanID(),
				clanMembers.toArray(new ClanMember[clanMembers.size()]));

		} catch (GameDatabaseException ex) {
			LOGGER.error("Error creating clan");
			LOGGER.catching(ex);
		}
	}

	private void databaseDeleteClan(Clan clan) {
		try {
			getWorld().getServer().getDatabase().deleteClan(clan.getClanID());
		} catch (GameDatabaseException ex) {
			LOGGER.error("Error deleting clan");
			LOGGER.catching(ex);
		}
	}

	private void saveClanPlayers(Clan clan) {
		try {
			ArrayList<ClanMember> clanMembers = new ArrayList<>();
			for (ClanPlayer member : clan.getPlayers()) {
				ClanMember clanMember = new ClanMember();
				clanMember.username = member.getUsername();
				clanMember.rank = member.getRank().getRankIndex();
				clanMember.kills = member.getKills();
				clanMember.deaths = member.getDeaths();

				clanMembers.add(clanMember);
			}

			getWorld().getServer().getDatabase()
				.saveClanMembers(clan.getClanID(), clanMembers.toArray(new ClanMember[clanMembers.size()]));

		} catch (GameDatabaseException e) {
			LOGGER.error("Unable to save clan players for clan: " + clan.getClanName());
			LOGGER.catching(e);
		}
	}

	private void deleteClanPlayers(Clan clan) {
		try {
			getWorld().getServer().getDatabase().deleteClanMembers(clan.getClanID());
		} catch (GameDatabaseException e) {
			LOGGER.error("Unable to delete players from clan: " + clan.getClanName());
			LOGGER.catching(e);
		}
	}

	private void updateClan(Clan clan) {
		try {
			ClanDef clanDef = new ClanDef();
			clanDef.id = clan.getClanID();
			clanDef.name = clan.getClanName();
			clanDef.tag = clan.getClanTag();
			clanDef.leader = clan.getLeader().getUsername();
			clanDef.kick_setting = clan.getKickSetting();
			clanDef.invite_setting = clan.getInviteSetting();
			clanDef.allow_search_join = clan.getAllowSearchJoin();
			clanDef.clan_points = clan.getClanPoints();

			getWorld().getServer().getDatabase().updateClan(clanDef);
		} catch (GameDatabaseException e) {
			LOGGER.error("Unable to update clan: " + clan.getClanName());
			LOGGER.catching(e);
		}
	}

	public void updateClanRankPlayer(ClanPlayer cp) {
		try {
			ClanMember clanMember = new ClanMember();
			clanMember.username = cp.getUsername();
			clanMember.rank = cp.getRank().getRankIndex();

			getWorld().getServer().getDatabase().updateClanMember(clanMember);
		} catch (GameDatabaseException e) {
			LOGGER.error("Unable to update rank for clan player: " + cp.getUsername());
			LOGGER.catching(e);
		}

	}

}
