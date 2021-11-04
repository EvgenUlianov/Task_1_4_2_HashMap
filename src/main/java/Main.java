import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.LongAdder;

public class Main {
    public static void main(String[] args) {
        System.out.println("Задача 2. Разница в производительности");

        System.out.println("Размер Map | потоков | итераций | ConcurrentHashMap | SynchronizedMap | ConcurrentSkipListMap ");

//        final int mapLimit = 10000;
//        final int threads = 1000;
//        final int iterations = 100000;
        for (int i = 2; i < 6 ; i++) {
            for (int j = 2; j < 4 ; j++) {
                for (int k = 2; k < 5 ; k++) {
                    int mapLimit = (int) Math.pow(10, i);
                    int threads = (int) Math.pow(10, j);
                    int iterations = (int) Math.pow(10, k);
                    double speedConcurrentHashMap = testSpeed(mapLimit, threads, iterations, new ConcurrentHashMap<>());
                    double speedSynchronizedMap = testSpeed(mapLimit, threads, iterations, Collections.synchronizedMap(new HashMap<>()));
                    double speedConcurrentSkipListMap = testSpeed(mapLimit, threads, iterations, new ConcurrentSkipListMap());

                    System.out.printf(" %d | %d | %d | %.3f | %.3f | %.3f \n",
                            mapLimit,
                            threads,
                            iterations,
                            speedConcurrentHashMap,
                            speedSynchronizedMap,
                            speedConcurrentSkipListMap);
                }
            }
        }

        System.out.println("Тестирование завершено");
    }


    public static double testSpeed(int mapLimit, int threads, int iterations, Map<Integer, Integer> map){
        LongAdder accumulator = new LongAdder();

        MapTester.MapTesterTemplate template = new MapTester.MapTesterTemplate()
                .setAccumulator(accumulator)
                .setIterations(iterations)
                .setMapLimit(mapLimit)
                .setMap(map);

        List<MapTester> mapTesters = new ArrayList<>();
        final ExecutorService threadPool = Executors.newFixedThreadPool(threads);
        CountDownLatch doneSignal = new CountDownLatch(threads);
        for (int i = 0; i < threads; i++) {
            MapTester tester = new MapTester(i, template, doneSignal);
            mapTesters.add(tester);
            threadPool.execute(tester);
        }

        try {
            doneSignal.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        threadPool.shutdownNow();

        return (double) accumulator.sum() / threads;


    }
}
