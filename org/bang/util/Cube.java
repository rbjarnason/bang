package org.bang.util;

import javax.media.j3d.*;
import javax.vecmath.*;

public class Cube extends Shape3D {
    private static final float[] verts = {
    // front face
	 1.0f, -1.0f,  1.0f,
	 1.0f,  1.0f,  1.0f,
	-1.0f,  1.0f,  1.0f,
	-1.0f, -1.0f,  1.0f,
    // back face
	-1.0f, -1.0f, -1.0f,
	-1.0f,  1.0f, -1.0f,
	 1.0f,  1.0f, -1.0f,
	 1.0f, -1.0f, -1.0f,
    // right face
	 1.0f, -1.0f, -1.0f,
	 1.0f,  1.0f, -1.0f,
	 1.0f,  1.0f,  1.0f,
	 1.0f, -1.0f,  1.0f,
    // left face
	-1.0f, -1.0f,  1.0f,
	-1.0f,  1.0f,  1.0f,
	-1.0f,  1.0f, -1.0f,
	-1.0f, -1.0f, -1.0f,
    // top face
	 1.0f,  1.0f,  1.0f,
	 1.0f,  1.0f, -1.0f,
	-1.0f,  1.0f, -1.0f,
	-1.0f,  1.0f,  1.0f,
    // bottom face
	-1.0f, -1.0f,  1.0f,
	-1.0f, -1.0f, -1.0f,
	 1.0f, -1.0f, -1.0f,
	 1.0f, -1.0f,  1.0f,
    };

    private static final Vector3f[] normals = {
	new Vector3f( 0.0f,  0.0f,  1.0f),	// front face
	new Vector3f( 0.0f,  0.0f, -1.0f),	// back face
	new Vector3f( 1.0f,  0.0f,  0.0f),	// right face
	new Vector3f(-1.0f,  0.0f,  0.0f),	// left face
	new Vector3f( 0.0f,  1.0f,  0.0f),	// top face
	new Vector3f( 0.0f, -1.0f,  0.0f),	// bottom face
    };

    public Cube() {
	super();

	int i;

	QuadArray cube = new QuadArray(24, QuadArray.COORDINATES |
		QuadArray.NORMALS);

	cube.setCoordinates(0, verts);
        for (i = 0; i < 24; i++) {
            cube.setNormal(i, normals[i/4]);
        }

	cube.setCapability(Geometry.ALLOW_INTERSECT);
        setGeometry(cube);


    Color3f eColor    = new Color3f(0.0f, 0.0f, 0.0f);
	  Color3f sColor    = new Color3f(1.0f, 1.0f, 1.0f);
	  Color3f objColor  = new Color3f(0.6f, 0.6f, 0.6f);
	  Color3f lColor1   = new Color3f(1.0f, 0.0f, 0.0f);
	  Color3f lColor2   = new Color3f(0.0f, 1.0f, 0.0f);
	  Color3f alColor   = new Color3f(0.2f, 0.2f, 0.2f);
	  Color3f bgColor   = new Color3f(0.05f, 0.05f, 0.2f);

Material m = new Material(objColor, eColor, objColor, sColor, 100.0f);
	Appearance a = new Appearance();
	  m.setLightingEnable(true);
	  a.setMaterial(m);

        setAppearance(a);
    }
}
