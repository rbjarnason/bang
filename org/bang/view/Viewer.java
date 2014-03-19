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

package org.bang.view;

import org.bang.view.*;
import org.bang.net.*;
import org.bang.net.vnet.*;
import org.bang.*;
import org.bang.content.*;
import org.bang.agent.Map;
import org.bang.util.*;
import org.bang.net.vnet.J3Dispatcher;
import org.bang.net.J3DeepMatrix;
import org.bang.space.*;

// Basic java classes
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.net.URL;
import java.net.MalformedURLException;
import java.io.*;
import java.lang.reflect.Array;

// Java3D classes
import javax.media.j3d.*;
import javax.vecmath.*;
import com.sun.j3d.utils.image.TextureLoader;
import com.sun.j3d.utils.geometry.ColorCube;
import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.loaders.vrml97.VrmlLoader;
import com.sun.j3d.loaders.vrml97.VrmlScene;
import com.sun.j3d.loaders.vrml97.node.Viewpoint;
import java.util.*;

// we import some swing classes
import javax.swing.*;

/**
 *   This is the main Java3D thread class for Bang.
 *   @authors Sean Brunwin & Robert Vidar Bjarnason (robofly@bang.is)
 */
public class Viewer extends JPanel implements Runnable
{
 public static JLabel loadingLocation;
 public static JProgressBar tempBar;
 public Canvas3D canvas;
 public MovingView view;
 public static Screamer screamer;
 public static J3Dispatcher dispatcher;
 public static J3DeepMatrix deepmatrix;

 public TransformWatcher tw;
 public PositionInterpolator translator;

 static Frame frame = null;
 static JPanel panel;

 public static Bang thisBang;

 public BangUniverse u;
 public TransformGroup vpTempTrans;
 public	static BranchGroup bg;
 public BranchGroup bangSpaceGroup;
 public Transform3D compassGroup;

 public	static TransformGroup vpTrans;
 public static DirectionalLight headLight;
 public static BranchGroup scene;

 static Viewpoint[] fileViewpoints = null;
 static	TransformGroup[] objVpWorldTrans;
 static String[] objVpWorldDesc;

 public boolean notifyOn = false;
 private Thread t;
 public boolean goForward;
 public Thread goForwardRunnable;
 public int runSpeed = 550;
 public float runDirection;
 public boolean goTurn;
 public Thread goTurnRunnable;
 public float turnDirection;

 public String theGUIName = null;
 public String address;
 public String port;
 public String avatarURL;
 public String theMyName;
 public org.bang.space.Dispatcher bangSpaceDispatcher = null;

 public Map theMap;
 private RotPosPathInterpolator rotPos;
 private BranchGroup animateGroup = null;
 public String currentSector;
 public Integer currentSectorInt[];
 String currentSectorString;
 public String lastSector;

 public Hashtable sectorCacheBG = new Hashtable();
 public Hashtable sectorCacheBGtotal = new Hashtable();

/**
 *   The construct method for the 3D scene
 */
 public Viewer(String initURL, Bang thisBang)
 {
	this.thisBang = thisBang;
	
	// We init the main internal sectorCache
	sectorCacheBG = new Hashtable();
	
	// and the object counters for each sector
	sectorCacheBGtotal = new Hashtable();
	
	// this the login .wrl 
	VrmlScene Vscene = null;

	// We start viewer thread
	t = null;
	t = new Thread(this);
	t.setPriority(Thread.MAX_PRIORITY);
	t.start();

	setLayout(new BorderLayout());
	thisBang.progressBar.setValue(++thisBang.currentProgressValue);

	/*
	* Create a Canvas3D.  We'll use this to display the contents of
	* the VR world.
	*/
	canvas = new Canvas3D(null) {
		Dimension prefSize = new Dimension(700, 700);
			public Dimension getPreferredSize()
			{
				return prefSize;
			}};
	canvas.setCursor(new Cursor(Cursor.HAND_CURSOR));

	TransformGroup tg = new TransformGroup();
	tg.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);
	tg.setCapability(TransformGroup.ENABLE_PICK_REPORTING);
	tg.setCapability(TransformGroup.ALLOW_BOUNDS_READ);
	tg.setCapability(TransformGroup.ALLOW_AUTO_COMPUTE_BOUNDS_READ);

	Transform3D t = new Transform3D();
	t.set(new Vector3f(0,0.0f,0));
	tg.setTransform(t);

