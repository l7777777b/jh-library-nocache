package com.jhtest.app.service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class PublisherMapperTest {

    private PublisherMapper publisherMapper;

    @BeforeEach
    public void setUp() {
        publisherMapper = new PublisherMapperImpl();
    }

    @Test
    public void testEntityFromId() {
        Long id = 1L;
        assertThat(publisherMapper.fromId(id).getId()).isEqualTo(id);
        assertThat(publisherMapper.fromId(null)).isNull();
    }
}
