package com.k1e1n04.bookmanagement.service

import com.k1e1n04.bookmanagement.request.BookRegisterRequest
import com.k1e1n04.bookmanagement.request.BookUpdateRequest
import com.k1e1n04.bookmanagement.response.BookResponse

/**
 * 書籍に関するサービスインターフェース
 */
interface BookService {
    /**
     * すべての書籍を取得する
     *
     * @return 書籍のリスト
     */
    fun getAllBooks(): List<BookResponse>

    /**
     * 著者に紐づく書籍を取得する
     *
     * @param authorId 著者のID
     * @return 著者に紐づく書籍のリスト
     */
    fun getBooksByAuthor(authorId: String): List<BookResponse>

    /**
     * 書籍を登録する
     *
     * @param book 登録する書籍の情報
     * @return 登録された書籍の情報
     */
    fun registerBook(book: BookRegisterRequest): BookResponse

    /**
     * 書籍を更新する
     *
     * @param id 書籍のID
     * @param book 更新する書籍の情報
     * @return 更新された書籍の情報
     */
    fun updateBook(
        id: String,
        book: BookUpdateRequest,
    ): BookResponse
}
