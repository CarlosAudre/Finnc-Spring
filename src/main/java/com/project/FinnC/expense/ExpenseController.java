package com.project.FinnC.expense;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/{id}/expense")
public class ExpenseController {
    @Autowired
    ExpenseService expenseService;

    @PostMapping
    ResponseEntity<ExpenseDto> createExpense(
            @RequestBody @Valid ExpenseDto expenseDto,
            @PathVariable Long id
    ){
        ExpenseDto expense = expenseService.createExpense(expenseDto, id);
        return ResponseEntity.ok(expense);
    }

    @PutMapping("/{expenseId}")
    ResponseEntity<ExpenseDto> updateExpense(
            @RequestBody @Valid ExpenseDto expenseDto,
            @PathVariable Long expenseId
    ){
        ExpenseDto expense = expenseService.updateExpense(expenseDto, expenseId);
        return ResponseEntity.ok(expense);
    }

    @DeleteMapping("/{expenseId}")
    ResponseEntity<Void> deleteExpense(@PathVariable Long expenseId){
        expenseService.deleteExpense(expenseId);
        return ResponseEntity.noContent().build();
    }
}
