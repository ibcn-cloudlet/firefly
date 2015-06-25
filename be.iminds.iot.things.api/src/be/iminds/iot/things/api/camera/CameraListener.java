package be.iminds.iot.things.api.camera;

import be.iminds.iot.things.api.camera.Camera.Format;

public interface CameraListener {

    public final static String CAMERA_ID = "be.iminds.iot.thing.camera.id";
    public final static String CAMERA_FORMAT = "be.iminds.iot.thing.camera.format";

    public void nextFrame(Format format, byte[] data);

}
