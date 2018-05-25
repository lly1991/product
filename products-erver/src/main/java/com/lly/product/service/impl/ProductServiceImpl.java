package com.lly.product.service.impl;


import com.lly.product.common.DecreaseStockInput;
import com.lly.product.common.ProductInfoOutput;
import com.lly.product.dataobject.ProductInfo;
import com.lly.product.enums.ProductStatusEnum;
import com.lly.product.enums.ResultEnum;
import com.lly.product.exception.ProductException;
import com.lly.product.repository.ProductInfoRepository;
import com.lly.product.service.ProductService;
import com.lly.product.utils.JsonUtil;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by 廖师兄
 * 2017-12-09 21:59
 */
@Service
public class ProductServiceImpl implements ProductService
{

    @Autowired
    private ProductInfoRepository productInfoRepository;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Override
    public List<ProductInfo> findUpAll()
    {
        return productInfoRepository.findByProductStatus(ProductStatusEnum.UP.getCode());
    }

    @Override
    public List<ProductInfoOutput> findList(List<String> productIdList)
    {
        return productInfoRepository.findByProductIdIn(productIdList).stream()
                .map(e ->
                {
                    ProductInfoOutput output = new ProductInfoOutput();
                    BeanUtils.copyProperties(e, output);
                    return output;
                })
                .collect(Collectors.toList());
    }

    @Override
    public void decreaseStock(List<DecreaseStockInput> decreaseStockInputList)
    {
        List<ProductInfo> productInfoList = decreaseStockProcess(decreaseStockInputList);

        //发送mq消息
        List<ProductInfoOutput> productInfoOutputList = productInfoList.stream().map(e ->
        {
            ProductInfoOutput output = new ProductInfoOutput();
            BeanUtils.copyProperties(e, output);
            return output;
        }).collect(Collectors.toList());
        amqpTemplate.convertAndSend("productInfo", JsonUtil.toJson(productInfoOutputList));

    }

    @Transactional
    public List<ProductInfo> decreaseStockProcess(List<DecreaseStockInput> decreaseStockInputList)
    {
        List<ProductInfo> productInfoList = new ArrayList<>();
        for (DecreaseStockInput decreaseStockInput : decreaseStockInputList)
        {


            ProductInfo productInfoOptional = productInfoRepository.findOne(decreaseStockInput.getProductId());
            //判断商品是否存在
            if (productInfoOptional==null)
            {
                throw new ProductException(ResultEnum.PRODUCT_NOT_EXIST);
            }


            //库存是否足够
            Integer result = productInfoOptional.getProductStock() - decreaseStockInput.getProductQuantity();
            if (result < 0)
            {
                throw new ProductException(ResultEnum.PRODUCT_STOCK_ERROR);
            }

            productInfoOptional.setProductStock(result);
            productInfoRepository.save(productInfoOptional);
            productInfoList.add(productInfoOptional);
        }
        return productInfoList;
    }
}
