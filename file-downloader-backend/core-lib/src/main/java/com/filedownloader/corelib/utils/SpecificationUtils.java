package com.filedownloader.corelib.utils;

import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static java.util.Objects.isNull;

@UtilityClass
public final class SpecificationUtils {

    /**
     * Specification for searching with collection filter
     * @param fieldName - name of field for search
     * @param values - collection with filter values
     * @return specification for object with <T> type
     */
    public <T, R> Specification<T> searchIn(String fieldName, Collection<R> values) {
        if (CollectionUtils.isEmpty(values))
            return null;

        List<R> nonNullValues = values.stream().filter(Objects::nonNull).toList();
        return (root, query, criteriaBuilder) -> root.get(fieldName).in(nonNullValues);
    }

    /**
     * Specification for searching with collection filter
     * @param paths - path to field for search
     * @param values - collection with filter values
     * @return specification for object with <T> type
     */
    public <T, R> Specification<T> searchIn(
            List<String> paths,
            Collection<R> values
    ) {
        if (CollectionUtils.isEmpty(values))
            return null;

        List<R> nonNullValues = values.stream().filter(Objects::nonNull).toList();
        return (root, query, criteriaBuilder) -> getObjectPath(root, paths).in(nonNullValues);
    }

    /**
     * Specification for searching with ilike filter
     * @param fieldName - name of field for search
     * @param value - string filter value
     * @return specification for object with <T> type
     */
    public <T> Specification<T> searchLike(String fieldName, String value) {
        return StringUtils.isEmpty(value)
            ? null
            : (root, query, cb) -> cb.like(cb.lower(root.get(fieldName)), "%" + value.toLowerCase() + "%");
    }

    public <T> Specification<T> searchLike(
            List<String> paths,
            String value
    ) {
        return CollectionUtils.isEmpty(paths) || StringUtils.isEmpty(value)
                ? null
                : (root, query, cb) -> cb.like(cb.lower(getObjectPath(root, paths).as(String.class)), "%" + value.toLowerCase() + "%");
    }

    /**
     * Specification for filter by field
     * @param fieldName - name of field for search
     * @param fieldValue - filter value
     * @return specification for object with <T> type
     */
    public <T, R> Specification<T> byFieldEqual(
        String fieldName,
        R fieldValue
    ) {
        return  isNull(fieldValue)
            ? null
            : (root, query, criteriaBuilder) -> criteriaBuilder
            .equal(root.get(fieldName), fieldValue);
    }

    /**
     * Specification for filter by field
     * @param fieldName - name of field for search
     * @return specification for object with <T> type
     */
    public <T, R> Specification<T> byFieldEqualNull(
        String fieldName
    ) {
        return (root, query, criteriaBuilder) -> criteriaBuilder
            .isNull(root.get(fieldName));
    }

    /**
     * Specification for filter by field path
     * @param paths - path to field for search
     * @param fieldValue - filter value
     * @return specification for object with <T> type
     */
    public <T, R> Specification<T> byFieldEqual(
            List<String> paths,
            R fieldValue
    ) {
        return CollectionUtils.isEmpty(paths) || isNull(fieldValue)
                ? null
                : (root, query, criteriaBuilder) -> criteriaBuilder
                .equal(
                        getObjectPath(root, paths),
                        fieldValue
                );
    }

    public <T, R extends Comparable<? super R>> Specification<T> byFieldGeOrEqual(
            List<String> paths,
            R fieldValue,
            Class<R> clazz
    ) {
        return CollectionUtils.isEmpty(paths) || isNull(fieldValue)
                ? null
                : (root, query, cb) -> cb
                .greaterThanOrEqualTo(getObjectPath(root, paths).as(clazz), fieldValue);
    }

    public <T, R extends Comparable<? super R>> Specification<T> byFieldLeOrEqual(
            List<String> paths,
            R fieldValue,
            Class<R> clazz
    ) {
        return CollectionUtils.isEmpty(paths) || isNull(fieldValue)
                ? null
                : (root, query, cb) -> cb
                .lessThanOrEqualTo(getObjectPath(root, paths).as(clazz), fieldValue);
    }

    public <T> Specification<T> checkFieldAsNull(
            List<String> paths,
            Boolean isNullCheck
    ) {
        return CollectionUtils.isEmpty(paths) || isNull(isNullCheck)
                ? null
                : (root, query, cb) -> isNullCheck
                ? cb.isNull(getObjectPath(root, paths))
                : cb.isNotNull(getObjectPath(root, paths));
    }


    public <T> Path<Object> getObjectPath(Root<T> root, List<String> paths) {
        if (CollectionUtils.isEmpty(paths)) {
            throw new IllegalArgumentException("The argument containing the path must not be empty");
        }

        Path<Object> objectPath = null;
        for (String path : paths) {
            objectPath = isNull(objectPath) ? root.get(path) : objectPath.get(path);
        }
        return objectPath;
    }

}
