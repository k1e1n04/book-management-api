package com.k1e1n04.bookmanagement.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.k1e1n04.bookmanagement.request.AuthorRegisterRequest
import com.k1e1n04.bookmanagement.request.AuthorUpdateRequest
import com.k1e1n04.bookmanagement.response.AuthorResponse
import com.k1e1n04.bookmanagement.service.AuthorService
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import java.time.LocalDate
import java.util.UUID
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

/**
 * 著者情報を管理するRESTコントローラーのテストクラス
 */
@WebMvcTest(AuthorRestController::class)
@ExtendWith(MockKExtension::class)
class AuthorRestControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockkBean
    private lateinit var authorService: AuthorService

    private lateinit var objectMapper: ObjectMapper

    companion object {
        private val AUTHOR_ID_1: UUID = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa")
    }

    @BeforeEach
    fun setUp() {
        objectMapper = ObjectMapper().registerModule(JavaTimeModule())
    }

    @Test
    fun `POST authors should create new author`() {
        val request =
            AuthorRegisterRequest(
                name = "新しい著者",
                dateOfBirth = LocalDate.of(1990, 1, 1),
            )

        val response =
            AuthorResponse(
                id = AUTHOR_ID_1.toString(),
                name = "新しい著者",
                dateOfBirth = "1990-01-01",
            )

        every { authorService.registerAuthor(request) } returns response

        mockMvc.perform(
            post("/api/authors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)),
        )
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(response.id))
            .andExpect(jsonPath("$.name").value(response.name))
            .andExpect(jsonPath("$.dateOfBirth").value(response.dateOfBirth))

        verify(exactly = 1) { authorService.registerAuthor(request) }
    }

    @Test
    fun `POST authors should return 400 Bad Request for invalid input`() {
        val request =
            AuthorRegisterRequest(
                name = "",
                dateOfBirth = LocalDate.now().plusYears(1),
            )

        mockMvc.perform(
            post("/api/authors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)),
        )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errors.length()").value(2))
            .andExpect(jsonPath("$.errors[?(@.field == 'name')].message").value("名前は必須です。"))
            .andExpect(jsonPath("$.errors[?(@.field == 'dateOfBirth')].message").value("生年月日は過去の日付である必要があります。"))

        verify(exactly = 0) { authorService.registerAuthor(any()) }
    }

    @Test
    fun `PUT authors should update existing author`() {
        val authorId = AUTHOR_ID_1.toString()
        val request =
            AuthorUpdateRequest(
                name = "更新された著者",
                dateOfBirth = LocalDate.of(1985, 5, 20),
            )

        val response =
            AuthorResponse(
                id = authorId,
                name = "更新された著者",
                dateOfBirth = "1985-05-20",
            )

        every { authorService.updateAuthor(authorId, request) } returns response

        mockMvc.perform(
            put("/api/authors/$authorId")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)),
        )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(response.id))
            .andExpect(jsonPath("$.name").value(response.name))
            .andExpect(jsonPath("$.dateOfBirth").value(response.dateOfBirth))

        verify(exactly = 1) { authorService.updateAuthor(authorId, request) }
    }

    @Test
    fun `PUT authors should return 400 Bad Request for invalid input`() {
        val authorId = AUTHOR_ID_1.toString()
        val request =
            AuthorUpdateRequest(
                name = "",
                dateOfBirth = LocalDate.now().plusYears(1),
            )

        mockMvc.perform(
            put("/api/authors/$authorId")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)),
        )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errors.length()").value(2))
            .andExpect(jsonPath("$.errors[?(@.field == 'name')].message").value("名前は必須です。"))
            .andExpect(jsonPath("$.errors[?(@.field == 'dateOfBirth')].message").value("生年月日は過去の日付である必要があります。"))

        verify(exactly = 0) { authorService.updateAuthor(any(), any()) }
    }
}
