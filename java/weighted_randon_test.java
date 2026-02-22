// Java port of go/weighted_randon_test.go
// Note: functions mirror the original behaviors (including quirks):
// - S4 samples after sorting weights descending and returns index in the sorted array.
// - S5/S6 return the weight value at the chosen position (not the index).

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

class WeightedRandonTest {
    // S1: expand indexes by weight and pick uniformly
    static int weightedRandomS1(int[] weights) {
        if (weights == null || weights.length == 0) return 0;
        List<Integer> indexList = new ArrayList<>();
        for (int i = 0; i < weights.length; i++) {
            int w = Math.max(0, weights[i]);
            for (int c = 0; c < w; c++) indexList.add(i);
        }
        if (indexList.isEmpty()) return 0;
        int idx = ThreadLocalRandom.current().nextInt(indexList.size());
        return indexList.get(idx);
    }

    // S2: hard-coded 4-bucket distribution matching the Go example
    static int weightedRandomS2() {
        int r = ThreadLocalRandom.current().nextInt(15); // 0..14
        if (r <= 1) {
            return 0;
        } else if (1 < r && r <= 3) {
            return 1;
        } else if (3 < r && r <= 7) {
            return 2;
        } else {
            return 3;
        }
    }

    // S3: subtractive walk using total sum
    static int weightedRandomS3(int[] weights) {
        int sum = 0;
        for (int w : weights) sum += w;
        if (sum <= 0) return 0;
        int r = ThreadLocalRandom.current().nextInt(sum); // 0..sum-1
        for (int i = 0; i < weights.length; i++) {
            r -= weights[i];
            if (r < 0) return i;
        }
        return Math.max(0, weights.length - 1);
    }

    // S4: same as S3 but after sorting weights DESC (index is for sorted array)
    static int weightedRandomS4(int[] weights) {
        if (weights == null || weights.length == 0) return 0;
        int[] copy = Arrays.copyOf(weights, weights.length);
        // sort descending
        Arrays.sort(copy);
        for (int i = 0; i < copy.length / 2; i++) {
            int tmp = copy[i];
            copy[i] = copy[copy.length - 1 - i];
            copy[copy.length - 1 - i] = tmp;
        }
        int sum = 0;
        for (int w : copy) sum += w;
        if (sum <= 0) return 0;
        int r = ThreadLocalRandom.current().nextInt(sum);
        for (int i = 0; i < copy.length; i++) {
            r -= copy[i];
            if (r < 0) return i;
        }
        return Math.max(0, copy.length - 1);
    }

    // S5: prefix-sum + binary search, returns weight value at chosen idx
    static int weightedRandomS5(int[] weights) {
        int sum = 0;
        int[] prefix = new int[weights.length];
        for (int i = 0; i < weights.length; i++) {
            sum += weights[i];
            prefix[i] = sum;
        }
        if (sum <= 0) return 0;
        int r = ThreadLocalRandom.current().nextInt(sum);
        int idx = lowerBound(prefix, r); // first i with prefix[i] >= r
        return weights[idx];
    }

    // S6: same as S5 but with custom lower bound, returns weight value
    static int weightedRandomS6(int[] weights) {
        int sum = 0;
        int[] prefix = new int[weights.length];
        for (int i = 0; i < weights.length; i++) {
            sum += weights[i];
            prefix[i] = sum;
        }
        if (sum <= 0) return 0;
        int r = ThreadLocalRandom.current().nextInt(sum);
        int idx = lowerBound(prefix, r);
        return weights[idx];
    }

    // S7: floating-point incremental selection (aka Efraimidis-Spirakis style)
    static int weightedRandomS7(double[] weights) {
        double sum = 0.0;
        int winner = 0;
        for (int i = 0; i < weights.length; i++) {
            double v = weights[i];
            sum += v;
            double f = ThreadLocalRandom.current().nextDouble();
            if (f * sum < v) {
                winner = i;
            }
        }
        return winner;
    }

    // lower bound: first index i s.t. a[i] >= x; a must be non-decreasing
    static int lowerBound(int[] a, int x) {
        int i = 0, j = a.length;
        while (i < j) {
            int h = (i + j) >>> 1;
            if (a[h] < x) i = h + 1; else j = h;
        }
        return i;
    }

    // Helper used by original benchmarks
    static int[] mockWeights(int n) {
        int[] w = new int[n];
        ThreadLocalRandom rnd = ThreadLocalRandom.current();
        for (int i = 0; i < n; i++) w[i] = rnd.nextInt(10);
        return w;
    }

    // Minimal demonstration similar to Go tests
    public static void main(String[] args) {
        int rounds = 100;
        int[] ints = new int[] {1, 2, 4, 8};
        double[] floats = new double[] {1, 2, 4, 8};

        Map<Integer, Integer> m1 = new HashMap<>();
        for (int i = 0; i < rounds; i++) m1.merge(weightedRandomS1(ints), 1, Integer::sum);
        System.out.println("S1 counts: " + m1);

        Map<Integer, Integer> m2 = new HashMap<>();
        for (int i = 0; i < rounds; i++) m2.merge(weightedRandomS2(), 1, Integer::sum);
        System.out.println("S2 counts: " + m2);

        Map<Integer, Integer> m3 = new HashMap<>();
        for (int i = 0; i < rounds; i++) m3.merge(weightedRandomS3(ints), 1, Integer::sum);
        System.out.println("S3 counts: " + m3);

        Map<Integer, Integer> m4 = new HashMap<>();
        for (int i = 0; i < rounds; i++) m4.merge(weightedRandomS4(ints), 1, Integer::sum);
        System.out.println("S4 counts (sorted index): " + m4);

        Map<Integer, Integer> m5 = new HashMap<>();
        for (int i = 0; i < rounds; i++) m5.merge(weightedRandomS5(ints), 1, Integer::sum);
        System.out.println("S5 counts (weight value): " + m5);

        Map<Integer, Integer> m6 = new HashMap<>();
        for (int i = 0; i < rounds; i++) m6.merge(weightedRandomS6(ints), 1, Integer::sum);
        System.out.println("S6 counts (weight value): " + m6);

        Map<Integer, Integer> m7 = new HashMap<>();
        for (int i = 0; i < rounds; i++) m7.merge(weightedRandomS7(floats), 1, Integer::sum);
        System.out.println("S7 counts: " + m7);
    }
}
