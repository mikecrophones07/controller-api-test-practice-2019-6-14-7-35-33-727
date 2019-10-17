package com.tw.api.unit.test.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tw.api.unit.test.domain.todo.Todo;
import com.tw.api.unit.test.domain.todo.TodoRepository;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(TodoController.class)
@ActiveProfiles(profiles = "test")
class TodoControllerTest {

    @Autowired
    private TodoController todoController;

    @MockBean
    private TodoRepository repository;

    @Autowired
    private MockMvc mvc;

    private List<Todo> toDoList;

    @Before
    public void setUp(){
        toDoList = new ArrayList<>();
    }

    @Test
    void should_return_ok_when_get_all_todo_list() throws Exception {
        //given
        when(repository.getAll()).thenReturn(toDoList);
        //when
        ResultActions result = mvc.perform(get("/todos"));
        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$", is(toDoList)));
    }

    @Test
    void should_return_json_todo_when_get_todo_is_called() throws Exception {
        Todo todo = new Todo(1, "Hello",  true, 1);
        Optional<Todo> optToDoList = Optional.of(todo);
        when(repository.findById(1)).thenReturn(optToDoList);
        //when
        ResultActions result = mvc.perform(get("/todos/1"));
        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Hello")))
                .andExpect(jsonPath("$.completed", is(true)));
    }

    @Test
    void should_return_status_created_when_save_todo_is_called() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Todo todo = new Todo(1, "Hello",  true, 1);

        when(repository.getAll()).thenReturn(new ArrayList<>(Arrays.asList(todo)));

        ResultActions result = mvc.perform(post("/todos")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(todo)));

        result.andExpect(status().isCreated());
    }

}