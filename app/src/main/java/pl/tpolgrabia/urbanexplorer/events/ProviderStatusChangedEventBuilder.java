package pl.tpolgrabia.urbanexplorer.events;

public class ProviderStatusChangedEventBuilder {
    private boolean enabled;
    private Object source;
    private Long time;
    private String provider;

    public ProviderStatusChangedEventBuilder setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public ProviderStatusChangedEventBuilder setSource(Object source) {
        this.source = source;
        return this;
    }

    public ProviderStatusChangedEventBuilder setTime(Long time) {
        this.time = time;
        return this;
    }

    public ProviderStatusChangedEventBuilder setProvider(String provider) {
        this.provider = provider;
        return this;
    }

    public ProviderStatusChangedEvent build() {
        return new ProviderStatusChangedEvent(enabled, source, time, provider);
    }
}