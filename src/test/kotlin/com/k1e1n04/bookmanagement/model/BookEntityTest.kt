package com.k1e1n04.bookmanagement.model

import com.k1e1n04.bookmanagement.exception.DomainValidationException
import java.util.UUID
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

/**
 * 著者エンティティのテストクラス
 */
class BookEntityTest {
    companion object {
        private const val AUTHOR_ID_1 = "11111111-1111-1111-1111-111111111111"
        private const val AUTHOR_ID_2 = "22222222-2222-2222-2222-222222222222"
    }

    @Test
    fun `new returns a new instance`() {
        val title = "Test Book"
        val price = 1500
        val authorIds =
            listOf(
                AUTHOR_ID_1,
                AUTHOR_ID_2,
            )
        val book =
            BookEntity.new(
                title = title,
                price = price,
                authorIds = authorIds,
                status = PublicationStatus.UNPUBLISHED,
            )
        assertThat(book.title).isEqualTo(title)
        assertThat(book.price).isEqualTo(price)
        assertThat(book.authorIds).containsExactlyInAnyOrder(
            UUID.fromString(AUTHOR_ID_1),
            UUID.fromString(AUTHOR_ID_2),
        )
        assertThat(book.status).isEqualTo(PublicationStatus.UNPUBLISHED)
    }

    @Test
    fun `new allows maximum title length`() {
        val title = "a".repeat(255)
        val price = 1500
        val authorIds = listOf(AUTHOR_ID_1, AUTHOR_ID_2)

        val book =
            BookEntity.new(
                title = title,
                price = price,
                authorIds = authorIds,
                status = PublicationStatus.UNPUBLISHED,
            )

        assertThat(book.title).isEqualTo(title)
        assertThat(book.price).isEqualTo(price)
        assertThat(book.authorIds).containsExactlyInAnyOrder(
            UUID.fromString(AUTHOR_ID_1),
            UUID.fromString(AUTHOR_ID_2),
        )
    }

    @Test
    fun `new allows multi-byte characters in title`() {
        val title = "あ".repeat(255)
        val price = 1500
        val authorIds = listOf(AUTHOR_ID_1, AUTHOR_ID_2)

        val book =
            BookEntity.new(
                title = title,
                price = price,
                authorIds = authorIds,
                status = PublicationStatus.UNPUBLISHED,
            )

        assertThat(book.title).isEqualTo(title)
        assertThat(book.price).isEqualTo(price)
        assertThat(book.authorIds).containsExactlyInAnyOrder(
            UUID.fromString(AUTHOR_ID_1),
            UUID.fromString(AUTHOR_ID_2),
        )
    }

    @Test
    fun `new allows zero price`() {
        val title = "Test Book"
        val price = 0
        val authorIds = listOf(AUTHOR_ID_1, AUTHOR_ID_2)

        val book =
            BookEntity.new(
                title = title,
                price = price,
                authorIds = authorIds,
                status = PublicationStatus.UNPUBLISHED,
            )

        assertThat(book.title).isEqualTo(title)
        assertThat(book.price).isEqualTo(price)
        assertThat(book.authorIds).containsExactlyInAnyOrder(
            UUID.fromString(AUTHOR_ID_1),
            UUID.fromString(AUTHOR_ID_2),
        )
    }

    @Test
    fun `new allows published status`() {
        val title = "Test Book"
        val price = 1500
        val authorIds = listOf(AUTHOR_ID_1, AUTHOR_ID_2)

        val book =
            BookEntity.new(
                title = title,
                price = price,
                authorIds = authorIds,
                status = PublicationStatus.PUBLISHED,
            )

        assertThat(book.title).isEqualTo(title)
        assertThat(book.price).isEqualTo(price)
        assertThat(book.authorIds).containsExactlyInAnyOrder(
            UUID.fromString(AUTHOR_ID_1),
            UUID.fromString(AUTHOR_ID_2),
        )
        assertThat(book.status).isEqualTo(PublicationStatus.PUBLISHED)
    }

