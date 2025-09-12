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

/**
 * Builder pattern for creating test data with flexible configuration.
 * Supports common entity fields used in cache tests.
 */
public class TestDataBuilder {
    private Integer id;
    private String name;

    public TestDataBuilder withId(Integer id) {
        this.id = id;
        return this;
    }

    public TestDataBuilder withName(String name) {
        this.name = name;
        return this;
    }

    // Getters for the builder data
    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    // Static factory method for the common scenario used in tests
    public static TestDataBuilder withIdAndName(Integer id, String name) {
        return new TestDataBuilder().withId(id).withName(name);
    }
}