package com.openrsc.server.net;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.openrsc.server.Server;
import com.openrsc.server.constants.Skills;
import com.openrsc.server.content.market.MarketItem;
import com.openrsc.server.external.SkillDef;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.snapshot.Chatlog;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.sql.DatabaseConnection;
import com.openrsc.server.sql.query.logs.ChatLog;
import com.openrsc.server.util.rsc.MessageType;
import com.vdurmont.emoji.EmojiParser;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.AnnotatedEventManager;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.util.JsonUtils;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDABuilder;

import javax.security.auth.login.LoginException;

public class DiscordService implements Runnable{

	private final ScheduledExecutorService scheduledExecutor;

	private Queue<String> auctionRequests = new ConcurrentLinkedQueue<String>();
	private Queue<String> monitoringRequests = new ConcurrentLinkedQueue<String>();

	private static final Logger LOGGER	= LogManager.getLogger();
	private long monitoringLastUpdate	= 0;
	private Boolean running				= false;

	private final Server server;
	private JDABuilder builder;
	private JDA jda;


	public DiscordService(Server server) {
		this.server = server;

		scheduledExecutor = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryBuilder().setNameFormat(getServer().getName()+" : DiscordServiceThread").build());
		if (server.getConfig().WANT_DISCORD_BOT) {
			File tokenFile = new File(server.getConfig().SERVER_NAME + ".tok");
			if (tokenFile.exists()) {
				try
				{
					byte[] encoded = Files.readAllBytes(Paths.get(tokenFile.getPath()));
					startPlayerBot(new String(encoded, StandardCharsets.UTF_8));
				} catch (IOException a) {
					a.printStackTrace();
				}
			} else {
				LOGGER.info(server.getConfig().SERVER_NAME + ".tok not found. Cannot start bot.");
			}
		}
	}

	public void startPlayerBot(String token) {
		this.builder = new JDABuilder(AccountType.BOT);
		this.builder.setEventManager(new AnnotatedEventManager());
		this.builder.addEventListeners(this);
		this.builder.setToken(token);
		try {
			this.jda = this.builder.build();
		} catch (LoginException a) {
			a.printStackTrace();
		}

	}

	@SubscribeEvent
	public void handleIncomingMessage(MessageReceivedEvent event)
	{
		MessageChannel channel = event.getChannel();
		Message message = event.getMessage();
		if (!event.getAuthor().isBot()) {
			String[] args = message.getContentRaw().split(" ");
			String reply = "";
			if (event.getChannelType() == ChannelType.PRIVATE) {
				if (message.getContentRaw().startsWith("!help"))
				{
					reply = "To use me, you must pair your accounts. To do this, login to your openrsc account and type ::pair . You will be given a unique code. Return to this conversation and use !pair TOKEN. To see the commands that are available, type !commands";
				} else if (message.getContentRaw().startsWith("!commands")) {
					reply = "!auctions";
				} else if (message.getContentRaw().startsWith("!pair")) {
					if (args.length != 2)
					{
						reply = "Usage: !pair TOKEN";
					} else {
						DatabaseConnection datConn = this.server.getDatabaseConnection();
						try {
							PreparedStatement pinStatement = datConn.prepareStatement("SELECT `playerID` FROM `" + this.server.getConfig().MYSQL_TABLE_PREFIX + "player_cache` WHERE `value` = ?;");
							pinStatement.setString(1, args[1]);
							ResultSet results = pinStatement.executeQuery();
							if (results.next()) {
								int dbID = results.getInt("playerID");
								pinStatement = datConn.prepareStatement("INSERT INTO `" + this.server.getConfig().MYSQL_TABLE_PREFIX + "player_cache`(`playerID`, `type`, `key`, `value`) VALUES(?, ?, ?, ?)");
								pinStatement.setInt(1, dbID);
								pinStatement.setInt(2, 3);
								pinStatement.setString(3, "discordID");
								pinStatement.setLong(4, message.getAuthor().getIdLong());
								pinStatement.executeUpdate();

								pinStatement = datConn.prepareStatement("DELETE FROM `" + this.server.getConfig().MYSQL_TABLE_PREFIX + "player_cache` WHERE `playerID`=? AND `key`='pair_token'");
								pinStatement.setInt(1, dbID);
								pinStatement.executeUpdate();
								String username = dbIdToUsername(dbID);
								if (!username.isEmpty()) {
									reply = "You have successfully paired " + username + " to this discord account.";
									Player mrMan = this.server.getWorld().getPlayerID(dbID);
									if (mrMan != null)
									{
										mrMan.getCache().remove("pair_token");
										mrMan.getCache().store("discordID", message.getAuthor().getIdLong());
									}
								} else {
									reply = "Error 1580. Please contact an administrator.";
								}

							} else {
								reply = "Invalid pair token.";
							}

						} catch (SQLException a) {
							a.printStackTrace();
						}
					}
				} else if (message.getContentRaw().startsWith("!auctions")) {
					ArrayList<MarketItem> auctionList = (ArrayList<MarketItem>)this.server.getWorld().getMarket().getAuctionItems().clone();
					Iterator<MarketItem> e = auctionList.iterator();
					if (e.hasNext()) {
							int dbID = 0;
							if ((dbID = discordToDBId(message.getAuthor().getIdLong())) != 0) {
								while (e.hasNext()) {
									MarketItem a = e.next();
									if (a.getSeller() == dbID)
										reply = reply + server.getEntityHandler().getItemDef(a.getItemID()).getName() + " (" + a.getAmountLeft() + ") @ " + a.getPrice() + "gp ea. (" + a.getHoursLeft() + "hrs)\n";
								}
							} else
								reply = "You have not paired an account yet. Type !help for more information";
					}
					if (reply.isEmpty())
						reply = "There are no auctions at the moment.";
				} else if (message.getContentRaw().startsWith("!stats")
							|| message.getContentRaw().startsWith("!skills")) {
					int dbID = 0;
					if ((dbID = discordToDBId(message.getAuthor().getIdLong())) != 0) {
						try {
							PreparedStatement pinStatement = this.server.getDatabaseConnection().prepareStatement("SELECT * FROM `" + this.server.getConfig().MYSQL_TABLE_PREFIX + "experience` WHERE `id` = ?");
							pinStatement.setLong(1, dbID);
							ResultSet results = pinStatement.executeQuery();
							if (results.next()) {
								String username = dbIdToUsername(dbID);
								if (!username.isEmpty()) {
									StringBuilder rep = new StringBuilder();
									rep.append("Stats for " + username + "`\n");
									for (SkillDef skill: Skills.skills) {
										int experience = results.getInt("exp_" + skill.getShortName());
										int level = Skills.getLevelForExperience(experience, server.getConfig().PLAYER_LEVEL_LIMIT);
										int tnl = 0;
										if (level < Skills.GLOBAL_LEVEL_LIMIT)
											tnl = Skills.experienceCurves.get(SkillDef.EXP_CURVE.ORIGINAL)[level] - experience;
										rep.append(StringUtils.rightPad(skill.getLongName(),12," ") + ": " + String.format("%02d", level) + " (@ " + tnl + ")\n");
									}
									rep.append("`");
									reply = rep.toString();
								} else
									reply = "Error 2250";
							}
						} catch (SQLException a) {
							a.printStackTrace();
						}


					} else {
						reply = "You have not paired an account yet. Type !help for more information";
					}
				}
			} else if (message.getChannel().getIdLong() == this.server.getConfig().CROSS_CHAT_CHANNEL
						&& !message.getContentRaw().isEmpty()) {
				String strMessage = EmojiParser.parseToAliases(message.getContentRaw());

				for (Player p : this.server.getWorld().getPlayers()) {
					ActionSender.sendMessage(p, null, 0, MessageType.GLOBAL_CHAT, "@whi@[@gr2@D@whi@>] @or1@" + message.getAuthor().getName() + "@yel@: " + strMessage, 0);
				}
			} else {
				if (message.getContentRaw().startsWith("!help"))
				{
					reply = "Please use !help in a DM to me for more information.";
				}
			}

			if (!reply.isEmpty())
				channel.sendMessage(reply).queue();
		}
	}

	public void chatToDiscord(String message) {
		TextChannel textChannel = jda.getTextChannelById(this.server.getConfig().CROSS_CHAT_CHANNEL);
		if (textChannel != null)
			textChannel.sendMessage(message).queue();
	}
	public final Server getServer() {
		return server;
	}

	public void auctionAdd(MarketItem addItem) {
		String pluralHandlerMessage = addItem.getAmount() > 1
				? "%d x %s, priced at %d coins each, auctioned by %s.  %d hours left."
				: "%d x %s, priced at %d coins, auctioned by %s.  %d hours left.";

		String addMessage = String.format(pluralHandlerMessage,
				addItem.getAmount(),
				getServer().getEntityHandler().getItemDef(addItem.getItemID()).getName(),
				addItem.getPrice() / addItem.getAmount(),
				addItem.getSellerName(),
				addItem.getHoursLeft()
		);

		auctionSendToDiscord(addMessage);
	}

	public void auctionBuy(MarketItem buyItem) {
		String buyMessage = String.format("%s purchased from %s.  %d left in auction.",
			getServer().getEntityHandler().getItemDef(buyItem.getItemID()).getName(),
				buyItem.getSellerName(),
				buyItem.getAmountLeft()
		);

		auctionSendToDiscord(buyMessage);
	}

	public void auctionCancel(MarketItem cancelItem) {
		String cancelMessage = String.format("%d x %s cancelled from auction by %s.",
				cancelItem.getAmount(),
				getServer().getEntityHandler().getItemDef(cancelItem.getItemID()).getName(),
				cancelItem.getSellerName()
		);

		auctionSendToDiscord(cancelMessage);
	}

	public void auctionModDelete(MarketItem deleteItem) {
		String cancelMessage = String.format("%d x %s, auctioned by %s, has been deleted by moderator.",
				deleteItem.getAmount(),
				getServer().getEntityHandler().getItemDef(deleteItem.getItemID()).getName(),
				deleteItem.getSellerName()
		);

		auctionSendToDiscord(cancelMessage);
	}

	public void monitoringSendServerBehind(String message) {
		try {
			monitoringSendToDiscord(message + "\r\n" + getServer().buildProfilingDebugInformation(false));
		} catch(Exception e) {
			LOGGER.catching(e);
		}
	}

	private void auctionSendToDiscord(String message) {
		if(getServer().getConfig().WANT_DISCORD_AUCTION_UPDATES) {
			auctionRequests.add(message);
		}
	}

	private void monitoringSendToDiscord(String message) {
		final long now = System.currentTimeMillis();

		if(getServer().getConfig().WANT_DISCORD_MONITORING_UPDATES && now >= (monitoringLastUpdate + 3600)) {
			monitoringRequests.add(message);
			monitoringLastUpdate = now;
		}
	}

	private static void sendToDiscord(String webhookUrl, String message) throws Exception {
		StringBuilder sb = new StringBuilder();
		JsonUtils.quoteAsString(message, sb);

		String jsonPostBody = String.format("{\"content\": \"%s\"}", sb);

		java.net.URL url = new java.net.URL(webhookUrl);

		URLConnection con = url.openConnection();
		HttpURLConnection http = (HttpURLConnection) con;
		http.setRequestMethod("POST");
		http.setDoOutput(true);

		byte[] out = jsonPostBody.getBytes(StandardCharsets.UTF_8);
		int length = out.length;

		http.setFixedLengthStreamingMode(length);
		http.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
		http.connect();
		try (OutputStream os = http.getOutputStream()) {
			os.write(out);
		}
	}

	public int discordToDBId(long discord) {
		try {
			DatabaseConnection datConn = this.server.getDatabaseConnection();
			PreparedStatement pinStatement = datConn.prepareStatement("SELECT `playerID` FROM `" + this.server.getConfig().MYSQL_TABLE_PREFIX + "player_cache` WHERE `value` = ?");
			pinStatement.setLong(1, discord);
			ResultSet results = pinStatement.executeQuery();
			if (results.next()) {
				return results.getInt("playerID");
			}
		} catch (SQLException a) {
			a.printStackTrace();
		}
		return 0;
	}

	public String dbIdToUsername(int dbId) {
		try {
			DatabaseConnection datConn = this.server.getDatabaseConnection();
			PreparedStatement pinStatement = datConn.prepareStatement("SELECT `username` FROM `" + this.server.getConfig().MYSQL_TABLE_PREFIX + "players` WHERE `id` = ?");
			pinStatement.setInt(1, dbId);
			ResultSet results = pinStatement.executeQuery();
			if (results.next()) {
				return results.getString("username");
			}
		} catch (SQLException a) {
			a.printStackTrace();
		}
		return "";
	}

	@Override
	public void run()  {
		synchronized(running) {
			String message = null;

			try {
				while ((message = auctionRequests.poll()) != null) {
					sendToDiscord(getServer().getConfig().DISCORD_AUCTION_WEBHOOK_URL, message);
				}

				while ((message = monitoringRequests.poll()) != null) {
					sendToDiscord(getServer().getConfig().DISCORD_MONITORING_WEBHOOK_URL, message);
				}
			} catch (Exception e) {
				LOGGER.catching(e);
			}
		}
	}

	public void start() {
		synchronized(running) {
			running = true;
			scheduledExecutor.scheduleAtFixedRate(this, 0, 50, TimeUnit.MILLISECONDS);
		}
	}

	public void stop() {
		synchronized(running) {
			running = false;
			scheduledExecutor.shutdown();
		}
	}

	public final boolean isRunning() {
		return running;
	}
}
