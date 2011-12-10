//  
//  GameStateInputData.java
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

/**
 * A container for input that must be handled by GameState.
 * 
 * @author Mitchell Kember
 * @since 09/12/2011
 */
final class GameStateInputData {
    
    private static final float DEFAULT_LOOK_SENSITIVITY = 1.f / 10.f;
    private static float lookSensitivity = DEFAULT_LOOK_SENSITIVITY;
    
    final boolean forwardKey;
    final boolean backwardKey;
    final boolean leftKey;
    final boolean rightKey;
    
    final boolean jumpKey;
    
    final float lookDeltaX;
    final float lookDeltaY;
    
    GameStateInputData(boolean forwardKey, boolean backwardKey, boolean leftKey, boolean rightKey, boolean jumpKey, float lookDeltaX, float lookDeltaY) {
        this.forwardKey = forwardKey;
        this.backwardKey = backwardKey;
        this.leftKey = leftKey;
        this.rightKey = rightKey;
        this.jumpKey = jumpKey;
        this.lookDeltaX = lookDeltaX * lookSensitivity;
        this.lookDeltaY = lookDeltaY * lookSensitivity;
    }
    
    static void setLookSensitivity(float lookSensitivity) {
        GameStateInputData.lookSensitivity = lookSensitivity * DEFAULT_LOOK_SENSITIVITY;
    }
}
