import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import javax.imageio.ImageIO;

import com.aposbot.Constants;
import com.aposbot.StandardCloseHandler;

public class PathWalker extends Script
   implements ActionListener, ItemListener {

    /*
     * - Features, etc.
     * Calculates a path from one point in the world to another and walks
     * there.
     * Prefers road over ground.
     * Can open many doors. Extra IDs and specifics like which key needs to
     * be used are appreciated.
     *
     * - Limitations, etc.
     * Can't change levels (with ladders, etc) or use any kind of
     * teleportation point.
     * No proper handling of direction with bounds.
     * Object information in the default loaded map may be inaccurate.
     *
     * - Credits
     * Stormy
     * Wikipedia
     * Xueqiao Xu <xueqiaoxu@gmail.com>
     *
     * Contributions are appreciated.
     */
     /**kRiStOf's edits:
     /* added paint
     /* added runtime to paint
     */

    // class for encapsulation
    public static class Path {
        private Node[] n;
    }

    private static final class Node {
        final short x;
        final short y;
        // Cost from start along best known path.
        short g;
        // Estimated total cost from start to goal through y.
        short f;
        // Heuristic cost to goal
        short h;
        byte walkable;
        ArrayList<Node> neighbors;
        // Already evaluated.
        boolean closed;
        // Tentative node to be evaluated
        boolean open;

        Node(int x, int y) {
            this.x = (short) x;
            this.y = (short) y;
            h = -1;
        }

        void reset() {
            h = -1;
            g = 0;
            f = 0;
            closed = false;
            open = false;
        }

        int distFrom(Node n) {
            final int sx = this.x - n.x;
            if (sx == 0) return 1;
            final int sy = this.y - n.y;
            if (sy == 0) return 1;
            int dx = Math.abs(sx);
            int dy = Math.abs(sy);
            dx *= dx;
            dy *= dy;
            return (int) Math.sqrt(dx + dy);
        }

        int estHeuristicCost(Node n) {
            // manhattan
            if (h == -1) {
                h = (short) ((2 - walkable) *
                    (Math.abs(this.x - n.x) +
                     Math.abs(this.y - n.y)));
            }
            return h;
        }

        ArrayList<Node> getNeighbors(Node[][] nodes) {
            if (this.neighbors != null) {
                return this.neighbors;
            }

            final boolean allowDiagonal = true;
            final boolean dontCrossCorners = true;

            boolean s0 = false, d0 = false,
                s1 = false, d1 = false,
                s2 = false, d2 = false,
                s3 = false, d3 = false;

            final int x = this.x;
            final int y = this.y;
            Node n;
            final ArrayList<Node> neighbors = new ArrayList<Node>(0);

            n = getNode(nodes, x, y - 1);
            if (n != null) {
                neighbors.add(n);
                s0 = true;
            }
            n = getNode(nodes, x + 1, y);
            if (n != null) {
                neighbors.add(n);
                s1 = true;
            }
            n = getNode(nodes, x, y + 1);
            if (n != null) {
                neighbors.add(n);
                s2 = true;
            }
            n = getNode(nodes, x - 1, y);
            if (n != null) {
                neighbors.add(n);
                s3 = true;
            }

            if (!allowDiagonal) {
                return neighbors;
            }

            if (dontCrossCorners) {
                d0 = s3 && s0;
                d1 = s0 && s1;
                d2 = s1 && s2;
                d3 = s2 && s3;
            } else {
                d0 = s3 || s0;
                d1 = s0 || s1;
                d2 = s1 || s2;
                d3 = s2 || s3;
            }

            n = getNode(nodes, x - 1, y - 1);
            if (n != null && d0) {
                neighbors.add(n);
            }
            n = getNode(nodes, x + 1, y - 1);
            if (n != null && d1) {
                neighbors.add(n);
            }
            n = getNode(nodes, x + 1, y + 1);
            if (n != null && d2) {
                neighbors.add(n);
            }
            n = getNode(nodes, x - 1, y + 1);
            if (n != null && d3) {
                neighbors.add(n);
            }

            this.neighbors = neighbors;
            return neighbors;
        }

        @Override
        public String toString() {
            return x + "," + y;
        }
    }

    public static class Location {

        public String name;
        public int x;
        public int y;
        public boolean bank;

        public Location(String name, int x, int y, boolean b) {
            this.name = name;
            this.x = x;
            this.y = y;
            this.bank = b;
        }

        @Override
        public String toString() {
            String s = bank ? "a bank" : "not a bank";
            return String.format("%s (%d, %d), %s", name, x, y, s);
        }
    }

    public static final Location[] locations = new Location[] {
        new Location("Al Kharid", 87, 695, true),
        new Location("Ardougne North", 580, 573, true),
        new Location("Ardougne South", 550, 612, true),
        new Location("Catherby", 440, 496, true),
        new Location("Draynor", 220, 635, true),
        new Location("Edgeville", 215, 450, true),
        new Location("Falador East", 285, 570, true),
        new Location("Falador West", 330, 555, true),
        new Location("Gnome Tree", 703, 537, false),
        new Location("Lumbridge", 128, 640, false),
        new Location("Port Sarim", 270, 625, false),
        new Location("Seers Village", 500, 453, true),
        new Location("Shilo Village", 401, 849, true),
        new Location("Varrock East", 102, 511, true),
        new Location("Varrock West", 150, 505, true),
        new Location("Yanille", 587, 752, true),
        new Location("Bone Yard", 700, 648, false),
        new Location("Legends Guild", 512, 554, false),
        new Location("Heroes Guild", 372, 443, false),
        new Location("Fishing Guild", 586, 527, false),
        new Location("Crafting Guild", 347, 599, false),
        new Location("Lost City Hut", 128, 686, false)
    };
    private static final boolean DEBUG = false;
    private static final int WORLD_W = 900;
    private static final int WORLD_H = 4050;
    private Node[][] nodes;
    private Node[] path;
    private long wait_time;
    private int path_ptr;
    private Frame frame;
    private List choice;
    private TextField field_start;
    private TextField field_end;
    private static final int[] objects_1 = new int[] {
        64, 60, 137, 138, 93
    };
    private static final int[] bounds_1 = new int[] {
        2, 8, 55, 68, 44, 74, 117
    };
    private long start_time;

    public PathWalker(Extension ex) {
        super(ex);
    }

    public static void main(String[] argv) {
        PathWalker pw = new PathWalker(null);
        pw.init("");
        while (pw.frame.isVisible()) {
            try {
                Thread.sleep(50L);
            } catch (InterruptedException ex) {
            }
        }
        System.exit(0);
    }

    @Override
    public void init(String params) {
        start_time = -1L;
        File dir = new File(
            "." + File.separator + "Map" + File.separator);
        dir.mkdir();

        if (nodes == null) {
            File file = new File(dir, "data.gz");

            System.out.print("Reading map... ");

            byte[][] walkable = new byte[WORLD_W][WORLD_H];
            GZIPInputStream in = null;
            try {
                in = new GZIPInputStream(
                    new BufferedInputStream(
                    new FileInputStream(file)));
                for (int i = 0; i < WORLD_W; ++i) {
                    int read = 0;
                    do {
                        int r = in.read(walkable[i], read, WORLD_H - read);
                        if (r == -1) {
                            throw new IOException("Unexpected EOF");
                        }
                        read += r;
                    } while (read != WORLD_H);
                }
            } catch (IOException ex) {
                System.out.println("failed: " + ex);
                return;
            } finally {
                try {
                    in.close();
                } catch (Throwable t) {
                }
            }

            nodes = new Node[WORLD_W][WORLD_H];

            for (int x = 0; x < WORLD_W; ++x) {
                for (int y = 0; y < WORLD_H; ++y) {
                    byte i = walkable[x][y];
                    if (i != 0) {
                        Node n = new Node(x, y);
                        n.walkable = i;
                        nodes[x][y] = n;
                    }
                }
            }

            walkable = null;
            System.gc();
            System.out.println("done.");
        }

        // The bot will always call init with "".
        // If init is called with null, it is being called by another script,
        // so don't create the UI.
        if (params != null) {
            createFrame();
        }
    }

    @Override
    public int main() {
        if (start_time == -1L) {
        start_time = System.currentTimeMillis();
        }
        if (inCombat()) {
            resetWait();
            walkTo(getX(), getY());
            return random(400, 600);
        }
        if (!walkPath()) {
            System.out.println("Reached destination.");
            System.out.println("Stopping.");
            stopScript();
            Toolkit.getDefaultToolkit().beep();
        }
        return 0;
    }

    @Override
    public String toString() {
        return "Path Walker";
    }

    private BufferedImage getMapImage() {
        File file = new File(
            "." + File.separator + "Map" + File.separator + "map.png");

        System.out.print("Reading map image... ");
        try {
            BufferedImage image = ImageIO.read(file);
            System.out.println("done.");
            return image;
        } catch (IOException ex) {
            System.out.println("failed: " + ex);
        }
        return null;
    }

    private void createFrame() {
        if (frame == null) {
            Panel bp = new Panel();
            Button button = new Button("OK");
            button.addActionListener(this);
            bp.add(button);
            button = new Button("Cancel");
            button.addActionListener(this);
            bp.add(button);

            Panel tp = new Panel();
            tp.setLayout(new GridLayout(0, 2, 2, 2));
            tp.add(new Label("Start location"));
            tp.add(field_start = new TextField());
            tp.add(new Label("Target location"));
            tp.add(field_end = new TextField());

            choice = new List(locations.length / 2);
            for (Location l : locations) {
                choice.add(l.name);
            }
            choice.addItemListener(this);

            Panel pp = new Panel();
            pp.setLayout(new BorderLayout());
            pp.add(new Label("Preset targets", Label.CENTER),
                BorderLayout.NORTH);
            pp.add(choice, BorderLayout.CENTER);

            field_end.setText("0,0");

            frame = new Frame(getClass().getSimpleName());
            frame.addWindowListener(
                new StandardCloseHandler(frame, StandardCloseHandler.HIDE)
            );
            frame.setIconImages(Constants.ICONS);
            frame.add(tp, BorderLayout.NORTH);
            frame.add(pp, BorderLayout.CENTER);
            frame.add(bp, BorderLayout.SOUTH);
            frame.pack();
            frame.setMinimumSize(frame.getSize());
            frame.setSize(245, 280);
        }
        String str = "0,0";
        StringBuilder sb = new StringBuilder();
        try {
            sb.append(getX());
            sb.append(',');
            sb.append(getY());
            str = sb.toString();
        } catch (Throwable t) {
        }
        field_start.setText(str);
        frame.toFront();
        frame.setLocationRelativeTo(null);
        frame.requestFocus();
        frame.setVisible(true);
    }

    public boolean walkPath() {
        if (path == null) return false;
        Node last = path[path.length - 1];
        if (getX() == last.x && getY() == last.y) {
            path = null;
            return false;
        }
        long c_time = System.currentTimeMillis();
        if (c_time >= wait_time) {
            Node n = getCurrentDest();
            if (n == null) return true;
            int x = n.x;
            int y = n.y;
            walkTo(x, y);
            int d = distanceTo(x, y);
            if (d != 0) {
                wait_time = c_time + random(500 * d, 600 * d);
            } else {
                wait_time = c_time + random(600, 800);
            }
        }
        return true;
    }

    public void resetWait() {
        wait_time = System.currentTimeMillis();
    }

    private Node getCurrentDest() {
        long c_time = System.currentTimeMillis();
        int ptr  = path_ptr;
        int x, y;
        int orig = ptr;
        do {
            if (ptr >= (path.length - 1)) {
                break;
            }
            Node cur = path[ptr];
            x = cur.x;
            y = cur.y;
            Node next = path[++ptr];
            if (!isReachable(next.x, next.y) && handleObstacles(x, y)) {
                // you may wish to modify this.
                wait_time = c_time + random(2500, 3000);
                path_ptr = ptr - 1;
                return null;
            }
        } while (distanceTo(x, y) < 6);

        ptr = orig;

        int min_dist = random(7, 18);
        int max_dist = 20;
        int dist;
        int loop = 0;
        do {
            if ((++ptr) >= path.length) {
                min_dist = random(1, 18);
                ptr = orig;
            }
            Node n = path[ptr];
            x = n.x;
            y = n.y;
            dist = distanceTo(x, y);
            if (dist > max_dist) {
                min_dist = random(1, 18);
                ptr = orig;
            }
            if ((loop++) > 500) {
                System.out.println("Pathing failure");
                return null;
            }
        } while (dist < min_dist || !isReachable(x, y));
        path_ptr = ptr;
        return path[path_ptr];
    }

    private boolean handleObstacles(int x, int y) {
        int id = getWallObjectIdFromCoords(x, y);
        if (id != -1) {
            for (int i : bounds_1) {
                if (id != i) continue;
                atWallObject(x, y);
                return true;
            }
        }
        // is this ridiculous or not? heh
        if (handleObject(x, y)) return true;
        if (handleObject(x + 1, y)) return true;
        if (handleObject(x - 1, y)) return true;
        if (handleObject(x, y + 1)) return true;
        if (handleObject(x, y - 1)) return true;
        if (handleObject(x - 1, y - 1)) return true;
        if (handleObject(x + 1, y + 1)) return true;
        if (handleObject(x - 1, y + 1)) return true;
        if (handleObject(x + 1, y - 1)) return true;
        return false;
    }

    private boolean handleObject(int x, int y) {
        int id = getObjectIdFromCoords(x, y);
        if (id == -1) return false;
        for (int i : objects_1) {
            if (id != i) continue;
            atObject(x, y);
            return true;
        }
        return false;
    }

    public void setPath(Path p) {
        if (p == null) {
            path = null;
            return;
        }
        if (p.n == path) return;
        path = p.n;
        wait_time = 0;
        path_ptr = 0;
    }

    public final Path calcPath(int x, int y) {
        return calcPath(getX(), getY(), x, y);
    }

    public Path calcPath(int x1, int y1, int x2, int y2) {
        Node start = getNode(nodes, x1, y1);
        if (start == null) return null;
        Node end = getNode(nodes, x2, y2);
        if (end == null) return null;
        Node[] n = astar(start, end);
        if (n == null) return null;
        Path p = new Path();
        p.n = n;
        return p;
    }

    public int pathLength(int x1, int y1, int x2, int y2) {
        Node start = getNode(nodes, x1, y1);
        if (start == null) return Integer.MAX_VALUE;
        Node end = getNode(nodes, x2, y2);
        if (end == null) return Integer.MAX_VALUE;
        Node[] n = astar(start, end);
        if (n == null) return Integer.MAX_VALUE;
        return n.length;
    }

    public final Location getNearestBank(final int x, final int y) {
        ArrayList<Location> ordered = new ArrayList<>();
        for (Location loc : locations) {
            if (loc.bank) {
                ordered.add(loc);
            }
        }
        Collections.sort(ordered, new Comparator<Location>() {
            @Override
            public int compare(Location l1, Location l2) {
                int dist1 = distanceTo(x, y, l1.x, l1.y);
                int dist2 = distanceTo(x, y, l2.x, l2.y);
                if (dist1 == dist2) {
                    return 0;
                } else if (dist1 < dist2) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });
        int best_dist = Integer.MAX_VALUE;
        Location best_loc = null;
        for (Location loc : ordered) {
            if (distanceTo(x, y, loc.x, loc.y) > best_dist) {
                continue;
            }
            int dist = pathLength(x, y, loc.x, loc.y);
            if (dist < best_dist) {
                best_dist = dist;
                best_loc = loc;
            }
        }
        return best_loc;
    }

    private Node[] astar(Node start, Node goal) {
        if (DEBUG) {
            System.out.print(
                "Calculating path from " + start +
                " to " + goal + "... ");
        }

        long start_ms = System.currentTimeMillis();

        start.f = (short) start.estHeuristicCost(goal);

        Deque<Node> open = new ArrayDeque<Node>(32);
        open.add(start);
        start.open = true;

        // The map of navigated nodes
        Map<Node, Node> came_from = new HashMap<Node, Node>();

        Node[][] nodes = this.nodes;

        while (!open.isEmpty()) {
            Node cur = getLowestFScore(open);
            if (cur.equals(goal)) {
                Node[] n = constructPath(came_from, start, goal);
                resetNodes(nodes);
                if (DEBUG) {
                    System.out.print("done. ms taken: ");
                    System.out.println(System.currentTimeMillis() - start_ms);
                }
                return n;
            }
            open.remove(cur);
            cur.open = false;
            cur.closed = true;
            for (Node n : cur.getNeighbors(nodes)) {
                int t_gscore = cur.g + n.distFrom(cur);
                int t_fscore = t_gscore + n.estHeuristicCost(goal);
                if (n.closed && t_fscore >= n.f) {
                    continue;
                }
                if (!n.open) {
                    came_from.put(n, cur);
                    n.g = (short) t_gscore;
                    n.f = (short) t_fscore;
                    open.add(n);
                    n.open = true;
                }
            }
        }

        resetNodes(nodes);
        if (DEBUG) {
            System.out.print("failed! ms taken: ");
            System.out.println(System.currentTimeMillis() - start_ms);
        }
        return null;
    }

    private static void resetNodes(Node[][] nodes) {
        for (int x = 0; x < WORLD_W; ++x) {
            for (int y = 0; y < WORLD_H; ++y) {
                Node n = nodes[x][y];
                if (n == null) continue;
                n.reset();
            }
        }
    }

    private static Node[] constructPath(
        Map<Node, Node> came_from, Node start, Node goal) {

        Deque<Node> path = new ArrayDeque<Node>();
        Node p = came_from.get(goal);
        while (p != start) {
            path.push(p);
            p = came_from.get(p);
        }
        path.push(p);
        path.add(goal);
        return path.toArray(new Node[path.size()]);
    }

    private static Node getLowestFScore(Deque<Node> open) {
        Node best_n = null;
        int best_f = Integer.MAX_VALUE;
        int f;
        for (Node n : open) {
            f = n.f;
            if (f < best_f) {
                best_n = n;
                best_f = f;
            }
        }
        return best_n;
    }

    private static Node getNode(Node[][] nodes, int x, int y) {
        if (x < 0 || x > (WORLD_W - 1)) {
            return null;
        }
        if (y < 0 || y > (WORLD_H - 1)) {
            return null;
        }
        return nodes[x][y];
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("OK")) {
            new Thread(new FinderInit()).start();
        }
        frame.setVisible(false);
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        Location loc = locations[choice.getSelectedIndex()];
        StringBuilder b = new StringBuilder();
        b.append(loc.x);
        b.append(',');
        b.append(loc.y);
        field_end.setText(b.toString());
    }

    private class FinderInit implements Runnable {

        @Override
        public void run() {
            String[] split = field_start.getText().split(",");
            int x1 = Integer.valueOf(split[0]);
            int y1 = Integer.valueOf(split[1]);

            split = field_end.getText().split(",");
            int x2 = Integer.valueOf(split[0]);
            int y2 = Integer.valueOf(split[1]);

            Node start = getNode(nodes, x1, y1);

            if (start == null) {
                System.out.println("Failed: invalid start position.");
                frame.setVisible(false); return;
            }

            Node end = getNode(nodes, x2, y2);

            if (end == null) {
                System.out.println("Failed: invalid end position.");
                frame.setVisible(false); return;
            }

            Node[] path = astar(start, end);
            if (path == null) {
                System.out.println("Failed to calculate path. :(");
                return;
            }

            BufferedImage image = getMapImage();
            if (image == null) {
                return;
            }

            System.out.print("Generating path image... ");
            Graphics g = image.getGraphics();
            g.setColor(Color.GREEN);
            int len = path.length;
            for (int i = 0; i < len; ++i) {
                Node p = path[i];
                g.fillOval(WORLD_W - 1 - p.x, p.y, 3, 3);
            }
            g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 11));
            g.setColor(Color.BLACK);
            g.drawString("Start", WORLD_W - x1, y1 + 1);
            g.drawString("Goal", WORLD_W - x2, y2 + 1);
            g.setColor(Color.WHITE);
            g.drawString("Start", WORLD_W - 1 - x1, y1);
            g.drawString("Goal", WORLD_W - 1 - x2, y2);
            System.out.println("done.");
            System.out.print("Writing path image... ");
            try {
                ImageIO.write(image, "PNG", new File(
                    "." + File.separator + "Map" +
                    File.separator + "path.png"));
                System.out.println("done.");
            } catch (Throwable t) {
                System.out.println("failed: " + t);
            }
            Path p = new Path();
            p.n = path;
            setPath(p);
            image = null;
            System.gc();
            System.out.println("Ready to run.");
        }
    }
    @Override
   public void paint() {
        int x = 320;
       int y = 46;
       drawString("Storm's Path Walker", x - 1, y, 4, 0x1E90FF);
        y += 15;
       drawString("Runtime: " + get_time_since(start_time), x, y, 1, 0xFFFFFF);
        drawVLine(x-7, 36, y - 32, 0x1E90FF);
        drawHLine(x-7, y + 3, 196, 0x1E90FF);
   }
    private static String get_time_since(long t) {
        long millis = (System.currentTimeMillis() - t) / 1000;
        long second = millis % 60;
        long minute = (millis / 60) % 60;
        long hour = (millis / (60 * 60)) % 24;
        long day = (millis / (60 * 60 * 24));

        if (day > 0L) {
            return String.format("%02d days, %02d hrs, %02d mins",
                day, hour, minute);
        }
        if (hour > 0L) {
            return String.format("%02d hours, %02d mins, %02d secs",
                hour, minute, second);
        }
        if (minute > 0L) {
            return String.format("%02d minutes, %02d seconds",
                minute, second);
        }
        return String.format("%02d seconds", second);
    }
}