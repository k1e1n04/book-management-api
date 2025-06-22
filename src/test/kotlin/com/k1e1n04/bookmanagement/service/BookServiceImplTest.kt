package com.k1e1n04.bookmanagement.service

import com.k1e1n04.bookmanagement.exception.DomainValidationException
import com.k1e1n04.bookmanagement.exception.NotFoundException
import com.k1e1n04.bookmanagement.model.AuthorEntity
import com.k1e1n04.bookmanagement.model.BookEntity
import com.k1e1n04.bookmanagement.model.PublicationStatus
import com.k1e1n04.bookmanagement.repository.AuthorRepository
import com.k1e1n04.bookmanagement.repository.BookRepository
import com.k1e1n04.bookmanagement.request.BookRegisterRequest
import com.k1e1n04.bookmanagement.request.BookUpdateRequest
import com.k1e1n04.bookmanagement.request.PublicationStatusRequest
import com.k1e1n04.bookmanagement.response.BookResponse
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import java.time.LocalDate
import java.util.UUID
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

/**
 * 書籍サービスのテストクラス
 */
@ExtendWith(MockKExtension::class)
class BookServiceImplTest {

    @MockK
    private lateinit var bookRepository: BookRepository

    @MockK
    private lateinit var authorRepository: AuthorRepository

    @InjectMockKs
    private lateinit var bookService: BookServiceImpl

    @Test
    fun `test getAllBooks`() {
        val bookId1 = UUID.randomUUID()
        val bookId2 = UUID.randomUUID()
        val authorId1 = UUID.randomUUID()
        val authorId2 = UUID.randomUUID()
        val authorId3 = UUID.randomUUID()

        val expected = listOf(
            BookResponse(
                id = bookId1.toString(),
                title = "Book 1",
                price = 1000,
                authorIds = listOf(authorId1.toString(), authorId2.toString()),
                status = PublicationStatus.PUBLISHED.name
            ),
            BookResponse(
                id = bookId2.toString(),
                title = "Book 2",
                price = 1500,
                authorIds = listOf(authorId3.toString()),
                status = PublicationStatus.UNPUBLISHED.name
            )
        )

        every {
            bookRepository.findAll()
        } returns listOf(
            BookEntity(
                id = bookId1,
                title = "Book 1",
                price = 1000,
                authorIds = listOf(authorId1, authorId2),
                status = PublicationStatus.PUBLISHED
            ),
            BookEntity(
                id = bookId2,
                title = "Book 2",
                price = 1500,
                authorIds = listOf(authorId3),
                status = PublicationStatus.UNPUBLISHED
            )
        )

        val actual = bookService.getAllBooks()
        assert(actual.size == 2)
        assert(actual.containsAll(expected))
    }

    @Test
    fun `test getAllBooks with empty list`() {
        every { bookRepository.findAll() } returns emptyList()

        val actual = bookService.getAllBooks()
        assert(actual.isEmpty())
    }

    @Test
    fun `test getBookByAuthor`() {
        val authorId = UUID.randomUUID()
        val bookId1 = UUID.randomUUID()
        val bookId2 = UUID.randomUUID()

        val expected = listOf(
            BookResponse(
                id = bookId1.toString(),
                title = "Book by Author",
                price = 1200,
                authorIds = listOf(authorId.toString()),
                status = PublicationStatus.PUBLISHED.name
            ),
            BookResponse(
                id = bookId2.toString(),
                title = "Another Book by Author",
                price = 900,
                authorIds = listOf(authorId.toString()),
                status = PublicationStatus.UNPUBLISHED.name
            )
        )

        every {
            bookRepository.findByAuthorId(authorId)
        } returns listOf(
            BookEntity(
                id = bookId1,
                title = "Book by Author",
                price = 1200,
                authorIds = listOf(authorId),
                status = PublicationStatus.PUBLISHED
            ),
            BookEntity(
                id = bookId2,
                title = "Another Book by Author",
                price = 900,
                authorIds = listOf(authorId),
                status = PublicationStatus.UNPUBLISHED
            )
        )

        val actual = bookService.getBooksByAuthor(authorId.toString())
        assert(actual.size == 2)
        assert(actual.containsAll(expected))
    }

    @Test
    fun `test getBookByAuthor with empty list`() {
        val authorId = UUID.randomUUID()
        every { bookRepository.findByAuthorId(authorId) } returns emptyList()

        val actual = bookService.getBooksByAuthor(authorId.toString())
        assert(actual.isEmpty())
    }

