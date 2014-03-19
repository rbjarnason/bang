// copyright (c) 1999 Sean Edin, Róbert Viðar Bjarnason, Daniel Piczak, Sean Brunwin
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

package org.bang.agent;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.border.*;
import javax.swing.undo.*;
import com.sun.j3d.utils.geometry.*;
import com.sun.j3d.utils.universe.*;
import javax.media.j3d.*;
import javax.vecmath.*;
import java.util.*;
import java.net.*;
import org.bang.space.MapEntry;
import org.bang.jini.BangSpace;
import org.bang.util.MemoryMonitor;
import org.bang.util.TreeClump;
import org.bang.util.BrowserControl;
import org.bang.util.*;
import org.bang.jini.*;


import java.util.*;
import java.awt.*;
import java.awt.event.*;
import org.bang.view.Viewer;

/** 
 *   This is the mapAgent, read MapEntry 's and draws bangMaps
 *   @author Sean Edin (sean@bang.is)
 *   @author Robert Vidar Bjarnason (robofly@bang.is)
 *   @author Daniel Piczak
 */
public class Map implements Runnable
{
 public BranchGroup theScene;
 public BranchGroup cubeBG;
 public BranchGroup lineBG;

 public BangSpace space;
 public Canvas3D theCanvas;

 public TransformGroup usersViewTrans;
 public SharedGroup normBox;
 public SharedGroup colorBox;

 public JLabel clickedURL;

 JButton insertButton;
 JButton updateButton;
  
 String theFilterUrl;
 float x=0.0f, y=0.0f, z=0.0f;
 float DEPTH=12.0f;
 public java.util.List toDraw;
 private Thread t;
 public Map myMap;
 public BrowserControl theDefaultBrowser;
 public JTextField textInput;
 public JTextField textFilterInput;

 public Viewer theViewer;

 public void stop() {}

 public void run() {}

 public void start() {}

 public void setViewer(Viewer thisViewer)
 {
	this.theViewer = thisViewer;
 }

