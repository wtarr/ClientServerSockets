

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.InetAddress;

public class FTPclient extends JFrame implements ActionListener, WindowListener {

    private JPanel panel_main, panel_new, panel_existing, panel_Existing, cardPanel;
    private CardLayout cardLayout;
    private JMenu fileMenu;
    private Toolkit toolkit;
    private JLabel lblname, lblSelectedDir;
    private JTextField txtUserName, txtSelectedDir;
    private JButton btnNew,
            btnSendNewUser,
            btnCancelNewUser,
            btnExisting,
            btnSend,
            btnClose,
            btnUpload,
            btnDownload;
    private String[] directories, files;
    private JList dirList, filesList;
    private String message, serverName = "";
    private Container cPane;
    private int portNumber = 12000;

    private BorderLayout layout;

    private MyStreamSocket streamSocket;


    public FTPclient() {

        SystemInformation sysinfo = new SystemInformation("systeminfo.xml");
        
        
        
        try {
            streamSocket = new MyStreamSocket(InetAddress.getByName(sysinfo.getAddress()), portNumber);
            serverName = streamSocket.receiveMessage();
            System.out.println(serverName);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        }

        setTitle("FTP - Client");
        setSize(400, 300);
        setResizable(false);
        addWindowListener(this);

        directories = new String[]{"Test1", "Test2"};
        files = new String[]{"File1, File2"};

        cardLayout = new CardLayout();

        cardPanel = new JPanel(cardLayout);

        toolkit = this.getToolkit();
        Dimension size = toolkit.getScreenSize();
        setLocation((size.width - getWidth()) / 2, (size.height - getHeight()) / 2);

        setDefaultCloseOperation(EXIT_ON_CLOSE);

        cPane = getContentPane();

        InitializeMainGUIpanel();




        JMenuBar menuBar = new JMenuBar();
        createFileMenu();
        setJMenuBar(menuBar);
        menuBar.add(fileMenu);

        cardPanel.add(panel_main, "1");
        //cardPanel.add(panel_Existing, "2");
        InitializeNewUser();
        InitializeDirectoryPanel();

        cPane.add(cardPanel);
    }

    private void InitializeDirectoryPanel() {

        panel_Existing = new JPanel(new GridLayout(2, 1, 5, 5));
        panel_Existing.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // http://docs.oracle.com/javase/tutorial/uiswing/components/list.html
        dirList = new JList(directories);
        dirList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        dirList.setLayoutOrientation(JList.VERTICAL);
        dirList.setVisibleRowCount(-1);

        JScrollPane listScroller = new JScrollPane(dirList);
        listScroller.setPreferredSize(new Dimension(200, 90));

        panel_Existing.add(dirList);

        JPanel temp = new JPanel(new GridLayout(2, 2, 5, 5));

        lblSelectedDir = new JLabel("File");
        txtSelectedDir = new JTextField(30);
        btnUpload = new JButton("Upload");
        btnUpload.addActionListener(this);
        btnDownload = new JButton("Download");
        btnDownload.addActionListener(this);

        temp.add(lblSelectedDir);
        temp.add(txtSelectedDir);
        temp.add(btnUpload);
        temp.add(btnDownload);

        panel_Existing.add(temp);

        cardPanel.add(panel_Existing, "3");
    }

    private void InitializeMainGUIpanel() {

        panel_main = new JPanel(new GridLayout(2, 1, 5, 5));
        panel_main.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JPanel temp = new JPanel(new GridLayout(1, 1, 5, 5));
        lblname = new JLabel(serverName);
        temp.add(lblname);
        panel_main.add(temp);

        temp = new JPanel(new GridLayout(1, 2, 5, 5));
        temp.setBorder(BorderFactory.createEmptyBorder(30, 5, 30, 5));   // t l b r
        btnNew = new JButton("I am a new User");
        btnNew.addActionListener(this);
        btnExisting = new JButton("I am a Existing User");
        btnExisting.addActionListener(this);
        temp.add(btnNew);
        temp.add(btnExisting);
        panel_main.add(temp);

    }

