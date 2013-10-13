/**
 * @author William
 * 
 * Modified version from - http://www.mkyong.com/java/how-to-read-xml-file-in-java-dom-parser/
 * 
 * This will "hide" but also allow the address and port details be updated if necessary
 */
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.DOMException;
import org.xml.sax.SAXException;


public class SystemInformation {
    
    private String address;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
    private int port;
        
    public SystemInformation(String filename) {
        
        FetchDetailsFromXML(filename);
    }
    
    private void FetchDetailsFromXML(String file)
    {
        try 
        {
            File fXMLfile = new File(file);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXMLfile);
            
            doc.getDocumentElement().normalize();
            
            Node node = doc.getElementsByTagName("userinfo").item(0);
            
            if (node.getNodeType() == Node.ELEMENT_NODE)
            {
                Element element = (Element)node;                
                setAddress(element.getElementsByTagName("address").item(0).getTextContent());
                setPort(Integer.parseInt(element.getElementsByTagName("port").item(0).getTextContent()));
            }
        } 
        catch (ParserConfigurationException | SAXException | IOException | DOMException e) 
        {
            e.printStackTrace();
        }
    }
    
}
