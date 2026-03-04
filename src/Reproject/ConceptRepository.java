package Reproject;

import java.util.*;

public class ConceptRepository {
    //자바 지식데이터를 한곳에 모으기 위한  ConceptRepository 선언~
    // 이후 findAll()이나 findMethodAll()을 통해 SearchService 및 MainWikiFrame으로 흘러감
    // 전체 내용을 바꿀 수 있는 메소드관리!
    private Map<String, Concept> database = new HashMap<>();

    public ConceptRepository() {
        // 내부 initData() -> initBasicConcepts() 등으로 실행 흐름
        initData();
        initMethod();
    }

    private void initData() {
        initBasicConcepts();       // 기초 (B01~B50)
        initIntermediateConcepts(); // 중급 (I51~I100)
        initAdvancedConcepts();     // 고급 (A101~A130)
    }

    public List<Concept> findMethodAll() {
        //메소드집합 버튼 클릭시 무조건 메소드 관련된것만 나오도록 하기 위한!
        return database.values().
                stream().filter(c->"메소드"
                        .equals(c.getCategory()))
                .sorted(Comparator.comparing(Concept::getTitle))
                .toList();
    }

    private void initMethod(){
        addConcept(new Concept("M01" ,"기본 메소드" ,"기초"));
    }

    private void initBasicConcepts() {
        //아래로부터 기초 중급 고급 자바 지식 넣는중 위 database로 이동
        addConcept(new Concept("B01", "기본 자료형(Primitive)", "기초")
                .addLine("[설명] 자바에서 지원하는 가장 기본적인 데이터 타입으로, 메모리 효율을 위해 정해진 크기를 가진다.")
                .addLine("• [메서드] Integer.toBinaryString(): 정수를 2진수 문자열로 변환하여 메모리 구조를 시각화한다.")
                .addLine("[코드] int num = 10; String binary = Integer.toBinaryString(num);")
                .addLine("[설명] 1. 원시 타입은 실제 값을 스택(Stack) 영역에 직접 저장하여 접근 속도가 매우 빠르다.")
                .addLine("[설명] 2. 객체가 아니므로 null 값을 가질 수 없으며, 선언과 동시에 메모리 크기가 고정된다."));

        addConcept(new Concept("B02", "변수와 상수(final)", "기초")
                .addLine("[설명] 데이터를 저장하는 공간인 변수와, 한 번 초기화하면 변경할 수 없는 상수를 구분한다.")
                .addLine("• [메서드] final: 변수 앞에 붙여 해당 메모리 공간을 '읽기 전용'으로 설정한다.")
                .addLine("[코드] final double PI = 3.14; // PI = 3.15; 시 컴파일 에러 발생")
                .addLine("[설명] 1. 상수는 의도치 않은 데이터 변경을 막아 프로그램의 안정성을 높이는 역할을 한다.")
                .addLine("[설명] 2. 관례적으로 상수는 모두 대문자로 작성하며 단어 사이는 언더바(_)로 연결한다."));

        addConcept(new Concept("B03", "산술 연산자와 나머지", "기초")
                .addLine("[설명] 사칙연산과 더불어 자바에서 배수나 홀짝 판별에 자주 쓰이는 나머지 연산을 다룬다.")
                .addLine("• [메서드] % (Modulo): 나눗셈 후 남은 나머지 값을 구한다.")
                .addLine("[코드] int count = 7; boolean isOdd = (count % 2 != 0);")
                .addLine("[설명] 1. % 연산자는 숫자의 범위를 제한하거나 특정 패턴을 반복시킬 때 필수적이다.")
                .addLine("[설명] 2. 0으로 나누거나 나머지를 구하려 하면 ArithmeticException이 발생하므로 주의해야 한다."));

        addConcept(new Concept("B04", "증감 연산자의 전후위", "기초")
                .addLine("[설명] 변수의 값을 1 증가/감소시킬 때 연산자의 위치에 따른 실행 순서 차이를 이해한다.")
                .addLine("• [메서드] ++, --: 값을 1씩 변화시키는 단항 연산자이다.")
                .addLine("[코드] int a = 5; int b = a++; // b는 5, a는 6 / int c = ++a; // c는 7, a는 7")
                .addLine("[설명] 1. 전위형은 증가 후 대입하고, 후위형은 대입 후 증가하는 원리이다.")
                .addLine("[설명] 2. 가독성을 위해 단독 문장으로 사용하는 것이 실무적으로 가장 안전하다."));

        addConcept(new Concept("B05", "비교 연산자와 결과", "기초")
                .addLine("[설명] 두 피연산자의 크기나 일치 여부를 비교하여 논리값(true/false)을 산출한다.")
                .addLine("• [메서드] ==, !=, >, <, >=, <=: 값의 관계를 따지는 연산자이다.")
                .addLine("[코드] boolean check = (10 >= 5); // 결과: true")
                .addLine("[설명] 1. 비교 연산의 결과물은 항상 boolean 타입으로만 반환된다.")
                .addLine("[설명] 2. 객체 비교 시 ==는 주소를 비교하므로 내용 비교는 equals()를 써야 한다."));

        addConcept(new Concept("B06", "논리 연산자와 단락 평가", "기초")
                .addLine("[설명] 여러 조건을 조합하며, 성능 최적화를 위한 단락 회로 평가(Short-circuit)를 다룬다.")
                .addLine("• [메서드] &&(AND), ||(OR), !(NOT): 논리적인 결합을 수행한다.")
                .addLine("[코드] boolean res = (obj != null) && (obj.isValid()); // null 체크 시 유용")
                .addLine("[설명] 1. &&는 앞이 거짓이면 뒤를 보지 않고, ||는 앞이 참이면 뒤를 무시한다.")
                .addLine("[설명] 2. 이를 통해 불필요한 연산을 줄이고 Null 에러를 사전에 방지할 수 있다."));

        addConcept(new Concept("B07", "if 조건문의 분기", "기초")
                .addLine("[설명] 특정 조건에 따라 코드의 실행 경로를 나누는 가장 기본적인 제어문이다.")
                .addLine("• [메서드] else if: 이전 조건이 거짓일 때 새로운 조건을 제시한다.")
                .addLine("[코드] if(n > 0) { ... } else if(n < 0) { ... } else { ... }")
                .addLine("[설명] 1. 순차적으로 검사하며 한 번 참이 나오면 나머지 블록은 모두 건너뛴다.")
                .addLine("[설명] 2. 조건식의 결과는 반드시 boolean 타입이어야 컴파일이 가능하다."));

        addConcept(new Concept("B08", "switch 문과 값 분기", "기초")
                .addLine("[설명] 변수의 값 하나에 따라 여러 경로 중 하나를 선택하는 다중 분기문이다.")
                .addLine("• [메서드] break: 실행을 마친 후 switch 블록을 즉시 탈출하게 한다.")
                .addLine("[코드] switch(level) { case 1: reward(); break; default: fail(); }")
                .addLine("[설명] 1. break가 누락되면 아래쪽 case까지 연달아 실행되는 Fall-through 현상이 발생한다.")
                .addLine("[설명] 2. Java 12 이후부터는 yield와 화살표 연산자로 더 간결하게 작성이 가능하다."));

        addConcept(new Concept("B09", "for 반복문의 구조", "기초")
                .addLine("[설명] 반복 횟수가 명확할 때 사용하는 루프로, 초기화와 증감이 한 곳에 모여있다.")
                .addLine("• [메서드] 초기화; 조건식; 증감식: 루프의 생명 주기를 결정하는 3요소이다.")
                .addLine("[코드] for(int i=0; i<10; i++) { System.out.println(i); }")
                .addLine("[설명] 1. 초기화식의 변수는 루프가 끝나면 메모리에서 사라지는 지역 변수이다.")
                .addLine("[설명] 2. 배열이나 리스트를 인덱스 번호로 순회할 때 가장 많이 사용되는 방식이다."));

        addConcept(new Concept("B10", "while 문과 조건 반복", "기초")
                .addLine("[설명] 반복 횟수보다 특정 조건이 유지되는 동안 계속 실행해야 할 때 사용한다.")
                .addLine("• [메서드] while(true): 특정 탈출 조건이 나오기 전까지 무한 반복을 수행한다.")
                .addLine("[코드] while(sc.hasNext()) { String data = sc.next(); }")
                .addLine("[설명] 1. 루프 시작 전 조건을 먼저 보므로 처음부터 거짓이면 단 한 번도 실행되지 않는다.")
                .addLine("[설명] 2. 루프 내부에서 조건을 언젠가 거짓으로 만드는 로직이 없으면 무한 루프에 빠진다."));

        addConcept(new Concept("B11", "do-while의 선실행", "기초")
                .addLine("[설명] 조건을 나중에 확인하여 최소 한 번은 무조건 실행이 필요한 경우에 쓴다.")
                .addLine("• [메서드] do { ... } while(조건); 형식을 따른다.")
                .addLine("[코드] do { input = sc.nextInt(); } while(input < 0);")
                .addLine("[설명] 1. 실행 후 마지막에 세미콜론(;)을 붙여야 하는 문법적 차이가 있다.")
                .addLine("[설명] 2. 사용자에게 입력을 먼저 한 번 받고 유효성을 검사할 때 매우 효과적이다."));

        addConcept(new Concept("B12", "break와 continue 제어", "기초")
                .addLine("[설명] 반복문 내부에서 실행 흐름을 강제로 조작하는 제어 키워드이다.")
                .addLine("• [메서드] break: 즉시 탈출 / continue: 다음 반복 회차로 건너뛰기")
                .addLine("[코드] if(i == 5) break; // 루프 종료 / if(i % 2 == 0) continue; // 다음 회차")
                .addLine("[설명] 1. break는 가장 가까운 반복문 하나를 완전히 종료시킨다.")
                .addLine("[설명] 2. continue는 아래쪽 코드를 무시하고 즉시 증감식이나 조건식으로 돌아간다."));

        addConcept(new Concept("B13", "배열 선언과 인덱스", "기초")
                .addLine("[설명] 동일한 타입의 데이터를 하나의 변수로 묶어 관리하는 연속적인 공간이다.")
                .addLine("• [메서드] new: 힙(Heap) 영역에 배열을 위한 실제 메모리를 할당한다.")
                .addLine("[코드] int[] scores = new int[5]; scores[0] = 100;")
                .addLine("[설명] 1. 인덱스는 항상 0부터 시작하며, 마지막 번호는 length-1 이다.")
                .addLine("[설명] 2. 생성 시 정한 크기는 프로그램 도중에 변경할 수 없는 고정 길이이다."));

        addConcept(new Concept("B14", "다차원 배열의 구조", "기초")
                .addLine("[설명] 배열의 요소로 또 다른 배열이 들어있는 중첩된 데이터 구조이다.")
                .addLine("• [메서드] length: 바깥쪽 배열의 길이와 안쪽 배열의 길이를 각각 알 수 있다.")
                .addLine("[코드] int[][] matrix = new int[2][3]; int rowLen = matrix.length;")
                .addLine("[설명] 1. matrix[0] 자체가 하나의 배열 주소를 가리키는 참조 변수가 된다.")
                .addLine("[설명] 2. 행과 열을 가진 테이블 형태의 데이터를 저장하고 관리하기에 최적이다."));

        addConcept(new Concept("B15", "String 클래스와 불변성", "기초")
                .addLine("[설명] 자바의 문자열은 한 번 만들어지면 수정할 수 없는 불변(Immutable) 객체이다.")
                .addLine("• [메서드] length(): 글자 수 / charAt(i): 특정 위치의 문자 추출")
                .addLine("[코드] String s = \"Java\"; s = s + \"!\"; // 새로운 객체가 생성됨")
                .addLine("[설명] 1. 문자열을 수정하면 기존 데이터가 바뀌는 게 아니라 메모리에 새 객체가 생긴다.")
                .addLine("[설명] 2. 빈번한 수정은 메모리 낭비를 부르므로 StringBuilder 사용을 고려해야 한다."));

        addConcept(new Concept("B16", "Main 메서드 상세 분석", "기초")
                .addLine("[설명] 자바 프로그램이 실행될 때 JVM이 가장 먼저 호출하는 표준 입구이다.")
                .addLine("• [메서드] main(String[] args): 외부로부터 인자값을 문자열 배열로 전달받는다.")
                .addLine("[코드] public static void main(String[] args) { ... }")
                .addLine("[설명] 1. public은 어디서든 접근 가능, static은 객체 생성 없이 실행을 뜻한다.")
                .addLine("[설명] 2. 이 서명이 정확히 일치하지 않으면 프로그램은 실행되지 않는다."));

        addConcept(new Concept("B17", "주석의 역할과 종류", "기초")
                .addLine("[설명] 코드를 설명하거나 특정 로직을 잠시 제외할 때 사용하는 문법이다.")
                .addLine("• [메서드] //: 한 줄 주석 / /* ... */: 범위 주석")
                .addLine("[코드] /** JavaDoc 주석은 API 문서를 자동 생성할 때 사용함 */")
                .addLine("[설명] 1. 주석은 컴파일 단계에서 제거되므로 프로그램 성능에는 아무 영향이 없다.")
                .addLine("[설명] 2. 단순 나열보다 '왜' 이 코드를 작성했는지 의도를 적는 것이 좋은 습관이다."));

        addConcept(new Concept("B18", "부동소수점 오차의 원인", "기초")
                .addLine("[설명] 컴퓨터가 10진수 소수점을 2진수로 바꾸며 발생하는 미세한 오차를 이해한다.")
                .addLine("• [메서드] BigDecimal: 금융권처럼 정밀도가 중요한 계산에서 오차 없이 실수를 다룬다.")
                .addLine("[코드] double d = 0.1 + 0.2; // 0.30000000000000004 출력됨")
                .addLine("[설명] 1. float과 double은 정밀도보다 속도에 최적화된 근사치 저장 방식이다.")
                .addLine("[설명] 2. 정확한 일치 여부를 ==로 비교하면 오차 때문에 false가 나올 수 있어 위험하다."));

        addConcept(new Concept("B19", "형변환(Casting) 원리", "기초")
                .addLine("[설명] 데이터 타입을 다른 타입으로 변환하며, 범위 확정이나 축소 시 발생한다.")
                .addLine("• [메서드] (타입): 강제 형변환 시 명시적으로 타입을 적어 데이터 유실을 승인한다.")
                .addLine("[코드] int i = (int)3.14; // i에는 소수점이 버려진 3만 저장됨")
                .addLine("[설명] 1. 자동 형변환은 작은 타입에서 큰 타입으로 이동할 때 손실 없이 일어난다.")
                .addLine("[설명] 2. 큰 타입에서 작은 타입으로 갈 때는 명시적 캐스팅을 하지 않으면 컴파일 에러가 난다."));

        addConcept(new Concept("B20", "Scanner 클래스 활용", "기초")
                .addLine("[설명] 키보드 입력을 편리하게 가공하여 읽어오는 표준 입력 유틸리티이다.")
                .addLine("• [메서드] nextInt(): 정수 읽기 / nextLine(): 한 줄 전체 읽기")
                .addLine("[코드] Scanner sc = new Scanner(System.in); int num = sc.nextInt();")
                .addLine("[설명] 1. 사용 후에는 System.in 자원을 위해 sc.close()를 호출하는 것이 원칙이다.")
                .addLine("[설명] 2. nextInt() 후 nextLine() 사용 시 버퍼에 남은 엔터를 먼저 비워주는 처리가 필요하다."));

        addConcept(new Concept("B21", "System.out.println() 분석", "기초")
                .addLine("[설명] 표준 출력 장치(콘솔)에 데이터를 출력하는 가장 기본적인 방법이다.")
                .addLine("• [메서드] println(): 출력 후 줄바꿈 / print(): 출력 후 대기")
                .addLine("[코드] System.out.printf(\"이름: %s, 나이: %d\", name, age);")
                .addLine("[설명] 1. printf는 서식 지정자(%d, %s 등)를 사용하여 가독성 있게 값을 출력한다.")
                .addLine("[설명] 2. 내부적으로 객체의 toString() 메서드를 호출하여 문자열로 변환해 출력한다."));

        addConcept(new Concept("B22", "이스케이프 문자 활용", "기초")
                .addLine("[설명] 특수 기호를 출력하거나 제어 기능을 수행하는 역슬래시(\\) 조합 문자이다.")
                .addLine("• [메서드] \\n: 개행 / \\t: 탭 / \\\": 큰따옴표 표현")
                .addLine("[코드] System.out.println(\"Hello\\nWorld\"); // 두 줄로 출력됨")
                .addLine("[설명] 1. 윈도우 경로를 적을 때는 역슬래시를 두 번(\\\\) 적어야 문자로 인식된다.")
                .addLine("[설명] 2. 문자열 내부에서 특정 제어가 필요할 때 프로그래밍 전반에서 쓰이는 규칙이다."));

        addConcept(new Concept("B23", "대입 연산자와 복합 대입", "기초")
                .addLine("[설명] 변수에 값을 저장하거나, 연산과 동시에 대입을 수행하여 코드를 줄인다.")
                .addLine("• [메서드] =, +=, -=, *=, /=: 할당과 연산의 결합이다.")
                .addLine("[코드] count += 10; // count = count + 10과 동일함")
                .addLine("[설명] 1. 코드가 간결해지고 연산 속도 면에서도 약간의 이점이 있을 수 있다.")
                .addLine("[설명] 2. 가독성을 위해 너무 복잡한 결합 연산은 피하는 것이 좋다."));

        addConcept(new Concept("B24", "삼항 연산자의 가독성", "기초")
                .addLine("[설명] (조건) ? 참일때 : 거짓일때 구조로 if-else를 한 줄로 대체한다.")
                .addLine("• [메서드] ? : 연산자는 항이 3개인 유일한 조건 연산자이다.")
                .addLine("[코드] String res = (score >= 60) ? \"Pass\" : \"Fail\";")
                .addLine("[설명] 1. 간단한 값 대입 조건문에 쓰면 코드 줄 수를 획기적으로 줄여준다.")
                .addLine("[설명] 2. 중첩해서 사용하면 가독성이 최악이 되므로 한 번만 쓰는 것을 권장한다."));

        addConcept(new Concept("B25", "Math.random()과 난수", "기초")
                .addLine("[설명] 0.0 이상 1.0 미만의 실수를 무작위로 생성하여 다양한 값으로 가공한다.")
                .addLine("• [메서드] (int)(Math.random() * 범위) + 시작값: 정수 난수 생성 공식이다.")
                .addLine("[코드] int dice = (int)(Math.random() * 6) + 1; // 1~6 난수")
                .addLine("[설명] 1. 보안이 중요한 난수는 SecureRandom 클래스를 쓰는 것이 더 안전하다.")
                .addLine("[설명] 2. 반환 타입이 double이므로 정수가 필요하면 반드시 강제 형변환을 해야 한다."));

        addConcept(new Concept("B26", "스택과 힙 메모리 구조", "기초")
                .addLine("[설명] 자바가 실행 중에 메모리를 효율적으로 쓰기 위해 나누는 두 핵심 영역이다.")
                .addLine("• [메서드] Stack: 지역 변수와 메서드 호출 정보 / Heap: 객체와 배열 저장")
                .addLine("[코드] int a = 10; // Stack에 저장 / String s = new String(); // Heap에 저장")
                .addLine("[설명] 1. 스택은 매우 빠르지만 정해진 순서로 관리되고, 힙은 크지만 GC가 관리한다.")
                .addLine("[설명] 2. 참조 변수는 스택에 '주소'를 갖고, 실제 알맹이는 힙에 거주하는 구조이다."));

        addConcept(new Concept("B27", "Null의 실체와 예방", "기초")
                .addLine("[설명] 참조 변수가 어떤 객체도 가리키지 않고 비어있는 상태를 뜻한다.")
                .addLine("• [메서드] Objects.requireNonNull(): null이면 예외를 던져 즉시 발견하게 한다.")
                .addLine("[코드] String str = null; if(str == null) { ... }")
                .addLine("[설명] 1. null인 객체의 메서드를 호출하면 악명 높은 NullPointerException이 터진다.")
                .addLine("[설명] 2. 객체 생성 전이나 찾기 실패 시의 상태를 표현하기 위해 실무에서 많이 쓰인다."));

        addConcept(new Concept("B28", "Scanner 버퍼 비우기", "기초")
                .addLine("[설명] 숫자 입력 후 문자열을 받을 때 발생하는 버퍼 찌꺼기 문제를 해결한다.")
                .addLine("• [메서드] sc.nextLine(): 버퍼에 남은 개행문자(\\n)를 강제로 소모한다.")
                .addLine("[코드] sc.nextInt(); sc.nextLine(); String s = sc.nextLine();")
                .addLine("[설명] 1. nextInt()는 숫자만 읽고 엔터키 값은 버퍼에 그대로 둔다.")
                .addLine("[설명] 2. 버퍼를 비우지 않으면 다음 문자열 입력이 바로 종료되는 버그가 생긴다."));

        addConcept(new Concept("B29", "무한 루프의 목적과 탈출", "기초")
                .addLine("[설명] 조건이 항상 참이 되도록 설정하여 멈추지 않고 계속 실행되는 구조이다.")
                .addLine("• [메서드] while(true) { if(...) break; }: 무한히 돌되 특정 조건에서 탈출한다.")
                .addLine("[코드] for(;;) { ... } // 이것도 무한 루프의 표현이다.")
                .addLine("[설명] 1. 서버 대기 로직이나 사용자 종료 명령 전까지 돌아가는 프로그램에 쓴다.")
                .addLine("[설명] 2. 탈출 조건이 없으면 시스템 자원을 과하게 소모하며 프로그램이 멈추지 않는다."));

        addConcept(new Concept("B30", "중첩 루프와 구구단", "기초")
                .addLine("[설명] 반복문 안에 또 다른 반복문이 들어있어 행과 열을 탐색하는 구조이다.")
                .addLine("• [메서드] 바깥 루프가 1번 돌 때 안쪽 루프는 전체 회차를 완료한다.")
                .addLine("[코드] for(i=2; i<10; i++) { for(j=1; j<10; j++) { ... } }")
                .addLine("[설명] 1. 중첩이 깊어질수록 실행 횟수가 기하급수적으로 늘어나 성능에 주의해야 한다.")
                .addLine("[설명] 2. 다차원 배열을 탐색하거나 격자 형태의 데이터를 처리할 때 핵심적이다."));

        addConcept(new Concept("B31", "유니코드와 char 타입", "기초")
                .addLine("[설명] 문자를 내부적으로 숫자로 관리하는 유니코드(UTF-16) 체계를 이해한다.")
                .addLine("• [메서드] 'A': 65 / 'a': 97 / '가': 44032 로 대응된다.")
                .addLine("[코드] char c = 'A'; int code = (int)c; // 65 저장")
                .addLine("[설명] 1. char는 2바이트 공간을 가지며 전 세계 문자를 숫자로 표현할 수 있다.")
                .addLine("[설명] 2. 숫자를 char로 형변환하면 해당 숫자에 매핑된 문자가 출력된다."));

        addConcept(new Concept("B32", "문자열 결합의 내부 동작", "기초")
                .addLine("[설명] + 연산자로 문자열을 합칠 때 자바가 처리하는 방식과 효율성을 다룬다.")
                .addLine("• [메서드] String + int = String: 다른 타입을 만나면 자동으로 문자열화한다.")
                .addLine("[코드] String res = \"Grade: \" + 10; // \"Grade: 10\"")
                .addLine("[설명] 1. 빈번한 + 결합은 내부적으로 StringBuilder 객체를 계속 만들어 느려질 수 있다.")
                .addLine("[설명] 2. 대량의 문자열을 합칠 때는 처음부터 StringBuilder를 쓰는 것이 정석이다."));

        addConcept(new Concept("B33", "자바 네이밍 컨벤션", "기초")
                .addLine("[설명] 개발자들 사이의 약속으로, 코드의 가독성과 유지보수성을 높이는 명명법이다.")
                .addLine("• [메서드] PascalCase: 클래스 / camelCase: 변수, 메서드 / UPPER_CASE: 상수")
                .addLine("[코드] class MyManager { int userCount; final int MAX_LIMIT = 100; }")
                .addLine("[설명] 1. 약속을 지키면 다른 사람이 짠 코드를 분석하는 속도가 비약적으로 빨라진다.")
                .addLine("[설명] 2. 숫자로 시작하거나 특수문자($, _ 제외)를 쓰는 것은 허용되지 않는다."));

        addConcept(new Concept("B34", "리터럴(Literal)의 개념", "기초")
                .addLine("[설명] 소스코드에 직접 작성되어 변하지 않는 고정된 값 그 자체를 의미한다.")
                .addLine("• [메서드] 10L: long형 / 3.14f: float형 / 'A': char형 리터럴이다.")
                .addLine("[코드] double d = 3.14; // 3.14 자체가 실수 리터럴이다.")
                .addLine("[설명] 1. 정수 리터럴은 기본적으로 int형, 실수는 double형으로 간주된다.")
                .addLine("[설명] 2. 접미사(L, f 등)를 붙여 리터럴의 정확한 데이터 타입을 지정할 수 있다."));

        addConcept(new Concept("B35", "자동 형변환(Promotion)", "기초")
                .addLine("[설명] 작은 타입이 큰 타입으로 저장될 때 데이터 유실이 없어 자바가 자동으로 해주는 변환이다.")
                .addLine("• [메서드] byte -> int -> long -> float -> double 순으로 흐른다.")
                .addLine("[코드] double d = 10; // int 10이 double 10.0으로 자동 변환")
                .addLine("[설명] 1. 표현 범위가 더 넓은 쪽으로 이동하는 것은 안전한 연산으로 간주된다.")
                .addLine("[설명] 2. 정수와 실수를 연산하면 정수가 자동으로 실수로 변환되어 계산된다."));

        addConcept(new Concept("B36", "명시적 형변환(Casting)", "기초")
                .addLine("[설명] 큰 타입을 작은 타입에 억지로 집어넣을 때 발생하며 데이터 손실 위험을 감수한다.")
                .addLine("• [메서드] (타입): 소괄호를 사용하여 변환하고자 하는 타입을 명시한다.")
                .addLine("[코드] int i = (int)10.7; // i에는 소수점이 잘린 10만 남음")
                .addLine("[설명] 1. 개발자가 '데이터 유실을 인지하고 책임지겠다'는 의미의 선언이다.")
                .addLine("[설명] 2. 객체 상속 관계에서 자식 타입으로 내릴 때도 사용되며 이때는 에러 위험이 크다."));

        addConcept(new Concept("B37", "오버플로우와 언더플로우", "기초")
                .addLine("[설명] 타입이 가진 표현 범위를 벗어날 때 값이 반대편 끝으로 돌아가는 현상이다.")
                .addLine("• [메서드] 최대값 + 1 = 최소값 / 최소값 - 1 = 최대값")
                .addLine("[코드] byte b = (byte)128; // b는 -128이 됨")
                .addLine("[설명] 1. 계산 결과가 예상과 전혀 다른 음수가 나온다면 오버플로우를 의심해야 한다.")
                .addLine("[설명] 2. 안전한 계산을 위해 더 큰 타입(long)을 쓰거나 연산 전 범위를 검사해야 한다."));

        addConcept(new Concept("B38", "실수 타입의 정밀도 차이", "기초")
                .addLine("[설명] float과 double이 소수점 이하의 값을 얼마나 정확하게 보존하는지 비교한다.")
                .addLine("• [메서드] float: 4바이트(7자리 정밀도) / double: 8바이트(15자리 정밀도)")
                .addLine("[코드] double d = 3.141592653589793; // 대부분 보존됨")
                .addLine("[설명] 1. 실수의 기본값은 double이므로 float은 숫자 뒤에 반드시 f를 붙여야 한다.")
                .addLine("[설명] 2. 정밀도가 중요한 과학 계산이나 금융은 반드시 double 이상을 써야 한다."));

        addConcept(new Concept("B39", "String.equals()의 원리", "기초")
                .addLine("[설명] 문자열이 메모리 주소가 달라도 담긴 글자 자체가 같은지를 대조하는 기술이다.")
                .addLine("• [메서드] equals(): Object의 메서드를 오버라이딩하여 글자 하나하나를 비교한다.")
                .addLine("[코드] if(str1.equals(str2)) { ... } // 문자열 비교의 정석")
                .addLine("[설명] 1. == 연산자는 주소를 비교하므로 new로 만든 문자열 비교 시 false가 날 수 있다.")
                .addLine("[설명] 2. 문자열 리터럴 방식과 new 방식의 주소 차이 때문에 반드시 equals()를 써야 한다."));

        addConcept(new Concept("B40", "배열의 length 필드 활용", "기초")
                .addLine("[설명] 배열이 생성될 때 정해진 크기 정보를 담고 있는 읽기 전용 상수이다.")
                .addLine("• [메서드] arr.length: 배열 요소의 전체 개수를 반환한다.")
                .addLine("[코드] for(int i=0; i<arr.length; i++) { ... }")
                .addLine("[설명] 1. 인덱스는 0부터 (length - 1)까지이므로 범위를 넘지 않게 주의해야 한다.")
                .addLine("[설명] 2. 컬렉션의 size() 메서드와 헷갈리기 쉬우니 주의 깊게 구별해야 한다."));

        addConcept(new Concept("B41", "향상된 for문의 편리함", "기초")
                .addLine("[설명] 인덱스 번호를 쓰지 않고 배열이나 컬렉션의 요소를 순차적으로 자동 추출한다.")
                .addLine("• [메서드] for(타입 변수 : 배열): 가독성이 높고 범위를 벗어날 위험이 없다.")
                .addLine("[코드] for(String s : fruits) { System.out.println(s); }")
                .addLine("[설명] 1. 배열의 값을 수정하기보다 전체를 훑으며 읽기만 할 때 최적이다.")
                .addLine("[설명] 2. 내부적으로는 반복자(Iterator)를 사용하여 매우 효율적으로 동작한다."));

        addConcept(new Concept("B42", "상수와 final 키워드", "기초")
                .addLine("[설명] 변수의 값을 딱 한 번만 정하고 이후 수정을 원천 봉쇄하여 안전을 도모한다.")
                .addLine("• [메서드] final: '최종적인'이라는 뜻으로 클래스, 메서드, 변수에 다 붙을 수 있다.")
                .addLine("[코드] final int STUDENT_MAX = 30;")
                .addLine("[설명] 1. 의도치 않게 설정값이 바뀌어 발생하는 버그를 미리 차단한다.")
                .addLine("[설명] 2. 상수는 관례적으로 이름을 모두 대문자로 적어 다른 변수와 확실히 구별한다."));

        addConcept(new Concept("B43", "메서드 정의와 재사용", "기초")
                .addLine("[설명] 반복되는 기능을 하나로 묶어 필요할 때마다 호출하여 코드 중복을 제거한다.")
                .addLine("• [메서드] 반환타입 이름(매개변수) { 실행문 }: 메서드의 기본 구조이다.")
                .addLine("[코드] public int add(int a, int b) { return a + b; }")
                .addLine("[설명] 1. 메서드는 하나의 기능만 수행하도록 작게 나누는 것이 좋은 설계이다.")
                .addLine("[설명] 2. 입력(파라미터)과 출력(리턴)의 타입을 명확히 정의해야 한다."));

        addConcept(new Concept("B44", "매개변수(Parameter) 전달", "기초")
                .addLine("[설명] 메서드 호출 시 외부에서 내부로 전달되는 데이터의 통로와 변수이다.")
                .addLine("• [메서드] Argument: 호출 시 넘기는 실제 값 / Parameter: 메서드 내의 변수")
                .addLine("[코드] void printMsg(String msg) { ... } // msg가 매개변수")
                .addLine("[설명] 1. 자바는 항상 '값에 의한 호출(Call by Value)' 방식을 취한다.")
                .addLine("[설명] 2. 기본형은 복사본을, 참조형은 '주소 복사본'을 전달하여 원본에 영향을 주기도 한다."));

        addConcept(new Concept("B45", "return문과 결과 반환", "기초")
                .addLine("[설명] 메서드의 실행을 마치고 호출한 곳으로 결과 데이터를 돌려주는 키워드이다.")
                .addLine("• [메서드] return 값: 결과 반환 / return;: void 메서드에서 즉시 종료")
                .addLine("[코드] int getAge() { return 25; }")
                .addLine("[설명] 1. return이 실행되면 아래쪽 코드가 남아있어도 메서드는 즉시 종료된다.")
                .addLine("[설명] 2. 반환 타입이 정해져 있다면 반드시 해당 타입의 데이터를 return해야 한다."));

        addConcept(new Concept("B46", "자바 주석의 실무 활용", "기초")
                .addLine("[설명] 컴퓨터는 읽지 않고 사람만 읽는 설명문으로, 협업과 문서화에 필수이다.")
                .addLine("• [메서드] //: 한 줄 / /* ... */: 여러 줄 / /** ... */: 문서화 주석")
                .addLine("[코드] // TODO: 나중에 구현할 부분 / /** API 설명 주석 */")
                .addLine("[설명] 1. 주석은 컴파일 과정에서 완전히 삭제되어 성능에는 영향이 없다.")
                .addLine("[설명] 2. '어떻게' 보다는 '왜' 이렇게 작성했는지 의도를 적는 주석이 훌륭하다."));

        addConcept(new Concept("B47", "JVM의 바이트코드 실행", "기초")
                .addLine("[설명] 자바 소스가 기계와 소통하기 위해 거치는 중간 번역 과정을 이해한다.")
                .addLine("• [메서드] .class: JVM만 이해하는 바이트코드 파일 / javac: 컴파일러")
                .addLine("[코드] java MyApp // JVM이 .class 파일을 실행함")
                .addLine("[설명] 1. 소스코드는 OS 독립적이지만, JVM은 각 OS에 맞게 설치되어야 한다.")
                .addLine("[설명] 2. 'Write Once, Run Anywhere' 철학의 핵심적인 기반 기술이다."));

        addConcept(new Concept("B48", "패키지와 이름 관리", "기초")
                .addLine("[설명] 관련된 클래스들을 묶어 관리하고 이름 충돌을 방지하는 물리적 폴더 구조이다.")
                .addLine("• [메서드] package com.project.util;: 클래스 최상단에 패키지 위치를 선언한다.")
                .addLine("[코드] package project; // 현재 프로젝트의 패키지명")
                .addLine("[설명] 1. 패키지명은 도메인 주소를 거꾸로 쓰는 것(com.google...)이 관례이다.")
                .addLine("[설명] 2. 다른 패키지의 클래스를 쓰려면 임포트(import) 과정이 필요하다."));

        addConcept(new Concept("B49", "임포트(import)의 작동 방식", "기초")
                .addLine("[설명] 다른 패키지에 있는 클래스를 불러와서 이름만으로 사용할 수 있게 한다.")
                .addLine("• [메서드] import java.util.*: 해당 패키지의 모든 클래스를 사용 가능케 한다.")
                .addLine("[코드] import java.util.Scanner; // Scanner 클래스 안내")
                .addLine("[설명] 1. java.lang 패키지에 있는 클래스들은 너무 기본이라 임포트가 생략된다.")
                .addLine("[설명] 2. 같은 패키지 내 클래스끼리는 임포트 없이 서로 자유롭게 호출한다."));

        addConcept(new Concept("B50", "컴파일러(javac)의 역할", "기초")
                .addLine("[설명] 사람이 쓴 .java 파일을 JVM용 언어인 .class로 번역하며 문법을 검사한다.")
                .addLine("• [메서드] javac Main.java: 터미널에서 명령어로 컴파일을 수행한다.")
                .addLine("[코드] static void main(String[] args) { ... } // 오타 시 컴파일 에러")
                .addLine("[설명] 1. 문법 오류를 실행 전에 잡아주어 프로그램의 신뢰성을 높여준다.")
                .addLine("[설명] 2. 컴파일이 성공했다는 것은 최소한 자바의 문법 규칙은 다 지켰다는 뜻이다."));
    }

