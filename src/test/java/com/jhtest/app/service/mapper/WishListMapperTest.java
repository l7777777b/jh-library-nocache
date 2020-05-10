package com.jhtest.app.service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class WishListMapperTest {

    private WishListMapper wishListMapper;

    @BeforeEach
    public void setUp() {
        wishListMapper = new WishListMapperImpl();
    }

    @Test
    public void testEntityFromId() {
        Long id = 1L;
        assertThat(wishListMapper.fromId(id).getId()).isEqualTo(id);
        assertThat(wishListMapper.fromId(null)).isNull();
    }
}
