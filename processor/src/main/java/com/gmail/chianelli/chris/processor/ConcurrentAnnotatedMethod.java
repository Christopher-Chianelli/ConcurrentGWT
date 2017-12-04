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
import com.gmail.chianelli.chris.annotation.ConcurrentMethod;

import javax.lang.model.element.TypeElement;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypeException;
import org.apache.commons.lang3.StringUtils;

/**
 * Holds the information about a class annotated with @ConcurrentClass
 *
 * @author Hannes Dorfmann
 */
public class ConcurrentAnnotatedMethod {

  private ExecutableElement annotatedMethodElement;
  private String qualifiedClassName;
  private String methodName;

  public ConcurrentAnnotatedMethod(ExecutableElement methodElement) throws ProcessingException {
    this.annotatedMethodElement = methodElement;
    ConcurrentMethod annotation = methodElement.getAnnotation(ConcurrentMethod.class);
    methodName = methodElement.getSimpleName().toString();
    
    TypeElement clazz = (TypeElement) (methodElement.getEnclosingElement());
    qualifiedClassName = clazz.getQualifiedName().toString();
  }

  /**
   * Get the simple name of the type specified in  {@link Factory#type()}.
   *
   * @return qualified name
   */
  public String getClassName() {
    return qualifiedClassName;
  }
  
  public String getMethodName() {
	  return methodName;
  }

  /**
   * The original element that was annotated with @Factory
   */
  public ExecutableElement getExecutableElement() {
    return annotatedMethodElement;
  }
  
  @Override
  public boolean equals(Object o) {
	  if (o instanceof ConcurrentAnnotatedMethod) {
		  ConcurrentAnnotatedMethod other = (ConcurrentAnnotatedMethod) o;
		  return qualifiedClassName.equals(other.qualifiedClassName) && methodName.equals(other.methodName);
	  }
	  return false;
  }
  
  @Override
  public int hashCode() {
	  return qualifiedClassName.hashCode() ^ methodName.hashCode();
  }
}
