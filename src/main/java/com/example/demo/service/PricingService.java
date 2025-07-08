package com.example.demo.service;

import com.example.demo.mapper.ArtworkDiscountMapper;
import com.example.demo.mapper.ArtworkMapper;
import com.example.demo.mapper.ArtworkPriceHistoryMapper;
import com.example.demo.pojo.*;
import com.example.demo.context.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PricingService {

    @Autowired
    private ArtworkMapper artworkMapper;

    @Autowired
    private ArtworkPriceHistoryMapper priceHistoryMapper;

    @Autowired
    private ArtworkDiscountMapper discountMapper;

    /**
     * 更新作品价格
     */
    @Transactional
    public boolean updateArtworkPrice(PriceUpdateRequest request) {
        Integer userId = UserContext.getCurrentUserId();

        // 验证作品权限
        Artwork artwork = artworkMapper.selectById(request.getArtworkId());
        if (artwork == null) {
            throw new RuntimeException("作品不存在");
        }

        if (!artwork.getUserId().equals(userId)) {
            throw new RuntimeException("只能修改自己的作品价格");
        }

        // 记录价格历史
        ArtworkPriceHistory history = new ArtworkPriceHistory();
        history.setArtworkId(request.getArtworkId());
        history.setOldPrice(artwork.getPrice());
        history.setNewPrice(request.getNewPrice());
        history.setChangeReason(request.getChangeReason());
        history.setOperatorId(userId);

        priceHistoryMapper.insert(history);

        // 更新作品价格
        artwork.setPrice(request.getNewPrice());
        artworkMapper.updatePrice(request.getArtworkId(), request.getNewPrice());

        return true;
    }

    /**
     * 创建折扣活动
     */
    @Transactional
    public Integer createDiscount(DiscountCreateRequest request) {
        Integer userId = UserContext.getCurrentUserId();

        // 验证作品权限
        Artwork artwork = artworkMapper.selectById(request.getArtworkId());
        if (artwork == null) {
            throw new RuntimeException("作品不存在");
        }

        if (!artwork.getUserId().equals(userId)) {
            throw new RuntimeException("只能为自己的作品创建折扣");
        }

        // 验证折扣参数
        validateDiscountRequest(request);

        ArtworkDiscount discount = new ArtworkDiscount();
        discount.setArtworkId(request.getArtworkId());
        discount.setDiscountType(request.getDiscountType());
        discount.setDiscountValue(request.getDiscountValue());
        discount.setMinPrice(request.getMinPrice());
        discount.setStartTime(request.getStartTime());
        discount.setEndTime(request.getEndTime());
        discount.setDescription(request.getDescription());
        discount.setCreatorId(userId);
        discount.setStatus(1);

        discountMapper.insert(discount);

        return discount.getId();
    }

    /**
     * 计算作品最终价格
     */
    public Map<String, Object> calculatePrice(Integer artworkId) {
        Artwork artwork = artworkMapper.selectById(artworkId);
        if (artwork == null) {
            throw new RuntimeException("作品不存在");
        }

        BigDecimal originalPrice = artwork.getPrice();
        BigDecimal finalPrice = originalPrice;
        ArtworkDiscount activeDiscount = null;

        // 查找有效折扣
        if (artwork.getAllowDiscount() != null && artwork.getAllowDiscount()) {
            activeDiscount = discountMapper.selectActiveDiscountByArtworkId(artworkId);

            if (activeDiscount != null) {
                finalPrice = calculateDiscountPrice(originalPrice, activeDiscount);
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("originalPrice", originalPrice);
        result.put("finalPrice", finalPrice);
        result.put("discount", activeDiscount);
        result.put("discountAmount", originalPrice.subtract(finalPrice));
        result.put("discountPercent", originalPrice.compareTo(BigDecimal.ZERO) > 0 ?
                originalPrice.subtract(finalPrice).divide(originalPrice, 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100")) : BigDecimal.ZERO);

        return result;
    }

    /**
     * 批量计算多个作品价格
     */
    public Map<Integer, Map<String, Object>> batchCalculatePrice(List<Integer> artworkIds) {
        Map<Integer, Map<String, Object>> result = new HashMap<>();

        // 批量查询有效折扣
        List<ArtworkDiscount> discounts = discountMapper.selectActiveDiscountsByArtworkIds(artworkIds);
        Map<Integer, ArtworkDiscount> discountMap = new HashMap<>();
        for (ArtworkDiscount discount : discounts) {
            discountMap.put(discount.getArtworkId(), discount);
        }

        // 计算每个作品的价格
        for (Integer artworkId : artworkIds) {
            try {
                Artwork artwork = artworkMapper.selectById(artworkId);
                if (artwork != null) {
                    BigDecimal originalPrice = artwork.getPrice();
                    BigDecimal finalPrice = originalPrice;
                    ArtworkDiscount discount = discountMap.get(artworkId);

                    if (discount != null && artwork.getAllowDiscount() != null && artwork.getAllowDiscount()) {
                        finalPrice = calculateDiscountPrice(originalPrice, discount);
                    }

                    Map<String, Object> priceInfo = new HashMap<>();
                    priceInfo.put("originalPrice", originalPrice);
                    priceInfo.put("finalPrice", finalPrice);
                    priceInfo.put("discount", discount);
                    priceInfo.put("discountAmount", originalPrice.subtract(finalPrice));

                    result.put(artworkId, priceInfo);
                }
            } catch (Exception e) {
                // 忽略单个作品的计算错误
            }
        }

        return result;
    }

    /**
     * 获取价格历史
     */
    public List<ArtworkPriceHistory> getPriceHistory(Integer artworkId, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return priceHistoryMapper.selectByArtworkId(artworkId, offset, pageSize);
    }

    /**
     * 获取我的价格变更记录
     */
    public List<ArtworkPriceHistory> getMyPriceChanges(int page, int pageSize) {
        Integer userId = UserContext.getCurrentUserId();
        int offset = (page - 1) * pageSize;
        return priceHistoryMapper.selectByOperatorId(userId, offset, pageSize);
    }

    /**
     * 获取折扣活动列表
     */
    public List<ArtworkDiscount> getDiscountHistory(Integer artworkId, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return discountMapper.selectByArtworkId(artworkId, offset, pageSize);
    }

    /**
     * 获取我的折扣活动
     */
    public List<ArtworkDiscount> getMyDiscounts(Integer status, int page, int pageSize) {
        Integer userId = UserContext.getCurrentUserId();
        int offset = (page - 1) * pageSize;
        return discountMapper.selectByCreatorId(userId, status, offset, pageSize);
    }

    /**
     * 停用折扣活动
     */
    @Transactional
    public boolean disableDiscount(Integer discountId) {
        Integer userId = UserContext.getCurrentUserId();
        discountMapper.updateStatus(discountId, 0, userId);
        return true;
    }

    /**
     * 计算折扣后价格
     */
    private BigDecimal calculateDiscountPrice(BigDecimal originalPrice, ArtworkDiscount discount) {
        BigDecimal discountedPrice = originalPrice;

        switch (discount.getDiscountType()) {
            case 1: // 百分比折扣
                BigDecimal discountPercent = discount.getDiscountValue().divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);
                discountedPrice = originalPrice.multiply(BigDecimal.ONE.subtract(discountPercent));
                break;

            case 2: // 固定金额减免
                discountedPrice = originalPrice.subtract(discount.getDiscountValue());
                break;

            case 3: // 限时特价
                discountedPrice = discount.getDiscountValue();
                break;
        }

        // 确保不低于最低价格
        if (discount.getMinPrice() != null && discountedPrice.compareTo(discount.getMinPrice()) < 0) {
            discountedPrice = discount.getMinPrice();
        }

        // 确保价格不为负数
        if (discountedPrice.compareTo(BigDecimal.ZERO) < 0) {
            discountedPrice = BigDecimal.ZERO;
        }

        return discountedPrice.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 验证折扣请求参数
     */
    private void validateDiscountRequest(DiscountCreateRequest request) {
        if (request.getStartTime().isAfter(request.getEndTime())) {
            throw new RuntimeException("开始时间不能晚于结束时间");
        }

        if (request.getStartTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("开始时间不能早于当前时间");
        }

        switch (request.getDiscountType()) {
            case 1: // 百分比折扣
                if (request.getDiscountValue().compareTo(BigDecimal.ZERO) <= 0 ||
                        request.getDiscountValue().compareTo(new BigDecimal("100")) >= 0) {
                    throw new RuntimeException("百分比折扣必须在0-100之间");
                }
                break;

            case 2: // 固定金额减免
                if (request.getDiscountValue().compareTo(BigDecimal.ZERO) <= 0) {
                    throw new RuntimeException("减免金额必须大于0");
                }
                break;

            case 3: // 限时特价
                if (request.getDiscountValue().compareTo(BigDecimal.ZERO) <= 0) {
                    throw new RuntimeException("特价金额必须大于0");
                }
                break;

            default:
                throw new RuntimeException("不支持的折扣类型");
        }
    }
}