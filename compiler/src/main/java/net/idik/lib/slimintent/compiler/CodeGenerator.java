package net.idik.lib.slimintent.compiler;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;


import net.idik.lib.slimintent.IIntentBinding;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Set;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.lang.model.element.Modifier;

import static com.squareup.javapoet.MethodSpec.methodBuilder;
import static com.squareup.javapoet.TypeSpec.classBuilder;

/**
 * Created by linshuaibin on 2017/7/10.
 */

class CodeGenerator {


    private static final String CLASS_NAME_SLIM_INTENT = "SlimIntent";
    private static final String PACKAGE_NAME_SLIM_INTENT = "net.idik.lib.slimintent";

    private Set<IntentData> intentDatas;
    private Filer filer;
    private Messager messager;

    public CodeGenerator(Set<IntentData> intentDatas, Filer filer, Messager messager) {
        this.intentDatas = intentDatas;
        this.filer = filer;
        this.messager = messager;
    }

    boolean generate() {

        TypeSpec.Builder builder = classBuilder(CLASS_NAME_SLIM_INTENT)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL);

        for (IntentData intentData : intentDatas) {
            generateToGoMethod(builder, intentData);
        }

        generateBindingMethod(builder);

        JavaFile file = JavaFile.builder(PACKAGE_NAME_SLIM_INTENT, builder.build()).build();
        try {
            file.writeTo(filer);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }


    private void generateToGoMethod(TypeSpec.Builder typeBuilder, IntentData intentData) {
        ClassName intentClass = ClassName.get("android.content", "Intent");
        ClassName targetClass = ClassName.get(intentData.typeElement);
        MethodSpec.Builder toMethodBuilder = methodBuilder("to" + targetClass.simpleName())
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(ClassName.get("android.content", "Context"), "context")
                .addStatement("Intent intent = new $T(context, $L.class)", intentClass, targetClass)
                .returns(intentClass);

        if (intentData.argDatas != null && intentData.argDatas.size() > 0) {

            TypeSpec.Builder binderTypeBuilder = TypeSpec.classBuilder(targetClass.simpleName() + "_SlimIntentBinder")
                    .addSuperinterface(IIntentBinding.class);

            MethodSpec.Builder bindMethodBuilder = methodBuilder("bind")
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .addAnnotation(Override.class)
                    .addParameter(Object.class, "o")
                    .addStatement("$T activity = ($T) o", targetClass, targetClass)
                    .addStatement("$T intent = activity.getIntent()", intentClass);

            for (IntentData.ArgData arg : intentData.argDatas) {
                typeBuilder.addField(FieldSpec.builder(String.class, arg.key, Modifier.PRIVATE, Modifier.FINAL, Modifier.STATIC)
                        .initializer("$S", arg.key).build());

                toMethodBuilder.addParameter(ClassName.get(arg.element.asType()), arg.name)
                        .addStatement("intent.putExtra($L, $N)", arg.key, arg.name);

                bindMethodBuilder.addStatement("activity.$N = $T.getExtra(intent, $S, $S)", arg.name, ClassName.get("net.idik.lib.slimintent.api", "IntentBindingUtils"), arg.key, arg.type);
            }

            binderTypeBuilder.addMethod(bindMethodBuilder.build());

            JavaFile file = JavaFile.builder(targetClass.packageName(), binderTypeBuilder.build()).build();
            try {
                file.writeTo(filer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        toMethodBuilder.addStatement("return intent");
        typeBuilder.addMethod(toMethodBuilder.build());

    }

    private void generateBindingMethod(TypeSpec.Builder typeBuilder) {

        MethodSpec.Builder bindingMethodBuilder = methodBuilder("bind")
                .addModifiers(Modifier.FINAL, Modifier.STATIC, Modifier.PUBLIC)
                .addParameter(ClassName.get("android.app", "Activity"), "activity")
                .addCode("try {\n" +
                        "    Class bindingClass = Class.forName(activity.getClass().getName() + \"_SlimIntentBinder\");\n" +
                        "    Object object = bindingClass.newInstance();\n" +
                        "    $T intentBinding = ($T) object;\n" +
                        "    intentBinding.bind(activity);\n" +
                        "} catch (ClassNotFoundException e) {\n" +
                        "    e.printStackTrace();\n" +
                        "} catch (InstantiationException e) {\n" +
                        "    e.printStackTrace();\n" +
                        "} catch (IllegalAccessException e) {\n" +
                        "    e.printStackTrace();\n" +
                        "}\n", IIntentBinding.class, IIntentBinding.class);


        typeBuilder.addMethod(bindingMethodBuilder.build());

    }

}
