import java.util.Calendar;
import java.util.Scanner;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.awt.Font;
import java.awt.Color;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.PopupMenu;
import java.awt.MenuItem;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JWindow;
import javax.swing.SwingConstants;

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
        
        SystemTray tray = SystemTray.getSystemTray();
        PopupMenu menu = new PopupMenu();
        TrayIcon icon = new TrayIcon(Toolkit.getDefaultToolkit().getImage("logo.png"), "ClockWidget");
        MenuItem item = new MenuItem("Exit");
        MenuItem settings = new MenuItem("Open Settings");
        item.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                tray.remove(icon);
                System.exit(0);
            }
        });
        settings.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                openSettings();
            }
        });
        menu.add(item);
        menu.add(settings);
        icon.setPopupMenu(menu);
        try{
            tray.add(icon);
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        
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

    private static void openSettings(){
        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
        JFrame settingsFrame = new JFrame("ClockWidget Settings");
        settingsFrame.setSize(600,500);
        settingsFrame.setLocation(((int)size.getWidth()-600)/2, ((int)size.getHeight()-500)/2);

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
        }catch(FileNotFoundException e){
            System.out.println(e.getMessage());
        }
        
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