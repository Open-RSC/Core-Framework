package com.openrsc.server.net;

import com.openrsc.server.Server;
import com.openrsc.server.constants.Constants;
import com.openrsc.server.content.market.MarketItem;
import com.openrsc.server.database.GameDatabaseException;
import com.openrsc.server.database.impl.mysql.queries.logging.GameReport;
import com.openrsc.server.database.struct.DiscordWatchlist;
import com.openrsc.server.database.struct.PlayerExperience;
import com.openrsc.server.external.ItemDefinition;
import com.openrsc.server.external.SkillDef;
import com.openrsc.server.model.entity.player.Group;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.util.MessageFilterType;
import com.openrsc.server.util.ServerAwareThreadFactory;
import com.openrsc.server.util.rsc.MessageType;
import com.vdurmont.emoji.EmojiParser;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.AnnotatedEventManager;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.util.JsonUtils;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;

import javax.security.auth.login.LoginException;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DiscordService implements Runnable{
	private static final int WATCHLIST_MAX_SIZE = 10;
	private ScheduledExecutorService scheduledExecutor;

	private final Queue<String> staffCommandRequests = new ConcurrentLinkedQueue<String>();
	private final Queue<String> generalLogs = new ConcurrentLinkedQueue<String>();
	private final Queue<String> auctionRequests = new ConcurrentLinkedQueue<String>();
	private final Queue<String> monitoringRequests = new ConcurrentLinkedQueue<String>();
	private final Queue<DiscordEmbed> reportAbuseRequests = new ConcurrentLinkedQueue<DiscordEmbed>();
	private final Queue<DiscordEmbed> naughtyWordsRequests = new ConcurrentLinkedQueue<DiscordEmbed>();
	private final Queue<DiscordEmbed> downtimeReports = new ConcurrentLinkedQueue<DiscordEmbed>();

	private static final Logger LOGGER = LogManager.getLogger();
	private long monitoringLastUpdate = 0;
	private Boolean running = false;

	private final Server server;
	private JDABuilder builder;
	private JDA jda;
	private GitLabApi gitLabApi;

	public DiscordService(final Server server) {
		this.server = server;

		if (server.getConfig().WANT_DISCORD_BOT) {
			File tokenFile = new File(server.getConfig().SERVER_NAME + ".tok");
			if (tokenFile.exists()) {
				try
				{
					final byte[] encoded = Files.readAllBytes(Paths.get(tokenFile.getPath()));
					startPlayerBot(new String(encoded, StandardCharsets.UTF_8));
				} catch (final IOException a) {
					a.printStackTrace();
				}
			} else {
				LOGGER.info(server.getConfig().SERVER_NAME + ".tok not found. Cannot start bot.");
			}

			tokenFile = new File("gitlab.pat");
			if (tokenFile.exists()) {
				try
				{
					byte[] encoded = Files.readAllBytes(Paths.get(tokenFile.getPath()));
					//gitLabApi = new GitLabApi("http://gitlab.openrsc.com", new String(encoded, StandardCharsets.UTF_8));
				} catch (final IOException a) {
					a.printStackTrace();
				}
			} else {
				LOGGER.info("gitlab.pat not found. Cannot start gitlab API.");
			}
		}
	}

	private void startPlayerBot(final String token) {
		this.builder = new JDABuilder(AccountType.BOT);
		this.builder.setEventManager(new AnnotatedEventManager());
		this.builder.addEventListeners(this);
		this.builder.setToken(token);
		try {
			this.jda = this.builder.build();
		} catch (final LoginException a) {
			a.printStackTrace();
		}
	}

	@SubscribeEvent
	private void handleIncomingMessage(final MessageReceivedEvent event)
	{
		final MessageChannel channel = event.getChannel();
		final Message message = event.getMessage();
		if (!event.getAuthor().isBot()) {
			final String[] args = message.getContentRaw().split(" ");
			String reply = "";
			if (event.getChannelType() == ChannelType.PRIVATE) {
				if (message.getContentRaw().startsWith("!help"))
				{
					reply = "To see the commands that are available, type !commands. Some commands require you to pair your discord account to your openrsc account. To do this, type ::pair in game to get your pairing token, then return to this DM and type !pair TOKEN";
				} else if (message.getContentRaw().startsWith("!commands")) {
					reply = "!auctions\n!stats\n!watch\n!pair\n!help";
				} else if (message.getContentRaw().startsWith("!pair")) {
					if (args.length != 2) {
						reply = "Usage: !pair TOKEN";
					} else {
						try {
							int dbID = getServer().getDatabase().playerIdFromDiscordPairToken(args[1]);
							if (dbID != -1) {
								getServer().getDatabase().pairDiscord(dbID, message.getAuthor().getIdLong());

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
									reply = "[Error 1580] Please contact an administrator.";
								}

							} else {
								reply = "Invalid pair token.";
							}
						} catch (GameDatabaseException a) {
							a.printStackTrace();
						}
					}
				} else if (message.getContentRaw().startsWith("!auctions")) {
					final ArrayList<MarketItem> auctionList = (ArrayList<MarketItem>)this.server.getWorld().getMarket().getAuctionItems().clone();
					final Iterator<MarketItem> e = auctionList.iterator();
					if (e.hasNext()) {
							int dbID = 0;
							if ((dbID = discordToDBId(message.getAuthor().getIdLong())) != 0) {
								while (e.hasNext()) {
									MarketItem a = e.next();
									if (a.getSeller() == dbID)
										reply = reply + server.getEntityHandler().getItemDef(a.getCatalogID()).getName() + " (" + a.getAmountLeft() + ") @ " + a.getPrice() + "gp ea. (" + a.getHoursLeft() + "hrs)\n";
								}
							} else
								reply = "You have not paired an account yet. Type !help for more information";
					}
					if (reply.isEmpty())
						reply = "You have no active auctions.";
					reply = "`" + reply + "`";
				} else if (message.getContentRaw().startsWith("!stats")
							|| message.getContentRaw().startsWith("!skills")) {
					int dbID = 0;
					if ((dbID = discordToDBId(message.getAuthor().getIdLong())) != 0) {
						try {
							PlayerExperience[] playerExp = getServer().getDatabase().getPlayerExp(dbID);
							String username = dbIdToUsername(dbID);
							if (!username.isEmpty()) {
								StringBuilder rep = new StringBuilder();
								rep.append("Stats for " + username + "`\n");
								for (PlayerExperience exp : playerExp) {
									int experience = exp.experience;
									int level = getServer().getConstants().getSkills()
										.getLevelForExperience(experience, getServer().getConfig().PLAYER_LEVEL_LIMIT);
									SkillDef skill = getServer().getConstants().getSkills().getSkill(exp.skillId);
									int tnl = 0;
									if (level < getServer().getConstants().getSkills().GLOBAL_LEVEL_LIMIT) {
										tnl = getServer().getConstants().getSkills().experienceCurves.get(SkillDef.EXP_CURVE.ORIGINAL)[level] - experience;
									}
									rep.append(StringUtils.rightPad(skill.getLongName(),12," ")
										+ ": " + String.format("%02d", level) + " (@ " + tnl + ")\n");
								}
								rep.append("`");
								reply = rep.toString();
							} else {
								reply = "Error 2250";
							}
						} catch (GameDatabaseException a) {
							a.printStackTrace();
						}


					} else {
						reply = "You have not paired an account yet. Type !help for more information";
					}
				} else if (message.getContentRaw().startsWith("!bug ")) {
					if (gitLabApi != null) {
						final int hasTitle = message.getContentRaw().indexOf(" -t ");
						final int hasDesc = message.getContentRaw().indexOf(" -d ");
						String title, desc;
						if (hasTitle != -1 && hasDesc != -1 &&
							hasTitle < hasDesc && hasDesc-hasTitle > 3) {
							title = message.getContentRaw().substring(hasTitle + 4, hasDesc);
							desc = message.getContentRaw().substring(hasDesc + 4);
							desc = "Submitted by: " + message.getAuthor().getName() + "\n" + "Discord Bot Submission (" + this.server.getName() + ")\n---------------------------------\n\n" + desc;
							try {
								gitLabApi.getIssuesApi().createIssue(2, title, desc);
							} catch (GitLabApiException a) {
								a.printStackTrace();
							}
						} else
							reply = "Usage: !bug -t TITLE -d DESCRIPTION";
					} else
						reply = "The bug submission service has malfunctioned. Please report this to an admin.";
				} else if (message.getContentRaw().startsWith("!watch")) {
					if (args.length > 1) {
						try {
							final String dbWatchlist = getServer().getDatabase().getWatchlist(message.getAuthor().getIdLong());
							if (args[1].equalsIgnoreCase("list")) {
								if (dbWatchlist != null) {
									String[] watchlist = dbWatchlist.split(",");
									reply = "`";
									for (String item : watchlist) {
										try {
											int itemID = Integer.parseInt(item);
											ItemDefinition itemDef = server.getEntityHandler().getItemDef(itemID);
											if (itemDef != null) {
												reply = reply + itemDef.getName() + " (" + itemID + ")\n";
											} else
												reply = reply + "ERROR (ID " + itemID + ")\n";
										} catch (NumberFormatException a) {
											a.printStackTrace();
										}
									}
									reply = reply + "`";
								} else
									reply = "You have nothing on your watchlist.";
							} else if (args[1].equalsIgnoreCase("add")) {
								if (args.length > 2) {
									int toAdd = 0;
									try {
										toAdd = Integer.parseInt(args[2]);
										ItemDefinition itemDef = server.getEntityHandler().getItemDef(toAdd);
										if (itemDef != null) {
											if (dbWatchlist != null) {
												String watchlist = dbWatchlist;
												if (!watchlist.contains(String.valueOf(toAdd))) {
													if (watchlist.split(",").length < WATCHLIST_MAX_SIZE) {
														watchlist = String.join(",", watchlist, String.valueOf(toAdd));
														getServer().getDatabase().updateWatchlist(message.getAuthor().getIdLong(), watchlist);
														reply = "Added " + itemDef.getName() + " to your watchlist.";
													} else
														reply = "Your watchlist is full. (10/10)";
												} else
													reply = "That item is already on your watchlist.";
											} else {
												getServer().getDatabase().newWatchlist(message.getAuthor().getIdLong(), String.valueOf(toAdd));
												reply = "Added " + itemDef.getName() + " to your watchlist.";
											}
										} else
											reply = "That item ID does not exist.";
									} catch (NumberFormatException a) {
										reply = "You must enter a valid number as the item ID.";
									}
								} else
									reply = "Usage: !watch add ITEMID";
							} else if (args[1].equalsIgnoreCase("del")
								|| args[1].equalsIgnoreCase("rem")) {
								if (args.length > 2) {
									int itemID = 0;
									try {
										itemID = Integer.parseInt(args[2]);
										if (dbWatchlist != null) {
											List<String> watchlist = new ArrayList<String>(Arrays.asList(dbWatchlist.split(",")));
											if (watchlist.contains(args[2])) {
												watchlist.remove(args[2]);
												if (watchlist.size() > 0) {
													StringBuilder query = new StringBuilder();
													for (String item : watchlist) {
														if (query.length() == 0) {
															query.append(item);
														} else
															query.append("," + item);
													}

													getServer().getDatabase().updateWatchlist(message.getAuthor().getIdLong(), query.toString());
												} else {
													getServer().getDatabase().deleteWatchlist(message.getAuthor().getIdLong());
												}
												ItemDefinition itemDef = server.getEntityHandler().getItemDef(Integer.parseInt(args[2]));
												if (itemDef != null)
													reply = "You have removed " + itemDef.getName() + " from your watchlist.";
												else
													reply = "You have removed " + args[2] + " from your watchlist.";
											} else
												reply = "You do not have that item on your watchlist.";
										} else
											reply = "Your watchlist is already empty.";
									} catch (NumberFormatException a) {
										reply = "You must enter a valid number as the item ID.";
									}

								} else
									reply = "Usage: !watch del ITEMID";
							} else if (args[1].equalsIgnoreCase("help")) {
								reply = "The auction watchlist feature will notify you when an item of your interest is placed on the auction house. To use it, use !watch [list add del] [item id]. To get an items ID, use https://openrsc.com/items. The number in () after the items name is its ID. You may have up to 10 items on your watchlist.";
							}
							else
								reply = "Usage: !watch [list add del help]";

						} catch (GameDatabaseException a) {
							a.printStackTrace();
						}
					} else
						reply = "Usage: !watch [list add del help]";

				}
			} else if (message.getChannel().getIdLong() == this.server.getConfig().CROSS_CHAT_CHANNEL
						&& !message.getContentRaw().isEmpty()) {
				final String strMessage = EmojiParser.parseToAliases(message.getContentRaw());

				for (Player p : this.server.getWorld().getPlayers()) {
					ActionSender.sendMessage(p, null, MessageType.GLOBAL_CHAT, "@whi@[@gr2@D>G@whi@] @or1@" + message.getAuthor().getName() + "@yel@: " + strMessage, 0, null);
				}
			} else {
				if (message.getContentRaw().startsWith("!help")) {
					reply = "Please use !help in a DM to me for more information.";
				}
			}

			if (!reply.isEmpty()) {
				channel.sendMessage(reply).queue();
			}
		}
	}

	public void sendMessage(final String message) {
		try {
			final TextChannel textChannel = jda.getTextChannelById(this.server.getConfig().CROSS_CHAT_CHANNEL);
			if (textChannel != null) {
				textChannel.sendMessage(message).queue();
			}
		} catch (Exception ex) {
			LOGGER.catching(ex);
			LOGGER.error("Discord Bot could not send message to CROSS_CHAT_CHANNEL.");
		}
	}

	public void sendPM(final long channelID, final String message) {
		final PrivateChannel textChannel = jda.getPrivateChannelById(channelID);
		final User user = jda.getUserById(channelID);
		if (user != null)
			user.openPrivateChannel().queue((channel) -> {
				channel.sendMessage(message).queue();
			});
	}

	public final Server getServer() {
		return server;
	}

	public void auctionAdd(final MarketItem addItem) {
		final String pluralHandlerMessage = addItem.getAmount() > 1
				? "%d x %s, priced at %d coins each, auctioned by %s."
				: "%d x %s, priced at %d coins, auctioned by %s.";

		final String addMessage = String.format(pluralHandlerMessage,
				addItem.getAmount(),
				getServer().getEntityHandler().getItemDef(addItem.getCatalogID()).getName(),
				addItem.getPrice() / addItem.getAmount(),
				addItem.getSellerName(),
				addItem.getHoursLeft()
		);

		auctionSendToDiscord(addMessage);

		//TODO: Add a delay between auction post and watchlist notification.
		try {
			final DiscordWatchlist[] watchlists = getServer().getDatabase().getWaitlists();
			for (DiscordWatchlist discordWatchlist : watchlists) {
				final String watchlist = discordWatchlist.list;
				if (watchlist.contains(String.valueOf(addItem.getCatalogID()))) {
					try {
						final long discordID = discordWatchlist.discordId;
						final ItemDefinition itemDef = server.getEntityHandler().getItemDef(addItem.getCatalogID());
						if (itemDef != null) {
							String message = "[" + server.getConfig().SERVER_NAME + " watchlist] " + itemDef.getName() + " ( " + addItem.getAmountLeft() + " @ " + addItem.getPrice() + "gp)";
							sendPM(discordID, message);
						}
					} catch (final NumberFormatException a) {
						a.printStackTrace();
					}
				}
			}
		} catch (final GameDatabaseException a) {
			a.printStackTrace();
		}
	}

	public void auctionBuy(final MarketItem buyItem) {
		final String buyMessage = String.format("%s purchased from %s.  %d left in auction.",
			getServer().getEntityHandler().getItemDef(buyItem.getCatalogID()).getName(),
				buyItem.getSellerName(),
				buyItem.getAmountLeft()
		);

		auctionSendToDiscord(buyMessage);
	}

	public void auctionCancel(final MarketItem cancelItem) {
		final String cancelMessage = String.format("%d x %s cancelled from auction by %s.",
				cancelItem.getAmount(),
				getServer().getEntityHandler().getItemDef(cancelItem.getCatalogID()).getName(),
				cancelItem.getSellerName()
		);

		auctionSendToDiscord(cancelMessage);
	}

	public void auctionModDelete(final MarketItem deleteItem) {
		final String cancelMessage = String.format("%d x %s, auctioned by %s, has been deleted by moderator.",
				deleteItem.getAmount(),
				getServer().getEntityHandler().getItemDef(deleteItem.getCatalogID()).getName(),
				deleteItem.getSellerName()
		);

		auctionSendToDiscord(cancelMessage);
	}

	private void auctionSendToDiscord(final String message) {
		if (getServer().getConfig().WANT_DISCORD_AUCTION_UPDATES) {
			auctionRequests.add(message);
		}
	}

	public void staffCommandLog(final Player player, final String command) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
		final String commandMessage = String.format("%s %s %s %s: %s used command: %s",
				"[" + dateFormat.format(calendar.getTime()) +  "]",
				"[" + player.getWorld().getServer().getConfig().SERVER_NAME + "]",
				Group.getGlobalMessageName(player.getGroupID()),
				player.getUsername(),
				"[X: " + player.getX() + ", Y: " + player.getY() + "]",
				command
		);

		staffCommandSendToDiscord(commandMessage);
	}

	private void staffCommandSendToDiscord(final String message) {
		if (getServer().getConfig().WANT_DISCORD_STAFF_COMMANDS) {
			staffCommandRequests.add(message);
		}
	}

	public void playerLog(final Player player, final String text) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar calendar = Calendar.getInstance();
		final String playerMessage = String.format("%s %s %s: %s %s: %s",
				"[" + dateFormat.format(calendar.getTime()) +  "]",
				"[" + player.getWorld().getServer().getConfig().SERVER_NAME + "]",
				player.getUsername(),
				"[X: " + player.getX() + ", Y: " + player.getY() + "]",
				"[Client Version: " + player.getClientVersion() + "]",
				text
		);
		generalLog(playerMessage);
	}

	private void generalLog(final String message) {
		if (getServer().getConfig().WANT_DISCORD_GENERAL_LOGGING) {
			generalLogs.add(message);
		}
	}

	public void monitoringSendServerBehind(final String message, final boolean showEventData) {
		monitoringSendToDiscord(message + (showEventData ? "\r\n" + getServer().getGameEventHandler().buildProfilingDebugInformation(false) : ""));
	}

	private void monitoringSendToDiscord(final String message) {
		final long now = System.currentTimeMillis();

		if(getServer().getConfig().WANT_DISCORD_MONITORING_UPDATES && now >= (monitoringLastUpdate + 3600)) {
			monitoringRequests.add(message);
			monitoringLastUpdate = now;
		}
	}

	public void reportSendToDiscord(GameReport gameReport, String serverName) {
		if(!getServer().getConfig().WANT_DISCORD_REPORT_ABUSE_UPDATES) {
			return;
		}

		StringBuilder reportMessage = new StringBuilder("Unix time: ");
		String unixTimestamp = String.format("%d", (System.currentTimeMillis() / 1000));
		reportMessage.append(unixTimestamp);
		reportMessage.append("\n\n**");
		reportMessage.append(gameReport.reporterPlayer.getUsername());
		reportMessage.append("** reported **");
		reportMessage.append(gameReport.reported);
		reportMessage.append("** for *\"");
		reportMessage.append(Constants.reportReasons.getOrDefault((int)gameReport.reason, "Unknown Reason"));
		reportMessage.append("\"*\n");

		if (gameReport.reported_x != -1) {
			reportMessage.append("**" + gameReport.reported + "** was at **(" + gameReport.reported_x + ", " + gameReport.reported_y + ")**");
		} else {
			reportMessage.append("**" + gameReport.reported + "** was offline at the time of report.");
		}

		if (gameReport.chatlog.toString().length() > 0) {
			reportMessage.append("\n\n**===  Chatlog Evidence  ===**\n```\n");
			reportMessage.append(gameReport.chatlog.toString());
			reportMessage.append("\n```");
		} else {
			if (gameReport.suggestsOrMutes) {
				reportMessage.append("\n");
			}
		}

		if (gameReport.suggestsOrMutes) {
			reportMessage.append("\n**The user was muted for this offense!**");
		}
		DiscordEmbed reportEmbed = new DiscordEmbed("**===  " + serverName + "  ===**",
		"",
		Constants.reportDiscordColours.getOrDefault((int)gameReport.reason, "0"),
		reportMessage.toString()
		);
		reportAbuseRequests.add(reportEmbed);
	}

	private static void sendToDiscord(final String webhookUrl, final String message) throws Exception {
		final StringBuilder sb = new StringBuilder();
		JsonUtils.quoteAsString(message, sb);

		final String jsonPostBody = String.format("{\"content\": \"%s\"}", sb);

		final java.net.URL url = new java.net.URL(webhookUrl);

		final URLConnection con = url.openConnection();
		final HttpURLConnection http = (HttpURLConnection) con;
		con.addRequestProperty("User-Agent", "openrsc");
		http.setRequestMethod("POST");
		http.setDoOutput(true);
		http.setUseCaches(false);

		final byte[] out = jsonPostBody.getBytes(StandardCharsets.UTF_8);
		final int length = out.length;

		http.setFixedLengthStreamingMode(length);
		http.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
		try {
			http.connect();
			try (OutputStream os = http.getOutputStream()) {
				os.write(out);
			}
			/* Debugging code, read response from discord
			System.out.println(http.getResponseMessage());
			BufferedReader br = new BufferedReader(new InputStreamReader(http.getInputStream()));
			String line = "";
			while (line != null) {
				line = br.readLine();
				if (line != null) {
					System.out.println(line);
				}
			}
			*/

		} catch (Exception e) {
			LOGGER.error(e);
		}
	}

	private static void sendEmbedToDiscord(final String webhookUrl, final DiscordEmbed discordEmbed) throws Exception {
		final String jsonPostBody = String.format("{ \"content\": \"%s\", \"embeds\": [ {" +
			" \"title\": \"%s\"," +
			" \"color\": %s," +
			" \"description\": \"%s\" } ] }", discordEmbed.content, discordEmbed.title, discordEmbed.color, discordEmbed.description);

		final java.net.URL url = new java.net.URL(webhookUrl);

		final URLConnection con = url.openConnection();
		final HttpURLConnection http = (HttpURLConnection) con;
		con.addRequestProperty("User-Agent", "openrsc");
		http.setRequestMethod("POST");
		http.setDoOutput(true);
		http.setUseCaches(false);

		final byte[] out = jsonPostBody.getBytes(StandardCharsets.UTF_8);
		final int length = out.length;

		http.setFixedLengthStreamingMode(length);
		http.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
		try {
			http.connect();
			try (OutputStream os = http.getOutputStream()) {
				os.write(out);
			}
			/* Debugging code, read response from discord
			System.out.println(http.getResponseMessage());
			BufferedReader br = new BufferedReader(new InputStreamReader(http.getInputStream()));
			String line = "";
			while (line != null) {
				line = br.readLine();
				if (line != null) {
					System.out.println(line);
				}
			}
			/**/

		} catch (Exception e) {
			LOGGER.error(e);
		}
	}

	public int discordToDBId(final long discord) {
		try {
			getServer().getDatabase().playerIdFromDiscordId(discord);
		} catch (GameDatabaseException a) {
			a.printStackTrace();
		}
		return 0;
	}

	public String dbIdToUsername(final int dbId) {
		try {
			return getServer().getDatabase().usernameFromId(dbId);
		} catch (GameDatabaseException a) {
			a.printStackTrace();
		}
		return "";
	}

	@Override
	public void run()  {
		String message = null;
		DiscordEmbed embed = null;

		try {
			while ((message = auctionRequests.poll()) != null) {
				sendToDiscord(getServer().getConfig().DISCORD_AUCTION_WEBHOOK_URL, message);
			}
			while ((message = staffCommandRequests.poll()) != null) {
				sendToDiscord(getServer().getConfig().DISCORD_STAFF_COMMANDS_WEBHOOK_URL, message);
			}
			while ((message = monitoringRequests.poll()) != null) {
				sendToDiscord(getServer().getConfig().DISCORD_MONITORING_WEBHOOK_URL, message);
			}
			while ((embed = reportAbuseRequests.poll()) != null) {
				sendEmbedToDiscord(getServer().getConfig().DISCORD_REPORT_ABUSE_WEBHOOK_URL, embed);
			}
			while ((embed = naughtyWordsRequests.poll()) != null) {
				sendEmbedToDiscord(getServer().getConfig().DISCORD_NAUGHTY_WORDS_WEBHOOK_URL, embed);
			}
			while ((message = generalLogs.poll()) != null) {
				sendToDiscord(getServer().getConfig().DISCORD_GENERAL_WEBHOOK_URL, message);
			}
			while ((embed = downtimeReports.poll()) != null) {
				sendEmbedToDiscord(getServer().getConfig().DISCORD_DOWNTIME_REPORT_WEBHOOK_URL, embed);
			}
		} catch (final Exception e) {
			LOGGER.catching(e);
		}

	}

	public void start() {
		scheduledExecutor = Executors.newSingleThreadScheduledExecutor(
				new ServerAwareThreadFactory(
						server.getName()+" : DiscordServiceThread",
						server.getConfig()
				)
		);
		scheduledExecutor.scheduleAtFixedRate(this, 0, 50, TimeUnit.MILLISECONDS);
		running = true;
	}

	public void stop() {
		scheduledExecutor.shutdown();
		try {
			final boolean terminationResult = scheduledExecutor.awaitTermination(1, TimeUnit.MINUTES);
			if (!terminationResult) {
				LOGGER.error("DiscordService thread termination failed");
				List<Runnable> skippedTasks = scheduledExecutor.shutdownNow();
				LOGGER.error("{} task(s) never commenced execution", skippedTasks.size());
			}
		} catch (final InterruptedException e) {
			LOGGER.catching(e);
		}
		running = false;
		run();
		clearRequests();
		scheduledExecutor = null;
	}

	private void clearRequests() {
		monitoringRequests.clear();
		auctionRequests.clear();
		staffCommandRequests.clear();
		reportAbuseRequests.clear();
		naughtyWordsRequests.clear();
		generalLogs.clear();
	}

	public final boolean isRunning() {
		return running;
	}

	public void reportNaughtyWordToDiscord(Player sender, String originalMessage, String message, ArrayList<String> stringProblems, String context) {
		if(!getServer().getConfig().WANT_DISCORD_NAUGHTY_WORDS_UPDATES) {
			return;
		}

		if (context.contains("private messages")) {
			// Reporting private messages turned out to be too detrimental for user privacy.
			// It also increases moderator workload, which is the opposite of what this feature should do.
			return;
		}
		String titleContent;
		String embedColour;

		StringBuilder mainContent = new StringBuilder();
		mainContent.append("**");
		mainContent.append(sender.getUsername());
		mainContent.append("** attempted to send the following message to **");
		mainContent.append(context);
		if (context.equals("public chat")) {
			mainContent.append("** near (");
			mainContent.append(sender.getX());
			mainContent.append(", ");
			mainContent.append(sender.getY());
			mainContent.append("):");
		} else {
			mainContent.append(":**");
		}
		mainContent.append("\n```\n");
		mainContent.append(originalMessage);
		if (originalMessage.equals(message)) {
			mainContent.append("\n\n```\n**Their message was sent unaltered.**\n");
			titleContent = "**===  Message by " + sender.getUsername() + " was flagged on " + sender.getConfig().SERVER_NAME + "  ===**";
			embedColour = "13851276"; // average of colour between yellow & report abuse button. Turns out to be pink.
		} else {
			mainContent.append("\n\n```\n**This message was sent instead:**\n```\n");
			mainContent.append(message);
			mainContent.append("\n\n```");
			titleContent = "**===  Message by " + sender.getUsername() + " was censored on " + sender.getConfig().SERVER_NAME + "  ===**";
			embedColour = "10949120"; // same colour as the report abuse button
		}
		mainContent.append("\n**Filtering rules triggered:**\n```\n");
		for (String problem : stringProblems) {
			mainContent.append(problem).append("\n");
		}
		mainContent.append("```");

		DiscordEmbed embed = new DiscordEmbed(
			titleContent,
			"",
			embedColour,
			mainContent.toString()
		);

		naughtyWordsRequests.add(embed);
	}

	public void reportAlertWordToDiscord(Player sender, String originalMessage, ArrayList<String> alertWords, String context) {
		if(!getServer().getConfig().WANT_DISCORD_NAUGHTY_WORDS_UPDATES) {
			return;
		}

		if (context.contains("private messages")) {
			// Reporting private messages turned out to be too detrimental for user privacy.
			return;
		}

		StringBuilder mainContent = new StringBuilder();
		mainContent.append("**");
		mainContent.append(sender.getUsername());
		mainContent.append("** sent the following message to **");
		mainContent.append(context);
		if (context.equals("public chat")) {
			mainContent.append("** near (");
			mainContent.append(sender.getX());
			mainContent.append(", ");
			mainContent.append(sender.getY());
			mainContent.append("):");
		} else {
			mainContent.append(":**");
		}
		mainContent.append("\n```\n");
		mainContent.append(originalMessage);
		mainContent.append("\n\n```\n**Alertwords found:**\n```\n");
		for (String alertWord : alertWords) {
			mainContent.append(alertWord).append("\n");
		}
		mainContent.append("```");

		DiscordEmbed embed = new DiscordEmbed(
			"**===  Message by " + sender.getUsername() + " contained alertword on " + sender.getConfig().SERVER_NAME + "  ===**",
			"",
			"16753433", // yellow
			mainContent.toString()
		);

		naughtyWordsRequests.add(embed);
	}

	public void reportNaughtyWordChangedToDiscord(Player sender, String word, int wordType, boolean added) {
		if(!getServer().getConfig().WANT_DISCORD_NAUGHTY_WORDS_UPDATES) {
			return;
		}
		StringBuilder mainContent = new StringBuilder();
		mainContent.append("**");
		mainContent.append(sender.getUsername());
		mainContent.append("** ");
		if (added) {
			mainContent.append("added");
		} else {
			mainContent.append("removed");
		}
		mainContent.append(" the word **");
		mainContent.append(word);
		mainContent.append("** ");
		if (added) {
			mainContent.append("to");
		} else {
			mainContent.append("from");
		}
		if (wordType == MessageFilterType.goodword) {
			mainContent.append(" **goodwords.txt**");
		} else if (wordType == MessageFilterType.badword) {
			mainContent.append(" **badwords.txt**");
		} else {
			mainContent.append(" **alertwords.txt**");
		}
		mainContent.append("\n\nThis change was made on the **");
		mainContent.append(sender.getWorld().getServer().getName());
		mainContent.append("** server, but will take effect on all servers with filtering enabled either when they reboot, or if the command **::syncgoodwordsbadwords** aka **::sgb** is run.");


		StringBuilder titleBuilder = new StringBuilder("**===  ");
		if (wordType == MessageFilterType.goodword) {
			titleBuilder.append("goodword");
		} else if (wordType == MessageFilterType.badword) {
			titleBuilder.append("badword");
		} else {
			titleBuilder.append("alertword");
		}
		if (added) {
			titleBuilder.append(" added by ");
		} else {
			titleBuilder.append(" removed by ");
		}
		titleBuilder.append(sender.getUsername());
		titleBuilder.append("   ===**");


		DiscordEmbed embed = new DiscordEmbed(
			titleBuilder.toString(),
			"",
			"1087508", // pleasant green colour, taken from RSC tree
			mainContent.toString()
		);

		naughtyWordsRequests.add(embed);
	}

	public void reportBabyModeFilteredMessageToDiscord(Player sender, String originalMessage, String context) {
		if(!getServer().getConfig().WANT_DISCORD_NAUGHTY_WORDS_UPDATES) {
			return;
		}

		if (context.contains("private messages")) {
			// Reporting private messages turned out to be too detrimental for user privacy.
			return;
		}

		StringBuilder mainContent = new StringBuilder();
		mainContent.append("**");
		mainContent.append(sender.getUsername());
		mainContent.append("** sent the following message to **");
		mainContent.append(context);
		if (context.equals("public chat")) {
			mainContent.append("** near (");
			mainContent.append(sender.getX());
			mainContent.append(", ");
			mainContent.append(sender.getY());
			mainContent.append("):");
		} else {
			mainContent.append(":**");
		}
		mainContent.append("\n```\n");
		mainContent.append(originalMessage);
		mainContent.append("```\n");

		DiscordEmbed embed = new DiscordEmbed(
			"**===  Message by " + sender.getUsername() + " was filtered on " + sender.getConfig().SERVER_NAME + " due to **Baby Mode**  ===**",
			"",
			"8942042", // purple
			mainContent.toString()
		);

		naughtyWordsRequests.add(embed);
	}

	public void reportSpaceFilteringConfigChangeToDiscord(Player sender) {
		if(!getServer().getConfig().WANT_DISCORD_NAUGHTY_WORDS_UPDATES || !getServer().getConfig().SERVER_SIDED_WORD_FILTERING) {
			return;
		}

		StringBuilder mainContent = new StringBuilder();
		mainContent.append("**");
		mainContent.append(sender.getUsername());
		mainContent.append("** ");
		if (getServer().getConfig().SERVER_SIDED_WORD_SPACE_FILTERING) {
			mainContent.append("enabled");
		} else {
			mainContent.append("disabled");
		}
		mainContent.append(" **S P A C E   F I L T E R I N G** ");

		mainContent.append("\n\nThis change was made on the **");
		mainContent.append(sender.getWorld().getServer().getName());
		mainContent.append("** server.");

		DiscordEmbed embed = new DiscordEmbed(
			"",
			"",
			"1087508", // pleasant green colour, taken from RSC tree
			mainContent.toString()
		);

		naughtyWordsRequests.add(embed);
	}

	public void reportBabyModeChangeToDiscord(Player sender) {
		if(!getServer().getConfig().WANT_DISCORD_NAUGHTY_WORDS_UPDATES || !getServer().getConfig().SERVER_SIDED_WORD_FILTERING) {
			return;
		}

		StringBuilder mainContent = new StringBuilder();
		mainContent.append("**");
		mainContent.append(sender.getUsername());
		if (getServer().getConfig().BABY_MODE_LEVEL_THRESHOLD > 0) {
			mainContent.append("** set Baby Mode level threshold to: **");
			mainContent.append(getServer().getConfig().BABY_MODE_LEVEL_THRESHOLD);
			mainContent.append("**");
		} else {
			mainContent.append("** disabled Baby Mode.");
		}

		mainContent.append("\n\nThis change was made on the **");
		mainContent.append(sender.getWorld().getServer().getName());
		mainContent.append("** server.");

		DiscordEmbed embed = new DiscordEmbed(
			"",
			"",
			"1087508", // pleasant green colour, taken from RSC tree
			mainContent.toString()
		);

		naughtyWordsRequests.add(embed);
	}

	public void reportDowntimeToDiscord(long startmillis, long endmillis, boolean unloaded, int onlineCount) {
		if(!getServer().getConfig().WANT_DISCORD_DOWNTIME_REPORTS) {
			return;
		}
		long downtime = endmillis - startmillis;
		if (downtime < getServer().getConfig().DISCORD_DOWNTIME_REPORTS_MILLISECONDS_DOWN_BEFORE_REPORT) {
			return;
		}

		StringBuilder mainContent = new StringBuilder();
		mainContent.append("**");
		mainContent.append(getServer().getName());
		mainContent.append(" is now back online...!");
		mainContent.append("**");

		mainContent.append("\n\nThe server detected it was offline at <t:");
		mainContent.append(startmillis / 1000);
		mainContent.append("> (");
		mainContent.append(startmillis);
		mainContent.append(") and recovered at <t:");
		mainContent.append(endmillis / 1000);
		mainContent.append("> (");
		mainContent.append(endmillis);
		mainContent.append("). A total downtime of **");

		if (downtime > 60000) {
			mainContent.append(downtime / 60000);
			mainContent.append(" minutes.**");
		} else {
			mainContent.append(downtime / 1000);
			mainContent.append(" seconds.**");
		}

		if (unloaded) {
			mainContent.append("\n\nBecause the downtime was so long, all players were unloaded from the server, after 10 seconds.");
		}

		mainContent.append("\n\n");
		mainContent.append(onlineCount);
		mainContent.append(" accounts were logged in at the time of the outage.");

		DiscordEmbed embed = new DiscordEmbed(
			"",
			"",
			"16753433", // yellow
			mainContent.toString()
		);

		downtimeReports.add(embed);
	}
}
