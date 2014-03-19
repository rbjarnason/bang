package org.bang.util;

import com.sun.j3d.utils.behaviors.picking.*;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.media.j3d.*;
import javax.vecmath.*;


/**
 * Base class that allows users to adding picking and mouse manipulation to
 * his scene graph (see PickDragBehavior for an example of how to extend
 * this base class). This class is useful for interactive apps.
 */

public abstract class BangPickMouseBehavior extends Behavior {
  
  /**
   * Portion of the scene graph to operate picking on.
   */
  protected BangPickObject pickScene;

  protected WakeupCriterion[] conditions;
  protected WakeupOr wakeupCondition;
  protected boolean buttonPress = false;

  protected TransformGroup currGrp;
  protected static final boolean debug = false;
  protected MouseEvent mevent;
  
  /** 
   * Creates a PickMouseBehavior given current canvas, root of the tree to
   * operate on, and the bounds.
   */
  public BangPickMouseBehavior(Canvas3D canvas, BranchGroup root, Bounds bounds){
    super();
    currGrp = new TransformGroup();
    currGrp.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
    currGrp.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
    root.addChild(currGrp);
    pickScene = new BangPickObject(canvas, root);
  }

  public void initialize() {

    conditions = new WakeupCriterion[1];
//    conditions[0] = new WakeupOnAWTEvent(Event.MOUSE_MOVE);
    conditions[0] = new WakeupOnAWTEvent(Event.MOUSE_DOWN);
    wakeupCondition = new WakeupOr(conditions);

    wakeupOn(wakeupCondition);
  }
  
  private void processMouseEvent(MouseEvent evt) {
    buttonPress = false;

    if (evt.getID()==MouseEvent.MOUSE_PRESSED |
	evt.getID()==MouseEvent.MOUSE_CLICKED) {
      buttonPress = true;
      return;
    }
    else if (evt.getID() == MouseEvent.MOUSE_MOVED) {
      // Process mouse move event
    }
  }
  
  public void processStimulus (Enumeration criteria) {
    WakeupCriterion wakeup;
    AWTEvent[] evt = null;
    int xpos = 0, ypos = 0;

    while(criteria.hasMoreElements()) {
      wakeup = (WakeupCriterion)criteria.nextElement();
      if (wakeup instanceof WakeupOnAWTEvent)
	evt = ((WakeupOnAWTEvent)wakeup).getAWTEvent();
    }
    
    if (evt[0] instanceof MouseEvent){
      mevent = (MouseEvent) evt[0];

      if (debug)
	System.out.println("got mouse event");
      processMouseEvent((MouseEvent)evt[0]);
      xpos = mevent.getPoint().x;
      ypos = mevent.getPoint().y;
    }
    
    if (debug)
      System.out.println("mouse position " + xpos + " " + ypos);
    
    if (buttonPress){
      updateScene(xpos, ypos);
    }
    wakeupOn (wakeupCondition);
  }

  /** Subclasses shall implement this update function 
   */
  public abstract void updateScene(int xpos, int ypos);
}