    @Test
    fun `new throws exception for invalid title`() {
        val invalidTitle = ""
        val price = 1500
        val authorIds = listOf(AUTHOR_ID_1, AUTHOR_ID_2)

        assertThatThrownBy {
            BookEntity.new(
                title = invalidTitle,
                price = price,
                authorIds = authorIds,
                status = PublicationStatus.UNPUBLISHED,
            )
        }.isInstanceOf(DomainValidationException::class.java)
            .hasMessageContaining("書籍のタイトルは1文字以上、255文字以下でなければなりません。")
    }

    @Test
    fun `new throws exception for too long title`() {
        val title = "a".repeat(256)
        val price = 1500
        val authorIds = listOf(AUTHOR_ID_1, AUTHOR_ID_2)

        assertThatThrownBy {
            BookEntity.new(title, price, authorIds, PublicationStatus.UNPUBLISHED)
        }.isInstanceOf(DomainValidationException::class.java)
            .hasMessageContaining("書籍のタイトルは1文字以上、255文字以下でなければなりません。")
    }

    @Test
    fun `new throws exception for negative price`() {
        val title = "Test Book"
        val price = -1000
        val authorIds = listOf(AUTHOR_ID_1, AUTHOR_ID_2)

        assertThatThrownBy {
            BookEntity.new(title, price, authorIds, PublicationStatus.UNPUBLISHED)
        }.isInstanceOf(DomainValidationException::class.java)
            .hasMessageContaining("書籍の価格は0円以上でなければなりません。")
    }

    @Test
    fun `new throws exception for invalid author IDs`() {
        val title = "Test Book"
        val price = 1500
        val authorIds = listOf("invalid-uuid", AUTHOR_ID_2)

        assertThatThrownBy {
            BookEntity.new(title, price, authorIds, PublicationStatus.UNPUBLISHED)
        }.isInstanceOf(DomainValidationException::class.java)
            .hasMessageContaining("著者IDの形式が不正です。")
    }

    @Test
    fun `new throws exception for empty author IDs`() {
        val title = "Test Book"
        val price = 1500
        val authorIds = emptyList<String>()

        assertThatThrownBy {
            BookEntity.new(title, price, authorIds, PublicationStatus.UNPUBLISHED)
        }.isInstanceOf(DomainValidationException::class.java)
            .hasMessageContaining("書籍には少なくとも1人の著者が必要です。")
    }

    @Test
    fun `new throws exception for duplicate author IDs`() {
        val title = "Test Book"
        val price = 1500
        val authorIds = listOf(AUTHOR_ID_1, AUTHOR_ID_1)

        assertThatThrownBy {
            BookEntity.new(title, price, authorIds, PublicationStatus.UNPUBLISHED)
        }.isInstanceOf(DomainValidationException::class.java)
            .hasMessageContaining("書籍の著者IDは重複してはいけません。")
    }

    @Test
    fun `new throws exception for exceeding maximum price`() {
        val title = "Test Book"
        val price = 1000001
        val authorIds = listOf(AUTHOR_ID_1, AUTHOR_ID_2)

        assertThatThrownBy {
            BookEntity.new(title, price, authorIds, PublicationStatus.UNPUBLISHED)
        }.isInstanceOf(DomainValidationException::class.java)
            .hasMessageContaining("書籍の価格は100万円以下でなければなりません。")
    }

    @Test
    fun `update returns updated book entity`() {
        val book =
            BookEntity.new(
                title = "Old Title",
                price = 1000,
                authorIds = listOf(AUTHOR_ID_1, AUTHOR_ID_2),
                status = PublicationStatus.UNPUBLISHED,
            )

        val updatedBook =
            book.update(
                title = "New Title",
                price = 1200,
                authorIds = listOf(AUTHOR_ID_1, AUTHOR_ID_2),
                status = PublicationStatus.PUBLISHED,
            )

        assertThat(updatedBook.title).isEqualTo("New Title")
        assertThat(updatedBook.price).isEqualTo(1200)
        assertThat(updatedBook.status).isEqualTo(PublicationStatus.PUBLISHED)
    }

