package luonga.petsitter;

import android.support.annotation.NonNull;

/**
 * This class represents one pet.
 *
 * @author Linda Zuvich
 * @version 12/21/2018
 */
class Pet implements Comparable<Pet> {

    //fields
    private int petId;
    private String name;
    private int birthYear;
    private String type;
    private int gender;
    private String care;
    private int clientId = -1;
    private int vetId = -1;
    private String photo; //base64 encoded byte array
    static boolean listType;
    static final int INTACT_MALE = 0;
    static final int NEUTERED_MALE = 1;
    static final int INTACT_FEMALE = 2;
    static final int SPAYED_FEMALE = 3;

    /**
     * Default constructor.
     */
    public Pet() {
    }

    /**
     * Full constructor.
     *
     * @param name      the name of the pet
     * @param birthYear the year the pet was born
     * @param type      the species/breed of the pet
     * @param gender    one of the four constants representing gender
     * @param care      care instructions
     * @param clientId  the id of the pet's owner
     * @param vetId     the id of the pet's veterinarian
     */
    public Pet(String name, int birthYear, String type, int gender, String care, int clientId, int vetId) {
        this.name = name;
        this.birthYear = birthYear;
        this.type = type;
        this.gender = gender;
        this.care = care;
        this.clientId = clientId;
        this.vetId = vetId;
    }

    /**
     * Provides access to the pet's unique ID in the system. The ID is set by the data manager.
     *
     * @return the pet's ID
     */
    int getPetId() {
        return petId;
    }

    /**
     * Allows the pet's unique ID in the system to be changed. This should only be used by
     * the data manager.
     *
     * @param petId the pet's ID
     */
    void setPetId(int petId) {
        this.petId = petId;
    }

    /**
     * Provides access to the pet's name.
     *
     * @return the pet's name
     */
    public String getName() {
        return name;
    }

    /**
     * Allows the pet's name to be changed.
     *
     * @param name the pet's name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Provides access to the pet's year of birth.
     *
     * @return the pet's year of birth
     */
    int getBirthYear() {
        return birthYear;
    }

    /**
     * Allows the pet's year of birth to be changed.
     *
     * @param birthYear the pet's year of birth
     */
    void setBirthYear(int birthYear) {
        this.birthYear = birthYear;
    }

    /**
     * Provides access to the pet's gender, using the gender constant values.
     *
     * @return the pet's gender as a gender constant value
     */
    int getGender() {
        return gender;
    }

    /**
     * Allows the pet's gender to be changed. The public gender constants should be
     * used as arguments for this method.
     *
     * @param gender the pet's gender as one of the gender constants
     */
    void setGender(int gender) {
        this.gender = gender;
    }

    /**
     * Provides access to the pet's species/breed information.
     *
     * @return the pet's species/breed information
     */
    public String getType() {
        return type;
    }

    /**
     * Allows the pet's species/breed information to be changed.
     *
     * @param type the pet's species/breed information
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Provides access to the pet's care instructions.
     *
     * @return the pet's care instructions
     */
    String getCare() {
        return care;
    }

    /**
     * Allows the pet's care instructions to be changed.
     *
     * @param care the pet's care instructions
     */
    void setCare(String care) {
        this.care = care;
    }

    /**
     * Provides access to the client/owner of the pet.
     *
     * @return the id of the client/owner object
     */
    int getClientId() {
        return clientId;
    }

    /**
     * Allows the client/owner of the pet to be changed.
     *
     * @param clientId the id of the client/owner ID
     */
    void setClientId(int clientId) {
        this.clientId = clientId;
    }

    /**
     * Provides access to the pet's veterinarian.
     *
     * @return the id of the veterinarian object
     */
    int getVetId() {
        return vetId;
    }

    /**
     * Allows the pet's veterinarian to be changed.
     *
     * @param vetId the id of the veterinarian
     */
    void setVetId(int vetId) {
        this.vetId = vetId;
    }

    /**
     * Provides access to the pet photo as a base64 encoded byte array.
     *
     * @return the encoded String
     */
    String getPhoto() {
        return photo;
    }

    /**
     * Allows the pet photo to be changed.
     *
     * @param photo the pet photo as a base64 encoded byte array.
     */
    void setPhoto(String photo) {
        this.photo = photo;
    }

    /**
     * Provides a String representation of this pet object.
     *
     * @return a String representation of this pet
     */
    @NonNull
    @Override
    public String toString() {
        String petStr = name;
        if (listType) {
            petStr += " - " + type;
        }
        return petStr;

    }

    /**
     * Determines if one Pet object is equal to another, based on the pet's ID
     *
     * @param o the other pet
     * @return true if they both have the same ID, false if not
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pet pet = (Pet) o;
        return petId == pet.petId;

    }

    /**
     * Provides a unique number to identify this object. Uses the pet's ID.
     *
     * @return the pet's ID
     */
    @Override
    public int hashCode() {
        return petId;
    }

    /**
     * Compares one pet to another for sorting purposes. Uses the String representation of
     * each pet for comparison.
     *
     * @param other the other pet to compare to this one
     * @return < 0 if this pet comes before the other, > 0 if this pet comes after the other,
     * = 0 if both pets are the in the same sort location
     */
    public int compareTo(@NonNull Pet other) {
        return this.toString().compareTo(other.toString());
    }

}
