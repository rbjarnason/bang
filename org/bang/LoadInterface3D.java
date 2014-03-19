// copyright (c) 1998,1999 Róbert Viðar Bjarnason
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

package org.bang;

// Java3D classes
import javax.media.j3d.*;
import javax.swing.JPanel;
import org.bang.jini.BangSpace;

/**
 *   Native J3D content that can be loaded over the web
 *   with Bang has to implement this interface
 *   @author Robert Vidar Bjarnason (robofly@bang.is)
 */
public interface LoadInterface3D {

/**
 *   This method give the remote object the viewTransform, the bang
 *   space refrence and the Canvas3D
 */
 public void createScene(TransformGroup theViewGroup, BangSpace space, 
						Canvas3D theCanvas);

/**
 *   This method returns the root BranchGroup from the remote object
 */
 public BranchGroup getScene3D();

/**
 *   This method returns the viewpoints from the remote object
 */
 public TransformGroup[] getViewpoints();

/**
 *   This method returns the 2D JPanel GUI to be displayed and 
 *   Bang displays the Panel in its master tabpane
 */
 public JPanel getGUI();

/**
 *   This method returns the name to put into the master tabpane
 */
 public String getGUIName();
}
