// copyright (c) 1998,1999 Chris Heistad, Róbert Viðar Bjarnason, Steve Pietrowicz
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

import javax.media.j3d.*;
import javax.vecmath.*;
import java.util.*;
import com.sun.j3d.utils.geometry.*;
import java.net.InetAddress;
import java.net.URL;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Panel;
import java.awt.Component;
import com.sun.j3d.utils.image.TextureLoader;
import javax.media.j3d.Texture;
import java.net.MalformedURLException;

import com.sun.j3d.loaders.vrml97.VrmlLoader;
import com.sun.j3d.loaders.vrml97.VrmlScene;
import javax.media.j3d.Node;
import java.net.MalformedURLException;
import java.io.*;
import org.bang.Bang;

/** 
 * This object manages all of the members currently transmitting in this 
 * virtual world.
 * 
 * @author Chris Heistad (heistad@ncsa.uiuc.edu or heistad@poboxes.com)
 * @author Steve Pietrowicz (srp@ncsa.uiuc.edu or spietrowicz@yahoo.com)
 * @author Róbert Bjarnason (robofly@this.is)
 */

public class PeopleManager{

  private boolean useIt = false;
   /*
   * contains the complete listing of InetAddress' that this 
   * people manager knows about
   */
  protected Hashtable people = new Hashtable();

  /*
   * contains the list of BranchGroups that represent the objects
   *	in the people HashTable
   *	this is the actual geometry that is representing the person
   *	it is designed to be dynamically removeable
   */
  protected Hashtable groups = new Hashtable();
  
 /*
   * contains the complete listing of names that this 
   * people manager knows about
   */
  protected Hashtable names = new Hashtable();

 /*
   * contains the complete listing of avatar urls that this 
   * people manager knows about
   */
  protected Hashtable urls = new Hashtable();

  /* The parent branchgroup where all the 'groups' are added.*/

  protected BranchGroup base;

  /* The multicast interface */
  protected Screamer screamer = null;

  /*The transform that is our view transform */
  protected TransformGroup me = null;
	
  /*The name tag */
  protected Text2D shape;
  public Text3D txt;
  public String theTempTXT = "";
  /*
   * The texture cache from where we will get the texture to represent
   * all of the people. We use this so that all textures in this system
   * are only loaded once. This saves a great deal of memory.
   */
 //protected TextureCache cache = null;

  public PeopleManager(BranchGroup bg){
	base = bg;
  }
  
  /**
   *	Allows the multicast interface to be specified.
   *	@param screamer The new multicast interface to use.
   */
  public void setScreamer(Screamer screamer){
	this.screamer = screamer;

	//if I just came online - tell everyone I'm here
	//This could be a BAD thing to do if many people join at once!
	//but if we don't do it, the person doesn't appear until it moves
        //the first time
//	if(screamer!=null && me !=null)
//		screamer.encode(me);//we are sending our view transform
  }

  /**
   *	Allows the view transform to be specified.
   *	@param group The new transform that represents our view transform.
   */
  public void setViewTransform(TransformGroup group){
	me = group;
	//if I just came online - tell everyone I'm here
        //see setScreamer!
	//This could be a bad thing if many people joined at the same time.
//	if(screamer!=null && me !=null)
//		screamer.encode(me);
  }

  /**
   *	Allows another object to specify that a participant is leaving
   *	our virtual world. This function will remove the entry in the
   *	people list. And then remove the geometry from the scene.
   *	@param address The address of the person to remove from the world.
   */
  public void quit(String address){
	Object o = people.remove(address);

	o = groups.get(address);
	people.remove(address);
	names.remove(address);
	urls.remove(address);

	if (o != null) ((BranchGroup)o).detach();//removes the geometry
  }

  /**
   *	Allows a participant's geometry to be updated or added.
   *	The geometry is updated if it has been previously created.
   *	It is created now if it has not been created previously.
   *	@param address The address of the participant to update.
   *	@param transform The position of the address in the world.
   */
  public void update(String address, Transform3D transform){
	Object o = people.get(address);

	
	if(o!=null){
		TransformGroup tg = (TransformGroup) o;
		tg.setTransform(transform);
	}
	
  }
  
  public void setPosition(String address, Vector3f theVector)
  {
    Object o = people.get(address);

    if(o!=null){
	    Transform3D theTransform = new Transform3D();	   
		TransformGroup tg = (TransformGroup) o;
		tg.getTransform(theTransform);
		theTransform.setTranslation(theVector);
		tg.setTransform(theTransform);
	}
  }

