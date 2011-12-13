//  
//  GameStateInputData.java
//  MyCraft
//  
//  Created on 09/12/2011.
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

/**
 * A container for input that must be handled by GameState.
 * 
 * @author Mitchell Kember
 * @since 09/12/2011
 */
final class GameStateInputData {
    
    private static final float DEFAULT_LOOK_SENSITIVITY = 1.f / 10.f;
    private static float lookSensitivity = DEFAULT_LOOK_SENSITIVITY;
    
    final boolean forward;
    final boolean backward;
    final boolean left;
    final boolean right;
    
    final boolean jump;
    
    final float lookDeltaX;
    final float lookDeltaY;
    
    final boolean placeBlock;
    
    /**
     * Creates a new GameStateInputData, initializing all fields.
     * 
     * @param forward if the Player should move forward
     * @param backward if the Player should move backward
     * @param left if the Player should move to the left
     * @param right if the Player should move to the right
     * @param jump if the Player should jump
     * @param lookDeltaX the distance along the x-axis the Player has shifted its gaze
     * @param lookDeltaY the distance along the y-axis the Player has shifted its gaze
     * @param placeBlock if the Player should place a block
     */
    GameStateInputData(boolean forward, boolean backward, boolean left, boolean right, boolean jump, float lookDeltaX, float lookDeltaY, boolean placeBlock) {
        this.forward = forward;
        this.backward = backward;
        this.left = left;
        this.right = right;
        this.jump = jump;
        this.lookDeltaX = lookDeltaX * lookSensitivity;
        this.lookDeltaY = lookDeltaY * lookSensitivity;
        this.placeBlock = placeBlock;
    }
    
    /**
     * Changes the look sensitivity. Smaller values allow looking around more
     * accurately but more slowly; for example if the mouse is used, a very small
     * value would require lifting the mouse several times to turn around. Larger
     * values allow looking around very quickly.
     * 
     * @param lookSensitivity the new look sensitivity
     */
    static void setLookSensitivity(float lookSensitivity) {
        GameStateInputData.lookSensitivity = lookSensitivity * DEFAULT_LOOK_SENSITIVITY;
    }
}
