/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.ingv;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 *
 * @author massimofares :: massimo.fares@ingv.it
 * 
 */
public class MainLine {
    
    private static Map<String, String> parametersMap ;
    private static final String version = "versione 0.2";
    private final List<PhsRecord> phs;
    private final String QmlString = null;
    private Options options;
    
    
    
    
    /*
    *
    * Construct new MainLine: run program via command line 
    *
    */
    public MainLine() {
        
        this.phs = null;
        
    }
        
    
    
    
    /*
    *
    * load parameters from command line
    *
    */
    public static void loadFromCommandLine(final String[] args) {
        Properties prop = new Properties();
        List<PhsRecord> phs;
        parametersMap = new HashMap<>();
        String QueryPrep = "query?";
        String OtherPrep = "";
        String PostPrep = "";
        String filename= "";
        String baseUrl, extended, eventVersion;
        eventVersion = "Preferred";
        String [] baseUrlArray;
        
        /*
        * restore the default value of standard output
        */
        //PrintStream standardOut = System.out;
        //PrintStream standardErr = System.err;
        
        FileInputStream fis ;
        try {
            fis = new FileInputStream("qml_to_phs_properties.xml");
            prop.loadFromXML( fis );
        } catch (IOException ex) {
            Logger.getLogger(Options.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        baseUrl = prop.getProperty("URI_WEBSERVICE_DEFAULT");
        baseUrlArray = baseUrl.split("\\\n");
        baseUrl = baseUrlArray[0].trim();
        extended = prop.getProperty("phs_extended");
        System.out.println("\n **** :: Client Events WS :: qml_to_phs "+version+" :: ***"+"\n\n");
        System.out.println(" url: "+baseUrl+" extend:"+extended+"\n");
        
        for (int i = 0 ; i < args.length ; i = i+2) {
            if(args[i].contains("-")) { 
                parametersMap.put(args[i], args[i+1]);
            } else {
                System.out.println(" Errore nei parametri: k="+args[i]+" v="+ args[i+1]);
                System.exit(0);
            }
        }
       
//        //** Print 4 Check Params
//        for (String key : parametersMap.keySet()) {
//            System.out.println(" MAP parametri: k="+key+" v="+ parametersMap.get(key));
//        }
        
        Date now= new Date();
        filename = "qml2phs_at_"+now+".phs".replace(" ","");
        for (String key: parametersMap.keySet()){
            if (key.contains("out")) filename = parametersMap.get(key);
            if (key.equalsIgnoreCase("-eventid")) {
                QueryPrep = QueryPrep+"eventid="+parametersMap.get(key);
                System.out.println(" parameter special:=  "+"query?eventid="+parametersMap.get(key));
                PostPrep = PostPrep+"&includearrivals=true"; // needed for PHS calculation
                
            }
            else {
                // ** Parameters inlineretrieve  **
                /*
                    QueryPrep
                */
                if (key.contains("start")) { QueryPrep = QueryPrep+"starttime="+parametersMap.get(key)+"&";  }
                if (key.contains("end")) {QueryPrep = QueryPrep+"endtime="+parametersMap.get(key)+"&"; }
                if (key.contains("mindepth")) {QueryPrep = QueryPrep+"mindepth="+parametersMap.get(key)+"&"; }
                if (key.contains("maxdepth")) {QueryPrep = QueryPrep+"maxdepth="+parametersMap.get(key)+"&"; }
                if (key.contains("minmag")) {QueryPrep = QueryPrep+"minmag="+parametersMap.get(key)+"&"; }
                if (key.contains("maxmag")) {QueryPrep = QueryPrep+"maxmag="+parametersMap.get(key)+"&"; }
                /*
                    OtherPrep
                */
                if (key.contains("lat")) {OtherPrep = OtherPrep+"lat="+parametersMap.get(key)+"&"; }
                if (key.contains("lon")) {OtherPrep = OtherPrep+"lon="+parametersMap.get(key)+"&"; }
                if (key.contains("maxradius")) {OtherPrep = OtherPrep+"maxradius="+parametersMap.get(key)+"&"; }
                if (key.contains("minradius")) {OtherPrep = OtherPrep+"minradius="+parametersMap.get(key)+"&"; }
                
                if (key.contains("minlat")) {OtherPrep = OtherPrep+"minlat="+parametersMap.get(key)+"&"; }
                if (key.contains("maxlat")) {OtherPrep = OtherPrep+"maxlat="+parametersMap.get(key)+"&"; }
                if (key.contains("minlon")) {OtherPrep = OtherPrep+"minlon="+parametersMap.get(key)+"&"; }
                if (key.contains("maxlon")) {OtherPrep = OtherPrep+"maxlon="+parametersMap.get(key)+"&"; }
                /*
                    PostPrep
                */
                if (key.contains("arrivals")) {PostPrep = PostPrep+"&includearrivals=true"; }
                if (key.contains("stations")) {PostPrep = PostPrep+"&includeallstationsmagnitudes=true"; }
                if (key.contains("origins")) {PostPrep = PostPrep+"&includeallorigins=true"; }
                if (key.contains("magnitudes")) {PostPrep = PostPrep+"&includeallmagnitudes=true"; }
                if (key.contains("after")) {PostPrep = PostPrep+"&updatedafter=true"; }
                if (key.contains("orderby")) {PostPrep = PostPrep+"&orderby="+parametersMap.get(key); }
                if (key.contains("limit")) {PostPrep = PostPrep+"&limit="+parametersMap.get(key); }
                if (key.contains("eventversion")) {eventVersion = parametersMap.get(key); }
                
            }
        }
                  
        try {
            // ** Parse from WS
            QmlParser parser = new QmlParser(eventVersion);
            parser.setExtended(Boolean.valueOf(extended));
            phs = parser.ParseQuakeML(baseUrl, QueryPrep,OtherPrep,PostPrep );
            // ** Save file PHS
            File file = new File(filename);
            if (!file.exists()) {
                    file.createNewFile();
            }
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(" "+System.lineSeparator());
            String currentEvent = null;
            for (int i = 0;i<phs.size(); i++){
                if(!phs.get(i).getEventId().equalsIgnoreCase(currentEvent)) {
                    System.out.println("  \n **************** Event id: " + phs.get(i).getEventId()+"  ");
                    currentEvent = phs.get(i).getEventId();
                    bw.write("                 10 -EVENT ID: "+currentEvent+System.lineSeparator());
                }
                String content = phs.get(i).getFormatPhsLine(Boolean.valueOf(extended));  
                if(content != null) {
                    System.out.println( content + " pickID:"+phs.get(i).getPickId());
                    bw.write(content+System.lineSeparator());
                }
            }
            bw.close();
            System.out.println("\n ..Done");
           
        } catch ( IOException | SAXException | ParserConfigurationException | ParseException  ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("error:  "+ex);
        }
        System.exit(0);
        
        
    }
    
}