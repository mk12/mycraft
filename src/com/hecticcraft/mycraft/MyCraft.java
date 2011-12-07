//  
//  MyCraft.java
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

/*
OpenGL
======
- postive Z axis: outside the screen
- negative Z axis: into the screen

- zNear/zFar: distance from camera (which moves around)
 */

package com.hecticcraft.mycraft;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.*;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.PixelFormat;

/**
 * MyCraft is an open source java game that uses the LightWeight Java
 * Game Library (LWJGL), based on the popular game Minecraft.
 * 
 * "Minecraft" is an official trademark of Mojang AB. This work is not
 * formally related to, endorsed by or affiliated with Minecraft or Mojang AB.
 * 
 * @author Mitchell Kember
 * @version 1.0 06/12/2011
 */
public class MyCraft {
    
    private static void drawBox() {
        glBegin(GL_TRIANGLE_STRIP);
        
        glEnd();
    }

    private static final int DISPLAY_WIDTH = 800;
    private static final int DISPLAY_HEIGHT = 600;
    private static final Logger LOGGER = Logger.getLogger(MyCraft.class.getName());
    private static final float SQUARE_SIZE_CHANGE = 0.1f;
    
    private float squareSize;
    private float squareX;
    private float squareY;
    private float squareZ;

    static {
        try {
            LOGGER.addHandler(new FileHandler("errors.log", true));
        } catch (IOException ioe) {
            LOGGER.log(Level.WARNING, ioe.toString(), ioe);
        }
    }

    public static void main(String[] args) {
        MyCraft main = null;
        try {
            System.out.println("YourCraft is starting up.");
            main = new MyCraft();
            main.create();
            main.run();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.toString(), e);
        } finally {
            if (main != null) {
                main.destroy();
            }
        }
    }

    public MyCraft() {
        squareSize = 0.5f;
        squareX = 0.f;
        squareY = 0.f;
        squareZ = 0.f;
    }

    public void create() throws LWJGLException {
        // Display
        Display.setDisplayMode(new DisplayMode(DISPLAY_WIDTH, DISPLAY_HEIGHT));
        //Display.setVSyncEnabled(true);
        Display.setFullscreen(false);
        Display.setTitle("MyCraft");
        //Display.create(new PixelFormat(0, 0, 0, 8));
        Display.create();

        // Keyboard
        Keyboard.create();

        // Mouse
        Mouse.setGrabbed(false);
        Mouse.create();

        // OpenGL
        initGL();
        resizeGL();
    }

    public void destroy() {
        // Methods already check if created before destroying.
        Mouse.destroy();
        Keyboard.destroy();
        Display.destroy();
    }

    public void initGL() {
        glDisable(GL_CULL_FACE);
        glEnable(GL_DEPTH_TEST);

        glDisable(GL_ALPHA_TEST);
        glDisable(GL_STENCIL_TEST);
        glDisable(GL_DITHER);
        glDisable(GL_LIGHTING);

        glClearColor(0.929f, 0.929f, 0.929f, 0.0f);
    }

    public void processKeyboard() {
        // Square's Size
        if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
            squareSize -= SQUARE_SIZE_CHANGE;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {
            squareSize += SQUARE_SIZE_CHANGE;
        }

        // Square's Z
        if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
            squareZ++;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
            squareZ--;
        }
    }

    public void processMouse() {
        squareX = Mouse.getX();
        squareY = Mouse.getY();
    }

    public void render() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glLoadIdentity();

        // Draw a basic square
        gluLookAt(0.f, 0.f, 20.f,
                  0.f, 0.f,  0.f,
                  0.f, 1.f,  0.f);
        glRotatef(squareX, 1.f, 0.f, 0.f);
        glRotatef(squareY, 0.f, 1.f, 0.f);
        glRotatef(squareZ, 0.f, 0.f, 1.f);


        glColor3f(0.0f, 0.5f, 0.5f);
        glBegin(GL_TRIANGLE_STRIP);
        glTexCoord2f(0.0f, 0.0f);
        glVertex2f(-squareSize / 2, -squareSize / 2);
        glTexCoord2f(1.0f, 0.0f);
        glVertex2f(squareSize / 2, -squareSize / 2);
        glTexCoord2f(1.0f, 1.0f);
        glVertex2f(-squareSize / 2, squareSize / 2);
        glTexCoord2f(0.0f, 1.0f);
        glVertex2f(squareSize / 2, squareSize / 2);
        glEnd();
    }

    public void resizeGL() {
        // 2D Scene
        glViewport(0, 0, DISPLAY_WIDTH, DISPLAY_HEIGHT);

        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        //gluOrtho2D(0.0f, DISPLAY_WIDTH, 0.0f, DISPLAY_HEIGHT);
        gluPerspective(45, DISPLAY_WIDTH / DISPLAY_HEIGHT, 0.1f, 100.f);
        glPushMatrix();

        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        glPushMatrix();
    }

    public void run() {
        while (!Display.isCloseRequested() && !Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
            if (Display.isVisible()) {
                processKeyboard();
                processMouse();
                update();
                render();
            } else {
                if (Display.isDirty()) {
                    render();
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                }
            }
            Display.update();
            Display.sync(60);
        }
    }
    
    

    public void update() {
        if (squareSize < 0.f) {
            squareSize = 0.f;
        } else if (squareSize >= DISPLAY_HEIGHT) {
            squareSize = DISPLAY_HEIGHT;
        }
    }
}
