package com.coggroach.modelrenderer.common;

import android.content.Context;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by TARDIS on 13/11/2014.
 */
public class ResourceReader
{
    public static String getString(Context c, int resId)
    {
        String s = "";

        try
        {
            InputStreamReader iStreamReader = new InputStreamReader( c.getResources().openRawResource(resId) );
            BufferedReader reader = new BufferedReader(iStreamReader);

            String line;

            while( (line = reader.readLine()) != null)
            {
                s += line;
                s += "\n";
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        return s;
    }
}
