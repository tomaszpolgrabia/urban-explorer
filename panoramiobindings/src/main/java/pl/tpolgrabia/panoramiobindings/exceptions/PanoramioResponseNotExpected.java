package pl.tpolgrabia.panoramiobindings.exceptions;

/**
 * Created by tpolgrabia on 27.08.16.
 */
public class PanoramioResponseNotExpected extends RuntimeException {
    private static final long serialVersionUID = 4518500758010368539L;

    public PanoramioResponseNotExpected(String errorCause) {
        super(errorCause);
    }
}
