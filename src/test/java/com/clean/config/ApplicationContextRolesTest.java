package com.clean.config;

import com.clean.domain.model.energy.EnergyCapacity;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ApplicationContextRolesTest {

    private static final String YAML = """
            properties:
              isp:
                greeting: hello
                fuel-ml: 50
            beans:
              hardware:
                class: com.clean.domain.model.energy.EnergyCapacity
                factory-method: of
                args: [ "${isp.fuel-ml}", 20 ]
            """;

    @Test
    void applicationContextCombinesBeanFactoryAndPropertyResolver() {
        assertTrue(BeanFactory.class.isAssignableFrom(ApplicationContext.class));
        assertTrue(PropertyResolver.class.isAssignableFrom(ApplicationContext.class));
        assertFalse(BeanFactory.class.isAssignableFrom(PropertyResolver.class));
    }

    @Test
    void contextCanBeUsedThroughEachRoleOnItsOwn() {
        ApplicationContext context = YamlApplicationContext.fromYaml(YAML);
        PropertyResolver properties = context;
        BeanFactory beans = context;

        assertEquals("hello", properties.getRequiredProperty("isp.greeting"));
        assertNull(properties.getProperty("isp.missing"));
        assertThrows(BeansException.class, () -> properties.getRequiredProperty("isp.missing"));
        assertTrue(beans.containsBean("hardware"));
        assertEquals(EnergyCapacity.of(50, 20), beans.getBean("hardware", EnergyCapacity.class));
        assertEquals(EnergyCapacity.of(50, 20), beans.getBean(EnergyCapacity.class));
    }
}
