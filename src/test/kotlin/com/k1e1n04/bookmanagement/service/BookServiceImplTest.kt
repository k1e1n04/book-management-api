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
import com.k1e1n04.bookmanagement.response.PublicationStatusResponse
import java.time.LocalDate
import java.util.UUID
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever

/**
 * 書籍サービスのテストクラス
 */
@ExtendWith(MockitoExtension::class)
class BookServiceImplTest {
    @Mock
    private lateinit var bookRepository: BookRepository

    @Mock
    private lateinit var authorRepository: AuthorRepository

    @InjectMocks
    private lateinit var bookService: BookServiceImpl

    companion object {
        private val BOOK_ID_1 = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa")
        private val BOOK_ID_2 = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb")
        private val AUTHOR_ID_1 = UUID.fromString("cccccccc-cccc-cccc-cccc-cccccccccccc")
        private val AUTHOR_ID_2 = UUID.fromString("dddddddd-dddd-dddd-dddd-dddddddddddd")
        private val AUTHOR_ID_3 = UUID.fromString("eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee")
    }

    @Test
    fun `test getAllBooks`() {
        val expected =
            listOf(
                BookResponse(
                    id = BOOK_ID_1.toString(),
                    title = "Book 1",
                    price = 1000,
                    authorIds = listOf(AUTHOR_ID_1.toString(), AUTHOR_ID_2.toString()),
                    status = PublicationStatusResponse.PUBLISHED,
                ),
                BookResponse(
                    id = BOOK_ID_2.toString(),
                    title = "Book 2",
                    price = 1500,
                    authorIds = listOf(AUTHOR_ID_3.toString()),
                    status = PublicationStatusResponse.UNPUBLISHED,
                ),
            )

        whenever(bookRepository.findAll()).thenReturn(
            listOf(
                BookEntity(
                    id = BOOK_ID_1,
                    title = "Book 1",
                    price = 1000,
                    authorIds = listOf(AUTHOR_ID_1, AUTHOR_ID_2),
                    status = PublicationStatus.PUBLISHED,
                ),
                BookEntity(
                    id = BOOK_ID_2,
                    title = "Book 2",
                    price = 1500,
                    authorIds = listOf(AUTHOR_ID_3),
                    status = PublicationStatus.UNPUBLISHED,
                ),
            ),
        )

        val actual = bookService.getAllBooks()
        assertThat(actual).hasSize(2)
        assertThat(actual).containsExactlyInAnyOrderElementsOf(expected)
    }

    @Test
    fun `test getAllBooks with empty list`() {
        whenever(bookRepository.findAll()).thenReturn(emptyList())

        val actual = bookService.getAllBooks()
        assertThat(actual).isEmpty()
    }

    @Test
    fun `test getBooksByAuthor`() {
        val expected =
            listOf(
                BookResponse(
                    id = BOOK_ID_1.toString(),
                    title = "Book by Author",
                    price = 1200,
                    authorIds = listOf(AUTHOR_ID_1.toString()),
                    status = PublicationStatusResponse.PUBLISHED
                ),
                BookResponse(
                    id = BOOK_ID_2.toString(),
                    title = "Another Book by Author",
                    price = 900,
                    authorIds = listOf(AUTHOR_ID_1.toString()),
                    status = PublicationStatusResponse.UNPUBLISHED
                ),
            )

        whenever(bookRepository.findByAuthorId(AUTHOR_ID_1)).thenReturn(
            listOf(
                BookEntity(
                    id = BOOK_ID_1,
                    title = "Book by Author",
                    price = 1200,
                    authorIds = listOf(AUTHOR_ID_1),
                    status = PublicationStatus.PUBLISHED,
                ),
                BookEntity(
                    id = BOOK_ID_2,
                    title = "Another Book by Author",
                    price = 900,
                    authorIds = listOf(AUTHOR_ID_1),
                    status = PublicationStatus.UNPUBLISHED,
                ),
            ),
        )

        val actual = bookService.getBooksByAuthor(AUTHOR_ID_1.toString())
        assertThat(actual).hasSize(2)
        assertThat(actual).containsExactlyInAnyOrderElementsOf(expected)
    }

    @Test
    fun `test getBooksByAuthor with empty list`() {
        whenever(bookRepository.findByAuthorId(AUTHOR_ID_1)).thenReturn(emptyList())

        val actual = bookService.getBooksByAuthor(AUTHOR_ID_1.toString())
        assertThat(actual).isEmpty()
    }

