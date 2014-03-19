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
package org.bang.content;

// Java3D classes
import javax.media.j3d.*;
import javax.vecmath.*;
import java.awt.*;
import javax.swing.*;
import org.bang.jini.BangSpace;

public class SpinDialog3D implements org.bang.LoadInterface3D {

 String theText = "";
 String theFontname;
 BranchGroup theScene;
 RotationInterpolator rotator;

 public void setText(String theText, String theFontname) {
 	this.theText = theText;
 	this.theFontname = theFontname;
 	}

 public JPanel getGUI() { return new JPanel(); }

 public String getGUIName() { return null; }

 public BranchGroup getScene3D()  {
	return theScene;
	 }

 public void createScene(TransformGroup uTrans, BangSpace space, Canvas3D theCanvas) {

    float sl = theText.length();
    // Create the root of the branch graph
    BranchGroup objRoot = new BranchGroup();
    objRoot.setCapability(objRoot.ALLOW_DETACH);
        
    // Create a Transformgroup to scale all objects so they
    // appear in the scene.
    TransformGroup objScale = new TransformGroup();
    Transform3D t3d = new Transform3D();
    
    // Assuming uniform size chars, set scale to fit string in view
    t3d.setScale(1.2/sl);
    objScale.setTransform(t3d);
    objRoot.addChild(objScale);

    // Create the transform group node and initialize it to the
    // identity.  Enable the TRANSFORM_WRITE capability so that
    // our behavior code can modify it at runtime.  Add it to the
    // root of the subgraph.
    TransformGroup objTrans = new TransformGroup();
    objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
    objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
    objScale.addChild(objTrans);
    Font3D f3d = new Font3D(new Font(theFontname, Font.PLAIN, 2),
				new FontExtrusion());
    Text3D txt = new Text3D(f3d, theText, new Point3f( -sl/2.0f, -1.f, -1.f));
    Shape3D sh = new Shape3D();
    Appearance app = new Appearance();
    Material mm = new Material();
    mm.setLightingEnable(true);
    app.setMaterial(mm);
    sh.setGeometry(txt);
    sh.setAppearance(app);
    objTrans.addChild(sh);

    BoundingSphere rbounds = new BoundingSphere(new Point3d(0.0,0.0,0.0), 500.0);
    Transform3D yAxis = new Transform3D();
    
    Alpha rotationAlpha = new Alpha(-1, Alpha.INCREASING_ENABLE,
					  0, 0,
					  60000, 0, 0,
					  0, 0, 0);
    rotator = new RotationInterpolator(rotationAlpha, objTrans, yAxis,
				       0.0f, (float) Math.PI*2.0f);
    rotator.setSchedulingBounds(rbounds);
    objTrans.addChild(rotator);

    Transform3D tt = new Transform3D();
    tt.set(new Vector3d(-0.5,0,0));
    objTrans.setTransform(tt);
	
  theScene = objRoot; 
  
  }
  
  public TransformGroup[] getViewpoints() {
 	TransformGroup theTemp[] = new TransformGroup[1];
  	Transform3D t = new Transform3D();
	t.setTranslation(new Vector3d(0.0,0,-3.5));
	Matrix3d mat = new Matrix3d();
	mat.rotY(3.13);
	t.setRotation(mat);
	TransformGroup ultraTemp = new TransformGroup();
	ultraTemp.setTransform(t);
	theTemp[0]=ultraTemp;
	return theTemp;
  }

  public void stopSpinning() {
  	rotator.setEnable(false);
  }

}  