//  
//  GameRenderer.java
//  MyCraft
//  
//  Created on 06/12/2011.
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
import java.nio.FloatBuffer;
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

//http://www.java-gaming.org/index.php?topic=23813.0


/**
 * GameRenderer is responsible for managing the application's window and
 * rendering an instance of GameState into it.
 * 
 * @author Mitchell Kember
 * @since 07/12/2011
 */
final class GameRenderer {
    
    private Camera camera = new Camera();
    
    private static final int DISPLAY_WIDTH = 800;
    private static final int DISPLAY_HEIGHT = 600;
    private static final int DESIRED_SAMPLES = 8;
    
    private static final String WINDOW_TITLE = "MyCraft";
    
    private int bufferObjectID;
    private Texture dirtTexture;
    
    GameRenderer() throws LWJGLException {
        // Display
        Display.setDisplayMode(new DisplayMode(DISPLAY_WIDTH, DISPLAY_HEIGHT));
        Display.setFullscreen(false);
        Display.setTitle(WINDOW_TITLE);
        
        // Try using oversampling for smooth edges.
        try {
            Display.create(new PixelFormat(0, 0, 0, DESIRED_SAMPLES));
        } catch (LWJGLException lwjgle) {
            // Replace this with text on screen
            System.out.println("Could not enable anti-aliasing. Prepare your eyes for jaggies.");
            Display.create();
        }
        
        prepareOpenGL();
        resizeOpenGL();
        initializeData();
        loadTextures();
        
        camera.move(new Vector(1, 3, 10));
    }
    
    private void prepareOpenGL() {
        glEnable(GL_CULL_FACE);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_TEXTURE_2D);

        glDisable(GL_ALPHA_TEST);
        glDisable(GL_STENCIL_TEST);
        glDisable(GL_DITHER);
        glDisable(GL_LIGHTING);
        
        glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
        glDepthFunc(GL_LEQUAL);
        glClearDepth(1.0f);
        glClearColor(0.929f, 0.929f, 0.929f, 0.0f);
    }
    
    private void resizeOpenGL() {
        glViewport(0, 0, DISPLAY_WIDTH, DISPLAY_HEIGHT);

        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        gluPerspective(45, (float)DISPLAY_WIDTH / (float)DISPLAY_HEIGHT, 1.f, 100.f);

        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
    }
    
    // Remeber, player will be moved (in state), camera moves accordingly
    void moveCamera(boolean up, boolean down, boolean left, boolean right, float x, float y) {
        glLoadIdentity();
        
        if (up) camera.moveForward(0.1f);
        if (down) camera.moveForward(-0.1f);
        if (left) camera.strafeRight(-0.1f);
        if (right) camera.strafeRight(0.1f);
        camera.pitch(y);
        camera.yaw(-x);
        
        camera.updateMatrix();
    }
    
    void render(GameState state) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        
        glDrawArrays(GL_TRIANGLE_STRIP, 0, 30);
        
        Display.update();
        Display.sync(60);
    }
    
    private void loadTextures() {
        try {
            dirtTexture = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("res/dirt.png"));
        } catch (IOException ioe) {
            MyCraft.LOGGER.log(Level.WARNING, ioe.toString(), ioe);
        }
        
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        
        dirtTexture.bind();
    }
    
    private void initializeData() {
        if (GLContext.getCapabilities().GL_ARB_vertex_buffer_object) {
            bufferObjectID = ARBVertexBufferObject.glGenBuffersARB();
            
            // Vertex Data interleaved format: XYZST
            final int position  = 3;
            final int texcoords = 2;
            final int sizeOfFloat = 4; // 4 bytes in a float
            final int vertexDataSize = (position + texcoords) * sizeOfFloat;
            
            final float data[] = { // 30 vertices
                // Ground
                0, 0, 0,     0, 0,
                100,0,0,     1, 0,
                0,0,-100,    0, 1,
                100,0,-100,  1, 1,
                
                0, 0, 0,     0, 0, // degenerate
                0, 0, 0,     0, 0, // degenerate
                
                0, 0, 0,     1, 0,
                2, 0, 0,     1, 1,
                
                0, 2, 0,     0, 0,
                2, 2, 0,     0, 1,
                
                0, 2,-2,     1, 0,
                2, 2,-2,     1, 1,
                
                0, 0,-2,     0, 0,
                2, 0,-2,     0, 1,
                
                0, 0, 0,     1, 0,
                2, 0, 0,     1, 1,
                
                2, 0, 0,     0, 0, // degenerate
                2, 0, 0,     0, 0, // degenerate
                
                2, 0, 0,     0, 1,
                2, 0,-2,     1, 1,
                
                2, 2, 0,     0, 0,
                2, 2,-2,     1, 0,
                
                2, 2,-2,     1, 0, // degenerate
                0, 2,-2,     0, 0, // degenerate
                
                0, 2,-2,     0, 0,
                0, 0,-2,     0, 1,
                
                0, 2, 0,     1, 0,
                0, 0, 0,     1, 1,
            };
            
            FloatBuffer dataBuffer = BufferUtils.createFloatBuffer(30*vertexDataSize);
            dataBuffer.put(data);
            dataBuffer.flip();
            
            ARBVertexBufferObject.glBindBufferARB(ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, bufferObjectID);
            ARBVertexBufferObject.glBufferDataARB(ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, dataBuffer, ARBVertexBufferObject.GL_STATIC_DRAW_ARB);
            
            glVertexPointer(3, GL_FLOAT, vertexDataSize, 0);
            glTexCoordPointer(2, GL_FLOAT, vertexDataSize, position*sizeOfFloat);
            glEnableClientState(GL_VERTEX_ARRAY);
            glEnableClientState(GL_TEXTURE_COORD_ARRAY);
        } else {
            MyCraft.LOGGER.log(Level.SEVERE, "GL_ARB_vertex_buffer_object not supported. Bailing out.");
            System.exit(1);
        }
    }
}
