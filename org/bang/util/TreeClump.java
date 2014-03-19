// copyright (c) 1999 Sean Edin, Róbert Viðar Bjarnason

package org.bang.util;

import java.lang.*;
import javax.media.j3d.*;
import javax.vecmath.*;
import java.awt.Font;
import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.geometry.Box;
import com.sun.j3d.utils.geometry.Text2D;

import com.sun.j3d.utils.universe.*;
import org.bang.agent.Map;

public class TreeClump
{

  public String myTitle;
  public Map theMap;

  public TreeClump(String myTitle, Transform3D myPos, BranchGroup cubeBG, BranchGroup lineBG,
	Canvas3D theCanvas, float parent_x, float parent_y, float parent_z, int theLineColor, 
	   SharedGroup normBox, SharedGroup colorBox, Map theMap)
  {
    	  
   	/*
	  * Create the main branch Group
	  */
	  UrlLink atg = new UrlLink( myTitle );
	  atg.setCapability(atg.ALLOW_TRANSFORM_WRITE);
	  atg.setCapability(atg.ALLOW_TRANSFORM_READ);
	  atg.setCapability(atg.ALLOW_CHILDREN_EXTEND);
	  atg.setCapability(atg.ENABLE_PICK_REPORTING);

	  myPos.setScale(0.08);
	  atg.setTransform(myPos);

	  LineArray theLine=new LineArray(2, GeometryArray.COLOR_3 | GeometryArray.COORDINATES);
            theLine.setCapability(Geometry.ALLOW_INTERSECT);


	  Vector3f vecPos = new Vector3f();
   	  Tuple3f tupPos = new Vector3f();
    	  myPos.get(vecPos);
    	  vecPos.get(tupPos);
    	  Point3d childPoint = new Point3d(parent_x,parent_y,parent_z);

  //  System.out.println("Point3d-A: "+childPoint.toString());
  //  System.out.println("Point3d-B: "+tupPos.toString());
 
	  Point3d[] verts={new Point3d(parent_x,parent_y,parent_z),
                      new Point3d(tupPos)};
	  LineAttributes lta = new LineAttributes( );
	  lta.setLineWidth( 1.0f );
	  lta.setLineAntialiasingEnable( true );
	  lta.setLinePattern( LineAttributes.PATTERN_SOLID );

    	  Appearance app=new Appearance();
	  app.setLineAttributes( lta );

	    Color3f color = null;

	    BoundingSphere bounds =
	    new BoundingSphere(new Point3d(0.0,0.0,0.0), 100.0);


 	  if (theLineColor == 0) { 
		color = new Color3f(0,0,1.0f);
		Link normLink = new Link(normBox);
		normLink.setCapability(Geometry.ALLOW_INTERSECT);
		atg.addChild(normLink); }
    	else {    color = new Color3f(1.0f,0,0);
		Link colorLink = new Link(normBox);
		colorLink.setCapability(Geometry.ALLOW_INTERSECT);
		atg.addChild(colorLink); }
	  
 	  // bggg.addChild(pickUrl);
	  // atg.addChild(bggg);

	  theLine.setColor(0,color); theLine.setCoordinate(0,verts[1]);
	  theLine.setColor(1,color); theLine.setCoordinate(1,verts[0]);

	  TransformGroup ltg = new TransformGroup();
	  ltg.setCapability(ltg.ALLOW_TRANSFORM_WRITE);
	  ltg.setCapability(ltg.ALLOW_TRANSFORM_READ);
	  ltg.setCapability(ltg.ALLOW_CHILDREN_EXTEND);
	  ltg.setCapability(ltg.ENABLE_PICK_REPORTING);    	 
 
	  Transform3D ltrans = new Transform3D();
	  ltrans.setTranslation(new Vector3d(0,0,0));

	  ltg.setTransform(ltrans);

	  Shape3D theRealLine = new Shape3D(theLine,app);
            theRealLine.setCapability(Geometry.ALLOW_INTERSECT);

	  ltg.addChild(theRealLine);

	cubeBG.addChild(atg);
	lineBG.addChild(ltg);
  } 
 
}