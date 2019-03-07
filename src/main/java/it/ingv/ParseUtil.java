package it.ingv;


import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author massimo.fares@ingv.it
 */
public class ParseUtil {
    
    public ParseUtil(){
        
    }
    
   
    

    /*
    * return a string: selectNodeValue(  node,  tagName,  attrName,  searchAttr,  searchTagName)
    *
    * somethings like this:   SELECT tagName.searchTagName FROM node WHERE tagName.attrName = searchAttr LIMIT 1 
    * 
    *   e.g.
    *   selectNodeValue( events,  "amplitude", "publicID", currentAmpli,  "category");
    
    <amplitude publicID="smi:webservices.rm.ingv.it/fdsnws/event/1/query?amplitudeId=amp-59781671">
        <category>other</category> <-- this!
    *
    *
    */
    protected String selectNodeValue( Node node, String tagName, String attrName, String searchAttr, String searchTagName) {
        String result = null;
        Element elements = (Element) node;
        NodeList myNodeList = elements.getElementsByTagName(tagName);
        //Node current=null;
        String currentAttribute;
        for (int i = 0; i < myNodeList.getLength(); i++) {
            //current = myNodeList.item(i);
            currentAttribute = getNodeAttr(attrName,myNodeList.item(i));
            if (currentAttribute.equalsIgnoreCase(searchAttr)) {
                result = getNodeValue(searchTagName, myNodeList.item(i).getChildNodes()); 
                break;
            }
        }
        return result;
    }

    /*
    *   selectNodeValueDeep( Node node, String tagName, String attrName, String searchAttr, String searchTagName, String deepSearch)
    *   
    *   somethings like this:   SELECT tagName.searchTagName.deepSearch FROM node WHERE tagName.attrName = searchAttr  LIMIT 1 
    *   
    *   e.g.
        selectNodeValueDeep( events,  "amplitude", "publicID", currentAmpli,  "genericAmplitude" , "value")
    
        <amplitude publicID="smi:webservices.rm.ingv.it/fdsnws/event/1/query?amplitudeId=amp-59781671">
            <genericAmplitude>
                <value>0.0001075</value> <-- this!
            </genericAmplitude>
    
    */
    protected String selectNodeValueDeep( Node node, String tagName, String attrName, String searchAttr, String searchTagName, String deepSearch) {
        String result = null;
        Element elements = (Element) node;
        NodeList myNodeList = elements.getElementsByTagName(tagName);
        //Node current;
        String currentAttribute;
        Node deepResult;
        Node resultDeep;
        for (int i = 0; i < myNodeList.getLength(); i++) {
            //current = myNodeList.item(i);
            currentAttribute = getNodeAttr(attrName,myNodeList.item(i));
            if (currentAttribute.equalsIgnoreCase(searchAttr)) {
                deepResult = getNode(searchTagName, myNodeList.item(i).getChildNodes());
                try {
                resultDeep = getNode(deepSearch, deepResult.getChildNodes());
                
                result = getNodeValue(resultDeep);
                } catch (NullPointerException ex) {
                    result = "";
                }
                
                break;
            }
        }
        return result;
    }
    
    /*
    *
    *   selectNodeAttribute(  node,  tagName,  attrName,  searchAttr,  secondTagName,  search2AttrName);
    * 
    
    *   somethings like this:   SELECT tagName.secondTagName.search2AttrName FROM node WHERE tagName.attrName = searchAttr   LIMIT 1 
    
    e.g.
            selectNodeAttribute( events, "pick", "publicID", currentPickId, "waveformID","networkCode")
    
    <pick publicID="smi:webservices.rm.ingv.it/fdsnws/event/1/query?pickId=60811731">
        <waveformID networkCode="IV" ( <-- this!) stationCode="LNSS" channelCode="HHZ" locationCode=""/>
    
    *
    */
    protected String selectNodeAttribute( Node node, String tagName, String attrName, String searchAttr, String secondTagName, String search2AttrName) {    
        String result = null;
        Element elements = (Element) node;
        NodeList myNodeList = elements.getElementsByTagName(tagName);
        Element currentElement;
        String currentAttribute;
        NodeList secondList;
        
        for (int i = 0; i < myNodeList.getLength(); i++) {
            currentElement = (Element) myNodeList.item(i);
            currentAttribute = getNodeAttr(attrName,myNodeList.item(i));
            if (currentAttribute.equalsIgnoreCase(searchAttr)) {
                secondList = currentElement.getElementsByTagName(secondTagName);
                result = getNodeAttr(search2AttrName, (Element)secondList.item(secondList.getLength()-1));
                break;
            }
        }
        return result;
    }    
    /*
    * getMultiNodeAttribute( Node node, String tagName, String attrName )
    *
    * List(resultset) of: SELECT (valueof)attrName FROM node WHERE TAG = tagName 
    */
    protected List getMultiNodeAttribute( Node node, String tagName, String attrName ) {
        List nodeAttrList = new ArrayList<>();
        Element elements = (Element) node;
        NodeList myNodeList = elements.getElementsByTagName(tagName);
        Node current;
        for (int i = 0; i < myNodeList.getLength(); i++) {
            current = myNodeList.item(i);
            nodeAttrList.add(getNodeAttr(attrName,current ));  
        }
        return nodeAttrList;
    }
    
