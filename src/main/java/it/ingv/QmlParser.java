package it.ingv;


import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;
import static java.lang.Math.sqrt;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


/**
 *
 * @author massimofares :: massimo.fares@ingv.it
 * 
 */

public class QmlParser  {
    
    private final List<PhsRecord> phsRecords ;
    private final List<PhsRecord> phsRecord4Station;
    private final ParseUtil util ;
    private String eventId;
    private Boolean extended;
    private String EventVersion;
    
    
    
    /*
    *
    * From GUI
    */
    public QmlParser(javax.swing.JTextArea jTextAreaTEST, String EventVer) {
        phsRecords = new ArrayList<>();
        util = new ParseUtil();
        phsRecord4Station = new ArrayList<>();
        EventVersion = EventVer;
        PrintStream printStream = new PrintStream(new CustomOutputStream(jTextAreaTEST));
        //PrintStream printStream = jTextAreaTEST;
        System.setOut(printStream);
        System.setErr(printStream);
        
        
    }
    
    /*
    *
    * From command line
    */
    public QmlParser(String EventVer) {
        phsRecords = new ArrayList<>();
        util = new ParseUtil();
        phsRecord4Station = new ArrayList<>();
        EventVersion = EventVer;
        
    }
    
    public Boolean getExtended() {
        return extended;
    }

    public void setExtended(Boolean extended) {
        this.extended = extended;
    }
    
    

    
    /*
    *                   **** from WS ****
    *
    * ParseQuakeML(String baseUrl, String QueryPrep,String OtherPrep,String PostPrep) 
    *
    *
    */
    public List<PhsRecord> ParseQuakeML(String baseUrl, String QueryPrep,String OtherPrep,String PostPrep) throws SAXException, IOException, ParserConfigurationException, ParseException {

        String publicID;
        String[] totalPublicID;  
        Element events;
        Node origin;
        Element origins;
        String preferredOriginID, selectedVersion;
        NodeList prefOrigin;
        String currentOrigin;
        Date now;
        String content;
        
        System.out.println("\n  parser called :  event version required:"+this.EventVersion);
        String postClient = "&orderby=time-asc&format=xml"; 
        
        //** set a user for statistics - ingv only **         
        if(baseUrl.contains(".ingv.it")) postClient = postClient+"&user=qml_to_phs"; 
        
        //** retrieve the NodeList for EventId ** 
        //* call webservices
        NodeList elementList = GetXmlDocNodeList (baseUrl+QueryPrep);
        System.out.println("\n  GetXmlDocNodeList :  "+elementList.getLength());
        /*
        * ** foreach event **
        */
        for(int x = 0; x < elementList.getLength(); x++) {
            publicID = util.getNodeAttr("publicID", elementList.item(x) );
            totalPublicID = publicID.split(":");
            // ** fix this issue (others ws don't work properly!!)
            
            if(publicID.contains("smi:")) {
                eventId = publicID.substring(publicID.indexOf("=") +1, publicID.length());
            }

            System.out.print("\n eventId [ "+eventId+" ]\n ");
            System.out.print("\n publicID [ "+publicID+" ]\n ");
            NodeList eventRoot = GetXmlDocNodeList (baseUrl+"query?eventid="+eventId+PostPrep+postClient+"");
            events = (Element) eventRoot.item(0);  // always the first - of course
            origin = util.getNode("origin", eventRoot); 
            origins = (Element) origin;
            /*
            *
            *  events , origins
            */
            //** select OriginID from Event Version **
            prefOrigin =  events.getElementsByTagName("preferredOriginID");
            preferredOriginID = util.getNodeValue(prefOrigin.item(0));
            NodeList originList = events.getElementsByTagName("origin");
            System.out.println("\n preferredOriginID :  "+preferredOriginID+"\n requested Event Version: "+this.EventVersion);
            
            for (int y= 0; y < originList.getLength(); y++) {
                currentOrigin = util.getNodeAttr("publicID",originList.item(y));
                selectedVersion =  util.getNodeDeepValue (originList.item(y), "creationInfo", "version");
                if (this.EventVersion.equalsIgnoreCase("preferred") && currentOrigin.equalsIgnoreCase(preferredOriginID))
                    origins = (Element)originList.item(y);
                else if (selectedVersion.equalsIgnoreCase(this.EventVersion))
                    origins = (Element)originList.item(y);
            }
            
            /*
            *
            * ** pick info ** 
            *
            */
            //
            System.out.println("\n  pickExtractor : ");
            pickExtractor(events,origins );
            
            /*
            *
            * ** amplitude info **
            *
            */
            //
            System.out.println("\n  amplitudeExtractor :  ");
            amplitudeExtractor(events);
            
            /*
            *
            * ** foreach record calculate S time **
            *
            */
            //
            System.out.println("\n  phaseSTimeCalc :  ");
            phaseSTimeCalc ();
        }

        /*
        *
        * ** END process, ready to write File **
        *
        */
        now= new Date();
        System.out.print("\n end process, ready to write File "+now+"\n\n");
        String author;
        
        /*
        *
        * ** Final Print 4 Checks
        */
        System.out.println("\n *************************************************************************************************************** \n");
        for (int i = 0;i<phsRecords.size(); i++){
            
            content = phsRecords.get(i).getFormatPhsLine(false); //+System.lineSeparator()
            author = phsRecords.get(i).getAuthor();
            if(content != null ) {
                
                
                System.out.println(content + " pickID:"+phsRecords.get(i).getPickId()+ " : "+author);

            }
        }
        /*
        *
        * Return PHS to Main
        *
        */
        return phsRecords;
        
    } 
    
    
    