 public BranchGroup makeMap(String theUrl, String theFilter, Matrix4f theEntryPos)
 {
	this.space = theViewer.thisBang.space;
	this.theCanvas = theViewer.canvas;
	this.myMap = this;

	theScene = new BranchGroup();
	theScene.setCapability(theScene.ALLOW_DETACH);
	theScene.setCapability(theScene.ALLOW_CHILDREN_EXTEND);
	theScene.setCapability(theScene.ALLOW_CHILDREN_WRITE);
	theScene.setCapability(theScene.ENABLE_PICK_REPORTING);
    
	cubeBG = new BranchGroup();
	cubeBG.setCapability(cubeBG.ALLOW_DETACH);
	cubeBG.setCapability(cubeBG.ALLOW_CHILDREN_EXTEND);
	cubeBG.setCapability(cubeBG.ALLOW_CHILDREN_WRITE);
	cubeBG.setCapability(cubeBG.ENABLE_PICK_REPORTING);

	lineBG = new BranchGroup();
	lineBG.setCapability(lineBG.ALLOW_DETACH);
	lineBG.setCapability(lineBG.ALLOW_CHILDREN_EXTEND);
	lineBG.setCapability(lineBG.ALLOW_CHILDREN_WRITE);
	lineBG.setCapability(lineBG.ENABLE_PICK_REPORTING);

	Color3f eColor    = new Color3f(0.0f, 0.0f, 0.0f);
	Color3f sColor    = new Color3f(1.0f, 1.0f, 1.0f);
	Color3f objColor  = new Color3f(0.6f, 0.6f, 0.6f);
	Color3f lColor1   = new Color3f(1.0f, 0.0f, 0.0f);
	Color3f lColor2   = new Color3f(0.0f, 1.0f, 0.0f);
	Color3f alColor   = new Color3f(0.2f, 0.2f, 0.2f);
	Color3f bgColor   = new Color3f(0.05f, 0.05f, 0.2f);

	normBox = new SharedGroup();
	colorBox = new SharedGroup();
	normBox.setCapability(normBox.ENABLE_PICK_REPORTING);
	colorBox.setCapability(colorBox.ENABLE_PICK_REPORTING);
	Material m = new Material(objColor, eColor, objColor, sColor, 100.0f);
	Appearance a = new Appearance();
	m.setLightingEnable(true);
	a.setMaterial(m);
	
	//Sphere sph = new Sphere(0.99f, SpherE.GENERATE_NORMALS, 60, a);
	Tetrahedron cube = new Tetrahedron();

	// cube.setappearance(
	normBox.addChild(cube);

	ColorCube theColBox = new ColorCube();
	colorBox.addChild(theColBox);
	normBox.compile();
	colorBox.compile();
	theDefaultBrowser = new BrowserControl();
	t = new Thread(this);
	t.setPriority(Thread.MIN_PRIORITY);
   	t.start();
	this.theFilterUrl = theFilter;
	Vector vectorToDraw;
	Vector vectorToDrawPos;
	Vector vectorDrawn;
	// initialize search data structures
	vectorToDraw = new Vector();
	vectorToDrawPos = new Vector();
	vectorDrawn = new Vector();
	// initialize search data structures
	vectorToDraw.removeAllElements();
	vectorToDrawPos.removeAllElements();
	vectorDrawn.removeAllElements();
	vectorToDraw.addElement(theUrl);
	// vectorToDrawPos.addElement(theEntryTrans);
	vectorToDrawPos.addElement(new Transform3D(theEntryPos));
	//ListIterator listIter = listToDraw.listIterator(listToDraw.size());
	// System.out.println("pre-while: 0");
	while (vectorToDraw.size() > 0 && space.online)
	{
		String urlDraw = (String) vectorToDraw.elementAt(0);
		Transform3D posDraw = (Transform3D) vectorToDrawPos.elementAt(0);
		vectorToDraw.removeElementAt(0);
		vectorToDrawPos.removeElementAt(0);
		// Random randGen = new Random();
		// DEPTH = ((randGen.nextFloat()*10)+2);
		cubeBG.setCapability(cubeBG.ALLOW_DETACH);
		cubeBG.setCapability(cubeBG.ALLOW_CHILDREN_EXTEND);
		cubeBG.setCapability(cubeBG.ALLOW_CHILDREN_WRITE);
		cubeBG.setCapability(cubeBG.ENABLE_PICK_REPORTING);
		//  if (DEPTH>10) DEPTH=DEPTH-2;
		//listIter.remove();
		//System.out.println("pre-space read: 1x");
		MapEntry theEntry = space.readMapEntry(urlDraw);
		if (theEntry!=null)
		{
		vectorDrawn.addElement(urlDraw);
		// System.out.println("post-space read: " + theEntry.theChildren.toString());
		ListIterator iter = theEntry.theChildren.listIterator();
  		Iterator iter1 = theEntry.theChildrenTitle.iterator();
        	int childCounter = 0;
        int numChildren = theEntry.theChildren.size();
    //    System.out.println("got children: 3x");
        while (iter.hasNext())
        {
         // System.out.println("child while: 4");
          String childUrl = (String) iter.next();
          String childTitle;
          try
          {
          childTitle = (String) iter1.next();
          }
          catch (Exception e)
          {
        //    System.out.println("null childTitle");
            childTitle=childUrl;
          }

          childCounter++;
       //   System.out.println("pre-draw: 5");

          int theLineColor = 0;

          if (!theFilterUrl.equals(""))
          {
            if (!childUrl.startsWith(theFilterUrl))
            {
              theLineColor = 1;
            }
          }
	Transform3D childPos = null;  
	if (!vectorDrawn.contains(childUrl)) {
             childPos = draw(childTitle, posDraw, childCounter, numChildren, theLineColor);
	  }   
      // System.out.println("post draw: 6");

            // mark the URL as searched (we want this one way or the other)

          if (!vectorDrawn.contains(childUrl)
			      && !vectorToDraw.contains(childUrl))
          {
            if (!theFilterUrl.equals(""))
            {
              if (childUrl.startsWith(theFilterUrl))
              {
                vectorToDraw.addElement(childUrl);
                vectorToDrawPos.addElement(childPos);
              }
            }
            else
            {
              vectorToDraw.addElement(childUrl);
              vectorToDrawPos.addElement(childPos);
            }
          }


          //listIter.add(tempObjToDraw);
          //System.out.println("ListIter: "+listIter.toString());
          // istToDraw.add(objToDraw);
        } // while more children in current item
     }
    } // while more toDraw items
    System.out.println("done with url");

BoundingSphere bounds =
	    new BoundingSphere(new Point3d(0.0,0.0,0.0), 100.0);

   PickUrlBehavior pickUrl = new 
	  PickUrlBehavior(theCanvas, cubeBG, bounds, myMap);

    cubeBG.compile();
    lineBG.compile();
    theScene.addChild(cubeBG);
    theScene.addChild(lineBG);

 
    return theScene;
  } //makeMap()



