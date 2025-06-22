package com.k1e1n04.bookmanagement.repository

import com.k1e1n04.bookmanagement.exception.NotFoundException
import com.k1e1n04.bookmanagement.model.BookEntity
import com.k1e1n04.bookmanagement.model.PublicationStatus
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.jooq.DSLContext
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.test.context.jdbc.Sql
import java.util.UUID

/**
 * 書籍リポジトリのテストクラス
 */
@RepositoryTest
class BookRepositoryImplTest {

    @Autowired
    private lateinit var dslContext: DSLContext

    private lateinit var bookRepository: BookRepositoryImpl

    companion object {
        private val BOOK_ID_1 = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa")
        private val BOOK_ID_2 = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb")
        private val NON_EXISTENT_BOOK_ID_1 = UUID.fromString("cccccccc-cccc-cccc-cccc-cccccccccccc")
        private val NON_EXISTENT_BOOK_ID_2 = UUID.fromString("dddddddd-dddd-dddd-dddd-dddddddddddd")
        private val AUTHOR_ID_1 = UUID.fromString("11111111-1111-1111-1111-111111111111")
        private val AUTHOR_ID_2 = UUID.fromString("22222222-2222-2222-2222-222222222222")
        private val NON_EXISTENT_AUTHOR_ID_1 = UUID.fromString("33333333-3333-3333-3333-333333333333")
        private val NON_EXISTENT_AUTHOR_ID_2 = UUID.fromString("44444444-4444-4444-4444-444444444444")
    }

    @BeforeEach
    fun setup() {
        bookRepository = BookRepositoryImpl(dslContext)
    }

    @Test
    @Sql("/data/insert_authors.sql")
    @Sql("/data/insert_books.sql")
    fun `findAll returns all books when data exists`() {
        val books = bookRepository.findAll()
        assertThat(books).hasSize(2)
        assertThat(books).containsExactlyInAnyOrder(
            BookEntity(
                id = BOOK_ID_1,
                title = "吾輩は猫である",
                price = 1200,
                status = PublicationStatus.UNPUBLISHED,
                authorIds = listOf(AUTHOR_ID_1, AUTHOR_ID_2)
            ),
            BookEntity(
                id = BOOK_ID_2,
                title = "羅生門",
                price = 900,
                status = PublicationStatus.PUBLISHED,
                authorIds = listOf(AUTHOR_ID_2)
            )
        )
    }

    @Test
    fun `findAll returns empty list when no data exists`() {
        val books = bookRepository.findAll()
        assertThat(books).isEmpty()
    }

    @Test
    @Sql("/data/insert_authors.sql")
    @Sql("/data/insert_books.sql")
    fun `findById returns book when it exists`() {
        val book = bookRepository.findById(BOOK_ID_1)
        assertThat(book).isNotNull
        assertThat(book).isEqualTo(
            BookEntity(
                id = BOOK_ID_1,
                title = "吾輩は猫である",
                price = 1200,
                status = PublicationStatus.UNPUBLISHED,
                authorIds = listOf(AUTHOR_ID_1, AUTHOR_ID_2)
            )
        )
    }

    @Test
    fun `findById returns null when book does not exist`() {
        val book = bookRepository.findById(NON_EXISTENT_BOOK_ID_1)
        assertThat(book).isNull()
    }

    @Test
    @Sql("/data/insert_authors.sql")
    @Sql("/data/insert_books.sql")
    fun `save stores new book and returns it`() {
        val newBook = BookEntity(
            id = UUID.randomUUID(),
            title = "新しい書籍",
            price = 1500,
            status = PublicationStatus.PUBLISHED,
            authorIds = listOf(AUTHOR_ID_1)
        )

        val savedBook = bookRepository.save(newBook)

        assertThat(savedBook).isEqualTo(newBook)

        val foundBook = bookRepository.findById(savedBook.id)
        assertThat(foundBook).isNotNull
        assertThat(foundBook).isEqualTo(savedBook)
    }

    @Test
    @Sql("/data/insert_authors.sql")
    fun `save allows maximum 255 characters for title`() {
        val validTitle = "a".repeat(255)
        val newBook = BookEntity(
            id = UUID.randomUUID(),
            title = validTitle,
            price = 1000,
            status = PublicationStatus.UNPUBLISHED,
            authorIds = listOf(AUTHOR_ID_1)
        )

        val savedBook = bookRepository.save(newBook)

        assertThat(savedBook).isEqualTo(newBook)

        val foundBook = bookRepository.findById(savedBook.id)
        assertThat(foundBook).isNotNull
        assertThat(foundBook).isEqualTo(savedBook)
    }

