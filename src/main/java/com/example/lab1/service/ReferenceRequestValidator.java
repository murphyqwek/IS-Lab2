package com.example.lab1.service;

import com.example.lab1.exception.InvalidReferenceException;

final class ReferenceRequestValidator {

    private ReferenceRequestValidator() {
    }

    static void requireExactlyOne(Object id, Object newObject, String fieldName) {
        boolean hasId = id != null;
        boolean hasNewObject = newObject != null;

        if (hasId == hasNewObject) {
            throw new InvalidReferenceException(
                    "Для поля '" + fieldName + "' необходимо указать либо id существующего объекта, либо newObject, но не оба одновременно"
            );
        }
    }

    static void requireReplacement(Object replacementId, String entityName) {
        if (replacementId == null) {
            throw new InvalidReferenceException(
                    entityName + " используется другими объектами. Перед удалением необходимо указать replacementId"
            );
        }
    }

    static void requireDifferent(Object id, Object replacementId, String entityName) {
        if (id != null && id.equals(replacementId)) {
            throw new InvalidReferenceException(
                    "Нельзя заменить " + entityName + " самим собой"
            );
        }
    }
}