    /*
    *   ***********************************************
    *   
    *   PickExtractor (Element events, Element origins)
    *
    *   ***********************************************
    */
    private void pickExtractor (Element events, Element origins) {
        
        Map<String, String> arrivalMap = new HashMap<>();
        String currentPickId, uncertaintyString, PhaseHint, onset , evaluationMode, author;
        PhsRecord objPick;
        double uncertaintyDouble;
        String[] timeP;
        List picksPublicIdList = util.getMultiNodeAttribute(  events, "pick", "publicID" );
        List arrivalPublicIdList = util.getMultiNodeAttribute(  origins, "arrival", "publicID" );
        
        /* 
        *  load arrivalMap 
        *  HashMAP of arrival is an array with: k=pickID v=arrivalID
        */
        for(int i = 0; i < arrivalPublicIdList.size(); i++) {
            arrivalMap.put(util.selectNodeValue( origins, "arrival", "publicID", (String)arrivalPublicIdList.get(i), "pickID") , (String)arrivalPublicIdList.get(i));
        }
        
        
        //System.out.println(arrivalMap);
        /*
        *   load 'Pick-object' into PHS class 
        */
        for (String key: arrivalMap.keySet()){ 
            for(int h = 0 ; h < picksPublicIdList.size(); h++) {

                //** PHS Record 
                currentPickId = (String)picksPublicIdList.get(h);
                if (currentPickId.equals(key)) {
                    
                evaluationMode = util.selectNodeValue( events, "pick", "publicID", currentPickId, "evaluationMode");
                //if (!evaluationMode.equalsIgnoreCase("manual")) continue;
                objPick = new PhsRecord();

                objPick.setEventId(eventId);
                objPick.setPickId(currentPickId);
                objPick.setNetworkCode(util.selectNodeAttribute( events, "pick", "publicID", currentPickId, "waveformID","networkCode"));
                objPick.setStationCode(util.selectNodeAttribute( events, "pick", "publicID", currentPickId, "waveformID","stationCode"));
                objPick.setChannelCode(util.selectNodeAttribute( events, "pick", "publicID", currentPickId, "waveformID","channelCode"));
                objPick.setLocationCode(util.selectNodeAttribute( events, "pick", "publicID", currentPickId, "waveformID","locationCode"));

                PhaseHint = util.selectNodeValue( events, "pick", "publicID", currentPickId, "phaseHint");

                objPick.setPickTime(util.selectNodeValueDeep( events,  "pick", "publicID", currentPickId,  "time" , "value"));
                timeP = findTimeDate(util.selectNodeValueDeep( events,  "pick", "publicID", currentPickId,  "time" , "value"));
                    objPick.setPickDate(timeP[1]);
                    objPick.setPickDateTime(timeP[2]+timeP[3]);

                if (PhaseHint != null && PhaseHint.contains("P")) {
                    PhaseHint = "P";
                    objPick.setPhasePTime(timeP[4]+"."+timeP[5]);
                }
                else if (PhaseHint != null && PhaseHint.contains("S")) {
                    PhaseHint = "S";
                    objPick.setPhaseSTime(timeP[4]+"."+timeP[5]);
                }
                objPick.setPhaseHint(PhaseHint);
                onset = util.selectNodeValue( events, "pick", "publicID", currentPickId, "onset");
                if (onset == null) onset = " ";
                objPick.setOnset(onset);
                objPick.setPolarity(util.selectNodeValue( events, "pick", "publicID", currentPickId, "polarity"));
                objPick.setpWeightCode("8"); // ??
                objPick.setsWeightCode(" "); // ??
                objPick.setTemp(true);

                //** PHS record added into PHS class
                phsRecords.add(objPick);
                }
            }
        }
        /*
        *   load 'Phase-Hint' into PHS class 
        */
        for (String key: arrivalMap.keySet()){ 
            
            for(int h = 0 ; h < phsRecords.size(); h++) {
                
                currentPickId = phsRecords.get(h).getPickId();
                if(key.equalsIgnoreCase(currentPickId)) {
                    //* Phase in arrival always present
                    PhaseHint = util.selectNodeValue(  origins, "arrival", "publicID", arrivalMap.get(currentPickId), "phase");
                    //** cut the second letter from Phase (i.e Pp, Ps, ..)
                    if (PhaseHint.contains("P")) PhaseHint = "P";
                    if (PhaseHint.contains("S")) PhaseHint = "S";
                    phsRecords.get(h).setPhaseHint(PhaseHint);
                    phsRecords.get(h).setTemp(false);
                    //* uncertainty not always present 
                    try {
                        uncertaintyDouble = Double.parseDouble(util.selectNodeValueDeep( events,  "pick", "publicID", currentPickId,  "time" , "uncertainty"));
                        uncertaintyString= uncertaintyRetrieve(uncertaintyDouble);
                    }
                    catch (NullPointerException | NumberFormatException ex){
                        uncertaintyString="8";    
                    }
                    //* author
                    author = util.selectNodeValueDeep( events,  "pick", "publicID", currentPickId,  "creationInfo" , "author");
                    phsRecords.get(h).setAuthor(author);
                    //* Weight
                    if (PhaseHint.contains("P")) {
                        phsRecords.get(h).setpWeightCode(uncertaintyString);
                    }
                    else {
                        phsRecords.get(h).setsWeightCode(uncertaintyString);
                    }
                    break;
                }
            }
        }
        
//        // ** PHS-RECORD CHECK **
//        // ** display partial PHS resultset  **
//        System.out.println("\n *************************************************************************************************************** \n");
//        System.out.print("\n      * * * pickExtractor Output * * *\n");
//        System.out.println("\n *************************************************************************************************************** \n");
//        int l = 0;
//        for (PhsRecord phsRecord1 : phsRecords) {
//            l++;
//            System.out.print("\n"+l+") phsRecord Station :  "+ phsRecord1.getStationCode()+" "+phsRecord1.getChannelCode()+" phase: " + phsRecord1.getPhaseHint() +"\n");
//       }
        
    }  
    
    
     