	thisBang.progressBar.setValue(++thisBang.currentProgressValue);
	thisBang.progressLabel.setText("loading & parsing VRML97 scene ...");

	VrmlLoader loader = new VrmlLoader();
	URL mittURL = null;
	try
	{
		mittURL = new URL(initURL);
	}
	catch (MalformedURLException e)
	{
		System.out.println("Bad URL: " + initURL);
	}
	try
	{
		Vscene = (VrmlScene) loader.load(mittURL);
	}
	catch (Exception e)
	{
		System.out.println("Bad URL: " + initURL);
	}
	if (Vscene != null)
	{
		scene = Vscene.getSceneGroup();
		scene.setCapability(scene.ALLOW_CHILDREN_EXTEND);
		scene.setCapability(scene.ALLOW_CHILDREN_WRITE);
		scene.setCapability(scene.ALLOW_DETACH);
		scene.addChild(tg);

		fileViewpoints = Vscene.getViewpoints();
		try
		{
			vpTempTrans = (TransformGroup) fileViewpoints[0].getTransformGroup();
		}
		catch (Exception e)
		{
			if (vpTempTrans==null)
			{
				Transform3D t3d = new Transform3D();
				t3d.set(new Vector3f(0.0f, 0.0f, 0.0f));
				vpTempTrans = new TransformGroup(t3d);
			}
		}
		thisBang.progressBar.setValue(++thisBang.currentProgressValue);
		thisBang.progressLabel.setText("creating the Java3D universe ...");
		u = new BangUniverse(canvas);
	
		BranchGroup hudGroup = new BranchGroup();
		hudGroup.setCapability(hudGroup.ALLOW_CHILDREN_EXTEND);
		hudGroup.setCapability(hudGroup.ALLOW_CHILDREN_WRITE);
		hudGroup.setCapability(hudGroup.ALLOW_DETACH);

		Material m = new Material(new Color3f(0.0f, 1.0f, 0.0f), new Color3f(0.0f, 0.0f, 0.0f),
					new Color3f(0.0f, 1.0f, 0.0f), new Color3f(1.0f, 1.0f, 1.0f),
							100.0f);
		Appearance a = new Appearance();
		m.setLightingEnable(true);
		a.setMaterial(m);
		Sphere sph = new Sphere(1.0f, Sphere.GENERATE_NORMALS, 80, a);
		hudGroup.addChild(sph);
		
		view = new MovingView(u.getViewTransform());

		vpTrans = u.getViewTransform();
		headLight = u.getHeadLight();
		
		BoundingSphere bounds = new BoundingSphere(new Point3d(0.0,0.0,0.0), 2000000.0);

		// Set up the startup background
		TextureLoader bgTexture = new TextureLoader("content/starfield.jpg", this);
		Background background = new Background(bgTexture.getImage());
		background.setApplicationBounds(bounds);
		scene.addChild(background);

		thisBang.progressBar.setValue(++thisBang.currentProgressValue);

		Transform3D axisOfTranslation = new Transform3D();
		Alpha transAlpha = new Alpha(-1,
					Alpha.INCREASING_ENABLE |
					Alpha.DECREASING_ENABLE,
					0, 0,
					5000, 2000, 2000,
					19000, 16000, 7000);
		axisOfTranslation.rotY(-Math.PI/2.0);
		translator = new PositionInterpolator(transAlpha, vpTrans,
							axisOfTranslation,
							0.62f, 89.5f);

		translator.setSchedulingBounds(new BoundingSphere(new Point3d(), 2000000.0) );
		scene.addChild(translator);

		setViewpoint(vpTempTrans);

//		compassGroup = u.addCompass();

		scene.compile();
		u.addBranchGraph(scene);

		thisBang.progressBar.setValue(++thisBang.currentProgressValue);

		add("Center",canvas);
		show();
	}
  }

/**
 *   Used to turn the view left. Used for Voice naviagtion
 *   trough the JavaSpeech API
 */
 public void turnLeft()
 {
	Transform3D transform2 = new Transform3D();
	vpTrans.getTransform(transform2);

	Transform3D original2 = new Transform3D(transform2);

	Matrix4d rotmat2 = new Matrix4d();
	transform2.get(rotmat2);
	rotmat2.m03 = rotmat2.m13 = rotmat2.m23 = 0.0;
	transform2.set(rotmat2);

	Transform3D trans2 = new Transform3D();

	// Translate to origin
	trans2.setTranslation(new Vector3d(0.0,0.0,0.0));
	trans2.rotY(0.12);
	transform2.mul(trans2);
	original2.mul(trans2);
	vpTrans.setTransform(original2);
 }

