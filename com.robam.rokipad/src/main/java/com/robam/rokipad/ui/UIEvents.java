package com.robam.rokipad.ui;


import com.legent.plat.pojos.device.IDevice;

public interface UIEvents {

    class HomeDeviceSelectedEvent {

        public IDevice device;

        public HomeDeviceSelectedEvent(IDevice device) {
            this.device = device;
        }
    }

}
