package com.xiaohai.llminterface.function;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import org.springframework.stereotype.Service;

/**
 * Title Mock order service.<br>
 * Description Mock order service.<br>
 *
 * @author yuanci.ytb
 * @since 2024/8/16 11:29
 */

@Service
public class MockOrderService {

	public record Response(String description) {
	}

	public Response getOrder(Request request) {
		String productName = "尤尼克斯羽毛球拍";
		return new Response(String.format("%s的订单编号为%s, 购买的商品为: %s", request.userId, request.orderId, productName));
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public record Request(
			@JsonProperty(required = true,
					value = "orderId") @JsonPropertyDescription("订单编号, 比如1001***") String orderId,
			@JsonProperty(required = true,
					value = "userId") @JsonPropertyDescription("用户编号, 比如2001***") String userId) {
	}

}
