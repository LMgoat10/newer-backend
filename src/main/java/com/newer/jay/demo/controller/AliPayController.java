package com.newer.jay.demo.controller;

import com.alipay.easysdk.factory.Factory;
import com.newer.jay.demo.config.AliPayConfig;
import com.newer.jay.demo.entity.AliPay;
import com.newer.jay.demo.entity.TicketOrder;
import com.newer.jay.demo.mapper.TicketOrderMapper;

import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("alipay")
@Transactional(rollbackFor = Exception.class)
public class AliPayController {

    @Resource
    AliPayConfig aliPayConfig;

    @Resource
    private TicketOrderMapper ticketOrderMapper;
    private static final String GATEWAY_URL ="https://openapi-sandbox.dl.alipaydev.com/gateway.do";
    private static final String FORMAT ="JSON";
    private static final String CHARSET ="utf-8";
    private static final String SIGN_TYPE ="RSA2";
    @GetMapping("/pay") // &subject=xxx&traceNo=xxx&totalAmount=xxx
    public void pay(AliPay aliPay, HttpServletResponse httpResponse) throws Exception {
        AlipayClient alipayClient = new DefaultAlipayClient(GATEWAY_URL, aliPayConfig.getAppId(),
                aliPayConfig.getAppPrivateKey(), FORMAT, CHARSET, aliPayConfig.getAlipayPublicKey(), SIGN_TYPE);
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        request.setNotifyUrl(aliPayConfig.getNotifyUrl());
        request.setBizContent("{\"out_trade_no\":\"" + aliPay.getTraceNo() + "\","
                + "\"total_amount\":\"" + aliPay.getTotalAmount() + "\","
                + "\"subject\":\"" + aliPay.getSubject() + "\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");
        String form = "";
        try {
            // 调用SDK生成表单
            form = alipayClient.pageExecute(request).getBody();
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        httpResponse.setContentType("text/html;charset=" + CHARSET);
        // 直接将完整的表单html输出到页面
        httpResponse.getWriter().write(form);
        httpResponse.getWriter().flush();
        httpResponse.getWriter().close();
    }

    @PostMapping("/notify")  // 注意这里必须是POST接口
    public String payNotify(HttpServletRequest request) throws Exception {
        if (request.getParameter("trade_status").equals("TRADE_SUCCESS")) {
            System.out.println("=========支付宝异步回调========");

            Map<String, String> params = new HashMap<>();
            Map<String, String[]> requestParams = request.getParameterMap();
            for (String name : requestParams.keySet()) {
                params.put(name, request.getParameter(name));
                // System.out.println(name + " = " + request.getParameter(name));
            }

            String tradeNo = params.get("out_trade_no");
            String gmtPayment = params.get("gmt_payment");
            String alipayTradeNo = params.get("trade_no");
            // 支付宝验签
            if (Factory.Payment.Common().verifyNotify(params)) {
                // 验签通过
                String subject = params.get("subject");
                System.out.println("交易状态: " + params.get("trade_status"));
                System.out.println("支付宝交易凭证号: " + params.get("trade_no"));
                System.out.println("商户订单号: " + params.get("out_trade_no"));
                System.out.println("交易金额: " + params.get("total_amount"));
                System.out.println("买家在支付宝唯一id: " + params.get("buyer_id"));
                System.out.println("买家付款时间: " + params.get("gmt_payment"));
                System.out.println("买家付款金额: " + params.get("buyer_pay_amount"));
                // 更新订单未已支付
            }
        }
        return "success";
    }

    // 2. 支付状态查询接口 - 从支付宝接口获取实时状态
    @GetMapping("/check-status")
    public ResponseEntity<Map<String, Object>> checkPaymentStatus(@RequestParam String tradeNo) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (tradeNo == null || tradeNo.trim().isEmpty()) {
                response.put("success", false);
                response.put("status", "INVALID_PARAMS");
                response.put("message", "交易号不能为空");
                return ResponseEntity.badRequest().body(response);
            }
            
            // 1. 首先尝试从支付宝API查询订单状态
            AlipayClient alipayClient = new DefaultAlipayClient(GATEWAY_URL, aliPayConfig.getAppId(),
                    aliPayConfig.getAppPrivateKey(), FORMAT, CHARSET, aliPayConfig.getAlipayPublicKey(), SIGN_TYPE);
            
            AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
            request.setBizContent("{\"out_trade_no\":\"" + tradeNo.trim() + "\"}");
            
            AlipayTradeQueryResponse alipayResponse = alipayClient.execute(request);
            
            if (alipayResponse.isSuccess()) {
                // 支付宝返回成功，解析订单状态
                String tradeStatus = alipayResponse.getTradeStatus();
                String totalAmount = alipayResponse.getTotalAmount();
                String buyerUserId = alipayResponse.getBuyerUserId();
                Date paymentDate = alipayResponse.getSendPayDate();
                String gmtPayment = null;
                if (paymentDate != null) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    gmtPayment = sdf.format(paymentDate);
                }
                
                response.put("success", true);
                response.put("message", "查询成功");
                
                Map<String, Object> orderInfo = new HashMap<>();
                orderInfo.put("tradeNo", tradeNo.trim());
                orderInfo.put("alipayTradeNo", alipayResponse.getTradeNo());
                orderInfo.put("totalAmount", totalAmount);
                orderInfo.put("buyerUserId", buyerUserId);
                orderInfo.put("paymentTime", gmtPayment);
                orderInfo.put("subject", alipayResponse.getSubject());
                
                // 根据支付宝返回的状态设置我们的状态
                if ("TRADE_SUCCESS".equals(tradeStatus) || "TRADE_FINISHED".equals(tradeStatus)) {
                    response.put("status", "PAID");
                    orderInfo.put("status", "PAID");
                    
                    // 如果支付成功，同步更新本地数据库
                    try {
                        TicketOrder localOrder = ticketOrderMapper.selectById(tradeNo.trim());
                        if (localOrder != null && localOrder.getStatus() != TicketOrder.OrderStatus.PAID) {
                            localOrder.setStatus(TicketOrder.OrderStatus.PAID);
                            // 可以设置支付时间等其他信息
                            ticketOrderMapper.updateById(localOrder);
                        }
                    } catch (Exception e) {
                        System.err.println("同步本地订单状态失败: " + e.getMessage());
                        // 不影响查询结果，只记录错误
                    }
                    
                } else if ("WAIT_BUYER_PAY".equals(tradeStatus)) {
                    response.put("status", "PENDING");
                    orderInfo.put("status", "PENDING");
                } else if ("TRADE_CLOSED".equals(tradeStatus)) {
                    response.put("status", "CANCELLED");
                    orderInfo.put("status", "CANCELLED");
                } else {
                    response.put("status", "UNKNOWN");
                    orderInfo.put("status", tradeStatus);
                }
                
                response.put("orderInfo", orderInfo);
                
            } else {
                // 支付宝API查询失败，可能订单不存在或参数错误
                String errorCode = alipayResponse.getCode();
                String errorMsg = alipayResponse.getMsg();
                
                if ("ACQ.TRADE_NOT_EXIST".equals(errorCode)) {
                    // 订单不存在，检查本地数据库
                    TicketOrder localOrder = ticketOrderMapper.selectById(tradeNo.trim());
                    if (localOrder != null) {
                        // 本地有订单但支付宝没有，说明订单可能还未支付
                        response.put("success", true);
                        response.put("status", "UNPAID");
                        response.put("message", "订单存在但尚未支付");
                        
                        Map<String, Object> orderInfo = new HashMap<>();
                        orderInfo.put("tradeNo", localOrder.getId());
                        orderInfo.put("totalAmount", localOrder.getTotalAmount());
                        orderInfo.put("status", "UNPAID");
                        response.put("orderInfo", orderInfo);
                    } else {
                        // 本地也没有订单
                        response.put("success", false);
                        response.put("status", "NOT_FOUND");
                        response.put("message", "订单不存在");
                    }
                } else {
                    // 其他错误
                    response.put("success", false);
                    response.put("status", "QUERY_ERROR");
                    response.put("message", "查询失败: " + errorMsg + " (错误码: " + errorCode + ")");
                }
            }
            
