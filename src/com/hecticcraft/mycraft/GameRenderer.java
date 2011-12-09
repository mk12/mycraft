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

import org.lwjgl.LWJGLException;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.*;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.PixelFormat;

//http://www.java-gaming.org/index.php?topic=23813.0


/**
 * GameRenderer is responsible for managing the application's window and
 * rendering an instance of GameState into it.
 * 
 * @author Mitchell Kember
 * @since 07/12/2011
 */
final class GameRenderer {
    
    private Camera camera = new Camera(new Vector(0, 0, 20), new Vector(0, 0, -1));
    
    private static final int DISPLAY_WIDTH = 800;
    private static final int DISPLAY_HEIGHT = 600;
    private static final int DESIRED_SAMPLES = 8;
    
    private static final String WINDOW_TITLE = "MyCraft";
    
    private int bufferObjectID;
    
    GameRenderer() throws LWJGLException {
        // Display
        Display.setDisplayMode(new DisplayMode(DISPLAY_WIDTH, DISPLAY_HEIGHT));
        Display.setFullscreen(false);
        Display.setTitle(WINDOW_TITLE);
        
        // Try using oversampling for smooth edges.
        try {
            Display.create(new PixelFormat(0, 0, 0, DESIRED_SAMPLES));
        } catch (LWJGLException lwjgle) {
            System.out.println("Your system doesn't appear to support anti-aliasing.");
            Display.create();
        }
        
        prepareOpenGL();
        resizeOpenGL();
        initializeData();
    }
    
    private void prepareOpenGL() {
        glEnable(GL_CULL_FACE);
        glEnable(GL_DEPTH_TEST);

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
        gluPerspective(45, DISPLAY_WIDTH / DISPLAY_HEIGHT, 1.f, 30.f);

        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
    }
    
    // Remeber, player will be moved (in state), camera moves accordingly
    void processInput(boolean up, boolean down, boolean left, boolean right, int x, int y) {
        glLoadIdentity();
        
        if (up) camera.moveForward(0.1f);
        if (down) camera.moveForward(-0.1f);
        if (left) camera.strafeRight(-0.1f);
        if (right) camera.strafeRight(0.1f);
        camera.rotateX(y/5.f);
        camera.rotateY(x/5.f);
        
        camera.updateMatrix();
    }
    
    void render(GameState state) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        
        glColor3f(0.1f, 0.55f, 0.87f);
        drawQuad(0, 1.5f, 0, 3);
        
        Display.update();
        Display.sync(60);
    }
    
    private void initializeData() {/*
        if (GLContext.getCapabilities().GL_ARB_vertex_buffer_object) {
            System.out.println("VBO enabled!");
            bufferObjectID = ARBVertexBufferObject.glGenBuffersARB();
            
            float verts[] = {  1,  1, -1,
                              -1,  1, -1,
                               1,  1,  1,
                              -1,  1,  1,
                               1, -1,  1,
                              -1, -1,  1,
                               1, -1, -1,
                              -1, -1, -1,
                               1,  1, -1 };
            
            
            
            ARBVertexBufferObject.glBindBufferARB(ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, bufferObjectID);
            ARBVertexBufferObject.glBufferDataARB(ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, BufferUtils.createFloatBuffer(9*3).put(verts), ARBVertexBufferObject.GL_STATIC_DRAW_ARB);
            
            glVertexPointer(3, GL_FLOAT, 0, 0);
            glEnableClientState(GL_VERTEX_ARRAY);
        }*/
    }
    
    private void drawQuad(float x, float y, float z, float size) {
        size /= 2;
        
        glBegin(GL_TRIANGLE_STRIP);
        glVertex3f(x+size, y+size, z-size); glVertex3f(x-size, y+size, z-size);
        glVertex3f(x+size, y+size, z+size); glVertex3f(x-size, y+size, z+size);
        glVertex3f(x+size, y-size, z+size); glVertex3f(x-size, y-size, z+size);
        glVertex3f(x+size, y-size, z-size); glVertex3f(x-size, y-size, z-size);
        glVertex3f(x+size, y+size, z-size); glVertex3f(x-size, y+size, z-size);
        glEnd();
        
        glBegin(GL_TRIANGLE_STRIP);
        glVertex3f(x-size, y+size, z-size); glVertex3f(x-size, y-size, z-size); glVertex3f(x-size, y+size, z+size); glVertex3f(x-size, y-size, z+size);
        glEnd();
        
        glBegin(GL_TRIANGLE_STRIP);
        glVertex3f(x+size, y+size, z+size); glVertex3f(x+size, y-size, z+size); glVertex3f(x+size, y+size, z-size); glVertex3f(x+size, y-size, z-size);
        glEnd();
    }
}
