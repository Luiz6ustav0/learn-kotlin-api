package com.hotmart.bankapi.service

import com.hotmart.bankapi.model.Account
import com.hotmart.bankapi.repository.AccountRepository
import org.springframework.stereotype.Service
import org.springframework.util.Assert
import java.util.*

@Service
class AccountServiceImpl(private val repository: AccountRepository) : AccountService {
    override fun create(account: Account): Account {
        Assert.hasLength(account.name, "[name] nao pode estar em branco")
        Assert.isTrue(account.name.length >= 5, "[name] minimo 5 caracteres")
        Assert.isTrue(account.document.length == 11, "[document] precisa ter 11 caracteres")
        return repository.save(account)
    }

    override fun getAll(): List<Account> {
        return repository.findAll()
    }

    override fun getById(id: Long): Optional<Account> {
        return repository.findById(id)
    }

    override fun update(id: Long, account: Account): Optional<Account> {
        val optional = getById(id)
        if (optional.isEmpty) Optional.empty<Account>()
        return optional.map {
            val accountToUpdate = it.copy(
                name = account.name, document = account.document, phone = account.phone
            )
            repository.save(accountToUpdate)
        }
    }

    override fun delete(id: Long) {
        repository.findById(id).map {
            repository.delete(it)
        }.orElseThrow { throw RuntimeException("ID Not Found") }
    }
}