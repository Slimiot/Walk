package com.slimiot.walk.Home;

import java.util.ArrayDeque;
import java.util.Queue;

public class MovingAverage {
    private Queue<Integer> window;
    private int size;
    private double sum,avg;

    public MovingAverage(int size) {
        this.window = new ArrayDeque<>(size);
        this.size = size;
        this.sum = 0;
    }

    public double next(int val) {
        if (window.size() == size) {
            sum -= window.remove();
        }
        sum += val;
        window.add(val);
        avg = sum / window.size();
        return avg;
    }
    public double getAverage(){
        return avg;
    }
}