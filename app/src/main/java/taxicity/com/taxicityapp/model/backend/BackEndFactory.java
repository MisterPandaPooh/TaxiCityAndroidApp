package taxicity.com.taxicityapp.model.backend;

import taxicity.com.taxicityapp.model.backend.BackEnd;
import taxicity.com.taxicityapp.model.datasources.FireBase_Manger;

public class BackEndFactory {

    private static BackEnd instance = null;

    public static BackEnd getInstance(){

        if(instance==null) {
            instance = new FireBase_Manger();
        }

        return instance;

    }
}
