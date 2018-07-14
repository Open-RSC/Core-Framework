package org.openrsc.server.logging;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.openrsc.server.Config;
import org.openrsc.server.logging.model.AutoBanLog;
import org.openrsc.server.logging.model.ChatLog;
import org.openrsc.server.logging.model.DeathLog;
import org.openrsc.server.logging.model.DropLog;
import org.openrsc.server.logging.model.DuelLog;
import org.openrsc.server.logging.model.ErrorLog;
import org.openrsc.server.logging.model.ExploitLog;
import org.openrsc.server.logging.model.GenericLog;
import org.openrsc.server.logging.model.GlobalLog;
import org.openrsc.server.logging.model.Log;
import org.openrsc.server.logging.model.PickUpLog;
import org.openrsc.server.logging.model.PlayerLoginLog;
import org.openrsc.server.logging.model.PrivateMessageLog;
import org.openrsc.server.logging.model.ReportLog;
import org.openrsc.server.logging.model.ScriptLog;
import org.openrsc.server.logging.model.ShopLog;
import org.openrsc.server.logging.model.TradeLog;
import org.openrsc.server.logging.model.eventLog;


public class Logger extends Thread {
	
	private final Connection connection;
	
	private static StringBuffer globalMessageLog, privateMessageLog, dropLog, pickupLog, loginLog, tradeLog, exploitLog, duelLog, deathLog, chatLog, errorLog, genericLog, reportLog, shopLog, scriptLog, autoBanLog, eventLog;

	private static int globalMessageLogResetLength, privateMessageLogResetLength, dropLogResetLength, pickUpLogResetLength, loginLogResetLength, tradeLogResetLength, exploitLogResetLength, duelLogResetLength, deathLogResetLength, chatLogResetLength, errorLogResetLength, genericLogResetLength, reportLogResetLength, shopLogResetLength, scriptLogResetLength, autoBanLogResetLength, eventLogResetLength;

