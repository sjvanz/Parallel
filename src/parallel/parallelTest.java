package parallel;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class parallelTest {

    private List<Player> testPlayers;

    @Before
    public void setUp() {

        Player messi = createPlayer("Messi", "31", "Argentina",
                "85", "90", "90", "95", "90");
        Player notMessi = createPlayer("NotMessi", "30", "Argentina",
                "50", "50", "50", "50", "50");
        Player ronaldo = createPlayer("Ronaldo", "33", "Portugal",
                "85", "90", "90", "95", "90");
        Player notRonaldo = createPlayer("Ronaldo", "33", "Portugal",
                "50", "50", "50", "50", "50");
        Player robben = createPlayer("Robben", "34", "Netherlands",
                "85", "91", "91", "98", "95");
        Player notRobben = createPlayer("Ronaldo", "33", "Netherlands",
                "50", "50", "50", "50", "50");
        Player ronaldinho = createPlayer("Ronaldinho", "33", "Brasil",
                "99", "99", "99", "99", "99");
        Player notRonaldinho = createPlayer("Ronaldinho", "33", "Brasil",
                "20", "25", "14", "25", "56");

        testPlayers = Arrays.asList(messi, notMessi, ronaldo, notRonaldo, robben, notRobben, ronaldinho, notRonaldinho);
    }

    @Test
    public void checkPotentialTest(){

        Comparator<Player> compareByNationality = Comparator.comparing(p -> p.getNationality());
        Comparator<Player> compareByPotential = compareByNationality.reversed().thenComparing((Player p) -> p.Potential).reversed();

        List<Player> inputList = testPlayers.stream()
                .filter(p -> p.getPotential() != 0)
                .sorted(compareByPotential)
                .collect(Collectors.toList());
        List<Player> filteredList = inputList.stream().filter(distinctByNationality(Player::getNationality)).collect(Collectors.toList());

        for (Player player : filteredList) {
            System.out.println("Country: " + player.Nationality + " Name: " + player.Name + " "
                    + "Potential: " + (player.Potential)/5);
        }

        Assert.assertEquals("Messi", filteredList.get(0).getName());
        Assert.assertEquals("Ronaldinho", filteredList.get(1).getName());
        Assert.assertEquals("Robben", filteredList.get(2).getName());
        Assert.assertEquals("Ronaldo", filteredList.get(3).getName());
    }

    private static <T> Predicate<T> distinctByNationality(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }

    private Player createPlayer(String name, String age, String nationality,
                                String weakFoot, String skillmoves, String vision,
                                String ballControl, String sprintSpeed) {
        Player player = new Player();
        player.setName(name);
        player.setAge(age);
        player.setNationality(nationality);
        player.setWeakFoot(weakFoot);
        player.setSkillMoves(skillmoves);
        player.setVision(vision);
        player.setBallControl(ballControl);
        player.setSprintSpeed(sprintSpeed);
        player.setPotential();

        return player;
    }
}
