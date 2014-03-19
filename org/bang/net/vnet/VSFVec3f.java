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

public class VSFVec3f extends VField
{
    private float[]	values = new float[3];

    public VSFVec3f(float x, float y, float z)
    {
	values[0] = x;
	values[1] = y;
	values[2] = z;
    }

    public VSFVec3f(float[] values)
    {
        if (values.length != 3) {
            this.values[0] = values[0];
            this.values[1] = values[1];
            this.values[2] = values[2];
        } else {
            this.values = values;
	}
    }

    public VSFVec3f(DataInputStream in) throws IOException
    {
	values[0] = in.readFloat();
	values[1] = in.readFloat();
	values[2] = in.readFloat();
    }

    public void write(DataOutputStream out) throws IOException
    {
	out.writeFloat(values[0]);
	out.writeFloat(values[1]);
	out.writeFloat(values[2]);
    }

    public String toString()
    {
	return "(" + values[0] + ", " + values[1] + ", " + values[2] + ")";
    }

    public byte getType() { return SFVEC3F; }

    public float[] getValue() { return values; }

    public VSFVec3f plus(VSFVec3f v) {
	return new VSFVec3f(values[0] + v.values[0],
			    values[1] + v.values[1],
			    values[2] + v.values[2]);
    }

    public VSFVec3f minus(VSFVec3f v) {
	return new VSFVec3f(values[0] - v.values[0],
			    values[1] - v.values[1],
			    values[2] - v.values[2]);
    }
}
