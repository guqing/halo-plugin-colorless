package io.github.guqing.colorless;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IModelFactory;
import org.thymeleaf.processor.element.IElementModelStructureHandler;
import reactor.core.publisher.Mono;
import run.halo.app.plugin.ReactiveSettingFetcher;
import run.halo.app.theme.dialect.TemplateHeadProcessor;

@Component
@AllArgsConstructor
public class ColorlessHeadProcessor implements TemplateHeadProcessor {

    private final ReactiveSettingFetcher settingFetcher;

    @Override
    public Mono<Void> process(ITemplateContext context,
                              IModel model,
                              IElementModelStructureHandler structureHandler) {
        return settingFetcher.fetch(BasicSetting.GROUP, BasicSetting.class)
            .defaultIfEmpty(new BasicSetting())
            .doOnNext(basicSetting -> {
                if (BooleanUtils.isNotTrue(basicSetting.getEnable())) {
                    // 如果插件未启用，直接返回
                    return;
                }

                LocalDate now = LocalDate.now(); // 获取当前时间
                LocalDate selfStartAt = basicSetting.getSelfStartAt(); // 获取自启动时间
                LocalDate selfCloseAt = basicSetting.getSelfCloseAt(); // 获取自关闭时间

                // 检查当前时间是否在自启动和自关闭时间之间
                if ((selfStartAt != null && now.isBefore(selfStartAt)) ||
                    (selfCloseAt != null && now.isAfter(selfCloseAt))) {
                    // 不在自启动时间段内，直接返回
                    return;
                }

                LocalDate selfCloseAtSetting = basicSetting.getSelfCloseAt(); // 过期检查
                if (selfCloseAtSetting != null && selfCloseAtSetting.isBefore(LocalDate.now())) {
                    // 如果已过期，直接返回
                    return;
                }

                String templateId = (String) context.getVariable("_templateId");
                boolean onlyIndex = BooleanUtils.isNotTrue(basicSetting.getScope());
                if (onlyIndex && !StringUtils.equals("index", templateId)) {
                    // 如果只在首页有效且当前模板不是首页，直接返回
                    return;
                }

                String s = htmlGrayFilter(); // 获取灰色滤镜的 CSS
                IModelFactory modelFactory = context.getModelFactory();
                model.add(modelFactory.createText(s)); // 将样式添加到模型中
            })
            .then();
    }

    private static String htmlGrayFilter() {
        return """
            <style type="text/css">
              html {
                filter: grayscale(100%);
                -webkit-filter: grayscale(100%);
                -moz-filter: grayscale(100%);
                -ms-filter: grayscale(100%);
                -o-filter: grayscale(100%);
                -webkit-filter: grayscale(1);
              }
            </style>
            """;
    }
}
