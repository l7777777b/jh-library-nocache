package com.jhtest.app.web.rest;

import com.jhtest.app.service.WishListService;
import com.jhtest.app.web.rest.errors.BadRequestAlertException;
import com.jhtest.app.service.dto.WishListDTO;
import com.jhtest.app.service.dto.WishListCriteria;
import com.jhtest.app.service.WishListQueryService;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link com.jhtest.app.domain.WishList}.
 */
@RestController
@RequestMapping("/api")
public class WishListResource {

    private final Logger log = LoggerFactory.getLogger(WishListResource.class);

    private static final String ENTITY_NAME = "jhlibrarynocacheWishList";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final WishListService wishListService;

    private final WishListQueryService wishListQueryService;

    public WishListResource(WishListService wishListService, WishListQueryService wishListQueryService) {
        this.wishListService = wishListService;
        this.wishListQueryService = wishListQueryService;
    }

    /**
     * {@code POST  /wish-lists} : Create a new wishList.
     *
     * @param wishListDTO the wishListDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new wishListDTO, or with status {@code 400 (Bad Request)} if the wishList has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/wish-lists")
    public ResponseEntity<WishListDTO> createWishList(@Valid @RequestBody WishListDTO wishListDTO) throws URISyntaxException {
        log.debug("REST request to save WishList : {}", wishListDTO);
        if (wishListDTO.getId() != null) {
            throw new BadRequestAlertException("A new wishList cannot already have an ID", ENTITY_NAME, "idexists");
        }
        WishListDTO result = wishListService.save(wishListDTO);
        return ResponseEntity.created(new URI("/api/wish-lists/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /wish-lists} : Updates an existing wishList.
     *
     * @param wishListDTO the wishListDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated wishListDTO,
     * or with status {@code 400 (Bad Request)} if the wishListDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the wishListDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/wish-lists")
    public ResponseEntity<WishListDTO> updateWishList(@Valid @RequestBody WishListDTO wishListDTO) throws URISyntaxException {
        log.debug("REST request to update WishList : {}", wishListDTO);
        if (wishListDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        WishListDTO result = wishListService.save(wishListDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, wishListDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /wish-lists} : get all the wishLists.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of wishLists in body.
     */
    @GetMapping("/wish-lists")
    public ResponseEntity<List<WishListDTO>> getAllWishLists(WishListCriteria criteria, Pageable pageable) {
        log.debug("REST request to get WishLists by criteria: {}", criteria);
        Page<WishListDTO> page = wishListQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /wish-lists/count} : count all the wishLists.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/wish-lists/count")
    public ResponseEntity<Long> countWishLists(WishListCriteria criteria) {
        log.debug("REST request to count WishLists by criteria: {}", criteria);
        return ResponseEntity.ok().body(wishListQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /wish-lists/:id} : get the "id" wishList.
     *
     * @param id the id of the wishListDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the wishListDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/wish-lists/{id}")
    public ResponseEntity<WishListDTO> getWishList(@PathVariable Long id) {
        log.debug("REST request to get WishList : {}", id);
        Optional<WishListDTO> wishListDTO = wishListService.findOne(id);
        return ResponseUtil.wrapOrNotFound(wishListDTO);
    }

    /**
     * {@code DELETE  /wish-lists/:id} : delete the "id" wishList.
     *
     * @param id the id of the wishListDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/wish-lists/{id}")
    public ResponseEntity<Void> deleteWishList(@PathVariable Long id) {
        log.debug("REST request to delete WishList : {}", id);
        wishListService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }
}
