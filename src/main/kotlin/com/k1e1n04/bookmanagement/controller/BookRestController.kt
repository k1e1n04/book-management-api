package com.k1e1n04.bookmanagement.controller

import com.k1e1n04.bookmanagement.request.BookRegisterRequest
import com.k1e1n04.bookmanagement.request.BookUpdateRequest
import com.k1e1n04.bookmanagement.service.BookService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * 著者情報を管理するRESTコントローラー
 */
@RestController
@RequestMapping("/api/books")
class BookRestController(
    private val bookService: BookService
) {
    /**
     * すべての書籍情報を取得
     *
     * @return 書籍のリスト
     */
    @GetMapping
    fun getAllBooks() = bookService.getAllBooks()

    /**
     * 著者に紐づく書籍情報を取得
     *
     * @param authorId 著者のID
     * @return 著者に紐づく書籍のリスト
     */
    @GetMapping("/author/{authorId}")
    fun getBooksByAuthor(
        @PathVariable authorId: String
    ) = bookService.getBooksByAuthor(authorId)

    /**
     * 書籍を登録
     *
     * @param book 登録する書籍の情報
     * @return 登録された書籍の情報
     */
    @PostMapping
    fun registerBook(
        @Valid @RequestBody book: BookRegisterRequest
    ) = bookService.registerBook(book)

    /**
     * 書籍情報を更新
     *
     * @param book 更新する書籍の情報
     * @return 更新された書籍の情報
     */
    @PutMapping("/{id}")
    fun updateBook(
        @PathVariable id: String,
        @Valid @RequestBody book: BookUpdateRequest
    ) = bookService.updateBook(
        id = id,
        book = book
    )
}
