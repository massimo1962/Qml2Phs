package it.ingv;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xml.sax.SAXException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author massimo.fares@ingv.it
 */
public class HttpUtil {
    
    public HttpUtil() {
        
    }
    
    public String RequestWs1(String WsQueryString) throws IOException, SAXException {
        
        System.out.print("\n send request"+WsQueryString);
        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(WsQueryString);
        HttpResponse response = client.execute(request);
        BufferedReader rd = new BufferedReader (new InputStreamReader(response.getEntity().getContent()));
        String line = "";
        String resposnse = "";
        StringBuilder thisQuakeML = new StringBuilder();
        Date now= new Date();
        System.out.print("\n building xml" + now.toString() );
        while ((line = rd.readLine()) != null) {
            //System.out.println(line);
            thisQuakeML.append(line);
            thisQuakeML.append("\n");
            resposnse += line+"\n"; 
        }
        if (resposnse.contains("Error")) System.out.println("\n * * * ERROR * * * \n" + resposnse );
        return resposnse;
    }
    
}
