package nl.jochembroekhoff.cdmlloader.meta;

import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * @author Jochem Broekhoff
 */
@Data
@RequiredArgsConstructor
public class ApplicationMeta {
    private final String mainLayoutId;
    private final boolean useColorScheme;
}
