package br.com.cassiopaixao.account.payment.api.cors;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;

import br.com.cassiopaixao.account.payment.api.config.property.AccountPaymentApiApplication;

public class CorsFilter implements Filter {
	
	@Autowired
	private AccountPaymentApiApplication accountPaymentApiApplication;
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		
		response.setHeader("Acess-Control-Allow-Origin", accountPaymentApiApplication.getOriginPermitida());
		response.setHeader("Acess-Control-Allow-Credentials", "true");
		
		if("OPTION".equals(request.getMethod()) && accountPaymentApiApplication.getOriginPermitida().equals(request.getHeader("Origin"))) {
			response.setHeader("Acess-Control-Allow-Methods", "POST, GET, DELETE, PUT,OPTIONS");
			response.setHeader("Acess-Control-Allow-Headers", "Authorization, Content-Type, Accept");
			response.setHeader("Acess-Control-Allow-Max-Age", "3600");
			response.setStatus(HttpServletResponse.SC_OK);
		}else {
			chain.doFilter(request, response);
		}
		
	}

	@Override
	public void destroy() {
	}

}
