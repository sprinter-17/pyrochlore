package lattice;

import java.util.HashMap;
import java.util.Map;
import java.util.OptionalInt;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public record Position(int x, int y, int z) {
    public static final int SIZE = 24;
    public static final int THICKNESS = 12;
    private static final Map<Position, Map<Position, Integer>> cache = new HashMap<>();
    private static OptionalInt count = OptionalInt.empty();

    public static Position random(Random random) {
        return Stream.generate(() -> new Position(random.nextInt(SIZE), random.nextInt(SIZE), random.nextInt(THICKNESS)))
                .dropWhile(pos -> !pos.isAllowed())
                .findAny()
                .orElseThrow();
    }

    public static Stream<Position> getAll() {
        return IntStream.range(0, SIZE).boxed()
                .flatMap(x -> IntStream.range(0, SIZE).boxed()
                        .flatMap(y -> IntStream.range(0, THICKNESS).mapToObj(z -> new Position(x, y, z))))
                .filter(Position::isAllowed);
    }

    public static int count() {
        if (count.isEmpty())
            count = OptionalInt.of((int) getAll().count());
        return count.orElseThrow();
    }

    public Stream<Position> neighbours() {
        return Stream.of(
                move(2, 0, 0),
                move(1, 2, 0),
                move(1, -2, 0),
                move(0, 1, 1),
                move(0, -1, 1),
                move(1, 1, 1),
                move(1, -1, 1),
                move(-1, 1, 1),
                move(-1, -1, 1))
                .filter(n -> !equals(n))
                .filter(Position::isAllowed);
    }

    private Position move(int xd, int yd, int zd) {
        return new Position((x + xd + SIZE) % SIZE, (y + yd + SIZE) % SIZE, (z + zd + THICKNESS) % THICKNESS);
    }

    public Map<Position, Integer> positionsWithin(int distance) {
        if (!cache.containsKey(this)) {
            Map<Position, Integer> positions = new HashMap<>();
            positions.put(this, 0);
            for (int d = 0; d < distance; d++)
                expand(positions, d);
            cache.put(this, positions);
        }
        return cache.get(this);
    }

    private void expand(Map<Position, Integer> positions, int distance) {
        positions.entrySet().stream()
                .filter(e -> e.getValue() == distance)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet())
                .stream()
                .flatMap(Position::neighbours)
                .filter(n -> !positions.containsKey(n))
                .forEach(n -> positions.put(n, distance + 1));
    }

    public boolean isAllowed() {
        if (z % 2 == 1)             // sparse layers
            return x % 4 == 1 && y % 8 == z % 4 || x % 4 == 3 && y % 8 == z % 4 + 4;
        else if (y % 4 == 0)        // base of triangles
            return x % 2 == 0;
        else if (y % 4 == 2)        // apex of triangles
            return x % 4 == (y % 8) / 2;
        else                        // everything else is empty
            return false;
    }
}
