package com.leyou.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum ExceptionEnum {

    ITEM_CATEGORY_NOT_FOUND(404,"商品分类没找到"),
    BRAND_NOT_FOUND(404, "品牌没有找到"),
    SPEC_GROUP_NOT_FOUND(404, "商品规格组不存在"),
    SPEC_PARAMS_NOT_FOUND(404, "商品规格参数不存在"),
    GOOD_NOT_FOUND(404,"商品不存在"),
    SPU_DETAIL_NOT_FOUND(404,"DETAIL不存在"),
    SKU_NOT_FOUND(404,"SKU不存在"),
    STOCK_NOT_FOUND(404, "STOCK库存不存在"),
    CART_NOT_FOUND(404,"购物车不存在"),
    INSERT_INTO_ERROR(500,"插入失败"),
    UPDATE_ERROR(500,"更新失败"),
    DELETE_ERROR(500,"删除失败"),
    UPLOAD_FILE_ERROR(500,"文件上传失败"),
    GOOD_SAVE_ERROR(500,"保存商品失败"),
    VALIDATION_FILE_TYPE(400,"文件类型不匹配"),
    VALIDATION_FILE_CONTENT(400,"文件内容不符合"),
    PRICE_CANNOT_BE_NULL(400,"价格不能为空!"),
    INVALID_USER_DATA_TYPE(400,"用户类型无效"),
    INVALID_USERNAME_PASSWORD(400,"用户名或密码错误"),
    UNAUTHORIZED(403,"未授权"),
    ORDER_CREATE_ERROR(500,"创建订单失败"),
    ORDER_DETAIL_CREATE_ERROR(500,"创建订单详细失败"),
    ORDER_STATUS_CREATE_ERROR(500,"创建订单状态失败"),
    STOCK_NOT_ENOUGH(500,"库存不足"),
    ORDER_NOT_FOUND(404, "订单不存在"),
    ORDER_DETAIL_FOUND(404, "订单详情不存在"),
    ORDER_STATUS_NOT_FOUND(404, "订单状态不存在"),
    WX_PAY_ORDER_ERROR(500,"微信下单失败"),
    ODER_STATUS_ERROR(400,"订单状态错误"),
    UPDATE_ORDER_STATUS_ERROR(400,"更新订单状态错误");
    private int code;
    private String msg;
}
