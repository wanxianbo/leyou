package com.leyou.order.utils;

import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayConstants;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.order.config.PayConfig;
import com.leyou.order.service.IOrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@EnableConfigurationProperties(PayConfig.class)
@Component
public class PayHelper {

    @Autowired
    private IOrderService orderService;

    @Autowired
    private WXPay wxPay;

    @Autowired
    private PayConfig config;

    public String createOrder(Long orderId, Long totalPay, String desc) {
        try {
            Map<String, String> data = new HashMap<>();
            // 商品描述
            data.put("body", desc);
            // 订单号
            data.put("out_trade_no", orderId.toString());
            //金额，单位是分
            data.put("total_fee", totalPay.toString());
            //调用微信支付的终端IP（乐优商城的IP）
            data.put("spbill_create_ip", "127.0.0.1");
            //回调地址，付款成功后的接口
            data.put("notify_url", config.getNotifyUrl());
            // 交易类型为扫码支付
            data.put("trade_type", "NATIVE");
            //利用wxPay工具完成下单
            Map<String, String> result = this.wxPay.unifiedOrder(data);

            // 判断通信标识
            if (WXPayConstants.FAIL.equals(result.get("return_code"))) {
                log.error("[微信下单] 微信下单失败,原因:{}",result.get("return_msg"));
                throw new LyException(ExceptionEnum.WX_PAY_ORDER_ERROR);
            }
            // 判断义务结果标识
            if (WXPayConstants.FAIL.equals(result.get("result_code"))) {
                log.error("[微信下单] 微信下单义务失败,错误代码:{},错误原因:{}",result.get("err_code"),result.get("err_code_des"));
            }
            // 下单成功，获取支付链接
            String url = result.get("code_url");
            return url;
        } catch (Exception e) {
            log.error("创建预交易订单异常", e);
            return null;
        }
    }
    /**
     * 查询订单状态
     *
     * @param orderId
     * @return
     */
    public PayState queryOrder(Long orderId) {
        Map<String, String> data = new HashMap<>();
        // 订单号
        data.put("out_trade_no", orderId.toString());
        try {
            Map<String, String> result = this.wxPay.orderQuery(data);
            if (result == null) {
                // 未查询到结果，认为是未付款
                return PayState.NOT_PAY;
            }
            String state = result.get("trade_state");
            if ("SUCCESS".equals(state)) {
                // success，则认为付款成功

                // 修改订单状态
                this.orderService.updateStatus(orderId, 2);
                return PayState.SUCCESS;
            } else if (StringUtils.equals("USERPAYING", state) || StringUtils.equals("NOTPAY", state)) {
                // 未付款或正在付款，都认为是未付款
                return PayState.NOT_PAY;
            } else {
                // 其它状态认为是付款失败
                return PayState.FAIL;
            }
        } catch (Exception e) {
            log.error("查询订单状态异常", e);
            return PayState.NOT_PAY;
        }
    }
}
