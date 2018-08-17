package com.oneflaretech.kiakia.googleAddress;

public class Geometry
{
    private Location location;

    public Location getLocation() { return this.location; }

    public void setLocation(Location location) { this.location = location; }

    private String location_type;

    public String getLocationType() { return this.location_type; }

    public void setLocationType(String location_type) { this.location_type = location_type; }

    private Viewport viewport;

    public Viewport getViewport() { return this.viewport; }

    public void setViewport(Viewport viewport) { this.viewport = viewport; }
}
