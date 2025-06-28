package com.k1e1n04.bookmanagement.repository

import com.k1e1n04.bookmanagement.exception.NotFoundException
import com.k1e1n04.bookmanagement.model.AuthorEntity
import java.time.LocalDate
import java.util.UUID
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.jooq.DSLContext
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DuplicateKeyException
import org.springframework.test.context.jdbc.Sql

/**
 * 著者リポジトリのテストクラス
 */
@RepositoryTest
class AuthorRepositoryImplTest {
    @Autowired
    private lateinit var dslContext: DSLContext

    private lateinit var authorRepository: AuthorRepositoryImpl

    companion object {
        private val AUTHOR_ID_1: UUID = UUID.fromString("11111111-1111-1111-1111-111111111111")
        private val AUTHOR_ID_2: UUID = UUID.fromString("22222222-2222-2222-2222-222222222222")
        private val NON_EXISTENT_AUTHOR_ID_1: UUID = UUID.fromString("33333333-3333-3333-3333-333333333333")
        private val NON_EXISTENT_AUTHOR_ID_2: UUID = UUID.fromString("44444444-4444-4444-4444-444444444444")
    }

    @BeforeEach
    fun setup() {
        authorRepository = AuthorRepositoryImpl(dslContext)
    }

    @Test
    @Sql("/data/insert_authors.sql")
    fun `findAll returns all authors when data exists`() {
        val authors = authorRepository.findAll()
        assertThat(authors).hasSize(2)
        assertThat(authors).containsExactlyInAnyOrder(
            AuthorEntity(
                id = AUTHOR_ID_1,
                name = "夏目漱石",
                dateOfBirth = LocalDate.of(1867, 2, 9),
            ),
            AuthorEntity(
                id = AUTHOR_ID_2,
                name = "芥川龍之介",
                dateOfBirth = LocalDate.of(1892, 3, 1),
            ),
        )
    }

    @Test
    fun `findAll returns empty list when no data exists`() {
        val authors = authorRepository.findAll()
        assertThat(authors).isEmpty()
    }

    @Test
    @Sql("/data/insert_authors.sql")
    fun `save creates and returns a new author`() {
        val newAuthor =
            AuthorEntity.new(
                name = "新しい著者",
                dateOfBirth = LocalDate.of(1990, 1, 1),
            )

        val savedAuthor = authorRepository.save(newAuthor)

        assertThat(savedAuthor)
            .usingRecursiveComparison()
            .isEqualTo(newAuthor)

        val foundAuthor = authorRepository.findById(newAuthor.id)
        assertThat(foundAuthor)
            .usingRecursiveComparison()
            .isEqualTo(newAuthor)
    }

    @Test
    fun `save allows maximum length name`() {
        val maxLengthName = "a".repeat(255)
        val newAuthor =
            AuthorEntity.new(
                name = maxLengthName,
                dateOfBirth = LocalDate.of(1990, 1, 1),
            )

        val savedAuthor = authorRepository.save(newAuthor)

        assertThat(savedAuthor.name).isEqualTo(maxLengthName)
    }

    @Test
    fun `save allows maximum length name with multibyte characters`() {
        val maxLengthName = "あ".repeat(255)
        val newAuthor =
            AuthorEntity.new(
                name = maxLengthName,
                dateOfBirth = LocalDate.of(1990, 1, 1),
            )

        val savedAuthor = authorRepository.save(newAuthor)

        assertThat(savedAuthor.name).isEqualTo(maxLengthName)
    }

    @Test
    @Sql("/data/insert_authors.sql")
    fun `save throws DuplicateKeyException when saving author with existing ID`() {
        val existingAuthor =
            AuthorEntity(
                id = AUTHOR_ID_1,
                name = "夏目漱石",
                dateOfBirth = LocalDate.of(1867, 2, 9),
            )

        assertThatThrownBy { authorRepository.save(existingAuthor) }
            .isInstanceOf(DuplicateKeyException::class.java)
    }

    @Test
    @Sql("/data/insert_authors.sql")
    fun `findById returns author when ID exists`() {
        val author = authorRepository.findById(AUTHOR_ID_1)
        assertThat(author).isNotNull
        assertThat(author)
            .usingRecursiveComparison()
            .isEqualTo(
                AuthorEntity(
                    id = AUTHOR_ID_1,
                    name = "夏目漱石",
                    dateOfBirth = LocalDate.of(1867, 2, 9),
                ),
            )
    }

    @Test
    @Sql("/data/insert_authors.sql")
    fun `findById returns null when ID does not exist`() {
        val author = authorRepository.findById(NON_EXISTENT_AUTHOR_ID_1)
        assertThat(author).isNull()
    }

