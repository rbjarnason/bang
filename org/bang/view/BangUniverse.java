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

import java.io.*;
import javax.media.j3d.*;
import javax.vecmath.*;
import com.sun.j3d.utils.geometry.*;
import com.sun.j3d.utils.image.TextureLoader;
import com.sun.j3d.loaders.objectfile.ObjectFile;
import com.sun.j3d.loaders.ParsingErrorException;
import com.sun.j3d.loaders.IncorrectFormatException;
import com.sun.j3d.loaders.Scene;
import com.sun.j3d.utils.geometry.ColorCube;
import com.sun.j3d.audioengines.javasound.*;
import com.sun.j3d.utils.behaviors.mouse.MouseTranslate;
import com.sun.j3d.utils.universe.*; 

/** 
 *   This object is very similar to simple universe. 
 *   It is derived from UsefulUniverse (part of YouBuildItVR):
 *   	@author Chris Heistad (heistad@ncsa.uiuc.edu or heistad@poboxes.com)
 *   	@author Steve Pietrowicz (srp@ncsa.uiuc.edu or spietrowicz@yahoo.com)
 *   @author Robert Vidar Bjarnason (robofly@bang.is)
 */
public class BangUniverse
{
 Canvas3D canvas;
 VirtualUniverse universe;
 Locale locale;
 public TransformGroup vpTrans;
 View view;
 DirectionalLight headLight;
 PhysicalEnvironment environment;
 ViewPlatform vp;
 BranchGroup vpRoot;

/** 
 *   The Construct Method for the BangUniverse
 *   Creates the VirtualUniverse and connected stuff
 */
 public BangUniverse(Canvas3D c)
 {
	this.canvas = c;
	universe = new VirtualUniverse();
	locale = new Locale(universe);

	PhysicalBody body = new PhysicalBody();
	environment = new PhysicalEnvironment();

	view = new View();
	view.setBackClipDistance(100.0);

	view.addCanvas3D(c);
	view.setPhysicalBody(body);
	view.setPhysicalEnvironment(environment);
	view.setWindowMovementPolicy(view.VIRTUAL_WORLD) ;
	view.setWindowResizePolicy(view.VIRTUAL_WORLD);

	vpRoot = new BranchGroup();

	BoundingSphere bounds = new BoundingSphere(new Point3d(0.0,0.0,0.0), 2000000.0);
	vpRoot.setCapability(vpRoot.ALLOW_CHILDREN_EXTEND);
	vpRoot.setCapability(vpRoot.ALLOW_CHILDREN_WRITE);
	vpRoot.setCapability(vpRoot.ENABLE_COLLISION_REPORTING);

	Transform3D t = new Transform3D();
	t.set(new Vector3f(0.0f, 0.0f, 2.0f)); //back just a hair

	//rotate so you are looking into the world and not down the side.
	t.rotY(Math.PI/180 * (-90));
	vp = new ViewPlatform();

	//this is key to the way the LoopingSound works.
	vp.setActivationRadius(1.5f);

	vpTrans = new TransformGroup(t);
	vpTrans.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
	vpTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
	vpTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
	vpTrans.setBounds( new BoundingSphere() );
	vpTrans.addChild(vp);

	//here we set up the headlight
	BoundingSphere lightBounds = new BoundingSphere(new Point3d(), Double.MAX_VALUE);
	headLight = new DirectionalLight();
	headLight.setColor(new Color3f(0.6f, 0.6f, 0.6f));
	headLight.setCapability(Light.ALLOW_STATE_WRITE);
	headLight.setCapability(Light.ALLOW_COLOR_WRITE);
	headLight.setInfluencingBounds(lightBounds);
	vpTrans.addChild(headLight);

	vpRoot.addChild(vpTrans);
	view.attachViewPlatform(vp);
	locale.addBranchGraph(vpRoot);
 }

/** 
 *   Adds an BranchGroup to the universe
 */
 public void addBranchGraph(BranchGroup bg)
 {
	locale.addBranchGraph(bg);
 }

/** 
 *   Repleaces a BranchGroup with another BranchGroup
 */
 public void replaceBranchGraph(BranchGroup theOld, BranchGroup theNew)
 {
	locale.replaceBranchGraph(theOld, theNew);
 }

/** 
 *   Returns the view.
 */
 public View view()
 {
	return view;
 }


/** 
 *   Adds a 3D compass to the Scene
 */
 public Transform3D addCompass()
 {
	BranchGroup pg = new BranchGroup();
	pg.setCapability(pg.ALLOW_DETACH);
 
	TransformGroup moveTG = new TransformGroup();
        moveTG.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        moveTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
 
	Transform3D xForm = new Transform3D();
	xForm.setTranslation(new Vector3d(0.25, -0.05, -1.0));
	xForm.setScale(0.04f);

	TransformGroup placementTG = new TransformGroup(xForm);
	moveTG.addChild(placementTG);

	Appearance app = new Appearance();

	// Globally used colors
	Color3f black = new Color3f(0.0f, 0.0f, 0.0f);
	Color3f white = new Color3f(1.0f, 1.0f, 1.0f);

	// Set up the texture map
	TextureLoader tex = new TextureLoader("content/testX.jpg", canvas);
	app.setTexture(tex.getTexture());
	
	// Set up the material properties
	app.setMaterial(new Material(white, black, white, black, 1.0f));

	// Now load the object files
	Scene s[] = new Scene[1];
	GeometryArray g[] = new GeometryArray[1];
	Shape3D shape[] = new Shape3D[1];
	
	ObjectFile loader = new ObjectFile(ObjectFile.RESIZE);
	s[0] = null;
	g[0] = null;
	shape[0] = null;
	try
	{
		s[0]= loader.load("content/testX.obj");
	}
	catch (FileNotFoundException e)
	{
		System.err.println(e);
	}
	catch (ParsingErrorException e)
	{
		System.err.println(e);
	}
	catch (IncorrectFormatException e)
	{
	System.err.println(e);
	}
	
	BranchGroup b = s[0].getSceneGroup();
	shape[0] = (Shape3D) b.getChild(0);
	g[0] = (GeometryArray) shape[0].getGeometry();
	shape[0].setGeometry(g[0]);
	shape[0].setAppearance(app);
	placementTG.addChild(b);
        vpTrans.addChild(pg);
        return xForm;
 }

/** 
 *   Returns the ViewTransform
 */
 public TransformGroup getViewTransform()
 {
	return vpTrans;
 }

/** 
 *   Returns the headLight
 */
 public DirectionalLight getHeadLight()
 {
	return headLight;
 }

/** 
 *   Returns the locale
 */
 public Locale getLocale()
 {
	return locale;
 }
 
/**
 * This does? the work to set up the 'java sound engine'.
 */
 static protected void enableSound()
 {
	// JavaSoundMixer javaSoundMixer = new JavaSoundMixer(environment);
	// JavaSoundMixer.initialize();
 }
}
