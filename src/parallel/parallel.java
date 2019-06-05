package parallel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.io.File;

public class parallel {

	public static void main(String[] args) throws InterruptedException, ExecutionException {
		/*
		 * stap 1 data n4
		 * 
		 */

		String data = "resources/data/";
		/*
		 * uncomment for data n2 String data = "resources/data2/";
		 */

		/*
		 * uncomment for data n String data = "resources/data3/";
		 */

		/*
		 * uncomment for data n0.5 String data = "resources/data4/";
		 */

		/*
		 * stap 1 
		 */
		parallelInlezenNietSorteren(data);

		/*
		 * stap 2
		 * 	parallelInlezenEnSorteren(data);
		 */
	
		/*
		 * parallelInlezenEnSorterenV2(String data) 
		 * This is work in progress. 
		 */

	}

	public static void parallelInlezenNietSorteren(String data) throws InterruptedException, ExecutionException {

		int cores = Runtime.getRuntime().availableProcessors();
		System.out.println("number of cores available: " + cores);
		double startTime = System.nanoTime();
		ExecutorService pool = Executors.newFixedThreadPool(6);
		List<Callable<List<Player>>> taskList = new ArrayList<>();

		File[] files = new File(data).listFiles();
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
			player.setPotential();
		}

		Comparator<Player> compareByPotential = Comparator.comparing((Player p) -> p.Potential).reversed();
		Collections.sort(players, compareByPotential);

		for (int i = 0; i < 10; i++) {
			System.out.println("Rank: " + (i + 1) + ", Name " + players.get(i).Name + ", Nationality: "
					+ players.get(i).Nationality);
		}

		System.out.println(bestOfCountry(players, "France").size());

		double nano_endTime = System.nanoTime();
		double total = nano_endTime - startTime;
		System.out.println("Nano endtime: " + total);
	}

	@SuppressWarnings("unchecked")
	public static void parallelInlezenEnSorteren(String data) throws InterruptedException, ExecutionException {

		int cores = Runtime.getRuntime().availableProcessors();
		System.out.println("number of cores available: " + cores);
		double startTime = System.nanoTime();
		ExecutorService pool = Executors.newFixedThreadPool(cores);
		List<Callable<List<Player>>> taskList = new ArrayList<>();

		
		File[] files = new File(data).listFiles();
		for (File file : files) {
			// This calls a different class which also presorts the list.
			taskList.add(new CsvImporterAndSort(file.getAbsolutePath()));
		}

		List<Future<List<Player>>> futurePlayers = pool.invokeAll(taskList);
		List<Player> players = new ArrayList<>();

		for (Future<List<Player>> f : futurePlayers) {
			players.addAll(f.get());
		}
		
		pool.shutdown();

		Comparator<Player> compareByPotential = Comparator.comparing((Player p) -> p.Potential).reversed();
		Collections.sort(players, compareByPotential);

		for (int i = 0; i < 10; i++) {
			System.out.println("Rank: " + (i + 1) + ", Name " + players.get(i).Name + ", Nationality: "
					+ players.get(i).Nationality);
		}

		double nano_endTime = System.nanoTime();
		double total = nano_endTime - startTime;
		System.out.println("Nano endtime: " + total);
	}
	
	public static void parallelInlezenEnSorterenV2(String data) throws InterruptedException, ExecutionException {
		
		int cores = Runtime.getRuntime().availableProcessors();
		System.out.println("number of cores available: " + cores);
		double startTime = System.nanoTime();
		ExecutorService pool = Executors.newFixedThreadPool(cores);
		List<Callable<List<Player>>> taskList = new ArrayList<>();

		
		File[] files = new File(data).listFiles();
		for (File file : files) {
			taskList.add(new CsvImporterAndSort(file.getAbsolutePath()));
		}

		List<Future<List<Player>>> futurePlayers = pool.invokeAll(taskList);
		List<Player> playersSorted = new ArrayList<>();
		List<List<Player>> playerslist = new ArrayList<>();
		for (Future<List<Player>> f : futurePlayers) {
			playerslist.add(f.get());
		}
		

		List<Callable<List<Player>>> taskList2 = new ArrayList<>();
		int counter = playerslist.size();
		while (counter > 1) {
			futurePlayers.clear();
			for (int i = 0, j = 1; i < playerslist.size() - 1; i=i+2, j = i + 1) {

				taskList2.add(new Sort(playerslist.get(i), playerslist.get(j)));
			}
			futurePlayers = pool.invokeAll(taskList2);
			System.out.println(playerslist.size());
			playerslist.clear();
			System.out.println(playerslist.size() + "hoi");
			for (Future<List<Player>> f : futurePlayers) {
				playerslist.add(f.get());
				counter = playerslist.size();
			}

			
		}
		pool.shutdown();


//		for (int i = 0; i < 10; i++) {
//			System.out.println("Rank: " + (i + 1) + ", Name " + players.get(i).Name + ", Nationality: "
//					+ players.get(i).Nationality);
//		}

		double nano_endTime = System.nanoTime();
		double total = nano_endTime - startTime;
		System.out.println("Nano endtime: " + total);
	}

	public static List<Player> bestOfCountry(List<Player> players, String country) {
		List<Player> bestOfCountry = players.stream().parallel().filter(p -> country.equals(p.Nationality))
				.collect(Collectors.toList());
		return bestOfCountry;
	}

	public static List<Player> bestPerAge(List<Player> players, String age) {
		List<Player> bestOfCountry = players.stream().parallel().filter(p -> age.equals(p.Age))
				.collect(Collectors.toList());
		return bestOfCountry;
	}
}
