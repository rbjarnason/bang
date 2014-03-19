// copyright (c) 1997,1998, 1999 róbert viðar bjarnason and sean edin
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
import org.bang.view.*;
import org.bang.*;

import java.net.*;
import javax.media.j3d.*;
import javax.vecmath.*;
import org.bang.view.Viewer;
import org.bang.net.PeopleManager;
import matrix.Message;
import matrix.RealTimeMessage;
import matrix.deck.Backend;
import matrix.deck.RealTimeConsumer;
import matrix.deck.ConsumerException;
import matrix.vrml.*;
import java.io.*;
import java.util.*;
import org.bang.net.J3Avatar;
import org.bang.net.vnet.*;

public class J3DeepMatrix implements Observer, RealTimeConsumer
{  
	PeopleManager pm;  //The object we are reporting participant events to.
	TransformGroup me = null;   //Our view platform transform group
	Viewer myViewer;    
	String name;
	String url;
	private Thread t;

	// Matrix stuff
	private Backend net;
	//  private Vector consumers = new Vector();

	public J3DeepMatrix(String hostname, int port, String username, 
												String avatarURL, Viewer theViewer) 
	{
		name = username;
		url = avatarURL;
   	this.myViewer = theViewer;
		System.out.println("Connecting to DeepMatrix server at " + hostname + " @ port " + Integer.toString(port) + " ...");
		try 
		{
			net = new Backend( true );
		}
		catch(IOException e)
		{
			System.err.println("Error opening backbone "+e);
			return;
		}
		// init network and open connection
		net.addObserver( this );

		try 
		{
			net.connect( hostname, port, name, name, "rvbtest.wrl" );
		}
		catch(IOException e)
		{
			System.err.println("Error connecting backbone "+e);
			return;
		}
		Message msg = new Message();
		msg.command = msg.OBJADD;
		msg.name = name;
		msg.arg = url;
		try 
		{
			net.sendMessage( msg );
		}
		catch(IOException e)
		{
			System.err.println(name+" error adding me "+e);
			return;
		}
		net.addConsumer( this );
		Thread t = new Thread();

		//    System.out.println("RotBot "+name+" started");
	}


 public void handleMessage( RealTimeMessage msg ) throws ConsumerException 
 {
	if(!msg.getReceiver().equals(name))
	throw new ConsumerException(name+" matches not "+msg.getReceiver());
	System.out.print(name+".");
 }


  /** 
   * Allows the branch group to be specified. This is the branch group upon
   * which all geometry will hang off. This function also constructs the people
   * manager that will track and represent all of the participants.
   * @param bg The branch group that we will hang all our geometry off.
   */
 public void setBranchGroup(BranchGroup bg)
 {
	pm = new PeopleManager(bg);
	if(me!=null) pm.setViewTransform(me);
 }


  /**
   * Allows our ViewTransform to be set. This is the transform that is sent
   * whenever we move our view point.
   * @param group The nex transform group.
   */
 public void setMyViewTransform(TransformGroup group)
 {
	me = group;
	if(pm!=null)
	pm.setViewTransform(me);
 }



 public void send(int vid, short field, VField value) 
 {
	//	if (thread != null) thread.send(vid, field, value);
 }


    // sendPosition:  called from scene when user moves viewpoint
 public void sendPosition(SFVec3f value)
 {
	RealTimeMessage msg = new RealTimeMessage();
	msg.setReceiver( name );
	msg.setEcho( false );
	msg.setData( new SFVec3f( value ));
	msg.setSequence( msg.getSequence() + 1);
	try 
	{
		net.sendRealTime( msg );
		// System.out.print(" V3 --");
	}
	catch(Exception e)
	{
		System.out.println(name+" error sending rtm\n\t"+e);
	}
 }
    

