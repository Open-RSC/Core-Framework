package com.aposbot;

public final class LocalRouteCalc {

    private final int[][] route_dir;
    private final int[] route_x;
    private final int[] route_y;

    public LocalRouteCalc() {
        route_dir = new int[96][96];
        route_x = new int[8000];
        route_y = new int[8000];
    }

    public int calculate(int[][] adjacency, int start_x, int start_y,
                         int min_x, int min_y, int max_x, int max_y, boolean approximate) {
        final int[][] route_dir = this.route_dir;
        for (int k1 = 0; k1 < 96; k1++) {
            for (int l1 = 0; l1 < 96; l1++) {
                route_dir[k1][l1] = 0;
            }
        }
        final int[] route_x = this.route_x;
        final int[] route_y = this.route_y;
        int tail = 0;
        int head = 0;
        int x = start_x;
        int y = start_y;
        route_dir[start_x][start_y] = 99;
        route_x[tail] = start_x;
        route_y[tail++] = start_y;
        final int len = route_x.length;
        boolean success = false;
        while (head != tail) {
            x = route_x[head];
            y = route_y[head];
            head = (head + 1) % len;
            if (x >= min_x && x <= max_x && y >= min_y && y <= max_y) {
                success = true;
                break;
            }
            if (approximate) {
                if (x > 0 && x - 1 >= min_x && x - 1 <= max_x && y >= min_y && y <= max_y && (adjacency[x - 1][y] & 8) == 0) {
                    success = true;
                    break;
                }
                if (x < 95 && x + 1 >= min_x && x + 1 <= max_x && y >= min_y && y <= max_y && (adjacency[x + 1][y] & 2) == 0) {
                    success = true;
                    break;
                }
                if (y > 0 && x >= min_x && x <= max_x && y - 1 >= min_y && y - 1 <= max_y && (adjacency[x][y - 1] & 4) == 0) {
                    success = true;
                    break;
                }
                if (y < 95 && x >= min_x && x <= max_x && y + 1 >= min_y && y + 1 <= max_y && (adjacency[x][y + 1] & 1) == 0) {
                    success = true;
                    break;
                }
            }
            if (x > 0 && route_dir[x - 1][y] == 0 && (adjacency[x - 1][y] & 0x78) == 0) {
                route_x[tail] = x - 1;
                route_y[tail] = y;
                tail = (tail + 1) % len;
                route_dir[x - 1][y] = 2;
            }
            if (x < 95 && route_dir[x + 1][y] == 0 && (adjacency[x + 1][y] & 0x72) == 0) {
                route_x[tail] = x + 1;
                route_y[tail] = y;
                tail = (tail + 1) % len;
                route_dir[x + 1][y] = 8;
            }
            if (y > 0 && route_dir[x][y - 1] == 0 && (adjacency[x][y - 1] & 0x74) == 0) {
                route_x[tail] = x;
                route_y[tail] = y - 1;
                tail = (tail + 1) % len;
                route_dir[x][y - 1] = 1;
            }
            if (y < 95 && route_dir[x][y + 1] == 0 && (adjacency[x][y + 1] & 0x71) == 0) {
                route_x[tail] = x;
                route_y[tail] = y + 1;
                tail = (tail + 1) % len;
                route_dir[x][y + 1] = 4;
            }
            if (x > 0 && y > 0 && (adjacency[x][y - 1] & 0x74) == 0 && (adjacency[x - 1][y] & 0x78) == 0 && (adjacency[x - 1][y - 1] & 0x7c) == 0 && route_dir[x - 1][y - 1] == 0) {
                route_x[tail] = x - 1;
                route_y[tail] = y - 1;
                tail = (tail + 1) % len;
                route_dir[x - 1][y - 1] = 3;
            }
            if (x < 95 && y > 0 && (adjacency[x][y - 1] & 0x74) == 0 && (adjacency[x + 1][y] & 0x72) == 0 && (adjacency[x + 1][y - 1] & 0x76) == 0 && route_dir[x + 1][y - 1] == 0) {
                route_x[tail] = x + 1;
                route_y[tail] = y - 1;
                tail = (tail + 1) % len;
                route_dir[x + 1][y - 1] = 9;
            }
            if (x > 0 && y < 95 && (adjacency[x][y + 1] & 0x71) == 0 && (adjacency[x - 1][y] & 0x78) == 0 && (adjacency[x - 1][y + 1] & 0x79) == 0 && route_dir[x - 1][y + 1] == 0) {
                route_x[tail] = x - 1;
                route_y[tail] = y + 1;
                tail = (tail + 1) % len;
                route_dir[x - 1][y + 1] = 6;
            }
            if (x < 95 && y < 95 && (adjacency[x][y + 1] & 0x71) == 0 && (adjacency[x + 1][y] & 0x72) == 0 && (adjacency[x + 1][y + 1] & 0x73) == 0 && route_dir[x + 1][y + 1] == 0) {
                route_x[tail] = x + 1;
                route_y[tail] = y + 1;
                tail = (tail + 1) % len;
                route_dir[x + 1][y + 1] = 12;
            }
        }
        if (!success) {
            return -1;
        }
        head = 0;
        route_x[head] = x;
        route_y[head++] = y;
        int _dir;
        for (int dir = _dir = route_dir[x][y]; x != start_x || y != start_y; dir = route_dir[x][y]) {
            if (dir != _dir) {
                _dir = dir;
                route_x[head] = x;
                route_y[head++] = y;
            }
            if ((dir & 2) != 0) {
                x++;
            } else if ((dir & 8) != 0) {
                x--;
            }
            if ((dir & 1) != 0) {
                y++;
            } else if ((dir & 4) != 0) {
                y--;
            }
        }
        return head;
    }
}