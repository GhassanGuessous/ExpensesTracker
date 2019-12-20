package com.guess.expensestracker.web.rest;

import com.guess.expensestracker.entity.Income;
import com.guess.expensestracker.service.IncomeService;
import com.guess.expensestracker.web.rest.errors.BadRequestAlertException;
import com.guess.expensestracker.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class IncomeResource {

    private final Logger log = LoggerFactory.getLogger(IncomeResource.class);

    private static final String ENTITY_NAME = "income";

    private IncomeService incomeService;

    public IncomeResource(IncomeService incomeService) {
        this.incomeService = incomeService;
    }

    @PostMapping("/incomes")
    public ResponseEntity<Income> createIncome(@RequestBody Income income) throws URISyntaxException {
        log.debug("REST request to save Income : {}", income);
        if(income.getId() != null) {
            throw new BadRequestAlertException("A new income cannot have already an Id!", ENTITY_NAME);
        }
        Income result = incomeService.save(income);
        return ResponseEntity.created(new URI("/api/incomes/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
                .body(result);
    }

    @PutMapping("/incomes")
    public ResponseEntity<Income> updateIncome(@RequestBody Income income) {
        log.debug("REST request to update Income : {}", income);
        if(income.getId() == null) {
            throw new BadRequestAlertException("Invalid Id!", ENTITY_NAME);
        }
        Income result = incomeService.save(income);
        return ResponseEntity.ok().body(result);
    }

    @GetMapping("/incomes")
    public ResponseEntity<List<Income>> getAllIncomes() {
        log.debug("REST request to get all incomes");
        List<Income> incomes = incomeService.findAll();
        return ResponseEntity.ok().body(incomes);
    }

    @GetMapping("/incomes/{id}")
    public ResponseEntity<Income> getIncome(@PathVariable Long id) {
        log.debug("REST request to get Income : {}", id);
        Optional<Income> income = incomeService.findOne(id);
        if(income.isPresent()) {
            return ResponseEntity.ok().body(income.get());
        } else {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/incomes/{id}")
    public ResponseEntity<Void> deleteIncome(@PathVariable Long id) {
        log.debug("REST request to delete Income : {}", id);
        incomeService.delete(id);
        return ResponseEntity.ok().build();
    }
}
