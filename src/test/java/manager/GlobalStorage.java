package manager;

public class GlobalStorage {

    private String randomFirstName;
    private String randomLastName;
    private String emailAddress;

    // Example of another variable (if needed in the future).
    private Integer someNumber;

    // Default constructor (required by PicoContainer)
    public GlobalStorage() {
    }

    // Getters and setters for the variables
    public String getRandomFirstName() {
        return randomFirstName;
    }

    public void setRandomFirstName(String randomFirstName) {
        this.randomFirstName = randomFirstName;
    }

    public String getRandomLastName() {
        return randomLastName;
    }

    public void setRandomLastName(String randomLastName) {
        this.randomLastName = randomLastName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public Integer getSomeNumber() {
        return someNumber;
    }

    public void setSomeNumber(Integer someNumber) {
        this.someNumber = someNumber;
    }

    /*
    // ThreadLocal variables to store data per thread.
    private final ThreadLocal<String> randomFirstName = ThreadLocal.withInitial(() -> null);
    private final ThreadLocal<String> randomLastName = ThreadLocal.withInitial(() -> null);
    private final ThreadLocal<String> emailAddress = ThreadLocal.withInitial(() -> null);

    // Example of another ThreadLocal variable (if needed in the future).
    private final ThreadLocal<Integer> someNumber = ThreadLocal.withInitial(() -> 0);

    public String getRandomFirstName() {
        return randomFirstName.get();
    }

    public void setRandomFirstName(String firstName) {
        this.randomFirstName.set(firstName);
    }

    public String getRandomLastName() {
        return randomLastName.get();
    }

    public void setRandomLastName(String lastName) {
        this.randomLastName.set(lastName);
    }

    public String getEmailAddress() {
        return emailAddress.get();
    }

    public void setEmailAddress(String email) {
        this.emailAddress.set(email);
    }*/
}