/**
 *   Used to turn the view right.
 */
 public void turnRight()
 {
	Transform3D transform3 = new Transform3D();
	vpTrans.getTransform(transform3);
	
	Transform3D original3 = new Transform3D(transform3);
	
	Matrix4d rotmat3 = new Matrix4d();
	transform3.get(rotmat3);
	rotmat3.m03 = rotmat3.m13 = rotmat3.m23 = 0.0;
	transform3.set(rotmat3);
	Transform3D trans3 = new Transform3D();
	
	// Translate to origin
	trans3.setTranslation(new Vector3d(0.0,0.0,0.0));
	trans3.rotY(-1*0.12);
	transform3.mul(trans3);
	original3.mul(trans3);
	vpTrans.setTransform(original3);
 }

/**
 *   Thread that moves the viewer forward. Used for Voice naviagtion
 *   trough the JavaSpeech API.
 */
 public void goForward(int theSpeed, float theDirection)
 {
	this.runSpeed = theSpeed;
	this.runDirection = theDirection;
	goForwardRunnable = new Thread() {
		public void run()
		{
			goForward = true;
			while (goForward)
			{
				Transform3D transform = new Transform3D();
				vpTrans.getTransform(transform);

				Transform3D original = new Transform3D(transform);
				
				Matrix4d rotmat = new Matrix4d();
				transform.get(rotmat);
				rotmat.m03 = rotmat.m13 = rotmat.m23 = 0.0;
				transform.set(rotmat);

				Transform3D trans = new Transform3D();
				trans.setTranslation(new Vector3d(0,0,(runDirection)*0.4));
				transform.mul(trans);
				original.mul(trans);
				float[] array = new float[16];
				original.get(array);
				original.set(array);
				vpTrans.setTransform(original);
				try 
				{
					this.sleep(runSpeed);
				}
				catch (Exception e)
				{}
			} //while
		}}; // goForwardRunnable inner class
	goForwardRunnable.start();
 } // goForward()

/**
 *   Thread that turns the viewer around. Used for Voice naviagtion
 *   trough the JavaSpeech API.
 */
 public void goTurn(float theDirection)
 {
	this.runDirection = theDirection;
	goTurnRunnable = new Thread() {
		public void run()
		{
			goTurn = true;
			while (goTurn)
			{
				Transform3D transform3 = new Transform3D();
				vpTrans.getTransform(transform3);

				Transform3D original3 = new Transform3D(transform3);

				Matrix4d rotmat3 = new Matrix4d();
				transform3.get(rotmat3);
				rotmat3.m03 = rotmat3.m13 = rotmat3.m23 = 0.0;
				transform3.set(rotmat3);

				Transform3D trans3 = new Transform3D();
				trans3.setTranslation(new Vector3d(0.0,0.0,0.0));
				trans3.rotY(turnDirection*0.012);
				transform3.mul(trans3);
				original3.mul(trans3);
				vpTrans.setTransform(original3);
				try
				{
					this.sleep(runSpeed);
				}
				catch (Exception e)
				{}
			} //while
		}}; //goTurnRunnable inner class
	goTurnRunnable.start();
 } //goTurn()

/**
 *   The main run() thread does nothing currently
 */
 public void run() {}

/**
 *   Called to quit everything connected to the 3D scene
 */
 public synchronized void quit()
 {
	if (bg != null)
	{
		bg.detach();
		bg = null;
		tw = null;
	}
	if (theGUIName!=null)
	{
		thisBang.tabbedPane.removeTabAt(thisBang.tabbedPane.indexOfTab(theGUIName));
		theGUIName=null;
	}
	if (screamer != null)
	{
		screamer.quit(); screamer = null;
	}
	if (dispatcher != null)
	{
		dispatcher.onQuit();
		dispatcher = null;
	}
	if (deepmatrix != null)
	{
		deepmatrix.onQuit();
		deepmatrix = null;
	}
	thisBang.space.disableNotify();
 } //quit()

