package it.ingv;


import java.util.Formatter;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author massimo.fares@ingv.it
 */
public class PhsEvent {
    
     public String   
             eventId,  latitude, longitude, 
             preferredOriginID,preferredMagnitudeID,
             creationTime, author, agencyID ;


    
    public PhsEvent() {
        
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getPreferredOriginID() {
        return preferredOriginID;
    }

    public void setPreferredOriginID(String preferredOriginID) {
        this.preferredOriginID = preferredOriginID;
    }

    public String getPreferredMagnitudeID() {
        return preferredMagnitudeID;
    }

    public void setPreferredMagnitudeID(String preferredMagnitudeID) {
        this.preferredMagnitudeID = preferredMagnitudeID;
    }

    public String getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(String creationTime) {
        this.creationTime = creationTime;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAgencyID() {
        return agencyID;
    }

    public void setAgencyID(String agencyID) {
        this.agencyID = agencyID;
    }
    
    
    
    
    
    /*
        print on fly
    */
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        Formatter formatter = new Formatter(stringBuilder);

        String template = "eventId: %-15s date %-15s "+System.lineSeparator(); //    %-35.2f %-5.2f $ %-8.2f
        formatter.format(template,getEventId(), getCreationTime() );
        return stringBuilder.toString();
    }
    
    
    
}
