/*     */ package org.openrsc.server.packethandler.web;
/*     */ 
/*     */ import org.apache.mina.common.IoSession;
import org.openrsc.server.Config;
import org.openrsc.server.ServerBootstrap;
import org.openrsc.server.entityhandling.EntityHandler;
import org.openrsc.server.model.GameObject;
import org.openrsc.server.model.Group;
import org.openrsc.server.model.InvItem;
import org.openrsc.server.model.Npc;
import org.openrsc.server.model.Player;
import org.openrsc.server.model.World;
import org.openrsc.server.net.WebPacket;
import org.openrsc.server.util.ChatFilter;
import org.openrsc.server.util.DataConversions;
import org.openrsc.server.util.Formulae;
/*     */ 
/*     */ public class StaffActionHandler
/*     */   implements WebPacketHandler
/*     */ {
/*     */   public void handlePacket(IoSession session, WebPacket packet)
/*     */   {
/*  21 */     int packetID = packet.readByte() & 0xFF;
/*     */     Player p;
/*  24 */     switch (packetID)
/*     */     {
/*     */     case 0:
/*  26 */       p = World.getPlayerByOwner(packet.readInt());
/*  27 */       if (p == null) {
/*  28 */         sendReply(session, 1); return;
/*     */       }
				ServerBootstrap.getDatabaseService().submit(new Player.BanTransaction(p.getUsernameHash(), true));
///*  30 */       Server.DB().addQuery(new MiscQueryRequest(p.getUsernameHash(), 0));
/*  31 */       World.unregisterEntity(p);
/*  32 */       sendReply(session, 0);
/*     */ 
/*  34 */       break;
/*     */     case 1:
/*  37 */       p = World.getPlayerByOwner(packet.readInt());
/*  38 */       if (p == null) {
/*  39 */         sendReply(session, 1); return;
/*     */       }
/*  41 */       World.unregisterEntity(p);
/*  42 */       sendReply(session, 0);
/*     */ 
/*  44 */       break;
/*     */     case 2:
/*  47 */       p = World.getPlayerByOwner(packet.readInt());
/*  48 */       if (p != null)
/*  49 */         p.sendAlert("Your password was just changed for character @gre@" + packet.readPaddedString(12) + "@whi@. If you didn't do this please change all your passwords immediately.");
/*  50 */       sendReply(session, 0);
/*  51 */       break;
/*     */     case 3:
/*  54 */       p = World.getPlayerByOwner(packet.readInt());
/*  55 */       if (p != null)
/*  56 */         p.sendAlert("Your password was just changed for your main website account. If you didn't do this please change all your passwords immediately.");
/*  57 */       sendReply(session, 0);
/*  58 */       break;
/*     */     case 4:
/*  61 */       p = World.getPlayerByOwner(packet.readInt());
/*  62 */       if (p != null)
/*  63 */         p.sendAlert("Your recovery questions were just changed. If you didn't do this please cancel the pending recovery questions and change all your passwords immediately.");
/*  64 */       sendReply(session, 0);
/*  65 */       break;
/*     */     case 5:
/*  68 */       p = World.getPlayerByOwner(packet.readInt());
/*  69 */       if (p != null) {
/*  70 */         int minutes = packet.readInt();
/*  71 */         p.sendAlert("Thanks for voting for openrsc!% %We have credited @gre@" + minutes + " subscription minutes@whi@ to your account.");
/*  72 */         synchronized (World.getPlayers()) {
/*  73 */           for (Player p1 : World.getPlayers())
/*  74 */             p1.sendNotification(Config.getPrefix() + p.getStaffName() + "@whi@ just got @gre@" + minutes + " minutes free subscription@whi@ by using @gre@::VOTE");
/*     */         }
/*  76 */         if ((p.getSubscriptionExpires() == 0L) || (p.getSubscriptionExpires() < DataConversions.getTimeStamp()))
/*  77 */           p.setSubscriptionExpires(DataConversions.getTimeStamp() + 60 * minutes);
/*     */         else
/*  79 */           p.setSubscriptionExpires(p.getSubscriptionExpires() + 60 * minutes);
/*  81 */         p.setGroupID(Group.SUBSCRIBER);
/*     */       }
/*  83 */       sendReply(session, 0);
/*  84 */       break;
/*     */     case 6:
/*  87 */       synchronized (World.getPlayers()) {
/*  88 */         for (Player a : World.getPlayers())
/*  89 */           a.sendAlert(packet.readPaddedString(100));
/*     */       }
/*  91 */       sendReply(session, 0);
/*  92 */       break;

/*     */     case 9:
/* 112 */       p = World.getPlayerByOwner(packet.readInt());
/* 113 */       if (p != null)
/* 114 */         p.sendAlert("Your e-mail address has just been changed to:% %@gre@" + packet.readPaddedString(70) + "@whi@% %If you didn't do this please change your e-mail address and all of your passwords immediately.", true);
/* 115 */       sendReply(session, 0);
/* 116 */       break;
/*     */     case 10:
/* 119 */       String poster = packet.readPaddedString(20);
/* 120 */       String subject = packet.readPaddedString(100);
/*     */ 
/* 122 */       synchronized (World.getPlayers()) {
/* 123 */         for (Player a : World.getPlayers())
/* 124 */           a.sendGraciousAlert("#adm#@yel@" + poster + " @whi@has just posted a new website announcement:% %@gre@" + subject);
/*     */       }
/* 126 */       sendReply(session, 0);
/* 127 */       break;
/*     */     case 11:
/* 130 */       p = World.getPlayerByOwner(packet.readInt());
/* 131 */       if (p != null) {
/* 132 */         p.sendGraciousAlert("@gre@" + packet.readPaddedString(20) + " @whi@has just sent you a message through the website message-centre:% %@gre@" + packet.readPaddedString(70));
/*     */       }
/* 134 */       sendReply(session, 0);
/* 135 */       break;
/*     */     case 12:
/* 138 */       p = World.getPlayerByOwner(packet.readInt());
/* 139 */       if (p == null) {
/* 140 */         sendReply(session, 1); return;
/*     */       }
/* 142 */       int duration = packet.readInt();
/*     */ 
/* 144 */       if (duration == -1)
/* 145 */         p.unmute();
/* 146 */       else if (duration == 0)
/* 147 */         p.mute(0L);
/*     */       else {
/* 149 */         p.mute(duration);
/*     */       }
/* 151 */       sendReply(session, 0);
/*     */ 
/* 153 */       break;
/*     */     case 13:
/* 156 */       p = World.getPlayerByOwner(packet.readInt());
/* 157 */       if (p != null)
/* 158 */         p.sendAlert("Your pending recovery questions were just cancelled. If you didn't do this please set new recovery questions and change all your passwords immediately.");
/* 159 */       sendReply(session, 0);
/* 160 */       break;
/*     */     case 14:
/* 163 */       p = World.getPlayerByOwner(packet.readInt());
/* 164 */       if (p == null) {
/* 165 */         sendReply(session, 1); return;
/*     */       }
/* 167 */       int x = packet.readInt();
/* 168 */       int y = packet.readInt();
/* 169 */       if (World.withinWorld(x, y)) {
/* 170 */         p.teleport(x, y);
/* 171 */         sendReply(session, 0);
/*     */       }
/* 173 */       sendReply(session, 2);
/*     */ 
/* 175 */       break;
/*     */     case 15:
/* 178 */       p = World.getPlayerByOwner(packet.readInt());
/* 179 */       if (p == null) {
/* 180 */         sendReply(session, 1); return;
/*     */       }
/* 182 */       for (InvItem itemToCheck : p.getInventory().getItems()) {
/* 183 */         if (p.getInventory().get(itemToCheck).isWielded()) {
/* 184 */           p.getInventory().get(itemToCheck).setWield(false);
/* 185 */           p.updateWornItems(itemToCheck.getWieldableDef().getWieldPos(), p.getPlayerAppearance().getSprite(itemToCheck.getWieldableDef().getWieldPos()));
/*     */         }
/*     */       }
/* 188 */       p.getInventory().getItems().clear();
/* 189 */       p.sendInventory();
/* 190 */       sendReply(session, 0);
/*     */ 
/* 192 */       break;
/*     */     case 16:
/* 195 */       p = World.getPlayerByOwner(packet.readInt());
/* 196 */       if (p == null) {
/* 197 */         sendReply(session, 1); return;
/*     */       }
/* 199 */       p.getBank().getItems().clear();
/* 200 */       sendReply(session, 0);
/*     */ 
/* 202 */       break;
/*     */     case 17:
/* 205 */       p = World.getPlayerByOwner(packet.readInt());
/* 206 */       if (p == null) {
/* 207 */         sendReply(session, 1); return;
/*     */       }
/* 209 */       int groupID = packet.readInt();
/* 210 */       p.setGroupID(groupID);
/* 212 */       sendReply(session, 0);
/*     */ 
/* 214 */       break;
/*     */     case 18:
/* 217 */       World.registerEntity(new GameObject(packet.readInt(), packet.readInt(), packet.readInt(), packet.readInt(), 0));
/* 218 */       sendReply(session, 0);
/* 219 */       break;
/*     */     case 19:
/* 222 */       Npc n = new Npc(packet.readInt(), packet.readInt(), packet.readInt(), packet.readInt(), packet.readInt(), packet.readInt(), packet.readInt());
/* 223 */       if (packet.readInt() == 0)
/* 224 */         n.setRespawn(false);
/* 225 */       World.registerEntity(n);
/*     */ 
/* 227 */       sendReply(session, 0);
/* 228 */       break;
/*     */     case 20:
/* 231 */       p = World.getPlayerByOwner(packet.readInt());
/* 232 */       int itemID = packet.readInt();
/* 233 */       int itemAmount = packet.readInt();
/* 234 */       if (p == null) {
/* 235 */         sendReply(session, 1); return;
/*     */       }
/* 237 */       if (EntityHandler.getItemDef(itemID) != null) {
/* 238 */         if ((p.getBank().countId(itemID) < itemAmount) && (p.getInventory().countId(itemID) < itemAmount)) {
/* 239 */           sendReply(session, 3); return;
/*     */         }
/* 241 */         if (p.getInventory().countId(itemID) >= itemAmount) {
/* 242 */           p.getInventory().remove(new InvItem(itemID, itemAmount));
/* 243 */           p.sendInventory();
/*     */         } else {
/* 245 */           p.getBank().remove(itemID, itemAmount);
/* 246 */         }sendReply(session, 0); return;
/*     */       }
/*     */ 
/* 249 */       sendReply(session, 2);
/*     */ 
/* 251 */       break;
/*     */     case 21:
/* 254 */       p = World.getPlayerByOwner(packet.readInt());
/* 255 */       int itemIDs = packet.readInt();
/* 256 */       int itemAmounts = packet.readInt();
/*     */ 
/* 258 */       if (EntityHandler.getItemDef(itemIDs) != null) {
/* 259 */         InvItem item = new InvItem(itemIDs, itemAmounts);
/* 260 */         if (p.getBank().canHold(item)) {
/* 261 */           p.getBank().add(item);
/* 262 */           sendReply(session, 0);
/* 263 */         } else if (p.getInventory().canHold(item)) {
/* 264 */           p.getInventory().add(item);
/*     */         } else {
/* 266 */           sendReply(session, 1);
/* 267 */         }return;
/* 268 */       }sendReply(session, 2);
/* 269 */       break;
/*     */     case 22:
/* 272 */       p = World.getPlayerByOwner(packet.readInt());
/* 273 */       int statID = packet.readInt();
/* 274 */       int statLevel = packet.readInt();
/* 275 */       if ((statID > 0) || (statID < 19)) {
/* 276 */         if ((statLevel < 100) && (statLevel >= 1)) {
/* 277 */           if (p != null) {
/* 278 */             if ((!p.getLocation().inWilderness()) || (!p.isDueling()) || (!p.isBusy())) {
/* 279 */               if (statLevel == 1) {
/* 280 */                 p.setExp(statID, 0);
/* 281 */                 p.setCurStat(statID, 1);
/* 282 */                 p.setMaxStat(statID, 1);
/*     */               } else {
/* 284 */                 p.setExp(statID, Formulae.experienceArray[(statLevel - 2)]);
/* 285 */                 p.setCurStat(statID, statLevel);
/* 286 */                 p.setMaxStat(statID, Formulae.experienceToLevel((int) p.getExp(statID)));
/*     */               }
/* 288 */               p.setCombatLevel(Formulae.getCombatlevel(p.getMaxStats()));
/* 289 */               p.sendStats();
/* 290 */               sendReply(session, 0); return;
/*     */             }
/* 292 */             sendReply(session, 4); return;
/*     */           }
/* 294 */           sendReply(session, 1); return;
/*     */         }
/* 296 */         sendReply(session, 2); return;
/*     */       }
/* 298 */       sendReply(session, 3);
/* 299 */       break;
/*     */     case 23:
/* 302 */       p = World.getPlayerByOwner(packet.readInt());
/* 303 */       if (p != null)
/* 304 */         p.sendAlert("Your character @gre@" + packet.readPaddedString(12) + "@whi@ has just started the deletion process. If you didn't do this please cancel the pending deletion and change all your passwords immediately.");
/* 305 */       sendReply(session, 0);
/* 306 */       break;
/*     */     case 24:
/* 309 */       ChatFilter.add(packet.readPaddedString(20));
/* 310 */       sendReply(session, 0);
/* 311 */       break;
/*     */     case 25:
/* 314 */       ChatFilter.remove(packet.readPaddedString(20));
/* 315 */       sendReply(session, 0);
/* 316 */       break;
/*     */     case 26:
/* 319 */       sendReply(session, World.getPlayers().count());
/*     */     }
/*     */   }
/*     */ 
/*     */   public void sendReply(IoSession session, int reply)
/*     */   {
/* 325 */     session.write(Integer.valueOf(reply));
/*     */   }
/*     */ }