package com.jhtest.app.service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class BlogMapperTest {

    private BlogMapper blogMapper;

    @BeforeEach
    public void setUp() {
        blogMapper = new BlogMapperImpl();
    }

    @Test
    public void testEntityFromId() {
        Long id = 1L;
        assertThat(blogMapper.fromId(id).getId()).isEqualTo(id);
        assertThat(blogMapper.fromId(null)).isNull();
    }
}
