package org.openrsc.server.packethandler;

import java.util.ArrayList;

import org.apache.mina.common.IoSession;
import org.openrsc.server.Config;
import org.openrsc.server.entityhandling.EntityHandler;
import org.openrsc.server.entityhandling.defs.GameObjectDef;
import org.openrsc.server.entityhandling.defs.extras.ChestDef;
import org.openrsc.server.entityhandling.defs.extras.ObjectFishDef;
import org.openrsc.server.entityhandling.defs.extras.ObjectFishingDef;
import org.openrsc.server.entityhandling.defs.extras.ObjectMiningDef;
import org.openrsc.server.entityhandling.defs.extras.StallThievingDefinition;
import org.openrsc.server.entityhandling.defs.extras.WoodcutDef;
import org.openrsc.server.event.DelayedEvent;
import org.openrsc.server.event.DelayedGenericMessage;
import org.openrsc.server.event.DelayedQuestChat;
import org.openrsc.server.event.MiniEvent;
import org.openrsc.server.event.ShortEvent;
import org.openrsc.server.event.SingleEvent;
import org.openrsc.server.event.WalkToObjectEvent;
import org.openrsc.server.logging.Logger;
import org.openrsc.server.logging.model.ErrorLog;
import org.openrsc.server.logging.model.eventLog;
import org.openrsc.server.model.*;
import org.openrsc.server.net.Packet;
import org.openrsc.server.net.RSCPacket;
import org.openrsc.server.states.Action;
import org.openrsc.server.util.DataConversions;
import org.openrsc.server.util.Formulae;

import com.rscdaemon.scripting.ScriptCache;
import com.rscdaemon.scripting.ScriptError;
import com.rscdaemon.scripting.ScriptVariable;
import com.rscdaemon.scripting.listener.UseObjectListener;

public class ObjectAction implements PacketHandler {
        
        private final ScriptCache<UseObjectListener> scriptCache = new ScriptCache<>();
        
