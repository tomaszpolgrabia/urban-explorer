package pl.tpolgrabia.urbanexplorer.dto;

/**
 * Created by tpolgrabia on 19.09.16.
 */
public enum MainActivityState {
    WIKI(1),
    GOOGLE_PLACES(2);

    private final Integer order;

    MainActivityState(Integer order) {
        this.order = order;
    }

    public Integer getOrder() {
        return order;
    }

    public MainActivityState prev() {
        int val = Integer.MIN_VALUE;
        MainActivityState greatestSmaller = null;

        for (MainActivityState state : values()) {
            if (state.getOrder() >= order || state.getOrder() < 0) {
                continue;
            }

            // we need the greatest smaller

            if (state.getOrder() > val) {
                val = state.getOrder();
                greatestSmaller = state;
            }
        }
        return greatestSmaller;
    }

    public MainActivityState next() {
        int val = Integer.MAX_VALUE;
        MainActivityState smallestGreater = null;

        for (MainActivityState state : values()) {
            if (state.getOrder() <= order || state.getOrder() < 0) {
                continue;
            }

            // we need the smallest greater

            if (state.getOrder() < val) {
                val = state.getOrder();
                smallestGreater = state;
            }
        }
        return smallestGreater;
    }
}
