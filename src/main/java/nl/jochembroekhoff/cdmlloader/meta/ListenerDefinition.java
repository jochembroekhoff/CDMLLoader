package nl.jochembroekhoff.cdmlloader.meta;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class ListenerDefinition {
    @Getter
    final String eventName;
    @Getter
    final String methodName;
    @Getter
    final Class<?>[] parameters;
}
