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

public class parallel {

	public static void main(String[] args) throws InterruptedException, ExecutionException, IOException {
		String data = "data.csv";
		
		
		
		//nietParallel(data);
		parallel(data);
	}

	public static void parallel(String data) {
		List<String> landen = new ArrayList<String>();
		double startTime = System.nanoTime();
		List<Player> inputList = new ArrayList<Player>();
		
		try {
			File inputF =
					new File(data);
			InputStream inputFS = new FileInputStream(inputF);
			BufferedReader br = new BufferedReader(new InputStreamReader(inputFS));
			// reading from one source instead of multiple
			Runtime runtime = Runtime.getRuntime();
			System.out.println("Number of available processors: " + runtime.availableProcessors());
			

			Comparator<Player> compareByPotential2 = new Comparator<Player>() {
				@Override
				public int compare(Player o1, Player o2) {
	                return o1.getNationality()
	                        .compareTo(o2.getNationality());
				};
			};
			
			Comparator<Player> compareByPotential = //Comparator.comparing((Player p) -> p.Potential).reversed();		
					compareByPotential2.reversed().thenComparing((Player p) -> p.Potential).reversed();
			
			inputList = br.lines().parallel().skip(1)
					.map(mapToItem)
					.filter(c -> c.getNationality() != null || c.getNationality() != "")
					.sorted(compareByPotential)
					.collect(Collectors.toList());

			br.close();
		} catch (IOException e) {

		}

		for (int i = 0; i < inputList.size(); i++) {
			System.out.println("Country: " + inputList.get(i).Nationality + " Name: " + inputList.get(i).Name + " "
					+ "Potential: " + inputList.get(i).Potential);
		}

		double nano_endTime = System.nanoTime();
		double total = nano_endTime - startTime;
		System.out.println("Parallel endtime: " + total);
	}

	public static void nietParallel(String data) {
		
		double startTime = System.nanoTime();
		List<Player> inputList = new ArrayList<Player>();
		
		try {
			File inputF = new File(data);
			InputStream inputFS = new FileInputStream(inputF);
			BufferedReader br = new BufferedReader(new InputStreamReader(inputFS));
			// reading from one source instead of multiple
			Runtime runtime = Runtime.getRuntime();
			System.out.println("Number of available processors: " + runtime.availableProcessors());
			
			Comparator<Player> compareByPotential2 = new Comparator<Player>() {
				@Override
				public int compare(Player o1, Player o2) {
	                return ((Player)o1).getNationality()
	                        .compareTo(((Player)o2).getNationality());
				};
			};
			
			Comparator<Player> compareByPotential = //Comparator.comparing((Player p) -> p.Potential).reversed();		
					compareByPotential2.thenComparing((Player p) -> p.Potential).reversed();

			inputList = br.lines().skip(1)
					.map(mapToItem)
					//.filter(c -> c.getNationality() != null || c.getNationality() != "")
					.sorted(compareByPotential)
					.collect(Collectors.toList());

			br.close();
		} catch (IOException e) {

		}

		for (int i = 0; i < 10; i++) {
			System.out.println("Country: " + inputList.get(i).Nationality + " Name: " + inputList.get(i).Name + " "
					+ "Potential: " + inputList.get(i).Potential);
		}

		double nano_endTime = System.nanoTime();
		double total = nano_endTime - startTime;
		System.out.println("Non parallel endtime: " + total);
	}
	
	static Function<String, Player> mapToItem = (line) -> {

		String[] p = line.split(",");// a CSV has comma separated lines
		Player player = new Player();
		// simple mapper but very effective
		if (p.length > 77) {
			player.setName(p[2].trim());
			player.setAge(p[3].trim());
			player.setNationality(p[5].trim());
			player.setWeakFoot(p[16].trim());
			player.setSkillMoves(p[17].trim());
			player.setVision(p[77].trim());
			player.setBallControl(p[63].trim());
			player.setSprintSpeed(p[66].trim());
		}
		else {
			player.setName("");
			player.setAge("");
			player.setNationality("");
			player.setWeakFoot("");
			player.setSkillMoves("");
			player.setVision("");
			player.setBallControl("");
			player.setSprintSpeed("");
		}
		player.setPotential();
		return player;
	};

}
