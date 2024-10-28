package io.github.guqing.colorless;

import java.time.LocalDate;
import lombok.Data;

/**
 * @author guqing,8butubb 
 * @since 2.0.0
 * 
 * 修改说明:
 * - 修改了原有的设置结构，增加了对自启动时间的支持。
 * - 增加 selfStartAt 字段
 */
@Data
public class BasicSetting {
    public static final String GROUP = "basic";

    private Boolean enable;

    private Boolean scope;

    private LocalDate selfStartAt; // 自启动时间
    private LocalDate selfCloseAt; // 自关闭时间
}