	public Logger()
		throws
			SQLException
	{
		connection = DriverManager.getConnection("jdbc:mysql://" + Config.DB_HOST + "/" + Config.LOG_DB_NAME + "?autoReconnect=true", Config.DB_LOGIN, Config.DB_PASS);
		globalMessageLog = new StringBuffer("INSERT INTO `game_global` (`user`, `account`, `ip`, `time`, `message`) VALUES ");
		globalMessageLogResetLength = globalMessageLog.length();
		
		privateMessageLog = new StringBuffer("INSERT INTO `game_pm` (`sender`, `sender_account`, `sender_ip`, `reciever`, `reciever_account`, `reciever_ip`, `time`, `message`) VALUES ");
		privateMessageLogResetLength = privateMessageLog.length();
		
		dropLog = new StringBuffer("INSERT INTO `game_drop` (`user`, `account`, `ip`, `x`, `y`, `item`, `amount`, `time`) VALUES ");
		dropLogResetLength = dropLog.length();
		
		pickupLog = new StringBuffer("INSERT INTO `game_pickup` (`user`, `account`, `ip`, `x`, `y`, `item`, `amount`, `time`) VALUES ");
		pickUpLogResetLength = pickupLog.length();
		
		loginLog = new StringBuffer("INSERT INTO `game_login`(`user`, `account`, `time`, `ip`) VALUES ");
		loginLogResetLength = loginLog.length();
		
		tradeLog = new StringBuffer("INSERT INTO `game_trade` (`user1`, `account1`, `user2`, `account2`, `user1_ip`, `time`, `user2_ip`, `user1_item1`, `user1_amount1`, `user1_item2`, `user1_amount2`, `user1_item3`, `user1_amount3`, `user1_item4`, `user1_amount4`, `user1_item5`, `user1_amount5`, `user1_item6`, `user1_amount6`, `user1_item7`, `user1_amount7`, `user1_item8`, `user1_amount8`, `user1_item9`, `user1_amount9`, `user1_item10`, `user1_amount10`, `user1_item11`, `user1_amount11`, `user1_item12`, `user1_amount12`, `user2_item1`, `user2_amount1`, `user2_item2`, `user2_amount2`, `user2_item3`, `user2_amount3`, `user2_item4`, `user2_amount4`, `user2_item5`, `user2_amount5`, `user2_item6`, `user2_amount6`, `user2_item7`, `user2_amount7`, `user2_item8`, `user2_amount8`, `user2_item9`, `user2_amount9`, `user2_item10`, `user2_amount10`, `user2_item11`, `user2_amount11`, `user2_item12`, `user2_amount12`) VALUES ");
		tradeLogResetLength = tradeLog.length();
		
		exploitLog = new StringBuffer("INSERT INTO `game_exploit` (`user`, `account`, `ip`, `time`, `exploit`) VALUES ");
		exploitLogResetLength = exploitLog.length();
		
		duelLog = new StringBuffer("INSERT INTO `game_duel` (`user1`, `account1`, `user1_ip`, `user2`, `account2`, `user2_ip`, `time`, `no_retreating`, `no_magic`, `no_prayer`, `no_weapons`, `user1_item1` , `user1_amount1` , `user1_item2` , `user1_amount2` , `user1_item3` , `user1_amount3` , `user1_item4` , `user1_amount4` , `user1_item5` , `user1_amount5` , `user1_item6` , `user1_amount6` , `user1_item7` , `user1_amount7` , `user1_item8` , `user1_amount8` , `user2_item1` , `user2_amount1` , `user2_item2` , `user2_amount2` , `user2_item3` , `user2_amount3` , `user2_item4` , `user2_amount4` , `user2_item5` , `user2_amount5` , `user2_item6` , `user2_amount6` , `user2_item7` , `user2_amount7` , `user2_item8` , `user2_amount8`) VALUES ");
		duelLogResetLength = duelLog.length();
		
		deathLog = new StringBuffer("INSERT INTO `game_death` (`user`, `account`, `ip` , `time` , `x` , `y` , `item1_id` , `item1_amount` , `item2_id` , `item2_amount` , `item3_id` , `item3_amount` , `item4_id` , `item4_amount` , `item5_id` , `item5_amount` , `item6_id` , `item6_amount` , `item7_id` , `item7_amount` , `item8_id` , `item8_amount` , `item9_id` , `item9_amount` , `item10_id` , `item10_amount` , `item11_id` , `item11_amount` , `item12_id` , `item12_amount` , `item13_id` , `item13_amount` , `item14_id` , `item14_amount` , `item15_id` , `item15_amount` , `item16_id` , `item16_amount` , `item17_id` , `item17_amount` , `item18_id` , `item18_amount` , `item19_id` , `item19_amount` , `item20_id` , `item20_amount` , `item21_id` , `item21_amount` , `item22_id` , `item22_amount` , `item23_id` , `item23_amount` , `item24_id` , `item24_amount` , `item25_id` , `item25_amount` , `item26_id` , `item26_amount` , `item27_id` , `item27_amount` , `item28_id` , `item28_amount` , `item29_id` , `item29_amount` , `item30_id` , `item30_amount`) VALUES ");
		deathLogResetLength = deathLog.length();
		
		chatLog = new StringBuffer("INSERT INTO `game_chat` (`user`, `account`, `ip`, `time`, `message`) VALUES ");
		chatLogResetLength = chatLog.length();
		
		errorLog = new StringBuffer("INSERT INTO `game_error` (`user`, `account`, `ip`, `time`, `error`) VALUES ");
		errorLogResetLength = errorLog.length();
		
		genericLog = new StringBuffer("INSERT INTO `game_generic` (`message`, `time`) VALUES ");
		genericLogResetLength = genericLog.length();
		
		reportLog = new StringBuffer("INSERT INTO `game_report` (`user`, `account`, `ip`, `reported`, `reported_account`, `reported_ip`, `time`, `rule`) VALUES ");
		reportLogResetLength = reportLog.length();
		
		shopLog = new StringBuffer("INSERT INTO `game_shop` (`user`, `ip`, `time`, `account`, `item_id`, `item_amount`, `action`) VALUES ");
		shopLogResetLength = shopLog.length();
		
		scriptLog = new StringBuffer("INSERT INTO `game_script` (`user`, `account`, `ip`, `time`, `script`, `target`, `status`) VALUES ");
		scriptLogResetLength = scriptLog.length();

		autoBanLog = new StringBuffer("INSERT INTO `game_bans` (`player`, `account`, `ip`, `time`, `reason`) VALUES ");
		autoBanLogResetLength = autoBanLog.length();
		
		eventLog = new StringBuffer("INSERT INTO `game_event` (`user`, `account`, `ip`, `time`, `message`) VALUES ");
		eventLogResetLength = eventLog.length();
	}

