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

/* NOTES
 * - postive Z axis: outside the screen
 * - negative Z axis: into the screen
 * - zNear/zFar: distance from camera (which moves around)
 */

package com.hecticcraft.mycraft;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * MyCraft is an open source java game that uses the LightWeight Java
 * Game Library (LWJGL), based on the popular game Minecraft.
 * 
 * "Minecraft" is an official trademark of Mojang AB. This work is not
 * formally related to, endorsed by or affiliated with Minecraft or Mojang AB.
 * 
 * @author Mitchell Kember
 * @version 1.0 06/12/2011
 * @since 06/12/2011
 */
final class MyCraft {
    
    private static final Logger LOGGER = Logger.getLogger(MyCraft.class.getName());

    static {
        try {
            LOGGER.addHandler(new FileHandler("errors.log", true));
        } catch (IOException ioe) {
            LOGGER.log(Level.WARNING, ioe.toString(), ioe);
        }
    }

    public static void main(String[] args) {
        GameController controller = null;
        try {
            System.out.println("YourCraft is starting up.");
            controller = new GameController();
            controller.run();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.toString(), e);
        } finally {
            if (controller != null) {
                controller.destroy();
            }
        }
    }
}
