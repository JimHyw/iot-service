package jim.framework.jwt;

import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.SubjectContext;
import org.apache.shiro.web.mgt.DefaultWebSubjectFactory;

/**
 * JWTSubjectFactory,自定义SubjectFactory,实现禁用session
 */
public class JWTSubjectFactory extends DefaultWebSubjectFactory { 

    public Subject createSubject(SubjectContext context) { 
        context.setSessionCreationEnabled(false);
        return super.createSubject(context); 
    }
}
