package com.k1e1n04.bookmanagement.model

import com.k1e1n04.bookmanagement.exception.DomainValidationException
import java.time.LocalDate
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

/**
 * 著者エンティティのテストクラス
 */
class AuthorEntityTest {
    @Test
    fun `new returns a new instance`() {
        val name = "Test Author"
        val dateOfBirth = LocalDate.of(1990, 1, 1)
        val author = AuthorEntity.new(name, dateOfBirth)

        assertThat(author.name).isEqualTo(name)
        assertThat(author.dateOfBirth).isEqualTo(dateOfBirth)
    }

    @Test
    fun `new allows maximum length name`() {
        val name = "a".repeat(255)
        val dateOfBirth = LocalDate.of(1990, 1, 1)
        val author = AuthorEntity.new(name, dateOfBirth)

        assertThat(author.name).isEqualTo(name)
        assertThat(author.dateOfBirth).isEqualTo(dateOfBirth)
    }

    @Test
    fun `new allows maximum length name with multibyte characters`() {
        val name = "あ".repeat(255)
        val dateOfBirth = LocalDate.of(1990, 1, 1)
        val author = AuthorEntity.new(name, dateOfBirth)

        assertThat(author.name).isEqualTo(name)
        assertThat(author.dateOfBirth).isEqualTo(dateOfBirth)
    }

    @Test
    fun `new throws DomainValidationException when name is blank`() {
        val name = ""
        val dateOfBirth = LocalDate.of(1990, 1, 1)

        val exception =
            org.junit.jupiter.api.Assertions.assertThrows(
                DomainValidationException::class.java,
            ) {
                AuthorEntity.new(name, dateOfBirth)
            }

        assertThat(exception.userMessage).isEqualTo("名前は1文字以上、255文字以下でなければなりません。")
    }

    @Test
    fun `new throws DomainValidationException when name exceeds 255 characters`() {
        val name = "a".repeat(256)
        val dateOfBirth = LocalDate.of(1990, 1, 1)

        val exception =
            org.junit.jupiter.api.Assertions.assertThrows(
                DomainValidationException::class.java,
            ) {
                AuthorEntity.new(name, dateOfBirth)
            }

        assertThat(exception.userMessage).isEqualTo("名前は1文字以上、255文字以下でなければなりません。")
    }

    @Test
    fun `new throws DomainValidationException when dateOfBirth is in the future`() {
        val name = "Test Author"
        val dateOfBirth = LocalDate.now().plusDays(1)

        val exception =
            org.junit.jupiter.api.Assertions.assertThrows(
                DomainValidationException::class.java,
            ) {
                AuthorEntity.new(name, dateOfBirth)
            }

        assertThat(exception.userMessage).isEqualTo("生年月日は過去の日付である必要があります。")
    }

    @Test
    fun `update returns an updated instance`() {
        val originalAuthor = AuthorEntity.new("Original Author", LocalDate.of(1990, 1, 1))
        val updatedName = "Updated Author"
        val updatedDateOfBirth = LocalDate.of(1995, 5, 5)
        val updatedAuthor = originalAuthor.update(updatedName, updatedDateOfBirth)

        assertThat(updatedAuthor.id).isEqualTo(originalAuthor.id)
        assertThat(updatedAuthor.name).isEqualTo(updatedName)
        assertThat(updatedAuthor.dateOfBirth).isEqualTo(updatedDateOfBirth)
    }

    @Test
    fun `update allows maximum length name`() {
        val originalAuthor = AuthorEntity.new("Original Author", LocalDate.of(1990, 1, 1))
        val updatedName = "a".repeat(255)
        val updatedDateOfBirth = LocalDate.of(1995, 5, 5)
        val updatedAuthor = originalAuthor.update(updatedName, updatedDateOfBirth)

        assertThat(updatedAuthor.name).isEqualTo(updatedName)
        assertThat(updatedAuthor.dateOfBirth).isEqualTo(updatedDateOfBirth)
    }

    @Test
    fun `update allows maximum length name with multibyte characters`() {
        val originalAuthor = AuthorEntity.new("Original Author", LocalDate.of(1990, 1, 1))
        val updatedName = "あ".repeat(255)
        val updatedDateOfBirth = LocalDate.of(1995, 5, 5)
        val updatedAuthor = originalAuthor.update(updatedName, updatedDateOfBirth)

        assertThat(updatedAuthor.name).isEqualTo(updatedName)
        assertThat(updatedAuthor.dateOfBirth).isEqualTo(updatedDateOfBirth)
    }

    @Test
    fun `update throws DomainValidationException when name is blank`() {
        val originalAuthor = AuthorEntity.new("Original Author", LocalDate.of(1990, 1, 1))
        val updatedName = ""
        val updatedDateOfBirth = LocalDate.of(1995, 5, 5)

        val exception =
            org.junit.jupiter.api.Assertions.assertThrows(
                DomainValidationException::class.java,
            ) {
                originalAuthor.update(updatedName, updatedDateOfBirth)
            }

        assertThat(exception.userMessage).isEqualTo("名前は1文字以上、255文字以下でなければなりません。")
    }

    @Test
    fun `update throws DomainValidationException when name exceeds 255 characters`() {
        val originalAuthor = AuthorEntity.new("Original Author", LocalDate.of(1990, 1, 1))
        val updatedName = "a".repeat(256)
        val updatedDateOfBirth = LocalDate.of(1995, 5, 5)

        val exception =
            org.junit.jupiter.api.Assertions.assertThrows(
                DomainValidationException::class.java,
            ) {
                originalAuthor.update(updatedName, updatedDateOfBirth)
            }

        assertThat(exception.userMessage).isEqualTo("名前は1文字以上、255文字以下でなければなりません。")
    }

    @Test
    fun `update throws DomainValidationException when dateOfBirth is in the future`() {
        val originalAuthor = AuthorEntity.new("Original Author", LocalDate.of(1990, 1, 1))
        val updatedName = "Updated Author"
        val updatedDateOfBirth = LocalDate.now().plusDays(1)

        val exception =
            org.junit.jupiter.api.Assertions.assertThrows(
                DomainValidationException::class.java,
            ) {
                originalAuthor.update(updatedName, updatedDateOfBirth)
            }

        assertThat(exception.userMessage).isEqualTo("生年月日は過去の日付である必要があります。")
    }
}