    @Test
    @Sql("/data/insert_authors.sql")
    fun `update modifies and returns existing author`() {
        val authorToUpdate = authorRepository.findById(AUTHOR_ID_1)!! // nullチェックはテストのために簡略化
        val updatedName = "更新された夏目漱石"
        val updatedAuthor =
            authorToUpdate.update(
                name = updatedName,
                dateOfBirth = LocalDate.of(1867, 2, 9),
            )

        val result = assertDoesNotThrow { authorRepository.update(updatedAuthor) }

        assertThat(result)
            .usingRecursiveComparison()
            .isEqualTo(updatedAuthor)

        val foundAuthor = authorRepository.findById(AUTHOR_ID_1)
        assertThat(foundAuthor).isNotNull
        assertThat(foundAuthor!!.name).isEqualTo(updatedName)
    }

    @Test
    @Sql("/data/insert_authors.sql")
    fun `update allows maximum length name`() {
        val authorToUpdate = authorRepository.findById(AUTHOR_ID_1)!!
        val maxLengthName = "a".repeat(255)
        val updatedAuthor =
            authorToUpdate.update(
                name = maxLengthName,
                dateOfBirth = LocalDate.of(1867, 2, 9),
            )

        val result = assertDoesNotThrow { authorRepository.update(updatedAuthor) }

        assertThat(result.name).isEqualTo(maxLengthName)
    }

    @Test
    @Sql("/data/insert_authors.sql")
    fun `update allows maximum length name with multibyte characters`() {
        val authorToUpdate = authorRepository.findById(AUTHOR_ID_1)!!
        val maxLengthName = "あ".repeat(255)
        val updatedAuthor =
            authorToUpdate.update(
                name = maxLengthName,
                dateOfBirth = LocalDate.of(1867, 2, 9),
            )

        val result = assertDoesNotThrow { authorRepository.update(updatedAuthor) }

        assertThat(result.name).isEqualTo(maxLengthName)
    }

    @Test
    @Sql("/data/insert_authors.sql")
    fun `update throws NotFoundException when author does not exist`() {
        val nonExistentAuthor =
            AuthorEntity(
                id = NON_EXISTENT_AUTHOR_ID_1,
                name = "存在しない著者",
                dateOfBirth = LocalDate.of(2000, 1, 1),
            )

        assertThatThrownBy { authorRepository.update(nonExistentAuthor) }
            .isInstanceOf(NotFoundException::class.java)
            .hasMessage("著者ID: ${NON_EXISTENT_AUTHOR_ID_1}は存在しません")
            .extracting("userMessage")
            .isEqualTo("指定された著者は存在しません。")
    }

    @Test
    @Sql("/data/insert_authors.sql")
    fun `findByIds returns all authors for existing IDs`() {
        val authorIds = listOf(AUTHOR_ID_1, AUTHOR_ID_2)
        val authors = authorRepository.findByIds(authorIds)

        assertThat(authors).hasSize(2)
        assertThat(authors).containsExactlyInAnyOrder(
            AuthorEntity(
                id = AUTHOR_ID_1,
                name = "夏目漱石",
                dateOfBirth = LocalDate.of(1867, 2, 9),
            ),
            AuthorEntity(
                id = AUTHOR_ID_2,
                name = "芥川龍之介",
                dateOfBirth = LocalDate.of(1892, 3, 1),
            ),
        )
    }

    @Test
    @Sql("/data/insert_authors.sql")
    fun `findByIds returns only existing authors when some IDs do not exist`() {
        val authorIds = listOf(AUTHOR_ID_1, NON_EXISTENT_AUTHOR_ID_1)
        val authors = authorRepository.findByIds(authorIds)

        assertThat(authors).hasSize(1)
        assertThat(authors).containsExactlyInAnyOrder(
            AuthorEntity(
                id = AUTHOR_ID_1,
                name = "夏目漱石",
                dateOfBirth = LocalDate.of(1867, 2, 9),
            ),
        )
    }

    @Test
    @Sql("/data/insert_authors.sql")
    fun `findByIds returns empty list when no IDs exist`() {
        val authorIds = listOf(NON_EXISTENT_AUTHOR_ID_1, NON_EXISTENT_AUTHOR_ID_2)
        val authors = authorRepository.findByIds(authorIds)
        assertThat(authors).isEmpty()
    }

    @Test
    @Sql("/data/insert_authors.sql")
    fun `findByIds returns empty list for empty ID list`() {
        val authors = authorRepository.findByIds(emptyList())
        assertThat(authors).isEmpty()
    }
}
