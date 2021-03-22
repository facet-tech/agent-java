package run.facet.agent.java;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class Toggle {
    private static Map<String,Boolean> toggle;

    private Toggle () {
        toggle = new HashMap<>();
    }


    public static boolean isEnabled(String name) {
        if(!toggle.containsKey(name)) {
            return true;
        } else {
            return toggle.get(name);
        }
    }

    public String getToggleName(String className, String signature) {
        return className + "." + signature;
    }

    public void updateToggle(String className, String signature, boolean enabled) {
        String name = getToggleName(className,signature);
        if(toggle.containsKey(name)) {
            toggle.replace(name,enabled);
        } else {
            toggle.put(name,enabled);
        }
    }

    public Map<String,Boolean> getAll() {
        return toggle;
    }
}
