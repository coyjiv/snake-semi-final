package com.codenjoy.dojo.snake.model;

public class Edge {
    private Vertex target;
    private double weight;

    public Vertex getTarget() {
        return target;
    }

    public void setTarget(Vertex target) {
        this.target = target;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public Edge(Vertex target, double weight) {
        this.target = target;
        this.weight = weight;
    }

    @Override
    public String toString() {
        return "{" +
                "target=" + target +
                ", weight=" + weight +
                '}';
    }
}
