package com.k1e1n04.bookmanagement.service

import com.k1e1n04.bookmanagement.exception.DomainValidationException
import com.k1e1n04.bookmanagement.exception.NotFoundException
import com.k1e1n04.bookmanagement.model.AuthorEntity
import com.k1e1n04.bookmanagement.repository.AuthorRepository
import com.k1e1n04.bookmanagement.request.AuthorRegisterRequest
import com.k1e1n04.bookmanagement.request.AuthorUpdateRequest
import com.k1e1n04.bookmanagement.response.AuthorResponse
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import java.time.LocalDate
import java.util.UUID
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

/**
 * 著者サービスのテストクラス
 */
@ExtendWith(MockKExtension::class)
class AuthorServiceImplTest  {

    @MockK
    private lateinit var authorRepository: AuthorRepository

    @InjectMockKs
    private lateinit var authorService: AuthorServiceImpl

    @Test
    fun `test getAllAuthors`() {
        val authorId1 = UUID.randomUUID()
        val authorId2 = UUID.randomUUID()

        val expected = listOf(
            AuthorResponse(
                id = authorId1.toString(),
                name = "Author 1",
                dateOfBirth = LocalDate.of(1990, 1, 1).toString()
            ),
            AuthorResponse(
                id = authorId2.toString(),
                name = "Author 2",
                dateOfBirth = LocalDate.of(1985, 5, 20).toString()
            )
        )

        every {
            authorRepository.findAll()
        } returns listOf(
            AuthorEntity(
                id = authorId1,
                name = "Author 1",
                dateOfBirth = LocalDate.of(1990, 1, 1)
            ),
            AuthorEntity(
                id = authorId2,
                name = "Author 2",
                dateOfBirth = LocalDate.of(1985, 5, 20)
            )
        )

        val actual = authorService.getAllAuthors()

        assertThat(actual).hasSize(2)
        assertThat(actual).containsExactlyInAnyOrderElementsOf(expected)
    }

    @Test
    fun `test getAllAuthors with empty list`() {
        every { authorRepository.findAll() } returns emptyList()

        val actual = authorService.getAllAuthors()

        assertThat(actual).isEmpty()
    }

    @Test
    fun `test registerAuthor`() {
        val request = AuthorRegisterRequest(
            name = "New Author",
            dateOfBirth = LocalDate.of(1990, 1, 1)
        )

        every { authorRepository.save(any()) } answers {
            it.invocation.args[0] as AuthorEntity
        }

        val actual = authorService.registerAuthor(request)

        assertThat(actual.name).isEqualTo(request.name)
        assertThat(actual.dateOfBirth).isEqualTo(request.dateOfBirth.toString())
    }

    @Test
    fun `test registerAuthor throws DomainValidationException when generate invalid Entity`() {
        val request = AuthorRegisterRequest(
            name = "",
            dateOfBirth = LocalDate.of(1990, 1, 1)
        )

        assertThatThrownBy { authorService.registerAuthor(request) }
            .isInstanceOf(DomainValidationException::class.java)
            .hasMessage("著者の名前は1文字以上、255文字以下でなければなりません。name: ")
            .extracting("userMessage")
            .isEqualTo("名前は1文字以上、255文字以下でなければなりません。")
    }

    @Test
    fun `test updateAuthor`() {
        val authorId = UUID.randomUUID()
        val request = AuthorUpdateRequest(
            name = "Updated Author",
            dateOfBirth = LocalDate.of(1990, 1, 1)
        )

        every { authorRepository.findById(authorId) } returns AuthorEntity(
            id = authorId,
            name = "Old Author",
            dateOfBirth = LocalDate.of(1985, 5, 20)
        )
        every { authorRepository.update(any()) } answers {
            it.invocation.args[0] as AuthorEntity
        }

        val actual = authorService.updateAuthor(authorId.toString(), request)

        assertThat(actual.id).isEqualTo(authorId.toString())
        assertThat(actual.name).isEqualTo(request.name)
        assertThat(actual.dateOfBirth).isEqualTo(request.dateOfBirth.toString())
    }

    @Test
    fun `test updateAuthor throws NotFoundException when author does not exist`() {
        val authorId = UUID.randomUUID()
        val request = AuthorUpdateRequest(
            name = "Updated Author",
            dateOfBirth = LocalDate.of(1990, 1, 1)
        )

        every { authorRepository.findById(authorId) } returns null

        assertThatThrownBy { authorService.updateAuthor(authorId.toString(), request) }
            .isInstanceOf(NotFoundException::class.java)
            .hasMessage("著者ID: $authorId は存在しません")
            .extracting("userMessage")
            .isEqualTo("指定された著者は存在しません")
    }

    @Test
    fun `test updateAuthor throws NotFoundException when authorId format is invalid`() {
        val invalidAuthorId = "invalid-uuid"
        val request = AuthorUpdateRequest(
            name = "Updated Author",
            dateOfBirth = LocalDate.of(1990, 1, 1)
        )

        assertThatThrownBy { authorService.updateAuthor(invalidAuthorId, request) }
            .isInstanceOf(NotFoundException::class.java)
            .hasMessage("著者IDの形式が不正です: $invalidAuthorId")
            .extracting("userMessage")
            .isEqualTo("指定された著者は存在しません")
    }

    @Test
    fun `test updateAuthor throws DomainValidationException when generate invalid Entity`() {
        val authorId = UUID.randomUUID()
        val request = AuthorUpdateRequest(
            name = "",
            dateOfBirth = LocalDate.of(1990, 1, 1)
        )

        every { authorRepository.findById(authorId) } returns AuthorEntity(
            id = authorId,
            name = "Old Author",
            dateOfBirth = LocalDate.of(1985, 5, 20)
        )

        assertThatThrownBy { authorService.updateAuthor(authorId.toString(), request) }
            .isInstanceOf(DomainValidationException::class.java)
            .hasMessage("著者の名前は1文字以上、255文字以下でなければなりません。name: ")
            .extracting("userMessage")
            .isEqualTo("名前は1文字以上、255文字以下でなければなりません。")
    }
}
