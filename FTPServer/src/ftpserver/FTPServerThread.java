public class FTPServerThread implements Runnable {
	
	String serverName;
    MyStreamSocket myStreamSocket;

	public FTPServerThread(MyStreamSocket myStreamSocket, String servername)
	{
		this.myStreamSocket = myStreamSocket;
        this.serverName = servername;
	}

	public void run()
	{
		boolean session = true;
		String message;

        try {

            myStreamSocket.sendMessage("Welcome to " + serverName);

			while(session)
			{

				message = myStreamSocket.receiveMessage();

				if (message.contains("101")) // add
				{
                    myStreamSocket.sendMessage("102");  // tell client next message is to be user name
                    message = myStreamSocket.receiveMessage();
                    addName(message);
                    System.out.println(message + " added to list");
                    myStreamSocket.sendMessage("103"); // tell the client all went ok
                }

                /*if (message.contains("102")) // send
                {
                    System.out.println("List sent");
                    myStreamSocket.sendMessage(FTPserver.names);
                }*/

                if (message.contains("200")) // close
                {
                    myStreamSocket.sendMessage("200 Ending session ...");
                    session = false;
                }

			}

            myStreamSocket.close();
            System.out.println("Session ended");

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

	}

	public void addName(String name)
	{
		FTPserver.names = FTPserver.names + name + " ";
	}

} // class