    private void initIntermediateConcepts() {
        addConcept(new Concept("I51", "상속(Inheritance)의 메커니즘", "중급")
                .addLine("[설명] 기존 클래스의 필드와 메서드를 자식 클래스가 물려받아 코드 재사용성을 높이고 계층 구조를 형성하는 기술이다.")
                .addLine("• [메서드] extends 키워드를 클래스 선언부에 사용하여 부모 클래스를 지정한다.")
                .addLine("[코드] class Dog extends Animal { void bark() { System.out.println(\"멍멍\"); } }")
                .addLine("[설명] 1. 자바는 다중 상속의 복잡성을 피하기 위해 단일 상속만을 허용하며, Object가 모든 클래스의 최상위 부모이다.")
                .addLine("[설명] 2. 자식 객체 생성 시 부모 생성자가 먼저 호출되어 부모의 멤버들이 먼저 메모리에 올라간다."));

        addConcept(new Concept("I52", "메서드 오버라이딩(Overriding)", "중급")
                .addLine("[설명] 부모로부터 물려받은 메서드의 동작을 자식 클래스에서 목적에 맞게 완전히 새롭게 구현하는 기술이다.")
                .addLine("• [메서드] @Override 어노테이션을 통해 컴파일러에게 재정의 사실을 알려 실수를 방지한다.")
                .addLine("[코드] @Override public void move() { System.out.println(\"네 발로 걷는다\"); }")
                .addLine("[설명] 1. 메서드 이름, 리턴 타입, 매개변수 리스트가 부모의 것과 정확히 일치해야 성립한다.")
                .addLine("[설명] 2. 다형성의 핵심 원리로, 부모 타입 변수로 자식 객체를 참조해도 재정의된 메서드가 우선 호출된다."));

        addConcept(new Concept("I53", "메서드 오버로딩(Overloading)", "중급")
                .addLine("[설명] 같은 이름의 메서드를 매개변수의 타입이나 개수만 다르게 하여 여러 개 정의하는 기술이다.")
                .addLine("• [메서드] 컴파일 시점에 매개변수 정보를 보고 어떤 메서드를 실행할지 결정한다.")
                .addLine("[코드] void print(int n); void print(String s); void print(int n, String s);")
                .addLine("[설명] 1. 메서드 이름 하나로 다양한 형태의 데이터를 처리할 수 있어 가독성과 편의성이 증대된다.")
                .addLine("[설명] 2. 리턴 타입만 다른 경우에는 오버로딩이 성립하지 않으므로 주의해야 한다."));

        addConcept(new Concept("I54", "캡슐화(Encapsulation)", "중급")
                .addLine("[설명] 객체의 데이터를 외부로부터 보호하고 내부 구현을 숨겨 결합도를 낮추는 객체지향의 핵심 원칙이다.")
                .addLine("• [메서드] Getter/Setter 메서드를 통해 private 필드에 안전하게 접근하고 값을 검증한다.")
                .addLine("[코드] private int age; public void setAge(int a) { if(a>0) this.age = a; }")
                .addLine("[설명] 1. 필드를 직접 노출하지 않고 메서드를 거치게 함으로써 데이터 무결성을 유지할 수 있다.")
                .addLine("[설명] 2. 클래스 내부 로직이 변경되어도 외부 코드는 영향을 받지 않아 유지보수가 매우 유리해진다."));

        addConcept(new Concept("I55", "인터페이스(Interface) 규격", "중급")
                .addLine("[설명] 객체가 가져야 할 기능의 목록을 정의한 일종의 설계 규격서로, 클래스 간의 약속을 뜻한다.")
                .addLine("• [메서드] implements 키워드를 사용하여 인터페이스에 정의된 추상 메서드를 강제로 구현한다.")
                .addLine("[코드] interface Drawable { void draw(); } class Circle implements Drawable { ... }")
                .addLine("[설명] 1. 인터페이스는 다중 구현이 가능하여, 한 클래스가 여러 역할을 수행하도록 유연하게 설계할 수 있다.")
                .addLine("[설명] 2. 구현체가 무엇인지 몰라도 규격(인터페이스)만 알면 기능을 쓸 수 있는 '약결합' 상태를 만든다."));

        addConcept(new Concept("I56", "추상 클래스(Abstract Class)", "중급")
                .addLine("[설명] 미완성된 설계도로, 자식 클래스들이 반드시 구현해야 할 공통 규격과 공통 로직을 동시에 제공한다.")
                .addLine("• [메서드] abstract 키워드를 사용하여 추상 메서드를 선언하며, 구현부({ })를 작성하지 않는다.")
                .addLine("[코드] abstract class Player { abstract void play(String file); void stop() { ... } }")
                .addLine("[설명] 1. 단독으로 객체를 생성할 수 없으며, 반드시 자식 클래스가 상속받아 미완성 기능을 채워야 한다.")
                .addLine("[설명] 2. 인터페이스와 상속의 중간 단계로, 공통된 필드와 로직을 상속해주고 싶을 때 최적이다."));

        addConcept(new Concept("I57", "다형성(Polymorphism)의 정수", "중급")
                .addLine("[설명] 하나의 참조 변수로 여러 타입의 객체를 다룰 수 있는 능력으로, 객체지향의 가장 강력한 무기이다.")
                .addLine("• [메서드] instanceof 연산자를 사용하여 참조 변수가 실제 어떤 타입의 객체인지 확인한다.")
                .addLine("[코드] Parent p = new Child(); if(p instanceof Child) { Child c = (Child)p; }")
                .addLine("[설명] 1. 부모 타입 배열에 여러 자식 객체를 담아 루프를 돌리며 동일한 메서드를 호출할 수 있다.")
                .addLine("[설명] 2. 코드는 '부모'를 향하지만 실제 작동은 '자식'의 것을 따라가므로 유연한 확장이 가능해진다."));

        addConcept(new Concept("I58", "생성자(Constructor) 체이닝", "중급")
                .addLine("[설명] 객체 생성 시 초기화 로직을 수행하며, 부모 생성자부터 차례로 호출되는 연쇄 작용이다.")
                .addLine("• [메서드] super(): 부모의 생성자를 명시적으로 호출한다. / this(): 자신의 다른 생성자를 호출한다.")
                .addLine("[코드] public Car() { this(\"기본모델\"); } public Car(String n) { super(); this.name = n; }")
                .addLine("[설명] 1. 생성자 첫 줄에는 반드시 super()나 this()가 와야 하며, 생략 시 자바가 자동으로 super()를 넣는다.")
                .addLine("[설명] 2. 부모 객체의 데이터가 먼저 완벽히 초기화된 후에 자식의 로직이 돌아가는 안전한 구조이다."));

        addConcept(new Concept("I59", "this와 super의 참조 범위", "중급")
                .addLine("[설명] 현재 인스턴스 자신과 부모 인스턴스를 가리키는 특수한 키워드로 멤버 접근 범위를 결정한다.")
                .addLine("• [메서드] this.필드명은 현재 클래스의 멤버를 가리키며, super.필드명은 부모의 멤버를 가리킨다.")
                .addLine("[코드] void set(int x) { this.x = x; } // 매개변수와 필드 이름이 같을 때 구분에 필수이다.")
                .addLine("[설명] 1. 자식 클래스에서 부모와 이름이 같은 필드나 메서드가 있을 때 super를 통해 부모의 것을 강제 호출한다.")
                .addLine("[설명] 2. static 메서드 내부에서는 인스턴스가 존재하지 않으므로 this나 super를 절대 사용할 수 없다."));

        addConcept(new Concept("I60", "가비지 컬렉션(GC) 원리", "중급")
                .addLine("[설명] 힙 영역에서 더 이상 어떤 참조 변수도 가리키지 않는 쓰레기 객체를 자동으로 제거하는 시스템이다.")
                .addLine("• [메서드] System.gc(): 가비지 컬렉션을 요청하지만, 즉시 실행은 보장되지 않으므로 호출을 지양한다.")
                .addLine("[코드] Object obj = new Object(); obj = null; // 이제 이 객체는 GC의 수거 대상이 된다.")
                .addLine("[설명] 1. C++와 달리 개발자가 수동으로 메모리를 해제하지 않아 메모리 누수(Leak) 사고를 획기적으로 방지한다.")
                .addLine("[설명] 2. GC가 작동할 때 앱의 모든 스레드가 멈추는 STW 현상이 발생하므로 효율적인 튜닝이 실무의 관건이다."));

        addConcept(new Concept("I61", "ArrayList의 내부 동작", "중급")
                .addLine("[설명] 배열의 단점인 고정 크기를 극복하고, 크기가 유동적으로 늘어나는 가장 보편적인 리스트이다.")
                .addLine("• [메서드] add(v): 순차 저장 / get(i): 즉시 조회 / remove(i): 특정 위치 삭제")
                .addLine("[코드] List<String> list = new ArrayList<>(); list.add(\"Java\"); String s = list.get(0);")
                .addLine("[설명] 1. 내부적으로는 배열을 사용하며, 공간이 꽉 차면 약 1.5배 큰 새 배열을 만들어 기존 데이터를 복사한다.")
                .addLine("[설명] 2. 인덱스를 통한 데이터 검색은 O(1)로 매우 빠르지만, 중간 삽입/삭제는 데이터 이동 때문에 느리다."));

        addConcept(new Concept("I62", "HashMap과 해시 알고리즘", "중급")
                .addLine("[설명] 키(Key)와 값(Value)을 한 쌍으로 묶어 관리하며, 키를 통해 값을 순식간에 찾아내는 자료구조이다.")
                .addLine("• [메서드] put(k, v): 데이터 매핑 저장 / get(k): 키로 값 조회 / containsKey(k): 키 존재 여부 확인")
                .addLine("[코드] Map<String, Integer> map = new HashMap<>(); map.put(\"A\", 100); int val = map.get(\"A\");")
                .addLine("[설명] 1. 키를 해시 함수에 넣어 고유한 인덱스를 산출하므로, 데이터 양에 관계없이 검색 속도가 O(1)에 가깝다.")
                .addLine("[설명] 2. 키는 중복될 수 없으며, 동일한 키로 데이터를 넣으면 기존 값이 새로운 값으로 덮어써진다."));

        addConcept(new Concept("I63", "try-catch-finally 예외 처리", "중급")
                .addLine("[설명] 프로그램 실행 중 발생할 수 있는 예외 상황에 대비하여 비정상 종료를 막고 안정성을 확보한다.")
                .addLine("• [메서드] e.printStackTrace(): 발생한 예외의 상세 경로를 출력하며, e.getMessage()로 짧은 원인을 본다.")
                .addLine("[코드] try { int n = 10/0; } catch(ArithmeticException e) { ... } finally { log(\"마무리\"); }")
                .addLine("[설명] 1. try 블록에서 사고가 나면 즉시 catch로 점프하며, 이후 코드는 실행되지 않는다.")
                .addLine("[설명] 2. finally는 에러 여부와 상관없이 파일 닫기나 DB 연결 해제 등 마무리 작업을 위해 반드시 거친다."));

        addConcept(new Concept("I64", "NullPointerException(NPE) 방어", "중급")
                .addLine("[설명] 참조 변수가 null인데 인스턴스 멤버에 접근하려 할 때 발생하는 자바 최고의 단골 에러이다.")
                .addLine("• [메서드] Objects.isNull(obj) 또는 obj != null 조건문으로 사전 차단한다.")
                .addLine("[코드] String str = null; if(str != null) { System.out.println(str.length()); }")
                .addLine("[설명] 1. 빈 객체에 점(.)을 찍는 행위는 죽음의 에러를 부르며, 실무에서는 항상 null 가능성을 염두에 둬야 한다.")
                .addLine("[설명] 2. 최근에는 Optional 클래스를 사용하여 명시적으로 null 처리를 유도하는 설계가 권장된다."));

        addConcept(new Concept("I65", "Math 클래스의 정적 도구들", "중급")
                .addLine("[설명] 수학적 계산을 돕는 유틸리티 클래스로, 모든 멤버가 static이어서 객체 생성 없이 즉시 사용한다.")
                .addLine("• [메서드] abs(): 절대값 / round(): 반올림 / max(a, b): 둘 중 큰 값 / sqrt(): 제곱근")
                .addLine("[코드] double rand = Math.random(); long rounded = Math.round(3.5); // 결과: 4")
                .addLine("[설명] 1. Math 클래스는 인스턴스화가 불가능하도록 생성자가 private으로 막혀 있는 대표적인 유틸리티 클래스이다.")
                .addLine("[설명] 2. 난수 생성이나 정밀한 수학 연산의 기초가 되며 가독성을 위해 정적 임포트(static import)를 섞기도 한다."));

        addConcept(new Concept("I66", "Wrapper 클래스의 존재 이유", "중급")
                .addLine("[설명] 기본 자료형(int, double 등)을 객체로 다루어야 할 때 사용하는 포장용 클래스이다.")
                .addLine("• [메서드] Integer.valueOf(): 기본형을 객체로 전환한다. (new 방식보다 메모리 효율적임)")
                .addLine("[코드] List<Integer> list = new ArrayList<>(); // 리스트는 객체만 담으므로 Integer가 필요하다.")
                .addLine("[설명] 1. 제네릭이나 컬렉션 프레임워크처럼 객체 주소만을 저장하는 API에서 기본형을 쓰기 위해 존재한다.")
                .addLine("[설명] 2. 문자열을 숫자로 바꾸는 Integer.parseInt() 같은 정적 유틸리티 메서드들도 풍부하게 제공한다."));

        addConcept(new Concept("I67", "오토박싱(Auto-boxing)의 편의", "중급")
                .addLine("[설명] 자바 컴파일러가 기본형과 Wrapper 객체 사이의 변환을 자동으로 처리해주는 기능이다.")
                .addLine("• [메서드] 박싱: int -> Integer / 언박싱: Integer -> int 로 자동으로 바꾼다.")
                .addLine("[코드] Integer n = 10; // 자동 박싱 / int i = n; // 자동 언박싱")
                .addLine("[설명] 1. 개발자가 일일이 valueOf()나 intValue()를 호출하지 않아도 되어 코드가 매우 깔끔해진다.")
                .addLine("[설명] 2. 단, 반복문 내에서 수백만 번 일어날 경우 성능 저하와 불필요한 객체 생성이 발생하므로 주의해야 한다."));

        addConcept(new Concept("I68", "StringTokenizer와 split", "중급")
                .addLine("[설명] 긴 문자열을 특정 구분자를 기준으로 여러 조각(Token)으로 분리하는 기술이다.")
                .addLine("• [메서드] nextToken(): 다음 조각을 하나 꺼내온다. / hasMoreTokens(): 꺼낼 게 남았는지 확인한다.")
                .addLine("[코드] StringTokenizer st = new StringTokenizer(\"사과,배,감\", \",\"); while(st.hasMoreTokens()) { ... }")
                .addLine("[설명] 1. split()은 정규식을 써서 강력하지만 배열을 통째로 만들어 메모리를 더 쓰고, Tokenizer는 하나씩 꺼내어 가볍다.")
                .addLine("[설명] 2. 단순한 구분자 한 개로 자를 때는 성능과 메모리 면에서 StringTokenizer가 유리한 경우가 많다."));

        addConcept(new Concept("I69", "자바 예외 계층과 Throwable", "중급")
                .addLine("[설명] 모든 오류의 근본인 Throwable 아래에 수습 불가능한 Error와 수습 가능한 Exception이 존재한다.")
                .addLine("• [메서드] throw 키워드를 통해 의도적으로 예외 인스턴스를 발생시킨다.")
                .addLine("[코드] if(age < 0) throw new IllegalArgumentException(\"나이는 음수불가\");")
                .addLine("[설명] 1. Error는 메모리 부족(OOM)처럼 하드웨어적 문제이고, Exception은 코드 로직으로 대응 가능한 문제이다.")
                .addLine("[설명] 2. 계층 구조를 이해해야 catch 블록에서 자식 예외부터 부모 순으로 올바르게 배치할 수 있다."));

        addConcept(new Concept("I70", "RuntimeException(Unchecked)", "중급")
                .addLine("[설명] 실행 중에 발생하는 개발자의 실수에 의한 예외로, 컴파일러가 처리를 강제하지 않는다.")
                .addLine("• [메서드] ArithmeticException: 0 나누기 / ArrayIndexOutOfBounds: 인덱스 초과 에러 등.")
                .addLine("[코드] int[] arr = {1}; int n = arr[5]; // 실행 시점에 예외가 터지며 중단된다.")
                .addLine("[설명] 1. 예외 처리를 강제하지 않으므로 코드가 지저분해지는 것을 막아주지만, 그만큼 개발자의 주의가 필요하다.")
                .addLine("[설명] 2. 로직상의 버그인 경우가 많으므로 try-catch 보다는 코드 수정으로 해결하는 것이 정석이다."));

        addConcept(new Concept("I71", "Checked Exception의 구속", "중급")
                .addLine("[설명] 외부 환경(파일 입출력, 네트워크, DB)과 소통할 때 발생할 수 있어 처리가 강제되는 예외이다.")
                .addLine("• [메서드] IOException, SQLException 등이 대표적이며 컴파일러가 감시한다.")
                .addLine("[코드] try { FileReader fr = new FileReader(\"a.txt\"); } catch(IOException e) { ... }")
                .addLine("[설명] 1. 발생 가능성이 희박하더라도 '반드시' 대비 코드를 짜야만 컴파일에 성공한다.")
                .addLine("[설명] 2. 처리가 번거로울 때는 RuntimeException으로 감싸서(Wrapping) 다시 던지는 기법이 실무에서 흔히 쓰인다."));

        addConcept(new Concept("I72", "throws의 예외 전가", "중급")
                .addLine("[설명] 발생한 예외를 내가 처리하지 않고, 나를 호출한 메서드에게 책임을 떠넘기는 키워드이다.")
                .addLine("• [메서드] 메서드 선언부 끝에 throws Exception 형식을 기재한다.")
                .addLine("[코드] public void readFile() throws IOException { ... } // 나를 부른 쪽에서 try-catch 해라!")
                .addLine("[설명] 1. 예외가 발생한 시점보다 더 적절한 대응이 가능한 상위 레벨로 처리를 모으고 싶을 때 사용한다.")
                .addLine("[설명] 2. main 메서드까지 throws를 하면 결국 JVM이 에러를 처리하며 프로그램을 강제 종료시킨다."));

        addConcept(new Concept("I73", "finally 블록과 자원 반납", "중급")
                .addLine("[설명] 예외 발생 여부와 상관없이 코드가 실행되는 영역으로, 시스템 자원의 안전한 폐기를 보장한다.")
                .addLine("• [메서드] close(): 파일이나 DB 커넥션을 닫아 OS에 자원을 돌려준다.")
                .addLine("[코드] try { ... } finally { if(resource != null) resource.close(); }")
                .addLine("[설명] 1. 중간에 return 문을 만나 메서드가 끝나려 해도 finally 블록은 기어코 실행되고 종료된다.")
                .addLine("[설명] 2. 최근에는 자동 자원 반납(Try-with-resources) 문법으로 대체되는 추세이지만 원리는 동일하다."));

        addConcept(new Concept("I74", "커스텀 예외(Custom Exception)", "중급")
                .addLine("[설명] 표준 예외로 설명이 부족한 비즈니스적 에러 상황을 명확히 표현하기 위해 직접 정의한다.")
                .addLine("• [메서드] Exception 또는 RuntimeException 클래스를 상속받아 생성한다.")
                .addLine("[코드] class NotEnoughBalanceException extends RuntimeException { ... }")
                .addLine("[설명] 1. 에러 이름만 봐도 \"잔액이 부족하구나\"라고 알 수 있게 하여 협업 가독성을 획기적으로 높인다.")
                .addLine("[설명] 2. 예외 발생 당시의 사용자 정보나 상태값을 필드로 추가해 상세한 디버깅을 돕게 할 수 있다."));

        addConcept(new Concept("I75", "Set 인터페이스의 유일성", "중급")
                .addLine("[설명] 수학의 집합과 같아 데이터의 중복 저장을 절대 허용하지 않고 순서도 보장하지 않는다.")
                .addLine("• [메서드] add() 호출 시 이미 같은 값이 있으면 false를 반환하고 추가하지 않는다.")
                .addLine("[코드] Set<String> set = new HashSet<>(); set.add(\"A\"); set.add(\"A\"); // 사이즈는 1이다.")
                .addLine("[설명] 1. 많은 데이터 중 중복을 싹 걸러내고 유일한 값들만 남겨야 할 때 최적의 성능을 낸다.")
                .addLine("[설명] 2. 정렬된 상태를 유지하고 싶다면 HashSet 대신 TreeSet을 사용하여 자동 오름차순 정렬을 수행한다."));

        addConcept(new Concept("I76", "HashSet 내부와 hashCode()", "중급")
                .addLine("[설명] 해시 알고리즘을 사용하여 매우 빠른 속도로 데이터를 분류하고 중복을 걸러내는 클래스이다.")
                .addLine("• [메서드] hashCode(): 객체의 지문을 숫자로 반환 / equals(): 실제 내용이 같은지 확인")
                .addLine("[코드] @Override public int hashCode() { return Objects.hash(id); }")
                .addLine("[설명] 1. HashSet은 먼저 hashCode()를 비교하고, 같으면 equals()를 확인하여 둘 다 통과해야 중복으로 본다.")
                .addLine("[설명] 2. 사용자 정의 객체를 Set에 담으려면 이 두 메서드를 반드시 '동시에' 재정의해야 정상 작동한다."));

        addConcept(new Concept("I77", "TreeSet의 이진 탐색 트리", "중급")
                .addLine("[설명] 데이터를 넣자마자 내부적으로 이진 탐색 트리 구조를 형성하여 자동 정렬을 수행하는 집합이다.")
                .addLine("• [메서드] first(): 최소값 / last(): 최대값 / subSet(): 범위 내 데이터 추출")
                .addLine("[코드] TreeSet<Integer> ts = new TreeSet<>(); ts.add(5); ts.add(1); ts.add(10); // [1, 5, 10]")
                .addLine("[설명] 1. 검색 속도는 HashSet보다 약간 느리지만, 데이터가 정렬된 상태여야 하는 시나리오에 아주 강력하다.")
                .addLine("[설명] 2. 데이터가 추가될 때마다 정렬 위치를 찾아 들어가므로 삽입 성능은 다소 떨어질 수 있다."));

        addConcept(new Concept("I78", "LinkedList의 연결 고리", "중급")
                .addLine("[설명] 각 요소가 앞뒤 데이터의 주소를 잡고 연결된 구조로, 빈번한 삽입과 삭제에 특화되어 있다.")
                .addLine("• [메서드] addFirst(): 맨 앞에 추가 / pollLast(): 맨 뒤의 것을 꺼내며 삭제")
                .addLine("[코드] LinkedList<String> queue = new LinkedList<>(); queue.add(\"Task\");")
                .addLine("[설명] 1. ArrayList와 달리 물리적으로 데이터가 붙어있지 않아 중간에 하나를 쏙 끼워넣는 작업이 매우 빠르다.")
                .addLine("[설명] 2. 반면 특정 인덱스를 찾으려면 처음부터 주소를 타고 가야 하므로 조회가 ArrayList보다 현저히 느리다."));

        addConcept(new Concept("I79", "Stack과 후입선출(LIFO)", "중급")
                .addLine("[설명] 데이터가 쌓이는 형태로, 마지막에 들어온 데이터가 가장 먼저 나가는 전형적인 자료구조이다.")
                .addLine("• [메서드] push(v): 위에 쌓기 / pop(): 제일 위의 것을 꺼내며 제거 / peek(): 확인만 하기")
                .addLine("[코드] Stack<String> s = new Stack<>(); s.push(\"A\"); s.push(\"B\"); s.pop(); // \"B\"가 나옴")
                .addLine("[설명] 1. 메서드 호출 스택, 수식의 괄호 검사, 웹 브라우저의 '뒤로가기' 로직 등에 핵심적으로 쓰인다.")
                .addLine("[설명] 2. 자바에서는 Stack 클래스보다 기능이 확장된 Deque(ArrayDeque) 사용을 더 권장한다."));

        addConcept(new Concept("I80", "Queue와 선입선출(FIFO)", "중급")
                .addLine("[설명] 줄서기와 같아 먼저 들어온 데이터가 가장 먼저 처리되고 나가는 공정한 자료구조이다.")
                .addLine("• [메서드] offer(v): 줄 세우기 / poll(): 맨 앞 요소 꺼내며 삭제 / isEmpty(): 비었는지 확인")
                .addLine("[코드] Queue<String> q = new LinkedList<>(); q.offer(\"P1\"); q.poll(); // \"P1\" 처리완료")
                .addLine("[설명] 1. 은행 번호표 처리, 인쇄 대기열, 동영상 스트리밍의 버퍼링 데이터 관리 등에 필수적이다.")
                .addLine("[설명] 2. 인터페이스 형태이므로 실제 구현체로는 LinkedList나 PriorityQueue(우선순위)를 쓴다."));

        addConcept(new Concept("I81", "Iterator 반복자의 정석", "중급")
                .addLine("[설명] 어떤 컬렉션이든 상관없이 요소를 순차적으로 읽어오는 통일된 인터페이스이다.")
                .addLine("• [메서드] hasNext(): 읽을 다음 요소가 있는지 / next(): 다음 요소 가져오기")
                .addLine("[코드] Iterator<String> it = list.iterator(); while(it.hasNext()) { String s = it.next(); }")
                .addLine("[설명] 1. 컬렉션 내부 구조를 몰라도 안전하게 순회할 수 있게 돕는 디자인 패턴의 산물이다.")
                .addLine("[설명] 2. 순회 중에 요소를 안전하게 제거하려면 list.remove() 대신 it.remove()를 반드시 써야 예외가 없다."));

        addConcept(new Concept("I82", "Comparable의 기본 정렬", "중급")
                .addLine("[설명] 객체 스스로가 자신과 다른 객체를 비교하는 기준을 세워 '기본 정렬'이 가능하게 한다.")
                .addLine("• [메서드] compareTo(T o): 나보다 크면 양수, 작으면 음수, 같으면 0을 반환하도록 재정의한다.")
                .addLine("[코드] class Student implements Comparable<Student> { @Override public int compareTo(Student s) { ... } }")
                .addLine("[설명] 1. 인터페이스를 구현해두면 Arrays.sort()나 Collections.sort() 호출 시 자동으로 이 기준을 따른다.")
                .addLine("[설명] 2. 문자열이나 숫자는 이미 이 인터페이스를 구현하고 있어 오름차순 정렬이 기본으로 된다."));

        addConcept(new Concept("I83", "Comparator와 다각도 정렬", "중급")
                .addLine("[설명] 객체의 기본 기준 외에 사용자가 원하는 다양한 기준으로 정렬하고 싶을 때 쓰는 외부 비교 도구이다.")
                .addLine("• [메서드] compare(T o1, T o2): 두 객체를 비교하여 정렬 순서를 결정한다.")
                .addLine("[코드] list.sort((s1, s2) -> s1.score - s2.score); // 람다를 이용한 점수순 정렬")
                .addLine("[설명] 1. 기본 정렬(나이순)은 Comparable로, 상황별 정렬(이름순, 성적순)은 Comparator로 해결하는 것이 정석이다.")
                .addLine("[설명] 2. 익명 객체나 람다식을 통해 즉석에서 정렬 전략을 갈아 끼울 수 있어 매우 유연하다."));

        addConcept(new Concept("I84", "Collections 유틸리티 활용", "중급")
                .addLine("[설명] 컬렉션을 조작하고 검색, 정렬하는 다양한 정적 메서드를 제공하는 도우미 클래스이다.")
                .addLine("• [메서드] reverse(): 순서 뒤집기 / shuffle(): 무작위 섞기 / min, max(): 극한값 찾기")
                .addLine("[코드] Collections.shuffle(lottoNumbers); Collections.sort(list, Collections.reverseOrder());")
                .addLine("[설명] 1. 직접 알고리즘을 짜지 않아도 이미 검증된 고성능 메서드들을 사용할 수 있어 개발 시간이 단축된다.")
                .addLine("[설명] 2. 동기화되지 않은 컬렉션을 멀티스레드용으로 감싸주는 synchronizedList() 기능도 유용하다."));

        addConcept(new Concept("I85", "Arrays 클래스의 배열 가공", "중급")
                .addLine("[설명] 일반 배열(Array)을 다룰 때 필요한 정렬, 복사, 리스트 변환 등의 도구를 모아놓은 클래스이다.")
                .addLine("• [메서드] asList(): 배열을 고정 크기 리스트로 / copyOfRange(): 특정 범위 복사 / fill(): 전체 채우기")
                .addLine("[코드] int[] arr = {3, 1, 2}; Arrays.sort(arr); List<Integer> l = Arrays.asList(1, 2, 3);")
                .addLine("[설명] 1. 배열은 기본적으로 기능이 빈약하므로, Arrays 클래스의 도움을 받아야 객체지향적인 처리가 가능하다.")
                .addLine("[설명] 2. binarySearch() 메서드를 쓰면 정렬된 배열에서 원하는 값을 로그 시간 만에 매우 빠르게 찾는다."));

        addConcept(new Concept("I86", "File 클래스와 입출력 준비", "중급")
                .addLine("[설명] 하드디스크의 실제 파일이나 디렉토리 정보를 조회하고 제어하는 역할을 한다.")
                .addLine("• [메서드] exists(): 존재 확인 / createNewFile(): 빈 파일 생성 / isDirectory(): 폴더인지 확인")
                .addLine("[코드] File f = new File(\"data.txt\"); if(!f.exists()) f.createNewFile();")
                .addLine("[설명] 1. 파일 내부의 데이터를 읽는 게 아니라 파일의 이름, 크기, 경로 등 '메타 정보'를 다루는 용도이다.")
                .addLine("[설명] 2. 파일 쓰기를 할 때 부모 폴더가 없으면 에러가 나므로 f.getParentFile().mkdirs() 처리가 실무 팁이다."));

        addConcept(new Concept("I87", "InputStream의 8비트 읽기", "중급")
                .addLine("[설명] 모든 자바 입출력의 시초로, 데이터가 흐르는 통로에서 1바이트씩 순차적으로 읽어온다.")
                .addLine("• [메서드] read(): 1바이트를 읽어 0~255 사이 정수로 반환하며, 끝에 도달하면 -1을 돌려준다.")
                .addLine("[코드] InputStream is = new FileInputStream(\"img.jpg\"); int data; while((data=is.read()) != -1) { ... }")
                .addLine("[설명] 1. 이미지, 동영상, 실행 파일 등 모든 형태의 바이너리 데이터를 원본 그대로 주고받을 때 사용한다.")
                .addLine("[설명] 2. 바이트 기반이므로 한글처럼 2바이트 이상인 문자를 처리하면 글자가 깨지는 현상이 발생한다."));

        addConcept(new Concept("I88", "Reader 인터페이스와 문자 처리", "중급")
                .addLine("[설명] 바이트 기반의 한계를 극복하고, 전 세계 모든 문자를 2바이트 단위로 정확히 입출력한다.")
                .addLine("• [메서드] read(char[] cbuf): 한 글자씩이 아닌 배열 단위로 읽어 효율을 높인다.")
                .addLine("[코드] Reader r = new FileReader(\"test.txt\"); char[] buf = new char[1024]; r.read(buf);")
                .addLine("[설명] 1. 자바 내부의 유니코드 체계와 완벽히 호환되어 텍스트 파일을 다루는 데 최적화되어 있다.")
                .addLine("[설명] 2. InputStreamReader를 쓰면 바이트 스트림을 문자 스트림으로 변환해주는 '다리' 역할을 수행한다."));

        addConcept(new Concept("I89", "보조 스트림과 버퍼링", "중급")
                .addLine("[설명] 기본 스트림에 추가 기능을 덧붙여 성능을 높이거나 편의를 더하는 데코레이터 클래스들이다.")
                .addLine("• [메서드] readLine(): 한 줄 전체를 읽어온다. (BufferedReader 전용이며 매우 편리함)")
                .addLine("[코드] BufferedReader br = new BufferedReader(new FileReader(\"data.txt\")); String line = br.readLine();")
                .addLine("[설명] 1. 하드디스크 접근을 줄이고 메모리 버퍼에서 한꺼번에 읽어오므로 입출력 속도가 수십 배 빨라진다.")
                .addLine("[설명] 2. 성능 향상(Buffered), 객체 저장(Object), 데이터 타입 변환 등 용도에 따라 체인처럼 엮어 쓴다."));

        addConcept(new Concept("I90", "Date 클래스의 레거시", "중급")
                .addLine("[설명] 자바 초기부터 있었던 날짜 관리 객체이지만, 설계 결함이 많아 현재는 사용이 지양된다.")
                .addLine("• [메서드] getTime(): 1970년 1월 1일부터 현재까지의 시간을 밀리초(ms) 단위로 반환한다.")
                .addLine("[코드] Date now = new Date(); long ms = now.getTime();")
                .addLine("[설명] 1. 월(Month)이 0부터 시작하거나 가변(Mutable) 객체라 값이 중간에 변할 수 있는 치명적 문제가 있다.")
                .addLine("[설명] 2. 오래된 라이브러리나 과거 시스템(Legacy)과의 연동을 위해서만 사용법을 익혀두는 것이 좋다."));

        addConcept(new Concept("I91", "Calendar와 복잡한 계산", "중급")
                .addLine("[설명] 특정 날짜의 요일을 구하거나 날짜 간의 덧셈, 뺄셈을 정교하게 수행하기 위해 등장했다.")
                .addLine("• [메서드] getInstance(): 시스템 시간대 기반 인스턴스 획득 / add(FIELD, n): 특정 날짜 가감 연산")
                .addLine("[코드] Calendar cal = Calendar.getInstance(); cal.add(Calendar.MONTH, -1); // 한 달 전 날짜")
                .addLine("[설명] 1. 추상 클래스이므로 new를 쓸 수 없고 정적 메서드로 구현체(보통 Gregorian)를 받아야 한다.")
                .addLine("[설명] 2. 날짜 필드를 상수로 지정(YEAR, DATE 등)하여 정밀하게 다룰 수 있지만 문법이 여전히 투박하다."));

        addConcept(new Concept("I92", "LocalDate와 현대적 날짜 API", "중급")
                .addLine("[설명] Java 8에서 도입된 날짜 API로, 불변 객체이며 타임존에 얽매이지 않는 직관적인 설계를 가졌다.")
                .addLine("• [메서드] plusDays(): 일수 더하기 / isAfter(): 선후 관계 비교 / withDayOfMonth(): 특정 날짜로 교체")
                .addLine("[코드] LocalDate today = LocalDate.now(); LocalDate nextWeek = today.plusDays(7);")
                .addLine("[설명] 1. 사칙연산 하듯 날짜를 계산할 수 있으며, 결과를 항상 새 객체로 돌려주어 멀티스레드 환경에 매우 안전하다.")
                .addLine("[설명] 2. 월(Month)이 1부터 시작하여 사람의 직관과 완벽히 일치하므로 신규 프로젝트에 무조건 권장된다."));

        addConcept(new Concept("I93", "SimpleDateFormat의 변환", "중급")
                .addLine("[설명] 날짜 객체를 원하는 서식의 문자열로 바꾸거나, 문자열을 날짜 객체로 해석해주는 포맷터이다.")
                .addLine("• [메서드] format(date): 객체 -> 문자열 / parse(str): 문자열 -> 객체 (예외 처리 필수)")
                .addLine("[코드] SimpleDateFormat sdf = new SimpleDateFormat(\"yyyy-MM-dd HH:mm\"); String s = sdf.format(new Date());")
                .addLine("[설명] 1. 화면에 사용자 친화적으로 날짜를 보여줄 때나 텍스트 데이터베이스에 날짜를 저장할 때 핵심이다.")
                .addLine("[설명] 2. 스레드에 안전하지 않으므로 전역 상수로 두고 여러 명이 동시에 쓰면 데이터가 꼬일 위험이 크다."));

        addConcept(new Concept("I94", "Enum의 valueOf와 유연성", "중급")
                .addLine("[설명] 관련된 상수들을 하나의 타입으로 묶어 관리하며, 외부 문자열 데이터를 상수로 전환할 때 주로 쓴다.")
                .addLine("• [메서드] values(): 정의된 모든 상수를 배열로 반환 / ordinal(): 상수의 순서 번호(0부터) 반환")
                .addLine("[코드] UserRole role = UserRole.valueOf(\"ADMIN\"); if(role == UserRole.ADMIN) { ... }")
                .addLine("[설명] 1. 단순히 숫자로 매핑하는 걸 넘어 내부에 필드와 메서드를 가질 수 있는 '클래스' 성격을 띤다.")
                .addLine("[설명] 2. 허용되지 않은 상수가 들어오는 것을 원천 차단하여 프로그램의 견고함을 극대화한다."));

        addConcept(new Concept("I95", "익명 객체(Anonymous Class)", "중급")
                .addLine("[설명] 클래스 정의와 객체 생성을 동시에 처리하며, 주로 일회성으로 쓰이는 인터페이스를 즉석 구현한다.")
                .addLine("• [메서드] new 인터페이스/클래스명() { ... } 문법으로 바디를 직접 채운다.")
                .addLine("[코드] btn.addActionListener(new ActionListener() { @Override public void action() { ... } });")
                .addLine("[설명] 1. 클래스 파일을 별도로 만들지 않아도 되어 코드가 집중되고 구조가 단순해지는 효과가 있다.")
                .addLine("[설명] 2. 람다식의 근간이 되는 문법이며, GUI 프로그래밍이나 이벤트 처리에서 매우 활발히 쓰인다."));

        addConcept(new Concept("I96", "인터페이스 다중 구현의 원리", "중급")
                .addLine("[설명] 클래스는 부모가 하나여야 하지만, 인터페이스는 여러 개를 동시에 구현하여 다중 정체성을 가질 수 있다.")
                .addLine("• [메서드] implements 뒤에 여러 인터페이스를 쉼표(,)로 나열한다.")
                .addLine("[코드] class Smartphone implements Phone, Camera, GamePlayer { ... }")
                .addLine("[설명] 1. '다이아몬드 문제'가 없는 이유는 인터페이스가 구현부가 없는 추상 메서드만 가졌기 때문(Java 8 전까지)이다.")
                .addLine("[설명] 2. 특정 규격을 만족하는지 여부를 검사하는 용도로 쓰이며 다형성 활용 범위를 극대화한다."));

        addConcept(new Concept("I97", "protected 상속 접근 권한", "중급")
                .addLine("[설명] 같은 패키지에 있거나, 패키지가 다르더라도 상속받은 자식 클래스라면 접근을 허용하는 중간 단계 제어자이다.")
                .addLine("• [메서드] 접근 범위: private < default < protected < public 순서이다.")
                .addLine("[코드] protected String serialNumber; // 자식 클래스에서 super.serialNumber로 접근 가능")
                .addLine("[설명] 1. 무분별한 public 노출은 막으면서 상속 관계의 자식에게만은 유산을 오픈하고 싶을 때 사용한다.")
                .addLine("[설명] 2. 라이브러리 설계 시 확장 포인트(Hook 메서드 등)를 제공할 때 가장 많이 쓰이는 기법이다."));

        addConcept(new Concept("I98", "메서드 시그니처와 JVM", "중급")
                .addLine("[설명] JVM이 메서드를 고유하게 식별할 수 있는 기준인 '이름'과 '매개변수 리스트'의 조합을 뜻한다.")
                .addLine("• [메서드] 메서드 오버로딩 여부는 이 시그니처가 겹치는지에 따라 결정된다.")
                .addLine("[코드] void play(int speed); // 시그니처: play(int)")
                .addLine("[설명] 1. 리턴 타입은 JVM이 메서드를 고르는 기준이 아니므로 리턴 타입만 다르게 하면 컴파일 에러가 난다.")
                .addLine("[설명] 2. 바이트코드 수준에서 메서드 호출(invokevirtual 등)의 대상이 되는 정확한 지표이다."));

        addConcept(new Concept("I99", "정적 초기화 블록(static { })", "중급")
                .addLine("[설명] 클래스가 메모리에 처음 로드될 때 단 한 번만 실행되는 영역으로, 복잡한 static 필드 초기화에 쓴다.")
                .addLine("• [메서드] static { ... } 키워드를 클래스 바디에 직접 선언한다.")
                .addLine("[코드] static { Map<String, String> m = new HashMap<>(); m.put(\"A\", \"1\"); configMap = m; }")
                .addLine("[설명] 1. 생성자보다 먼저 호출되며 인스턴스 생성과 무관하게 실행되는 것이 특징이다.")
                .addLine("[설명] 2. 드라이버 로딩이나 파일 읽기 등 프로그램 시작 전의 필수적인 정적 설정을 위해 사용한다."));

        addConcept(new Concept("I100", "가변 인자(Variable Arguments)", "중급")
                .addLine("[설명] 메서드 파라미터의 개수를 미리 정하지 않고 호출할 때마다 가변적으로 넘길 수 있는 문법이다.")
                .addLine("• [메서드] 타입 뒤에 마침표 세 개(...)를 붙여 선언하며 내부적으로는 배열로 처리된다.")
                .addLine("[코드] public void log(String... msgs) { for(String m : msgs) { ... } }")
                .addLine("[설명] 1. 호출하는 쪽에서 배열을 직접 만들지 않고 쉼표로 값을 나열할 수 있어 매우 편리하다.")
                .addLine("[설명] 2. 반드시 매개변수 리스트의 맨 마지막에 위치해야 하며, 한 메서드에 한 번만 쓸 수 있다."));
    }

