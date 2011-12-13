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

package com.hecticcraft.mycraft;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.lwjgl.LWJGLException;

/**
 * MyCraft is an open source java game that uses the LightWeight Java
 * Game Library (LWJGL). It is inspired by the popular game Minecraft.
 * It was written as a final summative project for the ICS2O course.
 * 
 * "Minecraft" is an official trademark of Mojang AB. This work is not
 * formally related to, endorsed by or affiliated with Minecraft or Mojang AB.
 * 
 * @author Mitchell Kember
 * @version 1.0 06/12/2011
 * @since 06/12/2011
 */
final class MyCraft {
    
    /**
     * Used to log errors to a log file.
     */
    static final Logger LOGGER = Logger.getLogger(MyCraft.class.getName());
    
    static {
        try {
            LOGGER.addHandler(new FileHandler("errors.log", true));
        } catch (IOException ioe) {
            LOGGER.log(Level.WARNING, ioe.toString(), ioe);
        }
    }
    
    /**
     * The main method.
     * 
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        GameController controller = null;
        
        // Try creating the GameController
        // Any LWJGLExceptions that occur during the initialization of LWJGL
        // (Display, Keyboard, Mouse) will propagate up here and be caught.
        try {
            System.out.println("MyCraft is starting up.");
            controller = new GameController();
            controller.run(); // begin the main loop
        } catch (LWJGLException lwjgle) {
            LOGGER.log(Level.SEVERE, lwjgle.toString(), lwjgle);
            System.out.println("NOOO");
        } finally {
            if (controller != null) {
                // Clean up
                controller.destroy();
            }
        }
    }
}
