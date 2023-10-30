package com.codenjoy.dojo.snake.services;

import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.snake.model.Vertex;
import com.codenjoy.dojo.snake.model.Edge;
import com.codenjoy.dojo.snake.client.Board;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.PointImpl;
import com.codenjoy.dojo.snake.utils.Helpers;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SnakeService implements ISnakeService{
    private final Vertex[][] graphMatrix;
    private final int MAX_SNAKE_LENGTH = 10;

    private final LeeService leeService;

    private final Board board;

    public SnakeService(Vertex[][] graphMatrix, Board board, LeeService leeService) {
        this.graphMatrix = graphMatrix;
        this.board = board;
        this.leeService = leeService;
    }


    @Override
    public Vertex[][] createGraphOfMap(int[][] map, List<Point> appleCoordinates, List<Point> stonesCoordinates, List<Point> snakeCoordinates) {
        Vertex[][] graphMatrix = new Vertex[map.length][map[0].length];

        // create the borders vertexes
        for (int i = 0; i < map.length; i++){
            //top
            graphMatrix[0][i] = new Vertex("/",i, 0);
            //bottom
            graphMatrix[map.length-1][i] = new Vertex("/",i, map.length-1);
            //left
            graphMatrix[i][0] = new Vertex("/",0, i);
            //right
            graphMatrix[i][map[i].length-1] = new Vertex("/",map[i].length-1, i);
        }

        // create the apples vertexes
        for(Point apple:appleCoordinates){
           graphMatrix[invertY(apple.getY())][apple.getX()] = new Vertex("X",apple.getX(), invertY(apple.getY()));
        }

        // create the stones vertexes
        for(Point stone: stonesCoordinates){
            graphMatrix[invertY(stone.getY())][stone.getX()] = new Vertex("O",stone.getX(), invertY(stone.getY()));
        }

        // create the snake vertexes
        for(Point snake: snakeCoordinates){
            graphMatrix[invertY(snake.getY())][snake.getX()] = new Vertex("S",snake.getX(), invertY(snake.getY()));
        }

        // mark the head of the snake
        graphMatrix[invertY(snakeCoordinates.get(0).getY())][snakeCoordinates.get(0).getX()].setSnakeHead(true);
        graphMatrix[invertY(snakeCoordinates.get(0).getY())][snakeCoordinates.get(0).getX()].setValue("H");

        //mark the tail of the snake
        graphMatrix[invertY(snakeCoordinates.get(snakeCoordinates.size()-1).getY())][snakeCoordinates.get(snakeCoordinates.size()-1).getX()].setValue("T");

        // create the field
        for (int i = 0; i < map.length; i++){
            for (int j = 0; j < map[i].length; j++){
                if(graphMatrix[i][j] == null){
                    graphMatrix[i][j] = new Vertex("*",j,i);
                }
            }
        }

        // create connections

        for(int i=0; i<graphMatrix.length; i++){
            for(int j=0; j< graphMatrix.length; j++){
                Vertex currentVertex = graphMatrix[i][j];
                if(currentVertex.getValue().equals("S") || currentVertex.getValue().equals("/")){
                    continue;
                }

                if(board.getSnake().size()>MAX_SNAKE_LENGTH && currentVertex.getValue().equals("O")){
                        currentVertex.getAdjacencies().add(new Edge(currentVertex, 2));
                }


                markTheAdjacencies(currentVertex, i, j);

            }
        }

        for (Vertex[] row : graphMatrix) {
            for (Vertex vertex : row) {
                System.out.print(" "+vertex.getValue() + " ");
            }
            System.out.println();

        }

        return graphMatrix;
    }

    public boolean isBorder(Vertex vertex){
        return !vertex.getValue().equals("/");
    }
    public boolean isStone(Vertex vertex){
        return vertex.getValue().equals("O");
    }
    public boolean isDefault(Vertex vertex){
        return vertex!=null && vertex.getValue().equals("*");
    }

    public void markTheAdjacencies(Vertex currentVertex, int i, int j){
        boolean isBigSnake = board.getSnake().size()>MAX_SNAKE_LENGTH;

        // add top if not border and if not a part of snake or stone
        Vertex top = graphMatrix[i-1][j];
        if(isBigSnake && isBorder(top)){
            if(isStone(top)){
                currentVertex.getAdjacencies().add(new Edge(top, 2));
            }
        }
        else if(isDefault(top)){
            currentVertex.getAdjacencies().add(new Edge(top, 1));
        }
        // add bottom if not border and if not a part of snake or stone
        Vertex bottom = graphMatrix[i+1][j];

        if(isBigSnake && isBorder(bottom)){
            if(isStone(bottom)){
                currentVertex.getAdjacencies().add(new Edge(bottom, 2));
            }
        }
        else if(isDefault(bottom)){
            currentVertex.getAdjacencies().add(new Edge(bottom, 1));
        }
        // add left if not border and if not a part of snake or stone
        Vertex left = graphMatrix[i][j-1];
        if(isBigSnake && isBorder(left)){
            if(isStone(left)){
                currentVertex.getAdjacencies().add(new Edge(left, 2));
            }
        }
        else if(isDefault(left)){
            currentVertex.getAdjacencies().add(new Edge(left, 1));
        }
        // add right if not border and if not a part of snake or stone
        Vertex right = graphMatrix[i][j+1];
        if(isBigSnake && isBorder(right)){
            if(isStone(right)){
                currentVertex.getAdjacencies().add(new Edge(right, 2));
            }
        }
        else if(isDefault(right)){
            currentVertex.getAdjacencies().add(new Edge(right, 1));
        }
    }
    public LinkedList<String> extractPath(Vertex currentVertex) {
        LinkedList<String> path = new LinkedList<>();
        Vertex current = currentVertex;
        while (current != null && current.getPrev() != null) {
            System.out.println("current: "+current.getPrev().getY() + " " + current.getPrev().getX());

            path.addFirst( getDirection(current.getPrev(), current).name() );
            current = current.getPrev();
        }
        return path;
    }

    public Direction getDirection(Vertex from, Vertex to) {
        if (from.getX() == to.getX()) {
            if (from.getY() < to.getY()) {
                return Direction.DOWN;
            } else {
                return Direction.UP;
            }
        } else {
            if (from.getX() < to.getX()) {
                return Direction.RIGHT;
            } else {
                return Direction.LEFT;
            }
        }
    }

    public String getDirection(Point from, Point to) {
        if (from.getX() == to.getX()) {
            if (from.getY() < to.getY()) {
                return Direction.DOWN.toString();
            } else {
                return Direction.UP.toString();
            }
        } else {
            if (from.getX() < to.getX()) {
                return Direction.RIGHT.toString();
            } else {
                return Direction.LEFT.toString();
            }
        }
    }

    @Override
    public LinkedList<String> findPathToApple(Vertex[][] graph, Vertex start, Vertex end) {

        PriorityQueue<Vertex> queue = new PriorityQueue<>();
        if(start==null || end==null){
            return new LinkedList<>();
        }
        start.setMinDistance(0);
        queue.add(start);

        while (!queue.isEmpty()) {
            Vertex currentVertex = queue.poll();
            for (Edge edge : currentVertex.getAdjacencies()) {
                Vertex nextVertex = edge.getTarget();

                double weight = edge.getWeight();
                double minDistance = currentVertex.getMinDistance() + weight;
                if (minDistance < nextVertex.getMinDistance()) {
                    queue.remove(nextVertex);
                    nextVertex.setMinDistance(minDistance);
                    nextVertex.setPrev(currentVertex);
                    queue.add(nextVertex);
                }
            }
        }
        return extractPath(end);
    }

    public List<Point> findPathToApplePoint(Vertex[][] graph, Vertex start, Vertex end){
        PriorityQueue<Vertex> queue = new PriorityQueue<>();
        if(start==null || end==null){
            return new LinkedList<>();
        }
        start.setMinDistance(0);
        queue.add(start);

        while (!queue.isEmpty()) {
            Vertex currentVertex = queue.poll();
            for (Edge edge : currentVertex.getAdjacencies()) {
                Vertex nextVertex = edge.getTarget();

                double weight = edge.getWeight();
                double minDistance = currentVertex.getMinDistance() + weight;
                if (minDistance < nextVertex.getMinDistance()) {
                    queue.remove(nextVertex);
                    nextVertex.setMinDistance(minDistance);
                    nextVertex.setPrev(currentVertex);
                    queue.add(nextVertex);
                }
            }
        }
        return extractPath(end).stream().map(s -> new PointImpl(start.getX(), start.getY())).collect(Collectors.toList());
    }

    public LinkedList<String> BFSPathToTail(){
        Point head = board.getHead();
        Point tail = Helpers.getEndOfTail(board);

        leeService.initializeBoard(getConcatObstacles(board, board.getSnake(), board.getStones()));
        Optional<List<Point>> trace = leeService.trace(head, tail, getConcatObstacles(board, board.getSnake(), board.getStones()));

        return trace.map(points -> points.stream().map(p -> getDirection(head, p)).collect(Collectors.toCollection(LinkedList::new))).orElseGet(LinkedList::new);
    }

    public String choiceLongDirectionToEndOfTail(Board board, List<Point> obstacles) {
        Point head = board.getHead();
        Point endOfTail = Helpers.getEndOfTail(board);

        leeService.initializeBoard(obstacles);
        Optional<Point> nextPoint = leeService.neighbours(head)
                .filter(leeService::isOnBoard)
                .filter(p -> !obstacles.contains(p))
                .max(Comparator.comparing(p -> leeService.trace(p, endOfTail, obstacles).orElse(List.of()).size()));

        return getStrDirection(head, nextPoint.orElse(head));
    }

    public String getStrDirection(Point now, Point next) {
        int dx = next.getX() - now.getX();
        int dy = next.getY() - now.getY();

        if (dx > 0) return Direction.RIGHT.toString();
        else if (dx < 0) return Direction.LEFT.toString();
        else if (dy > 0) return Direction.UP.toString();
        else return Direction.DOWN.toString();
    }

    public String getStrDirectionFromTrace(List<Point> trace) {
        return getStrDirection(trace.get(0), trace.get(1));
    }


    @SafeVarargs
    public final List<Point> getConcatObstacles(Board board, List<Point>... obstacles) {
        Point endOfTail = Helpers.getEndOfTail(board);
        return Stream
                .of(obstacles)
                .flatMap(Collection::stream)
                .filter(p -> !p.equals(endOfTail))
                .collect(Collectors.toList());
    }

    public Optional<List<Point>> checkTraceByVirtualSnake(Board board, Optional<List<Point>> trace, int min_step_to_end) {
        if (trace.isEmpty()) return Optional.empty();

        VirtualSnakeService virtualSnake = new VirtualSnakeService(board, trace.get());

        // Получаем препятствия - змея без конца своего хвоста
        List<Point> obstacles = virtualSnake.getSnake().stream().filter(ep -> !ep.equals(virtualSnake.getEndOfTail())).collect(Collectors.toList());

        // Добавляем к препятствиям камень, если его нет в trace
        if (!board.getStones().isEmpty() && !trace.get().contains(board.getStones().get(0)))
            obstacles.add(board.getStones().get(0));

        Optional<List<Point>> virtualTraceToEndOfTail = leeService.trace(virtualSnake.getHead(), virtualSnake.getEndOfTail(), obstacles);

        virtualTraceToEndOfTail.ifPresent(ePoints -> System.out.println("Виртуальный путь к хвсту -  " + ePoints));

        return (virtualTraceToEndOfTail.isPresent() && virtualTraceToEndOfTail.get().size() > min_step_to_end)
                ? trace
                : Optional.empty();
    }




    public int invertY(int y){
        return graphMatrix.length-y-1;
    }

    public LinkedList<String> checkTraceByVirtualSnake(Board board, List<Point> pathToApple) {
        Point tailEndEPoint = Helpers.getEndOfTail(board);
        if (pathToApple.isEmpty()) return new LinkedList<>();

        // Получаем виртуальную змею, змею съевшую яблоко по заданному пути
        VirtualSnakeService virtualSnake = new VirtualSnakeService(board, pathToApple);

        // Получаем препятствия - змея без конца своего хвоста
        List<Point> obstacles = virtualSnake.getSnake().stream().filter(ep -> !ep.equals(virtualSnake.getEndOfTail())).collect(Collectors.toList());

        // Добавляем к препятствиям камень, если его нет в trace
        if (!board.getStones().isEmpty() && !pathToApple.contains(board.getStones().get(0)))
            obstacles.add(board.getStones().get(0));

        // Ищем путь у виртуальной змеи к своему хвосту
        Optional<List<Point>> virtualTraceToEndOfTail = leeService.trace(virtualSnake.getHead(), virtualSnake.getEndOfTail(), obstacles);
        // Выводим путь в консоль
        virtualTraceToEndOfTail.ifPresent(ePoints -> System.out.println("Виртуальный путь к концу >  " + ePoints));

        return (virtualTraceToEndOfTail.isPresent() && virtualTraceToEndOfTail.get().size() > 3)
                ? pathToApple.stream().map(p -> getDirection(board.getHead(), p)).collect(Collectors.toCollection(LinkedList::new))
                : new LinkedList<>();
    }
}
