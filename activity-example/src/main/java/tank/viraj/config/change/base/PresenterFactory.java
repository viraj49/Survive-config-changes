package tank.viraj.config.change.base;

/**
 * Created by Viraj Tank, 01-12-2016.
 */
public interface PresenterFactory<T extends Presenter> {
    T create();
}