/**
 *   Moves the viewer to the middle of a given Sector
 */
 public void beemToSector(Integer[] theToSector)
 {
	Vector3f theBeemVector = new Vector3f((theToSector[0].floatValue()*100), (theToSector[1].floatValue()*100), (theToSector[2].floatValue()*100));
	Transform3D theBeem = new Transform3D();
	vpTrans.getTransform(theBeem);
	theBeem.setTranslation(theBeemVector);
	vpTrans.setTransform(theBeem);
 }

/**
 *   Set the viewpoint from a TransformGroup
 */
 public void setViewpoint(TransformGroup theVpTg)
 {
 	if (theVpTg!=null)
 	{
		Transform3D theVpLoc = new Transform3D();
		theVpTg.getTransform(theVpLoc);
		vpTrans.setTransform(theVpLoc);
	}
 } //setViewpoint()

/**
 *   Animates the viewpoint from a TransformGroup
 */
 public void animateViewpoint(TransformGroup theVpTg)
 {
	if (theVpTg!=null)
	{ 
		if (animateGroup!=null) animateGroup.detach();
		animateGroup=null;
		rotPos = null;
		Transform3D theVpLoc = new Transform3D();
		theVpTg.getTransform(theVpLoc);

		Transform3D theCurrLoc = new Transform3D();
		vpTrans.getTransform(theCurrLoc);

		Point3f thePos[] = new Point3f[2];
		Quat4f theQuats[] = new Quat4f[2];
		float theKnots[] = new float[2];

		thePos[0] = new Point3f();
		thePos[1] = new Point3f();

		theQuats[0] = new Quat4f();
		theQuats[1] = new Quat4f();

		theKnots[0] = 0.0f;
		theKnots[1] = 1.0f;

		theCurrLoc.get(theQuats[0]);
		theCurrLoc.transform(thePos[0]);

		theVpLoc.get(theQuats[1]);
		theVpLoc.transform(thePos[1]);

		Alpha theAlpha = new Alpha (1,Alpha.INCREASING_ENABLE,0, 0,
						1000, 2000, 4500,
						200, 300, 0);

		Transform3D axisTrans = new Transform3D();

		// axisTrans.rotY(-Math.PI/2.0);
		rotPos = new RotPosPathInterpolator(theAlpha, vpTrans, 
							axisTrans, theKnots,
							theQuats, thePos);

		BranchGroup animateGroup = new BranchGroup();
		animateGroup.setCapability(animateGroup.ALLOW_CHILDREN_EXTEND);
		animateGroup.setCapability(animateGroup.ALLOW_CHILDREN_WRITE);
		animateGroup.setCapability(animateGroup.ALLOW_DETACH);

		rotPos.setSchedulingBounds(new BoundingSphere(new Point3d(), 2000000.0) );
		setViewpoint(theVpTg);
	}
 } //animateViewpoint()


/**
 *   Sending chat string to VNet server
 */
 public void send_vchat(String txt)
 {
 	if (dispatcher != null)
	{
		dispatcher.sendMessage(txt+ "\n");
	}
	if (deepmatrix != null)
	{
		deepmatrix.sendMessage(txt+ "\n");
	}
 }

/**
 *   Writing chat string to the user from VNet server
 */
 public void rec_vchat(String txt)
 {
	if (thisBang != null)
	{
		thisBang.writeToUser("<VChat> " + txt);
	}
 }

/**
 *   Saves a VRML97 URL into bangSpace trough Jini/JavaSpaces.
 *   The reference to the content is saved at the users current 
 *   Transform3D and into the users current sector.
 */
 public void saveVRML97(String theUrl)
 {
	Transform3D temp3Dtrans = new Transform3D();
	vpTrans.getTransform(temp3Dtrans);
	Matrix4f temp3Dmatrix = new Matrix4f();
	temp3Dtrans.get(temp3Dmatrix);
	thisBang.space.writeSectorEntry(thisBang.space.VRML97, theUrl, temp3Dmatrix, "robofly", "");
 }

/**
 *   Saves a Java3D native URL into bangSpace trough Jini/JavaSpaces.
 *   The object is saved at the users current Transform3D and 
 *   into the users current sector.
 */
 public void saveJ3DNative(String theUrl)
 {
	Transform3D temp3Dtrans = new Transform3D();
	vpTrans.getTransform(temp3Dtrans);
	Matrix4f temp3Dmatrix = new Matrix4f();
	temp3Dtrans.get(temp3Dmatrix);
	thisBang.space.writeSectorEntry(thisBang.space.J3DNATIVE, theUrl, temp3Dmatrix, "robofly", "");
  }