    // sendOrientation:  called from scene when user turns viewpoint
 public void sendOrientation(SFRotation value)
 {
	RealTimeMessage msg = new RealTimeMessage();
	msg.setReceiver( name );
	msg.setEcho( false );
	msg.setData( new SFRotation( value ));
	msg.setSequence( msg.getSequence() + 1);
	try 
	{
		net.sendRealTime( msg );
	}
	catch(Exception e)
	{
		System.out.println(name+" error sending rtm\n\t"+e);
	}
 }
    

 public void sendMessage(String txt) 
 {
	if(net.isConnected())
	{
		Message msg = new Message();
		msg.name = "ALL";
		msg.command = Message.MSG;
		msg.arg = txt;
		try 
		{
			net.sendMessage(msg);
			myViewer.rec_vchat( msg.arg );
		}
		catch(IOException e)
		{ 
			System.err.println("IOEx in action : "+e);
		}
	}
 }


 public void sendPrivateMessage(int vid, String txt) 
 {
	//	if (thread != null) thread.send(vid, VIP.PRIVATE_MESSAGE,
	//					new VSFString(txt));
 }

 public void scaleAvatar(SFVec3f scaleIt) 
 {
	//	if (thread != null) thread.send(userVid, VIP.SCALE,  scaleIt);
 }

/**
* handles messages from network backend
*/
 public void update( Observable o, Object arg )
 {
	if( arg instanceof Exception )
	{  // we had a problem at the Backend
		onError("Network error : "+arg);
		return;
	}
    Message msg = (Message)arg;
    switch( msg.command )
	{
		case Message.OBJADD :
		String[] help = new String[1];
		help[0] = generateUrl( msg.arg );
		J3Avatar av = new J3Avatar( msg.name, help, pm );
		net.addConsumer( av );
		System.out.println("Avatar "+msg.name+"("+msg.arg+") added");
		break;
		case Message.OBJDEL :
		J3Avatar av2 = (J3Avatar)net.getConsumer( msg.name );
		if( av2 != null )
		av2.delete();
		net.removeConsumer( msg.name );
		System.out.println("Avatar "+msg.name+" removed");
		break;
		case Message.MSG :
		//        gui.display( msg.name, msg.arg );
		myViewer.rec_vchat( msg.arg + "\n");
		break;
		case Message.ERROR :
 			//      gui.display("Error",msg.arg);
 			//	myViewer.rec_vchat("Error: " + msg.arg );
			break;
		case Message.WHOIS :
			break;
		
		case Message.ROOM :
			break;

      case Message.UPDATE :

      case Message.LOGOUT :
			break;

      default :
			System.err.println("unrecognized/not handled Message "+msg);
	}
      return;
 }


/**
 * little helper to construct URLs from DocumentBase.
 * usually used for URLs send to Cosmoplayer. Cosmoplayer 2.0 is inconsistend with
 * the base of relative URLs, so we avoid that by using only absolute ones.
 */
 String generateUrl( String url )
 {
	URL help;
	try 
	{
		help = new URL( url );
	}
	catch( MalformedURLException e )
	{
		System.err.println("URLException "+e);
		return url;
	}
	return help.toExternalForm();
 }


 public synchronized void onQuit() 
 {
	if (net != null) 
	{
		net.removeConsumer( name );
		Message msg = new Message();
		msg.command = msg.OBJDEL;
		msg.name = name;
		msg.arg = "-";
		try 
		{
			net.sendMessage( msg );
		}
		catch(IOException e)
		{
			System.err.println(name+" error deleting me "+e);
			return;
		}
		pm.quit(name);
		pm = null;
		net.disconnect();
		net = null;
	}
 }

 public void onError(String msg) 
 {
	System.out.println("Error:" + msg);    
 }


 public String getName()
 {
	return name;
 }

 public void update( boolean now ){}


 public void delete() throws RuntimeException 
 {
	pm.quit(name);
	net.disconnect();
	net = null;
 }

 public void log(String str) 
 {
	System.err.println(name + ":  " + str);
 }

}
