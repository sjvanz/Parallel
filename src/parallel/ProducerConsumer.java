package parallel;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.concurrent.*;
import java.util.stream.Stream;

import static parallel.parallel.distinctByProperty;
import static parallel.parallel.mapToItem;

public class ProducerConsumer implements Runnable {

    private static final int CONSUMER_COUNT = 4;
    private final static BlockingQueue<Player> linesReadQueueNationalityPotential = new ArrayBlockingQueue<>(30);
    private final static BlockingQueue<Player> linesReadQueueAgeAndPotential = new ArrayBlockingQueue<Player>(30);

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

    @Override
    public void run() {
        if (isConsumer) {
            consumeNationalityAndPotential();
//            consumeAgeAndPotential();

        } else {
            filterFileNationalityPotential();
//            filterFileAgePotential();
        }
    }

    private void filterFileNationalityPotential() {
        String data = "data.csv";
        long startTime = System.nanoTime();
        try {

            Comparator<Player> compareByNationality = Comparator.comparing(o -> o.getNationality());

            Comparator<Player> compareByNationalityThenPotential = compareByNationality.reversed().thenComparing((Player p) -> p.Potential).reversed();
            Comparator<Player> compareByAgeThenPotential = Comparator
                    .comparing(Player::getAge).thenComparing((Player p) -> p.Potential).reversed();
            Comparator<Player> compareByPotential = Comparator.comparing(Player::getPotential).reversed();

            //Java 8: Stream class
            Stream<Player> linesByNationality = Files.lines(Paths.get(data), StandardCharsets.UTF_8)
                    .skip(1).map(mapToItem)
                    .filter(distinctByProperty(Player::getNationality))
                    .sorted(compareByNationalityThenPotential);

            for (Player player : (Iterable<Player>) linesByNationality::iterator) {
//                System.out.println("read=" + line);
                linesReadQueueNationalityPotential.put(player); //blocked if reaches its capacity, until consumer consumes
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

    private void filterFileAgePotential() {
        String data = "data.csv";
        long startTime = System.nanoTime();
        try {

            Comparator<Player> compareByNationality = Comparator.comparing(o -> o.getNationality());

            Comparator<Player> compareByNationalityThenPotential = compareByNationality.reversed().thenComparing((Player p) -> p.Potential).reversed();
            Comparator<Player> compareByAgeThenPotential = Comparator
                    .comparing(Player::getAge).thenComparing((Player p) -> p.Potential).reversed();
            Comparator<Player> compareByPotential = Comparator.comparing(Player::getPotential).reversed();

            //Java 8: Stream class
            Stream<Player> linesByAge = Files.lines(Paths.get(data), StandardCharsets.UTF_8)
                    .skip(1).map(mapToItem)
                    .filter(distinctByProperty(Player::getAge))
                    .sorted(compareByAgeThenPotential);

            for (Player player : (Iterable<Player>) linesByAge::iterator) {
//                System.out.println("read=" + line);
                linesReadQueueAgeAndPotential.put(player); //blocked if reaches its capacity, until consumer consumes
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

    private void consumeNationalityAndPotential() {
        try {
            System.out.println("Gesorteerd op nationaliteit en potentie");
            while (!producerIsDone || (producerIsDone && !linesReadQueueNationalityPotential.isEmpty())) {
                Player player = linesReadQueueNationalityPotential.take();
//                Player player2 = linesReadQueueAgeAndPotential.take();
                System.out.println(player.getNationality() + ": " + player.getName() + " " + player.Potential);
//                System.out.println(player2.getAge() + ": " + player2.getName() + " " + player2.Potential);
//                System.out.println(Thread.currentThread().getName() + ":: consumer count:" + linesReadQueue.size());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(Thread.currentThread().getName() + " consumer is done");
    }

    private void consumeAgeAndPotential() {
        try {
            System.out.println("Gesorteerd leeftijd en potentie");
            while (!producerIsDone || (producerIsDone && !linesReadQueueAgeAndPotential.isEmpty())) {
                Player player = linesReadQueueAgeAndPotential.take();
                System.out.println(player.getAge() + ": " + player.getName() + " " + player.getPotential());
//                System.out.println(Thread.currentThread().getName() + ":: consumer count:" + linesReadQueue.size());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(Thread.currentThread().getName() + " consumer is done");
    }
}
