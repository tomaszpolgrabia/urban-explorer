package pl.tpolgrabia.urbanexplorer.events;

import android.location.Location;

/**
 * Created by tpolgrabia on 08.10.16.
 */
public class LocationChangedEvent {
    private Location location;
    private Object source;
    private Long time;

    public LocationChangedEvent() {
    }

    public LocationChangedEvent(Location location, Object source, Long time) {
        this.location = location;
        this.source = source;
        this.time = time;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Object getSource() {
        return source;
    }

    public void setSource(Object source) {
        this.source = source;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "LocationChangedEvent{" +
            "location=" + location +
            ", source=" + source +
            ", time=" + time +
            '}';
    }
}
