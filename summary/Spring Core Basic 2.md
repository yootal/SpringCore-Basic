# Spring Core Basic 2

# 컴포넌트 스캔

= 설정 정보가 없어도 자동으로 스프링 빈을 등록하는 기능

의존관계 자동 주입은 @Autowired

### 사용

설정 정보에 @ComponentScan

→ @Component 어노테이션이 붙은 클래스를 스캔해서 스프링 빈으로 등록

→ 의존 관계 주입은 @Autowired

```java
@Component
public class OrderServiceImpl implements OrderService {
	private final MemberRepository memberRepository;
	private final DiscountPolicy discountPolicy;
	
	@Autowired
	public OrderServiceImpl(MemberRepository memberRepository, DiscountPolicy discountPolicy) {
		this.memberRepository = memberRepository;
		this.discountPolicy = discountPolicy;
	}
}
```

- 컴포넌트 스캔 시 스프링 빈의 기본 이름은 클래스명을 사용하되 맨 앞글자는 소문자
- 설정 정보 클래스의 위치를 프로젝트 최상단으로 두면 하위는 모두 컴포넌트 스캔 대상이 된다.
- @SpringBootApplication에 포함되어 이를 프로젝트 시작 루트 위치에 두면 된다.

### @Autowired

- 생성자에 @Autowired 지정 시 스프링 컨테이너가 자동으로 해당 스프링 빈을 찾아서 주입한다.

### 컴포넌트 스캔 대상

- @Component
- @Controller (스프링 MVC 컨트롤러로 인식)
- @Service (특별한 처리는 X, 비즈니스 계층 인식)
- @Repository (데이터 접근 계층)
- @Configuration

### 빈 중복 등록과 충돌

자동 빈 등록 vs 자동 빈 등록

→ ConflictingBeanDefinitionException 예외 발생

수동 빈 등록 vs 자동 빈 등록

→ 수동 빈 등록이 우선권을 가진다. 꼬일 경우 잡기 어려운 버그 발생 주의

# 의존관계 자동 주입

1. 생성자 주입
    1. 생정자 호출 시점에 딱 1번만 호출 보장
    2. 불변, 필수 의존관계에 사용
    3. 생성자가 딱 1개라면 @Autowired 생략 가능
2. 수정자 주입 (Setter)
    1. 선택, 변경 가능성이 있는 의존관계에 사용
    2. 자바진 프로퍼티 규약의 수정자 메서드 방식을 사용
3. 필드 주입
    1. 코드가 간결하지만 외부에서 변경이 불가능해 **테스트가 힘들다.**
    2. DI 프레임워크가 없으면 아무것도 할 수 없다.
    3. **쓰지마** (테스트코드, @Configuration 같은 곳에서만 특별한 용도 사용)
4. 일반 메서드 주입
    1. 한번에 여러 필드를 주입 받을 수 있다.
    2. 일반적으로 잘 사용하지 않는다.

### 생성자 주입을 써라

- 불변하게 설계
- 누락 예방 (주입 데이터 누락시 컴파일 오류 발생)
- final 키워드 (값이 설정되지 않는 오류를 컴파일 시점에 막아준다.)
- 프레임 워크에 의존하지 않고 순수한 자바 언어의 특징을 잘 살리는 방

### Lombok  활용 단축

```java
@Component
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
	private final MemberRepository memberRepository;
	private final DiscountPolicy discountPolicy;
}
```

### 조회 빈이 2개 이상 문제 해

@Autowired는 타입으로 조회하기 때문

1. @Autowired 필드 명 매칭

```java
@Autowired
private DiscountPolicy **rateDiscountPolicy**
```

1. @Qualifier 끼리 매칭 → 빈 이름 매칭
2. @Primary 사용 → 우선순위는 @Qualifier 우위

### 자동, 수동 실무 운영

- 편리한 자동 기능을 기본으로 사용하자
- 직접 등록하는 기술 지원 객체는 수동 등록
- 다형성을 적극 활용하는 비즈니스 로직은 수동 등록을 고민해보자