/**
 *   Saves a SpaceMark into bangSpace trough Jini/JavaSpaces.
 *   The object is saved at the users current Transform3D and 
 *   into the users current sector.
 */
 public void saveSpaceMark(String theDesc)
 {
	Transform3D temp3Dtrans = new Transform3D();
	vpTrans.getTransform(temp3Dtrans);
	Matrix4f temp3Dmatrix = new Matrix4f();
	temp3Dtrans.get(temp3Dmatrix);
	thisBang.space.writeSectorEntry(thisBang.space.SPACEMARK, "", temp3Dmatrix, "robofly", theDesc);
 }

/**
 *   Saves a bangMap URL & filter into bangSpace trough Jini/JavaSpaces.
 *   The object is saved at the users current Transform3D and 
 *   into the users current sector.
 */
 public void saveTreeMap(String theUrl, String theFilter)
 {
	Transform3D temp3Dtrans = new Transform3D();
	vpTrans.getTransform(temp3Dtrans);
	Matrix4f temp3Dmatrix = new Matrix4f();
	temp3Dtrans.get(temp3Dmatrix);
	thisBang.space.writeSectorEntry(thisBang.space.TREEMAP, theUrl, temp3Dmatrix, "robofly", theFilter);
 }

/**
 *   Loads a VRML97 entry from bangSpace
 *   using Jini/JavaSpaces.
 */
 public void loadVRML97Entry(SectorEntry theEntry)
 {
	VrmlScene Vscene = null;

	System.out.println("Loading vrml97 "+theEntry.url);

	TransformGroup  tg = new TransformGroup();
	tg.setCapability(TransformGroup.ENABLE_PICK_REPORTING);
	tg.setCapability(TransformGroup.ALLOW_BOUNDS_READ);
	tg.setCapability(TransformGroup.ALLOW_AUTO_COMPUTE_BOUNDS_READ);

	Transform3D ttTTtt = new Transform3D(theEntry.myTransform);
	tg.setTransform(ttTTtt);

	VrmlLoader loader = new VrmlLoader();

	URL mittURL = null;
	try
	{
		mittURL = new URL(theEntry.getUrl());
	}
	catch (MalformedURLException e)
	{
		System.out.println("Bad URL: " + theEntry.getUrl());
	}
	try
	{
		Vscene = (VrmlScene)loader.load(mittURL);
	}
	catch (Exception e)
	{
		System.out.println("Bad URL: " + theEntry.url);
	}
	if (Vscene != null)
	{
		vrml.BaseNode[] Vobjects = null;
		try
		{
			Vobjects = Vscene.getObjects();
		}
		catch(Exception e)
		{
			System.out.println("Couldn't get objects from VRML97 file :(");
		}
	for (int i = 0; i < Vobjects.length; i++)
	{
	if (Vobjects[i] != null && Vobjects[i].getType().equals("Transform"))
	{
		tg.addChild( (Node)((vrml.node.Node) Vobjects[i]).getImplNode());
	}
	}

	BranchGroup realRoot = new BranchGroup();
	realRoot.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
	realRoot.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
	realRoot.setCapability(BranchGroup.ALLOW_DETACH);

	System.out.println("Added Vrml97 Scene");

	realRoot.addChild(tg);
	realRoot.compile();

	BranchGroup o = (BranchGroup) sectorCacheBG.get(currentSector);
	o.addChild(realRoot);
	}
 }

