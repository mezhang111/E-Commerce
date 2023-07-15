/*
 * Copyright 2019 the original author or authors.
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

package com.greglturnquist.hackingspringboot.reactive;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.data.mongodb.core.ReactiveFluentMongoOperations;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import static org.springframework.data.mongodb.core.query.Criteria.byExample;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Greg Turnquist
 */
// tag::code[]
@Service
public class InventoryService {

    private ItemRepository repository;
    @Autowired
    private ItemByExampleRepository exampleRepository;
    private ReactiveFluentMongoOperations fluentOperations;

    public InventoryService(ItemRepository repository, //
                            ItemByExampleRepository exampleRepository, //
                            ReactiveFluentMongoOperations fluentOperations) {
        this.repository = repository;
        this.exampleRepository = exampleRepository;
        this.fluentOperations = fluentOperations;
    }

    Flux<Item> getItems() {
        // imagine calling a remote service!
        return Flux.empty();
    }

    // tag::code-2[]
    Flux<Item> search(String partialName, String partialDescription, boolean useAnd) {
        if (partialName != null) {
            if (partialDescription != null) {
                if (useAnd) {
                    return repository //
                            .findByNameContainingAndDescriptionContainingAllIgnoreCase( //
                                    partialName, partialDescription);
                } else {
                    return repository.findByNameContainingOrDescriptionContainingAllIgnoreCase( //
                            partialName, partialDescription);
                }
            } else {
                return repository.findByNameContaining(partialName);
            }
        } else {
            if (partialDescription != null) {
                return repository.findByDescriptionContainingIgnoreCase(partialDescription);
            } else {
                return repository.findAll();
            }
        }
    }
    // end::code-2[]

    // tag::code-3[]
    Flux<Item> searchByExample(String name, String description, boolean useAnd) {
        Item item = new Item(name, description, 0.0); // <1>

        ExampleMatcher matcher = (useAnd // <2>
                ? ExampleMatcher.matchingAll() //
                : ExampleMatcher.matchingAny()) //
                .withStringMatcher(StringMatcher.CONTAINING) // <3>
                .withIgnoreCase() // <4>
                .withIgnorePaths("price"); // <5>

        Example<Item> probe = Example.of(item, matcher); // <6>

        return exampleRepository.findAll(probe); // <7>
    }
    // end::code-3[]
}
// end::code[]