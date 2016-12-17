package tank.viraj.config.change.base;

/**
 * Created by Viraj Tank, 01-12-2016.
 */
public interface Presenter <V>{
    void onViewAttached(V view);
    void onViewDetached();
    void onDestroyed();
}
