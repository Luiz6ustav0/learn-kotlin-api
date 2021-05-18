package com.hotmart.bankapi

import com.fasterxml.jackson.databind.ObjectMapper
import com.hotmart.bankapi.model.Account
import com.hotmart.bankapi.repository.AccountRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@SpringBootTest
@AutoConfigureMockMvc
internal class AccountControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var accountRepository: AccountRepository

    @Test
    fun `test find all`() {
        accountRepository.save(Account(name = "Test", document = "123", phone = "987654321"))

        mockMvc.perform(MockMvcRequestBuilders.get("/accounts"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("\$[0].id").isNumber)
            .andExpect(MockMvcResultMatchers.jsonPath("\$[0].name").isString)
            .andExpect(MockMvcResultMatchers.jsonPath("\$[0].document").isString)
            .andExpect(MockMvcResultMatchers.jsonPath("\$[0].phone").isString)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `test find by id`() {
        val savedAccount = accountRepository.save(Account(name = "Test", document = "123", phone = "987654321"))

        mockMvc.perform(MockMvcRequestBuilders.get("/accounts/${savedAccount.id}"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.id").isNumber)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.name").isString)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.document").isString)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.phone").isString)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `test create account`() {
        val account = Account(name = "Test Name", document = "12345678900", phone = "987654321")
        val jsonAccount = ObjectMapper().writeValueAsString(account)
        accountRepository.deleteAll()
        mockMvc.perform(
            MockMvcRequestBuilders.post("/accounts")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonAccount)
        )
            .andExpect(MockMvcResultMatchers.status().isCreated)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.name").isString)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.document").isString)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.phone").isString)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `test update account`() {
        accountRepository.deleteAll()
        val account =
            accountRepository.save(Account(name = "Test", document = "123", phone = "987654321")).copy(name = "Updated")
        val jsonAccount = ObjectMapper().writeValueAsString(account)
        mockMvc.perform(
            MockMvcRequestBuilders.put("/accounts/${account.id}")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonAccount)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.name").value(account.name))
            .andExpect(MockMvcResultMatchers.jsonPath("\$.document").value(account.document))
            .andExpect(MockMvcResultMatchers.jsonPath("\$.phone").value(account.phone))
            .andDo(MockMvcResultHandlers.print())

        val findById = accountRepository.findById(account.id!!)
        Assertions.assertTrue(findById.isPresent)
        Assertions.assertEquals(account.name, findById.get().name)
    }

    @Test
    fun `test delete account`() {
        accountRepository.deleteAll()
        val account =
            accountRepository.save(Account(name = "Test", document = "123", phone = "987654321")).copy(name = "Updated")

        mockMvc.perform(
            MockMvcRequestBuilders.delete("/accounts/${account.id}")
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andDo(MockMvcResultHandlers.print())

        val findById = accountRepository.findById(account.id!!)
        Assertions.assertFalse(findById.isPresent)
    }

    @Test
    fun `test create account validation error empty name`() {
        val account = Account(name = "", document = "123", phone = "987654321")
        val jsonAccount = ObjectMapper().writeValueAsString(account)
        accountRepository.deleteAll()
        mockMvc.perform(
            MockMvcRequestBuilders.post("/accounts")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonAccount)
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.statusCode").isNumber)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.message").isString)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.statusCode").value(400))
            .andExpect(MockMvcResultMatchers.jsonPath("\$.message").value("[name] nao pode estar em branco"))
    }

    @Test
    fun `test create account validation error name should have min 3 characters`() {
        val account = Account(name = "luiz", document = "123", phone = "987654321")
        val jsonAccount = ObjectMapper().writeValueAsString(account)
        accountRepository.deleteAll()
        mockMvc.perform(
            MockMvcRequestBuilders.post("/accounts")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonAccount)
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.statusCode").isNumber)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.message").isString)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.statusCode").value(400))
            .andExpect(MockMvcResultMatchers.jsonPath("\$.message").value("[name] minimo 5 caracteres"))
    }

    @Test
    fun `test create account validation error document should have 11 characters`() {
        val account = Account(name = "luiz6u", document = "123", phone = "123456")
        val jsonAccount = ObjectMapper().writeValueAsString(account)
        accountRepository.deleteAll()
        mockMvc.perform(
            MockMvcRequestBuilders.post("/accounts")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonAccount)
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.statusCode").isNumber)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.message").isString)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.statusCode").value(400))
            .andExpect(MockMvcResultMatchers.jsonPath("\$.message").value("[document] precisa ter 11 caracteres"))
    }

}