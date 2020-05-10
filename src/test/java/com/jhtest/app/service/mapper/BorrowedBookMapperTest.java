package com.jhtest.app.service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class BorrowedBookMapperTest {

    private BorrowedBookMapper borrowedBookMapper;

    @BeforeEach
    public void setUp() {
        borrowedBookMapper = new BorrowedBookMapperImpl();
    }

    @Test
    public void testEntityFromId() {
        Long id = 1L;
        assertThat(borrowedBookMapper.fromId(id).getId()).isEqualTo(id);
        assertThat(borrowedBookMapper.fromId(null)).isNull();
    }
}
