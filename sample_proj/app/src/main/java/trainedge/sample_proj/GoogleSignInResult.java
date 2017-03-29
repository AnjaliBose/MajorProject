package trainedge.sample_proj;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

/**
 * Created by Lenovo on 29-03-17.
 */

class GoogleSignInResult {

    public static final boolean success;
    public static final GoogleSignInAccount signInAccount;

    public boolean isSuccess() {
        return success;
    }

    public GoogleSignInAccount getSignInAccount() {
        return signInAccount;
    }
}