    private void InitializeNewUser()
    {
        panel_new = new JPanel(new GridLayout(2, 1, 5, 40));
        panel_new.setBorder(BorderFactory.createEmptyBorder(40, 5, 40, 5));
        lblname = new JLabel("User Name: ");
        txtUserName = new JTextField(30);


        btnSendNewUser = new JButton("Join");
        btnSendNewUser.addActionListener(this);
        btnCancelNewUser = new JButton("Cancel");
        btnCancelNewUser.addActionListener(this);

        panel_new.add(lblname);
        panel_new.add(txtUserName);
        panel_new.add(btnSendNewUser);
        panel_new.add(btnCancelNewUser);

        cardPanel.add(panel_new, "2");
    }

    private void createFileMenu() {
        JMenuItem item;
        fileMenu = new JMenu("File");
        item = new JMenuItem("EXIT");
        item.addActionListener(this);
        fileMenu.add(item);
        item = new JMenuItem("Log off");
        item.addActionListener(this);
        fileMenu.add(item);
    }


    public static void main(String[] args) {


        FTPclient f = new FTPclient();
        f.setVisible(true);

    }


    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        String actionName;

        actionName = actionEvent.getActionCommand();

        try {

            if (actionName.equals("EXIT")) {
                streamSocket.sendMessage("200");
                message = streamSocket.receiveMessage();
                System.out.println(message);
                if (message.contains("200"))   // acknowledge
                {
                    streamSocket.close();
                    System.exit(0);
                }
            }
            else if (actionEvent.getSource().equals(btnNew))
            {
                // Initialize new User
                cardLayout.show(cardPanel, "2");
            }
            else if (actionEvent.getSource().equals(btnExisting))
            {
                // Initialize Existing
                cardLayout.show(cardPanel, "3");

            }
            else if (actionEvent.getSource().equals(btnSendNewUser))
            {
                // Send user name to server
                streamSocket.sendMessage("101"); // inform server that wish to add new user
                message = streamSocket.receiveMessage();
                if (message.contains("102")) // ok to send new user details
                {
                    streamSocket.sendMessage(txtUserName.getText());
                }
                message = streamSocket.receiveMessage();
                if (message.contains("103"))
                {
                    JOptionPane.showMessageDialog(null, "User successfully added");
                }
                else
                {
                    JOptionPane.showMessageDialog(null, "Something went wrong");
                }

                cardLayout.show(cardPanel, "3");
            }
            else if (actionEvent.getSource().equals(btnCancelNewUser))
            {
                // return to main
                cardLayout.show(cardPanel, "1");
            }
            else if (actionEvent.getSource() == btnSend) {
                /*String name2Send = txtUserName.getText();
                streamSocket.sendMessage("101 " + name2Send);
                message = streamSocket.receiveMessage();
                System.out.println(message);*/
            } else if (actionEvent.getSource() == btnClose) {
                streamSocket.sendMessage("200");
                message = streamSocket.receiveMessage();
                System.out.println(message);
                if (message.contains("200")) // acknowledge close request
                {
                    streamSocket.close();
                    System.exit(0);
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void windowOpened(WindowEvent windowEvent) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void windowClosing(WindowEvent windowEvent) {
        //To change body of implemented methods use File | Settings | File Templates.
        try {
            streamSocket.sendMessage("200");
            message = streamSocket.receiveMessage();
            System.out.println(message);
            if (message.contains("200"))   // acknowledge
            {
                streamSocket.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void windowClosed(WindowEvent windowEvent) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void windowIconified(WindowEvent windowEvent) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void windowDeiconified(WindowEvent windowEvent) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void windowActivated(WindowEvent windowEvent) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void windowDeactivated(WindowEvent windowEvent) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
