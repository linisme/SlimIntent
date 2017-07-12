package net.idik.lib.slimintent.compiler;

import net.idik.lib.slimintent.annotations.IntentArg;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;

/**
 * Created by linshuaibin on 2017/7/10.
 */

public class IntentData {

    public static IntentData parse(TypeElement element, Elements elementUtils, Messager messager) {
        String className = element.getQualifiedName().toString();
        String packageName = elementUtils.getPackageOf(element).getQualifiedName().toString();
        List<ArgData> argDatas = new ArrayList<>();
        for (Element e : element.getEnclosedElements()) {
            if (e.getKind() == ElementKind.FIELD && e.getAnnotation(IntentArg.class) != null) {
                argDatas.add(new ArgData(className, (VariableElement) e));
            }

        }
        return new IntentData(element, argDatas);
    }

    public final TypeElement typeElement;
    public final List<ArgData> argDatas;

    public IntentData(TypeElement typeElement, List<ArgData> argDatas) {
        this.typeElement = typeElement;
        this.argDatas = argDatas;
    }

    public static class ArgData {

        public final String name;
        public final String key;
        public final TypeMirror type;
        public final VariableElement element;

        public ArgData(String className, VariableElement element) {
            name = element.getSimpleName().toString();
            key = (className + ".ARG." + name).replace('.', '_').toUpperCase();
            this.element = element;
            this.type = element.asType();
        }
    }

}