    @Test
    fun `update allows maximum title length`() {
        val book =
            BookEntity.new(
                title = "Old Title",
                price = 1000,
                authorIds = listOf(AUTHOR_ID_1, AUTHOR_ID_2),
                status = PublicationStatus.UNPUBLISHED,
            )

        val longTitle = "a".repeat(255)
        val updatedBook =
            book.update(
                title = longTitle,
                price = 1200,
                authorIds = listOf(AUTHOR_ID_1, AUTHOR_ID_2),
                status = PublicationStatus.PUBLISHED,
            )

        assertThat(updatedBook.title).isEqualTo(longTitle)
    }

    @Test
    fun `update allows zero price`() {
        val book =
            BookEntity.new(
                title = "Old Title",
                price = 1000,
                authorIds = listOf(AUTHOR_ID_1, AUTHOR_ID_2),
                status = PublicationStatus.UNPUBLISHED,
            )

        val updatedBook =
            book.update(
                title = "New Title",
                price = 0,
                authorIds = listOf(AUTHOR_ID_1, AUTHOR_ID_2),
                status = PublicationStatus.PUBLISHED,
            )

        assertThat(updatedBook.price).isEqualTo(0)
    }

    @Test
    fun `update throws exception for empty title`() {
        val book =
            BookEntity.new(
                title = "Old Title",
                price = 1000,
                authorIds = listOf(AUTHOR_ID_1, AUTHOR_ID_2),
                status = PublicationStatus.UNPUBLISHED,
            )

        assertThatThrownBy {
            book.update(
                title = "",
                price = 1200,
                authorIds = listOf(AUTHOR_ID_1, AUTHOR_ID_2),
                status = PublicationStatus.PUBLISHED,
            )
        }.isInstanceOf(DomainValidationException::class.java)
            .hasMessageContaining("書籍のタイトルは1文字以上、255文字以下でなければなりません。")
    }

    @Test
    fun `update throws exception for too long title`() {
        val book =
            BookEntity.new(
                title = "Old Title",
                price = 1000,
                authorIds = listOf(AUTHOR_ID_1, AUTHOR_ID_2),
                status = PublicationStatus.UNPUBLISHED,
            )

        val longTitle = "a".repeat(256)

        assertThatThrownBy {
            book.update(
                title = longTitle,
                price = 1200,
                authorIds = listOf(AUTHOR_ID_1, AUTHOR_ID_2),
                status = PublicationStatus.PUBLISHED,
            )
        }.isInstanceOf(DomainValidationException::class.java)
            .hasMessageContaining("書籍のタイトルは1文字以上、255文字以下でなければなりません。")
    }

    @Test
    fun `update throws exception for negative price`() {
        val book =
            BookEntity.new(
                title = "Old Title",
                price = 1000,
                authorIds = listOf(AUTHOR_ID_1, AUTHOR_ID_2),
                status = PublicationStatus.UNPUBLISHED,
            )

        assertThatThrownBy {
            book.update(
                title = "New Title",
                price = -1000,
                authorIds = listOf(AUTHOR_ID_1, AUTHOR_ID_2),
                status = PublicationStatus.PUBLISHED,
            )
        }.isInstanceOf(DomainValidationException::class.java)
            .hasMessageContaining("書籍の価格は0円以上でなければなりません。")
    }

    @Test
    fun `update throws exception for exceeding maximum price`() {
        val book =
            BookEntity.new(
                title = "Old Title",
                price = 1000,
                authorIds = listOf(AUTHOR_ID_1, AUTHOR_ID_2),
                status = PublicationStatus.UNPUBLISHED,
            )

        assertThatThrownBy {
            book.update(
                title = "New Title",
                price = 1000001,
                authorIds = listOf(AUTHOR_ID_1, AUTHOR_ID_2),
                status = PublicationStatus.PUBLISHED,
            )
        }.isInstanceOf(DomainValidationException::class.java)
            .hasMessageContaining("書籍の価格は100万円以下でなければなりません。")
    }

    @Test
    fun `update throws exception for invalid author IDs`() {
        val book =
            BookEntity.new(
                title = "Old Title",
                price = 1000,
                authorIds = listOf(AUTHOR_ID_1, AUTHOR_ID_2),
                status = PublicationStatus.UNPUBLISHED,
            )

        assertThatThrownBy {
            book.update(
                title = "New Title",
                price = 1200,
                authorIds = listOf("invalid-uuid", AUTHOR_ID_2),
                status = PublicationStatus.PUBLISHED,
            )
        }.isInstanceOf(DomainValidationException::class.java)
            .hasMessageContaining("著者IDの形式が不正です。")
    }

