package org.bang.view;

import com.sun.j3d.utils.behaviors.mouse.*;
import javax.media.j3d.*;
import javax.vecmath.*;

import java.util.*;
import java.awt.*;
import java.awt.event.*;


/** 
 * This is an extension of the Sun provided MouseRotate class.
 * We have extended it to provide a different rate of rotation and
 * possibly changing the direction that the rotates happen.
 * 
 * @author Chris Heistad (heistad@ncsa.uiuc.edu or heistad@poboxes.com)
 * @author Steve Pietrowicz (srp@ncsa.uiuc.edu or spietrowicz@yahoo.com)
 */

public class ExtendedMouseRotate extends MouseRotate {

   double x_angle = 0.0;
   double y_angle = 0.0;
   double x_factor = 0.003;
   double y_factor = 0.003;

  public ExtendedMouseRotate(TransformGroup tg){
	super(tg);
  }



  public void processStimulus (Enumeration criteria) {
      WakeupCriterion wakeup;
      AWTEvent[] event;
      int id;
      int dx, dy;

      y_factor = 0;

      while (criteria.hasMoreElements()) {
         wakeup = (WakeupCriterion) criteria.nextElement();
         if (wakeup instanceof WakeupOnAWTEvent) {
            event = ((WakeupOnAWTEvent)wakeup).getAWTEvent();
            for (int i=0; i<event.length; i++) { 
			      processMouseEvent((MouseEvent) event[i]);
		
	    		  if (buttonPress){
              			
              			 id = event[i].getID();
              			
              			 if ((id == MouseEvent.MOUSE_DRAGGED) && 
		   					!((MouseEvent)event[i]).isMetaDown() && 
		    				!((MouseEvent)event[i]).isAltDown()){
				        
				                  x = ((MouseEvent)event[i]).getX();
                				  y = ((MouseEvent)event[i]).getY();
				                  dx = -(x - x_last);
                  				  dy = y - y_last;

		  						  if (!reset){	    
								    x_angle = dy * y_factor;
		    						y_angle = dx * x_factor;
		    
								    transformX.rotX(x_angle);
		    						transformY.rotY(y_angle);
		    
								    transformGroup.getTransform(currXform);
		    
								    //Vector3d translation = new Vector3d();
								    //Matrix3f rotation = new Matrix3f();
		    						Matrix4d mat = new Matrix4d();
		    
		    						// Remember old matrix
								    currXform.get(mat);
		    
								    // Translate to origin
								    currXform.setTranslation(new Vector3d(0.0,0.0,0.0));
		  		  if (invert) {
			currXform.mul(currXform, transformX);
			currXform.mul(currXform, transformY);
		    } else {
			currXform.mul(transformX, currXform);
			currXform.mul(transformY, currXform);
		    }
		    
		    // Set old translation back
		    Vector3d translation = new 
		      Vector3d(mat.m03, mat.m13, mat.m23);
		    currXform.setTranslation(translation);
		    
		    // Update xform
		    transformGroup.setTransform(currXform);
		  }
		  else {
		    reset = false;
		  }

                  x_last = x;
                  y_last = y;
               }
               else if (id == MouseEvent.MOUSE_PRESSED) {
                  x_last = ((MouseEvent)event[i]).getX();
                  y_last = ((MouseEvent)event[i]).getY();
               }
	      }
	    }
         }
      }
      if (buttonPress || ((flags & MANUAL_WAKEUP) == 0))
	wakeupOn (mouseCriterion);
   }


}