# 빈 생명주기 콜백

### 스프링 빈의 이벤트 라이프사이클

스프링 컨테이너 생성 → 스프링 빈 생성 → 의존관계 주입 → 초기화 콜백 → 사용 → 소멸전 콜백 → 스프링 종료

### 빈 생명주기 콜백

1. 인터페이스(InitializingBean, DisposableBean)
2. 설정 정보에 초기화 메서드, 종료 메서드 지정
3. @PostConstruct, @PreDestroy 어노테이션 지원

### 사용

- @PostConstruct, @PreDestroy 애노테이션을 사용하자
- 코드를 고칠 수 없는 외부 라이브러리를 초기화, 종료해야 하면 @Bean 의 initMethod , destroyMethod 를 사용하자.

# 빈 스코프

:빈이 존재할 수 있는 범위

1. 싱글톤
2. 프로토타입: 스프링 컨테이너는 빈의 생성, 의존관계 주입까지만 관여하고더는 관리하지 않는 짧은 범위의 스코프이다.
3. 웹 관련 스코프
    1. request: 웹 요청이 들어오고 나갈때 까지 유지되는 스코프
    2. session: 웹 세션이 생성되고 종료될 때 까지 유지되는 스코프
    3. application: 웹의 서블릿 컨텍스트와 같은 범위로 유지되는 스코프

**스프링 컨테이너는 프로토타입 빈을 생성하고, 의존관계 주입, 초기화까지만 처리**한다.

클라이언트에 빈을 반환하고, 이후 스프링 컨테이너는 생성된 프로토타입 빈을 관리하지 않는다. 

프로토타입 빈을 관리할 책임은 프로토타입 빈을 받은 클라이언트에 있다. 그래서 @PreDestroy 같은 종료 메서드가 호출되지 않는다.

### 싱글톤 빈에서 프로토타입 빈 사용

```java
public class SingletonWithPrototypeTest1 {
    @Test
    void singletonClientUsePrototype() {
        AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(ClientBean.class, PrototypeBean.class);
        ClientBean clientBean1 = ac.getBean(ClientBean.class);
        int count1 = clientBean1.logic();
        assertThat(count1).isEqualTo(1);
        ClientBean clientBean2 = ac.getBean(ClientBean.class);
        int count2 = clientBean2.logic();
        assertThat(count2).isEqualTo(2);
    }

    static class ClientBean {
        private final PrototypeBean prototypeBean;

        @Autowired
        public ClientBean(PrototypeBean prototypeBean) {
            this.prototypeBean = prototypeBean;
        }

        public int logic() {
            prototypeBean.addCount();
            int count = prototypeBean.getCount();
            return count;
        }
    }

    @Scope("prototype")
    static class PrototypeBean {
        private int count = 0;

        public void addCount() {
            count++;
        }

        public int getCount() {
            return count;
        }

        @PostConstruct
        public void init() {
            System.out.println("PrototypeBean.init " + this);
        }

        @PreDestroy
        public void destroy() {
            System.out.println("PrototypeBean.destroy");
        }
    }
}
```

싱글톤 내부에 가지고 있는 프로토타입 빈은 이미 과거에 주입이 끝난 빈이다.

주입 시점에 스프링 컨테이너에 요청해서 프로토타입 빈이 새로 생성이 된 것이지, 사용할 때마다 새로 생성되는 것은 아니다.

## 문제 해결

1. 싱글톤 빈이 프로토타입을 사용할 때마다 스프링 컨테이너에 새로 요청

```java
public int logic() {
    PrototypeBean prototypeBean = ac.getBean(PrototypeBean.class);
    prototypeBean.addCount();
    int count = prototypeBean.getCount();
    return count;
}
```

2. **ObjectFactory, ObjectProvider**

```java
@Autowired
private ObjectProvider<PrototypeBean> prototypeBeanProvider;

public int logic() {
    PrototypeBean prototypeBean = prototypeBeanProvider.getObject();
    prototypeBean.addCount();
    int count = prototypeBean.getCount();
    return count;
}
```

