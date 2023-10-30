package com.codenjoy.dojo.snake.services;

import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.PointImpl;
import com.codenjoy.dojo.snake.utils.Helpers;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class LeeService {

        private final int WIDTH;
        private final int HEIGHT;

        private final static int EMPTY = 0;
        protected final static int OBSTACLE = -9;
        protected final static int START = 1;

        private final int[][] board;

        public LeeService(int width, int height) {
            WIDTH = width;
            HEIGHT = height;
            board = new int[height][width];
        }

        private final Supplier<Stream<Point>> deltas = () -> Stream.of(
                new PointImpl(-1, 0),
                new PointImpl(1, 0),
                new PointImpl(0, -1),
                new PointImpl(0, 1)
        );

        private int get(int x, int y) {
            return board[y - 1][x - 1];
        }

        protected int get(Point point) {
            return get(point.getX(), point.getY());
        }

        protected void set(int x, int y, int value) {
            board[y - 1][x - 1] = value;
        }

        protected void set(Point point, int value) {
            set(point.getX(), point.getY(), value);
        }

        protected boolean isOnBoard(Point point) {
            return point.getX() >= 1 && point.getY() >= 1 && point.getX() <= WIDTH && point.getY() <= HEIGHT;
        }

        protected boolean isUnvisited(Point p) {
            return get(p) == EMPTY;
        }

        protected void clear() {
            Arrays.stream(board).forEach(l -> Arrays.fill(l, 0));
        }

        public Stream<Point> neighbours(Point p) {
            return deltas.get()
                    .map(d -> Helpers.doMove(p, d))
                    .filter(this::isOnBoard);
        }

        private Stream<Point> neighboursUnvisited(Point p) {
            return neighbours(p)
                    .filter(this::isUnvisited);
        }

        private List<Point> neighboursByValue(Point p, int value) {
            return neighbours(p)
                    .filter(p0 -> this.get(p0) == value)
                    .collect(Collectors.toList());
        }

        public void initializeBoard(List<Point> obstacles) {
            this.clear();
            obstacles.forEach(p -> this.set(p, OBSTACLE));
        }


        public Optional<List<Point>> trace(Point start, Point finish, List<Point> obstacles) {
            // 0. initialize algorithm
            initializeBoard(obstacles);
            boolean found = false;
            int[] counter = {START};
            this.set(start, counter[0]);

            // 1. fill
            {
                Set<Point> curr = new HashSet<>();
                curr.add(start);
                while (!(found || curr.isEmpty())) {
                    counter[0]++;
                    Set<Point> next = curr.stream()
                            .flatMap(this::neighboursUnvisited)
                            .collect(Collectors.toSet());
                    next.forEach(p -> this.set(p, counter[0]));
                    found = next.contains(finish);
                    curr = next;
                }
            }
            // 2. check
            if (!found) return Optional.empty();

            // 3. trace back (reconstruct path)
            LinkedList<Point> path = new LinkedList<>();
            path.add(finish);
            Point curr = finish;
            while (counter[0] > START) {
                counter[0]--;
                Point prev = neighboursByValue(curr, counter[0]).get(0);
                if (prev == start) break;
                path.addFirst(prev);
                curr = prev;
            }
            //System.out.println(boardFormatted(path));

            return Optional.of(path);
        }

        public String cellFormatted(Point p, List<Point> path) {
            int value = this.get(p);
            if (value == OBSTACLE) return " X ";
            if (path.isEmpty()) return String.format("%3s", " . ");
            if (path.contains(p)) return String.format("%3s", " * "); // value
            return " . ";
        }

        public String boardFormatted(List<Point> path) {
            System.out.println("Путь: " + path);
            return IntStream.rangeClosed(1, HEIGHT).map(i -> HEIGHT + 1 - i).mapToObj(y ->
                    IntStream.rangeClosed(1, WIDTH)
                            .mapToObj(x -> new PointImpl(x, y))
                            .map(p -> cellFormatted(p, path))
                            .collect(Collectors.joining())
            ).collect(Collectors.joining("\n"));
        }

    }
