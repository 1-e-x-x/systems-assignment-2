# systems-assignment-2

Authors:
- Alexander Sawatzky
- Ethan Kelly

Project:
- CSCI2020U (Software Systems Development and Integration) Assignment 2
- File IO and data processing
  
## Sections

*Note:* each image displayed is also available in the **>docs_&\_submission** directory

<img align="right" src="https://github.com/1-e-x-x/systems-assignment-2/blob/main/docs_%26_submission/converted.PNG" height="200">

### Part 1:

Task: read a .csv file, add a column, and output to a new .xml file

This functionality is achieved through the **input()** function, which takes the filepath of a .csv file and reads it into a 2-dimensional array of strings.
**processData()** can then be used on the return of **input()** to add the new column to the data.
Finally, the **output()** function writes a 2-dimensional array of strings to a .xml file, assuming that the first row of data is column titles.

### Part 2:

<img align="right" src="https://github.com/1-e-x-x/systems-assignment-2/blob/main/docs_%26_submission/summary.PNG" height="200">

Task: summarize the data of the previous .csv file and output the summary as a new .xml file

This is done through the use of the **summarize()** function, which takes the same format of data as the previous section and loops through each row in the table to total the data. The function then formats the summary into a new .xml file, once again assuming the first row of data is column titles.

This section also makes use of a ColumnData class which stores and updates the summary of data for a column in the table. Using this structure greatly reduces the amount of work to loop through the entire table.

### Part 3:
Task: use JavaFX to display data from the table as a bar chart

The **start()** function of the program starts the JavaFX platform. Through this function, we have created, populated, and displayed the requested bar chart in a manner to fit the chart to the size of the window.

<img src="https://github.com/1-e-x-x/systems-assignment-2/blob/main/docs_%26_submission/window.PNG">
