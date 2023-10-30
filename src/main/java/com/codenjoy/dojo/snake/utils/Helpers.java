package com.codenjoy.dojo.snake.utils;


import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.PointImpl;
import com.codenjoy.dojo.snake.client.Board;
import com.codenjoy.dojo.snake.model.Elements;

public class Helpers {

    public static int gameOvers = 0;

    // Возвращает точку на поле после шага от текущей на дельта
    public static Point doMove(Point point, Point delta) {
        return new PointImpl(point.getX() + delta.getX(), point.getY() + delta.getY());
    }

    // Возвращает точку на поле после шага от текущей на x и y
    public static Point doMove(Point point, int x, int y) {
        return new PointImpl(point.getX() + x, point.getY() + y);
    }

    // Возвращает конец тела змеи
    public static Point getEndOfTail(Board board) {
        return board.get(Elements.TAIL_END_DOWN,
                Elements.TAIL_END_LEFT,
                Elements.TAIL_END_UP,
                Elements.TAIL_END_RIGHT).get(0);
    }
}
