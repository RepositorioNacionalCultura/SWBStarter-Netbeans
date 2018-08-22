package mx.gob.cultura.commons.model;

/**
 * POJO Class that represents the identifier of a {@link CulturalGood}.
 * @author Hasdai Pacheco
 */
public class Identifier {
    private String type;
    private String value;
    boolean preferred;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isPreferred() {
        return preferred;
    }

    public void setPreferred(boolean preferred) {
        this.preferred = preferred;
    }
}
