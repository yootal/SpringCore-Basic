package core.basic;

import core.basic.member.Grade;
import core.basic.member.Member;
import core.basic.member.MemberService;
import core.basic.member.MemberServiceImpl;
import core.basic.order.Order;
import core.basic.order.OrderService;
import core.basic.order.OrderServiceImpl;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class OrderApp {
    public static void main(String[] args) {
//        AppConfig appConfig = new AppConfig();
//        MemberService memberService = appConfig.memberService();
//        OrderService orderService = appConfig.orderService();
//        MemberServ ice memberService = new MemberServiceImpl(null);
//        OrderService orderService = new OrderServiceImpl(null,null);

        // ApplicationContext = 스프링 컨테이너
        // 스프링 컨테이너에 등록된 객체 = 스프링 빈
        // 스프링 컨테이너에서 객체를 스프링 빈으로 등록하고, 스프링 컨테이너에서 빈을 찾아 사용하도록 변경
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(AppConfig.class);
        MemberService memberService = applicationContext.getBean("memberService", MemberService.class);
        OrderService orderService = applicationContext.getBean("orderService", OrderService.class);

        Long memberId = 1L;
        Member member = new Member(memberId, "memberA", Grade.VIP);
        memberService.join(member);

        Order order = orderService.createOrder(memberId, "itemA", 20000);

        System.out.println("order = " + order.toString());
        System.out.println("order.calculatePrice = " + order.calculatePrice());
    }
}