    @Test
    @Sql("/data/insert_authors.sql")
    fun `save allows maximum 255 characters for title with multi-byte characters`() {
        val multiByteTitle = "あ".repeat(255)
        val newBook = BookEntity(
            id = UUID.randomUUID(),
            title = multiByteTitle,
            price = 1000,
            status = PublicationStatus.UNPUBLISHED,
            authorIds = listOf(AUTHOR_ID_1)
        )

        val savedBook = bookRepository.save(newBook)

        assertThat(savedBook).isEqualTo(newBook)

        val foundBook = bookRepository.findById(savedBook.id)
        assertThat(foundBook).isNotNull
        assertThat(foundBook).isEqualTo(savedBook)
    }

    @Test
    @Sql("/data/insert_authors.sql")
    fun `save allows maximum price`() {
        val maxPrice = Int.MAX_VALUE
        val newBook = BookEntity(
            id = UUID.randomUUID(),
            title = "高価格の書籍",
            price = maxPrice,
            status = PublicationStatus.UNPUBLISHED,
            authorIds = listOf(AUTHOR_ID_1)
        )

        val savedBook = bookRepository.save(newBook)

        assertThat(savedBook).isEqualTo(newBook)

        val foundBook = bookRepository.findById(savedBook.id)
        assertThat(foundBook).isNotNull
        assertThat(foundBook).isEqualTo(savedBook)
    }

    @Test
    fun `save throws DataIntegrityViolationException when book has non-existing author IDs`() {
        val invalidAuthorIds = listOf(
            NON_EXISTENT_AUTHOR_ID_1,
            NON_EXISTENT_AUTHOR_ID_2
        )
        val newBook = BookEntity(
            id = UUID.randomUUID(),
            title = "無効な著者IDの書籍",
            price = 1000,
            status = PublicationStatus.UNPUBLISHED,
            authorIds = invalidAuthorIds
        )

        assertThatThrownBy { bookRepository.save(newBook) }
            .isInstanceOf(DataIntegrityViolationException::class.java)
    }

    @Test
    @Sql("/data/insert_authors.sql")
    @Sql("/data/insert_books.sql")
    fun `update modifies existing book and returns it`() {
        val existingBook = bookRepository.findById(BOOK_ID_1)
        assertThat(existingBook).isNotNull
        val updatedBook = existingBook!!.update(
            title = "更新された書籍",
            price = 1300,
            authorIds = listOf(AUTHOR_ID_1.toString(), AUTHOR_ID_2.toString()),
            status = PublicationStatus.PUBLISHED
        )
        val savedBook = bookRepository.update(updatedBook)
        assertThat(savedBook).isEqualTo(updatedBook)
        val foundBook = bookRepository.findById(savedBook.id)
        assertThat(foundBook).isNotNull
        assertThat(foundBook).isEqualTo(savedBook)
    }

    @Test
    @Sql("/data/insert_authors.sql")
    @Sql("/data/insert_books.sql")
    fun `update throws NotFoundException when book does not exist`() {
        val nonExistentBook = BookEntity(
            id = NON_EXISTENT_BOOK_ID_1,
            title = "存在しない書籍",
            price = 1000,
            status = PublicationStatus.UNPUBLISHED,
            authorIds = listOf(AUTHOR_ID_1)
        )

        assertThatThrownBy { bookRepository.update(nonExistentBook) }
            .isInstanceOf(NotFoundException::class.java)
            .hasMessage("書籍ID: $NON_EXISTENT_BOOK_ID_1 は存在しません。")
            .extracting("userMessage")
            .isEqualTo("指定された書籍は存在しません。")
    }

    @Test
    @Sql("/data/insert_authors.sql")
    @Sql("/data/insert_books.sql")
    fun `update throws DataIntegrityViolationException when book has non-existing author IDs`() {
        val existingBook = bookRepository.findById(BOOK_ID_1)
        assertThat(existingBook).isNotNull
        val updatedBook = existingBook!!.update(
            title = "更新された書籍",
            price = 1300,
            authorIds = listOf(NON_EXISTENT_AUTHOR_ID_1.toString(), NON_EXISTENT_AUTHOR_ID_2.toString()),
            status = PublicationStatus.PUBLISHED
        )

        assertThatThrownBy { bookRepository.update(updatedBook) }
            .isInstanceOf(DataIntegrityViolationException::class.java)
    }

    @Test
    @Sql("/data/insert_authors.sql")
    @Sql("/data/insert_books.sql")
    fun `findByAuthorId returns books for given author ID`() {
        val books = bookRepository.findByAuthorId(AUTHOR_ID_1)
        assertThat(books).hasSize(1)
        assertThat(books).containsExactlyInAnyOrder(
            BookEntity(
                id = BOOK_ID_1,
                title = "吾輩は猫である",
                price = 1200,
                status = PublicationStatus.UNPUBLISHED,
                authorIds = listOf(AUTHOR_ID_1, AUTHOR_ID_2)
            )
        )
    }

    @Test
    @Sql("/data/insert_authors.sql")
    @Sql("/data/insert_books.sql")
    fun `findByAuthorId returns empty list when no books for given author ID`() {
        val books = bookRepository.findByAuthorId(NON_EXISTENT_BOOK_ID_2)
        assertThat(books).isEmpty()
    }
}
