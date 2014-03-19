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

package org.bang.jini;

import javax.media.j3d.*;
import javax.vecmath.*;
import java.net.URL;
import java.net.MalformedURLException;
import net.jini.discovery.*;
import net.jini.core.lookup.*;
import net.jini.core.entry.*;
import net.jini.core.event.*;
import net.jini.core.discovery.*;    
import net.jini.lookup.entry.*; 
//import com.sun.jini.lookup.*;
import net.jini.lookup.entry.*;
//import net.jini.entry.*;
import net.jini.core.lease.Lease;
import net.jini.space.JavaSpace;
import net.jini.core.transaction.Transaction;
import java.rmi.*;
import org.bang.view.Viewer;
import org.bang.space.*;

/** 
 *   Class that takes care of the bangSpace connection 
 *   through Jini and JavaSpaces
 *   @authors Robert Vidar Bjarnason (robofly@bang.is) & Sean Brunwin
 */

public class BangSpace implements Runnable
{

  Dispatcher bangSpaceDispatcher;
  public static JavaSpace space;
  public static boolean online = false;
  private Thread t;

  public static final Integer VRML97 = new Integer(1);
  public static final Integer J3DNATIVE = new Integer(2);
  public static final Integer SPACEMARK = new Integer(3);
  public static final Integer TREEMAP = new Integer(4);

  public void stop() {}
 
  public void run() {}

  public void start() {}

  public BangSpace()
  {

    t = new Thread(this);
    t.start();

    System.out.println("BangSpace lookup activated");

 
        int iPort;
        String sHost;
        Entry[] aeAttributes;
        LookupLocator lookup;  
        ServiceID id;       
        ServiceRegistrar registrar;
        ServiceTemplate template;

        try 
          {


/*        Setting the security manager to allow the RMI class loader
          to go to the codebase for classes that are not available
          locally.
          ==========================================================  */

          System.setSecurityManager (new RMISecurityManager ());


/*        Find the Jini lookup service (reggie) and print its location.
          =============================================================  */

          lookup = new LookupLocator ("jini://Pineapple");
          sHost = lookup.getHost ();
          iPort = lookup.getPort ();
          System.out.println ();
          System.out.println ("bangSpace: LookupLocator      = " + lookup);
          System.out.println ("bangSpace: LookupLocator.host = " + sHost);
          System.out.println ("bangSpace: LookupLocator.port = " + iPort);


/*        Get the lookup service's ServiceRegistrar (the class by which
          interaction with the lookup service is possible).
          =============================================================  */

          registrar  = lookup.getRegistrar ();
          id  = registrar.getServiceID ();
          System.out.println ("bangSpace: ServiceRegistrar   = " + registrar);
          System.out.println ("bangSpace: ServiceID          = " + id);


/*        Perform a search on the lookup server to find the service 
          that has the name attribute of "MyServer".  The lookup
          service returns an interface object to the service.
          =========================================================  */
          
          aeAttributes = new Entry[1];
          aeAttributes[0] = new Name ("BangSpace");
          template = new ServiceTemplate (null, null, aeAttributes);
          JavaSpace js = (JavaSpace)  registrar.lookup (template);
          System.out.println ("bangSpace: ServiceTemplate    = " + template);
	  if (js == null)
          {
            System.out.println("BangSpace service could not be deserialized");
          }
          else
          {
          System.out.println ("bangSpace: Service object     = " + js);
            space = js;
            System.out.println("BangSpace service deserialized");
            online = true;
	  }

    }
    catch (Exception e)
    {
      System.out.println("Excepion while getting BangSpace service: " + e.getMessage());
      e.printStackTrace();
    }
   
  }


    public SectorEntry readSectorEntry(Integer objectID, Integer theSector[])
    {
      SectorEntry template = new SectorEntry(objectID, theSector);
      long timeToWait = 0L;

      Transaction sotxn = null;

      SectorEntry result = null;

      try
      {
        result = (SectorEntry) space.readIfExists (template, sotxn, timeToWait);
    //    System.out.println("Found SectorEntry entry in BangSpace");
      }
      catch (Exception e)
      {
        System.out.println("read next SectorEntry failed");
      e.printStackTrace();

        return null;
      }
     return result;
    } //readNextSectorEntry()

 public SectorEntry killSectorEntry(Integer objectID, Integer theSector[])
    {
      SectorEntry template = new SectorEntry(objectID, theSector);
      long timeToWait = 0L;

      Transaction sotxn = null;

      SectorEntry result = null;

      try
      {
        result = (SectorEntry) space.takeIfExists (template, sotxn, timeToWait);
    //    System.out.println("Found SectorEntry entry in BangSpace");
      }
      catch (Exception e)
      {
        System.out.println("take next SectorEntry failed");
      e.printStackTrace();

        return null;
      }
     return result;
    } //readNextSectorEntry()