    /*
    * 
    *   getMultiNodeValue(  origins, "arrival","publicID" , "pickID")
    * 
    *   somethings like this:   SELECT tagName.deepSearch FROM node WHERE TAG = tagName
    */ 
    protected List getMultiNodeValue(Element elements, String tagName, String attrName, String deepSearch) {
        List attributeList = getMultiNodeAttribute(elements, tagName, attrName );
        List returnList  = new ArrayList<>();
        String MyDeepSearch;
        String value;

        for (Object attributeList1 : attributeList) {
            MyDeepSearch = (String) attributeList1;
            value = selectNodeValue(elements, tagName, attrName, MyDeepSearch, deepSearch);
            returnList.add(value);
        }
        return returnList;
    }
    
    
    /*
    * 
    *   getMultiNodeValue(  origins, "arrival","publicID" , "pickID")
    * 
    *   somethings like this:   SELECT tagName.deepSearch FROM node WHERE TAG = tagName
    */ 
    protected List getMultiNodeValueSel(Element elements, String tagName, String attrName, String deepSearch, String Key) {
        List attributeList = getMultiNodeAttributeSel(elements, tagName, attrName, Key );
        List returnList  = new ArrayList<>();
        String MyDeepSearch;
        String value;

        for (Object attributeList1 : attributeList) {
            MyDeepSearch = (String) attributeList1;
            value = selectNodeValue(elements, tagName, attrName, MyDeepSearch, deepSearch);
            returnList.add(value);
        }
        return returnList;
    }
    
    /*
    * getMultiNodeAttribute( Node node, String tagName, String attrName )
    *
    * List(resultset) of: SELECT (valueof)attrName FROM node WHERE TAG = tagName
    */
    protected List getMultiNodeAttributeSel( Node node, String tagName, String attrName, String Key ) {
        List nodeAttrList = new ArrayList<>();
        Element elements = (Element) node;
        NodeList myNodeList = elements.getElementsByTagName(tagName);
        Node current;
        for (int i = 0; i < myNodeList.getLength(); i++) {
            current = myNodeList.item(i);
            if(getNodeAttr(attrName,current ).equalsIgnoreCase(Key)) {
                nodeAttrList.add(getNodeAttr(attrName,current ));  
            }
        }
        return nodeAttrList;
    }
    
    
    /*
    * 
    *   getNode( tagName , nodes)
    * 
    *   somethings like this:   SELECT tagName FROM nodes WHERE TAG = tagName  LIMIT 1 
    */ 
    protected Node getNode(String tagName, NodeList nodes) {
        Node node;
        for ( int x = 0; x < nodes.getLength(); x++ ) {
            node = nodes.item(x);
            if (node.getNodeName().equalsIgnoreCase(tagName)) {
                return node;
            }
        }
        return null;
    }

    /*
    * 
    *   getNodeValue(  node)
    * 
    *   somethings like this:   SELECT valueof node FROM node   LIMIT 1 
    */ 
    protected String getNodeValue( Node node ) {
        NodeList childNodes = node.getChildNodes();
        Node data ;
        String returnString = null;
        for (int x = 0; x < childNodes.getLength(); x++ ) {
            data = childNodes.item(x);
            if ( data.getNodeType() == Node.TEXT_NODE ) {
                returnString = data.getNodeValue();
                break;
            }
        }
        return returnString;
    }

     /*
    * 
    *   getNodeValue( tagName, node)
    * 
    *   somethings like this:   SELECT tagName FROM node WHERE TAG = tagName   LIMIT 1 
    */ 
    protected String getNodeValue(String tagName, NodeList nodes ) {
        Node node ;
        NodeList childNodes;
        Node data;
        String returnString = null;
        for ( int x = 0; x < nodes.getLength(); x++ ) {
            node = nodes.item(x);
            if (node.getNodeName().equalsIgnoreCase(tagName)) {
                childNodes = node.getChildNodes();
                for (int y = 0; y < childNodes.getLength(); y++ ) {
                    data = childNodes.item(y);
                    if ( data.getNodeType() == Node.TEXT_NODE ) {
                        returnString = data.getNodeValue();
                        break;
                    }
                }
            }
        }
        return returnString;
    }
    
    /*
    *
    * getNodeDeepValue (Node node, String tagName, String tagNameDeep)
    *
    * select first value in 2° down level from node   LIMIT 1  
    <node>
        <creationInfo> tagName
            <creationTime>2014-05-29T06:48:06</creationTime> tagNameDeep
    */
    protected String getNodeDeepValue (Node node, String tagName, String tagNameDeep) {
        Node creation = getNode(tagName, node.getChildNodes());
        Element creations = (Element)creation;
        NodeList creationList = creations.getElementsByTagName(tagNameDeep);
        return getNodeValue(creationList.item(0));
    }
    
