// copyright (c) 1998, 1999 Róbert Viðar Bjarnason and Brunwin
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
package org.bang.net;

// importing basic java classes
import java.awt.*;
import java.awt.event.*;
import java.awt.Toolkit;
import java.io.*;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.net.MalformedURLException;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.StringTokenizer;
// we import some swing classes
import javax.swing.text.*;

import org.bang.WhoPanel;
import org.bang.Bang;

public class MooConnection implements Runnable 
{
	// the moo server
	protected Socket mooserver;
	protected DataInputStream sti;
	protected PrintStream sto;
 
	public Bang thisBang;
 
	// we track and parse commands using an inner class
	public executedCommands eCom = new executedCommands();
	public String theCommand;
 
	// are we connected ?
	private boolean connected = false; 

	// and the thread we will live in
	private Thread t;

	// is this a new user ?
	private String createnew;

 public MooConnection(Bang theMoo) 
 {
	thisBang= theMoo;	
 }
 
 public void start()
 {
 }

 public final void stop()
 {
	disconnect();
 }
 
 // keep reading from the moo server until our thread dies
 public void run()  
 {
	while(t != null)
	{
		try
		{
		    if (sti.available() > 0 )
			{
				String inputString = sti.readLine() + "\n";                       
				if (inputString.indexOf("#z#") != -1)
				{
					eCom.addCommand(inputString);
				} 
				else
				{
					appendText(inputString);
				}
			}
		}
		catch(IOException e) 
		{
			System.out.println("Couldn't read from moo server!"); 
		}
	}
 }
 
 // connect to moo server
 public void connect(String proxyHost, String proxyPort)
 {
	String mooaddress;
	int mooport;
	if (proxyHost!=null && proxyPort!=null) 
	{
		mooaddress = proxyHost;
		mooport = new Integer(proxyPort).intValue();
	}
	else 
	{
		mooaddress = "and.this.is";
		mooport = 7777;
	}
	if (!connected) 
	{
		try 
		{
			mooserver = new Socket(mooaddress,mooport);
			connected = true;
			sti = new DataInputStream(new BufferedInputStream(mooserver.getInputStream()));
			sto = new PrintStream (new BufferedOutputStream(mooserver.getOutputStream(),1024),false);
		}
		catch(IOException e)
		{
			System.err.println("error connecting to moo!");
		}
		if (proxyHost!=null && proxyPort!=null) 
		{
//			writeToSocket("and.bang.is 7777");
		}
		t = new Thread(this);
		t.setPriority(Thread.MIN_PRIORITY);
		t.start();
	}
 }
   
 // disconnect from moo server
 public boolean disconnect()
 {
	if(sti == null) 
	{
		System.err.println("no connection to moo!");
		return false;
	}
    try 
	{
		connected = false; 
		t = null;
		sto.println("@quit");
		sto.flush(); 
		if (sto != null) sto.close(); 
		if (sti != null) sti.close();
		if (mooserver != null) mooserver.close(); 
	} 
	catch(Exception e) 
	{
		System.err.println("problem disconnecting to moo!");
		e.printStackTrace();
		sti = null;
		sto=null; 
		mooserver=null;
		return false;
    }
	return true;
 }
  
 // send a string to the moo server
 public void writeToSocket(String str)
 {
	if (connected) 
		try 
		{
   		sto.println(str);
			sto.flush();
		} 
		catch(Exception e) 
		{
			System.err.println("couldn't send to moo -disconnecting!");
			disconnect();
		}
 }

 
 public void setConnected(int vid) 
 {
	connected = vid != -1;
	if (connected) 
	{
	} 
	else 
	{
	}
 }
	// internal class keeps tracks of #z# commands from the LambdaMoo server
 public class executedCommands extends Vector 
 {
	private URL theFUrl;

	public void executedCommands() 
	{
	}
        
