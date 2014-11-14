package com.coggroach.modelrenderer.graphics;

import android.content.Context;
import android.util.Log;

import com.coggroach.modelrenderer.common.ResourceReader;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

/**
 * Created by TARDIS on 13/11/2014.
 */
public class Model
{
    private String name;
    private ArrayList<Float> vertices;
    private ArrayList<Float> textures;
    private ArrayList<Float> normals;
    private ArrayList<Short> faceVertices;
    private ArrayList<Short> faceTextures;
    private ArrayList<Short> faceNormals;


    public Model(Context c, int resId)
    {
        vertices = new ArrayList<Float>();
        textures = new ArrayList<Float>();
        normals = new ArrayList<Float>();
        faceVertices = new ArrayList<Short>();
        faceTextures = new ArrayList<Short>();
        faceNormals = new ArrayList<Short>();

        populate(ResourceReader.getString(c, resId));
    }

    public static void addFloatToArrayList(ArrayList<Float> list, String s, String regex)
    {
        if(s != null)
        {
            String[] subLine = s.split(regex);
            if (subLine != null)
                for (int j = 1; j < subLine.length; j++) {
                    if (subLine[j] != null)
                    {
                        Float f = Float.valueOf(subLine[j]);
                        if (f != null)
                            list.add(f);
                    }
                }
        }
    }

    public void populate(String s)
    {
        String[] lines = s.split("\n");
        for(int i = 0; i < lines.length; i++)
        {
            if(lines[i] != null) {
                if (lines[i].contains("v ")) {
                    addFloatToArrayList(vertices, lines[i], " ");
                } else if (lines[i].contains("vt ")) {
                    addFloatToArrayList(textures, lines[i], " ");
                } else if (lines[i].contains("vn ")) {
                    addFloatToArrayList(normals, lines[i], " ");
                } else if (lines[i].contains("f ")) {
                    String[] faces = lines[i].split("/");
                    if(faces.length == 3)
                    {
                        faceVertices.add(Short.valueOf(faces[0]));
                        faceTextures.add(Short.valueOf(faces[1]));
                        faceNormals.add(Short.valueOf(faces[2]));
                    }
                }
            }
        }
    }

    public String getName()
    {
        return this.name;
    }

    public static Object[] getArrayListAsObject(ArrayList<Float> list)
    {
        return list.toArray(new Float[list.size()]);
    }

    public static float[] getArrayListAsPrimitive(ArrayList<Float> list)
    {
        float[] primitive = new float[list.size()];
        int i = 0;
        for(Float f : list)
        {
            primitive[i++] = (f != null ? f : Float.NaN);
        }
        return primitive;
    }

    public ArrayList<Float> getVertices()
    {
        return this.vertices;
    }

    public ArrayList<Float> getTextures()
    {
        return this.textures;
    }

    public ArrayList<Float> getNormals()
    {
        return this.normals;
    }

    public int getVerticesLength()
    {
        return this.vertices.size();
    }

    public int getNormalsLength()
    {
        return this.normals.size();
    }

    public int getTexturesLength()
    {
        return this.textures.size();
    }
}
