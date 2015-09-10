package com.spark.media.match;

import static org.junit.Assert.*;

import org.junit.Test;

public class MatcherImplTest
{

	@Test
	public void testAppendTail()
	{
		MatcherImpl matcher = new MatcherImpl() ;
		System.out.println( matcher.appendTail( "尊敬的客户，您好！您已成功订购中国移动的%1业务，%2元/月，从%3开始生效。如需帮助，请咨询10086。中国移动", "推荐存100送300优惠活动，回复10查看详情" ));
		System.out.println( matcher.appendTail( "尊敬的客户，您的实名登记资料已提交成功。为更好保障您的权益，建议您尽快携带SIM卡和身份证件，凭服务密码亲临我司沟通100服务厅办理身份证件确认。感谢您的支持。中国移动广东公司", "推荐存100送300优惠活动，回复10查看详情" ));
		System.out.println( matcher.appendTail( "尊敬的客户：您已经办理了%1,无须再重新办理,如有疑问请致电10086,或到沟通100服务厅咨询。中国移动深圳分公司.", "推荐存100送300优惠活动，回复10查看详情" ));
		System.out.println( matcher.appendTail( "%3移动温馨提醒：您%1取消的《%2》业务于%4生效，套餐内赠送业务将于下月起收费，请及时取消，感谢您的使用！", "推荐存100送300优惠活动，回复10查看详情" ));
	}

}
