package doggytalents.api.feature;

public interface IModeFeature {

    /**
     * Gets the current mode the dog is in
     * @return The dog's mode
     */
    public EnumMode getMode();

    /**
     * Sets the dog's mode
     * @param mode The mode
     */
    public void setMode(EnumMode mode);

    /**
     * Checks if the dog's mode is the one given
     * @param mode The mode
     * @return Returns true if the dog is in the given mode
     */
    public boolean isMode(EnumMode mode);

    /**
     * Checks if the dog is in one of the given modes
     * @param modes The modes
     * @return Returns true if the dog is in one the given modes
     */
    default boolean isMode(EnumMode... modes) {
        for (EnumMode mode : modes) {
            if (this.isMode(mode)) {
                return true;
            }
        }

        return false;
    }
}
