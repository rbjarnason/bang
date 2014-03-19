package org.bang.net;

import javax.media.j3d.*;
import java.util.Enumeration;
import javax.vecmath.*;

import org.bang.view.Viewer;
import org.bang.net.vnet.*;
import org.bang.net.vnet.J3Dispatcher;
import org.bang.net.J3DeepMatrix;
import matrix.vrml.*;

/** 
 * This is a behavior that watches for a transform to be changed. When
 * this changes, the behavior sends the transform out over the multicast 
 * channel.
 * @author Chris Heistad (heistad@ncsa.uiuc.edu or heistad@poboxes.com)
 * @author Steve Pietrowicz (srp@ncsa.uiuc.edu or spietrowicz@yahoo.com)
 * @changes Róbert Viðar Bjarnason (robofly@this.is)
 */

public class TransformWatcher extends Behavior{
	TransformGroup transform;
	J3Dispatcher dispatcher;
	J3DeepMatrix deepmatrix;
	WakeupCriterion criterion;
	Screamer screamer;
	Viewer viewer;

	Transform3D previous = new Transform3D();

  public TransformWatcher(TransformGroup transform, J3DeepMatrix deepmatrix, Viewer viewer){
	this.deepmatrix = deepmatrix;
	this.transform = transform;
	this.viewer = viewer;
	BoundingSphere bs = new BoundingSphere();
	bs.setRadius(1000000);
	setSchedulingBounds(bs);
  }

  public TransformWatcher(TransformGroup transform, J3Dispatcher dispatcher, Viewer viewer){
	this.dispatcher = dispatcher;
	this.transform = transform;
	this.viewer = viewer;
	BoundingSphere bs = new BoundingSphere();
	bs.setRadius(1000000);
	setSchedulingBounds(bs);
  }

  public TransformWatcher(TransformGroup transform, Screamer screamer, Viewer viewer){
	this.screamer = screamer;
	this.transform = transform;
	this.viewer = viewer;
	BoundingSphere bs = new BoundingSphere();
	bs.setRadius(1000000);
	setSchedulingBounds(bs);
  }


  public void initialize(){
	criterion = new WakeupOnTransformChange(transform);
	wakeupOn(criterion);
	transform.getTransform(previous);	
  }

  public void processStimulus(Enumeration criteria){

   	    Transform3D theTransform = new Transform3D();
   	    Transform3D theCTransform = new Transform3D();

	    Quat4f theCQ = new Quat4f(); 

	    transform.getTransform(theTransform);
	  //  viewer.compassGroup.getTransform(theCTransform);
	    theTransform.get(theCQ);
	//    theCTransform.set(theCQ);
	//viewer.compassGroup.setRotation(theCQ);
	
  	Integer theNowSector[] = viewer.getSectorFromTG(transform);	

  
	if (!viewer.currentSectorInt[0].equals(theNowSector[0]) ||
	    !viewer.currentSectorInt[1].equals(theNowSector[1]) ||
	    !viewer.currentSectorInt[2].equals(theNowSector[2]))
	{
	        viewer.setLastSector(viewer.currentSectorInt);	
		viewer.setCurrentSector(theNowSector);
 		viewer.initSector();
	}

	if (screamer != null) {
		screamer.send(transform);
		}
	
	if (dispatcher != null) {
	

 		Vector3f theV = new Vector3f();
   		Vector3f theOldV = new Vector3f();

		theTransform.get(theV);
		previous.get(theOldV);
	
		if (!theOldV.equals(theV)) {
			float[]	values = new float[3];
			theV.get(values);	    
			dispatcher.sendPosition(new VSFVec3f(values[0], values[1], values[2]));
		}

		Quat4f theQ = new Quat4f();
		Quat4f theOldQ = new Quat4f();

		theTransform.get(theQ);
		previous.get(theOldQ);

		if (!theOldQ.equals(theQ)) {
			AxisAngle4f theQA = new AxisAngle4f();
			theQA.set(theQ);
			float[]	rvalues = new float[4];
			theQA.get(rvalues);
			dispatcher.sendOrientation(new VSFRotation(rvalues[0], rvalues[1], rvalues[2], rvalues[3]));
		}


	}

    if (deepmatrix != null) {

 		Vector3f theV = new Vector3f();
   		Vector3f theOldV = new Vector3f();

		theTransform.get(theV);
		previous.get(theOldV);
		if (!theOldV.equals(theV)) {
			float[]	values = new float[3];
			theV.get(values);	    
			deepmatrix.sendPosition(new SFVec3f(values[0], values[1], values[2]));
		}

		Quat4f theQ = new Quat4f();
		Quat4f theOldQ = new Quat4f();

		theTransform.get(theQ);
		previous.get(theOldQ);

		if (!theOldQ.equals(theQ)) {
			AxisAngle4f theQA = new AxisAngle4f();
			theQA.set(theQ);
			float[]	rvalues = new float[4];
			theQA.get(rvalues);
			deepmatrix.sendOrientation(new SFRotation(rvalues[0], rvalues[1], rvalues[2], rvalues[3]));
		}

	}


	wakeupOn(criterion);
	transform.getTransform(previous);//save this transform
  }


  /** 
   * This method allows the watcher to back up one transform.
   */
  public void backup(){
	transform.setTransform(previous);
  }
}




