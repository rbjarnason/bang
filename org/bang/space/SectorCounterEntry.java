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
 *   Master object counter for a given sector
 *   @author Robert Vidar Bjarnason (robofly@bang.is)
 */

public class SectorCounterEntry implements Serializable, Entry {
  
  public Integer totalObjects;
  public Integer sectorXYZ[];

public SectorCounterEntry()
{
  totalObjects = new Integer(0);
  sectorXYZ = null;
}

public SectorCounterEntry(Integer theSector[])
{
    totalObjects = null;
    sectorXYZ = theSector;
}

public SectorCounterEntry(Integer count, Integer theSector[])
{
    totalObjects = count;
    sectorXYZ = theSector;
}
}

