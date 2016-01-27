package com.jdy.ddj.utils;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.*;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.springframework.util.Assert.notNull;

/**
 * Created by tianding on 2015-07-16
 */
public class MapperScannerConfigurer implements BeanDefinitionRegistryPostProcessor, InitializingBean, ApplicationContextAware, BeanNameAware {

    private List<String> basePackage;

    private boolean addToConfig = true;

    private SqlSessionFactory sqlSessionFactory;

    private SqlSessionTemplate sqlSessionTemplate;

    private String sqlSessionTemplateBeanName;

    private String sqlSessionFactoryBeanName;

    private Class<? extends Annotation> annotationClass;

    private Class<?> markerInterface;

    private ApplicationContext applicationContext;

    private String beanName;

    private boolean processPropertyPlaceHolders;

    /**
     * This property lets you set the base package for your mapper interface files.
     * <p>
     * You can set more than one package by using a semicolon or comma as a separator.
     * <p>
     * Mappers will be searched for recursively starting in the specified package(s).
     *
     * @param basePackage base package name
     */
    public void setBasePackage(List<String> basePackage) {
        this.basePackage = basePackage;
    }

    /**
     * Same as {@code MapperFactoryBean#setAddToConfig(boolean)}
     *
     * @param addToConfig
     * @see org.mybatis.spring.mapper.MapperFactoryBean#setAddToConfig(boolean)
     */
    public void setAddToConfig(boolean addToConfig) {
        this.addToConfig = addToConfig;
    }

    /**
     * This property specifies the annotation that the scanner will search for.
     * <p>
     * The scanner will register all interfaces in the base package that also have the
     * specified annotation.
     * <p>
     * Note this can be combined with markerInterface.
     *
     * @param annotationClass annotation class
     */
    public void setAnnotationClass(Class<? extends Annotation> annotationClass) {
        this.annotationClass = annotationClass;
    }

    /**
     * This property specifies the parent that the scanner will search for.
     * <p>
     * The scanner will register all interfaces in the base package that also have the
     * specified interface class as a parent.
     * <p>
     * Note this can be combined with annotationClass.
     *
     * @param superClass parent class
     */
    public void setMarkerInterface(Class<?> superClass) {
        this.markerInterface = superClass;
    }

    /**
     * Specifies which {@code SqlSessionTemplate} to use in the case that there is
     * more than one in the spring context. Usually this is only needed when you
     * have more than one datasource.
     * <p>
     * Use {@link #setSqlSessionTemplateBeanName(String)} instead
     *
     * @param sqlSessionTemplate
     */
    @Deprecated
    public void setSqlSessionTemplate(SqlSessionTemplate sqlSessionTemplate) {
        this.sqlSessionTemplate = sqlSessionTemplate;
    }

    /**
     * Specifies which {@code SqlSessionTemplate} to use in the case that there is
     * more than one in the spring context. Usually this is only needed when you
     * have more than one datasource.
     * <p>
     * Note bean names are used, not bean references. This is because the scanner
     * loads early during the start process and it is too early to build mybatis
     * object instances.
     *
     * @since 1.1.0
     *
     * @param sqlSessionTemplateName Bean name of the {@code SqlSessionTemplate}
     */
    public void setSqlSessionTemplateBeanName(String sqlSessionTemplateName) {
        this.sqlSessionTemplateBeanName = sqlSessionTemplateName;
    }