	public void run() {
		while (true) {
			try {
				Thread.sleep(12000);
			} catch(InterruptedException interruptedException) {
				threadInterrupted(this.getClass().getName(), interruptedException);
			}
			
			if (Config.LOGGING)
			{
				try(Statement statement = connection.createStatement())
				{
						if (globalMessageLog.length() > globalMessageLogResetLength)
							statement.executeUpdate(globalMessageLog.substring(0, globalMessageLog.length() - 2));
						if (privateMessageLog.length() > privateMessageLogResetLength)
							statement.executeUpdate(privateMessageLog.substring(0, privateMessageLog.length() - 2));
						if (dropLog.length() > dropLogResetLength)
							statement.executeUpdate(dropLog.substring(0, dropLog.length() - 2));
						if (pickupLog.length() > pickUpLogResetLength)
							statement.executeUpdate(pickupLog.substring(0, pickupLog.length() - 2));
						if (loginLog.length() > loginLogResetLength) {
							statement.executeUpdate(loginLog.substring(0, loginLog.length() - 2));
						}
						if (tradeLog.length() > tradeLogResetLength)
							statement.executeUpdate(tradeLog.substring(0, tradeLog.length() - 2));
						if (exploitLog.length() > exploitLogResetLength)
							statement.executeUpdate(exploitLog.substring(0, exploitLog.length() - 2));
						if (duelLog.length() > duelLogResetLength)
							statement.executeUpdate(duelLog.substring(0, duelLog.length() - 2));
						if (deathLog.length() > deathLogResetLength)
							statement.executeUpdate(deathLog.substring(0, deathLog.length() - 2));
						if (chatLog.length() > chatLogResetLength)
							statement.executeUpdate(chatLog.substring(0, chatLog.length() - 2));
						if (errorLog.length() > errorLogResetLength)
							statement.executeUpdate(errorLog.substring(0, errorLog.length() - 2));
						if (genericLog.length() > genericLogResetLength)
							statement.executeUpdate(genericLog.substring(0, genericLog.length() - 2));
						if (reportLog.length() > reportLogResetLength)
							statement.executeUpdate(reportLog.substring(0, reportLog.length() - 2));
						if (shopLog.length() > shopLogResetLength)
							statement.executeUpdate(shopLog.substring(0, shopLog.length() - 2));
						if (scriptLog.length() > scriptLogResetLength)
							statement.executeUpdate(scriptLog.substring(0, scriptLog.length() - 2));
						if (autoBanLog.length() > autoBanLogResetLength)
							statement.executeUpdate(autoBanLog.substring(0, autoBanLog.length() - 2));
						if (eventLog.length() > eventLogResetLength)
							statement.executeUpdate(eventLog.substring(0, eventLog.length() - 2));
				}
				catch(SQLException e)
				{
					e.printStackTrace();
				}
			
			}
			try {
				resetQueries();
			} catch(OutOfMemoryError outOfMemoryError) {
				outOfMemoryError("reset queries");
			}
		}
	}

