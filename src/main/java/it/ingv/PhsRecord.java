package it.ingv;


//import java.util.ArrayList;
import java.util.Formatter;


/**
 *
 * @author massimo.fares@ingv.it
 * 
 */
public class PhsRecord {
    
    public String   eventId, pickId,amplitudeId ,
                    networkCode,stationCode,channelCode,locationCode, 
                    onset, phaseHint,phasePTime, polarity, pWeightCode, pickTime,pickDate, pickDateTime,
                    phaseHintS, phaseSTime,sWeightCode,
                    genericAmplitudeAmp, genericAmplitudeDur, unit, period, author;
    
    public Boolean temp;
    
    //** construct **
    public PhsRecord() {
        
    }
 

    /*
    *
    *    * * * setters * * *
    *
    */   
    public void setTemp(Boolean temp) {
        this.temp = temp;
    }
    
    public void setpWeightCode(String pWeightCode) {
        this.pWeightCode = pWeightCode;
    }
    
    public void setsWeightCode(String sWeightCode) {
        this.sWeightCode = sWeightCode;
    }
    
    public void setPhasePTime(String phasePTime) {
        this.phasePTime = phasePTime;
    }
    
    public void setPickDateTime(String pickDateTime) {
        this.pickDateTime = pickDateTime;
    }
    
    public void setPhaseHintS(String phaseHintS) {    
        this.phaseHintS = phaseHintS;
    }
    
    public void setPickDate(String pickDate) {
        this.pickDate = pickDate;
    }
    
    public void setPhaseSTime(String phaseSTime) {
        this.phaseSTime = phaseSTime;
    }
    
    public void setGenericAmplitudeDur(String genericAmplitudeDur) {
        this.genericAmplitudeDur = genericAmplitudeDur;
    }
    
    public void setAuthor(String author) {
        this.author = author;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }
  
    public void setPickId(String pickId) {
        this.pickId = pickId;
    }
    
    public void setAmplitudeId(String amplitudeId) {
        this.amplitudeId = amplitudeId;
    }
    
    public void setNetworkCode(String networkCode) {
        this.networkCode = networkCode;
    }
    
    public void setStationCode(String stationCode) {
        this.stationCode = stationCode;
    }
    
    public void setChannelCode(String channelCode) {
        this.channelCode = channelCode;
    }
    
    public void setLocationCode(String locationCode) {
        this.locationCode = locationCode;
    }
        
    public void setOnset(String onset) {
        this.onset = onset;
    }
    
    public void setPhaseHint(String phaseHint) {
        this.phaseHint = phaseHint;
    }
    
    public void setPolarity(String polarity) {
        this.polarity = polarity;
    }
    
