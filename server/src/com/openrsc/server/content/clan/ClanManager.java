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

	public ArrayList<Clan> getClans() {
		return clans;
	}

	private static class ClanRankComparator implements Comparator<Clan> {
		public int compare(final Clan o1, final Clan o2) {
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

	private final ArrayList<Clan> clans = new ArrayList<>();

	private final World world;

	public ClanManager (World world) {
		this.world = world;
	}

	public void createClan(Clan clan) {
		getClans().add(clan);
		databaseCreateClan(clan);
	}

	public void deleteClan(Clan clan) {
		databaseDeleteClan(clan);
		getClans().remove(clan);
	}

	public void initialize() {
		if (getWorld().getServer().getConfig().WANT_CLANS) {
			LOGGER.info("Loading Clans...");
			loadClans();
			LOGGER.info("Loaded " + getClans().size() + " clans");
		}
	}

	public void uninitialize() {
		if (getWorld().getServer().getConfig().WANT_CLANS) {
			getClans().clear();
		}
	}

	public Clan getClan(final String exist) {
		for (final Clan t : getClans()) {
			if (t.getClanName().equalsIgnoreCase(exist))
				return t;
			else if (t.getClanTag().equalsIgnoreCase(exist))
				return t;
		}
		return null;
	}

	public void checkAndAttachToClan(final Player player) {
		for (final Clan p : getClans()) {
			final ClanPlayer clanMember = p.getPlayer(player.getUsername());
			if (clanMember != null) {
				clanMember.setPlayerReference(player);
				player.setClan(p);
				p.updateClanGUI();
				p.updateClanSettings();
				break;
			}
		}
	}

	public void checkAndUnattachFromClan(final Player player) {
		for (Clan p : getClans()) {
			ClanPlayer cp = p.getPlayer(player.getUsername());
			if (cp != null) {
				cp.setPlayerReference(null);
				p.updateClanGUI();
				break;
			}
		}
	}

	public void saveClans() {
		for (final Clan t : getClans()) {
			saveClanChanges(t);
		}
	}

	public void saveClanChanges(final Clan clan) {
		updateClan(clan);

		deleteClanPlayers(clan);
		saveClanPlayers(clan);

		//saveBank(team);
	}

	private void loadClans() {
		try {
			ClanDef[] clansDefs = getWorld().getServer().getDatabase().getClans();
			for (ClanDef clanDef : clansDefs) {
				final Clan clan = new Clan(getWorld());
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

				getClans().add(clan);
			}
		} catch (final GameDatabaseException ex) {
			LOGGER.error("Unable to load clans.");
			LOGGER.catching(ex);
		}
	}

	private void databaseCreateClan(final Clan clan) {
		try {
			clan.setClanID(getWorld().getServer().getDatabase()
				.newClan(clan.getClanName(), clan.getClanTag(), clan.getLeader().getUsername()));

			final ArrayList<ClanMember> clanMembers = new ArrayList<>();

			for (final ClanPlayer member : clan.getPlayers()) {
				ClanMember clanMember = new ClanMember();
				clanMember.username = member.getUsername();
				clanMember.rank = member.getRank().getRankIndex();
				clanMember.kills = 0;
				clanMember.deaths = 0;
				clanMembers.add(clanMember);
			}

			getWorld().getServer().getDatabase().saveClanMembers(clan.getClanID(),
				clanMembers.toArray(new ClanMember[clanMembers.size()]));

		} catch (final GameDatabaseException ex) {
			LOGGER.error("Error creating clan");
			LOGGER.catching(ex);
		}
	}

	private void databaseDeleteClan(final Clan clan) {
		try {
			getWorld().getServer().getDatabase().deleteClan(clan.getClanID());
		} catch (final GameDatabaseException ex) {
			LOGGER.error("Error deleting clan");
			LOGGER.catching(ex);
		}
	}

	private void saveClanPlayers(final Clan clan) {
		try {
			final ArrayList<ClanMember> clanMembers = new ArrayList<>();
			for (final ClanPlayer member : clan.getPlayers()) {
				final ClanMember clanMember = new ClanMember();
				clanMember.username = member.getUsername();
				clanMember.rank = member.getRank().getRankIndex();
				clanMember.kills = member.getKills();
				clanMember.deaths = member.getDeaths();

				clanMembers.add(clanMember);
			}

			getWorld().getServer().getDatabase()
				.saveClanMembers(clan.getClanID(), clanMembers.toArray(new ClanMember[clanMembers.size()]));

		} catch (final GameDatabaseException e) {
			LOGGER.error("Unable to save clan players for clan: " + clan.getClanName());
			LOGGER.catching(e);
		}
	}

	private void deleteClanPlayers(final Clan clan) {
		try {
			getWorld().getServer().getDatabase().deleteClanMembers(clan.getClanID());
		} catch (final GameDatabaseException e) {
			LOGGER.error("Unable to delete players from clan: " + clan.getClanName());
			LOGGER.catching(e);
		}
	}

	private void updateClan(final Clan clan) {
		try {
			final ClanDef clanDef = new ClanDef();
			clanDef.id = clan.getClanID();
			clanDef.name = clan.getClanName();
			clanDef.tag = clan.getClanTag();
			clanDef.leader = clan.getLeader().getUsername();
			clanDef.kick_setting = clan.getKickSetting();
			clanDef.invite_setting = clan.getInviteSetting();
			clanDef.allow_search_join = clan.getAllowSearchJoin();
			clanDef.clan_points = clan.getClanPoints();

			getWorld().getServer().getDatabase().updateClan(clanDef);
		} catch (final GameDatabaseException e) {
			LOGGER.error("Unable to update clan: " + clan.getClanName());
			LOGGER.catching(e);
		}
	}

	public void updateClanRankPlayer(final ClanPlayer cp) {
		try {
			final ClanMember clanMember = new ClanMember();
			clanMember.username = cp.getUsername();
			clanMember.rank = cp.getRank().getRankIndex();

			getWorld().getServer().getDatabase().updateClanMember(clanMember);
		} catch (final GameDatabaseException e) {
			LOGGER.error("Unable to update rank for clan player: " + cp.getUsername());
			LOGGER.catching(e);
		}
	}
}
