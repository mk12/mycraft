//  
//  Chunk.java
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

import java.util.Random;

/**
 *
 * @author Mitchell Kember
 * @since 09/12/2011
 */
final class Chunk {
    
    // only recalculate connectivity, create new vbo when block changes!
    private Vector position;
    private boolean isAllAir;
    private byte[][][] data = new byte[8][8][8];
    
    {
        Random random = new Random();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                random.nextBytes(data[i][j]);
            }
        }
        data[0][0][0] = 0;
    }
    
    byte[][][] getData() {
        return data;
    }
}
