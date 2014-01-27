
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

    private static Client uniqueInstance;
    private StreamExtender streamSocket;
    private String message;
    private String serverName;
    private SystemInformation sysinfo;

    private Client() {
        connect();
    }

    public static Client getInstance() {
        if (uniqueInstance == null) {
            uniqueInstance = new Client();
        }

        return uniqueInstance;
    }

    public boolean connect() {
        try {
            try {
                //sysinfo = new SystemInformation(new File(getClass().getResource("./config/systeminfo.xml")));
                File f = new File(getClass().getResource("config/systeminfo.xml").toURI());
                sysinfo = new SystemInformation(f);
                streamSocket = new StreamExtender(InetAddress.getByName(sysinfo.getAddress()), sysinfo.getPort());
            } catch (Exception ex) {
                // Cant read file so default to localhost and port 12000
                streamSocket = new StreamExtender(InetAddress.getByName("localhost"), 12000);
            }
            serverName = streamSocket.receiveMessage();
            JOptionPane.showMessageDialog(null, serverName);
            //System.out.println(serverName);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        }
        
        return false;
    }

    public boolean createNew(String txtUserName) {
        try {

            boolean wasSuccessful = false;
            streamSocket.sendMessage("101"); // inform server that wish to add new user

            message = streamSocket.receiveMessage();

            if (message.contains("102")) // ok to send new user details
            {
                streamSocket.sendMessage(txtUserName);
            } else {
                JOptionPane.showMessageDialog(null, "Something went wrong, expected 102 response from server");
            }

            message = streamSocket.receiveMessage();

            if (message.contains("103")) {
                JOptionPane.showMessageDialog(null, "User successfully added");
                wasSuccessful = true;
            } else if (message.contains("104")) {
                JOptionPane.showMessageDialog(null, "That user already exists!");
            } else {
                JOptionPane.showMessageDialog(null, "Something went wrong, expected 103 success or 104 failed response");
            }

            return wasSuccessful;

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
            } else {
                JOptionPane.showMessageDialog(null, "Something went wrong, expected 102 response from server");
                return false;
            }
            message = streamSocket.receiveMessage();
            if (message.contains("106")) {
                JOptionPane.showMessageDialog(null, "User logged in");
                return true;
            } else if (message.contains("107")) {
                JOptionPane.showMessageDialog(null, "User details do not exist on server"
                        + "try creating a account");
            } else {
                JOptionPane.showMessageDialog(null, "Something went wrong, expected 106 or 107 response from server");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return false;
    }

    public boolean logout() {
        boolean wasSuccessful = false;
        try {
            if (!streamSocket.isClosed()) {
                streamSocket.sendMessage("300");
                message = streamSocket.receiveMessage();
                System.out.println(message);
                if (message.contains("301")) // acknowledge close request
                {
                    streamSocket.close();
                    wasSuccessful = true;
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return wasSuccessful;
    }

    public void upload(File file) {
        try {
            streamSocket.sendMessage("202");
            message = streamSocket.receiveMessage();
            System.out.println(message);
            if (message.contains("203")) {
                streamSocket.sendMessage(file.getName() + ";" + (int) file.length());

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
                JOptionPane.showMessageDialog(null, "There was an error in uploading the file");
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


                fileSize =
                        Integer.parseInt(streamSocket.receiveMessage()); // the file size 

                streamSocket.sendMessage("210"); // OK GO!!!
                // This is it, file inbound

                streamSocket.recieveFile(file, fileSize);


                return true;
            }

            return false;

        } catch (IOException ex) {
            ex.printStackTrace();
            System.out.println("An issue occured while downloading");
        }
        return false;
    }

    public DefaultListModel<String> fetchDirectoryListing() {
        DefaultListModel<String> list = new DefaultListModel<String>();
        try {
            streamSocket.sendMessage("200"); // request directory listing
            message = streamSocket.receiveMessage();
            //System.out.println(message);
            if (message.contains("201")) // receieving listing in next transmission
            {
                message = "";
                message = streamSocket.receiveMessage();

                String[] array = message.split(";");
                for (String file : array) {
                    list.addElement(file);
                }
            } else {
                JOptionPane.showMessageDialog(null, "No directory info found");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return list;
    }

    public void exit() {
        if (streamSocket.isClosed()) {
            System.exit(0);
        } else {
            logout();
            System.exit(0);
        }

    }
}
