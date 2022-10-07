package com.osm.gnl.ippms.ogsg.web.ui.security;

import com.osm.gnl.ippms.ogsg.auth.services.UserDetailsServiceImpl;
import com.osm.gnl.ippms.ogsg.web.ui.filter.MenuFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.support.ErrorPageFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.servlet.DispatcherType;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private UserDetailsServiceImpl userDetailsService;

	@Autowired
	private AjaxAwareAuthenticationSuccessHandler authenticationSuccessHandler;

	@Autowired
	private AjaxAuthenticationFailureHandler authenticationFailureHandler;

	@Autowired
	private Http401EntryPoint unauthorizedHandler;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public ErrorPageFilter errorPageFilter() {
		return new ErrorPageFilter();
	}
	@Bean(name="errorPageFilterGNL")
	public FilterRegistrationBean disableSpringBootErrorFilter(ErrorPageFilter filter) {
		FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
		filterRegistrationBean.setFilter(filter);
		filterRegistrationBean.setEnabled(false);
		return filterRegistrationBean;
	}
	@Bean
	public FilterRegistrationBean<MenuFilter> menuFilter() {
		FilterRegistrationBean<MenuFilter> filterRegBean = new FilterRegistrationBean<>();
		filterRegBean.setFilter(new MenuFilter());
		filterRegBean.addUrlPatterns("*.do");
		filterRegBean.setDispatcherTypes(DispatcherType.REQUEST,DispatcherType.FORWARD);
		filterRegBean.setOrder(Ordered.LOWEST_PRECEDENCE -1);
		return filterRegBean;
	}
	@Override
	@Bean
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return this.authenticationManager();
	}

	@Autowired
	public void globalUserDetails(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/resources/**");

	}

	private static final String[] FRONT_END_WHITELIST = {
			"/assets/**/*",
			"/static/**/*",
			"/styles/**",
			"/scripts/**",
			"/css/**",
			"/images/**",
			"/img/**",
			"/dataTables/**",
			"/font-awesome/**",
			"/fonts/**",
			"*.css",
			"*.pdf",
			"*.png"
	};

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		authenticationFailureHandler.setDefaultFailureUrl("/securedLoginForm?login_error=1");
		authenticationSuccessHandler.setDefaultTargetUrl("/loginForm.gnl");
		http.requiresChannel().anyRequest().requiresSecure();
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED).invalidSessionUrl("/securedLoginForm");
		http.sessionManagement().sessionFixation().newSession();
		http.authorizeRequests()
				.antMatchers(FRONT_END_WHITELIST).permitAll()
				.antMatchers( "/login", "/securedLogoutForm", "/securedLoginForm","/forgetPassword.gnl").permitAll()
				.anyRequest().authenticated().and().formLogin().loginProcessingUrl("/login")
				.loginPage("/securedLoginForm").successHandler(authenticationSuccessHandler)
				.failureHandler(authenticationFailureHandler).permitAll().and()
				.logout().logoutUrl("/signOut.do").logoutSuccessUrl("/securedLogoutForm.jsp").permitAll().deleteCookies("JSESSIONID").invalidateHttpSession(true);
		http.exceptionHandling().accessDeniedPage("/securedLoginForm?login_error=1");

	}

}
