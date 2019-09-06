--------------------
# Spring 입문 노트
--------------------

# 목치

- [1. 실행 구조 파악](#실행-구조-파악)
    - [1-1. Log 표시 설정](#Log-표시-설정)
    - [1-2. DEBUG Log 확인](#DEBUG-Log-확인)

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
DEBUG 7988 --- [nio-8080-exec-7] o.s.web.servlet.DispatcherServlet        : GET "/owners/new", parameters={}
DEBUG 7988 --- [nio-8080-exec-7] s.w.s.m.m.a.RequestMappingHandlerMapping : Mapped to public java.lang.String org.springframework.samples.petclinic.owner.OwnerController.initCreationForm(java.util.Map<java.lang.String, java.lang.Object>)
DEBUG 7988 --- [nio-8080-exec-7] o.s.w.s.v.ContentNegotiatingViewResolver : Selected 'text/html' given [text/html, application/xhtml+xml, image/webp, image/apng, application/signed-exchange;v=b3, application/xml;q=0.9, */*;q=0.8]
DEBUG 7988 --- [nio-8080-exec-7] o.s.web.servlet.DispatcherServlet        : Completed 200 OK
DEBUG 7988 --- [nio-8080-exec-9] o.s.web.servlet.DispatcherServlet        : GET "/resources/images/favicon.png", parameters={}
DEBUG 7988 --- [nio-8080-exec-9] o.s.w.s.handler.SimpleUrlHandlerMapping  : Mapped to ResourceHttpRequestHandler ["classpath:/META-INF/resources/", "classpath:/resources/", "classpath:/static/", "classpath:/public/", "/"]
DEBUG 7988 --- [nio-8080-exec-9] o.s.web.servlet.DispatcherServlet        : Completed 200 OK
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
DEBUG 7988 --- [nio-8080-exec-3] o.s.web.servlet.DispatcherServlet        : POST "/owners/new", parameters={masked}
DEBUG 7988 --- [nio-8080-exec-3] s.w.s.m.m.a.RequestMappingHandlerMapping : Mapped to public java.lang.String org.springframework.samples.petclinic.owner.OwnerController.processCreationForm(org.springframework.samples.petclinic.owner.Owner,org.springframework.validation.BindingResult)
DEBUG 7988 --- [nio-8080-exec-3] o.s.web.servlet.view.RedirectView        : View name 'redirect:/owners/11', model {}
DEBUG 7988 --- [nio-8080-exec-3] o.s.web.servlet.DispatcherServlet        : Completed 302 FOUND
DEBUG 7988 --- [nio-8080-exec-2] o.s.web.servlet.DispatcherServlet        : GET "/owners/11", parameters={}
DEBUG 7988 --- [nio-8080-exec-2] s.w.s.m.m.a.RequestMappingHandlerMapping : Mapped to public org.springframework.web.servlet.ModelAndView org.springframework.samples.petclinic.owner.OwnerController.showOwner(int)
DEBUG 7988 --- [nio-8080-exec-2] o.s.w.s.v.ContentNegotiatingViewResolver : Selected 'text/html' given [text/html, application/xhtml+xml, image/webp, image/apng, application/signed-exchange;v=b3, application/xml;q=0.9, */*;q=0.8]
DEBUG 7988 --- [nio-8080-exec-2] o.s.web.servlet.DispatcherServlet        : Completed 200 OK
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

- LastName이 아니라 firstName 으로 검색
- 정확한 일치가 아닌 키워드가 포함되도 검색
- Owns에 age 추가