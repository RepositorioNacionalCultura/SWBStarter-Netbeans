package mx.gob.cultura.commons.model;

import java.util.Date;

/**
 * POJO Class that represents a period associated to a BIC.
 * @author Hasdai Pacheco
 */
public class Period {
    private String name;
    Date startDate;
    Date endDate;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
