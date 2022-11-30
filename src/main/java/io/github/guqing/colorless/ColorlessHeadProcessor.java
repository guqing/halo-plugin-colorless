package io.github.guqing.colorless;

import org.springframework.stereotype.Component;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IModelFactory;
import org.thymeleaf.processor.element.IElementModelStructureHandler;
import reactor.core.publisher.Mono;
import run.halo.app.theme.dialect.TemplateHeadProcessor;

@Component
public class ColorlessHeadProcessor implements TemplateHeadProcessor {

    @Override
    public Mono<Void> process(ITemplateContext context,
                              IModel model,
                              IElementModelStructureHandler structureHandler) {
        String s = """
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
        IModelFactory modelFactory = context.getModelFactory();
        return Mono.just(modelFactory.createText(s))
            .doOnNext(model::add)
            .then();
    }
}
