import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Scanner;

public class App {

    //takes a csv filepath and converts to a 2d arraylist of strings
    public static ArrayList<ArrayList<String>> input(String filename){
        try (Scanner reader = new Scanner(new File(filename))){
            ArrayList<ArrayList<String>> output = new ArrayList<>();
            reader.useDelimiter("\n");
            while (reader.hasNext()) {
                output.add(new ArrayList<>());
                for(String x : reader.next().split(",")) output.get(output.size()-1).add(x);
                output.get(output.size()-1).set(output.get(output.size()-1).size()-1, output.get(output.size()-1).get(output.get(output.size()-1).size()-1)
                        .substring(0, output.get(output.size()-1).get(output.get(output.size()-1).size()-1).length()-1)); //lol fix this later
            }
            return output;
        } catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }

    //adds a column representing total number of incidents to the arraylist of flight data
    public static ArrayList<ArrayList<String>> processData(ArrayList<ArrayList<String>> data){
        boolean first = true;
        for (ArrayList<String> x : data) {
            if (first == true) {
                x.add("total_incidents");
                first = false;
            }
            else x.add(Integer.toString(Integer.valueOf(x.get(2)) + Integer.valueOf(x.get(5))));
        }
        return data;
    }

    //writes a 2d arraylist to an xml file, assuming the first line is column descriptions
    public static void output(ArrayList<ArrayList<String>> data) throws ParserConfigurationException {
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        Element rootElement = doc.createElement("Flight_Data");
        doc.appendChild(rootElement);

        boolean first = true;
        for (ArrayList<String> x : data) {
            if (first == true) first = false;
            else {
                Element airline = doc.createElement("Airline");
                rootElement.appendChild(airline);
                airline.setAttribute("Name", x.get(0));

                for (int i = 1; i < x.size(); i++){
                    Element temp = doc.createElement(data.get(0).get(i));
                    temp.setTextContent(x.get(i));
                    airline.appendChild(temp);
                }
            }
        }

        // write dom document to a file
        try (FileOutputStream output = new FileOutputStream("src\\main\\resources\\converted_airline_safety.xml")) {
            //TransformerFactory.newInstance().newTransformer().transform(new DOMSource(doc), new StreamResult(output));
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(new DOMSource(doc), new StreamResult(output));
        } catch (IOException | TransformerException e) {
            e.printStackTrace();
        }
    }

    public static void summarize(ArrayList<ArrayList<String>> data){
        int min1 = -1, max1 = -1, avg1 = 0, min2 = -1, max2 = -1, avg2 = 0;
        boolean first = true;
        int temp;
        for (ArrayList<String> x : data){
            if (first == true) first = false;
            else {
                temp = Integer.valueOf(x.get(1));
                if (min1 == -1 || temp < min1) min1 = temp);
                if (max1 == -1 || temp > max1) max1 = temp;
                avg1 += temp;
            }
        }
    }

    public static void main(String[] args) throws ParserConfigurationException {
        ArrayList<ArrayList<String>> test = processData(input("src\\main\\resources\\airline_safety.csv"));
        //for (ArrayList<String> x : test) for (String y : x) System.out.println(y); //outputs entire struct line by line

        output(test);
    }
}