/**
 *   Loads a native Java3D  entry from bangSpace
 *   using Jini/JavaSpaces.
 */
 public void loadJ3DNativeEntry(SectorEntry theEntry)
 {
	translator.setEnable(false);
	URL theURL;
	LoadInterface3D theApp = null;
	try
	{
		TransformGroup  tg = new TransformGroup();
		tg.setCapability(TransformGroup.ENABLE_PICK_REPORTING);
		tg.setCapability(TransformGroup.ALLOW_BOUNDS_READ);
		tg.setCapability(TransformGroup.ALLOW_AUTO_COMPUTE_BOUNDS_READ);

		Transform3D ttTTtt = new Transform3D(theEntry.myTransform);
		tg.setTransform(ttTTtt);

		theURL = new URL(theEntry.url);

		URL theURLs[] = new URL[1];
		theURLs[0] = theURL;
		URLClassLoader theTempLoader = new URLClassLoader(theURLs);
		ClassLoader theLoader = (ClassLoader) theTempLoader;
		Class theOApp = theLoader.loadClass(theEntry.aux);
		Object object = null;
		object = theOApp.newInstance();
		theApp = (LoadInterface3D) object;
		theApp.createScene(vpTrans, thisBang.space, canvas);
		BranchGroup objRoot = (BranchGroup) theApp.getScene3D();
		objRoot.setCapability(objRoot.ALLOW_CHILDREN_EXTEND);
		objRoot.setCapability(objRoot.ALLOW_CHILDREN_WRITE);
		objRoot.setCapability(objRoot.ALLOW_DETACH);
		objRoot.addChild(tg);
		objRoot.compile();
	
		System.out.println("Added J3DNative Scene");

		BranchGroup o = (BranchGroup) sectorCacheBG.get(currentSector);
		o.addChild(objRoot);
		
		theGUIName = theApp.getGUIName();

		if (theGUIName!=null)
		{
			theGUIName = theApp.getGUIName();
			thisBang.tabbedPane.addTab(theGUIName,null,theApp.getGUI());
			thisBang.tabbedPane.setSelectedIndex(thisBang.tabbedPane.indexOfTab(theGUIName));
		}
	}
	catch (ClassNotFoundException e)
	{
		System.out.println("Class not found: " + theEntry.aux);
	}
	catch (IllegalAccessException e)
	{
		System.out.println(e);
	}
	catch (InstantiationException e)
	{
		System.out.println(e);
	}
	catch (MalformedURLException e)
	{
		System.out.println("Bad URL: " + theEntry.url);
	}
 } //loadJ3DNative()


/**
 *   Loads an SpaceMark/Viewpoint entry from bangSpace
 *   using Jini/JavaSpaces.
 */
 public void loadSpaceMarkEntry(SectorEntry theEntry)
 {
	thisBang.viewHash.put((String) theEntry.aux, new TransformGroup(new Transform3D(theEntry.myTransform)));
	if (theEntry.aux.equals(""))
	{
		thisBang.viewpoints.addItem(" - no name - ");
	}
	else
	{
		thisBang.viewpoints.addItem(theEntry.aux);
	}
	thisBang.viewpoints.enable();
	thisBang.viewpoints.validate();
	thisBang.miscPanel.validate();
 }


/**
 *   Loads an bangMap entry from bangSpace
 *   using Jini/JavaSpaces.
 */
 public void loadTreeMapEntry(SectorEntry theEntry)
 {
	System.out.println("Loading TreeMap "+theEntry.url);

	TransformGroup  tg = new TransformGroup();
	tg.setCapability(TransformGroup.ENABLE_PICK_REPORTING);
	tg.setCapability(TransformGroup.ALLOW_BOUNDS_READ);
	tg.setCapability(TransformGroup.ALLOW_AUTO_COMPUTE_BOUNDS_READ);

	Transform3D ttTTtt = new Transform3D(theEntry.myTransform);
	tg.setTransform(ttTTtt);

	BranchGroup objRoot = theMap.makeMap(theEntry.url, theEntry.aux, theEntry.myTransform);
	tg.addChild(objRoot);

	BranchGroup realRoot = new BranchGroup();
	realRoot.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
	realRoot.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
	realRoot.setCapability(BranchGroup.ALLOW_DETACH);

	System.out.println("Added TreeMap Scene");

	realRoot.addChild(tg);
	BranchGroup o = (BranchGroup) sectorCacheBG.get(currentSector);
	o.addChild(realRoot);
 }

/**
 *   Sets the current sector.
 */
 public void setCurrentSector(Integer[] theInt)
 {
	currentSector=theInt[0].toString() + theInt[1].toString() + theInt[2].toString();
	currentSectorString="Current Sector is x " + theInt[0].toString() + " y " + theInt[1].toString()+ " z " + theInt[2].toString();
	currentSectorInt = (Integer[]) theInt.clone();
 }

/**
 *   Sets the last sector before the current one.
 */
 public void setLastSector(Integer[] theInt)
 {
	lastSector=theInt[0].toString() + theInt[1].toString() + theInt[2].toString();
 }

