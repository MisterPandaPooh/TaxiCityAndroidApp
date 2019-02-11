package taxicity.com.taxicityapp.model.backend;

/**
 * Action Call Back for the DataSource
 * @param <T>
 */
public interface ActionCallBack<T> {

    void onSuccess(T obj);

    void onFailure(Exception exception);

    void onProgress(String status, double percent);

}