    @Test
    fun `test registerBook`() {
        val request =
            BookRegisterRequest(
                title = "New Book",
                price = 2000,
                authorIds = listOf(AUTHOR_ID_1.toString(), AUTHOR_ID_2.toString()),
                status = PublicationStatusRequest.UNPUBLISHED,
            )

        whenever(authorRepository.findByIds(any())).thenReturn(
            listOf(
                AuthorEntity(
                    id = AUTHOR_ID_1,
                    name = "Author 1",
                    dateOfBirth = LocalDate.of(1980, 1, 1),
                ),
                AuthorEntity(
                    id = AUTHOR_ID_2,
                    name = "Author 2",
                    dateOfBirth = LocalDate.of(1990, 5, 20),
                ),
            ),
        )

        whenever(bookRepository.save(any())).thenAnswer {
            it.arguments[0] as BookEntity
        }

        val actual = bookService.registerBook(request)
        assertThat(actual.title).isEqualTo(request.title)
        assertThat(actual.price).isEqualTo(request.price)
        assertThat(actual.authorIds).hasSize(request.authorIds.size)
        assertThat(actual.status).isEqualTo(PublicationStatusResponse.UNPUBLISHED)
    }

    @Test
    fun `test registerBook with invalid author IDs`() {
        val request =
            BookRegisterRequest(
                title = "Invalid Book",
                price = 2000,
                authorIds = listOf("invalid-uuid", "another-invalid-uuid"),
                status = PublicationStatusRequest.UNPUBLISHED,
            )

        assertThatThrownBy { bookService.registerBook(request) }
            .isInstanceOf(DomainValidationException::class.java)
            .hasMessageContaining("著者IDの形式が不正です。")
            .extracting("userMessage")
            .isEqualTo("著者IDの形式が不正です。")
    }

    @Test
    fun `test registerBook with non-existing authors`() {
        val request =
            BookRegisterRequest(
                title = "Non-existing Authors Book",
                price = 2500,
                authorIds = listOf(AUTHOR_ID_1.toString(), AUTHOR_ID_2.toString()),
                status = PublicationStatusRequest.UNPUBLISHED,
            )

        whenever(authorRepository.findByIds(any())).thenReturn(emptyList())

        assertThatThrownBy { bookService.registerBook(request) }
            .isInstanceOf(DomainValidationException::class.java)
            .hasMessageContaining("著者ID: ${AUTHOR_ID_1}, $AUTHOR_ID_2 の一部が存在しません。")
            .extracting("userMessage")
            .isEqualTo("指定された著者の一部が存在しません。")
    }

    @Test
    fun `test updateBook`() {
        val existingBook =
            BookEntity(
                id = BOOK_ID_1,
                title = "Existing Book",
                price = 3000,
                authorIds = listOf(AUTHOR_ID_1, AUTHOR_ID_2),
                status = PublicationStatus.PUBLISHED,
            )

        whenever(bookRepository.findById(BOOK_ID_1)).thenReturn(existingBook)

        val request =
            BookUpdateRequest(
                title = "Updated Book",
                price = 3500,
                authorIds = listOf(AUTHOR_ID_1.toString(), AUTHOR_ID_2.toString()),
                status = PublicationStatusRequest.PUBLISHED,
            )

        whenever(authorRepository.findByIds(any())).thenReturn(
            listOf(
                AuthorEntity(
                    id = AUTHOR_ID_1,
                    name = "Author 1",
                    dateOfBirth = LocalDate.of(1980, 1, 1),
                ),
                AuthorEntity(
                    id = AUTHOR_ID_2,
                    name = "Author 2",
                    dateOfBirth = LocalDate.of(1990, 5, 20),
                ),
            ),
        )

        whenever(bookRepository.update(any())).thenAnswer {
            it.arguments[0] as BookEntity
        }

        val actual = bookService.updateBook(BOOK_ID_1.toString(), request)
        assertThat(actual.title).isEqualTo(request.title)
        assertThat(actual.price).isEqualTo(request.price)
        assertThat(actual.authorIds).hasSameSizeAs(request.authorIds)
        assertThat(actual.status).isEqualTo(PublicationStatusResponse.PUBLISHED)
    }

    @Test
    fun `test updateBook with invalid book ID format`() {
        val invalidBookId = "invalid-uuid"
        val request =
            BookUpdateRequest(
                title = "Invalid Book ID",
                price = 3000,
                authorIds = listOf(AUTHOR_ID_1.toString()),
                status = PublicationStatusRequest.PUBLISHED,
            )

        assertThatThrownBy { bookService.updateBook(invalidBookId, request) }
            .isInstanceOf(NotFoundException::class.java)
            .hasMessage("書籍IDの形式が不正です: $invalidBookId")
            .extracting("userMessage")
            .isEqualTo("指定された書籍は存在しません。")
    }

