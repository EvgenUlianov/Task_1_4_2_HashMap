import java.time.LocalDateTime;
import java.time.temporal.TemporalField;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.LongAdder;

public class MapTester implements Runnable{
    private final String name;
    private final int mapLimit;
    private final int iterations;
    private final Map<Integer, Integer> map;
    private LongAdder accumulator;
    private final CountDownLatch doneSignal;

    public MapTester(int number, MapTesterTemplate template, CountDownLatch doneSignal) {
        name = String.format("Счетчик %d", number);
        this.map = template.map;
        this.mapLimit = template.mapLimit;
        this.iterations = template.iterations;
        this.accumulator = template.accumulator;
        this.doneSignal = doneSignal;
    }

    static public class MapTesterTemplate {
        private int mapLimit;
        private int iterations;
        private Map<Integer, Integer> map;
        private LongAdder accumulator;

        public MapTesterTemplate setMapLimit(int mapLimit) {
            this.mapLimit = mapLimit;
            return this;
        }

        public MapTesterTemplate setIterations(int iterations) {
            this.iterations = iterations;
            return this;
        }

        public MapTesterTemplate setMap(Map<Integer, Integer> map) {
            this.map = map;
            return this;
        }

        public MapTesterTemplate setAccumulator(LongAdder accumulator) {
            this.accumulator = accumulator;
            return this;
        }
    }

    @Override
    public void run() {
        long millisecondsBegin = new  Date().getTime();
        for (int i = 0; i < iterations; i++) {
            Random random = new Random();
            final int randomKey = 1 + random.nextInt(mapLimit - 1);

            Integer value = map.get(randomKey);
            if (value == null)
                value = 0;
            value = value + i;
            map.put(randomKey, value);
        }
        long millisecondsEnd = new Date().getTime();
        long timeUsed = millisecondsEnd - millisecondsBegin;

//        System.out.printf("%s завершил за %d\n", name, timeUsed);
        accumulator.add(timeUsed);
        doneSignal.countDown();
    }

}
