package com.jhtest.app.service;

import com.jhtest.app.domain.WishList;
import com.jhtest.app.repository.WishListRepository;
import com.jhtest.app.service.dto.WishListDTO;
import com.jhtest.app.service.mapper.WishListMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing {@link WishList}.
 */
@Service
@Transactional
public class WishListService {

    private final Logger log = LoggerFactory.getLogger(WishListService.class);

    private final WishListRepository wishListRepository;

    private final WishListMapper wishListMapper;

    public WishListService(WishListRepository wishListRepository, WishListMapper wishListMapper) {
        this.wishListRepository = wishListRepository;
        this.wishListMapper = wishListMapper;
    }

    /**
     * Save a wishList.
     *
     * @param wishListDTO the entity to save.
     * @return the persisted entity.
     */
    public WishListDTO save(WishListDTO wishListDTO) {
        log.debug("Request to save WishList : {}", wishListDTO);
        WishList wishList = wishListMapper.toEntity(wishListDTO);
        wishList = wishListRepository.save(wishList);
        return wishListMapper.toDto(wishList);
    }

    /**
     * Get all the wishLists.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<WishListDTO> findAll(Pageable pageable) {
        log.debug("Request to get all WishLists");
        return wishListRepository.findAll(pageable)
            .map(wishListMapper::toDto);
    }

    /**
     * Get one wishList by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<WishListDTO> findOne(Long id) {
        log.debug("Request to get WishList : {}", id);
        return wishListRepository.findById(id)
            .map(wishListMapper::toDto);
    }

    /**
     * Delete the wishList by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete WishList : {}", id);
        wishListRepository.deleteById(id);
    }
}