    public void setGenericAmplitudeAmp(String genericAmplitude) {
        this.genericAmplitudeAmp = genericAmplitude;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public void setPeriod(String period) {
        this.period = period;
    }
    
    public void setPickTime(String pickTime) {
        this.pickTime = pickTime;
    }
    
    /*
    *
    *    * * * getters * * *
    *
    */
    
    public Boolean getTemp() {
        return temp;
    }

    public String getpWeightCode() {
        return pWeightCode;
    }

    public String getsWeightCode() {
        return sWeightCode;
    }

    public String getPickDateTime() {
        return pickDateTime;
    }

    public String getPhasePTime() {    
        return phasePTime;
    }

    public String getPhaseHintS() {
        return phaseHintS;
    }

    public String getPickDate() {
        return pickDate;
    }

    public String getPhaseSTime() {
        return phaseSTime;
    }

    public String getGenericAmplitudeDur() {
        return genericAmplitudeDur;
    }
    
    public String getAuthor() {
        return this.author;
    }
    
    public String getAmplitudeId() {
        return amplitudeId ;
    }

    public String getPickId() {
        return this.pickId ;
    }
    
    public String getNetworkCode( ) {
        return this.networkCode;
    }
    
    public String getStationCode( ) {
        return this.stationCode;
    }
    
    public String getChannelCode( ) {
        return this.channelCode;
    }
    
    public String getLastChannelCode( ) {
        if (channelCode.length() < 2 ) return this.channelCode; // bad channel ?
        String LastChannel = channelCode.substring(2);
        return LastChannel;
    }
    
    public String getLocationCode( ) {
        return this.locationCode;
    }
    
    public String getOnset( ) {
        return this.onset;
    }
    
    public String getPhaseHint( ) {
        return this.phaseHint;
    }
    
    public String getPolarity( ) {       
        return this.polarity;
    }
    
    public String getGenericAmplitudeAmp() {
        return genericAmplitudeAmp;
    }

    public String getUnit() {
        return unit;
    }

    public String getPeriod() {
        return period;
    }
    
    public String getPickTime() {
        return pickTime;
    }
    
   
    
    
    /*
    *
    *    PHS formatter
    *
    */
    public String getFormatPhsLine(Boolean extended) {
        
        // ** P or S ? **
        
        if( (!extended && ( phaseHint.equalsIgnoreCase("S") || pickDate == null || temp.equals(true) ) ) )// 
            return null;
        
        if( phaseHintS == null)
            phaseHintS = " ";
        
        // ***** Transform/Format the values in PHS style
        
        // ** onset **
        switch(this.onset) {
            case "impulsive" : 
               this.onset = "I";
               break;
            case "emergent" : 
               this.onset = "E";
               break;
            case "questionable" : 
               this.onset = " "; // to be blank
               break;
            
        }
        // ** polarity **
        switch(this.polarity) {
            case "positive" : 
               this.polarity = "+";
               break;
            case "negative" : 
               this.polarity = "-";
               break;
            case "undecidable" : 
               this.polarity = " "; // to be blank
               break;
            
        }
       
        
        // ** Amplitude (amp e dur) **
        if(genericAmplitudeAmp==null) genericAmplitudeAmp = "   "; // 3 digit
        
        if(genericAmplitudeDur==null || genericAmplitudeDur.equalsIgnoreCase("     ")) {
            genericAmplitudeDur = "     "; // 5 digit
        } 
        else {
            genericAmplitudeDur = String.format("%05d", Integer.parseInt(genericAmplitudeDur));
        }
        
        // ** period **
        if(period == null) period = "   ";
        
        // ** Weight **
        if (sWeightCode == null) sWeightCode = " ";
       
        // ** phaseSTime **
        if (phaseSTime == null) {
            phaseSTime = " ";
        }
        else if(phaseSTime.length() > 5)
            phaseSTime = phaseSTime.substring(0, 5);
        
        // ** stationCode V char issue
        String stationCodeV = " ";
        String stationCode1 = "    ";
        if (stationCode.length() > 4) {
            stationCode1 = stationCode.substring(0, 4);
            stationCodeV = stationCode.substring(4,5);
        }
        else 
            stationCode1 = stationCode;
        
        // ** Construct the Line for PHS file
        StringBuilder stringBuilder = new StringBuilder();
        Formatter formatter = new Formatter(stringBuilder);
        String template;
        if( ((phaseHint.contains("S") || pickDate == null || temp.equals(true)) && extended)  ) //
            template = "$%-4s%-1s%-1s%-1s%-1s%-1s%-6s%-4s%-5s       %-5s %-1s %-1s    %-3s%-3s                    %-4s  %-1s%-3s%-3s%-2s";
        else
            template = "%-4s%-1s%-1s%-1s%-1s%-1s%-6s%-4s%-5s       %-5s %-1s %-1s    %-3s%-3s                    %-4s  %-1s%-3s%-3s%-2s";
        
        formatter.format(template, 
                stationCode1 , onset,phaseHint,polarity,pWeightCode,getLastChannelCode( ),pickDate,pickDateTime,phasePTime, 
                phaseSTime,phaseHintS,sWeightCode,
                genericAmplitudeAmp,period,
                genericAmplitudeDur,
                stationCodeV,channelCode, networkCode,locationCode);
        
        return stringBuilder.toString();
       
    }
    
    /*
    *
    *    print on-the-fly 
    *
    */
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        Formatter formatter = new Formatter(stringBuilder);
        String PSTime = "";
        if (getpWeightCode() != null) PSTime=getpWeightCode();
        if (getsWeightCode() != null) PSTime=getsWeightCode();
        // eId: 2013-08-24T17:18:20.81 HPAC  emergent     P     positive Z     smi:webservices.ingv.it/fdsnws/event/1/query?pickId=65432481   null             null  null     null            HHZ   IV  
        String template = "eId: %-8s %-5s %-5s     %-5s  %-5s %-5s %-5s %-15s   %-15s  %-5s %-5s    %-15s %-5s %-5s %-5s | %-15s"+System.lineSeparator(); 
        formatter.format(template, getPickTime(), getStationCode(), getOnset(), 
                getPhaseHint(), PSTime, getPolarity(), getLastChannelCode( ), getPickId(),
                getGenericAmplitudeAmp(),getGenericAmplitudeDur(),getUnit(),
                getPeriod(),getChannelCode( ),getNetworkCode( ),getLocationCode( ),getAuthor()
                );
        return stringBuilder.toString();
    }
    
}
