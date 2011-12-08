//  
//  GameRenderer.java
//  GameRenderer
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


/**
 *
 * @author Mitchell Kember
 * @version 1.0 07/12/2011
 * @since 07/12/2011
 */
final class GameRenderer {
    
    static final int DISPLAY_WIDTH = 800;
    static final int DISPLAY_HEIGHT = 600;
    static final int DESIRED_SAMPLES = 8;
    
    static final String WINDOW_TITLE = "MyCraft";
    
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
    }
    
    private void prepareOpenGL() {
        glDisable(GL_CULL_FACE);
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
        gluPerspective(45, DISPLAY_WIDTH / DISPLAY_HEIGHT, 0.1f, 100.f);

        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
    }
    
    void render(GameState state) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glLoadIdentity();
        
        gluLookAt(0.f, 0.f, 20.f,
                  0.f, 0.f,  0.f,
                  0.f, 1.f,  0.f);
        
        Display.update();
        Display.sync(60);
    }  
}
