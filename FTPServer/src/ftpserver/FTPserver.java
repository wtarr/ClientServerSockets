
import java.io.*;
import java.net.ServerSocket;
import java.util.ArrayList;

public final class FTPserver {

    public static String names = "";
    public static ArrayList<Directory> serversDirectoryListing;
    private String servername;
    private int serverPort;
    private File root;
    private String defaultServername = "FTP Server";
    private int defaultPort = 12000;

    public FTPserver() {
        servername = defaultServername;
        serverPort = defaultPort;
        initialiseServer();
    }

    public void initialiseServer() {

        try {
            serversDirectoryListing = new ArrayList<>();
            InputStreamReader converter = new InputStreamReader(System.in);
            BufferedReader in = new BufferedReader(converter);
            
            System.out.println("----------------------------\n"
                    + "---------FTP Server---------\n"
                    + "----------------------------\n\n");

            System.out.println("Beginning Initial setup.....\n\n");

            System.out.println("Enter a name for the server: (hit return for default)");
            String input = in.readLine();
            if (!input.equals(""))
                servername = input;
                        
            System.out.println("Enter the port number: (hit return for default (12000))");            
            input = in.readLine();
            if (!input.equals(""))
                serverPort = Integer.parseInt(input);
            
            // Check for root folder, else create one.
            root = new File("ROOT");
            System.out.println("Checking if ROOT directory is present");
            if (!root.exists()) {
                boolean mkdir = root.mkdir();

                if (mkdir) {
                    System.out.println("ROOT not found so new ROOT created");
                }
            } else {
                System.out.println("ROOT found");
            }

            buildDirectoryListing();


            ServerSocket myServerSocket = new ServerSocket(serverPort);

            System.out.println("Server " + servername + " is ready");

            while (true) {
                System.out.println("Waiting for connections");

                StreamExtender myStreamSocket = new StreamExtender(
                        myServerSocket.accept());

                System.out.println("Connection accepted");
                // Start a new thread
                Thread thread = new Thread(new FTPServerThread(myStreamSocket,
                        servername, root));

                thread.start();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public synchronized void buildDirectoryListing() {
        
        FTPserver.serversDirectoryListing.clear();
        
        for (File sub : root.listFiles())
        {          
            Directory d = new Directory();
            d.setOwner(sub.getName());
            for (File f : sub.listFiles())
            {
                d.dirListing.add(f.getName());
            }            
            FTPserver.serversDirectoryListing.add(d);            
        }
                
    }
    
    public static void main(String[] args) throws IOException {

        FTPserver ftp = new FTPserver();
    }
    
    
}