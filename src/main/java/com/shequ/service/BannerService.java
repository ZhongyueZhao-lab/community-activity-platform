package com.shequ.service;

import com.shequ.dto.BannerDTO;
import com.shequ.dto.PageResult;
import com.shequ.entity.Banner;
import com.shequ.entity.User;
import com.shequ.repository.BannerRepository;
import com.shequ.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BannerService {

    @Autowired
    private BannerRepository bannerRepository;

    @Autowired
    private UserRepository userRepository;

    public PageResult<BannerDTO> getBanners(int page, int size, String title, Integer status) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.ASC, "sortOrder"));

        Page<Banner> pageResult = bannerRepository.findByConditions(title, status, pageable);

        List<BannerDTO> bannerDTOs = pageResult.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return new PageResult<>(bannerDTOs, pageResult.getTotalElements(), page, size);
    }

    public List<BannerDTO> getEnabledBanners() {
        List<Banner> banners = bannerRepository.findByStatusOrderBySortOrderAsc(1);
        return banners.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<BannerDTO> getActiveBanners() {
        LocalDateTime now = LocalDateTime.now();
        List<Banner> banners = bannerRepository.findActiveBanners(now);
        return banners.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public BannerDTO getBannerById(Long id) {
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("轮播图不存在"));
        return convertToDTO(banner);
    }

    @Transactional
    public BannerDTO createBanner(BannerDTO bannerDTO, Long creatorId) {
        Banner banner = new Banner();
        copyDTOToEntity(bannerDTO, banner);
        banner.setCreatorId(creatorId);
        banner.setCreatedAt(LocalDateTime.now());
        banner.setUpdatedAt(LocalDateTime.now());

        Banner savedBanner = bannerRepository.save(banner);
        return convertToDTO(savedBanner);
    }

    @Transactional
    public BannerDTO updateBanner(Long id, BannerDTO bannerDTO) {
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("轮播图不存在"));

        copyDTOToEntity(bannerDTO, banner);
        banner.setUpdatedAt(LocalDateTime.now());

        Banner savedBanner = bannerRepository.save(banner);
        return convertToDTO(savedBanner);
    }

    @Transactional
    public void deleteBanner(Long id) {
        if (!bannerRepository.existsById(id)) {
            throw new RuntimeException("轮播图不存在");
        }
        bannerRepository.deleteById(id);
    }

    @Transactional
    public BannerDTO updateBannerStatus(Long id, Integer status) {
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("轮播图不存在"));

        banner.setStatus(status);
        banner.setUpdatedAt(LocalDateTime.now());

        Banner savedBanner = bannerRepository.save(banner);
        return convertToDTO(savedBanner);
    }

    @Transactional
    public void incrementClickCount(Long id) {
        Optional<Banner> bannerOptional = bannerRepository.findById(id);
        if (bannerOptional.isPresent()) {
            Banner banner = bannerOptional.get();
            banner.setClickCount(banner.getClickCount() + 1);
            banner.setUpdatedAt(LocalDateTime.now());
            bannerRepository.save(banner);
        }
    }

    private BannerDTO convertToDTO(Banner banner) {
        BannerDTO dto = new BannerDTO(banner);

        // 设置创建者名称
        if (banner.getCreatorId() != null) {
            Optional<User> creator = userRepository.findById(banner.getCreatorId());
            creator.ifPresent(u -> dto.setCreatorName(u.getNickname()));
        }

        return dto;
    }

    private void copyDTOToEntity(BannerDTO dto, Banner entity) {
        entity.setTitle(dto.getTitle());
        entity.setImageUrl(dto.getImageUrl());
        entity.setLinkUrl(dto.getLinkUrl());
        entity.setDescription(dto.getDescription());
        entity.setSortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : 0);
        entity.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        entity.setStartTime(dto.getStartTime());
        entity.setEndTime(dto.getEndTime());
    }
}