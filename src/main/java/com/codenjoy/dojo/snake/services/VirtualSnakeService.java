package com.codenjoy.dojo.snake.services;

import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.PointImpl;
import com.codenjoy.dojo.snake.client.Board;
import com.codenjoy.dojo.snake.utils.Helpers;
import com.google.common.collect.Iterables;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

public class VirtualSnakeService {
    private final List<Point> snake;
    private final Point head;
    private Point endOfTail;

    public List<Point> getSnake() {
        return snake;
    }

    public Point getHead() {
        return head;
    }

    public Point getEndOfTail() {
        return endOfTail;
    }


    VirtualSnakeService(Board board, List<Point> trace0) {
        List<Point> trace = new ArrayList<>(trace0);
        trace.remove(0);

        int sizeTrace = trace.size();

        List<Point> oldSnake = board.getSnake();

        int sizeOldSnake = oldSnake.size();

        if (trace.size() >= (sizeOldSnake + 1)) {
            snake = IntStream.range(0, sizeOldSnake + 1).mapToObj(i -> trace.get(sizeTrace - 1 - i)).toList();
            head = snake.get(0);
            endOfTail = Iterables.getLast(snake);
        } else {

            Collections.reverse(trace);
            snake = new ArrayList<>(trace);
            head = snake.get(0);
            endOfTail = Helpers.getEndOfTail(board);

            Point delta = new PointImpl(0, 0);
            while ((snake.size() + oldSnake.size()) != (sizeOldSnake + 1)) {
                oldSnake.remove(endOfTail);

                delta = switch (board.getAt(endOfTail.getX(), endOfTail.getY())) {
                    case BAD_APPLE, GOOD_APPLE, BREAK, HEAD_DOWN, HEAD_LEFT, HEAD_RIGHT, HEAD_UP, NONE -> null;
                    case TAIL_END_DOWN -> Helpers.doMove(delta, 0, 1);
                    case TAIL_END_UP -> Helpers.doMove(delta, 0, -1);
                    case TAIL_END_LEFT -> Helpers.doMove(delta, 1, 0);
                    case TAIL_END_RIGHT -> Helpers.doMove(delta, -1, 0);
                    case TAIL_HORIZONTAL, TAIL_VERTICAL -> Helpers.doMove(delta, 0, 0);
                    case TAIL_RIGHT_UP -> Helpers.doMove(delta, 1, 1);
                    case TAIL_LEFT_DOWN -> Helpers.doMove(delta, -1, -1);
                    case TAIL_LEFT_UP -> Helpers.doMove(delta, -1, 1);
                    case TAIL_RIGHT_DOWN -> Helpers.doMove(delta, 1, -1);
                };
                endOfTail = Helpers.doMove(endOfTail, delta);
            }
            snake.addAll(oldSnake);
        }
    }
}