    /*
    *   ***********************************
    *
    *   amplitudeExtractor (Element events)
    *
    *   ***********************************
    */
    private void amplitudeExtractor (Element events) {
        
        String currentAmpli, networkCode, stationCode;
        String channelCode, locationCode; // not used now
        String typeAmpli, typeQml,  periodPhs, genAmpValdur;
        float genericAmplitudeAmpNum = 0, genericAmplitudeCurr =0 , periodNum ;
        int multiplier = 1;
        float meanGeometry = 0;
        Map<String, String> ampMap = new HashMap<>();
        String ampCorrect, unit, currentAmpType, timeReference;
        String[] timePrep;
        PhsRecord objAmp;
        
        List amplitudeList = util.getMultiNodeAttribute(  events, "amplitude", "publicID" );
        //** add all amplitudes 
        for(int i = 0; i < amplitudeList.size(); i++) {
            currentAmpli = (String)amplitudeList.get(i);
            unit = util.selectNodeValue( events,  "amplitude", "publicID", currentAmpli,  "unit" );
            if(unit.equalsIgnoreCase("m")) { multiplier = 2000;}
            else if (unit.equalsIgnoreCase("s")) { multiplier = 2; }
            
            networkCode = util.selectNodeAttribute( events, "amplitude", "publicID", currentAmpli, "waveformID","networkCode");
            stationCode = util.selectNodeAttribute( events, "amplitude", "publicID", currentAmpli, "waveformID","stationCode");
            channelCode = util.selectNodeAttribute( events, "amplitude", "publicID", currentAmpli, "waveformID","channelCode");
            locationCode = util.selectNodeAttribute( events, "amplitude", "publicID", currentAmpli, "waveformID","locationCode");
            
            currentAmpType = util.selectNodeValue( events,  "amplitude", "publicID", currentAmpli,  "type" );
            genericAmplitudeAmpNum = Float.parseFloat(util.selectNodeValueDeep( events,  "amplitude", "publicID", currentAmpli,  "genericAmplitude" , "value"));
            // @TODO: check error!!
            if (!util.selectNodeValueDeep( events,  "amplitude", "publicID", currentAmpli,  "period" , "value").isEmpty()) {
                periodNum = Float.parseFloat(util.selectNodeValueDeep( events,  "amplitude", "publicID", currentAmpli,  "period" , "value"));
            }
            else {
                periodNum = (float) 0.0;
            }
            timeReference = util.selectNodeValueDeep( events,  "amplitude", "publicID", currentAmpli,  "timeWindow" , "reference");
            
            for(int k = 0; k < phsRecords.size(); k++) {

                if(phsRecords.get(k).getNetworkCode().equalsIgnoreCase(networkCode) &&
                    phsRecords.get(k).getStationCode().equalsIgnoreCase(stationCode) ) {
                    // ** enlarge the 'resultset' if needed
                    if (!phsRecords.get(k).getChannelCode().equalsIgnoreCase(channelCode) ) {
//                        
                        objAmp = new PhsRecord();
                        objAmp.setEventId(eventId);
                        objAmp.setAmplitudeId(currentAmpli);
                        objAmp.setNetworkCode(networkCode);
                        objAmp.setStationCode(stationCode);
                        objAmp.setChannelCode(channelCode);
                        objAmp.setLocationCode(locationCode);
                        
                        objAmp.setPhaseHint("XS"); // set fake phase
                        
                        // ** case AML
                        if(currentAmpType.equalsIgnoreCase("AML")) 
                            objAmp.setGenericAmplitudeAmp(convertPhsFloat( genericAmplitudeAmpNum, 4));
                       
                        // ** case END
                        if(currentAmpType.equalsIgnoreCase("END"))
                            objAmp.setGenericAmplitudeDur(String.valueOf(genericAmplitudeAmpNum));
                        
                        objAmp.setPeriod(convertPhsFloat(periodNum, 4));
                        objAmp.setOnset(" ");
                        objAmp.setPolarity(" ");
                        objAmp.setpWeightCode("9"); // ??
                        objAmp.setsWeightCode(" "); // ??
                        objAmp.setTemp(true);
                        
                        objAmp.setPickTime(timeReference);
                        timePrep = findTimeDate(timeReference);
                        objAmp.setPickDate(timePrep[1]);
                        objAmp.setPickDateTime(timePrep[2]+timePrep[3]);
                        objAmp.setPhasePTime(timePrep[4]+"."+timePrep[5]);

                        phsRecords.add(objAmp);
                    }
                    break;
                }
            }
            
        }
        
        // ** attention  includeallstationsmagnitudes not for now - forced off -  (issue?!)
        List stationMagnitudeList = util.getMultiNodeValue(  events, "stationMagnitude", "publicID" ,"amplitudeID" );
        //* array : k=amplitudeID v= amplitudeValue
        for(int g=0;g<stationMagnitudeList.size();g++) {
            genericAmplitudeAmpNum = Float.parseFloat(util.selectNodeValueDeep( events,  "amplitude", "publicID", (String)stationMagnitudeList.get(g),  "genericAmplitude" , "value")); // to be *2000
            ampMap.put((String)stationMagnitudeList.get(g),String.valueOf(genericAmplitudeAmpNum) );
            System.out.println(" in map :"+(String)stationMagnitudeList.get(g) +" "+String.valueOf(genericAmplitudeAmpNum));
        }
        
        //** match P & S
        for(int k = 0; k < phsRecords.size(); k++) { 
            for (String key: ampMap.keySet()){
                currentAmpli = phsRecords.get(k).getAmplitudeId();
                networkCode = util.selectNodeAttribute( events, "amplitude", "publicID", currentAmpli, "waveformID","networkCode");
                stationCode = util.selectNodeAttribute( events, "amplitude", "publicID", currentAmpli, "waveformID","stationCode");
                // channels are not used now
                //channelCode = util.selectNodeAttribute( events, "amplitude", "publicID", currentAmpli, "waveformID","channelCode");
                
                /*
                * switch on QML type: :
                * AML : il nostro amp- -> verificare anche <unit> che se non metri non va moltiplicato x 2000 ma per 2
                * END : che sarebbe il nostro dur-
                * AMB & AMS da verificare se sono utili al phs ma sembra di no
                *
                */
                if(phsRecords.get(k).getNetworkCode().equalsIgnoreCase(networkCode) &&
                    phsRecords.get(k).getStationCode().equalsIgnoreCase(stationCode)  ) {
                    
                    //**   temporary ** to be decide
                    if(currentAmpli.contains("=amp-") || currentAmpli.contains("=dur-"))  // it's INGV WS
                        typeAmpli = currentAmpli.substring(currentAmpli.indexOf("=") +1, currentAmpli.lastIndexOf("-")); 
                    else
                        typeAmpli = "generic"; // it's other WS
                    
                    typeQml = util.selectNodeValue( events,  "amplitude", "publicID", currentAmpli,  "type" );
                    
                    switch(typeAmpli) {
                        case "amp" :
                            try {
                                genericAmplitudeAmpNum = Float.parseFloat(util.selectNodeValueDeep( events,  "amplitude", "publicID", currentAmpli,  "genericAmplitude" , "value"))*2000;
                                // *** mean geometry is used to calculate the 2 components mean; the magnitude result is ok
                                if(ampMap.get(currentAmpli) != null ) {
                                    genericAmplitudeCurr = Float.parseFloat(ampMap.get(currentAmpli))*2000;
                                    meanGeometry = (float) sqrt(genericAmplitudeCurr*genericAmplitudeAmpNum);
                                } else meanGeometry = genericAmplitudeAmpNum;
                            }
                            catch (NullPointerException | NumberFormatException ex) {
                                 //System.out.print("\n"+ex+"\n");
                                periodNum = 0;
                            }
                            ampCorrect =  convertPhsFloat(meanGeometry, 4);
                            periodPhs = convertPhsFloat(Float.parseFloat(util.selectNodeValueDeep( events,  "amplitude", "publicID", currentAmpli,  "period" , "value")), 4);
                            
                            phsRecords.get(k).setGenericAmplitudeAmp(ampCorrect);
                            phsRecords.get(k).setUnit(util.selectNodeValue( events,  "amplitude", "publicID", currentAmpli,  "unit" ));
                            phsRecords.get(k).setPeriod(periodPhs);
                                
                            break;
                            
                        case "dur" :
                            genAmpValdur = util.selectNodeValueDeep( events,  "amplitude", "publicID", currentAmpli,  "genericAmplitude" , "value");
                            phsRecords.get(k).setGenericAmplitudeDur(genAmpValdur);
                            break;
                        
                        case "generic" :
                            if (typeQml.equalsIgnoreCase("AML")) {
                                if(util.selectNodeValue( events,  "amplitude", "publicID", currentAmpli,  "unit" ).equalsIgnoreCase("m")) { multiplier = 2000;}
                                else if (util.selectNodeValue( events,  "amplitude", "publicID", currentAmpli,  "unit" ).equalsIgnoreCase("s")) { multiplier = 2; }
                                
                                genericAmplitudeAmpNum = Float.parseFloat(util.selectNodeValueDeep( events,  "amplitude", "publicID", currentAmpli,  "genericAmplitude" , "value"))*multiplier;
                                    
                                if(ampMap.get(currentAmpli) != null ) {
                                    genericAmplitudeCurr = Float.parseFloat(ampMap.get(currentAmpli))*multiplier;
                                    meanGeometry = (float) sqrt(genericAmplitudeCurr*genericAmplitudeAmpNum);
                                } else { meanGeometry = genericAmplitudeAmpNum; }
                                
                                phsRecords.get(k).setGenericAmplitudeAmp(convertPhsFloat(meanGeometry, 4));
                                phsRecords.get(k).setUnit(util.selectNodeValue( events,  "amplitude", "publicID", currentAmpli,  "unit" ));
                                phsRecords.get(k).setPeriod(util.selectNodeValueDeep( events,  "amplitude", "publicID", currentAmpli,  "period" , "value"));
                            }
                            else if (typeQml.equalsIgnoreCase("END")) {
                                genAmpValdur = util.selectNodeValueDeep( events,  "amplitude", "publicID", currentAmpli,  "genericAmplitude" , "value");
                                phsRecords.get(k).setGenericAmplitudeDur(genAmpValdur);
                            }
                            break;
                    }
                    break;    
                } 
            } 
        }
        
//        // *** for check resultset
//        System.out.print("\n Amp extractor \n");
//        int l = 0;
//        for (PhsRecord phsRecord1 : phsRecord) {
//            l++;
//            System.out.print("\n"+l+") "+ " phase: " + phsRecord1.getPhaseHint() + 
//                    " Sweight:" + phsRecord1.getsWeightCode() + " Pweight:" + phsRecord1.getpWeightCode() + " " + phsRecord1.getStationCode() + 
//                    " " + phsRecord1.getChannelCode() + " amp:" + phsRecord1.getGenericAmplitudeAmp()+" dur:"+ phsRecord1.getGenericAmplitudeDur() + 
//                    " pickid:" + phsRecord1.getPickId()  + "\n");
//        }
    }  
    
    
    
