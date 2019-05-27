package parallel;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    //	System.out.println("reading file: "+ path);
    
    	
    	try {
    	//	Thread.sleep(500);
    	}
    	catch (Exception e) {
            e.printStackTrace();
          //  return " error in task2";
        }
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

    
  
        // HeaderColumnNameTranslateMappingStrategy 
        // for Student class 
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
  
        for (Player player : list) { 
        	player.setPotential();
        }
        
		Comparator<Player> compareByPotential = Comparator.comparing((Player p) -> p.Potential).reversed();
		Collections.sort(list, compareByPotential);

        return list;
    }



}


    
    


    

