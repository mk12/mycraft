// Copyright 2012 Mitchell Kember. Subject to the MIT license.

package com.hecticcraft.mycraft;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.lwjgl.LWJGLException;

/**
 * Mycraft is an open source java game that uses the LightWeight Java
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
final class Mycraft {
    
    /**
     * Used to log errors to a log file.
     */
    static final Logger LOGGER = Logger.getLogger(Mycraft.class.getName());
    
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
            System.out.println("Mycraft is starting up.");
            controller = new GameController();
            controller.run(); // begin the main loop
        } catch (LWJGLException lwjgle) {
            LOGGER.log(Level.SEVERE, lwjgle.toString(), lwjgle);
        } finally {
            if (controller != null) {
                // Clean up
                controller.destroy();
            }
        }
    }
}
