/*
 * McZLauncher (ZeTRiX's Minecraft Launcher)
 * Copyright (C) 2012 Evgen Yanov <http://www.zetlog.ru>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program (In the "_License" folder). If not, see <http://www.gnu.org/licenses/>.
*/

package ru.zetrix.engine;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.border.EmptyBorder;
import ru.zetrix.settings.Base64;
//import ru.zetrix.gui.MZLaf;
import ru.zetrix.settings.Debug;
import ru.zetrix.settings.MZLOptions;
import ru.zetrix.settings.Util;

/**
 * @author ZeTRiX
 */
public class BuildGui extends JFrame {
    public static String[] clientinf;
    public static String[] hashes;
    public static boolean AuthOk = false;
    public static BuildGui buildgui;
    public static MCStart MineStart;
    public static Updater Update;
    private String RootDir = ru.zetrix.settings.Util.getWorkingDirectory().getAbsolutePath() + File.separator;
    public JButton loginb = new JButton("Login", (new ImageIcon(ru.zetrix.settings.Util.getRes("login.png"))));
    public JButton openNews = new JButton("News", (new ImageIcon(ru.zetrix.settings.Util.getRes("news.png"))));
    public JButton Options = new JButton("More Options", (new ImageIcon(ru.zetrix.settings.Util.getRes("opt.png"))));
    public static JLabel UserText = new JLabel("User:");
    public static JTextField UserName = new JTextField(20);
    public static JLabel PassText = new JLabel("Password:");
    public static JPasswordField Password = new JPasswordField(20);
    public JCheckBox SaveData = new JCheckBox("Remember me", false);
    public JCheckBox update = new JCheckBox("Update", false);
    public static JTextPane OutText;
    public static JProgressBar UpdBar;
    
    public static JTabbedPane tabbedPane;
    public static Box MainBox;
    public static Box OptBox;
    public static Box UpdBox;
    public static Box RegBox;
    public static JPanel optpane;
    public static JPanel updpane;
    
    public static JLabel User = new JLabel("Login:");
    public static JTextField Login = new JTextField(20);
    public static JLabel Pass = new JLabel("Password:");
    public static JPasswordField Passwd = new JPasswordField(20);
    public static JLabel Mail = new JLabel("Email:");
    public static JTextField MailF = new JTextField(20);
    public JButton regb = new JButton("Register", (new ImageIcon(ru.zetrix.settings.Util.getRes("reg.png"))));

    String[] modelem = new String[] {"Online", "Offline"};
    private JComboBox mode = new JComboBox(modelem);

    String[] memoryset = new String[] {"1GB", "2GB", "4GB", "8GB", "16GB"};
    private JComboBox memory = new JComboBox(memoryset);
    
    public JButton updb = new JButton("Update now", (new ImageIcon(ru.zetrix.settings.Util.getRes("upd.png"))));

    public BuildGui() {
        setTitle(MZLOptions.LauncherTitle);
        setIconImage(ru.zetrix.settings.Util.getRes("ficon.png"));
        setBackground(Color.BLACK);
        this.setBounds(200, 200, 460, 200);
        setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        final ImageIcon icon = new ImageIcon(BuildGui.class.getResource("/ru/zetrix/res/bg.png"));
        tabbedPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.WRAP_TAB_LAYOUT) {
            protected void paintComponent(Graphics g) {
                g.drawImage(icon.getImage(), 0,0, null);
                super.paintComponent(g);
            }
        };

