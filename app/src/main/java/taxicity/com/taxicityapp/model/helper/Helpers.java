package taxicity.com.taxicityapp.model.helper;

import android.text.TextUtils;

import java.text.SimpleDateFormat;

public class Helpers {
    public final static boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }
}
