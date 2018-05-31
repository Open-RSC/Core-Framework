package org.rscemulation.server.npchandler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.rscemulation.server.Config;
import org.rscemulation.server.ServerBootstrap;
import org.rscemulation.server.database.DefaultTransaction;
import org.rscemulation.server.database.game.Save;
import org.rscemulation.server.event.DelayedQuestChat;
import org.rscemulation.server.event.SingleEvent;
import org.rscemulation.server.model.ChatMessage;
import org.rscemulation.server.model.InvItem;
import org.rscemulation.server.model.MenuHandler;
import org.rscemulation.server.model.Npc;
import org.rscemulation.server.model.Player;
import org.rscemulation.server.model.World;
import org.rscemulation.server.util.DataConversions;

public class Subscription_Vendor implements NpcHandler {
	
	private final static class SubscriptionTransferTransaction
		extends
			DefaultTransaction
	{
		private /** final */ static PreparedStatement RESET_CARDS, LOG;
		
		static
		{
			DefaultTransaction dummy = new SubscriptionTransferTransaction();
			try
			{
				Connection connection = dummy.getConnection();
				RESET_CARDS = connection.prepareStatement("UPDATE `users` SET `sub_due` = '0', `sub_given` = UNIX_TIMESTAMP() WHERE `id` = ?");
				LOG = connection.prepareStatement("INSERT INTO `"+Config.LOG_DB_NAME+"`.`game_collect` (`user`, `account`, `time`, `amount`, `ip`) VALUES (?, ?, ?, ?, ?);");
			}
			catch(SQLException e)
			{
				throw (ExceptionInInitializerError)new ExceptionInInitializerError().initCause(e);
			}
		}
		
		private final int owner, amount;
		private final long user;
		private final String ip;
		private final int timestamp = DataConversions.getTimeStamp();
		
		private SubscriptionTransferTransaction()
		{
			this.owner = 0;
			this.amount = 0;
			this.user = 0;
			this.ip = null;
		}
		
		public SubscriptionTransferTransaction(int owner, long user, int amount, String ip)
		{
			this.owner = owner;
			this.amount = amount;
			this.user = user;
			this.ip = ip;
		}

		@Override
		public Integer call()
			throws
				SQLException
		{
			RESET_CARDS.setInt(1, owner);
			RESET_CARDS.executeUpdate();
			
			LOG.setLong(1, user); LOG.setInt(2, owner); LOG.setInt(3, timestamp); LOG.setInt(4, amount); LOG.setString(5, ip);
			LOG.executeUpdate();
			return 0;
		}

		@Override
		public boolean retryOnFatalError() {
			// TODO Auto-generated method stub
			return false;
		}
	}
	
	public void handleNpc(final Npc npc, final Player owner) throws Exception {
		npc.blockedBy(owner);
		owner.setBusy(true);
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Hello, how can I help you?"}, true) {
			public void finished() {
				World.getDelayedEventHandler().add(new SingleEvent(owner, 1500) {
					public void action() {
						owner.setBusy(false);
						owner.sendMenu(new String[] {"I've just purchased a Subscription Card, can I have it please?"});
						owner.setMenuHandler(new MenuHandler(new String[] {"I've just purchased a Subscription Card, can I have it please?"}) {
							public void handleReply(final int option, final String reply) {
								owner.setBusy(true);
								for (Player informee : owner.getViewArea().getPlayersInView())
									informee.informOfChatMessage(new ChatMessage(owner, reply, npc));
								switch (option) {
									case 0:
										checkCard(npc, owner);
									break;
								}
							}
						});
					}
				});
			}
		});
	}
	
	private final void checkCard(final Npc npc, final Player owner) {
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Let me check..."}) {
			public void finished() {
				int cardAvailable = 0;
				try(Connection connection = DriverManager.getConnection("jdbc:mysql://" + Config.DB_HOST + "/" + Config.DB_NAME, Config.DB_LOGIN, Config.DB_PASS))
				{
					try(Statement statement = connection.createStatement())
					{
						ResultSet result = statement.executeQuery("SELECT `sub_due` FROM `users` WHERE `id` = '" + owner.getAccount() + "'");						
						if (result.next())
						{
							cardAvailable = result.getInt("sub_due");
						}
						else
						{
							throw new SQLException("Unable to read `sub_due` from `users`");
						}
					}
				}
				catch(SQLException e)
				{
					errorCheckingCard(npc, owner);
				}
				if (cardAvailable <= 30)
				{
					if (cardAvailable > 0)
					{
						ownerHasSubCard(npc, owner, cardAvailable);
					}
					else
					{
						noSubCard(npc, owner);
					}
				}
				else
				{
					tooManyCards(npc, owner);
				}
			}
		});
	}
	
	private final void noSubCard(final Npc npc, final Player owner) {
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Hm, it appears that you don't have any Subscription Cards due", "If you haven't done so, please PM Pyru your PayPal Transaction ID"}) {
			public void finished() {
				owner.setBusy(false);
				npc.unblock();
			}
		});
	}
	
	private final void ownerHasSubCard(final Npc npc, final Player owner, final int amount) {
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {amount > 1 ? "You have " + amount + " subscription cards ready for you..." : "You have a subscription card ready for you..."}) {
			public void finished() {
				if (owner.getInventory().canHold(amount)) {
					Save s = new Save(owner);
					ServerBootstrap.getDatabaseService().submit(s, s.new DefaultSaveListener());
					World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"There you go"}) {
						public void finished() {
							ServerBootstrap.getDatabaseService().submit(new SubscriptionTransferTransaction(owner.getAccount(), owner.getUsernameHash(), amount, owner.getIP()));
							owner.sendMessage("The Subscription Vendor hands you " + amount + " Subscription Card(s)");
							for (int i = 0; i < amount; i++)
								owner.getInventory().add(new InvItem(1304, 1));
							owner.sendInventory();								
							owner.setBusy(false);
							npc.unblock();
						}
					});
				} else {
					World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Ah, you don't have enough room to hold them", "Come back when you have " + amount + " free inventory space(s)"}) {
						public void finished() {
							owner.setBusy(false);
							npc.unblock();
						}
					});
				}
			}
		});
	}
	
	private final void tooManyCards(final Npc npc, final Player owner) {
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"I see you have bought more cards than your inventory can hold", "Please contact a staff member to manually recieve your Subscription Cards."}) {
			public void finished() {
				owner.setBusy(false);
				npc.unblock();
			}
		});
	}
	
	private final void errorCheckingCard(final Npc npc, final Player owner) {
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"There seems to have been an error checking your card status", "Please contact a staff member for support."}) {
			public void finished() {
				owner.setBusy(false);
				npc.unblock();
			}
		});
	}
}