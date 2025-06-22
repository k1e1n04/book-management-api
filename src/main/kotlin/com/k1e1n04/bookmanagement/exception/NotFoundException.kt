package com.k1e1n04.bookmanagement.exception

/**
 * リソースが見つからない場合にスローされる例外
 *
 * @param userMessage ユーザに表示するメッセージ
 * @param message エラーメッセージ
 * @param cause 原因となる例外
 */
class NotFoundException(
    val userMessage: String,
    message: String,
    cause: Throwable? = null,
) : RuntimeException(message, cause)
