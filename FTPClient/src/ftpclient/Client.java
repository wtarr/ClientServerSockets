
import javax.swing.JOptionPane;
import java.io.*;
import java.net.InetAddress;

/**
 *
 * @author William
 */
public class Client {

    private MyStreamSocket streamSocket;
    private String message;
    private String serverName;
    private SystemInformation sysinfo;    
    
    
    public Client()
    {
        try {
            sysinfo = new SystemInformation("systeminfo.xml");
            streamSocket = new MyStreamSocket(InetAddress.getByName(sysinfo.getAddress()), sysinfo.getPort());
            serverName = streamSocket.receiveMessage();
            System.out.println(serverName);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        }
    }

    public boolean createNew(String txtUserName) {
        try {
            // Send user name to server
            streamSocket.sendMessage("101"); // inform server that wish to add new user
            message = streamSocket.receiveMessage();
            if (message.contains("102")) // ok to send new user details
            {
                streamSocket.sendMessage(txtUserName);
            }
            message = streamSocket.receiveMessage();
            if (message.contains("103")) {
                JOptionPane.showMessageDialog(null, "User successfully added");
                return true;
            } 
            else if (message.contains("104"))
            {
                JOptionPane.showMessageDialog(null, "That user already exists!");                
            }
            else
            {
                JOptionPane.showMessageDialog(null, "Something went wrong");                
            }
        } catch (IOException ex) 
        {
            ex.printStackTrace();
        }
        
        return false;
    }

    
    
    public boolean login() {
        return false;
    }

    public void logout() {
    }

    public void upload() {
    }

    public void download() {
    }

    public void fetchDirectoryListing() {
    }

    public void exit() {
        try {
            streamSocket.sendMessage("300");
            message = streamSocket.receiveMessage();
            System.out.println(message);
            if (message.contains("301")) // acknowledge close request
            {
                streamSocket.close();
                System.exit(0);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
