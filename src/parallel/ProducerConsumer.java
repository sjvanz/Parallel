package parallel;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Stream;

import static parallel.parallel.mapToItem;


public class ProducerConsumer implements Runnable {

    private static final int CONSUMER_COUNT = 1;
    private final static BlockingQueue<Player> linesReadQueue = new ArrayBlockingQueue<>(30);

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
        String data = "data.csv";
        long startTime = System.nanoTime();
        try {
            File inputF =
                    new File(data);
            InputStream inputFS = new FileInputStream(inputF);
            BufferedReader br = new BufferedReader(new InputStreamReader(inputFS));

            //   dit wordt nog niet gebruik.
            List<Player> filteredByNationalityList = new ArrayList<Player>();
            List<Player> filteredByAgeList = new ArrayList<Player>();
            List<Player> BestValueFromTenProcentLists = new ArrayList<Player>();

            Comparator<Player> compareByNationality = Comparator.comparing(o -> o.getNationality());

            Comparator<Player> compareByNationalityThenPotential = compareByNationality.reversed().thenComparing((Player p) -> p.Potential).reversed();
            Comparator<Player> compareByAgeThenPotential = Comparator
                    .comparing(Player::getAge).thenComparing((Player p) -> p.Potential).reversed();

            Comparator<Player> compareByPotential = Comparator.comparing(Player::getPotential).reversed();
            // tot en met hier. Maar ik denk dat dit nog wel makkelijk in te bouwen is. Hij doet nu alles en print alles.
            // in principe kunnen we al die system out prints er ook tussen uit halen voor de leesbaarheid.

            //Java 8: Stream class
            Stream<Player> lines = Files.lines(Paths.get(data), StandardCharsets.UTF_8).skip(1).map(mapToItem);

            // deze vervangen voor de gefilterde lijsten.
            for (Player line : (Iterable<Player>) lines::iterator) {

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
                Player lineToProcess = linesReadQueue.take();
                System.out.println("procesed:" + lineToProcess.getName() + " " + lineToProcess.Potential);
                System.out.println(Thread.currentThread().getName() + ":: consumer count:" + linesReadQueue.size());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println(Thread.currentThread().getName() + " consumer is done");
    }

}
