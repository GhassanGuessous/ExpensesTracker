package com.guess.expensestracker.service.impl;

import com.guess.expensestracker.entity.Expense;
import com.guess.expensestracker.repository.ExpenseRepository;
import com.guess.expensestracker.service.ExpenseService;
import com.guess.expensestracker.service.util.DateUtil;
import com.guess.expensestracker.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.List;
import java.util.Optional;

@Service
public class DefaultExpenseService implements ExpenseService {

    private final Logger log = LoggerFactory.getLogger(DefaultExpenseService.class);

    private static final String ENTITY_NAME = "expense";

    @Autowired
    private ExpenseRepository expenseRepository;

    @Override
    public Expense save(Expense expense) {
        try {
            expense.setInitialDate(DateUtil.getCurrentDate());
            return expenseRepository.save(expense);
        } catch (ParseException e) {
            throw new BadRequestAlertException("Invalid date", ENTITY_NAME);
        }
    }

    @Override
    public List<Expense> findAll() {
        return expenseRepository.findAll();
    }

    @Override
    public Optional<Expense> findOne(Long id) {
        return expenseRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        expenseRepository.deleteById(id);
    }
}
