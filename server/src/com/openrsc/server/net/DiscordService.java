package com.openrsc.server.net;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.openrsc.server.Server;
import com.openrsc.server.content.market.MarketItem;
import com.openrsc.server.database.GameDatabaseException;
import com.openrsc.server.database.struct.DiscordWatchlist;
import com.openrsc.server.database.struct.PlayerExperience;
import com.openrsc.server.external.ItemDefinition;
import com.openrsc.server.external.SkillDef;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
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
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DiscordService implements Runnable{
	private static final int WATCHLIST_MAX_SIZE = 10;
	private ScheduledExecutorService scheduledExecutor;

	private final Queue<String> auctionRequests = new ConcurrentLinkedQueue<String>();
	private final Queue<String> monitoringRequests = new ConcurrentLinkedQueue<String>();

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
		final TextChannel textChannel = jda.getTextChannelById(this.server.getConfig().CROSS_CHAT_CHANNEL);
		if (textChannel != null) {
			textChannel.sendMessage(message).queue();
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

	public void monitoringSendServerBehind(final String message, final boolean showEventData) {
		try {
			monitoringSendToDiscord(message + (showEventData ? "\r\n" + getServer().getGameEventHandler().buildProfilingDebugInformation(false) : ""));
		} catch(final Exception e) {
			LOGGER.catching(e);
		}
	}

	private void auctionSendToDiscord(final String message) {
		if(getServer().getConfig().WANT_DISCORD_AUCTION_UPDATES) {
			auctionRequests.add(message);
		}
	}

	private void monitoringSendToDiscord(final String message) {
		final long now = System.currentTimeMillis();

		if(getServer().getConfig().WANT_DISCORD_MONITORING_UPDATES && now >= (monitoringLastUpdate + 3600)) {
			monitoringRequests.add(message);
			monitoringLastUpdate = now;
		}
	}

	private static void sendToDiscord(final String webhookUrl, final String message) throws Exception {
		final StringBuilder sb = new StringBuilder();
		JsonUtils.quoteAsString(message, sb);

		final String jsonPostBody = String.format("{\"content\": \"%s\"}", sb);

		final java.net.URL url = new java.net.URL(webhookUrl);

		final URLConnection con = url.openConnection();
		final HttpURLConnection http = (HttpURLConnection) con;
		http.setRequestMethod("POST");
		http.setDoOutput(true);

		final byte[] out = jsonPostBody.getBytes(StandardCharsets.UTF_8);
		final int length = out.length;

		http.setFixedLengthStreamingMode(length);
		http.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
		try {
			http.connect();
			try (OutputStream os = http.getOutputStream()) {
				os.write(out);
			}
		}
		catch (Exception e) {
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
		synchronized(running) {
			String message = null;

			try {
				while ((message = auctionRequests.poll()) != null) {
					sendToDiscord(getServer().getConfig().DISCORD_AUCTION_WEBHOOK_URL, message);
				}

				while ((message = monitoringRequests.poll()) != null) {
					sendToDiscord(getServer().getConfig().DISCORD_MONITORING_WEBHOOK_URL, message);
				}
			} catch (final Exception e) {
				LOGGER.catching(e);
			}
		}
	}

	public void start() {
		synchronized(running) {
			scheduledExecutor = Executors.newSingleThreadScheduledExecutor(
					new ServerAwareThreadFactory(
							server.getName()+" : DiscordServiceThread",
							server.getConfig()
					)
			);
			scheduledExecutor.scheduleAtFixedRate(this, 0, 50, TimeUnit.MILLISECONDS);
			running = true;
		}
	}

	public void stop() {
		synchronized(running) {
			scheduledExecutor.shutdown();
			try {
				final boolean terminationResult = scheduledExecutor.awaitTermination(1, TimeUnit.MINUTES);
				if (!terminationResult) {
					LOGGER.error("DiscordService thread termination failed");
				}
			} catch (final InterruptedException e) {
				LOGGER.catching(e);
			}
			clearRequests();
			scheduledExecutor = null;
			running = false;
		}
	}

	private void clearRequests() {
		monitoringRequests.clear();
		auctionRequests.clear();
	}

	public final boolean isRunning() {
		return running;
	}
}