	public void addCommand (String theCommand) 
	{
		if (theCommand.indexOf("#z#vconnect") != -1) 
		{
			int startToken = theCommand.indexOf("<");
			int endToken = theCommand.lastIndexOf(">");
			if (startToken != -1 && endToken != -1) 
			{
				String theVconnect = (theCommand.substring(startToken+1,endToken));
				int idx = theVconnect.indexOf(';');
				int oldidx = 0;
				String theURL = theVconnect.substring(oldidx, idx);
				oldidx = idx+1;
				idx = theVconnect.indexOf(';', idx+1);
				String theServer = theVconnect.substring(oldidx, idx);
				oldidx = idx+1;
				idx = theVconnect.indexOf(';', idx+1);
				String  thePort = theVconnect.substring(oldidx, idx);
				oldidx = idx+1;
				idx = theVconnect.indexOf(';', idx+1);
				String theName = theVconnect.substring(oldidx, idx);
				oldidx = idx+1;
				idx = theVconnect.indexOf(';', idx+1);
				String theAvatar = theVconnect.substring(oldidx, idx);
				oldidx = idx+1;
				idx = theVconnect.indexOf(';', idx+1);
				String theJ3Dclass = theVconnect.substring(oldidx, idx);
				String whatItIs = theURL.substring(theURL.length()-3);
				if (whatItIs.equals("wrl")) 
				{
					thisBang.viewer.onLogin(theURL, theServer, thePort, theName, theAvatar, "vnet","wrl", "", thisBang); 
				}
				else 
				{
					thisBang.viewer.onLogin(theURL, theServer, thePort, theName, theAvatar, "vnet","j3d", theJ3Dclass, thisBang);
				}
			}      
		}
		if (theCommand.indexOf("#z#mconnect") != -1) 
		{
			int startToken = theCommand.indexOf("<");
			int endToken = theCommand.lastIndexOf(">");
			if (startToken != -1 && endToken != -1) 
			{
				String theVconnect = (theCommand.substring(startToken+1,endToken));
				int idx = theVconnect.indexOf(';');
				int oldidx = 0;
				String theURL = theVconnect.substring(oldidx, idx);
				oldidx = idx+1;
				idx = theVconnect.indexOf(';', idx+1);
				String theServer = theVconnect.substring(oldidx, idx);
				oldidx = idx+1;
				idx = theVconnect.indexOf(';', idx+1);
				String  thePort = theVconnect.substring(oldidx, idx);
				oldidx = idx+1;
				idx = theVconnect.indexOf(';', idx+1);
				String theName = theVconnect.substring(oldidx, idx);
				oldidx = idx+1;
				idx = theVconnect.indexOf(';', idx+1);
				String theAvatar = theVconnect.substring(oldidx, idx);
				//   thisBang.viewer.loadRoom3D(theURL);
			}      
		}
		if (theCommand.indexOf("#z#showApp3D") != -1) 
		{
			int startToken = theCommand.indexOf("<");
			int endToken = theCommand.lastIndexOf(">");
			if (startToken != -1 && endToken != -1) 
			{
				String theApp3D = (theCommand.substring(startToken+1,endToken));
				int idx = theApp3D.indexOf(';');
				int oldidx = 0;
				String theURL = theApp3D.substring(oldidx, idx);
				oldidx = idx+1;
				idx = theApp3D.indexOf(';', idx+1);
				String theName = theApp3D.substring(oldidx, idx);
				thisBang.viewer.quit();
				// thisBang.viewer.loadApp3D(theURL, theName, false);
			}      
		}
		if (theCommand.indexOf("#z#videochat") != -1) 
		{
			int startToken = theCommand.indexOf("<");
			int endToken = theCommand.lastIndexOf(">");
			if (startToken != -1 && endToken != -1) 
			{
				String theVideoChat = (theCommand.substring(startToken+1,endToken));
				int idx = theVideoChat.indexOf(';');
				int oldidx = 0;
				String theUrl = theVideoChat.substring(oldidx, idx);
				oldidx = idx+1;
				idx = theVideoChat.indexOf(';', idx+1);
				String thePort = theVideoChat.substring(oldidx, idx);
				if (!theUrl.equals("") && !thePort.equals("")) 
				{
					thisBang.goVideo(theUrl, thePort);
				} 
				else 
				{ 
					System.out.println("Player does not have a valid RTP address or port ;("); 
				}
			}      
		}
		if (theCommand.indexOf("#z#editpad") != -1) 
		{
			int startToken = theCommand.indexOf("<");
			int endToken = theCommand.lastIndexOf(">");
			if (startToken != -1 && endToken != -1) 
			{
				String theNoteText = (theCommand.substring(startToken+1,endToken));
 				thisBang.setMooText(theNoteText.replace(';','\n'));
			}
		}
		if (theCommand.indexOf("#z#saveVRML") != -1) 
		{
			int startToken = theCommand.indexOf("<");
			int endToken = theCommand.lastIndexOf(">");
			if (startToken != -1 && endToken != -1) 
			{
				String theUrl = (theCommand.substring(startToken+1,endToken));
				// thisBang.viewer.saveVRML(theUrl);
			}
		}
		if (theCommand.indexOf("#z#dconnect") != -1) 
		{
			int startToken = theCommand.indexOf("<");
			int endToken = theCommand.lastIndexOf(">");
			if (startToken != -1 && endToken != -1) 
			{
				String theVconnect = (theCommand.substring(startToken+1,endToken));
				int idx = theVconnect.indexOf(';');
				int oldidx = 0;
				String theURL = theVconnect.substring(oldidx, idx);
				oldidx = idx+1;
				idx = theVconnect.indexOf(';', idx+1);
				String theServer = theVconnect.substring(oldidx, idx);
				oldidx = idx+1;
				idx = theVconnect.indexOf(';', idx+1);
				String  thePort = theVconnect.substring(oldidx, idx);
				oldidx = idx+1;
				idx = theVconnect.indexOf(';', idx+1);
				String theName = theVconnect.substring(oldidx, idx);
				oldidx = idx+1;
				idx = theVconnect.indexOf(';', idx+1);
				String theAvatar = theVconnect.substring(oldidx, idx);
				thisBang.viewer.onLogin(theURL, theServer, thePort, theName, theAvatar, "deepmatrix", "wrl","",thisBang);
			}
		}
		if (theCommand.indexOf("#z#beep") != -1) 
		{ 
			Toolkit.getDefaultToolkit().beep(); 
		}
		if (theCommand.indexOf("#z#goVoice") != -1) 
		{ 
			thisBang.goVoice(); 
		}
		if (theCommand.indexOf("#z#noVoice") != -1) 
		{ 
			thisBang.noVoice(); 
		}
		if (theCommand.indexOf("#z#OOBE") != -1) 
		{
			/* dispatcher.toggleOOBE();*/ 
		}
		if (theCommand.indexOf("#z#newroom") != -1) 
		{
			int endToken = theCommand.indexOf("#");
			String theNname = theCommand.substring(0,endToken-1);
			thisBang.roomLocation.setText(theNname);
			thisBang.infoPanel.validate();
			thisBang.whoList.initPlayers(" ");
			thisBang.objList.initPlayers(" ");
			thisBang.objList.validate();
			thisBang.wscrollPane.validate();
			thisBang.oscrollPane.validate(); 
		}
		if (theCommand.indexOf("#z#ishere") != -1) 
		{ 
			int endToken = theCommand.indexOf("#");
			String theSname = theCommand.substring(0,endToken-1);
			thisBang.whoList.addPlayer(theSname);
			thisBang.wscrollPane.validate(); 
		}
		if (theCommand.indexOf("#z#vchat") != -1) 
		{ 
			int endToken = theCommand.indexOf("#");
			String theTxt = theCommand.substring(0,endToken-1);
			thisBang.viewer.send_vchat(theTxt);
		}
		if (theCommand.indexOf("#z#washere") != -1) 
		{
			int endToken = theCommand.indexOf("#");
			String theNname = theCommand.substring(0,endToken-1);
			thisBang.whoList.delPlayer(theNname);
			thisBang.wscrollPane.validate(); 
		}
		if (theCommand.indexOf("#z#connect") != -1) 
		{
			int endToken = theCommand.indexOf("#");
			String theCname = theCommand.substring(0,endToken-1);
			thisBang.whoList.addPlayer(theCname);
			thisBang.wscrollPane.validate();
		}
		if (theCommand.indexOf("#z#disconnect") !=-1) 
		{
			int endPoint = theCommand.lastIndexOf("#");
			String theDname = null;
			theDname = theCommand.substring(0, endPoint-1);
			thisBang.whoList.delPlayer(theDname);
			thisBang.wscrollPane.validate(); 
		}
     	/*   if (theCommand.indexOf("#z#showurl") != -1) 
		{
			int whereUrlStarts = theCommand.indexOf("<");
			int whereUrlEnds = theCommand.lastIndexOf(">");
			if (whereUrlStarts != -1 && whereUrlEnds != -1) 
			{
				String theUrl =theCommand.substring(whereUrlStarts+1,whereUrlEnds);
				System.out.println("theUrl="+theUrl);
			}          
		}*/
		if (theCommand.indexOf("#z#listPlayers") != -1) 
		{
			int startToken = theCommand.indexOf("<");
			int endToken = theCommand.lastIndexOf(">");
			if (startToken != -1 && endToken != -1) 
			{
				String thePlayers = (theCommand.substring(startToken+1,endToken)+",");
				thisBang.whoList.initPlayers(thePlayers);
				thisBang.wscrollPane.validate();
			}
		}
		if (theCommand.indexOf("#z#listObjects") != -1) 
		{
			int startToken = theCommand.indexOf("<");
			int endToken = theCommand.lastIndexOf(">");
			if (startToken != -1 && endToken != -1) 
			{
				String theObjects = (theCommand.substring(startToken+1,endToken)+",");
				thisBang.objList.initPlayers(theObjects);
				thisBang.oscrollPane.validate();
			}
		}        
		if (theCommand.indexOf("#z#listInv") != -1) 
		{
			int startToken = theCommand.indexOf("<");
			int endToken = theCommand.lastIndexOf(">");
			if (startToken != -1 && endToken != -1) 
			{
				String theInv = (theCommand.substring(startToken+1,endToken)+",");
				thisBang.invList.initPlayers(theInv);
				thisBang.iscrollPane.validate();
			}
		}       
		ensureCapacity(2);
		addElement(theCommand); 
	}
 } // end of internal class
  
 public void appendText(String str)
 {
	thisBang.writeToUser(str);
 }
}
