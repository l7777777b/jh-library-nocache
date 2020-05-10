package com.jhtest.app.service.mapper;


import com.jhtest.app.domain.*;
import com.jhtest.app.service.dto.WishListDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link WishList} and its DTO {@link WishListDTO}.
 */
@Mapper(componentModel = "spring", uses = {CustomerMapper.class})
public interface WishListMapper extends EntityMapper<WishListDTO, WishList> {

    @Mapping(source = "customer.id", target = "customerId")
    WishListDTO toDto(WishList wishList);

    @Mapping(target = "products", ignore = true)
    @Mapping(target = "removeProduct", ignore = true)
    @Mapping(source = "customerId", target = "customer")
    WishList toEntity(WishListDTO wishListDTO);

    default WishList fromId(Long id) {
        if (id == null) {
            return null;
        }
        WishList wishList = new WishList();
        wishList.setId(id);
        return wishList;
    }
}