    /**
     * Specifies which {@code SqlSessionFactory} to use in the case that there is
     * more than one in the spring context. Usually this is only needed when you
     * have more than one datasource.
     * <p>
     * Use {@link #setSqlSessionFactoryBeanName(String)} instead.
     *
     * @param sqlSessionFactory
     */
    @Deprecated
    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
    }

    /**
     * Specifies which {@code SqlSessionFactory} to use in the case that there is
     * more than one in the spring context. Usually this is only needed when you
     * have more than one datasource.
     * <p>
     * Note bean names are used, not bean references. This is because the scanner
     * loads early during the start process and it is too early to build mybatis
     * object instances.
     *
     * @since 1.1.0
     *
     * @param sqlSessionFactoryName Bean name of the {@code SqlSessionFactory}
     */
    public void setSqlSessionFactoryBeanName(String sqlSessionFactoryName) {
        this.sqlSessionFactoryBeanName = sqlSessionFactoryName;
    }

    /**
     *
     * @since 1.1.1
     *
     * @param processPropertyPlaceHolders
     */
    public void setProcessPropertyPlaceHolders(boolean processPropertyPlaceHolders) {
        this.processPropertyPlaceHolders = processPropertyPlaceHolders;
    }

    /**
     * {@inheritDoc}
     */
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * {@inheritDoc}
     */
    public void setBeanName(String name) {
        this.beanName = name;
    }

    /**
     * {@inheritDoc}
     */
    public void afterPropertiesSet() throws Exception {
        notNull(this.basePackage, "Property 'basePackage' is required");
    }

    /**
     * {@inheritDoc}
     */
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
    }

    /**
     * {@inheritDoc}
     */
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
        if (this.processPropertyPlaceHolders) {
            processPropertyPlaceHolders();
        }

        Scanner scanner = new Scanner(beanDefinitionRegistry);
        scanner.setResourceLoader(this.applicationContext);
        for(int i =0 ;i < basePackage.size();i++){
            scanner.scan(StringUtils.tokenizeToStringArray(basePackage.get(i), ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS));
        }

    }

    /*
     * BeanDefinitionRegistries are called early in application startup, before
     * BeanFactoryPostProcessors. This means that PropertyResourceConfigurers will not have been
     * loaded and any property substitution of this class' properties will fail. To avoid this, find
     * any PropertyResourceConfigurers defined in the context and run them on this class' bean
     * definition. Then update the values.
     */
    private void processPropertyPlaceHolders() {
        Map<String, PropertyResourceConfigurer> prcs = applicationContext.getBeansOfType(PropertyResourceConfigurer.class);

        if (!prcs.isEmpty() && applicationContext instanceof GenericApplicationContext) {
            BeanDefinition mapperScannerBean = ((GenericApplicationContext) applicationContext)
                    .getBeanFactory().getBeanDefinition(beanName);

            // PropertyResourceConfigurer does not expose any methods to explicitly perform
            // property placeholder substitution. Instead, create a BeanFactory that just
            // contains this mapper scanner and post process the factory.
            DefaultListableBeanFactory factory = new DefaultListableBeanFactory();
            factory.registerBeanDefinition(beanName, mapperScannerBean);

            for (PropertyResourceConfigurer prc : prcs.values()) {
                prc.postProcessBeanFactory(factory);
            }

            PropertyValues values = mapperScannerBean.getPropertyValues();

//            this.basePackage = updatePropertyValue("basePackage", values);
//
            this.sqlSessionFactoryBeanName = updatePropertyValue("sqlSessionFactoryBeanName", values);
            this.sqlSessionTemplateBeanName = updatePropertyValue("sqlSessionTemplateBeanName", values);
        }
    }

    private String updatePropertyValue(String propertyName, PropertyValues values) {
        PropertyValue property = values.getPropertyValue(propertyName);

        if (property == null) {
            return null;
        }

        Object value = property.getValue();

        if (value == null) {
            return null;
        } else if (value instanceof String) {
            return value.toString();
        } else if (value instanceof TypedStringValue) {
            return ((TypedStringValue) value).getValue();
        } else {
            return null;
        }
    }

    private final class Scanner extends ClassPathBeanDefinitionScanner {

        public Scanner(BeanDefinitionRegistry registry) {
            super(registry);
        }

        /**
         * Configures parent scanner to search for the right interfaces. It can search for all
         * interfaces or just for those that extends a markerInterface or/and those annotated with
         * the annotationClass
         */
        @Override
        protected void registerDefaultFilters() {
            boolean acceptAllInterfaces = true;

            // if specified, use the given annotation and / or marker interface
            if (MapperScannerConfigurer.this.annotationClass != null) {
                addIncludeFilter(new AnnotationTypeFilter(MapperScannerConfigurer.this.annotationClass));
                acceptAllInterfaces = false;
            }

            // override AssignableTypeFilter to ignore matches on the actual marker interface
            if (MapperScannerConfigurer.this.markerInterface != null) {
                addIncludeFilter(new AssignableTypeFilter(MapperScannerConfigurer.this.markerInterface) {
                    @Override
                    protected boolean matchClassName(String className) {
                        return false;
                    }
                });
                acceptAllInterfaces = false;
            }

            if (acceptAllInterfaces) {
                // default include filter that accepts all classes
                addIncludeFilter(new TypeFilter() {
                    public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException {
                        return true;
                    }
                });
            }

            // exclude package-info.java
            addExcludeFilter(new TypeFilter() {
                public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException {
                    String className = metadataReader.getClassMetadata().getClassName();
                    return className.endsWith("package-info");
                }
            });
        }

        /**
         * Calls the parent search that will search and register all the candidates. Then the
         * registered objects are post processed to set them as MapperFactoryBeans
         */
        @Override
        protected Set<BeanDefinitionHolder> doScan(String... basePackages) {
            Set<BeanDefinitionHolder> beanDefinitions = super.doScan(basePackages);

            if (beanDefinitions.isEmpty()) {
                logger.warn("No MyBatis mapper was found in '" + MapperScannerConfigurer.this.basePackage
                        + "' package. Please check your configuration.");
            } else {
                for (BeanDefinitionHolder holder : beanDefinitions) {
                    GenericBeanDefinition definition = (GenericBeanDefinition) holder.getBeanDefinition();

                    if (logger.isDebugEnabled()) {
                        logger.debug("Creating MapperFactoryBean with name '" + holder.getBeanName()
                                + "' and '" + definition.getBeanClassName() + "' mapperInterface");
                    }

                    // the mapper interface is the original class of the bean
                    // but, the actual class of the bean is MapperFactoryBean
                    definition.getPropertyValues().add("mapperInterface", definition.getBeanClassName());
                    definition.setBeanClass(MapperFactoryBean.class);

                    definition.getPropertyValues().add("addToConfig", MapperScannerConfigurer.this.addToConfig);

                    boolean explicitFactoryUsed = false;
                    if (StringUtils.hasLength(MapperScannerConfigurer.this.sqlSessionFactoryBeanName)) {
                        definition.getPropertyValues().add("sqlSessionFactory", new RuntimeBeanReference(MapperScannerConfigurer.this.sqlSessionFactoryBeanName));
                        explicitFactoryUsed = true;
                    } else if (MapperScannerConfigurer.this.sqlSessionFactory != null) {
                        definition.getPropertyValues().add("sqlSessionFactory", MapperScannerConfigurer.this.sqlSessionFactory);
                        explicitFactoryUsed = true;
                    }

                    if (StringUtils.hasLength(MapperScannerConfigurer.this.sqlSessionTemplateBeanName)) {
                        if (explicitFactoryUsed) {
                            logger.warn("Cannot use both: sqlSessionTemplate and sqlSessionFactory together. sqlSessionFactory is ignored.");
                        }
                        definition.getPropertyValues().add("sqlSessionTemplate", new RuntimeBeanReference(MapperScannerConfigurer.this.sqlSessionTemplateBeanName));
                        definition.getPropertyValues().add("sqlSessionFactory", null);
                    } else if (MapperScannerConfigurer.this.sqlSessionTemplate != null) {
                        if (explicitFactoryUsed) {
                            logger.warn("Cannot use both: sqlSessionTemplate and sqlSessionFactory together. sqlSessionFactory is ignored.");
                        }
                        definition.getPropertyValues().add("sqlSessionTemplate", MapperScannerConfigurer.this.sqlSessionTemplate);
                        definition.getPropertyValues().add("sqlSessionFactory", null);
                    }
                }
            }

            return beanDefinitions;
        }

        @Override
        protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
            return (beanDefinition.getMetadata().isInterface() && beanDefinition.getMetadata().isIndependent());
        }

        @Override
        protected boolean checkCandidate(String beanName, BeanDefinition beanDefinition) throws IllegalStateException {
            if (super.checkCandidate(beanName, beanDefinition)) {
                return true;
            } else {
                logger.warn("Skipping MapperFactoryBean with name '" + beanName
                        + "' and '" + beanDefinition.getBeanClassName() + "' mapperInterface"
                        + ". Bean already defined with the same name!");
                return false;
            }
        }
    }

}
