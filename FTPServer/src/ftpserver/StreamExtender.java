
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;

/**
 *
 * @author William
 */
public class StreamExtender extends MyStreamSocket {

    //private File file;
    byte[] byteArr;
    FileInputStream fis;
    BufferedInputStream bis;
    OutputStream os;

    public StreamExtender(InetAddress acceptorHost, int acceptorPort) throws SocketException, IOException {
        super(acceptorHost, acceptorPort);
    }

    public StreamExtender(Socket socket) throws IOException {
        super(socket);
    }

    public boolean recieveFile(File dir, String file, int size) { // use this receicve
        try {    
           
            File f = new File(dir, file);
            //f.createNewFile();
            
            FileOutputStream fos = new FileOutputStream(f);
            byte[] byteme = new byte[1024];
            int count = 0;
            int total = 0;
            while ((count = inStream.read(byteme))!= -1)
            {
                fos.write(byteme, 0, count);
                System.out.println("Recieved " + count);
                total += count;
                System.out.println("total " + total);
                if (total == size)    
                    break;                    
            }
            fos.flush();
            fos.close();   
            System.out.println("closing fos");
            System.out.println("It is done");
            return true;

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public boolean SendFile(File file) { // use this send              
        try {
            outStream.flush();
            System.out.println("sending...");
            byte[] byteme = new byte[1024];
           
            FileInputStream fis = new FileInputStream(file);
            int count = 0;
            while (-1 != (count = fis.read(byteme, 0, byteme.length)))
            {
                outStream.write(byteme, 0, count);
                System.out.println("Sent " + count);
                //break;
                outStream.flush();
            }
            //socket.shut();
            //socket.sh
            fis.close();
            System.out.println(".... Sent");
            return true;
        } catch (IOException ex) {
            System.out.println("Sending failed");
        }
        return false;
    }
}