    @Test
    fun `test updateBook with non-existing book`() {
        val request =
            BookUpdateRequest(
                title = "Non-existing Book",
                price = 4000,
                authorIds = listOf(AUTHOR_ID_1.toString()),
                status = PublicationStatusRequest.UNPUBLISHED,
            )

        whenever(bookRepository.findById(BOOK_ID_1)).thenReturn(null)

        assertThatThrownBy { bookService.updateBook(BOOK_ID_1.toString(), request) }
            .isInstanceOf(NotFoundException::class.java)
            .hasMessage("書籍ID: $BOOK_ID_1 は存在しません。")
            .extracting("userMessage")
            .isEqualTo("指定された書籍は存在しません。")
    }

    @Test
    fun `test updateBook with invalid author IDs`() {
        val request =
            BookUpdateRequest(
                title = "Invalid Author IDs Book",
                price = 4500,
                authorIds = listOf("invalid-uuid", "another-invalid-uuid"),
                status = PublicationStatusRequest.UNPUBLISHED,
            )

        whenever(bookRepository.findById(BOOK_ID_1)).thenReturn(
            BookEntity(
                id = BOOK_ID_1,
                title = "Existing Book",
                price = 3000,
                authorIds = listOf(UUID.randomUUID()),
                status = PublicationStatus.PUBLISHED,
            ),
        )

        assertThatThrownBy { bookService.updateBook(BOOK_ID_1.toString(), request) }
            .isInstanceOf(DomainValidationException::class.java)
            .hasMessageContaining("著者IDの形式が不正です。")
            .extracting("userMessage")
            .isEqualTo("著者IDの形式が不正です。")
    }

    @Test
    fun `test updateBook with non-existing authors`() {
        val request =
            BookUpdateRequest(
                title = "Non-existing Authors Update",
                price = 5000,
                authorIds = listOf(AUTHOR_ID_1.toString(), AUTHOR_ID_2.toString()),
                status = PublicationStatusRequest.UNPUBLISHED,
            )

        whenever(bookRepository.findById(BOOK_ID_1)).thenReturn(
            BookEntity(
                id = BOOK_ID_1,
                title = "Existing Book",
                price = 3000,
                authorIds = listOf(AUTHOR_ID_3),
                status = PublicationStatus.UNPUBLISHED,
            ),
        )

        whenever(authorRepository.findByIds(any())).thenReturn(emptyList())

        assertThatThrownBy { bookService.updateBook(BOOK_ID_1.toString(), request) }
            .isInstanceOf(DomainValidationException::class.java)
            .hasMessageContaining("著者ID: ${AUTHOR_ID_1}, $AUTHOR_ID_2 の一部が存在しません。")
            .extracting("userMessage")
            .isEqualTo("指定された著者の一部が存在しません。")
    }

    @Test
    fun `test updateBook throws DomainValidationException when trying to unpublish a published book`() {
        val existingBook =
            BookEntity(
                id = BOOK_ID_1,
                title = "Published Book",
                price = 3000,
                authorIds = listOf(AUTHOR_ID_1, AUTHOR_ID_2),
                status = PublicationStatus.PUBLISHED,
            )

        whenever(authorRepository.findByIds(any())).thenReturn(
            listOf(
                AuthorEntity(
                    id = AUTHOR_ID_1,
                    name = "Author 1",
                    dateOfBirth = LocalDate.of(1980, 1, 1),
                ),
                AuthorEntity(
                    id = AUTHOR_ID_2,
                    name = "Author 2",
                    dateOfBirth = LocalDate.of(1990, 5, 20),
                ),
            ),
        )

        whenever(bookRepository.findById(BOOK_ID_1)).thenReturn(existingBook)

        val request =
            BookUpdateRequest(
                title = "Trying to Unpublish",
                price = 3500,
                authorIds = listOf(AUTHOR_ID_1.toString(), AUTHOR_ID_2.toString()),
                status = PublicationStatusRequest.UNPUBLISHED,
            )

        assertThatThrownBy { bookService.updateBook(BOOK_ID_1.toString(), request) }
            .isInstanceOf(DomainValidationException::class.java)
            .hasMessage("出版済みの書籍を非公開にしようとしました。id: $BOOK_ID_1")
            .extracting("userMessage")
            .isEqualTo("出版済みの書籍を非公開にすることはできません。")
    }
}
