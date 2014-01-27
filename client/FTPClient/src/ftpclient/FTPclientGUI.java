import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class FTPclientGUI extends JFrame implements ActionListener, WindowListener {

    private JPanel panel_main,                                  
            cardPanel;
    private DirectoryPanel directoryPanel;
    private LoginPanel loginPanel;
    private JoinPanel joinPanel;            
    private CardLayout cardLayout;
    private JMenu fileMenu;
    private Toolkit toolkit;
    private JLabel lblname;    
    private JButton btnNew, btnExisting;   
    private String serverName = "";
    private Container cPane;
    private Client client;

    public FTPclientGUI() {
        client = Client.getInstance();
        setTitle("FTP - Client");
        setSize(400, 300);
        setResizable(false);
        addWindowListener(this);       
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
        directoryPanel = new DirectoryPanel();
        loginPanel = new LoginPanel(cardLayout, cardPanel, directoryPanel); 
        joinPanel = new JoinPanel(cardLayout, cardPanel); 
        cardPanel.add(loginPanel, "2");
        cardPanel.add(joinPanel, "3");
        cardPanel.add(directoryPanel, "4");
        cPane.add(cardPanel);
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
    
    private void createFileMenu() {
        JMenuItem item;
        fileMenu = new JMenu("File");
        item = new JMenuItem("Log off");
        item.addActionListener(this);
        fileMenu.add(item);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        String actionName;

        actionName = actionEvent.getActionCommand();

        if (actionName.equals("Log off")) {
            boolean success = client.logout();
            if (success)
                JOptionPane.showMessageDialog(null, "Successfully logged off, Goodbye");
            System.exit(0);
        } else if (actionEvent.getSource().equals(btnNew)) {
            // Show new user screen
           cardLayout.show(cardPanel, "3"); // Show the create new User panel
        } else if (actionEvent.getSource().equals(btnExisting)) {
            // Show the login screen     
            cardLayout.show(cardPanel, "2"); // Show login panel  
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

try {
            // Set cross-platform Java L&F (also called "Metal")
        UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
    } 
    catch (UnsupportedLookAndFeelException e) {
       // handle exception
    }
    catch (ClassNotFoundException e) {
       // handle exception
    }
    catch (InstantiationException e) {
       // handle exception
    }
    catch (IllegalAccessException e) {
       // handle exception
    }
        FTPclientGUI f = new FTPclientGUI();
        f.setVisible(true);

    }
}
