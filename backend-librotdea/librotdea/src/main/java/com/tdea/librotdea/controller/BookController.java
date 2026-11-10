package com.tdea.librotdea.controller;

import com.tdea.librotdea.entity.Book;
import com.tdea.librotdea.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = "/api/books", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Book API", description = "CRUD Operations for Managing books")
public class BookController {

    private final BookService service;

    @Autowired
    public BookController(BookService service) {
        this.service = service;
    }

    // GET ALL
    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks() {
        return ResponseEntity.ok(service.findAll());
    }

    // SEARCH por nombre (declarado antes de /{id} por claridad)
    @GetMapping("/search")
    @Operation(summary = "Search books by name")
    public ResponseEntity<List<Book>> searchBooks(@RequestParam String name) {
        List<Book> books = service.findByName(name);
        if (books.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204
        }
        return ResponseEntity.ok(books); // 200
    }

    // GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(service.findById(id));
        } catch (Exception e) {
            return ResponseEntity.notFound().build(); // 404
        }
    }

    // CREATE
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Book> addBook(@Valid @RequestBody Book book) {
        Book savedBook = service.save(book);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedBook.getId())
                .toUri();
        return ResponseEntity.created(location).body(savedBook); // 201
    }

    // UPDATE
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Book> updateBook(@PathVariable Long id, @Valid @RequestBody Book book) {
        try {
            service.findById(id); // validar existencia
            return ResponseEntity.ok(service.update(id, book)); // 200
        } catch (Exception e) {
            return ResponseEntity.notFound().build(); // 404
        }
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        try {
            service.findById(id); // validar existencia
            service.deleteById(id);
            return ResponseEntity.noContent().build(); // 204
        } catch (Exception e) {
            return ResponseEntity.notFound().build(); // 404
        }
    }
}
