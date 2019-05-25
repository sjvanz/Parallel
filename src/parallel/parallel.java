package parallel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.io.File;

public class parallel {

	public static void main(String[] args) throws InterruptedException, ExecutionException {
		int cores = Runtime.getRuntime().availableProcessors();
		System.out.println("number of cores available: " + cores);
		double startTime = System.nanoTime();
		ExecutorService pool = Executors.newFixedThreadPool(cores);
		List<Callable<List<Player>>> taskList = new ArrayList<>();
		File[] files = new File("resources/data/").listFiles();
		for (File file : files) {
			taskList.add(new CsvImporter(file.getAbsolutePath()));
		}
		List<Future<List<Player>>> futurePlayers = pool.invokeAll(taskList);
		List<Player> players = new ArrayList<>();
		for (Future<List<Player>> f : futurePlayers) {
			players.addAll(f.get());
		}
		pool.shutdown();
		for (Player player : players) {
			System.out.println("Name " + player.Name+ " Ballcontrol"+ player.BallControl);
			player.setPotential();
		}
		
		Comparator<Player> compareByPotential = (Player p1, Player p2) -> p1.Potential.compareTo( p2.Potential);
		 
		Collections.sort(players, compareByPotential);
		
		for (Player player : players) {
			System.out.println("Name " + player.Name);
		}
		double nano_endTime = System.nanoTime();
		double total = nano_endTime - startTime;
		System.out.println("Nano endtime: " + total);

	}

}
