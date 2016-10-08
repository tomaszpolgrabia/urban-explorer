package pl.tpolgrabia.urbanexplorer.events;

/**
 * Created by tpolgrabia on 08.10.16.
 */
public class RefreshSettingsEvent {
    private final Object source;

    public Object getSource() {
        return source;
    }

    public RefreshSettingsEvent(Object source) {
        this.source = source;
    }

    @Override
    public String toString() {
        return "RefreshSettingsEvent{" +
            "source=" + source +
            '}';
    }
}
