package com.greglturnquist.hackingspringboot.reactive;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

// tag::code[]
public interface CartRepository extends ReactiveCrudRepository<Cart, String> {

}
// end::code[]