        Box box1 = Box.createHorizontalBox();
        box1.add(UserText);
        box1.add(Box.createHorizontalStrut(6));
        box1.add(UserName);
        if (Util.getPropertyString("login") != null)
            UserName.setText(Util.getPropertyString("login"));
        Box box2 = Box.createHorizontalBox();
        box2.add(PassText);
        box2.add(Box.createHorizontalStrut(6));
        box2.add(Password);
        if (Util.getPropertyString("password") != null)
            Password.setText(new String(Base64.decode(Util.getPropertyString("password"))));
        Box box3 = Box.createHorizontalBox();
        box3.add(Box.createHorizontalGlue());
        //box3.add(update);
        box3.add(Box.createHorizontalStrut(12));
        box3.add(SaveData);
        box3.add(Box.createHorizontalStrut(10));
        mode.setSelectedIndex(0);
        box3.add(mode);
        box3.add(Box.createHorizontalStrut(10));
        box3.add(loginb);
        UserText.setPreferredSize(PassText.getPreferredSize());
        MainBox = Box.createVerticalBox();
        MainBox.setBorder(new EmptyBorder(12,12,12,12));
        MainBox.add(box1);
        MainBox.add(Box.createVerticalStrut(12));
        MainBox.add(box2);
        MainBox.add(Box.createVerticalStrut(17));
        MainBox.add(box3);
        tabbedPane.add("Login", MainBox);

        Box opt1 = Box.createHorizontalBox();
        opt1.add(memory);
        Box opt2 = Box.createHorizontalBox();
        opt2.add(Options);
        opt2.add(Box.createHorizontalStrut(6));
        opt2.add(openNews);
        Options.setPreferredSize(openNews.getPreferredSize());
        OptBox = Box.createVerticalBox();
        OptBox.setBorder(new EmptyBorder(12,12,12,12));
        OptBox.add(opt1);
        OptBox.add(Box.createVerticalStrut(12));
        OptBox.add(opt2);
        tabbedPane.add("Options", OptBox);
        
        OutText = new JTextPane() {
            private static final long serialVersionUID = 1L;
        };
        OutText.setContentType("text/html");
        OutText.setText("<span style=\"font-size: 15pt\">Update Info Will Be Here</span>");
        OutText.setEditable(false);
        Box upd1 = Box.createHorizontalBox();
        upd1.add(OutText);
        Box upd2 = Box.createHorizontalBox();
        UpdBar = new JProgressBar() {
            private static final long serialVersionUID = 1L;
        };
        UpdBar.setString("Progress Will Apeear Here!");
        upd2.add(UpdBar);
        Box upd3 = Box.createHorizontalBox();
        upd3.add(updb);
        upd3.add(Box.createHorizontalStrut(6));
        UpdBar.setPreferredSize(OutText.getPreferredSize());
        UpdBox = Box.createVerticalBox();
        UpdBox.setBorder(new EmptyBorder(12,12,42,12));
        UpdBox.add(upd1);
        UpdBox.add(Box.createVerticalStrut(12));
        UpdBox.add(upd2);
        UpdBox.add(Box.createVerticalStrut(12));
        UpdBox.add(upd3);
        tabbedPane.add("Update", UpdBox);
        
        Box usbox = Box.createHorizontalBox();
        usbox.add(User);
        usbox.add(Box.createHorizontalStrut(5));
        usbox.add(Login);
        Box pasbox = Box.createHorizontalBox();
        pasbox.add(Pass);
        pasbox.add(Box.createHorizontalStrut(5));
        pasbox.add(Passwd);
        Box embox = Box.createHorizontalBox();
        embox.add(Mail);
        embox.add(Box.createHorizontalStrut(5));
        embox.add(MailF);
        Box acpbox = Box.createHorizontalBox();
        acpbox.add(Box.createHorizontalGlue());
        acpbox.add(Box.createHorizontalStrut(8));
        acpbox.add(regb);
        User.setPreferredSize(Pass.getPreferredSize());
        Mail.setPreferredSize(Pass.getPreferredSize());
        RegBox = Box.createVerticalBox();
        RegBox.setBorder(new EmptyBorder(10,10,10,10));
        RegBox.add(usbox);
        RegBox.add(Box.createVerticalStrut(10));
        RegBox.add(pasbox);
        RegBox.add(Box.createVerticalStrut(10));
        RegBox.add(embox);
        RegBox.add(Box.createVerticalStrut(15));
        RegBox.add(acpbox);
        tabbedPane.add("Register", RegBox);
        
        this.getContentPane().add(tabbedPane);
        
