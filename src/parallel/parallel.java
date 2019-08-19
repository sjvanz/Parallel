package parallel;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class parallel {

    public static void main(String[] args) {
        String data = "data.csv";
        notParallel(data);
        readParallel(data);
    }

    private static void readParallel(String data) {
        List<Player> filteredByNationalityList = new ArrayList<Player>();
        List<Player> filteredByAgeList = new ArrayList<Player>();
        List<Player> BestValueFromTenProcentLists = new ArrayList<Player>();
        long startTime = System.nanoTime();

        try {
            File inputF =
                    new File(data);
            InputStream inputFS = new FileInputStream(inputF);
            BufferedReader br = new BufferedReader(new InputStreamReader(inputFS));
            // reading from one source instead of multiple
            Runtime runtime = Runtime.getRuntime();
            System.out.println("\nNumber of available processors: " + runtime.availableProcessors());

            // creating Comparators
            Comparator<Player> compareByNationality = Comparator.comparing(o -> o.getNationality());
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
            subSets
                    .forEach(l -> l.parallelStream()
                            .limit((l.size() / 10))
                            .reduce((x, y) -> (x.getValue() < y.getValue() ? x : y))
                            .ifPresent(BestValueFromTenProcentLists::add));

        } catch (IOException e) {
            e.printStackTrace();
        }


        System.out.println("\nParallel filtered by Nationality\n");
        for (Player player : filteredByNationalityList) {
            System.out.println("Country: " + player.Nationality + " Name: " + player.Name + " "
                    + "Potential: " + player.getPotential());
        }

        System.out.println("\nParallel filtered by age\n");
        for (Player player : filteredByAgeList) {
            System.out.println("Age: " + player.Age + " Name: " + player.Name + " "
                    + "Potential: " + player.getPotential());
        }

        System.out.println("\nParallel filtered by best 10% of their nation\n");
        for (Player bestValueFromTenProcentList : BestValueFromTenProcentLists) {
            System.out.println("Country: " + bestValueFromTenProcentList.Nationality + " Name: " + bestValueFromTenProcentList.Name
                    + " Potential: " + bestValueFromTenProcentList.getPotential() + " Value: " + bestValueFromTenProcentList.Value);
        }


        long endTime = System.nanoTime();
        long elapsedTimeInMillis = TimeUnit.MILLISECONDS.convert((endTime - startTime), TimeUnit.NANOSECONDS);
        System.out.println("Total elapsed time: " + elapsedTimeInMillis + " ms");
    }

    public static void notParallel(String data) {
        List<Player> filteredByNationalityList = new ArrayList<Player>();
        List<Player> filteredByAgeList = new ArrayList<Player>();
        List<Player> BestValueFromTenProcentLists = new ArrayList<Player>();
        long startTime = System.nanoTime();

        try {
            File inputF =
                    new File(data);
            InputStream inputFS = new FileInputStream(inputF);
            BufferedReader br = new BufferedReader(new InputStreamReader(inputFS));
            // reading from one source instead of multiple
            Runtime runtime = Runtime.getRuntime();
            System.out.println("\nNumber of available processors: " + runtime.availableProcessors());


            Comparator<Player> compareByNationality = Comparator.comparing(o -> o.getNationality());

            Comparator<Player> compareByNationalityThenPotential = compareByNationality.reversed().thenComparing((Player p) -> p.Potential).reversed();
            Comparator<Player> compareByAgeThenPotential = Comparator
                    .comparing(Player::getAge).thenComparing((Player p) -> p.Potential).reversed();

            Comparator<Player> compareByPotential = Comparator.comparing(Player::getPotential).reversed();

            //Parallel mapping and double sort(first Nationality, if Nationality is the same, look at Potential
            List<Player> inputList = br.lines().skip(1)
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
            filteredByAgeList = inputList.stream().sorted(compareByAgeThenPotential).collect(Collectors.toList());

            //Non-parallel filter only keep the first entry of every player by age,
            filteredByAgeList = filteredByAgeList.stream().filter(distinctByProperty(Player::getAge)).collect(Collectors.toList());


            //Group by Nationality after sorting creating a List with List of players
            Map<String, List<Player>> groups =
                    inputList.parallelStream().sorted(compareByPotential).collect(Collectors.groupingBy(Player::getNationality));
            List<List<Player>> subSets = new ArrayList<List<Player>>(groups.values());

            // first limit to the first 10% of a certain list (it is sorted by potential so it gives the best 10% of players fo every
            subSets.stream()
                    .forEach(l -> l.stream()
                            .limit((l.size() / 10))
                            .reduce((x, y) -> (x.getValue() < y.getValue() ? x : y))
                            .ifPresent(BestValueFromTenProcentLists::add));


        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("\n'not parallel' filtered by Nationality\n");
        for (Player player : filteredByNationalityList) {
            System.out.println("Country: " + player.Nationality + " Name: " + player.Name
                    + " Potential: " + player.getPotential());
        }

        System.out.println("\n'not parallel' filtered by age\n");
        for (Player player : filteredByAgeList) {
            System.out.println("Age: " + player.Age + " Name: " + player.Name
                    + " Potential: " + player.getPotential());
        }

        System.out.println("\n'not parallel' filtered by the best 10% of their nation\n");
        for (Player bestValueFromTenProcentList : BestValueFromTenProcentLists) {
            System.out.println("Country: " + bestValueFromTenProcentList.Nationality + " Name: " + bestValueFromTenProcentList.Name
                    + " Potential:" + bestValueFromTenProcentList.getPotential() + " Value: " + bestValueFromTenProcentList.Value);
        }

        long endTime = System.nanoTime();
        long elapsedTimeInMillis = TimeUnit.MILLISECONDS.convert((endTime - startTime), TimeUnit.NANOSECONDS);
        System.out.println("Total elapsed time: " + elapsedTimeInMillis + " ms");
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

    static int splitString(String str) {

        if (!str.equals("")) {
            //remove euro sign
            String substr = str.substring(1);

            //check if there is a dot (only used to for halves for example 10.5M)
            boolean hasDot = substr.contains(".");
            StringBuilder alpha = new StringBuilder(),
                    num = new StringBuilder();

            for (int i = 0; i < substr.length(); i++) {
                if (Character.isDigit(substr.charAt(i)))
                    num.append(substr.charAt(i));
                else if (Character.isAlphabetic(substr.charAt(i)))
                    alpha.append(substr.charAt(i));
            }
            String numString = num.toString();
            String alphaString = alpha.toString();
            int value = Integer.parseInt(numString);
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
        } else {
            return 0;
        }

    }

}