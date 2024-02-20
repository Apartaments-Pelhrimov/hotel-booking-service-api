/*
 * Copyright (c) 2024. Mykhailo Balakhon mailto:9mohapx9@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ua.mibal.test.model;

import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.beans.PropertyEditor;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
public class TestBindingResult implements BindingResult {

    private final List<FieldError> fieldErrors;

    public TestBindingResult(Map<String, String> fieldErrorsMap) {
        this.fieldErrors = toFieldErrors(fieldErrorsMap);
    }

    @Override
    public List<FieldError> getFieldErrors() {
        return fieldErrors;
    }

    @Override
    public List<ObjectError> getGlobalErrors() {
        return emptyList();
    }

    public List<FieldError> toFieldErrors(Map<String, String> fieldErrorsMap) {
        return fieldErrorsMap.entrySet().stream()
                .map(entry -> new FieldError("test", entry.getKey(), entry.getValue()))
                .toList();
    }


    // UNUSED STUBS

    @Override
    public Object getTarget() {
        return null;
    }

    @Override
    public Map<String, Object> getModel() {
        return null;
    }

    @Override
    public Object getRawFieldValue(String field) {
        return null;
    }

    @Override
    public PropertyEditor findEditor(String field, Class<?> valueType) {
        return null;
    }

    @Override
    public PropertyEditorRegistry getPropertyEditorRegistry() {
        return null;
    }

    @Override
    public String[] resolveMessageCodes(String errorCode) {
        return new String[0];
    }

    @Override
    public String[] resolveMessageCodes(String errorCode, String field) {
        return new String[0];
    }

    @Override
    public void addError(ObjectError error) {

    }

    @Override
    public String getObjectName() {
        return null;
    }

    @Override
    public String getNestedPath() {
        return null;
    }

    @Override
    public void setNestedPath(String nestedPath) {

    }

    @Override
    public void pushNestedPath(String subPath) {

    }

    @Override
    public void popNestedPath() throws IllegalStateException {

    }

    @Override
    public void reject(String errorCode) {

    }

    @Override
    public void reject(String errorCode, String defaultMessage) {

    }

    @Override
    public void reject(String errorCode, Object[] errorArgs, String defaultMessage) {

    }

    @Override
    public void rejectValue(String field, String errorCode) {

    }

    @Override
    public void rejectValue(String field, String errorCode, String defaultMessage) {

    }

    @Override
    public void rejectValue(String field, String errorCode, Object[] errorArgs, String defaultMessage) {

    }

    @Override
    public void addAllErrors(Errors errors) {

    }

    @Override
    public boolean hasErrors() {
        return false;
    }

    @Override
    public int getErrorCount() {
        return 0;
    }

    @Override
    public List<ObjectError> getAllErrors() {
        return null;
    }

    @Override
    public boolean hasGlobalErrors() {
        return false;
    }

    @Override
    public int getGlobalErrorCount() {
        return 0;
    }

    @Override
    public ObjectError getGlobalError() {
        return null;
    }

    @Override
    public boolean hasFieldErrors() {
        return false;
    }

    @Override
    public int getFieldErrorCount() {
        return 0;
    }

    @Override
    public FieldError getFieldError() {
        return null;
    }

    @Override
    public boolean hasFieldErrors(String field) {
        return false;
    }

    @Override
    public int getFieldErrorCount(String field) {
        return 0;
    }

    @Override
    public List<FieldError> getFieldErrors(String field) {
        return null;
    }

    @Override
    public FieldError getFieldError(String field) {
        return null;
    }

    @Override
    public Object getFieldValue(String field) {
        return null;
    }

    @Override
    public Class<?> getFieldType(String field) {
        return null;
    }
}
