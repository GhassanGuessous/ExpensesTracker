package com.guess.expensestracker.service;

import com.guess.expensestracker.entity.Expense;

import java.util.List;
import java.util.Optional;

public interface ExpenseService {
    Expense save(Expense expense);

    List<Expense> findAll();

    Optional<Expense> findOne(Long id);

    void delete(Long id);

}
