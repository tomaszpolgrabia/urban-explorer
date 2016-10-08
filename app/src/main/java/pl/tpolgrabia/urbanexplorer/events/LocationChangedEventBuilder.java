package pl.tpolgrabia.urbanexplorer.events;

import android.location.Location;

public class LocationChangedEventBuilder {
    private Location location;
    private Object source;
    private Long time;

    public LocationChangedEventBuilder setLocation(Location location) {
        this.location = location;
        return this;
    }

    public LocationChangedEventBuilder setSource(Object source) {
        this.source = source;
        return this;
    }

    public LocationChangedEventBuilder setTime(Long time) {
        this.time = time;
        return this;
    }

    public LocationChangedEvent build() {
        return new LocationChangedEvent(location, source, time);
    }
}