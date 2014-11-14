package com.coggroach.modelrenderer.graphics;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.SystemClock;

import com.coggroach.modelrenderer.R;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by TARDIS on 13/11/2014.
 */
public class MModelRenderer extends AbstractGLRenderer
{
    Model model;

    private float[] mModelMatrix = new float[16];
    private final FloatBuffer mModelVertices;
    private final FloatBuffer mModelColours;
    //private final IntBuffer mModelIndices;

    Context context;

    public MModelRenderer(Context c)
    {
        this.context = c;
        this.model = new Model(c, R.raw.cylinder_triads_texture);

        mModelVertices = ByteBuffer.allocateDirect(model.getVerticesLength() * mBytesPerFloat).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mModelColours = ByteBuffer.allocateDirect(model.getTexturesLength() * mBytesPerFloat).order(ByteOrder.nativeOrder()).asFloatBuffer();
        //mModelIndices = ByteBuffer.allocateDirect(model.get() * mBytesPerFloat).order(ByteOrder.nativeOrder()).asIntBuffer();

        mModelVertices.put(Model.getArrayListAsPrimitive(model.getVertices())).position(0);
        mModelColours.put(Model.getArrayListAsPrimitive(model.getTextures())).position(0);
       //mModelIndices.put(Model.getArrayListAsPrimitive(model.getVertices())).position(0);
    }

    @Override
    public String getVertexShader()
    {
        final String vertexShader =
                "uniform mat4 u_MVPMatrix;      \n"		// A constant representing the combined model/view/projection matrix.

                        + "attribute vec4 a_Position;     \n"		// Per-vertex position information we will pass in.
                        + "attribute vec4 a_Color;        \n"		// Per-vertex color information we will pass in.

                        + "varying vec4 v_Color;          \n"		// This will be passed into the fragment shader.

                        + "void main()                    \n"		// The entry point for our vertex shader.
                        + "{                              \n"
                        + "   v_Color = a_Color;          \n"		// Pass the color through to the fragment shader.
                        // It will be interpolated across the triangle.
                        + "   gl_Position = u_MVPMatrix   \n" 	// gl_Position is a special variable used to store the final position.
                        + "               * a_Position;   \n"     // Multiply the vertex by the matrix to get the final point in
                        + "}                              \n";    // normalized screen coordinates.
        return vertexShader;
    }

    @Override
    public String getFragmentShader()
    {
        final String fragmentShader =
                "precision mediump float;       \n"		// Set the default precision to medium. We don't need as high of a
                        // precision in the fragment shader.
                        + "varying vec4 v_Color;          \n"		// This is the color from the vertex shader interpolated across the
                        // triangle per fragment.
                        + "void main()                    \n"		// The entry point for our fragment shader.
                        + "{                              \n"
                        + "   gl_FragColor = v_Color;     \n"		// Pass the color directly through the pipeline.
                        + "}                              \n";
        return fragmentShader;
    }

    @Override
    public String getVertexShader(int resId)
    {
        return getVertexShader();
    }

    @Override
    public String getFragmentShader(int resId)
    {
        return getFragmentShader();
    }

    @Override
    public void setViewMatrix()
    {
        // Position the eye behind the origin.
        final float eyeX = 0.0f;
        final float eyeY = 0.0f;
        final float eyeZ = 3.0f;

        // We are looking toward the distance
        final float lookX = 0.0f;
        final float lookY = 0.0f;
        final float lookZ = -5.0f;

        // Set our up vector. This is where our head would be pointing were we holding the camera.
        final float upX = 0.0f;
        final float upY = 1.0f;
        final float upZ = 0.0f;

        // Set the view matrix. This matrix can be said to represent the camera position.
        // NOTE: In OpenGL 1, a ModelView matrix is used, which is a combination of a model and
        // view matrix. In OpenGL 2, we can keep track of these matrices separately if we choose.
        Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);
    }

    @Override
    public void setProjectionMatrix(int i, int j)
    {
        // Create a new perspective projection matrix. The height will stay the same
        // while the width will vary as per aspect ratio.
        final float ratio = (float) i / j;
        final float left = -ratio;
        final float right = ratio;
        final float bottom = -1.0f;
        final float top = 1.0f;
        final float near = 1.0f;
        final float far = 10.0f;

        Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, near, far);
    }

    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config)
    {
        //super.onSurfaceCreated(glUnused, config);
        GLES20.glClearColor(1.0F, 1.0F, 1.0F, 1.0F);
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        this.setViewMatrix();

        final String vertexShader = getVertexShader();
        final String fragmentShader = getFragmentShader();

        final int vertexShaderHandle = compileShader(GLES20.GL_VERTEX_SHADER, vertexShader);
        final int fragmentShaderHandle = compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShader);

        mPerVertexProgramHandle = createAndLinkProgram(vertexShaderHandle, fragmentShaderHandle, new String[] {"u_MVPMatrix",  "a_Position", "a_Color"});

        // Set program handles. These will later be used to pass in values to the program.
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mPerVertexProgramHandle, "u_MVPMatrix");
        mPositionHandle = GLES20.glGetAttribLocation(mPerVertexProgramHandle, "a_Position");
        mColorHandle = GLES20.glGetAttribLocation(mPerVertexProgramHandle, "a_Color");

        // Tell OpenGL to use this program when rendering.
        GLES20.glUseProgram(mPerVertexProgramHandle);
    }

    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height)
    {
        super.onSurfaceChanged(glUnused, width, height);
        this.setProjectionMatrix(width, height);
    }

    @Override
    public void onDrawFrame(GL10 glUnused)
    {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        // Do a complete rotation every 10 seconds.
        long time = SystemClock.uptimeMillis() % 10000L;
        float angleInDegrees = (360.0f / 10000.0f) * ((int) time);

        // Draw the triangle facing straight on.
        Matrix.setIdentityM(mModelMatrix, 0);
        //Matrix.translateM(mModelMatrix, 0, -2.0f, 0.0f, 0.0f);
        Matrix.rotateM(mModelMatrix, 0, angleInDegrees, 0.0f, 0.5f, 0.0f);

        // Pass in the position information
        mModelVertices.position(0);
        GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false,  0, mModelVertices);

        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // Pass in the color information
        mModelColours.position(0);
        GLES20.glVertexAttribPointer(mColorHandle, 4, GLES20.GL_FLOAT, false, 0, mModelColours);

        GLES20.glEnableVertexAttribArray(mColorHandle);

        // This multiplies the view matrix by the model matrix, and stores the result in the MVP matrix
        // (which currently contains model * view).
        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);

        // This multiplies the modelview matrix by the projection matrix, and stores the result in the MVP matrix
        // (which now contains model * view * projection).
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);

        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
        //GLES20.glDrawElements(GLES20.GL_TRIANGLES, model.getIndicesLength(), GLES20.GL_UNSIGNED_INT, mModelIndices);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, model.getVerticesLength()/3);
        //GLES20.glDisable(GLES20.GL_CULL_FACE);
    }
}