  public void setScale(String address, Vector3d theScale)
  {
    Object o = people.get(address);

    if(o!=null){
	    Transform3D theTransform = new Transform3D();	   
		TransformGroup tg = (TransformGroup) o;
		try {
		tg.getTransform(theTransform);
		theTransform.setScale(theScale);
		tg.setTransform(theTransform); 
			}
		catch (Exception e) {
		    System.out.println("The could not be set"); }
	}
  }


  public void setRotation(String address, float[] theRot)
  {
    Object o = people.get(address);

    if(o!=null){
		TransformGroup tg = (TransformGroup) o;
	    Transform3D theTransform = new Transform3D();
		tg.getTransform(theTransform);
		theTransform.setRotation(new AxisAngle4f(theRot));
//	    System.out.println(address + "setTransform: " + theTransform.toString());
		tg.setTransform(theTransform);
	}
  }

  	
  public void onLogin(String address, Transform3D transform, String theName, String avatarURL){
   Object o = people.get(address);
   VrmlScene Vscene = null;
   if (o==null) { 	
	VrmlLoader loader = new VrmlLoader();
	URL mittURL = null;

	try
	{ mittURL = new URL(avatarURL); }
		    catch (MalformedURLException e)
 		   { System.out.println("Bad avatarURL: " + avatarURL); }
	try
	  { 
     		 Vscene = (VrmlScene)loader.load(mittURL);
    	  }
   		 catch (Exception e)
			 { System.out.println("avatarURL is strange >(): " + avatarURL);} 
	 
	
	if (Vscene == null) {
		try
		{	mittURL = new URL("http://and.this.is/javamoo/emu.wrl"); }
	
    			catch (MalformedURLException e2)
   				 { System.out.println("Default avatar gone BAD :(") ;}
	 	 try
	  		{ 
     			 Vscene = (VrmlScene)loader.load(mittURL);
    	 		 }
  			  catch (Exception e2)
			 { System.out.println("avatarURL is strange >(): " + avatarURL);}
			}

	/*
	 * Create the main branch Group
	 */
	TransformGroup tg = new TransformGroup();
	tg.setCapability(tg.ALLOW_TRANSFORM_WRITE);
	tg.setCapability(tg.ALLOW_TRANSFORM_READ);
	tg.setCapability(tg.ALLOW_CHILDREN_EXTEND);

   	BranchGroup bgg = new BranchGroup();
   	bgg.setCapability(bgg.ALLOW_CHILDREN_WRITE);
   	bgg.setCapability(bgg.ALLOW_CHILDREN_EXTEND);
   	bgg.setCapability(bgg.ALLOW_DETACH);

 	tg.setTransform(transform);
	TransformGroup atg = new TransformGroup();
	vrml.BaseNode[] Vobjects = null;

	try{
		Vobjects = Vscene.getObjects();
	}
	catch(Exception e){
	   System.out.println("Couldn't get objects from VRML97 file :(");
	}
	    for (int i = 0; i < Vobjects.length; i++) {
		if (Vobjects[i] != null && Vobjects[i].getType().equals("Transform")) { atg.addChild( (Node)((vrml.node.Node) Vobjects[i]).getImplNode());}
	    }

  	Transform3D atrans = new Transform3D();
  	atrans.rotY((Math.PI/180) * 180);
  	atrans.setTranslation(new Vector3d(0,0,0));
  	atg.setTransform(atrans);

  	atg.setTransform(atrans);
  	tg.addChild(atg);

  	TransformGroup tga = new TransformGroup();
  	Transform3D ta= new Transform3D();
  	ta.rotY((Math.PI/180) * 180);
  	ta.setTranslation(new Vector3d(1,0.5,0));
  	ta.setScale(0.2);
  	tga.setTransform(ta);
  	tg.addChild(tga);
  	
  	bgg.addChild(tg);
  	bgg.compile();
  	base.addChild(bgg);
  	
  	people.put(address,tg);
  	groups.put(address,bgg);
  	names.put(address,theName);
  	urls.put(address,avatarURL);

  	System.out.println("user"+ address + " added");
  	if(screamer!=null && me !=null)
  		screamer.encode(me,screamer.myName+";"+screamer.myURL+";");
   }

  }

public void setName(String theRealName) {
	if (txt != null) { txt.setString(theRealName); }
//		else { theTempTXT = theRealName; }
	}


}









