package EntityManager;

import java.io.Serializable;

public class ReturnHelper implements Serializable {

    private boolean result;
    private String resultDescription;
    private Long ID;

    public boolean getResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public String getDescription() {
        return resultDescription;
    }

    public void setDescription(String resultDescription) {
        this.resultDescription = resultDescription;
    }

    public Long getID() {
        return ID;
    }

    public void setID(Long ID) {
        this.ID = ID;
    }

}
