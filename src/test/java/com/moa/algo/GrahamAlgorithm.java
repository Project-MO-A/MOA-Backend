package com.moa.algo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import static java.util.Comparator.comparingDouble;

public class GrahamAlgorithm {

    private List<Point> points = new ArrayList<>();

    @BeforeEach
    void initData() {
        points.add(new Point(0.0, 0.0));
        points.add(new Point(1.2, 2.3));
        points.add(new Point(2.3, 3.6));
        points.add(new Point(2.3, 3.3));
        points.add(new Point(1.5, 2.8));
        points.add(new Point(1.35, 3.2));
    }

    @Test
    void test() {
        Graham graham = new Graham();
        List<Point> outSide = graham.getOutSide(points);
        System.out.println("outSide.size() = " + outSide.size());
        Double sumX = 0.0;
        Double sumY = 0.0;
        for (Point point : outSide) {
            System.out.println("point = " + point);
            sumX += point.x;
            sumY += point.y;
        }

        double resultX = sumX / outSide.size();
        double resultY = sumY / outSide.size();

        System.out.println("resultX = " + resultX);
        System.out.println("resultY = " + resultY);

        for (Point point : outSide) {
            double distance = Math.sqrt(Math.pow(point.x - sumX, 2) + Math.pow(point.y - sumY, 2));
            System.out.println("distance = " + distance);
        }
    }

    class Graham {
        public List<Point> getOutSide(List<Point> points) {
            points.sort((o1, o2) -> o1.x == o2.x ? Double.compare(o1.y, o2.y) : Double.compare(o1.x, o2.x));

            Point start = points.get(0);
            points.remove(start);
            points.forEach(point -> point.calculateTan(start));
            points.sort(comparingDouble(o -> o.tan));

            Stack<Point> stack = getOutSidePoints(points, start);
            List<Point> result = new ArrayList<>();
            while (!stack.isEmpty()) {
                result.add(stack.pop());
            }
            return result;
        }

        private Stack<Point> getOutSidePoints(List<Point> points, Point start) {
            Stack<Point> stack = new Stack<>();
            stack.push(start);
            stack.push(points.get(0));

            for (int i = 1; i < points.size(); i++) {
                while (!ccw(stack.get(stack.size() - 2), stack.get(stack.size() - 1), points.get(i))) {
                    stack.pop();
                }
                stack.push(points.get(i));
            }
            return stack;
        }

        private boolean ccw(Point first, Point second, Point third) {
            return first.x * (second.y - third.y) + second.x * (third.y - first.y) + third.x * (first.y - second.y) > 0.0;
        }
    }

    class Point {
        private double x;
        private double y;
        private double tan;

        public Point(double x, double y) {
            this.x = x;
            this.y = y;
        }

        public void calculateTan(Point start) {
            this.tan = (this.y - start.y) / (this.x - start.x);
        }

        @Override
        public String toString() {
            return "Point{" +
                    "x=" + x +
                    ", y=" + y +
                    '}';
        }
    }
}



