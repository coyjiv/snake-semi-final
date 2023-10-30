package com.codenjoy.dojo.snake.model;

import java.util.ArrayList;
import java.util.List;

public class Vertex implements Comparable<Vertex>{
    private String value;
    private List<Edge> adjacencies = new ArrayList<>();
    private double minDistance = Double.POSITIVE_INFINITY;
    private boolean isSnakeHead = false;
    private Vertex prev;
    private int x;
    private int y;

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public Vertex(String value, int x, int y) {
        this.value = value;
        this.x = x;
        this.y = y;
    }

    public Vertex(String value, boolean isSnakeHead, int x, int y) {
        this.value = value;
        this.isSnakeHead = isSnakeHead;
        this.x = x;
        this.y = y;
    }


    public boolean isSnakeHead() {
        return isSnakeHead;
    }

    public void setSnakeHead(boolean snakeHead) {
        isSnakeHead = snakeHead;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public List<Edge> getAdjacencies() {
        return adjacencies;
    }

    public void setAdjacencies(List<Edge> adjacencies) {
        this.adjacencies = adjacencies;
    }

    public double getMinDistance() {
        return minDistance;
    }

    public void setMinDistance(double minDistance) {
        this.minDistance = minDistance;
    }

    public Vertex getPrev() {
        return prev;
    }

    public void setPrev(Vertex prev) {
        this.prev = prev;
    }

    @Override
    public String toString() {
        return "{" + value + '}';
    }

    @Override
    public int compareTo(Vertex o) {
        return Double.compare(this.minDistance, o.minDistance);
    }
}
