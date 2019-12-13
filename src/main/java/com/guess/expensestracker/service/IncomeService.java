package com.guess.expensestracker.service;

import com.guess.expensestracker.entity.Income;

import java.util.List;
import java.util.Optional;

public interface IncomeService {
    Income save(Income income);

    List<Income> findAll();

    Optional<Income> findOne(Long id);

    void delete(Long id);
}
