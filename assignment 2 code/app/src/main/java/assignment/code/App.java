package assignment.code;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class App {

    public static ArrayList<String> csvReader(File csvFile){
       //csvReader takes a csv text file as an input and returns an array
       //row 0 of the array is the csv description.
       //use string.split() command to split rows into items.
       
       try{
           Scanner scanner = new Scanner(csvFile);
           scanner.useDelimiter("\n");
           ArrayList<String> toReturn = new ArrayList<String>();
           while(scanner.hasNext()){
               toReturn.add(scanner.next());
           }
           return toReturn;
    }
       catch (Exception error){
           error.printStackTrace();
           ArrayList<String> toReturn = null;
           return toReturn;
       }
    }

    public static void main(String[] args) {
        ArrayList<String> testus = csvReader(new File("C:\\Users\\Alex\\Desktop\\systems assignment 2\\airline_safety.csv"));
        System.out.println(testus);
    }
}