    private void initAdvancedConcepts() {
        addConcept(new Concept("A101", "멀티 스레드(Thread) 생성", "고급")
                .addLine("[설명] 하나의 프로세스 안에서 여러 작업 줄기를 동시에 실행하여 CPU 활용도를 극대화하는 기술이다.")
                .addLine("• [메서드] start(): 새로운 스레드를 독립적인 스택 할당과 함께 실행시킨다.")
                .addLine("[코드] Thread t = new Thread(() -> System.out.println(\"Sub Thread Run\")); t.start();")
                .addLine("[설명] 1. run()을 직접 호출하면 단순 메서드 호출일 뿐, 새로운 스레드가 생성되지 않음에 주의해야 한다.")
                .addLine("[설명] 2. Thread 클래스 상속보다 Runnable 인터페이스 구현 방식을 써야 상속 구조에서 자유롭고 유연하다."));

        addConcept(new Concept("A102", "스레드 동기화(Synchronization)", "고급")
                .addLine("[설명] 여러 스레드가 공유 자원에 동시에 접근할 때 데이터 정합성이 깨지는 것을 막는 잠금(Lock) 메커니즘이다.")
                .addLine("• [메서드] synchronized 키워드: 메서드나 블록을 임계 영역으로 지정하여 한 번에 한 스레드만 진입을 허용한다.")
                .addLine("[코드] public synchronized void withdraw(int amt) { if(balance >= amt) balance -= amt; }")
                .addLine("[설명] 1. 데이터의 원자성(Atomicity)을 보장하여 다중 스레드 환경에서도 안전한 입출력을 가능케 한다.")
                .addLine("[설명] 2. 너무 넓은 범위를 잠그면 성능 저하(병목 현상)가 발생하므로 필요한 부분만 블록으로 잠그는 것이 기술이다."));

        addConcept(new Concept("A103", "제네릭(Generic)의 타입 안전성", "고급")
                .addLine("[설명] 클래스 내부에서 사용할 데이터 타입을 미리 정하지 않고 외부에서 지정하게 하여 타입 오류를 컴파일 시점에 잡는 기능이다.")
                .addLine("• [메서드] <T>: 타입을 파라미터화하여 재사용성과 안전성을 동시에 확보한다.")
                .addLine("[코드] class Box<T> { private T item; public void set(T t) { item = t; } }")
                .addLine("[설명] 1. 불필요한 형변환(Casting)을 없애 성능을 향상시키고 런타임에 ClassCastException이 발생하는 것을 막는다.")
                .addLine("[설명] 2. 컴파일 이후에는 타입 정보가 사라지는 '타입 이레이저(Type Erasure)' 방식으로 하위 호환성을 유지한다."));

        addConcept(new Concept("A104", "람다 표현식(Lambda Expression)", "고급")
                .addLine("[설명] 인터페이스의 익명 구현 객체를 함수형 스타일의 짧은 식으로 치환하여 코드 양을 획기적으로 줄이는 문법이다.")
                .addLine("• [메서드] -> 연산자: 매개변수와 실행 바디를 연결하는 화살표 기호이다.")
                .addLine("[코드] list.forEach(item -> System.out.println(\"값: \" + item));")
                .addLine("[설명] 1. 추상 메서드가 단 하나인 '함수형 인터페이스'를 대상으로만 사용할 수 있다.")
                .addLine("[설명] 2. 로직 자체를 매개변수로 전달하거나 결과로 반환할 수 있어 '함수형 프로그래밍'의 토대가 된다."));

        addConcept(new Concept("A105", "스트림(Stream) API의 파이프라인", "고급")
                .addLine("[설명] 배열이나 컬렉션 데이터를 선언적 방식으로 가공 처리하는 연속된 데이터 흐름이다.")
                .addLine("• [메서드] filter(): 조건에 맞는 요소 선별 / map(): 요소 형태 변환 / collect(): 가공 완료 후 수집")
                .addLine("[코드] list.stream().filter(n -> n % 2 == 0).map(n -> n * 10).toList();")
                .addLine("[설명] 1. 원본 데이터를 변경하지 않는 읽기 전용이며, 중간 연산은 최종 연산이 호출될 때까지 실행을 미루는 지연 실행을 한다.")
                .addLine("[설명] 2. 병렬 스트림(parallelStream)을 활용하면 대량의 데이터를 멀티코어 환경에서 초고속으로 처리할 수 있다."));

        addConcept(new Concept("A106", "Optional과 Null 안전 설계", "고급")
                .addLine("[설명] 존재할 수도 있고 아닐 수도 있는 객체를 감싸는 래퍼 클래스로, NPE(NullPointer)를 방지하는 현대적 설계이다.")
                .addLine("• [메서드] ofNullable(): null 가능 객체 생성 / orElseThrow(): 값이 없으면 예외 발생")
                .addLine("[코드] String res = Optional.ofNullable(data).orElse(\"기본값\");")
                .addLine("[설명] 1. 리턴 타입으로 Optional을 쓰면 API 사용자에게 '결과가 비어있을 수 있음'을 명시적으로 알려준다.")
                .addLine("[설명] 2. if(obj != null) 같은 지저분한 코드를 함수형 체이닝으로 깔끔하게 대체할 수 있게 해준다."));

        addConcept(new Concept("A107", "리플렉션(Reflection) API", "고급")
                .addLine("[설명] 실행 중인 자바 프로그램이 자기 자신의 구조(클래스, 필드, 메서드)를 들여다보고 조작하는 고급 기술이다.")
                .addLine("• [메서드] getDeclaredFields(): private 필드를 포함한 클래스의 모든 변수 정보를 가져온다.")
                .addLine("[코드] Class<?> clazz = Class.forName(\"User\"); Method[] methods = clazz.getMethods();")
                .addLine("[설명] 1. 프레임워크(Spring, JUnit)가 어노테이션을 읽어 객체를 자동으로 관리할 때 핵심적으로 사용하는 마법 같은 도구이다.")
                .addLine("[설명] 2. 성능 오버헤드가 발생할 수 있으므로 일반적인 비즈니스 로직보다는 라이브러리 개발 시 주로 활용한다."));

        addConcept(new Concept("A108", "싱글톤(Singleton) 디자인 패턴", "고급")
                .addLine("[설명] 프로그램 전체에서 특정 클래스의 인스턴스를 단 하나만 생성하도록 보장하고 이를 공유하는 패턴이다.")
                .addLine("• [메서드] getInstance(): 외부에서 객체를 얻는 유일한 정적 통로 역할을 한다.")
                .addLine("[코드] private static final Instance; private Singleton() {} public static getInstance() { ... }")
                .addLine("[설명] 1. 생성자를 private으로 막아 외부에서 new를 못 하게 하는 것이 핵심이다.")
                .addLine("[설명] 2. 무분별한 객체 생성을 막아 메모리를 아끼고 전역적인 상태를 관리하기에 최적이다."));

        addConcept(new Concept("A109", "직렬화(Serialization)와 전송", "고급")
                .addLine("[설명] 자바 객체의 상태를 바이트 흐름으로 변환하여 파일에 저장하거나 네트워크로 전송하는 기술이다.")
                .addLine("• [메서드] writeObject(): 객체를 스트림에 쓴다. / readObject(): 스트림에서 객체를 복원한다.")
                .addLine("[코드] class User implements Serializable { private static final long serialVersionUID = 1L; }")
                .addLine("[설명] 1. Serializable 인터페이스를 구현해야 하며, 전송에서 제외할 필드는 transient 키워드를 붙인다.")
                .addLine("[설명] 2. serialVersionUID는 클래스 구조 변경 시 버전 충돌을 막는 주민번호와 같은 역할을 한다."));

        addConcept(new Concept("A110", "JVM 런타임 데이터 영역(Memory)", "고급")
                .addLine("[설명] JVM이 운영체제로부터 할당받아 자바 앱 실행에 사용하는 전체 메모리 구조를 의미한다.")
                .addLine("• [메서드] Method Area: 클래스 원본 저장 / Heap: 객체 상주 / Stack: 메서드 실행 정보 보관")
                .addLine("[코드] // JVM 옵션 설정 예: -Xms512m (초기 힙 크기) -Xmx1024m (최대 힙 크기)")
                .addLine("[설명] 1. 힙은 모든 스레드가 공유하며 GC가 관리하지만, 스택은 각 스레드마다 독립적으로 생성된다.")
                .addLine("[설명] 2. 이 영역의 동작 방식을 이해하는 것이 메모리 릭(Leak)을 잡고 대규모 서비스를 튜닝하는 지름길이다."));

        addConcept(new Concept("A111", "스레드 우선순위와 스케줄링", "고급")
                .addLine("[설명] 스케줄러가 여러 스레드 중 어떤 것을 먼저 실행할지 결정하는 기준값이다.")
                .addLine("• [메서드] setPriority(int): 1(최저) ~ 10(최고) 사이의 값을 부여한다.")
                .addLine("[코드] thread.setPriority(Thread.MAX_PRIORITY); // CPU 점유 확률 상향")
                .addLine("[설명] 1. 절대적인 순서는 아니며 OS 스케줄러에게 주는 일종의 '힌트' 정도로 이해해야 한다.")
                .addLine("[설명] 2. 숫자가 높을수록 더 많은 실행 시간을 할당받을 확률이 높아지지만, 기아 현상(Starvation) 주의가 필요하다."));

        addConcept(new Concept("A112", "데몬 스레드(Daemon Thread)", "고급")
                .addLine("[설명] 주 스레드(Main)의 보조 역할을 수행하며, 주 스레드가 종료되면 자신의 작업 여부와 상관없이 자동 종료된다.")
                .addLine("• [메서드] setDaemon(true): 스레드를 시작(start)하기 전에 데몬 모드로 설정한다.")
                .addLine("[코드] daemonThread.setDaemon(true); daemonThread.start();")
                .addLine("[설명] 1. 가비지 컬렉터나 자동 저장 기능처럼 배경에서 묵묵히 돌아가는 작업에 주로 활용한다.")
                .addLine("[설명] 2. 메인 로직이 끝나면 무의미한 작업을 강제로 정리해주어 시스템 자원을 보호한다."));

        addConcept(new Concept("A113", "synchronized 메서드와 락(Lock)", "고급")
                .addLine("[설명] 메서드 전체를 하나의 모니터 락으로 묶어 다중 스레드의 동시 진입을 물리적으로 차단한다.")
                .addLine("• [메서드] 선언부 뒤에 synchronized를 붙이면 호출한 스레드가 해당 객체의 락을 획득한다.")
                .addLine("[코드] public synchronized void updateData() { ... } // 메서드 단위 잠금")
                .addLine("[설명] 1. 구현이 매우 간편하지만, 메서드 실행이 길어지면 다른 스레드들이 대기하는 병목 현상이 발생한다.")
                .addLine("[설명] 2. 성능 최적화가 필요할 때는 코드의 일부만 묶는 synchronized 블록 방식을 권장한다."));

        addConcept(new Concept("A114", "스레드 풀(Thread Pool) 운용", "고급")
                .addLine("[설명] 스레드를 미리 만들어두고 작업을 할당하는 관리 방식으로, 스레드 폭증으로 인한 서버 다운을 방지한다.")
                .addLine("• [메서드] execute(): 작업을 던지기 / shutdown(): 모든 작업 마친 후 풀 종료")
                .addLine("[코드] ExecutorService pool = Executors.newFixedThreadPool(5); pool.execute(task);")
                .addLine("[설명] 1. 스레드 생성과 소멸에 드는 막대한 오버헤드를 줄여 응답 속도를 비약적으로 높인다.")
                .addLine("[설명] 2. 실무에서는 안정성을 위해 Executors 팩토리보다는 ThreadPoolExecutor를 직접 설정하여 쓴다."));

        addConcept(new Concept("A115", "제네릭 타입 파라미터 관례", "고급")
                .addLine("[설명] 제네릭 코드 작성 시 개발자들끼리 약속한 문자를 사용하여 의미를 명확히 전달한다.")
                .addLine("• [메서드] <T>: Type / <E>: Element / <K, V>: Key, Value")
                .addLine("[코드] public class Result<T, E> { private T value; private E error; }")
                .addLine("[설명] 1. 단순한 알파벳이지만, 이 관례를 따르면 다른 개발자가 코드를 볼 때 의도를 즉시 파악할 수 있다.")
                .addLine("[설명] 2. 다중 제네릭 타입을 정의할 때 유용하며 클래스뿐만 아니라 개별 메서드에도 적용 가능하다."));

        addConcept(new Concept("A116", "와일드카드(?)의 상한/하한 제한", "고급")
                .addLine("[설명] 제네릭의 타입 범위를 유연하게 제한하여 다양한 자식 클래스를 한 번에 처리하는 기술이다.")
                .addLine("• [메서드] <? extends T>: T와 그 자손들만(읽기 전용) / <? super T>: T와 그 조상들만(쓰기 전용)")
                .addLine("[코드] List<? extends Number> list = new ArrayList<Integer>(); // 상한 제한")
                .addLine("[설명] 1. PECS(Producer-Extends, Consumer-Super) 법칙에 따라 설계하면 제네릭의 한계를 극복할 수 있다.")
                .addLine("[설명] 2. API 설계 시 유연함을 극대화하여 코드의 재사용 범위를 대폭 넓혀주는 기술이다."));

        addConcept(new Concept("A117", "함수형 인터페이스(@FunctionalInterface)", "고급")
                .addLine("[설명] 단 하나의 추상 메서드만을 가진 인터페이스로, 람다식의 설계도 역할을 한다.")
                .addLine("• [메서드] @FunctionalInterface: 메서드가 두 개 이상 늘어나지 않도록 컴파일러가 감시하게 한다.")
                .addLine("[코드] @FunctionalInterface interface Calculator { int calc(int a, int b); }")
                .addLine("[설명] 1. 자바 8부터 제공되는 Predicate, Function, Consumer 등이 대표적인 표준 인터페이스이다.")
                .addLine("[설명] 2. 람다식은 결국 이 인터페이스를 구현한 익명 객체로 취급되어 실행된다."));

        addConcept(new Concept("A118", "메서드 참조(Method Reference)", "고급")
                .addLine("[설명] 람다식이 단순히 기존 메서드를 호출하기만 할 경우, 불필요한 구문을 생략하고 메서드를 직접 가리키는 기법이다.")
                .addLine("• [메서드] :: 연산자: 클래스명::메서드명 형식으로 람다를 극한으로 줄인다.")
                .addLine("[코드] list.forEach(System.out::println); // (v -> System.out.println(v)) 의 축약")
                .addLine("[설명] 1. 코드를 더 짧고 읽기 쉽게 만들며, 컴파일러가 타입을 추론하여 안정적으로 연결한다.")
                .addLine("[설명] 2. 정적 메서드, 인스턴스 메서드, 심지어 생성자(new)도 참조 형식으로 쓸 수 있다."));

        addConcept(new Concept("A119", "병렬 스트림(Parallel Stream)의 코어 활용", "고급")
                .addLine("[설명] 데이터 소스를 잘게 쪼개어 여러 CPU 코어에서 병렬로 처리한 뒤 합치는 기술이다.")
                .addLine("• [메서드] parallelStream(): 컬렉션을 병렬 모드로 전환하여 파이프라인에 태운다.")
                .addLine("[코드] long count = bigList.parallelStream().filter(n -> n > 1000).count();")
                .addLine("[설명] 1. 내부적으로 ForkJoinPool을 사용하여 작업을 분할 정복 방식으로 처리한다.")
                .addLine("[설명] 2. 데이터 양이 적거나 연산이 단순하면 분할 오버헤드 때문에 일반 스트림보다 느릴 수 있으니 벤치마킹이 필수다."));

        addConcept(new Concept("A120", "Stream.collect() 결과 수집", "고급")
                .addLine("[설명] 스트림 연산을 마친 데이터를 다시 리스트, 셋, 맵 등의 컬렉션 형태로 묶어내는 최종 연산이다.")
                .addLine("• [메서드] Collectors.groupingBy(): 특정 필드 기준으로 데이터를 그룹화하여 Map으로 반환한다.")
                .addLine("[코드] Map<Category, List<Item>> map = items.stream().collect(groupingBy(Item::getCategory));")
                .addLine("[설명] 1. 단순 변환을 넘어 합계(summingInt), 평균(averaging), 요약(summarizing) 등 강력한 통계 기능을 제공한다.")
                .addLine("[설명] 2. 커스텀 컬렉터(Collector)를 만들어 복잡한 비즈니스 로직을 수집 단계에서 처리할 수 있다."));

        addConcept(new Concept("A121", "커스텀 어노테이션 정의", "고급")
                .addLine("[설명] 코드에 메타데이터를 심어 런타임에 리플렉션이나 프록시가 특정 동작을 수행하게 하는 꼬리표이다.")
                .addLine("• [메서드] @Retention: 어느 시점까지 유지할지 / @Target: 어디에 붙일지 설정한다.")
                .addLine("[코드] @Retention(RetentionPolicy.RUNTIME) @interface MyAnnotation { String value(); }")
                .addLine("[설명] 1. 자바 코드 자체에는 로직이 없지만, 외부 도구나 프레임워크가 이를 읽어 강력한 마법(의존성 주입 등)을 부린다.")
                .addLine("[설명] 2. 무분별한 어노테이션은 코드 추적을 어렵게 하므로 명확한 용도(설정 정보 등)로만 쓰는 것이 좋다."));

        addConcept(new Concept("A122", "Class 객체와 동적 로딩", "고급")
                .addLine("[설명] 현재 실행 중인 모든 클래스의 정보를 담고 있는 거울 객체로, 클래스 로딩의 핵심이다.")
                .addLine("• [메서드] getClass(): 인스턴스로부터 획득 / Class.forName(): 문자열 이름으로 획득")
                .addLine("[코드] Class<?> c = obj.getClass(); System.out.println(\"클래스명: \" + c.getName());")
                .addLine("[설명] 1. 클래스의 필드, 메서드, 부모 클래스 정보를 실시간으로 파악하여 동적인 프로그래밍을 가능케 한다.")
                .addLine("[설명] 2. JDBC 드라이버 로딩처럼 실행 시점에 클래스를 결정해야 하는 시나리오에서 필수적이다."));

        addConcept(new Concept("A123", "다이나믹 프록시(Dynamic Proxy)", "고급")
                .addLine("[설명] 실제 객체(Target)를 감싸서 메서드 호출 전후에 가로채기(Intercept) 로직을 추가하는 가짜 객체 기술이다.")
                .addLine("• [메서드] Proxy.newProxyInstance(): 인터페이스 기반의 프록시 객체를 동적으로 생성한다.")
                .addLine("[코드] Proxy.newProxyInstance(loader, interfaces, invocationHandler);")
                .addLine("[설명] 1. 트랜잭션 처리, 로깅, 권한 체크처럼 비즈니스 로직 외의 부가 기능을 분리(AOP)할 때 핵심이다.")
                .addLine("[설명] 2. 인터페이스가 없는 경우 CGLIB 라이브러리를 통해 클래스 기반 프록시를 생성하기도 한다."));

        addConcept(new Concept("A124", "팩토리(Factory) 패턴의 유연함", "고급")
                .addLine("[설명] 객체 생성 로직을 별도의 클래스(Factory)로 분리하여 객체 간의 결합도를 낮추는 디자인 패턴이다.")
                .addLine("• [메서드] create(): 입력 조건에 따라 적절한 구현체 객체를 생성하여 반환한다.")
                .addLine("[코드] Shape s = ShapeFactory.create(\"Circle\"); s.draw();")
                .addLine("[설명] 1. 클라이언트 코드는 실제 어떤 클래스가 생성되는지 몰라도 인터페이스만 알면 기능을 쓸 수 있다.")
                .addLine("[설명] 2. 새로운 기능 클래스가 추가되어도 메인 로직을 수정할 필요가 없어 확장성이 매우 뛰어나다."));

        addConcept(new Concept("A125", "빌더(Builder) 패턴과 메서드 체이닝", "고급")
                .addLine("[설명] 매개변수가 많은 복잡한 객체를 단계별로 안전하게 생성하며, 가독성을 극대화하는 패턴이다.")
                .addLine("• [메서드] .build(): 세팅을 마친 빌더 객체로부터 최종 완성본 객체를 받아온다.")
                .addLine("[코드] User u = User.builder().name(\"A\").age(20).email(\"a@b.com\").build();")
                .addLine("[설명] 1. 생성자 인자의 순서를 헷갈려 발생하는 치명적 버그를 원천 차단한다.")
                .addLine("[설명] 2. 각 데이터 입력 메서드가 이름을 갖고 있어 코드가 마치 영어 문장처럼 읽히는 효과가 있다."));

        addConcept(new Concept("A126", "Stop-The-World와 GC 튜닝", "고급")
                .addLine("[설명] 가비지 컬렉터가 메모리를 청소하기 위해 애플리케이션의 모든 스레드를 잠시 멈추는 현상이다.")
                .addLine("• [메서드] -XX:+UseG1GC: 최신 하이엔드 시스템에 적합한 G1 GC 알고리즘을 활성화한다.")
                .addLine("[코드] java -XX:+PrintGCDetails MyApp // GC 상세 로그 모니터링")
                .addLine("[설명] 1. 이 중단 시간을 최소화하는 것이 대규모 서버 개발의 핵심이며, 힙 크기와 GC 방식을 조율해야 한다.")
                .addLine("[설명] 2. 효율적인 코딩(객체 재사용 등)으로 쓰레기 발생량 자체를 줄이는 것이 최선의 튜닝이다."));

        addConcept(new Concept("A127", "Try-With-Resources 자동 반납", "고급")
                .addLine("[설명] close() 처리가 필요한 입출력 자원을 try 문 안에서 선언하여 코드 종료 시 자동 반납하는 문법이다.")
                .addLine("• [메서드] AutoCloseable: 이 인터페이스를 구현한 객체만이 이 문법의 대상이 된다.")
                .addLine("[코드] try (BufferedReader br = new BufferedReader(new FileReader(file))) { ... }")
                .addLine("[설명] 1. 개발자가 일일이 finally에서 close를 호출하다가 누락하여 메모리 릭이 발생하는 사고를 예방한다.")
                .addLine("[설명] 2. 예외가 중첩되어도 원본 예외와 자원 해제 예외를 모두 추적할 수 있어 디버깅에 유리하다."));

        addConcept(new Concept("A128", "네트워크 소켓(Socket) 통신", "고급")
                .addLine("[설명] IP와 Port 번호를 결합하여 네트워크상의 다른 컴퓨터와 데이터를 주고받는 종착점(Endpoint)이다.")
                .addLine("• [메서드] accept(): 서버 소켓에서 클라이언트의 접속 요청을 대기하며 연결 시 소켓을 반환한다.")
                .addLine("[코드] ServerSocket ss = new ServerSocket(9999); Socket s = ss.accept();")
                .addLine("[설명] 1. TCP/IP 프로토콜 기반의 안정적인 바이트 스트림 통신을 지원하는 자바 네트워크의 기초이다.")
                .addLine("[설명] 2. 채팅, 웹서버, 파일 전송 프로그램 등 모든 온라인 통신의 뿌리가 되는 기술이다."));

        addConcept(new Concept("A129", "JVM 옵션과 성능 최적화", "고급")
                .addLine("[설명] 자바 앱 실행 시 JVM에 전달하는 설정값으로, 메모리 크기나 가비지 컬렉션 방식을 결정한다.")
                .addLine("• [메서드] -Xmx: 힙 최대 크기 / -Xms: 힙 초기 크기 / -XX:PermSize: 영구 영역 설정")
                .addLine("[코드] java -Xmx2g -Xms512m -jar myApp.jar // 2GB 최대 메모리 설정")
                .addLine("[설명] 1. 서버 사양에 맞춰 이 옵션들을 정밀하게 조절해야 OutOfMemory 에러를 막고 안정성을 확보한다.")
                .addLine("[설명] 2. 성능 부하 테스트(JMeter 등)를 통해 최적의 값을 찾아내는 것이 아키텍트의 역할이다."));

        addConcept(new Concept("A130", "Record 클래스(Java 16+)", "고급")
                .addLine("[설명] 데이터를 전달하는 순수 가방(DTO) 역할을 수행하기 위해 도입된 불변 데이터 전용 클래스이다.")
                .addLine("• [메서드] 필드명을 이름으로 하는 접근자 메서드를 자바가 자동으로 생성해준다.")
                .addLine("[코드] public record Point(int x, int y) { }")
                .addLine("[설명] 1. 생성자, Getter, equals, hashCode, toString을 컴파일러가 자동으로 생성한다.")
                .addLine("[설명] 2. 불변 객체이므로 안전하며, 데이터 전달 목적이 명확해져 가독성이 상승한다."));
    }

    private void addConcept(Concept c) {
        //낱개 지식들을 Map으로관리하기 편하게 구성
        // database에 접근 후 검색과도 접근할 수 있도록 구성
        database.put(c.getId(), c);
    }

    public Concept findById(String id) {
        return database.get(id);
    }

    public List<Concept> findAll() {
        // 프로그램 시작 시 초기 전체 목록을 제공하기 위함!
        // Main클래스에서 화면 나올 시 MainWikiFrame으로 전달
        return new ArrayList<>(database.values());
    }
}