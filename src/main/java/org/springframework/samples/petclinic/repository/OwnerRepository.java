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
package org.springframework.samples.petclinic.repository;

import java.util.Collection;

import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.samples.petclinic.model.BaseEntity;
import org.springframework.samples.petclinic.model.Owner;

/**
 * Repository interface for <code>Owner</code> domain objects.
 * <p>
 * All method names are compliant with Spring Data naming conventions so this
 * interface can easily be extended for Spring Data support.
 * </p>
 *
 * @author Ken Krebs
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @author Michael Isvy
 * @author Vitaliy Fedoriv
 */
public interface OwnerRepository {

    /**
     * Retrieve <code>Owner</code>s from the data store by last name, returning all
     * owners whose last name <i>starts</i> with the given name.
     *
     * @param lastName value to search for
     * @return a <code>Collection</code> of matching <code>Owner</code>s
     */
    Collection<Owner> findByLastName(String lastName) throws DataAccessException;

    /**
     * Retrieve an <code>Owner</code> from the data store by id.
     *
     * @param id the id to search for
     * @return the <code>Owner</code> if found
     */
    Owner findById(int id) throws DataAccessException;

    /**
     * Save an <code>Owner</code> to the data store, either inserting or updating
     * it.
     *
     * @param owner the <code>Owner</code> to save
     * @see BaseEntity#isNew
     */
    void save(Owner owner) throws DataAccessException;

    /**
     * Retrieve all <code>Owner</code>s from the data store.
     *
     * @return a <code>Collection</code> of <code>Owner</code>s
     */
    Collection<Owner> findAll() throws DataAccessException;

    /**
     * Retrieve <code>Owner</code>s from the data store with pagination and sorting
     * support.
     *
     * @param pageable pagination information
     * @return a <code>Page</code> of <code>Owner</code>s
     */
    Page<Owner> findAll(Pageable pageable) throws DataAccessException;

    /**
     * Delete an <code>Owner</code> from the data store.
     *
     * @param owner the <code>Owner</code> to delete
     */
    void delete(Owner owner) throws DataAccessException;

}
