// copyright (c) 1997,1998 stephen f. white
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
package org.bang.net.vnet;

import java.io.*;

public class VMFColor extends VField
{
    VSFColor[]	colors;

    public VMFColor(DataInputStream in) throws IOException
    {
	colors = new VSFColor[in.readInt()];
	for (int i = 0; i < colors.length; i++) {
	    colors[i] = new VSFColor(in);
	}
    }

    public void write(DataOutputStream out) throws IOException
    {
	out.writeInt(colors.length);
	for (int i = 0; i < colors.length; i++) {
	    colors[i].write(out);
	}
    }

    public byte getType() { return MFCOLOR; }
}
