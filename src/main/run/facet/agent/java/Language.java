package main.java.agent;

public class Language {
    private String name;
    private String version;

    public Language(String name, String version) {
        this.name = name;
        this.version = version;
    }

    public Language() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
