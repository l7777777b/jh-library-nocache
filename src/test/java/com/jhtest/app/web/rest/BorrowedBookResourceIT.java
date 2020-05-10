package com.jhtest.app.web.rest;

import com.jhtest.app.JhlibrarynocacheApp;
import com.jhtest.app.domain.BorrowedBook;
import com.jhtest.app.domain.Book;
import com.jhtest.app.domain.Client;
import com.jhtest.app.repository.BorrowedBookRepository;
import com.jhtest.app.service.BorrowedBookService;
import com.jhtest.app.service.dto.BorrowedBookDTO;
import com.jhtest.app.service.mapper.BorrowedBookMapper;
import com.jhtest.app.service.dto.BorrowedBookCriteria;
import com.jhtest.app.service.BorrowedBookQueryService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link BorrowedBookResource} REST controller.
 */
@SpringBootTest(classes = JhlibrarynocacheApp.class)

@AutoConfigureMockMvc
@WithMockUser
public class BorrowedBookResourceIT {

    private static final LocalDate DEFAULT_BORROW_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_BORROW_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_BORROW_DATE = LocalDate.ofEpochDay(-1L);

    @Autowired
    private BorrowedBookRepository borrowedBookRepository;

    @Autowired
    private BorrowedBookMapper borrowedBookMapper;

    @Autowired
    private BorrowedBookService borrowedBookService;

    @Autowired
    private BorrowedBookQueryService borrowedBookQueryService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restBorrowedBookMockMvc;

    private BorrowedBook borrowedBook;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BorrowedBook createEntity(EntityManager em) {
        BorrowedBook borrowedBook = new BorrowedBook()
            .borrowDate(DEFAULT_BORROW_DATE);
        return borrowedBook;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BorrowedBook createUpdatedEntity(EntityManager em) {
        BorrowedBook borrowedBook = new BorrowedBook()
            .borrowDate(UPDATED_BORROW_DATE);
        return borrowedBook;
    }

    @BeforeEach
    public void initTest() {
        borrowedBook = createEntity(em);
    }

    @Test
    @Transactional
    public void createBorrowedBook() throws Exception {
        int databaseSizeBeforeCreate = borrowedBookRepository.findAll().size();

        // Create the BorrowedBook
        BorrowedBookDTO borrowedBookDTO = borrowedBookMapper.toDto(borrowedBook);
        restBorrowedBookMockMvc.perform(post("/api/borrowed-books")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(borrowedBookDTO)))
            .andExpect(status().isCreated());

        // Validate the BorrowedBook in the database
        List<BorrowedBook> borrowedBookList = borrowedBookRepository.findAll();
        assertThat(borrowedBookList).hasSize(databaseSizeBeforeCreate + 1);
        BorrowedBook testBorrowedBook = borrowedBookList.get(borrowedBookList.size() - 1);
        assertThat(testBorrowedBook.getBorrowDate()).isEqualTo(DEFAULT_BORROW_DATE);
    }

    @Test
    @Transactional
    public void createBorrowedBookWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = borrowedBookRepository.findAll().size();

        // Create the BorrowedBook with an existing ID
        borrowedBook.setId(1L);
        BorrowedBookDTO borrowedBookDTO = borrowedBookMapper.toDto(borrowedBook);

