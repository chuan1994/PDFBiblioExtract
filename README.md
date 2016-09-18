#PDFBiblioExtract

Requirements
==============
You will require the following conditions to run the program:
- JVM installed on the machine
- An internet connection
- CLI tool to execute the jar

For End Users
==============
The built standalone jar file is able to extract bibliography items from a specified PDF document or a specified list. You can perform this task by typing the following command into a CLI program:

    java -jar [name].jar [input files] [output folder]

where you replace
- name with the name of the standalone jar file
- input files with the paths to the list of files you wish to extract the bibliography from, each separated with a space. Such as "C:/users/file1.pdf C:/users/file2.pdf"
- output folder with the path to the directory you wish to store the output

It will store the output XML file as the name of the corresponding pdf.

An xsl style sheet has been provided, if the output xml file is opened in a browser it will disaply it in table format. This xsl style sheet may be modified to view more information

For Developers
--------------
