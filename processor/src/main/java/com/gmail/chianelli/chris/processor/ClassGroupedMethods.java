/*
 * Copyright (C) 2015 Hannes Dorfmann
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gmail.chianelli.chris.processor;

import com.gmail.chianelli.chris.tasks.Task;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.sun.source.tree.MethodTree;
import com.sun.source.util.Trees;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

/**
 * This class holds all {@link ConcurrentAnnotatedClass}s that belongs to one factory. In other words,
 * this class holds a list with all @ConcurrentMethod annotated methods for a @ConcurrentClass.
 */
public class ClassGroupedMethods {

  /**
   * Will be added to the name of the generated factory class
   */
  private static final String SUFFIX = "Tasks";

  private ConcurrentAnnotatedClass clazz;

  private Set<ConcurrentAnnotatedMethod> itemsMap =
      new LinkedHashSet<ConcurrentAnnotatedMethod>();

  public ClassGroupedMethods(ConcurrentAnnotatedClass clazz) {
    this.clazz = clazz;
  }

  /**
   * Adds an annotated class to this factory.
   *
   * @throws ProcessingException if another annotated class with the same id is
   * already present.
   */
  public void add(ConcurrentAnnotatedMethod toInsert) throws ProcessingException {
    itemsMap.add(toInsert);
  }

  public void generateCode(Trees trees, Elements elementUtils, Filer filer) throws IOException {
    TypeElement superClassName = elementUtils.getTypeElement(clazz.getQualifiedName());
    String factoryClassName = superClassName.getSimpleName() + SUFFIX;
    String qualifiedFactoryClassName = clazz.getQualifiedName() + SUFFIX;
    PackageElement pkg = elementUtils.getPackageOf(superClassName);
    String packageName = pkg.isUnnamed() ? null : pkg.getQualifiedName().toString();
    List<MethodSpec> methods = new ArrayList<MethodSpec>(itemsMap.size());
    for (ConcurrentAnnotatedMethod method : itemsMap) {
    	MethodScanner methodScanner = new MethodScanner();
		MethodTree methodTree = methodScanner.scan(method.getExecutableElement(), trees);
        MethodProcessor methodProcessor = new MethodProcessor(clazz.getQualifiedName(), methodTree);
    	MethodSpec code = methodProcessor.getGeneratedMethod();
    	methods.add(code);
    }
    
    TypeSpec.Builder typeSpec = TypeSpec.classBuilder(factoryClassName);
    for (MethodSpec methodCode : methods) {
    	typeSpec.addMethod(methodCode);
    }
    // Write file
    JavaFile.builder(packageName, typeSpec.build()).build().writeTo(filer);
  }

  /**
   * Generate the java code
   *
   * @throws IOException

  public void generateCode(Elements elementUtils, Filer filer) throws IOException {

  TypeElement superClassName = elementUtils.getTypeElement(qualifiedClassName);
  String factoryClassName = superClassName.getSimpleName() + SUFFIX;

  JavaFileObject jfo = filer.createSourceFile(qualifiedClassName + SUFFIX);
  Writer writer = jfo.openWriter();
  JavaWriter jw = new JavaWriter(writer);

  // Write package
  PackageElement pkg = elementUtils.getPackageOf(superClassName);
  if (!pkg.isUnnamed()) {
  jw.emitPackage(pkg.getQualifiedName().toString());
  jw.emitEmptyLine();
  } else {
  jw.emitPackage("");
  }

  jw.beginType(factoryClassName, "class", EnumSet.of(Modifier.PUBLIC));
  jw.emitEmptyLine();
  jw.beginMethod(qualifiedClassName, "create", EnumSet.of(Modifier.PUBLIC), "String", "id");

  jw.beginControlFlow("if (id == null)");
  jw.emitStatement("throw new IllegalArgumentException(\"id is null!\")");
  jw.endControlFlow();

  for (FactoryAnnotatedClass item : itemsMap.values()) {
  jw.beginControlFlow("if (\"%s\".equals(id))", item.getId());
  jw.emitStatement("return new %s()", item.getTypeElement().getQualifiedName().toString());
  jw.endControlFlow();
  jw.emitEmptyLine();
  }

  jw.emitStatement("throw new IllegalArgumentException(\"Unknown id = \" + id)");
  jw.endMethod();

  jw.endType();

  jw.close();
  }

   */
}
