package com.maxplus1.demo.config.datasource;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import org.apache.ibatis.session.AutoMappingBehavior;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

@Configuration
@MapperScan(basePackages = {"com.maxplus1.demo.dao.test2db"}, sqlSessionFactoryRef = "test2dbSqlSessionFactory")
public class Test2dbConfig {

    private final static Logger log = LoggerFactory.getLogger(Test2dbConfig.class);

    @Bean(name = "test2db")
    @ConfigurationProperties(prefix = "spring.datasource.druid.test2db")
    public DataSource dataSource() {
        return DruidDataSourceBuilder.create().build();
    }

    @Bean(name = "test2dbTransactionManager")
    public DataSourceTransactionManager transactionManager(@Qualifier("test2db") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean(name = "test2dbSqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory(@Qualifier("test2db") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(dataSource);
        // 多个 逗号, 分隔
        factoryBean.setTypeAliasesPackage("com.maxplus1.demo.entity");
        factoryBean.setMapperLocations(
                new PathMatchingResourcePatternResolver().getResources("classpath:mapper/test2db/*.xml"));
        SqlSessionFactory sqlSessionFactory = factoryBean.getObject();
        org.apache.ibatis.session.Configuration configuration = sqlSessionFactory.getConfiguration();
        configuration.setCacheEnabled(false);
        configuration.setDefaultExecutorType(ExecutorType.REUSE);
        configuration.setLazyLoadingEnabled(false);
        configuration.setAggressiveLazyLoading(true);
        configuration.setUseColumnLabel(true);
        configuration.setUseGeneratedKeys(true);
        configuration.setAutoMappingBehavior(AutoMappingBehavior.PARTIAL);
        configuration.setJdbcTypeForNull(JdbcType.NULL);
        configuration.setDefaultStatementTimeout(25000);
        return factoryBean.getObject();
    }
}