    @Test
    fun `test registerBook`() {
        val authorId1 = UUID.randomUUID()
        val authorId2 = UUID.randomUUID()

        val request = BookRegisterRequest(
            title = "New Book",
            price = 2000,
            authorIds = listOf(authorId1.toString(), authorId2.toString()),
            status = PublicationStatusRequest.UNPUBLISHED
        )

        every {
            authorRepository.findByIds(any())
        } returns listOf(
            AuthorEntity(
                id = authorId1,
                name = "Author 1",
                dateOfBirth = LocalDate.of(1980, 1, 1)
            ),
            AuthorEntity(
                id = authorId2,
                name = "Author 2",
                dateOfBirth = LocalDate.of(1990, 5, 20)
            )
        )

        every { bookRepository.save(any()) } answers {
            it.invocation.args[0] as BookEntity
        }

        val actual = bookService.registerBook(request)
        assert(actual.title == request.title)
        assert(actual.price == request.price)
        assert(actual.authorIds.size == request.authorIds.size)
        assert(actual.status == PublicationStatus.UNPUBLISHED.name)
    }

    @Test
    fun `test registerBook with invalid author IDs`() {
        val request = BookRegisterRequest(
            title = "Invalid Book",
            price = 2000,
            authorIds = listOf("invalid-uuid", "another-invalid-uuid"),
            status = PublicationStatusRequest.UNPUBLISHED
        )

        every { authorRepository.findByIds(any()) } returns emptyList()

        assertThatThrownBy {
            bookService.registerBook(request)
        }.isInstanceOf(DomainValidationException::class.java)
            .hasMessageContaining("著者IDの形式が不正です。")
            .hasFieldOrPropertyWithValue("userMessage", "著者IDの形式が不正です。")
    }

    @Test
    fun `test registerBook with non-existing authors`() {
        val authorId1 = UUID.randomUUID()
        val authorId2 = UUID.randomUUID()

        val request = BookRegisterRequest(
            title = "Non-existing Authors Book",
            price = 2500,
            authorIds = listOf(authorId1.toString(), authorId2.toString()),
            status = PublicationStatusRequest.UNPUBLISHED
        )

        every {
            authorRepository.findByIds(any())
        } returns emptyList()

        assertThatThrownBy {
            bookService.registerBook(request)
        }.isInstanceOf(DomainValidationException::class.java)
            .hasMessageContaining("著者ID: ${authorId1}, $authorId2 の一部が存在しません。")
            .hasFieldOrPropertyWithValue("userMessage", "指定された著者の一部が存在しません。")
    }

    @Test
    fun `test updateBook`() {
        val bookId = UUID.randomUUID()
        val authorId1 = UUID.randomUUID()
        val authorId2 = UUID.randomUUID()

        val existingBook = BookEntity(
            id = bookId,
            title = "Existing Book",
            price = 3000,
            authorIds = listOf(authorId1, authorId2),
            status = PublicationStatus.PUBLISHED
        )

        every { bookRepository.findById(bookId) } returns existingBook

        val request = BookUpdateRequest(
            title = "Updated Book",
            price = 3500,
            authorIds = listOf(authorId1.toString(), authorId2.toString()),
            status = PublicationStatusRequest.PUBLISHED
        )

        every {
            authorRepository.findByIds(any())
        } returns listOf(
            AuthorEntity(
                id = authorId1,
                name = "Author 1",
                dateOfBirth = LocalDate.of(1980, 1, 1)
            ),
            AuthorEntity(
                id = authorId2,
                name = "Author 2",
                dateOfBirth = LocalDate.of(1990, 5, 20)
            )
        )

        every { bookRepository.update(any()) } answers {
            it.invocation.args[0] as BookEntity
        }

        val actual = bookService.updateBook(bookId.toString(), request)
        assert(actual.title == request.title)
        assert(actual.price == request.price)
        assert(actual.authorIds.size == request.authorIds.size)
        assert(actual.status == PublicationStatus.PUBLISHED.name)
    }

    @Test
    fun `test updateBook with invalid book ID format`() {
        val invalidBookId = "invalid-uuid"
        val request = BookUpdateRequest(
            title = "Invalid Book ID",
            price = 3000,
            authorIds = listOf("valid-uuid"),
            status = PublicationStatusRequest.PUBLISHED
        )

        assertThatThrownBy {
            bookService.updateBook(invalidBookId, request)
        }.isInstanceOf(NotFoundException::class.java)
            .hasMessageContaining("書籍IDの形式が不正です: $invalidBookId")
            .hasFieldOrPropertyWithValue("userMessage", "指定された書籍は存在しません。")
    }

