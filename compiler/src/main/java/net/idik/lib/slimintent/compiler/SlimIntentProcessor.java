package net.idik.lib.slimintent.compiler;

import com.google.auto.service.AutoService;

import net.idik.lib.slimintent.annotations.AutoIntent;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

/**
 * Created by linshuaibin on 2017/7/10.
 */
@AutoService(Processor.class)
public class SlimIntentProcessor extends AbstractProcessor {

    private Types typeUtils;
    private Messager messager;
    private Filer filer;
    private Elements elementUtils;

    private Set<IntentData> intentDatas = new HashSet<>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        typeUtils = processingEnvironment.getTypeUtils();
        messager = processingEnvironment.getMessager();
        filer = processingEnvironment.getFiler();
        elementUtils = processingEnvironment.getElementUtils();
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(AutoIntent.class.getCanonicalName());
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {

        for (Element annotatedElement : roundEnvironment.getElementsAnnotatedWith(AutoIntent.class)) {

            if (!Validator.valid(annotatedElement, messager)) {
                return true;
            }

            TypeElement element = (TypeElement) annotatedElement;

            intentDatas.add(IntentData.parse(element, elementUtils, messager));
        }

        if (intentDatas.size() > 0) {
            new CodeGenerator(intentDatas, filer, messager).generate();
        }

        intentDatas.clear();

        return true;
    }
}
