package com.coggroach.modelrenderer.graphics;

import android.content.Context;
import android.util.Log;

import com.coggroach.modelrenderer.common.ResourceReader;

import java.util.Random;

/**
 * Created by TARDIS on 13/11/2014.
 */
public class Model
{
    private String name;
    private String raw;
    private float[] data;
    private float[] colours; // RGB 4 Data
    private int dataLength;
    private int colorLength;

    public Model(Context c, int resId)
    {
        this.dataLength = 0;
        this.raw = ResourceReader.getString(c, resId);
        //Log.i("Model", this.raw);
        this.count(this.raw);
        this.data = new float[this.dataLength];
        this.colorLength = (int)(this.dataLength * (float)4/3);

        this.colours = new float[this.colorLength];
        this.populatePosition(this.raw);
        this.generateColors();

        Log.i(name, String.valueOf(dataLength));
        Log.i(name, String.valueOf(colorLength));
        print();
    }

    public void print()
    {
        String s = "";
        for(int i = 0; i < this.data.length; i+=3)
        {
            s += "v: ";
            s += String.valueOf(data[i]);
            s += " ";
            s += String.valueOf(data[i+1]);
            s += " ";
            s += String.valueOf(data[i+2]);
            s += " ";

            Log.i(this.name, s);
            s = "";
        }
    }

    public void count(String s)
    {
        String[] lines = s.split("\n");
        for(int i = 0; i < lines.length; i++) {
            char c = lines[i].charAt(0);
            switch (c) {
                case 'o':
                    this.name = lines[i].replaceAll("o ", "");
                    break;
                case 'v':
                    this.dataLength += 3;
                    break;
                case 'f':
                    break;
                default:
                    break;
            }
        }
    }

    public void populatePosition(String s)
    {
        String[] lines = s.split("\n");
        int index = 0;
        for(int i = 0; i < lines.length; i++)
        {
            if(lines[i].charAt(0) == 'v')
            {
                String[] subLines = lines[i].split(" ");

                this.data[index] = Float.valueOf(subLines[1]);
                this.data[index+1] = Float.valueOf(subLines[2]);
                this.data[index+2] = Float.valueOf(subLines[3]);

                index += 3;
            }
        }
    }

    public void generateColors()
    {
        Random rand = new Random();
        for(int i = 0; i < this.colours.length; i++)
        {
            if( (i+1) % 4 == 0)
            {
                this.colours[i] = 1.0F;
            }
            else
            {
                this.colours[i] = rand.nextFloat();
            }
        }
    }

    public float[] getPositionData()
    {
        return this.data;
    }

    public float[] getColourData()
    {
        return this.colours;
    }

    public String getName()
    {
        return this.name;
    }

    public int getPositionLength()
    {
        return this.dataLength;
    }

    public int getColorLength()
    {
        return this.colorLength;
    }

}
