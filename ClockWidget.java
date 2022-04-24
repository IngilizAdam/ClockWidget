import java.util.Calendar;
import java.util.Scanner;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.net.URI;
import java.awt.Font;
import java.awt.Color;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.PopupMenu;
import java.awt.MenuItem;
import java.awt.Image;
import java.awt.Desktop;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.JWindow;
import javax.swing.SwingConstants;
import javax.swing.event.MouseInputAdapter;

public class ClockWidget implements Runnable{
    private static int DATE_LOCATION = 0;
    private static int CLOCK_LOCATION = 0;
    private static int SECOND_LOCATION = 0;
    private static int DATE_SIZE = 30;
    private static int CLOCK_SIZE = 160;
    private static int SECOND_SIZE = 30;
    private static Color DATE_COLOR = new Color(139,0,139);
    private static Color CLOCK_COLOR = new Color(139,0,139);
    private static Color SECOND_COLOR = new Color(139,0,139);
    private static String FONT_NAME = "OdudoMono-SemiBold.ttf";
    
    private static Thread t1 = null;

    public static void main(String[] args) {
        t1 = new Thread(new ClockWidget());
        t1.start();
    }

    public void run(){
        readData();
        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
        JWindow clockFrame = new JWindow();
        clockFrame.setFocusable(false);
        clockFrame.setSize((int)size.getWidth(), (int)size.getHeight());
        clockFrame.setLocation(0,0);
        clockFrame.setBackground(new Color(0,0,0,0));
        clockFrame.setLayout(null);

        
        Image image = Toolkit.getDefaultToolkit().getImage("logo.png");
        SystemTray tray = SystemTray.getSystemTray();
        PopupMenu menu = new PopupMenu();
        TrayIcon icon = new TrayIcon(image.getScaledInstance(new TrayIcon(image).getSize().width, -1, Image.SCALE_SMOOTH), "ClockWidget");
        MenuItem exitItem = new MenuItem("Exit");
        MenuItem settings = new MenuItem("Open Settings");
        MenuItem rightClickSettings = new MenuItem("Open Right Click Settings");
        exitItem.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                tray.remove(icon);
                System.exit(0);
            }
        });
        rightClickSettings.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                openMenuSettings();
            }
        });
        settings.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                openSettings();
            }
        });
        menu.add(settings);
        menu.add(rightClickSettings);
        menu.add(exitItem);
        icon.setPopupMenu(menu);
        try{
            tray.add(icon);
        }catch(Exception e){
            System.out.println(e.getMessage());
        }

        JPopupMenu clickMenu = new JPopupMenu();
        JMenuItem desktopMenu = new JMenuItem("Desktop");
        desktopMenu.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                openFolder(System.getProperty("user.home") + "/Desktop", clockFrame);
            }
        });
        JMenuItem exit = new JMenuItem("Exit");
        exit.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                tray.remove(icon);
                System.exit(0);
            }
        });
        JMenuItem[] menuItems = null;
        try{
            Scanner in = new Scanner(new FileInputStream("menus.tuna"));
            int menuCount = 0;
            String input = "";
            while(in.hasNextLine()){
                menuCount++;
                input += "\n" + in.nextLine();
            }
            in.close();
            input = input.trim();
            menuItems = new JMenuItem[menuCount+2];
            menuItems[0] = desktopMenu;
            menuItems[menuCount+1] = exit;

            for(int i = 1; i < menuCount+1; i++){
                String[] values = input.split("::");
                boolean newLine = false;
                String backup = input;
                input = "";
                for(int j = 1; j < backup.length(); j++){
                    if(newLine)
                        input += backup.charAt(j);
                    if(backup.charAt(j) == '\n')
                        newLine = true;
                }
                JMenuItem temp = new JMenuItem(values[0]);
                switch(values[1]){
                    case "Link":
                        temp.addActionListener(new ActionListener(){
                            public void actionPerformed(ActionEvent e) {
                                openURI(values[2], clockFrame);
                            }
                        });
                        break;
                    case "Folder":
                        temp.addActionListener(new ActionListener(){
                            public void actionPerformed(ActionEvent e) {
                                openFolder(values[2], clockFrame);
                            }
                        });
                        break;
                }
                menuItems[i] = temp;
            }
        }catch(FileNotFoundException e){
            try{
                PrintWriter print = new PrintWriter("menus.tuna");
                print.close();
                menuItems = new JMenuItem[0];
            }catch(Exception ex){}
        }
        for(int i = 0; i < menuItems.length; i++)
            clickMenu.add(menuItems[i]);
        
        Font clockFont = null;
        try{
            clockFont = Font.createFont(Font.TRUETYPE_FONT, new FileInputStream("Fonts\\" + FONT_NAME)).deriveFont(Font.BOLD, CLOCK_SIZE);
        }catch(Exception e){
            System.out.println("Font not found!");
        }
        Font secondFont = clockFont.deriveFont(Font.BOLD, SECOND_SIZE);
        Font dateFont = clockFont.deriveFont(Font.BOLD, DATE_SIZE);
        
        JLabel dateLabel = new JLabel("", SwingConstants.CENTER);
        dateLabel.setVerticalAlignment(JLabel.TOP);
        dateLabel.setFont(dateFont);
        dateLabel.setForeground(DATE_COLOR);
        dateLabel.setBounds(0,DATE_LOCATION,(int)size.getWidth(),(int)size.getHeight());
        clockFrame.add(dateLabel);
        
        JLabel clockLabel = new JLabel("", SwingConstants.CENTER);
        clockLabel.setVerticalAlignment(JLabel.TOP);
        clockLabel.setForeground(CLOCK_COLOR);
        clockLabel.setFont(clockFont);
        clockLabel.setBounds(0,CLOCK_LOCATION,(int)size.getWidth(),(int)size.getHeight());
        clockFrame.add(clockLabel);

        clockLabel.setComponentPopupMenu(clickMenu);

        clockFrame.addMouseListener(new MouseInputAdapter() {
            @Override
            public void mousePressed(MouseEvent e){
                clockFrame.remove(clickMenu);
            }
        });

        JLabel secondLabel = new JLabel("", SwingConstants.CENTER);
        secondLabel.setVerticalAlignment(JLabel.TOP);
        secondLabel.setFont(secondFont); 
        secondLabel.setForeground(SECOND_COLOR);
        secondLabel.setBounds(0,SECOND_LOCATION,(int)size.getWidth(),(int)size.getHeight());
        clockFrame.add(secondLabel);

        clockFrame.setVisible(true);

        Calendar date = null;
        String[] time = null;
        while(true){
            date = Calendar.getInstance();
            time = date.getTime().toString().split(" ");
            dateLabel.setText(time[0] + " " + time[1] + " " + time[2]);
            time = time[3].split(":");
            clockLabel.setText(time[0] + ":" + time[1]);
            secondLabel.setText(time[2]);
            try{
                Thread.sleep(100);
            }catch(InterruptedException e){
                clockFrame.setVisible(false);
                clockFrame.dispose();
                tray.remove(icon);
                t1 = new Thread(new ClockWidget());
                t1.start();
                return;
            }
        }
    }

    private static void openURI(String uri, JWindow clockFrame){
        try{
            Desktop.getDesktop().browse(new URI(uri));
        }catch(Exception exception){
            JOptionPane.showMessageDialog(clockFrame, exception.getMessage(), "ClockWidget", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void openFolder(String location, JWindow clockFrame){
        try{
            Desktop.getDesktop().open(new File(location));
        }catch(Exception exception){
            JOptionPane.showMessageDialog(clockFrame, exception.getMessage(), "ClockWidget", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void openSettings(){
        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
        JFrame settingsFrame = new JFrame("ClockWidget Settings");
        settingsFrame.setSize(600,500);
        settingsFrame.setLocation(((int)size.getWidth()-600)/2, ((int)size.getHeight()-500)/2);
        settingsFrame.setIconImage(new ImageIcon("logo.png").getImage());

        JPanel deneme = new JPanel();
        deneme.setLayout(new BoxLayout(deneme, BoxLayout.PAGE_AXIS));
        JTextField dateLocation = new JTextField(5);
        JTextField clockLocation = new JTextField(5);
        JTextField secondLocation = new JTextField(5);
        JTextField dateSize = new JTextField(5);
        JTextField clockSize = new JTextField(5);
        JTextField secondSize = new JTextField(5);
        JTextField dateColor = new JTextField(12);
        JTextField clockColor = new JTextField(12);
        JTextField secondColor = new JTextField(12);

        File folder = new File("Fonts");
        File[] allFonts = folder.listFiles();
        String[] fileNames = new String[allFonts.length];
        for(int i = 0; i < fileNames.length; i++)
            fileNames[i] = allFonts[i].getName();
        JComboBox<String> fontName = new JComboBox<String>(fileNames);

        try{
            Scanner scan = new Scanner(new FileInputStream("settings.tuna"));
            dateLocation.setText(scan.nextLine().trim());
            clockLocation.setText(scan.nextLine().trim());
            secondLocation.setText(scan.nextLine().trim());
            dateSize.setText(scan.nextLine().trim());
            clockSize.setText(scan.nextLine().trim());
            secondSize.setText(scan.nextLine().trim());
            dateColor.setText(scan.nextLine().trim() + "," + scan.nextLine().trim() + "," + scan.nextLine().trim());
            clockColor.setText(scan.nextLine().trim() + "," + scan.nextLine().trim() + "," + scan.nextLine().trim());
            secondColor.setText(scan.nextLine().trim() + "," + scan.nextLine().trim() + "," + scan.nextLine().trim());
            fontName.setSelectedItem(scan.nextLine().trim());
            scan.close();
        }catch(FileNotFoundException e){}
        
        JButton save = new JButton("Save Settings");
        save.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                try{
                    PrintWriter print = new PrintWriter("settings.tuna");
                    if(dateLocation.getText().length() == 0)
                        print.println(DATE_LOCATION);
                    else
                        print.println(dateLocation.getText().trim());
                    if(clockLocation.getText().length() == 0)
                        print.println(CLOCK_LOCATION);
                    else
                        print.println(clockLocation.getText().trim());
                    if(secondLocation.getText().length() == 0)
                        print.println(SECOND_LOCATION);
                    else
                        print.println(secondLocation.getText().trim());
                    if(dateSize.getText().length() == 0)
                        print.println(DATE_SIZE);
                    else
                        print.println(dateSize.getText().trim());
                    if(clockSize.getText().length() == 0)
                        print.println(CLOCK_SIZE);
                    else
                        print.println(clockSize.getText().trim());
                    if(secondSize.getText().length() == 0)
                        print.println(SECOND_SIZE);
                    else
                        print.println(secondSize.getText().trim());
                    if(dateColor.getText().length() == 0){
                        print.println(DATE_COLOR.getRed());
                        print.println(DATE_COLOR.getGreen());
                        print.println(DATE_COLOR.getBlue());
                    }
                    else{
                        String[] colors = dateColor.getText().trim().split(",");
                        print.println(colors[0]);
                        print.println(colors[1]);
                        print.println(colors[2]);
                    }
                    if(clockColor.getText().length() == 0){
                        print.println(CLOCK_COLOR.getRed());
                        print.println(CLOCK_COLOR.getGreen());
                        print.println(CLOCK_COLOR.getBlue());
                    }
                    else{
                        String[] colors = clockColor.getText().trim().split(",");
                        print.println(colors[0]);
                        print.println(colors[1]);
                        print.println(colors[2]);
                    }
                    if(secondColor.getText().length() == 0){
                        print.println(SECOND_COLOR.getRed());
                        print.println(SECOND_COLOR.getGreen());
                        print.println(SECOND_COLOR.getBlue());
                    }
                    else{
                        String[] colors = secondColor.getText().trim().split(",");
                        print.println(colors[0]);
                        print.println(colors[1]);
                        print.println(colors[2]);
                    }
                    print.println(fontName.getSelectedItem());
                    print.close();
                    t1.interrupt();
                    JOptionPane.showConfirmDialog(settingsFrame, "Saved Successfully", "ClockWidget", JOptionPane.DEFAULT_OPTION);
                }catch(FileNotFoundException exception){
                    System.out.println(exception.getMessage());
                }
            }
        });

        JPanel dateLocationPanel = new JPanel();
        dateLocationPanel.add(new JLabel("Date Location: "));
        dateLocationPanel.add(dateLocation);

        JPanel clockLocationPanel = new JPanel();
        clockLocationPanel.add(new JLabel("Clock Location: "));
        clockLocationPanel.add(clockLocation);

        JPanel secondLocationPanel = new JPanel();
        secondLocationPanel.add(new JLabel("Seconds Location: "));
        secondLocationPanel.add(secondLocation);

        JPanel dateSizePanel = new JPanel();
        dateSizePanel.add(new JLabel("Date Size: "));
        dateSizePanel.add(dateSize);

        JPanel clockSizePanel = new JPanel();
        clockSizePanel.add(new JLabel("Clock Size: "));
        clockSizePanel.add(clockSize);

        JPanel secondSizePanel = new JPanel();
        secondSizePanel.add(new JLabel("Seconds Size: "));
        secondSizePanel.add(secondSize);

        JPanel dateColorPanel = new JPanel();
        dateColorPanel.add(new JLabel("Date Color (R,G,B): "));
        dateColorPanel.add(dateColor);

        JPanel clockColorPanel = new JPanel();
        clockColorPanel.add(new JLabel("Clock Color (R,G,B): "));
        clockColorPanel.add(clockColor);

        JPanel secondColorPanel = new JPanel();
        secondColorPanel.add(new JLabel("Seconds Color (R,G,B): "));
        secondColorPanel.add(secondColor);

        JPanel fontNamePanel = new JPanel();
        fontNamePanel.add(new JLabel("Font Name: "));
        fontNamePanel.add(fontName);

        JPanel savePanel = new JPanel();
        savePanel.add(save);

        File[] themes = new File("Saved Themes").listFiles();
        String[] themeNames = new String[themes.length];
        for(int i = 0; i < themes.length; i++)
            themeNames[i] = themes[i].getName();

        JButton saveTheme = new JButton("Save Theme");
        saveTheme.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                String themeName = null;
                themeName = JOptionPane.showInputDialog(settingsFrame, "Type name for the theme").trim();
                if(themeName == null || themeName.length()==0)
                    return;
                try{
                    Scanner in = new Scanner(new FileInputStream("settings.tuna"));
                    PrintWriter theme = new PrintWriter("Saved Themes\\" + themeName);
                    while(in.hasNextLine())
                        theme.println(in.nextLine());
                    in.close();
                    theme.close();
                    settingsFrame.setVisible(false);
                    settingsFrame.dispose();
                    openSettings();
                }catch(FileNotFoundException exception){}
            }
        });
        JPanel saveThemePanel = new JPanel();
        saveThemePanel.add(saveTheme);

        JComboBox<String> selectTheme = new JComboBox<String>(themeNames);

        JButton useSavedTheme = new JButton("Use Selected Theme");
        useSavedTheme.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                try{
                    Scanner in = new Scanner(new FileInputStream("Saved Themes\\" + selectTheme.getSelectedItem()));
                    PrintWriter set = new PrintWriter("settings.tuna");
                    while(in.hasNextLine())
                        set.println(in.nextLine());
                    in.close();
                    set.close();
                    t1.interrupt();
                    settingsFrame.setVisible(false);
                    settingsFrame.dispose();
                    openSettings();
                }catch(FileNotFoundException exception){
                    JOptionPane.showMessageDialog(settingsFrame, "Failed to get theme", "ClockWidget", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        JButton removeSelectedTheme = new JButton("Delete Selected Theme");
        removeSelectedTheme.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                File theme = new File("Saved Themes\\" + (String)selectTheme.getSelectedItem());
                int result = JOptionPane.showConfirmDialog(settingsFrame, theme.getName() + " will be deleted.\nAre you sure?", "ClockWidget", JOptionPane.YES_NO_OPTION);
                if(result == JOptionPane.YES_OPTION){
                    theme.delete();
                    settingsFrame.setVisible(false);
                    settingsFrame.dispose();
                    openSettings();
                }
            }
        });
        JPanel selectThemePanel = new JPanel();
        selectThemePanel.add(selectTheme);
        selectThemePanel.add(useSavedTheme);
        selectThemePanel.add(removeSelectedTheme);

        deneme.add(dateLocationPanel);
        deneme.add(clockLocationPanel);
        deneme.add(secondLocationPanel);
        deneme.add(dateSizePanel);
        deneme.add(clockSizePanel);
        deneme.add(secondSizePanel);
        deneme.add(dateColorPanel);
        deneme.add(clockColorPanel);
        deneme.add(secondColorPanel);
        deneme.add(fontNamePanel);
        deneme.add(savePanel);
        deneme.add(saveThemePanel);
        deneme.add(selectThemePanel);
        settingsFrame.add(deneme);
        settingsFrame.setVisible(true);
    }

    private static void openMenuSettings(){
        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
        JFrame settingsFrame = new JFrame("ClockWidget Settings");
        settingsFrame.setSize(1000,500);
        settingsFrame.setLocation(((int)size.getWidth()-1000)/2, ((int)size.getHeight()-500)/2);
        settingsFrame.setIconImage(new ImageIcon("logo.png").getImage());

        String input = "";
        int menuCount = 0;
        try{
            Scanner in = new Scanner(new FileInputStream("menus.tuna"));
            while(in.hasNextLine()){
                menuCount++;
                input += in.nextLine() + "\n";
            }
            in.close();
            input = input.trim();
        }catch(FileNotFoundException e){
            try{
                PrintWriter print = new PrintWriter("menus.tuna");
                print.close();
            }catch(Exception ex){}
        }
        
        String[] menus = input.split("\n");
        String[] menuNames = new String[menus.length];
        for(int i = 0; i < menuNames.length; i++){
            menuNames[i] = menus[i].split("::")[0];
        }
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        JPanel[] panels = new JPanel[menuCount];
        JComboBox<String>[] menuName = new JComboBox[menuCount];
        for(int i = 0; i < panels.length; i++){
            panels[i] = new JPanel();
            JLabel order = new JLabel(i+1+". ");
            JButton remove = new JButton("Remove");
            remove.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e) {
                    int whichPanel = 0;
                    for(int j = 0; j < panels.length; j++){
                        if(panels[j] != null && panels[j] == remove.getParent()){
                            whichPanel = j;
                            break;
                        }
                    }
                    int result = JOptionPane.showConfirmDialog(settingsFrame, menuNames[whichPanel] + " will be deleted.\nAre you sure?", "ClockWidget", JOptionPane.YES_NO_OPTION);
                    if(result != JOptionPane.YES_OPTION){
                        return;
                    }
                    System.out.println(menus[whichPanel]);
                    try{
                        PrintWriter newFile = new PrintWriter("menus.tuna");
                        for(int j = 0; j < menus.length; j++){
                            if(!menus[whichPanel].equals(menus[j]))
                                newFile.println(menus[j]);
                        }
                        newFile.close();
                        settingsFrame.setVisible(false);
                        settingsFrame.dispose();
                        openMenuSettings();
                        t1.interrupt();
                    }catch(FileNotFoundException exception){
                        JOptionPane.showMessageDialog(settingsFrame, exception.getMessage(), "ClockWidget", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
            menuName[i] = new JComboBox<String>(menuNames);
            menuName[i].setSelectedIndex(i);
            panels[i].add(order);
            panels[i].add(menuName[i]);
            panels[i].add(remove);
            panel.add(panels[i]);
        }
        JTextField nameField = new JTextField(10), typeField = new JTextField(5), addressField = new JTextField(20);
        JTextField[] textFields = {nameField,typeField,addressField};
        JLabel[] labels = {new JLabel("Name: "), new JLabel("Type (Link-Folder): "), new JLabel("Address: ")};
        JPanel newdata = new JPanel();
        for(int i = 0; i < 3; i++){
            newdata.add(labels[i]);
            newdata.add(textFields[i]);
        }
        JButton save = new JButton("Save");
        save.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                try{
                    String name = nameField.getText().trim();
                    String type = typeField.getText().trim();
                    String address = addressField.getText().trim();
                    String newData = ""; 
                    for(int i = 0; i < menuName.length; i++)
                        newData += menus[menuName[i].getSelectedIndex()] + "\n";
                    if(name.length() != 0 && type.length() != 0 && address.length() != 0)
                        newData += name + "::" + type + "::" + address + "::";
                    PrintWriter print = new PrintWriter("menus.tuna");
                    print.print(newData);
                    print.close();
                    if(name.length() != 0 && type.length() != 0 && address.length() != 0)
                        JOptionPane.showMessageDialog(settingsFrame, "New Menu Saved", "ClockWidget", JOptionPane.INFORMATION_MESSAGE);
                    else
                        JOptionPane.showMessageDialog(settingsFrame, "Order Saved", "ClockWidget", JOptionPane.INFORMATION_MESSAGE);
                    settingsFrame.setVisible(false);
                    settingsFrame.dispose();
                    openMenuSettings();
                    t1.interrupt();
                }catch(Exception exception){
                    JOptionPane.showMessageDialog(settingsFrame, exception.getMessage(), "ClockWidget", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        newdata.add(save);
        panel.add(newdata);

        settingsFrame.add(panel);
        settingsFrame.setVisible(true);
    }

    private static void readData(){
        try{
            Scanner scan = new Scanner(new FileInputStream("settings.tuna"));
            DATE_LOCATION = Integer.parseInt(scan.nextLine().trim());
            CLOCK_LOCATION = Integer.parseInt(scan.nextLine().trim());
            SECOND_LOCATION = Integer.parseInt(scan.nextLine().trim());
            DATE_SIZE = Integer.parseInt(scan.nextLine().trim());
            CLOCK_SIZE = Integer.parseInt(scan.nextLine().trim());
            SECOND_SIZE = Integer.parseInt(scan.nextLine().trim());
            DATE_COLOR = new Color(Integer.parseInt(scan.nextLine().trim()),Integer.parseInt(scan.nextLine().trim()),Integer.parseInt(scan.nextLine().trim()));
            CLOCK_COLOR = new Color(Integer.parseInt(scan.nextLine().trim()),Integer.parseInt(scan.nextLine().trim()),Integer.parseInt(scan.nextLine().trim()));
            SECOND_COLOR = new Color(Integer.parseInt(scan.nextLine().trim()),Integer.parseInt(scan.nextLine().trim()),Integer.parseInt(scan.nextLine().trim()));
            FONT_NAME = scan.nextLine().trim();
            scan.close();
        }catch(FileNotFoundException e){
            System.out.println(e.getMessage());
        }
    }
}