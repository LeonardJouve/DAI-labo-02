package ch.heigvd.dai.server;

import java.nio.file.Path;

public class State {
    private boolean isLoggedIn = false;
    private Path vaultPath = null;
    private String userName = null;
    private String password = null;

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        isLoggedIn = loggedIn;
    }

    public Path getVaultPath() {
        return vaultPath;
    }

    public void setVaultPath(Path vaultPath) {
        this.vaultPath = vaultPath;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
