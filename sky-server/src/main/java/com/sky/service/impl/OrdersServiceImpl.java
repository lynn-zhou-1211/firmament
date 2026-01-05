package com.sky.service.impl;

import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.AddressBook;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.entity.ShoppingCart;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.AddressBookMapper;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrdersMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.OrdersService;
import com.sky.utils.SnowflakeUtils;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrdersServiceImpl implements OrdersService {

    @Autowired
    OrdersMapper ordersMapper;

    @Autowired
    OrderDetailMapper orderDetailMapper;

    @Autowired
    ShoppingCartMapper shoppingCartMapper;

    @Autowired
    AddressBookMapper addressBookMapper;

    @Transactional
    @Override
    public OrderSubmitVO submit(OrdersSubmitDTO ordersSubmitDTO) {
        // 1. 数据校验：地址存在？购物车为空？
        AddressBook addressBook = addressBookMapper.getById(ordersSubmitDTO.getAddressBookId());
        if (addressBook == null) {
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }

        Long userId = BaseContext.getCurrentId();
        List<ShoppingCart> cart = shoppingCartMapper.getByConditions(ShoppingCart.
                builder().
                userId(userId).
                build());
        if (cart == null || cart.isEmpty()) {
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }

        // 2. 构造订单主体：
        //      前端数据拷贝：地址id，支付方式，备注，预计送达时间，配送方式，打包费，餐具数量
        //      地址填充：手机，地址，收货人
        //      核心状态：订单状态，支付状态，订单号，用户id，当前时间
        //      重新计算总金额
        Orders order = new Orders();

        BeanUtils.copyProperties(ordersSubmitDTO,order);

        order.setAddress(addressBook.getDetail());
        order.setPhone(addressBook.getPhone());
        order.setConsignee(addressBook.getConsignee());

        order.setStatus(Orders.PENDING_PAYMENT);
        order.setPayStatus(Orders.UN_PAID);
        order.setNumber(SnowflakeUtils.nextIdStr());  // 雪花算法生成订单号
        order.setUserId(userId);
        order.setOrderTime(LocalDateTime.now());

        // 3. 插入订单主体
        ordersMapper.insert(order);

        // 4. 构造订单明细，遍历 cart，拷贝信息
        //      菜品名称，图片，价格，数量，口味，id
        List<OrderDetail> orderDetailList = new ArrayList<>();
        for (ShoppingCart item : cart) {
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(item,orderDetail);
            orderDetail.setOrderId(order.getId());
            orderDetailList.add(orderDetail);
        }

        // 5. 批量插入订单明细
        orderDetailMapper.insertList(orderDetailList);

        // 6. 清空购物车
        shoppingCartMapper.deleteByUserId(userId);

        // 7. 返回结果：id，订单号，订单金额，下单时间
        OrderSubmitVO orderSubmitVO = OrderSubmitVO.builder()
                .id(order.getId())
                .orderNumber(order.getNumber())
                .orderAmount(order.getAmount())
                .orderTime(order.getOrderTime())
                .build();

        return orderSubmitVO;
    }

    @Override
    public OrderPaymentVO paySuccess(String orderNumber) {
        // 1. 根据订单号查订单
        Long userId = BaseContext.getCurrentId();
        Orders ordersDB = ordersMapper.getByNumberAndUserId(orderNumber, userId);

        // 2. 修改订单状态
        // 支付状态改为：已支付 (PAID)
        Orders orders = Orders.builder()
                .id(ordersDB.getId())
                .status(Orders.TO_BE_CONFIRMED) // 状态变更为：待接单
                .payStatus(Orders.PAID)         // 支付状态：已支付
                .checkoutTime(LocalDateTime.now()) // 结账时间
                .build();

        // 3. 更新数据库
        ordersMapper.update(orders);

        // 4. 返回一个空的VO对象给前端，因为前端已经改了不处理签名，所以这里返回空即可
        OrderPaymentVO vo = new OrderPaymentVO();
        vo.setNonceStr("666");
        vo.setPaySign("666");
        vo.setPackageStr("prepay_id=wx");
        vo.setSignType("RSA");
        vo.setTimeStamp("10000000");

        return vo;
    }

    @Override
    public OrderVO details(Long id) {
        // 1. 根据id查询订单
        Orders orders = ordersMapper.getById(id);

        // 2. 查询该订单对应的菜品/套餐明细
        List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(orders.getId());

        // 3. 封装成VO返回
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(orders, orderVO);
        orderVO.setOrderDetailList(orderDetailList);

        return orderVO;
    }
}
