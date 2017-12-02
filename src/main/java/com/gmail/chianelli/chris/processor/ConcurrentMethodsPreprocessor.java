package com.gmail.chianelli.chris.processor;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
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
import javax.tools.JavaFileObject;

import com.sun.source.tree.*;
import com.sun.source.util.Trees;

import com.gmail.chianelli.chris.annotations.ConcurrentClass;
import com.gmail.chianelli.chris.annotations.ConcurrentMethod;

public class ConcurrentMethodsPreprocessor extends AbstractProcessor {
	 private Types typeUtils;
	 private Elements elementUtils;
	 private Filer filer;
	 private Messager messager;
	 private Set<String> concurrentClasses = new LinkedHashSet<>();
	 private Map<String,Set<String>> concurrentMethods = new LinkedHashMap<>();
	 private Map<String,JavaFileObject> classFiles = new LinkedHashMap<>();
	
	private Trees trees;
	private Class clazz = ConcurrentClass.class;
	
	@Override
	public synchronized void init(ProcessingEnvironment env){
		super.init(processingEnv);
		System.out.println("Hello world!");
	    trees = Trees.instance(processingEnv);
	    typeUtils = processingEnv.getTypeUtils();
	    elementUtils = processingEnv.getElementUtils();
	    filer = processingEnv.getFiler();
	    messager = processingEnv.getMessager();
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
		        concurrentClasses.add(typeElement.getQualifiedName().toString());
		        classFiles.put(typeElement.getQualifiedName().toString(), filer.createSourceFile(typeElement.getQualifiedName().toString()));
		      }
		      
		      for (Element annotatedElement : roundEnv.getElementsAnnotatedWith(ConcurrentMethod.class)) {

			        // Check if a class has been annotated with @Factory
			        if (annotatedElement.getKind() != ElementKind.METHOD) {
			          throw new ProcessingException(annotatedElement, "Only methods in classes annotated with @%s can be annotated with @%s",
			        		  ConcurrentClass.class.getSimpleName(), ConcurrentMethod.class.getSimpleName());
			        }

			        // We can cast it, because we know that it of ElementKind.METHOD
			        ExecutableElement methodElement = (ExecutableElement) annotatedElement;
			        TypeElement declaringClass =
			        	    (TypeElement) methodElement.getEnclosingElement();
			        	String className = declaringClass.getQualifiedName().toString();
			        	if (!concurrentClasses.contains(className)) {
			        		throw new ProcessingException(annotatedElement, "Only methods in classes annotated with @%s can be annotated with @%s",
					        		  ConcurrentClass.class.getSimpleName(), ConcurrentMethod.class.getSimpleName());
			        	}
			        	Set<String> classConcurrentMethods = concurrentMethods.getOrDefault(className, new HashSet<>());
			        	classConcurrentMethods.add(methodElement.getSimpleName().toString());
			        	concurrentMethods.put(className, classConcurrentMethods);
			 }
		      
		      for (Element annotatedElement : roundEnv.getElementsAnnotatedWith(ConcurrentMethod.class)) {
		    	        ExecutableElement methodElement = (ExecutableElement) annotatedElement;
			        TypeElement declaringClass =
			        	    (TypeElement) methodElement.getEnclosingElement();
			        	String className = declaringClass.getQualifiedName().toString();
		    	      processMethod(className, methodElement);
		      }
		 }
		catch (ProcessingException e) {
			error(e.getElement(), e.getMessage());
		}
		catch (Exception e) {
			error(null, e.getMessage());
		}
		return true;
	}
	
	public void processMethod(String className, ExecutableElement methodElement) throws Exception{
		MethodScanner methodScanner = new MethodScanner();
		MethodTree methodTree = methodScanner.scan(methodElement, this.trees);
		MethodProcessor methodProcessor = new MethodProcessor(className, methodTree);
		classFiles.get(className).openOutputStream().write(methodProcessor.getGeneratedMethod().getBytes());
		//error(null, methodProcessor.getGeneratedMethod());
		//methodProcessor.processBlock(methodTree.getBody());
		
	}
	
	@Override
	public Set<String> getSupportedAnnotationTypes() {
		return new HashSet<>(Arrays.asList(ConcurrentMethod.class.getName(),ConcurrentClass.class.getName()));
	}
	
	@Override
	public SourceVersion getSupportedSourceVersion() {
		return SourceVersion.latestSupported();
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
