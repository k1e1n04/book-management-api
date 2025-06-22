package com.k1e1n04.bookmanagement.repository

import com.k1e1n04.bookmanagement.model.BookEntity
import java.util.UUID

/**
 * 書籍リポジトリインターフェース
 */
interface BookRepository {
    /**
     * 書籍を保存する
     *
     * @param book 書籍エンティティ
     * @return 保存された書籍エンティティ
     */
    fun save(book: BookEntity): BookEntity

    /**
     * 書籍をIDで取得する
     *
     * @param id 書籍のID
     * @return 書籍エンティティ
     */
    fun findById(id: UUID): BookEntity?

    /**
     * 全ての書籍を取得する
     *
     * @return 書籍エンティティのリスト
     */
    fun findAll(): List<BookEntity>

    /**
     * 書籍を更新する
     *
     * @param book 書籍エンティティ
     * @return 更新された書籍エンティティ
     */
    fun update(book: BookEntity): BookEntity

    /**
     * 著者IDで書籍を取得する
     *
     * @param authorId 著者のID
     * @return 著者に紐づく書籍エンティティのリスト
     */
    fun findByAuthorId(authorId: UUID): List<BookEntity>
}
