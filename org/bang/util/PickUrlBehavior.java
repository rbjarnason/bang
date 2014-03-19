package org.bang.util;

import javax.media.j3d.*;
import java.util.*;
import java.awt.*;
import java.awt.Event;
import java.awt.AWTEvent;
import java.awt.event.MouseEvent;
import javax.vecmath.*;
import org.bang.agent.Map;

public class PickUrlBehavior extends BangPickMouseBehavior {

  Map myMap;

  public PickUrlBehavior(Canvas3D canvas, BranchGroup root,
			       Bounds bounds, Map theMap) {
      super(canvas, root, bounds);
      this.myMap = theMap;
      this.setSchedulingBounds(bounds);
      root.addChild(this);
      
  }

    public void updateScene(int xpos, int ypos) {
	Node shapeX;
      UrlLink shape;

	System.out.println("gotClick");
	shapeX = (Node) pickScene.pickNode(pickScene.pickClosest(xpos, ypos, 
								   BangPickObject.USE_GEOMETRY),
					     BangPickObject.TRANSFORM_GROUP);

	if (shapeX instanceof UrlLink) 
	{ shape = (UrlLink) shapeX;

	if (shape != null) {
   	    System.out.println("Found shape");
 	    String theUrl = shape.getMyTitle();
	    myMap.clickedURL.setText(theUrl);
	    myMap.theDefaultBrowser.displayURL(theUrl);

	  }
	}
    }
}
