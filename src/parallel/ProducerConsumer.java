package parallel;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.*;
import java.util.stream.Stream;

public class ProducerConsumer implements Runnable {

    private static final int CONSUMER_COUNT = 1;
    private final static BlockingQueue<String> linesReadQueue = new ArrayBlockingQueue<>(30);

    private boolean isConsumer;
    private static boolean producerIsDone = false;

    public ProducerConsumer(boolean consumer) {
        this.isConsumer = consumer;
    }

    public static void main(String[] args) {

        ExecutorService producerPool = Executors.newFixedThreadPool(1);

        // call run method
        producerPool.submit(new ProducerConsumer(false));

        // create a pool of consumer threads to parse the lines read
        ExecutorService consumerPool = Executors.newFixedThreadPool(CONSUMER_COUNT);
        for (int i = 0; i < CONSUMER_COUNT; i++) {
            consumerPool.submit(new ProducerConsumer(true)); // run method is called
        }

        producerPool.shutdown();
        consumerPool.shutdown();
    }

    private void readFile() {
        Path file = Paths.get("data.csv");
        long startTime = System.nanoTime();
        try {
            //Java 8: Stream class
            Stream<String> lines = Files.lines(file, StandardCharsets.UTF_8).skip(1);

            for (String line : (Iterable<String>) lines::iterator) {
                System.out.println("read=" + line);
                linesReadQueue.put(line); //blocked if reaches its capacity, until consumer consumes
//                System.out.println(Thread.currentThread().getName() + ":: producer count = " + linesReadQueue.size());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        long endTime = System.nanoTime();
        long elapsedTimeInMillis = TimeUnit.MILLISECONDS.convert((endTime - startTime), TimeUnit.NANOSECONDS);
        System.out.println("Total elapsed time: " + elapsedTimeInMillis + " ms");
        System.out.println(Thread.currentThread().getName() + " consumer is done");
    }

    @Override
    public void run() {
        if (isConsumer) {
            consume();
        } else {
            readFile(); //produce data by reading a file
        }
    }

    private void consume() {
        try {
            while (!producerIsDone || (producerIsDone && !linesReadQueue.isEmpty())) {
                String lineToProcess = linesReadQueue.take();
//                processCpuDummy(); // some CPU intensive processing
                System.out.println("procesed:" + lineToProcess);
                System.out.println(Thread.currentThread().getName() + ":: consumer count:" + linesReadQueue.size());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println(Thread.currentThread().getName() + " consumer is done");
    }

//    private void processCpuDummy() {
//        //takes ~ 15 ms of CPU time
//        //did not use Thread.sleep() as it does not consume any CPU cycles
//        for (long i = 0; i < 100000000L; i++) {
//            i = i + 1;
//        }
//    }
}
