--------------------
# Spring 입문 노트
--------------------

# 목치

- [1. 실행 구조 파악](#실행-구조-파악)
    - [1-1. Log 표시 설정](#Log-표시-설정)
    - [1-2. DEBUG Log 확인](#DEBUG-Log-확인)

# 초기 설정

~~~
./mvnw package
java -jar target/*.jar
~~~

# 실행 구조 파악

가장 간단하게 확인하는 방법은 `Log 확인` 입니다.
하지만 기본정의된 Log 표시값은 INFO, WARN .. 등등 기본적인것들 이기때문에
디버깅 Log 를 표시하도록 하여 에플리케이션이 어떻게 동작하는지 알 수 있도록 설정합니다.

## Log 표시 설정

> src/main/resources/application.properties

~~~
...
# Logging
logging.level.org.springframework=INFO
logging.level.org.springframework.web=DEBUG
...
~~~

DEBUG 설정을 주석 제거합니다. 

## DEBUG Log 확인

서버를 재실행 후 http://localhost:8080/owners/new 접속 후 Log 를 확인합니다.

~~~
DEBUG --- o.s.web.servlet.DispatcherServlet        : GET "/owners/new", parameters={}
DEBUG --- s.w.s.m.m.a.RequestMappingHandlerMapping : Mapped to public java.lang.String org.springframework.samples.petclinic.owner.OwnerController.initCreationForm(java.util.Map<java.lang.String, java.lang.Object>)
DEBUG --- o.s.w.s.v.ContentNegotiatingViewResolver : Selected 'text/html' given [text/html, application/xhtml+xml, image/webp, image/apng, application/signed-exchange;v=b3, application/xml;q=0.9, */*;q=0.8]
DEBUG --- o.s.web.servlet.DispatcherServlet        : Completed 200 OK
DEBUG --- o.s.web.servlet.DispatcherServlet        : GET "/resources/images/favicon.png", parameters={}
DEBUG --- o.s.w.s.handler.SimpleUrlHandlerMapping  : Mapped to ResourceHttpRequestHandler ["classpath:/META-INF/resources/", "classpath:/resources/", "classpath:/static/", "classpath:/public/", "/"]
DEBUG --- o.s.web.servlet.DispatcherServlet        : Completed 200 OK
~~~

여기서 확인해야 알것은 s.w.s.m.m.a.RequestMappingHandlerMapping 입니다. 
Mapped to public java.lang.String org.springframework.samples.petclinic.owner.OwnerController.initCreationForm(java.util.Map<java.lang.String, java.lang.Object>)

DispatcherServlet 이 OwnerController Class 의 initCreationForm 메서드를 실행해주고 owners/createOrUpdateOwnerForm 화면에 view 보여지게 됩니다.

~~~
...
private static final String VIEWS_OWNER_CREATE_OR_UPDATE_FORM = "owners/createOrUpdateOwnerForm";

@GetMapping("/owners/new")
public String initCreationForm(Map<String, Object> model) {
    Owner owner = new Owner();
    model.put("owner", owner);
    return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
}
...
~~~

이번엔 요청 POST 과정을 확인하겠습니다.

~~~
DEBUG --- o.s.web.servlet.DispatcherServlet        : POST "/owners/new", parameters={masked}
DEBUG --- s.w.s.m.m.a.RequestMappingHandlerMapping : Mapped to public java.lang.String org.springframework.samples.petclinic.owner.OwnerController.processCreationForm(org.springframework.samples.petclinic.owner.Owner,org.springframework.validation.BindingResult)
DEBUG --- o.s.web.servlet.view.RedirectView        : View name 'redirect:/owners/11', model {}
DEBUG --- o.s.web.servlet.DispatcherServlet        : Completed 302 FOUND
DEBUG --- o.s.web.servlet.DispatcherServlet        : GET "/owners/11", parameters={}
DEBUG --- s.w.s.m.m.a.RequestMappingHandlerMapping : Mapped to public org.springframework.web.servlet.ModelAndView org.springframework.samples.petclinic.owner.OwnerController.showOwner(int)
DEBUG --- o.s.w.s.v.ContentNegotiatingViewResolver : Selected 'text/html' given [text/html, application/xhtml+xml, image/webp, image/apng, application/signed-exchange;v=b3, application/xml;q=0.9, */*;q=0.8]
DEBUG --- o.s.web.servlet.DispatcherServlet        : Completed 200 OK
~~~

s.w.s.m.m.a.RequestMappingHandlerMapping : Mapped to public java.lang.String org.springframework.samples.petclinic.owner.OwnerController.processCreationForm(org.springframework.samples.petclinic.owner.Owner,org.springframework.validation.BindingResult)

이번엔 OwnerController.processCreationForm 메서드를 실행하는것을 확인했습니다.

~~~
...
@PostMapping("/owners/new")
public String processCreationForm(@Valid Owner owner, BindingResult result) {
    if (result.hasErrors()) {
        return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
    } else {
        this.owners.save(owner);
        return "redirect:/owners/" + owner.getId();
    }
}
...
~~~

owner 를 생성하여 저장해주고 생성한 owner의 Id 주소 위치로 이동하게 됩니다.
다음 owner의 Id 값을 받는 GET Mapping 으로 이동합니다.

~~~
...
@GetMapping("/owners/{ownerId}")
public ModelAndView showOwner(@PathVariable("ownerId") int ownerId) {
    ModelAndView mav = new ModelAndView("owners/ownerDetails");
    mav.addObject(this.owners.findById(ownerId));
    return mav;
}
...
~~~

좀더 자세하게 알고싶다면 Debug 를 통해서 확인하면 됩니다.

# 과제 

- 1. LastName이 아니라 firstName 으로 검색
- 2. 정확한 일치가 아닌 키워드가 포함되도 검색
- 3. Owns에 age 추가

## 1. LastName이 아니라 firstName 으로 검색

우선 데이터 정보를 하나 생성합니다.

![유저-생성](./images/data-insert.png)

이제 Last Name 이 아닌 Last name 이 검색 될 수 있도록 하겠습니다. <br/>
우선 아무 값이나 전송 후 Log 를 확인하여 어떠한 메서드를 통해서 동작하는지 위치를 파악합니다.<br/>

- 검색 결과 로그
~~~
DEBUG --- o.s.web.servlet.DispatcherServlet        : GET "/owners?lastName=First+Name", parameters={masked}
DEBUG --- s.w.s.m.m.a.RequestMappingHandlerMapping : Mapped to public java.lang.String org.springframework.samples.petclinic.owner.OwnerController.processFindForm(org.springframework.samples.petclinic.owner.Owner,org.springframework.validation.BindingResult,java.util.Map<java.lang.String, java.lang.Object>)
DEBUG --- o.s.w.s.v.ContentNegotiatingViewResolver : Selected 'text/html' given [text/html, application/xhtml+xml, image/webp, image/apng, application/signed-exchange;v=b3, application/xml;q=0.9, */*;q=0.8]
DEBUG --- o.s.web.servlet.DispatcherServlet        : Completed 200 OK
~~~

로그 확인 결과 Get 방식으로 OwnerController 클래스의 processFindForm 메서드를 통해서 실행되는 것을 확인했습니다.

> OwnerController

~~~
@GetMapping("/owners")
public String processFindForm(Owner owner, BindingResult result, Map<String, Object> model) {

    // allow parameterless GET request for /owners to return all records
    if (owner.getLastName() == null) {
        owner.setLastName(""); // empty string signifies broadest possible search
    }

    // find owners by last name
    Collection<Owner> results = this.owners.findByLastName(owner.getLastName());
    if (results.isEmpty()) {
        // no owners found
        result.rejectValue("lastName", "notFound", "not found");
        return "owners/findOwners";
    } else if (results.size() == 1) {
        // 1 owner found
        owner = results.iterator().next();
        return "redirect:/owners/" + owner.getId();
    } else {
        // multiple owners found
        model.put("selections", results);
        return "owners/ownersList";
    }
}
~~~

processFindForm 메소드에서 사용자가 저장한 데이터를 담은 `Owner owner` Entity 를 전달받습니다.
  
우선 `Owner owner` Entity 에 무슨 값이 담아져서 오는지 확인부터 해보겠습니다.

~~~
private static final Logger log = LoggerFactory.getLogger(OwnerController.class);
log.info("등장 - {}", owner);
~~~

slf4j 의 Logger 를 활용하여 검색창에 'First Name' 문자열을 검색 후 owner 값을 출력해 봅시다.

~~~
등장 - [Owner@11fa65d id = [null], new = true, lastName = 'First Name', firstName = [null], address = [null], city = [null], telephone = [null]]

> 정리하면
id = null
new = true
lastName = 'First Name'
firstName = null
address = null
city = null
telephone = null
~~~

lastName, new 값 이외에는 전부 null 값으로 나오는 것을 확인했습니다.
`Owner owner` Entity 의 lastName 을 가져와서 쿼리를 검색 후 찾는 결과물이 나올경우 사용자에게 보여주는 방법인거 같습니다.

~~~
Collection<Owner> results = this.owners.findByLastName(owner.getLastName());
~~~ 

this.owners 는 owner 의 Repository 저장소 이며  JPA 문법 findByLastName 활용하여 LastName 을 포함하는 
쿼리 리스트를 Collection 으로 가져옵니다.

결과값이 비어있는지 혹은 다른 조건이 있는지 체크 후 결과값을 담은 model 을 반환 합니다.
이번 과제의 목표는 firstName 으로 검색될 수 있게 하는것입니다.

FirstName 을 검색할 수 있도록 바꿔야하는 클래스 위치

GET 전송으로 보내는 input value 값  
> findOwners.html

~~~
+ <input class="form-control" th:field="*{firstName}" size="30" maxlength="80" /> 
<span class="help-inline">
    <div th:if="${#fields.hasAnyErrors()}">
        <p th:each="err : ${#fields.allErrors()}" th:text="${err}">Error</p>
    </div>
</span>
~~~

서버에 FirstName 값을 확인하고 결과를 반환하는 쿼리 저장소
> OwnerRepository.java

~~~
// FirstName 검색
@Query("SELECT DISTINCT owner FROM Owner owner left join fetch owner.pets WHERE owner.firstName LIKE :firstName%")
@Transactional(readOnly = true)
Collection<Owner> findByFirstName(@Param("firstName") String FirstName);
~~~

Last Name 을 설정하는 것이 아닌 GET FORM 으로 받은 First Name 값을 owner 값에 설정하도록 바꿔줬습니다.
> OwnerController.java

~~~
@GetMapping("/owners")
 public String processFindForm(Owner owner, BindingResult result, Map<String, Object> model) {

     + log.info("등장 - {}", owner);
     
     // allow parameterless GET request for /owners to return all records
     + if (owner.getFirstName() == null) {
     +     owner.setFirstName(""); // empty string signifies broadest possible search
     }

     // find owners by last name
     + Collection<Owner> results = this.owners.findByFirstName(owner.getFirstName());
     if (results.isEmpty()) {
         // no owners found
     +   result.rejectValue("FirstName", "notFound", "not found");
         return "owners/findOwners";
     } else if (results.size() == 1) {
         // 1 owner found
         owner = results.iterator().next();
         return "redirect:/owners/" + owner.getId();
     } else {
         // multiple owners found
         model.put("selections", results);
         return "owners/ownersList";
     }
 }
~~~

~~~
등장 - [Owner@1a535948 id = [null], new = true, lastName = [null], firstName = 'First Name', address = [null], city = [null], telephone = [null]]
~~~

정상적으로 firstName 값을 가져오며 검색되는 것을 확인할 수 있었습니다.