
package com.greglturnquist.hackingspringboot.reactive;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

// tag::code[]
@Repository
public interface ItemByExampleRepository extends ReactiveMongoRepository<Item, String>  {

}
// end::code[]
