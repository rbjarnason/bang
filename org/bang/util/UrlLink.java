// copyright (c) 1998,1999 R�bert Vi�ar Bjarnason
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

package org.bang.util;

import javax.media.j3d.*;
import javax.vecmath.*;

/**
 *   This defines a TransformGroup that know its URL
 *   for bangMap processing
 *   @author Robert Vidar Bjarnason (robofly@bang.is)
 */

public class UrlLink extends TransformGroup
 {
 public String myTitle;
 public UrlLink(String theTitle)
 {
	super();
	this.myTitle = theTitle;
 }

 public String getMyTitle()
 {
	return myTitle;
 }
}
