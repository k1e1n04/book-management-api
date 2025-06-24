package com.k1e1n04.bookmanagement.exception

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.HttpMediaTypeNotSupportedException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.resource.NoResourceFoundException

/**
 * グローバルな例外ハンドラー
 */
@RestControllerAdvice
class GlobalExceptionHandler {
    private val logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    /**
     * MethodArgumentNotValidExceptionを処理するハンドラー
     *
     * @param ex バリデーションエラー
     * @return エラーレスポンス
     */
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(ex: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        val errors =
            ex.bindingResult.fieldErrors.map { error ->
                ValidationError(
                    field = error.field,
                    message = error.defaultMessage ?: "バリデーションエラーが発生しました。",
                )
            }

        val errorResponse =
            ErrorResponse(
                message = "入力値にエラーがあります。",
                errors = errors,
            )

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }

    /**
     * HttpMessageNotReadableExceptionを処理するハンドラー
     *
     * @param ex リクエストの読み取りエラー
     * @return エラーレスポンス
     */
    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleHttpMessageNotReadableException(
        ex: HttpMessageNotReadableException,
    ): ResponseEntity<Map<String, String?>> {
        logger.warn("リクエストの読み取りに失敗しました: ${ex.message}", ex)
        val body =
            mapOf(
                "message" to "リクエストの形式が不正です。",
            )
        return ResponseEntity(body, HttpStatus.BAD_REQUEST)
    }

    /**
     *  DomainValidationExceptionを処理するハンドラー
     *
     * @param ex ドメインバリデーションエラー
     * @return エラーレスポンス
     */
    @ExceptionHandler(DomainValidationException::class)
    fun handleDomainValidationException(ex: DomainValidationException): ResponseEntity<Map<String, String?>> {
        logger.warn("ドメインバリデーションエラー: ${ex.message}", ex)
        val body =
            mapOf(
                "message" to ex.userMessage,
            )
        return ResponseEntity(body, HttpStatus.BAD_REQUEST)
    }

    /**
     * NotFoundExceptionを処理するハンドラー
     *
     * @param ex リソースが見つからないエラー
     * @return エラーレスポンス
     */
    @ExceptionHandler(NotFoundException::class)
    fun handleNotFoundException(ex: NotFoundException): ResponseEntity<Map<String, String?>> {
        logger.warn("リソースが見つかりません: ${ex.message}", ex)
        val body =
            mapOf(
                "message" to ex.userMessage,
            )
        return ResponseEntity(body, HttpStatus.NOT_FOUND)
    }

    /**
     * HttpRequestMethodNotSupportedExceptionを処理するハンドラー
     *
     * @param ex 許可されていないHTTPメソッドエラー
     * @return エラーレスポンス
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    fun handleHttpRequestMethodNotSupportedException(
        ex: HttpRequestMethodNotSupportedException,
    ): ResponseEntity<Map<String, String?>> {
        logger.warn("許可されていないHTTPメソッド: ${ex.method}", ex)
        val body =
            mapOf(
                "message" to "許可されていないHTTPメソッドです。",
            )
        return ResponseEntity(body, HttpStatus.METHOD_NOT_ALLOWED)
    }

    /**
     * NoResourceFoundExceptionを処理するハンドラー
     *
     * @param ex リソースが見つからないエラー
     * @return エラーレスポンス
     */
    @ExceptionHandler(NoResourceFoundException::class)
    fun handleNoResourceFoundException(ex: NoResourceFoundException): ResponseEntity<Map<String, String?>> {
        logger.debug("リソースが見つかりません: ${ex.resourcePath}", ex)
        val body =
            mapOf(
                "message" to "リクエストされたリソースが見つかりません。",
            )
        return ResponseEntity(body, HttpStatus.NOT_FOUND)
    }

    /**
     * HttpMediaTypeNotSupportedExceptionを処理するハンドラー
     *
     * @param ex サポートされていないメディアタイプエラー
     * @return エラーレスポンス
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException::class)
    fun handleHttpMediaTypeNotSupportedException(
        ex: HttpMediaTypeNotSupportedException,
    ): ResponseEntity<Map<String, String?>> {
        logger.warn("サポートされていないメディアタイプ: ${ex.contentType}", ex)
        val body =
            mapOf(
                "message" to "サポートされていないメディアタイプです。",
            )
        return ResponseEntity(body, HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    }

    /**
     * InvalidStateExceptionを処理するハンドラー
     *
     * @param ex 不正な状態エラー
     * @return エラーレスポンス
     */
    @ExceptionHandler(InvalidStateException::class)
    fun handleHttpMediaTypeNotSupportedException(ex: InvalidStateException): ResponseEntity<Map<String, String?>> {
        logger.error("不正な状態エラー: ${ex.message}", ex)
        val body =
            mapOf(
                "message" to "サーバーエラーが発生しました。",
            )
        return ResponseEntity(body, HttpStatus.INTERNAL_SERVER_ERROR)
    }

    /**
     * その他の予期せぬ例外を処理するハンドラー
     *
     * @param ex 予期せぬエラー
     * @return エラーレスポンス
     */
    @ExceptionHandler(Exception::class)
    fun handleException(ex: Exception): ResponseEntity<Map<String, String?>> {
        logger.error("予期せぬエラーが発生しました: ${ex.message}", ex)
        val body =
            mapOf(
                "message" to "サーバーエラーが発生しました。",
            )
        return ResponseEntity(body, HttpStatus.INTERNAL_SERVER_ERROR)
    }

    /**
     * エラーレスポンス用データクラス
     */
    data class ErrorResponse(
        val message: String,
        val errors: List<ValidationError>,
    )

    /**
     * バリデーションエラー詳細
     */
    data class ValidationError(
        val field: String,
        val message: String,
    )
}
