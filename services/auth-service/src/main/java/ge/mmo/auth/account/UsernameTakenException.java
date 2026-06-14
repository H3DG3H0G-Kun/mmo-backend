package ge.mmo.auth.account;

public class UsernameTakenException extends RuntimeException {
    public UsernameTakenException(String username) {
        super("Username already taken: " + username);
    }
}
