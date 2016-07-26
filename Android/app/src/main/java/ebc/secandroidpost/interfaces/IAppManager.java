package ebc.secandroidpost.interfaces;

/**
 * Created by echeb on 25-Jul-16.
 */
public interface IAppManager {
    boolean isNetworkConnected();

    boolean isUserAuthenticated();

    String sendMessage(String message);

    String authenticateUser(String usernameText, String passwordText);

}