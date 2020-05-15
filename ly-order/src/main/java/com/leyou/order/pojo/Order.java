package com.leyou.order.pojo;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;
import java.util.List;

@Data
@Table(name = "tb_order")
public class Order {
    @Id
    private Long orderId;// id
    private Long totalPay;// 总金额
    private Long actualPay;// 实付金额
    private Integer paymentType;//支付类型
    private String promotionIds;//参与促销活动的id

    private Long postFee = 0l;// 邮费
    private Date createTime;// 订单创建时间
    private String shippingName;// 物流名称
    private String shippingCode;// 物流单号
    private Long userId;//用户id
    private String buyerMessage;//买家留言
    private String buyerNick;//买家昵称
    private Boolean buyerRate;//买家是否已经评价
    private String receiverState;//收获地址（省）
    private String receiverCity;//收获地址（市）
    private String receiverDistrict;//收获地址（区/县）
    private String receiverAddress;//收获地址（街道、住址等详细地址）
    private String receiverMobile;//收货人手机
    private String receiverZip;//收货人邮编
    private String receiver;//收货人
    private Integer invoiceType = 0;//发票类型(0无发票1普通发票，2电子发票，3增值税发票)
    private Integer sourceType = 2;//订单来源：1:app端，2：pc端，3：M端，4：微信端，5：手机qq端

    @Transient
    private OrderStatus orderStatus;//订单状态表，一对一

    @Transient
    private List<OrderDetail> orderDetails;//订单详情表，一对多
}
