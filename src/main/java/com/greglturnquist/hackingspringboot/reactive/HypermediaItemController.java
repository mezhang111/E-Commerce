package com.greglturnquist.hackingspringboot.reactive;

import static org.springframework.hateoas.mediatype.alps.Alps.*;
import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.*;

import reactor.core.publisher.Mono;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Links;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.mediatype.alps.Alps;
import org.springframework.hateoas.mediatype.alps.Type;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

// tag::intro[]
@RestController
public class HypermediaItemController {

    private final ItemRepository repository;

    public HypermediaItemController(ItemRepository repository) {
        this.repository = repository;
    }
    // end::intro[]

    // tag::root[]
    @GetMapping("/hypermedia")
    Mono<RepresentationModel<?>> root() {
        HypermediaItemController controller = //
                methodOn(HypermediaItemController.class);

        Mono<Link> selfLink = linkTo(controller.root()).withSelfRel().toMono();

        Mono<Link> itemsAggregateLink = //
                linkTo(controller.findAll()) //
                        .withRel(IanaLinkRelations.ITEM) //
                        .toMono();

        return selfLink.zipWith(itemsAggregateLink) //
                .map(links -> Links.of(links.getT1(), links.getT2())) //
                .map(links -> new RepresentationModel<>(links.toList()));
    }
    // end::root[]

    // tag::find-all[]
    @GetMapping("/hypermedia/items")
    Mono<CollectionModel<EntityModel<Item>>> findAll() {

        return this.repository.findAll() //
                .flatMap(item -> findOne(item.getId())) //
                .collectList() //
                .flatMap(entityModels -> linkTo(methodOn(HypermediaItemController.class) //
                        .findAll()).withSelfRel() //
                        .toMono() //
                        .map(selfLink -> CollectionModel.of(entityModels, selfLink)));
    }
    // end::find-all[]

    // tag::find-one[]
    @GetMapping("/hypermedia/items/{id}")
    Mono<EntityModel<Item>> findOne(@PathVariable String id) {
        HypermediaItemController controller = methodOn(HypermediaItemController.class); // <1>

        Mono<Link> selfLink = linkTo(controller.findOne(id)).withSelfRel().toMono(); // <2>

        Mono<Link> aggregateLink = linkTo(controller.findAll()) //
                .withRel(IanaLinkRelations.ITEM).toMono(); // <3>

        return Mono.zip(repository.findById(id), selfLink, aggregateLink) // <4>
                .map(o -> EntityModel.of(o.getT1(), Links.of(o.getT2(), o.getT3()))); // <5>
    }
    // end::find-one[]
}
