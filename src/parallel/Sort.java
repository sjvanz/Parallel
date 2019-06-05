package parallel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class Sort implements Callable<List<Player>> {

	List<Player> players;
	List<Player> reducedPLayer;

	public Sort(List<Player> f1, List<Player> f2) {
		this.players = new ArrayList<>();
		this.players.addAll(f1);
		this.players.addAll(f2);
	}

	public List<Player> call() {
		Comparator<Player> compareByPotential = Comparator.comparing((Player p) -> p.Potential).reversed();
		players.sort(compareByPotential);
		for (int i = 0; i < 10; i++) {
			reducedPLayer.add(players.get(i));
			System.out.println(players.get(i).Name);
		}
		return reducedPLayer;
	}

	public static List<Player> bestOfCountryWhen20(List<Player> players, String age) {
		List<Player> bestOfCountry = players.parallelStream().filter(p -> age.equals(p.Age))
				.collect(Collectors.toList());
		return bestOfCountry;
	}

	public static List<Player> bestOfCountry(List<Player> players, String country) {
		List<Player> bestOfCountry = players.parallelStream().filter(p -> country.equals(p.Nationality))
				.collect(Collectors.toList());
		return bestOfCountry;
	}

	public static List<Player> bestOfCountry2(List<Player> players) {
		String[] countries = new String[4];
		countries[0] = "Netherlands";
		countries[1] = "Germany";
		countries[2] = "Spain";
		countries[3] = "France";

		List<Player> specificCountryList = new ArrayList<>();

		for (Player player2 : players) {

			System.out.println(player2.getName());

			for (int i = 0; i < countries.length; i++) {
				if (player2.getNationality().equals(countries[i])) {
					specificCountryList.add(player2);
				}
			}

		}
		return specificCountryList;
	}
}
