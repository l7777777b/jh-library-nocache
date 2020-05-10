package com.jhtest.app.web.rest;

import com.jhtest.app.JhlibrarynocacheApp;
import com.jhtest.app.domain.WishList;
import com.jhtest.app.domain.Product;
import com.jhtest.app.domain.Customer;
import com.jhtest.app.repository.WishListRepository;
import com.jhtest.app.service.WishListService;
import com.jhtest.app.service.dto.WishListDTO;
import com.jhtest.app.service.mapper.WishListMapper;
import com.jhtest.app.service.dto.WishListCriteria;
import com.jhtest.app.service.WishListQueryService;

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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link WishListResource} REST controller.
 */
@SpringBootTest(classes = JhlibrarynocacheApp.class)

@AutoConfigureMockMvc
@WithMockUser
public class WishListResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final Boolean DEFAULT_RESTRICTED = false;
    private static final Boolean UPDATED_RESTRICTED = true;

    @Autowired
    private WishListRepository wishListRepository;

    @Autowired
    private WishListMapper wishListMapper;

    @Autowired
    private WishListService wishListService;

    @Autowired
    private WishListQueryService wishListQueryService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restWishListMockMvc;

    private WishList wishList;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static WishList createEntity(EntityManager em) {
        WishList wishList = new WishList()
            .title(DEFAULT_TITLE)
            .restricted(DEFAULT_RESTRICTED);
        return wishList;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static WishList createUpdatedEntity(EntityManager em) {
        WishList wishList = new WishList()
            .title(UPDATED_TITLE)
            .restricted(UPDATED_RESTRICTED);
        return wishList;
    }

    @BeforeEach
    public void initTest() {
        wishList = createEntity(em);
    }

    @Test
    @Transactional
    public void createWishList() throws Exception {
        int databaseSizeBeforeCreate = wishListRepository.findAll().size();

        // Create the WishList
        WishListDTO wishListDTO = wishListMapper.toDto(wishList);
        restWishListMockMvc.perform(post("/api/wish-lists")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(wishListDTO)))
            .andExpect(status().isCreated());

        // Validate the WishList in the database
        List<WishList> wishListList = wishListRepository.findAll();
        assertThat(wishListList).hasSize(databaseSizeBeforeCreate + 1);
        WishList testWishList = wishListList.get(wishListList.size() - 1);
        assertThat(testWishList.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testWishList.isRestricted()).isEqualTo(DEFAULT_RESTRICTED);
    }

    @Test
    @Transactional
    public void createWishListWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = wishListRepository.findAll().size();

        // Create the WishList with an existing ID
        wishList.setId(1L);
        WishListDTO wishListDTO = wishListMapper.toDto(wishList);

        // An entity with an existing ID cannot be created, so this API call must fail
        restWishListMockMvc.perform(post("/api/wish-lists")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(wishListDTO)))
            .andExpect(status().isBadRequest());

        // Validate the WishList in the database
        List<WishList> wishListList = wishListRepository.findAll();
        assertThat(wishListList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void checkTitleIsRequired() throws Exception {
        int databaseSizeBeforeTest = wishListRepository.findAll().size();
        // set the field null
        wishList.setTitle(null);

        // Create the WishList, which fails.
        WishListDTO wishListDTO = wishListMapper.toDto(wishList);

        restWishListMockMvc.perform(post("/api/wish-lists")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(wishListDTO)))
            .andExpect(status().isBadRequest());

        List<WishList> wishListList = wishListRepository.findAll();
        assertThat(wishListList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllWishLists() throws Exception {
        // Initialize the database
        wishListRepository.saveAndFlush(wishList);

        // Get all the wishListList
        restWishListMockMvc.perform(get("/api/wish-lists?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(wishList.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].restricted").value(hasItem(DEFAULT_RESTRICTED.booleanValue())));
    }
    
    @Test
    @Transactional
    public void getWishList() throws Exception {
        // Initialize the database
        wishListRepository.saveAndFlush(wishList);

        // Get the wishList
        restWishListMockMvc.perform(get("/api/wish-lists/{id}", wishList.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(wishList.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.restricted").value(DEFAULT_RESTRICTED.booleanValue()));
    }


    @Test
    @Transactional
    public void getWishListsByIdFiltering() throws Exception {
        // Initialize the database
        wishListRepository.saveAndFlush(wishList);

        Long id = wishList.getId();

        defaultWishListShouldBeFound("id.equals=" + id);
        defaultWishListShouldNotBeFound("id.notEquals=" + id);

        defaultWishListShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultWishListShouldNotBeFound("id.greaterThan=" + id);

        defaultWishListShouldBeFound("id.lessThanOrEqual=" + id);
        defaultWishListShouldNotBeFound("id.lessThan=" + id);
    }


    @Test
    @Transactional
    public void getAllWishListsByTitleIsEqualToSomething() throws Exception {
        // Initialize the database
        wishListRepository.saveAndFlush(wishList);

        // Get all the wishListList where title equals to DEFAULT_TITLE
        defaultWishListShouldBeFound("title.equals=" + DEFAULT_TITLE);

        // Get all the wishListList where title equals to UPDATED_TITLE
        defaultWishListShouldNotBeFound("title.equals=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    public void getAllWishListsByTitleIsNotEqualToSomething() throws Exception {
        // Initialize the database
        wishListRepository.saveAndFlush(wishList);

        // Get all the wishListList where title not equals to DEFAULT_TITLE
        defaultWishListShouldNotBeFound("title.notEquals=" + DEFAULT_TITLE);

        // Get all the wishListList where title not equals to UPDATED_TITLE
        defaultWishListShouldBeFound("title.notEquals=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    public void getAllWishListsByTitleIsInShouldWork() throws Exception {
        // Initialize the database
        wishListRepository.saveAndFlush(wishList);

        // Get all the wishListList where title in DEFAULT_TITLE or UPDATED_TITLE
        defaultWishListShouldBeFound("title.in=" + DEFAULT_TITLE + "," + UPDATED_TITLE);

        // Get all the wishListList where title equals to UPDATED_TITLE
        defaultWishListShouldNotBeFound("title.in=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    public void getAllWishListsByTitleIsNullOrNotNull() throws Exception {
        // Initialize the database
        wishListRepository.saveAndFlush(wishList);

        // Get all the wishListList where title is not null
        defaultWishListShouldBeFound("title.specified=true");

        // Get all the wishListList where title is null
        defaultWishListShouldNotBeFound("title.specified=false");
    }
                @Test
    @Transactional
    public void getAllWishListsByTitleContainsSomething() throws Exception {
        // Initialize the database
        wishListRepository.saveAndFlush(wishList);

        // Get all the wishListList where title contains DEFAULT_TITLE
        defaultWishListShouldBeFound("title.contains=" + DEFAULT_TITLE);

        // Get all the wishListList where title contains UPDATED_TITLE
        defaultWishListShouldNotBeFound("title.contains=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    public void getAllWishListsByTitleNotContainsSomething() throws Exception {
        // Initialize the database
        wishListRepository.saveAndFlush(wishList);

        // Get all the wishListList where title does not contain DEFAULT_TITLE
        defaultWishListShouldNotBeFound("title.doesNotContain=" + DEFAULT_TITLE);

        // Get all the wishListList where title does not contain UPDATED_TITLE
        defaultWishListShouldBeFound("title.doesNotContain=" + UPDATED_TITLE);
    }


    @Test
    @Transactional
    public void getAllWishListsByRestrictedIsEqualToSomething() throws Exception {
        // Initialize the database
        wishListRepository.saveAndFlush(wishList);

        // Get all the wishListList where restricted equals to DEFAULT_RESTRICTED
        defaultWishListShouldBeFound("restricted.equals=" + DEFAULT_RESTRICTED);

        // Get all the wishListList where restricted equals to UPDATED_RESTRICTED
        defaultWishListShouldNotBeFound("restricted.equals=" + UPDATED_RESTRICTED);
    }

    @Test
    @Transactional
    public void getAllWishListsByRestrictedIsNotEqualToSomething() throws Exception {
        // Initialize the database
        wishListRepository.saveAndFlush(wishList);

        // Get all the wishListList where restricted not equals to DEFAULT_RESTRICTED
        defaultWishListShouldNotBeFound("restricted.notEquals=" + DEFAULT_RESTRICTED);

        // Get all the wishListList where restricted not equals to UPDATED_RESTRICTED
        defaultWishListShouldBeFound("restricted.notEquals=" + UPDATED_RESTRICTED);
    }

    @Test
    @Transactional
    public void getAllWishListsByRestrictedIsInShouldWork() throws Exception {
        // Initialize the database
        wishListRepository.saveAndFlush(wishList);

        // Get all the wishListList where restricted in DEFAULT_RESTRICTED or UPDATED_RESTRICTED
        defaultWishListShouldBeFound("restricted.in=" + DEFAULT_RESTRICTED + "," + UPDATED_RESTRICTED);

        // Get all the wishListList where restricted equals to UPDATED_RESTRICTED
        defaultWishListShouldNotBeFound("restricted.in=" + UPDATED_RESTRICTED);
    }

    @Test
    @Transactional
    public void getAllWishListsByRestrictedIsNullOrNotNull() throws Exception {
        // Initialize the database
        wishListRepository.saveAndFlush(wishList);

        // Get all the wishListList where restricted is not null
        defaultWishListShouldBeFound("restricted.specified=true");

        // Get all the wishListList where restricted is null
        defaultWishListShouldNotBeFound("restricted.specified=false");
    }

    @Test
    @Transactional
    public void getAllWishListsByProductIsEqualToSomething() throws Exception {
        // Initialize the database
        wishListRepository.saveAndFlush(wishList);
        Product product = ProductResourceIT.createEntity(em);
        em.persist(product);
        em.flush();
        wishList.addProduct(product);
        wishListRepository.saveAndFlush(wishList);
        Long productId = product.getId();

        // Get all the wishListList where product equals to productId
        defaultWishListShouldBeFound("productId.equals=" + productId);

        // Get all the wishListList where product equals to productId + 1
        defaultWishListShouldNotBeFound("productId.equals=" + (productId + 1));
    }


    @Test
    @Transactional
    public void getAllWishListsByCustomerIsEqualToSomething() throws Exception {
        // Initialize the database
        wishListRepository.saveAndFlush(wishList);
        Customer customer = CustomerResourceIT.createEntity(em);
        em.persist(customer);
        em.flush();
        wishList.setCustomer(customer);
        wishListRepository.saveAndFlush(wishList);
        Long customerId = customer.getId();

        // Get all the wishListList where customer equals to customerId
        defaultWishListShouldBeFound("customerId.equals=" + customerId);

        // Get all the wishListList where customer equals to customerId + 1
        defaultWishListShouldNotBeFound("customerId.equals=" + (customerId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultWishListShouldBeFound(String filter) throws Exception {
        restWishListMockMvc.perform(get("/api/wish-lists?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(wishList.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].restricted").value(hasItem(DEFAULT_RESTRICTED.booleanValue())));

        // Check, that the count call also returns 1
        restWishListMockMvc.perform(get("/api/wish-lists/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultWishListShouldNotBeFound(String filter) throws Exception {
        restWishListMockMvc.perform(get("/api/wish-lists?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restWishListMockMvc.perform(get("/api/wish-lists/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }


    @Test
    @Transactional
    public void getNonExistingWishList() throws Exception {
        // Get the wishList
        restWishListMockMvc.perform(get("/api/wish-lists/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateWishList() throws Exception {
        // Initialize the database
        wishListRepository.saveAndFlush(wishList);

        int databaseSizeBeforeUpdate = wishListRepository.findAll().size();

        // Update the wishList
        WishList updatedWishList = wishListRepository.findById(wishList.getId()).get();
        // Disconnect from session so that the updates on updatedWishList are not directly saved in db
        em.detach(updatedWishList);
        updatedWishList
            .title(UPDATED_TITLE)
            .restricted(UPDATED_RESTRICTED);
        WishListDTO wishListDTO = wishListMapper.toDto(updatedWishList);

        restWishListMockMvc.perform(put("/api/wish-lists")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(wishListDTO)))
            .andExpect(status().isOk());

        // Validate the WishList in the database
        List<WishList> wishListList = wishListRepository.findAll();
        assertThat(wishListList).hasSize(databaseSizeBeforeUpdate);
        WishList testWishList = wishListList.get(wishListList.size() - 1);
        assertThat(testWishList.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testWishList.isRestricted()).isEqualTo(UPDATED_RESTRICTED);
    }

    @Test
    @Transactional
    public void updateNonExistingWishList() throws Exception {
        int databaseSizeBeforeUpdate = wishListRepository.findAll().size();

        // Create the WishList
        WishListDTO wishListDTO = wishListMapper.toDto(wishList);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restWishListMockMvc.perform(put("/api/wish-lists")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(wishListDTO)))
            .andExpect(status().isBadRequest());

        // Validate the WishList in the database
        List<WishList> wishListList = wishListRepository.findAll();
        assertThat(wishListList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteWishList() throws Exception {
        // Initialize the database
        wishListRepository.saveAndFlush(wishList);

        int databaseSizeBeforeDelete = wishListRepository.findAll().size();

        // Delete the wishList
        restWishListMockMvc.perform(delete("/api/wish-lists/{id}", wishList.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<WishList> wishListList = wishListRepository.findAll();
        assertThat(wishListList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