내부에서 스프링 컨테이너를 통해 해당 빈을 찾아서 반환 (DL)

3. 스프링 부트 3.0은 jakarta.inject.Provider 사용

```java
@Autowired
private Provider<PrototypeBean> provider;

public int logic() {
    PrototypeBean prototypeBean = provider.get();
    prototypeBean.addCount();
    int count = prototypeBean.getCount();
    return count;
}
```

## 웹 스코프

웹 환경에서 동작, 스프링이 해당 스코프의 종료시점까지 관리한다. 종료 메서드가 호출된다.

1. request: HTTP 요청 하나가 들어오고 나갈 때까지 유지되는 스코프, 각각의 HTTP 요청마다 별도의 빈 인스턴스가 생성되고, 관리된다.
2. session: HTTP Session과 동일한 생명주기
3. application: 서블릿 컨텍스트와 동일한 생명주기
4. websocket: 웹 소켓과 동일한 생명주기

## 스코프와 Provider

```java
@Service
@RequiredArgsConstructor
public class LogDemoService {
    private final ObjectProvider<MyLogger> myLoggerProvider;

    public void logic(String id) {
        MyLogger myLogger = myLoggerProvider.getObject();
        myLogger.log("service id = " + id);
    }
}
```

ObjectProvider 덕분에 ObjectProvider.getObject() 를 호출하는 시점까지 request scope 빈의 생성을 지연할 수 있다.

ObjectProvider.getObject() 를 호출하시는 시점에는 HTTP 요청이 진행중이므로 request scope 빈의 생성이 정상 처리된다.

ObjectProvider.getObject() 를 LogDemoController , LogDemoService 에서 각각 한번씩 따로 호출해도 같은 HTTP 요청이면 같은 스프링 빈이 반환된다!

## 스코프와 프록시

```java
@Component
@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class MyLogger {
}
```

MyLogger의 가짜 프록시 클래스를 만들어두고 HTTP request와 상관 없이 가짜 프록시 클래스를 다른 빈에 미리 주입해 둘 수 있다.

**CGLIB라는 라이브러리로 내 클래스를 상속 받은 가짜 프록시 객체를 만들어서 주입한다.**

**가짜 프록시 객체는 요청이 오면 그때 내부에서 진짜 빈을 요청하는 위임 로직이 들어있다.**

**가짜 프록시 객체는 원본 클래스를 상속 받아서 만들어졌기 때문에 이 객체를 사용하는 클라이언트 입장에서는 사실 원본인지 아닌지도 모르게, 동일하게 사용할 수 있다(다형성).**

### 동작 정리

- CGLIB라는 라이브러리로 내 클래스를 상속 받은 가짜 프록시 객체를 만들어서 주입한다.
- 이 가짜 프록시 객체는 실제 요청이 오면 그때 내부에서 실제 빈을 요청하는 위임 로직이 들어있다.
- 가짜 프록시 객체는 실제 request scope와는 관계가 없다. 그냥 가짜이고, 내부에 단순한 위임 로직만 있고, 싱글톤 처럼 동작한다.

### 특징 정리

- 프록시 객체 덕분에 클라이언트는 마치 싱글톤 빈을 사용하듯이 편리하게 request scope를 사용할 수 있다.
- 사실 Provider를 사용하든, 프록시를 사용하든 핵심 아이디어는 진짜 객체 조회를 꼭 필요한 시점까지 지연처리한다는 점이다.
- 단지 애노테이션 설정 변경만으로 원본 객체를 프록시 객체로 대체할 수 있다. 이것이 바로 다형성과 DI 컨테이너가 가진 큰 강점이다.
- 꼭 웹 스코프가 아니어도 프록시는 사용할 수 있다.

### 주의점

- 마치 싱글톤을 사용하는 것 같지만 다르게 동작하기 때문에 결국 주의해서 사용해야 한다.
- 이런 특별한 scope는 꼭 필요한 곳에만 최소화해서 사용하자, 무분별하게 사용하면 유지보수하기 어려워진다.