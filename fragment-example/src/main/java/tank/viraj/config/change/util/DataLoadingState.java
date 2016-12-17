package tank.viraj.config.change.util;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Viraj Tank, 01-12-2016.
 */
@Setter
@Getter
public class DataLoadingState {
    private boolean loadingData;

    public DataLoadingState() {
        this.loadingData = false;
    }
}