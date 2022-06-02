# Sid Meier's Civilization V: XML to SQL code converter.
This is a tool to convert code from XML based files to SQL type files.

## REQUIREMENTS
This program has been written in Java 8 update 181.
You can download Java and install it from: https://java.com

## USAGE
After building the project with proper Java installed:
- start c5xml2sql.jar file like any .exe application,
- in main program's window set input directory with XML files from Configuration menu,
- then set output SQL file from Configuration menu, and chose File -> Convert.

## Additional notes
This program is a simple parser. If you have XML files with proper syntax and values then this utility should convert all to SQL files just fine. However if there are incorrect game values in XML files, this tool won't fix those at all, though such issues may be easier to recognize after conversion to SQL.

## Screenshots
![c5xml2sql](https://user-images.githubusercontent.com/80594221/171723872-ecda37c9-a6db-4dc1-a734-c27031321307.png)
