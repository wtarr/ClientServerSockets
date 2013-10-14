import java.io.File;
import java.util.ArrayList;


public class FTPServerThread implements Runnable {

    private String serverName;
    private MyStreamSocket myStreamSocket;    

    public FTPServerThread(MyStreamSocket myStreamSocket, String servername) {
        
        this.myStreamSocket = myStreamSocket;
        this.serverName = servername;
    }

    public void run() {
        boolean session = true;
        String message;

        try {

            myStreamSocket.sendMessage("Now connected to " + serverName);

            while (session) {

                message = myStreamSocket.receiveMessage();

                if (message.contains("101")) // add
                {
                    myStreamSocket.sendMessage("102");  // tell client next message is to be user name
                    message = myStreamSocket.receiveMessage(); // this is the user name                  
                    
                    myStreamSocket.sendMessage(createDirectory(message)); // tell the client if all went ok
                }

                /*if (message.contains("102")) // send
                 {
                 System.out.println("List sent");
                 myStreamSocket.sendMessage(FTPserver.names);
                 }*/

                if (message.contains("300")) // close
                {
                    myStreamSocket.sendMessage("301 Ending session ...");
                    session = false;
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
    
    public String createDirectory(String name)
    {
        // check if exists??
        // else
        Directory d = new Directory();
        d.setOwner(name);
        File root = new File("ROOT");        
        File combined = new File(root, name);
        boolean created = combined.mkdir();
        FTPserver.serversDirectoryListing.add(d);
        System.out.println(name + "- directory created for new user");
        return "103";
        
    }
} // class