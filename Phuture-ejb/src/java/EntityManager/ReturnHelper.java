package EntityManager;

import java.io.Serializable;

public class ReturnHelper implements Serializable {
    
    private boolean result;
    private String resultDescription;

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public String getResultDescription() {
        return resultDescription;
    }

    public void setResultDescription(String resultDescription) {
        this.resultDescription = resultDescription;
    }
}
