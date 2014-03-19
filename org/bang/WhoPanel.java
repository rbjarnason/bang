// copyright (c) 1998 Róbert Viðar Bjarnason
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

import java.awt.*;
import java.util.Vector;
import java.net.URL;
import java.net.MalformedURLException;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.border.*;

public class WhoPanel extends JList implements Runnable {

protected Vector whoData = new Vector(19);

// and the thread we will live in
private Thread t;

public void run() {}

public WhoPanel() {
	this.setCellRenderer(new MyCellRenderer());
        this.validate();

	t = new Thread(this);
        t.setPriority(Thread.MIN_PRIORITY);
        t.start();
}

public void delPlayer(String thePlayer) {
		int whereIs = whoData.indexOf(thePlayer);
		if (whereIs != -1) {whoData.removeElementAt(whereIs);}
		this.setListData(whoData);
		this.validate();
		this.repaint();}

public void addPlayer(String thePlayer) {
		whoData.ensureCapacity(20);
		whoData.addElement(thePlayer);
		this.setListData(whoData);
		this.validate();
		this.repaint();}

public void initPlayers(String thePlayers) {
		whoData.removeAllElements();
		whoData.ensureCapacity(20); 
      		int idx = thePlayers.indexOf(',');
      		int oldidx = 0;
      			while(idx >= 0) {
				String match = thePlayers.substring(oldidx, idx);
				whoData.addElement(match);
				oldidx = idx+1;
				idx = thePlayers.indexOf(',', idx+1);
		      			 }      
	       this.setListData(whoData);
		this.validate();
		this.repaint();
	    }

// Display an icon and a string for each object in the list.
public class MyCellRenderer extends JLabel implements ListCellRenderer {
private URL theURL;

public Component getListCellRendererComponent(
JList list,              
Object value,            	// value to display
int index,                  		// cell index
boolean isSelected,        // is the cell selected
boolean cellHasFocus)   // the list and the cell have the focuse
	{

	String path = System.getProperties().getProperty("user.dir") + '/' ;
	path = path.replace(java.io.File.separatorChar, '/');
		   try {theURL = new URL("file:"+ path + "content/icon.gif"); }
		   catch (MalformedURLException e) {
			System.out.println("Bad URL: ");
                   }
       ImageIcon aIcon = new ImageIcon(theURL);
	String s = value.toString();
	setText(s);
	setIcon(aIcon);
	return this;
	}
}
}