	public static synchronized void log(Log log) {
			if (log instanceof GlobalLog) {
				try {
					appendGlobalMessage((GlobalLog)log);
				} catch(OutOfMemoryError outOfMemoryError) {
					outOfMemoryError("append global message");
				}
			} else if (log instanceof PrivateMessageLog) {
				try {
					appendPrivateMessage((PrivateMessageLog) log);
				} catch(OutOfMemoryError outOfMemoryError) {
					outOfMemoryError("append private message");
				}
			} else if (log instanceof DropLog) {
				try {
					appendDrop((DropLog) log);
				} catch (OutOfMemoryError outOfMemoryError) {
					outOfMemoryError("append drop");
				}
			} else if (log instanceof PickUpLog) {
				try {
					appendPickUp((PickUpLog) log);
				} catch (OutOfMemoryError outOfMemoryError) {
					outOfMemoryError("append pick up");
				}
			} else if (log instanceof TradeLog) {
				try {
					appendTradeLog((TradeLog) log);
				} catch (OutOfMemoryError outOfMemoryError) {
					outOfMemoryError("trade");
				}
			} else if (log instanceof PlayerLoginLog) {
				try {
					appendPlayerLogin((PlayerLoginLog) log);
				} catch (OutOfMemoryError outOfMemoryError) {
					outOfMemoryError("player login");
				}
			} else if (log instanceof ExploitLog) {
				try {
					appendExploit((ExploitLog) log);
				} catch (OutOfMemoryError outOfMemoryError) {
					outOfMemoryError("exploit");
				}
			} else if (log instanceof DuelLog) {
				try {
					appendDuel((DuelLog) log);
				} catch (OutOfMemoryError outOfMemoryError){
					outOfMemoryError("duel");
				}
			} else if (log instanceof DeathLog) {
				try {
					appendDeath((DeathLog) log);
				} catch (OutOfMemoryError outOfMemoryError) {
					outOfMemoryError("death");
				}
			} else if (log instanceof ChatLog) {
				try {
					appendChat((ChatLog) log);
				} catch (OutOfMemoryError outOfMemoryError) {
					outOfMemoryError("chat");
				}
			} else if (log instanceof ErrorLog) {
				try {
					appendError((ErrorLog) log);
				} catch (OutOfMemoryError outOfMemoryError) {
					outOfMemoryError("error");
				}
			} else if (log instanceof GenericLog) {
				try {
					appendGeneric((GenericLog) log);
				} catch (OutOfMemoryError outOfMemoryError) {
					outOfMemoryError("generic");
				}
			} else if (log instanceof ReportLog) {
				try {
					appendReport((ReportLog) log);
				} catch (OutOfMemoryError outOfMemoryError) {
					outOfMemoryError("report");
				}
			} else if (log instanceof ShopLog) {
				try {
					appendShop((ShopLog) log);
				} catch (OutOfMemoryError outOfMemoryError) {
					outOfMemoryError("shop");
				}
			} else if (log instanceof ScriptLog) {
				try {
					appendScript((ScriptLog) log);
				} catch (OutOfMemoryError outOfMemoryError) {
					outOfMemoryError("script");
				}
			} else if (log instanceof AutoBanLog) {
				try {
					appendAutoBan((AutoBanLog) log);
				} catch(OutOfMemoryError outOfMemoryError) {
					outOfMemoryError("auto-ban");
				}
			} else if (log instanceof eventLog) {
				try {
					appendEventLog((eventLog) log);
				} catch(OutOfMemoryError outOfMemoryError) {
					outOfMemoryError("event-log");
				}
			}	
	}

	private static final void appendGeneric(GenericLog log) throws OutOfMemoryError {
		genericLog.append("('")
		.append(log.getMessage())
		.append("', " + log.getTime() + "), ");
	}
	
	private static final void appendReport(ReportLog log) throws OutOfMemoryError {
		reportLog.append("('")
		.append(log.getUser())
		.append("', '")
		.append(log.getAccount())
		.append("', '")		
		.append(log.getIP())
		.append("', '")		
		.append(log.getReported())
		.append("', '")
		.append(log.getReportedAccount())
		.append("', '")		
		.append(log.getReportedIP())		
		.append("', " + log.getTime() + ", ")
		.append(log.getRule())
		.append("), ");
	}
	
	private static final void appendShop(ShopLog log) throws OutOfMemoryError {
		shopLog.append("('")
		.append(log.getUser())
		.append("', '")
		.append(log.getIP())
		.append("', '")		
		.append(log.getTime())
		.append("', '")
		.append(log.getAccount())		
		.append("', '")
		.append(log.getItemID())		
		.append("', '")
		.append(log.getItemAmount())		
		.append("', '")
		.append(log.getAction())
		.append("'), ");
	}	

	private static final void appendError(ErrorLog log) throws OutOfMemoryError {
		errorLog.append("('")
		.append(log.getHash())
		.append("', '")
		.append(log.getAccount())
		.append("', '")		
		.append(log.getIP())
		.append("', " + log.getTime() + ", '")
		.append(log.getMessage())
		.append("'), ");
	}

	private static void appendChat(ChatLog log) throws OutOfMemoryError{
		chatLog.append("('")
		.append(log.getHash())
		.append("', '")
		.append(log.getAccount())
		.append("', '")		
		.append(log.getIP())
		.append("', " + log.getTime() + ", '")
		.append(log.getMessage())
		.append("'), ");
	}
	