        loginb.addActionListener(new LoginListner());
        regb.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Auther.Register(Login.getText(), Passwd.getPassword(), MailF.getText());
            }
        });
        updb.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updb.setEnabled(false);
                UpdBar.setStringPainted(true);
		UpdBar.setMinimum(0);
		UpdBar.setMaximum(100);
		UpdBar.setValue(0);
//		UpdBar.setVisible(true);
//                loginb.setEnabled(false);
                Updater.Update();
            }
        });        
        openNews.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new ru.zetrix.gui.News().setVisible(true);
            }
        });
        Options.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new ru.zetrix.gui.Options().setVisible(true);
            }
        });
        SaveData.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (SaveData.isSelected()) {
                    Util.setProperty("password", Base64.encode(new String (BuildGui.Password.getPassword())));
                }
            }
        });
    }
    
    class LoginListner implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (mode.getSelectedItem().equals("Online") && (UserName.getText().length() > 2) && (Password.getPassword().length > 1)) {
                
                Util.setProperty("login", BuildGui.UserName.getText());
                Auther.Authorize(BuildGui.UserName.getText(), new String(BuildGui.Password.getPassword()));
                checkUpdate();                
                MineStart = new MCStart(UserName.getText(), clientinf[1].trim());
                setVisible(false);
        
            } else if(UserName.getText().length() < 2) {
                javax.swing.JOptionPane.showMessageDialog((java.awt.Component)
                        null,
                        "You forget to enter a username or it's not valid \n Please, next time, do not forget to enter a valid one. \n",
                        "Warning",
                        JOptionPane.WARNING_MESSAGE);
            } else {
                print("Switching to offline mode...");
                File[] client = new File[4];
                client[0] = new File(RootDir + "bin" + File.separator + "minecraft.jar");
                client[1] = new File(RootDir + "bin" + File.separator + "lwjgl.jar");
                client[2] = new File(RootDir + "bin" + File.separator + "jinput.jar");
                client[3] = new File(RootDir + "bin" + File.separator + "lwjgl_util.jar");

                print("Searching for client files...");
                if ((!client[0].exists()) || (!client[1].exists()) || (!client[2].exists()) || (!client[3].exists())) {
                    print("One or more files nor found. \n Nothing can be done in Offline Mode!");
                    
                    JOptionPane.showMessageDialog(null,
                            "One or more game files nor found! \n Can't do anything, case of offline mode. \n You must be in Online mode to download client files.",
                            "Offline mode Error",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    print("Client files succesfully detected!");
                    print("Running game in offline mode!");
                    
                    MineStart = new MCStart(UserName.getText(), "");
                    setVisible(false);
                }
                
//            javax.swing.JOptionPane.showMessageDialog((java.awt.Component)
//                    null,
//                    "No games avaliable in offline mode \n Close this window, nigga! \n Understand me? \n",
//                    "Warning",
//                    JOptionPane.INFORMATION_MESSAGE);
        }                    
    }
}
    
    public void checkUpdate() {
        print("Searching for client files...");
        
        File[] client = new File[4];
        client[0] = new File(RootDir + "bin" + File.separator +"minecraft.jar");
        client[1] = new File(RootDir + "bin" + File.separator + "lwjgl.jar");
        client[2] = new File(RootDir + "bin" + File.separator + "jinput.jar");
        client[3] = new File(RootDir + "bin" + File.separator + "lwjgl_util.jar");
        
        if ((!client[0].exists()) || (!client[1].exists()) || (!client[2].exists()) || (!client[3].exists())) {
            print("One or more files not found. \n Starting Update!");
            Updater.Update();
        } else {
            print("Client files succesfully detected!");
        }
    }
    
	public static void main(String[] args) {
                        try {
//                            UIManager.setLookAndFeel(MZLaf.class.getCanonicalName());
                            for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                                if ("Nimbus".equals(info.getName())) {
                                    UIManager.setLookAndFeel(info.getClassName());
                                    break;
                                }
                            }
                        } catch (Exception e) {
                            print("Error: " + e);
                        }
                        buildgui = new BuildGui();
                        buildgui.show();

	}
        
        private static void print(String str) {
            Debug.Logger(str);
        }
}