        // An entity with an existing ID cannot be created, so this API call must fail
        restBorrowedBookMockMvc.perform(post("/api/borrowed-books")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(borrowedBookDTO)))
            .andExpect(status().isBadRequest());

        // Validate the BorrowedBook in the database
        List<BorrowedBook> borrowedBookList = borrowedBookRepository.findAll();
        assertThat(borrowedBookList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void getAllBorrowedBooks() throws Exception {
        // Initialize the database
        borrowedBookRepository.saveAndFlush(borrowedBook);

        // Get all the borrowedBookList
        restBorrowedBookMockMvc.perform(get("/api/borrowed-books?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(borrowedBook.getId().intValue())))
            .andExpect(jsonPath("$.[*].borrowDate").value(hasItem(DEFAULT_BORROW_DATE.toString())));
    }
    
    @Test
    @Transactional
    public void getBorrowedBook() throws Exception {
        // Initialize the database
        borrowedBookRepository.saveAndFlush(borrowedBook);

        // Get the borrowedBook
        restBorrowedBookMockMvc.perform(get("/api/borrowed-books/{id}", borrowedBook.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(borrowedBook.getId().intValue()))
            .andExpect(jsonPath("$.borrowDate").value(DEFAULT_BORROW_DATE.toString()));
    }


    @Test
    @Transactional
    public void getBorrowedBooksByIdFiltering() throws Exception {
        // Initialize the database
        borrowedBookRepository.saveAndFlush(borrowedBook);

        Long id = borrowedBook.getId();

        defaultBorrowedBookShouldBeFound("id.equals=" + id);
        defaultBorrowedBookShouldNotBeFound("id.notEquals=" + id);

        defaultBorrowedBookShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultBorrowedBookShouldNotBeFound("id.greaterThan=" + id);

        defaultBorrowedBookShouldBeFound("id.lessThanOrEqual=" + id);
        defaultBorrowedBookShouldNotBeFound("id.lessThan=" + id);
    }


    @Test
    @Transactional
    public void getAllBorrowedBooksByBorrowDateIsEqualToSomething() throws Exception {
        // Initialize the database
        borrowedBookRepository.saveAndFlush(borrowedBook);

        // Get all the borrowedBookList where borrowDate equals to DEFAULT_BORROW_DATE
        defaultBorrowedBookShouldBeFound("borrowDate.equals=" + DEFAULT_BORROW_DATE);

        // Get all the borrowedBookList where borrowDate equals to UPDATED_BORROW_DATE
        defaultBorrowedBookShouldNotBeFound("borrowDate.equals=" + UPDATED_BORROW_DATE);
    }

    @Test
    @Transactional
    public void getAllBorrowedBooksByBorrowDateIsNotEqualToSomething() throws Exception {
        // Initialize the database
        borrowedBookRepository.saveAndFlush(borrowedBook);

        // Get all the borrowedBookList where borrowDate not equals to DEFAULT_BORROW_DATE
        defaultBorrowedBookShouldNotBeFound("borrowDate.notEquals=" + DEFAULT_BORROW_DATE);

        // Get all the borrowedBookList where borrowDate not equals to UPDATED_BORROW_DATE
        defaultBorrowedBookShouldBeFound("borrowDate.notEquals=" + UPDATED_BORROW_DATE);
    }

    @Test
    @Transactional
    public void getAllBorrowedBooksByBorrowDateIsInShouldWork() throws Exception {
        // Initialize the database
        borrowedBookRepository.saveAndFlush(borrowedBook);

        // Get all the borrowedBookList where borrowDate in DEFAULT_BORROW_DATE or UPDATED_BORROW_DATE
        defaultBorrowedBookShouldBeFound("borrowDate.in=" + DEFAULT_BORROW_DATE + "," + UPDATED_BORROW_DATE);

        // Get all the borrowedBookList where borrowDate equals to UPDATED_BORROW_DATE
        defaultBorrowedBookShouldNotBeFound("borrowDate.in=" + UPDATED_BORROW_DATE);
    }

    @Test
    @Transactional
    public void getAllBorrowedBooksByBorrowDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        borrowedBookRepository.saveAndFlush(borrowedBook);

        // Get all the borrowedBookList where borrowDate is not null
        defaultBorrowedBookShouldBeFound("borrowDate.specified=true");

        // Get all the borrowedBookList where borrowDate is null
        defaultBorrowedBookShouldNotBeFound("borrowDate.specified=false");
    }

    @Test
    @Transactional
    public void getAllBorrowedBooksByBorrowDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        borrowedBookRepository.saveAndFlush(borrowedBook);

        // Get all the borrowedBookList where borrowDate is greater than or equal to DEFAULT_BORROW_DATE
        defaultBorrowedBookShouldBeFound("borrowDate.greaterThanOrEqual=" + DEFAULT_BORROW_DATE);

        // Get all the borrowedBookList where borrowDate is greater than or equal to UPDATED_BORROW_DATE
        defaultBorrowedBookShouldNotBeFound("borrowDate.greaterThanOrEqual=" + UPDATED_BORROW_DATE);
    }

    @Test
    @Transactional
    public void getAllBorrowedBooksByBorrowDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        borrowedBookRepository.saveAndFlush(borrowedBook);

        // Get all the borrowedBookList where borrowDate is less than or equal to DEFAULT_BORROW_DATE
        defaultBorrowedBookShouldBeFound("borrowDate.lessThanOrEqual=" + DEFAULT_BORROW_DATE);

        // Get all the borrowedBookList where borrowDate is less than or equal to SMALLER_BORROW_DATE
        defaultBorrowedBookShouldNotBeFound("borrowDate.lessThanOrEqual=" + SMALLER_BORROW_DATE);
    }

    @Test
    @Transactional
    public void getAllBorrowedBooksByBorrowDateIsLessThanSomething() throws Exception {
        // Initialize the database
        borrowedBookRepository.saveAndFlush(borrowedBook);

        // Get all the borrowedBookList where borrowDate is less than DEFAULT_BORROW_DATE
        defaultBorrowedBookShouldNotBeFound("borrowDate.lessThan=" + DEFAULT_BORROW_DATE);

        // Get all the borrowedBookList where borrowDate is less than UPDATED_BORROW_DATE
        defaultBorrowedBookShouldBeFound("borrowDate.lessThan=" + UPDATED_BORROW_DATE);
    }

    @Test
    @Transactional
    public void getAllBorrowedBooksByBorrowDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        borrowedBookRepository.saveAndFlush(borrowedBook);

        // Get all the borrowedBookList where borrowDate is greater than DEFAULT_BORROW_DATE
        defaultBorrowedBookShouldNotBeFound("borrowDate.greaterThan=" + DEFAULT_BORROW_DATE);

        // Get all the borrowedBookList where borrowDate is greater than SMALLER_BORROW_DATE
        defaultBorrowedBookShouldBeFound("borrowDate.greaterThan=" + SMALLER_BORROW_DATE);
    }


    @Test
    @Transactional
    public void getAllBorrowedBooksByBookIsEqualToSomething() throws Exception {
        // Initialize the database
        borrowedBookRepository.saveAndFlush(borrowedBook);
        Book book = BookResourceIT.createEntity(em);
        em.persist(book);
        em.flush();
        borrowedBook.setBook(book);
        borrowedBookRepository.saveAndFlush(borrowedBook);
        Long bookId = book.getId();

        // Get all the borrowedBookList where book equals to bookId
        defaultBorrowedBookShouldBeFound("bookId.equals=" + bookId);

        // Get all the borrowedBookList where book equals to bookId + 1
        defaultBorrowedBookShouldNotBeFound("bookId.equals=" + (bookId + 1));
    }


    @Test
    @Transactional
    public void getAllBorrowedBooksByClientIsEqualToSomething() throws Exception {
        // Initialize the database
        borrowedBookRepository.saveAndFlush(borrowedBook);
        Client client = ClientResourceIT.createEntity(em);
        em.persist(client);
        em.flush();
        borrowedBook.setClient(client);
        borrowedBookRepository.saveAndFlush(borrowedBook);
        Long clientId = client.getId();

        // Get all the borrowedBookList where client equals to clientId
        defaultBorrowedBookShouldBeFound("clientId.equals=" + clientId);

        // Get all the borrowedBookList where client equals to clientId + 1
        defaultBorrowedBookShouldNotBeFound("clientId.equals=" + (clientId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultBorrowedBookShouldBeFound(String filter) throws Exception {
        restBorrowedBookMockMvc.perform(get("/api/borrowed-books?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(borrowedBook.getId().intValue())))
            .andExpect(jsonPath("$.[*].borrowDate").value(hasItem(DEFAULT_BORROW_DATE.toString())));

        // Check, that the count call also returns 1
        restBorrowedBookMockMvc.perform(get("/api/borrowed-books/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultBorrowedBookShouldNotBeFound(String filter) throws Exception {
        restBorrowedBookMockMvc.perform(get("/api/borrowed-books?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restBorrowedBookMockMvc.perform(get("/api/borrowed-books/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }


    @Test
    @Transactional
    public void getNonExistingBorrowedBook() throws Exception {
        // Get the borrowedBook
        restBorrowedBookMockMvc.perform(get("/api/borrowed-books/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateBorrowedBook() throws Exception {
        // Initialize the database
        borrowedBookRepository.saveAndFlush(borrowedBook);

        int databaseSizeBeforeUpdate = borrowedBookRepository.findAll().size();

        // Update the borrowedBook
        BorrowedBook updatedBorrowedBook = borrowedBookRepository.findById(borrowedBook.getId()).get();
        // Disconnect from session so that the updates on updatedBorrowedBook are not directly saved in db
        em.detach(updatedBorrowedBook);
        updatedBorrowedBook
            .borrowDate(UPDATED_BORROW_DATE);
        BorrowedBookDTO borrowedBookDTO = borrowedBookMapper.toDto(updatedBorrowedBook);

        restBorrowedBookMockMvc.perform(put("/api/borrowed-books")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(borrowedBookDTO)))
            .andExpect(status().isOk());

        // Validate the BorrowedBook in the database
        List<BorrowedBook> borrowedBookList = borrowedBookRepository.findAll();
        assertThat(borrowedBookList).hasSize(databaseSizeBeforeUpdate);
        BorrowedBook testBorrowedBook = borrowedBookList.get(borrowedBookList.size() - 1);
        assertThat(testBorrowedBook.getBorrowDate()).isEqualTo(UPDATED_BORROW_DATE);
    }

    @Test
    @Transactional
    public void updateNonExistingBorrowedBook() throws Exception {
        int databaseSizeBeforeUpdate = borrowedBookRepository.findAll().size();

        // Create the BorrowedBook
        BorrowedBookDTO borrowedBookDTO = borrowedBookMapper.toDto(borrowedBook);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBorrowedBookMockMvc.perform(put("/api/borrowed-books")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(borrowedBookDTO)))
            .andExpect(status().isBadRequest());

        // Validate the BorrowedBook in the database
        List<BorrowedBook> borrowedBookList = borrowedBookRepository.findAll();
        assertThat(borrowedBookList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteBorrowedBook() throws Exception {
        // Initialize the database
        borrowedBookRepository.saveAndFlush(borrowedBook);

        int databaseSizeBeforeDelete = borrowedBookRepository.findAll().size();

        // Delete the borrowedBook
        restBorrowedBookMockMvc.perform(delete("/api/borrowed-books/{id}", borrowedBook.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<BorrowedBook> borrowedBookList = borrowedBookRepository.findAll();
        assertThat(borrowedBookList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
