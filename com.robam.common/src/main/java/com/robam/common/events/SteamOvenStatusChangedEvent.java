package com.robam.common.events;

import com.legent.events.AbsEvent;
import com.robam.common.pojos.device.Steamoven.AbsSteamoven;

/**
 * Created by Rosicky on 15/12/14.
 */
public class SteamOvenStatusChangedEvent extends AbsEvent<AbsSteamoven> {

    public SteamOvenStatusChangedEvent(AbsSteamoven absSteamoven) {
        super(absSteamoven);
    }
}
