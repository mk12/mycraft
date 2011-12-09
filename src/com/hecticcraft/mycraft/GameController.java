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
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

/**
 *
 * @author Mitchell Kember
 * @version 1.0 07/12/2011
 * @since 07/12/2011
 */
final class GameController {
    
    GameState state;
    GameRenderer renderer;
    
    GameController() throws LWJGLException {
        state = new GameState();
        renderer = new GameRenderer();
        
        Keyboard.create();
        
        Mouse.setGrabbed(true);
        Mouse.create();
    }
    
    void destroy() {
        // Methods already check if created before destroying.
        Mouse.destroy();
        Keyboard.destroy();
        Display.destroy();
    }
    
    void run() {
        while (!Display.isCloseRequested() && !Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
            if (Display.isVisible()) {
                processKeyboard();
                processMouse();
                
                state.update();
                renderer.processInput(Keyboard.isKeyDown(Keyboard.KEY_UP), Keyboard.isKeyDown(Keyboard.KEY_DOWN), Keyboard.isKeyDown(Keyboard.KEY_LEFT), Keyboard.isKeyDown(Keyboard.KEY_RIGHT), Mouse.getDX(), Mouse.getDY());
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
        
    }

    private void processMouse() {
        
    }
}