     /*
    * 
    *   getNodeAttr( tagName, node)
    * 
    *   somethings like this:   SELECT attrName FROM node WHERE ATTR = attrName   LIMIT 1 
    */ 
    protected String getNodeAttr(String attrName, Node node ) {
        NamedNodeMap attrs = node.getAttributes();
        Node attr;
        String returnString = null;
        for (int y = 0; y < attrs.getLength(); y++ ) {
            attr = attrs.item(y);
            if (attr.getNodeName().equalsIgnoreCase(attrName)) {
                returnString = attr.getNodeValue();
                break;
            }
        }
        return returnString;
    }

    /*
    * 
    *   getNodeAttr( tagName, node)
    * 
    *   somethings like this:   SELECT attrName FROM node WHERE ATTR = attrName   LIMIT 1 
    */
    protected String getNodeAttr(String tagName, String attrName, NodeList nodes ) {
        Node node;
        NodeList childNodes;
        Node data ;
        for ( int x = 0; x < nodes.getLength(); x++ ) {
            node = nodes.item(x);
            if (node.getNodeName().equalsIgnoreCase(tagName)) {
                childNodes = node.getChildNodes();
                for (int y = 0; y < childNodes.getLength(); y++ ) {
                    data = childNodes.item(y);
                    if ( data.getNodeType() == Node.ATTRIBUTE_NODE ) {
                        if ( data.getNodeName().equalsIgnoreCase(attrName) )
                            return data.getNodeValue();
                    }
                }
            }
        }

        return null;
    }
    
   
    /*
    * getMultiNodeValue( Node node, String tagName, String searchTagName  )
    *   **under construction**
    * 
    */
    protected List getMultiNodeValue( Node node, String tagName, String searchTagName  ) {
        List nodeValueList = new ArrayList<>();
        Element elements = (Element) node;
        NodeList myNodeList = elements.getElementsByTagName(tagName);
        Node current ;
        NodeList currentList;
        NodeList XcurrentList;
//        System.out.println("\n inside getNodeValueMulti tagName: "+tagName+" node: "+node+" searchTagName: "+searchTagName+"\n ");

        for (int i = 0; i < myNodeList.getLength(); i++) {

            current = myNodeList.item(i);
            currentList = current.getChildNodes();
            
            for(int j = 0; j < currentList.getLength(); j++) {
//                Node Xcurrent = currentList.item(j);
                XcurrentList = current.getChildNodes();
//                System.out.println("XcurrentList node->"+searchTagName+" : "+j+")" + getNodeValue(searchTagName,XcurrentList )+"\n");
            }
            
            nodeValueList.add(getNodeValue(searchTagName,currentList ));       
            System.out.println(" node->"+tagName+" : "+i+")" + getNodeValue(searchTagName,currentList )+"\n");

        }
        return nodeValueList;
    }    
    
    
    /*
    * selectNodeValue(  node,  tagName,  attribute,  searchAttr,  searchTagName)
    *
    * somethings like this:   SELECT searchTagName FROM node WHERE TAG = tagName AND attribute = searchAttr AND valueOfSearchAttr = valueOfAttr   LIMIT 1 
    *
    selectNodeAttrValue(  events,  "pick",  "publicID",  "publicID",  "publicID",  MyDeepSearch);
    */
    protected String selectNodeAttrValue( Node node, String tagName, String attribute, String searchAttr, String searchTagName, String valueOfAttr) {
        String result = null;
        Element elements = (Element) node;
        Node current;
        String currentAttribute;
        NodeList myNodeList = elements.getElementsByTagName(tagName);
        for (int i = 0; i < myNodeList.getLength(); i++) {
            current = myNodeList.item(i);
            currentAttribute = getNodeAttr(attribute,current);
            if (currentAttribute.equalsIgnoreCase(searchAttr)) {
                //getNodeAttr(String attrName, Node node )
                System.out.println("\n inside selectNodeAttrValue "+searchAttr+" "+current+" == "+getNodeAttr(searchAttr,current)+"\n");
                
                result = getNodeValue(searchTagName, current.getChildNodes());
            }
        }
        return result;
    }
    
    /*
    *   extendGetNodeValue
    *
    */
    protected String extendGetNodeValue(Element elements, String tag, String attr, String deepAttr, String deepVal) {
        List elementList = getMultiNodeAttribute(  elements, tag, attr );
        String currentElement;
        String returnString = null;
        for(int k = 0; k < elementList.size(); k++) {
            System.out.println("lista current value : "+k+") tag=" + elementList.get(k)+"\n");
            
            currentElement = (String)elementList.get(k);
            
            returnString = selectNodeValue(  elements, tag, attr, currentElement, deepAttr);
        
            if(returnString.equalsIgnoreCase(deepVal)) {
                return returnString;
            }
            
            System.out.println("current deepval : " + returnString+"\n");
            
        }
        return returnString;
    }

}
