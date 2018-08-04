package com.test.api.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.api.domain.Account;
import com.test.api.service.AccountService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
public class AccountControllerTest {
    MockMvc mockMvc;
    AccountController accountController;
    @Mock
    AccountService accountService;

    ObjectMapper objectMapper = new ObjectMapper();

    @Before
    public void setUp(){
        accountController = new AccountController(accountService);
        mockMvc = MockMvcBuilders.standaloneSetup(accountController).build();
    }

    @Test
    public void shouldValidateAccountNumber() throws Exception{
        String accountNumber = "$#)@";
        mockMvc.perform(get("/api/accounts").param("account-number",accountNumber)).andExpect(status().isBadRequest());
    }
    @Test
    public void shouldValidateAccountName() throws Exception{
        String name = "abc1";
        mockMvc.perform(get("/api/accounts").param("name",name)).andExpect(status().isBadRequest());
    }
    @Test
    public void shouldCallAccountService() throws Exception {
        String name = "test";
        String accountNumber = null;
        mockMvc.perform(get("/api/accounts").param("name","test")).andExpect(status().isOk());
        verify(accountService).getAccounts(name,accountNumber);
    }
    @Test
    public void shouldReturnAccountDetails() throws Exception{
        String name = "test";
        Account account =  new Account(null,"12","abc","CASH",true);
        when(accountService.getAccounts(any(),any())).thenReturn(Collections.singletonList(account));
        MvcResult mvcResult = mockMvc.perform(get("/api/accounts")
                .param("name","test")).andExpect(status().isOk()).andReturn();

        JsonNode jsonNode = objectMapper.readTree(mvcResult.getResponse().getContentAsString());
        assertThat(jsonNode).isEqualTo(objectMapper.valueToTree(Collections.singleton(account)));
    }

    @Test
    public void shouldAddAccount() throws Exception{
        Account account =  new Account(null,"12","abc","CASH",true);
        ObjectMapper objectMapper = new ObjectMapper();
        when(accountService.addAccount(any())).thenReturn(account.withId("123"));
        MvcResult mvcResult = mockMvc.perform(post("/api/accounts")
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(account))).andExpect(status().isCreated()).andReturn();
        Account accountActual = objectMapper.readValue(mvcResult.getResponse().getContentAsString(),Account.class);
        verify(accountService).addAccount(account);
        assertThat(accountActual).isEqualTo(account.withId("123"));
    }

    @Test
    public void shouldUpdateAccount() throws Exception{
        Account account =  new Account("123","12","abc","CASH",true);
        ObjectMapper objectMapper = new ObjectMapper();
         mockMvc.perform(put("/api/accounts/123")
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(account))).andExpect(status().isOk());
        verify(accountService).updateAccount(account);
    }

    @Test
    public void shouldDeleteAccount() throws Exception{
        String accountId = "123";
        ObjectMapper objectMapper = new ObjectMapper();
        mockMvc.perform(delete("/api/accounts/"+accountId)
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
        verify(accountService).deleteAccount(accountId);
    }
}