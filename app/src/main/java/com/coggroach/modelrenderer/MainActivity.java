package com.coggroach.modelrenderer;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

import com.coggroach.modelrenderer.graphics.MModelRenderer;
import com.coggroach.modelrenderer.graphics.TModelRenderer;

/**
 * Created by TARDIS on 13/11/2014.
 */
public class MainActivity extends Activity
{
    private GLSurfaceView mGLView;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mGLView = new GLSurfaceView(this);
        mGLView.setEGLContextClientVersion(2);

        mGLView.setRenderer(
                new MModelRenderer(this)
                //new TModelRenderer(this)
        );

        this.setContentView(mGLView);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        mGLView.onPause();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        mGLView.onResume();
    }
}
