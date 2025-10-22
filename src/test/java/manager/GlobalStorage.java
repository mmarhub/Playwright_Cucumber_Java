package manager;

public class GlobalStorage {

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
    }
}
