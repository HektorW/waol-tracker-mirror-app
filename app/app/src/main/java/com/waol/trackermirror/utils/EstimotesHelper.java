package com.waol.trackermirror.utils;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.Region;

import java.util.UUID;

public class EstimotesHelper {

    public static final String BeaconUUID = "b9407f30-f5f8-466e-aff9-25556b57fe6d";

    public static Region DefaultRegion(){
        return new Region("TrackerMirror", UUID.fromString(BeaconUUID), null, null);
    }
}