    /*
    *   **********************
    *   
    *   void phaseSTimeCalc () 
    *   
    *   **********************
    */
    private void phaseSTimeCalc () throws ParseException {
        PhsRecord recordP = null; 
        PhsRecord recordS = null;          
        String[] timeP = null;
        String authorS = null;
        
        for(int k = 0; k < phsRecords.size(); k++) {
            
            authorS = phsRecords.get(k).getAuthor();
            if(!phsRecords.get(k).getTemp() && phsRecords.get(k).getPhaseHint().contains("P") ) { // P is current k
                // set P time
                timeP = findTimeDate(phsRecords.get(k).getPickTime());
                phsRecords.get(k).setPickDate(timeP[1]);
                phsRecords.get(k).setPickDateTime(timeP[2]+timeP[3]);
                phsRecords.get(k).setPhasePTime(timeP[4]+"."+timeP[5]);
                // ** find S
                recordS = findStation(phsRecords.get(k).getNetworkCode(), phsRecords.get(k).getStationCode(),phsRecords.get(k).getChannelCode(), phsRecords.get(k).getEventId(),"S");
                
                if (recordS != null && recordS.getAuthor() != null ) {
                    
                    // set phaseSTime
                    phsRecords.get(k).setPhaseSTime(findDifferencePS (phsRecords.get(k).getPickTime(), recordS.getPickTime()));
                    phsRecords.get(k).setPhaseHintS("S");
                    
                }
            }
            else if( !phsRecords.get(k).getTemp() && phsRecords.get(k).getPhaseHint().equalsIgnoreCase("S") && authorS != null ) { // S is current k
                // ** find P
                recordP = findStation(phsRecords.get(k).getNetworkCode(),phsRecords.get(k).getStationCode(),phsRecords.get(k).getChannelCode(), phsRecords.get(k).getEventId(),"P");
                if (recordP != null) {
                    // set phaseSTime 
                    recordP.setPhaseSTime(findDifferencePS (recordP.getPickTime(), phsRecords.get(k).getPickTime()));
                    recordP.setsWeightCode(phsRecords.get(k).getsWeightCode());
                    recordP.setGenericAmplitudeDur(phsRecords.get(k).getGenericAmplitudeDur());
                    recordP.setGenericAmplitudeAmp(phsRecords.get(k).getGenericAmplitudeAmp());
                     // set P time
                    timeP = findTimeDate(recordP.getPickTime());
                    recordP.setPickDate(timeP[1]);
                    recordP.setPickDateTime(timeP[2]+timeP[3]);
                    recordP.setPhasePTime(timeP[4]+"."+timeP[5]);
                }
            }
            
        }
        
    }   
 
