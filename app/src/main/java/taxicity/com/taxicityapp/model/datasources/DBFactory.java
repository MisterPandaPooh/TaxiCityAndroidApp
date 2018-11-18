package taxicity.com.taxicityapp.model.datasources;

public class DBFactory {

    private static BackEnd instance = null;

    public static BackEnd getInstance(){

        if(instance==null)
            instance = new BackEnd();

        return instance;

    }
}
