import javax.swing.JOptionPane;
import java.io.*;
import java.net.InetAddress;
import java.util.ArrayList;
import javax.swing.DefaultListModel;
import javax.swing.ListModel;

/**
 *
 * @author William
 */
public class Client {

    private StreamExtender streamSocket;
    private String message;
    private String serverName;
    private SystemInformation sysinfo;

    public Client() {
        connect();
    }

    public boolean connect() {
        try {
            sysinfo = new SystemInformation("systeminfo.xml");
            streamSocket = new StreamExtender(InetAddress.getByName(sysinfo.getAddress()), sysinfo.getPort());
            serverName = streamSocket.receiveMessage();
            System.out.println(serverName);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        }
        return false;
    }

    public boolean createNew(String txtUserName) {
        try {
            
            streamSocket.sendMessage("101"); // inform server that wish to add new user
            message = streamSocket.receiveMessage();
            if (message.contains("102")) // ok to send new user details
            {
                streamSocket.sendMessage(txtUserName);
            }
            else
            {
                JOptionPane.showMessageDialog(null, "Something went wrong");
            }
            message = streamSocket.receiveMessage();
            if (message.contains("103")) {
                JOptionPane.showMessageDialog(null, "User successfully added");
                return true;
            } else if (message.contains("104")) {
                JOptionPane.showMessageDialog(null, "That user already exists!");
            } else {
                JOptionPane.showMessageDialog(null, "Something went wrong");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return false;
    }

    public boolean login(String txtUserName) {
        try {
            // Send user name to server
            streamSocket.sendMessage("105"); // inform server that wish to login
            message = streamSocket.receiveMessage();
            if (message.contains("102")) // ok to send new user details
            {
                streamSocket.sendMessage(txtUserName);
            }
            message = streamSocket.receiveMessage();
            if (message.contains("106")) {
                JOptionPane.showMessageDialog(null, "User logged in");
                return true;
            } else if (message.contains("107")) {
                JOptionPane.showMessageDialog(null, "User details do not exist on server"
                        + "try creating a account");
            } else {
                JOptionPane.showMessageDialog(null, "Something went wrong");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return false;
    }

    public void logout() {
        try {
            if (!streamSocket.isClosed()) {
                streamSocket.sendMessage("300");
                message = streamSocket.receiveMessage();
                System.out.println(message);
                if (message.contains("301")) // acknowledge close request
                {
                    streamSocket.close();
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void upload(File file) {
        try {
            streamSocket.sendMessage("202");
            message = streamSocket.receiveMessage();
            System.out.println(message);
            if (message.contains("203")) {
                streamSocket.sendMessage(file.getName() + ";" + (int) file.length());
                System.out.println(file.getName());
                System.out.println((int) file.length());


                message = streamSocket.receiveMessage();
                System.out.println(message);
                if (message.contains("204")) {

                    boolean result = streamSocket.SendFile(file);
                    System.out.println(result);
                    message = streamSocket.receiveMessage();
                    if (message.contains("205")) {
                        JOptionPane.showMessageDialog(null, "Upload successful");
                    }
                }
            } else {
                JOptionPane.showMessageDialog(null, "oh fudge it, error in upload");
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }


    }

    public boolean download(File file) {
        
        try {            
            int fileSize = 0;
            
            streamSocket.sendMessage("207");  // requesting to download a file
            message = streamSocket.receiveMessage(); // wait for server to acknowledge
            //System.out.println(message);
            if (message.contains("208")) {
                // server acknowledges, send file name
                streamSocket.sendMessage(file.getName());
            }

            message = streamSocket.receiveMessage();

            if (message.contains("213")) {
                // file exists on server
                streamSocket.sendMessage("209"); // request file size
            }

            fileSize = 
                    Integer.parseInt(streamSocket.receiveMessage()); // the file size 
            
            streamSocket.sendMessage("210"); // OK GO!!!
            // This is it, file inbound
            
            streamSocket.recieveFile(file, fileSize);          

            
            return true;

    }
    catch (IOException ex)
    {
            ex.printStackTrace();
            System.out.println("feck");
    }
        return false;
}
public DefaultListModel<String> fetchDirectoryListing() {
        DefaultListModel<String> list = new DefaultListModel<String>();
        try {
            streamSocket.sendMessage("200");
            message = streamSocket.receiveMessage();
            //System.out.println(message);
            if (message.contains("201")) // receiving
            {
                message = "";
                message = streamSocket.receiveMessage();

                String[] array = message.split(";");
                for (String file : array) {
                    list.addElement(file);
                }
            } else if (message.contains("202")) {
                JOptionPane.showMessageDialog(null, "No directory info found");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return list;
    }

    public void exit() {
        if (streamSocket.isClosed())
        {
            System.exit(0);
        }
        else
        {
            logout();
            System.exit(0);
        }
       
    }
}