  Transform3D draw(String childTitle, Transform3D position,
                    int childCounter, int numChildren, int theLineColor)
  {
    /* position on the (x,y,z) axis */
    float[] parentArray = new float[3];

    Vector3f vecPos = new Vector3f();
    position.get(vecPos);
    vecPos.get(parentArray);

    float parent_x=parentArray[0];
    float parent_y=parentArray[1];
    float parent_z=parentArray[2];

    /* form a circle (cone):
    we know number of nodes in list, calculate radius and angle */
    float 	angle;
    float 	radius;
    org.bang.util.TreeClump newClump;

    /* down a level, move further away on z axis */
    z = parent_z + DEPTH;


    if (numChildren == 0) return null;

    if (numChildren > 2)
    {
      angle=(float)(Math.PI * 2/numChildren);
      radius=(float)(1.0/(2.0 * Math.sin(angle)));
    }
    else if (numChildren == 2)
    {
      angle=(float) Math.PI;
      radius=2.0f;
    }
    else
    {
      // if there is one file or directory
      angle=0.0f;
      radius=0.1f;
    }
    if(angle != 0.0f)
    {
      x=(float)((radius * Math.sin(angle * childCounter))+parent_x
			+ (Math.cos (angle)/radius));
      y=(float)((radius * Math.cos(angle * childCounter))+parent_y
			+ (Math.sin (angle)/radius));
      /*if (parent_x < 0)
        x -= radius;
      else if (parent_x > 0)
        x += radius;
      if (parent_y < 0)
        y -= radius;
      else if (parent_y > 0)
        y += radius; */
    }
    else
    {
      x=0.0f+parent_x;
      y=0.0f+parent_y;
    }

    Transform3D childPosition = new Transform3D(position);
    childPosition.setTranslation(new Vector3f(x,y,z));

    /* create a new vrml node at specified x,y,z translation with known title & url
		       as specified in TreeNode treenode instance */


    newClump=new org.bang.util.TreeClump(childTitle, childPosition, cubeBG, lineBG, theCanvas,
                                          parent_x, parent_y, parent_z, theLineColor,
				 normBox, colorBox, myMap);
   // System.out.println("Parent: "+position.toString());
   // System.out.println("Child: "+childPosition.toString());
    return childPosition; // return number of nodes drawn
  } // end of function draw


  public JPanel getGUI()
  {
    JPanel scalePanel = new JPanel();
		scalePanel.setLayout(new BoxLayout(scalePanel, BoxLayout.Y_AXIS));
		scalePanel.setBackground(Color.white);
    // scalePanel.add(Box.createRigidArea(new Dimension(44, 0)));
    JPanel controlPanel = new JPanel();
    controlPanel.setLayout(new BorderLayout());
    controlPanel.setBorder(new TitledBorder(new EtchedBorder(), "Map Controls"));

    JPanel rootUrlPanel = new JPanel();
    rootUrlPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
    JLabel theLabel = new JLabel("Root URL:");
    rootUrlPanel.add(theLabel);
    textInput = new JTextField(15);
    rootUrlPanel.add(textInput);
    controlPanel.add(rootUrlPanel, BorderLayout.NORTH);

    JPanel filterUrlPanel = new JPanel();
    filterUrlPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
    JLabel theFilterLabel = new JLabel("Filter URL:");
    filterUrlPanel.add(theFilterLabel);
    textFilterInput = new JTextField(15);
    filterUrlPanel.add(textFilterInput);
    clickedURL = new JLabel("clicked url");
    filterUrlPanel.add(clickedURL);
    controlPanel.add(filterUrlPanel, BorderLayout.CENTER);

    JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout(new FlowLayout());
    insertButton = new JButton("Insert");
    insertButton.addActionListener(new ActionListener(){
			    		public void actionPerformed(ActionEvent e){
                theViewer.saveTreeMap(textInput.getText(), textFilterInput.getText());
	   						 }});
    insertButton.setEnabled(true);
    
    buttonPanel.add(insertButton);
    updateButton = new JButton("Update");
    updateButton.addActionListener(new ActionListener(){
			    		public void actionPerformed(ActionEvent e){
                theViewer.loadSectorEntries();
	   						 }});
    updateButton.setEnabled(true);
    
    buttonPanel.add(updateButton);

    controlPanel.add(buttonPanel, BorderLayout.SOUTH);
    scalePanel.add(controlPanel);
    scalePanel.add(new MemoryMonitor());
		scalePanel.setAlignmentX(scalePanel.CENTER_ALIGNMENT);
    return scalePanel;

  }

  public String getGUIName()
  {
    return new String("Map");
  }

  public TransformGroup[] getViewpoints()
  {
	  TransformGroup theTemp[] = new TransformGroup[1];
	  Transform3D t = new Transform3D();
	  t.setTranslation(new Vector3d(0.0,0,-30.5));
	  Matrix3d mat = new Matrix3d();
	  mat.rotY(3.13);
	  t.setRotation(mat);
	  TransformGroup ultraTemp = new TransformGroup();
  	ultraTemp.setTransform(t);
	  theTemp[0]=ultraTemp;
    return theTemp;
   } //getViewpoints()
} // end of class MapAgents
