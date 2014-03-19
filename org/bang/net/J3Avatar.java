/**
  Avatar.java

  Copyright (C) 1998 Gerhard Reitmayr

  This source is distributed as semi-opensource software. Please see the
  LICENSE file with this distribution or contact Geometrek (www.geometrek.com)
  for details.
*/

// (undocumented) Changes by Róbert Viðar Bjarnason (robofly@this.is)
// and Sean Edin (sean@bang.is)
package org.bang.net;

import org.bang.view.Viewer;
import org.bang.net.PeopleManager;
import matrix.RealTimeMessage;
import matrix.deck.RealTimeConsumer;
import matrix.deck.ConsumerException;

import matrix.vrml.*;
import java.io.*;
import javax.media.j3d.*;
import javax.vecmath.*;


/**
 *  manages a single avatar
 *
 *  @version 1.0
 */
public class J3Avatar implements RealTimeConsumer 
{
 private String name;
 PeopleManager pm;   //The object we are reporting participant events to.

 private int counter = 0;


    public J3Avatar( String n, String[] url, PeopleManager pm)
 {
  	name = n;
  	this.pm = pm;
  	System.out.println("URL "+url[0]);
  	pm.onLogin(name, new Transform3D(), name , url[0]);
 }

  
 public String getName()
 {
	return name;
 }

 public void handleMessage( RealTimeMessage msg ) throws ConsumerException 
 {
	Field help = msg.getData();
	if(!msg.getReceiver().equals(name))
	throw new ConsumerException(name+" is not equal to "+msg.getReceiver());
	try 
	{
		switch(help.getType())
		{
			case 10: //help.SFVEC3F:
				float[]	values = new float[3];
				values = ((SFVec3f) help).getValue();
	   		pm.setPosition(name,new Vector3f(values[0], values[1], values[2] ));
				break;
			case 6: //help.SFROTATION:
	  		float[] rvalues = new float[4];
	  		rvalues = ((SFRotation)help).getValue();
	  		pm.setRotation(name, rvalues);
				break;
			case 7: //help.SFSTRING:
				break;
			default:
				throw new ConsumerException("Wrong field type for "+name);
		}
    }
    catch(IllegalArgumentException e)
	{
		System.err.println("Error moving "+name+" "+e);
	}
 }

 public void update( boolean now ){}


 public void delete() throws RuntimeException 
 {
	pm.quit(name);
  }
} //end class J3Avatar
