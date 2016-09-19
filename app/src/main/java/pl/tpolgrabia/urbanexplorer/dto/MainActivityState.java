package pl.tpolgrabia.urbanexplorer.dto;

/**
 * Created by tpolgrabia on 19.09.16.
 */
public enum MainActivityState {
    PANORAMIO(0),
    WIKI(1),
    PANORAMIO_SHOWER(-1);

    private final Integer order;

    MainActivityState(Integer order) {
        this.order = order;
    }

    public Integer getOrder() {
        return order;
    }
}
