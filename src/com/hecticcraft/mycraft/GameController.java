//  
//  GameController.java
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
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

/**
 * GameController is the main controller in the Model-View-Controller (MVC)
 * design architecture for this application. It receives user input and mediates
 * between the GameState and GameRenderer.
 * 
 * @author Mitchell Kember
 * @since 07/12/2011
 * @see GameState
 * @see GameRenderer
 */
final class GameController {
    
    private static final float MAX_DELTA_TIME = 50;

    private GameState state;
    private GameRenderer renderer;
    
    /**
     * Used for calculating delta time between frames.
     */
    private double prevTime;
    
    /**
     * Creates a new GameController, which manages its own GameState and
     * GameRenderer.
     * 
     * @throws LWJGLException if there was an error loading any part of LWJGL
     */
    GameController() throws LWJGLException {
        state = new GameState();
        renderer = new GameRenderer();

        Keyboard.create();
        
        // This will make the mouse invisible, it will be "grabbed" by the window
        // so it cannot be seen and cannot leave the window.
        Mouse.setGrabbed(true);
        Mouse.create();
    }
    
    /**
     * Clean up LWJGL components.
     */
    void destroy() {
        // Methods already check if created before destroying.
        Mouse.destroy();
        Keyboard.destroy();
        Display.destroy();
    }
    
    /**
     * Gets the time in milliseconds since this method was last called. If it
     * is greater than MAX_DELTA_TIME, that will be returned instead.
     * 
     * @return the time since this method was last called in milliseconds
     */
    float getDeltaTime() {
        // Get hires time in milliseconds
        double newTime = (Sys.getTime() * 1000.0) / Sys.getTimerResolution();
        // Calculate the delta, and make sure it's not over the max
        float delta = (float)Math.min(newTime - prevTime, MAX_DELTA_TIME);
        // New becomes old for next call
        prevTime = newTime;
        
        return delta;
    }
    
    /**
     * The run loop. The application will stay inside this method until it exits.
     */
    void run() {
        while (!Display.isCloseRequested() && !Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
            if (Display.isVisible()) {
                
                //processKeyboard();
                //processMouse();
                
                state.processInput(new GameStateInputData(
                        Keyboard.isKeyDown(Keyboard.KEY_W),
                        Keyboard.isKeyDown(Keyboard.KEY_S),
                        Keyboard.isKeyDown(Keyboard.KEY_A),
                        Keyboard.isKeyDown(Keyboard.KEY_D),
                        Keyboard.isKeyDown(Keyboard.KEY_SPACE),
                        Mouse.getDX(), Mouse.getDY()));
                state.update(getDeltaTime());
                
                renderer.render(state);
            } else {
                if (Display.isDirty()) {
                    renderer.render(state);
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) { }
            }
        }
    }

    private void processKeyboard() {
        // UI/HUD stuff
    }

    private void processMouse() {
        // UI/HUD stuff
    }
}
