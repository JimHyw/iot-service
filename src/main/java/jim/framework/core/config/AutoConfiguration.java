package jim.framework.core.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jim.framework.core.annotation.Access;
import jim.framework.core.aspect.AccessAspect;

/**
 * Springboot自动配置类
 */
@Configuration                                                                                                                              
@ConditionalOnClass(Access.class)
public class AutoConfiguration {
	
	/**
	 * 配置AccessAspect
	 * @return AccessAspect实体
	 */
	@Bean
    @ConditionalOnMissingBean
	AccessAspect accessAspect(){
		return new AccessAspect();
	}
}
