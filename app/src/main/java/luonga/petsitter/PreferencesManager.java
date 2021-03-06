package luonga.petsitter;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Manages the user preferences for the app.
 *
 * @author Linda Zuvich
 * @version 12/21/2018
 */
public class PreferencesManager {

    //fields
    private static PreferencesManager pm;
    private boolean listBreed;
    private boolean sortAZ;
    private boolean warnBeforeDeletingPet;
    private boolean warnBeforeDeletingVet;
    private boolean warnBeforeDeletingClient;
    private String currentFragment;
    private final SharedPreferences PREFS;

    /**
     * Singleton pattern preferences manager instance.
     *
     * @return the single instance
     */
    public static PreferencesManager getInstance(Context ctx) {
        if (pm == null) {
            pm = new PreferencesManager(ctx);
        }
        return pm;
    }

    /**
     * private constructor
     */
    private PreferencesManager(Context ctx) {
        PREFS = ctx.getSharedPreferences("luonga.petsitter", Context.MODE_PRIVATE);
        listBreed = PREFS.getBoolean("listType", true);
        Pet.listType = listBreed;
        sortAZ = PREFS.getBoolean("sortAZ", true);
        warnBeforeDeletingPet = PREFS.getBoolean("warnBeforeDeletingPet", true);
        warnBeforeDeletingVet = PREFS.getBoolean("warnBeforeDeletingVet", true);
        warnBeforeDeletingClient = PREFS.getBoolean("warnBeforeDeletingClient", true);
        currentFragment = PREFS.getString(Extras.CURRENT_FRAGMENT, "pets");
    }

    /**
     * Provides access to the preference to list the breed with the pet name.
     *
     * @return true if breed should be listed, false if not
     */
    boolean isListBreed() {
        return listBreed;
    }

    /**
     * Allows the preference to list the breed with the pet name to be changed.
     *
     * @param listBreed true if the breed should be listed, false if not
     */
    void setListBreed(boolean listBreed) {
        this.listBreed = listBreed;
        PREFS.edit().putBoolean("listType", listBreed).apply();
        Pet.listType = listBreed;
    }

    /**
     * Provides access to the preference to sort pets A to Z or Z to A.
     *
     * @return true if pets should be sorted A to Z, false if Z to A
     */
    boolean isSortAZ() {
        return sortAZ;
    }

    /**
     * Allows the preference to sort pets A to Z or Z to A to be changed.
     *
     * @param sortAZ true if pets should be sorted A to Z, false if Z to A
     */
    void setSortAZ(boolean sortAZ) {
        this.sortAZ = sortAZ;
        PREFS.edit().putBoolean("sortAZ", sortAZ).apply();
    }

    /**
     * Provides access to the preference to warn before deleting a pet.
     *
     * @return true if a warning should be given, false if not
     */
    boolean isWarnBeforeDeletingPet() {
        return warnBeforeDeletingPet;
    }

    /**
     * Allows the preference to warn before deleting a pet to be changed.
     *
     * @param warnBeforeDeletingPet true if a a warning should be given, false if not
     */
    void setWarnBeforeDeletingPet(boolean warnBeforeDeletingPet) {
        this.warnBeforeDeletingPet = warnBeforeDeletingPet;
        PREFS.edit().putBoolean("warnBeforeDeletingPet", warnBeforeDeletingPet).apply();
    }

    /**
     * Provides access to the preference to warn before deleting a vet.
     *
     * @return true if a warning should be given, false if not
     */
    boolean isWarnBeforeDeletingVet() {
        return warnBeforeDeletingVet;
    }

    /**
     * Allows the preference to warn before deleting a vet to be changed.
     *
     * @param warnBeforeDeletingVet true if a a warning should be given, false if not
     */
    void setWarnBeforeDeletingVet(boolean warnBeforeDeletingVet) {
        this.warnBeforeDeletingVet = warnBeforeDeletingVet;
        PREFS.edit().putBoolean("warnBeforeDeletingVet", warnBeforeDeletingVet).apply();
    }

    /**
     * Provides access to the preference to warn before deleting a client.
     *
     * @return true if a warning should be given, false if not
     */
    boolean isWarnBeforeDeletingClient() {
        return warnBeforeDeletingClient;
    }

    /**
     * Allows the preference to warn before deleting a client to be changed.
     *
     * @param warnBeforeDeletingClient true if a a warning should be given, false if not
     */
    void setWarnBeforeDeletingClient(boolean warnBeforeDeletingClient) {
        this.warnBeforeDeletingClient = warnBeforeDeletingClient;
        PREFS.edit().putBoolean("warnBeforeDeletingClient", warnBeforeDeletingClient).apply();
    }

    /**
     * Provides access to the current fragment showing in the NavActivity. This allows the app
     * to open with the last fragment displayed. It also allows return to the same fragment when
     * navigating away from NavActivity and then back.
     *
     * @return the current fragment setting
     */
    String getCurrentFragment() {
        return currentFragment;
    }

    /**
     * Allows the current fragment for NavActivity to be set. Not handled by PreferencesActivity.
     * Instead, it is handled by the NavActivity.
     *
     * @param currentFragment the current fragment to display
     */
    void setCurrentFragment(String currentFragment) {
        this.currentFragment = currentFragment;
    }
}
