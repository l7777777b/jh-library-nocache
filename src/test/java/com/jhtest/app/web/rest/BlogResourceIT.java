package com.jhtest.app.web.rest;

import com.jhtest.app.JhlibrarynocacheApp;
import com.jhtest.app.domain.Blog;
import com.jhtest.app.domain.User;
import com.jhtest.app.repository.BlogRepository;
import com.jhtest.app.service.BlogService;
import com.jhtest.app.service.dto.BlogDTO;
import com.jhtest.app.service.mapper.BlogMapper;
import com.jhtest.app.service.dto.BlogCriteria;
import com.jhtest.app.service.BlogQueryService;

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
 * Integration tests for the {@link BlogResource} REST controller.
 */
@SpringBootTest(classes = JhlibrarynocacheApp.class)

@AutoConfigureMockMvc
@WithMockUser
public class BlogResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_HANDLE = "AAAAAAAAAA";
    private static final String UPDATED_HANDLE = "BBBBBBBBBB";

    @Autowired
    private BlogRepository blogRepository;

    @Autowired
    private BlogMapper blogMapper;

    @Autowired
    private BlogService blogService;

    @Autowired
    private BlogQueryService blogQueryService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restBlogMockMvc;

    private Blog blog;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Blog createEntity(EntityManager em) {
        Blog blog = new Blog()
            .name(DEFAULT_NAME)
            .handle(DEFAULT_HANDLE);
        return blog;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Blog createUpdatedEntity(EntityManager em) {
        Blog blog = new Blog()
            .name(UPDATED_NAME)
            .handle(UPDATED_HANDLE);
        return blog;
    }

    @BeforeEach
    public void initTest() {
        blog = createEntity(em);
    }

    @Test
    @Transactional
    public void createBlog() throws Exception {
        int databaseSizeBeforeCreate = blogRepository.findAll().size();

        // Create the Blog
        BlogDTO blogDTO = blogMapper.toDto(blog);
        restBlogMockMvc.perform(post("/api/blogs")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(blogDTO)))
            .andExpect(status().isCreated());

        // Validate the Blog in the database
        List<Blog> blogList = blogRepository.findAll();
        assertThat(blogList).hasSize(databaseSizeBeforeCreate + 1);
        Blog testBlog = blogList.get(blogList.size() - 1);
        assertThat(testBlog.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testBlog.getHandle()).isEqualTo(DEFAULT_HANDLE);
    }

    @Test
    @Transactional
    public void createBlogWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = blogRepository.findAll().size();

        // Create the Blog with an existing ID
        blog.setId(1L);
        BlogDTO blogDTO = blogMapper.toDto(blog);

        // An entity with an existing ID cannot be created, so this API call must fail
        restBlogMockMvc.perform(post("/api/blogs")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(blogDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Blog in the database
        List<Blog> blogList = blogRepository.findAll();
        assertThat(blogList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = blogRepository.findAll().size();
        // set the field null
        blog.setName(null);

        // Create the Blog, which fails.
        BlogDTO blogDTO = blogMapper.toDto(blog);

        restBlogMockMvc.perform(post("/api/blogs")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(blogDTO)))
            .andExpect(status().isBadRequest());

        List<Blog> blogList = blogRepository.findAll();
        assertThat(blogList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkHandleIsRequired() throws Exception {
        int databaseSizeBeforeTest = blogRepository.findAll().size();
        // set the field null
        blog.setHandle(null);

        // Create the Blog, which fails.
        BlogDTO blogDTO = blogMapper.toDto(blog);

        restBlogMockMvc.perform(post("/api/blogs")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(blogDTO)))
            .andExpect(status().isBadRequest());

        List<Blog> blogList = blogRepository.findAll();
        assertThat(blogList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllBlogs() throws Exception {
        // Initialize the database
        blogRepository.saveAndFlush(blog);

        // Get all the blogList
        restBlogMockMvc.perform(get("/api/blogs?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(blog.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].handle").value(hasItem(DEFAULT_HANDLE)));
    }
    
    @Test
    @Transactional
    public void getBlog() throws Exception {
        // Initialize the database
        blogRepository.saveAndFlush(blog);

        // Get the blog
        restBlogMockMvc.perform(get("/api/blogs/{id}", blog.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(blog.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.handle").value(DEFAULT_HANDLE));
    }


    @Test
    @Transactional
    public void getBlogsByIdFiltering() throws Exception {
        // Initialize the database
        blogRepository.saveAndFlush(blog);

        Long id = blog.getId();

        defaultBlogShouldBeFound("id.equals=" + id);
        defaultBlogShouldNotBeFound("id.notEquals=" + id);

        defaultBlogShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultBlogShouldNotBeFound("id.greaterThan=" + id);

        defaultBlogShouldBeFound("id.lessThanOrEqual=" + id);
        defaultBlogShouldNotBeFound("id.lessThan=" + id);
    }


    @Test
    @Transactional
    public void getAllBlogsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        blogRepository.saveAndFlush(blog);

        // Get all the blogList where name equals to DEFAULT_NAME
        defaultBlogShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the blogList where name equals to UPDATED_NAME
        defaultBlogShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllBlogsByNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        blogRepository.saveAndFlush(blog);

        // Get all the blogList where name not equals to DEFAULT_NAME
        defaultBlogShouldNotBeFound("name.notEquals=" + DEFAULT_NAME);

        // Get all the blogList where name not equals to UPDATED_NAME
        defaultBlogShouldBeFound("name.notEquals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllBlogsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        blogRepository.saveAndFlush(blog);

        // Get all the blogList where name in DEFAULT_NAME or UPDATED_NAME
        defaultBlogShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the blogList where name equals to UPDATED_NAME
        defaultBlogShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllBlogsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        blogRepository.saveAndFlush(blog);

        // Get all the blogList where name is not null
        defaultBlogShouldBeFound("name.specified=true");

        // Get all the blogList where name is null
        defaultBlogShouldNotBeFound("name.specified=false");
    }
                @Test
    @Transactional
    public void getAllBlogsByNameContainsSomething() throws Exception {
        // Initialize the database
        blogRepository.saveAndFlush(blog);

        // Get all the blogList where name contains DEFAULT_NAME
        defaultBlogShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the blogList where name contains UPDATED_NAME
        defaultBlogShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllBlogsByNameNotContainsSomething() throws Exception {
        // Initialize the database
        blogRepository.saveAndFlush(blog);

        // Get all the blogList where name does not contain DEFAULT_NAME
        defaultBlogShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the blogList where name does not contain UPDATED_NAME
        defaultBlogShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }


    @Test
    @Transactional
    public void getAllBlogsByHandleIsEqualToSomething() throws Exception {
        // Initialize the database
        blogRepository.saveAndFlush(blog);

        // Get all the blogList where handle equals to DEFAULT_HANDLE
        defaultBlogShouldBeFound("handle.equals=" + DEFAULT_HANDLE);

        // Get all the blogList where handle equals to UPDATED_HANDLE
        defaultBlogShouldNotBeFound("handle.equals=" + UPDATED_HANDLE);
    }

    @Test
    @Transactional
    public void getAllBlogsByHandleIsNotEqualToSomething() throws Exception {
        // Initialize the database
        blogRepository.saveAndFlush(blog);

        // Get all the blogList where handle not equals to DEFAULT_HANDLE
        defaultBlogShouldNotBeFound("handle.notEquals=" + DEFAULT_HANDLE);

        // Get all the blogList where handle not equals to UPDATED_HANDLE
        defaultBlogShouldBeFound("handle.notEquals=" + UPDATED_HANDLE);
    }

    @Test
    @Transactional
    public void getAllBlogsByHandleIsInShouldWork() throws Exception {
        // Initialize the database
        blogRepository.saveAndFlush(blog);

        // Get all the blogList where handle in DEFAULT_HANDLE or UPDATED_HANDLE
        defaultBlogShouldBeFound("handle.in=" + DEFAULT_HANDLE + "," + UPDATED_HANDLE);

        // Get all the blogList where handle equals to UPDATED_HANDLE
        defaultBlogShouldNotBeFound("handle.in=" + UPDATED_HANDLE);
    }

    @Test
    @Transactional
    public void getAllBlogsByHandleIsNullOrNotNull() throws Exception {
        // Initialize the database
        blogRepository.saveAndFlush(blog);

        // Get all the blogList where handle is not null
        defaultBlogShouldBeFound("handle.specified=true");

        // Get all the blogList where handle is null
        defaultBlogShouldNotBeFound("handle.specified=false");
    }
                @Test
    @Transactional
    public void getAllBlogsByHandleContainsSomething() throws Exception {
        // Initialize the database
        blogRepository.saveAndFlush(blog);

        // Get all the blogList where handle contains DEFAULT_HANDLE
        defaultBlogShouldBeFound("handle.contains=" + DEFAULT_HANDLE);

        // Get all the blogList where handle contains UPDATED_HANDLE
        defaultBlogShouldNotBeFound("handle.contains=" + UPDATED_HANDLE);
    }

    @Test
    @Transactional
    public void getAllBlogsByHandleNotContainsSomething() throws Exception {
        // Initialize the database
        blogRepository.saveAndFlush(blog);

        // Get all the blogList where handle does not contain DEFAULT_HANDLE
        defaultBlogShouldNotBeFound("handle.doesNotContain=" + DEFAULT_HANDLE);

        // Get all the blogList where handle does not contain UPDATED_HANDLE
        defaultBlogShouldBeFound("handle.doesNotContain=" + UPDATED_HANDLE);
    }


    @Test
    @Transactional
    public void getAllBlogsByUserIsEqualToSomething() throws Exception {
        // Initialize the database
        blogRepository.saveAndFlush(blog);
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();
        blog.setUser(user);
        blogRepository.saveAndFlush(blog);
        Long userId = user.getId();

        // Get all the blogList where user equals to userId
        defaultBlogShouldBeFound("userId.equals=" + userId);

        // Get all the blogList where user equals to userId + 1
        defaultBlogShouldNotBeFound("userId.equals=" + (userId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultBlogShouldBeFound(String filter) throws Exception {
        restBlogMockMvc.perform(get("/api/blogs?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(blog.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].handle").value(hasItem(DEFAULT_HANDLE)));

        // Check, that the count call also returns 1
        restBlogMockMvc.perform(get("/api/blogs/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultBlogShouldNotBeFound(String filter) throws Exception {
        restBlogMockMvc.perform(get("/api/blogs?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restBlogMockMvc.perform(get("/api/blogs/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }


    @Test
    @Transactional
    public void getNonExistingBlog() throws Exception {
        // Get the blog
        restBlogMockMvc.perform(get("/api/blogs/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateBlog() throws Exception {
        // Initialize the database
        blogRepository.saveAndFlush(blog);

        int databaseSizeBeforeUpdate = blogRepository.findAll().size();

        // Update the blog
        Blog updatedBlog = blogRepository.findById(blog.getId()).get();
        // Disconnect from session so that the updates on updatedBlog are not directly saved in db
        em.detach(updatedBlog);
        updatedBlog
            .name(UPDATED_NAME)
            .handle(UPDATED_HANDLE);
        BlogDTO blogDTO = blogMapper.toDto(updatedBlog);

        restBlogMockMvc.perform(put("/api/blogs")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(blogDTO)))
            .andExpect(status().isOk());

        // Validate the Blog in the database
        List<Blog> blogList = blogRepository.findAll();
        assertThat(blogList).hasSize(databaseSizeBeforeUpdate);
        Blog testBlog = blogList.get(blogList.size() - 1);
        assertThat(testBlog.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testBlog.getHandle()).isEqualTo(UPDATED_HANDLE);
    }

    @Test
    @Transactional
    public void updateNonExistingBlog() throws Exception {
        int databaseSizeBeforeUpdate = blogRepository.findAll().size();

        // Create the Blog
        BlogDTO blogDTO = blogMapper.toDto(blog);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBlogMockMvc.perform(put("/api/blogs")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(blogDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Blog in the database
        List<Blog> blogList = blogRepository.findAll();
        assertThat(blogList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteBlog() throws Exception {
        // Initialize the database
        blogRepository.saveAndFlush(blog);

        int databaseSizeBeforeDelete = blogRepository.findAll().size();

        // Delete the blog
        restBlogMockMvc.perform(delete("/api/blogs/{id}", blog.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Blog> blogList = blogRepository.findAll();
        assertThat(blogList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
