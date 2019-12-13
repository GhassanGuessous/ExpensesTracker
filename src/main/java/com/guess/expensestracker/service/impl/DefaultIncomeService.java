package com.guess.expensestracker.service.impl;

import com.guess.expensestracker.entity.Income;
import com.guess.expensestracker.repository.IncomeRepository;
import com.guess.expensestracker.service.IncomeService;
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
public class DefaultIncomeService implements IncomeService {

    private final Logger log = LoggerFactory.getLogger(DefaultIncomeService.class);

    private static final String ENTITY_NAME = "income";

    @Autowired
    private IncomeRepository incomeRepository;

    @Override
    public Income save(Income income) {
        try {
            income.setInitialDate(DateUtil.getCurrentDate());
            return incomeRepository.save(income);
        } catch (ParseException e) {
            throw new BadRequestAlertException("Invalid date", ENTITY_NAME);
        }
    }

    @Override
    public List<Income> findAll() {
        return incomeRepository.findAll();
    }

    @Override
    public Optional<Income> findOne(Long id) {
        return incomeRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        incomeRepository.deleteById(id);
    }
}
