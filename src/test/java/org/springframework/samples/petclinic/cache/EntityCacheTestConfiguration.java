/*
 * Copyright 2002-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.cache;

import org.springframework.http.HttpStatus;

import java.util.Collection;
import java.util.function.Function;

/**
 * Configuration class that encapsulates entity-specific test data and operations
 * for generic cache testing. This allows the same test logic to work with any entity type.
 *
 * @param <E> Entity type (e.g., Vet, Pet, Owner)
 * @param <D> DTO type (e.g., VetDto, PetDto, OwnerDto)
 */
public class EntityCacheTestConfiguration<E, D> {
    private final String entityName;
    private final String apiEndpoint;
    private final Function<TestDataBuilder, E> entityFactory;
    private final Function<TestDataBuilder, D> dtoFactory;
    private final Object repository;
    private final HttpStatus expectedCreateStatus;
    private final HttpStatus expectedUpdateStatus;
    private final HttpStatus expectedDeleteStatus;

    // Repository operation mocks
    private final RepositoryOperations<E> repositoryOps;

    public EntityCacheTestConfiguration(Builder<E, D> builder) {
        this.entityName = builder.entityName;
        this.apiEndpoint = builder.apiEndpoint;
        this.entityFactory = builder.entityFactory;
        this.dtoFactory = builder.dtoFactory;
        this.repository = builder.repository;
        this.expectedCreateStatus = builder.expectedCreateStatus;
        this.expectedUpdateStatus = builder.expectedUpdateStatus;
        this.expectedDeleteStatus = builder.expectedDeleteStatus;
        this.repositoryOps = builder.repositoryOps;
    }

    // Getters
    public String getEntityName() {
        return entityName;
    }

    public String getApiEndpoint() {
        return apiEndpoint;
    }

    public Function<TestDataBuilder, E> getEntityFactory() {
        return entityFactory;
    }

    public Function<TestDataBuilder, D> getDtoFactory() {
        return dtoFactory;
    }

    public Object getRepository() {
        return repository;
    }

    public HttpStatus getExpectedCreateStatus() {
        return expectedCreateStatus;
    }

    public HttpStatus getExpectedUpdateStatus() {
        return expectedUpdateStatus;
    }

    public HttpStatus getExpectedDeleteStatus() {
        return expectedDeleteStatus;
    }

    public RepositoryOperations<E> getRepositoryOps() {
        return repositoryOps;
    }

    // Helper methods
    public E createEntity(TestDataBuilder builder) {
        return entityFactory.apply(builder);
    }

    public D createDto(TestDataBuilder builder) {
        return dtoFactory.apply(builder);
    }

    // Builder pattern
    public static <E, D> Builder<E, D> builder() {
        return new Builder<>();
    }

    public static class Builder<E, D> {
        private String entityName;
        private String apiEndpoint;
        private Function<TestDataBuilder, E> entityFactory;
        private Function<TestDataBuilder, D> dtoFactory;
        private Object repository;
        private HttpStatus expectedCreateStatus = HttpStatus.CREATED;
        private HttpStatus expectedUpdateStatus = HttpStatus.NO_CONTENT;
        private HttpStatus expectedDeleteStatus = HttpStatus.NO_CONTENT;
        private RepositoryOperations<E> repositoryOps;

        public Builder<E, D> entityName(String entityName) {
            this.entityName = entityName;
            return this;
        }

        public Builder<E, D> apiEndpoint(String apiEndpoint) {
            this.apiEndpoint = apiEndpoint;
            return this;
        }

        public Builder<E, D> entityFactory(Function<TestDataBuilder, E> entityFactory) {
            this.entityFactory = entityFactory;
            return this;
        }

        public Builder<E, D> dtoFactory(Function<TestDataBuilder, D> dtoFactory) {
            this.dtoFactory = dtoFactory;
            return this;
        }

        public Builder<E, D> repository(Object repository) {
            this.repository = repository;
            return this;
        }

        public Builder<E, D> expectedCreateStatus(HttpStatus status) {
            this.expectedCreateStatus = status;
            return this;
        }

        public Builder<E, D> expectedUpdateStatus(HttpStatus status) {
            this.expectedUpdateStatus = status;
            return this;
        }

        public Builder<E, D> expectedDeleteStatus(HttpStatus status) {
            this.expectedDeleteStatus = status;
            return this;
        }

        public Builder<E, D> repositoryOperations(RepositoryOperations<E> repositoryOps) {
            this.repositoryOps = repositoryOps;
            return this;
        }

        public EntityCacheTestConfiguration<E, D> build() {
            if (entityName == null || apiEndpoint == null || entityFactory == null || 
                dtoFactory == null || repository == null || repositoryOps == null) {
                throw new IllegalStateException("All required fields must be set");
            }
            return new EntityCacheTestConfiguration<>(this);
        }
    }

    /**
     * Interface for repository operations that can be mocked
     */
    public interface RepositoryOperations<E> {
        void mockFindAll(Object repository, Collection<E> entities);
        void mockFindAllSequential(Object repository, Collection<E> firstCall, Collection<E> secondCall);
        void mockFindById(Object repository, Integer id, E entity);
        void mockFindByIdSequential(Object repository, Integer id, E firstCall, E secondCall);
        void mockSave(Object repository, E entity);
        void mockDelete(Object repository, E entity);
    }
}