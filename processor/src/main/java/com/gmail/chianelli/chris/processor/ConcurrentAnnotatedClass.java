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

import com.gmail.chianelli.chris.annotation.ConcurrentClass;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypeException;
import org.apache.commons.lang3.StringUtils;

/**
 * Holds the information about a class annotated with @ConcurrentClass
 *
 * @author Hannes Dorfmann
 */
public class ConcurrentAnnotatedClass {

  private TypeElement annotatedClassElement;
  private String qualifiedClassName;
  private String simpleClassName;

  /**
   * @throws ProcessingException if id() from annotation is null
   */
  public ConcurrentAnnotatedClass(TypeElement classElement) throws ProcessingException {
    this.annotatedClassElement = classElement;
    ConcurrentClass annotation = classElement.getAnnotation(ConcurrentClass.class);

    // Get the full QualifiedTypeName
    try {
      Class<?> clazz = annotation.name();
      qualifiedClassName = clazz.getCanonicalName();
      simpleClassName = clazz.getSimpleName();
    } catch (MirroredTypeException mte) {
      DeclaredType classTypeMirror = (DeclaredType) mte.getTypeMirror();
      TypeElement classTypeElement = (TypeElement) classTypeMirror.asElement();
      qualifiedClassName = classTypeElement.getQualifiedName().toString();
      simpleClassName = classTypeElement.getSimpleName().toString();
    }
  }

  /**
   * Get the full qualified name of the type specified in  {@link Factory#type()}.
   *
   * @return qualified name
   */
  public String getQualifiedName() {
    return qualifiedClassName;
  }

  /**
   * Get the simple name of the type specified in  {@link Factory#type()}.
   *
   * @return qualified name
   */
  public String getSimpleName() {
    return simpleClassName;
  }

  /**
   * The original element that was annotated with @Factory
   */
  public TypeElement getTypeElement() {
    return annotatedClassElement;
  }
  
  public TypeMirror getTypeMirror() {
	  return annotatedClassElement.asType();
  }
  
  @Override
  public boolean equals(Object o) {
	  if (o instanceof ConcurrentAnnotatedClass) {
		  ConcurrentAnnotatedClass other = (ConcurrentAnnotatedClass) o;
		  return qualifiedClassName.equals(other.qualifiedClassName);
	  }
	  return false;
  }
  
  @Override
  public int hashCode() {
	  return qualifiedClassName.hashCode();
  }
}
