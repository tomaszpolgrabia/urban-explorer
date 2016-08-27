package pl.tpolgrabia.urbanexplorer.exceptions;

/**
 * Created by tpolgrabia on 27.08.16.
 */
public class PanoramioResponseNotExpected extends RuntimeException {
    public PanoramioResponseNotExpected(String errorCause) {
        super(errorCause);
    }
}
