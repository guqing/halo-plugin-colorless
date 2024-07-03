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
                    // disabled
                    return;
                }

                LocalDate selfCloseAt = basicSetting.getSelfCloseAt();
                if (selfCloseAt != null && selfCloseAt.isBefore(LocalDate.now())) {
                    // expired already
                    return;
                }

                String templateId = (String) context.getVariable("_templateId");
                boolean onlyIndex = BooleanUtils.isNotTrue(basicSetting.getScope());
                if (onlyIndex && !StringUtils.equals("index", templateId)) {
                    return;
                }
                String s = htmlGrayFilter();
                IModelFactory modelFactory = context.getModelFactory();
                model.add(modelFactory.createText(s));
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
