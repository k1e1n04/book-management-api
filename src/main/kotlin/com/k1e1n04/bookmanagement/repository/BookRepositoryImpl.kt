package com.k1e1n04.bookmanagement.repository

import com.k1e1n04.bookmanagement.exception.InvalidStateException
import com.k1e1n04.bookmanagement.exception.NotFoundException
import com.k1e1n04.bookmanagement.jooq.enums.PublicationStatus
import com.k1e1n04.bookmanagement.jooq.tables.BookAuthors
import com.k1e1n04.bookmanagement.jooq.tables.Books
import com.k1e1n04.bookmanagement.model.BookEntity
import com.k1e1n04.bookmanagement.model.PublicationStatus as DomainPublicationStatus
import java.util.UUID
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

/**
 * 書籍リポジトリの実装クラス
 */
@Repository
class BookRepositoryImpl(
    private val dslContext: DSLContext,
) : BookRepository {
    private val b = Books.BOOKS.`as`("b")
    private val ba = BookAuthors.BOOK_AUTHORS.`as`("ba")

    override fun save(book: BookEntity): BookEntity {
        dslContext
            .newRecord(b)
            .apply {
                id = book.id.toString()
                title = book.title
                price = book.price
                publicationStatus = toPublicationStatus(book.status)
            }.store()

        storeBookAuthors(book.id, book.authorIds)

        return book
    }

    override fun findById(id: UUID) = findBooksByCondition(b.ID.eq(id.toString())).firstOrNull()

    override fun findAll(): List<BookEntity> = findBooksByCondition(null)

    override fun findByAuthorId(authorId: UUID): List<BookEntity> {
        val bookIds =
            dslContext
                .select(ba.BOOK_ID)
                .from(ba)
                .where(ba.AUTHOR_ID.eq(authorId.toString()))
                .fetchSet(ba.BOOK_ID)

        if (bookIds.isEmpty()) return emptyList()

        return findBooksByCondition(b.ID.`in`(bookIds))
    }

    override fun update(book: BookEntity): BookEntity {
        val updated =
            dslContext
                .update(b)
                .set(b.TITLE, book.title)
                .set(b.PRICE, book.price)
                .set(b.PUBLICATION_STATUS, toPublicationStatus(book.status))
                .where(b.ID.eq(book.id.toString()))
                .execute()

        if (updated == 0) {
            throw NotFoundException(
                message = "書籍ID: ${book.id} は存在しません。",
                userMessage = "指定された書籍は存在しません。",
            )
        }

        dslContext
            .deleteFrom(ba)
            .where(ba.BOOK_ID.eq(book.id.toString()))
            .execute()

        storeBookAuthors(book.id, book.authorIds)
        return book
    }

    /**
     * 書籍を検索条件に基づいて取得するヘルパーメソッド
     *
     * @param condition 検索条件 (nullの場合は全件検索)
     * @return 書籍エンティティのリスト
     */
    private fun findBooksByCondition(condition: org.jooq.Condition?): List<BookEntity> {
        val bookRecords =
            dslContext
                .selectFrom(b)
                .apply { condition?.let { where(it) } }
                .fetch()

        if (bookRecords.isEmpty()) {
            return emptyList()
        }

        val bookIds = bookRecords.map { it.id }

        val authorIdsByBookId =
            dslContext
                .select(ba.BOOK_ID, ba.AUTHOR_ID)
                .from(ba)
                .where(ba.BOOK_ID.`in`(bookIds))
                .fetchGroups(
                    { it.get(ba.BOOK_ID) },
                    { it.get(ba.AUTHOR_ID) },
                )

        return bookRecords.map { bookRecord ->
            try {
                val bookUuid = UUID.fromString(bookRecord.id)
                BookEntity(
                    id = bookUuid,
                    title = bookRecord.title,
                    price = bookRecord.price,
                    authorIds = authorIdsByBookId[bookRecord.id].orEmpty().map(UUID::fromString),
                    status = toDomainPublicationStatus(bookRecord.publicationStatus),
                )
            } catch (e: Exception) {
                throw InvalidStateException(
                    message = "書籍を復元できません。書籍ID: ${bookRecord.id}",
                    cause = e,
                )
            }
        }
    }

    /**
     * 書籍と著者の関連を保存するヘルパーメソッド
     * 著者IDのリストが空の場合は何もしない
     *
     * @param bookId 書籍ID
     * @param authorIds 著者IDのリスト
     */
    private fun storeBookAuthors(
        bookId: UUID,
        authorIds: List<UUID>,
    ) {
        if (authorIds.isEmpty()) return

        val records =
            authorIds.map { authorId ->
                dslContext.newRecord(BookAuthors.BOOK_AUTHORS).apply {
                    this.bookId = bookId.toString()
                    this.authorId = authorId.toString()
                }
            }
        dslContext.batchStore(records).execute()
    }

    /**
     * ドメインの公開ステータスをJOOQのPublicationStatusに変換するヘルパー関数
     */
    private fun toPublicationStatus(status: DomainPublicationStatus): PublicationStatus =
        PublicationStatus.valueOf(status.name)

    /**
     * JOOQのPublicationStatusをドメインの公開ステータスに変換するヘルパー関数
     */
    private fun toDomainPublicationStatus(status: PublicationStatus): DomainPublicationStatus =
        DomainPublicationStatus.valueOf(status.name)
}
