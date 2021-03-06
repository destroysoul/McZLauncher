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

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.swing.*;
import ru.zetrix.settings.Debug;
import ru.zetrix.settings.MZLOptions;
import ru.zetrix.settings.ShieldUtil;
import ru.zetrix.settings.Util;

public class Updater extends JFrame {
    public static final boolean debug = true;
    private static JPanel UpdPane;
    private static JTextPane OutText;
    private static JProgressBar UpdBar;
    private static FileOutputStream fw;
    
    private static JButton CancelUpd = new JButton("Cancel Update", (new ImageIcon(ru.zetrix.settings.Util.getRes("upd.png"))));
    private static JLabel Game = new JLabel("Now close this window and push the login button again!");
    
    private JPanel ProgressPanel;
    private JPanel InSideProgressPane;
    private JPanel ButtonPane;
    private static Font GameTextFont = new Font("Arial", Font.BOLD, 16);
    
    public Updater() {
        super(MZLOptions.UpdWinName);
        setIconImage(ru.zetrix.settings.Util.getRes("ficon.png"));
        setBackground(Color.BLACK);
        this.setBounds(200, 200, 460, 220);
        setResizable(true);
        this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

        UpdPane = new JPanel();
        this.getContentPane().add(UpdPane, BorderLayout.NORTH);
        UpdPane.setPreferredSize(new Dimension(0,40));
        
        ProgressPanel = new JPanel(new BorderLayout());
        this.getContentPane().add(ProgressPanel, BorderLayout.CENTER);
        ProgressPanel.setPreferredSize(new Dimension(0,80));
        
        InSideProgressPane = new JPanel(new GridLayout(3,1,0,0));
        ProgressPanel.add(InSideProgressPane, BorderLayout.CENTER);
        InSideProgressPane.setPreferredSize(new Dimension(420,0));
        InSideProgressPane.setOpaque(false);
        
        ButtonPane = new JPanel();
        this.getContentPane().add(ButtonPane, BorderLayout.SOUTH);
        ButtonPane.setPreferredSize(new Dimension(0,40));
        
        OutText = new JTextPane();
        OutText.setContentType("text/html");
        OutText.setText("<span style=\"font-size: 15pt\">Update Info Will Be Here</span>");
        OutText.setEditable(false);
        
        UpdBar = new JProgressBar();
        UpdBar.setStringPainted(true);
        UpdBar.setString("Progress Will Apeear Here!");
        UpdBar.setMinimum(0);
        UpdBar.setMaximum(100);
        UpdBar.setValue(0);
        OutText.setMinimumSize(new Dimension(440, 40));
        UpdBar.setMinimumSize(new Dimension(400, 40));
        UpdBar.setPreferredSize(OutText.getPreferredSize());
        
        Game.setFont(GameTextFont);
        Game.setVisible(false);
        
        UpdPane.add(OutText);
        InSideProgressPane.add(UpdBar);
        InSideProgressPane.add(Game);
        ButtonPane.add(CancelUpd);
        Update();
        
        CancelUpd.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Util.SleepTime(500L);
                setVisible(false);
            }
        });
    }
    
    public static void Update() {
        new Thread() {
            @Override
            public void run() {
                try {
                    //BuildGui.tabbedPane.remove(1);
                    UpdPane.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                    OutText.setText("<span style=\"font-size: 15pt\">Connecting to Download Server...</span>");
                    
                    
                    String ZipEx = ru.zetrix.settings.NetUtil.ConnectNet(MZLOptions.ZipURL, "mczip=" + MZLOptions.zip_package);
                    if (ZipEx.trim() == null) {
                        OutText.setText("<span style=\"font-size: 15pt\">Error zipping files...</span>");
                    } else if (ZipEx.trim().equals("Fuck Off")) {
                        BuildGui.WrongClient = true;
                        OutText.setText("<span style=\"font-size: 15pt\">Launcher version check failed...</span>");
                    } else if (ZipEx.trim().equals("Success")) {
                        URL url = new URL(MZLOptions.UpdURL + MZLOptions.zip_package);
                        OutText.setText("<span style=\"font-size: 15pt\">Recieving a list of files...</span>");
                        
                        HttpURLConnection updconn = (HttpURLConnection) url.openConnection();
                        File client = new File(MZLOptions.RootDir, MZLOptions.zip_package);
                        long cll_web = updconn.getContentLength();
                        
                        UpdBar.setMaximum((int) cll_web);
                        if (client.length() != cll_web && cll_web > 1) {}
                        
                        BufferedInputStream bis = new BufferedInputStream(updconn.getInputStream());
                        OutText.setText("<span style=\"font-size: 15pt\">Testing paths...</span>");
                        
                        fw = new FileOutputStream(client);
                        OutText.setText("<span style=\"font-size: 15pt\">Loading selected files...</span>");

                        byte[] b = new byte[1024];
                        int count = 0;
                        
                        OutText.setText("<span style=\"font-size: 15pt\">Saving client files to disc...</span>");
                        while ((count=bis.read(b)) != -1) {
                            fw.write(b,0,count);
                            UpdBar.setValue((int) client.length());
                            UpdBar.setString("Saving " + MZLOptions.zip_package + " ("
                                    + (int) client.length() + " bytes of " + cll_web
                                    + " bytes)");
                        }
                        fw.close();
                        
                        OutText.setText("<span style=\"font-size: 15pt\">Unpacking client...</span>");
                        unzip();
                        OutText.setText("<span style=\"font-size: 15pt\">All Done! Have a nice day!</span>");
                        UpdBar.setString("Done!");
                        UpdPane.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                        CancelUpd.setEnabled(false);
                        Game.setVisible(true);
                    }
                } catch (IOException ex) {
                    OutText.setText("Error: " + ex.toString());
                }
            }
        }
                .start();
    }
  
    protected static void unzip() {
        String path = MZLOptions.RootDir;
        String ZFilePath = path + MZLOptions.zip_package;
        String ExPath = path;

        Vector zipEntries = new Vector();
        try
        {
            ZipFile zf = new ZipFile(ZFilePath);
            Enumeration en = zf.entries();

      while (en.hasMoreElements()) zipEntries.addElement((ZipEntry)en.nextElement());

      for (int i = 0; i < zipEntries.size(); i++)
      {
        ZipEntry ze = (ZipEntry)zipEntries.elementAt(i);
        extractFromZip(ZFilePath, ExPath, ze.getName(), zf, ze);
      }

      zf.close();
    }
    catch (Exception ex)
    {
      print(ex.toString());
    }
    new File(ZFilePath).delete();
  }

  static void extractFromZip(String szZipFilePath, String szExtractPath, String szName, ZipFile zf, ZipEntry ze)
  {
    if (ze.isDirectory()) return;

    String szDstName = slash2sep(szName);
    String szEntryDir;
    if (szDstName.lastIndexOf(File.separator) != -1) szEntryDir = szDstName.substring(0, szDstName.lastIndexOf(File.separator)); else {
      szEntryDir = "";
    }
   try
    {
            File newDir = new File((new StringBuilder(String.valueOf(szExtractPath))).append(File.separator).append(szEntryDir).toString());
            newDir.mkdirs();
            FileOutputStream fos = new FileOutputStream((new StringBuilder(String.valueOf(szExtractPath))).append(File.separator).append(szDstName).toString());
            InputStream is = zf.getInputStream(ze);
            byte buf[] = new byte[1024];
            do
            {
                int nLength;
                try
                {
                    nLength = is.read(buf);
                }
                catch(EOFException ex)
                {
                    break;
                }
                if(nLength < 0)
                    break;
                fos.write(buf, 0, nLength);
            } while(true);
            is.close();
            fos.close();

    }
    catch (Exception e)
    {
      print(e.toString());
    }
  }

  static String slash2sep(String src)
  {
    char[] chDst = new char[src.length()];

    for (int i = 0; i < src.length(); i++)
    {
      if (src.charAt(i) == '/') chDst[i] = File.separatorChar; else
        chDst[i] = src.charAt(i);
    }
    String dst = new String(chDst);
    return dst;
  }
  
  private static void print(String str) {
      Debug.Logger(str);
  }
}