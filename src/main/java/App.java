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
                if (reader.hasNext()) output.get(output.size()-1).set(output.get(output.size()-1).size()-1, output.get(output.size()-1).get(output.get(output.size()-1).size()-1)
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

    //contains and updates data for each column
    static class ColumnData {
        public String name = "";
        public int col, min = -1, max = -1, avg = 0;
        public void compare(int temp) {
            if (min == -1 || temp < min) min = temp;
            if (max == -1 || temp > max) max = temp;
            avg += temp;
        }
        public ColumnData(int t_col) { col = t_col; };
    };

    //part 2 of the assignment, summarizes data then prints to XML
    public static void summarize(ArrayList<ArrayList<String>> data) throws ParserConfigurationException {
        //create list of columndata objects (one for each important column
        ColumnData[] summary = { new ColumnData(2), new ColumnData(3), new ColumnData(4), new ColumnData(5), new ColumnData(6), new ColumnData(7), new ColumnData(8) };
        for (ColumnData x : summary) x.name = data.get(0).get(x.col);

        boolean first = true; //loop through data to update column data
        for (ArrayList<String> x : data){
            if (first == true) first = false;
            else for (ColumnData y : summary) y.compare(Integer.valueOf(x.get(y.col)));
        }

        //create document
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        Element rootElement = doc.createElement("Summary");
        doc.appendChild(rootElement);

        { //add description element
            Element stat = doc.createElement("Stat");
            rootElement.appendChild(stat);

            Element name = doc.createElement("Name");
            name.setTextContent("column_name");
            stat.appendChild(name);

            Element min = doc.createElement("Min");
            min.setTextContent("min_val");
            stat.appendChild(min);

            Element max = doc.createElement("Max");
            max.setTextContent("max_val");
            stat.appendChild(max);

            Element avg = doc.createElement("Avg");
            avg.setTextContent("avg_val");
            stat.appendChild(avg);
        }
        for (ColumnData x : summary) { //fill in elements from columndata list
            Element stat = doc.createElement("Stat");
            rootElement.appendChild(stat);

            Element name = doc.createElement("Name");
            name.setTextContent(x.name);
            stat.appendChild(name);

            Element min = doc.createElement("Min");
            min.setTextContent(Integer.toString(x.min));
            stat.appendChild(min);

            Element max = doc.createElement("Max");
            max.setTextContent(Integer.toString(x.max));
            stat.appendChild(max);

            Element avg = doc.createElement("Avg");
            avg.setTextContent(Integer.toString(x.avg/(data.size()-1)));
            stat.appendChild(avg);
        }

        //write the file to the system
        try (FileOutputStream output = new FileOutputStream("src\\main\\resources\\airline_summary_statistics.xml")) {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(new DOMSource(doc), new StreamResult(output));
        } catch (IOException | TransformerException e) { e.printStackTrace(); }
    }

    public static void main(String[] args) throws ParserConfigurationException {
        ArrayList<ArrayList<String>> test = processData(input("src\\main\\resources\\airline_safety.csv"));
        //for (ArrayList<String> x : test) for (String y : x) System.out.println(y); //outputs entire struct line by line
        summarize(test);
        output(test);
    }
}