    /*
    *
    *   GetXmlDocNodeList (String FormattedUri)
    *   retrieve from WS a List of Event included into time-span requested
    *
    */
    public NodeList GetXmlDocNodeList (String FormattedUri) throws SAXException, IOException, ParserConfigurationException  {
        
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Date now= new Date();
        System.out.print("\n load obj Document w xml ... "+ now.toString()+"\n uri:"+FormattedUri+"\n" );
        // ** connect to WS **
        Document document = builder.parse(new URL(FormattedUri).openStream());
        //now= new Date();
        //System.out.print(" loaded! "+ now.toString()+" "+ "\n" );
        if (document.toString().contains("Error")) System.out.print("\n * * * ERROR * * * \n" + document.toString());
        //**  root QML node **
        NodeList root = document.getChildNodes();
        
        //** search events **
        Node quakeml = util.getNode("q:quakeml", root);
        Node eventParam = util.getNode("eventParameters", quakeml.getChildNodes());
        Element eventsParams = (Element) eventParam;
        
        //** return Event List **
        return eventsParams.getElementsByTagName("event");
    }
 
    /*
    *
    *   findDifferencePS (String getPickTimeP, String getPickTimeS) in PHS style
    *
    *   this difference is calculated from the last Minute of P and the current time of S 
    *   ie: P-time: 10.23.45,30 :: S-time: 10.24.40,30  diff= 1.05 (minutes) 
    */
    private String findDifferencePS (String getPickTimeP, String getPickTimeS) throws ParseException {

            
        String ReturnString = null;
        
        // get millisec
        String[] getPickTimeSmillisec = getPickTimeS.split("\\.");
        
        // Custom date format
        SimpleDateFormat format = new SimpleDateFormat("yyyy-dd-MM'T'HH:mm:ss"); 
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-dd-MM'T'HH:mm"); 
        Date d1 = null;
        Date d2 = null;
        try {
            d1 = format1.parse(getPickTimeP);
            d2 = format.parse(getPickTimeS);
        } catch (ParseException e) {
            System.out.print(e);
        } 
        
        // Get millisec from each, and subtract.
        long diff = (d2.getTime() - d1.getTime())/1000;
        String formattedDiff = String.format( "%02d",diff);
        String finalDiff = formattedDiff+"."+getPickTimeSmillisec[1];
        ReturnString = finalDiff;
        
        return ReturnString;
    }
    
