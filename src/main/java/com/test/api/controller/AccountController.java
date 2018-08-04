package com.test.api.controller;

import com.test.api.domain.Account;
import com.test.api.service.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private AccountService accountService;

    public AccountController(AccountService accountService) {

        this.accountService = accountService;
    }

    @GetMapping
    public ResponseEntity<java.util.List<Account>> getAccounts(@RequestParam(name = "name",required = false) String name, @RequestParam(name="account-number",required = false) String accountNumber){
        if(!isValidAccountNumber(accountNumber))return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if(!isValidName(name))return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(accountService.getAccounts(name,accountNumber),HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Account> addAccount(@RequestBody Account account){
        return new ResponseEntity<>(accountService.addAccount(account),HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity addAccount(@RequestBody Account account,@PathVariable(name = "id") String id){
        accountService.updateAccount(account);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    public ResponseEntity deleteAccount(@PathVariable(name = "id") String id){
        accountService.deleteAccount(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    private boolean isValidAccountNumber(String accountNumber){
        return Optional.ofNullable(accountNumber).map((str)-> str.matches("\\w+")).orElse(true);
    }
    private boolean isValidName(String name){
        return Optional.ofNullable(name).map((str)-> str.matches("[a-zA-Z]+")).orElse(true);
    }
}