            return ResponseEntity.ok(response);
            
        } catch (AlipayApiException e) {
            System.err.println("调用支付宝API异常: " + e.getMessage());
            e.printStackTrace();
            
            // API调用失败，降级到本地数据库查询
            try {
                TicketOrder localOrder = ticketOrderMapper.selectById(tradeNo.trim());
                if (localOrder != null) {
                    response.put("success", true);
                    response.put("status", localOrder.getStatus().toString());
                    response.put("message", "从本地数据库查询成功（支付宝API暂不可用）");
                    
                    Map<String, Object> orderInfo = new HashMap<>();
                    orderInfo.put("tradeNo", localOrder.getId());
                    orderInfo.put("totalAmount", localOrder.getTotalAmount());
                    orderInfo.put("paymentTime", localOrder.getPayTime());
                    orderInfo.put("status", localOrder.getStatus().toString());
                    response.put("orderInfo", orderInfo);
                } else {
                    response.put("success", false);
                    response.put("status", "NOT_FOUND");
                    response.put("message", "订单不存在且支付宝API不可用");
                }
                return ResponseEntity.ok(response);
            } catch (Exception dbE) {
                response.put("success", false);
                response.put("status", "ERROR");
                response.put("message", "查询失败: 支付宝API和本地数据库均不可用");
                return ResponseEntity.status(500).body(response);
            }
            
        } catch (Exception e) {
            System.err.println("查询支付状态异常: " + e.getMessage());
            e.printStackTrace();
            
            response.put("success", false);
            response.put("status", "ERROR");
            response.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}