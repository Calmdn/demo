package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.pojo.*;
import com.example.demo.service.PricingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pricing")
public class PricingController {

    @Autowired
    private PricingService pricingService;

    /**
     * 更新作品价格
     */
    @PutMapping("/price")
    public Result<String> updatePrice(@RequestBody PriceUpdateRequest request) {
        try {
            boolean success = pricingService.updateArtworkPrice(request);
            return success ? Result.success("价格更新成功") : Result.error("价格更新失败");
        } catch (Exception e) {
            return Result.error("更新失败：" + e.getMessage());
        }
    }

    /**
     * 创建折扣活动
     */
    @PostMapping("/discount")
    public Result<Map<String, Integer>> createDiscount(@RequestBody DiscountCreateRequest request) {
        try {
            Integer discountId = pricingService.createDiscount(request);
            return Result.success("折扣活动创建成功", Map.of("discountId", discountId));
        } catch (Exception e) {
            return Result.error("创建失败：" + e.getMessage());
        }
    }

    /**
     * 计算作品价格
     */
    @GetMapping("/calculate/{artworkId}")
    public Result<Map<String, Object>> calculatePrice(@PathVariable Integer artworkId) {
        try {
            Map<String, Object> priceInfo = pricingService.calculatePrice(artworkId);
            return Result.success(priceInfo);
        } catch (Exception e) {
            return Result.error("计算失败：" + e.getMessage());
        }
    }

    /**
     * 批量计算作品价格
     */
    @PostMapping("/calculate/batch")
    public Result<Map<Integer, Map<String, Object>>> batchCalculatePrice(@RequestBody List<Integer> artworkIds) {
        try {
            Map<Integer, Map<String, Object>> priceInfo = pricingService.batchCalculatePrice(artworkIds);
            return Result.success(priceInfo);
        } catch (Exception e) {
            return Result.error("计算失败：" + e.getMessage());
        }
    }

    /**
     * 获取价格历史
     */
    @GetMapping("/history/{artworkId}")
    public Result<List<ArtworkPriceHistory>> getPriceHistory(@PathVariable Integer artworkId,
                                                             @RequestParam(defaultValue = "1") int page,
                                                             @RequestParam(defaultValue = "10") int pageSize) {
        List<ArtworkPriceHistory> history = pricingService.getPriceHistory(artworkId, page, pageSize);
        return Result.success(history);
    }

    /**
     * 获取我的价格变更记录
     */
    @GetMapping("/history/my")
    public Result<List<ArtworkPriceHistory>> getMyPriceChanges(@RequestParam(defaultValue = "1") int page,
                                                               @RequestParam(defaultValue = "10") int pageSize) {
        List<ArtworkPriceHistory> changes = pricingService.getMyPriceChanges(page, pageSize);
        return Result.success(changes);
    }

    /**
     * 获取折扣活动历史
     */
    @GetMapping("/discount/history/{artworkId}")
    public Result<List<ArtworkDiscount>> getDiscountHistory(@PathVariable Integer artworkId,
                                                            @RequestParam(defaultValue = "1") int page,
                                                            @RequestParam(defaultValue = "10") int pageSize) {
        List<ArtworkDiscount> discounts = pricingService.getDiscountHistory(artworkId, page, pageSize);
        return Result.success(discounts);
    }

    /**
     * 获取我的折扣活动
     */
    @GetMapping("/discount/my")
    public Result<List<ArtworkDiscount>> getMyDiscounts(@RequestParam(required = false) Integer status,
                                                        @RequestParam(defaultValue = "1") int page,
                                                        @RequestParam(defaultValue = "10") int pageSize) {
        List<ArtworkDiscount> discounts = pricingService.getMyDiscounts(status, page, pageSize);
        return Result.success(discounts);
    }

    /**
     * 停用折扣活动
     */
    @PutMapping("/discount/{discountId}/disable")
    public Result<String> disableDiscount(@PathVariable Integer discountId) {
        try {
            boolean success = pricingService.disableDiscount(discountId);
            return success ? Result.success("折扣活动已停用") : Result.error("操作失败");
        } catch (Exception e) {
            return Result.error("操作失败：" + e.getMessage());
        }
    }
}