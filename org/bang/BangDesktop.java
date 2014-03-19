// copyright (c) 1998,1999 Róbert Viðar Bjarnason
// 
// This program is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2, or (at your option)
// any later version.
// 
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
// 
// You should have received a copy of the GNU General Public License
// along with this program; see the file COPYING.  If not, write to
// the Free Software Foundation, 675 Mass Ave, Cambridge, MA 02139, USA.

package org.bang;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import javax.accessibility.*;

import java.awt.BorderLayout;
import java.awt.*;
import java.awt.event.*;
import java.awt.Toolkit;
import org.bang.Bang;
import java.util.*;

/**
 *   This class is an full screen desktop wrapper around Bang
 *   @author Robert Vidar Bjarnason (robofly@bang.is)
 */
public class BangDesktop
{
 JInternalFrame mooSplitFrame;
 static JDesktopPane desktop;

 public BangDesktop()
 {}

 public static void main(String args[])
 {
	String urlProxyString = null;
	String telnetProxyString = null;
	String proxyHost = null;
	String proxyPort = null;
	if (args.length != 0)
	{
		if (args.length != 2)
		{
			System.out.println("Usage: java org.bang.BangDesktop urlProxy telnetProxy");
			System.exit(0);
		} 
		else
		{
			urlProxyString = args[0];
			try
			{
				StringTokenizer st = new StringTokenizer(urlProxyString,":");
				Properties props= new Properties(System.getProperties());
				props.put("http.proxySet", "true");
				props.put("http.proxyHost", st.nextToken());
				props.put("http.proxyPort", st.nextToken());
				Properties newprops = new Properties(props);
				System.setProperties(newprops);
				telnetProxyString = args[1];
				st = null;
				st = new StringTokenizer(telnetProxyString,":");
				proxyHost = st.nextToken();
				proxyPort = st.nextToken();
			}
			catch (Exception e)
			{
				System.out.println("Usage: java org.bang.BangDesktop urlproxy:port telnetproxy:port");
				System.exit(0);
			}
		}
	}

	final Bang thisBang = new Bang(proxyHost, proxyPort);

	JInternalFrame frame = new JInternalFrame("bang 0.073", true, true, true, true);

	InternalFrameListener l = new InternalFrameAdapter() {   
		public void internalFrameClosing (InternalFrameEvent e)
		{
			thisBang.stop();
			System.exit(0);
		}};
   
	frame.getContentPane().setLayout(new BorderLayout());
	frame.addInternalFrameListener(l);
	frame.getContentPane().add(thisBang, BorderLayout.CENTER);
	frame.setPreferredSize(new Dimension(640, 640));
	frame.setSize(640, 640);

	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	frame.setLocation(screenSize.width/2 - 640/2,
				screenSize.height/2 - 640/2);
	frame.show();

	desktop = new JDesktopPane();
	desktop.setOpaque(false);
	desktop.add(frame, JLayeredPane.PALETTE_LAYER);  
	Toolkit tk = Toolkit.getDefaultToolkit();
	Dimension d = tk.getScreenSize();
	JFrame f = new JFrame();
	JWindow w = new JWindow(f);
	w.setSize(d);
	w.getContentPane().add(desktop);
	w.setVisible(true);
 }
}
