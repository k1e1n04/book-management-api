package com.k1e1n04.bookmanagement.repository

import com.k1e1n04.bookmanagement.model.AuthorEntity
import java.util.UUID

/**
 * 著者リポジトリインターフェース
 */
interface AuthorRepository {
    /**
     * 著者を保存する
     *
     * @param author 著者エンティティ
     * @return 保存された著者エンティティ
     */
    fun save(author: AuthorEntity): AuthorEntity

    /**
     * 著者を全件取得する
     *
     * @return 著者エンティティのリスト
     */
    fun findAll(): List<AuthorEntity>

    /**
     * 著者を取得する
     *
     * @param authorId 著者ID
     * @return 著者エンティティ、存在しない場合はnull
     */
    fun findById(authorId: UUID): AuthorEntity?

    /**
     * 著者を取得する
     *
     * @param authorIds 著者IDのリスト
     * @return 著者エンティティのリスト
     */
    fun findByIds(authorIds: List<UUID>): List<AuthorEntity>

    /**
     * 著者を更新する
     *
     * @param author 更新する著者エンティティ
     * @return 更新された著者エンティティ
     */
    fun update(author: AuthorEntity): AuthorEntity
}
