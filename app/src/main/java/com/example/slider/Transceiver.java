package com.example.slider;

public abstract class Transceiver {

    public abstract void attachFrameProcessor();

    public abstract void detachFrameProcessor();

    public abstract void connect(Object device);

    public abstract void getState();

    public abstract void setState();

    public abstract void stop();

    public abstract void send();

}
