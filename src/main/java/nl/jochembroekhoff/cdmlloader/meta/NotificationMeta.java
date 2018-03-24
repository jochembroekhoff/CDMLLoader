package nl.jochembroekhoff.cdmlloader.meta;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class NotificationMeta {
    private final String name;
    private final String title;
    private final String subTitle;
    private final String iconName;
    private final String iconSet;
}
