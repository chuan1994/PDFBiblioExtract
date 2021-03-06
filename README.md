#PDFBiblioExtract

##Requirements
You will require the following conditions to run the program:
- JVM installed on the machine
- An internet connection
- CLI tool to execute the jar

##For End Users
The built standalone jar file is able to extract bibliography items from a specified PDF document or a specified list. You can perform this task by typing the following command into a CLI program:

    java -jar [name].jar [input files] [output folder]

where you replace
- name with the name of the standalone jar file
- input files with the paths to the list of files you wish to extract the bibliography from, each separated with a space. Such as "C:/users/file1.pdf C:/users/file2.pdf"
- output folder with the path to the directory you wish to store the output

It will store the output XML file as the name of the corresponding pdf.

An xsl style sheet has been provided, if the output xml file is opened in a browser it will disaply it in table format. This xsl style sheet may be modified to view more information

##For Developers
This code functions by using the Apache PDFBox library.

It functions by identifying where the bilbiography section of the report is, identifying each reference item and sending it as a list to FreeCite.

The source code is explained below.

###Package - main
Responsible for starting and running extractor classes.

**Main class:**
- entry point to the program
- parses the parameters provided
- provides help messages if input parameters are incorrect
- creates a local version of the resources (the xsl stylesheet)
- starts and handles all of the tasks running in background

**BiblioExtractor class:**
- processes each pdf input
- runs through the pdf twice: firstly to identify location of bibliography and finally to parse the bibliography into a list of references
- sends data to freecite and receives reply in form of xml
- processes xml reply performing some modifications and prints into an output file

###Package - extractor
Responsible for all the extracting logic and any helper classes involved.

**BibliographyParser class:**
- Responisble for parsing the bibliopgraphy into individual reference items
- getBiblio() function retrieves the parsed bibliography in the form of an arraylist
- functions by identifying new lines which start at the x coordinate identified in BiblioPageFinder

**BiblioPageFinder class:**
- Responsible for retrieving the page the bibliography starts on
- Assumes end of document is end of bibliography
- Searches for bibliography header on page, to make it the start
- Also finds the x coordinate of the start of each reference item, required for parsing the bibliograpy 

**FontGroup class:**
- Simple class to store the font based information
- Stores - text, font, font size, page number

**FreeCiteConnection class:**
- Constructor requires an arraylist of strings. This is what is sent through the API call
- Connects to FreeCite web API using a POST request
- Response is collected as a string

