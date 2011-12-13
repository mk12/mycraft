//  
//  GameController.java
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

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

/**
 * A tiny enum for identifying mouse buttons.
 * 
 * @author Mitchell Kember
 * @since 10/12/2011
 */
enum MouseButton {
    LEFT,
    RIGHT
}

/**
 * GameController is the controller in the Model-View-Controller (MVC) design
 * architecture for this application. GameController handles user input and mediates
 * between the GameState and GameRenderer classes. It also manages the run loop
 * of MyCraft.
 * 
 * @author Mitchell Kember
 * @since 07/12/2011
 * @see GameState
 * @see GameRenderer
 */
final class GameController {
    
    /**
     * The maximum amount of time to simulate over a single frame, in milliseconds.
     */
    private static final float MAX_DELTA_TIME = 50.f;
    
    /**
     * The renderer for this GameController's state.
     */
    private GameRenderer renderer;
    
    /**
     * The heart of the game, the GameState.
     */
    private GameState state;
    
    /**
     * Used for detecting mouse clicks.
     */
    private boolean wasMouseButtonDown = false;
    
    /**
     * Used for calculating delta time between frames.
     */
    private double previousTime = 0.0;
    
    /**
     * Creates a new GameController, which manages its own GameState and
     * GameRenderer, as well as user input (LWJGL Keyboard and Mouse).
     * 
     * @throws LWJGLException if there was an error loading any part of LWJGL
     */
    GameController() throws LWJGLException {
        renderer = new GameRenderer();
        state = new GameState(renderer);
        
        Keyboard.create();
        
        // This will make the mouse invisible. It will be "grabbed" by the
        // window, making it invisible and unable to leave the window.
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
     * @see #MAX_DELTA_TIME
     */
    private float getDeltaTime() {
        // Get hires time in milliseconds
        double newTime = (Sys.getTime() * 1000.0) / Sys.getTimerResolution();
        // Calculate the delta
        float delta = (float)(newTime - previousTime);
        // New becomes old for next call
        previousTime = newTime;
        
        // Return the delta time unless it's bigger than MAX_DELTA_TIME
        return (delta < MAX_DELTA_TIME) ? delta : MAX_DELTA_TIME;
    }
    
    /**
     * Determines whether the mouse {@code button} has been clicked or not.
     * 
     * @param button which mouse button to check
     * @return true if it is down and it wasn't last time this method was called
     */
    private boolean wasMouseClicked(MouseButton button) {
        // Check if the specified button is down
        boolean buttonDown = Mouse.isButtonDown(button.ordinal());
        // Check if it is was up before and down now
        boolean clicked = !wasMouseButtonDown && buttonDown;
        // New becomes old for next call
        wasMouseButtonDown = buttonDown;
        
        return clicked;
    }
    
    /**
     * The run loop. The application will stay inside this method until the window
     * is closed or the Escape key is pressed.
     */
    void run() {
        while (!Display.isCloseRequested() && !Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
            if (Display.isVisible()) {
                // Update the state with the required input
                state.update(new GameStateInputData(
                        Keyboard.isKeyDown(Keyboard.KEY_W),
                        Keyboard.isKeyDown(Keyboard.KEY_S),
                        Keyboard.isKeyDown(Keyboard.KEY_A),
                        Keyboard.isKeyDown(Keyboard.KEY_D),
                        Keyboard.isKeyDown(Keyboard.KEY_SPACE),
                        Mouse.getDX(), Mouse.getDY(),
                        wasMouseClicked(MouseButton.RIGHT)),
                        getDeltaTime());
                
                // Render it
                renderer.render(state);
            } else {
                // Only render if it needs rendering
                if (Display.isDirty()) {
                    renderer.render(state);
                }
                try {
                    // If the window isn't visible, sleep a bit so that we're
                    // not wasting resources by checking nonstop.
                    Thread.sleep(100);
                } catch (InterruptedException e) { }
            }
        }
    }
}