    @Test
    fun `update throws exception for empty author IDs`() {
        val book =
            BookEntity.new(
                title = "Old Title",
                price = 1000,
                authorIds = listOf(AUTHOR_ID_1, AUTHOR_ID_2),
                status = PublicationStatus.UNPUBLISHED,
            )

        assertThatThrownBy {
            book.update(
                title = "New Title",
                price = 1200,
                authorIds = emptyList(),
                status = PublicationStatus.PUBLISHED,
            )
        }.isInstanceOf(DomainValidationException::class.java)
            .hasMessageContaining("書籍には少なくとも1人の著者が必要です。")
    }

    @Test
    fun `update throws exception for duplicate author IDs`() {
        val book =
            BookEntity.new(
                title = "Old Title",
                price = 1000,
                authorIds = listOf(AUTHOR_ID_1, AUTHOR_ID_2),
                status = PublicationStatus.UNPUBLISHED,
            )

        assertThatThrownBy {
            book.update(
                title = "New Title",
                price = 1200,
                authorIds = listOf(AUTHOR_ID_1, AUTHOR_ID_1),
                status = PublicationStatus.PUBLISHED,
            )
        }.isInstanceOf(DomainValidationException::class.java)
            .hasMessageContaining("書籍の著者IDは重複してはいけません。")
    }

    @Test
    fun `update throws exception when trying to change status from published to unpublished`() {
        val book =
            BookEntity.new(
                title = "Published Book",
                price = 1000,
                authorIds = listOf(AUTHOR_ID_1, AUTHOR_ID_2),
                status = PublicationStatus.PUBLISHED,
            )

        assertThatThrownBy {
            book.update(
                title = "Unpublished Book",
                price = 1200,
                authorIds = listOf(AUTHOR_ID_1, AUTHOR_ID_2),
                status = PublicationStatus.UNPUBLISHED,
            )
        }.isInstanceOf(DomainValidationException::class.java)
            .hasMessageContaining("出版済みの書籍を非公開にしようとしました。")
    }

    @Test
    fun `update allows changing status from unpublished to published`() {
        val book =
            BookEntity.new(
                title = "Unpublished Book",
                price = 1000,
                authorIds = listOf(AUTHOR_ID_1, AUTHOR_ID_2),
                status = PublicationStatus.UNPUBLISHED,
            )

        val updatedBook =
            book.update(
                title = "Now Published Book",
                price = 1200,
                authorIds = listOf(AUTHOR_ID_1, AUTHOR_ID_2),
                status = PublicationStatus.PUBLISHED,
            )

        assertThat(updatedBook.status).isEqualTo(PublicationStatus.PUBLISHED)
    }

    @Test
    fun `update allows changing status from unpublished to unpublished`() {
        val book =
            BookEntity.new(
                title = "Unpublished Book",
                price = 1000,
                authorIds = listOf(AUTHOR_ID_1, AUTHOR_ID_2),
                status = PublicationStatus.UNPUBLISHED,
            )

        val updatedBook =
            book.update(
                title = "Still Unpublished Book",
                price = 1200,
                authorIds = listOf(AUTHOR_ID_1, AUTHOR_ID_2),
                status = PublicationStatus.UNPUBLISHED,
            )

        assertThat(updatedBook.status).isEqualTo(PublicationStatus.UNPUBLISHED)
    }

    @Test
    fun `update allows changing status from published to published`() {
        val book =
            BookEntity.new(
                title = "Published Book",
                price = 1000,
                authorIds = listOf(AUTHOR_ID_1, AUTHOR_ID_2),
                status = PublicationStatus.PUBLISHED,
            )

        val updatedBook =
            book.update(
                title = "Still Published Book",
                price = 1200,
                authorIds = listOf(AUTHOR_ID_1, AUTHOR_ID_2),
                status = PublicationStatus.PUBLISHED,
            )

        assertThat(updatedBook.status).isEqualTo(PublicationStatus.PUBLISHED)
    }
}
