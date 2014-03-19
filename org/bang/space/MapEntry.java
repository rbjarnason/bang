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

import java.io.*;
import net.jini.core.entry.*;
import java.util.*;


/** 
 *   This class defines a MapEntry used to make bangMaps
 *   @author Robert Vidar Bjarnason (robofly@bang.is)
 */
public class MapEntry implements Serializable, Entry
{
  public String url;
  public String theTitle;
  public Long lastModified;
  public List refLinks;         // links pointing to this url
  public List theChildren;      // links from this url
  public List theChildrenTitle; // titles of links from this url
  public Boolean isValidated;
  public Boolean isIndexed;

  public MapEntry(String url, String theTitle, long lastModified,
                  List refLinks, List theChildren, List theChildrenTitle,
                  boolean isValidated, boolean isIndexed)
  {
    this.url=url;
    this.theTitle=theTitle;
    this.lastModified = new Long(lastModified);
    this.refLinks = refLinks;
    this.theChildren=theChildren;
    this.theChildrenTitle=theChildrenTitle;
    this.isValidated=new Boolean(isValidated);
    this.isIndexed=new Boolean(isIndexed);
  }

  public MapEntry() {}
  public void init() {}
  public void MapEntry() {}

  public MapEntry(String url)
  {
    this.url=url;
    this.theTitle=null;
    this.lastModified = null;
    this.refLinks = null;
    this.theChildren=null;
    this.theChildrenTitle=null;
    this.isValidated=null;
    this.isIndexed=null;
  }

  public MapEntry(boolean isValidated)
  {
    this.url=url;
    this.theTitle=null;
    this.lastModified = null;
    this.refLinks = null;
    this.theChildren=null;
    this.theChildrenTitle=null;
    this.isValidated=new Boolean(isValidated);
    this.isIndexed=null;
  }
}