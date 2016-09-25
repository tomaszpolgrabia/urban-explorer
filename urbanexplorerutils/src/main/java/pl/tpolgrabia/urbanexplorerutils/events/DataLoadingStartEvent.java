package pl.tpolgrabia.urbanexplorerutils.events;

/**
 * Created by tpolgrabia on 24.09.16.
 */
public class DataLoadingStartEvent {
    private Object source;
    private Long time;

    public DataLoadingStartEvent() {
        this(null);
    }

    public DataLoadingStartEvent(Object source) {
        this(source, System.currentTimeMillis());
    }

    public DataLoadingStartEvent(Object source, Long time) {
        this.source = source;
        this.time = time;
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
        return "DataLoadingStartEvent{" +
            "source=" + source +
            ", time=" + time +
            '}';
    }
}
