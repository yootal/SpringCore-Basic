# Spring Core Basic 1

# 의존관계 주입

### 주문 서비스 구현체 코드 (개선 전)

```java
public class OrderServiceImpl implements OrderService {
	// private final DiscountPolicy discountPolicy = new FixDiscountPolicy();
	private final DiscountPolicy discountPolicy = new RateDiscountPolicy();
}
```

1. 역할과 구현을 분리
2. 다형성 활용, 인터페이스와 구현 객체 분리
3. OCP, DIP를 준수한 듯 하지만 아니다.

   → DIP: 추상(인터페이스) 뿐만 아니라 구체(구현) 클래스에도 의존하고 있다.

   → OCP: 기능을 확장해서 변경하려면 클라이언트 코드에 영향을 준다.

### 개선 방법

1. 클라이언트 코드인 OrderServiceImpl은 DiscountPolicy의 인터페이스 뿐만 아니라 구체 클래스도 함께 의존 → 추상에만 의존하도록 변경해야 한다.

```java
public class OrderServiceImpl implements OrderService {
	//private final DiscountPolicy discountPolicy = new RateDiscountPolicy();
	private DiscountPolicy discountPolicy;
}
```

1. 인터페이스에마 의존하도록 설계와 코드를 변경했지만 구현체가 없어서 NPE 발생 → **누군가 클라이언트인 OrderServiceImpl에 DiscountPolicy의 구현 객체를 대신 생성하여 주입해주어야 한다. (=의존 관계 주입, DI)**

```java
 private final DiscountPolicy discountPolicy;

 public OrderServiceImpl(DiscountPolicy discountPolicy) {
	 this.discountPolicy = discountPolicy;
 }
```

```java
public class AppConfig {
	public OrderService orderService() {
		return new OrderServiceImpl(
			discountPolicy());
	}
	public DiscountPolicy discountPolicy() {
		return new FixDiscountPolicy();
	}
}
```

1. 객체의 생성과 연결은 AppConfig 담당
   1. DIP 완성: 구체 클래스를 몰라도 된다.
   2. 관심사의 분리: 객체를 생성하고 연결하는 역할(AppConfig)과 실행하는 역할(OrderServiceImpl)이 분리
   3. 클라이언트인 OrderServiceImpl 입장에서 보면 의존관계를 마치 외부에서 주입해 주는 것 같다고 해서 **의존관계 주입**이라 한다. OrderServiceImpl은 기능을 실행하는 책임만 진다.

# IoC, DI, 그리고 컨테이너

### 제어의 역전 IoC(Inversion of Control)

- 프로그램의 제어 흐름을 직접 제어하는 것이 아니라 외부에서 관리하는 것을 제어의 역전(IoC)이라 한다(AppConfig).

### 프레임워크 vs 라이브러리

- 프레임워크가 내가 작성한 코드를 제어하고, 대신 실행하면 그것은 프레임워크가 맞다. (JUnit)
- 반면에 내가 작성한 코드가 직접 제어의 흐름을 담당한다면 그것은 프레임워크가 아니라 라이브러리다.

### 의존관계 주입 DI(Dependency Injection)

- 애플리케이션 실행 시점(런타임)에 외부에서 실제 구현 객체를 생성하고 클라이언트에 전달해서 클라이언트와 서버의 실제 의존관계가 연결 되는 것을 의존관계 주입이라 한다.
- 객체 인스턴스를 생성하고, 그 참조값을 전달해서 연결된다.
- 의존관계 주입을 사용하면 클라이언트 코드를 변경하지 않고, 클라이언트가 호출하는 대상의 타입 인스턴스를 변경할 수 있다.
- 의존관계 주입을 사용하면 정적인 클래스 의존관계를 변경하지 않고, 동적인 객체 인스턴스 의존관계를 쉽게 변경할 수 있다.

### IoC 컨테이너, DI 컨테이너

- AppConfig 처럼 객체를 생성하고 관리하면서 의존관계를 연결해 주는 것을
  IoC 컨테이너 또는 DI 컨테이너라 한다.
- 의존관계 주입에 초점을 맞추어 최근에는 주로 DI 컨테이너라 한다.

### 스프링 컨테이너

- ApplicationContext
- @Configuration을 설정 정보로 사용, @Bean이 붙은 메서드를 모두 호출해서 반환된 객체를 스프링 컨테이너에 등록 (=스프링 빈), 빈 이름은 메서드 이름 사용

### 지원 설정 형식

- 자바 코드, XML, Groovy

# 싱글톤 컨테이너

### 싱글톤 패턴

스프링 없는 순수 DI 컨테이너인 AppConfig는 요청마다 객체를 새로 생성

→ 메모리 낭비가 심하다

→ 해당 객체가 1개만 생성되고 공유하도록 설계

→ 싱글톤 패턴 사용

```java
public class SingletonService {
	//1. static 영역에 객체를 딱 1개만 생성해둔다.
	private static final SingletonService instance = new SingletonService();
	//2. public으로 열어서 객체 인스턴스가 필요하면 이 static 메서드를 통해서만 조회하도록 허용한다.
	public static SingletonService getInstance() {
		return instance;
	}
	//3. 생성자를 private으로 선언해서 외부에서 new 키워드를 사용한 객체 생성을 못하게 막는다.
	private SingletonService() {}
	public void logic() {
		System.out.println("싱글톤 객체 로직 호출");
	}
}
```

### 싱글톤 패턴 문제점

1. 싱글톤 패턴을 구현하는 코드 자체가 많이 들어간다.
2. 클라이언트가 구체 클래스에 의존 → DIP 위반
3. 1번으로 인한 OCP 원칙 위반 가능성 높음
4. 테스트의 어려움
5. 내부 속성을 변경하거나 초기화하기 어렵다.
6. private 생성자로 자식 클래스를 만들기 어렵다.

   **→ 유연성이 떨어진다.**

### 싱글톤 컨테이너

스프링 컨테이너는 싱글톤 패턴의 문제점을 해결하면서, 객체 인스턴스를 싱글톤으로 관리 (스프링 빈)

### 싱글톤 방식의 주의점

- 여러 클라이언트가 하나의 같은 객체 인스턴스를 공유하기 때문에 상대를 유지(stateful)하게 설계하면 안된다.
- 무상태(stateless)로 설계해야 한다.
  - 특정 클라이언트에 의존적인 필드가 있으면 안된다.
  - 특정 클라이언트가 값을 변경할 수 있는 필드가 있으면 안된다.
  - 가급적 읽기만 가능해야 한다.
  - 필드 대신에 자바에서 공유되지 않는 지역변수, 파라미터, ThreadLocal 등을 사용해야 한다.
- **스프링 빈의 필드에 공유 값을 설정하면 정말 큰 장애가 발생할 수 있다.**

### @Configuration을 붙여야 싱글톤이 보장된다.

- AppConfig를 상속받은 임의의 다른 클래스를 만들고 이를 스프링 빈으로 등록 (CGLIB)
- @Bean이 붙은 메서드마다 이미 스프링 빈이 존재하면 존재하는 빈을 반환하고, 스프링 빈이 없으면 생성해서 스프링 빈으로 등록하고 반환하는 코드가 동적으로 만들어 진다.
