package pl.tpolgrabia.urbanexplorer.events;

/**
 * Created by tpolgrabia on 08.10.16.
 */
public class ProviderStatusChangedEvent {
    private boolean enabled;
    private Object source;
    private Long time;
    private String provider;

    public ProviderStatusChangedEvent() {
    }

    public ProviderStatusChangedEvent(boolean enabled, Object source, Long time, String provider) {
        this.enabled = enabled;
        this.source = source;
        this.time = time;
        this.provider = provider;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
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

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }
}
