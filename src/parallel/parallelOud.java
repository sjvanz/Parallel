package parallel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
public class parallelOud {

	public static void main(String[] args) throws InterruptedException, ExecutionException, IOException {


		String data = "data.csv";
		/*
		 * uncomment for data n2 String data = "resources/data2/";
		 */
	//	String data = "resources/data2/";
		/*
		 * uncomment for data n String data = "resources/data3/";
		 */

		/*
		 * uncomment for data n0.5 String data = "resources/data4/";
		 */

		/*
		 * stap 1 
		 */
		//parallelInlezenNietSorteren(data);

		/*
		 * stap 2
		 * 	parallelInlezenEnSorteren(data);
		 */
		parallelInlezenEnSorterenV2(data);
		
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

		List<Future<List<Player>>> futurePlayers =  pool.invokeAll(taskList);
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
	

	
	public static void parallelInlezenEnSorterenV2(String data) {

		double startTime = System.nanoTime();
	    List<Player> inputList = new ArrayList<Player>();
	    try{
	      File inputF = new File(data);
	      InputStream inputFS = new FileInputStream(inputF);
	      BufferedReader br = new BufferedReader(new InputStreamReader(inputFS));
	      // reading from one source instead of multiple
	      Runtime runtime = Runtime.getRuntime();
	      System.out.println(runtime.availableProcessors());
			Comparator<Player> compareByPotential = Comparator.comparing((Player p) -> p.Potential).reversed();
	      inputList = br.lines().parallel().skip(1)
	    		  .map(mapToItem)
	    		  .sorted(compareByPotential)
	    		  .collect(Collectors.toList());
	
	      br.close();
	    } catch (IOException e) {
	    
	    }
	    for(int i = 0; i < 10; i++) {
	    	System.out.println("Name: "+ inputList.get(i).Name + " " + "Potential: "+ inputList.get(i).Potential);
	    }
//		for (Player f : inputList) {
//			System.out.println("Name: "+ f.Name + " " + "Potential: "+ f.Potential);
//		}  
		double nano_endTime = System.nanoTime();
		double total = nano_endTime - startTime;
		System.out.println("Nano endtime: " + total);
	}

	   static Function<String, Player> mapToItem = (line) -> {
		   
	    	  String[] p = line.split(",");// a CSV has comma separated lines
	    	  Player player = new Player();
	    	// simple mapper but very effective
	    	 if(p.length > 77) {
	    		  player.setName(p[2].trim());
	    		  player.setAge(p[3].trim());   
	    		  player.setNationality(p[5].trim());
	    		  player.setWeakFoot(p[16].trim());
	    		  player.setSkillMoves(p[17].trim());
	    		  player.setVision(p[77].trim());
	      		  player.setBallControl(p[63].trim());
	      		  player.setSprintSpeed(p[66].trim());
	    	 }
	    	 	
	      		player.setPotential();
	    	  return player;
	    	};

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
