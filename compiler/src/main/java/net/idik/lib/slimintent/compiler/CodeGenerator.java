package net.idik.lib.slimintent.compiler;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
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

import static com.squareup.javapoet.MethodSpec.constructorBuilder;
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

        generateAutoIntentType();

        generateSlimIntentType();

        return true;
    }

    private void generateSlimIntentType() {
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
    }


    private void generateToGoMethod(TypeSpec.Builder typeBuilder, IntentData intentData) {
        ClassName intentClass = ClassName.get("android.content", "Intent");
        ClassName targetClass = ClassName.get(intentData.typeElement);
        ClassName autoActivityClass = ClassName.get(PACKAGE_NAME_SLIM_INTENT, "AutoActivityIntent");
        MethodSpec.Builder toMethodBuilder = methodBuilder("to" + targetClass.simpleName())
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(ClassName.get("android.content", "Context"), "context")
                .addStatement("Intent intent = new $T(context, $L.class)", intentClass, targetClass)
                .returns(autoActivityClass);

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

        toMethodBuilder.addStatement("$T autoIntent = new $T(intent)", autoActivityClass, autoActivityClass);

        toMethodBuilder.addStatement("return autoIntent");
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

    private void generateAutoIntentType() {
        TypeSpec.Builder autoIntentTypeBuilder = classBuilder("AutoActivityIntent")
                .addModifiers(Modifier.FINAL, Modifier.PUBLIC);

        ClassName autoActivityIntentClassName = ClassName.get(PACKAGE_NAME_SLIM_INTENT, "AutoActivityIntent");

        autoIntentTypeBuilder.addField(ClassName.get("android.content", "Intent"), "intent", Modifier.PRIVATE, Modifier.FINAL);

        MethodSpec.Builder constructorBuilder = constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ClassName.get("android.content", "Intent"), "intent")
                .addStatement("this.$N = $N", "intent", "intent");
        autoIntentTypeBuilder.addMethod(constructorBuilder.build());

        MethodSpec.Builder startMethodBuilder = methodBuilder("start")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ClassName.get("android.content", "Context"), "context")
                .addStatement("context.startActivity(intent)");
        autoIntentTypeBuilder.addMethod(startMethodBuilder.build());

        startMethodBuilder = methodBuilder("start")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ClassName.get("android.app", "Fragment"), "fragment")
                .addStatement("fragment.startActivity(intent)");
        autoIntentTypeBuilder.addMethod(startMethodBuilder.build());

        startMethodBuilder = methodBuilder("start")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ClassName.get("android.support.v4.app", "Fragment"), "fragment")
                .addStatement("fragment.startActivity(intent)");
        autoIntentTypeBuilder.addMethod(startMethodBuilder.build());


        MethodSpec.Builder getMethodBuilder = methodBuilder("getIntent")
                .addModifiers(Modifier.PUBLIC)
                .returns(ClassName.get("android.content", "Intent"))
                .addStatement("return intent");
        autoIntentTypeBuilder.addMethod(getMethodBuilder.build());

        MethodSpec.Builder addFlagsMethodBuilder = methodBuilder("addFlags")
                .addModifiers(Modifier.PUBLIC)
                .returns(autoActivityIntentClassName)
                .addParameter(int.class, "flags")
                .addStatement("intent.addFlags(flags)")
                .addStatement("return this");
        autoIntentTypeBuilder.addMethod(addFlagsMethodBuilder.build());

        MethodSpec.Builder setFlagsMethodBuilder = methodBuilder("setFlags")
                .addModifiers(Modifier.PUBLIC)
                .returns(autoActivityIntentClassName)
                .addParameter(int.class, "flags")
                .addStatement("intent.setFlags(flags)")
                .addStatement("return this");
        autoIntentTypeBuilder.addMethod(setFlagsMethodBuilder.build());

        MethodSpec.Builder setActionMethodBuilder = methodBuilder("setAction")
                .addModifiers(Modifier.PUBLIC)
                .returns(autoActivityIntentClassName)
                .addParameter(String.class, "action")
                .addStatement("intent.setAction(action)")
                .addStatement("return this");
        autoIntentTypeBuilder.addMethod(setActionMethodBuilder.build());

        MethodSpec.Builder addCategoryMethodBuilder = methodBuilder("addCategory")
                .addModifiers(Modifier.PUBLIC)
                .returns(autoActivityIntentClassName)
                .addParameter(String.class, "category")
                .addStatement("intent.addCategory(category)")
                .addStatement("return this");
        autoIntentTypeBuilder.addMethod(addCategoryMethodBuilder.build());

        MethodSpec.Builder setDataMethodBuilder = methodBuilder("setData")
                .addModifiers(Modifier.PUBLIC)
                .returns(autoActivityIntentClassName)
                .addParameter(ClassName.get("android.net", "Uri"), "data")
                .addStatement("intent.setData(data)")
                .addStatement("return this");
        autoIntentTypeBuilder.addMethod(setDataMethodBuilder.build());

        setDataMethodBuilder = methodBuilder("setDataAndType")
                .addModifiers(Modifier.PUBLIC)
                .returns(autoActivityIntentClassName)
                .addParameter(ClassName.get("android.net", "Uri"), "data")
                .addParameter(String.class, "type")
                .addStatement("intent.setDataAndType(data, type)")
                .addStatement("return this");
        autoIntentTypeBuilder.addMethod(setDataMethodBuilder.build());

        JavaFile file = JavaFile.builder(PACKAGE_NAME_SLIM_INTENT, autoIntentTypeBuilder.build()).build();
        try {
            file.writeTo(filer);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
