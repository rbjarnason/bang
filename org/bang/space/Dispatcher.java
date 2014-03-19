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

import org.bang.view.*;
import org.bang.jini.*;
import net.jini.core.event.*;
import java.util.*;
import java.rmi.server.*;
import java.rmi.*;


/** 
 *   This is the dispatcher that notifies all clients 
 *   in a given sector about new objects. 
 *   @author Robert Vidar Bjarnason (robofly@bang.is)
 */
public class Dispatcher extends UnicastRemoteObject implements RemoteEventListener {
  private Hashtable liveEntries;
 // private Thread t;
  private Viewer myViewer;

   public Dispatcher()  throws RemoteException {
        super();
    }
 
 public void init(Viewer theViewer)
  {

   myViewer = theViewer;

//  t = new Thread(this);
//  t.start();

  liveEntries = new Hashtable();
  }

  public void stop() {}

   public void run() {}

   public void start() {}
	
  public void notify(RemoteEvent theEvent)
            throws UnknownEventException,
                   java.rmi.RemoteException
    {
          System.out.println("Got Notified");
	try {
//	Object theTempEntry = (Object) theEvent.getRegistrationObject().get();
//	System.out.println(theTempEntry);	
//
          		 myViewer.loadSectorEntries();
		}
		catch (Exception e) { System.out.println("ERROR");
e.printStackTrace();
}	
	}
  
}