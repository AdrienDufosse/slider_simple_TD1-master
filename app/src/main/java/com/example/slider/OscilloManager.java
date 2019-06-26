package com.example.slider;

public class OscilloManager {

    private Transceiver mTransceiver;

    public OscilloManager(Transceiver mTransceiver) {
        this.mTransceiver = mTransceiver;
    }

    public void connect(Object device){
        mTransceiver.connect(device);
    }

    public void attachOscilloGraphView(){

    }

    public void attachOscilloData(){

    }

    public void setOscilloEventListener(){

    }

    public void setCalibrationSutyCircle(){

    }

    public void setVerticalScale(){

    }

    public void setHorizontalScale(){

    }

    public void setChannel(){

    }

    private void send(){

    }

}
