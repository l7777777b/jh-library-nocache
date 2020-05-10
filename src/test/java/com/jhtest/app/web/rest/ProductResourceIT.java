package com.jhtest.app.web.rest;

import com.jhtest.app.JhlibrarynocacheApp;
import com.jhtest.app.domain.Product;
import com.jhtest.app.domain.WishList;
import com.jhtest.app.domain.Category;
import com.jhtest.app.repository.ProductRepository;
import com.jhtest.app.service.ProductService;
import com.jhtest.app.service.dto.ProductDTO;
import com.jhtest.app.service.mapper.ProductMapper;
import com.jhtest.app.service.dto.ProductCriteria;
import com.jhtest.app.service.ProductQueryService;

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
 * Integration tests for the {@link ProductResource} REST controller.
 */
@SpringBootTest(classes = JhlibrarynocacheApp.class)

@AutoConfigureMockMvc
@WithMockUser
public class ProductResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_KEYWORDS = "AAAAAAAAAA";
    private static final String UPDATED_KEYWORDS = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Integer DEFAULT_RATING = 1;
    private static final Integer UPDATED_RATING = 2;
    private static final Integer SMALLER_RATING = 1 - 1;

    private static final LocalDate DEFAULT_DATE_ADDED = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE_ADDED = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_DATE_ADDED = LocalDate.ofEpochDay(-1L);

    private static final LocalDate DEFAULT_DATE_MODIFIED = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE_MODIFIED = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_DATE_MODIFIED = LocalDate.ofEpochDay(-1L);

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductQueryService productQueryService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restProductMockMvc;

    private Product product;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Product createEntity(EntityManager em) {
        Product product = new Product()
            .title(DEFAULT_TITLE)
            .keywords(DEFAULT_KEYWORDS)
            .description(DEFAULT_DESCRIPTION)
            .rating(DEFAULT_RATING)
            .dateAdded(DEFAULT_DATE_ADDED)
            .dateModified(DEFAULT_DATE_MODIFIED);
        return product;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Product createUpdatedEntity(EntityManager em) {
        Product product = new Product()
            .title(UPDATED_TITLE)
            .keywords(UPDATED_KEYWORDS)
            .description(UPDATED_DESCRIPTION)
            .rating(UPDATED_RATING)
            .dateAdded(UPDATED_DATE_ADDED)
            .dateModified(UPDATED_DATE_MODIFIED);
        return product;
    }

    @BeforeEach
    public void initTest() {
        product = createEntity(em);
    }

    @Test
    @Transactional
    public void createProduct() throws Exception {
        int databaseSizeBeforeCreate = productRepository.findAll().size();

        // Create the Product
        ProductDTO productDTO = productMapper.toDto(product);
        restProductMockMvc.perform(post("/api/products")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(productDTO)))
            .andExpect(status().isCreated());

        // Validate the Product in the database
        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeCreate + 1);
        Product testProduct = productList.get(productList.size() - 1);
        assertThat(testProduct.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testProduct.getKeywords()).isEqualTo(DEFAULT_KEYWORDS);
        assertThat(testProduct.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testProduct.getRating()).isEqualTo(DEFAULT_RATING);
        assertThat(testProduct.getDateAdded()).isEqualTo(DEFAULT_DATE_ADDED);
        assertThat(testProduct.getDateModified()).isEqualTo(DEFAULT_DATE_MODIFIED);
    }

    @Test
    @Transactional
    public void createProductWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = productRepository.findAll().size();

        // Create the Product with an existing ID
        product.setId(1L);
        ProductDTO productDTO = productMapper.toDto(product);

        // An entity with an existing ID cannot be created, so this API call must fail
        restProductMockMvc.perform(post("/api/products")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(productDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Product in the database
        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void checkTitleIsRequired() throws Exception {
        int databaseSizeBeforeTest = productRepository.findAll().size();
        // set the field null
        product.setTitle(null);

        // Create the Product, which fails.
        ProductDTO productDTO = productMapper.toDto(product);

        restProductMockMvc.perform(post("/api/products")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(productDTO)))
            .andExpect(status().isBadRequest());

        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllProducts() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList
        restProductMockMvc.perform(get("/api/products?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(product.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].keywords").value(hasItem(DEFAULT_KEYWORDS)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].rating").value(hasItem(DEFAULT_RATING)))
            .andExpect(jsonPath("$.[*].dateAdded").value(hasItem(DEFAULT_DATE_ADDED.toString())))
            .andExpect(jsonPath("$.[*].dateModified").value(hasItem(DEFAULT_DATE_MODIFIED.toString())));
    }
    
    @Test
    @Transactional
    public void getProduct() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get the product
        restProductMockMvc.perform(get("/api/products/{id}", product.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(product.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.keywords").value(DEFAULT_KEYWORDS))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.rating").value(DEFAULT_RATING))
            .andExpect(jsonPath("$.dateAdded").value(DEFAULT_DATE_ADDED.toString()))
            .andExpect(jsonPath("$.dateModified").value(DEFAULT_DATE_MODIFIED.toString()));
    }


    @Test
    @Transactional
    public void getProductsByIdFiltering() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        Long id = product.getId();

        defaultProductShouldBeFound("id.equals=" + id);
        defaultProductShouldNotBeFound("id.notEquals=" + id);

        defaultProductShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultProductShouldNotBeFound("id.greaterThan=" + id);

        defaultProductShouldBeFound("id.lessThanOrEqual=" + id);
        defaultProductShouldNotBeFound("id.lessThan=" + id);
    }


    @Test
    @Transactional
    public void getAllProductsByTitleIsEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where title equals to DEFAULT_TITLE
        defaultProductShouldBeFound("title.equals=" + DEFAULT_TITLE);

        // Get all the productList where title equals to UPDATED_TITLE
        defaultProductShouldNotBeFound("title.equals=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    public void getAllProductsByTitleIsNotEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where title not equals to DEFAULT_TITLE
        defaultProductShouldNotBeFound("title.notEquals=" + DEFAULT_TITLE);

        // Get all the productList where title not equals to UPDATED_TITLE
        defaultProductShouldBeFound("title.notEquals=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    public void getAllProductsByTitleIsInShouldWork() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where title in DEFAULT_TITLE or UPDATED_TITLE
        defaultProductShouldBeFound("title.in=" + DEFAULT_TITLE + "," + UPDATED_TITLE);

        // Get all the productList where title equals to UPDATED_TITLE
        defaultProductShouldNotBeFound("title.in=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    public void getAllProductsByTitleIsNullOrNotNull() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where title is not null
        defaultProductShouldBeFound("title.specified=true");

        // Get all the productList where title is null
        defaultProductShouldNotBeFound("title.specified=false");
    }
                @Test
    @Transactional
    public void getAllProductsByTitleContainsSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where title contains DEFAULT_TITLE
        defaultProductShouldBeFound("title.contains=" + DEFAULT_TITLE);

        // Get all the productList where title contains UPDATED_TITLE
        defaultProductShouldNotBeFound("title.contains=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    public void getAllProductsByTitleNotContainsSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where title does not contain DEFAULT_TITLE
        defaultProductShouldNotBeFound("title.doesNotContain=" + DEFAULT_TITLE);

        // Get all the productList where title does not contain UPDATED_TITLE
        defaultProductShouldBeFound("title.doesNotContain=" + UPDATED_TITLE);
    }


    @Test
    @Transactional
    public void getAllProductsByKeywordsIsEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where keywords equals to DEFAULT_KEYWORDS
        defaultProductShouldBeFound("keywords.equals=" + DEFAULT_KEYWORDS);

        // Get all the productList where keywords equals to UPDATED_KEYWORDS
        defaultProductShouldNotBeFound("keywords.equals=" + UPDATED_KEYWORDS);
    }

    @Test
    @Transactional
    public void getAllProductsByKeywordsIsNotEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where keywords not equals to DEFAULT_KEYWORDS
        defaultProductShouldNotBeFound("keywords.notEquals=" + DEFAULT_KEYWORDS);

        // Get all the productList where keywords not equals to UPDATED_KEYWORDS
        defaultProductShouldBeFound("keywords.notEquals=" + UPDATED_KEYWORDS);
    }

    @Test
    @Transactional
    public void getAllProductsByKeywordsIsInShouldWork() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where keywords in DEFAULT_KEYWORDS or UPDATED_KEYWORDS
        defaultProductShouldBeFound("keywords.in=" + DEFAULT_KEYWORDS + "," + UPDATED_KEYWORDS);

        // Get all the productList where keywords equals to UPDATED_KEYWORDS
        defaultProductShouldNotBeFound("keywords.in=" + UPDATED_KEYWORDS);
    }

    @Test
    @Transactional
    public void getAllProductsByKeywordsIsNullOrNotNull() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where keywords is not null
        defaultProductShouldBeFound("keywords.specified=true");

        // Get all the productList where keywords is null
        defaultProductShouldNotBeFound("keywords.specified=false");
    }
                @Test
    @Transactional
    public void getAllProductsByKeywordsContainsSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where keywords contains DEFAULT_KEYWORDS
        defaultProductShouldBeFound("keywords.contains=" + DEFAULT_KEYWORDS);

        // Get all the productList where keywords contains UPDATED_KEYWORDS
        defaultProductShouldNotBeFound("keywords.contains=" + UPDATED_KEYWORDS);
    }

    @Test
    @Transactional
    public void getAllProductsByKeywordsNotContainsSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where keywords does not contain DEFAULT_KEYWORDS
        defaultProductShouldNotBeFound("keywords.doesNotContain=" + DEFAULT_KEYWORDS);

        // Get all the productList where keywords does not contain UPDATED_KEYWORDS
        defaultProductShouldBeFound("keywords.doesNotContain=" + UPDATED_KEYWORDS);
    }


    @Test
    @Transactional
    public void getAllProductsByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where description equals to DEFAULT_DESCRIPTION
        defaultProductShouldBeFound("description.equals=" + DEFAULT_DESCRIPTION);

        // Get all the productList where description equals to UPDATED_DESCRIPTION
        defaultProductShouldNotBeFound("description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllProductsByDescriptionIsNotEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where description not equals to DEFAULT_DESCRIPTION
        defaultProductShouldNotBeFound("description.notEquals=" + DEFAULT_DESCRIPTION);

        // Get all the productList where description not equals to UPDATED_DESCRIPTION
        defaultProductShouldBeFound("description.notEquals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllProductsByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultProductShouldBeFound("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION);

        // Get all the productList where description equals to UPDATED_DESCRIPTION
        defaultProductShouldNotBeFound("description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllProductsByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where description is not null
        defaultProductShouldBeFound("description.specified=true");

        // Get all the productList where description is null
        defaultProductShouldNotBeFound("description.specified=false");
    }
                @Test
    @Transactional
    public void getAllProductsByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where description contains DEFAULT_DESCRIPTION
        defaultProductShouldBeFound("description.contains=" + DEFAULT_DESCRIPTION);

        // Get all the productList where description contains UPDATED_DESCRIPTION
        defaultProductShouldNotBeFound("description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllProductsByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where description does not contain DEFAULT_DESCRIPTION
        defaultProductShouldNotBeFound("description.doesNotContain=" + DEFAULT_DESCRIPTION);

        // Get all the productList where description does not contain UPDATED_DESCRIPTION
        defaultProductShouldBeFound("description.doesNotContain=" + UPDATED_DESCRIPTION);
    }


    @Test
    @Transactional
    public void getAllProductsByRatingIsEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where rating equals to DEFAULT_RATING
        defaultProductShouldBeFound("rating.equals=" + DEFAULT_RATING);

        // Get all the productList where rating equals to UPDATED_RATING
        defaultProductShouldNotBeFound("rating.equals=" + UPDATED_RATING);
    }

    @Test
    @Transactional
    public void getAllProductsByRatingIsNotEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where rating not equals to DEFAULT_RATING
        defaultProductShouldNotBeFound("rating.notEquals=" + DEFAULT_RATING);

        // Get all the productList where rating not equals to UPDATED_RATING
        defaultProductShouldBeFound("rating.notEquals=" + UPDATED_RATING);
    }

    @Test
    @Transactional
    public void getAllProductsByRatingIsInShouldWork() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where rating in DEFAULT_RATING or UPDATED_RATING
        defaultProductShouldBeFound("rating.in=" + DEFAULT_RATING + "," + UPDATED_RATING);

        // Get all the productList where rating equals to UPDATED_RATING
        defaultProductShouldNotBeFound("rating.in=" + UPDATED_RATING);
    }

    @Test
    @Transactional
    public void getAllProductsByRatingIsNullOrNotNull() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where rating is not null
        defaultProductShouldBeFound("rating.specified=true");

        // Get all the productList where rating is null
        defaultProductShouldNotBeFound("rating.specified=false");
    }

    @Test
    @Transactional
    public void getAllProductsByRatingIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where rating is greater than or equal to DEFAULT_RATING
        defaultProductShouldBeFound("rating.greaterThanOrEqual=" + DEFAULT_RATING);

        // Get all the productList where rating is greater than or equal to UPDATED_RATING
        defaultProductShouldNotBeFound("rating.greaterThanOrEqual=" + UPDATED_RATING);
    }

    @Test
    @Transactional
    public void getAllProductsByRatingIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where rating is less than or equal to DEFAULT_RATING
        defaultProductShouldBeFound("rating.lessThanOrEqual=" + DEFAULT_RATING);

        // Get all the productList where rating is less than or equal to SMALLER_RATING
        defaultProductShouldNotBeFound("rating.lessThanOrEqual=" + SMALLER_RATING);
    }

    @Test
    @Transactional
    public void getAllProductsByRatingIsLessThanSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where rating is less than DEFAULT_RATING
        defaultProductShouldNotBeFound("rating.lessThan=" + DEFAULT_RATING);

        // Get all the productList where rating is less than UPDATED_RATING
        defaultProductShouldBeFound("rating.lessThan=" + UPDATED_RATING);
    }

    @Test
    @Transactional
    public void getAllProductsByRatingIsGreaterThanSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where rating is greater than DEFAULT_RATING
        defaultProductShouldNotBeFound("rating.greaterThan=" + DEFAULT_RATING);

        // Get all the productList where rating is greater than SMALLER_RATING
        defaultProductShouldBeFound("rating.greaterThan=" + SMALLER_RATING);
    }


    @Test
    @Transactional
    public void getAllProductsByDateAddedIsEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where dateAdded equals to DEFAULT_DATE_ADDED
        defaultProductShouldBeFound("dateAdded.equals=" + DEFAULT_DATE_ADDED);

        // Get all the productList where dateAdded equals to UPDATED_DATE_ADDED
        defaultProductShouldNotBeFound("dateAdded.equals=" + UPDATED_DATE_ADDED);
    }

    @Test
    @Transactional
    public void getAllProductsByDateAddedIsNotEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where dateAdded not equals to DEFAULT_DATE_ADDED
        defaultProductShouldNotBeFound("dateAdded.notEquals=" + DEFAULT_DATE_ADDED);

        // Get all the productList where dateAdded not equals to UPDATED_DATE_ADDED
        defaultProductShouldBeFound("dateAdded.notEquals=" + UPDATED_DATE_ADDED);
    }

    @Test
    @Transactional
    public void getAllProductsByDateAddedIsInShouldWork() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where dateAdded in DEFAULT_DATE_ADDED or UPDATED_DATE_ADDED
        defaultProductShouldBeFound("dateAdded.in=" + DEFAULT_DATE_ADDED + "," + UPDATED_DATE_ADDED);

        // Get all the productList where dateAdded equals to UPDATED_DATE_ADDED
        defaultProductShouldNotBeFound("dateAdded.in=" + UPDATED_DATE_ADDED);
    }

    @Test
    @Transactional
    public void getAllProductsByDateAddedIsNullOrNotNull() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where dateAdded is not null
        defaultProductShouldBeFound("dateAdded.specified=true");

        // Get all the productList where dateAdded is null
        defaultProductShouldNotBeFound("dateAdded.specified=false");
    }

    @Test
    @Transactional
    public void getAllProductsByDateAddedIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where dateAdded is greater than or equal to DEFAULT_DATE_ADDED
        defaultProductShouldBeFound("dateAdded.greaterThanOrEqual=" + DEFAULT_DATE_ADDED);

        // Get all the productList where dateAdded is greater than or equal to UPDATED_DATE_ADDED
        defaultProductShouldNotBeFound("dateAdded.greaterThanOrEqual=" + UPDATED_DATE_ADDED);
    }

    @Test
    @Transactional
    public void getAllProductsByDateAddedIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where dateAdded is less than or equal to DEFAULT_DATE_ADDED
        defaultProductShouldBeFound("dateAdded.lessThanOrEqual=" + DEFAULT_DATE_ADDED);

        // Get all the productList where dateAdded is less than or equal to SMALLER_DATE_ADDED
        defaultProductShouldNotBeFound("dateAdded.lessThanOrEqual=" + SMALLER_DATE_ADDED);
    }

    @Test
    @Transactional
    public void getAllProductsByDateAddedIsLessThanSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where dateAdded is less than DEFAULT_DATE_ADDED
        defaultProductShouldNotBeFound("dateAdded.lessThan=" + DEFAULT_DATE_ADDED);

        // Get all the productList where dateAdded is less than UPDATED_DATE_ADDED
        defaultProductShouldBeFound("dateAdded.lessThan=" + UPDATED_DATE_ADDED);
    }

    @Test
    @Transactional
    public void getAllProductsByDateAddedIsGreaterThanSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where dateAdded is greater than DEFAULT_DATE_ADDED
        defaultProductShouldNotBeFound("dateAdded.greaterThan=" + DEFAULT_DATE_ADDED);

        // Get all the productList where dateAdded is greater than SMALLER_DATE_ADDED
        defaultProductShouldBeFound("dateAdded.greaterThan=" + SMALLER_DATE_ADDED);
    }


    @Test
    @Transactional
    public void getAllProductsByDateModifiedIsEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where dateModified equals to DEFAULT_DATE_MODIFIED
        defaultProductShouldBeFound("dateModified.equals=" + DEFAULT_DATE_MODIFIED);

        // Get all the productList where dateModified equals to UPDATED_DATE_MODIFIED
        defaultProductShouldNotBeFound("dateModified.equals=" + UPDATED_DATE_MODIFIED);
    }

    @Test
    @Transactional
    public void getAllProductsByDateModifiedIsNotEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where dateModified not equals to DEFAULT_DATE_MODIFIED
        defaultProductShouldNotBeFound("dateModified.notEquals=" + DEFAULT_DATE_MODIFIED);

        // Get all the productList where dateModified not equals to UPDATED_DATE_MODIFIED
        defaultProductShouldBeFound("dateModified.notEquals=" + UPDATED_DATE_MODIFIED);
    }

    @Test
    @Transactional
    public void getAllProductsByDateModifiedIsInShouldWork() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where dateModified in DEFAULT_DATE_MODIFIED or UPDATED_DATE_MODIFIED
        defaultProductShouldBeFound("dateModified.in=" + DEFAULT_DATE_MODIFIED + "," + UPDATED_DATE_MODIFIED);

        // Get all the productList where dateModified equals to UPDATED_DATE_MODIFIED
        defaultProductShouldNotBeFound("dateModified.in=" + UPDATED_DATE_MODIFIED);
    }

    @Test
    @Transactional
    public void getAllProductsByDateModifiedIsNullOrNotNull() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where dateModified is not null
        defaultProductShouldBeFound("dateModified.specified=true");

        // Get all the productList where dateModified is null
        defaultProductShouldNotBeFound("dateModified.specified=false");
    }

    @Test
    @Transactional
    public void getAllProductsByDateModifiedIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where dateModified is greater than or equal to DEFAULT_DATE_MODIFIED
        defaultProductShouldBeFound("dateModified.greaterThanOrEqual=" + DEFAULT_DATE_MODIFIED);

        // Get all the productList where dateModified is greater than or equal to UPDATED_DATE_MODIFIED
        defaultProductShouldNotBeFound("dateModified.greaterThanOrEqual=" + UPDATED_DATE_MODIFIED);
    }

    @Test
    @Transactional
    public void getAllProductsByDateModifiedIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where dateModified is less than or equal to DEFAULT_DATE_MODIFIED
        defaultProductShouldBeFound("dateModified.lessThanOrEqual=" + DEFAULT_DATE_MODIFIED);

        // Get all the productList where dateModified is less than or equal to SMALLER_DATE_MODIFIED
        defaultProductShouldNotBeFound("dateModified.lessThanOrEqual=" + SMALLER_DATE_MODIFIED);
    }

    @Test
    @Transactional
    public void getAllProductsByDateModifiedIsLessThanSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where dateModified is less than DEFAULT_DATE_MODIFIED
        defaultProductShouldNotBeFound("dateModified.lessThan=" + DEFAULT_DATE_MODIFIED);

        // Get all the productList where dateModified is less than UPDATED_DATE_MODIFIED
        defaultProductShouldBeFound("dateModified.lessThan=" + UPDATED_DATE_MODIFIED);
    }

    @Test
    @Transactional
    public void getAllProductsByDateModifiedIsGreaterThanSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where dateModified is greater than DEFAULT_DATE_MODIFIED
        defaultProductShouldNotBeFound("dateModified.greaterThan=" + DEFAULT_DATE_MODIFIED);

        // Get all the productList where dateModified is greater than SMALLER_DATE_MODIFIED
        defaultProductShouldBeFound("dateModified.greaterThan=" + SMALLER_DATE_MODIFIED);
    }


    @Test
    @Transactional
    public void getAllProductsByWishListIsEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);
        WishList wishList = WishListResourceIT.createEntity(em);
        em.persist(wishList);
        em.flush();
        product.setWishList(wishList);
        productRepository.saveAndFlush(product);
        Long wishListId = wishList.getId();

        // Get all the productList where wishList equals to wishListId
        defaultProductShouldBeFound("wishListId.equals=" + wishListId);

        // Get all the productList where wishList equals to wishListId + 1
        defaultProductShouldNotBeFound("wishListId.equals=" + (wishListId + 1));
    }


    @Test
    @Transactional
    public void getAllProductsByCategoryIsEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);
        Category category = CategoryResourceIT.createEntity(em);
        em.persist(category);
        em.flush();
        product.addCategory(category);
        productRepository.saveAndFlush(product);
        Long categoryId = category.getId();

        // Get all the productList where category equals to categoryId
        defaultProductShouldBeFound("categoryId.equals=" + categoryId);

        // Get all the productList where category equals to categoryId + 1
        defaultProductShouldNotBeFound("categoryId.equals=" + (categoryId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultProductShouldBeFound(String filter) throws Exception {
        restProductMockMvc.perform(get("/api/products?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(product.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].keywords").value(hasItem(DEFAULT_KEYWORDS)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].rating").value(hasItem(DEFAULT_RATING)))
            .andExpect(jsonPath("$.[*].dateAdded").value(hasItem(DEFAULT_DATE_ADDED.toString())))
            .andExpect(jsonPath("$.[*].dateModified").value(hasItem(DEFAULT_DATE_MODIFIED.toString())));

        // Check, that the count call also returns 1
        restProductMockMvc.perform(get("/api/products/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultProductShouldNotBeFound(String filter) throws Exception {
        restProductMockMvc.perform(get("/api/products?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restProductMockMvc.perform(get("/api/products/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }


    @Test
    @Transactional
    public void getNonExistingProduct() throws Exception {
        // Get the product
        restProductMockMvc.perform(get("/api/products/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateProduct() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        int databaseSizeBeforeUpdate = productRepository.findAll().size();

        // Update the product
        Product updatedProduct = productRepository.findById(product.getId()).get();
        // Disconnect from session so that the updates on updatedProduct are not directly saved in db
        em.detach(updatedProduct);
        updatedProduct
            .title(UPDATED_TITLE)
            .keywords(UPDATED_KEYWORDS)
            .description(UPDATED_DESCRIPTION)
            .rating(UPDATED_RATING)
            .dateAdded(UPDATED_DATE_ADDED)
            .dateModified(UPDATED_DATE_MODIFIED);
        ProductDTO productDTO = productMapper.toDto(updatedProduct);

        restProductMockMvc.perform(put("/api/products")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(productDTO)))
            .andExpect(status().isOk());

        // Validate the Product in the database
        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeUpdate);
        Product testProduct = productList.get(productList.size() - 1);
        assertThat(testProduct.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testProduct.getKeywords()).isEqualTo(UPDATED_KEYWORDS);
        assertThat(testProduct.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testProduct.getRating()).isEqualTo(UPDATED_RATING);
        assertThat(testProduct.getDateAdded()).isEqualTo(UPDATED_DATE_ADDED);
        assertThat(testProduct.getDateModified()).isEqualTo(UPDATED_DATE_MODIFIED);
    }

    @Test
    @Transactional
    public void updateNonExistingProduct() throws Exception {
        int databaseSizeBeforeUpdate = productRepository.findAll().size();

        // Create the Product
        ProductDTO productDTO = productMapper.toDto(product);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProductMockMvc.perform(put("/api/products")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(productDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Product in the database
        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteProduct() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        int databaseSizeBeforeDelete = productRepository.findAll().size();

        // Delete the product
        restProductMockMvc.perform(delete("/api/products/{id}", product.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
