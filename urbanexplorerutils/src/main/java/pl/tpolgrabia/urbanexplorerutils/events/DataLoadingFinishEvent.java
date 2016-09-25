package pl.tpolgrabia.urbanexplorerutils.events;

/**
 * Created by tpolgrabia on 24.09.16.
 */
public class DataLoadingFinishEvent {
    private Object source;
    private Long time;

    public DataLoadingFinishEvent() {
        this(null);
    }

    public DataLoadingFinishEvent(Object source) {
        this(source, System.currentTimeMillis());
    }

    public DataLoadingFinishEvent(Object source, Long time) {
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
        return "DataLoadingFinishEvent{" +
            "source=" + source +
            ", time=" + time +
            '}';
    }
}
