package com.moa.service.util;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import static java.util.Comparator.comparingDouble;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class GrahamUtils {

    public static List<Point> getOutSide(List<Point> points) {
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

    private static Stack<Point> getOutSidePoints(List<Point> points, Point start) {
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

    private static boolean ccw(Point first, Point second, Point third) {
        return first.x * (second.y - third.y) + second.x * (third.y - first.y) + third.x * (first.y - second.y) > 0.0;
    }

    @Getter
    public static class Point {
        private final double x;
        private final double y;
        private double tan;

        public Point(double x, double y) {
            this.x = x;
            this.y = y;
        }

        public void calculateTan(Point start) {
            this.tan = (this.y - start.y) / (this.x - start.x);
        }
    }
}
