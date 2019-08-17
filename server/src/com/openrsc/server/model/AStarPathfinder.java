package com.openrsc.server.model;

import com.openrsc.server.model.world.World;
import com.openrsc.server.model.world.region.TileValue;
import com.openrsc.server.util.rsc.CollisionFlag;

import java.util.ArrayList;

public class AStarPathfinder {

	final static int basicCost = 10;
	final static int diagCost = 14;
	private int depth;
	private Node[][] costBoard = null;
	private Path bestPath = null;
	private Point pointStart;
	private Point pointEnd;
	private Point currentPosition;

	private ArrayList<Node> openNodes = new ArrayList<>();
	private ArrayList<Node> closedNodes = new ArrayList<>();

	public AStarPathfinder(World world, Point start, Point end, int depth) {
		this.pointStart = new Point(depth,depth);
		this.pointEnd = new Point((start.getX()+depth)-end.getX(), end.getY() - (start.getY() - depth));
		this.depth = depth;
		this.generateTraversalInfo(world, start, depth);
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

				costBoard[curposx][curposy].southBlocked = (tile.traversalMask & CollisionFlag.SOUTH_BLOCKED) != 0;
				costBoard[curposx][curposy].westBlocked = (tile.traversalMask & CollisionFlag.WEST_BLOCKED) != 0;
				costBoard[curposx][curposy].northBlocked = (tile.traversalMask & CollisionFlag.NORTH_BLOCKED) != 0;
				costBoard[curposx][curposy].eastBlocked = (tile.traversalMask & CollisionFlag.EAST_BLOCKED) != 0;

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
			}
		}
		return minNode;
	}

	private Path buildPath() {
		return null;
	}

	public Path findPath() {
		if (depth < 1)
			return null;
		boolean quit = false;

		costBoard[depth][depth].selectNode();

		while (true) {
			Node next = findNextNode();
			if (next == null)
				return null;

			if (next.position.getX() == pointEnd.getX()
			&& next.position.getY() == pointEnd.getY())
				return buildPath();

			next.selectNode();
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

	public class Node {
		int fCost, gCost, hCost;
		node_state state;
		boolean southBlocked = false;
		boolean northBlocked = false;
		boolean westBlocked = false;
		boolean eastBlocked = false;
		Point position;

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
				openNodes.add(this);
			} else if (this.state == node_state.CLOSED)
				return;

			this.fCost = node.fCost + cost;
			calcGCost();
			calcHCost();
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
			if (!southBlocked && !westBlocked && (neighbor = getNeighbor(adjacent_direction.SOUTHWEST)) != null)
				neighbor.update(this, diagCost);
			if (!northBlocked && !westBlocked && (neighbor = getNeighbor(adjacent_direction.NORTHWEST)) != null)
				neighbor.update(this, diagCost);
			if (!northBlocked && !eastBlocked && (neighbor = getNeighbor(adjacent_direction.NORTHEAST)) != null)
				neighbor.update(this, diagCost);
			if (!southBlocked && !eastBlocked && (neighbor = getNeighbor(adjacent_direction.SOUTHEAST)) != null)
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
}