/**
 *   Inits the SectorCache
 */
 public void initSector()
 {
	thisBang.space.disableNotify();
	thisBang.theSectorString.setText(currentSectorString); 

	Object ro = sectorCacheBG.get(lastSector);
	BranchGroup o = (BranchGroup) ro;
//	if (o!=null) o.detach();

	System.out.println("** LASTSECTOR " + lastSector); 
    
	Object roo = sectorCacheBG.get(currentSector);
	BranchGroup oo = (BranchGroup) roo;

	if (oo==null)
	{
		BranchGroup tempBG = new BranchGroup();
		tempBG.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
		tempBG.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
		tempBG.setCapability(BranchGroup.ALLOW_DETACH);
		sectorCacheBG.put(currentSector, tempBG);
		bangSpaceGroup.addChild(tempBG);
		Integer totalSectorObjects = new Integer(0);
		sectorCacheBGtotal.put(currentSector, totalSectorObjects); 
	} 
	else
	{
		System.out.println("** Got from cache :D");
		// bangSpaceGroup.addChild(oo);
	}

	System.out.println("After addChild");
	
	loadSectorEntries();
	System.out.println("After loadSectorEntries");

	System.out.println("After if");

	thisBang.space.enableNotify(this, currentSectorInt);
 }
	  
/**
 *   Designed for the sharedState of sectorEntries
 *   will be implemented in beta2 of version 1.1
 */
 public void updateSpacePos(SectorEntry theEntry) {}

/**
 *   Calculates and returns an sector pos from an TransformGroup
 */
 public Integer[] getSectorFromTG(TransformGroup theTG)
 {
	Transform3D theT3D = new Transform3D();
	Vector3f theVector = new Vector3f();

	theTG.getTransform(theT3D);
	theT3D.get(theVector);    
	  
	float theFloat[] = new float[3];
	Float theBigFloat;

	int theInteger;
	Integer theSectorXYZ[] = new Integer[3];

	theVector.get(theFloat);

	for(int i=0; i<3; i++) 
	{
		theFloat[i] = theFloat[i] / 100;
		theBigFloat = new Float(theFloat[i]);
		theSectorXYZ[i] = new Integer(theBigFloat.intValue());
	}
	return theSectorXYZ;     
 }

/**
 *   Calculates and returns an sector pos from an TransformGroup
 */
 public String getSectorStringFromTG(TransformGroup theTG)
 {
	Transform3D theT3D = new Transform3D();
	Vector3f theVector = new Vector3f();

	theTG.getTransform(theT3D);
	theT3D.get(theVector);    
	  
	float theFloat[] = new float[3];
	Float theBigFloat;

	int theInteger;
	String theSectorXYZ = new String("");

	theVector.get(theFloat);
	for(int i=0; i<3; i++) 
	{
		theFloat[i] = theFloat[i] / 100;
		theBigFloat = new Float(theFloat[i]);
		theSectorXYZ = theSectorXYZ + theBigFloat.toString();
	}
	return theSectorXYZ;     
 }

/**
 *   Loads all sector Entries in current sector.
 *   This method is called each time the client recives 
 *   a notify from bangSpace about a new object in the sector
 *   or when the users viewplatform breaks the bounderies of an old sector.   
 */
 public void loadSectorEntries()
 {
	Integer nowTotal = thisBang.space.readSectorCounterEntry(currentSectorInt);
	Object totSec = sectorCacheBGtotal.get(currentSector);
	Integer totalSectorObjects = (Integer) totSec;

	System.out.println("NowTotal="+nowTotal.toString());

	SectorEntry secEnt;
	boolean added = false;
	System.out.println("totalSectorObjects="+totalSectorObjects.toString());
	for(int i=totalSectorObjects.intValue(); i<=nowTotal.intValue(); i++)
	{
		secEnt = thisBang.space.readSectorEntry(new Integer(i), currentSectorInt);
		if (secEnt!=null)
		{
			System.out.println("Secc="+secEnt.toString()+" type="+secEnt.type.toString());
			if (secEnt.type.equals(thisBang.space.SPACEMARK))
				loadSpaceMarkEntry(secEnt);
			if (secEnt.type.equals(thisBang.space.VRML97))
				loadVRML97Entry(secEnt);
			else if (secEnt.type.equals(thisBang.space.J3DNATIVE))
				loadJ3DNativeEntry(secEnt);
			else if (secEnt.type.equals(thisBang.space.TREEMAP))
				loadTreeMapEntry(secEnt);
			added=true;
		}
	}

	if (added)
	{
		Integer totalSObjects = new Integer(nowTotal.intValue()+1);
		System.out.println("totalSObjects="+totalSObjects.toString());
		sectorCacheBGtotal.put(currentSector, totalSObjects);
	}
 }

