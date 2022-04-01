import javafx.scene.chart.*;
import javafx.scene.layout.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

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
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.stream.Collectors;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
    //takes a csv filepath and converts to a 2d arraylist of strings
    public static ArrayList<ArrayList<String>> input(String filename){
        try (Scanner reader = new Scanner(new File(filename))){
            ArrayList<ArrayList<String>> output = new ArrayList<>();
            reader.useDelimiter("\n");
            while (reader.hasNext()) { //split by line
                output.add(new ArrayList<>());
                for(String x : reader.next().split(",")) output.get(output.size()-1).add(x); //split each element in the line

                if (reader.hasNext()){ //removes the newline/carriage-return delimiter
                    ArrayList<String> temp = output.get(output.size()-1);
                    temp.set(temp.size()-1, temp.get(temp.size()-1).substring(0, temp.get(temp.size()-1).length()-1));
                }
            }
            return output;
        } catch (IOException e){
            e.printStackTrace();
            System.out.println("\nIO Exception thrown. Maybe the file doesn't exist?");
            return null;
        }
    }

    //adds a column representing total number of incidents to the arraylist of flight data
    public static ArrayList<ArrayList<String>> processData(ArrayList<ArrayList<String>> data, String filename) throws IOException {
        if (data.get(0).get(data.get(0).size()-1) == "total_incidents") return data;
        boolean first = true;
        for (ArrayList<String> x : data) {
            if (first == true) {
                x.add("total_incidents");
                first = false;
            }
            else x.add(Integer.toString(Integer.valueOf(x.get(2)) + Integer.valueOf(x.get(5))));
        }

        //write new data to csv
        FileWriter output = new FileWriter(filename);
        for (ArrayList<String> x : data) output.write(x.stream().collect(Collectors.joining(",")) + "\n");
        output.close();
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

    public void start(Stage primaryStage) throws IOException, ParserConfigurationException {
        //for testing purposes and because you shouldn't overwrite the original data file, I've set it to write to a second file instead of the original
        ArrayList<ArrayList<String>> data = processData(input("src\\main\\resources\\airline_safety.csv"), "src\\main\\resources\\airline_safety2.csv");
        output(data);
        summarize(data);

        //JavaFX App (part 3)
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Airline");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Fatal Incidents");

        BarChart barChart = new BarChart(xAxis, yAxis);
        XYChart.Series series1 = new XYChart.Series();
        series1.setName("1985-1999");
        XYChart.Series series2 = new XYChart.Series();
        series2.setName("2000-2014");

        boolean first = true;
        for (ArrayList<String> x : data) {
            if (first == true) first = false;
            else {
                series1.getData().add(new XYChart.Data(x.get(0), Integer.valueOf(x.get(3))));
                series2.getData().add(new XYChart.Data(x.get(0), Integer.valueOf(x.get(6))));
            }
        }

        barChart.getData().addAll(series1, series2);

        primaryStage.setTitle("CSCI2020U Assignment 2: E. Kelly & A. Sawatzky");
        primaryStage.setScene(new Scene(new AnchorPane(barChart), 1200, 800));
        AnchorPane.setBottomAnchor(barChart, 10.0);
        AnchorPane.setTopAnchor(barChart, 10.0);
        AnchorPane.setLeftAnchor(barChart, 10.0);
        AnchorPane.setRightAnchor(barChart, 10.0);
        primaryStage.show();
    }

    public static void main(String[] args) {Application.launch(args);}
}