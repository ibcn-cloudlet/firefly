package be.iminds.iot.things.api.camera;

import be.iminds.iot.things.api.Thing;

/**
 * Represents a camera device. Using this API the camera metadata can be
 * queried, and the camera device can be turned on.
 *
 * Frames can be fetched from the camera using the getFrame() method
 *
 * One can also use a push method to process frames of the Camera, by
 * registering a CameraListener
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
     * Get the supported frame formats for this camera
     *
     * @return supported image formats
     */
    public Format[] getSupportedFormats();

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
