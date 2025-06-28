package com.k1e1n04.bookmanagement.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.k1e1n04.bookmanagement.request.BookRegisterRequest
import com.k1e1n04.bookmanagement.request.BookUpdateRequest
import com.k1e1n04.bookmanagement.request.PublicationStatusRequest
import com.k1e1n04.bookmanagement.response.BookResponse
import com.k1e1n04.bookmanagement.response.PublicationStatusResponse
import com.k1e1n04.bookmanagement.service.BookService
import java.util.UUID
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(BookRestController::class)
@ExtendWith(SpringExtension::class)
class BookRestControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var bookService: BookService

    private lateinit var objectMapper: ObjectMapper

    companion object {
        private val BOOK_ID_1: UUID = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa")
        private val BOOK_ID_2: UUID = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb") // Fixed syntax error
        private val AUTHOR_ID_1: UUID = UUID.fromString("cccccccc-cccc-cccc-cccc-cccccccccccc")
        private val AUTHOR_ID_2: UUID = UUID.fromString("dddddddd-dddd-dddd-dddd-dddddddddddd")
    }

    @BeforeEach
    fun setUp() {
        objectMapper = ObjectMapper().registerModule(JavaTimeModule())
    }

    @Test
    fun `GET books should return all books`() {
        val response =
            listOf(
                BookResponse(
                    id = BOOK_ID_1.toString(),
                    title = "書籍1",
                    price = 1500,
                    authorIds = listOf(AUTHOR_ID_1.toString()),
                    status = PublicationStatusResponse.PUBLISHED,
                ),
                BookResponse(
                    id = BOOK_ID_2.toString(),
                    title = "書籍2",
                    price = 2000,
                    authorIds = listOf(AUTHOR_ID_2.toString()),
                    status = PublicationStatusResponse.UNPUBLISHED,
                ),
            )

        whenever(bookService.getAllBooks()).thenReturn(response)

        mockMvc.perform(get("/api/books"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$[0].id").value(BOOK_ID_1.toString()))
            .andExpect(jsonPath("$[0].title").value("書籍1"))
            .andExpect(jsonPath("$[0].price").value(1500))
            .andExpect(jsonPath("$[0].authorIds").isArray)
            .andExpect(jsonPath("$[0].status").value("PUBLISHED"))
            .andExpect(jsonPath("$[1].id").value(BOOK_ID_2.toString()))
            .andExpect(jsonPath("$[1].title").value("書籍2"))
            .andExpect(jsonPath("$[1].price").value(2000))
            .andExpect(jsonPath("$[1].authorIds").isArray)
            .andExpect(jsonPath("$[1].status").value("UNPUBLISHED"))
    }

    @Test
    fun `POST books should create new book`() {
        val request =
            BookRegisterRequest(
                title = "新しい書籍",
                price = 1000,
                authorIds =
                    listOf(
                        AUTHOR_ID_1.toString(),
                        AUTHOR_ID_2.toString(),
                    ),
                status = PublicationStatusRequest.UNPUBLISHED,
            )

        val response =
            BookResponse(
                id = UUID.randomUUID().toString(),
                title = "新しい書籍",
                price = 1000,
                authorIds =
                    listOf(
                        AUTHOR_ID_1.toString(),
                        AUTHOR_ID_2.toString(),
                    ),
                status = PublicationStatusResponse.UNPUBLISHED,
            )

        whenever(
            bookService.registerBook(any<BookRegisterRequest>()),
        ).thenReturn(response) // Using mockito-kotlin's any()

        mockMvc.perform(
            post("/api/books")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request)),
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").isNotEmpty)
            .andExpect(jsonPath("$.title").value("新しい書籍"))
            .andExpect(jsonPath("$.price").value(1000))
            .andExpect(jsonPath("$.authorIds").isArray)
            .andExpect(jsonPath("$.status").value("UNPUBLISHED"))
    }

    @Test
    fun `POST books should return 400 Bad Request for invalid input`() {
        val request =
            BookRegisterRequest(
                title = "",
                price = -100,
                authorIds = emptyList(),
                status = PublicationStatusRequest.UNPUBLISHED,
            )

        mockMvc.perform(
            post("/api/books")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request)),
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `POST books should return 400 Bad Request for price exceeding maximum`() {
        val request =
            BookRegisterRequest(
                title = "高額書籍",
                price = 1000001,
                authorIds = listOf(AUTHOR_ID_1.toString()),
                status = PublicationStatusRequest.UNPUBLISHED,
            )

        mockMvc.perform(
            post("/api/books")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request)),
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `PUT books should update book`() {
        val request =
            BookUpdateRequest(
                title = "更新された書籍",
                price = 1200,
                authorIds = listOf(AUTHOR_ID_1.toString()),
                status = PublicationStatusRequest.PUBLISHED,
            )

        val response =
            BookResponse(
                id = BOOK_ID_1.toString(),
                title = "更新された書籍",
                price = 1200,
                authorIds = listOf(AUTHOR_ID_1.toString()),
                status = PublicationStatusResponse.PUBLISHED,
            )

        whenever(bookService.updateBook(eq(BOOK_ID_1.toString()), any<BookUpdateRequest>()))
            .thenReturn(response)

        mockMvc.perform(
            put("/api/books/${BOOK_ID_1}")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request)),
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(BOOK_ID_1.toString()))
            .andExpect(jsonPath("$.title").value("更新された書籍"))
            .andExpect(jsonPath("$.price").value(1200))
            .andExpect(jsonPath("$.authorIds").isArray)
            .andExpect(jsonPath("$.status").value("PUBLISHED"))
    }

    @Test
    fun `PUT books should return 400 Bad Request for invalid input`() {
        val request =
            BookUpdateRequest(
                title = "",
                price = -100,
                authorIds = emptyList(),
                status = PublicationStatusRequest.UNPUBLISHED,
            )

        mockMvc.perform(
            put("/api/books/${BOOK_ID_1}")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request)),
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `PUT books should return 400 Bad Request for price exceeding maximum`() {
        val request =
            BookUpdateRequest(
                title = "更新テスト書籍",
                price = 1000001,
                authorIds = listOf(AUTHOR_ID_1.toString()),
                status = PublicationStatusRequest.UNPUBLISHED,
            )

        mockMvc.perform(
            put("/api/books/$BOOK_ID_1")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request)),
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.message").value("入力値にエラーがあります。"))
            .andExpect(jsonPath("$.errors[0].field").value("price"))
            .andExpect(jsonPath("$.errors[0].message").value("価格は100万円以下である必要があります。"))
    }

    @Test
    fun `GET by authorId should return books by author`() {
        val response =
            listOf(
                BookResponse(
                    id = BOOK_ID_1.toString(),
                    title = "著者の書籍1",
                    price = 1500,
                    authorIds = listOf(AUTHOR_ID_1.toString()),
                    status = PublicationStatusResponse.PUBLISHED,
                ),
                BookResponse(
                    id = BOOK_ID_2.toString(),
                    title = "著者の書籍2",
                    price = 2000,
                    authorIds = listOf(AUTHOR_ID_1.toString()),
                    status = PublicationStatusResponse.UNPUBLISHED,
                ),
            )

        whenever(bookService.getBooksByAuthor(eq(AUTHOR_ID_1.toString())))
            .thenReturn(response)

        mockMvc.perform(get("/api/books/author/$AUTHOR_ID_1"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$[0].authorIds[0]").value(AUTHOR_ID_1.toString()))
            .andExpect(jsonPath("$[1].authorIds[0]").value(AUTHOR_ID_1.toString()))
    }
}
