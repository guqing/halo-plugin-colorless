package io.github.guqing.colorless;

import java.time.LocalDate;
import lombok.Data;

/**
 * @author guqing
 * @since 2.0.0
 */
@Data
public class BasicSetting {
    public static final String GROUP = "basic";

    private Boolean enable;

    private Boolean scope;

    private LocalDate selfCloseAt;
}
