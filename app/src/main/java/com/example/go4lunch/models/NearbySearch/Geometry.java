
package com.example.go4lunch.models.NearbySearch;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Geometry implements Serializable
{

    @SerializedName("location")
    @Expose
    private Location location;
    private final static long serialVersionUID = -643221327948862509L;

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

}
