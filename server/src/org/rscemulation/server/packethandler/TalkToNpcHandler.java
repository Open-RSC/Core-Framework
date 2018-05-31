package org.rscemulation.server.packethandler;

import org.apache.mina.common.IoSession;
import org.rscemulation.server.event.SingleEvent;
import org.rscemulation.server.event.WalkToMobEvent;
import org.rscemulation.server.model.Npc;
import org.rscemulation.server.model.Path;
import org.rscemulation.server.model.Player;
import org.rscemulation.server.model.World;
import org.rscemulation.server.net.Packet;
import org.rscemulation.server.npchandler.NpcHandler;
import org.rscemulation.server.states.Action;

import com.rscdaemon.scripting.ScriptCache;
import com.rscdaemon.scripting.ScriptError;
import com.rscdaemon.scripting.ScriptVariable;
import com.rscdaemon.scripting.listener.TalkToNpcListener;

// All pimped out talktonpchandler.
public class TalkToNpcHandler implements PacketHandler {

	private final ScriptCache<TalkToNpcListener> scriptCache = new ScriptCache<>();
	
	private final static int[][] choices = new int[][]
	{
		{1, -1}, {0, -1}, {-1, -1}, {1, 0}, {-1, 0}, {1, 1}, {0, 1}, {-1, 1}, {0, 0}
	};
	
	private void fireEvent(Player owner, Npc npc)
	{
		if(!owner.getLocation().equals(npc.getLocation()))
		{
			npc.updateSprite(owner.getX(), owner.getY());
			owner.updateSprite(npc.getX(), npc.getY());
		}
		// No events should fire if there is an active script
		if(owner.getScript() != null)
		{
			return;
		}
		// Try to retrieve a script from the cache
		TalkToNpcListener script = scriptCache.get(npc.getID());		
		try
		{
			// If the script was found in the cache, try to run it
			if(script != null)
			{
				script = script.getClass().newInstance();
				script.Bind(ScriptVariable.OWNER, owner);
				script.Bind(ScriptVariable.NPC_TARGET, npc);
				owner.setScript(script);
				if(script.onTalkToNpc(owner, npc))
				{
					script.run();
					return;
				}
			}
			
			// If the script wasn't ran, search for one.
			for(TalkToNpcListener listener : World.getScriptManager().<TalkToNpcListener>getListeners(TalkToNpcListener.class))
			{
				script = listener.getClass().newInstance();
				script.Bind(ScriptVariable.OWNER, owner);
				script.Bind(ScriptVariable.NPC_TARGET, npc);
				owner.setScript(script);
				if(script.onTalkToNpc(owner, npc))
				{
					script.run();
					scriptCache.put(npc.getID(), listener);
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
		NpcHandler npcHandler = World.getNpcHandler(npc.getID());
		if (npcHandler != null)
		{
			try
			{
				npcHandler.handleNpc(npc, owner);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public void handlePacket(Packet p, IoSession session) throws Exception {
		Player player = (Player)session.getAttachment();
		if (player != null) {
			if (player.isBusy()) {
				player.resetPath();
				return;
			}
			player.resetAllExceptDMing();
			int index = p.readShort();
			final Npc affectedNpc = World.getNpc(index);
			if (affectedNpc == null)
				return;
			player.setFollowing(affectedNpc);
			player.setStatus(Action.TALKING_MOB);
			World.getDelayedEventHandler().add(new WalkToMobEvent(player, affectedNpc, 1) {
				public void failed() {
					owner.resetFollowing();
				}
				public void arrived() {
					owner.resetFollowing();
					owner.resetPath();
					if (owner.isBusy() || owner.isRanging() || !owner.nextTo(affectedNpc) || owner.getStatus() != Action.TALKING_MOB)
						return;
					owner.resetAllExceptDMing();
					if (affectedNpc.isBusy()) {
						owner.sendMessage(affectedNpc.getDef().getName() + " is busy at the moment");
						return;
					}
					affectedNpc.resetPath();
					if(owner.getLocation().equals(affectedNpc.getLocation()))
					{
						int i = 0;
						int[] coords = {-1, -1};
						do
						{
							coords = affectedNpc.getPathHandler().getNextCoords(affectedNpc.getX(), affectedNpc.getX() + choices[i][0], affectedNpc.getY(), affectedNpc.getY() + choices[i++][1]);
						}
						while(
							!(i == choices.length ||
							coords[0] != -1 && 
							coords[1] != -1 && 
							coords[0] >= affectedNpc.getLoc().minX &&
							coords[0] <= affectedNpc.getLoc().maxX &&
							coords[1] >= affectedNpc.getLoc().minY &&
							coords[1] <= affectedNpc.getLoc().maxY)
						);
						if(i < choices.length)
						{
							affectedNpc.setPath(new Path(affectedNpc.getX(), affectedNpc.getY(), coords[0], coords[1]));
						}
						World.getDelayedEventHandler().add(
							new SingleEvent(owner, 1000)
						{
							public void action() {
								if(!owner.isBusy()) // Some drunk coded this part and screwed up.
								{
									fireEvent(owner, affectedNpc);
								}
							}
						});		
					}
					else
					{
						fireEvent(owner, affectedNpc);
					}
				}
			});
		}
	}
}