    public Integer takeSectorCounterEntry(Integer theSector[])
    {
      SectorCounterEntry template = new SectorCounterEntry(theSector);
      long timeToWait = 0L;

      Transaction sotxn = null;

      SectorCounterEntry result = null;

      try
      {
        result = (SectorCounterEntry) space.takeIfExists (template, sotxn, timeToWait);
        System.out.println("Found SectorCounterEntry entry in BangSpace");
      }
      catch (Exception e)
      {
        System.out.println("take SectorCounterEntry failed");
      e.printStackTrace();


        return null;
      }
     
    if (result!=null)  
      return result.totalObjects;
    else return null;
    } //takeSectorCounterEntry()

  public Integer readSectorCounterEntry(Integer theSector[])
    {
      SectorCounterEntry template = new SectorCounterEntry(theSector);
      long timeToWait = 0L;

      Transaction sotxn = null;

      SectorCounterEntry result = null;

      try
      {
        result = (SectorCounterEntry) space.readIfExists (template, sotxn, timeToWait);
      //  System.out.println("Read SectorCounterEntry entry in BangSpace");
      }
      catch (Exception e)
      {
      e.printStackTrace();


        System.out.println("read SectorCounterEntry failed");
        return new Integer(0);
      }
    if (result!=null)  
      return result.totalObjects;
    else return new Integer(0);
    } //readNextSectorCounterEntry()


    public void enableNotify(Viewer theViewer, Integer[] theSector)
    {
     // try
    //  {
        // The transaction under which to perform the write
        Transaction txn = null;
        SectorEntry secSearch = new SectorEntry();
        secSearch.sectorXYZ = theSector;

        System.out.println(secSearch.toString());
	 try {	
        bangSpaceDispatcher = new Dispatcher();
        bangSpaceDispatcher.init(theViewer);
	} catch (Exception e) {}
        System.out.println(bangSpaceDispatcher.toString());

        long timeToLive = Lease.FOREVER;
        try {
        EventRegistration theReg = space.notify(secSearch, txn, bangSpaceDispatcher, timeToLive, null);
        System.out.println(theReg.toString());

	} catch (Exception e) {
      e.printStackTrace();


}
     //   System.out.println(theReg.toString());

	System.out.println("Notify enabled");
//        System.out.println("enableNotify failed");
//	e.printStackTrace();

  //    }
    } //enableNotify()

    public void disableNotify()
    {
	bangSpaceDispatcher = null;
    }

    public void addMapEntry(String theUrl, MapEntry mapEntry)
    {
      try
      {
        // The transaction under which to perform the write
        Transaction txn = null;
        MapEntry theMap = mapEntry;
        // The lease duration that should be requested for
        // this entry
        long timeToLive = Lease.FOREVER;
        space.write(theMap, txn, timeToLive);
        System.out.println("Wrote "+theUrl+" to BangSpace");
      }
      catch (Exception e)
      {
        System.out.println("addMapEntry failed");
      }
    } //addMapEntry()

    public void writeToSearchEntry(String theUrl)
    {

     if (readMapEntry(theUrl)==null && !doesToSearchEntryExists(theUrl))
     {
      try
      {
        // The transaction under which to perform the write
        Transaction txn = null;
        ToSearchEntry toSearch = new ToSearchEntry(theUrl);
        // The lease duration that should be requested for
        // this entry
        long timeToLive = Lease.FOREVER;
        space.write(toSearch, txn, timeToLive);
        System.out.println("Wrote ToSearchEntry "+theUrl+" to BangSpace");
      }
      catch (Exception e)
      {
        System.out.println("writeToSearchEntry failed");
      }
     } //endif(getMapEntry)
    } //addToSearchMapEntry()

    public MapEntry readMapEntry(String theUrl)
    {
      // Set attribute to be null, so it will act as a
      // wildcard and match on any Room3D acting on the url
      MapEntry template = new MapEntry(theUrl);

      // The amount of time to wait for a match
      long timeToWait = 0L;

      // The transaction under which to perform the read
      Transaction sotxn = null;
      try
      {
        MapEntry result = (MapEntry) space.readIfExists (template, sotxn, timeToWait);
        if (result!=null) {
// System.out.println("Read mapEntry from the BangSpace"); 
}
        else { System.out.println("end of map thread reached at "+theUrl); }
        return result;
      }
      catch (Exception e)
      {
     //   e.printStackTrace(System.err);
        System.out.println("readMapEntry failed");
        return null;
      }
    } //getMapEntry()

