package com.k1e1n04.bookmanagement.service

import com.k1e1n04.bookmanagement.exception.DomainValidationException
import com.k1e1n04.bookmanagement.exception.NotFoundException
import com.k1e1n04.bookmanagement.model.BookEntity
import com.k1e1n04.bookmanagement.model.PublicationStatus
import com.k1e1n04.bookmanagement.repository.AuthorRepository
import com.k1e1n04.bookmanagement.repository.BookRepository
import com.k1e1n04.bookmanagement.request.BookRegisterRequest
import com.k1e1n04.bookmanagement.request.BookUpdateRequest
import com.k1e1n04.bookmanagement.request.PublicationStatusRequest
import com.k1e1n04.bookmanagement.response.BookResponse
import com.k1e1n04.bookmanagement.response.PublicationStatusResponse
import java.util.UUID
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 書籍サービスの実装クラス
 */
@Service
class BookServiceImpl(
    private val bookRepository: BookRepository,
    private val authorRepository: AuthorRepository,
) : BookService {
    override fun getAllBooks(): List<BookResponse> = bookRepository.findAll().map(::toResponse)

    @Transactional(readOnly = true)
    override fun getBooksByAuthor(authorId: String): List<BookResponse> {
        val authorUuid =
            try {
                UUID.fromString(authorId)
            } catch (e: IllegalArgumentException) {
                throw NotFoundException(
                    userMessage = "指定された著者は存在しません。",
                    message = "著者IDの形式が不正です: $authorId",
                    cause = e,
                )
            }
        return bookRepository
            .findByAuthorId(authorUuid)
            .map(::toResponse)
    }

    @Transactional
    override fun registerBook(book: BookRegisterRequest): BookResponse {
        validateAuthors(book.authorIds)

        val newBook =
            BookEntity.new(
                title = book.title,
                price = book.price,
                authorIds = book.authorIds,
                status = toPublicationStatus(book.status),
            )

        return bookRepository.save(newBook).let(::toResponse)
    }

    @Transactional
    override fun updateBook(
        id: String,
        book: BookUpdateRequest,
    ): BookResponse {
        val bookId =
            try {
                UUID.fromString(id)
            } catch (e: IllegalArgumentException) {
                throw NotFoundException(
                    userMessage = "指定された書籍は存在しません。",
                    message = "書籍IDの形式が不正です: $id",
                    cause = e,
                )
            }

        val existingBook =
            bookRepository.findById(bookId)
                ?: throw NotFoundException(
                    userMessage = "指定された書籍は存在しません。",
                    message = "書籍ID: $bookId は存在しません。",
                )

        validateAuthors(book.authorIds)

        val updatedBook =
            existingBook.update(
                title = book.title,
                price = book.price,
                authorIds = book.authorIds,
                status = toPublicationStatus(book.status),
            )

        return bookRepository.update(updatedBook).let(::toResponse)
    }

    /**
     * 書籍の著者IDを検証するヘルパーメソッド
     *
     * @param authorIds 著者IDのリスト
     * @throws DomainValidationException 著者IDの形式が不正または存在しない場合
     */
    private fun validateAuthors(authorIds: List<String>) {
        val authorUUIDs =
            try {
                authorIds.map { UUID.fromString(it) }
            } catch (e: IllegalArgumentException) {
                throw DomainValidationException(
                    userMessage = "著者IDの形式が不正です。",
                    message = "著者IDの形式が不正です。: ${authorIds.joinToString(", ")}",
                    cause = e,
                )
            }
        val existingAuthors = authorRepository.findByIds(authorUUIDs)
        if (existingAuthors.size != authorIds.size) {
            throw DomainValidationException(
                userMessage = "指定された著者の一部が存在しません。",
                message = "著者ID: ${authorIds.joinToString(", ")} の一部が存在しません。",
            )
        }
    }

    /**
     * 公開ステータスのリクエストをドメインモデルの公開ステータスに変換するヘルパーメソッド
     *
     * @param status 公開ステータスのリクエスト
     * @return ドメインモデルの公開ステータス
     */
    private fun toPublicationStatus(status: PublicationStatusRequest): PublicationStatus =
        when (status) {
            PublicationStatusRequest.PUBLISHED -> PublicationStatus.PUBLISHED
            PublicationStatusRequest.UNPUBLISHED -> PublicationStatus.UNPUBLISHED
        }

    /**
     * 書籍エンティティをレスポンスに変換するヘルパーメソッド
     *
     * @param bookEntity 書籍エンティティ
     * @return 書籍レスポンス
     */
    private fun toResponse(bookEntity: BookEntity): BookResponse =
        BookResponse(
            id = bookEntity.id.toString(),
            title = bookEntity.title,
            authorIds = bookEntity.authorIds.map { it.toString() },
            status = toPublicationStatusResponse(bookEntity.status),
            price = bookEntity.price,
        )

    /**
     * ドメインモデルの公開ステータスをレスポンス用の公開ステータスに変換するヘルパーメソッド
     *
     * @param status ドメインモデルの公開ステータス
     * @return レスポンス用の公開ステータス
     */
    private fun toPublicationStatusResponse(status: PublicationStatus): PublicationStatusResponse =
        when (status) {
            PublicationStatus.PUBLISHED -> PublicationStatusResponse.PUBLISHED
            PublicationStatus.UNPUBLISHED -> PublicationStatusResponse.UNPUBLISHED
        }
}