/**
 *   This method is called the first time after the user has logged on 
 *   the MOO server.
 */
 public void onLogin(String vrmlURL, String address, String port, String theName,
			String avatarURL, String muservice, String fileformat,
			String theJ3Dclass, Bang thisBang)
 {
	theMap = new Map();
	theMap.setViewer(this);

	TransformGroup tempTrans[] = (TransformGroup[]) theMap.getViewpoints();
	Transform3D t3dt = new Transform3D();
	tempTrans[0].getTransform(t3dt);
	vpTrans.setTransform(t3dt);

	bangSpaceGroup = new BranchGroup();
	bangSpaceGroup.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
	bangSpaceGroup.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
	bangSpaceGroup.setCapability(BranchGroup.ALLOW_DETACH);

	this.address = address;
	this.port = port;
	this.avatarURL = avatarURL;
	this.theMyName = theName;
	translator.setEnable(false);
	String ttl = "127";

	u.replaceBranchGraph(scene, bangSpaceGroup);

	scene = bangSpaceGroup;
	
	thisBang.tabbedPane.addTab("Map",null,theMap.getGUI());
	thisBang.tabbedPane.setSelectedIndex(thisBang.tabbedPane.indexOfTab("Map"));

	Integer theSectorXYZ[] = getSectorFromTG(vpTrans);
	setCurrentSector(theSectorXYZ);
	setLastSector(theSectorXYZ);

	initSector();

	// IP Multicast or MBONE
	if (muservice.equals("multicast"))
	{
		setupNetwork(address,port,ttl,theName,avatarURL);
		screamer.encode(vpTrans,theName+";"+avatarURL+";");
		tw = new TransformWatcher(vpTrans,screamer,this);
	}
	// The VNet mu server from http://www.csclub.uwaterloo.ca/~sfwhite/vnet/
	if (muservice.equals("vnet"))
	{
		dispatcher = new J3Dispatcher(address, Integer.parseInt(port),
						theName, avatarURL, this);
		dispatcher.setBranchGroup(scene);
		dispatcher.setMyViewTransform(vpTrans);
		tw = new TransformWatcher(vpTrans,dispatcher,this);
	}
	// The DeepMatrix MUserver from http://www.geomatrix.com/
	if (muservice.equals("deepmatrix"))
	{
		deepmatrix = new J3DeepMatrix(address, Integer.parseInt(port),
						theName, avatarURL, this);
		deepmatrix.setBranchGroup(scene);
		deepmatrix.setMyViewTransform(vpTrans);
		tw = new TransformWatcher(vpTrans,deepmatrix,this);
	}
	bg = new BranchGroup();
	bg.setCapability(bg.ALLOW_DETACH);
	bg.addChild(tw);
	scene.addChild(bg);
 } //endof onLogin()

/**
 *   Sets up the viewpoint menu.
 */
 static public void setupVPmenu()
 {
	int theLength = Array.getLength(fileViewpoints);
	thisBang.viewHash.clear();
	for (int i = 0; i < theLength; i++)
	{
		thisBang.viewHash.put((String) fileViewpoints[i].getDescription(), 
					(TransformGroup) fileViewpoints[i].getTransformGroup());
	}
	thisBang.setupVPmenu();
 } // setupVPmenu


/**
 *   Sets up the multicast network.
 */
 static protected void setupNetwork(String netaddr,String netport,String netTTL, String theName, String avatarURL)
 {
	try
	{
		if ((netaddr == null) || (netport == null) || (netTTL == null))
		{
			System.out.println("properties file must contain network info");
			return;
		}
		screamer = new Screamer(netaddr,netport,netTTL,theName,avatarURL);
	
		//The branch group is set here because internal to the Screamer
		//an object called the PeopleManager needs to add representations
		//of the participants to the scene graph.
		screamer.setBranchGroup(scene);

		//This is needed because the screamer is responsible for sending
		//update messages when our view transform is updated. We will put
		//a transform change watcher on this later and whenever this
		//transform is changed the screamer will send an update to
		//all the other participants.
		screamer.setMyViewTransform(vpTrans);
	}
	catch (Exception e)
	{
		System.out.println(e);
		System.out.println("NETWORK DIED CAN'T CONTINUE....SORRY");
		System.out.println("Please dial 1-800-GET-MBONE :-)");
		System.exit(0);
	}
 } //setupNetwork()
} //Viewer.java
