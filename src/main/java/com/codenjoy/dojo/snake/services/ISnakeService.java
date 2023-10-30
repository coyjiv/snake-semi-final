package com.codenjoy.dojo.snake.services;

import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.snake.model.Vertex;


import java.util.LinkedList;
import java.util.List;

public interface ISnakeService {
    Vertex[][] createGraphOfMap(int[][] map, List<Point> appleCoordinates, List<Point> stonesCoordinates, List<Point> snakeCoordinates);
    LinkedList<String> findPathToApple(Vertex[][] graph, Vertex start, Vertex end);
}