	private static void appendDeath(DeathLog log) throws OutOfMemoryError {
		deathLog.append("('")
		.append(log.getHash())
		.append("', '")
		.append(log.getAccount())
		.append("', '")		
		.append(log.getIP())
		.append("', " + log.getTime() + ", '")
		.append(log.getX())
		.append("', '")
		.append(log.getY());
		int counter = 0;		
		for (org.openrsc.server.model.InvItem item : log.getItemsLost()) {
			deathLog.append("', '")
			.append(item.getID())
			.append("', '")
			.append(item.getAmount());
			counter++;
		}		
		for (; counter < 30; counter++) {
			deathLog.append("', '")
			.append("NULL")
			.append("', '")
			.append("NULL");
		}
		deathLog.append("'), ");
		int nullIndex = 0;
		while ((nullIndex = deathLog.indexOf("'NULL'")) != -1)
			deathLog = deathLog.replace(nullIndex, nullIndex + 6, "NULL");
	}

	private static void appendDuel(DuelLog log) throws OutOfMemoryError {
		duelLog.append("('")
		.append(log.getHash())
		.append("', '")
		.append(log.getAccount())
		.append("', '")		
		.append(log.getIP())
		.append("', '")
		.append(log.getOpponent())
		.append("', '")
		.append(log.getOpponentAccount())
		.append("', '")		
		.append(log.getOpponentIP())
		.append("', " + log.getTime() + ", '")
		.append(log.getDuelOption(0))
		.append("', '")
		.append(log.getDuelOption(1))
		.append("', '")
		.append(log.getDuelOption(2))
		.append("', '")
		.append(log.getDuelOption(3));
		int counter = 0;
		for (org.openrsc.server.model.InvItem item : log.getStakedItems()) {
			duelLog.append("', '")
			.append(item.getID())
			.append("', '")
			.append(item.getAmount());
			counter++;
		}	
		for(;counter < 8; counter++) {
			duelLog.append("', '")
			.append("NULL")
			.append("', '")
			.append("NULL");
		}
		counter = 0;
		for (org.openrsc.server.model.InvItem item : log.getStakedAgainstItems()) {
			duelLog.append("', '")
			.append(item.getID())
			.append("', '")
			.append(item.getAmount());
			counter++;
		}
		for (;counter < 8; counter++) {
			duelLog.append("', '")
			.append("NULL")
			.append("', '")
			.append("NULL");
		}
		duelLog.append("'), ");
		int nullIndex = 0;
		
		while ((nullIndex = duelLog.indexOf("'NULL'")) != -1)
			duelLog = duelLog.replace(nullIndex, nullIndex + 6, "NULL");
	}

	private static void appendExploit(ExploitLog log) throws OutOfMemoryError {
		exploitLog.append("('")
		.append(log.getHash())
		.append("', '")
		.append(log.getAccount())
		.append("', '")		
		.append(log.getIP())
		.append("', " + log.getTime() + ", '")
		.append(log.getExploit())
		.append("'), ");
	}

	private static void appendTradeLog(TradeLog log) throws OutOfMemoryError {
		tradeLog.append("('")
		.append(log.getHash())
		.append("', '")
		.append(log.getAccount())
		.append("', '")		
		.append(log.getReciever())
		.append("', '")
		.append(log.getRecieverAccount())
		.append("', '")		
		.append(log.getIP())
		.append("', " + log.getTime() + ", '")
		.append(log.getRecieverIP());
		int counter = 0;
		for(org.openrsc.server.model.InvItem item : log.getTradedItems()) {
			tradeLog.append("', '")
			.append(item.getID())
			.append("', '")
			.append(item.getAmount());
			counter++;
		}
		for(;counter < 12; counter++) {
			tradeLog.append("', '")
			.append("NULL")
			.append("', '")
			.append("NULL");
		}
		counter = 0;
		for (org.openrsc.server.model.InvItem item : log.getRecievedItems()) {
			tradeLog.append("', '")
			.append(item.getID())
			.append("', '")
			.append(item.getAmount());
			counter++;
		}
		for (;counter < 12; counter++) {
			tradeLog.append("', '")
			.append("NULL")
			.append("', '")
			.append("NULL");
		}
		tradeLog.append("'), ");
		int nullIndex = 0;
		while ((nullIndex = tradeLog.indexOf("'NULL'")) != -1)
			tradeLog = tradeLog.replace(nullIndex, nullIndex + 6, "NULL");
	}

	private static void appendPickUp(PickUpLog log) throws OutOfMemoryError {
		pickupLog.append("('")
		.append(log.getHash())
		.append("', '")
		.append(log.getAccount())
		.append("', '")		
		.append(log.getIP())
		.append("', '")
		.append(log.getX())
		.append("', '")
		.append(log.getY())
		.append("', '")
		.append(log.getItemID())
		.append("', '")
		.append(log.getItemAmount())
		.append("', " + log.getTime() + "), ");
	}

