
you have to logout with cookie
I can logout with both GET and POST
PKCS12 because it's language-neutral and JKS is only for JAVA

why for anonymous user I get FORBIDDEN - 403
2023-07-27T19:17:10.659+03:00 DEBUG 5654 --- [    Test worker] w.c.HttpSessionSecurityContextRepository :
Retrieved SecurityContextImpl [Authentication=UsernamePasswordAuthenticationToken 
[Principal=org.springframework.security.core.userdetails.User [Username=noRoot, Password=[PROTECTED], Enabled=true,
AccountNonExpired=true, credentialsNonExpired=true, AccountNonLocked=true, Granted Authorities=[ROLE_USER]], 
Credentials=[PROTECTED], Authenticated=true, Details=null, Granted Authorities=[ROLE_USER]]]

2023-07-27T19:17:10.667+03:00 DEBUG 5654 --- [    Test worker] horizationManagerBeforeMethodInterceptor : 
Failed to authorize ReflectiveMethodInvocation: public java.time.ZonedDateTime com.friday.mentoring.service.ClockService.getNowInUtc();
target is of class [com.friday.mentoring.service.ClockService] with authorization manager 
org.springframework.security.config.annotation.method.configuration.DeferringObservationAuthorizationManager@67e11bda 
and decision ExpressionAuthorizationDecision [granted=false, expressionAttribute=authentication.name == 'root' and hasRole('ADMIN')]

and for incorrect user I get NOT_FOUND - 404

2023-07-27T19:17:10.809+03:00 DEBUG 5654 --- [    Test worker] o.s.s.w.a.AnonymousAuthenticationFilter  :
Set SecurityContextHolder to anonymous SecurityContext
2023-07-27T19:17:10.810+03:00 DEBUG 5654 --- [    Test worker] horizationManagerBeforeMethodInterceptor : 
Failed to authorize ReflectiveMethodInvocation: public java.time.ZonedDateTime com.friday.mentoring.service.ClockService.getNowInUtc();
target is of class [com.friday.mentoring.service.ClockService] with authorization manager
org.springframework.security.config.annotation.method.configuration.DeferringObservationAuthorizationManager@67e11bda 
and decision ExpressionAuthorizationDecision [granted=false, expressionAttribute=authentication.name == 'root' and hasRole('ADMIN')]

Forbidden 403 access denied for incorrect user can be catched in 
@ExceptionHandler(value = AccessDeniedException.class)
protected ProblemDetail handleAccessDenied(AccessDeniedException ex, WebRequest request) {
ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.ALREADY_REPORTED);
pd.setDetail(ex.getMessage());
pd.setType(URI.create(request.getContextPath()));
return pd;
}