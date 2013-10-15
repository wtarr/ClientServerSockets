
import java.io.*;
import java.security.acl.Owner;
import java.util.ArrayList;

public class FTPServerThread implements Runnable {

    private String serverName;
    private StreamExtender myStreamSocket;
    private String message;
    private Boolean session;    
    private Directory currentUsersDirectory;

    public FTPServerThread(StreamExtender myStreamSocket, String servername) {

        this.myStreamSocket = myStreamSocket;
        this.serverName = servername;
    }

    public void run() {
        session = true;

        try {

            myStreamSocket.sendMessage("Now connected to " + serverName);

            while (session) {

                message = myStreamSocket.receiveMessage();

                switch (message) {
                    case "101":
                        addNewUser();
                        break;
                    case "105":
                        login();
                        break;
                    case "200":
                        sendDirectoryListingForCurrentUser();
                        break;
                    case "202":
                        receiveFile();
                        break;                     
                    case "300":
                        closeConnection();
                        break;
                }

            }

            myStreamSocket.close();
            System.out.println("Session ended");

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public void addName(String name) {
        FTPserver.names = FTPserver.names + name + " ";
    }

    public void addNewUser() {
        try {
            myStreamSocket.sendMessage("102");  // tell client next message is to be user name
            message = myStreamSocket.receiveMessage(); // this is the user name 
            myStreamSocket.sendMessage(createDirectory(message)); // tell the client if all went ok
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void closeConnection() {
        try {
            myStreamSocket.sendMessage("301 Ending session ...");
            session = false;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void login() {
        String signalToReturn = "107";

        try {
            myStreamSocket.sendMessage("102");  // tell client next message is to be user name
            message = myStreamSocket.receiveMessage(); // this is the user name 

            for (Directory d : FTPserver.serversDirectoryListing) {
                if (d.getOwner().toLowerCase().equals(message.toLowerCase())) {
                    //curer = message;
                    currentUsersDirectory = d;
                    signalToReturn = "106"; // login successful
                    System.out.println(d.getOwner() + " is connected");
                }
            }
            
            myStreamSocket.sendMessage(signalToReturn); // tell the client if all went ok

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    private void logoff()
    {
        
    }
    
    private void sendDirectoryListingForCurrentUser()
    {
        try {
            myStreamSocket.sendMessage("201");  // tell client to expect file listing in next transmission
            message = ""; 

            for (String f : currentUsersDirectory.dirListing) {                
                message += f + ";"; 
            }            
            myStreamSocket.sendMessage(message); // tell the client if all went ok
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private String createDirectory(String name) {
        // check if exists??
        for (Directory d : FTPserver.serversDirectoryListing) {
            if (d.getOwner().toLowerCase().equals(name.toLowerCase())) {
                return "104";
            }
        }

        // else
        Directory d = new Directory();
        d.setOwner(name.toLowerCase());
        File root = new File("ROOT");
        File combined = new File(root, name);
        boolean created = combined.mkdir();
        FTPserver.serversDirectoryListing.add(d);
        System.out.println(name + "- directory created for new user");
        //currentUser = name;
        return "103";

    }
    
    private void receiveFile()
    {
        try {
            myStreamSocket.sendMessage("203");  // tell client send file name
            message = myStreamSocket.receiveMessage(); // this is the file name and size 

            String name = message.split(";")[0];
            int size = Integer.parseInt(message.split(";")[1]);
            System.out.println("size" + size);
            File f = new File("ROOT", currentUsersDirectory.getOwner());
            
            //System.out.println(message);
            myStreamSocket.sendMessage("204"); // send the file, im a waiting
            boolean success = myStreamSocket.recieveFile(f, name, size);
            
            if (success)
                myStreamSocket.sendMessage("205");
            else
                myStreamSocket.sendMessage("206");
            

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
} // class