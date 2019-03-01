package luonga.petsitter;

import android.support.annotation.NonNull;

/**
 * This class represents one vet.
 *
 * @author Linda Zuvich
 * @version 12/21/2018
 */
class Vet implements Comparable<Vet> {

    //fields
    private int vetId;
    private String lastName;
    private String firstName;
    private String clinic;
    private String phone;
    private String address;

    /**
     * Default constructor.
     */
    public Vet() {
    }

    /**
     * Complete constructor.
     *
     * @param lastName vet's last name
     * @param firstName vet's first name
     * @param clinic vet clinic name
     * @param phone vet clinic phone number
     * @param address vet clinic address
     */
    public Vet(String lastName, String firstName, String clinic, String phone, String address) {
        this.address = address;
        this.clinic = clinic;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
    }

    /**
     * Provides access to the vet's ID.
     *
     * @return the vet's ID
     */
    int getVetId() {
        return vetId;
    }

    /**
     * Allows the vet's ID to be changed.
     *
     * @param vetId the vet's ID
     */
    void setVetId(int vetId) {
        this.vetId = vetId;
    }

    /**
     * Provides access to the vet's address.
     *
     * @return the vet's address
     */
    String getAddress() {
        return address;
    }

    void setAddress(String address) {
        this.address = address;
    }

    /**
     * Provides access to the vet's clinic.
     *
     * @return the vet's clinic
     */
    String getClinic() {
        return clinic;
    }

    void setClinic(String clinic) {
        this.clinic = clinic;
    }

    /**
     * Provides access to the vet's first name.
     *
     * @return the vet's first name
     */
    String getFirstName() {
        return firstName;
    }

    void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Provides access to the vet's last name.
     *
     * @return the vet's last name
     */
    String getLastName() {
        return lastName;
    }

    void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Provides access to the vet's phone number.
     *
     * @return the vet's phone number
     */
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * Provides a string representation of this Vet object.
     *
     * @return the vet's name or the clinic name, depending on which is available
     */
    @NonNull
    @Override
    public String toString() {
        String vet;
        if (lastName != null && !lastName.isEmpty()) {
            vet = "Dr. " + firstName + " " + lastName;
        } else {
            vet = clinic;
        }
        return vet;
    }

    /**
     * Compares another object to this one to determine if they are the same.
     *
     * @param o the other object
     * @return true if the IDs are the same, false if not
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vet other = (Vet) o;
        return (vetId == other.vetId);
   }

    /**
     * Used to determine if a list of Vets contains this vet.
     *
     * @return the unique hashcode for this object
     */
    @Override
    public int hashCode() {
        int result = vetId;
        result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
        result = 31 * result + (firstName != null ? firstName.hashCode() : 0);
        result = 31 * result + (clinic != null ? clinic.hashCode() : 0);
        result = 31 * result + (phone != null ? phone.hashCode() : 0);
        result = 31 * result + (address != null ? address.hashCode() : 0);
        return result;
    }

    /**
     * Used to sort a list of Vets.
     *
     * @param another Vet to compare to this one
     * @return 0 if equal, -1 if the other comes before, and 1 if it comes after this one
     */
    @Override
    public int compareTo(@NonNull Vet another) {
        String thisName = lastName.toLowerCase() + firstName.toLowerCase();
        String anotherName = another.lastName.toLowerCase() + another.firstName.toLowerCase();
        return thisName.compareTo(anotherName);
    }

}
