package com.codenjoy.dojo.snake.client;

/*-
 * #%L
 * Codenjoy - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2018 Codenjoy
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */


import com.codenjoy.dojo.client.Solver;
import com.codenjoy.dojo.client.WebSocketRunner;
import com.codenjoy.dojo.services.Dice;
import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.RandomDice;
import com.codenjoy.dojo.snake.model.Vertex;
import com.codenjoy.dojo.snake.services.LeeService;
import com.codenjoy.dojo.snake.services.SnakeService;
import com.codenjoy.dojo.snake.utils.Helpers;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * User: your name
 */
public class YourSolver implements Solver<Board> {

    private Dice dice;
    private Board board;

    private static final int MAX_SNAKE_LENGTH = 100;
    private SnakeService snakeService;
    private final int[][] boardMatrix = new int[15][15];
    private LinkedList<String> path = new LinkedList<>();

    public YourSolver(Dice dice) {
        this.dice = dice;
    }

    @Override
    public String get(Board board) {
        if (board.isGameOver()) {
            path = new LinkedList<>();
            return "";
        }
        this.board = board;
        LeeService leeService = new LeeService(13, 13);
        this.snakeService = new SnakeService(new Vertex[15][15], board, leeService);
        System.out.println(board.toString());

//            // 1. create graph of map
//            Vertex[][] graph = snakeService.createGraphOfMap(boardMatrix, board.getApples(), board.getStones(), board.getSnake());
//
//            // 2. find path to apple
//            Vertex headVertex = graph[snakeService.invertY(board.getHead().getY())][board.getHead().getX()];
//            Vertex appleVertex = graph[snakeService.invertY(board.getApples().get(0).getY())][board.getApples().get(0).getX()];
//            Vertex stoneVertex = graph[snakeService.invertY(board.getStones().get(0).getY())][board.getStones().get(0).getX()];
//            Vertex tailVertex = graph[snakeService.invertY(Helpers.getEndOfTail(board).getY())][Helpers.getEndOfTail(board).getX()];
//
//
//            LinkedList<String> pathToApple = snakeService.checkTraceByVirtualSnake(board, snakeService.findPathToApplePoint(graph, headVertex, appleVertex));
//            LinkedList<String> pathToStone = snakeService.checkTraceByVirtualSnake(board, snakeService.findPathToApplePoint(graph, headVertex, stoneVertex));
//            LinkedList<String> pathToTail = snakeService.checkTraceByVirtualSnake(board, snakeService.findPathToApplePoint(graph, headVertex, tailVertex));
//
//        System.out.println("pathToApple > " + pathToApple);
//        System.out.println("pathToStone > " + pathToStone);
//        System.out.println("pathToTail > " + pathToTail);
//            if(!pathToApple.isEmpty()){
//                System.out.println(">>> КОРОТКИЙ ПУТЬ К ЯБЛОКУ");
//                return pathToApple.poll();
//            }
//            if(!pathToStone.isEmpty() && board.getSnake().size() > MAX_SNAKE_LENGTH){
//                System.out.println(">>> КОРОТКИЙ ПУТЬ К КАМНЮ, длинна змеи больше максимальной");
//                return pathToStone.poll();
//            }
//            if(!pathToTail.isEmpty()){
//                System.out.println(">>> ДЛИННЫЙ ПУТЬ К ХВОСТУ");
//                return pathToTail.poll();
//            }
//        System.out.println("Meh...");
//        return board.getSnakeDirection().toString();

        // Голова змеи
        Point head = board.getHead();
        // Конец хвоста
        Point endTail = Helpers.getEndOfTail(board);
        // Яблоки
        List<Point> apples = board.getApples();
        // Камни
        List<Point> stones = board.getStones();
        // Змея
        List<Point> snake = board.getSnake();

        // Путь к яблоку
        Optional<List<Point>> appleTrace = !apples.isEmpty()
                ? snakeService.checkTraceByVirtualSnake(board, leeService.trace(head, apples.get(0), snakeService.getConcatObstacles(board, snake, stones)), 3)
                : Optional.empty();
        // Путь к камню
        Optional<List<Point>> stoneTrace = !stones.isEmpty()
                ? snakeService.checkTraceByVirtualSnake(board, leeService.trace(head, stones.get(0), snakeService.getConcatObstacles(board, snake)), 0) // apples
                : Optional.empty();
        // Путь к хвосту
        Optional<List<Point>> endTailTrace = leeService.trace(head, endTail, snakeService.getConcatObstacles(board, snake, stones)); // apples

        if (appleTrace.isPresent()) {
            System.out.println("--- short path to apple");
            return snakeService.getStrDirectionFromTrace(appleTrace.get());
        }

        if (stoneTrace.isPresent() && snake.size() > MAX_SNAKE_LENGTH) {
            System.out.println("--- short path to stone");
            return snakeService.getStrDirectionFromTrace(stoneTrace.get());
        }

        if (endTailTrace.isPresent()) {
            System.out.println("--- long path to tail");
            return snakeService.choiceLongDirectionToEndOfTail(board, snakeService.getConcatObstacles(board, snake, stones));
        }

        System.out.println("--- short path to tail -- remove stone from obstacles. Stone appeared in front of the nose");
        endTailTrace = leeService.trace(head, endTail, snakeService.getConcatObstacles(board, snake));
        System.out.println("tail trace - " + endTailTrace);
        return endTailTrace.isPresent() ? snakeService.getStrDirectionFromTrace(endTailTrace.get()) : board.getSnakeDirection().toString();
    }

    public static void main(String[] args) {

        WebSocketRunner.runClient(
                // paste here board page url from browser after registration
                "http://157.230.104.223/codenjoy-contest/board/player/m3bcok73gzn44sy9fyl5?code=1522088249912281076",
                new YourSolver(new RandomDice()),
                new Board());
    }

}
