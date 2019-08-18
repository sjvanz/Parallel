package parallel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
import java.util.function.Predicate;
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
		// nietParallel(data);
		parallel(data);
	}

	public static void parallel(String data) {
		List<Player> filteredByNationalityList = new ArrayList<Player>();
		List<Player> filteredByAgeList = new ArrayList<Player>();
		List<Player> BestValueFromTenProcentLists = new ArrayList<Player>();
		double startTime = System.nanoTime();
		//Map<String, List<Player>> inputList = (Map<String, List<Player>>) new ArrayList<Player>();
		
		try {
			File inputF =
					new File(data);
			InputStream inputFS = new FileInputStream(inputF);
			BufferedReader br = new BufferedReader(new InputStreamReader(inputFS));
			// reading from one source instead of multiple
			Runtime runtime = Runtime.getRuntime();
			System.out.println("Number of available processors: " + runtime.availableProcessors());
			

			Comparator<Player> compareByNationality = new Comparator<Player>() {
				@Override
				public int compare(Player o1, Player o2) {
	                return o1.getNationality()
	                        .compareTo(o2.getNationality());
				};
			};
			
			Comparator<Player> compareByNationalityThenPotential = compareByNationality.reversed().thenComparing((Player p) -> p.Potential).reversed();
			Comparator<Player> compareByAgeThenPotential = Comparator
					  .comparing(Player::getAge).thenComparing((Player p) -> p.Potential).reversed();
			
			Comparator<Player> compareByPotential = Comparator.comparing(Player::getPotential).reversed();
			
			//Parallel mapping and double sort(first Nationality, if Nationality is the same, look at Potential
			List<Player> inputList = br.lines().parallel().skip(1)
					.map(mapToItem)
					.filter(c -> c.getPotential() != 0)
					.sorted(compareByNationalityThenPotential)	
					.collect(Collectors.toList());
			
			br.close();
 	    
			//Non-parallel filter only keep the first entry of every Nation,
			//if you would make it parallel the distinct function could pick up lower potential player in the "seen" hashmap
			filteredByNationalityList = inputList.stream()
					.filter(distinctByProperty(Player::getNationality))
					.collect(Collectors.toList());
			
			//Re-sort to compare to age then potential 
			filteredByAgeList = inputList.parallelStream().sorted(compareByAgeThenPotential).collect(Collectors.toList());
			
			//Non-parallel filter only keep the first entry of every player by age,
			filteredByAgeList = filteredByAgeList.stream().filter(distinctByProperty(Player::getAge)).collect(Collectors.toList());
		
			//Group by Nationality after sorting creating a List with List of players
		    Map<String, List<Player>> groups = 
		    		inputList.parallelStream().sorted(compareByPotential).collect(Collectors.groupingBy(Player::getNationality));
		    	    List<List<Player>> subSets = new ArrayList<List<Player>>(groups.values());
		    	  
		    	    // first limit to the first 10% of a certain list (it is sorted by potential so it gives the best 10% of players fo every 
		    	    subSets.parallelStream()
		    	    		.forEach(l -> l.parallelStream()
		    	    				.limit((long)(l.size() / 10))
		    	    				.reduce((x,y) -> (x.getValue() < y.getValue() ? x : y ))
		    	    				.map(mapper)
	
		    	    				
		    	    		
		} catch (IOException e) {

		}
		//BestValueFromTenProcentLists.add
//		for (int i = 0; i < filteredByNationalityList.size(); i++) {
//			System.out.println("Country: " + filteredByNationalityList.get(i).Nationality + " Name: " + filteredByNationalityList.get(i).Name + " "
//					+ "Potential: " + filteredByNationalityList.get(i).getPotential());
//		}
//		System.out.println("\n");
//		for (int i = 0; i < filteredByAgeList.size(); i++) {
//			System.out.println("Age: " + filteredByAgeList.get(i).Age + " Name: " + filteredByAgeList.get(i).Name + " "
//					+ "Potential: " + filteredByAgeList.get(i).getPotential());
//		}
		
		for (int i = 0; i < BestValueFromTenProcentLists.size(); i++) {
		System.out.println("Age: " + BestValueFromTenProcentLists.get(i).Age + " Name: " + BestValueFromTenProcentLists.get(i).Name + " "
				+ "Potential: " + BestValueFromTenProcentLists.get(i).getPotential());
	}


		double nano_endTime = System.nanoTime();
		double total = nano_endTime - startTime;
		System.out.println("Parallel endtime: " + total);
	}

	public static <T> Predicate<T> distinctByProperty(Function<? super T, ?> keyExtractor) {
		Set<Object> seen = ConcurrentHashMap.newKeySet();
		return t -> seen.add(keyExtractor.apply(t));
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
			player.setValue(splitString(p[11].trim()));
		} else {
			player.setName("");
			player.setAge("");
			player.setNationality("");
			player.setWeakFoot("");
			player.setSkillMoves("");
			player.setVision("");
			player.setBallControl("");
			player.setSprintSpeed("");
			player.setValue(0);
		}
		
		

		player.setPotential();
		return player;
	};
	
	  public static int splitString(String str)  { 
		  //remove euro sign
		  String substr = str.substring(3);

		  //check if there is a dot (only used to for halves for example 10.5M)
		  Boolean hasDot = substr.contains(".");
	        StringBuffer alpha = new StringBuffer(),  
	        num = new StringBuffer(), special = new StringBuffer(); 
	          
	        for (int i=0; i<substr.length(); i++) 
	        { 
	            if (Character.isDigit(substr.charAt(i))) 
	                num.append(substr.charAt(i)); 
	            else if(Character.isAlphabetic(substr.charAt(i))) 
	                alpha.append(substr.charAt(i)); 
	            else
	                special.append(substr.charAt(i)); 
	        } 
	        String numString = num.toString();
	        String alphaString = alpha.toString();
	        int value = Integer.valueOf(numString);
	        if (alphaString.equals("K")) {
	        	value = value * 1000;
	        }
	        if (alphaString.equals("M")) {
	        	value = value * 1000000;
	        }
	        if (hasDot) {
	        	value = value / 10;
	        }

	        return value;
	    }

}