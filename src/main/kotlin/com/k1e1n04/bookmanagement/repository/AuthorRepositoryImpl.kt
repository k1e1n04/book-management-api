package com.k1e1n04.bookmanagement.repository

import com.k1e1n04.bookmanagement.exception.DomainValidationException
import com.k1e1n04.bookmanagement.exception.InvalidStateException
import com.k1e1n04.bookmanagement.exception.NotFoundException
import com.k1e1n04.bookmanagement.jooq.tables.Authors
import com.k1e1n04.bookmanagement.jooq.tables.records.AuthorsRecord
import com.k1e1n04.bookmanagement.model.AuthorEntity
import java.util.UUID
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

/**
 * 著者リポジトリの実装
 *
 * @property dslContext JOOQのDSLコンテキスト
 */
@Repository
class AuthorRepositoryImpl(
    private val dslContext: DSLContext,
) : AuthorRepository {
    private val a = Authors.AUTHORS.`as`("a")

    override fun save(author: AuthorEntity): AuthorEntity {
        val record =
            dslContext.newRecord(a).apply {
                id = author.id.toString()
                name = author.name
                birthDate = author.dateOfBirth
            }

        record.store()

        return author
    }

    override fun findAll(): List<AuthorEntity> =
        dslContext
            .selectFrom(a)
            .fetch()
            .map(::toEntity)

    override fun findById(authorId: UUID): AuthorEntity? =
        dslContext
            .selectFrom(a)
            .where(a.ID.eq(authorId.toString()))
            .fetchOne()
            ?.let(::toEntity)

    override fun findByIds(authorIds: List<UUID>): List<AuthorEntity> {
        if (authorIds.isEmpty()) return emptyList()

        return dslContext
            .selectFrom(a)
            .where(a.ID.`in`(authorIds.map(UUID::toString)))
            .fetch()
            .map(::toEntity)
    }

    override fun update(author: AuthorEntity): AuthorEntity {
        val updatedRows =
            dslContext
                .update(a)
                .set(a.NAME, author.name)
                .set(a.BIRTH_DATE, author.dateOfBirth)
                .where(a.ID.eq(author.id.toString()))
                .execute()

        if (updatedRows == 0) {
            throw NotFoundException(
                userMessage = "指定された著者は存在しません。",
                message = "著者ID: ${author.id}は存在しません",
            )
        }

        return author
    }

    /**
     * JOOQの著者レコードを著者エンティティに変換するヘルパーメソッド
     *
     * @param record 著者レコード
     * @return 著者エンティティ
     */
    private fun toEntity(record: AuthorsRecord): AuthorEntity =
        try {
            AuthorEntity(
                id = UUID.fromString(record.id),
                name = record.name,
                dateOfBirth = record.birthDate,
            )
        } catch (e: DomainValidationException) {
            throw InvalidStateException(
                message = "著者を復元できません。著者ID: ${record.id}",
                cause = e,
            )
        }
}