    public MapEntry takeNextToValidate()
    {
      // Set attribute to be null, so it will act as a
      // wildcard and match on any Room3D acting on the url
      MapEntry template = new MapEntry(false);

      // The amount of time to wait for a match
      long timeToWait = 0L;

      // The transaction under which to perform the read
      Transaction sotxn = null;
      try
      {
        MapEntry result = (MapEntry) space.takeIfExists (template, sotxn, timeToWait);
        if (result!=null) {System.out.println("Took next mapEntry to validate from BangSpace"); }
        else { System.out.println("mapEntry not found"); }
        return result;
      }
      catch (Exception e)
      {
     //   e.printStackTrace(System.err);
        System.out.println("takeNextToValidate failed");
        return null;
      }
    } //takeNextToValidate()

    public boolean doesToSearchEntryExists(String theUrl)
    {
      // Set attribute to be null, so it will act as a
      // wildcard and match on any Room3D acting on the url
      ToSearchEntry template = new ToSearchEntry(theUrl);
//      System.out.println("Created a template for ToSearchEntryExists");

      // The amount of time to wait for a match
      long timeToWait = 0L;

      // The transaction under which to perform the read
      Transaction sotxn = null;
      try
      {
        ToSearchEntry result = (ToSearchEntry) space.readIfExists (template, sotxn, timeToWait);

        if (result!=null) { System.out.println("Found ToSearchEntryExists entry in BangSpace"); return true; }
        else { System.out.println("toSearchEntryExists not found");}
      }
      catch (Exception e)
      {
        System.out.println("doesToSearchEntryExists failed");
        return false;
      }
      return false;
    } //doesToSearchEntryExists

    public String takeNextToSearch()
    {
      ToSearchEntry template = new ToSearchEntry(null);

      // The amount of time to wait for a match
      long timeToWait = 0L;

      // The transaction under which to perform the read
      Transaction sotxn = null;
     boolean itsOk = false;
     ToSearchEntry result = null;

      try
      {
        result = (ToSearchEntry) space.takeIfExists (template, sotxn, timeToWait);
        System.out.println("Found ToSearchEntry entry in BangSpace");
      }
      catch (Exception e)
      {
        System.out.println("takeNextToSearch failed");
        return null;
      }
     return result.url;
    } //getToSearchEntry()

  public void writeSectorEntry(Integer theType, String theUrl, Matrix4f theTransform, String theOwner, String theAux)
  {
      try
      {
        // The transaction under which to perform the write
        Transaction txn = null;
        long timeToLive = Lease.FOREVER;

        System.out.println(theUrl +" xx "+ theOwner + " xx " + theTransform.toString());

        Vector3f theVector = new Vector3f();
        theTransform.get(theVector);
 
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
    
       Integer objectID = takeSectorCounterEntry(theSectorXYZ); 

       if (objectID==null) 
       {
    	   objectID = new Integer(0);
       }
       else
       {
    	   int tempInt = objectID.intValue();
    	   tempInt++;
    	   objectID = new Integer(tempInt);
       }

       SectorEntry theEntry = new SectorEntry(theType, objectID, theUrl, theTransform, theOwner, theAux);
       
       space.write(theEntry, txn, timeToLive);

       System.out.println("Wrote SectorEntry "+theEntry.url+" to BangSpace");

       SectorCounterEntry theCounterEntry;

       if (objectID.equals(new Integer(0))) 
       {
    	   theCounterEntry = new SectorCounterEntry(new Integer(1), theSectorXYZ);
    	   space.write(theCounterEntry, txn, timeToLive);
    	   System.out.println("Created counter for ectorXYZ=X:"+theSectorXYZ[0].toString()+ "Y:"+theSectorXYZ[1].toString()+ "Z:"+theSectorXYZ[2].toString());
        }
        else
        {
        	theCounterEntry = new SectorCounterEntry(objectID, theSectorXYZ);
        	space.write(theCounterEntry, txn, timeToLive);
        	System.out.println("Uptated counter for sectorXYZ=X:"+theSectorXYZ[0].toString()+ "Y:"+theSectorXYZ[1].toString()+ "Z:"+theSectorXYZ[2].toString()+ "to"+theCounterEntry.totalObjects.toString());
        }

      }
      catch (Exception e)
      {
        System.out.println("writeVRML97 failed" + e.toString()+" xx "+ e.getMessage());
        e.printStackTrace();
      }
   } // writeSectorEntry
  } //bangSpace class
