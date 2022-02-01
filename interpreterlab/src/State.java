import java.util.HashMap;

public class State {
    private HashMap<String, Object> states;


    public State()
    {
        states = new HashMap<>();
    }

    public void setVariableValue(String varName, Object var)
    {
       states.put(varName,var);
    }

    public Object getVariableValue(String varName) {
        return states.get(varName);
    }
}
