/*******************************************************************************
 *  Copyright (c) 2015, Tim Verbelen
 *  Internet Based Communication Networks and Services research group (IBCN),
 *  Department of Information Technology (INTEC), Ghent University - iMinds.
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *     - Redistributions of source code must retain the above copyright notice,
 *       this list of conditions and the following disclaimer.
 *     - Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     - Neither the name of Ghent University - iMinds, nor the names of its 
 *       contributors may be used to endorse or promote products derived from 
 *       this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 *  AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 *  IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 *  ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 *  LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 *  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 *  SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 *  INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 *  CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 *  ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 *  POSSIBILITY OF SUCH DAMAGE.
 *******************************************************************************/
package be.iminds.iot.things.api.camera;

import be.iminds.iot.things.api.Thing;

/**
 * Represents a camera device. Using this API the camera metadata can be
 * queried, and the camera device can be turned on.
 *
 * Frames can be fetched from the camera using the getFrame() method.
 *
 * One can also use a push method to process frames of the Camera, by
 * registering a CameraListener.
 *
 * @author tverbele
 *
 */
public interface Camera extends Thing {

    public final static String STATE = "state";

    public static enum State {
    	OFF, RECORDING;
    }

    public static enum Format {
    	YUV, RGB, GRAYSCALE, MJPEG;
    }

    /**
     * Get the state of the camera
     *
     * @return state of the camera
     */
    public State getState();
    
    /**
     * Return boolean if the camera is on
     */
    public boolean isOn();

    /**
     * Get the width of the frames the camera is fetching
     *
     * @return frame width or -1 if not initialized
     */
    public int getWidth();

    /**
     * Get the height of the frames the camera is fetching
     *
     * @return frame height or -1 if not initialized
     */
    public int getHeight();
    
    /**
     * Get the current capturing format
     * 
     * @return frame format
     */
    public Format getFormat();
    
    /**
     * Return the framerate (frames per second) that is aimed for when having a CameraListener
     * 
     * @return framerate
     */
    public float getFramerate();
    
    /**
     * Set framerate
     * 
     * @param f framerate to set
     */
    public void setFramerate(float f);
    
    /**
     * Return the latest camera frame when the camera is turned on
     * @return byte array in the camera's current format
     */
    public byte[] getFrame();
    
    /**
     * Turn the camera on
     */
    public void start();

    /**
     * Turn the camera on with preferred width,height capture ratio
     *
     * @param width
     * @param height
     */
    public void start(int width, int height, Format format);

    /**
     * Stop capturing
     */
    public void stop();
    
    /**
     * Toggle
     */
    public void toggle();
}