        public void handlePacket(Packet p, IoSession session) {
                Player player = (Player)session.getAttachment();
                final int click = ((RSCPacket)p).getID();
                short x = p.readShort();
                short y = p.readShort();
                final GameObject object = World.getZone(x, y).getObjectAt(x, y);        
                if (object != null) {
                        if(!player.isBusy()) {
                                player.resetAllExceptDMing();
                                player.setStatus(Action.USING_OBJECT);
                                World.getDelayedEventHandler().add(new WalkToObjectEvent(player, object, false) {
                                        public void arrived() {
                                                
                                                owner.resetPath();
                                                GameObjectDef def = object.getGameObjectDef();
                                                if (owner.isBusy() || owner.isRanging() || !owner.nextTo(object) || def == null || owner.getStatus() != Action.USING_OBJECT)
                                                        return;
                                                owner.resetAllExceptDMing();
                                                String command = (click == 29 ? def.getCommand1() : def.getCommand2()).toLowerCase();
                                                
                                                // No events should fire if there is an active script
                                                if(owner.getScript() != null)
                                                {
                                                        return;
                                                }
                                                // Try to retrieve a script from the cache
                                                UseObjectListener script = scriptCache.get(object.getID());
                                                try
                                                {
                                                        // If the script was found in the cache, try to run it
                                                        if(script != null)
                                                        {
                                                                script = script.getClass().newInstance();
                                                                script.Bind(ScriptVariable.OWNER, owner);
                                                                script.Bind(ScriptVariable.OBJECT_TARGET, object);
                                                                owner.setScript(script);
                                                                if(script.onObjectUsed(owner, object, click == 29 ? 0 : 1))
                                                                {
                                                                        script.run();
                                                                        return;
                                                                }
                                                        }
                                                        
                                                        // If the script wasn't ran, search for one.
                                                        for(UseObjectListener listener : World.getScriptManager().<UseObjectListener>getListeners(UseObjectListener.class))
                                                        {
                                                                script = listener.getClass().newInstance();
                                                                script.Bind(ScriptVariable.OWNER, owner);
                                                                script.Bind(ScriptVariable.OBJECT_TARGET, object);
                                                                owner.setScript(script);
                                                                if(script.onObjectUsed(owner, object, click == 29 ? 0 : 1))
                                                                {
                                                                        script.run();
                                                                        scriptCache.put(object.getID(), listener);
                                                                        return;
                                                                }
                                                        }
                                                        
                                                        if(script != null)
                                                        {
                                                                // If no script was found, manually clean up
                                                                script.__internal_unbind_all();
                                                                owner.setScript(null);
                                                        }
                                                }
                                                catch(IllegalAccessException | InstantiationException e)
                                                {
                                                        if(script != null)
                                                        {
                                                                script.__internal_unbind_all();
                                                                owner.setScript(null);
                                                        }
                                                        throw (ScriptError)new ScriptError(script, e.getMessage()).initCause(e);
                                                }
                                                
                                                Point telePoint = EntityHandler.getObjectTelePoint(object.getLocation(), command);
                                                if (telePoint != null)
                                                        owner.teleport(telePoint.getX(), telePoint.getY(), false);
                                                else if (AgilityHandler.doEvent(owner, object.getID())){}
                                                else if (command.equals("balance on") || command.equals("balance"))
                                                        handleBalanceOnEvent();
                                                else if (command.equals("climb"))
                                                        handleClimbEvent();
                                                else if (command.equals("swing") || command.equals("swing on"))
                                                        handleSwingEvent();
                                                else if (command.equals("enter"))
                                                        handleEnterEvent();
                                                else if (command.equals("jump"))
                                                        handleJumpEvent();
                                                else if (command.equals("climb-up"))
                                                        handleClimbUpEvent();
                                                else if (command.equals("go up"))
                                                        handleGoUpEvent();
                                                else if (command.equals("climb-down"))
                                                        handleClimbDownEvent();
                                                else if (command.equals("climb down"))
                                                        handleClimb_DownEvent();
                                                else if (command.equals("go down"))
                                                        handleGoDownEvent();
                                                else if (command.equals("steal from"))
                                                        handleStealFrom();
                                                else if (command.equals("search for traps"))
                                                        handleSearchForTraps();
                                                else if (command.equals("rest"))
                                                        handleRest();
                                                else if (command.equals("lie in"))
                                                        handleRest();
                                                else if (command.equals("push"))
                                                        handlePush();
                                                else if (command.equals("pull"))
                                                        handlePull();
                                                else if (command.equals("warp"))
                                                        handleWarp();
                                                else if (command.equals("listen"))
                                                        handleListen();
                                                else if (command.equals("open"))
                                                        handleOpen();
                                                else if (command.equals("close"))
                                                        handleClose();
                                                else if (command.equals("approach"))
                                                        handleApproach();
                                                else if (command.equals("pick") || command.equals("pick banana") || command.equals("pick pineapple"))
                                                        handlePick();
                                                else if (command.equals("mine"))
                                                        handleMining(click);
                                                else if(command.equals("prospect"))
                                                        handleProspect();
                                                else if (command.equals("lure") || command.equals("bait") || command.equals("net") || command.equals("harpoon") || command.equals("cage"))
                                                         handleFishing(click);
                                                else if (command.equals("chop"))
                                                        handleWoodcutting(click);
                                                /*
                                                 * Added by Pyru.
                                                 * Dramen Tree, I also changed
                                                 * the "command" in the database for 
                                                 * this object to "cut", instead of "chop".
                                                 * 
                                                 * I like this +1
                                                 */
                                                else if (command.equals("cut"))
                                                        handleDramenEvent();
                                                else if (command.equals("stow away"))
                                                        handleShipEvent();
                                                /*
                                                 * End Pyru's addition.
                                                 */
                                                else if (command.equals("recharge at"))
                                                        handleRechargeAt();
                                                else if (command.equals("board"))
                                                        handleBoard();
                                                else if (command.equals("jump off"))
                                                        handleJumpOff();
                                                else if (command.equals("jump to next"))
                                                        handleJumpToNext();
                                                else if (command.equals("examine"))
                                                        handleExamine();
                                                else if (command.equals("search"))
                                                        handleSearch();
                                                else if (command.equals("twist"))
                                                        handleTwist();
                                                else if (command.equals("hit"))
                                                        handleHit();
                                                else if (command.equals("operate"))
                                                        handleOperate();
                                                else if (command.equals("drink from"))
                                                        handleDrinkFrom();
                                                else if(command.equals("inspect"))
                                                        handleInspect();
                                                else if(command.equals("pick up"))
                                                        handlePickUp();
                                                else if(command.equals("fire"))
                                                        handleFire();
                                                else if (command.equals("walk through")) // Runecrafting
                                                        handleWalkThrough(object.getX(), object.getY());
                                                else if (command.equals("go-up")) // Custom.
                                                        handleGoUp(object.getX(), object.getY());
                                                else if (command.equals("talk through")) // Jail Gate - Tree Gnome Village
                                                        handleTalkThrough(object.getX(), object.getY());
                                                else if (command.equals("talk to")) //  Teleport Trees.
                                                        handleTalkTo(object.getX(), object.getY());
                                                else
                                                        owner.sendMessage("Nothing interesting happens");
                                        }
                                        
                                        
                                private void handleWalkThrough(final int objectX, final int objectY) {
                                        owner.setBusy(true);
                                        owner.sendMessage("You step through the portal.");
                                        World.getDelayedEventHandler().add(new SingleEvent(owner, 1500) {
                                                public void action() {
                                                        if (objectX == 153 && objectY == 3594) // Body
                                                                owner.teleport(331, 507, false);
                                                        else if (objectX == 152 && objectY == 3739) // Air
                                                                owner.teleport(298, 586, false);
                                                        else if (objectX == 59 && objectY == 3739) // Water
                                                                owner.teleport(174, 682, false);
                                                        else if (objectX == 202 && objectY == 3739) // Fire
                                                                owner.teleport(50, 646, false);
                                                        else if (objectX == 246 && objectY == 3740) // Earth
                                                                owner.teleport(84, 468, false);
                                                        else if (objectX == 10 && objectY == 3737) // Nature
                                                                owner.teleport(479, 671, false);
                                                        else if (objectX == 59 && objectY == 3687) // Law
                                                                owner.teleport(401, 539, false);
                                                        else if (objectX == 107 && objectY == 3595) // Cosmic
                                                                owner.teleport(148, 3538, false);
                                                        else if (objectX == 109 && objectY == 3740) // Chaos
                                                                owner.teleport(208, 394, false);
                                                        else if (objectX == 7 && objectY == 3691) // Death
                                                                owner.teleport(163, 119, false);
                                                        else if (objectX == 8 && objectY == 3644) // Blood
                                                                owner.teleport(253, 124, false);
                                                        else if (objectX == 298 && objectY == 3739) // Soul
                                                                owner.teleport(495, 533, false);
                                                        else if (objectX == 12 && objectY == 3594) // Mind
                                                                owner.teleport(233, 474, false);
                                                        owner.setBusy(false);                                                                   
                                                }
                                        });                                                     
                                }
                                
                                private void handleTalkTo(final int objectX, final int objectY) 
                                {
                                        switch (object.getID()) 
                                        {
                                                case 390:
                                                case 391:
                                                case 661:
                                                        Quest q = owner.getQuest(Quests.TREE_GNOME_VILLAGE);
                                                        if(q != null && q.finished())
                                                        {
                                                                owner.sendMessage("The tree whispers in your mind... Where would you like to be teleported to?");
                                                                String[] options = new String[]{ "Gnome Stronghold", "Tree Gnome Village", "Between Varrock & Edgeville", "Battle Field" };
                                                                final Point[] locations = { Point.location(703, 488), Point.location(658, 695), Point.location(161, 453), Point.location(629, 629) };
                                                                owner.setMenuHandler(new MenuHandler(options) 
                                                                {
                                                                        public void handleReply(final int option, final String reply) 
                                                                        {
                                                                                if (!owner.isBusy()) 
                                                                                {
                                                                                        owner.sendMessage("You feel at one with the spirit tree");
                                                                                        owner.teleport(locations[option].getX(), locations[option].getY(), true);
                                                                                        return;
                                                                                }
                                                                        }
                                                                });
                                                                owner.sendMenu(options);
                                                                return;
                                                        }
                                                        else
                                                        {
                                                                owner.sendMessage("You must complete the Tree Gnome Village quest in order to use the " + object.getGameObjectDef().name);
                                                                return;
                                                        }
                                                }
                                        }
                                        

                                        private void handleGoUp(final int objectX, final int objectY) {
                                                owner.setBusy(true);
                                                owner.sendMessage("You climb up the ladder.");
                                                World.getDelayedEventHandler().add(new SingleEvent(owner, 0) {
                                                        public void action() {          
                                                                if (objectX == 221 && objectY == 3693)
                                                                        owner.teleport(233, 436, false);
                                                                owner.setBusy(false);                                                                   
                                                        }
                                                });                                                     
                                        }
                                        
                                        /*
                                         * Tree Gnome Village quest 
                                         * jail gate.
                                         */
                                        private void handleTalkThrough(final int objectX, final int objectY) 
                                        {
                                                switch (object.getID()) 
                                                {
                                                        case 392:
                                                                Quest Tree_Gnome_Village = owner.getQuest(Quests.TREE_GNOME_VILLAGE);
                                                                switch (Tree_Gnome_Village.getStage())
                                                                {
                                                                        case 4:
                                                                                final Npc Tracker_2 = World.getNpc(405, 647, 650, 628, 629);
                                                                                if (Tracker_2 != null) 
                                                                                {
                                                                                        Tracker_2.blockedBy(owner);
                                                                                        owner.setBusy(true);
                                                                                        World.getDelayedEventHandler().add(new DelayedQuestChat(owner, Tracker_2, new String[] {"Are you ok?"}, true) 
                                                                                        {
                                                                                                public void finished()
                                                                                                {
                                                                                                        owner.sendMessage("The gnome looks beaten and weak");
                                                                                                        World.getDelayedEventHandler().add(new DelayedQuestChat(Tracker_2, owner, new String[] {"They caught me spying on the stronghold..", "They beat and tortured me", "But I didn't crack, I told them nothing", "They can't break me"})
                                                                                                        {
                                                                                                                public void finished() 
                                                                                                                {
                                                                                                                        World.getDelayedEventHandler().add(new DelayedQuestChat(owner, Tracker_2, new String[] {"I'm sorry little man"}) 
                                                                                                                        {
                                                                                                                                public void finished() 
                                                                                                                                {
                                                                                                                                        World.getDelayedEventHandler().add(new DelayedQuestChat(Tracker_2, owner, new String[] {"Don't be, I have the position of the stronghold", "The Y coordinate is 5"}) 
                                                                                                                                        {
                                                                                                                                                public void finished() 
                                                                                                                                                {
                                                                                                                                                        World.getDelayedEventHandler().add(new DelayedQuestChat(owner, Tracker_2, new String[] {"Well done"}) 
                                                                                                                                                        {
                                                                                                                                                                public void finished() 
                                                                                                                                                                {
                                                                                                                                                                        World.getDelayedEventHandler().add(new DelayedQuestChat(Tracker_2, owner, new String[] {"Now leave before they find you and all is lost"}) 
                                                                                                                                                                        {
                                                                                                                                                                                public void finished() 
                                                                                                                                                                                {
                                                                                                                                                                                        World.getDelayedEventHandler().add(new DelayedQuestChat(owner, Tracker_2, new String[] {"Hang in there"}) 
                                                                                                                                                                                        {
                                                                                                                                                                                                public void finished() 
                                                                                                                                                                                                {
                                                                                                                                                                                                        World.getDelayedEventHandler().add(new DelayedQuestChat(Tracker_2, owner, new String[] {"Go!"})
                                                                                                                                                                                                        {
                                                                                                                                                                                                                public void finished() 
                                                                                                                                                                                                                {
                                                                                                                                                                                                                        owner.setBusy(false);
                                                                                                                                                                                                                        Tracker_2.unblock();
                                                                                                                                                                                                                }
                                                                                                                                                                                                        });
                                                                                                                                                                                                }       
                                                                                                                                                                                        });             
                                                                                                                                                                                }
                                                                                                                                                                        });
                                                                                                                                                                }
                                                                                                                                                        });
                                                                                                                                                }
                                                                                                                                        });
                                                                                                                                }
                                                                                                                        });
                                                                                                                }
                                                                                                        });
                                                                                                }
                                                                                        });
                                                                                }
                                                                        break;
                                                                        
                                                                        default:
                                                                                final Npc Tracker2 = World.getNpc(405, 647, 650, 628, 629);
                                                                                if (Tracker2 != null) 
                                                                                {
                                                                                        for(Player informee : Tracker2.getViewArea().getPlayersInView())
                                                                                        {
                                                                                                informee.informOfNpcMessage(new ChatMessage(Tracker2, "This is no place for a civilian, get out of here!", owner));
                                                                                        }
                                                                                }
                                                                        break;
                                                                }
                                                        break;
                                                }
                                        }
                                        /*
                                         * End jail gate for tree gnome village
                                         * quest.
                                         */
                                        
                                        private void handleShipEvent() {
                                                switch(object.getID()) {
                                                case 292:
                                                case 293:
                                                        Quest q22 = owner.getQuest(Quests.MERLINS_CRYSTAL);
                                                        if (q22 != null) {
                                                                if (q22.finished()) {
                                                                        owner.sendMessage("You sneak aboard the ship.");
                                                                        owner.teleport(457, 3353);              
                                                                }
                                                                else
                                                                if (q22.getStage() == 3) {
                                                                        owner.sendMessage("You sneak aboard the ship.");
                                                                        owner.teleport(457, 3353);
                                                                } else
                                                                        owner.sendMessage("Nothing interesting happens.");
                                                        } else 
                                                                owner.sendMessage("Nothing interesting happens.");
                                                break;
                                                }
                                        }                               
                                                                                
                                        
                                        private void handleFire() 
                                        {
                                                switch (object.getID()) 
                                                {
                                                        case 1118:
                                                                //owner.addCannonEvent();
                                                        break;
                                                        
                                                        /*
                                                         * Ballista Tree Gnome Village Quest
                                                         */
                                                        case 388:
                                                                Quest Tree_Gnome_Village = owner.getQuest(Quests.TREE_GNOME_VILLAGE);
                                                                switch (Tree_Gnome_Village.getStage())
                                                                {
                                                                        case 4:
                                                                                owner.sendMessage("To fire the ballista you must first set the coordinates");
                                                                                String[] options = new String[]{ "Coord 1", "Coord 2", "Coord 3", "Coord 4", "Coord 5" };
                                                                                owner.sendMessage("Set the height coordinate to");
                                                                                owner.setBusy(false);
                                                                                owner.sendMenu(options);
                                                                                owner.setMenuHandler(new MenuHandler(options) 
                                                                                {
                                                                                        public void handleReply(final int option, final String reply) 
                                                                                        {
                                                                                                switch(option)
                                                                                                {
                                                                                                        case 0:
                                                                                                        case 1:
                                                                                                        case 2:
                                                                                                        case 4:
                                                                                                                String[] options = new String[]{ "Coord 1", "Coord 2", "Coord 3", "Coord 4", "Coord 5" };
                                                                                                                owner.sendMessage("Set the x coordinate to");
                                                                                                                owner.sendMenu(options);
                                                                                                                owner.setMenuHandler(new MenuHandler(options) 
                                                                                                                {
                                                                                                                        public void handleReply(final int option, final String reply) 
                                                                                                                        {
                                                                                                                                switch(option)
                                                                                                                                {
                                                                                                                                case 0:
                                                                                                                                case 1:
                                                                                                                                case 2:
                                                                                                                                case 4:
                                                                                                                                case 3:
                                                                                                                                        String[] options = new String[]{ "Coord 1", "Coord 2", "Coord 3", "Coord 4", "Coord 5" };
                                                                                                                                        owner.sendMessage("Set the y coordinate to");
                                                                                                                                        owner.sendMenu(options);
                                                                                                                                        owner.setMenuHandler(new MenuHandler(options) 
                                                                                                                                        {
                                                                                                                                                public void handleReply(final int option, final String reply) 
                                                                                                                                                {
                                                                                                                                                        switch(option)
                                                                                                                                                        {
                                                                                                                                                        case 0:
                                                                                                                                                        case 1:
                                                                                                                                                        case 2:
                                                                                                                                                        case 3:
                                                                                                                                                        case 4:
                                                                                                                                                                owner.sendMessage("You fire the ballista");
                                                                                                                                                                owner.sendMessage("The huge spear flies through the air..");
                                                                                                                                                                owner.sendMessage("..And misses the stronghold");
                                                                                                                                                        break;
                                                                                                                                                        }
                                                                                                                                                }
                                                                                                                                        });
                                                                                                                                break;
                                                                                                                                }
                                                                                                                        }
                                                                                                                });
                                                                                                        break;
                                                                                                        
                                                                                                        case 3:
                                                                                                        //correct path
                                                                                                                String[] options1 = new String[]{ "Coord 1", "Coord 2", "Coord 3", "Coord 4", "Coord 5" };
                                                                                                                owner.sendMessage("Set the x coordinate to");
                                                                                                                owner.sendMenu(options1);
                                                                                                                owner.setMenuHandler(new MenuHandler(options1) 
                                                                                                                {
                                                                                                                        public void handleReply(final int option, final String reply) 
                                                                                                                        {
                                                                                                                                switch(option)
                                                                                                                                {
                                                                                                                                case 0:
                                                                                                                                case 1:
                                                                                                                                case 3:
                                                                                                                                case 4:
                                                                                                                                        String[] options = new String[]{ "Coord 1", "Coord 2", "Coord 3", "Coord 4", "Coord 5" };
                                                                                                                                        owner.sendMessage("Set the y coordinate to");
                                                                                                                                        owner.sendMenu(options);
                                                                                                                                        owner.setMenuHandler(new MenuHandler(options) 
                                                                                                                                        {
                                                                                                                                                public void handleReply(final int option, final String reply) 
                                                                                                                                                {
                                                                                                                                                        switch(option)
                                                                                                                                                        {
                                                                                                                                                        case 0:
                                                                                                                                                        case 1:
                                                                                                                                                        case 2:
                                                                                                                                                        case 3:
                                                                                                                                                        case 4:
                                                                                                                                                                owner.sendMessage("You fire the ballista");
                                                                                                                                                                owner.sendMessage("The huge spear flies through the air..");
                                                                                                                                                                owner.sendMessage("..And misses the stronghold");       
                                                                                                                                                        break;
                                                                                                                                                        }
                                                                                                                                                }
                                                                                                                                        });     
                                                                                                                                break;
                                                                                                        
                                                                                                                                case 2:
                                                                                                                                        //correct path
                                                                                                                                        String[] options2 = new String[]{ "Coord 1", "Coord 2", "Coord 3", "Coord 4", "Coord 5" };
                                                                                                                                        owner.sendMessage("Set the y coordinate to");
                                                                                                                                        owner.sendMenu(options2);
                                                                                                                                        owner.setMenuHandler(new MenuHandler(options2) 
                                                                                                                                        {
                                                                                                                                                public void handleReply(final int option, final String reply) 
                                                                                                                                                {
                                                                                                                                                        switch(option)
                                                                                                                                                        {
                                                                                                                                                                case 0:
                                                                                                                                                                case 1:
                                                                                                                                                                case 2:
                                                                                                                                                                case 3:
                                                                                                                                                                        owner.sendMessage("You fire the ballista");
                                                                                                                                                                        owner.sendMessage("The huge spear flies through the air..");
                                                                                                                                                                        owner.sendMessage("..And misses the stronghold");
                                                                                                                                                                break;
                                                                                                                                                                
                                                                                                                                                                case 4:
                                                                                                                                                                        owner.incQuestCompletionStage(Quests.TREE_GNOME_VILLAGE);
                                                                                                                                                                        owner.sendMessage("You fire the ballista");
                                                                                                                                                                        owner.sendMessage("The huge spear flies through the air");
                                                                                                                                                                        owner.sendMessage("And screams down directly into the Khazard stronghold");
                                                                                                                                                                        owner.sendMessage("A deafening crash echoes over the battlefield");
                                                                                                                                                                        owner.sendMessage("The front entrance is reduced to rubble");
                                                                                                                                                                break;
                                                                                                                                                        }
                                                                                                                                                }
                                                                                                                                        });
                                                                                                                                break;
                                                                                                                                }
                                                                                                                        }
                                                                                                                });
                                                                                                        break;
                                                                                                }
                                                                                        }
                                                                                });
                                                                                owner.sendMenu(options);
                                                                        break;
                                                                }
                                                        break;
                                                }
                                        }
                                        
                                        /*
                                         * CTF Addition
                                         */
                                        
                                        public void setFlagDelay(long timeLeft) 
                                        {
                                                owner.flagDelay = new DelayedEvent(owner, 500) 
                                                {
                                                        public void run() 
                                                        {
                                                                if (owner.hasRedFlag() == true || owner.hasBlueFlag() == true)
                                                                {
                                                                        if (owner.hasBlueFlag() == true)
                                                                        {
                                                                                owner.flagDelay.stop();
                                                                                owner.flagDelay = null;
                                                                                displayBlueflag();
                                                                        }
                                                                        else if (owner.hasRedFlag() == true)
                                                                        {
                                                                                owner.flagDelay.stop();
                                                                                owner.flagDelay = null;
                                                                                displayRedflag();
                                                                        }
                                                                }
                                                                else
                                                                {
                                                                        owner.flagDelay.stop();
                                                                        owner.flagDelay = null;
                                                                }
                                                                
                                                        }
                                                };
                                                        World.getDelayedEventHandler().add(owner.flagDelay);
                                        }
                                        
                                        public void displayRedflag()
                                        {
                                                //Bubble redFlag = new Bubble(owner, 1290);
                                                for(Player p : owner.getViewArea().getPlayersInView()) 
                                                {
                                                        p.watchItemBubble(owner.getIndex(), 1337);
                                                }
                                                setFlagDelay(500);              
                                        }
                                        
                                        public void displayBlueflag()
                                        {
                                                //Bubble blueFlag = new Bubble(owner, 1291); 
                                                for(Player p : owner.getViewArea().getPlayersInView()) 
                                                {
                                                        p.watchItemBubble(owner.getIndex(), 1338);
                                                }
                                                setFlagDelay(500);
                                        }
                                        /*
                                         * END CTF
                                         */
                                        
                                        private void handlePickUp() {
                                                /*switch(object.getID()) {
                                                case 1118:
                                                        if(owner.getCannonX() != object.getX() || owner.getCannonY() != object.getY()) {
                                                                owner.sendMessage("This isn't your cannon!");
                                                        } else {
                                                                owner.resetCannonEvent();
                                                                World.unregisterGameObject(object);
                                                                owner.sendMessage("You pick up your cannon");
                                                                owner.getInventory().add(new InvItem(1032, 1));
                                                                owner.getInventory().add(new InvItem(1033, 1));
                                                                owner.getInventory().add(new InvItem(1034, 1));
                                                                owner.getInventory().add(new InvItem(1035, 1));
                                                                owner.sendInventory();
                                                                owner.updateCannonX(-1);
                                                                owner.updateCannonY(-1);
                                                                owner.updateCannonStage(-1);
                                                        }
                                                        break;
                                                case 1119:
                                                        if(owner.getCannonX() != object.getX() || owner.getCannonY() != object.getY()) {
                                                                owner.sendMessage("This isn't your cannon!");
                                                        } else {
                                                                World.unregisterGameObject(object);
                                                                owner.sendMessage("You pick up your base");
                                                                owner.getInventory().add(new InvItem(1032, 1));
                                                                owner.sendInventory();
                                                                owner.updateCannonX(-1);
                                                                owner.updateCannonY(-1);
                                                                owner.updateCannonStage(-1);
                                                        }
                                                        break;
                                                case 1120:
                                                        if(owner.getCannonX() != object.getX() || owner.getCannonY() != object.getY()) {
                                                                owner.sendMessage("This isn't your cannon!");
                                                        } else {
                                                                World.unregisterGameObject(object);
                                                                owner.sendMessage("You pick up your base and stand");
                                                                owner.getInventory().add(new InvItem(1032, 1));
                                                                owner.getInventory().add(new InvItem(1033, 1));
                                                                owner.sendInventory();
                                                                owner.updateCannonX(-1);
                                                                owner.updateCannonY(-1);
                                                                owner.updateCannonStage(-1);
                                                        }
                                                        break;
                                                case 1121:
                                                        if(owner.getCannonX() != object.getX() || owner.getCannonY() != object.getY()) {
                                                                owner.sendMessage("This isn't your cannon!");
                                                        } else {
                                                                World.unregisterGameObject(object);
                                                                owner.sendMessage("You pick up your base, stand, and barrels");
                                                                owner.getInventory().add(new InvItem(1032, 1));
                                                                owner.getInventory().add(new InvItem(1033, 1));
                                                                owner.getInventory().add(new InvItem(1034, 1));
                                                                owner.sendInventory();
                                                                owner.updateCannonX(-1);
                                                                owner.updateCannonY(-1);
                                                                owner.updateCannonStage(-1);
                                                        }
                                                        break;
                                                default:
                                                        owner.sendMessage("Nothing interesting happens");
                                                }*/
                                        }
                                        
                                        private void handleDrinkFrom() {
                                                switch (object.getID()) {
                                                        case 147:
                                                                owner.setBusy(true);
                                                                if(owner.getQuestCompletionStage(Quests.WITCHS_POTION) == 1) {
                                                                        owner.sendMessage("You drink from the cauldron");
                                                                        World.getDelayedEventHandler().add(new SingleEvent(owner, 1000) {
                                                                                public void action() {
                                                                                        owner.sendMessage("You feel yourself imbued with power");
                                                                                        World.getDelayedEventHandler().add(new SingleEvent(owner, 1000) {
                                                                                                public void action() {
                                                                                                        owner.incQuestCompletionStage(Quests.WITCHS_POTION);
                                                                                                        owner.sendMessage("Well done you have completed the witches potion");
                                                                                                        owner.sendMessage("@gre@You haved gained 1 quest point!");
                                                                                                        owner.incQuestExp(Skills.MAGIC, 1000);
                                                                                                        owner.sendStat(6);
                                                                                                        owner.finishQuest(Quests.WITCHS_POTION);
                                                                                                        owner.setBusy(false);
                                                                                                        Logger.log(new eventLog(owner.getUsernameHash(), owner.getAccount(), owner.getIP(), DataConversions.getTimeStamp(), "<strong>" + owner.getUsername() + "</strong>" + " has completed the <span class=\"recent_quest\">Witches Potion</span> quest!"));
                                                                                                }
                                                                                        });
                                                                                }
                                                                        });
                                                                } else {
                                                                        final String[] messages4 = {"I'd rather not", "It doesn't look very tasty"};
                                                                        World.getDelayedEventHandler().add(new DelayedQuestChat(owner, owner, messages4, true) {
                                                                                public void finished() {
                                                                                        owner.setBusy(false);
                                                                                }
                                                                        });
                                                                }
                                                        break;
                                                }
                                        }
                                        
                                        private void handleOperate() {
                                                switch (object.getID()) {
                                                        case 52:
                                                                owner.sendMessage("You operate the hopper.");
                                                                if (owner.isGrainInDraynorHopper()) {
                                                                        owner.grainInDraynorHopper(false);
                                                                        owner.sendMessage("The grain slides down the chute.");
                                                                        World.registerEntity(new Item(23, 166, 599, 1, owner));
                                                                } else
                                                                        owner.sendMessage("Nothing interesting happens");
                                                        break;
                                                        
                                                        case 173: //Cooking Guild Hopper
                                                                owner.sendMessage("You operate the hopper.");
                                                                if (owner.isGrainInCookingGuildHopper()) {
                                                                        owner.grainInCookingGuildHopper(false);
                                                                        owner.sendMessage("The grain slides down the chute.");
                                                                        World.registerEntity(new Item(23, 179, 481, 1, owner));
                                                                } else
                                                                        owner.sendMessage("Nothing interesting happens");
                                                        break;
                                                }
                                        }
                                        
                                        
                                        private void handleGoUpEvent() {
                                                switch (object.getID()) {
                                                        case 621:
                                                                owner.teleport(605, 3555, false);
                                                        break;
                                                                
                                                        case 342:
                                                                final Npc paladin = World.getNpc(323, 608, 613, 599, 607);
                                                                if (paladin != null) {
                                                                        for(Player informee : paladin.getViewArea().getPlayersInView()) {
                                                                                informee.informOfNpcMessage(new ChatMessage(paladin, "Stop right there!", owner));
                                                                        }
                                                                        paladin.setAggressive(owner);
                                                                        World.getDelayedEventHandler().add(new ShortEvent(owner) {
                                                                                public void action() {
                                                                                        if(!owner.isBusy()) {
                                                                                                owner.sendMessage("You go up the " + object.getGameObjectDef().getName());
                                                                                                int[] coords = coordModifier(owner, true);
                                                                                                owner.teleport(coords[0], coords[1], false);                                                                                            
                                                                                        }
                                                                                }
                                                                        });
                                                                } else {
                                                                        owner.sendMessage("You go up the " + object.getGameObjectDef().getName());
                                                                        int[] coords = coordModifier(owner, true);
                                                                        owner.teleport(coords[0], coords[1], false);
                                                                }
                                                        break;
                                                                
                                                        default:
                                                                owner.sendMessage("You go up the " + object.getGameObjectDef().getName());
                                                                int[] coords = coordModifier(owner, true);
                                                                owner.teleport(coords[0], coords[1], false);
                                                                
                                                                if (owner.getLocation().isInSeersPartyHall()) {
                                                                        owner.sendAlert("Welcome to Seers Party Hall!% %If you want to drop an item simply right-click the item in your inventory and then use it on the chest in the corner to fairly drop it in a random location.% %Telekinetic Grab cannot be used.", true);
                                                                }
                                                }
                                        }
                                        
                                        private void handleClimb_DownEvent() {
                                                switch (object.getID()) {
                                                        case 79:
                                                                /*
                                                                 * Biohazard / Plague city manhole.
                                                                 */
                                                                if (object.getX() == 632 && object.getY() == 590)
                                                                {
                                                                        if (owner.getQuest(Quests.PLAGUE_CITY) != null && owner.getQuest(Quests.PLAGUE_CITY).finished())
                                                                        {
                                                                                owner.sendMessage("It's filled with concrete.");
                                                                                return;
                                                                        }
                                                                }
                                                                /*
                                                                 * End Biohazard
                                                                 * Addition.
                                                                 */

                                                                if (object.getX() == 233  && object.getY() == 437) 
                                                                {
                                                                        owner.sendMessage("You climb down the manhole.");
                                                                        owner.teleport(221, 3692, false);
                                                                } 
                                                                else 
                                                                {
                                                                        owner.sendMessage("You climb down the " + object.getGameObjectDef().getName());
                                                                        int[] coords = coordModifier(owner, false);
                                                                        owner.teleport(coords[0], coords[1], false);
                                                                }
                                                        break;
                                                }
                                        }
                                        
                                        private void handleGoDownEvent() 
                                        {
                                                
                                                switch (object.getID()) 
                                                {
                                                
                                                        case 359:
                                                        
                                                        /*
                                                         *  Fishing Contest quest addition, disable users
                                                         *  from using the stairs, if they have not completed
                                                         *  the Fishing Contest Quest
                                                         */
                                                                
                                                        Quest Fishing_Contest = owner.getQuest(Quests.FISHING_CONTEST);
                                                        if (Fishing_Contest == null || Fishing_Contest != null && !Fishing_Contest.finished())
                                                        {
                                                                
                                                                Npc Mountain_Dwarf = World.getNpc(355, 426, 426, 456, 456);
                                                                if (Mountain_Dwarf != null) 
                                                                {
                                                                        for(Player informee : Mountain_Dwarf.getViewArea().getPlayersInView()) 
                                                                        {
                                                                                informee.informOfNpcMessage(new ChatMessage(Mountain_Dwarf, "Stop right there!", owner));
                                                                        }
                                                                        World.getDelayedEventHandler().add(new ShortEvent(owner) 
                                                                        {
                                                                                public void action()
                                                                                {
                                                                                        owner.sendMessage("You must complete the fishing contest quest before using these stairs.");
                                                                                }
                                                                        });
                                                                }
                                                        }
                                                        else
                                                        {
                                                                int[] coords = coordModifier(owner, false);
                                                                owner.sendMessage("You go down the " + object.getGameObjectDef().getName());
                                                                owner.teleport(coords[0], coords[1], false);    
                                                        }
                                                        break;
                                                        
                                                        case 369:
                                                                if (object.getX() == 534 && object.getY() == 3371)
                                                                {
                                                                        owner.sendMessage("You go down the stairs.");
                                                                        owner.teleport(536, 3337);
                                                                        return;
                                                                }       
                                                        break;
                                                        
                                                        case 370:
                                                                if (object.getX() == 537 && object.getY() == 3337)
                                                                {
                                                                        owner.sendMessage("You go down the stairs.");
                                                                        owner.teleport(537, 3372);
                                                                        return;
                                                                }
                                                        break;
                                                        
                                                        default:
                                                                
                                                        int[] coords = coordModifier(owner, false);
                                                        owner.sendMessage("You go down the " + object.getGameObjectDef().getName());
                                                        owner.teleport(coords[0], coords[1], false);                                            
                                                }
                                        }
                                        
                                        private void handlePull() {
                                                if (owner.getLocation().inWilderness() && System.currentTimeMillis() - owner.getLastMoved() < 5000 && System.currentTimeMillis() - owner.getCombatTimer() < 5000 && !owner.isSuperMod())
                                                        owner.sendMessage("You must stand still for 5 seconds");
                                                else {
                                                        switch (object.getID()) {       
                                                                case 349:
                                                                if (object.getLocation().isInDMArena())
                                                                {
                                                                                        if(owner.canPullDMExitLever())//dmvictoryevent != null
                                                                                        {
                                                                                                super.owner.sendMessage(Config.getPrefix() + "As you pull the lever, you teleport back to the DM Arena");
                                                                                                owner.teleport(Point.location(218, 2901), true);
                                                                                                owner.dmVictoryEvent.cage.setActive(false);
                                                                                                owner.dmVictoryEvent.stop();
                                                                                                owner.dmVictoryEvent = null;
                                                                                        }
                                                                                        else // If the lever is pulled in the middle of a DM
                                                                                        {
                                                                                                owner.sendMessage(Config.getPrefix() + "You cannot pull the lever until you've defeated your opponent");
                                                                                        }
                                                                                        return;
                                                                }
                                                                // Ardy lever, from wilderness to Ardougne.
                                                                if (object.getX() == 180 && object.getY() == 129)
                                                                {
                                                                        if (owner.getLocation().inWilderness() && System.currentTimeMillis() - owner.getLastMoved() < 15000 || System.currentTimeMillis() - owner.getCombatTimer() < 15000)
                                                                                owner.sendMessage(Config.getPrefix() + "You must stand still for 15 seconds, and be out of combat to use this lever.");
                                                                        else
                                                                        if (owner.inCombat())
                                                                                owner.sendMessage(Config.getPrefix() + "You cannot use this lever while being in combat.");
                                                                        else
                                                                                owner.teleport(621, 596, true);
                                                                }
                                                                else
                                                                // Ardy lever, from Ardougne to Wilderness.
                                                                if (object.getX() == 621 && object.getY() == 596)
                                                                {
                                                                        owner.teleport(180, 128, true);
                                                                        return;
                                                                }
                                                                else
                                                                        owner.sendMessage("Nothing interesting happens.");
                                                                break;
                                                                
                                                                case 373:
                                                                        if (object.getY() == 3478) {
                                                                                if (object.getX() == 594) {
                                                                                        owner.sendMessage("The gate swings open");
                                                                                        replaceGameObject(374, true);
                                                                                        GameObject oldGate = World.getZone(590, 3475).getObjectAt(590, 3475);
                                                                                        if (oldGate != null)
                                                                                                replaceGameObject(oldGate, new GameObject(590, 3475, 372, 0, 0));
                                                                                } else if(object.getX() == 590) {
                                                                                        owner.sendMessage("The gate swings open");
                                                                                        replaceGameObject(374, true);
                                                                                        GameObject oldGate = World.getZone(594, 3475).getObjectAt(594, 3475);
                                                                                        if (oldGate != null)
                                                                                                replaceGameObject(oldGate, new GameObject(594, 3475, 372, 0, 0));
                                                                                }
                                                                        }
                                                                break;
                                                                        
                                                                case 487:                                                                       
                                                                        if (owner.inCombat() || System.currentTimeMillis() - owner.getCombatTimer() < 15000 || System.currentTimeMillis() - owner.getLastMoved() < 15000)
                                                                        {
                                                                                owner.sendMessage("You must be out of combat for 15 seconds to use this lever.");
                                                                                return;
                                                                        }
                                                                        else
                                                                        {
                                                                                owner.sendMessage("You pull the lever");
                                                                                owner.teleport(567, 3330, true);
                                                                        }
                                                                break;
                                                                        
                                                                case 488:
                                                                        owner.sendMessage("You pull the lever");
                                                                        owner.teleport(282, 3019, true);
                                                                break;
                                                                        
                                                                case 124: //Lever A
                                                                        if (owner.leverADown())
                                                                                owner.pullLeverAUp();
                                                                        else
                                                                                owner.pullLeverADown();
                                                                        owner.sendMessage("You pull lever A " + (owner.leverADown() ? "down" : "up"));
                                                                        owner.sendMessage("You hear a clunk");
                                                                break;
                                                                
                                                                case 125: //Lever B
                                                                        if (owner.leverBDown())
                                                                                owner.pullLeverBUp();
                                                                        else
                                                                                owner.pullLeverBDown();
                                                                        owner.sendMessage("You pull lever B " + (owner.leverBDown() ? "down" : "up"));
                                                                        owner.sendMessage("You hear a clunk");
                                                                break;
                                                                        
                                                                case 126:
                                                                        if (owner.leverCDown())
                                                                                owner.pullLeverCUp();
                                                                        else
                                                                                owner.pullLeverCDown();
                                                                        owner.sendMessage("You pull lever C " + (owner.leverCDown() ? "down" : "up"));
                                                                        owner.sendMessage("You hear a clunk");
                                                                break;
                                                                        
                                                                case 127:
                                                                        if (owner.leverDDown())
                                                                                owner.pullLeverDUp();
                                                                        else
                                                                                owner.pullLeverDDown();
                                                                        owner.sendMessage("You pull lever D " + (owner.leverDDown() ? "down" : "up"));
                                                                        owner.sendMessage("You hear a clunk");
                                                                break;
                                                                        
                                                                case 128:
                                                                        if (owner.leverEDown())
                                                                                owner.pullLeverEUp();
                                                                        else
                                                                                owner.pullLeverEDown();
                                                                        owner.sendMessage("You pull lever E " + (owner.leverEDown() ? "down" : "up"));
                                                                        owner.sendMessage("You hear a clunk");
                                                                break;
                                                                
                                                                case 129:
                                                                        if (owner.leverFDown())
                                                                                owner.pullLeverFUp();
                                                                        else
                                                                                owner.pullLeverFDown();
                                                                        owner.sendMessage("You pull lever F " + (owner.leverFDown() ? "down" : "up"));
                                                                        owner.sendMessage("You hear a clunk");
                                                                break;
                                        
                                                                default:
                                                                        owner.sendMessage("Nothing interesting happens.");
                                                        }
                                                }
                                        }
                                        
                                        private void handleWarp() {
                                                if(World.getWildernessIPTracker().ipCount(player.getIP()) > Config.getAllowedConcurrentIpsInWilderness())
                                                        owner.sendMessage(Config.getPrefix() + "You have reached the IP limit for characters allowed in the wildereness.");
                                                else if(!World.getWorldLoader().canPortal(owner))
                                                        owner.sendMessage(Config.getPrefix() + "You must wait 15 seconds to use this portal after dying or logging out.");
                                                else if (owner.getLocation().inWilderness() && System.currentTimeMillis() - owner.getLastMoved() < 15000 && !owner.isSuperMod())
                                                        owner.sendMessage(Config.getPrefix() + "You must stand still for 15 seconds before using a wilderness portal");
                                                else if (System.currentTimeMillis() - owner.getCombatTimer() < 15000 && owner.getLocation().inWilderness() && !owner.isSuperMod())
                                                        owner.sendMessage(Config.getPrefix() + "You must be out of combat for 15 seconds before using a wilderness portal");
                                                else if (!owner.getLocation().isInDMArena() && owner.canWarp() && !owner.isSuperMod())
                                                        owner.sendMessage(Config.getPrefix() + "You cannot warp at this time");
                                                else {                                                                          
                                                        owner.sendMessage(Config.getPrefix() + "Where would you like to teleport?");
                                                        String[] options = new String[]
                                                        {
                                                                "Edgeville",
                                                                "Varrock",
                                                                "Varrock Border",
                                                                "Draynor",
                                                                "Falador",
                                                                "Ardougne",
                                                                "Catherby",
                                                                "Yanille",
                                                                "Mage Bank",
                                                                "", 
                                                                "@red@Wilderness:", 
                                                                "Castle", 
                                                                "Level-12 Altar", 
                                                                "Level-38 Altar",
                                                                "DM Arena"
                                                        };
                                                        final Point[] locations = 
                                                        {
                                                                Point.location(231, 442), 
                                                                Point.location(144, 506), 
                                                                Point.location(96, 441),
                                                                Point.location(211, 624), 
                                                                Point.location(291, 553),
                                                                Point.location(549, 594), 
                                                                Point.location(421, 492), 
                                                                Point.location(590, 746),
                                                                Point.location(445, 3374),
                                                                owner.getLocation(), 
                                                                owner.getLocation(), 
                                                                Point.location(268, 342), 
                                                                Point.location(115, 370), 
                                                                Point.location(330, 195),
                                                                Point.location(216, 2905)
                                                        };
                                                        final Point[] RandomCastleLocations = { Point.location(276, 352),  Point.location(271, 340),  Point.location(275, 362),  Point.location(271, 340),  Point.location(262, 339) ,  Point.location(255, 364),  Point.location(267, 343),  Point.location(274, 356),  Point.location(284, 356),  Point.location(266, 353),  Point.location(275, 356)  };
                                                                                                        
                                                        owner.setMenuHandler(new MenuHandler(options) {
                                                                public void handleReply(final int option, final String reply) {
                                                                        if (!owner.isBusy()) {
                                                                                
                                                                                /*
                                                                                 * Experimental Biohazard
                                                                                 * Quest.
                                                                                 */
                                                                                if (player.getInventory().containsAnyOf(809, 810, 811, 812))
                                                                                {
                                                                                        player.sendMessage("The vials break, you are going to have to get more.");
                                                                                        player.getInventory().remove(809);
                                                                                        player.getInventory().remove(810);
                                                                                        player.getInventory().remove(811);
                                                                                        player.getInventory().remove(812);
                                                                                        player.sendInventory();
                                                                                }
                                                                                
                                                                                if (option == 0 || locations[option].inWilderness() && !owner.getLocation().inWilderness())
                                                                                {
                                                                                        if (System.currentTimeMillis() - owner.getCombatTimer() < 30000 || System.currentTimeMillis() - owner.getLastLogin() < 30000)
                                                                                        {
                                                                                                owner.sendMessage(Config.getPrefix() + "You must wait 30 seconds to teleport to this location.");
                                                                                                return;
                                                                                        }
                                                                                }
                                                                                
                                                                                if (option == 10)
                                                                                {
                                                                                        if (locations[option].inWilderness() && !owner.getLocation().inWilderness()) 
                                                                                        {
                                                                                                for (int i = 0; i <= 6; i++) {
                                                                                                        if (owner.getCurStat(i) < owner.getMaxStat(i)) {
                                                                                                                owner.setCurStat(i, owner.getMaxStat(i));
                                                                                                                owner.sendStat(i);
                                                                                                                if (i == 5) owner.sendSound("recharge", false);
                                                                                                        }                                                                                               
                                                                                                }
                                                                                        }       
                                                                                        int rand = DataConversions.random(0, RandomCastleLocations.length - 1);
                                                                                        owner.teleport(RandomCastleLocations[rand].getX(), RandomCastleLocations[rand].getY()); 
                                                                                        owner.addSkull(1200000);
                                                                                }
                                                                                
                                                                                if (option != 10)
                                                                                {
                                                                                        if (locations[option].inWilderness() && option != 7) {  
                                                                                                for (int i = 0; i <= 6; i++) {
                                                                                                        if (owner.getCurStat(i) < owner.getMaxStat(i)) {
                                                                                                                owner.setCurStat(i, owner.getMaxStat(i));
                                                                                                                owner.sendStat(i);
                                                                                                                if (i == 5) owner.sendSound("recharge", false);
                                                                                                        }                                                                                               
                                                                                                }
                                                                                                owner.addSkull(1200000);
                                                                                        }       
                                                                                        owner.teleport(locations[option].getX(), locations[option].getY(), false);
                                                                                }
                                                                        }
                                                                }
                                                        });
                                                        owner.sendMenu(options);
                                                }
                                        }
                                        
                                        private void handleTwist() {
                                                switch (object.getID()) {
                                                        case 643: // Gnome tree stone
                                                                owner.setBusy(true);
                                                                owner.sendMessage("You twist the stone tile to one side");
                                                                World.getDelayedEventHandler().add(new ShortEvent(owner) {
                                                                        public void action() {
                                                                                owner.sendMessage("It reveals a ladder, you climb down");
                                                                                owner.teleport(703, 3284, false);
                                                                                owner.setBusy(false);
                                                                        }
                                                                });
                                                        break;
                                                                
                                                        default:
                                                                owner.sendMessage("Nothing interesting happens");
                                                }
                                        }
                                        
                                        private void handleHit() {
                                                switch (object.getID()) {
                                                        case 49:
                                                                owner.setBusy(true);
                                                                owner.sendMessage("You swing at the dummy");
                                                                World.getDelayedEventHandler().add(new SingleEvent(owner, 2000) {
                                                                        public void action() {
                                                                                owner.sendMessage("You hit the dummy");
                                                                                owner.sendMessage("There is nothing more you can learn from hitting a dummy");
                                                                                owner.setBusy(false);
                                                                                /*if (owner.getCurStat(0) < 8) {
                                                                                        owner.increaseXP(Skills.ATTACK, 5, 1); // Should be 5 XP in Attack
                                                                                        owner.sendStat(0);
                                                                                        owner.setBusy(false);
                                                                                        owner.increaseXP(Skills.HITS, 1, 1); // Should be 0 XP in Hits
                                                                                        owner.sendStat(3);
                                                                                } else {
                                                                                        owner.sendMessage("There is nothing more you can learn from hitting a dummy");
                                                                                        owner.setBusy(false);
                                                                                }
                                                                                */
                                                                        }
                                                                });
                                                        break;
                                                                
                                                        default:
                                                                owner.sendMessage("Nothing interesting happens");
                                                }
                                        }
                                        
                                        private void handleExamine() {
                                                switch (object.getID()) {
                                                        case 613:
                                                                owner.setBusy(true);
                                                                owner.sendMessage("You search for a way over the cart");
                                                                World.getDelayedEventHandler().add(new ShortEvent(owner) {
                                                                        public void action() {
                                                                                owner.sendMessage("You climb across");
                                                                                if (owner.getX() < 384)
                                                                                        owner.teleport(386, 851, false);
                                                                                else
                                                                                        owner.teleport(383, 851, false);
                                                                                owner.setBusy(false);
                                                                        }
                                                                });
                                                        break;
                                                        
                                                        default:
                                                                owner.sendMessage("Nothing interesting happens.");
                                                }
                                        }
                                        
                                        private void handleSearch() {
                                                switch (object.getID()) {
                                                
                                                /*
                                                 * Biohazard
                                                 * Crate
                                                 */
                                                case 505:
                                                        if(owner.getQuest(Quests.BIOHAZARD) != null && owner.getQuest(Quests.BIOHAZARD).getStage() == 4)
                                                        {
                                                                if(owner.getInventory().countId(804) > 0)
                                                                {
                                                                        owner.sendMessage("The crate is empty");
                                                                }
                                                                else
                                                                {
                                                                        owner.sendMessage("You search the crate");
                                                                        World.getDelayedEventHandler().add(new SingleEvent(owner, 2000)
                                                                        {
                                                                                public void action()
                                                                                {

                                                                                        owner.sendMessage("and find Elena's distillator");
                                                                                        owner.incQuestCompletionStage(Quests.BIOHAZARD);
                                                                                        owner.getInventory().add(804, 1);
                                                                                        owner.sendInventory();
                                                                                }
                                                                        });
                                                                }
                                                        }
                                                        else if(owner.getQuest(Quests.BIOHAZARD) != null && owner.getQuest(Quests.BIOHAZARD).getStage() >= 5)
                                                        {
                                                                if(owner.getInventory().countId(804) > 0)
                                                                {
                                                                        owner.sendMessage("The crate is empty");
                                                                }
                                                                else
                                                                {
                                                                        owner.sendMessage("You search the crate");
                                                                        World.getDelayedEventHandler().add(new SingleEvent(owner, 2000)
                                                                        {
                                                                                public void action()
                                                                                {

                                                                                        owner.sendMessage("and find Elena's distillator");
                                                                                        owner.getInventory().add(804, 1);
                                                                                        owner.sendInventory();
                                                                                }
                                                                        });
                                                                }
                                                        }
                                                        else
                                                        {
                                                                owner.sendMessage("The crate is empty");
                                                                return;
                                                        }
                                                break;
                                                
                                                //Scarface Pete's Chest
                                                //action: Search
                                                case 265:
                                                        if(owner.getQuest(Quests.HEROS_QUEST) != null && owner.getQuest(Quests.HEROS_QUEST).getStage() == 4  && owner.getQuest(Quests.JOIN_BLACKARM_GANG).finished())
                                                        {
                                                                if(owner.getInventory().countId(585) >= 1)
                                                                {
                                                                        owner.sendMessage("You find nothing");
                                                                }
                                                                else
                                                                {
                                                                        owner.setBusy(true);
                                                                        owner.sendMessage("You find two candlesticks in the chest");
                                                                        World.getDelayedEventHandler().add(new SingleEvent(owner, 2000)
                                                                        {
                                                                                public void action()
                                                                                {
                                                                                        owner.sendMessage("So that will be one for you");
                                                                                        World.getDelayedEventHandler().add(new SingleEvent(owner, 2000)
                                                                                        {
                                                                                                public void action()
                                                                                                {
                                                                                                        owner.sendMessage("And one to the person who killed grip for you");
                                                                                                        owner.getInventory().add(585, 1);
                                                                                                        owner.getInventory().add(585, 1);
                                                                                                        owner.sendInventory();
                                                                                                        owner.setBusy(false);
                                                                                                }
                                                                                        });
                                                                                }
                                                                        });
                                                                }
                                                        }
                                                        else
                                                        {
                                                                owner.sendMessage("You find nothing");
                                                                return;
                                                        }
                                                break;
                                                
                                                //Bird feed Cupboard
                                                //action: Search
                                                case 500:
                                                        if(owner.getQuest(Quests.BIOHAZARD) != null && owner.getQuest(Quests.BIOHAZARD).getStage() == 2)
                                                        {
                                                                if(owner.getInventory().countId(800) > 0)
                                                                {
                                                                        owner.sendMessage("You find nothing");
                                                                }
                                                                else
                                                                {
                                                                        owner.sendMessage("You find some bird feed");
                                                                        owner.getInventory().add(800, 1);
                                                                        owner.sendInventory();
                                                                }
                                                        }
                                                        else
                                                        {
                                                                owner.sendMessage("You find nothing");
                                                        }
                                                break;
                                                
                                                //Nurse house Cupboard
                                                //action: Search
                                                case 510:
                                                        if(owner.getQuest(Quests.BIOHAZARD) != null && owner.getQuest(Quests.BIOHAZARD).getStage() >= 4)
                                                        {
                                                                if(owner.getInventory().countId(802) > 0)
                                                                {
                                                                        owner.sendMessage("You find nothing");
                                                                }
                                                                else
                                                                {
                                                                        owner.sendMessage("You find a doctors gown");
                                                                        owner.getInventory().add(802, 1);
                                                                        owner.sendInventory();
                                                                }
                                                        }
                                                        else
                                                        {
                                                                owner.sendMessage("You find nothing");
                                                        }
                                                break;
                                                
                                                 //Plaguehouse Barrel
                                                case 456:
                                                        if(owner.getQuest(Quests.PLAGUE_CITY) != null && owner.getQuest(Quests.PLAGUE_CITY).getStage() >= 12)
                                                        {
                                                                if(owner.getInventory().countId(780) > 0)
                                                                {
                                                                        owner.sendMessage("You find nothing");
                                                                }
                                                                else
                                                                {
                                                                        owner.sendMessage("You find a small key");
                                                                        owner.getInventory().add(780, 1);
                                                                        owner.sendInventory();
                                                                }
                                                        }
                                                        else
                                                        {
                                                                owner.sendMessage("You find nothing");
                                                        }
                                                break;
                                                //Gasmask Cupboard
                                                        case 452:
                                                                if(owner.getQuest(Quests.PLAGUE_CITY) != null && owner.getQuest(Quests.PLAGUE_CITY).getStage() >= 2)
                                                                {
                                                                        if(owner.getInventory().countId(766) > 0)
                                                                        {
                                                                                owner.sendMessage("You find nothing");
                                                                                return;
                                                                        }
                                                                        else
                                                                        {
                                                                                owner.sendMessage("You find a spare gasmask");
                                                                                owner.getInventory().add(766, 1);
                                                                                owner.sendInventory();
                                                                        }
                                                                }
                                                                else
                                                                {
                                                                        owner.sendMessage("You find nothing");
                                                                        return;
                                                                }
                                                        break;
                                                        case 409: // Tree Gnome Village chest
                                                                Quest Tree_Gnome_Village = owner.getQuest(Quests.TREE_GNOME_VILLAGE);
                                                                switch (Tree_Gnome_Village.getStage())
                                                                {
                                                                        case 5:
                                                                                if (owner.getInventory().countId(740) > 0)
                                                                                {
                                                                                        owner.sendMessage("You have already looted this chest.");
                                                                                }
                                                                                else
                                                                                {
                                                                                        owner.sendMessage("You search through the chest");
                                                                                        owner.getInventory().add(new InvItem(740, 1));
                                                                                        owner.sendMessage("You find the orb");
                                                                                        owner.sendInventory();
                                                                                }
                                                                        break;
                                                                }
                                                        break;
                                                        
                                                        case 141: //  Draynor cupboard.
                                                                if(owner.getInventory().countId(218) > 0)
                                                                {
                                                                        owner.sendMessage("There is nothing in this cupboard");
                                                                }
                                                                else
                                                                {
                                                                        owner.sendMessage("Inside you find a clove of garlic");
                                                                        owner.getInventory().add(new InvItem(218, 1));
                                                                        owner.sendInventory();
                                                                }
                                                        break;
                                                        
                                                        case 986: //Crates in Goblin Hideout
                                                                owner.setBusy(true);
                                                                owner.sendMessage("you search the crate");
                                                                Npc goblin = World.getNpc(4, 613, 621, 3313, 3324);
                                                                if (goblin != null)
                                                                        goblin.setAggressive(owner);
                                                                World.getDelayedEventHandler().add(new SingleEvent(owner, 1500) {
                                                                        public void action() {
                                                                                owner.sendMessage("but it's empty");
                                                                                owner.setBusy(false);
                                                                        }
                                                                });
                                                        break;
                                                        
                                                        case 987: //Crate with boy in it
                                                                owner.setBusy(true);
                                                                owner.sendMessage("you search the crate");
                                                                Npc goblin2 = World.getNpc(4, 613, 621, 3313, 3324);
                                                                if (goblin2 != null)
                                                                        goblin2.setAggressive(owner);
                                                                Quest dwarfCannon = owner.getQuest(Quests.DWARF_CANNON);
                                                                if(dwarfCannon != null) {
                                                                        if(dwarfCannon.getStage() == 2) {
                                                                                World.getDelayedEventHandler().add(new SingleEvent(owner, 1500) {
                                                                                        public void action() {
                                                                                                owner.sendMessage("inside you see a dwarf child tied up");
                                                                                                World.getDelayedEventHandler().add(new SingleEvent(owner, 1500) {
                                                                                                        public void action() {
                                                                                                                owner.sendMessage("you untie the child");
                                                                                                                final Npc lollk = new Npc(695, 620, 3317, 620, 620, 3317, 3317, true);
                                                                                                                lollk.setRespawn(false);
                                                                                                                World.registerEntity(lollk);
                                                                                                                lollk.blockedBy(owner);
                                                                                                                World.getDelayedEventHandler().add(new DelayedQuestChat(lollk, owner, new String[] {"Thank the heavens, you saved me", "I thought i'd be goblin lunch for sure"}) {
                                                                                                                        public void finished() {
                                                                                                                                World.getDelayedEventHandler().add(new DelayedQuestChat(owner, lollk, new String[] {"Are you ok?"}) {
                                                                                                                                        public void finished() {
                                                                                                                                                World.getDelayedEventHandler().add(new DelayedQuestChat(lollk, owner, new String[] {"I think so, i'd better run off home"}) {
                                                                                                                                                        public void finished() {
                                                                                                                                                                World.getDelayedEventHandler().add(new DelayedQuestChat(owner, lollk, new String[] {"That's right, you get going, I'll catch up"}) {
                                                                                                                                                                        public void finished() {
                                                                                                                                                                                World.getDelayedEventHandler().add(new DelayedQuestChat(lollk, owner, new String[] {"Thanks again brave adventurer"}) {
                                                                                                                                                                                        public void finished() {
                                                                                                                                                                                                World.getDelayedEventHandler().add(new SingleEvent(owner, 1500) {
                                                                                                                                                                                                        public void action() {
                                                                                                                                                                                                                owner.sendMessage("the dwarf child runs off into the caverns");
                                                                                                                                                                                                                lollk.remove();
                                                                                                                                                                                                                owner.incQuestCompletionStage(Quests.DWARF_CANNON);
                                                                                                                                                                                                                owner.setBusy(false);
                                                                                                                                                                                                        }
                                                                                                                                                                                                });
        
                                                                                                                                                                                        }
                                                                                                                                                                                });
                                                                                                                                                                        }
                                                                                                                                                                });
                                                                                                                                                        }
                                                                                                                                                });
                                                                                                                                        }
                                                                                                                                });
                                                                                                                        }
                                                                                                                });
                                                                                                        }
                                                                                                });
                                                                                        }
                                                                                });
                                                                        } else {
                                                                                World.getDelayedEventHandler().add(new SingleEvent(owner, 1500) {
                                                                                        public void action() {
                                                                                                owner.sendMessage("but it's empty");
                                                                                                owner.setBusy(false);
                                                                                        }
                                                                                });
                                                                        }
                                                                } else {
                                                                        World.getDelayedEventHandler().add(new SingleEvent(owner, 1500) {
                                                                                public void action() {
                                                                                        owner.sendMessage("but it's empty");
                                                                                        owner.setBusy(false);
                                                                                }
                                                                        });
                                                                }
                                                        break;
                                                                
                                                        case 237: //Leprechaun Tree
                                                                Quest lostCity = owner.getQuest(Quests.LOST_CITY);
                                                                if (lostCity != null) {
                                                                        if (lostCity.getStage() == 2) {
                                                                                Npc leprechaun = World.getNpc(211, 165, 663, 172, 666);
                                                                                if (leprechaun == null) {
                                                                                        owner.sendMessage("A Leprechaun jumps down from the tree and runs off");
                                                                                        leprechaun = new Npc(211, 172, 663, 172, 172, 663, 663, true);
                                                                                        World.registerEntity(leprechaun, 100000);
                                                                                        leprechaun.setPath(new Path(leprechaun.getX(), leprechaun.getY(), 165, 666));
                                                                                } else
                                                                                        owner.sendMessage("There is nothing in this tree");
                                                                        } else
                                                                                owner.sendMessage("There is nothing in this tree");
                                                                } else
                                                                        owner.sendMessage("There is nothing in this tree");
                                                        break;
                                                        
                                                        case 332: //
                                                                if (owner.getInventory().countId(54) == 0) {
                                                                        Quest q = owner.getQuest(Quests.SHIELD_OF_ARRAV);
                                                                        if (q != null) {
                                                                                if (!q.finished()) {
                                                                                        owner.sendMessage("You find a half of a shield in the chest");
                                                                                        owner.getInventory().add(new InvItem(54, 1));
                                                                                        owner.sendInventory();
                                                                                } else
                                                                                        owner.sendMessage("You find nothing in the chest");
                                                                        } else
                                                                                owner.sendMessage("You find nothing in the chest");
                                                                } else
                                                                        owner.sendMessage("You find nothing in the chest");
                                                        break;
                                                                
                                                        case 85: //Phoenix Gang Shield of Arrav cupboard
                                                                if (owner.getInventory().countId(53) == 0) {
                                                                        Quest q = owner.getQuest(Quests.SHIELD_OF_ARRAV);
                                                                        if (q != null) {
                                                                                if (!q.finished()) {
                                                                                        owner.sendMessage("You find a half of a shield in the cupboard");
                                                                                        owner.getInventory().add(new InvItem(53, 1));
                                                                                        owner.sendInventory();                                                                          
                                                                                } else
                                                                                        owner.sendMessage("You find nothing in the cupboard");
                                                                        } else
                                                                                owner.sendMessage("You find nothing in the cupboard");
                                                                } else
                                                                        owner.sendMessage("You find nothing in the cupboard");
                                                                break;
                                                        case 67: //Varrock Library Bookshelf
                                                                if(owner.getInventory().countId(30) == 0){
                                                                        Quest q13 = owner.getQuest(Quests.SHIELD_OF_ARRAV);
                                                                        if (q13 != null) {
                                                                                if (q13.getStage() == 0) {
                                                                                        World.getDelayedEventHandler().add(new DelayedQuestChat(owner, owner, new String[] {"Aha the shield of Arrav", "That was what I was looking for"}, true) {
                                                                                                public void finished() {
                                                                                                        owner.sendMessage("You take the book from the bookcase");
                                                                                                        owner.getInventory().add(new InvItem(30, 1));
                                                                                                        owner.sendInventory();
                                                                                                }
                                                                                        });
                                                                                } else
                                                                                        owner.sendMessage("A large collection of books");
                                                                        } else
                                                                                owner.sendMessage("A large collection of books");
                                                                } else
                                                                        owner.sendMessage("A large collection of books");
                                                        break;
                                                        
                                                        case 175: //Sir Vyvin's chest
                                                                final Npc sirVyvin = World.getNpc(138, owner.getX() - 5, owner.getX() + 5, owner.getY() - 5, owner.getY() + 5);
                                                                if (sirVyvin != null) {
                                                                        owner.setBusy(true);
                                                                        if (!sirVyvin.isBusy()) {
                                                                                sirVyvin.blockedBy(owner);
                                                                                World.getDelayedEventHandler().add(new DelayedQuestChat(sirVyvin, owner, new String[] {"Hey what are you doing?", "That's my cupboard"}, true) {
                                                                                        public void finished() {
                                                                                                owner.sendMessage("Maybe you need to get someone to distract Sir Vyvin for you");
                                                                                                owner.setBusy(false);
                                                                                                sirVyvin.unblock();
                                                                                        }
                                                                                });
                                                                        } else {
                                                                                if(owner.getInventory().countId(264) > 0) {
                                                                                        World.getDelayedEventHandler().add(new DelayedGenericMessage(owner, new String[] {"You search the cupboard", "It contains mostly junk"}, 2000) {
                                                                                                public void finished() {
                                                                                                        owner.setBusy(false);
                                                                                                }
                                                                                        });
                                                                                } else {
                                                                                        Quest q = owner.getQuest(Quests.THE_KNIGHTS_SWORD);
                                                                                        if(q != null) {
                                                                                                if(q.getStage() == 2) {
                                                                                                        World.getDelayedEventHandler().add(new DelayedGenericMessage(owner, new String[] {"You search the cupboard", "You find a small portrait, which you take"}, 2000) {
                                                                                                                public void finished() {
                                                                                                                        owner.getInventory().add(new InvItem(264, 1));
                                                                                                                        owner.sendInventory();
                                                                                                                        owner.setBusy(false);
                                                                                                                }
                                                                                                        });
                                                                                                } else {
                                                                                                        World.getDelayedEventHandler().add(new DelayedGenericMessage(owner, new String[] {"You search the cupboard", "It contains mostly junk"}, 2000) {
                                                                                                                public void finished() {
                                                                                                                        owner.setBusy(false);
                                                                                                                }
                                                                                                        });
                                                                                                }
                                                                                        } else {
                                                                                                World.getDelayedEventHandler().add(new DelayedGenericMessage(owner, new String[] {"You search the cupboard", "It contains mostly junk"}, 2000) {
                                                                                                        public void finished() {
                                                                                                                owner.setBusy(false);
                                                                                                        }
                                                                                                });
                                                                                        }
                                                                                }
        
                                                                        }
                                                                } else {
                                                                        owner.sendMessage("@red@An error has occured with the Knight's Sword quest");
                                                                        Logger.log(new ErrorLog(owner.getUsernameHash(), owner.getAccount(), owner.getIP(), "Sir Vyvin null with the Knights Sword", DataConversions.getTimeStamp()));
                                                                }
                                                        break;
                                                                
                                                        case 555: // search rock for Volencia Moss
                                                                owner.sendMessage("A small herb plant is growing in the scorched soil");
                                                                Quest qA = owner.getQuest(Quests.JUNGLE_POTION);
                                                                if (qA != null) {
                                                                        if (qA.getStage() == 3 && owner.getInventory().countId(822) == 0)
                                                                                World.registerEntity(new Item(821, 412, 794, 1, owner));
                                                                }
                                                        break;
                                                        
                                                        case 554: // Scorched earth Sito Foil
                                                                owner.sendMessage("A small herb plant is growing in the scorched soil");
                                                                Quest qB = owner.getQuest(Quests.JUNGLE_POTION);
                                                                if (qB != null) {
                                                                        if (qB.getStage() == 2 && owner.getInventory().countId(820) == 0)
                                                                                World.registerEntity(new Item(819, 447, 778, 1, owner));
                                                                }
                                                        break;
                                                
                                                        case 553: // Palm tree for Ardrigal
                                                                owner.sendMessage("You find an herb plant growing at the base of the palm");
                                                                Quest qC = owner.getQuest(Quests.JUNGLE_POTION);
                                                                if (qC != null) {
                                                                        if (qC.getStage() == 1 && owner.getInventory().countId(818) == 0)
                                                                                World.registerEntity(new Item(817, 398, 762, 1, owner));
                                                                }
                                                        break;
                                                        
                                                        case 564:       //Jungle Vine for Snake Weed
                                                                owner.sendMessage("Small amounts of an herb are growing near this vine");
                                                                Quest qD = owner.getQuest(Quests.JUNGLE_POTION);
                                                                if(qD != null) {
                                                                        if (qD.getStage() == 0 && owner.getInventory().countId(816) == 0)
                                                                                World.registerEntity(new Item(815, 471, 794, 1, owner));
                                                                }
                                                        break;
                                                        
                                                        case 17:        //Chest for The Holy Grail quest, later added
                                                                owner.sendMessage("You search the chest, but find nothing");
                                                        break;
                                                        
                                                        case 230: //Chest in Dwarven mine for map piece
                                                                if (owner.getInventory().countId(418) == 0) {
                                                                        Quest q = owner.getQuest(Quests.DRAGON_SLAYER);
                                                                        if (q != null) {
                                                                                if (owner.getQuestCompletionStage(Quests.DRAGON_SLAYER) > 0 && !q.finished()) {
                                                                                        owner.sendMessage("You find a piece of map in the chest");
                                                                                        owner.getInventory().add(new InvItem(418, 1));
                                                                                        owner.sendInventory();
                                                                                } else
                                                                                        owner.sendMessage("You find nothing in the chest");
                                                                        } else
                                                                                owner.sendMessage("You find nothing in the chest");
                                                                } else
                                                                        owner.sendMessage("You find nothing in the chest");
                                                        break;
                                                        
                                                        case 228: //Chest in Melzar's maze for map piece
                                                           if (owner.getInventory().countId(417) == 0) {
                                                                        owner.sendMessage("You find a piece of map in the chest");
                                                                        owner.getInventory().add(new InvItem(417, 1));
                                                                        owner.sendInventory();
                                                                } else
                                                                        owner.sendMessage("You find nothing in the chest");
                                                        break;

                                                        case 255: //Witches House Door Mat
                                                                if (owner.getInventory().countId(538) == 0)
                                                                        owner.sendMessage("You find a key under the mat");
                                                                else
                                                                        owner.sendMessage("You find nothing interesting");
                                                        break;
                                                        
                                                        case 40: // Lumbridge Coffin [QUEST]
                                                                Quest q = owner.getQuest(Quests.THE_RESTLESS_GHOST);
                                                                if (q == null)
                                                                        owner.sendMessage("You search the coffin and find some human remains");
                                                                else if (q.finished())
                                                                        owner.sendMessage("There is a nice and complete skeleton in here");
                                                                else
                                                                        owner.sendMessage("There's a skeleton without a skull in here");
                                                        break;
                                                        
                                                        case 182: // Luthas' Banana Box
                                                                if (owner.getBananas() == 0) {
                                                                        if (owner.rumInKaramjaCrate())
                                                                                owner.sendMessage("The crate is empty except for a bottle of rum.");
                                                                        else
                                                                                owner.sendMessage("The crate is completely empty.");
                                                                } else if (owner.getBananas() < 10)
                                                                        owner.sendMessage("The crate is partially full of bananas.");
                                                                else if (owner.getBananas() == 10)
                                                                        owner.sendMessage("The crate is full of bananas.");
                                                        break;
                                                                
                                                        case 185: //Port Sarim Banana Box
                                                                owner.sendMessage("There are a lot of bananas in the crate.");
                                                                if(owner.rumInSarimCrate()) {
                                                                        World.getDelayedEventHandler().add(new MiniEvent(owner) {
                                                                                public void action() {
                                                                                        owner.sendMessage("You find your bottle of rum amoungst the bananas");
                                                                                        owner.takeRum();
                                                                                        owner.getInventory().add(new InvItem(318,1));
                                                                                        owner.sendInventory();
                                                                                        World.getDelayedEventHandler().add(new MiniEvent(owner) {
                                                                                                public void action() {
                                                                                                        owner.sendMessage("Do you want to take a banana?");
                                                                                                        World.getDelayedEventHandler().add(new MiniEvent(owner) {
                                                                                                                public void action() {
                                                                                                                        String[] options = {"Yes", "No"};
                                                                                                                        owner.setBusy(false);
                                                                                                                        owner.sendMenu(options);
                                                                                                                        owner.setMenuHandler(new MenuHandler(options) {
                                                                                                                                public void handleReply(final int option, final String reply) {
                                                                                                                                        switch(option) {
                                                                                                                                                case 0:
                                                                                                                                                        owner.sendMessage("You take a banana");
                                                                                                                                                        owner.getInventory().add(new InvItem(249, 1));
                                                                                                                                                        owner.sendInventory();
                                                                                                                                                break;
                                                                                                                                        }
                                                                                                                                }
                                                                                                                        });
                                                                                                                        owner.sendMenu(options);
                                                                                                                }
                                                                                                        });
                                                                                                }
                                                                                        });
                                                                                }
                                                                        });
                                                                } else {
                                                                        World.getDelayedEventHandler().add(new MiniEvent(owner) {
                                                                                public void action() {
                                                                                        owner.sendMessage("Do you want to take a banana?");
                                                                                        World.getDelayedEventHandler().add(new MiniEvent(owner) {
                                                                                                public void action() {
                                                                                                        String[] options = {"Yes", "No"};
                                                                                                        owner.setBusy(false);
                                                                                                        owner.sendMenu(options);
                                                                                                        owner.setMenuHandler(new MenuHandler(options) {
                                                                                                                public void handleReply(final int option, final String reply) {
                                                                                                                        switch (option) {
                                                                                                                                case 0:
                                                                                                                                        owner.sendMessage("You take a banana");
                                                                                                                                        owner.getInventory().add(new InvItem(249, 1));
                                                                                                                                        owner.sendInventory();
                                                                                                                                break;
                                                                                                                        }
                                                                                                                }
                                                                                                        });
                                                                                                        owner.sendMenu(options);
                                                                                                }
                                                                                        });
                                                                                }
                                                                        });
                                                                }
                                                        break;
                                                                
                                                        case 613:
                                                                owner.setBusy(true);
                                                                owner.sendMessage("You search for a way over the cart");
                                                                World.getDelayedEventHandler().add(new ShortEvent(owner) {
                                                                        public void action() {
                                                                                owner.sendMessage("You climb across");
                                                                                if (owner.getX() < 384)
                                                                                        owner.teleport(386, 851, false);
                                                                                else
                                                                                        owner.teleport(383, 851, false);
                                                                                owner.setBusy(false);
                                                                        }
                                                                });
                                                        break;
                                                        
                                                        case 77:
                                                                if (owner.getQuestCompletionStage(Quests.DEMON_SLAYER) < 1)
                                                                        owner.sendMessage("I can see a key but can't quite reach it...");
                                                                else {
                                                                        owner.setBusy(true);
                                                                        owner.sendMessage("This is the drainpipe");
                                                                        World.getDelayedEventHandler().add(new SingleEvent(owner, 1250) {
                                                                                public void action() {
                                                                                        owner.sendMessage("Running from the kitchen sink to the sewer");
                                                                                        World.getDelayedEventHandler().add(new SingleEvent(owner, 1250) {
                                                                                                public void action() {
                                                                                                        owner.sendMessage("I can see a key just inside the drain");
                                                                                                        World.getDelayedEventHandler().add(new SingleEvent(owner, 1250) {
                                                                                                                public void action() {
                                                                                                                        owner.sendMessage("That must be the key that Sir Prysin dropped");
                                                                                                                        World.getDelayedEventHandler().add(new SingleEvent(owner, 1250) {
                                                                                                                                public void action() {
                                                                                                                                        owner.sendMessage("I don't seem to be able to quite reach it");
                                                                                                                                        World.getDelayedEventHandler().add(new SingleEvent(owner, 1250) {
                                                                                                                                                public void action() {
                                                                                                                                                        owner.sendMessage("It is stuck part way down");
                                                                                                                                                        World.getDelayedEventHandler().add(new SingleEvent(owner, 1250) {
                                                                                                                                                                public void action() {
                                                                                                                                                                        owner.sendMessage("I wonder if I can dislodge it somehow");
                                                                                                                                                                        World.getDelayedEventHandler().add(new SingleEvent(owner, 1250) {
                                                                                                                                                                                public void action() {
                                                                                                                                                                                        owner.sendMessage("And knock it down into the sewers");
                                                                                                                                                                                        owner.setBusy(false);
                                                                                                                                                                                }
                                                                                                                                                                        });
                                                                                                                                                                }
                                                                                                                                                        });
                                                                                                                                                }
                                                                                                                                        });
                                                                                                                                }
                                                                                                                        });
                                                                                                                }
                                                                                                        });
                                                                                                }
                                                                                        });
                                                                                }
                                                                        });
                                                                }
                                                        break;
                                                                
                                                        case 134:
                                                                owner.sendMessage("I'm not looking through that with my hands");
                                                        break;
                                                                
                                                        case 86: // Draynor pirhana fountain
                                                                owner.setBusy(true);
                                                                if(!owner.killedFish()) {
                                                                        World.getDelayedEventHandler().add(new DelayedQuestChat(owner, owner, new String[] {"There seems to be a pressure gauge in here", "There are a lot of Pirhanas in there though", "I can't get the gauge out"}, true) {
                                                                                public void finished() {
                                                                                        owner.setBusy(false);
                                                                                }
                                                                        });
                                                                } else {
                                                                        if (owner.getInventory().countId(175) > 0) {
                                                                                owner.sendMessage("It's full of dead fish");
                                                                                owner.setBusy(false);
                                                                        } else {
                                                                                World.getDelayedEventHandler().add(new DelayedQuestChat(owner, owner, new String[] {"There seems to be a pressure gauge in here", "There are also some dead fish"}, true) {
                                                                                        public void finished() {
                                                                                                owner.sendMessage("You get the pressure gauge from the fountain");
                                                                                                owner.getInventory().add(new InvItem(175, 1));
                                                                                                owner.sendInventory();
                                                                                                owner.setBusy(false);
                                                                                        }
                                                                                });
                                                                        }
                                                                }
                                                        break;
                                                                
                                                        default:
                                                                owner.sendMessage("Nothing interesting happens");
                                                }
                                        }
                                        
                                        private void handleJumpOff() {
                                                owner.sendMessage("You are washed off of the waterfall by the strong currents.");
                                                owner.teleport(655, 508, false);
                                        }
                                        
                                        private void handleJumpToNext() {
                                                owner.sendMessage("You are washed off of the waterfall by the strong currents.");
                                                owner.teleport(655, 508, false);
                                        }

                                        private void handleBoard() {
                                                switch (object.getID()) {
                                                        case 242:
                                                        case 243:
                                                                owner.setBusy(true);
                                                                owner.sendMessage("You board the ship");
                                                                World.getDelayedEventHandler().add(new SingleEvent(owner, 2000) {
                                                                        public void action() {
                                                                                owner.sendMessage("The ship arrives at Port Sarim");
                                                                                owner.teleport(263, 660);
                                                                                owner.setBusy(false);
                                                                        }
                                                                });
                                                        break;
                                                        
                                                        case 224:
                                                        case 225:
                                                                // 280, 3494 patched, empty.
                                                                // 258, 3473 unpatched, empty. New Coords 259 3472
                                                                // 258, 3494 patched, empty.
                                                                // 280, 3473 unpatched, empty.
                                                                // 281, 3473 Ladder up, unpatched.
                                                                
                                                                Quest Dragon_Slayer = owner.getQuest(Quests.DRAGON_SLAYER);
                                                                
                                                                if (Dragon_Slayer != null)
                                                                {
                                                                        if (Dragon_Slayer.finished())
                                                                        {
                                                                                owner.sendMessage("You board the ship");
                                                                                owner.setCrandor(false);
                                                                                owner.teleport(258, 3494, false);
                                                                                return;
                                                                        }

                                                                        switch (Dragon_Slayer.getStage())
                                                                        {
                                                                                case 2:
                                                                                        owner.sendMessage("You board the ship");
                                                                                        owner.setCrandor(false);
                                                                                        
                                                                                        if (owner.ladyFixed())
                                                                                                owner.teleport(280, 3494);
                                                                                        else
                                                                                                owner.teleport(258, 3473);
                                                                                break;

                                                                                case 3:
                                                                                        owner.setCrandor(false);
                                                                                        owner.sendMessage("You board the ship");
                                                                                        if (owner.ladyFixed())
                                                                                                owner.teleport(258, 3494);
                                                                                        else
                                                                                                owner.teleport(280, 3473);
                                                                                        
                                                                                        owner.teleport(258, owner.ladyFixed() ? 3494 : 3473, false);
                                                                                break;
                                                                                
                                                                                default:
                                                                                        owner.sendMessage("You must talk to the owner about this.");    
                                                                        }
                                                                } 
                                                                else
                                                                {
                                                                        owner.sendMessage("You must talk to the owner about this.");
                                                                }
                                                        break;  
                                                        
                                                        /*
                                                         *  Boats
                                                         */
                                                        case 233:
                                                        case 234:
                                                                owner.setCrandor(true);
                                                                owner.sendMessage("You board the ship");
                                                                if(owner.ladyFixed())
                                                                        owner.teleport(258, 3494);
                                                                else
                                                                        owner.teleport(280, 3473);
                                                        break;
                                                                
                                                        case 464: // log raft (waterfall)
                                                                owner.sendMessage("You board the raft...");
                                                                owner.teleport(662,463,false);
                                                        break;
                                                                
                                                        default:
                                                                owner.sendMessage("You must talk to the owner about this.");                                            
                                                }       
                                        }
                                        
                                        private void handleRechargeAt() {
                                                switch(object.getID()) {
                                                        case 625:
                                                                owner.teleport(608,3525);
                                                        break;
                                                        
                                                        default:
                                                                owner.sendMessage("You recharge at the altar.");
                                                                owner.sendSound("recharge", false);
                                                                int maxPray = object.getID() == 200 ? owner.getMaxStat(5) + 2 : owner.getMaxStat(5);
                                                                if (owner.getCurStat(5) < maxPray)
                                                                        owner.setCurStat(5, maxPray);
                                                                owner.sendStat(5);
                                                        break;
                                                }
                                        }
                                        
                                        private void handlePick() {
                                                switch (object.getID()) {
                                                        case 72: // Wheat
                                                                pickLoop(29, "You get some grain");
                                                        break;
                                                        
                                                        case 191: // Potatos
                                                                pickLoop(348, "You pick a potato");
                                                        break;
                                                        
                                                        case 313: // Flax
                                                                pickLoop(675, "You uproot a flax plant");
                                                        break;
                                                        
                                                        case 430: // Pineapple
                                                                pickLoop(748, "You pull a pineapple off the tree");
                                                        break;
                                                        
                                                        case 183: // Banana
                                                                pickLoop(249, "You pull a banana off the tree");
                                                        break;
                                                        
                                                        default:
                                                                owner.sendMessage("Nothing interesting happens");
                                                }
                                        }
                                        
                                        private void pickLoop(final int item, final String message) {
                                                if (!owner.getInventory().full()) {
                                                        World.getDelayedEventHandler().add(new SingleEvent(owner, 500) {
                                                                public void action() {
                                                                        if (!owner.getInventory().full()) {
                                                                                owner.setBusy(true);
                                                                                owner.sendMessage(message);
                                                                                owner.getInventory().add(new InvItem(item, 1));
                                                                                owner.sendInventory();
                                                                                owner.sendSound("potato", false);
                                                                                pickLoop(item, message);
                                                                        } else
                                                                                owner.setBusy(false);
                                                                }
                                                        });
                                                } else
                                                        owner.setBusy(false);
                                        }
                                        
                                        private void handleApproach() {
                                                switch (object.getID()) {
                                                        case 88:
                                                                owner.sendMessage("The tree seems to lash out at you!");
                                                                owner.setBusy(true);
                                                                World.getDelayedEventHandler().add(new ShortEvent(owner) {
                                                                        public void action() {
                                                                                owner.setBusy(false);
                                                                                owner.sendMessage("You are badly scratched by the tree");
                                                                                int damage = owner.getHits() / 5;
                                                                                owner.setHits(owner.getHits() - damage);
                                                                                owner.setLastDamage(damage);
                                                                                ArrayList<Player> playersToInform = new ArrayList<Player>();
                                                                                playersToInform.addAll(owner.getViewArea().getPlayersInView());
                                                                                for (Player p : playersToInform)
                                                                                        p.informOfModifiedHits(owner);
                                                                                owner.sendStat(3);
                                                                        }
                                                                });
                                                        break;
                                                        
                                                        default:
                                                                owner.sendMessage("Nothing interesting happens");
                                                }
                                        }
                                        
                                        private void handleClose() {
                                                switch(object.getID()) {
                                                        
                                                        case 85:
                                                                owner.sendMessage("You close the cupboard");
                                                                replaceGameObject(84, false);
                                                        break;
                                                        
                                                        case 332:
                                                                owner.sendMessage("You close the chest");
                                                                replaceGameObject(333, false);
                                                        break;
                                                        
                                                        case 228: 
                                                                owner.sendMessage("You close the chest");
                                                                replaceGameObject(229, false);
                                                        break;
                                                        
                                                        case 230: //Dwarven mine map piece
                                                                owner.sendMessage("You close the chest");
                                                                replaceGameObject(231, false);
                                                        break;
                                                        
                                                        case 265:
                                                                owner.sendMessage("You close the chest");
                                                                replaceGameObject(266, false);
                                                        break;
                                                        
                                                        case 136:       //Draynor Manor Coffin
                                                                owner.sendMessage("You close the coffin");
                                                                replaceGameObject(135, false);
                                                        break;
                                                        
                                                        case 17:        //Chest for The Holy Grail, to add
                                                                replaceGameObject(18, false);
                                                                owner.sendMessage("You close the chest");
                                                        break;
                                                        
                                                        case 40: //Lumbridge Graveyard Coffin [QUEST]
                                                                owner.sendMessage("You close the coffin");
                                                                replaceGameObject(39, false);
                                                        break;
                                                        
                                                        case 58:
                                                                replaceGameObject(57, false);
                                                        break;
                                                        
                                                        case 63:
                                                                replaceGameObject(64, false);
                                                        break;
                                                        
                                                        case 79:
                                                                replaceGameObject(78, false);
                                                        break;
                                                        
                                                        case 59:
                                                                replaceGameObject(60, false);
                                                        break;
                                                        
                                                        default:
                                                                owner.sendMessage("Nothing interesting happens.");
                                                }
                                        }
                                        
                                        private void handleOpen() {
                                                switch (object.getID()) {
                                                
                                                
                                                
                                                //Mourner HQ upstairs Gate
                                                //action: Open
                                                case 504:
                                                        if (owner.getX() == 630) 
                                                        {
                                                                doGate();
                                                                owner.teleport(631, 1514, false);
                                                                owner.sendMessage("You go through the gate");
                                                } 
                                                        else
                                                        {
                                                                owner.sendMessage("The gate is locked");
                                                        }
                                                break;
                                                
                                                case 1220:
                                                        /*
                                                         *      Falador Custom Area
                                                         */
                                                        if (object.getX() == 271 && object.getY() == 555)
                                                        {
                                                                doGate();
                                                                owner.sendMessage("You go through the gate");
                                                                        if (owner.getX() == 272)
                                                                                owner.teleport(271, object.getY(), false);
                                                                        else
                                                                                owner.teleport(object.getX() + 1, object.getY(), false);
                                                                return;
                                                        }
                                                        
                                                        if (object.getX() == 269 && object.getY() == 542)
                                                        {
                                                                owner.sendMessage("You go through the gate");
                                                                doGate();
                                                                if (owner.getY() == 542)
                                                                        owner.teleport(269, 543, false);
                                                                else
                                                                        owner.teleport(269, 542, false);
                                                                
                                                                return;
                                                        }
                                                        
                                                        if (object.getX() == 265 && object.getY() == 3653)
                                                        {
                                                                if (owner.getX() <= 264)
                                                                {
                                                                        doGate();
                                                                        owner.teleport(265, 3653);
                                                                }
                                                                else 
                                                                {
                                                                        doGate();
                                                                        owner.teleport(264, 3653);
                                                                }
                                                                
                                                                return;
                                                        }
                                                        
                                                        if (owner.getX() <=219 && owner.getY() >= 3673)
                                                        {
                                                                doGate();
                                                                owner.sendMessage("You go through the gate");
                                                                owner.teleport(219, 3672, false);
                                                        } else 
                                                        if (owner.getX() <=219 && owner.getY() <= 3672)
                                                        {
                                                                doGate();
                                                                owner.sendMessage("You go through the gate");
                                                                owner.teleport(219, 3673, false);
                                                        }
                                                break;
                                                
                                                //West Ardougne wall gateway
                                                //action: Open
                                                case 450:
                                                        if(owner.getQuest(Quests.BIOHAZARD) != null && owner.getQuest(Quests.BIOHAZARD).finished())
                                                        {
                                                                if (owner.getX() == 622) 
                                                                {
                                                                        doGate();
                                                                        owner.teleport(624, 588, false);
                                                                        owner.sendMessage("You go through the gates");
                                                                } 
                                                                else
                                                                {
                                                                        doGate();
                                                                        owner.teleport(622, 589, false);
                                                                        owner.sendMessage("You go through the gates");
                                                                }
                                                        }
                                                        else
                                                        {
                                                                owner.sendMessage("The gates are locked");
                                                        }
                                                break;
                                                
                                                
                                                
                                                //elena gate
                                                        case 457:
                                                        if (owner.getY() == 3448) 
                                                                {
                                                                        doGate();
                                                                        owner.teleport(637, 3447, false);
                                                        }
                                                                else
                                                                {
                                                                        if(owner.getInventory().countId(780) > 0)
                                                                        {
                                                                                owner.sendMessage("The gate is locked");
                                                                                owner.sendMessage("Maybe you should try using the little key on it");
                                                                        }
                                                                        else
                                                                        {
                                                                                owner.setBusy(true);
                                                                                World.getDelayedEventHandler().add(new SingleEvent(owner, 0)
                                                                                {
                                                                                        public void action()
                                                                                        {
                                                                                                Npc elena = World.getNpc(465, 636, 639, 3448, 3450);
                                                                                                if(elena != null) 
                                                                                                {
                                                                                                        elena.blockedBy(owner);
                                                                                                        for (Player informee : elena.getViewArea().getPlayersInView())
                                                                                                        informee.informOfNpcMessage(new ChatMessage(elena, "Hey get me out of here please", owner));                                                                            
                                                                                                }
                                                                                                World.getDelayedEventHandler().add(new DelayedQuestChat(owner, elena, new String[] {"I would do but I don't have a key"})
                                                                                                {
                                                                                                        public void finished()
                                                                                                        {
                                                                                                                World.getDelayedEventHandler().add(new DelayedQuestChat(elena, owner, new String[] {"I think there may be one around here somewhere", "I'm sure I saw them stashing it somewhere"})
                                                                                                                {
                                                                                                                        public void finished()
                                                                                                                        {
                                                                                                                                World.getDelayedEventHandler().add(new SingleEvent(owner, 2000)
                                                                                                                                {
                                                                                                                                        public void action()
                                                                                                                                        {
                                                                                                                                                final String[] options107 = {"Have you caught the plague?", "Ok I will look for it"};
                                                                                                                                                owner.setBusy(false);
                                                                                                                                                owner.sendMenu(options107);
                                                                                                                                                owner.setMenuHandler(new MenuHandler(options107) 
                                                                                                                                                {
                                                                                                                                                        public void handleReply(final int option, final String reply)
                                                                                                                                                        {
                                                                                                                                                                owner.setBusy(true);
                                                                                                                                                                for(Player informee : owner.getViewArea().getPlayersInView())
                                                                                                                                                                {
                                                                                                                                                                        informee.informOfChatMessage(new ChatMessage(owner, reply, elena));
                                                                                                                                                                }
                                                                                                                                                                switch(option) 
                                                                                                                                                                {
                                                                                                                                                                        case 0:
                                                                                                                                                                                World.getDelayedEventHandler().add(new DelayedQuestChat(elena, owner, new String[] {"No, I have none of the symptoms"})
                                                                                                                                                                                {
                                                                                                                                                                                        public void finished()
                                                                                                                                                                                        {
                                                                                                                                                                                                World.getDelayedEventHandler().add(new DelayedQuestChat(owner, elena, new String[] {"Strange I was told this house was plague infected"})
                                                                                                                                                                                                {
                                                                                                                                                                                                        public void finished()
                                                                                                                                                                                                        {
                                                                                                                                                                                                                World.getDelayedEventHandler().add(new DelayedQuestChat(elena, owner, new String[] {"I suppose that was a cover up by the kidnappers"})
                                                                                                                                                                                                                {
                                                                                                                                                                                                                        public void finished()
                                                                                                                                                                                                                        {
                                                                                                                                                                                                                                owner.setBusy(false);
                                                                                                                                                                                                                                elena.unblock();
                                                                                                                                                                                                                        }
                                                                                                                                                                                                                });     
                                                                                                                                                                                                        }
                                                                                                                                                                                                });
                                                                                                                                                                                        }
                                                                                                                                                                                });
                                                                                                                                                                        break;
                                                                                                                                                                        case 1:
                                                                                                                                                                                owner.setBusy(false);
                                                                                                                                                                                elena.unblock();
                                                                                                                                                                        break;
                                                                                                                                                                }
                                                                                                                                                        }
                                                                                                                                                });
                                                                                                                                        }
                                                                                                                                });
                                                                                                                        }
                                                                                                                });
                                                                                                        }
                                                                                                });
                                                                                        }
                                                                                });
                                                                        }
                                                                }
                                                        break;
                                                        case 358: // Gate for Fishing Contest
                                                                if (owner.getX() == 565 && owner.getY() == 492 || owner.getX() == 565 && owner.getY() == 493)
                                                                {
                                                                        owner.sendMessage("You go through the gate.");
                                                                        owner.teleport(564, 492, false);
                                                                        return;
                                                                }
                                                                
                                                                Npc Morris = World.getNpc(349, 564, 564, 491, 491);
                                                                if (Morris != null) 
                                                                {
                                                                        for(Player informee : Morris.getViewArea().getPlayersInView()) 
                                                                        {
                                                                                informee.informOfNpcMessage(new ChatMessage(Morris, "Competition pass please!", owner));
                                                                        }

                                                                        World.getDelayedEventHandler().add(new ShortEvent(owner) 
                                                                        {
                                                                                public void action()
                                                                                {
                                                                                        if(owner.getInventory().countId(719) > 0)
                                                                                        {
                                                                                                World.getDelayedEventHandler().add(new ShortEvent(owner) 
                                                                                                {
                                                                                                        public void action()
                                                                                                        {
                                                                                                                owner.sendMessage("You show Morris your pass");
                                                                                                        }
                                                                                                });
                                                                                                World.getDelayedEventHandler().add(new ShortEvent(owner) 
                                                                                                {
                                                                                                        public void action()
                                                                                                        {
                                                                                                                Npc Morris = World.getNpc(349, 564, 564, 491, 491);
                                                                                                                if (Morris != null) 
                                                                                                                {
                                                                                                                        for(Player informee : Morris.getViewArea().getPlayersInView()) 
                                                                                                                        {
                                                                                                                                informee.informOfNpcMessage(new ChatMessage(Morris, "Go on through", owner));
                                                                                                                                World.getDelayedEventHandler().add(new ShortEvent(owner) 
                                                                                                                                {
                                                                                                                                        public void action()
                                                                                                                                        {
                                                                                                                                                owner.teleport(565, 492, false);
                                                                                                                                                owner.sendMessage("You go through the gate");
                                                                                                                                        }
                                                                                                                                });
                                                                                                                        }
                                                                                                                }                                                                                                       
                                                                                                        }
                                                                                                });
                                                                                        }
                                                                                        else
                                                                                        {

                                                                                                World.getDelayedEventHandler().add(new DelayedQuestChat(owner, owner, new String[] {"Sorry, I don't have a competition pass"}, true) 
                                                                                                {
                                                                                                        public void finished()
                                                                                                        {
                                                                                                                owner.sendMessage("You must be on the Fishing Contest quest to enter this area.");
                                                                                                                return;
                                                                                                        }
                                                                                                });
                                                                                        }
                                                                                }
                                                                        });
                                                                }       
                                                        break;
                                                        
                                                        //Grip's drinks cupboard
                                                        //action: Open
                                                        case 263:
                                                                if(owner.getQuest(Quests.HEROS_QUEST) == null || owner.getQuest(Quests.HEROS_QUEST) != null && owner.getQuest(Quests.HEROS_QUEST).finished() || owner.getQuest(Quests.JOIN_PHOENIX_GANG) != null && owner.getQuest(Quests.JOIN_PHOENIX_GANG).finished())
                                                                {
                                                                        owner.sendMessage("You shouldn't be in here");
                                                                        return;
                                                                }
                                                                else
                                                                {
                                                                        owner.setBusy(true);
                                                                        Npc grip = World.getNpc(259, 459, 469, 672, 680);
                                                                        Npc guard = World.getNpc(258, 459, 469, 672, 680);
                                                                        if(owner.getQuest(Quests.HEROS_QUEST).getStage() == 4 )
                                                                        {       
                                                                                guard.blockedBy(owner);
                                                                                if(guard != null) 
                                                                                {
                                                                                        for (Player informee : guard.getViewArea().getPlayersInView())
                                                                                        informee.informOfNpcMessage(new ChatMessage(guard, "I don't think Mr. Grip will like you opening that up", owner));                                                                             
                                                                                }
                                                                                World.getDelayedEventHandler().add(new DelayedQuestChat(guard, owner, new String[] {"That's his drinks cabinet"})
                                                                                {
                                                                                        public void finished()
                                                                                        {
                                                                                                World.getDelayedEventHandler().add(new SingleEvent(owner, 2000)
                                                                                                {
                                                                                                        public void action()
                                                                                                        {
                                                                                                                final String[] options107 = {"He won't notice me having a quick look", "Ok I'll leave it"};
                                                                                                                owner.setBusy(false);
                                                                                                                owner.sendMenu(options107);
                                                                                                                owner.setMenuHandler(new MenuHandler(options107) 
                                                                                                                {
                                                                                                                        public void handleReply(final int option, final String reply)
                                                                                                                        {
                                                                                                                                owner.setBusy(true);
                                                                                                                                for(Player informee : owner.getViewArea().getPlayersInView())
                                                                                                                                {
                                                                                                                                        informee.informOfChatMessage(new ChatMessage(owner, reply, guard));
                                                                                                                                }
                                                                                                                                switch(option) 
                                                                                                                                {
                                                                                                                                        case 0:
                                                                                                                                                Npc grip2 = new Npc(259, 463, 673, 462, 464, 672, 675, true);
                                                                                                                                                World.registerEntity(grip2, 100000);
                                                                                                                                                World.unregisterEntity(grip);
                                                                                                                                                grip2.setRespawn(false);
                                                                                                                                                World.getDelayedEventHandler().add(new SingleEvent(owner, 1000)
                                                                                                                                                {
                                                                                                                                                        public void action()
                                                                                                                                                        {
                                                                                                                                                                if(grip2 != null) 
                                                                                                                                                                {
                                                                                                                                                                        for (Player informee : grip.getViewArea().getPlayersInView())
                                                                                                                                                                        informee.informOfNpcMessage(new ChatMessage(grip2, "Hey what are you doing there", owner));                                                                             
                                                                                                                                                                }
                                                                                                                                                                World.getDelayedEventHandler().add(new DelayedQuestChat(grip2, owner, new String[] {"That's my drinks cabinet get away from it"})
                                                                                                                                                                {
                                                                                                                                                                        public void finished()
                                                                                                                                                                        {
                                                                                                                                                                                owner.setBusy(false);
                                                                                                                                                                                grip2.unblock();
                                                                                                                                                                        }
                                                                                                                                                                });
                                                                                                                                                        }
                                                                                                                                                });
                                                                                                                                        break;
                                                                                                                                        case 1:
                                                                                                                                                owner.setBusy(false);
                                                                                                                                                guard.unblock();
                                                                                                                                        break;
                                                                                                                                }
                                                                                                                        }
                                                                                                                });
                                                                                                        }
                                                                                                });
                                                                                        }
                                                                                });
                                                                        }
                                                                        else
                                                                        {
                                                                                owner.sendMessage("You shouldn't be here");
                                                                                owner.setBusy(false);
                                                                        }
                                                                }
                                                        break;
                                                        
                                                        case 84:
                                                                owner.sendMessage("You open the cupboard");
                                                                replaceGameObject(85, false);
                                                        break;
                                                        
                                                        case 266:
                                                                owner.sendMessage("You open the chest");
                                                                replaceGameObject(265, false);
                                                        break;
                                                                
                                                        case 333: // Phoenix Gang Shield Chest
                                                                owner.sendMessage("You open the chest");
                                                                replaceGameObject(332, false);
                                                        break;
                                                        
                                                        case 231: // Dwarven mine map piece
                                                                owner.sendMessage("You open the chest");
                                                                replaceGameObject(230, false);
                                                        break;
                                                                
                                                        case 229: // Melzar's maze chest
                                                                owner.sendMessage("You open the chest");
                                                                replaceGameObject(228, false);
                                                        break;
                                                                
                                                        case 135:
                                                                owner.sendMessage("You open the coffin");
                                                                replaceGameObject(136, false);
                                                        break;
                                                                
                                                        case 18: //Chest for the Holy Grail quest, to add
                                                                replaceGameObject(17, false);
                                                                owner.sendMessage("You open the chest");
                                                        break;
                                                        
                                                        case 39: //Lumbridge Coffin [QUEST]
                                                                owner.sendMessage("You open the coffin");
                                                                replaceGameObject(40, false);
                                                        break;
                                                                
                                                        case 471: //Waterfall Door
                                                                owner.sendMessage("You attempt to open the door...");
                                                                World.getDelayedEventHandler().add(new ShortEvent(owner) {
                                                                        public void action() {
                                                                                if (owner.getY() == 3302)
                                                                                        owner.teleport(owner.getX(), owner.getY() + 1, false);                                  
                                                                                else
                                                                                        owner.teleport(owner.getX(), owner.getY() - 1, false);
                                                                                owner.sendMessage("The door opens and you walk through.");
                                                                        }
                                                                });
                                                        break;
                                                                
                                                        case 187: // One-Eye Hector's Chest
                                                                owner.sendMessage("The chest is locked");
                                                        break;
                                                                
                                                        case 57:                                                                
                                                                replaceGameObject(58, true);
                                                        break;
                                                                
                                                        case 64:
                                                                replaceGameObject(63, true);
                                                        break;
                                                                
                                                        case 78:
                                                                replaceGameObject(79, true);
                                                        break;
                                                                
                                                        case 60:
                                                                replaceGameObject(59, true);
                                                        break;
                                                                
                                                        case 137: //Members Gate By Doric

                                                                        doGate();
                                                                        if (owner.getX() < 342)
                                                                                owner.teleport(342, 487, false);
                                                                        else
                                                                                owner.teleport(341, 487, false);
                                                        break;
                                                        
                                                        case 138: //Members Gate By Crafting Guild

                                                                        doGate();
                                                                        if (owner.getY() < 581)
                                                                                owner.teleport(343, 581, false);
                                                                        else
                                                                                owner.teleport(343, 580, false);
                                                        break;
                                                        
                                                        case 180: // Al-Kharid Gate
                                                        Quest q = owner.getQuest(Quests.PRINCE_ALI_RESCUE);
                                                        if (q != null && q.finished())
                                                        {
                                                                doGate();
                                                                if (owner.getX() < 92)
                                                                        owner.teleport(92, 649, false);
                                                                else
                                                                        owner.teleport(91, 649, false);
                                                        }
                                                        else
                                                        {
                                                                final Npc Guard = World.getNpc(161, 92, 94, 647, 652);
                                                                if(Guard != null)
                                                                {
                                                                        for (Player informee : Guard.getViewArea().getPlayersInView())
                                                                        {
                                                                                informee.informOfNpcMessage(new ChatMessage(Guard, "You must pay 10 gold to use this gate!", owner));
                                                                        owner.setBusy(true);
                                                                        World.getDelayedEventHandler().add(new ShortEvent(player) {
                                                                                public void action() {
                                                                                        owner.setBusy(false);
                                                                                        String[] options = new String[]{"No thank you", "Ok, I'll pay"};
                                                                                        owner.setMenuHandler(new MenuHandler(options) {
                                                                                                public void handleReply(final int option, final String reply) {
                                                                                                        if(owner.isBusy()) {
                                                                                                                return;
                                                                                                        }
                                                                                                        owner.informOfChatMessage(new ChatMessage(owner, reply, Guard));
                                                                                                        owner.setBusy(true);
                                                                                                        World.getDelayedEventHandler().add(new ShortEvent(owner) {
                                                                                                                public void action() {
                                                                                                                        owner.setBusy(false);
                                                                                                                        if(option == 1) {
                                                                                                                                if(owner.getInventory().remove(10, 10) > -1) {
                                                                                                                                        owner.sendMessage("You pay 10 gold.");
                                                                                                                                        owner.sendInventory();
                                                                                                                                        doGate();
                                                                                                                                        if (owner.getX() < 92)
                                                                                                                                                owner.teleport(92, 649, false);
                                                                                                                                        else
                                                                                                                                                owner.teleport(91, 649, false);
                                                                                                                                        Guard.unblock();
                                                                                                                                }
                                                                                                                                else {
                                                                                                                                        owner.informOfChatMessage(new ChatMessage(owner, "I don't have enough gold to pay you", Guard));
                                                                                                                                        owner.setBusy(true);
                                                                                                                                        World.getDelayedEventHandler().add(new ShortEvent(owner) {
                                                                                                                                                public void action() {
                                                                                                                                                        owner.setBusy(false);
                                                                                                                                                        owner.informOfNpcMessage(new ChatMessage(Guard, "Sorry, I cannot allow you to pass then.", owner));
                                                                                                                                                        Guard.unblock();
                                                                                                                                                }
                                                                                                                                        });
                                                                                                                                }
                                                                                                                        }
                                                                                                                        else {
                                                                                                                                Guard.unblock();
                                                                                                                        }
                                                                                                                }
                                                                                                        });
                                                                                                }
                                                                                        });
                                                                                        owner.sendMenu(options);
                                                                                }
                                                                        });
                                                                        Guard.blockedBy(player);
                                                                                }
                                                                        }
                                                                }
                                                        break;
                                                                
                                                        case 254: // Karamja Members Gate
                                                                        doGate();
                                                                        if (owner.getX() < 435)
                                                                                owner.teleport(435, 682, false);
                                                                        else
                                                                                owner.teleport(434, 682, false);
                                                        break;
                                                        
                                                        //Varrock Gate
                                                        //action: Open
                                                        case 443:
                                                                if (owner.getX() == 93) 
                                                                {
                                                                        doFence();
                                                                        owner.teleport(94, 521, false);
                                                                        owner.sendMessage("You go through the gate");
                                                        } 
                                                                else
                                                                {
                                                                        owner.setBusy(true);
                                                                        Npc guard = World.getNpc(503, 94, 95, 520, 522);
                                                                        guard.blockedBy(owner);
                                                                        if(owner.getQuest(Quests.BIOHAZARD) != null && owner.getQuest(Quests.BIOHAZARD).getStage() >= 7)
                                                                        {
                                                                                if(guard != null) 
                                                                                {
                                                                                        for (Player informee : guard.getViewArea().getPlayersInView())
                                                                                        informee.informOfNpcMessage(new ChatMessage(guard, "Halt, I need to conduct a search on you", owner));                                                                          
                                                                                }
                                                                                World.getDelayedEventHandler().add(new DelayedQuestChat(guard, owner, new String[] {"There have been reports of someone bringing a virus into Varrock"})
                                                                                {
                                                                                        public void finished()
                                                                                        {
                                                                                                World.getDelayedEventHandler().add(new SingleEvent(owner, 1500)
                                                                                                {
                                                                                                        public void action()
                                                                                                        {
                                                                                                                owner.sendMessage("The guard searches you");
                                                                                                                if (owner.getInventory().countId(809) > 0 || owner.getInventory().countId(810) > 0 || owner.getInventory().countId(811) > 0)
                                                                                                                {
                                                                                                                        World.getDelayedEventHandler().add(new DelayedQuestChat(guard, owner, new String[] {"So you're the carrier!", "These items are hereby confiscated by law!"})
                                                                                                                        {
                                                                                                                                public void finished()
                                                                                                                                {
                                                                                                                                        owner.sendMessage("The guard takes all the vials and touch paper");
                                                                                                                                        owner.getInventory().remove(809, 1);
                                                                                                                                        owner.getInventory().remove(810, 1);
                                                                                                                                        owner.getInventory().remove(811, 1);
                                                                                                                                        owner.getInventory().remove(812, 1);
                                                                                                                                        owner.getInventory().remove(813, 1);
                                                                                                                                        owner.sendInventory();
                                                                                                                                        owner.setBusy(false);
                                                                                                                                        guard.unblock();
                                                                                                                                }
                                                                                                                        });
                                                                                                                }
                                                                                                                else
                                                                                                                {
                                                                                                                        World.getDelayedEventHandler().add(new DelayedQuestChat(guard, owner, new String[] {"You may pass"})
                                                                                                                        {
                                                                                                                                public void finished()
                                                                                                                                {
                                                                                                                                        doFence();
                                                                                                                                        owner.teleport(93, 522, false);
                                                                                                                                        owner.sendMessage("You open the gate and pass through");
                                                                                                                                        owner.setBusy(false);
                                                                                                                                        guard.unblock();
                                                                                                                                }
                                                                                                                        });
                                                                                                                }
                                                                                                        }
                                                                                                });
                                                                                        }
                                                                                });
                                                                        }
                                                                        else
                                                                        {
                                                                                owner.sendMessage("The gate is locked");
                                                                                owner.setBusy(false);
                                                                                guard.unblock();
                                                                        }
                                                                }
                                                        break;
                                                        
                                                        case 563: // King Lathas Training Grounds Gate
                                                                if (owner.getQuest(Quests.BIOHAZARD) == null || !owner.getQuest(Quests.BIOHAZARD).finished())
                                                                {
                                                                        owner.sendMessage("In order to access this area the Biohazard quest must be completed.");
                                                                        return;
                                                                }
                                                                else
                                                                if (owner.getQuest(Quests.BIOHAZARD).finished())
                                                                {
                                                                        doGate();
                                                                        if (owner.getY() < 552)
                                                                                owner.teleport(660, 552, false);
                                                                        else
                                                                                owner.teleport(660, 551, false);
                                                                }
                                                        break;
                                                                
                                                        case 311: // Barbarian Outpost Gate
                                                                doGate();
                                                                if (owner.getX() < 494)
                                                                        owner.teleport(494, 544, false);
                                                                else
                                                                        owner.teleport(493, 544, false);
                                                        break;
                                                        
                                                        case 626:
                                                                doGate();
                                                                if (owner.getY() < 532)
                                                                        owner.teleport(703, 532, false);
                                                                else
                                                                        owner.teleport(703, 531, false);
                                                        break;
                                                        
                                                        case 305:
                                                                        doGate();
                                                                        if (owner.getY() < 3266)
                                                                                owner.teleport(196, 3266, false);
                                                                        else
                                                                                owner.teleport(196, 3265, false);       
                                                        break;
                                                        
                                                        case 1089:
                                                                        doGate();
                                                                        if (owner.getX() < 59)
                                                                                owner.teleport(59, 573, false);
                                                                        else
                                                                                owner.teleport(58, 573, false);
                                                        break;
                                                        
                                                        case 356:
                                                                if (owner.getY() < 473) {
                                                                        doGate();
                                                                        owner.teleport(560, 473, false);
                                                                } else {
                                                                        if (owner.getCurStat(8) < 70) {
                                                                                owner.setBusy(true);
                                                                                Npc mcgrubor = World.getNpc(255, 556, 564, 473, 476);
                                                                                if(mcgrubor != null) {
                                                                                        for (Player informee : mcgrubor.getViewArea().getPlayersInView())
                                                                                                informee.informOfNpcMessage(new ChatMessage(mcgrubor, "Hello only the top woodcutters are allowed in here", owner));
                                                                                }
                                                                                World.getDelayedEventHandler().add(new ShortEvent(owner) {
                                                                                        public void action() {
                                                                                                owner.setBusy(false);
                                                                                                owner.sendMessage("You need a woodcutting level of 70 to enter");
                                                                                        }
                                                                                });
                                                                        } else {
                                                                                doGate();
                                                                                owner.teleport(560, 472, false);
                                                                        }
                                                                }
                                                        break;
                                                        
                                                        case 142:
                                                                owner.sendMessage("The doors are locked");
                                                        break;
                                                        
                                                        case 93:
                                                                        doGate();
                                                                        if (owner.getY() < 181)
                                                                                owner.teleport(140, 181, false);
                                                                        else
                                                                                owner.teleport(140, 180, false);
                                                        break;
                                                                
                                                        case 508:
                                                                if (object.getY() > 140 && object.getY() < 148)
                                                                {
                                                                        
                                                                        doGate();
                                                                        owner.sendMessage(Config.getPrefix() + "@ora@Beware @whi@P2P items can be used beyond this gate.");
                                                                        owner.teleport(owner.getX(), owner.getY() < 144 ? owner.getY()+1 : owner.getY()-1);
                                                                }                                                       
                                                                else // Lesser Cage..
                                                                if (owner.getX() < 285)
                                                                {
                                                                        doGate();
                                                                        owner.teleport(285, 185, false);
                                                                }
                                                                else
                                                                {
                                                                        doGate();
                                                                        owner.teleport(284, 185, false);
                                                                }
                                                        break;
                                                        
                                                        case 319:
                                                                        doGate();
                                                                        if (owner.getY() < 179)
                                                                                owner.teleport(243, 179, false);
                                                                        else
                                                                                owner.teleport(243, 178, false);
                                                        break;
                                                                
                                                        case 712:
                                                                owner.teleport(383, 851, false);
                                                        break;
                                                                
                                                        case 611:
                                                                owner.teleport(394, 851, false);
                                                        break;

                                                        case 942:
                                                            owner.setAccessingBank(true);
                                                            owner.showBank();
                                                            break;

                                                                
                                                        case 1079:
                                                                if (owner.getSkillTotal() < 1300) {
                                                                        owner.setBusy(true);
                                                                        final Npc legend = World.getNpc(736, 510, 516, 551, 553);
                                                                        if (legend != null) {
                                                                                for (Player informee : legend.getViewArea().getPlayersInView())
                                                                                        informee.informOfNpcMessage(new ChatMessage(legend, "You need 1,300 Skill Total to pass through this gate", owner));
                                                                                World.getDelayedEventHandler().add(new ShortEvent(owner) {
                                                                                        public void action() {
                                                                                                for (Player informee : legend.getViewArea().getPlayersInView())
                                                                                                        informee.informOfNpcMessage(new ChatMessage(legend, "Come back when you've gained " + (1300 - owner.getSkillTotal()) + " levels!", owner));
                                                                                                owner.setBusy(false);
                                                                                        }
                                                                                });
                                                                        }
                                                                } /*else if (!owner.getInventory().wielding(1288)) {
                                                                        owner.setBusy(true);
                                                                        final Npc legend = World.getNpc(736, 510, 516, 551, 553);
                                                                        if (legend != null) {
                                                                                for (Player informee : legend.getViewArea().getPlayersInView())
                                                                                        informee.informOfNpcMessage(new ChatMessage(legend, "You need to be wearing a Legends Cape to pass through this gate", owner));
                                                                                World.getDelayedEventHandler().add(new ShortEvent(owner) {
                                                                                        public void action() {
                                                                                                owner.setBusy(false);
                                                                                        }
                                                                                });
                                                                        }
                                                                } */else {
                                                                        if (owner.getY() < 551)
                                                                                owner.teleport(513, 551, false);
                                                                        else
                                                                                owner.teleport(513, 550, false);
                                                                }
                                                        break;
                                                        
                                                        default:
                                                                owner.sendMessage("Nothing interesting happens");
                                                }
                                        }
                                        
                                        private void handleListen() {
                                                switch (object.getID()) {
                                                        case 148:
                                                                if (owner.getQuestCompletionStage(Quests.BLACK_KNIGHTS_FORTRESS) == 1) {
                                                                        final Npc blackKnight = World.getNpc(108, 276, 282, 1377, 1382);
                                                                        final Npc witch = World.getNpc(107, 276, 282, 1377, 1382);
                                                                        final Npc greldo = World.getNpc(109, 276, 282, 1377, 1382);
                                                                        if (blackKnight == null || witch == null || greldo == null) {
                                                                                owner.sendMessage("@red@An error has occured with the Black Knight Fortress quest.");
                                                                                owner.sendMessage("@red@Contact Kenix or Marwolf for support");
                                                                        } else {
                                                                                owner.setBusy(true);
                                                                                World.getDelayedEventHandler().add(new DelayedQuestChat(blackKnight, owner, new String[] {"So how's the secret weapon coming along?"}, true) {
                                                                                        public void finished() {
                                                                                                World.getDelayedEventHandler().add(new DelayedQuestChat(witch, owner, new String[] {"The invincibility potion is almost ready", "It's taken me five years but it's almost ready", "Greldo the Goblin here", "Is just going to fetch the last ingredient for me", "It's a specially grown cabbage", "Grown by my cousin Helda who lives in Draynor Manor", "The soil there is slightly magical", "And it gives the cabbages slight magic properties", "Not to mention the trees", "Now remember Greldo only a Draynor Manor cabbage will do", "Don't get lazy and bring any old cabbage", "That would entirely wreck the potion"}) {
                                                                                                        public void finished() {
                                                                                                                World.getDelayedEventHandler().add(new DelayedQuestChat(greldo, owner, new String[] {"Yeth Mithreth"}) {
                                                                                                                        public void finished() {
                                                                                                                                owner.setBusy(false);
                                                                                                                                owner.incQuestCompletionStage(Quests.BLACK_KNIGHTS_FORTRESS);
                                                                                                                        }
                                                                                                                });
                                                                                                        }
                                                                                                });
                                                                                        }
                                                                                });
                                                                        }
                                                                } else
                                                                        owner.sendMessage("Nothing interesting happens");
                                                        break;
                                                        
                                                        default:
                                                                owner.sendMessage("Nothing interesting happens");
                                                }
                                        }
                                        
                                        private void handlePush() {
                                                switch (object.getID()) {
                                                        case 374:
                                                                if (object.getX() == 594 && object.getY() == 3478) {
                                                                        owner.sendMessage("The gate creaks shut");
                                                                        replaceGameObject(373, false);
                                                                        GameObject oldGate = World.getZone(590, 3475).getObjectAt(590, 3475);
                                                                        if(oldGate != null) {
                                                                                replaceGameObject(oldGate, new GameObject(590, 3475, 371, 0, 0));
                                                                        }
                                                                } else if (object.getX() == 590 && object.getY() == 3478){
                                                                        owner.sendMessage("The gate creaks shut");
                                                                        replaceGameObject(373, false);
                                                                        GameObject oldGate = World.getZone(594, 3475).getObjectAt(594, 3475);
                                                                        if (oldGate != null)
                                                                                replaceGameObject(oldGate, new GameObject(594, 3475, 371, 0, 0));
                                                                }
                                                        break;
                                                        
                                                        case 638: // First roots in gnome cave
                                                                owner.setBusy(true);
                                                                owner.sendMessage("You push the roots");
                                                                World.getDelayedEventHandler().add(new ShortEvent(owner) {
                                                                        public void action() {
                                                                                owner.sendMessage("They wrap around you and drag you forwards");
                                                                                owner.teleport(701, 3278, false);
                                                                                owner.setBusy(false);
                                                                        }
                                                                });
                                                        break;
                                                        
                                                        case 639: // Second roots in gnome cave
                                                                owner.setBusy(true);
                                                                owner.sendMessage("You push the roots");
                                                                World.getDelayedEventHandler().add(new ShortEvent(owner) {
                                                                        public void action() {
                                                                                owner.sendMessage("They wrap around you and drag you forwards");
                                                                                owner.teleport(701, 3281, false);
                                                                                owner.setBusy(false);
                                                                        }
                                                                });
                                                        break;
                                                        
                                                        default:
                                                                owner.sendMessage("Nothing interesting happens");
                                                }
                                        }
                                        
                                        private void handleRest() {
                                            handleSleepObject("You rest on the bed");
                                        }
                                        
                                        /*
                                         * Pyru added.
                                         */
                                        private void handleHammock()
                                        {
                        handleSleepObject("You rest on the hammock");
                                        }

                                        private void handleSleepObject(String message) {
                                            if (Config.isDisableFatigue()) {
                                                owner.sendMessage("Fatigue is disabled on this server.");
                                                return;
                        }

                        if (System.currentTimeMillis() - player.getLastSleep() > 500)
                        {
                            owner.sendMessage(message);
                            owner.sleep();
                        }
                    }
                                        
                                        private void handleSearchForTraps() {
                                                if (object.getID() != 338) {
                                                        final ChestDef chest = EntityHandler.getChestDefinition(object.getID());
                                                        if (chest != null && owner.getStatus() == Action.IDLE) {
                                                                owner.setBusy(true);
                                                                owner.setStatus(Action.THIEVING_CHEST);
                                                                if (owner.getMaxStat(17) < chest.getLevel()) {
                                                                        owner.sendMessage("You do not have a high enough thieving level to steal from this chest.");
                                                                        owner.setBusy(false);
                                                                        owner.setStatus(Action.IDLE);
                                                                } else {
                                                                        if (!chest.requiresLockpick() || owner.getInventory().countId(714) > 0) {
//                                                                              Bubble bubble = new Bubble(owner.getIndex(), 549);
                                                                                for (Player p : owner.getViewArea().getPlayersInView())
                                                                                {
                                                                                        p.watchItemBubble(owner.getIndex(), 549);
//                                                                                      p.informOfBubble(bubble);
                                                                                }
                                                                                World.getDelayedEventHandler().add(new DelayedGenericMessage(owner, new String[] {"You search for traps on the chest...", "You find a trap on the chest...", "You disable the trap"}, 1500) {
                                                                                        public void finished() {
                                                                                                World.registerEntity(new GameObject(object.getLocation(), 339, object.getDirection(), object.getType()));
                                                                                                World.getDelayedEventHandler().add(new DelayedGenericMessage(owner, new String[] {"You open the chest", "You find treasure inside!"}, 500) {
                                                                                                        public void finished() {
                                                                                                                World.registerEntity(new GameObject(object.getLocation(), 338, object.getDirection(), object.getType()));
                                                                                                                World.delayedSpawnObject(object.getLoc(), chest.getRespawnTime());
                                                                                                                owner.increaseXP(Skills.THIEVING, chest.getExperience());
                                                                                                                owner.sendStat(17);
                                                                                                                for (InvItem item : chest.getLoot())
                                                                                                                        owner.getInventory().add(item);
                                                                                                                owner.sendInventory();
                                                                                                                if (World.withinWorld(chest.getXTeleport(), chest.getYTeleport())) {
                                                                                                                        owner.sendMessage("You have activated a magical trap on the chest!");
                                                                                                                        owner.teleport(chest.getXTeleport(), chest.getYTeleport(), true);
                                                                                                                }
                                                                                                                owner.setBusy(false);
                                                                                                                owner.setStatus(Action.IDLE);
                                                                                                        }
                                                                                                });
                                                                                        }
                                                                                });
                                                                        } else
                                                                                owner.sendMessage("You need a lockpick to steal from this chest");
                                                                }
                                                        }                                               
                                                } else
                                                        owner.sendMessage("It looks like that chest has already been looted.");
                                        }
                                        
                                        private void handleStealFrom() {
                                                final StallThievingDefinition stall = EntityHandler.getStallThievingDefinition(object.getID());
                                                if (stall != null) {
                                                        owner.setStatus(Action.THIEVING_STALL);
                                                        if (owner.getMaxStat(17) < stall.getLevel()) {
                                                                owner.sendMessage("You need a thieving level of " + stall.getLevel() + " to steal from that");
                                                                owner.setStatus(Action.IDLE);
                                                                owner.setBusy(false);
                                                        } else {
                                                                owner.setBusy(true);
//                                                              final Bubble bubble = new Bubble(owner.getIndex(), 609);
                                                                for (Player p : owner.getViewArea().getPlayersInView())
                                                                {
                                                                        p.watchItemBubble(owner.getIndex(), 609);
//                                                                      p.informOfBubble(bubble);
                                                                }
                                                                owner.sendMessage("You attempt to steal from the " + object.getGameObjectDef().name);
                                                                World.getDelayedEventHandler().add(new ShortEvent(owner) {
                                                                        public void action() {
                                                                                final Npc guardian = Formulae.isPlayerCaughtThievingStall(owner, stall);
                                                                                if (guardian == null || !guardian.withinAggressionRange(owner)) {
                                                                                        if (!Formulae.thievingFormula(owner.getMaxStat(17), stall.getLevel()))
                                                                                                owner.sendMessage("You failed to steal from the " + object.getGameObjectDef().name);
                                                                                        else {
                                                                                                World.registerEntity(new GameObject(object.getLocation(), 341, object.getDirection(), object.getType()));
                                                                                                World.delayedSpawnObject(object.getLoc(), stall.getRespawnTime());
                                                                                                owner.sendMessage("You successfully thieved from the " + object.getGameObjectDef().name);
                                                                                                owner.getInventory().add(stall.getLoot());
                                                                                                owner.sendInventory();
                                                                                                owner.increaseXP(Skills.THIEVING, stall.getExperience());
                                                                                                owner.sendStat(17);
                                                                                        }
                                                                                } else if (guardian.getID() == stall.getOwner()) {
                                                                                        owner.sendMessage("You failed to steal from the " + object.getGameObjectDef().name);
                                                                                        for (Player p : owner.getViewArea().getPlayersInView())
                                                                                                p.informOfNpcMessage(new ChatMessage(guardian, "Guards! Guards! Help I'm being robbed!", owner));
                                                                                        for (int stallGuardian : stall.getGuardians()) {
                                                                                                Npc attackerGuardian = World.getNpc(stallGuardian, owner.getX() - 5, owner.getX() + 5, owner.getY() - 5, owner.getY() + 5);
                                                                                                if (attackerGuardian != null) {
                                                                                                        attackerGuardian.setAggressive(owner);
                                                                                                        break;
                                                                                                }
                                                                                        }
                                                                                } else {
                                                                                        owner.sendMessage("You failed to steal from the " + object.getGameObjectDef().name);
                                                                                        guardian.setAggressive(owner);
                                                                                }
                                                                                owner.setStatus(Action.IDLE);
                                                                                owner.setBusy(false);
                                                                        }
                                                                });
                                                        }
                                                }
                                        }
                                        
                                        private void handleClimbDownEvent() {
                                                switch(object.getID()) {
                                                        
                                                        case 244: //Entrana Ladder
                                                                Npc Monk = World.getNpc(213, owner.getX() - 5, owner.getX() + 5, owner.getY() - 5, owner.getY() + 5);
                                                                if (Monk != null && owner.getInventory().containsViolentEquipment() && owner.getQuest(Quests.LOST_CITY).getStage() >= 3) 
                                                                {
                                                                        for (Player informee : Monk.getViewArea().getPlayersInView())
                                                                        {
                                                                                informee.informOfNpcMessage(new ChatMessage(Monk, "Sorry, we don't allow any violent weapons on our holy island.", owner));
                                                                        }
                                                                        return;
                                                                }
                                                                else
                                                                {
                                                                        owner.sendMessage("You climb down the ladder...");
                                                                        owner.sendMessage("...And end up in a dark cave.");
                                                                        owner.teleport(428, 3380);
                                                                }
                                                        break;


                                                        // Mage Bank Ladder
                                                        case 1188:
                                                                if (object.getX() == 223 && object.getY() == 110)
                                                                {
                                                                        // if not a mod and they've moved under 3 seconds or ran under 3 seconds, deny. What about if they're in combat for 3 seconds, then click the ladder?
                                                                        // try.
                                                                        if (!owner.isSuperMod() && (System.currentTimeMillis() - owner.getLastMoved() < 3000 || System.currentTimeMillis() - owner.getRunTimer() < 3000 || owner.inCombat()))
                                                                        {
                                                                                owner.sendMessage("You need to be standing still for 3 seconds, and out of combat, in order to climb-down this ladder.");
                                                                                return;
                                                                        }
                                                                        else
                                                                        {
                                                                                owner.sendMessage("You climb down the ladder...");
                                                                                owner.teleport(446, 3368);
                                                                                return;
                                                                        }
                                                                }
                                                        break;
                                                        
                                                        /*case 244:
                                                                owner.sendMessage("You climb down the ladder");
                                                                if (owner.getCurStat(5)> (owner.getMaxStat(5) * .1)) {
                                                                        owner.setCurStat(5, (int)(owner.getMaxStat(5) * .1));
                                                                        owner.sendStat(5);
                                                                }
                                                                owner.teleport(428, 3380, false);
                                                                break;
                                                                //Entrana Ladder Guardian
                                                                /*Npc monk = World.getNpc(213, 424, 429, 545, 549);
                                                                if(monk != null) {
                                                                        if (Formulae.getDirection(owner, monk) != -1) {
                                                                                monk.setSprite(Formulae.getDirection(owner, monk));
                                                                            owner.setSprite(Formulae.getDirection(monk, owner));
                                                                        }
                                                                        Interface monkHandler = World.executors.get(213);
                                                                        if(monkHandler != null) {
                                                                                monkHandler.setOwner(owner);
                                                                                monkHandler.setNpc(monk);
                                                                                World.eventPool.assignTask(monkHandler);
                                                                        }
                                                                }
                                                                break;*/
                                                        case 223:
                                                                if (owner.getCurStat(14) >= 60)
                                                                {
                                                                        owner.teleport(274, 3397, false);
                                                                        return;
                                                                }
                                                                else
                                                                {
                                                                        owner.setBusy(true);
                                                                        Npc dwarf = World.getNpc(owner.getX(), owner.getY(), 5);
                                                                        if (dwarf != null) {
                                                                                for (Player informee : dwarf.getViewArea().getPlayersInView())
                                                                                        informee.informOfNpcMessage(new ChatMessage(dwarf, "Sorry only the top miners are allowed in there", owner));
                                                                        }
                                                                        World.getDelayedEventHandler().add(new ShortEvent(owner) {
                                                                                public void action() {
                                                                                        owner.setBusy(false);
                                                                                        owner.sendMessage("You need a mining of level 60 to enter");
                                                                                }
                                                                        });
                                                                }
                                                                break;

                                                                default:
                                                                int[] coords = coordModifier(owner, false);
                                                                owner.sendMessage("You go down the " + object.getGameObjectDef().getName());
                                                                owner.teleport(coords[0], coords[1], false);
                                                }
                                                
                                                /*
                                                 * Pyru Added, Merlins Crystal
                                                 */
                                                if (object.getX() == 460 && object.getY() == 3280) {
                                                        owner.teleport(460, 1393);
                                                }
                                        }
                                        
                                        private void handleClimbUpEvent() {
                                                switch (object.getID()) {
                                                
                                                        case 5:
                                                                if (object.getX() == 459 && object.getY() == 1393)
                                                                {
                                                                        if (owner.getQuest(Quests.MERLINS_CRYSTAL) != null)
                                                                        {
                                                                                if(owner.getQuest(Quests.MERLINS_CRYSTAL).getStage() == 7 || owner.getQuest(Quests.MERLINS_CRYSTAL).finished())
                                                                                {
                                                                                        owner.teleport(461, 3280);
                                                                                        owner.sendMessage("You climb up the ladder");
                                                                                        return;
                                                                                }
                                                                        }
                                                                        else
                                                                        {
                                                                                owner.sendMessage("You go up the " + object.getGameObjectDef().getName());
                                                                                int[] coords = coordModifier(owner, true);
                                                                                owner.teleport(coords[0], coords[1], false);
                                                                                return;
                                                                        }
                                                                }
                                                                else
                                                                {
                                                                        owner.sendMessage("You go up the " + object.getGameObjectDef().getName());
                                                                        int[] coords = coordModifier(owner, true);
                                                                        owner.teleport(coords[0], coords[1], false);
                                                                        return;
                                                                }
                                                        break;
                                                        
                                                        case 981:
                                                                Quest dwarfCannon = owner.getQuest(Quests.DWARF_CANNON);
                                                                if (dwarfCannon != null) {
                                                                        if (dwarfCannon.getStage() == 1) {
                                                                                owner.sendMessage("You go up the " + object.getGameObjectDef().getName());
                                                                                owner.teleport(616, 1435, false);
                                                                        } else
                                                                                owner.sendMessage("The trap door is locked");
                                                                } else
                                                                        owner.sendMessage("The trap door is locked");
                                                                break;
                                                        /*case 249:
                                                                Npc fairy = World.getNpc(229, 97, 100, 3536, 3539);
                                                                if(fairy != null) {
                                                                        Interface fairyLadderAttendant = World.executors.get(229);
                                                                        if(fairyLadderAttendant != null) {
                                                                                owner.updateSprite(fairy.getX(), fairy.getY());
                                                                                fairy.updateSprite(owner.getX(), owner.getY());
                                                                                fairyLadderAttendant.setOwner(owner);
                                                                                fairyLadderAttendant.setNpc(fairy);
                                                                                World.eventPool.assignTask(fairyLadderAttendant);
                                                                        }
                                                                }

                                                                break;*/
                                                        case 170:
                                                        case 270:
                                                                owner.sendMessage("You climb up the ladder");
                                                                if(owner.onCrandor()) 
                                                                {
                                                                        owner.teleport(408, 640, false);
                                                                }
                                                                else
                                                                {
                                                                        owner.teleport(259, 641, false);
                                                                }
                                                        break;
                                                        
                                                        case 198:
                                                                if (owner.getMaxStat(5) > 30)
                                                                        owner.teleport(251, 1411, false);
                                                                else {
                                                                        owner.setBusy(true);
                                                                        Npc abbot = World.getNpc(174, 249, 252, 458, 468);
                                                                        if(abbot != null) {
                                                                                for(Player informee : abbot.getViewArea().getPlayersInView()) {
                                                                                        informee.informOfNpcMessage(new ChatMessage(abbot, "Hello only people with high prayer are allowed in here", owner));
                                                                                }
                                                                        }
                                                                        World.getDelayedEventHandler().add(new ShortEvent(owner) {
                                                                                        public void action() {
                                                                                                owner.setBusy(false);
                                                                                                owner.sendMessage("You need a prayer level of 31 to enter");
                                                                                        }
                                                                                });
                                                                }
                                                        break;
                                                        
                                                        case 342:
                                                                final Npc paladin = World.getNpc(323, 608, 613, 599, 607);
                                                                if (paladin != null) {
                                                                        for(Player informee : paladin.getViewArea().getPlayersInView()) {
                                                                                informee.informOfNpcMessage(new ChatMessage(paladin, "Stop right there!", owner));
                                                                        }
                                                                        paladin.setAggressive(owner);
                                                                        World.getDelayedEventHandler().add(new ShortEvent(owner) {
                                                                                public void action() {
                                                                                        if (!owner.isBusy()) {
                                                                                                owner.sendMessage("You go up the " + object.getGameObjectDef().getName());
                                                                                                int[] coords = coordModifier(owner, true);
                                                                                                        owner.teleport(coords[0], coords[1], false);                                                                                            
                                                                                        }
                                                                                }
                                                                        });
                                                                }
                                                        break;
                                                        
                                                        default:
                                                                owner.sendMessage("You go up the " + object.getGameObjectDef().getName());
                                                                int[] coords = coordModifier(owner, true);
                                                                owner.teleport(coords[0], coords[1], false);
                                                }
                                        }       
                                        
                                        private void handleMining(final int click)
                                        {
                                                owner.setCancelBatch(false);
                                                switch(Formulae.getPickAxe(owner)) {
                                                        case -1:
                                                                player.sendMessage("You need a pickaxe in order to mine this rock.");
                                                                break;
                                                        case -2:
                                                                player.sendMessage("You are not high enough level to use the pickaxe in your inventory.");
                                                                break;
                                                        case 156:
                                                                mineLoop(click, 1);
                                                                break;
                                                        case 1258:
                                                                mineLoop(click, 2);
                                                                break;
                                                        case 1259:
                                                                mineLoop(click, 4);
                                                                break;
                                                        case 1260:
                                                                mineLoop(click, 6);
                                                                break;
                                                        case 1261:
                                                                mineLoop(click, 9);
                                                                break;
                                                        case 1262:
                                                                mineLoop(click, 12);
                                                                break;
                                                        default:
                                                                break;


                                                }
                                                    
                                                // XXXXX
                                                //if (owner.isSub())
                                                //      mineLoop(click, (int)Math.ceil((owner.getMaxStat(14) / 10) * 2));
                                                //else
                                                //      mineLoop(click, (int)Math.ceil(owner.getMaxStat(14) / 10));
                                        }
                                        
                                        private void mineLoop(final int click, final int loop) 
                                        {
                                                if (owner.getCancelBatch())
                                                        return;
                                                
                                                if (owner.isFatigued())
                                                {
                                                        owner.sendMessage("You are too tired to mine this rock.");
                                                        owner.cancelBatch = true;
                                                        return;
                                                }
                                                
                                                final ObjectMiningDef def = EntityHandler.getObjectMiningDef(object.getID());
                                                
                                                if (def != null && !object.isRemoved())
                                                {
                                                        if (owner.getCurStat(14) < def.getReqLevel())
                                                        {
                                                                player.sendMessage("You need a mining level of " + def.getReqLevel() + " to mine this rock.");
                                                                return;
                                                        }
                                                        else
                                                        {
                                                                owner.setBusy(true);
                                                                owner.sendSound("mine", false);                                                         
                                                                owner.sendMessage("You swing your pick at the rock...");
                                                                final int pickaxe = Formulae.getPickAxe(owner);
                                                                for (Player p : owner.getViewArea().getPlayersInView())
                                                                {
                                                                        p.watchItemBubble(owner.getIndex(), pickaxe);
                                                                }
                                                                
                                                                World.getDelayedEventHandler().add(new SingleEvent(owner, 2000) 
                                                                {
                                                                        public void action() 
                                                                        {                                               
                                                                                if (Formulae.getOre(def, owner.getCurStat(14), pickaxe)) 
                                                                                {
                                                                                        if (DataConversions.random(0, owner.isWearing(597) ? 100 : 200) == 0) 
                                                                                        {
                                                                                                InvItem gem = new InvItem(Formulae.getGem(), 1);
                                                                                                owner.getInventory().add(gem);
                                                                                                owner.sendMessage("You found a gem!");
                                                                                        } 
                                                                                        else 
                                                                                        {
                                                                                                final InvItem ore = new InvItem(def.getOreId());
                                                                                                owner.getInventory().add(ore);
                                                                                                owner.sendMessage("You manage to obtain some " + ore.getDef().getName() + ".");
                                                                                                owner.increaseXP(Skills.MINING, def.getExp());
                                                                                                owner.sendStat(14);
                                                                                                World.registerEntity(new GameObject(object.getLocation(), 98, object.getDirection(), object.getType()));
                                                                                                World.delayedSpawnObject(object.getLoc(), def.getRespawnTime() * 1000);
                                                                                        }
                                                                                        owner.sendInventory();
                                                                                        owner.setBusy(false);
                                                                                        return;
                                                                                } 
                                                                                else {
                                                                                    owner.sendMessage("You only succeed in scratching the rock.");
                                                                                    owner.setBusy(false);
                                                                                    if (Config.getSkillLoopMode() == 1 || Config.getSkillLoopMode() == 2) {
                                                                                        World.getDelayedEventHandler().add(new SingleEvent(owner, 2500) {
                                                                                            public void action() {
                                                                                                mineLoop(click, loop);
                                                                                            }
                                                                                        });
                                                                                    } else {
                                                                                        if (loop > 1)
                                                                                            mineLoop(click, loop - 1);
                                                                                    }
                                                                                }
                                                                        }
                                                                });
                                                        }
                                                }
                                                else
                                                {
                                                        player.sendMessage("There is currently no ore available in this rock.");
                                                        return;
                                                }
                                        }
                                        
                                        private void handleProspect() {
                                                owner.sendMessage("You survey the rock for ores...");
                                                World.getDelayedEventHandler().add(new ShortEvent(owner) {
                                                        public void action() {
                                                                final ObjectMiningDef def = EntityHandler.getObjectMiningDef(object.getID());
                                                                if (def != null) {
                                                                        InvItem ore = new InvItem(def.getOreId());
                                                                        if (owner.getLocation().onTutorialIsland())
                                                                        {
                                                                                Quest tutorialIsland = owner.getQuest(Quests.TUTORIAL_ISLAND);
                                                                                if (tutorialIsland != null)
                                                                                {
                                                                                        if (tutorialIsland.getStage() == 12)
                                                                                        {
                                                                                                owner.sendMessage("This rock contains " + ore.getDef().getName() + ".");
                                                                                                player.incQuestCompletionStage(Quests.TUTORIAL_ISLAND);
                                                                                        }
                                                                                        else
                                                                                        {
                                                                                                owner.sendMessage("Please speak to the mining instructor for further direction.");
                                                                                        }
                                                                                }
                                                                                return;
                                                                        }
                                                                        owner.sendMessage("This rock contains " + ore.getDef().getName() + ".");
                                                                } else
                                                                        owner.sendMessage("There is currently no ore available in this rock.");                                 
                                                        }
                                                });
                                        }
                                        
                                        /*
                                         *      Pyru added this.
                                         */

                                        private void handleDramenEvent() 
                                        {
                                                final int DRAMEN_BRANCH_ID = 510;
                                                if(object.getID() == 245)
                                                {
                                                        Quest quest = owner.getQuest(Quests.LOST_CITY);
                                                        if(quest.finished()) 
                                                        {
                                                                final int axe = Formulae.getWoodcuttingAxe(owner);
                                                                if (axe > -1)
                                                                {
                                                                        if (owner.getMaxStat(8) > 35) {
                                                                                if(owner.getInventory().countId(DRAMEN_BRANCH_ID) == 0)
                                                                                {
                                                                                        owner.sendMessage("You cut a branch from the tree");
                                                                                        owner.getInventory().add(new InvItem(DRAMEN_BRANCH_ID, 1));
                                                                                        owner.sendInventory();
                                                                                }
                                                                                else
                                                                                {
                                                                                        owner.sendMessage("You already have the Dramen branch");
                                                                                }
                                                                        } else
                                                                        owner.sendMessage("You must have at least 36 woodcutting in order to chop this tree.");
                                                                }
                                                                else
                                                                {
                                                                                owner.sendMessage("You need an axe in order to cut a branch from the Dramen tree.");
                                                                }
                                                        }
                                                        else
                                                        if(quest != null)
                                                        {
                                                                switch(quest.getStage())
                                                                {
                                                                case 3: // didn't kill spirit yet
                                                                        Npc spirit = World.getNpc(216, 408, 415, 3399, 3405);
                                                                        if(spirit == null || spirit.isBusy() || spirit.inCombat()) // spirit not there or in use...make a new one
                                                                        {
                                                                                spirit = new Npc(216, 412, 3403, 408, 415, 3399, 3405);
                                                                                
                                                                                spirit.setRespawn(false);
                                                                                World.registerEntity(spirit, 200000);
                                                                        }
                                                                        spirit.setAggressive(owner);
                                                                        final Npc chat = spirit;
                                                                        owner.setBusy(true);
                                                                        chat.blockedBy(owner);
                                                                        World.getDelayedEventHandler().add(
                                                                                new DelayedQuestChat(spirit, owner, new String[] {"Stop", "I am the spirit of the dramen tree", "You must come through me before touching that tree"}, true)
                                                                                {
                                                                                        @Override
                                                                                        public void finished()
                                                                                        {
                                                                                                owner.setBusy(false);
                                                                                                chat.unblock();
                                                                                                
                                                                                        }
                                                                                }
                                                                        );
                                                                        break;
                                                                case 4: // killed spirit
                                                                        final int axe = Formulae.getWoodcuttingAxe(owner);
                                                                        if (axe > -1)
                                                                        {
                                                                                if (owner.getMaxStat(8) > 35) {
                                                                                        if(owner.getInventory().countId(DRAMEN_BRANCH_ID) == 0)
                                                                                        {
                                                                                                owner.sendMessage("You cut a branch from the tree");
                                                                                                owner.getInventory().add(new InvItem(DRAMEN_BRANCH_ID, 1));
                                                                                                owner.sendInventory();
                                                                                        }
                                                                                        else
                                                                                        {
                                                                                                owner.sendMessage("You already have the dramen branch");
                                                                                        }
                                                                                } else
                                                                                owner.sendMessage("You must have at least 36 woodcutting in order to chop this tree.");
                                                                        }
                                                                        else
                                                                        {
                                                                                owner.sendMessage("You need an axe in order to cut a branch from the dramen tree.");
                                                                        }
                                                                        break;
                                                                        default: //actually...can happen.
                                                                                owner.sendMessage("Nothing interesting happens");
                                                                        break;
                                                                                
                                                                }
                                                        }
                                                }
                                                /*if (object.getID() == 245) {
                                                        
                                                        Npc existingSpirit = World.getNpc(216, 408, 415, 3399, 3405);
                                                        Npc treeSpirit = null;
                                                        if (existingSpirit == null) {
                                                                treeSpirit = new Npc(216, 412, 3403, 408, 415, 3399, 3405);
                                                                World.registerEntity(treeSpirit, 500000);
                                                        } else {
                                                                if (existingSpirit.isBusy()) {
                                                                        treeSpirit = new Npc(216, 412, 3403, 408, 415, 3399, 3405);
                                                                        World.registerEntity(treeSpirit, 500000);
                                                                } else
                                                                        treeSpirit = existingSpirit;
                                                        }
                                                        treeSpirit.setRespawn(false);
                                                        final Npc chat = treeSpirit;
                                                        owner.setBusy(true);
                                                        chat.blockedBy(owner);
                                                        World.getDelayedEventHandler().add(new DelayedQuestChat(treeSpirit, owner, new String[] {"Stop", "I am the spirit of the dramen tree", "You must come through me before touching that tree"}, true) {
                                                                @Override
                                                                public void finished() {
                                                                        owner.setBusy(false);
                                                                        chat.unblock();
                                                                }
                                                        });
                                                }*/
                                        }
                                        
                                        private void handleWoodcutting(final int click)
                                        {
                                                owner.setCancelBatch(false);
                                                
                                                if (owner.isSub())
                                                        woodcutLoop(click, (int)Math.ceil((owner.getMaxStat(8) / 10) * 2));
                                                else
                                                        woodcutLoop(click, (int)Math.ceil(owner.getMaxStat(8) / 10));
                                        }
                                        
                                        private void woodcutLoop(final int click, final int loop) 
                                        {
                                                final WoodcutDef def = EntityHandler.getWoodcutDef(object.getID());
                                                
                                                if (owner.getCancelBatch())
                                                        return;
                                                
                                                if (owner.isFatigued())
                                                {
                                                        owner.cancelBatch = true;
                                                        owner.sendMessage("You are too tired to chop this tree.");
                                                        return;
                                                }
                                                
                                                if (owner.getMaxStat(8) < def.getLevel())
                                                {
                                                        owner.cancelBatch = true;
                                                        owner.sendMessage("Your woodcutting level is not high enough to chop this tree.");
                                                        return;
                                                }
                                                
                                                if (def != null)
                                                {
                                                        final int axe = Formulae.getWoodcuttingAxe(owner);

                                                        if (axe == -1)
                                                        {
                                                                owner.cancelBatch = true;
                                                                owner.sendMessage("You need an axe in order to chop this tree.");
                                                                return;
                                                        }
                                                        
                                                        if(axe == -2) {
                                                                owner.cancelBatch = true;
                                                                owner.sendMessage("You need an axe which you have the proper woodcutting level to use to chop this tree.");
                                                                return;
                                                        }
                                                        
                                                        owner.setStatus(Action.CHOPPING_TREE);
                                                        owner.setBusy(true);
                                                        owner.sendMessage("You swing your " + EntityHandler.getItemDef(axe).getName().toLowerCase() + " at the tree...");
                                                        
                                                        for (Player p : owner.getViewArea().getPlayersInView())
                                                        {
                                                                p.watchItemBubble(owner.getIndex(), axe);
                                                        }
                                                        World.getDelayedEventHandler().add(new SingleEvent(owner, 1500) 
                                                        {
                                                                public void action() 
                                                                {
                                                                        if (Formulae.getLog(def.getLevel(), owner.getCurStat(8), axe)) 
                                                                        {
                                                                                InvItem log = new InvItem(def.getLogID());
                                                                                owner.getInventory().add(log);
                                                                                owner.sendMessage("You get some wood");
                                                                                owner.sendInventory();
                                                                                owner.increaseXP(Skills.WOODCUT, def.getExperience());
                                                                                owner.sendStat(8);
                                                                                owner.setBusy(false);
                                                                                if (DataConversions.random(1, 100) <= def.getFell()) 
                                                                                {
                                                                                        World.unregisterEntity(object);
                                                                                        GameObject stump = new GameObject(object.getLocation(), 4, object.getDirection(), object.getType());
                                                                                        World.registerEntity(stump);
                                                                                        World.delayedRemoveObject(stump, def.getRespawnTime() * 1000);
                                                                                        World.delayedSpawnObject(object.getLoc(), def.getRespawnTime() * 1000);
                                                                                        return;
                                                                                }
                                                                                if(Config.getSkillLoopMode() == 2) {
                                                                                    if (!owner.getInventory().full()) {
                                                                                        World.getDelayedEventHandler().add(new SingleEvent(owner, 2500) {
                                                                                            public void action() {
                                                                                                woodcutLoop(click, loop);
                                                                                            }
                                                                                        });
                                                                                    }
                                                                                }
                                                                        } 
                                                                        else
                                                                        {       
                                                                                owner.sendMessage("You slip and fail to hit the tree");
                                                                                owner.setBusy(false);
                                                                                owner.setStatus(Action.IDLE);
                                                                                if(Config.getSkillLoopMode() == 1 || Config.getSkillLoopMode() == 2) {
                                                                                    World.getDelayedEventHandler().add(new SingleEvent(owner,2500) {
                                                                                        public void action() {
                                                                                            woodcutLoop(click, loop);
                                                                                        }
                                                                                    });
                                                                            }
                                                                        }
                                                                }
                                                        });
                                                }
                                        }
                                        
                                        private void fishLoop(final int click, final int loop) 
                                        {
                                                if (owner.getCancelBatch())
                                                        return;
                                                
                                                if (owner.isFatigued()) 
                                                {
                                                        owner.sendMessage("You are too tired to catch this fish");
                                                        owner.cancelBatch = true;
                                                        return;
                                                }
                                                
                                                /*
                                                 * Fishing Contest
                                                 * bait fish spot. 
                                                 */
                                                if (object.getX() == 570 && object.getY() == 489)
                                                {
                                                        Quest Fishing_Contest = owner.getQuest(Quests.FISHING_CONTEST);
                                                        switch(Fishing_Contest.getStage())
                                                        {
                                                                case 3:
                                                        if(owner.getInventory().countId(377) > 0 && owner.getInventory().countId(715) > 0) 
                                                        {
                                                                        if (owner.getCurStat(10) > 9)
                                                                        {
                                                                                owner.sendMessage("You attempt to bait a fish...");
                                                                                owner.sendMessage("You catch a giant carp");
                                                                                owner.getInventory().remove(715, 1);
                                                                                owner.getInventory().add(717, 1);
                                                                                owner.sendInventory();
                                                                        }
                                                                        else
                                                                        {
                                                                                owner.sendMessage("You must have a fishing level of 10 to bait these fish.");
                                                                        }
                                                        }
                                                        else
                                                        {
                                                                owner.sendMessage("You must have a fishing rod and red vine worms in your inventory");
                                                        }
                                                                break;
                                                        }
                                                        return;
                                                }
                                                /*
                                                 * End Fishing Contest.
                                                 */
                                                        
                                                final ObjectFishingDef def = EntityHandler.getObjectFishingDef(object.getID(), click);
                                                if (def != null) {
                                                        if (owner.getCurStat(10) < def.getReqLevel())
                                                                owner.sendMessage("You need a fishing level of " + def.getReqLevel() + " to fish here.");
                                                        else {
                                                                int netId = def.getNetId();
                                                                if (owner.getInventory().countId(netId) > 0) {
                                                                        final int baitId = def.getBaitId();
                                                                        if (owner.getInventory().countId(baitId) > 0 || baitId == -1) {
                                                                                owner.setBusy(true);
                                                                                owner.sendSound("fish", false);
//                                                                              Bubble bubble = new Bubble(owner.getIndex(), netId);
                                                                                for (Player p : owner.getViewArea().getPlayersInView())
                                                                                {
                                                                                        p.watchItemBubble(owner.getIndex(), netId);
//                                                                                      p.informOfBubble(bubble);
                                                                                }
                                                                                owner.sendMessage("You attempt to catch some fish");
                                                                                World.getDelayedEventHandler().add(new ShortEvent(owner) {
                                                                                        public void action() {
                                                                                            ObjectFishDef def = Formulae.getFish(object.getID(), owner.getCurStat(10), click);
                                                                                            if (def != null) {
                                                                                                if (baitId >= 0) {
                                                                                                    int idx = owner.getInventory().getLastIndexById(baitId);
                                                                                                    InvItem bait = owner.getInventory().get(idx);
                                                                                                    long newCount = bait.getAmount() - 1;
                                                                                                    if (newCount <= 0)
                                                                                                        owner.getInventory().remove(idx);
                                                                                                    else
                                                                                                        bait.setAmount(newCount);
                                                                                                }
                                                                                                InvItem fish = new InvItem(def.getId());
                                                                                                owner.getInventory().add(fish);
                                                                                                owner.sendMessage("You catch a " + fish.getDef().getName() + ".");
                                                                                                owner.sendInventory();
                                                                                                owner.increaseXP(Skills.FISHING, def.getExp());
                                                                                                owner.sendStat(10);
                                                                                                owner.setBusy(false);
                                                                                                if(Config.getSkillLoopMode() == 2) {
                                                                                                    if (!owner.getInventory().full()) {
                                                                                                        World.getDelayedEventHandler().add(new SingleEvent(owner, 2500) {
                                                                                                            public void action() {
                                                                                                                fishLoop(click, loop);
                                                                                                            }
                                                                                                        });
                                                                                                    }
                                                                                                }
                                                                                            } else {
                                                                                                owner.sendMessage("You fail to catch anything.");
                                                                                                owner.setBusy(false);
                                                                                                if(Config.getSkillLoopMode() == 1 || Config.getSkillLoopMode() == 2) {
                                                                                                    World.getDelayedEventHandler().add(new SingleEvent(owner,2500) {
                                                                                                        public void action() {
                                                                                                            fishLoop(click, loop - 1);
                                                                                                        }
                                                                                                    });
                                                                                                }
                                                                                            }
                                                                                        }
                                                                                });
                                                                        } else
                                                                                owner.sendMessage("You don't have any " + EntityHandler.getItemDef(baitId).getName() + " left.");
                                                                }  else
                                                                        owner.sendMessage("You need a " + EntityHandler.getItemDef(netId).getName() + " to catch these fish.");
                                                        }
                                                }                                               
                                        }
                                        
                                        private void handleFishing(final int click) 
                                        {
                                                owner.setCancelBatch(false);
                                                
                                                if (owner.isSub())
                                                        fishLoop(click, (int)Math.ceil((owner.getMaxStat(10) / 10) * 2));
                                                else
                                                        fishLoop(click, (int)Math.ceil(owner.getMaxStat(10) / 10));
                                        }
                                        
                                        private void handleSwingEvent() {
                                                switch (object.getID()) {
                                                        case 684:
                                                                if (owner.getY() == 3225 && owner.getX() > 205 && owner.getX() < 209) {
                                                                        owner.sendMessage("You reach out and grab the rope swing");
                                                                        if (agilityFormulae(1)) {
                                                                                owner.teleport(207, 3221, false);
                                                                                owner.sendMessage("you hold on tight and manage to make it across");
                                                                        } else {
                                                                                int damage = (int)(owner.getCurStat(3) * 0.9);
                                                                                owner.setLastDamage(damage);
                                                                                owner.setCurStat(3, owner.getCurStat(3) - damage);
                                                                                owner.sendStat(3);
                                                                                owner.sendMessage("Ouch!");
                                                                                owner.informOfModifiedHits(owner);
                                                                        }
                                                                }
                                                        break;
                                                        
                                                        case 685:
                                                                if (owner.getY() == 3221 && owner.getX() > 205 && owner.getX() < 209) {
                                                                        owner.sendMessage("You reach out and grab the rope swing");
                                                                        if (agilityFormulae(1)) {
                                                                                owner.teleport(207, 3225, false);
                                                                                owner.sendMessage("you hold on tight and manage to make it across");
										owner.increaseXP(Skills.AGILITY, 40);
                                                                        } else {
                                                                                int damage = (int)(owner.getCurStat(3) * 0.9);
                                                                                owner.setLastDamage(damage);
                                                                                owner.setCurStat(3, owner.getCurStat(3) - damage);
                                                                                owner.sendStat(3);
                                                                                owner.sendMessage("Ouch!");
                                                                                owner.informOfModifiedHits(owner);
                                                                        }
                                                                }
                                                        break;
                                                                
                                                        case 627:
                                                                if (owner.getX() == 598 && owner.getY() == 3581) {
                                                                        owner.sendMessage("You reach out and grab the rope swing");
                                                                        if (agilityFormulae(1)) {
                                                                                owner.teleport(597, 3585, false);
                                                                                owner.sendMessage("you hold on tight and manage to make it across");
                                                                        } else {
                                                                                int damage = (int)(owner.getCurStat(3) * 0.9);
                                                                                owner.setLastDamage(damage);
                                                                                owner.setCurStat(3, owner.getCurStat(3) - damage);
                                                                                owner.sendStat(3);
                                                                                owner.sendMessage(" and fall into the pit below!");
                                                                                owner.sendMessage("Ouch!");
                                                                                owner.informOfModifiedHits(owner);
                                                                        }
                                                                }
                                                        break;
                                                                
                                                        case 628: // Yanille Rope Swing
                                                                if (owner.getX() == 596 && owner.getY() == 3585) {
                                                                        owner.sendMessage("You reach out and grab the rope swing");
                                                                        if (agilityFormulae(1)) {
                                                                                owner.teleport(596, 3581, false);
                                                                                owner.sendMessage("you hold on tight and manage to make it across");
										owner.increaseXP(Skills.AGILITY, 110);
                                                                        } else {
                                                                                int damage = (int)(owner.getCurStat(3) * 0.9);
                                                                                owner.setLastDamage(damage);
                                                                                owner.setCurStat(3, owner.getCurStat(3) - damage);
                                                                                owner.sendStat(3);
                                                                                owner.sendMessage("and fall into the pit below!");
                                                                                owner.sendMessage("Ouch!");
                                                                                owner.informOfModifiedHits(owner);
                                                                        }
                                                                }
                                                        break;
                                                                
                                                        case 706:
                                                                if (owner.getX() == 292 && owner.getY() == 111) {
                                                                        owner.sendMessage("You reach out and grab the rope swing");
                                                                        if (agilityFormulae(1)) {
                                                                                owner.teleport(292, 108, false);
                                                                                owner.sendMessage("you hold on tight and manage to make it across");
                                                                        } else {
                                                                                owner.teleport(293, 2942, false);
                                                                                int damage = (int)(owner.getCurStat(3) * 0.9);
                                                                                owner.setLastDamage(damage);
                                                                                owner.setCurStat(3, owner.getCurStat(3) - damage);
                                                                                owner.sendStat(3);
                                                                                owner.sendMessage("You slip and fall");
                                                                                owner.sendMessage("Ouch!");
                                                                                owner.informOfModifiedHits(owner);
                                                                        }
                                                                }
                                                        break;

                                                        case 695: // Brimhaven Moss Giant Swing
                                                                if (owner.getX() > 510 && owner.getX() < 513) {
                                                                        owner.sendMessage("You skillfully swing across the stream");
                                                                        owner.addMessageToChatQueue("Aaaaahahah");
                                                                        owner.teleport(508, 669, false);
                                                                        owner.increaseXP(Skills.AGILITY, 23);
                                                                }
                                                                break;
                                                        case 694: // Brimhaven Moss Giant Swing
                                                                if (owner.getX() < 511 && owner.getX() > 507) {
                                                                        owner.sendMessage("You skillfully swing across the stream");
                                                                        owner.addMessageToChatQueue("Aaaaahahah");
                                                                        owner.teleport(512, 669, false);
                                                                        owner.increaseXP(Skills.AGILITY, 23);
                                                                }
                                                                break;

                                                        default:
                                                                owner.sendMessage("Nothing interesting happens");
                                                }
                                                owner.sendStat(16);
                                        }
                                        
                                        private void handleBalanceOnEvent() {
                                                switch (object.getID()) {
                                                        case 614: // Yanille Dungeon Ledge
                                                                if (owner.getX() == 601) {
                                                                        if (owner.getY() == 3557) {
                                                                                owner.sendMessage("You balance on the slippery ledge");
                                                                                if(agilityFormulae(1)) {
                                                                                        owner.teleport(601, 3563, false);
                                                                                        owner.sendMessage("and walk across.");
											owner.increaseXP(Skills.AGILITY, 90);
                                                                                } else {
                                                                                        owner.teleport(597, 3535, false);
                                                                                        int damage = (int)(owner.getCurStat(3) * 0.9);
                                                                                        owner.setLastDamage(damage);
                                                                                        owner.setCurStat(3, owner.getCurStat(3) - damage);
                                                                                        owner.sendStat(3);
                                                                                        owner.sendMessage(" and fall into the pit below!");
                                                                                        owner.informOfModifiedHits(owner);
                                                                                }
                                                                        } else if (owner.getY() == 3563) {
                                                                                owner.sendMessage("You balance on the slippery ledge");
                                                                                if (agilityFormulae(1)) {
                                                                                        owner.teleport(601, 3557, false);
                                                                                        owner.sendMessage("and walk across.");
                                                                                } else {
                                                                                        int damage = (int)(owner.getCurStat(3) * 0.9);
                                                                                        owner.setLastDamage(damage);
                                                                                        owner.setCurStat(3, owner.getCurStat(3) - damage);
                                                                                        owner.sendStat(3);
                                                                                        owner.sendMessage(" and fall into the pit below!");
                                                                                        owner.informOfModifiedHits(owner);
                                                                                }
                                                                        }
                                                                }
                                                                break;

                                                        case 680:
                                                                owner.sendMessage("You stand on the slippery log");
                                                                if (agilityFormulae(1)) {
                                                                        owner.teleport(598, 458, false);
                                                                        owner.sendMessage("and walk across");
                                                                                owner.increaseXP(Skills.AGILITY, 34);
                                                                } else {
                                                                        owner.teleport(597, 461, false);
                                                                        int damage = (int)(owner.getCurStat(3) * 0.9);
                                                                        owner.setLastDamage(damage);
                                                                        owner.setCurStat(3, owner.getCurStat(3) - damage);
                                                                        owner.sendStat(3);
                                                                        owner.sendMessage("You slip and fall");
                                                                        owner.sendMessage("You manage to make it out before you drown");
                                                                        owner.informOfModifiedHits(owner);
                                                                }
                                                                break;

                                                        case 692: // Karamja Log Bridge
                                                                owner.sendMessage("You attempt to walk over the slippery log..");
                                                                        if (owner.getX() == 367 && (owner.getY() <= 782 && owner.getY() >= 780)) // Move to proper position
                                                                                owner.teleport(367, 781, false);
                                                                        else if (owner.getX() == 369 && (owner.getY() <= 782 && owner.getY() >= 780))
                                                                                owner.teleport(369, 781, false);
                                                                        else if (owner.getY() == 781 && owner.getX() == 370)
                                                                                owner.teleport(369, 781, false);
                                                                        else if (owner.getY() == 781 && owner.getX() == 366)
                                                                                owner.teleport(367, 781, false);
                                                                if (agilityFormulae(1)) {
                                                                        owner.sendMessage("...and make it without any problems!");
                                                                        if (owner.getX() == 367) {
                                                                                        owner.teleport(368, 781, false);
                                                                                        owner.teleport(369, 781, false);
                                                                                owner.increaseXP(Skills.AGILITY, 34);
                                                                                } else if (owner.getX() == 369) {
                                                                                        owner.teleport(368, 781, false);
                                                                                        owner.teleport(367, 781, false);
                                                                                owner.increaseXP(Skills.AGILITY, 34);
                                                                                }
                                                                        } else {
                                                                                owner.teleport(366, 789, false);
                                                                                int damage = (int)(owner.getCurStat(3) * 0.9);
                                                                                owner.setLastDamage(damage);
                                                                                owner.setCurStat(3, owner.getCurStat(3) - damage);
                                                                                owner.sendStat(3);
                                                                                owner.sendMessage("You fall into the stream!");
                                                                                owner.sendMessage("You lose some health");
                                                                                owner.informOfModifiedHits(owner);
                                                                        }
                                                                break;
                                                                        
                                                        case 681: //Log near Sir Galahad's house, west of river
                                                                owner.sendMessage("You stand on the slippery log");
                                                                if (agilityFormulae(1)) {
                                                                        owner.teleport(592, 458, false);
                                                                        owner.sendMessage("and walk across");
                                                                } else {
                                                                        owner.teleport(597, 461, false);
                                                                        int damage = (int)(owner.getCurStat(3) * 0.9);
                                                                        owner.setLastDamage(damage);
                                                                        owner.setCurStat(3, owner.getCurStat(3) - damage);
                                                                        owner.sendStat(3);
                                                                        owner.sendMessage("You slip and fall");
                                                                        owner.sendMessage("You manage to make it out before you drown");
                                                                        owner.informOfModifiedHits(owner);
                                                                }
                                                                break;

                                                        case 701:
                                                                // Calculate the rock we are hopping to
                                                                int x = -1;
                                                                int y = -1;
                                                                if (object.getX() == 346 && object.getY() == 807) {
                                                                        x = 346;
                                                                        y = 807;
                                                                } else if (object.getX() == 347 && object.getY() == 806) {
                                                                        x = 347;
                                                                        y = 806;
                                                                }
                                                                if (x >= 0 && y >= 0) {
                                                                        owner.sendMessage("You carefully step to the rock...");
                                                                        if (agilityFormulae(1)) {
                                                                                owner.teleport(x, y, false);
                                                                                owner.sendMessage("...and successfully balance upon it");
                                                                                owner.increaseXP(Skills.AGILITY, 10);
                                                                        } else {
                                                                                owner.sendMessage("...but fall off of the rock and wash down stream");
                                                                                owner.teleport(342, 804, false);
                                                                                owner.increaseXP(Skills.AGILITY, 4);
                                                                        }
                                                                }
                                                                break;  
                                                                        
                                                        default:
                                                                owner.sendMessage("Nothing interesting happens");
                                                }
                                                owner.sendStat(16);
                                        }
                                        
                                        private void handleClimbEvent() {
                                                switch (object.getID()) {
                                                        case 393: // Tree Gnome Village Wall
                                                                Quest Tree_Gnome_Village = owner.getQuest(Quests.TREE_GNOME_VILLAGE);
                                                                switch (Tree_Gnome_Village.getStage())
                                                                {
                                                                        case 5:
                                                                                if (owner.getY() == 633)
                                                                                {
                                                                                        owner.sendMessage("You climb over the wall.");
                                                                                        owner.teleport(659, 632, false);
                                                                                }
                                                                                else
                                                                                if (owner.getY() == 631)
                                                                                {
                                                                                        owner.sendMessage("You climb over the wall.");
                                                                                        owner.teleport(659, 633, false);
                                                                                }
                                                                        break;
                                                                        default:
                                                                                owner.sendMessage("The wall is too high to climb over.");
                                                                        break;
                                                                }
                                                        break;
                                                        
                                                        case 448:
                                                                owner.sendMessage("You climb up the pile of mud");
                                                                owner.teleport(618, 580);
                                                                break;
                                                        
                                                        case 636:
                                                                if (owner.getX() == 582 && (owner.getY() == 3573 || owner.getY() == 3574)) {
                                                                        owner.teleport(580, 3525, false);
                                                                        owner.sendMessage("You look through the dirt pile");
                                                                        owner.sendMessage("and it reveals a secret passage!");
                                                                }
                                                                if (owner.getX() < 580 && owner.getY() < 3528 && owner.getX() > 579 && owner.getY() > 3523) {
                                                                        owner.teleport(582, 3573, false);
                                                                        owner.sendMessage("You look through the dirt pile");
                                                                        owner.sendMessage("and it reveals a secret passage!");
                                                                }
                                                                break;

                                                        case 710: // Karamja Rocks
                                                                if (owner.getX() == 450 || owner.getX() == 449) {
                                                                        owner.teleport(452, 828, false);
                                                                        owner.sendMessage("You climb up the rocks");
                                                                        owner.increaseXP(Skills.AGILITY, 10);
                                                                }
                                                                else if (owner.getX() == 452 ) {
                                                                        owner.teleport(449, 828, false);
                                                                        owner.sendMessage("You climb down the rocks");
                                                                        owner.increaseXP(Skills.AGILITY, 10);
                                                                }
                                                        
                                                        case 693: // Falador Handholds
                                                                if ((owner.getX() == 339 || owner.getX() == 340) && (owner.getY() == 554 || owner.getY() == 555 || owner.getY() == 556)) {
                                                                        owner.teleport(338, 555, false);
                                                                        owner.sendMessage("You grab hold of the handholds");
                                                                        owner.sendMessage("and climb over to the other side");
									owner.increaseXP(Skills.AGILITY, 50);
                                                                }
                                                                break;

                                                        case 633: // Yanille Rubble Pile
                                                                if(owner.getX() > 579 && owner.getX() < 582 && owner.getY() > 3523 && owner.getY() < 3528) {
                                                                        owner.teleport(582, 3573, false);
                                                                        owner.sendMessage("You climb up the pile of rubble");
                                                                        owner.increaseXP(Skills.AGILITY, 54);
                                                                }
                                                                break;

                                                        case 1029: // Yanille north climbing rocks
                                                                if (owner.getX() == 624) {
                                                                        if (owner.getY() == 743) {
                                                                                owner.teleport(624, 742, false);
                                                                        }
                                                                        if (owner.getY() == 742) {
                                                                                owner.teleport(624, 741, false);
                                                                                owner.sendMessage("You climb the rocks and scale the wall"); // XXX
                                                                                owner.increaseXP(Skills.AGILITY, 40);
                                                                        }
                                                                }
                                                                break;
                                                                
                                                        default:
                                                                owner.sendMessage("Nothing interesting happens.");
                                                }
                                                owner.sendStat(16);
                                        }
                                        
                                        private void handleEnterEvent() {
                                                switch (object.getID()) {
                                                        case 449: // Plague City Sewer.
                                                                if (!owner.getQuest(Quests.PLAGUE_CITY).finished() && owner.getQuest(Quests.PLAGUE_CITY).getStage() >= 6)
                                                                {
                                                                        if (owner.getInventory().wielding(766)) // Check for gas mask, Plague City
                                                                        {
                                                                                owner.sendMessage("You climb up the sewer");
                                                                                owner.teleport(632, 591);
                                                                        }
                                                                        else
                                                                        {
                                                                                owner.sendMessage("I'd better wear the gas mask before entering.");
                                                                                return;
                                                                        }
                                                                }
                                                                else
                                                                {
                                                                        owner.sendMessage("The grill is too secure, you can't pull it off alone");
                                                                        return;
                                                                }
                                                        break;
                                                        
                                                        case 656: // Yanille pipe
                                                                owner.teleport(608, 3568, false);
                                                                owner.sendMessage("You squeeze into the pipe");
                                                                owner.sendMessage("and shuffle down into it");
                                                                owner.increaseXP(Skills.AGILITY, 30);
                                                        break;
                                                        
                                                        case 657:
                                                                owner.teleport(605, 3568, false);
                                                                owner.sendMessage("You squeeze into the pipe");
                                                                owner.sendMessage("and shuffle down into it");
                                                        break;
                                                        
                                                        case 982: //Goblin Hideout Entrance
                                                                owner.teleport(577, 3354);
                                                                owner.sendMessage("you cautiously enter the cave");
                                                        break;
                                                        
                                                        case 671:
                                                                owner.teleport(487, 554, false);
                                                                owner.sendMessage("You squeeze into the pipe");
                                                                owner.sendMessage("and shuffle down into it");
                                                        break;
                                                        
                                                        case 672:
                                                                owner.teleport(487, 551, false);
                                                                owner.sendMessage("You squeeze into the pipe");
                                                                owner.sendMessage("and shuffle down into it");
                                                        break;
                                                        
                                                        default:
                                                                owner.sendMessage("Nothing interesting happens.");
                                                }
                                                owner.sendStat(16);
                                        }

                                        private void handleJumpEvent() {
                                                switch (object.getID()) {
                                                        case 691: // Karamja Fence
                                                                if (owner.getX() == 458 && owner.getY() == 828) {
                                                                        owner.sendMessage("You prepare to negotiate the bridge fence...");
                                                                        owner.teleport(457, 828, false);
                                                                        owner.sendMessage("You run and jump...");
                                                                        owner.teleport(458, 828, false);
                                                                        owner.teleport(459, 828, false); // Successful jump
                                                                        owner.sendMessage("...and land perfectly on the other side!");
                                                                        //owner.teleport(452, 836, false); // Failed Jump TODO
                                                                }
                                                                else if (owner.getX() == 459 && owner.getY() == 828) {
                                                                        owner.sendMessage("You prepare to negotiate the bridge fence...");
                                                                        owner.teleport(460, 828, false);
                                                                        owner.sendMessage("You run and jump...");
                                                                        owner.teleport(459, 828, false);
                                                                        owner.teleport(458, 828, false); // Successful jump
                                                                        owner.sendMessage("...and land perfectly on the other side!");
                                                                        //owner.teleport(452, 836, false); // Failed Jump TODO
                                                                }
                                                                break;

                                                }
                                                owner.sendStat(16);
                                        }
                                        
                                        private void handleInspect() {
                                                switch (object.getID()) {
                                                        case 124: //Lever A
                                                                owner.sendMessage("The lever is " + (owner.leverADown() ? "down" : "up"));
                                                        break;
                                                        
                                                        case 125: //Lever B
                                                                owner.sendMessage("The lever is " + (owner.leverBDown() ? "down" : "up"));
                                                        break;
                                                        
                                                        case 126:
                                                                owner.sendMessage("The lever is " + (owner.leverCDown() ? "down" : "up"));
                                                        break;
                                                        
                                                        case 127:
                                                                owner.sendMessage("The lever is " + (owner.leverDDown() ? "down" : "up"));
                                                        break;
                                                        
                                                        case 128:
                                                                owner.sendMessage("The lever is " + (owner.leverEDown() ? "down" : "up"));
                                                        break;
                                                        
                                                        case 129:
                                                                owner.sendMessage("The lever is " + (owner.leverFDown() ? "down" : "up"));
                                                        break;
                                                        
                                                        case 994:
                                                                owner.setBusy(true);
                                                                owner.sendMessage("You inspect the multi cannon");
                                                                if(!owner.isCannonFixed()) {
                                                                        World.getDelayedEventHandler().add(new SingleEvent(owner, 1500) {
                                                                                public void action() {
                                                                                        if (new java.util.Random().nextInt(3) != 0) {
                                                                                                owner.sendMessage("You see that there are some damaged components");
                                                                                                World.getDelayedEventHandler().add(new SingleEvent(owner, 1500) {
                                                                                                        public void action() {
                                                                                                                final String[] brokenParts = owner.getBrokenCannonParts();
                                                                                                                String message = "";
                                                                                                                for (String s : brokenParts) {
                                                                                                                        if (s.toLowerCase().equals("pipe"))
                                                                                                                                message += "a pipe, ";
                                                                                                                        else if(s.toLowerCase().equals("barrel"))
                                                                                                                                message += "a gun barrel, ";
                                                                                                                        else if(s.toLowerCase().equals("shaft"))
                                                                                                                                message += "a shaft, ";
                                                                                                                        else if(s.toLowerCase().equals("axle"))
                                                                                                                                message += "an axle, ";
                                                                                                                }
                                                                                                                owner.sendMessage(message.substring(0, message.length() - 2) + " seem to be damaged");
                                                                                                                World.getDelayedEventHandler().add(new SingleEvent(owner, 1500) {
                                                                                                                        public void action() {
                                                                                                                                owner.sendMessage("which part of the cannon will you attempt to fix?");
                                                                                                                                World.getDelayedEventHandler().add(new SingleEvent(owner, 1500) {
                                                                                                                                        public void action() {
                                                                                                                                                owner.setBusy(false);
                                                                                                                                                owner.setMenuHandler(new MenuHandler(brokenParts) {
                                                                                                                                                        @Override
                                                                                                                                                        public void handleReply(int option, String reply) {
                                                                                                                                                                owner.setBusy(true);
                                                                                                                                                                owner.fixPart(reply);
                                                                                                                                                        }
                                                                                                                                                });
                                                                                                                                                owner.sendMenu(brokenParts);
                                                                                                                                        }
                                                                                                                                });
                                                                                                                        }
                                                                                                                });
                                                                                                        }
                                                                                                });
                                                                                        } else {
                                                                                                World.getDelayedEventHandler().add(new SingleEvent(owner, 1500) {
                                                                                                        public void action() {
                                                                                                                owner.sendMessage("you try, but can't quite find the problem");
                                                                                                                World.getDelayedEventHandler().add(new SingleEvent(owner, 1500) {
                                                                                                                        public void action() {
                                                                                                                                owner.sendMessage("maybe you should inspect it again");
                                                                                                                                owner.setBusy(false);
                                                                                                                        }
                                                                                                                });
                                                                                                        }
                                                                                                });
                                                                                        }                                                                                               
                                                                                }
                                                                        });
                                                                } else {
                                                                        owner.sendMessage("The cannon seems to be in complete working order");
                                                                        owner.setBusy(false);
                                                                }
                                                        break;
                                                        
                                                        default:
                                                                owner.sendMessage("Nothing interesting happens.");
                                                }
                                        }
                                        
                                        private boolean agilityFormulae(int temp) {
                                                return true;
                                        }
                                        
                                        private void replaceGameObject(int newID, boolean open) {
                                                World.registerEntity(new GameObject(object.getLocation(), newID, object.getDirection(), object.getType()));
                                                owner.sendSound(open ? "opendoor" : "closedoor", false);
                                        }
                                        
                                        private void replaceGameObject(GameObject oldObject, GameObject newObject) {
                                                World.registerEntity(newObject);
                                        }
                                        
                                        private void doGate() {
                                                owner.sendSound("opendoor", false);
                                                World.registerEntity(new GameObject(object.getLocation(), 181, object.getDirection(), object.getType()));
                                                World.delayedSpawnObject(object.getLoc(), 1000);
                                        }
                                        
                                        private void doFence() 
                                        {
                                                owner.sendSound("opendoor", false);
                                                World.registerEntity(new GameObject(object.getLocation(), 442, object.getDirection(), object.getType()));
                                                World.delayedSpawnObject(object.getLoc(), 1000);
                                        }
                                        
                                        private int[] coordModifier(Player player, boolean up) {
                                                if (object.getGameObjectDef().getHeight() <= 1)
                                                        return new int[]{player.getX(), Formulae.getNewY(player.getY(), up)};
                                                int[] coords = {object.getX(), Formulae.getNewY(object.getY(), up)};
                                                switch (object.getDirection()) {
                                                        case 0:
                                                                coords[1] -= (up ? -object.getGameObjectDef().getHeight() : 1);
                                                        break;
                                                                
                                                        case 2:
                                                                coords[0] -= (up ? -object.getGameObjectDef().getHeight() : 1);
                                                        break;
                                                                
                                                        case 4:
                                                                coords[1] += (up ? -1 : object.getGameObjectDef().getHeight());
                                                        break;
                                                                
                                                        case 6:
                                                                coords[0] += (up ? -1 : object.getGameObjectDef().getHeight());
                                                        break;
                                                }
                                                return coords;
                                        }
                                });
                        } else
                                player.resetPath();
                }
        }
}