    /*
    *
    *   findStation (String networkCode, String stationCode,String channelCode, String eventID, String phaseHint)
    *   retrieve a PHS record selected by SCN + event ID and Phase
    *
    */
    private PhsRecord findStation (String networkCode, String stationCode,String channelCode, String eventID, String phaseHint) {
        PhsRecord ret=null;
        for (PhsRecord record: phsRecords) {
            if (record.getNetworkCode().equalsIgnoreCase(networkCode) &&
                record.getStationCode().equalsIgnoreCase(stationCode) &&
                record.getChannelCode().equalsIgnoreCase(channelCode) &&    
                record.getEventId().equalsIgnoreCase(eventID) && 
                record.getPhaseHint().equalsIgnoreCase(phaseHint)) {
                
                ret= record;
                break;
            }
        }
        return ret;
    }
 
    /*
    *   findTimeDate(String DateTime)
    *
    *   this method return an array:
    *
    *    ReturnString[0] = Date: YYYY-MM-DD
    *    ReturnString[1] = DatePHS: YYMMDD
    *    ReturnString[2] = Hour: hh
    *    ReturnString[3] = Min: mm
    *    ReturnString[4] = sec: ss
    *    ReturnString[5] = millsec: iiiii
    *
    *    from input format date & time  :: 2012-05-29T07:01:43.59998
    */
    private String[] findTimeDate(String DateTime) {
        // 
        String[] ReturnString = new String[6];
        String[] secMil = null;
        if(DateTime.isEmpty()) return ReturnString;

        String[] PhsDatePart = DateTime.split("T"); // 
        String[] PhsDatePrep = PhsDatePart[0].split("-");
        String PhsDateYY = PhsDatePrep[0].substring(2, 4);

        ReturnString[0] = PhsDatePart[0]; // // Date: YYYY-MM-DD
        ReturnString[1] = PhsDateYY+PhsDatePrep[1]+PhsDatePrep[2]; // DatePHS: YYMMDD
       
        String[] PhsTimePart = PhsDatePart[1].split(":");  //  Hour + Min + sec.mill
        ReturnString[2] = PhsTimePart[0]; // Hour: hh
        ReturnString[3] = PhsTimePart[1]; // Min: mm

        secMil = PhsTimePart[2].split("\\."); // sec & milliseconds
        ReturnString[4] = secMil[0]; // sec
        if (secMil[1].length() > 2)  // milliseconds
            ReturnString[5] = secMil[1].substring(0,2 ); 
        else 
            ReturnString[5] = secMil[1];
        
        
        return ReturnString;
    }   
    
