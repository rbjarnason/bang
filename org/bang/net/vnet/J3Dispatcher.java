// copyright (c) 1997,1998 stephen f. white, róbert viðar bjarnason
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

// changes from the original (Vnet) Dispathcer.java made by <robofly@this.is>
// you can find VNet @ http://www.csclub.uwaterloo.ca/~sfwhite/vnet/
package org.bang.net.vnet;

import java.util.*;
import java.net.*;
import java.io.*;
import javax.media.j3d.*;
import javax.vecmath.*;
import org.bang.net.*;
import org.bang.view.*;
import org.bang.net.vnet.*;
import org.bang.*;

public class J3Dispatcher implements ClientThreadObserver
{
    ClientThread	thread;
	PeopleManager pm;//The object we are reporting participant events to.

    TransformGroup me = null; //Our view platform transform group

	Viewer myViewer;

    String myName;
    String myURL;

    // VID of the player object
    int			userVid;

    // mode for having an out-of-body experience
    boolean		oobe = false;

    // relative position for next OOBE
    VSFVec3f		oobeOffset = new VSFVec3f(0.0F, 0.75F, -3.0F);
    VSFRotation		oobeOrientation = new VSFRotation(0.0F, 1.0F, 0.1F,
							  3.14159F);

    public J3Dispatcher(String hostname, int port,
			String username, String avatarURL, Viewer theViewer) {
     
   		this.myViewer = theViewer;
	    System.out.println("Connecting to VNet server at " + hostname + " @ port " + Integer.toString(port) + " ...");
		thread = new ClientThread(this, hostname, port, username, avatarURL);
		thread.start();
    }


  /** 
   * Allows the branch group to be specified. This is the branch group upon
   * which all geometry will hang off. This function also constructs the people
   * manager that will track and represent all of the participants.
   * @param bg The branch group that we will hang all our geometry off.
   */
    public void setBranchGroup(BranchGroup bg){
  		pm = new PeopleManager(bg);
  		if(me!=null) pm.setViewTransform(me);
  	}


  /**
   * Allows our ViewTransform to be set. This is the transform that is sent
   * whenever we move our view point.
   * @param group The nex transform group.
   */
    public void setMyViewTransform(TransformGroup group){
		me = group;
		if(pm!=null)
			pm.setViewTransform(me);
		  }



    public void send(int vid, short field, VField value) {
	if (thread != null && thread.connected) thread.send(vid, field, value);
    }

    // sendPosition:  called from scene when user moves viewpoint

    public void sendPosition(VSFVec3f value)
    {
	    if (thread != null && thread.connected) { thread.send(userVid, VIP.POSITION, value);
	    /*System.out.println(Integer.toString(userVid) + " " + value.toString());*/ }
//	    if (self != null) self.setField(VIP.POSITION, value);
	    }

    // sendOrientation:  called from scene when user turns viewpoint

    public void sendOrientation(VSFRotation value)
    {
	    if (thread != null && thread.connected) thread.send(userVid, VIP.ORIENTATION, value);
//	    if (self != null) self.setField(VIP.ORIENTATION, value);
	
    }

    

    public void sendMessage(String txt) {
	if (thread != null && thread.connected) thread.send(userVid, VIP.MESSAGE,
					new VSFString(txt));
    }

    public void sendPrivateMessage(int vid, String txt) {
	if (thread != null && thread.connected) thread.send(vid, VIP.PRIVATE_MESSAGE,
					new VSFString(txt));
    }

    public void scaleAvatar(VSFVec3f scaleIt) {
	if (thread != null && thread.connected) thread.send(userVid, VIP.SCALE,
		    scaleIt);
    }

    public synchronized void onQuit() {
	if (thread != null && thread.connected) {
	  thread.send(userVid, VIP.QUIT, null);
      pm.quit(Integer.toString(userVid));
	  thread.connected = false;
	  pm = null;
	  thread = null;
	}
    }

    public void onError(String msg) {
		  System.out.println("Error:" + msg);    }

    // ClientThread notifications
    public void onNetConnect(int vid) {
	if (vid >= 0) {
     	  userVid = vid;
	} else {
	    onError("That name is already in use, or invalid.  Please choose another.");
	    thread = null;
	}
	
    }

    public void setField(int vid, short field, VField value)
    {
    if (vid != userVid) {
		if (field == VIP.POSITION) {
		float[]	values = new float[3];
		values = ((VSFVec3f) value).getValue();
	        pm.setPosition(Integer.toString(vid),new Vector3f(values[0], values[1], values[2] ));
		// System.out.println(vid + " setField: " + Float.toString(values[0]));
			} else if (field == VIP.ORIENTATION) {
			  float[] rrvalues = new float[4];
			  rrvalues = ((VSFRotation)value).getValue();
	    		  pm.setRotation(Integer.toString(vid), rrvalues);
	 	        	} else if (field == VIP.SCALE) {
			  	  float[] values = new float[3];
			  	  values = ((VSFVec3f) value).getValue();
		          	  pm.setScale(Integer.toString(vid),new Vector3d(values[0], values[1], values[2]));
			    }
    else {
	    if (field == VIP.POSITION) {
	    Transform3D theTransform = new Transform3D();
	    me.getTransform(theTransform);
	    float[] values = new float[3];
		values = ((VSFVec3f) value).getValue();
	    theTransform.setTranslation(new Vector3f(values[0], values[1], values[2]));
	    	} else if (field == VIP.ORIENTATION) {
			float[]	rrrvalues = new float[4];
			rrrvalues = ((VSFRotation)value).getValue();
	    		Transform3D theTransform = new Transform3D();
	   		 me.getTransform(theTransform);
	  		  theTransform.setRotation(new Quat4f(rrrvalues[0], rrrvalues[1], rrrvalues[2], rrrvalues[3] ));	    
			}
	   	}
}		
  }  
    public void onNetInput(int vid, short field, VField value) {
	if (field >= 0) {
	    setField(vid, field, value);
	} else switch (field) {
	  case VIP.ADD_OBJECT:
	    String url = ((VSFString) value).getValue();
	    log("Add Object URL: " + url);
	 	if (vid != userVid) pm.onLogin(Integer.toString(vid), new Transform3D(), Integer.toString(vid) , url);
	    break;
	  case VIP.REMOVE_OBJECT:
	    pm.quit(Integer.toString(vid));
	    break;
	  case VIP.MESSAGE:
	  	myViewer.rec_vchat(((VSFString) value).getValue());
	  case VIP.PRIVATE_MESSAGE:
//	    talker.appendText(((VSFString) value).getValue());
	    break;
	  case VIP.CREATE_OBJECT:
	    break;
	  case VIP.USER_INFO:
	    pm.setName(((VSFString) value).getValue());
	    break;
	}
    }

    public void onNetDisconnect() {
	pm = null;
	
    }

    private void addObject(VRMLObject obj)
    {
    }

    private void removeObject(VRMLObject obj)
    {
    }

    private void removeAllObjects()
    {
    }

    public void log(String str) {
	System.err.println(userVid + ":  " + str);
    }

    public void createObject() {
	if (thread != null && thread.connected) thread.send(userVid, VIP.CREATE_OBJECT, null);
    }

    public void deleteObject(int vid) {
	if (thread != null && thread.connected) thread.send(vid, VIP.REMOVE_OBJECT, null);
    }

    public void changeURL(int vid, String url) {
	if (thread != null && thread.connected) thread.send(vid, VIP.ADD_OBJECT, new VSFString(url));
    }


}











