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
import run.halo.app.plugin.SettingFetcher;
import run.halo.app.theme.dialect.TemplateHeadProcessor;
import run.halo.app.theme.router.strategy.ModelConst;

@Component
@AllArgsConstructor
public class ColorlessHeadProcessor implements TemplateHeadProcessor {

    private final SettingFetcher settingFetcher;

    @Override
    public Mono<Void> process(ITemplateContext context,
                              IModel model,
                              IElementModelStructureHandler structureHandler) {
        BasicSetting basicSetting = settingFetcher.fetch(BasicSetting.GROUP, BasicSetting.class)
            .orElse(new BasicSetting());

        LocalDate selfCloseAt = basicSetting.getSelfCloseAt();
        if (selfCloseAt != null && selfCloseAt.isBefore(LocalDate.now())) {
            // close already
            return Mono.empty();
        }

        String templateId = (String) context.getVariable(ModelConst.TEMPLATE_ID);
        boolean onlyIndex = BooleanUtils.isNotTrue(basicSetting.getScope());
        if (onlyIndex && !StringUtils.equals("index", templateId)) {
            return Mono.empty();
        }
        String s = htmlGrayFilter();
        IModelFactory modelFactory = context.getModelFactory();
        return Mono.just(modelFactory.createText(s))
            .doOnNext(model::add)
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
