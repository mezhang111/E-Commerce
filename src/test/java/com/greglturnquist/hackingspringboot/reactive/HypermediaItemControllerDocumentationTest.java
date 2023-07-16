package com.greglturnquist.hackingspringboot.reactive;

import static org.mockito.Mockito.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.*;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;

@WebFluxTest(controllers = HypermediaItemController.class)
@AutoConfigureRestDocs
public class HypermediaItemControllerDocumentationTest {

    @Autowired private WebTestClient webTestClient;

    @MockBean InventoryService service;

    @MockBean ItemRepository repository;
    // end::intro[]

    // tag::test1[]
    @Test
    void findingAllItems() {
        when(repository.findAll())
                .thenReturn(Flux.just(
                        new Item("Alf alarm clock",
                                "nothing I really need", 19.99)));
        when(repository.findById((String) null))
                .thenReturn(Mono.just(
                        new Item("item-1", "Alf alarm clock",
                                "nothing I really need", 19.99)));

        this.webTestClient.get().uri("/hypermedia/items")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .consumeWith(document("findAll-hypermedia",
                        preprocessResponse(prettyPrint())));
    }
    // end::test1[]

    // tag::test2[]
    // @Test
    void postNewItem() {
        this.webTestClient.post().uri("/hypermedia/items")
                .body(Mono.just(
                                new Item("item-1", "Alf alarm clock",
                                        "nothing I really need", 19.99)),
                        Item.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody().isEmpty();
    }
    // end::test2[]

    // tag::test3[]
    @Test
    void findOneItem() {
        when(repository.findById("item-1")).thenReturn(Mono.just(
                new Item("item-1", "Alf alarm clock", "nothing I really need", 19.99)));

        this.webTestClient.get().uri("/hypermedia/items/item-1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .consumeWith(document("findOne-hypermedia", preprocessResponse(prettyPrint()),
                        links(
                                linkWithRel("self").description("Canonical link to this `Item`"),
                                linkWithRel("item").description("Link back to the aggregate root"))));
    }
    // end::test3[]
}
