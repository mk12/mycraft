//  
//  GameRenderer.java
//  MyCraft
//  
//  Created on 07/12/2011.
//  Copyright (c) 2011 Mitchell Kember. All rights reserved.
//
//  This software is provided 'as-is', without any express or implied
//  warranty. In no event will the authors be held liable for any damages
//  arising from the use of this software.
//  
//  Permission is granted to anyone to use this software for any purpose,
//  including commercial applications, and to alter it and redistribute it
//  freely, subject to the following restrictions:
//  
//  1. The origin of this software must not be misrepresented; you must not
//  claim that you wrote the original software. If you use this software
//  in a product, an acknowledgment in the product documentation would be
//  appreciated but is not required.
//  
//  2. Altered source versions must be plainly marked as such, and must not be
//  misrepresented as being the original software.
//  
//  3. This notice may not be removed or altered from any source
//  distribution.
//  

package com.hecticcraft.mycraft;

import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.IntBuffer;
import java.util.logging.Level;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.ARBVertexBufferObject;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.*;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.opengl.PixelFormat;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

/**
 * GameRenderer is responsible for managing the application's window and
 * rendering an instance of GameState into it.
 * 
 * @author Mitchell Kember
 * @since 07/12/2011
 */
final class GameRenderer implements GameStateListener {
    
    private static final int DISPLAY_WIDTH = 800;
    private static final int DISPLAY_HEIGHT = 600;
    private static final int DEPTH_BUFFER_BITS = 24;
    private static final int DESIRED_SAMPLES = 8;
    
    private static final String WINDOW_TITLE = "MyCraft";
    
    /**
     * The width and height of the cross hairs in the middle of the screen.
     */
    private static final float CROSSHAIR_SIZE = 0.1f;
    
    /**
     * The furthest away from this Camera an object that will be rendered can be.
     */
    private float renderDistance = 100;
    
    /**
     * The ID for the Vertex Buffer Object (VBO).
     */
    private int bufferObjectID;
    
    private Texture dirtTexture;
    
    private int numVerts;
    
    /**
     * Creates a new GameRenderer and sets up the LWJGL window.
     * 
     * @throws LWJGLException if there is an error setting up the window
     */
    GameRenderer() throws LWJGLException {
        Display.setDisplayMode(new DisplayMode(DISPLAY_WIDTH, DISPLAY_HEIGHT));
        Display.setFullscreen(false);
        Display.setTitle(WINDOW_TITLE);
        
        // Try using oversampling for smooth edges.
        try {
            Display.create(new PixelFormat(0, DEPTH_BUFFER_BITS, 0, DESIRED_SAMPLES));
        } catch (LWJGLException lwjgle) {
            // Replace this with text on screen
            System.out.println("Could not enable anti-aliasing. Brace yourself for jaggies.");
            Display.create(new PixelFormat(0, DEPTH_BUFFER_BITS, 0, 0));
        }
        
        prepareOpenGL();
        resizeOpenGL();
        initializeData();
        loadTextures();
    }
    
    /**
     * Enables and Disables various OpenGL states. This should be called once when
     * the GameRenderer is created, before any rendering.
     */
    private void prepareOpenGL() {
        glEnable(GL_CULL_FACE);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_TEXTURE_2D);

        glDisable(GL_ALPHA_TEST);
        glDisable(GL_STENCIL_TEST);
        glDisable(GL_DITHER);
        glDisable(GL_LIGHTING);
        
        glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
        glClearColor(0.929f, 0.929f, 0.929f, 0.0f);
    }
    
    /**
     * Resizes the OpenGL viewport and recalculates the projection matrix.
     */
    private void resizeOpenGL() {
        glViewport(0, 0, DISPLAY_WIDTH, DISPLAY_HEIGHT);

        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        gluPerspective(45, (float)DISPLAY_WIDTH / (float)DISPLAY_HEIGHT, 1.f, renderDistance);

        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
    }
    
    /**
     * Renders a GameState.
     * 
     * @param state the GameState to render
     */
    void render(GameState state) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        
        glLoadIdentity();
        state.getPlayerView().updateMatrix();
        glColor3b((byte)127, (byte)127, (byte)127);
        
        // Start at 1 to avoid drawing 1st degenerate and messing everything else up
        glDrawArrays(GL_TRIANGLE_STRIP, 1, numVerts);
        
        glLoadIdentity();
        // Draw crosshair
        glColor3b((byte)-127, (byte)-127, (byte)-127);
        glBegin(GL_LINES);
        glVertex3f(-CROSSHAIR_SIZE/2, 0, -1); glVertex3f(CROSSHAIR_SIZE/2, 0, -1);
        glVertex3f(0, -CROSSHAIR_SIZE/2, -1); glVertex3f(0, CROSSHAIR_SIZE/2, -1);
        glEnd();
        
        Display.update();
        Display.sync(60);
    }
    
    /**
     * Loads textures that will be used by this GameRenderer.
     */
    private void loadTextures() {
        try {
            dirtTexture = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("res/dirt.png"));
        } catch (IOException ioe) {
            MyCraft.LOGGER.log(Level.WARNING, ioe.toString(), ioe);
        }
        
        // Use mipmaps!
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        
        dirtTexture.bind();
    }
    
    /**
     * Creates the VBO that this GameRenderer will use.
     * 
     * @throws LWJGLException if VBOs are not supported
     */
    private void initializeData() throws LWJGLException {
        if (!GLContext.getCapabilities().GL_ARB_vertex_buffer_object) {
            MyCraft.LOGGER.log(Level.SEVERE, "GL_ARB_vertex_buffer_object not supported.");
            throw new LWJGLException("GL_ARB_vertex_buffer_object not supported");
        }
        
        bufferObjectID = ARBVertexBufferObject.glGenBuffersARB();

        // Vertex Data interleaved format: XYZST
        final int position  = 3;
        final int texcoords = 2;
        final int sizeOfInt = 4; // 4 bytes in an int
        final int vertexDataSize = (position + texcoords) * sizeOfInt;
        
        /*
        final int data[] = { // 30 vertices
            // Ground
            0, 0, 0,     0, 0,
            100,0,0,     100, 0,
            0,0,-100,    0, 100,
            100,0,-100,  100, 100,

            // Cube
            0, 0, 0,     0, 0, // degenerate
            0, 0, 0,     0, 0, // degenerate

            0, 0, 0,     1, 0,
            1, 0, 0,     1, 1,

            0, 1, 0,     0, 0,
            1, 1, 0,     0, 1,

            0, 1,-1,     1, 0,
            1, 1,-1,     1, 1,

            0, 0,-1,     0, 0,
            1, 0,-1,     0, 1,

            0, 0, 0,     1, 0,
            1, 0, 0,     1, 1,

            1, 0, 0,     0, 0, // degenerate
            1, 0, 0,     0, 0, // degenerate

            1, 0, 0,     0, 1,
            1, 0,-1,     1, 1,

            1, 1, 0,     0, 0,
            1, 1,-1,     1, 0,

            1, 1,-1,     0, 0, // degenerate
            0, 1,-1,     0, 0, // degenerate

            0, 1,-1,     0, 0,
            0, 0,-1,     0, 1,

            0, 1, 0,     1, 0,
            0, 0, 0,     1, 1,
        };

        IntBuffer dataBuffer = BufferUtils.createIntBuffer(30*vertexDataSize);
        dataBuffer.put(data);
        dataBuffer.flip();*/

        ARBVertexBufferObject.glBindBufferARB(ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, bufferObjectID);
        //ARBVertexBufferObject.glBufferDataARB(ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, dataBuffer, ARBVertexBufferObject.GL_DYNAMIC_DRAW_ARB);

        glVertexPointer(3, GL_INT, vertexDataSize, 0);
        glTexCoordPointer(2, GL_INT, vertexDataSize, position*sizeOfInt);
        glEnableClientState(GL_VERTEX_ARRAY);
        glEnableClientState(GL_TEXTURE_COORD_ARRAY);
    }
    
    /**
     * Calculates vertices for a cube located at ({@code x}, {@code y}, {@code z}).
     * The vertices must be rendered with GL_TRIANGLE_STRIP.
     * 
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @param z the z-coordinate
     * @return the vertices in interleaved XYZST format
     */
    private int[] cubeData(int x, int y, int z) {
        return new int[]{ // 23*5 ints
            x, y, z,     0, 0, // degenerate
            
            x, y, z,     1, 0,
            x+1,y,z,     1, 1,
            
            x,y+1,z,     0, 0,
            x+1,y+1,z,   0, 1,
            
            x,y+1,z-1,   1, 0,
            x+1,y+1,z-1, 1, 1,
            
            x,y,z-1,     0, 0,
            x+1,y,z-1,   0, 1,
            
            x, y, z,     1, 0,
            x+1,y,z,     1, 1,
            
            x+1,y,z,     0, 0, // degenerate
            x+1,y,z,     0, 0, // degenerate
            
            x+1,y,z,     0, 1,
            x+1,y,z-1,   1, 1,
            
            x+1,y+1,z,   0, 0,
            x+1,y+1,z-1, 1, 0,
            
            x+1,y+1,z-1, 0, 0, // degenerate
            x,y+1,z-1,   0, 0, // degenerate
            
            x,y+1,z-1,   0, 0,
            x,y,z-1,     0, 1,
            
            x,y+1,z,     1, 0,
            x, y, z,     1, 1,
            
            x, y, z,     0, 0, // degenerate
        };
    }

    /**
     * Updates the VBO when the a {@code chunk} in the GameState has changed.
     * 
     * @param chunk the chunk that has changed
     */
    @Override
    public void gameStateChunkChanged(Chunk chunk) {
        byte[][][] data = chunk.getData();
        IntBuffer vertexData = BufferUtils.createIntBuffer(70000);
        
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                for (int z = 0; z > -8; z--) {
                    if (data[x][y][-z] != 0) vertexData.put(cubeData(x,y,z));
                }
            }
        }
        
        numVerts = vertexData.position() / 5;
        vertexData.flip();
        
        ARBVertexBufferObject.glBufferDataARB(ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, vertexData, ARBVertexBufferObject.GL_DYNAMIC_DRAW_ARB);
    }
}
