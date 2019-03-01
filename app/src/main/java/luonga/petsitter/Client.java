package luonga.petsitter;

import android.support.annotation.NonNull;

/**
 * This class represents one client.
 *
 * @author Linda Zuvich
 * @version 12/21/2018
 */
class Client implements Comparable<Client> {

    //fields
    private int clientId;
    private String lastName;
    private String firstName;
    private String cell;
    private String email;

    /**
     * Default constructor.
     */
    public Client() {
    }

    /**
     * Complete constructor
     *
     * @param lastName  client's last name
     * @param firstName client's first name
     * @param cell      client's cell phone number
     * @param email     client's email address
     */
    public Client(String lastName, String firstName, String cell, String email) {
        this.cell = cell;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    /**
     * Provides access to the ID of the client.
     *
     * @return the client ID
     */
    int getClientId() {
        return clientId;
    }

    /**
     * Allows the ID of the client to be changed.
     *
     * @param clientId the client ID
     */
    void setClientId(int clientId) {
        this.clientId = clientId;
    }

    /**
     * Provides access to the cellphone number of the client.
     *
     * @return the client's cellphone number
     */
    String getCell() {
        return cell;
    }

    /**
     * Allows the client's cellphone number to be changed.
     *
     * @param cell the client's cellphone number
     */
    void setCell(String cell) {
        this.cell = cell;
    }

    /**
     * Provides access to the client's email address.
     *
     * @return the client's email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Allows the client's email address to be changed.
     *
     * @param email the client's email address
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Provides access to the client's first name.
     *
     * @return the client's first name
     */
    String getFirstName() {
        return firstName;
    }

    /**
     * Allows the client's first name to be changed.
     *
     * @param firstName the client's first name
     */
    void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Provides access to the client's last name.
     *
     * @return the client's last name
     */
    String getLastName() {
        return lastName;
    }

    /**
     * Allows the client's last name to be changed.
     *
     * @param lastName the client's last name
     */
    void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Creates a string representation of this Client object.
     *
     * @return the Client's full name
     */
    @NonNull
    @Override
    public String toString() {
        return (firstName + " " + lastName);
    }

    /**
     * Used to tell if one Client object reference is the same as another.
     *
     * @param o the object to compare to this one
     * @return true if the object has the same ID, false if not.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Client other = (Client) o;
        return (clientId == other.clientId);
    }

    /**
     * Used to find this Client object in a list.
     *
     * @return the unique hashcode for this object
     */
    @Override
    public int hashCode() {
        int result = clientId;
        result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
        result = 31 * result + (firstName != null ? firstName.hashCode() : 0);
        result = 31 * result + (cell != null ? cell.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        return result;
    }

    /**
     * Used to sort a list of Client objects.
     *
     * @param another the other Client object to compare this one
     * @return 0 if equal, -1 if the other comes before, and 1 if it comes after this one
     */
    @Override
    public int compareTo(@NonNull Client another) {
        String thisName = lastName.toLowerCase() + firstName.toLowerCase();
        String anotherName = another.lastName.toLowerCase() + another.firstName.toLowerCase();
        return thisName.compareTo(anotherName);
    }

}
