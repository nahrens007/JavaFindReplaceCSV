import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public class FindReplace {
    public static void usage() {
        System.out.println( "\nUsage:" );
        System.out.println( "java FindReplace [options (see required * below)]" );
        System.out.println( "\t*-i <input file>\t\tFile to parse.");
        System.out.println( "\t*-m <mapping file>\t\tCSV file to use as mapping.");
        System.out.println( "\t\t\t\t\tFormat of: MAPFROM<delimiter character>MAPTO");
        System.out.println( "\t\t\t\t\tExample (assume delimiter character is a comma):");
        System.out.println( "\t\t\t\t\t\tOldString,NewString");
        System.out.println( "\t-o <output file>\t\tFile output results. Defualt is console.");
        System.out.println( "\t-d <\\t | \\n | \",\" | etc...>\tCharacter(s) to use as a delimter in the CSV file. Default is comma.");
        System.out.println( "\t\t\t\t\t\\t and \\n are valid delimiters." );
        System.out.println( "\t-c <T | F>\t\t\tTrue = Case specific. False = ignore case (default).");
        
        System.out.println();
        System.exit(0);
    }
    
    public static void main(String[] args) 
    {
        boolean caseSpecific=false; // default case is ignore/false
        String inFile = "";
        String mapFile = "";
        String outFile = "";
        String delimiter=",";

        for (int i = 0; i < args.length; i++) {
            switch (args[i].toLowerCase()) {
                case "-i":
                    // input file
                    // ensure there is a following argument (input file path)
                    if (args.length-1==i) {
                        usage();
                    } else {
                        inFile = args[++i];
                    }
                    break;
                case "-m":
                    // mapping file
                    // ensure there is a following argument (mapping file path)
                    if (args.length-1==i) {
                        usage();
                    } else {
                        mapFile = args[++i];
                    }
                    break;
                case "-o":
                    // output file
                    // ensure there is a following argument (output file path)
                    if (args.length-1==i) {
                        usage();
                    } else { 
                        outFile=args[++i];
                    }
                    break;
                case "-d":
                    // delimiter character
                    // ensure there is a following argument (delimiter character(s))
                    if (args.length-1==i) {
                        usage();
                    } else {
                        delimiter = args[++i];
                    }
                    break;
                case "-c":
                    // case specification
                    // ensure there is a following argument (T or F to specify case)
                    if (args.length-1==i) {
                        usage();
                    } else if (args[i+1].toUpperCase().equals("T")) {
                        caseSpecific=true;
                    } else if (args[i+1].toUpperCase().equals("F")) { 
                        caseSpecific=false;
                    } else { 
                        usage();
                    }
                    i++;
                    break;
                default:
                    usage();
            }
        }
        if (inFile.isEmpty() || mapFile.isEmpty()) {
            usage();
        }
        
        try {
            BufferedReader csvReader = new BufferedReader(new FileReader(mapFile));
            String row;
            if (caseSpecific) {
                System.out.println("Case sensitive...");
            } else {
                System.out.println("Case insensitive...");
            }
            
            Path path = Paths.get(inFile);
            Charset charset = StandardCharsets.UTF_8;

            String content = new String(Files.readAllBytes(path), charset);
            String[] data;
            
            // Loop through CSV mappings
            while ((row = csvReader.readLine()) != null) {
                data = row.split(delimiter);
                System.out.println(data[0] + " : " + Integer.toString(content.split( "(?i)"+data[0], -1).length-1));
                
                if (caseSpecific) {
                    content = content.replaceAll(data[0], data[1]);
                } else {
                    content = content.replaceAll("(?i)"+data[0], data[1]);
                }
            }
            
            // Output results
            try {
                if (outFile.isEmpty()) {
                    // output to console
                    System.out.println(content);
                } else {
                    Files.write(Paths.get(outFile), content.getBytes(charset));
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
                
            csvReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        
    }
}