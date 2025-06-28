package com.k1e1n04.bookmanagement.service

import com.k1e1n04.bookmanagement.exception.DomainValidationException
import com.k1e1n04.bookmanagement.exception.NotFoundException
import com.k1e1n04.bookmanagement.model.AuthorEntity
import com.k1e1n04.bookmanagement.repository.AuthorRepository
import com.k1e1n04.bookmanagement.request.AuthorRegisterRequest
import com.k1e1n04.bookmanagement.request.AuthorUpdateRequest
import com.k1e1n04.bookmanagement.response.AuthorResponse
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
 * 著者サービスのテストクラス
 */
@ExtendWith(MockitoExtension::class)
class AuthorServiceImplTest {
    @Mock
    private lateinit var authorRepository: AuthorRepository

    @InjectMocks
    private lateinit var authorService: AuthorServiceImpl

    companion object {
        private val AUTHOR_ID_1: UUID = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa")
        private val AUTHOR_ID_2: UUID = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb")
    }

    @Test
    fun `test getAllAuthors`() {
        val expected =
            listOf(
                AuthorResponse(
                    id = AUTHOR_ID_1.toString(),
                    name = "Author 1",
                    dateOfBirth = LocalDate.of(1990, 1, 1).toString(),
                ),
                AuthorResponse(
                    id = AUTHOR_ID_2.toString(),
                    name = "Author 2",
                    dateOfBirth = LocalDate.of(1985, 5, 20).toString(),
                ),
            )

        whenever(authorRepository.findAll()).thenReturn(
            listOf(
                AuthorEntity(
                    id = AUTHOR_ID_1,
                    name = "Author 1",
                    dateOfBirth = LocalDate.of(1990, 1, 1),
                ),
                AuthorEntity(
                    id = AUTHOR_ID_2,
                    name = "Author 2",
                    dateOfBirth = LocalDate.of(1985, 5, 20),
                ),
            ),
        )

        val actual = authorService.getAllAuthors()

        assertThat(actual).hasSize(2)
        assertThat(actual).containsExactlyInAnyOrderElementsOf(expected)
    }

    @Test
    fun `test getAllAuthors with empty list`() {
        whenever(authorRepository.findAll()).thenReturn(emptyList())

        val actual = authorService.getAllAuthors()

        assertThat(actual).isEmpty()
    }

    @Test
    fun `test registerAuthor`() {
        val request =
            AuthorRegisterRequest(
                name = "New Author",
                dateOfBirth = LocalDate.of(1990, 1, 1),
            )

        whenever(authorRepository.save(any())).thenAnswer {
            it.arguments[0] as AuthorEntity
        }

        val actual = authorService.registerAuthor(request)

        assertThat(actual.name).isEqualTo(request.name)
        assertThat(actual.dateOfBirth).isEqualTo(request.dateOfBirth.toString())
    }

    @Test
    fun `test registerAuthor throws DomainValidationException when generate invalid Entity`() {
        val request =
            AuthorRegisterRequest(
                name = "",
                dateOfBirth = LocalDate.of(1990, 1, 1),
            )

        assertThatThrownBy { authorService.registerAuthor(request) }
            .isInstanceOf(DomainValidationException::class.java)
            .hasMessage("著者の名前は1文字以上、255文字以下でなければなりません。name: ")
            .extracting("userMessage")
            .isEqualTo("名前は1文字以上、255文字以下でなければなりません。")
    }

    @Test
    fun `test updateAuthor`() {
        val request =
            AuthorUpdateRequest(
                name = "Updated Author",
                dateOfBirth = LocalDate.of(1990, 1, 1),
            )

        whenever(authorRepository.findById(AUTHOR_ID_1)).thenReturn(
            AuthorEntity(
                id = AUTHOR_ID_1,
                name = "Old Author",
                dateOfBirth = LocalDate.of(1985, 5, 20),
            ),
        )
        whenever(authorRepository.update(any())).thenAnswer {
            it.arguments[0] as AuthorEntity
        }

        val actual = authorService.updateAuthor(AUTHOR_ID_1.toString(), request)

        assertThat(actual.id).isEqualTo(AUTHOR_ID_1.toString())
        assertThat(actual.name).isEqualTo(request.name)
        assertThat(actual.dateOfBirth).isEqualTo(request.dateOfBirth.toString())
    }

    @Test
    fun `test updateAuthor throws NotFoundException when author does not exist`() {
        val request =
            AuthorUpdateRequest(
                name = "Updated Author",
                dateOfBirth = LocalDate.of(1990, 1, 1),
            )

        whenever(authorRepository.findById(AUTHOR_ID_1)).thenReturn(null)

        assertThatThrownBy { authorService.updateAuthor(AUTHOR_ID_1.toString(), request) }
            .isInstanceOf(NotFoundException::class.java)
            .hasMessage("著者ID: $AUTHOR_ID_1 は存在しません")
            .extracting("userMessage")
            .isEqualTo("指定された著者は存在しません。")
    }

    @Test
    fun `test updateAuthor throws NotFoundException when authorId format is invalid`() {
        val invalidAuthorId = "invalid-uuid"
        val request =
            AuthorUpdateRequest(
                name = "Updated Author",
                dateOfBirth = LocalDate.of(1990, 1, 1),
            )

        assertThatThrownBy { authorService.updateAuthor(invalidAuthorId, request) }
            .isInstanceOf(NotFoundException::class.java)
            .hasMessage("著者IDの形式が不正です: $invalidAuthorId")
            .extracting("userMessage")
            .isEqualTo("指定された著者は存在しません。")
    }

    @Test
    fun `test updateAuthor throws DomainValidationException when generate invalid Entity`() {
        val request =
            AuthorUpdateRequest(
                name = "",
                dateOfBirth = LocalDate.of(1990, 1, 1),
            )

        whenever(authorRepository.findById(AUTHOR_ID_1)).thenReturn(
            AuthorEntity(
                id = AUTHOR_ID_1,
                name = "Old Author",
                dateOfBirth = LocalDate.of(1985, 5, 20),
            ),
        )

        assertThatThrownBy { authorService.updateAuthor(AUTHOR_ID_1.toString(), request) }
            .isInstanceOf(DomainValidationException::class.java)
            .hasMessage("著者の名前は1文字以上、255文字以下でなければなりません。name: ")
            .extracting("userMessage")
            .isEqualTo("名前は1文字以上、255文字以下でなければなりません。")
    }
}
