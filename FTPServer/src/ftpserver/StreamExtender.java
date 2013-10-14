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
    
    public void recieveFile(String dir, String filename) throws FileNotFoundException, IOException
    {
        File file = new File(dir, filename);
        FileOutputStream fos = new FileOutputStream(file);
        byte[] barray = new byte[1024*1024];
        int count = 0;
        while ((count = inStream.read(barray)) >= 0)
        {
            fos.write(byteArr, 0, count);
            break;
        }        
    }

    public void SendFile(File file) throws FileNotFoundException, IOException {
       byteArr = new byte [(int) file.length()];
       fis = new FileInputStream(file);
       bis = new BufferedInputStream(fis);
       bis.read(byteArr, 0, byteArr.length);      
       outStream.write(byteArr, 0, byteArr.length);
       outStream.flush();
       System.out.println("Sent");        
    }
}
