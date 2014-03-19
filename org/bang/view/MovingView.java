package org.bang.view;

import javax.media.j3d.*;
import java.util.Enumeration;
import com.sun.j3d.utils.behaviors.mouse.MouseRotate;
import com.sun.j3d.utils.behaviors.mouse.MouseTranslate;
import com.sun.j3d.utils.behaviors.mouse.MouseZoom;

import org.bang.net.TransformWatcher;

/** 
 * This object receives what is supposed to be the view platform transform
 * group and contructs objects that will operate on the group. This is to control
 * movement in the 3d world. Like move forward or back and turning.
 *
 * @author Chris Heistad (heistad@ncsa.uiuc.edu or heistad@poboxes.com)
 * @author Steve Pietrowicz (srp@ncsa.uiuc.edu or spietrowicz@yahoo.com)
 */ 

public class MovingView
{
 MouseRotate rotate;
 MouseTranslate translate;
 MouseZoom zoom;
 KeyboardBehavior kb;

 BranchGroup bg;

/** 
 * Not sure how we evolve this class ...(rvb)
 */ 
 public MovingView(TransformGroup group)
 {	
	bg = new BranchGroup();
	bg.setCapability(BranchGroup.ALLOW_AUTO_COMPUTE_BOUNDS_READ);
	bg.setCapability(BranchGroup.ALLOW_BOUNDS_READ);

	BoundingSphere bounds = new BoundingSphere();
	bounds.setRadius(10000);

	rotate = new ExtendedMouseRotate(group);
	bg.addChild(rotate);
	rotate.setSchedulingBounds(bounds);
	
	translate = new MouseTranslate(group);
	bg.addChild(translate);
	translate.setSchedulingBounds(bounds);

	zoom = new ExtendedMouseZoom(group);
	bg.addChild(zoom);
	zoom.setSchedulingBounds(bounds);
	
	kb = new KeyboardBehavior(group);
	bg.addChild(kb);
	kb.setSchedulingBounds(bounds);
	
	group.addChild(bg);
 } 
}
