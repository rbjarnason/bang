package org.bang.view;

import com.sun.j3d.utils.behaviors.mouse.*;
import javax.media.j3d.*;
import javax.vecmath.*;

import java.util.*;
import java.awt.*;
import java.awt.event.*;

/** 
 * This is an extension of the Sun provided MouseZoom class.
 * We have overridden it so that we can change the rate of z translation.
 * 
 * @author Chris Heistad (heistad@ncsa.uiuc.edu or heistad@poboxes.com)
 * @author Steve Pietrowicz (srp@ncsa.uiuc.edu or spietrowicz@yahoo.com)
 */

public class ExtendedMouseZoom extends MouseZoom{

   double z_factor = 0.1;

  public ExtendedMouseZoom(TransformGroup tg){
	super(tg);
  }


  public void processStimulus(Enumeration criteria){
    WakeupCriterion wakeup;
    AWTEvent[] event;
    int id;
    int dx, dy;

    while (criteria.hasMoreElements()) {
      wakeup = (WakeupCriterion) criteria.nextElement();
      if (wakeup instanceof WakeupOnAWTEvent) {
        event = ((WakeupOnAWTEvent)wakeup).getAWTEvent();
        for (int i=0; i<event.length; i++) {
          processMouseEvent((MouseEvent) event[i]);

          if (buttonPress){
            id = event[i].getID();
          if ((id == MouseEvent.MOUSE_DRAGGED) &&
              ((MouseEvent)event[i]).isShiftDown() &&
               !((MouseEvent)event[i]).isMetaDown()){


            x = ((MouseEvent)event[i]).getX();
            y = ((MouseEvent)event[i]).getY();

            dx = x - x_last;
            dy = y - y_last;

            if (!reset){
		Transform3D transform = new Transform3D();
		transformGroup.getTransform(transform);
		Transform3D original = new Transform3D(transform);

		Matrix4d rotmat = new Matrix4d();
		transform.get(rotmat);

		rotmat.m03 = rotmat.m13 = rotmat.m23 = 0.0;
	
		transform.set(rotmat);

		Transform3D trans = new Transform3D();
		trans.setTranslation(new Vector3d(0,0,dy*z_factor));
		
		transform.mul(trans);
		original.mul(trans);

		float[] array = new float[16];
		original.get(array);
	//	array[7] = 0;
		original.set(array);	
		
		transformGroup.setTransform(original);
		
			
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
