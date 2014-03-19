// copyright (c) 1998,1999 Unknown, Róbert Viðar Bjarnason
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
//
// somebody posted the code that this class is derived from to a mailinglist .... 
// cant remember who.... if YOU did it, please come forward and we'll 
// add your credit here. (rvb)

package org.bang.view;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.media.j3d.*;
import javax.vecmath.*;

/**
 *   This class defines keyboard behaviors for 3D Navigation
 *   @author Unkown
 *   @author Robert Vidar Bjarnason (robofly@bang.is)
 */
public class KeyboardBehavior extends Behavior
{
 WakeupCriterion[] keyboardEvents;
 WakeupOr keyboardCriterion;
 TransformGroup transformGroup;	// the transform group that the viewplatform is attached to
 Transform3D move;
 float theChange;
 float theRotChange;
	
 public KeyboardBehavior(TransformGroup transformGroup) 
 {
	super();
	this.transformGroup = transformGroup;
	move = new Transform3D();
	theChange=0.55f;
	theRotChange=0.04f;
 }
	
 public void initialize() 
 {
	keyboardEvents = new WakeupCriterion[2];
	// Only wake up when a key is pressed or released
	keyboardEvents[0] = new WakeupOnAWTEvent(KeyEvent.KEY_PRESSED);
	keyboardEvents[1] = new WakeupOnAWTEvent(KeyEvent.KEY_RELEASED);
	keyboardCriterion = new WakeupOr(keyboardEvents);
	wakeupOn (keyboardCriterion);
 }

 public void processStimulus (Enumeration criteria)
 {
	WakeupCriterion wakeup;
	AWTEvent[] event;
	KeyEvent evt;
	while (criteria.hasMoreElements()) {
			wakeup = (WakeupCriterion) criteria.nextElement();
			if (wakeup instanceof WakeupOnAWTEvent) {
				event = ((WakeupOnAWTEvent)wakeup).getAWTEvent();
				for (int i=0; i<event.length; i++) {
					evt = (KeyEvent) event[i];
					if (evt.getID()==KeyEvent.KEY_PRESSED &&
					    !(evt.isAltDown())) {
						switch(evt.getKeyCode()) {
						case KeyEvent.VK_NUMPAD8:	// move forward
							Transform3D transform = new Transform3D();
							transformGroup.getTransform(transform);
							Transform3D original = new Transform3D(transform);
							Matrix4d rotmat = new Matrix4d();
							transform.get(rotmat);
							rotmat.m03 = rotmat.m13 = rotmat.m23 = 0.0;
							transform.set(rotmat);
							Transform3D trans = new Transform3D();
							trans.setTranslation(new Vector3d(0,0,-1*theChange));
		
							transform.mul(trans);
							original.mul(trans);

							float[] array = new float[16];
							original.get(array);
							//	array[7] = 0;
							original.set(array);	
		
							transformGroup.setTransform(original);
							break;
				
						case KeyEvent.VK_NUMPAD2:	// move backwards
							Transform3D transform1 = new Transform3D();
							transformGroup.getTransform(transform1);
							Transform3D original1 = new Transform3D(transform1);
							Matrix4d rotmat1 = new Matrix4d();
							transform1.get(rotmat1);
							rotmat1.m03 = rotmat1.m13 = rotmat1.m23 = 0.0;
							transform1.set(rotmat1);
							Transform3D trans1 = new Transform3D();
							trans1.setTranslation(new Vector3d(0,0,theChange));
		
							transform1.mul(trans1);
							original1.mul(trans1);

							float[] array1 = new float[16];
							original1.get(array1);
							//	array1[7] = 0;
							original1.set(array1);	
		
							transformGroup.setTransform(original1);
							break;
	
						case KeyEvent.VK_NUMPAD4:	// rotate left
							Transform3D transform2 = new Transform3D();
							transformGroup.getTransform(transform2);
							Transform3D original2 = new Transform3D(transform2);
							Matrix4d rotmat2 = new Matrix4d();
							transform2.get(rotmat2);
							rotmat2.m03 = rotmat2.m13 = rotmat2.m23 = 0.0;
							transform2.set(rotmat2);
							Transform3D trans2 = new Transform3D();
							trans2.rotY(theRotChange);
		
							transform2.mul(trans2);
							original2.mul(trans2);

							transformGroup.setTransform(original2);
							break;

						case KeyEvent.VK_NUMPAD6:	// rotate right
							Transform3D transform3 = new Transform3D();
							transformGroup.getTransform(transform3);
							Transform3D original3 = new Transform3D(transform3);
							Matrix4d rotmat3 = new Matrix4d();
							transform3.get(rotmat3);
							rotmat3.m03 = rotmat3.m13 = rotmat3.m23 = 0.0;
							transform3.set(rotmat3);
							Transform3D trans3 = new Transform3D();
							trans3.rotY(-1*theRotChange);
		
							transform3.mul(trans3);
							original3.mul(trans3);

							transformGroup.setTransform(original3);
							break;

						} // end switch
					} // end if keypressed			
				} // end for loop that processes all events
			} //end if wakeup
		} // end while		
		wakeupOn(keyboardCriterion);	// VERY IMPORTANT TO CALL THIS AGAIN AT THE END OF THE FUNCTION
	} // end process stimulus
} // end class
