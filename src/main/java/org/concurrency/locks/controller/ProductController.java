package org.concurrency.locks.controller;

import org.concurrency.locks.biz.ProductBiz;
import org.concurrency.locks.exception.BusinessException;
import org.concurrency.locks.exception.ErrorCode;
import org.concurrency.locks.response.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * @author tianbo
 * @date 2018-11-28
 */
@Controller
@RequestMapping(path = "/product")
public class ProductController {

    @Autowired
    private ProductBiz productBiz;

    @RequestMapping(path = "/purchase/v1", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @ResponseBody
    public String purchaseV1(@RequestParam("userId") Integer userId, @RequestParam("productId") Long productId) {
        if (userId == null || productId == null) {
            return ResponseUtils.getByErrorCode(userId, ErrorCode.INVALID_PARAM);
        }
        try {
            boolean purchaseResult = productBiz.purchaseV1(userId, productId, "purchase_v1");
            if (purchaseResult) {
                return ResponseUtils.getSuccess(userId);
            }
            return ResponseUtils.getFailed(userId);
        } catch (BusinessException ex) {
            return ResponseUtils.getByErrorCode(userId, ex.getErrorCode());
        }
    }

    @RequestMapping(path = "/purchase/v2", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @ResponseBody
    public String purchaseV2(@RequestParam("userId") Integer userId, @RequestParam("productId") Long productId) {
        if (userId == null || productId == null) {
            return ResponseUtils.getByErrorCode(userId, ErrorCode.INVALID_PARAM);
        }
        try {
            boolean purchaseResult = productBiz.purchaseV2(userId, productId, "purchase_v2");
            if (purchaseResult) {
                return ResponseUtils.getSuccess(userId);
            }
            return ResponseUtils.getFailed(userId);
        } catch (BusinessException ex) {
            return ResponseUtils.getByErrorCode(userId, ex.getErrorCode());
        }
    }

    @RequestMapping(path = "/purchase/v3", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @ResponseBody
    public String purchaseV3(@RequestParam("userId") Integer userId, @RequestParam("productId") Long productId) {
        if (userId == null || productId == null) {
            return ResponseUtils.getByErrorCode(userId, ErrorCode.INVALID_PARAM);
        }
        try {
            boolean purchaseResult = productBiz.purchaseV3(userId, productId, "purchase_v3");
            if (purchaseResult) {
                return ResponseUtils.getSuccess(userId);
            }
            return ResponseUtils.getFailed(userId);
        } catch (BusinessException ex) {
            return ResponseUtils.getByErrorCode(userId, ex.getErrorCode());
        }
    }
}