    /*
    *
    *   uncertaintyRetrieve
    *   retrieve PHS value for uncertainty QML
    *
    */
    private String uncertaintyRetrieve(double uncertaintyDouble) {
        String uncertaintyString;
        if  ( uncertaintyDouble <= 0.1 ) { 
            uncertaintyString="0";
        }
        else if (uncertaintyDouble <= 0.3) {
            uncertaintyString="1";
        }
        else if (uncertaintyDouble <= 0.6) {
            uncertaintyString="2";
        }
        else if (uncertaintyDouble <= 1.0) {
            uncertaintyString="3";
        }
        else if (uncertaintyDouble <= 3.0) {
            uncertaintyString="4";
        }
        else   uncertaintyString="8";
        
        return uncertaintyString;
    } 
    
    /*
    *
    *   convertPhsFloat (Float number, int maxLength)
    *   PHS Fortran Float converter
    *
    */
    private String convertPhsFloat (Float number, int maxLength) {
        
        if (number < 1.0 ) { 
            if (String.valueOf(number).length() > maxLength-1)
                return String.valueOf(number).substring(1, maxLength);
            else
                return String.valueOf(number);

        } else if (number > 1.0 && number < 999.9) {
            if (String.valueOf(number).length() > maxLength-1)
                return String.valueOf(number).substring(0, maxLength-1);
            else
                return String.valueOf(number);
                
        } 
        return " ";
    }
    
    
    /*
    *             **** from local file *****
    *
    *           ParseQuakeML(String QMLstring) 
    *
    *
    *
    *
    *
    *
    */
    public List<PhsRecord> ParseQuakeML(String QMLstring) throws SAXException, IOException, ParserConfigurationException, ParseException {

        String publicID;
        Element events;
        Node origin;
        Element origins;
        String preferredOriginID;
        NodeList prefOrigin;
        String currentOrigin;
        Date now;
        
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        System.out.println("\n load obj Document from file ... \n" );

        InputSource is = new InputSource(new StringReader(QMLstring));
        Document document = builder.parse(is);

        now= new Date();
        System.out.print(" loaded! "+ now.toString()+"\n" );
        
        //**  root QML node
        NodeList root = document.getChildNodes();
        
        //** search events
        Node quakeml = util.getNode("q:quakeml", root);
        Node eventParam = util.getNode("eventParameters", quakeml.getChildNodes());
        Element eventsParams = (Element) eventParam;
        
        //** return Event List
        NodeList eventRoot = eventsParams.getElementsByTagName("event");
        events = (Element) eventRoot.item(0);  // always the first

        publicID = util.getNodeAttr("publicID", eventRoot.item(0));
        eventId = publicID.substring(publicID.indexOf("=") +1, publicID.length());

        origin = util.getNode("origin", eventRoot); 
        origins = (Element) origin;
        
        //** select only preferredOriginID
        prefOrigin =  events.getElementsByTagName("preferredOriginID");
        preferredOriginID =  util.getNodeValue(prefOrigin.item(0));
        NodeList originList = events.getElementsByTagName("origin");

        for (int y= 0; y < originList.getLength(); y++) {
            currentOrigin =  util.getNodeAttr("publicID",originList.item(y));
            if(currentOrigin.equalsIgnoreCase(preferredOriginID))
                origins = (Element)originList.item(y);
        }
        
        //** pick info
        pickExtractor(events,origins );
        System.out.print("\n end process pickExtractor "+now);
        
        //** amplitude info
        amplitudeExtractor(events);
        System.out.print("\n end process amplitudeExtractor "+now);
        
        //** foreach record calculate time
        phaseSTimeCalc ();
        now= new Date();
        System.out.print("\n end process prep File "+now);
            
        return phsRecords;
        
    }        
    
   
   
}