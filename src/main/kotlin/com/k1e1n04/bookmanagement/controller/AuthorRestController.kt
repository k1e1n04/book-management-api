package com.k1e1n04.bookmanagement.controller

import com.k1e1n04.bookmanagement.request.AuthorRegisterRequest
import com.k1e1n04.bookmanagement.request.AuthorUpdateRequest
import com.k1e1n04.bookmanagement.service.AuthorService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

/**
 * 著者情報を管理するRESTコントローラー
 */
@RestController
@RequestMapping("/api/authors")
class AuthorRestController(
    private val authorService: AuthorService
) {
    /**
     * すべての著者情報を取得
     *
     * @return 著者のリスト
     */
    @GetMapping
    fun getAllAuthors() = authorService.getAllAuthors()

    /**
     * 著者を登録
     *
     * @param author 登録する著者の情報
     * @return 登録された著者の情報
     */
     @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun registerAuthor(
        @RequestBody @Valid author: AuthorRegisterRequest
    ) = authorService.registerAuthor(author)

    /**
     * 著者情報を更新
     *
     * @param author 更新する著者の情報
     * @return 更新された著者の情報
     */
    @PutMapping("/{id}")
    fun updateAuthor(
        @PathVariable id: String,
        @RequestBody @Valid author: AuthorUpdateRequest
        ) =
        authorService.updateAuthor(
            id = id,
            author = author
        )
}
