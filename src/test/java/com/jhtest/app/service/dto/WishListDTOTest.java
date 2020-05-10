package com.jhtest.app.service.dto;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import com.jhtest.app.web.rest.TestUtil;

public class WishListDTOTest {

    @Test
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(WishListDTO.class);
        WishListDTO wishListDTO1 = new WishListDTO();
        wishListDTO1.setId(1L);
        WishListDTO wishListDTO2 = new WishListDTO();
        assertThat(wishListDTO1).isNotEqualTo(wishListDTO2);
        wishListDTO2.setId(wishListDTO1.getId());
        assertThat(wishListDTO1).isEqualTo(wishListDTO2);
        wishListDTO2.setId(2L);
        assertThat(wishListDTO1).isNotEqualTo(wishListDTO2);
        wishListDTO1.setId(null);
        assertThat(wishListDTO1).isNotEqualTo(wishListDTO2);
    }
}
