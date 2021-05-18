package com.hotmart.bankapi.repository

import com.hotmart.bankapi.model.Account
import org.springframework.data.jpa.repository.JpaRepository

interface AccountRepository : JpaRepository<Account, Long> {
}