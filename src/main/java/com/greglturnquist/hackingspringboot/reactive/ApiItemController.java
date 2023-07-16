package com.greglturnquist.hackingspringboot.reactive;

import java.net.URI;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

// tag::intro[]
@RestController
public class ApiItemController {

    private final ItemRepository repository;

    public ApiItemController(ItemRepository repository) {
        this.repository = repository;
    }
    // end::intro[]

    // tag::all-items[]
    @GetMapping("/api/items")
    Flux<Item> findAll() {
        return this.repository.findAll();
    }
    // end::all-items[]

    // tag::one-item[]
    @GetMapping("/api/items/{id}")
    Mono<Item> findOne(@PathVariable String id) {
        return this.repository.findById(id);
    }
    // end::one-item[]

    // tag::new-item[]
    @PostMapping("/api/items")
    Mono<ResponseEntity<?>> addNewItem(@RequestBody Mono<Item> item) {
        return item.flatMap(s -> this.repository.save(s))
                .map(savedItem -> ResponseEntity
                        .created(URI.create("/api/items/" + savedItem.getId()))
                        .body(savedItem));
    }
    // end::new-item[]

    // tag::replace-item[]
    @PutMapping("/api/items/{id}")
    public Mono<ResponseEntity<?>> updateItem(
            @RequestBody Mono<Item> item,
            @PathVariable String id) {

        return item
                .map(content -> new Item(id, content.getName(), content.getDescription(),
                        content.getPrice()))
                .flatMap(this.repository::save)
                .thenReturn(ResponseEntity.created(URI.create("/api/items/" + id)).build());
    }
    // end::replace-item[]
}
