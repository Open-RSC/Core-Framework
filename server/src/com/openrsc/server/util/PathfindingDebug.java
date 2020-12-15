package com.openrsc.server.util;

import com.openrsc.server.model.Point;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.model.world.region.TileValue;
import com.openrsc.server.util.rsc.CollisionFlag;

import javax.swing.*;
import java.awt.*;
import java.util.Iterator;

public class PathfindingDebug {
	private final World world;
	private final PathfindingDebugPanel panel;
	private final JFrame frame;
	private final GroupLayout layout;

	public PathfindingDebug(final World world) {
		this.world = world;

		panel = new PathfindingDebugPanel();
		frame = new JFrame();
		layout = new GroupLayout(panel);

		panel.setLayout(layout);
		frame.add(panel);
		frame.setSize(600, 600);
		frame.setVisible(true);
	}

	public void destroy() {
		frame.dispose();
	}

	public World getWorld() {
		return world;
	}

	class PathfindingDebugPanel extends JPanel {
		int size = 15;
		int width = 20;
		Color[][] board = null;

		public PathfindingDebugPanel() {
			// set a preferred size for the custom panel.
			setPreferredSize(new Dimension(420, 420));
		}

		private void setTile(int x, int y, Color color) {
			if (x < 0 || x >= 2 * size + 1)
				return;
			if (y < 0 || y >= 2 * size + 1)
				return;
			this.board[x][y] = color;
		}

		private void drawBorder(int x, int y, Graphics g) {
			g.setColor(Color.black);
			g.drawRect(x * width, y * width, width, width);
		}

		private void drawBlocks(int x, int y, TileValue tile, Graphics g) {
			x *= width;
			y *= width;
			if ((tile.traversalMask & (CollisionFlag.FULL_BLOCK_A | CollisionFlag.FULL_BLOCK_B | CollisionFlag.FULL_BLOCK_C)) != 0) {
				g.fillRect(x, y, width, width);
				return;
			}
			g.setColor(Color.red);
			if ((tile.traversalMask & CollisionFlag.EAST_BLOCKED) != 0) {
				g.fillRect(x + width - 4, y + 1, 3, width);
			}
			if ((tile.traversalMask & CollisionFlag.WEST_BLOCKED) != 0) {
				g.fillRect(x + 1, y + 1, 3, width);
			}
			if ((tile.traversalMask & CollisionFlag.NORTH_BLOCKED) != 0) {
				g.fillRect(x, y + 1, width, 3);
			}
			if ((tile.traversalMask & CollisionFlag.SOUTH_BLOCKED) != 0) {
				g.fillRect(x, y + width - 4, width, 3);
			}
		}

		private void drawPath(Mob mob, Graphics g) {
			if (mob.getWalkingQueue() != null && mob.getWalkingQueue().path != null && mob.getWalkingQueue().path.size() > 0) {
				Iterator<com.openrsc.server.model.Point> path = mob.getWalkingQueue().path.iterator();
				if (mob.isPlayer()) {
					g.setColor(Color.BLUE);
				} else {
					g.setColor(Color.ORANGE);
				}
				while (path.hasNext()) {
					Point next = path.next();
					if (mob.isPlayer())
						g.fillRect(((mob.getX() + size) - next.getX()) * width, (next.getY() - (mob.getY() - size)) * width, width, width);
					else
						g.fillRect(((((Npc) mob).getBehavior().getChaseTarget().getX() + size) - next.getX()) * width, (next.getY() - (((Npc) mob).getBehavior().getChaseTarget().getY() - size)) * width, width, width);
				}
			}
		}

		@Override
		public void paintComponent(Graphics g) {
			board = new Color[2 * size + 1][2 * size + 1];
			super.paintComponent(g);
			if (getWorld().getPlayers().size() > 0) {
				Player test = getWorld().getPlayers().get(0);
				int centerx = test.getX();
				int centery = test.getY();

				for (int x = -size; x <= size; x++) {
					for (int y = -size; y <= size; y++) {
						drawBorder(x + size, y + size, g);
						TileValue tile = getWorld().getTile(centerx - x, centery + y);
						if (tile == null) {
							continue;
						}
						drawBlocks(x + size, y + size, tile, g);

					}
				}

				g.setColor(Color.pink);
				g.fillRect(size * width, size * width, width, width);
				drawPath(test, g);
				for (Npc npc : getWorld().getNpcs()) {
					if (npc.isChasing()) {
						g.setColor(Color.red);
						g.fillRect(((centerx + size) - npc.getX()) * width, (npc.getY() - (centery - size)) * width, width, width);
						drawPath(npc, g);
					}

				}
			}
		}
	}
}
