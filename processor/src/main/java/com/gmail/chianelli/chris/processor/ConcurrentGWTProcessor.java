package com.gmail.chianelli.chris.processor;

import com.gmail.chianelli.chris.annotation.ConcurrentClass;
import com.gmail.chianelli.chris.annotation.ConcurrentMethod;
import com.google.auto.service.AutoService;
import com.sun.source.util.Trees;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

@AutoService(Processor.class) public class ConcurrentGWTProcessor extends AbstractProcessor {

  private Types typeUtils;
  private Elements elementUtils;
  private Filer filer;
  private Messager messager;
  private Map<String, ClassGroupedMethods> classMethods =
      new LinkedHashMap<String, ClassGroupedMethods>();
  
  private Trees trees;

  @Override public synchronized void init(ProcessingEnvironment processingEnv) {
    super.init(processingEnv);
    trees = Trees.instance(processingEnv);
    typeUtils = processingEnv.getTypeUtils();
    elementUtils = processingEnv.getElementUtils();
    filer = processingEnv.getFiler();
    messager = processingEnv.getMessager();
  }

  @Override public Set<String> getSupportedAnnotationTypes() {
    Set<String> annotations = new LinkedHashSet<String>();
    annotations.addAll(Arrays.asList(ConcurrentClass.class.getCanonicalName(), ConcurrentMethod.class.getCanonicalName()));
    return annotations;
  }

  @Override public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latestSupported();
  }

  /**
   * Checks if the annotated element observes our rules
   */
  private void checkValidClass(ConcurrentAnnotatedClass item) throws ProcessingException {
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

    try {

      // Scan classes
    	for (Element annotatedElement : roundEnv.getElementsAnnotatedWith(ConcurrentClass.class)) {

            // Check if a class has been annotated with @Factory
            if (annotatedElement.getKind() != ElementKind.CLASS) {
              throw new ProcessingException(annotatedElement, "Only classes can be annotated with @%s",
            		  ConcurrentClass.class.getSimpleName());
            }

            // We can cast it, because we know that it of ElementKind.CLASS
            TypeElement typeElement = (TypeElement) annotatedElement;

            ConcurrentAnnotatedClass annotatedClass = new ConcurrentAnnotatedClass(typeElement);

            checkValidClass(annotatedClass);

            // Everything is fine, so try to add
            ClassGroupedMethods classGroup = new ClassGroupedMethods(new ConcurrentAnnotatedClass(typeElement));
            classMethods.put(typeElement.getQualifiedName().toString(), classGroup);
          }
    	
      for (Element annotatedElement : roundEnv.getElementsAnnotatedWith(ConcurrentMethod.class)) {

        if (annotatedElement.getKind() != ElementKind.METHOD) {
          throw new ProcessingException(annotatedElement, "Only methods can be annotated with @%s",
        		  ConcurrentMethod.class.getSimpleName());
        }

        // We can cast it, because we know that it of ElementKind.CLASS
        ExecutableElement executableElement = (ExecutableElement) annotatedElement;

        ConcurrentAnnotatedMethod annotatedMethod = new ConcurrentAnnotatedMethod(executableElement);

        // Everything is fine, so try to add
        ClassGroupedMethods methodClass =
            classMethods.get(annotatedMethod.getClassName());
        
        methodClass.add(annotatedMethod);
      }

      // Generate code
      for (ClassGroupedMethods classMethods : classMethods.values()) {
    	  classMethods.generateCode(trees, elementUtils, filer);
      }
      classMethods.clear();
    } catch (ProcessingException e) {
      error(e.getElement(), e.getMessage());
    } catch (IOException e) {
      error(null, e.getMessage());
    }

    return true;
  }

  /**
   * Prints an error message
   *
   * @param e The element which has caused the error. Can be null
   * @param msg The error message
   */
  public void error(Element e, String msg) {
    messager.printMessage(Diagnostic.Kind.ERROR, msg, e);
  }
}
