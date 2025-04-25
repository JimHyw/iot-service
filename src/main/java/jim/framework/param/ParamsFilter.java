package jim.framework.param;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class ParamsFilter implements Filter {

    @Override
    public void doFilter(ServletRequest arg0, ServletResponse arg1,
                         FilterChain arg2) throws IOException, ServletException {
        ParameterRequestWrapper parmsRequest = new ParameterRequestWrapper(
                (HttpServletRequest) arg0);
        arg2.doFilter(parmsRequest, arg1);
    }

    @Override
    public void init(FilterConfig arg0) throws ServletException {

    }

    @Override
    public void destroy() {

    }
}