	private static void appendDrop(DropLog log) throws OutOfMemoryError {
		dropLog.append("('")
		.append(log.getHash())
		.append("', '")
		.append(log.getAccount())
		.append("', '")		
		.append(log.getIP())
		.append("', '")
		.append(log.getX())
		.append("', '")
		.append(log.getY())
		.append("', '")
		.append(log.getItemID())
		.append("', '")
		.append(log.getItemAmount())
		.append("', " + log.getTime() + "), ");
	}

	private static void appendPrivateMessage(PrivateMessageLog log) throws OutOfMemoryError  {
		privateMessageLog.append("('")
		.append(log.getHash())
		.append("', '")
		.append(log.getAccount())
		.append("', '")		
		.append(log.getIP())
		.append("', '")
		.append(log.getReciever())
		.append("', '")
		.append(log.getRecieverAccount())
		.append("', '")		
		.append(log.getRecieverIP())
		.append("', " + log.getTime() + ", '")
		.append(log.getMessage())
		.append("'), ");
	}

	private static void appendGlobalMessage(GlobalLog log) throws OutOfMemoryError {
		globalMessageLog.append("('")
		.append(log.getHash())
		.append("', '")
		.append(log.getAccount())
		.append("', '")		
		.append(log.getIP())
		.append("', " + log.getTime() + ", '")
		.append(log.getMessage())
		.append("'), ");
	}

	private static void appendPlayerLogin(PlayerLoginLog log) throws OutOfMemoryError {
		loginLog.append("('")
		.append(log.getHash())
		.append("', '" + log.getAccount() + "', ")		
		.append(log.getTime() + ", '")
		.append(log.getIP())
		.append("'), ");
	}
	
	private static void appendScript(ScriptLog log) throws OutOfMemoryError {
		scriptLog.append("(")
		.append(log.getHash())
		.append(", ").append(log.getAccount()).append(", '")
		.append(log.getIP()).append("', ")
		.append(log.getTime()).append(", '")
		.append(log.getScript()).append("', ")
		.append(log.getTarget()).append(", ")
		.append(log.getStatus())
		.append("), ");
	}
	
	private static void appendAutoBan(AutoBanLog log) throws OutOfMemoryError {
		autoBanLog.append("(")
		.append(log.getHash())
		.append(", ").append(log.getAccount()).append(", '")
		.append(log.getIP()).append("', UNIX_TIMESTAMP(), '")
		.append(log.getReason()).append("'), ");
	}
	
	private static void appendEventLog(eventLog log) throws OutOfMemoryError {
		eventLog.append("('")
		.append(log.getHash())
		.append("', '")
		.append(log.getAccount())
		.append("', '")		
		.append(log.getIP())
		.append("', " + log.getTime() + ", '")
		.append(log.getMessage())
		.append("'), ");
	}

	private static void resetQueries() throws OutOfMemoryError {
		globalMessageLog.delete(globalMessageLogResetLength, globalMessageLog.length());
		privateMessageLog.delete(privateMessageLogResetLength, privateMessageLog.length());
		dropLog.delete(dropLogResetLength, dropLog.length());
		pickupLog.delete(pickUpLogResetLength, pickupLog.length());
		loginLog.delete(loginLogResetLength, loginLog.length());
		tradeLog.delete(tradeLogResetLength, tradeLog.length());
		exploitLog.delete(exploitLogResetLength, exploitLog.length());
		duelLog.delete(duelLogResetLength, duelLog.length());
		deathLog.delete(deathLogResetLength, deathLog.length());
		chatLog.delete(chatLogResetLength, chatLog.length());
		errorLog.delete(errorLogResetLength, errorLog.length());
		genericLog.delete(genericLogResetLength, genericLog.length());
		reportLog.delete(reportLogResetLength, reportLog.length());
		shopLog.delete(shopLogResetLength, shopLog.length());
		scriptLog.delete(scriptLogResetLength, scriptLog.length());
		autoBanLog.delete(autoBanLogResetLength, autoBanLog.length());
		eventLog.delete(eventLogResetLength, eventLog.length());
	}

	private static void threadInterrupted(String className, InterruptedException interruptedException) {
		interruptedException.printStackTrace();
	}

	private static void outOfMemoryError(String logType) {
		System.out.println("This system has run out of usable memory for Logger to operate with: " + logType);
		System.exit(-1);
	}
}