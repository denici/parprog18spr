import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Main {

    private static final int TIME_BETWEEN_CAR_ARRIVALS = 1000;
    private static final int TIME_FOR_CHANGING_LIGHTS = 500;
    private static final int TIME_FOR_CAR_RIDE = 100;

    private static Queue<Car> inTraffic = new ConcurrentLinkedQueue<>();

    public static void main(String[] args) throws InterruptedException {
        Thread carGenerator = new Thread(new CarGenerator());
        Thread trafficLights = new Thread(new TrafficLights());

        carGenerator.start();
        trafficLights.start();

        carGenerator.join();
        trafficLights.join();
    }

    static class TrafficLights implements Runnable {
        HashSet<Direction> permittedDirections = new HashSet<>();

        private static HashMap<Direction, HashSet<Direction>> incompatibleDirections = new HashMap<>();
        static {
            HashSet<Direction> notCompatibleWithNorthSouth = new HashSet<>();
            notCompatibleWithNorthSouth.add(new Direction(CardinalDirection.EAST, CardinalDirection.WEST));
            notCompatibleWithNorthSouth.add(new Direction(CardinalDirection.WEST, CardinalDirection.EAST));
            notCompatibleWithNorthSouth.add(new Direction(CardinalDirection.WEST, CardinalDirection.NORTH));
            notCompatibleWithNorthSouth.add(new Direction(CardinalDirection.SOUTH, CardinalDirection.WEST));
            incompatibleDirections.put(
                    new Direction(CardinalDirection.NORTH, CardinalDirection.SOUTH),
                    notCompatibleWithNorthSouth
            );

            HashSet<Direction> notCompatibleWithNorthEast = new HashSet<>();
            notCompatibleWithNorthEast.add(new Direction(CardinalDirection.EAST, CardinalDirection.WEST));
            notCompatibleWithNorthEast.add(new Direction(CardinalDirection.EAST, CardinalDirection.SOUTH));
            notCompatibleWithNorthEast.add(new Direction(CardinalDirection.WEST, CardinalDirection.NORTH));
            notCompatibleWithNorthEast.add(new Direction(CardinalDirection.SOUTH, CardinalDirection.NORTH));
            incompatibleDirections.put(
                    new Direction(CardinalDirection.NORTH, CardinalDirection.EAST),
                    notCompatibleWithNorthEast
            );

            HashSet<Direction> notCompatibleWithWestNorth = new HashSet<>();
            notCompatibleWithWestNorth.add(new Direction(CardinalDirection.NORTH, CardinalDirection.SOUTH));
            notCompatibleWithWestNorth.add(new Direction(CardinalDirection.NORTH, CardinalDirection.EAST));
            notCompatibleWithWestNorth.add(new Direction(CardinalDirection.SOUTH, CardinalDirection.WEST));
            notCompatibleWithWestNorth.add(new Direction(CardinalDirection.EAST, CardinalDirection.WEST));
            incompatibleDirections.put(
                    new Direction(CardinalDirection.WEST, CardinalDirection.NORTH),
                    notCompatibleWithWestNorth
            );

            HashSet<Direction> notCompatibleWithWestEast = new HashSet<>();
            notCompatibleWithNorthSouth.add(new Direction(CardinalDirection.NORTH, CardinalDirection.SOUTH));
            notCompatibleWithNorthSouth.add(new Direction(CardinalDirection.SOUTH, CardinalDirection.NORTH));
            notCompatibleWithNorthSouth.add(new Direction(CardinalDirection.SOUTH, CardinalDirection.WEST));
            notCompatibleWithNorthSouth.add(new Direction(CardinalDirection.EAST, CardinalDirection.SOUTH));
            incompatibleDirections.put(
                    new Direction(CardinalDirection.WEST, CardinalDirection.EAST),
                    notCompatibleWithWestEast
            );

            HashSet<Direction> notCompatibleWithSouthNorth = new HashSet<>();
            notCompatibleWithNorthSouth.add(new Direction(CardinalDirection.EAST, CardinalDirection.WEST));
            notCompatibleWithNorthSouth.add(new Direction(CardinalDirection.WEST, CardinalDirection.EAST));
            notCompatibleWithNorthSouth.add(new Direction(CardinalDirection.EAST, CardinalDirection.SOUTH));
            notCompatibleWithNorthSouth.add(new Direction(CardinalDirection.NORTH, CardinalDirection.EAST));
            incompatibleDirections.put(
                    new Direction(CardinalDirection.SOUTH, CardinalDirection.NORTH),
                    notCompatibleWithSouthNorth
            );

            HashSet<Direction> notCompatibleWithSouthWest = new HashSet<>();
            notCompatibleWithNorthEast.add(new Direction(CardinalDirection.WEST, CardinalDirection.EAST));
            notCompatibleWithNorthEast.add(new Direction(CardinalDirection.WEST, CardinalDirection.NORTH));
            notCompatibleWithNorthEast.add(new Direction(CardinalDirection.EAST, CardinalDirection.SOUTH));
            notCompatibleWithNorthEast.add(new Direction(CardinalDirection.NORTH, CardinalDirection.SOUTH));
            incompatibleDirections.put(
                    new Direction(CardinalDirection.SOUTH, CardinalDirection.WEST),
                    notCompatibleWithSouthWest
            );

            HashSet<Direction> notCompatibleWithEastSouth = new HashSet<>();
            notCompatibleWithWestNorth.add(new Direction(CardinalDirection.SOUTH, CardinalDirection.NORTH));
            notCompatibleWithWestNorth.add(new Direction(CardinalDirection.SOUTH, CardinalDirection.WEST));
            notCompatibleWithWestNorth.add(new Direction(CardinalDirection.NORTH, CardinalDirection.EAST));
            notCompatibleWithWestNorth.add(new Direction(CardinalDirection.WEST, CardinalDirection.EAST));
            incompatibleDirections.put(
                    new Direction(CardinalDirection.EAST, CardinalDirection.SOUTH),
                    notCompatibleWithEastSouth
            );

            HashSet<Direction> notCompatibleWithEastWest = new HashSet<>();
            notCompatibleWithNorthSouth.add(new Direction(CardinalDirection.NORTH, CardinalDirection.SOUTH));
            notCompatibleWithNorthSouth.add(new Direction(CardinalDirection.SOUTH, CardinalDirection.NORTH));
            notCompatibleWithNorthSouth.add(new Direction(CardinalDirection.NORTH, CardinalDirection.EAST));
            notCompatibleWithNorthSouth.add(new Direction(CardinalDirection.WEST, CardinalDirection.NORTH));
            incompatibleDirections.put(
                    new Direction(CardinalDirection.EAST, CardinalDirection.WEST),
                    notCompatibleWithEastWest
            );
        }

        public TrafficLights() {
            change();
        }

        void change() {
            permittedDirections.clear();
            for (int i = 0; i < 10; ++i) {
                Direction newDirection = Direction.random();
                boolean isInterceptOtherDirections = false;
                for (Direction d : permittedDirections) {
                    HashSet<Direction> directions = incompatibleDirections.get(d);
                    if (directions != null && directions.contains(newDirection)) {
                        isInterceptOtherDirections = true;
                        break;
                    }
                }
                if (isInterceptOtherDirections) {
                    continue;
                }
                permittedDirections.add(newDirection);
            }
            System.out.println("Traffic lights changing directions!");
            try {
                Thread.sleep(TIME_FOR_CHANGING_LIGHTS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            while (true) {
                if (inTraffic.isEmpty()) {
                    continue;
                }
                Car car = inTraffic.remove();
                while (!permittedDirections.contains(car.getDirection())) {
                    this.change();
                }
                car.ride();
                // рандомное изменение состояние светофора
                if (new Random().nextBoolean()) {
                    this.change();
                }
            }
        }
    }

    static class CarGenerator implements Runnable {

        //1st variant
        static Car[] carTypes = {
                new Car(new Direction(CardinalDirection.NORTH, CardinalDirection.SOUTH)),
                new Car(new Direction(CardinalDirection.WEST, CardinalDirection.EAST)),
                new Car(new Direction(CardinalDirection.SOUTH, CardinalDirection.WEST))
        };
        //2nd variant
//              static Car[] carTypes = {
//                  new Car(new Direction(Direction.NORTH, Direction.SOUTH)),
//                  new Car(new Direction(Direction.WEST, Direction.EAST)),
//                  new Car(new Direction(Direction.SOUTH, Direction.NORTH)),
//                  new Car(new Direction(Direction.EAST, Direction.SOUTH))
//              };

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                Car car = carTypes[new Random().nextInt(carTypes.length)];
                inTraffic.add(car);
                System.out.println("New car arrived");
                try {
                    Thread.sleep(TIME_BETWEEN_CAR_ARRIVALS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static class Car implements Comparable<Car> {
        Direction direction;

        public Car(Direction direction) {
            this.direction = direction;
        }

        public Direction getDirection() {
            return direction;
        }

        public void ride() {
            System.out.println(System.currentTimeMillis() + ": " + this.toString() + " rides");
            try {
                Thread.sleep(TIME_FOR_CAR_RIDE);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        @Override
        public String toString() {
            return "Car " + direction.toString();
        }

        @Override
        public int compareTo(Car o) {
            return 0;
        }
    }

    static class Direction {
        public static Direction random() {
            Random random = new Random();
            int indexOfCardinalDirectionFrom = random.nextInt(CardinalDirection.values().length);
            int indexOfCardinalDirectionTo = random.nextInt(CardinalDirection.values().length - 1);
            if (indexOfCardinalDirectionTo >= indexOfCardinalDirectionFrom) {
                indexOfCardinalDirectionTo++;
            }
            return new Direction(
                    CardinalDirection.values()[indexOfCardinalDirectionFrom],
                    CardinalDirection.values()[indexOfCardinalDirectionTo]
            );
        }

        CardinalDirection from;
        CardinalDirection to;

        public Direction(CardinalDirection from, CardinalDirection to) {
            this.from = from;
            this.to = to;
        }

        @Override
        public String toString() {
            return "From " + from.toString() + " to " + to.toString();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Direction direction = (Direction) o;
            return from == direction.from &&
                    to == direction.to;
        }

        @Override
        public int hashCode() {
            return Objects.hash(from, to);
        }
    }

    enum CardinalDirection {
        NORTH,
        SOUTH,
        EAST,
        WEST
    }
}
