// copyright (c) 1999 Róbert Viðar Bjarnason and Sean Brunwin
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


package org.bang.space;

import javax.media.j3d.*;
import javax.vecmath.*;
import java.io.*;
import net.jini.core.entry.*;
import java.util.*;
import java.lang.*;

/** 
 *   This class define a sectorEntry that can be one of folowing:
 *    public static final Integer VRML97 = new Integer(1);
 *    public static final Integer J3DNATIVE = new Integer(2);
 *    public static final Integer SPACEMARK = new Integer(3);
 *    public static final Integer TREEMAP = new Integer(4);
 *
 *   These are read from JavaSpaces to and are the datatype 
 *   that provides the basis for the persistent state
 *
 *   @author Robert Vidar Bjarnason (robofly@bang.is)
 */

public class SectorEntry implements Serializable, Entry {
  
  public String url;
  public String owner;
  public String aux;
  
  public Matrix4f myTransform;
 
  public Integer sectorXYZ[]; 

  public Integer objectID;

  public Integer type;

  public SectorEntry(Integer objType, Integer objID,  String theUrl, Matrix4f toBe, String theOwner, String theAux) 
  {
     this.type = objType;
     this.objectID = objID;
     this.url = theUrl;
     this.owner = theOwner;
     this.aux = theAux;
     setMyTransform(toBe);
    System.out.println("sectorXYZ=X:"+sectorXYZ[0].toString()+ "Y:"+sectorXYZ[1].toString()+ "Z:"+sectorXYZ[2].toString() );  
  }

  public SectorEntry(Integer objID, Integer searchSector[])
  {  
  System.out.println("SearchSec=X:"+searchSector[0].toString()+ "Y:"+searchSector[1].toString()+ "Z:"+searchSector[2].toString() );  
    this.type = null;
    this.objectID = objID;
    this.url = null;
    this.owner = null;
    this.myTransform = null;
    this.aux = null;
    this.sectorXYZ = searchSector;
  }

public SectorEntry()
  {
    this.type = null;
    this.objectID = null;
    this.url = null;
    this.owner = null;
    this.aux = null;
    this.myTransform = null;
    this.sectorXYZ = null;
  }


public String getUrl() { return url; }

  public void setMyTransform(Matrix4f toBe) 
  {
    myTransform = toBe;
    Vector3f theVector = new Vector3f();
    myTransform.get(theVector);
    sectorXYZ = getSectorFromVector(theVector);
  }

 public Integer[] getSectorFromVector(Vector3f theVector)
 {
    float theFloat[] = new float[3];
    Float theBigFloat;

    int theInteger;
    Integer theSectorXYZ[] = new Integer[3];

    theVector.get(theFloat);

    for(int i=0; i<3; i++) 
    {
      theFloat[i] = theFloat[i] / 100;
      theBigFloat = new Float(theFloat[i]);
      theSectorXYZ[i] = new Integer(theBigFloat.intValue());
    }

    return theSectorXYZ;     
 }
}  
