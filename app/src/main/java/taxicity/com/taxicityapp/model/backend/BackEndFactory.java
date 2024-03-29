package taxicity.com.taxicityapp.model.backend;

import taxicity.com.taxicityapp.model.datasources.FireBase_Manager;

/**
 * Factory class of the BackEnd
 */
public class BackEndFactory {

    private static BackEnd instance = null;

    public static BackEnd getInstance() {

        if (instance == null) {
            instance = new FireBase_Manager();
        }

        return instance;

    }
}
