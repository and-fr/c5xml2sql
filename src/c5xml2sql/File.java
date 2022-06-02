package c5xml2sql;

import static c5xml2sql.App.selectedDirPath;
import static c5xml2sql.App.setProgressIncrement;
import static c5xml2sql.App.xmlFiles;
import static c5xml2sql.App.xmlFilesNum;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
 
public class File {

    //Read file content into string with - Files.lines(Path path, Charset cs)
    public static String getContent (String filePath)
    {
        StringBuilder contentBuilder = new StringBuilder();
 
        try (Stream<String> stream = Files.lines( Paths.get(filePath), StandardCharsets.UTF_8))
        {
            stream.forEach(s -> contentBuilder.append(s).append("\n"));
        }
        catch (IOException e)
        {
            System.out.println(e);
        }
 
        return contentBuilder.toString();
    }
    

    
    public static void save(String content, String path)
    {
        java.io.File file = new java.io.File(path);

        try (FileWriter fileWriter = new FileWriter(file)) {
            Log.add("Saving output to sql file: " + path + "\n\n");
            fileWriter.write(content);
            Log.add("DONE");
        } catch (IOException e) {
            // System.out.println(e);
            Log.add(e.toString());
        }
    }    


    
    public static Boolean getXmlFiles(){
        xmlFiles.clear();
        xmlFilesNum = null;
        Boolean isScanned = false;
        
        Log.add("\nSearching for XML files in: " + App.selectedDirPath);

        try {
            Files.walk(Paths.get(selectedDirPath))
                .filter(p -> Files.isRegularFile(p))
                .filter(p -> p.getFileName().toString().endsWith(".xml"))
                .forEach(filePath -> {
                    xmlFiles.add(filePath.toString());
                });
        } catch (IOException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
            // System.out.println("ERR!!!:" + ex);
        }

        xmlFilesNum = xmlFiles.size();
        if(xmlFilesNum > 0){
            setProgressIncrement();
            Log.add("\n\nXML files found: " + xmlFilesNum);
            isScanned = true;
        } else {
            Log.add("\n\nNo XML files found.");
        }
        
        return isScanned;
    }
    
}
