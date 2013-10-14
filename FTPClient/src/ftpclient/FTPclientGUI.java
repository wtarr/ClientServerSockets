import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class FTPclientGUI extends JFrame implements ActionListener, WindowListener {

    private JPanel panel_main, panel_new, panel_Existing, cardPanel;
    private CardLayout cardLayout;
    private JMenu fileMenu;
    private Toolkit toolkit;
    private JLabel lblname, lblSelectedDir;
    private JTextField txtUserName, txtNewUserName, txtSelectedDir;
    private JButton 
            btnNew, btnJoin, btnCancelNewUser,
            btnExisting, btnLogin, btnCancelLogin,
            btnSend,
            btnClose,
            btnUpload,
            btnDownload;
    private String[] directories, files;
    private JList dirList, filesList;
    private String message, serverName = "";
    private Container cPane; 
    private BorderLayout layout;
    private Client client;


    public FTPclientGUI() {
        
        client = new Client();
        
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

        
        //cardPanel.add(panel_Existing, "2");
        initializeNewUser();
        initializeLoginPanel();
        initializeDirectoryPanel();

        cPane.add(cardPanel);
    }

    private void initializeDirectoryPanel() {

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

        cardPanel.add(panel_Existing, "4");
    }

    private void InitializeMainGUIpanel() {

        /// MAIN PANEL !!!!
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
        cardPanel.add(panel_main, "1");
    }

    private void initializeNewUser()
    {
        /// NEW USERS !!!!!
        panel_new = new JPanel(new GridLayout(2, 1, 5, 40));
        panel_new.setBorder(BorderFactory.createEmptyBorder(40, 5, 40, 5));
        lblname = new JLabel("User Name: ");
        txtNewUserName = new JTextField(30);


        btnJoin = new JButton("Join");
        btnJoin.addActionListener(this);
        btnCancelNewUser = new JButton("Cancel");
        btnCancelNewUser.addActionListener(this);

        panel_new.add(lblname);
        panel_new.add(txtNewUserName);
        panel_new.add(btnJoin);
        panel_new.add(btnCancelNewUser);

        cardPanel.add(panel_new, "3");
    }
    
    private void initializeLoginPanel()
    {
        ///EXISTING USERS !!!!
        panel_Existing = new JPanel(new GridLayout(2, 1, 5, 40));
        panel_Existing.setBorder(BorderFactory.createEmptyBorder(40, 5, 40, 5));
        lblname = new JLabel("User Name: ");
        txtUserName = new JTextField(30);


        btnLogin = new JButton("Join");
        btnLogin.addActionListener(this);
        btnCancelLogin = new JButton("Cancel");
        btnCancelLogin.addActionListener(this);

        panel_Existing.add(lblname);
        panel_Existing.add(txtUserName);
        panel_Existing.add(btnLogin);
        panel_Existing.add(btnCancelLogin);

        cardPanel.add(panel_Existing, "2");
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
    
    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        String actionName;

        actionName = actionEvent.getActionCommand();        

            if (actionName.equals("EXIT")) {
                client.exit();
            }
            else if (actionEvent.getSource().equals(btnNew))
            {
                // Initialize new User
                cardLayout.show(cardPanel, "3");
            }
            else if (actionEvent.getSource().equals(btnExisting))
            {
                // Initialize Existing
                cardLayout.show(cardPanel, "2"); // show login panel

            }
            else if (actionEvent.getSource().equals(btnJoin))
            {                
                boolean ok = client.createNew(txtNewUserName.getText());
                txtUserName.setText("");
                if (ok)
                    cardLayout.show(cardPanel, "4");
            }
            else if (actionEvent.getSource().equals(btnCancelNewUser) ||
                    actionEvent.getSource().equals(btnCancelLogin) )
            {
                // return to main
                cardLayout.show(cardPanel, "1");
            }
            else if (actionEvent.getSource().equals(btnLogin))
            {
                boolean ok = client.login();
                txtUserName.setText("");
                if (ok)
                    cardLayout.show(cardPanel, "4");
            }
            else if (actionEvent.getSource() == btnSend) {
                /*String name2Send = txtUserName.getText();
                streamSocket.sendMessage("101 " + name2Send);
                message = streamSocket.receiveMessage();
                System.out.println(message);*/
            } else if (actionEvent.getSource() == btnClose) {
                client.exit();
            }
        
    }

    @Override
    public void windowOpened(WindowEvent windowEvent) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void windowClosing(WindowEvent windowEvent) {
        //To change body of implemented methods use File | Settings | File Templates.
        client.exit();
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
    
    public static void main(String[] args) {


        FTPclientGUI f = new FTPclientGUI();
        f.setVisible(true);

    }
}
