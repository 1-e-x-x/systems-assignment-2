package assignment.code;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class App {

    public static ArrayList<String> csvReader(File csvFile){
       //csvReader takes a csv text file as an input and returns an array
       //row 0 of the array is the csv description.
       //for example, csvReader(airline_safety.csv).get(0) returns "airline,avail_seat_km_per_week,incidents_85_99,fatal_accidents_85_99,fatalities_85_99,incidents_00_14,fatal_accidents_00_14,fatalities_00_14"
       
       try{
           Scanner scanner = new Scanner(csvFile);
           scanner.useDelimiter("\n");
           ArrayList<String> toReturn = new ArrayList<String>();
           while(scanner.hasNext()){
               toReturn.add(scanner.next());
           }
           scanner.close();
           return toReturn;
    }
       catch (Exception error){
           error.printStackTrace();
           ArrayList<String> toReturn = null;
           return toReturn;
       }
    }

    public static String[][] convert2D(ArrayList<String> toConvert){
        //convert2D takes the ArrayList from csvReader and turns it into a 2D array
        //it uses the string.split() command to split rows into items.
        //for example:
        //  ArrayList<String> test = csvReader(airline_safety.csv)
        //  String[][] convertTest = convert2D(test)
        //  converTest[0][0] returns "airline"

        int numColumns = toConvert.get(0).split(",").length;
        int numRows = toConvert.size();
        String[][] toReturn = new String[numRows][numColumns];
        for(int rows = 0; rows < numRows; rows++){
            String[] temp = toConvert.get(rows).split(",");
            for(int cols = 0; cols < numColumns; cols++){
                toReturn[rows][cols] = temp[cols];
            }
        }
        return toReturn;
    }
    public static void main(String[] args) {
        ArrayList<String> testus = csvReader(new File("src\\main\\resources\\airline_safety.csv"));
        String[][] testus2 = convert2D(testus);
        System.out.println(testus2[testus.size()-1][5]);
    }
}