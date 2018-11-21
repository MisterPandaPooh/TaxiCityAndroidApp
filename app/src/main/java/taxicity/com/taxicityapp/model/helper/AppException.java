package taxicity.com.taxicityapp.model.helper;


import com.google.firebase.database.ServerValue;

import static taxicity.com.taxicityapp.model.helper.AppException.ErrorTypeCode.DANGER;
import static taxicity.com.taxicityapp.model.helper.AppException.ErrorTypeCode.UNDEFINED;

public class AppException extends RuntimeException {

    public enum ErrorTypeCode {SUCCESS, WARNING, ERROR, DANGER, INFO, UNDEFINED}

    private ErrorTypeCode code;

    private String publicMessage;

    AppException(RuntimeException r) {
        super(r);
        code = UNDEFINED;
        publicMessage = r.getMessage();

    }

    AppException(Exception e) {
        super(e);
        code = DANGER;
        publicMessage = e.getMessage();
    }

    AppException(String aPublicMessage, ErrorTypeCode aCode) {
        code = aCode;
        publicMessage = aPublicMessage;
    }

    AppException(ErrorTypeCode aCode){
        code = aCode;
        publicMessage = "Undefined";
        }


    public ErrorTypeCode getCode() {
        return code;
    }


    public String getPublicMessage() {
        return publicMessage;
    }


    @Override
    public String toString() {
        return getCode() + " : " + getPublicMessage();
    }


}
