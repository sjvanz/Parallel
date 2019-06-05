package parallel;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;
import java.util.concurrent.Callable;

import com.opencsv.CSVReader;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.HeaderColumnNameTranslateMappingStrategy;


public class CsvImporter implements Callable<List<Player>> {

    String path;
    static int id;
    public CsvImporter(String s) {
        this.path = s;
    }

    public List<Player> call() {
        return mapCsvToPlayer(path);
    }

    public static List<Player> mapCsvToPlayer(String path) {

        System.out.println("reading file: "+ path);
        // Hashmap to map CSV data to
        // Bean attributes.
        Map<String, String> mapping = new
                HashMap<String, String>();
        mapping.put("Name", "Name");
        mapping.put("Age", "Age");
        mapping.put("BallControl", "BallControl");
        mapping.put("SprintSpeed", "SprintSpeed");
        mapping.put("Vision", "Vision");
        mapping.put("Skill Moves", "SkillMoves");
        mapping.put("Weak Foot", "WeakFoot");
        mapping.put("Nationality", "Nationality");
        mapping.put("ShotPower", "ShotPower");
        mapping.put("Finishing", "Finishing");

        // HeaderColumnNameTranslateMappingStrategy
        // for PLayer class
        HeaderColumnNameTranslateMappingStrategy<Player> strategy =  new HeaderColumnNameTranslateMappingStrategy<Player>();
        strategy.setType(Player.class);
        strategy.setColumnMapping(mapping);

        // Create castobaen and csvreader object
        CSVReader csvReader = null;
        try {
            csvReader = new CSVReader(new FileReader
                    (path));
        }
        catch (FileNotFoundException e) {

            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        CsvToBean<Player> csvToBean = new CsvToBean<Player>();

        // call the parse method of CsvToBean
        // pass strategy, csvReader to parse method
        @SuppressWarnings("deprecation")
        List<Player> list = csvToBean.parse(strategy, csvReader);

//         print details of Bean object
        
        return list;

    }


}








