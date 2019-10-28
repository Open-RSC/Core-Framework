package com.openrsc.server.model;

import com.openrsc.server.model.world.World;
import com.openrsc.server.model.world.region.TileValue;
import com.openrsc.server.util.rsc.CollisionFlag;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class AStarPathfinder {

	boolean debug = false;
	//JPanel2 panel = new JPanel2();
	//JFrame frame = new JFrame();
	//javax.swing.GroupLayout layout = new javax.swing.GroupLayout(panel);
	final static int basicCost = 10;
	final static int diagCost = 14;
	private int depth;
	private Node[][] costBoard = null;
	private Path bestPath = null;
	private Point worldStart;
	private Point pointStart;
	private Point pointEnd;
	private Point currentPosition;
	long starttime;
	long endtime;
	private Path path;
	private ArrayList<Node> openNodes = new ArrayList<>();
	private ArrayList<Node> closedNodes = new ArrayList<>();

	public AStarPathfinder(World world, Point start, Point end, int depth) {
		this.worldStart = start;
		this.pointStart = new Point(depth,depth);
		this.pointEnd = new Point((start.getX()+depth)-end.getX(), end.getY() - (start.getY() - depth));
		this.depth = depth;
		this.generateTraversalInfo(world, start, depth);
		/*
		if (debug) {
			panel.setLayout(layout);
			frame.add(panel);
			frame.setSize(600, 600);
			frame.setVisible(true);
		}
		 */
	}

	public void feedPath(Path path) {
		this.path = path;
	}
	public void generateTraversalInfo(World world, Point center, int depth) {
		if (depth < 1)
			return;

		costBoard = new Node[2*depth + 1][2*depth + 1];
		initBoard(costBoard);
		int curposx, curposy;
		for (int x = -depth; x <= depth; x++) {
			for (int y = -depth; y <= depth; y++) {
				TileValue tile = world.getTile(center.getX() - x, center.getY() + y);
				if (tile == null) {
					continue;
				}
				curposx = x + depth;
				curposy = y + depth;

				if ((tile.traversalMask & (CollisionFlag.FULL_BLOCK_A | CollisionFlag.FULL_BLOCK_B | CollisionFlag.FULL_BLOCK_C)) != 0) {
					if (y < depth) {
						costBoard[curposx][curposy+1].northBlocked = true;
					}
					if (x > -depth) {
						costBoard[curposx-1][curposy].eastBlocked = true;
					}
					if (y > -depth) {
						costBoard[curposx][curposy-1].southBlocked = true;
					}
					if (x < depth) {
						costBoard[curposx+1][curposy].westBlocked = true;
					}
				} else {
					if (!costBoard[curposx][curposy].southBlocked)
						costBoard[curposx][curposy].southBlocked = (tile.traversalMask & CollisionFlag.SOUTH_BLOCKED) != 0;
					if (!costBoard[curposx][curposy].westBlocked)
						costBoard[curposx][curposy].westBlocked = (tile.traversalMask & CollisionFlag.WEST_BLOCKED) != 0;
					if (!costBoard[curposx][curposy].northBlocked)
						costBoard[curposx][curposy].northBlocked = (tile.traversalMask & CollisionFlag.NORTH_BLOCKED) != 0;
					if (!costBoard[curposx][curposy].eastBlocked)
						costBoard[curposx][curposy].eastBlocked = (tile.traversalMask & CollisionFlag.EAST_BLOCKED) != 0;
				}
			}
		}
	}

	private int calcDistance(Point one, Point two) {
		int xdiff = Math.abs(one.getX() - two.getX());
		int ydiff = Math.abs(one.getY() - two.getY());

		int shortL = xdiff > ydiff ? ydiff : xdiff;
		int longL = xdiff > ydiff ? xdiff : ydiff;

		return shortL * diagCost + (longL - shortL) * basicCost;
	}

	public Node findNextNode() {
		int minimum = Integer.MAX_VALUE;
		Node minNode = null;
		for (Node node : openNodes) {
			if (node.hCost < minimum) {
				minimum = node.hCost;
				minNode = node;
			} else if (node.hCost == minimum && node.fCost < minNode.fCost)
				minNode = node;

		}
		return minNode;
	}

	private Path buildPath() {
		Point parent = closedNodes.get(closedNodes.size()-1).parent;
		Node endNode = costBoard[parent.getX()][parent.getY()];
		while (endNode != null) {
			int worldX = worldStart.getX() + depth - endNode.position.getX();
			int worldY = worldStart.getY() - depth + endNode.position.getY();
			if (endNode.parent == null)
				endNode = null;
			else {
				path.addDirect(worldX, worldY);
				endNode = costBoard[endNode.parent.getX()][endNode.parent.getY()];
			}

		}
		return path;
	}

	public Path findPath() {
		if (depth < 1)
			return null;

		if (pointStart.getX() == pointEnd.getX()
		&& pointStart.getY() == pointEnd.getY())
			return null;

		boolean quit = false;
		starttime = System.currentTimeMillis();
		costBoard[depth][depth].selectNode();

		while (true) {
			Node next = findNextNode();
			endtime = System.currentTimeMillis();
			if (next == null)
				return null;

			if (next.position.getX() == pointEnd.getX()
			&& next.position.getY() == pointEnd.getY()) {
				closedNodes.add(next);
				return buildPath();
			}


			next.selectNode();
			if (debug) {
			//	panel.repaint();

				/*
				// We should no longer put threads to sleep
				try {
					Thread.sleep(100);
				} catch (InterruptedException a) {
					a.printStackTrace();
				}*/

			}
		}
	}

	private void initBoard(Node[][] board) {
		if (board == null)
			return;

		for(int i = 0; i < board[0].length; i++) {
			for (int j = 0; j < board[0].length; j++) {
				board[i][j] = new Node(i,j);
			}
		}
	}

	public boolean diagBlocked(Node node, adjacent_direction dir) {
		Node neighbor1 = null;
		Node neighbor2 = null;
		if (dir == adjacent_direction.SOUTHWEST) {
			neighbor1 = node.getNeighbor(adjacent_direction.WEST);
			if (neighbor1 != null) {
				neighbor2 = node.getNeighbor(adjacent_direction.SOUTH);
				if (neighbor2 != null) {
					if (!neighbor1.southBlocked && !neighbor2.westBlocked)
						return false;
				}
			}
		} else if (dir == adjacent_direction.NORTHWEST) {
			neighbor1 = node.getNeighbor(adjacent_direction.WEST);
			if (neighbor1 != null) {
				neighbor2 = node.getNeighbor(adjacent_direction.NORTH);
				if (neighbor2 != null) {
					if (!neighbor1.northBlocked && !neighbor2.westBlocked)
						return false;
				}
			}
		} else if (dir == adjacent_direction.NORTHEAST) {
			neighbor1 = node.getNeighbor(adjacent_direction.EAST);
			if (neighbor1 != null) {
				neighbor2 = node.getNeighbor(adjacent_direction.NORTH);
				if (neighbor2 != null) {
					if (!neighbor1.northBlocked && !neighbor2.eastBlocked)
						return false;
				}
			}
		} else if (dir == adjacent_direction.SOUTHEAST) {
			neighbor1 = node.getNeighbor(adjacent_direction.EAST);
			if (neighbor1 != null) {
				neighbor2 = node.getNeighbor(adjacent_direction.SOUTH);
				if (neighbor2 != null) {
					if (!neighbor1.southBlocked && !neighbor2.eastBlocked)
						return false;
				}
			}
		}
		return true;
	}

	public long getRunTime() { return endtime - starttime; }

	public class Node {
		int fCost, gCost, hCost;
		node_state state;
		boolean southBlocked = false;
		boolean northBlocked = false;
		boolean westBlocked = false;
		boolean eastBlocked = false;
		public Point position;
		Point parent;

		public Node(int x, int y) {
			position = new Point(x,y);
			fCost = 0;
			gCost = 0;
			hCost = 0;
			state = node_state.INIT;
		}

		public void setState(node_state state) {
			this.state = state;
		}

		public void update(Node node, int cost) {
			if (this.state == node_state.INIT) {
				this.setState(node_state.OPEN);
				this.fCost = node.fCost + cost;
				calcGCost();
				openNodes.add(this);
			} else if (this.state == node_state.CLOSED)
				return;
			else {
				int newFcost = node.fCost + cost;
				if (newFcost > this.fCost)
					return;
				this.fCost = newFcost;
			}
			calcHCost();
			this.parent = node.position;
		}

		public void calcGCost() {
			gCost = calcDistance(this.position, pointEnd);
		}

		public void calcHCost() {
			hCost = fCost + gCost;
		}

		public Node getNeighbor(adjacent_direction dir) {
			switch (dir) {
				case SOUTH:
					if (position.getY() < 2*depth)
						return costBoard[position.getX()][position.getY()+1];
					else
						return null;
				case SOUTHWEST:
					if (position.getY() < 2*depth && position.getX() > 0)
						return costBoard[position.getX()-1][position.getY()+1];
					else
						return null;
				case WEST:
					if (position.getX() > 0)
						return costBoard[position.getX()-1][position.getY()];
					else
						return null;
				case NORTHWEST:
					if (position.getY() > 0 && position.getX() > 0)
						return costBoard[position.getX()-1][position.getY()-1];
					else
						return null;
				case NORTH:
					if (position.getY() > 0)
						return costBoard[position.getX()][position.getY()-1];
					else
						return null;
				case NORTHEAST:
					if (position.getY() > 0 && position.getX() < 2*depth)
						return costBoard[position.getX()+1][position.getY()-1];
					else
						return null;
				case EAST:
					if (position.getX() < 2*depth)
						return costBoard[position.getX()+1][position.getY()];
					else
						return null;
				case SOUTHEAST:
					if (position.getY() < 2*depth && position.getX() < 2*depth)
						return costBoard[position.getX()+1][position.getY()+1];
					else
						return null;
			}
			return null;
		}

		private void selectNode() {
			if (state == node_state.OPEN)
				openNodes.remove(this);

			setState(node_state.CLOSED);
			closedNodes.add(this);
			Node neighbor = null;
			//South to SouthEast
			if (!southBlocked && (neighbor = getNeighbor(adjacent_direction.SOUTH)) != null)
				neighbor.update(this, basicCost);
			if (!westBlocked && (neighbor = getNeighbor(adjacent_direction.WEST)) != null)
				neighbor.update(this, basicCost);
			if (!northBlocked && (neighbor = getNeighbor(adjacent_direction.NORTH)) != null)
				neighbor.update(this, basicCost);
			if (!eastBlocked && (neighbor = getNeighbor(adjacent_direction.EAST)) != null)
				neighbor.update(this, basicCost);
			if (!(southBlocked || westBlocked) && !diagBlocked(this, adjacent_direction.SOUTHWEST)
				&& (neighbor = getNeighbor(adjacent_direction.SOUTHWEST)) != null)
				neighbor.update(this, diagCost);
			if (!(northBlocked || westBlocked) && !diagBlocked(this, adjacent_direction.NORTHWEST)
				&& (neighbor = getNeighbor(adjacent_direction.NORTHWEST)) != null)
				neighbor.update(this, diagCost);
			if (!(northBlocked || eastBlocked) && !diagBlocked(this, adjacent_direction.NORTHEAST)
				&& (neighbor = getNeighbor(adjacent_direction.NORTHEAST)) != null)
				neighbor.update(this, diagCost);
			if (!(southBlocked || eastBlocked) && !diagBlocked(this, adjacent_direction.SOUTHEAST)
				&& (neighbor = getNeighbor(adjacent_direction.SOUTHEAST)) != null)
				neighbor.update(this, diagCost);
		}
	}


	public enum node_state{
		INIT,
		OPEN,
		CLOSED
	}

	public enum adjacent_direction{
		SOUTH,
		SOUTHWEST,
		WEST,
		NORTHWEST,
		NORTH,
		NORTHEAST,
		EAST,
		SOUTHEAST
	}

	/*debug code*/
	class JPanel2 extends JPanel {
		int width = 20;
		JPanel2() {
		}


		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.setFont(new Font("Arial",1,10));
			for (Node[] nodes : costBoard) {
				for (Node node : nodes) {
					g.setColor(Color.black);
					g.drawRect(node.position.getX()*width, node.position.getY()*width, width, width);
					g.setColor(Color.red);
					if (node.southBlocked)
						g.fillRect(node.position.getX()*width, node.position.getY()*width+width - 4,width,3);
					if (node.eastBlocked)
						g.fillRect(node.position.getX()*width + width - 4, node.position.getY()*width,3,width);
					if (node.northBlocked)
						g.fillRect(node.position.getX()*width, node.position.getY()*width+1,width,3);
					if (node.westBlocked)
						g.fillRect(node.position.getX()*width+1, node.position.getY()*width,3,width);
					if (node.state == node_state.OPEN) {
						g.setColor(Color.green);
						g.drawString("" + node.hCost, node.position.getX()*width, node.position.getY()*width + width/2);
					} else if (node.state == node_state.CLOSED) {
						g.setColor(Color.red);
						g.drawString("" + node.hCost, node.position.getX()*width, node.position.getY()*width + width/2);
					}
				}
			}
			Node guy = closedNodes.get(closedNodes.size()-1);
			g.setColor(Color.ORANGE);
			while (guy != null) {
				g.fillRect(guy.position.getX() * width, guy.position.getY()*width, 5,5);
				if (guy.parent == null)
					guy = null;
				else
					guy = costBoard[guy.parent.getX()][guy.parent.getY()];
			}
		}
	}
}