    @Test
    fun `test updateBook with non-existing book`() {
        val bookId = UUID.randomUUID()
        val request = BookUpdateRequest(
            title = "Non-existing Book",
            price = 4000,
            authorIds = listOf("invalid-uuid"),
            status = PublicationStatusRequest.UNPUBLISHED
        )

        every { bookRepository.findById(bookId) } returns null

        assertThatThrownBy {
            bookService.updateBook(bookId.toString(), request)
        }.isInstanceOf(NotFoundException::class.java)
            .hasMessageContaining("書籍ID: $bookId は存在しません。")
            .hasFieldOrPropertyWithValue("userMessage", "指定された書籍は存在しません。")
    }

    @Test
    fun `test updateBook with invalid author IDs`() {
        val bookId = UUID.randomUUID()
        val request = BookUpdateRequest(
            title = "Invalid Author IDs Book",
            price = 4500,
            authorIds = listOf("invalid-uuid", "another-invalid-uuid"),
            status = PublicationStatusRequest.UNPUBLISHED
        )

        every { bookRepository.findById(bookId) } returns BookEntity(
            id = bookId,
            title = "Existing Book",
            price = 3000,
            authorIds = listOf(UUID.randomUUID()),
            status = PublicationStatus.PUBLISHED
        )

        assertThatThrownBy {
            bookService.updateBook(bookId.toString(), request)
        }.isInstanceOf(DomainValidationException::class.java)
            .hasMessageContaining("著者IDの形式が不正です。")
            .hasFieldOrPropertyWithValue("userMessage", "著者IDの形式が不正です。")
    }

    @Test
    fun `test updateBook with non-existing authors`() {
        val bookId = UUID.randomUUID()
        val authorId1 = UUID.randomUUID()
        val authorId2 = UUID.randomUUID()

        val request = BookUpdateRequest(
            title = "Non-existing Authors Update",
            price = 5000,
            authorIds = listOf(authorId1.toString(), authorId2.toString()),
            status = PublicationStatusRequest.UNPUBLISHED
        )

        every { bookRepository.findById(bookId) } returns BookEntity(
            id = bookId,
            title = "Existing Book",
            price = 3000,
            authorIds = listOf(UUID.randomUUID()),
            status = PublicationStatus.PUBLISHED
        )

        every {
            authorRepository.findByIds(any())
        } returns emptyList()

        assertThatThrownBy {
            bookService.updateBook(bookId.toString(), request)
        }.isInstanceOf(DomainValidationException::class.java)
            .hasMessageContaining("著者ID: ${authorId1}, $authorId2 の一部が存在しません。")
            .hasFieldOrPropertyWithValue("userMessage", "指定された著者の一部が存在しません。")
    }

    @Test
    fun `test updateBook throws DomainValidationException when trying to unpublish a published book`() {
        val bookId = UUID.randomUUID()
        val authorId1 = UUID.randomUUID()
        val authorId2 = UUID.randomUUID()

        val existingBook = BookEntity(
            id = bookId,
            title = "Published Book",
            price = 3000,
            authorIds = listOf(authorId1, authorId2),
            status = PublicationStatus.PUBLISHED
        )

        every { authorRepository.findByIds(any()) } returns listOf(
            AuthorEntity(
                id = authorId1,
                name = "Author 1",
                dateOfBirth = LocalDate.of(1980, 1, 1)
            ),
            AuthorEntity(
                id = authorId2,
                name = "Author 2",
                dateOfBirth = LocalDate.of(1990, 5, 20)
            )
        )

        every { bookRepository.findById(bookId) } returns existingBook

        val request = BookUpdateRequest(
            title = "Trying to Unpublish",
            price = 3500,
            authorIds = listOf(authorId1.toString(), authorId2.toString()),
            status = PublicationStatusRequest.UNPUBLISHED
        )

        assertThatThrownBy {
            bookService.updateBook(bookId.toString(), request)
        }.isInstanceOf(DomainValidationException::class.java)
            .hasMessageContaining("出版済みの書籍を非公開にしようとしました。id: $bookId")
            .hasFieldOrPropertyWithValue("userMessage", "出版済みの書籍を非公開にすることはできません。")
    }

}
